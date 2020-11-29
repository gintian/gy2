package com.hjsj.hrms.interfaces.sys;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>Title:CreateCodeXml</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 09, 2007:5:08:57 PM</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class CreateHireXml {
	   /**代码类*/
    private String codesetid;
    /**代码项*/
    private String codeitemid;
    /**根据管理范围找第一层代码*/
    private String privflag;
    /*
     * 为了显示第一层为管理的最高层的节点
     * */
    private String isfirstnode;
    /**
     * 
     */
    public CreateHireXml(String codesetid,String codeitemid) {
        this.codeitemid=PubFunc.getReplaceStr(codeitemid);
        this.codesetid=PubFunc.getReplaceStr(codesetid);
    }
    
    public CreateHireXml(String codesetid,String codeitemid,String privflag) {
    	this(codesetid,codeitemid);
    	this.privflag=privflag;
    }  
    public CreateHireXml(String codesetid,String codeitemid,String privflag,String isfirstnode) {
    	this(codesetid,codeitemid);
    	this.privflag=privflag;
    	this.isfirstnode=isfirstnode;
    }
    private String getZ0311(int flag){
    	String id = "";
    	Set set = new HashSet();
    	Connection conn = null;
    	RowSet rs = null;
		try{
			conn = AdminDb.getConnection();
	    	ContentDAO dao = new ContentDAO(conn);
			rs = dao.search("select z0311 from z03");
			while(rs.next()){
				if(rs.getString("z0311").length()>=flag){
					set.add(rs.getString("z0311").substring(0,flag));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally
        {
	          try
	          {
	            if (rs != null)
	            {
	            	rs.close();
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
		if(!set.isEmpty()){
			id = "("+set.toString().replace("["," ").replace("]"," ") + ")";
		}
		return id;
    }
    /**求查询代码的字符串*/
    private String getQueryString()
    {
        StringBuffer str=new StringBuffer();
        if("UN".equalsIgnoreCase(this.codesetid)|| "UM".equalsIgnoreCase(this.codesetid)|| "@K".equalsIgnoreCase(this.codesetid))
        {
            if("UN".equalsIgnoreCase(this.codesetid)){
                str.append("select codesetid,codeitemid,codeitemdesc,childid ");
                str.append(" from organization where codesetid='");
                str.append(this.codesetid);
                str.append("'"); 
            }
            else if("UM".equalsIgnoreCase(this.codesetid))
            {
                str.append("select codesetid,codeitemid,codeitemdesc,childid (codesetid='");
                str.append(" from organization where (codesetid='");
                str.append(this.codesetid);
                str.append("' or codesetid='UN') "); 
            }  
            else if ("@K".equalsIgnoreCase(this.codesetid)){
            	str.append("select oz.codesetid,oz.codeitemid,oz.codeitemdesc,oz.childid,z.z0301");
                str.append(" from organization oz,z03 z where (oz.codesetid='");
                str.append(this.codesetid);
                str.append("' or oz.codesetid='UN' or oz.codesetid='UM') ");
            }               
        }
        else
        {
        	str.append("select codesetid,codeitemid,codeitemdesc,childid ");
            str.append(" from organization where codesetid='");
            str.append(this.codesetid);
            str.append("'");         
        }
        /**所有的第一层代码值列表*/
        if(privflag==null|| "".equals(privflag))
        {
             if(this.isfirstnode!=null && "1".equals(this.isfirstnode))
            {
            	if(this.codeitemid==null|| "".equals(this.codeitemid)|| "ALL".equals(this.codeitemid))
		        {
		              str.append(" and parentid=codeitemid");
		        }
		        else
		        {
		            str.append(" and codeitemid='");
		            str.append(codeitemid);
		            str.append("'");
		        } 
            }else
            {
	        	if(this.codeitemid==null|| "".equals(this.codeitemid)|| "ALL".equals(this.codeitemid))
		        {
	        		String id = this.getZ0311(3);
		            str.append(" and parentid=codeitemid");
		            if(id.length()>0){
		            	str.append(" and codeitemid in");
		            	str.append(id);
		            }
		        }
		        else
		        {
		        	if ("@K".equalsIgnoreCase(this.codesetid)){
		        		str.append(" and oz.parentid<>oz.codeitemid and oz.parentid like'");
		        		str.append(codeitemid);
		        		str.append("%'");
		        		str.append(" and oz.codeitemid=z.z0311");
		        	}else{
		        		String id = this.getZ0311(codeitemid.length()+3);
		        		str.append(" and parentid<>codeitemid and parentid='");
		        		str.append(codeitemid);
		        		str.append("'");
		        		if(id.length()>codeitemid.length()+3){
			            	str.append(" and codeitemid in");
			            	str.append(id); 
			            }
		        	}
		        }
        	} 
        }
        else //根据管理范围过滤相应的节点内容
        {
            if("ALL".equals(this.codeitemid))
	        {
            	   str.append("and parentid=codeitemid");
	        }
            else if(this.codeitemid==null|| "".equals(this.codeitemid))
            {
         	   str.append(" and 1=2");            	
            }
	        else
	        {
	        	str.append(" and codeitemid='");
	           	str.append(codeitemid);
	        	str.append("'");
	        }
        }
        if("UN".equalsIgnoreCase(this.codesetid)|| "UM".equalsIgnoreCase(this.codesetid)|| "@K".equalsIgnoreCase(this.codesetid))
        {
        	if ("@K".equalsIgnoreCase(this.codesetid)){
        		str.append(" ORDER BY oz.a0000,oz.codeitemid ");
        	}else{
        		str.append(" ORDER BY a0000,codeitemid ");
        	}
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
        try
        {
          
          strsql.append(getQueryString());
          ContentDAO dao = new ContentDAO(conn);
          rset = dao.search(strsql.toString());
          while (rset.next())
          {
            Element child = new Element("TreeNode");
            String itemid=rset.getString("codeitemid");
            if(itemid==null)
            	itemid="";
            itemid=itemid.trim();
            String codesetid=rset.getString("codesetid");
            if ("@K".equalsIgnoreCase(this.codesetid)){
            	String z0311 = rset.getString("z0301");
            	child.setAttribute("id", codesetid+itemid+"-"+z0311);
            }else{
            	child.setAttribute("id", codesetid+itemid);
            }
            child.setAttribute("text", rset.getString("codeitemdesc"));
            child.setAttribute("title", itemid+":"+rset.getString("codeitemdesc"));
            if(!itemid.equalsIgnoreCase(rset.getString("childid")))
            	child.setAttribute("xml", "/system/get_hire_tree.jsp?codesetid=" + this.codesetid/*rset.getString("codesetid")*/+"&codeitemid="+itemid);
            if("UN".equals(codesetid))
            	child.setAttribute("icon","/images/unit.gif");
            else if("UM".equals(codesetid))
            	child.setAttribute("icon","/images/dept.gif");
            else if("@K".equals(codesetid))
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
