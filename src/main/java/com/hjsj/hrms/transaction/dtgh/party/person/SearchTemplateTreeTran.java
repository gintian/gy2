package com.hjsj.hrms.transaction.dtgh.party.person;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * 
 * @author xujian
 *Feb 11, 2010
 */
public class SearchTemplateTreeTran extends IBusiness {


	public void execute() throws GeneralException {

		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String type=(String)hm.get("type");	
		/**控制是否去掉人员调入业务分类
		 *=0去掉
		 *=1保留 
		 */
		String dr=(String)hm.get("dr");
		if(dr==null)
			dr="0";
		String select_id=(String)hm.get("select_id");
		if(select_id==null||select_id.length()<=0)
			select_id="";
		if(type==null|| "".equals(type))
			type="-1";
		String res_flag="7";
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
	    treeItem.setLoadChieldAction("/sys/warn/search_template?module=-1&type="+type+"&res_flag="+res_flag+"&select_id="+select_id+"&dr="+dr+"");
	    treeItem.setAction("javascript:void(0)");	   
	    try
	    {
		    this.getFormHM().put("bs_tree",treeItem.toJS());			    
	    }
	    catch(Exception ex)
	    {
	    	ex.printStackTrace();
	    	throw GeneralExceptionHandler.Handle(ex);
	    }		
	}

}
