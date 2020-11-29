package com.hjsj.hrms.transaction.org.autostatic.confset;

import com.hjsj.hrms.businessobject.pos.posparameter.PosparameXML;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
  */
public class SetScanDataTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = this.getFormHM();
		HashMap reqhm = (HashMap) hm.get("requestPamaHM");
		/*String view_scan = (String)reqhm.get("view_scan");
		reqhm.remove("view_scan");*/
		String flag = (String)reqhm.get("flag");
		PosparameXML pos = new PosparameXML(this.frameconn); 
		String view_scan = pos.getNodeAttributeValue("/params/view_scan", flag);
		view_scan=view_scan!=null&&view_scan.trim().length()>1?view_scan:"Usr,";
		hm.put("view_scan",view_scan);
		hm.put("scan_table",viewTable(view_scan));
	}
	public String viewTable(String view_scan){
		StringBuffer tableview = new StringBuffer();
		//ArrayList dblist=this.userView.getPrivDbList();
		ArrayList dblist=DataDictionary.getDbpreList();
		DbNameBo dbvo=new DbNameBo(this.getFrameconn());
		tableview.append("<table width='70%' border='0' cellspacing='0' cellpadding='0'>");
		try {
			dblist=dbvo.getDbNameVoList(dblist);
			for(int i=0;i<dblist.size();i++){
				RecordVo dbname=(RecordVo)dblist.get(i);
				tableview.append("<tr><td>");
				tableview.append("<input type='checkbox' name='");
				tableview.append(dbname.getString("pre"));
				tableview.append("' value='");
				tableview.append(dbname.getString("pre"));
				tableview.append("' onclick='checkSelect();'");
				if(view_scan.indexOf(dbname.getString("pre"))!=-1){
					tableview.append(" checked ");
				}
				tableview.append(">");
				tableview.append(dbname.getString("dbname"));
				tableview.append("</td></tr>");
			}
			
		} catch (GeneralException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		tableview.append("<tr><td>&nbsp;</td></tr>");
		tableview.append("</table>");
		return tableview.toString();
	}
}
