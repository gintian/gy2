<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html>
  <head>
   <link rel="stylesheet" type="text/css" href="/css/diary.css"/>
   <script type="text/javascript" src="/performance/nworkdiary/diary.js"></script>
   <script type="text/javascript">
   function goNextWeek(type)
   {
      monthWorkForm.action="/performance/nworkdiary/homepage_diary.do?b_search=link&type="+type;
      monthWorkForm.submit();
   }
   function setNavigation(fromurl,frommodeid)
	{
		document.sysForm.fromUrl.value=fromurl;
		document.sysForm.fromModid.value=frommodeid;
		document.sysForm.action="/templates/index/submainpanel.do?b_query=link&amp;module=-1";
		document.sysForm.submit();
	}
   function goCalendar(str)
   {
		var temp =str.split("-");
   		setNavigation("/performance/nworkdiary/myworkdiary/daywork.do?b_init=link&year="+temp[0]+"&month="+temp[1]+"&day="+temp[2]+"&frompage=-2",'37');
   }
   </script>
  </head>
  <body>
  <html:form action="/templates/index/submainpanel" target="i_body">
	<html:hidden property="fromUrl" name="sysForm"/>
	<html:hidden property="fromModid" name="sysForm"/> 
</html:form> 
  <html:form action="/performance/nworkdiary/homepage_diary">
  <html:hidden property="hp_start"/>
  <html:hidden property="hp_end"/>
  <html:hidden property="currDateStr"/>
    <br>
    <table width="95%" align="center" height="140" border="0" cellpadding="0" cellspacing="0">
         <tr>
            <td colspan="7" width="100%" height="40" align="center" valign="middle">
                <table width="100%" height="40" border="0" cellpadding="0" cellspacing="0" background="/images/epm_72.gif" class="b3">
                      <tr>
                        <td width="2%">&nbsp;</td>
                        <td width="7%" align="left" valign="middle" height="39" background="/images/epm_bj.gif" >
                       <img src="/images/epm_78.gif" width="100%" height="39" style="cursor:hand;" onclick="goNextWeek('1');"/></td>
                        <td width="40%" align="center" valign="middle" height="39" background="/images/epm_bj.gif" class="z5">
                        ${monthWorkForm.hp_start }--${monthWorkForm.hp_end}
                        </td>
                        <td width="7%" align="left" valign="middle" height="39" background="/images/epm_bj.gif">
                         <img style="cursor:hand;" onclick="goNextWeek('-1');"  src="/images/epm_80.gif" width="100%" height="39" alt=""/></td>
                      
                       <td width="43%" align="left" valign="middle">&nbsp;&nbsp;&nbsp;<img src="/images/epm_82.gif" width="27" height="26" alt="" />
                       </td>
                      </tr>
                    </table>
            </td>
         </tr>
         <tr>
          <td width="100%" height="100" align="center" valign="middle">
          <table width="100%" height="88" border="0" cellpadding="0" cellspacing="0" class="b3">
                        <tr>
                       <logic:iterate id="element" name="monthWorkForm" property="gridList" offset="0" indexId="index">
                       <logic:equal value="${monthWorkForm.currDateStr}" name="element" property="str">
                       <td width="14.3%" height="38" align="center" valign="middle" background="/images/epm_95.gif">
                        </logic:equal>
                        <logic:notEqual value="${monthWorkForm.currDateStr}" name="element" property="str">
                        <td width="14.3%" height="38" align="center" valign="middle" background="/images/epm_94.gif">
                        </logic:notEqual>
                         <span class="z4">
                                <bean:write name="element" property="day"/>&nbsp;-&nbsp;<bean:write name="element" property="week"/>
                         </span>
                       </td>
                       </logic:iterate>
                       </tr>
                        <tr>
                        <logic:iterate id="element" name="monthWorkForm" property="gridList" offset="0" indexId="index">
                          <td width="14.3%" height="47" class="b4" align="center">
                           <a href='javascript:goCalendar("<bean:write name="element" property="str"/>");'>
                             <img src="/images/epm_add.gif" width="16" height="16" border=0 alt="" />
                           </a>
                          </td>
                         </logic:iterate>
                        </tr>
                      </table>
                      </td>
         </tr>
    </table>
           
  </body>
  </html:form>
</html>
