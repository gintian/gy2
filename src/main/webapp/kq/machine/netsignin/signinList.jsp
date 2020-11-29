<%@ page errorPage="error.jsp"%>
<%@ page import="javax.servlet.ServletException"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<% int i=0;%>
<script language="javascript">
  function changes()
  {
      var dbsign = document.getElementById("dbsign").value;
      var a0100sign = document.getElementById("a0100sign").value;
  	  netSigninForm.action="/kq/machine/netsignin/signinlist.do?b_self=link&dbsign="+dbsign+"&a0100sign="+a0100sign+"&filg=2";
      netSigninForm.submit();
  }
  function makeup()
  {
      netSigninForm.action="/kq/machine/netsignin/makeupsign.do?b_make=link";
      netSigninForm.submit();
  }
  function goback()
  {
      netSigninForm.action="/kq/machine/netsignin/empNetSingnin_data.do?b_search=link";
      netSigninForm.submit();
  }
</script>

<html:form action="/kq/machine/netsignin/signinlist">
		<table width="80%" border="0" cellspacing="0"  align="center">
			<tr>
				<td align="left" nowrap colspan="6">
						<bean:message key="label.from"/>
   	  	 				<input type="text" name="start_date" value="${netSigninForm.start_date}" extra="editor" class="inputtext" style="width:100px;font-size:10pt;text-align:left" id="editor1"  dropDown="dropDownDate">
   	  	 				<bean:message key="label.to"/>
   	  	 				<input type="text" name="end_date"  value="${netSigninForm.end_date}" extra="editor" class="inputtext" style="width:100px;font-size:10pt;text-align:left" id="editor2"  dropDown="dropDownDate">
            			&nbsp;<input type="button" name="br_return" value='<bean:message key="button.query"/>' class="mybutton" onclick="changes();"> 
				</td>
			</tr>
		</table>
	<table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTableF" style="margin-top:3px;">
		<html:hidden styleId="dbsign" name="netSigninForm" property="dbsign"/>
		<html:hidden styleId="a0100sign" name="netSigninForm" property="a0100sign"/>
		<thead>
			<tr class="TableRow">
     			<td align="center" class="TableRow" nowrap>
					姓名
     			</td>
      			<td align="center" class="TableRow"  nowrap>
					卡号
      			</td> 
      			<td align="center" class="TableRow"  nowrap>
					日期
      			</td> 
      			<td align="center" class="TableRow"  nowrap>
					时间
      			</td> 
      			<td align="center" class="TableRow"  nowrap>
					说明
      			</td>
      			<td align="center" class="TableRow"  nowrap>
					审批标志
      			</td>
    		</tr> 
		</thead>
		<hrms:paginationdb id="element" name="netSigninForm" sql_str="netSigninForm.sql_self" table="" where_str="netSigninForm.where_self" columns="${netSigninForm.column_self}" order_by="${netSigninForm.order_self}" page_id="pagination" pagerows="20"  indexes="indexes">
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
           <td align="center" class="RecordRow" nowrap>
             <bean:write name="element" property="a0101" filter="false"/>
           </td>
           <td align="center" class="RecordRow" nowrap>
              <bean:write name="element" property="card_no" filter="false"/>
           </td>
           <td align="center" class="RecordRow" nowrap>
              <bean:write name="element" property="work_date" filter="false"/>
           </td>
           <td align="center" class="RecordRow" nowrap>
              <bean:write name="element" property="work_time" filter="false"/>
           </td>
           <td align="center" class="RecordRow" nowrap>
              <bean:write name="element" property="location" filter="false"/>
           </td>  
           <td align="center" class="RecordRow" nowrap>
             <hrms:codetoname codeid="23" name="element" codevalue="sp_flag" codeitem="codeitem" scope="page"/>  	      
                <bean:write name="codeitem" property="codename" />&nbsp;  
           </td>        
       </tr>
      </hrms:paginationdb>
	</table>
	<table  width="80%" align="center" class="RecordRowP">
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
		          <p align="right"><hrms:paginationdblink name="netSigninForm" property="pagination" nameId="netSigninForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
    
  </table>
  <table  width="80%" align="center">
		<tr>
		    <td align="center">	
		    	<hrms:priv func_id="0C3463">         
                        <input type="button" name="br_return" value='补签' class="mybutton" onclick="makeup();"> 
		        </hrms:priv>
		        <input type="button" name="br_return" value='<bean:message key="button.return"/>' class="mybutton" onclick="goback();"> 
		        
			</td>
		</tr>
  </table>
</html:form>