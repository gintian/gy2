<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.general.statics.StaticFieldForm" %>
<%
	StaticFieldForm statForm=(StaticFieldForm)session.getAttribute("staticFieldForm");
	String chart_type=statForm.getChart_type();
%>
 <SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>
 <script type="text/javascript" src="/js/hjsjUrlEncode.js"></script>
 <SCRIPT LANGUAGE=javascript>
 function testchart(e)
 {
     var name=e.name;    
     if(name!='')
     {
      	 name = $URL.encode(name);
      	 staticFieldForm.action="/general/static/static_data.do?b_data=data&&showLegend="+name+"&stat_type=simple&result=${staticFieldForm.result}&history=${staticFieldForm.history}";
      	 staticFieldForm.submit();
     }
}
</SCRIPT>
   <br> 
   <hrms:themes />
  <html:form action="/general/static/static_result">
<table  align="center">
<tr align="left">  
 <%--    <td valign="top"  nowrap>
      &nbsp;&nbsp;<a href="/general/static/static_result.do?chart_type=12&result=${staticFieldForm.result}&history=${staticFieldForm.history}">立体直方图</a>
    </td> --%>
    <td valign="top"  nowrap>
      &nbsp;&nbsp;<a href="/general/static/static_result.do?chart_type=11&result=${staticFieldForm.result}&history=${staticFieldForm.history}">平面直方图</a>
    </td>
<%--     <td valign="top"  nowrap>
      &nbsp;&nbsp;<a href="/general/static/static_result.do?chart_type=5&result=${staticFieldForm.result}&history=${staticFieldForm.history}">立体圆饼图</a>
    </td> --%>
    <td valign="top"  nowrap>
      &nbsp;&nbsp;<a href="/general/static/static_result.do?chart_type=20&result=${staticFieldForm.result}&history=${staticFieldForm.history}">平面圆饼图</a>
    </td>
    <td valign="top"  nowrap>
      &nbsp;&nbsp;<a href="/general/static/static_result.do?chart_type=1000&result=${staticFieldForm.result}&history=${staticFieldForm.history}">平面折线图</a>
    </td>
  </tr>
  </table>
<table align="center" border="0" cellpadding="0" cellspacing="0">
     <html:hidden property="statid"/>
           <tr>
            <td align="center" nowrap>
             <logic:equal  name="staticFieldForm" property="chart_type"  value="1000">
                <hrms:chart name="staticFieldForm" title="${staticFieldForm.snamedisplay}" scope="session" legends="jfreemap" data="" width="1200" height="530" xangle="${staticFieldForm.xangle}" chart_type="1000"  pointClick="testchart">
	    	     </hrms:chart>
             </logic:equal>
             <logic:notEqual  name="staticFieldForm" property="chart_type"  value="1000">
                <hrms:chart name="staticFieldForm" title="${staticFieldForm.snamedisplay}" scope="session" legends="list" data="" width="1200" height="530" xangle="${staticFieldForm.xangle}" chart_type="${staticFieldForm.chart_type}" pointClick="testchart">
	    	     </hrms:chart>
             </logic:notEqual>
            </td>
          </tr>  
</table>
<div style="position:relative; width:50px; margin-top:!important; margin-top:;left:50%;margin-left:-0px; ">
	           <hrms:submit styleClass="mybutton" property="br_back">
                <bean:message key="static.back"/>
	             </hrms:submit>
</div>
</html:form>
<script type="text/javascript">
function refresh(){
	document.getElementById("___CONTAINER___Nchart__0").style.position="absolute";
	document.getElementById("___CONTAINER___Nchart__0").style.left="50%";
	document.getElementById("___CONTAINER___Nchart__0").style.margin="0 0 0 -335px";
	document.getElementById("___CONTAINER___Nchart__0").style.top="60px";
}
if(navigator.appName.indexOf("Microsoft")== -1)
refresh();
</script>
