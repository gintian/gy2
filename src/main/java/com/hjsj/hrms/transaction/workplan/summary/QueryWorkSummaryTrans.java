package com.hjsj.hrms.transaction.workplan.summary;

import com.hjsj.hrms.businessobject.workplan.WorkPlanBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanConstant;
import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.businessobject.workplan.summary.WorkPlanSummaryBo;
import com.hjsj.hrms.businessobject.workplan.summary.WorkSummaryMethodBo;
import com.hjsj.hrms.module.workplan.config.businessobject.WorkPlanConfigBo;
import com.hjsj.hrms.module.workplan.config.businessobject.WorkPlanFunctionBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONArray;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class QueryWorkSummaryTrans extends IBusiness {

    public void execute() throws GeneralException {

    	RowSet frowset = null;
        try {
            WorkPlanSummaryBo wp = new WorkPlanSummaryBo(userView, getFrameconn());
          	WorkPlanUtil util = new WorkPlanUtil(getFrameconn(), userView);
            WorkPlanBo pb = new WorkPlanBo(getFrameconn(), getUserView());

            String ishr = (String) this.getFormHM().get("ishr");
            String type = (String) this.getFormHM().get("type");
            String maptype = (String) this.getFormHM().get("maptype");
            String haveleader = (String) this.getFormHM().get("haveleader");
            
            type = null == type ? "" : type;
            haveleader = null == haveleader ? "" : haveleader;

            String nbaseA0100 = WorkPlanUtil.decryption((String) this.getFormHM().get("nbaseA0100"));
            String nbase = WorkPlanUtil.decryption((String) this.getFormHM().get("nbase"));
            String a0100 = WorkPlanUtil.decryption((String) this.getFormHM().get("a0100"));
            //2是部门总结
            String belong_type = (String) this.getFormHM().get("belong_type");
            //拆分usra0100
            if( null == nbaseA0100 || "".equals(nbaseA0100))
            {
            	nbaseA0100=nbase+a0100;
            }else if (null == a0100 || "".equals(a0100))
			{
            	nbase=nbaseA0100.substring(0,3);
            	a0100=nbaseA0100.substring(3);
			}
            String summaryCycle = (String) this.getFormHM().get("cycle");
            //获取参数show_task
            WorkPlanConfigBo bo = new WorkPlanConfigBo(this.getFrameconn(), this.userView);
            RecordVo paramsVo=ConstantParamter.getConstantVo("OKR_CONFIG");
			String xmlValue = "";
			Map mapXml = new HashMap();
			// 有缓存则取缓存数据
			if(null != paramsVo){
				xmlValue = paramsVo.getString("str_value");
			}
			mapXml = bo.parseXml(xmlValue);
			String show_task = mapXml.get("show_task")==null?"false":(String)mapXml.get("show_task");
			this.getFormHM().put("show_task", show_task);
			
            //需要显示的总结类型列表 haosl  start
			WorkPlanFunctionBo funcBo = new WorkPlanFunctionBo(frameconn,userView);
            List<HashMap<String, HashMap<String, String>>> configList = funcBo.getXmlData();
            this.getFormHM().put("summaryTypeJson",JSONArray.fromObject(configList).toString());
          
            String defaultCycle = "";
            boolean flag = true;
            String monthCycle = "";
          //启用的总结
            StringBuffer realSummaryCycle = new StringBuffer();
            for(HashMap<String, HashMap<String, String>> map : configList){
        		if(map.get("s0")!=null){//年总结
        			if(flag) {
	        			flag = false;
	        			defaultCycle = WorkPlanConstant.SummaryCycle.YEAR;
        			}
        			realSummaryCycle.append(WorkPlanConstant.SummaryCycle.YEAR+",");
        		}else if(map.get("s1")!=null){//半年总结
        			if(flag) {
	        			flag = false;
	        			defaultCycle = WorkPlanConstant.SummaryCycle.HALFYEAR;
        			}
        			realSummaryCycle.append(WorkPlanConstant.SummaryCycle.HALFYEAR+",");
        		}else if(map.get("s2")!=null){//季度总结
        			if(flag) {
	        			flag = false;
	        			defaultCycle = WorkPlanConstant.SummaryCycle.QUARTER;
        			}
        			realSummaryCycle.append(WorkPlanConstant.SummaryCycle.QUARTER+",");
        		}else if(map.get("s3")!=null){//月度总结
					HashMap temp = map.get("s3");
					if(flag) {
						flag = false;
						defaultCycle = WorkPlanConstant.SummaryCycle.MONTH;
					}
					monthCycle =(String)temp.get("cycle");
					realSummaryCycle.append(WorkPlanConstant.SummaryCycle.MONTH+",");
				}else if(map.get("s4")!=null){//周总结
					if(flag) {
						flag = false;
						defaultCycle = WorkPlanConstant.SummaryCycle.WEEK;
					}
					realSummaryCycle.append(WorkPlanConstant.SummaryCycle.WEEK+",");
				}
            }
            //增加条件，如果前台传过来的总结类型并没有启用，则默认显示启用的总结的第一个类型  haosl 2018-3-21
			if (StringUtils.isBlank(summaryCycle) || realSummaryCycle.indexOf(summaryCycle)==-1) {
				summaryCycle = defaultCycle;
			}
            if(((nbase==null) || (nbase.trim().length()==0)) && StringUtils.isBlank(belong_type)){
            	return;
            }
            
            String e0122 = (String) this.getFormHM().get("e0122");
            if (e0122 != null && e0122.trim().length() > 0) {
                e0122 = WorkPlanUtil.decryption(e0122);
            }
            String b01ps = (String) this.getFormHM().get("b01ps");
            if (b01ps != null && b01ps.trim().length() > 0) {
            	b01ps = WorkPlanUtil.decryption(b01ps);
            }
            String a0101 = (String) this.getFormHM().get("a0101");
            
            if (!"team".equals(type) && !"sub_org".equals(type) && ("".equals(type) || type == null ))
			{
            	type = "2".equals(belong_type) ?"org" : "person";
			}
            String can_edit = "none";          //不可修改
            String isself="";                   //查看的是否自己的总结
            
            //isself = ((this.userView.getA0100().equals(a0100) && this.userView.getDbname().equals(nbase)) || null == a0100 || "".equals(a0100))&& ( haveleader ==null || "".equals(haveleader)||"yes".equals(haveleader)) ? "me" : "other"; //是自己为me
            if(((this.userView.getA0100().equals(a0100) && this.userView.getDbname().equals(nbase)) || null == a0100 || "".equals(a0100))&& ( haveleader ==null || "".equals(haveleader)||"yes".equals(haveleader))){
            	isself="me";//当前是自己的总结
            }else{
            	//不是自己的总结：只要是自己的下级(包括下级的下级),就可以看 wusy
            	 String p0100 = WorkPlanUtil.decryption((String) this.getFormHM().get("p0100"));
            	/* String myE01a1s=util.getMyE01a1s(this.userView.getDbname(), this.userView.getA0100());
            	 RecordVo vo = new RecordVo(nbase + "a01");
         		vo.setString("a0100", a0100);
         		vo = new ContentDAO(frameconn).findByPrimaryKey(vo);
         		String e01a1 = vo.getString("e01a1");*/
            	 
            	 //directsuper标记是直接上下级的标记，如果查看下级的下级的总结也是这个标记，就可以批准退回总结了，不合理（暂时将上面代码注释）。  haosl 2018-6-20
            	 if(wp.isMyDirectSubTeamPeople(nbase, a0100, belong_type, p0100)){
            		 isself="directsuper";
            	 }
         		/*if(util.isMySubE01a1(myE01a1s, e01a1)){
            		 isself="directsuper";
            	 }*/else{
            		 isself="other";
            	 }
            }
            if ("me".equalsIgnoreCase(isself))
			{
            	can_edit = "block" ;
            	nbase = this.userView.getDbname(); //人员库
                a0100 = this.userView.getA0100();
                a0101 = "我的";
                if("org".equals(type)){
                	a0101 = "("  +  this.userView.getUserFullName() + ")";
                }
			}
            //取当前操作用户的头像
            String photo = pb.getPhotoPath(nbase, a0100);
            WorkPlanUtil wputil = new WorkPlanUtil(getFrameconn(), getUserView());
            //是否是最高领导（无上级）
            String isLeader = wputil.isLeader(a0100,nbase);
            this.getFormHM().put("isLeader", isLeader);
            String summaryYear = (String) this.getFormHM().get("year");
            String summaryMonth = (String) this.getFormHM().get("month");
            
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
            String select_week = (String) this.getFormHM().get("week");
            //部门是否有负责人
            this.getFormHM().put("haveleader", haveleader);
            //用于发邮件 
            this.getFormHM().put("nbaseA0100", WorkPlanUtil.encryption(nbaseA0100));
            if ("org".equalsIgnoreCase(type) || "2".equals(belong_type))
			{
				ArrayList e0122list = wputil.getDeptList(nbase, a0100,false);
				//显示选择的部门 
				 for (int i = 0; i < e0122list.size(); i++)
				{
					 LazyDynaBean bean= (LazyDynaBean)e0122list.get(i);
					 if (e0122==null || "".equals(e0122))
					{
						 String deptdesc =(String)bean.get("deptdesc");
						 e0122 = (String)bean.get("b0110");
						 b01ps = (String)bean.get("b01ps");
				         	this.getFormHM().put("deptdesc", deptdesc);
				         	break;
					} else
					 if (((String)bean.get("b0110")).equals(e0122))
					{
						 String deptdesc =(String)bean.get("deptdesc");
						 b01ps = (String)bean.get("b01ps");
			         	this.getFormHM().put("deptdesc", deptdesc);
			         	break;
					}
				}
				 if (e0122list.size() == 0 &&  e0122!=null && !"".equals(e0122))
				{
			         	this.getFormHM().put("deptdesc", a0101);
			         	this.getFormHM().put("orgperson", "noperson");
			         	a0101 = "";
				}
			}
            
            this.getFormHM().put("b01ps", WorkPlanUtil.encryption(b01ps));
            this.getFormHM().put("e0122", WorkPlanUtil.encryption(e0122));
            
            ArrayList p0115state;
            String p0114 = ""; //提交时间
            String p0115 = ""; //本周状态
            String score = ""; //分
            String p0109 = "";
            String p0120 = "";
            String p0113 = "";
            String p0100 = "";
            String p0100tmp = "";
            String scope = WorkPlanConstant.Scope.SUPERIOR;

            ContentDAO dao = new ContentDAO(frameconn);

            p0115state = wp.getP011503Num(dao, 
                    Integer.parseInt(summaryCycle), 
                    Integer.parseInt(summaryYear), 
                    Integer.parseInt(summaryMonth), 
                    nbase, a0100, e0122, belong_type);

            	   if (!(select_week != null && select_week.trim().length() > 0)) {
                       select_week = wp.getCurCycleIndex(summaryCycle, summaryYear, summaryMonth, p0115state.get(0) + "");                
                   }
//               if (WorkPlanConstant.SummaryCycle.MONTH.equals(summaryCycle))
//               {
//               	 if (!(summaryMonth != null && summaryMonth.trim().length() > 0))
//               	summaryMonth = select_week;               
//               } 
            //本月有多少周
            int weeknum = wp.getSummaryNum(summaryCycle, summaryYear, summaryMonth);

            this.getFormHM().put("isself", isself);
            this.getFormHM().put("can_edit", can_edit);
            this.getFormHM().put("nbase", WorkPlanUtil.encryption(nbase));
            this.getFormHM().put("a0100", WorkPlanUtil.encryption(a0100));
            this.getFormHM().put("type", type);
            this.getFormHM().put("photo", photo);
            this.getFormHM().put("week", select_week);
            this.getFormHM().put("cycle", summaryCycle);
            this.getFormHM().put("year", summaryYear);
            this.getFormHM().put("month", summaryMonth);
            this.getFormHM().put("weeknum", weeknum + "");
            this.getFormHM().put("p011503", p0115state.get(1));
           // this.getFormHM().put("p011501", p0115state[1] + "");
            
            String[] summaryDates = wp.getSummaryDates(summaryCycle, summaryYear, summaryMonth, Integer.parseInt(select_week));

            StringBuilder sql = new StringBuilder();
            sql.append("SELECT p0100,P0113,p0109,p0114,p0115,p0120,score,scope");
            sql.append(" FROM P01 ");
            sql.append(" WHERE state=" + summaryCycle);
            sql.append(" AND P0104=" + Sql_switcher.dateValue(summaryDates[0]));
            sql.append(" AND P0106=" + Sql_switcher.dateValue(summaryDates[1]));

            StringBuilder sqlwhere = new StringBuilder();
            if(!"me".equalsIgnoreCase(isself)){
            	sqlwhere.append(" AND p0115 IN (02,03,07)");
            }
            //我的工作总结
           if ("org".equals(type) ||  "2".equals(belong_type)) { //部门
        	   sqlwhere.append(" AND e0122='" + e0122 + "'");
        	   sqlwhere.append(" AND belong_type=2");
            } else if ("person".equals(type) || belong_type==null || !"2".equals(belong_type)) {
            	sqlwhere.append(" AND nbase='" + nbase + "'");
            	sqlwhere.append(" AND a0100='" + a0100 + "'");
            	sqlwhere.append(" AND (belong_type is null or belong_type=0)");
            }
           
           sql.append(sqlwhere.toString());
           frowset = dao.search(sql.toString());
           while (frowset.next()) {
            	Timestamp ts = null;
    			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            	p0114 = df.format(frowset.getDate("p0114"));
				if(Sql_switcher.searchDbServer() == 2){
					ts = frowset.getTimestamp("p0114");
					if(ts != null){
						p0114 = df.format(new Date(ts.getTime()));
					}
				}
                p0115 = frowset.getString("p0115") == null ? "01" : frowset.getString("p0115");
                score = frowset.getString("score");
                p0109 = frowset.getString("p0109");
                p0120 = frowset.getString("p0120");
                p0100 = frowset.getString("p0100");
                p0100tmp = p0100;
                p0100 = WorkPlanUtil.encryption(p0100);
                p0113 = frowset.getString("p0113") == null ? "" : frowset.getString("p0113");;
                scope = frowset.getInt("scope") + "";
            }

            //linbz 获取上一期间参数
            HashMap mapPeriodParam = bo.getPriorPeriodParam("0", "1", summaryCycle, summaryYear, summaryMonth, select_week);
            String lastYear = mapPeriodParam.get("year")==null?summaryYear:mapPeriodParam.get("year").toString();
            String lastmonth = mapPeriodParam.get("month")==null?summaryMonth:mapPeriodParam.get("month").toString();
            String lastWeek = mapPeriodParam.get("week")==null?"1":mapPeriodParam.get("week").toString();
            String[] lastSummaryDates = wp.getSummaryDates(summaryCycle, lastYear, lastmonth, Integer.parseInt(lastWeek));
            String lastp0120 = "";
            StringBuilder lastsql = new StringBuilder();
            lastsql.append("SELECT p0120 ");
            lastsql.append(" FROM P01 ");
            lastsql.append(" WHERE state=" + summaryCycle);
            lastsql.append(" AND P0104=" + Sql_switcher.dateValue(lastSummaryDates[0]));
            lastsql.append(" AND P0106=" + Sql_switcher.dateValue(lastSummaryDates[1]));
            lastsql.append(sqlwhere.toString());
            
            frowset = dao.search(lastsql.toString());
            while (frowset.next()) {
            	lastp0120 = frowset.getString("p0120");
            }
            //获取本期计划
            if(StringUtils.isNotBlank(lastp0120))
				this.getFormHM().put("thisWorkPlan", lastp0120);
			else this.getFormHM().put("thisWorkPlan", "");
            
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
            this.getFormHM().put("p0100", p0100);
            this.getFormHM().put("p0113", WorkPlanUtil.formatText(p0113));
            this.getFormHM().put("a0101", a0101);
            this.getFormHM().put("belong_type", belong_type);
            // 获取年份范围
            this.getFormHM().put("yearList", wp.getMinYear(nbase, a0100)+ "");
            
            if (WorkPlanConstant.SummaryCycle.WEEK.equals(summaryCycle)) {
                this.getFormHM().put("weekstart", summaryDates[0]);
                this.getFormHM().put("weekend", summaryDates[1]);
            }
			if (!"true".equals(ishr)) {
				if (!"me".equals(isself) && !"orgmap".equals(maptype)) // 个人总结
				{
					String iscanread = "no";
					String str = "";
					if ("personmap".equals(maptype)) {
						ArrayList list = wp.searchPersonMap(this.userView.getA0100(), Integer.parseInt(summaryCycle), Integer.parseInt(summaryYear), Integer.parseInt(summaryMonth), select_week,
								WorkPlanConstant.PAGESIZE, 1);
						str = (String) ((HashMap) list.get(list.size() - 1)).get("sql");

					}
					else if ("teammap".equals(maptype)) {
						String hisnbase = WorkPlanUtil.decryption((String) this.getFormHM().get("hisnbase"));
						hisnbase = null == hisnbase || "".equals(hisnbase) ? userView.getDbname() : WorkPlanUtil.decryption((String) this.getFormHM().get("hisnbase"));
						String hisa0100 = WorkPlanUtil.decryption((String) this.getFormHM().get("hisa0100"));
						hisa0100 = null == hisa0100 || "".equals(hisa0100) ? userView.getA0100() : WorkPlanUtil.decryption((String) this.getFormHM().get("hisa0100"));
						WorkSummaryMethodBo wsmBo = new WorkSummaryMethodBo(this.userView, this.getFrameconn());
						str = "(" + wsmBo.getTeamPeopleSql(hisnbase, hisa0100, true, summaryDates[0], summaryDates[1], summaryCycle) + ") tmp";

					}
					//这段代码是为了使可以查看人力地图显示的人员的总结，不能被注释
					if("teammap".equals(maptype) || "personmap".equals(maptype)) {
						RowSet frowset1 = dao.search("select nbase,a0100 from " + str);
						while (frowset1.next()) {
							if (a0100.equals(frowset1.getString("a0100")) && nbase.equals(frowset1.getString("nbase"))) {
								iscanread = "yes";
								break;
							}
						}
					}
					WorkPlanSummaryBo summarybo=new WorkPlanSummaryBo(userView, frameconn);
					if (p0100tmp==null || p0100tmp.length()<1) p0100tmp="0";
					if(summarybo.checkIsCanReadSummary(Integer.parseInt(belong_type),nbase+a0100,Integer.parseInt(p0100tmp))){
						iscanread = "yes";
					}
					this.getFormHM().put("iscanread", iscanread);
				}
			}else {
				//为hr用户时的人员范围
				ArrayList hrList = new ArrayList();
				String iscanread = "no";
				WorkSummaryMethodBo wsmBo = new WorkSummaryMethodBo(this.userView, this.getFrameconn());
				if ("0".equalsIgnoreCase(belong_type)) {
					hrList = wsmBo.selectTeamWeekly(nbase, a0100, summaryDates[0], summaryDates[1], summaryCycle, "","","", true,"");
					if (hrList.size() != 0 ) {
						for (int j = 0; j < hrList.size(); j++) {
							HashMap map = new HashMap();
							map = (HashMap) hrList.get(j);
							if (WorkPlanUtil.encryption(a0100).equals(map.get("a0100")) && WorkPlanUtil.encryption(nbase).equals(map.get("nbase")) ) {
								iscanread = "yes";
								break;
							}
						}
					}
				}
				else if ("2".equalsIgnoreCase(belong_type)) {
					ArrayList e01a1list = new ArrayList();
					e01a1list = wsmBo.getHre01a1list("","");
					hrList = wsmBo.getMySubDeptPerson(e01a1list, summaryDates[0], summaryDates[1], summaryCycle,"",true);
					if (hrList.size() != 0 ) {
						for (int j = 0; j < hrList.size(); j++) {
							HashMap map = new HashMap();
							map = (HashMap) hrList.get(j);
							//if (WorkPlanUtil.encryption(b01ps).equals(map.get("e0122")) || e0122.equals(map.get("e0122"))) {
							//bug 34853   需要对e0122加密后在判断  haosl 2018-2-12 
							if (WorkPlanUtil.encryption(b01ps).equals(map.get("e0122")) || WorkPlanUtil.encryption(e0122).equals(map.get("e0122"))) {
								iscanread = "yes";
								break;
							}
						}
					}
				}
				this.getFormHM().put("iscanread", iscanread);
			}
        } catch (Exception e) {
            throw GeneralExceptionHandler.Handle(e);
        }finally{
        	PubFunc.closeResource(frowset);
        }
    }
}
