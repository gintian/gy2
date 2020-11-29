<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>

<hrms:themes></hrms:themes>
<style type="text/css"> 
.vButtonmargin {
	margin-bottom: 30px;

}
</style>

<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="javascript">
function saveSort(){ 
	var hashvo=new ParameterSet();
	var tableid = "${setFormulaForm.tableid}";
	hashvo.setValue("tableid",tableid);
	hashvo.setValue("sorting",selectTostr('sort_fields'));
	
	var request=new Request({method:'post',asynchronous:false,
     	onSuccess:showFieldList,functionId:'3020110059'},hashvo);
}
function showFieldList(outparamters){
		var base=outparamters.getValue("info");
		if(base=='ok'){
			window.returnValue = 'ok';
			window.close();
		}else{
			alert("<bean:message key='gz.formula.alert.tempsort.failure'/>");
		}
}
function selectTostr(){
  var vos,right_vo,i,str='';
  vos= document.getElementsByName('sort_fields');
  if(vos==null || vos[0].length==0){
  	return;  	
 	vos[0].options[0].selected=false;
  }
  //设为要可选状态
  right_vo=vos[0];  
  for(i=0;i<right_vo.options.length;i++){
  	var item = right_vo.options[i].value;
	str += item+"`";
  }
  return str;  	
}		   	  		 		
</script>
<%

    UserView userView = (UserView) session.getAttribute(WebConstant.userView);
    String bosflag = "";
    if (userView != null) {
        bosflag = userView.getBosflag();
    }
%>
 <%
     if ("hcm".equals(bosflag)) {
 %>
<link href="/general/template/template.css" rel="stylesheet" type="text/css"/>
<%
    }
%>
<html:form action="/general/salarychange/sort">
<table width="352" height="300" border="0" align="center" class="formulaSortmargin">
  <tr> 
    <td align="center">
    	<fieldset style="width:100%;height:250">
    	<legend><bean:message key='menu.gz.sortitem'/></legend>
    	<table width="100%" border="0" align="center" >
    	<tr>
    		<td width="85%">
    			<html:select name="setFormulaForm" property="sort_fields" multiple="multiple" ondblclick="removeitem('sort_fields');" style="height:300px;width:100%;font-size:10pt">
                    <html:optionsCollection property="sortlist" value="dataValue" label="dataName"/>   		      
 		     	</html:select>
    		</td>
    		<td valign="middle">
    			<table width="100%"  border="0" align="center" height="240">
    		 	<tr>
    					<td align="center">
							<html:button  styleClass="mybutton vButtonmargin" property="b_up" onclick="upItem($('sort_fields'));">
            		     		<bean:message key="button.previous"/> 
	           				</html:button>
	           				<br>					
    			
							<html:button  styleClass="mybutton" property="b_down" onclick="downItem($('sort_fields'));">
            		     		<bean:message key="button.next"/>    
	           				</html:button>	 
						</td>
    				</tr>
    	
    			</table>
    		</td>
    	</tr>
    	</table>
    	 </fieldset>
    </td>
  </tr>
  
  <tr height="35px">
		<td align="center">
		  <input type="button" value="<bean:message key='button.ok'/>" 
					onclick="saveSort();" Class="mybutton"> 			
    					
	     <input type="button" value="<bean:message key='button.close'/>" 
	     onclick="window.close();" Class="mybutton">
     </td>
    </tr>
</table>
</center>
</html:form>
