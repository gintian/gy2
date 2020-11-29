<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import=" com.hjsj.hrms.actionform.performance.implement.ImplementForm, org.apache.commons.beanutils.LazyDynaBean,com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant" %>
				 
<%
	ImplementForm implementForm=(ImplementForm)session.getAttribute("implementForm");	
	String planStatus=implementForm.getPlanStatus();
	ArrayList pointItemList=implementForm.getPointItemList();
	int i=0;
		  UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
 %>
<script language="javascript">
function returns()
{	
	//implementForm.action="/selfservice/performance/performancePointPrivImplement.do?br_return=return";
	implementForm.action="/selfservice/performance/performanceImplement.do?b_query=link&a_code=${performanceImpForm.a_code}";
	implementForm.submit();
}

</script>
<style>
 .TableRow_self {

	background-position : center left;
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
 <hrms:themes />
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GB18030">
<script language="JavaScript" src="implement.js"></script>
<SCRIPT LANGUAGE=javascript src="/js/constant.js"></SCRIPT>  
<title>Insert title here</title>
</head>
<body>
<html:form action="/performance/implement/performanceImplement">
<%if("hl".equals(hcmflag)){%>
	<br>
<% } %>
<table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0" >

  <tr>
    <td align="left" nowrap style="height:20px">       
        <bean:message key="lable.appraisemutual.examineobject"/>: <bean:write name="implementForm" property="khObject" filter="false"/>&nbsp;
    </td> 
   </tr>
<tr height="20"><td>
	<bean:message key="performance.item.priv"/>
</td></tr>
<tr><td>	
		<table  border="0" cellspacing="0"  align="left" cellpadding="0" class="ListTable">
			<thead>
       			 <tr>
       			 <td align="center" width='30'   class="TableRow_self common_border_color" nowrap ><bean:message key="label.serialnumber"/></td>
       			 <td align="center" width='100'   class="TableRow_self common_border_color" nowrap ><bean:message key="lable.performance.perMainBodySort"/></td>
       			 <% for(int j=0;j<pointItemList.size();j++){
       			 		LazyDynaBean abean=(LazyDynaBean)pointItemList.get(j);
       			 		String itemdesc=(String)abean.get("itemdesc");
       			  %>
       			 <td align="center" width='100' class="TableRow_self common_border_color"   ><%=itemdesc%></td>
       			 <% } %>
       			 </tr>
       		</head>
       		<% i=0;  %>
       			 
       		<logic:iterate id="element" name="implementForm" property="itemprivList" >	 
       		<% i++; %>
       			 <tr>
       			 	 <td align="left" width='30'  class="RecordRow" nowrap >&nbsp;<%=i%></td>
	       			 <td align="left" width='70'  class="RecordRow" nowrap >&nbsp; <bean:write name="element" property="bodyName" filter="true"/></td>
	       			 <% for(int j=0;j<pointItemList.size();j++){
	       			 		LazyDynaBean abean=(LazyDynaBean)pointItemList.get(j);
	       			 		String item_id=((String)abean.get("item_id"));
	       			  %>
	       			  
	       			 	<td align="center"   class="RecordRow"  >
	       	&nbsp;&nbsp;&nbsp;&nbsp;<input type='checkbox' name='itemPriv'  <% if(!planStatus.equals("3")&&!planStatus.equals("5")){ %> disabled  <% } %>  onclick='setItemPriv("<bean:write name="element" property="object_id" filter="true"/>","<bean:write name="element" property="body_id" filter="true"/>","<%=item_id%>","${implementForm.planid}",this)'   value='1' <logic:equal name="element" property="<%=item_id%>"  value="1">checked</logic:equal>  />&nbsp;&nbsp;&nbsp;&nbsp;
	       			 	</td>
	       			 
	       			 <% } %>

       			 </tr>
       		</logic:iterate>       		
			</table>			
</td></tr>
<tr height="30"><td style="height:35px">
	 <input type="button" name="bt_return"  value="<bean:message key="button.return"/>" class="mybutton" onclick="returns()">    
</td></tr>
</table>
</html:form>

</body>
</html>