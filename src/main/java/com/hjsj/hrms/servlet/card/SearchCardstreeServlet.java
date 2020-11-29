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
public class SearchCardstreeServlet extends HttpServlet {
	   /* 
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException { 
    	StringBuffer sbXml = new StringBuffer();
        String flag=req.getParameter("flag"); 
        String flaga=req.getParameter("flaga");
        String temp_id=req.getParameter("temp_id");
        String moduleflag=req.getParameter("moduleflag");
		try {
		   	UserView userview=(UserView) req.getSession().getAttribute(WebConstant.userView);
			sbXml.append(loadYKCardNodes(flag,flaga,moduleflag,userview,temp_id));
		} catch (Exception e) {
			System.out.println(e);
		}			
		resp.setContentType("text/xml;charset=UTF-8");
		resp.getWriter().println(sbXml.toString());
    }
    private String loadYKCardNodes(String flag,String flaga,String moduleflag,UserView userview,String temp_id) throws Exception
    {
    	StringBuffer strXml=new StringBuffer();
    	List rs=ExecuteSQL.executeMyQuery(getLoadTreeQueryString(flag,flaga,moduleflag,userview,temp_id)); 
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
        		     String sortname=rec.get("sortname")!=null?rec.get("sortname").toString().replaceAll("&",	"&amp;"):"";
        		     treeitem.setXml("/general/card/searchcardstree?flag=2&amp;moduleflag=" + SafeCode.encode(sortid)  + 
        		             "&amp;flaga=" + flaga+"&amp;temp_id="+temp_id);
        	         treeitem.setIcon("/images/open.png");
       	             treeitem.setAction("javascript:void(0)");	
       	             treeitem.setText(sortname);
       	             treeitem.setTitle(sortname);
       	             treeitem.setName(sortid);
        	         strXml.append(treeitem.toChildNodeJS() + "\n");        	  
    		     }else
    		     {
    		     	 String tabid=rec.get("tabid")!=null?rec.get("tabid").toString():"";
        		     String name=rec.get("name")!=null?rec.get("name").toString().replaceAll("&",	"&amp;"):"";
        		     if((userview.isHaveResource(IResourceConstant.CARD,tabid)) || userview.isSuper_admin())
        	         {
	        		     treeitem.setText(tabid+":"+name);
	        	         treeitem.setTitle(tabid+":"+name);
	        	         treeitem.setTarget("mil_body");
	        	         treeitem.setName(tabid + moduleflag);
	        		     treeitem.setIcon("/images/overview_obj.gif");
	        		     treeitem.setOnicon("/images/file.png");
	        		     treeitem.setAction("/general/card/searchshowcard.do?b_showcard=link&amp;tabid="+ tabid+getInfoKindParam((String)rec.get("flaga")));     	  
	        	         strXml.append(treeitem.toChildNodeJS() + "\n");   
        	         }  
    		     }   	       
    		}
    		if("1".equals(flag))
    		{
    			StringBuffer strsql=new StringBuffer();
    			strsql.append("select DISTINCT Rname.tabid,Rname.name,Rname.flaga from Rname,RSORT "+
    			        "where (Rname.ModuleFlag NOT IN (SELECT DISTINCT sortid FROM rsort, rname"+
    			            " WHERE rsort.SORTID = rname.moduleflag) and "+flagaCond(flaga));
    			strsql.append(")");
    			strsql.append(" order by flaga, tabid");
    			List rss=ExecuteSQL.executeMyQuery(strsql.toString()); 
    			for(int j=0;j<rss.size();j++)
    			{
    				 TreeItemView treeitem=new TreeItemView();
    				 LazyDynaBean rec=(LazyDynaBean)rss.get(j);
    				 String tabid=rec.get("tabid")!=null?rec.get("tabid").toString():"";
        		     String name=rec.get("name")!=null?rec.get("name").toString().replaceAll("&",	"&amp;"):"";
        		     
        		     if((userview.isHaveResource(IResourceConstant.CARD,tabid)) || userview.isSuper_admin())
        	         {
	        		     treeitem.setText(tabid+":"+name);
	        	         treeitem.setTitle(tabid+":"+name);
	        	         treeitem.setTarget("mil_body");
	        	         treeitem.setName(tabid + moduleflag);
	        		     treeitem.setIcon("/images/overview_obj.gif");
	        		     treeitem.setAction("/general/card/searchshowcard.do?b_showcard=link&amp;tabid="+ tabid+getInfoKindParam((String)rec.get("flaga")));     	  
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
    			strsql.append("select tabid,name,flaga from rname where "+flagaCond(flaga)+" and moduleflag not in(");
                strsql.append("select sortid from RSort");
				strsql.append(")");
				strsql.append(" order by flaga, tabid");
    			List rss=ExecuteSQL.executeMyQuery(strsql.toString()); 
    			if(!rss.isEmpty())
    			{
    				strXml.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>\n<TreeNode>\n");
    				for(int j=0;j<rss.size();j++)
        			{
        				 TreeItemView treeitem=new TreeItemView();
        				 LazyDynaBean rec=(LazyDynaBean)rss.get(j);
        				 String tabid=rec.get("tabid")!=null?rec.get("tabid").toString():"";
            		     String name=rec.get("name")!=null?rec.get("name").toString().replaceAll("&",	"&amp;"):"";
            		     if(zpid.indexOf(tabid)!=-1 || "su".equalsIgnoreCase(userview.getUserName()))
            	         {
    	        		     treeitem.setText(tabid+":"+name);
    	        	         treeitem.setTitle(tabid+":"+name);
    	        	         treeitem.setTarget("mil_body");
    	        	         treeitem.setName(tabid + moduleflag);
    	        		     treeitem.setIcon("/images/overview_obj.gif");
    	        		     treeitem.setAction("/general/card/searchshowcard.do?b_showcard=link&amp;tabid="+ tabid+getInfoKindParam((String)rec.get("flaga")));     	  
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
	private String getLoadTreeQueryString(String flag,String flaga,String moduleflag,UserView userview,String temp_id) {
		StringBuffer strsql=new StringBuffer();
		try
		{
			String cardids=userview.getResourceString(IResourceConstant.CARD);
	    	String[] cardarr=StringUtils.split(cardids,",");//得到当前用户有权限的登记表号
	    	//cardids=Arrays.toString(cardarr); websphere6.0.0.1
	    	//cardids=StringUtils.join(cardarr, ',');//liunx中weblogic不支持
	    	StringBuffer buf=new StringBuffer();
	    	for(int i=0;i<cardarr.length;i++ )
	    	{
	    		buf.append(cardarr[i]+",");
	    	}
	    	if(buf.length()>0)
                buf.setLength(buf.length()-1);
	    	//cardids=cardids.substring(1, cardids.length()-1);
			if("1".equals(flag))
			{
				if(userview.isSuper_admin())
				{
					strsql.append("SELECT sortid,sortname FROM RSORT where sortid in "+
					        "(select distinct moduleflag from rname where "+flagaCond(flaga)+")");
					strsql.append(" or sortid in (select sortid from RSORT where flag is null and"+
					                    " SORTID in (select distinct moduleflag from rname where "+flagaCond(flaga));
					strsql.append("))");
				}
				else
				{
					if(cardids.length()>0)
					{
						strsql.append("SELECT sortid,sortname FROM RSORT where sortid in "+
						                    "(select distinct moduleflag from rname where tabid in (");
						strsql.append(cardids);
						strsql.append(") and "+flagaCond(flaga));
						strsql.append(") or sortid in (select sortid from RSORT where flag is null and SORTID in"+
						                    " (select distinct moduleflag from rname where tabid in (");
						strsql.append(cardids);
						strsql.append(") and "+flagaCond(flaga));
					    strsql.append("))");
					}
					else
					{
						strsql.append("SELECT sortid,sortname FROM RSORT");
						strsql.append(" where sortid in (select distinct moduleflag from rname where 1=2)");
					}
				}

				
			}else
			{
				if(moduleflag!=null&&moduleflag.length()>0)
					moduleflag=SafeCode.decode(moduleflag);
				if(moduleflag!=null&& "#".equalsIgnoreCase(moduleflag))
				{
					strsql.append("select tabid,name,flaga from rname where "+flagaCond(flaga)+" and moduleflag not in(");
	                strsql.append("select sortid from RSort");
					strsql.append(")");
					strsql.append(" order by flaga, tabid");
				}else if("P".equalsIgnoreCase(flaga))
				{
					if(temp_id!=null&&!"".equalsIgnoreCase(temp_id)){
					
						List rs=ExecuteSQL.executeMyQuery("SELECT tabids from per_template where template_id='"+temp_id+"'");
						String tabids="";
						if(!rs.isEmpty()&&rs.size()>0)
				    	{
							 LazyDynaBean rec=(LazyDynaBean)rs.get(0);
							 tabids=(String)rec.get("tabids");
							 if(tabids!=null&&tabids.length()>0)
							 {
								 String tabss[]=tabids.split(",");
								 strsql.append("SELECT tabid,name,flaga from Rname where "+flagaCond(flaga));
								 strsql.append(" and moduleflag='");
								 strsql.append(moduleflag);
								 strsql.append("' and tabid in(");
								 for(int i=0;i<tabss.length;i++)
									 strsql.append("'"+tabss[i]+"',");
								 strsql.setLength(strsql.length()-1);
								 strsql.append(")");
								 strsql.append(" order by flaga, tabid");
							 }else
							 {
								 strsql.append("SELECT tabid,name,flaga from Rname where " + flagaCond(flaga));
								 strsql.append(" and moduleflag='");
								 strsql.append(moduleflag);
								 strsql.append("' and 1=2");
								 strsql.append(" order by flaga, tabid");
							 }
				    	}
					}else
					{
						strsql.append("SELECT tabid,name,flaga from Rname where " + flagaCond(flaga));
						strsql.append(" and moduleflag='");
						strsql.append(moduleflag);
						strsql.append("'");
						strsql.append(" order by flaga, tabid");
					}
				}else
				{
					strsql.append("SELECT tabid,name,flaga from Rname where " + flagaCond(flaga));
					strsql.append(" and moduleflag='");
					strsql.append(moduleflag);
					strsql.append("'");
					strsql.append(" order by flaga, tabid");
				}
				
			}    
		}catch(Exception e)
		{
			e.printStackTrace();
		}    	
		return strsql.toString();
	}
	
	private String flagaCond(String flaga){
	    if("A,B,K".equals(flaga))
	        return "flaga in ('A', 'B', 'K')";
	    else
	        return "flaga='"+flaga+"'";
	}
	
	private String getInfoKindParam(String flaga) {
	    String infokind = "1";
	    if("B".equals(flaga))
	        infokind = "2";
	    else if("K".equals(flaga))
	        infokind = "4";
	    else if ("P".equals(flaga))//liuy 2015-1-5 6470：绩效评估/打印登记表：选择考核表后，界面显示都错乱了
	    	infokind = "5";
	    else if("H".equals(flaga)) 
	    	infokind = "6";
	    String param = "&amp;inforkind=" + infokind;
	    return param;
	}
}
