package com.hjsj.hrms.servlet.orgtree;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.log4j.Category;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class KqReportOrgtreeServlet extends HttpServlet {
	private  Category cat = Category.getInstance(this.getClass());
	//private UserView userview;
	private boolean isPost = true;
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
        
        // 考勤组织机构树是否显示岗位
    	KqParameter para = new KqParameter();
    	if ("1".equalsIgnoreCase(para.getKq_orgView_post())) {
    		isPost = false;
    	} else {
    		isPost = true;
    	}
        
    	UserView userview=(UserView) req.getSession().getAttribute(WebConstant.userView);	
		try {
			sbXml.append(loadOrgItemNodes(params,issuperuser,parentid,manageprive,action,target,treetype, userview));
		} catch (Exception e) {
			System.out.println(e);
		}		
		resp.setContentType("text/xml");
		resp.getWriter().println(sbXml.toString());
    }
    
   private String  loadOrgItemNodes(String params,String issuperuser,String parentid,String manageprive,String action,String target,String treetype, UserView userview) throws Exception
   {
   	StringBuffer strXml=new StringBuffer();
   	
	List rs=ExecuteSQL.executeMyQuery(getLoadTreeQueryString(params,issuperuser,parentid,manageprive, userview));    	
	if(!rs.isEmpty())
	{
		strXml.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>\n<TreeNode>\n");
		for(int i=0;i<rs.size();i++)
		{
		     TreeItemView treeitem=new TreeItemView();
		     DynaBean rec=(DynaBean)rs.get(i);
		     String codeitemid=rec.get("codeitemid")!=null?rec.get("codeitemid").toString():"";
		     // 44264 linbz 20190123 自助-部门考勤表树结构乱码
		     String codeitemdesc=rec.get("codeitemdesc")!=null?com.hrms.frame.codec.SafeCode.encode(rec.get("codeitemdesc").toString()): "";
		     String codesetid=rec.get("codesetid")!=null?rec.get("codesetid").toString():"";
		     treeitem.setName(codesetid+codeitemid);
		     treeitem.setText(codeitemdesc);
	         treeitem.setTitle(codeitemdesc);  
	         treeitem.setTarget(target);
	         if(rec.get("codesetid")!=null && "UN".equals(rec.get("codesetid")))
	         {
        		treeitem.setXml("/common/org/loadtree?params=child&amp;treetype="+ treetype + "&amp;parentid="  + codeitemid + "&amp;kind=2&amp;issuperuser=" + issuperuser + "&amp;manageprive=" + manageprive + "&amp;action=" + action + "&amp;target=" + target);
	        	treeitem.setIcon("/images/unit.gif");
	            if("javascript:void(0)".equals(action))
	            	treeitem.setAction(action);			
	            else if("duty".equals(treetype))
	            	treeitem.setAction("showerrorinfo.do?b_search=link&amp;code=" + codeitemid + "&amp;kind=2");
	            else
	                treeitem.setAction(action + "?b_search=link&amp;code=" + codeitemid + "&amp;kind=2");
	            strXml.append(treeitem.toChildNodeJS() + "\n");
	         }else if(rec.get("codesetid")!=null && "UM".equals(rec.get("codesetid")) && !"noum".equals(treetype)){
        		treeitem.setXml("/common/org/loadtree?params=child&amp;treetype="+ treetype + "&amp;parentid=" + codeitemid +	"&amp;kind=1&amp;issuperuser=" + issuperuser + "&amp;manageprive=" + manageprive  + "&amp;action=" + action + "&amp;target=" + target);
	        	treeitem.setIcon("/images/dept.gif");
	            if("javascript:void(0)".equals(action))
	            	treeitem.setAction(action);	
	            else if("duty".equals(treetype))
	            	treeitem.setAction("showerrorinfo.do?b_search=link&amp;code=" + codeitemid + "&amp;kind=1");
	            else
	                treeitem.setAction(action + "?b_search=link&amp;code=" + codeitemid + "&amp;kind=1");
	            strXml.append(treeitem.toChildNodeJS() + "\n");
	         }else if(rec.get("codesetid")!=null && "@K".equals(rec.get("codesetid")) && (!"org".equals(treetype) && !"noum".equals(treetype))){
        		treeitem.setXml("/common/org/loadtree?params=child&amp;treetype="+ treetype + "&amp;parentid=" + codeitemid + "&amp;kind=0&amp;issuperuser=" + issuperuser + "&amp;manageprive=" + manageprive  + "&amp;action=" + action + "&amp;target=" + target);
	        	treeitem.setIcon("/images/pos_l.gif");
	            if("javascript:void(0)".equals(action))
	            	treeitem.setAction(action);			        
	            else
	                treeitem.setAction(action + "?b_search=link&amp;code=" + codeitemid + "&amp;kind=0");
	            strXml.append(treeitem.toChildNodeJS() + "\n");
	         }
		}
		strXml.append("</TreeNode>\n");
		//System.out.println(strXml.toString());
		return strXml.toString();
	} 
	return strXml.toString();

}
    protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1)
            throws ServletException, IOException {
        doPost(arg0, arg1);
    }
	private String getLoadTreeQueryString(String params,String isSuperuser,String parentid,String managepriv, UserView userview) {
		StringBuffer strsql=new StringBuffer();
		strsql.append("SELECT codesetid,codeitemdesc,codeitemid,parentid,childid ");
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
    				if(userview.getUserDeptId()!=null&&userview.getUserDeptId().length()>0)	
    				  strsql.append(" WHERE codeitemid='"+userview.getUserDeptId()+"'");	 
    				else if(userview.getUserOrgId()!=null&&userview.getUserOrgId().length()>0)
    				  strsql.append(" WHERE codeitemid='"+userview.getUserOrgId()+"'");
    				else
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
    	// 不显示岗位
    	if (!isPost) {
    		strsql.append(" and codesetid<>'@K' ");
    	}
    	
    	//zxj 20141119 增加历史时点控制
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String backdate = sdf.format(new Date());        
    	strsql.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
    	
    	strsql.append(" ORDER BY a0000,codeitemid ");
		return strsql.toString();
	}


}
