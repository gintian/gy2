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
  function goback()
  {
  	window.close();
  }
  
  
  function enter()
  {
  	var aa = document.getElementById("description").value;
  	 var pattern=/[~]/im;  
  	 if(pattern.test(aa))
  	 {
  			alert("~为非法字符！");
  	 		return;
  	 }
  	 if(trim(document.salaryStandardDataForm.description.value).length==0)
  	 {
  			alert(GZ_TEMPLATESET_INFO13);
  	 		return;
  	 }
  	 if(document.salaryStandardDataForm.heightOperate.value=='无'&&document.salaryStandardDataForm.lowerOperate.value=='无')
  	 {
  			alert(GZ_TEMPLATESET_INFO14+"!");
  	 		return;
  	 }
  	 
  	 
  	  <logic:equal name="salaryStandardDataForm" property="type" value="N" >  
        var myReg =/^(-?\d+)(\.\d+)?$/   //实数
        var desc=GZ_TEMPLATESET_FLOAT;
       </logic:equal>
       <logic:equal name="salaryStandardDataForm" property="type" value="D" > 
        var myReg =/^\d+$/   //正整数
        var desc=GZ_TEMPLATESET_INTEGER;
       </logic:equal>
  	    
		if(document.salaryStandardDataForm.lowerValue.value!="")
  	    {
			if(!myReg.test(document.salaryStandardDataForm.lowerValue.value)) 
		  	{
		  		alert(GZ_TEMPLATESET_INPUT+desc);	
		  		document.salaryStandardDataForm.lowerValue.focus();
		  		return;
		  	}
		}
		else if(document.salaryStandardDataForm.lowerOperate.value!='无')
		{
				alert(GZ_TEMPLATESET_INFO15);
				return;
		}
		if(document.salaryStandardDataForm.heightValue.value!="")
  	    {
		  	if(!myReg.test(document.salaryStandardDataForm.heightValue.value)) 
		  	{
		  		alert(GZ_TEMPLATESET_INPUT+desc);
		  		document.salaryStandardDataForm.heightValue.focus();
		  		return;
		  		
		  	}	
		}	
  		else if(document.salaryStandardDataForm.heightOperate.value!='无')
		{
				alert(GZ_TEMPLATESET_INFO16);
				return;
		}
		
 <logic:equal name="salaryStandardDataForm" property="type" value="D" > 
		if(document.salaryStandardDataForm.isAccuratelyDay.checked==false)
		{
			document.salaryStandardDataForm.isAccuratelyDay.value="0";
			document.salaryStandardDataForm.isAccuratelyDay.checked=true;
		}
 </logic:equal>	
		document.salaryStandardDataForm.action="/gz/templateset/standard/standardData.do?b_save=save&opt=save";		
		document.salaryStandardDataForm.submit();
	
  }
  
  
  function validateValue()
  {
  	if(document.salaryStandardDataForm.middleValue.value.substring(0,2)==GZ_TEMPLATESET_YEAR||document.salaryStandardDataForm.middleValue.value.substring(0,2)==GZ_TEMPLATESET_MONTH)
  	{
  				document.salaryStandardDataForm.isAccuratelyDay.checked=false;
  				document.salaryStandardDataForm.isAccuratelyDay.disabled=true;
  	}
  	else
  	{
  				
  				document.salaryStandardDataForm.isAccuratelyDay.disabled=false;
  	}
  	
  }
  
  <%
  if(request.getParameter("opt")!=null&&request.getParameter("opt").equals("save"))
  {
  %> 		
  		window.close();
  <%
  }	  
  %>
  
  
  </script>
  <body>
  
  <html:form action="/gz/templateset/standard/standardData" > 
    
    <table width='100%' align='center' ><tr><td>
    
    <fieldset align="center" style="width:95%;">
         <legend>
       <logic:equal name="salaryStandardDataForm" property="type" value="N" >  
         <bean:message key="hmuster.label.data_scope"/>
       </logic:equal>
       <logic:equal name="salaryStandardDataForm" property="type" value="D" > 
         <bean:message key="kq.init.tscope"/>
       </logic:equal>
         </legend>
         <table border='0' width="100%">
        		  <tr> 
		            <td height='35' colspan='3'>
		            	<logic:equal name="salaryStandardDataForm" property="type" value="N" > 
		            	<br>
		            	</logic:equal>
		            
						&nbsp;&nbsp;<bean:message key="gz.templateset.intervalDesc"/><Input type='text' name="description"  class=textbox maxlength='50'  size='54'  value='${salaryStandardDataForm.description}' >
					</td>
		          </tr>
		          
		          <tr>
			          <td height='35' width='40%' > &nbsp; 
			          <Input type='text' name="lowerValue"  class=textbox  size='15' value='${salaryStandardDataForm.lowerValue}' >
			          <html:select name="salaryStandardDataForm" property="lowerOperate" size="1">
                              <html:optionsCollection property="lowerOperateList" value="dataValue" label="dataName"/>
        			   </html:select> 
			          </td>
			          
			          <td align='center' width='20%' >
			          <logic:equal name="salaryStandardDataForm" property="type" value="N" > 
			          		 ${salaryStandardDataForm.middleValue}
			          </logic:equal> 
			           <logic:equal name="salaryStandardDataForm" property="type" value="D" > 
			          		 <html:select name="salaryStandardDataForm" property="middleValue" onchange='validateValue()' size="1">
                              <html:optionsCollection property="middleValueList" value="dataValue" label="dataName"/>
        			  		 </html:select> 
			          </logic:equal> 
			           </td>
			          
			          <td> 
			          <html:select name="salaryStandardDataForm" property="heightOperate" size="1">
                              <html:optionsCollection property="heightOperateList" value="dataValue" label="dataName"/>
        			   </html:select> 
			          <Input type='text' name="heightValue"  class=textbox  size='15' value='${salaryStandardDataForm.heightValue}' >
			          
			           </td>
		  		  </tr>
		  	<logic:equal name="salaryStandardDataForm" property="type" value="D" > 
		  		  <tr> 
		            <td height='30' colspan='3'>
						&nbsp;<html:checkbox   name="salaryStandardDataForm" property="isAccuratelyDay" value="1" ><bean:message key="gz.templateset.preciseDay"/></html:checkbox>
						
					</td>
		          </tr>
		  	</logic:equal>	  
         </table>
    </fieldset>
    </td></tr>
    <tr align='center' ><td><br>
    <input type='button'  class="mybutton" value=' <bean:message key="reporttypelist.confirm"/> '  onclick='enter()'  />		          
    <input type='button'  class="mybutton" value=' <bean:message key="kq.register.kqduration.cancel"/> ' onclick='goback()'  />
    </td></tr>
    </table>
   
   <input type='hidden' name="item"  value="${salaryStandardDataForm.item}" />
   <input type='hidden' name="item_id"  value="${salaryStandardDataForm.item_id}" />
   <input type='hidden' name="type"  value="${salaryStandardDataForm.type}" />
   
   </html:form>
  
   
   <script language='javascript'>
   		 <logic:equal name="salaryStandardDataForm" property="type" value="D" > 
       			validateValue();
         </logic:equal>
   </script>
   
   
  </body>
</html>
