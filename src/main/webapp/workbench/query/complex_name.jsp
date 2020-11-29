<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="javascript">
var userAgent = window.navigator.userAgent; //取得浏览器的userAgent字符串  
var isOpera = userAgent.indexOf("Opera") > -1;
var isIE = (userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1 && !isOpera); 
 function addName()
 {
    var complex_name=document.complexInterfaceForm.name.value;
    if(complex_name=="")
    {
           alert("名称不能为空！");
           return false;   
    }
    if(confirm("确认保存吗？"))
    {
         var thevo=new Object();
	     thevo.complex_name=complex_name;
	     thevo.flag="ok";
	     if(isIE){
			 window.returnValue=thevo;
			 window.close();  
	     }else{
	    	 if(parent.opener&&parent.opener.saveOpenReturn){
	    		 top.window.close();
	    		 parent.opener.saveOpenReturn(thevo); 
	    	 }
	     }
    }
    
 }
 function closeFunc(){
	 if(isIE){
		 window.close();
	 }else{
		 top.window.close();
	 }
 }
</script>
<hrms:themes />
<html:form action="/workbench/query/complex_interface">
	<table width="80%" cellpmoding="0" cellspacing="0" class="DetailTable"
		cellpadding="0" align="center" valign="middle">
		<!--<tr>
           <td width=1 height="20" valign="top" class="tableft1"></td>
       		               <td width=130 align=center class="tabcenter">保存条件</td>   
       		               <td width=10 valign="top" class="tabright"></td>
       		               <td valign="top" class="tabremain" width="300"></td>       		           	      
     </tr>   -->
		<tr>
			<td width=1 height="20" />
		</tr>
		<tr>
			<td width="100%" valign="middle" class="framestyle9"
				style="border-top:#C4D8EE 1px solid;">
				<br>
				<table width="80%" cellpmoding="0" cellspacing="0"
					class="DetailTable" cellpadding="0" align="center" valign="middle">
					<tr>
						<td>名称</td>
						<td><input type="text" name="name" value="" style="width: 220px; font-size: 10pt; text-align: left" class="inputtext">
						</td>
					</tr>
				</table>
				<br>
			</td>
		</tr>
		<tr>
			<td align="center" style="height: 35px;">
				<input type="button" name="Submit" value="保存" class="mybutton"
					onClick="addName();">
				<input type="button" name="Submit" value="关闭" class="mybutton"
					onClick="closeFunc();">
			</td>
		</tr>
	</table>
</html:form>
