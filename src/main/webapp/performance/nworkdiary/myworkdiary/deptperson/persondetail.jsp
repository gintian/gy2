 <%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.performance.nworkdiary.myworkdiary.deptperson.DeptPersonForm,com.hjsj.hrms.utils.PubFunc"%>
<html>
  <head>
  <%
   DeptPersonForm deptPersonForm=(DeptPersonForm)session.getAttribute("deptPersonForm");
   String a0100=deptPersonForm.getA0100();
  String desa0100=PubFunc.encryption(a0100);
   
  %>
<link href="/css/deptperson.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="/js/popcalendar3.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<script language="JavaScript" src="/performance/nworkdiary/deptperson.js"></script>
<script type="text/javascript">
function forwardTO(flag,a0100,nbase){
	var url="";
	if(flag=='1'){
		url="/templates/attestation/gw/showotheros.jsp?type=1&userInfo="+nbase+a0100+"&checkType=1";
	}else if(flag=='2'){
		url="/templates/attestation/gw/showotheros.jsp?type=2&userInfo="+nbase+a0100+"&checkType=1";
	}else if(flag=='3'){
		
		url="/performance/nworkdiary/staffdiary/staff_diary.do?b_search=link&fromFlag=0&a0100="+a0100+"&nbase="+nbase;
		
	}else if(flag=='5'){
		
		url="/performance/kh_result/kh_plan_list.do?b_init=link&opt=1&model=2&isClose=0&distinctionFlag=0&a0100="+a0100;

	}
	
	document.location=url;
	
}
</script>
  </head>

<body background="#fff">
<html:form action="/performance/nworkdiary/myworkdiary/deptperson"> 
<div id="epm-index-all">
        <div class="epm-other-xiang">
        	<div class="epm-o-all">
            	<div class="epm-o-all-left">
                	<dl>
                    	<dt>
                    	<hrms:ole name="deptPersonForm" dbpre="deptPersonForm.nbase" href="" width="120" height="150" a0100="a0100" scope="session" div="XX"/>
                    	</dt>
                        <logic:iterate id="deptPersonDetail" name="deptPersonForm" property="list" indexId="index">
							<dd>
							<bean:write name="deptPersonDetail"/>
							</dd>
						</logic:iterate>
                    </dl>
                </div>
                <div class="epm-o-all-right">
                	<table border="1">
                      <tr>
                        <td><input type="button" value="基本情况" class="epm-jbqk" onclick='forwardTO("1","${deptPersonForm.a0100}","${deptPersonForm.nbase}")'/></td>
                        <td><input type="button" value="岗位职责" class="epm-jbqk" /></td>
                        <td><input type="button" value="工作流程" class="epm-jbqk" onclick='forwardTO("2","${deptPersonForm.a0100}","${deptPersonForm.nbase}")' /></td>
                      </tr>
                      <tr>
                        <td><input type="button" value="工作日志" class="epm-jbqk" onclick='forwardTO("3","${deptPersonForm.a0100}","${deptPersonForm.nbase}")' /></td>
                        <td colspan="2"><input type="button" value="工作计划总结" class="epm-jbqk" /></td>
                      </tr>
                      <tr>
                        <td colspan="3"><input type="button" value="考评结果" class="epm-jbqk"  onclick='forwardTO("5","<%=desa0100%>","${deptPersonForm.nbase}")'/></td>
                      </tr>
                    </table>
                </div>
          </div>
        </div>	
    </div>
</html:form>
</body>
