<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="com.hjsj.hrms.businessobject.gz.piecerate.SelPersonByXmlBo"%>
<%@ page import="com.hrms.struts.exception.*" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.codec.SafeCode"%>


<%
    response.setContentType("text/xml;charset=UTF-8");
	String flag = request.getParameter("flag");
	String nbase = request.getParameter("nbase");
	String id = request.getParameter("id");
	String s0100=request.getParameter("s0100");
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);		

	id = id != null ? id : "";
	id = SafeCode.decode(id);
	
	SelPersonByXmlBo orgPersonByXml = new SelPersonByXmlBo(flag,nbase,id,s0100,userView);
		
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