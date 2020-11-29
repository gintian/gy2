package com.hjsj.hrms.service.ladp;

import com.hjsj.hrms.utils.PubFunc;
import org.apache.log4j.Category;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


/**
 * <p>
 * Title:PareXmlUtils
 * </p>
 * <p>
 * Description:解析xml
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2012-06-25
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 * 
 */
public class PareXmlUtils {
	
	// 在AD.xml文件内容
	private String adXMLContent = "";
	// Ad.xml文件
	private File adXMLFile = null;
	// xml文档
	private Document doc = null;
	// 日志
	private Category cat = null;

	public PareXmlUtils() {
		
	}
	
	public PareXmlUtils(String adXMLContent) {
		this.adXMLContent = adXMLContent;
		init();
	}
	
	public PareXmlUtils(File adXMLFile) {
		this.adXMLFile = adXMLFile;
		init();
	}

	/**
	 * 初始化
	 * @throws JDOMException
	 * @throws IOException
	 */
	public void init () {
		InputStream in = null;
		try {
			this.cat = Category.getInstance("com.hjsj.hrms.service.ladp.PareXmlUtils");
			if (adXMLContent != null && adXMLContent.length() > 0) {
				doc = PubFunc.generateDom(this.adXMLContent);
			} else if (adXMLFile != null && adXMLFile.exists()) {
				in = new FileInputStream(adXMLFile);
				doc = PubFunc.generateDom(in);
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.cat.error("初始化AD.xml文件时出错！");
		}finally {
			PubFunc.closeResource(in);
		}
	}
	
	/**
	 * 获取单节点
	 * @param path 节点路径
	 * @return
	 */
	public Element getSingleNode (String path) {
		Element el = null;
		try {
			XPath xpath = XPath.newInstance(path);
			el  = (Element)xpath.selectSingleNode(doc);
		} catch (Exception e) {
			e.printStackTrace();
			this.cat.error("获取" + path + "节点时失败！");
		}
		
		return el;
	}
	
	/**
	 * 获取多个节点
	 * @param path 节点路径
	 * @return
	 */
	public List getNodes (String path) {
		List list = null;
		try {
			XPath xpath = XPath.newInstance(path);
			list  = (List) xpath.selectNodes(doc);
		} catch (Exception e) {
			e.printStackTrace();
			this.cat.error("获取" + path + "节点时失败！");
		}
		
		return list;
	}
	
	/**
	 * 获取带命名空间的 Xml 节点
	 * @param path 节点路径
	 * @return
	 */
	public List getNamespaceNodes (String path,String prefix,String uri) {
		List list = null;
		try {
			XPath xpath = XPath.newInstance(path);
			xpath.addNamespace(prefix, uri);
			list  = (List) xpath.selectNodes(doc);
		} catch (Exception e) {
			e.printStackTrace();
			this.cat.error("获取" + path + "节点时失败！");
		}
		
		return list;
	}
	
	/**
	 * 获取节点值
	 * @param path 节点路径
	 * @return
	 */
	public String getTextValue (String path) {
		String value = "";
		try {
			Element el = this.getSingleNode(path);
			if (el != null) {
				value = el.getTextTrim();
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.cat.error("获取" + path + "节点值时失败！");
		}
		
		return value;
	}
	
	/**
	 * 获取属性值
	 * @param path 节点路径
	 * @return
	 */
	public String getAttributeValue (String path, String attri) {
		String value = "";
		try {
			Element el = this.getSingleNode(path);
			if (el != null) {
				value = el.getAttributeValue(attri);
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.cat.error("获取" + path + "===>" + attri + "属性值时失败！");
		}
		
		return value;
	}
	
	
	/**
	 * 设置对应节点参数的值
	 * @param str_path 保存路径 例如：/Params/nbase
	 * @param value //值
	 * @return
	 */
	public void setTextValue(String str_path,String value){
		try{
			XPath xpath=XPath.newInstance(str_path);
			Element spElement=(Element)xpath.selectSingleNode(doc);
			if(spElement!=null){
				spElement.setText(value);
			}else{
				String arr[] = str_path.split("/");
				if(arr!=null&&arr.length>0){
					for(int i=1;i<arr.length;i++){
						String path = "";
						for(int j=1;j<=i;j++){
							path+="/"+arr[j];
						}
						xpath=XPath.newInstance(path);
						Element bbElement=(Element)xpath.selectSingleNode(doc);
						if(bbElement==null){	
							int index = arr[i].indexOf("[");
							Element element= null;
							if (index != -1) {
								element = new Element(arr[i].substring(0, index));
							} else {
								element = new Element(arr[i]);
							}
							
							if(i==arr.length-1)
								element.setText(value);
							spElement.addContent(element);
						}else{
						    spElement = bbElement;
						}
					}
				}
			}

		}catch(Exception ex){
			ex.printStackTrace();
		}			
	}
	
	/**
	 * 设置对应节点属性的值
	 * @param str_path 保存路径 例如：/Params/nbase
	 * @param value //值
	 * @return
	 */
	public void setAttributeValue(String str_path,String attributeName,String attributeValue){
		try{
			XPath xpath=XPath.newInstance(str_path);
			Element spElement=(Element)xpath.selectSingleNode(doc);
			if(spElement!=null){
				spElement.setAttribute(attributeName, attributeValue);
			}else{
				String arr[] = str_path.split("/");
				if(arr!=null&&arr.length>0){
					for(int i=1;i<arr.length;i++){
						String path = "";
						for(int j=1;j<=i;j++){
							path+="/"+arr[j];
						}
						xpath=XPath.newInstance(path);
						Element bbElement=(Element)xpath.selectSingleNode(doc);
						if(bbElement==null){
							int index = arr[i].indexOf("[");
							Element element= null;
							if (index != -1) {
								element = new Element(arr[i].substring(0, index));
							} else {
								element = new Element(arr[i]);
							}
							if(i==arr.length-1)
								element.setAttribute(attributeName, attributeValue);
							spElement.addContent(element);
						}else{
						    spElement = bbElement;
						}
					}
				}
			}

		}catch(Exception ex){
			ex.printStackTrace();
		}			
	}
	
	public String getDocumentString() {
		StringBuffer buf=new StringBuffer();
		XMLOutputter outputter=new XMLOutputter();
		Format format=Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		buf.append(outputter.outputString(doc));
		
		return buf.toString();
	}

	public void setAdXMLContent(String adXMLContent) {
		this.adXMLContent = adXMLContent;
	}

	public void setAdXMLFile(File adXMLFile) {
		this.adXMLFile = adXMLFile;
	}
}
