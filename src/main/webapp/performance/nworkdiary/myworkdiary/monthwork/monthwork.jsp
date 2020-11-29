 <%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.ArrayList,
				 com.hjsj.hrms.actionform.performance.nworkdiary.myworkdiary.monthwork.MonthWorkForm,
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant"%>
<% 
    String a0100="";	
    String nbase="";	
    String isOwner="";	
    String initflag="";	
    String backUrl="";	
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
		a0100 = (String)userView.getHm().get("a0100"); 
		nbase = (String)userView.getHm().get("nbase"); 
		isOwner = (String)userView.getHm().get("isOwner"); 
		initflag = (String)userView.getHm().get("initflag")==null?"":(String)userView.getHm().get("initflag");  
		backUrl = (String)userView.getHm().get("backUrl"); 
	}%>
<link href="/css/diary.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="/js/popcalendar3.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<script language="JavaScript" src="/performance/nworkdiary/diary.js"></script>

<body scroll="no">
<html:form action="/performance/nworkdiary/myworkdiary/monthwork">
   <html:hidden property="currentYear" name="monthWorkForm" styleId="fromyear"/>
   <html:hidden property="currentMonth" name="monthWorkForm" styleId="frommonth"/>
   <html:hidden property="currentDay" name="monthWorkForm" styleId="fromday"/>
   <input type="hidden" id="a0100" value=<%=a0100 %>/>   
   <input type="hidden" id="nbase" value=<%=nbase %>/> 
   <input type="hidden" id="p0100">
   <input type="hidden" id="record_num">
   
   	
        <div class="epm-j-all">
    	<div class="epm-j-h2">
    	    <span class="zreed" style="float:left;">${monthWorkForm.currentTime}&nbsp;&nbsp; </span>
        	<div class="epm-jintian">
            	<table border="1">
                  <tr>
                    <td>
	                    <%if(isOwner.equals("0")){ %>
	                      <input type="button" value="新增" class="epm-j-xinz" onclick="showQueryDiv('add');"/>&nbsp;&nbsp;
	                    <%} %>
	                    <input type="button" value="查询" class="epm-j-xinz" onclick="showSearchDiv('2','query');"/>&nbsp;&nbsp;
	                    <input type="button" value="导出" class="epm-j-xinz" onclick="exportDiary('3','${monthWorkForm.currentYear}','${monthWorkForm.currentMonth}','','${monthWorkForm.currentYear}','${monthWorkForm.currentMonth}','');">&nbsp;&nbsp;
				        <%if(initflag.equals("4")||initflag.equals("2")){ %>
		                 <input type="button" value="返回" class="epm-j-xinz" onclick="goback()">&nbsp;&nbsp;
		                <%}else{ %> 
				        <logic:equal value="2" name="monthWorkForm" property="init">
				          <input type="button" value="返回" class="epm-j-xinz" onclick="goback()">&nbsp;&nbsp;
				        </logic:equal>
				        <%} %>
			        </td>
                    <td><input style="cursor:hand;" type="button" class="epm-zuo" onclick="changeDate('previous');"/></td>
                    <td><a href="###" onclick="changeDate('today');">今天</a></td>
                    <td><input style="cursor:hand;" type="button" class="epm-you" onclick="changeDate('next');"/></td>
                  </tr>
                </table>
            </div>
        </div>
        <div class="epm-jh-all-gundong">
         ${monthWorkForm.tableHtml}
        </div>
      </div>
        
</html:form>
<jsp:include page="/performance/nworkdiary/myworkdiary/public/publicdiv.jsp"></jsp:include>
<script type="text/javascript">
    var thisDate = ${monthWorkForm.currentYear}+"-"+ ${monthWorkForm.currentMonth}+"-"+ ${monthWorkForm.currentDay};
    var nodeId=document.getElementById(thisDate);
function changeDate(flag){
      var hashvo = new ParameterSet();
	  hashvo.setValue("flag",flag);
	  hashvo.setValue("thisDate",thisDate);
	  var request=new Request({method:'post',asynchronous:false,onSuccess:getAfterDate,functionId:'302001020602'},hashvo);
    }
function getAfterDate(outparamters){
      //0:切到上月 1：还是本月 2：切到下月
      var afterDate = outparamters.getValue("afterDate");
      var flag = outparamters.getValue("flag");
      var year = afterDate.split("-")[0];
      var month = afterDate.split("-")[1];
      var day = afterDate.split("-")[2];
      document.monthWorkForm.currentYear.value=year;
      document.monthWorkForm.currentMonth.value=month;
      document.monthWorkForm.currentDay.value=day;
      var init = "${monthWorkForm.init}";
      document.monthWorkForm.action="/performance/nworkdiary/myworkdiary/monthwork.do?b_search=link&init=1";
      if(init=='2'){
         document.monthWorkForm.action+="&fromYearFlag=1";
      }
      document.monthWorkForm.submit();
    }
function goback(){
  var initflag = "<%=initflag %>";
  var backUrl = "<%=backUrl %>";
 if(initflag=="4"){
        window.location=backUrl;
 }else{ 
  window.location.href='${monthWorkForm.returnUrl}';
  }
}
</script>
</body>
