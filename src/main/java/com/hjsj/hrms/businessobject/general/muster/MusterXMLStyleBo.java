package com.hjsj.hrms.businessobject.general.muster;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import java.sql.Connection;

public class MusterXMLStyleBo {
	private Connection conn;
	private String tabid;
	private Document doc;
	private String xml;
	public static final int Param=1;
	public static final int Report=2; 
	public MusterXMLStyleBo(Connection conn,String tabid){
		this.conn=conn;
		this.tabid=tabid;
		init();
	}
	public MusterXMLStyleBo(Connection conn){
		this.conn=conn;
	}
	public String getElementName(int type){
		String name = "";
		switch(type){
    		case Param:
    			name = "param";
    			break;
    		case Report:
    			name = "report";
    			break;
		}
		return name;
			
	}
	public String getParentPath(int type){
		String path = "/report/param";
		switch(type){
			case Param:
				path = "/report/param";
				break;
			case Report:
				path = "/report";
				break;
		}
		return path;
	}
	private void init(){
		RecordVo vo=new RecordVo("lname");
		vo.setString("tabid",tabid);
		StringBuffer temp_xml=new StringBuffer();
		temp_xml.append("<?xml version=\"1.0\" encoding=\"GB2312\" ?>");
		temp_xml.append("<report>");
		temp_xml.append("</report>");		
		try{
			ContentDAO dao=new ContentDAO(this.conn);
			if(dao.isExistRecordVo(vo))
			{
		    	vo=dao.findByPrimaryKey(vo);
		     	if(vo!=null) {
                    xml=vo.getString("xml_style");
                }
			}
			if(xml==null|| "".equals(xml)){
				xml=temp_xml.toString();
			}

			doc = PubFunc.generateDom(xml.toString());

//			System.out.println(xml);
		}catch(Exception ex){
			xml=temp_xml.toString();
		}
	}
	public String getParamValue(int type,String property){
		String paramvalue = "";
		String path=this.getParentPath(type);
		XPath xpath;
		try {
			xpath = XPath.newInstance(path);
			Element element=(Element)xpath.selectSingleNode(doc);
			if(element!=null){
				paramvalue = element.getAttributeValue("repeat_mainset");
				paramvalue=paramvalue!=null&&paramvalue.trim().length()>0?paramvalue:"False";
			}
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return paramvalue;
	}
	public void setParamValue(int type,String property,String paramvalue){
		String path=this.getParentPath(type);
		XPath xpath;
		try {
			xpath = XPath.newInstance(path);
			Element element=(Element)xpath.selectSingleNode(doc);
			if(element!=null){
				element.setAttribute("repeat_mainset",paramvalue);
			}else{
				xml = defaultValue(paramvalue);
			}
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private String defaultValue(String paramvalue){
		StringBuffer temp_xml=new StringBuffer();
		temp_xml.append("<?xml version=\"1.0\" encoding=\"GB2312\" ?>");
		temp_xml.append("<report>");
		temp_xml.append("<param repeat_mainset=\""+paramvalue+"\"/>");
		temp_xml.append("</report>");
		return temp_xml.toString();
	}
	public void saveSetValue(){
		try{
			ContentDAO dao=new ContentDAO(this.conn);
			
			dao.update("update lname set xml_style='"+xml+"' where tabid="+tabid); 
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public String getParamValue2(int type,String property){
		String paramvalue = "";
		String path=this.getParentPath(type);
		XPath xpath;
		try {
			xpath = XPath.newInstance(path);
			Element element=(Element)xpath.selectSingleNode(doc);
			if(element!=null){
				paramvalue = element.getAttributeValue(property);
				paramvalue=paramvalue!=null&&paramvalue.trim().length()>0?paramvalue:"";
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		}
		return paramvalue;
	}
	public void setPropertyValue(int type,String property,String pvalue)
	{
		String path=this.getParentPath(type);
		XPath xpath;
		try {
			xpath = XPath.newInstance(path);
			Element element=(Element)xpath.selectSingleNode(doc);
			if(element!=null){
				element.setAttribute(property,pvalue);
			}else{
				path = "/report";
				xpath = XPath.newInstance(path);
				Element element2=(Element)xpath.selectSingleNode(doc);
				Element element3 = new Element("param");
				element3.setAttribute(property, pvalue);
				element2.addContent(element3);
			}
			StringBuffer buf=new StringBuffer();
			XMLOutputter outputter=new XMLOutputter();
			Format format=Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			buf.append(outputter.outputString(doc));
			this.xml=buf.toString();
		} catch (JDOMException e) {
			e.printStackTrace();
		}
	}
}
