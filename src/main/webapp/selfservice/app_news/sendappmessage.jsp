<%@page import="java.util.Date"%>
<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page errorPage="error.jsp"%>
<%@ page import="javax.servlet.ServletException"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
if(userView != null){
	userView.getHm().put("fckeditorAccessTime", new Date().getTime());
}
%>
<script language="javascript">
   function check(tag)
   {
   		var title = document.getElementsByName("title")[0];
   		var constant = FCKeditorAPI.GetInstance('FCKeditor1');
   		var inceptname = document.getElementsByName("inceptname")[0];
   		var days = document.getElementsByName("days")[0];
   		var disposal0 = document.getElementsByName("disposals")[0];
   		var disposal1 = document.getElementsByName("disposals")[1];  
   		if(tag=='send'){
	   		if(title.value=="")
	   		{
	   			alert(TITLE_ISNOT_EMPTY);
	   			title.focus;
	   			return;
	   		}
	   		if(constant.GetXHTML(true)=="")
	   		{
	   			alert(CONTENT_ISNOT_EMPTY);
	   			//constant.focus;
	   			return;
	   		}
	   		if(inceptname.value=="")
	   		{
	   			alert("发送对象不能为空");
	   			inceptname.focus;
	   			return;
	   		}
	   		if(days.value=="")
	   		{
	   			alert(SEND_PERSON_ISNOT_EMPTY);
	   			days.focus;
	   			return;
	   		}
	   		if(disposal0.checked==false&&disposal1.checked==false)
	   		{
	   			alert(VALIDITY_DAYS_ISNOT_EMPTY);
	   			return;
	   		}
	   		if(days.value.length!=0){     
	        	var reg=/^[+]?\d*$/;
		        if(!reg.test(days.value)){ 
		            alert(INTEGER_TYPE_FALSENESS);    
		            return;
		        }     
	        }
	        appNewsForm.action = "/selfservice/app_news/appmessage.do?b_sendmessage2=link&state=1";
	   		appNewsForm.submit();  
	   	}
	   	if(tag=='save'){
	   		if(days.value.length!=0){     
	        	var reg=/^[+]?\d*$/;
		        if(!reg.test(days.value)){ 
		            alert(INTEGER_TYPE_FALSENESS);    
		            return;
		        }     
	        }  
	   		appNewsForm.action = "/selfservice/app_news/appmessage.do?b_sendmessage=link&state=0";
	   		appNewsForm.submit();
	   	}
   }
   function showView() {
       var oEditor = FCKeditorAPI.GetInstance('FCKeditor1');
       var oldInputs = document.getElementsByName('constant');
       oldInputs[0].value = oEditor.GetXHTML(true);
    }
   function checkdate()
   {
   		var str = $('sendtime');
   		if(str!=null&&str.value!="")
  		{
  			if(validate(str)){
	  			appNewsForm.action = "/selfservice/app_news/appmessage.do?b_query=link&type=select";
	  			appNewsForm.submit();
	  		}
	  		else{
	  			return false;
	  		}
	  	}
		else
	  	{
	  		appNewsForm.action = "/selfservice/app_news/appmessage.do?b_query=link&type=select";
	  		appNewsForm.submit();
	  	}
   }
   function deletenews()
   {
   		appNewsForm.action = "/selfservice/app_news/appmessage.do?b_delete=link";
	  	appNewsForm.submit();
   }
   function goback()
   {
   		appNewsForm.action = "/selfservice/app_news/appmessage.do?b_return=link&isdraft=1";
	  	appNewsForm.submit();
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
	   		$('inceptname').value=rolevo.role_name;
	   		$('inceptnameid').value=rolevo.role_id;        	   
	   }
	 }
	 if(objecttype=="3"||objecttype=="1")
	 {
     	 var return_vo=select_org_emp_dialog(flag,2,1,0);   
 		 if(return_vo)
		 {
	 		$('inceptname').value=return_vo.title;
	 		$('inceptnameid').value=return_vo.content;
	 	}	
	 }
	 if(objecttype=="4")
	 {
	 	var return_vo=select_user_dialog('1',2);
	 	
	 	if(return_vo)
	 	{
			$('inceptname').value=return_vo.title;
	 		$('inceptnameid').value=return_vo.content;	 		
	 	}
	 }	
	}
</script>
<%int n = 0;
			%>
<script type="text/javascript" src="/fckeditor/fckeditor.js"></script>
<html:form action="/selfservice/app_news/appmessage" enctype="multipart/form-data">
<br>
	<table width="700" border="0" cellpadding="0" cellspacing="0" align="center">
		<br>
		<tr height="20">
			<!--  <td width=10 valign="top" class="tableft"></td>
			<td width=130 align="center" class="tabcenter">
				&nbsp;
				<bean:message key="slef.app_news.sendmessage" />
				&nbsp;
			</td>
			<td width=10 valign="top" class="tabright"></td>
			<td valign="top" width="780" class="tabremain"></td>-->
			<td align="center" class="TableRow">
				&nbsp;
				<bean:message key="slef.app_news.sendmessage" />
				&nbsp;
			</td>
		</tr>
		<tr>
			<td class="framestyle9">
				<br>
				<table border="0" cellpmoding="0" cellspacing="0" class="DetailTable" cellpadding="0" width="600">
					<tr>
						<td height="10"></td>
					</tr>
					<tr class="list3">
						<TD align="right"  >
							<bean:message key="column.law_base.title" />&nbsp;:&nbsp;&nbsp;
						</TD>
						<TD>
							<html:text name="appNewsForm" property="title" size="30"></html:text>
						</TD>
					</tr>
					<tr class="list3">
						<td align="right" nowrap valign="top" width="20%">
							<bean:message key="conlumn.board.content" />
							:&nbsp;&nbsp;
						</td>

						<td align="left"  nowrap>
                            <html:textarea name="appNewsForm" property="constant" cols="70" rows="20" style="display:none;" />
							<script type="text/javascript">
					              var oldInputs = document.getElementsByName('constant');   
					              var oFCKeditor = new FCKeditor( 'FCKeditor1' ) ;
					              oFCKeditor.BasePath	= '/fckeditor/';
					              oFCKeditor.Height	= 250 ;
					              oFCKeditor.ToolbarSet='Simple';					             
					              oFCKeditor.Value	= oldInputs[0].value;
					              oFCKeditor.Create() ;
           
                            </script>
						</td>
					</tr>
					<tr>
						<td height="10"></td>
					</tr>
				</table>
				<table border="0" cellpmoding="0" cellspacing="0" class="DetailTable" cellpadding="0" width="600">
					<tr class="list3">
						<td align="right" width="20%">
							<bean:message key="self.app_news.sendobject" />
							:&nbsp;&nbsp;
						</td>
						<td width="40%">
							<html:text name="appNewsForm" property="inceptname" size="10" readonly="true"></html:text>
							 <html:select name="appNewsForm" property="objecttype" size="1" onchange="selectobject();">
               					<option value="#" selected="selected"><bean:message key="label.select" /></option>
               					<option value="1"><bean:message key="label.query.employ" /></option>
               					<option value="4"><bean:message key="task.selectobject.user" /></option>
               				</html:select>
               				<html:hidden name="appNewsForm" property="inceptnameid" />
						</TD>
						<TD width="10%">
							&nbsp;
						</TD>
						<TD width="30%" rowspan=3>
						<FIELDSET>
							<LEGEND align = "center">
								<font color="red"><bean:message key="self.app_news.overtimetransact" /></font>
							</LEGEND>
								<html:radio name="appNewsForm" property="disposals" value="0" ><bean:message key="self.app_news.straightdelete" /></html:radio>
							<BR>
								<html:radio name="appNewsForm" property="disposals" value="1"><bean:message key="slef.app_news.returnwaiting" /></html:radio>
						</FIELDSET>
						</TD>
					</tr>
					<tr class="list3">
						<TD align="right">
							<bean:message key="kq.wizard.days" /> 
							:&nbsp;&nbsp;
						</TD>
						<td>
							<html:text name="appNewsForm" property="days" size="10"></html:text>
						</td>
					</tr>
					<tr class="list3">
						<TD align="right">
						<logic:notEqual name="appNewsForm" property="isdraft" value="1">
							<a href="/selfservice/app_news/appmessage.do?b_affix=link"><bean:message key="button.setfield.addfield" /><bean:message key="conlumn.resource_list.name" /> </a>
							&nbsp;&nbsp;
						</logic:notEqual>
						</TD>
					
						<TD width="10%">
							
						</TD>
						
					</tr>
					
				</table>
			</td>
		</tr>

		<tr class="list3">
			<td align="center" style="height:35px;">				
				<input  type = "button" Class="mybutton" name="b_send" value="<bean:message key="button.sms.send" />" onclick="showView();check('send');" >
				<input  type = "button" Class="mybutton" name="b_save" value="<bean:message key="slef.app_news.savedraft" />" onclick="showView();check('save');" >	
				<input  type = "button" Class="mybutton" name="b_return" value="<bean:message key="button.return"/>" onclick="goback()" >	
			</td>
		</tr>
	</table>
</html:form>