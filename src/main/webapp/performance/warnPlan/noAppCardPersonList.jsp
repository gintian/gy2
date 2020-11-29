<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.performance.warnPlan.WarnPlanForm,
				 com.hrms.struts.constant.SystemConfig,
				 com.hrms.hjsj.sys.DataDictionary,
				 com.hrms.hjsj.sys.FieldItem,
				 com.hrms.struts.constant.WebConstant" %>

<%
 	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	    String tt4CssName="ttNomal4";
	    String tt3CssName="ttNomal3";
	    String buttonClass="mybutton";
	    if(SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("zglt"))
	    {
	       tt4CssName="tt4";
	       tt3CssName="tt3";
	       buttonClass="mybuttonBig";
	    }
%>
<%  
		WarnPlanForm warnPlanForm = (WarnPlanForm)session.getAttribute("warnPlanForm");
		String levelstr = warnPlanForm.getLevel();					
		int level = 1;
		if(levelstr!=null && !levelstr.equals(""))
		{
		   level = Integer.parseInt(levelstr);
		}
%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="JavaScript" src="/js/validateDate.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<link href="/performance/objectiveManage/objectiveCard/objectiveCard.css" rel="stylesheet" type="text/css">

<style>

.TableRow_self 
{
	
	margin-left:auto;
	margin-right:auto;
	background-position : center;
	background-color:#f4f7f7;
	font-size: 12px;  
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	height:25px;
	font-weight: bold;	
	valign:middle;
}
</style>

<script type="text/javascript">

function detail(object_id)
{
  var plan_id="${warnPlanForm.plan_id}";
  var thecodeurl="/performance/objectiveManage/objectiveCardMonitor/objective_state_list.do?b_detail=link`plan_id="+plan_id+"`object_id="+object_id; 
  var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
  var retvo = window.showModalDialog(iframe_url, null, 
			 "dialogWidth:600px; dialogHeight:430px;resizable:no;center:yes;scroll:yes;status:no");			
  
}

function sendMail(object_id,to_a0100)
{
 
	 var url="/performance/objectiveManage/objectiveCardMonitor/objective_state_list.do?b_initMail=link`opt=1`plan_id=${warnPlanForm.plan_id}`object_id="+object_id+"`to_a0100="+to_a0100;
	 var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(url);
   // window.open(url,"_aa","hotkeys=0,menubar=0,height=470,width=700");
    window.showModalDialog(iframe_url,"","dialogWidth=700px;dialogHeight=470px;resizable=yes;scroll=yes;status=no;");  
}

</script>


<html:form action="/performance/warnPlan/noAppCardPersonList">	
<br>

<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0">
	<tr>
		<td align="left" height='25' nowrap >                	 		
	       	<strong><font size='3'><bean:write name="warnPlanForm" property="plan_name" filter="true" /></font></strong>
	 	</td>
	</tr>
	<tr>
		<td>
			<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
			<thead>
				<tr>
					<%    
						 FieldItem fielditem = DataDictionary.getFieldItem("E0122");
					%>
					<td align="center" class="TableRow" nowrap >
						&nbsp;<%=fielditem.getItemdesc()%>&nbsp;
					</td>
				  	<td align="center" class="TableRow" nowrap>
				  		&nbsp; <font class='<%=tt4CssName%>'><bean:message key="lable.performance.perObject"/></font>&nbsp;
				  	</td>
				  	<td align="center" class="TableRow" nowrap>
				  		&nbsp;<font class='<%=tt4CssName%>'><bean:message key="label.zp_resource.status"/></font>&nbsp;
				  	</td>
				  	<td align="center" class="TableRow" nowrap>
				  		&nbsp;<font class='<%=tt4CssName%>'><bean:message key="label.performance.reportdate"/></font>&nbsp;
				  	</td>
				  	<logic:iterate id="cloumn" name="warnPlanForm" property="leaderList" offset="0">
				    	<td align="center" class="TableRow" nowrap>
				      		<font class='<%=tt4CssName%>'><bean:write name="cloumn" property="sp"/></font>
				      	</td>
				       	<td align="center" class="TableRow" nowrap>
				      		<font class='<%=tt4CssName%>'><bean:write name="cloumn" property="spd"/></font>
				      	</td>
				 	</logic:iterate>
				</tr>
			</thead>
			
			<% int j=0; %>
	 		<hrms:extenditerate id="element" name="warnPlanForm" property="setlistform.list" indexes="indexes"  pagination="setlistform.pagination" pageCount="20" scope="session">
	 		<%if(j%2==0){ %>
		     	<tr class="trShallow">
		    <%} else { %>
		     	<tr class="trDeep">
		    <%}%>
		    
		       	<td align="center" class="RecordRow" nowrap>
		    		&nbsp;<font class='<%=tt3CssName%>'><bean:write name="element" property="_e0122"/></font>
		     	</td>		     
		     	<td align="left" class="RecordRow" nowrap>
		     		<a href="javascript:detail('<bean:write name="element" property="object_id"/>');">
		     		&nbsp;<font class='<%=tt3CssName%>'><bean:write name="element" property="a0101"/></font>&nbsp;</a>
		     	</td>
		      	<td align="left" class="RecordRow" nowrap>
		     		&nbsp;<font class='<%=tt3CssName%>'><bean:write name="element" property="sp_flag"/></font>&nbsp;		     
		     		<logic:equal name="element" property="flag" value="01">
		     			<img src='/images/mail2.gif'  onclick='sendMail("<bean:write name="element" property="object_id"/>","<bean:write name="element" property="object_id"/>")' />
		     		</logic:equal>
		     		<logic:equal name="element" property="flag" value="07">
		     			<img src='/images/mail2.gif'  onclick='sendMail("<bean:write name="element" property="object_id"/>","<bean:write name="element" property="object_id"/>")' />
		     		</logic:equal>		     		     
		     	</td>
		      	<td align="left" class="RecordRow" nowrap>
		     		&nbsp;<font class='<%=tt3CssName%>'><bean:write name="element" property="report_date"/></font>&nbsp;
		     	</td>
		     	<% for(int i=1;i<=level;i++)
		           {
		        		String date=String.valueOf(i)+"date";
		        		String xx=String.valueOf(i);
		     	%>
		        <td align="left" class="RecordRow" nowrap>
		         	&nbsp;<font class='<%=tt3CssName%>'><bean:write name="element" property="<%=xx%>" filter="false"  /></font>&nbsp;
		        </td>
		        <td align="left" class="RecordRow" nowrap>
		       		&nbsp;<font class='<%=tt3CssName%>'><bean:write name="element" property="<%=date%>"/></font>&nbsp;
		        </td>
		     	<% }%>
		     </tr>
		     <% j++; %>
		    </hrms:extenditerate>
			</table>
		</td>
	</tr>
	<tr>
		<td align="center">
			<table width="100%" align="center" class="RecordRowP">
				<tr>
					<td valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
						<bean:write name="warnPlanForm" property="setlistform.pagination.current" filter="true" />
						<bean:message key="label.page.sum"/>
						<bean:write name="warnPlanForm" property="setlistform.pagination.count" filter="true" />
						<bean:message key="label.page.row"/>
						<bean:write name="warnPlanForm" property="setlistform.pagination.pages" filter="true" />
						<bean:message key="label.page.page"/>
					</td>
				    <td align="right" nowrap class="tdFontcolor">	               
						<p align="right">
					    <hrms:paginationlink name="warnPlanForm" property="setlistform.pagination"
							nameId="setlistform" propertyId="roleListProperty">
						</hrms:paginationlink>
						</p>
					</td>
				</tr>
			</table>					
		</td> 
	</tr>
	<tr>		
		<td align="left" style="height:35px"> 
		<%if("hcm".equals(userView.getBosflag())){ %>
			<html:button styleClass="mybutton" property="bc_btn1" onclick="window.location.replace('/templates/index/hcm_portal.do?b_query=link');">
				<bean:message key="button.return" />
			</html:button>
			<%}else{ %>
			<html:button styleClass="mybutton" property="bc_btn1" onclick="window.location.replace('/templates/index/portal.do?b_query=link');">
				<bean:message key="button.return" />
			</html:button>
			<%} %>
		</td>				
	</tr>
</table>
	
</html:form>