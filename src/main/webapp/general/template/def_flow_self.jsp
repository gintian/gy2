<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.general.template.TemplateForm" %>
<%@ page import="com.hjsj.hrms.actionform.general.template.DefFlowSelfForm" %>
<%@ page import="com.hjsj.hrms.actionform.general.template.TemplateListForm" %>
<%
DefFlowSelfForm defFlowSelfForm = (DefFlowSelfForm)session.getAttribute("defFlowSelfForm");
%>
<style id=iframeCss>

div{
	font-size:12px;
   }
a{
text-decoration:none;color:black;font-size:12px;
}

a.a1:active {
	color: #003100;
	text-decoration: none;
}
a.a1:hover {
	color: #FFCC00;
	text-decoration: none;
}
a.a1:visited {	
	text-decoration: none;
}
a.a1:link {
	color: #003100;
	text-decoration: none;
}
</style>

<script language='javascript' > 
	var strXml="${defFlowSelfForm.defFlowSelfXml}";
	var fromflag="${defFlowSelfForm.fromflag}";

	if ((fromflag=="card") || (fromflag=="myapply")){	
		var tabid='${templateForm.tabid}';
		var sp_mode='${templateForm.sp_mode}';
		var nextNodeStr="${templateForm.nextNodeStr}";
		var returnflag="${templateForm.returnflag}";
		var sp_batch="${templateForm.sp_batch}";
		var fromtaskid='${templateForm.taskid}';
		var fromins_id='${templateForm.ins_id}';
	}
	else {
		var tabid='${templateListForm.tabid}';
		var returnflag="${templateListForm.returnflag}";
		var sp_batch="${templateListForm.sp_batch}";
		var warn_id="${templateListForm.warn_id}";
		var batch_task="${templateListForm.tasklist_str}";
		var fromtaskid='${templateListForm.task_id}';
		var fromins_id='${templateListForm.ins_id}';
	}
	var taskid='0';
	var ins_id='-1';
	var node_id ='0';

</script>

<script language="JavaScript" src="template.js"></script>
<script language="JavaScript" src="def_flow_self.js"></script>
<html:form action="/general/template/def_flow_self">
<br>
<table align="center"  width="80%">
 <tr>
  <td>
	<fieldset style="width: 100%;align:center">
		<legend>
			<bean:message key="tab.label.defflow" />
		</legend>
		<div style="height:400px;width:99% ;overflow: auto;border:1px solid  #eee;align:center;margin:2px">
		<table id="tblDefFlowSelf" width="100%" border="0" cellspacing="0" 
		           align="center" cellpadding="0" class="ListTable">
		   	  <thead>
		           <tr>
		            	<td align="center" class="TableRow" nowrap width="10%">
							<input type="checkbox" name="selbox" onclick="batch_select(this,'select');" title='<bean:message key="label.query.selectall"/>'>
			    		</td>         
		            	<td align="center" class="TableRow" nowrap width="10%">
							<bean:message key="t_template.approve.selfdefflowLevel"/>
			    		</td>
		            	<td align="center" colspan=2 class="TableRow" nowrap width="80%">
		            	
							<bean:message key="general.template.nodedefine.nodeapprove"/>
			    		</td>          
	    		        	        	        
		           </tr>
		   	  </thead>	   	  
	          <hrms:extenditerate id="element" name="defFlowSelfForm" property="defFlowSelfListform.list" 
	              indexes="indexes"  pagination="defFlowSelfListform.pagination" pageCount="1000"
	                        scope="session">
	            <bean:define id="levelnum" name="element" property="levelnum" />           
	            <bean:define id="bsflag" name="element" property="bsflag" />           
	          <tr onclick='tr_onclick(this,"#F3F5FC");'>            		        	
	            <td width="8%" align="center" class="RecordRow" nowrap>
	            	<logic:equal name="element" property="canCheck" value="1">
	   					<input type="checkbox" name="select" value="true" />
	   				</logic:equal>	  	
	   			    <logic:equal name="element" property="canCheck" value="0">
	   					&nbsp;
	   				</logic:equal>		
		    	</td>
   
	            <td width="14%"  align="left" class="RecordRow">	
	            &nbsp;   
                   <input type="hidden" name="levels" value="${levelnum}">          
                   <bean:write  name="element" property="levelDesc" filter="true"/>
	            </td>
	            <td width="70%" align="left" class="RecordRow" >   	            	         
                    <logic:iterate id="person" name="element" property="personlist" indexId="index"  > 
                       <bean:define id="id" name="person" property="id" />  
                       &nbsp;
	                    <bean:write name='person' property='personname' filter='true'/>   
	                      <img src="/images/icon_fbyjs.gif"  onclick="javascript:delPerson('${id}');" 
	                                   border=0 style="cursor:hand">   
	                
	                </logic:iterate>              
	            </td>
	            <td width="8%" align="center" class="RecordRow" nowrap>     
	                <img src="/images/edit.gif" onclick="javascript:addPerson('${bsflag}','${levelnum}')" 
	                     border=0 style="cursor:hand" >
	            </td>
	        
	          </tr>
	        </hrms:extenditerate>	        
		</table>
		</div>
		<table  width="90%" align="center">
          <tr>
            <td align="left">
         		<input type="button" class="mybutton" value="<bean:message key='button.insert'/>" onclick="addLevel();">
	 			<input type="button" class="mybutton" value="<bean:message key='button.delete'/>" onclick="delLevel();">
	 			<input type="button" class="mybutton" value="<bean:message key='button.return'/>" 
	 			   onclick="returnMain('${defFlowSelfForm.fromflag}');">
	 	    
	 	    </td>
          </tr>          
		</table>
	</fieldset>
   </td>
  </tr>
</table>
</html:form>
