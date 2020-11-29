<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script type="text/javascript">
<!--
//2保存，3删除
function save(oper,aparam)
{
   var hashVo=new ParameterSet();
   var fieldsetid=document.getElementById("postFieldSetId").value;
   var dmand=document.getElementById("demandFieldId").value;
   var pitemid=document.getElementById("postFieldItemId").value;
   if(oper=='2')
   {
     if(dmand==''||pitemid=='')
       return;
     aparam=dmand+"`"+pitemid;
   }
   hashVo.setValue("oper",oper);
   hashVo.setValue("fieldsetid",fieldsetid);
   hashVo.setValue("aparam",aparam);
   var param=document.getElementById("param").value;
   hashVo.setValue("param",param);
   var request=new Request({method:'post',asynchronous:false,onSuccess:save_ok,functionId:'90100170038'},hashVo);					   
}
function save_ok(outparameters)
{
   var demandFieldList=outparameters.getValue("demandFieldList");
	AjaxBind.bind(positionDemandForm.demandFieldId,demandFieldList);
	var postFieldItemList=outparameters.getValue("postFieldItemList");
	AjaxBind.bind(positionDemandForm.postFieldItemId,postFieldItemList);
	var table=getDecodeStr(outparameters.getValue("table"));
	document.getElementById("tabStr").innerHTML=table;
	var param=outparameters.getValue("param");
	document.getElementById("param").value=param;
}
function changeSet(oper)
{
   var dmand=document.getElementById("demandFieldId").value;
   var obj=document.getElementById("postFieldSetId");
   var param=document.getElementById("param").value;
   var hashVo=new ParameterSet();
   hashVo.setValue("oper",oper);
   hashVo.setValue("fieldsetid",obj.value);
   hashVo.setValue("zItemId",dmand);
   hashVo.setValue("param",param);
	var request=new Request({method:'post',asynchronous:false,onSuccess:change_ok,functionId:'90100170038'},hashVo);			
}
function change_ok(outparameters)
{
    var oper=outparameters.getValue("oper");
    if(oper=='1')
    {
        var itemList=outparameters.getValue("itemList");
        AjaxBind.bind(positionDemandForm.postFieldItemId,itemList);
    }
}
function saveParam()
{
   var hashVo=new ParameterSet();
   var param=document.getElementById("param").value;
   hashVo.setValue("param",param);
   var request=new Request({method:'post',asynchronous:false,onSuccess:saveParam_ok,functionId:'90100170037'},hashVo);	
}
function saveParam_ok(outparameters)
{
    alert("设置成功！");
   	window.close();	
}
//-->
</script>
<style>
<!--
.t_cell_locked2 {
	  /*  background-image:url(/images/listtableheader_deep-8.jpg);*/
		background-repeat:repeat;
		background-position : center left;
		BACKGROUND-COLOR: #f4f7f7;  
		BORDER-BOTTOM: #94B6E6 1pt solid; 
		BORDER-LEFT: #94B6E6 0pt solid; 
		BORDER-RIGHT: #94B6E6 1pt solid; 
		BORDER-TOP: #94B6E6 1pt solid;
		height:22;
		font-weight: bold;	
		valign:middle;
		COLOR : black;
		left: expression(document.getElementById("tabStr").scrollLeft); /*IE5+ only*/
		top: expression(document.getElementById("tabStr").scrollTop); /*IE5+ only*/
		position: relative;
		z-index: 20;
	
	}
	.t_cell_locked3 {
	  /*  background-image:url(/images/listtableheader_deep-8.jpg);*/
		background-repeat:repeat;
		background-position : center left;
		BACKGROUND-COLOR: #f4f7f7;  
		BORDER-BOTTOM: #94B6E6 1pt solid; 
		BORDER-LEFT: #94B6E6 0pt solid; 
		BORDER-RIGHT: #94B6E6 1pt solid; 
		BORDER-TOP: #94B6E6 1pt solid;
		height:22;
		font-weight: bold;	
		valign:middle;
		COLOR : black;
		left: expression(document.getElementById("tabStr").scrollLeft); /*IE5+ only*/
		top: expression(document.getElementById("tabStr").scrollTop); /*IE5+ only*/
		position: relative;
		z-index: 20;
	
	}
	.t_cell_locked1 {
	  /*  background-image:url(/images/listtableheader_deep-8.jpg);*/
		background-repeat:repeat;
		background-position : center left;
		BACKGROUND-COLOR: #f4f7f7;  
		BORDER-BOTTOM: #94B6E6 1pt solid; 
		BORDER-LEFT: #94B6E6 0pt solid; 
		BORDER-RIGHT: #94B6E6 0pt solid; 
		BORDER-TOP: #94B6E6 1pt solid;
		height:22;
		font-weight: bold;	
		valign:middle;
		COLOR : black;
		left: expression(document.getElementById("tabStr").scrollLeft); /*IE5+ only*/
		top: expression(document.getElementById("tabStr").scrollTop); /*IE5+ only*/
		position: relative;
		z-index: 20;
	
	}
 .t_cell_locked {
		border: inset 1px #94B6E6;
		BACKGROUND-COLOR: #ffffff;
		BORDER-BOTTOM: #94B6E6 1pt solid; 
		BORDER-LEFT: #94B6E6 1pt solid; 
		BORDER-RIGHT: #94B6E6 1pt solid; 
		BORDER-TOP: #94B6E6 0pt solid;
		font-size: 12px;
		border-collapse:collapse; 
		height:22;
	
		background-position : center left;
		left: expression(document.getElementById("tabStr").scrollLeft); /*IE5+ only*/
		position: relative;
		z-index: 10;
	
	}
	.t_cell_locked_r {
		BACKGROUND-COLOR: #ffffff;
		BORDER-BOTTOM: #94B6E6 1pt solid; 
		BORDER-LEFT: #94B6E6 0pt solid; 
		BORDER-RIGHT: #94B6E6 0pt solid; 
		BORDER-TOP: #94B6E6 0pt solid;
		font-size: 12px;
		border-collapse:collapse; 
		height:22;
	
		background-position : center left;
		left: expression(document.getElementById("tabStr").scrollLeft); /*IE5+ only*/
		position: relative;
		z-index: 10;
	
	}
	 .t_cell_lockedg {
		BACKGROUND-COLOR: #ffffff;
		BORDER-BOTTOM: #94B6E6 1pt solid; 
		BORDER-LEFT: #94B6E6 0pt solid; 
		BORDER-RIGHT: #94B6E6 1pt solid; 
		BORDER-TOP: #94B6E6 0pt solid;
		font-size: 12px;
		border-collapse:collapse; 
		height:22;
	
		background-position : center left;
		left: expression(document.getElementById("tabStr").scrollLeft); /*IE5+ only*/
		position: relative;
		z-index: 10;
	
	}
	.t_cell_lockedg_l {
		BACKGROUND-COLOR: #ffffff;
		BORDER-BOTTOM: #94B6E6 1pt solid; 
		BORDER-LEFT: #94B6E6 0pt solid; 
		BORDER-RIGHT: #94B6E6 1pt solid; 
		BORDER-TOP: #94B6E6 0pt solid;
		font-size: 12px;
		border-collapse:collapse; 
		height:22;
	
		background-position : center left;
		left: expression(document.getElementById("tabStr").scrollLeft); /*IE5+ only*/
		position: relative;
		z-index: 10;
	
	}
	.div_modify{
	   border:1px solid #C4D8EE;
	   border-top:0px solid #C4D8EE;
	}
-->
</style>
<hrms:themes></hrms:themes>
<%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String bosflag= userView.getBosflag();//得到系统的版本号
    String _width="95%";
    if(bosflag!=null&&bosflag.equals("hcm")){
        _width="680px";
%>
    <link href="/hire/css/layout.css" rel="stylesheet" type="text/css">
<%
    }
 
%>
<html:form action="/hire/demandPlan/positionDemand/post_field_config">
<%
    
    if(bosflag!=null&&!bosflag.equals("hcm")){
%>
<br>   
<%
}
%>
<table width="<%=_width%>" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable setFieldTable">  
<tr><td align="center" width="45%" valign="top">
<fieldset>
<legend>用工需求表指标</legend>
<table width="100%">
<tr>
<td>
<td align="center" valign="top">
<hrms:optioncollection name="positionDemandForm" property="demandFieldList" collection="list" />
						 <html:select name="positionDemandForm" property="demandFieldId" onchange="changeSet('1');" size="10" style="width:200px;">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>
</td>
</tr>
</table>
</fieldset>
</td>
<td width="10%" align="center" valign="middle"><input type="button" name="dy" value="对应" class="mybutton" onclick="save('2','');"/></td>
<td align="center">		
<fieldset>
<legend>岗位子集指标</legend>
<table>
<tr>
<td align="center">        
子集&nbsp;<hrms:optioncollection name="positionDemandForm" property="postFieldSetList" collection="list" />
						 <html:select name="positionDemandForm" property="postFieldSetId" size="1" onchange="changeSet('1');" style="width:200px;">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>
</td>	
</tr>	
<tr>
<td align="center" valign="top">		        
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<hrms:optioncollection name="positionDemandForm" property="postFieldItemList" collection="list" />
						 <html:select name="positionDemandForm" property="postFieldItemId" size="8"  style="width:201px;">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>
</td>
</tr>
</table>
</fieldset>
</td>
</tr>

<tr>
<td align="center" colspan="3" valign="top" style="padding-bottom:2px;">
<div id="tabStr" style="overflow:auto;width:100%;height:130px;valign:top; margin-top:5px;" class="div_modify common_border_color">
${positionDemandForm.tableStr}
</div>
</td></tr>

<tr><td align="center" colspan='3' style="padding-top:3px;">
<div style="overflow:hidden;width:100%;height:30px;valign:top;">
<table><tr><td align="center">
<input type="button" class="mybutton" value="<bean:message key="button.ok"/>" onclick="saveParam();"/>
 
<input type="button" class="mybutton" value="<bean:message key="button.close"/>" onclick="window.close();"/>
</td>
</tr>
</table>
</div>
<html:hidden property="param" name="positionDemandForm"/>
</td>
</tr>
</table>
</html:form>
