<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java"%>

<%@ page import="com.hrms.struts.exception.*"%>
<%@ page import="com.hjsj.hrms.businessobject.gz.gz_budget.budget_examination.BudgetExamBo,
com.hjsj.hrms.actionform.gz.gz_budget.BudgetExecRateForm"%>
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
		BudgetExamBo bo = new BudgetExamBo(conn, userView);
		String topunit = request.getParameter("topunit");
		BudgetExecRateForm budgetForm = (BudgetExecRateForm)session.getAttribute("budgetExecRateForm");
		String Budget_id = budgetForm.getBudget_id();
		//create xtree.js treeview.
		String xmlc=bo.genOrgXml(Budget_id,topunit,"/gz/gz_budget/budget_execrate.do?b_query=query", "mil_body","3");  
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
