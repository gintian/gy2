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

<html>
  <head>
  

  </head>
  <style type="text/css">
	
	#scroll_box {
	           border: 1px solid #ccc;
	           height: 170px;    
	           width: 270px;            
	           overflow: auto;            
	           margin: 1em 0;
	       }
	</style>
  <script language='javascript' >
  	function selectAll()
  	{
  		for(var i=0;i<document.gzReportForm.elements.length;i++)
  		{
  			if(document.gzReportForm.elements[i].type=='checkbox'&&document.gzReportForm.elements[i].name=='right_fields')
  			{
  				document.gzReportForm.elements[i].checked=true;
  			}
  		}
  	}
  
  	function unselect()
  	{
  		for(var i=0;i<document.gzReportForm.elements.length;i++)
  		{
  			if(document.gzReportForm.elements[i].type=='checkbox'&&document.gzReportForm.elements[i].name=='right_fields')
  			{
  				document.gzReportForm.elements[i].checked=false;
  			}
  		}
  	}
  
  
  
  	function sub()
  	{
  	   var gz_moudle="${gzReportForm.gz_module}";
  		if(trim(document.gzReportForm.salaryReportName.value).length==0)
  		{
  		    if(gz_moudle=='0')
  		    {
  		    	alert("请填写薪资报表名称!");
  			}
  			else
  			{
  			   alert("请填写保险报表名称!");
  			}
  			return;
  		}
  		var num=0;
  		
  	    var vv="${gzReportForm.reportStyleID}";
  	    if(vv=='3'||vv == '13')
  	    {
  	       var f_obj = document.getElementById("f_groupItem");
  	       var s_obj = document.getElementById("s_groupItem");
  	       var f_group = "";
  	       var s_group = "";
  	       for(var i=0;i<f_obj.options.length;i++)
  	       {
  	           if(f_obj.options[i].selected)
  	           {
  	               f_group=f_obj.options[i].value;
  	               break;
  	           }
  	       }
  	       for(var j=0;j<s_obj.options.length;j++)
  	       {
  	          if(s_obj.options[j].selected)
  	          {
  	              s_group = s_obj.options[j].value;
  	              break;
  	          }
  	       }
  	       if(f_group!=""&&s_group!=''&&f_group==s_group)
  	       {
  	          alert("第一分组指标和第二分组指标不能选择同一个指标！");
  	          return;
  	       }
  	    }
  		for(var i=0;i<document.gzReportForm.elements.length;i++)
  		{	
  			if(document.gzReportForm.elements[i].type=='checkbox'&&document.gzReportForm.elements[i].name=='isPrintWithGroup'&&document.gzReportForm.elements[i].checked==false)
  			{
  					document.gzReportForm.elements[i].value="0";
  					document.gzReportForm.elements[i].checked=true;
  			}
  			if(document.gzReportForm.elements[i].type=='checkbox'&&document.gzReportForm.elements[i].name=='isGroup'&&document.gzReportForm.elements[i].checked==false)
  			{
  					document.gzReportForm.elements[i].value="0";
  					document.gzReportForm.elements[i].checked=true;
  			}
  			
  			
  			if(document.gzReportForm.elements[i].type=='checkbox'&&document.gzReportForm.elements[i].name=='right_fields'&&document.gzReportForm.elements[i].checked==true)
  				num++;
  		}
  		if(num==0)
  		{
  		    if(gz_moudle=='0')
  		    {
  		    	alert("请选择薪资项目!");
  		    }
  		    else
  		    {
  		       alert("请选择保险项目!");
  		    }
  			return;
  		}
  		
  		document.gzReportForm.action="/gz/gz_accounting/report.do?b_save=save";
  		document.gzReportForm.submit();
  	
  	}
  	
  	<% if(request.getParameter("b_save")!=null&&request.getParameter("b_save").equals("save")){ %>
  		var obj=new Array();
  		obj[0]='${gzReportForm.reportStyleID}';
  		obj[1]='${gzReportForm.salaryReportName}';
  		returnValue=obj;
	    window.close();
  	
  	<% } %>


	function setDetail()
	{
		if(document.gzReportForm.isPrintWithGroup.checked==false)
		{
			document.gzReportForm.f_groupItem.disabled=true;
			document.gzReportForm.isGroup.disabled=true;
		}
		else
		{
			document.gzReportForm.f_groupItem.disabled=false;
			document.gzReportForm.isGroup.disabled=false;
		}
	}
	
	
  </script>
  
  <body>
   <html:form action="/gz/gz_accounting/report">
<%if("hl".equals(hcmflag)){ %>
   <table align="center" width="350px;">
<%}else{%>   
   <table align="center" width="350px;" style="margin-left:-3px;margin-top:-3px">
<%} %>

   <tr>
   <td>
   <hrms:tabset name="pageset" width="350px;" height="340" type="false"> 
  		<hrms:tab name="tab1" label="基本信息" visible="true">
		 <table width="100%"  height='100%'   align="center"> 
			<tr> <td class="framestyle" valign="top"  align='center'  >
				
				 <table width="90%"  border="0" align='center' cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
			             <tr>
				               <td width="15%" height="30" >
				                  名&nbsp;称：&nbsp;
				               </td>
				               <td>
				               <input type='text' size='30' maxlength="40" name='salaryReportName' value='${gzReportForm.salaryReportName}' class="inputtext"/>
							   </td>
				         </tr>
				         <tr>
				               <td width="20%" height="30" >
				                 <html:radio property="ownerType" value="0" name='gzReportForm'>公有</html:radio>
				               </td>
				               <td align="right" style="padding-right:20px;">
				              <html:radio property="ownerType" value="1" name='gzReportForm'>私有</html:radio>
							   </td>
				         </tr>
				         <tr><td colspan='2' valign='bottom' ><br>薪资项目</td></tr>
				          <tr><td colspan='2'  valign='top' >
				          <div id="scroll_box">	 
				          	<table >
				          	<logic:iterate   id="element" name="gzReportForm" property="rightlist"  >
				          	<tr>
					          	<td>
					          	<input type='checkbox' name='right_fields'  <logic:equal  name="element" property="isSelected" value="1" >checked</logic:equal>      value='<bean:write name="element" property="itemid" filter="true"/>' />
					          	</td>
					          	<td>
					          		 <bean:write name="element" property="itemdesc" filter="true"/>
					          	</td>
				          	</tr>
				          	</logic:iterate>
				          	</table>
				          </div>
				          </td></tr>
				         
	  			 </table>
			</td></tr>
		 </table>
		</hrms:tab>
  		<logic:notEqual   name="gzReportForm" property="reportStyleID" value="1" >
  		<hrms:tab name="tab2" label="其他选项" visible="true">
		 <table width="100%"  height='100%'  align="center"> 
			<tr> <td class="framestyle" valign="top"  align='center'  >
				
				
				
				
				<logic:equal   name="gzReportForm" property="reportStyleID" value="2" >
				 <table width="95%"  border="0" align='center' cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
			             <tr>
				               <td   height="30" >
				                 &nbsp;&nbsp;<input type='checkbox' name='isPrintWithGroup'  onclick='setDetail()'   <logic:equal  name='gzReportForm' property='isPrintWithGroup' value='1'  >checked</logic:equal>  value='1' />	
								是否分组
							   </td>
				         </tr>
				         
				          <tr>
				               <td   height="30" >
					              <fieldset align="center" style="width:90%;">
		    							 <legend >分组项</legend>
				                      			<table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
				                      				<tr>
					                					<td width="20%" height="25" >
					                		           <br>  
					                		             分组指标
					                		             <html:select name="gzReportForm" property="f_groupItem" style="width:180;"   size="1">
															   <html:optionsCollection property="f_groupItemList" value="dataValue" label="dataName"/>
														</html:select>
														<Br><br><Br>
														<input type='checkbox' name='isGroup'  <logic:equal  name='gzReportForm' property='isGroup' value='1'  >checked</logic:equal>  value='1' />	
														分组分页打印
														<Br><br> &nbsp;
					                					</td>
					                				</tr>
					                				
				                      			</table>
				                 </fieldset>  
					                
					                
				               
							   </td>
				         </tr>
	  			 </table>
	  			 </logic:equal>
	  			 <logic:equal   name="gzReportForm" property="reportStyleID" value="12" >
				 <table width="95%"  border="0" align='center' cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
			             <tr>
				               <td   height="30" >
				                 &nbsp;&nbsp;<input type='checkbox' name='isPrintWithGroup'  onclick='setDetail()'   <logic:equal  name='gzReportForm' property='isPrintWithGroup' value='1'  >checked</logic:equal>  value='1' />	
								是否分组
							   </td>
				         </tr>
				         
				          <tr>
				               <td   height="30" >
					              <fieldset align="center" style="width:90%;">
		    							 <legend >分组项</legend>
				                      			<table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
				                      				<tr>
					                					<td width="20%" height="25" >
					                		           <br>  
					                		             分组指标
					                		             <html:select name="gzReportForm" property="f_groupItem" style="width:180;"   size="1">
															   <html:optionsCollection property="f_groupItemList" value="dataValue" label="dataName"/>
														</html:select>
														<Br><br><Br>
														<input type='checkbox' name='isGroup'  <logic:equal  name='gzReportForm' property='isGroup' value='1'  >checked</logic:equal>  value='1' />	
														分组分页打印
														<Br><br> &nbsp;
					                					</td>
					                				</tr>
					                				
				                      			</table>
				                 </fieldset>  
					                
					                
				               
							   </td>
				         </tr>
	  			 </table>
	  			 </logic:equal>
	  			 
	  			 <logic:equal   name="gzReportForm" property="reportStyleID" value="3" >
				 <table width="95%"  border="0" align='center' cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
			             <tr>
				               <td   height="30" >
				                 &nbsp;&nbsp;<input type='checkbox' name='isPrintWithGroup'  <logic:equal  name='gzReportForm' property='isPrintWithGroup' value='1'  >checked</logic:equal>  value='1' />	
								按第一分组词分页打印
							   </td>
				         </tr>
				         
				          <tr>
				               <td   height="30" >
					              <fieldset align="center" style="width:90%;">
		    							 <legend >分组项</legend>
				                      			<table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
				                      				<tr>
					                					<td width="20%" height="25" >
					                		           <br>  
					                		             第一分组词
					                		             <html:select name="gzReportForm" property="f_groupItem" style="width:180;"   size="1">
															   <html:optionsCollection property="f_groupItemList" value="dataValue" label="dataName"/>
														</html:select>
														<Br><Br>
														 第二分组词
					                		             <html:select name="gzReportForm" property="s_groupItem" style="width:180;"   size="1">
															   <html:optionsCollection property="s_groupItemList" value="dataValue" label="dataName"/>
														</html:select>
														<br> &nbsp;
					                					</td>
					                				</tr>
					                				
				                      			</table>
				                 </fieldset>  
					                
					                
				               
							   </td>
				         </tr>
	  			 </table>
	  			 </logic:equal>
	  			 <logic:equal   name="gzReportForm" property="reportStyleID" value="13" >
				 <table width="95%"  border="0" align='center' cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
			             <tr>
				               <td   height="30" >
				                 &nbsp;&nbsp;<input type='checkbox' name='isPrintWithGroup'  <logic:equal  name='gzReportForm' property='isPrintWithGroup' value='1'  >checked</logic:equal>  value='1' />	
								按第一分组词分页打印
							   </td>
				         </tr>
				         
				          <tr>
				               <td   height="30" >
					              <fieldset align="center" style="width:90%;">
		    							 <legend >分组项</legend>
				                      			<table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
				                      				<tr>
					                					<td width="20%" height="25" >
					                		           <br>  
					                		             第一分组词
					                		             <html:select name="gzReportForm" property="f_groupItem" style="width:180;"   size="1">
															   <html:optionsCollection property="f_groupItemList" value="dataValue" label="dataName"/>
														</html:select>
														<Br><Br>
														 第二分组词
					                		             <html:select name="gzReportForm" property="s_groupItem" style="width:180;"   size="1">
															   <html:optionsCollection property="s_groupItemList" value="dataValue" label="dataName"/>
														</html:select>
														<br> &nbsp;
					                					</td>
					                				</tr>
					                				
				                      			</table>
				                 </fieldset>  
					                
					                
				               
							   </td>
				         </tr>
	  			 </table>
	  			 </logic:equal>
	  			 
			</td></tr>
		 </table>
		</hrms:tab>
  </logic:notEqual>
  
  
   </hrms:tabset>
</td>
</tr>
<tr>
<td style="padding-top:3px" align='center'>


	　<Input type='button' value='<bean:message key="button.all.select"/>'  class="mybutton"  onclick='selectAll()' /> 
  	 <Input type='button' value='<bean:message key="button.all.reset"/>'  class="mybutton"  onclick='unselect()' /> 
     <Input type='button' value='<bean:message key="lable.tz_template.enter"/>'  class="mybutton"  onclick='sub()' /> 
  	 <Input type='button' value='<bean:message key="lable.tz_template.cancel"/>'  class="mybutton"  onclick='window.close()' /> 
   	
   	 <input type='hidden' name='reportStyleID' value='${gzReportForm.reportStyleID}'  />
   	 <input type='hidden' name='reportDetailID' value='${gzReportForm.reportDetailID}'  />
   	 
   </td>
   </tr>
   </table>
   <script languge='javascript'>
	   <logic:equal   name="gzReportForm" property="reportStyleID" value="2" >
	  	 var temp_var='${gzReportForm.isPrintWithGroup}'

	   	 if(temp_var==0)
	   	 {
	   		document.gzReportForm.f_groupItem.disabled=true;
			document.gzReportForm.isGroup.disabled=true;
	   	 }
	   </logic:equal>
   </script>
   
   
   
   </html:form>
  </body>
</html>
