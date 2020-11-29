<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.lawbase.LawBaseForm" %>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
	int i=0;
%>

<style id=iframeCss>
div{
	cursor:hand;font-size:12px;
   }
a{
text-decoration:none;color:black;font-size:12px;
}

a.a1:active {
	color: #003100;
	text-decoration: none;
}
a.a1:hover {
	color: #FFCC00;
	text-decoration: none;
}
a.a1:visited {	
	text-decoration: none;
}
a.a1:link {
	color: #003100;
	text-decoration: none;
}
.fixedHeaderTr{
 	border:1px solid #C4D8EE;
}
.myleft
{
	border-left: none;
}
.mytop
{
	border-top: none;
}
.myright
{
 	border-right:none; 
}
</style>
<script language="javascript">
</script>
<base target="_self"/>
<html:form action="/selfservice/lawbase/lawtext/law_onetext_role">
<table width="95%" border="0" cellspacing="0"  align="center" cellpadding="0">
<tr><td>
<div class="fixedDiv2" style="height:expression(document.body.clientHeight-100);"> 
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0"> 
		<tr class="fixedHeaderTr" valign="top">           
            <td align="center" class="TableRow myleft mytop" valign="middle" nowrap width="30%"><span style="vertical-align: middle;">
		<bean:message key="column.name"/>&nbsp;</span>
	    </td>
            <td align="center" class="TableRow myleft mytop myright" valign="middle" nowrap width="70%"><span style="vertical-align: middle;">
		<bean:message key="column.desc"/>&nbsp;</span>
	    </td>
	    </tr>
          <hrms:extenditerate id="element" name="lawbaseForm" property="roleListForm.list" indexes="indexes"  pagination="roleListForm.pagination" pageCount="${lawbaseForm.pagerows}" scope="session">
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
          i++;          
          %>                   
            <td align="left" class="RecordRow myleft mytop" nowrap>
                   &nbsp;<bean:write name="element" property="string(role_name)" filter="true"/>&nbsp;
	    </td>
         
            <td align="left" class="RecordRow myleft mytop myright" style="word-break:break-all;">
                   &nbsp; <bean:write  name="element" property="string(role_desc)" filter="false"/>&nbsp;
            </td>
          </tr>
        </hrms:extenditerate>
        
</table>
</div>
<div class="fixedDiv3"> 
<table  width="100%" align="center"  class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		    	<hrms:paginationtag name="lawbaseForm" pagerows="${lawbaseForm.pagerows}" property="roleListForm.pagination" scope="session" refresh="true"></hrms:paginationtag>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="lawbaseForm" property="roleListForm.pagination"
				nameId="roleListForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
</div>
<table  width="70%" align="center">
          <tr>
            <td align="center">	       
              <html:button styleClass="mybutton" property="b_o_close" onclick="window.close();">
            	   	<bean:message key="button.close"/>
  	       </html:button>  	           	          	   	 	  
            </td>
          </tr>          
</table>
</td>
</tr></table>
</html:form>
