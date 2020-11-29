<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<script language="javascript" src="/js/validate.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<%
	int i=0;	
	int r=0;
%>
<SCRIPT language=JavaScript>
 
function test(name)
{

   if(name!=null&&name!=""&&name!="undefined"&&name.length>0&&name!="06")
   {
      var obj=$('cardset');
      obj.setSelectedTab(name);
   }
		
}  
</SCRIPT>
<body onload="test('${feastForm.hols_status}')">	
<html:form action="/kq/feast_manage/managerdata">
<logic:equal name="feastForm" property="error_flag_session" value="0">
	<logic:equal name="feastForm" property="error_flag_nbase" value="0">
	    <table border="0" cellspacing="0"  align="left" cellpadding="0" width="100%" >
	      <tr>
	        <td>
	         <hrms:tabset name="cardset" width="100%" height="100%" type="true">         
		 		<logic:iterate id="element" name="feastForm"  property="holi_list" indexId="index">  
			    	<hrms:tab name="${element.dataValue}" label="${element.dataName}" visible="true" url="/kq/feast_manage/managerdata.do?b_search=query&hols_status=${element.dataValue}">
		            </hrms:tab>	
	           </logic:iterate>
		       	<logic:equal name="feastForm" property="isshow" value="1">
			        <hrms:tab label="调休假" name="q33" visible="true" url="/kq/feast_manage/managerdata.do?b_search=link&hols_status=q33">
			        </hrms:tab>
		       	</logic:equal>
		 	 </hrms:tabset>
	        </td>
	      </tr>      
	      <!--<tr>
	      <td width="100%">
	      <hrms:tipwizardbutton flag="workrest" target="il_body" formname="feastForm"/> 
	      </td>
	   	  </tr> -->
		</table> 
	</logic:equal>
</logic:equal>

<logic:notEqual name="feastForm" property="error_flag_session" value="0">
	<script language="javascript">
		var error_str=kqErrorProcess('<bean:write name="feastForm"  property="error_flag_session"/>','<bean:write name="feastForm"  property="error_message_session"/>','<bean:write name="feastForm"  property="error_return"/>');
		document.write(error_str);
	</script>
</logic:notEqual>
<logic:equal value="0" name="feastForm" property="error_flag_session">
	<logic:notEqual value="0" name="feastForm" property="error_flag_nbase">
		<script language="javascript">
			var error_str=kqErrorProcess('<bean:write name="feastForm"  property="error_flag_nbase"/>','<bean:write name="feastForm"  property="error_message_nbase"/>','<bean:write name="feastForm"  property="error_return"/>');
			document.write(error_str);
		</script>
	</logic:notEqual>
</logic:equal> 
</html:form>
</body>