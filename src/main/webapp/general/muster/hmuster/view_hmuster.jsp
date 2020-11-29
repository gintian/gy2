<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.actionform.general.muster.hmuster.HmusterForm"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.hjsj.utils.Sql_switcher"%>
<%@ page import="com.hrms.hjsj.sys.ConstantParamter"%>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<link rel="stylesheet" href="/css/css1_brokenline.css" type="text/css">
<script language="JavaScript" src="/js/function.js"></script>
<script language="javascript" src="/general/sys/hjaxmanage.js"></script>
<script type='text/javascript' src='../../../module/utils/js/template.js'></script>
<link rel="stylesheet" href="/css/css1_brokenline.css" type="text/css">
<%
	// 在标题栏显示当前用户和日期 2004-5-10 
	
	String userName = null;
	String css_url = "/css/css1.css";
	UserView userView = (UserView) session
			.getAttribute(WebConstant.userView);
	String a_code="UN";
	
	if (userView != null) {
		css_url = userView.getCssurl();
		if (css_url == null || css_url.equals(""))
			css_url = "/css/css1.css";
		userName=userView.getUserName();
	}
	String superUser = "0";
	if(userView!=null){
		if (userView.isSuper_admin()){
			superUser = "1";
		}else {
			a_code=userView.getManagePrivCode()+userView.getManagePrivCodeValue();
		}
	}
	int dbtype=Sql_switcher.searchDbServer();
	String appdate=ConstantParamter.getAppdate(userName);
	String aurl = (String) request.getServerName();
	String port = request.getServerPort() + "";
	String url_p = SystemConfig.getCsClientServerURL(request);
	  EncryptLockClient lockclient=(EncryptLockClient)session.getServletContext().getAttribute("lock");
   String license=lockclient.getLicenseCount();
   int version=userView.getVersion();
   if(license.equals("0"))
        version=100+version;
   int usedday=lockclient.getUseddays();
   String bosflag=userView.getBosflag();   
   String showreturn=request.getParameter("showreturn");
   //zxj 返回按钮显示控制
   String returnFlag = "yes";
   if(request.getParameter("return") != null) {
       //从menu进来时带return参数
       returnFlag = (String)request.getParameter("return");
       //记入userView中，供翻页等操作时，判断是否应显示返回
       userView.getHm().put("muster_return", returnFlag);
   }
   else if (userView.getHm().get("muster_return") != null) {
       
       returnFlag = (String)userView.getHm().get("muster_return");
   }
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
	position:absolute;
	top:0;
	left:0.0;
	width:expression(document.body.clientWidth-18);
	height:20;
	background-color:#DEEAF5;
}
#scroll_box {
 	BACKGROUND-COLOR: #FFFFFF;
	border: 1px solid #EEEEEE; 
   	overflow:auto; 
	height:expression(document.body.clientHeight-50);
	width:expression(document.body.clientWidth);
}
.pagebgk{
	position:absolute;
	line-height:0;
	background-color: #FFFFFF;
    border:4px solid #878886;
    border-left-style: solid;
    border-top-style: solid;
	border-top-width: 1px;  
	border-left-width: 1px;	  
    margin-right: auto; 
    margin-left: auto;	
}
.locked_self_t{
	font-size: 14px;
	height:22;
	font-weight: bold;	
	valign:middle;
	left: expression(document.getElementById("tbl-container").scrollLeft-1); /*IE5+ only*/
	top: expression(document.getElementById("tbl-container").scrollTop); /*IE5+ only*/
	position: relative;
	background-color:#FFFFFF;
	z-index: 30;
}
div#tbl-container {
		overflow: auto;
		z-index:-1;/*非ie页面被遮罩无法滚动页面*/
		 
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
var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
var isEdge = userAgent.indexOf("Edge") > -1;
//liuy 2015-3-2 6794：重点人群岗位匹配度中高级花名册打印插件需要时再加载 start
function initMuster(){
      var aurl="<%=url_p%>";
      var DBType="<%=dbtype%>";
      var UserName="<%=userName%>";  
      var obj = document.getElementById('MusterPreview1');

      obj.SetURL(aurl);
      obj.SetDBType(DBType);
      obj.SetUserName(UserName);
      obj.SetBizDate("<%=appdate%>");
      obj.SetHrpVersion("<%=version%>");
      obj.SetTrialDays("<%=usedday%>","30");
}
function showMuster(){
      var obj = document.getElementById('MusterPreview1');      
      	obj.SetParams("${hmusterForm.musterPreviewPluginParams}");
      	try { obj.SetJSessionId(AxManager.getJSessionId()); } catch(err) {}
      	obj.ShowMusterModal();
      	 var waitInfo;
	    waitInfo=eval("wait");
     	waitInfo.style.display="none";
}
//liuy 2015-3-2 end
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
	Ext.MessageBox.close();
	var res=Ext.decode(outparamters.responseText);
	var url=res.url;
	window.location.target="_blank";
	/* if(isEdge){
		window.location.href="/servlet/DisplayOleContent?platform=H5&filename="+url;
	}else{
		window.location.href="/servlet/DisplayOleContent?filename="+url;
	} */
	window.location.href="/servlet/vfsservlet?fromjavafolder=true&fileid="+url;
	
}
function showExcel(outparamters)
{
	Ext.MessageBox.close();
	var res=Ext.decode(outparamters.responseText);
	var outName=res.outName;
	window.location.target="_blank";
	/* if(isEdge){
		window.location.href="/servlet/DisplayOleContent?platform=H5&filename="+outName;
	}else{
		window.location.href="/servlet/DisplayOleContent?filename="+outName;
	} */
	window.location.href="/servlet/vfsservlet?fromjavafolder=true&fileid="+outName;
}  
function outStyleHRoster(){
	hmusterForm.action="/general/muster/hmuster/select_muster_name.do?b_next=next&checkflag=1"; 
   	hmusterForm.submit();
}
function onchanges(value,id){
	var dbper,condition;
	if(id=="dbName"){
		 //全部：“”
		 dbper=value;
		if(Ext.getCmp("condition")){
			condition=Ext.getCmp("condition").getSelection().data.dataValue;
			condition=(condition=='全部'?"":condition);
		} 
	}else{
		condition=value;
		if(Ext.getCmp("dbName")){
			dbper=Ext.getCmp("dbName").getSelection().data.dataValue;
			dbper=(dbper=="全部人员库"?"All":dbper);
		}else{
			dbper="${hmusterForm.dbpre}";
		}		
	}
	/* 哈药领导桌面按钮控制 xiaoyun 2014-8-19 start */
	//hmusterForm.action="/general/muster/hmuster/select_muster_name.do?b_view=next&changeDbpre=1&modelFlag=${hmusterForm.modelFlag}&res=1&clears=1&operateMethod=direct&tabID=${hmusterForm.tabID}"; 
	hmusterForm.action="/general/muster/hmuster/select_muster_name.do?b_view=next&dbpre="+dbper+"&condition="+condition+"&changeDbpre=1&modelFlag=${hmusterForm.modelFlag}&isGetData=1&res=1&clears=1&operateMethod=direct&tabID=${hmusterForm.tabID}&showbuttons=${hmusterForm.showbuttons}";
   	/* 哈药领导桌面按钮控制 xiaoyun 2014-8-19 end */
   	hmusterForm.submit();
}
function excecutePDF()
{
	var hashvo=new HashMap();
	hashvo.put("infor_Flag","${hmusterForm.infor_Flag}");
	hashvo.put("tabID","${hmusterForm.tabID}");
	hashvo.put("isGroupPoint","${hmusterForm.isGroupPoint}");
	hashvo.put("groupPoint","${hmusterForm.groupPoint}");
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
	 var In_paramters="exce=PDF";  
	 Ext.MessageBox.wait("数据正在导出，请稍候...", "等待");
	 Rpc({method:'post',asynchronous:true,functionId:'0550000005',success:showFieldList},hashvo);
	
}


function excecuteExcel(type)
{
	var hashvo=new HashMap();
	hashvo.put("infor_Flag","${hmusterForm.infor_Flag}");
	hashvo.put("tabID","${hmusterForm.tabID}");
	hashvo.put("isGroupPoint","${hmusterForm.isGroupPoint}");
	hashvo.put("groupPoint","${hmusterForm.groupPoint}");
	hashvo.put("zeroPrint","${hmusterForm.zeroPrint}");
	hashvo.put("column","${hmusterForm.column}");
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
	hashvo.put("dataarea","${hmusterForm.dataarea}");
	hashvo.put("excelType",type);
	var In_paramters="exce=excel";  
    hashvo.put("isGroupPoint2","${hmusterForm.isGroupPoint2}");
	hashvo.put("groupPoint2","${hmusterForm.groupPoint2}");
	Ext.MessageBox.wait("数据正在导出，请稍候...", "等待");
	// var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showExcel,functionId:'0550000009'},hashvo);
	 Rpc({method:'post',asynchronous:true,functionId:'0550000009',success:showExcel},hashvo);
	
}
function goto(page)
{
	document.hmusterForm.currpage.value=page;
	document.hmusterForm.submit();
}

function gotos()
{
	if(document.getElementsByName("pageSelect")[0].value=='')
	{ 
	  alert("<bean:message key='inform.inforcheck.input.page'/>");
	  return;
	}
	if(!checkNUM1(document.getElementsByName("pageSelect")[0]))
		return;
	document.hmusterForm.currpage.value=document.getElementsByName("pageSelect")[0].value;
	document.hmusterForm.submit();
}
function IsDigit() { 
    return ((event.keyCode >= 47) && (event.keyCode <= 57)); 
} 
function comback(returnType){
    if(returnType=='1')
	   document.location.href="/general/muster/emp_muster.do?b_query=link&backCurrentPage=1";//liuy 2014-12-26 634：主页花名册，在第二页选择个花名册打开后返回，返回到第一页了
	else if(returnType=='2'){
		<%if("hcm".equalsIgnoreCase(bosflag)){%>
			window.location.href="/templates/index/hcm_portal.do?b_query=link";
		<%}else{%>
	   		window.location.href="/templates/index/portal.do?b_query=link";
		<%}%>
	}
	
}
function openSelfInfo(url)
{
    //update by xiegh on date20171125 修改自助服务-员工信息-员工名册：浏览器兼容问题    
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(url+"&width="+(window.screen.width-10)+"&height="+(window.screen.height-90));
    window.open(iframe_url,'_blank',"width="+window.screen.width+",height="+window.screen.height+",top=0px,left=0px,toolbar=no,location=no,resizable=no,scroll=no,status=no");
}
//liuy 2015-3-2 6794：重点人群岗位匹配度中高级花名册打印插件需要时再加载 start
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
function loadPrint(){ 
   //liuy 2015-3-30 8290：主页，花名册，点击进去后，双击打印预演，偶尔报插件下载失败的错误，进入打印预演点页面设置，偶尔报内存地址错误
   jinduo();
   document.getElementById("hlw").innerHTML="正在加载数据，请稍候...";
   if(!AxManager.setup("Hmustercab", "MusterPreview1", 0, 0, dg_init, AxManager.musterpkgName))
       return false;
   dg_init();
}
function dg_init()
{
   try
   {
      initMuster();
      showMuster();
   }catch(e1)
   {
      timecount=timecount+1;
      if(timecount<10)
      {
          setTimeout("dg_init()",2000);
      }else{
        var waitInfo;
	    waitInfo=eval("wait");
     	waitInfo.style.display="none";
         alert("插件下载失败，请查看系统用户的权限或者插件是否被禁用！");
         return;
      }
   }
}
//liuy 2015-3-2 end

/**
 * 弹出雷达图	
 */
function openAbilityDialog(a0100,planId){     
   var strurl="/performance/perAnalyse.do?b_contrastAnalyse=query`opt=1`a0100="+a0100+"`planIds="+planId+"`plan_id="+planId+"`busitype=1`chartHeight=550`chartWidth=600`lastplan=0";
   var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
   var dw=700,dh=450,dl=(screen.width-dw)/2;dt=(screen.height-dh)/3;
   window.showModalDialog(iframe_url,"","dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth=650px;dialogHeight=600px;resizable=no;status=no;");  
   
}
</script>
<html:form action="/general/muster/hmuster/select_muster_name.do?b_view=next&checkflag=${hmusterForm.checkflag}" >
<html:hidden name="hmusterForm" property="historyRecord"/>
<html:hidden name="hmusterForm" property="isReData"/>
 <script language='javascript' >
 	/* 任务：3692 支持常用查询参数，机构模块，如果只有一页，且不显示按钮、上边距太大的问题 xiaoyun 2014-8-29 start */
 	var turnPage ='${hmusterForm.turnPage}';
 	var showbuttons = "${hmusterForm.showbuttons}";
 	var showreturn = "<%=showreturn%>";
 	var mainParamTitle = "${hmusterForm.mainParamTitle}";
 	var dblist = "${hmusterForm.dblist}";
 	var conditionslist = "${hmusterForm.conditionslist}";
 	var isDbValid = (!dblist || dblist.length==2);
 	var isConVaild = (!conditionslist || conditionslist.length==2);
 	var isShowBtn = (showbuttons=='0');
 	if(!turnPage && isShowBtn && !mainParamTitle && isDbValid && isConVaild) {
 		document.write("<div id=\"tbl-container\" style='position:absolute;top:1;margin-top:-20px;height:100%;width:100%'  >");
 	}else {
	 	document.write("<div id=\"tbl-container\" style='position:absolute;top:1;height:100%;width:100%'  >");
 	}
 	/* 任务：3692 支持常用查询参数，机构模块，如果只有一页，且不显示按钮、上边距太大的问题 xiaoyun 2014-8-29 end */			
		

Ext.onReady(function(){
	document.getElementsByTagName('body')[0].style.cssText="visibility: visible;";
	var excel1=Ext.create('Ext.button.Button',{
						  width:175,
						  text:'支持office2010及以上版本',
						  handler:function(){
							  excecuteExcel('1');
					      }
						});
	var excel2=Ext.create('Ext.button.Button',{
						  width:175,
						  text:'支持office2010以下版本',
						  handler:function(){
							  excecuteExcel('0');
					      }
		});
	
	var menu=Ext.create('Ext.menu.Menu',{
						  width:175,
						  items:[excel1,excel2],
						  plain: true,
						  floating: true,
		});
	var excelBtn=Ext.create('Ext.button.Button',{
							width:120,
							icon:'/images/outExcel.png',
							text:'<bean:message key="general.inform.muster.output.excel"/>',
							menu:menu
		});
	
	var pdfbtn=Ext.create('Ext.button.Button',{
						  width:100,
						  icon:'/images/outpdf.png',
						  text:'<bean:message key="edit_report.outPDF"/>',
						  handler:function(){
							  excecutePDF();
					      }
		});
	var privewBtn=Ext.create('Ext.button.Button',{
						  width:100,
						  icon:'/images/print.gif',
						  text:'打印预览',
						  handler:function(){
							  loadPrint();
					      }
		});
	var returnBtn=Ext.create('Ext.button.Button',{
						  width:80,
						  text:'返回',
						  handler:function(){
							  comback("${hmusterForm.returnType}");
					      }
		});
	
	var toolbar=Ext.create('Ext.toolbar.Toolbar',{
						  width:'100%',
						  height:35
		});
	//var showbuttons="${hmusterForm.showbuttons}";
	var bosflag="<%=bosflag%>";
	var returnFlag="<%=returnFlag%>";
	var mainParamTitle="${hmusterForm.mainParamTitle}";
	var conditionslist="${hmusterForm.conditionslist}";
	var mainParamCondList="${hmusterForm.mainParamCondList}";
	var dblist="${hmusterForm.dblist}";
	if(dblist=='[]'){
		dblist='';
	}
	//查询条件只有全部时，默认不显示
	if(conditionslist!=''&&conditionslist.split(",").length==2){
		conditionslist=''
	}
	if(showbuttons=='1'||showbuttons==''){//showbuttons 为空或者为1 时显示工具栏按钮
		toolbar.add(excelBtn);
		toolbar.add('-');
		toolbar.add(pdfbtn);
		toolbar.add('-');
		if(Ext.isIE){
			toolbar.add(privewBtn);
			toolbar.add('-');
		}
		if(showreturn!='0'){
			if("bi"!=bosflag&&"no"!=returnFlag){
				toolbar.add(returnBtn);
				toolbar.add('-');			
			}
			
		}
	}else{
		if(showreturn!='0'){
			if("bi"!=bosflag&&"no"!=returnFlag){
				toolbar.add(returnBtn);
				toolbar.add('-');			
			}
			
		}
	}
	var dbCombo,condCombo,mianCombo;
	if(showbuttons=='0'){
		if(mainParamTitle==''){
			if(dblist!=''){
				dbCombo=createCombo(dblist,'人员库',"dbName");
				toolbar.add(dbCombo);
			}
			if(conditionslist!=''&&conditionslist.length!=2){
				condCombo=createCombo(conditionslist,'查询条件',"condition");
				toolbar.add(condCombo);
			}
		}else{
			if(mainParamCondList!=''&&mainParamCondList.length!=2){
			    mianCombo=createCombo(mainParamCondList,'查询条件',"condition");
				toolbar.add(condCombo);
			}
			
		}
	}else{
		if(mainParamTitle==''){
			if(conditionslist==''){
				if(dblist!=''){
					dbCombo=createCombo(dblist,'人员库',"dbName");
					toolbar.add(dbCombo);
				}
			}else{
				if(conditionslist!=''){
					if(dblist!=''){
						dbCombo=createCombo(dblist,'人员库',"dbName");
						toolbar.add(dbCombo);
					}
					if(conditionslist!=''&&conditionslist.length!=2){
						condCombo=createCombo(conditionslist,'查询条件',"condition");
						toolbar.add(condCombo);
					}
				}
			}
		}else{
			if(conditionslist!=''&&conditionslist.length!=2){
					 mianCombo=createCombo(mainParamCondList,'<bean:write name="hmusterForm" property="mainParamTitle"/>',"condition");
				toolbar.add(mianCombo);
			}
		}
	}
	
	if(dbCombo){
	  dbCombo.setValue("${hmusterForm.dbpre}");
	}
	if(condCombo){
		condCombo.setValue("${hmusterForm.condition}");
	}
	if(mianCombo){
		mianCombo.setValue("${hmusterForm.condition}");
	}
	
	
	toolbar.add('->');
	toolbar.add('${hmusterForm.turnPage}');
	
	var mainPanel=Ext.create("Ext.container.Viewport",{
		layout:'fit',
		width:'100%',
		height:'100%',
		renderTo:Ext.getBody()
	});
	var body='${hmusterForm.tableBody}';
	var panel=Ext.create('Ext.panel.Panel',{
						  layout:'fit',
						  width:'100%',
						  height:'98%',
						  html:'<div style="width:100%;height:98%;position:relative;overflow:auto;">${hmusterForm.tableTitleTop}'+
						  '${hmusterForm.tableHeader}'+body+'${hmusterForm.tableTitleBottom}</div>'
	});
	panel.addDocked(toolbar,0);
	mainPanel.add(panel);
	
}); 	

//创建下拉列表
function createCombo(dbArr,label,id){
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
	

	 var dbStore=Ext.create('Ext.data.Store',{
						fields:['dataName','dataValue'],
						data:arr
				});
	 var dbComb=Ext.create('Ext.form.ComboBox',{
						fieldLabel:label,
						labelWidth:(label!='人员库')?50:40,
						id:id,		
						store:dbStore,
						width:(label!='人员库')?240:140,
						displayField:'dataName',
						valueField:'dataValue',
						queryMode: 'local',
					    editable:false,
					    listeners:{
					    	select:function(combo,record,index){
					    		onchanges(record.data.dataValue,id);
					    	}
					    }
				});
	 //document.gzReportForm.
	 return dbComb
}
 	
</script>

<div   id="wait" style='position:absolute;top:285;left:120;display:none;width:500px;heigth:250px'>
 
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
<!-- 		    <iframe src="javascript:false" style="position:absolute; visibility:inherit; top:0px; left:0px;width:285px;height:120px;z-index:-1;filter='progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)';">
		    </iframe>	
 -->	</div>
<div id='Hmustercab'></div>
<input type='hidden' name='currpage' value='' />

<script language="JavaScript">
document.body.onbeforeunload=function(){ 
	checkprint = "1";
}
/* if(!getBrowseVersion()){ //兼容非IE浏览器 wangb 20180124
	var printBtn = document.getElementById('print');//非IE浏览器下 打印预演 按钮隐藏
	printBtn.style.display = 'none'; 
} */
</script>
</html:form>
