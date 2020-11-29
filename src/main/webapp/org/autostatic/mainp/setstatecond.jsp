<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<style type="text/css"> 
.btn1 {
 BORDER-RIGHT: #7b9ebd 1px solid;
 PADDING-RIGHT: 5px;
 BORDER-TOP: #7b9ebd 1px solid; 
 PADDING-LEFT: 5px; FONT-SIZE: 12px; 
 BORDER-LEFT: #7b9ebd 1px solid; 
 CURSOR: hand; 
 COLOR: black; 
 PADDING-TOP: 5px; 
 PADDING-BOTTOM: 5px;
 BORDER-BOTTOM: #7b9ebd 1px solid;
 border:#0042A0 1px solid;
 background-image:url(/images/button.jpg);	
}
.btn2 {
 BORDER-RIGHT: #7b9ebd 1px solid;
 PADDING-RIGHT: 8px; 
 BORDER-TOP: #7b9ebd 1px solid; 
 PADDING-LEFT: 8px; FONT-SIZE: 12px; 
 BORDER-LEFT: #7b9ebd 1px solid;  
 CURSOR: hand; 
 COLOR: black; 
 PADDING-TOP: 5px; 
 PADDING-BOTTOM: 5px;
 BORDER-BOTTOM: #7b9ebd 1px solid;
 border:#0042A0 1px solid;
 background-image:url(/images/button.jpg);	
}
.btn3 {
BORDER-RIGHT: #7b9ebd 1px solid;
 PADDING-RIGHT: 5px;
 BORDER-TOP: #7b9ebd 1px solid; 
 PADDING-LEFT: 5px; FONT-SIZE: 12px; 
 BORDER-LEFT: #7b9ebd 1px solid; 
 CURSOR: hand; 
 COLOR: black; 
 PADDING-TOP: 22px; 
 PADDING-BOTTOM: 22px;
 BORDER-BOTTOM: #7b9ebd 1px solid;
 border:#0042A0 1px solid;
 background-image:url(/images/button.jpg);	
}
.btn4 {
 BORDER-RIGHT: #C0C0C0 1px solid;
 BORDER-TOP: #C0C0C0 1px solid; 
 PADDING-LEFT: 0px; FONT-SIZE: 12px; 
 BORDER-LEFT: #C0C0C0 1px solid; 
 COLOR: #808080; 
 PADDING-TOP: 0px; 
 PADDING-BOTTOM: 0px;
 BORDER-BOTTOM: #C0C0C0 1px solid;
  border:#0042A0 1px solid;
 background-image:url(/images/button.jpg);	
}
#strTable{
           border: 1px solid #eee;
           height: 140px; 
           width:99%;           
           overflow: auto;            
           margin: 1em 1;
           position:absolute;
}
</style>
<script language="JavaScript" src="./setstatecond.js"></script>
<html:form action="/org/autostatic/mainp/setstatecond">
<input type="hidden" name="itemid">
<table width="100%" border="0" align="center">
  <tr> 
    <td width="30%" valign="bottom">&nbsp;</td>
    <td width="10%" rowspan="4" align="center"> <table width="100%" border="0">
        <tr> 
          <td height="60" align="center"> <input type="button" name="Submit" value="<bean:message key='button.setfield.addfield'/>" onclick="additemtr('item_field');" Class="mybutton"> 
          </td>
        </tr>
        <tr> 
          <td height="70" align="center"> <input type="button" name="Submit2" onclick="delTableStr();" value="<bean:message key='kq.emp.change.emp.leave'/>" Class="mybutton"> 
          </td>
        </tr>
      </table></td>
    <td align="center">&nbsp; </td>
  </tr>
  <tr> 
    <td height="333" rowspan="3" align="center" valign="top">
     <fieldset style="width:100%;height: 333px;">
     <legend><bean:message key='selfservice.query.queryfield'/></legend>
     <table width="100%" border="0" align="center">
        <tr> 
          <td height="30"> <html:select name="setStateCondForm" property="fieldid" onchange="change();" style="width:100%"> 
            <html:optionsCollection property="setlist" value="dataValue" label="dataName" /> 
            </html:select> </td>
        </tr>
        <tr> 
          <td height="250" valign="top"> 
            <select name="item_field" multiple="multiple" ondblclick="additemtr('item_field');" style="height:275px;width:100%;font-size:9pt">
            </select></td>
        </tr>
      </table>
      </fieldset>
    </td>
    <td align="center" valign="top"> 
      <fieldset style="width:100%;">
      <legend><bean:message key='general.inform.search.condset'/></legend>
      <table width="100%" border="0">
        <tr> 
          <td height="160" valign="top"> 
            <div id="strTable" class="complex_border_color"> 
              <table width="100%" id="tablestr" border="0" cellspacing="0">
                <tr> 
                  <td align="center" class="TableRow" style="border-top:none;border-left: none;" width="15%"><bean:message key='label.serialnumber'/></td>
                  <td align="center" class="TableRow" style="border-top:none;border-left: none;" width="30%" nowrap><bean:message key='general.inform.search.item.object'/></td>
                  <td align="center" class="TableRow" style="border-top:none;border-left: none;" width="15%"><bean:message key='label.query.relation'/></td>
                  <td align="center" class="TableRow" style="border-top:none;border-left: none;border-right:none;"  nowrap><bean:message key='label.query.value'/></td>
                </tr>
              </table>
            </div></td>
        </tr>
      </table>
      </fieldset>
    </td>
  </tr>
  <tr> 
    <td height="100" colspan="2" valign="top">
    	<fieldset style="width:100%;">
      	<legend><bean:message key='kq.wizard.expre'/></legend>
      	<table width="100%" border="0">
    		<tr> 
    			<td height="70" valign="top" style="padding-left: 2px;"> 
      				<textarea name="cond" cols="61" onblur="setLogicArr();" rows="4"></textarea>
      			</td>
      		</tr>
      		<tr> 
    			<td height="20" valign="top" style="padding-left: 2px;"> 
      				<input name="button1" type="button" onclick="symbol('cond','(');setLogicArr();" class="mybutton"  value="&nbsp;(&nbsp;&nbsp;">
      				<input name="button2" type="button" onclick="symbol('cond','*');setLogicArr();" class="mybutton" value="&nbsp;<bean:message key='kq.formula.even'/>&nbsp;">
      				<input name="button2" type="button" onclick="symbol('cond','!');setLogicArr();" class="mybutton" value="&nbsp;<bean:message key='kq.formula.not'/>&nbsp;">
      				<input name="button3" type="button" onclick="symbol('cond',')');setLogicArr();" class="mybutton" value="&nbsp;&nbsp;)&nbsp;">
      				<input name="button4" type="button" onclick="symbol('cond','+');setLogicArr();" class="mybutton" value="&nbsp;<bean:message key='kq.formula.or'/>&nbsp;">
      			</td>
      		</tr>
    	</table>
    	</fieldset>
    </td>
  </tr>
</table>
<table width="100%" border="0" align="center" style="margin-top: -7px;">
<tr> 
    <td align="right">
    <input type="button" name="searchok" value="<bean:message key='button.ok'/>" onclick="checkCond();" Class="mybutton"> 
    </td>
    <td align="left">
    <input type="button" name="close" value="<bean:message key='button.cancel'/>" onclick="window.close();" Class="mybutton"> 
    </td>
    <!-- 
    <td height="25" width="50">&nbsp;</td>
    <td height="25" width="200">&nbsp;</td>
    <td height="25" >
    	<table width="100%" border="0">
    		<tr>
    			<td width="20%">
    				<input type="button" name="searchok" value="<bean:message key='button.ok'/>" onclick="checkCond();" Class="mybutton"> 
    			</td>
    			<td  width="20%">
      				<input type="button" name="close" value="<bean:message key='button.cancel'/>" onclick="window.close();" Class="mybutton"> 
      			</td>
      			<td>&nbsp;</td>
      	</table>
    </td>
     -->
  </tr>
</table>
<div id="date_panel" style="display:none">
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
<script language="JavaScript">
change();
editTable("${setStateCondForm.tablestr}");
</script>
</html:form>
