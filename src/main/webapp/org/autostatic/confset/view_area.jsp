<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hjsj.hrms.businessobject.sys.SysParamBo"%>
<%
	// 在标题栏显示当前用户和日期 2004-5-10 
	String css_url="/css/css1.css";
	String bosflag="";
	String themes="default";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
	      //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");
	  bosflag=userView.getBosflag(); 
	  /*xuj added at 2014-4-18 for hcm themes*/
	  themes=SysParamBo.getSysParamValue("THEMES",userView.getUserName());  
	}
%>
<HTML>
<HEAD>
<TITLE>
</TITLE>
  	 <link rel="stylesheet" href="<%=css_url%>" type="text/css">
	<link rel="stylesheet" href="/css/xtree.css" type="text/css">
	<%if("hcm".equals(bosflag)){ %>
 	  <link href="/css/hcm/themes/<%=themes %>/content.css" rel="stylesheet" type="text/css" />
  
 	 <%} %> 
   <SCRIPT LANGUAGE=javascript src="/js/constant.js"></SCRIPT>
   <SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<script language="JavaScript">
var input_field_id;
var selfForm;
function savecode(){
   var currnode,codeitemid;
   currnode=Global.selectedItem;
   if(currnode==null)
    	return;   
   codeitemid=currnode.uid;
   window.returnValue = codeitemid;
}
</script> 
</HEAD>
<body   topmargin="0" leftmargin="0" marginheight="0" marginwidth="0">
<div class="fixedDiv3">
   <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >          
         <tr>
           <td align="left">
            <div id="treemenu" style="height:300px !important;overflow:auto;width:expression(document.body.clientWidth-15);" class="RecordRow"></div>
            </td>
         </tr>
   
   <SCRIPT language=javascript>
             var m_sXMLFile="/org/autostatic/confset/get_viewarea_tree.jsp?codeitemid=root";	 
                          
             var root=new xtreeItem("0",INSTI_TUTIONAL,"","",INSTI_TUTIONAL,"/images/unit.gif",m_sXMLFile);
             Global.closeAction="savecode();window.close();";
             root.setup(document.getElementById("treemenu"));
	     
   </SCRIPT>
   <tr> 
	   <td align="center">
	      <span> <input type="button" name="btnok" onclick="savecode();window.close();" value="<bean:message key='reporttypelist.confirm'/>" Class="mybutton"/></span>
		  <span> <input type="button" name="btncancel" onclick="window.close();" value="<bean:message key='kq.register.kqduration.cancel'/>"  Class="mybutton"/></span> 
	    </td>
    </tr> 
   </table> 
</div>
</body>
</HTML>
