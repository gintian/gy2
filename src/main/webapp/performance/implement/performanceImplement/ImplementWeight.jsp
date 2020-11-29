<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page
	import="java.util.*,com.hjsj.hrms.actionform.performance.implement.ImplementForm"%>
<%
    String closeFlag = "";
    if(request.getParameter("closeflag")!=null){
        closeFlag = (String)request.getParameter("closeflag");
    }
%>
<html>
	<style>
	div#treemenu {
	BORDER-BOTTOM:#94B6E6 1pt solid;
	BORDER-LEFT: #94B6E6 1pt solid;
	BORDER-RIGHT: #94B6E6 1pt solid;
	BORDER-TOP: #94B6E6 0pt solid;
	width: 400px;
	height: 200px;
	overflow: auto;
	}
</style>
	<script language='javascript'>
    <% if(closeFlag!=null && closeFlag.length()>0){ %>
        parent.window.close();
    <%}%>
  function mincrease1(obj_name)
  {
  		var objs =document.getElementsByName(obj_name);
  		if(objs==null)
  		  return false;
  		var obj=objs[0];
  		if(parseFloat(obj.value)>0&&parseFloat(obj.value)<1){
		obj.value = (parseFloat(obj.value)+0.1).toFixed(1);
		}
		if(parseFloat(obj.value)==0){
		obj.value = (parseFloat(obj.value)+0.1).toFixed(1);
		}
  }
  function msubtract1(obj_name)
  {
  		var objs =document.getElementsByName(obj_name);
  		if(objs==null)
  		  return false;
  		var obj=objs[0];
  		if(parseFloat(obj.value)>0&&parseFloat(obj.value)<1){
		obj.value = (parseFloat(obj.value)-0.1).toFixed(1);
		}
		if(parseFloat(obj.value)==1){
		obj.value = (parseFloat(obj.value)-0.1).toFixed(1);
		}
  }
  
  function checkNum(obj)
  {
  	if(checkIsNum(obj.value))
  	{
  		if(obj.value*1>1)
  		{
  			alert(KHSS_KHZT_QZ1);
  			obj.value="0.0";
  			return;
  		}
  	}
  	else
  	{
  		alert(KHSS_KHZT_QZ2);
  		obj.value="0.0";
  		return;
  	}
  }
  	function IsDigit(obj) 
	{
		if((event.keyCode >= 46) && (event.keyCode <= 57) && event.keyCode!=47)
		{
			var values=obj.value;
			if((event.keyCode == 46) && (values.indexOf(".")!=-1))//有两个.
				return false;
			if((event.keyCode == 46) && (values.length==0))//首位是.
				return false;	
			return true;
		}
			return false;	
	}
  
  function enter()
  {
 	var value=0;
  	for(var i=0;i<document.implementForm.elements.length;i++)
  	{
  		if(document.implementForm.elements[i].type=='text')
  		{
  			if(ltrim(rtrim(document.implementForm.elements[i].value))=='')
  				document.implementForm.elements[i].value=0;
  			value+=document.implementForm.elements[i].value*1;
  		}
  	}

  	if((Math.abs(value-1)<0.0000001)==false)
  	{
  		alert(KHSS_KHZT_QZ3);
  		return;
  	}
  	
    document.implementForm.action="/performance/implement/performanceImplement.do?b_sub=link"+"&closeflag=true";
  	document.implementForm.submit();
  }
  </script>
	<body>
		<html:form action="/performance/implement/performanceImplement">
			<table width='100%'  align="center" style="margin-left:-3px;">			
				<tr>
					<td width='100%'>
						<div id='treemenu' style="width:490px;">
							<table width="100%" border="0" cellspacing="0"
								cellpadding="0" class="ListTable">
								<thead>
									<tr>
										<td align="center" class="TableRow" width='15%' nowrap style="border-left:0px;">
											<bean:message key='label.serialnumber' />
										</td>
										<td align="center" class="TableRow" width='35%' nowrap>
											<bean:message key='performance.implement.examinebodytype' />
										</td>
										<td align="center" class="TableRow" width='50%' nowrap style="border-right:0px;">
											<bean:message key='label.kh.template.qz' />
										</td>
									</tr>
								</thead>
								<%
								int i = 0;
								%>
								<logic:iterate id="element" name="implementForm"
									property="purviewList" indexId="index">
									<%
									if (i % 2 == 0) {
									%>
									<tr class="trShallow">
										<%
										} else {
										%>
									
									<tr class="trDeep">

										<%
													}
													i++;
										%>
										<td align="center" class="RecordRow" nowrap style="border-left:0px;">
											<%=(i)%>
										</td>
										<td align="center" class="RecordRow" nowrap>
											<bean:write name="element" property="name" filter="true" />
										</td>
										<td class="RecordRow" nowrap style="border-right:0px;">
											<table>
												<tr>
													<td>
														<%-- 确认的主体权重为0，且不允许修改 by 刘蒙 --%>
														<bean:define id="rankValue" value="${element.map.rank }"></bean:define>
														<bean:define id="isDisabled" value=""></bean:define>
														<logic:equal name="element" property="pbOpt" value="1">
															<bean:define id="isDisabled" value="disabled"></bean:define>
															<bean:define id="rankValue" value="0.0"></bean:define>
														</logic:equal>
														<input type="text" name="<%="purviewList[" + index + "].rank"%>" ${isDisabled }
															 onkeypress="event.returnValue=IsDigit(this);" value="${rankValue }" size='6' class="inputtext"/>
													</td>
													<td>
														<table border="0" cellspacing="2" cellpadding="0">
														<!-- xus 19/12/26 【56680】V77绩效管理：考核实施/考核主体/设置主体权重，按钮样式有问题，见附件 -->
															<tr><td><button type="button" ${isDisabled } id="m_up" class="m_arrow" onclick="mincrease1('<%="purviewList[" + index + "].rank"%>')">5</button></td></tr>
															<tr><td><button type="button" ${isDisabled } id="m_down" class="m_arrow" onclick="msubtract1('<%="purviewList[" + index + "].rank"%>')">6</button></td></tr>
														</table>
													</td>
												</tr>
											</table>
										</td>
									</tr>
								</logic:iterate>
							</table>
						</div>
					</td>				
				</tr>
			</table>
<table width='100%'  >			
				<tr>
	<td align="center" >
						<input type='button' value='<bean:message key='button.ok' />' onclick='enter()'
							class="mybutton">
					
						<input type='button' value='<bean:message key='lable.tz_template.cancel' />'
							onclick='javascript:parent.window.close()' class="mybutton">
					</td>
				</tr>
			</table>
		</html:form>
	</body>
</html>
