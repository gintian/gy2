<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<script type="text/javascript">
<!--
function saveSp()
{
   var url_p=document.getElementById("hostname").href;
   var contentSP=document.getElementById("ctt").value;
   if(trim(contentSP).length<=0)
   {
      alert("审批意见不能为空!");
      return;
   }
   var title,content,type;
   <logic:notEmpty property="spRelation" name="positionDemandForm">
   var object=document.getElementsByName("select");
   var objectvalue;
   for(var i=0;i<object.length;i++){
	   if(object[i].checked==true){
		   objectvalue=object[i].value.split(",") ;
	 }
   }
     if(typeof(objectvalue)=="undefined" && sp_flag=='02'){
    	 alert("请选择报送对象！");
  	   	 return;
     }
     if(objectvalue!=null&&objectvalue.length==3){
     	title=objectvalue[1];
     	content=objectvalue[2];
    	type=objectvalue[0];
     }
   </logic:notEmpty>

   <logic:empty property="spRelation" name="positionDemandForm">
    title=$('user_').value;
    content=$('user_h').value;
    type=$('type').value;
   </logic:empty>
   
   var sp_flag=$('sp').value;
   if(sp_flag=='02'&&trim(content).length<=0)
   {
      alert("请选择报送对象!");
      return;
   }
   var z0301=$('z0').value;
   var a0100=$('a0').value;
   var hashVo=new ParameterSet();
   hashVo.setValue("title",title);
   hashVo.setValue("content",content);
   hashVo.setValue("contentSP",getEncodeStr(contentSP));
   hashVo.setValue("z0301",z0301);
   hashVo.setValue("url_p",url_p);
   hashVo.setValue("sp_flag",sp_flag);
   hashVo.setValue("a0100",a0100);
   hashVo.setValue('type',type);
   var In_parameters="opt=1";
   var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:save_ok,functionId:'3000000237'},hashVo);			
}
///异步发送邮件
function save_ok()
{
   var url_p=document.getElementById("hostname").href;
   var contentSP=document.getElementById("ctt").value;
   var title=$('user_').value;
   var content=$('user_h').value;
   var sp_flag=$('sp').value;
   var z0301=$('z0').value;
   var a0100=$('a0').value;
   var type=$('type').value;
   var hashVo=new ParameterSet();
   hashVo.setValue("title",title);
   hashVo.setValue("content",content);
   hashVo.setValue("contentSP",getEncodeStr(contentSP));
   hashVo.setValue("z0301",z0301);
   hashVo.setValue("url_p",url_p);
   hashVo.setValue("sp_flag",sp_flag);
   hashVo.setValue("a0100",a0100);
   hashVo.setValue('type',type);
   var In_parameters="opt=2";
   var request=new Request({method:'post',asynchronous:true,parameters:In_parameters,functionId:'3000000237'},hashVo);	
  var obj=new Object();
  obj.clo="2";
  returnValue=obj;
  window.close();
}
function winClose()
{
  var obj=new Object();
  obj.clo="1";
  returnValue=obj;
  window.close();
}
function selectobject()
	{
	 var objecttype=$F('roleid');
	 if(objecttype=="#")
	 {
	   $('user_').value='';
	   $('user_h').value='';
	   return;
	 }
	 var flag=0;
	  if(objecttype=="1") 
	 {
	   flag=1;
	 }
	 if(objecttype=="1")
	 {
     	 var return_vo=select_org_emp_dialog(flag,2,0,0,0);   
		 if(return_vo)
		 {
	 		$('user_').value=return_vo.title;
	 		$('user_h').value=return_vo.content;
	 	}	
	 }
	 else if(objecttype=="4")
	 {
	 	var return_vo=select_user_dialog('1','2');
	 	if(return_vo)
	 	{
	 		$('user_').value=return_vo.title;
	 		$('user_h').value=return_vo.content;	 		
	 	}
	 }
	 $('type').value=objecttype;
	}
	function lookGC()
	{
	     var z0301=document.getElementById("z0").value;
         var thecodeurl="/hire/demandPlan/positionDemand/auto_logon_sp.do?b_look=search`z0301="+z0301; 
		 var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
		 var values= window.showModalDialog(iframe_url,null, 
		        "dialogWidth:650px; dialogHeight:540px;resizable:yes;center:yes;scroll:no;status:no");		   
  	    
	}
//-->
</script>
<%
 String aurl = (String)request.getServerName();
    //String port=request.getServerPort()+"";
   // String prl=request.getProtocol();
   // int idx=prl.indexOf("/");
    //prl=prl.substring(0,idx);
   // String url_p=prl+"://"+aurl+":"+port;
   String url_p=SystemConfig.getServerURL(request);

 %>
<html:form action="/hire/demandPlan/positionDemand/auto_logon_sp">
<br>
 <table border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
          <thead>
            <tr> 
              <td colspan="2" align="left" class="TableRow" nowrap>审批意见&nbsp; </td>
              <a href="<%=url_p%>" style="display:none" id="hostname">for vpn</a>
            </tr>
          </thead>
          <tr>
          <td colspan="2" class="RecordRow" align="center" style="padding: 5px;">
           <html:textarea styleId="ctt" property="content" name="positionDemandForm" cols="80" rows="12" ></html:textarea>

</td>
</tr>
 <logic:equal value="02" name="positionDemandForm" property="sp_flag">
<logic:notEmpty property="spRelation" name="positionDemandForm">
<tr>
	<td align="left" colspan="2" class="RecordRow">
		<bean:message key="rsbd.task.selectobject"/>
	</td>
</tr>
<tr>
	<td align="left" colspan="2" class="RecordRow" style="padding: 2px;">
<% int i=0; %>
		<table width="100%" border="0" cellspacing="0" cellpadding="0" class="ListTable">
		<logic:iterate id="element" name="positionDemandForm" property="zparrplist" indexId="index">
			<%
				if (i % 2 == 0) {
			%>
			<tr class="trShallow">
				<%
					} 
								i++;
					%>
					<logic:equal value="1" name="positionDemandForm" property="actortype">
					<td align="left" class="RecordRow" width="50%" nowrap>
						&nbsp;<input type="radio" name="select" 
						<%if(i==1){%> checked="checked" <%} %>
							value="${actortype},<bean:write name="element" property="a0101" />,<bean:write name="element" property="a0100" />">
					
					<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" scope="page"/>  	      
          	   		&nbsp;<bean:write name="codeitem" property="codename" />/<bean:write name="element" property="a0101" />
						&nbsp;
					</td>
					</logic:equal>
					
					<logic:equal value="4" name="positionDemandForm" property="actortype">
					<td align="left" class="RecordRow" width="35px" nowrap>
						&nbsp;<input type="radio" name="select"
						<%if(i==1){%> checked="checked" <%} %>
							value="${actortype},<bean:write name="element" property="a0101" />,<bean:write name="element" property="username" />">
					
						&nbsp;<bean:write name="element" property="groupName" />/<bean:write name="element" property="a0101" />
						&nbsp;
					</td>
					</logic:equal>
			<%
				if (i % 2 == 0) {
			%>
			</tr>
			<%}%>
			
		</logic:iterate>
		<%if(i %2 != 0){%>
				<td align="center" width="50%" nowrap>&nbsp;
				</td>
				</tr>
			<%}%>
		</table>
	</td>
</tr>
</logic:notEmpty>

<logic:empty property="spRelation" name="positionDemandForm">

<tr>
<td width="50%" align="left" colspan="2" class="RecordRow">
<bean:message key="rsbd.task.selectobject"/>
            				 <html:select name="positionDemandForm" property="roleid" size="1" onchange="selectobject();">
               					<option value="#" selected="selected"><bean:message key="label.select" /></option>
                                <html:optionsCollection property="rolelist" value="codeitem" label="codename"/>
				        	 </html:select>
				<INPUT type="text" id="user_" value="" class="TEXT9" size="20" maxlength="200">
                <INPUT type="hidden" id="user_h" value=""  size=30>	
                <INPUT type="hidden" id="type" value=""  size=30>	
</td>

</tr>
</logic:empty> 
</logic:equal>       
<html:hidden name="positionDemandForm" property="z0301" styleId="z0"/>
<html:hidden name="positionDemandForm" property="a0100" styleId="a0"/>
<html:hidden name="positionDemandForm" property="sp_flag" styleId="sp"/>
</table>
 <table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0" >
<logic:equal value="02" name="positionDemandForm" property="sp_flag">
<tr>

<td colspan="2"  align="center" style="padding-top: 5px;">
          <input type="button" class="mybutton" value="<bean:message key="button.ok"/>" onclick="saveSp();"/>
           <input type="button" class="mybutton" value="查阅审批过程" onclick="lookGC();"/>
           <input type="button" class="mybutton" value="<bean:message key="button.close"/>" onclick="winClose();"/>
          </td>
</tr>
</logic:equal>
<logic:notEqual value="02" name="positionDemandForm" property="sp_flag">
          <tr>
          <td colspan="2" align="center" style="padding-top: 5px;">
          <input type="button" class="mybutton" value="<bean:message key="button.ok"/>" onclick="saveSp();"/>
           <input type="button" class="mybutton" value="查阅审批过程" onclick="lookGC();"/>
           <input type="button" class="mybutton" value="<bean:message key="button.close"/>" onclick="winClose();"/>
          </td>
          </tr>
   </logic:notEqual>       
          
</table>
</html:form>