package com.hjsj.hrms.transaction.workplan;

import com.hjsj.hrms.businessobject.workplan.WorkPlanBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanCommunicationBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanConstant;
import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.businessobject.workplan.plan_task.ExportWorkPlanExcelBo;
import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskBo;
import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskTreeTableBo;
import com.hjsj.hrms.module.workplan.config.businessobject.WorkPlanConfigBo;
import com.hjsj.hrms.module.workplan.config.businessobject.WorkPlanFunctionBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONArray;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.*;

@SuppressWarnings("all")
public class WorkPlanMainTrans extends IBusiness {
	private String oprType; // 操作类型
	private String objectid; // 团队id 人员：库+人员编号
	private String p0723; //
	private String p0700; //
	private String periodType;// 期间类型
	private String periodYear;// 年
	private String periodMonth;// 月 根据期间类型不同代码月份、季度、上半年
	private String periodWeek;// 周
	private String deptLeader; // 团队计划 负责人
	private String curjsp; // 当前操作的jsp页面 区分 查看个人界面及团队界面
	private String needRefresh; // 前台请求是否需要刷新人力地图
	// ---团队成员及我关注的
	String concerned_bteam;
	String concerned_cur_page;
	String subobjectid;// 当前显示的 团队上级
	String subPersonFlag;
	String fromflag;
	private String planType;	//查看计划类型 haosl 20161128
	public void execute() throws GeneralException {
		try {
			HashMap hm = this.getFormHM();
			oprType = (String) hm.get("oprType");// 初始jsp时退出
			needRefresh = (String) hm.get("needRefresh");
			if ((oprType == null) || ("".equals(oprType)))
				return;
			// 初始化参数
			initPublicParam(hm);
			// 判断业务用户是否能登陆
			String ishr = (String) hm.get("ishr");// hr入口
			if (("0".equals(p0700) || "".equals(p0700)) && "".equals(objectid)
					&& "".equals(this.userView.getA0100())
					&& !"1".equals(ishr)
					&& !"hr_create".equals(fromflag)) {
				throw new Exception("非自助用户不能使用该功能！");
			}

			if (isDealedInitPlan(hm, oprType)) {// 初始计划、刷新计划
				return;
			}
			if (isDealedDropDownMenuList(hm, oprType)) {// 处理下拉列表
				return;
			}
			if (isDealedTaskInfo(hm, oprType)) {// 处理任务相关，新增、删除任务、更新权重
				return;
			}
			if (isDealedPlanInfo(hm, oprType)) {// 处理计划相关、发布计划、批准、退回
				return;
			}
			if (isDealedFollower(hm, oprType)) {// 下拉、新增、删除关注人
				return;
			}
			if (isDealedParticipant(hm, oprType)) {// 下拉、新增负责人
				return;
			}

			if ("getConcerneders".equals(oprType)) {// 我关注的 团队人员 我的下属部门
				WorkPlanBo planBo = new WorkPlanBo(this.frameconn,
						this.userView);
				String needSeeSub = (String) hm.get("needSeeSub");
				if(StringUtils.isNotBlank(needSeeSub)){
					planBo.setNeedSeeSub(needSeeSub);
				}
				planBo.setDeptLeaderId(deptLeader);
				planBo.setCurJsp(curjsp);
				planBo.setSubObjectId(subobjectid);
				planBo.setSubPersonFlag(subPersonFlag);
				planBo.setHumanMapType(concerned_bteam);
				planBo.setHumanMap_cur_page(concerned_cur_page);
				planBo.setPlanType(this.planType);
				planBo.initPlan(objectid, p0723, periodType, periodYear,
						periodMonth, periodWeek);
				String info = planBo.getHumanMap(false);
				info = "{" + info + "}";
				info = "{" + WorkPlanUtil.quotedDoubleValue("planinfo") + ":"
						+ info + "}";
				info = SafeCode.encode(info);
				hm.put("info", info);
			} else if ("getMyFirstDept".equals(oprType)) {// 获取我负责的第一个部门
				WorkPlanUtil planUtil = new WorkPlanUtil(this.frameconn,
						this.userView);
				ArrayList deptlist = planUtil.getDeptList(this.userView
						.getDbname(), this.userView.getA0100());
				String b0110 = "";
				if (deptlist.size() > 0) {
					b0110 = (String) ((LazyDynaBean) (deptlist.get(0)))
							.get("b0110");
				}
				hm.put("p0723", SafeCode.encode(WorkPlanUtil.encryption("2")));
				hm.put("objectid", SafeCode.encode(WorkPlanUtil
						.encryption(b0110)));
				hm.put("dept_leader", SafeCode.encode(WorkPlanUtil
						.encryption(this.userView.getDbname()
								+ this.userView.getA0100())));
			} else if ("updatePlanScope".equals(oprType)) {// 更新可见范围
				String plan_scope = (String) hm.get("plan_scope");
				String plan_id = (String) hm.get("plan_id");
				plan_id = WorkPlanUtil.decryption(plan_id);
				WorkPlanBo planBo = new WorkPlanBo(this.frameconn,
						this.userView);
				planBo.updatePlanScope(plan_id, plan_scope);
			} else if ("remindTeam".equals(oprType)) {// 提醒制定计划
				WorkPlanBo planBo = new WorkPlanBo(this.frameconn,
						this.userView);
				if ("".equals(objectid)) {
					p0723 = ("2".equals(concerned_bteam)) ? "1" : "2";
					planBo.setSubPersonFlag(subPersonFlag);
					planBo.remindMyTeamToSubmitPlan(p0723, periodType,
							periodYear, periodMonth, periodWeek, subobjectid);
				} else {
					if ("".equals(p0723)) {
						p0723 = "1";
					}
					planBo.remindSubmitPlan(p0723, periodType, periodYear,
							periodMonth, periodWeek, objectid, "0");
				}
				String info = "true";
				hm.put("info", info);
			}
			else if("getWeekNum".equals(oprType)) {//获得选中月份有多少周
            	WorkPlanUtil workPlanUtil = new WorkPlanUtil(this.frameconn,this.userView);
            	int weeknum = workPlanUtil.getWeekNum(periodYear, periodMonth);
            	hm.put("weeknum", weeknum);
            }else if("validPreNow".equals(oprType)) {//校验填报期限
            	int validPre = Integer.valueOf((String)hm.get("validPre"));
            	int validNow = Integer.valueOf((String)hm.get("validNow"));
            	WorkPlanBo planBo = new WorkPlanBo(this.frameconn,this.userView);
            	boolean fillPlan = planBo.validPreNow(periodType,periodYear,periodMonth,periodWeek,validPre,validNow);
            	hm.put("fillPlan", fillPlan);
            }
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

	/**
	 * @Title: initPublicParam
	 * @Description: 初始公用参数
	 * @param @param hm
	 * @param @return
	 * @param @throws Exception
	 * @return boolean
	 * @author:wangrd
	 * @throws
	 */
	private boolean initPublicParam(HashMap hm) throws Exception {
		boolean b = false;
		try {
			//计划类型
			planType = WorkPlanUtil.nvl(hm.get("planType"), "");
			
			// 计划期间
			periodType = WorkPlanUtil.nvl(hm.get("periodType"), "");
			periodYear = WorkPlanUtil.nvl(hm.get("periodYear"), "");
			periodMonth = WorkPlanUtil.nvl(hm.get("periodMonth"), "");
			periodWeek = WorkPlanUtil.nvl(hm.get("periodWeek"), "");

			p0723 = WorkPlanUtil.nvl( hm.get("p0723"), "");
			p0723 = "undefined".equals(p0723) ? "" : p0723;
			p0723 = WorkPlanUtil.decryption(p0723);

			// 计划所有人
			objectid = WorkPlanUtil.nvl(hm.get("objectId"), "");
			objectid = WorkPlanUtil.decryption(objectid);
			if ("".equals(objectid)) {// 我的计划
				p0723 = "";
			}
			// 部门计划的负责人 如不传 则随机取第一个
			deptLeader = WorkPlanUtil.nvl(hm.get("deptLeader"), "");
			deptLeader = WorkPlanUtil.decryption(deptLeader);

			// 计划id 如果传计划id 计划期间可以不传
			p0700 = WorkPlanUtil.nvl(hm.get("p0700"), "");
			p0700 = "undefined".equals(p0700) ? "" : p0700;
			p0700 = WorkPlanUtil.decryption(p0700);
			if ("".equals(p0700)) {
				p0700 = "0";
			}

			curjsp = (String) hm.get("curjsp");
			curjsp = (curjsp == null) ? "selfplan" : curjsp;
			// ---团队成员及我关注的
			concerned_bteam = (String) hm.get("concerned_bteam");// 是否团队、
			concerned_bteam = (concerned_bteam == null) ? "" : concerned_bteam;
			concerned_cur_page = (String) hm.get("concerned_cur_page");
			concerned_cur_page = (concerned_cur_page == null) ? "1"
					: concerned_cur_page;
			subobjectid = (String) hm.get("subobjectid");// 当前显示的 团队上级
			subobjectid = (subobjectid == null) ? "" : subobjectid;
			subobjectid = WorkPlanUtil.decryption(subobjectid);

			subPersonFlag = (String) hm.get("subpersonflag");// 当前显示的 团队上级
			subPersonFlag = (subPersonFlag == null) ? "true" : subPersonFlag;

			// ------------
			fromflag=WorkPlanUtil.nvl(hm.get("fromflag"), ""); //来自哪个模块or功能 hr_create:工作计划制定
 
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return b;

	}

	/**
	 * @Title: isDealedInitPlan
	 * @Description: 初始计划、刷新计划
	 * @param @param hm
	 * @param @param oprType
	 * @param @return
	 * @param @throws Exception
	 * @return boolean
	 * @author:wangrd
	 * @throws
	 */
	private boolean isDealedInitPlan(HashMap hm, String oprType)
			throws Exception {
		WorkPlanUtil wpUtil = new WorkPlanUtil(frameconn, userView);
		boolean b = false;
		try {
			if ("getPlanInfo".equals(oprType)) {// 初始进入计划界面
				String needCheck = (String) hm.get("needcheck");// 检查是否有权限关注别人的计划
																// ？
				needCheck = (needCheck == null) ? "false" : needCheck;

				WorkPlanBo planBo = new WorkPlanBo(this.frameconn,
						this.userView);
				planBo.setDeptLeaderId(deptLeader);
				planBo.setSubPersonFlag(subPersonFlag);
				planBo.setSubObjectId(subobjectid);
				planBo.setHumanMapType(concerned_bteam);
				planBo.setHumanMap_cur_page(concerned_cur_page);
				planBo.setCurJsp(curjsp);
				planBo.setFromflag(fromflag);
				planBo.setPlanType(this.planType);//计划的类型（个人|部门）
				WorkPlanFunctionBo funcBo = new WorkPlanFunctionBo(this.frameconn, this.userView);
				List<HashMap<String, HashMap<String, String>>> configList = funcBo.getXmlData();
				if ("".equals(periodType)) { // 初始计划
					String cookie_periodType = (String) hm.get("cookie_periodType");
					if (Integer.parseInt(p0700) > 0) {
						planBo.initPlan(Integer.parseInt(p0700));
					} else {
						//判断cookie是否符合规则,如果不符合,置为null
						if ("1".equals(cookie_periodType)
								|| "2".equals(cookie_periodType)
								|| "3".equals(cookie_periodType)
								|| "4".equals(cookie_periodType)
								|| "5".equals(cookie_periodType)) {
							//如果cookie 中的计划类型未启用 ，置为null haosl
							 boolean flag = false;
							 for(HashMap<String, HashMap<String, String>> map : configList){
								if(map.containsKey("p"+(Integer.parseInt(cookie_periodType)-1))) {
									flag = true;
									break;
								}
							 }
							 if(!flag) {
								 cookie_periodType = null;
							 }
						}else{
							cookie_periodType = null;
						}
						planBo.initPlan(cookie_periodType);
					}
					
					// 显示当前默认的负责人
					if (!"".equals(objectid)) {
						String defaultUserId = "";
						if ("1".equals(p0723)) {
							defaultUserId = objectid;
						} else if ("2".equals(p0723)) {
							defaultUserId = wpUtil
									.getFirstDeptLeaders(objectid);
						}
						if (!"".equals(defaultUserId)) {// 防止部门不存在负责人的情况
							formHM.put("defaultName", wpUtil.getUsrA0101(
									defaultUserId.substring(0, 3),
									defaultUserId.substring(3)));
						}
					} else {
						formHM.put("defaultName", this.userView
								.getUserFullName());
					}
				} else {
					if (p0723 == null || p0723.length() < 1) {
						if("person".equals(planType)){
							objectid = this.userView.getDbname()+ this.userView.getA0100();
							p0723 = "1";
						}else{
							WorkPlanUtil workPlanUtil = new WorkPlanUtil(this.frameconn,this.userView);
							ArrayList deptlist =workPlanUtil.getDeptList(this.userView.getDbname(), 
			                         this.userView.getA0100());
			                String b0110 ="";
			                if (deptlist.size()>0){                   
			                    b0110 = (String) ((LazyDynaBean)(deptlist.get(0))).get("b0110");
			                    this.objectid= b0110;
			                    this.p0723="2";
			                } 
						}
					}
					planBo.initPlan(objectid, p0723, periodType, periodYear,
							periodMonth, periodWeek);
					// 显示当前默认的负责人
					if (!"".equals(objectid)) {
						String defaultUserId = "";
						if ("1".equals(p0723)) {
							defaultUserId = objectid;
						} else if ("2".equals(p0723)) {
							defaultUserId = wpUtil
									.getFirstDeptLeaders(objectid);
						}
						if (!"".equals(defaultUserId)) {// 防止部门不存在负责人的情况
							formHM.put("defaultName", wpUtil.getUsrA0101(
									defaultUserId.substring(0, 3),
									defaultUserId.substring(3)));
						}
					} else {
						formHM.put("defaultName", this.userView
								.getUserFullName());
					}
				}
				if ("true".equals(needCheck)) {

					if (!planBo.checkIsCanReadPlan()) {// 不能查看别人的计划
						planBo.initPlan(this.userView.getDbname()
								+ this.userView.getA0100(), "1", periodType,
								periodYear, periodMonth, periodWeek);
					}
				}
				String info = "false";
				if ("false".equals(planBo.getReturnInfo())) { // 初始有错
					info = "false";
				} else {
					info = planBo.getPlanInfoList(true, needRefresh);
				}
				info = SafeCode.encode(info);
				hm.put("info", info);
				// 填报期间范围权限 chent 20170112 start
	            hm.put("plan_cycle_function", SafeCode.encode(JSONArray.fromObject(configList).toString()));
	            // 填报期间范围权限 chent 20170112 end
	            //linbz  增加个人填写权限明细
	            String nbase = "";
	            String a0100 = "";
	            if("person".equals(this.planType) && !StringUtils.isEmpty(objectid)) {
	            	nbase = objectid.substring(0, 3);
	            	a0100 = objectid.substring(3);
	            }else if("org".equals(this.planType) && !StringUtils.isEmpty(deptLeader)) {
	            	nbase = deptLeader.substring(0, 3);
	            	a0100 = deptLeader.substring(3);
	            }else {
	            	nbase = this.userView.getDbname();
	            	a0100 = this.userView.getA0100();
	            }
	    		if(StringUtils.isNotBlank(nbase) && StringUtils.isNotBlank(a0100)){
	    			WorkPlanConfigBo configBo = new WorkPlanConfigBo(this.frameconn, this.userView);
	    			HashMap myFuncMap = configBo.getPersonFillType(this.userView.getDbname(), this.userView.getA0100());
	    			HashMap personFunctions = configBo.getPersonFunctions(myFuncMap, configList);
	    			List<HashMap<String, HashMap<String, String>>> personConfigList = (List<HashMap<String, HashMap<String, String>>>) personFunctions.get("person");
					List<HashMap<String, HashMap<String, String>>> orgConfigList = (List<HashMap<String, HashMap<String, String>>>) personFunctions.get("org");
	    			
	    			hm.put("person_cycle_function", SafeCode.encode(JSONArray.fromObject(personConfigList).toString()));
	    			hm.put("org_cycle_function", SafeCode.encode(JSONArray.fromObject(orgConfigList).toString()));
	    		}
	            
				b = true;
			} else if ("refreshPlanInfo".equals(oprType)) { // 刷新计划
				WorkPlanBo planBo = new WorkPlanBo(this.frameconn,
						this.userView);
				planBo.setCurJsp(curjsp);
				planBo.setDeptLeaderId(deptLeader);
				planBo.setSubObjectId(subobjectid);
				planBo.setSubPersonFlag(subPersonFlag);
				planBo.setHumanMapType(concerned_bteam);
				planBo.setHumanMap_cur_page(concerned_cur_page);
				planBo.setFromflag(fromflag);
				planBo.setPlanType(this.planType);//haosl 20161128
				if("".equals(objectid)){// 个人 haosl 20161128
					
					if("person".equals(this.planType)){
						objectid = this.userView.getDbname()+ this.userView.getA0100();
						p0723 = "1";
					}else if("org".equals(this.planType)){
						/**
						 * 这里增加判断当前登录人是不是真正部门领导，如果不是则导向个人计划页面
						 * 原因：之前能进入部门计划的肯定是部门领导，现在部门计划的菜单放开了，如果不是部门领导，也可以
						 * 进部门页面。  
						 * haosl 2018-3-22
						 */
						List deptlist = wpUtil.getDeptList(this.userView.getDbname(),this.userView.getA0100());
						if(deptlist.size()>0) {//是部门领导
							objectid = (String) ((LazyDynaBean) (deptlist.get(0)))
									.get("b0110");
							p0723 = "2";
						}else {//不是领导，导向个人计划页面
							objectid = this.userView.getDbname()+ this.userView.getA0100();
							p0723 = "1";
						}
					}
				}
				planBo.initPlan(objectid, p0723, periodType, periodYear,
						periodMonth, periodWeek);
				// 显示默认的负责人
				String defaultUserId = "";
				if (!"".equals(objectid)) {
					if ("1".equals(p0723)) {
						defaultUserId = objectid;
					} else if ("2".equals(p0723)) {
						defaultUserId = wpUtil.getFirstDeptLeaders(objectid);
					}
					if (!"".equals(defaultUserId)
							|| defaultUserId.trim().length() != 0) {
						formHM.put("defaultName", wpUtil.getUsrA0101(
								defaultUserId.substring(0, 3), defaultUserId
										.substring(3)));
					} else {
						formHM.put("defaultName", "无负责人");
					}
				} else {
					formHM.put("defaultName", this.userView.getUserFullName());
				}
				String bRefHummanMap = (String) hm.get("bRefHummanMap");// 是否刷新右侧人力地图
				boolean bhavePublicInfo = false;
				if ("true".equals(bRefHummanMap))
					bhavePublicInfo = true;

				String info = planBo.getPlanInfoList(bhavePublicInfo,
						needRefresh);
				info = SafeCode.encode(info);
				hm.put("info", info);
				
				// 从人力地图点击头像刷新计划时，查询计划人的填报范围 chent 20180329 add start
				if(StringUtils.isNotEmpty(defaultUserId)){
					String nBase = defaultUserId.substring(0, 3);
					String A0100 = defaultUserId.substring(3);
	    			WorkPlanConfigBo configBo = new WorkPlanConfigBo(this.frameconn, this.userView);
	    			HashMap myFuncMap = configBo.getPersonFillType(nBase, A0100);
	    			WorkPlanFunctionBo funcBo = new WorkPlanFunctionBo(this.frameconn, this.userView);
	    			List<HashMap<String, HashMap<String, String>>> configList = funcBo.getXmlData();
	    			HashMap personFunctions = configBo.getPersonFunctions(myFuncMap, configList);
	    			List<HashMap<String, HashMap<String, String>>> personConfigList = (List<HashMap<String, HashMap<String, String>>>) personFunctions.get("person");
					List<HashMap<String, HashMap<String, String>>> orgConfigList = (List<HashMap<String, HashMap<String, String>>>) personFunctions.get("org");
	    			
	    			hm.put("person_cycle_function", SafeCode.encode(JSONArray.fromObject(personConfigList).toString()));
	    			hm.put("org_cycle_function", SafeCode.encode(JSONArray.fromObject(orgConfigList).toString()));
	    		}
				// 从人力地图点击头像刷新计划时，查询计划人的填报范围 chent 20180329 add end
				b = true;
			}

			else if ("checkIsCanReadPlan".equals(oprType)) {// 切换计划期间时
															// ，检查是否有权限查看
				String fromflag=WorkPlanUtil.nvl((String)hm.get("fromflag"),""); //email:邮件进入，hr:监控进入，hr_create:工作计划制定
				String locationCurWeek = (String) hm.get("locationCurWeek");
				locationCurWeek = (locationCurWeek == null) ? "false"
						: locationCurWeek;
				WorkPlanBo planBo = new WorkPlanBo(this.frameconn,
						this.userView);
				if ("true".equals(locationCurWeek)) {
					planBo.setCurJsp(curjsp);
					planBo.LocationCurPeriodPlan(objectid, p0723, periodType,
							periodYear, periodMonth);
					hm.put("periodType", planBo.getPeriodType());
					hm.put("periodYear", planBo.getPeriodYear());
					hm.put("periodMonth", planBo.getPeriodMonth());
					hm.put("periodWeek", planBo.getPeriodWeek());
					hm.put("weekNum", planBo.getWeekNum() + "");

				} else {
					planBo.initPlan(objectid, p0723, periodType, periodYear,
							periodMonth, periodWeek);
				}
				planBo.setDeptLeaderId(deptLeader);
				String info = "true";
				//工作监控界面的不检查权限  haosl update 2017-07-12
				if (!"hr".equals(fromflag) && !planBo.checkIsCanReadPlan() && !"hr_create".equals(fromflag)) {
					info = "false";
					hm.put("failInfo", planBo.getReturnInfo());
				}
				hm.put("info", info);

			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return b;

	}

	/**
	 * @Title: isDealedDropDownMenuList
	 * @Description: 处理下拉列表的情况
	 * @param @param hm
	 * @param @param oprType
	 * @param @return
	 * @return boolean
	 * @author:wangrd
	 * @throws
	 */
	private boolean isDealedDropDownMenuList(HashMap hm, String oprType)
			throws Exception {// 处理下拉列表
		boolean b = false;
		try {
			if ("dropdownPeriodType".equals(oprType)) { // 期间类型下拉列表
				WorkPlanUtil planUtil = new WorkPlanUtil(this.frameconn,
						this.userView);
				String periodlist = planUtil.getPeriodTypeJsonList();
				periodlist = SafeCode.encode(periodlist);
				hm.put("info", periodlist);
				b = true;
			} else if ("dropdownPeriodList".equals(oprType)) {// 计划时间下拉列表 例：1月
																// 2月 3月……
				WorkPlanUtil planUtil = new WorkPlanUtil(this.frameconn,
						this.userView);
				String periodlist = planUtil.getPeriodList(periodType);
				periodlist = SafeCode.encode(periodlist);
				hm.put("info", periodlist);
				b = true;
			} else if ("dropdownPlanScope".equals(oprType)) {// 可见范围
				WorkPlanUtil planUtil = new WorkPlanUtil(this.frameconn,
						this.userView);
				String planTypeList = planUtil.getPlanScopeList();
				planTypeList = SafeCode.encode(planTypeList);
				hm.put("info", planTypeList);
				b = true;
			} else if ("dropdownHummanMapTypeList".equals(oprType)) {// 关注
																		// 团队成员菜单
				WorkPlanBo planBo = new WorkPlanBo(this.frameconn,
						this.userView);
				planBo.setPlanType(this.planType);//haosl 20161128
				planBo.initPlan(objectid, p0723, periodType, periodYear,
						periodMonth, periodWeek);
				String strList = planBo.getHumanMapTypeJsonList();
				strList = SafeCode.encode(strList);
				hm.put("info", strList);
				b = true;
			} else if ("dropdownDeptList".equals(oprType)) {// 我的部门列表
				WorkPlanUtil planUtil = new WorkPlanUtil(this.frameconn,
						this.userView);
				String nbase = this.userView.getDbname();
				String a0100 = this.userView.getA0100();
				if (deptLeader != null && deptLeader.length() > 3) {
					nbase = deptLeader.substring(0, 3);
					a0100 = deptLeader.substring(3, deptLeader.length());
				}
				String strList = planUtil.getDeptList_Json(nbase, a0100);
				strList = SafeCode.encode(strList);
				hm.put("info", strList);
				b = true;
			} else if ("dropdownAddMenuList".equals(oprType)) {// 下拉 复制任务、导入任务、
				StringBuilder menus = new StringBuilder("");
				menus.append("{");
				menus.append(quotedValue("periodlist")).append(":");
				menus.append("[");
				
				menus.append("{");
				menus.append(quotedValue("menu_id")).append(":");
				menus.append(quotedValue("copyPirorTask")).append(",");
				menus.append(quotedValue("menu_name")).append(":");
				menus.append(quotedValue("复制任务"));
				menus.append("}");
				//增加导入任务功能
				menus.append(",{");
				menus.append(quotedValue("menu_id")).append(":");
				menus.append(quotedValue("importTask")).append(",");
				menus.append(quotedValue("menu_name")).append(":");
				menus.append(quotedValue("导入任务"));
				menus.append("}");
				
				menus.append("]");
				menus.append("}");
				
				hm.put("info", SafeCode.encode(menus.toString()));
				b = true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return b;

	}

	/**
	 * @Title: isDealedTaskInfo
	 * @Description:
	 * @param @param hm
	 * @param @param oprType
	 * @param @return
	 * @return boolean
	 * @author:wangrd
	 * @throws
	 */
	private boolean isDealedTaskInfo(HashMap hm, String oprType)
			throws Exception {// 处理任务：新增、删除任务
		boolean b = false;
		try {
			if ("delTask".equals(oprType)) {// 删除任务:包括子任务删除
				String deltype = (String) hm.get("deltype");
				String task_ids = (String) hm.get("task_ids");
				// 解密
				String[] arrids = task_ids.split(",");
				String ids = "";
				for (int i = 0; i < arrids.length; i++) {
					String id = arrids[i];
					if ("".equals(id))
						continue;
					String[] idArray = id.split("_");
					id = idArray[0];
					String othertask = idArray[1];
					id = SafeCode.decode(id);
					id = WorkPlanUtil.decryption(id);
					if (ids.length() == 0)
						ids = id + "_" + othertask;
					else
						ids = ids + "," + id + "_" + othertask;
				}
				String plan_id = (String) hm.get("plan_id");
				plan_id = WorkPlanUtil.decryption(plan_id);

				WorkPlanBo planBo = new WorkPlanBo(this.frameconn,
						this.userView);
				String info = "";
				if (!planBo.delTask(plan_id, ids, deltype)) {
					info = planBo.getReturnInfo();
				} else {
					PlanTaskTreeTableBo taskTreeBo = new PlanTaskTreeTableBo(
							this.frameconn, Integer.parseInt(plan_id));
					String sumRank = taskTreeBo.getSumRank();
					hm.put("sum_rank", sumRank);
				}
				hm.put("info", info);
				b = true;

			} else if ("checkSubTask".equals(oprType)) {
				String task_ids = (String) hm.get("task_ids");
				// 解密任务id
				String[] arrids = task_ids.split(",");
				String ids = "";
				for (int i = 0; i < arrids.length; i++) {
					String id = arrids[i];
					if ("".equals(id))
						continue;
					String[] idArray = id.split("_");
					id = idArray[0];
					String othertask = idArray[1];
					id = SafeCode.decode(id);
					id = WorkPlanUtil.decryption(id);
					if (ids.length() == 0)
						ids = id + "_" + othertask;
					else
						ids = ids + "," + id + "_" + othertask;
				}
				String plan_id = (String) hm.get("plan_id");
				plan_id = WorkPlanUtil.decryption(plan_id);
				String objectid = (String) hm.get("objectid");
				objectid = WorkPlanUtil.decryption(objectid);
				String p0723 = (String) hm.get("p0723");
				p0723 = WorkPlanUtil.decryption(p0723);
				
				WorkPlanBo planBo = new WorkPlanBo(this.frameconn,
						this.userView);
				boolean flag = planBo.CheckSubTask(plan_id, ids, p0723, objectid);
				String info = planBo.getReturnInfo();
				hm.put("flag", flag + "");
				//返回信息会包含任务名称，如果任务名称有空格，数据传输过程会报错，所以后台加密后传输
				hm.put("info", SafeCode.encode(info));
				b = true;

			} else if ("checkNewTask".equals(oprType)) {// 检查新增任务
				String info = "";
				WorkPlanBo planBo = new WorkPlanBo(this.frameconn,
						this.userView);
				planBo.initPlan(objectid, p0723, periodType, periodYear,
						periodMonth, periodWeek);
				RecordVo p07Vo = planBo.getP07_vo();
				String othertask = (String) hm.get("othertask");//父级是否是穿透任务
				if (p07Vo != null) {
					if("0".equals(othertask) || "".equals(othertask)){//该计划不是穿透任务
						String _p0700 = p07Vo.getString("p0700");
						String rank = (String) hm.get("task_rank");
						String parent_id = (String) hm.get("task_parentid");
						parent_id = WorkPlanUtil.decryption(parent_id);
						PlanTaskBo taskBo = new PlanTaskBo(frameconn, userView);
						Number oValue = rank == null || "".equals(rank) ? null
								: new Float(rank);
						oValue = oValue == null ? null : Float.valueOf(oValue
								.floatValue() / 100);
						info = taskBo.getNewTaskRankMessage(_p0700, parent_id,
								oValue);
					}else{
						if("0".equals(p07Vo.getString("p0719")))
							info = "cannot_add";
					}
				}
				hm.put("info", info);
				b = true;
			} else if ("addTask".equals(oprType)) {// 增加任务
				WorkPlanBo planBo = new WorkPlanBo(this.frameconn,
						this.userView);
				planBo.initPlan(objectid, p0723, periodType, periodYear,
						periodMonth, periodWeek);
				//
				String task_name = (String) hm.get("task_name");
				String task_desc = (String) hm.get("task_desc");
				String task_cyr = (String) hm.get("task_cyr");
				String task_rank = (String) hm.get("task_rank");
				String task_startdate = (String) hm.get("task_startdate");
				String task_enddate = (String) hm.get("task_enddate");
				String parent_id = (String) hm.get("task_parentid");
				String task_seq = (String) hm.get("task_seq");
				String plan_scope = (String) hm.get("plan_scope");
				parent_id = WorkPlanUtil.decryption(parent_id);
				String othertask = (String) hm.get("othertask");//父级是否是穿透任务

				HashMap valueMap = new HashMap();
				valueMap.put("task_name", task_name);// 任务
				valueMap.put("task_desc", task_desc);// 任务id
				valueMap.put("task_cyr", task_cyr);// 成员标识
				valueMap.put("task_rank", task_rank);
				valueMap.put("task_startdate", task_startdate);
				valueMap.put("task_enddate", task_enddate);
				valueMap.put("task_parentid", parent_id);
				valueMap.put("task_seq", task_seq);
				valueMap.put("plan_scope", plan_scope);
				valueMap.put("othertask", othertask);
			
				String info = "";
				if (planBo.addTask(valueMap)) {//新增子任务成功
					hm.put("clearIDs", (String)valueMap.get("clearIDs"));
					PlanTaskTreeTableBo taskTreeBo = new PlanTaskTreeTableBo(
							this.frameconn, Integer.parseInt(planBo.getP0700()));
					String sumRank = taskTreeBo.getSumRank();
					info = planBo.getReturnInfo();
					/*info = "{"
					+ quotedValue("rowinfo")
					+ ":"
					+ info
					+ ","
					+ quotedValue("p0700")
					+ ":"
					+ quotedValue(WorkPlanUtil.encryption(planBo
							.getP0700())) + ","
					+ quotedValue("sum_rank") + ":"
					+ quotedValue(sumRank) + "}";*/
					info = SafeCode.encode(info);
					hm.put("rowinfo", info);
					hm.put("othertask", othertask);
					hm.put("p0700", WorkPlanUtil.encryption(planBo.getP0700()));
					hm.put("sum_rank", sumRank);
					hm.put("clearIDs", (String)valueMap.get("clearIDs"));
				}
				b = true;
			} else if ("checkUpdateTask".equals(oprType)) {// 检查更新权重
				String info = "";
				Map params = PlanTaskBo.setOutParams(formHM);
				String rank = (String) hm.get("value");
				periodType = (periodType == null) ? "" : periodType;
				PlanTaskBo bo = new PlanTaskBo(frameconn, userView);
				Number oValue = rank == null || "".equals(rank) ? null
						: new Float(rank);
				oValue = oValue == null ? null : Float.valueOf(oValue
						.floatValue() / 100);
				String p0800 = (String) params.get("p0800");
				RecordVo vo = bo.getTask(Integer.parseInt(p0800));
				// 如果任务已经取消(取消并被批准p0809=5)或任务状态为取消(任务取消,但未被批准p0833=2)
				if ("5".equals(vo.getString("p0809"))
						|| "2".equals(vo.getInt("p0833") + "")) {
					hm.put("info", "p0809false");
					b = true;
				} else {
					int p0833 = vo.getInt("p0833"); // 任务变更状态
					String p0809 = vo.getString("p0809"); // 任务执行状态
					if (WorkPlanConstant.TaskExecuteStatus.CANCEL.equals(p0809)
							|| WorkPlanConstant.TaskChangedStatus.Cancel == p0833) { // 已取消
						// 取消不检查
					} else {
						info = bo.getRankMessage(params, oValue);
					}
					b = true;
					hm.put("info", info);
				}
			} else if ("checkUpdateP0835".equals(oprType)) {// 检查更新权重和完成进度和更改负责人等
				String info = "";
				String field = (String) formHM.get("field");
				String fromflag=WorkPlanUtil.nvl((String)formHM.get("fromflag"),""); // "hr_create":工作计划制定
				Map params = PlanTaskBo.setOutParams(formHM);
				String p0800 = (String) params.get("p0800");
				PlanTaskBo bo = new PlanTaskBo(frameconn, userView);
				bo.setFromflag(fromflag);
				
				String p0835 = (String) hm.get("value");
				periodType = (periodType == null) ? "" : periodType;
				Number oValue = p0835 == null || "".equals(p0835) ? null
						: new Float(p0835);
				oValue = oValue == null ? null : Float.valueOf(oValue
						.floatValue() / 100);
				RecordVo vo = bo.getTask(Integer.parseInt(p0800));

				// 重置任务编辑/查看界面涉及到字段的编辑情况
				Map editableFields = bo.getEditableFields(params);
//				System.out.println(editableFields.toString());
				String always = null; // 总是可编辑的字段
				String normal = null; // 需要按钮触发编辑的字段
				always = "," + (String) editableFields.get("always");
				normal = "," + (String) editableFields.get("normal");
				if (!always.contains(field.toUpperCase())
						&& !normal.contains(field.toUpperCase()))
					hm.put(field, "false");
				else{
					//可编辑字段中，判断任务名称是否可编辑 lis 20160623
					String is_subplan = (String) formHM.get("subplan");//是否是我的下级
					String p0811 = vo.getString("p0811");//审批状态，lis 20160623
					if(!"true".equals(is_subplan) && "p0801".equalsIgnoreCase(field)){//是本人的任务且是任务名称
						if("03".equals(p0811))//如果是已批过，则不可修改名称
							hm.put(field, "false");
						else
							hm.put(field, "true");
					}
					else
						hm.put(field, "true");
				}
				
				// 如果任务已经取消(取消并被批准p0809=5)或任务状态为取消(任务取消,但未被批准p0833=2)
				if ("5".equals(vo.getString("p0809"))
						|| "2".equals(vo.getInt("p0833") + "")) {
					hm.put("info", "p0809false");
				} else
					hm.put("info", "p0809true");

				b = true;
			} else if ("checkothertaskversion".equals(oprType)) {//穿透任务编辑时，判断是否有权限 chent 20160517
				String p08nbase = "";
				String p08a0100 = "";
				
				Map params = PlanTaskBo.setOutParams(formHM);
				String p0800 = (String) params.get("p0800");
				String sql = "select nbase,a0100 from P09 where p0901=2 and p0903='"+p0800+"' and p0905=1";
				ContentDAO dao = new ContentDAO(this.frameconn);
				RowSet rs = null;
				rs = dao.search(sql);
				if(rs.next()){
					p08nbase = rs.getString("nbase");
					p08a0100 = rs.getString("a0100");
				}
				
				WorkPlanUtil workPlanUtil = new WorkPlanUtil(frameconn, userView);
				String myE01a1s = workPlanUtil.getMyE01a1s(this.userView.getDbname(), this.userView.getA0100());// 我的岗位
				String planPersonE01a1 = workPlanUtil.getMyMainE01a1(p08nbase, p08a0100);//任务负责人岗位
				if(workPlanUtil.isMySubE01a1( myE01a1s,planPersonE01a1)) {//是否是我的下级
					//可编辑字段中，判断任务名称是否可编辑 lis 20160623
					PlanTaskBo bo = new PlanTaskBo(frameconn, userView);
					RecordVo vo = bo.getTask(Integer.parseInt(p0800));
					String is_subplan = (String) formHM.get("subplan");//是否是我的下级
					String p0811 = vo.getString("p0811");//审批状态，lis 20160623 
					String field = (String) formHM.get("field");
					if(!"true".equals(is_subplan) && "p0801".equalsIgnoreCase(field)){//是本人的任务且是任务名称
						if("03".equals(p0811))//如果是已批过，则不可修改名称
							hm.put("info", "false");
						else
							hm.put("info", "true");
					}
					else
						hm.put("info", "true");
				} else {
					hm.put("info", "false");
				}
				b = true;
			} else if ("updateTask".equals(oprType)) {// 更新权重
				Map params = PlanTaskBo.setOutParams(formHM);
				String value = (String) hm.get("value");
				PlanTaskBo bo = new PlanTaskBo(frameconn, userView);
				String p0800 = (String) params.get("p0800"); // 任务id
				String objectid = (String) params.get("objectid");
				String p0723 = (String) params.get("p0723");
				RecordVo copy = bo.getTask(Integer.parseInt(p0800));
				// 获取该任务对应的计划信息
				RecordVo p07Vo = bo.getPlan(copy.getInt("p0700"));
				WorkPlanUtil wpUtil = new WorkPlanUtil(frameconn, userView);
				// (上级修改下级任务)如果该任务对应的计划的创建人是我的下级,则状态直接批准,isSub=true;
				boolean isSub = false;
				if (!objectid.equals(this.userView.getDbname()
						+ this.userView.getA0100())) {
					// 当前被操作任务所属的人员id和岗位(有可能不同于当前登陆人,如上级修改下级任务)
					String userId = "";
					String e01a1 = "";
					if ("1".equals(p07Vo.getString("p0723"))) {
						userId = p07Vo.getString("nbase")
								+ p07Vo.getString("a0100");
						e01a1 = wpUtil.getMyMainE01a1(userId.substring(0, 3),
								userId.substring(3));
					} else {
						userId = wpUtil.getFirstDeptLeaders(p07Vo
								.getString("p0707"));
						e01a1 = wpUtil.getDeptLeaderE01a1(p07Vo
								.getString("p0707"));
					}
					// 当前登陆人的岗位列表
					String loaderE01a1s = wpUtil.getMyE01a1s(this.userView
							.getDbname(), this.userView.getA0100());
					// 是否是下级
					isSub = wpUtil.isMySubE01a1(loaderE01a1s, e01a1);
				}
				int _p0700 = Integer.parseInt(p0700);
				int _p0800 = Integer.parseInt(p0800);

				// 更新权重
				if (!bo.updateRank(_p0700, _p0800, value, 1)) {
					return true;
				}
				// 清除上下级的权重
				bo.clearBranchRank(_p0700, _p0800);
				String clearIDs = bo.getClearRankTaskIds(_p0700, _p0800);
				hm.put("clearIDs", clearIDs);
				// 如果权重的修改需要改动任务变更状态
				hm.put("p0833", "0");
				if (WorkPlanConstant.TaskInfo.TASK_CHANGE_STATUS_FIELD
						.contains("RANK")) {
					RecordVo plan = bo.getPlan(_p0700);
					if (bo.ifCauseChangedStatusAltering(plan, copy)) {
						if (isSub) {
							copy.setString("p0811",
									WorkPlanConstant.TaskStatus.APPROVED);
							copy.setInt("p0833",
									WorkPlanConstant.TaskChangedStatus.Normal);
							hm.put("p0833",
									WorkPlanConstant.TaskChangedStatus.Normal
											+ "");
						} else {
							copy.setInt("p0833",
									WorkPlanConstant.TaskChangedStatus.Changed);
							hm.put("p0833",
									WorkPlanConstant.TaskChangedStatus.Changed
											+ "");
						}
						ContentDAO dao = new ContentDAO(this.frameconn);
						dao.updateValueObject(copy);
					}

				}
				PlanTaskTreeTableBo taskTreeBo = new PlanTaskTreeTableBo(
						this.frameconn, _p0700);
				String sumRank = taskTreeBo.getSumRank();
				hm.put("sum_rank", sumRank);
				b = true;

			} else if ("copyPirorTask".equals(oprType)) {// 复制任务
				String task_ids = (String) hm.get("task_ids");
				String parent_id = (String) hm.get("task_parentid");
				//okr复制任务优化，注释isCopyInfo参数
//				String isCopyInfo = (String) hm.get("isCopyInfo");//是否复制进度、完成情况
				String[] taskIds = task_ids.split(",");
				ArrayList p0800s = new ArrayList();
				for (int i = 0; i < taskIds.length; i++) {
					if (taskIds[i].trim().length() > 0) {
						p0800s.add(WorkPlanUtil.decryption(taskIds[i]));
					}
				}
				parent_id = WorkPlanUtil.decryption(parent_id);
				
				WorkPlanBo planBo = new WorkPlanBo(this.frameconn,
						this.userView);
				planBo.initPlan(objectid, p0723, periodType, periodYear,
						periodMonth, periodWeek);
				HashMap valueMap = new HashMap();
				String plan_scope = (String) hm.get("plan_scope");
				valueMap.put("plan_scope", plan_scope);
				valueMap.put("parent_id", parent_id);
				// 保存复制的上期任务
				String info = "";
				if (planBo.copyPirorTask(p0800s, valueMap, "")) {
					hm.put("clearIDs", valueMap.get("clearIDs"));
					info = "{"
							+ quotedValue("p0700")
							+ ":"
							+ quotedValue(WorkPlanUtil.encryption(planBo
									.getP0700())) + "}";
				}
				info = SafeCode.encode(info);
				hm.put("info", info);
				b = true;

			} else if ("exportExcel".equals(oprType)) {// 导出任务列表
				String outName = "";
				String p0700 = (String) this.getFormHM().get("p0700");
				p0700 = WorkPlanUtil.decryption(p0700);
				String p0723 = (String) this.getFormHM().get("p0723");
				p0723 = WorkPlanUtil.decryption(p0723);
				String periodType = (String) this.getFormHM().get("periodType");
				String periodYear = (String) this.getFormHM().get("periodYear");
				String periodMonth = (String) this.getFormHM().get(
						"periodMonth");
				String periodWeek = (String) this.getFormHM().get("periodWeek");
				String objectId = (String) this.getFormHM().get("objectId");
				
				//是否导出下属任务 =1 导出 =0 不导出
				String exportSubTask = (String)this.getFormHM().get("exportSubTask");
				
				objectId = WorkPlanUtil.decryption(objectId);
				WorkPlanBo workPlanBo = new WorkPlanBo(this.frameconn,
						this.userView);
				// 得到导出文件的名称
				String plan_title = workPlanBo.getExportPlanName(periodType,
						periodYear, periodMonth, periodWeek, objectId, p0723);

				outName = plan_title+".xls";
				ExportWorkPlanExcelBo exportWPBo = new ExportWorkPlanExcelBo(this.getFrameconn(), Integer.parseInt(p0700),this.userView);
				exportWPBo.createExcel(outName, "sheet", Integer.valueOf(p0723),Integer.valueOf(p0700),periodType,exportSubTask);
				outName = SafeCode.encode(PubFunc.encrypt(outName));
				this.getFormHM().put("outName", outName);
			} else if("editTaskfields".equals(oprType)){//计划页修改任务名称
				WorkPlanBo planBo = new WorkPlanBo(getFrameconn(), getUserView());
				String p0800 = (String) this.getFormHM().get("p0800");
				String field = (String) this.getFormHM().get("field");
				String value = (String) this.getFormHM().get("value");
				String info = "";
				if("p0801".equals(field)){
					String p0801 = (String) this.getFormHM().get("taskName");
					//前台任务名称加密处理（含有特殊字符时，报错很诡异），后台再次解密还原字符串
					if(StringUtils.isNotBlank(p0801))
						p0801 = PubFunc.keyWord_reback(SafeCode.decode(p0801));
					info = planBo.editTaskName(p0800, p0801);
				}else if("p0817".equals(field)){
					String p0817 = (String) this.getFormHM().get("value");
					info = planBo.PlanedHours(p0800, p0817);
				}else if("p0835".equals(field)){
					String p0835 = (String) this.getFormHM().get("value");
					// 计划跟踪界面修改任务进度的操作
					planBo.editTaskProgress(p0800, p0835);
					planBo.syncP04(WorkPlanUtil.decryption(p0800), "p0419", p0835 , "N");//同步目标卡的值 chent 20160414
				}else if("p0803".equals(field) || "p0841".equals(field) || "p0837".equals(field)){
					value = SafeCode.decode(value);
					value = value.replaceAll("＜", "<");//前台为避免跨域攻击做了字符过滤keyWord_filter（）；现把换大写行符换回小写 不然前台显示错乱 chent 20160323
					value = value.replaceAll("＞", ">");
					value = value.replaceAll("<br>", "\n");
					info = planBo.updateBigTextColumn(p0800, field, value);
					if("p0837".equals(field)){//进度说明
						planBo.syncP04(WorkPlanUtil.decryption(p0800), "p0409", value , "M");//同步目标卡的值 chent 20160414
					}
				}else if("p0813".equals(field)){
					info = planBo.updateStartTime(p0800, value);
				}else if("p0815".equals(field)){
					info = planBo.updateEndTime(p0800, value);
				}else{
					FieldItem item = DataDictionary.getFieldItem(field, "p08");
					String itemType = item.getItemtype();
					//代码类型
					if((!"".equals(item.getCodesetid())&&!"0".equals(item.getCodesetid()))){
						//修改value.split("`")[0]报角标越界的错	haosl
						info = planBo.updateTaskCode(p0800, field, value.split("`").length>0?value.split("`")[0]:"");	
					}else if("M".equalsIgnoreCase(itemType)){//大文本类型
						info = planBo.updateBigTextColumn(p0800, field, SafeCode.decode(value));
					}else if("D".equalsIgnoreCase(itemType)){
						info = planBo.updateTimeField(p0800, field, value);
					}else{//字符型\数值型 等普通类型
						info = planBo.updateBigTextColumn(p0800, field, value);
					}
				}
				hm.put("info", info);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return b;
	}

	/**
	 * @Title: isDealedPlanInfo
	 * @Description: //处理计划：发布 、批准、退回
	 * @param @param hm
	 * @param @param oprType
	 * @param @return
	 * @param @throws Exception
	 * @return boolean
	 * @author:wangrd
	 * @throws
	 */
	private boolean isDealedPlanInfo(HashMap hm, String oprType)
			throws Exception {
		boolean b = false;
		try {
			if ("publishPlan".equals(oprType)) {// 发布计划
				String plan_id = (String) hm.get("plan_id");
				plan_id = WorkPlanUtil.decryption(plan_id);
				String planType = (String)hm.get("planType");
				
				
				// 如果设置了权重之和强制为100时校验权重 chent 20160413 start
				WorkPlanConfigBo workPlanConfigBo = new WorkPlanConfigBo(this.frameconn);
				Map<String, String> config = workPlanConfigBo.getXmlData();//获取配置参数
				String plan_weight = config.get("plan_weight");
				if("1".equals(plan_weight)){//控制权重
					PlanTaskTreeTableBo taskTreeBo = new PlanTaskTreeTableBo(this.frameconn, Integer.parseInt(plan_id));
					String sumRank = taskTreeBo.getSumRank();//获取计划下权重之和
					int from = Integer.parseInt(config.get("from"));
					int to = Integer.parseInt(config.get("to"));
					int rankSum = (int)Float.parseFloat(sumRank);
					
					if(rankSum < from || rankSum > to) {
						String msg = "";
						if(from == to) {
							if(rankSum<from)
								msg = "权重之和不足"+from+"%，不允许提交！";
							else
								msg = "权重之和超过"+from+"%，不允许提交！";
						} else {
							msg = "权重之和必须在"+from+"%至"+to+"%之间！";
						}
						hm.put("info", SafeCode.encode(msg));
						return true;
					}
				}
				// 如果设置了权重之和强制为100时校验权重 chent 20160413 end
				
				WorkPlanBo planBo = new WorkPlanBo(this.frameconn,
						this.userView);
				planBo.initPlan(Integer.parseInt(plan_id));
				planBo.setPlanType(planType);
				/**
				 * 校验人员身份信息是否变动
				 */
				String info = planBo.checkPersonChange(plan_id);
				if(StringUtils.isNotEmpty(info)){
					//传大数据得转码
					info = SafeCode.encode(info);
					hm.put("info", info);
					return true;
				}
				
				//更改发布状态并邮件通知
				planBo.updatePlanStatus(plan_id, "1");
				WorkPlanUtil planUtil = new WorkPlanUtil(this.frameconn,
						this.userView);

				// 重新更新P07表的B0110,E0122,e01a1,superE01a1字段
				RecordVo vo = planBo.getP07Vo(Integer.parseInt(plan_id));
				if ("2".equals(vo.getString("p0723"))) {// 部门计划
					// 暂时不使用，留个接口
				} else {// 个人计划
					vo.setString("b0110", this.userView.getUserOrgId());
					vo.setString("e0122", this.userView.getUserDeptId());
					vo.setString("e01a1", this.userView.getUserPosId());
					if (this.userView.getUserPosId() != null)
						vo.setString("supere01a1", planUtil
								.getDirectSuperE01a1(this.userView
										.getUserPosId()));
				}
				ContentDAO dao = new ContentDAO(this.frameconn);
				dao.updateValueObject(vo);

				// 添加沟通信息
				String communicationMsg = "我发布了工作计划";
				new WorkPlanCommunicationBo(this.frameconn, this.userView)
						.publishMessage("1", plan_id, communicationMsg,
								DateUtils.FormatDate(new Date(),
										"yyyy-MM-dd HH:mm"), "");
				hm.put("info", "1");
				// 无上级岗位的直接批准
				if (!planUtil.isHaveDirectSuper(planBo.getObjectId(), planBo
						.getP0723())) {
					planBo.updatePlanStatus(plan_id, "2");
					hm.put("info", "2");
				}
				b = true;
			} else if ("approvePlan".equals(oprType)) {// 批准计划
				String plan_id = (String) hm.get("plan_id");
				plan_id = WorkPlanUtil.decryption(plan_id);
				WorkPlanBo planBo = new WorkPlanBo(this.frameconn,
						this.userView);
				planBo.updatePlanStatus(plan_id, "2");
				// 添加沟通信息
				String communicationMsg = this.userView.getUserFullName()
						+ "批准了工作计划";
				new WorkPlanCommunicationBo(this.frameconn, this.userView)
						.publishMessage("1", plan_id, communicationMsg,
								DateUtils.FormatDate(new Date(),
										"yyyy-MM-dd HH:mm"), "");
				b = true;
			} else if ("rejectPlan".equals(oprType)) {// 退回计划
				String plan_id = (String) hm.get("plan_id");
				String msg = (String) hm.get("rejectInfo");
				if (msg == null || msg.length() < 1) {
					msg = "无";
				} else {
					msg = SafeCode.decode(msg);
				}
				plan_id = WorkPlanUtil.decryption(plan_id);
				WorkPlanBo planBo = new WorkPlanBo(this.frameconn,
						this.userView);
				planBo.setReturnInfo(msg);
				planBo.updatePlanStatus(plan_id, "3");
				// 添加沟通信息
				String communicationMsg = this.userView.getUserFullName()
						+ "退回了您的工作计划，退回原因如下：" + msg;
				new WorkPlanCommunicationBo(this.frameconn, this.userView)
						.publishMessage("1", plan_id, communicationMsg,
								DateUtils.FormatDate(new Date(),
										"yyyy-MM-dd HH:mm"), "");
				b = true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return b;
	}

	/**
	 * @Title: isDealedFollower
	 * @Description: 处理关注人相关 、增加、删除关注人
	 * @param @param hm
	 * @param @param oprType
	 * @param @return
	 * @param @throws Exception
	 * @return boolean
	 * @author:wangrd
	 * @throws
	 */
	private boolean isDealedFollower(HashMap hm, String oprType)
			throws Exception {
		boolean b = false;
		try {
			if ("dropdownFollower".equals(oprType)) {// 下拉选择关注人
				String plan_id = (String) hm.get("plan_id");
				plan_id = WorkPlanUtil.decryption(plan_id);
				String keyword = (String) hm.get("keyword");
				WorkPlanBo planBo = new WorkPlanBo(this.frameconn,
						this.userView);
				String info = planBo.getDropdwonFollower(plan_id, keyword);
				info = "{" + info + "}";
				info = SafeCode.encode(info);
				hm.put("info", info);
				b = true;
			} else if ("addFollower".equals(oprType)) {// 增加关注人
				String plan_id = (String) hm.get("plan_id");
				if(!StringUtils.isEmpty(WorkPlanUtil.decryption(plan_id))){
					plan_id = WorkPlanUtil.decryption(plan_id);
				}
				String userid = (String) hm.get("followerId");
				userid = PubFunc.decryption(userid);
				WorkPlanBo planBo = new WorkPlanBo(this.frameconn,
						this.userView);
				
				String info = "";
				if (userid != null && userid.length() > 3) {
					String nbase = userid.substring(0, 3);
					String a0100 = userid.substring(3, userid.length());
					HashMap personMap = new HashMap();
					personMap.put("p0901", "1");// 计划
					personMap.put("p0903", plan_id);
					personMap
							.put("p0905", WorkPlanConstant.MemberType.FOLLOWER);// 成员标识
					personMap.put("nbase", nbase);
					personMap.put("a0100", a0100);
					if (planBo.addTaskPerson(personMap, true)) {
						info = planBo.getFollowerList(plan_id, nbase, a0100);
						// 发送邮件
						planBo.sendEmailToFollower(plan_id, nbase, a0100, true);
					} else {
						info = quotedValue("follower") + ":[" + info + "]";
					}
				}
				info = "{" + info + "}";
				info = SafeCode.encode(info);
				hm.put("info", info);
				b = true;
			} else if ("delFollower".equals(oprType)) {// 删除关注人
				String plan_id = (String) hm.get("plan_id");
				if(!StringUtils.isEmpty(WorkPlanUtil.decryption(plan_id))){
					plan_id = WorkPlanUtil.decryption(plan_id);
				}
				String info = "false";
				WorkPlanBo planBo = new WorkPlanBo(this.frameconn, this.userView);
				if(hm.get("followerId") != null) {// 删除指定关注人
					
					String userid = (String) hm.get("followerId");
					userid = WorkPlanUtil.decryption(userid);
					if (userid != null && userid.length() > 3) {
						String nbase = userid.substring(0, 3);
						String a0100 = userid.substring(3, userid.length());
						if (planBo.delFollower(plan_id, nbase, a0100)) {
							info = "true";
						}
					}
				} else {// 删除所有关注人
					if (planBo.delFollower(plan_id, "", "")) {
						info = "true";
					}
				}

				hm.put("info", info);
				b = true;
			} else if ("delMeConcerneder".equals(oprType)) {// 删除我关注的
				WorkPlanBo planBo = new WorkPlanBo(this.frameconn,
						this.userView);
				planBo.initPlan(objectid, p0723, periodType, periodYear,
						periodMonth, periodWeek);
				if (planBo.getP07_vo() != null) {
					String _p0700 = planBo.getP07_vo().getString("p0700");
					planBo.delFollower(_p0700, this.userView.getDbname(),
							this.userView.getA0100());
				}
				b = true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return b;
	}

	/**
	 * @Title: isDealedParticipant
	 * @Description: 处理负责人相关、下拉选择、增加负责人
	 * @param @param hm
	 * @param @param oprType
	 * @param @return
	 * @param @throws Exception
	 * @return boolean
	 * @author:wangrd
	 * @throws
	 */
	private boolean isDealedParticipant(HashMap hm, String oprType)
			throws Exception {
		boolean b = false;
		try {
			if ("dropdownParticipant".equals(oprType)) {// 下拉选择负责人
				String keyword = (String) hm.get("keyword");
				String userid = (String) hm.get("usrId");
				userid = WorkPlanUtil.decryption(userid);
				WorkPlanBo planBo = new WorkPlanBo(this.frameconn,
						this.userView);
				String info = planBo.getDropdownPersonJson(keyword, userid);
				info = "{" + info + "}";
				info = SafeCode.encode(info);
				hm.put("info", info);
				b = true;
			} else if ("addParticipant".equals(oprType)) {// 增加负责人 不在库中新增 只返回前台
				String userid = (String) hm.get("usrId");
				userid = PubFunc.decryption(userid);
				WorkPlanBo planBo = new WorkPlanBo(this.frameconn,
						this.userView);
				String info = "";
				if (userid != null && userid.length() > 3) {
					String nbase = userid.substring(0, 3);
					String a0100 = userid.substring(3, userid.length());
					info = planBo.getParticipantList(nbase, a0100);
				}
				info = "{" + info + "}";
				info = SafeCode.encode(info);
				hm.put("info", info);
				b = true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return b;
	}

	private String quotedValue(String value) {
		String str = WorkPlanUtil.quotedDoubleValue(value);

		return str;
	}

}
