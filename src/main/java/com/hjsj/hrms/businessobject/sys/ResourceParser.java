/**
 * 
 */
package com.hjsj.hrms.businessobject.sys;

import com.hjsj.hrms.interfaces.sys.IResourceConstant;
import com.hjsj.hrms.utils.PubFunc;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import java.util.List;
import java.util.StringTokenizer;

/**
 * <p>Title:</p>
 * <p>Description:资源解释器</p> 
 * <p>Company:hjsj</p> 
 * create time at:Jul 24, 20064:04:31 PM
 * @author chenmengqing
 * @version 4.0
 */
public class ResourceParser implements IResourceConstant {
	private String xmlcontent;
	private int res_type=CARD;
	private Document doc=null;
	public ResourceParser(String xmlcontent,int res_type) {
		super();
		this.xmlcontent = xmlcontent;
		this.res_type=res_type;
		init();
	}
	
	public ResourceParser(String xmlcontent) {
		this(xmlcontent,CARD);
	}	
	/**
	 * 初始化
	 */
	private void init()
	{
		StringBuffer strxml=new StringBuffer();
		try
		{
	        strxml.append("<?xml version='1.0' encoding='GB2312'?>");
	        strxml.append("<resource>");
	        strxml.append(xmlcontent);
	        strxml.append("</resource>");
			doc = PubFunc.generateDom(strxml.toString());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * 取得对应资源节点的名称
	 * @return
	 */
	private String getElementName(int res_type)
	{
		String name=null;
		switch(res_type)
		{
		case CARD:
			name="Card";
			break;
		case REPORT:
			name="Report";
			break;
		case HIGHMUSTER:
			name="HighMuster";
			break;
		case MUSTER:
			name="Muster";
			break;
		case LEXPR:
			name="Lexpr";
			break;
		case STATICS:
			name="Statis";
			break;
		}
		return name;
	}
	/**
	 * 增设资源串
	 * @param str_res
	 */
	public void addContent(String str_res)
	{
		String name=getElementName(this.res_type);
		String xpath="/resource/"+name;
		try
		{
			XPath reportPath = XPath.newInstance(xpath);// 取得符合条件的节点
			List childlist=reportPath.selectNodes(doc);
			Element element=null;
			if(childlist.size()==0)
			{
				element=new Element(name);
				element.setText(str_res);
				doc.getRootElement().addContent(element);
			}
			else
			{
				element=(Element)childlist.get(0);
				element.setText(str_res);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public void addContent(String str_res,int res_type)
	{
		this.res_type=res_type;
		addContent(str_res);
	}	
	
/**
 * 取得对应资源类型的值 ,2,3,3,4,
 * @return
 */	
   public String getContent()	
   {
		String name=getElementName(this.res_type);
		String xpath="/resource/"+name;
		String result="";
		StringBuffer str_value=new StringBuffer();
		try
		{
			XPath reportPath = XPath.newInstance(xpath);// 取得符合条件的节点
			List childlist=reportPath.selectNodes(doc);
			Element element=null;
			if(childlist.size()==0)
			{
				result= "";
			}
			else
			{
				element=(Element)childlist.get(0);
				result= element.getText();
			}
			StringTokenizer token=new StringTokenizer(result,",");
			while(token.hasMoreTokens())
			{
				str_value.append(token.nextToken());
				str_value.append(",");
			}
			if(str_value.length()>0) {
                str_value.setLength(str_value.length()-1);
            }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	    return str_value.toString();
   }

   /**
    * @param res_type 资源类型
    * @return
    */
   public String getContent(int res_type)	
   {
	   this.res_type=res_type;
	   return getContent();
   }   
	/**
	 * 输出内容
	 * @return
	 */
	public String outResourceContent()
	{
		StringBuffer strcontent=new StringBuffer();
        XMLOutputter outputter = new XMLOutputter();
        Format format=Format.getPrettyFormat();
        format.setEncoding("UTF-8");
        outputter.setFormat(format);
        Element root=doc.getRootElement();
        strcontent.append(outputter.outputString(/*doc.getRootElement()*/root.getChildren()));
		return strcontent.toString();
	}
	
	public String getXmlcontent() {
		return xmlcontent;
	}

	public void setXmlcontent(String xmlcontent) {
		this.xmlcontent = xmlcontent;
	}

	public int getRes_type() {
		return res_type;
	}

	public void setRes_type(int res_type) {
		this.res_type = res_type;
	}
	
}
