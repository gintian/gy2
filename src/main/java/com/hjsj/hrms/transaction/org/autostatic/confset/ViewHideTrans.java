package com.hjsj.hrms.transaction.org.autostatic.confset;

import com.hjsj.hrms.businessobject.org.autostatic.confset.ViewHideSortBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
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
public class ViewHideTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = this.getFormHM();
		HashMap reqhm = (HashMap) hm.get("requestPamaHM");
		String subset = (String)reqhm.get("subset");
		reqhm.remove("subset");
		subset=subset!=null?subset:"";
		
		this.getFormHM().put("subset", subset);
		this.getFormHM().put("view_hide", viewTable(subset));
	}
	public String viewTable(String fieldsetid){
		StringBuffer tableview = new StringBuffer();
		ContentDAO dao = new ContentDAO(this.frameconn);
		ViewHideSortBo vsbo = new ViewHideSortBo(dao,this.userView,fieldsetid);
		String hideitemid=vsbo.getHideitemid();
		String sortitem=vsbo.getSortitem();
		
		ArrayList fieldset = this.userView.getPrivFieldList(fieldsetid,Constant.USED_FIELD_SET);
		if(hideitemid.length()<1){
			int m=0;
			if(!fieldsetid.startsWith("K")){
				FieldItem fi=DataDictionary.getFieldItem("B0110");
				tableview.append(trStr(fi.getItemid(),fi.getItemdesc(),"view",m));
				m++;
			}else{
				FieldItem fi=new FieldItem();
				fi.setItemid("B0110");
				fi.setFieldsetid("UM");
				fi.setItemdesc(ResourceFactory.getProperty("column.sys.dept"));
				tableview.append(trStr(fi.getItemid(),fi.getItemdesc(),"view",m));
				m++;

				FieldItem efi=DataDictionary.getFieldItem("E01A1");		
				tableview.append(trStr(efi.getItemid(),efi.getItemdesc(),"view",m));
				m++;
			}
			FieldItem fi=new FieldItem();
			fi.setItemid("id");
			fi.setItemdesc(ResourceFactory.getProperty("hmuster.label.nybs"));
			tableview.append(trStr(fi.getItemid(),fi.getItemdesc(),"view",m));
			m++;
			for(int i=0;i<fieldset.size();i++){
				FieldItem fielditem = (FieldItem)fieldset.get(i);
				if(!fielditem.getItemdesc().equals(ResourceFactory.getProperty("hmuster.label.nybs"))){
					tableview.append(trStr(fielditem.getItemid(),fielditem.getItemdesc(),"view",m));
					m++;
				}
			}
		}else{
			
			if(sortitem!=null&&sortitem.trim().length()>4){
				String[] sort = sortitem.split(",");
				int m=0;
				for(int i=0;i<sort.length;i++){
					if(sort[i]!=null&&sort[i].trim().length()>0){
						FieldItem fi= null;
						if(!fieldsetid.startsWith("K")){
							if("id".equalsIgnoreCase(sort[i])){
								fi=new FieldItem();
								fi.setItemid("id");
								fi.setItemdesc(ResourceFactory.getProperty("hmuster.label.nybs"));
							}else{
								fi=DataDictionary.getFieldItem(sort[i]);
							}
						}else{
							if("B0110".equalsIgnoreCase(sort[i])){
								fi=new FieldItem();
								fi.setItemid("B0110");
								fi.setItemdesc(ResourceFactory.getProperty("column.sys.dept"));
							}else if("id".equalsIgnoreCase(sort[i])){
								fi=new FieldItem();
								fi.setItemid("id");
								fi.setItemdesc(ResourceFactory.getProperty("hmuster.label.nybs"));
							}else{
								fi=DataDictionary.getFieldItem(sort[i]);
							}
						}

						if(hideitemid.toUpperCase().indexOf(fi.getItemid().toUpperCase())!=-1){
							tableview.append(trStr(fi.getItemid(),fi.getItemdesc(),"view",m));
						}else{
							tableview.append(trStr(fi.getItemid(),fi.getItemdesc(),"hide",m));
						}
						m++;
						
					}
				}
				//当有新指标添加了，排序中未有此指标时：xuj 2010-4-7
				for(int i=0;i<fieldset.size();i++){
					FieldItem fielditem = (FieldItem)fieldset.get(i);
					if(!fielditem.getItemdesc().equals(ResourceFactory.getProperty("hmuster.label.nybs"))){
						if(sortitem.toUpperCase().indexOf(fielditem.getItemid().toUpperCase())==-1){
							if(hideitemid.toUpperCase().indexOf(fielditem.getItemid().toUpperCase())!=-1){
								tableview.append(trStr(fielditem.getItemid(),fielditem.getItemdesc(),"view",m));
							}else{
								tableview.append(trStr(fielditem.getItemid(),fielditem.getItemdesc(),"hide",m));
							}
							m++;
						}
					}
				}
			}else{
				int m=0;
				if(!fieldsetid.startsWith("K")){
					FieldItem fi=DataDictionary.getFieldItem("B0110");
					if(hideitemid.toUpperCase().indexOf(fi.getItemid().toUpperCase())!=-1){
						tableview.append(trStr(fi.getItemid(),fi.getItemdesc(),"view",m));
					}else{
						tableview.append(trStr(fi.getItemid(),fi.getItemdesc(),"hide",m));
					}
					m++;
				}else{
					FieldItem fi=new FieldItem();
					fi.setItemid("B0110");
					fi.setFieldsetid("UM");
					fi.setItemdesc(ResourceFactory.getProperty("column.sys.dept"));
					if(hideitemid.toUpperCase().indexOf(fi.getItemid().toUpperCase())!=-1){
						tableview.append(trStr(fi.getItemid(),fi.getItemdesc(),"view",m));
					}else{
						tableview.append(trStr(fi.getItemid(),fi.getItemdesc(),"hide",m));
					}
					m++;

					FieldItem efi=DataDictionary.getFieldItem("E01A1");		
					if(hideitemid.toUpperCase().indexOf(efi.getItemid().toUpperCase())!=-1){
						tableview.append(trStr(efi.getItemid(),efi.getItemdesc(),"view",m));
					}else{
						tableview.append(trStr(efi.getItemid(),efi.getItemdesc(),"hide",m));
					}
					m++;

				}
				FieldItem fi=new FieldItem();
				fi.setItemid("id");
				fi.setItemdesc(ResourceFactory.getProperty("hmuster.label.nybs"));
				
				if(hideitemid.toUpperCase().indexOf(fi.getItemid().toUpperCase())!=-1){
					tableview.append(trStr(fi.getItemid(),fi.getItemdesc(),"view",m));
				}else{
					tableview.append(trStr(fi.getItemid(),fi.getItemdesc(),"hide",m));
				}
				m++;
				for(int i=0;i<fieldset.size();i++){
					FieldItem fielditem = (FieldItem)fieldset.get(i);
					if(!fielditem.getItemdesc().equals(ResourceFactory.getProperty("hmuster.label.nybs"))){
						
						if(hideitemid.toUpperCase().indexOf(fielditem.getItemid().toUpperCase())!=-1){
							tableview.append(trStr(fielditem.getItemid(),fielditem.getItemdesc(),"view",m));
						}else{
							tableview.append(trStr(fielditem.getItemid(),fielditem.getItemdesc(),"hide",m));
						}
						m++;
					}
				}
			}
		}
		return tableview.toString();
	}
	private String trStr(String itemid,String itemdesc,String viewhide,int m){
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
		tableview.append("<td  class='RecordRow' style='border-left: 0px;' align='center' nowrap>");
		tableview.append(itemdesc);
		tableview.append("</td><td align='center' style='border-right: 0px;' class='RecordRow' nowrap>");
		tableview.append("<select name='");
		tableview.append(itemid);
		tableview.append("'>");
		tableview.append("<option value='"+itemid+"' ");
		if("view".equalsIgnoreCase(viewhide))
			tableview.append("selected");
		tableview.append(">");
		tableview.append(ResourceFactory.getProperty("lable.channel.visible"));
		tableview.append("</option>");
		tableview.append("<option value='0' ");
		if("hide".equalsIgnoreCase(viewhide))
			tableview.append("selected");
		tableview.append(">");
		tableview.append(ResourceFactory.getProperty("lable.channel.hide"));
		tableview.append("</option>");
		tableview.append("</select></td></tr>");
		
		return tableview.toString();
	}
}
