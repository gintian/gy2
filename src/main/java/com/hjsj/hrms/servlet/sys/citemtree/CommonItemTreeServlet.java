package com.hjsj.hrms.servlet.sys.citemtree;

import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CommonItemTreeServlet extends HttpServlet{
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp){
		HttpSession session=req.getSession();
		UserView uv=(UserView) session.getAttribute(WebConstant.userView);
		resp.setContentType("text/xml");
		String type=req.getParameter("type")==null?"":req.getParameter("type");
		String fsid=req.getParameter("fid")==null?"":req.getParameter("fid");
		String param=req.getParameter("param")==null?"":req.getParameter("param");
		String url=req.getParameter("url")==null?"":req.getParameter("url");
		String urlay=req.getParameter("urlay")==null?"":req.getParameter("urlay");
		String target=req.getParameter("target")==null?"":req.getParameter("target");
		url=url.replaceAll("&","&amp;");
		if(type==null){
			type="";
		}
		String xml="";
		try{	
			xml=this.getNodeXmo(uv,param,fsid,type,url,urlay,target);		
		resp.getWriter().println(xml);
		resp.getWriter().close();
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1)
	throws ServletException, IOException {
		doPost(arg0, arg1);
	}
	/**
	 * 获得树状菜单需要的xml文件格式
	 * @param uv Uerveiw
	 * @param param 是否为根节点
	 * @param fsid fiedlsetid
	 * @param type 显示树状菜单类型
	 * @return xml格式的字符串
	 * @throws UnsupportedEncodingException
	 */
	private String getNodeXmo(UserView uv,String param,String fsid,String type,String url,String urlay,String target) throws UnsupportedEncodingException{
		StringBuffer strXml = new StringBuffer();
		
		strXml.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>\n<TreeNode>\n");
		if("root".equalsIgnoreCase(param)){
			fsid=fsid.trim();
			if(fsid!=null&&fsid.length()>0){
				String refstr=this.getItemXml(uv,param,fsid,type,url,urlay,target);
				strXml.append(refstr);
			}else{
				String refstr=this.getFieldXml(uv,param,fsid,type,url,urlay,target);
				
				strXml.append(refstr);
			}
			
		}else{
			
			String restr=this.getItemXml(uv,param,fsid,type,url,urlay,target);
			strXml.append(restr);
		}
		strXml.append("</TreeNode>\n"); 
		
		return strXml.toString();
	}

/**
 * 返回指标list
 * @param uv userview
 * @param fsid 子集名称
 * @return fielditem list
 */
	private List getFieldItem(UserView uv,String fsid){
		ArrayList retlist=new ArrayList();
		FieldSet fs=DataDictionary.getFieldSetVo(fsid);
		retlist=fs.getFieldItemList(1);
		return retlist;
	}
	
	
/**
 * 返主集和子集
 * @param uv Userview
 * @param type 菜单类型
 * @return 放回一个List
 */
	private List getFieldlist(UserView uv,String type){
		ArrayList retlist=new ArrayList();
		String[] typestr=type.split("\\|");
		for(int i=0;i<typestr.length;i++){
			String ctype=typestr[i];
			if(ctype!=null&&ctype.length()>0){
				ArrayList fslist=uv.getPrivFieldSetList(this.getCt(ctype));
				retlist.addAll(fslist);
			
			}
		}
	
	return retlist;
}
	/**
	 * 返回加在指标类型
	 * @param type
	 * 0 全部
	 * 1 人员
	 * 2 机构
	 * 3 职位
	 * @return
	 */
	private int getCt(String type){
		
		int Ct=Constant.ALL_FIELD_SET;
		if("1".equals(type))
			Ct=Constant.EMPLOY_FIELD_SET;
		if("2".equals(type))
			Ct=Constant.UNIT_FIELD_SET;
		if("3".equals(type))
			Ct=Constant.POS_FIELD_SET;
		
		return Ct;
	}

	private String getItemXml(UserView uv,String param,String fsid,String type,String url,String urlay,String target) throws UnsupportedEncodingException{
		StringBuffer strXml=new StringBuffer();
		ArrayList itemlist=new ArrayList();
		String[] fsstr=fsid.split("\\|");
		for(int i=0;i<fsstr.length;i++){
			if(fsstr.length>0)
				itemlist=(ArrayList) this.getFieldItem(uv,fsstr[i]);
			for(Iterator its=itemlist.iterator();its.hasNext();){
				FieldItem fi=(FieldItem)its.next();
				if("1".equals(urlay)){
					url="";
				}else{
					if(url==null||url.length()<1){
						url="";
					}else{
						url=url+"&amp;setid="+fi.getFieldsetid()+"&amp;itemid="+fi.getItemid();
					}
				}
				TreeItemView treeitem = new TreeItemView();
				String itemdesc=new String(fi.getItemdesc().replace("&","&amp;").getBytes("GBK"), "ISO-8859-1");
				treeitem.setName((String) fi.getItemid()+"/"+fi.getItemdesc()+"/"+fi.getFieldsetid());
				treeitem.setText(itemdesc);
				treeitem.setTitle(itemdesc);
				treeitem.setIcon("/images/groups.gif");
				treeitem.setAction(url);
				treeitem.setTarget(target);
				strXml.append(treeitem.toChildNodeJS() + "\n");
			}
		}
		return strXml.toString();
	}
	private String getFieldXml(UserView uv,String param,String fsid,String type,String url,String urlay,String target) throws UnsupportedEncodingException{
		StringBuffer strXml=new StringBuffer();
		List fieldlist=this.getFieldlist(uv,type);
		for(Iterator it=fieldlist.iterator();it.hasNext();){
			FieldSet fs=(FieldSet)it.next();
			if(url==null||url.length()<1){
				url="";
			}else{
				url=url+"&amp;setid="+fs.getFieldsetid();
			}
			TreeItemView treeitem = new TreeItemView();
			String fieldsetdesc=new String(fs.getFieldsetdesc().toString().replace("&","&amp;").getBytes("GBK"), "ISO-8859-1");
			treeitem.setName(fs.getFieldsetid()+"/"+fs.getFieldsetdesc());
			treeitem.setText(fieldsetdesc);
			treeitem.setTitle(fieldsetdesc);
			treeitem.setXml("/servlet/citemtree?param=child&amp;fid="+fs.getFieldsetid()+"&amp;url="+url+"&amp;urlay="+urlay+"&amp;target="+target);
			treeitem.setIcon("/images/groups.gif");
			treeitem.setAction(url);
			treeitem.setTarget(target);
			strXml.append(treeitem.toChildNodeJS() + "\n");
		}
		return strXml.toString();
	}
}
