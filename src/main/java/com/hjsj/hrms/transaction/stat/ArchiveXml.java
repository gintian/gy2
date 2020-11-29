package com.hjsj.hrms.transaction.stat;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ArchiveXml {
	private Connection conn;
	private Document doc;
	private String xml;
	private String snameid;
	private ContentDAO dao;
	public ArchiveXml(Connection conn,String snameid,String xmlstr){
		this.conn=conn;
		this.dao=new ContentDAO(conn);
		this.snameid=snameid;
		init(xmlstr);
		try{
			doc=PubFunc.generateDom(xml.toString());
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * <?xml version="1.0" encoding="GB2312"?>
		<params>
		<auto>是否启用归档，1:归档 0:不归档</auto>
		<unit_level>单位归档层级</unit_level>
		<dept_ctrl>部门是否归档，1:归档 0:不归档</dept_ctrl>
		<dept_level>部门归档层级</dept_level>
	   </params>
	 */
	private void init(String xmlstr){
		StringBuffer temp_xml=new StringBuffer();
		temp_xml.append("<?xml version='1.0' encoding='GB2312' ?>");
		temp_xml.append("<params>");
		temp_xml.append("</params>");
		try{
			if(xmlstr==null|| "".equals(xmlstr)){
				xml=temp_xml.toString();
			}else{
				xml=xmlstr;
			}
		}catch(Exception ex){
			xml=temp_xml.toString();
		}finally{
		}
	}

	public List getAllChildren(){
		List list = null;
		try {
			XPath xpath = XPath.newInstance("/params");
			Element root=(Element)xpath.selectSingleNode(doc);
			list = root.getChildren();
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	 
	public void setValue(String elementname,String value){
		try{
			XPath xpath=XPath.newInstance("/params/"+elementname);
			Element element=(Element)xpath.selectSingleNode(doc);
			if(element!=null){
				element.setText(value);
			}else{
				xpath=XPath.newInstance("params");
				Element spElement = (Element) xpath.selectSingleNode(doc);
				element=new Element(elementname);
				element.setText(value);
				spElement.addContent(element);
			}
		} catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void delValue(String elementname){
			try{
				XPath xpath=XPath.newInstance("params");
				Element spElement = (Element) xpath.selectSingleNode(doc);
				spElement.removeChild(elementname);
			} catch(Exception ex){
				ex.printStackTrace();
			}
		}				
	
	public String getValue(String elementname){ 
			try{
				XPath xpath=XPath.newInstance("/params/"+elementname);
				Element element=(Element)xpath.selectSingleNode(doc);
				if(element!=null){
					return element.getText();
				}
			} catch(Exception ex){
				ex.printStackTrace();
			}
			return "";
	}
	
	/**
	 * 保存xml格式的内容
	 *
	 */
	public void saveStrValue(){
		PreparedStatement pstmt = null;		
		StringBuffer strsql=new StringBuffer();
		try{
			StringBuffer buf=new StringBuffer();
			XMLOutputter outputter=new XMLOutputter();
			Format format=Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			buf.append(outputter.outputString(doc));
			strsql.append("update sname set archive=? where id="+snameid);
			pstmt = this.conn.prepareStatement(strsql.toString());	
			switch(Sql_switcher.searchDbServer()){
				 case Constant.MSSQL:
					  pstmt.setString(1, buf.toString());
					  break;
				 case Constant.ORACEL:
					  pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(buf.toString().
					          getBytes())), buf.length());
					  break;
				  case Constant.DB2:
					  pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(buf.toString().
					          getBytes())), buf.length());
					  break;
			}
			pstmt.executeUpdate();	
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(pstmt!=null){
					pstmt.close();
				}
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
	}
	
}
