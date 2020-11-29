<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<hrms:themes></hrms:themes>
<style type="text/css"> 
#scroll_box {
    border: 1px solid #eee;
    height: 370px;    
    width: 220px;            
    overflow: auto;            
    margin: 1em 0;
}
.btn1 {
 BORDER-RIGHT: 1px solid;
 PADDING-RIGHT: 5px; 
 BORDER-TOP:  1px solid; 
 PADDING-LEFT: 6px; FONT-SIZE: 12px; 
 BORDER-LEFT: 1px solid; 
 CURSOR: hand; 
 COLOR: black; 
 PADDING-TOP: 1px; 
 PADDING-BOTTOM: 0px;
 BORDER-BOTTOM:  1px solid;
 border: 1px solid;
}
.btn2 {
 BORDER-RIGHT:  1px solid;
 PADDING-RIGHT: 1px; 
 BORDER-TOP: 1px solid; 
 PADDING-LEFT: 1px; FONT-SIZE: 12px; 
 BORDER-LEFT:  1px solid; 
 CURSOR: hand; 
 COLOR: black; 
 PADDING-TOP: 1px; 
 PADDING-BOTTOM: 0px;
 BORDER-BOTTOM: 1px solid;
 border: 1px solid;
}
.btn3 {
 BORDER-RIGHT:  1px solid;
 PADDING-RIGHT: 6px; 
 BORDER-TOP:  1px solid; 
 PADDING-LEFT: 6px; FONT-SIZE: 12px; 
 BORDER-LEFT:  1px solid; 
 CURSOR: hand; 
 COLOR: black; 
 PADDING-TOP: 1px; 
 PADDING-BOTTOM: 0px;
 BORDER-BOTTOM:  1px solid;
 border: 1px solid;
}
.btn {
 BORDER-RIGHT: #C0C0C0 1px solid;
 BORDER-TOP: #C0C0C0 1px solid; 
 PADDING-LEFT: 1px;
 PADDING-RIGHT: 1px;
 FONT-SIZE: 12px; 
 BORDER-LEFT: #C0C0C0 1px solid; 
 COLOR: #808080; 
 BORDER-BOTTOM: #C0C0C0 1px solid
}
.hcmbtn {
    border:#C0C0C0 1px solid; 
    height:25px;
    width:35px;
    line-height:20px;
    color:#808080; 
}
</style>
<%UserView userView=(UserView)session.getAttribute(WebConstant.userView);
String classname= "btn";
if("hcm".equals(userView.getBosflag()))
    classname= "hcmbtn";
%>

<script language="JavaScript" src="/general/inform/informcheck/setformula.js"></script>
<html:form action="/general/inform/informcheck/setformula">
<input type="hidden" name="itemid">
<table width="100%" border="0">
  <tr> 
    <td width="33%" height="400" rowspan="3" valign="top">
    	<fieldset style="width:100%;height:485">
    	<legend><bean:message key="gz.formula.list.table"/></legend>
		<table width="100%" align="center" border="0">
        	<tr> 
          		<td height="400"><div id="scroll_box" style="margin-left:15px;">${auditForm.formulastr}</div></td>
        	</tr>
      	</table>
      	</fieldset>
	</td>
    <td width="56%" height="70">
    	<fieldset style="width:100%;height:60">
    	<legend><bean:message key="inform.inforcheck.review.item"/></legend> 
      	<table width="100%" border="0">
        <tr>
          <td  width="55" height="30" align="right"><bean:message key="label.zp_options.subset"/>
          </td>
          <td>
          	<html:select name="auditForm" property="fieldid" onchange="change();" style="width:300;font-size:9pt">
    			<html:optionsCollection property="fieldlist" value="dataValue" label="dataName" />
 			</html:select> 
          </td>
        </tr>
        <tr>
          <td height="30" align="right"><bean:message key="kq.wizard.target"/>
          </td>
          <td>
          	<select name="itemid_arr" onchange="changeItem(this);viewSaveButton();" style="width:300;font-size:9pt">
             </select>
          </td>
        </tr>
      </table>
      </fieldset>
	</td>
    <td width="11%" rowspan="3" valign="top">
      <table width="100%" border="0">
        <tr> 
          <td height="40" align="center"> 
            <input type="button" name="button1" value="<bean:message key='button.sys.warn.guide'/>" onclick='function_Wizard("${auditForm.infor}");' Class="mybutton">
          </td>
        </tr>
        <tr>
          <td height="40" align="center">
			<input type="button" name="button12" value="<bean:message key='kq.shift.cycle.add'/>" onclick="addTable();" Class="mybutton">
          </td>
        </tr>
        <tr>
          <td height="40" align="center">
			<input type="button" name="button13" value="<bean:message key='kq.shift.cycle.del'/>" onclick="delItemTable();" Class="mybutton">
          </td>
        </tr>
        <tr>
          <td height="40" align="center">
          	<div id="viewbutton" style="display:none">
				<input name="button14" type="button" value="<bean:message key='options.save'/>" onclick="saveFormula();" Class="mybutton">
			</div>
			<div id="hidebutton">
				<input style="margin-left: -6px;" name="button143" type="button" value="<bean:message key='options.save'/>" Class="<%=classname %>">
			</div>
          </td>
        </tr>
        <tr>
          <td height="80">&nbsp;</td>
        </tr>
        <tr>
          <td height="80" align="center">
          	<input name="button142" type="button" value="<bean:message key='lable.welcomeboard.close'/>" onclick="closeFormula();" Class="mybutton">
          </td>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td height="220">
    	<fieldset style="width:100%;height:210">
    	<legend><bean:message key='inform.inforcheck.review.conditions'/></legend> 
		<table width="100%" height="210" border="0">
        	<tr> 
          	<td height="200">
          		<html:textarea name="auditForm" property="formula" onkeydown="viewSaveButton();" onclick="this.pos=document.selection.createRange();" cols="60" rows="13"></html:textarea>  
          	</td>
        	</tr>
      	</table>
      	</fieldset>
	 </td>
  </tr>
  <tr>
    <td height="100">
    	<table width="100%" border="0">
        <tr> 
          <td width="65%" height="90">
          	<fieldset style="width:100%;height:95">
    		<legend><bean:message key='org.maip.reference.projects'/></legend> 
          	<table width="100%" border="0">
          		<tr>
          			<td width="55" align="right"><bean:message key='label.zp_options.subset'/>
          			</td>
          			<td>
          			<html:select name="auditForm" property="field" onchange="changeField();" style="width:180;font-size:9pt">
    					<html:optionsCollection property="listfield" value="dataValue" label="dataName" />
 					</html:select> 
          			</td>
          		</tr>
          		<tr>
          			<td align="right"><bean:message key='kq.wizard.target'/>
          			</td>
          			<td>
          				<select name="itemarr" onchange="changeCodeValue();" style="width:180;font-size:9pt">
             			</select>
          			</td>
          		</tr>
          		<tr id="viewcode" style="display:none">
          			<td align="right"><bean:message key='conlumn.codeitemid.caption'/>
          			</td>
          			<td>
          				<select name="codearr" onchange="getCodesid();" style="width:180;font-size:9pt">
             			</select>
          			</td>
          		</tr>
          	</table>
          	</fieldset>
          </td>
           <td>
          	<fieldset style="width:100%;height:95">
    		<legend><bean:message key='gz.formula.operational.symbol'/></legend> 
          	<table width="100%" border="0">
          		<tr>
          			<td>
          				<table width="100%" border="0">
          					<tr>
          						<td align="center">
          							<input type="button"  value="<bean:message key='general.mess.and'/>" onclick='symbol(" <bean:message key='general.mess.and'/> ")' class="btn2 smallbutton">
          							<input type="button"  value="<bean:message key='general.mess.or'/>" onclick='symbol(" <bean:message key='general.mess.or'/> ")' class="btn2 smallbutton">
          							<input type="button"  value="<bean:message key='kq.wizard.not'/>" onclick='symbol(" <bean:message key='kq.wizard.not'/> ")' class="btn2 smallbutton">
          							<input type="button"  value="<bean:message key='gz.formula.if'/>" onclick='symbol(" <bean:message key='gz.formula.if'/> ")'  class="btn2 smallbutton">
          						</td>
          					</tr>
          					<tr>
          						<td align="center">
          							<input type="button"  value="<bean:message key='kq.wizard.thing'/>" onclick='symbol(" <bean:message key='kq.wizard.thing'/> ")' class="btn1 smallbutton">
          							<input type="button"  value="<bean:message key='kq.formula.fou'/>" onclick='symbol(" <bean:message key='kq.formula.fou'/> ")' class="btn2 smallbutton">
          						</td>
          					</tr>
          					<tr>
          						<td align="center">
          							<input type="button"  value="<bean:message key='kq.formula.then'/>" onclick='symbol(" <bean:message key='kq.formula.then'/> ")' class="btn3 smallbutton">
          							<input type="button"  value="<bean:message key='kq.formula.end'/>" onclick='symbol(" <bean:message key='kq.formula.end'/> ")' class="btn3 smallbutton">
          						</td>
          					</tr>
          				</table>
          			</td>
          		</tr>
          	</table>
          	</fieldset>
          </td>
        </tr>
      </table>
     </td>
  </tr>
</table>
</html:form>
<script language="JavaScript">
initArr("${auditForm.formulaarr}","${auditForm.itemidarr}");
window.onbeforeunload = function() { 
	var n = window.event.screenX - window.screenLeft; 
	var b = n > document.documentElement.scrollWidth-20; 
	if(b && window.event.clientY < 0 || window.event.altKey) {
		window.returnValue="111";
		window.close();
	}
}  
</script>