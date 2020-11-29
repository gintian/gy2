<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.module.recruitment.parameter.actionform.ParameterForm,java.util.ArrayList,com.hrms.struts.taglib.CommonData"%>
<%@page import="com.hrms.struts.constant.SystemConfig,com.hrms.frame.codec.SafeCode"%>
<html>
<hrms:themes></hrms:themes>
  <head>
  <%
        ParameterForm moudleParameterForm = (ParameterForm)session.getAttribute("moudleParameterForm");
        String path=moudleParameterForm.getPath();
        if(SystemConfig.getPropertyValue("webserver").equals("weblogic"))
       {
      	  path=session.getServletContext().getResource("/UserFiles").getPath();//.substring(0);
          if(path.indexOf(':')!=-1)
      	  {
	    	 path=path.substring(1);   
      	  }
     	  else
      	  {
	    	 path=path.substring(0);      
      	  }
          int nlen=path.length();
    	  StringBuffer buf=new StringBuffer();
     	  buf.append(path);
  	      buf.setLength(nlen-1);
   	      path=buf.toString();
   	      path=SafeCode.encode(path);
      }
        UserView userView = (UserView)session.getAttribute(WebConstant.userView);
    	String bosflag = userView.getBosflag();
   %>
<script language="JavaScript" src="/module/utils/js/template.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/module/utils/js/createWindow.js"></script>
<script language="JavaScript" src="/components/tableFactory/tableFactory.js"></script>
<link href="/module/recruitment/css/style.css" rel="stylesheet" type="text/css" />
<link  rel="stylesheet" type="text/css"  href="/module/recruitment/css/newParameterSet.css"/>
  <SCRIPT language="javascript">
  var tablegrid = undefined;
  Ext.onReady(function(){
         <hrms:tableFactory  jsObjName="tablegrid" sqlProperty="str_sql"    constantName="recruitment/orgIntroList"   orderbyProperty="orderbystr" isColumnFilter="false"
     		subModuleId="zp_preparameter_00001" columnProperty="columns" formName="moudleParameterForm">
     	</hrms:tableFactory>    
     	tablegrid.setBorderLayoutRegion("center");
     	//替换内容形式编辑器为下拉框
     	var colums = Ext.getCmp("tablegrid_tablePanel").getColumnManager().getColumns();//获取所有column对象
		Ext.each(colums,function(col){
			if(col.dataIndex == "contenttype"){
				 var store = Ext.create('Ext.data.Store',{
						fields:['dataName','dataValue'],
						data:${moudleParameterForm.contentJson}
				 });
			   	var box = Ext.create('Ext.form.ComboBox', {
			  		width:150,
			  	    store: store,
			  	    autoSelect:true,
			  	    queryMode: 'local',
			  	   	displayField: 'dataName',
			  	    valueField: 'dataValue',
			  	    labelPad:0,
			  	    labelWidth:0,
			  	    listeners:{
				   		blur:{
							fn:function(combox){
								var res = store.find("dataValue",combox.getValue());
								if(res==-1){//无效输入值
									combox.setValue("");
								}
							}
						},change:{
							fn:function(combox,newvalue,oldvalue){
								if(!Ext.isEmpty(Ext.getCmp(id+"box")))
									save(param);
							}
						}
			  		}
			  	});
			  	col.editor = box;
			}
		});
		//屏幕分辨率
     	var screenHeight =  window.screen.height;
     	var screenWidth = window.screen.width;
     	//处理屏幕分辨率过小时页面显示不全问题
     	if(screenHeight * screenWidth <= 1280*768)
         	Ext.getDom("extTable").style.height = "170px" ;
     	tablegrid.renderTo("extTable");
        //generateBox("orgCode");
        //generateBox("contentCode");
  });
  //生成单位介绍指标和内容形式指标下拉框
  function generateBox(id){
	  var store;
	  var index = 0;
	  var param = "";
	  if(id == "orgCode"){
		  store = Ext.create('Ext.data.Store',{
				fields:['dataName','dataValue'],
				data:${moudleParameterForm.orgFieldListJson}
		 });
		 index = store.find("dataValue","${moudleParameterForm.orgFieldIDs}");
		 param = "1";
	  }else if(id == "contentCode"){
		  store = Ext.create('Ext.data.Store',{
				fields:['dataName','dataValue'],
				data:${moudleParameterForm.contentTypeListJson}
		 });
		  index = store.find("dataValue","${moudleParameterForm.contentType}");
		  param = "2";
	  }
	  if(index==-1)
			index=0;
	  
 	var box = Ext.create('Ext.form.ComboBox', {
		width:150,
		id:id+"box",
	    store: store,
	    autoSelect:true,
	    queryMode: 'local',
	   	displayField: 'dataName',
	    valueField: 'dataValue',
	    labelPad:0,
	    renderTo:id,
	    labelWidth:0,
	    listeners:{
	   		blur:{
				fn:function(combox){
					var res = store.find("dataValue",combox.getValue());
					if(res==-1){//无效输入值
						combox.setValue("");
					}
				}
			},
			change:{
				fn:function(combox,newvalue,oldvalue){
					if(!Ext.isEmpty(Ext.getCmp(id+"box")))
						save(param);
				}
			}
		}
	});
 	box.setValue(store.getAt(index).data.dataName);
  }
  //渲染内容形式
  function contentRender(contentType){
		if("1"==contentType)
			contentType = "文字";
		else if("0"==contentType)
			contentType = "网址";
		return contentType;
  }
  //渲染内容形式
  function operator(value,meteData,record){
	  	var store = Ext.data.StoreManager.lookup("tablegrid_dataStore");
	  	var n = record.data.codeitemid;
	 	 var id = record.data.contenttype;
		return '<a href="javascript:void(0);"  onclick="showEditPage(\''+n+'\',\''+id+'\');" ><img src="/images/edit.gif" style="cursor:hand"/></a>';
  }
  
  function isIE() { //ie?  
	    if (!!window.ActiveXObject || "ActiveXObject" in window)  
	        return true;  
	    else  
	        return false;  
	 }
	 
  function getInfo(){
		var ret = new Object();
		for(var i=0;i<parameterForm2.orgFieldIDs.options.length;i++){
			if(parameterForm2.orgFieldIDs.options[i].selected){
			ret.orgFieldIDs = parameterForm2.orgFieldIDs.options[i].value;
			ret.orgFieldIDsView = parameterForm2.orgFieldIDs.options[i].text;
			}
		}
		for(var j=0;j<parameterForm2.contentType.options.length;j++){
			if(parameterForm2.contentType.options[j].selected){
			ret.contentType = parameterForm2.contentType.options[j].value;
			ret.contentTypeView = parameterForm2.contentType.options[j].text;
			}
		}
		
		if(window.parent.parent.me){//针对新招聘
			window.parent.parent.me.setCallBack({returnValue:ret});
			window.parent.parent.Ext.getCmp('window').close();
		}else{
			window.close();
		}
	}
  function showEditPage(n,id){
        if(id=='null'||id=='undefined'){
       	 	Ext.Msg.alert("提示信息",NOUNIT_CONTENT_FIELD+"!");
       		 return;
        }
	     var type = id;
       /*	if(id=="文字")
           	type = "1";
       	else if(id=="网址")
          	type = "0";*/
       	
        if(type.length==0)
        {
        	Ext.Msg.alert("提示信息",SELECT_CONTENT_TYPE+"!");
           return;
        }
        var dw="";
        var dh = "";
        if(type=='0')
        {
             dw="540px";
             dh="200px";
        }
        if(type=='1')
        {
             dw="800px";
             dh="750px";
        }
        var dl=(screen.width-dw)/2;
        var dt=(screen.height-dh)/2;
        var thecodeurl="/recruitment/parameter/configureParameter.do?b_edit=edit`codeitemid="+n+"`type="+type; 
         var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
         var values="";
         if(Ext.isChrome){//chrome浏览器
  		    values=window.open(iframe_url, "", 
  	               "width="+dw+"px,height="+dh+"px,top="+dt+",left="+dl+",resizable=no,center=yes,scroll=yes,location=no,status=no");
  	   	 }else if(!isIE()&&!Ext.isChrome){//非ie和chrome  主要针对火狐和safari中弹窗的位置
  		    values=window.open(iframe_url, "", 
  	               "width="+dw+"px,height="+dh+"px,screenY="+dt+",screenX="+dl+",resizable=no,center=yes,scroll=yes,location=no,status=no");
  	   	 }else{
  		   values=window.showModalDialog(iframe_url,null,"dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
  		   setEditValue(values);
  	   	 }
         /*me.openWindow({
            id:'window1',
 			title:'单位内容编辑',
 			width:dw,
 			height:dh,
 			url:thecodeurl,
 			callBack:"setEditValue()"
 		});*/	
}
  function setEditValue(values){
	  if(values!=null)
      {
		  parameterForm2.action="/recruitment/parameter/configureParameter.do?b_neworgIntro=inti";
		  parameterForm2.submit();
      }
  }
function save(type){
	var orgFieldIDs ;
	var contentType;
	/*var orgFieldIDs = Ext.getCmp("orgCodeBox").getValue();
	var contentType = Ext.getCmp("contentCodeBox").getValue();
	if(Ext.isEmpty(orgFieldIDs)){
		Ext.Msg.alert("提示信息",SELECT_UNIT_FIELD+"!");
		return;
	}
	if(Ext.isEmpty(contentType)){
		Ext.Msg.alert("提示信息",SELECT_CONTENT_TYPE+"!");
		return;
	}*/
	for(var i=0;i<parameterForm2.orgFieldIDs.options.length;i++){
		if(parameterForm2.orgFieldIDs.options[i].selected){
			if(parameterForm2.orgFieldIDs.options[i].value ==""||parameterForm2.orgFieldIDs.options[i].value =="       "){
				if(type=="2")
				{
					alert(SELECT_UNIT_FIELD+"!");
				}
				return;
			}
			orgFieldIDs=parameterForm2.orgFieldIDs.options[i].value;
		}
	}
	for(var j=0;j<parameterForm2.contentType.options.length;j++){
		if(parameterForm2.contentType.options[j].selected){
			if(parameterForm2.contentType.options[j].value =="" || parameterForm2.contentType.options[j].value =="       "){
				if(type=="2")
				{
					alert(SELECT_CONTENT_TYPE+"!");
				}
				return;
			}
			contentType=parameterForm2.contentType.options[j].value;
		}
	}
	parameterForm2.action="/recruitment/parameter/configureParameter.do?b_newsave=save&orgFieldIDs="+orgFieldIDs+"&contentType="+contentType;
	parameterForm2.submit();
  
  }
  function col(){
	  if(window.parent.parent.me)
	  	  window.parent.parent.Ext.getCmp('window').close();
	  else
		  window.close();
  }
  function query(type)
  {  
	  //会出现isVisible=0会出现图标备份还原功能
      //parameterForm2.action="/recruitment/parameter/configureParameter.do?b_neworgIntro=inti&isVisible=0&type="+type;
      parameterForm2.action="/recruitment/parameter/configureParameter.do?b_neworgIntro=inti&type="+type;
      parameterForm2.submit();
  }
  
  
  
	function backup()
	{
	  jinduo(1);
	  var hashvo=new ParameterSet();
      hashvo.setValue("path","<%=path%>");
	
     var request=new Request({method:'post',asynchronous:true,onSuccess:backup_ok,functionId:'ZP0000002375'},hashvo);			   	
	}
	function backup_ok(outparameters)
   {
     closejinduo(1);
     var outName=outparameters.getValue("filename");
     if(outName=="1")
     {
         alert(BACKUP_FILE_NOT_EXIST+"!");
         return;
     }
       outName = decode(outName);
       var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName);
    }
	var objlist;
	function Reduction()
	{
	   var theurl="/recruitment/parameter/configureParameter.do?b_reduction=init`isclose=1";
	   var iframe_url="/gz/templateset/tax_table/iframe_tax.jsp?src="+$URL.encode(theurl);
      // var objlist =window.showModalDialog(iframe_url,null,"dialogWidth=350px;dialogHeight=150px;resizable=yes;status=no;");  

       var gloleft = (window.screen.availWidth-540-10)/2;//计算窗口距离屏幕左侧的间距  540是窗口宽度，10是边框大小
  	   var glotop = (window.screen.availHeight-400-30)/2;//窗口距离屏幕上方的间距         400是窗口高度，30是边框和标题栏大小(20)

  	   var values="";
  	   var width=450;
  	   var height=190;
  	   var aleft = Ext.isEmpty(width)?gloleft:(2*gloleft-width+540)/2;
  	   var atop = Ext.isEmpty(height)?glotop:(2*glotop-height+400)/2;
  	   
  	 	me.openWindow({
         id:'window1',
			title:'还原',
			width:width,
			height:height,
			url:iframe_url,
			callBack:"Reduction_return()"
		});
	}
	  //window.open(theurl);
	 function Reduction_return(objlist){ 
	   if(objlist == null)
	   		return;
	   var obj=new Object();
       obj.dir=objlist.dir;
       //jinduo(2);
      //var hashVo=new ParameterSet();
       //hashVo.setValue("dir",obj.dir);
       //hashVo.setValue("path","");
       //var In_parameters="flag=1";
       //var request=new Request({method:'post',asynchronous:true,parameters:In_parameters,onSuccess:export_in_tax_ok,functionId:'3000000188'},hashVo);			   	
	   
	}
	function export_in_tax_ok(outparameters)
	{
	   closejinduo(2);
	}
	function closejinduo(type){
	   var waitInfo;
	if(type==1)
	     waitInfo=eval("wait");
	else
	     waitInfo = eval("wait2");
	   waitInfo.style.display="none";
     }
	function jinduo(type){
		var x=document.body.scrollLeft+event.clientX-120;
	    var y=document.body.scrollTop+event.clientY-300; 
		var waitInfo;
		if(type==1)
		     waitInfo=eval("wait");
		else
		     waitInfo = eval("wait2");
		waitInfo.style.top=y;
		waitInfo.style.left=x;
		waitInfo.style.display="block";
	}
	
	function changeMsg(){
	    if(!window.addEventListener){ 
	   	    openInputCodeDialogText('UM','orgName','orgId');
	    }else{
	        Ext.Msg.alert('提示','该功能仅支持在ie浏览器中进行操作');
	    }
	    }
</script>
	
  </SCRIPT>
  </head>
  <body>
  <base id="mybase" target="_self">
 <html:form action="/hire/parameterSet/configureParameter/nextPage">
	<div  id="wait" style='position:absolute;top:285;left:120;display:none;width:285px;heigth:120px'>
 
		<table border="1" width="37%" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
		<tr>
			<td class="td_style" height=24><bean:message key="hire.backuping.waiter"/></td>
		</tr>
		
		<tr>
			<td style="font-size:12px;line-height:200%" align=center>
			<marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10">
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
	    <iframe src="javascript:false" style="position:absolute; visibility:inherit; top:0px; left:0px;width:285px;height:120px;z-index:-1;filter='progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)';">
	    </iframe>
	    	
	</div>
	<!-- 系统管理，参数设置，hr页面，内容与顶部之间间隔10px   jingq  add 2014.12.23 -->
    <table style="width:100%;" border='0' align="center" class="RecordRow" cellpadding="0" cellspacing="0" <%if(!"hcm".equals(bosflag)){ %>style="margin-top:10px;"<%} %>>
    <thead>
    <tr> 
      <td align="left"class="TableRow" nowrap><bean:message key="hire.appoint.field"/>&nbsp; </td>
    </tr>
     </thead>
   	<TR>
	   	<td>
	   	<table border='0'>
	   	<tr height="30px;">
		   	<td align='right' width='25%' style="padding-right:5px;"><bean:message key="hire.select.unitfield"/></td>
		   	<td align='left' width='75%'>
		   	    <%--<div style="display:inline;"><div id="orgCode" style="float: left;"></div><bean:message key="hire.unit.baseset"/></div>--%>
		   		<hrms:optioncollection name="moudleParameterForm" property="orgFieldList" collection="list" />
				<html:select name="moudleParameterForm" property="orgFieldIDs" size="1" style="width:100px"  onchange="save('1');" >
					             	<html:options collection="list" property="dataValue" labelProperty="dataName"/>
				</html:select><bean:message key="hire.unit.baseset"/>
		   	</td>
		   	<td width="60%"></td>
	   	</TR>
	   	<TR height="30px;">
		   	<td align='right' width='25%' style="padding-right:5px;"><bean:message key="hire.content.type"/></td>
		   	<td align='left' width="75%">  
		   		<%--<div style="display:inline;"><div id="contentCode"  style="float: left;"></div><bean:message key="hire.unitbase.43"/></div>--%>
		   		<hrms:optioncollection name="moudleParameterForm" property="contentTypeList" collection="list" />
				<html:select name="moudleParameterForm" property="contentType" size="1" style="width:100px"  onchange="save('2');" styleId="aa">
             		<html:options collection="list" property="dataValue" labelProperty="dataName"/>
				</html:select>
				<bean:message key="hire.unitbase.43"/>
		   	</td>
		   	<td width="60%">&nbsp;</td>
	   	</TR>
	   	</table>
	   	</td>
   	</TR>
    </table>
    <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
    <tr>
	    <td align="center" style="padding-top:10px;">
	    <div style="overflow:auto;width:100%;height:100%;" >
		<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="RecordRow">
		  <thead>
		  <tr>
			<td colspan="4" align="left" class="RecordRow" nowrap>
				<table cellpadding="0" cellspacing="0" border="0">
				<tr>
					<td>
					<bean:message key="hire.unit.query"/>
					
					
					<input type="text" name="orgName" value="${moudleParameterForm.orgName}" size="20px" class="text4"/>
					<input type="hidden" name="orgId" value=""/>
					<input type="hidden" name="hiddenOrgId" value="${moudleParameterForm.hiddenOrgId}"/>
					<img src="/images/code.gif" border="0" align="absmiddle" onclick="changeMsg();"/>
					</td>
					
					<td align="center"><input type="button" class="mybutton" value="查 询" onclick="query('0');" /></td>
					<td align="center"><input type="button" class="mybutton" value="全 部" onclick="query('all');"/></td>
				
				</tr>
				</table>
			</td> 
	       </tr>
	      </thead>
	    </table>
	     	<div style="width:100%;height:330px" id="extTable"></div>
		</div>
	    </td>
    </tr>
   	  
	</table> 
	</td>
	</tr>
	<logic:notEqual value="1" name="moudleParameterForm" property="isVisible">
	<tr>
		<td align="center">
		<table  style="width:580px;" align="center">
		<tr>
			<td style="padding-top:3px;" align="center">
			<input type="button" value="<bean:message key="hire.backup.file"/>" class="mybutton" onclick="backup();"/>
			<input type="button" value="<bean:message key="hire.reduction.file"/>" class="mybutton" onclick="Reduction();"/>
			</td>
		</tr>
		</table> 
		</td>
	</tr>
	</logic:notEqual>
	</table>
	
	<logic:equal value="1" name="moudleParameterForm" property="isVisible">
	<table  width="100%" align="center">
	<tr class="list3">
		<td align="center" colspan="2">
		<html:button styleClass="mybutton" property="b_center" onclick="getInfo();">
			<bean:message key="button.ok"/>
		</html:button>
		
		<html:button styleClass="mybutton" property="br_return" onclick="col();">
			<bean:message key="button.close"/>
		</html:button>            
		</td>
	</tr>   
	</table>  
	</logic:equal>
  </html:form>  
  </body>
</html>
