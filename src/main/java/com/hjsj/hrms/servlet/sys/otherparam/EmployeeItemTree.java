package com.hjsj.hrms.servlet.sys.otherparam;

import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class EmployeeItemTree extends HttpServlet{
	protected void doPost(HttpServletRequest req, HttpServletResponse resp){
		HttpSession session=req.getSession();
		UserView uv=(UserView) session.getAttribute(WebConstant.userView);
		resp.setContentType("text/xml");
		String param=req.getParameter("param");
		String xml="";
		try{
		if("root".equals(param)){
			xml=this.getNodeXml(uv,param,null);
		}else{
			String fid=req.getParameter("fid");
			xml=this.getNodeXml(uv,param,fid);
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1)
	throws ServletException, IOException {
		doPost(arg0, arg1);
	}
	private String getNodeXml(UserView uv,String param,String fid) throws Exception{
		StringBuffer strXml = new StringBuffer();
		strXml.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>\n<TreeNode>\n");
		if("root".equalsIgnoreCase(param)){
			List fieldlist=this.getFieldlist(uv);
			for(Iterator it=fieldlist.iterator();it.hasNext();){
				FieldSet fs=(FieldSet)it.next();
				TreeItemView treeitem = new TreeItemView();
				treeitem.setName(fs.getFieldsetid());
				treeitem.setText(fs.getFieldsetdesc());
				treeitem.setTitle(fs.getFieldsetdesc());
				treeitem.setXml("/servlet/eitemtree?params=child&amp;fid="+fs.getFieldsetid());
				treeitem.setIcon("/images/groups.gif");
				treeitem.setAction("");
				strXml.append(treeitem.toChildNodeJS() + "\n");
			}
		}else{
			List itemlist=this.getItemlist(fid);
			for(Iterator its=itemlist.iterator();its.hasNext();){
				DynaBean db =(DynaBean)its.next();
				TreeItemView treeitem = new TreeItemView();
				String itemdesc=new String(db.get("itemdesc").toString().replaceAll("&","&amp;").getBytes("GBK"), "ISO-8859-1");
				treeitem.setName(itemdesc);
				treeitem.setText(itemdesc);
				treeitem.setTitle(itemdesc);
				treeitem.setXml("");
				treeitem.setIcon("/images/groups.gif");
				treeitem.setAction("");
				strXml.append(treeitem.toChildNodeJS() + "\n");
			}
		}
		strXml.append("</TreeNode>\n"); 
		return strXml.toString();
	}
	private List getItemlist(String fid){
		String sql="select * from fielditem where fieldsetid='"+fid+"' and useflag='1' and codesetid<>'0' order by displayid";
		List myList=ExecuteSQL.executeMyQuery(sql);
		return myList;
	}
	private List getFieldlist(UserView uv){
		List myList=uv.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
		return myList;
	}

}
