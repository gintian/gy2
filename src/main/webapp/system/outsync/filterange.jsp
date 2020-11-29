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
<hrms:themes></hrms:themes>
<script language="JavaScript" src="./filterange.js"></script>
<html:form action="/system/outsync/filterange">
<input type="hidden" name="itemid" id="itemid">
<table width="690" border="0" align="center" cellspacing="0" cellpadding="0">
  <tr> 
    <td height="270px;" width="200px;" align="left" valign="top"> 
      <fieldset style="width:200px;">
      <legend><bean:message key='selfservice.query.queryfield'/></legend>
      <table width="100%" border="0" align="center">
       <!-- <tr> 
          <td height="27"> 
          <html:select name="outsyncFrom" styleId="fieldid" property="type" onchange="change();" style="width:100%"> 
            <html:optionsCollection property="setlist" value="dataValue" label="dataName" /> 
            </html:select> 
            </td>
        </tr>
         --> 
        <tr> 
          <td height="257"> 
            <html:select name="outsyncFrom" multiple="multiple" styleId="item_field" property="type" ondblclick="additemtr('item_field');" style="height:247px;width:100%;font-size:9pt"> 
            	<html:optionsCollection property="setlist" value="dataValue" label="dataName" /> 
            </html:select> 
          </td>
        </tr>
      </table>
     </fieldset>
    </td>
    <td width="70px;" rowspan="2" align="left" valign="middle">
    	<table width="70px;" border="0">
        	<tr> 
          		<td height="60" align="center"> 
            		<input type="button" name="Submit111" value="<bean:message key='button.setfield.addfield'/>" onclick="additemtr('item_field');" Class="smallbutton"> 
          		</td>
        	</tr>
        	<tr> 
          		<td height="70" align="center"> 
            		<input type="button" name="Submit2" onclick="delTableStr();" value="<bean:message key='button.delete'/>" Class="smallbutton"> 
          		</td>
        	</tr>
      </table>
   </td>
   <td align="center" height="270px" valign="top">
   		<fieldset>
      	<legend><bean:message key='general.inform.search.condset'/></legend>
    	<table width="420px;" border="0" cellpadding="0" cellspacing="0">
        	<tr> 
         		<td height="260" valign="top" align="left">
         			<div id="strTable" class="common_border_color" style="width:410;margin-left:5px;">
         				<table width="408px;" id="tablestr" border="0" class="ListTableF" style="border:none;" cellpadding="0" cellspacing="0">
         					<tr><!-- 【7694】系统管理/应用设置/数据交换/数据视图,外部系统配置,过滤范围界面框线颜色不对 jingq upd 2015.02.25 -->
         						<td align="center" class="TableRow" width="15%" style="border-left:none;border-top:none;"><bean:message key='label.query.logic'/></td>
         						<td align="center" class="TableRow" width="30%" nowrap style="border-top:none;"><bean:message key='general.inform.search.item.object'/></td>
         						<td align="center" class="TableRow" width="15%" style="border-top:none;"><bean:message key='label.query.relation'/></td>
         						<td align="center" class="TableRow" nowrap style="border-right:none;border-top:none;"><bean:message key='label.query.value'/></td>
         					</tr>
         				</table>
         			</div>
       				<logic:iterate id="jsscrpt" name="outsyncFrom" property="htmllist">
       					<script type="text/javascript">
       						${jsscrpt}
       					</script>
       				</logic:iterate>
         		</td>
        	</tr>
      	</table>
      	</fieldset>
   </td>
  </tr>
  <tr width="420"> 
      <td height="25" width="50%" align="right">
    	<table width="100%" border="0">
    		<tr>
    			<td width="300" align="right">
    				<logic:equal value="1" name="outsyncFrom" property="like">
    					<input type="checkbox" name="like" checked="checked"><bean:message key='label.query.like'/>
    				</logic:equal>
    				<logic:notEqual value="1" name="outsyncFrom" property="like">
	    				<input type="checkbox" name="like"><bean:message key='label.query.like'/>
    				</logic:notEqual>
    			</td>
    		</tr>
    	</table>
      </td>
      <td height="35" align="center">
    	<table width="300" border="0">
    		<tr>
    			<td width="25%"><input type="button" name="searchok" value="<bean:message key='button.query'/>" onclick='searchSetCond("${outsyncFrom.type}","${outsyncFrom.other_param }");' Class="mybutton"></td>
    			<td width="25%"><input type="button" name="close" value="<bean:message key='button.cancel'/>" onclick="winclose();" Class="mybutton"></td>
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
//change();
Element.hide('date_panel');
//关闭弹窗方法  wangb 20190320
function winclose(){
	if(parent.Ext && parent.Ext.getCmp('filterange')){
		var win = parent.Ext.getCmp('filterange');
		win.close();
		return;
	}
	window.close();
}
</script>