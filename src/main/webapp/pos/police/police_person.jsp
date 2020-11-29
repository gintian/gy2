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
	policeForm.action = "/pos/police/person.do?b_up=link&a0100="+id;
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
	policeForm.action = "/pos/police/person.do?b_search=link&tofirst=yes";
	policeForm.submit();	
}
 function returnTO()
  {
     window.location="/templates/attestation/police/wizard.do?br_work_wizard=link";
  }
</script>

<html:form action="/pos/police/person">
<br>
<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
<thead>
	<tr class="" width="100%">
		<td  align="left" nowrap colspan="6">
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
		</td>
	</tr>
</thead>
</talbe>
<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
           	 <td align="center" class="TableRow" width="40%" nowrap>
                <bean:message key="police.person.book"/>&nbsp;
             </td>
             <td align="center" class="TableRow" width="30%" nowrap>
                <bean:message key="police.person.uptime"/>&nbsp;
             </td>
             <td align="center" class="TableRow" width="15%" nowrap>
                <bean:message key="police.person.see"/>&nbsp;
             </td>
             <td align="center" class="TableRow" width="15%" nowrap>
                <bean:message key="police.person.up"/>&nbsp;
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
                	 &nbsp;<bean:write  name="element" property="title" filter="true"/>&nbsp;
            </td>
            <td align="left" class="RecordRow" nowrap>                
                &nbsp;<bean:write  name="element" property="createtime" filter="true"/>&nbsp;
            </td>
           <td align="center" class="RecordRow" nowrap>
	           <hrms:priv func_id="570402">
		           	<logic:notEqual name="element" property="ext" value="">
		           		<a href="/pos/police/open_police_book?tablename=${policeForm.userbase }A00&filetype=a0100&filevalue=${policeForm.a0100}&i9999=<bean:write  name="element" property="i9999" filter="true"/>"><img src="/images/view.gif" border=0></a>
		           	</logic:notEqual>
	           </hrms:priv>
	   		</td>
	   		<td align="center" class="RecordRow" nowrap>               
		   		<hrms:priv func_id="570401">
	                <a href="javascript:send('${policeForm.a0100 }');"><img src="/images/edit.gif" border=0></a>
	            </hrms:priv>
            </td>      	    		        	        	        
          </tr>
   	   </hrms:paginationdb>
         
         
</table>
<table  width="90%" align="center" class="RecordRowP">
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
<table  width="90%" align="center">
	<tr>
		<td align="center">
			<hrms:priv func_id="570401">
				<input type="button" class="mybutton" value="<bean:message key="police.person.up"/>" onclick="send('${policeForm.a0100 }')"/>
			</hrms:priv>
			<logic:equal value="wizard" name="policeForm" property="returnvalue">
              <input type='button' name='b_save' value='返回' onclick='returnTO();' class='mybutton'>
            </logic:equal>
		</td>
	</tr>
</table>
</html:form>