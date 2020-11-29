<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page errorPage="error.jsp"%>
<%@ page import="javax.servlet.ServletException"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="javascript" src="/js/validateDate.js"></script>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script language="javascript">
    	var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	var ViewProperties=new ParameterSet();
</script>
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
	   			alert(SEND_PERSON_ISNOT_EMPTY);
	   			inceptname.focus;
	   			return;
	   		}
	   		if(days.value=="")
	   		{
	   			alert(VALIDITY_DAYS_ISNOT_EMPTY);
	   			days.focus;
	   			return;
	   		}
	   		if(disposal0.checked==false&&disposal1.checked==false)
	   		{
	   			alert(CHOICE_OVERTIME_MANAGE_MODE);
	   			return;
	   		}
	   		if(days.value.length!=0){     
	        	var reg=/^[+]?\d*$/;
		        if(!reg.test(days.value)){ 
		            alert(INTEGER_TYPE_FALSENESS);    
		            return;
		        }     
	        }  
	   		var path=appNewsForm.newsfile.value;
			var fso=new ActiveXObject("Scripting.FileSystemObject");
			if(path.length>0)   
			if(!fso.FileExists(path))
			{
				alert(CHOICE_FILE_NOT_EXIST);
				return;
			}
	   		appNewsForm.action = "/selfservice/app_news/appmessage.do?b_sendmessage=link&state=1";
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
	   		var path=appNewsForm.newsfile.value;
			var fso=new ActiveXObject("Scripting.FileSystemObject");
			if(path.length>0)   
			if(!fso.FileExists(path))
			{
				alert(CHOICE_FILE_NOT_EXIST);
				return;
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
   function checkdate(tag)
   {
		if(tag=='send')
		{
			var str = $('sendtime');
			var sendtimeto = $('sendtimeto');
			var state = $('state');
			var bool = true;
	   		if(str!=null&&str.value!="")
	  		{
	  			if(validate(str)){
		  			//appNewsForm.action = "/selfservice/app_news/appmessage2.do?b_query2=link&type=receive&sendtime="+str.value+"&sendtimeto="+sendtimeto.value+"&state="+state.value;
		  			//appNewsForm.submit();
		  		}
		  		else{
		  			bool = false;
		  			return false;
		  		}
		  	}else if(sendtimeto!=null&&sendtimeto.value!="")
	  		{
	  			if(validate(sendtimeto)){
		  			//appNewsForm.action = "/selfservice/app_news/appmessage2.do?b_query2=link&type=receive&sendtime="+str.value+"&sendtimeto="+sendtimeto.value+"&state="+state.value;
		  			//appNewsForm.submit();
		  		}
		  		else{
		  			bool = false;
		  			return false;
		  		}
		  	}
			if(bool)
		  	{
		  		appNewsForm.action = "/selfservice/app_news/appmessage2.do?b_query2=link&type=receive&sendtime="+str.value+"&sendtimeto="+sendtimeto.value+"&state="+state.value;
		  		appNewsForm.submit();
		  	}
		}
   }
   function deletenews()
   {
   		if(confirm(CONFIRMATION_DEL))
		{
	   		appNewsForm.action = "/selfservice/app_news/appmessage2.do?b_delete=link";
		  	appNewsForm.submit();
		}
   }
   function writemessage()
   {
   		appNewsForm.action = "/selfservice/app_news/appmessage.do?b_writemassage=link&isdraft=1&news_id=";
   		appNewsForm.submit();
   }
</script>
<%int n = 0;
			%>
<script type="text/javascript" src="/fckeditor/fckeditor.js"></script>
<html:form action="/selfservice/app_news/appmessage2" enctype="multipart/form-data">
<br>
	<br>
		<table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
			<tr class="TableRow">
				<TD align="center" colspan="4"><bean:message key="slef.app_news.senddate" />:&nbsp;
					<input type="text" name="sendtime" value="${appNewsForm.sendtime}" extra="editor" style="width:100px;font-size:10pt;text-align:left" id="editor1"  dropDown="dropDownDate">
				<bean:message key="label.to" />:&nbsp;
					<input type="text" name="sendtimeto" value="${appNewsForm.sendtimeto}" extra="editor" style="width:100px;font-size:10pt;text-align:left" id="editor1"  dropDown="dropDownDate">
				</TD>
				<TD align="center" colspan="3"><bean:message key="column.sys.status" />:&nbsp;
					<html:select property="state" size="1" >
						<html:option value="4"><bean:message key="label.all" /></html:option>
					    <html:option value="0"><bean:message key="label.hiremanage.status1" /></html:option>
					    <html:option value="1"><bean:message key="slef.app_news.sendnorefer" /></html:option>
					    <html:option value="2"><bean:message key="slef.app_news.validityrefer" /></html:option>
					    <html:option value="5"><bean:message key="slef.app_news.excessvalidrefer" /></html:option>
				    </html:select>
				    
				</TD>
				<TD width="10%" align="center">
					<input type="button" name="b_select" class="mybutton" value="<bean:message key="button.query" />" onclick="checkdate('send');">
				</TD>
			</tr>
			<tr>
				<td width="10%" align="center" class="TableRow" nowrap>
					<bean:message key="lable.channel_detail.choose" />
				</td>
				<td width="20%"  align="center" class="TableRow"  nowrap>
					<bean:message key="slef.app_news.messagename" />
				</td>
				<td width="10%" align="center" class="TableRow" nowrap>
					<bean:message key="label.zp_employ.to" />
				</td>
				<td width="20%" align="center" class="TableRow" nowrap>
					<bean:message key="system.sms.stime" />
				</td>
				<td width="10%" align="center" class="TableRow" nowrap>
					<bean:message key="slef.app_news.validdate" />
				</td>
				<td width="10%" align="center" class="TableRow" nowrap>
					<bean:message key="column.sys.status" />
				</td>
				<td width="10%" align="center" class="TableRow" nowrap>
					<bean:message key="conlumn.resource_list.name" />
				</td>
				<td width="10%" align="center" class="TableRow" nowrap>
					<bean:message key="kq.feast_type_list.modify" />
				</td>
			</tr>
			<hrms:extenditerate id="element" name="appNewsForm" property="roleListForm2.list" indexes="indexes"  pagination="roleListForm2.pagination" pageCount="13" scope="session">
			<%	if(n%2==0){
	        %>
	             	<tr class="trShallow">            
	        <%}
	       	 	else
	        	{%>
	            	<tr class="trDeep">  
	            <%}
	            n++;
	            %>
	            <td width="10%" align="center" class="RecordRow" nowrap>
	            	<logic:equal name="element" property="states" value="0">
		            	<hrms:checkmultibox name="appNewsForm" property="roleListForm2.select"  value="true" indexes="indexes"/>&nbsp;
	            	</logic:equal>
	            </td>
	            <td width="20%" align="center" class="RecordRow" nowrap>
	            	<bean:write name="element" property="title" filter="false"/>
	            </td>
	            <td width="10%" align="left" class="RecordRow" nowrap>
	            	<bean:write name="element" property="inceptuser" filter="true"/>
	            </td>
	            <td width="20%" align="center" class="RecordRow" nowrap>
	            	<bean:write name="element" property="sendtime" filter="true"/>
	            </td>
	            <td width="10%" align="center" class="RecordRow" nowrap>
	            	<bean:write name="element" property="days" filter="true"/>
	            </td>
	            <td width="10%" align="left" class="RecordRow" nowrap>
	            	<bean:write name="element" property="statesvalue" filter="true"/>
	            </td>
	            <td width="10%" align="center" class="RecordRow" nowrap>
	            	<logic:equal name="element" property="filecontent" value="1">
	            		<a href="/selfservice/app_news/appmessage.do?b_affix=link&news_id=<bean:write name="element" property="news_id" filter="true"/>"><bean:message key="conlumn.resource_list.name" /></a>
	            	</logic:equal>
	            </td>
	             <td width="10%" align="center" class="RecordRow" nowrap>
		            	<logic:equal name="element" property="states" value="0">
		            		<a href="/selfservice/app_news/appmessage.do?b_writemassage=link&isdraft=0&news_id=<bean:write name="element" property="news_id" filter="true"/>" ><img src="/images/edit.gif" border=0></a>&nbsp;
		            	</logic:equal>
	            </td>
		  	 </tr>
			</hrms:extenditerate>
			</table>
			<table  width="80%" align="center" class="RecordRowP">
			  <tr>
			      <td valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
			     <bean:write name="appNewsForm" property="roleListForm2.pagination.current" filter="true" />
			     <bean:message key="label.page.sum"/>
			     <bean:write name="appNewsForm" property="roleListForm2.pagination.count" filter="true" />
			     <bean:message key="label.page.row"/>
			     <bean:write name="appNewsForm" property="roleListForm2.pagination.pages" filter="true" />
			     <bean:message key="label.page.page"/>
			   </td>
			   <td  align="right" nowrap class="tdFontcolor">
			         <p align="right">
			         <hrms:paginationlink name="appNewsForm" property="roleListForm2.pagination"
			                  nameId="roleListForm2" propertyId="roleListProperty">
			         </hrms:paginationlink>
			   </td>
			  </tr>
			</table>
			<table  width="80%" align="center" >
				<tr>
				<td align="left" >
					<input type="button" name="b_delete" class="mybutton" value="<bean:message key="button.delete" />" onclick="deletenews();">
					<input type="button" name="b_write" class="mybutton" value="<bean:message key='selfservice.appnews.write'/>" onclick="writemessage();">
				</td>
			  </tr>
			</table>

</html:form>
<script language="javascript">
   var dropDownDate=createDropDown("dropDownDate");
   var __t=dropDownDate;
   __t.type="date";
   __t.tag="";
   _array_dropdown[_array_dropdown.length]=__t;
   initDropDown(__t);
</script>
<script language="javascript">
    var dropdownCode=createDropDown("dropdownCode");
    var __t=dropdownCode;
    __t.type="custom";
    __t.path="/general/muster/select_code_tree.do";
    __t.readFields="codeitemid";
    //__t.writeFields="xxxx";
    __t.cachable=true;__t.tag="";
    _array_dropdown[_array_dropdown.length]=__t;
    initDropDown(__t);
</script>
<script language="javascript">
  initDocument();
</script>
