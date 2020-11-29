/**
 * 
 */
package com.hjsj.hrms.transaction.general.template.historydata;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:SearchTemplateTreeTrans</p>
 * <p>Description:查询业务模板树交易</p> 
 * <p>Company:hjsj</p> 
 * create time at:Sep 26, 20061:08:52 PM
 * @author chenmengqing
 * @version 4.0
 */
public class SearchTemplateTreeTrans extends IBusiness {


	public void execute() throws GeneralException {
		String type=(String)this.getFormHM().get("type");
		if(type==null|| "".equals(type))
			type="1";
		String res_flag=(String)this.getFormHM().get("res_flag");
		TreeItemView treeItem=new TreeItemView();
		treeItem.setName("root");
		treeItem.setRootdesc("root");
		treeItem.setTitle("root");
		treeItem.setIcon("/images/add_all.gif");	
		treeItem.setTarget("il_body");
		String rootdesc=ResourceFactory.getProperty("sys.res.rsbd");
		if("2".equals(type))
			rootdesc=ResourceFactory.getProperty("sys.res.gzbd");
		if("8".equals(type))
			rootdesc=ResourceFactory.getProperty("sys.res.ins_bd");
			
	    treeItem.setRootdesc(rootdesc);
		treeItem.setText(rootdesc); 
	    treeItem.setLoadChieldAction("/template/search_template?module=-1&type="+type+"&res_flag="+res_flag);
	    treeItem.setAction("javascript:void(0)");	   
	    try
	    {
	    	this.getFormHM().put("bs_tree",treeItem.toJS());	  
	    	/*******得到启用模板的分类******/
	    	/*SubsysOperation subsysOperation=new SubsysOperation(this.getFrameconn());
	    	HashMap map = subsysOperation.getMap();
	    	String isOpen="";
	    	if(type.equals("1"))
	    		isOpen = (String)map.get("37");//37是人事异动
	    	else if(type.equals("2"))
	    		isOpen = (String)map.get("34");//34是薪资管理
	    	String openseal="0";
	    	if(isOpen!=null&&isOpen.equals("true")){
	    		if(type.equals("1"))
	    			openseal = "37";
	    		else if(type.equals("2"))
	    			openseal = "34";
	    	}
	    	this.getFormHM().put("openseal", openseal);	  */
	    }
	    catch(Exception ex)
	    {
	    	ex.printStackTrace();
	    	throw GeneralExceptionHandler.Handle(ex);
	    }	
	    
	}

}
