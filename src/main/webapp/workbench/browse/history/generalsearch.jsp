<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	//String manager=userView.getManagePrivCodeValue(); 
	String manager=userView.getUnitIdByBusi("4"); 
%>
<script type="text/javascript">
	var manageCode = '<%=manager%>';
</script>
<style type="text/css"> 
.btn1 {
 BORDER-RIGHT: #7b9ebd 1px solid;
 PADDING-RIGHT: 0px;
 BORDER-TOP: #7b9ebd 1px solid; 
 PADDING-LEFT: 0px; FONT-SIZE: 12px; 
 BORDER-LEFT: #7b9ebd 1px solid; 
 CURSOR: hand; 
 COLOR: black; 
 PADDING-TOP: 1px; 
 PADDING-BOTTOM: 0px;
 BORDER-BOTTOM: #7b9ebd 1px solid;
 border:#0042A0 1px solid;
 background-image:url(/images/button.jpg);	
}
.btn2 {
 BORDER-RIGHT: #7b9ebd 1px solid;
 PADDING-RIGHT: 3px; 
 BORDER-TOP: #7b9ebd 1px solid; 
 PADDING-LEFT: 3px; FONT-SIZE: 12px; 
 BORDER-LEFT: #7b9ebd 1px solid;  
 CURSOR: hand; 
 COLOR: black; 
 PADDING-TOP: 1px; 
 PADDING-BOTTOM: 0px;
 BORDER-BOTTOM: #7b9ebd 1px solid;
 border:#0042A0 1px solid;
 background-image:url(/images/button.jpg);	
}
.btn3 {
BORDER-RIGHT: #7b9ebd 1px solid;
 PADDING-RIGHT: 2px;
 BORDER-TOP: #7b9ebd 1px solid; 
 PADDING-LEFT: 2px; FONT-SIZE: 12px; 
 BORDER-LEFT: #7b9ebd 1px solid; 
 CURSOR: hand; 
 COLOR: black; 
 PADDING-TOP: 1px; 
 PADDING-BOTTOM: 0px;
 BORDER-BOTTOM: #7b9ebd 1px solid;
 border:#0042A0 1px solid;
 background-image:url(/images/button.jpg);	
}
.fixedtab{
	border: 1px solid #eee;
	height: 240px; 
	width: 385px;   
	width: 400px!important;            
	overflow: auto;            
	margin: 1em 1;
	position:absolute;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
}
</style>
<script language="JavaScript" src="./generalsearch.js"></script>

<hrms:themes />
<style>
.notop{
	border-top: 0pt solid;
}
</style>
<html:form action="/workbench/browse/history/showinfodata">
<input type="hidden" name="itemid" id='itemid'>
<table width="100%" border="0" align="center">
  <tr> 
    <td width="30%" height="270" align="center"> 
      <fieldset style="width:auto;">
      <legend><bean:message key='selfservice.query.queryfield'/></legend>
      <table width="100%" border="0" align="center">
        <tr> 
          <td height="27"> 
          <hrms:optioncollection name="personHistoryForm" property="setlist" collection="lel"   />
          <html:select name="personHistoryForm" property="fieldid" styleId="fieldid" onchange="change();" style="width:100%"> 
            <html:options collection="lel" property="dataValue" labelProperty="dataName"/>
            </html:select> 
            </td>
        </tr>
        <tr> 
          <td height="230"> 
            <select name="item_field" multiple="multiple" ondblclick="additemtr('item_field');" style="height:230px;width:100%;font-size:9pt">
            </select>
          </td>
        </tr>
      </table>
     </fieldset>
    </td>
    <td width="8%" rowspan="2" align="center" valign="middle">
    	<table width="100%" border="0">
        	<tr> 
          		<td height="50" align="center"> 
            		<input type="button" name="Submit111" value="<bean:message key='button.setfield.addfield'/>" onclick="additemtr('item_field');" Class="mybutton"> 
          		</td>
        	</tr>
        	<tr> 
          		<td height="50" align="center"> 
            		<input type="button" name="Submit2" onclick="delTableStr();" value="<bean:message key='button.delete'/>" Class="mybutton"> 
          		</td>
        	</tr>
      </table>
    </td>
   <td width="62%" align="center" height="270" valign="top">
   		<fieldset style="auto">
      	<legend><bean:message key='general.inform.search.condset'/></legend>
    	<table width="100%" border="0">
        	<tr> 
         		<td height="260" valign="top">
         			<div id="strTable" class="fixedtab">
         				<table width="100%" id="tablestr" border="0" class="ListTable">
         					<tr>
         						<td align="center" class="TableRow_top" width="15%"><bean:message key='label.query.logic'/></td>
         						<td align="center" class="TableRow_left notop" width="30%" nowrap><bean:message key='general.inform.search.item.object'/></td>
         						<td align="center" class="TableRow_left notop" width="15%"><bean:message key='label.query.relation'/></td>
         						<td align="center" class="TableRow_left notop" nowrap><bean:message key='label.query.value'/></td>
         					</tr>
         				</table>
         			</div>
         		</td>
        	</tr>
      	</table>
      	</fieldset>
   </td>
  </tr>
</table>
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
	<tr> 
      <td height="25" width="200">
    	<table width="100%" border="0" cellpadding="0" cellspacing="0">
    		<tr>
    		 <logic:equal value="0" name="personHistoryForm" property="fieldSetId">
    			<td width="80"><input type="checkbox" name="like" id='like'><bean:message key='label.query.like'/></td>
    			<td id="viewHistory" style="display:none"><input type="checkbox" id='history' name="history"><bean:message key='label.query.history'/></td>
    		</logic:equal>
    		<logic:notEqual value="0" name="personHistoryForm" property="fieldSetId">
    			<td width="80"><input type="checkbox" name="likeflag" id='likeflag'><bean:message key='label.query.like'/></td>
    		</logic:notEqual>
    		</tr>
    	</table>
      </td>
      <td height="25" width="200">
    	<table width="100%" border="0" cellpadding="0" cellspacing="0">
    		<tr>
    			<td>&nbsp;</td>
      			<logic:equal name="personHistoryForm" property="type" value="2">
    			<td><input type="radio" name="unite" value="1"><bean:message key='label.query.dept'/></td>
    			<td><input type="radio" name="unite" value="0"><bean:message key='label.query.org'/></td>
    			<td><input type="radio" name="unite" value="2" checked><bean:message key='label.query.all'/></td>
      			</logic:equal>
      		</tr>
    	</table>
      </td>
      <td height="25">
    	<table width="100%" border="0" cellpadding="0" cellspacing="0">
    		<tr>
    			<td colspan="3"><input type="button" name="searchok" value="<bean:message key='button.query'/>" onclick='searchSetCond("${personHistoryForm.a_code}","${personHistoryForm.tablename}","${personHistoryForm.type}","${personHistoryForm.fieldSetId}");' Class="mybutton">
    			<input type="button" name="close" value="<bean:message key='button.cancel'/>" onclick="windowClose();" Class="mybutton">
    			</td>
    		</tr>
    	</table>
      </td>
  </tr>
</table>
<div id="date_panel">
	<select name="date_box" multiple="multiple" size="10"  style="width:120" onchange="setSelectValue();" onclick="setSelectValue();">    
		<option value="$YRS[10]"><bean:message key='general.inform.search.years'/></option>
		<option value="<bean:message key='general.inform.search.this.years'/>"><bean:message key='general.inform.search.this.years'/></option>
		<option value="<bean:message key='general.inform.search.this.month'/>"><bean:message key='general.inform.search.this.month'/></option>
		<option value="<bean:message key='general.inform.search.this.day'/>"><bean:message key='general.inform.search.this.day'/></option>				    
		<option value="<bean:message key='general.inform.search.day'/>"><bean:message key='general.inform.search.day'/></option>
		<option value="<bean:message key='kq.wizard.edate'/>"><bean:message key='kq.wizard.edate'/></option>
		<option value="1992.4.12">1992.04.12</option>	
		<option value="1992.4">1992.04</option>	
		<option value="1992">1992</option>			    
		<option value="1992-04-12">1992-04-12</option>
		<option value="1992-04">1992-04</option>			    			    		    
	</select>
</div>
</html:form>
<script language="JavaScript">
change();
Element.hide('date_panel');
</script>