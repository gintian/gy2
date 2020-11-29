/**
 * 
 */
package com.hjsj.hrms.businessobject.general.cadrerm;

import com.hjsj.hrms.utils.PubFunc;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.io.IOException;


/**
 * XML 处理
 * @author Owner
 *
 */
public class HtmlParse {

	private Document doc;	//文档对象
	private FormatHtml formatHtml = null; //HTML格式化
	
	/**
	 * 构造器
	 */
	public HtmlParse(String htmlPath){
		formatHtml = new FormatHtml(htmlPath);
		String xml = formatHtml.htmlToXML();
		try {
			//xus 20/4/23 xml 编码改造
			 Document doc = PubFunc.generateDom(xml);
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * HTML body 部分
	 * @return
	 */
	public String  xmlToHtml(){
		String html = "";
		try {
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			String temp = outputter.outputString(doc);			
			html = formatHtml.xmlToHtmlBody(temp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return html.toString();
	}
	
	
	
	public Document getDoc() {
		return doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}

	public FormatHtml getFormatHtml() {
		return formatHtml;
	}

	public void setFormatHtml(FormatHtml formatHtml) {
		this.formatHtml = formatHtml;
	}

	

}
