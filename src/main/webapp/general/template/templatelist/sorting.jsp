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
.gztable {
 	border-right:#7b9ebd 1px solid;
 	border-left:#7b9ebd 1px solid;
 	border-top:#7b9ebd 1px solid;
 	border-bottom:#7b9ebd 1px solid;
 	word-break: break-all; 
 	word-wrap:break-word;
}
#scroll_box {
	border: 1px  #eee;
 	border-bottom:#7b9ebd 1px solid;
	height: 360px;    
	width: 325px;            
	overflow: auto;            
	margin:0px 5px 5px ;
}


</style>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="javascript">
function saveSort(){ 
	var hashvo=new ParameterSet();
	var tabid = document.getElementById("tabid").value;
	hashvo.setValue("tabid",tabid);
	hashvo.setValue("sorting",selectTostr('sort_fields'));
	
	var request=new Request({method:'post',asynchronous:false,
     	onSuccess:showFieldList,functionId:'0570040024'},hashvo);
}
function showFieldList(outparamters){
		var base=outparamters.getValue("info");
		
			window.returnValue = base;
			window.close();
		
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
<html:form action="/general/template/sorting">
 <html:hidden name="templateListForm" property="tabid"/>
<table width="324"  border="0" class="sortingmargin">
  <tr> 
    <td align="center">
    	<fieldset style="width:100%;height:270">
    	<legend><bean:message key='menu.gz.sortitem'/></legend>
    	<table width="100%" border="0" align="center">
    	<tr>
    		<td width="80%">
    			<html:select name="templateListForm" property="sort_fields" multiple="multiple"  style="height:240px;width:100%;font-size:10pt">
                    <html:optionsCollection property="sortlist" value="dataValue" label="dataName"/>   		      
 		     	</html:select>
    		</td>
    		<td valign="middle">
    			<table width="100%"  border="0" align="center"  valign="center" height="240">
        		  <tr>
    					<td align="center">
							<html:button  styleClass="mybutton vButtonmargin" property="b_up" onclick="upItem($('sort_fields'));">
            		     		<bean:message key="button.previous"/> 
	           				</html:button >
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
  
   <tr height="35px">  				
		<td align="center">
			<input type="button" value="<bean:message key='button.ok'/>" onclick="saveSort();" Class="mybutton">
			<input type="button" value="<bean:message key='button.cancel'/>" onclick="window.close();" Class="mybutton">
		</td>
	</tr>
    				
    				
</table>
</html:form>
