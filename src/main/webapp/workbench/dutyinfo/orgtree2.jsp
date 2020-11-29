<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hjsj.hrms.businessobject.sys.SysParamBo"%>
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
    String bosflag="";
    String themes="default";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");
	  bosflag=userView.getBosflag(); 
      /*xuj added at 2014-4-18 for hcm themes*/
      themes=SysParamBo.getSysParamValue("THEMES",userView.getUserName());  
	}
	
	//版本号大于等于50才显示这些功能
	int version = userView.getVersion();
%>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 

<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link rel="stylesheet" href="/css/xtree.css" type="text/css">
<%if("hcm".equals(bosflag)){ %>
   <link href="/css/hcm/themes/<%=themes %>/content.css" rel="stylesheet" type="text/css" />
   <style>
	   body{margin: 0 0 0 0}
	   a {padding-right: 5px;}
   </style>
  
  <%} %> 
  <body style="overflow: auto;" style="margin:0px;padding:0px;">
<html:form action="workbench/dutyinfo/searchdutyinfo">
           <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
		    	<%if(version>=50){ %>
		    	<tr align="left" class="toolbar" >
				<td valign="middle" align="left" style="padding-left:10px;" nowrap="nowrap">
					<hrms:priv func_id="23110107">
						<a href="###" onclick="return backDate();"><img src="/images/quick_query.gif" title="历史时点查询" border="0"></a>
					</hrms:priv>
			  		<hrms:priv func_id="25031">                   
						<a href="###" onclick="openwin('/module/muster/mustermanage/MusterManage.html?musterType=3&moduleID=1')"><img src="/images/prop_ps.gif" border=0 title="常用花名册"></a>
				    </hrms:priv>
				    <hrms:priv func_id="25032">     
				    	<a href="###" onclick="openwin('/general/muster/hmuster/searchHroster.do?b_search=link&nFlag=41&a_inforkind=3&result=0&closeWindow=1')"><img src="/images/bm10.gif" border=0 title="高级花名册"></a>
				    </hrms:priv> 
				    <hrms:priv func_id="231103">  
				    	<a href="###" onclick="openwin('/general/static/commonstatic/statshow.do?b_ini=link&infokind=3&home=0')"><img src="/images/img_f2.gif" border=0 title="统计分析"></a>
					</hrms:priv>
					<hrms:priv func_id="23110108">  
					     
					     <a href="###" onclick=<% if((version>=70) || "hcm".equals(bosflag)) { %>"to_new_report_relations();" <% } else { %> "to_report_relations();" <% } %>>
					     <img src="/images/img_a.gif" border=0 title="<bean:message key="pos.info.report.relations"/>"></a>
					</hrms:priv>
					<logic:equal value="show" name="dutyInfoForm" property="ps_c_sduty">
						<hrms:priv func_id="23110110">  
						     <a href="###" onclick="addduty();"><img src="/images/add_del.gif" border=0 title="引用基准岗位"></a>
						</hrms:priv>
					</logic:equal>
				</td>
				</tr>  
		    	<%} %>
		                
		    </table>
            <div id="treemenu" style="width:100%"> 
               <SCRIPT LANGUAGE=javascript>    
               <bean:write name="dutyInfoForm" property="treeCode" filter="false"/>
                <%
               	if("2".equals(org_expand_level)){
               	%>
					root.expand2level();
				 <%}
               %>
             </SCRIPT>		
             </div>             
</html:form>
</body>
<script type="text/javascript">
//	function backDate(){
//		var dw=300,dh=350,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;					//【5664】岗位设置中历史时点查询界面出现滚动条，建议去掉   jingq upd 2014.12.04
//		var backdate=showModalDialog('/org/orginfo/searchorgtree.do?b_backdate=link','_blank','dialogLeft:'+dl+'px;dialogTop:'+dt+'px;dialogHeight:350px;dialogWidth:400px;center:yes;help:no;resizable:no;status:no;');
//		if(backdate&&backdate.length>9) {
//			dutyInfoForm.action="/workbench/dutyinfo/searchdutyinfo.do?b_search=link&treetype=vorg&backdate="+backdate+"&code=";
//			dutyInfoForm.target="il_body";
//			dutyInfoForm.submit();
//		}else
//			return false;
//	}

function backDate(){
    var top = (window.screen.availHeight-30-400)/2;//获得窗口的垂直位置;
    var left = (window.screen.availWidth-10-370)/2; //获得窗口的水平位置;
    //兼容非IE浏览器 弹窗改用open  wangb 20171122 
    open('/org/orginfo/searchorgtree.do?b_backdate_new=link','_blank','height=290px,width=400px,resizable=no,status=no,top='+top+',left='+left);
}
//弹窗调用父窗口方法  wangb 20171122
function openHistoryReturn(backdate){
    if(backdate&&backdate.length>9) {
        dutyInfoForm.action="/workbench/dutyinfo/searchdutyinfo.do?b_search=link&treetype=vorg&backdate="+backdate+"&code=";
        dutyInfoForm.target="il_body";
        dutyInfoForm.submit();
    }else
        return false;
}
	function openwin(url)
	{
	   window.open(url,"_blank","left=0,top=0,width="+(screen.availWidth-20)+",height="+(screen.availHeight-60)+",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");
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

function addduty(){
	var currnode;
    currnode=Global.selectedItem;
    if(currnode==null) 
  	    return;
  	if(currnode.uid=='root'){
  	  	alert("请选择一个单位或部门！");
  	  	return;
  	}
  	var hashvo=new ParameterSet();
  	hashvo.setValue("orgid",currnode.uid);
  	var request=new Request({asynchronous:false,onSuccess:checknode,functionId:'18010000071'},hashvo);
  	var check = false;
  	function checknode(outp){
  		var orgtype = outp.getValue("orgtype");
  		if(orgtype == 'vorg'){
  			alert("虚拟机构下不许新增岗位!"); return;
  		}
  		
  		theurl="/workbench/dutyinfo/linksduty.jsp?code="+currnode.uid;
  	    var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;

		var iTop = (window.screen.height-30-520)/2;       //获得窗口的垂直位置;
		var iLeft = (window.screen.width-10-530)/2;        //获得窗口的水平位置;
		if(getBrowseVersion()){
            var return_vo=window.showModalDialog(iframe_url,"linksduty_win","dialogWidth:530px; dialogHeight:520px;resizable:no;center:yes;scroll:false;status:no");
            adddutyReturn(return_vo);
		}else {
            window.open(iframe_url, '', 'height=530, width=520,top=' + iTop + ',left=' + iLeft + ',toolbar=no,menubar=no,scrollbars=no,resizable=no,location=no,status=no');
        }
  	}
}
//open 弹窗回调方法  wangb 20190312
function adddutyReturn(return_vo){
	var obj;
	if(getBrowseVersion()) // ie 浏览器获取mil_body 对应的 iframe 
  		obj = parent.frames[1];
 	else //非 ie浏览器 获取 mil_body 对应的 iframe 
  		obj = parent.frames['center_iframe'][1].contentWindow;
  		
	if(return_vo == 'ok'){
  		obj.location.href=Global.selectedItem.action;
  	}
}
function to_new_report_relations(){
	var hashvo=new ParameterSet();
	  var request=new Request({asynchronous:false,onSuccess:showRelation,functionId:'0405050028'},hashvo); 
}

function showRelation(outparamters){
var result=outparamters.getValue("result");
	
	if(result=="yes")
	{
		var thecodeurl ="/pos/posreport/get_relation_tree.do?b_search=link&openwin=1&returnvalue=&yfiles=1";		
		openwin(thecodeurl);
	}else{
		alert("请先设置汇报关系参数");
	}
}

</script>