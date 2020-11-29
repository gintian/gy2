<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="com.hrms.frame.dao.RecordVo"%>
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<hrms:themes></hrms:themes>
<%int i = 0;%>
<script language="javascript">
   
   function change(){
      keyDefinitionForm.action="/general/deci/definition/search_definition.do?b_query=link";
      keyDefinitionForm.submit();
   }
   
   
   function adds(){
       //keyDefinitionForm.action="/general/deci/definition/search_definition.do?b_add=link&operate=1";

       var value = keyDefinitionForm.object.value;
       var nam = keyDefinitionForm.nam.value;
       if(nam == ""){
       	alert("指标分类不能为空!");
       	return;
       }
       keyDefinitionForm.action="/general/deci/definition/search_definition.do?b_add=link&object="+value+"&nam="+nam;      
       keyDefinitionForm.submit(); 
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
				alert("请选择指标！");
				return;
			}else{
				if(window.confirm("您确认要删除所选记录？")){
				    hashvo.setValue("factorid",str);
				   	var In_paramters="flag=1"; 	
					var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:checkresult,functionId:'05601000026'},hashvo);			
				}
			}
	  }
			
	  function checkresult(outparamters){
		   var info = outparamters.getValue("info");
		   //alert(info);
		   if(info == "true"){
		  	    keyDefinitionForm.action="/general/deci/definition/search_definition.do?b_query=link";
      			keyDefinitionForm.submit();
		   }else{
		   		var arrays = info.split("/");
		   		var message = "指标：";
				for(var i=0 ; i<arrays.length; i++){
					message += arrays[i];
					message += " ";
				}
		   	   message +="存在关联数据，无法删除！";
		   	   alert(message);
		   }
		   
	  }
   
   
   
   
</script>
<html:form action="/general/deci/definition/search_definition">
<table width="85%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
 <tr>
  <td>
      <table width="100%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
		<tr align="center" nowrap >
			<td align="left" nowrap colspan="10" style="padding-bottom:5px;">
				<bean:message key="general.defini.object" />
				&nbsp;
				<html:select name="keyDefinitionForm" property="object" size="1" onchange="change();">
					<html:optionsCollection property="olist" value="dataValue" label="dataName" />
				</html:select>
				&nbsp;&nbsp;&nbsp;
				<bean:message key="general.defini.sort" />
				&nbsp;
				<html:select name="keyDefinitionForm" property="nam" size="1" onchange="change();">
					<html:optionsCollection property="tlist" value="dataValue" label="dataName" />
				</html:select>
				&nbsp;
				<hrms:submit styleClass="mybutton" property="b_next">
					<bean:message key="kq.search_feast.modify" />
				</hrms:submit>
			</td>
		</tr>		
		<tr>
			<td align="center" class="TableRow" nowrap>
            <input type="checkbox" name="selbox" onclick="batch_select(this,'keyDefinitionForm.select');" title='<bean:message key="label.query.selectall"/>'>	    
			</td>
			<td align="center" class="TableRow" nowrap>
				<bean:message key="kq.item.name" />
			</td>
			<td align="center" class="TableRow" nowrap>
				<bean:message key="general.defini.explain" />
			</td>
			<td align="center" class="TableRow" nowrap>
				<bean:message key="kq.item.count" />
			</td>
			<td align="center" class="TableRow" nowrap>
				<bean:message key="general.defini.standard" />
			</td>
			<td align="center" class="TableRow" nowrap>
				<bean:message key="general.defini.control" />
			</td>
			<td align="center" class="TableRow" nowrap>
				<bean:message key="general.defini.method" />
			</td>
			<td align="center" class="TableRow" nowrap>
				<bean:message key="general.defini.xname" />
			</td>
			<td align="center" class="TableRow" nowrap>
				<bean:message key="general.defini.code" />
			</td>
			<td align="center" class="TableRow" nowrap>
				<bean:message key="kq.item.edit" />
			</td>
		</tr>
		<hrms:extenditerate id="element" name="keyDefinitionForm" property="keyDefinitionForm.list" indexes="indexes" pagination="keyDefinitionForm.pagination" pageCount="10" scope="session">
			<%if (i % 2 == 0) {%>
			<tr class="trShallow">
			<%} else {%>
			<tr class="trDeep">
			<%
			}
				i++;
			%>
				<td align="left" class="RecordRow" nowrap>
					<hrms:checkmultibox name="keyDefinitionForm" property="keyDefinitionForm.select" value="true" indexes="indexes" />
					<INPUT type="hidden" name="<%=i%>" value='<bean:write name="element" property="string(factorid)" filter="true"/>'>
				</td>
				<td align="left" class="RecordRow" nowrap>
					&nbsp;<bean:write name="element" property="string(name)" filter="true" />
					&nbsp;
				</td>
				<td align="left" class="RecordRow" nowrap>
					&nbsp;<bean:write name="element" property="string(description)" filter="true" />
					&nbsp;
				</td>
				<td align="center" class="RecordRow" nowrap>
					<bean:write name="element" property="string(formula)" filter="true" />
					&nbsp;
				</td>
				<td align="left" class="RecordRow" nowrap>
					&nbsp;<bean:write name="element" property="string(standard_value)" filter="true" />
					&nbsp;
				</td>
				<td align="left" class="RecordRow" nowrap style="color: red;">
					&nbsp;<bean:write name="element" property="string(control_value)" filter="true" />
					&nbsp;
				</td>
				<td align="left" class="RecordRow" nowrap>
					<logic:equal name="element" property="string(static_method)" value="1">
						&nbsp;<bean:message key="kq.formula.sum" />&nbsp;
                 </logic:equal>
					<logic:equal name="element" property="string(static_method)" value="2">
						&nbsp;<bean:message key="kq.formula.max" />&nbsp;
                 </logic:equal>
					<logic:equal name="element" property="string(static_method)" value="3">
						&nbsp;<bean:message key="kq.formula.min" />&nbsp;
                 </logic:equal>
					<logic:equal name="element" property="string(static_method)" value="4">
						&nbsp;<bean:message key="kq.formula.average" />&nbsp;
                 </logic:equal>
				</td>
				<td align="left" class="RecordRow" >
					<hrms:fieldtoname name="element" fieldname="string(field_name)" fielditem="fielditem" />
					&nbsp;<bean:write name="fielditem" property="dataValue" />
					&nbsp;
				</td>
				<td align="left" class="RecordRow" >
					<bean:write name="element" property="string(codeitem_value)" filter="true" />
					&nbsp;
				</td>
				<%
					RecordVo vo = (RecordVo)pageContext.getAttribute("element");
					String factorid = vo.getString("factorid");
				 %>
				<td align="center" class="RecordRow" nowrap>
					<a href="/general/deci/definition/add_definition.do?b_edit=link&encryptParam=<%=PubFunc.encrypt("set_id="+factorid)%>"><img src="/images/edit.gif" border=0></a>

				</td>

			</tr>
		</hrms:extenditerate>
	</table>
  </td>
  </tr>
  <tr>
  <td>
     <table width="100%" align="center" class="RecordRowP">
		<tr>
			<td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
				<bean:write name="keyDefinitionForm" property="keyDefinitionForm.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
				<bean:write name="keyDefinitionForm" property="keyDefinitionForm.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
				<bean:write name="keyDefinitionForm" property="keyDefinitionForm.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
			<td align="right" nowrap class="tdFontcolor">

				<p align="right">
					<hrms:paginationlink name="keyDefinitionForm" property="keyDefinitionForm.pagination" nameId="keyDefinitionForm" propertyId="roleListProperty">
					</hrms:paginationlink>
			</td>
		</tr>
	</table>
  </td>
  </tr>
</table>
	
	
	<table width="85%" align="center">
		<tr>
			<td align="center" height="35px;">
				<input type="button" name="b_saveb" value="<bean:message key="button.insert"/>" class="mybutton" onclick="adds()">

				<!-- 
					<hrms:submit styleClass="mybutton" property="b_delete">
						<bean:message key="button.delete" />
					</hrms:submit>
				-->		
				<INPUT type="button" name="b_delete" onClick="checkdelete()" value="<bean:message key="button.delete" />" class="mybutton" >
				<INPUT type="button" name="b_return" onClick="javascript:history.back();" value="<bean:message key="button.return" />" class="mybutton" >
				
			</td>
		</tr>
	</table>
</html:form>

