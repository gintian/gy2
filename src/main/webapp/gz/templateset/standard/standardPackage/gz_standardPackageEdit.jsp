<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html>
  <head>
   
  </head>
  <script language='javascript'>
  
  	function sub()
  	{
   	    if(document.gzStandardPackageForm.newStandards.options.length==0)
			document.gzStandardPackageForm.newStandards.options[document.gzStandardPackageForm.newStandards.options.length]=new Option("#","#")
	 	 setselectitem('newStandards');
  		document.gzStandardPackageForm.action="/gz/templateset/standard/standardPackage.do?b_saveStandard=link";
  		document.gzStandardPackageForm.submit();
  	
  	}
  
  	function up()
  	{
  		document.gzStandardPackageForm.action="/gz/templateset/standard/standardPackage.do?b_query=link";
  		document.gzStandardPackageForm.submit();
  		
  	}
  
  </script>
  
  <body>
  
    <html:form action="/gz/templateset/standard/standardPackage">

	  <table width="700" border="0" cellspacing="0" cellpadding="0" align="center" class="framestyle" style="margin-top:60px;">
	  <tr>
	    <td height="20" colspan="3" class="TableRow_top common_background_color common_border_color">&nbsp;<bean:message key="label.gz.editGzStandard"/><Br></td>
	  </tr>
	  <tr>
	    <td width="220"><br>&nbsp;&nbsp; <bean:message key="label.gz.importedGzStandard"/></td>
	    <td width="40"><div align="left"></div></td>
	    <td width="220"><br>&nbsp;&nbsp;&nbsp;<bean:message key="label.gz.currentStandard"/></td>
	  </tr>
	  <tr>
	    <td>
	    &nbsp;&nbsp;
	    <select name="left_fields" size="15" style='width:300'  multiple ondblclick="additem('left_fields','newStandards');; removeitem('left_fields');"   >
	    	 <logic:iterate id="element" name="gzStandardPackageForm" property="standardList"   >
	      	<option value='<bean:write name="element" property="id" filter="true"/>' ><bean:write name="element" property="name" filter="true"/></option>
	         </logic:iterate>
	    
	    </select>    </td>
	    <td><div align="center">
	      <input name="Button" type="button" class="mybutton" value=" &gt;&gt; "  onclick="additem('left_fields','newStandards');; removeitem('left_fields');" /><Br><Br>
	      <input name="Submit2" type="button" class="mybutton" value=" &lt;&lt; " onclick="additem('newStandards','left_fields'); removeitem('newStandards');" />
	    </div></td>
	    <td>
	    &nbsp;&nbsp;
	    <select name="newStandards" size="15"  style='width:300' multiple  ondblclick="additem('newStandards','left_fields'); removeitem('newStandards');"  >
	       <logic:iterate id="element" name="gzStandardPackageForm" property="currentStandardList"   >
	      	<option value='<bean:write name="element" property="id" filter="true"/>' ><bean:write name="element" property="name" filter="true"/></option>
	      </logic:iterate>
	    </select></td>
	  </tr>
	  <tr>
	    <td>&nbsp;</td>
	    <td>&nbsp;</td>
	    <td>&nbsp;</td>
	  </tr>
	  <tr>
	    <td colspan="3"><div align="center">
	      <input name="Button" type="button" class="mybutton" value="<bean:message key="static.back"/>" onclick="up()" />
	      <input name="Submit3" type="button" class="mybutton" value="<bean:message key="kq.emp.button.save"/>" onclick='sub()' />
	    </div><br></td>
	  </tr>
	</table>
   </html:form>
  </body>
</html>
