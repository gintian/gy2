package com.hjsj.hrms.interfaces.kq;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.sql.Connection;
import java.sql.ResultSet;

public class KqGroupDirectoryByXml {

	    private String params;	   
	    private String action;	  
	    private String target;
        private String image="";
        private UserView userView;
	    public KqGroupDirectoryByXml (UserView userView,String params,String action,String target) {
	        this.userView=userView;
	    	this.params=PubFunc.keyWord_reback(params);
	        this.target=target;
	        this.action=action;
	        
	    }
	    public String outTree()throws GeneralException
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
	          strsql.append("select group_id,name from kq_shift_group where 1=1 and ");
	          strsql.append(params);
	          strsql.append(" order by group_id");
	          
	          ContentDAO dao = new ContentDAO(conn);
	          rset = dao.search(strsql.toString());
	          while (rset.next())
	          {
	            Element child = new Element("TreeNode");
	            if (!userView.isHaveResource(IResourceConstant.KQ_CLASS_GROUP, rset.getString("group_id")))
					continue;	
	            child.setAttribute("id", rset.getString("group_id"));
	            child.setAttribute("text", rset.getString("name"));
	            child.setAttribute("title", rset.getString("name"));
	           
	            if(this.action!=null&&this.action.length()>0)
	            {
	            	 theaction=this.action+"?b_query=link&group_id="+rset.getString("group_id");
	            	 child.setAttribute("href", theaction);
	 	             child.setAttribute("target", this.target);
	            }
	            
	            //child.setAttribute("xml", "/kq/options/class/class_list.jsp?params=1<2 and class_id<'0'&class_id="+rset.getString("class_id"));
	            if(this.image!=null&&this.image.length()>0)
	            	child.setAttribute("icon",this.image);
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
	            PubFunc.closeResource(rset);
	            PubFunc.closeResource(conn);
	        }
	        return xmls.toString();        
	    }

		public String getImage() {
			return image;
		}

		public void setImage(String image) {
			this.image = image;
		}
}
