package com.hjsj.hrms.taglib.smartphone;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import org.apache.struts.taglib.TagUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PersonInfoTag extends BodyTagSupport {

	private String dbpre;
	private String a0100;
	private String a0101;

	/**
	 * <div data-role="header"> 
		 * <a href="#mainbar" data-role="button" data-icon="forward">返回</a>
		 * <h1>王广言</h1>
	 * </div>
	 * <div data-role="content" id="scard"> 
	 * <div data-role="collapsible">
	 * <h3>人员基本信息</h3>
	 * <p>
	 * 男，蒙古族，55岁(1953-10-26出生)，山西省太原市市辖区，[入党 时间]入党，1977-12-26参加工作。
	 * </p>
	 * </div> 
		 * <div data-role="collapsible" data-collapsed="true">
		 * 	<h3>学历子集</h3>
			 * <p>
			 * 学历：本科<br/>毕业时间：2009-6-30<br/>毕业院校：河北金融学院<hr/>学历：本科<br/>毕业时间：2009-6-30<br/>毕业院校：河北金融学院
			 * </p>
		 * </div> 
	 * </div> 
	 */
	public int doStartTag() throws JspException {
		Connection conn = null;
		RowSet rs = null;
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			Map map = (Map) TagUtils.getInstance().lookup(pageContext,
					"sphoneForm", "basicinfo_template", "session");
			Map setsMap = (Map) TagUtils.getInstance().lookup(pageContext,
					"sphoneForm", "setsMap", "session");
			StringBuffer html = new StringBuffer();
			html
					.append("<div data-role=\"header\"><a href=\"#mainbar\" data-role=\"button\" data-icon=\"forward\">返回</a>");
			html.append("<h1>" + a0101
					+ "</h1></div><div data-role=\"content\">");
			FieldSet fieldset = DataDictionary.getFieldSetVo("A01");
			html.append("<div data-role=\"collapsible\">");
			if(map==null||fieldset==null){
				html.append("<h3>人员基本信息</h3>");
				html.append("<p>人员主集未构库或设置显示人员主集信息!</p>");
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
					rs = dao.search(sql.toString());
					if (rs.next()) {
						for (int n = 0; n < itemids.size(); n++) {
							String itemid = (String) itemids.get(n);
							FieldItem fielditem = DataDictionary
									.getFieldItem(itemid);
							String itemtype = fielditem.getItemtype();
							String value = "";
							Object obj=null;
							if ("N".equals(itemtype)) {
								value = String.valueOf(rs.getInt(itemid));
							} else if ("D".equals(itemtype)) {
								obj = rs.getDate(itemid);
								value = String.valueOf(obj==null?"":obj);
							} else if ("A".equals(itemtype)) {
								String codesetid = fielditem.getCodesetid();
								value = rs.getString(itemid);
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
				html.append("<p>"+basicinfo_template+"</p>");
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
						sql.append(" from "+dbpre+setid+" where a0100='"+a0100+"'");
						try{
							rs = dao.search(sql.toString());
						}catch(Exception e){
							continue;
						}
						if(rs.next()){
							html.append("<div data-role=\"collapsible\" data-collapsed=\"true\">");
							html.append("<h3>"+fieldset.getCustomdesc()+"</h3>");
							html.append("<p>");
							for(int n=0;n<itemids.size();n++){
								String itemid = (String)itemids.get(n);
								FieldItem fielditem = DataDictionary
								.getFieldItem(itemid);
								String itemtype = fielditem.getItemtype();
								String value = "";
								Object obj=null;
								if ("N".equals(itemtype)) {
									value = String.valueOf(rs.getInt(itemid));
								} else if ("D".equals(itemtype)) {
									obj = rs.getDate(itemid);
									value = String.valueOf(obj==null?"":obj);
								} else if ("A".equals(itemtype)) {
									String codesetid = fielditem.getCodesetid();
									value = rs.getString(itemid);
									value=value==null?"":value;
									if (!(codesetid.length() == 0 || "0"
											.equals(codesetid))) {
										value = com.hrms.frame.utility.AdminCode
												.getCodeName(codesetid, value);
									}
								}
								html.append(fielditem.getItemdesc()+"："+value+"<br/>");
							}
							while(rs.next()){
								html.append("<hr>");
								for(int n=0;n<itemids.size();n++){
									String itemid = (String)itemids.get(n);
									FieldItem fielditem = DataDictionary
									.getFieldItem(itemid);
									String itemtype = fielditem.getItemtype();
									String value = "";
									Object obj=null;
									if ("N".equals(itemtype)) {
										value = String.valueOf(rs.getInt(itemid));
									} else if ("D".equals(itemtype)) {
										obj = rs.getDate(itemid);
										value = String.valueOf(obj==null?"":obj);
									} else if ("A".equals(itemtype)) {
										String codesetid = fielditem.getCodesetid();
										value = rs.getString(itemid);
										value = value==null?"":value;
										if (!(codesetid.length() == 0 || "0"
												.equals(codesetid))) {
											value = com.hrms.frame.utility.AdminCode
													.getCodeName(codesetid, value);
										}
									}
									html.append(fielditem.getItemdesc()+"："+value+"<br/>");
								}
							}
							html.append("<p></div>");
						}
					}
				}
			}
			html.append("</div>");
			pageContext.getOut().println(html.toString());
			return EVAL_BODY_BUFFERED;
		} catch (Exception ge) {
			ge.printStackTrace();
			return 0;
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}

	public String getDbpre() {
		return dbpre;
	}

	public void setDbpre(String dbpre) {
		this.dbpre = dbpre;
	}

	public String getA0100() {
		return a0100;
	}

	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}

	public String getA0101() {
		return a0101;
	}

	public void setA0101(String a0101) {
		this.a0101 = a0101;
	}

}
