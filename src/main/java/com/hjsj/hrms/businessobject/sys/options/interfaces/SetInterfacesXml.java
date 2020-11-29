package com.hjsj.hrms.businessobject.sys.options.interfaces;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SetInterfacesXml {
	private String xmlcontent="";
	private Document doc;
    public SetInterfacesXml(String xmlContent)
    {
    	this.xmlcontent=xmlContent;
    	if(this.xmlcontent==null||this.xmlcontent.length()<=0)
    	{
    		StringBuffer strxml=new StringBuffer();
    		strxml.append("<?xml version='1.0' encoding='UTF-8' ?>");
    		strxml.append("<param>");
    		strxml.append("</param>");	
    		this.xmlcontent=strxml.toString();
    	}
    	try
		{
			this.doc=PubFunc.generateDom(xmlcontent);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
    }
	public LazyDynaBean getHrService(String str_path)
	{
		LazyDynaBean bean=new LazyDynaBean();	
		try {
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(doc);
			Iterator i = childlist.iterator();
			Element element=null;			
			while(i.hasNext())
			{
				element=(Element)i.next();		
				String src=element.getAttributeValue("src");
				String dest=element.getAttributeValue("dest");
				String codesetid=element.getAttributeValue("codesetid");
				LazyDynaBean obean=new LazyDynaBean();	
				obean.set("src", src);
				obean.set("dest", dest);
				obean.set("codesetid", codesetid);
				bean.set(src, obean);
			}
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		return bean;
	}
	public ArrayList getHrServiceList(String str_path)
	{
       ArrayList list=new ArrayList();
       try {
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(doc);
			Iterator i = childlist.iterator();
			Element element=null;			
			while(i.hasNext())
			{
				element=(Element)i.next();		
				String src=element.getAttributeValue("src");
				String dest=element.getAttributeValue("dest");
				String codesetid=element.getAttributeValue("codesetid");
				LazyDynaBean obean=new LazyDynaBean();	
				obean.set("src", src);
				obean.set("dest", dest);
				obean.set("codesetid", codesetid);
				list.add(obean);
			}
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		return list;
	}
	/**
	 * 
	 * @param str_path
	 * @param attributeName
	 * @return
	 */
	public String getHrServiceParam(String str_path,String attributeName)
	{
		String value="";
		try {
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(doc);
			Iterator i = childlist.iterator();
			Element element=null;			
			if(i.hasNext())
			{
				element=(Element)i.next();		
				value=element.getAttributeValue(attributeName);				
			}
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return value;
	}
	public String getHrServiceParam(String str_path,String attributeName,String attributevalue,String getattributeName)
	{
		String value="";
		try {
			XPath xpath=XPath.newInstance(str_path+"[@"+attributeName+"='"+attributevalue+"']");
			List childlist=xpath.selectNodes(doc);
			Iterator i = childlist.iterator();
			Element element=null;			
			if(i.hasNext())
			{
				element=(Element)i.next();		
				value=element.getAttributeValue(getattributeName);				
			}
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return value;
	}
	public boolean saveParamAttribute(String path,String name,String attribute,String value)
	{
		boolean isCorrect=true;		
		try
		{
			    XPath xpath=XPath.newInstance(path);
				List childlist=xpath.selectNodes(doc);
				Element element=null;
				if(childlist.size()==0)
				{
					element=new Element(name);
					element.setAttribute(attribute,value);
					doc.getRootElement().addContent(element);
				}
				else
				{
					element=(Element)childlist.get(0);
					element.setAttribute(attribute,value);
				}
			
		}catch(Exception e)
		{
			isCorrect=false;
			e.printStackTrace();
		}
		
		return isCorrect;
	}
	/**
	 * 组织机构单位属性对应
	 */
	public boolean saveOrgParamAttribute(String path,String name)
	{
		boolean isCorrect=true;		
		try
		{
			    XPath xpath=XPath.newInstance(path);
				List childlist=xpath.selectNodes(doc);
				Element element=null;
				if(childlist.size()==0)
				{
					element=new Element(name);
					doc.getRootElement().addContent(element);
				}
				else
				{
					element=(Element)childlist.get(0);
					
				}
			
		}catch(Exception e)
		{
			isCorrect=false;
			e.printStackTrace();
		}
		
		return isCorrect;
	}
	/**
	 * 保存 list存放的是LazyDynaBean，三个属性src，dest，codesetid
	 * @param path
	 * @param list
	 * @return
	 */
	public boolean saveParamAttribute(String path,String name,ArrayList list)
	{
		boolean isCorrect=true;		
		try
		{
			XPath xpath=XPath.newInstance(path);
			List childlist=xpath.selectNodes(doc);
			Element element=null;
			if(childlist.size()==0)
			{
				element=new Element(name);
				for(int i=0;i<list.size();i++)
				{
					LazyDynaBean bean=(LazyDynaBean)list.get(i);
					Element element_o=new Element("rec");
					element_o.setAttribute("src", (String)bean.get("src"));
					element_o.setAttribute("dest", (String)bean.get("dest"));
					element_o.setAttribute("codesetid", (String)bean.get("codesetid"));
					element.addContent(element_o);
				}
				Element element_p=new Element("param");
				element_p.addContent(element);
				doc.getRootElement().addContent(element_p);
			}
			else
			{
				element=(Element)childlist.get(0);				
				//element.removeChild("rec");
				element.removeChildren("rec");
				for(int i=0;i<list.size();i++)
				{
					LazyDynaBean bean=(LazyDynaBean)list.get(i);
					Element element_o=new Element("rec");
					element_o.setAttribute("src", (String)bean.get("src"));
					element_o.setAttribute("dest", (String)bean.get("dest"));
					element_o.setAttribute("codesetid", (String)bean.get("codesetid"));
					element.addContent(element_o);
				}
				
			}
		}catch(Exception e)
		{
			isCorrect=false;
			e.printStackTrace();
		}
		return isCorrect;
	}
	public boolean if_vo_Empty(String constant,Connection conn)
	{
		  String sql="select * from constant where UPPER(Constant)='"+constant.toUpperCase()+"'";
		  ContentDAO dao = new ContentDAO(conn);
		  boolean is_correct=true;
		  RowSet rs=null;
		  try
		  {
			rs=dao.search(sql);		  
			  if(!rs.next())
			  {
				  is_correct=false; 
			  }
		  }catch(Exception e)
		  {
			  e.printStackTrace();
		  }	
		  return is_correct;
	}
	/**
	 * 保存参数，先设置参数值，再保存
	 * @throws GeneralException
	 */
	public void saveParameter(Connection conn)throws GeneralException
	{	
		StringBuffer strsql=new StringBuffer();
		try
		{
			ContentDAO dao = new ContentDAO(conn);
			
			StringBuffer buf=new StringBuffer();
			XMLOutputter outputter=new XMLOutputter();
			Format format=Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			buf.append(outputter.outputString(doc));
            //System.out.println(buf.toString());
			//if(1==1)return;
			boolean iscorrect=if_vo_Empty("HR_SERVICE",conn);	
			ArrayList list = new ArrayList();
			if(!iscorrect)
			{
				strsql.append("insert into constant(constant,str_value) values(?,?)");	
				
								
				list.add("HR_SERVICE");
				list.add(buf.toString());
				dao.insert(strsql.toString(), list);
			}
			else
			{
				strsql.append("update constant set str_value=? where constant='HR_SERVICE'");
					
				switch(Sql_switcher.searchDbServer())
				{
				  case Constant.MSSQL:
				  {
					  list.add(buf.toString());
					  break;
				  }
				  case Constant.ORACEL:
				  {
					  list.add(buf.toString());
					  break;
				  }
				  case Constant.DB2:
				  {
					  list.add(buf.length());
					  break;
				  }
				}
				
			}
			dao.update(strsql.toString(), list);		
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		finally
		{
					
		}
	}	
}
