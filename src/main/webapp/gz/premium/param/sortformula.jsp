<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<link rel="stylesheet" href="/css/css1.css" type="text/css">
<hrms:themes />
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="javascript">
function saveSort(){ 
	var hashvo=new ParameterSet();
	var setid = document.getElementById("setid").value;
	hashvo.setValue("setid",setid);
	hashvo.setValue("sorting",selectTostr('sort_fields'));
	
	var request=new Request({method:'post',asynchronous:false,
     	onSuccess:showFieldList,functionId:'3020131018'},hashvo);
}
function showFieldList(outparamters){
		var base=outparamters.getValue("info");
		if(base=='ok'){
			returnViewtemp();
		}else{
			alert("<bean:message key='gz.formula.alert.tempsort.failure'/>");
		}
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
function returnViewtemp(){
	var setid = document.getElementById("setid").value;
	var fmode = "${premiumParamForm.fmode}";
	var theurl="/gz/premium/param/formula.do?b_count=link&fmode="+fmode; 
	if(fmode=="0")
	theurl="/gz/premium/param/formula.do?b_count=link&fmode="+fmode; 
	if(fmode=="1")
	theurl="/gz/premium/param/formula.do?b_import=link&fmode="+fmode; 
	if(fmode=="2")
	theurl="/gz/premium/param/formula.do?b_stat=link&fmode="+fmode; 
	
	document.location.href=theurl; 
}		   	  		 		
</script>
<html:form action="/gz/premium/param/sortformula">
<center>
<table width="40%" height="300" border="0" align="center">
  <tr> 
    <td height="50" >
		<html:hidden name="premiumParamForm" property="setid"/>
	</td>
  </tr>
  <tr> 
    <td>
    	<fieldset style="width:80%;height:250">
    	<legend><bean:message key='gz.formula.alert.cond.sort'/></legend>
    	<table width="100%" border="0" align="center">
    	<tr>
    		<td width="80%">
    			<html:select name="premiumParamForm" property="sort_fields" multiple="multiple" ondblclick="removeitem('sort_fields');" style="height:240px;width:100%;font-size:10pt">
                    <html:optionsCollection property="sortlist" value="dataValue" label="dataName"/>   		      
 		     	</html:select>
    		</td>
    		<td>
    			<table width="100%"  border="0" align="center" height="240">
    				<tr height="100">
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
    					<td align="center"><input type="button" value="<bean:message key='button.return'/>" onclick="returnViewtemp();" Class="mybutton"></td>
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
