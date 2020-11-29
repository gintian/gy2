package com.hjsj.hrms.transaction.workplan.summary;

import com.hjsj.hrms.businessobject.workplan.WorkPlanBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanConstant;
import com.hjsj.hrms.businessobject.workplan.WorkPlanConstant.Cycle;
import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.businessobject.workplan.summary.WorkPlanSummaryBo;
import com.hjsj.hrms.businessobject.workplan.summary.WorkSummaryMethodBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * 查询出，【我的团队】的工作总结情况
 * 
 * @author Administrator
 *
 */
public class QueryTeamWorkSummaryTrans extends IBusiness {

	public void execute() throws GeneralException {

		// 团队工作总结 or 下属工作总结
		String type = (String) this.getFormHM().get("type0");
		String summaryWeek = (String) this.getFormHM().get("week");
		String summaryCycle = (String) this.getFormHM().get("cycle");
		String summaryYear = (String) this.getFormHM().get("year");
		String summaryMonth = (String) this.getFormHM().get("month");
		//第几页
		//int pagenum = Integer.parseInt((String) this.getFormHM().get("pagenum"));
		// 判断，已提交，未提交，以打分
		String stateSign = (String) this.getFormHM().get("stateSign");
		String e01a1s = (String) this.getFormHM().get("e01a1s");
		e01a1s = WorkPlanUtil.decryption(e01a1s);
		
		String nbase = (String) this.getFormHM().get("nbase");
		nbase = WorkPlanUtil.decryption(nbase);
		if (null == nbase || "".equals(nbase.trim()))
			nbase = this.userView.getDbname();
		
		String a0100 = (String) this.getFormHM().get("a0100");
		a0100 = WorkPlanUtil.decryption(a0100);
		if (null == a0100 || "".equals(a0100.trim()))
			a0100 = this.userView.getA0100();
		
		// 处理nbase+A0100 ， 此时:nbase = nbase+A0100
		if (nbase.length() > 3) {
			String nbaseA0100 = nbase;
			nbase = nbaseA0100.substring(0, 3);
			a0100 = nbaseA0100.substring(3);
		}
		
		WorkPlanBo pb = new WorkPlanBo(getFrameconn(), getUserView());
		WorkPlanSummaryBo wpsBo = new WorkPlanSummaryBo();
		WorkSummaryMethodBo wsmBo = new WorkSummaryMethodBo(this.userView, this.getFrameconn());
		
		WorkPlanUtil workPlanUtil = new WorkPlanUtil(getFrameconn(), this.userView);

		// 已报
		int approveList = 0;
		// 未批
		int notApproveList = 0;
		// 已批
		int scoreList = 0;
		

		if (null == type || "".equals(type.trim())) {
			HashMap urlParam = (HashMap) this.getFormHM().get("requestPamaHM");
			type = (String) urlParam.get("type");

			if (null == type || "".equals(type.trim()))
				type = "team";
		}

		if (null == summaryCycle || "".equals(summaryCycle.trim())) {
			// 获取当前年月
			Date now = new Date();
			summaryCycle = WorkPlanConstant.SummaryCycle.WEEK;
			summaryYear = String.valueOf(DateUtils.getYear(now));
			summaryMonth = String.valueOf(DateUtils.getMonth(now));
		}
       
		if (null == summaryWeek || "".equals(summaryWeek))
		{
			String[] planCycles = { Cycle.Day, Cycle.WEEK, Cycle.MONTH, Cycle.QUARTER, Cycle.YEAR, Cycle.HALFYEAR };
			int cycle = Integer.parseInt(planCycles[Integer.parseInt(summaryCycle)]);
			int[] weeks = workPlanUtil.getLocationPeriod(cycle + "", Integer.parseInt(summaryYear), Integer.parseInt(summaryMonth));
			summaryWeek = wpsBo.getCurCycleIndex(summaryCycle, summaryYear,summaryMonth, weeks[2]+"");
		}
		
		try {

			String[] summaryDates = wpsBo.getSummaryDates(summaryCycle,summaryYear, summaryMonth, Integer.parseInt(summaryWeek));
			String startTime = summaryDates[0];
			String endTime = summaryDates[1];
			ArrayList list = new ArrayList();
			if ("team".equalsIgnoreCase(type)) {
				String flag= (String) this.getFormHM().get("flag");
				if("false".equals(flag)){	//当flag=false时，是说明从人力地图的缺编岗位进来的
					
        			 ContentDAO dao=new ContentDAO(getFrameconn());
        			//取直接上级字段
        			 String superoirField = new WorkPlanUtil(getFrameconn(), userView).getSuperiorFld();
        			 if (superoirField == null || "".equals(superoirField)) {
        					return;
        			}
        			//由当前岗位查找下级岗位
                 	 ArrayList e01a1List=new ArrayList();
                 	 String sql="select K.e01a1,O.codeitemdesc from k01 K,organization O where K.e01a1=O.codeitemid and "+superoirField+"='"+e01a1s+"'";
 	       			 RowSet rset=dao.search(sql);
 	       			 while(rset.next()){
 	       				 LazyDynaBean e01a1bean = new LazyDynaBean();
 	       				 e01a1bean.set("e01a1", rset.getString("e01a1"));
 	       				 e01a1bean.set("codeitemdesc", rset.getString("codeitemdesc"));
 	       				 e01a1List.add(e01a1bean);
 	       			 }
					list = wsmBo.selectTeamWeekly(e01a1List, startTime, endTime, summaryCycle, stateSign,"","",false,"");
                }else{
                	list = wsmBo.selectTeamWeekly(nbase,a0100, startTime, endTime, summaryCycle, stateSign,"","",false,"");
                }
				

			} else if ("sub_org".equalsIgnoreCase(type)) {
				ArrayList e01a1list = new ArrayList();
				
				if (e01a1s != null && !"".equals(e01a1s)) {
					String[] arre01a1 = e01a1s.split(",");
					for (int i = 0; i < arre01a1.length; i++) {
						String e01a1 = arre01a1[i];
						if ("".equals(e01a1))
							continue;
						LazyDynaBean bean = new LazyDynaBean();
						bean.set("e01a1", e01a1);
						e01a1list.add(bean);
					}
				} else {
					e01a1list = workPlanUtil.getMyE01a1List(nbase, a0100);
				}
				
				list = wsmBo.getMySubDeptPerson(e01a1list, startTime, endTime, summaryCycle ,stateSign,false);

			}

			// （以提交，未提交，以打分）各自的人数
			if (list.size() != 0 && "".equals(stateSign)) {

				for (int j = 0; j < list.size(); j++) {
					HashMap map = new HashMap();
					map = (HashMap) list.get(j);
					if ("02".equals(map.get("p0115")) && map.get("score") != null) {
						notApproveList+=1;
						approveList += 1;
					}
					// 已批准
					if ("03".equals(map.get("p0115"))) {
						approveList += 1;
						scoreList += 1;
					}
				}
				stateSign = "sign";
			}
			this.getFormHM().put("totalPeopleNumber", list.size() + "");
			this.getFormHM().put("approvePeopleNumber", approveList + "");//已报
			this.getFormHM().put("scorePeopleNumber", scoreList + "");//已批
			this.getFormHM().put("notApprovePeopleNumber", notApproveList + "");//未批
			this.getFormHM().put("type", type);
			this.getFormHM().put("stateSign", stateSign);
			/**分页
			if ((pagenum-1)*11-list.size() ==0 || (pagenum-1)*11>list.size() ){
                if (pagenum > 1)
                	pagenum= pagenum - 1;  
            }
			
			  int toIndex=pagenum*11;
		        if (toIndex>list.size()) toIndex=list.size();
			this.getFormHM().put("list", list.subList((pagenum-1)*11, toIndex)); **/
			this.getFormHM().put("list", list);

			// 获取该月有几周
			int weeknum = wpsBo.getSummaryNum(summaryCycle, summaryYear,summaryMonth);
			this.getFormHM().put("weeknum", weeknum + "");
			  if (WorkPlanConstant.SummaryCycle.WEEK.equals(summaryCycle)) {
	                this.getFormHM().put("weekstart", startTime);
	                this.getFormHM().put("weekend", endTime);
	            }
			this.getFormHM().put("type", type);
//			this.getFormHM().put("cycle", summaryCycle);
//			this.getFormHM().put("year", summaryYear);
//			this.getFormHM().put("month", summaryMonth);
			this.getFormHM().put("week", summaryWeek);
			this.getFormHM().put("a0101", this.userView.getUserFullName());
			
			String photoUrl = pb.getPhotoPath(this.userView.getDbname(),this.userView.getA0100());
			this.getFormHM().put("photo", photoUrl);

		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}

	}
	

}
