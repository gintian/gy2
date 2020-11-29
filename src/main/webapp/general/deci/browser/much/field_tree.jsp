<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>
<%@ page import="com.hjsj.hrms.interfaces.general.deci.browser.MuchFieldByXML"%>
<%@ page import="com.hrms.struts.exception.*" %>
<%
    response.setContentType("text/xml;charset=UTF-8");

	String params = request.getParameter("params");

	MuchFieldByXML sfxml = new MuchFieldByXML(params,"/general/deci/browser/much/muchfieldanalyse.do" , "ril_body1");
	String xmlc = "";
	try{
    	xmlc=sfxml.outPutSingleFieldXml(); 
    }	catch(GeneralException e)
	{
      	    e.printStackTrace();
	}
     //create xtree.js treeview.
	//out.println(xmlc);
	 response.getWriter().write(xmlc);
	 response.getWriter().close();

%>