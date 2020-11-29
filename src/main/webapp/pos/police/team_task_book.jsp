<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>
<%int i=0;%>
<script language="javascript">
function send(id)
{
	policeForm.action = "/pos/police/jqdt.do?b_up=link&a0100="+id;
	policeForm.submit();
}
function openword(id)
{
	var	iframe_url = "/pos/roleinfo/pos_dept_post.do?b_open=link&codesetid="+id;
	var return_vo= window.open(iframe_url, 'newwindow', 
        "height=300, width=500, top=0,left=0, toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no");
}
function IsDigit() 
{ 
	return ((event.keyCode != 96)); 
}
function querry() {
	policeForm.action = "/pos/police/team.do?b_search=link&tofirst=yes";
	policeForm.submit();	
}
</script>

<html:form action="/pos/police/team">
<br>
<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0">
  <tr>
    <td>
      <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
<thead>
	<tr class="" width="100%">
		<td  align="left" nowrap colspan="6">
		人员库：
		<html:select name="policeForm" size="1" property="userbase"  style="width:120px;" onchange="querry()">
			<html:optionsCollection property="userbaselist" value="dataValue" label="dataName"/>
		</html:select>&nbsp;&nbsp;
		<logic:equal value="0" name="policeForm" property="cycle">
			<html:select name="policeForm" size="1" property="taskyear" onchange="querry()" >
				<html:optionsCollection property="yearlist" value="dataValue" label="dataName"/>
			</html:select>年&nbsp;&nbsp;
		</logic:equal>			
		<logic:equal value="1" name="policeForm" property="cycle">
			<html:select name="policeForm" size="1" property="taskyear" onchange="querry()">
				<html:optionsCollection property="yearlist" value="dataValue" label="dataName"/>
			</html:select>年&nbsp;&nbsp;
			<html:select name="policeForm" size="1" property="taskmonth" onchange="querry()">
				<html:optionsCollection property="monthlist" value="dataValue" label="dataName"/>
			</html:select>月&nbsp;&nbsp;
		</logic:equal>
		<logic:equal value="2" name="policeForm" property="cycle">
			<html:select name="policeForm" size="1" property="taskyear" onchange="querry()">
				<html:optionsCollection property="yearlist" value="dataValue" label="dataName"/>
			</html:select>年&nbsp;&nbsp;
			<html:select name="policeForm" size="1" property="taskweek" onchange="querry()">
				<html:optionsCollection property="weeklist" value="dataValue" label="dataName"/>
			</html:select>&nbsp;&nbsp;
		</logic:equal>
		姓名：
		<html:text name="policeForm" property="username" size="9" maxlength="20" onkeypress="event.returnValue=IsDigit();"/>
		&nbsp;&nbsp;&nbsp;&nbsp;
		<hrms:submit styleClass="mybutton"  property="b_search">
                  <bean:message key="button.query"/>
	   </hrms:submit>
		</td>
	</tr>
</thead>
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
                <bean:message key="tree.unroot.undesc"/>
             </td>
           	 <td align="center" class="TableRow" nowrap>
                <bean:message key="column.sys.dept"/>&nbsp;
             </td>
             <td align="center" class="TableRow" nowrap>
                <bean:message key="tree.kkroot.kkdesc"/>&nbsp;
             </td>
             <td align="center" class="TableRow" nowrap>
                <bean:message key="label.title.name"/>&nbsp;
             </td>
             <td align="center" class="TableRow" nowrap>
                <bean:message key="per.achivement.taskbook"/>&nbsp;
             </td>                  
             <td align="center" class="TableRow" nowrap>
		  <bean:message key="hire.jp.apply.upload"/>            	
             </td>        
           </tr>
   	  </thead>
   	   <hrms:paginationdb id="element" name="policeForm" sql_str="policeForm.sqlstr" table="" where_str="" columns="policeForm.column" order_by="policeForm.order_by" pagerows="21" page_id="pagination" pagerows="${policeForm.pagerows}">
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
           <td align="left" class="RecordRow" nowrap>                
                	&nbsp;<hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page" uplevel="${policeForm.uplevel}"/>         
           			<bean:write name="codeitem" property="codename" />&nbsp;
            </td>
            <td align="left" class="RecordRow" nowrap>                
                	 &nbsp;<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" scope="page"/>         
           			<bean:write name="codeitem" property="codename" />&nbsp;
            </td>
            <td align="left" class="RecordRow" nowrap>                
                	&nbsp;<hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>         
           			<bean:write name="codeitem" property="codename" />&nbsp;
            </td>
            <td align="left" class="RecordRow" nowrap>                
                &nbsp;<bean:write  name="element" property="a0101" filter="true"/>&nbsp;
            </td>
           	<td align="center" class="RecordRow" nowrap>
           		<hrms:priv func_id="570502">
	           		<logic:notEqual name="element" property="ext" value="">
	           			<a href="/pos/roleinfo/pos_task_book?usertable=${policeForm.userbase}A00&usernumber=<bean:write  name="element" property="a0100" filter="true"/>&i9999=<bean:write  name="element" property="i9999" filter="true"/>"><img src="/images/view.gif" border=0></a>
	           		</logic:notEqual>
           		</hrms:priv>
	   		</td>
	   		<td align="center" class="RecordRow" nowrap>                
	   			<hrms:priv func_id="570501"> 
                	<a href="javascript:send('<bean:write  name="element" property="a0100" filter="true"/>')"  target="mil_body"><img src="/images/edit.gif" border=0></a>
                </hrms:priv>
            </td>      	    		        	        	        
          </tr>
   	   </hrms:paginationdb>
      </table>
    </td>
  </tr>
  <tr>
  <td>
      <table  width="100%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
				<hrms:paginationtag name="policeForm"
								pagerows="${policeForm.pagerows}" property="pagination"
								scope="page" refresh="true"></hrms:paginationtag>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		         <p align="right"><hrms:paginationdblink name="policeForm" property="pagination" nameId="policeForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table>
  </td>
  </tr>
</table>


</html:form>