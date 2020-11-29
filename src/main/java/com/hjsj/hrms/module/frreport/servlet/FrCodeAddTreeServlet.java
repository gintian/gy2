package com.hjsj.hrms.module.frreport.servlet;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;
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

public class FrCodeAddTreeServlet extends HttpServlet {

	private  Category cat = Category.getInstance(this.getClass());

	@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException { 
		StringBuffer sbXml = new StringBuffer();
		String params=req.getParameter("params");
		String issuperuser=req.getParameter("issuperuser");
		String parentid=req.getParameter("parentid");
		String privType = req.getParameter("privType");
		String manageprive=req.getParameter("manageprive"); 	
		String action=req.getParameter("action");
		action = PubFunc.decrypt(action);
		action = PubFunc.keyWord_reback(action);
		String target=req.getParameter("target");
		String showType=req.getParameter("showType");
		String treetype=req.getParameter("treetype");
		String orgtype=req.getParameter("orgtype");

		boolean isPost = true;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String backdate = req.getParameter("backdate");
		backdate = backdate!=null&&backdate.length()>9?backdate:sdf.format(new Date());
		String nmodule = req.getParameter("nmodule");
		nmodule = nmodule==null?"":nmodule;
		privType = (privType==null?"U":privType);
		UserView userView = null;
		if(params!=null && "root".equals(params)){
			userView = (UserView)req.getSession().getAttribute("userView");
		}
		try {
			sbXml.append(loadOrgItemNodes(params,issuperuser,parentid,manageprive,action,target,treetype,orgtype,backdate,nmodule,userView, isPost,showType));

		} catch (Exception e) {
			System.out.println(e);
		}
		cat.debug("catalog xml" + sbXml.toString());
		resp.setContentType("text/xml;charset=gb2312");
		resp.getWriter().write(sbXml.toString());
		resp.getWriter().close();
	}

	private String  loadOrgItemNodes(String params,String issuperuser,String parentid,String manageprive,String action,String target,String treetype,String orgtype,String backdate,String nmodule,UserView userView, boolean isPost,String showType) throws Exception
	{
		StringBuffer strXml=new StringBuffer();
		List rs=new ArrayList();
		rs=ExecuteSQL.executeMyQuery(getLoadTreeQueryString(params,issuperuser,parentid,manageprive,backdate,nmodule,userView, isPost,showType)); 
		if(!rs.isEmpty())
		{
			strXml.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>\n<TreeNode>\n");
			for(int i=0;i<rs.size();i++)
			{
				TreeItemView treeitem=new TreeItemView();
				DynaBean rec=(DynaBean)rs.get(i);
				String image="admin.gif";
				String codeitemid=rec.get("codeitemid")!=null?rec.get("codeitemid").toString():"";
				String codeitemdesc=rec.get("codeitemdesc")!=null?com.hrms.frame.codec.SafeCode.encode(rec.get("codeitemdesc").toString()):"";
				String codesetid=rec.get("codesetid")!=null?rec.get("codesetid").toString():"";
				treeitem.setName(codesetid+codeitemid);
				treeitem.setText(codeitemdesc);
				treeitem.setTitle(codeitemdesc);  
				treeitem.setTarget(target);
				if(!codeitemid.equalsIgnoreCase(rec.get("childid")!=null?rec.get("childid").toString():"")) {
					treeitem.setXml("/servlet/FrCodeAddTreeServlet?itemCode="+codeitemid+"&amp;params=child&amp;treetype="+ treetype + "&amp;showType="+showType+"&amp;parentid="  + codeitemid + "&amp;kind=2&amp;issuperuser=" + issuperuser + "&amp;manageprive=" + manageprive + "&amp;action=" + action + "&amp;target=" + target+"&amp;backdate="+backdate+"&amp;jump=1");
				}
				image="/images/table.gif";
				treeitem.setIcon(image);
				if("root0".equals(codeitemid)){
					codeitemid="";
				}
				if("javascript:void(0)".equals(action))
					treeitem.setAction(action);			
				else
					treeitem.setAction(action + "&amp;itemCode="+PubFunc.encrypt(codesetid+codeitemid)+"&amp;encryptParam="+PubFunc.encrypt("code=" + codeitemid + "&amp;kind=2&amp;root=0"+"&amp;backdate="+backdate+"&amp;jump=1&amp;query=&amp;idordesc="));
				strXml.append(treeitem.toChildNodeJS() + "\n");
			}
			strXml.append("</TreeNode>\n");
			//System.out.println(strXml.toString());
			return strXml.toString();
		}
		System.out.println(strXml.toString());
		return strXml.toString();

	}
	/* 
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
    protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1)
	throws ServletException, IOException {
		doPost(arg0, arg1);
	}
	private String getLoadTreeQueryString(String params,String isSuperuser,String parentid,String managepriv,String backdate,String nmodule,UserView userView, boolean isPost,String showType) {
		StringBuffer strsql=new StringBuffer();
		if(params!=null && "root".equals(params)){
			strsql.append("SELECT codesetid,codeitemdesc,codeitemid,parentid,childid,end_date ");
			strsql.append(" FROM codeitem WHERE codesetid = '"+showType+"'");
			strsql.append(" AND codeitemid=parentid ");
		}else{
			
			strsql.append("SELECT codesetid,codeitemdesc,codeitemid,parentid,childid,end_date ");
			strsql.append(" FROM codeitem WHERE codesetid = '"+showType+"'"); 
			strsql.append(" AND parentid='");
			strsql.append(parentid);
			strsql.append("'");
			strsql.append(" AND codeitemid<>parentid ");
			strsql.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
			strsql.append(" ORDER BY codeitemid ");
			
		}
		return strsql.toString();
	}

}
