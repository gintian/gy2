<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %> 
<head>
<script language="javascript">
function canable(obj){
	if(obj.checked){
		document.getElementById("22").checked=true;
	}else{
		document.getElementById("11").checked=false;
		document.getElementById("22").checked=false;
		document.getElementById("33").checked=false;
		document.getElementById("44").checked=false;
	}
	document.getElementById("11").disabled=!obj.checked;
	document.getElementById("22").disabled=!obj.checked;
	document.getElementById("33").disabled=!obj.checked;
	document.getElementById("44").disabled=!obj.checked;
}
function ablethis(){
	if(document.getElementById("bb").checked){
		var sumtype;
		var objs=document.getElementsByName("aa");
		for(var i=0;i<objs.length;i++){
			if(objs[i].checked){
				sumtype=objs[i].value;
				break;
			}
		}
		window.returnValue=sumtype;
	}else{
		window.returnValue="no";
	}
	window.close();
}
function init(){
	document.getElementById("11").disabled=true;
	document.getElementById("22").disabled=true;
	document.getElementById("33").disabled=true;
	document.getElementById("44").disabled=true;
} 
</script>
<title><%=request.getParameter("msg") %></title>
</head>
<hrms:themes />
<html:form action="/workbench/browse/history/parameters_deploy">
<table width="100%" border="0" style="margin-top: 2px;" cellspacing="0" align="center" cellpadding="0">
        <tr>
          <td>
          <fieldset align="center" style="width:96%;">
         <legend ><input id=bb type=checkbox <logic:notEmpty name="personHistoryForm" property="sumtype">checked="checked"</logic:notEmpty> onclick="canable(this)" ><label for="bb" >汇总</label></legend>
          <table align="center" border="0" width="100%"> 
	         <tr>
	            <td align="left" valign="top" nowrap>
	     	       &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="radio" <logic:equal value="AVG" name="personHistoryForm" property="sumtype">checked="checked"</logic:equal> id=11 name="aa" value="AVG"><label for="11" id=55 >平均值</label>
		    	</td>  
		    	<td align="left" nowrap>
	     	        <input type="radio" <logic:equal value="SUM" name="personHistoryForm" property="sumtype">checked="checked"</logic:equal> id=22 name="aa" value="SUM"><label for="22" id=66 >求和</label>
	            </td>                	        	        
	        </tr> 
	        <tr style="height: 5px;"><td>&nbsp;</td></tr>
			<tr>
	            <td align="left" valign="top" nowrap>
	     	       &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="radio" <logic:equal value="MAX" name="personHistoryForm" property="sumtype">checked="checked"</logic:equal> id=33 name="aa" value="MAX"><label for="33" id=77 >最高值</label>
		    	</td>  
		    	<td align="left" nowrap>
	     	        <input type="radio" <logic:equal value="MIN" name="personHistoryForm" property="sumtype">checked="checked"</logic:equal> id=44 name="aa" value="MIN"><label for="44" id=88 >最低值</label>
	            </td>                	        	        
	        </tr>
	        <tr style="height: 5px;"><td>&nbsp;</td></tr>
        </table>
    </fieldset>  
		  </td>
		</tr>
</table>
<table  width="100%">
          <tr style="height: 35px;">
            <td align="center">       
               <html:button styleClass="mybutton" property="br_return" onclick="ablethis();">
					<bean:message key="button.ok"/>
			   </html:button>
			   
			   <html:button styleClass="mybutton" property="br_return" onclick="window.close();">
					<bean:message key="button.cancel"/>
			   </html:button>         	   
            </td>
          </tr>          
</table>
</html:form>
<logic:empty name="personHistoryForm" property="sumtype">
<script language="javascript">
	init();
</script>
</logic:empty>
