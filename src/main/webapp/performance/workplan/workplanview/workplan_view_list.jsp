<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.performance.WorkPlanViewForm,
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant,
				 com.hrms.frame.dao.RecordVo,
				 com.hjsj.hrms.utils.PubFunc,
				 com.hrms.frame.codec.SafeCode,
				 org.apache.commons.beanutils.LazyDynaBean" %>

<%
		WorkPlanViewForm workPlanViewForm = (WorkPlanViewForm)session.getAttribute("workPlanViewForm");	
		if(!"view".equals(workPlanViewForm.getSearchflag())){
			workPlanViewForm.setSearchterm("");
		}
		workPlanViewForm.setSearchflag("");
  	    String state = workPlanViewForm.getState();	   // =0 日报    =1 周报    =2 月报     =3 季报     =4 年报 	
  	    
  	    UserView userView = (UserView)session.getAttribute(WebConstant.userView);     	    		   		
   		String a0100 = userView.getA0100();
   		a0100 = SafeCode.encode(PubFunc.convertTo64Base(a0100));
   		String nbase = userView.getDbname();
   		nbase = SafeCode.encode(PubFunc.convertTo64Base(nbase));
   		String home = request.getParameter("home");
   		String returnFlag = request.getParameter("returnFlag");
   			      
%>

<SCRIPT LANGUAGE=javascript src="/performance/workplan/workplanview/workplanview.js"></SCRIPT>
<html:form action="/performance/workplan/workplanview/workplan_view_list">
<input type="hidden" name="returnURL" value="/performance/workplan/workplanview/workplan_view_list.do?b_query=link"/>
<input type="hidden" name="target" value="il_body"/>

<html:hidden property="workType" name="workPlanViewForm"/>
<html:hidden property="state" name="workPlanViewForm"/>
  <table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
    <tr>
       <td  colspan="5" align="left" style="height:35px">
       <% if(!state.equalsIgnoreCase("4")){ %>
	       <bean:message key="performance.workplan.workplanview.yearFen"/><hrms:optioncollection name="workPlanViewForm" property="yearList" collection="list" />
			   <html:select name="workPlanViewForm" property="year" size="1" style="width:150px;" onchange="changeQuery();">
					 <html:options collection="list" property="dataValue" labelProperty="dataName"/>
			   </html:select>
			  &nbsp;&nbsp;
	   <%} %>		        
	   <% if(state.equalsIgnoreCase("1") || state.equalsIgnoreCase("0")){ %>		        
  		  <bean:message key="performance.workplan.workplanview.monthFen"/><hrms:optioncollection name="workPlanViewForm" property="monthList" collection="list" />
						 <html:select name="workPlanViewForm" property="month" size="1" style="width:150px;" onchange="changeQuery();">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>
	   <%} %>
			  <logic:equal value="" name="workPlanViewForm" property="searchterm">
			  <html:text name="workPlanViewForm" style="color:gray" value="请输入批示、内容"  onfocus="notext(this);"  onblur="addtext(this)" property="searchterm" size="25"/>	
			  &nbsp;
			  </logic:equal>
			  <logic:notEqual value="" name="workPlanViewForm" property="searchterm">
			  <logic:equal value="请输入批示、内容" name="workPlanViewForm" property="searchterm">
			  <html:text name="workPlanViewForm" style="color:gray" onfocus="notext(this);"  onblur="addtext(this)" property="searchterm" size="25"/>
			  </logic:equal>
			  <logic:notEqual value="请输入批示、内容" name="workPlanViewForm" property="searchterm">
			  <html:text name="workPlanViewForm"  onfocus="notext(this);"  onblur="addtext(this)" property="searchterm" size="25"/>	
			  &nbsp;
			  </logic:notEqual>
			  </logic:notEqual>
			  &nbsp;
			  <BUTTON name="bdel" class="mybutton"  onclick="search();"><bean:message key="button.query"/></BUTTON>
			</td>
    </tr>

    <tr>
      <td align="center" class="TableRow">
      <% if(state.equalsIgnoreCase("4")){ %>
       		<bean:message key="performance.workplan.workplanview.yearDu"/>
      <% }else if(state.equalsIgnoreCase("3")){ %>
       		<bean:message key="performance.workplan.workplanview.quarterDu"/>
      <% }else if(state.equalsIgnoreCase("2")){ %>
       		<bean:message key="performance.workplan.workplanview.monthDu"/>
      <% }else if(state.equalsIgnoreCase("1")){ %>
       		<bean:message key="performance.workplan.workplanview.weekReport"/>
      <% }else if(state.equalsIgnoreCase("0")){ %>
       		<bean:message key="performance.workplan.workplanview.dayReport"/>
      <%} %>
      </td>
      <td align="center" class="TableRow">
       <bean:message key="performance.workplan.workplanview.type"/>
      </td>
      <td align="center" class="TableRow">
       <bean:message key="performance.workplan.workplanview.fillTime"/>
      </td>
      <td align="center" class="TableRow">
       <bean:message key="performance.workplan.workplanview.state"/>
      </td>
      <td align="center" class="TableRow">
       <bean:message key="performance.workplan.workplanview.operation"/>
      </td>
    </tr>
    <hrms:extenditerate id="element" name="workPlanViewForm" property="planListForm.list" indexes="indexes"  pagination="planListForm.pagination" pageCount="35" scope="session">
     <%int i=0;%>
       <logic:iterate id="sub" name="element" property="subList" indexId="index" offset="0">
         <%if(i==0){ %>
           <tr><td rowspan="<bean:write name="element" property="rowspan"/>" align="left" class="RecordRow">&nbsp;<bean:write name="element" property="month"/></td>
         <%}else{ %>
           <tr>
         <%} %>
         <td align="left" class="RecordRow">&nbsp;<bean:write name="sub" property="name"/></td>
         <td align="left" class="RecordRow">&nbsp;<bean:write name="sub" property="time"/></td>
         <td align="center" class="RecordRow"><bean:write name="sub" property="p0115desc"/></td>
         <td align="center" class="RecordRow" style="padding-top:3px;padding-bottom:3px;">
         <logic:equal value="0" name="sub" property="opt">
         	<input type="button" class="mybutton" value="<bean:message key="performance.workplan.workplanview.see"/>" 
         		onclick='writePlan("<bean:write name="sub" property="mdopt"/>","<%=nbase%>","<%=a0100%>","<bean:write name="sub" property="log_type"/>"
         		,"<bean:write name="sub" property="mdp0100"/>","<%=state%>","<bean:write name="sub" property="p0115"/>","<bean:write name="element" property="year_num"/>"
         		,"<bean:write name="element" property="quarter_num" />","<bean:write name="element" property="month_num"/>"
         		,"<bean:write name="element" property="week_num" />","<bean:write name="element" property="day_num" />");'/>
         </logic:equal>
         <logic:equal value="1" name="sub" property="opt">
         	<input type="button" class="mybutton" value="<bean:message key="performance.workplan.workplanview.fill"/>" 
         		onclick='writePlan("<bean:write name="sub" property="mdopt"/>","<%=nbase%>","<%=a0100%>","<bean:write name="sub" property="log_type"/>"
         		,"<bean:write name="sub" property="mdp0100"/>","<%=state%>","<bean:write name="sub" property="p0115"/>","<bean:write name="element" property="year_num"/>"
         		,"<bean:write name="element" property="quarter_num" />","<bean:write name="element" property="month_num"/>"
         		,"<bean:write name="element" property="week_num" />","<bean:write name="element" property="day_num" />");'/>
         </logic:equal>
         <logic:equal value="2" name="sub" property="opt">
         	<input type="button" class="mybutton" value="<bean:message key="performance.workplan.workplanview.fill"/>" disabled/>
         </logic:equal>
         </td>
         </tr>
         <%i++; %>
       </logic:iterate>
    </hrms:extenditerate>
    
<%--     
    <tr> 
		<td colspan="5" class="RecordROw">
		    <table  width="100%" align="center">
				<tr>
				   <td valign="bottom" class="tdFontolor" nowrap>第
				   <bean:write name="workPlanViewForm" property="planListForm.pagination.current" filter="true"/>
				   页
				   共
				   <bean:write name="workPlanViewForm" property="planListForm.pagination.count" filter="true"/>
				   条
				   共
				   <bean:write name="workPlanViewForm" property="planListForm.pagination.pages" filter="true"/>
				   页
				   </td>
				   <td align="right" class="tdFontcolor" nowrap>
				   <p align="right">
				   <hrms:paginationlink name="workPlanViewForm" property="planListForm.pagination" nameId="planListForm" propertyId="planListProperty">
				   </hrms:paginationlink>
				   </td>
				</tr> 
			</table>
		</td>
	</tr>
--%>

  </table>
</html:form>
<script type="text/javascript">
	//2016/1/27 wangjl 
	function notext(dd){
		 dd.value="";
		 dd.style.color="black";
	}
	function addtext(ad){
		 if(ad.value==""){
			 ad.value="请输入批示、内容";
			 ad.style.color="gray";
			 }
}
</script>