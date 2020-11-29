<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>
<%@ page import="com.hjsj.hrms.businessobject.train.TrainAddBo,java.sql.*"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.AdminDb"%>
<%  
    response.setContentType("text/xml;charset=UTF-8");
	String classid = request.getParameter("classid");
	String r3702 =  request.getParameter("r3702");

	Connection connection=null;
	try
	{
	  connection = (Connection) AdminDb.getConnection();
	  TrainAddBo tree= new TrainAddBo(connection);
	  String xmlc=tree.outPutXmlStr(classid,r3702);  //create xtree.js treeview.
	  //out.println(xmlc);
	  response.getWriter().write(xmlc);
	  response.getWriter().close();;
	}  
	catch(Exception e)
	{
      	    e.printStackTrace();
	}finally
	{
		try{
	  		if(connection!=null)
	   		connection.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
%>