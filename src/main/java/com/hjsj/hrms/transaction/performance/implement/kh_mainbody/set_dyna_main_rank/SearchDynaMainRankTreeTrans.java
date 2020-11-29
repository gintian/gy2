package com.hjsj.hrms.transaction.performance.implement.kh_mainbody.set_dyna_main_rank;

import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.interfaces.performance.OrgPersonByXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;

/**
 *<p>Title:SearchDynaMainRankTreeTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Jun 4, 2008:10:51:15 AM</p> 
 *@author JinChunhai
 *@version 1.0
 */

public class SearchDynaMainRankTreeTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{	    
		String planid = (String)this.getFormHM().get("planid");
		CheckPrivSafeBo _bo = new CheckPrivSafeBo(this.frameconn,this.userView);
        if(!_bo.isHavePriv(this.userView, planid)){	
        	return;
        }
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
		this.getFormHM().put("optString",opt);	
			
		/*
		String planid = (String)hm.get("planid");
		PerformanceImplementBo pb=new PerformanceImplementBo(this.getFrameconn());
		RecordVo vo=pb.getPerPlanVo(planid);
		String object_type=String.valueOf(vo.getInt("object_type"));   //1部门 2：人员
		String template_id = vo.getString("template_id");
//		String planid = (String)this.getFormHM().get("planid");
//		String object_type = (String)this.getFormHM().get("object_type");	
		
		TreeItemView treeItem=new TreeItemView();
		String action="/performance/implement/kh_mainbody/set_dyna_main_rank/searchdynamainbodypropotion.do"; 
		String target="mil_body";
		//String treetype="duty";//org,duty,employee,noum
		treeItem.setName("roo274t");		
		treeItem.setIcon("/images/unit.gif");	
		treeItem.setTarget(target);
		String rootdesc=ResourceFactory.getProperty("tree.orgroot.orgdesc");
	    treeItem.setRootdesc(rootdesc);
	    
	    String privCode = this.userView.getManagePrivCode() + this.userView.getManagePrivCodeValue();// 管理范围
	    String username = this.userView.getUserName();
	    String password = this.userView.getPassWord();	 
	    username =  SafeCode.encode(username);
		treeItem.setLoadChieldAction("/performance/implement/kh_mainbody/PremainTree?params=root&parentid=00&object_type="+object_type+"&planid="+planid+"&action=" + action + "&target=" + target+"&privCode="+privCode+"&username="+username+"&password="+password);
		treeItem.setAction(action + "?b_search=link&planid=" + planid+"&codeid=root&template_id="+template_id);
//	   	treeItem.setAction("javascript:void(0)");
	    this.getFormHM().put("treeCode",treeItem.toJS());
	    */
	}

}
