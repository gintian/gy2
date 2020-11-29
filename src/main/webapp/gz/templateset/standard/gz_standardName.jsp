<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<hrms:themes />
<html>
  <head>
 
 
  </head>
  <script language='javascript'>
  
  	function saves()
  	{
  	   var nase=document.getElementById("gsn").value;
  	   if(trim(nase).length==0)
  	   {
  	      alert("请输入标准表名称！");
  	      return;
  	   }
  	   if(containSpecial(nase)){
			alert("薪资标准表名称中不允许有特殊字符！");
			return;
  		}
  		document.salaryStandardForm.action="/gz/templateset/standard.do?b_saveStandard=save";
      	document.salaryStandardForm.submit();
  	}
    function containSpecial( s ) { 
  		var containSpecial = RegExp(/[(\ )(\~)(\!)(\@)(\#) (\$)(\%)(\^)(\&)(\*)(\()(\))(\-)(\_)(\+)(\=) (\[)(\])(\{)(\})(\|)(\\)(\;)(\:)(\')(\")(\,)(\.)(\/) (\<)(\>)(\?)(\)]+/); 
  		return (containSpecial.test(s));
  	} 
  </script>
  
  <body>
  <html:form action="/gz/templateset/standard">
   <table width="303" height="117" border="0" align="center" cellpadding="0" cellspacing="0" class="ListTable">
	  <tr>
	    <td height="21"  class="TableRow">&nbsp;<bean:message key="gz.formula.salaries.standart.table"/></td>
	  </tr>
	  <tr>
	  <td class="RecordRow" align="center">
	  <table>
	  <tr>
	    <td width="86" height="28"> <div align="right"><br><bean:message key="gz.formula.standart.tablename"/></div></td>
	    <td width="201" ><br><input type="text" id="gsn" name="gzStandardName" value='${salaryStandardForm.gzStandardName}' /></td>
	    </tr>
	      <tr>
	     <td height="26" colspan="2">&nbsp;</td>
	   
	    </tr>
	    </table>
	    </td>
	  </tr>
	
	  <tr>
	    <td height="35" valign="middle"><div align="center">
	      <input name="Button2" type="button" class="mybutton" value="<bean:message key="static.back"/>" onclick="javascript:history.go(-1)"/>
	      <input name="Button" type="button" class="mybutton" value="<bean:message key="reporttypelist.confirm"/>" onclick="saves()"/>
	    </div></td>
	  </tr>
	</table>
   
   
  </html:form>
  </body>
</html>
