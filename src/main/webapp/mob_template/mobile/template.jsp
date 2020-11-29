<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.transaction.mobileapp.template.TemplateBo"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.AdminDb"%>
<%@ page import="com.hrms.struts.exception.GeneralException"%>
<%@ page import="java.sql.*"%>
<%
String message="";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <meta name="viewport" content="minimum-scale=0.1; maximum-scale=5;  initial-scale=0.2; user-scalable=yes">
  <script language="javascript" src="/ajax/constant.js"></script>
  <script language="javascript" src="/ajax/basic.js"></script>
  <script language="javascript" src="/ajax/common.js"></script>
  <script language="javascript" src="/ajax/control.js"></script>
  <script language="javascript" src="/ajax/editor.js"></script>
  <script language="javascript" src="/ajax/dropdown.js"></script>
  <script language="javascript" src="/ajax/table.js"></script>
  <script language="javascript" src="/ajax/menu.js"></script>
  <script language="javascript" src="/ajax/tree.js"></script>
  <script language="javascript" src="/ajax/pagepilot.js"></script>
  <script language="javascript" src="/ajax/command.js"></script>
  <script language="javascript" src="/ajax/format.js"></script>
  <script type="text/javascript" src="../js/m_common.js"></script>
  <script type="text/javascript" src="../js/m_dataset.js"></script>
  <script type="text/javascript" src="../js/m_constant.js"></script>
  <script type="text/javascript" src="../js/m_basic.js"></script>
  <script type="text/javascript" src="../js/m_loadxmldoc.js"></script>
  <link href="../css/mobile.css" rel="stylesheet" type="text/css">
  <script language="javascript">
    var _checkBrowser=true;
    var _disableSystemContextMenu=false;
    var _processEnterAsTab=true;
    var _showDialogOnLoadingData=true;
    var _enableClientDebug=true;
    var _theme_root="/ajax/images";
    var _application_root="";
    var __viewInstanceId="968";
    var ViewProperties=new ParameterSet();
</script>
  </head>
  <body onload="init()">
    <%
    Connection connection=null;
    String htmlview="";
        try
        {
           connection = (Connection) AdminDb.getConnection();
           int tabid=Integer.parseInt(request.getParameter("tabid")==null?"0":request.getParameter("tabid"));
           String taskid=request.getParameter("taskid");
           int pagenum =Integer.parseInt(request.getParameter("pagenum")==null?"0":request.getParameter("pagenum"));
           String infor_type = request.getParameter("infor_type");
           String objId =request.getParameter("objid");
           String selfapply=request.getParameter("selfapply")==null?"0":request.getParameter("selfapply"); //个人业务申请
           String basepre ="";
           String a0100 ="";
           String b0110 ="";
           String e01a1 ="";
           if(infor_type.equals("1")){
            String tempArray[]=objId.split("`");
            a0100=tempArray[0];
            basepre=tempArray[1];
           }else if(infor_type.equals("2")){
            b0110=objId;
           }else if(infor_type.equals("3")){
            e01a1=objId;
           }
           UserView userView=(UserView)request.getSession().getAttribute(WebConstant.userView); 
           String business_model=(String)userView.getHm().get("business_model");
           TemplateBo bo = new TemplateBo(connection,userView);
           bo.setBusiness_model(business_model);
           bo.setA0100(a0100);
           bo.setB0110(b0110);
           bo.setBasepre(basepre);
           bo.setE01a1(e01a1);
           htmlview = bo.createPageview(tabid,taskid,pagenum,selfapply);
           
        }catch(Exception e)
        {
            if(e instanceof GeneralException){
                String errorMsg=e.toString();
                int index_i=errorMsg.indexOf("description:");
                message=errorMsg.substring(index_i+12); 
            }else{
                e.printStackTrace();
            }
            
        }finally
        {
          if(connection!=null)
           connection.close();  
        }
        
    %>
    <%
       if(message.length()>0){
        out.println("<script type='text/javascript'>");
        out.println(" var message =\""+message+"\";");
        out.println("if(window.Android!=null){");
        out.println("window.Android.showToast(message);");
        out.println("}else{");
        out.println("document.location=\"objc::showToast::\"+message;");
        out.println("}");
        out.println("</script>");
       }else{
            out.println(htmlview);  
       }
    %>
    
  </body>
</html>