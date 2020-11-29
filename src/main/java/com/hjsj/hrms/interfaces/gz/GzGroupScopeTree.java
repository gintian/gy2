package com.hjsj.hrms.interfaces.gz;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class GzGroupScopeTree {
	String codesetid="";
	String codeitemid="";
	
	
	public GzGroupScopeTree(String codesetid,String codeitemid)
	{
		this.codesetid=codesetid;
		this.codeitemid=codeitemid;
	}
	
	public String outPutXml() throws GeneralException {

		// 生成的XML文件
		StringBuffer xmls = new StringBuffer();
		// 创建xml文件的根元素
		Element root = new Element("TreeNode");
		// 设置根元素属性
		root.setAttribute("id", "");
		root.setAttribute("text", "root");
		root.setAttribute("title", "organization");
		// 创建xml文档自身
		Document myDocument = new Document(root);
		ArrayList list =getChildList();

		for (Iterator t = list.iterator(); t.hasNext();) {
			LazyDynaBean abean = (LazyDynaBean) t.next();

			// 创建子元素
			Element child = new Element("TreeNode");
			// 设置子元素属性
			String id = (String) abean.get("id");
			String name = (String) abean.get("name");
			child.setAttribute("defaultInput","1");
			child.setAttribute("id",id);
			child.setAttribute("text", name);
			child.setAttribute("title", name);
			if(!"4".equals(id))
					child.setAttribute("href", "");
			else
					child.setAttribute("href", "");
			child.setAttribute("target", "_self"); 
			child.setAttribute("icon","/images/prop_ps.gif");	
			String a_xml="/gz/gz_accounting/report/gz_group_tree.jsp?codesetid="+this.codesetid+"&codeitemid="+id;
			child.setAttribute("xml", a_xml);
			// 将子元素作为内容添加到根元素
			root.addContent(child);
			
		}

		XMLOutputter outputter = new XMLOutputter();
		// 格式化输出类
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);

		// 将生成的XML文件作为字符串形式
		xmls.append(outputter.outputString(myDocument));
		return xmls.toString();
	}
	
	private ArrayList getChildList()
	{
		ArrayList list=new ArrayList();
		 // DB相关
		ResultSet rs = null;	
		Connection conn=null;
		try
		{
			String sql="";
			this.codeitemid = PubFunc.keyWord_reback(this.codeitemid);
			String[] temps=this.codeitemid.split("/");
			if("UN".equals(this.codesetid))
			{
				if("-1".equals(temps[1]))
					sql="select * from organization where parentid=codeitemid and codesetid='UN'";
				else
					sql="select * from organization where   codesetid='UN' and parentid in ('"+temps[1]+"')  and    parentid<>codeitemid";
			}
			else if("UM".equals(this.codesetid))
			{
				if("-1".equals(temps[1]))
					sql="select * from organization where parentid=codeitemid";
				else
					sql="select * from organization where   codesetid<>'@K' and parentid in ('"+temps[1]+"')  and    parentid<>codeitemid";
			}
			else 
			{
				if("-1".equals(temps[1]))
					sql="select * from codeitem where codesetid='"+this.codesetid+"' and parentid=codeitemid";
				else
					sql="select * from codeitem where codesetid='"+this.codesetid+"' and parentid in ('"+temps[1]+"') and parentid<>codeitemid";
			}
			
			String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
			sql+=" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date order by a0000";
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			rs=dao.search(sql);
			while(rs.next())
			{
				LazyDynaBean aBean=new LazyDynaBean();
				aBean.set("id",rs.getString("codesetid")+"/"+rs.getString("codeitemid"));
				aBean.set("name",rs.getString("codeitemdesc"));
				list.add(aBean);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return list;
	}

}
