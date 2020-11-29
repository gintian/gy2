<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="java.util.*,
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant" %>

<script type="text/javascript">

function isCheckDegree()
{
	var upIsValid = document.getElementById("upIsValid").checked;
	var downIsValid = document.getElementById("downIsValid").checked;
	
	if(upIsValid==true)
		document.getElementById('upDegreeId').disabled=false;
	else
		document.getElementById('upDegreeId').disabled=true;
	
	if(downIsValid==true)
		document.getElementById('downDegreeId').disabled=false;
	else
		document.getElementById('downDegreeId').disabled=true;
	
}

function saveRankTarget()
{
	var upIsValid = document.getElementById("upIsValid").checked;
	var downIsValid = document.getElementById("downIsValid").checked;	
	var upDegreeId = document.getElementById("upDegreeId").value;
	var downDegreeId = document.getElementById("downDegreeId").value;
	
	var thevo = new Object();
	thevo.upIsValid = upIsValid;
	thevo.downIsValid = downIsValid;
	thevo.upDegreeId = upDegreeId;
	thevo.downDegreeId = downDegreeId;
	thevo.flag = "true";
    if(window.showModalDialog) {
        parent.window.returnValue=thevo;
        parent.window.close();
    }else{

        parent.parent.window.opener.window.mustWriteScore_ok(thevo);
        window.open("about:blank","_top").close();

    }
}

function goback()
{
    if(window.showModalDialog) {
        parent.window.close();
    }else{
        window.open("about:blank","_top").close();
    }
}
</script>
<html>
	<hrms:themes />
	<head>
		
	</head>
	<body>
	<logic:equal parameter="method" value="2">
		<html:form action="/performance/kh_plan/kh_params">		
		<Br>
		<Br>
			<fieldset align="center" style="width:90%;margin:0 auto;">
			<div id="tbl-container"  style='height:90px;width:93%;align:center;margin-bottom:10px;'>
			<Br>
				<table border="0" cellspacing="0"  align="center" cellpadding="0" align="center" style="width:93%;">
	          		<tr align="center" > 
	            		<td id="rankTarget_name1">	
	            		<%--
	            			<input type="checkbox" name="upIsValidb" id='upIsValid' value="1" onclick=';' />		
	            			 	--%>						
							<html:checkbox styleId="upIsValid" name="examPlanForm" property="upIsValid" value="1" onclick="isCheckDegree();" />
							
							高于																													
							<html:select name="examPlanForm" property="upDegreeId" size="1" styleId="upDegreeId" style="width:150px">
								<html:option value=""></html:option>																															
								<html:optionsCollection property="grade_template" value="dataValue" label="dataName" />
							</html:select>																				
							&nbsp;	
							<bean:message key='jx.khplan.param2.gradeShowMustFill' />									
						</td>						
	          		</tr>	          			
	          		<tr>
	          			<td>
	          				&nbsp;
	          			</td>
					</tr>	
					<tr>
						<td>
							&nbsp;
	          			</td>
					</tr>					
	        		<tr align="center" > 
	            		<td id="rankTarget_name2">	
	            		<%--
	            			<input type="checkbox" name="downIsValidb" id='downIsValid' value="1" onclick=';' />		
	            			 	--%>							
							<html:checkbox styleId="downIsValid" name="examPlanForm" property="downIsValid" value="1" onclick="isCheckDegree();" />
							
							低于	
							<html:select name="examPlanForm" property="downDegreeId" size="1" styleId="downDegreeId" style="width:150px">
								<html:option value=""></html:option>
								<html:optionsCollection property="grade_template" value="dataValue" label="dataName" />							
							</html:select>								
							&nbsp;	
							<bean:message key='jx.khplan.param2.gradeShowMustFill' />									
						</td>						
	          		</tr>
				</table>	                         		           
		    </div>
		    <br/>
		    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		    <bean:message key='jx.khplan.param2.gradeShowHighorUnder' />		     
		    </fieldset>	
		    <br/>		    
		    <table border="0" cellspacing="0"  align="center" cellpadding="0" >
          		<tr align="center" > 
            		<td style="height:40px">
					    <input type="button" value="确定" id="b_ok" class="mybutton" onclick="saveRankTarget();" />
					    &nbsp;	
					    <input type="button" value="取消" id="b_cansal" class="mybutton" onclick="goback();" />
					</td>
          		</tr>	  
        	</table>
        	<script>
        	isCheckDegree();	        		
        		
        	var theStatus = '${examPlanForm.status}';				
			if(theStatus=='5' || theStatus=='0')				
				document.getElementById("b_ok").disabled=false;
			else
				document.getElementById("b_ok").disabled=true;
        	</script>
	   </html:form>								
	</logic:equal>
	<logic:equal parameter="method" value="1">
		<jsp:include page="planWriteScore_360.jsp"></jsp:include>
	</logic:equal>
   </body>
</html>

