<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<style>
<!--
.TableRow_2lock_blt {
	background-position : center left;
	font-size: 12px;  
	BORDER-BOTTOM: 0pt; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: 0pt; 
	BORDER-TOP: #C4D8EE 1pt solid;
	height:22px;
	font-weight: bold;
	background-color:#f4f7f7;	
	valign:middle;
	position: relative;
	top: expression(document.getElementById("tb_cantainer").scrollTop); /*IE5+ only*/
}
.TableRow_2lock_blt2 {
	background-position : center left;
	font-size: 12px;  
	BORDER-BOTTOM: 0pt; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT:#C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	height:22px;
	font-weight: bold;
	background-color:#f4f7f7;	
	valign:middle;
	position: relative;
	top: expression(document.getElementById("tb_cantainer").scrollTop); /*IE5+ only*/
}
-->
</style>
<script language="JavaScript" src="../../../module/utils/js/template.js"></script>
<script type="text/javascript">
<!--
function selectAll(obj)
{
   var xx=document.getElementsByName("ids");
   for(var j=0;j<xx.length;j++)
   {
      if(obj.checked)
         xx[j].checked=true;
      else
        xx[j].checked=false;
   }
}
function sub()
{
   var ids="";
   var num=0;
   var xx=document.getElementsByName("ids");
   for(var j=0;j<xx.length;j++)
   {
     if(xx[j].checked)
     {
        ids+="/"+xx[j].value;
        num++;
     }
   }
   if(num<=0)
   {
     Ext.showAlert("请选择要退回的人员！");
     return;
   }
   var obj = new Object();
   var unid=document.getElementById("unid");
   if(trim(unid.value)=='')
   { 
     Ext.showAlert("请填写退回原因!");
     return;
   }
   obj.ids=ids;
   obj.reason=getEncodeStr(unid.value);
   if (window.showModalDialog) {
       parent.window.returnValue = obj;
   }else{
	   if(parent.parent.myuntread_ok){
		   parent.parent.myuntread_ok(obj);
	   }else{
	       window.opener.myuntread_ok(obj);
	   }
   }
   closeWin();
}
function closeWin(){
	if(parent.parent.Ext&&parent.parent.Ext.getCmp('reject_win')){
		parent.parent.Ext.getCmp('reject_win').close();
	}else if(parent.close){
		parent.close();
	}else{
		window.close();
	}
}
//-->
</script>
<table width="90%" align="center" border="0" cellpadding="0" cellspacing="0">
<tr>
<td width="330px">
<div id="tb_cantainer" style="overflow:auto;width:330px;height:100px;border:1px">
<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0" class="ListTable">
<script type="text/javascript">
var paraArray;
if(parent.window.dialogArguments){
	paraArray = parent.window.dialogArguments;
}else if(parent.parent.dialogArguments){
	paraArray = parent.parent.dialogArguments;
}else if(parent.opener.dialogArguments){
	paraArray = parent.opener.dialogArguments;
}
for(var i=0;i<paraArray.length;i++)
{
    if(i==0)
    {
        document.write("<thead><tr><td align='center' class='RecordRow' style='background-color:#F4F7F7;'><input type='checkbox' name='al' onclick='selectAll(this);'/>");
        document.write("</TD> <td align='center' class='RecordRow' style='background-color:#F4F7F7;'>姓名</td></tr></thead>");
    }
    var arr=paraArray[i].split("`");
    document.writeln("<tr>");
    document.writeln("<td align='center' class='RecordRow'><input type='checkbox' name='ids' value='"+arr[0]+"'/></td>");
    document.writeln("<td align='center' class='RecordRow'>"+arr[1]+"</td></tr>");
}
</script>
</table>
</div>
</td>
</tr>
<tr><td width="330px">退回原因：
</td></tr>
<tr><td width="330px"><textarea name="unstread"  id="unid" style="height:90px;width:330px;" cols="45"></textarea></td></tr>
<tr><td height="35" valign="middle" align='center'>
<input type='button' class='mybutton' value='确定' onclick='sub();'/>
<input type='button' class='mybutton' value='关闭' onclick='closeWin();'/>
</td></tr>
</table>