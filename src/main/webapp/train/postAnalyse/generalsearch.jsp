<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.businessobject.train.TrainCourseBo"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	TrainCourseBo bo = new TrainCourseBo(userView);
	String manager=bo.getUnitIdByBusi();//userView.getManagePrivCodeValue();  
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
#strTable{
	border: 1px solid #eee;
	height: 240px;    
	width: 400px;            
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
<html:form action="/train/postAnalyse/generalsearch">
<input type="hidden" name="itemid" id="itemid">
<table width="100%" border="0" align="center">
  
  <tr> 
    <td width="30%" height="270" align="center"> 
      <fieldset style="width:100%;">
      <legend><bean:message key='selfservice.query.queryfield'/></legend>
      <table width="100%" border="0" align="center">
        <tr> 
          <td height="27"> 
          <html:select name="trainStationForm" styleId="fieldid" property="fieldid" onchange="change();" style="width:100%"> 
            <html:optionsCollection property="setlist" value="dataValue" label="dataName" /> 
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
    <td width="10%" align="center">
    	<table width="100%" border="0">
        	<tr> 
          		<td height="40" align="center"> 
            		<input type="button" name="Submit111" value="<bean:message key='button.setfield.addfield'/>" onclick="additemtr('item_field');" Class="mybutton"> 
          		</td>
        	</tr>
        	<tr> 
          		<td height="40" align="center"> 
            		<input type="button" name="Submit2" onclick="delTableStr();" value="<bean:message key='button.delete'/>" Class="mybutton"> 
          		</td>
        	</tr>
      </table>
    </td>
   <td width="60%" align="center" height="270" valign="top">
   		<fieldset style="width:100%;">
      	<legend><bean:message key='general.inform.search.condset'/></legend>
    	<table width="100%" border="0">
        	<tr> 
         		<td height="260" valign="top">
         			<div id="strTable" style="border-left: none;border-right: none;border-bottom: none;" class="common_border_color">
         				<table width="100%" id="tablestr" border="0" style="border-collapse: collapse">
         					<tr class="fixedHeaderTr">
         						<td align="center" class="TableRow" style="border-top: none;" width="15%"><bean:message key='label.query.logic'/></td>
         						<td align="center" class="TableRow" style="border-top: none;"  width="30%" nowrap><bean:message key='general.inform.search.item.object'/></td>
         						<td align="center" class="TableRow" style="border-top: none;"  width="15%"><bean:message key='label.query.relation'/></td>
         						<td align="center" class="TableRow" style="border-top: none;"  nowrap><bean:message key='label.query.value'/></td>
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
<table width="100%" border="0" align="center">
	<tr> 
      <td height="25" width="200">
    	<table width="100%" border="0">
    		<tr>
    		 <logic:equal value="0" name="trainStationForm" property="fieldSetId">
    			<td width="80"><input type="checkbox" name="like"><bean:message key='label.query.like'/></td>
    			<td id="viewHistory" style="display:none"><input type="checkbox" name="history"><bean:message key='label.query.history'/></td>
    		</logic:equal>
    		<logic:equal value="A01" name="trainStationForm" property="fieldSetId">
    			<td width="80"><input type="checkbox" name="like"><bean:message key='label.query.like'/></td>
    			<td id="viewHistory" style="display:none"><input type="checkbox" name="history"><bean:message key='label.query.history'/></td>
    		</logic:equal>
    		<logic:notEqual value="0" name="trainStationForm" property="fieldSetId">
    		  <logic:notEqual value="A01" name="trainStationForm" property="fieldSetId">
    			<td width="80"><input type="checkbox" name="like"><bean:message key='label.query.like'/></td>
    		  </logic:notEqual>
    		</logic:notEqual>
    		</tr>
    		
    	</table>
      </td>
      <td height="25" width="200">
    	<table width="100%" border="0">
    		<tr>
    			<td>&nbsp;</td>
      			<logic:equal name="trainStationForm" property="type" value="2">
    			<td><input type="radio" name="unite" value="1"><bean:message key='label.query.dept'/></td>
    			<td><input type="radio" name="unite" value="0"><bean:message key='label.query.org'/></td>
    			<td><input type="radio" name="unite" value="2" checked><bean:message key='label.query.all'/></td>
      			</logic:equal>
      		</tr>
    	</table>
      </td>
      <td height="25">
    	<table width="100%" border="0" align="center">
    		<tr>
    			<td width="25%" align="right"><input type="button" name="searchok" value="<bean:message key='button.query'/>" onclick='searchSetCond("${trainStationForm.code}","${trainStationForm.tablename}","${trainStationForm.type}","${trainStationForm.fieldSetId}");' Class="mybutton"></td>
    			<td width="25%" align="left"><input type="button" name="close" value="<bean:message key='button.cancel'/>" onclick="window.close();" Class="mybutton"></td>
    			<td>&nbsp;</td>
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