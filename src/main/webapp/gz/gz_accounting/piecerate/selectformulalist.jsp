<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.gz.gz_accounting.AcountingForm,java.util.*"%>
<script language="JavaScript" src="/js/validate.js"></script>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<hrms:themes />
 <%
	int i=0;
%>

<script language='javascript' >
function gzcompute(busiid,s0100)
{
	var itemids=new Array();
	var temps=document.getElementsByName("chk");
	for(var i=0;i<temps.length;i++)
	{
		if(temps[i].checked)
		{
			itemids[itemids.length]=temps[i].value;
		}
	}
	if(itemids.length==0)
	{
		alert("请选择计算公式!");
		return;
	}
	var waitInfo=eval("wait");			
	waitInfo.style.display="block";
	
	document.getElementsByName("compute")[0].disabled=true;
	var hashvo=new ParameterSet();
	
	hashvo.setValue("itemids",itemids);
	hashvo.setValue("busiid",busiid);	
	hashvo.setValue("s0100",s0100);	
	hashvo.setValue("model","calc");	
   	var request=new Request({method:'post',asynchronous:true,onSuccess:computeIsOk,functionId:'3020091050'},hashvo);
	
}

function computeIsOk(outparamters)
{
	var waitInfo=eval("wait");			
	waitInfo.style.display="none";
	var salaryid=outparamters.getValue("busiid");
	var flag=outparamters.getValue("succeed");

	if(flag=="false")
		return;
	var retvo=new Object();	
	retvo.success="1";
	retvo.salaryid=salaryid;
    window.returnValue=retvo;
	window.close();
}

function setformulavalid(obj,itemid,busiid)
{
	var flag;
	if(obj.checked)
	  flag="1";
	else
	  flag="0";
    var hashvo=new ParameterSet();
 //   hashvo.setValue("itemid",itemid);
//	hashvo.setValue("busiid",busiid);
//	hashvo.setValue("flag",flag);	
//	hashvo.setValue("batch","0");   
//   	var request=new Request({asynchronous:false,functionId:'3020070102'},hashvo); 
}


function batch_set_valid(flag,busiid)
{
	var name;
	if(flag==1)
		Element.allselect('chk');
	else
		Element.unallselect('chk');
  //  var hashvo=new ParameterSet();
//	hashvo.setValue("busiid",busiid);
//	hashvo.setValue("flag",flag);	   
//	hashvo.setValue("batch","1");   
  // 	var request=new Request({asynchronous:false,functionId:'3020070102'},hashvo);	
}

function select_All(obj)
{
	if(obj.checked)
		 batch_set_valid(1,'${pieceRateFormulaForm.busiid}')
	else
		 batch_set_valid(0,'${pieceRateFormulaForm.busiid}')
}

</script>
<hrms:themes />
<html:form action="/gz/gz_accounting/piecerate/search_piecerate_formula"> 
<br>
<br>
<br>

	<div id='wait' style='position:absolute;top:120;left:60;display:none;'>
		<table border="1" width="67%" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td  class="td_style"  height=24>
					<bean:message key="org.autostatic.mainp.calculation.wait"/>
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee  class="marquee_style"  direction="right" width="300" scrollamount="5" scrolldelay="10"  >
						<table cellspacing="1" cellpadding="0">
							<tr height=8>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
							</tr>
						</table>
					</marquee>
				</td>
			</tr>
		</table>
	</div>
<table align="center"  width="80%">
<tr>
<td>

  <fieldset align="center" style="width:100%;">
   <legend><bean:message key="label.gz.select.formula"/></legend>
	<table width="100%" border="0" cellspacing="0" align="left" cellpadding="0">
	 
	<tr>
	 <td width="80%" align='center' >
		<table border="0" cellspacing="0" cellpadding="0">
		<tr>
 		   <td height="370px">
 		    	<div style="height: 360px;overflow: auto" class="complex_border_color">
				<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
				   	  <thead>
			           <tr class="fixedHeaderTr">
				            <td align="center" class="TableRow" style="border-top:none;border-left:none;" nowrap >
								 <input type="checkbox" name="selectAll"    onclick='select_All(this)'  > 	
					    	</td>         
				            <td align="center" class="TableRow" style="border-top:none;border-left:none;border-right:none;" nowrap width="250">
								<bean:message key="label.gz.formula"/>&nbsp;
					    	</td>
			           </tr>
				   	  </thead>
				          <hrms:extenditerate id="element" name="pieceRateFormulaForm" property="formulalistform.list" indexes="indexes"  
				             pagination="formulalistform.pagination" pageCount="200" scope="session">
				          <%
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
				            <td align="center" class="RecordRow" style="border-top:none;border-left:none;" nowrap>
							    <input type="checkbox" name="chk"   
							         <logic:equal name="element" property="useflag" value="1">checked</logic:equal>     
							        value="<bean:write name="element" property="formulaid" filter="true"/>" onclick ="setformulavalid(this,
							        '<bean:write name="element" property="formulaid" filter="true"/>','${pieceRateFormulaForm.busiid}');"> 	            
					    	</td>            
				            <td align="left" class="RecordRow" style="border-top:none;border-left:none;border-right:none;" nowrap>
				                <bean:write name="element" property="itemname" filter="true"/>&nbsp;
					    	</td>
				          </tr>
				        </hrms:extenditerate>
				</table>
 		   </div>
    	   </td>
		</tr>
		</table>    
     </td>     
	</tr>

	</table>
	</fieldset>
</td>
</tr>
	<tr>
	<td>
		<table align="center">
    		<tr >
		  	  <td>
				<button name="compute" Class="mybutton" onclick="gzcompute('${pieceRateFormulaForm.busiid}','${pieceRateFormulaForm.s0100}');">
				<bean:message key="button.computer"/></button>&nbsp;&nbsp;
			  	<button name="cancel" Class="mybutton" onclick="window.close();"><bean:message key="button.cancel"/></button>
			  </td>
	    	</tr>
    	</table>
	</td>
	</tr>
</table>
</html:form>


  