package com.hjsj.hrms.servlet.orgtree;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.log4j.Category;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.RowSet;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("all")
public class GrouptreeServlet  extends HttpServlet {
	   /* 
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
	private  Category cat = Category.getInstance(this.getClass());
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException { 
    	StringBuffer sbXml = new StringBuffer();
    	String params=req.getParameter("params");
        String action=req.getParameter("action");
        String target=req.getParameter("target");
        String id=(String)req.getParameter("id");
        String flag=(String)req.getParameter("flag");
		String dbtype=(String)req.getParameter("dbtype");
		String kq_type=(String)req.getParameter("kq_type");      
    	try {
    		UserView userview=(UserView) req.getSession().getAttribute(WebConstant.userView);	
			sbXml.append(loadOrgGroupNodes(userview,params,action,target,flag,id,kq_type,dbtype));
		} catch (Exception e) {
			System.out.println(e);
		}
		resp.setContentType("text/xml;charset=UTF-8");		
		resp.getWriter().println(sbXml.toString());
    }
    private String  loadOrgGroupNodes(UserView userview,String params,String action,String target,String flag,String parentid,String kq_type,String dbtype) throws Exception
    {
    	
    	StringBuffer xmls = new StringBuffer();
        StringBuffer strsql = new StringBuffer();
        Connection conn = AdminDb.getConnection();     
        ContentDAO dao = new ContentDAO(conn);
        ResultSet rset = null;
        
        Element root = new Element("TreeNode");
        root.setAttribute("id","00");
        root.setAttribute("text","root");
        root.setAttribute("title","organization");
        Document myDocument = new Document(root);
        String theaction=null;
        try
        {
          strsql.append("select codesetid,codeitemid,codeitemdesc,childid from organization where ");
          strsql.append(params);
          rset = dao.search(strsql.toString());
          /**加载组织机构树*/
          while (rset.next())
          {
            Element child = new Element("TreeNode");
            child.setAttribute("id", rset.getString("codesetid")+rset.getString("codeitemid"));
            child.setAttribute("text", rset.getString("codeitemdesc"));
            child.setAttribute("title", rset.getString("codeitemdesc"));
            if(!(action==null|| "".equals(action)))
            {
		        theaction=action+"?b_search=link&a_code="+rset.getString("codesetid")+rset.getString("codeitemid");
            }
            if(theaction==null|| "".equals(theaction))
            	child.setAttribute("href", "javascript:void(0)");            	
            else
            	child.setAttribute("href", theaction);
            child.setAttribute("target", target);
    		String url="/common/org/group/loadtree?target="+target+"&flag="+flag+"&dbtype="+dbtype+"&action="+action;
    		url=url+"&kq_type="+kq_type+"&params=parentId<>codeitemid and parentid%3D'" + rset.getString("codeitemid")+"'";
    		url=url+"&id="+rset.getString("codesetid")+rset.getString("codeitemid");
            child.setAttribute("xml", url);
            if("UN".equals(rset.getString("codesetid")))
                child.setAttribute("icon","/images/unit.gif");
            if("UM".equals(rset.getString("codesetid")))
                child.setAttribute("icon","/images/dept.gif");
            if("@K".equals(rset.getString("codesetid")))
                child.setAttribute("icon","/images/pos_l.gif");
            root.addContent(child);
          }
          /**加载当前机构下的人员*/
          if(kq_type!=null&&kq_type.length()>0)
  		  {
  			 
  		  }
           if("1".equals(flag))
        	   getGroups_Href(userview,parentid,root,conn,action,target);
          XMLOutputter outputter = new XMLOutputter();
          Format format=Format.getPrettyFormat();
          format.setEncoding("UTF-8");
          outputter.setFormat(format);
          xmls.append(outputter.outputString(myDocument));
          //System.out.println("SQL=" +xmls.toString());
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
    /**
     * 得到连接的人员节点
     * @param userview
     * @param parentid
     * @param root
     * @param conn
     */
   private void getGroups_Href(UserView userview,String parentid,Element root,Connection conn,String action,String target)
    {
	  
	  StringBuffer strsql=new StringBuffer();	
      if(parentid!=null&&parentid.length()>2)
      {
    	  strsql.append("select group_id,name,org_id from kq_shift_group where 1=1 ");
    	  String codeid=parentid.substring(0,2);
    	  String codevalue=parentid.substring(2);
    	  strsql.append(" and org_id='"+codevalue+"'");
      }
      if("".equals(strsql.toString()))
    	  return;
      ContentDAO dao=new ContentDAO(conn);
      RowSet rset=null;
      String theaction=null;
      try
      {
    	  rset=dao.search(strsql.toString());
    	  while(rset.next())
    	  {
    		  String group_id=rset.getString("group_id");
    		  String name=rset.getString("name");    		 
              Element child = new Element("TreeNode");
              child.setAttribute("id", "GP"+group_id);
              if(name==null)
            	  name="";
              child.setAttribute("text", name);
              child.setAttribute("title", name);
              child.setAttribute("target",target);
              if(!(action==null|| "".equals(action)))
              {
  		        theaction=action+"?b_search=link&a_code=GP"+rset.getString("group_id");
              }
              if(theaction==null|| "".equals(theaction))
              	child.setAttribute("href", "javascript:void(0)");            	
              else
              	child.setAttribute("href", theaction);             
              child.setAttribute("icon","/images/group_p.gif");
              root.addContent(child);
    	  }
      }
      catch(Exception ex)
      {
    	  ex.printStackTrace();
      }
    }
 
}
