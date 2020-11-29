<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link href="/css/css1.css" rel="stylesheet" type="text/css"/>
<hrms:themes></hrms:themes>
	<title>修改指标代码</title>
	<script language="JavaScript">
	function closedialog()
	{
		var re = /^([a-z]|[A-Z])\w{0,29}$/i; //+号表示字符至少要出现1次,\s表示空白字符,\d表示一个数字  
		if(!re.test(document.hrSyncForm.editname.value)){
			alert("指标代码必须以字母开头，由字母和数字组成！")
			return;
		} 
		if(parent.Ext && parent.Ext.getCmp('edit_field')){
			var win = parent.Ext.getCmp('edit_field');
			win.orgname = document.hrSyncForm.editname.value;
		}else{
		    returnValue=document.hrSyncForm.editname.value;
		}
	  // window.close();	   
	  	winclose();
	}
	document.onkeydown=function()                //网页内按下回车触发
	{
        if(event.keyCode==13)
        {
            document.getElementById("b_ok").click();   
            return false;                               
        }
	}
    function IsDigit()
    {
    	return (((event.keyCode > 47) && (event.keyCode <= 57))|| ((event.keyCode >= 65)&& (event.keyCode <= 90))|| ((event.keyCode >= 97)&& (event.keyCode <= 122))|| (event.keyCode == 95));
    }
	
	function winclose(){
		if(parent.Ext && parent.Ext.getCmp('edit_field')){
			parent.Ext.getCmp('edit_field').close();
			return;
		}
		window.close();
	}
	</script>
<html:form action="/sys/export/SearchHrSyncFiled">
<table width="100%" cellpadding="0" cellspacing="0" align="center">
	<tr><td>
     <table width="240px;" class="RecordRow" cellpadding="0" cellspacing="0" align="center" style="margin-top:5px;">
            <tr>
                 <td align="left" nowrap valign="middle" height="150px;" width="240px;">
            	    <bean:message key="sys.export.sync.custom.field"/><input type="text" name="editname" size="20" onkeypress="event.returnValue=IsDigit();" class="text4" style="width:140px;margin-left:5px;">
                 </td>
              </tr>  
     </table>
     </td></tr>
     <tr><td>       
     <table  width="100%" align="center">
          <tr>
            <td align="center" height="35px;">
         	  <input id="b_ok"  type="button" name="b_ok" value="<bean:message key="button.ok"/>" class="mybutton" onclick="closedialog()">
	          <input type="button" name="br_return" value="<bean:message key="button.cancel"/>" class="mybutton" onclick="winclose();">
            </td>
          </tr>          
    </table>
</td></tr></table>
</html:form >
