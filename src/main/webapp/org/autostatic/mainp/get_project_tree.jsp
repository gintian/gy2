<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>
<%@ page import="com.hjsj.hrms.interfaces.sys.CteateProjectXml"%>
<%@ page import="com.hrms.struts.exception.*" %>

<%
    response.setContentType("text/xml;charset=UTF-8");
    String fieldsetid = request.getParameter("fieldsetid");
    fieldsetid = fieldsetid!=null&&fieldsetid.length()>0?fieldsetid:"";
    
    String fielditemid = request.getParameter("fielditemid");
    fielditemid = fielditemid!=null&&fielditemid.length()>0?fielditemid:""; 

	CteateProjectXml codexml=new CteateProjectXml();
	if(fielditemid.length()<1){
		try{
	  		String xmlc= "";
	  		if(fieldsetid.length()>1){
	  			xmlc = codexml.outProjectTree(fieldsetid); 
	  		}else{
	  			xmlc = codexml.outCodeTree();  //create xtree.js treeview.
	 		 }
	  		//out.println(xmlc);
	  response.getWriter().write(xmlc);
	  response.getWriter().close();;
		}catch(GeneralException ee){
      	    ee.printStackTrace();
		}
	}

%>