<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script LANGUAGE=javascript src="/js/function.js"></script>

<html:form action="/performance/implement/kh_object/dynaitem">
	<table border="0" cellspacing="0" align="center" cellpadding="5" style="margin-top:-6px;">
			<tr>
					<td align="center" nowrap >
						<fieldset align="center" style="width:300;">
							<legend>
									<bean:message key='jx.implement.selTaskRule' />
							</legend>
							<table border="0" cellspacing="0" align="center" cellpadding="2">
								<tr height="35">
									<td  align="right" nowrap>
											<bean:message key='jx.implement.minTaskCount' />
									</td>
									<td  align="left" nowrap>
										<html:text name="implementForm" property="minTaskCount" onblur='checkIntValue(this)' onkeypress="event.returnValue=IsDigit2();" styleClass="inputtext"></html:text>
									</td>
								</tr>
								<tr height="35">
									<td  align="right" nowrap>
										<bean:message key='jx.implement.maxTaskCount' />
									</td>
									<td  align="left" nowrap>
										<html:text name="implementForm" property="maxTaskCount" onblur='checkIntValue(this)' onkeypress="event.returnValue=IsDigit2();" styleClass="inputtext"></html:text>
									</td>
								</tr>
								<!-- dml 2011年9月13日16:59:18调整大小顺 -->
								<tr height="35">
									<td  align="right" nowrap>
										<logic:equal name="implementForm" property="templateStatus" value="0">
											<bean:message key='jx.implement.minScore' />
										</logic:equal>
										<logic:equal name="implementForm" property="templateStatus" value="1">
											<bean:message key='jx.implement.minRank' />
										</logic:equal>
									</td>
									<td  align="left" nowrap>
										<html:text name="implementForm" property="minScore" onblur='checkValue(this)' onkeypress="event.returnValue=IsDigit1(this);" styleClass="inputtext"></html:text>
									</td>
								</tr>
								<tr height="35">
									<td  align="right" nowrap>
										<logic:equal name="implementForm" property="templateStatus" value="0">
											<bean:message key='jx.implement.maxScore' />
										</logic:equal>
										<logic:equal name="implementForm" property="templateStatus" value="1">
											<bean:message key='jx.implement.maxRank' />
										</logic:equal>
									</td>
									<td  align="left" nowrap>
										<html:text name="implementForm" property="maxScore" onblur='checkValue(this)' onkeypress="event.returnValue=IsDigit1(this);" styleClass="inputtext"></html:text>
									</td>
								</tr>
								<logic:equal value="true" property="canshow" name="implementForm">
								<tr height="35">
									<td  align="right" nowrap>
										任务得分范围
									</td>
									<td  align="left" nowrap>
										<logic:equal value="0" name="implementForm" property="flag">
											<input type="text" value="${implementForm.scope}" name="scope" onblur='checkValue(this)' onkeypress="event.returnValue=IsDigit3(this);" style="width:40px" class="inputtext" disabled>
											~
											<input type="text" value="${implementForm.to_scope}" name="to_scope" onblur='checkValue(this)' onkeypress="event.returnValue=IsDigit3(this);" style="width:40px" class="inputtext" disabled>
										</logic:equal>
										<logic:equal name="implementForm" property="flag" value="1">
											<input type="text" value="${implementForm.scope}" name="scope"  style="width:40px" onblur='checkValue(this)' onkeypress="event.returnValue=IsDigit3(this);"  class="inputtext">
											~
											<input type="text" value="${implementForm.to_scope}" name="to_scope"  style="width:40px" onblur='checkValue(this)' onkeypress="event.returnValue=IsDigit3(this);"  class="inputtext">
										</logic:equal>
										<logic:equal name="implementForm" property="flag" value="0">
											<input type="checkbox" name="flag" value='0' onclick="setable(this);" id="id_flag"/>加扣分
										</logic:equal>
										<logic:equal name="implementForm" property="flag" value="1">
											<input type="checkbox" name="flag" value='1' onclick="setable(this);" id="id_flag" checked/>加扣分
										</logic:equal>
									</td>
								</tr>
								</logic:equal>
							</table>
						</fieldset>
					</td>
			</tr>
			</table>			
			
			<table width="100%">
				<tr>
					<td align="center">
						
						<input type="button" class="mybutton"
							value="<bean:message key='button.ok' />" onClick="saveRule();" />
					
						<input type="button" class="mybutton"
							value="<bean:message key='button.cancel' />"
							onClick="parent.window.close();">
					</td>
				</tr>
			</table>
			<input type="hidden" name="flag" value="">
			<input type="hidden" name="scope" value="">
			<input type="hidden" name="to_scope" value="">
</html:form>
<script language="javascript">

	function saveRule()
	{
		if(document.getElementById("id_flag")!=null)
		{
			var flag=document.getElementById("id_flag");
			if(flag.checked)
			{
			   var scope=document.getElementsByName("scope")[0].value;
			   var to_scope=document.getElementsByName("to_scope")[0].value;
			   if(scope=='-'||to_scope=='-')
			   {
			      alert("加扣分项必须输入数字！");
			      return;
			   }
			   if(parseFloat(scope)>parseFloat(to_scope))
			   {
			      alert("加扣分项，左边的分值不能大于右边的分值！");
			      return;
			   }
			}
		}
		implementForm.action="/performance/implement/kh_object/dynaitem.do?b_saveRule=link&item_id=${param.item_id}";
		implementForm.submit();
	}		
			
	function IsDigit1(obj) 
	{
		if((event.keyCode >= 46) && (event.keyCode <= 57) && event.keyCode!=47)
		{
			var values=obj.value;
			if((event.keyCode == 46) && (values.indexOf(".")!=-1))//有两个.
				return false;
			if((event.keyCode == 46) && (values.length==0))//首位是.
				return false;	
			return true;
		}
			return false;	
	}
	function IsDigit2() 
	{
		return ((event.keyCode >= 47) && (event.keyCode <= 57)); 
	}
	function IsDigit3(obj) 
	{
	    if(event.keyCode==45 && obj.value.indexOf("-")==-1)
	      	return true;
		if((event.keyCode >= 46) && (event.keyCode <= 57) && event.keyCode!=47)
		{
			var values=obj.value;
			if((event.keyCode == 46) && (values.indexOf(".")!=-1))//有两个.
				return false;
			if((event.keyCode == 46) && (values.length==0))//首位是.
				return false;	
			return true;
		}
		return false;	
	}
	
	//检验数字类型
	function checkValue(obj)
	{
	  	if(obj.value.length>0)
	  	{
	  		if(!checkIsNum2(obj.value))
	  		{
	  			alert('请输入数值！');
	  			obj.value='';
	  			obj.focus();
	  		}
	  	} 
	}
	//检验正数类型
	function checkNumValue(obj)
	{
	  	if(obj.value.length>0)
	  	{
	  		if(!checkIsNum(obj.value))
	  		{
	  			alert('请输入正数！');
	  			obj.value='';
	  			obj.focus();
	  		}
	  	} 
	}
	//检查是否是整数类型
	function checkIntValue(obj)
	{
	  	if(obj.value.length>0)
	  	{
	  		if(!checkIsIntNum(obj.value))
	  		{
	  			alert('请输入整数！');
	  			obj.value='';
	  			obj.focus();
	  		}
	  	} 
	}
	
	<%if(request.getParameter("b_saveRule")!=null){%>
		var thevo=new Object();
		thevo.flag="true";
		thevo.minTaskCount='${implementForm.minTaskCount}';
		thevo.maxTaskCount='${implementForm.maxTaskCount}';
		thevo.maxScore='${implementForm.maxScore}';
		thevo.minScore='${implementForm.minScore}';
		thevo.cflag='${implementForm.flag}';
		thevo.scope='${implementForm.scope}';
		thevo.to_scope='${implementForm.to_scope}';
		thevo.canshow='${implementForm.canshow}';
		
		if(window.showModalDialog){
			parent.window.returnValue=thevo;
		}else {
			parent.window.opener.editRule_ok(thevo);
		}	
		parent.window.close();
	<%}%>
	function setable(obj)
	{
		var value=obj.value;
		var scope=document.getElementsByName("scope")[0];
		var to_scope=document.getElementsByName("to_scope")[0];
		if(obj.checked==false)
		{
			scope.disabled=true;
			to_scope.disabled=true;
			obj.value=0;
		}else
		{
			scope.disabled=false;
			to_scope.disabled=false;
			obj.value=1;
		}
	}
</script>