<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html>
  <head>
    
    
  </head>
  <script language='javascript'>
  function initDocument()
  {
	 var In_paramters="opt=1";  	 	  
	 var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showFieldSetList,functionId:'3020030010'});
  
  }
  
  function showFieldSetList(outparamters)
  {
		var fieldlist=outparamters.getValue("list");	
		AjaxBind.bind(gztemplateSetForm.fieldSet,fieldlist);
  }
  
  function showFieldList()
  {
  	if(document.gztemplateSetForm.fieldSet.value!='')
  	{
  		var hashvo=new ParameterSet();	
	  	var In_paramters="opt=2";  
	  	hashvo.setValue("fieldSetid",document.gztemplateSetForm.fieldSet.value);
	  	hashvo.setValue("salaryid",'${gztemplateSetForm.salaryid}');
	    var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showFieldList2,functionId:'3020030010'},hashvo);
  	}
  }
  
  
  function showFieldList2(outparamters)
  {
		var fieldlist=outparamters.getValue("list");	
		AjaxBind.bind(gztemplateSetForm.left_fields,fieldlist);
  }
  
  
  function savecode()
  {		
   		var objlist=new Array(); 	 	
   	 	for(var i=0;i<gztemplateSetForm.salarySetIDs.options.length;i++)
   	 	{
   	 		var temp=gztemplateSetForm.salarySetIDs.options[i].value;
   	 		var temp2=gztemplateSetForm.salarySetIDs.options[i].text;
   	 		var num=0;
   	 		for(var j=0;j<gztemplateSetForm.salarySetIDs.options.length;j++)
   	 		{
   	 			if(gztemplateSetForm.salarySetIDs.options[j].value==temp)
   	 				num++;
   	 		}
   	 		if(num>1)
   	 		{
   	 			alert(temp2+" "+ITEM_NOT_RESET+"！");
   	 			return;
   	 		}
   	 	
   	 		objlist.push(gztemplateSetForm.salarySetIDs.options[i].value); 			
   	 	}	
   	 	if(objlist.length==0)
   	 		window.close();
   	 	else
   	 	{
   	 		setselectitem('salarySetIDs');
   	 		document.getElementById("saveb").disabled=true;
   	 		document.gztemplateSetForm.action="/gz/templateset/salaryItem.do?b_add=add";
  			document.gztemplateSetForm.submit();
   	 	}	 	
   	  // 	returnValue=objlist;
	      
    	  	
   }
   <%  
   if(request.getParameter("b_add")!=null&&request.getParameter("b_add").equals("add"))
   {
   %>
   returnValue="1";
   window.close();
   <%
   }
   %>
   
  
  function goback()
  {
  	 window.close();
  }
  
  </script>
  <body>
   <html:form action="/gz/templateset/salaryItem">
   
   
<table width="525px" align="center" border="0" cellpadding="0" cellspacing="0">
  <tr>  
    <td valign="top" align="center"  >  
     <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTableF">
   	  <thead>
       <tr>
        <td align="center" class="TableRow" nowrap colspan="3"><bean:message key="button.new.add"/><logic:equal name="gztemplateSetForm" property="gz_module" value="0"><bean:message key="gz.report.salary"/></logic:equal><logic:equal name="gztemplateSetForm" property="gz_module" value="1"><bean:message key="gz.report.welfare"/></logic:equal><bean:message key="gz.formula.project"/>
         </td>            	        	        	        
        </tr>
   	  </thead>
   	   <tr>
   
        <td width="100%" align="center"  nowrap>
          <table >
            <tr>
             <td align="center"  width="46%">
               <table align="center" width="100%">              
                <tr>
                 <td align="center" >
                  
                  	<select name='fieldSet' style="width:100%;font-size:9pt"  onchange="showFieldList()" >
                  	
                  	</select>
                                       
                  </td>
                 </tr>
                <tr>
                 <td align="center" >
                  <select name="left_fields" multiple="multiple" size="10" ondblclick="additem('left_fields','salarySetIDs');" style="height:205px;width:100%;font-size:9pt">
                   </select>
                   </td>
                  </tr>
                 </table>
                </td>
               
               <td width="8%" align="center">  
	            <html:button  styleClass="mybutton" property="b_addfield" onclick="additem('left_fields','salarySetIDs');">
            		     <bean:message key="button.setfield.addfield"/> 
	            </html:button >
	            <br>
	            <br>
	            <html:button  styleClass="mybutton" property="b_delfield" onclick="removeitem('salarySetIDs');">
            		     <bean:message key="button.setfield.delfield"/>    
	            </html:button >	
                </td>         
                <td width="46%" align="center">

                 <table width="100%">                
                  <tr>
                  <td width="100%" align="left">
     	            
 		     <select name="salarySetIDs" multiple="multiple" size="10"  ondblclick="removeitem('salarySetIDs');" style="height:230px;width:100%;font-size:9pt;">
                     </select>            
 		                 
                   </td>
                  </tr>
                 </table>             
                </td>               
                </tr>
              </table>             
            </td>
            </tr>
          <tr>      
          <td align="center" class="RecordRow" nowrap  colspan="3">

	  	
       	<html:button  styleClass="mybutton" property="b_save" styleId="saveb" onclick="savecode()">
            		    <bean:message key="button.ok"/>
	        </html:button >&nbsp;
         <input type='button' class="mybutton" value="<bean:message key="kq.register.kqduration.cancel"/>"  onclick='goback()'  />
       
         </td>
        </tr>   
     </table>
   </td>
  </tr>
</table>
   
   </html:form> 
   
   <script language='javascript' >
   
   initDocument();
   </script>
  </body>
</html>
