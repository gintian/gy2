<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script type="text/javascript">
	function changeSalarySet(){
	     var salaryid=financial_voucherForm.salaryid.value;
	     var pn_id=document.getElementById("pn_id").value;
	    var fl_id=document.getElementById("fl_id").value;
	     var hashvo=new ParameterSet();
	     hashvo.setValue("salaryid",salaryid);
	     hashvo.setValue("pn_id",pn_id);
	    hashvo.setValue("fl_id",fl_id);
	   	 var In_paramters="itemflag=1"; 	
		 var request=new Request({method:'post',asynchronous:false,
		parameters:In_paramters,onSuccess:changeSalarySetOk,functionId:'3020073017'},hashvo);
	}
	function changeSalarySetOk(outparameters)
	{
	   var fielditemlist=outparameters.getValue("salaryItemList");
	   var cgroupList =outparameters.getValue("cgroupList");
	   AjaxBind.bind(financial_voucherForm.itemdesc,fielditemlist);
	   AjaxBind.bind(financial_voucherForm.right_itemdesc,cgroupList);
	}
	function savefield(){ 
	     var hashvo=new ParameterSet();   
	     var vos= document.getElementById("right");       
	     var code=document.getElementById("itemdesc").value;
	     if(vos.length!=0)  {
	        for(var i=0;i<vos.length;i++)
	        {
	          var valueS=vos.options[i].value;
	          //xiegh 20170421 bug24071
	          if(code =='' || code == null){
	        		alert(CHOICE_EXECUTE_REGISTER);
	          		return false;
	          }
	          	if(valueS.toUpperCase()==code)
	          	{
	          		alert(SAME_SUBSET_RE_SELECT);
	          		return false;
	          	}
	        }       
    		 }
	     additem('itemdesc','right_itemdesc');
   	}
	function savefieldOk(){  	  
	    var hashvo=new ParameterSet();          
	    var vos= document.getElementById("right");     
	    var pn_id=document.getElementById("pn_id").value;
	    var fl_id=document.getElementById("fl_id").value;
	    var code_fields="";        
	    for(var i=0;i<vos.length;i++)
	    {
	      var valueS=vos.options[i].value;          
	      code_fields=code_fields+valueS+",";
	    }          
	    hashvo.setValue("c_group",code_fields); 
	    hashvo.setValue("pn_id",pn_id);
	    hashvo.setValue("fl_id",fl_id);
	    var InParameters="";
	    var request=new Request({method:'post',parameters:InParameters,onSuccess:showSelectOk,functionId:'3020073018'},hashvo);
	   }
    function showSelectOk(outparameters) { 
		var parameter=new Array();
		parameter[0]=outparameters.getValue("pn_id");
		parameter[1]=outparameters.getValue("interface_type");
		returnValue=parameter;
		window.close();
		
   	}
   	function itemback(){
   		window.close();
   	}
</script>
<html:form action="/gz/voucher/setgroup">
	<html:hidden property ="pn_id"/>
	<html:hidden property ="fl_id"/>

	<table width="515" border="0" cellspacing="0" align="center" style="margin-left: -5px;"
		cellpadding="0" class="ListTable">
		<tr>
			<td align="left" class="TableRow_lrt" nowrap colspan="3">
				<bean:message key="gz.report.groupfield" />
			</td>
		</tr>
		<tr>
			<td width="100%" align="center" nowrap>
				<table width="100%" align="center" border="0" cellspacing="0"  cellpadding="0" class="RecordRow_lrt">
					<tr>
						<td align="center" width="46%">
							<table align="center" width="100%">
								<tr>
									<td align="left" height="32" style="padding-left: 5px;">
										<bean:message key="label.gz.itemback" />
										&nbsp;&nbsp;
									</td>
								</tr>
								<tr>
									<td align="center" style="padding-left: 5px;padding-bottom: 5px;">
										<hrms:optioncollection name="financial_voucherForm" property="salarySetList" collection="list" />
										<html:select property="salaryid" style="width:97%;font-size:9pt"
											onchange="changeSalarySet();">
											<html:options collection="list" property="dataValue"
												labelProperty="dataName" />
										</html:select>
									</td>

								</tr>
								<tr>
									<td align="left" height="32" style="padding-left: 5px;padding-bottom: 5px;">
										<hrms:optioncollection name="financial_voucherForm"
											property="salaryItemList" collection="list" />
										<html:select property="itemdesc" size="10" multiple="true"
											style="height:230px;width:97%;font-size:9pt"
											ondblclick="savefield();">
											<html:options collection="list" property="dataValue"
												labelProperty="dataName" />
										</html:select>
									</td>
								</tr>
							</table>
						</td>

						<td width="8%" align="center">
							<html:button styleClass="mybutton" property="b_addfield"
								onclick="savefield();">
								<bean:message key="button.setfield.addfield" />
							</html:button>
							<br>
							<br>
							<html:button styleClass="mybutton" property="b_delfield"
								onclick="removeitem('right_itemdesc');">
								<bean:message key="button.setfield.delfield" />
							</html:button>
						</td>


						<td width="46%" align="center">


							<table width="100%">
								<tr>
									<td width="100%" align="left" height="32">
										<bean:message key="label.gz.selecteditem" />
										&nbsp;&nbsp;
									</td>
								</tr>
								<tr>
									<td width="100%" align="left" style="padding-bottom: 5px;">
										<hrms:optioncollection name="financial_voucherForm"
											property="cgroupList" collection="selectedlist" />
										<html:select property="right_itemdesc" size="10" multiple="true"
											style="height:230px;width:97%;font-size:9pt" styleId="right"
											ondblclick="removeitem('right_itemdesc');">
											<html:options collection="selectedlist" property="dataValue"
												labelProperty="dataName" />
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
			<td align="center" class="RecordRow" style="padding-top: 2px;padding-bottom: 1px;height: 35px;" nowrap colspan="3">
					<input type="button" name="save"
						value='<bean:message key="button.ok"/>' class="mybutton"
						onclick="  savefieldOk();">
					<input type="button" name="btnreturn"
						value='<bean:message key="button.cancel"/>' class="mybutton"
						onclick=" itemback();">
			</td>
		</tr>
	</table>
<script language="javaScript">
  <%String privflag = (String)request.getParameter("privflag");%>
  	<%if("3".equals(privflag)){%>
		document.getElementsByName("save")[0].disabled=true;
	<%}%>
</script>
</html:form>
