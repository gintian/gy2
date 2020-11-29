<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="com.hjsj.hrms.interfaces.performance.OrgPersonByXml"%>
<%@ page import="com.hrms.struts.exception.*" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.codec.SafeCode"%>


<%
    response.setContentType("text/xml;charset=UTF-8");
  
	String flag = request.getParameter("flag");
	String id = request.getParameter("id");
	String planid=request.getParameter("planid");
	String opt=request.getParameter("opt");
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);		

	id = id != null ? id : "";
	id = SafeCode.decode(id);
	OrgPersonByXml orgPersonByXml = new OrgPersonByXml(flag,id,opt,planid,userView);
	if(opt!=null)
	{
		if(opt.equals("5") || opt.equals("8"))
		{
			String khObjCopyed = request.getParameter("khObjCopyed");
			orgPersonByXml.setKhObjCopyed(khObjCopyed);
		}
		if(opt.equals("2"))
		{
			String oldPlan_id = request.getParameter("oldPlan_id");
			orgPersonByXml.setOldPlan_id(oldPlan_id);
		}
		if(opt.equals("1") || opt.equals("9")|| opt.equals("13"))
		{
			String accordPriv = request.getParameter("accordPriv");
			accordPriv = accordPriv != null ? accordPriv : "true";
			orgPersonByXml.setAccordPriv(accordPriv);
		}
		if(opt.equals("12"))
		{		
			orgPersonByXml.setAccordPriv("false");
		}
	}		
	try
	{
	  String xmlc=orgPersonByXml.outPutXml();  
	  if(xmlc!=null&&xmlc.length()>0)
      {
      		response.getWriter().write(xmlc);
	 		response.getWriter().close(); 
      }
	}
	catch(GeneralException e)
	{
      	    e.printStackTrace();
	}

%>