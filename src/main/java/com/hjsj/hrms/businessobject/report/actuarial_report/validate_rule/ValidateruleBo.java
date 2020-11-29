package com.hjsj.hrms.businessobject.report.actuarial_report.validate_rule;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:填报单位类
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Jun 12, 2006:4:28:24 PM
 * </p>
 * 
 * @author dengcan
 * @version 1.0
 * 
 */
public class ValidateruleBo {
	Connection conn = null;

	public ValidateruleBo(Connection conn) {
		this.conn = conn;
	}

	public ValidateruleBo() {

	}

	
	/**
	 * 生成报表状态表头信息
	 * 
	 * @param reportSetList
	 * @return
	 */
	public String getTheadHtml(ArrayList reportSetList) {
		StringBuffer html = new StringBuffer("");
		html
				.append("<thead><tr><td  width='100'   valign='top' align='center' class='TableRow' rowspan='2'  nowrap ><br>人员范围<br>&nbsp;</td>");
		html
				.append("<td  width='100' class='TableRow'  valign='top' align='center'  rowspan='2'  nowrap ><br>人员分类<br>&nbsp;</td>");
		String[] subclass = new String[reportSetList.size()];
		for (int i = 0; i < reportSetList.size(); i++) {
			// 进行判断是否是时间格式

			subclass[i] = (String) reportSetList.get(i);
			FieldItem fielditem = DataDictionary.getFieldItem(subclass[i]
					.toUpperCase());
			if (fielditem != null && "D".equals(fielditem.getItemtype())) {
				html
						.append("<td  class='TableRow' valign='top' align='center' valign='top'  width='400' colspan='2'><br>");
				html.append(fielditem.getItemdesc());
				//html.append("<br>(yyyymm)</td>");
				html.append("<br>&nbsp;</td>");
			} else {
				html.append("<td  class='TableRow'  valign='top' align='center'  width='100'><br>");
				html.append(fielditem.getItemdesc());
				//if(fielditem.getItemdesc().indexOf("年")!=-1){
				//html.append("<br>(元/年)</td>");
				//}else if(fielditem.getItemdesc().indexOf("月")!=-1){
				//	html.append("<br>(元/月)</td>");
				//}else{
				//	html.append("<br>(元)</td>");
				//}
				html.append("<br>&nbsp;</td>");
			}
		}
		html.append("</tr><tr>");
		ArrayList list = new ArrayList();
		list.add(ResourceFactory.getProperty("report.validate_rule.upper_limit"));

		for (int i = 0; i < reportSetList.size(); i++) {
			subclass[i] = (String) reportSetList.get(i);
			FieldItem fielditem = DataDictionary.getFieldItem(subclass[i]
					.toUpperCase());
			if (fielditem != null && "D".equals(fielditem.getItemtype())) {
				html.append("<td class='TableRow' align='center' width='100' nowrap>");
				html.append(ResourceFactory
						.getProperty("report.validate_rule.upper_limit"));
				html.append("</td>");
				html.append("<td class='TableRow' align='center' width='100' nowrap>");
				html.append(ResourceFactory
						.getProperty("report.validate_rule.down_limit"));
				html.append("</td>");
			} else {
				html.append("<td class='TableRow' align='center' width='100' nowrap>");
				html.append(ResourceFactory
						.getProperty("report.validate_rule.upper_limit"));
				html.append("</td>");
			}

		}

		html.append("</tr></thead>");
		return html.toString();
	}

	/**
	 * 取得表格内容
	 * 
	 * @param subUnitList
	 * @param parmcopyList
	 * @param reportSetList
	 * @return
	 */
	public String getTabBody(ArrayList subUnitList, StringBuffer parmcopyList,
			ArrayList reportSetList) {
		StringBuffer bodyHtml = new StringBuffer("");
		ArrayList list = new ArrayList();
		list.add("-1");
		list.add("0");

		HashMap countMap = new HashMap();
		String a_className = "";
		// 先进行结构同步,然后数据同步
		syncGzField("tt_updown_rule");
		for (int i = 0; i < subUnitList.size(); i++) {
			DynaBean bean = (DynaBean) subUnitList.get(i);
			// subUnitList.get(i);
			DecimalFormat myformat1 = new DecimalFormat("###########.#####");//
			String unitcode = (String) bean.get("codeitemid");
			String unitname = (String) bean.get("codeitemdesc");
			String className = "trDeep";
			String persontype = "";
			if (i % 2 == 0) {
                className = "trShallow";
            }
			a_className = className;
			String color = "#F3F5FC";
			if ("trDeep".equals(className)) {
                color = "#DDEAFE";
            }
			bodyHtml.append("<tr class='" + className
					+ "' onClick='javascript:tr_onclick(this,\"" + color
					+ "\")'   >");
			bodyHtml
					.append("<td align='center' class='RecordRow' nowrap>&nbsp;&nbsp;");
			bodyHtml.append("&nbsp;&nbsp;" + unitname);
			bodyHtml.append("</td>");
			bodyHtml
					.append("<td align='center' class='RecordRow' nowrap>&nbsp;&nbsp;");
			if (i % 2 == 0) {
                persontype = "原有人员";
            } else {
                persontype = "新增人员";
            }
			bodyHtml.append("&nbsp;&nbsp;" + persontype);
			bodyHtml.append("</td>");
			
			
			ContentDAO dao = new ContentDAO(conn);
		
			String[] subclass = new String[reportSetList.size()];
			String fieldname_u = "";
			String fieldname_d = "";
			String fieldvalue_u="";
			String fieldvalue_d="";
			for (int j = 0; j < reportSetList.size(); j++) {
				String value = "";
				subclass[j] = (String) reportSetList.get(j);
				FieldItem fielditem = DataDictionary.getFieldItem(subclass[j]
						.toUpperCase());
				if (fielditem != null && "D".equals(fielditem.getItemtype())) {
					fieldname_u = fielditem.getItemid() + "_u";
					fieldname_d = fielditem.getItemid() + "_d";
					 parmcopyList.append(fieldname_u+"_"+unitcode+"_"+i%2);
					 parmcopyList.append(",");
					 parmcopyList.append(fieldname_d+"_"+unitcode+"_"+i%2);
					 parmcopyList.append(",");
					 
					try {
						ResultSet rs = dao
								.search("select "+fieldname_u+","+fieldname_d+" from tt_updown_rule  where emtype='"+unitcode+"' and emflag='"+i % 2+"' ");
						if(rs.next()){
						ResultSetMetaData data=rs.getMetaData();
						
						
							 int columnType=data.getColumnType(1);
							 if(columnType==java.sql.Types.TIMESTAMP){
								
								 fieldvalue_u= rs.getTimestamp(fieldname_u)==null?"": PubFunc.FormatDate(String.valueOf(rs.getTimestamp(fieldname_u)));
								 fieldvalue_d= rs.getTimestamp(fieldname_d)==null?"": PubFunc.FormatDate(String.valueOf(rs.getTimestamp(fieldname_d)));
								 if(!"".equals(fieldvalue_u)&&fieldvalue_u.indexOf(".")!=-1) {
                                     fieldvalue_u= fieldvalue_u.replace(".", "-");
                                 }
								 if(!"".equals(fieldvalue_d)&&fieldvalue_d.indexOf(".")!=-1) {
                                     fieldvalue_d= fieldvalue_d.replace(".", "-");
                                 }
								 bodyHtml
									.append("<td align='center' width='100' class='RecordRow' nowrap>");
								 bodyHtml
									.append("<input type=\"text\" style=\"width:100px\" name="+fieldname_u+"_"+unitcode+"_"+i%2+"  extra=\"editor\"  id=\"editor4\" dropDown=\"dropDownDate\"    size=\'22\' value=\""+fieldvalue_u+"\"/>");
							bodyHtml.append("</td>");
							 bodyHtml
								.append("<td align='center' width='100'  class='RecordRow' nowrap>");
							 bodyHtml
								.append("<input type=\"text\" style=\"width:100px\" name="+fieldname_d+"_"+unitcode+"_"+i%2+"  extra=\"editor\"  id=\"editor4\" dropDown=\"dropDownDate\"    size=\'22\' value=\""+fieldvalue_d+"\"/>");
						bodyHtml.append("</td>");
							 }else if(columnType==java.sql.Types.DATE){
								 fieldvalue_u= rs.getDate(fieldname_u)==null?"": PubFunc.FormatDate(rs.getDate(fieldname_u));
								 fieldvalue_d= rs.getDate(fieldname_d)==null?"": PubFunc.FormatDate(rs.getDate(fieldname_d));
								 bodyHtml
									.append("<td align='center' width='100'  class='RecordRow' nowrap>");
								 bodyHtml
									.append("<input type=\"text\" style=\"width:100px\" name="+fieldname_u+"_"+unitcode+"_"+i%2+"  extra=\"editor\"  id=\"editor4\" dropDown=\"dropDownDate\"    size=\'22\' value=\""+fieldvalue_u+"\"/>");
							bodyHtml.append("</td>");
							 bodyHtml
								.append("<td align='center' width='100'  class='RecordRow' nowrap>");
							 bodyHtml
								.append("<input type=\"text\" style=\"width:100px\" name="+fieldname_d+"_"+unitcode+"_"+i%2+"  extra=\"editor\"  id=\"editor4\" dropDown=\"dropDownDate\"    size=\'22\' value=\""+fieldvalue_d+"\"/>");
						bodyHtml.append("</td>");
							 }else if(columnType==java.sql.Types.TIME){
								 fieldvalue_u= rs.getTime(fieldname_u)==null?"": PubFunc.FormatDate(String.valueOf(rs.getTime(fieldname_u)));
								 fieldvalue_d= rs.getTime(fieldvalue_d)==null?"": PubFunc.FormatDate(String.valueOf(rs.getTime(fieldvalue_d)));
								 if(!"".equals(fieldvalue_u)&&fieldvalue_u.indexOf(".")!=-1) {
                                     fieldvalue_u= fieldvalue_u.replace(".", "-");
                                 }
								 if(!"".equals(fieldvalue_d)&&fieldvalue_d.indexOf(".")!=-1) {
                                     fieldvalue_d= fieldvalue_d.replace(".", "-");
                                 }
								 bodyHtml
									.append("<td align='center' width='100'  class='RecordRow' nowrap>");
								 bodyHtml
									.append("<input type=\"text\" style=\"width:100px\" name="+fieldname_u+"_"+unitcode+"_"+i%2+"  extra=\"editor\"  id=\"editor4\" dropDown=\"dropDownDate\"    size=\'22\' value=\""+fieldvalue_u+"\"/>");
							bodyHtml.append("</td>");
							 bodyHtml
								.append("<td align='center' width='100'  class='RecordRow' nowrap>");
							 bodyHtml
								.append("<input type=\"text\" style=\"width:100px\" name="+fieldname_d+"_"+unitcode+"_"+i%2+"  extra=\"editor\"  id=\"editor4\" dropDown=\"dropDownDate\"    size=\'22\' value=\""+fieldvalue_d+"\"/>");
						bodyHtml.append("</td>");
							 }
							
						 
						}else{
							bodyHtml
								.append("<td align='center' width='100'  class='RecordRow' nowrap>");
							 bodyHtml
								.append("<input type=\"text\" style=\"width:100px\" name="+fieldname_u+"_"+unitcode+"_"+i%2+"  extra=\"editor\"  id=\"editor4\" dropDown=\"dropDownDate\"    size=\'22\' value=\"\"/>");
						bodyHtml.append("</td>");
						 bodyHtml
							.append("<td align='center' width='100'  class='RecordRow' nowrap>");
						 bodyHtml
							.append("<input type=\"text\" style=\"width:100px\"  name="+fieldname_d+"_"+unitcode+"_"+i%2+"  extra=\"editor\"  id=\"editor4\" dropDown=\"dropDownDate\"    size=\'22\' value=\"\"/>");
					bodyHtml.append("</td>");
						}
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				
				} else {

					fieldname_u = fielditem.getItemid() + "_u";
					 parmcopyList.append(fieldname_u+"_"+unitcode+"_"+i%2);
					 parmcopyList.append(",");
					try {
						ResultSet rs = dao
								.search("select "+fieldname_u+" from tt_updown_rule  where emtype='"+unitcode+"' and emflag='"+i % 2+"' ");
						if(rs.next()){
						ResultSetMetaData data=rs.getMetaData();
							 int columnType=data.getColumnType(1);
							 if(columnType==java.sql.Types.FLOAT){
								 fieldvalue_u= rs.getFloat(fieldname_u)==0.0?"":""+rs.getFloat(fieldname_u);
								 bodyHtml
									.append("<td align='center' width='100'  class='RecordRow' nowrap>");
								 bodyHtml
									.append("<input type=\"text\" style=\"width:100px\" name="+fieldname_u+"_"+unitcode+"_"+i%2+"  extra=\"editor\"  id=\"editor4\"     size=\'22\' value=\""+fieldvalue_u+"\"/>");
							bodyHtml.append("</td>");
							 }else if(columnType==java.sql.Types.DOUBLE){
								 fieldvalue_u= rs.getDouble(fieldname_u)==0.0?"":""+rs.getDouble(fieldname_u);
								 bodyHtml
									.append("<td align='center' width='100'  class='RecordRow' nowrap>");
								 bodyHtml
									.append("<input type=\"text\" style=\"width:100px\" name="+fieldname_u+"_"+unitcode+"_"+i%2+"  extra=\"editor\"  id=\"editor4\"     size=\'22\'value=\""+fieldvalue_u+"\"/>");
							bodyHtml.append("</td>");
							 }else if(columnType==java.sql.Types.NUMERIC){
								 fieldvalue_u= myformat1.format(rs.getDouble(fieldname_u));
								 if("0".equals(fieldvalue_u)) {
                                     fieldvalue_u="";
                                 }
									 //System.out.println("fieldvalue_u"+rs.getDouble(fieldname_u));
								 bodyHtml
									.append("<td align='center' width='100'  class='RecordRow' nowrap>");
								 bodyHtml
									.append("<input type=\"text\" style=\"width:100px\" name="+fieldname_u+"_"+unitcode+"_"+i%2+"  extra=\"editor\"  id=\"editor4\"     size=\'22\' value=\""+fieldvalue_u+"\"/>");
							bodyHtml.append("</td>");
							 }else if(columnType==java.sql.Types.INTEGER){
								 fieldvalue_u= rs.getInt(fieldname_u)==0?"":""+rs.getInt(fieldname_u);
								 bodyHtml
									.append("<td align='center' width='100'  class='RecordRow' nowrap>");
								 bodyHtml
									.append("<input type=\"text\" style=\"width:100px\" name="+fieldname_u+"_"+unitcode+"_"+i%2+"  extra=\"editor\"  id=\"editor4\"     size=\'22\'value=\""+fieldvalue_u+"\"/>");
							     bodyHtml.append("</td>");
							 }
						}else{
							 bodyHtml
								.append("<td align='center' width='100'  class='RecordRow' nowrap>");
							 bodyHtml
								.append("<input type=\"text\" style=\"width:100px\" name="+fieldname_u+"_"+unitcode+"_"+i%2+"  extra=\"editor\"  id=\"editor4\"     size=\'22\' value=\"\"/>");
						bodyHtml.append("</td>");
						}
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				
				
				}

			}
			bodyHtml.append("</tr>");
		}

		return bodyHtml.toString();
	}

	
	public ArrayList getPersonscope() throws GeneralException {
		StringBuffer strsql = new StringBuffer();
		strsql.append("select * from codeitem where codesetid='61' ");
		ContentDAO dao = new ContentDAO(conn);

		ArrayList list = new ArrayList();

		try {
			RowSet rs = dao.search(strsql.toString());
			while (rs.next()) {
				list.add(rs.getString("codeitemid"));
			}

		} catch (Exception ee) {
			ee.printStackTrace();
			throw GeneralExceptionHandler.Handle(ee);
		}
		return list;
	}

	public String getPersonscopeName() throws GeneralException {
		StringBuffer strsql = new StringBuffer();
		strsql.append("select CodeSetDesc from codeSet where codesetid='61' ");
		ContentDAO dao = new ContentDAO(conn);

		String name = "";

		try {
			RowSet rs = dao.search(strsql.toString());
			while (rs.next()) {
				name = rs.getString("CodeSetDesc");
			}

		} catch (Exception ee) {
			ee.printStackTrace();
			throw GeneralExceptionHandler.Handle(ee);
		}
		return name;
	}

	public ArrayList getSelfSortList() {
		ArrayList list = new ArrayList();
		ArrayList fielditemlist = new ArrayList();

		fielditemlist = DataDictionary.getFieldList("U02",
				Constant.USED_FIELD_SET);
		if (fielditemlist != null) {
			for (int i = 0; i < fielditemlist.size(); i++) {
				if (fielditemlist.get(i) == null) {
                    continue;
                }
				// 先加日期类型
				FieldItem fielditem = (FieldItem) fielditemlist.get(i);
				if (fielditem.getItemtype() != null
						&& "D".equals(fielditem.getItemtype())) {
					// CommonData dataobj = new
					// CommonData(fielditem.getItemid(),fielditem.getItemdesc());
					list.add(fielditem.getItemid());
				}
			}
			for (int i = 0; i < fielditemlist.size(); i++) {
				if (fielditemlist.get(i) == null) {
                    continue;
                }
				// FieldItem item = (FieldItem)z03list.get(i);
				// 在加数值类型
				FieldItem fielditem = (FieldItem) fielditemlist.get(i);
				if (fielditem.getItemtype() != null
						&& "N".equals(fielditem.getItemtype())) {
					// CommonData dataobj = new
					// CommonData(fielditem.getItemid(),fielditem.getItemdesc());
					list.add(fielditem.getItemid());
				}
			}
		}
		return list;
	}

	public ArrayList getUnderUnitList(Connection con) {
		StringBuffer strsql = new StringBuffer();
		strsql
				.append("select * from codeitem where codesetid='61' order by codeitemid asc");
		ContentDAO dao = new ContentDAO(con);

		ArrayList list = new ArrayList();

		try {
			RowSet rs = dao.search(strsql.toString());
			while (rs.next()) {
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("codeitemid", rs.getString("codeitemid"));
				bean.set("codeitemdesc", rs.getString("codeitemdesc"));
				list.add(bean);
				list.add(bean);
			}

		} catch (Exception ee) {
			ee.printStackTrace();
		}
		return list;
	}
	private void  syncGzField(String tableName)
	{
		try
		{
			 ContentDAO dao=new ContentDAO(this.conn);
			
			 HashMap map=new HashMap();
			 ArrayList fieldlist = new ArrayList();

			 fieldlist = DataDictionary.getFieldList("U02",
						Constant.USED_FIELD_SET);
			 String name="";
			 ArrayList overlist = new ArrayList();
				if (fieldlist != null) {
					for (int i = 0; i < fieldlist.size(); i++) {
				 
				 FieldItem fielditem = (FieldItem) fieldlist.get(i);
					if (fielditem.getItemtype() != null
							&& "D".equals(fielditem.getItemtype())) {
						name=fielditem.getItemid().toLowerCase()+"_u";
						overlist.add(name);
						FieldItem tempItem=DataDictionary.getFieldItem(fielditem.getItemid());
						map.put(name, tempItem);
						name=fielditem.getItemid().toLowerCase()+"_d";
						overlist.add(name);
						 tempItem=DataDictionary.getFieldItem(fielditem.getItemid());
						map.put(name, tempItem);
					}
					else if (fielditem.getItemtype() != null
							&& "N".equals(fielditem.getItemtype())) {
						name=fielditem.getItemid().toLowerCase()+"_u";
						FieldItem tempItem=DataDictionary.getFieldItem(fielditem.getItemid());
						map.put(name, tempItem);
						overlist.add(name);
					}
					}
					}
			 RowSet rowSet=dao.search("select * from "+tableName+" where 1=2");
			 ResultSetMetaData data=rowSet.getMetaData();
			 HashMap mapcopy=map;
			
			 ArrayList alterList=new ArrayList();
			 ArrayList resetList=new ArrayList();
			 HashMap alterListmap=new HashMap();
			 HashMap resetListmap=new HashMap();
			 for(int i=1;i<=data.getColumnCount();i++)
			 {
					String columnName=data.getColumnName(i).toLowerCase();
					if(map.get(columnName)!=null)
					{
						FieldItem tempItem=(FieldItem)map.get(columnName);
						int columnType=data.getColumnType(i);	
						int scale=data.getScale(i);
						switch(columnType)
						{
						
						case java.sql.Types.TIMESTAMP:
							if(!"D".equals(tempItem.getItemtype()))
							{
								resetList.add(columnName);
								resetListmap.put(columnName, tempItem.cloneField());
							}
							break;
						case java.sql.Types.TIME :
							if(!"D".equals(tempItem.getItemtype()))
							{
								resetList.add(columnName);
								resetListmap.put(columnName, tempItem.cloneField());
							}
							break;
						case java.sql.Types.DATE  :
							if(!"D".equals(tempItem.getItemtype()))
							{
								resetList.add(columnName);
								resetListmap.put(columnName, tempItem.cloneField());
							}
							break;
						case java.sql.Types.FLOAT :
							if(!"N".equals(tempItem.getItemtype()))
							{
								resetList.add(columnName);
								resetListmap.put(columnName, tempItem.cloneField());
							}else {
								if(tempItem.getDecimalwidth()!=scale){
									alterList.add(columnName);
									alterListmap.put(columnName, tempItem.cloneField());
								}
							}
							
							break;
						case java.sql.Types.DOUBLE :
							if(!"N".equals(tempItem.getItemtype()))
							{
								resetList.add(columnName);
								resetListmap.put(columnName, tempItem.cloneField());
							}else {
								if(tempItem.getDecimalwidth()!=scale){
									alterList.add(columnName);
									alterListmap.put(columnName, tempItem.cloneField());
								}
							}
							
							break;
						case java.sql.Types.NUMERIC :
							if(!"N".equals(tempItem.getItemtype()))
							{
								resetList.add(columnName);
								resetListmap.put(columnName, tempItem.cloneField());
							}else {
								if(tempItem.getDecimalwidth()!=scale){
									alterList.add(columnName);
									alterListmap.put(columnName, tempItem.cloneField());
								}
							}
							
							break;
					}
						map.remove(columnName);
					}
				}
				rowSet.close();
				DbWizard dbw=new DbWizard(this.conn);
			    Table table=new Table(tableName);
			    if(Sql_switcher.searchDbServer()!=2)  //不为oracle
			    {
			    for(int i=0;i<alterList.size();i++){
					 Field field = (Field)alterListmap.get(alterList.get(i));
						field.setName(alterList.get(i).toString().toLowerCase());
						table.addField(field);
						
				 }
			if(alterList.size()>0) {
                dbw.alterColumns(table);
            }
			    }else{
			    	syncGzOracleField(data,mapcopy,tableName);
			    }
			 table.clear();
				 for(int i=0;i<resetList.size();i++){
					 Field field = (Field)resetListmap.get(resetList.get(i));
						field.setName(resetList.get(i).toString().toLowerCase());
						table.addField(field);
						
				 }
				 if(resetList.size()>0)
				 {
					 dbw.dropColumns(table);
					 dbw.addColumns(table);
					 table.clear();
				 }
				 table.clear();
				 boolean flag =false;
				 for(int i=0;i<overlist.size();i++){
					 String nameid =overlist.get(i).toString().toLowerCase();
					 if(map.get(nameid)!=null){
						 FieldItem item = (FieldItem)map.get(nameid);
						 Field field = item.cloneField();
						field.setName(nameid);
						table.addField(field);
						flag = true;
					 }
				 }
			 if(flag) {
                 dbw.addColumns(table);
             }
			 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	private void syncGzOracleField(ResultSetMetaData data,HashMap map,String tableName)
	{
		try
		{
			 DbWizard dbw=new DbWizard(this.conn);
			 ContentDAO dao=new ContentDAO(this.conn);
			 for(int i=1;i<=data.getColumnCount();i++)
			 {
				String columnName=data.getColumnName(i).toLowerCase();
				if(map.get(columnName)!=null)
				{
					FieldItem tempItem=(FieldItem)map.get(columnName);
					int columnType=data.getColumnType(i);	
					
					//int size=data.getColumnDisplaySize(i);
					int scale=data.getScale(i);
					switch(columnType)
					{
					case java.sql.Types.FLOAT:
						if("N".equals(tempItem.getItemtype()))
						{
							if(tempItem.getDecimalwidth()!=scale) {
                                alertColumn(tableName,tempItem,dbw,dao);
                            }
						}
			
						break;
						case java.sql.Types.DOUBLE:
							if("N".equals(tempItem.getItemtype()))
							{
								if(tempItem.getDecimalwidth()!=scale) {
                                    alertColumn(tableName,tempItem,dbw,dao);
                                }
							}
				
							break;
						case java.sql.Types.NUMERIC:
							if("N".equals(tempItem.getItemtype()))
							{
								if(tempItem.getDecimalwidth()!=scale) {
                                    alertColumn(tableName,tempItem,dbw,dao);
                                }
							}
							
							break;	
					}
				}
			 }
	
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	private void alertColumn(String tableName,FieldItem _item,DbWizard dbw,ContentDAO dao)
	{
		try
		{
			FieldItem item=(FieldItem)_item.cloneItem();
			Table table=new Table(tableName);
			 String item_id=item.getItemid();
			 item.setItemid(item_id+"_x");
			 //TableModel tm=new TableModel(tableName);
			 if(!dbw.isExistField(tableName, item_id+"_x"))
			 {
		    	 table.addField(item.cloneField());
		    	 dbw.addColumns(table);
			 }
			 
			 if("N".equalsIgnoreCase(item.getItemtype()))
			 {
				 int dicimal=item.getDecimalwidth();
				 dao.update("update "+tableName+" set "+item_id+"_x=ROUND("+item_id+"_u,"+dicimal+")");
			 }
			
			 table.clear();
			 
			 item.setItemid(item_id+"_u");
			 table.addField(item.cloneField());
			 dbw.dropColumns(table);
			 dbw.addColumns(table);
			 
			 dao.update("update "+tableName+" set "+item_id+"_u="+item_id+"_x");
			 table.clear();
			 item.setItemid(item_id+"_x");
			 table.addField(item.cloneField());
			 dbw.dropColumns(table);
			 item.setItemid(item_id+"_u");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
}
