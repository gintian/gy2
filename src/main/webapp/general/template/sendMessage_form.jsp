<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page import="com.hrms.hjsj.utils.Sql_switcher"%>
<%@ page import="com.hrms.struts.valueobject.UserView,
				com.hrms.struts.taglib.CommonData,
				java.util.*"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.general.template.TemplateForm"%>

<%
	TemplateForm templateForm=(TemplateForm)session.getAttribute("templateForm");
	ArrayList    mailTempletList=templateForm.getMailTempletList();
	String       email_staff=templateForm.getEmail_staff();
	String      template_staff=templateForm.getTemplate_staff();
	String      template_bos=templateForm.getTemplate_bos();
	String readOnlyValue="false";
	if(mailTempletList.size()==2)
		readOnlyValue="true";
	if(mailTempletList.size()==1&&((CommonData)mailTempletList.get(0)).getDataValue().equals(template_bos))
		readOnlyValue="true";
	request.setAttribute("readOnlyValue",readOnlyValue);
 %>

<script language="javascript">
	function clearUser()
	{
			$('user_').value="";
	   		$('user_h').value="";    
	}


	function selectobject()
	{
	 var objecttype=$F('objecttype');
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
       		for(var i=0;i<return_vo.length;i++)
       		{
       			var rolevo=return_vo[i];
	   			$('user_').value+=","+rolevo.role_name;
	   			$('user_h').value+=","+objecttype+":"+rolevo.role_id; 
	   		}       	   
	   }
	 }
     
	 if(objecttype=="1")
	 {
     	 var return_vo=select_org_emp_dialog(flag,1,1,0,0,1);   //select_org_emp_dialog(flag,1,1,0);   
		 if(return_vo)
		 {
		 	
	 		var a_temps=return_vo.title.split(",");
	 		var temps=return_vo.content.split(",");
	 		for(var i=0;i<temps.length;i++)
	 		{
	 			if(temps[i].length>0&&temps[i].substr(0,2)!='UN'&&temps[i].substr(0,2)!='UM'&&temps[i].substr(0,2)!='@K')
	 			{
	 				$('user_').value+=","+a_temps[i];
	 				$('user_h').value+=","+objecttype+":"+temps[i];
	 			}
	 		}
	 	}	
	 }else if(objecttype=="3")
	 {
	    var return_vo=select_org_dialog(flag,2,1,0);   
 		 if(return_vo)
		 {
	 		$('user_').value+=","+return_vo.title;
	 		$('user_h').value+=","+objecttype+":"+return_vo.content;
	 	}
	 }	
	 if(objecttype=="4")
	 {
	 	var return_vo=select_user_dialog('1','1');
	 	if(return_vo)
	 	{
	 		var a_temps=return_vo.title.split(",");
	 		var temps=return_vo.content.split(",");
	 		for(var i=0;i<temps.length;i++)
	 		{
	 			if(temps[i].length>0)
	 			{
	 				$('user_').value+=","+a_temps[i];
	 				$('user_h').value+=","+objecttype+":"+temps[i];
	 			}
	 		}
	 				
	 	}
	 }
	 
	  if($('user_').value.length>0&&$('user_').value.substring(0,1)==',')
		   			$('user_').value=$('user_').value.substring(1);
	 
	 
	}
	
	function clearObjs()
	{
		$('user_').value='';
	   	$('user_h').value='';
	
	}
	
	
	
	
	
	
	function getSelectedData()
	{
	   var topic=$F('context');
	   var title=$F('title');
	  // var oEditor = FCKeditorAPI.GetInstance('FCKeditor1');
	  // var topic= oEditor.GetXHTML(true);
	   
	   if(trim(topic).length==0)
	   {
	   		alert("请填写消息内容");
	   		return;
	   }
	   var user_h=$F('user_h');
/*	   if(trim(user_h).length==0)
	   {
	     alert('<bean:message key="error.notselect.object"/>');
	   	 return;
	   }
	*/   
	    var hashvo=new ParameterSet();
       	hashvo.setValue("isSendMessage",'<%=(request.getParameter("isSendMessage"))%>');
        hashvo.setValue("pt_type",'<%=(request.getParameter("pt_type"))%>');
       	hashvo.setValue("context",getEncodeStr(topic)); 
       	hashvo.setValue("title",getEncodeStr(title)); 
       	hashvo.setValue("user_h",getEncodeStr(user_h)); 
       	hashvo.setValue("tabid","<%=(request.getParameter("tabid"))%>");
       	
       <% if(email_staff.equalsIgnoreCase("True")){ %>
       	if(document.templateForm.email_staff_value.checked)
       			hashvo.setValue("email_staff_value","1");
       	else
       		    hashvo.setValue("email_staff_value","0");
       <% } %>	
   	    var request=new Request({asynchronous:false,onSuccess:isSuccess,functionId:'0570010140'},hashvo);   
	}
	
	function isSuccess(outparamters)
    {
		window.close();
	
	}
	
	
	function getMailTempletValue()
	{
		 var hashvo=new ParameterSet();
         hashvo.setValue("id",document.templateForm.mailTempletID.value);
         var request=new Request({asynchronous:false,onSuccess:isSuccess2,functionId:'0570010142'},hashvo);   
	
	}
	
	function isSuccess2(outparamters)
	{
		var subject=getDecodeStr(outparamters.getValue("subject"));
		var content=getDecodeStr(outparamters.getValue("content"));
		document.templateForm.title.value=subject;
		document.templateForm.context.value=content;
	}
	

</script>

<html:form action="/general/template/submit_form">

	<table width="636" border="0" cellpadding="0" cellspacing="0" margin-top="10px" margin-left="5px">
	<!-- 	<tr><td colspan=4 align='center' >
		<font size=3 align='center' ><b>邮&nbsp;件&nbsp;发&nbsp;送</b></font>
		</td>
		</tr>
	 
		<tr><td colspan=4 align='center' >&nbsp;
		</td></tr>-->
		<tr height="20">
		<!--<td width=10 valign="top" class="tableft"></td>
			<td width=150 align=center class="tabcenter">&nbsp;邮&nbsp;件&nbsp;发&nbsp;送&nbsp;</td>
			<td width=10 valign="top" class="tabright"></td>
			<td valign="top" width="600" class="tabremain"></td>   -->	
			<td align="left" colspan="4" class="TableRow">&nbsp;邮&nbsp;件&nbsp;发&nbsp;送&nbsp;</td> 
			
		</tr>
		<tr>
			<td colspan="4" class="framestyle">
				
				<table border="0" cellpmoding="0" cellspacing="0" class="DetailTable" cellpadding="0" width="500">
					<tr><td>&nbsp;</td></tr>
					<% if(mailTempletList.size()==2){ %>
					<tr class="list3">
						<td align="left"  nowrap>&nbsp;邮件模板:&nbsp;</td>
                        <td align="left" >
                       
                       <hrms:optioncollection name="templateForm" property="mailTempletList" collection="list"   />
				       <html:select name="templateForm"  property="mailTempletID" size="1" onchange="getMailTempletValue()"  >
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>
                       
                       
                       
						</td>
					</tr>
					
					<% } %>
					
					<tr class="list3">
						<td align="left"  nowrap>&nbsp;标&nbsp;题:&nbsp;</td>
                        <td align="left" >
                        <INPUT type="text" id="title" value="${templateForm.title}"  <%=(readOnlyValue.equalsIgnoreCase("true")?"readonly":"")%> size="50" maxlength="100">
						</td>
					</tr>
					<tr><td colspan='2' >&nbsp;</td></tr>				
					<tr class="list3">
						<td align="left" valign='top'  nowrap>&nbsp;内&nbsp;容:&nbsp;</td>
                       	<td align="left" >
                       	<html:textarea name="templateForm"   readonly="${readOnlyValue}"     property="context" cols="85" rows="13" />&nbsp;&nbsp;
						</td>
					</tr>
					
					<tr  class="list3" ><td align="left"  nowrap  >&nbsp;收件对象:&nbsp;</td>
						<td>
            				 <html:select name="templateForm" property="objecttype" size="1" onchange="selectobject();">
               					<option value="#" selected="selected"><bean:message key="label.select" /></option>
                                <option value="1"  >人员</option>
                                <option value="2"  >角色</option>
                                <option value="4"  >用户</option>
				        	 </html:select>&nbsp; 	
					<INPUT type="text" id="user_" value="${templateForm.user_}" class="TEXT9" size="48" maxlength="200">
                	<img src='/images/del.gif' title='清空收件人员' onclick='clearObjs()' /> 
                	
                	<INPUT type="hidden" id="user_h" value="${templateForm.user_h}"  size=30>	
					
					</td></tr>
					<% if(email_staff.equalsIgnoreCase("True")){ %>
						<tr><td  >&nbsp;</td>
						<td>
						<input type='checkbox' name='email_staff_value' value='1'  checked >通知本人
						</td></tr>
					<% } %>
					<tr><td colspan='2' >&nbsp;</td></tr>
					
					
					
				</table>
			</td>
		</tr>

		<tr class="list3" height="10">
			<td align="right" nowrap valign="top" colspan="4">
				&nbsp;
			</td>
		</tr>
		<tr class="list3">
			<td align="center" colspan="4">
		
				<button extra="button" onclick="clearUser();">
            		清空
	 	        </button>&nbsp;							
	 	        <button extra="button" onclick="getSelectedData();">
            		<bean:message key="button.ok"/>
	 	        </button>&nbsp;			
	 	        <button extra="button" onclick="window.close();">
            		<bean:message key="button.close"/>
	 	        </button>	
	 	        
                
	 	    </td>
		</tr>
	</table>
<script language="javascript">
	$('objecttype').options[0].selected=true;
</script>
	
</html:form>