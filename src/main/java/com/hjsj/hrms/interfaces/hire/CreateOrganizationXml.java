package com.hjsj.hrms.interfaces.hire;

import com.hjsj.hrms.utils.PubFunc;
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
import java.sql.Statement;
/**
 * <p>Title:在web前台界面生成组织机构树形</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:May 12, 2005:1:54:19 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class CreateOrganizationXml {
    /**
     * 参数串
     */
    private String params;
    /**
     * 执行jsp文件
     */
    private String action;
    /**
     * 目标窗口
     */
    private String target;
    /**人员还是对组织*/
    private String flag;

    /**
     * 构造函数
     * @param params
     * @param action
     */
    public CreateOrganizationXml(String params,String action,String target,String flag) {
        this.params=params;
        this.target=target;
        this.action=action;
        this.flag=flag;
    }
    /**
     * 分析是否有子节点
     * @param codeitemid
     * @param conn
     * @return
     */
    private boolean HaveChild(String codeitemid,Connection conn)
    {
        StringBuffer strsql=new StringBuffer();
        boolean bhave=false;
        strsql.append("select count(*) as num from organization where parentid='");
        strsql.append(codeitemid);
        strsql.append("'");
        ResultSet rset=null;
        int ncount=0;
        ContentDAO dao=new ContentDAO(conn);
        try
        {
             rset=dao.search(strsql.toString());
             ncount=rset.getInt("num");
             //System.out.println("------->record count="+ncount);
             if(ncount<=0)
                 bhave= false;
             else
                 bhave= true;
        }
        catch(SQLException sqle)
        {
            sqle.printStackTrace();
        }
        finally
        {
            try
            {
                if(rset!=null)
                    rset.close();
            }
            catch(SQLException sql)
            {
                sql.printStackTrace();
            }
        }

        return bhave;
    }
    
    public String outOrganizationTree()throws GeneralException
    {
        StringBuffer xmls = new StringBuffer();
        StringBuffer strsql = new StringBuffer();
        Statement stmt = null;
        ResultSet rset = null;
        Connection conn = AdminDb.getConnection();
        ContentDAO dao = new ContentDAO(conn);
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
          //stmt = conn.createStatement();
          //System.out.println("SQL="+strsql.toString());
         // rset = stmt.executeQuery(strsql.toString());
          rset = dao.search(strsql.toString());
          while (rset.next())
          {
            Element child = new Element("TreeNode");
            child.setAttribute("id", rset.getString("codeitemid"));
            child.setAttribute("text", rset.getString("codeitemdesc"));
            child.setAttribute("title", rset.getString("codeitemdesc"));
            theaction=this.action+"?b_query=link&a_code="+rset.getString("codesetid")+rset.getString("codeitemid");
            child.setAttribute("href", theaction);
            child.setAttribute("target", this.target);
            //if(!HaveChild(rset.getString("codeitemid"),conn))
            //update by wangchaoqun on 2014-9-24
            child.setAttribute("xml", "/hire/zp_options/get_org_tree.jsp?encryptParam="+PubFunc.encrypt("flag="+flag+"&params=parentId<>codeitemid and parentid%3D'" + rset.getString("codeitemid")+"'"));
            if("UN".equals(rset.getString("codesetid")))
                child.setAttribute("icon","/images/unit.gif");
            if("UM".equals(rset.getString("codesetid")))
                child.setAttribute("icon","/images/dept.gif");
            if("@K".equals(rset.getString("codesetid")))
                child.setAttribute("icon","/images/pos_l.gif");
            
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
            if (stmt != null)
            {
              stmt.close();
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
}
