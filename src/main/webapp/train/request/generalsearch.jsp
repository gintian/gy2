<%@ page contentType="text/html; charset=UTF-8"%>
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
<script language="JavaScript" src="/train/request/generalsearch.js"></script>
<html:form action="/general/inform/search/generalsearch">
<input type="hidden" name="itemid">
<html:hidden  name="searchInformForm" property="sexpr" />
<html:hidden  name="searchInformForm" property="sfactor" />
<table width="100%" border="0" align="center">
  <tr> 
    <td width="30%" valign="bottom">&nbsp;</td>
    <td width="10%" rowspan="2" align="center">
    	<table width="100%" border="0">
        	<tr> 
          		<td height="60" align="center"> 
            		<input type="button" name="Submit111" value="<bean:message key='button.setfield.addfield'/>" onclick="additemtr('item_field');" Class="mybutton"> 
          		</td>
        	</tr>
        	<tr> 
          		<td height="70" align="center"> 
            		<input type="button" name="Submit2" onclick="delTableStr();" value="<bean:message key='button.delete'/>" Class="mybutton"> 
          		</td>
        	</tr>
      </table>
    </td>
    <td width="60%" align="center">&nbsp; </td>
  </tr>
  <tr> 
    <td height="270" align="center"> 
      <fieldset style="width:100%;">
      <legend><bean:message key='selfservice.query.queryfield'/></legend>
      <table width="100%" border="0" align="center">
        <tr> 
          <td height="27"> <html:select name="searchInformForm" property="fieldid" onchange="change();" style="width:100%"> 
            <html:optionsCollection property="setlist" value="dataValue" label="dataName" /> 
            </html:select> </td>
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
<table width="100%" border="0" align="center">
	<tr> 
      <td height="25" width="200">
    	<table width="100%" border="0">
    		<tr>
    			<td width="80"><input type="checkbox" name="like"><bean:message key='label.query.like'/></td>
    			<td id="viewHistory" style="display:none"><input type="checkbox" name="history"><bean:message key='label.query.history'/></td>
    		</tr>
    	</table>
      </td>
      <td height="25" width="200">
    	<table width="100%" border="0">
    		<tr>
    			<td>&nbsp;</td>
      		</tr>
    	</table>
      </td>
      <td height="25">
    	<table width="100%" border="0">
    		<tr>
    			<td width="25%"><input type="button" value="<bean:message key='kq.formula.true'/>" onclick="searchSetCond();" class="mybutton"></td>
    			<td width="25%"><input type="button" name="close" value="<bean:message key='button.cancel'/>" onclick="window.close();" Class="mybutton"></td>
    			<td>&nbsp;</td>
    		</tr>
    	</table>
      </td>
  </tr>
</table>
<div id="date_panel" style="display:none;">
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
</script>