<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String manager=userView.getManagePrivCodeValue();  
%>
<script language="JavaScript" src="/module/utils/js/template.js"></script>
<script type="text/javascript">
	var manageCode = '<%=manager%>';
	if("exam"=="${aboutForm.t_flag}"){//培训计划用到
		manageCode="<%=userView.getUnitIdByBusi("6")%>";
	}
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
.strTable{
	border: 1px solid #eee;
	height: 220px;    
	width: 400px;            
	overflow: auto;            
	margin: 1em 1;
	
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
}
</style>
<hrms:themes></hrms:themes>
<script language="JavaScript" src="./generalsearch.js"></script>
<html:form action="/system/sms/send_sms_query">
<input type="hidden" id="itemid" name="itemid">
<table width="690px" border="0" align="center" style="margin-top: -24px;">
  <tr> 
    <td width="35%" valign="bottom">&nbsp;</td>
    <td width="8%" rowspan="2" align="center">
    	<table width="100%" border="0" cellpadding=0>
        	<tr> 
          		<td height="35" align="center"> 
            		<input type="button" name="Submit111" value=" <bean:message key='button.setfield.addfield'/> " onclick="additemtr('item_field');" Class="smallbutton"> 
          		</td>
        	</tr>
        	<tr> 
          		<td height="70" align="center"> 
            		<input type="button" name="Submit2" onclick="delTableStr();" value=" <bean:message key='button.delete'/> " Class="smallbutton"> 
          		</td>
        	</tr>
      </table>
    </td>
    <td  align="center">&nbsp; </td>
  </tr>
  <tr> 
    <td height="270" align="center"> 
      <fieldset style="width:100%;padding:5px 0px 5px 0px;">
      <legend><bean:message key='selfservice.query.queryfield'/></legend>
      <table width="100%" border="0" align="center">
        <tr> 
          <td height="27"> 
          <html:select styleId="fieldSetId" name="aboutForm" property="fieldSetId" onchange="change();" style="width:100%"> 
            <html:optionsCollection property="setList" value="dataValue" label="dataName" /> 
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
   <td align="center" height="270" valign="top">
   		<fieldset style="width:100%;padding:5px;">
      	<legend><bean:message key='general.inform.search.condset'/></legend>
    	<table width="100%" border="0" cellpadding="0" cellspacing="0">
    		<tr>
    			<td style="padding-top:5px;">
    			&nbsp;<bean:message key='label.query.dbpre'/>&nbsp;<html:select name="aboutForm" property="pre"> 
            			<html:optionsCollection property="preList" value="dataValue" label="dataName" /> 
            		</html:select>
    			</td>
    		</tr>
        	<tr> 
         		<td height="220" valign="top">
         			<div id="strTable" class="strTable common_border_color" style="width:100%;margin-top:5px;">
						<table width="100%"id="tablestr" cellpadding="0" cellspacing="0" border="0">
							<tr>
								<td align="center" class="TableRow noleft" style="border-top: none;" width="15%"><bean:message key='label.query.logic'/></td>
         						<td align="center" class="TableRow noleft" style="border-top: none;" width="30%" nowrap><bean:message key='general.inform.search.item.object'/></td>
         						<td align="center" class="TableRow noleft" style="border-top: none;" width="15%"><bean:message key='label.query.relation'/></td>
         						<td align="center" class="TableRow noleft noright" style="border-top: none;" width="40%" nowrap="nowrap"><bean:message key='label.query.value'/></td>
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
    			<td align="left" nowrap="nowrap"><input type="checkbox" name="like"><bean:message key='label.query.like'/></td>
    			<td align="left" nowrap="nowrap">&nbsp;&nbsp;&nbsp;</td> 
    			<td align="left" nowrap="nowrap"></td> 
    			<td align="left">
    				
            	</td>    		
    		</tr>
    		
    	</table>
      </td>
     
  </tr>
</table>
<table width="100%" border="0" align="center">
    		<tr>
    			<td align="right"><input type="button" name="searchok" value="<bean:message key='button.ok'/>" onclick='searchSetCond();' Class="mybutton"></td>
    			<td align="left"><input type="button" name="close" value="<bean:message key='button.cancel'/>" onclick="windowClose();" Class="mybutton"></td>
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