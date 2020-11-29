/*
 * Created on 2006-2-20
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.general.inform.org;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * @author wlh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchOrgTreeTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub 
		TreeItemView treeItem=new TreeItemView();
		String action="searchorgbrowse.do"; 
		String target="mil_body";
		String treetype="org";//org,duty,employee,noum
		treeItem.setName("root");		
		treeItem.setIcon("/images/unit.gif");	
		String kind="2";
		treeItem.setTarget(target);
		String rootdesc="";
		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
		rootdesc=sysoth.getValue(Sys_Oth_Parameter.ORG_ROOT_DESC);
		if(rootdesc==null||rootdesc.length()<=0)
		{
			rootdesc=ResourceFactory.getProperty("tree.orgroot.orgdesc");
		}
	    treeItem.setRootdesc(rootdesc.replaceAll("&", "&amp;"));
	    if(userView.isSuper_admin())
		    treeItem.setLoadChieldAction("/common/org/loadtree?params=root&parentid=00&issuperuser=1&treetype=" + treetype + "&action=" + action + "&target=" + target + "&manageprive=" + userView.getManagePrivCode() + userView.getManagePrivCodeValue());
		else
		{
			if(userView.getStatus()==4 || userView.getStatus()==0)
				treeItem.setLoadChieldAction("/common/org/loadtree?params=root&parentid=00&issuperuser=0&treetype=" + treetype + "&action=" + action + "&target=" + target + "&manageprive=" + userView.getManagePrivCode() + userView.getManagePrivCodeValue());
		    else
			    treeItem.setLoadChieldAction("/common/org/loadtree?params=root&parentid=00&issuperuser=0&treetype=" + treetype + "&action=" + action + "&target=" + target + "&manageprive=" + userView.getManagePrivCode() + "no");
			if("UN".equals(userView.getManagePrivCode()))
		    	kind="2";
		    else if("UM".equals(userView.getManagePrivCode()))
		    	kind="1";
		    else if("@K".equals(userView.getManagePrivCode()))
		    	kind="0";
		}
	   	treeItem.setAction("javascript:void(0)");
	    this.getFormHM().put("treeCode",treeItem.toJS());
	    String sqlstr="";	
	    if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
	    	sqlstr="select min(b0110) as b0110 from b01 where b0110 like '" + userView.getManagePrivCodeValue() + "'";
	    else
	    	sqlstr="select min(b0110) as b0110 from b01";
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    String code="";
	    try{
	    	this.frowset=dao.search(sqlstr);
	    	if(this.frowset.next())
	    		code=this.frowset.getString("b0110");
	    }catch(Exception e)
		{
	    	e.printStackTrace();
	    	throw GeneralExceptionHandler.Handle(e);
	    }	   
	    this.getFormHM().put("nid",code);
	    this.getFormHM().put("infokind","2");
	}

}
