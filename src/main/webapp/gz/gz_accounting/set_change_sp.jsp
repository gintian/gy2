<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
 %>
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
	var hashVo=new ParameterSet();
	var salaryid =  document.getElementById("salaryid").value;
	var param_flag =  document.getElementById("param_flag").value;
	if(param_flag==""){
		param_flag="aa";
	}
	var _collectPoint = "";
	var collectPoint = document.getElementsByName("collectPoint")[0];
	if(collectPoint){
		for(var i=0;i<collectPoint.options.length;i++){
			if(collectPoint.options[i].selected==true){
				_collectPoint = collectPoint.options[i].value;
			}
		}
	}

 	hashVo.setValue("salaryid",salaryid);
 	hashVo.setValue("rightvalue",rightvalue);
 	hashVo.setValue("collectPoint",_collectPoint);
 	hashVo.setValue("entry_type","${accountingForm.entry_type}");
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
<%if("hl".equals(hcmflag)){ %>
<br>
<%} %>
<html:hidden name="accountingForm" property="salaryid"/>
<html:hidden name="accountingForm" property="param_flag"/>
<table width="490px;" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow_lrt" nowrap>
            <logic:equal value="1" name="accountingForm" property="entry_type">
				<bean:message key="menu.gz.chgitem"/>&nbsp;&nbsp;
				</logic:equal>
				 <logic:equal value="0" name="accountingForm" property="entry_type">
				<bean:message key="menu.gz.ffchgitem"/>&nbsp;&nbsp;
				</logic:equal>
				<logic:equal value="savecollect" name="accountingForm" property="param_flag">
				<bean:message key="menu.gz.collectpoint"/>&nbsp;&nbsp;
				</logic:equal>
				<logic:equal value="saveverify" name="accountingForm" property="param_flag">
				<bean:message key="gz.templateset.verifyReportOutItem"/>&nbsp;&nbsp;
				</logic:equal>
				<logic:equal value="savesp" name="accountingForm" property="param_flag">
				设置审批指标&nbsp;&nbsp;
				</logic:equal>
				
            </td>            	        	        	        
           </tr>
   	  </thead>
   	  <logic:equal value="savecollect" name="accountingForm" property="param_flag">
   	  <tr>
   	  	<td width="100%" align="left" class="RecordRow_lrt" nowrap><bean:message key="menu.gz.collectpoint1"/>
   	  	<html:select name="accountingForm" property="collectPoint" style="height:230px;width:40%;font-size:9pt">
                      	<html:optionsCollection property="collectList" value="dataValue" label="dataName"/>  
        </html:select>
        </td>
   	  </tr>
   	  </logic:equal>
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
          	<input type="button" name="button1" value='<bean:message key="reporttypelist.confirm"/>' Class="mybutton" onclick="setscond();">
          	<input type="button" name="button2" value='<bean:message key="kq.register.kqduration.cancel"/>' Class="mybutton" onclick="window.close();">        	                    
          </td>
          </tr>
</table>
</html:form>
