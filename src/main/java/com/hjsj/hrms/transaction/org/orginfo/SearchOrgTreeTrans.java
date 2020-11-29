/*
 * Created on 2005-12-5
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.org.orginfo;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Administrator
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
		
		String backdate = (String)this.getFormHM().get("backdate");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		backdate = backdate!=null&&backdate.length()>9?backdate:sdf.format(new Date());
		TreeItemView treeItem=new TreeItemView();
	    String action="searchorglist.do"; 
		String target="mil_body";
		String treetype="org";//org,duty,employee
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
	    treeItem.setText(rootdesc);
	    treeItem.setTitle(rootdesc);
	    
	    String vflag = (String)this.getFormHM().get("vflag");
	    if(userView.isSuper_admin()){
	    	if("0".equalsIgnoreCase(vflag))
	    		treeItem.setLoadChieldAction("/common/org/loadtree?params=root&parentid=00&issuperuser=1&treetype=" + treetype + "&action=" + action + "&target=" + target + "&manageprive="/* + userView.getManagePrivCode() + userView.getManagePrivCodeValue()*/+"&backdate="+backdate);
	    	else
	    		treeItem.setLoadChieldAction("/common/vorg/loadtree?busiPriv=1&params=root&parentid=00&issuperuser=1&treetype=" + treetype + "&action=" + action + "&target=" + target + "&manageprive="/* + userView.getManagePrivCode() + userView.getManagePrivCodeValue()*/+"&backdate="+backdate);
	    }
		else
		{
			if(userView.getStatus()==4 || userView.getStatus()==0){
				if("0".equalsIgnoreCase(vflag))
					treeItem.setLoadChieldAction("/common/org/loadtree?params=root&parentid=00&issuperuser=0&treetype=" + treetype + "&action=" + action + "&target=" + target + "&manageprive=" + userView.getManagePrivCode() + userView.getManagePrivCodeValue()+"&backdate="+backdate);
				else
					treeItem.setLoadChieldAction("/common/vorg/loadtree?busiPriv=1&params=root&parentid=00&issuperuser=0&treetype=" + treetype + "&action=" + action + "&target=" + target + "&manageprive=" + userView.getManagePrivCode() + userView.getManagePrivCodeValue()+"&backdate="+backdate);
			}
		    else{
		    	if("0".equalsIgnoreCase(vflag))
		    		treeItem.setLoadChieldAction("/common/org/loadtree?params=root&parentid=00&issuperuser=0&treetype=" + treetype + "&action=" + action + "&target=" + target + "&manageprive=" + userView.getManagePrivCode() + "no"+"&backdate="+backdate);
		    	else
		    		treeItem.setLoadChieldAction("/common/vorg/loadtree?busiPriv=1&params=root&parentid=00&issuperuser=0&treetype=" + treetype + "&action=" + action + "&target=" + target + "&manageprive=" + userView.getManagePrivCode() + "no"+"&backdate="+backdate);
		    }
			if("UN".equals(userView.getManagePrivCode()))
		    	kind="2";
		    else if("UM".equals(userView.getManagePrivCode()))
		    	kind="1";
		    else if("@K".equals(userView.getManagePrivCode()))
		    	kind="0";
		}
	    if(userView.isSuper_admin()){
	    	treeItem.setAction(action + "?b_search=link&code=&kind=" + kind+"&root=1"+"&backdate="+backdate+"&orgtype=org&query=&idordesc=");
	    }else{
	    	treeItem.setAction(action + "?b_search=link&code=" +/* userView.getManagePrivCodeValue() +*/ "&kind=" + kind+"&root=1"+"&backdate="+backdate+"&orgtype=org&query=&idordesc=");
	    }
	    String labelmessage=ResourceFactory.getProperty("label.org.firstchildmessage") + 30;
	    this.getFormHM().put("kind",kind);
	    this.getFormHM().put("len","30");
	    this.getFormHM().put("labelmessage",labelmessage);
	    this.getFormHM().put("treeCode",treeItem.toJS());
	    this.getFormHM().put("isrefresh","no");	  
	    this.getFormHM().put("backdate", backdate);
	 }

}
