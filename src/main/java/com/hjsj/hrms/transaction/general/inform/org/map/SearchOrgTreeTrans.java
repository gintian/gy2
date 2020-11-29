/*
 * Created on 2006-3-24
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.general.inform.org.map;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.tools.ant.util.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

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
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
		String busiPriv = (String)hm.get("busiPriv");
		busiPriv=busiPriv==null?"1":busiPriv;
		String backdate=(String)this.getFormHM().get("backdate");
		if(backdate==null||backdate.length()<=0)
			backdate=DateUtils.format(new Date(), "yyyy-MM-dd");
		
		TreeItemView treeItem=new TreeItemView();
		
		// 如果是新机构图，走新的action
		String isyfiles = (String)this.getFormHM().get("isyfiles");
		String action="showorgmap.do"; 
		  if(isyfiles != null && "1".equals(isyfiles))
			  action = "showyFilesOrgMap.do";
		String target="mil_body";
		String treetype="employee";//org,duty,employee,noum
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
	    if(userView.isSuper_admin())
		    treeItem.setLoadChieldAction("/common/vorg/loadtree?busiPriv="+busiPriv+"&params=root&parentid=00&issuperuser=1&treetype=" + treetype + "&action=" + action + "&target=" + target + "&manageprive=" + userView.getManagePrivCode() + userView.getManagePrivCodeValue()+"&backdate="+backdate);
		else
		{
			if(userView.getStatus()==4 || userView.getStatus()==0)
				treeItem.setLoadChieldAction("/common/vorg/loadtree?busiPriv="+busiPriv+"&params=root&parentid=00&issuperuser=0&treetype=" + treetype + "&action=" + action + "&target=" + target + "&manageprive=" + userView.getManagePrivCode() + userView.getManagePrivCodeValue()+"&backdate="+backdate);
		    else
			    treeItem.setLoadChieldAction("/common/vorg/loadtree?busiPriv="+busiPriv+"&params=root&parentid=00&issuperuser=0&treetype=" + treetype + "&action=" + action + "&target=" + target + "&manageprive=" + userView.getManagePrivCode() + "no&backdate="+backdate);
			if("UN".equals(userView.getManagePrivCode()))
		    	kind="2";
		    else if("UM".equals(userView.getManagePrivCode()))
		    	kind="1";
		    else if("@K".equals(userView.getManagePrivCode()))
		    	kind="0";
		}
	    treeItem.setAction(action + "?b_search=link&isroot=1&code=" + /*userView.getManagePrivCodeValue() +*/"&kind=" + kind+"&orgtype=org&backdate="+backdate);
	    /**应用库过滤前缀符号*/
        ArrayList dblist=userView.getPrivDbList();
        StringBuffer cond=new StringBuffer();
        String dbname="";
        cond.append("select pre,dbname from dbname where pre in (");
        for(int i=0;i<dblist.size();i++)
        {
            if(i!=0)
                cond.append(",");
            else
            	dbname=(String)dblist.get(i);
            cond.append("'");
            cond.append((String)dblist.get(i));
            cond.append("'");
        }
        if(dblist.size()==0)
            cond.append("''");
        cond.append(")");
        cond.append(" order by dbid");
        /**应用库前缀过滤条件*/
        this.getFormHM().put("dbcond",cond.toString());
        this.getFormHM().put("dbname",dbname);
	    this.getFormHM().put("treeCode",treeItem.toJS());
	    this.getFormHM().put("report_relations","no");
	    this.getFormHM().put("ishistory", "false");
	}

}
