package com.hjsj.hrms.transaction.performance.implement.kh_object.set_dyna_target_rank;

import com.hjsj.hrms.interfaces.performance.OrgPersonByXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;

/**
 *<p>Title:SearchDynaTargetRankTreeTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Jul 4, 2008:2:54:45 PM</p> 
 *@author JinChunhai
 *@version 1.0
 */

public class SearchDynaTargetRankTreeTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		String planid = (String)this.getFormHM().get("planid");
//		String opt = (String)this.getFormHM().get("optSring");
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String opt=(String)hm.get("optString");
		OrgPersonByXml myOrg = new OrgPersonByXml("", "", "", planid, this.userView);
		String sql = "select codeitemid,codesetid from organization where 1=1 " + myOrg.getRootOrgNodeStr(myOrg.userview, myOrg.plan_b0110);
		sql+=" order by codeitemid";
		String topOrg = "";//机构树展示的顶层结构的第一个 list页面显示第一个的权重列表
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			this.frowset=dao.search(sql);
			if(this.frowset.next())
				topOrg = this.frowset.getString(2)+this.frowset.getString(1);
		} catch (SQLException e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		this.getFormHM().put("codeid", topOrg);
		this.getFormHM().put("optSring", opt);
		/*
		String planid = (String)this.getFormHM().get("planid");
		String object_type = (String)this.getFormHM().get("object_type");
		String template_id = (String)this.getFormHM().get("template_id");
		TreeItemView treeItem=new TreeItemView();
		String action="/performance/implement/kh_object/set_dyna_target_rank/searchdynatargetpropotion.do"; 
		String target="mil_body";
		//String treetype="duty";//org,duty,employee,noum
		treeItem.setName("root");		
		treeItem.setIcon("/images/unit.gif");	
		treeItem.setTarget(target);
		String rootdesc=ResourceFactory.getProperty("tree.orgroot.orgdesc");
	    treeItem.setRootdesc(rootdesc);
	    String username = this.userView.getUserName();
	    String password = this.userView.getPassWord();
		treeItem.setLoadChieldAction("/performance/implement/kh_mainbody/PremainTree?params=root&parentid=00&object_type="+object_type+"&planid="+planid+"&action=" + action + "&target=" + target+"&template_id="+template_id+"&username="+username+"&password="+password);
		
	   	treeItem.setAction("javascript:void(0)");
	    this.getFormHM().put("tartreeCode",treeItem.toJS());*/
	}

}
