<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.hjsj.sys.VersionControl,
				 org.apache.commons.beanutils.LazyDynaBean,
				 java.util.*,
				 com.hrms.struts.taglib.CommonData,
				 com.hjsj.hrms.actionform.gz.templateset.TemplateSetPropertyForm"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
	VersionControl vc = new VersionControl();
	TemplateSetPropertyForm templateSetPropertyForm=(TemplateSetPropertyForm)session.getAttribute("templateSetPropertyForm");
	String orgid=templateSetPropertyForm.getOrgid();
	String flow_ctrl=templateSetPropertyForm.getFlow_ctrl();
	String amount_ctrl=templateSetPropertyForm.getAmount_ctrl();
	String verify_ctrl=templateSetPropertyForm.getVerify_ctrl();
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
		int versionFlag = 1;
		//zxj 20160613 薪资审批不再区分标准版专业版
		//if (userView != null)
		//	versionFlag = userView.getVersion_flag(); // 1:专业版 0:标准版		
	int num = templateSetPropertyForm.getDbList().size();
 %>

<html>
  <head>
  

  </head>
  <style>
  
  .TEXT_NB {
	BACKGROUND-COLOR:transparent;
	
	BORDER-BOTTOM: medium none; 
	BORDER-LEFT: medium none; 
	BORDER-RIGHT: medium none; 
	BORDER-TOP: medium none;
}

  </style>
  
  
  <script language='javascript' >
  	// 薪资管理-薪资类别-属性 样式调整 xiaoyun 2014-8-25
  	window.onload = function(){  
  		if(document.getElementById('pageset_tab1'))
  			document.getElementById('pageset_tab1').style.marginLeft='3px';
  		if(document.getElementById('pageset_tab3'))
  			document.getElementById('pageset_tab3').style.marginLeft='3px';
  		if(document.getElementById('pageset_tab4'))
  			document.getElementById('pageset_tab4').style.marginLeft='3px';
  		if(document.getElementById('pageset_tab5'))
  			document.getElementById('pageset_tab5').style.marginLeft='3px';
  		if(document.getElementById('pageset_tab6'))
  			document.getElementById('pageset_tab6').style.marginLeft='3px';	
  	} 
  	
  	function sub(module)
  	{

  		if(document.getElementsByName("aroyalty_valid")[0]!=undefined&&document.getElementsByName("aroyalty_valid")[0].checked)
  		{
  			if(trim(document.getElementsByName("royalty_setid")[0].value).length==0)
  			{
  				alert("请设置提成薪资参数!");
  				return;
  			}
  			document.getElementsByName("royalty_valid")[0].value="1";
  		}
  		else
  			document.getElementsByName("royalty_valid")[0].value="0";

  		var isShares=document.getElementsByName("isShare");
  		for(var i=0;i<isShares.length;i++)
  		{
  			if(isShares[i].checked)
  			{
  				if(isShares[i].value=='1'&&document.templateSetPropertyForm.manager.value.length==0)
  				{
  					if(module==0)
 	 					alert(GZ_TEMPLATESET_INFO31+"!");
  					else
  						alert(GZ_TEMPLATESET_INFO32+"!");
  					return;
  				}
  				
  				if(isShares[i].value=='0')
  					document.templateSetPropertyForm.manager.value="";
  			}
  		}
  	
  		
  	    var ctrl=document.getElementById("ctrl_amount");
  		if(!ctrl.checked)
  		{
  		    ctrl.value="0";
  		    ctrl.checked=true;
  		}
  			
  		var priv = document.getElementById("mode_priv");
  		if(priv&&!priv.checked)
  		{
  		    priv.value="0";
  		    priv.checked=true;
  		}
  		var sms = document.getElementById("sms");
  		if(!sms.checked)
  		{
  		    sms.value="0";
  		    sms.checked=true;
  		}
  		var mail = document.getElementById("mail");
  		if(!mail.checked)
  		{
  		    mail.value="0";
  		    mail.checked=true;
  		}
  		
  		var a01z0 = document.getElementById("a01z0");
  		if(!a01z0.checked)
  		{
  		    a01z0.value="0";
  		    a01z0.checked=true;
  		}
  		var field_priv=document.getElementById("field_priv");
  		if(!field_priv.checked)
  		{
  		    field_priv.value="0";
  		    field_priv.checked=true;
  		}
  		var read_field=document.getElementById("read_field");
  		if(!read_field.checked)
  		{
  		    read_field.value="0";
  		    read_field.checked=true;
  		}
  		for(var i=0;i<document.templateSetPropertyForm.elements.length;i++)
  		{
  			if(document.templateSetPropertyForm.elements[i].type=='checkbox')
  			{
  				if(document.templateSetPropertyForm.elements[i].checked==false)
  				{
  					document.templateSetPropertyForm.elements[i].value="-1";
  					document.templateSetPropertyForm.elements[i].checked=true;
  				}
  			}
  		}  
  		
  		var setid=$F('setid');
		var type=$F('type');
		
		var item_str="";
		for(var i=0;i<itemArray.length;i++)
		{
			item_str+="/"+itemArray[i];
		}
		var type_str="";
		for(var i=0;i<typeArray.length;i++)
		{
			type_str+="/"+typeArray[i];
		}
		var set_str="";
		if(typeof(setid)=='string')
		{
			set_str+=setid;
		}
		else
		{
			for(var i=0;i<setid.length;i++)
			{
				set_str+="/"+setid[i];
			}
		}
		var typestr="";
		if(typeof(type)=='string')
		{
			typestr+=type;
		}
		else
		{
			for(var i=0;i<type.length;i++)
			{
				typestr+="/"+type[i];
			}
		}
		document.templateSetPropertyForm.item_str.value=item_str;
		document.templateSetPropertyForm.type_str.value=type_str;
		document.templateSetPropertyForm.set_str.value=set_str;
		document.templateSetPropertyForm.typestr.value=typestr;
		var subNoShowUpdateFashion = document.getElementById("subNoShowUpdateFashion");
  		if(!subNoShowUpdateFashion.checked)
  		{
  		    subNoShowUpdateFashion.value="0";
  		    subNoShowUpdateFashion.checked=true;
  		}
  		var subNoPriv = document.getElementById("subNoPriv");
  		if(!subNoPriv.checked)
  		{
  		    subNoPriv.value="0";
  		    subNoPriv.checked=true;
  		}
  		var allowEditSubdata = document.getElementById("allowEditSubdata");
  		if(!allowEditSubdata.checked)
  		{
  		    allowEditSubdata.value="0";
  		    allowEditSubdata.checked=true;
  		}
  		
  		document.templateSetPropertyForm.action="/gz/templateset/gz_templateProperty.do?b_save=add";
		document.templateSetPropertyForm.submit();
  	}
  	
  	<%  
  	if(request.getParameter("b_save")!=null&&request.getParameter("b_save").equals("add"))
  	{
  	%>
  	window.close();
  	<%
  	}
  	
  	%>
  	
  function setSpchange(){
   var salaryid = "${templateSetPropertyForm.salaryid}";
   var gz_module = "${templateSetPropertyForm.gz_module}";
 	var thecodeurl ="/gz/gz_accounting/set_change_sp.do?b_check=check&flag=alert&salaryid="+salaryid+"&gz_module="+gz_module; 
 	if(isIE6()){
 	    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:530px; dialogHeight:450px;resizable:no;center:yes;scroll:yes;status:no");
 	}else{
 	    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:500px; dialogHeight:430px;resizable:no;center:yes;scroll:yes;status:no");
 	}
 }

  function setCollectPoint(){
   var salaryid = "${templateSetPropertyForm.salaryid}";
 	var thecodeurl ="/gz/gz_accounting/set_change_sp.do?b_query=link&param_flag=collect&salaryid="+salaryid; 
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:500px; dialogHeight:430px;resizable:no;center:yes;scroll:yes;status:no");
  }
  function setSpPoint(){
   var salaryid = "${templateSetPropertyForm.salaryid}";
 	var thecodeurl ="/gz/gz_accounting/set_change_sp.do?b_query=link&param_flag=sp&salaryid="+salaryid; 
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:500px; dialogHeight:430px;resizable:no;center:yes;scroll:yes;status:no");
  }

	//设置管理员
	function setManager()
	{
		var returnValue= select_user_dialog("1",2,"1");
		
		if(returnValue)
		{		
			document.templateSetPropertyForm.manager.value=returnValue.content;
		}
	}
	
	function setShare(opt)
	{
		if(opt==0)
		{
			document.getElementById("manager_td").style.display="none";
			document.templateSetPropertyForm.manager.value="";
			document.templateSetPropertyForm.setName.style.display="none";
		}
		else
		{
			document.getElementById("manager_td").style.display="block";
			document.templateSetPropertyForm.setName.style.display="block";
		}
	}

function hiddenSm()
{
   var obj = document.getElementsByName("flow_ctrl");
   var flow_ctrl;
   for(var i=0;i<obj.length;i++)
   {
      if(obj[i].checked)
      {
         flow_ctrl=obj[i].value;
         break;
      }
   }
   if(flow_ctrl=='1')
   {
      document.getElementById("sm").style.display="block";
	  document.getElementById("sp1").style.display="block";
      document.getElementById("sp_relation").style.display="block";
      document.getElementById("sp_defaultfilter").style.display="block";
      document.getElementById("_reject_mode2").style.display="block";
      <logic:equal value="0" name="templateSetPropertyForm" property="gz_module">
          <%
          if(vc.searchFunctionId("3240315")&&versionFlag==1){ 
          %>
       <hrms:priv func_id="3240315">	
      document.getElementById("sm1").style.display="block";
      </hrms:priv>
         <%} %>
         </logic:equal> 
         <logic:equal value="1" name="templateSetPropertyForm" property="gz_module">
          <%
          if(vc.searchFunctionId("3250313")&&versionFlag==1){ 
          %>
          <hrms:priv func_id="3250313">
          document.getElementById("sm1").style.display="block";
      </hrms:priv>
         <%} %>
         </logic:equal>  
   }
   else
   {
     document.getElementById("sm").style.display="none";
	 document.getElementById("sp1").style.display="none";
     document.getElementById("sp_relation").style.display="none";
     document.getElementById("sp_defaultfilter").style.display="none";
     document.getElementById("_reject_mode2").style.display="none";     
     <logic:equal value="0" name="templateSetPropertyForm" property="gz_module">
          <%
          if(vc.searchFunctionId("3240315")&&versionFlag==1){ 
          %>
          <hrms:priv func_id="3240315">
     document.getElementById("sm1").style.display="none";   
      </hrms:priv>
         <%} %>
         </logic:equal> 
         <logic:equal value="1" name="templateSetPropertyForm" property="gz_module">
          <%
          if(vc.searchFunctionId("3250313")&&versionFlag==1){ 
          %>
          <hrms:priv func_id="3250313">
          document.getElementById("sm1").style.display="none";
      </hrms:priv>
         <%} %>
         </logic:equal>
   }
}
function new_template(obj)
{
  var selectedid;
  for(var i=0;i<obj.options.length;i++)
  {
     if(obj.options[i].selected)
     {
      selectedid=obj.options[i].value;
     }
  }
  if(selectedid=='createnew')
  {
     var theURL="/general/email_template/addEmailTemplate.do?b_init=init`nmodule=5`opt=edit`templateId=first`type=0";
     var url="/general/email_template/iframe_gz_email.jsp?src="+theURL;
     var objlist =window.showModalDialog(url,null,"dialogWidth=900px;dialogHeight=600px;resizable=yes;status=no;"); 
        var hashVo=new ParameterSet();
        hashVo.setValue("nmodule","5");
        var In_parameters="opt=1";
        var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:changeList,functionId:'0202030026'},hashVo);			   
  }
}
function changeList(outparameters)
{
   var codelist=outparameters.getValue("list");
	if(codelist!=null&&codelist.length>1){
		AjaxBind.bind(templateSetPropertyForm.mailTemplateId,codelist);
    }
}
function isVisible(obj)
{
     if(obj.checked)
     {
         obj.value="1";
     }
     else
     { 
         obj.value="0";
     }
}


  </script>
  
  <body>
   <html:form action="/gz/templateset/gz_templateProperty">
   <hrms:tabset name="pageset" width="540px;" height="540" type="false"> 
	<hrms:tab name="tab1" label="label.gz.cond" visible="true">
	 <table width="100%" height='100%' align="center"> 
	 
		<tr> <td class="framestyle" valign="top"><Br>
		 
						<table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">    
						<tr>
						<td>               	
						<fieldset align="center" style="width:90%;">
    							 <legend ><bean:message key="gz.columns.nbase"/></legend>
    								<div style="overflow:auto;width:420px;height:150px;" >
		                      			<table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">  
		                      			<%int i = 1; %>		                 	
		                      				<logic:iterate  id="element" name="templateSetPropertyForm" property="dbList" indexId="id" >
		                      				<%if(num<=5){ %>
			                      				<tr>
			                					<td width="20%" height="25" >
			                					<input type='checkbox' name='dbValue' id="${id }"  <logic:equal  name='element' property='isSelected' value='1'  >checked</logic:equal>  value='<bean:write name="element" property="pre" filter="true"/>' />	
			                					</td>
			                					<td><label for="${id }"><bean:write name="element" property="dbname" filter="true"/></label></td>
			                					</tr>
			                					<%}else{ %>
			                						<%if(i%2!=0){ %>
			                							<tr>
			                							<td width="20%" height="25" >
			                							<input type='checkbox' name='dbValue' id='${id }'  <logic:equal  name='element' property='isSelected' value='1'  >checked</logic:equal>  value='<bean:write name="element" property="pre" filter="true"/>' />	
			                							</td>
			                							<td><label for="${id }"><bean:write name="element" property="dbname" filter="true"/></label></td>
			                						<%}else{ %>
			                						<td width="20%" height="25" >
			                						<input type='checkbox' name='dbValue' id='${id }' <logic:equal  name='element' property='isSelected' value='1'  >checked</logic:equal>  value='<bean:write name="element" property="pre" filter="true"/>' />	
			                						</td>
			                						<td><label for="${id }"><bean:write name="element" property="dbname" filter="true"/></label></td>
			                						</tr>
			                						<%} i++;%>
			                					<%} %>
		                					</logic:iterate>
		                      			</table>
		  							</div>
		                 </fieldset>
		                 </td>
		                 </tr>
		                 </table>
		                 
		                 <br>
		                 <fieldset align="center" style="width:90%;">
    							 <legend ><bean:message key="gz.columns.menScope"/></legend>
		                      			<table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		                      				<tr>
			                					<td width="20%" height="25" >
			                		            <html:radio  name="templateSetPropertyForm" property="personScope" value="1" styleId="personScope1"/><label for="personScope1"><bean:message key="gz.templateset.simpleCondition"/></label>	
			                		            &nbsp;<Input type='button' value='...'  class="mybutton"  onclick='simpleCondition()' />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			                		            <Input type='button' value='<bean:message key="button.clearup"/>'  class="mybutton"  onclick='clearCondition();' />
			                					</td>
			                				</tr>
			                				<tr>
			                					<td width="20%" height="25" >
			                		            <html:radio  name="templateSetPropertyForm" property="personScope" value="2" styleId="personScope2" /><label for="personScope2"><bean:message key="gz.templateset.complexCondition"/></label>
			                		             &nbsp;<Input type='button' value='...'  class="mybutton"  onclick='complexCondition()' />
			                		            
			                		            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			                		            <hrms:priv func_id="325050801,324080801">
			                		            <html:checkbox property="priv_mode" name="templateSetPropertyForm" value="1" styleId="mode_priv"><label for="mode_priv"><bean:message key="gz.templateset.rightFilter"/></label></html:checkbox>		
			                					</hrms:priv>
			                					</td>
			                				</tr>
		                      			</table>
		                 </fieldset>
		                   <br>&nbsp;<br>
		                   <fieldset align="center" style="width:90%;">
    							 <legend ><bean:message key="gz.templateset.shareFashion"/></legend>
		                      			<table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		                      				<tr>
			                					<td width="20%" height="25" >
			                		           		<table ><tr><td>
				                		            <html:radio  name="templateSetPropertyForm"  onclick='setShare(0)' style="margin-left:-2px;" property="isShare" value="0" styleId="isShare0"/><label for="isShare0"><bean:message key="gz.templateset.notShare"/></label>
				                		            &nbsp;
				                		            <html:radio  name="templateSetPropertyForm"  onclick='setShare(1)' property="isShare" value="1" styleId="isShare1"/><label for="isShare1"><bean:message key="gz.templateset.share"/></label>
				                		            </td><td id="manager_td" >
				         
				                		            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				                		             <bean:message key="gz.templateset.manager"/>: <input type='text' size=8 readonly class='TEXT_NB'  name="manager"  value='${templateSetPropertyForm.manager}' />
				                		          	&nbsp;&nbsp;&nbsp;&nbsp; 
				                		          	</td>
				                		          	<td>
				                		             <Input type='button' value='<bean:message key="gz.report.config"/>..'  name='setName' class="mybutton"  onclick='setManager()' />
			                						</td></tr></table>
			                					
			                					</td>
			                				</tr>
			                				
		                      			</table>
		                 </fieldset>
		                 
		</td></tr>
	 </table>
	</hrms:tab>


	<logic:equal name="templateSetPropertyForm" property="gz_module"  value="0">
	
   <hrms:tab name="tab3" label="gz.templateset.taxParam" visible="true">
	 <table width="100%"  height='100%'  align="center"> 
		<tr> <td class="framestyle" valign="top"  align='center'  >
			<Br>
			 <table width="90%"  border="0" align='center' cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		             <tr>
			               <td width="25%" height="30" >
			                  <bean:message key="gz.templateset.computeTaxPoint"/>&nbsp;
			               </td>
			               <td>
			                <html:select name="templateSetPropertyForm" property="calculateTaxTime" style="width:240;"   size="1">
								   <html:optionsCollection property="calculateTaxTimeList" value="dataValue" label="dataName"/>
							</html:select>  
						   </td>
			         </tr>
			         
			          <tr>
			               <td width="25%" height="30" >
			                  <bean:message key="gz.templateset.appealTaxPoint"/>&nbsp;
			               </td>
			               <td>
			                <html:select name="templateSetPropertyForm" property="appealTaxTime" style="width:240;"   size="1">
								   <html:optionsCollection property="appealTaxTimeList" value="dataValue" label="dataName"/>
							</html:select>  
						   </td>
			         </tr>
			         
			          <tr>
			               <td width="25%" height="30" >
			                  <bean:message key="gz.templateset.paySalaryPoint"/>&nbsp;
			               </td>
			               <td>
			                <html:select name="templateSetPropertyForm" property="sendSalaryItem" style="width:240;"   size="1">
								   <html:optionsCollection property="sendSalaryItemList" value="dataValue" label="dataName"/>
							</html:select>  
						   </td>
			         </tr>
			         
			          <tr>
			               <td width="25%" height="30" >
			                 <bean:message key="gz.templateset.computeTaxTypePoint"/>&nbsp;
			               </td>
			               <td>
			                <html:select name="templateSetPropertyForm" property="taxType" style="width:240;"   size="1">
								   <html:optionsCollection property="taxTypeList" value="dataValue" label="dataName"/>
							</html:select>  
						   </td>
			         </tr>
			         
			          <tr>
			               <td width="25%" height="30" >
			                  <bean:message key="gz.templateset.taxDeclare"/>&nbsp;
			               </td>
			               <td>
			                <html:select name="templateSetPropertyForm" property="ratepayingDecalre" style="width:240;"   size="1">
								   <html:optionsCollection property="ratepayingDeclareList" value="dataValue" label="dataName"/>
							</html:select>  
						   </td>
			         </tr>
			         <logic:equal value="1" name="templateSetPropertyForm" property="islsDept">
			          <tr>
			               <td width="25%" height="30" >
			                  <bean:message key="gz.templateset.lsdeptfield"/>&nbsp;
			               </td>
			               <td>
			                <html:select name="templateSetPropertyForm" property="lsDept" style="width:240;"   size="1">
								   <html:optionsCollection property="lsDeptList" value="dataValue" label="dataName"/>
							</html:select>  
						   </td>
			         </tr>
			         </logic:equal>
			         <tr>
			         	<td width='100%' colspan=2>
			         		<Br><Br><br><bean:message key="kq.set.card.explain"/>：
			         	</td>
			         </tr>
			         <tr>
			         	<td  width='100%' colspan=2>
			         		1.<bean:message key="gz.templateset.info1"/><br>
							2.<bean:message key="gz.templateset.info2"/><br>
							3.<bean:message key="gz.templateset.info3"/><br>
							<logic:equal value="1" name="templateSetPropertyForm" property="islsDept">
							 4.<bean:message key="gz.templateset.info5"/>
							</logic:equal>
			         	</td>
			         </tr>
			         
			  </table>
		   
			
			
			
		</td></tr>
	 </table>
	</hrms:tab>
	</logic:equal>
	<hrms:tab name="tab5" label="jx.khplan.spmode" visible="true">
	<table width="100%"  height='100%' align="center"> 
  	 	<tr> <td class="framestyle" valign="top">
    	<table width="100%"  height='100%' align="center"> 
	 	<tr> <td  valign="top">
	 	<fieldset align="center" style="width:90%;position:absolute;top:9;left:30">
	 	   <legend ><bean:message key="gz.templateset.comparepoint"/></legend>
	 	   
	 	   <table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		                      				<tr>
			                					<td width="30%" height="25" nowrap="nowrap"><BR>
	 	  &nbsp;&nbsp;&nbsp;&nbsp; <input type="button" class="mybutton" name="bd" value="设置比对指标" onclick="setSpchange();"/>
	 	 <br>
	 	 <Br>
	 	 </td>
	 	 </tr>
	 	 </table>
	 	   </fieldset>
	 	</td>
	 	</tr>
	 	<tr> <td valign="top">
	 	<fieldset align="center" style="width:90%;position:absolute;top:100;left:30">
    							 <legend ><bean:message key="jx.khplan.spmode"/></legend>
		                      			<table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		                      				<tr style="padding-bottom: 3px;">
			                					<td   height="25" nowrap="nowrap">
			                					&nbsp;
			                					 <html:radio  name="templateSetPropertyForm" property="flow_ctrl" value="0" onclick="hiddenSm();" styleId="approve_no"/><label for="approve_no"><bean:message key="t_template.approve.no"/></label>	
			                		            

			                		             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			                		             <html:radio  name="templateSetPropertyForm" property="flow_ctrl" value="1" onclick="hiddenSm();" styleId="approve_ok"/><label for="approve_ok"><bean:message key="t_template.approve.ok"/></label>
			                		             &nbsp;
												</td>
												<td align="left">
	<logic:equal value="0" name="templateSetPropertyForm" property="gz_module">
          <%
          if(vc.searchFunctionId("3240315")&&versionFlag==1){ 
          %>
          <hrms:priv func_id="3240315">
         
			                					<logic:equal value="1" name="templateSetPropertyForm" property="flow_ctrl" >

			                					  <input style="" id="sm1" type="button" class="mybutton" name="bd" value="<bean:message key="gz.templateset.collectpoint"/>" onclick="setCollectPoint();"/>
			                					</logic:equal>
			                					<logic:equal value="0" name="templateSetPropertyForm" property="flow_ctrl" >
	
			                					  <input style=" display:none" id="sm1" type="button" class="mybutton" name="bd" value="<bean:message key="gz.templateset.collectpoint"/>" onclick="setCollectPoint();"/>
			                					</logic:equal>
			</hrms:priv>
         <%} %>
         </logic:equal> 
		
		<logic:equal value="1" name="templateSetPropertyForm" property="gz_module">
          <%
          if(vc.searchFunctionId("3250313")&&versionFlag==1){ 
          %>
          <hrms:priv func_id="3250313">	                					
			                			<logic:equal value="1" name="templateSetPropertyForm" property="flow_ctrl" >

			                					  <input style="" id="sm1" type="button" class="mybutton" name="bd" value="<bean:message key="gz.templateset.collectpoint"/>" onclick="setCollectPoint();"/>
			                					</logic:equal>
			                					<logic:equal value="0" name="templateSetPropertyForm" property="flow_ctrl" >

			                					  <input style=" display:none" id="sm1" type="button" class="mybutton" name="bd" value="<bean:message key="gz.templateset.collectpoint"/>" onclick="setCollectPoint();"/>
			                					</logic:equal>
		</hrms:priv>
         <%} %>
         </logic:equal>	                			
         </td>
         </tr>
         <tr style="padding-top: 1px;"><td nowrap="nowrap">&nbsp;</td><td align="left">
         
			                					<logic:equal value="1" name="templateSetPropertyForm" property="flow_ctrl" >
												 	<input style="" id="sp1" type="button" class="mybutton" name="sp" value="设置审批指标" onclick="setSpPoint();"/>
			                					</logic:equal>
			                					<logic:equal value="0" name="templateSetPropertyForm" property="flow_ctrl" >
													<input style=" display:none" id="sp1" type="button" class="mybutton" name="sp" value="设置审批指标" onclick="setSpPoint();"/>
			                					</logic:equal>
			                					
			                					</td>				              			
			                				</tr>		
			                				</table>
			                				<table>                					                				                				

         
         
         <logic:equal value="1" name="templateSetPropertyForm" property="flow_ctrl">
    
         <tr  id="_reject_mode2"  height="28" > <td  colspan='2'  >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;驳回方式：
           &nbsp;<html:radio  name="templateSetPropertyForm" property="reject_mode" value="1" styleId="reject_1"/><label for="reject_1">逐级驳回</label> 
              &nbsp;&nbsp;&nbsp;<html:radio  name="templateSetPropertyForm" property="reject_mode" value="2" styleId="reject_2"/> <label for="reject_2">驳回到发起人</label></td></tr>
        
         </logic:equal>
          <logic:equal value="0" name="templateSetPropertyForm" property="flow_ctrl">
          
         <tr  id="_reject_mode2"  height="28" style="display=none"  > <td   colspan='2'  >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;驳回方式：
         &nbsp;<html:radio  name="templateSetPropertyForm" property="reject_mode" value="1" styleId="reject_1"/><label for="reject_1">逐级驳回</label> 
         &nbsp;&nbsp;&nbsp;<html:radio  name="templateSetPropertyForm" property="reject_mode" value="2" styleId="reject_2"/> <label for="reject_2">驳回到发起人</label></td></tr>
       
         </logic:equal>
         
           <tr  id="sp_relation"  style="display=<%=flow_ctrl.equals("0")?"none":"block"%>" > <td  colspan='2'  height="28"  >
           &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;审批关系：
         &nbsp;  
         <html:select name="templateSetPropertyForm" property="sp_relation_id" style="width:200">
			 				<html:optionsCollection property="spRelationList" value="dataValue" label="dataName" />
						</html:select>
          
          </td></tr>
           <tr  id="sp_defaultfilter"  style="display=<%=flow_ctrl.equals("0")?"none":"block"%>" > 
           <td  colspan='2'  height="25"  >
           &nbsp;&nbsp;<bean:message key="gz.templateset.DefaultSpFilterItem"/>：
           &nbsp;&nbsp;<html:select name="templateSetPropertyForm" property="sp_default_filter_id" style="width:200">
			 				<html:optionsCollection property="spDefaultFilterList" value="dataValue" label="dataName" />
						</html:select>
          
          </td></tr>  
         <tr > <td colspan='2'  height="25" >
        
         <br>
         &nbsp;&nbsp;归属单位指标：
&nbsp;&nbsp;<html:select name="templateSetPropertyForm" property="orgid" style="width:150">
			 				<html:optionsCollection property="orgList" value="dataValue" label="dataName" />
						</html:select>
								(除B0110外关联UN的指标)
         </td></tr>
         
         <tr  > <td colspan='2' height="25" >
         &nbsp;&nbsp;归属部门指标：
&nbsp;&nbsp;<html:select name="templateSetPropertyForm" property="deptid" style="width:150">
			 				<html:optionsCollection property="deptList" value="dataValue" label="dataName" />
						</html:select>		(除E0122外关联UM的指标)
         
         </td></tr>
         
          <tr > <td colspan='2' > &nbsp; </td></tr>
         
			                			</table>
		     </fieldset>
	 	</td>
	 	</tr>
	 	
	 		<logic:equal value="1" name="templateSetPropertyForm" property="flow_ctrl" >
			                				<tr id="sm">
			                				</logic:equal>
			                				<logic:equal value="0" name="templateSetPropertyForm" property="flow_ctrl" >
			                				<tr id="sm" style="display:none">
			                				</logic:equal>
 <td valign="top"> 	 
	 	 <fieldset align="center" style="width:90%;position:absolute;top:408;left:30;margin-top: 20px;">
    							 <legend >通知方式</legend>
		                      			<table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		                      				
			                					<tr>
			                				 <td align="left" height="25" style="padding-left: 8px;"><!-- modify by xiaoyun 2014-8-25 -->
			                				 <html:checkbox property="msNotice" name="templateSetPropertyForm" value="1" styleId="sms"/><label for="sms">短信通知</label>
			                				 &nbsp;
			                				 <html:checkbox property="mailNotice" name="templateSetPropertyForm" value="1" styleId="mail"/><label for="mail">邮件通知</label>
			                				</td>
			                				<td align="right">
			                				 通知模板：<html:select name="templateSetPropertyForm" property="mailTemplateId" style="width:160;" size="1" onchange="new_template(this);">
							                 	   <html:optionsCollection property="mailTemplateList" value="dataValue" label="dataName"/>
							                   </html:select>  
							                 &nbsp;  
			                				 </td>
			                				</tr>
			                					</table>
			                					</fieldset><BR>
			                				
	 	</td>
	 	</table>
	 	</td>
	 	</tr>
	 	</table>
	</hrms:tab>
	
	
	
	<hrms:tab name="tab6" label="gz.templateset.sjtjfs" visible="true">
	<!-- 薪资管理-基础数据维护-薪资类别-属性 样式优化 xiaoyun 2014-8-25 start -->
	<!-- 
	<table width="100%"  height='100%' align="center"> 
  	 	<tr> <td valign="top"> 
	 -->
  	<!-- 薪资管理-基础数据维护-薪资类别-属性 样式优化 xiaoyun 2014-8-25 start -->
    	<table width="100%"  height='50%' align="top"> 
	 	<tr> <td class="framestyle" height='320px'  valign="top">
	     <div style="height: 300px;overflow: auto;padding: 5px; ">
				<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
				   	  <thead>
			           <tr>
				            <td align="center" class="TableRow" nowrap width="250">
								<bean:message key="set.label"/>&nbsp;
					    	</td>         
				            <td align="center" class="TableRow" nowrap >
								<bean:message key="label.gz.submit.type"/>&nbsp;
					    	</td>
			           </tr>
				   	  </thead>
				          <% int i=0; 
				          ArrayList typelist= templateSetPropertyForm.getTypelist();
				         
				          %>
				          <hrms:extenditerate id="element" name="templateSetPropertyForm" property="fieldsetlistform.list" indexes="indexes"  pagination="fieldsetlistform.pagination" pageCount="200" scope="session">
				          <%
				          LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
				          String type=(String)abean.get("type");
				          
				          if(i%2==0)
				          {
				          %>
				          <tr class="trShallow">
				          <%}
				          else
				          {%>
				          <tr class="trDeep">
				          <%
				          }
				          i++;          
				          %>  
				            <td align="left" class="RecordRow" nowrap>
				                <bean:write name="element" property="name" filter="true"/>&nbsp;
				                <html:hidden name="element" property="setid"/>
					    	</td>            
				            <td align="center" class="RecordRow" nowrap>
				    		     <select name="type" size="1" onchange="update()" >		
				    		     <%
				    		   		for(int j=0;j<typelist.size();j++)
				    		   		{
					    		    	CommonData dt=(CommonData)typelist.get(j);
					    		     	out.println("<option value='"+dt.getDataValue()+"' ");
					    		     	if(dt.getDataValue().equals(type))
					    		     		out.print(" selected ");
					    		     	out.print(" >"+dt.getDataName()+"</option>");
					    		    }
				    		      %>
				    		     
				    		     </select>		    	
				    		</td>
				          </tr>
				        </hrms:extenditerate>
				</table>
 		   </div>
	     
	     
	      
	    <Input type='button'   style='display:<bean:write name="templateSetPropertyForm" property="isUpdateSet" filter="true"/>'   name="advance"  Class="mybutton" onclick='enter()' value='<bean:message key="button.sys.cond"/>...' >
	 
		</td>
	 	</tr>
	 	
	 	<tr><td align='left' >
	 	<html:checkbox property="subNoShowUpdateFashion" name="templateSetPropertyForm" value="1" styleId="subNoShow_1"></html:checkbox><label for="subNoShow_1">提交时不显示数据操作方式设置</label>
	 	<br>
	 	<html:checkbox property="subNoPriv" name="templateSetPropertyForm" value="1" styleId="subNoShow_2"></html:checkbox><label for="subNoShow_2">数据提交入库不判断子集及指标权限</label>
	 	<br>
	 	<html:checkbox property="allowEditSubdata" name="templateSetPropertyForm" value="1" styleId="isAllow">
	 	<label for="isAllow">
	 	<bean:message key="gz.templateset.allowEditSubdata"/>
	 	</label>
	 	</html:checkbox>
	 	</td></tr> 
	 	</table>
	 	<!-- 薪资管理-基础数据维护-薪资类别-属性 样式优化 xiaoyun 2014-8-25 start -->
		<!-- 
	 	</td>
	 	</tr>
	 	</table>
	 	-->
	 	<!-- 薪资管理-基础数据维护-薪资类别-属性 样式优化 xiaoyun 2014-8-25 end -->  
	</hrms:tab>
	
	
	<hrms:tab name="tab4" label="gz.templateset.otherParam" visible="true">
	 <table width="100%"  height='100%' align="center"> 
	 	<tr> <td class="framestyle" valign="top" style="padding-top: 10px;">
		    <fieldset align="center" style="width:90%;">
    							 <legend ><bean:message key="gz.templateset.selectMoney"/></legend>
		                      			<table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		                      				<tr>
			                					<td width="20%" height="25" align="left">&nbsp;&nbsp;&nbsp;&nbsp;<html:select name="templateSetPropertyForm" property="moneyType" style="width:240;"   size="1"><html:optionsCollection property="moneyTypeList" value="dataValue" label="dataName"/>
								        		</html:select>  
			                					</td>
			                				</tr>
			                			</table>
		     </fieldset>
		   <!-- <Br><Br><Br>
		    
		    <logic:equal name="templateSetPropertyForm" property="gz_module"  value="0">
		    <fieldset align="center" style="width:90%;">
    							 <legend >计件工资项目</legend>
		                      			<table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		                      				<tr>
			                					<td width="20%" height="25" >
			                					<html:select name="templateSetPropertyForm" property="piecerate" style="width:240;"   size="1">
								                              <html:optionsCollection property="piecerateList" value="dataValue" label="dataName"/>
								        		</html:select>  
			                					</td>
			                				</tr>
			                			</table>
		     </fieldset>
		     </logic:equal>
		     -->

		     <logic:equal name="templateSetPropertyForm" property="gz_module"  value="0">
		     		     <%if(versionFlag==1){ %>
		      
		     <fieldset align="center" style="width:90%;padding-top: 10px;">
		     <%}else{ %>
		      <fieldset align="center" style="width:90%;display:none; padding-top: 10px;">
		     <%} %>
    							 <legend >
   <bean:message key="gz.templateset.gzTotalControl"/>
 

    							 </legend>
    							  </logic:equal>
    							 			<%
			                				 String ss="none";
			                				 if(amount_ctrl.equals("1"))
			                				 	ss="block";
			                				%>
    							 <logic:equal name="templateSetPropertyForm" property="gz_module"  value="0">
		                      			<table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		                      				<tr>
			                					<td width="20%" height="25" >
			                					&nbsp;&nbsp;<html:checkbox property="amount_ctrl" name="templateSetPropertyForm" value="1"  onclick='showAmountScope()'   styleId="ctrl_amount"><label for="ctrl_amount"><bean:message key="gz.templateset.info4"/></label></html:checkbox>
			                					</td>
			                				</tr>

			                				<tr  id='amount_scope'  style="display:<%=(ss)%>"   >
			                					<td width="20%" height="25" >
			                					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;控制范围：&nbsp;&nbsp;
			                					<html:checkbox property="amount_ctrl_ff" name="templateSetPropertyForm" value="1"  styleId="amount_ctrl_ff" ><label for="amount_ctrl_ff">控制薪资发放</label></html:checkbox>
			                					&nbsp;&nbsp;&nbsp;
			                					<html:checkbox property="amount_ctrl_sp" name="templateSetPropertyForm" value="1"  styleId="amount_ctrl_sp"  ><label for="amount_ctrl_sp">控制薪资审批</label></html:checkbox>
			                					
			                					</td>
			                				</tr>
			                				<tr  id='amount_type'  style="display:<%=(ss)%>"   >
			                					<td width="20%" height="25" >
			                					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;控制方式：&nbsp;&nbsp;
			                					<html:radio property="ctrlType" name="templateSetPropertyForm" value="1" styleId="ctrlType_1"><label for="ctrlType_1">强制控制</label></html:radio>
			                					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			                					<html:radio property="ctrlType" name="templateSetPropertyForm" value="0" styleId="ctrlType_2"><label for="ctrlType_2">预警提示</label></html:radio>
			                					
			                					</td>
			                				</tr>
			                				
			                			</table>
			                			</fieldset>
			                			 </logic:equal>
			                			 
			                			<logic:notEqual name="templateSetPropertyForm" property="gz_module"  value="0">
		                      			<table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0" style="display:none">                   	
		                      				<tr>
			                					<td width="20%" height="25" >
			                					&nbsp;&nbsp;<html:checkbox property="amount_ctrl" name="templateSetPropertyForm" value="1"  onclick='showAmountScope()'   styleId="ctrl_amount"><label for="ctrl_amount"><bean:message key="gz.templateset.info4"/></label></html:checkbox>
			                					</td>
			                				</tr>

			                				<tr  id='amount_scope'  style="display:<%=(ss)%>"   >
			                					<td width="20%" height="25" >
			                					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;控制范围：&nbsp;&nbsp;
			                					<html:checkbox property="amount_ctrl_ff" name="templateSetPropertyForm" value="1"  styleId="amount_ctrl_ff" ><label for="amount_ctrl_ff">控制薪资发放</label></html:checkbox>
			                					&nbsp;&nbsp;&nbsp;
			                					<html:checkbox property="amount_ctrl_sp" name="templateSetPropertyForm" value="1"  styleId="amount_ctrl_sp"  ><label for="amount_ctrl_sp">控制薪资审批</label></html:checkbox>
			                					
			                					</td>
			                				</tr>
			                				<tr  id='amount_type'  style="display:<%=(ss)%>"   >
			                					<td width="20%" height="25" >
			                					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;控制方式：&nbsp;&nbsp;
			                					<html:radio property="ctrlType" name="templateSetPropertyForm" value="1" styleId="ctrlType_1"><label for="ctrlType_1">强制控制</label></html:radio>
			                					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			                					<html:radio property="ctrlType" name="templateSetPropertyForm" value="0" styleId="ctrlType_2"><label for="ctrlType_2">预警提示</label></html:radio>
			                					
			                					</td>
			                				</tr>
			                				
			                			</table>
			                			 </logic:notEqual>
		     
		     
		     
		      
		     
		     
		     <fieldset align="center" style="width:90%;padding-top: 10px;">
    							 <legend >
					  审核公式控制
    							 </legend>
		                      			<table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		                      				<tr>
			                					<td width="40%" height="25" >
			                					
			                				&nbsp;&nbsp;<html:checkbox property="verify_ctrl"  onclick='showVeriyScope()'   name="templateSetPropertyForm" value="1" styleId="verify_1"><label for="verify_1">是否进行审核公式控制</label></html:checkbox>
			                					</td>
			                					<td width="60%" align='left' >
			                					 <Input type='button' value="<bean:message key="gz.templateset.verifyReportOutItem"/>"  id='verify_item_button'   class="mybutton"  onclick='setVerifyItems()' /> 
			                					
			                					</td>
			                				</tr>
			                				<%
			                				 ss="none";
			                				 if(verify_ctrl.equals("1"))
			                				 	ss="block";
			                				  %>
			                				<tr  id='verify_scope'  style="display:<%=(ss)%>"  >
			                					<td  colspan='2' width="20%" height="25" >
			                					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;控制范围：&nbsp;&nbsp;
			                					<html:checkbox property="verify_ctrl_ff" name="templateSetPropertyForm" value="1" styleId="verify_ff"><label for="verify_ff">控制<logic:equal name="templateSetPropertyForm" property="gz_module"  value="0">薪资</logic:equal><logic:notEqual name="templateSetPropertyForm" property="gz_module"  value="0">保险</logic:notEqual>发放</label></html:checkbox>
			                					&nbsp;&nbsp;&nbsp;
			                					<html:checkbox property="verify_ctrl_sp" name="templateSetPropertyForm" value="1" styleId="verify_sp"><label for="verify_sp">控制<logic:equal name="templateSetPropertyForm" property="gz_module"  value="0">薪资</logic:equal><logic:notEqual name="templateSetPropertyForm" property="gz_module"  value="0">保险</logic:notEqual>审批</label></html:checkbox>
			                					
			                					</td>
			                				</tr>
			                				
			                				
			                			</table>
		     </fieldset>
		     
		     
		     
		     
		     
		     
		     
		     <fieldset align="center" style="width:90%;padding-top: 10px;">
    							 <legend >
    							 停发标识控制
    							  </legend>
		   		 <table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		                      				<tr>
			                					<td width="20%" height="25" >
			                					&nbsp;&nbsp;<html:checkbox property="a01z0Flag" name="templateSetPropertyForm" value="1" styleId="a01z0"><label for="a01z0">显示停发标识</label></html:checkbox>
			                					</td>
			                				</tr>
				 </table>
			</fieldset>
		          <%
		          if(vc.searchFunctionId("3240214")) {%>
		      
		      
		     <fieldset align="center" style="width:90%;padding-top: 10px;">
    							 <legend><bean:message key="gz.bonus.item"/></legend>
		   		 <table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		                      				<tr>
			                					<td width="20%" height="25" style="padding-left: 4px;" >&nbsp;&nbsp;
			                					<html:select name="templateSetPropertyForm" property="bonusItemFld" style="width:240;"   size="1">
								                              <html:optionsCollection property="bonusItemFldList" value="dataValue" label="dataName"/>
								        		</html:select>  
			                					</td>
			                				</tr>
				 </table>
			</fieldset>
			<%} %>
			
			
			  <fieldset align="center" style="width:90%;padding-top: 10px;">
    							 <legend><bean:message key="gz.lable.field_priv"/></legend>
		   		 <table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		                      				<tr>
			                					<td width="20%" height="25" >&nbsp;
			                					<html:checkbox property="field_priv" name="templateSetPropertyForm" value="1" styleId="field_priv_1"><label for="field_priv_1"><bean:message key="gz.lable.info"/></label></html:checkbox>
			                					</td>
			                				</tr>
			                				<tr>
			                					<td width="20%" height="25" >&nbsp;
			                					<html:checkbox property="read_field" name="templateSetPropertyForm" value="1" styleId="read_field_1"><label for="read_field_1"><bean:message key="gz.lable.inforeadfield"/></label></html:checkbox>
			                					</td>
			                				</tr>
				 </table>
			</fieldset>
			
			<% if(vc.searchFunctionId("32416")||vc.searchFunctionId("32516")) {%>
			
			
			  <fieldset align="center" style="width:90%;padding-top: 10px;">
    							 <legend>汇总审批发放金额指标</legend>
		   		 <table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		                      				<tr><!-- 【6232】薪资管理：其它参数，页面优化问题   jingq add 2015.01.15 -->
			                					<td width="20%" height="25" style="padding-left:10px;">
			                					<html:select name="templateSetPropertyForm" property="collect_je_field"     size="1">
								                              <html:optionsCollection property="number_field_list" value="dataValue" label="dataName"/>
								        		</html:select>  
			                					</td>
			                				</tr>
				 </table>
			</fieldset>
			
			<% } %>
			<logic:equal value="0" name="templateSetPropertyForm" property="gz_module">
			<div style="padding-top: 3px;">
			<!--xiegh 20170412 bug25544 add资源权限判断   -->
		<% 	if(vc.searchFunctionId("324080802")&&versionFlag==1&&userView.hasTheFunction("324080802")){ %>
			<input type="checkbox" name="aroyalty_valid"  onclick="showButton(this)" value="1" id="ticheng" style="margin-left: 23px;margin-top: 5px;"/><label for="ticheng">提成薪资</label> 
			&nbsp;
			<Input type='button' value='设置'  id='bt1'  <logic:equal value="0" name="templateSetPropertyForm" property="royalty_valid">style="display:none;vertical-align:-10%;margin-top: 13px;"</logic:equal>    class="mybutton"  onclick='setRoyalty()' /> 
		<% } %>	 
			</div>
			<div style="padding-top: 3px;">
		<% 	if(vc.searchFunctionId("32421")&&versionFlag==1){ %>
			<input type="checkbox" name="apriecerate_valid"  onclick="showButton2(this)" value="1" id='jijian' style="margin-left: 23px;margin-top: 5px;"/><label for="jijian">计件薪资</label> 
			&nbsp;
			 <Input type='button' value='设置'  id='bt2'  <logic:equal value="0" name="templateSetPropertyForm" property="priecerate_valid">style="display:none;vertical-align:-12%;margin-top: 13px;"</logic:equal>    class="mybutton"  onclick='setRoyalty2()' /> 
		<% } %>	 
			</div>
			
		   </logic:equal>
		   <br>&nbsp;
		</td></tr>
	 </table>
	</hrms:tab>
	
	
   </hrms:tabset> 
 <table width="95%" align="center"><tr><td align="center">
     <Input type='button' value='<bean:message key="lable.tz_template.enter"/>'  class="mybutton"  onclick='sub("${templateSetPropertyForm.gz_module}")' /> 
  	 <Input type='button' value='<bean:message key="lable.tz_template.cancel"/>'  class="mybutton"  onclick='window.close()' /> 
  	 </td></tr></table>
    	<input type='hidden' name='condStr' value="" />
    	<input type='hidden' name='cexpr' value="" />
    	
    	<input type='hidden' name='item_str' value="" />
    	<input type='hidden' name='type_str' value="" />
    	<input type='hidden' name='set_str' value="" />
    	<input type='hidden' name='typestr' value="" />
    	<input type='hidden' name='royalty_valid' value="${templateSetPropertyForm.royalty_valid}" />
    	<input type='hidden' name='royalty_setid' value="${templateSetPropertyForm.royalty_setid}" />
    	<input type='hidden' name='royalty_date' value="${templateSetPropertyForm.royalty_date}" />
    	<input type='hidden' name='royalty_period' value="${templateSetPropertyForm.royalty_period}" />
    	<input type='hidden' name='royalty_relation_fields' value="${templateSetPropertyForm.royalty_relation_fields}" />
    	<input type='hidden' name='strExpression'  value="${templateSetPropertyForm.strExpression}"  />
    	<input type='hidden' name='priecerate_expression_str'  value="${templateSetPropertyForm.priecerate_expression_str}"  />
    	<input type='hidden' name='priecerate_zhouq1'  value="${templateSetPropertyForm.priecerate_zhouq1}"  />
    	<input type='hidden' name='priecerate_str'  value="${templateSetPropertyForm.priecerate_str}"  />
    	<input type='hidden' name='priecerate_zhibiao'  value="${templateSetPropertyForm.priecerate_zhibiao}"  />
    	<input type='hidden' name='priecerate_valid'  value="${templateSetPropertyForm.priecerate_valid}"  />    	
    	<input type='hidden' name='verify_item' value="${templateSetPropertyForm.verify_item}" />
    	
   </html:form>
   <script language="javascript">
   		
   		var expr="";
   		var a_condStr="";
   		var a_cexpr="";
   		var a_royalty_valid="${templateSetPropertyForm.royalty_valid}";
   		var a_priecerate_valid="${templateSetPropertyForm.priecerate_valid}";
   		
   		<%  
  	if(!(request.getParameter("b_save")!=null&&request.getParameter("b_save").equals("add")))
  	{
  	%>	
   		intData();
   		<%
   		}%>
   		
   		
   	function showButton(obj)
   	{
   		if(obj.checked)
	   		//document.getElementById("bt1").style.display="block";
	   		document.getElementById("bt1").style.display="inline"; // modify by xiaoyun 2014-8-25
   		else
   			document.getElementById("bt1").style.display="none";
   	}
    function showButton2(obj)
   	{
   		if(obj.checked){
	   		//document.getElementById("bt2").style.display="block"; 
	   		document.getElementById("bt2").style.display="inline"; // modify by xiaoyun 2014-8-25
	   		document.getElementsByName("priecerate_valid")[0].value="1";
   		}else{
   			document.getElementById("bt2").style.display="none";
   			document.getElementsByName("priecerate_valid")[0].value="0";
   			}
   	}	
   		
   	function setRoyalty()
   	{
	   	var thecodeurl="/gz/templateset/gz_templateProperty.do?br_royalty=link"; 
		var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
		var objlist =window.showModalDialog(iframe_url,null,"dialogWidth=500px;dialogHeight=490px;resizable=yes;status=no;");  
   		if(objlist&&objlist!=null)
   		{
   			document.getElementsByName("royalty_setid")[0].value=objlist[0];
   			document.getElementsByName("royalty_date")[0].value=objlist[1];
   			document.getElementsByName("royalty_period")[0].value=objlist[2];
   			document.getElementsByName("royalty_relation_fields")[0].value=objlist[3]; 
		    document.getElementsByName("strExpression")[0].value=objlist[4];
   		}
   	 
   	
   	}
   	
   	function setVerifyItems()
   	{
   	 	var salaryid = "${templateSetPropertyForm.salaryid}";
 		var thecodeurl ="/gz/gz_accounting/set_change_sp.do?b_query=link&param_flag=verify&salaryid="+salaryid; 
 		if(isIE6()){
 		   	    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:520px; dialogHeight:430px;resizable:no;center:yes;scroll:yes;status:no");
 		}else{
 		   	    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:500px; dialogHeight:430px;resizable:no;center:yes;scroll:yes;status:no");
 		}
   	}
   	
   	
   	
      	function setRoyalty2()
   	{
   		
   		expression_str = document.getElementsByName("priecerate_expression_str")[0].value;
   		zhouq1 = document.getElementsByName("priecerate_zhouq1")[0].value;
   		str = document.getElementsByName("priecerate_str")[0].value;
   		zhibiao = document.getElementsByName("priecerate_zhibiao")[0].value;
   		 var salaryid = "${templateSetPropertyForm.salaryid}";
   		 var gz_module = "${templateSetPropertyForm.gz_module}";
	   	var thecodeurl="/gz/gz_accounting/piecerate/search_piecerate.do?b_jjgz=link`zhibiao="+zhibiao+"`str="+str+"`zhouq1="+zhouq1+"`expression_str="+expression_str+"`gz_module="+gz_module+"`salaryid="+salaryid; 
		var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
		var objlist =window.showModalDialog(iframe_url,null,"dialogWidth=500px;dialogHeight=500px;resizable=yes;scroll:no;status=no;");  	
	if(objlist&&objlist!=null)
   		{
   			document.getElementsByName("priecerate_expression_str")[0].value=objlist[0];
   			document.getElementsByName("priecerate_zhouq1")[0].value=objlist[1];
   			document.getElementsByName("priecerate_str")[0].value=objlist[2];
   			document.getElementsByName("priecerate_zhibiao")[0].value=objlist[3]; 
   		}
   	}
   
    function showVeriyScope()
    {
    	if(document.getElementById("verify_ctrl").checked)
    	{
    		//document.getElementById("verify_item_button").style.display="block";
    		document.getElementById("verify_scope").style.display="block";
    		document.getElementById("verify_ctrl_ff").checked=true;
    		document.getElementById("verify_ctrl_sp").checked=true;
    	
    	}
    	else
    	{
    		//document.getElementById("verify_item_button").style.display="none";
    		document.getElementById("verify_scope").style.display="none";
    		document.getElementById("verify_ctrl_ff").checked=false;
    		document.getElementById("verify_ctrl_sp").checked=false;
    	}
    
    }
   
    function showAmountScope()
    {
    	if(document.getElementById("amount_ctrl").checked)
    	{
    		document.getElementById("amount_scope").style.display="block";
    		document.getElementById("amount_ctrl_ff").checked=true;
    		document.getElementById("amount_ctrl_sp").checked=true;
    		document.getElementById("amount_type").style.display="block";
    	
    	}
    	else
    	{
    		document.getElementById("amount_scope").style.display="none";
    		document.getElementById("amount_ctrl_ff").checked=false;
    		document.getElementById("amount_ctrl_sp").checked=false;
    		document.getElementById("amount_type").style.display="none";
    	}
    
    }
   
   
	   		
	function update()
	{
		var flag="none";
		
	
		for(var i=0;i<document.forms[0].elements.length;i++)
		{
			if(document.forms[0].elements[i].type=='select-one'&&document.forms[0].elements[i].name=='type')
			{	
				for(var j=0;j<document.forms[0].elements[i].options.length;j++)
				{
					if(document.forms[0].elements[i].options[j].selected==true&&document.forms[0].elements[i].options[j].value=='0')
						flag="block";
				}
				
			}
		}
		var obj=document.getElementsByName("advance");
		obj[0].style.display=flag;
		
	}
	   		
   		
   		
   	
var itemArray=new Array();
var typeArray=new Array();
function enter()
{
	var setid=$F('setid');
	var index=0;
	var setids="";
	for(var i=0;i<document.forms[0].elements.length;i++)
	{
		if(document.forms[0].elements[i].type=='select-one'&&document.forms[0].elements[i].name=='type')
		{	
			for(var j=0;j<document.forms[0].elements[i].options.length;j++)
			{
				if(document.forms[0].elements[i].options[j].selected==true&&document.forms[0].elements[i].options[j].value=='0')
				{
					setids+="/"+setid[index];
				}
			}
			index++;
		}
	}
	var thecodeurl="/gz/gz_accounting/submit_data.do?b_updateFashion=link`salaryid=${templateSetPropertyForm.salaryid}`sets="+setids; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
	var objlist =window.showModalDialog(iframe_url,null,"dialogWidth=400px;dialogHeight=450px;resizable=yes;status=no;");  
   
    if(objlist!=null)
    {
    	itemArray=objlist.items;
    	typeArray=objlist.types;
    }
}
		
   		function intData()
   		{
   			if(a_royalty_valid=="1"&&document.getElementsByName("aroyalty_valid")[0]!=undefined)
   			{
   				document.getElementsByName("aroyalty_valid")[0].checked=true;
   			}
   			if(a_priecerate_valid=="1"&&document.getElementsByName("apriecerate_valid")[0]!=undefined)
   			{
   				document.getElementsByName("apriecerate_valid")[0].checked=true;
   			}
   			var showButton="${templateSetPropertyForm.isShare}";
   			if(showButton=="0")
   			{
   		   	    document.getElementById("manager_td").style.display="none";
   				document.templateSetPropertyForm.setName.style.display="none";
   				
   			}
   		
   			var In_paramters="salaryid=${templateSetPropertyForm.salaryid}"; 	
			var request=new Request({method:'post',asynchronous:true,parameters:In_paramters,onSuccess:returnInfo,functionId:'3020030017'});			
   		}
   		
   		function returnInfo(outparamters)
   		{
   			var condStr = getDecodeStr(outparamters.getValue("condStr"));
   			var cexpr = getDecodeStr(outparamters.getValue("cexpr"));
   			document.templateSetPropertyForm.condStr.value=condStr;
   			document.templateSetPropertyForm.cexpr.value=cexpr;
   			a_condStr=condStr;
   			<logic:equal name="templateSetPropertyForm" property="personScope"  value="2" >
	  		expr=a_condStr;
	  		</logic:equal>
   			a_cexpr=cexpr;

   		}
   		
   		function simpleCondition()
	  	{
	  		var info,queryType,dbPre;
		    info="1";
		    dbPre="Usr";
	        queryType="1";
	        var express="";
	        <logic:equal name="templateSetPropertyForm" property="personScope"  value="1" >
	        express=a_cexpr+'|'+a_condStr;
	        </logic:equal>
	        var strExpression = generalExpressionDialog(info,dbPre,queryType,express);
	        if(strExpression)
	        {
	        	
	        	var temps=strExpression.split("|");
	        	document.templateSetPropertyForm.cexpr.value=temps[0];
	        	document.templateSetPropertyForm.condStr.value=temps[1];
	        	
	        	document.templateSetPropertyForm.personScope[0].checked=true;
	        }
	  	}
	  	
	  	function complexCondition()
	  	{
	  	
	  		
	  		var strExpression=generalComplexConditionDialog(expr,"0",GZ_TEMPLATESET_LOOKCONDITION,"4");
	  		 if(strExpression!=undefined)
	        {
				document.templateSetPropertyForm.condStr.value=strExpression;
				expr=strExpression;
				document.templateSetPropertyForm.cexpr.value="";
				document.templateSetPropertyForm.personScope[1].checked=true;
	        }
	  	}
  	function clearCondition()
  	{
  	   if(confirm(GZ_TEMPLATESET_INFO33+"！"))
  	   {
  	    var salaryid="${templateSetPropertyForm.salaryid}";
  	    var gz_module="${templateSetPropertyForm.gz_module}";
  	    var hashVo=new ParameterSet();
        hashVo.setValue("salaryid",salaryid);
        hashVo.setValue("gz_module",gz_module);
        var In_parameters="opt=1";
        var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:clear_ok,functionId:'3020030018'},hashVo);			
  	    
  	     }
  	}
  	function clear_ok(outparameters)
  	{
  	    var salaryid=outparameters.getValue("salaryid");
  	    var gz_module=outparameters.getValue("gz_module");
  	   templateSetPropertyForm.action="/gz/templateset/gz_templateProperty.do?b_query=select&gz_module="+gz_module+"&salaryid="+salaryid;
       templateSetPropertyForm.submit();
  	}
   </script>
  </body>
</html>
