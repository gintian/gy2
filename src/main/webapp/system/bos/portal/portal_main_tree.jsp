<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="com.hjsj.hrms.interfaces.sys.bos.PortalMainTree"%>
<%@ page import="com.hrms.struts.exception.*,org.jdom.Document,
com.hjsj.hrms.actionform.sys.bos.portal.PortalMainForm" %>

<%
        response.setContentType("text/xml;charset=UTF-8");
  
	String opt = request.getParameter("opt");
	String codeid = request.getParameter("codeid");
	String parent_id = request.getParameter("portal_id"); 
	//String parent_name = request.getParam
	PortalMainTree portalMainTree = new PortalMainTree(opt,codeid,parent_id);
	try
	{
		PortalMainForm portalMainForm=(PortalMainForm)session.getAttribute("portalMainForm"); 
		Document doc=portalMainForm.getPortal_dom(); 
		//System.out.println("展现doc"+doc);
	  String xmlc=portalMainTree.outPut_Xml(doc);
	 // out.println(xmlc);
	  response.getWriter().write(xmlc);
	    response.getWriter().close();
	}
	catch(GeneralException e)
	{
      	    e.printStackTrace();
	}

%>