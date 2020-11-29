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
	policeForm.action = "/pos/police/jqdt.do?b_up=link&b0110="+id;
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
	policeForm.action = "/pos/police/jqdt.do?b_search=link&tofirst=yes";
	policeForm.submit();	
}
 function returnTO()
  {
     window.location="/templates/attestation/police/wizard.do?br_work_wizard=link";
  }
</script>

<html:form action="/pos/police/jqdt">
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
			机构名称
			<html:text name="policeForm" property="orgname" size="9" maxlength="20" onkeypress="event.returnValue=IsDigit();"/>
			&nbsp;&nbsp;&nbsp;&nbsp;
			
		   <input class="mybutton" type="button" name="search" value="<bean:message key="button.query"/>" onclick="querry()"/>
		</td>
	</tr>
</thead>
</talbe>
<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
           	 <td align="center" class="TableRow" width="30%" nowrap>
                <bean:message key="police.um.name"/>&nbsp;
             </td>
             <td align="center" class="TableRow" width="40%" nowrap>
                <bean:message key="police.yqdt.title"/>&nbsp;
             </td>
             <td align="center" class="TableRow" width="15%" nowrap>
             	<logic:equal value="dept" name="policeForm" property="cyclename">
             		<bean:message key="police.workinfo.orgtaskbook"/>&nbsp;
             	</logic:equal>
             	<logic:equal value="yqdt" name="policeForm" property="cyclename">
                	<bean:message key="police.yqdt.dt"/>&nbsp;
                </logic:equal>
             </td>
             <td align="center" class="TableRow" width="15%" nowrap>
             	<logic:equal value="yqdt" name="policeForm" property="cyclename">
                	<bean:message key="police.yqdt.up"/>&nbsp;
                </logic:equal>
                <logic:equal value="dept" name="policeForm" property="cyclename">
             		<bean:message key="police.person.up"/>&nbsp;
             	</logic:equal>
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
                	 &nbsp;<hrms:codetoname codeid="UM" name="element" codevalue="codeitemid" codeitem="codeitem" scope="page"/>         
           			<bean:write name="codeitem" property="codename" />&nbsp;
            </td>
            <td align="left" class="RecordRow" nowrap>                
                &nbsp;<bean:write  name="element" property="title" filter="true"/>&nbsp;
            </td>
           <td align="center" class="RecordRow" nowrap>
           		<logic:equal value="yqdt" name="policeForm" property="cyclename">
	           		<hrms:priv func_id="570202">
		           		<logic:notEqual name="element" property="ext" value="">
		           			<a href="/pos/police/open_police_book?tablename=b00&filetype=b0110&filevalue=<bean:write  name="element" property="codeitemid" filter="true"/>&i9999=<bean:write  name="element" property="i9999" filter="true"/>"><img src="/images/view.gif" border=0></a>
		           		</logic:notEqual>
	           		</hrms:priv>
	           	</logic:equal>
	           	<logic:equal value="dept" name="policeForm" property="cyclename">
	           		<hrms:priv func_id="570302">
		           		<logic:notEqual name="element" property="ext" value="">
		           			<a href="/pos/police/open_police_book?tablename=b00&filetype=b0110&filevalue=<bean:write  name="element" property="codeitemid" filter="true"/>&i9999=<bean:write  name="element" property="i9999" filter="true"/>"><img src="/images/view.gif" border=0></a>
		           		</logic:notEqual>
	           		</hrms:priv>
	           	</logic:equal>
	   		</td>
	   		<td align="center" class="RecordRow" nowrap>
	   			<logic:equal value="yqdt" name="policeForm" property="cyclename">                
		   			<hrms:priv func_id="570201">
	                	<a href="javascript:send('<bean:write  name="element" property="codeitemid" filter="true"/>')"  target="mil_body"><img src="/images/edit.gif" border=0></a>
	                </hrms:priv>
	            </logic:equal>
	            <logic:equal value="dept" name="policeForm" property="cyclename">
	            	<hrms:priv func_id="570301">
	                	<a href="javascript:send('<bean:write  name="element" property="codeitemid" filter="true"/>')"  target="mil_body"><img src="/images/edit.gif" border=0></a>
	                </hrms:priv>
	            </logic:equal>
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
<table  width="90%" align="center" >
  <tr>
   <td align="center">
      <logic:equal value="wizard" name="policeForm" property="returnvalue">
         <input type='button' name='b_save' value='返回' onclick='returnTO();' class='mybutton'>
      </logic:equal>
      
   </td>
  </tr>
</table>
</html:form>