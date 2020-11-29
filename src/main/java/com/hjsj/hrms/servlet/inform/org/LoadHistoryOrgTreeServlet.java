/*
 * Created on 2006-3-6
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.servlet.inform.org;

import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.RowSet;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author wlh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LoadHistoryOrgTreeServlet extends HttpServlet {

	private static ThreadLocal threadLocal = new ThreadLocal();

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		UserView userView =  (UserView) req.getSession().getAttribute(WebConstant.userView);
		threadLocal.set(userView);
		StringBuffer sbXml = new StringBuffer();
		String isroot=req.getParameter("isroot");           /*是否根节点*/
		String issuperuser=req.getParameter("issuperuser"); /*是否超级用户*/
		String parentid=req.getParameter("parentid");       /*父节点*/
		String manageprive=req.getParameter("manageprive"); /*管理权限*/
		String action=req.getParameter("action");           /*节点的动作*/
		String target=req.getParameter("target");           /*页面显示的目标*/
		String catalog_id=req.getParameter("catalog_id");   /*历史的归档ID*/
		try {
			sbXml.append(loadOrgItemNodes(isroot,issuperuser,parentid,manageprive,action,target,catalog_id));
		} catch (Exception e) {
			System.out.println(e);
		}
		resp.setContentType("text/xml;charset=UTF-8");
		resp.getWriter().println(sbXml.toString());
	}
	private String  loadOrgItemNodes(String params,String issuperuser,String parentid,String manageprive,String action,String target,String catalog_id) throws Exception
	{
		StringBuffer strXml=new StringBuffer();
		List rs=ExecuteSQL.executeMyQuery(getLoadTreeQueryString(params,issuperuser,parentid,manageprive,catalog_id));
		if(!rs.isEmpty())
		{
			strXml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<TreeNode>\n");
			for(int i=0;i<rs.size();i++)
			{
				TreeItemView treeitem=new TreeItemView();
				DynaBean rec=(DynaBean)rs.get(i);
				String codeitemid=rec.get("codeitemid")!=null?rec.get("codeitemid").toString():"";
				String codeitemdesc=rec.get("codeitemdesc")!=null?/*new String(*/rec.get("codeitemdesc").toString()/*.replaceAll("&",	"&amp;").getBytes("GB2312"),"ISO-8859-1")*/:"";
				String codesetid=rec.get("codesetid")!=null?rec.get("codesetid").toString():"";
				String a0000=rec.get("a0000")!=null?rec.get("a0000").toString():"";
				a0000 = "0".equals(a0000)?"":a0000;
				treeitem.setName(codesetid+codeitemid);
				treeitem.setText(codeitemdesc);
				treeitem.setTitle(codeitemdesc);
				treeitem.setTarget(target);
				if(rec.get("codesetid")!=null && "UN".equals(rec.get("codesetid")))
				{
					treeitem.setXml("/general/inform/org/loadhistroyorgtree?isroot=child&amp;parentid="  + codeitemid + "&amp;kind=2&amp;catalog_id=" + catalog_id + "&amp;issuperuser=" + issuperuser + "&amp;manageprive=" + manageprive + "&amp;action=" + action + "&amp;target=" + target);
					if(StringUtils.isNotBlank(a0000))//说明是虚拟机构，显示图片修改一下
						treeitem.setIcon("/images/b_vroot.gif");
					else
						treeitem.setIcon("/images/unit.gif");
					if("javascript:void(0)".equals(action))
						treeitem.setAction(action);
					else
						treeitem.setAction(action + "?b_search=link&amp;code=" + codeitemid + "&amp;kind=2&amp;catalog_id=" + catalog_id);
					strXml.append(treeitem.toChildNodeJS() + "\n");
				}else if(rec.get("codesetid")!=null && "UM".equals(rec.get("codesetid"))){
					treeitem.setXml("/general/inform/org/loadhistroyorgtree?isroot=child&amp;parentid=" + codeitemid + "&amp;kind=1&amp;catalog_id=" + catalog_id + "&amp;issuperuser=" + issuperuser + "&amp;manageprive=" + manageprive  + "&amp;action=" + action + "&amp;target=" + target);
					if(StringUtils.isNotBlank(a0000))//说明是虚拟机构，显示图片修改一下
						treeitem.setIcon("/images/vdept.gif");
					else
						treeitem.setIcon("/images/dept.gif");
					if("javascript:void(0)".equals(action))
						treeitem.setAction(action);
					else
						treeitem.setAction(action + "?b_search=link&amp;code=" + codeitemid + "&amp;kind=1&amp;catalog_id=" + catalog_id);
					strXml.append(treeitem.toChildNodeJS() + "\n");
				}else if(rec.get("codesetid")!=null && "@K".equals(rec.get("codesetid"))){
					treeitem.setXml("/general/inform/org/loadhistroyorgtree?isroot=child&amp;parentid=" + codeitemid + "&amp;kind=0&amp;catalog_id=" + catalog_id + "&amp;issuperuser=" + issuperuser + "&amp;manageprive=" + manageprive  + "&amp;action=" + action + "&amp;target=" + target);
					if(StringUtils.isNotBlank(a0000))//说明是虚拟机构，显示图片修改一下
						treeitem.setIcon("/images/vdept.gif");
					else
						treeitem.setIcon("/images/vpos_l.gif");
					if("javascript:void(0)".equals(action))
						treeitem.setAction(action);
					else
						treeitem.setAction(action + "?b_search=link&amp;code=" + codeitemid + "&amp;kind=0&amp;catalog_id=" + catalog_id);
					strXml.append(treeitem.toChildNodeJS() + "\n");
				}
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
	private String getLoadTreeQueryString(String params,String isSuperuser,String parentid,String managepriv,String catalog_id) {
		StringBuffer strsql=new StringBuffer();
		strsql.append("SELECT hoh.codesetid,hoh.codeitemdesc,hoh.codeitemid,hoh.parentid,hoh.childid,hoh.catalog_id,vor.a0000");
		strsql.append(" FROM hr_org_history hoh left join vorganization vor on vor.codeitemid = hoh.codeitemid ");//和虚拟机构关联一下，如果这个机构不是虚拟机构的话，a0000没有值，为了区别显示
		if(params!=null && "root".equals(params)){
			if("1".equals(isSuperuser))
			{
				strsql.append(" WHERE hoh.codesetid='UN' AND hoh.codeitemid=hoh.parentid and hoh.catalog_id='" + catalog_id + "'");
			}
			else
			{
    			/*if((managepriv !=null && managepriv.trim().length()==2))
    			{
    				strsql.append(" WHERE codesetid='UN' AND codeitemid=parentid and catalog_id='" + catalog_id + "'");
    			}else if((managepriv !=null && managepriv.trim().length()>=2))
    			{
    				managepriv=managepriv.substring(2,managepriv.length());
	    			strsql.append(" WHERE codeitemid='");
	    			strsql.append(managepriv);
	    			strsql.append("' and catalog_id='");
	    			strsql.append(catalog_id);
	    			strsql.append("'");
    			}else
    			{
    				strsql.append(" WHERE 1=2");
    			}*/
				String busi=this.getBusi_org_dept((UserView) threadLocal.get());
				if(busi.length()>2){
					if(busi.indexOf("`")!=-1){
						StringBuffer sb = new StringBuffer();
						String[] tmps=busi.split("`");
						for(int i=0;i<tmps.length;i++){
							String a_code=tmps[i];
							if(a_code.length()>2){
								sb.append("','"+a_code.substring(2));
							}
						}
						if(sb.length()>3){
							strsql.append(" where hoh.codeitemid in('"+sb.substring(3)+"') ");
							strsql.append(" and hoh.catalog_id='");
							strsql.append(catalog_id);
							strsql.append("'");
						}else if("UN".equalsIgnoreCase(tmps[0].toUpperCase())){
							strsql.append(" WHERE hoh.codesetid='UN' AND hoh.codeitemid=hoh.parentid and hoh.catalog_id='" + catalog_id + "'");
						}else
							strsql.append(" where 1=2 ");
					}else{
						strsql.append(" WHERE hoh.codeitemid='");
						strsql.append(busi.substring(2));
						strsql.append("' and hoh.catalog_id='");
						strsql.append(catalog_id);
						strsql.append("'");
					}
				}else{
					strsql.append(" where 1=2 ");
				}
			}
		}
		else
		{
			strsql.append(" WHERE hoh.parentid='");
			strsql.append(parentid);
			strsql.append("'");
			strsql.append(" AND hoh.codeitemid<>hoh.parentid and hoh.catalog_id='");
			strsql.append(catalog_id);
			strsql.append("'");
		}
		strsql.append(" ORDER BY hoh.a0000,hoh.codeitemid ");
		return strsql.toString();
	}

	private String getBusi_org_dept(UserView userView) {
		String busi = "";
		String busi_org_dept = "";
		Connection conn = null;
		RowSet rs = null;
		try {

			busi_org_dept = userView.getUnitIdByBusi("4");
			if (busi_org_dept.length() > 0) {
				busi = com.hjsj.hrms.utils.PubFunc.getTopOrgDept(busi_org_dept);
			}else{
				busi=userView.getManagePrivCode()+userView.getManagePrivCodeValue();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {// 1,UNxxx`UM9191`|2,UNxxx`UM9191`
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return busi;
	}
}
