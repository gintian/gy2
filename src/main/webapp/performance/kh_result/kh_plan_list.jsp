<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.SystemConfig,
				 org.apache.commons.beanutils.LazyDynaBean,
				 com.hjsj.hrms.utils.PubFunc,
				 com.hrms.struts.constant.WebConstant,
				 com.hrms.struts.valueobject.UserView,				 
				 com.hjsj.hrms.actionform.performance.kh_result.KhResultForm" %>
<%
   		String tt4CssName="ttNomal4";
   		String tt3CssName="ttNomal3";
   		String buttonClass="mybutton";
   		if(SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("zglt"))
   		{
      		tt4CssName="tt4";
      		tt3CssName="tt3";
      		buttonClass="mybuttonBig";
   		}
   		KhResultForm khResultForm = (KhResultForm)session.getAttribute("khResultForm");
   		String mda0100=PubFunc.encryption(khResultForm.getObject_id());
   		UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
   		
%>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript">
	var ViewProperties=new ParameterSet();
</script>
<script type="text/javascript">
<!--
function ret(code,model,distinctionFlag,nbase)
{
   	khResultForm.action="/performance/kh_result/kh_result_personlist.do?b_init=init&opt=1&a_code="+code+"&model="+model+"&distinctionFlag="+distinctionFlag+"&nbase="+nbase;
   	khResultForm.submit();   
}
function ret2(a0100,nbase){
	
	document.location="/performance/nworkdiary/myworkdiary/deptperson.do?b_query=link&a0100="+a0100+"&nbase="+nbase;
}
function query(model)
{
   	var year ;
   	var obj=document.getElementById("performanceYear");
   	for(var i=0;i<obj.options.length;i++)
   	{
      	if(obj.options[i].selected)
      	{
         	year=obj.options[i].value;
         	break;
      	}
   	}
   	if(model=='3')
   	{
      	khResultForm.action="/performance/kh_result/org_kh_plan.do?b_init=init&year="+year+"&distinctionFlag=${khResultForm.distinctionFlag}&model=3&modelType=${khResultForm.modelType}";
      	khResultForm.submit();
   	}
   	else
   	{
      	khResultForm.action="/performance/kh_result/kh_plan_list.do?b_init=link&year="+year+"&model=${khResultForm.model}&distinctionFlag=${khResultForm.distinctionFlag}&a0100=<%=mda0100%>"
      	khResultForm.submit();
   	}
}
function returnTOWizard()
{
    khResultForm.action="/templates/attestation/police/wizard.do?br_postwizard=link";
    khResultForm.target="il_body";
    khResultForm.submit();
}
function goDetail(model,distinctionFlag,plan_id,object_id)
{
	var action = "/performance/kh_result/kh_result_figures.do?b_init2=link";
	action += "&model=" + model;
	action += "&distinctionFlag=" + distinctionFlag;
	action += "&planid=" + plan_id;
	action += "&object_id=" + object_id;
	action += "&opt=1";
	action += "&chartParameters=null";
	action += "&isEncrypted=true"; // 关键数据是否加密
    khResultForm.action = action;
    khResultForm.submit();
}

function confirmed(planId,objectId,personOrTeamType) {//personOrTeamType,,person:人员，team:团队
	if(!confirm("您将执行结果确认操作？"))
    {
      return;
    }
	var hashvo=new ParameterSet();   
    hashvo.setValue("plan_id",planId); 
    hashvo.setValue("object_id",objectId); 
    hashvo.setValue("personOrTeamType",personOrTeamType); 
    var request=new Request({method:'post',asynchronous:false,onSuccess:confirm_ok,functionId:'90100130031'},hashvo);    
}

function confirm_ok(outparameter)
{
    var type = outparameter.getValue("personOrTeamType");
    alert("确认成功！");
    if(type == "person") {
    	khResultForm.action = "/performance/kh_result/kh_plan_list.do?b_init=link&amp;model=0&amp;distinctionFlag=0&amp;opt=1";
    } else if(type == "team") {
    	khResultForm.action = "/performance/kh_result/org_kh_plan.do?b_init=init&amp;distinctionFlag=0&amp;model=3&amp;modelType=UU";
    }
    khResultForm.submit();
}
//-->
</script>
<link href="/performance/objectiveManage/objectiveCard/objectiveCard.css" rel="stylesheet" type="text/css">
<html:form action="/performance/kh_result/kh_plan_list">
<%if("hl".equals(hcmflag)){ %>
<br>
<%} %>

<table align="center" border="0" width="85%" cellpmoding="0" cellspacing="0" cellpadding="0">
<tr>
<td align="left" style="height:35px">   
<font class="<%=tt3CssName%>"><bean:message key="org.performance.evaluate"/></font>:
<html:select name="khResultForm" property="performanceYear" onchange="query('${khResultForm.model}');">
			<html:optionsCollection property="performanceYearList" value="dataValue" label="dataName"/>
		    </html:select>
</td>
</tr>
<tr>
<td>
<table border="0" width="100%" cellpmoding="0" cellspacing="0"  class="ListTable"  cellpadding="0">
<thead>
<tr>
<td align="center" class="TableRow" nowrap>
<font class="<%=tt4CssName%>"><bean:message key="label.serialnumber"/></font>
</td>
<td align="center" class="TableRow" nowrap>
<font class="<%=tt4CssName%>"><bean:message key="lable.zp_plan.name"/></font>
</td>
<td align="center" class="TableRow" nowrap>
<font class="<%=tt4CssName%>"><bean:message key="jx.khplan.objectype"/></font>
</td>
<td align="center" class="TableRow" nowrap>
<font class="<%=tt4CssName%>"><bean:message key="org.performance.khgrade"/></font>
</td>
<logic:equal value="0" name="khResultForm" property="model"><!-- 区别模块=0是本人=1是员工=3是团队*/ -->
<hrms:priv func_id="06030101">
<td align="center" class="TableRow" nowrap>
<font class="<%=tt4CssName%>"><bean:message key="label.zp_exam.sum_score"/></font><!-- 总分 -->
</td>
</hrms:priv>
<hrms:priv func_id="06030103">
<td align="center" class="TableRow" nowrap>
<font class="<%=tt4CssName%>"><bean:message key="jx.khplan.confirmResult"/></font><!-- 结果确认 -->
</td>
</hrms:priv>
<hrms:priv func_id="06030102">
<td align="center" class="TableRow" nowrap>
<font class="<%=tt4CssName%>"><bean:message key="kq.strut.more"/></font><!-- 详细 -->
</td>
</hrms:priv>
</logic:equal>
<%-- 2013.12.16 pjf 
<logic:notEqual value="0" name="khResultForm" property="model">
<td align="center" class="TableRow" nowrap>
<font class="<%=tt4CssName%>"><bean:message key="label.zp_exam.sum_score"/></font>
</td>
<td align="center" class="TableRow" nowrap>
<font class="<%=tt4CssName%>"><bean:message key="kq.strut.more"/></font>
</td>
</logic:notEqual>
--%>
<logic:equal value="1" name="khResultForm" property="model"><!-- 区别模块=0是本人=1是员工=3是团队*/ -->
<hrms:priv func_id="06040101">
<td align="center" class="TableRow" nowrap>
<font class="<%=tt4CssName%>"><bean:message key="label.zp_exam.sum_score"/></font><!-- 总分 -->
</td>
</hrms:priv>
<hrms:priv func_id="06040102">
<td align="center" class="TableRow" nowrap>
<font class="<%=tt4CssName%>"><bean:message key="kq.strut.more"/></font><!-- 详细 -->
</td>
</hrms:priv>
</logic:equal>
<logic:equal value="3" name="khResultForm" property="model"><!-- 区别模块=0是本人=1是员工=3是团队*/ -->
<hrms:priv func_id="06030301">
<td align="center" class="TableRow" nowrap>
<font class="<%=tt4CssName%>"><bean:message key="label.zp_exam.sum_score"/></font><!-- 总分 -->
</td>
</hrms:priv>
<hrms:priv func_id="06030303">
<td align="center" class="TableRow" nowrap>
<font class="<%=tt4CssName%>"><bean:message key="jx.khplan.confirmResult"/></font><!-- 审核确认 -->
</td>
</hrms:priv>
<hrms:priv func_id="06030302">
<td align="center" class="TableRow" nowrap>
<font class="<%=tt4CssName%>"><bean:message key="kq.strut.more"/></font><!-- 详细 -->
</td>
</hrms:priv>
</logic:equal>
</tr>
</thead>
<% int i=0; %>
<hrms:extenditerate id="element" name="khResultForm" property="planListForm.list" indexes="indexes"  pagination="planListForm.pagination" pageCount="15" scope="session">
<%if(i%2==0){ %>
	     <tr class="trShallow">
	     <%} else { %>
	     <tr class="trDeep">
	     <%}%>
<td align="right" class="RecordRow" nowrap>
&nbsp;<font class="<%=tt3CssName%>"><%=i+1%></font>&nbsp;
</td>
<td align="left" class="RecordRow" nowrap>
&nbsp;<font class="<%=tt3CssName%>"><bean:write name="element" property="name"/></font>&nbsp;
</td>
<td align="left" class="RecordRow" nowrap>
&nbsp;<font class="<%=tt3CssName%>"><bean:write name="element" property="object_type"/></font>&nbsp;
</td>
<td align="left" class="RecordRow" nowrap>
&nbsp;<font class="<%=tt3CssName%>"><bean:write name="element" property="grade"/></font>&nbsp;
</td>

<logic:equal value="0" name="khResultForm" property="model">
<%
	LazyDynaBean bean=(LazyDynaBean)pageContext.getAttribute("element");
	String mdoid=PubFunc.encryption(((String)bean.get("object_id")));
	String mdpid=PubFunc.encryption(((String)bean.get("plan_id")));
	String personOrTeamType=PubFunc.encryption(((String)bean.get("personOrTeamType")));
%>
<hrms:priv func_id="06030101">
<td align="right" class="RecordRow" nowrap>
&nbsp;<font class="<%=tt3CssName%>"><bean:write name="element" property="score"/></font>&nbsp;
</td>
</hrms:priv>
<hrms:priv func_id="06030103">
<td align="left" class="RecordRow" nowrap>
  <logic:equal name="element" property="confirmFlag" value="2">
    &nbsp;<font class="<%=tt3CssName%>"><bean:message key="jx.khplan.confirmed"/></font>&nbsp;
  </logic:equal>
  <logic:notEqual name="element" property="confirmFlag" value="2">
    <a href="javascript:void(0);" onclick='confirmed("<%=mdpid%>","<%=mdoid %>","<%=personOrTeamType %>")'><bean:message key="jx.khplan.confirm"/></font>&nbsp;
  </logic:notEqual>
</td>
</hrms:priv>
<hrms:priv func_id="06030102">
<td align="center" class="RecordRow" nowrap>
<logic:equal name="element" property="visibleEdit" value="1">
<%if(SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("zglt")){ %>
<a href="/performance/kh_result/kh_result_muster.do?b_init=link&model=${khResultForm.model}&distinctionFlag=${khResultForm.distinctionFlag}&opt=1&planid=<bean:write name="element" property="plan_id"/>&object_id=<bean:write name="element" property="object_id"/>"><img src="/images/view.gif" border="0"/></a>
<%}else{%>
<img src="/images/view.gif" style="cursor:hand;" border="0"  onclick='goDetail("${khResultForm.model}","${khResultForm.distinctionFlag}","<%=mdpid%>","<%=mdoid%>");'/>
<%} %>
</logic:equal>
</td>
</hrms:priv>
</logic:equal>
<%-- 
<logic:notEqual value="0" name="khResultForm" property="model">
<td align="right" class="RecordRow" nowrap>
<font class="<%=tt3CssName%>">&nbsp;<bean:write name="element" property="score"/></font>
</td>
<td align="center" class="RecordRow" nowrap>
<logic:equal name="element" property="visibleEdit" value="1">
<%if(SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("zglt")){ %>
<a href="/performance/kh_result/kh_result_muster.do?b_init=link&model=${khResultForm.model}&distinctionFlag=${khResultForm.distinctionFlag}&opt=1&planid=<bean:write name="element" property="plan_id"/>&object_id=<bean:write name="element" property="object_id"/>"><img src="/images/edit.gif" border="0"/></a>
<%}else{
LazyDynaBean bean=(LazyDynaBean)pageContext.getAttribute("element");
String mdoid=PubFunc.encryption(((String)bean.get("object_id")));
String mdpid=PubFunc.encryption(((String)bean.get("plan_id")));

 %>
<img src="/images/edit.gif" style="cursor:hand;" border="0"  onclick='goDetail("${khResultForm.model}","${khResultForm.distinctionFlag}","<%=mdpid%>","<%=mdoid%>");'/>
<%} %>
</logic:equal>
</td>
</logic:notEqual>
--%>  
<logic:equal value="1" name="khResultForm" property="model">
<hrms:priv func_id="06040101">
<td align="right" class="RecordRow" nowrap>
&nbsp;<font class="<%=tt3CssName%>"><bean:write name="element" property="score"/></font>&nbsp;
</td>
</hrms:priv>
<hrms:priv func_id="06040102">
<td align="center" class="RecordRow" nowrap>
<logic:equal name="element" property="visibleEdit" value="1">
<%if(SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("zglt")){ %>
<a href="/performance/kh_result/kh_result_muster.do?b_init=link&model=${khResultForm.model}&distinctionFlag=${khResultForm.distinctionFlag}&opt=1&planid=<bean:write name="element" property="plan_id"/>&object_id=<bean:write name="element" property="object_id"/>"><img src="/images/view.gif" border="0"/></a>
<%}else{ 
LazyDynaBean bean=(LazyDynaBean)pageContext.getAttribute("element");
String mdoid=PubFunc.encryption(((String)bean.get("object_id")));
String mdpid=PubFunc.encryption(((String)bean.get("plan_id")));
%>
<img src="/images/view.gif" style="cursor:hand;" border="0"  onclick='goDetail("${khResultForm.model}","${khResultForm.distinctionFlag}","<%=mdpid%>","<%=mdoid%>");'/>
<%} %>
</logic:equal>
</td>
</hrms:priv>
</logic:equal>
<logic:equal value="3" name="khResultForm" property="model">
<%
	LazyDynaBean bean=(LazyDynaBean)pageContext.getAttribute("element");
	String mdoid=PubFunc.encryption(((String)bean.get("object_id")));
	String mdpid=PubFunc.encryption(((String)bean.get("plan_id")));
	String personOrTeamType=PubFunc.encryption(((String)bean.get("personOrTeamType")));
%>
<hrms:priv func_id="06030301">
<td align="right" class="RecordRow" nowrap>
&nbsp;<font class="<%=tt3CssName%>"><bean:write name="element" property="score"/></font>&nbsp;
</td>
</hrms:priv>
<hrms:priv func_id="06030303">
<td align="left" class="RecordRow" nowrap>
  <logic:equal name="element" property="confirmFlag" value="2">
   	 &nbsp;<font class="<%=tt3CssName%>"><bean:message key="jx.khplan.confirmed"/></font>&nbsp;
  </logic:equal>
  
  <logic:equal name="element" property="isTeamLeader" value="true"><%--只有团队负责人才能点击考核确认--%>
  	<logic:notEqual name="element" property="confirmFlag" value="2">
    	<a href="javascript:void(0);" onclick='confirmed("<%=mdpid%>","<%=mdoid %>","<%=personOrTeamType %>")'><bean:message key="jx.khplan.confirm"/></font>&nbsp;
    </logic:notEqual>
  </logic:equal>
  
</td>
</hrms:priv>
<hrms:priv func_id="06030302">
<td align="center" class="RecordRow" nowrap>
<logic:equal name="element" property="visibleEdit" value="1">
<%if(SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("zglt")){ %>
<a href="/performance/kh_result/kh_result_muster.do?b_init=link&model=${khResultForm.model}&distinctionFlag=${khResultForm.distinctionFlag}&opt=1&planid=<bean:write name="element" property="plan_id"/>&object_id=<bean:write name="element" property="object_id"/>"><img src="/images/view.gif" border="0"/></a>
<%}else{%>
<img src="/images/view.gif" style="cursor:hand;" border="0"  onclick='goDetail("${khResultForm.model}","${khResultForm.distinctionFlag}","<%=mdpid%>","<%=mdoid%>");'/>
<%} %>
</logic:equal>
</td>
</hrms:priv>
</logic:equal>
<% i++; %>
</hrms:extenditerate>
</table>
</td>
</tr>
<tr>
<td>

<table  width="100%" align="center" class="RecordRowP">
		<tr>
		   <td valign="bottom" class="tdFontcolor" nowrap>
		            <bean:message key="label.page.serial"/>
		   ${khResultForm.planListForm.pagination.current}
					<bean:message key="label.page.sum"/>
		   ${khResultForm.planListForm.pagination.count}
					<bean:message key="label.page.row"/>
		   ${khResultForm.planListForm.pagination.pages}
					<bean:message key="label.page.page"/>
		   </td>
		   <td align="right" class="tdFontcolor" nowrap>
		   <p align="right">
            <hrms:paginationlink name="khResultForm" property="planListForm.pagination" nameId="planListForm" propertyId="planListProperty">
		   </hrms:paginationlink>
		   </p>
		   </td>
		</tr> 
</table>


</td>
</tr>
<tr>
<td align="left" style="height:35px">   
   <logic:equal value="1" name="khResultForm" property="model">
		    <input type="button" name="ll" value="<bean:message key="button.return"/>" onclick='ret("${khResultForm.code}","${khResultForm.model}","${khResultForm.distinctionFlag}","${khResultForm.nbase}");' class="<%=buttonClass%>"/>
</logic:equal>
 <logic:equal value="2" name="khResultForm" property="model">
		    <input type="button" name="ll" value="<bean:message key="button.return"/>" onclick='ret2("<%=khResultForm.getObject_id()%>","USR");' class="<%=buttonClass%>"/>
</logic:equal>
<logic:equal value="poloicewizard" name="khResultForm" property="returnvalue">
                          <input type='button' name='b_save' value='返回' onclick='returnTOWizard();' class='mybutton'>
</logic:equal>	
</td>
</tr>
</table>
</html:form>