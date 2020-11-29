/*
 * Created on 2005-12-16
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.interfaces.sys;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CreateCodeActionXmlCanBack {
    /**代码类*/
    private String codesetid;
    /**代码项*/
    private String codeitemid;
    /**根据管理范围找第一层代码*/
    private String privflag;
    /**
     * 执行jsp文件
     */
    private String action;
    /**
     * 目标窗口
     */
    private String target;
    /**
     * 获得子节点的jsp页名
     */
    private String getcodetree;
    
    private ArrayList<String> valueList = new ArrayList<String>();
    /*
     * 
     */
    private String backdate;
    private String param;
    public CreateCodeActionXmlCanBack(String codesetid,String codeitemid) {
        this.codeitemid=codeitemid;
        this.codesetid=codesetid;
        this.getcodetree="/system/get_code_tree.jsp";
    }
    
    public CreateCodeActionXmlCanBack(String codesetid,String codeitemid,String privflag) {
    	this(codesetid,codeitemid);
    	this.privflag=privflag;
    	this.getcodetree="/system/get_code_tree.jsp";
    } 
    public CreateCodeActionXmlCanBack(String codesetid,String codeitemid,String action,String target,String privflag) {
    	this(codesetid,codeitemid);
    	this.privflag=privflag;
    	this.action=action;
    	this.target=target;
    	this.getcodetree="/system/get_code_tree.jsp";
    	/**/
    }    
    public CreateCodeActionXmlCanBack(String codesetid,String codeitemid,String action,String target,String privflag,String getcodetree,String backdate) {
    	this(codesetid,codeitemid);
    	this.privflag=privflag;
    	this.action=action;
    	this.target=target;
    	this.getcodetree=getcodetree;
    	if(backdate==null||backdate.length()<=8){
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    		backdate = backdate!=null&&backdate.length()>9?backdate:sdf.format(new Date());
    	}
    	this.backdate=backdate;
    	/**/
    }    
    public CreateCodeActionXmlCanBack(String codesetid,String codeitemid,String action,String target,String privflag,String getcodetree,String backdate,String param) {
    	this(codesetid,codeitemid);
    	this.privflag=privflag;
    	this.action=action;
    	this.target=target;
    	this.getcodetree=getcodetree;
    	if(backdate==null||backdate.length()<=8){
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    		backdate = backdate!=null&&backdate.length()>9?backdate:sdf.format(new Date());
    	}
    	this.backdate=backdate;
    	this.param=param;
    	/**/
    }   
    /**求查询代码的字符串*/
    private String getQueryString()
    {
        StringBuffer str=new StringBuffer();
        if("UN".equalsIgnoreCase(this.codesetid)|| "UM".equalsIgnoreCase(this.codesetid)|| "@K".equalsIgnoreCase(this.codesetid))
        {
            /*if(this.codesetid.equalsIgnoreCase("UN"))
            {
                str.append("select codesetid,codeitemid,codeitemdesc,childid from organization where codesetid='");
                str.append(this.codesetid);
                str.append("'");
            }
            else if(this.codesetid.equalsIgnoreCase("UM"))
            {
                str.append("select codesetid,codeitemid,codeitemdesc,childid from organization where (codesetid='");
                str.append(this.codesetid);
                str.append("' or codesetid='UN') ");
            }  
            else if (this.codesetid.equalsIgnoreCase("@K"))
            {
                str.append("select codesetid,codeitemid,codeitemdesc,childid from organization where (codesetid='");
                str.append(this.codesetid);
                str.append("' or codesetid='UN' or codesetid='UM') ");
            }*/
        	//str.append("select codesetid from organization where 1=2 ");
        	str.append("select codesetid,codeitemid,codeitemdesc,childid from codeitem where codesetid=?");
            this.valueList.add(this.codesetid);
        }
        else
        {
            str.append("select codesetid,codeitemid,codeitemdesc,childid from codeitem where codesetid=?");  
            this.valueList.add(this.codesetid);
        }
        /**所有的第一层代码值列表*/
        if(privflag==null|| "".equals(privflag))
        {
        	if(this.codeitemid==null|| "".equals(this.codeitemid)|| "ALL".equals(this.codeitemid))
	        {
	              str.append(" and parentid=codeitemid");
	        }
	        else
	        {
	            str.append(" and parentid<>codeitemid and parentid=?");
	            this.valueList.add(this.codeitemid);
	         }    
        }
        else //根据管理范围过滤相应的节点内容
        {
            if("ALL".equals(this.codeitemid))
	        {
            	   str.append(" and parentid=codeitemid");
	        }else if(this.codeitemid==null|| "".equals(this.codeitemid)){
	        	str.append(" and 1=2");
	        }
	        else
	        {
	        	String[] codeitemids; 
	        	if(this.codeitemid.indexOf("','") > -1)
	        		codeitemids = this.codeitemid.split("','");
	        	else if(this.codeitemid.indexOf("＇,＇") > -1)
	        		codeitemids = this.codeitemid.split("＇,＇");
	        	else
	        		codeitemids = this.codeitemid.split(",");
	        	
	        	StringBuffer value = new StringBuffer();
	        	for(int i = 0; i <codeitemids.length; i++) {
	        		String id = codeitemids[i];
	        		if(StringUtils.isEmpty(id))
	        			continue;
	        		
	        		value.append("?,");
	        		this.valueList.add(id);
	        	}
	        	
	        	if(value.toString().endsWith(","))
	        		value.setLength(value.length() - 1);
	        	
	        	str.append(" and codeitemid in (");
	           	str.append(value);
	        	str.append(")");
	        	
	        	
	        }
        }
        str.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date order by codeitemid,a0000"); //zhaogd 2013-11-21 党团组织机构树排序有误，a0000无值，改用codeitemid排序。
        return str.toString();
    }
    
    public String outCodeTree()throws GeneralException
    {
        StringBuffer xmls = new StringBuffer();
        StringBuffer strsql = new StringBuffer();
        ResultSet rset = null;
        Connection conn = AdminDb.getConnection();
        Element root = new Element("TreeNode");
        root.setAttribute("id","$$00");
        root.setAttribute("text","root");
        root.setAttribute("title","codeitem");
        Document myDocument = new Document(root);
        String theaction=null;
        try
        {
          
            strsql.append(getQueryString());
//          strsql.append("select codesetid,codeitemid,codeitemdesc,childid from codeitem where codesetid='");
//          
//          strsql.append(this.codesetid);
//          /**所有的第一层代码值列表*/
//          if(this.codeitemid==null||this.codeitemid.equals("")||this.codeitemid.equals("ALL"))
//          {
//              strsql.append("' and parentid=codeitemid");
//          }
//          else
//          {
//              strsql.append("' and parentid<>codeitemid and parentid='");
//              strsql.append(codeitemid);
//              strsql.append("'");
//          }
          
          //System.out.println("SQL="+strsql.toString());
          ContentDAO dao = new ContentDAO(conn);
          rset = dao.search(strsql.toString(), this.valueList);
          while (rset.next())
          {
            Element child = new Element("TreeNode");
            child.setAttribute("id", rset.getString("codesetid")+rset.getString("codeitemid"));
            child.setAttribute("text", rset.getString("codeitemdesc"));
            child.setAttribute("title", rset.getString("codeitemdesc"));
            if("/dtgh/party/searchpartybusinesslist.do".equalsIgnoreCase(this.action)){
            	theaction=this.action+"?b_query=link&a_code="+rset.getString("codesetid")+rset.getString("codeitemid");//+"&politics=";
            }else{
            	theaction=this.action+"?b_search=link&a_code="+rset.getString("codesetid")+rset.getString("codeitemid");//+"&politics=";
            }
            
            child.setAttribute("href", theaction);
            child.setAttribute("target", this.target);
            child.setAttribute("xml", this.getcodetree + "?codesetid=" + this.codesetid/*rset.getString("codesetid")*/+"&codeitemid="+rset.getString("codeitemid"));
            if(!"H".equals(param))
            	child.setAttribute("icon","/images/unit.gif");
            else{
            	child.setAttribute("icon","/images/table.gif");
            }
            root.addContent(child);
          }

          XMLOutputter outputter = new XMLOutputter();
          Format format=Format.getPrettyFormat();
          format.setEncoding("UTF-8");
          outputter.setFormat(format);
          xmls.append(outputter.outputString(myDocument));
          //System.out.println("SQL=" +xmls.toString());
        }
        catch (SQLException ee)
        {
          ee.printStackTrace();
          GeneralExceptionHandler.Handle(ee);
        }
        finally
        {
          try
          {
            if (rset != null)
            {
              rset.close();
            }
            if (conn != null)
            {
              conn.close();
            }
          }
          catch (SQLException ee)
          {
            ee.printStackTrace();
          }
          
      }
      return xmls.toString();        
    }
    public String outCodeItemTree()throws GeneralException
    {
        StringBuffer xmls = new StringBuffer();
        StringBuffer strsql = new StringBuffer();
        ResultSet rset = null;
        Connection conn = AdminDb.getConnection();
        Element root = new Element("TreeNode");
        root.setAttribute("id","$$00");
        root.setAttribute("text","root");
        root.setAttribute("title","codeitem");
        Document myDocument = new Document(root);
        String theaction=null;
        try
        {
          
          strsql.append(getQueryString());
          ContentDAO dao = new ContentDAO(conn);
          rset = dao.search(strsql.toString(), this.valueList);
          while (rset.next())
          {
            Element child = new Element("TreeNode");
            child.setAttribute("id", rset.getString("codeitemid"));
            child.setAttribute("text", rset.getString("codeitemdesc"));
            child.setAttribute("title", rset.getString("codeitemdesc"));
            theaction=this.action+"&a_code="+rset.getString("codeitemid");
            child.setAttribute("href", theaction);
            child.setAttribute("target", this.target);
            child.setAttribute("xml", this.getcodetree + "?codesetid=" + this.codesetid/*rset.getString("codesetid")*/+"&codeitemid="+rset.getString("codeitemid"));
            child.setAttribute("icon","/images/book.gif");
            root.addContent(child);
          }

          XMLOutputter outputter = new XMLOutputter();
          Format format=Format.getPrettyFormat();
          format.setEncoding("UTF-8");
          outputter.setFormat(format);
          xmls.append(outputter.outputString(myDocument));
          //System.out.println("SQL=" +xmls.toString());
        }
        catch (SQLException ee)
        {
          ee.printStackTrace();
          GeneralExceptionHandler.Handle(ee);
        }
        finally
        {
          try
          {
            if (rset != null)
            {
              rset.close();
            }
           
            if (conn != null)
            {
              conn.close();
            }
          }
          catch (SQLException ee)
          {
            ee.printStackTrace();
          }
          
      }
      return xmls.toString();        
    }
    public String outCodeItemTree1()throws GeneralException
    {
        StringBuffer xmls = new StringBuffer();
        StringBuffer strsql = new StringBuffer();
        ResultSet rset = null;
        Connection conn = AdminDb.getConnection();
        Element root = new Element("TreeNode");
        root.setAttribute("id","$$00");
        root.setAttribute("text","root");
        root.setAttribute("title","codeitem");
        Document myDocument = new Document(root);
        String theaction=null;
        try
        {
          strsql.append(getQueryString());
          ContentDAO dao = new ContentDAO(conn);
          rset = dao.search(strsql.toString(), this.valueList);
          while (rset.next())
          {
            Element child = new Element("TreeNode");
            child.setAttribute("id", rset.getString("codeitemid"));
            child.setAttribute("text", rset.getString("codeitemdesc"));
            child.setAttribute("title", rset.getString("codeitemdesc"));
            //theaction=this.action+"&a_code="+rset.getString("codeitemid");
            //child.setAttribute("href", theaction);
            //child.setAttribute("target", this.target);
            child.setAttribute("xml", this.getcodetree + "?codesetid=" + this.codesetid/*rset.getString("codesetid")*/+"&codeitemid="+rset.getString("codeitemid"));
            child.setAttribute("icon","/images/book.gif");
            root.addContent(child);
          }

          XMLOutputter outputter = new XMLOutputter();
          Format format=Format.getPrettyFormat();
          format.setEncoding("UTF-8");
          outputter.setFormat(format);
          xmls.append(outputter.outputString(myDocument));
          //System.out.println("SQL=" +xmls.toString());
        }
        catch (SQLException ee)
        {
          ee.printStackTrace();
          GeneralExceptionHandler.Handle(ee);
        }
        finally
        {
          try
          {
            if (rset != null)
            {
              rset.close();
            }
           
            if (conn != null)
            {
              conn.close();
            }
          }
          catch (SQLException ee)
          {
            ee.printStackTrace();
          }
          
      }
      return xmls.toString();        
    }
    
    public String getCodeitemid() {
        return codeitemid;
    }
    public void setCodeitemid(String codeitemid) {
        this.codeitemid = codeitemid;
    }
    public String getCodesetid() {
        return codesetid;
    }
    public void setCodesetid(String codesetid) {
        this.codesetid = codesetid;
    }
}
