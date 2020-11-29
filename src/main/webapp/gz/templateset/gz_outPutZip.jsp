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
  		document.gztemplateSetForm.action="/gz/templateset/gz_templatelist.do?b_query=link";
		document.gztemplateSetForm.submit();
  	}
  	
  	
  	function output()
  	{
  		var ids="";
  		for(var i=0;i<document.gztemplateSetForm.elements.length;i++)
  		{
  			if(document.gztemplateSetForm.elements[i].type=='checkbox')
  			{
  				if(document.gztemplateSetForm.elements[i].checked==true)
  				{
  					ids=ids+"#"+document.gztemplateSetForm.elements[i].value;
  				} 				
  			}
  		}
  		
  		if(ids.length==0)
  			return;
  		var In_paramters="salaryid="+ids.substring(1); 
		var request=new Request({method:'post',asynchronous:true,parameters:In_paramters,onSuccess:returnInfo,functionId:'3020010120'});			
  	
  	}
  
	  function returnInfo(outparamters)
	  {
			var outName=outparamters.getValue("outName");
			var fieldName = getDecodeStr(outName);
			var win=open("/servlet/vfsservlet?fileid="+fieldName+"&fromjavafolder=true","zip");
	  }
  	
  </script>
  <body>
  <html:form action="/gz/templateset/gz_templatelist">
  
    <table width="70%" height='100%' align="center"> 
		<tr> <td valign="top"><Br>
		
		
		<fieldset align="center" style="width:90%;">
    							 <legend >
    							  <logic:equal name="gztemplateSetForm" property="gz_module" value="0">
    							  <bean:message key="sys.res.gzset"/>
    							 </logic:equal>
    							  <logic:equal name="gztemplateSetForm" property="gz_module" value="1">
    							   <bean:message key="sys.res.ins_set"/>
    							  </logic:equal>
    							 </legend>
		                      
		                      	 <table width="90%" border="0" cellspacing="1"  align="center" cellpadding="1" >
								 <logic:iterate  id="element"   name="gztemplateSetForm" property="setlist" >
								  <tr><td>
								<Input type='checkbox' value='<bean:write name="element" property="salaryid" filter="true"/>' name='a' /><bean:write name="element" property="cname" filter="true"/>
								  </td></tr>
								 </logic:iterate>
								  <tr><td align='center' >
								  
								  <input type='button' class="mybutton" value="<bean:message key="sys.export.derived"/>"  onclick='output()'  />&nbsp;
									<input type='button' class="mybutton" value="<bean:message key="label.query.selectall"/>"  onclick='selectAll()'  />&nbsp;
									<input type='button' class="mybutton" value="<bean:message key="kq.search_feast.back"/>"  onclick='goback()'  />
								  </td></tr>
								 </table>
								 
		</fieldset>
    
    	</td></tr>
    </table>
  
    </html:form>
  </body>
</html>
