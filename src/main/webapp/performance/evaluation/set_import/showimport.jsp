<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="evaluation.js"></script>
<script language="javascript" src="/js/common.js"></script>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<STYLE type=text/css>
.div2
{
 overflow:auto; 
 width: 98%;height: 170px;
 line-height:15px; 
 border-width:1px; 
 border-style: groove;
 border-width:thin ;
 border: inset 1px #C4D8EE;
 BORDER-BOTTOM:#C4D8EE 1pt solid;
 BORDER-LEFT: #C4D8EE 1pt solid; 
 BORDER-RIGHT: #C4D8EE 1pt solid; 
 BORDER-TOP: #C4D8EE 1pt solid;
}

</STYLE>
<hrms:themes />
<html>
<%int i=1,j=1;%>
<script language='javascript' >
var obj_tr;
var uplist = new Array();
var downlist = new Array();
var selPlans = new Array();
function replaceAll(str, sptr, sptr1)
{
	while (str.indexOf(sptr) >= 0)
	{
   		str = str.replace(sptr, sptr1);
	}
	return str;
} 
function saverelate()
{
	var tabup=document.getElementById("up");
	var list = new Array();
	for(i=1;i<tabup.rows.length;i++)
	{
		list.push(ltrim(rtrim(tabup.rows[i].cells[1].innerHTML))+':'+'');
		//replaceAll(ltrim(rtrim(tabup.rows[i].cells[6].innerHTML)),',','`')
	}
	//list.push('21:Score`Avg`');
	var hashvo=new ParameterSet();
	hashvo.setValue("list",list);
	hashvo.setValue("planid",'${importForm.planid}');
	var request=new Request({method:'post',onSuccess:showprompt,functionId:'9024003001'},hashvo);
}
function showprompt(outparamters)
{
	var mess = outparamters.getValue("mess");
	if(window.showModalDialog){
        parent.window.returnValue='ok';
	}else {
		parent.parent.importexpre_ok('ok')
	}
}
function close_showimport() {
	if(!window.showModalDialog){
		var win = null;
		if(parent.parent.Ext){
			win = parent.parent.Ext.getCmp('importexpre_win');
		}
		if(win) {
			win.close();
		}else{
			parent.window.close();
		}
	}else{
		parent.window.close();
	}
}
function uprelate()
{
	var tabup=document.getElementById("up");
	var list = new Array();
	for(i=1;i<tabup.rows.length;i++)
	{
		list.push(ltrim(rtrim(tabup.rows[i].cells[1].innerHTML))+':'+'');
//		list.push(tabup.rows[i].cells[1].innerHTML);
	}
	var hashvo=new ParameterSet();
	hashvo.setValue("list",list);
	hashvo.setValue("planid",'${importForm.planid}');
	var request=new Request({method:'post',onSuccess:showprompt2,functionId:'9024003001'},hashvo);
}
function showprompt2(outparamters)
{
	var mess = outparamters.getValue("mess");
	//window.dialogArguments.window.location =window.dialogArguments.window.location;
	
	if(window.showModalDialog){
        parent.window.returnValue=mess;
	}else {
 		parent.window.opener.importexpre1_ok(mess);
	}
	window.close();
}
function set()
{
	var n = selPlans.length;
	/*alert(n);
	for(var x=0;x<selPlans.length;x++)
	{
		alert(selPlans[x]);
	}*/
	if(n!=1)
	{
		alert('请选择一个关联计划进行设置!');
		return;
	}
	var planMenu='';
	var planid = ltrim(rtrim(selPlans[0]));
	var tabup=document.getElementById("up");
	for(i=1;i<tabup.rows.length;i++)
	{	
		if(ltrim(rtrim(tabup.rows[i].cells[1].innerHTML))==planid)
		{
			planMenu=tabup.rows[i].cells[6].innerHTML;
			break;
		}
    }	
	var strurl="/performance/evaluation/calculate.do?b_option=link`planid=${importForm.planid}`relaPlan="+planid+'`planMenu='+replaceAll(planMenu,'`',',');
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
	var return_vo=window.showModalDialog(iframe_url,arguments,"dialogWidth=500px;dialogHeight=400px;resizable=yes;scroll=no;status=no;");  
	if(return_vo==null)
		return false;	
	tabup=document.getElementById("up");
	for(i=1;i<tabup.rows.length;i++)
	{
		if(parseInt(tabup.rows[i].cells[1].innerHTML)==parseInt(planid))
		{
			tabup.rows[i].cells[6].innerHTML=return_vo.menus;
			break;
		}
	}
	
}
</script>  

  <head>
  
  </head>
  
  <body>

  <html:form action="/performance/evaluation/calculate">  
	<table width="100%">
		<tr>
			<td>
				<table width="100%" border="0" cellspacing="1"  align="center" cellpadding="2">
		<tr>
			<td style="height:20px">
				<bean:message key="jx.evaluation.associateplan"/>		   	
			</td>
			</tr>
				<tr>
			<td>
				<%
					com.hrms.struts.valueobject.UserView userView = (com.hrms.struts.valueobject.UserView)
									session.getAttribute(com.hrms.struts.constant.WebConstant.userView);
					String bosFlag = userView.getBosflag();
					String top = "";
					top += bosFlag.equalsIgnoreCase("hcm") ? 38 : 30;
				%>
			 				<div class="div2 common_border_color" class='complex_border_color' style='position:absolute;top:<%=top %>px;z-index:0;left:10px;'>
		    		<table id="up" width="100%"  align="center" border="0" cellspacing="0" cellpadding="0" class="ListTable">
					 <thead>
					    <tr >
							<td align="center" class="TableRow common_background_color common_border_color" style="border-left:0px;border-top:0px;" nowrap>
							<input type="checkbox" name="selbox"
										onclick="batch_select(this, 'up');choiceidAll('up')">
										
							</td>
							<td align="center" style="border-top:0px;" class="TableRow" nowrap>
								<bean:message key="jx.khplan.plannumber"/>
							</td>
							<td align="center" style="border-top:0px;" class="TableRow" nowrap>
								<bean:message key="userlist.username"/>
							</td>
							<td align="center" style="border-top:0px;" class="TableRow" nowrap>
								<bean:message key="jx.khplan.cycle"/>
							</td>
							<td align="center" style="border-top:0px;" class="TableRow" nowrap>
								<bean:message key="jx.khplan.timeframe"/>
							</td>
							<td align="center" style="border-top:0px;border-right:0px;" class="TableRow" nowrap>
								<bean:message key="jx.khplan.objectype"/>
							</td>						
						 </tr>
					 </thead>
					  <logic:iterate id="element" name="importForm" property="relatelist">
					 	<tr class="trDeep" style="border-left:0px;" id="up_<%=i%>">
					 		<td width="8%" align="center" class="RecordRow" style="border-left:0px;" nowrap>
					 			<input type="checkbox" name="up_<%=i%>"  onclick="choiceiddown(this)"  value='<bean:write name="element" property="plan_id" />'/>
					 		</td>
			        		<td width="10%" align="center" class="RecordRow" nowrap>
			        			<bean:write name="element" property="plan_id" />
			        		</td>
			        		<td width="40%" align="left	" class="RecordRow" nowrap>
			        			&nbsp;<bean:write name="element" property="name" />
			        		</td>
			        		<td width="10%" align="left" class="RecordRow" nowrap>
			        			&nbsp;<bean:write name="element" property="cyclegb" />
			        		</td>
			        		<logic:equal name="element" property="cycle" value="0">
			        			<td width="22%" align="left" class="RecordRow" nowrap>
				        			&nbsp;<bean:write name="element" property="theyeargb" />
				        		</td>
			        		</logic:equal>
			        		<logic:equal name="element" property="cycle" value="1">
			        			<td width="22%" align="left" class="RecordRow" nowrap>
			        			&nbsp;<bean:write name="element" property="halfyeargb" />
				        		</td>
			        		</logic:equal>
			        		<logic:equal name="element" property="cycle" value="2">
			        			<td width="22%" align="left" class="RecordRow" nowrap>
				        			&nbsp;<bean:write name="element" property="thequartergb" />
				        		</td>
			        		</logic:equal>
			        		<logic:equal name="element" property="cycle" value="3">
			        			<td width="22%" align="left" class="RecordRow" nowrap>
				        			&nbsp;<bean:write name="element" property="themonthgb" />
				        		</td>
			        		</logic:equal>
			        		<logic:equal name="element" property="cycle" value="7">
			        			<td width="22%" align="left" class="RecordRow" nowrap>
				        			&nbsp;<bean:write name="element" property="yeartoyear" />
				        		</td>
			        		</logic:equal>
			        		<td width="10%" align="left" class="RecordRow" style="border-right:0px" nowrap>
			        			&nbsp;<bean:write name="element" property="object_typegb" />
			        		</td>
			       
			        	</tr>
			        	<%i++;%>
					 </logic:iterate>
		    		</table>
		    		</div>

		    </td>
		</tr>
		<TR align="center">
			<td>
				<div style='position:absolute;top:218px;left:43%;z-index:0;'>	
				<html:button styleClass="mybutton" property="b_up" onclick="up()">
					<bean:message key="button.previous" />
				</html:button>
				<html:button styleClass="mybutton" property="b_down" onclick="down()">
					<bean:message key="button.next" />
				</html:button>
			</div>
			</td>

		</TR>
		<tr>
			<td >
		    	<div style='position:absolute;top:250px;z-index:0;'>	<bean:message key="jx.evaluation.choiceplan"/>		</div>
		    		</td>

		</tr>
		<tr>
		<td>
		    	  <div class="div2 common_border_color" style='position:absolute;top:270px;z-index:0;left:10px;'>
		    		<table id="down" width="100%" id ="tt" align="center" border="0" cellspacing="0" cellpadding="0" class="ListTable">
					 <thead>
					    <tr class="fixedHeaderTr">
							<td align="center" style="border-left:0px;border-top:0px;" class="TableRow common_background_color common_border_color" nowrap>
								 <input type="checkbox" name="selbox"
										onclick="batch_select(this, 'down');;choiceidAll('down')">
									
							</td>
							<td align="center" style="border-top:0px;" class="TableRow" nowrap>
								<bean:message key="jx.khplan.plannumber"/>
							</td>
							<td align="center" style="border-top:0px;" class="TableRow" nowrap>
								<bean:message key="userlist.username"/>
							</td>
							<td align="center" style="border-top:0px;" class="TableRow" nowrap>
								<bean:message key="jx.khplan.cycle"/>
							</td>
							<td align="center" style="border-top:0px;" class="TableRow" nowrap>
								<bean:message key="jx.khplan.timeframe"/>
							</td>
							<td align="center" style="border-top:0px;" class="TableRow_left" nowrap>
								<bean:message key="jx.khplan.objectype"/>
							</td>
				
						 </tr>
					 </thead>
					 <logic:iterate id="element1" name="importForm" property="choicelist">
					 	<tr class="trDeep" id="down_<%=j%>">
					 		<td width="8%" style="border-left:0px;" align="center" class="RecordRow"  nowrap>
					 			<input type="checkbox" name="down_<%=j%>"   onclick="choiceidup(this)" value='<bean:write name="element1" property="plan_id" />'/>
					 		</td>
			        		<td width="10%" align="center" class="RecordRow_left" nowrap>
			        			<bean:write name="element1" property="plan_id" />
			        		</td>
			        		<td width="40%" align="left	" class="RecordRow_left" nowrap>
			        			&nbsp;<bean:write name="element1" property="name" />
			        		</td>
			        		<td width="10%" align="left" class="RecordRow_left" nowrap>
			        			&nbsp;<bean:write name="element1" property="cyclegb" />
			        		</td>
			        		<logic:equal name="element1" property="cycle" value="0">
			        			<td width="22%" align="left" class="RecordRow_left" nowrap>
				        			&nbsp;<bean:write name="element1" property="theyeargb" />
				        		</td>
			        		</logic:equal>
			        		<logic:equal name="element1" property="cycle" value="1">
			        			<td width="22%" align="left" class="RecordRow_left" nowrap>
			        				&nbsp;<bean:write name="element1" property="halfyeargb" />
				        		</td>
			        		</logic:equal>
			        		<logic:equal name="element1" property="cycle" value="2">
			        			<td width="22%" align="left" class="RecordRow_left" nowrap>
				        			&nbsp;<bean:write name="element1" property="thequartergb" />
				        		</td>
			        		</logic:equal>
			        		<logic:equal name="element1" property="cycle" value="3">
			        			<td width="22%" align="left" class="RecordRow_left" nowrap>
				        			&nbsp;<bean:write name="element1" property="themonthgb" />
				        		</td>
			        		</logic:equal>
			        		<logic:equal name="element1" property="cycle" value="7">
			        			<td width="22%" align="left" class="RecordRow_left" nowrap>
				        			&nbsp;<bean:write name="element1" property="yeartoyear" />
				        		</td>
			        		</logic:equal>
			        		<td width="10%" align="left" class="RecordRow_left"  nowrap>
			        			&nbsp;<bean:write name="element1" property="object_typegb" />
			        		</td>
			     
			        	</tr>
			        	<%j++;%>
					 </logic:iterate>
		    		</table>
		    		</div>

		    </td>
		</tr>
		<TR align="center">
		<td>
			<div style='position:absolute;top:450px;left:43%;z-index:0;'>	
		    <input type="button" name="cancel" style="display:none" value="<bean:message key="conlumn.investigate.questionItem"/>" class="mybutton" onclick="set();">
			<logic:equal name="importForm" property="flag" value="plan">
				<input type="button" name="save" value="<bean:message key="button.save"/>" class="mybutton" onclick="saverelate()">
			</logic:equal>
			<logic:equal name="importForm" property="flag" value="expr">
				<input type="button" name="save" value="<bean:message key="button.ok"/>" class="mybutton" onclick="uprelate()">
			</logic:equal>			
			<input type="button" name="cancel" value="<bean:message key="button.cancel"/>" class="mybutton" onclick="close_showimport();">   
				</div> 		
    	</td>
		</TR>
    </table>    	
			</td>
		</tr>
	</html:form>
  
  </body>


</html>
