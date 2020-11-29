package com.hjsj.hrms.transaction.tree;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.sys.VersionControl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoadInfoVOrgtreeTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub 
		TreeItemView treeItem=new TreeItemView();
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
		String multimedia_file_flag="";
		List infoSetList=userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);   //获得所有权限的子集
		String action=(String)hm.get("action"); 
		String target=(String)hm.get("target");
		String treetype=(String)hm.get("treetype");//org,duty,employee,noum
		String loadtype=(String)hm.get("loadtype");	 /**加载选项  * =0（单位|部门|职位）   * =1 (单位|部门)   * =2 (单位) * */
		if(loadtype==null||loadtype.length()<=0)
			loadtype="0";
		String busiPriv = (String)hm.get("busiPriv");
		busiPriv=busiPriv==null?"1":busiPriv;
		treeItem.setName("root");		
		treeItem.setIcon("/images/unit.gif");	
		String kind="2";
		treeItem.setTarget(target);
		String rootdesc="";
		String b_searchsort = (String)hm.get("b_searchsort");
		String isRecordEntry = "";
		if("showinfodata.do".equals(action)&&"link".equals((String)hm.get("b_search"))){
			isRecordEntry =  "browse";
		}else if("showinfodata.do".equals(action)&&"link".equals((String)hm.get("b_searchsort"))){
			isRecordEntry =  "entry";
		}
		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
		rootdesc=sysoth.getValue(Sys_Oth_Parameter.ORG_ROOT_DESC);
		if(rootdesc==null||rootdesc.length()<=0)
		{
			rootdesc=ResourceFactory.getProperty("tree.orgroot.orgdesc");
		}
	    treeItem.setRootdesc(rootdesc.replaceAll("&", "&amp;"));
	    treeItem.setText(rootdesc);
	    treeItem.setTitle(rootdesc);
	    String backdate = (String)this.getFormHM().get("backdate");
	    if(userView.isSuper_admin())
		    treeItem.setLoadChieldAction("/common/vorg/loadtree?busiPriv="+busiPriv+"&params=root&parentid=00&issuperuser=1&loadtype="+loadtype+"&treetype=" + treetype + "&action=" + action + "&target=" + target + "&manageprive=" + userView.getManagePrivCode() + userView.getManagePrivCodeValue()+"&backdate="+backdate+"&isRecordEntry="+isRecordEntry);
		else
		{
			if(userView.getStatus()==4 || userView.getStatus()==0)
				treeItem.setLoadChieldAction("/common/vorg/loadtree?busiPriv="+busiPriv+"&params=root&parentid=00&issuperuser=0&loadtype="+loadtype+"&treetype=" + treetype + "&action=" + action + "&target=" + target + "&manageprive=" + userView.getManagePrivCode() + userView.getManagePrivCodeValue()+"&backdate="+backdate+"&isRecordEntry="+isRecordEntry);
		    else
			    treeItem.setLoadChieldAction("/common/vorg/loadtree?busiPriv="+busiPriv+"&params=root&parentid=00&issuperuser=0&loadtype="+loadtype+"&treetype=" + treetype + "&action=" + action + "&target=" + target + "&manageprive=" + userView.getManagePrivCode() + "no&backdate="+backdate+"&isRecordEntry="+isRecordEntry);
			if("UN".equals(userView.getManagePrivCode()))
		    	kind="2";
		    else if("UM".equals(userView.getManagePrivCode()))
		    	kind="1";
		    else if("@K".equals(userView.getManagePrivCode()))
		    	kind="0";
		}
	    if(!"org".equals(treetype) && !"duty".equals(treetype))
	    	if(this.userView.isSuper_admin())
	    	  treeItem.setAction(action + "?b_search=link&isroot=1&code=" + "" + "&kind=" + kind);
	    	else
	          treeItem.setAction(action + "?b_search=link&isroot=1&code=" + userView.getManagePrivCodeValue() + "&kind=" + kind);
	    else
	    	treeItem.setAction("javascript:void(0)");
	    this.getFormHM().put("treeCode",treeItem.toJS());
	    /**应用库过滤前缀符号*/
        ArrayList dblist=userView.getPrivDbList();
        StringBuffer cond=new StringBuffer();
        cond.append("select pre,dbname from dbname where pre in (");
        String userbase=(String)this.getFormHM().get("userbase");
        if((userbase==null||userbase.length()<1)&&dblist.size()>0){
        	userbase=dblist.get(0).toString();      
        }
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
        for(int p=0;p<infoSetList.size();p++)
		{
			FieldSet fieldset=(FieldSet)infoSetList.get(p);
			if("A01".equalsIgnoreCase(fieldset.getFieldsetid()))
			{
				multimedia_file_flag = fieldset.getMultimedia_file_flag();
				break;
			}
		}
        
        VersionControl ver_ctrl = new VersionControl();
         if(!ver_ctrl.searchFunctionId("03040110")){ 
            multimedia_file_flag="";   
        }
        /**应用库前缀过滤条件*/
        cat.debug("-----userbase------>" + userbase);
        this.getFormHM().put("userbase",userbase);
        this.getFormHM().put("dbcond",cond.toString());
        this.getFormHM().put("multimedia_file_flag", multimedia_file_flag);
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