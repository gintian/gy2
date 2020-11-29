<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript"><!--
function savefield(){  	  
	var hashvo=new ParameterSet();          
    var vos= document.getElementById("right");       
    if(vos.length!=0){
    	var code_fields=new Array();        
        for(var i=0;i<vos.length;i++){
          	var valueS=vos.options[i].value;          
         	code_fields[i]=valueS;
          	for(var j=i+1;j<vos.length;j++){
          		if(valueS.toUpperCase()==vos.options[j].value.toUpperCase()){
          			alert("<bean:message key='train.b_plan.request.select.item'/>");
          			return false;
          		}
        	}
     	}       
	}
    var code_fields=new Array();        
    for(var i=0;i<vos.length;i++){
      	var valueS=vos.options[i].value;          
      	code_fields[i]=valueS;
    }
    hashvo.setValue("subclass_value",code_fields); 
    var request=new Request({method:'post',onSuccess:showSelect,functionId:'2020020120'},hashvo);
}	
function showSelect(outparamters){ 
   	 returnValue="aaaa";
	 window.close(); 
}
function savefieldOk(){  	  
     var hashvo=new ParameterSet();          
     var vos= document.getElementById("sort_recode");
     if(vos.length==0){
     	alert("<bean:message key='train.b_plan.request.notrian.sort'/>");
     	return false;
     }
    var code_fields=new Array();        
    for(var i=0;i<vos.length;i++){
      	var valueS=vos.options[i].value;          
     	code_fields[i]=valueS;
    }          
    hashvo.setValue("subclass_value",code_fields); 
    hashvo.setValue("tablename",'${trainCourseForm.tablename}'); 
    var request=new Request({method:'post',onSuccess:showSelectOk,functionId:'2020020216'},hashvo);
}
function showSelectOk(outparamters){ 
	returnValue="ssss";
	window.close();
}
function closeOk(){ 
	returnValue="ssss";
	window.close();
}	  
--></script>
<html:form action="/train/traincourse/traindata">
<center>
<table width="95%" height="300" border="0" align="center">
  <tr> 
    <td>
    	<fieldset style="width:100%;height:300">
    	<legend><bean:message key="jx.param.modifysort"/></legend>
    	<table width="100%"  border="0" align="center">
    	<tr>
    		<td width="100%">
 		     		<hrms:optioncollection name="trainCourseForm" property="sortlist" collection="selectedlist"/> 
     	             	<html:select property="sort_recode" size="10" multiple="true" style="height:280px;width:100%;font-size:9pt"  styleId="right">
                        	<html:options collection="selectedlist"  property="dataValue" labelProperty="dataName"/>
                     	</html:select>   
    		</td>
    		<td valign="top">
    			<table width="100%"  border="0" cellpadding="0" cellspacing="0" align="center">
    				<tr>
    					<td align="center" valign="top" style="padding-bottom: 10px;">
							<html:button  styleClass="mybutton" property="b_up" onclick="upItem($('sort_recode'));">
            		     		<bean:message key="button.previous"/> 
	           				</html:button >
						</td>
    				</tr>
    				<tr>
    					<td align="center" valign="top"  style="padding-bottom: 10px;">
							<html:button  styleClass="mybutton" property="b_down" onclick="downItem($('sort_recode'));">
            		     		<bean:message key="button.next"/>    
	           				</html:button >	 
						</td>
    				</tr>
    				<tr>
    					<td align="center" valign="top" style="padding-bottom: 10px;">
    						<input id="ok" type="button" value="<bean:message key='button.ok'/>" onclick="savefieldOk();" Class="mybutton"></td>
    				</tr>
    				<tr>
    					<td align="center" valign="top" style="padding-bottom: 10px;">
    						<input type="button" value="<bean:message key='button.close'/>" onclick="window.close();" Class="mybutton"></td>
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
