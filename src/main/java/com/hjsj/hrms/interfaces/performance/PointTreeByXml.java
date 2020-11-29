package com.hjsj.hrms.interfaces.performance;

import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
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
import java.util.Hashtable;
import java.util.Iterator;
public class PointTreeByXml {
	
	String flag;	  // 1：项目  2：指标
	String id;
	String isItem;    //是否包含项目  0:不包含  1：包含
	String template_id;
	
	public PointTreeByXml() {
	}
	
	public PointTreeByXml(String flag,String id,String isItem,String template_id) {
		this.flag=flag;
		this.id=id;
		this.isItem=isItem;
		this.template_id=template_id;
	 }

	
	public String getTemplateid(String planid)
	{
		String templateid="";
		ResultSet rs = null;	
		Connection conn=null;
		ArrayList list=new ArrayList();
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search("select template_id from per_plan where plan_id="+planid);
			if(rs.next())
				templateid=rs.getString("template_id");
		} catch (Exception e) {
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
		return templateid;
	}
	
	/**
	 * 判断 归档计划是否包含 项目
	 * @param planid
	 * @return 0:不包含  1：包含
	 */
	public String getIsItem(String planid)
	{
		String a_isItem="1";
		Connection conn=null;
		try
		{
			conn = AdminDb.getConnection();
			LoadXml loadxml=new LoadXml(conn,planid);
			Hashtable htxml=new Hashtable();		
			htxml=loadxml.getDegreeWhole();
			
			//a_isItem=(String)htxml.get("a_isItem");
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return a_isItem;
	}
	
	
	
	
	public String outPutOrgXml() throws GeneralException {

		// 生成的XML文件
		StringBuffer xmls = new StringBuffer();
		// 创建xml文件的根元素
		Element root = new Element("TreeNode");
		// 设置根元素属性
		root.setAttribute("id", "00");
		root.setAttribute("text", "root");
		root.setAttribute("title", "organization");
		// 创建xml文档自身
		Document myDocument = new Document(root);
		// 设置跳转字符串
		String theaction = "";

		ArrayList list = getInfoList(this.id);
		for (Iterator t = list.iterator(); t.hasNext();) {
			LazyDynaBean abean = (LazyDynaBean) t.next();

			// 创建子元素
			Element child = new Element("TreeNode");
			// 设置子元素属性
			String codeitemid = (String) abean.get("codeitemid");
			String codeitemdesc = (String) abean.get("codeitemdesc");
			String child_id=(String)abean.get("child_id");
			
			child.setAttribute("id", this.flag+"`"+codeitemid);
			child.setAttribute("text", codeitemdesc);
			child.setAttribute("title", codeitemdesc);
			child.setAttribute("href", theaction);
			child.setAttribute("target", "mil_body"); 
			if("1".equals(this.flag))
				child.setAttribute("icon","/images/unit.gif");
			else
				child.setAttribute("icon","/images/admin.gif");
				
			String a_xml="point_tree.jsp?isItem="+this.isItem+"&template_id="+this.template_id+"&id="+codeitemid;
	        if("0".equals(child_id))
	        	a_xml+="&flag=2";
	        else
	        	a_xml+="&flag=1";;
	        if("1".equals(this.flag))
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
	
	
	
	public ArrayList getInfoList(String parentid)
	{
        // DB相关
		ResultSet rs = null;	
		Connection conn=null;
		ArrayList list=new ArrayList();
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			// SQL语句
			StringBuffer strsql = new StringBuffer("");
			if("1".equals(this.flag))  //项目
			{
				strsql.append("select item_id,itemdesc,child_id from per_template_item where  template_id='"+this.template_id+"' ");
				if("0".equals(parentid))
					strsql.append(" and parent_id is null");
				else
					strsql.append(" and parent_id="+parentid);
			}
			else						//指标
			{
				strsql.append("select pp.point_id,pp.pointname from per_template_point ptp,per_point pp where ptp.point_id=pp.point_id and item_id="+parentid);
			}
			
		
			rs = dao.search(strsql.toString());
			while (rs.next()) {
				LazyDynaBean lazyDynaBean=new LazyDynaBean();
				if("1".equals(this.flag))  //项目
				{
					lazyDynaBean.set("codeitemid",rs.getString("item_id"));
					lazyDynaBean.set("codeitemdesc",rs.getString("itemdesc"));
				}
				else
				{
					lazyDynaBean.set("codeitemid",rs.getString("point_id"));
					lazyDynaBean.set("codeitemdesc",rs.getString("pointname"));
				}
				
				if("2".equals(this.flag))
					lazyDynaBean.set("child_id","0");
				else if(rs.getString("child_id")!=null)
					lazyDynaBean.set("child_id","1");
				else
					lazyDynaBean.set("child_id","0");
				list.add(lazyDynaBean);
			}
			rs.close();
				
		} catch (Exception e) {
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