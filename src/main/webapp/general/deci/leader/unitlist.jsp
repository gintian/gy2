<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="java.util.*,
                com.hrms.struts.constant.WebConstant,
                com.hrms.struts.valueobject.UserView,
				com.hjsj.hrms.actionform.general.deci.leader.LeaderForm,
				org.apache.commons.beanutils.LazyDynaBean" %>
<% 
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String bosflag = "";
    if (null != userView) {
        bosflag = userView.getBosflag();
        bosflag = bosflag != null ? bosflag : "";
    } 
%>

<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<script language="javascript" src="/js/validate.js"></script>


<script language="javascript">
	function searchUnitList()
	{
		var tablename=$F('setlist');
		leaderForm.action="/general/deci/leader/unitlist.do?b_search=link&tablename="+tablename;
		leaderForm.submit();
	}
	function showunitcard()
	{
		var t_url = "/general/deci/leader/ykcard.do?b_search=link";	         
       window.open(t_url,"_blank","left=0,top=0,width="+screen.availWidth+",height="+screen.availHeight+",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");
	
	}
</script>

<body style="margin-left: 10px;margin-top:-10px;">
<table  width='' border='0' cellspacing='0'  align='left' cellpadding='0'><tr><td style="padding-bottom: 5px">
<html:form action="/general/deci/leader/unitlist"> <span style="vertical-align: middle;">
	<b><bean:message key="leaderteam.unitlist.positionnumbercomplexion"/></b>
	<html:select styleId ="setlist" name="leaderForm" size="1" property="select_file" onchange="searchUnitList();">    
		<html:optionsCollection property="fieldlist" value="dataValue" label="dataName"/>	
	</html:select>&nbsp;</span>
	<span style="vertical-align: middle;">
	<logic:notEqual name="leaderForm" property="unitcard" value="">
		<button extra="mybutton" onclick="showunitcard();"><bean:message key="leaderteam.leaderparam.unitnumbertable"/></button>
	</logic:notEqual>
	<logic:equal value="dxt" name="leaderForm" property="returnvalue">
        <%
           if (bosflag.equals("hcm"))
           {
        %>
        <input type='button' class="mybutton" name="returnButton"
            onclick='hrbreturn("leader", "il_body", "leaderForm");'
            value='<bean:message key='reportcheck.return'/>' />
        <%
           }
        %>
      </logic:equal> </span>
      </td></tr><tr><td>
	<table width="100%" border="0" cellspacing="0" cellpadding="0" class="ListTable">
		<logic:iterate id="unitfile" name="leaderForm" property="unitfilelist">
	  	 	<td align="center" class="TableRow" nowrap >
	  	 	<bean:write name="unitfile" property="dataName" />&nbsp;
	  	 	</td>
	  	 </logic:iterate>
	  	 <logic:notEqual name="leaderForm" property="columns" value="">
	  <hrms:paginationdb id="element" name="leaderForm" sql_str="leaderForm.strsql" table="" where_str="" columns="leaderForm.columns" order_by="" page_id="pagination" pagerows="7" distinct="" indexes="indexes" keys="">
	  	 <tr>
	  	 
	  	 <%
	  	 	LeaderForm leaderform  = (LeaderForm)session.getAttribute("leaderForm");
	  	 	ArrayList unitlist = leaderform.getUnitlist();
	  	 	for(int i=0;i<unitlist.size();i++){
	  	 		LazyDynaBean bean = (LazyDynaBean)unitlist.get(i);
	  	 		if(bean.get("itemid").toString().equalsIgnoreCase("B0110")){
	  	 %>
	  	 	<td align="left" class="RecordRow" nowrap>
	          		<hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>         
	           <bean:write name="codeitem" property="codename" />&nbsp;
	        </td>
	  	 <%
	  	 		}
				else if(!(bean.get("itemtype").toString().equalsIgnoreCase("A")&&!bean.get("codesetid").toString().equalsIgnoreCase("0"))){
	  	 %>
	  	 <td align="right" class="RecordRow" nowrap>
	  	 	<bean:write name="element" property='<%=bean.get("itemid").toString()%>'/>&nbsp;
	  	 </td>
	  	 <%	
	  	 		}else if(bean.get("itemtype").toString().equalsIgnoreCase("A")&&!bean.get("codesetid").toString().equalsIgnoreCase("0")){
	  	 %>
	  	 <td align="left" class="RecordRow" nowrap>
	 		<hrms:codetoname codeid='<%=bean.get("codesetid").toString().toUpperCase()%>' name="element" codevalue='<%=((String)bean.get("itemid")).toLowerCase()%>' codeitem="codeitem" scope="page"/>         
	           <bean:write name="codeitem" property="codename" />&nbsp;
	 	</td>
	  	 
	  	 <%
	  	 		}
	  	 %>
	  	 <%
	  	 	}
	  	 %>
	  	 
	  	
	  	 	
	          
		</tr>
	  </hrms:paginationdb>
	 
	<table  width="100%" align="center" class="RecordRowP">
	  <tr>
	      <td valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
	     <bean:write name="pagination" property="current" filter="true" />
	     <bean:message key="label.page.sum"/>
	     <bean:write name="pagination" property="count" filter="true" />
	     <bean:message key="label.page.row"/>
	     <bean:write name="pagination" property="pages" filter="true" />
	      <bean:message key="label.page.page"/>
	   </td>
	       <td  align="right" nowrap class="tdFontcolor">
	            <p align="right"><hrms:paginationdblink name="leaderForm" property="pagination" nameId="leaderForm" scope="page">
	    </hrms:paginationdblink>
	   </td>
	   
	  </tr>
	</table>
	</logic:notEqual>
	 <logic:equal name="leaderForm" property="columns" value="">
	 <!-- br><br><br><br><br><br>
	 	<h5 align="center"><strong>
	 	</strong></h5>
	 	 -->
	 	<script>
	 	alert("<bean:message key="leaderteam.unitlist.promptmessage"/>");
	 	</script>
	 </logic:equal>
</table> 
</html:form>
</td>
</tr>
</table>
</body>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
