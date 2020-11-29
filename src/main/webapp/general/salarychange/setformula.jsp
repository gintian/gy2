<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>


<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="./formulatemp.js"></script>
<script type="text/javascript" src="/js/function.js"></script>
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
.itemtable {
           border: 1px solid #eee;
           height: 415px;    
           width: 230px;            
           overflow: auto;            
           margin: 1 -3;
           position:absolute;
           
}
</style>
<%

    UserView userView = (UserView) session.getAttribute(WebConstant.userView);
    String bosflag = "";
    if (userView != null) {
        bosflag = userView.getBosflag();
    }
%>
 <%
     if ("hcm".equals(bosflag)) {
 %>
<link href="/general/template/template.css" rel="stylesheet" type="text/css"/>
<%
    }
%>
<script type="text/javascript">
<!--
function setTPinput(){
    var InputObject=document.getElementsByTagName("input");
    for(var i=0;i<InputObject.length;i++){
        var InputType=InputObject[i].getAttribute("type");
        if(InputType!=null&&(InputType=="text"||InputType=="password")){
            InputObject[i].className=" "+"TEXT4";
        }
    }
}
//-->
</script>
<hrms:themes></hrms:themes>
<body onload="setTPinput()">
<html:form action="/general/salarychange/setformula">
<html:hidden name="setFormulaForm" property="cfactor"/>
<html:hidden name="setFormulaForm" property="id"/>
<html:hidden name="setFormulaForm" property="tableid"/>
<html:hidden name="setFormulaForm" property="item"/>
<html:hidden name="setFormulaForm" property="itemids"/>
<html:hidden name="setFormulaForm" property="chz"/>
<html:hidden name="setFormulaForm" property="chz_arr"/>
<html:hidden name="setFormulaForm" property="affteritem_arr"/>

<table width="743"  border="0" align="center" >
<tr>
<td align="center">  
<fieldset align="center" style="width:100%;">
<legend><bean:message key='kq.item.count'/></legend>
<table width="100%" height="390" border="0" align="center">
  <tr> 
    <td width="40%" height="385" align="center">
    <fieldset align="center" style="width:290;height: 472px;"> <!-- 将两个fieldset高度改为一样高 xiaoyun 2014-9-1 -->
	<legend><bean:message key='org.maip.comp.project'/></legend> 
      <table width="100%" height="450px"  cellSpacing=0 cellPadding=0 border="0">
        <tr > 
          <td width="15%">
          	<table width="100%" border="0">          	
          		<tr>
          		<td align="center">
          		<input type="button" value="<bean:message key='kq.shift.cycle.up'/>" onclick="upSort();" Class="mybutton" Style="margin-bottom: 30px;">
          		<br>
          		<input type="button" value="<bean:message key='kq.shift.cycle.down'/>" onclick="downSort();"   Class="mybutton">
          		</td><tr>
          	</table>
          </td>
          <td valign="top" >
          	<div id="itemtable" class="itemtable common_border_color">
          	${setFormulaForm.itemtable}
          	</div>
          </td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td height="30" align='center' > 
            <input  type="button"  value="<bean:message key='button.new.add'/>" onclick="addTable();"  Class="mybutton" >  
            <input  type="button"  value="<bean:message key='button.delete'/>" onclick="delItemTable();"  Class="mybutton"> 
            </td>
        </tr>
      </table> 
      </fieldset>
    </td>
    <td width="60%" align="center" height="450">
    <table border="0" align="center">
    <tr><td>
    <fieldset align="center" style="width:430;height: 100%;">
	<legend><bean:message key='kq.item.count'/></legend> 
		<table width="100%" height="450px;" border="0">
        	<tr> 
          		<td colspan="1" align="center"> 
            		<html:textarea name="setFormulaForm" property="formula" cols="63" rows="12"></html:textarea> 
            	</td>
        	</tr>
        	<tr> 
          		<td height="21" colspan="1" align="right">
          			<input name="wizard" type="button" id="wizard" value='<bean:message key="kq.formula.function"/>' onclick='function_Wizard("${setFormulaForm.tableid}","formula");alertFormula();' Class="mybutton"> 
            		<input type="button" value="<bean:message key='gz.formula.calculation.conditions'/>" onclick="condiTions();" Class="mybutton"> 
            		<input type="button" value="<bean:message key='performance.workdiary.check.formula'/>" Class="mybutton" onclick="checkCurFormula('check');">&nbsp; 
            	</td>
        	</tr>
        	<tr> 
          		
          		<td width="100%" align="center"> 
          		 <fieldset  align="center" style="width:96%;height=80">
				 <legend><bean:message key='org.maip.reference.projects'/></legend> 
            		<table width="100%" border="0">
            			<tr height="30">
            				<td>
            					<table width="100%"  border="0" >
              						<tr> 
                						<td height="30"><bean:message key='menu.field'/>
											<html:select name="setFormulaForm" property="itemid" onchange="changeCodeValue('formula');alertFormula();" style="width:240;font-size:9pt;">
			 									<html:optionsCollection property="itemlist" value="dataValue" label="dataName" />
											</html:select>
                 						</td>
              						</tr>
            					</table>
            				</td>
            			</tr>
            			<tr height="30">
            				<td>
            					<span id="codeview">
            					<table width="100%" border="0" >
              						<tr> 
                						<td><bean:message key="codemaintence.codeitem.id"/>
											<select name="codesetid_arr"  onchange="getCodesid('formula');alertFormula();"  style="font-size:9pt;width:240px;">
             								</select>
                 						</td>
              						</tr>
            					</table>
            				 </span>
            				</td>
            			</tr>
            		</table>
            		</fieldset>
          		</td>
          	</tr>
          	<tr>	
          		<td width="100%">
          		<fieldset align="center" style="width:96%;">
				 <legend><bean:message key="gz.formula.operational.symbol"/></legend> 
					<table width="80%" border="0">
              			<tr> 
              				<td>
              				<table width="100%" border="0">
              				
              				<tr>
                				<td valign="middle"><input type="button"  value="0" onclick="symbol('formula',0);alertFormula();" class="btn2 common_btn_bg" style="margin-top: -1px;"></td>
                				<td valign="middle"><input type="button"  value="1" onclick="symbol('formula',1);alertFormula();" class="btn2 common_btn_bg"> </td>
                				<td valign="middle"><input type="button"  value="2" onclick="symbol('formula',2);alertFormula();" class="btn2 common_btn_bg"> </td>
                				<td valign="middle"><input type="button"  value="3" onclick="symbol('formula',3);alertFormula();" class="btn2 common_btn_bg"> </td>
                				<td valign="middle"><input type="button"  value="4" onclick="symbol('formula',4);alertFormula();" class="btn2 common_btn_bg"> </td>
                				
                				<td valign="middle"><input type="button"  value="5" onclick="symbol('formula',5);alertFormula();" class="btn2 common_btn_bg"> </td>
                				<td valign="middle"><input type="button"  value="6" onclick="symbol('formula',6);alertFormula();" class="btn2 common_btn_bg"> </td>
                				<td valign="middle"><input type="button"  value="7" onclick="symbol('formula',7);alertFormula();" class="btn2 common_btn_bg"> </td>
                				<td valign="middle"><input type="button"  value="8" onclick="symbol('formula',8);alertFormula();" class="btn2 common_btn_bg"> </td>
                				<td valign="middle"><input type="button"  value="9" onclick="symbol('formula',9);alertFormula();" class="btn2 common_btn_bg"> </td>
              					<td valign="middle"><input type="button"  value="(" onclick="symbol('formula','(');alertFormula();" class="btn2 common_btn_bg"> </td>
              					<td valign="middle"><input type="button"  value=")" onclick="symbol('formula',')');alertFormula();" class="btn2 common_btn_bg"> </td>
              					<td valign="middle" colspan='2' ><input type="button"  value="<bean:message key='gz.formula.if'/>" onclick="symbol('formula','<bean:message key='gz.formula.if'/>');alertFormula();" class="btn3 common_btn_bg" style="margin-top: -1px;"></td>
                				<td valign="middle" colspan='2' ><input type="button"  value="<bean:message key='gz.formula.else'/>" onclick="symbol('formula','<bean:message key='gz.formula.else'/>');alertFormula();" class="btn3 common_btn_bg" style="margin-top: -1px;"></td>
              				</tr>
         
              				<tr> 
                				<td valign="middle"><input type="button"  value="+" onclick="symbol('formula','+');alertFormula();" class="btn2 common_btn_bg"> </td>
                				<td valign="middle"><input type="button"  value="-" onclick="symbol('formula','-');alertFormula();" class="btn2 common_btn_bg"> </td>
                				<td valign="middle"><input type="button"  value="*" onclick="symbol('formula','*');alertFormula();" class="btn2 common_btn_bg"> </td>
                				<td valign="middle"><input type="button"  value="/" onclick="symbol('formula','/');alertFormula();" class="btn2 common_btn_bg"> </td>
                				<td valign="middle"><input type="button"  value="\" onclick="symbol('formula','\\');alertFormula();" class="btn2 common_btn_bg"> </td>
                				<td valign="middle"><input type="button"  value="%" onclick="symbol('formula','%');alertFormula();" class="btn2 common_btn_bg"> </td>
                				<td valign="middle"><input type="button"  value="=" onclick="symbol('formula','=');alertFormula();" class="btn2 common_btn_bg"> </td>
                				<td valign="middle"><input type="button"  value="&gt;" onclick="symbol('formula','&gt;');alertFormula();" class="btn2 common_btn_bg"> </td>
                				<td valign="middle"><input type="button"  value="&lt;" onclick="symbol('formula','&lt;');alertFormula();" class="btn2 common_btn_bg"> </td>
                				<td valign="middle"><input type="button"  value="&lt;&gt;" onclick="symbol('formula','&lt;&gt;');alertFormula();" class="btn1 common_btn_bg"> </td>
                				<td valign="middle"><input type="button"  value="&lt;=" onclick="symbol('formula','&lt;=');alertFormula();"class="btn1 common_btn_bg"> </td>
                				<td valign="middle"><input type="button"  value="&gt;=" onclick="symbol('formula','&gt;=');alertFormula();"class="btn1 common_btn_bg"> </td>
                				<td valign="middle"><input type="button"  value="~" onclick="symbol('formula','~');alertFormula();" class="btn2 common_btn_bg"> </td>
                				
               			 		<td valign="middle"><input type="button"  value="<bean:message key='general.mess.and'/>" onclick="symbol('formula','<bean:message key='general.mess.and'/>');alertFormula();" class="btn1 common_btn_bg"> </td>
                				<td valign="middle"><input type="button"  value="<bean:message key='general.mess.or'/>" onclick="symbol('formula','<bean:message key='general.mess.or'/>');alertFormula();" class="btn1 common_btn_bg"> </td>
              					<td valign="middle"><input type="button"  value="<bean:message key='kq.wizard.not'/>" onclick="symbol('formula','<bean:message key='kq.wizard.not'/>');alertFormula();" class="btn1 common_btn_bg"> </td>
              				</tr>
              				
              			
              				
              				
            			</table>
            			</td>
            		</tr>
            		</table>
            		</fieldset>
          		</td>
        	</tr>
      </table>
      </fieldset>
      </td></tr>
      </table>
	</td>
</tr>
</table>
<table width="100%" border="0" align="center">
	<tr height="35px">
		<td align="center" valign="middle">
			<logic:equal name="setFormulaForm" property="flag" value="alert">
				<input type="button"  value="<bean:message key='button.ok'/>" onclick="checkCurFormulaOK('alert');" Class="mybutton">
			</logic:equal>
			<logic:notEqual name="setFormulaForm" property="flag" value="alert">
				<input type="button"  value="<bean:message key='button.ok'/>" onclick="checkCurFormulaOK('save');" Class="mybutton">
			</logic:notEqual>
			<input type="button"  value="<bean:message key='button.cancel'/>" onclick="window.close();" Class="mybutton">
		</td>		
	</tr>
</table>
</fieldset>
</td>
</tr>
</table>

<script language="JavaScript" src="./setformula.js"></script>
<logic:equal name="setFormulaForm" property="flag" value="alert">
<script language="javascript">
onDefSelects();
</script>
</logic:equal>
<script language="javascript">
hides("codeview");
</script>
</html:form>
</body>