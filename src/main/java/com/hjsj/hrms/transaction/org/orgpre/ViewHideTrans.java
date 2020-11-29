package com.hjsj.hrms.transaction.org.orgpre;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 机构编制指标显示隐藏
 * @author xujian
 *
 */
public class ViewHideTrans extends IBusiness {

	public void execute() throws GeneralException {
		String fieldset = (String)this.getFormHM().get("setid");
		ArrayList itemlist = DataDictionary.getFieldList(fieldset, 1);
		StringBuffer sb = new StringBuffer(); 
		//PosparameXML pos = new PosparameXML(this.frameconn); 
		//String sp_flag = pos.getValue(PosparameXML.AMOUNTS,"sp_flag");
		for(int i=0;i<itemlist.size();i++){
			FieldItem fi = (FieldItem)itemlist.get(i);
			String itemid = fi.getItemid();
			//if(itemid.equalsIgnoreCase(sp_flag))
				//continue;
			String viewhide = "view";
			if(fi.getDisplaywidth()<1){
				viewhide = "hide";
			}
			sb.append(this.trStr(itemid, fi.getItemdesc(), viewhide, i,fi.getDisplaywidth()));
		}
		this.getFormHM().put("viewhide", sb.toString());
	}

	private String trStr(String itemid,String itemdesc,String viewhide,int m,int displaywidth){
		if(!"id".equalsIgnoreCase(itemid)&&!"b0110".equalsIgnoreCase(itemid)){
			if("0".equals(userView.analyseFieldPriv(itemid))
				|| "0".equals(userView.analyseFieldPriv(itemid,1))){
				return "";
			}
		}
		StringBuffer tableview = new StringBuffer();
		if(m%2==0){
			tableview.append("<tr class='trShallow'>");
		}else{
			tableview.append("<tr class='trDeep'>");
		}
		tableview.append("<td  class='RecordRow' align='center' style='border-left:0px;' nowrap>");
		tableview.append(itemdesc);
		tableview.append("</td><td align='center' class='RecordRow' style='border-right:0px;' nowrap>");
		tableview.append("<select name='");
		tableview.append(itemid);
		tableview.append("'>");
		tableview.append("<option value='"+itemid+":"+(displaywidth==0?8:displaywidth)+"' ");
		if("view".equalsIgnoreCase(viewhide))
			tableview.append("selected");
		tableview.append(">");
		tableview.append(ResourceFactory.getProperty("lable.channel.visible"));
		tableview.append("</option>");
		tableview.append("<option value='"+itemid+":0' ");
		if("hide".equalsIgnoreCase(viewhide))
			tableview.append("selected");
		tableview.append(">");
		tableview.append(ResourceFactory.getProperty("lable.channel.hide"));
		tableview.append("</option>");
		tableview.append("</select></td></tr>");
		
		return tableview.toString();
	}
}
