<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%!
	private static String org_expand_level;
	static{
		org_expand_level=com.hrms.struts.constant.SystemConfig.getPropertyValue("org_expand_level");
	}
 %>
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
	String url="";
	if(userView != null)
	{
		url=userView.getBosflag();
	}
%>
   <style>
<!--
	body {
	margin-left: 0px;
	margin-top: 0px;
	margin-right: 0px;
	margin-bottom: 0px;
	}
	
-->
</style>
<script type="text/javascript" src="/js/hjsjUrlEncode.js"></script>
<script language="javascript" src="/js/showModalDialog.js"></script> 
<script type="text/javascript">
	function static(){
		personHistoryForm.target="il_body";
		personHistoryForm.action="/general/static/commonstatic/history/statshow.do?b_ini=link&infokind=1&backdate=${personHistoryForm.backdate }&uniqueitem=${personHistoryForm.uniqueitem }";
		personHistoryForm.submit();
	}
	
	function backup(url){
		var dh=300;
		if(url=="hcm"){
			dh=270;
		}else if(url=="hl"){
			dh=270;
		}
		var theurl="/workbench/browse/history/showinfo.do?br_backup=link";
		var dw=380,dl=(screen.width-dw)/2,dt=(screen.height-dh)/2;
		var config = {id:'backup_showModalDialogs',width:dw,height:dh,type:'0'};
		modalDialog.showModalDialogs(theurl,'',config,backup_callbackfunc);
		//var returnv=window.showModalDialog(theurl,'_blank','dialogLeft:'+dl+'px;dialogTop:'+dt+'px;dialogHeight:'+dh+'px;dialogWidth:'+dw+'px;center:yes;help:no;resizable:no;scroll:no;status:no;');
	}
	//19/3/14 xus 快照 浏览器兼容 回调方法
	function backup_callbackfunc(returnv){
		if(returnv=="aa"){
        	personHistoryForm.target="il_body";
			personHistoryForm.action="/workbench/browse/history/showinfo.do?b_search=link&action=showinfodata.do&target=nil_body&strQuery=&returnvalue=&backdate=";
			personHistoryForm.submit();
        }else{
        	return false;
        }
	}
	function deploy(url){
		var dh=350;
		if(navigator.appVersion.indexOf('MSIE 6') != -1){
			dh=380;
		}
		if(url=="hcm"){
			dh=380;
		}else if(url=="hl"){
			dh=380;
		}
		var dw=600,dl=(screen.width-dw)/2,dt=(screen.height-dh)/2;
		var thecodeurl="/workbench/browse/history/parameters_deploy.do?b_query=link";
		//19/3/14 xus 历史时点查询 浏览器兼容 
		var config = {id:'deploy_showModalDialogs',width:dw,height:dh,type:'0'};
		modalDialog.showModalDialogs(thecodeurl,'',config,deploy_callbackfunc);
		/*
		var returnvo=window.showModalDialog(thecodeurl, "_blank", 
              "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogHeight:"+dh+"px;dialogWidth:600px;center:yes;help:no;resizable:no;status:no;scroll:no;");
		*/
	}
	//19/3/14 xus 配置按钮 浏览器兼容 回调方法
	function deploy_callbackfunc(returnvo){
		if(returnvo!=null&&returnvo=="ok"){
        	personHistoryForm.target="il_body";
			personHistoryForm.action="/workbench/browse/history/showinfo.do?b_search=link&action=showinfodata.do&target=nil_body&strQuery=&returnvalue=";
			personHistoryForm.submit();
        }else
        	return false;
	}
	function backDate(){
		var dh=300,dw=400;
		if(navigator.appVersion.indexOf('MSIE 6') != -1){
			dh=330;
			dw=410;
		}
		var dl=(screen.width-dw)/2,dt=(screen.height-dh)/2;
		var theurl="/workbench/browse/history/showinfo.do?b_backdate=link";
		//19/3/14 xus 历史时点查询 浏览器兼容 
		var config = {id:'backDate_showModalDialogs',width:dw,height:dh,type:'0'};
		modalDialog.showModalDialogs(theurl,'',config,backDate_callbackfunc);
	}
	//19/3/14 xus 历史时点查询 浏览器兼容 回调方法
	function backDate_callbackfunc(backvo){
		if((backvo!=null&&backvo.length>8&&backvo!="${personHistoryForm.backdate }")||backvo=="delall") {
			if(backvo=="delall")backvo="";
			var ba = backvo.split("'");/*将传过来的时点名称和生成日期分开，其中ba[0]为日期，ba[1]为名称*/
			personHistoryForm.action="/workbench/browse/history/showinfo.do?b_search=link&action=showinfodata.do&target=nil_body&strQuery=&returnvalue=&backdate="+ba[0]+"&backname="+$URL.encode(ba[1]);
			personHistoryForm.target="il_body"
			personHistoryForm.submit();
		}else
			return false;
	}
</script>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link rel="stylesheet" href="/css/xtree.css" type="text/css">
<hrms:themes />
<body style="margin-left:0px;margin-top:0px;overflow:auto;height:100%;">
<html:form action="/workbench/browse/history/showinfo"> 
    <table border="0" cellspacing="0"  align="center" cellpadding="0" class="mainbackground" style="width:100%;">
    <tr align="left" valign="middle" class="toolbar" >
				<td valign="middle" align="left" style="padding-left: 8px;">
				    <hrms:priv func_id="260121">  
				    	<logic:equal value="1" name="personHistoryForm" property="ifbackup">
				    <!-- 间距调整 xiaoyun 2014-6-4 start -->
					  	    <!-- <input type="image" name="###" src="/images/quick_query.gif" alt="历史时点" style="cursor:pointer;" onclick="return backDate();"> -->
					  	    <img src="/images/quick_query.gif" border=0 title='历史时点' style="cursor:pointer;" onclick="return backDate();"/>
					    </logic:equal>
                    </hrms:priv> 	
                    <hrms:priv func_id="260123"> 
                    	<logic:equal value="1" name="personHistoryForm" property="ifbackup">
                    		<img src="/images/45.bmp" border=0 title='统计分析' style="cursor:pointer;margin-left: 3px;" onclick="return static();"/>
					   		<!-- <input type="image" name="###" src="/images/45.bmp" alt="统计" style="cursor:pointer;" onclick="return static();"> -->
                    	</logic:equal>
                    </hrms:priv> 
                    <hrms:priv func_id="260124">  
                    	<img src="/images/save_edit.gif" border=0 title='快照' style="cursor:pointer;margin-left: 3px;" onclick="return backup('<%=url%>');"/>
					   	<!-- <input type="image" name="###" src="/images/save_edit.gif" alt="快照" style="cursor:pointer;" onclick="return backup();"> -->
                    </hrms:priv> 
                    <hrms:priv func_id="260122">  
                       <img src="/images/img_o.gif" border=0 title='配置' style="cursor:pointer;margin-left: 3px;" onclick="return deploy('<%=url%>');"/>
					   <!-- <input type="image" name="###" src="/images/img_o.gif" alt="配置" style="cursor:pointer;" onclick="return deploy();"> -->
                    </hrms:priv>
                    <!-- 间距调整 xiaoyun 2014-6-4 end -->	
				</td>
	</tr>
       <tr>
           <td align="left"> 
            <div id="treemenu"> 
             <SCRIPT LANGUAGE="javascript">    
              Global.defaultInput=0;
              Global.showroot=false;
              Global.defaultchecklevel=3;
              Global.defaultradiolevel=3;
              Global.showorg=1;
               <bean:write name="personHistoryForm" property="treeCode" filter="false"/>
             </SCRIPT>
             </div>             
           </td>
           </tr>           
    </table>
</html:form>
<script LANGUAGE="javascript">
	root.openURL();
	<%
               	if("2".equals(org_expand_level)){
               	%>
					root.expand2level();
				 <%}
               %>
</script>
</body>