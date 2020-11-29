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
	String isSendMessage=templateForm.getIsSendMessage();	
	String email_staff=templateForm.getEmail_staff();
	String infor_type=templateForm.getInfor_type();
	String signLogo=templateForm.getSignLogo();
	String actor_type=templateForm.getActor_type();
%>

<script language="javascript">

	function validateValue()
	{
		 var hashvo=new ParameterSet();
		 hashvo.setValue("value",$('user_h').value);  	      
		 var request=new Request({asynchronous:false,onSuccess:validatesuccess,functionId:'0570010144'},hashvo);
	}
	
	function setNull()
	{
		document.getElementById("specialOperate").innerHTML="";
	
	}
	
	function validatesuccess(outparamters)
	{
		/*
		
		 var flag=outparamters.getValue("flag");
		 if(flag!='0')
		 {
		 	var str="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
		 	str+="<input type='checkbox'  name='specialOperateFlag'   checked value='1' />业务模板中人员需要报送给各自领导进行审批处理 ";
		 	document.getElementById("specialOperate").innerHTML=str;
		 
		 }
		 else 
		 	setNull();
	   */
	}
	
	
	function selectobject3()
	{
	 	var sp_mainbody = $F('sp_mainbody');
	 	if(sp_mainbody=="#")
	 	{
	 		$('user_').value = '';
	   		$('user_h').value = '';
	   		return;
	   	}
       	if(sp_mainbody && sp_mainbody.length>0)
       	{ 
       		var temps = sp_mainbody.split("`");
	   		$('user_').value = temps[1];
	   		$('user_h').value = temps[0];       	   			   		 
	   	}	   	
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
       		var rolevo=return_vo[0];
	   		$('user_').value=rolevo.role_name;
	   		$('user_h').value=rolevo.role_id;       
	   		<% if(infor_type.equals("1")){ %>
	   		validateValue();
	   		<% } %>
	   		 
	   }
	 }
     
	 if(objecttype=="1")
	 {
     	 var return_vo=select_org_emp_dialog5(flag,2,1,0,1,0,1,1);   
		 if(return_vo)
		 {
	 		$('user_').value=return_vo.title;
	 		$('user_h').value=return_vo.content;
	 //		setNull();
	 	}	
	 }else if(objecttype=="3")
	 {
	    var return_vo=select_org_dialog(flag,2,1,0);   
 		 if(return_vo)
		 {
	 		$('user_').value=return_vo.title;
	 		$('user_h').value=return_vo.content;
	 //		setNull();
	 	}
	 }	
	 if(objecttype=="4")
	 {
	 	var return_vo=select_user_dialog('1','2');
	 	if(return_vo)
	 	{
	 		$('user_').value=return_vo.title;
	 		$('user_h').value=return_vo.content;
	 //		setNull();	 		
	 	}
	 }
	}
	
	
	
	function selectobject2()
	{
	 var objecttype=$F('objecttype2');
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
	   			$('user_s').value+=","+rolevo.role_name;
	   			$('user_h_s').value+=","+objecttype+":"+rolevo.role_id; 
	   			
	   		}        	   
	   }
	 }
     
	 if(objecttype=="1")
	 {
     	
	 	var return_vo=select_org_emp_dialog2(flag,1,1,0,0,1);   
		if(return_vo)
		{
		 	
	 		var a_temps=return_vo.title.split(",");
	 		var temps=return_vo.content.split(",");
	 		for(var i=0;i<temps.length;i++)
	 		{
	 			if(temps[i].length>0&&temps[i].substr(0,2)!='UN'&&temps[i].substr(0,2)!='UM'&&temps[i].substr(0,2)!='@K')
	 			{
	 				$('user_s').value+=","+a_temps[i];
	 				$('user_h_s').value+=","+objecttype+":"+temps[i];
	 			}
	 		}
	 	}	
	 	
	 }else if(objecttype=="3")
	 {
	    var return_vo=select_org_dialog(flag,2,1,0);   
 		 if(return_vo)
		 {
	 		$('user_s').value+=return_vo.title;
	 		$('user_h_s').value+=return_vo.content;
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
	 				$('user_s').value+=","+a_temps[i];
	 				$('user_h_s').value+=","+objecttype+":"+temps[i];
	 			}
	 		}
	 				
	 	}
	 }
	 
	 if($('user_s').value.length>0&&$('user_s').value.substring(0,1)==',')
		   			$('user_s').value=$('user_s').value.substring(1);
	 
	}
	
	
	
	
	
	function getSelectedData()
	{
	   var obj=new Object();
	   <% if(signLogo!=null && signLogo.equalsIgnoreCase("hand")){ %>
	   		obj.objecttype='1';
	   		<% if(actor_type!=null && actor_type.equalsIgnoreCase("1")){ %>
				obj.objecttype='1';
	  		<% }else if(actor_type!=null && actor_type.equalsIgnoreCase("4")){%>
				obj.objecttype='4';
			<% } %>
	   <% }else{ %>
	   		obj.objecttype=$F('objecttype');
	   <% } %>
	   obj.name=$F('user_h')
	   obj.fullname=$F('user_');
	   obj.pri=$F('emergency'); 
	   var content="";
	   obj.content = content;
	   obj.sp_yj=$F('sp_yj');
	   
	   <% if(!request.getParameter("allow_def_flow_self").equals("true")&&(request.getParameter("noObject")==null||request.getParameter("noObject").equals("0"))){ %>
	   if(obj.objecttype=="#"||obj.name=="")
	   {
	     alert('<bean:message key="error.notselect.object"/>');
	   	 return;
	   }
	   <% } %>
	   if(obj.pri=="#")
		 obj.pri="1";
	   if(obj.sp_yj=="#")
		 obj.sp_yj="01";	   
	   obj.isSendMessage='<%=isSendMessage%>';
	   <% if(!isSendMessage.equals("0")){ %>
	   		
	   		obj.user_s=$F('user_s');
	   		obj.user_h_s=$F('user_h_s');
	   		<% if(email_staff.equalsIgnoreCase("True")){ %>
	   		if(document.templateForm.email_staff_value.checked)	
		   		obj.email_staff_value='1';
		   	else
		   		obj.email_staff_value='0';
	   		
	   		<% }else{ %>
	   		 obj.email_staff_value='0';
	   		<% } %>
	   <% } %>
	   
	   /*
	   if(eval("document.templateForm.specialOperateFlag"))
	   {
	   		if(document.getElementsByName("specialOperateFlag")[0].checked)
	   			obj.specialOperate='1';
	   		else
	   			obj.specialOperate='0';
	   }
	   else */
	   		
	   	obj.specialOperate='0';
	   		
	
	   returnValue=obj;
	   window.close();	
	}


	function clearObjs()
	{
		$('user_s').value='${templateForm.user_}';
	   	$('user_h_s').value='${templateForm.user_h}';
	
	}
	
	
	
	
	
</script>
<script type="text/javascript" src="/fckeditor/fckeditor.js"></script>

<html:form action="/general/template/submit_form">
	<br>
	<table width="450" border="0" cellpadding="0" cellspacing="0" align="center">
		<tr height="20">
			<!--  td width=10 valign="top" class="tableft"></td>
			<td width=130 align=center class="tabcenter">&nbsp;<bean:message key="rsbd.task.topic" />&nbsp;</td>
			<td width=10 valign="top" class="tabright"></td>
			<td valign="top" width="600" class="tabremain"></td-->
			<td align="left" colspan="4" class="TableRow">&nbsp;<bean:message key="rsbd.task.topic"/>&nbsp;</td> 
		</tr>
		<tr>
			<td colspan="4" class="framestyle3">
				<table border="0"  cellspacing="5" class="DetailTable" cellpadding="0" width="400">
					<tr class="list3">
						<td align="right">
			 				<bean:message key="rsbd.task.emergency" /><!-- 优先级 -->
			 		    </td>
			 		    <td align="left"> 			
            				 <html:select name="templateForm" property="emergency" size="1">
               					<option value="#" selected="selected"><bean:message key="label.select" /></option>
                                <html:optionsCollection property="emergencylist" value="codeitem" label="codename"/>
				        	 </html:select>
				       </td>   	 				        	   				        	   				
					</tr>
					<tr class="list3">
						<td align="right"  nowrap>
						  <bean:message key="rsbd.task.idea" />   <!-- 审批意见 -->                            
						</td>
						<td align="left">
						  <html:select name="templateForm" property="sp_yj" size="1">
                                <option value="#" selected="selected"><bean:message key="label.select" /></option>
                                <html:optionsCollection property="sp_yjlist" value="codeitem" label="codename"/>
                             </html:select>
						</td>
					</tr>
					<% if(!request.getParameter("allow_def_flow_self").equals("true")&&(request.getParameter("noObject")==null||request.getParameter("noObject").equals("0"))){ %>
					<tr class="list3"><!--noObject=1表示的是自定义审批流程  ||自动流转-->
						<td align="right" nowrap>
						<bean:message key="rsbd.task.selectobject" /><!--报送对象（现在这个情况看来只有手动流转才会有）  -->
						</td>
						<td align="left">
						                 <% if(signLogo!=null && signLogo.equalsIgnoreCase("hand")){ %>
			            				 	<html:select name="templateForm" property="sp_mainbody" size="1" onchange="selectobject3();">
			            				 		<option value="#" selected="selected"><bean:message key="label.select" />&nbsp;&nbsp;</option>
				               					<html:optionsCollection property="rolelist" value="dataValue" label="dataName" />
							        	 	</html:select>&nbsp;
							        	 <% }else{ %>
							        	 	<html:select name="templateForm" property="objecttype" size="1" onchange="selectobject();">
				               					<option value="#" selected="selected"><bean:message key="label.select" /></option>
				                                <html:optionsCollection property="rolelist" value="codeitem" label="codename"/>
							        	 	</html:select>&nbsp;
							        	 <% } %> 
			                <INPUT type="text" id="user_" value=""     class="TEXT" size="18" maxlength="200">
			                <INPUT type="hidden" id="user_h" value=""   size=30>	
				 	    </td>
					</tr>
					<% } %>      	 
					<% if(!isSendMessage.equals("0")){ %>
					<tr class="list3">
						<td align="right"  style="padding-left:7px;padding-top:5px" >
						邮件抄送
						</td>
						<td>
						<select name="objecttype2"   onchange="selectobject2();">
			               					<option value="#" selected="selected"><bean:message key="label.select" />&nbsp;&nbsp;</option>
			                                <option value="1"  >人员</option>
			                                <option value="2"  >角色</option>
			                                <option value="4"  >用户</option>
							        	 </select>&nbsp; 
						<INPUT type="text" id="user_s" value="${templateForm.user_}"  class="TEXT"  size="30"  >
						<img src='/images/del.gif' title='清空抄送人员' onclick='clearObjs()' /> 
			                <INPUT type="hidden" id="user_h_s" value="${templateForm.user_h}"  size=30>	
						</td>
					</tr>
					<% if(email_staff.equalsIgnoreCase("True")){ %>
						<tr class="list3"   <%=((infor_type.equals("2")||infor_type.equals("3"))?"style='display:none'":"")%>>
						<td colspan="2" align="left">
						<input type='checkbox' name='email_staff_value' value='1'    <%=((infor_type.equals("2")||infor_type.equals("3"))?"":"checked")%>    >通知本人
						</td>
						</tr>
					<% } %>
					<% } 
			//去掉按业务模板中人员的关系上报功能
			//		 if(infor_type.equals("1")&&request.getParameter("isApplySpecialRole")!=null&&request.getParameter("isApplySpecialRole").equals("1")){
					 
					  %>
			<!-- 
					 <tr class="list3" ><td  >
							<input type='checkbox'  name='specialOperateFlag'   checked value='1' />业务模板中人员需要报送给各自领导进行审批处理
					 </td></tr>   -->
					<% 
			//		  }
					%>
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
				<button extra="button" onclick="getSelectedData();">
            		<bean:message key="button.ok"/>
	 	        </button>&nbsp;			
	 	        <button extra="button" onclick="window.close();">
            		<bean:message key="button.close"/>
	 	        </button>	
	 	        <br>
		  </td>
		</tr>
		
	</table>
<script language="javascript">
	if($('emergency').options.length>2)
		$('emergency').options[2].selected=true;
	
	var obj=$('sp_yj');
	for(var i=0;i<obj.options.length;i++)
	{
		if(trim(obj.options[i].text)=='同意')
			$('sp_yj').options[i].selected=true;
	}
</script>
	
</html:form>

