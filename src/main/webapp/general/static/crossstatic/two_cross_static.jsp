<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="java.util.*"%>
<%@ page import="java.util.Map.*"%>
<%@ page import="com.hjsj.hrms.actionform.general.statics.CrossStaticForm"%>
<%@ page import="com.hrms.struts.taglib.CommonData"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>

<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="JavaScript" src="/anychart/js/AnyChart.js"></script>
<script language="JavaScript" src="/ajax/basic.js"></script>
<script language="JavaScript" src="../../../module/utils/js/template.js"></script>
<script language="JavaScript" src="../../../components/extWidget/field/CodeTreeCombox.js"></script>
<script type="text/javascript" src="../../../ext/rpc_command.js"></script> 
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script> 
<%
  	if(request.getParameter("hideFlag") == null ){//多维统计请求中带有hideFlag参数控制多维统计定义区域 放到session中存放 wangb 20180804 bug 39405
    	session.setAttribute("hideFlag","");
  	}else{
  		session.setAttribute("hideFlag",request.getParameter("hideFlag") );
  	}

	UserView userView = (UserView) session.getAttribute(WebConstant.userView);
	String bosflag = "";
	if (userView != null) {
		bosflag = userView.getBosflag();
	}
	CrossStaticForm crossStaticForm = (CrossStaticForm) session.getAttribute("crossStaticForm");
	String totalvalue = crossStaticForm.getTotalvalue();
	totalvalue = totalvalue==null?"0":totalvalue;
	double totalvalues = Double.parseDouble(totalvalue);
	ArrayList statIdslist = crossStaticForm.getStatIdslist();
	int statlength = 0;
	if (statIdslist != null) {
		statlength = statIdslist.size();
	}
	int k = 0;
	
	String hideFlag = (String)session.getAttribute("hideFlag");
%>
<link href="/css/css1.css" rel="stylesheet" type="text/css">
<style>
.div_table{
    border-width: 1px;
    BORDER-BOTTOM: #aeac9f 1pt solid; 
    BORDER-LEFT: #aeac9f 1pt solid; 
    BORDER-RIGHT: #aeac9f 1pt solid; 
    BORDER-TOP: #aeac9f 1pt solid ; 

}
.tdFontcolor{
	text-decoration: none;
	Font-family:;
	font-size:12px;
	height:12px;
	align:"center"
}
</style>
<script language="JavaScript">
var statid = '<%=request.getParameter("statid")%>';
var stats = new Array();
<%for(int i=0;i<statlength;i++){%>
	stats[<%=i%>] = <%=(String) ((CommonData) statIdslist.get(i)).getDataValue()%>;
<%}%>
function additems1(sourcebox_id,targetbox_id){
	var left_vo,right_vo,vos,i;
	vos= document.getElementsByName(sourcebox_id);
	if(vos==null)
		return false;
	left_vo=vos[0];
	vos= document.getElementsByName(targetbox_id);
	if(vos==null)
		return false;
	right_vo=vos[0];
	var flag = false;
	for(i=0;i<left_vo.options.length;i++){
		if(left_vo.options[i].selected)
	    {
	        var no = new Option();
	    	no.value="1_"+left_vo.options[i].value;
	    	no.text=left_vo.options[i].text;
	    	right_vo.options[right_vo.options.length]=no;
	    	flag=true;
		}
	}
	removeitem(sourcebox_id);
	return flag;	  	
}
function additems2(type,sourcebox_id,targetbox_id){
	var left_vo,right_vo,vos,i,lnum,j;
	vos = document.getElementsByName(sourcebox_id);
	if(vos==null)
		return false;
	left_vo = vos[0];
	vos = document.getElementsByName(targetbox_id);  
	if(vos==null)
		return false;
	right_vo = vos[0];
	if(right_vo.options.length == '0'){//防止第一个直接为二级维度指标
		alert("您尚未选择一级维度！");
		return;
	}
	lnum = 0;//纵向或横向维度选择的数量
	for(i = 0;i < right_vo.options.length;i++){
		if(right_vo.options[i].selected)
	    {
	    	lnum++;
	    	j=i;  // 插入位置
		}
	}
	if(lnum > 1){
		if(type == "l"){
			alert("纵向维度最多只能选择一个！");
			return;
		}else if(type == "c"){
			alert("横向维度最多只能选择一个！");
			return;
		}
	}
	var flag = false;
	if(lnum == 1){// 已选维度个数为1
		var snum=0;//中间的选择维度选择的数量
		for(i=0;i<left_vo.options.length;i++){
			if(left_vo.options[i].selected)
		    {
		    	snum++;
			}
		}
		if(right_vo.options.length != j+1){
			for(i = right_vo.options.length-1;i > j;i--){
				var op = new Option();
		    	op.value = right_vo.options[i].value;
		    	op.text = right_vo.options[i].text;
				right_vo.options[i+snum]=op;
			}
			for(i = 0;i < left_vo.options.length;i++){
				if(left_vo.options[i].selected)
			    {
			        var no = new Option();
			    	no.value="2_"+left_vo.options[i].value;
			    	no.text="　　"+left_vo.options[i].text;
			    	right_vo.options[j+1]=no;
			    	j++;
			    	flag=true;
				}
			}
		}else{
			for(i = 0;i < left_vo.options.length;i++){
				if(left_vo.options[i].selected)
			    {
			        var no = new Option();
			    	no.value="2_"+left_vo.options[i].value;
			    	no.text="　　"+left_vo.options[i].text;
			    	right_vo.options[right_vo.options.length]=no;
			    	flag=true;
				}
			}
		}
	}else{// // 已选维度个数为0
		for(i = 0;i < left_vo.options.length;i++){
			if(left_vo.options[i].selected)
		    {
		        var no = new Option();
		    	no.value="2_"+left_vo.options[i].value;
		    	no.text="　　"+left_vo.options[i].text;
		    	right_vo.options[right_vo.options.length]=no;
		    	flag=true;
			}
		}
	}
	removeitem(sourcebox_id);
	return flag;	  	
}
function additems3(sourcebox_id,targetbox_id){
	var left_vo,right_vo,vos,i;
	vos= document.getElementsByName(sourcebox_id);
	if(vos==null)
		return false;
	left_vo=vos[0];
	vos= document.getElementsByName(targetbox_id);  
	if(vos==null)
		return false;
	right_vo=vos[0];
	var flag = false;
	for(i=0;i<left_vo.options.length;i++){
		if(left_vo.options[i].selected)
	    {
	        if(i!=left_vo.options.length-1){
	    		if(left_vo.options[i].value.substring(0,1)==1&&left_vo.options[i+1].value.substring(0,1)==2){
	    			alert("当前维度有下级维度，不允许删除！");
	    			return;
	    		}
	    	}
	        var no = new Option();
	    	no.value=left_vo.options[i].value.substring(left_vo.options[i].value.lastIndexOf("_")+1);// 截取正确的值
	    	no.text=left_vo.options[i].text.replace("　　", "");// 去除空格  // /(^\s*)|(\s*$)/g
	    	right_vo.options[right_vo.options.length]=no;
	    	flag=true;
		}
	}
	removeitem(sourcebox_id);
	return flag;	  	
}

function removeitem(sourcebox_id){
	var vos,right_vo,i;
	var isCorrect = false;
	vos= document.getElementsByName(sourcebox_id);
	if(vos==null)
  		return false;
	right_vo=vos[0];
	for(i=right_vo.options.length-1;i>=0;i--)
	{
		if(right_vo.options[i].selected)
	    {
	    	right_vo.options.remove(i);
	    	isCorrect = true;
	    }
	}
	if(!isCorrect)
	{
		alert("请选择需要操作的对象！");
	 	return false;
  	}
  	return true;	  	
}

function upitem(sourcebox_id){
    var vos,right_vo,i;
    var isCorrect = false;
    vos = document.getElementsByName(sourcebox_id);
    if(vos==null)
        return false;
    right_vo=vos[0];
    for(i=right_vo.options.length-1;i>=0;i--)
    {
        if(right_vo.options[i].selected)
        {
            if(right_vo.options[i].value.substring(0,1)==2){
                right_vo.options[i].value = "1" + right_vo.options[i].value.substring(1);
                right_vo.options[i].text = right_vo.options[i].text.replace("　　", "");
            }
            isCorrect = true;
        }
    }
    if(!isCorrect)
    {
        alert("请选择需要操作的对象！");
        return false;
    }
    return true;
}

function downitem(sourcebox_id){
    var vos,right_vo,i;
    var isCorrect = false;
    vos = document.getElementsByName(sourcebox_id);
    if(vos==null)
        return false;
    right_vo=vos[0];
    for(i=right_vo.options.length-1;i>=0;i--)
    {
        if(right_vo.options[i].selected)
        {
            // 第一项不能降为二级
            if(i!=0 && right_vo.options[i].value.substring(0,1)==1) {
                //right_vo.options[i].value = "2" + right_vo.options[i].value.substring(1);
                //right_vo.options[i].text = "　　" + right_vo.options[i].text;
                var text = "　　" + right_vo.options[i].text;
                var value = "2" + right_vo.options[i].value.substring(1);
                right_vo.options[i] =  new Option(text,value);
            }    
            isCorrect = true;
        }
    }
    if(!isCorrect)
    {
        alert("请选择需要操作的对象！");
        return false;
    }
    return true;
}

function query(value){
	var lvos,l_vo,i,cvos,c_vo,j,lengthways="",crosswise="",complex_id="",complex_ids;
	complex_ids = document.getElementsByName("complex_id");
	if(value=="1"){
		//complex_id = document.getElementById("complex_id").value;//$F('complex_id');
		complex_id = complex_ids[0].value;
	}else if(value=="2"){
		complex_id = complex_ids[1].value
	}
	lvos= document.getElementsByName("lengthways_dimension");
	if(lvos==null||lvos[0].options.length==0){
  		alert("请选择纵向维度后再点击确定！");
  		return;
	}else{
		l_vo=lvos[0];
		for(i=l_vo.options.length-1;i>=0;i--)
		{
	    	lengthways += l_vo.options[i].value+",";
		}
	}
	cvos= document.getElementsByName("crosswise_dimension");
	if(cvos==null||cvos[0].options.length==0){
  		alert("请选择横向维度后再点击确定！");
  		return;
	}else{
		c_vo=cvos[0];
		for(j=c_vo.options.length-1;j>=0;j--)
		{
	    	crosswise += c_vo.options[j].value+",";
		}
	}
	crossStaticForm.action="/general/deci/statics/crosstab.do?b_query=link&hideFlag=<%=hideFlag%>&type=${crossStaticForm.type}&hideFlag=1&infokind=1&lengthways="+lengthways+"&crosswise="+crosswise+"&complex_id="+complex_id;
	crossStaticForm.submit();
	showWait();
}

function saveInfo(){
	var lvos,l_vo,i,cvos,c_vo,j,lengthways="",crosswise="";
	lvos= document.getElementsByName("lengthways_dimension");
	if(lvos==null||lvos[0].options.length==0){
  		alert("请选择纵向维度后再点击保存！");
  		return;
	}else{
		l_vo=lvos[0];
		for(i=l_vo.options.length-1;i>=0;i--)
		{
	    	lengthways += l_vo.options[i].value+",";
		}
	}
	cvos= document.getElementsByName("crosswise_dimension");
	if(cvos==null||cvos[0].options.length==0){
  		alert("请选择横向维度后再点击保存！");
  		return;
	}else{
		c_vo=cvos[0];
		for(j=c_vo.options.length-1;j>=0;j--)
		{
	    	crosswise += c_vo.options[j].value+",";
		}
	}
	var userbases = document.getElementsByName("userbases")[0].value;
	var srcurl = "/general/deci/statics/savecrosstab.do?b_save=link`lengthways="+lengthways+"`crosswise="+crosswise+"`userbases="+userbases;
	srcurl = $URL.encode(srcurl); 
	var theurl="/general/static/commonstatic/iframe_query.jsp?src="+srcurl;
	var dw=430,dh=460,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
	if(getBrowseVersion()){//ie浏览器
  		window.showModalDialog(theurl,'_blank','dialogLeft:'+dl+'px;dialogTop:'+dt+'px;dialogHeight:460px;dialogWidth:430px;center:yes;scroll:no;help:no;resizable:no;status:no;');
	}else{//非IE浏览器 wangb 20180803 bug 39353
		window.open(theurl,'_blank','width='+(dw+40)+',height='+dh+',center=yes,scrollbars=no,resizable=no,left='+dl+'px,top='+dt+'px');
	}
}
function testchart(e){
	var name=e.name;
	//var tid = e.event.event.currentTarget.parentNode.id;//获取当前所点击的图表的id
	var tid = e.seriesName;
	var id = tid.substring(tid.length-1,tid.length)-1;
	if(name!=""){
		name=getEncodeStr(name);
		var commfacor = '${crossStaticForm.commfacor}';
		commfacor=getEncodeStr(commfacor);
		var commlexr='${crossStaticForm.commlexr}';
		commlexr = getEncodeStr(commlexr);
		var home=$URL.encode('${crossStaticForm.home}');
		crossStaticForm.action="/general/static/commonstatic/statshow.do?b_data=data&hideFlag=<%=hideFlag%>&statid="+stats[id]+"&showLegend="+$URL.encode(name)+"&showflag=1&flag=15&commlexr="+$URL.encode(commlexr)+"&commfacor="+$URL.encode(commfacor)+"&home="+home+"&type=cross&crosstabtype=${crossStaticForm.type}&dbname="+$URL.encode('${crossStaticForm.dbname}');
		crossStaticForm.submit();
	}
}
function showdata(v,h){
	var complex_id = document.getElementsByName("complex_id")[0].value;
	var commlexr=$URL.encode('${crossStaticForm.commlexr}');
	//commlexr = commlexr.split("+").join("%2B");
	var home='${crossStaticForm.home}';
	var querycond = $URL.encode('${crossStaticForm.querycond}');
	var commfacor = $URL.encode('${crossStaticForm.commfacor}');
	var param = "b_double=link&statid="+statid+"&hideFlag=<%=hideFlag%>&querycond="+querycond+"&v="+v+"&h="+h+"&flag=15&home="+home+"&type=cross&crosstabtype=${crossStaticForm.type}&lengthways=${crossStaticForm.lengthways}&crosswise=${crossStaticForm.crosswise}&commlexr="+commlexr+"&commfacor="+commfacor+"&vtotal=${crossStaticForm.vtotal}&htotal=${crossStaticForm.htotal}&vnull=${crossStaticForm.vnull}&hnull=${crossStaticForm.hnull}&dbname="+$URL.encode('${crossStaticForm.dbname}')+"&userbases="+$URL.encode('${crossStaticForm.dbname}')+"&org_filter=${crossStaticForm.org_filter}";
	var theurl="/general/static/commonstatic/statshow.do?"+param;
	crossStaticForm.action =theurl;
	crossStaticForm.submit();
}
function showOrClose()
{
	var obj=document.getElementById("chart");
    if(obj.style.display=='none')
    {
   		obj.style.display='block'
   	}
   	else
    {
    	obj.style.display='none';
   	}
}
function closeView(){
// 避免页面重新加载时，少图问题
    var obj=document.getElementById("chart");
    obj.style.display="none";
}
function vhtotal(flag,total){
	if(total==0){
		crossStaticForm.action="/general/deci/statics/crosstab.do?b_query=link&hideFlag=<%=hideFlag%>&type=${crossStaticForm.type}&"+flag+"total=1&filter_type=1&dbname="+$URL.encode('${crossStaticForm.dbname}')+"&userbases="+$URL.encode('${crossStaticForm.dbname}');
		crossStaticForm.submit();
		showWait();
	}else{
		crossStaticForm.action="/general/deci/statics/crosstab.do?b_query=link&hideFlag=<%=hideFlag%>&type=${crossStaticForm.type}&"+flag+"total=0&filter_type=1&dbname="+$URL.encode('${crossStaticForm.dbname}')+"&userbases="+$URL.encode('${crossStaticForm.dbname}');
		crossStaticForm.submit();
		showWait();
	}
}
function vhnull(flag,isnull){
	if(isnull==0){
		crossStaticForm.action="/general/deci/statics/crosstab.do?b_query=link&hideFlag=<%=hideFlag%>&type=${crossStaticForm.type}&"+flag+"null=1&filter_type=1&dbname="+$URL.encode('${crossStaticForm.dbname}')+"&userbases="+$URL.encode('${crossStaticForm.dbname}');
		crossStaticForm.submit();
		showWait();
	}else{
		crossStaticForm.action="/general/deci/statics/crosstab.do?b_query=link&hideFlag=<%=hideFlag%>&type=${crossStaticForm.type}&"+flag+"null=0&filter_type=1&dbname="+$URL.encode('${crossStaticForm.dbname}')+"&userbases="+$URL.encode('${crossStaticForm.dbname}');
		crossStaticForm.submit();
		showWait();
	}
}

function showWait(){
    var x=window.screen.width/2-300;
    var y=window.screen.height/2-200;
    var waitInfo;
    waitInfo=eval("wait");
    waitInfo.style.top=y;
    waitInfo.style.left=x;
    waitInfo.style.display="block";
    if(!getBrowseVersion() || getBrowseVersion() == 10){
    	var iframe = waitInfo.getElementsByTagName('iframe')[0];
    	iframe.style.display ='none';
    }
}

function addDict(obj,event,flag)
{ 
	var ff=document.getElementById('dict').style.display;
	if('block'==ff){
		document.getElementById('dict').style.display="none";
		return;
	}
	var evt = event ? event : (window.event ? window.event : null);
	var np = evt.keyCode; 
	if(np==38||np==40){ 
	
	} 
	var aTag;
	aTag = obj;   
	var un_vos=document.getElementsByName("dbname")[0];
	var userbases=document.getElementsByName("userbases")[0].value;
	if(!un_vos)
		return false;
	var unArrs=un_vos.options;
	var c=0;
	var rs =new Array();
	for(var i=0;i<unArrs.length;i++)
	{
		var un_str=unArrs[i];
		if(un_str)
		{	
			if(userbases.toUpperCase().indexOf(un_str.value.toUpperCase())!=-1){
				if(c%2==0)
					rs[c]="<tr id='tv' name='tv'><td id='al"+c+"' style='height:10px;width:50%;cursor:pointer' nowrap class=tdFontcolor><input name=backdatebox type=checkbox onclick='' value='"+un_str.value+","+un_str.text+"' checked=checked />"+un_str.text+"</td>"; 
				else
					rs[c]="<td id='al"+c+"' style='height:10px;width:50%;cursor:pointer' nowrap class=tdFontcolor><input name=backdatebox type=checkbox onclick='' value='"+un_str.value+","+un_str.text+"' checked=checked />"+un_str.text+"</td></tr>"; 
			}else{
				if(c%2==0)
					rs[c]="<tr id='tv' name='tv'><td id='al"+c+"' style='height:10px;cursor:pointer' nowrap class=tdFontcolor><input name=backdatebox type=checkbox onclick='' value='"+un_str.value+","+un_str.text+"' />"+un_str.text+"</td>"; 
				else
					rs[c]="<td id='al"+c+"' style='height:10px;width:50%;cursor:pointer' nowrap class=tdFontcolor><input name=backdatebox type=checkbox onclick='' value='"+un_str.value+","+un_str.text+"' />"+un_str.text+"</td></tr>"; 
			}
			c++;
		}
	}
	if(c%2!=0){
		rs[c]="<td id='al"+c+"'  onclick=\"\"  style='height:10px;cursor:pointer' nowrap class=tdFontcolor></td></tr>"; 
		c++;
	}
    resultuser=rs.join("");											//【8177】员工管理-统计分析-多维统计-人员范围（页面滚动条那显示一半） jingq upd 2015.03.23
    resultuser="<div style='border-width: 1px;BORDER-bottom: #aeac9f 1pt solid;height:80px;width:238px;overflow:auto;margin:5 9 9 0'><table width='100%' cellpadding='0' border='0' bgcolor='#FFFFFF' cellspacing='0'>"+resultuser+"</table></div>"; 
    resultuser+="<table style='margin:9 9 9 9' width='238px' cellpadding='0' border='0' bgcolor='#FFFFFF' cellspacing='0'><tr id='tv' name='tv'><td id='al"+c+"' style='width:85%;height:10px;cursor:pointer' nowrap class=tdFontcolor><input name=allbox type=checkbox onclick='selectallcheckbox(this)' value='' />全部</td></tr>";
    resultuser+="<tr><td align='center' style='height:35px'><input onclick=\"selectcheckbox();document.getElementById('dict').style.display='none'\" value='确定' type='button' class='mybutton'/>&nbsp;&nbsp;<input onclick=\"document.getElementById('dict').style.display='none'\" value='取消' type='button' class='mybutton'/></td></tr></table>";
    document.getElementById("dict").innerHTML=resultuser;
    document.getElementById('dict').style.display = "block";
    var pos=getAbsPosition(aTag);
    document.getElementById('dict').style.position="absolute";	
	document.getElementById('dict').style.left=pos[0]-$('viewuserbases').offsetWidth+'px';
    document.getElementById('dict').style.top=pos[1]+aTag.offsetHeight+'px';
    if(navigator.appName.indexOf("Microsoft")!= -1){
	    var objdiv=document.getElementById("dict");
	    var w = objdiv.offsetWidth;
		var h = objdiv.offsetHeight;
		var ifrm = document.createElement('iframe');
		ifrm.src = 'javascript:false';
		ifrm.style.cssText = 'display:none;position:absolute; visibility:inherit; top:0px; left:0px; width:' + w + 'px; height:' + h + 'px; z-index:-1; filter: \'progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)\'';
		objdiv.appendChild(ifrm);
	}
}
function selectallcheckbox(o){
	var backdatebox=document.getElementsByName("backdatebox");
	for(var i=0;i<backdatebox.length;i++){
		var obj=backdatebox[i];
		obj.checked=o.checked;
	}
}
function selectcheckbox(){
	var backdatebox=document.getElementsByName("backdatebox");
	var userbases=document.getElementsByName("userbases")[0];
	userbases.value="";
	var veiwuserbases=document.getElementsByName("viewuserbases")[0];
	veiwuserbases.value="";
	for(var i=0;i<backdatebox.length;i++){
		var obj=backdatebox[i];
		if(obj.checked){
			var tmp=obj.value.split(",");
			var viewuserbasesv=tmp[1];
			var userbasesv=tmp[0];
			if(userbases.value.length>0){
				userbases.value=userbases.value+","+userbasesv;
				veiwuserbases.value=veiwuserbases.value+";"+viewuserbasesv;
			}else{
				userbases.value=userbasesv;
				veiwuserbases.value=viewuserbasesv;
			}
		}
	}
}
function display(){
	var obj	= document.getElementById("dimension");
	if(obj.style.display&&obj.style.display=='none')
		obj.style.display="block";
	else
		obj.style.display="none";
}
function returnhome(tar){
	if(tar=="hl4"){
		crossStaticForm.action="/system/home.do?b_query=link";
		crossStaticForm.target="il_body";
		crossStaticForm.submit();
	}else if(tar=="hl"){
		crossStaticForm.action="/templates/index/portal.do?b_query=link";
		crossStaticForm.target="il_body";
		crossStaticForm.submit();
	}else if(tar=="hcm"){
		crossStaticForm.action="/templates/index/hcm_portal.do?b_query=link";
		crossStaticForm.target="il_body";
		crossStaticForm.submit();
	}else{
		crossStaticForm.action="/system/home.do?b_query=link";
		crossStaticForm.target="i_body";
		crossStaticForm.submit();
	}
}
/*
 *创建机构筛选组件  wangb  20190819
 */
function createOrgSelector(){
	if('${crossStaticForm.org_filter}'=='1'){
		var value = '${crossStaticForm.filterId}'+'`'+'${crossStaticForm.filterName}';
		Ext.onReady(function(){
			Ext.widget("codecomboxfield",{
				border: false,
				onlySelectCodeset: false,
				codesetid: "UM",
				ctrltype: "1",
				editable: false,
				value:value,
				listeners: {
					afterrender: function () {
						// this.setValue("",true); //初始化赋值
					},
					select: function (a, b) {
						var dw=300,dh=150;
						var x=(document.body.clientWidth-dw)/2;;
	    				var y=(document.body.clientHeight-dh)/2; 
						var waitInfo=eval("wait");
						waitInfo.style.top=y;
						waitInfo.style.left=x;
						waitInfo.style.display="block";
						if(window.location.href.indexOf('filterId') !=-1){
							window.location.href = window.location.href.substring(0,window.location.href.indexOf('filterId'))+'filterId='+a.value;
						}else{
							window.location.href = window.location.href+'&filter_type=1&filterId='+a.value;
						}
					}
				},
				renderTo:document.getElementById("org")
			}).show();
		});
	}
}
createOrgSelector();
</script>
<hrms:themes />
<style>
.notop {
	border-top: none;
}

.noleft {
	border-left: none;
}

.noright {
	border-right: none;
}

.nobottom {
	border-bottom: none;
}
</style>
<body>
<html:form action="/general/deci/statics/crosstab">
	<%if(!"hcm".equals(bosflag)) {%>
	<table width="100%" border="0" cellspacing="0" align="center"
		cellpadding="0"  style="margin-top: 10px">
	<%}else{ %><!--bug36527 update by xiegh ie非兼容模式会多出一条滚动条  -->
	<table width="auto" border="0" cellspacing="0" align="left"
		cellpadding="0"><!--update by xiegh 当统计条件过多时，界面的宽度不固定  所以将自定义区域固定靠左显示  -->
	<%} %>
		<tr>
			<td>
			<% if(!"1".equals(hideFlag)){ %><%--bug 49100 处理闪一下  wangb 20190617 --%>
				<table width="100%" align="left" border="0" cellpadding="0"
					cellspacing="0" class="ListTable" style="margin-left: 6px;">
					<tr>
						<td>
							<table width="910px"  border="0" cellspacing="0" align="left"
								cellpadding="0" class="RecordRow" id=dimension style="margin-left: 20px;display:block;height:180px;">
								<tr>
									<td width="25%" align="left">
										<bean:message key="static.lengthways.dimension" />
									</td>
									<td width="5%" align="center"></td>
									<td width="25%" align="left">
										<bean:message key="static.dimension.optional" />
									</td>
									<td width="5%" align="center"></td>
									<td width="25%" align="left">
										<bean:message key="static.crosswise.dimension" />
									</td>
									<td width="37%" align="left" id="showCondTitle" style="display:block;"><!--非IE浏览器中改td宽度不够，文字向下排列；故增加他的宽度 多个浏览器已测正常显示  -->
										<bean:message key="targetsortlist.menscope" />
									</td>
								</tr>
								<tr>
									<td width="25%" align="center">
										
										<hrms:optioncollection name="crossStaticForm" property="lengthways_dimension_list" collection="lengthways_selectlist" />
										<html:select name="crossStaticForm" styleId="lengthways_dimension" property="lengthways_dimension" multiple="multiple" size="10" style="height:150px;width:100%;font-size:9pt">
											<html:options collection="lengthways_selectlist" property="dataValue" labelProperty="dataName" />
										</html:select>
									</td>
									<td width="5%" align="center" >
                                        <table>
                                            <tr>
                                                <td align="center">
                                                   <%--  <bean:message key="static.dimension.select" /> --%>
			             							<img src="/images/left_arrow2.jpg" align="middle" alt="" onclick="additems1('select_dimension','lengthways_dimension');" style=" cursor: pointer"/>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td height="0px"></td>
                                            </tr>
                                        </table>
									</td>
									<td width="25%" align="center">
										<hrms:optioncollection name="crossStaticForm"
											property="select_dimension_list" collection="selectlist" />
										<html:select name="crossStaticForm"
											property="select_dimension" multiple="multiple" size="10"
											style="height:150px;width:100%;font-size:9pt">
											<html:options collection="selectlist" property="dataValue"
												labelProperty="dataName" />
										</html:select>
									</td>
									<td width="5%" align="center">
										<table>
											<tr>
												<td align="center">
                                                    <%-- <bean:message key="static.dimension.select" /> --%>
                                                    <img src="/images/right_arrow2.jpg" align="middle" alt="" onclick="additems1('select_dimension','crosswise_dimension');" style=" cursor: pointer"/>
												</td>
											</tr>
											<tr>
												<td height="0px"></td>
											</tr>
										</table>
									</td>
									<td width="25%" align="center"><!--add by xiegh Ext.get(crosswise_dimension) 在非IE浏览器中位空，故为下面的select 增加styleID属性  -->
										<hrms:optioncollection name="crossStaticForm" property="crosswise_dimension_list" collection="crosswise_selectlist" />
										<html:select name="crossStaticForm" styleId="crosswise_dimension" property="crosswise_dimension" multiple="multiple" size="10" style="height:150px;width:100%;font-size:9pt">
											<html:options collection="crosswise_selectlist" property="dataValue" labelProperty="dataName" />
										</html:select>
									</td>
									<td width="15%" align="left" style="padding-left:5px;"><!--add by xiegh bug36007  -->
										<table height="100%" border="0" cellspacing="0" cellpadding="0">
											<tr style="display: none">
												<td  align="right" nowrap class="tdFontcolor"><bean:message key="static.stor"/></td>
				      								<td align="left" nowrap class="tdFontcolor">       
				      								<html:select name="crossStaticForm" property="dbname" size="1" >
	                  									<html:optionsCollection property="dblist" value="dataValue" label="dataName"/> 
	                								</html:select> 
	             								</td>
	            							</tr>
	            							<tr id="showdbname" style="display: block;">
		     	    							<td align="center" valign="top" height="33">
									       			<input name=viewuserbases style="width: 130px;height:21px;vertical-align: middle;font-family:微软雅黑;font-size: 12px" value='${crossStaticForm.viewuserbases }' readonly="readonly"><img  id=imgid style="height:23px;cursor:pointer; vertical-align: middle;" src="/images/select.jpg" onmouseover="this.src='/images/selected.jpg'" onmouseout="this.src='/images/select.jpg'" onclick="addDict(this,event,'hidcategories');">
							       					<input name=userbases type="hidden" value='${crossStaticForm.userbases }' />
									       		</td>
											</tr>
											<tr id="showCond" style="display: block;">
												<td align="center" valign="top">
													<html:select name="crossStaticForm" property="complex_id" size="1" style="width: 150px;">
														<html:optionsCollection property="condlist" value="dataValue" label="dataName" />
													</html:select>
												</td>
											</tr>
											<tr id="topbutton" style="display: none">
												<td align="center" valign="top">
													<input type="button" name="btnok"
														value='<bean:message key="button.ok" />'
														onclick="query('1');" class="mybutton"><br><br>
													<hrms:priv func_id="2602302">	
														<input type="button" name="btnsave"
															value='<bean:message key="button.save" />'
															onclick="saveInfo();" class="mybutton">
													</hrms:priv>
												</td>
											</tr>
											<tr id="botbutton" style="display: block;margin-top:38px;">
												<td align="center">
													<input type="button" name="btnok" style="margin-left:24px;"
														value='<bean:message key="button.ok" />'
														onclick="query('1');" class="mybutton">
													<input type="button" name="btnsave"
														value='<bean:message key="button.save" />'
														onclick="saveInfo();" class="mybutton">
												</td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td height="5px" colspan="6"></td>
								</tr>
							</table>
							<%}%>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<%if(crossStaticForm.getCondlist().size()>0){ %>
		<tr id=cond style="display:none">
			<td>
				<table height="100%" border="0" cellspacing="0" cellpadding="0" style="margin-left: 56px;">
					<tr>
						<td align="center" valign="top">
							<html:select name="crossStaticForm" property="complex_id"
								size="1" onchange="javascript:query('2')">
								<html:optionsCollection property="condlist"
									value="dataValue" label="dataName" />
							</html:select>
						</td>
						<td>
							<logic:equal  name="crossStaticForm"  property="home"  value="1" >
          						&nbsp;&nbsp;&nbsp;&nbsp;<input type="button" name="b_return" value="<bean:message key="button.return"/>" class="mybutton" onclick="returnhome('<%=bosflag%>')">
      						</logic:equal>
						</td>
					</tr>
					<tr>
						<td height="5px"></td>
					</tr>
				</table>
			</td>
		</tr>
		<% if(!"1".equals(hideFlag)){ %><%--bug 49100 处理闪一下  wangb 20190617 --%>
		<tr  id = "hideEleId">
			<td style="padding-bottom:0px;height:40px;">
				<a  href="javascript:void(0);" style="margin-left:20px;" onclick="display()">&nbsp;&nbsp;&nbsp;  [显示/隐藏]</a>
			</td>
		</tr>
		<% } %>
		<%} %>
		<logic:equal name="crossStaticForm" property="org_filter" value="1">
			<tr>
				<td>
					<div id="org" style="margin-left:10px;margin-bottom:10px;"></div>
				</td>
			</tr>
		</logic:equal> 
		<tr>
			<td>
				<%if("1".equals(crossStaticForm.getShowChart())){ %> 
				<table align="left" id="chart" style="display:block">
					<%
						ArrayList decimalwidthlist = crossStaticForm.getDecimalwidthlist();
						ArrayList isneedsumlist = crossStaticForm.getIsneedsumlist();
						ArrayList listlist = crossStaticForm.getListlist();
						ArrayList label_enabledlist = crossStaticForm.getLabel_enabledlist();
						ArrayList xanglelist = crossStaticForm.getXanglelist();
						ArrayList snamedisplaylist = crossStaticForm.getSnamedisplaylist();
						int i = 0;
						if (decimalwidthlist != null) {
							int colnum=2;
							/*update by xiegh on20180609 陈总的意思要以两列向下排序 */
							/* if(decimalwidthlist.size()>4){
								colnum=3;								
							} */
							while (i < decimalwidthlist.size()) {
					%>
					<tr>
							<%
								for (int j = 0; j < colnum; j++) {
									if (i < decimalwidthlist.size()) {
										String listid = "list" + i;
										crossStaticForm.setIsneedsum((String) ((CommonData) isneedsumlist.get(i)).getDataValue());
										crossStaticForm.setXangle((String) ((CommonData) xanglelist.get(i)).getDataValue());
										crossStaticForm.setSnamedisplay((String) ((CommonData) snamedisplaylist.get(i)).getDataValue());
										crossStaticForm.setList((ArrayList) ((HashMap) listlist.get(i)).get(listid));
										crossStaticForm.setLabel_enabled((String) ((CommonData) label_enabledlist.get(i)).getDataValue());
										crossStaticForm.setDecimalwidth((String) ((CommonData) decimalwidthlist.get(i)).getDataValue());
							%>
						<td width="550" align="center" nowrap id="">
							<div id="cross_<%=i%>" style="">
								<table>
									<tr>
										<td id='<%="multi_pie" + i%>'>
											<hrms:chart name="crossStaticForm" chartpnl='<%="multi_pie" + i%>'  
												isneedsum="${crossStaticForm.isneedsum }"
												islabelname="false"
												xangle="${crossStaticForm.xangle }"
												title="${crossStaticForm.snamedisplay}" scope="session"
												legends="list" data="" width="550" height="400"
												chart_type="20" pointClick="testchart"
												label_enabled="${crossStaticForm.label_enabled }"
												numDecimals="${crossStaticForm.decimalwidth}" pieoutin="true">
											</hrms:chart>
										</td>
									</tr>
								</table>
							</div>
						</td>
							<%
										i++;
									}
								}
							%>
					</tr>
					<%
							}
						}
					%>
				</table>
				<%} %>
			</td>
		</tr>
		<tr>
			<td>
				<logic:notEmpty name="crossStaticForm" property="varraysecondlist">
					<logic:notEmpty name="crossStaticForm" property="harraysecondlist">
							<table border="1" cellspacing="0" align="left" cellpadding="0" class="ListTable0" style="margin-left: 6px;margin-right:10px">
								<thead>
									<tr>
										<td align="center" colspan="2" class="TableRow notop noleft nobottom" nowrap>
											&nbsp;
										</td>
										<logic:iterate id="element" name="crossStaticForm"
											property="varrayfirstlist" indexId="index">
											<logic:equal value="合计" name="element" property="legend">
												<td align="center" width="90px" class="TableRow notop noleft" rowspan="2" colspan="<bean:write name="element" property="size" />" nowrap>
													<bean:write  name="element" property="legend" />
												</td>
											</logic:equal>
											<logic:notEqual value="合计" name="element" property="legend">
												<td align="center" class="TableRow notop noleft" colspan="<bean:write name="element" property="size" />" nowrap>
													<bean:write name="element" property="legend" />
												</td>
											</logic:notEqual>
										</logic:iterate>
									</tr>
									<tr>
										<td align="center" colspan="2" class="TableRow notop noleft" nowrap>
											&nbsp;
										</td>
										<logic:iterate id="element" name="crossStaticForm"
											property="varraysecondlist" indexId="index">
											<logic:equal value="合计" name="element" property="legend">
											</logic:equal>
											<logic:notEqual value="合计" name="element" property="legend">
												<td align="center" width="90px" class="TableRow notop noleft" nowrap>
													<bean:write name="element" property="legend" />
												</td>
											</logic:notEqual>
										</logic:iterate>
									</tr>
								</thead>
								<%
									int m = 1;
									int n = 0;
								%>
								<logic:empty name="crossStaticForm" property="sformula">
									<logic:iterate id="element" name="crossStaticForm"
										property="harraysecondlist" indexId="indexh">
										<tr>
											<%
												int size = Integer.parseInt((String) ((LazyDynaBean) crossStaticForm.getHarrayfirstlist().get(n)).get("size"));
												if ((size > m&&size!=1)||(size==1)) {
													if (m == 1) {
											%>
											<td align="center" <logic:equal value="合计" name="element" property="legend"> colspan="2" class="TableRow noleft notop" </logic:equal>
											<logic:notEqual value="合计" name="element" property="legend"> class="TableRow noleft notop noright" </logic:notEqual> 
												rowspan="<%=size%>" nowrap>
												<%=(String) ((LazyDynaBean) crossStaticForm.getHarrayfirstlist().get(n)).get("legend")%>
											</td>
											<%
													}
													m++;
													if(size==1){
														n++;
														m = 1;
													}
												} else {
													n++;
													m = 1;
												}
											%>
											<logic:equal value="合计" name="element" property="legend">
											</logic:equal>
											<logic:notEqual value="合计" name="element" property="legend">
												<td align="center" class="TableRow notop" nowrap>
													<bean:write name="element" property="legend" />
												</td>
											</logic:notEqual>
											<logic:iterate id="helement" name="crossStaticForm" property="varraysecondlist" indexId="indexv">
											    <bean:define id="cellvalue" value="${crossStaticForm.statdoublevalues[indexv][indexh]}"/>
											    <logic:equal name="cellvalue" value="0">
											        <td align="center" class="RecordRow noleft" nowrap>
                                                        0
                                                    </td>
                                                </logic:equal>
                                                <logic:notEqual name="cellvalue" value="0">
													<td align="center" class="RecordRow noleft" nowrap>
														<a href="###" onclick="showdata('${indexv}','${indexh}');">${crossStaticForm.statdoublevalues[indexv][indexh]}</a>
													</td>
												</logic:notEqual>
											</logic:iterate>
										</tr>
									</logic:iterate>
								</logic:empty>
								<logic:notEmpty name="crossStaticForm" property="sformula">
									<logic:iterate id="element" name="crossStaticForm"
										property="harraysecondlist" indexId="indexh">
										<tr>
											<%
												int size = Integer.parseInt((String) ((LazyDynaBean) crossStaticForm.getHarrayfirstlist().get(n)).get("size"));
												if ((size > m&&size!=1)||(size==1)) {
													if (m == 1) {
											%>
											<td align="center" <logic:equal value="合计" name="element" property="legend"> colspan="2" class="TableRow noleft notop" </logic:equal><logic:notEqual value="合计" name="element" property="legend"> class="TableRow noleft notop noright" </logic:notEqual> 
												rowspan="<%=size%>" nowrap>
												<%=(String) ((LazyDynaBean) crossStaticForm.getHarrayfirstlist().get(n)).get("legend")%>
											</td>
											<%
													}
													m++;
													if(size==1){
														n++;
														m = 1;
													}
												} else {
													n++;
													m = 1;
												}
											%>
											<logic:equal value="合计" name="element" property="legend">
											</logic:equal>
											<logic:notEqual value="合计" name="element" property="legend">
												<td align="center" class="TableRow notop" nowrap>
													<bean:write name="element" property="legend" />
												</td>
											</logic:notEqual>
											<logic:iterate id="helement" name="crossStaticForm" property="varraysecondlist" indexId="indexv">
                                                <bean:define id="cellvalue" value="${crossStaticForm.statdoublevalues[indexv][indexh]}"/>
                                                <logic:equal name="cellvalue" value="0">
                                                    <td align="center" class="RecordRow noleft" nowrap>
                                                        0
                                                    </td>
                                                </logic:equal>
                                                <logic:notEqual name="cellvalue" value="0">
													<td align="center" class="RecordRow noleft" nowrap>
														<a href="###" onclick="showdata('${indexv}','${indexh}');"><hrms:formatDecimals
																value="${crossStaticForm.statdoublevaluess[indexv][indexh]}"
																length="${crossStaticForm.decimalwidth}"></hrms:formatDecimals>
														</a>
													</td>
												</logic:notEqual>
											</logic:iterate>
										</tr>
									</logic:iterate>
								</logic:notEmpty>
							</table>
					</logic:notEmpty>
				</logic:notEmpty>
			</td>
		</tr>
		<tr>
			<td id="statis" style="display:block">
				<logic:notEmpty name="crossStaticForm" property="varraysecondlist">
					<logic:notEmpty name="crossStaticForm" property="harraysecondlist">
						<table border="0" cellspacing="0" align="left" cellpadding="0">
							<tr>
								<td nowrap>
								<%if(totalvalues>0){//统计结果都为空则不显示隐藏空行，隐藏空列 %>
									<logic:equal value="0" name="crossStaticForm" property="hnull">
										<input type="checkbox" onclick="vhnull('h',${crossStaticForm.hnull });" />
										<bean:message key="static.hide.null.row" />
									</logic:equal>
									<logic:equal value="1" name="crossStaticForm" property="hnull">
										<input type="checkbox" checked="checked" onclick="vhnull('h',${crossStaticForm.hnull });" />
										<bean:message key="static.hide.null.row" />
									</logic:equal>
									<logic:equal value="0" name="crossStaticForm" property="vnull">
										<input type="checkbox" onclick="vhnull('v',${crossStaticForm.vnull });" />
										<bean:message key="static.hide.null.column" />
									</logic:equal>
									<logic:equal value="1" name="crossStaticForm" property="vnull">
										<input type="checkbox" checked="checked" onclick="vhnull('v',${crossStaticForm.vnull });" />
										<bean:message key="static.hide.null.column" />
									</logic:equal>
								<%} %>
									<logic:equal value="0" name="crossStaticForm" property="vtotal">
										<input type="checkbox" onclick="vhtotal('v',${crossStaticForm.vtotal });" />
										<bean:message key="static.crosswise.total" />
									</logic:equal>
									<logic:equal value="1" name="crossStaticForm" property="vtotal">
										<input type="checkbox" checked="checked" onclick="vhtotal('v',${crossStaticForm.vtotal });" />
										<bean:message key="static.crosswise.total" />
									</logic:equal>
									<logic:equal value="0" name="crossStaticForm" property="htotal">
										<input type="checkbox" onclick="vhtotal('h',${crossStaticForm.htotal });" />
										<bean:message key="static.lengthways.total" />
									</logic:equal>
									<logic:equal value="1" name="crossStaticForm" property="htotal">
										<input type="checkbox" checked="checked" onclick="vhtotal('h',${crossStaticForm.htotal });" />
										<bean:message key="static.lengthways.total" />
									</logic:equal>
								</td>
								<%--
								<td nowrap>
									<table border="0" cellpmoding="0" cellspacing="0" class="DetailTable" cellpadding="0" style="margin-left: 20px;">
										<tr>
											<td nowrap>
												<a href="###" onclick="javascript:showOrClose();"><bean:message key="static.show.or.hide.chart" /></a>
											</td>
										</tr>
									</table>
								</td>
								 --%>
							</tr>
						</table>
					</logic:notEmpty>
				</logic:notEmpty>
			</td>
		</tr>
	</table>
	<%if("1".equals(crossStaticForm.getShowChart())){ %>
	<table>
		<tr id="showChart" style="display:none">
			<td>
				<table border="0" cellpmoding="0" cellspacing="0" class="DetailTable" cellpadding="0">
					<tr>
						<td nowrap>
							<a href="###" onclick="javascript:showOrClose();"><bean:message key="static.show.or.hide.chart" /></a>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	<%} %>
	<table id="breturn" height="100%" border="0" cellspacing="0" cellpadding="0" style="margin-left:1px;margin-top:5px;display:none">
		<tr>
			<td  align="center" valign="top">
				<logic:equal  name="crossStaticForm"  property="home"  value="1" >
          			<input type="button" name="b_return" value="<bean:message key="button.return"/>" class="mybutton" onclick="returnhome('<%=bosflag%>')">
      			</logic:equal>
			</td>
		</tr>
	</table>
    <div id="wait" style='position:absolute;top:285;left:120;display:none;width:500px;heigth:250px;z-index:99999999999;'>
         <table border="1" width="50%" cellspacing="0" cellpadding="4" class="table_style" height="100" align="center">
            <tr>
                <td class="td_style" height="24" id="hlw">
                    <bean:message key="static.dimension.wait"/>
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
        <iframe src="javascript:false" frameBorder="0" style="position:absolute;display:none; visibility:inherit; top:0px; left:0px;width:285px;height:120px;z-index:-1;filter='progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)';">
        </iframe> 
    </div>
</html:form>
</body>
<script type="text/javascript">
//定义右键菜单
var lengthways_pop = new Ext.menu.Menu({
   id :'lengthways_pop',
   items : [{
       id:'lengthways_mnu1',
       text : '升级',
       handler:function (){
                 upitem('lengthways_dimension');
               }
   }, {
       id:'lengthways_mnu2',
       text : '降级',
       handler:function (){
                 downitem('lengthways_dimension');
               }
   }, {
       id:'lengthways_mnu3',
       text : '删除',
       handler:function (){
                 additems3('lengthways_dimension','select_dimension');
               }
   }]
});
var crosswise_pop = new Ext.menu.Menu({
   id :'crosswise_pop',
   items : [{
       id:'crosswise_mnu1',
       text : '升级',
       handler:function (){
                 upitem('crosswise_dimension');
               }
   }, {
       id:'crosswise_mnu2',
       text : '降级',
       handler:function (){
                 downitem('crosswise_dimension');
               }
   }, {
       id:'crosswise_mnu3',
       text : '删除',
       handler:function (){
                 additems3('crosswise_dimension','select_dimension');
               }
   }]
});

// 增加右键点击事件
if(Ext.get('lengthways_dimension')){
	Ext.get('lengthways_dimension').on('contextmenu',function(event){
    	event.preventDefault();
    	lengthways_pop.showAt(event.getXY());
	});
	
	
	Ext.get('lengthways_dimension').on('dblclick',function(){
		additems3('lengthways_dimension','select_dimension');
	})

	
}
if(Ext.get('crosswise_dimension')){
	Ext.get('crosswise_dimension').on('contextmenu',function(event){
    	event.preventDefault();
    	crosswise_pop.showAt(event.getXY());
	});
	Ext.get('crosswise_dimension').on('dblclick',function(){
		additems3('crosswise_dimension','select_dimension');
	})
}
var statisobj=document.getElementById("statis");//控制隐藏空行，空列；横、纵向合计；隐藏/显示统计图
var dimensionobj=document.getElementById("dimension");//控制配置多维统计
var condobj=document.getElementById("cond");//控制有树情况下的人员范围
var breturnobj=document.getElementById("breturn");//控制在首页点多维进去没有人员范围显示返回
var showChartobj=document.getElementById("showChart");//控制单独写的是否隐藏/显示统计图（有树时显示）
var type='${crossStaticForm.type}';//得到类型 =0，树  =1，人员结构 =2，离职人员结构
var statidFlag='${crossStaticForm.statidFlag}';//是否配置了statid
var showDbname='${crossStaticForm.showdbname}';
var showDbnameObj=document.getElementById("showdbname");
if(showDbnameObj){
	if(showDbname=="0"){
		showDbnameObj.style.display='none';
	}else{
		showDbnameObj.style.display='block';
	}
}
if(condobj){
	if(breturnobj)
   		breturnobj.style.display='none';
}else{
	if(breturnobj)
   		breturnobj.style.display='block';
}
if(type=="0"){
	if(statisobj)
		statisobj.style.display='none';
	if(dimensionobj)
   		dimensionobj.style.display='none';
   	if(condobj)
   		condobj.style.display='block';
   	if(showChartobj)
   		showChartobj.style.display='block';
}else{
	if(statidFlag=="false"){
		if(statisobj)
			statisobj.style.display='block';
		if(dimensionobj)		
	   		dimensionobj.style.display='block';
	   	if(condobj)		
	   		condobj.style.display='none';
	   	if(showChartobj)
	   		showChartobj.style.display='none';
	}
}

var showCond='${crossStaticForm.showcond}';
var showCondObj=document.getElementById("showCond");//控制在配置多维统计时是否显示人员范围下拉列表框
var showCondTitleObj=document.getElementById("showCondTitle");//控制在配置多维统计时是否显示人员范围表头
var topButtonObj=document.getElementById("topbutton");//控制在配置多维统计时上面的"确定""保存"按钮
var botButtonObj=document.getElementById("botbutton");//控制在配置多维统计时下面的"确定""保存"按钮
if(showCond=="0"){
	if(showCondObj)
		showCondObj.style.display='none';
	if(showCondTitleObj)
		showCondTitleObj.style.display='none';
	if(botButtonObj)
		botButtonObj.style.display='none';
	if(topButtonObj)
		topButtonObj.style.display='block';
}else{
	if(showCondObj)
		showCondObj.style.display='block';
	if(showCondTitleObj)
		showCondTitleObj.style.display='block';
	if(botButtonObj)
		botButtonObj.style.display='block';
	if(topButtonObj)
		topButtonObj.style.display='none';
}
<%--
if('<%=hideFlag%>' == 1){//隐藏自定义区域的标识参数hideFlag
	var dimensionObj = document.getElementById("dimension");
	if(dimensionObj)
		dimensionObj.style.display = "none";
	
	var hideEleObj = document.getElementById("hideEleId");
	if(hideEleObj)
		hideEleObj.style.display = "none";
}--%>
if(getBrowseVersion()){
	var viewuserbases = document.getElementsByName('viewuserbases')[0];
	viewuserbases.style.width="127px";
	viewuserbases.style.height="15px";
	if(getBrowseVersion()!=10){
		var dimension = document.getElementById('dimension');
		dimension.getElementsByTagName('tr')[0].getElementsByTagName('td')[0].style.paddingLeft='5px';
		dimension.getElementsByTagName('tr')[1].getElementsByTagName('td')[0].style.paddingLeft='5px';
		var tds= dimension.getElementsByTagName('tr')[1].getElementsByTagName('td');
		dimension.getElementsByTagName('tr')[1].children[5].style.paddingRight='5px';
	}
}
</script>
<div id="dict" class='div_table'  style="display:none;z-index:+999;position:absolute;width:240px;height:160px;overflow:hidden;background-color:#FFF;z-index:9999999999;"></div>