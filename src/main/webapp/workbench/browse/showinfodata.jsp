<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="org.apache.commons.lang.StringUtils"%>

<div align="right"> 
<%@taglib uri="/tags/struts-bean" prefix="bean"%> 
<%@taglib uri="/tags/struts-html" prefix="html"%> 
<%@taglib uri="/tags/struts-logic" prefix="logic"%> 
<%@taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%> 
<%@page import="com.hrms.struts.valueobject.UserView"%> 
<%@page import="com.hrms.struts.constant.WebConstant"%> 
<%@page import="com.hrms.struts.constant.SystemConfig"%>
<%@ page import="com.hjsj.hrms.actionform.browse.BrowseForm"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc" %>
<%@ page import="java.util.List"%>
<%@ page import="java.util.HashMap,com.hrms.hjsj.sys.FieldItem" %> 
<%@ page import="org.apache.commons.beanutils.LazyDynaBean,com.hrms.frame.codec.SafeCode"%>
<%
	BrowseForm browseForm=(BrowseForm)session.getAttribute("browseForm");
	//System.out.println(scrW+" "+scrH);
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	//String manager=userView.getManagePrivCodeValue();
	String manager=userView.getUnitIdByBusi("4");
	int status=userView.getStatus();
	int i=0;
	String caution_field=SystemConfig.getPropertyValue("caution_field");
	String caution_codeitems=SystemConfig.getPropertyValue("caution_codeitems");
	String caution_colors=SystemConfig.getPropertyValue("caution_colors");
	session.setAttribute("caution_field",caution_field);
	session.setAttribute("caution_codeitems",caution_codeitems);
	session.setAttribute("caution_colors",caution_colors);
	HashMap partMap=(HashMap)browseForm.getPart_map();
	i=0;
	  String inputchinfor = browseForm.getInputchinfor();
	  String approveflag = browseForm.getApproveflag();	  
	partMap=(HashMap)browseForm.getPart_map();
	int columnnum=0;
	String bosflag="";
	if(userView!=null){
	 	bosflag = userView.getBosflag();
	}
	
	String unitID = (String)partMap.get("unit");
	String deptID = (String)partMap.get("dept");
	String posID = (String)partMap.get("pos");
%>
<%// 在标题栏显示当前用户和日期 2004-5-10 
		
%> 
 <logic:notEqual value="1" name="browseForm" property="isphotoview">
<hrms:linkExtJs/><!-- add by xiegh 该标签需要放在validate.js的上面  validate.js里面有ext6 会导致界面报错-->
<script language="javascript" src="/js/validate.js"></script> 
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script> 
<script type="text/javascript" src="/phone-app/jquery/jquery-3.5.1.min.js"></script>
<script type="text/javascript" src="/js/fixtable.js"></script> 
<script language="javascript" src="/ajax/basic.js"></script>
<script type="text/javascript" src="/ext/rpc_command.js"></script>
<script language='JavaScript' src='/components/codeSelector/codeSelector.js'></script>
<script type="text/javascript" src="/components/extWidget/proxy/TransactionProxy.js"></script>
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
<script type="text/javascript">
function change()
{
      browseForm.action="/workbench/browse/showinfodata.do?b_query=link&code=${browseForm.code}&kind=${browseForm.kind}";
      browseForm.submit();
}
 
 function changesort()
{
   browseForm.action="/workbench/browse/showinfodata.do?b_search=link&code=${browseForm.code}&kind=${browseForm.kind}";
   browseForm.submit();
}        
function executeOutFile(){
	document.getElementById('exportButton').disabled = true;
	document.getElementById('wait').style.display='block';
	if(!getBrowseVersion() || getBrowseVersion()==10){ //非IE浏览器 样式问题         导出等待时，多出来一块   bug 34775   wangb 20180209
		document.getElementById('wait').style.zIndex ='999';
		var diviframe = wait.getElementsByTagName('iframe')[0];
		diviframe.style.display = 'none';
	}
	var map = new HashMap();
    map.put("userbase", "${browseForm.userbase}");
    map.put("code","${browseForm.code}");
	map.put("orgtype","${browseForm.orgtype}");
	map.put("roster","${browseForm.roster}");
	map.put("checksort","1");
    Rpc({functionId:"0521010019",success:showFieldList},map); 
}
function showFieldList(outparamters){
	document.getElementById('exportButton').disabled = false;
	document.getElementById('wait').style.display='none';
	var value=outparamters.responseText;
	var map=Ext.decode(value);
	if(map.succeed){
		var outName=map.outName;
		window.open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName,"xls");
	} else {
		alert(map.message);
	}
}
function openwin(url)
{
   url = url.replace(/&/g,"`");
   var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(url);
   window.open(iframe_url,"_blank","left=0,top=0,width="+(screen.availWidth-10)+",height="+(screen.availHeight-70)+",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");
}
function selectQ()
   {
       var code="${browseForm.code}";
       var kind="${browseForm.kind}";
       var tablename="${browseForm.userbase}";
       var a_code="UN";
       if(kind=="2")
       {
          a_code="UN"+code;
       }else if(kind=="1")
       {
          a_code="UM"+code;
       }else if(kind=="1")
       {
          a_code="@K"+code;
       }else
       {
          a_code="UN"+code;
       }
        //update by xiegh on date20171125 修改自助服务-员工信息-信息浏览：浏览器兼容问题    
       var thecodeurl="/general/inform/search/generalsearch.do?b_query=link&type=1&a_code="+a_code+"&tablename="+tablename+"&fieldsetid=A01&second=1";
       var dw=700,dh=380,dl=(screen.width-dw)/2;
       var dt=(screen.height-dh)/2;
        if (checkBrowser().indexOf("MSIE|6") != -1) {
       	 dh=400;
   		 } 
      //19/3/29 xus 浏览器兼容 信息浏览-查询-高级 谷歌不弹窗
        var config = {id:'selectQ_showModalDialogs',width:dw,height:dh,type:0};
 	   	modalDialog.showModalDialogs(thecodeurl,'',config, selectReturn);
   }
//19/3/23 xus 浏览器兼容  信息浏览-查询-高级 谷歌不弹窗 关闭窗口
function closeExtWin(){
		if(Ext.getCmp('selectQ_showModalDialogs'))
			Ext.getCmp('selectQ_showModalDialogs').close();
	}
   //open弹窗返回值调用方法  wangb 20180206 bug 34758
   function selectReturn(return_vo){
   		if(return_vo!=null){
            var expr= return_vo.expr;
            var factor=return_vo.factor;
            var history=return_vo.history;
            var second = return_vo.second;
            var o_obj=document.getElementById('factor');
            o_obj.value=factor;
            o_obj=document.getElementById('expr');
            o_obj.value=expr;
            o_obj=document.getElementById('history');
            o_obj.value=history;
            document.getElementsByName('likeflag')[0].value=return_vo.likeflag;
            document.getElementsByName('querySecond')[0].value=second;
            browseForm.action="/workbench/browse/showinfodata.do?b_query=link&code=${browseForm.code}&kind=${browseForm.kind}&check=ok&isAdvance=0&query=1";
            //zgd 2014-1-13 高级查询，在任意页进入某人员信息后，点返回，都跳到第一页；通过修改isAdvance参数为0，返回后就还在先前所在页。
            browseForm.submit();
      }
   }
   function clearQ()
   {
       browseForm.action="/workbench/browse/showinfodata.do?b_search=link&code=${browseForm.code}&kind=${browseForm.kind}&check=no&query=0";
       browseForm.submit();
   }
   function viewPhoto()
   {
       browseForm.action="/workbench/browse/showinfodata.do?b_view_photo=link&code=${browseForm.code}&kind=${browseForm.kind}&query=${browseForm.query}&isphotoview=1&isResult=1";       
       browseForm.target="nil_body";
       browseForm.submit();
   }   
   function winhrefOT(a0100,target)
{
   if(a0100=="")
      return false;
   var returnvalue="${browseForm.returnvalue}";
   if(returnvalue!="dxt")
     returnvalue="1";    
   browseForm.action="/workbench/browse/showselfinfo.do?b_search=link&userbase=${browseForm.userbase}&encryptParam="
          +a0100+"&flag=notself&returnvalue="+returnvalue+"&fromPhoto=0";
   browseForm.target=target;
   browseForm.submit();
}
function multimediahref(dbname,a0100){
	var thecodeurl =""; 
	var return_vo=null;
	var setname = "A01";
	var dw=800,dh=500,dl=(screen.width-dw)/2;
	var dt=(screen.height-dh)/2;
  	thecodeurl="/general/inform/multimedia/multimedia_tree.do?b_query=link&setid="+setname+"&a0100="+a0100+"&nbase="+dbname+"&dbflag=A&canedit=false";
	if(getBrowseVersion()){
	    thecodeurl = thecodeurl.replace(/&/g,"`");
  		var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
  		return_vo= window.showModalDialog(iframe_url, "", 
  		"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:800px; dialogHeight:500px;resizable:no;center:yes;scroll:no;status:no");
  	}else{//非IE浏览器
  		thecodeurl = encode(thecodeurl);//链接中有a0100 该参数需要加密 xiegh bug36555
  		var iframe_url="/general/query/common/iframe_query.jsp?isEncode=1&src="+$URL.encode(thecodeurl);
  		var iTop = (window.screen.availHeight - 30 - dh) / 2;  //获得窗口的垂直位置
		var iLeft = (window.screen.availWidth - 10 - dw) / 2; //获得窗口的水平位置 
		window.open(iframe_url,"","width="+dw+",height="+dh+",resizable=no,scrollbars=no,status=no,left="+iLeft+",top="+iTop);
  	}
}

function showOrClose()
{
		var obj=eval("aa");
	    var obj3=eval("vieworhidd");
		//var obj2=eval("document.browseForm.isShowCondition");
	    if(obj.style.display=='none')
	    {
    		obj.style.display='';
        	obj3.innerHTML="<a href=\"javascript:showOrClose();\"> 查询隐藏 </a>";
    	}
    	else
	    {
	    	obj.style.display='none';
	    	obj3.innerHTML="<a href=\"javascript:showOrClose();\"> 查询显示 </a>";
	    	
    	}
}
function fieldCheckBox(hiddenname,id,obj)
{
   if(obj.checked==true)
   {
      var vo=document.getElementById(hiddenname);
      var iv=obj.value;
      var value=vo.value;
      value="`"+value+"`";
      if(value.indexOf("`"+iv+"`")==-1)
      {
         vo.value=vo.value+"`"+iv;
      }
   }else
   {
      var vo=document.getElementById(hiddenname);
      var voID=document.getElementsByName(id);      
      var len=voID.length;    
      var value="";
      for (i=0;i<len;i++)
      {
         if(voID[i].checked)
          {
             
            value=value+"`"+voID[i].value;
          }
       }
       vo.value=value;
   }
}
function selectCheckBox(obj,hiddname)
{
   if(obj.checked==true)
   {
      var vo=document.getElementById(hiddname);
      if("querylike" == hiddname){
	      var Info=eval("info_cue1");	
		  Info.style.display="";
      }
      
      if(vo)
         vo.value="1";
   }else {
      var vo=document.getElementById(hiddname);
      if("querylike" == hiddname){
   	      var Info=eval("info_cue1");	
		  Info.style.display="none";
      }
            
      if(vo)
         vo.value="0";
   }

}
function MusterInitData()
{
	   
	   var vo=document.getElementsByName("querlike2");
	   var obj=vo[0];
	   if(obj.checked==true)
	   {
          
          var Info=eval("info_cue1");	
	      Info.style.display="";
          
       }else
       {
         
         var Info=eval("info_cue1");	
	     Info.style.display="none";
         
   }
}
function returnTOWizard()
{
     browseForm.action="/templates/attestation/police/wizard.do?br_postwizard=link";
     browseForm.target="il_body";
     browseForm.submit();
}
function query1(query1)
{
   browseForm.action="/workbench/browse/showinfodata.do?b_query=link&code=${browseForm.code}&kind=${browseForm.kind}&query="+query1;
   browseForm.submit();
} 
function resetQuery()
{
    var vo=document.getElementById("query");
    var inps=vo.getElementsByTagName("input") ;
    for(i=0;i<inps.length;i++)
    {
      if(inps[i].type=="hidden"||inps[i].type=="text")
        inps[i].value="";
      else if(inps[i].type=="checkbox")      
         inps[i].checked=false;
      
    }   
    var sels=document.getElementsByTagName("select") ;
    for(i=0;i<sels.length;i++)
    {
     sels[i].options[0].selected=true ;
    }
     var o_obj=document.getElementById('factor');
     o_obj.value="";
     o_obj=document.getElementById('expr');          
     o_obj.value="";
     o_obj=document.getElementById('history');
     o_obj.value="";
     query1("");
}

window.onresize = function(){
	setDivStyle();
}

function setDivStyle(){
	document.getElementById("fixedDiv").style.height = document.body.clientHeight-167;
    document.getElementById("fixedDiv").style.width = document.body.clientWidth-15; 
    document.getElementById("pageDiv").style.width = document.body.clientWidth-15; 
}
function checkDate(obj){
    var radio = document.getElementById("day");
    if(radio && radio.checked) {
        if(!obj.value){
            return true;
        }
        
        var checkFlag = checkDateTime(obj.value);
        if(!checkFlag) {
            obj.value="";
            obj.focus();
            alert(INPUT_FORMAT_DATE);
            return false;
        }
    }
}

function checkDates(itemid){
    var radio = document.getElementById("day");
    if(radio && radio.checked) {
        var flag = checkDate(document.getElementById(itemid + "S"));
        if(!flag){
            return false;
        }
        
        flag = checkDate(document.getElementById(itemid + "E"));
        if(!flag){
            return false;
        }
    }
}
</script>
 </logic:notEqual>
<logic:equal value="1" name="browseForm" property="isphotoview">
<script src="/phone-app/jquery/jquery-3.5.1.min.js" type="text/javascript"></script>
<script type="text/javascript" src="/js/bigphoto.js"></script>
<script type="text/javascript" src="/js/validate.js"></script> 
<link rel="stylesheet" type="text/css" href="/ext/ext6/resources/ext-theme.css"></link> 
<script type="text/javascript" src="/ext/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="/ext/ext-all.js"></script>
<script type="text/javascript" src="/ext/rpc_command.js"></script>
<script type="text/javascript" src="/js/constant.js"></script>
<script type="text/javascript" src="/js/showModalDialog.js"></script>
<script type="text/javascript">
function photoexport(){
	var thecodeurl="/workbench/browse/view_photo.do?b_photoname=link";
    var dw=350,dh=200;
    if(!window.showModalDialog)
    	dh=250;
    
	var config = {
   		width:dw,
        height:dh,
        id:'setPhotoNameWin',
        title:SET_PHOTOFILE_NAME
    }
       
   	modalDialog.showModalDialogs(thecodeurl,null,config,returnFun);
}

function returnFun(return_vo){
	if(return_vo){
		document.all.ly.style.display="block";   
		document.all.ly.style.width=document.body.scrollWidth;   
		document.all.ly.style.height=document.body.scrollHeight;
		var waitInfo=document.getElementById("wait");
		waitInfo.style.display='block';
		var map = new HashMap();
	    map.put("userbase", return_vo.userbase);
	    map.put("where_n", return_vo.where_n);
	    map.put("formula", return_vo.photoname);
	   Rpc({functionId:'0201001007',success:searchok,timeout:'30000000'},map); 
	   function searchok(response){
			var value=response.responseText;
			var map=Ext.decode(value);
			if(map.succeed.toString()=='false'){
				waitInfo.style.display="none";
				alert(map.message);
			}else{
			    var hasData=map.hasData;
				var outName=map.outName;
				var name=outName;//+".zip";
				if (hasData=='true'){//tiany add 添加hasData标记 记录是否有照片
                    window.open("/servlet/vfsservlet?fromjavafolder=true&fileid="+name);
                    waitInfo.style.display="none";
                    document.all.ly.style.display="none";
                }else{
                    waitInfo.style.display="none";
                    document.all.ly.style.display="none";
                    alert(NOT_PHOTO_EXPORT);
                }
			}
		}
   }
}

$(checkScreenWidth);
/* 
 * 根据屏幕宽度动态修改照片墙的样式  
 */
function checkScreenWidth(){
    // 59142 非ie浏览器,margin-left与table的center冲突，暂不设置
    if(getBrowseVersion()<=0)
        return;

	if(window.screen.width < 1300) {
		$("#photo_tab").css({'margin-left':'-9px'});
	}else {
		$("#photo_tab").css({'margin-left':'15px'});
	}
}
</script>
<script language="javascript" type="text/javascript">
function winhref(a0100,target)
{
   if(a0100=="")
      return false;
    <%if(inputchinfor.equals("1")&&approveflag.equals("1")) {%>
    	browseForm.action="/workbench/browse/showselfinfo.do?b_search=link&userbase=${browseForm.userbase}&a0100="+a0100+"&flag=notself&returnvalue=1&fromPhoto=1";
    <%} else {%>
    browseForm.action="/workbench/browse/showselfinfo.do?b_search=link&userbase=${browseForm.userbase}&a0100="+a0100+"&flag=notself&returnvalue=111";
    <%}%>
    browseForm.target=target;
    browseForm.submit();
   
      
}
  document.oncontextmenu = function() {return false;};
function returnShow()
{
   browseForm.action="/workbench/browse/showinfodata.do?br_query=link&query=${browseForm.query}&isphotoview=";   
   browseForm.submit();
}
</script>
</logic:equal>

<style type="text/css">
.myfixedDiv
{  
    overflow:auto;
    height:expression(document.body.clientHeight-167);
    width:expression(document.body.clientWidth-15); 
    BORDER-BOTTOM: #99BBE8 1pt solid; 
    BORDER-LEFT: none; 
    BORDER-RIGHT: #99BBE8 1pt solid; 
    BORDER-TOP: none;
}
.RecordRow_top {
	border: inset 1px #94B6E6;	
	BORDER-BOTTOM: #94B6E6 0pt solid; 
	BORDER-LEFT: #94B6E6 0pt solid; 
	BORDER-RIGHT: #94B6E6 0pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	font-size: 12px;
}		

.photoTr{
	padding-left: 3px;
}

img {vertical-align: middle;}
input[type='checkbox']{vertical-align:middle;}
 </style>
</div><% i=0;%>
<style>
.dataTable{
	border:#94B6E6 1pt solid;
}
</style>
<hrms:themes></hrms:themes>
<!--zgd 2014-7-9 信息列表中岗位中有兼职情况的特殊处理。partdescdiv在ParttimeTag中写入-->
<style>
<%if("hcm".equals(bosflag)){%>
.partdescdiv{           
	margin-top:-14px;
}
<%}%>
.notop{
	border-top: 0pt solid;
}

.x-btn-button {
    margin-top: -1px!important;
    margin-top: 0px\9;
    padding-top: 3px\9;
}
</style>
<logic:notEqual value="1" name="browseForm" property="isphotoview">
<html:form action="/workbench/browse/showinfodata">
<html:hidden name="browseForm" property="factor" styleId="factor" styleClass="text"/>
<html:hidden name="browseForm" property="expr" styleId="expr" styleClass="text"/>  
<html:hidden name="browseForm" property="history" styleId="history" styleClass="text"/>  
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" style="">
<tr>
    <td align="left"  nowrap>
      <table  border="0" cellspacing="0" style="margin-top: 6px;"  cellpadding="0"> <!-- zhangcq 2016/5/4 调整布局 -->
     <tr>
    <td align="left"  nowrap>
	       <logic:notEmpty  name="browseForm" property="code">
	          <bean:message key="system.browse.info.currentorg"/>:
	          <hrms:codetoname codeid="UN" name="browseForm" codevalue="code" codeitem="codeitem" scope="session" uplevel="${browseForm.uplevel}"/>  	      
          	  <bean:write name="codeitem" property="codename" />
          	  <hrms:codetoname codeid="UM" name="browseForm" codevalue="code" codeitem="codeitem" scope="session" uplevel="${browseForm.uplevel}"/>  	      
          	  <bean:write name="codeitem" property="codename" />
          	  <hrms:codetoname codeid="@K" name="browseForm" codevalue="code" codeitem="codeitem" scope="session" uplevel="${browseForm.uplevel}"/>  	      
          	  <bean:write name="codeitem" property="codename" />
          	  
	       </logic:notEmpty>
	       <logic:notEmpty name="browseForm" property="personsortlist">
	           <html:select name="browseForm" property="personsort" size="1" onchange="changesort()">
                           <html:option value="All">全部</html:option>
                           <html:optionsCollection property="personsortlist" value="codeitem" label="codename"/>
               </html:select>  
	      </logic:notEmpty>
	    </td>   
	    <td nowrap>
             <table  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">
                <tr>
                       <td nowrap>&nbsp;[&nbsp;
                       </td>
                       <td nowrap id="vieworhidd"> 
                          <a href="javascript:showOrClose();"> 
                              <logic:equal name="browseForm" property="isShowCondition" value="none" >查询显示</logic:equal>   
                              <logic:equal name="browseForm" property="isShowCondition" value="block" >查询隐藏</logic:equal>   
                          </a>
                       </td>                       
                       <td nowrap>&nbsp;]&nbsp;&nbsp;&nbsp;&nbsp;
                       </td>
                    </tr>
             </table>
      </td>  
      <td nowrap>
          <logic:equal name="browseForm" property="orglike" value="1">
                     <input type="checkbox" id="orglikeid1" name="orglike2" value="true" onclick="selectCheckBox(document.getElementById('orglikeid1'),'orglike');change();" checked>
          </logic:equal>
          <logic:notEqual name="browseForm" property="orglike" value="1">
                     <input type="checkbox" id="orglikeid2" name="orglike2" value="true" onclick="selectCheckBox(document.getElementById('orglikeid2'),'orglike');change();">
          </logic:notEqual>                 
           <html:hidden name="browseForm" styleId="orglike" property='orglike' styleClass="text"/>                 
           <bean:message key="system.browse.info.viewallpeople"/>        
      </td>       
   </tr>
   </table>
    </td>
</tr>
<tr>
   <td nowrap style="padding-left: 5px;">
<%
	int flag=0;
	int j=0;
	int n=0;
	int column=0;
%>
 <table width="100%" border="0"  cellspacing="0"  align="center" cellpadding="0" id='aa' style='display:${browseForm.isShowCondition}'>
  <tr>
   <td>
     <!-- 查询开始 -->
     <div id="table1">
     <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="RecordRow" id="query">
     <tr><td height="5"></td></tr><!-- 这一行的作用是为了让最上边的一行 与 table边框 有5像素的距离，实现padding的效果 -->
         <hrms:importgeneraldata showColumn="dbname" valueColumn="pre" flag="false" paraValue="" 
                 sql="browseForm.dbcond" collection="list" scope="page"/>
         <%
           List list=(List) pageContext.getAttribute("list");
           if(list!=null&&list.size()>1){
         %>
         <tr>
           <td align="right" height='28' nowrap>
             <bean:message key="label.dbase"/></td>
           <td align="left"  nowrap><!-- 人员库 -->  
                  <html:select name="browseForm" property="userbase" onchange="change();" size="1">
                        <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                   </html:select>
              <!-- 人员分类 -->
              <logic:notEqual value="0" name="browseForm" property="showflag">
	              &nbsp;人员分类
	              <hrms:optioncollection name="browseForm" property="condlist" collection="cond" />
	              <html:select name="browseForm" property="stock_cond" onchange="query1('1');" size="1">
	            	<option value="-1" label="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" />	
	                <html:options collection="cond" property="dataValue" labelProperty="dataName"/>
	              </html:select>
              </logic:notEqual>
           </td>
           <td align="right" height='28' nowrap><!-- 姓名 -->
                   <bean:message key="label.title.name"/></td>
                  <td align="left"  nowrap>
                  <input type="text" name="select_name" value="${browseForm.select_name}" size="32" class="text4" >
            </td>
            </tr>
         <%}else{%>
             <logic:notEqual value="0" name="browseForm" property="showflag">
              <tr>
	              <td align="right" height='28' nowrap>人员分类</td>
	              <td align="left"  nowrap> 
	                <hrms:optioncollection name="browseForm" property="condlist" collection="cond" />
	                <html:select name="browseForm" property="stock_cond" onchange="query1('1');" size="1">
	            	<option value="-1" label="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" />	
	                <html:options collection="cond" property="dataValue" labelProperty="dataName"/>
	               </html:select>
	              </td>
	              <td align="right" height='28' nowrap><!-- 姓名 -->
                   <bean:message key="label.title.name"/></td>
                  <td align="left"  nowrap>
                  <input type="text" name="select_name" value="${browseForm.select_name}" size="32" class="text4" >
                  </td>
                  </tr>
              </logic:notEqual>
              <logic:equal value="0" name="browseForm" property="showflag">
               <tr>
               <%flag=1; %>
                 <td align="right" height='28' nowrap><!-- 姓名 -->                   
                   <bean:message key="label.title.name"/></td>
                  <td align="left"  nowrap>
                  <input type="text" name="select_name" value="${browseForm.select_name}" size="32" class="text4" >
                  </td>
              </logic:equal>
         <%} %>        
       
       <logic:iterate id="element" name="browseForm"  property="queryfieldlist" indexId="index">            
           <!-- 时间类型 -->
          <logic:equal name="element" property="itemtype" value="D">
               <% 
                  if(flag==0)
                  {
                   out.println("<tr>");
                         flag=1;          
                  }else{
                       flag=0;           
                  }
               %>  
              <td align="right" height='28' nowrap>
                   <bean:write  name="element" property="itemdesc" filter="true"/>
              </td>
              <td align="left"  nowrap>
                  <html:text name="browseForm" property='<%="queryfieldlist["+index+"].value"%>' size="13" maxlength="10" styleId="${element.itemid}S" onblur="checkDate(this)" styleClass="text4" title="输入格式：2008.08.08" onclick=""/>
                  <bean:message key="label.query.to"/>
                  <html:text name="browseForm" property='<%="queryfieldlist["+index+"].viewvalue"%>' size="13" maxlength="10" styleId="${element.itemid}E" onblur="checkDate(this)" styleClass="text4" title="输入格式：2008.08.08"  onclick=""/>
			      <INPUT type="radio" name="${element.itemid}"  checked="true"><bean:message key="label.query.age"/>	
			      <INPUT type="radio" name="${element.itemid}" onclick="checkDates('${element.itemid}')" id="day"><bean:message key="label.query.day"/>
              </td>
              <%
                 if(flag==0)
        			out.println("</tr>");
              %>   
          </logic:equal>
          <logic:equal name="element" property="itemtype" value="M">
               <% 
                  if(flag==0)
                  {
                   out.println("<tr>");
                         flag=1;          
                  }else{
                       flag=0;           
                  }
              %> 
              <td align="right" height='28' nowrap>
                   <bean:write  name="element" property="itemdesc" filter="true"/>
              </td>
              <td align="left"  nowrap>
                  <html:text name="browseForm" property='<%="queryfieldlist["+index+"].value"%>' size="32" maxlength='<%="queryfieldlist["+index+"].itemlength"%>' styleClass="text4"/>
              </td>
              <%
                 if(flag==0)
        			out.println("</tr>");
              %> 
          </logic:equal> 
           <logic:equal name="element" property="itemtype" value="N">   
              <% 
                  if(flag==0)
                  {
                   out.println("<tr>");
                         flag=1;          
                  }else{
                       flag=0;           
                  }
              %> 
              <td align="right" height='28' nowrap>
                   <bean:write  name="element" property="itemdesc" filter="true"/>
              </td>
             <td align="left"  nowrap> 
              <html:text name="browseForm" property='<%="queryfieldlist["+index+"].value"%>' size="32" maxlength="${element.itemlength}" styleClass="text4"/> 
             </td>
              <%
                 if(flag==0)
        			out.println("</tr>");
              %> 
              
           </logic:equal>
           <logic:equal name="element" property="itemtype" value="A">
              <logic:notEqual name="element" property="codesetid" value="0">
              <%String delFuntion =  "deleteData(this,'queryfieldlist["+index+"].value');"; %>
                  <logic:equal name="element" property="codesetid" value="UN">
                     <%
                       if(flag==0)
                       {
                           out.println("<tr>");
                           flag=1;          
                       }else{
                            flag=0;           
                       }
                      %> 
                      <td align="right" height='28' nowrap>
                        <bean:write  name="element" property="itemdesc" filter="true"/>
                     </td>
                     <td align="left" nowrap>
                       <html:hidden name="browseForm" property='<%="queryfieldlist["+index+"].value"%>' styleClass="text"/>                               
                       <html:text name="browseForm" onkeydown='<%=delFuntion %>' property='<%="queryfieldlist["+index+"].viewvalue"%>' size="32" maxlength="50" styleClass="text4" onchange="fieldcode(this,2);"/>
                       <logic:equal name="element" property="itemid" value="b0110"> 
                            <img src="/images/code.gif"  plugin="codeselector" codesetid='UN' nmodule='4' ctrltype='3' inputname='<%="queryfieldlist["+index+"].viewvalue"%>' valuename='<%="queryfieldlist["+index+"].value"%>' multiple='true' onlySelectCodeset='true' align="absmiddle" />
                       </logic:equal> 
                       <logic:notEqual name="element" property="itemid" value="b0110">                                         
                            <img src="/images/code.gif"  plugin="codeselector" codesetid='${element.codesetid}' inputname='<%="queryfieldlist["+index+"].viewvalue"%>' valuename='<%="queryfieldlist["+index+"].value"%>' multiple='true' onlySelectCodeset='true' align="absmiddle" />
                      </logic:notEqual>   
                    </td>
                     <%
                       if(flag==0)
        	         out.println("</tr>");
                     %>                                  
                   </logic:equal>                          
                   <logic:equal name="element" property="codesetid" value="UM">
                       <%
                       if(flag==0)
                       {
                           out.println("<tr>");
                           flag=1;          
                       }else{
                            flag=0;           
                       }
                      %>  
                      <td align="right" height='28' nowrap>
                        <bean:write  name="element" property="itemdesc" filter="true"/>
                      </td>
                      <td align="left" nowrap>
                       <html:hidden name="browseForm" property='<%="queryfieldlist["+index+"].value"%>' styleClass="text"/>                               
                       <html:text name="browseForm" onkeydown='<%=delFuntion %>' property='<%="queryfieldlist["+index+"].viewvalue"%>' size="32" maxlength="50" styleClass="text4" onchange="fieldcode(this,2);"/>
                       <logic:equal name="element" property="itemid" value="e0122"> 
                             <img src="/images/code.gif"  plugin="codeselector" codesetid='UM' nmodule='4' ctrltype='3' inputname='<%="queryfieldlist["+index+"].viewvalue"%>' valuename='<%="queryfieldlist["+index+"].value"%>' multiple='true' onlySelectCodeset='true' onlySelectCodeset='true' align="absmiddle"  align="absmiddle" />
                       </logic:equal> 
                       <logic:notEqual name="element" property="itemid" value="e0122">                                         
                           <img src="/images/code.gif"  plugin="codeselector" codesetid='${element.codesetid}' inputname='<%="queryfieldlist["+index+"].viewvalue"%>' valuename='<%="queryfieldlist["+index+"].value"%>' multiple='true' onlySelectCodeset='true' align="absmiddle" align="absmiddle" />
                      </logic:notEqual>   
                    </td>
                     <%
                       if(flag==0)
        	         out.println("</tr>");
                     %>           
                   </logic:equal>
                   <logic:equal name="element" property="codesetid" value="@K">
                       <%
                       if(flag==0)
                       {
                           out.println("<tr>");
                           flag=1;          
                       }else{
                            flag=0;           
                       }
                      %>  
                      <td align="right" height='28' nowrap>
                        <bean:write  name="element" property="itemdesc" filter="true"/></td>
                      <td align="left" nowrap>
                       <html:hidden name="browseForm" property='<%="queryfieldlist["+index+"].value"%>' styleClass="text"/>                               
                       <html:text name="browseForm" onkeydown='<%=delFuntion %>' property='<%="queryfieldlist["+index+"].viewvalue"%>' size="32" maxlength="50" styleClass="text4" onchange="fieldcode(this,2);"/>
                       <logic:equal name="element" property="itemid" value="e01a1"> 
                            <img src="/images/code.gif"  plugin="codeselector" codesetid='@K' nmodule='4' ctrltype='3' inputname='<%="queryfieldlist["+index+"].viewvalue"%>' valuename='<%="queryfieldlist["+index+"].value"%>' multiple='true'  onlySelectCodeset='true' align="absmiddle"  />
                       </logic:equal> 
                       <logic:notEqual name="element" property="itemid" value="e01a1"> 
                       <img src="/images/code.gif"  plugin="codeselector" codesetid='${element.codesetid}' inputname='<%="queryfieldlist["+index+"].viewvalue"%>' valuename='<%="queryfieldlist["+index+"].value"%>' multiple='true' onlySelectCodeset='true' align="absmiddle"  />
                    	</logic:notEqual>
                    </td>
                     <%
                       if(flag==0)
        	         out.println("</tr>");
                     %>           
                   </logic:equal>
                   <logic:notEqual name="element" property="codesetid" value="UN">
                      <logic:notEqual name="element" property="codesetid" value="UM">
                         <logic:notEqual name="element" property="codesetid" value="@K">
                             <logic:greaterThan name="element" property="itemlength" value="20">
                               <!-- 大于 -->
                                <%
                                 if(flag==0)
                                 {
                                   out.println("<tr>");
                                   flag=1;          
                                 }else{
                                   flag=0;           
                                 }
                                %>  
                                <td align="right" height='28' nowrap>
                                  <bean:write  name="element" property="itemdesc" filter="true"/></td>
                                <td align="left" nowrap>
                                  <html:hidden name="browseForm" property='<%="queryfieldlist["+index+"].value"%>' styleClass="text"/>                               
                                  <html:text name="browseForm" onkeydown='<%=delFuntion %>' property='<%="queryfieldlist["+index+"].viewvalue"%>' size="32" maxlength="50" styleClass="text4" onchange="fieldcode(this,2);"/>
                                  <img src="/images/code.gif"  plugin="codeselector" codesetid='${element.codesetid}' inputname='<%="queryfieldlist["+index+"].viewvalue"%>' valuename='<%="queryfieldlist["+index+"].value"%>' multiple='true' onlySelectCodeset='true' align="absmiddle" />
                                </td>
                               <%
                                if(flag==0)
        	                    out.println("</tr>");
                                %>         
                             </logic:greaterThan>
                             <logic:lessEqual  name="element" property="itemlength" value="20">
                               <!-- 小于等于 -->
                                 <%
                                   if(flag==1)
    				    {
    				      out.println("<td colspan=\"2\">");
                                      out.println("</td>");
                                      out.println("</tr>");
    				    }
    				%>		
    				<tr>
    				  <td align="right" height='28' nowrap>       
        	             	    <bean:write  name="element" property="itemdesc" filter="true"/>
        	             	    <html:hidden name="browseForm" styleId='<%="queryfieldlist["+index+"].value"%>' property='<%="queryfieldlist["+index+"].value"%>' styleClass="text"/>                               
        	             	 </td> 
       	             	        <td align="left" colspan="3" nowrap>
       	             	           <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
       	             	             <tr>
       	             	              <td>
       	             	                 <hrms:codesetmultiterm codesetid="${element.codesetid}" itemid="${element.itemid}" itemvalue="${element.value}" rownum="8" hiddenname='<%="queryfieldlist["+index+"].value"%>'/>
    				       </td>
                                    </tr> 
        	             	    </table> 
        	             	</td>
        	             	</tr>
        	             	 <%flag=0;%>
                             </logic:lessEqual>
                         </logic:notEqual>
                      </logic:notEqual>
                   </logic:notEqual>
              </logic:notEqual>
              <logic:equal name="element" property="codesetid" value="0">
              
                                                              
               <% 
                  if(flag==0)
                  {
                   out.println("<tr>");
                         flag=1;          
                  }else{
                       flag=0;           
                  }
              %> 
              <td align="right" height='28' nowrap>
                   <bean:write  name="element" property="itemdesc" filter="true"/>
              </td>
              <td align="left"  nowrap>
               <html:text name="browseForm" property='<%="queryfieldlist["+index+"].value"%>' size="32" maxlength="${element.itemlength}" styleClass="text4"/>
              </td>
              <%
                 if(flag==0)
        			out.println("</tr>");
              %> 
            </logic:equal>             
         </logic:equal>
       </logic:iterate>
        <%
         if(flag==1)
    	{
    		 out.println("<td colspan=\"2\">");
             out.println("</td>");
             out.println("</tr>");
    	}
    	%> 
    	<tr>
    		<td colspan="4">
    		<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
		    	<tr>
		    	  <td align="right" height='20' width="180"  nowrap>
		    	     <logic:equal name="browseForm" property="querySecond" value="1">
		    	     <input type="checkbox" name="query_Second" style="margin-right: 10px;" value="1" onclick="selectCheckBox(this,'querySecond');" checked>
		    	     </logic:equal>
		    	     <logic:notEqual name="browseForm" property="querySecond" value="1">
		    	     <input type="checkbox" name="query_Second" style="margin-right: 10px;" value="1" onclick="selectCheckBox(this,'querySecond');">
		    	     </logic:notEqual>
		    	     <bean:message key="label.query.second"/>
		    	  </td>
		    	  <td align="right" height='20' width="110" nowrap>
	    	           <logic:equal name="browseForm" property="querylike" value="1">
		    	            <input type="checkbox" name="querlike2" style="margin-right: 10px;" value="true" onclick="selectCheckBox(this,'querylike');" checked>
		   	          </logic:equal>  
		   	          <logic:notEqual name="browseForm" property="querylike" value="1">
		    	            <input type="checkbox" name="querlike2" style="margin-right: 10px;" value="true" onclick="selectCheckBox(this,'querylike');">
		   	          </logic:notEqual>
		   	          <bean:message key="label.query.like"/>
		   	       </td>
		    	  <td align="left" colspan="2" height='20' nowrap>
		    	    <table width="100%" border="0" cellspacing="0" cellpadding="0" >
		    	      <tr>
		    	        <td>
		    	           <html:hidden name="browseForm" property='querylike' styleId="querylike" styleClass="text"/>
		    	           <html:hidden name="browseForm" property='querySecond' styleId="querySecond" styleClass="text"/>
		    	        </td>
		    	        <td>   
		    	        <!-- 【5652】员工管理，查询浏览，点击信息浏览，状态栏报错。   jingq upd 2014.12.04 --> 	        
		    	          	<logic:equal name="browseForm" property="querylike" value="1">
		    	              <div  id="info_cue1" class="query_cue1" style="margin-left: 10px;">
		    	               <bean:message key="infor.menu.query.cue1"/>
		    	              </div>
		    	          	</logic:equal>  
		    	          	<logic:notEqual name="browseForm" property="querylike" value="1">
		    	          		<div  id="info_cue1" style='display:none;' class="query_cue1" style="margin-left: 10px;">
		    	               <bean:message key="infor.menu.query.cue1"/>
		    	              </div>
		    	          	</logic:notEqual>
		    	        </td>
		    	      </tr>
		    	    </table>
		    	  </td>    	  
		    	</tr>
		    </table>
		  </td>
		 </tr>
     </table>
   </td>
  </tr>
    <tr>
      <td height="5px">
      </td>
    </tr>
    <tr>
    	  <td align="center" colspan="4" height='20'  nowrap>    	   
    	    <Input type='button' value="<bean:message key="infor.menu.query"/>" onclick="query1('1');" class="mybutton" /> 
    	    <%if(status!=4)
    	    { %>
    	    <Input type='button' value="<bean:message key="button.sys.cond"/>" onclick='selectQ();' class="mybutton" />
    	     <%}else{ %> 
    	     	<hrms:priv func_id="2601008,0303014">
    	     		<Input type='button' value="<bean:message key="button.sys.cond"/>" onclick='selectQ();' class="mybutton" />
    	     	</hrms:priv> 
    	     <%} %>
    	    <Input type='button' value="<bean:message key="button.clear"/>" onclick=' resetQuery();' class="mybutton" />
    	   
    	  </td>
    </tr>
 </table>
 </div>
     <!-- 查询结束 -->
   </td>
</tr>
<tr>
	<td height="3px"></td>
</tr>
<%
String TableRowClass="TableRow_right notop"; 
String RecordRowClass="RecordRow_right notop"; 
%>
<tr >
    <td width="100%" style="padding-left: 5px;" nowrap>
    <div class="myfixedDiv" id='fixedDiv' style="padding: 0;overflow:hidden;">
      <table  style="width:100%;height:10px; color: #000000;" id="MyTable" border="0" cellspacing="0"  cellpadding="0">
           <thead>
           <tr class="fixedHeaderTr">
           <logic:equal name="browseForm" property="caution_color" value="true">
             <th align="center" class="TableRow_right notop" width="30" nowrap>
               &nbsp;  <!-- 颜色警示  class="fixedHeaderTr" --><% column++; %>
              </th>
           </logic:equal>
           <logic:equal name="browseForm" property="ps_card_attach" value="true">
           <!-- 显示岗位说明书 -->
               <th align="center" class="TableRow_right notop" nowrap>
                   <bean:message key="lable.pos.e01a1.manual"/> <% column++; %>           
              </th>
           </logic:equal>
           <logic:equal name="browseForm" property="task_card_attach" value="true">
             <th align="center" class="TableRow_right notop" nowrap>
               <bean:message key="lable.pos.task.manual"/>   <!-- 显示任务说明书 --><% column++; %>
              </th>
           </logic:equal>
           <logic:iterate id="info"    name="browseForm"  property="browsefields" indexId="index">
           		<%TableRowClass="TableRow_right notop"; %>
              <logic:equal name="info" property="visible" value="true">
              <th align="center" class="<%=TableRowClass%>" nowrap>
                   <bean:write  name="info" property="itemdesc" filter="true"/>              
              </th>
              </logic:equal>
              <logic:notEqual value="a0101" name="info" property="itemid">
              	<% column++; %>
              </logic:notEqual>
              <logic:equal value="a0101" name="info" property="itemid">
              	<% columnnum=column; %>
               </logic:equal>
             </logic:iterate> 	
		    <logic:notEqual name="browseForm" property="cardid" value="-1">
             <th align="center" class="TableRow_right notop" nowrap>
		     	<bean:message key="tab.base.info"/>          	
             </th>	 
             </logic:notEqual>               
            <th align="center" class="TableRow_right notop" nowrap>
		     	<bean:message key="tab.synthesis.info"/>          	
	    	</th>
	    	<logic:equal name="browseForm" property="multimedia_file_flag" value="1">
	 		  <th align="center" class="TableRow_right notop" nowrap>
				<bean:message key="conlumn.resource_list.name"/>             	
			 </th>
			</logic:equal> 
           </tr>
           </thead>
           <tbody>
           <hrms:paginationdb id="element" name="browseForm" sql_str="browseForm.strsql" table="" where_str="browseForm.cond_str" columns="browseForm.columns" order_by="order by a0000" page_id="pagination" pagerows="${browseForm.pagerows}" keys="${browseForm.userbase}A01.a0100">
           
           <bean:define id="usA0100" name="element" property="a0100" />
		   <% String  a0100tra = PubFunc.encrypt("a0100="+usA0100);%>
          <%
          if(i%2==0)
          {	
          %>
          <tr class="trShallow" onMouseOver="javascript:tr_onclick(this,'')">
          <%}
          else
          {%> 
          <tr class="trDeep" onMouseOver="javascript:tr_onclick(this,'DDEAFE')">
          <%
          }
          i++;          
          %>  
	     <bean:define id="a0100" name="element" property="a0100"/>	    
	      <logic:equal name="browseForm" property="caution_color" value="true">
             <hrms:cautioncolor colors="${caution_colors}" caution_field="${caution_field}" a0100="${a0100}" nbase="${browseForm.userbase}" codeitems="${caution_codeitems}" bgcolor="bgcolor"></hrms:cautioncolor>
             <td align="center" class="RecordRow_right notop" bgcolor="${bgcolor}" nowrap>             
                &nbsp;
              </td>
           </logic:equal>
	     <logic:equal name="browseForm" property="ps_card_attach" value="true">
            <td align="center" class="RecordRow_right notop" nowrap>            
                 &nbsp;  <hrms:browseaffix pertain_to="post" a0100="${a0100}" nbase="${browseForm.userbase}"></hrms:browseaffix> &nbsp;
            </td>
         </logic:equal>	
         <logic:equal name="browseForm" property="task_card_attach" value="true">
             <td align="center" class="RecordRow_right notop" nowrap>
                 &nbsp;<hrms:browseaffix pertain_to="task" a0100="${a0100}" nbase="${browseForm.userbase}"></hrms:browseaffix> &nbsp;
              </td>
         </logic:equal>
        <hrms:parttime a0100="${a0100}" nbase="${browseForm.userbase}" part_map="<%=partMap%>" name="element" scope="page" code="${browseForm.code}" kind="${browseForm.kind}" uplevel="${browseForm.uplevel}"  b0110_desc="b0110_desc" e0122_desc="e0122_desc" part_desc="part_desc" descOfPart="descOfPart"/>
	     <logic:iterate id="info"    name="browseForm"  property="browsefields" indexId="index">
           <%RecordRowClass="RecordRow_right notop"; %>	     	   	     		
	     	   <logic:equal name="info" property="visible" value="true">	     	   
                  <logic:notEqual  name="info" property="itemtype" value="N">               
                    <td align="left" class="<%=RecordRowClass %>" nowrap>        
                  </logic:notEqual>
                  <logic:equal  name="info" property="itemtype" value="N">               
                    <td align="right" class="<%=RecordRowClass %>" nowrap>        
                  </logic:equal>   
                  <logic:equal  name="info" property="codesetid" value="0">   
                   <logic:notEqual name="info"   property="itemid" value="a0101">  
                   	 <logic:equal  name="info" property="itemtype" value="D">
                   	 &nbsp;
                   	 	<bean:define id="elementvalue" name="element" property="${info.itemid}"></bean:define>
                   	 	<%
                   	 		FieldItem item = (FieldItem)pageContext.getAttribute("info");
                   	 		String value = (String)pageContext.getAttribute("elementvalue");
                   	 		out.write(PubFunc.splitString(value,item.getItemlength()));
						%>
						&nbsp;
                     </logic:equal>
                     <logic:notEqual  name="info" property="itemtype" value="D">   
                     	<bean:define id="itemValue" name="element" property="${info.itemid}"></bean:define> 
                     <%if(itemValue.toString().length() > 20){%>
                    	<div title="<%=itemValue %>" style="width:200px;overflow:hidden;text-overflow:ellipsis; white-space:nowrap;">
                    	&nbsp;&nbsp;<%=itemValue %>
                    	</div> 
                     <%} else {%>  
                     	&nbsp;<bean:write  name="element" property="${info.itemid}" filter="true"/>&nbsp;
					<%} %>                     	
                     </logic:notEqual>
                   </logic:notEqual>
                      <logic:equal name="info"   property="itemid" value="a0101">  
          	   			 &nbsp;
		          	   	 <a href="###" onclick="winhrefOT('<%=a0100tra %>','nil_body');">
          	   			     <bean:write name="element" property="a0101" filter="true"/>
          	   			  </a>
          	   			&nbsp;
          	   	 	</logic:equal>
                  </logic:equal>
                 <logic:notEqual  name="info" property="codesetid" value="0">  
                 &nbsp;
                  <logic:notEqual  name="info"   property="itemid" value="e01a1">  
                   <logic:notEqual  name="info"   property="itemid" value="a0101">  
                    <logic:notEqual  name="info"   property="itemid" value="e0122">  
                     <logic:notEqual  name="info"   property="itemid" value="b0110">  
                      <logic:equal name="info" property="codesetid" value="UM">
                        <hrms:codetoname codeid="UM" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page" uplevel="${browseForm.uplevel}"/>  	      
          	            	<logic:notEqual  name="codeitem" property="codename" value="">
          	           			<bean:write name="codeitem" property="codename" /> 
          	           		</logic:notEqual>
          	          		<logic:equal  name="codeitem" property="codename" value="">
          	          		 	<hrms:codetoname codeid="UN" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page" uplevel="${browseForm.uplevel}"/>  
          	           			<bean:write name="codeitem" property="codename" /> 
          	           		</logic:equal>   
                      </logic:equal>
                      <logic:notEqual name="info" property="codesetid" value="UM">
                        <hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page"/>  	      
          	    	   <bean:write name="codeitem" property="codename" />  
                      </logic:notEqual>
                    </logic:notEqual>
                   </logic:notEqual>
          	     </logic:notEqual>
          	    </logic:notEqual>  
          	    <logic:equal name="info"   property="itemid" value="b0110"> 
          	          ${b0110_desc}
          	         <%if(StringUtils.isNotEmpty(unitID) && StringUtils.isEmpty(deptID) && StringUtils.isEmpty(posID)){%>
          	         ${empty b0110_desc ? descOfPart : part_desc}
          	         <%} %>
          	    </logic:equal> 
          	    <logic:equal name="info"   property="itemid" value="e0122">  
          	         ${e0122_desc}
          	         <%if(StringUtils.isNotEmpty(unitID) && StringUtils.isNotEmpty(deptID) && StringUtils.isEmpty(posID)){%>
          	         ${empty e0122_desc ? descOfPart : part_desc}
          	         <%} %>
          	    </logic:equal> 	    
          	    <logic:equal name="info"   property="itemid" value="e01a1"> 
          	       <logic:empty name="browseForm" property="ishavepostdesc">
                     <hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>  	      
          	         <bean:write name="codeitem" property="codename" />           	         
                   </logic:empty>                
                   <logic:notEmpty name="browseForm" property="ishavepostdesc">
                      <logic:equal name="browseForm" property="ishavepostdesc" value="true">
                          <bean:define id="e01a1" name="element" property="e01a1"/>
                          <% String e01a1tran =PubFunc.encrypt(e01a1.toString()) ;//"~" + SafeCode.encode(PubFunc.convertTo64Base(e01a1.toString())); %>
                          <a href="/workbench/browse/showposinfo.do?b_browse=link&a0100=<%=e01a1tran%>&infokind=4">
                            <hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>  	      
          	                <bean:write name="codeitem" property="codename" />
          	              </a> 
          	         </logic:equal>
          	         <logic:equal name="browseForm" property="ishavepostdesc" value="false">
                        <hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>  	      
          	             <bean:write name="codeitem" property="codename" />           	      
          	          </logic:equal>
                   </logic:notEmpty>     
                   <%if(StringUtils.isNotEmpty(posID)){%>
          	         <logic:empty name="codeitem" property="codename">${descOfPart}</logic:empty>  
          	         <logic:notEmpty name="codeitem" property="codename">${part_desc}</logic:notEmpty>
          	       <%} %>
          	     </logic:equal>          	       
               &nbsp; 
              </logic:notEqual> 
              </td>
              </logic:equal>
             </logic:iterate> 
		    <logic:notEqual name="browseForm" property="cardid" value="-1">
	    			 <td align="center" class="RecordRow_right notop" nowrap>
	    			  &nbsp;
	    			    <% String a0100tran = "~" + SafeCode.encode(PubFunc.convertTo64Base(a0100.toString()));
	    			       String a0100_card=PubFunc.encrypt(browseForm.getUserbase()+"`"+a0100.toString());
	    			    %>
               			<!-- <a href="###" onclick='openwin("/general/inform/synthesisbrowse/synthesiscard.do?b_search=link&userbase=${browseForm.userbase}&a0100=<%=a0100tran%>&inforkind=1&tabid=${browseForm.cardid}&multi_cards=-1&userpriv=nopriv&flag=nopriv");'>  -->
               			<a href="###" onclick='openwin("/module/card/cardCommonSearch.jsp?inforkind=1&a0100=<%=a0100_card%>&tabid=${browseForm.cardid}&callbackfunc=window.close&fieldpriv=1&cardFlag=1");'>
               			<img src="../../images/table.gif" border="0"></a>
               			 &nbsp;
				     </td>	                
             </logic:notEqual>	                		
             <td align="center" class="RecordRow_right notop" nowrap>
          	   	 &nbsp;<a href="###" onclick="winhrefOT('<%=a0100tra %>','nil_body');">
          	   		<img src="../../images/view.gif" border="0">
            	 </a>&nbsp;   	     	   
	      </td>
		     <logic:equal name="browseForm" property="multimedia_file_flag" value="1">
		      	<td align="center" class="RecordRow_right notop" nowrap> 
			     	 &nbsp;<hrms:browseaffix pertain_to="record" a0100="${a0100}" nbase="${browseForm.userbase}" setId="${browseForm.setname}"></hrms:browseaffix>&nbsp;
		     	</td>
		     </logic:equal>
          </tr>
        </hrms:paginationdb>  
        </tbody>      
</table>
    </div>
</td></tr>
<tr>
<td style="padding-left: 5px;">
<div id='pageDiv' style="padding: 0;">
<table width="100%"  align="center" class="RecordRowTop0">
		<tr>
		    <td class="tdFontcolor">
					<hrms:paginationtag name="browseForm"
								pagerows="${browseForm.pagerows}" property="pagination"
								scope="page" refresh="true"></hrms:paginationtag>
			</td>
			<td  align="right" nowrap class="tdFontcolor">
			<p align="right"><hrms:paginationdblink name="browseForm" property="pagination" nameId="browseForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table>
</div>
<table  align="center" style="margin-top: 2px" >
          <tr>
            <td align="left">
            <input type="button" name="addbutton"  value="<bean:message key="button.query.viewphoto"/>" class="mybutton" onclick='viewPhoto();' >  	
	 	    	<logic:notEqual name="browseForm" property="roster" value="no"> 
	 	    		<input type="button" id="exportButton" value="<bean:message key="goabroad.collect.educe.excel"/>" class="mybutton" onclick="executeOutFile();">
	 	    	</logic:notEqual> 
	 	    	<logic:equal value="dxt" name="browseForm" property="returns">       
	 	    	  <!--<hrms:tipwizardbutton flag="emp" target="il_body" formname="browseForm"/>-->
	 	    	  <input type="button" name="returnemp" class="mybutton" value="<bean:message key="button.return"/>" onclick="hrbreturn('emp','il_body','browseForm');"/>
	 	    	</logic:equal>
	 	    	<logic:equal value="leaderdxt" name="browseForm" property="returns">       
                  <input type="button" name="returnemp" class="mybutton" value="<bean:message key="button.return"/>" onclick="hrbreturn('leader','il_body','browseForm');"/>
                </logic:equal>
                <!-- 自助服务导航图返回 -->
                <logic:equal name="browseForm" property="returnvalue" value="dxt">
					<hrms:tipwizardbutton flag="selfinfo" target="il_body" formname="browseForm"></hrms:tipwizardbutton>
				</logic:equal>
	 	    	<logic:equal value="poloicewizard" name="browseForm" property="returnvalue">
                          <input type='button' name='b_save' value='返回' onclick='returnTOWizard();' class='mybutton'>
                </logic:equal>	
                <html:hidden name="browseForm" property="roster"/>
            </td>
          </tr>          
</table>
    </td>
</tr>
</table>

<html:hidden name="browseForm" property="likeflag" styleId="likeflag"/>
</html:form>
</logic:notEqual>
<logic:equal value="1" name="browseForm" property="isphotoview">
<html:form action="/workbench/browse/view_photo">
<div id="ly" style="position:absolute;top:0px;FILTER:alpha(opacity=0);background-color:#FFF;z-index:2;left:0px;display:none;"></div>
<div id='wait' style='position:absolute;top:350;left:200;display:none;'>
		<table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td class="td_style" height=24>
					正在导出照片，请稍候...
				</td>
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
	</div>
<table id='photo_tab' width="80%" border="0" cellspacing="0"  align="center" cellpadding="0" style="margin-top: -2px;">
          <hrms:paginationdb id="element" name="browseForm" sql_str="browseForm.strsql" table="" where_str="browseForm.cond_str" columns="A0100,B0110,E0122,E01A1,A0101,UserName," order_by="browseForm.order_by" pagerows="${browseForm.pagerows}" page_id="pagination" keys="">
           <%
          if(i%4==0)
          {
          %>
          <tr class="photoTr">
          <%
          }
          %>             
          <%
             	LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
             	String a0100=(String)abean.get("a0100"); 
                request.setAttribute("name",a0100);
          %>
          <!-- 标识：3081 信息浏览-照片墙增加兼职信息显示 xiaoyun 2014-7-21 start -->
          <hrms:parttime a0100="${name}" nbase="${browseForm.userbase}" part_map="<%=partMap%>" name="element" scope="page" code="${browseForm.code}" kind="${browseForm.kind}" uplevel="${browseForm.uplevel}"  b0110_desc="b0110_desc" e0122_desc="e0122_desc" part_desc="part_desc" partInfo="partInfo" deptCode="deptCode" unitCode="unitCode" descOfPart="descOfPart"/>
          <!-- 标识：3081 信息浏览-照片墙增加兼职信息显示 xiaoyun 2014-7-21 end -->
          <td align="center" nowrap>
          	<!-- 照片墙样式修改 xiaoyun 2014-6-11 start -->
         	<logic:equal name="browseForm" property="photolength" value=""> 	
          	<ul class="photos">
          		<li>
          			<hrms:ole name="element" photoWall="true" dbpre="browseForm.userbase" href="###" target="nil_body" a0100="a0100" div="" scope="page" height="120" width="85" onclick="winhref('${name}','nil_body')" />
          			<div class="detail">
          				<p><a href="###" onclick="winhref('${name}','nil_body')"><strong><bean:write name="element" property="a0101" filter="true"/></strong></a></p>
          				<p class="linehg">
          					<hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>
          					<!-- 标识：3081 信息浏览-照片墙增加兼职信息显示 xiaoyun 2014-7-21 start -->
          					<hrms:photoviewInfo name="element" a0100="" nbase="" itemid="" isNotSetQuota="true" jobCode="${codeitem.codeitem}" partInfo="${partInfo}" deptCode="${deptCode}" unitCode="${unitCode}" isInfoView="true"  scope="page"/>
          					<!-- 标识：3081 信息浏览-照片墙增加兼职信息显示 xiaoyun 2014-7-21 end -->
                		</p>
          			</div>
          		</li>
          	</ul>
          	</logic:equal>
          	<logic:notEqual name="browseForm" property="photolength" value="">
          		<ul class="photos">
          		  <li>
          		 	<hrms:ole name="element" photoWall="true" dbpre="browseForm.userbase" href="###" target="nil_body" a0100="a0100" div="" scope="page" height="120" width="85" onclick="winhref('${name}','nil_body')" />
          			<div class="detail">
          			<p>
                		<hrms:photoviewInfo name="element" a0100="a0100" params="${name},nil_body" nbase="${browseForm.userbase}" itemid="browseForm.photo_other_view" scope="page"/>
                	</p>
	           </div>
	          </li>
	         </ul>
          	</logic:notEqual>
          	<!-- 照片墙样式修改 xiaoyun 2014-6-11 end -->
          </td> 
          <%
          if((i+1)%4==0)
          {%>
          </tr>
          <%
          }
          i++;          
          %>         
        </hrms:paginationdb>
        
</table>
<table  width="70%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="browseForm" property="pagination" nameId="browseForm" scope="page">
				</hrms:paginationdblink>
		       </td>
		</tr>
</table>
<table  width="70%" align="center">
          <tr>
            <td align="center">       
               <html:button styleClass="mybutton" property="br_return" onclick="returnShow();">
					<bean:message key="workbench.browse.displayinfo"/>
			   </html:button>
			   <hrms:priv func_id="260113">
			   <html:button styleClass="mybutton" property="br_return" onclick="photoexport();">
					<bean:message key="workbench.browse.photoexport"/>
			   </html:button> 
			   </hrms:priv>        	   
            </td>
          </tr>          
</table>
</html:form>
</logic:equal>
<script type="text/javascript">
$(document).ready(function () {
	   <%if(columnnum>5)columnnum=0; else columnnum++;%>
	    //xupengyu修改,照片模式没有mytable 浏览器会报错 
	   <logic:notEqual value="1" name="browseForm" property="isphotoview">
	   FixTable("MyTable", '<%=columnnum %>', document.body.clientWidth-15, document.body.clientHeight-167);///zhancq 2016/8/24 变动主窗口大小
	   $("#table1").css("width",$("#MyTable_tableData").width());
	    var width = document.getElementById("MyTable_tableData").style.width;
	    document.getElementById("fixedDiv").style.height = document.getElementById("MyTable_tableData").style.height;
		document.getElementById("fixedDiv").style.width = width; 
	    document.getElementById("pageDiv").style.width = width;
	   $(window).resize(function (){
	   	  browseForm.submit();
	   });  
	   </logic:notEqual>
	});

</script>
<div id="wait" style='position:absolute;top:285;left:120;display:none;width:500px;heigth:250px'>
 
		<table border="1" width="50%" cellspacing="0" cellpadding="4" class="table_style" height="100" align="center">
			<tr>
			
				<td class="td_style" height=24 id="hlw">
					请稍候，正在导出数据...
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
		    <iframe src="javascript:false" style="position:absolute; visibility:inherit; top:0px; left:0px;width:285px;height:120px;z-index:-1;filter='progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)';">
		    </iframe>	
</div>
<script>
<logic:notEqual value="1" name="browseForm" property="isphotoview">
if(!getBrowseVersion() || getBrowseVersion()==10){//兼容非IE浏览器  wangb 20180130 bug 34329  调整table 文字和文本框之间的距离 和table边框显示不全
	var query = document.getElementById('query');
	var trs = query.getElementsByTagName('tr');
	for(var i = 0; i < trs.length-1 ; i++){
		if(i==0){
			var firsttd = trs[i].getElementsByTagName('td')[0];
			firsttd.setAttribute('colspan','4');
			continue;
		}
		var tds = trs[i].getElementsByTagName('td');
		if(tds[0])
			tds[0].style.paddingRight = '10px';
		
		if(tds[2])
			tds[2].style.paddingRight = '10px';
	}
}
</logic:notEqual>
</script>