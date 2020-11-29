<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<hrms:themes></hrms:themes>
<script language="JavaScript" src="/js/validate.js"></script>

<%
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null)
	{
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	   css_url="/css/css1.css";
	}
	boolean privflag = userView.hasTheFunction("9A5111"); //hasTheFunction，可查看当前用户是否有此Function_id。没有传回FALSE，有没有传回TRUE。 
%>

<script language="javascript"> 
var isSave = false;
function qxFunc()
{
	if(isSave)
	{
		var thevo=new Object();
		thevo.flag="true";
		window.returnValue=thevo;
		window.close();
	}
	else
	window.close();
}
	function save(type)
	{
		var name = document.getElementById("cname").value;
		if(ltrim(rtrim(name)) == "")
		{
		 	alert("<bean:message key='jx.paramset.info1'/>");
		 	return;
		}
		
 		var hashvo=new ParameterSet();
		hashvo.setValue("actor_type",document.getElementById('actor_type').value);
		hashvo.setValue("cname",getEncodeStr(document.getElementById('cname').value));
		hashvo.setValue("relying",getEncodeStr(document.getElementById('relying').value));
		hashvo.setValue("relation_id",getEncodeStr(document.getElementById('relation_id').value));
		if(document.genRelationForm.validflag.checked)
		hashvo.setValue("validflag","1");
		else
		hashvo.setValue("validflag","0");
		
		if(document.genRelationForm.default_line.checked)
		hashvo.setValue("default_line","1");
		else
		hashvo.setValue("default_line","0");
		
		hashvo.setValue("info","save");
		var request=new Request({method:'post',asynchronous:false,onSuccess:afterSave,functionId:'1010070031'},hashvo);	   
	}
	function afterSave(outparamters){
	    var errorinfo=outparamters.getValue("errorinfo")
	    if(errorinfo.length>0){
	    	alert(errorinfo);
	    	return;
	    }
		var thevo=new Object();
		thevo.flag="true";
		if(parent.window){
			parent.window.returnValue=thevo;
			parent.window.close();
		}else{
			window.returnValue=thevo;
			window.close();
		}
		
		//window.location.href="/general/relation/relationmaintence.do?b_query=link";
	}
	function changeRelyingList(){
		var hashvo=new ParameterSet();
		hashvo.setValue("actor_type",document.getElementById('actor_type').value);
		var request=new Request({method:'post',asynchronous:false,onSuccess:changeRelyingListOk,functionId:'1010070044'},hashvo);	   
	}
	function changeRelyingListOk(outparamters){
		var relyingList=outparamters.getValue("relyingList");
		AjaxBind.bind(genRelationForm.relying,relyingList);
	}
	function init(){
		if(document.getElementById('relation_id').value!=""){
			document.getElementById('actor_type').disabled=true;
		}
		if(document.getElementById('default_line').checked){
		   document.getElementById('relying').disabled=true;
		}else{
		   document.getElementById('relying').disabled=false;
		}
	}
	function disableRelyingList(){
		if(document.getElementById('default_line').checked){
		   document.getElementById('relying').disabled=true;
		}else{
		   document.getElementById('relying').disabled=false;
		}
		var hashvo=new ParameterSet();
		hashvo.setValue("actor_type",document.getElementById('actor_type').value);
		hashvo.setValue("default_line",document.getElementById('default_line').value);
		hashvo.setValue("relation_id",document.getElementById('relation_id').value);
		var request=new Request({method:'post',asynchronous:false,onSuccess:disableRelyingListOk,functionId:'1010070044'},hashvo);	  
	}
	function disableRelyingListOk(outparamters){
		var relyingList=outparamters.getValue("relyingList");
		AjaxBind.bind(genRelationForm.relying,relyingList);
	}	
</script>
<body onload="init();">
<html:form action="/general/relation/relationmaintence">
	<table border="0" cellspacing="0" align="center" cellpadding="2"  class="ListTable1">
			<br>
			<br>
			<thead>
            <tr> 
            <logic:equal name="genRelationForm" property="checkrelationvo.string(relation_id)" value="">
              <td align="left" colspan="2" class="TableRow">新增审批关系&nbsp; </td>
            </logic:equal>
            <logic:notEqual name="genRelationForm" property="checkrelationvo.string(relation_id)"  value="">
              <td align="left" colspan="2" class="TableRow">修改审批关系&nbsp; </td>
            </logic:notEqual>  
            </tr>
          </thead>
			<tr class="trShallow">
				<td align="left" class="RecordRow" nowrap>
					&nbsp; 关系名称
				</td>
				<td align="left" class="RecordRow" nowrap >
					<html:text name="genRelationForm" styleId="cname" property="checkrelationvo.string(cname)" styleClass="TEXT4" />
				</td>  
			</tr> 
		
			<tr class="trDeep">
				<td align="left" class="RecordRow" nowrap >
					&nbsp; 主汇报关系
				</td>
				<td align="left" class="RecordRow" nowrap >
					 <html:checkbox name="genRelationForm" property="checkrelationvo.string(default_line)" styleId="default_line" onclick="disableRelyingList();" value="1"></html:checkbox>
			    </td>
			 </tr>
		
			<tr class="trDeep">
				<td align="left" class="RecordRow" nowrap >
					&nbsp; 依赖关系
				</td>
				<td align="left" class="RecordRow" nowrap >		
				<hrms:optioncollection name="genRelationForm" property="relyingList" collection="list" />
					  <html:select name="genRelationForm" property="checkrelationvo.string(relying)" size="1" styleId="relying" style="width:150px;">
						  <html:options collection="list"  property="dataValue" labelProperty="dataName"/>
					  </html:select>
			    </td>
			 </tr>
		
			<tr class="trDeep">
				<td align="right" class="RecordRow" nowrap >
					&nbsp; 审批对象类型
				</td>
				<td align="left" class="RecordRow" nowrap >
							   				
					<html:select name="genRelationForm" property="checkrelationvo.string(actor_type)" styleId="actor_type" onchange="changeRelyingList();" size="1">			
						<html:option value="1">
							自助用户
						</html:option>	
						<html:option value="4">
							业务用户
						</html:option>	
						
					</html:select>
			 </td>
			 </tr>
			<tr class="trShallow">
				<td align="left" class="RecordRow" nowrap >
          		<html:hidden name="genRelationForm" property="checkrelationvo.string(relation_id)" styleId="relation_id" />
          		<html:hidden name="genRelationForm" property="checkrelationvo.string(validflag)" styleId="validflag1" />
          		<html:hidden name="genRelationForm" property="checkrelationvo.string(default_line)" styleId="default_line1" />
				</td>
				<td align="left" class="RecordRow" nowrap >
				<%if(privflag){
				%>
					<html:checkbox  name="genRelationForm" styleId="validflag" property="checkrelationvo.string(validflag)" value="1" ></html:checkbox>有效
				<%
				}else{
				%>
					<html:checkbox  name="genRelationForm" styleId="validflag" property="checkrelationvo.string(validflag)" value="1" disabled="true"></html:checkbox>有效
				<%
				} 
				%>
				</td>
				
			</tr>
			</table>
	<table border="0" cellspacing="0" align="center" cellpadding="2">		
			<tr>
				<td align="center">
					<input type="button" class="mybutton" value="<bean:message key='button.ok' />" onClick="save('save');" />
						<input type="button" class="mybutton" value="<bean:message key='button.cancel'/>" onClick="window.close();">  
					
				</td>
			</tr>
		</table>
</html:form>
</body>