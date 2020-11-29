package com.hjsj.hrms.transaction.welcome.per;

import com.hjsj.hrms.actionform.performance.batchGrade.BatchGradeForm;
import com.hjsj.hrms.businessobject.performance.singleGrade.SingleGradeBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
/**
 * 法院的自助
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:May 30, 2007:11:47:31 AM</p> 
 *@author dengcan
 *@version 4.0
 */
public class PerIndexTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		ArrayList dblist=getJXplans();
		ArrayList marklist=getMarkList();
		this.getFormHM().put("dblist", dblist);
		
	}
	/**
	 * 得到绩效计划
	 * @return
	 */
	private ArrayList getJXplans()
	{
		  // 得到绩效考核计划列表
		ArrayList dblist=new ArrayList();
		String perPlanSql = "select plan_id,name,status from per_plan where ( status=4 or status=6 ) ";
		if (!userView.isSuper_admin())
			perPlanSql += "and plan_id in (select plan_id from per_mainbody where mainbody_id='"
					+ userView.getUserId() + "' and  object_id<>mainbody_id)";

		perPlanSql += " order by " + Sql_switcher.isnull("a0000", "999999999") + " asc,plan_id desc ";
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String titlename="";
			String planid="";
			this.frowset = dao.search(perPlanSql);

			/*CommonData vo1 = new CommonData("0", " ");
			dblist.add(vo1);*/
			
			int i = 0;
			while (this.frowset.next()) {
				if (i == 0) {
					titlename = this.getFrowset().getString("name");
					planid = this.getFrowset().getString("plan_id");

				}
				String name = this.getFrowset().getString("name");
				String plan_id = this.getFrowset().getString("plan_id");
				CommonData vo = new CommonData(plan_id, name);
				dblist.add(vo);
				i++;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		SingleGradeBo singleGradeBo=new SingleGradeBo(this.getFrameconn());
		ArrayList aList=singleGradeBo.getBatchGradeBo().addGradeStaus(dblist,this.getUserView().getUserId(),3);
		dblist=aList;
		if(dblist==null||dblist.size()<=0)
		{
			CommonData vo1 = new CommonData("0", " 没有计划");
			dblist.add(vo1);
		}
	    HttpSession session=(HttpSession)this.getFormHM().get("session");
	    BatchGradeForm batchGradeForm=(BatchGradeForm)session.getAttribute("batchGradeForm");
	    if(batchGradeForm==null)
	    {
	    	batchGradeForm=new BatchGradeForm ();
	    	batchGradeForm.setPlan_descript("");
	    	batchGradeForm.setTargetDeclare("");
	    	batchGradeForm.setIndividualPerformance("");
	    	batchGradeForm.setSpan_ids("");
	    	batchGradeForm.setDbpre("0");
	    	batchGradeForm.setDblist(dblist);
	    }
	    session.setAttribute("batchGradeForm",batchGradeForm);
	    return dblist;
	}
	/**
	 * 打分状态
	 * @return
	 */
	private ArrayList getMarkList()
	{
		ArrayList marklist=new ArrayList();
		return marklist;
	}
}
	

