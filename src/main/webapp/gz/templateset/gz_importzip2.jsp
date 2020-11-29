<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>


<html>
  <head>
   
  </head>
  <script language='javascript'>
  	function selectAll()
  	{
  		var num=0;
		for(var i=0;i<document.gztemplateSetForm.elements.length;i++)
  		{
  			if(document.gztemplateSetForm.elements[i].type=='checkbox')
  			{
  				document.gztemplateSetForm.elements[i].checked=true			
  			}
  		}
  	}
  
  	function goback()
  	{
  		document.gztemplateSetForm.action="/gz/templateset/gz_templatelist.do?br_importput=int";
		document.gztemplateSetForm.submit();
  	}
  	
  	
  	function importData(gz_module)
  	{
  		var num=0;
  		var sum=0;
  		var clikbutton=document.getElementById("clickButton");
  		if(clikbutton)
  		{
  		    clikbutton.disabled=true;
  		}
  		var elements = document.getElementsByName("salarySetIDs");
  		var repeats="";
  		for(var i=0;i<elements.length;i++)
  		{
  		     if(elements[i].checked)
  		     {
  		         var obj = document.getElementById("repeat"+i);
  		         for(var j=0;j<obj.options.length;j++)
  		         {
  		            if(obj.options[j].selected)
  		            {
  		                repeats+=","+elements[i].value+"`"+obj.options[j].value+"`"+ document.getElementById("old"+i).value;
  		                if(parseInt(obj.options[j].value) ==1)
  		                {
  		                  sum++;
  		                }
  		            }
  		         }
  		         num++;
  		     }
  		}	
  		if(num==0)
  		{
  		    alert("<bean:message key="gz.templateset.select"/>");
  		     if(clikbutton)
  		     {
  		         clikbutton.disabled=false;
  	        }
  			return;
  		}
  		gztemplateSetForm.isrepeat.value=repeats;
  		if(sum>0)
  		{
  		 if(gz_module=='0')
  		 {
  		   if(confirm("<bean:message key="gz.templateset.confirmegz"/>"))
  		   {
         		document.gztemplateSetForm.action="/gz/templateset/gz_templatelist.do?b_importZip=import";
	        	document.gztemplateSetForm.submit();
  		    }
  		    else
  		    {
  		       if(clikbutton)
  		       {
  		          clikbutton.disabled=false;
  	         	}
  		     }
  		   }
  		   else
  		   {
  		      if(confirm("<bean:message key="gz.templateset.confirmebx"/>"))
  		      {
         		document.gztemplateSetForm.action="/gz/templateset/gz_templatelist.do?b_importZip=import";
	        	document.gztemplateSetForm.submit();
  		       }else{
  		       
  		           if(clikbutton)
  		           {
  		             clikbutton.disabled=false;
  	               }
  		       }
  		   }
  		}else
  		{
  		    document.gztemplateSetForm.action="/gz/templateset/gz_templatelist.do?b_importZip=import";
	        document.gztemplateSetForm.submit();
  		}
  		//window.setTimeout(outputTxt,5000);
 // 		window.location.href=window.location.href+"&import=import";

  		
	  }
  	function outputTxt()
  	{
  		var filename='${gztemplateSetForm.filename}';
  		if(filename!=null && filename!=""){
  			var fieldName = getDecodeStr(filename);
        	var win=open("/servlet/vfsservlet?fileid="+fieldName+"&fromjavafolder=true","txt");
  		}
  	}
  </script>
  <body <%if("import".equals(request.getParameter("b_importZip"))){ %>onload="outputTxt()"<%} %>>
  <html:form action="/gz/templateset/gz_templatelist">
  
    <table width="70%" height='100%' align="center"> 
		<tr> <td valign="top"><Br>
		
		<input type="hidden" name="isrepeat" value=""/>
		<fieldset align="center" style="width:90%;">
    							 <legend > <logic:equal name="gztemplateSetForm" property="gz_module" value="0"><bean:message key="sys.res.gzset"/></logic:equal><logic:equal name="gztemplateSetForm" property="gz_module" value="1"><bean:message key="sys.res.ins_set"/></logic:equal></legend>
		                      
		                      	 <table width="95%" border="0" cellspacing="0"  align="center" cellpadding="0">
		                      	 <tr>
		                      	 <td align="center">
		                      	 <bean:message key="lable.select"/>
		                      	 </td>
		                      	 <td align="left">
		                      	 <logic:equal name="gztemplateSetForm" property="gz_module" value="0"><bean:message key="sys.res.gzset"/></logic:equal><logic:equal name="gztemplateSetForm" property="gz_module" value="1"><bean:message key="sys.res.ins_set"/></logic:equal>
		                      	 </td>
		                      	 <td align="left">
		                      	 <bean:message key="label.gz.submit.type"/>
		                      	 </td>
		                      	 </tr>
		                      	 <% int i=0; %>
								 <logic:iterate  id="element"   name="gztemplateSetForm" property="setlist" >
								  <tr><td align="center">
								<Input type='checkbox' value='<bean:write name="element" property="id" filter="true"/>' name='salarySetIDs' /></td>
								<td align="left"><bean:write name="element" property="name" filter="true"/>
								  </td>
								  <td>
								  <logic:equal name="element" property="isrepeat" value="0">
								  <select name="isrep" id='<%="repeat"+i%>'><option value="0"><bean:message key="gz.templateset.add"/></option></select>
							
								  </logic:equal>
								  <logic:equal value="1" name="element" property="isrepeat">
								   <select name="isrep" id='<%="repeat"+i%>'><option value="1"><bean:message key="gz.templateset.repeat"/></option><option value="0"><bean:message key="gz.templateset.add"/></option></select>
								  
								  </logic:equal>
								    <input type="hidden" name="old" id="<%="old"+i%>" value="<bean:write name="element" property="oldid"/>"/>
								  </td>
								  </tr>
								  <% i++; %>
								 </logic:iterate>
								  <tr><td align='center' colspan="3">
								  <table width="95%" border="0" cellspacing="0"  align="center" cellpadding="0">
								 
									</table>
								  </td></tr>
								 </table>
								 
		</fieldset>
    
    	</td></tr>
    	 <tr>
								  <td align="center" style="padding-top:3px;">
								  <input type='button' id="clickButton" class="mybutton" value="<bean:message key="menu.gz.import"/>"  onclick='importData("${gztemplateSetForm.gz_module}")'  />&nbsp;
									<input type='button' class="mybutton" value="<bean:message key="label.query.selectall"/>"  onclick='selectAll()'  />&nbsp;
									<input type='button' class="mybutton" value="<bean:message key="kq.search_feast.back"/>"  onclick='goback()'  />
									</td>
									</tr>
    </table>
  
    </html:form>
  </body>
</html>
