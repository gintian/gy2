<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="org.apache.commons.beanutils.LazyDynaBean, com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.lawbase.LawBaseForm" %>
<%@ page import="java.util.*" %>
<%
	String field_str_item= ((LawBaseForm)session.getAttribute("lawbaseForm")).getField_str_item();
	String[] items = field_str_item.split(",");
	for(int i=0;i<items.length;i++){
		String[] itemss = items[i].split("`");
	    if(itemss.length==2){
	        	pageContext.setAttribute(itemss[0],itemss[1]);
	    }
	}
 %>
 <%
 	LawBaseForm lawBaseForm = (LawBaseForm)session.getAttribute("lawbaseForm");
    ArrayList contentlist = lawBaseForm.getContentlist();
    ArrayList itemList = lawBaseForm.getItemList();
    int len = itemList.size();
    String itemId = "";
    String itemDesc = "";
    String itemType = "";
    String itemCodeid = "";
    int num = 0;
    if(itemList != null && itemList.size() > 0){
	    for(int i=0;i<itemList.size();i++){
	    	LazyDynaBean lBean = (LazyDynaBean)itemList.get(i);
	    	itemId = itemId + lBean.get("itemid") + "`";
	    	itemDesc = itemDesc + lBean.get("itemdesc") + "`";
	    	itemType = itemType  + lBean.get("itemtype") + "`";
	    	itemCodeid = itemCodeid + lBean.get("codesetid") + "`";
	    	num++;
	    }
    }
 %>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>HRPWEB3</title>
		
		<script language="JavaScript" src="/js/newcalendar.js"></script>
        <script language="JavaScript" src="/js/validate.js"></script>
         <script language="JavaScript" src="/js/function.js"></script>
		<script language="JavaScript">
			function getContentType(){
				var hashvo=new ParameterSet();
				hashvo.setValue("type","content_type");
				hashvo.setValue("basetype",$F('basetype'));
				var request=new Request({method:'post',onSuccess:initSelect,functionId:'10400101012'},hashvo);
			}
			function initSelect(outparamters){
				var date_panel=document.getElementById("date_panel");
				var contentlist=outparamters.getValue("contentlist");
				var html="<select id =\"date_box\" name=\"content_type\" multiple=\"multiple\" style=\"width:178\" size=\"6\" onchange=\"setSelectValue();\"  onblur=\"$('date_panel').style.display='none';\" onclick=\"setSelectValue();\">";
				for (var i = 0; i < contentlist.length; i++) {
					html +="<option value=\""+contentlist[i].dataValue+"\">"+contentlist[i].dataName+"</option>";
				}
				html +="</select>";
				date_panel.innerHTML += html;
			}
			function pf_ChangeFocus(e) 
			{
				  e=e?e:(window.event?window.event:null);//xuj update 2011-5-11 兼容firefox、chrome
			      var key = window.event?e.keyCode:e.which;
			      var t=e.target?e.target:e.srcElement;
			      if ( key==0xD && t.tagName!='TEXTAREA') /*0xD*/
			      {    
			   		   if(window.event)
			   		   	e.keyCode=9;
			   		   else
			   		   	e.which=9;
			      }
			   //按F5刷新问题,重复提交问题,右键菜单也设法去掉
			   if ( key==116)
			   {
			   		if(window.event){
			   		   	e.keyCode=0;
			   		   	e.returnValue=false;
			   		}else{
			   		   	e.which=0;
			   		   	e.preventDefault();
			   		}
			   }   
			   if ((e.ctrlKey)&&(key==82))//屏蔽 Ctrl+R  
			   {    
			        if(window.event){
			   		   	e.keyCode=0;
			   		   	e.returnValue=false;
			   		}else{
			   		   	e.which=0;
			   		   	e.preventDefault();
			   		}
			   } 
			}
			var date_desc;
			  
			   function setSelectValue()
			   {
			     if(date_desc)
			     {
			       date_desc.value=$F("date_box");
			       Element.hide("date_panel"); 

			     }

			   }
			   function showDateSelectBox(srcobj)
			   {
				   if ($("date_box").length==0) {
				   getContentType();
				}
			      date_desc=document.getElementsByName(srcobj)[0];
			      Element.show("date_panel");   
			      $("date_box").focus();
			      var pos=getAbsPosition(date_desc);
				  with($("date_panel"))
				  {
			        style.position="absolute";
			        if(navigator.appName.indexOf("Microsoft")!= -1){
						style.posLeft=pos[0]-1;
						style.posTop=pos[1]-1+date_desc.offsetHeight;
						style.width=(date_desc.offsetWidth<150)?150:date_desc.offsetWidth+1;
					}else{
						style.left=pos[0]-1;
						style.top=pos[1]-1+date_desc.offsetHeight;
						style.width=(date_desc.offsetWidth<150)?150:date_desc.offsetWidth+1;
					}
			      }                 
			   }
				function _onkeydown(e){
			  		e=e?e:(window.event?window.event:null);//xuj update 2011-5-11 兼容firefox、chrome
			      var key = window.event?e.keyCode:e.which;
			      if(key==13) remove();
			  	}
				function IsDigit(e) 
			    { 
			    	e=e?e:(window.event?window.event:null);//xuj update 2011-5-11 兼容firefox、chrome
			      var key = window.event?e.keyCode:e.which;
			    return ((key != 96)); 
			    } 
			function exeAdd(addStr)
			{
			    target_url=addStr;
			    window.open(target_url, '_self'); //il_body
			}
			function query(){
				var hashvo=new ParameterSet();
				var basetype = $F('basetype');
				var num = $F('num');
				var itemid = $F('itemid');
				var itemtype = $F('itemtype');
				var itemCodeid = $F('itemCodeid');
				hashvo.setValue("basetype",basetype);
				hashvo.setValue("itemid",$F('itemid'));
				hashvo.setValue("itemdesc",$F('itemdesc'));
				hashvo.setValue("itemtype",$F('itemtype'));
				
				items = new Array;
				itemtypes = new Array;
				itemCodeids = new Array;
				
				items = itemid.split("`");
				itemtypes = itemtype.split("`");
				itemCodeids = itemCodeid.split("`");
				
				itemid = "";
				itemtype = "";
				itemcodesetid = "";
				var valueStr = "";
				var size = items.length;
				if(size > 0){
					for(var j = 0;j<size;j++){
						if(items[j].length <= 0)
							continue;
						if(itemtypes[j] == "D"){
							var start = document.getElementById(items[j]+"_start").value;
							var end = document.getElementById(items[j]+"_end").value;
							var itemValue = start + "+" + end;
						}else{
							var itemValue = document.getElementById(items[j]).value;
						}
						if(itemValue == null || "" == itemValue){
							continue;
						}
						itemid = itemid + items[j] + "`";
						itemtype = itemtype + itemtypes[j] + "`";
						valueStr = valueStr + itemValue + "`";
						itemcodesetid = itemcodesetid + itemCodeids[j] + "`";
					}
				}
				document.getElementById("itemvalue").value = valueStr;
				document.getElementById("itemid").value = itemid;
				document.getElementById("itemtype").value = itemtype;
				document.getElementById("itemCodeid").value = itemcodesetid;
			}
			//屏蔽右键,实在没有办法的采用此办法,解决重复提交问题
			/*
			document.oncontextmenu=function(e) 
			{ 
			  	return false; 
			} 
			*/
        </script>
	</head>
	<body onKeyDown="return pf_ChangeFocus(event);">
		<br>
		<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0" style="margin-top: 50px;">
			<tr>
				<td valign="top">
					<form name="law_term_queryForm" method="post" action="/selfservice/lawbase/lawtext/law_term_query.do" onsubmit="query();">
						<table width="700" border="0" cellpadding="0" cellspacing="0" align="center">
							<tr height="20">
								<!-- td width=1 valign="top" class="tableft1"></td>

								<td width=130 align=center class="tabcenter">
									&nbsp;特征检索&nbsp;
								</td>
								<td width=10 valign="top" class="tabright"></td>
								<td valign="top" class="tabremain" width="700"></td> -->
								<td align="center" colspan="4" class="TableRow">&nbsp;
									&nbsp;特征检索&nbsp;
								</td>
							</tr>
							<tr>
								<td colspan="4" class="framestyle3">
									<table border="0" cellspacing="0" width="70%" class="ListTable" cellpadding="2" align="center">
										<tr>
										<td colspan="4">
											&nbsp;
										</td>
										</tr>
										<tr>
											<td colspan="4">
												<table border="0" cellspacing="0" width="70%" class="ListTable1" cellpadding="0" align="center">
													<tr>

														<td align="center" nowrap class="TableRow">
															项目
														</td>
														<td align="center" nowrap class="TableRow">
															值
														</td>
														<td align="center" nowrap class="TableRow">
															项目
														</td>
														<td align="center" nowrap class="TableRow">
															值
														</td>
													</tr>
													<logic:notEmpty name="lawbaseForm" property="field_str_item">
													<%
														int i=0;
													 %>
													<tr>
														<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",name," scope="session">           
															<logic:match name="lawbaseForm" property="field_str_item" value="name`" scope="session"> 
															<%
																i++;
															 %>
															<td align="center" class="RecordRow" nowrap style="border-bottom:none">
																<!--<bean:message key="column.law_base.filename" />-->
																<bean:write name="name"/>
																<bean:message key="law_maintenance.contain" />
															</td>
															<td align="center" class="RecordRow" nowrap style="border-bottom:none">
																&nbsp;<input type="text" name="name" maxlength="50" size="30" value="" class="text4">&nbsp;
															</td>
															</logic:match>
														</logic:notMatch>
														<logic:match name="lawbaseForm" property="field_str_item" value="title`" scope="session"> 
														<%
																i++;
															 %>
														<td align="center" class="RecordRow" nowrap style="border-bottom:none">
															<bean:write name="title"/>
															<bean:message key="law_maintenance.contain" />
														</td>
														<td align="left" class="RecordRow" nowrap style="border-bottom:none">
															&nbsp;<input type="text" name="title" maxlength="50" size="30" value="" class="text4">&nbsp;
														</td>
														</logic:match>
													<%
													if(i%2==0){ %>
													</tr>
													<tr>
													<%} %>
													<logic:notEqual name="lawbaseForm" property="basetype" value="5">
														<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",type," scope="session">           
														<logic:match name="lawbaseForm" property="field_str_item" value="type`" scope="session"> 
														<%
																i++;
															 %>
														<%if(i == 1 || i == 2 ){%>
														<td align="center" class="RecordRow" nowrap style="border-bottom:none">
															<bean:write name="type"/>
														</td>
														<td align="center" class="RecordRow" nowrap style="border-bottom:none">
															&nbsp;<input type="text" name="type" maxlength="50" size="30" value="" class="text4">&nbsp;
														</td>
														<%}else{ %>
														<td align="center" class="RecordRow" nowrap>
															<bean:write name="type"/>
														</td>
														<td align="center" class="RecordRow" nowrap>
															&nbsp;<input type="text" name="type" maxlength="50" size="30" value="" class="text4">&nbsp;
														</td>
														<%} %>
														</logic:match>
														</logic:notMatch>
													</logic:notEqual>
													<%
													if(i%2==0){ %>
													</tr>
													<tr>
													<%} %>
													<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",content_type," scope="session">           
														<logic:match name="lawbaseForm" property="field_str_item" value="content_type`" scope="session"> 
														<%
																i++;
															 %>
														<%if(i == 1 || i == 2 ){%>
														<td align="center" class="RecordRow" nowrap style="border-bottom:none">
															<bean:write name="content_type"/>
														</td>
														<td align="left" class="RecordRow" nowrap style="border-bottom:none">
															&nbsp;<input type="text" name="content_type" maxlength="50" size="26" value="" class="text4">&nbsp;
															<img  src="/images/code.gif" align="absmiddle" onclick="showDateSelectBox('content_type');" >
														</td>
														<%}else{ %>
														<td align="center" class="RecordRow" nowrap>
															<bean:write name="content_type"/>
														</td>
														<td align="left" class="RecordRow" nowrap>
															&nbsp;<input type="text" name="content_type" maxlength="50" size="26" value="" class="text4">&nbsp;
															<img  src="/images/code.gif" align="absmiddle" onclick="showDateSelectBox('content_type');" >
														</td>
														<%} %>
														</logic:match>
													</logic:notMatch>
													<%
													if(i%2==0){ %>
													</tr>
													<tr>
													<%} %>
													<logic:notEqual name="lawbaseForm" property="basetype" value="5">
													<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",valid," scope="session">           
														<logic:match name="lawbaseForm" property="field_str_item" value="valid`" scope="session"> 
														<%
																i++;
															 %>
														<td align="center" class="RecordRow" nowrap>
															<bean:write name="valid"/>
														</td>
														<td align="left" class="RecordRow" nowrap>
															&nbsp;<SELECT name="valid">
																<OPTION value="1">
																	<bean:message key="lable.lawfile.availability" />
																</OPTION>
																<OPTION value="0">
																	<bean:message key="lable.lawfile.allabolish" />
																</OPTION>
																<OPTION value="2">
																	<bean:message key="lable.lawfile.partabolish" />
																</OPTION>
																<OPTION value="3">
																	<bean:message key="lable.lawfile.editing" />
																</OPTION>
																<OPTION value="4">
																	<bean:message key="lable.lawfile.other" />
																</OPTION>
																<OPTION value="" selected="selected">						
																</OPTION>
															</SELECT>
														</td>
														</logic:match>
													</logic:notMatch>
													<%
													if(i%2==0){ %>
													</tr>
													<tr>
													<%} %>
													<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",issue_org," scope="session">           
														<logic:match name="lawbaseForm" property="field_str_item" value="issue_org`" scope="session"> 
														<%
																i++;
															 %>
														<td align="center" class="RecordRow" nowrap>
															<bean:write name="issue_org"/>
														</td>
														<td align="left" class="RecordRow" nowrap>
															&nbsp;<input type="text" name="issue_org" maxlength="50" size="30" value="" class="text4">&nbsp;
														</td>
														</logic:match>
													</logic:notMatch>
													<%
													if(i%2==0){ %>
													</tr>
													<tr>
													<%} %>
													<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",notes," scope="session">           
														<logic:match name="lawbaseForm" property="field_str_item" value="notes`" scope="session"> 
														<%
																i++;
															 %>
														<td align="center" class="RecordRow" nowrap>
														<bean:write name="notes"/>

														</td>
														<td align="center" class="RecordRow" nowrap>
															&nbsp;<input type="text" name="notes" maxlength="50" size="30" value="" class="text4">&nbsp;
														</td>
														</logic:match>
													</logic:notMatch>
													</logic:notEqual>
													<%
													if(i%2==0){ %>
													</tr>
													<tr>
													<%} %>
													<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",note_num," scope="session">           
														<logic:match name="lawbaseForm" property="field_str_item" value="note_num`" scope="session"> 
														<%
																i++;
															 %>
														<td align="center" class="RecordRow" nowrap>
															<bean:write name="note_num"/>
															 <bean:message key="law_maintenance.contain" />
														</td>
														<td align="left" class="RecordRow" nowrap>
															&nbsp;<input type="text" name="note_num" maxlength="50" size="30" value="" class="text4">&nbsp;
														</td>
														</logic:match>
													</logic:notMatch>
													<%
													boolean br = false;
													if(i%2==0){
														br = true;
													%>
													</tr>
													<tr>
													<%} %>
													<%int k = 0; %>
												<logic:iterate id="element" name="lawbaseForm" property="itemList">
													<logic:notEqual value="D" name="element" property="itemtype">
															<%
																i++;
																k++;
															%>
														<%if((k == len && br) || (k == len-1 && len % 2 == 0 && br) ){%>
														<td align="center" class="RecordRow" nowrap style="border-bottom:none">
															<bean:write name="element" property="itemdesc"/><%=len %>
														</td>
														<td align="left" class="RecordRow" nowrap style="border-bottom:none">
															&nbsp;<input type="text" name='<bean:write name="element" property="itemid"/>' maxlength="50" size="30" value="" class="text4">&nbsp;
														</td>
														<%}else{ %>
														<td align="center" class="RecordRow" nowrap>
															<bean:write name="element" property="itemdesc"/>
														</td>
														<td align="left" class="RecordRow" nowrap>
															&nbsp;<input type="text" name='<bean:write name="element" property="itemid"/>' maxlength="50" size="30" value="" class="text4">&nbsp;
														</td>
														<%} %>
													</logic:notEqual>
													<%
												    if(i%2==0){ %>
													</tr>
													<tr>
													<%} %>
													<logic:equal value="D" name="element" property="itemtype">
													</tr>
													<tr>
														<td align="center" class="RecordRow" nowrap>
															<bean:write name="element" property="itemdesc"/>
														</td>
														<td align="center" class="RecordRow" nowrap>
															&nbsp;<input type="text" name="<bean:write name="element" property="itemid"/>_start" maxlength="50" size="30" value="" class="text4" readonly="readonly" extra="editor" dropDown="dropDownDate">&nbsp;
														</td>
														<td align="center" class="RecordRow" nowrap>
															<bean:message key="label.query.to" />
														</td>
														<td align="left" class="RecordRow" nowrap>
															&nbsp;<input type="text" name="<bean:write name="element" property="itemid"/>_end" maxlength="50" size="30" value="" class="text4" readonly="readonly" extra="editor" dropDown="dropDownDate">&nbsp;
														</td>
													</tr>
													</logic:equal>
												</logic:iterate>
													<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",issue_date," scope="session">           
													<logic:match name="lawbaseForm" property="field_str_item" value="issue_date`" scope="session"> 
													</tr>
													<tr>
														<td align="center" class="RecordRow" nowrap>
															<bean:write name="issue_date"/>
															<bean:message key="label.query.from" />
														</td>
														<td align="center" class="RecordRow" nowrap>
															&nbsp;<input type="text" name="issue_date_start" maxlength="50" size="30" value="" class="text4" readonly="readonly" extra="editor" dropDown="dropDownDate">&nbsp;
														</td>
														<td align="center" class="RecordRow" nowrap>
															<bean:message key="label.query.to" />
														</td>
														<td align="left" class="RecordRow" nowrap>
															&nbsp;<input type="text" name="issue_date_end" maxlength="50" size="30" value="" class="text4" readonly="readonly" extra="editor" dropDown="dropDownDate">&nbsp;
														</td>
													</tr>
													</logic:match>
													</logic:notMatch>
													<logic:notEqual name="lawbaseForm" property="basetype" value="5">
													<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",implement_date," scope="session">           
													<logic:match name="lawbaseForm" property="field_str_item" value="implement_date`" scope="session"> 
													<tr>
														<td align="center" class="RecordRow" nowrap>
															<bean:write name="implement_date"/>
															<bean:message key="label.query.from" />
														</td>
														<td align="center" class="RecordRow" nowrap>
															&nbsp;<input type="text" name="implement_date_start" maxlength="50" size="30" value="" class="text4" readonly="readonly" extra="editor" dropDown="dropDownDate">&nbsp;
														</td>
														<td align="center" class="RecordRow" nowrap>
															<bean:message key="label.query.to" />
														</td>
														<td align="left" class="RecordRow" nowrap>
															&nbsp;<input type="text" name="implement_date_end" maxlength="50" size="30" value="" class="text4" readonly="readonly" extra="editor" dropDown="dropDownDate">&nbsp;
														</td>
													</tr>
													</logic:match>
													</logic:notMatch>
													<logic:match name="lawbaseForm" property="field_str_item" value="valid_date`" scope="session">           
													<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",valid_date," scope="session">           
													<tr>
														<td align="center" class="RecordRow" nowrap>
														<bean:write name="valid_date"/>
															<bean:message key="label.query.from" />
														</td>
														<td align="center" class="RecordRow" nowrap>
															&nbsp;<input type="text" name="valid_date_start" maxlength="50" size="30" value="" class="text4" readonly="readonly" extra="editor" dropDown="dropDownDate">&nbsp;
														</td>
														<td align="center" class="RecordRow" nowrap>
															<bean:message key="label.query.to" />
														</td>
														<td align="left" class="RecordRow" nowrap>
															&nbsp;<input type="text" name="valid_date_end" class="text4" maxlength="50" size="30" value="" readonly="readonly" extra="editor" dropDown="dropDownDate">&nbsp;
														</td>
													</tr>
													</logic:notMatch>
													</logic:match>
													</logic:notEqual>
													</logic:notEmpty>
													
													<logic:empty name="lawbaseForm" property="field_str_item">
													<%
														int j=0;
													 %>
													<tr>
														<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",name," scope="session">
														<%j++; %>           
															<td align="center" class="RecordRow" nowrap style="border-bottom:none">
																<bean:message key="column.law_base.filename" />
																<bean:message key="law_maintenance.contain" />
															</td>
															<td align="center" class="RecordRow" nowrap style="border-bottom:none">
																&nbsp;<input type="text" name="name" maxlength="50" size="30" value="" class="text4">&nbsp;
															</td>
														</logic:notMatch>
														<%j++; %>
														<td align="center" class="RecordRow" nowrap style="border-bottom:none">
															<bean:message key="column.law_base.title" />
															<bean:message key="law_maintenance.contain" />
														</td>
														<td align="left" class="RecordRow" nowrap style="border-bottom:none">
															&nbsp;<input type="text" name="title" maxlength="50" size="30" value="" class="text4">&nbsp;
														</td>
														<%
														if(j%2==0){
														%>
													</tr>
													<tr>
													<%}%>
													<logic:notEqual name="lawbaseForm" property="basetype" value="5">
														<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",type," scope="session">
														<%j++; %>           
														<td align="center" class="RecordRow" nowrap>
															分类号
														</td>
														<td align="center" class="RecordRow" nowrap>
															&nbsp;<input type="text" name="type" maxlength="50" size="30" value="" class="text4">&nbsp;
														</td>
														</logic:notMatch>
													</logic:notEqual>
													<%
														if(j%2==0){
														%>
													</tr>
													<tr>
													<%}%>
													<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",content_type," scope="session">
													<%j++; %>           
														<td align="center" class="RecordRow" nowrap>
															<bean:message key="lable.lawfile.contenttype" />
														</td>
														<td align="left" class="RecordRow" nowrap>
															&nbsp;<input type="text" name="content_type" maxlength="50" size="26" value="" class="text4">&nbsp;
															<img  src="/images/code.gif" align="absmiddle" onclick="showDateSelectBox('content_type');" >
														</td>
													</logic:notMatch>
													<%
														if(j%2==0){
														%>
													</tr>
													<tr>
													<%}%>
													<logic:notEqual name="lawbaseForm" property="basetype" value="5">
													<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",valid," scope="session">
													<%j++; %>           
														<td align="center" class="RecordRow" nowrap>
															<bean:message key="lable.lawfile.valid" />
														</td>
														<td align="left" class="RecordRow" nowrap>
															&nbsp;<SELECT name="valid">
																<OPTION value="1">
																	<bean:message key="lable.lawfile.availability" />
																</OPTION>
																<OPTION value="0">
																	<bean:message key="lable.lawfile.allabolish" />
																</OPTION>
																<OPTION value="2">
																	<bean:message key="lable.lawfile.partabolish" />
																</OPTION>
																<OPTION value="3">
																	<bean:message key="lable.lawfile.editing" />
																</OPTION>
																<OPTION value="4">
																	<bean:message key="lable.lawfile.other" />
																</OPTION>
																<OPTION value="" selected="selected">						
																</OPTION>
															</SELECT>
														</td>
													</logic:notMatch>
													<%
														if(j%2==0){
														%>
													</tr>
													<tr>
													<%}%>
													<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",issue_org," scope="session">
													<%j++; %>           
														<td align="center" class="RecordRow" nowrap>
															<bean:message key="lable.lawfile.issue_org" />
														</td>
														<td align="left" class="RecordRow" nowrap>
															&nbsp;<input type="text" name="issue_org" maxlength="50" size="30" value="" class="text4">&nbsp;
														</td>
													</logic:notMatch>
													<%
														if(j%2==0){
														%>
													</tr>
													<tr>
													<%}%>
													<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",notes," scope="session">      
													<%j++; %>     
														<td align="center" class="RecordRow" nowrap>
														<bean:message key="lable.lawfile.note" />

														</td>
														<td align="center" class="RecordRow" nowrap>
															&nbsp;<input type="text" name="notes" maxlength="50" size="30" value="" class="text4">&nbsp;
														</td>
													</logic:notMatch>
													<%
														if(j%2==0){
														%>
													</tr>
													<tr>
													<%}%>
													</logic:notEqual>
													
													<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",note_num," scope="session">
													<%j++; %>           
														<td align="center" class="RecordRow" nowrap>
															<bean:message key="lable.lawfile.notenum" />
															 <bean:message key="law_maintenance.contain" />
														</td>
														<td align="left" class="RecordRow" nowrap>
															&nbsp;<input type="text" name="note_num" maxlength="50" size="30" value="" class="text4">&nbsp;
														</td>
													</logic:notMatch>
													</tr>
													<tr>
													<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",issue_date," scope="session">           
														<td align="center" class="RecordRow" nowrap>
															<bean:message key="column.law_base.issuedate" />
															<bean:message key="label.query.from" />
														</td>
														<td align="center" class="RecordRow" nowrap>
															&nbsp;<input type="text" name="issue_date_start" maxlength="50" size="30" value="" class="text4" readonly="readonly" 
															extra="editor" dropDown="dropDownDate"
															/>&nbsp;
														</td>
														<td align="center" class="RecordRow" nowrap>
															<bean:message key="label.query.to" />
														</td>
														<td align="left" class="RecordRow" nowrap>
															&nbsp;<input type="text" name="issue_date_end" maxlength="50" size="30" value="" class="text4" readonly="readonly" 
															extra="editor" dropDown="dropDownDate" >&nbsp;
														</td>
													</tr>
													</logic:notMatch>
													<logic:notEqual name="lawbaseForm" property="basetype" value="5">
													<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",implement_date," scope="session">           
													<tr>
														<td align="center" class="RecordRow" nowrap>
															<bean:message key="column.law_base.impdate" />
															<bean:message key="label.query.from" />
														</td>
														<td align="center" class="RecordRow" nowrap>
															&nbsp;<input type="text" name="implement_date_start" maxlength="50" size="30" value="" class="text4" readonly="readonly" extra="editor" dropDown="dropDownDate">&nbsp;
														</td>
														<td align="center" class="RecordRow" nowrap>
															<bean:message key="label.query.to" />
														</td>
														<td align="left" class="RecordRow" nowrap>
															&nbsp;<input type="text" name="implement_date_end" maxlength="50" size="30" value="" class="text4" readonly="readonly" extra="editor" dropDown="dropDownDate">&nbsp;
														</td>
													</tr>
													</logic:notMatch>
													<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",valid_date," scope="session">           
													<tr>
														<td align="center" class="RecordRow" nowrap>
														<bean:message key="lable.lawfile.invalidationdate" />
															<bean:message key="label.query.from" />
														</td>
														<td align="center" class="RecordRow" nowrap>
															&nbsp;<input type="text" name="valid_date_start" maxlength="50" size="30" value="" class="text4" readonly="readonly" extra="editor" dropDown="dropDownDate">&nbsp;
														</td>
														<td align="center" class="RecordRow" nowrap>
															<bean:message key="label.query.to" />
														</td>
														<td align="left" class="RecordRow" nowrap>
															&nbsp;<input type="text" name="valid_date_end" class="text4" maxlength="50" size="30" value="" readonly="readonly" extra="editor" dropDown="dropDownDate">&nbsp;
														</td>
													</tr>
													</logic:notMatch>
													</logic:notEqual>
													</logic:empty>
												</table>
											</td>
										</tr>
										<tr>
											<td height="15" colspan="4"></td>
										</tr>
									</table>
								</td>
							</tr>
							
							<tr class="list3" style="padding-top: 5px;">
								<td colspan="4" align="center">
								    <bean:define id="baseid" name="lawbaseForm" property="base_id"/>
								    <input type="hidden" name="a_base_id" value="<%=PubFunc.encrypt((String)baseid) %>">
								    <INPUT type="hidden" name="basetype" value="<bean:write name="lawbaseForm" property="basetype" filter="true"/>">
								    <input type="hidden" name="itemid" id="itemid" value='<%=itemId %>'/>
									<input type="hidden" name="itemdesc" id="itemdesc" value='<%=itemDesc %>'/>
									<input type="hidden" name="itemtype" id="itemtype" value='<%=itemType %>'/>
									<input type="hidden" name="itemCodeid" id="itemCodeid" value='<%=itemCodeid %>'/>
									<input type="hidden" name="num" id="num" value='<%=num %>'/>
									<input type="hidden" name="itemvalue" id="itemvalue" value=""/>
									<input type="submit" name="b_query" value="<bean:message key="button.query"/>" class="mybutton"/>
									<input type="reset" name="br_return" class="mybutton" value="<bean:message key="button.clear"/>">
									<input type="button" class="mybutton" onclick="exeAdd('/selfservice/lawbase/lawtext/law_maintenance.do?b_query=link&encryptParam=<%=PubFunc.encrypt("a_base_id=" + (String)baseid) %>')" value="<bean:message key="button.return" />">
								</td>
							</tr>
						</table>
					</form>
						 <div id="date_panel" style="display:none;">
						 	
						 </div>
				</td>
			</tr>
		</table>
	</body>
	<script type="text/javascript">
		<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",name," scope="session">           
			<logic:match name="lawbaseForm" property="field_str_item" value="name`" scope="session"> 
													document.getElementsByName("name")[0].value="";
			</logic:match>
		</logic:notMatch>
		<logic:match name="lawbaseForm" property="field_str_item" value="title`" scope="session"> 
		document.getElementsByName("title")[0].value="";
		</logic:match>
		<logic:notEqual name="lawbaseForm" property="basetype" value="5">
			<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",type," scope="session">           
				<logic:match name="lawbaseForm" property="field_str_item" value="type`" scope="session"> 
					document.getElementsByName("type")[0].value="";
				</logic:match>
			</logic:notMatch>
		</logic:notEqual>
		<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",content_type," scope="session">           
			<logic:match name="lawbaseForm" property="field_str_item" value="content_type`" scope="session"> 
					document.getElementsByName("content_type")[0].value="";
				</logic:match>
			
		</logic:notMatch>
		<logic:notEqual name="lawbaseForm" property="basetype" value="5">
			<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",valid," scope="session">           
				<logic:match name="lawbaseForm" property="field_str_item" value="valid`" scope="session"> 
					document.getElementsByName("valid")[0].value="";
				</logic:match>
				
			</logic:notMatch>
			<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",issue_org," scope="session">           
				<logic:match name="lawbaseForm" property="field_str_item" value="issue_org`" scope="session"> 
					document.getElementsByName("issue_org")[0].value="";
				</logic:match>
				
			</logic:notMatch>
			<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",notes," scope="session">           
				<logic:match name="lawbaseForm" property="field_str_item" value="notes`" scope="session"> 
					document.getElementsByName("notes")[0].value="";
				</logic:match>
				
			</logic:notMatch>
		</logic:notEqual>
		<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",note_num," scope="session">           
			<logic:match name="lawbaseForm" property="field_str_item" value="note_num`" scope="session"> 
					document.getElementsByName("note_num")[0].value="";
				</logic:match>
			
		</logic:notMatch>
		<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",issue_date," scope="session">           
			<logic:match name="lawbaseForm" property="field_str_item" value="issue_date`" scope="session"> 
					document.getElementsByName("issue_date_start")[0].value="";
			document.getElementsByName("issue_date_end")[0].value="";
				</logic:match>
			
		</logic:notMatch>
		<logic:notEqual name="lawbaseForm" property="basetype" value="5">
			<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",implement_date," scope="session">           
				<logic:match name="lawbaseForm" property="field_str_item" value="implement_date`" scope="session"> 
					document.getElementsByName("implement_date_start")[0].value="";
				document.getElementsByName("implement_date_end")[0].value="";
				</logic:match>
				
			</logic:notMatch>
			<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",valid_date," scope="session">           
				<logic:match name="lawbaseForm" property="field_str_item" value="valid_date`" scope="session"> 
					document.getElementsByName("valid_date_start")[0].value="";
				document.getElementsByName("valid_date_end")[0].value="";
				</logic:match>
				
			</logic:notMatch>
		</logic:notEqual>
		if(!getBrowseVersion()){//兼容非IE浏览器  wangb 20171127
			var list3 = document.getElementsByClassName('list3')[0];//获取按钮所在的 tr   wangb 20171127
			list3.style.height='40px'; //调整高度
		}
	</script>
</html>
