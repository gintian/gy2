<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript" src="/js/validate.js"></script>
<script language="javascript">
function selectFieldItems1(label,labelValue,flag,type){
	var _polity=$F('polity');
	var url="";
	if(_polity!=null&&_polity.length>0){
		var _ps=_polity.split('.');
		if(_ps.length==2){
			//alert(_ps[0]+"  "+_ps[1]);
			url="/general/deci/definition/add_definition.do?b_select=link&object="+flag+"&type="+type+"&party=party&set="+_ps[0]+"&itemid="+_ps[1]; 		
		}
	}else{
 		url="/general/deci/definition/add_definition.do?b_select=link&object="+flag+"&type="+type+"&party=party"; 		
	}
	var parameter = type;
	var obj= window.showModalDialog(url, parameter, "dialogWidth:300px; dialogHeight:175px;resizable:no;center:yes;scroll:no;status:no");      	
	if(obj == null){
	}else{
		//alert(obj);
		info = obj.split("/");
		var polity=$F('polity');
		labelValue.value=info[0]+"."+info[2];
		label.value=info[3];
		//alert(labelValue.value);
		//alert(polity);
		if(polity!=labelValue.value){
			($('party')).value="";
			($('preparty')).value="";
			($('important')).value="";
			($('active')).value="";
			($('application')).value="";
			($('member')).value="";
			($('person')).value="";
			pparameterForm.action="/dtgh/party/person/party_parameter.do?b_save=link";
			pparameterForm.submit();
		}
	}
 }
 function inintbutton(){
 	if(($('party')).value==""){
 		($('partybutton')).disabled="disabled"
 	}
 	if(($('preparty')).value==""){
 		($('prepartybutton')).disabled="disabled"
 	}
 	if(($('important')).value==""){
 		($('importantbutton')).disabled="disabled"
 	}
 	if(($('active')).value==""){
 		($('activebutton')).disabled="disabled"
 	}
 	if(($('application')).value==""){
 		($('applicationbutton')).disabled="disabled"
 	}
 	if(($('member')).value==""){
 		($('memberbutton')).disabled="disabled";
 	}
 	if(($('person')).value==""){
 		($('personbutton')).disabled="disabled"
 	}
 } 
 function initonebut(param){
 	if(($(param)).value==""){
 		($(param+'button')).disabled="disabled"
 	}else{
 		($(param+'button')).disabled=false;
 	}
 }
 function selectperson(){
 	var return_v=select_codeTree_dialog("${pparameterForm.codesetid }");
 	if(return_v!=null){
 		//alert(return_v.content+"  "+return_v.title);
 		if(return_v.content=="root,"){
 			alert("根节点无效！重新选择。");
 			return;
 		}
		($('person')).value=return_v.content;
		($('personview')).value=return_v.title;
		initonebut('person');
	}
 }
 function localvalidata(){
 	var v1=($('party')).value;
	var v2=($('preparty')).value;
	var v3=($('important')).value;
	var v4=($('active')).value;
	var v5=($('application')).value;
	var v6=($('member')).value;
	var person=($('person')).value;
	for(var i=1;i<6;i++){
		for(var n=i+1;n<7;n++){
			if(eval("v"+i)==eval("v"+n)&&eval("v"+n).length>0){
				alert("政治面貌指标中代码项值不能重复！");
				return false;
			}
		}
	}
	var ps=person.split(',');
	for(var i=0;i<ps.length;i++){
		for(var n=1;n<7;n++){
			if(eval("v"+n)==ps[i]&&eval("v"+n).length>0){
				alert("政治面貌指标中代码项值不能重复！");
				return false;
			}
		}
	}
	return true;
 }
 
 function busSetup(param){
 	if(!localvalidata())
 		return;
 	pparameterForm.action="/dtgh/party/person/party_parameter.do?b_query_bus=link&param="+param;
 	pparameterForm.submit();
 }
</script>
<style>
<!--
  legend {margin-top: 20px;}
  fieldset {padding: 10px;}
-->
</style>
<html:form action="/dtgh/party/person/party_parameter" onsubmit="return localvalidata();">
<table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0" class="RecordRow">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="dtgh.party.parameter.setup"/>
            </td>            	        	        	        
           </tr>
   	  </thead>
   	  <hrms:priv func_id="350410">
    <tr>
    <td align="center">
     <fieldset align="center" style="width:90%;">
         <legend ><bean:message key="dtgh.party.parameter.polity"/></legend>
          <table align="center" > 
          <hrms:priv func_id="35041007">
           <tr>
	           			<td align="right"  nowrap>
	     	       &nbsp;&nbsp;<bean:message key="dtgh.party.parameter.polity.label"/>    	          
		    </td>   
		     <td align="left"  nowrap>
		     			<html:hidden name="pparameterForm" property="polity"/>
	     	         <input type="text" class="text4" name="polityview" value="${pparameterForm.polityview }"  style="width:170px" readonly="readonly"> 
                 <img  src="/images/code.gif" align="absmiddle" onclick="javascript:selectFieldItems1($('polityview'),$('polity'),'A','AC');"/>  
	               <span style="padding-left: 95px;">（人员指标中的代码型指标）</span>  
		    </td>  
                                	        	        
           </tr> 
         </hrms:priv>
           <hrms:priv func_id="35041000">
           <tr> 

              <td align="right"  nowrap>
     	       <bean:message key="dtgh.party.party.label"/>
	    </td>  
	    <td align="left"  nowrap>
                   <html:select name="pparameterForm" property="party" size="1" onchange="initonebut('party');" style="width:170px">
                <html:optionsCollection property="politylist" value="dataValue" label="dataName" /> 
                </html:select>
               	<input type="button" name="partybutton" class="mybutton" onclick="busSetup('party');" value="<bean:message key='dtgh.party.setup.bus'/>">
                （政治面貌指标中代码项值）
	    </td>                       	        	        
           </tr> 
           </hrms:priv>
           <hrms:priv func_id="35041001">
            <tr>
              <td align="right"  nowrap>
     	       <bean:message key="dtgh.party.prep.party.label"/>
	    </td>  
	    <td align="left"  nowrap>
                   <html:select name="pparameterForm" property="preparty" size="1" onchange="initonebut('preparty');" style="width:170px">
                <html:optionsCollection property="politylist" value="dataValue" label="dataName" /> 
                </html:select>
               	<input type="button" name="prepartybutton" class="mybutton" onclick="busSetup('preparty');" value="<bean:message key='dtgh.party.setup.bus'/>">
                （政治面貌指标中代码项值）
	    </td>                       	        	        
           </tr>  
           </hrms:priv>
           <hrms:priv func_id="35041002">
       <tr>
              <td align="right"  nowrap>
     	       <bean:message key="dtgh.party.important.label"/>
	    </td>  
	    <td align="left"  nowrap>
    	           <html:select name="pparameterForm" property="important" size="1" onchange="initonebut('important');" style="width:170px">
                <html:optionsCollection property="politylist" value="dataValue" label="dataName" /> 
                </html:select>
                <input type="button" name="importantbutton" class="mybutton" onclick="busSetup('important');" value="<bean:message key='dtgh.party.setup.bus'/>">
                （政治面貌指标中代码项值）
	    </td>                       	        	        
           </tr>
           </hrms:priv>
           <hrms:priv func_id="35041003">
           <tr>
              <td align="right"  nowrap>
     	       <bean:message key="dtgh.party.active.label"/>
	    </td>  
	    <td align="left"  nowrap>
    	           <html:select name="pparameterForm" property="active" size="1" onchange="initonebut('active');" style="width:170px">
                	<html:optionsCollection property="politylist" value="dataValue" label="dataName" /> 
                </html:select>
                <input type="button" name="activebutton" class="mybutton" onclick="busSetup('active');" value="<bean:message key='dtgh.party.setup.bus'/>">
                （政治面貌指标中代码项值）
	    </td>                       	        	        
           </tr>
           </hrms:priv>
           <hrms:priv func_id="35041004">
           <tr>
              <td align="right"  nowrap>
     	       <bean:message key="dtgh.party.application.label"/>
	    </td>  
	    <td align="left"  nowrap>
    	           <html:select name="pparameterForm" property="application" size="1" onchange="initonebut('application');" style="width:170px">
    	           <html:optionsCollection property="politylist" value="dataValue" label="dataName" /> 
                </html:select>
                <input type="button" name="applicationbutton" class="mybutton" onclick="busSetup('application');" value="<bean:message key='dtgh.party.setup.bus'/>">
                （政治面貌指标中代码项值）
	    </td>                       	        	        
           </tr>
           </hrms:priv>
           <hrms:priv func_id="35041005">
            <tr>
              <td align="right"  nowrap>
     	       <bean:message key="dtgh.party.member.label"/>
	    </td>  
	    <td align="left"  nowrap>
    	        <html:select name="pparameterForm" property="member" size="1" onchange="initonebut('member');" style="width:170px">
    	           <html:optionsCollection property="politylist" value="dataValue" label="dataName" /> 
                </html:select>
                <input type="button" name="memberbutton" class="mybutton" onclick="busSetup('member');" value="<bean:message key='dtgh.party.setup.bus'/>">
                （政治面貌指标中代码项值）
	    </td>                       	        	        
           </tr>
           </hrms:priv>
           <hrms:priv func_id="35041006">
            <tr>
              <td align="right"  nowrap>
     	       <bean:message key="dtgh.party.person.label"/>
	    </td>  
	    <td align="left"  nowrap>
	    			<html:hidden name="pparameterForm" property="person"/>
    	           <input type="text" class="text4" name="personview" value="${pparameterForm.personview }" style="width:148px" readonly="readonly"> 
                 <img  src="/images/code.gif" align="absmiddle" onclick='javascript:selectperson();'/> 
                <input type="button" name="personbutton" class="mybutton" onclick="busSetup('person');" value="<bean:message key='dtgh.party.setup.bus'/>" >
                （政治面貌指标中代码项值）
	    </td>                       	        	          
          </tr>  
</hrms:priv>        
       </table>
    </fieldset>  
    </td>
   </tr>
   </hrms:priv>
   <hrms:priv func_id="350411">
   <tr>
    <td align="center">
     <fieldset align="center" style="width:90%;">
         <legend ><bean:message key="dtgh.party.Y.codesetdesc"/></legend>
          <table align="center" > 
           <tr>
              <td align="right"  nowrap>
     	       <bean:message key="dtgh.party.belong.party"/>    	          
	    </td>   
	     <td align="left"  nowrap>
     	         <html:select name="pparameterForm" property="belongparty" size="1">
     	         <html:optionsCollection property="belongpartylist" value="dataValue" label="dataName" /> 
                </html:select> 
                (人员基本情况子集，关联代码类64的指标) 
	    </td>                     	        	        
           </tr>           
       </table>
    </fieldset>  
    </td>
   </tr>
   </hrms:priv>
   <hrms:priv func_id="350412">
   <tr>
    <td align="center">
     <fieldset align="center" style="width:90%;">
         <legend ><bean:message key="dtgh.party.V.codesetdesc"/></legend>
          <table align="center" > 
           <tr>
              <td align="right"  nowrap>
     	       <bean:message key="dtgh.party.belong.member"/>    	          
	    </td>   
	     <td align="left"  nowrap>
     	         <html:select name="pparameterForm" property="belongmember" size="1">
     	         	<html:optionsCollection property="belongmemberlist" value="dataValue" label="dataName" /> 
                </html:select>  
                (人员基本情况子集，关联代码类65的指标)
	    </td>                     	        	        
           </tr>           
       </table>
    </fieldset>  
    </td>
   </tr>
   </hrms:priv>
   <!-- tr>
    <td align="center">
     <fieldset align="center" style="width:90%;">
         <legend ><bean:message key="dtgh.party.W.codesetdesc"/></legend>
          <table align="center" > 
           <tr>
              <td align="right"  nowrap>
     	       <bean:message key="dtgh.party.belong.meet"/>    	          
	    </td>   
	     <td align="left"  nowrap>
     	         <html:select name="pparameterForm" property="belongmeet" size="1">
     	         <html:optionsCollection property="belongmeetlist" value="dataValue" label="dataName" /> 
                </html:select>  
                (人员基本情况子集，关联代码类66的指标)
	    </td>                     	        	        
           </tr>           
       </table>
    </fieldset>  
    </td>
   </tr> -->
   <tr height="5"><td>&nbsp;</td></tr>
</table>
<table width="70%" border="0" cellspacing="0"  align="center" cellpadding="0">

<tr>
        <td align="center"  nowrap style="padding: 5px;">
            <hrms:submit property="b_save" styleClass="mybutton">&nbsp;<bean:message key='button.save'/>&nbsp;</hrms:submit>
			<logic:equal value="dxt" name="pparameterForm" property="returnvalue">       
			   <hrms:tipwizardbutton flag="dtgh" target="il_body" formname="pparameterForm"></hrms:tipwizardbutton>
			</logic:equal>
            
        </td>
   </tr> 
</table>

</html:form>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script>
	inintbutton();
</script>