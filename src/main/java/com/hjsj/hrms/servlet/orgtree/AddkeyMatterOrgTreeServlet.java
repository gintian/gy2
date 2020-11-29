package com.hjsj.hrms.servlet.orgtree;

import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.log4j.Category;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddkeyMatterOrgTreeServlet extends HttpServlet
{
    private Category cat = Category.getInstance(this.getClass());

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {

	StringBuffer sbXml = new StringBuffer();
	String params = req.getParameter("params");
	String issuperuser = req.getParameter("issuperuser");
	String parentid = req.getParameter("parentid");
	String manageprive = req.getParameter("manageprive");
	String action = req.getParameter("action");
	String target = req.getParameter("target");
	String treetype = req.getParameter("treetype");
	String orgtype = req.getParameter("orgtype");
	String objecType = req.getParameter("objecType");
	String userbase = req.getParameter("userbase");
	if (orgtype == null || orgtype.length() <= 0)
	    orgtype = "org";
	try
	{
	    if ("1".equals(objecType))// 团体
		sbXml.append(loadOrgItemNodes1(params, issuperuser, parentid, manageprive, action, target, treetype, orgtype, objecType));
	    else if ("2".equals(objecType))// 人员
		sbXml.append(loadOrgItemNodes2(params, issuperuser, parentid, manageprive, action, target, treetype, orgtype, objecType,userbase));

	} catch (Exception e)
	{
	    System.out.println(e);
	}
	cat.debug("catalog xml" + sbXml.toString());
	resp.setContentType("text/xml;charset=UTF-8");
	resp.getWriter().println(sbXml.toString());
    }

    /*
         * 加载团体类型的树结点到部门
         */
    private String loadOrgItemNodes1(String params, String issuperuser, String parentid, String manageprive, String action, String target, String treetype, String orgtype, String objecType)
	    throws Exception
    {

	StringBuffer strXml = new StringBuffer();
	List rs = new ArrayList();
	if (orgtype == null || orgtype.length() <= 0 || !"vorg".equalsIgnoreCase(orgtype))
	    rs = ExecuteSQL.executeMyQuery(getLoadTreeQueryString(params, issuperuser, parentid, manageprive));
//	getVorgTreeXml(rs, manageprive, parentid);
	if (!rs.isEmpty())
	{
	    strXml.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>\n<TreeNode>\n");
	    for (int i = 0; i < rs.size(); i++)
	    {
		TreeItemView treeitem = new TreeItemView();
		DynaBean rec = (DynaBean) rs.get(i);
		String org_type = rec.get("orgtype") != null ? rec.get("orgtype").toString() : "";
		String image = "";
		String codeitemid = rec.get("codeitemid") != null ? rec.get("codeitemid").toString() : "";
		String codeitemdesc = rec.get("codeitemdesc") != null ? rec.get("codeitemdesc").toString().replaceAll("&", "&amp;"): "";
		String codesetid = rec.get("codesetid") != null ? rec.get("codesetid").toString() : "";

		treeitem.setName(codesetid + codeitemid);
		treeitem.setText(codeitemdesc);
		treeitem.setTitle(codeitemdesc);
		treeitem.setTarget(target);

		if (rec.get("codesetid") != null && "UN".equals(rec.get("codesetid")))
		{
		    if (!codeitemid.equalsIgnoreCase(rec.get("childid") != null ? rec.get("childid").toString() : ""))
			treeitem.setXml("/manageKeyMatter/loadtree?params=child&amp;orgtype=" + org_type + "&amp;treetype=" + treetype + "&amp;parentid=" + codeitemid + "&amp;kind=2&amp;issuperuser="
				+ issuperuser + "&amp;manageprive=" + manageprive + "&amp;action=" + action + "&amp;target=" + target + "&amp;objecType=" + objecType);
		    if ("vorg".equals(org_type) && !"vorg".equals(orgtype))
			image = "/images/vroot.gif";
		    else
			image = "/images/unit.gif";
		    treeitem.setIcon(image);
		    if ("javascript:void(0)".equals(action))
			treeitem.setAction(action);
		    else
			treeitem.setAction(action + "?b_query=link&amp;code=" + codeitemid + "&amp;kind=2&amp;orgtype=" + org_type + "");
		    strXml.append(treeitem.toChildNodeJS() + "\n");
		} else if (rec.get("codesetid") != null && "UM".equals(rec.get("codesetid")) && !"noum".equals(treetype))
		{
		    String childid = rec.get("childid") != null ? rec.get("childid").toString() : "";

		    if (!codeitemid.equalsIgnoreCase(childid) && ("UM".equals(this.getChildNode(childid))))
			treeitem.setXml("/manageKeyMatter/loadtree?params=child&amp;orgtype=" + org_type + "&amp;treetype=" + treetype + "&amp;parentid=" + codeitemid + "&amp;kind=1&amp;issuperuser="
				+ issuperuser + "&amp;manageprive=" + manageprive + "&amp;action=" + action + "&amp;target=" + target + "&amp;objecType=" + objecType);
		    if ("vorg".equals(org_type) && !"vorg".equals(orgtype))
			image = "/images/vdept.gif";
		    else
			image = "/images/dept.gif";
		    treeitem.setIcon(image);
		    if ("javascript:void(0)".equals(action))
			treeitem.setAction(action);
		    else
			treeitem.setAction(action + "?b_query=link&amp;code=" + codeitemid + "&amp;kind=1&amp;orgtype=" + org_type + "");
		    strXml.append(treeitem.toChildNodeJS() + "\n");
		}
	    }
	    strXml.append("</TreeNode>\n");
	}

	return strXml.toString();

    }

    /*
         * 加载人员类型的树
         */
    private String loadOrgItemNodes2(String params, String issuperuser, String parentid, String manageprive, String action, String target, String treetype, String orgtype, String objecType,String userbase)
	    throws Exception
    {

//	System.out.println("orgtype:" + orgtype + " params:" + params + " issuperuser:" + issuperuser + " parentid:" + parentid + " manageprive:" + manageprive + " --" + this.isExistChild(parentid));

	StringBuffer strXml = new StringBuffer();
	List rs = new ArrayList();

	// 看是否在单位部门职位关系表中存在子节点;如果不存在从人员表中取人员做子节点
	if (this.isExistChild(parentid) || "00".equals(parentid))
	{
	    if (orgtype == null || orgtype.length() <= 0 || !"vorg".equalsIgnoreCase(orgtype))
		rs = ExecuteSQL.executeMyQuery(getLoadTreeQueryString(params, issuperuser, parentid, manageprive));
	   // getVorgTreeXml(rs, manageprive, parentid);
	    if (!rs.isEmpty())
	    {
		strXml.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>\n<TreeNode>\n");
		for (int i = 0; i < rs.size(); i++)
		{
		    TreeItemView treeitem = new TreeItemView();
		    DynaBean rec = (DynaBean) rs.get(i);
		    String org_type = rec.get("orgtype") != null ? rec.get("orgtype").toString() : "";
		    String image = "";
		    String codeitemid = rec.get("codeitemid") != null ? rec.get("codeitemid").toString() : "";
		    String codeitemdesc = rec.get("codeitemdesc") != null ? rec.get("codeitemdesc").toString().replaceAll("&", "&amp;") : "";
		    String codesetid = rec.get("codesetid") != null ? rec.get("codesetid").toString() : "";

		    treeitem.setName(codesetid + codeitemid);
		    treeitem.setText(codeitemdesc);
		    treeitem.setTitle(codeitemdesc);
		    treeitem.setTarget(target);
		    if (rec.get("codesetid") != null && "UN".equals(rec.get("codesetid")))
		    {
			if (!codeitemid.equalsIgnoreCase(rec.get("childid") != null ? rec.get("childid").toString() : ""))
			    treeitem.setXml("/manageKeyMatter/loadtree?params=child&amp;orgtype=" + org_type + "&amp;treetype=" + treetype + "&amp;parentid=" + codeitemid
				    + "&amp;kind=2&amp;issuperuser=" + issuperuser + "&amp;manageprive=" + manageprive + "&amp;action=" + action + "&amp;target=" + target + "&amp;objecType="
				    + objecType+"&amp;userbase="+userbase);
			if ("vorg".equals(org_type) && !"vorg".equals(orgtype))
			    image = "/images/vroot.gif";
			else
			    image = "/images/unit.gif";
			treeitem.setIcon(image);
			if ("javascript:void(0)".equals(action))
			    treeitem.setAction(action);
			// else if ("duty".equals(treetype))
			// treeitem.setAction("showerrorinfo.do?b_query=link&amp;orgtype="
			// + org_type + "&amp;code=" + codeitemid +
			// "&amp;kind=2");
			else
			    treeitem.setAction(action + "?b_query=link&amp;code=" + codeitemid + "&amp;kind=2&amp;orgtype=" + org_type + "");
			strXml.append(treeitem.toChildNodeJS() + "\n");
		    } else if (rec.get("codesetid") != null && "UM".equals(rec.get("codesetid")) && !"noum".equals(treetype))
		    {
			String childid = rec.get("childid") != null ? rec.get("childid").toString() : "";
			if (!codeitemid.equalsIgnoreCase(childid) || (codeitemid.equalsIgnoreCase(childid) && this.searchEmpByType(codeitemid, "dept",userbase)))
			    treeitem.setXml("/manageKeyMatter/loadtree?params=child&amp;orgtype=" + org_type + "&amp;treetype=" + treetype + "&amp;parentid=" + codeitemid
				    + "&amp;kind=1&amp;issuperuser=" + issuperuser + "&amp;manageprive=" + manageprive + "&amp;action=" + action + "&amp;target=" + target + "&amp;objecType="
				    + objecType+"&amp;userbase="+userbase);
			if ("vorg".equals(org_type) && !"vorg".equals(orgtype))
			    image = "/images/vdept.gif";
			else
			    image = "/images/dept.gif";
			treeitem.setIcon(image);
			if ("javascript:void(0)".equals(action))
			    treeitem.setAction(action);
			else
			    treeitem.setAction(action + "?b_query=link&amp;code=" + codeitemid + "&amp;kind=1&amp;orgtype=" + org_type + "");
			strXml.append(treeitem.toChildNodeJS() + "\n");
		    } else if (rec.get("codesetid") != null && "@K".equals(rec.get("codesetid")) && (!"org".equals(treetype) && !"noum".equals(treetype)))
		    {
			String childid = rec.get("childid") != null ? rec.get("childid").toString() : "";
			if (!codeitemid.equalsIgnoreCase(childid) || (codeitemid.equalsIgnoreCase(childid) && this.searchEmpByType(codeitemid, "position",userbase)))
			    treeitem.setXml("/manageKeyMatter/loadtree?params=child&amp;treetype=" + treetype + "&amp;parentid=" + codeitemid + "&amp;kind=0&amp;issuperuser=" + issuperuser
				    + "&amp;manageprive=" + manageprive + "&amp;action=" + action + "&amp;target=" + target + "&amp;objecType=" + objecType+"&amp;userbase="+userbase);
			if ("vorg".equals(org_type) && !"vorg".equals(orgtype))
			    image = "/images/vpos_l.gif";
			else
			    treeitem.setIcon("/images/pos_l.gif");

			if ("javascript:void(0)".equals(action))
			    treeitem.setAction(action);
			else
			    treeitem.setAction(action + "?b_query=link&amp;orgtype=" + org_type + "&amp;code=" + codeitemid + "&amp;kind=0");
			strXml.append(treeitem.toChildNodeJS() + "\n");
		    }
		}
		strXml.append("</TreeNode>\n");
	    }
	} else
	{
	    String type = this.getType(parentid);
	    String strSql = "";
	    if ("UM".equals(type))// 部门下找人
		strSql = "select a0100,a0101 from "+userbase+"a01 where (e01a1=''  or e01a1 is null ) and  e0122='" + parentid + "'";
	    if ("@K".equals(type))// 职位下找人
		strSql = "select a0100,a0101 from "+userbase+"a01 where e01a1='" + parentid + "'";
	    rs = ExecuteSQL.executeMyQuery(strSql);
	    if (!rs.isEmpty())
	    {
		strXml.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>\n<TreeNode>\n");
		for (int i = 0; i < rs.size(); i++)
		{
		    TreeItemView treeitem = new TreeItemView();
		    DynaBean rec = (DynaBean) rs.get(i);
		    String codeitemid = rec.get("a0100") != null ? rec.get("a0100").toString() : "";
		    String codeitemdesc = rec.get("a0101") != null ? new String(rec.get("a0101").toString().replaceAll("&", "&amp;").getBytes("GBK"), "ISO-8859-1") : "";
		    
		    treeitem.setName("usr" + codeitemid);
		    treeitem.setText(codeitemdesc);
		    treeitem.setTitle(codeitemdesc);
		    treeitem.setTarget(target);
		    treeitem.setIcon("/images/man.gif");

		    if ("javascript:void(0)".equals(action))
			treeitem.setAction(action);
		    else
			treeitem.setAction(action + "?b_query=link&amp;" + "code=" + codeitemid + "&amp;kind=3");
		    strXml.append(treeitem.toChildNodeJS() + "\n");
		}
		strXml.append("</TreeNode>\n");
	    }
	}
	return strXml.toString();
    }

    /*
         * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
         *      javax.servlet.http.HttpServletResponse)
         */
    protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException
    {

	doPost(arg0, arg1);
    }

    private String getLoadTreeQueryString(String params, String isSuperuser, String parentid, String managepriv)
    {

	StringBuffer strsql = new StringBuffer();
	strsql.append("SELECT codesetid,codeitemdesc,codeitemid,parentid,childid,'org' as orgtype ");
	strsql.append(" FROM organization ");
	if (params != null && "root".equals(params))
	{
	    if ("1".equals(isSuperuser))
	    {
		strsql.append(" WHERE codeitemid=parentid ");
	    } else
	    {

		if ((managepriv != null && managepriv.trim().length() == 2))
		{
		    strsql.append(" WHERE codeitemid=parentid ");
		} else if ((managepriv != null && managepriv.trim().length() >= 2))
		{
		    managepriv = managepriv.substring(2, managepriv.length());
		    strsql.append(" WHERE codeitemid='");
		    strsql.append(managepriv);
		    strsql.append("'");
		} else
		{
		    strsql.append(" WHERE 1=2");
		}
	    }
	} else
	{
	    strsql.append(" WHERE parentid='");
	    strsql.append(parentid);
	    strsql.append("'");
	    strsql.append(" AND codeitemid<>parentid ");
	}
	strsql.append(" ORDER BY a0000,codeitemid ");
	return strsql.toString();
    }

    /**
         * 虚拟表结构SQL
         * 
         * @param params
         * @param issuperuser
         * @param parentid
         * @param manageprive
         * @return
         */
    private String getLoadVorgTreeQueryString(String manageprive, String parentid)
    {

	StringBuffer strsql = new StringBuffer();
	strsql.append("SELECT codesetid,codeitemdesc,codeitemid,parentid,childid,'vorg' as orgtype ");
	strsql.append(" FROM vorganization ");
	manageprive = manageprive.substring(2, manageprive.length());
	strsql.append(" WHERE ");
	strsql.append(" parentid='" + parentid + "'");
	strsql.append(" order by A0000");
	return strsql.toString();
    }

    private void getVorgTreeXml(List rs, String manageprive, String parentid)
    {

	List v_rs = ExecuteSQL.executeMyQuery(getLoadVorgTreeQueryString(manageprive, parentid));
	if (!v_rs.isEmpty())
	{
	    for (int i = 0; i < v_rs.size(); i++)
	    {
		DynaBean rec = (DynaBean) v_rs.get(i);
		rs.add(rec);
	    }
	}
    }

    // 判断节点是否是部门节点
    private String getChildNode(String codeitemid)
    {

	String flag = "";
	String strSql = "select codesetid from  organization where codesetid='UM' and codeitemid='" + codeitemid + "'";
	List list = ExecuteSQL.executeMyQuery(strSql);
	if (list.isEmpty())
	    flag = "null";
	else
	{
	    DynaBean bean = (DynaBean) list.get(0);
	    flag = (String) bean.get("codesetid");
	}
	return flag;
    }

    // 判断是否存在子节点在organization表中
    private boolean isExistChild(String parentId)
    {

	boolean flag = false;
	String strSql = "select codesetid from  organization where parentid='" + parentId + "'";
	List list = ExecuteSQL.executeMyQuery(strSql);
	if (!list.isEmpty())
	    flag = true;
	return flag;
    }

    // 找人
    private boolean searchEmpByType(String code, String type,String userbase)
    {

	boolean flag = false;
	String strSql = "";
	if ("position".equals(type))
	    strSql = "select a0100 from "+userbase+"a01 where e01a1='" + code + "'";
	else if ("dept".equals(type))
	    strSql = "select a0100 from "+userbase+"a01 where e0122='" + code + "'";
	List list = ExecuteSQL.executeMyQuery(strSql);
	if (!list.isEmpty())
	    flag = true;
	return flag;
    }

    // 取得当前节点的类型
    private String getType(String nodeId)
    {

	String flag = "";
	String strSql = "select codesetid from  organization where codeitemid='" + nodeId + "'";
	List list = ExecuteSQL.executeMyQuery(strSql);
	if (list.isEmpty())
	    flag = "null";
	else
	{
	    DynaBean bean = (DynaBean) list.get(0);
	    flag = (String) bean.get("codesetid");
	}
	return flag;

    }
}
