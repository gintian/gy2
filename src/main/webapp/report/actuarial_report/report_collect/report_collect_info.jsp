<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				 org.apache.commons.beanutils.LazyDynaBean,
				 com.hjsj.hrms.actionform.report.actuarial_report.ActuarialCollectReportForm" %>

<% 
	
	ActuarialCollectReportForm actuarialCollectReportForm=(ActuarialCollectReportForm)session.getAttribute("actuarialCollectReportForm");	
	ArrayList actuarialReportStatusList=actuarialCollectReportForm.getActuarialReportStatusList();
    String selfUnitcode=actuarialCollectReportForm.getSelfUnitcode();
    String unitCode=actuarialCollectReportForm.getUnitCode();
    String isCollectUnit=actuarialCollectReportForm.getIsCollectUnit();
    String cycleStatus = actuarialCollectReportForm.getCycleStatus();
    String rootUnit = actuarialCollectReportForm.getRootUnit();
  //  System.out.println("isCollectUnit:"+isCollectUnit);
 %>


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GB18030">
<title></title>
</head>
<link href="/css/css1.css" rel="stylesheet" type="text/css">

<script language='javascript' >
function changeCycle()
{
	document.actuarialCollectReportForm.action="/report/actuarial_report/report_collect.do?b_query=query";
	parent.mil_menu.document.location = "/report/report_collect/reportOrgCollecttree.jsp?type=actuarial&cycle_id="+document.actuarialCollectReportForm.cycle_id.value;
	document.actuarialCollectReportForm.submit();

}
 
//查看打回说明
function description(report_id,unitcode,cycle_id)
{	  
	  		var info='';
			var thecodeurl="/report/report_collect/reportOrgCollecttree.do?b_lookDesc=description`bopt=3`report_id="+report_id+"`unitcode="+unitcode+"`cycle_id="+cycle_id;	
			var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl; 
			var win= window.showModalDialog(iframe_url,info, 
		        "dialogWidth:430px; dialogHeight:360px;resizable:no;center:yes;scroll:yes;status:no");

}
var curObjTr= null;
var oldObjTr_c= "";
function tr_onclick(objTr,bgcolor)
{
	/*
	if(curObjTr!=null)
		curObjTr.style.background=oldObjTr_c;
	curObjTr=objTr;
	oldObjTr_c=bgcolor;
	curObjTr.style.background='FFF8D2';
	* */
	if(curObjTr!=null)
		curObjTr.style.backgroundColor="";
	curObjTr=objTr;
	oldObjTr_c="FFF8D2";
	curObjTr.style.backgroundColor='FFF8D2';		 
	//curObj.style.color='#ffdead'; 
}
</script>
<hrms:themes />
<body>

<table width='100%' id="temp" style="margin-top: -3px;margin-left: -5px;"> <tr><td style="padding-left: 2px;">

<html:form action="/report/actuarial_report/report_collect">
<html:hidden styleId="paracopy" name="actuarialCollectReportForm"
				property="paracopy" />
				<html:hidden styleId="paracopy2" name="actuarialCollectReportForm"
				property="paracopy2" />
				<html:hidden styleId="isfillpara" name="actuarialCollectReportForm"
				property="isfillpara" />
				<html:hidden styleId="isfillpara2" name="actuarialCollectReportForm"
				property="isfillpara2" />
<% if(actuarialReportStatusList.size()!=0){ %>

填报周期&nbsp;<html:select name="actuarialCollectReportForm" property="cycle_id" size="1" style="vertical-align: middle;" onchange="changeCycle()">
  	 <html:optionsCollection property="cycleList" value="dataValue" label="dataName"/>
	</html:select>
</td></tr>
<tr><td>

<table width='90%' >
<tr><td>
<table width="100%" border="0" cellspacing="0" align="left" cellpadding="0" class="ListTable" style="margin-left: -1px;">
   	  <thead>
   	  <tr>
   	  <% if(cycleStatus.equals("04")){ %>
   	  <logic:equal  name="actuarialCollectReportForm"  property="rootUnit" value="1">
   	  <td  width='15%' align="center" class="TableRow" nowrap>
			<input type="checkbox" name="selbox" onclick="batch_select(this,'id');" title='<bean:message key="label.query.selectall"/>'>
			</td> 
   	  </logic:equal>
   	  <logic:notEqual name="actuarialCollectReportForm"  property="rootUnit" value="1">
   	  	<logic:notEqual  name="actuarialCollectReportForm"  property="isCollectUnit" value="1">
			<%if(!selfUnitcode.equals(unitCode)){ %>
			<td  width='15%' align="center" class="TableRow" nowrap>
			<input type="checkbox" name="selbox" onclick="batch_select(this,'id');" title='<bean:message key="label.query.selectall"/>'>
			</td> 
			<%} %>
		 </logic:notEqual>	
   	  </logic:notEqual>
   	  	<% } %>				
      
         <td  width='50%'  align="center" class="TableRow" nowrap>
		    名称
         </td>         
		 <td width='15%' align="center" class="TableRow" nowrap>
		    状态
         </td>
		 <td width='15%'  align="center" class="TableRow" nowrap>
		    操作
         </td>
		</tr>
	   </thead>
	   <% int i=0; %>
	   <logic:iterate id="element"  name="actuarialCollectReportForm"  property="actuarialReportStatusList" >
	   	 <logic:notEqual name="actuarialCollectReportForm"  property="kmethod" value="0">
	   		 <logic:notEqual name="element" property="tabid"  value="U02_1">
	   		  <logic:notEqual name="element" property="tabid"  value="U02_2">
	   		   <logic:notEqual name="element" property="tabid"  value="U02_4">
	   		   
	   		 
	    <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow" onclick='tr_onclick(this,"#F3F5FC");'>
          <%}
          else
          {%>
          <tr class="trDeep" onclick='tr_onclick(this,"#F3F5FC");'>
          <%
          }
          i++;    
          
           LazyDynaBean a_bean=(LazyDynaBean)pageContext.getAttribute("element");
           String tabid=(String)a_bean.get("tabid");
           String status=(String)a_bean.get("status");
          // System.out.println(status);
                
          %> 
          <% if(cycleStatus.equals("04")){ %>
      <logic:equal  name="actuarialCollectReportForm"  property="rootUnit" value="1">
   	  		 <%if(selfUnitcode.equals(unitCode)){ %>
   	  		  <logic:equal  name="element"  property="status" value="1">
			<td align="center" class="RecordRow"   nowrap>
	   			<input type='checkbox' name='id'   value='<bean:write name="element" property="tabid" filter="false" />' />
	   		</td>
	   		</logic:equal>
	   		 <logic:notEqual  name="element"  property="status" value="1">
			<td align="center" class="RecordRow"   nowrap>
	   			<input type='checkbox' name='id' disabled='disabled'   value='<bean:write name="element" property="tabid" filter="false" />' />
	   		</td>
	   		</logic:notEqual>
   	  		 <%}else{ %>
   	  	
			 <logic:equal  name="element"  property="status" value="1">
		
			<td align="center" class="RecordRow"   nowrap>
	   			<input type='checkbox' name='id'   value='<bean:write name="element" property="tabid" filter="false" />' />
	   		</td>
	   		</logic:equal>
	   		 <logic:notEqual  name="element"  property="status" value="1">
			<%if(!selfUnitcode.equals(unitCode)){ %>
			<td align="center" class="RecordRow"   nowrap>
	   			<input type='checkbox' name='id' disabled='disabled'   value='<bean:write name="element" property="tabid" filter="false" />' />
	   		</td>
	   		<%} %>
	   		</logic:notEqual>
   	  		 <%} %>
   	  		
   	  		
   	  </logic:equal>
   	  <logic:notEqual name="actuarialCollectReportForm"  property="rootUnit" value="1">
   	  	<logic:notEqual  name="actuarialCollectReportForm"  property="isCollectUnit" value="1">
			<logic:notEqual  name="element"  property="status_up" value="1">
			 <logic:equal  name="element"  property="status" value="1">
			 <%if(!selfUnitcode.equals(unitCode)){ %>
			<td align="center" class="RecordRow"   nowrap>
	   			<input type='checkbox' name='id'   value='<bean:write name="element" property="tabid" filter="false" />' />
	   		</td>
	   		<%} %>
	   		</logic:equal>
	   		 <logic:notEqual  name="element"  property="status" value="1">
			<%if(!selfUnitcode.equals(unitCode)){ %>
			<td align="center" class="RecordRow"   nowrap>
	   			<input type='checkbox' name='id' disabled='disabled'   value='<bean:write name="element" property="tabid" filter="false" />' />
	   		</td>
	   		<%} %>
	   		</logic:notEqual>
	   		</logic:notEqual>
	   		<logic:equal  name="element"  property="status_up" value="1">	
	   		<%if(!selfUnitcode.equals(unitCode)){ %>
	   		<td align="center" class="RecordRow"   nowrap>
	   			<input type='checkbox' name='id' disabled='disabled'   value='<bean:write name="element" property="tabid" filter="false" />' />
	   		</td>
	   		<%} %>
	   		</logic:equal>
		 </logic:notEqual>	
   	  </logic:notEqual>
       <% } %> 
	   	 	<td align="left" class="RecordRow"   nowrap>
	   			&nbsp; <bean:write name="element" property="name" filter="true"/>
	   		</td>
	   		<td align="center" class="RecordRow" nowrap>
	   			 <bean:write name="element" property="status_desc" filter="false"/>
	   		</td>
	   	
	   		<td align="center" class="RecordRow" nowrap>
	   		
	   		 
	   		<logic:equal name="element" property="tabid"  value="U03">
	   			<% if(isCollectUnit.equals("1")&&selfUnitcode.equals(unitCode)) {%>
	   			<a href='/report/actuarial_report/edit_report/editreportlist.do?b_initReport3=query&from_model=collect&opt=1&id=<bean:write name="element" property="cycle_id" filter="true"/>&unitcode=<bean:write name="element" property="unitCode" filter="true"/>' >	<img src='/images/view.gif' border='0'  style="cursor: hand" /> </a>
	   			<% }else{ %>
	   			<a href='/report/actuarial_report/edit_report/editreportlist.do?b_initReport3=query&from_model=collect&opt=0&id=<bean:write name="element" property="cycle_id" filter="true"/>&unitcode=<bean:write name="element" property="unitCode" filter="true"/>' >	<img src='/images/view.gif' border='0'  style="cursor: hand" /> </a>
	   			<% } %>
	   		</logic:equal>
	   		<logic:equal name="element" property="tabid"  value="U05">
	   		<a href='/report/actuarial_report/edit_report/editreportlist.do?b_queryReport5=query&opt=0&id=<bean:write name="element" property="cycle_id" filter="true"/>&unitcode=<bean:write name="element" property="unitCode" filter="true"/>' >	<img src='/images/view.gif' border='0'  style="cursor: hand" /> </a>
	   		</logic:equal>
	   		<logic:equal name="element" property="tabid"  value="U04">
	   		<a href='/report/actuarial_report/edit_report/editreportlist.do?b_initReport4=query&opt=0&id=<bean:write name="element" property="cycle_id" filter="true"/>&unitcode=<bean:write name="element" property="unitCode" filter="true"/>' >	<img src='/images/view.gif' border='0'  style="cursor: hand" /> </a>
	   		</logic:equal>
	   		<logic:equal name="element" property="tabid"  value="U01">
	   		<% if(isCollectUnit.equals("1")&&selfUnitcode.equals(unitCode)) {%>
	   		<a href='/report/actuarial_report/edit_report/editreportU01.do?b_query=link&from_model=collect&opt=1&unitcode=<bean:write name="element" property="unitCode" filter="true"/>&id=<bean:write name="element" property="cycle_id" filter="true"/>&flag=<bean:write name="element" property="status" filter="true"/>' >	<img src='/images/view.gif' border='0'  style="cursor: hand" /> </a>	
	   		<% }else{ %>
	   		<a href='/report/actuarial_report/edit_report/editreportU01.do?b_query=link&from_model=collect&opt=0&unitcode=<bean:write name="element" property="unitCode" filter="true"/>&id=<bean:write name="element" property="cycle_id" filter="true"/>&flag=<bean:write name="element" property="status" filter="true"/>' >	<img src='/images/view.gif' border='0'  style="cursor: hand" /> </a>	
	   		<% } %>
	   		</logic:equal>
	   		<%  if(tabid.indexOf("U02")!=-1){
	   				String opt="0";
	   				if(!selfUnitcode.equals(unitCode)&&status.equals("1"))
	   					opt="1";
	   					if(rootUnit.equals("1")&&status.equals("1"))
	   						opt="1";
	   		 %>
	   	    <a href='/report/actuarial_report/edit_report/searcheportU02List.do?b_query=link&opt=<%=opt%>&from_model=collect&unitcode=<bean:write name="element" property="unitCode" filter="true"/>&id=<bean:write name="element" property="cycle_id" filter="true"/>&flag=<bean:write name="element" property="status" filter="true"/>&report_id=<bean:write name="element" property="tabid" filter="true"/>' >	<img src='/images/view.gif' border='0'  style="cursor: hand" /> </a>	
	   		<% } %> 
	   		
	   		
	   		
	   		</td>
	   		
	   	  </tr>
	   	    </logic:notEqual>
	   		   </logic:notEqual>
	   		   </logic:notEqual>
	   		   </logic:notEqual>
	   		     	 <logic:equal name="actuarialCollectReportForm"  property="kmethod" value="0">
	   		 
	   		   
	   		 
	    <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow" onclick='tr_onclick(this,"#F3F5FC");'>
          <%}
          else
          {%>
          <tr class="trDeep" onclick='tr_onclick(this,"#F3F5FC");'>
          <%
          }
          i++;    
          
           LazyDynaBean a_bean=(LazyDynaBean)pageContext.getAttribute("element");
           String tabid=(String)a_bean.get("tabid");
           String status=(String)a_bean.get("status");
          // System.out.println(status);
                
          %> 
          <% if(cycleStatus.equals("04")){ %>
      <logic:equal  name="actuarialCollectReportForm"  property="rootUnit" value="1">
   	  		 <%if(selfUnitcode.equals(unitCode)){ %>
   	  		  <logic:equal  name="element"  property="status" value="1">
			<td align="center" class="RecordRow"   nowrap>
	   			<input type='checkbox' name='id'   value='<bean:write name="element" property="tabid" filter="false" />' />
	   		</td>
	   		</logic:equal>
	   		 <logic:notEqual  name="element"  property="status" value="1">
			<td align="center" class="RecordRow"   nowrap>
	   			<input type='checkbox' name='id' disabled='disabled'   value='<bean:write name="element" property="tabid" filter="false" />' />
	   		</td>
	   		</logic:notEqual>
   	  		 <%}else{ %>
   	  	
			 <logic:equal  name="element"  property="status" value="1">
		
			<td align="center" class="RecordRow"   nowrap>
	   			<input type='checkbox' name='id'   value='<bean:write name="element" property="tabid" filter="false" />' />
	   		</td>
	   	
	   		</logic:equal>
	   		 <logic:notEqual  name="element"  property="status" value="1">
			<%if(!selfUnitcode.equals(unitCode)){ %>
			<td align="center" class="RecordRow"   nowrap>
	   			<input type='checkbox' name='id' disabled='disabled'   value='<bean:write name="element" property="tabid" filter="false" />' />
	   		</td>
	   		<%} %>
	   		</logic:notEqual>
   	  		 <%} %>
   	  		
   	  		
   	  </logic:equal>
   	  <logic:notEqual name="actuarialCollectReportForm"  property="rootUnit" value="1">
   	  	<logic:notEqual  name="actuarialCollectReportForm"  property="isCollectUnit" value="1">
			<logic:notEqual  name="element"  property="status_up" value="1">
			 <logic:equal  name="element"  property="status" value="1">
			 <%if(!selfUnitcode.equals(unitCode)){ %>
			<td align="center" class="RecordRow"   nowrap>
	   			<input type='checkbox' name='id'   value='<bean:write name="element" property="tabid" filter="false" />' />
	   		</td>
	   		<%} %>
	   		</logic:equal>
	   		 <logic:notEqual  name="element"  property="status" value="1">
			<%if(!selfUnitcode.equals(unitCode)){ %>
			<td align="center" class="RecordRow"   nowrap>
	   			<input type='checkbox' name='id' disabled='disabled'   value='<bean:write name="element" property="tabid" filter="false" />' />
	   		</td>
	   		<%} %>
	   		</logic:notEqual>
	   		</logic:notEqual>
	   		<logic:equal  name="element"  property="status_up" value="1">	
	   		<%if(!selfUnitcode.equals(unitCode)){ %>
	   		<td align="center" class="RecordRow"   nowrap>
	   			<input type='checkbox' name='id' disabled='disabled'   value='<bean:write name="element" property="tabid" filter="false" />' />
	   		</td>
	   		<%} %>
	   		</logic:equal>
		 </logic:notEqual>	
   	  </logic:notEqual>
       <% } %> 
	   	 	<td align="left" class="RecordRow"   nowrap>
	   			&nbsp; <bean:write name="element" property="name" filter="true"/>
	   		</td>
	   		<td align="center" class="RecordRow" nowrap>
	   			 <bean:write name="element" property="status_desc" filter="false"/>
	   		</td>
	   	
	   		<td align="center" class="RecordRow" nowrap>
	   		
	   		 
	   		<logic:equal name="element" property="tabid"  value="U03">
	   			<% if(isCollectUnit.equals("1")&&selfUnitcode.equals(unitCode)) {%>
	   			<a href='/report/actuarial_report/edit_report/editreportlist.do?b_initReport3=query&from_model=collect&opt=1&id=<bean:write name="element" property="cycle_id" filter="true"/>&unitcode=<bean:write name="element" property="unitCode" filter="true"/>' >	<img src='/images/view.gif' border='0'  style="cursor: hand" /> </a>
	   			<% }else{ %>
	   			<a href='/report/actuarial_report/edit_report/editreportlist.do?b_initReport3=query&from_model=collect&opt=0&id=<bean:write name="element" property="cycle_id" filter="true"/>&unitcode=<bean:write name="element" property="unitCode" filter="true"/>' >	<img src='/images/view.gif' border='0'  style="cursor: hand" /> </a>
	   			<% } %>
	   		</logic:equal>
	   		<logic:equal name="element" property="tabid"  value="U05">
	   		<a href='/report/actuarial_report/edit_report/editreportlist.do?b_queryReport5=query&opt=0&id=<bean:write name="element" property="cycle_id" filter="true"/>&unitcode=<bean:write name="element" property="unitCode" filter="true"/>' >	<img src='/images/view.gif' border='0'  style="cursor: hand" /> </a>
	   		</logic:equal>
	   		<logic:equal name="element" property="tabid"  value="U04">
	   		<a href='/report/actuarial_report/edit_report/editreportlist.do?b_initReport4=query&opt=0&id=<bean:write name="element" property="cycle_id" filter="true"/>&unitcode=<bean:write name="element" property="unitCode" filter="true"/>' >	<img src='/images/view.gif' border='0'  style="cursor: hand" /> </a>
	   		</logic:equal>
	   		<logic:equal name="element" property="tabid"  value="U01">
	   		<% if(isCollectUnit.equals("1")&&selfUnitcode.equals(unitCode)) {%>
	   		<a href='/report/actuarial_report/edit_report/editreportU01.do?b_query=link&from_model=collect&opt=1&unitcode=<bean:write name="element" property="unitCode" filter="true"/>&id=<bean:write name="element" property="cycle_id" filter="true"/>&flag=<bean:write name="element" property="status" filter="true"/>' >	<img src='/images/view.gif' border='0'  style="cursor: hand" /> </a>	
	   		<% }else{ %>
	   		<a href='/report/actuarial_report/edit_report/editreportU01.do?b_query=link&from_model=collect&opt=0&unitcode=<bean:write name="element" property="unitCode" filter="true"/>&id=<bean:write name="element" property="cycle_id" filter="true"/>&flag=<bean:write name="element" property="status" filter="true"/>' >	<img src='/images/view.gif' border='0'  style="cursor: hand" /> </a>	
	   		<% } %>
	   		</logic:equal>
	   		<%  if(tabid.indexOf("U02")!=-1){
	   				String opt="0";
	   				if(!selfUnitcode.equals(unitCode)&&status.equals("1"))
	   					opt="1";
	   					if(rootUnit.equals("1")&&status.equals("1"))
	   						opt="1";
	   		 %>
	   	    <a href='/report/actuarial_report/edit_report/searcheportU02List.do?b_query=link&opt=<%=opt%>&from_model=collect&unitcode=<bean:write name="element" property="unitCode" filter="true"/>&id=<bean:write name="element" property="cycle_id" filter="true"/>&flag=<bean:write name="element" property="status" filter="true"/>&report_id=<bean:write name="element" property="tabid" filter="true"/>' >	<img src='/images/view.gif' border='0'  style="cursor: hand" /> </a>	
	   		<% } %> 
	   		
	   		
	   		
	   		</td>
	   		
	   	  </tr>
	   	 
	   		   </logic:equal>
	   </logic:iterate>
	   
</table>
</td></tr>
<logic:equal  name="actuarialCollectReportForm"  property="isCollectUnit" value="1">
  <logic:equal  name="actuarialCollectReportForm"  property="isAllSub" value="0">
	 <% if(selfUnitcode.equals(unitCode)&&cycleStatus.equals("04")){ %>
	 <tr><td> <table width="100%"  align="center"  border="0">
	${actuarialCollectReportForm.htmlbody}
	</table></td></tr>
 	<% }%>
  </logic:equal>
 </logic:equal>
 <tr>
 <td align='center' width="80%">
 <logic:equal  name="actuarialCollectReportForm"  property="isCollectUnit" value="1">
  <logic:equal  name="actuarialCollectReportForm"  property="isAllSub" value="0">
	
	 <%if(selfUnitcode.equals(unitCode)&&cycleStatus.equals("04")){ %>
	 <input type='button' id="collect"  value='汇总数据'  onclick="collectData()" class="mybutton">
	 <input type='button' id="sub" value='统一提交'  onclick='unionsub();' class="mybutton">
	<% }%>
  </logic:equal>
 </logic:equal>
 <% if(cycleStatus.equals("04")){ %>
   <logic:equal  name="actuarialCollectReportForm"  property="rootUnit" value="1">
   	  <hrms:priv func_id="29060303">
			  <input type='button' id="reject" value='驳回'  onclick='rejectselect();' class="mybutton">
	  </hrms:priv>
   	  </logic:equal>
   	  <logic:notEqual name="actuarialCollectReportForm"  property="rootUnit" value="1">
   	  	<logic:notEqual  name="actuarialCollectReportForm"  property="isCollectUnit" value="1">
			 <%if(!selfUnitcode.equals(unitCode)){ %>
			<hrms:priv func_id="29060303"> 
			  <input type='button' id="reject" value='驳回'  onclick='rejectselect();' class="mybutton">
			 </hrms:priv>
			 <%} %>
		 </logic:notEqual>	
   	  </logic:notEqual>
   	  <%} %>
   	  </td>
   	  </tr>
  <% if(!selfUnitcode.equals(unitCode)){ %>
    <tr><td> <table width="100%"  align="center"  border="0">
	${actuarialCollectReportForm.htmlbody2}
	</table></td></tr>
	<%} %>
 


<tr><td align='right' ><Br>填报单位:${actuarialCollectReportForm.unitName}</td></tr>
<tr><td>
<table width="100%" border="0" cellspacing="0"    align="left" cellpadding="0" class="ListTable">
							${actuarialCollectReportForm.tableHtml}
						</table>
</td></tr>
</table>


<% }else{ %>
<div align='center' >	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;无有效数据可显示！ </div>
<% } %>

</td></tr>

</table>



<script language='javascript' >

function unionsub()
{
	<logic:equal  name="actuarialCollectReportForm"  property="isAllEdit" value="0">
		alert("当前单位没有汇总数据，不能统一提交!");
		return;
	 </logic:equal>
	 <logic:equal  name="actuarialCollectReportForm"  property="u02_3flag" value="1">
		if(confirm("表2-3 内退人员处于未填状态，是否继续？")){
		}else{
		return;
		}
		
	 </logic:equal>
	 		var hashvo=new ParameterSet();
			
		var hashvo=new ParameterSet();
		var isfillpara = trim('${actuarialCollectReportForm.isfillpara}');
		if(isfillpara!=null&&isfillpara!=""){
  		var ispara=isfillpara.split(",");
  		for(var i=0;i<ispara.length;i++){
  		if(trim(document.getElementsByName(ispara[i])[0].value)==""){
  		alert(ispara[i]+"不能为空!");
  		return;
  		}
  		}
  		}
  		var isfillpara2 = trim('${actuarialCollectReportForm.isfillpara2}');
  			if(isfillpara2!=null&&isfillpara2!=""){
  			var ispara2=isfillpara2.split(",");
  		for(var i=0;i<ispara2.length;i++){
  		if(trim(document.getElementsByName(ispara2[i])[0].value)==""){
  		alert(ispara2[i]+"不能为空!");
  		return;
  		}
  		if(!validate(document.getElementsByName(ispara2[i])[0],ispara2[i])){
		return;
		}
  		}
  		}
		var paracopy = trim('${actuarialCollectReportForm.paracopy}');
		if(paracopy!=null&&paracopy!=""){
  		var para=paracopy.split(",");
  		for(var i=0;i<para.length;i++){
  		
  		hashvo.setValue(para[i],trim(document.getElementsByName(para[i])[0].value));
  		}
  		}
  		var paracopy2 = trim('${actuarialCollectReportForm.paracopy2}');
  		if(paracopy2!=null&&paracopy2!=""){
  		var para2=paracopy2.split(",");
  		for(var i=0;i<para2.length;i++){
  		
		hashvo.setValue(para2[i],trim(document.getElementsByName(para2[i])[0].value));
  		}
  		}
		document.actuarialCollectReportForm.sub.disabled="true";
	 var waitInfo=eval("wait");	   
        waitInfo.style.display="block";
        	hashvo.setValue("paracopy",paracopy);
		hashvo.setValue("paracopy2",paracopy2);
		hashvo.setValue("id",document.actuarialCollectReportForm.cycle_id.value);
		hashvo.setValue("unitcode","${actuarialCollectReportForm.unitCode}");
		var request=new Request({method:'post',asynchronous:false,onSuccess:success,onFailure:failure,functionId:'03060000227'},hashvo);
}

function rejectselect()
	{
		var value="";
		var value2="";
		var num="0";
		for(var i=0;i<document.actuarialCollectReportForm.elements.length;i++)
		{
			if(document.actuarialCollectReportForm.elements[i].type=='checkbox'&&document.actuarialCollectReportForm.elements[i].name !="selbox")
			{
			if(document.actuarialCollectReportForm.elements[i].disabled){
			}else{
			num="1";
			}
				if(document.actuarialCollectReportForm.elements[i].checked==false)
				{
					value+=document.actuarialCollectReportForm.elements[i].value+",";
				}
				else
					value2+=document.actuarialCollectReportForm.elements[i].value+",";
			}
		}
		
		if(value2==""){
		if(num=="1"){
		alert("请选择表");
		}else{
		
		}
		return;
		}
		
		var arguments=new Array();
		arguments[0]="";
		arguments[1]="驳回原因";  
	    var strurl="/gz/gz_accounting/rejectCause.jsp";
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
		var ss=window.showModalDialog(iframe_url,arguments,"dialogWidth=460px;dialogHeight=350px;resizable=yes;scroll=no;status=no;");  
	    if(ss)
		{
			var hashvo=new ParameterSet();	
			hashvo.setValue("cycle_id",'${actuarialCollectReportForm.cycle_id}');
			hashvo.setValue("unitcode",'${actuarialCollectReportForm.unitCode}');
			hashvo.setValue("report_id",value2);
			hashvo.setValue("cause",getEncodeStr(ss[0]));
			var request=new Request({method:'post',asynchronous:false,onSuccess:success,functionId:'03060000218'},hashvo);
		}
	}
function collectData()
{
	<logic:equal  name="actuarialCollectReportForm"  property="isAllSub_child" value="0">
		alert("直属单位没有上报数据，汇总数据失败!");
		return;
	 </logic:equal>
	document.actuarialCollectReportForm.collect.disabled="true";
//	 var waitInfo=eval("wait");	   
//     waitInfo.style.display="block";
	 var hashvo=new ParameterSet();
	 hashvo.setValue("cycle_id",document.actuarialCollectReportForm.cycle_id.value);
	 hashvo.setValue("unitcode",'${actuarialCollectReportForm.unitCode}'); 
	 hashvo.setValue("opt","1");
	 var request=new Request({method:'post',asynchronous:true,onSuccess:success,onFailure:failure,functionId:'03060000302'},hashvo); 
}

function success(outparamters)
{
	var waitInfo=eval("wait");	
	waitInfo.style.display="none";
	var url=document.location.href;
	document.location=url;
}
function failure(outparamters)
{
	var waitInfo=eval("wait");	
	waitInfo.style.display="none";
	document.actuarialCollectReportForm.sub.disabled="";
	//var url=document.location.href;
	//document.location=url;
}
function failure(outparamters)
{
	var waitInfo=eval("wait");	
	waitInfo.style.display="none";
	var url=document.location.href;
	document.location=url;
}
 function returnInfo(outparamters)
	 {
	    	window.close();
	 }
	 var start1;//用于判断-号出现的位置
  var i;//用于判断字符串中'-'号的出现位置,定义的循环变量
  var chkyear;//用于截取年
  var chkyearinteger;
  var chkmonths;//用于截取月
  var chkmonthsinteger;
  var chkdays;//用于截取日
  var chkdaysinteger;
  var chk1;//用于按位判断输入的年,月,日是否为整数
  var chk2;
  var mon=new Array(12);/*声明一个日期天数的数组*/
  mon[0]=31;
  mon[1]=28;
  mon[2]=31;
  mon[3]=30;
  mon[4]=31;
  mon[5]=30;
  mon[6]=31;
  mon[7]=31;
  mon[8]=30;
  mon[9]=31;
  mon[10]=30;
  mon[11]=31;
  
  function isDigit(s)   
  {   
		var patrn=/^[0-9]{1,20}$/;   
		if (!patrn.exec(s)) return false  
			return true  
  } 
  function validate(aa,bb)
  {
	   if(aa.value == "")
	   {
	    alert(bb+":"+GZ_ACCOUNTING_IFNO4+"！");
	    aa.focus();
	    return false;
	   }
	   
		var temps;
		var flag=false;
		if(aa.value.indexOf(".")!=-1)
		{
			temps=aa.value.split(".");
			flag=true;
		}
		if(aa.value.indexOf("-")!=-1)
		{
			temps=aa.value.split("-");
			flag=true;
		}
		if(flag==false)
		{
			 alert(bb+":"+REPORT_INFO14+"！");
			 aa.focus();
			 return false;
		}
		
		if(temps.length!=3)
		{
			alert(bb+":"+REPORT_INFO14+"！");
			aa.focus();
			return false;
		}
		
		for(var i=0;i<temps.length;i++)
		{
			if(!isDigit(temps[i]))
			{
				alert(bb+":"+REPORT_INFO14+"！");
				aa.focus();
				return false;
			}
		}
		if(!(temps[0]>=1900&&temps[0]<=2100))
		{
		     alert(bb+":"+REPORT_INFO6+"!");
		     aa.focus();
		     return false;
	     	}
		chkyearinteger=parseInt(temps[0],10);
		 //根据年设2月份的日期
		    if(chkyearinteger%100==0||chkyearinteger%4==0)
		    {
		    mon[1]=29;
		    }
		    else
		    {
		    mon[1]=28;
		    }
		    //判断月是否符合条件
		    chkmonths=temps[1];
		    chkmonthsinteger=parseInt(temps[1],10);
		    if(!(chkmonthsinteger>=1&&chkmonthsinteger<=12))
		    {
		     alert(bb+":"+REPORT_INFO7+"!");
		     aa.focus();
		     return false;
		    }
	    //判断日期是否符合条件
		    chkdays=temps[2];
		    chkdaysinteger=parseInt(chkdays,10);
		   
		    switch(chkmonthsinteger)
		    {
		     case 1:if(!(chkdaysinteger>0&&chkdaysinteger<=mon[0]))
		        {
		         alert(bb+":1"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		     case 2:if(!(chkdaysinteger>0&&chkdaysinteger<=mon[1]))
		        {
		         alert(bb+":2"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		    
		     case 3:if(!(chkdaysinteger>0&&chkdaysinteger<=mon[2]))
		        {
		         alert(bb+":3"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		     case 4:if(!(chkdaysinteger>0&&chkdaysinteger<=mon[3]))
		        {
		         alert(bb+":4"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		     case 5:if(!(chkdaysinteger>0&&chkdaysinteger<=mon[4]))
		        {
		         alert(bb+":5"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		     case 6:if(!(chkdaysinteger>0&&chkdaysinteger<=mon[5]))
		        {
		         alert(bb+":6"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		    
		     case 7:if(!(chkdaysinteger>0&&chkdaysinteger<=mon[6]))
		        {
		         alert(bb+":7"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		     case 8:if(!(chkdaysinteger>0&&chkdaysinteger<=mon[7]))
		        {
		         alert(bb+":8"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		     
		     case 9:if(!(chkdaysinteger>0&&chkdaysinteger<=mon[8]))
		        {
		         alert(bb+":9"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		     case 10:if(!(chkdaysinteger>0&&chkdaysinteger<=mon[9]))
		        {
		         alert(bb+":10"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		     case 11:if(!(chkdaysinteger>0&&chkdaysinteger<=mon[10]))
		        {
		         alert(bb+":11"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		     case 12:if(!(chkdaysinteger>0&&chkdaysinteger<=mon[11]))
		        {
		         alert(bb+":12"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		     }//日期判断结束


  		  return true;
  }
function MusterInitData()
{
	var waitInfo=eval("wait");	
	waitInfo.style.display="none";
}

</script>

</html:form>
</body>
</html>
<div id='wait' style='position: absolute; top: 200; left: 250;'>
	<table border="1" width="400" cellspacing="0" cellpadding="4"
		style="border-collapse: collapse" bgcolor="#F7FAFF" height="87"
		align="center">
		<tr>
			<td bgcolor="#057AFC" style="font-size: 12px; color: #ffffff"
				height=24>
				正在处理数据请稍候....
			</td>
		</tr>
		<tr>
			<td style="font-size: 12px; line-height: 200%" align=center>
				<marquee style="border: 1px solid #000000" direction="right"
					width="300" scrollamount="5" scrolldelay="10" bgcolor="#ECF2FF">
					<table cellspacing="1" cellpadding="0">
						<tr height=8>
							<td bgcolor=#3399FF width=8></td>
							<td></td>
							<td bgcolor=#3399FF width=8></td>
							<td></td>
							<td bgcolor=#3399FF width=8></td>
							<td></td>
							<td bgcolor=#3399FF width=8></td>
							<td></td>
						</tr>
					</table>
				</marquee>
			</td>
		</tr>
	</table>
</div>
<script language="javascript">
 MusterInitData();
</script>