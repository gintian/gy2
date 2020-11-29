<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.sys.HomeForm"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<% 

	HomeForm homeForm = (HomeForm)session.getAttribute("homeForm");
	int currentPage=0,i=0;
	if(homeForm.getRecordListForm()!=null)
	{
		currentPage = homeForm.getRecordListForm().getPagination().getCurrent();
		i=(currentPage-1)*15;
	}
    
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String bosflag="";
    if(userView!=null){
     bosflag = userView.getBosflag();
    }
	//判断当前用户是否自助用户（4）还是业务用户（0）
	int status=0;
	status = userView.getStatus();
	String isEpmLoginFlag="0";	
	String hcmflag="";
	if(userView != null){
	  isEpmLoginFlag=(String)userView.getHm().get("isEpmLoginFlag"); 
	  isEpmLoginFlag = (isEpmLoginFlag==null||isEpmLoginFlag.equals(""))?"0":isEpmLoginFlag;
	  hcmflag=userView.getBosflag();
	}
	String returnvalue = request.getParameter("returnvalue")==null?"":request.getParameter("returnvalue");
%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script type="text/javascript">
document.oncontextmenu = function(e) {return false;}
function goback(){
  var isEpmLoginFlag="<%=isEpmLoginFlag%>";
       		if('<%=hcmflag%>'=="hcm"){
 	      		 url='/templates/index/hcm_portal.do?b_query=link';      		
       		}else{
 	       		url='/templates/index/portal.do?b_query=link';      		
       		}
  if(isEpmLoginFlag=='1'){
      url = '/templates/index/subportal.do?b_query=link';
      parent.location.href = url;
  }
  window.location.href = url;
}
</script>
<link href="../../css/css1.css" rel="stylesheet" type="text/css">
<hrms:themes></hrms:themes>
<html:form action="/general/template/matterList">
<table width="70%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" style="margin-top:8px;">
		<thead>
			<tr>
			    <td align="center" width="30" class="TableRow" nowrap>
				  序号
				</td>
				<td align="center" class="TableRow" nowrap>
				任务名称&nbsp;
				</td>
			</tr>
		</thead>
		
	<hrms:extenditerate id="element" name="homeForm" property="recordListForm.list" indexes="indexes" pagination="recordListForm.pagination" pageCount="15" scope="session">
          <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow">
          <%}
          else
          {%>
          <tr class="trDeep">
          <%
          }
          
          %> 
			<td align="left" class="RecordRow" nowrap>		
					&nbsp;<%=(++i)%>&nbsp;
				
			</td>
			<td align="left" class="RecordRow" nowrap>		
			&nbsp;&nbsp;
			<a href='<bean:write name="element" property="dataValue" filter="false" />'>
			<bean:write name="element" property="dataName" filter="false" />
			</a>
			</td>
		</tr>
	</hrms:extenditerate>
</table>

<table width="70%" align="center" class="RecordRowP">
	<tr>
		<td valign="bottom" class="tdFontcolor">
		    <bean:message key="label.page.serial"/>
			<bean:write name="homeForm" property="recordListForm.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
			<bean:write name="homeForm" property="recordListForm.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
			<bean:write name="homeForm" property="recordListForm.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
		</td>
		<td align="right" nowrap class="tdFontcolor">
			<p align="right">
				<hrms:paginationlink name="homeForm" property="recordListForm.pagination" nameId="recordListForm">
				</hrms:paginationlink>
		</td>
	</tr>
</table>
<center><div style="width: 70%;text-align: center;margin-top: 5px">

<input type="button" value='<bean:message key="button.return"/>' class="mybutton" onclick="goback()"/>

</div></center>
</html:form>