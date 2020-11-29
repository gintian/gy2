<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript">
function setscond(){
	var rightvalue = "";
	var rights= document.getElementsByName("right_fields");
		for(var i=0;i<rights[0].options.length;i++){
			rightvalue +=rights[0].options[i].value;
			if(i+1<rights[0].options.length){
				rightvalue+=",";
			}
		}
		
	var addrightvalue = "";
	var addrights= document.getElementsByName("addright_fields");
		for(var i=0;i<addrights[0].options.length;i++){
			addrightvalue +=addrights[0].options[i].value;
			if(i+1<addrights[0].options.length){
				addrightvalue+=",";
			}
		}
		
	var delrightvalue = "";
	var delrights= document.getElementsByName("delright_fields");
		for(var i=0;i<delrights[0].options.length;i++){
			delrightvalue +=delrights[0].options[i].value;
			if(i+1<delrights[0].options.length){
				delrightvalue+=",";
			}
		}
		
	var hashVo=new ParameterSet();
	var salaryid =  document.getElementById("salaryid").value;
	var param_flag =  document.getElementById("param_flag").value;
	if(param_flag==""){
		param_flag="aa";
	}
 	hashVo.setValue("salaryid",salaryid);
 	hashVo.setValue("rightvalue",rightvalue);
 	hashVo.setValue("addrightvalue",addrightvalue);
 	hashVo.setValue("delrightvalue",delrightvalue);
 	hashVo.setValue("tempflag","check");
 	hashVo.setValue("entry_type","${accountingForm.entry_type}");
 	hashVo.setValue("gz_module","${templateSetPropertyForm.gz_module}");
 	var In_parameters="param_flag="+param_flag+"&flag=save";
 	var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:checkOk,functionId:'3020110040'},hashVo);
	setselectitem('right_fields');
}
function checkOk(outparamters){
	var check=outparamters.getValue("check");
	if(check=='ok'){
		window.close();
	}else{
		return;
	}
}
</script>
<base id="mybase" target="_self">
<html:form action="/gz/gz_accounting/set_change_sp">

<html:hidden name="accountingForm" property="salaryid"/>
<html:hidden name="accountingForm" property="param_flag"/>
   <hrms:tabset name="pageset" width="490px;" height="380" type="false"> 
	<hrms:tab name="tab1" label="gz.info.change" visible="true">
<table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow_lrt" nowrap>
				<bean:message key="gz.info.change"/>&nbsp;&nbsp;			
            </td>            	        	        	        
           </tr>
   	  </thead>
   	   <tr>
            <td width="100%" align="center" class="RecordRow_lrt" nowrap>
              <table>
                <tr>
                 <td align="center"  width="46%">
                   <table align="center" width="100%">
                   <tr>
                    <td align="left">
                     	<bean:message key="gz.bankdisk.preparefield"/>&nbsp;&nbsp;
                    </td>
                   </tr>
                   <tr>
                    <td align="center">
                      <html:select name="accountingForm" property="left_fields" multiple="multiple" ondblclick="additem('left_fields','right_fields');removeitem('left_fields');" style="height:230px;width:100%;font-size:9pt">
                      	<html:optionsCollection property="leftlist" value="dataValue" label="dataName"/>  
                      </html:select>
                    </td>
                    </tr>
                   
                   </table>
                </td>
               
                <td width="8%" align="center">
                   <html:button  styleClass="mybutton" property="b_addfield" onclick="additem('left_fields','right_fields');removeitem('left_fields');">
            		     <bean:message key="button.setfield.addfield"/> 
	           </html:button >
	           <br>
	           <br>
	           <html:button  styleClass="mybutton" property="b_delfield" onclick="additem('right_fields','left_fields');removeitem('right_fields');">
            		     <bean:message key="button.setfield.delfield"/>    
	           </html:button >	     
                </td>         
                <td width="46%" align="center">
                 <table width="100%">
                  <tr>
                  <td width="100%" align="left">
                     	<bean:message key="gz.bankdisk.selectedfield"/>&nbsp;&nbsp;
                  </td>
                  </tr>
                  <tr>
                  <td width="100%" align="left">
     	             <html:select name="accountingForm" property="right_fields" multiple="multiple" ondblclick="additem('right_fields','left_fields');removeitem('right_fields');" style="height:230px;width:100%;font-size:9pt">
                           <html:optionsCollection property="rightlist" value="dataValue" label="dataName"/>   		      
 		     		</html:select>	
                  </td>
                  </tr>
                  </table>             
                </td>               
                </tr>
              </table>             
            </td>
            </tr>
          <tr>
          <td align="center" class="RecordRow" style="padding-top:3px;padding-bottom:3px;" nowrap>  
          	<input type="button" name="button1" value='<bean:message key="reporttypelist.confirm"/>' Class="mybutton" onclick="setscond();">&nbsp;&nbsp;
          	<input type="button" name="button2" value='<bean:message key="kq.register.kqduration.cancel"/>' Class="mybutton" onclick="window.close();">        	                    
          </td>
          </tr>
</table>
</hrms:tab>
	<hrms:tab name="tab2" label="gz.info.addmen" visible="true">
<table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow_lrt" nowrap>
				<bean:message key="gz.info.addmen"/>&nbsp;&nbsp;			
            </td>            	        	        	        
           </tr>
   	  </thead>
   	   <tr>
            <td width="100%" align="center" class="RecordRow_lrt" nowrap>
              <table>
                <tr>
                 <td align="center"  width="46%">
                   <table align="center" width="100%">
                   <tr>
                    <td align="left">
                     	<bean:message key="gz.bankdisk.preparefield"/>&nbsp;&nbsp;
                    </td>
                   </tr>
                   <tr>
                    <td align="center">
                      <html:select name="accountingForm" property="addleft_fields" multiple="multiple" ondblclick="additem('addleft_fields','addright_fields');removeitem('addleft_fields');" style="height:230px;width:100%;font-size:9pt">
                      	<html:optionsCollection property="addleftlist" value="dataValue" label="dataName"/>  
                      </html:select>
                    </td>
                    </tr>
                   
                   </table>
                </td>
               
                <td width="8%" align="center">
                   <html:button  styleClass="mybutton" property="b_addfield" onclick="additem('addleft_fields','addright_fields');removeitem('addleft_fields');">
            		     <bean:message key="button.setfield.addfield"/> 
	           </html:button >
	           <br>
	           <br>
	           <html:button  styleClass="mybutton" property="b_delfield" onclick="additem('addright_fields','addleft_fields');removeitem('addright_fields');">
            		     <bean:message key="button.setfield.delfield"/>    
	           </html:button >	     
                </td>         
                <td width="46%" align="center">
                 <table width="100%">
                  <tr>
                  <td width="100%" align="left">
                     	<bean:message key="gz.bankdisk.selectedfield"/>&nbsp;&nbsp;
                  </td>
                  </tr>
                  <tr>
                  <td width="100%" align="left">
     	             <html:select name="accountingForm" property="addright_fields" multiple="multiple" ondblclick="additem('addright_fields','addleft_fields');removeitem('addright_fields');" style="height:230px;width:100%;font-size:9pt">
                           <html:optionsCollection property="addrightlist" value="dataValue" label="dataName"/>   		      
 		     		</html:select>	
                  </td>
                  </tr>
                  </table>             
                </td>               
                </tr>
              </table>             
            </td>
            </tr>
          <tr>
          <td align="center" class="RecordRow" style="padding-top:3px;padding-bottom:3px;" nowrap>  
          	<input type="button" name="button1" value='<bean:message key="reporttypelist.confirm"/>' Class="mybutton" onclick="setscond();">&nbsp;&nbsp;
          	<input type="button" name="button2" value='<bean:message key="kq.register.kqduration.cancel"/>' Class="mybutton" onclick="window.close();">        	                    
          </td>
          </tr>
</table>
</hrms:tab>
	<hrms:tab name="tab3" label="gz.info.delmen" visible="true">
<table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow_lrt" nowrap>
				<bean:message key="gz.info.delmen"/>&nbsp;&nbsp;			
            </td>            	        	        	        
           </tr>
   	  </thead>
   	   <tr>
            <td width="100%" align="center" class="RecordRow_lrt" nowrap>
              <table>
                <tr>
                 <td align="center"  width="46%">
                   <table align="center" width="100%">
                   <tr>
                    <td align="left">
                     	<bean:message key="gz.bankdisk.preparefield"/>&nbsp;&nbsp;
                    </td>
                   </tr>
                   <tr>
                    <td align="center">
                      <html:select name="accountingForm" property="delleft_fields" multiple="multiple" ondblclick="additem('delleft_fields','delright_fields');removeitem('delleft_fields');" style="height:230px;width:100%;font-size:9pt">
                      	<html:optionsCollection property="delleftlist" value="dataValue" label="dataName"/>  
                      </html:select>
                    </td>
                    </tr>
                   
                   </table>
                </td>
               
                <td width="8%" align="center">
                   <html:button  styleClass="mybutton" property="b_addfield" onclick="additem('delleft_fields','delright_fields');removeitem('delleft_fields');">
            		     <bean:message key="button.setfield.addfield"/> 
	           </html:button >
	           <br>
	           <br>
	           <html:button  styleClass="mybutton" property="b_delfield" onclick="additem('delright_fields','delleft_fields');removeitem('delright_fields');">
            		     <bean:message key="button.setfield.delfield"/>    
	           </html:button >	     
                </td>         
                <td width="46%" align="center">
                 <table width="100%">
                  <tr>
                  <td width="100%" align="left">
                     	<bean:message key="gz.bankdisk.selectedfield"/>&nbsp;&nbsp;
                  </td>
                  </tr>
                  <tr>
                  <td width="100%" align="left">
     	             <html:select name="accountingForm" property="delright_fields" multiple="multiple" ondblclick="additem('delright_fields','delleft_fields');removeitem('delright_fields');" style="height:230px;width:100%;font-size:9pt">
                           <html:optionsCollection property="delrightlist" value="dataValue" label="dataName"/>   		      
 		     		</html:select>	
                  </td>
                  </tr>
                  </table>             
                </td>               
                </tr>
              </table>             
            </td>
            </tr>
          <tr>
          <td align="center" class="RecordRow" style="padding-top:3px;padding-bottom:3px;" nowrap>  
          	<input type="button" name="button1" value='<bean:message key="reporttypelist.confirm"/>' Class="mybutton" onclick="setscond();">&nbsp;&nbsp;
          	<input type="button" name="button2" value='<bean:message key="kq.register.kqduration.cancel"/>' Class="mybutton" onclick="window.close();">        	                    
          </td>
          </tr>
</table>
</hrms:tab>
</hrms:tabset>
</html:form>
