<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView,com.hrms.hjsj.sys.VersionControl,com.hrms.struts.constant.WebConstant"%>
<%
		UserView userView=(UserView)session.getAttribute(WebConstant.userView);
		String priv=userView.getManagePrivCodeValue();
		if(priv.length()==0){
			if(!userView.getManagePrivCode().startsWith("UN"))
				priv="`";
		}
 %>
<link href="/css/css1.css" rel="stylesheet" type="text/css">
<body style="margin:0px;padding:0px;overflow: hidden;">
<div id=div style="height: 40px;padding-left: 10px;padding-top: 5px">
<bean:message key="label.select.org"/>:
<input type="hidden" name="orgid" id="orgname.value" onchange="changeorg(this.value);">
<input type="text" name="orgname.viewvalue" value="${lawbaseForm.orgname }" readonly="readonly" style="height: 20px;"><img  src="/images/code.gif" align="absmiddle" onclick='javascript:openInputCodeDialogOrgInputPos("UN","orgname.viewvalue","<%=priv %>","1");'/>&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;
<bean:message key="edit_report.year"/>:
<html:select styleId="yearid" property="year" name="lawbaseForm" onchange="changeyear(this.value);">
	<html:optionsCollection name="lawbaseForm" property="yearlist" value="dataValue" label="dataName" />
</html:select> 
</div>
<iframe id="fram" name="bb" frameborder="0" scrolling="yes" style="border: 0px;margin:0px;padding:0px;overflow: hidden;" src="" width="100%"></iframe>
<script>
	var orgname="${lawbaseForm.orgname }";
	var url=getDecodeStr("${lawbaseForm.url }");
	document.getElementById("fram").src=url;
	setHight();
	function setHight() { 
		var obj = document.getElementById("fram");
		var bodyHeight = document.body.offsetHeight;
		var bodyWidth = document.body.offsetWidth;
		//var divHeight = document.getElementById("div").offsetHeight;
		obj.height = bodyHeight - 40;
		obj.width = bodyWidth;
	}
	function changeyear(value){
		//alert(value);
		//alert(url);
		//alert(document.getElementById("fram").src);
		var dirpath="";
		if(url.indexOf("/")!=-1){
			dirpath=url.substring(0,url.lastIndexOf("/")+1);
		}else{
			dirpath=url.substring(0,url.lastIndexOf("\\")+1);
		}
		document.getElementById("fram").src=dirpath+value;
	}
	
	function changeorg(value){
		//alert(value);
		var hashvo=new ParameterSet();
		hashvo.setValue("tmppath",getDecodeStr("${lawbaseForm.tmppath }"));
		hashvo.setValue("dirname","${lawbaseForm.dirname }");
	    hashvo.setValue("orgid",value);	
		var request=new Request({method:'post',asynchronous:false,onSuccess:showyear,functionId:'10400101101'},hashvo);
	}
	function showyear(outparamters) {
		var msg = outparamters.getValue("msg");
		if(msg=="ok"){
		    url = getDecodeStr(outparamters.getValue("url"));
			var yearlist=outparamters.getValue("yearlist");
			var yearselect = document.getElementById("yearid");
			AjaxBind.bind(yearselect,yearlist);
			yearselect.value=outparamters.getValue("year");
			orgname=document.getElementsByName("orgname.viewvalue")[0].value;
			try{
	    	   if (navigator.appName.indexOf("Microsoft")!= -1) { 
	    	   		yearselect.fireEvent('onchange'); 
			         //ie  
			    }else{ 
			        yearselect.onchange(); 
			    }  
			}catch(e){
			}
		}else{
			alert(msg);
			document.getElementsByName("orgname.viewvalue")[0].value=orgname;
			document.getElementById("orgname.value").value="";
		}
	}
</script>
</body>
