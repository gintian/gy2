package com.hjsj.hrms.transaction.stat.history;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

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
		String backdates=(String)hm.get("backdates");
		hm.put("scan_table",viewTable(backdates));
	}
	public String viewTable(String view_scan){
		StringBuffer tableview = new StringBuffer();
		String allbackdates = (String)this.getFormHM().get("allbackdates");
		tableview.append("<table width='70%' border='0' cellspacing='0' cellpadding='0'>");
			String[] tmpbackdates= allbackdates.split(",");
			for(int i=0;i<tmpbackdates.length;i++){
				String backdate = tmpbackdates[i];
				if(backdate.length()>7){
					tableview.append("<tr><td>");
					tableview.append("<input type='checkbox' name='");
					tableview.append(backdate);
					tableview.append("' value='");
					tableview.append(backdate);
					tableview.append("' onclick='checkSelect();'");
					if(view_scan.indexOf(backdate)!=-1){
						tableview.append(" checked ");
					}
					tableview.append(">");
					tableview.append(backdate);
					tableview.append("</td></tr>");
				}
			}
		tableview.append("<tr><td>&nbsp;</td></tr>");
		tableview.append("</table>");
		return tableview.toString();
	}
}
