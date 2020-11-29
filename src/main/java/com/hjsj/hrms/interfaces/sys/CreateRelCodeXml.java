/*
 * 创建日期 2005-9-13
 *
 * TODO 要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
package com.hjsj.hrms.interfaces.sys;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author luangaojiong
 *
 * TODO 要更改此生成的类型注释的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
public class CreateRelCodeXml {
	
	String relTableName="";
	String relFieldDesc="";
	String relFieldId="0";
	String userOrgId="0";
   
    	/**
	 * @return 返回 userOrgId。
	 */
	public String getUserOrgId() {
		return userOrgId;
	}
	/**
	 * @param userOrgId 要设置的 userOrgId。
	 */
	public void setUserOrgId(String userOrgId) {
		this.userOrgId = userOrgId;
	}
/**
     * 
     */
   
    public CreateRelCodeXml(String relTableName,String relFieldId,String relFieldDesc,String userOrgId) {
    	this.relTableName=relTableName;
    	this.relFieldDesc=relFieldDesc;
    	this.relFieldId=relFieldId;
    	this.userOrgId=userOrgId;
    	
    }    
    /**求查询代码的字符串*/
    private String getQueryString()
    {
    	
        StringBuffer str=new StringBuffer();
        		if("r13".equals(relTableName) || "R13".equals(relTableName))
        		{
        			str.append("select ");
        			str.append(this.relFieldId);
        			str.append(",");
        			str.append(this.relFieldDesc);
        			str.append(" ");
        			str.append(" from ");
        			str.append(this.relTableName);
        			str.append(" where B0110='");
        			str.append(this.userOrgId);
        			str.append("' or B0110='HJSJ'");
        		}
        		else
        		{
        			str.append("select ");
        			str.append(this.relFieldId);
        			str.append(",");
        			str.append(this.relFieldDesc);
        			str.append(" ");
        			str.append(" from ");
        			str.append(this.relTableName);
        			str.append(" where B0110='");
        			str.append(this.userOrgId);
        			str.append("'");
        		}
                     
                       
        // System.out.println("---->CreateRelCodeXml-getQueryString-sql-->"+str.toString());
         if("".equals(this.relFieldDesc.trim()) || "".equals(this.relFieldId.trim()) || "".equals(this.relTableName.trim()))
         {
         	return "";
         }
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
        	if("".equals(getQueryString()))
        	{
        		return "";
        	}
          
            strsql.append(getQueryString());

          
          //System.out.println("SQL="+strsql.toString());
          ContentDAO dao = new ContentDAO(conn);
          rset = dao.search(strsql.toString());
          while (rset.next())
          {
            Element child = new Element("TreeNode");
           // System.out.println("---->CreateRelCodeXml-outCodeTree-->"+rset.getString(this.relFieldId));
            child.setAttribute("id", rset.getString(this.relFieldId));
            child.setAttribute("text", rset.getString(this.relFieldDesc));
            child.setAttribute("title", rset.getString(this.relFieldDesc));
            child.setAttribute("xml", "/system/get_relcode_tree.jsp?codesetid=" + this.relFieldId/*rset.getString("codesetid")*/+"&codeitemid="+rset.getString(this.relFieldDesc));
            child.setAttribute("icon","/images/table.gif");
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

     

	/**
	 * @return 返回 relFieldDesc。
	 */
	public String getRelFieldDesc() {
		return relFieldDesc;
	}
	/**
	 * @param relFieldDesc 要设置的 relFieldDesc。
	 */
	public void setRelFieldDesc(String relFieldDesc) {
		this.relFieldDesc = relFieldDesc;
	}
	/**
	 * @return 返回 relFieldId。
	 */
	public String getRelFieldId() {
		return relFieldId;
	}
	/**
	 * @param relFieldId 要设置的 relFieldId。
	 */
	public void setRelFieldId(String relFieldId) {
		this.relFieldId = relFieldId;
	}
	/**
	 * @return 返回 relTableName。
	 */
	public String getRelTableName() {
		return relTableName;
	}
	/**
	 * @param relTableName 要设置的 relTableName。
	 */
	public void setRelTableName(String relTableName) {
		this.relTableName = relTableName;
	}
}
