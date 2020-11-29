<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hjsj.hrms.actionform.general.inform.search.SearchInformForm"%>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String manager=userView.getManagePrivCodeValue(); 
	SearchInformForm form=(SearchInformForm)session.getAttribute("searchInformForm");
    /**
    * 由先前的按人员管理范围控制改成按如下规则进行控制：
    * 人员、单位和岗位按业务范围-操作单位-人员管理范围优先级进行控制 
    * cmq changed at 2012-09-29
    */	
	if(form.getType().equalsIgnoreCase("1")||form.getType().equalsIgnoreCase("2")||form.getType().equalsIgnoreCase("3"))
	{
		manager=userView.getUnitIdByBusi("4");
	}
	//end.	
	String oper = (String)request.getParameter("oper");//oper='bonus'表示奖金管理调用
	String bzsearch = (String)request.getParameter("bzsearch");
	
	String moduleFlag=(String)request.getParameter("moduleFlag");
	if(moduleFlag==null){
		moduleFlag="";
	}
%>
<script language="javascript" src="/module/utils/js/template.js"></script>
<script type="text/javascript">
	var manageCode = '<%=manager%>';
	var moduleFlag = '<%=moduleFlag%>';
</script>
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
.strTable{
	border: 1px solid #eee;
	height: 140px;    
	width: 400px;            
	overflow: auto;            
	margin: 1em 1;
	position:absolute;
	/*BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;*/
}
</style>
<script language="JavaScript" src="./gmsearch.js"></script>
<html:form action="/general/inform/search/gmsearch">
<div id="divid" style="display: true;">
<input type="hidden" name="itemid">
<table width="100%" border="0" align="center">
  <tr> 
    <td width="30%" valign="bottom">&nbsp;<html:hidden name="searchInformForm" property="privflag" styleId="pflag"/></td>
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
    <td height="300px" rowspan="3" align="center">
     <fieldset style="width:100%;height: 100%">
     <legend><bean:message key='selfservice.query.queryfield'/></legend>
     <table width="100%" border="0" align="center">
        <tr> 
          <td height="30" style="padding-top: 13px;">
          <logic:equal value="0" name="searchInformForm" property="fieldSetId">
          <html:select name="searchInformForm" property="fieldid" onchange="change();" style="width:100%"> 
            <html:optionsCollection property="setlist" value="dataValue" label="dataName" /> 
            </html:select> 
            </logic:equal>
            <logic:notEqual value="0" name="searchInformForm" property="fieldSetId">
             <html:select name="searchInformForm" property="fieldid" onchange="change();" style="width:100%;display=none"> 
            <html:optionsCollection property="setlist" value="dataValue" label="dataName" /> 
            </html:select> 
            子集：<bean:write name="searchInformForm" property="fieldSetDesc"/>
            </logic:notEqual>
            </td>
        </tr>
        <tr> 
          <td height="250" valign="top"> 
            <select name="item_field" multiple="multiple" ondblclick="additemtr('item_field');" style="height:230px;width:100%;font-size:9pt">
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
		    <div id="strTable" class="strTable common_border_color">
              <table width="100%" id="tablestr" border="0" class="ListTableF" style="border: 0px">
                <tr> 
                  <td align="center" class="TableRow" width="15%" style="border-left:0px;border-top:0px;"><bean:message key='label.serialnumber'/></td>
                  <td align="center" class="TableRow" width="30%" style="border-top:0px;" nowrap><bean:message key='general.inform.search.item.object'/></td>
                  <td align="center" class="TableRow" width="15%" style="border-top:0px;"><bean:message key='label.query.relation'/></td>
                  <td align="center" class="TableRow" nowrap style="border-right:0px;border-top:0px;"><bean:message key='label.query.value'/></td>
                </tr>
              </table>
            </div></td>
        </tr>
      </table>
      </fieldset>
    </td>
  </tr>
  <tr> 
    <td height="100" colspan="2" valign="top" align="center">
    	<fieldset style="width:100%;">
      	<legend><bean:message key='kq.wizard.expre'/></legend>
      	<table width="100%" border="0">
    		<tr> 
    			<td height="70" valign="top"> 
      				<textarea name="cond" cols="66" onblur="setLogicArr();" rows="4"></textarea>
      			</td>
      		</tr>
      		<tr> 
    			<td height="20" valign="top"> 
      				<input name="button1" type="button" onclick="symbol('cond','(');setLogicArr();" class="mybutton"  value="&nbsp;(&nbsp;&nbsp;">
      				<input name="button3" type="button" onclick="symbol('cond',')');setLogicArr();" class="mybutton" value="&nbsp;&nbsp;)&nbsp;">
      				<input name="button2" type="button" onclick="symbol('cond','*');setLogicArr();" class="mybutton" value="&nbsp;且&nbsp;">
      				<input name="button4" type="button" onclick="symbol('cond','+');setLogicArr();" class="mybutton" value="&nbsp;或&nbsp;">
      				<input name="button2" type="button" onclick="symbol('cond','!');setLogicArr();" class="mybutton" value="&nbsp;非&nbsp;">
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
    		<logic:equal value="0" name="searchInformForm" property="fieldSetId">
    			<td width="80"><input type="checkbox" name="like"><bean:message key='label.query.like'/></td>
    			<td id="viewHistory" style="display:none">
    			   <% if(bzsearch==null){%>
    				<input type="checkbox" name="history"><bean:message key='label.query.history'/>
    				<%} %>
    			</td>
    			</logic:equal>
    		</tr>
      	</table>
    </td>
    <td height="25" width="200">
    	<table width="100%" border="0">
    		<tr>
    			<td>&nbsp;</td>
      			<logic:equal name="searchInformForm" property="type" value="2">
      			<td><input type="radio" name="unite" value="1"><bean:message key='label.query.dept'/></td>
    			<td><input type="radio" name="unite" value="0"><bean:message key='label.query.org'/></td>
    			<td><input type="radio" name="unite" value="2" checked><bean:message key='label.query.all'/></td>
      			</logic:equal>
      		</tr>
    	</table>
     </td>
    <td height="25" >
    	<table width="100%" border="0">
    		<tr>
    		<html:hidden name="searchInformForm" property="fieldSetId" styleId="fsid" />
    			<logic:notEqual name="searchInformForm" property="ps_flag" value="1">
    			<td width="20%">
    				<input type="button" name="searchok" value="<bean:message key='button.query'/>" onclick='checkCond(1,"${searchInformForm.a_code}","${searchInformForm.tablename}","${searchInformForm.type}","${searchInformForm.checkflag}");' Class="mybutton">
    			</td>
    			<td  width="20%">
      				<input type="button" name="close" value="<bean:message key='button.close'/>" onclick="winclose();" Class="mybutton"> 
      			</td>
      			</logic:notEqual>      			
      			<logic:equal name="searchInformForm" property="ps_flag" value="1">
      			<td  width="20%">
      				<input type="button" name="close" value="<bean:message key='button.close'/>" onclick="ps_close();" Class="mybutton"> 
      			</td>
      			</logic:equal>
      			<td>&nbsp;</td>
      	</table>
    </td>
  </tr>
  <tr id="lert"><td colspan="5" align="left">提示：字符型、代码型指标可使用通配符 "*" 或 "?" 辅助查询</td></tr>
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
</div>
<iframe src="" id="iframeid" border="0" frameborder="0" style="display: none; width: 100%; height: 405px;"></iframe>
</html:form>
<script language="JavaScript">
<%
	if(oper!=null && oper.equalsIgnoreCase("bonus")){
%>
change('bonusinfo');
<%
}else{
%>
change();
<%
}
%>
Element.hide('date_panel');
editTable("${searchInformForm.tablestr}");
	//if(getBrowseVersion() == 10 || !getBrowseVersion()){//非ie兼容模式下   样式修改  wangb 20190318
		var divid = document.getElementById('divid');
		var table2 = divid.getElementsByTagName('table')[0].getElementsByTagName('table')[0];
		table2.style.marginLeft = '16px';
		var fieldset = document.getElementsByTagName('fieldset');
		fieldset[1].style.width='95%';
		fieldset[2].style.width='95%';
	//}
//关闭弹窗  wangb 20190319
function winclose(){
	if(parent.parent.Ext)
		parent.parent.Ext.getCmp('simple_query').close();
	else
		window.close();
}
</script>