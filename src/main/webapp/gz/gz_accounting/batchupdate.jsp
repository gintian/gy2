<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/gz/salary.js"></script>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<hrms:themes />
 <%
	int i=0;
%>
<script language="javascript">
	var whl=dialogArguments;
	

	function set_refitem(objname)
	{
	  var itemdesc="";
	  var vo=$(objname);
	  for(i=vo.options.length-1;i>=0;i--)
	  {
	    if(vo.options[i].selected)
	    {
			itemdesc=vo.options[i].text
			break;
	    }
	  }	
 	 symbol('formula',itemdesc);
	}

	function set_codeitem(objname)
	{
	  var itemdesc="";
	  var vo=$(objname);
	  for(i=vo.options.length-1;i>=0;i--)
	  {
	    if(vo.options[i].selected)
	    {
			itemdesc=vo.options[i].value
			break;
	    }
	  }	
 	  symbol('formula',"\""+itemdesc+"\"");
	}	
	
	function getCodeValue()
	{
  		var item=document.getElementById("itemid").value;
		var in_paramters="itemid="+item;
   		var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showCodeFieldList,functionId:'3020050012'});
	}
	
	function showCodeFieldList(outparamters)
	{
		var value=outparamters.getValue("codelist");
		
		var str_value=getDecodeStr(outparamters.getValue("str_value"));
		if(value.length>1)
		{
			var elem=batchForm.codeitem;
			Element.show('coderow');
		AjaxBind.bind(elem,value);
			

			
		//if (typeof(value) != "object" || value.constructor != Array) {
		//	this.reportError(elem,value,"Array Type Needed for binding select!");
		//}
		//while (elem.childNodes.length > 0) {
		//	elem.removeChild(elem.childNodes[0]);
		//}
		//for (var i = 0; i < value.length; i++) 
		//{
		//	var option = document.createElement("OPTION");
		//	var data = value[i];
		//	if (data == null || typeof(data) == "undefined") {
		//		option.value = "";
		//		option.text = "";
		//	}
		//	if (typeof(data) != 'object') {
		//		option.value = data;
		//		option.text = data;
		//	} else {
		//		option.value = getDecodeStr(data.dataValue);
		//		option.text = getDecodeStr(data.dataName);	
		//	}
		//	elem.options.add(option);
		//}
	
		
			if(typeof(str_value)!="undefined"&&str_value.length>0)
			{ 
				var objs=str_value.split("`");
				var _objs= new Array();
				for(var i=0;i<objs.length;i++)
				{
					var temp=objs[i].split("~");
					_objs["_"+temp[0]]=temp[1];
				}
				
				var obj=document.getElementsByName("codeitem");
				for(var i=0;i<obj[0].options.length;i++)
	  			{
	  				var _value=obj[0].options[i].value;
	  				var desc=_objs["_"+_value];
	  				if(typeof(desc)!="undefined")
	  				{
	  					obj[0].options[i].title=desc;
	  				}
	  			}
			}
		}
		else
		{
			Element.hide('coderow');
		}	
}	
function reportError(elem, value, msg)
{
     throw "Data bind failed: "+msg;	
}

</script>
<html:form action="/gz/gz_accounting/batchupdate"> 
<input type="hidden" name="cond" id="cond"/>

	<div id='wait' style='position:absolute;top:40;left:40;display:none;'>
		<table border="1" width="67%" cellspacing="0" cellpadding="4"   class="table_style" height="87" align="center">
			<tr>
				<td  class="td_style"  height=24>
					<bean:message key="org.autostatic.mainp.calculation.wait"/>
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee  class="marquee_style"   direction="right" width="300" scrollamount="5" scrolldelay="10" >
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
<table align="center"  width="400px">
<tr>
	 <td>
  		<fieldset align="center" style="width:100%;">
   			<legend><bean:message key="menu.gz.batch.update"/></legend>	 
				<table width="100%" border="0" cellspacing="0" align="left" cellpadding="2">
				  <tr>
				    <td align="right" nowrap="nowrap">
				    <logic:equal name="batchForm" property="gz_module" value="0">
				    	<bean:message key="label.gz.gzitem"/>
				    </logic:equal>
				    <logic:equal name="batchForm" property="gz_module" value="1">
				    	<bean:message key="label.gz.insitem"/>
				    </logic:equal>
				    </td>
				    	<td>
				    		<html:select name="batchForm" property="itemid" size="1" onchange="getCodeValue();" style="width:265;font-size:9pt">
	                              <html:optionsCollection property="itemlist" value="dataValue" label="dataName"/>				    		
				    		</html:select>
					    </td>
				    <td rowspan="5" valign="middle">
						
						<button name="btn_cond" Class="mybutton" onclick="set_batch_update('cond','${batchForm.salaryid}')"><bean:message key="button.cond"/></button><br><br>
						<button name="btn_wizard" Class="mybutton" onclick="function_Wizard('${batchForm.salaryid}','formula')"><bean:message key="button.wizard"/></button><br><br>
						
				    </td>
				  </tr>
				  <tr>
				    <td align="right" valign="top" nowrap="nowrap"><bean:message key="label.gz.update.src"/></td>
				    <td>
				    	<html:textarea name="batchForm" property="formula"   style="width:265;height:250px;" onclick="this.pos=document.selection.createRange();">
				    	</html:textarea>
				    </td>
				  </tr>
				  <tr>
				    <td align="right"><bean:message key="org.maip.reference.projects"/></td>
				    <td>
				    		<html:select name="batchForm" property="ref_itemid" size="1" onchange="set_refitem('ref_itemid');" style="width:265;font-size:9pt">
	                              <html:optionsCollection property="ref_itemlist" value="dataValue" label="dataName"/>				    		
				    		</html:select>				    
				    </td>
				  </tr>
				  <tr id="coderow">
				    <td align="right"><bean:message key="conlumn.codeitemid.caption"/></td>
				    <td>
							<select name="codeitem" onchange="set_codeitem('codeitem');" style="width:265;font-size:9pt">
             				</select>			    
				    </td>
				  </tr>				  
				  <tr align="right" valign="top">
				    <td><bean:message key="kq.set.card.explain"/>:</td>
				    <td align="left"><bean:message key="label.gz.descript"/></td>
				  </tr>
				</table>
   			
   			
   		</fieldset>
	</td>
	 
</tr>
<tr><td align="center">
<button name="btn_ok" Class="mybutton" onclick="run_batch_update('${batchForm.salaryid}',whl)"><bean:message key="button.ok"/></button>
<button name="cancel" Class="mybutton" onclick="window.close();"><bean:message key="button.cancel"/></button>
</td></tr>
</table>
<script language="javascript">

	Element.hide('coderow');
</script>
</html:form>


  