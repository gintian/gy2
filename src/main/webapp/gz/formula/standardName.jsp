<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<hrms:themes />
<script language='javascript'>
function saves(){
  	var hfactor = document.getElementById("hfactor").value
  	var s_hfactor = document.getElementById("s_hfactor").value
  	var vfactor = document.getElementById("vfactor").value
  	var s_vfactor = document.getElementById("s_vfactor").value
  	var hcontent = document.getElementById("hcontent").value
  	var vcontent = document.getElementById("vcontent").value
  	var item = document.getElementById("item").value
  	var gzStandardName = document.getElementById("gzStandardName").value
  	
	var hashvo=new ParameterSet();
	hashvo.setValue("hfactor",hfactor);
	hashvo.setValue("s_hfactor",s_hfactor);
	hashvo.setValue("vfactor",vfactor);
	hashvo.setValue("s_vfactor",s_vfactor);
	hashvo.setValue("hcontent",hcontent);
	hashvo.setValue("vcontent",vcontent);
	hashvo.setValue("item",item);
	hashvo.setValue("gzStandardName",gzStandardName);
	var request=new Request({asynchronous:false,functionId:'3020060016'},hashvo);
	window.returnValue = 'ok';
	window.close();
}
  
</script>
<html:form action="/gz/formula/standardName">
<table height="60" border="0">
<tr><td>&nbsp;</td></tr>
</table>
   <table width="303" height="117" border="0" align="center" cellpadding="0" cellspacing="0" class="framestyle">
	  <tr>
	    <td height="21" colspan="2" class="TableRow">&nbsp;<bean:message key='gz.formula.salaries.standart.table'/></td>
	  </tr>
	  <tr>
	    <td width="86" height="28"> <div align="right"><br><bean:message key='gz.formula.standart.tablename'/></div></td>
	    <td width="201"><br><input type="text" name="gzStandardName"></td>
	  </tr>
	  <tr>
	    <td height="26">&nbsp;</td>
	    <td>
			<Input type='hidden' name='hfactor'  value='${salaryStandardForm.hfactor}' />
			<Input type='hidden' name='s_hfactor'  value='${salaryStandardForm.s_hfactor}' />
			<Input type='hidden' name='vfactor'  value='${salaryStandardForm.vfactor}' />
			<Input type='hidden' name='s_vfactor'  value='${salaryStandardForm.s_vfactor}' />
			<Input type='hidden' name='hcontent'  value='${salaryStandardForm.hcontent}' />
			<Input type='hidden' name='vcontent'  value='${salaryStandardForm.vcontent}' />
			<Input type='hidden' name='item'  value='${salaryStandardForm.item}' />
		</td>
	  </tr>
	</table>
	<table align="center" >
		  <tr>
	    <td height="26" colspan="2"><div align="center">
	      <input name="Button" type="button" class="mybutton" onclick="saves();" value="<bean:message key='reporttypelist.confirm'/>"/>
	      <input name="Button2" type="button" class="mybutton" onclick="window.close();" value="<bean:message key='kq.register.kqduration.cancel'/>"/>
	    </div></td>
	  </tr>
	</table>
</html:form>
