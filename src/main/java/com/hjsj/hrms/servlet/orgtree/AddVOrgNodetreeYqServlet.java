package com.hjsj.hrms.servlet.orgtree;

import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.utils.Sql_switcher;
import org.apache.commons.beanutils.DynaBean;
import org.apache.log4j.Category;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddVOrgNodetreeYqServlet extends HttpServlet {

	private  Category cat = Category.getInstance(this.getClass());

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException { 
    	StringBuffer sbXml = new StringBuffer();
    	String deptCode = req.getParameter("deptCode");
        String params=req.getParameter("params");
        String issuperuser=req.getParameter("issuperuser");
        String parentid=req.getParameter("parentid");
        String manageprive=req.getParameter("manageprive"); 	
        String action=req.getParameter("action");
        String target=req.getParameter("target");
        String treetype=req.getParameter("treetype");
        String orgtype=req.getParameter("orgtype");
        String loadtype=req.getParameter("loadtype");/**加载选项  * =0（单位|部门|职位）   * =1 (单位|部门)   * =2 (单位) * */
        loadtype=loadtype!=null&&loadtype.trim().length()>0?loadtype:"0";
        String droit=req.getParameter("droit");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String backdate = req.getParameter("backdate");
		backdate = backdate!=null&&backdate.length()>9?backdate:sdf.format(new Date());
        if(orgtype==null||orgtype.length()<=0)
        	orgtype="org";
		try {
			sbXml.append(loadOrgItemNodes(params,issuperuser,parentid,manageprive,action,target,treetype,orgtype,backdate,deptCode, droit, loadtype));
			
		} catch (Exception e) {
			System.out.println(e);
		}
		  cat.debug("catalog xml" + sbXml.toString());
		resp.setContentType("text/xml");
		resp.getWriter().println(sbXml.toString());
    }
   private String  loadOrgItemNodes(String params,String issuperuser,String parentid,String manageprive,String action,String target,String treetype,String orgtype,String backdate,String deptCode, String droit, String loadtype) throws Exception
   {
   	StringBuffer strXml=new StringBuffer();
   	List rs=new ArrayList();
   	/*if(orgtype==null||orgtype.length()<=0||!orgtype.equalsIgnoreCase("vorg"))
	  rs=ExecuteSQL.executeMyQuery(getLoadTreeQueryString(params,issuperuser,parentid,manageprive));    	
   	getVorgTreeXml(rs,manageprive,parentid);*/
   	rs=ExecuteSQL.executeMyQuery(getLoadTreeQueryString(params,issuperuser,parentid,manageprive,backdate,deptCode, droit));
   	if(!rs.isEmpty())
	{
		strXml.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>\n<TreeNode>\n");
		for(int i=0;i<rs.size();i++)
		{
		     TreeItemView treeitem=new TreeItemView();
		     DynaBean rec=(DynaBean)rs.get(i);
		     String org_type=rec.get("orgtype")!=null?rec.get("orgtype").toString():"";
		     String image="";		     
		     String codeitemid=rec.get("codeitemid")!=null?rec.get("codeitemid").toString():"";
		     String codeitemdesc=rec.get("codeitemdesc")!=null?new String(rec.get("codeitemdesc").toString().replaceAll("&",	"&amp;").getBytes("GBK"),"ISO-8859-1"):"";
		     String codesetid=rec.get("codesetid")!=null?rec.get("codesetid").toString():"";
		     if("2".equalsIgnoreCase(loadtype))
	         {
	            	if("@K".equalsIgnoreCase(codesetid)|| "UM".equalsIgnoreCase(codesetid))
	            		continue;            	
	         }
	         if("1".equalsIgnoreCase(loadtype))
	         {
	            	if("@K".equalsIgnoreCase(codesetid))
	            		continue;
	         }
		     treeitem.setName(codesetid+codeitemid);
		     treeitem.setText(codeitemdesc);
	         treeitem.setTitle(codeitemdesc);  
	         treeitem.setTarget(target);
	        // System.out.println("ddd" + treetype);
	         if(rec.get("codesetid")!=null && "UN".equals(rec.get("codesetid")))
	         {
	            if(!codeitemid.equalsIgnoreCase(rec.get("childid")!=null?rec.get("childid").toString():""))
                    treeitem.setXml("/common/vorg/loadtreeyq?params=child&amp;orgtype="+org_type+"&amp;treetype="+ treetype + "&amp;parentid="  + codeitemid + "&amp;loadtype="+loadtype+"&amp;kind=2&amp;issuperuser=" + issuperuser + "&amp;manageprive=" + manageprive + "&amp;action=" + action + "&amp;target=" + target+"&amp;backdate="+backdate);

	            if("vorg".equals(org_type)&&!"vorg".equals(orgtype))
			    	 image="/images/vroot.gif";
	            else
	            	image="/images/unit.gif";
	            treeitem.setIcon(image);
	            if("javascript:void(0)".equals(action))
	            	treeitem.setAction(action);			
	            else if("duty".equals(treetype))
	            	treeitem.setAction("showerrorinfo.do?b_search=link&amp;orgtype="+org_type+"&amp;code=" + codeitemid + "&amp;kind=2"+"&amp;backdate="+backdate);
	            else
	                treeitem.setAction("/pos/police/jqdt.do?b_search=link&amp;tofirst=yes&amp;a_code=" + codesetid+ codeitemid + "&amp;kind=2&amp;orgtype="+org_type);
	            strXml.append(treeitem.toChildNodeJS() + "\n");
	         }else if(rec.get("codesetid")!=null && "UM".equals(rec.get("codesetid")) && !"noum".equals(treetype)){
	        	 String childid=rec.get("childid")!=null?rec.get("childid").toString():"";
	         	if(!codeitemid.equalsIgnoreCase(childid))
						treeitem.setXml("/common/vorg/loadtreeyq?params=child&amp;orgtype=" + org_type
								+ "&amp;treetype=" + treetype + "&amp;parentid=" + codeitemid
								+ "&amp;loadtype=" + loadtype + "&amp;kind=1&amp;issuperuser=" + issuperuser
								+ "&amp;manageprive=" + manageprive + "&amp;action=" + action
								+ "&amp;target=" + target + "&amp;backdate=" + backdate);
	         	if("vorg".equals(org_type)&&!"vorg".equals(orgtype))
			    	 image="/images/vdept.gif";
	            else
	            	image="/images/dept.gif";
	         	treeitem.setIcon(image);
	            if("javascript:void(0)".equals(action))
	            	treeitem.setAction(action);	
	            else if("duty".equals(treetype))
	            	treeitem.setAction("showerrorinfo.do?b_search=link&amp;orgtype="+org_type+"&amp;code=" + codeitemid + "&amp;kind=1"+"&amp;backdate="+backdate);
	            else
	                treeitem.setAction("/pos/police/jqdt.do?b_search=link&amp;tofirst=yes&amp;a_code="+ codesetid + codeitemid + "&amp;kind=1&amp;orgtype="+org_type+""+"&amp;backdate="+backdate);
	            strXml.append(treeitem.toChildNodeJS() + "\n");
	         }else if(rec.get("codesetid")!=null && "@K".equals(rec.get("codesetid")) && (!"org".equals(treetype) && !"noum".equals(treetype))){
	         	//treeitem.setXml("/common/vorg/loadtree?params=child&amp;treetype="+ treetype + "&amp;parentid=" + codeitemid + "&amp;kind=0&amp;issuperuser=" + issuperuser + "&amp;manageprive=" + manageprive  + "&amp;action=" + action + "&amp;target=" + target);
	        	if("vorg".equals(org_type)&&!"vorg".equals(orgtype))
	        		treeitem.setIcon("/images/vpos_l.gif");
	            else
	        	 treeitem.setIcon("/images/pos_l.gif");
	          
	            if("javascript:void(0)".equals(action))
	            	treeitem.setAction(action);			        
	            else
	                treeitem.setAction("/pos/police/jqdt.do?b_search=link&amp;tofirst=yes&amp;orgtype="+org_type+"&amp;a_code="+ codesetid + codeitemid + "&amp;kind=0"+"&amp;backdate="+backdate);
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
	private String getLoadTreeQueryString(String params,String isSuperuser,String parentid,String managepriv,String backdate,String deptCode, String droit) {
		StringBuffer strsql=new StringBuffer();
		strsql.append("SELECT codesetid,codeitemdesc,codeitemid,parentid,childid,'org' as orgtype,a0000 ");
		strsql.append(" FROM organization ");   
    	if(params!=null && "root".equals(params)){
    		if(droit!=null&& "0".equals(droit))
    		{
    			strsql.append(" WHERE codeitemid=parentid ");
    		}else
    		{
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
        				strsql.append(" WHERE codeitemid='");
        				strsql.append(deptCode);
        				strsql.append("'");
        			}
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
    	
    	//根据设置显示组织机构
    	if (selectPoliceConstant().length() > 0) {
	    	strsql.append(" and codeitemid in (");
	    	strsql.append(selectPoliceConstant());
	    	strsql.append(") ");
    	}
    	
    	strsql.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
    	strsql.append(" union ");
		strsql.append("SELECT codesetid,codeitemdesc,codeitemid,parentid,childid,'vorg' as orgtype,a0000 ");
		strsql.append(" FROM vorganization ");  
		/*if(managepriv!=null&&managepriv.length()>1)
		{
			managepriv=managepriv.substring(2,managepriv.length());
			strsql.append(" WHERE ");
			strsql.append(" parentid='"+parentid+"'");
		}else
		{
			strsql.append(" WHERE 1=2");
		}*/
		if(params!=null && "root".equals(params)){
    		if(droit!=null&& "0".equals(droit))
    		{
    			strsql.append(" WHERE codeitemid=parentid ");
    		}else
    		{
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
//        				strsql.append(" WHERE 1=2");	
        				strsql.append(" WHERE codeitemid='");
        				strsql.append(deptCode);
        				strsql.append("'");
        			}
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
		
		//根据设置显示组织机构
    	if (selectPoliceConstant().length() > 0) {
	    	strsql.append(" and codeitemid in (");
	    	strsql.append(selectPoliceConstant());
	    	strsql.append(") ");
    	}
		strsql.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
    	strsql.append(" ORDER BY orgtype,a0000,codeitemid ");
		return strsql.toString();
	}
	/**
	 * 虚拟表结构SQL
	 * @param params
	 * @param issuperuser
	 * @param parentid
	 * @param manageprive
	 * @return
	 */
	private String getLoadVorgTreeQueryString(String manageprive,String parentid)
	{
		StringBuffer strsql=new StringBuffer();
		strsql.append("SELECT codesetid,codeitemdesc,codeitemid,parentid,childid,'vorg' as orgtype ");
		strsql.append(" FROM vorganization ");  
		if(manageprive!=null&&manageprive.length()>1)
		{
			manageprive=manageprive.substring(2,manageprive.length());
			strsql.append(" WHERE ");
			strsql.append(" parentid='"+parentid+"'");
			strsql.append(" order by A0000");
			
		}else
		{
			strsql.append(" WHERE 1=2");
		}
		return strsql.toString();
	}
	private void  getVorgTreeXml(List rs,String manageprive,String parentid)
	{
		List v_rs=ExecuteSQL.executeMyQuery(getLoadVorgTreeQueryString(manageprive,parentid)); 
	    if(!v_rs.isEmpty())
	    {
	    	for(int i=0;i<v_rs.size();i++)
	    	{
	    		 DynaBean rec=(DynaBean)v_rs.get(i);
	    		 rs.add(rec);
	    	}
	    }
	}
	
	private String selectPoliceConstant() {
		String checkvalues = "";
		String sql = "select * from constant where constant='POLICE_SETYQDT'";
		Connection conn = null;
		ResultSet rs = null;
		try {
			conn = AdminDb.getConnection();
		ContentDAO dao = new ContentDAO(conn);
		
			rs = dao.search(sql);
			if (rs.next()) {
				checkvalues = rs.getString("str_value");
				if (checkvalues == null) {
					checkvalues = "";
				}
			}
		if (checkvalues != null && checkvalues.trim().length() > 0) {
			String []check = checkvalues.split(",");
			checkvalues = "";
			for (int i = 0; i < check.length; i++) {
				if (i == 0) {
					checkvalues += ("'" + check[i].substring(2) + "'");
				} else {
					checkvalues += ("," + "'" +check[i].substring(2) + "'");
				}
			}
		}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				if (conn != null) {
					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			
		}
		return checkvalues;
	}

}
