<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
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
}
</style>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String manager="";
	if(!userView.isSuper_admin())
		manager = userView.getUnitIdByBusi("6");  
%>
<script type="text/javascript">
<!--
var manageCode = '<%=manager%>';
//-->
</script>
<script language="JavaScript" src="./trainsearch.js"></script>
<html:form action="/train/attendance/trainAtteCourse.do?b_refer=link">
<input type="hidden" name="itemid">
<table width="100%" border="0" align="center">
  <tr> 
    <td height="270"  width="30%" align="center"> 
      <fieldset style="width:100%;">
      <legend><bean:message key='selfservice.query.queryfield'/></legend>
      <table width="100%" border="0" align="center">
        <tr> 
          <td height="260"> 
           <html:select name="trainAtteForm" property="item_field" multiple="multiple" ondblclick="additemtr('item_field');" style="height:230px;width:100%;font-size:9pt"> 
            <html:optionsCollection property="fieldlist" value="dataValue" label="dataName" /> 
            </html:select> </td>
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
   <td align="center" height="270" width="60%" valign="top">
   		<fieldset style="width:100%;">
      	<legend><bean:message key='general.inform.search.condset'/></legend>
    	<table width="100%" border="0">
        	<tr> 
         		<td height="260" valign="top">
         			<div id="strTable">
         				<table width="100%" id="tablestr" border="0" class="ListTable1">
         					<tr>
         						<td align="center" class="TableRow" width="15%"><bean:message key='label.query.logic'/></td>
         						<td align="center" class="TableRow" width="30%" nowrap><bean:message key='general.inform.search.item.object'/></td>
         						<td align="center" class="TableRow" width="15%"><bean:message key='label.query.relation'/></td>
         						<td align="center" class="TableRow" nowrap><bean:message key='label.query.value'/></td>
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
<table width="100%" cellpadding="0" cellspacing="0" border="0" align="center">
	<tr> 
      <td height="25" width="170">
    	<table width="100%" border="0">
    		<tr>
    			<td><input type="checkbox" name="like"><bean:message key='label.query.like'/></td>
    		</tr>
    	</table>
      </td>
      <td height="25" width="100">&nbsp;
      </td>
      <td height="25" align="right">
    	<table width="100%" border="0" align="center">
    		<tr>
    			<td width="50%" align="right"><input type="button" name="searchok" value="<bean:message key='button.query'/>" onclick='searchSetCond("","","");' Class="mybutton"></td>
    			<td width="35%"><input type="button" name="close" value="<bean:message key='button.cancel'/>" onclick="window.close();" Class="mybutton"></td>
    			<td>&nbsp;</td>
    		</tr>
    	</table>
      </td>
  </tr>
</table>
<div id="date_panel" style="display:none">
	<bean:define id="dlist" name="trainAtteForm" property="tjtimelist" />
	<%
		int sizeNum=0;
		ArrayList list=(ArrayList)dlist;
		if(list!=null){
			sizeNum=list.size()>10?10:list.size();
		}
	 %>
	<select name="date_box" size="<%=sizeNum %>"  style="width:120" onchange="setSelectValue();" onclick="setSelectValue();">    
		<!-- <option value="$YRS[10]"><bean:message key='general.inform.search.years'/></option>
		<option value="<bean:message key='general.inform.search.this.years'/>"><bean:message key='general.inform.search.this.years'/></option>
		<option value="<bean:message key='general.inform.search.this.month'/>"><bean:message key='general.inform.search.this.month'/></option>
		<option value="<bean:message key='general.inform.search.this.day'/>"><bean:message key='general.inform.search.this.day'/></option>				    
		<option value="<bean:message key='general.inform.search.day'/>"><bean:message key='general.inform.search.day'/></option>
		<option value="<bean:message key='kq.wizard.edate'/>"><bean:message key='kq.wizard.edate'/></option>
		<option value="1992.4.12">1992.04.12</option>	
		<option value="1992.4">1992.04</option>	
		<option value="1992">1992</option>			    
		<option value="1992-04-12">1992-04-12</option>
		<option value="1992-04">1992-04</option>			 -->  
		<logic:iterate id="d" name="dlist">
			<option value="${d }">${d }</option>
		</logic:iterate>  			    		    
	</select>
</div>
</html:form>