/*
 * Created on 2006-2-14
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.interfaces.kq;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author wxh
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CreateTree {
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
    public CreateTree(String params,String action,String target,String flag) {
        this.params=PubFunc.keyWord_reback(params);
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
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    String backdate =sdf.format(new Date());
        StringBuffer strsql=new StringBuffer();
        boolean bhave=false;
        strsql.append("select count(*) as num from organization where parentid='");
        strsql.append(codeitemid);
        strsql.append("'");
        strsql.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
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
    
    public String outTree()throws GeneralException
    {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    String backdate =sdf.format(new Date());
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
          strsql.append("select codesetid,codeitemid,codeitemdesc,childid from organization where ");
          strsql.append(params);     
          strsql.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
          strsql.append(" ORDER BY a0000,codeitemid ");
          
          ContentDAO dao=new ContentDAO(conn);
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
            child.setAttribute("xml", "/kq/app_check_in/get_org_tree.jsp?encryptParam="+PubFunc.encrypt("flag="+flag+"&params=parentId<>codeitemid and parentid%3D'" + rset.getString("codeitemid")+"'"));
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
        catch (Exception ee)
        {
          ee.printStackTrace();
          GeneralExceptionHandler.Handle(ee);
        }
        finally
        {
            PubFunc.closeResource(rset);
            PubFunc.closeResource(conn);
        }
        return xmls.toString();        
    }
}
