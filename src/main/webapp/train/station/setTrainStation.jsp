<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient"%>
<%
    EncryptLockClient lockclient = (EncryptLockClient)session.getServletContext().getAttribute("lock");
    //zxj 20171207 是否有在线学习模块
    boolean eLearning = lockclient.isHaveBM(39);
%>
<script language="javascript">
<!--
function loademp(){
	var hashvo=new ParameterSet(); 
	hashvo.setValue("emp_setid",document.getElementById("emp_setid").value);
	hashvo.setValue("flag","1"); 
   	var request=new Request({method:'post',onSuccess:showSelectOk1,functionId:'2020020401'},hashvo);
}
function showSelectOk1(outparamters){
	if(outparamters){
		var csp=document.getElementById("emp_coursecloumn");
		var psp=document.getElementById("emp_passcloumn");
		csp.options.length = 0;
		var value1=outparamters.getValue("value1");
		var text1=outparamters.getValue("text1");
		if(value1!=null&&value1.length>0){
			var val1s=value1.split(",");
			var txs=text1.split(",");
			for(var i=0;i<val1s.length;i++){
				var varItem = new Option(txs[i],val1s[i]);
				csp.options.add(varItem);
				if(val1s[i]=="${trainStationForm.emp_coursecloumn}")
					csp.options[i].selected=true;
			}
		}		
		
		passtag.style.display='';
        var vo=document.getElementById("passitems");
        vo.innerHTML="";	
      	passtag.style.display='none';
	      
		psp.options.length = 0;
		if(value1!=null&&value1.length>0){
			var val1s=value1.split(",");
			var txs=text1.split(",");
			for(var i=0;i<val1s.length;i++){
				var varItem = new Option(txs[i],val1s[i]);
				psp.options.add(varItem);
				if(val1s[i]=="${trainStationForm.emp_pssscloumn}"){
					psp.options[i].selected=true;
					loadpassvalue();
				}
			}
		}
	}
}
function loadpost(){
		var hashvo=new ParameterSet(); 
		hashvo.setValue("post_setid",document.getElementById("post_setid").value);
		hashvo.setValue("flag","2"); 
    	var request=new Request({method:'post',onSuccess:showSelectOk2,functionId:'2020020401'},hashvo);
}
function showSelectOk2(outparamters){
	if(outparamters){
		var csp=document.getElementById("post_coursecloumn");
		csp.options.length = 0;
		var value1=outparamters.getValue("value2");
		var text1=outparamters.getValue("text2");
		if(value1!=null&&value1.length>0){
			var val1s=value1.split(",");
			var txs=text1.split(",");
			for(var i=0;i<val1s.length;i++){
				var varItem = new Option(txs[i],val1s[i]);
				csp.options.add(varItem);
				if(val1s[i]=="${trainStationForm.post_coursecloumn}")
					csp.options[i].selected=true;
			}
		}
	}
}
function loadpassvalue()
{
    var pass_column="${trainStationForm.emp_pssscloumn}";
    var pass_itemid=""    
    var csp=document.getElementById("emp_passcloumn");
    var checkedstr="";
    for(var i=0;i<csp.length;i++)
    {
        if(csp.options[i].selected)
        {
            pass_itemid=csp.options[i].value;           
            if(csp.options[i].value==pass_column)
              checkedstr="1";            
            break;
        }        
    }
    var hashvo=new ParameterSet(); 
	hashvo.setValue("pass_column",pass_itemid);
	hashvo.setValue("flag","3"); 
	hashvo.setValue("emp_passvalues","${trainStationForm.emp_passvalues}");
	hashvo.setValue("checked",checkedstr); 
    var request=new Request({method:'post',onSuccess:showSelectOk3,functionId:'2020020401'},hashvo);
}
function showSelectOk3(outparamters){
	var passtag = document.getElementById("passtag");
	if(outparamters){
	   var text3=outparamters.getValue("text3");
	   if(text3!=null/*&&text3.length>0*/)
	   {
	   	  passtag.style.display='';
	      text3=getDecodeStr(text3);
	      var vo=document.getElementById("passitems");
	      vo.innerHTML=text3;	
	      if(text3.length<1)
	      	  passtag.style.display='none';
	   }
	}
}
function saveset(){
	var ids="";
	var chks=document.getElementsByName("nbase");
	for(var i=0;i<chks.length;i++){
		if(chks[i].type=="checkbox"&&chks[i].checked==true)
			ids+=chks[i].value+",";
	}
	var hashvo=new ParameterSet(); 
	hashvo.setValue("nbase",ids);
	hashvo.setValue("emp_setid",document.getElementById("emp_setid").value);
	hashvo.setValue("emp_coursecloumn",document.getElementById("emp_coursecloumn").value);
	hashvo.setValue("post_setid",document.getElementById("post_setid").value);
	hashvo.setValue("post_coursecloumn",document.getElementById("post_coursecloumn").value);
	hashvo.setValue("flag","0"); 
	hashvo.setValue("emp_passcloumn",document.getElementById("emp_passcloumn").value);	
	var emp_passvalues="";
	var passvalues=document.getElementsByName("passitem");	
	for(var i=0;i<passvalues.length;i++){	
		if(passvalues[i].type=="checkbox"&&passvalues[i].checked==true)
		{
		   
		   emp_passvalues+=passvalues[i].value+",";
		}
			
	}
	hashvo.setValue("emp_passvalues",emp_passvalues);
	
   	var request=new Request({method:'post',onSuccess:saveOk,functionId:'2020020401'},hashvo);
}
function saveOk(outparamters){
	if(outparamters){
		var mess=outparamters.getValue("mess");
		if(mess=="ok")
			alert("岗位培训指标设置成功!");
		else if(mess=="inequality")
			alert('< <bean:message key="train.b_plan.emp.coursecloumn"/> > 和  < <bean:message key="train.b_plan.post.coursecloumn"/> >'
					+" 所选的对应指标必须关联同一代码类，请重新选择！");
		else
			alert("保存失败！");
	}else{
		alert("保存失败！");
	}
}
-->
</script>
<center>
<html:form action="/train/station/setTrainStation">
	<fieldset style="height: 120px;width: 510px;">
		<legend><bean:message key="report.nbase"/></legend>
		<div style="height: 105px;width: 510px;overflow: auto;text-align: left;vertical-align: top;padding-left: 50px;">
			<logic:iterate id="dbs" name="trainStationForm" property="nbase_list" indexId="i">
				<logic:notEqual value="0" name="i">
					<br/>
				</logic:notEqual>
				<%String check=""; %>
				<logic:iterate id="ss" name="trainStationForm" property="sel_nbase">
					<logic:equal value="${ss}" name="dbs" property="dataName">
						<%check="checked"; %>
					</logic:equal>
				</logic:iterate>
				<input type="checkbox" name="nbase" style="vertical-align: middle; " value='<bean:write name="dbs" property="dataName"/>' <%=check %>/>
				<bean:write name="dbs" property="dataValue"/>
			</logic:iterate>
		</div>
	</fieldset><br><br>
	<fieldset style="height: 115px;width: 510px;<% if(!eLearning){ %> display:none; <%} %>" >
		<legend><bean:message key="train.b_plan.part.station" /></legend>
		<table width="100%" style="line-height: 30px;margin-top: 10px;margin-left: 10px;">
			<tr>
				<td width="75" align="right"><bean:message key="train.b_plan.emppart.station"/></td>
				<td>
					<hrms:optioncollection name="trainStationForm" property="emp_list" collection="emplist"/> 
    	            <html:select name="trainStationForm" property="emp_setid" style="width:150px;" onchange="loademp();">
						<html:options collection="emplist" property="dataName" labelProperty="dataValue" />
					</html:select>
				</td>
				<td width="75" align="right"><bean:message key="train.b_plan.station.emppart" /></td>
				<td>
					<hrms:optioncollection name="trainStationForm" property="post_list" collection="postlist"/> 
    	            <html:select name="trainStationForm" property="post_setid" style="width:150px;" onchange="loadpost();">
						<html:options collection="postlist" property="dataName" labelProperty="dataValue" />
					</html:select>
				</td>
			</tr>
			<tr>
				<td align="right"><bean:message key="train.b_plan.emp.coursecloumn"/></td>
				<td>
					<select style="width:150px;" name="emp_coursecloumn"></select>
				</td>
				<td align="right"><bean:message key="train.b_plan.post.coursecloumn"/></td>
				<td>
					<select style="width:150px;" name="post_coursecloumn"></select>
				</td>
			</tr>
			<tr>
				<td align="right"><bean:message key="train.b_plan.emp.passcloumn"/></td>
				<td>
					<select style="width:150px;" name="emp_passcloumn" onchange="loadpassvalue();"></select>
				</td>
				<td align="right"></td>
				<td>
					
				</td>
			</tr>
			<tr id="passtag">
				<td align="right"><bean:message key="train.b_plan.emp.passsetid"/></td>
				<td colspan="3" id="passitems">				
					
				</td>
			</tr>
		</table>
	</fieldset>
	<br/>
	<input type="button" class="mybutton" value='<bean:message key="button.ok"/>' onclick="saveset();"/>
</html:form>
</center>
<script>loademp();loadpost();loadpassvalue();</script>