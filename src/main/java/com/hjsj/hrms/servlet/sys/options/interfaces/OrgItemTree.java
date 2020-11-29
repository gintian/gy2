package com.hjsj.hrms.servlet.sys.options.interfaces;

import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
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
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OrgItemTree extends HttpServlet {

	protected void doPost(HttpServletRequest req, HttpServletResponse resp){
		HttpSession session=req.getSession();
		UserView uv=(UserView) session.getAttribute(WebConstant.userView); //返回一个uservaiew对象
		resp.setContentType("text/xml");
		String param=req.getParameter("param");
		String codesetid=req.getParameter("codesetid");
		codesetid = codesetid == null ? "" : codesetid;
	
		String xml="";
		try{
		if("root".equals(param)){
			xml=this.getNodeXml(uv,param,null, codesetid);
		}else{
			String fid=req.getParameter("fid");
			xml=this.getNodeXml(uv,param,fid, codesetid);
		}
		resp.setContentType("text/xml;charset=UTF-8");
		resp.getWriter().println(xml);
		resp.getWriter().close();
//		System.out.println(xml);
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1)
	throws ServletException, IOException {
		doPost(arg0, arg1);
	}
	
	private String getNodeXml(UserView uv,String param,String fid, String codesetid) throws Exception{
		StringBuffer strXml = new StringBuffer();
		strXml.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>\n<TreeNode>\n");
		if("root".equalsIgnoreCase(param)){
			List fieldlist=this.getFieldlist(uv, codesetid);
			for(Iterator it=fieldlist.iterator();it.hasNext();){
				FieldSet fs=(FieldSet)it.next();
				TreeItemView treeitem = new TreeItemView();
				String fieldsetdesc=new String(fs.getFieldsetdesc().toString().replaceAll("&","&amp;"));
				treeitem.setName(fs.getFieldsetid());
				treeitem.setText(fieldsetdesc);
				treeitem.setTitle(fieldsetdesc);
				treeitem.setXml("/servlet/orgsettree?param=child&amp;fid="+fs.getFieldsetid());
				treeitem.setIcon("/images/groups.gif");
				treeitem.setAction("");
				strXml.append(treeitem.toChildNodeJS() + "\n");
			}
			
		}else{
			List itemlist=this.getItemlist(fid);
			if("B01".equalsIgnoreCase(fid)){
//				FieldItem efi=DataDictionary.getFieldItem("e01a1");
//				TreeItemView treeitem = new TreeItemView();
//				String itemdesc=new String(efi.getItemdesc().replace("&","&amp;").getBytes("GBK"), "ISO-8859-1");
//				treeitem.setName(efi.getItemid());
//				treeitem.setText(itemdesc);
//				treeitem.setTitle(itemdesc);
//				treeitem.setIcon("/images/groups.gif");
//				treeitem.setAction("");
//				strXml.append(treeitem.toChildNodeJS() + "\n");
				FieldItem bfi=DataDictionary.getFieldItem("b0110");
				TreeItemView btreeitem = new TreeItemView();
				String bitemdesc=new String(bfi.getItemdesc().replace("&","&amp;"));
				btreeitem.setName(bfi.getItemid());
				btreeitem.setText(bitemdesc);
				btreeitem.setTitle(bitemdesc);
				btreeitem.setIcon("/images/groups.gif");
				btreeitem.setAction("");
				strXml.append(btreeitem.toChildNodeJS() + "\n");
			}
			for(Iterator its=itemlist.iterator();its.hasNext();){
				DynaBean db =(DynaBean)its.next();
				TreeItemView treeitem = new TreeItemView();
				String itemdesc=new String(db.get("itemdesc").toString().replace("&","&amp;"));
				treeitem.setName((String) db.get("itemid"));
				treeitem.setText(itemdesc);
				treeitem.setTitle(itemdesc);
//				treeitem.setXml("");
				treeitem.setIcon("/images/groups.gif");
				treeitem.setAction("");
				strXml.append(treeitem.toChildNodeJS() + "\n");
			}
		}
		strXml.append("</TreeNode>\n"); 
		
		return strXml.toString();
		
	}
	private List getItemlist(String fid){
		String sql="select * from fielditem where fieldsetid='"+fid+"' and useflag='1' ";
		ContentDAO dao=null;
		Connection conn=null;
		List myList=new ArrayList();
		try {
			conn=AdminDb.getConnection();
			dao=new ContentDAO(conn);
			myList=dao.searchDynaList(sql);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(conn!=null)
					conn.close();
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}
		return myList;
	}
	
	private List getFieldlist(UserView uv, String codesetid){
		List myList=uv.getPrivFieldSetList(0);
		if(codesetid!=null&&codesetid.length()>0){
			FieldItem fi=DataDictionary.getFieldItem(codesetid);
			String sa=fi.getFieldsetid().substring(0,1);
			for(int i=0;i<myList.size();i++){
				FieldSet fs=(FieldSet) myList.get(i);
				if(!fs.getFieldsetid().startsWith(sa)){
					myList.remove(i);
					i--;
				}
				
			}
		}else{
			for(int i=0;i<myList.size();i++){
				FieldSet fs=(FieldSet) myList.get(i);
				if(!fs.getFieldsetid().startsWith("B")){
					myList.remove(i);
					i--;
				}
				
			}
		}
		return myList;
	}

}
