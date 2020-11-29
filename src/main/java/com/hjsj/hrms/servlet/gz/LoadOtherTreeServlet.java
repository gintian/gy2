package com.hjsj.hrms.servlet.gz;

import com.hjsj.hrms.businessobject.gz.gz_budget.budgeting.BudgetingBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
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
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.ArrayList;

public class LoadOtherTreeServlet extends HttpServlet{
	protected void doGet(HttpServletRequest req,HttpServletResponse resp) throws ServletException,IOException
	{
		doPost(req,resp);
	}
	protected void doPost(HttpServletRequest req,HttpServletResponse resp) throws ServletException,IOException
	{
		String modelflag=req.getParameter("modelflag");
		String flag=req.getParameter("flag");
		String budget_id=req.getParameter("budget_id");
		UserView userView=(UserView)req.getSession().getAttribute(WebConstant.userView);
		resp.setContentType("text/xml;charset=UTF-8");
		resp.getWriter().println(this.getXmlTree(flag, modelflag, userView,budget_id));  
	}
	/**
	 * 
	 * @param flag
	 * @param modelflag =1表示编制预算模块
	 * @param action
	 * @return
	 */
	private String getXmlTree(String flag,String modelflag,UserView userView,String budget_id){
		String str="";
		try{
			Element root = new Element("TreeNode");
			root.setAttribute("id","$$00");
			root.setAttribute("text","root");
			root.setAttribute("title","root");
			Document myDocument = new Document(root);
			if("1".equals(modelflag)){//编制
				this.getBudgetingTree(flag, userView, root);
			}
			else if("2".equals(modelflag)){//审批
				this.getBudgetingTree2(flag, userView, root);
			}
			else if("3".equals(modelflag)){//执行率
				if (budget_id ==null) budget_id="0";
				this.getBudgetingTree3(flag, userView, root,budget_id);
			}
			else if("4".equals(modelflag)){//历史
				if (budget_id ==null) budget_id="0";
				this.getBudgetingTree4(flag, userView, root,budget_id);
			}
			XMLOutputter outputter = new XMLOutputter();
			Format format=Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			str=outputter.outputString(myDocument);
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return str;
	}
	/**
	 * 执行率分析
	 * @param flag
	 * @param userView
	 * @param root
	 */
	private void getBudgetingTree3(String flag,UserView userView,Element root,String budget_id ){
		RowSet rs = null;
		Connection con = null;
		try{
			con = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(con);
			StringBuffer buf = new StringBuffer();
			flag=SafeCode.decode(flag);
			if(flag!=null&&!"".equals(flag)){
				buf.append(" select tab_id,tab_name,tab_type,codesetid from gz_budget_tab ");
				buf.append(" where tab_id in (");
				buf.append(" select tab_id from gz_budget_exec where budget_id=");
				buf.append(budget_id);
				buf.append(")");
				buf.append(" and budgetGroup='"+flag+"' and tab_type=4  order by seq");
			}else{
				buf.append(" select tab_id,tab_name,tab_type,codesetid from gz_budget_tab  ");
				buf.append(" where tab_id in (");
				buf.append(" select tab_id from gz_budget_exec where budget_id=");
				buf.append(budget_id);
				buf.append(")");
				buf.append(" and (("+Sql_switcher.length(Sql_switcher.isnull("budgetGroup", "''"))+"=0 and tab_type=4) or tab_type=3) order by tab_type,seq");
			}
			rs = dao.search(buf.toString());
			while(rs.next())
			{
				Element child = new Element("TreeNode");
				child.setAttribute("id",rs.getString("tab_id"));
				child.setAttribute("text",rs.getString("tab_name"));
				child.setAttribute("title",rs.getString("tab_name"));
				child.setAttribute("target","mil_body");
				child.setAttribute("href","/gz/gz_budget/budget_execrate.do?b_query=query&tab_id="+rs.getString("tab_id"));
				child.setAttribute("icon","/images/table.gif");
				root.addContent(child);
			}
			if(flag==null|| "".equals(flag)){
				ArrayList list = this.getGroupExec(userView, con);
				for(int i=0;i<list.size();i++){
					LazyDynaBean bean = (LazyDynaBean)list.get(i);
					Element child = new Element("TreeNode");
					child.setAttribute("id",(String)bean.get("name"));
					child.setAttribute("text",(String)bean.get("name"));
					child.setAttribute("title",(String)bean.get("name"));
				    child.setAttribute("xml","/gz/LoadOtherTreeServlet?modelflag=3&budget_id="+budget_id+"&flag="+URLEncoder.encode((String)bean.get("encodename")));
					child.setAttribute("icon","/images/open.png");
					root.addContent(child);
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
				if(con!=null)
					con.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 预算历史
	 * @param flag
	 * @param userView
	 * @param root
	 */
	private void getBudgetingTree4(String flag,UserView userView,Element root,String budget_id ){
		RowSet rs = null;
		Connection con = null;
		try{
			con = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(con);
			StringBuffer buf = new StringBuffer();
			flag=SafeCode.decode(flag);
			if(flag!=null&&!"".equals(flag)){
				buf.append(" select tab_id,tab_name,tab_type,codesetid from gz_budget_tab ");
				buf.append(" where tab_id in (");
				buf.append(" select tab_id from gz_budget_exec where budget_id=");
				buf.append(budget_id);
				buf.append(")");
				buf.append(" and budgetGroup='"+flag+"' and tab_type=4  order by seq");
			}else{
				buf.append(" select tab_id,tab_name,tab_type,codesetid from gz_budget_tab  ");
				buf.append(" where tab_id in (");
				buf.append(" select tab_id from gz_budget_exec where budget_id=");
				buf.append(budget_id);
				buf.append(")");
				buf.append(" and (("+Sql_switcher.length(Sql_switcher.isnull("budgetGroup", "''"))+"=0 and tab_type=4) or tab_type=3) order by tab_type,seq");
			}
			rs = dao.search(buf.toString());
			while(rs.next())
			{
				Element child = new Element("TreeNode");
				child.setAttribute("id",rs.getString("tab_id"));
				child.setAttribute("text",rs.getString("tab_name"));
				child.setAttribute("title",rs.getString("tab_name"));
				child.setAttribute("target","mil_body");
				child.setAttribute("href","/gz/gz_budget/budget_examination.do?b_query=query&tab_id="+rs.getString("tab_id"));
				child.setAttribute("icon","/images/table.gif");
				root.addContent(child);
			}
			if(flag==null|| "".equals(flag)){
				ArrayList list = this.getGroupOne(userView, con, budget_id);
				for(int i=0;i<list.size();i++){
					LazyDynaBean bean = (LazyDynaBean)list.get(i);
					Element child = new Element("TreeNode");
					child.setAttribute("id",(String)bean.get("name"));
					child.setAttribute("text",(String)bean.get("name"));
					child.setAttribute("title",(String)bean.get("name"));
				    child.setAttribute("xml","/gz/LoadOtherTreeServlet?modelflag=4&budget_id="+budget_id+"&flag="+URLEncoder.encode((String)bean.get("encodename")));
					child.setAttribute("icon","/images/open.png");
					root.addContent(child);
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
				if(con!=null)
					con.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 预算审批
	 * @param flag
	 * @param userView
	 * @param root
	 */
	private void getBudgetingTree2(String flag,UserView userView,Element root){
		RowSet rs = null;
		Connection con = null;
		try{
			con = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(con);
			StringBuffer buf = new StringBuffer();
			flag=SafeCode.decode(flag);
			if(flag!=null&&!"".equals(flag)){
				buf.append(" select tab_id,tab_name,tab_type,codesetid from gz_budget_tab ");
				buf.append(" where tab_id in (");
				buf.append(" select tab_id from gz_budget_exec where budget_id=");
				buf.append("(select max(budget_id) budget_id from gz_budget_index where (budgetType=1 or budgetType=2 or budgetType=3))");
				buf.append(")");
				buf.append(" and budgetGroup='"+flag+"' and validFlag=1  and bpFlag=1 and tab_type=4  order by seq");
			}else{
				buf.append(" select tab_id,tab_name,tab_type,codesetid from gz_budget_tab  ");
				buf.append(" where tab_id in (");
				buf.append(" select tab_id from gz_budget_exec where budget_id=");
				buf.append("(select max(budget_id) budget_id from gz_budget_index where (budgetType=1 or budgetType=2 or budgetType=3))");
				buf.append(")");
				buf.append(" and (("+Sql_switcher.length(Sql_switcher.isnull("budgetGroup", "''"))+"=0 and tab_type=4) or tab_type=3) and validFlag=1 and bpFlag=1 order by tab_type,seq");
			}
			rs = dao.search(buf.toString());
			while(rs.next())
			{
				Element child = new Element("TreeNode");
				child.setAttribute("id",rs.getString("tab_id"));
				child.setAttribute("text",rs.getString("tab_name"));
				child.setAttribute("title",rs.getString("tab_name"));
				child.setAttribute("target","mil_body");
				child.setAttribute("href","/gz/gz_budget/budget_examination.do?b_query=query&tab_id="+rs.getString("tab_id"));
				child.setAttribute("icon","/images/table.gif");
				root.addContent(child);
			}
			if(flag==null|| "".equals(flag)){
				ArrayList list = this.getGroup(userView, con);
				for(int i=0;i<list.size();i++){
					LazyDynaBean bean = (LazyDynaBean)list.get(i);
					Element child = new Element("TreeNode");
					child.setAttribute("id",(String)bean.get("name"));
					child.setAttribute("text",(String)bean.get("name"));
					child.setAttribute("title",(String)bean.get("name"));
				    child.setAttribute("xml","/gz/LoadOtherTreeServlet?modelflag=2&flag="+ URLEncoder.encode((String)bean.get("encodename")));
					child.setAttribute("icon","/images/open.png");
					root.addContent(child);
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
				if(con!=null)
					con.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	
	
	/**
	 * 获得action...xml
	 * @param flag
	 * @param userView
	 * @param root
	 */
	
	
	private void getBudgetingTree(String flag,UserView userView,Element root){
		RowSet rs = null;
		RowSet mc = null;
		Connection con = null;
		try{
			con = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(con);
			StringBuffer buf = new StringBuffer();
			BudgetingBo bo = new BudgetingBo(con,userView,true,"");
			StringBuffer ssql = new StringBuffer();
			String b0110=bo.getUnitcode();
			flag=SafeCode.decode(flag);
			if(flag!=null&&!"".equals(flag)){
				buf.append(" select tab_id,tab_name,tab_type,codesetid from gz_budget_tab ");
				buf.append(" where tab_id in (");
				buf.append(" select tab_id from gz_budget_exec where budget_id=");
				buf.append("(select max(budget_id) budget_id from gz_budget_index where (budgetType=1 or budgetType=2 or budgetType=3))");
				buf.append(" and b0110='"+b0110+"'");
				buf.append(")");
				buf.append(" and budgetGroup='"+flag+"' and validFlag=1 and tab_type=4  order by seq");
			}else{
				buf.append(" select tab_id,tab_name,tab_type,codesetid from gz_budget_tab  ");
				buf.append(" where tab_id in (");
				buf.append(" select tab_id from gz_budget_exec where budget_id=");
				buf.append("(select max(budget_id) budget_id from gz_budget_index where (budgetType=1 or budgetType=2 or budgetType=3))");
				buf.append(" and b0110='"+b0110+"'");
				buf.append(")");
				buf.append(" and (("+Sql_switcher.length(Sql_switcher.isnull("budgetGroup", "''"))+"=0 and tab_type=4) or tab_type=3) and validFlag=1  order by tab_type,seq");
				
				ssql.append("select tab_id,tab_name,tab_type from gz_budget_tab ");
				ssql.append(" where tab_id in (");
				ssql.append(" select tab_id from gz_budget_exec where budget_id=");
				ssql.append("(select max(budget_id) budget_id from gz_budget_index where (budgetType=1 or budgetType=2 or budgetType=3))");
				ssql.append(" and b0110='"+b0110+"'");
				ssql.append(")");
				ssql.append("and tab_type=2 ");
			}
			if("".equals(flag)||flag==null){
				Element child1 = new Element("TreeNode");//预算参数表（节点）
				child1.setAttribute("id","params"+b0110);
				child1.setAttribute("text",ResourceFactory.getProperty("gz.budget.budgeting.params.table"));
				child1.setAttribute("title",ResourceFactory.getProperty("gz.budget.budgeting.params.table"));
				child1.setAttribute("target","mil_body");
				child1.setAttribute("href","/gz/gz_budget/budgeting/budgeting_table.do?b_params_ze=int&b0110="+b0110+"&canshu=params");
				child1.setAttribute("icon","/images/table.gif");
				root.addContent(child1);
				
				Element child0 = new Element("TreeNode");//预算总额表（节点）
				child0.setAttribute("id","zonge"+b0110);
				child0.setAttribute("text",ResourceFactory.getProperty("gz.budget.budgeting.ze.table"));
				child0.setAttribute("title",ResourceFactory.getProperty("gz.budget.budgeting.ze.table"));
				child0.setAttribute("target","mil_body");
				child0.setAttribute("href","/gz/gz_budget/budgeting/budgeting_table.do?b_params_ze=link&b0110="+b0110+"&canshu=zonge");
				child0.setAttribute("icon","/images/table.gif");
				root.addContent(child0);
				
				mc=dao.search(ssql.toString());
				if(mc.next())//员工名册表（节点）
				{
					Element child = new Element("TreeNode");
					child.setAttribute("id",mc.getString("tab_id"));
					child.setAttribute("text",mc.getString("tab_name"));
					child.setAttribute("title",mc.getString("tab_name"));
					child.setAttribute("target","mil_body");
					child.setAttribute("href","/gz/gz_budget/budgeting/budgeting_table.do?b_tree=int&tab_id="+mc.getString("tab_id"));
					child.setAttribute("icon","/images/table.gif");
					root.addContent(child);
				}
			}
	

			rs = dao.search(buf.toString());
			while(rs.next())  //用工计划/其他计划表
			{
				Element child = new Element("TreeNode");
				child.setAttribute("id",rs.getString("tab_id"));
				child.setAttribute("text",rs.getString("tab_name"));
				child.setAttribute("title",rs.getString("tab_name"));
				child.setAttribute("target","mil_body");
				child.setAttribute("href","/gz/gz_budget/budgeting/budgeting_table.do?b_open=int&tab_id="+rs.getString("tab_id"));
				child.setAttribute("icon","/images/table.gif");
				root.addContent(child);
			}
			
			if("".equals(flag)||flag==null){
				ArrayList list = this.getGroup(userView, con);
				for(int i=0;i<list.size();i++){
					LazyDynaBean bean = (LazyDynaBean)list.get(i);
					Element child = new Element("TreeNode");
					child.setAttribute("id","-1");
					child.setAttribute("text",(String)bean.get("name"));
					child.setAttribute("title",(String)bean.get("name"));
				    child.setAttribute("xml","/gz/LoadOtherTreeServlet?modelflag=1&flag="+URLEncoder.encode((String)bean.get("encodename")));
					child.setAttribute("icon","/images/open.png");
					root.addContent(child);
			}

		}

			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
				if(con!=null)
					con.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	private ArrayList getGroup(UserView userView,Connection conn){
		RowSet rs = null;
		BudgetingBo bo = new BudgetingBo(conn,userView,true,"");
		String b0110=bo.getUnitcode();
		ArrayList list= new ArrayList();
		try{
			StringBuffer buf = new StringBuffer();
			buf.append(" select budgetGroup from gz_budget_tab ");
			buf.append(" where tab_id in (");
			buf.append(" select tab_id from gz_budget_exec where budget_id=");
			buf.append("(select max(budget_id) budget_id from gz_budget_index where (budgetType=1 or budgetType=2 or budgetType=3))");
			buf.append(" and b0110='"+b0110+"'");
			buf.append(") and tab_type=4 ");
			buf.append(" and "+Sql_switcher.length(Sql_switcher.isnull("budgetGroup", "''"))+">0 and validFlag=1 group by  budgetGroup");
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(buf.toString());
			while(rs.next()){
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("name",rs.getString("budgetGroup"));
				bean.set("encodename",SafeCode.encode(rs.getString("budgetGroup")));
				list.add(bean);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return list;
	}

	// 执行率的组
	private ArrayList getGroupExec(UserView userView,Connection conn){
		RowSet rs = null;
		ArrayList list= new ArrayList();
		try{
			StringBuffer buf = new StringBuffer();
			buf.append(" select budgetGroup from gz_budget_tab ");
			buf.append(" where tab_id in (");
			buf.append(" select tab_id from gz_budget_exec where budget_id in (select budget_id from gz_budget_index where budgetType=4)");
			buf.append(") and tab_type=4 ");
			buf.append(" and "+Sql_switcher.length(Sql_switcher.isnull("budgetGroup", "''"))+">0 and validFlag=1 group by  budgetGroup");
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(buf.toString());
			while(rs.next()){
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("name",rs.getString("budgetGroup"));
				bean.set("encodename",SafeCode.encode(rs.getString("budgetGroup")));
				list.add(bean);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return list;
	}
	
	// 预算历史的组
	private ArrayList getGroupOne(UserView userView,Connection conn, String budget_id){
		RowSet rs = null;
		BudgetingBo bo = new BudgetingBo(conn,userView,true,"");
		String b0110=bo.getUnitcode();
		ArrayList list= new ArrayList();
		try{
			StringBuffer buf = new StringBuffer();
			buf.append(" select budgetGroup from gz_budget_tab ");
			buf.append(" where tab_id in (");
			buf.append(" select tab_id from gz_budget_exec where budget_id=");
			buf.append(budget_id);
			buf.append(" and b0110='"+b0110+"'");
			buf.append(") and tab_type=4 ");
			buf.append(" and "+Sql_switcher.length(Sql_switcher.isnull("budgetGroup", "''"))+">0 and validFlag=1 group by  budgetGroup");
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(buf.toString());
			while(rs.next()){
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("name",rs.getString("budgetGroup"));
				bean.set("encodename",SafeCode.encode(rs.getString("budgetGroup")));
				list.add(bean);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return list;
	}
}
