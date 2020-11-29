package com.hjsj.hrms.interfaces.kq;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.util.ArrayList;


public class CreateFieldBySetXml {
	/**代码类*/
    private String setname;
    /**代码项*/
    private String setType;
    private String control="1";//0:不控制权限；1：控制权限
    public CreateFieldBySetXml()
    {
    	
    }
    public CreateFieldBySetXml(String setname,String setType)
    {
    	this.setname=setname;
    	this.setType=setType;
    }
    public String outFieldByTree()throws GeneralException 
    {
    	ArrayList fielditemlist=DataDictionary.getFieldList(this.setname,Constant.USED_FIELD_SET);
    	String z0=this.setname+"z0";
		String z1=this.setname+"z1";
    	 Element root = new Element("TreeNode");
    	 StringBuffer xmls = new StringBuffer();
         root.setAttribute("id","$$00");
         root.setAttribute("text","root");
         root.setAttribute("title","codeitem");
         Document myDocument = new Document(root);
         String theaction=null;
         try
         {
    	    for(int i=0;i<fielditemlist.size();i++)
	        {
	           FieldItem fielditem=(FieldItem)fielditemlist.get(i);
	           //System.out.println(fielditem.getItemid()+"---"+fielditem.getItemtype());
	           if("A".equalsIgnoreCase(this.setType.trim()))
	           {
	        	   if("A".equals(fielditem.getItemtype())||fielditem.getItemid().toString().toLowerCase().indexOf(z0.toLowerCase())!=-1)
		           {
		        	   if("N".equals(fielditem.getItemtype())&&fielditem.getItemid().toString().toLowerCase().indexOf(z1.toLowerCase())!=-1)
		        	   {
		        		    continue;        		  
		        	   }
		        	   //System.out.println(fielditem.getItemid()+"---"+fielditem.getItemtype());
		        	   Element child = new Element("TreeNode");
		               String itemid=fielditem.getItemid();
		               if(itemid==null)
		               	itemid="";
		               itemid=itemid.trim();
		               String itemdesc=fielditem.getItemdesc();
		               child.setAttribute("id", itemid);
		               child.setAttribute("text", itemdesc);
		               child.setAttribute("title", itemdesc);
		               //child.setAttribute("xml", "/kq/register/pigeonhole/get_code_tree.jsp?setType=" + this.setType/*rset.getString("codesetid")*/+"&itemid="+itemid);
		               child.setAttribute("icon","/images/table.gif");
		               root.addContent(child);
		           }
	           }else if(fielditem.getItemtype().equals(this.setType.trim())&&!"A".equals(this.setType.trim()))
	           {
	        	   if("N".equals(fielditem.getItemtype())&&fielditem.getItemid().toString().toLowerCase().indexOf(z1.toLowerCase())!=-1)
	        	   {
	        		    continue;        		  
	        	   }
	        	   if("D".equals(fielditem.getItemtype())&&fielditem.getItemid().toString().toLowerCase().indexOf(z0.toLowerCase())!=-1)
	        	   {
	        		   continue;
	        	   }
	        	   Element child = new Element("TreeNode");
	               String itemid=fielditem.getItemid();
	               if(itemid==null)
	               	itemid="";
	               itemid=itemid.trim();
	               String itemdesc=fielditem.getItemdesc();
	               child.setAttribute("id", itemid);
	               child.setAttribute("text", itemdesc);
	               child.setAttribute("title", itemdesc);
	               //child.setAttribute("xml", "/kq/register/pigeonhole/get_code_tree.jsp?setType=" + this.setType/*rset.getString("codesetid")*/+"&itemid="+itemid);
	               child.setAttribute("icon","/images/table.gif");
	               root.addContent(child);
	           }	    	  
	       }
    	    XMLOutputter outputter = new XMLOutputter();
            Format format=Format.getPrettyFormat();
            format.setEncoding("UTF-8");
            outputter.setFormat(format);
            xmls.append(outputter.outputString(myDocument));            
         }
    	 catch(Exception e)
    	 {
    	    e.printStackTrace();
    	    throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.pigeonhole.no.data"),"",""));
    	 }
    	 return xmls.toString();       
    }
}
