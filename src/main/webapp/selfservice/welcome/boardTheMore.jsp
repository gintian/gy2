<%@page import="com.hrms.frame.dao.RecordVo"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    /**登录版本不同,界面*/
    String bosflag=userView.getBosflag();
	String isEpmLoginFlag="0";	
	if(userView != null){
	  isEpmLoginFlag=(String)userView.getHm().get("isEpmLoginFlag"); 
	  isEpmLoginFlag = (isEpmLoginFlag==null||isEpmLoginFlag.equals(""))?"0":isEpmLoginFlag;
	}
	String returnvalue = request.getParameter("returnvalue")==null?"":request.getParameter("returnvalue");	 
%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script type="text/javascript">
function goback(){
  var isEpmLoginFlag="<%=isEpmLoginFlag%>";
  var url = '/templates/index/portal.do?b_query=link';
  if(isEpmLoginFlag=='1'){
      url = '/templates/index/subportal.do?b_query=link';
      parent.location.href = url;
  }
  var bosflag = "<%=bosflag %>";
  if(bosflag=='hcm'){
	  url = '/templates/index/hcm_portal.do?b_query=link';
  }
  window.location.href = url;
}
</script>
<style>
.tableStyle{
    
    BORDER-LEFT: #C4D8EE 1pt solid; 
    BORDER-RIGHT: #C4D8EE 1pt solid; 
    BORDER-bottom: #C4D8EE 1pt solid; 
    font-size: 12px;
    height:22px;
    
}
.tdStyle{ background-color:#ffffff; border-bottom:#C4D8EE 1pt solid; border-right:#C4D8EE 1pt solid;}
.pageStyle{
    border-bottom:#C4D8EE 1pt solid; background-color:#ffffff
}
</style>
<html:form action="/selfservice/welcome/boardTheMore">
<center>
  <br>
  <br>
  <table width="80%" border="0" cellpadding="0" cellspacing="0" align="center">
   <tr height="20" >
    <td align="left" colspan="4" class="TableRow">
     <bean:message key="lable.welcomeboard.notice"/></td>
   </tr> 
   <tr>
   <td colspan="4" class="tdStyle" style="border-bottom:none">
    <table width="100%" class="tableStyle" style="border-right:none;"  cellspacing="0" align="center" cellpadding="0">
      <hrms:extenditerate id="element" name="welcomeForm" property="welcomeForm.list"  pagination="welcomeForm.pagination" indexes="indexes"> 
      <tr height="20"> 
        <td class="tdStyle" align="left"  nowrap="nowrap"
     <%if(isEpmLoginFlag.equals("1")){ %>
         class="RecordRowPo2"
         <%} %>
         > 
         &nbsp;<a href="/selfservice/welcome/welcome.do?b_view=link&a_id=<bean:write name="element" property="string(id)" filter="true"/>&annouceflag=<bean:write name="element" property="string(flag)" filter="true"/>&flag=flag" 
            target="_blank">
            <bean:write name="element" property="string(topic)" filter="false"/>
          </a> 
        </td>
        <td class="tdStyle" align="center" nowrap="nowrap">
          <bean:write name="element" property="string(approvetime)" filter="true"/>
        </td>
        <td class="tdStyle" align="center" nowrap="nowrap"> 
          <logic:notEqual name="element" property="string(ext)" value="">
           	<logic:notEqual name="element" property="string(ext)" value="null">
            	<a href='downboard?id=<%=PubFunc.encrypt(((RecordVo)element).getString("id")) %>&topic=announceFile&ext=<bean:write name="element" property="string(ext)" filter="true"/>'>
            	<bean:message key="lable.welcomeboard.accessoriesdownload"/>
            	</a>
            		</logic:notEqual>
            	</logic:notEqual>&nbsp;
        </td>
        <td class="tdStyle" nowrap="nowrap" align="center">
          <bean:message key="label.sys.count"/><bean:write name="element" property="string(viewcount)" filter="true"/>
        </td>
      </tr>
      </hrms:extenditerate>       
    </table>   
     </td>
   </tr>
   <!-- 分页信息 -->
    <tr>
        <td class="pageStyle" nowrap="nowrap">
        <table  width="100%" align="center" class="tableStyle">
        <tr>
            <td valign="bottom" class="tdFontcolor">
                <bean:message key="label.page.serial" />
                    <bean:write name="welcomeForm" property="welcomeForm.pagination.current" filter="true" />
                <bean:message key="label.page.sum" />
                    <bean:write name="welcomeForm" property="welcomeForm.pagination.count" filter="true" />
                <bean:message key="label.page.row" />
                    <bean:write name="welcomeForm" property="welcomeForm.pagination.pages" filter="true" />
                <bean:message key="label.page.page" />
            </td>
                   <td  align="right" nowrap class="tdFontcolor">
                  <p align="right"><hrms:paginationlink name="welcomeForm" property="welcomeForm.pagination"
                nameId="welcomeForm" propertyId="list">
                </hrms:paginationlink>
            </td>
        </tr>
		</table>   
        </td>
    </tr>
    
  </table>
 
   <table>
     <tr class="list3">
       <td align="left" colspan="2">
        <br>
        <% 
		if(userView!=null && userView.getBosflag()!=null && returnvalue.equals("dxt"))
		{
		%>
	        <!-- 自助服务导航图返回 -->
	        <input type="button" name="b_return" value="<bean:message key="button.return"/>" class=mybutton  onclick="hrbreturn('selfinfo','il_body','welcomeForm')">
		<%}else{ %>
	        <%if(isEpmLoginFlag.equals("1")) {%>
	            <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="goback()"> 
	        <%}else{ %>
	        <%if(bosflag.equalsIgnoreCase("hl")){%>
	            <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="goback()"> 
	         	<%}else if(bosflag.equalsIgnoreCase("hcm")){%>
	            <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="goback()"> 
	            <%}else{%>  
	         	<input type="button" class="mybutton" value="<bean:message key="lable.welcomeboard.close"/>" onclick="var url ='/templates/cclose.jsp';newwin=window.open(url,'_parent','toolbar=no,location=no,directories=0,status=no,menubar=no,scrollbars=no,resizable=no','true');" /> 
		 	<%}} %>          
		<%} %>
       </td>
     </tr> 
 </table>  	 
</center>     
               
   
   
</html:form>


