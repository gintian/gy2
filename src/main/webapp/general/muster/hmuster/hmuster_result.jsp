<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.hjsj.utils.Sql_switcher"%>
<%@ page import="com.hrms.struts.constant.SystemConfig,com.hrms.struts.constant.SystemConfig,com.hjsj.hrms.businessobject.sys.SysParamBo"%>
<%@ page import="com.hrms.hjsj.sys.ConstantParamter"%>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient"%>
<script type='text/javascript' src='../../../module/utils/js/template.js'></script>
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
<link rel="stylesheet" href="/css/css1_brokenline.css" type="text/css">
<script language="JavaScript" src="/js/function.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<logic:equal value="mobile" name="hmusterForm" property="returnflag">
	<script type="text/javascript" src="/phone-app/jquery/jquery-3.5.1.min.js"></script>
	<script type="text/javascript" src="/phone-app/jquery/rpc_command.js"></script>
</logic:equal>
<script language="javascript" src="/general/sys/hjaxmanage.js"></script>
<%
	// 在标题栏显示当前用户和日期 2004-5-10 
	
	String userName = null;
	String css_url = "/css/css1.css";
	UserView userView = (UserView) session
			.getAttribute(WebConstant.userView);
	String a_code="UN";
	String bosflag="";
	if(userView != null){	  
	  	bosflag=userView.getBosflag();
	  	bosflag=bosflag!=null?bosflag:"";                
	}
	if (userView != null) {
		css_url = userView.getCssurl();
		if (css_url == null || css_url.equals(""))
			css_url = "/css/css1.css";
		userName=userView.getUserName();
	}
    String themes="default";
	if(userView!=null){
		if (userView.isSuper_admin()){
		}else {
			a_code=userView.getManagePrivCode()+userView.getManagePrivCodeValue();
		}
	      /*xuj added at 2014-4-18 for hcm themes*/
	      themes=SysParamBo.getSysParamValue("THEMES",userView.getUserName()); 
	}
	String appdate=ConstantParamter.getAppdate(userName);
	int dbtype=Sql_switcher.searchDbServer();
	String aurl = (String) request.getServerName();
	String port = request.getServerPort() + "";
	String url_p = SystemConfig.getCsClientServerURL(request);
	String userFullName=userView.getUserFullName();
	if(userFullName==null||userFullName.equals(""))
	    userFullName=userName;
   EncryptLockClient lockclient=(EncryptLockClient)session.getServletContext().getAttribute("lock");
   String license=lockclient.getLicenseCount();
   int version=userView.getVersion();
   if(license.equals("0"))
        version=100+version;
   int usedday=lockclient.getUseddays();
   String res=request.getParameter("res");
   String showreturn=request.getParameter("showreturn");
   String showbuttons=request.getParameter("showbuttons");
   
%>
<style>
.x-btn-default-toolbar-small .x-frame-tl{
	background-image:url(/ext/ext6/resources/images/btn/btn-default-toolbar-small-corners.gif)
}
.x-btn-default-toolbar-small .x-frame-tc{
	background-image:url(/ext/ext6/resources/images/btn/btn-default-toolbar-small-corners.gif)
}
.x-btn-default-toolbar-small .x-frame-tr{
background-image:url(/ext/ext6/resources/images/btn/btn-default-toolbar-small-corners.gif)
}

.x-btn-default-toolbar-small .x-frame-bl{
background-image:url(/ext/ext6/resources/images/btn/btn-default-toolbar-small-corners.gif)
}
.x-btn-default-toolbar-small .x-frame-br{
background-image:url(/ext/ext6/resources/images/btn/btn-default-toolbar-small-corners.gif)
}
.x-btn-default-toolbar-small .x-frame-bc{
background-image:url(/ext/ext6/resources/images/btn/btn-default-toolbar-small-corners.gif)
}

.x-btn-over .x-btn-default-toolbar-small-ml,.x-btn-over .x-btn-default-toolbar-small-mr
	{
	background-image:
		url(/ext/ext6/resources/images/btn/btn-default-toolbar-small-sides.gif)
}

.x-btn-over .x-btn-default-toolbar-small-mc {
	background-image: url(/ext/ext6/resources/images/btn/btn-default-toolbar-small-fbg.gif)
}

.ListTable_self {
    BACKGROUND-COLOR: #F7FAFF;
    BORDER-BOTTOM: medium none; 
    BORDER-COLLAPSE: collapse; 
    BORDER-LEFT: medium none; 
    BORDER-RIGHT: medium none; 
    BORDER-TOP: medium none;    
}
.TEXT {
	BACKGROUND-COLOR:transparent;
	
	BORDER-BOTTOM: medium none; 
	BORDER-LEFT: medium none; 
	BORDER-RIGHT: medium none; 
	BORDER-TOP: medium none;
	text-align: center;
}
.tableLoca{
	position:relative;
	top:0;
	left:0.0;
	height:20;
	width:expression(document.body.scrollWidth); 
	background-color:#DEEAF5;
	white-space:nowrap;
}
.locked_self_t{
	font-size: 14px;
	height:22;
	/* font-weight: bold; */	
	valign:middle;
	margin-top: 0px;
	
}
div#tbl-container {
		 
}
a:link {
	COLOR: #1B4A98 !important; TEXT-DECORATION: none;font-size: 100% !important	
}
a:visited {
	COLOR: #1B4A98 !important; TEXT-DECORATION: none;font-size: 100% !important
}
a:active {
	COLOR: #1B4A98;TEXT-DECORATION: none;font-size: 100% !important
}
a:hover {
	COLOR: #E39E19; TEXT-DECORATION:none;font-size: 100% !important
}

</style>
<script language='javascript'>
var checkprint = "1";
var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
var isEdge = userAgent.indexOf("Edge") > -1;
function initMuster(){
      var aurl="<%=url_p%>";
      var DBType="<%=dbtype%>";
      var UserName="<%=userName%>";  
      
      var obj = document.getElementById('MusterPreview1');   
      obj.SetURL(aurl);
      obj.SetDBType(DBType);

      obj.SetUserName(UserName);
      obj.SetBizDate("<%=appdate%>");
      obj.SetUserFullName("<%=userFullName%>");
      obj.SetHrpVersion("<%=version%>");
      obj.SetTrialDays("<%=usedday%>","30");
      checkprint="2";
}
function showMuster(){
      var obj = document.getElementById('MusterPreview1');      
       //if(checkprint=="2"){
      	obj.SetParams("${hmusterForm.musterPreviewPluginParams}");
      	try { obj.SetJSessionId(AxManager.getJSessionId()); } catch(err) {}
      	obj.ShowMusterModal();
      	 var waitInfo;
	    waitInfo=eval("wait");
     	waitInfo.style.display="none";
     // }
}
function returns()
{
	var modleFlag="${hmusterForm.modelFlag}";
	if(modleFlag==3||modleFlag==21||modleFlag==41)
		hmusterForm.action="/general/muster/hmuster/select_muster_name.do?b_return=return";
	else
	{
		hmusterForm.action="${hmusterForm.returnURL}";
	}
	hmusterForm.submit();

}
function returns2()
{
	hmusterForm.action="/general/muster/hmuster/select_muster_name.do?b_query=link&a_inforkind=${hmusterForm.infor_Flag}";
	hmusterForm.submit();
}

function showFieldList(outparamters)
{
	<logic:notEqual value="mobile" name="hmusterForm" property="returnflag">
	Ext.MessageBox.close();
	  var res=Ext.decode(outparamters.responseText);// changxy  20160826  使用ext解析获取的内容
	  if(res.succeed){
		   if(!res.errorMsg){
			   var url=res.url;
				window.location.target="_blank";
				window.location.href="/servlet/vfsservlet?fromjavafolder=true&fileid="+url;
				
		   }else{
			   Ext.showAlert(res.errorMsg);
		   }
			
	  }else{
		  Ext.showAlert(res.message);
	  }
	</logic:notEqual>
	<logic:equal value="mobile" name="hmusterForm" property="returnflag">
		var map=JSON.parse(outparamters);
		if(map.succeed){
			url=map.url;
			window.location.target="_blank";
			window.location.href="/servlet/vfsservlet?fromjavafolder=true&fileid="+url;
		}
	</logic:equal>
}

function showExcel(outparamters)
{
	<logic:notEqual value="mobile" name="hmusterForm" property="returnflag">
		//var outName=outparamters.getValue("outName");
		Ext.MessageBox.close();
		var res=Ext.decode(outparamters.responseText);// changxy  20160826  使用ext解析获取的内容
		if(res.succeed){
			if(!res.errorMsg){
				var outName=res.outName;
				window.location.target="_blank";
				window.location.href="/servlet/vfsservlet?fromjavafolder=true&fileid="+outName;
			}else{
				Ext.showAlert(res.errorMsg);
			}
		}else{
			Ext.showAlert(res.message);
		}
	</logic:notEqual>
	<logic:equal value="mobile" name="hmusterForm" property="returnflag">
		var map=JSON.parse(outparamters);
		if(map.succeed){
			var outName=map.outName;
			window.location.target="_blank";
			window.location.href="/servlet/vfsservlet?fromjavafolder=true&fileid="+outName;
			
		}
	</logic:equal>
}  

function outStyleHRoster(){
	hmusterForm.action="/general/muster/hmuster/select_muster_name.do?b_next=next&checkflag=1"; 
   	hmusterForm.submit();
}
function onchanges(){
	hmusterForm.action="/general/muster/hmuster/select_muster_name.do?b_next2=b_next2&res=1&operateMethod=next&tabID=${hmusterForm.tabID}&isGetData=1"; 
   	hmusterForm.submit();
   	jinduo();
}

function onchanges(dbpre,type){
	var mainCom=Ext.getCmp("mainTitle");
	var dbCom=Ext.getCmp("dbpre");
	var dbValue="";
	if(dbCom){
		dbValue=dbCom.getSelection().data.dataValue;
	}
	var mainValue="";
	if(mainCom){
		mainValue=mainCom.getSelection().data.dataValue;
	}
	hmusterForm.action="/general/muster/hmuster/select_muster_name.do?b_next2=b_next2&res=1&operateMethod=next&tabID=${hmusterForm.tabID}&isGetData=1&dbpre="+dbValue+"&conditionBase="+mainValue+"&checkflag=${hmusterForm.checkflag}"; 
   	hmusterForm.submit();
   	jinduo();
}

function excecutePDF()
{
	// xuj update 兼容Safari浏览器内核
	<logic:notEqual value="mobile" name="hmusterForm" property="returnflag">
		Ext.MessageBox.wait("数据正在导出，请稍候...", "等待");
		var hashvo=new HashMap(); //使用RPC向后台传数据 changxy20160826
		hashvo.put("infor_Flag","${hmusterForm.infor_Flag}");
		hashvo.put("tabID","${hmusterForm.tabID}");
		hashvo.put("isGroupPoint","${hmusterForm.isGroupPoint}");
		hashvo.put("groupPoint","${hmusterForm.groupPoint}");
		hashvo.put("isGroupPoint2","${hmusterForm.isGroupPoint2}");
		hashvo.put("groupPoint2","${hmusterForm.groupPoint2}");
		hashvo.put("pageRows","${hmusterForm.pageRows}");
		hashvo.put("currpage","${hmusterForm.currpage}");
		hashvo.put("isAutoCount","${hmusterForm.isAutoCount}");
		hashvo.put("zeroPrint","${hmusterForm.zeroPrint}");
		hashvo.put("emptyRow","${hmusterForm.emptyRow}");
		hashvo.put("column","${hmusterForm.column}");
		hashvo.put("dataarea","${hmusterForm.dataarea}");
		hashvo.put("columnLine","${hmusterForm.columnLine}");
		hashvo.put("dbpre","${hmusterForm.dbpre}");
		hashvo.put("pix","${hmusterForm.pix}");
		
		hashvo.put("history","${hmusterForm.history}");
		hashvo.put("year","${hmusterForm.year}");
		hashvo.put("month","${hmusterForm.month}");
		hashvo.put("count","${hmusterForm.count}");
		hashvo.put("printGrid","${hmusterForm.printGrid}");
		
		hashvo.put("modelFlag","${hmusterForm.modelFlag}");
		hashvo.put("relatTableid","${hmusterForm.relatTableid}");
		hashvo.put("condition","${hmusterForm.condition}");
		hashvo.put("returnURL","${hmusterForm.returnURL}");
		hashvo.put("selectedPoint","${hmusterForm.selectedPoint}");
		hashvo.put("toScope","${hmusterForm.toScope}");
		hashvo.put("fromScopt","${hmusterForm.fromScope}");
		hashvo.put("showPartJob","${hmusterForm.showPartJob}");
		hashvo.put("exce","PDF");
		//var In_paramters="exce=PDF";  
		
		Rpc({functionId:'0550000005',success:showFieldList},hashvo);  
	   	//var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,
	   //	onSuccess:showFieldList,functionId:'0550000005'},hashvo);
	</logic:notEqual>
	<logic:equal value="mobile" name="hmusterForm" property="returnflag">
		var map = new HashMap();
		map.put("infor_Flag","${hmusterForm.infor_Flag}");
		map.put("tabID","${hmusterForm.tabID}");
		map.put("isGroupPoint","${hmusterForm.isGroupPoint}");
		map.put("groupPoint","${hmusterForm.groupPoint}");
		map.put("isGroupPoint2","${hmusterForm.isGroupPoint2}");
		map.put("groupPoint2","${hmusterForm.groupPoint2}");
		map.put("pageRows","${hmusterForm.pageRows}");
		map.put("currpage","${hmusterForm.currpage}");
		map.put("isAutoCount","${hmusterForm.isAutoCount}");
		map.put("zeroPrint","${hmusterForm.zeroPrint}");
		map.put("emptyRow","${hmusterForm.emptyRow}");
		map.put("column","${hmusterForm.column}");
		map.put("dataarea","${hmusterForm.dataarea}");
		map.put("columnLine","${hmusterForm.columnLine}");
		map.put("dbpre","${hmusterForm.dbpre}");
		map.put("pix","${hmusterForm.pix}");
		
		map.put("history","${hmusterForm.history}");
		map.put("year","${hmusterForm.year}");
		map.put("month","${hmusterForm.month}");
		map.put("count","${hmusterForm.count}");
		map.put("printGrid","${hmusterForm.printGrid}");
		
		map.put("modelFlag","${hmusterForm.modelFlag}");
		map.put("relatTableid","${hmusterForm.relatTableid}");
		map.put("condition","${hmusterForm.condition}");
		map.put("returnURL","${hmusterForm.returnURL}");	
		map.put("selectedPoint","${hmusterForm.selectedPoint}");
		map.put("toScope","${hmusterForm.toScope}");
		map.put("fromScopt","${hmusterForm.fromScope}");
		map.put("showPartJob","${hmusterForm.showPartJob}");
		map.put("exce","PDF"); 
		var platform=navigator.platform;
		map.put("platform",platform); 
		Rpc({functionId:'0550000005',success:showFieldList},map); 	
	</logic:equal>
}


function excecuteExcel(type)
{
	// xuj update 兼容Safari浏览器内核
	<logic:notEqual value="mobile" name="hmusterForm" property="returnflag">	
		Ext.MessageBox.wait("数据正在导出，请稍候...", "等待");
		var hashvo=new HashMap();//使用RPC向后台传数据 changxy20160826
		hashvo.put("infor_Flag","${hmusterForm.infor_Flag}");
		hashvo.put("tabID","${hmusterForm.tabID}");
		hashvo.put("isGroupPoint","${hmusterForm.isGroupPoint}");
		hashvo.put("groupPoint","${hmusterForm.groupPoint}");
		hashvo.put("isGroupPoint2","${hmusterForm.isGroupPoint2}");
		hashvo.put("groupPoint2","${hmusterForm.groupPoint2}");
		hashvo.put("zeroPrint","${hmusterForm.zeroPrint}");
		hashvo.put("column","${hmusterForm.column}");
		hashvo.put("dataarea","${hmusterForm.dataarea}");
		hashvo.put("dbpre","${hmusterForm.dbpre}");
		hashvo.put("history","${hmusterForm.history}");
		hashvo.put("year","${hmusterForm.year}");
		hashvo.put("month","${hmusterForm.month}");
		hashvo.put("count","${hmusterForm.count}");
		hashvo.put("printGrid","${hmusterForm.printGrid}");
		hashvo.put("modelFlag","${hmusterForm.modelFlag}");
		hashvo.put("paperRows","${hmusterForm.paperRows}");
		hashvo.put("pageRows","${hmusterForm.pageRows}");
		hashvo.put("emptyRow","${hmusterForm.emptyRow}");
		hashvo.put("selectedPoint","${hmusterForm.selectedPoint}");
		hashvo.put("toScope","${hmusterForm.toScope}");
		hashvo.put("fromScopt","${hmusterForm.fromScope}");
		hashvo.put("showPartJob","${hmusterForm.showPartJob}");
		hashvo.put("excelType",type);
		hashvo.put("exce","excel"); 	
		Rpc({functionId:'0550000009',success:showExcel},hashvo);  
		
		//var In_paramters="exce=excel";  
		//var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,
		//onSuccess:showExcel,functionId:'0550000009'},hashvo);
	</logic:notEqual>
	<logic:equal value="mobile" name="hmusterForm" property="returnflag">
		var map = new HashMap();
	    map.put("infor_Flag","${hmusterForm.infor_Flag}");
		map.put("tabID","${hmusterForm.tabID}");
		map.put("isGroupPoint","${hmusterForm.isGroupPoint}");
		map.put("groupPoint","${hmusterForm.groupPoint}");
		map.put("isGroupPoint2","${hmusterForm.isGroupPoint2}");
		map.put("groupPoint2","${hmusterForm.groupPoint2}");
		map.put("zeroPrint","${hmusterForm.zeroPrint}");
		map.put("column","${hmusterForm.column}");
		map.put("dataarea","${hmusterForm.dataarea}");
		map.put("dbpre","${hmusterForm.dbpre}");
		map.put("history","${hmusterForm.history}");
		map.put("year","${hmusterForm.year}");
		map.put("month","${hmusterForm.month}");
		map.put("count","${hmusterForm.count}");
		map.put("printGrid","${hmusterForm.printGrid}");
		map.put("modelFlag","${hmusterForm.modelFlag}");
		map.put("paperRows","${hmusterForm.paperRows}");
		map.put("pageRows","${hmusterForm.pageRows}");
		map.put("emptyRow","${hmusterForm.emptyRow}");
		map.put("showPartJob","${hmusterForm.showPartJob}");
		map.put("selectedPoint","${hmusterForm.selectedPoint}");
		map.put("toScope","${hmusterForm.toScope}");
		map.put("fromScopt","${hmusterForm.fromScope}");
		hashvo.put("excelType",type);
		map.put("exce","excel"); 
	    Rpc({functionId:'0550000009',success:showExcel},map); 
	</logic:equal>
}
function goto(page)
{
	
	if(page=='1')
	{
	    document.hmusterForm.historyRecord.value="1";
	    document.hmusterForm.isReData.value="0";
	}
	document.hmusterForm.currpage.value=page;
	hmusterForm.action="/general/muster/hmuster/select_muster_name.do?b_next2=next&operateMethod=next"+
			"&checkflag=${hmusterForm.checkflag}&isReData=${hmusterForm.isReData}"+
			"&currpage="+page+"&filterByMdule=${hmusterForm.filterByMdule}"+
			"&combineField=${hmusterForm.combineField}&closeWindow=${hmusterForm.closeWindow} "+
			"&isPrint=${hmusterForm.isPrint}&kqtable=${hmusterForm.kqtable}&historyRecord=1";
	hmusterForm.submit();
}

function gotos()
{
	if(document.getElementsByName("pageSelect")){
		if(document.getElementsByName("pageSelect")[0].value=='')
		{ 
		  alert("<bean:message key='inform.inforcheck.input.page'/>");
		  return;
		}
		if(!checkNUM1(document.getElementsByName("pageSelect")[0]))
			return;
		
		document.hmusterForm.currpage.value=document.getElementsByName("pageSelect")[0].value;
		document.hmusterForm.action="/general/muster/hmuster/select_muster_name.do?b_next2=next&operateMethod=next"+
		"&checkflag=${hmusterForm.checkflag}&isReData=${hmusterForm.isReData}"+
		"&currpage="+document.hmusterForm.currpage.value+"&filterByMdule=${hmusterForm.filterByMdule}"+
		"&combineField=${hmusterForm.combineField}&closeWindow=${hmusterForm.closeWindow} "+
		"&isPrint=${hmusterForm.isPrint}&kqtable=${hmusterForm.kqtable}&historyRecord=1";
		document.hmusterForm.submit();
	}
	
}
function IsDigit() { 
    return ((event.keyCode >= 47) && (event.keyCode <= 57)); 
} 
function returnFirst(){
   	self.parent.location= "/general/tipwizard/tipwizard.do?br_employee=link";
}

 function openSelfInfo(url)
{//点击姓名查看详细信息统一使用window.open 不区分浏览器
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(url+"`width="+(window.screen.width-10)+"`height="+(window.screen.height-90));
    /* if(Ext.isIE){
    var return_vo= window.showModalDialog(iframe_url,"", 
              "dialogWidth="+window.screen.width+"px;dialogHeight="+window.screen.height+"px;resizable=yes;scroll=no;status=no;");
    }else{ */
	      window.open(iframe_url,"_blank",'toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=yes,width='+window.screen.width+'px,height='+window.screen.heigh+'px');
    //}
} 

function closeWin(type)
{
 if(type=='1')
    window.parent.close();
 else if(type=='2')
	 window.open("","_top").close();//人事异动iframe 调用高级花名册 window.close()不生效 
    //window.close();
 else if(type=='3')
 {
    window.location.href="/templates/index/portal.do?b_query=link";
 }else if(type=='4')
 {
	window.location.href="/general/muster/hmuster/select_muster_name.do?br_next_singleTable=next";
 }else if(type=='6')
	window.open("about:blank","_top").close();//所得税管理调用
}


function returnMobile(){
	window.location.href="/phone-app/app/hroster.do?b_query=link&a_code=&dbpre=&sortid=&flag=2";
}

function test(nbase,a0100,a0101){
	//alert(nbase+a0100+"   ${hmusterForm.cardid}");
	window.location.href="/general/inform/synthesisbrowse/synthesiscard.do?b_search=link&userbase="+nbase+"&a0100="+a0100+"&inforkind=1&tabid=${hmusterForm.cardid}&multi_cards=-1&isMobile=1";
}
function jinduo(){
	var x=document.body.clientWidth/2-300;
    var y=document.body.clientHeight/2-125;
	var waitInfo;
	waitInfo=eval("wait");
	waitInfo.style.top=y;
	waitInfo.style.left=x;
	waitInfo.style.display="block";
}
var timecount=0;
//加载打印插件
function loadPrint(){
    var userAgent=navigator.userAgent;
    var isOpera = userAgent.indexOf("Opera") > -1;
    //判断ie浏览器方法不对   wangb 20190316
    if (!getBrowseVersion()/*!(userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1 && !isOpera)*/) {//非IE浏览器不支持打印预演 changxy 20160831
           alert("此功能需要插件支持，请在IE浏览器下使用此功能!");
           return ;
    }
    
   //liuy 2015-4-15 8740：高级花名册的打印预演锁没有控制住，登记表在没有锁的情况下点击打印预演会提示，不能使用，但是高级花名册打印预演可以用 begin
   <%if(license.equals("0")){%>
   	   <%if(usedday>=30&&usedday<=40){ %>
   	     alert('<bean:message key='label.test.first.stage'/>');
   	     return;
   	   <%}else if(usedday>40){%>
   	     alert('<bean:message key='label.test.second.stage'/>');
   	     return;
   <%}}%>
   //liuy 2015-4-15 end
   jinduo();
   /* 标识：2668 提示信息修改 xiaoyun 2014-6-18 start */
   //document.getElementById("hlw").innerHTML="正在加载打印插件，请稍候...";
   document.getElementById("hlw").innerHTML="正在加载数据，请稍候...";
   /* 标识：2668 提示信息修改 xiaoyun 2014-6-18 end */
   setTimeout("dg_init()",1000);
}
function dg_init()
{
   try
   {
   	  if(!AxManager.setup("Hmustercab", "MusterPreview1", 0, 0, dg_init, AxManager.musterpkgName))
      	return false;
      initMuster();
      showMuster();
   }catch(e1)
   {
   	  var waitInfo;
	  waitInfo=eval("wait");
      waitInfo.style.display="none";
      alert("插件下载失败，请查看系统用户的权限或者插件是否被禁用！");
   }
}

/**
 * 弹出雷达图	
 */
function openAbilityDialog(a0100,planId){     
   var strurl="/performance/perAnalyse.do?b_contrastAnalyse=query`opt=1`a0100="+a0100+"`planIds="+planId+"`plan_id="+planId+"`busitype=1`chartHeight=550`chartWidth=600`lastplan=0";
   var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
   var dw=700,dh=450,dl=(screen.width-dw)/2,dt=(screen.height-dh)/3;
   window.showModalDialog(iframe_url,"","dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth=650px;dialogHeight=600px;resizable=no;status=no;");  
}

</script>
<script type="text/javascript">
			

//获取元素的横坐标 
function getLeft(e){ 
var offset=e.offsetLeft; 
if(e.offsetParent!=null) offset+=getLeft(e.offsetParent); 
return offset; 
} 			

Ext.onReady(function(){
	document.getElementsByTagName('body')[0].style.cssText="visibility: visible;";
	var returnflag="${hmusterForm.returnflag}";//mobile
	var modelFlag="${hmusterForm.modelFlag}";
	var isPrint="${hmusterForm.isPrint}";
	var closeWindow="${hmusterForm.closeWindow}";
	var infor_Flag="${hmusterForm.infor_Flag}";
	var isCloseButton="${hmusterForm.isCloseButton}";
	//------
	var showbuttons = "<%=showbuttons%>";
	var showreturn = "<%=showreturn%>";
	if(showreturn&&showreturn=='1')
		closeWindow='5';
	//------
	
	var hRoster=Ext.create('Ext.button.Button',{
							width:50,
					       // height:'90%',
							text:'取数',
							handler:function(){
								outStyleHRoster();
							}
	});
	
	var printView=Ext.create('Ext.button.Button',{
					        width:80,
					      //  height:'90%',
					        text:'打印预览',
					        icon:'/images/print.gif',
					        handler:function(){
					        	loadPrint();
					        }
	});
	var excelMenu=Ext.create('Ext.menu.Menu',{
						   	   width: 170,
							   plain: true,
							   floating: true,  
							   items:[
							       	{
				    				text: '支持office2010及以上版本',
				    				listeners:{
							    				'click':{
							    						  Element:'el',
											        	  fn:function(){
											        	  				excecuteExcel('1');
											        				    }
							    						 }
				    						   }
							       	},
							       	{
				    				text: '支持office2010以下版本',
				    				listeners:{
				    							  'click':{
						    								Element:'el',
										        			fn:function(){
										        							excecuteExcel('0');
										        						 }
				    										}
				    							}	
							       	}
							       ]
		});
	
	
	var outExcel=Ext.create('Ext.button.Button',{
					         width:100,
					        // height:'90%',
					         icon:'/images/outExcel.png',
					         text:'导出Excel',
					         menu:excelMenu
	});
	var outPdf=Ext.create('Ext.button.Button',{
					        width:100,
					       // height:'90%',
					        icon:'/images/outpdf.png',
					        text:'导出PDF',
					        handler:function(){
					        	excecutePDF();
					        }
	});
	
	var closeBtn=Ext.create('Ext.button.Button',{
					        width:50,
					       // height:'90%',
					        text:'关闭'
	});
	var returnBtn=Ext.create('Ext.button.Button',{
					        width:50,
					      //  height:'90%',
					        text:'返回'
	});
	
	var toolbar=Ext.create('Ext.toolbar.Toolbar',{
		 border:0,
		 width:'100%',	
		 height:40
		});	
	if(returnflag=='mobile'){//手机端只显示返回
		toolbar.add({xtype:'button',width:50,height:'90',text:'返回',handler:function(){returnMobile();}});
	}else{
		if(modelFlag!='5'){
		  if(showbuttons!='0'){
			  if(modelFlag!='81'){
					if("0"!="<%=res%>")	{
						toolbar.add(hRoster);
						toolbar.add("-");
					}			
				}
				if(Ext.isIE){
				toolbar.add(printView);
				toolbar.add("-");
				}
				toolbar.add(outExcel);
				toolbar.add("-");
				toolbar.add(outPdf);
				toolbar.add("-");
		  }	
			
		}else{
			if(isPrint=='1'){
				toolbar.add(printView);
				toolbar.add("-");
			}
			toolbar.add(outExcel);
			toolbar.add("-");
			toolbar.add(outPdf);
			toolbar.add("-");
		}
		if(infor_Flag!='81'){
			if(closeWindow=='1'){
				toolbar.add(returnBtn);
				toolbar.add('-');
				returnBtn.setText('关闭');
				returnBtn.on("click", function(){this.closeWin('1');},this);
			}else if(closeWindow=='2'){
				toolbar.add(returnBtn);
				toolbar.add('-');
				returnBtn.setText('关闭');
				returnBtn.on("click", function(){this.closeWin('2');},this);
			}else if(closeWindow=='5'){
				toolbar.add(returnBtn);
				toolbar.add('-');
				returnBtn.setText('返回');
				returnBtn.on("click", function(){this.closeWin('3');},this);
			}else if(closeWindow=='4'){
				toolbar.add(returnBtn);
				toolbar.add('-');
				returnBtn.setText('返回');
				returnBtn.on("click", function(){this.closeWin('4');},this);
			}else if(closeWindow=='6'){
				toolbar.add(returnBtn);
				toolbar.add('-');
				returnBtn.setText('关闭');
				returnBtn.on("click", function(){this.closeWin('6');},this);
			}
			if(isCloseButton=='1'){
				toolbar.add(returnBtn);
				toolbar.add('-');
				returnBtn.setText('关闭');
				returnBtn.on("click", function(){
							if(parent.window.winScope)
							{ 
									parent.window.winScope.closeWin();
							}else{
									window.close();
							}
					},this);
			}
		}
		
		//下拉列表 开始
		/**"[dataName=全部人员库,dataValue=ALL]", "[dataName=在职人员库,dataValue=USR]", 
		"[dataName=离退人员库,dataValue=RET]", "[dataName=调转人员库,dataValue=TRS]", 
		"[dataName=其他人员库,dataValue=OTH]", "[dataName=测试,dataValue=TES]", 
		"[dataName=测试1,dataValue=TST]"***/
		if(infor_Flag=='1'||infor_Flag=='81'){
			
			var dbArr="${hmusterForm.dblist}";
			if(dbArr.length>0&&dbArr.length>2&&modelFlag!='5'){ //bug 51317
				var dbComb=createCombo(dbArr);
				 var items=dbComb.getStore().data.items;
				 	dbComb.setValue("${hmusterForm.dbpre}");
				  toolbar.add(dbComb);
			}
			
			var condList="${hmusterForm.mainParamCondList}";
			if(condList.length>0){
				var label='<bean:write name="hmusterForm" property="mainParamTitle"/>';
				var condListCom=createCombo(condList,label);
				condListCom.setValue("${hmusterForm.conditionBase}");
				toolbar.add('-');
				toolbar.add(condListCom);
			}
		}
		
		
		
		//下拉列表 结束
		
		
		//翻页放置最右侧
		toolbar.add('->');
		toolbar.add('${hmusterForm.turnPage}');
		toolbar.add(' ');
	}
	var mainPanel=Ext.create("Ext.container.Viewport",{
		layout:'fit',
		width:'100%',
		height:'100%',
		renderTo:Ext.getBody()
	});
	var body='${hmusterForm.tableBody}';
	var panel=Ext.create('Ext.panel.Panel',{
						  layout:'fit',
						  id:'musterID',
						  width:'100%',
						  height:'98%',
						  html:'<div style="width:100%;height:98%;position:relative;overflow:auto;margin-left:10px">${hmusterForm.tableTitleTop}'+
						  '${hmusterForm.tableHeader}'+body+'${hmusterForm.tableTitleBottom}</div>'
	});
	panel.addDocked(toolbar,0);
	mainPanel.add(panel);
});

//创建下拉列表
function createCombo(dbArr,label){
	dbArr=dbArr.substring(1,dbArr.length-1).split(", ");//去除前后[ ]
	var arr=[];
	
	for(var i=0;i<dbArr.length;i++){
		if(dbArr[i].indexOf(",")>-1){
			var db=dbArr[i].replace("[","").replace("]","").split(",");
			var obj={
					"dataName":""+db[0].split("=")[1]+"",
					"dataValue":""+db[1].split("=")[1]+""
			};
			arr.push(obj);
		}
	}	
	var label_text=label;
	if(!label){
		label="";
	}
	 
	label="<div style='overflow: hidden;text-overflow: ellipsis;white-space:nowrap;cursor:hand' title='"+label+"'>"+label+"</div>"
	 var dbStore=Ext.create('Ext.data.Store',{
						fields:['dataName','dataValue'],
						data:arr
				});
	 var dbComb=Ext.create('Ext.form.ComboBox',{
						fieldLabel:label,
						labelWidth:label_text?50:0,
						id:label_text?"mainTitle":"dbpre",
						store:dbStore,
						height:24,
						width:label_text?240:100,
						displayField:'dataName',
						valueField:'dataValue',
						queryMode: 'local',
						fieldStyle:'height:20px;',
					    editable:false,
					    listeners:{
					    	select:function(combo,record,index){
					    		onchanges(record.data.dataValue,label?"mainTitle":"dbpre");
					    	}
					    }
				}); 
	 return dbComb
}

</script>


<body  style="overflow: hidden;" >
<html:form action="/general/muster/hmuster/select_muster_name.do?b_next2=next&checkflag=${hmusterForm.checkflag}" >
<div   id="wait" style='position:absolute;top:285;left:120;display:none;width:500px;heigth:250px;z-index:99999'>
		<table border="1" width="50%" cellspacing="0" cellpadding="4" class="table_style" height="100" align="center">
			<tr>
				<td class="td_style" height=24 id="hlw">
					<bean:message key="hmuster.label.wait"/>
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee class="marquee_style" direction="right" width="400" scrollamount="5" scrolldelay="10">
						<table cellspacing="1" cellpadding="0">
							<tr height=8>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
							</tr>
						</table>
					</marquee>
				</td>
			</tr>
		</table>
	</div>
	<div id='Hmustercab'></div>
<html:hidden name="hmusterForm" property="isReData"/>
<html:hidden name="hmusterForm" property="currpage"/>
<html:hidden name="hmusterForm" property="filterByMdule"/>
<html:hidden name="hmusterForm" property="combineField"/>
<html:hidden name="hmusterForm" property="closeWindow"/> 
<html:hidden name="hmusterForm" property="isPrint"/>
<html:hidden name="hmusterForm" property="kqtable"/>
<html:hidden name="hmusterForm" property="historyRecord"/>
<script language="JavaScript">	
document.body.onbeforeunload=function(){ 
	checkprint = "1";
}
window.focus();
</script>
</html:form>
</body>