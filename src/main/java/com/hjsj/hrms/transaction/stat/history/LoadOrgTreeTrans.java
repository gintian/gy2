package com.hjsj.hrms.transaction.stat.history;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
/**
 * 统计分析历史数据组织机构树
 * <p>Title:LoadOrgTreeTrans.java</p>
 * <p>Description>:LoadOrgTreeTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Mar 22, 2010 3:55:09 PM</p>
 * <p>@version: 4.0</p>
 * <p>@author: s.xin
 */
public class LoadOrgTreeTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	private String privtype="";
	 /**加载选项
    * =0（单位|部门|职位）
    * =1 (单位|部门)
    * =2 (单位)
    * */
   private String loadtype="0";
   /**选择方式
	 * =0,正常方式
	 * =1,checkbox
	 * =2,radio
	 * */
	private String selecttype="0";
	public void execute() throws GeneralException {
		TreeItemView treeItem=new TreeItemView();
		treeItem.setName("root");		
		treeItem.setIcon("/images/unit.gif");	
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
		String target=(String)hm.get("target");
		String action=(String)hm.get("action");
		if(action==null||action.length()<=0)
		   action="javascript:void(0)";
		String backdate = (String) hm.get("backdate");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		backdate = backdate!=null&&backdate.length()>9?backdate:sdf.format(new Date());
		treeItem.setAction("javascript:void(0)");
		treeItem.setTarget(target);
		String rootdesc="";
		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.frameconn);
		rootdesc=sysoth.getValue(Sys_Oth_Parameter.ORG_ROOT_DESC);
		if(rootdesc==null||rootdesc.length()<=0)
		{
			rootdesc=ResourceFactory.getProperty("tree.orgroot.orgdesc");
		}
	    treeItem.setRootdesc(rootdesc.replaceAll("&", "&amp;"));
	    treeItem.setText(rootdesc);
	    treeItem.setTitle(rootdesc);
	    
	    if(userView.isSuper_admin())
		    treeItem.setLoadChieldAction("/stat/history/loadtree?params=root&parentid=00&issuperuser=1&action="+action+"&target=" + target + "&manageprive=" + userView.getManagePrivCode() + userView.getManagePrivCodeValue()+"&selecttype=1&loadtype=1&backdate="+backdate+"");
		else
			treeItem.setLoadChieldAction("/stat/history/loadtree?params=root&parentid=00&issuperuser=0&action="+action+"&target=" + target + "&manageprive=" + userView.getManagePrivCode() + userView.getManagePrivCodeValue()+"&selecttype=1&loadtype=1&backdate="+backdate+"");
	    
	    this.getFormHM().put("treeCode",treeItem.toJS());
	}

}
