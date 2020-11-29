/*
 * Created on 2005-6-17
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.servlet.orgtree;

import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.hjsj.utils.Sql_switcher;
import org.apache.commons.beanutils.DynaBean;
import org.apache.log4j.Category;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AddressOrgNodetreeServlet extends HttpServlet {
	   /* 
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
	private  Category cat = Category.getInstance(this.getClass());

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException { 
    	StringBuffer sbXml = new StringBuffer();
        String params=req.getParameter("params");
        String issuperuser=req.getParameter("issuperuser");
        String parentid=req.getParameter("parentid");
        String manageprive=req.getParameter("manageprive"); 	
        String action=req.getParameter("action");
        String target=req.getParameter("target");
        String treetype=req.getParameter("treetype");
        String orgtype=req.getParameter("orgtype");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String backdate = req.getParameter("backdate");
		backdate = backdate!=null&&backdate.length()>9?backdate:sdf.format(new Date());
        if(orgtype==null||orgtype.length()<=0)
        	orgtype="org";
		try {
			sbXml.append(loadOrgItemNodes(params,issuperuser,parentid,manageprive,action,target,treetype,orgtype,backdate));
			
		} catch (Exception e) {
			System.out.println(e);
		}
		  cat.debug("catalog xml" + sbXml.toString());
		resp.setContentType("text/xml");
		resp.setContentType("text/xml;charset=utf-8");
		resp.getWriter().println(sbXml.toString());
		resp.getWriter().close();
    }
   private String  loadOrgItemNodes(String params,String issuperuser,String parentid,String manageprive,String action,String target,String treetype,String orgtype,String backdate) throws Exception
   {
   	StringBuffer strXml=new StringBuffer();
   	List rs=new ArrayList();
   	if(orgtype==null||orgtype.length()<=0||!"vorg".equalsIgnoreCase(orgtype))
	  rs=ExecuteSQL.executeMyQuery(getLoadTreeQueryString(params,issuperuser,parentid,manageprive,backdate)); 
   	if(!rs.isEmpty())
	{
		strXml.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<TreeNode>\n");
		for(int i=0;i<rs.size();i++)
		{
		     TreeItemView treeitem=new TreeItemView();
		     DynaBean rec=(DynaBean)rs.get(i);
		     String org_type=rec.get("orgtype")!=null?rec.get("orgtype").toString():"";
		     String image="admin.gif";
		     
		     String codeitemid=rec.get("codeitemid")!=null?rec.get("codeitemid").toString():"";
		     String codeitemdesc=rec.get("codeitemdesc") != null ? com.hrms.frame.codec.SafeCode.encode(rec.get("codeitemdesc").toString()): "";
		     String codesetid=rec.get("codesetid")!=null?rec.get("codesetid").toString():"";
		     treeitem.setName(codesetid+codeitemid);
		     treeitem.setText(codeitemdesc);
	         treeitem.setTitle(codeitemdesc);  
	         treeitem.setTarget(target);
	        // System.out.println("ddd" + treetype);
	         if(rec.get("codesetid")!=null && "UN".equals(rec.get("codesetid")))
	         {
	            if(!codeitemid.equalsIgnoreCase(rec.get("childid")!=null?rec.get("childid").toString():""))
	         	    treeitem.setXml("/common/org/loadtree?params=child&amp;treetype="+ treetype + "&amp;parentid="  + codeitemid + "&amp;kind=2&amp;issuperuser=" + issuperuser + "&amp;manageprive=" + manageprive + "&amp;action=" + action + "&amp;target=" + target+"&amp;backdate="+backdate+"&amp;jump=1");
	            if("vorg".equals(org_type)&&!"vorg".equals(orgtype))
			    	 image="/images/admin.gif";
	            else
	            	image="/images/unit.gif";
	            treeitem.setIcon(image);
	            if("javascript:void(0)".equals(action))
	            	treeitem.setAction(action);			
	            else if("duty".equals(treetype))
	            	treeitem.setAction("showerrorinfo.do?b_search=link&code=" + codeitemid + "&kind=2&root=0"+"&backdate="+backdate+"&jump=1");
	            else
	                treeitem.setAction(action + "?b_search=link&code=" + codeitemid + "&kind=2&root=0"+"&backdate="+backdate+"&jump=1");
	            strXml.append(treeitem.toChildNodeJS() + "\n");
	         }else if(rec.get("codesetid")!=null && "UM".equals(rec.get("codesetid")) && !"noum".equals(treetype)){
	        	//String childid=rec.get("childid")!=null?rec.get("childid").toString():"";
	         	//if(!codeitemid.equalsIgnoreCase(childid))
	        	   treeitem.setXml("/common/org/loadtree?params=child&amp;treetype="+ treetype + "&amp;parentid=" + codeitemid +	"&amp;kind=1&amp;issuperuser=" + issuperuser + "&amp;manageprive=" + manageprive  + "&amp;action=" + action + "&amp;target=" + target+"&amp;backdate="+backdate+"&amp;jump=1");
	         	if("vorg".equals(org_type)&&!"vorg".equals(orgtype))
			    	 image="/images/admin.gif";
	            else
	            	image="/images/dept.gif";
	         	treeitem.setIcon(image);
	            if("javascript:void(0)".equals(action))
	            	treeitem.setAction(action);	
	            else if("duty".equals(treetype))
	            	treeitem.setAction("showerrorinfo.do?b_search=link&amp;code=" + codeitemid + "&amp;kind=1&amp;root=0"+"&amp;backdate="+backdate+"&amp;jump=1");
	            else
	                treeitem.setAction(action + "?b_search=link&amp;code=" + codeitemid + "&amp;kind=1&amp;root=0"+"&amp;backdate="+backdate+"&amp;jump=1");
	            strXml.append(treeitem.toChildNodeJS() + "\n");
	         }else if(rec.get("codesetid")!=null && "@K".equals(rec.get("codesetid")) && (!"org".equals(treetype) && !"noum".equals(treetype))){
	         	//treeitem.setXml("/common/org/loadtree?params=child&amp;treetype="+ treetype + "&amp;parentid=" + codeitemid + "&amp;kind=0&amp;issuperuser=" + issuperuser + "&amp;manageprive=" + manageprive  + "&amp;action=" + action + "&amp;target=" + target);
	            treeitem.setIcon("/images/pos_l.gif");
	            if("javascript:void(0)".equals(action))
	            	treeitem.setAction(action);			        
	            else
	                treeitem.setAction(action + "?b_search=link&amp;code=" + codeitemid + "&amp;kind=0&amp;root=0"+"&amp;backdate="+backdate+"&amp;jump=1");
	            strXml.append(treeitem.toChildNodeJS() + "\n");
	         }
		}
		strXml.append("</TreeNode>\n");
		//System.out.println(strXml.toString());
		return strXml.toString();
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
	private String getLoadTreeQueryString(String params,String isSuperuser,String parentid,String managepriv,String backdate) {
		StringBuffer strsql=new StringBuffer();
		strsql.append("SELECT codesetid,codeitemdesc,codeitemid,parentid,childid,'org' as orgtype ");
		strsql.append(" FROM organization "); 
    	if(params!=null && "root".equals(params)){
    		if("1".equals(isSuperuser))
    		{
    			strsql.append(" WHERE codeitemid=parentid ");
    		}
    		else
    		{
    			
    			if((managepriv !=null && managepriv.trim().length()==2))
    			{
    				strsql.append(" WHERE codeitemid=parentid ");
    			}else if((managepriv !=null && managepriv.trim().length()>=2))
    			{
    				managepriv=managepriv.substring(2,managepriv.length());
	    			strsql.append(" WHERE codeitemid='");
	    			strsql.append(managepriv);
	    			strsql.append("'");  
    			}else
    			{
    				strsql.append(" WHERE 1=2");	    		
    			}
    		}
    	}
    	else
    	{
    		strsql.append(" WHERE parentid='");
    		strsql.append(parentid);
    		strsql.append("'");
    		strsql.append(" AND codeitemid<>parentid ");
    	}
    	strsql.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
    	strsql.append(" ORDER BY a0000,codeitemid ");
		return strsql.toString();
	}	
}