<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/gz/salary.js"></script>
<html>
  <head>
    

  </head>
  <style>
	div#data {
	background-color:#FFFFFF;
	BORDER-BOTTOM:#94B6E6 1pt inset; 
	BORDER-COLLAPSE: collapse;
	BORDER-LEFT: #94B6E6 1pt inset; 
	BORDER-RIGHT: #94B6E6 1pt inset; 
	BORDER-TOP: #94B6E6 1pt inset; 
	width: 370px;
	height: 250px;
	overflow: auto;
	}
	
	
	
	
  </style>
  <body>
   	 <html:form action="/gz/gz_accounting/importMen">
   		<table width='100%' >
   			<tr>
   			<td><bean:message key="label.gz.orgSet"/>:
   				<html:select name="importPersonnelForm" property="fieldSetId" size="1"  onchange='getItemHtml()'   >
				   <html:optionsCollection property="fieldSetList" value="dataValue" label="dataName" />
				</html:select> 
   			
   			 </td>
   			</tr>
   			<tr>
   			<td valign='top'> 
   				<bean:message key="label.gz.otherFieldItem"/>:<br>
   				<div id="data" class="complex_border_color">
   					<logic:iterate  id="element"    name="importPersonnelForm"  property="fieldItemList" indexId="index"> 
   						<input type="checkbox" name="<%="fieldItemList["+index+"].isExist"%>"   <logic:equal name='element' property="isExist" value="1">checked</logic:equal>   value="<bean:write  name="element" property="itemid"/>" />
   						<bean:write  name="element" property="itemdesc"/><br>
   					</logic:iterate>
   				</div>
   			</td>

   			</tr>
   			<tr>
   			<td valign='bottom' align='center' >
   				<Input type='hidden' name='fieldItems' value='' />
   				<Input type='button'   class="mybutton"  onclick='sub()'  value='<bean:message key="button.ok"/>' />
   				<Input type='button'  onclick='javascript:window.close()'  class="mybutton"  value='<bean:message key="button.cancel"/>' />
   			</td>
   			</tr>
   	 </html:form>
  
  <script language='javascript' >
  
  <% if(request.getParameter("b_saveParam")!=null&&request.getParameter("b_saveParam").equals("save"))
  	 {
  	 	out.println("returnValue='1';");
   	  	out.println("window.close();");
  	 }
  
   %>
  
  
  function sub()
  {
  		var num=0;
  		var a_fielditems="";
  		for(var i=0;i<document.importPersonnelForm.elements.length;i++)
  		{
  			if(document.importPersonnelForm.elements[i].type=='checkbox')
  			{
  				if(document.importPersonnelForm.elements[i].checked==true)
  				{
  					a_fielditems+=","+document.importPersonnelForm.elements[i].value;
  				}
  			}
  		}
  		if(a_fielditems.length>0)
  			a_fielditems=a_fielditems.substring(1);	
  		document.importPersonnelForm.fieldItems.value=a_fielditems;
  		document.importPersonnelForm.action="/gz/gz_accounting/importMen.do?b_saveParam=save";
  		document.importPersonnelForm.submit();
  		
  }
  
  </script>
  
  </body>
</html>

