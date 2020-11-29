<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.transaction.sys.warn.ColumnBean" %>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page import="com.hrms.frame.utility.AdminCode"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<script type="text/javascript">
function change()
{
    keyLogForm.action="/selfservice/welcome/keylog.do?b_query=link";
    keyLogForm.submit();
}
function viewcontent(logid)
{
var target_url="/selfservice/welcome/welcome.do?b_revert=link&logid="+logid;
     var return_vo= window.showModalDialog(target_url,1, 
        "dialogWidth:500px; dialogHeight:350px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");

}
</script>
<style type="text/css">
.huise
{
filter:Gray;
} 
</style>


<html>
  <head>  
  </head>
  <body>
<html:form action="/selfservice/welcome/keylog">
 <table width="90%" border="0" cellspacing="0" align="center" cellpadding="0">
     <tr height="25">
      <td align="left"  nowrap>
       <table  border="0" cellspacing="0"  cellpadding="0">
        <tr>
        <td align="left"  nowrap>
           
            
     	     <bean:message key="label.query.dbpre"/>
    	       <hrms:importgeneraldata showColumn="dbname" valueColumn="pre" flag="false" paraValue="" 
                 sql="keyLogForm.dbcond" collection="list" scope="page"/>
              <html:select name="keyLogForm" property="userbase" size="1" onchange="javascript:change()">
                      <html:options collection="list" property="dataValue" labelProperty="dataName"/>
              </html:select>&nbsp;
              姓名
               <input type="text" name="select_name" value="${keyLogForm.select_name }" style="width:100px;font-size:10pt;text-align:left" >
	       	<input type="button" name="addbutton"  value="<bean:message key='lable.law_base_file_search.query'/>" class="mybutton" onclick='change();' >  	
	         <input type="button" id="to" onclick="top.close();" class="mybutton" value="<bean:message key="button.close"/>"></input>
          </td>
          </tr>
       </table>
      </td>
     </tr>
     <tr>
      <td nowrap style="padding-top:3px;">
          <table  width="100%"  border="0" cellspacing="0"  cellpadding="0" class="ListTable">
            <tr>
               <td align="center" class="TableRow" nowrap>
				<bean:message key="label.title.org" />
				&nbsp;
			   </td>
			   <td align="center" class="TableRow" nowrap>
				<bean:message key="label.title.dept" />
				&nbsp;
			   </td>
			   <td align="center" class="TableRow" nowrap>
				<bean:message key="label.title.name" />
				&nbsp;
			   </td>
			   <td align="center" class="TableRow" nowrap>
				<bean:message key="lable.visit.ip" />
				&nbsp;
			   </td>
			   <td align="center" class="TableRow" nowrap>
				<bean:message key="lable.visit.tiem" />
				&nbsp;
			   </td>
			   <td align="center" class="TableRow" nowrap>
				<bean:message key="lable.revert.content" />
				&nbsp;
			   </td>
			</tr>
			<%int i=0; %>
			<hrms:paginationdb id="element" name="keyLogForm" sql_str="keyLogForm.sql" table="" where_str="keyLogForm.where" columns="keyLogForm.cloumn" page_id="pagination" pagerows="15" indexes="indexes">
			<bean:define id="logid" name="element" property="logid"/>
			<%if (i % 2 == 0) {%>
			<tr class="trShallow">
				<%} else {%>			
			<tr class="trDeep">
				<%}i++;%>	
				<td align="left" class="RecordRow" nowrap>
					<!--bean:write  name="element" property="o1name" filter="true"/-->
					&nbsp;<hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page" />
					<bean:write name="codeitem" property="codename" />
					&nbsp;
				</td>
				<td align="left" class="RecordRow" nowrap>
					<!--bean:write name="element" property="o2name" filter="true"/-->
					&nbsp;<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" scope="page" />
					<bean:write name="codeitem" property="codename" />					
					&nbsp;
				</td>
				<td align="left" class="RecordRow">
				   &nbsp;<bean:write name="element" property="a0101" filter="true" />&nbsp;
				</td>	
				<td align="left" class="RecordRow">
				   &nbsp;<bean:write name="element" property="address" filter="true" />&nbsp;
				</td>
				<td align="left" class="RecordRow">
				   &nbsp;<bean:write name="element" property="access_time" filter="true" />&nbsp;
				</td>	
				<td align="center" class="RecordRow">
				 <logic:empty name="element" property="opinion">
				   <div class="huise">
				    <img src="/images/view_huise.gif" border="0"  style="filter:gray">
				   </div>
				 </logic:empty>
				 <logic:notEmpty name="element" property="opinion">
				   <a href="javascript:viewcontent('${logid}');">
				    <img src="/images/view.gif" border="0">
				   </a>
				 </logic:notEmpty>
				</td>	
			</tr>
			</hrms:paginationdb>
          </table>
      </td>
     </tr>
     <tr>
       <td>
          <table width="100%" align="center" class="RecordRowP">
		   <tr>
			<td valign="bottom" class="tdFontcolor">
				<bean:message key="label.page.serial" />
				<bean:write name="pagination" property="current" filter="true" />
				<bean:message key="label.page.sum" />
				<bean:write name="pagination" property="count" filter="true" />
				<bean:message key="label.page.row" />
				<bean:write name="pagination" property="pages" filter="true" />
				<bean:message key="label.page.page" />
			</td>
			<td align="right" nowrap class="tdFontcolor">
				<p align="right">
					<hrms:paginationdblink name="keyLogForm" property="pagination" nameId="keyLogForm" scope="page">
					</hrms:paginationdblink>
			</td>
		  </tr>
	     </table>
       </td>
     </tr>
  </table>
</html:form>
  </body>
</html>
