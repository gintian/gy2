<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>

<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/common.js"></script>
<script language="JavaScript" src="./chkformula.js"></script>
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
	overflow:auto; 
	border: 1px solid #eee;
	height: 375px;    
	width: 270px;       
	overflow: auto;            
	margin: 0 0 0 5;
	position:absolute;
}
</style>
<hrms:themes></hrms:themes>
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
<html:form action="/general/chkformula/setformula">
<input type="hidden" name="chkid">
<html:hidden name="chkFormulaForm" property="tabid"/>
<table width="792"  border="0" align="center" class="chkformulamargin">
<tr>
<td align="center">  
<fieldset align="center" style="width:100%;">
<legend><bean:message key="workdiary.message.check.formula"/></legend>
<table width="100%"  border="0" align="center" >
  <tr height="430px"> 
    <td width="290" height="430"  align="center" >
    <fieldset align="center" style="width:100%;height:430px;margin-left:3px">
	 <legend><bean:message key="workdiary.message.check.project"/></legend> 
      <table width="100%"   cellSpacing=0 cellPadding=0 border="0">
        <tr height="380"> 
          <td valign="top" >
          	<div id="itemtable" class='itemtable common_border_color'>
	          	<table width="100%" border="0" class="ListTable" style="border-left:0px;border-right:0px">
					<tr class="fixedHeaderTr1">
						<td width="15%" class="TableRow" style="border-left:0px;" align="center"><bean:message key="column.select"/></td>
						<td class="TableRow" align="center" style="border-left:0px;"><bean:message key="column.name"/></td>
						<td width="15%" class="TableRow" align="center" style="border-left:0px;border-right:0px"><bean:message key="label.edit.user"/></td>
					</tr>
					<hrms:paginationdb id="element" name="chkFormulaForm" sql_str="chkFormulaForm.sql" table="" where_str="chkFormulaForm.where" columns="chkFormulaForm.column" order_by="chkFormulaForm.orderby" pagerows="200" page_id="pagination" indexes="indexes">	
					<bean:define id="chkid" name='element' property='chkid'/>
					<bean:define id="name" name="element" property="name"/>
					<bean:define id="tabid" name="element" property="tabid"/>
					<tr> 
						<td class="RecordRow"  style="border-left:0px;" nowrap>
							<input type="checkbox" name="${chkid}" value="${chkid}"> 
						</td>
						<td class="RecordRow" style="word-break: break-all; word-wrap:break-word; border-left:0px;" onclick="setCvalue('${chkid}');">
							${name}
						</td>
						<td align="center" onclick="setCvalue('${chkid}');" class="RecordRow" nowrap style="border-left:0px;border-right:0px">
							<a href="###" onclick="addName('${chkFormulaForm.tabid}','${chkFormulaForm.flag}','${chkid}');"><img src="/images/edit.gif"  border=0></a>
						</td>
					</tr>
					</hrms:paginationdb>
				</table>
          	</div>
          </td>
        </tr>
        <tr>
          <td align="center" valign="middle"> 
            <input  type="button"  value="<bean:message key='button.new.add'/>" onclick="addName('${chkFormulaForm.tabid}','${chkFormulaForm.flag}','');"  Class="mybutton">  
            <input  type="button"  value="<bean:message key='button.delete'/>" onclick="delVariables('${chkFormulaForm.tabid}','${chkFormulaForm.flag}');" Class="mybutton"> 
            <input  type="button"  value="<bean:message key='jx.param.modifysort'/>" onclick="setSorting('${chkFormulaForm.tabid}','${chkFormulaForm.flag}');" Class="mybutton"> 
          </td>
        </tr>
     </table> 
     </fieldset>
    </td>
    <td width="465" height="430" align="center">
			<fieldset align="center" style="width:100%;height:430px;margin-left:10px">
			<legend><bean:message key='workdiary.message.check.formula'/></legend> 
				<table width="100%" border="0">
		        	<tr> 
		          		<td colspan="2" align="center"> 
		            		<html:textarea name="chkFormulaForm" property="formula" cols="70" rows="12" ></html:textarea> 
		            	</td>
		        	</tr>
		        	<tr> 
		          		<td height="21" colspan="2" align="right">
		      		        <input name="wizard" type="button" id="wizard" value='<bean:message key="kq.formula.function"/>' onclick="function_Wizard('${chkFormulaForm.tabid}','formula');" Class="mybutton">
		      		        <input type="button" id="savebutton" value="<bean:message key='org.maip.formula.preservation'/>" Class="mybutton" onclick="saveFormula('${chkFormulaForm.tabid}','${chkFormulaForm.flag}');">&nbsp;
		            	</td>
		        	</tr>
		        	<tr> 
		          		<td width="52%" align="center"> 
		          		 <fieldset  align="center" style="width:96%;height=120">
						 <legend><bean:message key='org.maip.reference.projects'/></legend> 
		            		<table width="100%" border="0">
		            			<tr height="30">
		            				<td>
		            					<table width="100%"  border="0" >
		              						<tr> 
		                						<td height="30"><bean:message key='menu.field'/>
													<html:select name="chkFormulaForm" property="itemid" onchange="changeCodeValue('formula');" style="width:160">
					 									<html:optionsCollection property="itemlist" value="dataValue" label="dataName" />
													</html:select>
		                 						</td>
		              						</tr>
		            					</table>
		            				</td>
		            			</tr>
		            			<tr height="30">
		            				<td>
		            					<span id="codeview" style="display:none">
		            					<table width="100%" border="0" >
		              						<tr> 
		                						<td><bean:message key="codemaintence.codeitem.id"/>
													<select name="codesetid_arr"  onchange="getCodesid('formula');"  style="width:160;font-size:9pt">
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
		          		<td width="48%">
		          		<fieldset align="center" style="width:100%;">
						 <legend><bean:message key="gz.formula.operational.symbol"/></legend> 
							<table width="80%" border="0">
		              			<tr> 
		              				<td>
		              				<table width="100%" border="0">
		              				<tr>
		                				<td><input type="button"  value="0" onclick="symbol('formula',0);" class="btn2 common_btn_bg"></td>
		                				<td><input type="button"  value="1" onclick="symbol('formula',1);" class="btn2 common_btn_bg"> </td>
		                				<td><input type="button"  value="2" onclick="symbol('formula',2);" class="btn2 common_btn_bg"> </td>
		                				<td><input type="button"  value="3" onclick="symbol('formula',3);" class="btn2 common_btn_bg"> </td>
		                				<td><input type="button"  value="4" onclick="symbol('formula',4);" class="btn2 common_btn_bg"> </td>
		                				<td><input type="button"  value="(" onclick="symbol('formula','(');" class="btn2 common_btn_bg"> </td>
		                				<td colspan="2"><input type="button"  value="<bean:message key='gz.formula.if'/>" onclick="symbol('formula','<bean:message key='gz.formula.if'/>');" class="btn3 common_btn_bg"></td>
		              				</tr>
		              				<tr> 
		                				<td><input type="button"  value="5" onclick="symbol('formula',5);" class="btn2 common_btn_bg"> </td>
		                				<td><input type="button"  value="6" onclick="symbol('formula',6);" class="btn2 common_btn_bg"> </td>
		                				<td><input type="button"  value="7" onclick="symbol('formula',7);" class="btn2 common_btn_bg"> </td>
		                				<td><input type="button"  value="8" onclick="symbol('formula',8);" class="btn2 common_btn_bg"> </td>
		                				<td><input type="button"  value="9" onclick="symbol('formula',9);" class="btn2 common_btn_bg"> </td>
		                				<td><input type="button"  value=")" onclick="symbol('formula',')');" class="btn2 common_btn_bg"> </td>
		                				<td colspan="2"><input type="button"  value="<bean:message key='gz.formula.else'/>" onclick="symbol('formula','<bean:message key='gz.formula.else'/>');" class="btn3 common_btn_bg"></td>
		              				</tr>
		              				<tr> 
		                				<td><input type="button"  value="+" onclick="symbol('formula','+');" class="btn2 common_btn_bg"> </td>
		                				<td><input type="button"  value="-" onclick="symbol('formula','-');" class="btn2 common_btn_bg"> </td>
		                				<td><input type="button"  value="*" onclick="symbol('formula','*');" class="btn2 common_btn_bg"> </td>
		                				<td><input type="button"  value="/" onclick="symbol('formula','/');" class="btn2 common_btn_bg"> </td>
		                				<td><input type="button"  value="\" onclick="symbol('formula','\\');" class="btn2 common_btn_bg"> </td>
		                				<td><input type="button"  value="%" onclick="symbol('formula','%');" class="btn2 common_btn_bg"> </td>
		               			 		<td><input type="button"  value="<bean:message key='general.mess.and'/>" onclick="symbol('formula','<bean:message key='general.mess.and'/>');" class="btn1 common_btn_bg"> </td>
		                				<td><input type="button"  value="<bean:message key='general.mess.or'/>" onclick="symbol('formula','<bean:message key='general.mess.or'/>');" class="btn1 common_btn_bg"> </td>
		              				</tr>
		              				<tr> 
		               		 			<td><input type="button"  value="=" onclick="symbol('formula','=');" class="btn2 common_btn_bg"> </td>
		                				<td><input type="button"  value="&gt;" onclick="symbol('formula','&gt;');" class="btn2 common_btn_bg"> </td>
		                				<td><input type="button"  value="&lt;" onclick="symbol('formula','&lt;');" class="btn2 common_btn_bg"> </td>
		                				<td><input type="button"  value="&lt;&gt;" onclick="symbol('formula','&lt;&gt;');" class="btn1 common_btn_bg"> </td>
		                				<td><input type="button"  value="&lt;=" onclick="symbol('formula','&lt;=');"class="btn1 common_btn_bg"> </td>
		                				<td><input type="button"  value="&gt;=" onclick="symbol('formula','&gt;=');"class="btn1 common_btn_bg"> </td>
		                				<td><input type="button"  value="~" onclick="symbol('formula','~');" class="btn2 common_btn_bg"> </td>
		                				<td><input type="button"  value="<bean:message key='kq.wizard.not'/>" onclick="symbol('formula','<bean:message key='kq.wizard.not'/>');" class="btn1 common_btn_bg"> </td>
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
</tr>
</table>
</fieldset>
</td>
</tr>
	<tr height="35">
		<td>
			<table width="100%" border="0" align="center">
				<tr>
					 
					<td align="center">
						<logic:equal name="chkFormulaForm" property="flag" value="0">
							<input type="button"  value="<bean:message key='button.close'/>" onclick="window.close();" Class="mybutton">
						</logic:equal>
					</td>
					 
				</tr>
			</table>
		
		</td>
	</tr>

</table>	

<script language="JavaScript">
setCvalue('${chkFormulaForm.chkid}');
</script>
</html:form>
