<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.welcome.WelcomeForm,com.hjsj.hrms.utils.PubFunc" %>
<%
  UserView userView=(UserView)session.getAttribute(WebConstant.userView);
  String url="";
  if(userView != null)
  {
     url=userView.getBosflag();
  
  }
  WelcomeForm welcomeForm=(WelcomeForm)session.getAttribute("welcomeForm");
	//String mdid=PubFunc.encryption(welcomeForm.getHomePageHotId());
	String mdid = welcomeForm.getHomePageHotId();
	String encryptType = welcomeForm.getEnteryType();
	String home = welcomeForm.getHome();
%>
<%
	int i=0;
%>
<script type="text/javascript">
<!--
function reutrnBack()
{
	//【5297】主页-热点调查-查看结果-返回（空白的）  jingq upd 2014.11.24
    //welcomeForm.action="/selfservice/welcome/hot_topic.do?b_query=query&homePageHotId=<%=mdid%>&enteryType=${welcomeForm.enteryType}&home=${welcomeForm.home}";
   	welcomeForm.action = "/selfservice/welcome/hot_topic.do?b_query=query&encryptParam=<%=PubFunc.encrypt("homePageHotId="+mdid+"&encryptType="+encryptType+"&home="+home)%>";
    welcomeForm.submit();
}
//-->
</script>

<style>

.RecordRow_self {
	border: inset 0px #C4D8EE;
	border-bottom-style: dotted;
	border-bottom-width: 1px;
	
	
	BORDER-LEFT: #C4D8EE 0pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 0pt solid;
	font-size: 12px;

	height:22;
}

.RecordRow_self0 {
	border: inset 0px #C4D8EE;
	border-bottom-style: dotted;
	border-bottom-width: 1px;
	
	
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 0pt solid;
	font-size: 12px;

	height:22;
}

.RecordRow {
    border: inset 1px #C4D8EE;
    BORDER-BOTTOM: #C4D8EE 1pt solid; 
    BORDER-LEFT: #C4D8EE 1pt solid; 
    BORDER-RIGHT: #C4D8EE 1pt solid; 
    BORDER-TOP: #C4D8EE 0pt solid;
    font-size: 12px;
    height:22px;
}

</style>
<hrms:themes />
<script language="JavaScript" src="/anychart/js/AnyChart.js"></script>
<script language="JavaScript" src="/ajax/basic.js"></script>

<html:form action="/selfservice/welcome/welcome" >
<%
    String bosflag= userView.getBosflag();//得到系统的版本号
    
%>  
<%
    if(bosflag!=null&&!bosflag.equals("hcm")){
%>
<Br>
<%
}
%>
    <table width="700" border="0" cellspacing="0"  align="center" cellpadding="0"  class="ListTableF" style="margin-top:10px;">
  	   <tr>
    		<td class="TableRow common_border_color" nowrap colspan="3"><bean:message key="lable.topicname"/>:<bean:write name="welcomeForm" property="name" filter="true"/></td>
	   </tr> 
    	
        <%int j=0; %>
  	 <logic:iterate  id="element" name="welcomeForm" property="itemwhilelst"  scope="session">
    	
    	<tr> 
   		 <td width="600" class="RecordRow common_border_color" nowrap colspan="3">
   		 <bean:write name="element" property="itemName"/>&nbsp;
   		 </td>
   		
       </tr>
  	 <logic:iterate id="test" name="element" property="endviewlst" >
 	 <tr>
    		<td class="RecordRow_self0 common_border_color" nowrap> 
	    		<table border="0" cellspacing="0" cellpadding="0">
	    			<tr>
	    			<td width="350" align="left" style="word-break: break-all;">
	    				&nbsp;°<bean:write name="test" property="pointName" filter="true"/>
	    			</td>	
	    			<td>
	    				<logic:equal name="test" property="conextFlag" value="1">
	    					<a href="/selfservice/welcome/infodescribe.do?b_query=link&itemid=<bean:write name="test" property="itemid" filter="true"/>&pointid=<bean:write name="test" property="pointid" filter="true"/>">
	    					<bean:message key="lable.welcome.invdescribe.describeinfo"/>
	    					</a>
	    				</logic:equal>
	    			</td>
	    			</tr>
	    		</table>
    		</td>
    		<td class="RecordRow_self common_border_color" nowrap>&nbsp;<bean:write name="test" property="sumNum" filter="true"/>
     		 <br>
       		</td>
       		<td class="RecordRow_self common_border_color" nowrap align='center' >  				
    					
    					<bean:write name="test" property="precent" filter="true"/>
   					   
   			</td>
  	</tr>
   	
  </logic:iterate>
  <logic:equal name="element" property="chartFlag" value="true">
  <tr>
  <td colspan="3" align="center" id='<%="pnl_"+j %>'>
   <hrms:chart name="element" title="" scope="page" legends="picList" data="" width="600" height="350" chart_type="11" chartpnl='<%="pnl_"+j %>'>
   </hrms:chart></td>
   </tr>
   </logic:equal>
   <%j++; %>
 </logic:iterate>
</table>
<table width="700" border="0" cellspacing="0"  align="center" cellpadding="0" style="margin-top:5px;">
 <tr>
 <td align="center">
 <%if(!"bi".equals(url)) {%>
 <logic:notEqual value="-1" name="welcomeForm" property="homePageHotId">
 <input type="button" name="dd" value="<bean:message key="button.return"/>" onclick="reutrnBack();" class="mybutton"/> 
 </logic:notEqual>
 <%} %>
 </td>
 </tr>
</table>
 </html:form>
