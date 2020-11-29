<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";	
%>
 <script language="JavaScript1.2">
   <!--设置IE工具条和菜单条都瞧不见?
    
    function pf_ChangeFocus() 
    { 
      key = window.event.keyCode;
      if ( key==0xD && event.srcElement.tagName!='TEXTAREA') /*0xD*/
      {
   	window.event.keyCode=9;
      }
    }   
    /*设置计算截止日期*/
    function getAppdate()
    {
      var strvalue;
      var now = new Date();        
      strvalue=getCookie("appdate");
      if(strvalue==null)
      {
    	strvalue=getDateString(now,".");
    	setCookie("appdate",strvalue);
      }
      document.logonForm.appdate.value=strvalue.replace(/\./g,"-");
    } 
    
    function setAppdate()
    {
      var strvalue;
      strvalue=document.logonForm.appdate.value;
      strvalue=strvalue.replace(/\-/g,".");
      setCookie("appdate",strvalue);
    }     
//-->
   </script>
<script language="javascript">
  function exeButtonAction(actionStr,target_str)
  {
    target_url=actionStr;
    window.open(target_url,target_str); 
  }  
</script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>


<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<hrms:themes></hrms:themes>
 <body bgcolor="#FFFFFF" text="#000000" style="margin:0 0 0 0">
 <table align="center" cellspacing="0" cellpadding="0" class="tabpos">
 <tr>
 <td width="100%" align="center">
   <br>
   <br>
    <bean:message key="label.zp_person.applysuccess"/>    
 </td>
 </tr>
 <tr>
   <td width="100%" align="center">
      <br>
    <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeButtonAction('/hire/zp_release_pos/search_zp_poslist.do?b_query=link','il_body')"> 
  </td>
  </tr>
 </table>
 </body>

