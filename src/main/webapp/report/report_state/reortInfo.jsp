<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.actionform.report.report_state.ReportStateForm"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html>
  <head>
<%
	String unitcode=request.getParameter("unitcode");
	ReportStateForm reportStateForm=(ReportStateForm)session.getAttribute("reportStateForm");
	String info=reportStateForm.getInfo();
 %>
    
    <title><bean:message key="hire.batch.sendemail"/></title>
    <link rel="stylesheet" href="/css/css1.css" type="text/css">
    <hrms:themes />
  </head>
  <script language="JavaScript" src="/module/utils/js/template.js"></script>
  <script language="JavaScript" src="/js/constant.js"></script>
  <script language='javascript' >

	 function changeFieldItem(){
  	 	var filed = document.getElementById("field");
  	 	var m=filed.value;
  		 insertTxt(m);
  	 }
  	 
  	 
  	 	function insertTxt(strtxt)
		{
		    if(strtxt==null)
		   	 	return ; 
		    if((strtxt.toString()).indexOf("(")!=-1)
		     	strtxt="["+strtxt+"]";
			var expr_editor=$('content');
			//兼容谷歌 ie11 wangbs 20190318
			if(getBrowseVersion()&&getBrowseVersion()!=10){
                expr_editor.focus();
                var element = document.selection;
                if (element!=null)
                {
                    var rge = element.createRange();
                    if (rge!=null)
                        rge.text=strtxt;
                }
			}else{
                expr_editor.value = expr_editor.value+strtxt;
            }
		}
  		function sendMail(){
  			var isSub;
  			var title=document.getElementsByName("title");
  			var ss=title[0];
  			if(ss.value==null||trim(ss.value).length==0){
  				alert("请输入标题");
  				return;
  			}
  			if(confirm("您确定要发送邮件吗？")){
	  			if(confirm("是否包含下级？")){
	  				isSub=1;
	  			}else{
	  				isSub=2;
	  			}
  			}else{
  				return;
  			}
			var map=new HashMap();
			map.put("sendtype","1");
			map.put("isSub",isSub+"");
			map.put("unitcode",'<%=unitcode%>');
			map.put("content",$('content').value);
			map.put("title",ss.value);
			 Rpc({functionId:'0305000215',asynchronous:true,success:function(res){
				 res=Ext.decode(res.responseText)
				 var info = res.info;
					if(info=="1")
						alert("发送成功！");
					else if(info=="2")
						alert("发送失败！请检查通讯服务配置是否正确！");
					else if(info=='3')
						alert("当前登录用户不是报表负责人！");
					else if(info=='4')
						alert("报表负责人没有设置邮箱！");
			 },scope:this},map);
  		}
  		function sendMessage(){
  			var isSub;
  			var title=document.getElementsByName("title");
  			var ss=title[0];
  			if(ss.value==null||trim(ss.value).length==0){
  				alert("请输入标题");
  				return;
  			}
  			if(confirm("您确定要发送短信吗？")){
	  			if(confirm("是否包含下级？")){
	  				isSub=1;
	  			}else{
	  				isSub=2;
	  			}
  			}else{
  				return;
  			}
  			var hashvo=new ParameterSet();
			hashvo.setValue("sendtype","2");
			hashvo.setValue("isSub",isSub);
			hashvo.setValue("unitcode",<%=unitcode%>);
			hashvo.setValue("content",$('content').value);
			hashvo.setValue("title",ss.value);
			var request=new Request({method:'post',asynchronous:true,
				onSuccess:function(res){
					var info = res.getValue("info");
					if(info=="1")
						alert("发送成功！");
					else if(info=="2")
						alert("发送失败！请检查通讯服务配置是否正确！");
						
				},
				functionId:'0305000215'},hashvo);
  		}
     	function winClose(){
	     	if(parent.parent.Ext){
	     	    var closeTarget = parent.parent.Ext.getCmp("sendInfoWin");
	     	    if(closeTarget){
                    closeTarget.close();
				}
			}else{
	     	    window.close();
			}
		}
  </script>
  <body >
   <html:form action="/report/report_state/reportstate">
  	<table align='center'  >
  		<tr><td><bean:message key="lable.tz_template.title"/></td>
  		   <td>
  			<html:text property="title" onkeydown="if(event.keyCode==13)return false;" name="reportStateForm" styleClass="text4" style="width:150px" />
  			
  		   </td>
  		</tr>
  		<tr><td><bean:message key="hire.inset.field"/></td>
  		<td  width="20">   
  		
  			<select style= "width:150px " onchange="changeFieldItem();" id="field">
  					<logic:iterate id="element" name="reportStateForm" property="titleList"  > 
	               	 	<option value='<bean:write name="element" property="value" />' ><bean:write name="element" property="name" /></option>             
	               </logic:iterate>
  			</select>
  			
  		</td></tr>
  		<tr><td><bean:message key="hire.email.content"/></td>
  		<td>   
  		<html:textarea name="reportStateForm" property="content"  cols="80" rows="18" >
  		
  		</html:textarea>
  		</td></tr>
  		
  		<tr><td colspan='2' align="center" >
  		<input type='button' class='mybutton' value="<bean:message key="label.sms.send"/>" onclick='sendMessage();'/>
  			<Input type='button' class='mybutton'  value="<bean:message key="label.zp_employ.sendmail"/>" onclick='sendMail()'  />
  			<Input type='button' class='mybutton'  value="<bean:message key="button.cancel"/>" onclick='winClose()' />
  		</td></tr>
  		
  	</table>
  	</html:form>
  </body>
</html>
