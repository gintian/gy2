
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<html>

	<script language="javascript">
	 function saveSort(){ 
	var sorting = selectTostr('sort_fields');
	var hashvo=new ParameterSet();
			hashvo.setValue("sorting",sorting);			
			var In_paramters="flag=1"; 		
			var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'03020000049'},hashvo);
		
}
function selectTostr(listbox){
  var vos,right_vo,i,str='';
  vos= document.getElementsByName(listbox);
  if(vos==null || vos[0].length==0){
  	return;  	
 	vos[0].options[0].selected=false;

  }
  //设为要可选状态
  right_vo=vos[0];  
  for(i=0;i<right_vo.options.length;i++){
	str += right_vo.options[i].value+",";
  }
  return str;  	
}
   function returnInfo(outparamters){
 	var info=outparamters.getValue("info");
	  	if(info=="ok")
	  	{
	  	window.returnValue="ok";
   		window.close();
	  	}else{
	  	alert(SAVEFAILED+"!");
	  	}
	  	}
   </script>
<body>
<html:form action="/report/edit_report/editReport/staticStatement">
		<center>
<table width="100%" height="300" border="0" align="center">
  <tr> 
    <td height="50">
    	<html:hidden name="staticStatementForm" property="scopeid"/>
	</td>
  </tr>
  <tr> 
    <td align="center">
    	<fieldset style="width:80%;height:250">
    	<legend><bean:message key='report.static.sortitem'/></legend>
    	<table width="100%" border="0" align="center">
    	<tr>
    		<td width="80%">
    			<html:select name="staticStatementForm" property="sort_fields" multiple="multiple" style="height:240px;width:100%;font-size:10pt">
                    <html:optionsCollection property="sortList" value="dataValue" label="dataName"/>   		      
 		     	</html:select>
    		</td>
    		<td>
    			<table width="100%"  border="0" align="center"  valign="top" height="240">
    				<tr height="20">
    					<td>&nbsp;</td>
    				</tr>
    				<tr>
    					<td>&nbsp;</td>
    				</tr>
    				<tr>
    					<td align="center">
							<html:button  styleClass="mybutton" property="b_up" onclick="upItem($('sort_fields'));">
            		     		<bean:message key="button.previous"/> 
	           				</html:button >
						</td>
    				</tr>
    				<tr>
    					<td align="center">
							<html:button  styleClass="mybutton" property="b_down" onclick="downItem($('sort_fields'));">
            		     		<bean:message key="button.next"/>    
	           				</html:button >	 
						</td>
    				</tr>
    				<tr>
    					<td align="center"><input type="button" value="<bean:message key='button.ok'/>" onclick="saveSort();" Class="mybutton"></td>
    				</tr>
    				<tr>
    					<td align="center"><input type="button" value="<bean:message key='button.cancel'/>" onclick="window.close();" Class="mybutton"></td>
    				</tr>
    			</table>
    		</td>
    	</tr>
    	</table>
    	 </fieldset>
    </td>
  </tr>
</table>
</center>
</html:form>

	</body>
</html>
