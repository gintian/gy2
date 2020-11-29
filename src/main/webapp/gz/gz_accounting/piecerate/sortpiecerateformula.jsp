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
	var busiid = document.getElementById("busiid").value;
	hashvo.setValue("busiid",busiid);
	hashvo.setValue("sorting",selectTostr('sort_fields'));
	hashvo.setValue("model","savesort");	
	
	var request=new Request({method:'post',asynchronous:false,
     	onSuccess:showFieldList,functionId:'3020091050'},hashvo);
}
function showFieldList(outparamters){
		var base=outparamters.getValue("strResult");
		if(base=='ok'){
			window.returnValue="111";
			window.close();
		}else{
			alert('调整顺序失败');
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
</script>
<hrms:themes />
<html:form action="/gz/gz_accounting/piecerate/search_piecerate_formula">
<center>
<table width="100%" height="300" border="0" align="center">
  <tr> 
    <td height="50" >
		<html:hidden name="pieceRateFormulaForm" property="busiid"/>
	</td>
  </tr>
  <tr> 
    <td align="center">
    	<fieldset style="width:80%;height:250">
    	<legend><bean:message key='gz.formula.alert.cond.sort'/></legend>
    	<table width="100%" border="0" align="center">
    	<tr>
    		<td width="80%">
    			<html:select name="pieceRateFormulaForm" property="sort_fields" multiple="multiple" ondblclick="removeitem('sort_fields');" style="height:240px;width:100%;font-size:10pt">
                    <html:optionsCollection property="sortlist" value="dataValue" label="dataName"/>   		      
 		     	</html:select>
    		</td>
    		<td valign="top">
						<html:button  styleClass="mybutton" property="b_up" onclick="upItem($('sort_fields'));">
            		    	<bean:message key="button.previous"/> 
	           			</html:button >
						<html:button  styleClass="mybutton" property="b_down" onclick="downItem($('sort_fields'));">
            		    	<bean:message key="button.next"/>    
	           			</html:button >	 
    					<input type="button" value="<bean:message key='button.ok'/>" onclick="saveSort();" Class="mybutton">
    					<input type="button" value="<bean:message key='button.close'/>" onclick="window.close();" Class="mybutton">
    		</td>
    	</tr>
    	</table>
    	 </fieldset>
    </td>
  </tr>
</table>
</center>
</html:form>
