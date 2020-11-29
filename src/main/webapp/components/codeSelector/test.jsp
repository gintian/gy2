<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<meta http-equiv="X-UA-Compatible" content="IE=EDGE">
<!--  <script type="text/javascript" src="/ext/ext6/ext-all.js"></script>
<script type="text/javascript" src="/ext/ext6/locale-zh_CN.js" ></script>-->
<script language="JavaScript" src="/module/utils/js/template.js"></script>
<script type="text/javascript" src="/components/codeSelector/codeSelector.js"></script>
<script type="text/javascript" src="/components/extWidget/field/CodeTreeCombox.js"></script>
<script type="text/javascript" src="/components/codeSelector/deepCodeSelector.js"></script>
<script type="text/javascript" src="/components/dateTimeSelector/dateTimeSelector.js"></script>
<link rel="stylesheet" href="/ext/ext6/resources/ext-theme.css" type="text/css" />
<style>
</style>
<table border=0 cellpadding="0" cellspacing="0" style="margin:10 0 0 100px"><tr>
<td >树形代码：
<input type="text" name="test_value" >
<input type="text" name="test_view" title="常用模式"  ctrltype="3" nmodule="8" codesetid="01" inputname="test_view" id='12345'>
<!--  <img src="/images/add.gif"  plugin="codeselector" title="自定义生成数据模式"  codesource="GetCodeDataExample" codesetid="UM" inputname="test_view"/> 
<img src="/images/add.gif"  plugin="codeselector" title="常用模式"  ctrltype="3" nmodule="4" codesetid="AV" inputname="test_view"/>-->

</td></table>
<br/><br/>
<table border=3>
 <tr>
   <td id = "deepCon">三级代码测试：
   <input name="threeLevelCode_value" ><br>
      <input name="threeLevelCode_view" ><img src="/images/add.gif" id="abc" plugin="deepcodeselector"  title="三级代码选择"  multiple='true' codesetid="TO" afterfunc="afterfunc" inputname="threeLevelCode_view"/>
   </td>
 </tr>
 <tr><td style="padding-left:500px">
<input name="cccc" >
 <img src="/images/add.gif"  id="abcde" inputname="cccc" format="Y-m-d H:i:s"/> 
 </td></tr>
</table>

<script>
Ext.onReady(function(){
	
	Ext.create('EHR.extWidget.field.CodeTreeCombox',{
		codesetid:'${param.codesetid}',onlySelectCodeset:false,multiple:true,
		renderTo:document.body
		
	});
});
   //Ext.getDom('deepCon').innerHTML="<input name='aaa_value'><input name='aaa_view'><img src='/images/add.gif' id='deep' codesetid='TO' inputname='aaa_view'>"
  //setDeepEleConnect('deep');
   setEleConnect(['12345']);
  setDateEleConnect(['abcde']);
  function afterfunc(){
	  
	  console.log(arguments);
  }
</script>

