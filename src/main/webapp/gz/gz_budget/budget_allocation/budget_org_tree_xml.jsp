<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java"%>

<%@ page import="com.hrms.struts.exception.*"%>
<%@ page import="com.hjsj.hrms.businessobject.gz.gz_budget.budget_allocation.BudgetAllocBo"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.frame.utility.AdminDb"%>
<%@ page import="java.sql.*"%>
<%
	Connection conn=null;
    response.setContentType("text/xml;charset=UTF-8");
	try
	{
	    conn = AdminDb.getConnection();
	    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
		BudgetAllocBo bo = new BudgetAllocBo(conn, userView);
		String topunit = request.getParameter("topunit");
		//create xtree.js treeview.
		String xmlc=bo.genOrgXml(topunit,"/gz/gz_budget/budget_allocation/budget_allocation.do?b_query=query", "mil_body",null);  
	   	response.getWriter().write(xmlc);
	   	response.getWriter().close();
	}
	catch(GeneralException e)
	{
      	e.printStackTrace();
	}finally
	{
	  if(conn!=null)
	    conn.close();	
	}
%>
