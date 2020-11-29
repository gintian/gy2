<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="java.util.HashMap"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css"; 
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
	String bosflag="";
	if(userView != null)
	{
		bosflag=userView.getBosflag();
	}
%>
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<script language="JavaScript" src="/module/utils/js/template.js"></script>
<script type="text/javascript">
	function getsnapshot(){
		var dh=350;
		if(navigator.appVersion.indexOf('MSIE 6') != -1){
			dh=380;
		}
		var snap_f=document.getElementById("snapshot").value;
		var dw=600,dl=(screen.width-dw)/2,dt=(screen.height-dh)/2;
        var strUrl="/workbench/browse/history/parameters_deploy.do?b_snap=link&snap_norm="/*+snap_f*/;
        //19/3/22 xus 浏览器兼容 历史时点-配置-选择指标按钮 谷歌不弹窗 
        var config = {id:'getsnapshot_showModalDialogs',width:dw,height:dh,type:'0'};
    	modalDialog.showModalDialogs(strUrl,'',config,getsnapshot_callbackfunc);
	}
	//19/3/22 xus 浏览器兼容 谷歌不弹窗 回调函数
	function getsnapshot_callbackfunc(returnvalues){
		 if(returnvalues!=null&&returnvalues.length>0){
	        	var snap_v=returnvalues.split("#");
	        	document.getElementById("snapshot").value=snap_v[0];
	        	document.getElementById("snapshot1").innerHTML=snap_v[1];
	        	
	        	var _querynorm=document.getElementById("querynorm").value;
	        	var _querynorm1=document.getElementById("querynorm1").innerHTML;
	        	var _q=_querynorm.split(",");
	        	var _qview=_querynorm1.split("、");
	        	var _value="";
	        	var _view="";
	        	for(i=0;i<_q.length;i++){
	        		var itemid=_q[i];
	        		var itemdesc=_qview[i];
	        		if(snap_v[0].indexOf(itemid)!=-1){
	        			_value+=","+itemid;
	        			_view+="、"+itemdesc;
	        		}
	        	}
	        	document.getElementById("querynorm").value=_value.substring(1);
	        	document.getElementById("querynorm1").innerHTML=_view.substring(1);
	        } 
	}
	function getquerynorm(){
		var dh=350;
		if(navigator.appVersion.indexOf('MSIE 6') != -1){
			dh=380;
		}
		var snap_f=document.getElementById("snapshot").value;
        var norm_f=document.getElementById("querynorm").value;
        var strUrl="/workbench/browse/history/parameters_deploy.do?b_norm=link&snap_norm="/* +snap_f */+"&sn_left="/* +norm_f */;
		var dw=600,dl=(screen.width-dw)/2,dt=(screen.height-dh)/2;
        //19/3/22 xus 浏览器兼容 历史时点-配置-选择指标按钮 谷歌不弹窗
		var config = {id:'getquerynorm_showModalDialogs',width:dw,height:dh,type:'0'};
    	modalDialog.showModalDialogs(strUrl,'',config,getquerynorm_callbackfunc);
	}
	//19/3/22 xus 浏览器兼容 谷歌不弹窗 回调函数
	function getquerynorm_callbackfunc(returnvalues){
		if(returnvalues!=null&&returnvalues.length>0){
        	var norm_v=returnvalues.split("#");
        	document.getElementById("querynorm").value=norm_v[0];
        	document.getElementById("querynorm1").innerHTML=replaceAll(norm_v[1],"`","、");
        }
	}
	function deploy_save(){
	   var chk_v="";
	   var chkbox=document.getElementsByTagName("input");
	   for(var i=0; i<chkbox.length;i++){
	   	   if(chkbox[i].type=="checkbox" && chkbox[i].checked==true){
	   	   	   chk_v+=chkbox[i].name+",";
	   	   }
	   }
	   if(chk_v==null||chk_v.length<1){
	   	alert("请设置快照人员库！");
	   	return;
	   }
	   
	   var hint = "确定要保存参数吗？\n\n请注意：快照指标如有删除，那么历史快照中原有指标和数据也将相应删除！";
	   if(!confirm(hint))
		   return;
	   
	   var snap_v=document.getElementById("snapshot").value;
	   var norm_v=document.getElementById("querynorm").value;
	   var uniqueitem="${personHistoryForm.uniqueitem }";
	   var In_paramters="snap_v="+snap_v+"&norm_v="+norm_v+"&chk_v="+chk_v+"&uniqueitem="+uniqueitem;
   	   var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:_success,functionId:'0201001205'});
	}
	function _success(outparamters){
		var mess=outparamters.getValue("mess");
		if(mess=="success"){
			//19/3/14 xus 配置按钮 浏览器兼容 
			if(window.showModalDialog){
				returnValue="ok";
			}else{
				window.opener.deploy_callbackfunc("ok");
			}
			window.close();
		}else{
			alert("参数设置失败");
			//19/3/14 xus 配置按钮 浏览器兼容 
			if(window.showModalDialog){
				returnValue="error";
			}else{
				window.opener.deploy_callbackfunc("error");
			}
		}
	}
</script>
<hrms:themes />
<%if("hcm".equalsIgnoreCase(bosflag)){ %>
<style>
.ListTable{
	width:expression(document.body.clientWidth-10); 
	height:expression(document.body.clientHeight-20); 
	margin-left:-4px;
}
</style>
<%}else{ %>
<style>
.ListTable{
	margin-top:10px;
	margin-left:-4px;
	height:expression(document.body.clientHeight-20); 
	width:expression(document.body.clientWidth-10); 
}
</style>
<%} %>
<html:form action="/workbench/browse/history/parameters_deploy">
	<table width="98%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable" style="margin-left:0px;">
		<thead>
			<tr>
				<td class="TableRow" nowrap colspan="1">
					<bean:message key="sys.label.param"/>
				</td>
			</tr>
		</thead>
		<tr>
			<td align="center" class="RecordRow" nowrap>
				<table style="margin: 5 0 5 0;" height="100%" width="100%" border="0" cellspacing="0" align="center" cellpadding="0">
					<tr>
						<td valign="top" align="right" width="20%"><bean:message key="static.target1"/>&nbsp;</td>
						<td valign="top">
							<input type="hidden" id="snapshot" name="snapshot" value='<bean:write name="personHistoryForm" property="left_fields"/>'/>
							<div id="snapshot1" class="RecordRow" style="width: 100%; height: 88px;text-align: left;padding-left: 3px;overflow:auto;margin-right:2px;">
								<bean:write name="personHistoryForm" property="left_value"/>
							</div>
						</td>
						<td valign="top" width="10%"><div style="text-align: right;margin-right:-2px;"><input type="button" value='...' class="mybutton" onclick="getsnapshot();"></div></td>
					</tr>
					<tr>
						<td valign="top" align="right" width="20%"><bean:message key="gz.bankdisk.queryfield"/>&nbsp;</td>
						<td valign="top">
							<input type="hidden" id="querynorm" name="querynorm" value='<bean:write name="personHistoryForm" property="right_fields"/>'/>
							<div id="querynorm1" class="RecordRow"  style="width: 100%; height: 88px;text-align: left;padding-left: 3px;overflow:auto;margin-right:2px;">
								<bean:write name="personHistoryForm" property="right_value"/>
							</div>
						</td>
						<td valign="top" width="10%"><div style="text-align: right;margin-right:-2px;"><input type="button" value='...' class="mybutton" onclick="getquerynorm();"></div></td>
					</tr>
					<tr>
						<td valign="top" align="right"><bean:message key="hrms.nbase"/>&nbsp;</td>
						<td valign="top">
							<div id="querynorm1" class="RecordRow" style="width: 100%; height: 88px;text-align: left;padding-left: 3px;overflow:auto;margin-right:2px;">
					             <bean:define id="chk" name="personHistoryForm" property="chk_v" />
					             <logic:iterate id="dv" name="personHistoryForm" property="chklist">
					             	<bean:define id="k" name="dv" property="dataValue" />
					              	<span style="width: 125px; float: left;margin-top: 2px;">
					              		<input type="checkbox" name="${k }" ${chk[k] }/><bean:write name="dv" property="dataName"/>
					              	</span>
					             </logic:iterate>
							</div>
						</td>
						<td valign="top">&nbsp;</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td align="center" class="RecordRow" nowrap colspan="1"  style="height: 35">
				<html:button styleClass="mybutton" property="b_save" onclick="deploy_save();">
					<bean:message key="button.ok" />
				</html:button>
				<html:button styleClass="mybutton" property="cancel" onclick="top.close();">
					<bean:message key="button.cancel" />
				</html:button>
			</td>
		</tr>
	</table>
</html:form>
<script>
    if(!getBrowseVersion() || getBrowseVersion()==10){ //非IE浏览器兼容性   wangb 20180127
            //menu的齿轮按钮触发的页面样式修改  wangbs 2019年3月6日17:32:08
            var outForm = document.getElementsByTagName("form")[0];
            var outTable1 = outForm.getElementsByTagName("table")[1];
            outTable1.style.marginTop = "10px";
    }
</script>
