<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.performance.kh_plan.ExamPlanForm,
				 org.apache.commons.beanutils.LazyDynaBean,				 
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant" %>
<% 
	ExamPlanForm examPlanForm=(ExamPlanForm)session.getAttribute("examPlanForm");	
	int dataSize = examPlanForm.getExtproList().size();

%>				 

<style>
.myFixedDiv 
{ 
	overflow:auto; 
	height:expression(document.body.clientHeight-200);
	width:100%; 
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 0pt solid ; 
}
</style>
<hrms:themes />
<script type="text/javascript">

	if('${param.oper}'=='close')
		parent.window.close();
	
	//全选
	function selectAll()
	{
		var records=document.getElementsByName("checkAppraise");
 		var allselect=document.getElementById("checkAll");
 		if(records)
 		{
     		for(var i=0;i<records.length;i++)
     		{
        		if(allselect.checked)       	
           			records[i].checked=true;        	
        		else       	
            		records[i].checked=false;       	
     		}
 		}
	}
	function add()
	{
		var str = $('addDescription').value;
		if(str==null || trim(str).length<=0)
		{
			alert('请填写新增内容！');
      		return;
		}
		if(trim(str).length>200)
		{
			alert('您填写的内容过长，请控制在200字以内！');
      		return;
		}

		examPlanForm.action="/performance/kh_plan/proAppraise.do?b_add=link&addDescription="+$URL.encode(getEncodeStr(str));
		examPlanForm.submit();	  
	}
	function del()
	{
		var records = document.getElementsByName("checkAppraise");
    	var num = 0;
    	var ids = "";
    	if(records)
    	{
      		for(var i=0;i<records.length;i++)
      		{
         		if(records[i].checked)
         		{
            		num++;
            		ids+="@"+records[i].value;
         		}
      		}
   		}
   		if(num==0)
   		{
      		alert('请选择记录！');
      		return;
   		}
   		if(confirm("确认删除所选记录吗？"))
    	{
   			examPlanForm.action="/performance/kh_plan/proAppraise.do?b_delete=link&ids="+ids.substring(1);
   			examPlanForm.submit();
   		}
	}	
	// 移动记录
	function moveRecord(plan_id,num,move)
	{
		var hashvo = new ParameterSet();
		hashvo.setValue("plan_id",plan_id);
		hashvo.setValue("seq",num);
		hashvo.setValue("move",move);		
		hashvo.setValue("opt","36");
		var request=new Request({method:'post',asynchronous:false,onSuccess:moveRecordResult,functionId:'9023000003'},hashvo);
	}
	function moveRecordResult(outparamters)
	{		
		window.location='/performance/kh_plan/proAppraise.do?b_query=link';
	}
</script>
<html:form action="/performance/kh_plan/proAppraise">
	<table width="95%" align="center">		
		<tr>
		<td width="100%">
	<div class="myFixedDiv common_border_color">
		<table width="100%" border="0" cellspacing="0" align="center"
			cellpadding="0" class="ListTable">
			<thead>
				<tr>
					<td align="center" width="25" class="TableRow_2rows" style="border-left:0px;" nowrap >
						<input type="checkbox" name="check" id='checkAll' value="1" onclick='selectAll();' />
					</td>
					<td align="center" class="TableRow_2rows" nowrap >
		 			    描述性评议项
	    			</td>   
					
					<td align="center" width="45" class="TableRow_2rows" style="border-right:0px;" nowrap >
						
					</td>
				</tr>
			</thead>
			<%int i=0; %>
			<logic:iterate id="element" name="examPlanForm" property="extproList">
				<%
					if (i % 2 == 0)
					{
			%>
				<tr class="trShallow">
					<%
						} else
						{
				%>
				
				<tr class="trDeep">
					<%
						}
						i++;
				%>
					<td align="center" class="RecordRow_right common_border_color" nowrap>
						<input type="checkbox" id='proappId' name="checkAppraise" value="<bean:write name="element" property="id" filter="true"/>"/>	
					</td>
					<td align="left" class="RecordRow">
						<bean:write name="element" property="value" filter="true" />&nbsp;
					</td>
					<td align="center" class="RecordRow_left common_border_color" nowrap>
						<logic:notEqual name="element" property="count" value="1">
							<a href="javaScript:moveRecord('<bean:write name="element" property="plan_id" filter="true"/>','<bean:write name="element" property="num" filter="true"/>','up')">
							<img src="../../images/up01.gif" width="12" height="17" border=0></a> 
						</logic:notEqual>
						<logic:equal name="element" property="count" value="1">																		
							&nbsp;&nbsp;&nbsp;
						</logic:equal>
						<%	
							LazyDynaBean a_bean=(LazyDynaBean)pageContext.getAttribute("element");									
							String count = null==(String)a_bean.get("count")?"0":(String)a_bean.get("count");
							if(Integer.parseInt(count)==dataSize){
						%>
							&nbsp;&nbsp;&nbsp;
						<% }else{%>
						
						<a href="javaScript:moveRecord('<bean:write name="element" property="plan_id" filter="true"/>','<bean:write name="element" property="num" filter="true"/>','down')">
						<img src="../../images/down01.gif" width="12" height="17" border=0></a> 
						<% }%>
					</td>
				</tr>
			</logic:iterate>
		</table>
	</div>
		</td>
		</tr>
		<tr>
			<td>
				<table border="0" cellspacing="0" width="100%" cellpadding="0" align="center">
					<tr class="trDeep1">
						<td>
							<html:textarea name="examPlanForm" styleId="addDescription"
									property="addDescription" cols="90" rows="6">
							</html:textarea>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>	
	<table width="60%" align="center">
		<tr>
			<td align="center">
				<input type='button' id="addInsert" class="mybutton" property="b_add"
					onclick="add()"
					value='<bean:message key="button.insert"/>' />
		
				<input type='button' id="delete" class="mybutton" property="b_delete"
					onclick='del()'
					value='<bean:message key="button.delete"/>' />
								
				<input type='button' class="mybutton" property="b_cancel"
					onclick='parent.window.close();' value='<bean:message key="button.close"/>' />
			</td>
		</tr>
	</table>
</html:form>
<script type="text/javascript">

var status = '${examPlanForm.status}';
if(status!='0' && status!='5' && document.getElementById('addInsert')!=null)
{
   document.getElementById('addInsert').disabled=true;
   document.getElementById('delete').disabled=true;
}

</script>