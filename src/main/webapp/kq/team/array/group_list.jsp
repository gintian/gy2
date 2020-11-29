<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>
<%@ page import="com.hjsj.hrms.interfaces.kq.KqGroupByXml"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.exception.*" %>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%
   	response.setContentType("text/xml;charset=UTF-8");

    //zxj 20150825 安全漏洞
    String params = (String)request.getParameter("params");
    if (params != null && !"".equals(params) 
            && !(params.toLowerCase().startsWith("gp")
                    ||params.toLowerCase().startsWith("un")
                    ||params.toLowerCase().startsWith("um")
                    ||params.toLowerCase().startsWith("@k")
                    ||"root".equalsIgnoreCase(params))
            && !(params.startsWith("EP") || params.length()==11)
                    ){
        params = PubFunc.decrypt(params);
    }
    String straction=(String)request.getParameter("straction");
    if(straction==null||straction.length()<0)
       straction="/kq/team/array/search_array_data.do";
      
    //zxj 20150825 安全漏洞
    String codeitem=(String)request.getParameter("codetiem");
    if (codeitem != null && !"".equals(codeitem) 
            && !(codeitem.toLowerCase().startsWith("gp")
                    ||codeitem.toLowerCase().startsWith("un")
                    ||codeitem.toLowerCase().startsWith("um")
                    ||codeitem.toLowerCase().startsWith("@k")
                    ||"root".equalsIgnoreCase(codeitem))
            && !(codeitem.startsWith("EP") || codeitem.length()==11)
                    ){
        codeitem = PubFunc.decrypt(codeitem);
    }
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
 
	KqGroupByXml  orgxml=new KqGroupByXml (params,straction,"mil_body",codeitem,userView);
	try
	{
	  String xmlc=orgxml.outTree();  //create xtree.js treeview.
	  //out.println(xmlc);
	  response.getWriter().write(xmlc);
	  response.getWriter().close();
	}
	catch(Exception ee)
	{
      	    ee.printStackTrace();
	}
%>