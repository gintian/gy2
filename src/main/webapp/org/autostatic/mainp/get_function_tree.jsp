<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>
<%@ page import="com.hjsj.hrms.interfaces.sys.CteateProjectXml"%> 
<%@ page import="com.hrms.struts.exception.*" %>

<%
    response.setContentType("text/xml;charset=UTF-8");
    String id = request.getParameter("id");
    String checktemp=(String)request.getParameter("checktemp");
	String mode = (String)request.getParameter("mode");
	CteateProjectXml codexml=new CteateProjectXml();
	try{
	  	String xmlc = "";
	  	   if(id!=null){
	  	   		xmlc = codexml.outMainpTree(id,checktemp,mode);
	  	   }
	  	   else{
	  	   		xmlc = codexml.outFunctionTree(checktemp,mode);  //create xtree.js treeview.
	  	   }
	  	//out.println(xmlc);
	  response.getWriter().write(xmlc);
	  response.getWriter().close();;
	}catch(GeneralException ee){
      	ee.printStackTrace();
	}
%>