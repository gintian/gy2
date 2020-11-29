<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link href="/css/css1.css" rel="stylesheet" type="text/css">
	<title></title>
	<script language="JavaScript">
	function ss()
	{
	   var nam=$('names');
	   if(getBrowseVersion() && getBrowseVersion()!=10){//确定返回数据  wangb 20180802 bug 39338
		   returnValue=nam.value;
		   window.close();
	   }else{
	   	   parent.returnValue(nam.value);
	   }
	   
	}
	</script>
<hrms:themes />
<html:form action="/general/static/add_general">
	<html:hidden  name="staticFieldForm" property="logiclist"/>
      <fieldset align="center" style="width:30%;">
         <legend><bean:message key="static.input.name"/></legend>
      <table width="260" border="0" cellpadding="0" cellspacing="0" align="center">
           <tr class="trDeep1"  style="height: 60px;">
                 <td align="center"  nowrap valign="center">
            	    <html:text name="staticFieldForm" value="${staticFieldForm.names}" property="names" size="40" styleClass="text4"/>
                 </td>
              </tr>  
              </table>
       </fieldset>
      <table width="260" border="0" cellpadding="0" cellspacing="0" align="center">
      <tr style="height: 40px;">
            <td align="center">
         	  <input type="button" name="b_ok" value="<bean:message key="button.ok"/>" class="mybutton" onclick="ss()">
	          <input type="button" name="br_return" value="<bean:message key="button.close"/>" class="mybutton" onclick="winClose();">
            </td>
          </tr> 
      </table> 
</html:form >
<script language="javascript">
	function winClose(){//关闭弹窗  wangb 20180802 bug 39338
		if(getBrowseVersion()&& getBrowseVersion()!=10)
			window.close();
		else
			parent.winClose();
	}
	if(getBrowseVersion() ==10){ //ie11浏览器
        document.getElementsByTagName('fieldset')[0].style.width = "85%";
    }
</script>
