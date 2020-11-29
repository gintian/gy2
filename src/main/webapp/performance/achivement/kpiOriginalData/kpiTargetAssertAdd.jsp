<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="java.util.*, 
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant,
				 com.hrms.hjsj.sys.EncryptLockClient" %>

<%
    
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	
	//  绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围    JinChunhai 2011.05.11	
	String operOrg =userView.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理	
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
	
%>

<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT> 
<script language="JavaScript" src="/js/validateDate.js"></script>    
<SCRIPT LANGUAGE=javascript src="/js/function.js"></SCRIPT> 
<SCRIPT LANGUAGE=javascript src="/js/constant.js"></SCRIPT> 
<SCRIPT Language="JavaScript">dateFormat='yyyy-mm-dd'</SCRIPT>
<script language="javascript" src="/performance/achivement/kpiOriginalData/kpiOriginalData.js"></script>
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
<script language="JavaScript">

function getorg()
{
//	var ret_vo=select_org_emp_dialog(0,1,0,1,0,1);

	var ret_vo;
	<% if (operOrg.length() > 2){ %>
		ret_vo=select_kpiorg_dialog(0,1,0,1,0,1,1,5);   	 	  	  			
	<% }else{ %>	
		ret_vo=select_kpiorg_dialog(0,1,0,1,0,1,0,5);
	<% } %>
	 if(ret_vo)
	{
		if(ret_vo==null)
			return false;
		var	re=/,/g;
		var tmp=ret_vo.content;
		var str=tmp.replace(re,"`");	
				
		$('kpiBean.b0110').value=str;
		$('kpiBean.b0110desc').value=ret_vo.title;
	}else{
		 var interval = setInterval(function(){
				if(window.returnValue){
					ret_vo = window.returnValue;
					var	re=/,/g;
					var tmp=ret_vo.content;
					var str=tmp.replace(re,"`");	
					$('kpiBean.b0110').value=str;
					$('kpiBean.b0110desc').value=ret_vo.title;
					 clearInterval(interval);
				}else
					return;
	     },300);
	}
}

function screenbyChange(obj,flag)
{
  	var targetvalue=document.getElementById(flag); 
  	targetvalue.value=obj.value; 
  	
//  	alert(obj.value);
//  	alert(targetvalue.value);
}

function addDict(obj,event,flag)
{ 
   	var evt = event ? event : (window.event ? window.event : null);
   	var np = evt.keyCode; 
   	if(np==38 || np==40)
   	{ 
   
   	} 
   	var textv=obj.value;
   	var aTag;
   	aTag = obj.offsetParent; 
   	if(textv==null||textv=="")
	   	return false;
   	textv=textv.toLowerCase();  
   	var un_vos=document.getElementsByName(flag+"_value");
   	if(!un_vos)
		return false;
   	var unStrs=un_vos[0].value;	
   	var unArrs=unStrs.split(",");
   	var c=0;
   	var rs =new Array();
   	for(var i=0;i<unArrs.length;i++)
   	{
		var un_str=unArrs[i];
		if(un_str)
		{
		    if(un_str.indexOf(textv)!=-1)
	        {
			    rs[c]="<tr id='tv' name='tv'><td id='al"+c+"' onclick=\"onV("+c+",'"+flag+"')\" style='height:15;cursor:pointer' onmouseover='alterBg("+c+",0)' onmouseout='alterBg("+c+",1)' nowrap class=tdFontcolor>"+un_str+"</td></tr>"; 
                c++;
		    }		 
		}       
	}
    resultuser=rs.join("");
    if(textv.length==0)
    { 
       	resultuser=""; 
    } 
    document.getElementById("dict").innerHTML="<table width='100%' class='div_table' cellpadding='2' border='0' bgcolor='#FFFFFF' cellspacing='2'>"+resultuser+"</table>";//???????????????? 
    document.getElementById('dict').style.display = "";
    document.getElementById('dict').style.position="absolute";	
	document.getElementById('dict').style.left=aTag.offsetLeft+"px";
   	document.getElementById('dict').style.top=aTag.offsetTop+20+"px";
} 
function onV(j,flag)
{
   	var o = document.getElementById('al'+j).innerHTML; 
   	document.getElementById(flag).value=o; 
   	document.getElementById(flag+"select").value=o;
   	document.getElementById('dict').style.display = "none";
} 
function alterBg(j,i)
{
    var o = document.getElementById('al'+j); 
    if(i==0) 
       o.style.backgroundColor = "#3366cc"; 
    else   if(i==1) 
       o.style.backgroundColor = "#FFFFFF"; 
}
function hiddendict()
{
	document.getElementById('dict').style.display = 'none';
}

function valid(obj) {
	if(!validate(obj)) {
		obj.focus(); 
		if(obj.id == "start_date") {
			obj.value=document.getElementById("starDate").value; 
		}else {
			obj.value=document.getElementById("enDate").value;
		}
	}
}
</script>
<style>

.div_table
{
    border-width: 1px;
    BORDER-BOTTOM: #aeac9f 1pt solid; 
    BORDER-LEFT: #aeac9f 1pt solid; 
    BORDER-RIGHT: #aeac9f 1pt solid; 
    BORDER-TOP: #aeac9f 1pt solid ; 
}
.tdFontcolor
{
	text-decoration: none;
	Font-family:????;
	font-size:12px;
	height=20px;
	align="center"
}

</style>

<body onclick="hiddendict();">
<html:hidden name="kpiOriginalDataForm" styleId="starDate" property="kpiBean.start_date"/>
<html:hidden name="kpiOriginalDataForm" styleId="enDate" property="kpiBean.end_date"/>
<html:form action="/performance/achivement/kpiOriginalData/kpiTargetAssertList">
	<html:hidden name="kpiOriginalDataForm" styleId="hidKpiItemType" property="hidKpiItemType"/>

	<table width="60%" align="center" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td valign="top">
				<table width="100%" border="0" cellpadding="3" cellspacing="0"
					align="center" class="ListTableF">
					<tr height="20">
						<td colspan="2" align="left" class="TableRow">
							<bean:message key="kpi.originalData.KpiTarget" />&nbsp;
						</td>
					</tr>
					<tr class="trDeep1">
						<td align="left" class="RecordRow" nowrap>
							<bean:message key="kpi.originalData.targetNumber" />
						</td>
						<td align="left" class="RecordRow" nowrap>
							<html:text maxlength="50" size="30" styleClass=""
								name="kpiOriginalDataForm" styleId="item_id"
								property="kpiBean.item_id" readonly="true" />
						</td>												
					</tr>
					<tr class="trShallow1">						
						<td align="left" class="RecordRow" nowrap>
							<bean:message key="kpi.originalData.targetName" />
						</td>
						<td align="left" class="RecordRow" nowrap>
							<html:text maxlength="50" size="30" styleClass="textColorWrite"
								name="kpiOriginalDataForm" styleId="itemdesc"
								property="kpiBean.itemdesc" />&nbsp;&nbsp;<font color='red'>*</font>
						</td>						
					</tr>
					<tr class="trDeep1">
						<td align="left" class="RecordRow" nowrap>
							<bean:message key="kpi.originalData.targetType" />
						</td>
						
<!--						
						<td align="left" class="RecordRow" nowrap>
							<html:text maxlength="50" size="30" styleClass="textColorWrite"
								name="kpiOriginalDataForm" styleId="item_type_desc"
								property="kpiBean.item_type_desc" />&nbsp;&nbsp;<font color='red'>*</font>
						</td>
-->						
						
                		<td align="left" class="RecordRow" nowrap>    
                		<%if("hl".equals(hcmflag)){ %>         			
	                		<div style="position:absolute;z-index:1;width:220px;top:68px;height:20px"> 
	                	<%}else{ %>
	                		<div style="position:absolute;z-index:1;width:220px;top:110px;height:20px"> 
	                	<%} %>  
	                  			<html:select name="kpiOriginalDataForm" property='kpiItemType' styleId="hidKpiItemTypeSignselect" style="width:220px;height:20px;clip:rect(0 220 20 204)" onchange="screenbyChange(this,'hidKpiItemTypeSign');" onfocus=''>   									
									<html:optionsCollection property="kpiItemTypeList" value="dataValue" label="dataName" />
	            	  			</html:select>
	            			</div>  
	            		<%if("hl".equals(hcmflag)){ %>
								<div style="position:absolute;z-index:2;top:68px;width:204px;height:20px">
						<%}else{ %>
								<div style="position:absolute;z-index:2;top:110px;width:204px;height:20px">
						<%} %>            			    					    
	            			<input name=kpiItemType id='hidKpiItemTypeSign' style="position:absolute;width:202px; height:20px;" value='${kpiOriginalDataForm.kpiItemType }' onkeyup="addDict(document.getElementById('hidKpiItemTypeSign'),event,'hidKpiItemTypeSign');" >
	            			</div>
	            			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	            			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	            			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	            			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	            			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	            			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	            			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font color='red'>*</font>
	            			<input type="hidden" name="hidKpiItemTypeSign_value" value='${kpiOriginalDataForm.hidKpiItemType }' />
	            			
               			</td>            			
												
					</tr>
					<tr class="trShallow1">						
						<td align="left" class="RecordRow" nowrap>
							<bean:message key="kpi.originalData.targetCycle" />
						</td>
						<td align="left" class="RecordRow" nowrap>
							<html:radio name="kpiOriginalDataForm" styleId="cycle"
								property="kpiBean.cycle" value="0" />
							<bean:message key="kpi.originalData.targetYear" />
							<html:radio name="kpiOriginalDataForm" styleId="cycle"
								property="kpiBean.cycle" value="1" />
							<bean:message key="kpi.originalData.targetHalfYear" />
							<html:radio name="kpiOriginalDataForm" styleId="cycle"
								property="kpiBean.cycle" value="2" />
							<bean:message key="kpi.originalData.targetQuarter" />
							<html:radio name="kpiOriginalDataForm" styleId="cycle"
								property="kpiBean.cycle" value="3" />
							<bean:message key="kpi.originalData.targetMonth" />
						</td>
					</tr>
					<tr class="trDeep1">
						<td align="left" class="RecordRow" nowrap>
							<bean:message key="kpi.originalData.targetDateBegin" />
						</td>						
						<td align="left" class="RecordRow" nowrap>
							<html:text maxlength="50" size="30" styleClass="" onchange="valid(this)" 
								name="kpiOriginalDataForm" styleId="start_date" onclick="popUpCalendar(this,this, '','','',true,false,dateFormat);"
								property="kpiBean.start_date" />
						</td>												
					</tr>
					<tr class="trShallow1">						
						<td align="left" class="RecordRow" nowrap>
							<bean:message key="kpi.originalData.targetDateEnd" />
						</td>
						<td align="left" class="RecordRow" nowrap>
							<html:text maxlength="50" size="30" styleClass="" onchange="valid(this)" 
								name="kpiOriginalDataForm" styleId="end_date" onclick="popUpCalendar(this,this, '','','',true,false,dateFormat);"
								property="kpiBean.end_date" />
						</td>
					</tr>
					<tr class="trDeep1">
						<td align="left" class="RecordRow" nowrap>
							<bean:message key="kpi.originalData.targetAffiliationB0110" />
						</td>
						<td align="left" class="RecordRow" nowrap>
																			
							<html:text maxlength="50" size="30" 
								name="kpiOriginalDataForm" styleId="b0110"
								property="kpiBean.b0110desc" styleClass="" readonly="true" />
								<a onclick="getorg();">							
									<img src="/images/edit.gif" border=0>																					
								</a>
							<html:hidden name="kpiOriginalDataForm" styleId="unitB0110" property="kpiBean.b0110"/> 
						</td>
						
					</tr>														
					
					<tr class="trShallow1">
						<td colspan="2" class="RecordRow">
							<bean:message key="kpi.originalData.targetDescription" />
						</td>
					</tr>
					<tr>
						<td colspan="2" class="RecordRow">
							<table border="0" cellspacing="0" width="100%" 
								cellpadding="2" align="center">
								<tr  class="trDeep1">
									<td>
										<html:textarea name="kpiOriginalDataForm" styleId="description"
											property="kpiBean.description" style="width :600;height:65" cols="70" rows="6"
											styleClass="textColorWrite"></html:textarea>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					
				</table>
			</td>
		</tr>
		<tr> 
			<td>
				<table width='100%' align='center'>
					<tr>
						<td align='center' style="height:35px">
							<input type='button'
								value='<bean:message key='button.save' />'
								class="mybutton" onclick='saveKpiTarget();'>
							<input type='button' id="button_goback" 
								value='<bean:message key='button.return' />'
								onclick='goback();' class="mybutton">
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	<script>
		
		
	</script>
</html:form>
<div id="dict" style="display:none;z-index:+999;position:absolute;height:100px;width:220px;overflow:auto;bgcolor='#FFFFFF';"></div>
</body>
