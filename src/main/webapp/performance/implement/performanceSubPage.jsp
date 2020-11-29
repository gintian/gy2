<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.performance.implement.ImplementForm,
				 org.apache.commons.beanutils.LazyDynaBean,
				 com.hrms.hjsj.sys.DataDictionary,
				 com.hrms.hjsj.sys.FieldItem,
				 com.hrms.struts.taglib.CommonData,
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant" %>
				 
<%
	ImplementForm implementForm=(ImplementForm)session.getAttribute("implementForm");	
	ArrayList pointPowerHeadList=implementForm.getPointPowerHeadList();
	String method = implementForm.getMethod();
	String object_type = implementForm.getObject_type();
	String gradeByBodySeq = implementForm.getGradeByBodySeq();
	String planStatus=implementForm.getPlanStatus();
	String objectid=request.getParameter("objectid");
	String template_id=request.getParameter("template_id");
	ArrayList pointItemList=implementForm.getPointItemList();
	String opt=request.getParameter("opt")!=null?request.getParameter("opt"):"1";
	int i=0;
	ArrayList pointPowerList = (ArrayList)implementForm.getPointPowerList();
	ArrayList itemprivList = (ArrayList)implementForm.getItemprivList();
 %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GB18030">
<script language="JavaScript" src="implement.js"></script>
<SCRIPT LANGUAGE=javascript src="/js/constant.js"></SCRIPT>  
<title>Insert title here</title>
</head>
<script language='javascript' >

//删除考核主体
function delmainBody(plan_id,templateid,select_objectid)
{
	var objs=eval("document.implementForm.mainbodyIDs");
	var mainbodyIDs="";
	if(objs)
	{
	
		if(objs.length)
		{
			for(var i=0;i<objs.length;i++)
			{
				if(objs[i].checked==true)
					mainbodyIDs+="`"+objs[i].value;	
			}
		}
		else
		{
			if(objs.checked==true)
					mainbodyIDs+="`"+objs.value;	
		}
	}
	if(mainbodyIDs=="")
	{
			alert(P_I_INFO4+"!");
			return;
	}
	
    var hashvo=new ParameterSet();
	hashvo.setValue("mainbodyids",mainbodyIDs.substring(1));
	hashvo.setValue("object_id",select_objectid);
	hashvo.setValue("plan_id",plan_id);
	hashvo.setValue("templateid",templateid);
	<%if(method.equals("2")){%>	//判断所选考核主体已参与目标卡的审批流程,如删除,则考核对象的目标卡状态将被初始化！您确定要删除所选考核主体吗？
		hashvo.setValue("opt","10");
		var request=new Request({method:'post',asynchronous:false,onSuccess:testDel,functionId:'9023000003'},hashvo);
	<%}else{%>
		if(confirm(P_I_INF14))
		{
			hashvo.setValue("opt","5");
			var request=new Request({method:'post',asynchronous:false,onSuccess:delOk,functionId:'9023000003'},hashvo);
		}
	<%}%>
}

function testDel(outparamters)
{
	var isHave = outparamters.getValue("isHave");
	var mainbodyIDs = outparamters.getValue("mainbodyids");
	var select_objectid = outparamters.getValue("object_id");
	var plan_id = outparamters.getValue("plan_id");
	var templateid = outparamters.getValue("templateid");
	var info=P_I_INF14;
	if(isHave=='1')
		info=P_I_INF18;
	if(confirm(info))
	{
		var hashvo=new ParameterSet();
		hashvo.setValue("mainbodyids",mainbodyIDs);
		hashvo.setValue("object_id",select_objectid);
		hashvo.setValue("plan_id",plan_id);
		hashvo.setValue("templateid",templateid);
		hashvo.setValue("opt","5");
		var request=new Request({method:'post',asynchronous:false,onSuccess:delOk,functionId:'9023000003'},hashvo);
	}	
}
	function numCheck(){
		if ( !(((window.event.keyCode >= 48) && (window.event.keyCode <= 57)) 
		|| (window.event.keyCode == 13) 
		|| (window.event.keyCode == 45)))
		{
			window.event.keyCode = 0 ;
		}
	}
	function checkNuNS(obj){
 	if(! /^[1-9]\d*$/.test(trim(obj.value))){
 		obj.value='';
 		return;
 	}
   }
function isNums(i_value){
    re=new RegExp("[^A-Za-z0-9]");
    var s;
    if(s=i_value.match(re)){
        return false;
     }
    return true;
}
function batch_selectXXX(obj,name)
  {
  	if(obj.checked)
  	  setCheckState(1,name);
  	else
  	  setCheckState(2,name);
  }
  
  function setCheckState(flag,name)
  {
      var chklist,objname,i,typeanme;
      chklist=document.getElementsByTagName('INPUT');
      if(!chklist)
        return;
	  for(i=0;i<chklist.length;i++)
	  {
	     typeanme=chklist[i].type.toLowerCase();
	     if(typeanme!="checkbox")
	        continue;	  
	     if(chklist[i].disabled)
	     	continue;
	     objname=chklist[i].name;
	     if(objname==name)
			{
	     if(flag=="1")
  	       chklist[i].checked=true;
  	     else  
  	       chklist[i].checked=false;
  	       }
	  }   
  }
</script>
<style>
.Input_self{  
	BACKGROUND-COLOR:transparent;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: medium none; 
	BORDER-RIGHT: medium none; 
	BORDER-TOP: medium none;                                                                  
  font-size:   12px;                                              
  letter-spacing:   1px;                      
  text-align:   right;                        
 /* height:   90%; haosl 20170314 update 顺序评分，输入的数值都居上了，应该是在线上的位置显示。 */                              
  width:   80%; 
  cursor:   hand;                                     
  }
.fixedHeaderTr1{
    position: static !important;
}
/*指标授权时锁定考核主体 郭峰*/
.head_title{
background-position : center left;
	font-size: 12px;  
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 0pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 0pt solid;
	height:22px;
	vertical-align:middle;
	font-weight: bold;
	background-color:#f4f7f7;	
	valign:middle;
	z-index: 20;
	position:relative;
	top:expression(this.offsetParent.scrollTop);
    left:expression(this.offsetParent.scrollLeft);
}
.head_data{
font-size: 12px;  
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 0pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 0pt solid;
	height:22px;
    padding-top=5;   
	font-weight: bold;
	background-color:#f4f7f7;	
	valign:cennter;
	z-index: 15;
	position:relative;
	top:expression(this.offsetParent.scrollTop);
}
.cell_title{
font-size: 12px;  
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 0pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 0pt solid;
	height:22px;
		font-weight: bold;
	background-color:white;	/*需要加背景色 否则横向拖动滚动条时会被拖拽内容覆盖*/
	valign:middle;
	z-index: 15;
	position:relative;
    left:expression(this.offsetParent.scrollLeft);
}
.cell_data {
	background-position : center left;
	font-size: 12px;  
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 0pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 0pt solid;
	height:22px;
	font-weight: bold;
	valign:middle;
}
</style>
<body>
<html:form action="/performance/implement/performanceImplement" onsubmit="return false;">
<table border="0" height="200px" width="100%" cellspacing="0" cellpadding="0" class="ListTable">
<% if(opt.endsWith("1")){ %>

	<table width="100%" border="0" cellspacing="0"  align="left" cellpadding="0" style="border:0px; margin:5px 0px 0px 0px; BORDER-COLLAPSE: collapse">
			<thead>
       			 <tr class="fixedHeaderTr1">
       			 <td align="center" width='40' style="border-right:0px;" class="TableRow td_no_t_r" nowrap >
       			 <input type="checkbox" name="selbox" onclick="batch_selectXXX(this,'mainbodyIDs');" title='<bean:message key="label.query.selectall"/>'>
       			 </td>
       			 <%
	         		FieldItem fielditem = DataDictionary.getFieldItem("E0122");
	         	 %>	         			 						
       			 <td align="center" width='30'  style=""   class="TableRow td_no_t_r" nowrap ><bean:message key="label.serialnumber"/></td>
       			 <td align="center" width='100' style=""   class="TableRow td_no_t_r" nowrap ><bean:message key="b0110.label"/></td>
       			 <td align="center" width='100' style=""   class="TableRow td_no_t_r" nowrap ><%=fielditem.getItemdesc()%></td>
       			 <td align="center" width='100' style=""   class="TableRow td_no_t_r" nowrap ><bean:message key="e01a1.label"/></td>
       			 <td align="center" width='100' style=""   class="TableRow td_no_t_r" nowrap ><bean:message key="hire.employActualize.name"/></td>
       			 <td align="center" width='' style=""  class="TableRow td_no_t_r" nowrap ><bean:message key="reporttypelist.sort"/></td>
       			        			       			 
       			 <logic:equal name="implementForm" property="busitype"  value="0">       			 	
       			 	<%if(gradeByBodySeq.equalsIgnoreCase("True")){%>
       			 		<td align="center" width='60' style=""  class="TableRow td_no_t_r" nowrap ><bean:message key="label.order.sp_seq"/></td>
       			 		<td align="center" width='60' style=""  class="TableRow td_no_t_r" nowrap ><bean:message key="label.order.seq"/></td>
       			 	<%}else{%>
       			 	<logic:notEqual value="1" name="implementForm" property="method">
       			 		<td align="center" width='50' style=""  class="TableRow td_no_t_r" nowrap ><bean:message key="label.order"/></td>
       			 	</logic:notEqual>
       			 	<%}%>
       			 </logic:equal> 
       			 <%if(method.equals("2")){%>	
       			 <td align="center" width='100' style=""   class="TableRow td_no_t_r" nowrap ><bean:message key="performance.implement.isgradeOrNo"/></td>
       			 <%}%>
       			 <td align="center" width='100'  style=""  class="TableRow td_no_t_r" nowrap ><input type="checkbox" name="sx" onclick="batch_selectXXX(this,'must');selectall(this);" title='<bean:message key="label.query.selectall"/>'><bean:message key="performance.implement.mustscore"/></td>
       			 </tr>
       		</thead>
       		<% i=0; %>
			<logic:iterate id="element" name="implementForm" property="perMainBodyList" >
			<% i++;
   	  	   if(i%2==1)
   	  	   	out.println("<tr class='trShallow'>");
   	  	   else
   	  	   	out.println("<tr class='trDeep'>");
   	  	 	%>
				<td align="center" style="" class="RecordRow" nowrap>
			  		<input type='checkbox' name='mainbodyIDs'  value='<bean:write name="element" property="mainbody_id" filter="true"/>:<bean:write name="element" property="body_id" filter="true"/>'   />
	        	</td> 
	        	<td align="center" style="" class="RecordRow" nowrap>
	        		<%=i %>
	        	</td> 
	        	<td align="left" style="" class="RecordRow" nowrap>
				 &nbsp;<bean:write name="element" property="b0110" filter="true"/>
				</td>
				<td align="left" style="" class="RecordRow" nowrap>
				 &nbsp;<bean:write name="element" property="e0122" filter="true"/>
				</td>
				<td align="left"  class="RecordRow" nowrap>
				 &nbsp;<bean:write name="element" property="e01a1" filter="true"/>
				</td>
				<td align="left" class="RecordRow" nowrap>
				 &nbsp;<bean:write name="element" property="a0101" filter="true"/>
				</td>
				<td align="left" class="RecordRow" nowrap>
				 &nbsp;<bean:write name="element" property="bodyTypeName" filter="true"/>
				</td>
				
				<logic:equal name="implementForm" property="busitype"  value="0">
				
					<%if(gradeByBodySeq.equalsIgnoreCase("True")){%>
					<logic:notEqual value="1" name="implementForm" property="method">
       			 		<td align="right" class="RecordRow" nowrap>
							<%
				     		LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
				     		String body_id=(String)abean.get("body_id");
				     		String object_id=(String)abean.get("object_id");
				     		String mainbody_id=(String)abean.get("mainbody_id");
				     		pageContext.setAttribute("object_id",object_id);
				     		pageContext.setAttribute("mainbody_id",mainbody_id);
				     		
				     	    %>		
				     	    <%  if(planStatus.equals("3")||planStatus.equals("5")){ 	
				     	    	if(body_id!=null && body_id.trim().length()>0 && ((object_type.equalsIgnoreCase("2") && body_id.equalsIgnoreCase("5")) || (!object_type.equalsIgnoreCase("2") && body_id.equalsIgnoreCase("-1")))){
				     	    %>
								<input type='text' name='sp_seq' class="Input_self common_border_color" value='<bean:write name="element" property="sp_seq" filter="true" />'  size="2" onKeypress="numCheck()" onchange='setBodySp_seq("<bean:write name="element" property="object_id" filter="true"/>","<bean:write name="element" property="mainbody_id" filter="true"/>","${implementForm.planid}",this)' disabled /> &nbsp;
							<%}else{ %>
								<input type='text' name='sp_seq' class="Input_self common_border_color" value='<bean:write name="element" property="sp_seq" filter="true" />'  size="2" onKeypress="numCheck()" onchange='setBodySp_seq("<bean:write name="element" property="object_id" filter="true"/>","<bean:write name="element" property="mainbody_id" filter="true"/>","${implementForm.planid}",this)' /> &nbsp;							
							<%} %>
							<%}else{ %>
							<bean:write name="element" property="sp_seq" filter="true" /> &nbsp;
							<%} %>
						</td>
					</logic:notEqual>
						<td align="right" class="RecordRow" nowrap>
				     	    <% if(planStatus.equals("3")||planStatus.equals("5")){ 	%>
							<input type='text' name='seq' class="Input_self common_border_color" value='<bean:write name="element" property="seq" filter="true" />'  size="2" onKeypress="numCheck()" onchange='setBodySeq("<bean:write name="element" property="object_id" filter="true"/>","<bean:write name="element" property="mainbody_id" filter="true"/>","${implementForm.planid}",this)' /> &nbsp;
							<%}else{ %>
							<bean:write name="element" property="seq" filter="true" /> &nbsp;
							<%} %>
						</td>
       			 	<%}else{%>
       			 		<logic:notEqual value="1" name="implementForm" property="method">
       			 		<td align="right" class="RecordRow" nowrap>
							<%
				     		LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
				     		String body_id=(String)abean.get("body_id");
				     		String object_id=(String)abean.get("object_id");
				     		String mainbody_id=(String)abean.get("mainbody_id");
				     		pageContext.setAttribute("object_id",object_id);
				     		pageContext.setAttribute("mainbody_id",mainbody_id);
				     		
				     	    %>		
				     	    <%  if(planStatus.equals("3")||planStatus.equals("5")){ 	
				     	    	if(body_id!=null && body_id.trim().length()>0 && ((object_type.equalsIgnoreCase("2") && body_id.equalsIgnoreCase("5")) || (!object_type.equalsIgnoreCase("2") && body_id.equalsIgnoreCase("-1")))){
				     	    %>
								<input type='text' name='sp_seq' class="Input_self common_border_color" value='<bean:write name="element" property="sp_seq" filter="true" />'  size="2" onKeypress="numCheck()" onchange='setBodySp_seq("<bean:write name="element" property="object_id" filter="true"/>","<bean:write name="element" property="mainbody_id" filter="true"/>","${implementForm.planid}",this)' disabled /> &nbsp;
							<%}else{ %>
								<input type='text' name='sp_seq' class="Input_self common_border_color" value='<bean:write name="element" property="sp_seq" filter="true" />'  size="2" onKeypress="numCheck()" onchange='setBodySp_seq("<bean:write name="element" property="object_id" filter="true"/>","<bean:write name="element" property="mainbody_id" filter="true"/>","${implementForm.planid}",this)' /> &nbsp;							
							<%} %>
							<%}else{ %>
							<bean:write name="element" property="sp_seq" filter="true" /> &nbsp;
							<%} %>
						</td>
						</logic:notEqual>
       			 	<%}%>					
				</logic:equal> 	
				<%if(method.equals("2")){%>
				<td align="left" class="RecordRow" nowrap>
				 &nbsp;<bean:write name="element" property="isgradesc" filter="true"/>
				</td>
				<%}%>
				<td align="center" class="RecordRow" nowrap>
				<%-- 不参与评分:禁用；参与评分且只有确认权限:禁用 add by 刘蒙 --%>
					<bean:define id="optMap_bodyid" value="${element.map.body_id }"></bean:define>
					<input type='checkbox' name='must' value='<bean:write name="element" property="mainbody_id" filter="true"/>:<bean:write name="element" property="object_id" filter="true"/>:${implementForm.planid}'   onclick='setBodyMustScore("<bean:write name="element" property="object_id" filter="true"/>","<bean:write name="element" property="mainbody_id" filter="true"/>","${implementForm.planid}",this)'  <logic:equal name="element" property="fillctrl"  value="1">checked</logic:equal>
						<logic:equal name="element" property="isgradesc" value="不评分">disabled</logic:equal> 
						<logic:equal name="element" property="isgradesc" value="评分">
							${implementForm.optMap[optMap_bodyid] }
						</logic:equal> />
				</td>
			</tr>
			</logic:iterate>
			</table>
			<script language='javascript' >
			
			parent.parent.ril_body1.setSecondPage(1)
			
			</script>
		
			
<% } else if(opt.endsWith("2")){  %>
			<table width="100%" border="0" cellspacing="0"  align="left" cellpadding="0" style="border:0px; margin-top:5px; BORDER-COLLAPSE: collapse;">
			<thead>
       			 <tr class="fixedHeaderTr1">
       			 <td align="center" width='30' class="TableRow" nowrap ><bean:message key="label.serialnumber"/></td><!-- 序号 -->
       			 <td align="center" width='100' class="TableRow" nowrap ><bean:message key="lable.performance.perMainBodySort"/></td><!-- 主体类别 -->
       			 <td align="center" width='70' class="TableRow" nowrap ><bean:message key="lable.performance.evaluateMan"/></td>
       			 <% for(int j=0;j<pointPowerHeadList.size();j++){
       			 		LazyDynaBean abean=(LazyDynaBean)pointPowerHeadList.get(j);
       			 		String pointname=(String)abean.get("pointname");
       			 		if(pointname.length()>20){
       			 %>
       			 <td align="center" valign="center" width='200' class="TableRow" nowrap  ><input type="checkbox" name="_pointPriv<%=j %>" onclick="batch_selectXXX(this,'pointPriv<%=j %>');setAllPointPriv(this,'pointPriv<%=j %>');" title='<bean:message key="label.query.selectall"/>'><%=pointname%></td>
       			 <%
      			 		}else{
       			  %>
       			 <td align="center" valign="center" width='150' class="TableRow" nowrap  ><input type="checkbox" name="_pointPriv<%=j %>" onclick="batch_selectXXX(this,'pointPriv<%=j %>');setAllPointPriv(this,'pointPriv<%=j %>');" title='<bean:message key="label.query.selectall"/>'><%=pointname%></td>
       			 <% }
       			 		} %>
       			
       			 </tr>
       		</thead>
       		<% i=0;  %>
       			 
       		<logic:iterate id="element" name="implementForm" property="pointPowerList" >	 
       		<% i++; %>
       			 <tr>
       			 	 <td align="center" width='30' style="border-top:0px;border-right:0px;" class="RecordRow" nowrap ><%=i%></td>
	       			 <td align="left" width='100' style="border-top:0px;border-right:0px;" class="RecordRow" nowrap > &nbsp;<bean:write name="element" property="bodyType" filter="true"/></td>
	       			 <td align="left" width='70' style="border-top:0px;border-right:0px;" class="RecordRow" nowrap > &nbsp;<bean:write name="element" property="bodyname" filter="true"/></td>
	       			 <% for(int j=0;j<pointPowerHeadList.size();j++){
	       			 		LazyDynaBean abean=(LazyDynaBean)pointPowerHeadList.get(j);
	       			 		String point_id=((String)abean.get("point_id")).toLowerCase();
	       			 	//	System.out.println(point_id);
	       			 	//	String objectid=(String)abean.get("objectid");
	       			 	//	String mainbody_id=(String)abean.get("mainbody_id");
	       			  %>

	       			 	<td align="center"  style="border-top:0px;" class="RecordRow"  >
	       	&nbsp;&nbsp;&nbsp;&nbsp;<input type='checkbox' name='pointPriv<%=j %>' value="<bean:write name="element" property="mainbody_id" filter="true"/>:<bean:write name="element" property="objectid" filter="true"/>:<%=point_id%>:${implementForm.planid}"  <% if(!planStatus.equals("3")&&!planStatus.equals("5")){ %> disabled  <% } %>  onclick='setPointPriv("<bean:write name="element" property="mainbody_id" filter="true"/>","<bean:write name="element" property="objectid" filter="true"/>","<%=point_id%>","${implementForm.planid}",this)'   value='1' <logic:equal name="element" property="<%=point_id%>"  value="1">checked</logic:equal>  />&nbsp;&nbsp;&nbsp;&nbsp;
	       			 	</td>
	       			 
	       			 <% } %>
	       			
       			 </tr>
       		</logic:iterate>
       		<!-- 	
       		<% //if(i==0){ %>
       		<tr>
       		 	<td align="center" width='30'  class="RecordRow" nowrap >&nbsp;&nbsp;&nbsp;&nbsp;</td>
       		 	<td align="center" width='70'  class="RecordRow" nowrap >&nbsp;&nbsp;&nbsp;&nbsp;</td>
       		 	<td align="center" width='70'  class="RecordRow" nowrap >&nbsp;&nbsp;&nbsp;&nbsp;</td>
       		 	 <%// for(int j=0;j<pointPowerHeadList.size();j++){%>
       		 	 	<td align="center" width='70'  class="RecordRow" nowrap >&nbsp;&nbsp;&nbsp;&nbsp;</td>
       		 	<% //} %>
       		 	<td align="center" width='70'  class="RecordRow" nowrap >&nbsp;&nbsp;&nbsp;&nbsp;</td>
       		</tr>	
       		<%//} %>
       		 -->
			</table>
	
			<script language='javascript' >
			
			parent.parent.ril_body1.setSecondPage(2)			
			</script>			
<%} else if(opt.endsWith("3")){%>		
		<table width="100%" border="0" cellspacing="0"  align="left" cellpadding="0" style="border:0px; margin-top:5px; BORDER-COLLAPSE: collapse">
			<thead>
       			 <tr  class="fixedHeaderTr1" >
       			 <%
	         		FieldItem fielditem = DataDictionary.getFieldItem("E0122");
	         	 %>
       			 <td align="center" width='100'  style=""   class="TableRow" nowrap ><bean:message key="b0110.label"/></td>
       			 <td align="center" width='100'   style=""  class="TableRow" nowrap ><%=fielditem.getItemdesc()%></td>
       			 <td align="center" width='100'  style=""   class="TableRow" nowrap ><bean:message key="e01a1.label"/></td>
       			 <td align="center" width='100'  style=""   class="TableRow" nowrap ><bean:message key="hire.employActualize.name"/></td>
       			 <td align="center" width='100'  style=""  class="TableRow" nowrap ><bean:message key="reporttypelist.sort"/></td>
       			 </tr>
       		</thead>
       		<% i=0; %>
			<logic:iterate id="element" name="implementForm" property="khRelaMainbody" >
			<% i++;
   	  	   if(i%2==1)
   	  	   	out.println("<tr class='trShallow'>");
   	  	   else
   	  	   	out.println("<tr class='trDeep'>");
   	  	 	%>
	        	<td align="left" style="border-top:0px;border-right:0px;"  class="RecordRow" nowrap>
				 &nbsp;<bean:write name="element" property="b0110" filter="true"/>
				</td>
				<td align="left" style="border-top:0px;border-right:0px;"  class="RecordRow" nowrap>
				 &nbsp;<bean:write name="element" property="e0122" filter="true"/>
				</td>
				<td align="left" style="border-top:0px;border-right:0px;"  class="RecordRow" nowrap>
				 &nbsp;<bean:write name="element" property="e01a1" filter="true"/>
				</td>
				<td align="left" style="border-top:0px;border-right:0px;" class="RecordRow" nowrap> 
				 &nbsp;<bean:write name="element" property="a0101" filter="true"/>
				</td>
				<td align="left" style="border-top:0px;" class="RecordRow" nowrap>
				 &nbsp;<bean:write name="element" property="bodyTypeName" filter="true"/>
				</td>
			</tr>
			</logic:iterate>
			</table>
			<script language='javascript' >
			
			parent.parent.ril_body1.setSecondPage(3)
			
			</script>
<% } else if(opt.endsWith("4")){  %>
			<table width="100%" border="0" cellspacing="0"  align="left" cellpadding="0" style="border:0px; margin-top:5px; BORDER-COLLAPSE: separate">
			<thead>
       			 <tr    class="fixedHeaderTr1"    >
       			 <td align="center" width='5%' style="border-right:0px;" class="TableRow" nowrap ><bean:message key="label.serialnumber"/></td>
       			 <td align="center" width='20%' style="border-right:0px;" class="TableRow" nowrap ><bean:message key="lable.performance.perMainBodySort"/></td>
       			 <% 
       			 	int width = 75/pointItemList.size();
       			 	for(int j=0;j<pointItemList.size();j++){
       			 		LazyDynaBean abean=(LazyDynaBean)pointItemList.get(j);
       			 		String itemdesc=(String)abean.get("itemdesc");
       			 		if(j==pointItemList.size()-1){
       			  %>
       			         <td align="center" width='<%=width %>%' class="TableRow"><input type="checkbox" name="_itemPriv<%=j %>" onclick="batch_selectXXX(this,'itemPriv<%=j %>');AllitemPriv(this,'itemPriv<%=j %>');" title='<bean:message key="label.query.selectall"/>'><%=itemdesc%></td>
       			     <% } else{%>
                     <td align="center" width='<%=width %>%' class="TableRow" style="border-right:0px;" ><input type="checkbox" name="_itemPriv<%=j %>" onclick="batch_selectXXX(this,'itemPriv<%=j %>');AllitemPriv(this,'itemPriv<%=j %>');" title='<bean:message key="label.query.selectall"/>'><%=itemdesc%></td>
       			    <% } %>
       			 <% } %>
       			 </tr>
       			 <!-- TableRow_5rows -->
       		</thead>
       		<% i=0;  %>
       			 
       		<logic:iterate id="element" name="implementForm" property="itemprivList" >	 
       		<% i++; %>
       			 <tr>
       			 	 <td align="center" style="border-top:0px;border-right:0px;" class="RecordRow" nowrap ><%=i%></td>
	       			 <td align="left" style="border-top:0px;border-right:0px;" class="RecordRow" nowrap >&nbsp; <bean:write name="element" property="bodyName" filter="true"/></td>
	       			 <% for(int j=0;j<pointItemList.size();j++){
	       			 		LazyDynaBean abean=(LazyDynaBean)pointItemList.get(j);
	       			 		String item_id=((String)abean.get("item_id"));
	       			  %>
	       			  
	       			 	<td align="center"  style="border-top:0px;" class="RecordRow"  >&nbsp;&nbsp;&nbsp;&nbsp;
	       			 		<%-- 主体中打分确认标识opt为1时，禁用复选框 by 刘蒙 --%>
	       			 		<%
								ImplementForm iform = (ImplementForm) session.getAttribute("implementForm");
	       			 			LazyDynaBean ldb = (LazyDynaBean) pageContext.getAttribute("element");
	       			 			Object optValue = iform.getOptMap().get(ldb.get("body_id")); // "disabled" or ""
	       			 			boolean isDisabled = false;
	       			 			String checked = "";
	       			 			// 当前body_id对应的optvalue为"disabled"时禁用该复选框
	       			 			if (optValue != null) {
	       			 				isDisabled = optValue.equals("") ? false : true;
	       			 				Object checkedValue = ldb.get(item_id);
       			 					checked = checkedValue != null && checkedValue.equals("1") ? "checked" : "";
	       			 			}
	       			 		%>
	       					<input type='checkbox' name='itemPriv<%=j %>'
	       						value="<bean:write name="element" property="object_id" filter="true"/>:<bean:write name="element" property="body_id" filter="true"/>:<%=item_id%>:${implementForm.planid}"
	       						<% if(!planStatus.equals("3")&&!planStatus.equals("5") || isDisabled) %> disabled <%=checked %>
	       						onclick='setItemPriv("<bean:write name="element" property="object_id" filter="true"/>","<bean:write name="element" property="body_id" filter="true"/>","<%=item_id%>","${implementForm.planid}",this)'  />&nbsp;&nbsp;&nbsp;&nbsp;
	       			 	</td>	       			 		       			 
       			
	       			 <% } %>

       			 </tr>
       		</logic:iterate>       		  		
			</table>		
			<script language='javascript' >
			
			parent.parent.ril_body1.setSecondPage(4)			
			</script>			
<%}%>	
</td></tr>
<tr><td style="height:35px">
<% 

if(planStatus.equals("3")||planStatus.equals("5")){  %>
<% if(opt.endsWith("1")){ %>
<hrms:priv func_id="326030129">
<button extra="button" id="clo" onclick='delmainBody("${implementForm.planid}","<%=template_id%>","<%=objectid%>")'    allowPushDown="false" down="false">删 除</button>&nbsp;
</hrms:priv>
<% } %>
<% if(opt.endsWith("2")&& pointPowerList!=null && pointPowerList.size()>0){ %>
<button extra="button" name='b1' onclick='selectAll("1");' style="position:relative;left:expression(this.offsetParent.scrollLeft+5);">&nbsp;全选&nbsp;</button>&nbsp;&nbsp;
<button extra="button" name='b2' onclick='selectAll("0");' style="position:relative;left:expression(this.offsetParent.scrollLeft+5);">&nbsp;全撤&nbsp;</button>&nbsp;
<% } %>
<% if(opt.endsWith("4") && itemprivList!=null && itemprivList.size()>0){ %>
<button extra="button" name='b3' onclick='batch_selectAll("1");' >&nbsp;全选&nbsp;</button>&nbsp;&nbsp;
<button extra="button" name='b4' onclick='batch_selectAll("0");' >&nbsp;全撤&nbsp;</button>&nbsp;
<script type="text/javascript">
// 复写batch_selectAll(implement.js:793) 全选/全撤项目权限 by 刘蒙
//个人觉得不需要复写，没必要加回调方法。异步保存了，回调刷新页面后还得重新定位选项卡，麻烦。2019-5-10 haosl
/*function batch_selectAll(theFlag) {
	var temp = document.getElementsByTagName("input");
  	var item_ids = new Array();
  	for(var i = 0; i < temp.length; i++) {
  		if (temp[i].type != "checkbox") continue;
  		if (temp[i].disabled) continue; // 禁用的复选框弃之不理
  		
	  	if(temp[i].name.substring(0, 8) == "itemPriv") {
	  		temp[i].checked = theFlag == '1' ? true : false;
			item_ids[item_ids.length] = temp[i].value;	
		} else if(temp[i].name.substring(0, 9) == "_itemPriv") {
			temp[i].checked = theFlag == '1' ? true : false;			
		}
	}

	var hashvo = new ParameterSet();
	hashvo.setValue("item_ids", item_ids);
	if (theFlag == '1') {
		hashvo.setValue("item_value", "1");
	} else {
	    hashvo.setValue("item_value", "0");
    }
	hashvo.setValue("opt", "8");
	var request = new Request({method:'post', asynchronous:false, functionId:'9023000003'}, hashvo);
}

function winReload(outparamters){
	parent.window.location.reload();
}*/
</script>
<% } %>
<% }
%>
		
</td></tr>

</table>

</html:form>
<script language='javascript' >
<% if(opt.endsWith("1")){ %>
	var temp=document.getElementsByName("must");
	var str="1";
	for(var i=0;i<temp.length;i++){
		if(!temp[i].checked){
			str="2";
			break;
		}
	}
	if(str=="1"){
		var temp1=document.getElementsByName("sx");
		temp1[0].checked=true;
	}
<%}%>
<% if(opt.endsWith("4")){ %>
<% for(int j=0;j<pointItemList.size();j++){%>
	
	var temp=document.getElementsByName("itemPriv<%=j %>");
	var str="1";
	var _str="1";
	for(var i=0;i<temp.length;i++){
		if(!temp[i].checked){
			str="2";
		}
		if(!temp[i].disabled){
			_str="2";
		}
	}
	if(str=="1"){
		var temp1=document.getElementsByName("_itemPriv<%=j %>");
		temp1[0].checked=true;
	}  
	if(_str=="1"){
		var temp2=document.getElementsByName("_itemPriv<%=j %>");
		temp2[0].disabled=true;
	}    			  
<%}%>	
<%}%>       	

<% if(opt.endsWith("2")){ %>
<% for(int j=0;j<pointPowerHeadList.size();j++){%>
	
	var temp=document.getElementsByName("pointPriv<%=j %>");
	var str="1";
	var _str="1";
	for(var i=0;i<temp.length;i++){
		if(!temp[i].checked){
			str="2";
		}

		if(!temp[i].disabled){
			_str="2";
		}
	}
	if(str=="1"){
		var temp1=document.getElementsByName("_pointPriv<%=j %>");
		temp1[0].checked=true;
	}      		
	if(_str=="1"){
		var temp2=document.getElementsByName("_pointPriv<%=j %>");
		temp2[0].disabled=true;
	}  	  
<%}%>	
<%}%>    		  
</script>
</body>
</html>
<script type="text/javascript">
	document.body.style.marginTop = "0px";
	if(!getBrowseVersion()){
	    var form = document.getElementsByTagName("form")[0];
	    if(form){
	        form.style.paddingRight="5px";
        }
    }
	
</script>