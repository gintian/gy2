<%@ page contentType="text/html; charset=UTF-8"%>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7">
</head>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.hjsj.sys.ConstantParamter"%>
<%@ page import="com.hrms.hjsj.utils.Sql_switcher"%>
<%@ page import="com.hrms.struts.constant.SystemConfig,com.hjsj.hrms.utils.PubFunc"%>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient,com.hjsj.hrms.actionform.gz.gz_accounting.GzReportForm"%>
<script type='text/javascript' src='../../../ext/ext6/ext-all.js'></script>
<script type='text/javascript' src='../../../ext/ext6/locale-zh_CN.js' ></script>
<script type='text/javascript' src='../../../ext/rpc_command.js'></script>
<link rel="stylesheet" href="../../../general/muster/hmuster/css1.css" type="text/css">
<link rel="stylesheet" href="/css/css1_brokenline.css" type="text/css">
<script language="JavaScript" src="/js/function.js"></script>
<script language="javascript" src="/general/sys/hjaxmanage.js"></script>
<link rel='stylesheet' href='../../../ext/ext6/resources/ext-theme.css' type='text/css' />
<html>

 
<%
	// 在标题栏显示当前用户和日期 2004-5-10 
	String userName = null;
	String css_url = "/css/css1.css";
	UserView userView = (UserView) session.getAttribute(WebConstant.userView);
	String Bosflag = "";
	if (userView != null) {
		userName=userView.getUserName();
		Bosflag = userView.getBosflag();
	}
	int dbtype=Sql_switcher.searchDbServer();
	String appdate=ConstantParamter.getAppdate(userName);
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
    String subModuleId=request.getParameter("subModuleId");
    GzReportForm gzReportForm = (GzReportForm)session.getAttribute("gzReportForm");
    String encryptParam="encryptParam="+PubFunc.encrypt("checksalary="+gzReportForm.getChecksalary()+
        "&salaryid="+gzReportForm.getSalaryid()+"&tabid="+gzReportForm.getTabid()+"&subModuleId="+subModuleId);
    String ajaxEncryptParam = "encryptParam="+PubFunc.encrypt("checksalary="+gzReportForm.getChecksalary()+
        "&salaryid="+gzReportForm.getSalaryid()+"&tabID="+gzReportForm.getTabid()+"&a_code="+gzReportForm.getA_code()+
        "&filterWhl="+gzReportForm.getFilterWhl());
%>
<style>
body {  
	/*background-color:#E1F1FB;*/
	font-size: 12px;
	margin:4 0 0 4;
}


.x-window-header-default-top{
	border-top-left-radius: 5px;
    border-top-right-radius: 5px;
    border-bottom-right-radius: 0px;
    border-bottom-left-radius: 0px;
    background-color: #ffffff;
    padding: 4px 5px 0px;
    border-width: 1px 1px 1px 0px;
    border-style: solid;
}
.x-message-box .x-window-body{
    background-color: #fafbfd;
    border-width: 0;
}
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
	width:expression(document.body.scrollWidth-20); 
	height:expression(document.body.clientHeight-20);//添加高度防止表格内容遮挡
	white-space:nowrap;
}
#scroll_box {
 	BACKGROUND-COLOR: #FFFFFF;
	border: 1px solid #EEEEEE; 
	height:expression(document.body.clientHeight-50);
	width:expression(document.body.clientWidth-20);
}

/* .x-window-header-default-top {
    border-top-left-radius: 5px;
    border-top-right-radius: 5px;
    border-bottom-right-radius: 0px;
    border-bottom-left-radius: 0px;
    background-color: #ffffff;
    padding: 4px 5px 0px;
    border-width: 1px 1px 0px;
    border-style: solid;
} */
.x-tool-close{
    background: url("/ext/ext6/resources/images/tools/tool-sprites.gif") ;
    width:15px !important;
    height:15px !important;
}


</style>
<script language='javascript'>
var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
var isEdge = userAgent.indexOf("Edge") > -1;
var checkprint = "1";
function initMuster(){
      var aurl="<%=url_p%>";
      var DBType="<%=dbtype%>";
      var UserName="<%=userName%>";  
      
      var obj = document.getElementById('MusterPreview1');      
      obj.SetURL(aurl);
      obj.SetDBType(DBType);
      obj.SetUserName(UserName);
      obj.SetBizDate("<%=appdate%>");  // 设置业务日期,和BS一致
      obj.SetUserFullName("<%=userFullName%>");
      obj.SetHrpVersion("<%=version%>");
      obj.SetTrialDays("<%=usedday%>","30");
      checkprint="2";
}
function showMuster(){
      if(!AxManager.setup("Hmustercab", "MusterPreview1", 0, 0, showMuster, AxManager.musterpkgName))
          return false;
      initMuster();
      var obj = document.getElementById('MusterPreview1');      
      var a_code = "${gzReportForm.a_code}";
      a_code = a_code!="null"?a_code:"";
      a_code = a_code.length>2?a_code.substring(2):"";
          
      var condid="${gzReportForm.condid}";
      condid = condid=="all"?"":condid;
      if(checkprint=="2"){
     	obj.SetParams("${gzReportForm.musterPreviewPluginParams}");
     	try { obj.SetJSessionId(AxManager.getJSessionId()); } catch(err) {}
      	obj.ShowMusterModal();
      	var returnvalue = obj.GetParams();
       	var chkrefresh = "false";
      	if(returnvalue!=null&&returnvalue.length>1){
      		if(returnvalue.indexOf("<DataChanged>")!=-1)
      			chkrefresh = returnvalue.substring(returnvalue.indexOf("<DataChanged>")+13,returnvalue.indexOf("</DataChanged>"));
      		if(returnvalue.indexOf("<FilterID>")!=-1){
      			condid = returnvalue.substring(returnvalue.indexOf("<FilterID>")+10,returnvalue.indexOf("</FilterID>"));
      			condid=condid!=null&&condid.length>0?condid:"all";
      			condid=condid!=null&&condid=="0">0?"all":condid;
      		}
      		if(returnvalue.indexOf("<CurOrgId>")!=-1){
      			a_code = returnvalue.substring(returnvalue.indexOf("<CurOrgId>")+10,returnvalue.indexOf("</CurOrgId>"));
      		}
      	}
      	if(chkrefresh=='True'){
	 		var strurl="/gz/gz_accounting/report/open_gzbanner.do?b_report=link";
	 		strurl+="&reset=1&<%=encryptParam%>&opt=int";
	 		strurl+="&a_code="+a_code+"&condid="+condid;
	 		gzReportForm.action=strurl;
	 		gzReportForm.submit();
	 	}
	 }
}
function IsDigit() { 
    return ((event.keyCode >= 47) && (event.keyCode <= 57)); 
} 

function showFieldList(outparamters)
{
    var res=Ext.decode(outparamters.responseText);// 
    Ext.MessageBox.close();
    var url=res.url;
	if(res.succeed){
		if(!res.errorMsg){
			window.location.target="_blank";
			window.location.href="/servlet/vfsservlet?fromjavafolder=true&fileid="+url;
		}else{
			Ext.showAlert(res.errorMsg);
		}
	}else{
		Ext.showAlert(res.message);
	}
	
}

function showExcel(outparamters)
{
   var res=Ext.decode(outparamters.responseText);
   Ext.MessageBox.close();
   var outName=res.outName;
	if(res.succeed){
		if(!res.errorMsg){
			window.location.target="_blank";
			window.location.href="/servlet/vfsservlet?fromjavafolder=true&fileid="+outName;
		}else{
			Ext.showAlert(res.errorMsg);
		}
	}else{
		Ext.showAlert(res.message);
	}
}

function excecutePDF(){
	//var hashvo=new ParameterSet();
	var hashvo=new HashMap();
	hashvo.put("infor_Flag","salary");
    hashvo.put("archive","${gzReportForm.archive}");
	hashvo.put("modelFlag","salary");
	hashvo.put("isAutoCount","${gzReportForm.isAutoCount}");
	hashvo.put("pageRows","${gzReportForm.pageRows}");
	hashvo.put("topDateTitleMap","${gzReportForm.topDateTitleMap}");
	hashvo.put("printGrid","${gzReportForm.printGrid}");
	hashvo.put("zeroPrint","${gzReportForm.zeroPrint}");
	hashvo.put("emptyRow","${gzReportForm.emptyRow}");
	hashvo.put("column","${gzReportForm.column}");
	hashvo.put("dataarea","${gzReportForm.dataarea}");
	hashvo.put("pix","${gzReportForm.pix}");
	hashvo.put("columnLine","${gzReportForm.columnLine}");
	hashvo.put("bosdate","${gzReportForm.bosdate}");	
	hashvo.put("boscount","${gzReportForm.boscount}");
	hashvo.put("groupPoint","${gzReportForm.groupPoint}");
    hashvo.put("model","${gzReportForm.model}");
    hashvo.put("condid","");
    //hashvo.put("condid","${gzReportForm.condid}");//condid 过滤条件 临时表内数据已筛选 无需再次过滤数据
    hashvo.put("exce","PDF");
    hashvo.put("checksalary","${gzReportForm.checksalary}");
	hashvo.put("salaryid","${gzReportForm.salaryid}");
	hashvo.put("tabID","${gzReportForm.tabid}");
	hashvo.put("a_code","${gzReportForm.a_code}");
	hashvo.put("filterWhl","${gzReportForm.filterWhl}");
	var In_paramters="exce=PDF&<%=ajaxEncryptParam%>";  
	Ext.MessageBox.wait("数据正在导出，请稍候...", "等待");
	//var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showFieldList,functionId:'0550000005'},hashvo);
	Rpc({functionId:'0550000005',async:true,success:showFieldList,scope:this},hashvo);  
}


function excecuteExcel(type){
	var hashvo=new HashMap();
	hashvo.put("infor_Flag","salary");
	hashvo.put("archive","${gzReportForm.archive}");
	hashvo.put("isGroupPoint","0");
    hashvo.put("groupPoint","${gzReportForm.groupPoint}");
	hashvo.put("zeroPrint","${gzReportForm.zeroPrint}");
	hashvo.put("column","0");
	hashvo.put("dbpre","");
	hashvo.put("history","0");
	hashvo.put("topDateTitleMap","${gzReportForm.topDateTitleMap}");
	hashvo.put("year","");
	hashvo.put("month","");
	hashvo.put("count","");
	hashvo.put("printGrid","${gzReportForm.printGrid}");
	hashvo.put("modelFlag","salary");
	hashvo.put("isAutoCount","${gzReportForm.isAutoCount}");
	hashvo.put("pageRows","${gzReportForm.pageRows}");
	hashvo.put("emptyRow","${gzReportForm.emptyRow}");
	hashvo.put("column","${gzReportForm.column}");
	hashvo.put("dataarea","${gzReportForm.dataarea}");
	hashvo.put("pix","${gzReportForm.pix}");
	hashvo.put("columnLine","${gzReportForm.columnLine}");
	hashvo.put("model","${gzReportForm.model}");
	hashvo.put("condid","");//condid 过滤条件 临时表内数据已筛选 无需再次过滤数据
	hashvo.put("checksalary","${gzReportForm.checksalary}");
	hashvo.put("salaryid","${gzReportForm.salaryid}");
	hashvo.put("tabID","${gzReportForm.tabid}");
	hashvo.put("bosdate","${gzReportForm.bosdate}");	
	hashvo.put("boscount","${gzReportForm.boscount}");
	hashvo.put("a_code","${gzReportForm.a_code}");
	hashvo.put("filterWhl","${gzReportForm.filterWhl}");	
	hashvo.put("excelType",type);			
	hashvo.put("exce","excel");
	 //var In_paramters="exce=excel&<%=ajaxEncryptParam%>";
	 Ext.MessageBox.wait("数据正在导出，请稍候...", "等待");
	// var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showExcel,functionId:'0550000009'},hashvo);
	  Rpc({functionId:'0550000009',async:true,success:showExcel,scope:this},hashvo);   
	
}




function goto(page){
	document.gzReportForm.currpage.value=page;
	gzReportForm.action="/gz/gz_accounting/report/open_gzbanner.do?b_report=link&reset=2&<%=encryptParam%>&a_code=${gzReportForm.a_code}&condid=${gzReportForm.condid}";
	gzReportForm.submit();
}

function gotos()
{
	if(document.getElementsByName("pageSelect")){
		if(document.getElementsByName("pageSelect")[0].value=='')
		{ 
		  alert('必须输入页码');
		  return;
		}		
		if(!checkNUM1(document.getElementsByName("pageSelect")[0]))
			return;
		document.gzReportForm.currpage.value=document.getElementsByName("pageSelect")[0].value;
		gzReportForm.action="/gz/gz_accounting/report/open_gzbanner.do?b_report=link&reset=2&<%=encryptParam%>&a_code=${gzReportForm.a_code}&condid=${gzReportForm.condid}";
		document.gzReportForm.submit();
	}
}
function search_bycond(obj){
	var strurl="/gz/gz_accounting/report/open_gzbanner.do?b_report=link&reset=1&<%=encryptParam%>&opt=int&a_code=${gzReportForm.a_code}&condid="+obj;
	gzReportForm.action=strurl;
	gzReportForm.submit();
}
function setZeroPrint(zerovalue){
	document.gzReportForm.zeroPrint.value=zerovalue;
	var strurl="/gz/gz_accounting/report/open_gzbanner.do?b_report=link&reset=2&<%=encryptParam%>&opt=int&a_code=${gzReportForm.a_code}&condid=${gzReportForm.condid}";
	gzReportForm.action=strurl;
	gzReportForm.submit();
}
function setPrintGrid(gridvalue){
	document.gzReportForm.printGrid.value=gridvalue;
	var strurl="/gz/gz_accounting/report/open_gzbanner.do?b_report=link&reset=2&<%=encryptParam%>&opt=int&a_code=${gzReportForm.a_code}&condid=${gzReportForm.condid}";
	gzReportForm.action=strurl;
	gzReportForm.submit();
}
function setPageRow(){
	var strurl="/gz/gz_accounting/report/setpagerows.jsp?pageRow=${gzReportForm.pageRows}&isAutoCount=${gzReportForm.isAutoCount}";
	Ext.create('Ext.window.Window',{
		title:'<font color="black">设置每页行数</font>',
		style:'backgroundColor:#FFFFFF',
		id:'pageRowId',
		width:300,
		height:140,
		resizable:false,  
		html:'<iframe  name="childFrame" id="childFrame" style="border-top-width: 0px; border-left-width: 0px;background-color:#ffffff" height="100%" width="100%" src='+strurl+'></iframe>',
		listeners:{
			'close':function(){
				if(typeof(returnValue)!='undefined')//子窗口给父窗口设置的全局变量 直接用
				{
					document.gzReportForm.pageRows.value=returnValue;
					var strurl="/gz/gz_accounting/report/open_gzbanner.do?b_report=link&reset=2&<%=encryptParam%>&opt=int&a_code=${gzReportForm.a_code}&condid=${gzReportForm.condid}";
					gzReportForm.action=strurl;
					gzReportForm.submit();
				}
			}
		}
		}).show();

	//var strurl="/gz/gz_accounting/report/setpagerows.jsp?pageRow=${gzReportForm.pageRows}&isAutoCount=${gzReportForm.isAutoCount}";
	//var flag=window.showModalDialog(strurl,"","dialogWidth=300px;dialogHeight=140px;resizable=no;scroll=no;status=no;");  
	//	var feature="width=300;height=140;location=no;menubar=no,toolbar=no,location=no,scrollbars=no,status=no,modal=yes;resizable=no";
	//var win = window.open(strurl,"_blank",feature);
	//if(flag.returnValue){
	//	document.gzReportForm.pageRows.value=flag.returnValue;
	//	var strurl="/gz/gz_accounting/report/open_gzbanner.do?b_report=link&reset=2&<%=encryptParam%>&opt=int&a_code=${gzReportForm.a_code}&condid=${gzReportForm.condid}";
	//	gzReportForm.action=strurl;
	//	gzReportForm.submit();
	//}    
}
function sortSet(){
	var thecodeurl ="/gz/sort/sorting.do?b_query=link&checksalary=${gzReportForm.checksalary}&flag=6&salaryid=${gzReportForm.salaryid}&sortitem="+getEncodeStr("${gzReportForm.sortitem}"); 
	Ext.create('Ext.window.Window',{
		title:'<font color="black">排序</font>',
		style:'backgroundColor:#FFFFFF',
		id:'sortWindowId',
		width:540,
		height:370,
		resizable:false,
		html:'<iframe  name="childFrame" id="childFrame" style="border-top-width: 0px; border-left-width: 0px;background-color:#ffffff" height="100%" width="100%" src='+thecodeurl+'></iframe>',
		listeners:{
			'close':function(){
				if(typeof(return_Value)!='undefined'){
					return_Value=return_Value!='not'?return_Value:'';
					var map=new HashMap();
					map.put("sortitem",return_Value);
					map.put("tabid","${gzReportForm.tabid}");
					Rpc({functionId:'0550000013',async:false,scope:this},map);   
					var strurl="/gz/gz_accounting/report/open_gzbanner.do?b_report=link&<%=encryptParam%>";
					strurl+="&opt=int";
					strurl+="&a_code=${gzReportForm.a_code}&condid=${gzReportForm.condid}&reset=1&gz_module=${gzReportForm.gz_module}";
					strurl+="&year=${gzReportForm.year}&month=${gzReportForm.month}&count=${gzReportForm.count}";
					gzReportForm.action=strurl;
					gzReportForm.submit();
				}
			}
		}
	}).show();
}
function calsortSet(){
	var hashvo=new HashMap();
	hashvo.put("sortitem","");
	hashvo.put("tabid","${gzReportForm.tabid}");
	Rpc({functionId:'0550000013',async:false,scope:this},hashvo);   
	var strurl="/gz/gz_accounting/report/open_gzbanner.do?b_report=link&reset=1&<%=encryptParam%>";
	strurl+="&opt=int";
	strurl+="&a_code=${gzReportForm.a_code}&condid=${gzReportForm.condid}";
	gzReportForm.action=strurl;
	gzReportForm.submit();
}

function closeWindow()
{ 
	if(parent.window.winScope){ 
			parent.window.winScope.closeWin();
	}else if(parent.window){
		parent.window.close()
	}else{ 
			window.close();
	}
}
Ext.onReady(function(){
	if(document.getElementsByTagName('body'))
		document.getElementsByTagName('body')[0].style.cssText="visibility: visible;";
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
         	
    var printView=Ext.create('Ext.button.Button',{
					    	icon:'/images/print.gif',
					        text: '打印预览',
					        listeners:{
					        	'click':{
					        			Element:'el',
					        			fn:function(){
					        			     showMuster();
					        			}
					        	}
					        }
    });     	
         	
    var excelBtn=Ext.create('Ext.button.Button',{
					    	icon:'/images/outExcel.png',
					        text: '<bean:message key="general.inform.muster.output.excel"/>',
					        menu:excelMenu
    				});
     var pdfBtn=Ext.create('Ext.button.Button',{
					    	 icon:'/images/outpdf.png',
						     text: '<bean:message key="edit_report.outPDF"/>',
						     listeners:{
						        	'click':{
						        			Element:'el',
						        			fn:function(){
						        				excecutePDF();
						        			}
						        	}
						        }
    				});     	
     var closeBtn=Ext.create('Ext.button.Button',{
					         width:50,
					         text:'关闭',
					         listeners:{
								        	'click':{
								        			Element:'el',
								        			fn:function(){
								        				closeWindow();
								        			}
								        	}
								        }
    				});     	
         	
	var toolbar=Ext.create('Ext.toolbar.Toolbar',{
			 border:0,
			 width:'100%',	
			 height:40
			});			
	if(Ext.isIE){
		toolbar.add(printView);
		toolbar.add("-");
	}
	toolbar.add(excelBtn);
	toolbar.add("-");
	toolbar.add(pdfBtn);
	toolbar.add("-");
	toolbar.add(closeBtn);
	toolbar.add("-");
	
	 if("${gzReportForm.checksalary}"=='salary'&&"${gzReportForm.model}"!='1'){
		
		 var label='<bean:message key="label.gz.condfilter"/>';
		 var condlist="${gzReportForm.condlist}";
		if(condlist.length>0){
			var combo=createCombo(condlist,label);
			 var items=combo.getStore().data.items;
			for(var i=0;i<items.length;i++){
				if(items[i].data.dataValue=="${gzReportForm.condid}"){
					combo.setValue(items[i].data.dataName);
				}
			}
		  toolbar.add(combo);
		}
		
	} 
	
	//翻页工具条
	toolbar.add('->');
	toolbar.add('${gzReportForm.turnPage}');
	toolbar.add(' ');
	var body='${gzReportForm.html}';
	var panel=Ext.create('Ext.panel.Panel',{
						layout:'fit',
						width:'100%',
						height:'100%',
						html:'<div style="width:100%;height:98%;position:relative;overflow:auto;">'+body+'</div>'
	});
	panel.addDocked(toolbar,0);//0  上方 
	Ext.create('Ext.container.Viewport',{
				layout:'fit',
				width:'100%',
				height:'100%',
				renderTo:Ext.getBody(),
				items:[panel]
	});
	
});

//创建下拉列表
function createCombo(dbArr,label){
	dbArr=dbArr.substring(1,dbArr.length-1).split(", ");//去除前后[ ]
	var arr=[];
	for(var i=0;i<dbArr.length;i++){
		
		var db=dbArr[i].replace("[","").replace("]","").split(",");
		var obj={
				"dataName":""+db[0].split("=")[1]+"",
				"dataValue":""+db[1].split("=")[1]+""
		};
		arr.push(obj);
	}	
	

	 var dbStore=Ext.create('Ext.data.Store',{
						fields:['dataName','dataValue'],
						data:arr
				});
	 var dbComb=Ext.create('Ext.form.ComboBox',{
						fieldLabel:label,
						labelWidth:label.length>0?60:0,
						store:dbStore,
						width:240,
						displayField:'dataName',
						valueField:'dataValue',
						queryMode: 'local',
					    editable:false,
					    listeners:{
					    	select:function(combo,record,index){
					    		search_bycond(record.data.dataValue);
					    	}
					    }
				}); 
	 return dbComb;
}

</script>
<body  style="margin:0 0 0 0 ;overflow: hidden;height:100% ">
<html:form action="/gz/gz_accounting/report/open_gzbanner.do?b_report=next">
</table>
<input type='hidden' id="currpage" name='currpage' value='${gzReportForm.currpage}' /> 
<input type='hidden' id="zeroPrint" name='zeroPrint' value='${gzReportForm.zeroPrint}'/>
<input type='hidden' id="printGrid" name='printGrid' value='${gzReportForm.printGrid}'/>
<input type='hidden' id="pageRows" name='pageRows' value='${gzReportForm.pageRows}'/>
<input type='hidden' id="sortitem" name="sortitem" value='${gzReportForm.sortitem}'/>
<html:hidden name="gzReportForm" property="archive"/>
</html:form>
<div id='Hmustercab' style="display: none;"></div>
</body>
<script language="JavaScript">
var waitInfo=parent.document.getElementById("wait");
if(waitInfo!=null)
	waitInfo.style.display="none";
document.body.onbeforeunload=function(){ 
	checkprint="1";
}
</script>
</html>