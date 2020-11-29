<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.businessobject.train.TrainClassBo" %>

<html>
<head>

<script language='javascript'>
	function goback()
	{
		var operator=${trainClassForm.operator};
		if(operator==1)
			trainClassForm.action="/train/job/browseTrainClassList.do?b_query=query";
		else if(operator==2)
			trainClassForm.action="/train/job/browseTrainClassList.do?b_myClass=link";
		else if(operator==3)
			trainClassForm.action="/train/signUp/browseSignUpAuditingList.do?b_query=link";
		
		trainClassForm.submit();
	}
	//试听
	function learn(courseid) {
	var url = "/train/resource/mylessons/learncoursebyextjs.jsp?opt=sss`lesson=" + courseid;
	var fram = "/train/resource/mylessons/learniframe.jsp?src="+url;
	//window.showModalDialog(fram, "", "dialogWidth:880px; dialogHeight:700px;resizable:no;center:yes;scroll:yes;status:yes");
	window.open(fram,'','fullscreen=yes,left=0,top=0,width='+ (screen.availWidth - 10) +',height='+ (screen.availHeight-50) +',scrollbars,resizable=no,toolbar=no,location=no,status=no,menubar=no');
	}
function loadFile(r4114,type){
	var target_url="/train/resource/file_upload.do?b_query=link`r0701="+r4114+"`type="+type+"`myself=0";
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
	var return_vo= window.showModalDialog(iframe_url, "memoFld_win", "dialogWidth:700px; dialogHeight:330px;resizable:no;center:yes;scroll:no;status:no");
}
</script>

</head>
<body>
<html:form action="/train/job/browseTrainClassList">


<table border='0' width='100%' cellpadding="0" cellspacing="0">


<tr>
	<td>&nbsp;&nbsp;</td>
	<td>
	<Br>
	<table border='0' width='95%'  class='RecordRow' cellpadding="0" cellspacing="0" style="border: solid 1px;">
		<tr><td height='40'>&nbsp;</td></tr>
		<tr><td align='center'>
		
		
				<table width="90%" border="0"  cellspacing="0" cellpadding="0" style="border-collapse: collapse;">
					<tr>		
					   <td height="40" class='tableRow'  colspan="8">
					   &nbsp;<strong><bean:write name="trainClassForm" property="trainClassDesc.r3130" filter="true"/></strong>
					   </td>
					</tr>
					<tr>		
					   <td height="40" class='RecordRow'  colspan="8">
					   <Br>
					  &nbsp; <bean:message key="train.job.signUpNumber"/>：
					  [<bean:write name="trainClassForm" property="trainClassDesc.appealCount" filter="true"/>]人
					  ,&nbsp;&nbsp;<bean:message key="train.job.trainClassTime"/>：<bean:write name="trainClassForm" property="trainClassDesc.startDate" filter="true"/>
					 - <bean:write name="trainClassForm" property="trainClassDesc.endDate" filter="true"/>
					  <br>
					   &nbsp;&nbsp;<bean:message key="train.job.courseTime"/>：<bean:write name="trainClassForm" property="trainClassDesc.r3112" filter="true"/>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="train.job.money"/>：<bean:write name="trainClassForm" property="trainClassDesc.r3111" filter="true"/>
					  <logic:notEmpty name="trainClassForm" property="trainClassDesc.r3109">
					  <br>
					  &nbsp;&nbsp;培训对象：<bean:write name="trainClassForm" property="trainClassDesc.r3109" filter="true"/>
					  </logic:notEmpty>
					  <br>
					  &nbsp;&nbsp;<bean:message key="train.job.trainPosition"/>：
					  <bean:define id="tid" name="trainClassForm" property="trainClassDesc.r1001"></bean:define>
					  <%String r1001 = SafeCode.encode(PubFunc.encrypt(tid.toString())); %>
					  <a href='/train/job/browseTrainClassList.do?b_desc=query&operator=r10&id=<%=r1001 %>'>
					  <bean:write name="trainClassForm" property="trainClassDesc.r1011" filter="true"/>
					  </a>
					  <br>
					  &nbsp;&nbsp;<bean:message key="train.job.courseArrange"/>：
					  <Br>
					  <table border='0' cellpadding="0" cellspacing="0">
					  	<tr>
					  		<td> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </td>
					  		<td> 
					  		&nbsp;<bean:write name="trainClassForm" property="trainClassDesc.r3122" filter="false"/>
					  		 </td>
					  	</tr>
					  </table> 
					  <Br>
					  &nbsp;&nbsp;活动说明：
					  <Br>
					  <table border='0'>
					  	<tr>
					  		<td> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </td>
					  		<td> 
					  		&nbsp;<bean:write name="trainClassForm" property="trainClassDesc.r3117" filter="false"/>
					  		 </td>
					  	</tr>
					  </table> 
					   </td>
					</tr>
					<tr>		
					   <td height="30" class='RecordRow' style="border-bottom: none;" colspan="8">
					   &nbsp;<bean:message key="train.job.courseList"/>
					   </td>
					</tr>
					<tr>		
					   <td colspan="8">
					   <table width="100%" border="0" cellpadding="0" cellspacing="0" style="border-collapse: collapse;">
					   <tr height="30">
						<%int j=0; %>
						<logic:iterate id="element" name="trainClassForm" property="r41list" indexId="index">
							 <logic:notEqual name="element" property="itemid" value="r4101">
									<%if(j==1){%> 
									 <td align="center" class="RecordRow" nowrap>
									 <%}else{ %>
									 <td align="center" class="RecordRow" nowrap>
									 <%} %>
											 <bean:write name="element" property="itemdesc" filter="true" />
									 </td>
							 </logic:notEqual>
							 <%j++; %>
					 	</logic:iterate>
				
					 </tr>
					 <%int i=0; %>
					 <hrms:paginationdb id="element" name="trainClassForm" sql_str="trainClassForm.sql" table="" 
						where_str="trainClassForm.wherestr" columns="trainClassForm.columns" 
							order_by="order by r4101" page_id="pagination" pagerows="999999999">
    						<%i=1; %>
    					<tr style="border-left: 1px solid #000000;">	
				    	<bean:define id="nid" name='element' property='r4101'/>
				    	<logic:iterate id="fielditem"  name="trainClassForm"  property="r41list" indexId="index">
					      <logic:notEqual name="fielditem" property="itemid" value="r4101">
					      <%if(i==2){ %>
					      <logic:notEqual name="fielditem" property="itemtype" value="N">
					      	<td align="left" class="RecordRow" nowrap height="30">
					      </logic:notEqual>
					      <logic:equal name="fielditem" property="itemtype" value="N">
					      	<td align="right" class="RecordRow" nowrap height="30">
					      </logic:equal>
					      <%}else{ %>
					      <logic:notEqual name="fielditem" property="itemtype" value="N">
				    		<td align="left" class="RecordRow" nowrap height="30">
				    	  </logic:notEqual>
				    	  <logic:equal name="fielditem" property="itemtype" value="N">
				    	  	<td align="right" class="RecordRow" nowrap height="30">
				    	  </logic:equal>
				    		<%} %>
				               <logic:notEqual name="fielditem" property="codesetid" value="0">
				               	 	<logic:notEqual value="55" name="fielditem" property="codesetid">
				               		<logic:notEqual name="fielditem" property="itemid" value="${trainClassForm.lesson}">
						                  <logic:equal name="fielditem" property="itemid" value="r4105">
						                  	<bean:define id="r4105" name="element" property="r4105"></bean:define>
						                  		&nbsp;<%=TrainClassBo.getProgrammeName(r4105.toString()) %>&nbsp;
						                  </logic:equal>
						                  <logic:equal name="fielditem" property="itemid" value="r4106">
						                  	<bean:define id="r4106" name="element" property="r4106"></bean:define>
						                  	<%String r4106s = SafeCode.encode(PubFunc.encrypt(r4106.toString())); %>
						                  	<a href='/train/job/browseTrainClassList.do?b_desc=query&operator=r04&id=<%=r4106s %>'>
						                  		&nbsp;<%=TrainClassBo.getTeacherName(r4106.toString()) %>&nbsp;</a>
						                  </logic:equal>
						                  <logic:equal name="fielditem" property="itemid" value="r4114">
						                  	<bean:define id="r4114" name="element" property="r4114"></bean:define>
						                  	<%String r4114s = SafeCode.encode(PubFunc.encrypt(r4114.toString())); %>
						                  	<a href='/train/job/browseTrainClassList.do?b_desc=query&operator=r07&id=<%=r4114s %>'>
						                  		&nbsp;<%=TrainClassBo.getDataName(r4114.toString()) %>&nbsp;</a><%if(TrainClassBo.haveFile(r4114.toString())){ %>[<a onclick="loadFile('<%=r4114s %>','0');" href='###'>附件</a>]<%} %>
						                  </logic:equal>
						                   <logic:notEqual name="fielditem" property="itemid" value="r4105">
						                   <logic:notEqual name="fielditem" property="itemid" value="r4106">
						                   <logic:notEqual name="fielditem" property="itemid" value="r4114">
						          	   		<hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
						          	   		&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;  
						          	   		</logic:notEqual>     
						          	   		</logic:notEqual>
						          	   		</logic:notEqual>
						          	 </logic:notEqual> 
						          	 </logic:notEqual>
						          	  <logic:equal name="fielditem" property="codesetid" value="55">
						          	   <hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>
						          	  <bean:define id="lessons" name="element" property="${fielditem.itemid}"></bean:define>
						          	   <%String r5000=TrainClassBo.getLessinsCode(lessons.toString()); 
						          	   if(r5000!=null&&(!"".equals(r5000))){
						          	       r5000 = SafeCode.encode(PubFunc.encrypt(r5000));
						          	   %>
						          	   <a href="###" onclick="learn('<%=r5000 %>');">&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;</a>
						          	   <%}else{ %>
						          	  	&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;
						          	  	<%} %>
						          	  </logic:equal>
						          	 <logic:equal name="fielditem" property="itemid" value="${trainClassForm.lesson}"> 
						          	 		<bean:define id="lesson" name="element" property="${trainClassForm.lesson}"></bean:define>
						          	 		<hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
						          	   	<%String ilearn = SafeCode.encode(PubFunc.encrypt(lesson.toString())); %>
						          	   	<a href='###' onclick="learn('<%=ilearn %>');">
						          	   		&nbsp;<bean:write name="codeitem" property="codename" />&nbsp; </a>
						          	 </logic:equal>    
				               </logic:notEqual>
				               <logic:equal name="fielditem" property="codesetid" value="0">
				               		<logic:notEqual name="fielditem" property="itemid" value="${trainClassForm.lesson}">
						                  <logic:equal name="fielditem" property="itemid" value="r4105">
						                  	<bean:define id="r4105" name="element" property="r4105"></bean:define>
						                  		&nbsp;<%=TrainClassBo.getProgrammeName(r4105.toString()) %>&nbsp;
						                  </logic:equal>
						                  <logic:equal name="fielditem" property="itemid" value="r4106">
						                  	<bean:define id="r4106" name="element" property="r4106"></bean:define>
						                  	<%String tearcherid = SafeCode.encode(PubFunc.encrypt(r4106.toString())); %>
						                  	<a href='/train/job/browseTrainClassList.do?b_desc=query&operator=r04&id=<%=tearcherid %>'>
						                  		&nbsp;<%=TrainClassBo.getTeacherName(r4106.toString()) %>&nbsp;</a>
						                  </logic:equal>
						                  <logic:equal name="fielditem" property="itemid" value="r4114">
						                  	<bean:define id="r4114" name="element" property="r4114"></bean:define>
						                  	<%String r4114s = SafeCode.encode(PubFunc.encrypt(r4114.toString())); %>
						                  	<a href='/train/job/browseTrainClassList.do?b_desc=query&operator=r07&id=<%=r4114s %>'>
						                  		&nbsp;<%=TrainClassBo.getDataName(r4114.toString()) %>&nbsp;</a><%if(TrainClassBo.haveFile(r4114.toString())){ %>[<a onclick="loadFile('<%=r4114s %>','0');" href='#'>附件</a>]<%} %>
						                  </logic:equal>
						                   <logic:notEqual name="fielditem" property="itemid" value="r4105">
						                   <logic:notEqual name="fielditem" property="itemid" value="r4106">
						                   <logic:notEqual name="fielditem" property="itemid" value="r4114">
						          	   		<hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
						          	   		&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;  
						          	   		</logic:notEqual>     
						          	   		</logic:notEqual>
						          	   		</logic:notEqual>
						          	 </logic:notEqual>  
						          	 <logic:equal name="fielditem" property="itemid" value="${trainClassForm.lesson}"> 
						          	 		<bean:define id="lesson" name="element" property="${trainClassForm.lesson}"></bean:define>
						          	 		<hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
						          	   	<%String ilearn = SafeCode.encode(PubFunc.encrypt(lesson.toString())); %>
						          	   	<a href='###' onclick="learn('<%=ilearn %>');">
						          	   		&nbsp;<bean:write name="codeitem" property="codename" />&nbsp; </a>
						          	 </logic:equal>
						          	<logic:notEqual name="fielditem" property="itemid" value="${trainClassForm.lesson}">  
						          	<logic:notEqual name="fielditem" property="itemid" value="r4105">
						            <logic:notEqual name="fielditem" property="itemid" value="r4106">
						            <logic:notEqual name="fielditem" property="itemid" value="r4114"> 
				                   		&nbsp;<bean:write name="element" property="${fielditem.itemid}" filter="false"/>&nbsp; 
				                   	</logic:notEqual>     
						          	</logic:notEqual>
						          	</logic:notEqual>
						          	</logic:notEqual>                
				               </logic:equal>                              
					      </td> 
					      </logic:notEqual>
					      <% i++;  %>  
				    	</logic:iterate>
				    	
				      </tr>
				     </hrms:paginationdb>      
	
						 <% if(i==0){  %>	
						 <tr style="padding: 0px;margin: 0px;" height="30">
						 		<%for(int a=1;a<j;a++){ 
						 		if(a==1){%>
								 <td height="30" class='RecordRow' align='left' >&nbsp; </td>
								 <%}else{ %>
								 <td  class='RecordRow'  align='left'>&nbsp;</td>
								 <%}
						 		}%>
						  </tr>
						 
						 <% } %>
					     </table>
					   </td>
				   </tr>
					
				</table>
				
				<table width="90%" style="margin-top: 5px;" cellspacing="0" >
					<tr><td>		
				<Input type='button' onclick='goback()'  value='<bean:message key="button.return"/>'  class="mybutton" />
					</td></tr>
				</table>
				
				
		</td></tr>
		
		
		<tr><td hight='30'><br><br>&nbsp;</td></tr>
	</table>

</td></tr>
</table>
</html:form>

</body>
</html>