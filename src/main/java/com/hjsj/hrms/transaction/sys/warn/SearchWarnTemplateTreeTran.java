package com.hjsj.hrms.transaction.sys.warn;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * 预警设置模版
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Apr 3, 2008</p> 
 *@author sunxin
 *@version 4.0
 */
public class SearchWarnTemplateTreeTran extends IBusiness {


	public void execute() throws GeneralException {

		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String type=(String)hm.get("type");	
		hm.remove("type");
		/**控制是否去掉人员调入业务分类
		 *=0去掉
		 *=1保留 
		 */
		String dr=(String)hm.get("dr");
		hm.remove("dr");
		if(dr==null)
			dr="1";
		String select_id=(String)hm.get("select_id");
		String confine=(String)hm.get("confine");
		hm.remove("confine");
		if(confine==null||confine.length()<=0)
			confine="";
		if(select_id==null||select_id.length()<=0)
			select_id="";
		if(type==null|| "".equals(type))
			type="-1";
		String res_flag="8";//业务分类   薪酬管理 获取的权限不对     wangb 20190610 bug 48653
		TreeItemView treeItem=new TreeItemView();		
		treeItem.setName("root");
		treeItem.setRootdesc("root");
		treeItem.setTitle("root");
		treeItem.setIcon("/images/add_all.gif");	
		treeItem.setTarget("il_body");
		String rootdesc=ResourceFactory.getProperty("label.bos.rsbd");
		if("2".equals(type))
			rootdesc=ResourceFactory.getProperty("label.bos.gzbd");
		if("8".equals(type))
			rootdesc=ResourceFactory.getProperty("sys.res.ins_bd");
		rootdesc=ResourceFactory.getProperty("system.operation.template");
	    treeItem.setRootdesc(rootdesc);
		treeItem.setText(rootdesc); 
	    treeItem.setLoadChieldAction("/sys/warn/search_template?module=-1&type="+type+"&res_flag="+res_flag+"&select_id="+select_id+"&dr="+dr+"&confine="+confine);
	    treeItem.setAction("javascript:void(0)");	   
	    try
	    {
	    	this.getFormHM().put("select_id", select_id);
	    	this.getFormHM().put("confine", confine);
		    this.getFormHM().put("bs_tree",treeItem.toJS());			    
	    }
	    catch(Exception ex)
	    {
	    	ex.printStackTrace();
	    	throw GeneralExceptionHandler.Handle(ex);
	    }		
	}

}
