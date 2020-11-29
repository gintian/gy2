<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<% 
     java.util.Calendar cal = java.util.Calendar.getInstance(); 
     int year = cal.get(java.util.Calendar.YEAR); 
%>
<hrms:themes></hrms:themes>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="../../inform.js"></script>

<html:form action="/general/inform/emp/batch/delind">
<html:hidden name="indBatchHandForm" property="count"/>
<html:hidden name="indBatchHandForm" property="countall"/>
<table width="330" border="0" align="center">
  <tr> 
    <td align="center">
      <fieldset style="width:100%;">
       <legend><bean:message key='infor.menu.batupdate_d'/></legend>
      <table width="100%" border="0" align="center">
        <tr> 
          <td width="35%" align="center"> 
          	<input type="radio" name="valuebutton" value="1" onclick="viewTimes(1)" checked>&nbsp;<bean:message key='infor.menu.del.current.records'/>
          </td>
          <td>&nbsp;</td>
        </tr>
        <logic:equal name="indBatchHandForm" property="flag" value="1">
        <tr height="30"> 
          <td align="center">
           	 <input type="radio" name="valuebutton" onclick="viewTimes(2)" value="2">&nbsp;<bean:message key='infor.menu.del.particular.records'/>
          </td>
          <td>
          	<div id="timeSpan" style="display:none">
          	<table width="80%" border="0">
              <tr> 
                <td>
                	<select name="year" style="width:60px">
                		<%for(int i=year+5;i>year-15;i--){%>
                			<%if(i==year){%>
                				<option value="<%=i%>" selected><%=i%><bean:message key='datestyle.year'/></option>
                			<%}else{%>
                				<option value="<%=i%>"><%=i%><bean:message key='datestyle.year'/></option>
                		<%}}%>
                	</select>
                </td>
                <td>
                	<select name="month" style="width:50px">
                		<option value="1">01<bean:message key='datestyle.month'/></option>
                		<option value="2">02<bean:message key='datestyle.month'/></option>
                		<option value="3">03<bean:message key='datestyle.month'/></option>
                		<option value="4">04<bean:message key='datestyle.month'/></option>
                		<option value="5">05<bean:message key='datestyle.month'/></option>
                		<option value="6">06<bean:message key='datestyle.month'/></option>
                		<option value="7">07<bean:message key='datestyle.month'/></option>
                		<option value="8">08<bean:message key='datestyle.month'/></option>
                		<option value="9">09<bean:message key='datestyle.month'/></option>
                		<option value="10">10<bean:message key='datestyle.month'/></option>
                		<option value="11">11<bean:message key='datestyle.month'/></option>
                		<option value="12">12<bean:message key='datestyle.month'/></option>
                	</select>
                </td>
                <td>
                	<select name="frequency" style="width:50px">
                		<option value="1">1<bean:message key='hmuster.label.count'/></option>
                		<option value="2">2<bean:message key='hmuster.label.count'/></option>
                		<option value="3">3<bean:message key='hmuster.label.count'/></option>
                		<option value="4">4<bean:message key='hmuster.label.count'/></option>
                		<option value="5">5<bean:message key='hmuster.label.count'/></option>
                		<option value="6">6<bean:message key='hmuster.label.count'/></option>
                		<option value="7">7<bean:message key='hmuster.label.count'/></option>
                		<option value="8">8<bean:message key='hmuster.label.count'/></option>
                		<option value="9">9<bean:message key='hmuster.label.count'/></option>
                	</select>
                </td>
              </tr>
            </table>
            </div>
            </td>
        </tr>
        </logic:equal>
        <tr> 
          <td align="center">
             <input type="radio" name="valuebutton" onclick="viewTimes(0)" value="0">&nbsp;<bean:message key='infor.menu.del.all.records'/>
          </td>
          <td>&nbsp;</td>
        </tr>
      </table>
      </fieldset>
      </td>
  </tr>
  <tr> 
    <td align="center">
    	<input type="button" name="button1" value="<bean:message key='button.ok'/>" onclick='saveDelind("${indBatchHandForm.setname}","${indBatchHandForm.a_code}","${indBatchHandForm.viewsearch}","${indBatchHandForm.dbname}","${indBatchHandForm.infor}");' Class="mybutton">&nbsp;
    	<input type="button" name="button2" value="<bean:message key='button.close'/>" onclick="window.close();" Class="mybutton">&nbsp;&nbsp;&nbsp;&nbsp;
    </td>
  </tr>
</table>
<html:hidden name="indBatchHandForm" property="flag"/>
<html:hidden name="indBatchHandForm" property="prive"/>
</html:form>
