<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript">
	 function savefield()
	 {
	 	//var dbname = document.getElementById("choice").value;
	 	var hashvo=new ParameterSet();
	 	var dblist = document.getElementsByName("id");
	 	for(var i=0;i<dblist.length;i++){
	 		if(dblist[i].checked==true)
	 			var id = dblist[i].value;
	 	}
	 	hashvo.setValue("id",id);
	 	var request=new Request({method:'post',onSuccess:showSelect,functionId:'05603000022'},hashvo);
	 	window.close();
	 }
	 function showSelect(outparamters)
	 {
	 	 var mess=outparamters.getValue("mess");        
         var thevo=new Object();
		 thevo.mess=mess;
		 window.returnValue=thevo;
		 window.close(); 
	 	
	 } 
</script>
<html:form action="/general/deci/leader/param">
<%int i=0;%>
<table width="95%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap colspan="3">
		<bean:message key="leaderteam.setdb.choicedb"/>&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
   	  	<table>
           		<logic:iterate id="db" name="leaderParamForm" property="dbprelist">
           		<tr>
           			<td nowrap>
           				<input type="radio" name="id" value="<bean:write name="db" property="id"/>">
      						<bean:write name="db" property="name"/>
	           		</td>
	           	</tr>
	           	<%i++;%>
           		</logic:iterate>
           	</table>
          <tr>
          <td align="center" class="RecordRow" nowrap  colspan="3">
               <input type="button" name="btnreturn" value='<bean:message key="button.ok"/>' class="mybutton" onclick=" savefield();">
	     <input type="button" name="btnreturn" value='<bean:message key="button.close"/>' class="mybutton" onclick=" window.close();">
          </td>
          </tr>
</table>
</html:form>