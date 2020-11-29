<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="javascript"><!--
function savefieldOk(){  	  
     var hashvo=new ParameterSet();          
     var vos= document.getElementById("setid");  
     var khvalue=""; 
     for(var i=0;i<vos.length;i++){
     	var valueS=vos.options[i].value;
     	if(valueS!=null&&valueS.length>2){
     		khvalue+=valueS+",";
     	}
     }
     hashvo.setValue("khtitle","${configParameterForm.khtitle}"); 
    hashvo.setValue("khvalue",khvalue);  
    var request=new Request({method:'post',functionId:'9026006016'},hashvo);
    returnFirst();
}
function returnFirst(){
   	document.location= "/performance/totalrank/setfield.do?b_query=link";
}	 
--></script>
<html:form action="/performance/totalrank/setfield">
	<br>
	<br>
	<table width="500" border="0" cellspacing="0" align="center"
		cellpadding="0" class="ListTableF">
		<tr>
			<td align="center" class="TableRow" nowrap colspan="3">
				<logic:equal name="configParameterForm" property="khtitle" value="kh_set">
					考核结果子集
				</logic:equal>
				<logic:notEqual name="configParameterForm" property="khtitle" value="kh_set">
					工作业绩子集
				</logic:notEqual>
			</td>
		</tr>
		<tr>
			<td width="100%" align="center" nowrap>
				<table width="100%" align="center" border="0" cellspacing="0"  cellpadding="0" class="RecordRow">
					<tr>
						<td align="center" width="46%">
							<table align="center" width="100%">
								<tr>
									<td align="left">
										<bean:message key="system.param.sysinfosort.bsubset" />
										&nbsp;&nbsp;
									</td>
								</tr>
								<tr>
									<td align="center">
										<hrms:optioncollection name="configParameterForm"
											property="fieldList" collection="list" />
										<html:select property="fieldid" size="10" multiple="true"
											style="height:230px;width:100%;font-size:9pt"
											ondblclick="additem('fieldid','setid');removeitem('fieldid');">
											<html:options collection="list" property="dataValue"
												labelProperty="dataName" />
										</html:select>
									</td>

								</tr>

							</table>
						</td>

						<td width="8%" align="center">
							<html:button styleClass="mybutton" property="b_addfield"
								onclick="additem('fieldid','setid');removeitem('fieldid');">
								<bean:message key="button.setfield.addfield" />
							</html:button>
							<br>
							<br>
							<html:button styleClass="mybutton" property="b_delfield"
								onclick="additem('setid','fieldid');removeitem('setid');">
								<bean:message key="button.setfield.delfield" />
							</html:button>
						</td>


						<td width="46%" align="center">


							<table width="100%">
								<tr>
									<td width="100%" align="left">
										<bean:message key="system.param.sysinfosort.ysubset" />
										&nbsp;&nbsp;
									</td>
								</tr>
								<tr>
									<td width="100%" align="left">
										<hrms:optioncollection name="configParameterForm"
											property="setlist" collection="selectedlist" />
										<html:select property="setid" size="10" multiple="true"
											style="height:230px;width:100%;font-size:9pt" styleId="right"
											ondblclick="additem('setid','fieldid');removeitem('setid');">
											<html:options collection="selectedlist" property="dataValue"
												labelProperty="dataName" />
										</html:select>
									</td>
								</tr>
							</table>
						</td>
						<td width="8%" align="center">
							<html:button styleClass="mybutton" property="b_up"
								onclick="upItem($('setid'));">
								<bean:message key="button.previous" />
							</html:button>
							<br>
							<br>
							<html:button styleClass="mybutton" property="b_down"
								onclick="downItem($('setid'));">
								<bean:message key="button.next" />
							</html:button>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td align="center" class="RecordRow" nowrap colspan="3">
					<input type="button" name="btnreturn"
						value='<bean:message key="button.ok"/>' class="mybutton"
						onclick="savefieldOk();">
					<input type="button" name="btnreturn"
						value='返回' class="mybutton"
						onclick="returnFirst();">
			</td>
		</tr>
	</table>
</html:form>