<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<hrms:themes></hrms:themes>
<style type="text/css">
.btn3 {
 BORDER-RIGHT: #C0C0C0 1px solid;
 BORDER-TOP: #C0C0C0 1px solid; 
 PADDING-LEFT: 0px; FONT-SIZE: 12px; 
 BORDER-LEFT: #C0C0C0 1px solid; 
 COLOR: #808080; 
 PADDING-TOP: 0px; 
 PADDING-BOTTOM: 0px;
 BORDER-BOTTOM: #C0C0C0 1px solid
}
#scroll_box {
    border: 1px solid #eee;
    height: 220px;    
    width: 240px;           
    overflow: auto;            
    margin: 1em 0;
}
</style>
<html:form action="/org/autostatic/confset/included">
<table width="100%" border="0" align="center">
	<tr>
    <td>
    <fieldset align="center" style="width:100%;">
     <legend><bean:message key='org.autostatic.mainp.item.load.data'/></legend>
     	<table width="100%" height="250" border="0" align="center">
     		<tr>
     			<td valign="top">
     			<div id="scroll_box" style="margin-left:8px;">
    			${subsetConfsetForm.included_table}
    			</div>
    			</td>
    		</tr>
    	</table>
    </fieldset>
    </td>	
    <td valign="top"><table   border="0" cellpadding="0" cellspacing="0">
         <tr>
          <td  align="center" style="padding-top:9px;"><input type="button" name="button_all" value=" <bean:message key='label.query.selectall'/> " onclick="checkall();" Class="mybutton"></td>
        </tr>
        <tr>
          <td  align="center" style="padding-top:10px;"><input type="button" name="button_no" value=" <bean:message key='button.all.reset'/> " onclick="checkclear();" Class="mybutton"></td>
        </tr>
        <tr> 
          <td  align="center" style="padding-top:10px;">
          	<input type="button" name="button_ok" value=" <bean:message key='reporttypelist.confirm'/> " onclick="check_ok();" Class="mybutton">
          </td>
        </tr>
        <tr>
          <td  align="center" style="padding-top:10px;">
             <input type="button" name="button_no" value=" <bean:message key='kq.register.kqduration.cancel'/> " onclick="window.close();" Class="mybutton">
          </td>
        </tr>
       
      </table></td>
	</tr>
	<tr>
		<td colspan="2">
			说明：将上期数据载入到本期选中的指标中,然后
			<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			根据载入的数据进行计算。
		</td>
	</tr>
</table>
<script language="JavaScript">
function checkall(){
	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++){
	     if(tablevos[i].type=="checkbox"){
	     	tablevos[i].checked=true;
		 }
     }
}
function checkclear(){
	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++){
	     if(tablevos[i].type=="checkbox"){
	     	tablevos[i].checked=false;
		 }
     }
}
function check_ok(){
	var thecontent="";
	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++){
	     if(tablevos[i].type=="checkbox"){
	     	if(tablevos[i].checked){
	      		thecontent +=tablevos[i].value+",";
	      	}
		 }
     }
     window.returnValue = thecontent;
     window.close();
}
function checkefirst(){
	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++){
	     if(tablevos[i].type=="checkbox"){
	     	tablevos[i].checked=true;
	     	break;
		 }
     }
}
</script>
</html:form>


