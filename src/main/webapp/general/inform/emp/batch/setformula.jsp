<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="./batch.js"></script>
<script type="text/javascript" src="../../../../js/hjsjUrlEncode.js"></script>
<style type="text/css"> 
.btn3 {
 BORDER-RIGHT: #C0C0C0 1px solid;
 BORDER-TOP: #C0C0C0 1px solid; 
 PADDING-LEFT: 0px; FONT-SIZE: 12px; 
 BORDER-LEFT: #C0C0C0 1px solid; 
 COLOR: #808080; 
 PADDING-TOP: 0px; 
 PADDING-BOTTOM: 0px;
 BORDER-BOTTOM: #C0C0C0 1px solid;
 line-height:18px;
 border:#0042A0 1px solid;
 width:37px;
 background-image:url(/images/button.jpg);
}

</style>
<hrms:themes/>
<style>
.common_border_color{
	height:100%;
}
/* 员工管理，记录录入，计算，公式，计算项目太多时，页面有问题   jingq add 2015.06.23*/
#itemtable{
	height:300px;
	overflow:scroll;
}
</style>
<html:form action="/general/inform/emp/batch/setformula">
<html:hidden name="indBatchHandForm" property="formulastr" styleId="formulastr"/>
<html:hidden name="indBatchHandForm" property="infor" styleId="infor"/>
<html:hidden name="indBatchHandForm" property="unit_type" styleId="unit_type"/> 
<html:hidden name="indBatchHandForm" property="isSetId" styleId="isSetId"/>
<html:hidden name="indBatchHandForm" property="setname" styleId="setname"/>
<input type="hidden" name="id" id="id"/>
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0" align="center">
<tr>
<td align="center">  
<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
  <tr> <!-- 【6869】汉口银行，员工管理，记录录入，计算，公式，计算项目展现空白，再次修改请写明注释  jingq upd 2015.01.20 -->
    <td width="200" align="center" valign="top">
    <fieldset align="center" style="height:365px;width: 200px;">
	<legend><bean:message key='org.maip.comp.project'/></legend> 
     <div style="height:330px;overflow: hidden;width: 200px;" id="divId">
      <table border="0" cellpadding="0px" style="width: 180px;" cellspacing="0px">
        <tr>
          <td width="40%" height="310px;" style="padding:5 5 0 5;" align="center" valign="top" id='itemtable1'> 
          		${indBatchHandForm.formulatable}     		
          </td>
        </tr>
        </table>
     </div>
     <div style="height:35px; margin-top: 5px;">
      <table border="0" cellpadding="0px" cellspacing="0px" width='200'>
        <tr>
        	<td align="center">
        	<input type="button" style="" value="<bean:message key='button.new.add'/>" onclick='addFormula("${indBatchHandForm.infor}","${indBatchHandForm.setname}","${indBatchHandForm.isSetId}");' Class="mybutton">
			<input type="button" style="" value="<bean:message key='button.delete'/>" onclick="delFormula();" Class="mybutton">
        	</td>
        </tr>
      </table> 
      </div>
      </fieldset>
    </td>
    <td width="400" align="center" style="padding-left:4px;">
    <fieldset style="height:365px;width: 350px;" id="fsId">
	<legend><bean:message key='kq.item.count'/></legend> 
		<table width="100%" border="0" cellpadding="0px" cellspacing="0px"">
        	<tr> 
          		<td align="left"> 
            		<html:textarea name="indBatchHandForm" property="formula" styleId="formula"  onfocus="formulaView();" cols="60" rows="13" 
            		style="width:360px; height:200px!important;font-size:12px;font-family:Arial, Helvetica, sans-serif"></html:textarea> 
            	</td>
        	</tr>
        	<tr height="3"></tr>
        	<tr> 
          		<td align="center" style="padding-bottom:5px;"> 
          		 <fieldset id="fieldId" align="left" style="width: 340px;">
				 <legend><bean:message key='org.maip.reference.projects'/></legend> 
            		<table width="100%" border="0">
            			<tr>
            				<td>
            					<table width="100%"  border="0" >
              						<tr> 
                						<td>
                							子集 <html:select name="indBatchHandForm" property="fieldsetid" styleId="fieldsetid" onchange="change('${indBatchHandForm.infor}');" style="width:270">
			 									<html:optionsCollection property="fieldsetlist"  value="dataValue" label="dataName" />
											</html:select>  
                 						</td>
              						</tr>
            					</table>
            				</td>
            			</tr>
            			<tr>
            				<td>
            					<table width="100%" border="0">
              						<tr> 
                						<td>
											指标 <select name="itemid_arr" onchange="getItemid('itemid_arr');" style="width:270;font-size:9pt">
             								</select>
                 						</td>
              						</tr>
            					</table>
            				</td>
            			</tr>
            			<tr>
            				<td>
            					<table width="100%" border="0">
              						<tr> 
                						<td nowrap="nowrap"><div id="codeview" style="display: none">代码
											<select name="itemid_value_arr" onchange="getItemid('itemid_value_arr');" style="width:270;font-size:9pt">
             								</select></div>
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
      </fieldset>
	</td>
	<td valign="top">
		<table width="100%" border="0" >
			<tr>
				<td align="left" valign="top" height="50" style="padding-top:5px;">
					<input name="wizard" type="button"   value="<bean:message key='button.wizard'/>" onclick='function_Wizard("${indBatchHandForm.infor}");' Class="mybutton">
				</td>
			</tr>
			
			<tr>
				<td align="left" height="50" valign="top">
					<span id="viewSave">
						<input type="button"  value="<bean:message key='button.save'/>" onclick="resultCheckExpr();hides('viewSave');toggles('hideSave');" Class="mybutton">
					</span>
					<span id="hideSave">
						<input type="button"  value="<bean:message key='button.save'/>" Class="mybutton">
					</span>
				</td>
			</tr>
			<tr>
				<td align="left"  valign="top" height="50">
					<input type="button"  value="<bean:message key='button.close'/>" onclick="closeOk();" Class="mybutton">
				</td>
			</tr>
		</table>
	</td>
</tr>
</table>
</td></tr></table>
</html:form>
</div>
<script language="JavaScript">
toggles("viewSave");
hides("hideSave");
change("${indBatchHandForm.infor}");
defaultSelectFormula("${indBatchHandForm.unit_type}");
window.onbeforeunload=function(){   
	closeOk();
}   
window.onload = function(){
	document.getElementById('itemtable').style.marginBottom='5px';
}

var itemtable = document.getElementById('itemtable');
var div = document.getElementById('divId');
var fs = document.getElementById('fsId');
var fieldset = document.getElementById('fieldId');
var text = document.getElementsByName('formula')[0];
if(getBrowseVersion()){
	if(isCompatibleIE()) {
		itemtable.style.left='5px';
		itemtable.style.width='190px';
		itemtable.style.height='300px';
		div.style.height='310px';
		text.style.width='340px';
		fs.style.paddingLeft="5px";
	} else {
		text.style.width='320px';
		fs.style.width='320px';
		fieldset.style.width='300px';
		itemtable.style.height='310px';
		div.style.height='320px';
	}
}
	
</script>
