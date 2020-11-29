/**
 * 
 */
package com.hjsj.hrms.interfaces.general.deci.browser;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.exception.GeneralException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 * <p>Title:SingleFieldByXML</p>
 * <p>Description:单个指标分析树图生成</p>
 * <p>Company:hjsj</p>
 * <p>create time:Aug 29, 2006:11:48:24 AM</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class SingleFieldByXML {

	private String params;
	private String actionName;
	private String target;
	
	
	/**
	 * 默认构造器
	 */
	public SingleFieldByXML() {
		super();
	}
	
	/**
	 * 
	 * @param params  
	 * @param actionName
	 * @param target
	 */
	public SingleFieldByXML(String params , String actionName,String target) {
		this.params = params;
		this.actionName = actionName;
		this.target = target;
	}
	
	public String outPutSingleFieldXml() throws GeneralException{
		//生成的XML文件
		StringBuffer xmls = new StringBuffer();
		//SQL语句//填报单位信息表
		StringBuffer strsql = new StringBuffer();

		//DB相关
		ResultSet rs = null;		
		Connection conn;
		conn = AdminDb.getConnection();
		//创建xml文件的根元素
		Element root = new Element("TreeNode");
		//设置根元素属性
		root.setAttribute("id", "00");
		root.setAttribute("text", "field");
		root.setAttribute("title", "field");

		//创建xml文档自身
		Document myDocument = new Document(root);

		//设置跳转字符串
		String theaction = null;

		try {
			
			//生成SQL语句
			if("-1".equals(this.params)){
				strsql.append(" select * from ds_key_factortype ");
			}else{
				strsql.append(" select * from ds_key_factor where typeid = '");
				strsql.append(this.params);
				strsql.append("'");
			}
			
			//System.out.println("params=" + this.params);
			
			//执行SQL
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(strsql.toString());

			while (rs.next()) {
				//创建子元素
				Element child = new Element("TreeNode");				
				if("-1".equals(this.params)){
					//设置子元素属性
					String typeid =  rs.getString("typeid");
					String name =  rs.getString("name");					
					child.setAttribute("id", typeid);
					child.setAttribute("text", name);
					child.setAttribute("title", name);
				/*	theaction = this.actionName + "?b_query=link&code="+ typeid;									
					child.setAttribute("href", theaction);*/
					child.setAttribute("href", "");
					
					child.setAttribute("target", this.target);
					child.setAttribute("icon", "/images/dept.gif");
					//child.setAttribute("icon", "/images/unit.gif");
					child.setAttribute("xml" ,"field_tree.jsp?params="+typeid);
				}else{
					//设置子元素属性
					String factorid =  rs.getString("factorid");
					String name =  rs.getString("name");					
					child.setAttribute("id", factorid);
					child.setAttribute("text", name);
					child.setAttribute("title", name);
					theaction = this.actionName + "?b_search=link&factorid="+ factorid;									
					child.setAttribute("href", theaction);
					child.setAttribute("target", this.target);
					child.setAttribute("icon", "/images/table.gif");
					//child.setAttribute("icon", "/images/unit.gif");
					/*child.setAttribute("xml" ,"field_tree.jsp?params="+factorid);*/
					child.setAttribute("xml" ,"");
				}
				
				//将子元素作为内容添加到根元素
				root.addContent(child);
			}

			XMLOutputter outputter = new XMLOutputter();

			//格式化输出类
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);

			//将生成的XML文件作为字符串形式
			xmls.append(outputter.outputString(myDocument));
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
			PubFunc.closeDbObj(conn);
		} 
		///System.out.println(xmls.toString());
		return xmls.toString();
	}
}
