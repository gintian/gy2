package com.hjsj.hrms.businessobject.kq.interfaces;

import com.hjsj.hrms.businessobject.general.template.TemplateTableParamBo;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Instance;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.app_check_in.GetValiateEndDate;
import com.hjsj.hrms.businessobject.kq.app_check_in.ValidateAppOper;
import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.businessobject.kq.kqself.CancelHols;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.pigeonhole.UpdateQ33;
import com.hjsj.hrms.module.kq.application.KqOverTimeForLeaveBo;
import com.hjsj.hrms.module.kq.interfaces.KqAppCaculator;
import com.hjsj.hrms.module.kq.util.KqItem;
import com.hjsj.hrms.module.kq.util.KqVer;
import com.hjsj.hrms.utils.OperateDate;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 考勤业务申请接口类，供人事异动等模块调用
 * 
 * @author zxj
 * @version 1.0
 */
public class KqAppInterface {
    Category cat = Category.getInstance(getClass());
    
    public static final int LEAVE_IS_NOT        = -2; // 不是请假
    public static final int LEAVE_NOT_EXIST     = -1; // 无此请假类型
    public static final int LEAVE_IS_HOLIDAY    = 0; // 年假等假期
    public static final int LEAVE_USED_OVERTIME = 1; // 倒休假(加班倒休)
    public static final int LEAVE_NORMAL        = 2; // 普通请假

    public static final int IS_LEAVE            = 2; // 是请假申请
    public static final int IS_OVERTIME         = 1; // 是加班申请
    public static final int IS_OFFICELEAVE      = 3; // 是公出申请
    public static final int IS_LEAVEBACK        = 4; // 是销假申请

    private UserView        userView;
    private Connection      conn;
    private ContentDAO      dao;
    private KqDBHelper      dbHelper;
    
    private KqVer kqVer;
    
    // 申请单类型(=1加班/=2请假/=3公出/=4销假)
    private int appType = 0;

    private KqAppInterface() {

    }

    public KqAppInterface(Connection conn, UserView userView) {
        this.userView = userView;
        this.conn = conn;
        this.dao = new ContentDAO(conn);
        dbHelper = new KqDBHelper(conn);
        
        this.kqVer = new KqVer();
    }

    /*
              * 得到申请有效时长（时长按考勤规则定义的计量单位）
     * 
     * @param appInfo <lazyDynaBean>
     * appInfo中需包含type,nbase,a0100,starttime,endtime五项内容
     */
    public double getAppFactDays(LazyDynaBean appInfo) {
        double days = -1;

        try {
            String appTypeId = (String) appInfo.get("type");
            String nbase = (String) appInfo.get("nbase");
            String a0100 = (String) appInfo.get("a0100");
            
            //28852 linbz 校验开始结束时间是否为null是否为Data类型 //liuyz bug31859
            if(!(appInfo.get("starttime") instanceof Date) || !(appInfo.get("endtime") instanceof Date)){
            	return days;
            }
            
            Date starttime = (Date) appInfo.get("starttime");
            Date endtime = (Date) appInfo.get("endtime");

            KqVer kqVer = new KqVer();
            
            // 高校医院班考勤计算时长
            if (kqVer.getVersion() == KqConstant.Version.UNIVERSITY_HOSPITAL) {
                KqAppCaculator kqAppCalculator = new KqAppCaculator(conn);
                return kqAppCalculator.calcAppTimeLen(appInfo);
            }
            
            // 标准班考勤计算时长
            // 检查是否为需要考勤人员
            if (!needKq(nbase, a0100)) {
                return days;
            }

            try {
                AnnualApply annualApply = new AnnualApply(this.userView, this.conn);
                HashMap kqItemHash = annualApply.count_Leave(appTypeId);
                String b0110 = getB0110ByEmpInfo(nbase, a0100);

                // 请假和公出时长，算法一样，在班次定义时间内的是有效时长
                if (appTypeId.startsWith("0") || appTypeId.startsWith("3")) {
                    float[] holiday_rules = null;
                    if (LEAVE_IS_HOLIDAY == getLeaveType(appTypeId, nbase, a0100)) {
                        holiday_rules = annualApply.getHoliday_minus_rule();
                    }
                    
                    days = annualApply.calcLeaveAppTimeLen(nbase, a0100, b0110, starttime, endtime, 
                            kqItemHash, holiday_rules, Integer.MAX_VALUE);
                } else {
                    // 加班，在班次时间之外的才是有效时长
                    days = annualApply.calcOverAppTimeLen(nbase, a0100, starttime, endtime, 
                            kqItemHash, Integer.MAX_VALUE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return days;
    }

    /*
     * 得到某假期可休天数
     * 
     * @param appInfo <lazyDynaBean>
     * appInfo中需包含type,nbase,a0100,starttime,endtime五项内容
     */
    public double getAppCanUseDays(LazyDynaBean appInfo) throws Exception {
        double days = -1;

        String leaveId = (String) appInfo.get("type");
        if (leaveId == null || "".equals(leaveId)) {
            return days;
        }

        // 老考勤需要检查考勤方式
        if (kqVer.getVersion() == KqConstant.Version.STANDARD && !needKq((String) appInfo.get("nbase"), (String) appInfo.get("a0100"))) {
            return days;
        }

        int leaveType = getLeaveType(leaveId, (String) appInfo.get("nbase"), (String) appInfo.get("a0100"));
        try {
            if (leaveType == LEAVE_IS_HOLIDAY) {
                days = getUsableHolidayDays(appInfo);
            } else if (leaveType == LEAVE_USED_OVERTIME) {
                days = getOverTimeToRestDays(appInfo);
            }
        } catch (Exception e) {
            throw e;
        }
        return days;
    }

    /*
     * 得到某假期已休天数
     * 
     * @param appInfo <lazyDynaBean>
     * appInfo中需包含type,nbase,a0100,starttime,endtime五项内容，
     * 如果是年假等假期，还可以包含yx_falg(已休天数取数规则）
     */
    public double getAppUsedDays(LazyDynaBean appInfo) {
        double days = -1;

        String leaveId = (String) appInfo.get("type");
        if (leaveId == null || "".equals(leaveId)) {
            return days;
        }

        if (kqVer.getVersion() == KqConstant.Version.STANDARD && !needKq((String) appInfo.get("nbase"), (String) appInfo.get("a0100"))) {
            return days;
        }

        int leaveType = getLeaveType(leaveId, (String) appInfo.get("nbase"), (String) appInfo.get("a0100"));
        if (leaveType == LEAVE_IS_HOLIDAY) {
            days = getUsedHolidayDays(appInfo);
        } else if (leaveType == LEAVE_USED_OVERTIME) {
            days = getUsedOverTimeToRestDays(appInfo);
        }

        return days;
    }

    /*
     * 将申请记录同步到考勤业务表中（报批、批准、驳回）
     * 
     * @param templateId String 模板号
     * 
     * @param appInfo LazyDynaBean 按照对应关系封装的申请记录信息
     * 
     * @param operState String 申请记录操作状态（02：报批，03：批准 07：驳回）
     */
    public boolean syncAppInfoToKqTab(String templateId, LazyDynaBean appInfo, String operState) throws GeneralException {
        boolean isOK = false;

        try {
            KqVer kqVer = new KqVer();    
            if (kqVer.getVersion() == KqConstant.Version.UNIVERSITY_HOSPITAL) {
                return true;
            }
            
            if (!needKq((String) appInfo.get("nbase"), (String) appInfo.get("a0100"))) {
                logAppInfo(templateId, "", appInfo, operState, "申请人不需要考勤：");
                return isOK;
            }

            TemplateTableParamBo templeteBo = new TemplateTableParamBo(Integer.parseInt(templateId), this.conn);

            appType = getAppTypeByTemplate(templeteBo);
            if (-1 == appType) {
                return isOK;
            }

            String appTab = "";

            if (IS_LEAVE == appType) {
                appTab = "Q15";
            } else if (IS_OVERTIME == appType) {
                appTab = "Q11";
            } else if (IS_OFFICELEAVE == appType) {
                appTab = "Q13";
            } else if (IS_LEAVEBACK == appType) {
            	//销假、销公出、销加班按类型获取对应的表
            	String itemId = (String) appInfo.get("QXJ03_O");
            	if(StringUtils.isEmpty(itemId)) {
                    itemId = (String) appInfo.get("qxj03_o");
                }
            	
            	KqItem kqItem = new KqItem(this.conn);
            	HashMap<String, HashMap<String, String>> kqItemsMap = kqItem.getKqItem();
                HashMap<String, String> kqItemMap = kqItemsMap.get(itemId);
            	appTab = kqItemMap.get("sdata_src");
            	if(StringUtils.isEmpty(appTab)) {
            		logAppInfo(templateId, "", appInfo, operState, "该考勤项目没有设置对应的考勤表！");
                    return isOK;
            	}
            } else {
                return isOK;
            }
            
            //验证单号正确性
            int checkFlag = checkAppIdError(appInfo, appTab, appType);
            //已处理过的单据直接返回，不再重复处理。避免某些特殊情况下，出现数据不一致，导致重复审批不了的情况。
            if (1 == checkFlag) {
                return true;
            }
            
            String leaveId = (String) appInfo.get("QXJ01_O");
            if(StringUtils.isEmpty(leaveId)) {
                leaveId = (String) appInfo.get("qxj01_o");
            }
            
            if (IS_LEAVEBACK == appType && StringUtils.isEmpty(leaveId)) {
                appInfo.set("QXJ01_O", getLeaveId(appInfo, appTab));
            }
            	
            //删除申请单
            if ("10".equals(operState)) {
                logAppInfo(templateId, appTab, appInfo, operState, "删除申请单：");
                isOK = delAppByAppInfo(appInfo, appTab);
            } else {
                RecordVo vo = getAppVo(templeteBo, appInfo, appTab, operState, appType);
                if (vo == null) {
                    logAppInfo(templateId, appTab, appInfo, operState, "申请单无效：");
                    return isOK;
                }

                String keyFld = appTab.toLowerCase() + "01";
                //销假申请单独处理
                if(IS_LEAVEBACK == appType) {
                	String sp = "";
                	if ("07".equals(operState)) {
                		vo.setString(appTab.toLowerCase()+"z5", "07");
                		sp = "";
                	} else if ("03".equals(operState)) {
                		vo.setString(appTab.toLowerCase()+"z5", "03");
                		sp = "5";
                	} else {
                		vo.setString(appTab.toLowerCase()+"z5", "02");
                		sp = "";
                	}
                	String sels = vo.getString(appTab.toLowerCase()+"03");
                	CancelHols cancelHols = new CancelHols(this.userView, this.conn);
                	cancelHols.cancelTimeApp(vo, sels, vo.getDate(appTab.toLowerCase()+"z1"), vo.getDate(appTab.toLowerCase()+"z3"), true, sp, "app", appTab.toLowerCase());
                	
                } else {
                	if (!isExistRecord(appTab, keyFld, vo.getString(keyFld))) {
                		logAppInfo(templateId, appTab, appInfo, operState, "新增申请单：");
                		dao.addValueObject(vo);
                	} else {
                		logAppInfo(templateId, appTab, appInfo, operState, "更新申请单：");
                		dao.updateValueObject(vo);
                	}
                }
                    
                isOK = true;
            }
        } catch (GeneralException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isOK;
    }

    /*
     * 将申请记录同步到考勤业务表中（报批、批准、驳回）
     * 
     * @param templateId String 模板号
     * 
     * @param list ArrayList<LazyDynaBean> 按照对应关系封装的申请记录信息集合
     * 
     * @param operState String 申请记录操作状态（02：报批，03：批准 07：驳回）
     */
    public ArrayList<String> syncAppInfoToKqTab(String templateId, ArrayList<LazyDynaBean> list, String operState) throws GeneralException {
    	boolean isOK = false;
    	// 提示信息--- 姓名 + 假期类型 + 时间 + 错误信息
    	ArrayList<String> msglist = new ArrayList<String>();
    	
    	KqVer kqVer = new KqVer();    
        
        ArrayList<ArrayList> alllist = new ArrayList<ArrayList>();
        
        // 首先校验该模板是否为考勤模板
        TemplateTableParamBo templeteBo = new TemplateTableParamBo(Integer.parseInt(templateId), this.conn);
		appType = getAppTypeByTemplate(templeteBo);
		if (-1 == appType) {
			msglist.add("该申请单不属于考勤单据！");
			return msglist;
		}
        
        try {
            	// 循环list校验每个表单
            	for(int i=0;i<list.size();i++) {
            		ArrayList volist = new ArrayList();
            		LazyDynaBean appInfo = list.get(i);
            		
            		String name = (String)appInfo.get("a0101");
            		if (kqVer.getVersion() == KqConstant.Version.STANDARD && !needKq((String) appInfo.get("nbase"), (String) appInfo.get("a0100"))) {
            			logAppInfo(templateId, "", appInfo, operState, name + "申请人不需要考勤：");
            			msglist.add(name + "不需要考勤！");
            			continue;
            		}
            		// 获取申请类型
            		String appTab = "";
            		if (IS_LEAVE == appType) {
                        appTab = "Q15";
                    } else if (IS_OVERTIME == appType) {
                        appTab = "Q11";
                    } else if (IS_OFFICELEAVE == appType) {
                        appTab = "Q13";
                    } else if (IS_LEAVEBACK == appType) {
            			//销假、销公出、销加班按类型获取对应的表
            			String itemId = (String) appInfo.get("QXJ03_O");
            			if(StringUtils.isEmpty(itemId)) {
                            itemId = (String) appInfo.get("qxj03_o");
                        }
            			
            			KqItem kqItem = new KqItem(this.conn);
            			HashMap<String, HashMap<String, String>> kqItemsMap = kqItem.getKqItem();
            			HashMap<String, String> kqItemMap = kqItemsMap.get(itemId);
            			appTab = kqItemMap.get("sdata_src");
            			if(StringUtils.isEmpty(appTab)) {
            				logAppInfo(templateId, "", appInfo, operState, "该考勤项目没有设置对应的考勤表！");
            				msglist.add(name + "该考勤项目没有设置对应的考勤表！");
            				continue;
            			}
            		} else {
            			msglist.add(name + "该申请单不属于考勤单据！");
            			continue;
            		}
            		
            		if (kqVer.getVersion() == KqConstant.Version.STANDARD) {
                		/** 优先校验日期  如果错误直接返回**/
                		HashMap map = getAppDates(appInfo, IS_LEAVEBACK==appType ? "qxj" : appTab.toLowerCase());
                        Date startTime = (Date)map.get("tableZ1");
                        Date endTime = (Date)map.get("tableZ3");
                        // 开始、结束时间是否填写
                        if (null == startTime || null == endTime) {
                        	msglist.add(name + "申请开始或结束时间不能为空，请检查申请单数据是否完整！");
                			continue;
                        }
                        // 开始、结束时间是否合法
                        if (startTime.after(endTime)) {
                        	msglist.add(name + "申请开始时间不能大于申请结束时间！");
                			continue;
                        }
                        
                		//验证单号正确性
                		int checkFlag = 0;
                		try {
                    		checkFlag = checkAppIdError(appInfo, appTab, appType);
                		}catch (GeneralException e) {
                			checkFlag = 1;
                			msglist.add(name + e.getErrorDescription());
                			continue;
                        } 
                		//已处理过的单据直接返回，不再重复处理。避免某些特殊情况下，出现数据不一致，导致重复审批不了的情况。
                		if (1 == checkFlag) {
                            continue;
                        }
                		
                		String leaveId = (String) appInfo.get("QXJ01_O");
                		if(StringUtils.isEmpty(leaveId)) {
                            leaveId = (String) appInfo.get("qxj01_o");
                        }
                		
                		if (IS_LEAVEBACK == appType && StringUtils.isEmpty(leaveId)) {
                            appInfo.set("QXJ01_O", getLeaveId(appInfo, appTab));
                        }
            		}
            		// 删除申请单不需校验
            		if ("10".equals(operState)) {
            			logAppInfo(templateId, appTab, appInfo, operState, "删除申请单：");
            			isOK = delAppByAppInfo(appInfo, appTab);
            		}else {
                			RecordVo vo = null;
                			try {
                				vo = getCheckAppVo(templeteBo, appInfo, appTab, operState, appType);
                			}catch (GeneralException e) {
                				vo = null;
                				msglist.add(e.getErrorDescription());
                				continue;
                			}
                			
                			if (vo == null) {
                				logAppInfo(templateId, appTab, appInfo, operState, name + "申请单无效：");
                				msglist.add(name + "申请单无效");
                				continue;
                			}
                			
                			if (kqVer.getVersion() == KqConstant.Version.STANDARD) {
                			// 销假申请再次单独校验
                			if(IS_LEAVEBACK == appType) {
                				vo.setString(appTab.toLowerCase() + "17", "1");
                				String sp = "";
                				if ("07".equals(operState)) {
                					vo.setString(appTab.toLowerCase()+"z5", "07");
                					sp = "";
                				} else if ("03".equals(operState)) {
                					vo.setString(appTab.toLowerCase()+"z5", "03");
                					sp = "5";
                				} else {
                					vo.setString(appTab.toLowerCase()+"z5", "02");
                					sp = "";
                				}
                				String sels = vo.getString(appTab.toLowerCase()+"03");
                				CancelHols cancelHols = new CancelHols(this.userView, this.conn);
                				// 销假申请只校验标识
                				cancelHols.setCheckFlag("0");
                				try {
                					cancelHols.cancelTimeApp(vo, sels, vo.getDate(appTab.toLowerCase()+"z1"), vo.getDate(appTab.toLowerCase()+"z3"), true, sp, "app", appTab.toLowerCase());
                				}catch (GeneralException e) {
                					msglist.add(e.getErrorDescription());
                					continue;
                				} 
                			}
            		    }
            			
            			volist.add(appType);
            			volist.add(appTab);
            			volist.add(appInfo);
            			volist.add(vo);
            			alllist.add(volist);
            		}
            	}
            	// 若校验出错误数据，则直接返回错误提示信息
            	if(msglist.size() > 0) {
                    return msglist;
                }
                
        	/** 先校验后  再集中对数据库操作 **/
        	for(int i=0;i<alllist.size();i++) {
        		ArrayList applist = alllist.get(i);
        		int appTypen = (Integer) applist.get(0);
        		String appTab = (String) applist.get(1);
        		LazyDynaBean appInfo = (LazyDynaBean) applist.get(2);
        		RecordVo vo = (RecordVo) applist.get(3);
        		
        		//销假申请单独处理
        		if(IS_LEAVEBACK == appTypen) {
        			vo.setString(appTab.toLowerCase() + "17", "1");
        			String sp = "";
        			if ("07".equals(operState)) {
        				vo.setString(appTab.toLowerCase()+"z5", "07");
        				sp = "";
        			} else if ("03".equals(operState)) {
        				vo.setString(appTab.toLowerCase()+"z5", "03");
        				sp = "5";
        			} else {
        				vo.setString(appTab.toLowerCase()+"z5", "02");
        				sp = "";
        			}
        			String sels = vo.getString(appTab.toLowerCase()+"03");
        			CancelHols cancelHols = new CancelHols(this.userView, this.conn);
        			// 37801 如果是报批状态销假只是校验不进行扣减等操作
        			if("02".equalsIgnoreCase(vo.getString(appTab.toLowerCase()+"z5"))) {
                        cancelHols.setCheckFlag("0");
                    }
        			cancelHols.cancelTimeApp(vo, sels, vo.getDate(appTab.toLowerCase()+"z1"), vo.getDate(appTab.toLowerCase()+"z3"), true, sp, "app", appTab.toLowerCase());
        			
        		} else {
        			// 进行扣减年假或调休假
        			if(("q15").equalsIgnoreCase(appTab) && "03".equals(operState)) {
        				updateHolidayByAppInfo(vo, operState);
        			}
        			
        			if (kqVer.getVersion() == KqConstant.Version.STANDARD) {
            			String keyFld = appTab.toLowerCase() + "01";
            			if (!isExistRecord(appTab, keyFld, vo.getString(keyFld))) {
            				logAppInfo(templateId, appTab, appInfo, operState, "新增申请单：");
            				dao.addValueObject(vo);
            			} else {
            				logAppInfo(templateId, appTab, appInfo, operState, "更新申请单：");
            				dao.updateValueObject(vo);
            			}
        			}
        		}
        	}
        } catch (GeneralException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return msglist;
    }
    
    /**
     * 校验完后进行扣减年假或调休假
     * @param appInfo
     * @param table
     * @return
     */
    private boolean updateHolidayByAppInfo(RecordVo vo, String operState) throws GeneralException{
    	
    	try {
    		// 年假和调休假批准时，需进行天数扣减  33652 linbz 增加校验 销假除外
//    		if (table.equalsIgnoreCase("q15") && ("03".equals(operState)||"02".equals(operState))) {
    			vo.setString("q1517", "0");
    			int leaveType = getLeaveType(vo.getString("q1503"), vo.getString("nbase"), vo.getString("a0100"));
    			
    			AnnualApply annualApply = new AnnualApply(this.userView, this.conn);
    			
    			if (leaveType == LEAVE_IS_HOLIDAY) {
    				String leaveTypeId = vo.getString("q1503");
    				String targetHolidayId = switchTypeIdFromHolidayMap(leaveTypeId);
    				
    				//考勤规则应取改假类自己的规则
    				HashMap kqItem_hash = annualApply.count_Leave(leaveTypeId);
    				kqItem_hash.remove("item_unit");
    				kqItem_hash.put("item_unit", KqConstant.Unit.DAY);
    				
    				//假期时长扣减规则参数
    				float[] holiday_rules = annualApply.getHoliday_minus_rule();
    				
    				//批准 进行扣减
	                if ("03".equals(operState) ) {
	                    //按规则计算申请假期时长
	                    float d_Count = annualApply.calcLeaveAppTimeLen(vo.getString("nbase"), vo.getString("a0100"), (String) vo.getString("b0110"), 
	                            vo.getDate("q15z1"), vo.getDate("q15z3"), kqItem_hash, holiday_rules, Integer.MAX_VALUE);
	                   
	                    //扣减假期时长
	                    String history = annualApply.upLeaveManage(vo.getString("a0100"), vo.getString("nbase"),
	                            targetHolidayId, OperateDate.dateToStr(vo.getDate("q15z1"), "yyyy-MM-dd HH:mm"), 
	                            OperateDate.dateToStr(vo.getDate("q15z3"), "yyyy-MM-dd HH:mm"), d_Count, "1", 
	                            (String) vo.getString("b0110"), kqItem_hash, holiday_rules);
	                    // 记录本记录年假的扣减情况
	                    vo.setString("history", history);
	                } 
    			} else if (leaveType == LEAVE_USED_OVERTIME) {// && "03".equals(operState)
    				
    				String leaveTypeId = vo.getString("q1503");
    				//考勤规则应取该假类自己的规则
    				HashMap kqItem_hash = annualApply.count_Leave(leaveTypeId);
    				kqItem_hash.remove("item_unit");
    				kqItem_hash.put("item_unit", KqConstant.Unit.MINUTE);
    				int timeCount = 0;
    				//按规则计算申请假期时长
    				timeCount = Math.round(annualApply.calcLeaveAppTimeLen(vo.getString("nbase"), vo.getString("a0100"), (String) vo.getString("b0110"), 
    						vo.getDate("q15z1"), vo.getDate("q15z3"), kqItem_hash, null, Integer.MAX_VALUE));
    				/** 审批请假单时 如果是调休假 更新调休明细表Q33 */
    				// 只有批准时更新q33
	                if (timeCount>0  && "03".equals(operState)) {
	                	 UpdateQ33 updateq33 = new UpdateQ33(this.userView, this.conn);
	                	 // 48612
	                	 updateq33.setStartDate(vo.getDate("q15z1"));
	                	 updateq33.upQ33(vo.getString("nbase"), vo.getString("a0100"), timeCount);
	                }
    			}
//    		} 
    	} catch (GeneralException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
    	return false;
    }
    
    private boolean delAppByAppInfo(LazyDynaBean appInfo, String table) {
        boolean isOK = false;

        String appId = getAppIdByAppInfo(appInfo, table);
        if (appId == null || "".equals(appId.trim())) {
            return true;
        }

        try {
            dao.update("DELETE FROM " + table + " WHERE " + table + "01='" + appId + "'");
            isOK = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isOK;
    }

    private boolean isExistRecord(String table, String priKeyFld, String value) {
        boolean isExist = false;
        RowSet rs = null;
        try {
            rs = dao.search("SELECT 1 FROM " + table + " WHERE " + priKeyFld + "='" + value + "'");
            isExist = rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }

        return isExist;
    }

    /**
     * 检查申请单号重复情况并抛异常，阻止错误数据进入考勤申请表
     * @param appInfo
     * @param table
     * @throws GeneralException
     * @return flag 0:单号正常；1:本人同单号单据已批
     */
    private int checkAppIdError(LazyDynaBean appInfo, String table, int appType) throws GeneralException {
        int flag = 0;
        String error = "申请单号异常，请立即联系管理员！\n";
        
        String appTable = table;
        if (IS_LEAVEBACK == appType) {
            appTable = "QXJ";
        }
        	
        //检查不同人之间是否存在单号重复情况
        String appId = getAppIdByAppInfo(appInfo, appTable);
        if(StringUtils.isEmpty(appId)) {
            return flag;
        }
        
        String a0100 = (String)appInfo.get("a0100");
        String nbase = (String)appInfo.get("nbase");
        String whr = table + "01='" + appId + "' and (a0100<>'" + a0100 + "' or nbase<>'" + nbase + "')";
        if (dbHelper.isRecordExist(table, whr)) {
            throw new GeneralException(error + "申请表中已存在其他人员相同单号数据(单号：" + appId + ")。");
        }
        
        //检查本人是否存在已批申请与本次申请单号重复情况
        whr = table + "01='" + appId + "' and a0100='" + a0100 + "' and nbase='" + nbase + "' and " + table+"z5='03'";
        if (dbHelper.isRecordExist(table, whr)) {
            //进一步检查单据内容是否相同           
            RecordVo vo = new RecordVo(table);
            vo.setString(table.toLowerCase() + "01", appId);
            try {
                vo = dao.findByPrimaryKey(vo);
                String q1103 = vo.getString(table.toLowerCase() + "03");
                Date q11z1 = vo.getDate(table.toLowerCase() + "z1");
                Date q11z3 = vo.getDate(table.toLowerCase() + "z3");
                
                //检查申请类型，起止时间是否一样，都一样就认为是已经处理过的单据
                if (q1103.equalsIgnoreCase((String)appInfo.get(appTable.toLowerCase() + "03"))
                        && 0 == q11z1.compareTo((Date)appInfo.get(appTable.toLowerCase() + "z1"))
                        && 0 == q11z3.compareTo((Date)appInfo.get(appTable.toLowerCase() + "z3"))) {
                    flag = 1;
                } 
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (flag != 1){
                throw new GeneralException(error + "申请表中已存在相同单号的已批准数据(单号：" + appId + ")。");
            }
        }
        
        return flag;
    }
    
    private int getAppTypeByTemplate(TemplateTableParamBo templateBo) {
        int appType = -1;

        String strAppType = templateBo.getKq_type();
        if ("".equals(strAppType)) {
            strAppType = "-1";
        }

        appType = Integer.parseInt(strAppType);

        return appType;
    }

    private RecordVo getAppVo(TemplateTableParamBo templateBo, LazyDynaBean appInfo, String table, String operState, int appType)
            throws GeneralException {
        RecordVo vo = null;

        String appTable = table;
        if (IS_LEAVEBACK == appType) {
            appTable = "QXJ";
        }
        
        // 没有定义映射关系
        String mapping = templateBo.getKq_field_mapping();
        if (mapping == null || "".equals(mapping)) {
            return vo;
        }

        // 没有对应申请类型或类型值没填
        String appTypeId = (String) appInfo.get(appTable.toLowerCase() + "03");
        if (IS_LEAVEBACK == appType) {
            appTypeId = (String) appInfo.get(appTable.toLowerCase() + "03_o");
        }
        
        if (null == appTypeId || "".equals(appTypeId) || 1 == appTypeId.length()) {
            return vo;
        }

        String[] busiMapping = mapping.split(",");

        vo = new RecordVo(table.toLowerCase());
        try {
            for (int i = 0; i < busiMapping.length; i++) {
                String aMapping = busiMapping[i];
                int pos = aMapping.indexOf(":");
                String appItemId = aMapping.substring(0, pos).toLowerCase();
                String kqItemId = appItemId.toLowerCase().replace(appTable.toLowerCase(), table).toLowerCase();
                if(kqItemId.endsWith("z1_o") || kqItemId.endsWith("z3_o")) {
                    continue;
                }
                
                kqItemId = kqItemId.replace("01_o", "19").replace("03_o", "03");
                String ydItemId = aMapping.substring(pos + 1);

                if (ydItemId == null || "".equals(ydItemId)) {
                    continue;
                }

                if (kqItemId == null || "".equals(kqItemId)) {
                    continue;
                }

                if (appInfo.get(appItemId) != null) {
                    //参考班次校验
                    if ("Q1104".equalsIgnoreCase(kqItemId)) {
                        String classId = appInfo.get(appItemId).toString().trim();
                        if ("0".equals(classId)) {
                            classId = "";
                        }
                        
                        //检查填写的班次是否存在
                        if (!"".equals(classId) && !dbHelper.isRecordExist("kq_class", "class_id=" + classId)) {
                            throw new GeneralException(ResourceFactory.getProperty("kq.class.re.class") 
                                    + ResourceFactory.getProperty("constant.e_factornoexist"));
                        }
                        
                        vo.setObject(kqItemId, classId);
                    }
                    // 39991 防止 申请事由、部门领导审批意见、单位领导审批意见 长度超过字典定义长度 故增加校验截取
                    else if ((table.toLowerCase()+"07").equals(kqItemId) || (table.toLowerCase()+"11").equals(kqItemId)
                    		|| (table.toLowerCase()+"15").equals(kqItemId)) {
                        
                    	String reason = (String)appInfo.get(appItemId);
                        FieldItem item = DataDictionary.getFieldItem(kqItemId, table);
                        if ("A".equals(item.getItemtype())) {
                            if (null != reason) {
                                reason = PubFunc.splitString(reason, item.getItemlength());
                            }
                        }
                        vo.setObject(kqItemId, reason);
                    }else {
                        vo.setObject(kqItemId, appInfo.get(appItemId));                        
                    }
                }
            }

            // 取模板中传来的单号
            String appId = getAppIdByAppInfo(appInfo, appTable);
            // 没单号的生成新单号
            if (appId == null || "".equals(appId)) {
                appId = createAppId(table);
                appInfo.set(appTable.toLowerCase() + "01", appId);
            }
            vo.setString(table.toLowerCase() + "01", appId);

            String nbase = (String) appInfo.get("nbase");
            ArrayList dbnames = DataDictionary.getDbpreList();
            for (int j = 0; j < dbnames.size(); j++) {
                if (nbase.equalsIgnoreCase((String) dbnames.get(j))) {
                    nbase = (String) dbnames.get(j);
                    break;
                }
            }

            vo.setString("nbase", nbase); // 应用库前缀
            vo.setString("a0100", (String) appInfo.get("a0100")); // 人员编号

            vo.setObject("b0110", appInfo.get("b0110")); // 单位编码
            vo.setObject("e0122", appInfo.get("e0122")); // 部门编码
            vo.setObject("a0101", appInfo.get("a0101")); // 姓名
            vo.setObject("e01a1", appInfo.get("e01a1")); // 职务编码
            // 增加校验 当前审批人及领导不能是本人用户名，现规则应是上级审批后添加领导用户名
            // 37961 更换审批人取消校验人员库是否相同
            if (this.userView != null 
            		&& !((String) appInfo.get("a0100")).equalsIgnoreCase(this.userView.getA0100())) {
                vo.setString(table.toLowerCase() + "09", this.userView.getUserFullName()); // 部门领导
                vo.setString(table.toLowerCase() + "13", this.userView.getUserFullName()); // 单位领导
            }

            if ("08".equalsIgnoreCase(operState)) {
                // 驳回 审批结果固定为不同意
                vo.setString(table.toLowerCase() + "z0", "02");
            } else {
                //  批准 审批结果固定为同意
                vo.setString(table.toLowerCase() + "z0", "01");
            }
            
            if ("02".equalsIgnoreCase(operState)) {
            	// 报批  审批结果固定为未审阅
                vo.setString(table.toLowerCase() + "z0", "03");
            }
            
            if ("03".equalsIgnoreCase(operState)) {
            	// 审批日期
            	 Date appDate = vo.getDate(table.toLowerCase() + "z7");
            	 if (null == appDate) {
                     vo.setDate(table.toLowerCase() + "z7", new Date());
                 }
            }
            // 申请时间 34953 防止不走流程，直接批准或其他操作没有申请日期对应指标
       	 	Date appDate = vo.getDate(table.toLowerCase() + "05");
       	 	if (null == appDate) {
                vo.setDate(table.toLowerCase() + "05", new Date());
            }
       	 	
            // 审批状态
            vo.setString(table.toLowerCase() + "z5", operState);

            if (IS_LEAVEBACK == appType) {
            	String qxj19 = (String) appInfo.get("QXJ01_O");
            	if(StringUtils.isEmpty(qxj19)) {
                    qxj19 = (String) appInfo.get("qxj01_o");
                }
            	
            	vo.setString(table.toLowerCase() + "19", qxj19);
            }
            // 检查 销假 申请是否合法，不合法直接返回null
            if(IS_LEAVEBACK == appType) {
            	vo.setString(table.toLowerCase() + "17", "1");
            	// 只校验报批的销假申请
            	if("02".equalsIgnoreCase(operState)) {
            		String msg = checkQxjAppInfo(vo, table);
            		if (StringUtils.isNotEmpty(msg)) {
	   					 throw new GeneralException(msg);
	   				}
            	}
            }
            // 检查其他申请是否合法，不合法直接返回null
            else {
            	if (!checkAppInfo(table.toLowerCase(), vo, appInfo, appType)) {
                    return null;
                }
            }

        } catch (GeneralException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return vo;
    }

    private RecordVo getCheckAppVo(TemplateTableParamBo templateBo, LazyDynaBean appInfo, String table, String operState, int appType)
            throws GeneralException {
        RecordVo vo = null;

        String appTable = table;
        if (IS_LEAVEBACK == appType) {
            appTable = "QXJ";
        }
        
        // 没有定义映射关系
        String mapping = templateBo.getKq_field_mapping();
        if (mapping == null || "".equals(mapping)) {
            return vo;
        }

        // 没有对应申请类型或类型值没填
        String appTypeId = (String) appInfo.get(appTable.toLowerCase() + "03");
        if (IS_LEAVEBACK == appType) {
            appTypeId = (String) appInfo.get(appTable.toLowerCase() + "03_o");
        }
        
        if (null == appTypeId || "".equals(appTypeId) || 1 == appTypeId.length()) {
            return vo;
        }

        String[] busiMapping = mapping.split(",");

        vo = new RecordVo(table.toLowerCase());
        try {
            for (int i = 0; i < busiMapping.length; i++) {
                String aMapping = busiMapping[i];
                int pos = aMapping.indexOf(":");
                String appItemId = aMapping.substring(0, pos).toLowerCase();
                String kqItemId = appItemId.toLowerCase().replace(appTable.toLowerCase(), table).toLowerCase();
                if(kqItemId.endsWith("z1_o") || kqItemId.endsWith("z3_o")) {
                    continue;
                }
                
                kqItemId = kqItemId.replace("01_o", "19").replace("03_o", "03");
                String ydItemId = aMapping.substring(pos + 1);

                if (ydItemId == null || "".equals(ydItemId)) {
                    continue;
                }

                if (kqItemId == null || "".equals(kqItemId)) {
                    continue;
                }

                if (appInfo.get(appItemId) != null) {
                    //参考班次校验
                    if ("Q1104".equalsIgnoreCase(kqItemId)) {
                        String classId = appInfo.get(appItemId).toString().trim();
                        if ("0".equals(classId)) {
                            classId = "";
                        }
                        
                        //检查填写的班次是否存在
                        if (!"".equals(classId) && !dbHelper.isRecordExist("kq_class", "class_id=" + classId)) {
                            throw new GeneralException(ResourceFactory.getProperty("kq.class.re.class") 
                                    + ResourceFactory.getProperty("constant.e_factornoexist"));
                        }
                        
                        vo.setObject(kqItemId, classId);
                    } else if ((table.toLowerCase() + "07").equals(kqItemId) || (table.toLowerCase() + "11").equals(kqItemId)) {
                        String reason = (String)appInfo.get(appItemId);
                        
                        // 防止事由超长，按字典定义长度进行截断	38760 防止领导审批意见超长
                        FieldItem item = DataDictionary.getFieldItem(kqItemId, table);
                        if ("A".equals(item.getItemtype())) {
                            if (null != reason) {
                                reason = PubFunc.splitString(reason, item.getItemlength());
                            }
                        }
                        vo.setObject(kqItemId, reason);
                    }else {
                        vo.setObject(kqItemId, appInfo.get(appItemId));                        
                    }
                }
            }

            // 取模板中传来的单号
            String appId = getAppIdByAppInfo(appInfo, appTable);
            // 没单号的生成新单号
            if (appId == null || "".equals(appId)) {
                appId = createAppId(table);
                appInfo.set(appTable.toLowerCase() + "01", appId);
            }
            vo.setString(table.toLowerCase() + "01", appId);

            String nbase = (String) appInfo.get("nbase");
            ArrayList dbnames = DataDictionary.getDbpreList();
            for (int j = 0; j < dbnames.size(); j++) {
                if (nbase.equalsIgnoreCase((String) dbnames.get(j))) {
                    nbase = (String) dbnames.get(j);
                    break;
                }
            }

            vo.setString("nbase", nbase); // 应用库前缀
            vo.setString("a0100", (String) appInfo.get("a0100")); // 人员编号

            vo.setObject("b0110", appInfo.get("b0110")); // 单位编码
            vo.setObject("e0122", appInfo.get("e0122")); // 部门编码
            vo.setObject("a0101", appInfo.get("a0101")); // 姓名
            vo.setObject("e01a1", appInfo.get("e01a1")); // 职务编码
            // 增加校验 当前审批人及领导不能是本人用户名，现规则应是上级审批后添加领导用户名
            // 37961 更换审批人取消校验人员库是否相同
            if (this.userView != null 
            		&& !((String) appInfo.get("a0100")).equalsIgnoreCase(this.userView.getA0100())) {
                vo.setString(table.toLowerCase() + "09", this.userView.getUserFullName()); // 部门领导
                vo.setString(table.toLowerCase() + "13", this.userView.getUserFullName()); // 单位领导
            }

            if ("08".equalsIgnoreCase(operState)) {
                // 驳回 审批结果固定为不同意
                vo.setString(table.toLowerCase() + "z0", "02");
            } else {
                //  批准 审批结果固定为同意
                vo.setString(table.toLowerCase() + "z0", "01");
            }
            
            if ("02".equalsIgnoreCase(operState)) {
            	// 报批  审批结果固定为未审阅
                vo.setString(table.toLowerCase() + "z0", "03");
            }
            
            if ("03".equalsIgnoreCase(operState)) {
            	// 审批日期
            	 Date appDate = vo.getDate(table.toLowerCase() + "z7");
            	 if (null == appDate) {
                     vo.setDate(table.toLowerCase() + "z7", new Date());
                 }
            }
            // 申请时间 34953 防止不走流程，直接批准或其他操作没有申请日期对应指标
       	 	Date appDate = vo.getDate(table.toLowerCase() + "05");
       	 	if (null == appDate) {
                vo.setDate(table.toLowerCase() + "05", new Date());
            }
       	 	
            // 审批状态
            vo.setString(table.toLowerCase() + "z5", operState);

            if (IS_LEAVEBACK == appType) {
            	String qxj19 = (String) appInfo.get("QXJ01_O");
            	if(StringUtils.isEmpty(qxj19)) {
                    qxj19 = (String) appInfo.get("qxj01_o");
                }
            	
            	vo.setString(table.toLowerCase() + "19", qxj19);
            }
            // 检查 销假 申请是否合法，不合法直接返回null
            if(IS_LEAVEBACK == appType) {
            	vo.setString(table.toLowerCase() + "17", "1");
            	// 只校验报批的销假申请
            	if("02".equalsIgnoreCase(operState)) {
            		String msg = checkQxjAppInfo(vo, table);
            		if (StringUtils.isNotEmpty(msg)) {
	   					 throw new GeneralException(msg);
	   				}
            	}
            }
            // 检查其他申请是否合法，不合法直接返回null
            else {
            	if (!checkAppInfo(table.toLowerCase(), vo, appInfo, appType)) {
                    return null;
                }
            }

        } catch (GeneralException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return vo;
    }
    
    /**
     * 验报批的销假申请
     * @param vo
     * @param table
     * @return
     */
    private String checkQxjAppInfo(RecordVo vo, String table) throws GeneralException {
    	
    	String msg = "";
    	String qxj19 = vo.getString(table.toLowerCase() + "19");
    	Date z1 = vo.getDate(table.toLowerCase() + "z1");
    	Date z3 = vo.getDate(table.toLowerCase() + "z3");
    	// 查询销假申请起止日期是否合法
    	if(z1.after(z3)){
    		msg = "开始时间不能大于结束时间！";
    		return msg;
		}
    	RowSet rs = null;
    	try {
        	// 查询销假单据是否存在
        	StringBuffer sql = new StringBuffer("");
        	sql.append("select ").append(table.toLowerCase()).append("19 table19,").append(table.toLowerCase()).append("z5 tablez5 ");
        	sql.append(" from " + table);
        	sql.append(" where ").append(table.toLowerCase()).append("01 in(");
        	sql.append(" select ").append(table.toLowerCase()).append("01 ");
            sql.append(" from " + table);
            sql.append(" where ").append(table.toLowerCase()).append("19=? ");
            // 36369 完善校验，不能等于本身数据的单号
            sql.append(" and ").append(table.toLowerCase()).append("01<>? ");
            sql.append(") ");
            ArrayList list = new ArrayList();
            list.add(qxj19);
            list.add(vo.getString(table.toLowerCase() + "01"));
            rs = dao.search(sql.toString(), list);
            // 目前控制一个单据只能有一条销假单
            boolean bool = false;
            while (rs.next()) {
            	String table19 = rs.getString("table19");
            	// 34940 完善校验条件，只有驳回状态的单据可以重复，防止多个模板报批已驳回单据时出现的冲突
            	String tablez5 = rs.getString("tablez5");
            	if(!qxj19.equalsIgnoreCase(table19) 
            			|| (!"07".equalsIgnoreCase(tablez5) && qxj19.equalsIgnoreCase(table19))
            			|| bool) {
            		msg = "该单据已存在销假申请，不能重复操作！";
            		return msg;
				}
            	bool = true;
            }
            // 查询销假申请起止日期 是否在原申请单的日期范围之内
        	sql.setLength(0);
        	sql.append("select ").append(table.toLowerCase()).append("z1 Z1,").append(table.toLowerCase()).append("z3 Z3");
            sql.append(" from " + table);
            sql.append(" where a0100=? and nbase=? and ").append(table.toLowerCase()).append("01=? ");
            list = new ArrayList();
            list.add(vo.getString("a0100"));
            list.add(vo.getString("nbase"));
            list.add(qxj19);
        
            rs = dao.search(sql.toString(), list);
            if (rs.next()) {
            	Date startDate = rs.getTimestamp("Z1");
            	Date endDate = rs.getTimestamp("Z3");
            	if (z1.before(startDate) || z3.after(endDate)) {
            		msg = "超出了可销假范围，请重新申请！";
            		return msg;
				}
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
    	
    	return msg;
    }
    
    private String getAppIdByAppInfo(LazyDynaBean appInfo, String table) {
        String appId = null;
        Object valueObj = appInfo.get(table.toLowerCase() + "01");
        
        if (valueObj != null && valueObj instanceof String) {
            appId = (String) appInfo.get(table.toLowerCase() + "01");
        }
        // 36371 业务模板删除销假申请单特殊处理
        if(null == appId && IS_LEAVEBACK == appType) {
            appId = (String) appInfo.get("qxj01");
        }

        return appId;
    }

    private String createAppId(String table) {
        IDGenerator idg = new IDGenerator(2, this.conn);
        String insertid = "";
        try {
            boolean iscorrect = false;
            while (!iscorrect) {
                insertid = idg.getId((table + "." + table + "01").toUpperCase());
                iscorrect = checkAppId(table, insertid, dao);
                
            }
        } catch (GeneralException e) {
            e.printStackTrace();
        }
        return insertid;
    }

    private boolean checkAppId(String table, String id, ContentDAO dao) {
        boolean iscorrect = true;
        RowSet rs = null;
        try {
            String sql = "select 1 from " + table + " where " + table + "01='" + id + "'";
            rs = dao.search(sql);
            if (rs.next()) {
                iscorrect = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return iscorrect;
    }

    /**
     * 校验申请记录是否合法
     * 
     * @param table
     * @param appVo
     * @return
     */
    private boolean checkAppInfo(String table, RecordVo appVo, LazyDynaBean appInfo, int appType) throws GeneralException {
        boolean isOK = false;

        String startTimeFld = table.toLowerCase() + "z1";
        String endTimeFld = table.toLowerCase() + "z3";
        String keyFld = table.toLowerCase() + "01";

        Date startTime = appVo.getDate(startTimeFld);
        Date endTime = appVo.getDate(endTimeFld);

        // 开始、结束时间是否填写
        if (null == startTime || null == endTime) {
            throw new GeneralException(appVo.getString("a0101") + "申请开始或结束时间不能为空，请检查申请单数据是否完整！");
        }
        // 开始、结束时间是否合法
        if (startTime.after(endTime)) {
            throw new GeneralException(appVo.getString("a0101") + "申请开始时间不能大于申请结束时间！");
        }

        // 申请是否在已经封存的期间内
        AnnualApply annualApply = new AnnualApply(userView, conn);
        
        if (this.kqVer.getVersion() == KqConstant.Version.STANDARD) {
            annualApply.checkAppInSealDuration(startTime, appVo.getString("a0101"));
    
            String qxj19 = "";
            if (IS_LEAVEBACK == appType) {
                qxj19 = appVo.getString(table + "19");
            }
            
            // 是否与其它申请冲突
            annualApply.isRepeatedAllAppType(table, appVo.getString("nbase"), appVo.getString("a0100"), 
                    appVo.getString("a0101"), DateUtils.format(startTime, "yyyy-MM-dd HH:mm"), 
                    DateUtils.format(endTime, "yyyy-MM-dd HH:mm"), this.conn, appVo.getString(keyFld), qxj19);
        }
        
        if ("q11".equalsIgnoreCase(table)) {
            if (this.kqVer.getVersion() == KqConstant.Version.STANDARD) {
                //检查加班类型是否与申请日期匹配
                checkOverTimeType(appVo.getString("nbase"),  appVo.getString("a0100"), appVo.getString("a0101"),
                        appVo.getString("b0110"), startTime, endTime, appVo.getString("q1103"));
                
                String error = "";
                // 校验该加班类型是否属于调休加班
                String iftoRestField = KqUtilsClass.getFieldByDesc(table, ResourceFactory.getProperty("kq.self.app.workingdaysoff.yesorno"));
                if (iftoRestField != null && iftoRestField.length() > 0) {
                    String iftoRest = appVo.getString(iftoRestField);
                    // 不知为何查出来再原封不动塞进去？？？ 先注释
    //                appVo.setString(iftoRestField, iftoRest);
                    if ("1".equals(iftoRest)) {
                        error = annualApply.CheckAppTypeIsToLeave(appVo.getString("q1103"));
                        if (StringUtils.isNotEmpty(error)){
                            throw new GeneralException(error);
                        }
                    }
                }
                
                // 检查加班限额  优化该校验方法
                error = annualApply.checkOverTimelenMorethanLimit(appVo, "1");
                if (StringUtils.isNotEmpty(error)) {
                        throw new GeneralException(error);
                }
                
                //检查调休加班限额
                KqOverTimeForLeaveBo overTimeForLeaveBo = new KqOverTimeForLeaveBo(this.conn, this.userView);
                error = overTimeForLeaveBo.checkOvertimeForLeaveMaxHour(appVo);
                if(!StringUtils.isEmpty(error)) {
                    throw new GeneralException(error);
                }
            }
        }
        // 年假和调休假批准时，需进行天数扣减  33652 linbz 增加校验 销假除外
        else if ("q15".equalsIgnoreCase(table) ) {//&& ("03".equals(operState)||"02".equals(operState))
        	appVo.setString("q1517", "0");
        	String nbase = appVo.getString("nbase");
        	String a0100 = appVo.getString("a0100");
            int leaveType = getLeaveType(appVo.getString("q1503"), nbase, a0100);
            if (leaveType == LEAVE_IS_HOLIDAY) {
            	
                String leaveTypeId = appVo.getString("q1503");
                String targetHolidayId = switchTypeIdFromHolidayMap(leaveTypeId);
                //批准 进行扣减
//                if ("03".equals(operState) ) {
//                } else {
                //报批 进行年假天数校验
                ValidateAppOper validateAppOper = new ValidateAppOper(this.userView, this.conn);
                String errorInfo = validateAppOper.checkHoliday(appVo.getDate("q15z1"), appVo.getDate("q15z3"), appVo, targetHolidayId, "up");
                if (!"".equals(errorInfo)) {
                    throw new GeneralException(errorInfo);
                }
//                }
            } else if (leaveType == LEAVE_USED_OVERTIME) {// && "03".equals(operState)
                
                String leaveTypeId = appVo.getString("q1503");
                //考勤规则应取该假类自己的规则
                HashMap kqItem_hash = annualApply.count_Leave(leaveTypeId);
                kqItem_hash.remove("item_unit");
                kqItem_hash.put("item_unit", KqConstant.Unit.MINUTE);
                int timeCount = 0;
                //按规则计算申请假期时长
                timeCount = Math.round(annualApply.calcLeaveAppTimeLen(nbase, a0100, (String) appVo.getString("b0110"), 
                		appVo.getDate("q15z1"), appVo.getDate("q15z3"), kqItem_hash, null, Integer.MAX_VALUE));
                // 报批调休假时增加控制校验
                GetValiateEndDate ve = new GetValiateEndDate(this.userView,	this.conn);
                String hr_counts = String.valueOf(timeCount);
                Map infoMap = new HashMap();
                infoMap.put("a0100", a0100);
                infoMap.put("a0101", appVo.getString("a0101"));
                // 请调休假 检查调休假可用时长是否够用 暂用该方法
                String err_message = ve.checkUsableTime(appVo.getDate("q15z1"), infoMap, "", nbase, null, hr_counts);
                if (StringUtils.isNotEmpty(err_message)) {
                	throw new GeneralException(err_message);
                }
                /** 审批请假单时 如果是调休假 更新调休明细表Q33 */
                // 只有批准时更新q33
//                if (timeCount>0  && "03".equals(operState)) {
//                }
            }
        } 
        
        isOK = true;

        return isOK;
    }
    
    private void checkOverTimeType(String nbase, String a0100, String a0101, String b0110, Date startDate, Date endDate, String overTimeType) throws GeneralException {
        ValidateAppOper validateAppOper = new ValidateAppOper(this.userView, this.conn);
        String app_class_id = null; //vo.getString("q1104"); 暂不考虑参考班次

        //是否需要判断加班类型与日期一致
        if (!validateAppOper.is_OVERTIME_TYPE()) {
            return;
        }
        
        //判断是否是公休日
        if (overTimeType.startsWith("10")) {            
            if (!validateAppOper.is_Rest(startDate, endDate, a0100, nbase, app_class_id)) {
                throw new GeneralException(a0101 + " " + validateAppOper.getNo_Rest_mess());
            }

            if (validateAppOper.is_Feast(startDate, endDate, b0110)) {
                throw new GeneralException("【" + a0101 + "】" +  ResourceFactory.getProperty("error.kq.nofeast"));
            }
        }

        //判断是否是节假日
        if (overTimeType.startsWith("11")) {
            if (!validateAppOper.is_Feast(startDate, endDate, b0110, app_class_id)) {
                throw new GeneralException("【" + a0101 + "】" +  ResourceFactory.getProperty("error.kq.nfeast"));
            }
        }

        // 平时加班
        if (overTimeType.startsWith("12")) {
            if (!validateAppOper.if_Peacetime(startDate, endDate, nbase, a0100)) {
                throw new GeneralException("【" + a0101 + "】" + validateAppOper.getRest_Peacetime_mess());
            }
        }
    }

    /*
     * 得到请假的分类（-2: 不是请假；-1：无此请假类型；0：年假等假期；1：倒休假; 2:普通请假)
     */
    private int getLeaveType(String leaveTypeId, String nbase, String a0100) {
        int leaveType = LEAVE_IS_NOT;

        if (leaveTypeId == null || "".equals(leaveTypeId)) {
            return leaveType;
        }

        // 不是请假
        if (!leaveTypeId.startsWith("0")) {
            return leaveType;
        }

        String sql = "SELECT 1 FROM kq_item WHERE item_id=?";
        ArrayList<String> sqlParams = new ArrayList<String>();
        sqlParams.add(leaveTypeId);
        RowSet rs = null;
        try {
            rs = dao.search(sql, sqlParams);

            // 不存在该请假类型
            if (!rs.next()) {
                leaveType = LEAVE_NOT_EXIST;
            } else {
                //取系统映射假期
                String targetHolidayId = switchTypeIdFromHolidayMap(leaveTypeId);
                
                // 存在要求的请假类型，检查是否为假期管理假类，或调休假
                KqParam kqParam = KqParam.getInstance();

                String b0110 = getB0110ByEmpInfo(nbase, a0100);
                // 本身是年假或映射的目标假是年假的
                if (kqParam.isHoliday(this.conn, b0110, leaveTypeId) 
                        || (!leaveTypeId.equals(targetHolidayId) 
                                && kqParam.isHoliday(this.conn, b0110, targetHolidayId))) {
                    leaveType = LEAVE_IS_HOLIDAY;
                } else {
                    String leaveTimeTypeUsedOverTime = kqParam.getLeaveTimeTypeUsedOverTime();
                    // 是调休假
                    if (leaveTimeTypeUsedOverTime.equalsIgnoreCase(leaveTypeId)) {
                        leaveType = LEAVE_USED_OVERTIME;
                    } else {
                        leaveType = LEAVE_NORMAL;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }

        return leaveType;
    }

    private double tranMinuteValueByItemUnit(String leaveTypeId, float minuteValue, float baseValue) {
        double tranedValue = minuteValue;
        
        AnnualApply annualApply = new AnnualApply(this.userView, this.conn);
        try {
            //考勤规则应取改假类自己的规则
            HashMap kqItemHash = annualApply.count_Leave(leaveTypeId);
            String itemUnit = PubFunc.DotstrNull((String)kqItemHash.get("item_unit"));
            tranedValue = annualApply.tranMinuteValueByUnit(minuteValue, itemUnit, baseValue);
        } catch(Exception e) {
            
        }
        
        return tranedValue;
    }
    
    private double getOverTimeToRestDays(LazyDynaBean appInfo) {
        double days = 0;
        String nbase = (String) appInfo.get("nbase");
        String a0100 = (String) appInfo.get("a0100");
        String leaveTypeId = (String) appInfo.get("type");
        
        GetValiateEndDate gve = new GetValiateEndDate(null, this.conn);
        // 47461 可休天数 按照申请单开始时间计算倒休区间内的天数  与考勤模块申请规则保持一致
        Date startDate = (Date) appInfo.get("starttime");
        int usableTime = gve.getTimesCount(startDate, nbase, a0100, this.conn);
        // 46452 获取参数中设置的标准工时
        float baseValue = Integer.parseInt(KqParam.getInstance().getSTANDARD_HOURS()) * 60;
        days = tranMinuteValueByItemUnit(leaveTypeId, usableTime, baseValue);
        
        //包含在途时长（未批时长）
        if (includePendingTimeLen(appInfo)) {
            try {
            	AnnualApply annual = new AnnualApply(this.userView, this.conn);
	            HashMap kqItem = annual.count_Leave(leaveTypeId);
	            String q1501 = (String) appInfo.get("q1501");
	            
	            String b0110 = getB0110ByEmpInfo(nbase, a0100);
	            days = days - annual.calcAppendingLeaveUsedOverTimeLen(
                    leaveTypeId, a0100, nbase, b0110, q1501, kqItem, "up", "", startDate);
            } catch (Exception e) {
                
            }
        }
        return days;
    }

    private double getUsedOverTimeToRestDays(LazyDynaBean appInfo) {
        double days = 0;

        String usable = KqParam.getInstance().getOVERTIME_FOR_LEAVETIME_LIMIT();
        if ("".equals(usable)) {
            return 0;
        }
// 上一版调休加有效期规则
//        Date today = new Date();// 结束时间改为当前系统时间
//        int validityTime = Integer.parseInt(usable);
//        String start_d = OperateDate.dateToStr(OperateDate.addDay(today, 0 - validityTime), "yyyy.MM.dd");
//        String end_d = OperateDate.dateToStr(today, "yyyy.MM.dd");
        
        KqOverTimeForLeaveBo kqOverTimeForLeave = new KqOverTimeForLeaveBo(this.conn, this.userView);
        // 47461 已休天数 按单据开始时间计算 
        HashMap period = kqOverTimeForLeave.getEffectivePeriod((Date) appInfo.get("starttime"));
        String start_d = ((String)period.get("from")).replaceAll("-", ".");
        String end_d = ((String)period.get("to")).replaceAll("-", ".");

        String nbase = (String) appInfo.get("nbase");
        String a0100 = (String) appInfo.get("a0100");
        String leaveTypeId = (String) appInfo.get("type");

        StringBuffer sql = new StringBuffer();
        sql.append("select sum(Q3307) from Q33");
        sql.append(" where nbase ='" + nbase + "' and a0100 ='" + a0100 + "'");
        sql.append(" and Q3303 >= '" + start_d + "' and Q3303 <= '" + end_d + "'");

        RowSet rs = null;
        int usedTime = 0;
        ContentDAO dao = new ContentDAO(conn);
        try {
            rs = dao.search(sql.toString());
            if (rs.next()) {
                usedTime = rs.getInt(1);
                // 46452 获取参数中设置的标准工时
                float baseValue = Integer.parseInt(KqParam.getInstance().getSTANDARD_HOURS()) * 60;
                days = tranMinuteValueByItemUnit(leaveTypeId, usedTime, baseValue);
                //days = usedTime / 480.0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }

        return days;
    }

    private boolean includePendingTimeLen(LazyDynaBean appInfo) {
        //是否包含待批申请
        String includePending = (String)appInfo.get("ztflag");
        includePending = (null == includePending || "".equals(includePending.trim())) ? "0" : includePending;
        return "1".equals(includePending);
    }
    
    private double getUsableHolidayDays(LazyDynaBean appInfo) throws Exception {
        double days = 0;
        double curYearDays = 0;
        double preYearDays = 0;
        
        final String KX_FLAG_CUR_YEAR = "1";
        final String KX_FLAG_PRE_YEAR = "2";
        
        
        try {
            String itemId = (String) appInfo.get("type");
            String nbase = (String) appInfo.get("nbase");
            String a0100 = (String) appInfo.get("a0100");
            //flag 0或null：按原逻辑取天数 1：取当年可休天数  2：取上年结余剩余天数
            String flag = (String)appInfo.get("kx_flag");
            flag = (flag == null) || "".equals(flag) ? "0" : flag;
            
            //28852 linbz 校验开始结束时间是否为null是否为Data类型
            if(!(appInfo.get("starttime") instanceof Date) && !(appInfo.get("endtime") instanceof Date)){
            	return days;
            }
            
            Date dStartTime = (Date) appInfo.get("starttime");
            Date dEndTime = (Date) appInfo.get("endtime");
            if (dEndTime == null) {
                dEndTime = dStartTime;
            }
            
            String startTime = DateUtils.format(dStartTime, "yyyy.MM.dd HH:mm");
            String endTime = DateUtils.format(dEndTime, "yyyy.MM.dd HH:mm");
            
            String b0110 = getB0110ByEmpInfo(nbase, a0100);

            AnnualApply annual = new AnnualApply(this.userView, this.conn);
            //不需要验证已休天数异常情况（负数）
            annual.setValidateUsedTimeLenError(false);

            HashMap kqItem = annual.count_Leave(itemId);
            String targetHolidayId = switchTypeIdFromHolidayMap(itemId);


            if (KX_FLAG_PRE_YEAR.equals(flag) || KX_FLAG_CUR_YEAR.equals(flag)) {
                curYearDays = annual.getCurYearCanUseDays(targetHolidayId, a0100, nbase, startTime, endTime, b0110, kqItem);
                preYearDays = annual.getPreYearCanUseDays(targetHolidayId, a0100, nbase, startTime, endTime, b0110, kqItem);
            } else {
                days = annual.getMy_Time(targetHolidayId, a0100, nbase, startTime, endTime, b0110, kqItem);
            }
            
            //包含在途时长（未批时长）
            if (includePendingTimeLen(appInfo)) {
                String q1501 = (String) appInfo.get("q1501");
                float[] history_rule = annual.getHoliday_minus_rule();
                if ("0".equals(flag)) {
                    days = days - annual.othenSealTime(
                            itemId, 
                            dStartTime, dEndTime, 
                            a0100, nbase, b0110, q1501, kqItem, "up","", 
                            history_rule);
                } else {
                    double curYearSealDays = annual.getHolidaySealTime(itemId, dStartTime, dEndTime, a0100, nbase, b0110, q1501,kqItem, "up", "", history_rule, "1");
                    double preYearSealDays = annual.getHolidaySealTime(itemId, dStartTime, dEndTime, a0100, nbase, b0110, q1501,kqItem, "up", "", history_rule, "2");
                    if (preYearSealDays > preYearDays) {
                        curYearSealDays = curYearSealDays + preYearSealDays - preYearDays;
                        preYearSealDays = preYearDays;
                    }
                    
                    if (KX_FLAG_CUR_YEAR.equals(flag)) {
                        days = curYearDays - curYearSealDays;
                    } else {
                        days = preYearDays - preYearSealDays;
                    }
                }
            } else {
                if (KX_FLAG_CUR_YEAR.equals(flag)) {
                    days = curYearDays;
                } else if (KX_FLAG_PRE_YEAR.equals(flag)) {
                    days = preYearDays;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return days;
    }

    /**
     * 取年假等假期已休天数（三种取数规则：当年总已休天数、当年已休天数、上年结余已休天数）
     * @param appInfo 申请单信息，包括申请类型type,人员信息nbase,a0100,申请时间starttime,取数规则yx_flag
     * @return 天数
     */
    private double getUsedHolidayDays(LazyDynaBean appInfo) {
        double days = 0;

        String itemId = (String) appInfo.get("type");
        String nbase = (String) appInfo.get("nbase");
        String a0100 = (String) appInfo.get("a0100");  
        
        // yx_flag: 0：当年总已休天数 1：当年已休天数 2：上年结余已休天数
        String yxFlag = (String) appInfo.get("yx_flag");
        yxFlag = StringUtils.isBlank(yxFlag) ? "0" : yxFlag;
        
        String targetHolidayId = switchTypeIdFromHolidayMap(itemId);
        
        // 当年已休天数指标
        String usedDaysFld =  Sql_switcher.isnull("Q1705", "0");
        String lastBalanceFld = PubFunc.DotstrNull(KqUtilsClass.getFieldByDesc("q17", "上年结余"));
        String lastSpareFld = PubFunc.DotstrNull(KqUtilsClass.getFieldByDesc("q17", "结余剩余"));
        
        // 除当年已休天数外，其它要判断上年结余情况
        if (!"1".equals(yxFlag) && !"".equals(lastBalanceFld) && !"".equals(lastSpareFld)) {
            // 上年结余已休
            if ("2".equals(yxFlag)) {
                usedDaysFld = Sql_switcher.isnull(lastBalanceFld, "0") + "-" + Sql_switcher.isnull(lastSpareFld, "0");
            } else // 总休天数=当年休+结余休(上年结余-结余剩余)
            {
                usedDaysFld = usedDaysFld + "+" + Sql_switcher.isnull(lastBalanceFld, "0") + "-" + Sql_switcher.isnull(lastSpareFld, "0");
            }
        }
        
        //28852 linbz 校验开始结束时间是否为null是否为Data类型
        if(!(appInfo.get("starttime") instanceof Date)){
        	return days;
        }
        //zxj Q17中假期开始结束日期指标中经常只有日期没有时间，所以改为只取日期
        String startTime = DateUtils.format((Date) appInfo.get("starttime"), "yyyy.MM.dd");

        StringBuffer sql = new StringBuffer();
        sql.append("select " + usedDaysFld + " from Q17");
        sql.append(" where nbase ='" + nbase);
        sql.append("' and a0100 ='" + a0100);
        sql.append("' and Q1709='" + targetHolidayId);
        sql.append("' and Q17Z1 <= " + Sql_switcher.dateValue(startTime));
        sql.append(" and Q17Z3 >= " + Sql_switcher.dateValue(startTime));

        RowSet rs = null;
        ContentDAO dao = new ContentDAO(conn);
        try {
            rs = dao.search(sql.toString());
            if (rs.next()) {
                days = rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }

        return days;
    }

    public String getB0110ByEmpInfo(String nbase, String a0100) {
        String b0110 = "";
        String sql = "SELECT B0110 FROM " + nbase + "A01 WHERE a0100=?";
        
        ArrayList<String> sqlParam = new ArrayList<String>();
        sqlParam.add(a0100);
        
        RowSet rs = null;
        try {
            rs = dao.search(sql, sqlParam);
            if (rs.next()) {
                b0110 = rs.getString("B0110");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }

        return b0110 == null ? "" : b0110;
    }

    /**
     * 检查申请人员是否在考勤人员库中
     * 
     * @param nbase
     *            <String> 人员库
     * @param a0100
     *            <String> 人员编号
     * @return <boolen> 是否需要考勤
     */
    private boolean needKq(String nbase, String a0100) {
        return true;
        /* 不再判断是否需要考勤，模板允许申请就放过即可
        boolean isOK = false;
        
        //如果是自助申请，不用判断
        if(nbase.equalsIgnoreCase(this.userView.getDbname()) && a0100.equalsIgnoreCase(this.userView.getA0100()))
            return true;

        String b0110 = getB0110ByEmpInfo(nbase, a0100);
        if (null == b0110 || "".equals(b0110))
            return isOK;
        
        KqUtilsClass kqUtilsClass = new KqUtilsClass(conn, userView);
        try {
            ArrayList nbases = kqUtilsClass.getKqPreList();
            for (int i = 0; i < nbases.size(); i++) {
                String aNbase = (String) nbases.get(i);
                isOK = aNbase.equalsIgnoreCase(nbase);

                if (isOK)
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isOK;
        */
    }

    private static int getOverTimeType(String overtimeTypeId) {
        int overtimeType = 0;

        try {
            overtimeType = Integer.parseInt(overtimeTypeId.substring(0, 2));
        } catch (Exception e) {
        }

        return overtimeType;
    }
    
    /*
     * 是否是平时加班
     */
    public static boolean isNormalOvertime(String overtimeTypeId) {
        int overtimeType = getOverTimeType(overtimeTypeId);
        return KqConstant.AppType.OVERTIME_IS_NORMAL == overtimeType;
    }
    
    /*
     * 是否是公休日加班
     */
    public static boolean isRestOvertime(String overtimeTypeId) {
        int overtimeType = getOverTimeType(overtimeTypeId);
        return KqConstant.AppType.OVERTIME_IS_REST == overtimeType;
    }
    
    /*
     * 是否是节假日加班
     */
    public static boolean isFeastOvertime(String overtimeTypeId) {
        int overtimeType = getOverTimeType(overtimeTypeId);
        return KqConstant.AppType.OVERTIME_IS_FEAST == overtimeType;
    }
  
    /**
     * @Title: switchTypeIdFromHolidayMap   
     * @Description: 将传入的请假类型转换为system参数中的假期映射关系中的目标假期类型   
     * @param @param leaveTypeId 需要转换的请假类型
     * @param @return 转换后的请假类型（如不在映射关系中，则返回原值）
     * @return String    
     * @throws
     */
    public static String switchTypeIdFromHolidayMap(String leaveTypeId) {
        String switchTypeId = leaveTypeId;
        
        String holidayMaps = SystemConfig.getPropertyValue("kq_holiday_map");
        holidayMaps = null == holidayMaps ? "" : holidayMaps.trim();
        if ("".equals(holidayMaps)) {
            return switchTypeId;
        }
        
        String[] holidayMap = holidayMaps.split(";");
        for (int i=0; i<holidayMap.length; i++) {
            String aMap = holidayMap[i].trim();
            
            if ("".equals(aMap)) {
                continue;
            }
            
            if (!aMap.startsWith("[") || !aMap.endsWith("]") || !aMap.contains(":")) {
                continue;
            }
            
            int pos = aMap.indexOf(":");
            String targetHolidayId = aMap.substring(1, pos);
            String srcHolidayIds = aMap.substring(pos+1, aMap.length()-1);
            
            if (("," + srcHolidayIds + ",").contains("," + leaveTypeId + ",")) {
                switchTypeId = targetHolidayId;
                break;
            }                
        }        
        
        return switchTypeId;
    }
    
    /*
     * 
     */
    public static String getMapTypeIdsFromHolidayMap(String holidayId) {
        String mapTypeIds = "'" + holidayId + "'";
        
        String holidayMaps = SystemConfig.getPropertyValue("kq_holiday_map");
        holidayMaps = null == holidayMaps ? "" : holidayMaps.trim();
        if ("".equals(holidayMaps)) {
            return mapTypeIds;
        }
        
        String[] holidayMap = holidayMaps.split(";");
        for (int i=0; i<holidayMap.length; i++) {
            String aMap = holidayMap[i].trim();
            
            if ("".equals(aMap)) {
                continue;
            }
            
            if (!aMap.startsWith("[") || !aMap.endsWith("]") || !aMap.contains(":")) {
                continue;
            }
            
            int pos = aMap.indexOf(":");
            String targetHolidayId = aMap.substring(1, pos);
            if (!holidayId.equals(targetHolidayId)) {
                continue;
            }
            
            String srcHolidayIds = aMap.substring(pos+1, aMap.length()-1);
            if ("".equals(srcHolidayIds)) {
                continue;
            }
            
            String[] srcIds = srcHolidayIds.split(",");
            for (int j=0; j<srcIds.length; j++) {
                String aSrcId = srcIds[j].trim();
                if ("".equals(aSrcId)) {
                    continue;
                }
                
                mapTypeIds = mapTypeIds + ",'" + aSrcId + "'"; 
            }
                
            break;
        }    
        
        return mapTypeIds;
    }
    
    private void logAppInfo(String templateId, String table, LazyDynaBean appInfo, String operState, String msg) {
        StringBuffer debugInfo = new StringBuffer();
        debugInfo.append(msg);
        debugInfo.append(" templateId=>").append(templateId);
        if (null != table && !"".equals(table)) {
            debugInfo.append(" appId=>").append(getAppIdByAppInfo(appInfo, table));
        }
        debugInfo.append(" state=>").append(operState);
        this.cat.debug(debugInfo.toString());
    }
    /**
     * 获取销假、销公出、销加班申请中被取消的单号
     * @param appInfo 申请单中信息
     * @param appTab 申请单对应的表
     * @return
     */
    private String getLeaveId(LazyDynaBean appInfo, String appTab) {
    	String leaveId = "";
    	RowSet rs = null;
    	try {
    		Date startDate = (Date) appInfo.get("QXJZ1");
    		if(startDate == null) {
                startDate = (Date) appInfo.get("qxjz1");
            }
    		
    		Date endDate = (Date) appInfo.get("QXJZ3");
    		if(endDate == null) {
                endDate = (Date) appInfo.get("qxjz3");
            }
    		
    		String nbase = (String) appInfo.get("nbase");
            String a0100 = (String) appInfo.get("a0100");
            ContentDAO dao = new ContentDAO(this.conn);
            StringBuffer sql = new StringBuffer();
            sql.append("select " + appTab + "01");
            sql.append(" from " + appTab);
            sql.append(" where a0100=? and nbase=? and " + appTab + "z1<?" );
            sql.append(" and " + appTab + "z3>?");
            sql.append(" and " + Sql_switcher.isnull(appTab + "19", "'hjsoft'") + "=?");
            ArrayList<Object> valuesList = new ArrayList<Object>();
            valuesList.add(a0100);
            valuesList.add(nbase);
            valuesList.add(endDate);
            valuesList.add(startDate);
            valuesList.add("hjsoft");
            rs = dao.search(sql.toString(), valuesList);
            if(rs.next()) {
                leaveId = rs.getString(appTab + "01");
            }
            
    	}catch (Exception e) {
    		e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
    	
		return leaveId;
    }
    
    /**
	 * 获取申请单的开始日期、结束日期等数据
	 * @param appInfo
	 * @param tableflag
	 * @return
	 */
    private HashMap getAppDates(LazyDynaBean appInfo,String tableflag) {
		HashMap map = new HashMap();
		try {
			Date kq_start = null;	
			Date kq_end = null;
			 
			Object start = appInfo.get(tableflag+"z1");
			if(start instanceof String) {
                kq_start =DateUtils.getDate(String.valueOf(start),"yyyy-MM-dd HH:mm");
            } else {
                kq_start = (Date)start;
            }
			
			Object end = appInfo.get(tableflag+"z3");
			if(end instanceof String) {
                kq_end =DateUtils.getDate(String.valueOf(end),"yyyy-MM-dd HH:mm");
            } else {
                kq_end = (Date)end;
            }
			 
			map.put("tableZ1", kq_start);
			map.put("tableZ3", kq_end);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return map;
	}
    
    /**
     * 审批过程中驳回操作同步到考勤表单
     * @param tabid			模板id
     * @param sqlApply		人事异动流程查询表单SQL
     * @throws GeneralException
     */
    public void synApproverRejectKqApp(String tabid, String sqlApply) throws GeneralException {
    	
    	RowSet rs = null;
    	try {
    		WF_Instance ins = new WF_Instance(Integer.parseInt(tabid),  conn, userView);
    		//q15~Q1501:A8115_2,Q1503:A8101_2,Q1505:A8102_2,Q15Z1:A8109_2,Q15Z3:A8110_2,Q1507:A8111_2
            String info = ins.getKqMappingInfo(tabid);
            String tableflag = "";
            if(info.indexOf("~") != -1 && !"~".equals(info)) {
            	tableflag = info.split("~")[0];
            }
            // 若对应业务标识为空 直接返回
            if(StringUtils.isEmpty(tableflag)) {
                return ;
            }
            
            String tabinfo = info.split("~")[1];
            String[] tabMapping = tabinfo.split(","); 
            // 单据单号对应的指标
            String tab01 = "";
            for(int i=0;i<tabMapping.length;i++) {
            	if(tabMapping[i].split(":")[0].equalsIgnoreCase(tableflag+"01")) {
                    tab01 = tabMapping[i].split(":")[1];
                }
            }
            String tab01Value = "";
            rs = dao.search(sqlApply);
            while(rs.next()){
            	tab01Value = rs.getString(tab01);
			}
            // 若单据单号为空 直接返回
            if(StringUtils.isEmpty(tab01Value)) {
                return ;
            }
            // 审批状态置为驳回、审批人为当前用户/*状态暂时不更新*/
    		StringBuffer sql = new StringBuffer("");
    		sql.append("update ").append(tableflag).append(" set ");
    		sql.append(tableflag).append("13='").append(this.userView.getUserFullName()).append("'");
    		sql.append(" where ").append(tableflag).append("01='").append(tab01Value).append("'");
    		
            dao.update(sql.toString());
    		
        } catch (Exception e) {
        	e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
			PubFunc.closeResource(rs);
		}
    }
}
