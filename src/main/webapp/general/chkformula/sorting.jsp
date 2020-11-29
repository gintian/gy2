<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.general.chkformula.ChkFormulaForm"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>

<hrms:themes></hrms:themes>
<style type="text/css"> 
/**上下按钮间距**/
.vButtonmargin {
	margin-bottom: 30px;

}

</style>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="javascript">
function saveSort(){ 
	var hashvo=new ParameterSet();
	var tabid = '${chkFormulaForm.tabid}';
	hashvo.setValue("tabid",tabid);
	hashvo.setValue("sorting",selectTostr('sort_fields'));
	
	var request=new Request({method:'post',asynchronous:false,
     	onSuccess:showFieldList,functionId:'1010092019'},hashvo);
}
function showFieldList(outparamters){
	var base=outparamters.getValue("info");
	if(base=='ok'){
		window.returnValue="ok";
		window.close();
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
<%
   ChkFormulaForm cff = (ChkFormulaForm)session.getAttribute("chkFormulaForm");
   ArrayList sizeList = cff.getSortlist();
%>
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
<html:form action="/general/chkformula/setformula">
<center>
<table width="352" height="300" border="0" align="center" class="sortingmargin">
  <tr> 
    <td>
    	<fieldset style="width:100%;height:300">
    	<legend><bean:message key="workdiary.message.sort.check.formula"/></legend>
    	<table width="100%"  border="0" align="center">
    	<tr>
    		<td width="100%">
    			<html:select name="chkFormulaForm" property="sort_fields" multiple="multiple" ondblclick="removeitem('sort_fields');" style="height:270px;width:100%;font-size:10pt">
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
    				<%//修该不允许排序的问题 xcs 
    			        if(sizeList.size()<=0){
    			    %>
    			        <input type="button" value="<bean:message key='button.ok'/>" onclick="saveSort();" Class="mybutton" disabled>
    			    <%        
    			        }else{
    				%>
    					<input type="button" value="<bean:message key='button.ok'/>" onclick="saveSort();" Class="mybutton">
    				<%
    			        }
    				%>
    				<input type="button" value="<bean:message key='button.close'/>" onclick="window.close();" Class="mybutton"></td>
    				</tr>
</table>
</center>
</html:form>
