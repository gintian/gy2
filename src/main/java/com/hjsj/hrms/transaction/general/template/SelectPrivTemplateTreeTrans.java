/**
 * 
 */
package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.sys.SysPrivBo;
import com.hjsj.hrms.constant.GeneralConstant;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.hjsj.sys.ResourceParser;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:查询权限范围查板</p>
 * <p>Description:</p> 
 * <p>Company:hjsj</p> 
 * create time at:Nov 22, 20066:31:03 PM
 * @author chenmengqing
 * @version 4.0
 */
public class SelectPrivTemplateTreeTrans extends IBusiness {

	public void execute() throws GeneralException {
		String type=(String)this.getFormHM().get("type");
		String flag=(String)this.getFormHM().get("flag");
		String roleid=(String)this.getFormHM().get("roleid");
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String sorttype=(String)hm.get("sorttype");
		hm.remove("sorttype");
		if(type==null|| "".equals(type))
			type="1";
		String res_flag=(String)this.getFormHM().get("res_flag");
		TreeItemView treeItem=new TreeItemView();
		treeItem.setName("root");
		treeItem.setRootdesc("root");
		treeItem.setTitle("root");
		treeItem.setIcon("/images/add_all.gif");	
		treeItem.setTarget("il_body");
		String rootdesc=ResourceFactory.getProperty("label.bos.rsbd");
		if("2".equals(type))
		{
			rootdesc=ResourceFactory.getProperty("sys.res.gzbd");
		}
		else if("8".equals(type)){
			rootdesc=ResourceFactory.getProperty("sys.res.ins_bd");
		}
		else if("10".equals(type)){
			rootdesc=ResourceFactory.getProperty("sys.res.card");
		}
		else if("11".equals(type)){
			rootdesc=ResourceFactory.getProperty("sys.res.tjb");
		}
		else if("14".equals(type)){
			rootdesc=ResourceFactory.getProperty("sys.res.muster");
		}
		else if("15".equals(type)){
			rootdesc=ResourceFactory.getProperty("sys.res.hmuster");
		}
	    treeItem.setRootdesc(rootdesc);
		treeItem.setText(rootdesc);
		if(sorttype==null)
			treeItem.setLoadChieldAction("/template/search_template?module=-1&type="+type+"&res_flag="+res_flag+"&href=1");
		else
			treeItem.setLoadChieldAction("/template/search_template?sorttype=1&type="+type+"&res_flag="+res_flag+"&href=1");
	    treeItem.setAction("javascript:void(0)");	   
	    try
	    {
		    this.getFormHM().put("bs_tree",treeItem.toJS());
		    /**资源查询*/
			if(flag==null|| "".equals(flag))
	            flag=GeneralConstant.ROLE;
			if(res_flag==null|| "".equals(res_flag))
				res_flag="0";
			/**资源类型*/
			int res_type=Integer.parseInt(res_flag);
			/**采用预警字段作为其资源控制字段*/
			/**当前被授权用户拥有的资源*/
			SysPrivBo privbo=new SysPrivBo(roleid,flag,this.getFrameconn(),"warnpriv");
			String res_str=privbo.getWarn_str();
			ResourceParser parser=new ResourceParser(res_str,res_type);
			/**1,2,3*/
			String str_content=","+parser.getContent()+",";
			this.getFormHM().put("law_dir",str_content);		    
	    }
	    catch(Exception ex)
	    {
	    	ex.printStackTrace();
	    	throw GeneralExceptionHandler.Handle(ex);
	    }		

	}

}
