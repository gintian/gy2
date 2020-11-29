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
		for(var i=0;i<document.taxDetailTableForm.elements.length;i++)
  		{
  			if(document.taxDetailTableForm.elements[i].type=='checkbox')
  			{
  				document.taxDetailTableForm.elements[i].checked=true			
  			}
  		}
  	}
  
  	function goback()
  	{
  		document.taxDetailTableForm.action="/gz/templateset/tax_table/initTaxTable.do?br_query=link`opt=0";
		document.taxDetailTableForm.submit();
  	}
  	
  	
  	function importData()
  	{
  		var num=0;
  		var sum=0;
  		var clikbutton=document.getElementById("clickButton");
  		if(clikbutton)
  		{
  		    clikbutton.disabled=true;
  		}
  		var elements = document.getElementsByName("taxIDs");
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
  		    alert("请选择税率表！");
  		     if(clikbutton)
  		     {
  		         clikbutton.disabled=false;
  	        }
  			return;
  		}
  		taxDetailTableForm.repeats.value=repeats;
  		if(sum>0)
  		{
  		   if(confirm("确定覆盖原有税率表吗？"))
  		   {
         		document.taxDetailTableForm.action="/gz/templateset/tax_table/initTaxTable.do?b_upload=upload&opt=2";
	        	document.taxDetailTableForm.submit();
  		    }
  		    else
  		    {
  		       if(clikbutton)
  		       {
  		          clikbutton.disabled=false;
  	         	}
  		    }
  		}else
  		{
  		    document.taxDetailTableForm.action="/gz/templateset/tax_table/initTaxTable.do?b_upload=upload&opt=2";
	        document.taxDetailTableForm.submit();
  		}
	  }

  </script>
  <body>
  <html:form action="/gz/templateset/tax_table/initTaxTable">
  <br>
  <br>
    <table width="50%" height='100%' align="center"> 
		<tr> <td valign="top"><Br>
		
		<input type="hidden" name="repeats" value=""/>
		<fieldset align="center" style="width:90%;">
    							 <legend ><bean:message key="gz.formula.scale"/><!-- 税率表 --></legend>
		                      
		                      	 <table width="95%" border="0" cellspacing="0"  align="center" cellpadding="0">
		                      	 <tr>
		                      	 <td align="center">
		                      	 <bean:message key="lable.select"/>
		                      	 </td>
		                      	 <td align="left">
		                      	 <bean:message key="gz.columns.name"/><!-- 税率名称 -->
		                      	 </td>
		                      	 <td align="left">
		                      	 <bean:message key="label.gz.submit.type"/>
		                      	 </td>
		                      	 </tr>
		                      	 <% int i=0; %>
								 <logic:iterate  id="element"   name="taxDetailTableForm" property="validateList" >
								  <tr><td align="center">
								<Input type='checkbox' value='<bean:write name="element" property="id" filter="true"/>' name='taxIDs' /></td>
								<td align="left"><bean:write name="element" property="name" filter="true"/>
								  </td>
								  <td>
								  <logic:equal name="element" property="taxflag" value="0">
								  <select name="isrep" id='<%="repeat"+i%>'><option value="0"><bean:message key="gz.templateset.add"/></option></select>
								  </logic:equal>
								  <logic:equal value="1" name="element" property="taxflag">
								   <select name="isrep" id='<%="repeat"+i%>'><option value="0"><bean:message key="gz.templateset.add"/></option><option value="1"><bean:message key="gz.templateset.repeat"/></option></select>								  
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
								  <input type='button' id="clickButton" class="mybutton" value="<bean:message key="menu.gz.import"/>"  onclick='importData()'  />
									<input type='button' class="mybutton" value="<bean:message key="label.query.selectall"/>"  onclick='selectAll()'  />
									<input type='button' class="mybutton" value="<bean:message key="kq.search_feast.back"/>"  onclick='goback()'  />
									</td>
									</tr>
    </table>
  
    </html:form>
  </body>
</html>
