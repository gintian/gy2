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
    hashvo.setValue("tablename",'${courseForm.tablename}'); 
    var request=new Request({method:'post',onSuccess:showSelectOk,functionId:'2020030056'},hashvo);
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
<html:form action="/train/resource/course">
<center>
<div class="fixedDiv3">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="1" style="border:1px solid #C4D8EE;" class="ListTableF common_border_color" >
	<tr>
		<td width="100%" align="left" class="TableRow" colspan="2">
          	&nbsp;&nbsp;<bean:message key='button.movenextpre'/>
       	</td>
	</tr>
	<tr>
		<td width="85%" height="260" align="right">
        	
                  	<hrms:optioncollection name="courseForm" property="sortlist" collection="selectedlist"/> 
     	             <html:select property="sort_recode" size="10" multiple="true" style="height:230px;width:95%;font-size:9pt"  styleId="right">
                        	<html:options collection="selectedlist"  property="dataValue" labelProperty="dataName"/>
                     	</html:select>		
                     	            
         </td>
         <td width="15%" align="center">
         	<table width="40" border="0" cellspacing="0"  align="center" cellpadding="1">
         	<tr>
         		<td>
					<html:button styleClass="mybutton" property="b_up" onclick="upItem($('sort_recode'));">
					<bean:message key="button.previous" />
					</html:button>
				</td>
			</tr>
			<tr><td>&nbsp;</td></tr>
			<tr>
				<td>	
					<html:button styleClass="mybutton" property="b_down" onclick="downItem($('sort_recode'));">
					<bean:message key="button.next" />
					</html:button>
				</td>
			</tr>
			</table>
		</td>                              
    </tr>
</table>   
	<bean:define name="courseForm" property="sortlist" id="slist" type="java.util.ArrayList"/>
    <input type="button" name="btnreturn" value='<bean:message key="button.ok"/>' class="mybutton" style="margin-top: 5px;" onclick="savefieldOk();" <%=slist.size()==0?"disabled":"" %> />
    <input type="button" name="btnreturn" value='<bean:message key="button.cancel"/>' style="margin-top: 5px;" class="mybutton" onclick="window.close();"/>
    </div>
</center>
</html:form>
