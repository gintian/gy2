package com.hjsj.hrms.interfaces.sys;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * <p>Title:在web前台界面生成组织机构树形</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:May 12, 2005:1:54:19 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class CreateTreeXml {
   
    /**
     * 执行jsp文件
     */
    private String action;
    /**
     * 目标窗口
     */
    private String target;
      
    /**代码类id */
	private String codeSetID="";
    private boolean bfirst=true;
    public boolean isBfirst() {
		return bfirst;
	}


	public void setBfirst(boolean bfirst) {
		this.bfirst = bfirst;
	}


	/**
     * 构造函数
     * @param params
     * @param action
     * @param target
     * @param flag
     */
    public CreateTreeXml(String action,String target,String codeSetID) {
        this.target=target;
        this.action=action;      
        this.codeSetID=codeSetID;
    }
    
  
    public String outOrgEmployTree(UserView userview,String parentid)throws GeneralException
    {
        StringBuffer xmls = new StringBuffer();
        StringBuffer strsql = new StringBuffer();
        ResultSet rset = null;
        Connection conn = AdminDb.getConnection();
        Element root = new Element("TreeNode");
        root.setAttribute("id","00");
        root.setAttribute("text","root");
        root.setAttribute("title","organization");
        Document myDocument = new Document(root);
        String theaction=null;
        try
        {        
           	 strsql.append(getSql(parentid,codeSetID,userview,conn));
           	ContentDAO dao = new ContentDAO(conn);
	          rset = dao.search(strsql.toString());
	          /**加载组织机构树*/
	          while (rset.next())
	          {
	        	String codeitemid=rset.getString("codeitemid");  
	        	String a_codesetid=rset.getString("codesetid");
	        	//权限控制
	        	if(!userview.isAdmin()&&("UN".equals(codeSetID)|| "UM".equals(codeSetID)|| "@K".equals(codeSetID)))
	        	{
	        		String privCodeValue=userview.getManagePrivCodeValue();
	        		if(codeitemid.length()>=privCodeValue.length())
	        		{
	        			if(codeitemid.toLowerCase().indexOf(privCodeValue.toLowerCase())==-1)
	        				continue;
	        		}
	        		else
	        		{
	        			if(privCodeValue.toLowerCase().indexOf(codeitemid.toLowerCase())==-1)
	        				continue;
	        		}
	        	}
	        	  
	            Element child = new Element("TreeNode");
	            child.setAttribute("id",codeitemid);
	            child.setAttribute("text", rset.getString("codeitemdesc"));
	            child.setAttribute("title", rset.getString("codeitemdesc"));
	          
	            if(theaction==null|| "".equals(theaction))
	            	child.setAttribute("href", "javascript:void(0)");            	
	            else
	            	child.setAttribute("href", theaction);
	            child.setAttribute("target", this.target);
	    		String url="/system/load_tree2?target="+this.target+"&codeSetID="+this.codeSetID;	    	
	    		url=url+"&id="+rset.getString("codeitemid");
	            if(!rset.getString("codeitemid").equalsIgnoreCase(rset.getString("childid")))
	            	child.setAttribute("xml", url);	 
	            
	            if("UN".equals(a_codesetid))
	                child.setAttribute("icon","/images/unit.gif");
	            else if("UM".equals(a_codesetid))
		             child.setAttribute("icon","/images/dept.gif");
	            else if("@K".equals(a_codesetid))
		        	 child.setAttribute("icon","/images/pos_l.gif");
	            else	
	            	child.setAttribute("icon","/images/table.gif");  
	            
	            root.addContent(child);
	          }
	          XMLOutputter outputter = new XMLOutputter();
	          Format format=Format.getPrettyFormat();
	          format.setEncoding("UTF-8");
	          outputter.setFormat(format);
	          xmls.append(outputter.outputString(myDocument));
        }
        catch (Exception ee)
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
    
 

    private String getSql(String parentid,String codeSetID,UserView userview,Connection conn)
    {
    	StringBuffer strsql=new StringBuffer("");   	
    	if(!"UN".equals(codeSetID)&&!"UM".equals(codeSetID)&&!"@K".equals(codeSetID))
    	{
    		  if(parentid==null|| "0".equals(parentid))
        		  strsql.append("select * from codeitem where codesetid='"+this.codeSetID+"' and codeitemid=parentid");
        	  else
        		  strsql.append("select * from codeitem where codesetid='"+this.codeSetID+"' and codeitemid<>parentid  and parentid='"+parentid+"'");
    	}
    	else
    	{
    		 if((parentid==null|| "0".equals(parentid))&&this.isBfirst())
    		 {
    			
       		  	strsql.append("select codeitemid,codeitemdesc,codesetid,childid from organization where codesetid='UN' and codeitemid=parentid  ");
    		 } 
       		 else
       		 {
       			 if("UN".equals(codeSetID))
       				 strsql.append("select codeitemid,codeitemdesc,codesetid,childid from organization where codesetid='UN' and codeitemid<>parentid  and parentid='"+parentid+"'");
       			 if("UM".equals(codeSetID))
       				 strsql.append("select codeitemid,codeitemdesc,codesetid,childid from organization where codesetid!='@K' and codeitemid<>parentid  and parentid='"+parentid+"'");
       			 if("@K".equals(codeSetID))
      				 strsql.append("select codeitemid,codeitemdesc,codesetid,childid from organization where  codeitemid<>parentid  and parentid='"+parentid+"'");
      		 
       		 }
       	}
    	
    	return strsql.toString();
    }
    
    
	
}
