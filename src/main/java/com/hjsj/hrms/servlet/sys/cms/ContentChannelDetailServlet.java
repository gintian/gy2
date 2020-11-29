package com.hjsj.hrms.servlet.sys.cms;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.RowSet;
import java.io.IOException;
import java.sql.Connection;

public class ContentChannelDetailServlet extends HttpServlet{
	
	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		doPost(arg0, arg1);
	}
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("GBK");
		StringBuffer treexml = new StringBuffer();
		int parent_id = Integer.parseInt((String)req.getParameter("parent_id"));
		try{
			treexml.append(loadChannelNodes(parent_id));
		}catch(Exception e){
			e.printStackTrace();
		}
		resp.setContentType("text/xml;charset=UTF-8");
		resp.getWriter().println(treexml.toString());  
	}
	private boolean isHaveChild(int parent_id,Connection conn)
	{
		boolean flag=false;
		StringBuffer buf=new StringBuffer();
		try
		{
			ContentDAO dao=new ContentDAO(conn);
			buf.append("select name from t_cms_channel where parent_id = ");
			buf.append(parent_id);
			RowSet rset=dao.search(buf.toString());
			if(rset.next())
				flag=true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return flag;
	}
	private boolean hasOnlyOne(int channel_id,Connection conn){
		boolean flag=false;
		StringBuffer buf = new StringBuffer();
		try{
			ContentDAO dao = new ContentDAO(conn);
			buf.append("select count(*) dd from t_cms_content where channel_id = ");
			buf.append(channel_id);
			RowSet rset = dao.search(buf.toString());
			if(rset.next()){
				if(rset.getInt("dd")==1){
					flag =true;
					}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return flag;
	}
	private int findContentidByChannelid(int channel_id,Connection conn){
		int content_id=0;
		StringBuffer buf = new StringBuffer();
		try{
			ContentDAO dao = new ContentDAO(conn);
			buf.append("select content_id from t_cms_content where channel_id = ");
			buf.append(channel_id);
			RowSet rs = dao.search(buf.toString());
			while(rs.next()){
				content_id = rs.getInt("content_id");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return content_id;
	}
	private String loadChannelNodes(int parent_id){
		StringBuffer channelNode = new StringBuffer();
		StringBuffer sql = new StringBuffer();
		/**
		 * parent_id=-1加载第一级节点
		 * 加载第一级节点规则：channel_id=parent_id
		 */
		if(parent_id==-1)
			sql.append("select * from t_cms_channel where parent_id = channel_id order by chl_sort");
		else
			sql.append("select * from t_cms_channel where parent_id <> channel_id and parent_id = "+parent_id+" order by chl_sort");
		Connection conn = null;
		RowSet rowSet = null;
		try{
			conn = (Connection)AdminDb.getConnection();
			ContentDAO  dao = new ContentDAO(conn);
			rowSet = dao.search(sql.toString());
			Element root = new Element("TreeNode");
			root.setAttribute("id","$$00");
			root.setAttribute("text","root");
			root.setAttribute("title","root");
			Document myDocument = new Document(root);
			while(rowSet.next()){
				Element child = new Element("TreeNode");
				child.setAttribute("id",rowSet.getString("channel_id"));
				child.setAttribute("text",rowSet.getString("name"));
				child.setAttribute("title",rowSet.getString("name"));
				if(isHaveChild(rowSet.getInt("channel_id"),conn)){
					child.setAttribute("xml","/system/channel/search_channel_servlet?parent_id="+rowSet.getInt("channel_id"));
				}
				child.setAttribute("target","mil_body");
				String num = rowSet.getInt("channel_id")+"";
				child.setAttribute("href","/sys/cms/queryChannel.do?b_query=query&encryptParam="+PubFunc.encrypt("channel_id="+num));
				/*
				if(hasOnlyOne(rowSet.getInt("channel_id"),conn)){
					child.setAttribute("href","/sys/cms/queryChannel.do?b_edit=edit&flag=0&content_id="+this.findContentidByChannelid(rowSet.getInt("channel_id"),conn)+"&channel_id="+rowSet.getInt("channel_id"));
				}else{
				    child.setAttribute("href","/sys/cms/queryChannel.do?b_query=query&channel_id="+rowSet.getInt("channel_id")+"&parent_id="+rowSet.getInt("parent_id"));
				}*/
				if(rowSet.getInt("state")==0)
					child.setAttribute("icon","/images/lock_co.gif");
				else
					child.setAttribute("icon","/images/lock_co_1.gif");
				root.addContent(child);
			}
			XMLOutputter outputter = new XMLOutputter();
			Format format=Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			channelNode.append(outputter.outputString(myDocument));
			
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				if(conn != null && (!conn.isClosed())){
					conn.close();
				}
			}catch(Exception ee){
				ee.printStackTrace();
			}
		}
		return channelNode.toString();
	}
}
