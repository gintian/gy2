package com.hjsj.hrms.transaction.report.reportdisk;

import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.sql.RowSet;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * 该类封装了生成上报盘功能。将数据库内容生成XML文件。
 * 
 * @author lzy
 * 
 */
public class UpDisk {

	private DocumentBuilderFactory factory;
	private DocumentBuilder builder = null;
	private Document doc = null;
	
	private Connection con = null;

	private String tabid = "";//上报的报表ID	
	private String unitname = ""; //根据用户输入转换过的填报单位名称	
	private String username = ""; //用户名
	private String unitcode = "";//根据用户输入转换过的填报单位编码	
	private String old_unitcode = "";	// 原填报单位编码
	private String parentcode = "";//根据用户输入转换过的填报单位编码父编码

	
	public static String[] tabidArray = null;

	/**
	 * 
	 * @param con
	 *            可使用连接
	 * @param username
	 *            当前用户名称
	 * @param tabid
	 *            报表id
	 * @param unitname
	 *            规范的用户输入的单位名称
	 * @param unitcode
	 *            规范的用户输入的单位编码
	 * @param parentcode
	 *            上级单位编码
	 * @param document
	 *            如果此参数为null新建文档，不为null将把doc指向些参数(为多个tabid的xml合并功能服务)
	 * @param old_unitcode 用户对应的填表单位
	 */
	public UpDisk(Connection con, String tabid, String unitname,
			String unitcode, String parentcode, Document document,
			String old_unitcode) {
		super();
		if (con != null) {
			this.con = con;
		}
		try {
			if (document == null) {
				factory = DocumentBuilderFactory.newInstance();
				builder = factory.newDocumentBuilder();
				doc = builder.newDocument();
			} else {
				doc = document;
			}
			this.old_unitcode = old_unitcode;
			this.unitname = unitname;
			this.unitcode = unitcode;
			this.parentcode = parentcode;
			this.tabid = tabid;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @param con
	 *            可使用连接
	 * @param username
	 *            当前用户名称
	 * @param tabid
	 *            报表id
	 * @param unitname
	 *            规范的用户输入的单位名称
	 * @param unitcode
	 *            规范的用户输入的单位编码
	 * @param parentcode
	 *            上级单位编码
	 * @param document
	 *            如果此参数为null新建文档，不为null将把doc指向些参数(为多个tabid的xml合并功能服务)
	 * @param old_unitcode 用户对应的填表单位
	 */
	public UpDisk(Connection con, String tabid, String unitname,String username,
			String unitcode, String parentcode, Document document,
			String old_unitcode) {
		super();
		if (con != null) {
			this.con = con;
		}
		try {
			if (document == null) {
				factory = DocumentBuilderFactory.newInstance();
				builder = factory.newDocumentBuilder();
				doc = builder.newDocument();
			} else {
				doc = document;
			}
			this.old_unitcode = old_unitcode;
			this.unitname = unitname;
			this.username = username;
			this.unitcode = unitcode;
			this.parentcode = parentcode;
			this.tabid = tabid;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 创建上报盘(xml)
	 * @return
	 */
	public Document createUpDisk() {
		Element root = null;
		if (doc.getElementsByTagName("hrp_reports").getLength() == 0) {
			root = doc.createElement("hrp_reports");
			root.appendChild(this.getGlobalElement()); //全局
			root.appendChild(this.getStyleElement());  //表类
		} else {
			root = (Element) doc.getElementsByTagName("hrp_reports").item(0);
		}
		
		root.setAttribute("unitcode", unitcode);
		root.setAttribute("unitname", unitname);
		root.setAttribute("parented", parentcode);
		
		Element report = doc.createElement("report");
		TnameBo namebo = new TnameBo(con, tabid);
		report.setAttribute("rows", String.valueOf(namebo.getRowMap().size()));
		report.setAttribute("columns", String
				.valueOf(namebo.getColMap().size()));
		report.setAttribute("tabid", tabid);
		
		report.appendChild(getTableElement());//表内参数
		report.appendChild(getRecordsElement());//表结构与表数据
		
		root.appendChild(report);
		if (doc.getElementsByTagName("hrp_reports").getLength() == 0) {
			doc.appendChild(root);
		}
		//ScreenPrint();
		return doc;
	}
	/**
	 * 创建生成报盘(xml)
	 * @return
	 */
	public Document createUpDisk(String flag) {
		Element root = null;
		if (doc.getElementsByTagName("hrp_reports").getLength() == 0) {
			root = doc.createElement("hrp_reports");
			root.appendChild(this.getGlobalElement()); //全局
			root.appendChild(this.getStyleElement());  //表类
		} else {
			root = (Element) doc.getElementsByTagName("hrp_reports").item(0);
		}
		
		root.setAttribute("unitcode", unitcode);
		root.setAttribute("unitname", unitname);
		root.setAttribute("parented", parentcode);
		
		Element report = doc.createElement("report");
		TnameBo namebo = new TnameBo(con, tabid);
		report.setAttribute("rows", String.valueOf(namebo.getRowMap().size()));
		report.setAttribute("columns", String
				.valueOf(namebo.getColMap().size()));
		report.setAttribute("tabid", tabid);
		
		report.appendChild(getTableElement());//表内参数
		report.appendChild(getRecordsElement2());//表结构与表数据
		
		root.appendChild(report);
		if (doc.getElementsByTagName("hrp_reports").getLength() == 0) {
			doc.appendChild(root);
		}
		//ScreenPrint();
		return doc;
	}
	/**
	 * 
	 * @return 返回全局参数的xml结点
	 */
	public Element getGlobalElement() {
		Hashtable h = this.getElement();
		return (Element) (h.get("tp_global_element"));
	}

	/**
	 * 
	 * @return 返回表类参数的xml结点
	 */
	public Element getStyleElement() {
		Hashtable h = getElement();
		return (Element) (h.get("tp_style_element"));
	}

	/**
	 * 
	 * @return 返回表内参数的xml结点
	 */
	public Element getTableElement() {
		Hashtable h = getElement();
		return (Element) (h.get("tp_table_element"));
	}
	
	/**
	 * 
	 * @param tabid
	 *            报表id
	 * @return xml树型结点列表包括三个结点(tp_global_element全局 , tp_style_element表类,
	 *         tp_table_element表内)
	 */
	public Hashtable getElement() {
		Hashtable ht = new Hashtable();
		Element tp_global_element = doc.createElement("tp_global");
		Element tp_style_element = doc.createElement("tp_style");
		Element tp_table_element = doc.createElement("tp_table");
		try {
			for (int i = 0; i <= 2; i++) {
				//参数类型(0 全局， 1 表类 ，2表内)
				ResultSet rs = this.getParameter(tabid, i);//
				while (rs.next()) {
					Element parameter_element = doc.createElement("parameter");
					parameter_element.setAttribute("name", rs
							.getString("paramename"));
					parameter_element.setAttribute("type", rs
							.getString("paramtype"));
					
					String value = this.getValue(i, rs.getString("paramename"));
					
					// System.out
					// .println(rs.getString("paramename") + "," + value);
					parameter_element.appendChild(doc.createTextNode(value));
					if (i == 0) {
						tp_global_element.appendChild(parameter_element);
					}
					if (i == 1) {
						tp_style_element.appendChild(parameter_element);
					}
					if (i == 2) {
						tp_table_element.appendChild(parameter_element);
					}
				}
				if (rs != null) {
					rs.close();
				}
			}
			ht.put("tp_global_element", tp_global_element);
			ht.put("tp_style_element", tp_style_element);
			ht.put("tp_table_element", tp_table_element);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ht;
	}

	/**
	 * @param tabid
	 *            报表id
	 * @param scope
	 *            参数类型(0 全局， 1 表类 ，2表内)
	 * @return 返回报表中的所有参数
	 */
	public ResultSet getParameter(String tabid, int scope) {
		ResultSet rs = null;
		ContentDAO dao = null;
		try {
			dao = new ContentDAO(this.con);
			String sqlText = "";
			if (scope == 2) { //表内
				//参数 名称 代号 类型
				sqlText = "select tparam.paramname,tparam.paramtype,tparam.paramename from tparam,tpage where tpage.hz = tparam.paramname and tpage.Flag = 9 and tpage.tabid = "
						+ tabid
						+ " and paramscope = "
						+ scope
						+ " group by tparam.paramname,tparam.paramtype,tparam.paramename";
			} else {
				StringBuffer sb = new StringBuffer();
				sb.append("(");
				boolean flg = false;
				for (int i = 0; i < tabidArray.length; i++) {
					if ("".equals(tabidArray[i].trim())) {
						continue;
					}
					if (!flg) {
						sb.append("tpage.tabid = " + tabidArray[i]);
						flg = true;
					} else {
						sb.append(" or " + "tpage.tabid = " + tabidArray[i]);
					}

				}
				sb.append(")");
				//参数名称(paramname) 参数代号(paramename) 参数类型 (paramtype) 参数范围(paramscope)
				sqlText = "select tparam.paramname,tparam.paramtype,tparam.paramename from tparam,tpage where tpage.hz = tparam.paramname and tpage.Flag = 9 and "
						+ "paramscope = "
						+ scope
						+ " and "
						+ sb.toString()
						+ " group by tparam.paramname,tparam.paramtype,tparam.paramename";
			}
			
			//System.out.println(sqlText);

			rs = dao.search(sqlText);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}

	/**
	 * 
	 * @param scope
	 *            参数类型(0 全局， 1 表类 ，2表内)
	 * @param name
	 *            参数名称
	 * @return 得到参数名为name的参数值
	 */
	public String getValue(int scope, String name) {
		ContentDAO dao = null;
		ResultSet rs = null;
		String value = "";
		try {
			dao = new ContentDAO(this.con);
			StringBuffer sqlbuffer = new StringBuffer();
			sqlbuffer.append("select " + name + " from ");
			if (scope == 0) {
				sqlbuffer.append("tt_p");
			}
			if (scope == 1) {
				sqlbuffer.append("tt_s" + UpDisk.getSortId(con, tabid));
			}
			if (scope == 2) {
				sqlbuffer.append("tt_t" + tabid);
			}
			sqlbuffer.append(" where unitcode='" + old_unitcode + "'");
			rs = dao.search(sqlbuffer.toString());
			
			//System.out.println(sqlbuffer.toString());
			
			if (rs.next()) {
				value = rs.getString(name);
				if (value == null || "null".equals(value.trim())) {
					value = "";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * 
	 * @return 返回报表记录xml结点
	 */
	public Element getRecordsElement() {
		Element records_element = doc.createElement("records");
		try {
			TnameBo tnameBo=new TnameBo(this.con,tabid);
			ContentDAO dao = new ContentDAO(this.con);
			tnameBo.insertByRowLimit(tabid, dao, "2", username, old_unitcode);
			ResultSet rs = dao.search("select distinct * from tt_" + tabid
					+ " where unitcode = " + "'" + old_unitcode + "'");
			ResultSetMetaData rsmd = rs.getMetaData();
			String value = null;
			String column = null;
			int j=0;
			while (rs.next()) {
				value = "";
				column = "";
				//String[] rowInfo=(String[])resultList.get(i);
				RecordVo colVo=(RecordVo)tnameBo.getColInfoBGrid().get(j);
				j++;
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					int npercent=0;
					if(rsmd.getColumnName(i).trim().toUpperCase().startsWith("C")){
						int m = Integer.parseInt(rsmd.getColumnName(i).substring(1));
							
					RecordVo rowVo=(RecordVo)tnameBo.getRowInfoBGrid().get(m-1);
//					if(rowVo.getInt("flag1")==3)
//						npercent=rowVo.getInt("npercent");
//					else if(colVo.getInt("flag1")==3)
//						npercent=colVo.getInt("npercent");
//					else
						npercent=rowVo.getInt("npercent")>=colVo.getInt("npercent")?rowVo.getInt("npercent"):colVo.getInt("npercent");
					
					//平均人数 小数位
					if(rowVo.getInt("flag2")==5&&rowVo.getInt("flag1")==1&&rowVo.getString("cexpr2").length()>0)
					{
						String[] temp=rowVo.getString("cexpr2").substring(rowVo.getString("cexpr2").indexOf("(")+1,rowVo.getString("cexpr2").indexOf(")")).split(";");
						npercent=0;
						if(temp.length==3&&Integer.parseInt(temp[2].trim())>0)
							npercent=Integer.parseInt(temp[2].trim());
					}
					if(colVo.getInt("flag2")==5&&colVo.getInt("flag1")==1)
					{
						String[] temp=colVo.getString("cexpr2").substring(colVo.getString("cexpr2").indexOf("(")+1,colVo.getString("cexpr2").indexOf(")")).split(";");
						npercent=0;
						if(temp.length==3&&Integer.parseInt(temp[2].trim())>0)
							npercent=Integer.parseInt(temp[2].trim());
					}
					}
					if (i != 1) {
						value += "`";
						column += "`";
					}
					column += rsmd.getColumnName(i);
					// 将报表值中的unitcode替换成用户输入的unitcode
					if ("unitcode".equalsIgnoreCase(rsmd.getColumnName(i).trim())) {
						value += unitcode;
					} else {
						value += PubFunc.round(rs.getString(rsmd.getColumnName(i)),npercent);
					}
				}
				
				records_element.setAttribute("columns", column);
				Element record_element = doc.createElement("record");
				
				record_element.appendChild(doc.createTextNode(value));
				records_element.appendChild(record_element);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return records_element;
	}

	/**
	 * 
	 * @return 返回报表记录xml结点
	 */
	public Element getRecordsElement2() {
		ContentDAO dao = null;
		Element records_element = doc.createElement("records");
		try {
			dao = new ContentDAO(this.con);
			ResultSet rs = dao.search("select distinct * from tb" + tabid
					+ " where username = " + "'" + username.trim() + "'");
			ResultSetMetaData rsmd = rs.getMetaData();
			String value = null;
			String column = null;
			while (rs.next()) {
				value = "";
				column = "";
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					if (i != 1) {
						value += "`";
						column += "`";
					}
					column += rsmd.getColumnName(i);
					// 将报表值中的unitcode替换成用户输入的unitcode
					if ("unitcode".equals(rsmd.getColumnName(i).trim())) {
						value += unitcode;
					} else {
						value += rs.getString(rsmd.getColumnName(i));
					}
				}
				
				records_element.setAttribute("columns", column);
				Element record_element = doc.createElement("record");
				
				record_element.appendChild(doc.createTextNode(value));
				records_element.appendChild(record_element);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return records_element;
	}

	
	/**
	 * 
	 * @param con
	 * @param unitcode
	 *            填报单位编码
	 * @return 填报单位名称
	 */
	public static String getUnitName(Connection con, String unitcode) {
		ContentDAO dao = null;
		ResultSet rs = null;
		String unitname = "";
		try {
			dao = new ContentDAO(con);
			String sqltext = "";
			sqltext = "select unitname from tt_organization where unitcode = '"
					+ unitcode + "'";
			rs = dao.search(sqltext);
			if (rs.next())
				unitname = rs.getString(1);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (Exception err) {
				err.printStackTrace();
			}
		}
		return unitname;
	}

	
	/**
	 * 当前用户对应的填表单位集合(包括子节点)
	 * @param con
	 *            数据库连接
	 * @param user_name
	 *            用户名
	 * @param contain_child
	 *            是否需求包含子结点 (编辑报表中不需要 / 报表汇总中需要)
	 * @return 填报单位ID
	 */
	public static List getUserUnitCode(Connection con, String user_name,
			boolean contain_child) {
		ArrayList mylist = new ArrayList();
		ContentDAO dao = null;
		ResultSet rs = null;
		try {
			dao = new ContentDAO(con);
			String sqltext = "";
			if (contain_child) { //报表汇总中操作上报
				sqltext = "select unitcode from operuser where username = '"
						+ user_name + "'";
				rs = dao.search(sqltext);
				if (rs.next()) {
					sqltext = "select unitcode from tt_organization where unitcode like '"
							+ rs.getString(1) + "%'";
				}
			} else {//编辑状态操作上报
				sqltext = "select unitcode from operuser where username = '"
						+ user_name + "'";
			}
			rs = dao.search(sqltext);
			while (rs.next()) {
				mylist.add(rs.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (Exception err) {
				err.printStackTrace();
			}
		}
		return mylist;
	}

	
	


	
	/**
	 * 
	 * @param 输出文件
	 */
	public void saveToFile(File outFile) {
		try {
			TransformerFactory factory = TransformerFactory.newInstance();
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING,true);
			Transformer transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "gb2312");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer
					.transform(new DOMSource(doc), new StreamResult(outFile));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 控制台输出
	 *
	 */
	public void ScreenPrint() {
		try {
			TransformerFactory factory = TransformerFactory.newInstance();
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING,true);
			Transformer transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "gb2312");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(new DOMSource(doc), new StreamResult(
					System.out));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param out
	 *            输出流
	 */
	public void saveToStream(OutputStream out) {
		try {
			TransformerFactory factory = TransformerFactory.newInstance();
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING,true);
			Transformer transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "gb2312");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(new DOMSource(doc), new StreamResult(out));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param con
	 * @param tabid
	 *            报表ID
	 * @return 表类ID
	 */
	public static String getSortId(Connection con, String tabid) {
		String sortid = "";
		ContentDAO dao = null;
		ResultSet rs = null;
		try {
			dao = new ContentDAO(con);
			rs = dao.search("select tsortid from tname where tabid = "
					+ tabid);
			if (rs.next()) {
				sortid = rs.getString(1);
			}
			return sortid;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (Exception err) {
				err.printStackTrace();
			}
		}
		return null;
	}

	public static String getParentUnitcode(Connection con, String unitcode) {
		String Parent_unitcode = "";
		try {
			ContentDAO dao = new ContentDAO(con);
			RowSet rs = dao
					.search("select parentid from tt_organization where unitcode = '"
							+ unitcode + "'");
			if (rs != null && rs.next()) {
				Parent_unitcode = rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Parent_unitcode;
	}

	public String[] getTabidArray() {
		return tabidArray;
	}

	public void setTabidArray(String[] tabidArray) {
		this.tabidArray = tabidArray;
	}
}
