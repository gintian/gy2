package com.hjsj.hrms.transaction.sys.options.interfaces;

import com.hjsj.hrms.businessobject.sys.options.otherparam.OtherParam;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
import java.util.Map;
/**
 * 
 * <p>Title:平台接口组织结构树</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jul 13, 2009:1:51:17 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class OrgTreeTrans extends IBusiness{

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		String froms=(String) reqhm.get("froms");
		String defaultInput="1";
		String input=(String)reqhm.get("input");
		if(input!=null&&input.length()>0)
			defaultInput=input;
		String xml="/servlet/orgsettree?param=root";
		if("item".equals(froms)){
			String codese=(String) reqhm.get("codesetid");
			String[] c=codese.split("/");
			xml="/servlet/orgsettree?param=root&codesetid="+c[1];
		}
		TreeItemView treeItem = new TreeItemView();
		treeItem.setName("root");
		treeItem.setIcon("/images/group.gif");
		treeItem.setTarget("");
		treeItem.setRootdesc("组织机构指标");
		treeItem.setText("组织机构指标");
		treeItem.setLoadChieldAction(xml);
		treeItem.setAction("");
//		UserView uv=this.getUserView();
		String checkvalue=",";
		try{
			OtherParam op=new OtherParam(this.getFrameconn());
			if("item".equals(froms)){
				String name=(String) reqhm.get("name");
				String codefield=(String) reqhm.get("codesetid");
				String[] codefields=codefield.split("/");
				String codesetid=codefields[0];
				String feild=codefields[1];
				Map myMap=op.getEmployeeType(feild,codesetid,name);
				checkvalue=(String)myMap.get("table")+(String)myMap.get("field");
				reqhm.remove("codesetid");
				reqhm.remove("name");
			}else{
				String name=(String) reqhm.get("name");
				reqhm.remove("name");
				Map myMap=op.getBaseFieldMap(name);
				checkvalue=(String)myMap.get("table")+(String)myMap.get("field");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		hm.put("treecode","Global.checkvalue='"+checkvalue+",';"+"Global.defaultInput="+defaultInput+";Global.showroot=false;"+treeItem.toJS());
//		System.out.println("ddd = "+treeItem.toJS());
	}

}
