<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<link rel="stylesheet" href="/css/css1.css" type="text/css">
<%@ page import="java.util.*,com.hrms.struts.constant.WebConstant,com.hrms.struts.valueobject.UserView"%>
 <%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
%>
<hrms:themes />
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="javascript">
function saveSort(){ 
	var hashvo=new ParameterSet();
	var salaryid = document.getElementById("salaryid").value;
	hashvo.setValue("salaryid",salaryid);
	hashvo.setValue("sorting",selectTostr('sort_fields'));
	
	var request=new Request({method:'post',asynchronous:false,
     	onSuccess:showFieldList,functionId:'3020090004'},hashvo);
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
<html:form action="/gz/gz_amount/tax/sorting">
<%if("hl".equals(hcmflag)){ %>
<table width="355px" height="300" border="0" align="center" valign="top">
<%}else{ %>
<table width="355px" height="300" border="0" align="center" valign="top"  style="margin-top:-5px;margin-left:-2px;">
<%} %>


    	<html:hidden name="sortingForm" property="salaryid"/>

  <tr> 
    <td align="center">
    	<fieldset style="width:100%;height:300">
    	<legend><bean:message key='menu.gz.sortitem'/></legend>
    	<table width="100%" border="0" align="center">
    	<tr>
    		<td width="80%">
    			<html:select name="sortingForm" property="sort_fields" multiple="multiple" ondblclick="removeitem('sort_fields');" style="height:240px;width:100%;font-size:10pt">
                    <html:optionsCollection property="sortlist" value="dataValue" label="dataName"/>   		      
 		     	</html:select>
    		</td>
    		<td>
    			<table width="100%"  border="0" valign="center" align="center" height="240">

    				<tr>
    					<td align="center">
							<html:button  styleClass="mybutton" property="b_up" onclick="upItem($('sort_fields'));">
            		     		<bean:message key="button.previous"/> 
	           				</html:button >
								<br>
								<br>
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
    <td align="center">
    <input type="button" value="<bean:message key='button.ok'/>" onclick="saveSort();" Class="mybutton">
	<input type="button" value="<bean:message key='button.cancel'/>" onclick="window.close();" Class="mybutton">
	</td>
  </tr>
</table>
</center>
</html:form>
