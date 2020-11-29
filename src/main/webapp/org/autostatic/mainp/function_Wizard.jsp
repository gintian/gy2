<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<hrms:themes></hrms:themes>
<SCRIPT LANGUAGE=javascript src="/js/selectfunction.js"></SCRIPT>
<script type="text/javascript" src="/ext/ext-all.js" ></script>
    <script type="text/javascript" src="/ext/ext-lang-zh_CN.js" ></script> 
    <script type="text/javascript" src="/ext/rpc_command.js"></script>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
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
<%
String mode = request.getParameter("mode")==null?"":(String)request.getParameter("mode");
%>
</style>
<hrms:themes />
<html:form action="/org/autostatic/mainp/function_Wizard" >
<html:hidden name="projectForm" styleId="codearr" property="codearr"/>
<html:hidden name="projectForm" property="strarr"/>
	<div id="wizarddiv" style="overflow: auto">
<table width="100%" height="230" border="0" align="center">
  <tr> 
    <td width="84%" height="227"> 
      <table width="100%" height="220" border="0" align="center">
        <tr> 
          <td height="216">
			<div id="selectifram" class="node"> <bean:message key="org.maip.selection.function"/> 
            	<iframe src="/org/autostatic/mainp/functionifram.jsp?checktemp=${projectForm.checktemp}&mode=<%=mode %>" width="100%" height="230" ></iframe>
           	</div> 
           	<div id="selectformula"  style="display:none" class="node"><bean:message key="org.maip.set.function.parameters"/> 
            	<!-- <fieldset align="center" style="width:100%;">
            	<legend></legend> 
            	【6877】查询浏览-复杂查询-向导-选取一个函数-下步（界面的线条有个缺口）。 jingq upd 2015.01.22-->
            	<div align="center" style="width:100%;border:1px solid;" class="common_border_color">
            	<table width="100%" height="205" border="0" align="center">
             		<tr> 	
                		<td valign="top"><div id="explained" class="node"></div> 
                		<div id="fieldsetview"  style="display:none"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;"><bean:message key="label.zp_options.subset"/></td>
                				<td>
                					<html:select name="projectForm" property="fieldname" onchange="changes(this,'${projectForm.salaryid}','${projectForm.tabid}','${projectForm.checktemp}','projectForm.mode');" style="width:160">
			 							<html:optionsCollection property="fieldsetlist"  value="dataValue" label="dataName" />
									</html:select>  
                				</td>
                			</tr>
                 		 </table>		
                  		</div> 
                  		
                  		<div id="subsetnum1"  style="display:none"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<div id="numtitle"><bean:message key="kq.wizard.szbd"/></div>
                				</td>
                				<td>
                					<input type="text" name="numexpression1_item" id="numexpression1_item" style="width:160" onclick="toggleSelect('numexpression1');">
                					<select name="numexpression1_arr" id="numexpression1_arr" style="width:160;display:none"  onblur="toggleText('numexpression1');" id="numexpression1_arr">
             						</select>
                				</td>
                			</tr>
                 		 </table>		
                  		</div>
                  		<div id="subsetnum2"  style="display:none"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<bean:message key="kq.wizard.szbd"/>
                				</td>
                				<td>
                					<input type="text" name="numexpression2_item" id="numexpression2_item" style="width:160" onclick="toggleSelect('numexpression2');">
                					<select name="numexpression2_arr" id="numexpression2_arr" style="width:160;display:none" onblur="toggleText('numexpression2');" id="numexpression2_arr">
             						</select>
                				</td>
                			</tr>
                 		 </table>		
                  		</div>  
                 		<div id="subsetdate1"  style="display:none"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<div id="datesubset" class="node"></div>
                				</td>
                				<td>
                					<input type="text" name="dateexpression1_item" id="dateexpression1_item" style="width:160" onclick="toggleSelect('dateexpression1');">
                					<select name="dateexpression1_arr" id="dateexpression1_arr" style="width:160;display:none"  onblur="toggleText('dateexpression1');" id="dateexpression1_arr">
             						</select>
                				</td>
                			</tr>
                 		</table>		
                 		</div> 
                 		<div id="subsetdatetype"  style="display:none"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<div id="datesubsettype" class="node"></div>
                				</td>
                				<td>
                					<input type="text" name="datetype_item" id="datetype_item" style="width:160" onclick="toggleSelect('datetype');">
                					<select name="datetype_arr" id="datetype_arr" style="width:160;display:none"  onblur="toggleText('datetype');" id="datetype_arr">
                					<option value=""></option>
                					<option value="'YYYY-MM-DD':'YYYY-MM-DD'">'YYYY-MM-DD'</option>
                					<option value="'YYYY-MM-DD HH24':'YYYY-MM-DD HH24'">'YYYY-MM-DD HH24'</option>
                					<option value="'YYYY-MM-DD HH24@MI':'YYYY-MM-DD HH24@MI'">'YYYY-MM-DD HH24:MI'</option>
                					<option value="'YYYY-MM-DD HH24@MI@SS':'YYYY-MM-DD HH24@MI@SS'">'YYYY-MM-DD HH24:MI:SS'</option>
             						</select>
                				</td>
                			</tr>
                 		</table>		
                 		</div> 
                 		<div id="subsetdate2"  style="display:none"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<div id="datesubset1" class="node"></div>
                				</td>
                				<td>
                					<input type="text" name="dateexpression2_item" id="dateexpression2_item" style="width:160" onclick="toggleSelect('dateexpression2');">
                					<select name="dateexpression2_arr" id="dateexpression2_arr" style="width:160;display:none" onblur="toggleText('dateexpression2');">
             						</select>
                				</td>
                			</tr>
                 		</table>		
                 		</div> 
                  		<div id="subsetstr"  style="display:none"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<div id="strsubset" class="node"></div>
                				</td>
                				<td>
                					<input type="text" name="strexpression_item" id="strexpression_item" style="width:160" onclick="toggleSelect('strexpression');">
                					<select name="strexpression_arr" id="strexpression_arr" style="width:160;display:none" onblur="toggleText('strexpression');">
             						</select>
                				</td>
                			</tr>
                 		 </table>		
                  		</div> 	
                 		
					
                 		<div id="datastrcss"  style="display:none"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<div id="datastrcssaname"><bean:message key="menu.field"/><bean:message key="column.name"/></div>
                				</td>
                				<td>
                					<input type="text" name="datestr_item" id="datestr_item" style="width:160" onclick="toggleSelect('datestr');">
                					<select name="datestr_arr" id="datestr_arr" style="width:160;display:none" onblur="toggleText('datestr');">
             						</select>
                				</td>
                			</tr>
                 		 </table>		
                  		</div>
                  			<div id="condstrview"  style="display:none"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<div id="condname"></div>
                				</td>
                				<td>
                					<html:select name="projectForm" property="statid" styleId="statid" style="width:160">
			 							<html:optionsCollection property="statlist"  value="dataValue" label="dataName" />
									</html:select>  
                				</td>
                			</tr>
                 		 </table>		
                  		</div> 
                  		<div id="itemidSelect"  style="display:none"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<div id="itemidname"></div>
                				</td>
                				<td>
                					<input type="text" name="itemid_item" id="itemid_item" style="width:160" onclick="toggleSelect('itemid');">
                					<select name="itemid_arr" id="itemid_arr" style="width:160;display:none" onchange="changeCodeValue();" onblur="toggleText('itemid');">
             						</select>
                				</td>
                			</tr>
                 		</table>		
                 		</div>
                  		<div id="hdayslogoview"  style="display:none"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<div id="hdayslogoname"><bean:message key="org.maip.hdaylogo"/></div>
                				</td>
                				<td>
                					<select name="hdayslogo" id="hdayslogo" style="width:160">
                						<option value='<bean:message key="org.maip.hdaylogo.no"/>'><bean:message key="org.maip.hdaylogo.no"/></option>
                						<option value='<bean:message key="org.maip.hdaylogo.yes"/>'><bean:message key="org.maip.hdaylogo.yes"/></option>
                					</select>
                				</td>
                			</tr>
                 		 </table>		
                  		</div>
                  		<div id="incrementalItem"  style="display:none"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<div id="incrementalItemname"></div>
                				</td>
                				<td>
                					<input type="text" name="incrementalItem_item" id="incrementalItem_item" style="width:160" onclick="toggleSelect('incrementalItem');">
                					<select name="incrementalItem_arr" id="incrementalItem_arr" style="width:160;display:none" onblur="toggleText('incrementalItem');">
             						</select>
                				</td>
                			</tr>
                 		</table>		
                 		</div>
                 		<div id="rangeidview"  style="display:none"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<div id="rangeidname"></div>
                				</td>
                				<td>
                					<html:select name="projectForm" property="rangeid" styleId="rangeid" style="width:160">
			 							<html:optionsCollection property="rangelist"  value="dataValue" label="dataName" />
									</html:select>  
                				</td>
                			</tr>
                 		 </table>		
                  		</div> 
                 		<div id="decimalpoint"  style="display:none"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<div id="decimalname"></div>
                				</td>
                				<td width="140">
                					<input type="text" name="decimal" value="0" style="width:140"/>
                				</td>
                				<td>
                					<table border="0" cellspacing="0" cellpadding="0" >
		      							<tr><td><button type="button" id="y_up" class="m_arrow"  onclick="upadd('decimal');">5</button></td></tr>
		      							<tr><td><button type="button" id="y_down" class="m_arrow" onclick="downcut('decimal');">6</button></td></tr>
	          						</table>
                				</td>
                			</tr>
                 		</table>		
                 		</div>
                 		<div id="initiationnum"  style="display:none"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td id="initiationnumid" width="80" style="word-break: break-all; word-wrap:break-word;">
                					<bean:message key="kq.wizard.long1"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                					&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="kq.wizard.long2"/>
                				</td>
                				<td width="140">
                					<input type="text" name="initiation" value="0" style="width:140"/>
                				</td>
                				<td>
                					<table border="0" cellspacing="0" cellpadding="0" >
		      							<tr><td><button type="button" id="y_up" class="m_arrow" value="0" onclick="upadd('initiation');">5</button></td></tr>
		      							<tr><td><button type="button" id="y_down" class="m_arrow" value="0" onclick="downcut('initiation');">6</button></td></tr>
	          						</table>
                				</td>
                			</tr>
                 		</table>		
                 		</div>
                 		<div id="directioncss"  style="display:none"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<bean:message key="kq.wizard.direct1"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                					<bean:message key="kq.wizard.direct2"/>
                				</td>
                				<td>
                					<select name="direction" id="direction" style="width:160">
                						<option value=""> </option>
                						<option value='<bean:message key="org.maip.first.paragraph"/>'><bean:message key="org.maip.first.paragraph"/></option>
                						<option value='<bean:message key="org.maip.recently.article"/>'><bean:message key="org.maip.recently.article"/></option>
                					</select>
                				</td>
                			</tr>
                 		</table>		
                 		</div>
                 		<div id="fieldsetunitview"  style="display:none"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;"><bean:message key="label.zp_options.subset"/></td>
                				<td>
                					<html:select name="projectForm" property="fieldnameunit" styleId="fieldnameunit" onchange="changesunit(this,'${projectForm.salaryid}','${projectForm.tabid}','${projectForm.checktemp}');" style="width:160">
			 							<html:optionsCollection property="fieldsetlistunit"  value="dataValue" label="dataName" />
									</html:select>  
                				</td>
                			</tr>
                 		 </table>		
                  		</div> 
                  		<div id="fieldsetposview"  style="display:none"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;"><bean:message key="label.zp_options.subset"/></td>
                				<td>
                					<html:select name="projectForm" property="fieldnamepos" styleId="fieldnamepos" onchange="changespos(this,'${projectForm.salaryid}','${projectForm.tabid}','${projectForm.checktemp}');" style="width:160">
			 							<html:optionsCollection property="fieldsetlistpos"  value="dataValue" label="dataName" />
									</html:select>  
                				</td>
                			</tr>
                 		 </table>		
                  		</div> 
                  		<div id="datastrcss1"  style="display:none"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<div id="datastrcss1name"><bean:message key="menu.field"/><bean:message key="column.name"/></div>
                				</td>
                				<td>
                					<input type="text" name="strid_item" id="strid_item" style="width:160" onclick="toggleSelect('strid');">
                					<select name="strid_arr" id="strid_arr" style="width:160;display:none" onblur="toggleText('strid');">
             						</select>
                				</td>
                			</tr>
                 		 </table>		
                  		</div>    
                  		<div id="datastrcss2"  style="display:none"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<div id="datastrcss2name"><bean:message key="menu.field"/><bean:message key="column.name"/></div>
                				</td>
                				<td>
                					<input type="text" name="strid2_item" id="strid2_item" style="width:160" onclick="toggleSelect('strid2');">
                					<select name="strid2_arr" id="strid2_arr" style="width:160;display:none" onblur="toggleText('strid2');">
             						</select>
                				</td>
                			</tr>
                 		 </table>		
                  		</div>  
                 		<div id="conditionscss"  style="display:none"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<div id="conditionssubset0" class="node"><bean:message key="label.item"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="org.maip.pieces"/>
                					</div>
                				</td>
                				<td><input type="text" name="conditions" id="conditions" style="width:160"></input></td>
                			</tr>
                 		</table>		
                 		</div>
                 	<div id="conditionscss1"  style="display:none"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<div id="conditionssubset1" class="node"></div>
                				</td>
                				<td><input type="text" name="conditions1" id="conditions1" style="width:160"></input></td>
                			</tr>
                 		</table>		
                 		</div>
                 	<div id="conditionscss2"  style="display:none"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<div id="conditionssubset" class="node"></div>
                				</td>
                				<td><input type="text" name="conditions2" style="width:160"></input></td>
                			</tr>
                 		</table>		
                 		</div>
                 	<div id="conditionscss3"  style="display:none"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<div id="conditionssubset3" class="node"></div>
                				</td>
                				<td><input type="text" name="conditions3" id="conditions3" style="width:160"></input></td>
                			</tr>
                 		</table>		
                 		</div>
                 		<div id="waycss"  style="display:none"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<bean:message key="org.maip.way1"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                					<bean:message key="org.maip.way2"/>
                				</td>
                				<td>
                					<select name="way" id="way" style="width:160">
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
                 		</div>
                 		<div id="waycss"  style="display:none"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<bean:message key="org.maip.way1"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                					<bean:message key="org.maip.way2"/>
                				</td>
                				<td>
                					<select name="way" id="way" style="width:160">
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
                 		</div>
                 		<div id="templates"  style="display:none"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<div id="templatename"><bean:message key="system.operation.template"/></div>
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
                 		</div>
                 		<div id="partTimeJob"  style="display:none"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<div id="job"><bean:message key="sys.label.param"/></div>
                				</td>
                				<td>
                					<select name="partTimeJob_select" id="partTimeJob_select" style="width:160"> 
                						<option value='<bean:message key="tree.unroot.undesc"/><bean:message key="tree.umroot.umdesc"/>'><bean:message key="tree.unroot.undesc"/><bean:message key="tree.umroot.umdesc"/></option>
                						<option value='<bean:message key="tree.unroot.undesc"/>'><bean:message key="tree.unroot.undesc"/></option>
                						<option value='<bean:message key="tree.umroot.umdesc"/>'><bean:message key="tree.umroot.umdesc"/></option>
                						<option value=''></option>
                					</select>
                				</td>
                			</tr>
                 		</table>		
                 		</div>
                 		<div id="sorting"  style="display:none"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td id="sortingnumber" width="80" style="word-break: break-all; word-wrap:break-word;">
                					<bean:message key="org.maip.sorting"/>
                				</td>
                				<td>
                					<select name="sortingnum" id="sortingnum" style="width:160">
                						<option value='0'><bean:message key="org.maip.sorting.no"/></option>
                						<option value='1'><bean:message key="org.maip.sorting.yes"/></option>
                					</select>
                				</td>
                			</tr>
                 		</table>		
                 		</div>
                 		<div id="codeMax"  style="display:none"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<div id="codeMaxname"></div>
                				</td>
                				<td>
                					<select name="code_maxarr" id="code_maxarr" style="width:160">
             						</select>
                				</td>
                			</tr>
                 		</table>		
                 		</div>
                 		<div id="codeMin"  style="display:none"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<div id="codeMinname"></div>
                				</td>
                				<td>
                					<select name="code_minarr" id="code_minarr" style="width:160">
             						</select>
                				</td>
                			</tr>
                 		</table>		
                 		</div>
                 		<div id="stand"  style="display:none"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<div id="standname"></div>
                				</td>
                				<td>
                					<html:select name="projectForm" styleId="standid" property="standid" onchange="standSelect('${projectForm.salaryid}','${projectForm.tabid}','${projectForm.checktemp}');" style="width:160">
			 							<html:optionsCollection property="standlist" value="dataValue" label="dataName" />
									</html:select>  
                				</td>
                			</tr>
                 		 </table>		
                  		</div>
                  		
                  		<div id="standTzTd" style="display:none"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<div id="standhlname"></div>
                				</td>
                				<td>
                					<html:select name="projectForm" property="standhlid" styleId="standhlid" onchange="standSelect2('${projectForm.salaryid}','${projectForm.tabid}','${projectForm.checktemp}');" style="width:160">
			 							<html:optionsCollection property="standidlist" value="dataValue" label="dataName" />
									</html:select>  
                				</td>
                			</tr>
                 		 </table>		
                  		</div>
                  		
                  		<div id="standhHighLow" style="display:none"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<div id="standhlname1"></div>
                				</td>
                				<td>
                					<html:select name="projectForm" property="standhlid" styleId="standhlid" onchange="changeItemValue('${projectForm.salaryid}','${projectForm.tabid}','${projectForm.checktemp}');" style="width:160">
			 							<html:optionsCollection property="standidlist" value="dataValue" label="dataName" />
									</html:select>  
                				</td>
                			</tr>
                 		 </table>		
                  		</div>
                  		
                  		<!--判断是哪种点击进来的，是执行标准还是就近就高等这些-->
                  		<div id="lastFormulaName" style="display:none"></div>
                  		
                  		<div id="standHfactor"  style="display:none"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<div id="hfactorname"></div>
                				</td>
                				<td>
                					<select name="hfactor_arr" id="hfactor_arr" style="width:160">
             						</select>
                				</td>
                			</tr>
                 		</table>		
                 		</div>
                 		<div id="standS_hfactor"  style="display:none"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<div id="s_hfactorname"></div>
                				</td>
                				<td>
                					<select name="s_hfactor_arr" id="s_hfactor_arr" style="width:160">
             						</select>
                				</td>
                			</tr>
                 		</table>		
                 		</div>
                 		<div id="standVfactor"  style="display:none"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<div id="vfactorname"></div>
                				</td>
                				<td>
                					<select name="vfactor_arr" id="vfactor_arr" style="width:160">
             						</select>
                				</td>
                			</tr>
                 		</table>		
                 		</div>
                 		<div id="standS_vfactor" style="display:none"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<div id="s_vfactorname"></div>
                				</td>
                				<td>
                					<select name="s_vfactor_arr" id="s_vfactor_arr" style="width:160">
             						</select>
                				</td>
                			</tr>
                 		</table>		
                 		</div>
                 		<div id="standItem" style="display:none"> 
                  		<table width="100%" height="30" border="0">
                			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;">
                					<div id="itemname"></div>
                				</td>
                				<td>
                					<select name="item" id="item" style="width:160">
             						</select>
                				</td>
                			</tr>
                 		</table>		
                 		</div>
                 		<!--新增临时变量，为了取自于函数-->
                 		<div id="getFrom"  style="display:none"> 
                  		<table width="100%" height="30" border="0">
                  			<tr>
                				<td width="80" style="word-break: break-all; word-wrap:break-word;"><bean:message key="label.gz.variable"/></td>
                				<td>
                					<html:select name="projectForm" property="fieldname" styleId="fieldname" style="width:160">
			 							<html:optionsCollection property="tempfieldsetlist"  value="dataValue" label="dataName" />
									</html:select>  
                				</td>
                			</tr>
                 		 </table>		
                  		</div>
                  </td>
              	</tr>
            </table>
            <!-- </fieldset> -->
            </div>
            </div> 
           </td>
        </tr>
      </table> </td>
    <td width="16%" valign="top" rowspan="3"> <table width="100%" border="0" style="margin-top:19px">
        <tr> 
          <td height="40" align="left" valign="bottom"> <div id="stepDark"> 
            <input type="button" name="nextstepDark" value=' <bean:message key="label.zp_options.down_step"/> ' class="mybutton">
            </div> <div id="stepBrilliant"  style="display:none">
            <input type="button" name="nextstepBrilliant" value=' <bean:message key="label.zp_options.down_step"/> ' onclick="nextStep();subsetfunction();" Class="mybutton">
            </div> </td>
        </tr>
        <tr> 
          <td height="40" align="left" valign="bottom"> <div id="darkReturnStep"> 
            <input type="button" name="darkstep" value=' <bean:message key="label.zp_options.up_step"/> ' class="mybutton">
            </div> <div id="brilliantReturnStep"  style="display:none">
            <input type="button" name="brilliantstep" value=' <bean:message key="label.zp_options.up_step"/> ' onclick="returnStep();" Class="mybutton">
            </div> </td>
        </tr>
        <tr> 
          <td height="40" align="left" valign="bottom">
          	<div id="darkCompleted"> 
            <input type="button" name="completeddark" value=' <bean:message key="button.muster.finished"/> ' class="mybutton">
            </div>
            <div id="brilliantCompleted"  style="display:none">
            <input type="button" name="completedbrilliant" value=' <bean:message key="button.muster.finished"/> ' onclick="completed();"  Class="mybutton">
            </div>
          </td>
        </tr>
      </table></td>
  </tr>
  <tr> 
    <td height="14"><div id="note" class="node"><bean:message key="label.description"/><bean:message key="hire.zp_options.semicolon"/><bean:message key="kq.formula.number"/></div></td>
  </tr>
  <tr> 
    <td height="20" align="center">
    	<table width="99%" height="20" border="0">
       		<tr>
       			<td valign="top">
       				<div id="calculation" class="node"></div>
      				<input type="hidden" name="formula" value="" id="formula"> 
      				<input type="hidden" name="id" value="" id="id"> 
      				<input type="hidden" name="attribute" value="">
				</td>
       		</tr>
       </table> 
      </td>
  </tr>
</table>
	</div>
</html:form>
<script language="javaScript">
	document.getElementById('wizarddiv').style.height=document.body.clientHeight-30;

</script>


