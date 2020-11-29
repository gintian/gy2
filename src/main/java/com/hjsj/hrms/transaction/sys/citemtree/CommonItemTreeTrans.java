package com.hjsj.hrms.transaction.sys.citemtree;

import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class CommonItemTreeTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub

		HashMap hm=this.getFormHM();
		HashMap reqhm=(HashMap)this.getFormHM().get("requestPamaHM");
		String type=(String) reqhm.get("type")==null?"":(String) reqhm.get("type");
		String fsid=(String) reqhm.get("fid")==null?"":(String)reqhm.get("fid");
		String url=(String) reqhm.get("url")==null?"":(String)reqhm.get("url");
		String urlay=(String) reqhm.get("urlay")==null?"":(String)reqhm.get("urlay");
		String checkbox=(String)reqhm.get("checkbox")==null?"":(String)reqhm.get("checkbox");
		String checkvalue=(String)reqhm.get("checkvalue")==null?"":(String)reqhm.get("checkvalue");
		String target=(String)reqhm.get("target")==null?"":(String)reqhm.get("target");
		String xml=this.getXmlRoot(fsid,type,url,urlay);	
		System.out.println(xml);
		TreeItemView treeItem = new TreeItemView();
		treeItem.setName("root");
		treeItem.setIcon("/images/group.gif");
		treeItem.setTarget(target);
		treeItem.setRootdesc("子集指标");
		treeItem.setText("子集指标");
		treeItem.setLoadChieldAction(xml);
		treeItem.setAction("");		
		String treecode=this.getCheckboxType(checkbox)+this.getCheckValue(checkvalue)+treeItem.toJS();
		System.out.println(treecode);
		hm.put("treecode",treecode);
	}
	private String getCheckboxType(String checkbox){
		return "Global.defaultInput="+checkbox+";Global.showroot=false;";
	}
	private String getCheckValue(String checkvalue){
		checkvalue=","+checkvalue;
		return "Global.checkvalue='"+checkvalue+",';";
	}
	private String getXmlRoot(String fsid,String type,String url,String urlay){
		StringBuffer xml=new StringBuffer();
		xml.append("/servlet/citemtree?param=root");
		xml.append("&fid="+fsid);
		xml.append("&type="+type);
		xml.append("&url="+url);
		xml.append("&urlay="+urlay);
		return xml.toString();
	}
}
