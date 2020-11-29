<%@page import="java.net.URLEncoder"%>
<%@page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.general.muster.MusterForm"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.hjsj.utils.Sql_switcher"%>
<%@ page import="com.hrms.hjsj.sys.ConstantParamter"%>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    
    String userName = null;
    String css_url = "/css/css1.css";
    UserView userView = (UserView) session
            .getAttribute(WebConstant.userView);
    String fields = "";
    String tables = "";
    String a_code="UN";
    
    if (userView != null) {
        css_url = userView.getCssurl();
        if (css_url == null || css_url.equals(""))
            css_url = "/css/css1.css";
        userName=userView.getUserName();
    }
    String superUser = "0";
    if(userView!=null){
        if (userView.isSuper_admin()){
            superUser = "1";
        }else {
            a_code=userView.getManagePrivCode()+userView.getManagePrivCodeValue();
            fields = userView.getFieldpriv().toString();
            tables = userView.getEmp_tablepriv().toString();
        }
    }
   String bosflag=userView.getBosflag();
%>
<%
	MusterForm myform = (MusterForm)session.getAttribute("musterForm");
	int currentPage=0,i=0;
	if(myform.getPagination()!=null)
	{
		currentPage = myform.getPagination().getCurrent();
		i=(currentPage-1)*20;
	}
%>

<script type="text/javascript">
<!--
function returnFirstPage(returnvalue)
{
  if(returnvalue=='2')
  {
  	<%if("hcm".equalsIgnoreCase(bosflag)){%>
		window.location.href="/templates/index/hcm_portal.do?b_query=link";
	<%}else{%>
   		window.location.href="/templates/index/portal.do?b_query=link";
	<%}%>
  }
}
//-->
</script>
<hrms:themes />
<style>
<!--
.fixedDiv4 
{ 
	overflow:visible; 
	height:expression(document.body.clientHeight-160);
	width:60%;
	BORDER-BOTTOM:  0pt solid; 
    BORDER-LEFT:  1pt solid; 
    BORDER-RIGHT: 1pt solid; 
    BORDER-TOP: 1pt solid ; 
}
-->
.TableRow_left{
    BORDER-TOP: 0pt solid ; 
}
</style>
<html:form action="/general/muster/emp_muster">
<!-- 【7453】主页上在花名册那点击more，然后点击下页，界面有问题  jingq upd 2015.02.11 -->
<table width="50%" style="border:1px solid;" cellpadding="0" cellspacing="0" align="center" class="ListTable common_border_color">
<tr><td align="center" style="border-bottom:1px solid;" valign="top" class="fixedDiv4 common_border_color">
      <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" >
           <tr>
            <td align="center" class="TableRow_top" nowrap width="40">
		     	<bean:message key="kjg.gather.xuhao"/>          	
	    	</td>      
            <td align="center" class="TableRow_left" nowrap>
		     	<bean:message key="muster.label.label"/><bean:message key="column.name"/>          	
	    	</td>    	    	    		        	        	        
           </tr>
           <hrms:extenditerate id="element" name="musterForm" property="tablistForm.list" indexes="indexes"  pagination="tablistForm.pagination" pageCount="20" scope="session">
      	 	
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
          <td align="center" class="RecordRow_right" nowrap>          
          	<%=++i%>
          </td>
          <td align="left" class="RecordRow_left" nowrap>          
        
          	 <logic:equal name="element" property="type" value="1">
          	 	<%
          	 		LazyDynaBean ob = (LazyDynaBean)pageContext.getAttribute("element");
	          	 	String url = "/general/muster/hmuster/select_muster_name.do?b_view=link`isGetData=1`returnType=1`modelFlag="+ob.get("flag")+"`res=1`clears=1`operateMethod=direct`tabID="+ob.get("tabid");
	          	 	url = URLEncoder.encode(url, "GBK");
          	 	%>
          	 	<a href="/general/muster/hmuster/processBar.jsp?url=<%=url %>"><bean:write  name="element" property="name" filter="true"/></a>
          	 	<!-- 
          	 	<a href="/general/muster/hmuster/processBar.jsp?url=/general/muster/hmuster/select_muster_name.do?b_view=link`isGetData=1`returnType=1`modelFlag=<bean:write name="element" property="flag"/>`res=1`clears=1`operateMethod=direct`tabID=<bean:write  name="element" property="tabid"/>"><bean:write  name="element" property="name" filter="true"/></a>
          	 	 -->
          	 </logic:equal>
          	 <logic:equal name="element" property="type" value="0">
          	 	<%
          	 	LazyDynaBean ob = (LazyDynaBean)pageContext.getAttribute("element");
          	 	String url="/general/muster/open_musterdata.do?b_open=link`condid=`tabid="+ob.get("tabid")+"`infor_Flag="+ob.get("flag")+"`res=1`isImportData=1";
          	 	url = URLEncoder.encode(url, "GBK");
          	 	%>
          	 	<%-- <a href="/general/muster/hmuster/processBar.jsp?url=<%=url%>"><bean:write  name="element" property="name" filter="true"/></a> --%>
                <a href="/module/muster/showmuster/ShowMuster.html?musterType=<bean:write  name="element" property="flag"/>&moduleID=<bean:write  name="element" property="moduleID"/>&tabid=<bean:write  name="element" property="tabid"/>&source=homepage2"><bean:write  name="element" property="name" filter="true"/></a>

          	 </logic:equal>
          	   <logic:equal value="1" name="element" property="hasQuery"><img src='/images/overview_n_obj.gif' border="0"/></logic:equal>
          </td>            	        
          </tr>
       </hrms:extenditerate>
</table>
</td></tr>
<tr><td align="center">
<table style="width:100%;" border="0" cellpadding="0" cellspacing="0" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor" nowrap>
		            <bean:message key="label.page.serial"/>
		   <bean:write name="musterForm" property="tablistForm.pagination.current" filter="true"/>
					<bean:message key="label.page.sum"/>
		   <bean:write name="musterForm" property="tablistForm.pagination.count" filter="true"/>
					<bean:message key="label.page.row"/>
		   <bean:write name="musterForm" property="tablistForm.pagination.pages" filter="true"/>
					<bean:message key="label.page.page"/>
		   </td>
		   <td align="right" class="tdFontcolor" nowrap>
		   <p align="right">
		   <hrms:paginationlink name="musterForm" property="tablistForm.pagination" nameId="tablistForm" propertyId="tablistProperty">
		   </hrms:paginationlink>
		   </td>
		</tr>
</table>
</td></tr>
</table>
<%if(!"bi".equals(bosflag)){%>
<logic:equal value="2" name="musterForm" property="returnvalue">
<table style="width:60%"  align="center">
<tr><td align="center">
	<input type="button" value="<bean:message key='button.return'/>" class="mybutton" onclick='returnFirstPage("${musterForm.returnvalue}");'>
</td>
</tr>
</table>
</logic:equal>
<%} %>
</html:form>