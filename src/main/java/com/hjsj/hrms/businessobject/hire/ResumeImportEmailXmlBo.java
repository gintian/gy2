package com.hjsj.hrms.businessobject.hire;

import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;

public class ResumeImportEmailXmlBo {
	private Connection conn;
	private String xml;
	private Document doc;
	public static HashMap hm;
	DbSecurityImpl dbS = new DbSecurityImpl();

	public ResumeImportEmailXmlBo(Connection conn) {
		// TODO Auto-generated constructor stub
		this.conn = conn;
		this.initXML();
	}

	private void initXML() {
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			// 常量表中查找ZP_IMPORT_EMAIL常量
			rs = dao.search("select str_value  from CONSTANT where UPPER(CONSTANT)='ZP_IMPORT_EMAIL'");
			if (rs.next()) {
				// 获取XML文件
				xml = Sql_switcher.readMemo(rs, "STR_VALUE");

				// System.out.println(xml);
			} else {
				ArrayList list = new ArrayList();
				list.add("ZP_IMPORT_EMAIL");
				list.add("A");
				list.add("简历导入—邮箱");

				list.add("");
				dao.insert("insert into CONSTANT values(?,?,?,?)", list);
				init();

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 初始化邮箱导入参数
	 */
	public void init() {

		PreparedStatement ps = null;
		try {
			StringBuffer strxml = new StringBuffer();
			strxml.append("<?xml version='1.0' encoding='GB2312' ?>");
			strxml.append("<scheme>");
			strxml.append("<email>");
			strxml.append("<emailaddr></emailaddr>");
			strxml.append("<datetime></datetime>");
			strxml.append("<title></title>");
			strxml.append("</email>");
			strxml.append("</scheme>");
			xml = strxml.toString();
			String sql = "update CONSTANT set STR_VALUE=?  where UPPER(CONSTANT)='ZP_IMPORT_EMAIL'";
			ps = this.conn.prepareStatement(sql);
			switch (Sql_switcher.searchDbServer()) {
			case Constant.MSSQL:
				ps.setString(1, xml);
				break;
			case Constant.ORACEL:
				ps.setCharacterStream(1, new InputStreamReader(new ByteArrayInputStream(xml.getBytes())), xml.length());
				break;
			case Constant.DB2:
				ps.setCharacterStream(1, new InputStreamReader(new ByteArrayInputStream(xml.getBytes())), xml.length());
				break;
			}
			// 打开Wallet
			dbS.open(conn, sql);
			ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(ps);
			try {
				// 关闭Wallet
				dbS.close(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 更新最近邮件导入参数
	 * 
	 * @param mailAddr
	 * @param mailTitle
	 * @param sendtime
	 * @throws GeneralException
	 */
	public void updateEmailInfo(String mailAddr, String mailTitle, String sendtime) throws GeneralException {
		PreparedStatement ps = null;
		ByteArrayOutputStream bo = null;
		try {
			doc = PubFunc.generateDom(xml);
			XPath xPath = XPath.newInstance("/scheme/email[emailaddr='"+mailAddr+"']");
			Element email = (Element) xPath.selectSingleNode(this.doc);
			if(email != null){
				Element emailaddr = email.getChild("emailaddr");
				Element datetime = email.getChild("datetime");
				Element title = email.getChild("title");
				emailaddr.setText(mailAddr);
				datetime.setText(sendtime);
				title.setText(mailTitle);
			}else{
	           Element root = doc.getRootElement();
	           Element email1=new Element("email");
	           email1.addContent(new Element("emailaddr").setText(mailAddr));
	           email1.addContent(new Element("datetime").setText(sendtime));
	           email1.addContent(new Element("title").setText(mailTitle));
	           root.addContent(email1);
			}

			bo = new ByteArrayOutputStream();
			XMLOutputter XMLOut = new XMLOutputter(FormatXML());
			XMLOut.output(doc, bo);

			String StrValue = bo.toString();
			String sql ="update CONSTANT set STR_VALUE=?  where UPPER(CONSTANT)='ZP_IMPORT_EMAIL'";
			ps = this.conn.prepareStatement(sql);
			switch (Sql_switcher.searchDbServer()) {
			case Constant.MSSQL:
				ps.setString(1, StrValue);
				break;
			case Constant.ORACEL:
				ps.setCharacterStream(1, new InputStreamReader(new ByteArrayInputStream(StrValue.getBytes())), StrValue.length());
				break;
			case Constant.DB2:
				ps.setCharacterStream(1, new InputStreamReader(new ByteArrayInputStream(StrValue.getBytes())), StrValue.length());
				break;
			}
			// 打开Wallet
			dbS.open(conn, sql);
			ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(ps);
			PubFunc.closeResource(bo);
			try {
				// 关闭Wallet
				dbS.close(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public Format FormatXML() {
		// 格式化生成的xml文件，如果不进行格式化的话，生成的xml文件将会是很长的一行...
		Format format = Format.getCompactFormat();
		format.setEncoding("UTF-8");
		format.setIndent(" ");
		return format;
	}

	/**
	 * 判断邮件是否导入
	 * 
	 * @param mailAddr
	 * @param mailTitle
	 * @param sendtime
	 * @return
	 * @throws GeneralException
	 */
	public boolean isImportFlag(String mailAddr, String mailTitle, String sendtime,String sartDate) throws GeneralException {

		boolean flag = false;
		try {
			doc = PubFunc.generateDom(xml);
			XPath xPath = XPath.newInstance("/scheme/email[emailaddr='"+mailAddr+"']");
			Element email = (Element) xPath.selectSingleNode(this.doc);
			String Eaddr = "";
			String Date =  "";
			if(email!=null){
				Eaddr = email.getChildText("emailaddr");
				Date = email.getChildText("datetime");
				Date = Date.replace("-", "");
				Date = Date.replace(":", "");
				Date = Date.replace(" ", "");
				sendtime = sendtime.replace("-", "");
				sendtime = sendtime.replace(":", "");
				sendtime = sendtime.replace(" ", "");
				String Etitle = email.getChildText("title");

				if ("".equals(Date)) {
					if("".equals(sartDate)){
						Date = "0";
					}else{
						sartDate = sartDate.replace("-", "");
						sartDate = sartDate.replace(":", "");
						sartDate = sartDate.replace(" ", "");
						Date=sartDate;
					}

				}

				if (Double.parseDouble(Date) < Double.parseDouble(sendtime) ) {
					flag = true;

				} else {
                    flag = false;
                }
			}else{
				flag = true;
			}
			

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return flag;

	}

}
