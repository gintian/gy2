<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
	}
%>
<script LANGUAGE=javascript>
function winhref(a0100,target)
{
   if(a0100=="")
      return false;
  // makeupAnalyseForm.action="/general/deci/statics/employmakeupanalysebrowse.do?b_search=link&dbpre=${makeupAnalyseForm.dbpre}&a0100="+a0100+"&flag=notself&returnphoto=1";
   makeupAnalyseForm.action="/workbench/browse/showselfinfo.do?b_search=link&userbase=${makeupAnalyseForm.dbpre}&a0100="+a0100+"&flag=notself&returnvalue=returnphoto";
   makeupAnalyseForm.target=target;
   makeupAnalyseForm.submit();
}
</script> 
<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<hrms:themes></hrms:themes>
<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<html:form action="/general/deci/statics/employmakeupanalyse">
<%int i=0;%>
 <table width="80%" border="0" cellspacing="1"  align="center" cellpadding="1">
          <hrms:paginationdb id="element" name="makeupAnalyseForm" sql_str="makeupAnalyseForm.strsql" table="" where_str="makeupAnalyseForm.cond_str" columns="A0100,B0110,E0122,E01A1,A0101,UserName,"  order_by="makeupAnalyseForm.order_by" pagerows="21" page_id="pagination">
          <%
          if(i%7==0)
          {
          %>
          <tr>
           <%
          }
             	LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
             	String a0100=(String)abean.get("a0100");
                request.setAttribute("name",a0100);       	                           
          %>             
          <td align="center" NOWRAP>
          	<a href="/workbench/browse/showselfinfo.do?b_search=link&userbase=${makeupAnalyseForm.dbpre}&a0100=<bean:write name="element" property="a0100" filter="true"/>&flag=notself&returnvalue=returnphoto" target="nil_body">
          	<hrms:ole name="element" dbpre="makeupAnalyseForm.dbpre" a0100="a0100" scope="page" height="120" width="85" href="/workbench/browse/showselfinfo.do?b_search=link&userbase=${makeupAnalyseForm.dbpre}&a0100=${name}&flag=notself&returnvalue=returnphoto"  target="nil_body"/></a>&nbsp;
          	<br>
          	<a href="/workbench/browse/showselfinfo.do?b_search=link&userbase=${makeupAnalyseForm.dbpre}&a0100=<bean:write name="element" property="a0100" filter="true"/>&flag=notself&returnvalue=returnphoto" target="nil_body">
          	<bean:write name="element" property="a0101" filter="true"/></a>&nbsp;
          	<br>
          	<a href="/workbench/browse/showselfinfo.do?b_search=link&userbase=${makeupAnalyseForm.dbpre}&a0100=<bean:write name="element" property="a0100" filter="true"/>&flag=notself&returnvalue=returnphoto" target="nil_body">
          	<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" scope="page"/>  	      
          	<bean:write name="codeitem" property="codename" /> </a>&nbsp; 
          	<br>
          	<a href="/workbench/browse/showselfinfo.do?b_search=link&userbase=${makeupAnalyseForm.dbpre}&a0100=<bean:write name="element" property="a0100" filter="true"/>&flag=notself&returnvalue=returnphoto" target="nil_body">
          	<hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>  	      
          	<bean:write name="codeitem" property="codename" /></a>&nbsp;
          </td> 
          <%
          if((i+1)%7==0)
          {%>
          </tr>
          <%
          }
          i++;          
          %>         
        </hrms:paginationdb>
        
</table>
<table  width="70%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor">第
					<bean:write name="pagination" property="current" filter="true" />
					页
					共
					<bean:write name="pagination" property="count" filter="true" />
					条
					共
					<bean:write name="pagination" property="pages" filter="true" />
					页
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="makeupAnalyseForm" property="pagination" nameId="makeupAnalyseForm" scope="page">
				</hrms:paginationdblink>
		       </td>
		</tr>
</table>
<table  width="70%" align="center">
          <tr>
            <td align="left">       
         	    <hrms:submit styleClass="mybutton" property="br_returnphoto">
            		<bean:message key="button.return"/>
	 	    </hrms:submit>
            </td>
          </tr>          
</table>
</html:form>
