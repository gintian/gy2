<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%@page import="com.hrms.hjsj.sys.VersionControl"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<HTML>
<HEAD>
	<TITLE>
	</TITLE>
	<link href="/css/css1.css" rel="stylesheet" type="text/css">
	<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
	<hrms:themes />
	<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
	<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>     
	<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
	<script type="text/javascript" src="../../js/hjsjUrlEncode.js"></script>
	<SCRIPT LANGUAGE=javascript>
	
	function goback()
	{
		window.close();
	
	}	
	function initData()
	{
	    var pre= getCookie("pre");
	    var salaryid=getCookie("salary");
	    if(pre)
	    {
	       if(trim(pre).length>0||pre!="undefined")
	       {
	          var pre_a=pre.substring(1).split(",");
	          for(var i=0;i<pre_a.length;i++)
	          {
	             var obj = document.getElementById(pre_a[i]);
	             if(obj)
	                obj.checked=true;
	          }
	       }
	    }
	      if(salaryid)
	    {
	       if(trim(salaryid).length>0||salaryid!="undefined")
	       {
	          var salaryid_a=salaryid.substring(1).split(",");
	          for(var i=0;i<salaryid_a.length;i++)
	          {
	             var obj = document.getElementById(salaryid_a[i]);
	             if(obj)
	                obj.checked=true;
	          }
	       }
	    }
	    
	}
</SCRIPT>   

<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String b_units=userView.getUnitIdByBusiOutofPriv("1");
    String url_p=SystemConfig.getServerURL(request);
     VersionControl ver = new VersionControl();
 %>  
</HEAD>
<body>
<html:form action="/gz/gz_analyse/gzAnalyseList" method="post">

<table width="90" align="center" border="0" cellpadding="0" cellspacing="0" class="mainbackground" style="margin-top:45px">
<tr>
<td align="left" nowrap>
<fieldset>
<legend><INPUT type="checkbox" name="pre" id="preid" checked="true" onclick="on(this)" value="preArray"/>人员库</legend>
<a href="<%=url_p%>" style="display:none" id="hostname">for vpn</a>
<div id="presalary"  style="overflow:auto;width:250px;height:160px;overflow-y:scroll">

<logic:iterate id="nbase" name="gzAnalyseForm" property="preList" indexId="index">

&nbsp;&nbsp;<input type="checkbox" name="preArray" checked="true" id="<bean:write name="nbase" property="pre"/>" onclick="savePreAndSalaryToCookie('1');" value="<bean:write name="nbase" property="pre"/>"/><bean:write name="nbase" property="dbname"/><br>

</logic:iterate>

</div>
</fieldset>
</td>
<td align="left" nowrap>
<fieldset>
<logic:equal name="gzAnalyseForm" property="gz_module" value="0">
<legend><INPUT type="checkbox" name="salary" id="salaryid" onclick="on(this)" value="salarySetArray"/><bean:message key="sys.res.gzset"/></legend>
</logic:equal>
<logic:equal name="gzAnalyseForm" property="gz_module" value="1">
<legend><INPUT type="checkbox" name="salary" id="salaryid" onclick="on(this)" value="salarySetArray"/><bean:message key="sys.res.ins_set"/></legend>
</logic:equal>
<div id="presalary" style="overflow:auto;width:250px;height:160px;overflow-y:scroll">

<logic:iterate id="salaryset" name="gzAnalyseForm" property="salarySetList" indexId="index">

&nbsp;&nbsp;<input type="checkbox" name="salarySetArray" id="<bean:write name="salaryset" property="salaryid"/>" onclick="savePreAndSalaryToCookie('2');" value="<bean:write name="salaryset" property="salaryid"/>"/><bean:write name="salaryset" property="cname"/><br>

</logic:iterate>

</div>
</fieldset>
</td>
<td valign="center" nowrap>

<div id="www"  style="height:160px;">
<div id="b_design" style="display:none">
&nbsp;&nbsp;<input type="button" class="mybutton" value="设计"/>
<br><br>
</div>

<hrms:priv func_id="32407101,325040101">
<logic:equal name="gzAnalyseForm" property="gz_module" value="0">
<div id="b_add" style="display:none">
&nbsp;&nbsp;<input type="button" class="mybutton" value="新增" onclick="add();"/>
<br><br>
</div>
</logic:equal>
<logic:equal name="gzAnalyseForm" property="gz_module" value="1">
<div id="b_add" style="display:none">
&nbsp;&nbsp;<input type="button" class="mybutton" value="新增" onclick="add();"/>
<br><br>
</div>
</logic:equal>


<div id="b_edit" style="display:none">
&nbsp;&nbsp;<input type="button" class="mybutton" value="修改" onclick="edit();"/>
<br><br>
</div>
</hrms:priv>

<hrms:priv func_id="325040102,32407102">
<div id="b_delete" style="display:none">
&nbsp;&nbsp;<input type="button" class="mybutton" value="删除" onclick="del();"/>
<br><br>
</div>
</hrms:priv>

<div id="b_open" style="display:none">
&nbsp;&nbsp;<input type="button" class="mybutton" value="打开" onclick="showOpen();"/>
<br><br>
</div>

<div id="bb_open" style="display:none">
&nbsp;&nbsp;<input type="button" class="mybutton" value="打开" onclick="showMusterOpen();"/>
<br><br>
</div>
<div id="bbb_open" style="display:none">
&nbsp;&nbsp;<input type="button" class="mybutton" value="打开" onclick="showOpenMusterOne();"/>
<br><br>
</div>
<div id="bbbb_open" style="display:none">
&nbsp;&nbsp;<input type="button" class="mybutton" value="打开" onclick="showOpenMusterTwo();"/>
<br><br>
</div>
<div id="bbbbb_open" style="display:none">
&nbsp;&nbsp;<input type="button" class="mybutton" value="打开" onclick="showOpenMusterThree();"/>
<br><br>
</div>
<div id="bbbbbb_open" style="display:none">
&nbsp;&nbsp;<input type="button" class="mybutton" value="打开" onclick="showCustom();"/>
<br><br>
</div>
<div id="bbbbbbb_open" style="display:none">
&nbsp;&nbsp;<input type="button" class="mybutton" value="打开" onclick="showXLSCustom();"/>
<br><br>
</div>
<div id="simpleMuster_open" style="display:none">
&nbsp;&nbsp;<input type="button" class="mybutton" value="打开" onclick="showSimpleMuster();"/>
<br><br>
</div>
<div >
<logic:equal name="gzAnalyseForm" property="gz_module" value="0">
&nbsp;&nbsp;<hrms:tipwizardbutton flag="compensation" target="il_body" formname="gzAnalyseForm"/> 
<br><br>
</logic:equal>
<logic:equal name="gzAnalyseForm" property="gz_module" value="1">
&nbsp;&nbsp;<hrms:tipwizardbutton flag="insurance" target="il_body" formname="gzAnalyseForm"/> 
<br><br>
</logic:equal>
</div>
</div>
</td>
</tr>
<td align="left" colspan="2" valign="top" nowrap>		
<fieldset>
<legend>分析报表</legend>		
			<div id="treemenu" style="overflow:auto;width:500px;height:180px;"></div>
</fieldset>
</td>
<td>





</td>
<tr>
</tr>
<tr>
<td colspan='3' align="left">
<input type="checkbox" name="his" value="0" id='h' onclick="en(this)"/>归档数据分析
<hrms:priv func_id="32407103">
<input type="checkbox" name="sp" value="0" id='s'/>包含审批过程的数据
</hrms:priv>
</td>
</tr>
</table>
<script language="javascript">
initData();
</script>
</html:form>
</body>
</html>
<script language="javascript">

    var rsid;
	var rsdtlid;
	var focus_obj_node;

	var gz_module = "${gzAnalyseForm.gz_module}";
	var m_sXMLFile	= "/gz/gz_analyse/gzAnalyseTree.jsp?gz_module="+gz_module;		
	var newwindow;
	var rootname="";
	if(gz_module =="0")
	{
	    rootname="薪资分析";
	}else if(gz_module == "1")
	{
	   rootname="保险分析";
	}
	var root=new xtreeItem("root",rootname,"","mil_body",rootname,"/images/add_all.gif",m_sXMLFile);
	//Global.defaultInput=1;
	Global.showroot=true;
	
	root.setup(document.getElementById("treemenu"));	
	if(newwindow!=null)
	{
		newwindow.focus();
	}
	if(parent.parent.myNewBody!=null)
	{
		parent.parent.myNewBody.cols="*,0"
	}
	
	
	var focus_obj_node=root.getFirstChild();
	focus_obj_node.expand();
	function showEidtButton()
	{
		focus_obj_node=Global.selectedItem;
		rsdtlid=focus_obj_node.uid;
		rsid=focus_obj_node.parent.uid;	
		var obj=document.getElementById("b_add");
		if(obj)
	    	obj.style.display="none";
		var obj1=document.getElementById("b_edit");
		var obj2=document.getElementById("b_delete");
		if(parseInt(rsid)==8||parseInt(rsid)==9||parseInt(rsid)==17)
		{
		    if(obj1)
	    	   obj1.style.display="none";
	    	if(obj2)
	        	obj2.style.display="none";
		}
		else
		{
		   if(obj1)
		      obj1.style.display="block";
		   if(obj2)
	         obj2.style.display="block";
		}
		var obj3=eval("b_open");
		obj3.style.display="block";
		var obj4 = eval("bb_open");
		obj4.style.display="none";
		var obj5 = eval("bbb_open");
		obj5.style.display="none";
		var obj6 = eval("bbbb_open");
		obj6.style.display="none";
		var obj7 = eval("bbbbb_open");
		obj7.style.display="none";
		var obj8 = eval("bbbbbb_open");
		obj8.style.display="none";
		var obj9 = eval("bbbbbbb_open");
		obj9.style.display="none";
		var obj10 = eval("simpleMuster_open");
        obj10.style.display="none";
		//focus_obj_node=Global.selectedItem;
		//rsdtlid=focus_obj_node.uid;
		//rsid=focus_obj_node.parent.uid;		
	}
	function showNewButton()
	{
		var obj=document.getElementById("b_add");
		if(obj)
		   obj.style.display="block";
		var obj1=document.getElementById("b_edit");
		if(obj1)
		  obj1.style.display="none";
		var obj2=document.getElementById("b_delete");
		if(obj2)
	    	obj2.style.display="none";
		var obj3=eval("b_open");
		obj3.style.display="none";
		var obj4 = eval("bb_open");
		obj4.style.display="none";
		var obj5 = eval("bbb_open");
		obj5.style.display="none";
		var obj6 = eval("bbbb_open");
		obj6.style.display="none";
		var obj7 = eval("bbbbb_open");
		obj7.style.display="none";
		var obj8 = eval("bbbbbb_open");
		obj8.style.display="none";
		var obj9 = eval("bbbbbbb_open");
		obj9.style.display="none";
		var obj10 = eval("simpleMuster_open");
        obj10.style.display="none";
		focus_obj_node=Global.selectedItem;
	}
	function openBbButton()
	{
	    var obj=document.getElementById("b_add");
	    if(obj)
		  obj.style.display="none";
		var obj1=document.getElementById("b_edit");
		if(obj1)
		  obj1.style.display="none";
		var obj2=document.getElementById("b_delete");
		if(obj2)
	    	obj2.style.display="none";
		var obj3=eval("b_open");
		obj3.style.display="none";
		var obj4=eval("bb_open");
		obj4.style.display="block";
		var obj5 = eval("bbb_open");
		obj5.style.display="none";
		var obj6 = eval("bbbb_open");
		obj6.style.display="none";
		var obj7 = eval("bbbbb_open");
		obj7.style.display="none";
		var obj8 = eval("bbbbbb_open");
		obj8.style.display="none";
		var obj9 = eval("bbbbbbb_open");
		obj9.style.display="none";
		var obj10 = eval("simpleMuster_open");
        obj10.style.display="none";
		focus_obj_node=Global.selectedItem;
	}
	function closeButton()
	{
	   var obj=document.getElementById("b_add");
	   if(obj)
		  obj.style.display="none";
		var obj1=document.getElementById("b_edit");
		if(obj1)
		  obj1.style.display="none";
		var obj2=document.getElementById("b_delete");
		if(obj2)
	    	obj2.style.display="none";
		var obj3=eval("b_open");
		obj3.style.display="none";
		var obj4 = eval("bb_open");
		obj4.style.display="none";
		var obj5 = eval("bbb_open");
		obj5.style.display="none";
		var obj6 = eval("bbbb_open");
		obj6.style.display="none";
		var obj7 = eval("bbbbb_open");
		obj7.style.display="none";
		var obj8 = eval("bbbbbb_open");
		obj8.style.display="none";
		var obj9 = eval("bbbbbbb_open");
		obj9.style.display="none";
		var obj10 = eval("simpleMuster_open");
        obj10.style.display="none";
		focus_obj_node=Global.selectedItem;
	}
	function del()
	{
	    var currnode=Global.selectedItem;
	    if(currnode.uid=="root")
	    {
	        return;
	    }
	    if(ifdel())
	    {
		 var a_parent=currnode.parent;
		 var hashvo=new ParameterSet();
	     hashvo.setValue("rsdtlid",currnode.uid);	
	     hashvo.setValue("rsid",a_parent.uid);       
	   　 var request=new Request({asynchronous:false,onSuccess:delete_ok,functionId:'3020130012'},hashvo);      
	    }
	}
	
	function delete_ok(outparamters)
	{
	     var currnode=Global.selectedItem;
	     var preitem=currnode.getPreviousSibling();
	     currnode.remove();
	     preitem.select(preitem);
	}
function isOutofPriv(){
	var b_units = '<%=b_units%>';
	if(b_units=="UN"||b_units==""){
		alert("管理员没有给您授权业务范围或操作单位，您无权查看数据！");
		return true;
	}else{
		return false;
	}
}
	function add()
	{
		
		var arguments=new Array(); 
        var gz_module = "${gzAnalyseForm.gz_module}";    
	    var strurl="/gz/gz_analyse/addGzReport.do?b_query=link`gz_model="+gz_module+"`isclose=0`opt=new`rsid="+focus_obj_node.uid;
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl); 
        var flag=window.showModalDialog(iframe_url,arguments,"dialogWidth=600px;dialogHeight=420px;resizable=yes;scroll=no;status=no;");  
	    if(flag)
		{
		   	focus_obj_node.clearChildren();
	    	focus_obj_node.loadChildren();
			focus_obj_node.expand();
		}
	}
	function edit()
	{
		var arguments=new Array();     
		var gz_module = "${gzAnalyseForm.gz_module}";    
	   var strurl="/gz/gz_analyse/addGzReport.do?b_query=link`gz_model="+gz_module+"`isclose=0`opt=edit`rsdtlid="+focus_obj_node.uid+"`rsid="+focus_obj_node.parent.uid;
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl); 
	    var flag=window.showModalDialog(iframe_url,arguments,"dialogWidth=600px;dialogHeight=420px;resizable=yes;scroll=no;status=no;");  
		if(flag)
		{
		   	var a_parent=focus_obj_node.parent;
		   	a_parent.clearChildren();
	    	a_parent.loadChildren();
			a_parent.expand();
		}
	}
	function check_ok(outparameters)
	{
	var gz_module = "${gzAnalyseForm.gz_module}";    
	var  msg=outparameters.getValue("msg");
	if(msg=='1')
	{
    	if(gz_module=='0')
    	{
	       alert("请为 ["+focus_obj_node.text+"] 选择工资项目");
	    }else{
	       alert("请为 ["+focus_obj_node.text+"] 选择保险项目");
	     }
	    return;
	}
	var preObj = document.getElementsByName("preArray");
	var salaryArray=document.getElementsByName("salarySetArray");
    var pre="";
    if(preObj)
    {
       for(var i=0;i<preObj.length;i++)
       {
          if(preObj[i].checked)
            pre+=","+preObj[i].value;
       }
    }
	    if(trim(pre).length==0||pre=="undefined"){
	        alert("请选择人员库");
	        return;
	    }
	    var salaryid="";
	    if(salaryArray)
       {
           for(var i=0;i<salaryArray.length;i++)
           {
             if(salaryArray[i].checked)
              salaryid+=","+salaryArray[i].value;
           }
       }
	    if(trim(salaryid).length==0||salaryid == "undefined")
	    { 
	        if(gz_module=='0')
	        {
	           alert("请选择薪资类别");
	        }
	        else{
	           alert("请选择保险类别");
	        }
	        return;
	    }
	    pre=pre.substring(1);
        salaryid=salaryid.substring(1);
        rsdtlid=focus_obj_node.uid;
	    rsid=focus_obj_node.parent.uid;
	    var window_height=window.screen.availHeight-80;
	    var window_width=window.screen.availWidth-20;
	    
	    var a=900-window_height;
	    var b=a%30;
	    var _height=window_height-85-(b*5);
		var Actual_Version=browserinfo();
		if (Actual_Version!=null&&Actual_Version.length>0&&Actual_Version=='7.0') {
				  	
				   _height=_height-25;
			 }
		if (Actual_Version!=null&&Actual_Version.length>0&&Actual_Version=='8.0') {
				  	
				   _height=_height-50;
			 }
		if (Actual_Version!=null&&Actual_Version.length>0&&Actual_Version=='9.0') {
				  	
				   _height=_height-10;
			 }

	var archive="1";//=1分析历史数据
	var eobj=document.getElementById("h");
	var obj=document.getElementById("s");
		if(eobj.checked){
	    	archive="0";
	    }else if(obj!=null&&obj.checked){
	    	archive="3";
	    }
    var theArr=new Array(focus_obj_node.parent.text,focus_obj_node.text); 
    var rl = document.getElementById("hostname").href; 
	var thecodeurl ="/gz/gz_anaylse/open_gz_analyse_report.do?b_query=link`archive="+archive+"`rsdtlid="+rsdtlid+"`rsid="+rsid+"`salaryid="+salaryid+"`pre="+pre+"`w="+(window_width-10)+"`h="+(window_height-15)+"`pt="+rl;
	var iframe_url="/gz/gz_analyse/gz_analyse_iframe.jsp?src="+$URL.encode(thecodeurl);
    var return_vo= window.showModalDialog(iframe_url,theArr, 
              "dialogWidth="+window_width+"px;dialogHeight="+window_height+"px;resizable=yes;scroll=no;status=no;");
	}
	function en(eobj){
		var obj=document.getElementById("s");
		if(!obj){
			return;
		}
		if(eobj.checked){
			obj.checked=false;
			obj.disabled=true;
		}else{
			obj.disabled=false;
		}
	}
	function showOpen()
	{
		if(isOutofPriv()){
			return;
		}
	  rsdtlid=focus_obj_node.uid;
	  rsid=focus_obj_node.parent.uid;
	  var hashvo=new ParameterSet();
     hashvo.setValue("rsid",rsid);
     hashvo.setValue("rsdtlid",rsdtlid);
     var request=new Request({asynchronous:false,onSuccess:check_ok,functionId:'3020130022'},hashvo);
    }
	  function browserinfo(){
        var Browser_Name=navigator.appName;
        var Browser_Version=parseFloat(navigator.appVersion);
        var Browser_Agent=navigator.userAgent;
        
        var Actual_Version;
        var is_IE=(Browser_Name=="Microsoft Internet Explorer");
        if(is_IE){
            var Version_Start=Browser_Agent.indexOf("MSIE");
            var Version_End=Browser_Agent.indexOf(";",Version_Start);
            Actual_Version=Browser_Agent.substring(Version_Start+5,Version_End)
        }
       return Actual_Version;
    }
   
	function savePreAndSalaryToCookie(type)
	{
	    var name="";
	    var value="";
	    var obj;
	    if(type=='1')
	    {
	        name="pre";
	        obj=document.getElementsByName("preArray");
	    }
	     else
	     {
	        name="salary";
	        obj=document.getElementsByName("salarySetArray");
	     }
	    for(var i=0;i<obj.length;i++)
	    {
	          if(obj[i].checked)
	          {
	              value+=","+obj[i].value;
	          }
	    }
	    deleteCookie(name);
	    addCookie(name,value,1);
	}
	function addCookie(name,value,expireHours)
	{
       var cookieString=name+"="+escape(value);
       //判断是否设置过期时间
       if(expireHours>=0)
       {
            var date=new Date();
            date.setTime(date.getTime()+expireHours*3600*1000); // 转换为毫秒
            cookieString=cookieString+"; expires="+date.toGMTString();
       }
     document.cookie=cookieString;
    }
    function getCookie(name)
    {
       var strCookie=document.cookie;
       var arrCookie=strCookie.split("; ");
       for(var i=0;i<arrCookie.length;i++)
       { // 遍历cookie数组，处理每个cookie对
          var arr=arrCookie[i].split("=");
          if(arr[0]==name)
          {
              return unescape(arr[1]);
          }
       }
    return "";
    }
    function deleteCookie(name)
    {
      var date=new Date();
      date.setTime(date.getTime()-10000); // 删除一个cookie，将其过期时间设定为一个过去的时间
      document.cookie=name+"=v; expire="+date.toGMTString();
    }
    function openGzFareAnalyse()
    {
      document.location.href="/gz/gz_analyse/gz_fare/fare_analyse_orgtree.do?b_query=query&opt=init";
      //alert(gzAnalyseForm.action);
      // gzAnalyseForm.submit();
    }
  
function showMusterOpen(){  
		if(isOutofPriv()){
			return;
		}
	 var pre= getPreAndSalaryArray("preArray");
	 if(pre==null||trim(pre).length==0||pre=="undefined"){
	     alert("请选择人员库");
	     return;
	}
	var gz_module = "${gzAnalyseForm.gz_module}"; 
	var salaryid=getPreAndSalaryArray("salarySetArray");
	if(trim(salaryid).length==0||salaryid == "undefined"){ 
		if(parseInt(gz_module)==0)
	    	alert("请选择薪资类别");
	    else if(parseInt(gz_module)==1)
	    	alert("请选择保险类别");
	    return;
	}
	var archive="1";//=1分析历史数据
	var eobj=document.getElementById("h");
	var obj=document.getElementById("s");
		if(eobj.checked){
	    	archive="0";
	    }else if(obj!=null&&obj.checked){
	    	archive="3";
	    }
	var iframe_url="/gz/gz_analyse/gz_setinfor.do?b_query=link&archive="+archive+"&titlename="+getEncodeStr(focus_obj_node.text);
		iframe_url+="&tabid="+focus_obj_node.uid+"&gz_module="+gz_module+"&dbname="+pre+"&category="+salaryid;
    var return_vo= window.showModalDialog(iframe_url,"",
    	"dialogWidth=420px;dialogHeight=370px;resizable=no;scroll=no;status=no;");
}
function showOpenMusterOneButton()
{
       var obj=document.getElementById("b_add");
       if(obj)
		  obj.style.display="none";
		var obj1=document.getElementById("b_edit");
		if(obj1)
		   obj1.style.display="none";
		var obj2=document.getElementById("b_delete");
		if(obj2)
		   obj2.style.display="none";
		var obj3=eval("b_open");
		obj3.style.display="none";
		var obj4=eval("bb_open");
		obj4.style.display="none";
		var obj5 = eval("bbb_open");
		obj5.style.display="block";
		var obj6 = eval("bbbb_open");
		obj6.style.display="none";
		var obj7 = eval("bbbbb_open");
		obj7.style.display="none";
		var obj8 = eval("bbbbbb_open");
		obj8.style.display="none";
		var obj9 = eval("bbbbbbb_open");
		obj9.style.display="none";
		var obj10 = eval("simpleMuster_open");
        obj10.style.display="none";
		focus_obj_node=Global.selectedItem;
}
function showOpenMusterTwoButton()
{
       var obj=document.getElementById("b_add");
       if(obj)
		   obj.style.display="none";
		var obj1=document.getElementById("b_edit");
		if(obj1)
		   obj1.style.display="none";
		var obj2=document.getElementById("b_delete");
		if(obj2)
	    	obj2.style.display="none";
		var obj3=eval("b_open");
		obj3.style.display="none";
		var obj4=eval("bb_open");
		obj4.style.display="none";
		var obj5 = eval("bbb_open");
		obj5.style.display="none";
		var obj6 = eval("bbbb_open");
		obj6.style.display="block";
		var obj7 = eval("bbbbb_open");
		obj7.style.display="none";
		var obj8 = eval("bbbbbb_open");
		obj8.style.display="none";
		var obj9 = eval("bbbbbbb_open");
		obj9.style.display="none";
		var obj10 = eval("simpleMuster_open");
        obj10.style.display="none";
		focus_obj_node=Global.selectedItem;
}
function showOpenMusterThreeButton()
{
       var obj=document.getElementById("b_add");
       if(obj)
		obj.style.display="none";
		var obj1=document.getElementById("b_edit");
		if(obj1)
		  obj1.style.display="none";
		var obj2=document.getElementById("b_delete");
		if(obj2)
		   obj2.style.display="none";
		var obj3=eval("b_open");
		obj3.style.display="none";
		var obj4=eval("bb_open");
		obj4.style.display="none";
		var obj5 = eval("bbb_open");
		obj5.style.display="none";
		var obj6 = eval("bbbb_open");
		obj6.style.display="none";
		var obj7 = eval("bbbbb_open");
		obj7.style.display="block";
		var obj8 = eval("bbbbbb_open");
		obj8.style.display="none";
		var obj9 = eval("bbbbbbb_open");
		obj9.style.display="none";
		var obj10 = eval("simpleMuster_open");
        obj10.style.display="none";
		focus_obj_node=Global.selectedItem;
}
function showOpenCustomButton()
{
       var obj=document.getElementById("b_add");
       if(obj)
		  obj.style.display="none";
		var obj1=document.getElementById("b_edit");
		if(obj1)
	    	obj1.style.display="none";
		var obj2=document.getElementById("b_delete");
		if(obj2)
	    	obj2.style.display="none";
		var obj3=eval("b_open");
		obj3.style.display="none";
		var obj4=eval("bb_open");
		obj4.style.display="none";
		var obj5 = eval("bbb_open");
		obj5.style.display="none";
		var obj6 = eval("bbbb_open");
		obj6.style.display="none";
		var obj7 = eval("bbbbb_open");
		obj7.style.display="none";
		var obj8 = eval("bbbbbb_open");
		obj8.style.display="block";
		var obj9 = eval("bbbbbbb_open");
		obj9.style.display="none";
		var obj10 = eval("simpleMuster_open");
        obj10.style.display="none";
		focus_obj_node=Global.selectedItem;
}
function showOpenCustomXLSButton()
{
       var obj=document.getElementById("b_add");
       if(obj)
		 obj.style.display="none";
		var obj1=document.getElementById("b_edit");
		if(obj1)
		   obj1.style.display="none";
		var obj2=document.getElementById("b_delete");
		if(obj2)
	    	obj2.style.display="none";
		var obj3=eval("b_open");
		obj3.style.display="none";
		var obj4=eval("bb_open");
		obj4.style.display="none";
		var obj5 = eval("bbb_open");
		obj5.style.display="none";
		var obj6 = eval("bbbb_open");
		obj6.style.display="none";
		var obj7 = eval("bbbbb_open");
		obj7.style.display="none";
		var obj8 = eval("bbbbbb_open");
		obj8.style.display="none";
		var obj9 = eval("bbbbbbb_open");
		obj9.style.display="block";
		var obj10 = eval("simpleMuster_open");
        obj10.style.display="none";
		focus_obj_node=Global.selectedItem;
}
function showOpenMusterThreeButton()
{
       var obj=document.getElementById("b_add");
       if(obj)
        obj.style.display="none";
        var obj1=document.getElementById("b_edit");
        if(obj1)
          obj1.style.display="none";
        var obj2=document.getElementById("b_delete");
        if(obj2)
           obj2.style.display="none";
        var obj3=eval("b_open");
        obj3.style.display="none";
        var obj4=eval("bb_open");
        obj4.style.display="none";
        var obj5 = eval("bbb_open");
        obj5.style.display="none";
        var obj6 = eval("bbbb_open");
        obj6.style.display="none";
        var obj7 = eval("bbbbb_open");
        obj7.style.display="block";
        var obj8 = eval("bbbbbb_open");
        obj8.style.display="none";
        var obj9 = eval("bbbbbbb_open");
        obj9.style.display="none";
        var obj10 = eval("simpleMuster_open");
        obj10.style.display="none";
        focus_obj_node=Global.selectedItem;
}
function showSimpleMusterButton()
{
       var obj=document.getElementById("b_add");
       if(obj)
          obj.style.display="none";
        var obj1=document.getElementById("b_edit");
        if(obj1)
            obj1.style.display="none";
        var obj2=document.getElementById("b_delete");
        if(obj2)
            obj2.style.display="none";
        var obj3=eval("b_open");
        obj3.style.display="none";
        var obj4=eval("bb_open");
        obj4.style.display="none";
        var obj5 = eval("bbb_open");
        obj5.style.display="none";
        var obj6 = eval("bbbb_open");
        obj6.style.display="none";
        var obj7 = eval("bbbbb_open");
        obj7.style.display="none";
        var obj8 = eval("bbbbbb_open");
        obj8.style.display="none";
        var obj9 = eval("bbbbbbb_open");
        obj9.style.display="none";
        var obj10 = eval("simpleMuster_open");
        obj10.style.display="block";
        focus_obj_node=Global.selectedItem;
}
//打开人员花名册
function showOpenMusterOne()
{
		if(isOutofPriv()){
			return;
		}
  var tabid=focus_obj_node.uid;
  var theArr=new Array(focus_obj_node.parent.text,focus_obj_node.text); 
  var thecodeurl ="/general/muster/hmuster/select_muster_name.do?b_custom=link`nFlag=3`a_inforkind=1`result=0`isGetData=1`operateMethod=direct`costID="+tabid;     	  
  var iframe_url="/gz/gz_analyse/gz_analyse_iframe.jsp?src="+$URL.encode(thecodeurl);
  var return_vo= window.showModalDialog(iframe_url,theArr, 
              "dialogWidth="+window.screen.width+"px;dialogHeight="+window.screen.height+"px;resizable=yes;scroll=no;status=no;");
  
}
//打开机构花名册
function showOpenMusterTwo()
{
		if(isOutofPriv()){
			return;
		}
  var tabid=focus_obj_node.uid;
  var theArr=new Array(focus_obj_node.parent.text,focus_obj_node.text); 
  var thecodeurl ="/general/muster/hmuster/select_muster_name.do?b_custom=link`nFlag=21`a_inforkind=2`result=0`isGetData=1`operateMethod=direct`costID="+tabid;     	  
  var iframe_url="/gz/gz_analyse/gz_analyse_iframe.jsp?src="+$URL.encode(thecodeurl);
  var return_vo= window.showModalDialog(iframe_url,theArr, 
              "dialogWidth="+window.screen.width+"px;dialogHeight="+window.screen.height+"px;resizable=yes;scroll=no;status=no;");
  
}
//打开职位花名册
function showOpenMusterThree()
{
		if(isOutofPriv()){
			return;
		}
  var tabid=focus_obj_node.uid;
  var theArr=new Array(focus_obj_node.parent.text,focus_obj_node.text); 
  var thecodeurl ="/general/muster/hmuster/select_muster_name.do?b_custom=link`nFlag=41`a_inforkind=3`result=0`isGetData=1`operateMethod=direct`costID="+tabid;     	  
  var iframe_url="/gz/gz_analyse/gz_analyse_iframe.jsp?src="+$URL.encode(thecodeurl);
  var return_vo= window.showModalDialog(iframe_url,theArr,  "dialogWidth="+window.screen.width+"px;dialogHeight="+window.screen.height+"px;resizable=yes;scroll=no;status=no;");
  
}
//打开自定义报表
function showCustom()
{
		if(isOutofPriv()){
			return;
		}
   var url="/system/options/customreport/displaycustomreportservlet?ispriv=1&id="+focus_obj_node.uid;
   window.open(url,"_blank","left=0,top=0,width="+screen.availWidth+",height="+screen.availHeight+",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");
}
function showXLSCustom()
{
		if(isOutofPriv()){
			return;
		}
    //var hashvo=new ParameterSet();
	//hashvo.setValue("id",focus_obj_node.uid);	
	//hashvo.setValue("ispriv","1");
	//var request=new Request({method:'post',asynchronous:false,onSuccess:showReport,functionId:'10100103411'},hashvo);
	var url="/system/options/customreport/displaycustomreportservlet?ispriv=1&id="+focus_obj_node.uid;
   window.open(url,"_blank","left=0,top=0,width="+screen.availWidth+",height="+screen.availHeight+",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");
	
}
//打开简单名册报表
function showSimpleMuster()
{
    window.open(focus_obj_node.uid,"_blank","left=0,top=0,width="+screen.availWidth+",height="+screen.availHeight+",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");
}
function showReport(outparamters)
   {
      var ext=outparamters.getValue("ext");
	  var filename=outparamters.getValue("filename");	
	  if(ext.indexOf('xls')!=-1)
	  {
	     gzAnalyseForm.action="/servlet/DisplayOleFile?filename="+filename;
	     gzAnalyseForm.submit();
	  }
	  
	}
function getPreAndSalaryArray(type){
	var value="";
	var obj=document.getElementsByName(type);
	for(var i=0;i<obj.length;i++){
	    if(obj[i].checked){
	       value+=obj[i].value+",";
	    }
	}
	return value;
}
function on(obj){
	var name="";
	var value="";
	var obj1=document.getElementsByName(obj.value);
	if(obj.checked){
		for(var i=0;i<obj1.length;i++){	 
			name=obj.name;
			value+=","+obj1[i].value;
			value+=","+obj.id;
	       obj1[i].checked=true;
		}
	}else{
		for(var i=0;i<obj1.length;i++){	  
			name=obj.name;  

	       obj1[i].checked=false;
		}
	}
	deleteCookie(name);
	addCookie(name,value,1);
}
</script>