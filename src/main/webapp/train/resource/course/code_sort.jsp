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
    hashvo.setValue("setid","<bean:write name="courseForm" property="codeSetId"/>");  
    var request=new Request({method:'post',onSuccess:showSelect,functionId:'2020020172'},hashvo);
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
    hashvo.setValue("setid","<bean:write name="courseForm" property="codeSetId"/>");  
    var request=new Request({method:'post',onSuccess:showSelectOk,functionId:'2020020172'},hashvo);
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
<br>
<table width="95%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTableF common_border_color" style="border:1px solid;">
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
         <td width="15%" align="center" valign="top" style="padding-top: 15px;">
         	<table width="40" border="0" cellspacing="1"  align="center" cellpadding="1">
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
<br/>          
	<bean:define name="courseForm" property="sortlist" id="slist" type="java.util.ArrayList"/>
    <input type="button" name="btnreturn" value='<bean:message key="button.ok"/>' class="mybutton" onclick="savefieldOk();" <%=slist.size()==0?"disabled":"" %> />
    &nbsp;&nbsp;<input type="button" name="btnreturn" value='<bean:message key="button.cancel"/>' class="mybutton" onclick="window.close();"/>
</center>
</html:form>
