<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>
<%@ page import="com.hjsj.hrms.interfaces.sys.CreateReportOrgXml"%>
<%@ page import="com.hrms.struts.exception.*" %>


<%
    response.setContentType("text/xml;charset=UTF-8");
	String unitcode=(String)request.getParameter("unitcode");
	unitcode=unitcode!=null?unitcode:"";
	 
	String report_type=(String)request.getParameter("report_type");
	report_type=report_type!=null?report_type:"";
	
	CreateReportOrgXml  xml=new CreateReportOrgXml();
	try
	{
	  String xmlc= xml.outCodeTree(unitcode,report_type);  //create xtree.js treeview.
	  //out.println(xmlc);
	    response.getWriter().write(xmlc);
	    response.getWriter().close();	  
	}
	catch(GeneralException ee)
	{
      	    ee.printStackTrace();
	}

%>
