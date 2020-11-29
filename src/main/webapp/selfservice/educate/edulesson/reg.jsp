<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<%
 
 int i=0;
 
 
 
%>
<br>
<br>
<br>
<center>

<html:form action="/selfservice/educate/edulesson/reg">
     
              	<table border="0" cellpmoding="0" cellspacing="2"  class="DetailTable"  cellpadding="0">
       			 <tr> 
          			<td align="left"  nowrap valign="center">
          				<bean:message key="lable.enterfor.select"/>&nbsp;
          			</td>
          			<td >
          				
          			 	<hrms:importgeneraldata showColumn="R3130" valueColumn="R3101" flag="true"  paraValue="1"
                  			sql="select R3101,R3130 from R31 where R3127='04' and 1=? " collection="list" scope="page"/> 
                  			<html:select name="regForm" property="movementNum" size="1"> 
                  			<html:option value="#">请选择</html:option>
            				<html:options collection="list" property="dataValue" labelProperty="dataName"/> 
            				</html:select>
            			 </td>
          			<td align="left" nowrap  valign="center">&nbsp;</td>
          			<td  width="20%"></td>
        		</tr>
        	</table>
        	<br>	
   <table>                                             
          <tr class="list3">
            	<td align="center" >
         	<hrms:submit styleClass="mybutton" property="b_save" onclick="document.regForm.target='_self';validate('RS','movementNum','申请的活动');return (document.returnValue && ifqrtj());">
            		<bean:message key="conlumn.infopick.educate.enterfor"/>
	 	</hrms:submit>	 	
         		 	           
            </td>
          </tr>          
      </table>
    <logic:equal name="regForm" property="flag" value="1">
    <script>
    	alert('报名成功');
    </script>
    </logic:equal>
    <logic:equal name="regForm" property="moveFlag" value="1">
    <script>
    	alert('你已提交了该活动');
    </script>
    </logic:equal>
</html:form>

  </center>