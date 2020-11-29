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

public class LoadKqReportOrgtreeTrans extends IBusiness {

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
		String treetype=(String)hm.get("treetype");//org,duty,employee,noum
		String privtype=(String)hm.get("privtype");//权限类型，如果是kq就使用考勤权限	
		String self_flag=(String)hm.get("self_flag"); //self：自助  tran:部门
		
		// 组织机构是否显示岗位 2011-07-14
		String viewPost = (String) hm.get("viewPost");
		
		hm.remove("self_flag");
		self_flag = self_flag == null ? "" : self_flag;
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
	    	if("self".equalsIgnoreCase(self_flag))
	    	{
	    		privCodeValue=userView.getUserDeptId();
	    		if ("".equals(privCode)) {
	    			privCode = "UM";
	    		}
	    	}else
	    	{
	    		privCodeValue=RegisterInitInfoData.getKqPrivCodeValue(userView);
	    	}
//	    	privCodeValue=userView.getUserDeptId();
//	    	if (privCodeValue ==null || privCodeValue.length() <= 0) {
//	    		privCodeValue = userView.getUnit_id();
//	    	}
	    }
	    if(userView.isSuper_admin()) {
	    	if("self".equalsIgnoreCase(self_flag)){
	    		privCode = "UM";
	    		privCodeValue=userView.getUserDeptId();
	    		if ("kq".equalsIgnoreCase(viewPost)) //组织机构树是否显示
	    			treeItem.setLoadChieldAction("/common/org/kqreportloadtree?params=root&parentid=00&issuperuser=0&treetype=" + treetype + "&action=" + action + "&viewPost="+viewPost+"&target=" + target + "&manageprive=" + privCode + privCodeValue);
	    		else
	    			treeItem.setLoadChieldAction("/common/org/kqreportloadtree?params=root&parentid=00&issuperuser=0&treetype=" + treetype + "&action=" + action + "&target=" + target + "&manageprive=" + privCode + privCodeValue);
	    				
	    	} else {
	    		treeItem.setLoadChieldAction("/common/org/kqreportloadtree?params=root&parentid=00&issuperuser=1&treetype=" + treetype + "&action=" + action + "&target=" + target + "&manageprive=" + privCode + privCodeValue);
	    	}
		}
		else
		{
			if(userView.getStatus()==4 || userView.getStatus()==0){
				if ("kq".equalsIgnoreCase(viewPost)) //组织机构树是否显示
					treeItem.setLoadChieldAction("/common/org/kqreportloadtree?params=root&parentid=00&issuperuser=0&treetype=" + treetype + "&action=" + action + "&viewPost="+viewPost+"&target=" + target + "&manageprive=" + privCode + privCodeValue);
				else
					treeItem.setLoadChieldAction("/common/org/kqreportloadtree?params=root&parentid=00&issuperuser=0&treetype=" + treetype + "&action=" + action + "&target=" + target + "&manageprive=" + privCode + privCodeValue);
			}else
			    treeItem.setLoadChieldAction("/common/org/kqreportloadtree?params=root&parentid=00&issuperuser=0&treetype=" + treetype + "&action=" + action + "&target=" + target + "&manageprive=" + privCode + "no");
			if("UN".equals(privCode))
		    	kind="2";
		    else if("UM".equals(privCode))
		    	kind="1";
		    else if("@K".equals(privCode))
		    	kind="0";
		}
	    if(!"org".equals(treetype) && !"duty".equals(treetype))
	        treeItem.setAction(action + "?b_search=link&isroot=1&code=" + privCodeValue + "&kind=" + kind);
	    else
	    	treeItem.setAction("javascript:void(0)");
	    this.getFormHM().put("treeCode",treeItem.toJS());
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
        this.getFormHM().put("userbase",userbase);
        this.getFormHM().put("dbcond",cond.toString());
        this.getFormHM().put("setprv",getEditSetPriv(infoSetList,"A01"));
        this.getFormHM().put("code", privCodeValue);
        this.getFormHM().put("kind", kind);
        this.getFormHM().put("self_flag", self_flag);
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
