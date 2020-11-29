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
	height: 160px;
	width: 400px;
	overflow: auto;
	margin: 0 0 0 0;
	padding:0;
	position: absolute;
}
</style>
<script language="JavaScript" src="./filter.js"></script>
<html:form action="/gz/gz_analyse/filter">
<input type="hidden" name="itemid" id="itemid">
<table width="100%" border="0" align="center">
  <tr> 
    <td height="380" rowspan="3" align="center" valign="top" width="30%">
     <fieldset style="width:100%;height: 370;">
     <legend><bean:message key='selfservice.query.queryfield'/></legend>
     <table width="100%" border="0" align="center">
        <tr> 
          <td height="30">
            <html:select name="gzFilterForm" multiple="multiple" property="salaryitemid" ondblclick="additemtr('salaryitemid');" style="height:335px;width:100%;font-size:9pt"> 
            	<html:optionsCollection property="salaryitemlist" value="dataValue" label="dataName" /> 
            </html:select>
          </td>
        </tr>
      </table>
      </fieldset>
    </td>
    <td width="10%" rowspan="4" align="center"> 
    	<table width="100%" border="0" style="padding-left: 30px;">
        <tr> 
          <td height="60" align="center"> <input type="button" name="Submit" value="<bean:message key='button.setfield.addfield'/>" onclick="additemtr('salaryitemid');" Class="mybutton"> 
          </td>
        </tr>
        <tr> 
          <td height="70" align="center"> <input type="button" name="Submit2" onclick="delTableStr();" value="<bean:message key='kq.emp.change.emp.leave'/>" Class="mybutton"> 
          </td>
        </tr>
      </table>
    </td>
    <td align="center" valign="top" width="60%"> 
      <fieldset style="width:95%;height: 100%">
      <legend><bean:message key='general.inform.search.condset'/></legend>
      <table width="100%" border="0">
        <tr> 
          <td height="190" valign="top"> 
            <div id="strTable" class="strTable RecordRow"> 
              <table width="100%" id="tablestr" border="0" class="ListTable">
                <tr> 
                  <td align="center" class="TableRow noleft" width="10%" nowrap style="border-top:0px;"><bean:message key='label.serialnumber'/></td>
                  <td align="center" class="TableRow" width="37%" nowrap style="border-top:0px;"><bean:message key='general.inform.search.item.object'/></td>
                  <td align="center" class="TableRow" width="10%" nowrap style="border-top:0px;"><bean:message key='label.query.relation'/></td>
                  <td align="center" class="TableRow" nowrap style="border-top:0px;"><bean:message key='label.query.value'/></td>
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
    	<fieldset style="width:95%;">
      	<legend><bean:message key='kq.wizard.expre'/></legend>
      	<table width="100%" border="0">
    		<tr> 
    			<td height="70" valign="top"> 
      				<textarea name="cond" id="cond" cols="63" onblur="setLogicArr();" rows="4">${gzFilterForm.expr}</textarea>
      			</td>
      		</tr>
      		<tr>
      			<td height="20" align="left">
      				<table border="0">
      					<tr>
      						<td><input name="button1" type="button" onclick="symbol('cond','(');setLogicArr();" class="mybutton"  value="(&nbsp;"></td>
      						<td><input name="button3" type="button" onclick="symbol('cond',')');setLogicArr();" class="mybutton" value="&nbsp;)"></td>
      						<td><input name="button2" type="button" onclick="symbol('cond','*');setLogicArr();" class="mybutton" value="且"></td>
      						<td><input name="button2" type="button" onclick="symbol('cond','!');setLogicArr();" class="mybutton" value="非"></td>
      						<td><input name="button4" type="button" onclick="symbol('cond','+');setLogicArr();" class="mybutton" value="或"></td>
      					</tr>
      				</table>
      			</td>
      		</tr>
    	</table>
    	</fieldset>
    </td>
  </tr>
</table>
<table width="100%" border="0" align="center">
<tr> 
    <td height="25" align="center">
    	
    				<input type="button" name="searchok" onclick="searchSetCond('${gzFilterForm.flag}','${gzFilterForm.tabID}','${gzFilterForm.seiveid}','${gzFilterForm.name}');" value="<bean:message key='button.ok'/>" Class="mybutton"> 
    			&nbsp;
      				<input type="button" name="close" value="<bean:message key='button.close'/>" onclick="parent.window.close();"  Class="mybutton"> 
      
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
Element.hide('date_panel');
editTable('${gzFilterForm.factor}');
</script>