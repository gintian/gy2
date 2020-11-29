<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,java.text.SimpleDateFormat,java.util.Date"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
%>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<script language="JavaScript" src="/js/function.js"></script>
<script type="text/javascript" src="/js/validateDate.js"></script>
<script type="text/javascript">
    var info;
    if(window.opener.formHistoryDateReturn){
        info = window.opener.openerConfig;
    }else{
        info=dialogArguments;
    }
<!--
    // 兼容ie 谷歌 wangbs 20190318
	function clickok(){
        backdate=document.forms[0].end_date.value;
        if(window.opener.formHistoryDateReturn){
            window.opener.formHistoryDateReturn(backdate);
        }else{
            returnValue=backdate;
        }
        window.close();
    }
//-->
</script>
<hrms:themes />
<html:form action="/report/report_analyse/reportunittree"> 
  <table width="95%" border="0" cellspacing="0"  align="center" cellpadding="0">
  <tr >  <td colspan="2" class="framestyle1"><table  width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
	 <tr align="center">
		<td align="left" class="TableRow" style="border-left:none;border-right:none;">
		  历史时点查询
		</td>
	 </tr>         
           	  
         <%
	             	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		            String date = sdf.format(new Date());
              %>
              <tr height="10">
            	<td>&nbsp;</td>
            </tr>
             <tr  align="center" class="list3">
             
             </tr>
             <tr height="20">
            	<td>&nbsp;</td>
            </tr>
             <tr  align="center" class="list3">
               <td> 
             <script language='javascript'>
	   	  document.write('<input type="text" name="end_date" class="text4"  maxlength="50" extra="editor" dropDown="dropDownDate" onchange="if(!validate(this,\'截止日期\')) {this.focus(); this.value=\'\'; }else{this.value=replaceAll(this.value,\'.\',\'-\')}"/>');
   	   	  </script>
               </td>
             </tr>  
            <tr height="10">
            	<td>&nbsp;</td>
            </tr>
            <tr height="100">
            	<td>&nbsp;</td>
            </tr>
            </table>
            </td>
            </tr>
      </table>  
   <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0"> 
   <tr>
      <td height="5px"></td>
    </tr>
    <tr>
            <td align="center">
         	 <html:button styleClass="mybutton" property="" onclick="clickok();">
            		<bean:message key="button.ok"/>
	 	    </html:button>
         	<html:button styleClass="mybutton" property="" onclick="window.close();">
            		<bean:message key="button.cancel"/>
	 	    </html:button>  
            </td>         
         </tr>        
   </table>
</html:form>
