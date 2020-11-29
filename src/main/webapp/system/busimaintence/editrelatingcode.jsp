<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script type="text/javascript">
<!--
	function InitData()
	{
	   var hashvo=new ParameterSet();
	   hashvo.setValue("flag",${relatingcodeForm.flag});
	   hashvo.setValue("codesetid",'${relatingcodeForm.codesetid}');
   	   var request=new Request({method:'post',asynchronous:false,onSuccess:showSetList,functionId:'1010060014'},hashvo);
	}
	function showSetList(outparamters)
	{
		
		var setlist=outparamters.getValue("systemlist");
		AjaxBind.bind(relatingcodeForm.sys,/*$('setlist')*/setlist);
		
		if($('sys').options.length>0)
		{
		  $('sys').options[0].selected=true;
		  $('sys').fireEvent("onchange");
		}
		for(i=0;i<$('sys').options.length;i++)
		{
			//alert($('sys').options[i].value);
		}	
		
	}
	function getziji(){
		var pars="sysvalue="+relatingcodeForm.sys.value;
		var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:showzijiList,functionId:'1010060014'});
	}
	function showzijiList(outparamters){
		var setlist=outparamters.getValue("zijilist");
		var codetable = outparamters.getValue("codetable");
		AjaxBind.bind(relatingcodeForm.ziji,/*$('setlist')*/setlist);	
		if($('ziji').options.length>0)
		{
		  if(${relatingcodeForm.flag}==1)
		  {
			  for(i=0;i<$('ziji').options.length;i++)
			  {
				  if($('ziji').options[i].value==codetable)
				  {
				  	 $('ziji').options[i].selected=true;
				  }
			  }
		  }else
		  {
		  	$('ziji').options[0].selected=true;
		  }
		  $('ziji').fireEvent("onchange");
		}
	}
function getitem(){
	var pars="zijivalue="+relatingcodeForm.ziji.value;
	var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:showitemList,functionId:'1010060014'});
}
function showitemList(outparamters){
	var setlist=outparamters.getValue("itemlist");
	AjaxBind.bind(relatingcodeForm.codevalue,/*$('setlist')*/setlist);	
	AjaxBind.bind(relatingcodeForm.codedesc,/*$('setlist')*/setlist);	
	AjaxBind.bind(relatingcodeForm.upcodevalue,/*$('setlist')*/setlist);
	var codevalue = outparamters.getValue("codevalue");
	var codedesc = outparamters.getValue("codedesc");
	var upcodevalue = outparamters.getValue("upcodevalue");	
	if($('codevalue').options.length>0)
		{
		  if(${relatingcodeForm.flag}==1)
		  {
			  for(i=0;i<$('codevalue').options.length;i++)
			  {
				  if($('codevalue').options[i].value==codevalue)
				  {
				  	 $('codevalue').options[i].selected=true;
				  }
			  }
		  }else
		  {
		  	$('codevalue').options[0].selected=true;
		  }
	}
	if($('codedesc').options.length>0)
		{
		  if(${relatingcodeForm.flag}==1)
		  {
			  for(i=0;i<$('codedesc').options.length;i++)
			  {
				  if($('codedesc').options[i].value==codedesc)
				  {
				  	 $('codedesc').options[i].selected=true;
				  }
			  }
		  }else
		  {
		  	$('codedesc').options[0].selected=true;
		  }
	}
	if($('upcodevalue').options.length>0)
		{
		  if(${relatingcodeForm.flag}==1)
		  {
			  for(i=0;i<$('upcodevalue').options.length;i++)
			  {
				  if($('upcodevalue').options[i].value==upcodevalue)
				  {
				  	 $('upcodevalue').options[i].selected=true;
				  }
			  }
		  }else
		  {
		  	$('upcodevalue').options[0].selected=true;
		  }
	}
}
function addrc(){
	var codesetid=relatingcodeForm.codesetids.value;
	
	relatingcodeForm.action="/system/busimaintence/editrelatingcode.do?b_edit=link&add=add";
	relatingcodeForm.submit();
}
function updaterc(){
	var codesetid=relatingcodeForm.codesetids.value;
	
	relatingcodeForm.action="/system/busimaintence/editrelatingcode.do?b_edit=link&update=add";
	relatingcodeForm.submit();
}
function backrc(){
	var codesetid=relatingcodeForm.codesetids.value;
	
	relatingcodeForm.action="/system/busimaintence/editrelatingcode.do?b_edit=link";
	relatingcodeForm.submit();
}
//-->
</script>
<hrms:themes/>
<html:form action="/system/busimaintence/editrelatingcode">

<table  width="100%" cellpadding="0" cellspacing="0" align="left">
<tr>
<td>
<br><br>
	<table  width="80%" align="center" border="0" cellspacing="0"  cellpadding="0">
	
		<TR>
				<td class="RecordRow" align="right" style="border-right: none;">
				编码（2位）
				</td>
				<td class="RecordRow">
				<logic:equal value="1" name="relatingcodeForm" property="flag">
				<html:text styleId="codesetids" name="relatingcodeForm" styleClass="text4" property="relatingcodeVo.string(codesetid)" maxlength="2" disabled="true"></html:text>
				</logic:equal>
				<logic:equal value="0" name="relatingcodeForm" property="flag">
				<html:text styleId="codesetids" name="relatingcodeForm" styleClass="text4" property="relatingcodeVo.string(codesetid)" maxlength="2"></html:text>
				</logic:equal>
				</td>
				
			</TR>
			<TR>
				<td class="RecordRow" align="right" style="border-top: none;border-right: none;">
				所属模块
				</td>
				<td class="RecordRow" style="border-top: none;">
				<select name="sys" style="width:50%" onchange="getziji();">
				<option value="">#</option>
				</select>
				</td>
				
			</TR>
			<TR>
				<td class="RecordRow" align="right" style="border-top: none;border-right: none;">
				代码表
				</td>
				<td class="RecordRow" style="border-top: none;">
				
				<html:select styleId="ziji" name="relatingcodeForm" property="relatingcodeVo.string(codetable)" style="width:50%" onchange="getitem();">
				
				</html:select>
				</td>
				
			</TR>
			<TR>
				<td class="RecordRow" align="right" style="border-top: none;border-right: none;">
				代码值指标
				</td>
				<td class="RecordRow" style="border-top: none;">
				<html:select styleId="codevalue" name="relatingcodeForm" property="relatingcodeVo.string(codevalue)" style="width:50%">
				
				</html:select>
				</td>
				
			</TR>
			<TR>
				<td class="RecordRow" align="right" style="border-top: none;border-right: none;">
				代码名称指标
				</td>
				<td class="RecordRow" style="border-top: none;">
				<html:select styleId="codedesc" name="relatingcodeForm" property="relatingcodeVo.string(codedesc)" style="width:50%">
				
				</html:select>
				</td>
				
			</TR>
			<TR>
				<td class="RecordRow" align="right" style="border-top: none;border-right: none;">
				上层代码指标
				</td>
				<td class="RecordRow" style="border-top: none;">
				<html:select styleId="upcodevalue" name="relatingcodeForm" property="relatingcodeVo.string(upcodevalue)" style="width:50%">
				
				</html:select>
				</td>
				
			</TR>
			<TR>
				<td class="RecordRow" align="right" style="border-top: none;border-right: none;">
				显示方式
				</td>
				<td class="RecordRow" style="border-top: none;">
				<html:select name="relatingcodeForm" property="relatingcodeVo.string(status)" style="width:50%"> 
				<html:option value="1">列表</html:option>
				<html:option value="0">树状</html:option>
				</html:select>
				</td>
				
			</TR>
		</table>
		<table cellpadding="0" cellspacing="0" border="0" align="center">
		<TR>
				<td colspan="2" align="center" style="border: none;padding-top: 5px;">
				<logic:equal value="1" name="relatingcodeForm" property="flag">
				<button name="add" class="mybutton" onclick="updaterc();">修改</button>
				</logic:equal>
				<logic:equal value="0" name="relatingcodeForm" property="flag">
				<button name="add" class="mybutton" onclick="addrc();">增加</button>
				</logic:equal>
				<button name="back" class="mybutton" onclick="backrc();">返回</button>
				</td>
				
			</TR>
		</table>
	</td>
	</tr>
	</table>
		

</html:form>
<script language="javascript">
InitData();
</script>




