<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script language="JavaScript" src="/module/utils/js/template.js"></script>
<%
	// 在标题栏显示当前用户和日期 2004-5-10 
	String css_url = "/css/css1.css";
	UserView userView = (UserView) session
			.getAttribute(WebConstant.userView);
	if (userView != null) {
		css_url = userView.getCssurl();
		if (css_url == null || css_url.equals(""))
			css_url = "/css/css1.css";
		//out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
	String bosflag="";
	if(userView != null)
	{
		bosflag=userView.getBosflag();
	}
%>
<script type="text/javascript">
<!--
//19/3/14 xus 历史时点查询 浏览器兼容 窗口关闭方法
function windowClose(){
	parent.window.close();
}
function submitT(){
	 var bdate="";
	 var chkbox=document.getElementsByTagName("input");
	 for(var i=0; i<chkbox.length; i++){
		 if(chkbox[i].type=="radio" && chkbox[i].checked==true){
		     /*wangbs 浏览器非兼容模式下chkbox[i].value2拿不到value2值，换种方式*/
             var value2 = chkbox[i].getAttribute("value2");
             //bdate=chkbox[i].value +"'"+ chkbox[i].value2;
             bdate=chkbox[i].value +"'"+ value2;/*将所选中的时点名称和生成日期一起传回到orgtree.jsp页面*/
         }
	 }
	 if(bdate.length==0)
	 {
	 	Ext.Msg.alert("提示","请将光标定位在要查询历史时点所在行!");
	 	return;
	 }
	//19/3/14 xus 历史时点查询 浏览器兼容返回值
	if(window.showModalDialog){
		returnValue=bdate;
	}else{
		window.opener.backDate_callbackfunc(bdate);
	}
	windowClose();
}
function deleteT(){
	var strId="";
	var chkbox=document.getElementsByTagName("input");
	for(var i=0; i<chkbox.length; i++){
		if(chkbox[i].type=="checkbox" && chkbox[i].checked==true && chkbox[i].name!="all")
			strId += chkbox[i].value+",";
	}
	if(strId.length<1){
		alert("请选择删除项！");
		return false;
	}else if(!compConfirm("确认要删除吗？"))
		return false;
		
	 var In_paramters="strId="+strId;
   	 var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:_success,functionId:'0201001215'});
}
//19/3/22 xus 浏览器兼容 confirm兼容
function compConfirm(msg){
	if(getBrowseVersion()){
		if(!confirm(msg))
			return false;
	}else{
		if(!Ext.MessageBox.confirm("",msg))
			return false;
	}
	return true;
}
function _success(outparamters){
		var mess=outparamters.getValue("mess");
		if(mess=="success"){
			alert("删除失败！");
		}else{
			var strId=outparamters.getValue("strId");
			var str=strId.substring(0,strId.length-1).split(",");
			for(var i=0; i<str.length;i++){
					var tr=document.getElementById("tr"+str[i]);
					tr.parentNode.removeChild(tr);
			}
			var ob=document.getElementById("tablestr");
		    var j=ob.rows.length;
		    for(var i=1;i<j;i++)
		    {
		    	ob.rows[i].className="trShallow";
		    }
		    var chkbox=document.getElementsByTagName("input");
			for(var i=0; i<chkbox.length; i++){
				if(chkbox[i].type=="radio")
					chkbox[i].checked=false;
			}
			if(j>1){
				ob.rows[j-1].className="selectedBackGroud";
			    document.getElementById("rdo"+ob.rows[j-1].id.substring(2)).checked=true;
		    	var d=rtrim(ltrim(ob.rows[j-1].cells[1].innerHTML.toString()));
			    d=d.substring(6);
			    d=d.substring(0,d.length-7);
			    if(navigator.appName.indexOf("Microsoft")!= -1){
			    	window.returnValue=d;
			    }else{
					top.returnValue=d;
				}
			}else{
				if(navigator.appName.indexOf("Microsoft")!= -1){
					window.returnValue="delall";
				}else{
					top.returnValue="delall";
				}
			}
			return true;
		}
}
function selAll(){
	var chkbox=document.getElementsByTagName("input");
	for(var i=0; i<chkbox.length; i++){
		if(chkbox[i].type=="checkbox" && chkbox[i].name!="all")
			chkbox[i].checked=document.getElementById("all").checked;
	}
}
function changeTrColor(id)
 {
    var ob=document.getElementById("tablestr");
    var j=ob.rows.length;
    for(var i=1;i<j;i++)
    {
    	ob.rows[i].className="trShallow";
    }
    var chkbox=document.getElementsByTagName("input");
	for(var i=0; i<chkbox.length; i++){
		if(chkbox[i].type=="radio")
			chkbox[i].checked=false;
	}
    document.getElementById("tr"+id).className="selectedBackGroud";
    document.getElementById("rdo"+id).checked=true;
 }	
 function onchg(){
 	var ob=document.getElementById("tablestr");
    var j=ob.rows.length;
    for(var i=1;i<j;i++){
    	var d=ob.rows[i].cells[1].innerHTML.substring(6);
		d=d.substring(0,d.length-7);
		if(d=="${personHistoryForm.backdate }"){
			ob.rows[i].className="selectedBackGroud";
		    document.getElementById("rdo"+ob.rows[i].id.substring(2)).checked=true;
		}
    }
 }
//去掉左边空格
function ltrim(s)
{ 
	return s.replace(/^\s+/, ''); 
} 

//去掉右边空格
function rtrim(s)
{ 
	return s.replace(/\s+$/, ''); 
} 
//-->
</script>
<style>
<!--
.tablestyle {
	width: 100%;
	text-align: center;
	border: 0px;
	border-collapse: collapse;
}

-->
</style>
<hrms:themes></hrms:themes>
<%if("hcm".equalsIgnoreCase(bosflag)){ %>
<style>
.ListTableF{
	width:expression(document.body.clientWidth-10)!important;
	height:expression(document.body.clientHeight-48)!important;
}
.backdateDiv{
	height: 222px;
}
.TableRow{
	border-right:0;
	border-left:0;
}
.RecordRow{
	border-left:0;
}
</style>
<%}else{ %>
<style>
.ListTableF{
	width:expression(document.body.clientWidth-10)!important;
	height:expression(document.body.clientHeight-48)!important;
}
.backdateDiv{
	height: 229px;
}
.TableRow{
	border-right:0;
	border-left:0;
}
.RecordRow{
	border-left:0;
}
</style>
<%} %>
<body>
	<html:form action="/workbench/browse/history/showinfo">
		<table width="90%" border="0" cellspacing="0" align="center"
			cellpadding="0" class="ListTableF">
			<tr>
				<td colspan="2">
					<table width="100%" height="100%" border="0" cellspacing="0" align="center"
						cellpadding="0">
						<tr>
							<td class="TableRow_top" align="left" style="padding:0 5px 0 5px;">历史时点</td>
						</tr>
						<tr>
							<td valign="top" align="center">
								<div style=" border-collapse: collapse; width: 100%; overflow: auto;border:0;" class="backdateDiv">
									<table id="tablestr" border="0" cellpadding="0" cellspacing="0"
										class="tablestyle">
										<tr class="fixedHeaderTr">
											<td class="TableRow_top" style="border-right-width : 1pt;padding:0 5 0 5px;" width="35" nowrap>
												<input type="checkbox" name="all" id='all' onclick="selAll()"/>
											</td>
											<td class="TableRow_top" style="border-right-width : 1pt;padding:0 5 0 5px;" nowrap width="100">
												<b>生成日期</b>
											</td>
											<td class="TableRow_top" nowrap>
												<b>时点名称</b>
											</td>
										</tr>
										<logic:iterate id="list" name="personHistoryForm"
											property="list1" indexId="i">
											<tr id="tr${list[0] }" onclick="changeTrColor('${list[0] }');">
												<td class="RecordRow_right" width="35" nowrap>
													<input id="rdo${list[0] }"  type="radio" value="${list[1] }" value2="${list[2] }" style="display: none;"/>
													<input type="checkbox" value="${list[0] }" />
												</td>
												<td class="RecordRow" nowrap>
													&nbsp;${list[1] }&nbsp;
												</td>
												<td class="RecordRow_left" nowrap title="${list[2] }" align="left">
													<bean:define id="ls" value="${list[2] }" />
													&nbsp;<%
													if(ls.getBytes().length>36){
														ls=PubFunc.splitString(ls,36)+"......"; 
													}
													%><%=ls %>&nbsp;
												</td>
											</tr>
										</logic:iterate>
									</table>
								</div>
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
		<table width="100%" border="0" cellspacing="0" align="center"
			cellpadding="0">
			<tr>
				<td height="5px"></td>
			</tr>
			<tr>
				<td align="center">

					<html:button styleClass="mybutton" property="" onclick="submitT();">
						查询
					</html:button>
					<hrms:priv func_id="2601211">
					<html:button styleClass="mybutton" property="" onclick="return deleteT();">
						<bean:message key="button.delete" />
					</html:button>
					</hrms:priv>
					<html:button styleClass="mybutton" property=""
						onclick="windowClose();">
						<bean:message key="button.close" />
					</html:button>
				</td>
			</tr>
		</table>
	</html:form>
</body>
<script>onchg();</script>