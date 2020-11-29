<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"/>
<hrms:themes></hrms:themes>
	<title></title>
	<script language="JavaScript">
	function closedialog()
	{
	
		if(parent.Ext){//ext 弹窗 返回数据  wangb 20190319
			var win = parent.Ext.getCmp('select_field');
			var value = document.sysinfosortForm.reworkname.value;
			value=replaceAll(value, "'", "‘");
	    	value=replaceAll(value, '\"', '”');
			win.reworkname = value;
			win.close();
		}else{
		    returnValue=document.sysinfosortForm.reworkname.value;
		    returnValue=replaceAll(returnValue, "'", "‘");
	    	returnValue=replaceAll(returnValue, '\"', '”');
	   		window.close();	   
		}
	
	}
	document.onkeydown = function(e) { //网页内按下回车触发
    	var e = e || event;
    	if(e.keyCode == 13) {
        	document.getElementById("b_ok").click();   
            return false; 
    	}
	}
	function estop()
	{
		return (((event.keyCode > 47) && (event.keyCode <= 57))|| ((event.keyCode >= 65)&& (event.keyCode <= 90))|| ((event.keyCode >= 97)&& (event.keyCode <= 122))|| (event.keyCode == 95));
	}	
	function replaceAll(str, sptr, sptr1)
   {
	while (str.indexOf(sptr) >= 0)
	{
   		str = str.replace(sptr, sptr1);
	}
	return str;
   } 
   
   function onkeypress1(){
   		if(parent.Ext){
			parent.Ext.getCmp('select_field').return_vo=estop();   		
   		}else{
	   		event.returnValue=estop();
   		}
   }
   
   function winclose(){
   		if(parent.Ext){
			parent.Ext.getCmp('select_field').close();
			return;   			
   		}
   		window.close();
   }
	</script>
<html:form action="/system/param/sysinfosort">
        <table width="290" border="0" cellpadding="0" cellspacing="0" align="center" style="margin-top: 10px">
            <tr >
                 <td align="center"  nowrap valign="center" class="RecordRow" style="height: 100px">
            	    修改后的名称&nbsp;<input style="width:180px;" type="text" name="reworkname" size="20" onkeypress="onkeypress1()" class="text4">
                 </td>
              </tr>  
          </table>       
     <table  width="100%" align="center">
          <tr>
            <td align="center" height="35px;">
         	  <input id="b_ok"  type="button" name="b_ok" value="<bean:message key="button.ok"/>" class="mybutton" onclick="closedialog()">
	          <input type="button" name="br_return" value="<bean:message key="button.cancel"/>" class="mybutton" onclick="winclose();">
            </td>
          </tr>          
    </table>
</html:form >