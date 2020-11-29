<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<hrms:themes></hrms:themes>
<script language="JavaScript" src="/js/calendar.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>

<% int i=0; %>

<script language="javascript">



function goBack()
{
	hmusterForm.action="/general/muster/hmuster/select_muster_name.do?b_query=link&nFlag=${hmusterForm.modelFlag}&relatTableid=${hmusterForm.relatTableid}&condition=${hmusterForm.condition}";
	hmusterForm.submit();

}



function check(flag)
{	

	hmusterForm.historyRecord.value=flag;
	
	var a;
	for(var i=0;i<document.hmusterForm.isAutoCount.length;i++)
	{
		if(document.hmusterForm.isAutoCount[i].checked)
		{
			a=document.hmusterForm.isAutoCount[i].value;
		}
	}
	if(a==1)
	{
		if(document.hmusterForm.pageRows.value=="")
		{
			alert(INPUT_DEVELOP_CELL);
			return;
		}
		if(checkNUM1(hmusterForm.pageRows))	
		{
			document.hmusterForm.action="/general/muster/hmuster/select_muster_name.do?b_next2=b_next2&operateMethod=next&kqtable=${hmusterForm.kqtable}&closeWindow=4";
			document.hmusterForm.submit();
		}
		else
		{
			return;
		}	
		
	}
	else
	{
		document.hmusterForm.action="/general/muster/hmuster/select_muster_name.do?b_next2=b_next2&operateMethod=next&kqtable=${hmusterForm.kqtable}&closeWindow=4";
		document.hmusterForm.submit();
	}
}


</script>



<html:form action="/general/muster/hmuster/select_muster_name" >
<br>	
<br>
		<br>
      <table width="400" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr height="20">
       		<!--  <td width=10 valign="top" class="tableft"></td>
       		<td width=130 align=center class="tabcenter">&nbsp;<bean:message key="hmuster.label.info"/>&nbsp;</td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="500"></td>-->  
       		<td align=center class="TableRow">&nbsp;<bean:message key="hmuster.label.info"/>&nbsp;</td>            	      
          </tr> 
          <tr>
            <td class="framestyle9">
            	<br>

    
             		<table  width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">   
 
		               	<tr>  
		                      <td width="75%">
		                      		<fieldset align="center" style="width:90%;">
    							 <legend><bean:message key="gz.gz_analyse.gz_setinfor.rows"/></legend>
		                      			<table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		                      				<tr>
		                					<td width="20%" height="30" >
		                					
		                						<html:radio name="hmusterForm" property="isAutoCount" value="0"   />
		                					</td>
		                					<td><bean:message key="hmuster.label.auto_count"/></td>
		                      				</tr>
		                      					<tr>
		                					<td width="20%" height="30" >
		                						<html:radio name="hmusterForm" property="isAutoCount" value="1"   />
		                					</td>
		                					<td  nowrap >
			                					<table width="100%">
			                						<tr>
			                							<td width="35%">
			                								<bean:message key="hmuster.label.user_define"/>&nbsp; 
			                							</td>
			                							<td width="65%" >
						                					
						                					<html:text name="hmusterForm" property="pageRows" size="5" maxlength="4" styleClass="text"/>
						                					
			                							</td>
			                						</tr>
			                					</table>
		                					</td>
		                      				</tr>
		                      				<tr>
		                      				<td width="20%" height="25" >
		                						<html:checkbox name="hmusterForm" property="zeroPrint" value="1" />
		                					</td>
		                					<td><bean:message key="hmuster.label.zero_print"/></td>
		                      				</tr>
		                      				<tr>
		                					<td width="20%" height="25" >
		                						<html:checkbox name="hmusterForm" property="printGrid" value="0" />
		                					</td>
		                					<td><bean:message key="inform.muster.not.print.secant"/></td>
		                      			</table>
		                      		</fieldset>
		                      		
		                      </td>
		                      <td align='right' >
				                      &nbsp;       
		                      </td>
		                     
		                </tr>
		        </table>
		        <br>
		</td>
		</tr>
		<tr><td align="left" style="height:35px;">
	<html:button  styleClass="mybutton" property="b_next" onclick="check(0)">
						            		<bean:message key="hmuster.label.reGetData"/>
							 		  </html:button>&nbsp;&nbsp;  
						         	  <html:button  styleClass="mybutton" property="b_next" onclick="check(1)">
						            		<bean:message key="hmuster.label.privGetData"/>
							 		  </html:button>&nbsp;&nbsp;
							 		  <html:button  styleClass="mybutton" property="b_next" onclick="goBack()">
						            		<bean:message key="kq.emp.button.return"/>
							 		  </html:button>&nbsp;
	</td></tr>
	</table>
	<input type='hidden' name='historyRecord' />
          
          
     
</html:form>
