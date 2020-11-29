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
<html>
   <%
   TemplateSetPropertyForm templateSetPropertyForm=(TemplateSetPropertyForm)session.getAttribute("templateSetPropertyForm");
   ArrayList fieldList=templateSetPropertyForm.getFieldList();
   String royalty_relation_fields=","+templateSetPropertyForm.getRoyalty_relation_fields()+",";
   String salaryid=templateSetPropertyForm.getSalaryid();
   String strExpression=templateSetPropertyForm.getStrExpression();
   
    %>
  <head>
   <script language='javascript' >
    function hideDataRange()
    {
    	var hasData = document.getElementsByName("royalty_setid")[0].value;
    	if(hasData==null || hasData.length==0)
    		document.getElementById("dataRange").style.display="none";
    }
   	function relationField()
   	{
   	//	if(trim(document.getElementsByName("royalty_setid")[0].value).length>0)
   		{
   			var setid=document.getElementsByName("royalty_setid")[0].value;
   			var hashvo=new ParameterSet();
		    hashvo.setValue("royalty_setid",setid); 
		    hashvo.setValue("salaryid","<%=salaryid%>");
			var request=new Request({method:'post',asynchronous:false,onSuccess:resultRelationField,functionId:'3020030022'},hashvo);	
   		}
   	}
   	
   
  
   function resultRelationField(outparamters){
   
  		var fielditemlist=outparamters.getValue("fieldList");
  		var dateitemlist=outparamters.getValue("dateList");
  		var hideFlag = outparamters.getValue("hideFlag");
	//	AjaxBind.bind(templateSetPropertyForm.royalty_relation_fields,fielditemlist);
		document.getElementById("relationItem").innerHTML='';
		
		var str="<table width='100%' border='0' cellspacing='0'  align='center' cellpadding='0' class='ListTable'>" 
		for(var i=0;i<fielditemlist.length;i++)
		{
			var colarr=fielditemlist[i];
		//	 colarr.dataValue  colarr.dataName);
			str+="<tr><td><input type='checkbox' name='royalty_relation_fields'   value='"+colarr.dataValue+"' /></td>";
    		str+="<td>"+colarr.dataName+"</td></tr>"; 
		}
		str+="</table>";
		document.getElementById("relationItem").innerHTML=str;
		AjaxBind.bind(templateSetPropertyForm.royalty_date,dateitemlist);
		
		if(hideFlag==1){
			document.getElementById("dataRange").style.display="none";
		}else if(hideFlag==0){
			document.getElementById("dataRange").style.display="";
		}
  }
   	
   	
   	function sub()
   	{
   		var royalty_setid=document.getElementsByName("royalty_setid")[0].value;
   		var royalty_date=document.getElementsByName("royalty_date")[0].value;
   		var royalty_period=document.getElementsByName("royalty_period")[0].value;
   		
   		var royalty_relation_fields="";
   		var obj=document.getElementsByName("royalty_relation_fields");
   		for(var i=0;i<obj.length;i++)
   		{
   			if(obj[i].checked)
   				royalty_relation_fields+=","+obj[i].value;
   		}
   		if(trim(royalty_setid).length==0)
   		{
   			alert("请选择提成数据子集!");
   			return;
   		} 
   		if(trim(royalty_date).length==0)
   		{
   			alert("请选择计提日期指标!");
   			return;
   		}  
   		if(trim(royalty_relation_fields).length==0)
   		{
   			alert("请选择关联指标!");
   			return;
   		} 
   		var result=new Array();
  		result[0]=royalty_setid;
	  	result[1]=royalty_date;
	  	result[2]=royalty_period;
	  	result[3]=royalty_relation_fields.substring(1);
	  	result[4]=document.getElementsByName("expression_str")[0].value;
  		returnValue=result;
	    window.close();
   	}
   	
   	
   	
   		function simpleCondition()
	  	{
	  		var setid=""; 
	  		var setdesc=""; 
	  		for(var n=0;n<document.getElementsByName("royalty_setid")[0].options.length;n++)
	  		{
	  			if(document.getElementsByName("royalty_setid")[0].options[n].selected)
	  			{
	  				setid=document.getElementsByName("royalty_setid")[0].options[n].value;
	  				setdesc=document.getElementsByName("royalty_setid")[0].options[n].text;
	  			}
	  		}
	  		if(trim(setid).length==0)
	  		{
	  			alert("请选择提成数据子集!");
	  			return;
	  		} 
	  		var info,queryType,dbPre;
		    info="1";
		    dbPre="Usr";
	        queryType="1";
	        var expression="<%=strExpression%>"; 
	        // var expression=getEncodeStr(express);
	        var formurl="/system/busimaintence/showbusifield.do?b_viewformula=links&itemsetid="+setid+"&fielditemid=&formula="+expression+"&itemtype=";
			var strExpression= window.showModalDialog(formurl, false, 
        "dialogWidth:450px; dialogHeight:450px;resizable:no;center:yes;scroll:yes;status:no");
		   
	        
	    //    var strExpression =generalExpressionDialog(info,dbPre,queryType,express,setid,setdesc);
	    
		    if(strExpression!='undefined'){
		        if(strExpression)
		        {
		        	document.getElementsByName("expression_str")[0].value=strExpression;
		        }
		        else
		        	document.getElementsByName("expression_str")[0].value="";
		  	}
	  	}
   	
   		function clearCondition()
   		{
   			document.getElementsByName("expression_str")[0].value="";
   		}
   		
   		
   		
   		
   		function setcond(){
			var salaryid=document.getElementById("salaryid").value;
			var item=document.getElementById("item").value;
		  	var hashvo=new ParameterSet();
			hashvo.setValue("salaryid",salaryid);
			hashvo.setValue("item",item);
			var In_paramters="flag=1"; 	
			var request=new Request({method:'post',asynchronous:false,
				parameters:In_paramters,onSuccess:getcond,functionId:'3020060019'},hashvo);				
		}
		
		function getcond(outparamters){
			var conditions = outparamters.getValue("conditions");
			var salaryid=document.getElementById("salaryid").value;
			var cond = condiTions(conditions,salaryid);
			if(cond!=null){
				savecond(cond);
			}
		}

		/**
		 * 判断当前浏览器是否为ie6
		 * 返回boolean 可直接用于判断 
		 * @returns {Boolean}
		 */
		function isIE6() 
		{ 
			if(navigator.appName == "Microsoft Internet Explorer") 
			{
				if(navigator.userAgent.indexOf("MSIE 6.0")>0) 
				{ 
					return true;
				}else{
					return false;
				}
			}else{
				return false;
			}
		}
			
		//如果是ie6
		function ie6Style(){
			if(isIE6()){
				document.getElementById('tableId').style.cssText="margin-top:0px;margin-left=-3px;";
			}
		}
   </script>
  </head>
   
  <body>
 <html:form action="/gz/templateset/gz_templateProperty">
  

    <table width='490px;' id="tableId" style="margin-top:-3px;margin-left=-1px;"><tr><td>
    
     <fieldset align="center" style="width:100%px;">
    							 <legend>提成薪资</legend>
    
    <table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">     
    	<tr><td width='30%' valign='top'  align=left ><br> &nbsp;&nbsp;&nbsp;提成数据子集</td>
    	<td align='left'  valign='top'  nowrap><br> 
    	
    	<html:select name="templateSetPropertyForm" property="royalty_setid"   onchange="relationField()"   size="1">
			<html:optionsCollection property="setList" value="dataValue" label="dataName"/>
		</html:select>  
    	<span id="dataRange" >
    		  &nbsp;<Input type='button' value='...'  class="mybutton"  onclick='simpleCondition()' />&nbsp;(&nbsp;<bean:message key="gz.templateset.cond" />&nbsp;)&nbsp; 
    	</span>
    	</td>
    	</tr>
    
    	<tr><td align='left' >
    	<br><br> 
    	 &nbsp;&nbsp;&nbsp;计划日期指标</td>
    	<td align='left' ><br><br> 
    	<html:select name="templateSetPropertyForm" property="royalty_date"     size="1">
								                              <html:optionsCollection property="dateList" value="dataValue" label="dataName"/>
								        		</html:select>  
    	
    	</td></tr>
    	
    	<tr><td align='left' >
    	<br><br> 
    	 &nbsp;&nbsp;&nbsp;周期</td>
    	<td align='left'><br><br> <html:select name="templateSetPropertyForm" property="royalty_period"     size="1">
								                              <html:optionsCollection property="periodList" value="dataValue" label="dataName"/>
								        		</html:select>  </td></tr>
    	
    	<tr><td align='left'  valign='top'>
    	<br><br> 
    	 &nbsp;&nbsp;&nbsp;关联指标 <br><br> <br><br> </td>
    	<td  align='left' valign='top' ><br><br> 
    	
    	  <div style="width:200px;height: 150px;overflow: auto; border:1px solid;" class="fixedtab"  id='relationItem' >
    		<table width='100%' border='0' cellspacing='0'  align='center' cellpadding='0' class='ListTable'>
    		<% for(int i=0;i<fieldList.size();i++){
    				CommonData cd=(CommonData)fieldList.get(i);
    				String value="";
    				if(royalty_relation_fields.indexOf(","+cd.getDataValue()+",")!=-1)
    					value="checked";
    				out.print("<tr><td><input type='checkbox' name='royalty_relation_fields' "+value+" value='"+cd.getDataValue()+"' /></td>" );
    				out.print("<td>"+cd.getDataName()+"</td></tr>");
    		} %>
    		</table>
    	  <div>
    	<!-- 
    	<select name="royalty_relation_fields"  multiple  size="10" >
    		
    		  for(int i=0;i<fieldList.size();i++){
    				CommonData cd=(CommonData)fieldList.get(i);
    				String value="";
    				if(royalty_relation_fields.indexOf(","+cd.getDataValue()+",")!=-1)
    					value="selected";
    				out.print("<option value='"+cd.getDataValue()+"' "+value+"   >"+cd.getDataName()+"</option>");
    		}  
    		<option value="">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</option>
    	</select>
    	 -->
    	
    	<br><br> 
    	 </td></tr>
    	 <!-- 
    	 <tr><td align='left' >
    	<br><br> 
    	 &nbsp;&nbsp;&nbsp;条件: </td>
    	<td align='left' ><br><br> 
    	 &nbsp;<Input type='button' value='...'  class="mybutton"  onclick='simpleCondition()' />&nbsp;&nbsp; 
			                		            <Input type='button' value='<bean:message key="button.clearup"/>条件'  class="mybutton"  onclick='clearCondition();' />  
    	
    	</td></tr>
    	  -->
    	 <tr><td colspan='2' >
    	  &nbsp;&nbsp;
    	 </td></tr>
    
    </table>
    
    </fieldset>
    <input type='hidden' name='expression_str'  value='<%=strExpression%>' />
    <table width="95%" align="center"><tr><td align="center">
     <Input type='button' value='<bean:message key="lable.tz_template.enter"/>'  class="mybutton"  onclick='sub()' /> 
  	 <Input type='button' value='<bean:message key="lable.tz_template.cancel"/>'  class="mybutton"  onclick='window.close()' /> 
  	 </td></tr></table>
    
    
	</td></tr></table>
</html:form>
  </body>
</html>
<script language='javascript' >
    hideDataRange();
    ie6Style();
</script>