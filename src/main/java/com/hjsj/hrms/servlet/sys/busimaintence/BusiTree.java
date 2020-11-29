package com.hjsj.hrms.servlet.sys.busimaintence;

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

public class BusiTree extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	private Category cat = Category.getInstance(this.getClass());

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		StringBuffer sbXml = new StringBuffer();
		String params = req.getParameter("params");
		String target = req.getParameter("target");
		String cid = req.getParameter("cid");
		if (cid == null || "null".equals(cid)) {
			cid = "";
			req.removeAttribute("cid");
		} 

		try {
			sbXml.append(loadOrgItemNodes(params,  target,
					cid));
		} catch (Exception e) {
			System.out.println(e);
		}
		cat.debug("catalog xml" + sbXml.toString());
		resp.setContentType("text/xml;charset=UTF-8");
		resp.getWriter().println(sbXml.toString());
	}

	private String loadOrgItemNodes(String params,  String target, String cid) throws Exception {
		StringBuffer strXml = new StringBuffer();
		strXml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<TreeNode>\n");
		List rs = ExecuteSQL.executeMyQuery(getLosdTreeQueryString(params, cid));
		if(cid!=null&&cid.length()>0&& "root".equals(params)){
			//rs=ExecuteSQL.executeMyQuery("select fieldsetid,id,fieldsetdesc,displayorder,useflag,customdesc from t_hr_BusiTable where id="+cid+" order by displayorder");//已经构库的子集useflag='1'
			if (!rs.isEmpty()) {

				for (int i = 0; i < rs.size(); i++) {
					TreeItemView treeitem = new TreeItemView();
					DynaBean rec = (DynaBean) rs.get(i);
					String fieldsetid = rec.get("fieldsetid") != null ? rec.get("fieldsetid").toString() : "";
					// 下一级菜单
					String fieldsetdesc = rec.get("customdesc") != null ? /*new String(*/rec.get("customdesc").toString().replaceAll("&","&amp;")/*.getBytes("GBK"), "ISO-8859-1")*/: "";
					treeitem.setName(fieldsetid+"#0");
					treeitem.setText(fieldsetdesc);
					// treeitem.setTitle(codeitemdesc);
					treeitem.setTarget(target);
					treeitem.setAction("/system/busimaintence/showbusifield.do?b_query=link&amp;fieldsetid="+ fieldsetid + "&amp;param=child&amp;id="+cid);
					//treeitem.setIcon("/images/admin.gif");
					//treeitem.setXml("");
					strXml.append(treeitem.toChildNodeJS() + "\n");

				}
			}
		}else{
		
		if ("child".equals(params)) {
			if (!rs.isEmpty()) {

				for (int i = 0; i < rs.size(); i++) {
					TreeItemView treeitem = new TreeItemView();
					DynaBean rec = (DynaBean) rs.get(i);
					String fieldsetid = rec.get("fieldsetid") != null ? rec.get("fieldsetid").toString() : "";
					// 下一级菜单
					String fieldsetdesc = rec.get("customdesc") != null ? /*new String(*/rec.get("customdesc").toString().replaceAll("&","&amp;")/*.getBytes("GBK"), "ISO-8859-1")*/: "";
					treeitem.setName(fieldsetid+"#0");
					treeitem.setText(fieldsetdesc);
					treeitem.setTarget(target);
					treeitem.setAction("/system/busimaintence/showbusifield.do?b_query=link&amp;fieldsetid="+ fieldsetid + "&amp;param=child&amp;id="+cid);
					//treeitem.setIcon("/images/admin.gif"); 这里增加了一个图片为0的时候;
					if(!("0".equals(rec.get("useflag")))){
						treeitem.setIcon("/images/open1.png");
					}else if(("0".equals(rec.get("useflag")))){
						treeitem.setIcon("/images/open.png");
					}
					//treeitem.setXml("");
					strXml.append(treeitem.toChildNodeJS() + "\n");

				}
			}
		} else {
			if (!rs.isEmpty()) {
				for (int i = 0; i < rs.size(); i++) {
					TreeItemView treeitem = new TreeItemView();
					DynaBean rec = (DynaBean) rs.get(i);
					String id  = rec.get("id") != null ? rec.get("id").toString() : "";
					String name = rec.get("name") != null ? /*new String(*/rec.get("name").toString().replaceAll("&","&amp;")/*.getBytes("GBK"), "ISO-8859-1")*/: "";
					treeitem.setName(id+"#1");
					treeitem.setText(id + " " + name);
					treeitem.setTitle(name);
					treeitem.setTarget(target);
					treeitem.setXml("/servlet/BusiTree?params=child&amp;cid="+ id + "&amp;target=" + target);
			        //treeitem.setIcon("/images/groups.gif");
					treeitem.setAction("/system/busimaintence/ShowSubsys.do?b_query=link&amp;id="+id);
					strXml.append(treeitem.toChildNodeJS() + "\n");
				}
			}

		}
		}
		strXml.append("</TreeNode>\n"); 
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

	private String getLosdTreeQueryString(String params,String cid) {

		StringBuffer strsql = new StringBuffer();
		if ("root".equals(params)) {
			if(cid==null||cid.length()<1){
			strsql.append("select ts.id,name,description from t_hr_busitable tb left join t_hr_subsys ts on tb.id=ts.id where  is_available='1' group by ts.id,name,description order by ts.id"/*and id='20' or id='30' or id='31' or id='32' or id='33' or id='34' or id='35' or id='36'"*/);
			}else{
				strsql.append("select fieldsetid,id,fieldsetdesc,displayorder,useflag,customdesc from t_hr_BusiTable where id="+cid+" order by displayorder");

			}
		} else {
			
				strsql.append("select fieldsetid,id,fieldsetdesc,displayorder,useflag,customdesc from t_hr_BusiTable where  id="+cid+" order by displayorder");
			
		}
		return strsql.toString();
	}

}