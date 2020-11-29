package com.hjsj.hrms.businessobject.general.template.privy_explain;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class PrivyExplain {
    private Connection conn;
    private String constant_value="SYS_ABR_DESC";
	public PrivyExplain(Connection conn)
	{
		this.conn=conn;
	}
	/**
	 * 得到临时文本内容
	 * @return
	 */
	public HashMap getSysConstantXML()
	{
		String xmlConstant=getConstantXML();
		HashMap hashMap=new HashMap();
		if(xmlConstant!=null&&xmlConstant.length()>0)
		{
		    try{
			   Document doc=PubFunc.generateDom(xmlConstant);//读入xml
			   String xpath="/SYS/ABR_DESC";
			   XPath reportPath = XPath.newInstance(xpath);// 取得根节点
		       List childlist=reportPath.selectNodes(doc);
		       Iterator t = childlist.iterator();
		       if(t.hasNext())
		       {
		    	   Element childR=(Element)t.next();
		    	   String constant=childR.getText();
		    	   if(constant==null||constant.length()<0)
		    	   {
		    		   constant="";
		    	   }		    					   
		    	   hashMap.put("constant",constant); //考勤方式	
		       }
		    }catch(Exception e)
		    {
		      e.printStackTrace();	
		    }
		}
		return hashMap;
	}
	/**
	 * 创建xml
	 * @param start_date
	 * @param days
	 * @param constant
	 * @return
	 */
	public String creatSysContentXml(String constant)
	{
	  String temp = null;		
	  try
	  {
		Element notes = new Element("SYS");
		Element note = new Element("ABR_DESC");
		note.setText(constant);		
		notes.addContent(note);
		Document myDocument = new Document(notes);
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		temp= outputter.outputString(myDocument);		
	/*	System.out.println("*********创建XML**************");
		System.out.println(temp);
		System.out.println("********************");*/
	  }
	  catch(Exception ex)
	  {
		  ex.printStackTrace();
	  }
	  return temp;
	}
	/**
	 * 得到sys_note的xml
	 * @return
	 */
	public String getConstantXML()
	{
		  StringBuffer sb=new StringBuffer();
		  sb.append("select Str_Value from constant where UPPER(Constant)='"+this.constant_value+"'");
		  ContentDAO dao = new ContentDAO(conn);
		  RowSet rowSet=null;
		  String xmlConstant="";
		  try
		  {
			  rowSet=dao.search(sb.toString());
			  if(rowSet.next())
			  {
				  xmlConstant=rowSet.getString("Str_Value");
			  }
			  
		  }catch(Exception e)
		  {
			  e.printStackTrace();
		  }
		  return xmlConstant;
	}
	/**
	 * 保存xml
	 *
	 */
	public void addSysContentXML(String constantXml)
	{
		  String sql="select * from constant where UPPER(Constant)='"+this.constant_value+"'";
		  ContentDAO dao = new ContentDAO(conn);
		  RowSet rs=null;
		  try
		  {
			rs=dao.search(sql);		  
			  if(rs.next())
			  {
				  updateContentXML(constantXml); 
			  }else
			  {
				  insertContentXML(constantXml);
			  }
		  }catch(Exception e)
		  {
			  e.printStackTrace();
		  }		 
	}
	public void updateContentXML(String constantXml)
	{
		  StringBuffer sb=new StringBuffer();
		  sb.append("update constant set Str_Value='"+constantXml+"' where UPPER(Constant)='"+this.constant_value+"'");
		  ContentDAO dao = new ContentDAO(conn);
		  try
		  {
			dao.update(sb.toString());		  
			  
		  }catch(Exception e)
		  {
			  e.printStackTrace();
		  }		 
	}
	/**
	 * 插入新的
	 * @param constantXml
	 */
	public void insertContentXML(String constantXml)
	{
		String insert="insert into constant(Constant,Str_Value) values (?,?)";
		ArrayList list=new ArrayList();
		list.add(this.constant_value);
		list.add(constantXml);
		ContentDAO dao = new ContentDAO(conn);
		  try
		  {
			dao.insert(insert,list);		  
			  
		  }catch(Exception e)
		  {
			  e.printStackTrace();
		  }		
	}

}
