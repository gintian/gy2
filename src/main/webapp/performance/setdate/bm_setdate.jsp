<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<hrms:themes />
<style>
<!--
.hand { cursor:pointer;
}
-->
</style>
<script type="text/javascript">
<!--
	function saveSet() {
		policeForm.action="/performance/setdate/bm_setdate.do?b_save=link&save=save";
		policeForm.target="il_body";
		policeForm.submit();	
	}
	function init(year,quar,moth)
	{
	    if(year!="1")
	    {
	       if(quar=="1"||moth=="1")
	       {
	          var vos= document.getElementById('taskyear');
	          vos.checked=true;
	       }
	    }
	}
	function setvalue(obj,hiddenname)
	{
	   var vos= document.getElementById(hiddenname);
	   if(obj.checked)
	   {
	      vos.value="1";
	      if(hiddenname=="task_cly_quar"||hiddenname=="task_cly_moth")
	      {
	          var voss= document.getElementById("task_cly_year");
	          voss.value="1";
	          voss= document.getElementById("year");
	          voss.checked=true;
	      }
	   }else
	   {
	      vos.value="0";
	   }
	}
//-->
</script>
<html:form action="/performance/setdate/bm_setdate"><br/><br/>
	<table width="50%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
		<thead>
           <tr>
            	<td align="center" class="TableRow" colspan="2" nowrap><bean:message key="police.customization.setdate"/></td>	    	    	    
           </tr>
		</thead>		
        <tbody>          	
          	<tr class="trShallow">
            	<td align="right" class="RecordRow" nowrap><bean:message key="police.workinfo.orgtask"/> </td>            
            	<td align="left" class="RecordRow" nowrap>
            		<logic:equal value="1" name="policeForm" property="task_cly_year">
            			<input id="taskyear" type="checkbox" name="year" checked="checked" value="1" class="hand" onclick="setvalue(this,'task_cly_year');"/><label class="hand" for="taskyear"><bean:message key="police.lable.year"/></label>
            		</logic:equal>
            		<logic:notEqual value="1" name="policeForm" property="task_cly_year">
            			<input id="taskyear" type="checkbox" name="year" value="1" class="hand" onclick="setvalue(this,'task_cly_year');"/><label for="taskyear" class="hand"><bean:message key="police.lable.year"/></label>
            		</logic:notEqual>
            		&nbsp;&nbsp;&nbsp;
            		<logic:equal value="1" name="policeForm" property="task_cly_quar">
            			<input id="taskquar" type="checkbox" name="quar" checked="checked" value="1" class="hand" onclick="setvalue(this,'task_cly_quar');"/><label class="hand" for="taskweek"><bean:message key="police.lable.week"/></label>
            		</logic:equal>
            		<logic:notEqual value="1" name="policeForm" property="task_cly_quar">
            			<input id="taskquar" type="checkbox" name="quar" value="1" class="hand" onclick="setvalue(this,'task_cly_quar');"/><label class="hand" for="taskweek"><bean:message key="police.lable.week"/></label>
            		</logic:notEqual>
            		&nbsp;&nbsp;&nbsp;
            		<logic:equal value="1" name="policeForm" property="task_cly_moth">
            			<input id="taskmonth" type="checkbox" name="moth" checked="checked" value="1" class="hand" onclick="setvalue(this,'task_cly_moth');"/><label class="hand" for="taskmonth"><bean:message key="police.lable.month"/></label>
            		</logic:equal>
            		<logic:notEqual value="1" name="policeForm" property="task_cly_moth">
            			<input id="taskmonth" type="checkbox" name="moth" value="1" class="hand" onclick="setvalue(this,'task_cly_moth');"/><label class="hand" for="taskmonth"><bean:message key="police.lable.month"/></label>
            		</logic:notEqual>
            		 <html:hidden name="policeForm" property="task_cly_year" styleClass="text"/>
            		  <html:hidden name="policeForm" property="task_cly_quar" styleClass="text"/>
            		   <html:hidden name="policeForm" property="task_cly_moth" styleClass="text"/>
            	</td>	    	    	    		        	        	        
          	</tr>          	
          	<tr class="trShallow">
            	<td align="center" class="RecordRow" colspan="2" nowrap>
            		<input class="mybutton" type="button" name="bu" value="<bean:message key="button.submit"/>"  onclick="saveSet()"/>
            	</td>            	    	    	    		        	        	        
          	</tr>
		</tbody>        
	</table>	
</html:form>
<script type="text/javascript">
init("${policeForm.task_cly_year}","${policeForm.task_cly_quar}","${policeForm.task_cly_moth}");
</script>