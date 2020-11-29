<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%int i = 0;%>
<script language="javascript" src="/ajax/common.js"></script>
<script language='javascript'>
	
	  var timer ;
	  var newwindow;
	
	  function adds()
	  {			//【8241】单指标分析，指标分类维护中新增时，指标分类为无效，不对  jingq add 2015.04.03
	    	   target_url="/general/deci/definition/add_definition_type.do?br_add=link&sel=1";
	    	   var dw=330,dh=150,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
	    	   var return_vo=window.showModalDialog(target_url,1,
	    	   		"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
	    	   if(return_vo==null){
	    	   		return;
	    	   }
	    	   var sel = return_vo.sel;
	    	   var type = return_vo.type;
	    	   var hashvo = new ParameterSet();
	    	   hashvo.setValue("type",type);
	    	   hashvo.setValue("sel",sel);
	    	   var request = new Request({asynchronous:false,onSuccess:adds_ok,functionId:'05601000003'},hashvo);
		  	   //timer=window.setInterval("IfWindowClosed()",500);

	  }
	  
	  function adds_ok(outparameters){
	  		keyDefinitionForm.action="/general/deci/definition/search_definition.do?b_next=link";
	  		keyDefinitionForm.submit();
	  }
	  function edit(str)
	  {
	    	   target_url="/general/deci/definition/add_definition_type.do?b_edit=link&set_ida="+str;
	    	   var dw=330,dh=150,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
	    	   var return_vo=window.showModalDialog(target_url,1,
	    	   		"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
	    	   if(return_vo==null){
	    	   		return;
	    	   }
	    	   var sel = return_vo.sel;
	    	   var type = return_vo.type;
	    	   var typeid = return_vo.typeid;
	    	   var hashvo = new ParameterSet();
	    	   hashvo.setValue("type",type);
	    	   hashvo.setValue("sel",sel);
	    	   hashvo.setValue("typeid",typeid);
	    	   var request = new Request({asynchronous:false,onSuccess:adds_ok,functionId:'05601000003'},hashvo);
		  	   //timer=window.setInterval("IfWindowClosed()",500);
	  }
	  
	   function IfWindowClosed() {
		if (newwindow.closed == true) { 
			window.clearInterval(timer)
			keyDefinitionForm.action="/general/deci/definition/search_definition.do?b_next=link"
		    keyDefinitionForm.submit();
		}
	}
		
		
	  function checkdelete(){
			var hashvo=new ParameterSet();
			var str="";
			for(var i=0;i<document.keyDefinitionForm.elements.length;i++)
			{
				if(document.keyDefinitionForm.elements[i].type=="checkbox")
				{
					if(document.keyDefinitionForm.elements[i].checked==true)
					{
						str+=document.keyDefinitionForm.elements[i+1].value+"/";
					}
				}
			}
			if(str.length==0)
			{
				alert("请选择指标类别！");
				return;
			}else{
			    hashvo.setValue("typeid",str);
			   	var In_paramters="flag=1"; 	
				var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:checkresult,functionId:'05601000024'},hashvo);			
			}
	  }
			
	  function checkresult(outparamters){
		   var info = outparamters.getValue("info");
		   if(info == "true"){
		  	   keyDefinitionForm.action="/general/deci/definition/search_definition.do?b_next=link"
			   keyDefinitionForm.submit();
		   }else{
		   		var arrays = info.split("/");
		   		var message = "指标分类：";
				for(var i=0 ; i<arrays.length; i++){
					message += arrays[i];
					message += " ";
				}
		   	   message +="存在关联数据，无法删除！";
		   	   alert(message);
		   }
	  }
	
</script>
<html:form action="/general/deci/definition/definition_list">

	<table width="50%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">

		<tr>
			<td align="center" class="TableRow" nowrap>
				<bean:message key="kq.search_feast.select" />
				&nbsp;
			</td>

			<td align="center" class="TableRow" nowrap>
				<bean:message key="general.defini.sort" />
			</td>
			<td align="center" class="TableRow" nowrap>
				<bean:message key="general.defini.isOk" />
				&nbsp;
			</td>
			<td align="center" class="TableRow" nowrap>
				<bean:message key="kq.feast_type_list.modify" />
			</td>

		</tr>



		<hrms:extenditerate id="element" name="keyDefinitionForm" property="keyDefinition.list" indexes="indexes" pagination="keyDefinition.pagination" pageCount="30" scope="session">
			<%if (i % 2 == 0) {
			%>
			<tr class="trShallow">
				<%} else {%>
			<tr class="trDeep">
				<%}
				i++;
				%>
				<td align="center" class="RecordRow" nowrap>
					<hrms:checkmultibox name="keyDefinitionForm" property="keyDefinition.select" value="true" indexes="indexes" />
					&nbsp;
					<INPUT type="hidden" name="<%=i%>" value='<bean:write name="element" property="string(typeid)" filter="true"/>'>
				</td>

				<td align="center" class="RecordRow" nowrap>
					<bean:write name="element" property="string(name)" filter="true" />
					&nbsp;
				</td>
				<td align="center" class="RecordRow" nowrap>
					<logic:equal name="element" property="string(status)" value="1">
						<bean:message key="column.law_base.status" />
					</logic:equal>
					<logic:equal name="element" property="string(status)" value="0">
						<bean:message key="lable.lawfile.invalidation" />
					</logic:equal>

				</td>
				<td align="center" class="RecordRow" nowrap>
					<a onclick="edit('<bean:write name="element" property="string(typeid)" filter="true"/>');"><img src="/images/edit.gif" border=0></a>
				</td>
			</tr>
		</hrms:extenditerate>
	</table>


	<table width="50%" align="center" class="RecordRowP">
		<tr>
			<td valign="bottom" class="tdFontcolor">
				第
				<bean:write name="keyDefinitionForm" property="keyDefinition.pagination.current" filter="true" />
				页 共
				<bean:write name="keyDefinitionForm" property="keyDefinition.pagination.count" filter="true" />
				条 共
				<bean:write name="keyDefinitionForm" property="keyDefinition.pagination.pages" filter="true" />
				页
			</td>
			<td align="right" nowrap class="tdFontcolor">

				<p align="right">
					<hrms:paginationlink name="keyDefinitionForm" property="keyDefinition.pagination" nameId="keyDefinitionForm" propertyId="roleListProperty">
					</hrms:paginationlink>
			</td>
		</tr>
	</table>




	<table width="70%" align="center">
		<tr>
			<td align="center" height="35px;">
				<input type="button" name="b_saveb" value="<bean:message key="button.insert"/>" class="mybutton" onclick="adds()">
				<!-- <hrms:submit styleClass="mybutton" property="b_delete">
					<bean:message key="button.delete" />
				</hrms:submit>
				-->
				<INPUT type="button" name="b_delete" onClick="checkdelete()" value="<bean:message key="button.delete" />" class="mybutton" >
				
				<hrms:submit styleClass="mybutton" property="b_return">
					<bean:message key="button.return" />
				</hrms:submit>
			</td>
		</tr>
	</table>

</html:form>
