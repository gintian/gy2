<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
%>
<script language="javascript">
	function refurbish(){
		if(trim(searchReportUnitForm.start_date.value).length==0||trim(searchReportUnitForm.end_date.value).length==0)
		{
			alert("需填写有效日期!");
			return;
		}
	    if(trim(searchReportUnitForm.unitName.value).length==0)//liuy 2015-2-10 7529：填报单位维护:修改填报单位名称为空，报空指针
		{
			alert("需填写代码名称!");
			return;
		}
	    searchReportUnitForm.action="/report/org_maintenance/reportunitupdate.do?b_update=link";
		searchReportUnitForm.submit();
		parent.mil_menu.document.location = "reportunittree.jsp";  
	}
	function back(){
 		var parentid = document.getElementById("parentCode").value;
		window.location.target="_blank";
		window.location.href = "/report/org_maintenance/reportunitlist.do?b_query=link";	
	}
</script>
<script type="text/javascript" src="/js/validateDate.js"></script>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<hrms:themes/>
<html:form action="/report/org_maintenance/reportunitupdate" > 
<table width="500" border="0" cellpadding="0" cellspacing="0" align="center">
   <tr height="20">
       		<!-- td width=10 valign="top" class="tableft"></td>
       		<td width=150 align=center class="tabcenter">&nbsp;<bean:message key="updateunitinfo.title"/>&nbsp;</td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="500"></td -->   
       		<td align="left" colspan="4" class="TableRow"><bean:message key="updateunitinfo.title"/>&nbsp;</td>             	      
  </tr>  
   <tr>
      <td colspan="4" class="framestyle3" width="100%" align="center">
           <br>
           <table width="100%" border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0" align="center"> 
             <tr  align="right" height="30" class="list3">
               <td>
                  <bean:message key="updateunitinfo.unitcode"/>&nbsp;
                </td>
               <td align="left">
                 <html:text   name="searchReportUnitForm" property="unitCode"  readonly="true" styleClass="textColorRead" />
                 <html:hidden   name="searchReportUnitForm" property="parentCode" styleId="parentCode"  />
               </td>
             </tr> 
             <tr  align="right" height="30" class="list3">
                <td>
                   <bean:message key="updateunitinfo.unitname"/>&nbsp;
                </td>
               <td align="left">	
                  <html:text   name="searchReportUnitForm" property="unitName"  styleClass="textColorWrite" maxlength="50"/>
               </td>
             </tr> 
             <tr align="right" height="30" class="list3">
						<td>
							<bean:message key="conlumn.codeitemid.start_date"/>&nbsp;
						</td>
						<td align="left">
						 <input type="text" name="start_date" value="${searchReportUnitForm.start_date}" maxlength="50" style="width:200px" extra="editor" dropDown="dropDownDate" onchange="if(!validate(this,'有效日期起')) {this.focus(); this.value='${searchReportUnitForm.start_date}'; }"/>
						</td>
			 </tr>
					
			 <tr align="right" height="30" class="list3">
						<td>
							<bean:message key="conlumn.codeitemid.end_date"/>&nbsp;
						</td>
						<td align="left">
							<input type="text" name="end_date" value="${searchReportUnitForm.end_date}" maxlength="50" style="width:200px" extra="editor" dropDown="dropDownDate" onchange="if(!validate(this,'有效日期止')) {this.focus(); this.value='9999-12-31'; }"/>
						</td>
			 </tr>
					
          </table>
          <br>
       </td>
   </tr>   
  </table>
  
   <table align="center"  >
          	<tr  align="center" class="list3">
                <td colspan="2">
					 <input type="button"  class="mybutton" name="b_update" onClick="refurbish()" value="<bean:message key='updateunitinfo.save'/>">
					 <input type="button" name="b_return"  value="<bean:message key='reporttypelist.cancel'/>" class="mybutton" onClick="back()" >  
					
				</td>
            </tr>
          </table>
</html:form>
