<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import=" java.util.*,com.hjsj.hrms.actionform.performance.data_collect.Data_collectForm"%>
<%
	Data_collectForm dcform =(Data_collectForm) session.getAttribute("data_collectForm");
	ArrayList dbList = dcform.getDbList();
	String personScope = dcform. getPersonScope();
%>
<style type="text/css">
	#tff{
		position:absolute;
		top:10%;
		left:20%;
	}
	#tf{
		position:absolute;
		top:10%;
		left:10%;
	}
</style>
<script language="javascript">
   		var a_condStr="";
   		var a_cexpr="";
   		intData();
   		function intData()
   		{		
   			var In_paramters="fieldsetid=${data_collectForm.fieldsetid}"; 	
			var request=new Request({method:'post',asynchronous:true,parameters:In_paramters,onSuccess:returnInfo,functionId:'3020073074'});			
   		}
   		
   		function returnInfo(outparamters)
   		{
   			var condStr = getDecodeStr(outparamters.getValue("condStr"));
   			var cexpr = getDecodeStr(outparamters.getValue("cexpr"));
   			a_condStr=condStr;
   			a_cexpr=cexpr;

   		}
	function simpleCondition()
  	{
  		var info,queryType,dbPre;
	    info="1";
	    dbPre="Usr";
        queryType="1";
        var express=a_cexpr+'|'+a_condStr;
        var strExpression = generalExpressionDialog(info,dbPre,queryType,express);
        if(strExpression)
        {	
        	var temps=strExpression.split("|");
        	document.getElementById("cexpr").value=temps[1];
        	var rr = document.getElementById("cexpr").value;
        	document.getElementById("personScope").value="1";
        	var personScope_s = document.getElementById("personScope_s");
        	personScope_s.checked=true;
        }
  	}
	 function complexCondition()
  	{
  		var personScope = document.getElementById("personScope").value;
  		var cexpr = document.getElementById("cexpr").value;
  		if(""==personScope||"1"==personScope){
  			cexpr="";
  		}
  		var strExpression=generalComplexConditionDialog(cexpr,"0",GZ_TEMPLATESET_LOOKCONDITION,"4");
  		 if(strExpression!=undefined)
        {
			document.getElementById("cexpr").value=strExpression;
			var personScope_c = document.getElementById("personScope_c");
			document.getElementById("personScope").value="2";
			personScope_c.checked=true;
        }
  	}
  	function clearCondition(){
  		 if(confirm(GZ_TEMPLATESET_INFO33+"！"))
  	   {
	  		var arr = new Array();
	  		var sdbid="";
	  		 arr= document.getElementsByName("dbValue");
	  		 for(var i=0;i<arr.length;i++){
	  		 	if(arr[i].checked){
	  		 		sdbid=sdbid+arr[i].value+",";
	  		 	}
	  		 }
	  		 var audit = document.getElementById("audit").value;
	  		 var fieldsetid= document.getElementById("fieldsetid").value;
	  		 var personScope=document.getElementById("personScope").value;
	  		 var cexpr = "";
	  		 var hashvo=new ParameterSet();		
	  		 hashvo.setValue("sdbid",sdbid);
			 hashvo.setValue("audit",audit);
			 hashvo.setValue("fieldsetid",fieldsetid);
			 hashvo.setValue("personScope",personScope);
			 hashvo.setValue("cexpr",cexpr);
			 var request=new Request({method:'post',asynchronous:true,onSuccess:clearpama_ok,functionId:'3020073052'},hashvo);
		}
  	}
  	function clearpama_ok(){
  		alert(WHERE_ALL_CLEAR);
  		data_collectForm.action="/performance/data_collect/data_collect.do?b_setpama=link";
  		data_collectForm.submit();
  	}
  	function setpama_save(){
  		var arr = new Array();
  		var sdbid="";
  		 arr= document.getElementsByName("dbValue");
  		 for(var i=0;i<arr.length;i++){
  		 	if(arr[i].checked){
  		 		sdbid=sdbid+arr[i].value+",";
  		 	}
  		 }
  		 if(""==sdbid){
  		 	alert(ONE_DB_SELECT_MUST);
  		 	return false;
  		 }
  		 var audit = document.getElementById("audit").value;
  		 var fieldsetid= document.getElementById("fieldsetid").value;
  		 var personScope=document.getElementById("personScope").value;
  		 if(""==personScope){
  		 	alert(SET_PERSONSCOPE_MUST);
  		 }
  		 var cexpr = document.getElementById("cexpr").value;
  		 var hashvo=new ParameterSet();		
  		hashvo.setValue("sdbid",sdbid);
		hashvo.setValue("audit",audit);
		hashvo.setValue("fieldsetid",fieldsetid);
		hashvo.setValue("personScope",personScope);
		hashvo.setValue("cexpr",cexpr);
		var request=new Request({method:'post',asynchronous:true,onSuccess:setpama_ok,functionId:'3020073052'},hashvo);
  	}
  	function setpama_ok(){
  		data_collectForm.action="/performance/data_collect/data_collect.do?b_query=link";
  		data_collectForm.submit();
  	}
  	function setpama_cancel(){
  		data_collectForm.action="/performance/data_collect/data_collect.do?b_query=link";
  		data_collectForm.submit();
  	}
</script>
<body >
	<html:form action="/performance/data_collect/data_collect">
		<html:hidden name="data_collectForm" property="cexpr"/>
		<html:hidden name="data_collectForm" property="personScope"/>
		<html:hidden name="data_collectForm" property="fieldsetid"/>
		<logic:empty name="data_collectForm" property="auditList">
<br>
<br>
<br>
		<table>
			<tr>
				<td>&nbsp;&nbsp;&nbsp;</td>
				<td>注意</td>
				<td>：</td>
			</tr>
			<tr>
				<td>&nbsp;&nbsp;&nbsp;</td>
				<td></td>
				<td>1.所选子集必须年月变化子集。</td>
			</tr>
			<tr>
				<td>&nbsp;&nbsp;&nbsp;</td>
				<td></td>
				<td>2.子集中未关联代码类23的指标(审批指标)且该指标要构库。</td>
			</tr>
		</table>


		</logic:empty>
		<logic:notEmpty name="data_collectForm" property="auditList">
		<fieldset align="center" style="width:70%;" style="border:0px">
			<br/>
			<div id="nomal" align="center" style="width:90%;">
			<fieldset align="center" style="width:90%;">
			 <legend ><bean:message key="gz.acount.sp.state"/></legend>
			<table width="90%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
  				<tr>
  					<td width="40%"align="left"><bean:message key="gz.acount.sp.state"/>:
  						<hrms:optioncollection name="data_collectForm" property="auditList" collection="list" />
  						<html:select name="data_collectForm" property="audit" style="width:152">
							<html:options collection="list" property="dataValue" labelProperty="dataName" />
						</html:select>(关联代码类23)
					</td>
  				</tr>
		      </table>
			 <br/>
			 </fieldset>
			 <br/>
			 <br/>
			  <fieldset align="center" style="width:90%;">
			  		<legend ><bean:message key="gz.columns.nbase"/></legend>
			  		<div style="overflow:auto;height:150px;" >
			  			<table width="90%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
			  				<%
			  					for(int i=0;i<dbList.size();i++){
			  						HashMap  tMap = (HashMap)dbList.get(i);
			  				%>
			  					<tr>
			  						<td>
			  							<input type='checkbox' name='dbValue'
			  							 <%
			  								if("1".equals((String)tMap.get("isSelect"))){
			  								%>
												checked value='<%=tMap.get("dbid")%>' />			  								
			  							<%
			  								}
			  							else{
			  							%>
			  								value='<%=tMap.get("dbid")%>' />
			  							<%
			  							}
			  							%>
			  						</td>
			  						<td align="left"><%=tMap.get("dbname")%></td>
			  					</tr>
			  				<%
			  					}
			  				%>
		                	
					    </table>
					    </div>
			  </fieldset>
			  <br>&nbsp;<br>
		                 <fieldset align="center" style="width:90%;">
    							 <legend ><bean:message key="gz.columns.menScope"/></legend>
		                      			<table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		                      				<tr>
			                					<td width="20%" height="25" >
			                		            <input name="personScope" type="radio" id="personScope_s" value="2" 
			                					<%
			                						if("1".equals(personScope)){
			                					%>
			                						checked="checked"
			                					<%
			                						}
			                					%> 
			                					 />
			                					 <bean:message key="gz.templateset.simpleCondition"/>
			                		            <Input type='button' value='...'  class="mybutton"  onclick="simpleCondition();"/>&nbsp;&nbsp;&nbsp;&nbsp;
			                		            <Input type='button' value='<bean:message key="button.clearup"/>'  class="mybutton"  onclick="clearCondition();" />
			                					</td>
			                				</tr>
			                				<tr>
			                					<td width="20%" height="25" >
			                					<input name="personScope" type="radio" id="personScope_c" value="2" 
			                					<%
			                						if("2".equals(personScope)){
			                					%>
			                						checked="checked"
			                					<%
			                						}
			                					%> 
			                					 />
			                		            <bean:message key="gz.templateset.complexCondition"/>&nbsp;
			                		            <Input type='button' value='...'  class="mybutton"  onclick="complexCondition();"/>
			                					</td>
			                				</tr>
		                      			</table>
		                      			<br>
		                 </fieldset>
			  
			  
			  <br/> <br/>

			  <table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">  
				<tr>
					<td width="10%" align="center">
	     				<Input type='button' value='<bean:message key="lable.tz_template.enter"/>'  class="mybutton"  onclick="setpama_save()" /> 
	    	 				&nbsp;&nbsp;&nbsp;
	  	 				<Input type='button' value='<bean:message key="lable.tz_template.cancel"/>'  class="mybutton"  onclick="setpama_cancel()" /> 
	  	 			</td>
	  	 	  </tr>
  	 		</table>

  	 		</div>
		</fieldset>
	</logic:notEmpty>
	</html:form>
</body>