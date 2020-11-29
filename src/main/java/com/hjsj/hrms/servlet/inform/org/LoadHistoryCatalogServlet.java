/*
 * Created on 2006-3-6
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.servlet.inform.org;

import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.log4j.Category;

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
public class LoadHistoryCatalogServlet extends HttpServlet {
	   /* 
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
	private  Category cat = Category.getInstance(this.getClass());
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException { 
    	StringBuffer sbXml = new StringBuffer();
        String issuperuser=req.getParameter("issuperuser"); /*是否超级用户*/
        String manageprive=req.getParameter("manageprive"); /*管理权限*/
        String action=req.getParameter("action");           /*节点的动作*/
        String target=req.getParameter("target");           /*页面显示的目标*/      
		try {
			sbXml.append(loadOrgItemNodes(issuperuser,manageprive,action,target));
		} catch (Exception e) {
			System.out.println(e);
		}			
		  cat.debug("catalog xml" + sbXml.toString());
		resp.setContentType("text/xml;charset=UTF-8");
		resp.getWriter().println(sbXml.toString());
    }
   private String  loadOrgItemNodes(String issuperuser,String manageprive,String action,String target) throws Exception
   {
       
    	StringBuffer strXml=new StringBuffer();
	    List rs=ExecuteSQL.executeMyQuery("select catalog_id,name,archive_date,description from hr_org_catalog order by catalog_id desc");    	
	   if(!rs.isEmpty())
	   {
		  strXml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<TreeNode>\n");
		  for(int i=0;i<rs.size();i++)
		  {
    		TreeItemView treeitem=new TreeItemView();
    	    DynaBean rec=(DynaBean)rs.get(i);
    	    String catalog_id=rec.get("catalog_id")!=null?rec.get("catalog_id").toString():"";
    	    String name=rec.get("name")!=null?rec.get("name").toString():"";//new String(rec.get("name").toString().replaceAll("&",	"&amp;").getBytes("GB2312"),"ISO-8859-1"):"";
    	    name=name.replaceAll("\"", "");
    	    treeitem.setName("hroot" + catalog_id);
    		treeitem.setText(name);
    		treeitem.setTitle(name);
    		treeitem.setAction(action + "?b_search=link&amp;code=&amp;kind=2&amp;catalog_id=" + catalog_id);
    		treeitem.setXml("/general/inform/org/loadhistroyorgtree?isroot=root&amp;parentid=00&amp;issuperuser=" + issuperuser + "&amp;action=" + action + "&amp;target=" + target + "&amp;manageprive=" + manageprive + "&amp;catalog_id=" + catalog_id);
    		treeitem.setTarget(target);
    		treeitem.setIcon("/images/open.png");
            strXml.append(treeitem.toChildNodeJS() + "\n");	        
		  }
		  strXml.append("</TreeNode>\n");
		
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
}
