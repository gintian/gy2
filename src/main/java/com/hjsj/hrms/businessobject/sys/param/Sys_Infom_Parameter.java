package com.hjsj.hrms.businessobject.sys.param;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
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
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * 处理常用参数constant中Infom的参数
 * 
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:May 31, 2008</p> 
 *@author sxin
 *@version 5.0
 */
public class Sys_Infom_Parameter {
	private Document doc;
	private Connection conn;
	private String xmlcontent="";
	public final static int PHOTO=1; 
	public final static int MULTIMEDIA=1;
	public Sys_Infom_Parameter(Connection conn,String constant)
	{
		this.conn = conn;
		init(constant);
		try
		{
			doc= PubFunc.generateDom(xmlcontent.toString());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	public boolean saveParamAttribute(int param_type,String attribute,String value)
	{
		boolean isCorrect=true;
		String name=getElementName(param_type);	
		try
		{
			if(name!=null&&name.length()>0)
			{
				String path="/INFOM/"+name;
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
			}else
			{
				isCorrect=false;
			}
		}catch(Exception e)
		{
			isCorrect=false;
			e.printStackTrace();
		}
		
		return isCorrect;
	}
	public String getValue(int param_type,String attribute)
	{
		String value="";
		String name=getElementName(param_type);
		if(!"".equals(name))
		{
		  try
		  {
			String str_path="/INFOM/"+name;
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(doc);
			Element element=null;
			if(childlist.size()!=0)
			{
				element=(Element)childlist.get(0);
				value=element.getAttributeValue(attribute);
			}
		  }
		  catch(Exception ex)
		  {
			  ex.printStackTrace();
		  }
		}		
		return value;		
	}	
	/**
	 * 读参数的内容
	 *
	 */
	private void init(String constant)
	{
		RecordVo vo=new RecordVo("constant");
		vo.setString("constant",constant);
		StringBuffer strxml=new StringBuffer();
		strxml.append("<?xml version='1.0' encoding='GB2312' ?>");
		strxml.append("<INFOM>");
		strxml.append("</INFOM>");		
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			vo=dao.findByPrimaryKey(vo);
			if(vo!=null) {
                xmlcontent=vo.getString("str_value");
            }
			if(xmlcontent==null|| "".equals(xmlcontent))
			{
				xmlcontent=strxml.toString();
			}
		}
		catch(Exception ex)
		{
			xmlcontent=strxml.toString();
			ex.printStackTrace();
		}
		//System.out.println(xmlcontent);
	}
	private String getElementName(int param_type)
	{
		String name="";
		switch(param_type)
		{
		   case PHOTO:
		 	name="Photo";
			break;
		}
		return name;
	}
	/**
	 * 保存参数，先设置参数值，再保存
	 * @throws GeneralException
	 */
	public void saveParameter()throws GeneralException
	{	
		StringBuffer strsql=new StringBuffer();
		try
		{
			if_SysConstant_Save("INFOM");
			StringBuffer buf=new StringBuffer();
			XMLOutputter outputter=new XMLOutputter();
			Format format=Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			buf.append(outputter.outputString(doc));

			//ContentDAO dao=new ContentDAO(this.conn);
			//RecordVo vo=new RecordVo("constant");			
			boolean iscorrect=if_vo_Empty("INFOM");
			ArrayList list = new ArrayList();
			ContentDAO dao = new ContentDAO(this.conn);
			if(!iscorrect)
			{
				strsql.append("insert into constant(constant,str_value) values(?,?)");	
								
				list.add("INFOM");
				list.add(buf.toString());
				/*vo=new RecordVo("constant");　　//以后再查原因，其它备注型字段这处理没问题
				vo.setString("constant","SYS_OTH_PARAM");				
				vo.setString("str_value",buf.toString());
				dao.addValueObject(vo);*/
			}
			else
			{
				strsql.append("update constant set str_value=? where constant='INFOM'");
					
				switch(Sql_switcher.searchDbServer())
				{
				  case Constant.MSSQL:
				  {
					  list.add(buf.toString());
					  break;
				  }
				  case Constant.ORACEL:
				  {
					 /* pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(buf.toString().
					          getBytes())), buf.length());*/
					  list.add(buf.toString());
					  break;
				  }
				  case Constant.DB2:
				  {
					  list.add(new InputStreamReader(new ByteArrayInputStream(buf.toString().
					          getBytes())));
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
	public void if_SysConstant_Save(String constant)
	{
		  String sql="select * from constant where UPPER(Constant)='"+constant.toUpperCase()+"'";
		  ContentDAO dao = new ContentDAO(conn);
		  RowSet rs=null;
		  try
		  {
			rs=dao.search(sql);		  
			  if(!rs.next())
			  {
				  String insert="insert into constant(Constant) values (?)";
				  ArrayList list=new ArrayList();
				  list.add(constant.toUpperCase());			
				  dao.insert(insert,list);		  
			  }
		  }catch(Exception e)
		  {
			  e.printStackTrace();
		  }		 
	}
	public boolean if_vo_Empty(String constant)
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
}
