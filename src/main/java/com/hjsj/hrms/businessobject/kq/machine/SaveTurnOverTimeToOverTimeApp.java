package com.hjsj.hrms.businessobject.kq.machine;

import com.hjsj.hrms.businessobject.general.template.TemplateTableParamBo;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Instance;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.app_check_in.GetValiateEndDate;
import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.utils.OperateDate;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SaveTurnOverTimeToOverTimeApp {

	private Connection conn = null;
	private UserView userView = null;
	private String err_message = "";
	private String errorMess = "";//加班限额异常信息
	private String overtimeSrcTab = "";

	public SaveTurnOverTimeToOverTimeApp(Connection conn, UserView userView, String overtimeSrcTab) {
		this.conn = conn;
		this.userView = userView;
		this.overtimeSrcTab = overtimeSrcTab;
	}

	
	/**
	 * 保存 休息日转加班 到 加班申请
	 * @param list <ArrayList> 加班信息列表
	 * @param overtimeReason <加班事由> 加班事由
	 * @return
	 * @throws GeneralException 
	 */
	public int saveToOverTimeApp(ArrayList list, String overtimeReason, String templateId, String spState) throws GeneralException {
		int num = 0;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			
			StringBuffer sql = new StringBuffer();
			ArrayList insertList = new ArrayList();
			
			//有模板号并且有指标映射关系的就是需要走人事异动审批的数据
			ArrayList appInfos = null;
			String mappings = "";
			boolean haveBusiTemplateSetting = !"".equals(templateId) && !"-1".equals(templateId);
			if (haveBusiTemplateSetting) {
			    TemplateTableParamBo templateBo = new TemplateTableParamBo(Integer.parseInt(templateId), this.conn);
			    mappings = templateBo.getKq_field_mapping();
			    
			    haveBusiTemplateSetting = !"".equals(mappings);
			    
	    		if (haveBusiTemplateSetting) {
                    appInfos = new ArrayList();
                }
			}
			
			DbWizard dbw = new DbWizard(this.conn);
			String flagFld = "", flagValue = "";
			if (dbw.isExistField("Q11", "flag", false)) {
			    flagFld = ",flag";
			    flagValue = "2";
			}
			String existQ11xx = KqUtilsClass.getFieldByDesc("Q11", "休息扣除数");
			
			String overtimeForLeaveFld = "";
			//处理是否调休指标，如果有，应默认设为"否" 20131107 zxj 去掉默认值，hkyh要求必须由员工确认是否转调休
			//String overtimeForLeaveFld = KqUtilsClass.getFieldByDesc("Q11", "是否调休");
			//overtimeForLeaveFld = overtimeForLeaveFld == null ? "" : overtimeForLeaveFld;
			String overtimeForLeaveValue = "";
			//if (!"".equals(overtimeForLeaveFld)) {
			//    overtimeForLeaveValue = "2";
			//    overtimeForLeaveFld = "," + overtimeForLeaveFld;
			//}
			KqParam kqParam = KqParam.getInstance();
			float overtimeLen = 0;
			float apptimeLen = 0;
			String para = kqParam.getDURATION_OVERTIME_MAX_LIMIT();
	        if (para == null || para.length() <= 0) {
                para = "-1";
            }
	        int overtimeLimit = Float.valueOf(para).intValue();//加班时长限额
			String currA0100 = "";
			String nextA0100 = "";
			boolean isCorrect = true;
			
			ArrayList kqDuration = RegisterDate.getKqDayList(conn);
            Date kqStart = new Date();
            Date kqEnd = new Date();
        	if (kqDuration != null && kqDuration.size() > 0) 
			{
        		kqStart = OperateDate.strToDate(((String)kqDuration.get(0)).replace(".", "-") + " 00:00", "yyyy-MM-dd HH:mm");
        		kqEnd = OperateDate.strToDate(((String)kqDuration.get(1)).replace(".", "-") + " 23:59", "yyyy-MM-dd HH:mm");
			}
	        	
			for (int i = 0;i < list.size();i ++) {
				String oneRecord = (String) list.get(i);
				LazyDynaBean userBean = (LazyDynaBean) getOvertimeInfo(oneRecord, dao, overtimeSrcTab);//重新组合加班信息
				String A0100 = (String) userBean.get("a0100");// 人员编号
				currA0100 = A0100;
				if(i != list.size() -1) {
                    nextA0100 = (String) ((LazyDynaBean) getOvertimeInfo((String)list.get(i + 1), dao, overtimeSrcTab)).get("a0100");
                }
				String nbase = (String) userBean.get("nbase");// 人员库
				String a0101 = (String) userBean.get("a0101");// 人员姓名

				//2014.10.30 xxd通过判断取出的指标值是否为null进行存值
				String b0110 = null;// 单位
				if(!"".equals(userBean.get("b0110"))){
					b0110 =  (String) userBean.get("b0110");
				}
				String e0122 = null;// 部门
				if(!"".equals(userBean.get("e0122"))){
					e0122 = (String) userBean.get("e0122");
				}
				String e01a1 = null;// 岗位
				if(!"".equals(userBean.get("e01a1"))){
					e01a1 = (String) userBean.get("e01a1");
				}
				String overtime_type = (String) userBean.get("overtime_type");// 加班类型
				String begin_date = (String) userBean.get("begin_date");// 开始时间
				String end_date = (String) userBean.get("end_date");// 结束时间
				String state = (String) userBean.get("status");// 状态
				
	            
	            if ("03".equals(spState) && overtimeLimit > 0) //转加班的话，选择已批，检查考勤期间内的加班时长是否超过加班时间时长限额的限制
	            {
	            	String start_d = begin_date.replace(".", "-");
	            	String end_d = end_date.replace(".", "-");
	            	SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
                    Date startTime = sdf.parse(start_d);//申请开始日期
                    Date endTime = sdf.parse(end_d);//申请结束日期
                    
                    //加班限额累计的加班时长只累计当前考勤期间内的时长
                    if (startTime.getTime() < kqStart.getTime() && endTime.getTime() > kqStart.getTime()) 
    				{
    					startTime = kqStart;
    				}else if (startTime.getTime() < kqEnd.getTime() && endTime.getTime() > kqEnd.getTime()) 
    				{
    					endTime = kqEnd;
    				}
                    
	            	AnnualApply annualApply = new AnnualApply(userView, conn);
	            	float count = annualApply.getOvertimeLen(overtime_type, "", nbase, A0100, startTime, endTime);
	            	apptimeLen = apptimeLen + count;
	            	
	            	if (overtimeLen == 0 && (!nextA0100.equals(currA0100) || i == list.size() -1)) 
					{
	                    overtimeLen = annualApply.getKqdurationOverTimelen(nbase, A0100, "3");
	                    
					}
	                if (overtimeLen + apptimeLen > overtimeLimit && (!nextA0100.equals(currA0100) || i == list.size() -1)) {
	                	if(errorMess.length() == 0) {
                            errorMess += "无法将所选记录转为已批申请单，请重新选择！\r下列人员超出本期间允许加班" + PubFunc.round(""+overtimeLimit,2) + "小时的限制：\r";
                        }
	                	if (num == 9 && errorMess.length() > 0) {
	                		errorMess += "。。。。。。";
	                		break;
	                	} else {
                            errorMess += "已选" + a0101 + "的申请时长为" + PubFunc.round(""+apptimeLen,2) + "小时，" + "累计已有加班" + PubFunc.round(""+overtimeLen,2) + "小时；\r";
                        }
	                	num ++;
	                	isCorrect = false;
	                }
	                if (!nextA0100.equals(currA0100)) 
					{
	                	overtimeLen = 0;
	                	apptimeLen = 0;
					}
	            }
	            
				// 验证信息 是否已经在申请中存在
				GetValiateEndDate ve = new GetValiateEndDate(this.userView,this.conn);
				boolean isV = true;
				String msg1 = a0101 + "加班时段【" + begin_date + "~" + end_date + "】与";
				String msg2 = "记录冲突，无法申请！\n";
				if (ve.checkTimeX("q15", nbase, A0100, begin_date, end_date)) {
					err_message += msg1 + "请假" + msg2;
					isV = false;
					isCorrect = false;
				}
				
				if (ve.checkTimeX("q11", nbase, A0100, begin_date, end_date)) {
					err_message += msg1 + "其它加班" + msg2;
					isV = false;
					isCorrect = false;
				}
				
				//公出期间不允许请假加班时，要检查是否与公出申请冲突
				if (!"1".equals(kqParam.getOFFICELEAVE_ENABLE_LEAVE_OVERTIME()) 
				        && ve.checkTimeX("q13", nbase, A0100, begin_date, end_date)) {
					err_message += msg1 + "公出" + msg2;
					isV = false;
					isCorrect = false;
				}
				
				if (isV && isCorrect) {
					if (haveBusiTemplateSetting) {
                        addAppInfoToList(mappings, userBean, appInfos);
                    } else {
						ArrayList oneApp = new ArrayList(25);
						oneApp.add(checkAppkeyid());
						oneApp.add(nbase);
						oneApp.add(A0100);
						oneApp.add(a0101);
						oneApp.add(b0110);
						oneApp.add(e0122);
						oneApp.add(e01a1);
						oneApp.add(new java.sql.Timestamp(new Date().getTime()));
						oneApp.add(new java.sql.Timestamp(OperateDate.strToDate(begin_date, "yyyy-MM-dd HH:mm").getTime()));
						oneApp.add(new java.sql.Timestamp(OperateDate.strToDate(end_date, "yyyy-MM-dd HH:mm").getTime()));
						oneApp.add(new java.sql.Timestamp(new Date().getTime()));
						oneApp.add(state);
						oneApp.add(overtime_type);
						oneApp.add(overtimeReason);
						oneApp.add(spState);
						oneApp.add("01");
						if(flagFld != null && flagFld.length() > 0) {
                            oneApp.add(flagValue);
                        }
						if(overtimeForLeaveFld != null && overtimeForLeaveFld.length() > 0) {
                            oneApp.add(overtimeForLeaveValue);
                        }
						if (existQ11xx != null && existQ11xx.length() > 0) {
                            oneApp.add(String.valueOf(this.GetRestTimelen(A0100, nbase, begin_date, end_date)));
                        }
						insertList.add(oneApp);
						
						delAppInfoFromTab(dao, overtimeSrcTab, nbase, A0100, begin_date, end_date);//删除数据处理表里的记录
					}
				}
			}
			
			if (isCorrect) 
			{
				//拼sql语句
				sql.setLength(0);
				sql.append("INSERT INTO Q11(q1101,nbase,A0100,a0101,b0110,e0122,e01a1");
				sql.append(",q1105,q11z1,q11z3,q11z7,state,q1103,q1107,q11z5,q11z0");
				sql.append(flagFld);
				sql.append(overtimeForLeaveFld);
				if(existQ11xx != null && existQ11xx.length() > 0) {
                    sql.append("," + existQ11xx);
                }
				sql.append(")");
				sql.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?");
				if(flagFld != null && flagFld.length() > 0) {
                    sql.append(",?");
                }
				if(overtimeForLeaveFld != null && overtimeForLeaveFld.length() > 0) {
                    sql.append(",?");
                }
				if (existQ11xx != null && existQ11xx.length() > 0) {
                    sql.append(",?");
                }
				sql.append(")");
				
				dao.batchInsert(sql.toString(), insertList);//将申请记录批量插入
			}
			
			if (haveBusiTemplateSetting && appInfos.size() > 0){
			    try {
			        syncAppInfoToYdTemplate(dao, overtimeSrcTab, templateId, appInfos);
			    } catch (GeneralException e) {
			        err_message += e.getErrorDescription();
			        num = 0;
	            } catch (Exception e) {
	                e.printStackTrace();
	                err_message += e.getMessage();
	                num = 0;
	            }
			}
			
		} catch (GeneralException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return num;
		
	}
	
	private boolean delAppInfoFromTab (ContentDAO dao, String table, String nbase, String a0100, String begin_date, String end_date) {
        boolean isOK = false;

        try {
            StringBuilder sql = new StringBuilder();
            sql.append("DELETE FROM " + overtimeSrcTab);
            sql.append(" WHERE nbase='" + nbase);
            sql.append("' AND a0100='" + a0100);
            sql.append("' AND begin_date=" + Sql_switcher.dateValue(begin_date));
            sql.append(" AND end_date=" + Sql_switcher.dateValue(end_date));
            dao.update(sql.toString());
            
            isOK = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isOK;
    }
	
	private void syncAppInfoToYdTemplate(ContentDAO dao, String table, String templateId, ArrayList appInfos)
	   throws GeneralException{
	    if (appInfos == null || appInfos.size()==0) {
            return;
        }
	    
        ArrayList syncAppInfos = new ArrayList();
        String nbase = "", a0100 = "", preNbase = "", preA0100 = "";
        
        while (appInfos.size() > 0) {
            //筛选本批同步到异动中的数据（一人一条）
            for (int i = 0; i < appInfos.size(); i++) {
                LazyDynaBean appBean = (LazyDynaBean)appInfos.get(i);
                nbase = (String)appBean.get("nbase");
                a0100 = (String)appBean.get("a0100");
                
                if (!nbase.equals(preNbase) || !a0100.equals(preA0100)){
                    syncAppInfos.add(appBean);
                }
                
                preA0100 = a0100;
                preNbase = nbase;
            }
            
            //同步到异动中
            if (syncAppInfos.size() > 0){
                WF_Instance wfInstance = new WF_Instance(Integer.parseInt(templateId), this.conn, this.userView);
                wfInstance.syncAppInfoToTemplateTab(templateId, syncAppInfos);                   
            }
            
            //移除已同步数据
            int appInfoCount = syncAppInfos.size();
            for (int i = appInfoCount - 1; i >= 0 ; i--){
                LazyDynaBean aBean = (LazyDynaBean)syncAppInfos.get(i);
                delAppInfoFromTab(dao, table, 
                                  (String)aBean.get("nbase"), 
                                  (String)aBean.get("a0100"), 
                                  OperateDate.dateToStr((Date)aBean.get("q11z1"), "yyyy-MM-dd HH:mm"), 
                                  OperateDate.dateToStr((Date)aBean.get("q11z3"), "yyyy-MM-dd HH:mm"));
                appInfos.remove(aBean);
                syncAppInfos.remove(i);
            }
        }
    }
	
	private void addAppInfoToList(String mappings, LazyDynaBean overtimeBean, ArrayList appInfoList) {
        LazyDynaBean appBean = new LazyDynaBean();
        appBean.set("nbase", overtimeBean.get("nbase"));
        appBean.set("a0100", overtimeBean.get("a0100"));
        appBean.set("a0101", overtimeBean.get("a0101"));
        appBean.set("b0110", overtimeBean.get("b0110"));
        appBean.set("e0122", overtimeBean.get("e0122"));
        appBean.set("e01a1", overtimeBean.get("e01a1"));
        
        String[] busiMapping = mappings.split(",");
        for (int i=0; i<busiMapping.length; i++){
            String aMapping = busiMapping[i];
            int pos = aMapping.indexOf(":");
            String kqItemId = aMapping.substring(0, pos).toLowerCase();
            
            if ("q1103".equals(kqItemId)) {
                appBean.set(kqItemId, overtimeBean.get("overtime_type"));
            } else if ("q11z1".equals(kqItemId)){
                String strDate = ((String)overtimeBean.get("begin_date")).replaceAll("\\.", "-");
                Date q11z1 = OperateDate.strToDate(strDate, "yyyy-MM-dd HH:mm");
                appBean.set(kqItemId, q11z1);
            }
            else if ("q11z3".equals(kqItemId)){
                String strDate = ((String)overtimeBean.get("end_date")).replaceAll("\\.", "-");
                Date q11z3 = OperateDate.strToDate(strDate, "yyyy-MM-dd HH:mm");
                appBean.set(kqItemId, q11z3);
            }
        }
        
        appInfoList.add(appBean);
    }

	/**
	 * 生产最大标识
	 * @return
	 */
	private String checkAppkeyid() {
		IDGenerator idg = new IDGenerator(2, this.conn);
		String insertid = "";
		try {
			boolean iscorrect = false;
			while (!iscorrect) {
				insertid = idg.getId(("q11.q1101").toUpperCase());
				iscorrect = checkAppkeyid2(insertid);
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		return insertid;
	}

	/**
	 * 验证这个标识是否 存在
	 * @param id
	 * @return
	 */
	private boolean checkAppkeyid2(String id) {
		boolean iscorrect = true;
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			String sql = "select 1 from q11 where q1101='" + id + "'";
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
	private int GetRestTimelen(String a0100,String nbase,String begin_date,String end_date){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT q11xx FROM " + overtimeSrcTab);
		sql.append(" WHERE a0100 = '" + a0100 + "'");
		sql.append(" AND nbase = '" + nbase + "'");
        sql.append(" AND begin_date=" + Sql_switcher.dateValue(begin_date));
        sql.append(" AND end_date=" + Sql_switcher.dateValue(end_date));
        
        ContentDAO dao = new ContentDAO(conn);
        ResultSet rs = null;
        int len = 0;
        try {
			rs = dao.search(sql.toString());
			if (rs.next()) 
			{
				len = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		    KqUtilsClass.closeDBResource(rs);
		}
		return len;
	}
	
	/**
	 * 根据唯一标识重新组合一个LazyDynaBean
	 * @param oneRecord
	 * @param dao
	 * @param table
	 * @return
	 */
	private LazyDynaBean getOvertimeInfo(String oneRecord, ContentDAO dao, String table){
		LazyDynaBean lBean = new LazyDynaBean();
		
		String [] list = oneRecord.split("`");
		String nbase = list[0];
		String a0100 = list[1];
		String start = list[2];
		String end = list[3];
		
		StringBuffer sql = new StringBuffer();
		sql.append("select * from " + table);
		sql.append(" where a0100 = '" + a0100 + "'");
		sql.append(" and nbase = '" + nbase + "'");
		sql.append(" and begin_date = " + Sql_switcher.dateValue(start));
		sql.append(" and end_date = " + Sql_switcher.dateValue(end));
		
		ResultSet rs = null;
		try {
			rs = dao.search(sql.toString());
			if (rs.next()) 
			{
				lBean.set("nbase", nbase);
				lBean.set("a0100", a0100);
				lBean.set("a0101", rs.getString("a0101"));
				lBean.set("b0110", rs.getString("b0110")==null?"":rs.getString("b0110"));
				lBean.set("e0122", rs.getString("e0122")==null?"":rs.getString("e0122"));
				lBean.set("e01a1", rs.getString("e01a1")==null?"":rs.getString("e01a1"));
				lBean.set("overtime_type", rs.getString("overtime_type"));
				lBean.set("begin_date", start);
				lBean.set("end_date", end);
				lBean.set("status", rs.getString("status"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			KqUtilsClass.closeDBResource(rs);
		}
		
		return lBean;
	}
	
	public String getErr_message() {
		return err_message;
	}

	public void setErr_message(String err_message) {
		this.err_message = err_message;
	}

	public String getErrorMess() {
		return errorMess;
	}

	public void setErrorMess(String errorMess) {
		this.errorMess = errorMess;
	}
	
}
