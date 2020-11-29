package com.hjsj.hrms.transaction.workplan.summary;

import com.hjsj.hrms.businessobject.workplan.WorkPlanBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanConstant;
import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.businessobject.workplan.summary.WorkPlanSummaryBo;
import com.hjsj.hrms.module.workplan.config.businessobject.WorkPlanConfigBo;
import com.hjsj.hrms.module.workplan.config.businessobject.WorkPlanFunctionBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONArray;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class SearchWorkSummaryTrans extends IBusiness {

	public void execute() throws GeneralException {

		try {
			WorkPlanSummaryBo wp = new WorkPlanSummaryBo(userView, this.getFrameconn());
			WorkPlanBo pb = new WorkPlanBo(getFrameconn(), getUserView());
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String isInit = (String)hm.get("b_query");	//判断是不是第一次进入该页面
			boolean checkWeek = needCheckWeek(hm);
			// 标志是否是业务用户
			String ishr = (String) hm.get("ishr");
			this.getFormHM().put("ishr", ishr);
			// 标志是否是email查看
			String isemail = (String) hm.get("isemail");
			this.getFormHM().put("isemail", isemail);
			String type = (String) hm.get("type");
			
			if("team".equals(type) || "sub_org".equals(type))
				type="";
			// 2是部门总结
			String belong_type = (String) hm.get("belong_type");

			ArrayList p0115state;
			String nbaseA0100 = WorkPlanUtil.decryption((String) hm.get("nbaseA0100"));
			String nbase = WorkPlanUtil.decryption((String) hm.get("nbase"));
			String a0100 = WorkPlanUtil.decryption((String) hm.get("a0100"));
			if(StringUtils.isBlank(nbaseA0100) && StringUtils.isBlank(nbase) && StringUtils.isBlank(a0100)){
                nbase = this.userView.getDbname();
                a0100 = this.userView.getA0100();
            }
            // 拆分usra0100
            if (null == nbaseA0100 || "".equals(nbaseA0100)) {
                nbaseA0100 = nbase + a0100;
            }
            else if (null == a0100 || "".equals(a0100)) {
                nbase = nbaseA0100.substring(0, 3);
                a0100 = nbaseA0100.substring(3);
            }
			// 部门总结时如果没有负责部门，则显示个人总结（处理与部门计划保持一致） chent 20180329 add start
			WorkPlanUtil wputil = new WorkPlanUtil(getFrameconn(), getUserView());
			if ("org".equalsIgnoreCase(type)) {
			    //初始进入页面时并没有nbase 和 a0100
                ArrayList e0122list = new ArrayList();
                if("true".equals(ishr)){
                    e0122list = wputil.getDeptList(nbase, a0100,false);
                }else{
                    e0122list = wputil.getDeptList(nbase, a0100,true);
                }
				if(e0122list.size() == 0) {
					type = "person";
				}
			}
			// 部门总结时如果没有负责部门，则显示个人总结（处理与部门计划保持一致） chent 20180329 add end
			

			 //需要显示的总结类型列表 haosl  start
			WorkPlanFunctionBo funcBo = new WorkPlanFunctionBo(frameconn,userView);
            List<HashMap<String, HashMap<String, String>>> configList = funcBo.getXmlData();
            this.getFormHM().put("summaryTypeJson",JSONArray.fromObject(configList).toString());
          
            String defaultCycle = "";
            String summaryCycle = (String) hm.get("cycle"); //需要默认显示的总结区间 
            boolean flag = true;
            String monthCycle = "";
            for(HashMap<String, HashMap<String, String>> map : configList){
        		if(map.get("s0")!=null && flag){//年总结
        			flag = false;
        			defaultCycle = WorkPlanConstant.SummaryCycle.YEAR;
        		}else if(map.get("s1")!=null && flag){//半年总结
        			flag = false;
        			defaultCycle = WorkPlanConstant.SummaryCycle.HALFYEAR;
        		}else if(map.get("s2")!=null && flag){//季度总结
        			flag = false;
        			defaultCycle = WorkPlanConstant.SummaryCycle.QUARTER;
        		}else if(map.get("s3")!=null){//月度总结
					HashMap temp = map.get("s3");
					if(flag)
						defaultCycle = WorkPlanConstant.SummaryCycle.MONTH;
						
					monthCycle =(String)temp.get("cycle");
					flag = false;
				}else if(map.get("s4")!=null && flag){//周总结
					flag = false;
					defaultCycle = WorkPlanConstant.SummaryCycle.WEEK;
				}
            }
            if (null == summaryCycle || "".equals(summaryCycle.trim())) {
				summaryCycle = defaultCycle;
			}
            if(StringUtils.isBlank(summaryCycle)){//程序执行到这，如果summaryCycle为空，那么提示用户启用工作总结
				throw new Exception("未启用任何类型的工作总结，暂无法查看！");
			}
			// 登录用户
			String a0101 = this.getUserView().getUserFullName();
			// 取头像
			String photo = pb.getPhotoPath(this.userView.getDbname(), this.userView.getA0100());
			// 当前用户
			String user_a0101 = (String) hm.get("a0101");
			user_a0101 = SafeCode.decode(user_a0101);
			if (!"true".equals(ishr)) {
				if ("".equals(a0100) && "".equals(nbase) && "".equals(this.userView.getA0100())) {
					throw new Exception("非自助用户不能使用该功能！");
				}
			}

			// 显示总结类型
			if (WorkPlanConstant.SummaryCycle.WEEK.equals(summaryCycle))
				this.getFormHM().put("typetitle", "工作周报");
			else if (WorkPlanConstant.SummaryCycle.MONTH.equals(summaryCycle))
				this.getFormHM().put("typetitle", "工作月报");
			else if (WorkPlanConstant.SummaryCycle.QUARTER.equals(summaryCycle))
				this.getFormHM().put("typetitle", "季度总结");
			else if (WorkPlanConstant.SummaryCycle.HALFYEAR.equals(summaryCycle))
				this.getFormHM().put("typetitle", "半年总结");
			else if (WorkPlanConstant.SummaryCycle.YEAR.equals(summaryCycle))
				this.getFormHM().put("typetitle", "年度总结");
			String user_photo = pb.getPhotoPath(nbase, a0100);
			String can_edit = "none"; // 不可修改
			String isself = ""; // 查看的是否自己的总结
			//isself = (this.userView.getA0100().equals(a0100) && this.userView.getDbname().equals(nbase)) || null == a0100 || "".equals(a0100) ? "me" : "other"; // 是自己为me
		    if((this.userView.getA0100().equals(a0100) && this.userView.getDbname().equals(nbase)) || null == a0100 || "".equals(a0100) ){
            	isself="me";//当前是自己的总结
            }else{
            	//不是自己的总结：直接下级可以看，否则不可以看
            	 String p0100 = WorkPlanUtil.decryption((String) this.getFormHM().get("p0100"));
            	 if(wp.isMyDirectSubTeamPeople(nbase, a0100, belong_type, p0100)){
            		 isself="directsuper";
            	 }else{
            		 isself="other";
            	 }
            }
		    String isorg = wp.isHaveOrg();
			if ("me".equalsIgnoreCase(isself)) {
				can_edit = "block";
				nbase = this.userView.getDbname(); // 人员库
				a0100 = this.userView.getA0100();
				user_a0101 = "我的";
				if("true".equals(isorg) && !"person".equals(type)){//兼容程序其他调用，有不传type的情况，如邮件连接
					user_a0101 = this.userView.getUserFullName();
				}
				user_photo = photo;
			}
			
			// 是否是最高领导（无上级）
			String isLeader = wputil.isLeader(a0100, nbase);
			this.getFormHM().put("isLeader", isLeader);
			this.getFormHM().put("a0101", a0101);
			this.getFormHM().put("user_photo", user_photo);
			String p0114 = ""; // 提交时间
			String p0115 = ""; // 本周状态
			String score = ""; // 分
			String p0109 = "";
			String p0120 = "";
			String p0100 = "";
			String p0113 = "";
			String scope = WorkPlanConstant.Scope.SUPERIOR;

			ContentDAO dao = new ContentDAO(frameconn);
			boolean isNow = false;
			String summaryYear = (String) hm.get("year");
			String summaryMonth = (String) hm.get("month");
			String select_week = (String) hm.get("week");
			//hr查询的条件，返回hr时使用
			String returnurl = (String) hm.get("returnurl");
			returnurl = null == returnurl ? "" : returnurl;

			if(WorkPlanConstant.SummaryCycle.MONTH.equals(summaryCycle)){//月报需要重新定位月份
				String currentMonth = DateUtils.getMonth(new Date())+"";
				if(( ","+monthCycle+",").indexOf(","+summaryMonth+",")>-1){

				}else if(( ","+monthCycle+",").indexOf(","+currentMonth+",")>-1){
					summaryMonth = currentMonth;
				}else{
					String[] cycleArr = monthCycle.split(",");
					if(cycleArr.length>0)
						summaryMonth = cycleArr[0];
				}

			}
			if(StringUtils.isBlank(summaryMonth)){
				summaryMonth = String.valueOf(DateUtils.getMonth(new Date()));
			}
			
			if (!"".equals(returnurl)) {
				returnurl = SafeCode.decode(returnurl)+"&cycle="+summaryCycle+"&year="+summaryYear+"&month="+summaryMonth+"&week="+select_week;
			}
			this.getFormHM().put("returnurl", returnurl);
			hm.clear();
			Date now = new Date();
			select_week = null == select_week || "".equals(select_week) ? "1" : select_week;
			summaryYear = null == summaryYear || "".equals(summaryYear) ? "" + DateUtils.getYear(now) : summaryYear;
			int year = Integer.parseInt(summaryYear);
			int month = Integer.parseInt(summaryMonth);
			
			// 判断一号是否是本月
			if (checkWeek && wp.getTrueDate(year, month, -1)) {
				year = DateUtils.getYear(DateUtils.addMonths(now, -1));
				month = DateUtils.getMonth(DateUtils.addMonths(now, -1));
				isNow = true;
			}

			// 判断月末是否为下个月
			if (checkWeek && wp.getTrueDate(DateUtils.getYear(DateUtils.addMonths(now, 1)), DateUtils.getMonth(DateUtils.addMonths(now, -1)), 1)) {
				year = DateUtils.getYear(DateUtils.addMonths(now, 1));
				month = DateUtils.getMonth(DateUtils.addMonths(now, 1));
				isNow = true;
			}
			// 默认显示页面
			// 是否有关注人
			// String isperson=wp.isHavePerson(Integer.parseInt(summaryCycle),
			// Integer.parseInt(summaryYear),Integer.parseInt(summaryMonth),
			// select_week);
			// 是否有下属部门
			// String issuborg=wp.isHaveSubOrg();
			// 是否是部门负责人
			//String isorg = wp.isHaveOrg();
			if (("true".equals(isorg) || "true".equals(ishr))&&!"person".equals(type)) {
				if((belong_type == null || "".equals(belong_type)) && ( type == null ||"".equals(type)|| "org".equals(type)))
				{
					belong_type = "2";
				}
				this.getFormHM().put("maptype", "personorgmap");
			}
			else {
				// 是否有团队成员
				String isteam = wp.isHaveTeam();
				belong_type = "0";
				if ("true".equals(isteam)) {
					this.getFormHM().put("maptype", "teammap");
				}
				else {
					this.getFormHM().put("maptype", "personmap");
				}
			}
			if (!"team".equals(type) && !"sub_org".equals(type) && ("".equals(type) || type == null )) {
				type = "2".equals(belong_type) ? "org" : "person";
			}
			
			if ("".equals(user_a0101)) {
				user_a0101 = wp.getUserA0101(nbase, a0100);
			}
			if (!"我的".equals(user_a0101) && !"".equals(user_a0101) && "org".equals(type)) {
				user_a0101 = "(" + user_a0101 + ")";
			}
			this.getFormHM().put("user_a0101", user_a0101);
			this.getFormHM().put("belong_type", belong_type);
			String e0122 = "";
			String deptdesc = "";
			if ("org".equalsIgnoreCase(type) || "2".equals(belong_type)) {
				String b01ps = ""; // 负责岗位
				if(!"init".equals(isInit))
					e0122 = (String) this.getFormHM().get("e0122");
				deptdesc = (String) this.getFormHM().get("deptdesc");
				// if (e0122 == null || e0122.trim().length() == 0)
				// {
				//		    		  
				// }
				if (e0122 != null && e0122.trim().length() > 0) {
					e0122 = WorkPlanUtil.decryption(e0122);
				}
				if (!"true".equals(ishr)) {
					ArrayList e0122list = wputil.getDeptList(nbase, a0100);
					if (e0122list.size() == 0) {
						throw new GeneralException("您没有负责的部门，不能进行操作！");
					}
					for (int i = 0; i < e0122list.size(); i++) {
						LazyDynaBean bean = (LazyDynaBean) e0122list.get(i);
						if (StringUtils.isBlank(e0122)) {
							bean = (LazyDynaBean) e0122list.get(0);
							e0122 = (String) bean.get("b0110");
							deptdesc = (String) bean.get("deptdesc");
							b01ps = (String) bean.get("b01ps");
							break;
						}
						else if (((String) bean.get("b0110")).equals(e0122)) {
							deptdesc = (String) bean.get("deptdesc");
							b01ps = (String) bean.get("b01ps");
							break;
						}
					}
				}
				this.getFormHM().put("b01ps", WorkPlanUtil.encryption(b01ps));
				this.getFormHM().put("deptdesc", deptdesc);
				this.getFormHM().put("e0122", WorkPlanUtil.encryption(e0122));
			}

			p0115state = wp.getP011503Num(dao, Integer.parseInt(summaryCycle), year, month, nbase, a0100, e0122, belong_type);

			// 默认显示当前周
			if (checkWeek &&!"0".equals(p0115state.get(0)) && (DateStyle.dateformat(new Date(), "yyyy-MM").equals(month < 10 ? year + "-0" + month : year + "-" + month) || isNow)) {
				select_week = p0115state.get(0) + "";
			}
			// String firstday=wp.getMondayOfDate(year, month,
			// Integer.parseInt(select_week));

			// 本月有多少周
			int weeknum = wp.getSummaryNum(summaryCycle, year+"", month+"");

			this.getFormHM().put("isself", isself);
			this.getFormHM().put("can_edit", can_edit);
			this.getFormHM().put("nbase", WorkPlanUtil.encryption(nbase));
			this.getFormHM().put("a0100", WorkPlanUtil.encryption(a0100));
			this.getFormHM().put("user_nbase", WorkPlanUtil.encryption(nbase));
			this.getFormHM().put("user_a0100", WorkPlanUtil.encryption(a0100));
			// 用于发邮件
			this.getFormHM().put("nbaseA0100", WorkPlanUtil.encryption(nbase + a0100));
			this.getFormHM().put("type", type);
			this.getFormHM().put("photo", photo);
			this.getFormHM().put("week", select_week);
			this.getFormHM().put("weeknum", weeknum + "");
			this.getFormHM().put("p011503", p0115state.get(1));
			// this.getFormHM().put("p011501", p0115state[1]+"");

			String summaryDatesWhr = wp.getSummaryDatesWhr(summaryCycle, String.valueOf(year), String.valueOf(month), Integer.parseInt(select_week));

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT p0100,P0113,p0109,p0114,p0115,p0120,score,scope");
			sql.append(" FROM P01 ");
			sql.append(" WHERE " + summaryDatesWhr);

			// 我的工作总结
			if ("org".equals(type) || "2".equals(belong_type)) { // 部门
				sql.append(" AND e0122='" + e0122 + "'");
				sql.append(" AND belong_type=2");
			}
			else if ("person".equals(type) || belong_type == null || !"2".equals(belong_type)) {
				sql.append(" AND nbase='" + nbase + "'");
				sql.append(" AND a0100='" + a0100 + "'");
				sql.append(" AND (belong_type is null or belong_type=0)");
			}

			this.frowset = dao.search(sql.toString());
			Timestamp ts = null;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			if (this.frowset.next()) {

				p0114 = df.format(this.frowset.getDate("p0114"));
				if(Sql_switcher.searchDbServer() == 2){
					ts = this.frowset.getTimestamp("p0114");
					if(ts != null){
						p0114 = df.format(new Date(ts.getTime()));
					}
				}
				p0115 = this.frowset.getString("p0115") == null ? "01" : this.frowset.getString("p0115");
				score = this.frowset.getInt("score")+"";
				p0109 = this.frowset.getString("p0109");
				p0120 = this.frowset.getString("p0120");
				p0100 = this.frowset.getInt("p0100")+"";
				p0113 = this.frowset.getString("p0113");
				scope = this.frowset.getInt("scope") + "";

			}
			this.getFormHM().put("scope", "0".equals(scope) ? WorkPlanConstant.Scope.SUPERIOR : scope);
			if(StringUtils.isNotBlank(p0109))
				this.getFormHM().put("thisWorkSummary", p0109);
			else this.getFormHM().put("thisWorkSummary", "");
			if(StringUtils.isNotBlank(p0120))
				this.getFormHM().put("nextWorkSummary", p0120);
			else this.getFormHM().put("nextWorkSummary", "");
			this.getFormHM().put("score", null == score || "".equals(score) ? "-1" : score);
			this.getFormHM().put("p0115", p0115);
			this.getFormHM().put("p0114", "".equals(p0114) ? "" : p0114.substring(0, 16));
			this.getFormHM().put("p0100", WorkPlanUtil.encryption(p0100));
			this.getFormHM().put("p0113", WorkPlanUtil.formatText(p0113));
			this.getFormHM().put("cycle", summaryCycle);
			this.getFormHM().put("year", String.valueOf(year));
			this.getFormHM().put("month", String.valueOf(month));
			String[] summaryDates = wp.getSummaryDates(summaryCycle, String.valueOf(year), String.valueOf(month), Integer.parseInt(select_week));
			this.getFormHM().put("weekstart", summaryDates[0]);
			this.getFormHM().put("weekend", summaryDates[1]);
			// 获取用户的年份权限
			this.getFormHM().put("yearList", wp.getMinYear(nbase, a0100) + "");
			
			
			// 周总结 chent 20161205 add start
			String zhouzj = "false";
			WorkPlanConfigBo workPlanConfigBo = new WorkPlanConfigBo(this.getFrameconn());
			Map map = workPlanConfigBo.getXmlData();
			if("2".equals(map.get("fillModel"))){
				zhouzj = "true";
			}
			this.getFormHM().put("zhouzj", zhouzj);//1、启用周总结
			
			StringBuilder zhouzjpx = new StringBuilder();
			ArrayList fieldList = DataDictionary.getFieldList("P01", Constant.USED_FIELD_SET);
			for(int i=0; i<fieldList.size(); i++){
				FieldItem item = (FieldItem)fieldList.get(i);

				if("0".equals(item.getState())){
					continue ;
				}
				
				if("M".equals(item.getItemtype())){//备注
					String itemid = item.getItemid().toLowerCase();//字段id
					// appuser：操作人、p0113：领导批示、p0103：本期工作计划、p0109：计划完成情况及业绩总结,p0101：工作项目,p0111：出现问题及建议 、p0120：下期工作计划
					//if(",appuser,p0113,p0101,p0120,".indexOf(itemid) > -1){// 排除默认备注型
					if(",appuser,p0113,p0103,p0109,p0101,p0111,p0120,".indexOf(itemid) > -1){// 排除默认备注型
						continue ;
					}
					String itemdesc = item.getItemdesc().toLowerCase();
					zhouzjpx.append(itemid+":"+ itemdesc+",");
				}
			}
			if(zhouzjpx.length() > 0){
				zhouzjpx = zhouzjpx.deleteCharAt(zhouzjpx.length()-1);
			}
			this.getFormHM().put("zhouzjpx", zhouzjpx.toString());
			// 周总结 chent 20161205 add end
            if(StringUtils.isNotBlank(nbase) && StringUtils.isNotBlank(a0100)){
                WorkPlanConfigBo configBo = new WorkPlanConfigBo(this.frameconn, this.userView);
                HashMap myFuncMap = configBo.getPersonFillType(this.userView.getDbname(), this.userView.getA0100());
                HashMap personFunctions = configBo.getPersonFunctions(myFuncMap, configList);
                List<HashMap<String, HashMap<String, String>>> personConfigList = (List<HashMap<String, HashMap<String, String>>>) personFunctions.get("person");
                List<HashMap<String, HashMap<String, String>>> orgConfigList = (List<HashMap<String, HashMap<String, String>>>) personFunctions.get("org");

                this.getFormHM().put("personCycleFunction", JSONArray.fromObject(personConfigList).toString());
                this.getFormHM().put("orgCycleFunction", JSONArray.fromObject(orgConfigList).toString());
            }
		}
		catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}

	}

	private boolean needCheckWeek(HashMap tranParam) {
		String summaryCycle = (String) tranParam.get("cycle");
		String summaryYear = (String) tranParam.get("year");
		String select_week = (String) tranParam.get("week");

		return (null == summaryCycle || "".equals(summaryCycle)) && (null == summaryYear || "".equals(summaryYear)) && (null == select_week || "".equals(select_week));
	}
}