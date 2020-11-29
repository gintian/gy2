<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@page import="com.hjsj.hrms.actionform.performance.nworkplan.QueryMonthWorkPlanForm,
               org.apache.commons.beanutils.LazyDynaBean,
               java.util.ArrayList,
               com.hrms.struts.valueobject.UserView,
			   com.hrms.struts.constant.WebConstant
               "%>
<%
  QueryMonthWorkPlanForm myForm=(QueryMonthWorkPlanForm)session.getAttribute("queryMonthWorkPlanForm");
  String pagerows = String.valueOf(myForm.getPagerows());
  UserView userView=(UserView)session.getAttribute(WebConstant.userView);
  String backurl="";
  if(userView != null){
	  backurl = (String)userView.getHm().get("backurl"); 
  }
%>               
<hrms:themes />
<script>
function changeQuery(init){
    queryMonthWorkPlanForm.action = "/performance/nworkplan/queryMonthWorkPlan.do?b_query=link&init="+init;
    queryMonthWorkPlanForm.submit();
}
function returnback(backurl){
    window.location.href=backurl;
}
function gotoworkplan(p0100,state){
    if(state=='2')
        window.location.href = "/performance/nworkplan/searchMonthWorkplan.do?b_query=link&init=fromquery&p0100="+p0100;
    else if(state=='1')
        window.location.href = "/performance/nworkplan/week/searchWeekWorkplan.do?b_query=link&init=fromquery&p0100="+p0100;
}
</script>
 

  	<html:form action="/performance/nworkplan/queryMonthWorkPlan">
    	<table cellpadding="0" cellspacing="0" style="margin-top:5px;margin-left:5px;" class="ListTable">
    		<tr>
    			<td  style="width:600px;" colspan="3">
    				工作内容: <html:text property="queryContent" name="queryMonthWorkPlanForm"></html:text>&nbsp;
    				<input type="button" value="查询" class="mybutton" onclick="changeQuery('query');" />
	    			<input type="button" value="返回" class="mybutton" onclick="returnback('<%=backurl %>');" />
    			</td>
    			
    		</tr>
    		<tr>
    			<td colspan="3">
    				&nbsp;
	    		</td>
    		</tr>
    		<tr>
    			<td  class="TableRow" align="center" style="width:15%;" nowrap>
    				序号
    			</td>
    			<td  class="TableRow" align="center" style="border-left:0;width:60%;" nowrap>
    				工作内容
    			</td>
    			<td class="TableRow" align="center" style="border-left:0;width:25%;" nowrap>
    				周期
    			</td>
    		</tr>
    		<hrms:extenditerate id="element" name="queryMonthWorkPlanForm" property="setlistform.list" indexes="indexes"  pagination="setlistform.pagination" pageCount="<%=pagerows %>" scope="session">   	
	         <tr class="trShallow" onMouseOver="javascript:tr_onclick(this,'E4F2FC')" height="70px">
	            <td class="RecordRow">
	               <bean:write name="element" property="count" />
	            </td>
	            <td class="RecordRow" style="cursor:pointer" onclick="gotoworkplan('<bean:write name="element" property="p0100"/>','<bean:write name="element" property="state"/>')">
	               <bean:write name="element" property="content" />
	            </td>
	            <td class="RecordRow">
	               <bean:write name="element" property="cycle" />
	            </td>
		     </tr>
		    </hrms:extenditerate>
		    <tr>
		    	<td colspan="3">
			    	<table width="100%" align="center" class="RecordRowP">
			    	    <tr>
						    <td valign="bottom" class="tdFontcolor"><bean:message key="hmuster.label.d"/>
						    
						    <bean:write name="queryMonthWorkPlanForm" property="setlistform.pagination.current" filter="true" />
									<bean:message key="hmuster.label.paper"/>
									<bean:message key="hmuster.label.total"/>
							
						    <bean:write name="queryMonthWorkPlanForm" property="setlistform.pagination.count" filter="true" />
						    
									
									<bean:message key="label.item"/>
									<bean:message key="hmuster.label.total"/>
							
						    <bean:write name="queryMonthWorkPlanForm" property="setlistform.pagination.pages" filter="true" />	
									<bean:message key="hmuster.label.paper"/>
									&nbsp;&nbsp;
							 <bean:message key="log.teamwork.workplan.show"/><html:text property="pagerows" name="queryMonthWorkPlanForm" size="3"></html:text>
							        
									 
							 <bean:message key="label.every.row"/>
							 &nbsp;&nbsp;
							 <a href="javascript:changeQuery('refresh');"><bean:message key="label.page.refresh"/></a>
							</td>
					        <td  align="right" nowrap class="tdFontcolor">
					               
								<p align="right"><hrms:paginationlink name="queryMonthWorkPlanForm" property="setlistform.pagination"
								nameId="setlistform" propertyId="roleListProperty">
								</hrms:paginationlink>
							</td>
						</tr>
			    	</table>
		    	</td>
		    </tr>
    	</table>
    </html:form>
