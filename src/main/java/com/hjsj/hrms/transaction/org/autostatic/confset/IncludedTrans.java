package com.hjsj.hrms.transaction.org.autostatic.confset;

import com.hjsj.hrms.businessobject.pos.posparameter.PosparameXML;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
  */
public class IncludedTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = this.getFormHM();
		HashMap reqhm = (HashMap) hm.get("requestPamaHM");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String fieldsetid = (String)reqhm.get("fieldsetid");
		reqhm.remove("fieldsetid");
		String flag=(String)reqhm.get("flag");
		reqhm.remove("flag");
		fieldsetid=fieldsetid!=null&&fieldsetid.length()>1?fieldsetid:"";
		String[] arr = fieldsetid.split("-");
		if(arr.length>1){
			fieldsetid = arr[1];
		}
		if(flag!=null&& "orgpre".equals(flag)){
			hm.put("included_table",viewTable1(dao,fieldsetid));
		}else{
			hm.put("included_table",viewTable(dao,fieldsetid));
		}
	}
	public String viewTable(ContentDAO dao,String fieldsetid){
		StringBuffer tableview = new StringBuffer();
		String sqlstr = "select itemid,itemdesc,expression from fielditem where fieldsetid='"+fieldsetid+"' and useflag=1";
		tableview.append("<table width='70%' border='0' cellspacing='0' cellpadding='0'>");
		try{
			ArrayList dylist = dao.searchDynaList(sqlstr);
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				
				String expr = (String)dynabean.get("expression");
				expr=expr!=null&&expr.trim().length()>0?expr:"";
				
				String itemid = (String)dynabean.get("itemid");
				String itemdesc = (String)dynabean.get("itemdesc");
				
				if(expr.length()<2&&!itemdesc.equals(ResourceFactory.getProperty("hmuster.label.nybs"))
						&&!itemdesc.equals(ResourceFactory.getProperty("hmuster.label.counts"))){
					tableview.append("<tr><td>");
					tableview.append("<input type='checkbox' name='");
					tableview.append(itemid);
					tableview.append("' value='");
					tableview.append(itemid);
					tableview.append("'>");
					tableview.append(itemdesc);
					tableview.append("</td></tr>");
				}
				if("K".equalsIgnoreCase(fieldsetid.substring(0,1))&&expr.trim().length()>1
						&& "3".equals(expr.substring(0,1))){
					tableview.append("<tr><td>");
					tableview.append("<input type='checkbox' name='");
					tableview.append(itemid);
					tableview.append("' value='");
					tableview.append(itemid);
					tableview.append("'>");
					tableview.append(itemdesc);
					tableview.append("</td></tr>");
				}
			}
			tableview.append("</table>");
		}catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tableview.toString();
	}
	public String viewTable1(ContentDAO dao,String fieldsetid){
		StringBuffer tableview = new StringBuffer();
		String sqlstr = "select itemid,itemdesc,expression from fielditem where fieldsetid='"+fieldsetid+"' and useflag=1";
		tableview.append("<table width='70%' border='0' cellspacing='0' cellpadding='0'>");
		try{
			ArrayList dylist = dao.searchDynaList(sqlstr);
			PosparameXML pos = new PosparameXML(this.frameconn); 
			String sp_flag=pos.getValue(PosparameXML.AMOUNTS,"sp_flag");
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				
				String expr = (String)dynabean.get("expression");
				expr=expr!=null&&expr.trim().length()>0?expr:"";
				
				String itemid = (String)dynabean.get("itemid");
				String itemdesc = (String)dynabean.get("itemdesc");
				
				if(expr.length()<2&&!itemdesc.equals(ResourceFactory.getProperty("hmuster.label.nybs"))&&!itemid.equals(sp_flag)
						&&!itemdesc.equals(ResourceFactory.getProperty("hmuster.label.counts"))){
					tableview.append("<tr><td>");
					tableview.append("<input type='checkbox' name='");
					tableview.append(itemid);
					tableview.append("' value='");
					tableview.append(itemid);
					tableview.append("'>");
					tableview.append(itemdesc);
					tableview.append("</td></tr>");
				}
				
			}
			tableview.append("</table>");
		}catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tableview.toString();
	}

}
