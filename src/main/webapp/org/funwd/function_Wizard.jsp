<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<hrms:themes></hrms:themes>
<SCRIPT LANGUAGE=javascript src="selectfunction.js"></SCRIPT>
<style type="text/css">
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
.btn1 {
 BORDER-RIGHT: #C0C0C0 1px solid;
 BORDER-TOP: #C0C0C0 1px solid; 
PADDING-LEFT: 7px;
 PADDING-RIGHT: 7px;
 FONT-SIZE: 12px; 
 BORDER-LEFT: #C0C0C0 1px solid; 
 COLOR: #000000; 
 PADDING-TOP: 1px; 
 PADDING-BOTTOM: 1px;
 BORDER-BOTTOM: #C0C0C0 1px solid
}
.node{
	FONT-SIZE: 12px; 
}
.m_arrow {
	width: 16px;
	height: 8px;
	font-family: "Webdings";
	font-size: 7px;
	line-height: 2px;
	padding-left: 2px;
	cursor: default;
}

</style>
<html:form action="/org/funwd/function_Wizard">
<html:hidden name="funWdForm" property="codearr"/>
<html:hidden name="funWdForm" property="strarr"/>
<table width="100%" height="334" border="0" align="center">
  <tr> 
    <td width="76%" height="227"> 
      <table width="100%" height="220" border="0" align="center">
        <tr> 
          <td height="216">
			<span id="selectifram" class="node"> <bean:message key="org.maip.selection.function"/> 
            	<iframe src="/org/funwd/functionifram.jsp?checktemp=${funWdForm.checktemp}" width="100%" height="200" ></iframe>
           	</span> 
           	<span id="selectformula" class="node"><bean:message key="org.maip.set.function.parameters"/> 
            	<fieldset align="center">
            	<legend></legend>
            	<table width="100%" height="175" border="0" align="center">
             		<tr> 	
                		<td valign="top"><span id="explained" class="node"></span> 
                  		<span id="subsetstr"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<span id="strsubset" class="node"></span>
                				</td>
                				<td>
                					<html:select name="funWdForm" property="strexpression" style="width:160">
			 							<html:optionsCollection property="alist"  value="dataValue" label="dataName" />
									</html:select>  
                				</td>
                			</tr>
                 		 </table>		
                  		</span> 
                  		<span id="subsetnum1"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<bean:message key="kq.wizard.szbd"/>
                				</td>
                				<td>
                					<html:select name="funWdForm" property="numexpression1" style="width:160">
			 							<html:optionsCollection property="nlist" value="dataValue" label="dataName" />
									</html:select>  
                				</td>
                			</tr>
                 		 </table>		
                  		</span>
                  		<span id="subsetnum2"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<bean:message key="kq.wizard.szbd"/>
                				</td>
                				<td>
                					<html:select name="funWdForm" property="numexpression2" style="width:160">
			 							<html:optionsCollection property="nlist" value="dataValue" label="dataName" />
									</html:select>  
                				</td>
                			</tr>
                 		 </table>		
                  		</span>  
                 		<span id="subsetdate1"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<span id="datesubset" class="node"></span>
                				</td>
                				<td>
                					<html:select name="funWdForm" property="dateexpression1" style="width:160">
			 							<html:optionsCollection property="dlist" value="dataValue" label="dataName" />
									</html:select>  
                				</td>
                			</tr>
                 		</table>		
                 		</span> 
                 		<span id="subsetdate2"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<span id="datesubset1" class="node"></span>
                				</td>
                				<td>
                					<html:select name="funWdForm" property="dateexpression2" style="width:160">
			 							<html:optionsCollection property="dlist" value="dataValue" label="dataName" />
									</html:select>  
                				</td>
                			</tr>
                 		</table>		
                 		</span> 
                 		<span id="itemidSelect"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<span id="itemidname"></span>
                				</td>
                				<td>
                					<html:select name="funWdForm" property="itemid" onchange="changeCodeValue();" style="width:160">
			 							<html:optionsCollection property="itemlist" value="dataValue" label="dataName" />
									</html:select>  
                				</td>
                			</tr>
                 		</table>		
                 		</span>
                 		<span id="datastrcss"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<div id="datastrcssaname"><bean:message key="menu.field"/><bean:message key="column.name"/></div>
                				</td>
                				<td>
                					<html:select name="funWdForm" property="datestr" style="width:160">
			 							<html:optionsCollection property="vlist" value="dataValue" label="dataName" />
									</html:select>  
                				</td>
                			</tr>
                 		 </table>		
                  		</span>
                  		<span id="condstrview" > 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<div id="condname"></div>
                				</td>
                				<td>
                					<html:select name="funWdForm" property="statid" style="width:160">
			 							<html:optionsCollection property="statlist"  value="dataValue" label="dataName" />
									</html:select>  
                				</td>
                			</tr>
                 		 </table>		
                  		</span> 
                  			
                  		<span id="rangeidview" > 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<div id="rangeidname"></div>
                				</td>
                				<td>
                					<html:select name="funWdForm" property="rangeid" style="width:160">
			 							<html:optionsCollection property="rangelist"  value="dataValue" label="dataName" />
									</html:select>  
                				</td>
                			</tr>
                 		 </table>		
                  		</span> 
                 		<span id="decimalpoint"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<span id="decimalname"></span>
                				</td>
                				<td width="140">
                					<input type="text" name="decimal" id="decimal" style="width:140"/>
                				</td>
                				<td>
                					<table border="0" cellspacing="0" cellpadding="0" >
		      							<tr><td><button type="button" id="y_up" class="m_arrow"  onclick="upadd('decimal');">5</button></td></tr>
		      							<tr><td><button type="button" id="y_down" class="m_arrow" onclick="downcut('decimal');">6</button></td></tr>
	          						</table>
                				</td>
                			</tr>
                 		</table>		
                 		</span>
                 		<span id="initiationnum"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<bean:message key="kq.wizard.long1"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                					&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="kq.wizard.long2"/>
                				</td>
                				<td width="140">
                					<input type="text" name="initiation" id="initiation" style="width:140"/>
                				</td>
                				<td>
                					<table border="0" cellspacing="0" cellpadding="0" >
		      							<tr><td><button type="button" id="y_up" class="m_arrow" value="0" onclick="upadd('initiation');">5</button></td></tr>
		      							<tr><td><button type="button" id="y_down" class="m_arrow" value="0" onclick="downcut('initiation');">6</button></td></tr>
	          						</table>
                				</td>
                			</tr>
                 		</table>		
                 		</span>
                 		<span id="directioncss"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<bean:message key="kq.wizard.direct1"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                					<bean:message key="kq.wizard.direct2"/>
                				</td>
                				<td>
                					<select name="direction" style="width:160">
                						<option value=""> </option>
                						<option value='<bean:message key="org.maip.first.paragraph"/>'><bean:message key="org.maip.first.paragraph"/></option>
                						<option value='<bean:message key="org.maip.recently.article"/>'><bean:message key="org.maip.recently.article"/></option>
                					</select>
                				</td>
                			</tr>
                 		</table>		
                 		</span>
                 		
                  		<span id="datastrcss1"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<div id="datastrcss1name"><bean:message key="menu.field"/><bean:message key="column.name"/></div>
                				</td>
                				<td>
                					<html:select name="funWdForm" property="strid" style="width:160">
			 							<html:optionsCollection property="vlist" value="dataValue" label="dataName" />
									</html:select>  
                				</td>
                			</tr>
                 		 </table>		
                  		</span>   
                  		<span id="datastrcss2"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<div id="datastrcss2name"><bean:message key="menu.field"/><bean:message key="column.name"/></div>
                				</td>
                				<td>
                					<input type="text" name="strid2_item" id="strid2_item" style="width:160" onclick="toggleSelect('strid2');">
                					<select name="strid2_arr" style="width:160;display:none" onblur="toggleText('strid2');">
             						</select>
                				</td>
                			</tr>
                 		 </table>		
                  		</span>   
                 		<span id="conditionscss"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<div id="conditionssubset0" class="node"><bean:message key="label.item"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="org.maip.pieces"/>
                				</div>
                				</td>
                				<td><input type="text" name="conditions" id="conditions" style="width:160"></input></td>
                			</tr>
                 		</table>		
                 		</span>
                 			<span id="conditionscss1"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<div id="conditionssubset1" class="node"></div>
                				</td>
                				<td><input type="text" name="conditions1" id="conditions1" style="width:160"></input></td>
                			</tr>
                 		</table>		
                 		</span>
                 		<span id="conditionscss2"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<div id="conditionssubset" class="node"></div>
                				</td>
                				<td><input type="text" name="conditions2" id="conditions2" style="width:160"></input></td>
                			</tr>
                 		</table>		
                 		</span>
                 			<span id="conditionscss3"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<div id="conditionssubset3" class="node"></div>
                				</td>
                				<td><input type="text" name="conditions3" id="conditions3" style="width:160"></input></td>
                			</tr>
                 		</table>		
                 		</span>
                 		<span id="waycss"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<bean:message key="org.maip.way1"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                					<bean:message key="org.maip.way2"/>
                				</td>
                				<td>
                					<select name="way" style="width:160">
                						<option value=""> </option>
                						<option value='<bean:message key="org.maip.the.sum"/>'><bean:message key="org.maip.the.sum"/></option>
                						<option value='<bean:message key="org.maip.the.average"/>'><bean:message key="org.maip.the.average"/></option>
                						<option value='<bean:message key="org.maip.number.of"/>'><bean:message key="org.maip.number.of"/></option>
                						<option value='<bean:message key="org.maip.the.maximum"/>'><bean:message key="org.maip.the.maximum"/></option>
                						<option value='<bean:message key="org.maip.the.minimum"/>'><bean:message key="org.maip.the.minimum"/></option>
                						<option value='<bean:message key="org.maip.first.record"/>'><bean:message key="org.maip.first.record"/></option>
                						<option value='<bean:message key="org.maip.recent.record"/>'><bean:message key="org.maip.recent.record"/></option>
                					</select>
                				</td>
                			</tr>
                 		</table>		
                 		</span>
                 		<span id="templates"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<span id="templatename"><bean:message key="system.operation.template"/></span>
                				</td>
                				<td>
                					<select name="template" id="template" style="width:160">
                						<option value=''></option>
                						<option value='1'><bean:message key="org.gz.fafang"/></option>
                						<option value='2'><bean:message key="org.gz.zonge"/></option>
                						<option value='3'><bean:message key="org.gz.suodeshui"/></option>
                					</select>
                				</td>
                			</tr>
                 		</table>		
                 		</span>
                 		<span id="partTimeJob"  style="display:none"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<div id="job"><bean:message key="sys.label.param"/></div>
                				</td>
                				<td>
                					<select name="partTimeJob_select" id="partTimeJob_select"  style="width:160"> 
                						<option value='<bean:message key="tree.unroot.undesc"/><bean:message key="tree.umroot.umdesc"/>'><bean:message key="tree.unroot.undesc"/><bean:message key="tree.umroot.umdesc"/></option>
                						<option value='<bean:message key="tree.unroot.undesc"/>'><bean:message key="tree.unroot.undesc"/></option>
                						<option value='<bean:message key="tree.umroot.umdesc"/>'><bean:message key="tree.umroot.umdesc"/></option>
                						<option value=''></option>
                					</select>
                				</td>
                			</tr>
                 		</table>		
                 		</span>
                 		<span id="codeMax"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<span id="codeMaxname"></span>
                				</td>
                				<td>
                					<select name="code_maxarr" id="code_maxarr" style="width:160">
             						</select>
                				</td>
                			</tr>
                 		</table>		
                 		</span>
                 		<span id="codeMin"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<span id="codeMinname"></span>
                				</td>
                				<td>
                					<select name="code_minarr" id="code_minarr" style="width:160">
             						</select>
                				</td>
                			</tr>
                 		</table>		
                 		</span>
                 		<span id="stand"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<span id="standname"></span>
                				</td>
                				<td>
                					<html:select name="funWdForm" property="standid" onchange="standSelect();" style="width:160">
			 							<html:optionsCollection property="standlist" value="dataValue" label="dataName" />
									</html:select>  
                				</td>
                			</tr>
                 		 </table>		
                  		</span>
                  		
                  		<span id="standTzTd"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<span id="standhlname"></span>
                				</td>
                				<td>
                					<html:select name="funWdForm" property="standhlid" onchange="standSelect2();" style="width:160">
			 							<html:optionsCollection property="standidlist" value="dataValue" label="dataName" />
									</html:select>  
                				</td>
                			</tr>
                 		 </table>		
                  		</span>
                  		
                  		<span id="standhHighLow"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<span id="standhlname"></span>
                				</td>
                				<td>
                					<html:select name="funWdForm" property="standhlid" onchange="changeItemValue();" style="width:160">
			 							<html:optionsCollection property="standidlist" value="dataValue" label="dataName" />
									</html:select>  
                				</td>
                			</tr>
                 		 </table>		
                  		</span>
                  		<span id="standHfactor"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<span id="hfactorname"></span>
                				</td>
                				<td>
                					<select name="hfactor_arr" style="width:160">
             						</select>
                				</td>
                			</tr>
                 		</table>		
                 		</span>
                 		<span id="standS_hfactor"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<span id="s_hfactorname"></span>
                				</td>
                				<td>
                					<select name="s_hfactor_arr" style="width:160">
             						</select>
                				</td>
                			</tr>
                 		</table>		
                 		</span>
                 		<span id="standVfactor"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<span id="vfactorname"></span>
                				</td>
                				<td>
                					<select name="vfactor_arr" style="width:160">
             						</select>
                				</td>
                			</tr>
                 		</table>		
                 		</span>
                 		<span id="standS_vfactor"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<span id="s_vfactorname"></span>
                				</td>
                				<td>
                					<select name="s_vfactor_arr" style="width:160">
             						</select>
                				</td>
                			</tr>
                 		</table>		
                 		</span>
                 		<span id="standItem"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<span id="itemname"></span>
                				</td>
                				<td>
                					<select name="item" style="width:160">
             						</select>
                				</td>
                			</tr>
                 		</table>		
                 		</span>
                  </td>
              	</tr>
            </table>
            </fieldset>
            </span> 
           </td>
        </tr>
      </table> </td>
    <td width="24%"> <table width="100%" height="314" border="0" style="margin-top:-130px">
        <tr> 
          <td height="154" align="center" valign="bottom"> <span id="stepDark"> 
            <input type="button" name="nextstepDark" value=' <bean:message key="label.zp_options.down_step"/> ' class="mybutton" disabled="disabled">
            </span> <span id="stepBrilliant"> 
            <input type="button" name="nextstepBrilliant" value=' <bean:message key="label.zp_options.down_step"/> ' onclick="nextStep();subsetfunction();" Class="mybutton">
            </span> </td>
        </tr>
        <tr> 
          <td height="111" align="center"> <span id="darkReturnStep"> 
            <input type="button" name="darkstep" value=' <bean:message key="label.zp_options.up_step"/> ' class="mybutton" disabled="disabled">
            </span> <span id="brilliantReturnStep"> 
            <input type="button" name="brilliantstep" value=' <bean:message key="label.zp_options.up_step"/> ' onclick="returnStep();" Class="mybutton">
            </span> </td>
        </tr>
        <tr> 
          <td height="34" align="center">
          	<span id="darkCompleted"> 
            <input type="button" name="completeddark" value=' <bean:message key="button.muster.finished"/> ' class="mybutton" disabled="disabled">
            </span>
            <span id="brilliantCompleted"> 
            <input type="button" name="completedbrilliant" value=' <bean:message key="button.muster.finished"/> ' onclick="completed_();"  Class="mybutton">
            </span>
          </td>
        </tr>
      </table></td>
  </tr>
  <tr> 
    <td height="14"><span id="note" class="node"><bean:message key="label.description"/><bean:message key="hire.zp_options.semicolon"/><bean:message key="kq.formula.number"/></span></td>
  </tr>
  <tr> 
    <td height="83" align="center">
    	<table width="99%" height="82" border="0">
       		<tr>
       			<td valign="top">
       				<span id="calculation" class="node"></span>
      				<input type="hidden" name="formula" id="formula" value=""> 
      				<input type="hidden" name="id" id="id" value=""> 
      				<input type="hidden" name="attribute" id="attribute" value="">
				</td>
       		</tr>
       </table> 
      </td>
  </tr>
</table>
<SCRIPT LANGUAGE=javascript >
var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
var isOpera = userAgent.indexOf("Opera") > -1;
var isIE = (userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1 && !isOpera);
var callBackFunc = "";
<%
	String callBackFunc = request.getParameter("callBackFunc");
%>

function completed_(){
	var formula = document.getElementById("formula").value;
	var id = document.getElementById("id").value;
	var array = id.split("_");
	var attribute = defvalue();
	if(array[0].length==2){
	    if(window.showModalDialog){
            parent.window.returnValue=formula;
        }else{
            if(parent.Ext)
                parent.parent.<%=callBackFunc%>(formula);
            else if(parent.opener)
                parent.opener.<%=callBackFunc%>(formula);
        }
	}else if(id=="V_data4_2_1"){
		var returns = formula;
		var dateexpression1=document.getElementById("dateexpression1").value;
		var direction=document.getElementById("direction").value;
		var decimal=document.getElementById("decimal").value;
		 if(dateexpression1.length>0){
		 	var itemarr = dateexpression1.split(":"); 
			if(itemarr.length==2){
				returns += " " +itemarr[1]+" ";
			}
		 }
		 if(direction.length>0){
			returns += " " +direction+" ";
		 }
		 if(decimal.length>0){
			returns += " " +decimal+" ";
		 }else{
			returns += " 0 ";
		 }

        if(window.showModalDialog){
            parent.window.returnValue=returns + "条记录";
        }else{
            if(parent.Ext)
                parent.parent.<%=callBackFunc%>(returns + "条记录");
            else if(parent.opener)
                parent.opener.<%=callBackFunc%>(returns + "条记录");
        }
	}else if(id=="V_vol5_3_3"){
		var returns = formula;
		var datestr=document.getElementById("datestr").value;
		var conditions=document.getElementById("conditions").value;
		var way=document.getElementById("way").value;
		if(datestr.length>0){
			var itemarr = datestr.split(":"); 
			if(itemarr.length==2){
				returns += " " +itemarr[1]+" ";
			}
		}
		returns+= " 满足 ";
		if(conditions.length>0){
			returns+= " "+conditions+" ";
		}
		if(way.length>0){
			returns+= " "+way+" ";
		}
		if(window.showModalDialog){
            parent.window.returnValue=returns;
        }else{
            if(parent.Ext)
                parent.parent.<%=callBackFunc%>(returns);
            else if(parent.opener)
                parent.opener.<%=callBackFunc%>(returns);
        }
	}else{
        if(window.showModalDialog) {
            parent.window.returnValue=formula+"("+attribute+")";
        }else{
            if(parent.parent.Ext)
                parent.parent.<%=callBackFunc%>(formula+"("+attribute+")");
            else if(parent.opener)
                parent.opener.<%=callBackFunc%>(formula+"("+attribute+")");
        }
	}
    if(window.showModalDialog) {
        parent.window.close();
    }else{
        if(parent.parent.Ext)
            parent.parent.closeFunc();
        else if(parent.opener);
            parent.window.close();
    }
}

toggles("stepDark");
hides("stepBrilliant");
toggles("darkReturnStep");
hides("brilliantReturnStep");
toggles("darkCompleted");
hides("brilliantCompleted");
hides("directioncss");
hides("conditionscss");
hides("conditionscss2");
hides("conditionscss1");
hides("conditionscss3");
hides("datastrcss2");
hides("condstrview");
hides("rangeidview");

hides("datastrcss");
hides("datastrcss1");
hides("waycss");
hides("templates");
hides("partTimeJob");
toggles("selectifram");
hides("selectformula");
hides("subsetstr");
hides("subsetnum1");
hides("subsetnum2");
hides("subsetdate1");
hides("subsetdate2");
hides("decimalpoint");
hides("initiationnum");
hides("codeMax");
hides("codeMin");
hides("itemidSelect");
hides("stand");
hides("standHfactor");
hides("standVfactor");
hides("standS_hfactor");
hides("standS_vfactor");
hides("standhHighLow");
hides("standTzTd");
hides("standItem");
</SCRIPT>
</html:form>


