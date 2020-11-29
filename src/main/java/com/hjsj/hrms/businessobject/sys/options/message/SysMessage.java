package com.hjsj.hrms.businessobject.sys.options.message;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
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

/**
 * 系统通知业务_通用类
 * <p>Title:SysMessage.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Oct 16, 2006 1:50:54 PM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class SysMessage {
    private Connection conn;
    private String view_hr;
    private String view_em;
	public String getView_hr() {
		if(view_hr==null||view_hr.length()<=0) {
            view_hr="0";
        }
		return view_hr;
	}
	public void setView_hr(String view_hr) {
		this.view_hr = view_hr;
	}
	public String getView_em() {
		if(view_em==null||view_em.length()<=0) {
            view_em="0";
        }
		return view_em;
	}
	public void setView_em(String view_em) {
		this.view_em = view_em;
	}
	public SysMessage()
	{
		
	}
	public SysMessage(Connection conn)
	{
		this.conn=conn;
	}
	/**
	 * 得到系统通知
	 * @return
	 */
	public HashMap getAllSysNoteXML()
	{
		String xmlConstant=getSysNoteXML();		
		HashMap hashMap=new HashMap();
		if(xmlConstant!=null&&xmlConstant.length()>0)
		{
		    try{
			   Document doc=PubFunc.generateDom(xmlConstant);//读入xml
			   String xpath="/notes/note";
			   XPath reportPath = XPath.newInstance(xpath);// 取得根节点
		       List childlist=reportPath.selectNodes(doc);
		       Iterator t = childlist.iterator();
		       if(t.hasNext())
		       {
		    	   Element childR=(Element)t.next();
		    	   String start_date=childR.getAttributeValue("start_date");
		    	   String hr=childR.getAttributeValue("hr");
		    	   String em=childR.getAttributeValue("em");
		    	   if(hr==null||hr.length()<=0) {
                       hr="";
                   }
		    	   if(em==null||em.length()<=0) {
                       em="";
                   }
		    	   if(start_date==null||start_date.length()<0)
		    	   {
		    		   start_date="";
		    	   }
		    	   String days=childR.getAttributeValue("days");
		    	   if(days==null||days.length()<0)
		    	   {
		    		   days="";
		    	   }
		    	   String constant=childR.getText();
		    	   if(constant==null||constant.length()<0)
		    	   {
		    		   constant="";
		    	   }
//		    	   String backgroudimage = childR.getAttributeValue("backgroudimage");
		    	   String backgroudimage = childR.getAttributeValue("fileid");
		    	   
		    	   if(backgroudimage==null||backgroudimage.length()<0)
		    	   {
		    		   backgroudimage="";
		    	   }
		    	   hashMap.put("view_em",em);
		    	   hashMap.put("view_hr",hr);
		    	   hashMap.put("start_date",start_date);					   
		    	   hashMap.put("days",days); //考勤工号					   
		    	   hashMap.put("constant",constant); //考勤方式	
		    	   hashMap.put("backgroudimage", backgroudimage);
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
	public String creatSysNoteXml(String start_date,String days,String constant,String backgroudimage)
	{
	  String temp = null;		
	  try
	  {
		Element notes = new Element("notes");
		Element note = new Element("note");
		note.setAttribute("start_date",start_date);
		note.setAttribute("days",days);
		note.setAttribute("hr",this.getView_hr());
		note.setAttribute("em",this.getView_em());
//		note.setAttribute("backgroudimage",backgroudimage);
		note.setAttribute("fileid",backgroudimage);
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
	public String getSysNoteXML()
	{
		  String constant = "constant";
		  if(Sql_switcher.searchDbServer()==Constant.KUNLUN) {
              constant = "\"constant\"";
          }
		  StringBuffer sb=new StringBuffer();
		  sb.append("select Str_Value from ").append(constant).append(" where UPPER(").append(constant).append(")='SYS_NOTE'");
		  ContentDAO dao = new ContentDAO(conn);
		  RowSet rowSet=null;
		  String xmlConstant="";
		  try
		  {
			  rowSet=dao.search(sb.toString());
			  if(rowSet.next())
			  {
				  xmlConstant=Sql_switcher.readMemo(rowSet, "Str_Value");
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
	public void addSysNoteXML(String constantXml)
	{
		  String sql="select * from constant where UPPER(Constant)='SYS_NOTE'";
		  ContentDAO dao = new ContentDAO(conn);
		  RowSet rs=null;
		  try
		  {
			rs=dao.search(sql);		  
			  if(rs.next())
			  {
				  updateSysNoteXML(constantXml); 
			  }else
			  {
				  insertSysNoteXML(constantXml);
			  }
		  }catch(Exception e)
		  {
			  e.printStackTrace();
		  }		 
	}
	public void updateSysNoteXML(String constantXml)
	{
		  StringBuffer sb=new StringBuffer();
		  //改用预编译方式  wangb 20181101 
//		  sb.append("update constant set Str_Value='"+constantXml+"' where UPPER(Constant)='SYS_NOTE'");
		  sb.append("update constant set Str_Value=? where UPPER(Constant)='SYS_NOTE'");
		  ArrayList list =new ArrayList();
		  list.add(constantXml);
		  ContentDAO dao = new ContentDAO(conn);
		  try
		  {
			dao.update(sb.toString(),list);		  
			  
		  }catch(Exception e)
		  {
			  e.printStackTrace();
		  }		 
	}
	/**
	 * 插入新的
	 * @param constantXml
	 */
	public void insertSysNoteXML(String constantXml)
	{
		String insert="insert into constant(Constant,Str_Value) values (?,?)";
		ArrayList list=new ArrayList();
		list.add("SYS_NOTE");
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
