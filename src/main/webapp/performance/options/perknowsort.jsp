<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>


<hrms:themes />
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="javascript">
function saveSort(){ 
	var hashvo=new ParameterSet();
	hashvo.setValue("sorting",selectTostr('sort_fields'));
	
	var request=new Request({method:'post',asynchronous:false,
     	onSuccess:showFieldList,functionId:'9026002007'},hashvo);
}
function showFieldList(outparamters){
		var base=outparamters.getValue("info");
		if(base=='ok'){
			if(window.showModalDialog){
                parent.window.returnValue="ok";
			}else {
			    if(parent.parent.perknow_ok)
		 		    parent.parent.perknow_ok("ok");
			    else if(parent.opener.toStirng_sort_ok)
                    parent.opener.toStirng_sort_ok("ok");
			}
			sort_close();
		}else{
			alert("<bean:message key='gz.tempvar.alert.temp.sort.failure'/>");
		}
}

function sort_close() {
	if(!window.showModalDialog && parent.parent.Ext){
		var win = parent.parent.Ext.getCmp('toSorting_win');
   		if(win) {
    		win.close();
   		}
	}
	parent.window.close();
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
<base target="_self">
<html:form action="/performance/options/perKnowSort">
<center>
<table width="100%" height="300" border="0" align="center">

  <tr> 
    <td>
    	<fieldset style="width:95%;height:300px;">
    	<legend><bean:message key="jx.param.modifysort"/></legend>
    	<table width="100%"  border="0" align="center">
    	<tr><td colspan="2">&nbsp;</td></tr>
    	<tr>
    		<td width="100%">
    			<html:select name="perKnowForm" property="sort_fields" multiple="multiple"  style="height:243px;width:100%;">
                         <html:optionsCollection property="sortlist" value="dataValue" label="dataName"/>   		      
 		     	</html:select>
    		</td>
    		<td>
    			<table width="100%"  border="0" align="center" >   				   				
    				<tr>
    					<td align="center">
							<html:button  styleClass="mybutton" property="b_up" onclick="upItem($('sort_fields'));">
            		     		<bean:message key="button.previous"/> 
	           				</html:button >
						</td>
    				</tr>
    				<tr>
    					<td>&nbsp;</td>
    				</tr>
    				<tr>
    					<td align="center">
							<html:button  styleClass="mybutton" property="b_down" onclick="downItem($('sort_fields'));">
            		     		<bean:message key="button.next"/>    
	           				</html:button >	 
						</td>
    				</tr>    				
    			</table>
    		</td>
    	</tr>
    	</table>
    	 </fieldset>
    </td>
  </tr>
  <tr>
    	<td align="center" style="height:35px">
    		<input id="ok" type="button" value="<bean:message key='button.ok'/>" onclick="saveSort();" Class="mybutton">
    		<input type="button" value="<bean:message key='button.close'/>" onclick="sort_close();" Class="mybutton">
    	</td>
 </tr>
</table>
</center>
</html:form>
<script>
  var vo= document.getElementsByName('sort_fields');
  var vo1=vo[0];  
  if(vo1.length==0)
  	document.getElementById('ok').disabled=true;
</script>