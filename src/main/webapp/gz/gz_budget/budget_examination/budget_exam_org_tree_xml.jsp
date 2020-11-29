<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java"%>

<%@ page import="com.hrms.struts.exception.*"%>
<%@ page import="com.hjsj.hrms.businessobject.gz.gz_budget.budget_examination.BudgetExamBo,
com.hjsj.hrms.actionform.gz.gz_budget.BudgetExaminationForm"%>
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
		BudgetExaminationForm budgetForm = (BudgetExaminationForm)session.getAttribute("budgetExaminationForm");
		String Budget_id = budgetForm.getBudget_id();
		String flag = budgetForm.getFlag();
		//create xtree.js treeview.
		String xmlc="";
		if (flag.equals("2")){//审批	
			 xmlc=bo.genOrgXml(Budget_id,topunit,"/gz/gz_budget/budget_examination.do?b_query=query", "mil_body","2");  
		}
		else {
			xmlc=bo.genOrgXml(Budget_id,topunit,"/gz/gz_budget/budget_examination.do?b_query=query", "mil_body",null);  
		}
		
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
