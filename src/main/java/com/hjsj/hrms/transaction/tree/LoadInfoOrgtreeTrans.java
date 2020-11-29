/*
 * Created on 2005-6-22
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.tree;

import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LoadInfoOrgtreeTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub 
		TreeItemView treeItem=new TreeItemView();
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
		List infoSetList=userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);   //获得所有权限的子集
		String action=(String)hm.get("action"); 
		String target=(String)hm.get("target");
		String privtype=(String)hm.get("viewPost");//权限类型，如果是kq就使用考勤权限	
		String treetype=(String)hm.get("treetype");//org,duty,employee,noum
		String issuperuser=(String)hm.get("issuperuser");
		String nmodule=(String)hm.get("nmodule");
		if(issuperuser==null||issuperuser.length()<=0)
			issuperuser="0";
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
	    String privCode=userView.getManagePrivCode();
	    String privCodeValue=userView.getManagePrivCodeValue();
	    if(privtype!=null&& "kq".equals(privtype))
	    {
	    	privCode=RegisterInitInfoData.getKqPrivCode(userView);
	    	privCodeValue=RegisterInitInfoData.getKqPrivCodeValue(userView);
	    }
	    if(userView.isSuper_admin()) {
    		treeItem.setLoadChieldAction("/common/org/loadtree?params=root&parentid=00&issuperuser=1&treetype=" + treetype + "&action=" + action + "&target=" + target + "&manageprive=" + privCode + privCodeValue+"&nmodule="+nmodule);
	    }
		else
		{
			if(userView.getStatus()==4 || userView.getStatus()==0) {
				treeItem.setLoadChieldAction("/common/org/loadtree?params=root&parentid=00&issuperuser="+issuperuser+"&treetype=" + treetype + "&action=" + action + "&target=" + target + "&manageprive=" + privCode + privCodeValue+"&nmodule="+nmodule);
			} else {
				treeItem.setLoadChieldAction("/common/org/loadtree?params=root&parentid=00&issuperuser="+issuperuser+"&treetype=" + treetype + "&action=" + action + "&target=" + target + "&manageprive=" + privCode + "no"+"&nmodule="+nmodule);
			}
			if("UN".equals(privCode))
		    	kind="2";
		    else if("UM".equals(privCode))
		    	kind="1";
		    else if("@K".equals(privCode))
		    	kind="0";
		}
	    if(!"org".equals(treetype) && !"duty".equals(treetype))
	        treeItem.setAction(action + "?b_search=link&isroot=1&code=" + privCodeValue + "&kind=" + kind+"&jump=1");
	    else
	    	treeItem.setAction("javascript:void(0)");
	    String isFire=(String)hm.get("isFire");
	    if(isFire!=null&& "noOpen".equals(isFire))
	    {
	    	this.getFormHM().put("treeCode",treeItem.toJS());
	    	hm.remove("isFire");
	    }
	    else
	    	this.getFormHM().put("treeCode",treeItem.toJS());
//	    System.out.println(treeItem.toJS());
	   
	    /**应用库过滤前缀符号*/
        ArrayList dblist=userView.getPrivDbList();
        StringBuffer cond=new StringBuffer();
        cond.append("select pre,dbname from dbname where pre in (");
        String userbase="";
        if(dblist.size()>0){
        	userbase=dblist.get(0).toString();      
        }
        else
        	userbase="usr";
        for(int i=0;i<dblist.size();i++)
        {
        	
        	if(i!=0)
                cond.append(",");
            cond.append("'");
            cond.append((String)dblist.get(i));
            cond.append("'");
        }
        if(dblist.size()==0)
            cond.append("''");
        cond.append(")");
        cond.append(" order by dbid");
        /**应用库前缀过滤条件*/
        cat.debug("-----userbase------>" + userbase);
        this.getFormHM().put("userbase",userbase);
        this.getFormHM().put("dbcond",cond.toString());
        this.getFormHM().put("setprv",getEditSetPriv(infoSetList,"A01"));
	}
	/**
	 * 求对子集修改权限，具体算法根据子集权限和指标权限进行分析．
	 * @param infoSetList
	 * @param infoFieldSetList
	 * @param setname
	 * @return
	 */
	private String getEditSetPriv(List infoSetList,String setname)
	{
		String setpriv="1";
		boolean bflag=false;
		/**先根据子集分析*/
		for(int p=0;p<infoSetList.size();p++)
		{
			FieldSet fieldset=(FieldSet)infoSetList.get(p);
			if(setname.equalsIgnoreCase(fieldset.getFieldsetid()))
			{
				setpriv=String.valueOf(fieldset.getPriv_status());
				break;
			}
		}	
	  return setpriv;	
	}
}
