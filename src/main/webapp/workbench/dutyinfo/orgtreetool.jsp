<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript" src="/js/validate.js"></script>
<script language="javascript">
    	var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	var ViewProperties=new ParameterSet();
</script>
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
	boolean version = false;
	if(userView.getVersion()>=50){//版本号大于等于50才显示这些功能
		version = true;
		}
%>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
<script type="text/javascript">
<!--
//	function backDate(){
//		var dw=300,dh=350,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
//		var backdate=showModalDialog('/org/orginfo/searchorgtree.do?b_backdate=link','_blank','dialogLeft:'+dl+'px;dialogTop:'+dt+'px;dialogHeight:300px;dialogWidth:350px;center:yes;help:no;resizable:no;status:no;');
//		if(backdate&&backdate.length>9) {
//			dutyInfoForm.action="/workbench/dutyinfo/searchdutyinfo.do?b_search=link&treetype=vorg&backdate="+backdate+"&code=";
//			dutyInfoForm.target="il_body";
//			dutyInfoForm.submit();
//		}else
//			return false;
//	}
function backDate(){
    var top = (window.screen.availHeight-30-600)/2;//获得窗口的垂直位置;
    var left = (window.screen.availWidth-10-800)/2; //获得窗口的水平位置;
    //兼容非IE浏览器 弹窗改用open  wangb 20171122
    open('/org/orginfo/searchorgtree.do?b_backdate_new=link','_blank','height=370px,width=400px,resizable=no,status=no,top='+top+',left='+left);
}
//弹窗调用父窗口方法  wangb 20171122
function click_ok(backdate){
    if(backdate&&backdate.length>9) {
        dutyInfoForm.action="/workbench/dutyinfo/searchdutyinfo.do?b_search=link&treetype=vorg&backdate="+backdate+"&code=";
        dutyInfoForm.target="il_body";
        dutyInfoForm.submit();
    }else
        return false;
}
	function openwin(url)
	{
	   window.open(url,"_blank","left=0,top=0,width="+screen.availWidth+",height="+screen.availHeight+",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");
	}
	function to_report_relations()
    {
	  var hashvo=new ParameterSet();
	  var request=new Request({asynchronous:false,onSuccess:dialogOk,functionId:'0405050028'},hashvo); 	
    }
function dialogOk(outparamters)
{
	var result=outparamters.getValue("result");
	
	if(result=="yes")
	{
		var thecodeurl ="/pos/posreport/report_relations_tree.do?b_search=link&openwin=1&returnvalue=";		
		openwin(thecodeurl);
	}else{
		alert("请先设置汇报关系参数");
	}
}
//-->
</script>

<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<style>
<!--
	body {
	margin-left: 0px;
	margin-top: -1px;
	margin-right: 0px;
	margin-bottom: 0px;
	}
-->
</style>
<body border="0" cellspacing="0"  cellpadding="0">
<html:form action="workbench/dutyinfo/searchdutyinfo"> 
    <table width="1000" border="0" cellspacing="0"  align="center" cellpadding="0" style="margin-top: 0">
    	<%if(version){ %>
    	<tr align="left" class="toolbar" style="padding-left:2px;">
		<td valign="middle" align="left">
		<hrms:priv func_id="23110107">
			<a href="###" onclick="return backDate();"><img src="/images/quick_query.gif" alt="历史时点查询" border="0"></a>               
		</hrms:priv>
  		<hrms:priv func_id="25031">                   
			<a href="###" onclick="openwin('/general/muster/hmuster/searchroster.do?b_search=link&a_inforkind=3&result=0&closeWindow=1')"><img src="/images/prop_ps.gif" border=0 alt="常用花名册"></a>
	    </hrms:priv>
	    <hrms:priv func_id="25032">     
	    	<a href="###" onclick="openwin('/general/muster/hmuster/searchHroster.do?b_search=link&nFlag=41&a_inforkind=3&result=0&closeWindow=1')"><img src="/images/bm10.gif" border=0 alt="高级花名册"></a>
	    </hrms:priv> 
	    <hrms:priv func_id="231103">  
	    	<a href="###" onclick="openwin('/general/static/commonstatic/statshow.do?b_ini=link&infokind=3&home=0')"><img src="/images/img_f2.gif" border=0 alt="统计分析"></a>
		</hrms:priv>
		<hrms:priv func_id="23110108">  
		     <a href="###" onclick="to_report_relations();"><img src="/images/img_a.gif" border=0 alt="<bean:message key="pos.info.report.relations"/>"></a>
		</hrms:priv>
		</td>
		</tr>  
    	<%} %>
                
    </table>
</html:form>
</body>
