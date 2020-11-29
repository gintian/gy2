<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.ArrayList,
				 com.hjsj.hrms.actionform.performance.nworkdiary.myworkdiary.yearwork.YearWorkForm,
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
<html:form action="/performance/nworkdiary/myworkdiary/yearwork">
   <html:hidden property="currentYear" name="yearWorkForm"/>
   <input type="hidden" id="a0100" value=<%=a0100 %>/>   
   <input type="hidden" id="nbase" value=<%=nbase %>/>   
   <table width="96%" border="0" align="center" cellpadding="0" cellspacing="0" class="epm-all">
	  
	  
	  
	  
	  
	  <tr>
	    <td width="100%" height="30">
	      <table width="99%" height="20" border="0" cellpadding="0" cellspacing="0">
		      <tr>
		        <td width="75%" height="20" style="padding-left:10px;"><span class="zreed">${yearWorkForm.currentTime}&nbsp;&nbsp; </span></td>
		        <td width="15%" align="right" nowrap>
		        <%if(isOwner.equals("0")){ %>
		            <input type="button" value="新增" class="epm-bianji" onclick="showQueryDiv('add');">&nbsp;&nbsp;
		        <%} %>
		        <input type="button" value="导出" class="epm-bianji" onclick="exportDiary('4','${yearWorkForm.currentYear}','','','${yearWorkForm.currentYear}','','');">&nbsp;&nbsp;
		        <%if(isOwner.equals("1")){ %>
		            <input type="button" value="返回" class="epm-bianji" onclick="goback()">&nbsp;&nbsp;
		         <%}else{ %>   
				<%} %>
		        </td>
		        <td width="8%" align="left" valign="middle">
		        	<table width="100%" border="0" cellpadding="0" cellspacing="0" class="b5">
		              <tr>
		                <td width="18%" align="left" valign="middle"><a href="#" onclick="changeYear('previous');"><img  src="/images/epm_w.gif" width="18" height="17" /></a></td>
		                <td width="64%" align="center" valign="middle" class="z1"><a href="###" onclick="changeYear('thisyear');">今天</a></td>
		                <td width="18%" align="center" valign="middle" ><a href="#"  onclick="changeYear('next');"><img  src="/images/epm_w1.gif" width="18" height="17" /></a></td>
		              </tr>
		        	</table>
		        </td>
		      </tr>
	         </table>
    
    </td>
   </tr>
   
   </table>
   
   <div class="epm-jh-all-gundong1">
   
   <div class="epm-jh-nian-from">
   
   		${yearWorkForm.tableHtml}
  
   </div>
   
   
   
   
   
   
  <div>
       <table style="margin-left:10px;width:60%">
         <tr>
           <td style="width:30px;height:20px;background:#FFF06D;" >
           </td>
           <td colspan="2">
           <strong>1个工作记录</strong>
           </td>
           <td style="width:30px;background:#FFCC00;">
           </td>
           <td>
           <strong>2-4个工作记录</strong>
           </td>
           <td style="width:30px;height:10px;background:#FEB198;">
           </td>
           <td >
           <strong>5-7个工作记录</strong>
           </td>
           <td style="width:30px;height:10px;background:#FE865F;">
           </td>
           <td >
           <strong>8个以上工作记录</strong>
           </td>
           <td style="width:30px;height:10px;background:#66A7E9;">
           </td>
           <td >
           <strong>今天</strong>
           </td>
         </tr>
       </table>
    </div>
   
   </div>

   
   
   
</html:form>
<jsp:include page="/performance/nworkdiary/myworkdiary/public/publicdiv.jsp"></jsp:include>
<script type="text/javascript">
    var thisDate = ${yearWorkForm.currentYear};
    function changeYear(flag){
      var hashvo = new ParameterSet();
	  hashvo.setValue("flag",flag);
	  hashvo.setValue("thisDate",thisDate);
	  var request=new Request({method:'post',asynchronous:false,onSuccess:getAfterYearDate,functionId:'302001020608'},hashvo);
    }
    function getAfterYearDate(outparamters){
      
      var afterDate = outparamters.getValue("afterDate");
      var flag = outparamters.getValue("flag");
      var year = afterDate;
      document.yearWorkForm.currentYear.value=year;
      var init = "${yearWorkForm.init}";
      document.yearWorkForm.action="/performance/nworkdiary/myworkdiary/yearwork.do?b_search=link&init=1";
      if(init=='2'){
         document.yearWorkForm.action+="&fromYearFlag=1";
      }
      document.yearWorkForm.submit();
    }
    function gotoMonthWork(currentYear,currentMonth,currentDay){
      window.location.href="/performance/nworkdiary/myworkdiary/monthwork.do?b_search=link&init=2&currentYear="+currentYear+"&currentMonth="+currentMonth+"&currentDay="+currentDay;
    } 
    function goback(){
      var initflag = "<%=initflag %>";
      var backUrl = "<%=backUrl %>";
	  window.location.href=backUrl;
    }
</script>
</body>