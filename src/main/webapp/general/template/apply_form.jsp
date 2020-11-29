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
<%@ page import="com.hjsj.hrms.actionform.general.template.TemplateListForm"%>

<%
	String view_type = request.getParameter("view_type");//0 卡片 1 列表
	if(view_type==null)
		view_type = "0";//默认卡片
	TemplateForm templateForm=(TemplateForm)session.getAttribute("templateForm");
	TemplateListForm templateListForm=(TemplateListForm)session.getAttribute("templateListForm");
		
	String isSendMessage=templateForm.getIsSendMessage();	
	String email_staff=templateForm.getEmail_staff();
	ArrayList list=templateForm.getSp_yjListForm().getList();
	ArrayList rejectObjList=templateForm.getRejectObjList();
	String operationtype=templateForm.getOperationtype();
	if(operationtype==null)
		operationtype=request.getParameter("operationtype");
	String enduser=templateForm.getEnduser();
	String endusertype=templateForm.getEndusertype();
	String enduser_fullname=templateForm.getEnduser_fullname();
	String sp_batch="0";
	if(request.getParameter("sp_batch")!=null&&request.getParameter("sp_batch").equals("1"))
		sp_batch="1";
	ArrayList yjlist=templateForm.getSp_yjlist();
	String isEndTask_flow="0".equals(view_type)?templateForm.getIsEndTask_flow():templateListForm.getIsEndTask_flow();
	if(isEndTask_flow==null){
	   isEndTask_flow="";
	}
	UserView userView = (UserView) request.getSession().getAttribute(WebConstant.userView);
	boolean isDefineEndUser = false;
	if(enduser.trim().length()>0&&endusertype.trim().length()>0)
		isDefineEndUser = true;
	String _enduser=userView.getUserName();
	if(endusertype.equals("1"))
		_enduser=userView.getDbname()+userView.getA0100();
	String infor_type=templateForm.getInfor_type();
	String signLogo=templateForm.getSignLogo();
	String actor_type=templateForm.getActor_type();////bug 43677 无法发送短信通知 用户选择的是用户还是人员
		 String bosflag= userView.getBosflag();
	
%>




<script language="javascript">
	function validateValue()
	{
	    /*
		 var hashvo=new ParameterSet();
		 hashvo.setValue("value",$('user_h').value);  	      
		 var request=new Request({asynchronous:false,onSuccess:validatesuccess,functionId:'0570010144'},hashvo);
		 */
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
		 	str+="<input type='checkbox' name='specialOperateFlag'  checked value='1' />业务模板中人员需要报送给各自领导进行审批处理 ";
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
     if(objecttype=="3")
     {
        var return_vo=select_org_dialog(flag,2,1,0);   
		 if(return_vo)
		 {
	 		$('user_').value=return_vo.title;
	 		$('user_h').value=return_vo.content;
	 		setNull();
	 	}	
     }
	 if(objecttype=="1")
	 {
     	 var return_vo=select_org_emp_dialog(flag,2,1,0,0,1);       //select_org_emp_dialog(flag,2,1,0);   
		 if(return_vo)
		 {
	 		$('user_').value=return_vo.title;
	 		$('user_h').value=return_vo.content;
	 		setNull();
	 	}	
	 }	
	 if(objecttype=="4")
	 {
	 	var return_vo=select_user_dialog('1','2');
	 	if(return_vo)
	 	{
	 		$('user_').value=return_vo.title;
	 		$('user_h').value=return_vo.content;
	 		setNull();	 		
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
     	
	 	var return_vo=select_org_emp_dialog(flag,1,1,0,0,1); //select_org_emp_dialog(flag,1,1,0);   
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
	 		$('user_s').value=return_vo.title;
	 		$('user_h_s').value=return_vo.content;
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
	
	function clearObjs()
	{
		$('user_s').value='${templateForm.user_}';
	   	$('user_h_s').value='${templateForm.user_h}';
	
	}
	
	
	function subEndUser()
	{
	
		if(!confirm('此信息将转入下一审批环节，是否确认提交？'))
			return;
	
	   var obj=new Object();
	   var objectype="1";
	   <%
	   	if(endusertype.equals("0")){
		%>
			objectype="4";
	    <%		   
		}
	    %>
	   obj.objecttype=objectype;
	   obj.name='<%=enduser%>';
	   obj.fullname='<%=enduser_fullname%>';	
	      
	   obj.pri=$F('emergency');
     //  var oEditor = FCKeditorAPI.GetInstance('FCKeditor1');	   
      // $('topic').value = oEditor.GetXHTML(true);
	     var content=$F('topic');
	   //编辑器有问题，?自动转为&nbsp;空格转为编码为160
	   //解决办法：先把&nbsp;转为?,再把160转为&nbsp;
	   
	 //  while(content.indexOf("&nbsp;")!=-1){
	 //  content = content.replace("&nbsp;","?");
	 //  }
	  
	 //  var contenttemp ="";
	 //   for(var i=0;i<content.length;i++)
	//	     {
	//	        if(content.charCodeAt(i)==160){
	//	        contenttemp+="&nbsp;";
	//	        }else{
	//	         contenttemp+=content.charAt(i);
	//	        }
	//	        
//	     } 
		     
	   obj.content = content;
	   obj.sp_yj=$F('sp_yj');
	   obj.flag="1";
	
	   if(obj.sp_yj=="#")
	   {
	    	//obj.sp_yj="01";
	    	if(flag=="3")
	    	{
	   			alert("请选择审批意见!");
	   			return;
	   		}
	   		else
	   			obj.sp_yj="01";
	   }
	   if(obj.pri=="#")
		 obj.pri="1";
	   obj.specialOperate='0';
	   
	   obj.isSendMessage='<%=isSendMessage%>';
	   <% 
	   
	     if(request.getParameter("message")==null||!request.getParameter("message").equals("0")){
		   if(!isSendMessage.equals("0")){ %>
		   		
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
		   <% }
	   } %>
	
	
	
	   returnValue=obj;
	   window.close();	
	}
	
	
	function getSelectedData(flag)
	{
	  <% if(request.getParameter("noObject")!=null&&request.getParameter("noObject").equals("1")){ 
	  		 if(request.getParameter("flag")!=null){ 
	  %>
			flag='<%=(request.getParameter("flag"))%>';
	  <% } } %>
	
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
     //  var oEditor = FCKeditorAPI.GetInstance('FCKeditor1');	   
     //  $('topic').value = oEditor.GetXHTML(true);
	    var content=$F('topic');
	   //编辑器有问题，?自动转为&nbsp;空格转为编码为160
	   //解决办法：先把&nbsp;转为?,再把160转为&nbsp;
	   
	 //  while(content.indexOf("&nbsp;")!=-1){
	  // content = content.replace("&nbsp;","?");
	 //  }
	  
	 //  var contenttemp ="";
	  //  for(var i=0;i<content.length;i++)
	//	     {
	//	        if(content.charCodeAt(i)==160){
	//	        contenttemp+="&nbsp;";
	//	        }else{
	//	         contenttemp+=content.charAt(i);
	//	        }
	//	        
	//	     } 
		     
	   obj.content = content;  
	   obj.sp_yj=$F('sp_yj');
	   obj.flag=flag;
	   
	   <% if(request.getParameter("def_flow_self")==null&&(request.getParameter("noObject")==null||request.getParameter("noObject").equals("0"))){ %>
	  
	   if((obj.objecttype=="#"||obj.name=="")&&flag=="1")
	   {
	     alert('<bean:message key="error.notselect.object"/>');
	   	 return;
	   }
	   <% } %>
	   
	   if(obj.sp_yj=="#")
	   {
	    	//obj.sp_yj="01";
	    	if(flag=="3")
	    	{
	   			alert("请选择审批意见!");
	   			return;
	   		}
	   		else
	   			obj.sp_yj="01";
	   }
	   
	   if(flag=="3")//提交加判断
	   {
		
			if(obj.sp_yj=='01')
			{
				<% if(infor_type.equals("1")){ %>
				if(!confirm(SUBMIT_APPLY_INFO))	   
		   	 		return;
		   	 	<% }else{ %>
		   	 	if(!confirm(SUBMIT_APPLY_INFO2))	   
		   	 		return;
		   	 	<% } %>	
		   	}
		   	else if(obj.sp_yj=='02')
		   	{
		   		if(!confirm("审批意见为不同意时，此操作只结束流程，\r\n但表单数据不入库归档，是否确认提交?"))	   
		   	 		return;
		   	}
		    else if(obj.sp_yj=='03')
		    {
		   		if(!confirm("审批意见为未审阅时，此操作只结束流程，\r\n但表单数据不入库归档，是否确认提交?"))	   
		   	 		return;
		    }
	   }
	   if(obj.pri=="#")
		 obj.pri="1";
		 
		 obj.isSendMessage='<%=isSendMessage%>';
	   <% 
	   
	     if(request.getParameter("def_flow_self")==null&&(request.getParameter("message")==null||!request.getParameter("message").equals("0"))){
	   if(!isSendMessage.equals("0")){ %>
	   		
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
	   <% }
	   } %>
	  
		<% 
		//如果是批量处理里的驳回操作，驳回对象设置为self
		if(sp_batch.equals("1")&&request.getParameter("type")!=null&&request.getParameter("type").equals("2"))
		{ %>
		
			obj.rejectObj='self';
		<%
		}
		else if(rejectObjList.size()>0){ %>
		 if(flag=="2")
		 {
		 	var a_obj=window.showModalDialog("/general/template/apply_form.do?br_reject=spyj","", 
              "dialogWidth:300px; dialogHeight:250px;resizable:yes;center:yes;scroll:yes;status:no");
		 	if(a_obj)
		 	{
		 		obj.rejectObj=a_obj;
		 	}
		 	else
		 	{
		 		alert("请选择驳回的对象!");
		 		return;
		 	}
		 
		 }
		
	<% } %> 
	/* if(eval("document.templateForm.specialOperateFlag"))
	   {
	   		if(document.getElementsByName("specialOperateFlag")[0].checked)
	   			obj.specialOperate='1';
	   		else
	   			obj.specialOperate='0';
	   }
	   else  */
	   		obj.specialOperate='0';
	   returnValue=obj;
	   window.close();	
	}
	
	function hvchange(yj,arrow1)
	{
		Element.toggle(yj);
	}
	
	
	
	
	
	
	function showyj()
	{
	    var dialogWidth="630px";
	    var dialogHeight="500px";
	    if (isIE6()){
	    	dialogWidth="650px";
	    	dialogHeight="530px";
	    } 
		window.showModalDialog("/general/template/apply_form.do?br_spyj=spyj","", 
              "dialogWidth:"+dialogWidth+"; dialogHeight:"+dialogHeight+";resizable:no;center:yes;scroll:yes;status:no");
	}
	
</script>
<script type="text/javascript" src="/fckeditor/fckeditor.js"></script>
<%
	int i=0;
%>
<%	   
if ("hcm".equals(bosflag)){	   
%>
<link href="/general/template/template.css" rel="stylesheet" type="text/css"/>
<%} %>
<html:form action="/general/template/apply_form">
<% if (!"hcm".equals(bosflag)){	 %>
<br>
<%} %>
	<table width="637" border="0" cellpadding="0" cellspacing="0" align="center" style="margin-left:1px">
		<tr height="20">
	<!--	<td width=10 valign="top" class="tableft"></td>
			<td width=130 align=center class="tabcenter">&nbsp;<bean:message key="rsbd.task.topic" />&nbsp;</td>
			<td width=10 valign="top" class="tabright"></td>
			<td valign="top" width="600" class="tabremain"></td>   -->
			
			<td align="left" colspan="4" class="TableRow1"><bean:message key="rsbd.task.topic"/>&nbsp;</td> 
		</tr>
		<tr>
			<td colspan="4" class="framestyle">
				<table border="0" cellpmoding="0" cellspacing="0" class=""DetailTable"" cellpadding="0" width="600" >
					<tr height="25px" style="padding-left:7px;padding-top:5px">
						<td valign="middle">
			 				<bean:message key="rsbd.task.emergency" /> 			
            				 <html:select name="templateForm" property="emergency" size="1">
               					<option value="#" selected="selected"><bean:message key="label.select" /></option>
                                <html:optionsCollection property="emergencylist" value="codeitem" label="codename"/>
				        	 </html:select> 
			 				<bean:message key="rsbd.task.idea" /> 					        	 
            				 <html:select name="templateForm" property="sp_yj" size="1">
            				 <% if(yjlist.size()>1){ %>
               					<option value="#" selected="selected"><bean:message key="label.select" /></option>
                              <% } %>  
                                <html:optionsCollection property="sp_yjlist" value="codeitem" label="codename"/>
				        	 </html:select> 				        	   				        	   				
						</td>							
					</tr>
					<tr class="list3">
						<td align="left" style="padding-left:5px" nowrap>
                            <html:textarea name="templateForm" property="topic" cols="97" rows="20" style="display:block;" />
							<script type="text/javascript">
					        //      var oldInputs = document.getElementsByName('topic');   
					        //      var oFCKeditor = new FCKeditor( 'FCKeditor1' ) ;
					        //      oFCKeditor.BasePath	= '/fckeditor/';
					       //       oFCKeditor.Height	= 200 ;
					       //       oFCKeditor.ToolbarSet='Simple';					             
					       //       oFCKeditor.Value	= oldInputs[0].value;
					       //       oFCKeditor.Create() ;
                            </script>
						</td>
					</tr>
					
						<% if(request.getParameter("def_flow_self")==null&&(request.getParameter("noObject")==null||request.getParameter("noObject").equals("0"))){ %>
	 				 <tr class="list3" >
						<td align="left"  style="padding-left:7px;padding-top:5px" nowrap >
							<bean:message key="rsbd.task.selectobject" />
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
				        	 <INPUT type="text" id="user_" value="" onchange='validateValue()'  class="TEXT9" size="18" maxlength="200">
               				 <INPUT type="hidden" id="user_h" value=""    size=30>	
				        	</td>
					</tr>	 
				        <% } %>
				    <tr class="list3">
						<td align="left"  id='specialOperate'  >
						
						
						</td>
					</tr>   
				        
				     <% 
				     //
				     if(request.getParameter("def_flow_self")==null&&(request.getParameter("message")==null||!request.getParameter("message").equals("0"))){
				     	if(!isSendMessage.equals("0")){ %>
					<tr class="list3">
						<td align="left"  style="padding-left:7px;" nowrap >
						邮件抄送
						<% if(request.getParameter("type")!=null&&request.getParameter("type").equals("2")) {%>
						<select name="objecttype2" disabled  onchange="selectobject2();">
						<% }else{ %>
						<select name="objecttype2"   onchange="selectobject2();">
			             <% } %>
			               					<option value="#" selected="selected"><bean:message key="label.select" />&nbsp;&nbsp;</option>
			                                <option value="1"  >人员</option>
			                                <option value="2"  >角色</option>
			                                <option value="4"  >用户</option>
							        	 </select>&nbsp; 
						<INPUT type="text" id="user_s"  value="${templateForm.user_}" class="TEXT" size="48" maxlength="200">
			            <img src='/images/del.gif' title='清空抄送人员' onclick='clearObjs()' />     <INPUT type="hidden" id="user_h_s"  value="${templateForm.user_h}"  size=30>	
						</td>
					</tr>
						<% if(email_staff.equalsIgnoreCase("True")){ %>
							<tr class="list3" ><td  >
							<% if(operationtype.equals("0")||(request.getParameter("type")!=null&&request.getParameter("type").equals("2"))){ %>
							<input type='checkbox' name='email_staff_value' value='1'  disabled >通知本人
							<% }else{ %>
							<input type='checkbox' name='email_staff_value' value='1'  checked >通知本人
							<% } %>
							</td></tr>
						<% } %>
					<%	 }
					
					  }
					  
					  if(infor_type.equals("1")&&request.getParameter("isApplySpecialRole")!=null&&request.getParameter("isApplySpecialRole").equals("1")){
						  String disabled_str="";
						  String checked_str="checked";
						  if(request.getParameter("type").equals("2"))
						  {
						    	disabled_str="disabled";
						  		checked_str="";  	
						  }
					 %>
				        
				     <tr class="list3" ><td  >
				     	<!-- 	
							<input type='checkbox'  <%=disabled_str%> name='specialOperateFlag'   <%=checked_str%> value='1' />业务模板中人员需要报送给各自领导进行审批处理
					 	 -->
					 </td></tr>   
				        
				     <% } %>
										
				</table>
			</td>
		</tr>
		<tr class="list3" height="35px">
			<td align="center" colspan="4">
			
			<% if(request.getParameter("noObject")==null||request.getParameter("noObject").equals("0")){ %>
	  					
	  			<hrms:priv func_id="32117,37017,37117,37217,37317,33001017,33101017,2701517,0C34817,32017,324010117,325010117,3800717">	 
				<% if(request.getParameter("def_flow_self")==null&&enduser.trim().length()>0&&endusertype.trim().length()>0&&!_enduser.equalsIgnoreCase(enduser)){ %>
					<button extra="button" onclick="subEndUser();"  style="margin-right:10px">
	            		<bean:message key="label.agree"/>
		 	        </button>
				<% } %>
				</hrms:priv>
	  			<%if("0".equals(view_type)){ %>
		  			<logic:notEqual   name="templateForm" property="isEndTask_flow" value="true">	
		 	        <button extra="button" onclick="getSelectedData('1');"  style="margin-right:10px">
	            		继续<bean:message key="button.appeal"/>
		 	        </button>&nbsp;	  
		 	        </logic:notEqual>
	 	        <%}else{ %>
		 	        <logic:notEqual   name="templateListForm" property="isEndTask_flow" value="true">	
		 	        <button extra="button" onclick="getSelectedData('1');"  style="margin-right:10px">
	            		继续<bean:message key="button.appeal"/>
		 	        </button>&nbsp;	  
		 	        </logic:notEqual>
	 	        <%} %>
	 	        <logic:equal name="templateForm" property="startflag" value="0">	
		 	        <button extra="button" onclick="getSelectedData('2');"  style="margin-right:10px">
	            		<bean:message key="button.reject"/>
		 	        </button>
					<% if(isEndTask_flow.equalsIgnoreCase("true")||(request.getParameter("def_flow_self")==null&&((isDefineEndUser&&_enduser.equalsIgnoreCase(enduser))||!isDefineEndUser))){ 
						//添加判断条件，非自定义流程下，1、如果定义了业务办理人，当前登录人等于业务办理人则有批准。2、如果没有定义业务办理人，则直接有批准。 update hej 20180228
					%>
					<hrms:priv func_id="010703,32101,33001001,33101001,2701501,0C34801,32001,324010102,325010102,3800701">	 	        
		 	        <button extra="button" onclick="getSelectedData('3');"  style="margin-right:10px">
	            		<bean:message key="button.approve"/>
		 	        </button>
		 		   </hrms:priv>  
		 		  <% } %>
		 		 </logic:equal>	   
			<% }else { %>	
				
				        
	 	        <button extra="button" onclick="getSelectedData('3');"  style="margin-right:10px">
            		<bean:message key="button.ok"/>
	 	        </button>
	 		   
           	 	      
           	<% } 
           
           	if(list!=null&&list.size()>0){
                //<button extra="button" onclick="showyj();"  style="margin-right:10px">
                //  <bean:message key="rsbd.wf.sploop"/>
              //  </button>
           	%>  	 	        
           	
           	<% } %>
	 	        <button extra="button" onclick="window.close();"  style="margin-right:10px">
            		<bean:message key="button.close"/>
	 	        </button>	
	 	        <br>
                
	 	    </td>
		</tr>
	</table>
</html:form>

