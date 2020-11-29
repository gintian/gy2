<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>


<style>
<!--
.TableRoww {
	background-position : center left;
	font-size: 12px;  
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: medium none; 
	BORDER-TOP: medium none;
	height:22px;
	font-weight: bold;
	background-color:#f4f7f7;	
	/*
	color:#336699;
	*/
	valign:middle;
}
.TableRowv {
	background-position : center left;
	font-size: 12px;  
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT:medium none; 
	BORDER-TOP: medium none;
	height:22px;
	font-weight: bold;	
	valign:middle;
}
.ListTable2 {
	border:1px solid #8EC2E6;
	border-collapse:collapse; 
	BORDER-BOTTOM: medium none; 
    BORDER-LEFT: medium none; 
    BORDER-TOP: medium none; 
    BORDER-RIGHT: medium none;
}
.trShallow1 {  
	BORDER-RIGHT: medium none;
}

.trDeep1 {  
	BORDER-RIGHT: medium none;
}
.RecordRowP {
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: medium none;
	BORDER-TOP: #C4D8EE 0pt solid;
	font-size: 12px;
	margin-top:-1px;
	height:22;
}
.RecordRowy {
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: medium none;
	font-size: 12px;
	border-collapse:collapse; 
	height:22;
}
-->
</style>
<hrms:themes></hrms:themes>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>

<script type="text/javascript">

function showpos(){
subsetConfsetForm.action="/org/autostatic/confset/subsetconfset.do?b_query=link";
subsetConfsetForm.submit();
}
function jumpconfset(){
	//window.location.href="/org/autostatic/confset/subsetconfset.do?br_query=link";
    subsetConfsetForm.action="/org/autostatic/confset/datasynchro.do?b_init=link";
    subsetConfsetForm.submit();
}
function returnto(){
window.location.href="/org/autostatic/confset/subsetconfset.do?br_query=link";
window.location.target="_top";

}
function getsetchangflag(){
		var tablevos;
		var changeflag="";
      	tablevos=document.getElementsByTagName("SELECT");   
      	for(var i=0;i<tablevos.length;i++)
      	{
      		var setname=tablevos[i].name;
      		changeflag = changeflag+setname+","+tablevos[i].value+"/"
      	}
      	if(document.getElementById("changeflagstr")!=undefined){
            document.getElementById("changeflagstr").value=changeflag;
        }else if(document.getElementsByName("changeflagstr")!=undefined){
            var inputs=document.getElementsByName("changeflagstr");
            for(var i=0;i<inputs.length;i++){
                var input=inputs[i];
                input.value=changeflag;
            }
		}
	showpos()
}

function showsetinfo(setid)
{
	var obj = document.getElementById(setid);
	var obj1=document.getElementById(setid+"a");
	var setdesc='人员信息集';
	if(setid=='setB')
		setdesc='单位信息集';
	else if(setid=='setK')
		setdesc='岗位信息集';
	if(obj.style.display=='none'){
		obj.style.display='block';
		obj1.innerHTML='<img src=\"/images/tree_collapse.gif\" style=\"vertical-align: middle\" border=\"0\">'+setdesc;
	}else{
		obj.style.display='none';
		obj1.innerHTML='<img src=\"/images/tree_expand.gif\" style=\"vertical-align: middle\" border=\"0\">'+setdesc;
	}
}

</script>

<html:form action="/org/autostatic/confset/subsetconfset">
	<table align="center" border="0" width="60%">
		<%int i = 1;%>
		<tr>
			<td  width="100%">
				<table width="100%" border="0" cellspacing="0" align="left" cellpadding="0">
					<tr>
						<td>
							<html:hidden name="subsetConfsetForm" property="changeflagstr" />
							<table border="0" width="100%" cellspacing="0" cellpadding="0" align="center" class="ListTable1">
								<tr class="TableRow">
									<td width="10%" align="center" class="TableRow" nowrap>
										<bean:message key="conlumn.mediainfo.info_id" />
									</td>
									<td width="70%" align="center" class="TableRow" nowrap>
										<bean:message key="set.label" />
									</td>
									<td align="center" width="20%" class="TableRow" nowrap>
										<bean:message key="organization.org.subset.change" />
									</td>
								</tr>
								<bean:define id="subsetlist" name="subsetConfsetForm"
									property="subsetlist" />
								<tr><td colspan="3" class="TableRoww common_border_color" nowrap valign="top">
											<a href="###" onclick="showsetinfo('setA')" id="setAa">
												<img src="/images/tree_expand.gif" border="0" style="vertical-align: middle">人员信息集</a>
									</td>
								</tr>
								<tr>
									<td colspan="3">
										<div id=setA style="display: none">
											<table border="0" width="100%" cellspacing="0" cellpadding="0" align="center" class="ListTable2 common_border_color">	
											<logic:iterate id="element" name="subsetConfsetForm"
												property="subsetlist">
												<logic:match value="A" location="start" name="element" property="fieldsetid">
														<tr class='<%=i%2==0?"trShallow1":"trDeep1"%>'>
																		<td class="RecordRowy common_border_color" width="10%" align="center" nowrap>
																			<%=i%>
																		</td>
								
																		<td class="RecordRowy common_border_color" width="70%" align="center" nowrap>
																			<bean:write name="element" property="customdesc" />
																		</td>
																		<td align="center" width="20%" class="TableRowv common_border_color" nowrap>
																			<select name="${element.fieldsetid }">
																				<logic:equal value="0" name="element" property="changeflag">
																					<option value="0" selected="selected">
																						<bean:message key="organization.org.subset.general" />
																					</option>
																					<option value="1">
																						<bean:message key="organization.org.subset.month" />
																					</option>
																					<option value="2">
																						<bean:message key="organization.org.subset.year" />
																					</option>
																				</logic:equal>
																				<logic:notEqual value="0" name="element"
																					property="changeflag">
																					<logic:equal value="1" name="element" property="changeflag">
																						<option value="0">
																							<bean:message key="organization.org.subset.general" />
																						</option>
																						<option value="1" selected="selected">
																							<bean:message key="organization.org.subset.month" />
																						</option>
																						<option value="2">
																							<bean:message key="organization.org.subset.year" />
																						</option>
																					</logic:equal>
																					<logic:equal value="2" name="element" property="changeflag">
																						<option value="0">
																							<bean:message key="organization.org.subset.general" />
																						</option>
																						<option value="1">
																							<bean:message key="organization.org.subset.month" />
																						</option>
																						<option value="2" selected="selected">
																							<bean:message key="organization.org.subset.year" />
																						</option>
																					</logic:equal>
																				</logic:notEqual>
																			</select>
																		</td>
								
																	</tr>
													<%
													i++;
													%>	
												</logic:match>	
											</logic:iterate>
										</table>		
										</div>				
									
						</td>
					</tr>
					<tr><td colspan="3" class="TableRoww common_border_color" nowrap valign="top">
											<a href="###" onclick="showsetinfo('setB')" id="setBa">
												<img src="/images/tree_expand.gif" border="0" align="middle" style="vertical-align: middle">单位信息集</a>
									</td>
								</tr>
								<tr>
									<td colspan="3">
										<div id=setB style="display: none">
											<table border="0" width="100%" cellspacing="0" cellpadding="0" align="center" class="ListTable2 common_border_color">	
											<logic:iterate id="element" name="subsetConfsetForm"
												property="subsetlist">
												<logic:match value="B" location="start" name="element" property="fieldsetid">
														<tr class='<%=i%2==0?"trShallow1":"trDeep1"%>'>
																		<td class="RecordRowy common_border_color" width="10%" align="center" nowrap>
																			<%=i%>
																		</td>
								
																		<td class="RecordRowy common_border_color" width="70%" align="center" nowrap>
																			<bean:write name="element" property="customdesc" />
																		</td>
																		<td align="center" width="20%" class="TableRowv common_border_color" nowrap>
																			<select name="${element.fieldsetid }">
																				<logic:equal value="0" name="element" property="changeflag">
																					<option value="0" selected="selected">
																						<bean:message key="organization.org.subset.general" />
																					</option>
																					<option value="1">
																						<bean:message key="organization.org.subset.month" />
																					</option>
																					<option value="2">
																						<bean:message key="organization.org.subset.year" />
																					</option>
																				</logic:equal>
																				<logic:notEqual value="0" name="element"
																					property="changeflag">
																					<logic:equal value="1" name="element" property="changeflag">
																						<option value="0">
																							<bean:message key="organization.org.subset.general" />
																						</option>
																						<option value="1" selected="selected">
																							<bean:message key="organization.org.subset.month" />
																						</option>
																						<option value="2">
																							<bean:message key="organization.org.subset.year" />
																						</option>
																					</logic:equal>
																					<logic:equal value="2" name="element" property="changeflag">
																						<option value="0">
																							<bean:message key="organization.org.subset.general" />
																						</option>
																						<option value="1">
																							<bean:message key="organization.org.subset.month" />
																						</option>
																						<option value="2" selected="selected">
																							<bean:message key="organization.org.subset.year" />
																						</option>
																					</logic:equal>
																				</logic:notEqual>
																			</select>
																		</td>
								
																	</tr>
													<%
													i++;
													%>	
												</logic:match>	
											</logic:iterate>
										</table>		
										</div>				
						</td>
					</tr>
					<tr ><td colspan="3" class="TableRoww common_border_color" nowrap>
											<a href="###" onclick="showsetinfo('setK')" id="setKa" >
												<img src="/images/tree_expand.gif" border="0" align="middle" style="vertical-align: middle">岗位信息集</a>
									</td>
								</tr>
								<tr>
									<td colspan="3">
										<div id=setK style="display: none">
											<table border="0" width="100%" cellspacing="0" cellpadding="0" align="center" class="ListTable2 common_border_color">	
											<logic:iterate id="element" name="subsetConfsetForm"
												property="subsetlist">
												<logic:match value="K" location="start" name="element" property="fieldsetid">
														<tr class='<%=i%2==0?"trShallow1":"trDeep1"%>'>
																		<td class="RecordRowy common_border_color" width="10%" align="center" nowrap>
																			<%=i%>
																		</td>
								
																		<td class="RecordRowy common_border_color" width="70%" align="center" nowrap>
																			<bean:write name="element" property="customdesc" />
																		</td>
																		<td align="center"  width="20%" class="TableRowv common_border_color" nowrap>
																			<select name="${element.fieldsetid }">
																				<logic:equal value="0" name="element" property="changeflag">
																					<option value="0" selected="selected">
																						<bean:message key="organization.org.subset.general" />
																					</option>
																					<option value="1">
																						<bean:message key="organization.org.subset.month" />
																					</option>
																					<option value="2">
																						<bean:message key="organization.org.subset.year" />
																					</option>
																				</logic:equal>
																				<logic:notEqual value="0" name="element"
																					property="changeflag">
																					<logic:equal value="1" name="element" property="changeflag">
																						<option value="0">
																							<bean:message key="organization.org.subset.general" />
																						</option>
																						<option value="1" selected="selected">
																							<bean:message key="organization.org.subset.month" />
																						</option>
																						<option value="2">
																							<bean:message key="organization.org.subset.year" />
																						</option>
																					</logic:equal>
																					<logic:equal value="2" name="element" property="changeflag">
																						<option value="0">
																							<bean:message key="organization.org.subset.general" />
																						</option>
																						<option value="1">
																							<bean:message key="organization.org.subset.month" />
																						</option>
																						<option value="2" selected="selected">
																							<bean:message key="organization.org.subset.year" />
																						</option>
																					</logic:equal>
																				</logic:notEqual>
																			</select>
																		</td>
								
																	</tr>
													<%
													i++;
													%>	
												</logic:match>	
											</logic:iterate>
										</table>		
										</div>				
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	</td>
	</tr>
	</table>
	<div style="margin-top: 5px;" align="center">
	   <button name="savechange" class="mybutton"
                                            onclick="getsetchangflag();"
                                            style="font-size:10pt">
                                            <bean:message key="button.save" />
                                        </button>
        <button name="return" class="mybutton"
                                            onclick="jumpconfset();"
                                            style="font-size:10pt">
                                            <bean:message key="button.return" />
                                        </button>
	</div>

</html:form>
<script>
if(!getBrowseVersion() || getBrowseVersion() == 10){//非ie兼容模式下 样式修改    wangb 20190308
	var ListTable1 = document.getElementsByClassName('ListTable1')[0];
	var trs = ListTable1.getElementsByTagName('tr');
	for(var i = 0 ; i < trs.length ; i++){
		if(!trs[i].getAttribute('class'))
			trs[i].style.borderRight="1px solid #8EC2E6";
	}
}

</script>


