package com.hjsj.hrms.transaction.smartphone;

import com.hjsj.hrms.servlet.ServletUtilities;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PersonInfoTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		this.test();
		String dbpre=(String)this.getFormHM().get("nbase");
		String a0100=(String)this.getFormHM().get("a0100");
		String a0101 = (String) this.getFormHM().get("a0101");
		StringBuffer html = new StringBuffer();
		//html.append("<hrms:personInfo a0100=\""+a0100+"\" dbpre=\""+dbpre+"\" a0101=\""+a0101+"\"></hrms:personInfo>");
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			Map map = (Map) this.userView.getHm().get("basicinfo_template");
			Map setsMap = (Map) this.userView.getHm().get("setsMap");
			html
					.append("<div data-role=\"page\" id=\"smain\"  data-inset=\"true\"><div data-role=\"header\"  data-position=\"fixed\" data-position=\"inline\"><a href=\"#mainbar\" data-role=\"button\" data-icon=\"forward\">返回</a>");
			html.append("<h1>" + a0101
					+ "</h1></div><div data-role=\"content\">");
			FieldSet fieldset = DataDictionary.getFieldSetVo("A01");
			html.append("<div data-role=\"collapsible\">");
			if(map==null||fieldset==null){
				html.append("<h3>人员基本信息</h3>");
				html.append("人员主集未构库或未设置显示人员主集信息!");
				html.append("</div>");
			}else{
				html.append("<h3>"+fieldset.getCustomdesc()+"</h3>");
				StringBuffer sql = new StringBuffer();
				String basicinfo_template = (String) map
						.get("basicinfo_template");
				Map mapsets = (Map) map.get("mapsets");
				Map mapsetstr = (Map) map.get("mapsetstr");
				for (Iterator i = mapsets.keySet().iterator(); i.hasNext();) {
					String setid = (String) i.next();
					List itemids = (List) mapsets.get(setid);
					String itemidstr = ((StringBuffer) mapsetstr.get(setid))
							.substring(1);
					sql.setLength(0);
					sql.append("select " + itemidstr + " from " + dbpre + setid
							+ " where a0100='" + a0100 + "'");
					if (!"A01".equals(setid))
						sql.append(" and i9999=(select max(i9999) from "
								+ dbpre + setid + " where a0100='" + a0100
								+ "')");
					this.frowset = dao.search(sql.toString());
					if (this.frowset.next()) {
						for (int n = 0; n < itemids.size(); n++) {
							String itemid = (String) itemids.get(n);
							FieldItem fielditem = DataDictionary
									.getFieldItem(itemid);
							String itemtype = fielditem.getItemtype();
							String value = "";
							Object obj = null;
							if ("N".equals(itemtype)) {
								value = String.valueOf(this.frowset.getInt(itemid));
							} else if ("D".equals(itemtype)) {
								obj = this.frowset.getDate(itemid);
								value = String.valueOf(obj==null?"":obj);
								value=value.replace('-', '.');
							} else if ("A".equals(itemtype)) {
								String codesetid = fielditem.getCodesetid();
								value = this.frowset.getString(itemid);
								value=value==null?"":value;
								if (!(codesetid.length() == 0 || "0"
										.equals(codesetid))) {
									value = com.hrms.frame.utility.AdminCode
											.getCodeName(codesetid, value);
								}
							}
							basicinfo_template = basicinfo_template.replace("["
									+ itemid + "]", value);
						}
					} else {
						for (int n = 0; n < itemids.size(); n++) {
							String itemid = (String) itemids.get(n);
							basicinfo_template = basicinfo_template.replace("["
									+ itemid + "]", "");
						}
					}
				}
				String filename=ServletUtilities.createOleFile(dbpre+"A00",a0100,this.getFrameconn());	
				html.append("<p><a href=\"javascript:getCard('"+dbpre+"','"+a0100+"','"+a0101+"');\" ><img src=\"/servlet/DisplayOleContent?filename="+filename+"\" width=\"85px\" border=0 style=\"position: relative\" style=\"border: 0\"/></a></p>");
				/*StringBuffer tmpstr=new StringBuffer(basicinfo_template);
				for(int i=basicinfo_template.length();i<60;i++){
					tmpstr.append("&nbsp;");
				}
				html.append(tmpstr.toString());*/
				html.append(basicinfo_template);
				html.append("</div>");
			}
			if(setsMap!=null){
				for(Iterator i=setsMap.keySet().iterator();i.hasNext();){
					String setid = (String)i.next();
					fieldset = DataDictionary.getFieldSetVo(setid);
					if(fieldset!=null){
						List itemids = (List)setsMap.get(setid);
						for(int m=0;m<itemids.size();m++){
							String itemid = (String)itemids.get(m);
							FieldItem item = DataDictionary.getFieldItem(itemid);
							if(item==null){
								itemids.remove(m);
								continue;
							}
						}
						if(itemids.size()==0)
							continue;
						StringBuffer sql = new StringBuffer();
						sql.append("select "+itemids.toString().substring(1,itemids.toString().length()-1));
						sql.append(" from "+dbpre+setid+" where a0100='"+a0100+"' order by i9999 desc");
						try{
							this.frowset = dao.search(sql.toString());
						}catch(Exception e){
							continue;
						}
						if(this.frowset.next()){
							html.append("<div data-role=\"collapsible\" data-collapsed=\"true\">");
							html.append("<h3>"+fieldset.getCustomdesc()+"</h3>");
							html.append("<table border=\"0\">");
							for(int n=0;n<itemids.size();n++){
								String itemid = (String)itemids.get(n);
								FieldItem fielditem = DataDictionary
								.getFieldItem(itemid);
								String itemtype = fielditem.getItemtype();
								String value = "";
								Object obj=null;
								if ("N".equals(itemtype)) {
									value = String.valueOf(this.frowset.getInt(itemid));
								} else if ("D".equals(itemtype)) {
									obj=this.frowset.getDate(itemid);
									value = String.valueOf(obj==null?"":obj);
									value=value.replace('-', '.');
								} else if ("A".equals(itemtype)) {
									String codesetid = fielditem.getCodesetid();
									value = this.frowset.getString(itemid);
									value=value==null?"":value;
									if (!(codesetid.length() == 0 || "0"
											.equals(codesetid))) {
										value = com.hrms.frame.utility.AdminCode
												.getCodeName(codesetid, value);
									}
								}
								html.append("<tr>");
								html.append("<td  align=\"right\" nowrap=\"nowrap\">");
								html.append(fielditem.getItemdesc()+"：");
								html.append("</td>");
								html.append("<td align=\"left\">");
								html.append(value);
								html.append("</td>");
								html.append("</tr>");
							}
							html.append("</table>");
							while(this.frowset.next()){
								html.append("<hr>");
								html.append("<table border=\"0\">");
								for(int n=0;n<itemids.size();n++){
									String itemid = (String)itemids.get(n);
									FieldItem fielditem = DataDictionary
									.getFieldItem(itemid);
									String itemtype = fielditem.getItemtype();
									String value = "";
									Object obj = null;
									if ("N".equals(itemtype)) {
										value = String.valueOf(this.frowset.getInt(itemid));
									} else if ("D".equals(itemtype)) {
										obj = this.frowset.getDate(itemid);
										value = String.valueOf(obj==null?"":obj);
										value=value.replace('-', '.');
									} else if ("A".equals(itemtype)) {
										String codesetid = fielditem.getCodesetid();
										value = this.frowset.getString(itemid);
										value = value==null?"":value;
										if (!(codesetid.length() == 0 || "0"
												.equals(codesetid))) {
											value = com.hrms.frame.utility.AdminCode
													.getCodeName(codesetid, value);
										}
									}
									html.append("<tr>");
									html.append("<td  align=\"right\" nowrap=\"nowrap\">");
									html.append(fielditem.getItemdesc()+"：");
									html.append("</td>");
									html.append("<td align=\"left\">");
									html.append(value);
									html.append("</td>");
									html.append("</tr>");
									//html.append(fielditem.getItemdesc()+"："+value+"<br/>");
								}
								html.append("</table>");
							}
							html.append("</div>");
						}
					}
				}
			}
			html.append("</div></div>");
		} catch (Exception ge) {
			ge.printStackTrace();
		} finally {
			this.getFormHM().put("html", html.toString());
		}
	}

	private void test(){
		/**
		UserView userView = this.getUserView();
		Connection conn = this.getFrameconn();
		SearchInformationClassBo searchInformationClassBo = new SearchInformationClassBo(userView, conn);
		// 获得人员库前缀
		String mNbase = "usr";
		// 获得人员编号
		String mA0100 = "00000023";
		List subSetNameList = searchInformationClassBo.getSubSetDetailed(mNbase,mA0100,"A07");
		*/
	}
}
