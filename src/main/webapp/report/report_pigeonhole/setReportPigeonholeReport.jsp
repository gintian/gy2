<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<html>
  <head>

  </head>
  <script language='javascript' >
  var count=0;
  function sub()
  {
  		document.reportPigeonholeForm.action="/report/report_pigeonhole/reportBatchPigeonhole.do?b_setReport=set";
  		document.reportPigeonholeForm.submit();
  }
  
  
  
	function selectAll()
	{
		for(var i=0;i<document.reportPigeonholeForm.elements.length;i++)
		{
			if(document.reportPigeonholeForm.elements[i].type=="checkbox")
			{
				if(count%2==0)
					document.reportPigeonholeForm.elements[i].checked=true;
				else
					document.reportPigeonholeForm.elements[i].checked=false;;
			}
		}
		count++;
	}
	
	
	function enter()
	{
		var selectids="";
		for(var i=0;i<document.reportPigeonholeForm.elements.length;i++)
		{
			if(document.reportPigeonholeForm.elements[i].type=="checkbox"&&document.reportPigeonholeForm.elements[i].checked==true)
			{
				selectids+=","+document.reportPigeonholeForm.elements[i].value;
			}
		}
		
		if(selectids.length==0)
		{
			alert(REPORT_INFO58+"！");
			return;
		}
		returnValue=selectids;
		window.close();	
	}

  </script>
  <body>
  <Br>
  <base id="mybase" target="_self">
  <hrms:themes />
  <html:form action="/report/report_pigeonhole/reportBatchPigeonhole">
  		<table width="80%" height='20' border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">

   	  <thead>
   	   <logic:equal name="reportPigeonholeForm" property="operate" value="2">
	     <tr>
		      <td colspan="5" class="TableRow"><bean:message key="report.reportlist.reportsort"/>：
		      		<html:select name="reportPigeonholeForm" property="sortid" onchange='sub()' size="1">
                        <html:optionsCollection property="reportSortList" value="dataValue" label="dataName"/>
        			</html:select>
		        </td>           
		 </tr>
		</logic:equal>
		
           <tr>
            <td align="center" class="TableRow" nowrap width="10%">
				<bean:message key="column.select"/>&nbsp;
	    	</td>         
            <td align="center" class="TableRow" nowrap width="20%">
				ID&nbsp;
	   		 </td>
            <td align="center" class="TableRow" nowrap width="60%">
				<bean:message key="kq.shift.relief.name"/>&nbsp;
	  		  </td>               		        	        	        
           </tr>
   	  </thead>
          
         <% 
         int i=0; 
         
         %> 
       <logic:iterate id="element" name="reportPigeonholeForm" property="infoList"  > 
          <tr class="<%=(i%2==0?"trShallow":"trDeep")%>">
            
            <td align="center" class="RecordRow" nowrap>
            	<input type="checkbox" name="selectedIDs" value="<bean:write name="element" property="id" />">&nbsp;
            </td>            
            <td align="left" class="RecordRow" nowrap>
           		<bean:write name="element" property="id" />&nbsp;
	    	</td>
         
            <td align="left" class="RecordRow" wrap>
         		<bean:write name="element" property="name" />&nbsp;
            </td>
               
          </tr>
			<% i++; %>
        </logic:iterate>  
</table>

<table  width="50%" align="center">
	 <tr>
            <td align="center"> 
              <input type="button" name="b_add" value=" <bean:message key="label.query.selectall"/> " class="mybutton" onClick="selectAll();">
         	  &nbsp;<input type="button" name="b_add" value=" <bean:message key="reporttypelist.confirm"/> " class="mybutton" onClick="enter();">
            </td>
          </tr>   
</table>   
  		
  
  
  
  </html:form>
  </body>
</html>