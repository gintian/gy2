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
	taskBookForm.action = "/pos/roleinfo/taskbooklist.do?b_up=link&a0100="+id;
	taskBookForm.submit();
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
</script>

<html:form action="/pos/roleinfo/taskbooklist">
<br>
<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
<thead>
	<tr class="" width="100%">
		<td  align="left" nowrap colspan="6">
		人员库：
		<html:select name="taskBookForm" size="1" property="dbname"  style="width:120px;">
			<html:optionsCollection property="dbnamelist" value="dataValue" label="dataName"/>
		</html:select>&nbsp;&nbsp;
		年度：
		<html:select name="taskBookForm" size="1" property="taskyear" >
			<html:optionsCollection property="yearlist" value="dataValue" label="dataName"/>
		</html:select>&nbsp;&nbsp;
		姓名：
		<html:text name="taskBookForm" property="username" size="9" maxlength="20" onkeypress="event.returnValue=IsDigit();"/>
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
   	   <hrms:paginationdb id="element" name="taskBookForm" sql_str="taskBookForm.sqlstr" table="" where_str="" columns="taskBookForm.column" order_by="taskBookForm.order_by" page_id="pagination" pagerows="${taskBookForm.pagerows}">
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
                	<hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page" uplevel="${taskBookForm.uplevel}"/>         
           			<bean:write name="codeitem" property="codename" />&nbsp;
            </td>
            <td align="left" class="RecordRow" nowrap>                
                	 <hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" scope="page"/>         
           			<bean:write name="codeitem" property="codename" />&nbsp;
            </td>
            <td align="left" class="RecordRow" nowrap>                
                	 <hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>         
           			<bean:write name="codeitem" property="codename" />&nbsp;
            </td>
            <td align="left" class="RecordRow" nowrap>                
                <bean:write  name="element" property="a0101" filter="true"/>&nbsp;
            </td>
           <td align="center" class="RecordRow" nowrap>
           		<logic:notEqual name="element" property="ext" value="">
           			<a href="/pos/roleinfo/pos_task_book?usertable=${taskBookForm.dbname}A00&usernumber=<bean:write  name="element" property="a0100" filter="true"/>&i9999=<bean:write  name="element" property="i9999" filter="true"/>"><img src="/images/view.gif" border=0></a>
           		</logic:notEqual>
	   		</td>
	   		<td align="center" class="RecordRow" nowrap>                
	   			<hrms:priv func_id="52021"> 
                	<a href="javascript:send('<bean:write  name="element" property="a0100" filter="true"/>')"  target="mil_body"><img src="/images/link.gif" border=0></a>
                </hrms:priv>
            </td>      	    		        	        	        
          </tr>
   	   </hrms:paginationdb>
         
         
</table>
<table  width="90%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
				<hrms:paginationtag name="taskBookForm"
								pagerows="${taskBookForm.pagerows}" property="pagination"
								scope="page" refresh="true"></hrms:paginationtag>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		         <p align="right"><hrms:paginationdblink name="taskBookForm" property="pagination" nameId="taskBookForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table>
</html:form>