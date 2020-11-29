package com.hjsj.hrms.interfaces.performance;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
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
import java.util.Iterator;

/**
 * <p>Title:AchivementTaskTree.java</p>
 * <p>Description>:业绩任务书机构树</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Dec 18, 2010 09:15:57 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class AchivementTaskTree 
{
	String opt="0";
	String codeid="0";
	String number="0";
	
	public AchivementTaskTree(String a_opt,String a_codeid,String number)
	{
		this.opt=a_opt;
		this.codeid=a_codeid;
		this.number=number;
	}
	
	public String outPut_Xml() throws GeneralException 
	{
		
//		 生成的XML文件
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

		ArrayList list =getInfoList();
		for (Iterator t = list.iterator(); t.hasNext();) 
		{
			LazyDynaBean abean = (LazyDynaBean) t.next();

			// 创建子元素
			Element child = new Element("TreeNode");
			// 设置子元素属性
			String codeitemid = (String) abean.get("codeitemid");
			String codeitemdesc = (String) abean.get("codeitemdesc");
			String flag=(String)abean.get("flag");
			String object_type=(String)abean.get("object_type");
			String _opt=(String)abean.get("_opt");
			String num=(String)abean.get("numx");
			if("1".equals(flag))
				child.setAttribute("icon","/images/img_l.gif");
			else
				child.setAttribute("icon","/images/admin.gif");
			child.setAttribute("id",codeitemid);
			child.setAttribute("text", codeitemdesc);
			child.setAttribute("title", codeitemdesc);
			if("1".equals(flag))
			{
				if("0".equals(this.number))
					child.setAttribute("href","/performance/achivement/achivementTask.do?b_search=query&encryptParam="+PubFunc.encrypt("onePage=1&target_id="+codeitemid));
				else 
					child.setAttribute("href","/performance/achivement/achivementTask.do?b_querys=link&encryptParam="+PubFunc.encrypt("hjsoft=hj&paramd=0&object_type="+object_type+"&target_id="+codeitemid));
			}
			else
				child.setAttribute("href","");
			child.setAttribute("target", "mil_body"); 
			
			String a_xml="/performance/achivement/achivementTask/achivement_task_tree.jsp?opt="+_opt+"&codeid="+codeitemid+"&object_type="+object_type+"&num1="+num;
            if(!"1".equals(flag))
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

	
	
	public ArrayList getInfoList()
	{
        // DB相关
		ResultSet rs = null;	
		Connection conn=null;
		ArrayList list=new ArrayList();
		try {
			
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			if("0".equals(this.opt))
			{
				LazyDynaBean a_bean=new LazyDynaBean();
				a_bean.set("codeitemid", "1");  // 1\2  团队\人员
				a_bean.set("codeitemdesc",ResourceFactory.getProperty("jx.khplan.team"));
				a_bean.set("_opt","1");
				if("0".equals(this.number))
					a_bean.set("numx","0");
				else
					a_bean.set("numx","1");
				a_bean.set("flag","0");
				list.add(a_bean);
				a_bean=new LazyDynaBean();
				a_bean.set("codeitemid", "2");  // 1\2  团队\人员
				a_bean.set("codeitemdesc",ResourceFactory.getProperty("jx.jifen.person"));
				a_bean.set("_opt","1");
				if("0".equals(this.number))
					a_bean.set("numx","0");
				else
					a_bean.set("numx","1");
				a_bean.set("flag","0");
				list.add(a_bean);
			}
			else if("1".equals(this.opt))
			{
				rs=dao.search("select distinct theyear from per_target_list where object_type='"+this.codeid+"'");
				LazyDynaBean a_bean=new LazyDynaBean();
				while(rs.next())
				{
					a_bean=new LazyDynaBean();
					String theyear=rs.getString("theyear");
					a_bean.set("codeitemid", theyear);  // 1\2  团队\人员
					a_bean.set("codeitemdesc",theyear+ResourceFactory.getProperty("jx.khplan.yeardu"));
					a_bean.set("flag","0");
					if("0".equals(this.number))
						a_bean.set("numx","0");
					else
						a_bean.set("numx","1");
					if("1".equals(this.codeid))
						a_bean.set("_opt","3");
					else
						a_bean.set("_opt","4");
					list.add(a_bean);
				}
			}
			else
			{
				String sql="";
				if("3".equals(this.opt))  //团队
				{
					sql="select * from per_target_list where object_type='1' ";
					sql+=" and theyear='"+this.codeid+"'";
					rs=dao.search(sql);
					LazyDynaBean a_bean=new LazyDynaBean();
					while(rs.next())
					{
						a_bean=new LazyDynaBean();
						String theyear=rs.getString("theyear");
						a_bean.set("codeitemid", rs.getString("target_id"));
						a_bean.set("codeitemdesc",rs.getString("name"));
						a_bean.set("flag","1");
						a_bean.set("object_type","1");
						a_bean.set("_opt","5");
						if("0".equals(this.number))
							a_bean.set("numx","0");
						else
							a_bean.set("numx","1");
						list.add(a_bean);
					}
				}
					
				else if("4".equals(this.opt)) //个人
				{
					sql="select * from per_target_list where object_type='2' ";
					sql+=" and theyear='"+this.codeid+"'";
					rs=dao.search(sql);
					LazyDynaBean a_bean=new LazyDynaBean();
					while(rs.next())
					{
						a_bean=new LazyDynaBean();
						String theyear=rs.getString("theyear");
						a_bean.set("codeitemid", rs.getString("target_id"));
						a_bean.set("codeitemdesc",rs.getString("name"));
						a_bean.set("flag","1");
						a_bean.set("object_type","2");
						a_bean.set("_opt","5");
						if("0".equals(this.number))
							a_bean.set("numx","0");
						else
							a_bean.set("numx","1");
						list.add(a_bean);
					}
				}				
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
