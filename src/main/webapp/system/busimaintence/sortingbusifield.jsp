<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<hrms:themes></hrms:themes>
<script language="javascript">
function saveSort(){
	var hashvo=new ParameterSet();
	// var fsetid = document.getElementById("fsetid").value;
	var fsetid = document.getElementsByName("fsetid")[0].value;
	hashvo.setValue("fsetid",fsetid);
	hashvo.setValue("displayid",selectTostr('sort_fields'));
	var request=new Request({method:'post',asynchronous:false,
     	onSuccess:showFieldList,functionId:'1010061023'},hashvo);
}
function showFieldList(outparamters){
		var base=outparamters.getValue("info");
		if(base=='ok'){
			// window.returnValue="ok";
			// window.close();
			parent.return_vo = "ok";
			winClose();
		}else{
			alert(KJG_ZBTX_INF26);
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
function winClose() {
	if(parent.Ext.getCmp('orderitem')){
        parent.Ext.getCmp('orderitem').close();
	}
}
</script>
<base target="_self">
<html:form action="/system/busimaintence/showbusifield">
<center>
<table width="290" height="100%" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr> 
    <td valign="top" height="250px">
    	<html:hidden name="busiMaintenceForm" property="fsetid"/>
    	<fieldset style="width:100%;height:100%">
    	<legend><bean:message key="menu.gz.sortitem"/></legend>
    	<table width="100%"  border="0" align="center">
    	<tr>
    		<td width="90%" align="center" valign="middle">
    			<html:select name="busiMaintenceForm" property="sort_fields" multiple="multiple" ondblclick="removeitem('sort_fields');" style="height:250px;width:100%;font-size:10pt">
                         <html:optionsCollection property="sortlist" value="dataValue" label="dataName"/>   		      
 		     	</html:select>
    		</td>
    		<td width="10%" align="center" valign="middle">
    			
							<html:button  styleClass="smallbutton" property="b_up" onclick="upItem($('sort_fields'));">
            		     		<bean:message key="button.previous"/> 
	           				</html:button >
							<html:button  styleClass="smallbutton" property="b_down" onclick="downItem($('sort_fields'));" style="margin-top:30px;">
            		     		<bean:message key="button.next"/>    
	           				</html:button >	 
						
    		</td>
    	</tr>
    	</table>
    	 </fieldset>
    </td>
  </tr>
  <tr>
    <td align="center" height="35px;">
    	<input type="button" value="<bean:message key='button.ok'/>" onclick="saveSort();" Class="mybutton">
    	<%--<input type="button" value="<bean:message key='button.close'/>" onclick="window.close();" Class="mybutton">--%>
    	<input type="button" value="<bean:message key='button.close'/>" onclick="winClose();" Class="mybutton">
    </td>
  </tr>
</table>
</center>
</html:form>
<script>
if(navigator.userAgent.indexOf("Firefox")>0){ //处理firefox下按钮显示问题 bug 50986 wangb 2020-02-10 
    var fieldset = document.getElementsByTagName('fieldset')[0];
    fieldset.style.height = '110%';
} 
</script>