package com.hjsj.hrms.servlet.general.operation;

import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.IResourceConstant;
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
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

public class OperationTree extends HttpServlet {

	/**
	 * Constructor of the object.
	 */

	/*
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	private Category cat = Category.getInstance(this.getClass());

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		StringBuffer sbXml = new StringBuffer();
		String params = req.getParameter("params");
		String statid = req.getParameter("statid");
		String operationcode = req.getParameter("operationcode");
		String target = req.getParameter("target");
		String cid = req.getParameter("cid");
		if (cid == null || "null".equals(cid)) {
			cid = "";
			req.removeAttribute("cid");
		} else {
			cid = "and codesetid='" + cid + "'";
			req.removeAttribute("cid");
		}

		try {
			sbXml.append(loadOrgItemNodes(params, statid, operationcode, target,
					cid,req));
		} catch (Exception e) {
			System.out.println(e);
		}
		cat.debug("catalog xml" + sbXml.toString());
		resp.setContentType("text/xml;charset=gb2312");
		resp.getWriter().println(sbXml.toString());
	}

	
	public boolean isPriv(String operationcode,UserView  userView)
	{
		boolean isPriv=false;
		Connection connection = null;
		ResultSet resultset = null;
		/*
		if(userView.isSuper_admin())
			return true;
		*/
		try
		{
			connection = (Connection) AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(connection);			
			resultset = dao.search("select tabid  from template_table where operationcode like '"+operationcode+"%' ");
			while(resultset.next())
			{
				String _tabid=resultset.getString("tabid");
				boolean isCorrect = false;
      			if (userView.isHaveResource(IResourceConstant.RSBD, _tabid))//人事移动
      				isCorrect = true;
      			if (!isCorrect)
      				if (userView.isHaveResource(IResourceConstant.ORG_BD,
      						_tabid))//组织变动
      					isCorrect = true;
      			if (!isCorrect)
      				if (userView.isHaveResource(IResourceConstant.POS_BD,
      						_tabid))//岗位变动
      					isCorrect = true;
      			if (!isCorrect)
      				if (userView.isHaveResource(IResourceConstant.GZBD,
      						_tabid))//工资变动
      					isCorrect = true;
      			if (!isCorrect)
      				if (userView.isHaveResource(IResourceConstant.INS_BD,
      						_tabid))//保险变动
      					isCorrect = true;
      			if (!isCorrect)
      				if (userView.isHaveResource(IResourceConstant.PSORGANS,
      						_tabid))
      					isCorrect = true;
      			if (!isCorrect)
      				if (userView.isHaveResource(
      						IResourceConstant.PSORGANS_FG, _tabid))
      					isCorrect = true;
      			if (!isCorrect)
      				if (userView.isHaveResource(
      						IResourceConstant.PSORGANS_GX, _tabid))
      					isCorrect = true;
      			if (!isCorrect)
      				if (userView.isHaveResource(
      						IResourceConstant.PSORGANS_JCG, _tabid))
      					isCorrect = true;
				
				if(isCorrect)
				{
					isPriv=true;
					break;
				}
			}
			
			
			
	
			
		}
		 catch (Exception ex) {
				ex.printStackTrace();
			} 
		 finally {
				/**
				 * 关闭各种资源
				 */
				try { 
					if (resultset != null)
						resultset.close();
					if (connection != null)
						connection.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
		}
		return isPriv;
	}
	
	
	
	private String loadOrgItemNodes(String params, String statid,
			String operationcode, String target, String cid,HttpServletRequest req) throws Exception {
		StringBuffer strXml = new StringBuffer();
		List rs = ExecuteSQL.executeMyQuery(getLosdTreeQueryString(params,statid, operationcode));
		UserView userView=(UserView)req.getSession().getAttribute(WebConstant.userView);
		strXml.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>\n<TreeNode>\n");
		if ("child".equals(params))
		{
			if (!rs.isEmpty()) 
			{
				for (int i = 0; i < rs.size(); i++) 
				{
					TreeItemView treeitem = new TreeItemView();
					DynaBean rec = (DynaBean) rs.get(i);
					if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
						statid = rec.get("static_o") != null ? rec.get(
								"static_o").toString() : "";
					}else {
						statid = rec.get("static") != null ? rec.get(
								"static").toString() : "";
					}
					
					operationcode = rec.get("operationcode") != null ? rec.get(
						"operationcode").toString() : "";
						
					if(operationcode.length()>0&&!isPriv(operationcode,userView))	
						continue;
						
					String operationid = rec.get("operationid") != null ? rec.get(
						"operationid").toString() : "";
					String operationname = rec.get("operationname") != null?com.hrms.frame.codec.SafeCode.encode(rec.get("operationname").toString()):"";
					treeitem.setName(operationid+"/"+operationcode);
					treeitem.setText(operationname);
					treeitem.setTitle(operationname);
					treeitem.setTarget(target);
					//treeitem.setXml("");
					treeitem.setIcon("/images/open.png");
					treeitem.setAction("/general/operation/showtable.do?b_query=link&amp;operationcode="+operationcode);
					strXml.append(treeitem.toChildNodeJS() + "\n");
				}
			}
		} 
		else 
		{
			if (!rs.isEmpty()) 
			{
				for (int i = 0; i < rs.size(); i++) {
					TreeItemView treeitem = new TreeItemView();
					DynaBean rec = (DynaBean) rs.get(i);
					if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
						statid = rec.get("static_o") != null ? rec.get("static_o").toString() : "";
					}else {
						statid = rec.get("static") != null ? rec.get("static").toString() : "";
					}
					
					operationcode = rec.get("operationcode") != null ? rec.get("operationcode").toString() : "";
					if(operationcode.length()>0&&!isPriv(operationcode,userView))	
						continue;
					
					String operationid = rec.get("operationid") != null ? rec.get("operationid").toString() : "";
					String operationname = rec.get("operationname") != null?com.hrms.frame.codec.SafeCode.encode(rec.get("operationname").toString()):"";
					treeitem.setName(operationid+"/"+operationcode);
					treeitem.setText(operationname);
					treeitem.setTitle(operationname);
					treeitem.setTarget(target);
					treeitem.setXml("/servlet/OperationTree?params=child&amp;statid="+statid+"&amp;target="+ target+"&amp;operationcode="+ operationcode);
					treeitem.setIcon("/images/open.png");
					treeitem.setAction("/general/operation/showtable.do?b_query=link&amp;operationcode="+operationcode);
					strXml.append(treeitem.toChildNodeJS() + "\n");
				}
			}

		}
		strXml.append("</TreeNode>\n"); 
		return strXml.toString();
	}


	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1)
			throws ServletException, IOException {
		doPost(arg0, arg1);
	}

	private String getLosdTreeQueryString(String params, String statid, String operationcode) {
		String _static="static";
		if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
			_static="static_o";
		}
		StringBuffer strsql = new StringBuffer();
		if ("root".equals(params))
		{
			if("all".equals(statid))
			{
				strsql.append("select operationid,"+_static+",operationcode,operationname,operationtype,policyid,factor,expr from operation  where  operationcode like '__' order by operationcode");
			}else
			{
				strsql.append("select operationid,"+_static+",operationcode,operationname,operationtype,policyid,factor,expr from operation  where "+_static+"="+statid+"  and operationcode like '__' order by operationcode");
			}
		} 
		else 
		{
				strsql.append("select operationid,"+_static+",operationcode,operationname,operationtype,policyid,factor,expr from operation  where "+_static+"="+statid+"  and operationcode like '"+operationcode+"__'  order by operationcode");	
		}
		return strsql.toString();
	}

}