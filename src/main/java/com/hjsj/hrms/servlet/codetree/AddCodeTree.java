package com.hjsj.hrms.servlet.codetree;

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

public class AddCodeTree extends HttpServlet {

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
		String parentid = req.getParameter("parentid");
		String codesetid = req.getParameter("codesetid");
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
			sbXml.append(loadOrgItemNodes(params, codesetid, parentid, target,
					cid));
		} catch (Exception e) {
			System.out.println(e);
		}
		cat.debug("catalog xml" + sbXml.toString());
		resp.setContentType("text/xml");
		resp.getWriter().println(sbXml.toString());
	}

	private String loadOrgItemNodes(String params, String codesetid,
			String parentid, String target, String cid) throws Exception {
		StringBuffer strXml = new StringBuffer();
		List rs = ExecuteSQL.executeMyQuery(getLosdTreeQueryString(params,
				parentid, codesetid, cid));

		strXml
				.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>\n<TreeNode>\n");
		if ("child".equals(params)) {
			if (!rs.isEmpty()) {

				for (int i = 0; i < rs.size(); i++) {
					TreeItemView treeitem = new TreeItemView();
					DynaBean rec = (DynaBean) rs.get(i);
					String codeitemid = rec.get("codeitemid") != null ? rec
							.get("codeitemid").toString() : "";
					// 下一级菜单
					String codesetids = rec.get("codesetid") != null ? rec.get(
							"codesetid").toString() : "";
					parentid = rec.get("parentid") != null ? rec
							.get("parentid").toString() : "";
					String codeitemdesc = rec.get("codeitemdesc") != null ? new String(
							rec.get("codeitemdesc").toString().replaceAll("&",
									"&amp;").getBytes("GBK"), "ISO-8859-1")
							: "";
					treeitem.setName(codeitemid + "/" + codesetids + "/"
							+ parentid);
					treeitem.setText(codeitemdesc);
					// treeitem.setTitle(codeitemdesc);
					treeitem.setTarget(target);
					treeitem
							.setAction("/system/codemaintence/codetree.do?b_search=link&amp;codesetid="
									+ codesetids
									+ "&amp;parentid="
									+ codeitemid + "&amp;param=child");
					treeitem.setIcon("/images/admin.gif");
					treeitem
							.setXml("/maintence/codetree?params=child&amp;parentid="
									+ codeitemid
									+ "&amp;codesetid="
									+ codesetids + "&amp;target=" + target);
					strXml.append(treeitem.toChildNodeJS() + "\n");

				}
			}
		} else {
			if (!rs.isEmpty()) {
				for (int i = 0; i < rs.size(); i++) {
					TreeItemView treeitem = new TreeItemView();
					DynaBean rec = (DynaBean) rs.get(i);
					codesetid = rec.get("codesetid") != null ? rec.get(
							"codesetid").toString() : "";
					String codesetdesc = rec.get("codesetdesc") != null ? new String(
							rec.get("codesetdesc").toString().replaceAll("&",
									"&amp;").getBytes("GBK"), "ISO-8859-1")
							: "";
					treeitem.setName(codesetid);
					treeitem.setText(codesetid + " " + codesetdesc);
					treeitem.setTitle(codesetdesc);
					treeitem.setTarget(target);
					treeitem
							.setXml("/maintence/codetree?params=child&amp;codesetid="
									+ codesetid + "&amp;target=" + target);
					treeitem.setIcon("/images/groups.gif");
					treeitem
							.setAction("/system/codemaintence/codetree.do?b_search=link&amp;codesetid="
									+ codesetid + "&amp;param=root");
					strXml.append(treeitem.toChildNodeJS() + "\n");
				}
			}

		}
		strXml.append("</TreeNode>\n"); 
//		System.out.println(strXml.toString()) ;
		return strXml.toString();

	}

	/*
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1)
			throws ServletException, IOException {
		doPost(arg0, arg1);
	}

	private String getLosdTreeQueryString(String params, String parentid,
			String codesetid, String cid) {

		StringBuffer strsql = new StringBuffer();
		if ("root".equals(params)) {
			strsql
					.append("select codesetid,codesetdesc,maxlength from codeset where codesetid<>'@k' and codesetid<>'um' and codesetid<>'un'"
							+ cid);
		} else {
			if (parentid == null)
				strsql
						.append("select codesetid,codeitemid,codeitemdesc,parentid,childid from codeitem where codesetid='"
								+ codesetid + "' and parentid=codeitemid");
			else {
				strsql
						.append("select codesetid,codeitemid,codeitemdesc,parentid,childid from codeitem where codesetid='"
								+ codesetid
								+ "' and parentid='"
								+ parentid
								+ "' and codeitemid<>parentid");
			}
		}
		return strsql.toString();
	}

}