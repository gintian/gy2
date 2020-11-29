<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="javascript">
	function selectobject()
	{ var objecttype=$F('objecttype');
	 var flag=0;
	 if(objecttype=="#")
	   return;
	 if(objecttype=="1") 
	 {
	   flag=1;
	 }
	 else if(objecttype=="2")
	 { 
       var return_vo=select_role_dialog(1);
       if(return_vo&&return_vo.length>0)
       { 
       		var rolevo=return_vo[0];
	   		$('user_').value=rolevo.role_name;
	   		$('user_h').value=rolevo.role_id;        	   
	   }
	 }
	if(objecttype=="1")
	 {
	     var return_vo=select_org_emp_dialog(flag,2,1,0,0,1);   //select_org_emp_dialog(flag,2,1,0);   
 		 if(return_vo)
		 {
	 		$('user_').value=return_vo.title;
	 		$('user_h').value=return_vo.content;
	 	}	
	 }else if(objecttype=="3")
	 {
	    var return_vo=select_org_dialog(flag,2,1,0);   
 		 if(return_vo)
		 {
	 		$('user_').value=return_vo.title;
	 		$('user_h').value=return_vo.content;
	 	}
	 }	
	 if(objecttype=="4")
	 {
	 	var return_vo=select_user_dialog('1',2);
	 	if(return_vo)
	 	{
	 		$('user_').value=return_vo.title;
	 		$('user_h').value=return_vo.content;	 		
	 	}
	 	}
	 }
	 
	
	function getSelectedData()
	{
	   var obj=new Object();
	   obj.objecttype=$F('objecttype');
	   obj.name=$F('user_h')
	   obj.fullname=$F('user_');
	   if($F('nodenamenew').indexOf('&')!=-1)
	   {
	   		alert("节点名称不能包含'&'符号!");
	   		return;
	   }
	   if($F('nodenamenew').indexOf(',')!=-1)
	   {
	   		alert("节点名称不能包含','符号!");
	   		return;
	   }
	   if($F('nodenamenew').indexOf('\'')!=-1)
	   {
	   		alert("节点名称不能包含\'符号!");
	   		return;
	   }
	   if($F('nodenamenew').indexOf('\"')!=-1)
	   {
	   		alert("节点名称不能包含'\"'符号!");
	   		return;
	   }
	   obj.nodename=$F('nodenamenew');
	   obj.node_id=$F('node_id');
	   obj.tabid=$F('tabid');
	   if(obj.objecttype=="#"||obj.name=="")
	   {
	     alert('<bean:message key="error.notselect.object"/>');
	   	 return;
	   }
	   returnValue=obj;
	   window.close();	
	}

</script>


<html:form action="/general/template/nodedefine/wf_node_define">
	<br>
	<table width="400" border="0" cellpadding="0" cellspacing="0" align="center">
		<tr height="20">
			<!-- td width=10 valign="top" class="tableft"></td>
			<td width=130 align=center class="tabcenter">&nbsp;<bean:message key="rsbd.task.topic" />&nbsp;</td>
			<td width=10 valign="top" class="tabright"></td>
			<td valign="top" width="450" class="tabremain"></td -->
			<td align="left" colspan="4" class="TableRow">&nbsp;<bean:message key="rsbd.task.topic"/>&nbsp;</td> 
		</tr>
		<tr>
			<td colspan="4" class="framestyle3">
				<table border="0" cellpmoding="0" cellspacing="0" class="DetailTable" cellpadding="0" width="400">
					<tr>
					   <td align="right">	
					       	<bean:message key="general.template.nodedefine.nodename"/>&nbsp;  	 							        	   				        	   				
					    </td>
					     <td align="left">	
					       	<html:text name="nodeDefineForm" property="nodenamenew"/>	 
					       	<html:hidden name="nodeDefineForm" property="node_id"/>	
					       	<html:hidden name="nodeDefineForm" property="tabid"/>								        	   				        	   				
					    </td>							
					</tr>					
				</table>
			</td>
		</tr>

		<tr class="list3" height="10">
			<td align="right" nowrap valign="top" colspan="4">
				&nbsp;
			</td>
		</tr>
		<tr class="list3">
			<td align="left" colspan="4">
			<bean:message key="rsbd.task.selectobject" />
            				 <html:select name="nodeDefineForm" property="objecttype" size="1" onchange="selectobject();">
               					<option value="#" selected="selected"><bean:message key="label.select" /></option>
                                <html:optionsCollection property="rolelist" value="codeitem" label="codename"/>
				        	 </html:select>&nbsp; 							
	 	        <button extra="button" onclick="getSelectedData();">
            		<bean:message key="button.ok"/>
	 	        </button>&nbsp;			
	 	        <button extra="button" onclick="window.close();">
            		<bean:message key="button.close"/>
	 	        </button>	
	 	        <br>
                <INPUT type="text" id="user_" value="" class="TEXT9" size="48" maxlength="200">
                <INPUT type="hidden" id="user_h" value=""  size=30>	
	 	    </td>
		</tr>
	</table>
</html:form>

