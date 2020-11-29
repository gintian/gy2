/*
 * Created on 2006-2-7
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.servlet.card;

import com.hjsj.hrms.interfaces.sys.IResourceConstant;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author wlh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchCardstreeTwoServlet extends HttpServlet {
	   /* 
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException { 
    	StringBuffer sbXml = new StringBuffer();
        String flag=req.getParameter("flag"); 
//        String flaga=req.getParameter("flaga");
        String moduleflag=req.getParameter("moduleflag");
        
        if (moduleflag != null) {
        	moduleflag = SafeCode.decode(moduleflag);
        }
        
		try {
		   	UserView userview=(UserView) req.getSession().getAttribute(WebConstant.userView);
			sbXml.append(loadYKCardNodes(flag,moduleflag,userview));
		} catch (Exception e) {
			System.out.println(e);
		}			
		resp.setContentType("text/xml;charset=UTF-8");
		resp.getWriter().println(sbXml.toString());
    }
    private String loadYKCardNodes(String flag,String moduleflag,UserView userview) throws Exception
    {
    	StringBuffer strXml=new StringBuffer();
    	List rs=ExecuteSQL.executeMyQuery(getLoadTreeQueryString(flag,moduleflag,userview)); 
 	    String zpid=userview.getResourceString(0);
    	if(!rs.isEmpty())
    	{
    		strXml.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>\n<TreeNode>\n");
    		for(int i=0;i<rs.size();i++)
    		{
    		     TreeItemView treeitem=new TreeItemView();
    		     DynaBean rec=(DynaBean)rs.get(i);
    		     if("1".equals(flag))
    		     {
    			     String sortid=rec.get("sortid")!=null?rec.get("sortid").toString():"";
        		     String sortname=rec.get("sortname")!=null?new String(rec.get("sortname").toString().replaceAll("&",	"&amp;")):"";
        		     treeitem.setXml("/general/card/searchcardstreetwo?flag=2&amp;moduleflag=" + SafeCode.encode(sortid));
        	         treeitem.setIcon("/images/open.png");
       	             treeitem.setAction("javascript:void(0)");	
       	             treeitem.setText(sortname);
       	             treeitem.setTitle(sortname);
       	             treeitem.setName(sortid);
        	         strXml.append(treeitem.toChildNodeJS() + "\n");        	  
    		     }else
    		     {
    		     	 String tabid=rec.get("tabid")!=null?rec.get("tabid").toString():"";
        		     String name=rec.get("name")!=null?new String(rec.get("name").toString().replaceAll("&",	"&amp;")):"";
        		     if((userview.isHaveResource(IResourceConstant.CARD,tabid)) || userview.isSuper_admin())
        	         {
	        		     treeitem.setText(tabid+":"+name);
	        	         treeitem.setTitle(name);
	        	         treeitem.setTarget("mil_body");
	        	         treeitem.setName(tabid);
	        		     treeitem.setIcon("/images/overview_obj.gif");
	        		     treeitem.setOnicon("/images/file.png");
	        		     treeitem.setAction("javascript:void(0)");     	  
	        	         strXml.append(treeitem.toChildNodeJS() + "\n");   
        	         }  
    		     }   	       
    		}
    		if("1".equals(flag))
    		{
    			StringBuffer strsql=new StringBuffer();
    			strsql.append("select DISTINCT Rname.tabid,Rname.name from Rname,RSORT where (Rname.ModuleFlag NOT IN (SELECT DISTINCT sortid FROM rsort, rname WHERE rsort.SORTID = rname.moduleflag) and rname.flaga in ('A','B','K')");
//    			strsql.append(flaga);
    			strsql.append(")");    			
    			List rss=ExecuteSQL.executeMyQuery(strsql.toString()); 
    			for(int j=0;j<rss.size();j++)
    			{
    				 TreeItemView treeitem=new TreeItemView();
    				 LazyDynaBean rec=(LazyDynaBean)rss.get(j);
    				 String tabid=rec.get("tabid")!=null?rec.get("tabid").toString():"";
        		     String name=rec.get("name")!=null?new String(rec.get("name").toString().replaceAll("&",	"&amp;")):"";
        		     
        		     if((userview.isHaveResource(IResourceConstant.CARD,tabid)) || userview.isSuper_admin())
        	         {
	        		     treeitem.setText(tabid+":"+name);
	        	         treeitem.setTitle(name);
	        	         treeitem.setTarget("mil_body");
	        	         treeitem.setName(tabid);
	        		     treeitem.setIcon("/images/overview_obj.gif");
	        		     treeitem.setAction("javascript:void(0)");     	  
	        	         strXml.append(treeitem.toChildNodeJS() + "\n");   
        	         }  
    			}
    			
    		}
    		/*if("1".equals(flag))
    		{
    			String sortid="";
    		    String sortname=new String("未分类".getBytes("GBK"),"ISO-8859-1");
    		    TreeItemView treeitem=new TreeItemView();
    		    treeitem.setXml("/general/card/searchcardstree?flag=2&amp;moduleflag=" + SafeCode.encode("#")  + "&amp;flaga=" + flaga);
    	        treeitem.setIcon("/images/open.png");
    	        treeitem.setAction("javascript:void(0)");	
    	        treeitem.setText(sortname);
    	        treeitem.setTitle(sortname);
    	        treeitem.setName(sortid);
    	        strXml.append(treeitem.toChildNodeJS() + "\n");   
    		}*/
    		strXml.append("</TreeNode>\n");    		
    		return strXml.toString();
    	
    	}else
    	{
    		/*if("1".equals(flag))
		    {
    			strXml.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>\n<TreeNode>\n");
        		String sortid="";
    		    String sortname=new String("未分类".getBytes("GBK"),"ISO-8859-1");
    		    TreeItemView treeitem=new TreeItemView();
    		    treeitem.setXml("/general/card/searchcardstree?flag=2&amp;moduleflag=" + SafeCode.encode("#")  + "&amp;flaga=" + flaga);
    	        treeitem.setIcon("/images/open.png");
    	        treeitem.setAction("javascript:void(0)");	
    	        treeitem.setText(sortname);
    	        treeitem.setTitle(sortname);
    	        treeitem.setName(sortid);
    	        strXml.append(treeitem.toChildNodeJS() + "\n");   
        		strXml.append("</TreeNode>\n"); 
		    }*/
    		if("1".equals(flag))
    		{
    			StringBuffer strsql=new StringBuffer();
    			strsql.append("select tabid,name from rname where moduleflag not in(");
                strsql.append("select sortid from RSort");
				strsql.append(")"); 			
    			List rss=ExecuteSQL.executeMyQuery(strsql.toString()); 
    			if(!rss.isEmpty())
    			{
    				strXml.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>\n<TreeNode>\n");
    				for(int j=0;j<rss.size();j++)
        			{
        				 TreeItemView treeitem=new TreeItemView();
        				 LazyDynaBean rec=(LazyDynaBean)rss.get(j);
        				 String tabid=rec.get("tabid")!=null?rec.get("tabid").toString():"";
            		     String name=rec.get("name")!=null?new String(rec.get("name").toString().replaceAll("&",	"&amp;")):"";
            		     if(zpid.indexOf(tabid)!=-1 || "su".equalsIgnoreCase(userview.getUserName()))
            	         {
    	        		     treeitem.setText(tabid+":"+name);
    	        	         treeitem.setTitle(name);
    	        	         treeitem.setTarget("mil_body");
    	        	         treeitem.setName(tabid);
    	        		     treeitem.setIcon("/images/overview_obj.gif");
    	        		     treeitem.setAction("javascript:void(0)");     	  
    	        	         strXml.append(treeitem.toChildNodeJS() + "\n");   
            	         }  
        			}
    				strXml.append("</TreeNode>\n"); 
    			}
    			
    			
    		}  
    	}
       	return strXml.toString();
    }  
	  /* 
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1)
            throws ServletException, IOException {
        doPost(arg0, arg1);
    }
	private String getLoadTreeQueryString(String flag,String moduleflag,UserView userview) {
		StringBuffer strsql=new StringBuffer();
    	String cardids=userview.getResourceString(IResourceConstant.CARD);
    	String[] cardarr=StringUtils.split(cardids,",");

    	//cardids=Arrays.toString(cardarr); websphere6.0.0.1
    	cardids=StringUtils.join(cardarr, ',');
    	//cardids=cardids.substring(1, cardids.length()-1);
		if("1".equals(flag))
		{
			if(userview.isSuper_admin())
			{
				strsql.append("SELECT sortid,sortname FROM RSORT where sortid in (select moduleflag from rname where flaga in ('A','B','K')");
//				strsql.append(flaga);
				strsql.append(")");
			}
			else
			{
				if(cardids.length()>0)
				{
					strsql.append("SELECT sortid,sortname FROM RSORT where sortid in (select moduleflag from rname where flaga in ('A','B','K') and tabid in (");
					strsql.append(cardids);
					strsql.append(") ");
//					strsql.append(flaga);
					strsql.append(")");					
				}
				else
				{
					strsql.append("SELECT sortid,sortname FROM RSORT where sortid in (select distinct moduleflag from rname where 1=2)");
				}
			}

			
		}else
		{
//			if(moduleflag!=null&&moduleflag.length()>0)
//				moduleflag=SafeCode.decode(moduleflag);
//			if(moduleflag!=null&&moduleflag.equalsIgnoreCase("#"))
//			{
//				strsql.append("select tabid,name from rname where flaga='"+flaga+"' and moduleflag not in(");
//                strsql.append("select sortid from RSort");
//				strsql.append(")");
//			}else
//			{
				strsql.append("SELECT tabid,name from Rname where flaga in ('A','B','K') and ");
//				strsql.append(flaga);
				strsql.append(" moduleflag='");
				strsql.append(moduleflag);
				strsql.append("'");
//			}
			
		}    
		return strsql.toString();
	}
}
