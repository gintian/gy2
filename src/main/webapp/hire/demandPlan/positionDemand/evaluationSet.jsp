<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hrms.struts.taglib.CommonData"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,com.hjsj.hrms.actionform.hire.demandPlan.PositionDemandForm"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script language='javascript' >

function goback()
{
    var obj= new Object();
      obj.refresh="0";
      returnValue=obj;
      window.close();
}

function changeselect() {
	var id = document.getElementById("selectid").value;
	document.getElementById("testid").value = id;
}
</script>

<html:form action="/hire/demandPlan/positionDemand/positionDemandTree">
<%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String bosflag= userView.getBosflag();//得到系统的版本号
    if(bosflag!=null&&!bosflag.equalsIgnoreCase("hcm")){
%>
<br>
<%
}
%>
<table height="110" width="440" border="0" cellpadding="0" cellspacing="0" align="center" class="ftable">

<tr height="20">
       		<td align="left" class="TableRow" colspan="2">&nbsp;<bean:message key="lable.zp_plan.appoint"/>&nbsp;</td>
</tr> 
<tr>
 <td width="120" align="right"><bean:message key="lable.zp_plan.evaluationList"/></td>
	<td>
		<html:hidden name="positionDemandForm" property="testid" styleId="testid"/>
		<select name="select" onchange="changeselect()" size="1" id="selectid">
		<%PositionDemandForm positionDemandForm = (PositionDemandForm)session.getAttribute("positionDemandForm");
		String testid = positionDemandForm.getTestid();
		ArrayList list =  positionDemandForm.getTestTemplateList();
		for(int i = 0; i<list.size(); i++){
		    CommonData data = (CommonData)list.get(i);
		    String id = data.getDataValue();
		    String titleDesc = data.getDataName();
		    String showDesc = titleDesc;
		    if(showDesc != null && showDesc.length() > 15)
		        showDesc = showDesc.substring(0, 15) + "...";
		        
		%>
		  <option value="<%=id %>" title="<%=titleDesc %>"
		  <%if(id.equalsIgnoreCase(testid)){ %> selected="selected"<%} %>
		  ><%=showDesc %></option>
		<%} %>
		</select>
  </td>
</tr>
   
 <tr>
 <td width="120" align="right">  <bean:message key="lable.content_channel.type"/>
 </td>
       <td>
           <html:radio name="positionDemandForm" property="valid" value="1"/>
           <bean:message key="lable.performance.grademark"/>&nbsp;<html:radio name="positionDemandForm" property="valid" value="2"/> 
           <bean:message key="label.performance.mixmark"/>				               	      
       </td>
 </tr>    


<tr class="list3">
            <td align="center" colspan="2" style="height:35px">
         	<hrms:submit styleClass="mybutton" property="b_savebrowse2" onclick='goback()'>
            		<bean:message key="button.save"/>
	 	</hrms:submit>
         			<input type='button' value="<bean:message key="button.cancel"/>"  onclick='goback()' class="mybutton" >             
            </td>
          </tr> 
</table>
</html:form>
