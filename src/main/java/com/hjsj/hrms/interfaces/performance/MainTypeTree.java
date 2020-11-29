package com.hjsj.hrms.interfaces.performance;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * <p>Title:MainTypeTree.java</p>
 * <p>Description:绩效考核关系手工选人机构树</p>
 * <p>Company:hjsj</p>
 * <p>create time:2010-07-17 10:05:18</p>
 * @author JinChunhai
 * @version 1.0
 */

public class MainTypeTree
{
    private String plan_id;

    public MainTypeTree(String planID)
    {

    	this.plan_id = planID;
    }

    public MainTypeTree()  {  }
    /**
     * flag=0:考核实施/指定考核主体
     * flag=1:考核关系/指定考核主体
     * flag=2:考核实施/考核对象/设置动态项目权重(分值)
     */
    public String outPutXmlStr(String flag)
    {
    	
		StringBuffer xml = new StringBuffer();
		try
		{
		    // 创建xml文件的根元素
		    Element root = new Element("TreeNode");
		    // 设置根元素属性
		    root.setAttribute("id", "");
		    root.setAttribute("text", "root");
		    root.setAttribute("title", "organization");
		    root.setAttribute("target", "mil_body");
		    // 创建xml文档自身
		    Document myDocument = new Document(root);
	
		    ArrayList list = getChildList(flag);
	
		    for (Iterator t = list.iterator(); t.hasNext();)
		    {
				LazyDynaBean bean = (LazyDynaBean) t.next();
				Element child = new Element("TreeNode");
				String id = (String) bean.get("id");
				String name = (String) bean.get("name");
		
				child.setAttribute("id", id);
				child.setAttribute("text", name);
				child.setAttribute("title", name);
				if("0".equals(flag)|| "1".equals(flag))
					child.setAttribute("href", "mainBodyList.do?b_query=link&code="+id);
				else if("2".equals(flag))
					child.setAttribute("href", "/performance/implement/kh_object/dynaitem.do?b_query=link&objTypeId="+id);
		
				child.setAttribute("target", "mil_body");
				child.setAttribute("icon", "/images/table.gif");
				// 将子元素作为内容添加到根元素
				root.addContent(child);
		    }
	
		    XMLOutputter outputter = new XMLOutputter();
		    // 格式化输出类
		    Format format = Format.getPrettyFormat();
		    format.setEncoding("UTF-8");
		    outputter.setFormat(format);
	
		    // 将生成的XML文件作为字符串形式
		    xml.append(outputter.outputString(myDocument));
	
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return xml.toString();
    }
    public String outPutGenRelationXmlStr()
    {
    	
		StringBuffer xml = new StringBuffer();
		try
		{
		    // 创建xml文件的根元素
		    Element root = new Element("TreeNode");
		    // 设置根元素属性
		    root.setAttribute("id", "");
		    root.setAttribute("text", "root");
		    root.setAttribute("title", "organization");
		    root.setAttribute("target", "mil_body");
		    // 创建xml文档自身
		    Document myDocument = new Document(root);
	
		    ArrayList list = new ArrayList();
		    LazyDynaBean bean = new LazyDynaBean();
			bean.set("id", "9");
			bean.set("name", "直接领导");
			list.add(bean);
			bean = new LazyDynaBean();
			bean.set("id", "10");
			bean.set("name", "主管领导");
			list.add(bean);
			bean = new LazyDynaBean();
			bean.set("id", "11");
			bean.set("name", "第三级领导");
			list.add(bean);
			bean = new LazyDynaBean();
			bean.set("id", "12");
			bean.set("name", "第四级领导");
			list.add(bean);
		    for (Iterator t = list.iterator(); t.hasNext();)
		    {
				 bean = (LazyDynaBean) t.next();
				Element child = new Element("TreeNode");
				String id = (String) bean.get("id");
				String name = (String) bean.get("name");
		
				child.setAttribute("id", id);
				child.setAttribute("text", name);
				child.setAttribute("title", name);
					child.setAttribute("href", "relationmainbodylist.do?b_query=link&code="+id);
		
				child.setAttribute("target", "mil_body");
				child.setAttribute("icon", "/images/table.gif");
				// 将子元素作为内容添加到根元素
				root.addContent(child);
		    }
	
		    XMLOutputter outputter = new XMLOutputter();
		    // 格式化输出类
		    Format format = Format.getPrettyFormat();
		    format.setEncoding("UTF-8");
		    outputter.setFormat(format);
	
		    // 将生成的XML文件作为字符串形式
		    xml.append(outputter.outputString(myDocument));
	
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return xml.toString();
    }
    private ArrayList getChildList(String flag)
    {

		ArrayList list = new ArrayList();
		ResultSet rs = null;
		Connection conn = null;
	
		String strSql ="";
		if("0".equals(flag))
		    strSql = "Select P.body_id, B.name FROM per_plan_body P, per_mainbodyset B WHERE P.body_id = B.body_id and plan_id =" + this.plan_id + " and P.body_id<>5 ORDER BY b.seq ";
		else if ("1".equals(flag))
		    strSql="select * from per_mainbodyset where status=1 and (body_type is null or body_type = 0) and body_id not in (5,-1) ORDER BY seq ";
		else if("2".equals(flag))
			 strSql="select * from per_mainbodyset where status=1 and body_type = 1 and body_id in (select distinct body_id from  per_object where plan_id="+this.plan_id+") ORDER BY seq ";
		try
		{
	
		    conn = AdminDb.getConnection();
		    ContentDAO dao = new ContentDAO(conn);
		    rs = dao.search(strSql);
		    while (rs.next())
		    {
				LazyDynaBean bean = new LazyDynaBean();
		
				bean.set("id", rs.getString("body_id"));
				bean.set("name", rs.getString("name"));
		
				list.add(bean);
		    }
	
		} catch (Exception e)
		{
		    e.printStackTrace();
		}finally
		{
		    try
		    {
				if(rs!=null)
					rs.close();
				if(conn!=null)
					conn.close();
		    } catch (SQLException e)
		    {
		    	e.printStackTrace();
		    }
		}
	
		return list;
    }

}