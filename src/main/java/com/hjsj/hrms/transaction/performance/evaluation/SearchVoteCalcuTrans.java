package com.hjsj.hrms.transaction.performance.evaluation;

import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * <p>
 * Title:SearchVoteCalcuTrans.java
 * </p>
 * <p>
 * Description:考核评估/票数计算
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-05-07 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class SearchVoteCalcuTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
	String testTable = (String)this.getFormHM().get("testTable");
	if(testTable!=null)
	{
	    String planid = (String)this.getFormHM().get("planid");
	    String code = (String)this.getFormHM().get("code");
	    String isexists ="1";
	    DbWizard dbWizard = new DbWizard(this.frameconn);
	    if (!dbWizard.isExistTable("PER_BODYVOTE_"+planid, false))
		isexists ="0";
	    this.getFormHM().put("isExist", isexists);
	    this.getFormHM().put("code", code);
	    this.getFormHM().put("planid", planid);
	}
	else
	{
	    HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	    String planid = (String) hm.get("planid");
	    this.getFormHM().put("planid", planid);

	    LoadXml parameter_content = new LoadXml(this.getFrameconn(), planid);
	    Hashtable params = parameter_content.getDegreeWhole();
	    String voteScoreDecimal = (String) params.get("voteScoreDecimal");// 总分精度
	    String voteDecimal = (String) params.get("voteDecimal");// 权重精度

	    this.getFormHM().put("voteScoreDecimal", voteScoreDecimal);
	    this.getFormHM().put("voteDecimal", voteDecimal);

	    ArrayList list = new ArrayList();
	    ContentDAO dao = new ContentDAO(this.getFrameconn());

	    String strSql = "SELECT a.body_id,b.name,a.voterank,a.opt FROM per_plan_body a  join per_mainbodyset b "
	    		+ "on a.body_id=b.body_id and a.plan_id=" + planid + " order by b.seq"; // 加入主体打分确认标识，为1时，权重置为0 by 刘蒙
	    try
	    {
		this.frowset = dao.search(strSql);
		while (this.frowset.next())
		{
		    LazyDynaBean abean = new LazyDynaBean();
		    String body_id = this.frowset.getString("body_id");
		    abean.set("body_id", body_id);
		    String name = this.frowset.getString("name") == null ? "" : this.frowset.getString("name");
		    String voterank = this.frowset.getString("voterank") == null ? "1" : this.frowset.getString("voterank");
		    int pbOpt = this.frowset.getInt("opt");
		    voterank = pbOpt == 1 ? "0" : voterank; // 如果当前主体是确认权限，则权重置为0 by 刘蒙
		    voterank = PubFunc.round(voterank, Integer.parseInt(voteDecimal));

		    abean.set("voterank", voterank);
		    abean.set("name", name);
		    abean.set("pbOpt", new Integer(pbOpt));
		    list.add(abean);
		}
	    } catch (Exception e)
	    {
		e.printStackTrace();
		throw GeneralExceptionHandler.Handle(e);
	    }
	    this.getFormHM().put("planbodylist", list);
	}
    }

}
