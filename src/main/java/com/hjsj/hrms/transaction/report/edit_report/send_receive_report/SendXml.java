package com.hjsj.hrms.transaction.report.edit_report.send_receive_report;

import com.hjsj.hrms.businessobject.common.ReportDAO;
import com.hjsj.hrms.businessobject.common.StringExt;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
import java.sql.*;
import java.util.List;

/**
 * 该类封装了生成上报盘功能。将数据库内容生成XML文件。
 * 
 * @author lzy
 * 
 */
public class SendXml {
	
	/**DOM创建相关*/
	private DocumentBuilderFactory factory; //DOM工厂类
	private DocumentBuilder builder = null;
	private Document doc = null;

	
	private Connection con = null; //DB连接

	// private Statement stmt = null;

	private String unitname = ""; 

	// 根据用户输入转换过的填报单位编码
	private String unitcode = "";

	// 原填报单位编码
	private String old_unitcode = "";

	private String parentcode = "";

	private ReportDAO dao;

	private String where; //SQL中的where语句

	public static String[] tabidArray = null;  //上传报表ID集合

	/**
	 * 
	 * @param con
	 *            可使用连接
	 * @param tabids
	 *            存放报表id的数组
	 * @param document
	 *            如果此参数为null新建文档，不为null将把doc指向些参数(为多个tabid的xml合并功能服务)
	 */
	public SendXml(Connection con, String[] tabidArray, Document document) {
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
			this.tabidArray = tabidArray;
			where = StringExt.delimitString(tabidArray, " or ", "tabid", "'");
			// tabid = '1' or tabid='2'
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 创建报表样式文件
	 * @return
	 */
	public Document createUpDisk() {
		Element root = null;
		root = doc.createElement("hrp_reports");
		
		//报表类信息
		root.appendChild(getElement("tsort", "tname", null));
		
		//报表信息
		Element tabsElement = doc.createElement("tabs");
		getTabElement(tabsElement);
		root.appendChild(tabsElement);
		
		//参数信息表
		root.appendChild(getTparamElement());
		
		doc.appendChild(root);
		// ScreenPrint();
		return doc;
	}


	/**
	 * 
	 * @param elementName 节点名称
	 * @param tableName   对应报表名称
	 * @param tabid       节点值
	 * @return
	 */
	public Element getElement(String elementName, String tableName, String tabid) {
		Element tsortElement = doc.createElement(elementName);
		ResultSet rs = null;
		Connection conn=null;
		ContentDAO dao = null;
		try {
			conn=AdminDb.getConnection();
			dao = new ContentDAO(conn);
			if ("tsort".equalsIgnoreCase(elementName)) {
				rs = dao.search("select * from tsort where tsortid in (select tsortid from "
								+ tableName + " where " + where + ")");
			} else {
				rs = dao.search("select * from " + tableName
						+ " where tabid = " + tabid);
			}
			
			ResultSetMetaData rsmd = rs.getMetaData();
			
			//从ResultSetMetaData中的取出列名并用delimiterChar分隔组成字符串
			String columnNames = StringExt.delimitString(rsmd, "γ").toLowerCase();//tsortidγnameγsdesγsid
			tsortElement.setAttribute("columns", columnNames);
			
			//从求值并用delimiterChar分隔组成字符串,每一条记录组合为一个字符串
			List valueList = StringExt.delimitString(rs, "γ");
			for (int i = 0; i < valueList.size(); i++) {
				Element recordElement = doc.createElement("record");
				recordElement.appendChild(doc.createTextNode((String) valueList
						.get(i)));
				tsortElement.appendChild(recordElement);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try
			{
				if(rs!=null)
					rs.close();
				if(conn!=null)
					conn.close();
			  } catch (SQLException e) {
					e.printStackTrace();
			
			}
		}
		return tsortElement;
	}

	/**
	 * 报表信息
	 * @param tabsElement
	 */
	public void getTabElement(Element tabsElement) {

		ResultSet rs = null;
		Connection conn=null;
		ContentDAO dao = null;
		try {
			conn=AdminDb.getConnection();
			dao = new ContentDAO(conn);
			rs = dao.search(" select * from tname where " + where);
			ResultSetMetaData rsmd = rs.getMetaData();
			while (rs.next()) {
				Element tabElement = doc.createElement("tab");
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					
					String str = rs.getString(rsmd.getColumnName(i)); //字段值
					
					tabElement.setAttribute(rsmd.getColumnName(i).toLowerCase(), str);//tab 节点上添加属性
					
					//当前报表相关表信息
					if ("tabid".equalsIgnoreCase(rsmd.getColumnName(i))) {
						//单元格信息表
						tabElement.appendChild(getElement("tgrid2", "tgrid2",
								str));
						//报表外框信息
						tabElement.appendChild(getElement("tgrid3", "tgrid3",
								str));
						//计算公式
						tabElement.appendChild(getElement("tformula",
								"tformula", str));
						//行效验公式
						tabElement.appendChild(getElement("rowchk", "rowchk",
								str));
						//列效验公式
						tabElement.appendChild(getElement("colchk", "colchk",
								str));
						//表间效验公式
						tabElement.appendChild(getElement("tcheck", "tcheck",
								str));
						//报表标题信息
						tabElement.appendChild(getElement("tpage", "tpage",
								str));
					}
					tabsElement.appendChild(tabElement);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs!=null)
					rs.close();
				if(conn!=null)
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			
		}
	}

	/**
	 * 参数信息表信息描述
	 * @return
	 */
	public Element getTparamElement() {
		Element tparamElement = doc.createElement("tparam");
		ResultSet rs = null;
		Connection conn=null;
		Statement stmt = null;
		try {
			conn=AdminDb.getConnection();
			stmt = conn.createStatement();
			String sqlText = "";
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
			sqlText = "select paramname,paramcon,paramtype,paramfmt,paramid,paramename,paramlen,paramCode,paramscope,paramsum,paramNull from tparam,tpage where tpage.hz = tparam.paramname and tpage.Flag = 9 and "
					+ sb.toString()
					+ " group by  paramname,paramcon,paramtype,paramfmt,paramid,paramename,paramlen,paramCode,paramscope,paramsum,paramNull";

			
			rs = stmt.executeQuery(sqlText);
			ResultSetMetaData rsmd = rs.getMetaData();
			String columnNames = StringExt.delimitString(rsmd, "γ").toLowerCase();
			List valueList = StringExt.delimitString(rs, "γ");
			tparamElement.setAttribute("columns", columnNames);
			for (int i = 0; i < valueList.size(); i++) {
				Element recordElement = doc.createElement("record");
				recordElement.appendChild(doc.createTextNode((String) valueList
						.get(i)));
				tparamElement.appendChild(recordElement);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
			PubFunc.closeResource(stmt);
			PubFunc.closeResource(conn);
		}
		return tparamElement;
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
	 * 将结果输出到屏幕上
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
}
