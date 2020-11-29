<%@page import="com.hjsj.hrms.actionform.sys.options.otherparam.SysOthParamForm"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+(80==(request.getServerPort())?"":(":"+request.getServerPort()))+path+"/";

%>
<script language="javascript" src="/js/dict.js"></script>
<script type="text/javascript"
	src="/train/resource/course/courseTrain.js"></script>
<script type="text/javascript" src="/train/resource/course/gmsearch.js"></script>
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
<style>
.tbl-container
{  
	overflow:auto; 
	height:expression(document.body.clientHeight-150);
	width:expression(document.body.clientWidth-10); 
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
}
.t_cell_locked 
{
	border: inset 1px #C4D8EE;
	BACKGROUND-COLOR: #ffffff;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: 0pt; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: 0pt;
	font-size: 12px;
	border-collapse:collapse; 
	
	background-position : center left;
	left: expression(document.getElementById("tbl-container").scrollLeft); /*IE5+ only*/
	position: relative;
	z-index: 10;
	
}
.t_cell_locked_b {
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: 0pt; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: 0pt;
	font-size: 12px;
}

.t_header_locked
{
	/*background-image:url(/images/listtableheader_deep-8.jpg);*/
	background-repeat:repeat;
	background-position : center left;
	background-color:#f4f7f7;
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: 0pt; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: 0pt;
	valign:middle;
	font-weight: bold;	
	text-align:center;
	top: expression(document.getElementById("tbl-container").scrollTop); /*IE5+ only*/
	position: relative;
	z-index: 15;
}
	 		
.t_cell_locked2 
{
	/*  background-image:url(/images/listtableheader_deep-8.jpg);*/
	background-repeat:repeat;
	background-position : center left;
	background-color:#f4f7f7;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: 0pt; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: 0pt;
	font-weight: bold;	
	valign:middle;
	left: expression(document.getElementById("tbl-container").scrollLeft); /*IE5+ only*/
	top: expression(document.getElementById("tbl-container").scrollTop); /*IE5+ only*/
	position: relative;
	z-index: 20;
	
}
</style>
<hrms:themes></hrms:themes>
<script type="text/javascript">


function getR5004(r5004){
	var hashvo=new ParameterSet();
	hashvo.setValue("codeitemid",r5004);
	var request=new Request({method:'post',asynchronous:false,onSuccess:showFieldList,functionId:'2020030117'},hashvo);
	function showFieldList(outparamters){
		document.write(outparamters.getValue("codeitemdesc"));
	}
}
function edite(id,a_code){
	//var a_code = $F("a_code");
	courseForm.action="/train/resource/course.do?b_add=link&a_code="+a_code+"&id="+id;
	courseForm.submit();
}

function courseware(id,code){
	courseForm.action="/train/resource/courseware.do?b_query=link&id="+id+"&a_code="+code;
	courseForm.submit();
}
function showfile(outparamters){
	if(outparamters!=null){
		var a_code = document.getElementsByName("a_code")[0].value;
		courseForm.action = "/train/resource/course.do?b_query=link&a_code=" + a_code;
		courseForm.submit();
	}
}
function del(){
	var sel="";
	var sels=document.getElementsByName("r5000");
	for(var i=0;i<sels.length;i++){
		if(sels[i].checked)
			sel+=sels[i].value+",";
	}
	if(sel!=""&&sel.length>0){
		if(checkIsParent(sel)){
			alert("只能删除管理范围内的课程！");
			return false;
		}
		var hashvo=new ParameterSet();
		hashvo.setValue("deletestr",sel);
		hashvo.setValue("type","course");
		var request=new Request({method:'post',asynchronous:false,onSuccess:delteacher,functionId:'202003000101'},hashvo);

		function delteacher(outparamters){
			var flag=outparamters.getValue("flag");
			if(flag=="true"){
				if(sel!=null&&confirm('确认要删除吗？')){
					var hashvo=new ParameterSet();
					hashvo.setValue("sel",sel.substring(0,sel.length-1));
					var request=new Request({method:'post',asynchronous:false,onSuccess:showfile3,functionId:'2020030052'},hashvo);
				}
			}else{
				alert(flag+TRAIN_DELETE_RESCCOURSE);
			}
		}
	}else{
		alert("请选择要删除的课程！");
		return null;
	}
}
function showfile3(outparamters){
	if(outparamters!=null){
		var flag = outparamters.getValue("fg");
		var sels = outparamters.getValue("ids");
		var selss = sels.split(",");
		if(flag=="true"){
			var str="";
			var delSelf = false;
			var currnode = parent.frames['mil_menu'].Global.selectedItem.root();
			var curRoot;
		    for(var j=0;j<selss.length;j++) {
		        if(currnode!=null){ 
		        	currnode = getTreeItem(selss[j], currnode);
		        	curRoot = currnode.parent;
		        	currnode.remove();
		        }			  	
			}
		    
		    if(currnode!=null)
		        curRoot.select();
		    else
		    	currnode.select();
		    
		var a_code = document.getElementsByName("a_code")[0].value;
		courseForm.action = "/train/resource/course.do?b_query=link&a_code=" + a_code;
		courseForm.submit();
		}else{
			alert("删除失败");
		}
	}
}

function approve(){
	var sel="";
	var status = "";
	var sels=document.getElementsByName("r5000");
	var s = document.getElementsByName("r5022");
	
	for(var i=0;i<sels.length;i++){
		if(sels[i].checked){
			if(s[i].value != "02"){
				alert("只能对已报批的课程进行批准!");
				return null;
			}else{
				sel+=sels[i].value+",";
				status+=s[i].value+",";
			}
		}
	}
	
	if(sel!=""&&sel.length>0){
		
		if(sel!=null&&confirm('确认要批准吗？')){
			var a = 1;
			var hashvo=new ParameterSet();
			hashvo.setValue("s","1");
			hashvo.setValue("sel",sel.substring(0,sel.length-1));
			hashvo.setValue("status",status.substring(0,status.length-1));
			var request=new Request({method:'post',asynchronous:false,onSuccess:addcode,functionId:'202003005101'},hashvo);
		}
	}else{
		alert("请选择要批准的课程！");
		return null;
	}
}

function addcode(outparamters){
	var codeitem=outparamters.getValue("codeitem");
	var codeitems=codeitem.split(",");
	var currnode=parent.frames['mil_menu'].Global.selectedItem;
	var selectNodeId = currnode.uid;
	currnode = currnode.root();
	if(currnode) {
		for(var i=0;i<codeitems.length;i++){
			var item = codeitems[i].split(":");
			var codeitemdesc = item[1];
			var codeId = item[2];
			var itemid = item[3];
			var flag=true;
			currnode = getTreeItem(codeId, currnode);
			if(!currnode)
				continue;
			
			var itemNode = getTreeItem(itemid, currnode);
			if(itemNode) {
				currnode = itemNode;
				flag=false;			
				if(codeitemdesc!=currnode.text) {
					currnode.setText(codeitemdesc);
					currnode.reload();
				}
			}
			
			if(flag) {
				var action="/train/resource/course.do?b_query=link&a_code="+itemid;
				var imgurl='/images/icon_wsx.gif';
				var xml="/train/resource/course/get_code_tree.jsp?codesetid=55&codeitemid="+itemid;
				parent.frames['mil_menu'].add(currnode,itemid,codeitemdesc,action,"mil_body",codeitemdesc,imgurl,xml);
			}
		}
	}
		
	var hashvo=new ParameterSet();
	hashvo.setValue("s","1");
	hashvo.setValue("codeitem",codeitem);
	var request=new Request({method:'post',asynchronous:false,onSuccess:showfile,functionId:'2020030088'},hashvo);
}

function reject(){
	var sel="";
	var status = "";
	var sels=document.getElementsByName("r5000");
	var s = document.getElementsByName("r5022");
	for(var i=0;i<sels.length;i++){
		if(sels[i].checked){
				if(s[i].value != "02"){
					alert("只能对已报批的课程进行驳回!");
					return null;
				}else{
					sel+=sels[i].value+",";
					status+=s[i].value+",";
				}
			}
	}
	if(sel!=""&&sel.length>0){
		if(sel!=null&&confirm('确认要驳回吗？')){
			var hashvo=new ParameterSet();
			hashvo.setValue("s","2");
			hashvo.setValue("sel",sel.substring(0,sel.length-1));
			hashvo.setValue("status",status.substring(0,status.length-1));
			var request=new Request({method:'post',asynchronous:false,onSuccess:showfile,functionId:'2020030088'},hashvo);
		}
	}else{
		alert("请选择要驳回的课程！");
		return null;
	}
}
function susp(){
	var sel="";
	var stats=true;
	var sels=document.getElementsByName("r5000");
	var s = document.getElementsByName("r5022");
	for(var i=0;i<sels.length;i++){
		if(sels[i].checked){
			sel+=sels[i].value+",";
			if(s[i].value!="04")
				stats=false;
		}
	}
	if(sel!=""&&sel.length>0){
		if(checkIsParent(sel)){
			alert(TRAIN_COURSE_VIEW_ERROR);
			return false;
		}
		if(!stats)
			alert("请选择已发布的培训课程，再暂停！");
		else{
			var hashvo=new ParameterSet();
			hashvo.setValue("sel",sel.substring(0,sel.length-1));
			var request=new Request({method:'post',asynchronous:false,onSuccess:showfile,functionId:'2020030057'},hashvo);
		}
	}else{
		alert("请选择要暂停的课程！");
	}
}
function pub(){
	var sel="";
	var stats=true;
	var sels=document.getElementsByName("r5000");
	for(var i=0;i<sels.length;i++){
		if(sels[i].checked){
			sel+=sels[i].value+",";
			if(sels[i].title=="04")
				stats=false;
		}
	}
	if(sel!=""&&sel.length>0){
		if(!stats)
			alert("培训课程已经发布，不能重复发布！");
		else{
			if(checkIsParent(sel)){
				alert(TRAIN_COURSE_VIEW_ERROR);
				return false;
			}
			var hashvo=new ParameterSet();
			hashvo.setValue("sel",sel.substring(0,sel.length-1));
			var request=new Request({method:'post',asynchronous:false,onSuccess:showfile1,functionId:'2020030058'},hashvo);
		}
	}else{
		alert("请选择要发布的课程！");
	}
}
function push(){
	var sel="";
	var stats=true;
	var sels=document.getElementsByName("r5000");
	var s = document.getElementsByName("r5022");
	for(var i=0;i<sels.length;i++){
		if(sels[i].checked){
			sel+=sels[i].value+",";
			if(s[i].value!="04")
				stats=false;
		}
	}
	if(sel!=""&&sel.length>0){
		if(!stats)
			alert("\u8BF7\u9009\u62E9\u5DF2\u53D1\u5E03\u7684\u57F9\u8BAD\u8BFE\u7A0B\uFF0C\u518D\u63A8\u9001\uFF01");//请选择已发布的培训课程，再推送！
		else{
			if(checkIsParent(sel)){
				alert("\u53EA\u80FD\u64CD\u4F5C\u7BA1\u7406\u8303\u56F4\u5185\u7684\u8BFE\u7A0B\uFF01");//只能操作管理范围内的课程！
				return false;
			}
			sel = sel.substring(0,sel.length-1);
			var return_vo = selectPerson(sel);
			if(return_vo!=null&&return_vo.length>0){
				var hashvo=new ParameterSet();
				hashvo.setValue("personstr",return_vo);
				hashvo.setValue("sel",sel);
				hashvo.setValue("basePath","<%=basePath %>");
				var request=new Request({method:'post',asynchronous:false,onSuccess:pushinfo,functionId:'2020030049'},hashvo);
			}
		}
	}else{
		alert("\u8BF7\u9009\u62E9\u8981\u63A8\u9001\u7684\u8BFE\u7A0B\uFF01");//请选择要推送的课程！
	}
}
function pushinfo(outparamters){
	if(outparamters!=null){
		var flag=outparamters.getValue("flag");
		if("ok"==flag){
			alert("\u63A8\u9001\u6210\u529F\uFF01");//推送成功
		} else {
			alert(flag);//邮件地址异常的人员
		}
	}
}
function selectPerson(sel){
	var preflag = 2;
	//if(window.confirm("是否要列出已推送过的人员？如课程有修改可以进行再推送。\r\n\r\n点击\"确定\"为显示，\"取消\"为不显示。")){
		preflag = 0;
	//}
	var theurl="/train/request/selectpre.do?b_query=link`itemkey="+sel+"`nbase=all`preflag="+preflag;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
   	var return_vo= window.showModalDialog(iframe_url, 'trainClass_win2', 
      				"dialogWidth:600px; dialogHeight:430px;resizable:no;center:yes;scroll:yes;status:no");	
    return return_vo;
}
function showfile1(outparamters){
	if(outparamters!=null){
		var flag=outparamters.getValue("flag");
		if("ok"==flag){
			var a_code = document.getElementsByName("a_code")[0].value;
			courseForm.action = "/train/resource/course.do?b_query=link&a_code=" + a_code;
			courseForm.submit();
		}else{
			alert(getDecodeStr(flag));
		}
	}
}
//编辑备注字段
function editMemoFild(priFld,memoFldName,isflag)
{
	var target_url="/train/resource/memoFld.do?b_query=link`flag="+isflag+"`type=7`priFld="+priFld+"`memoFldName="+memoFldName;
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
	var return_vo= window.showModalDialog(iframe_url, "memoFld_win", 
	              "dialogWidth:390px; dialogHeight:360px;resizable:no;center:yes;scroll:no;status:no");	
	if(!return_vo)
	   	return;	   
   	if(return_vo.flag=="true")
    {       
       var code = document.getElementById("a_code").value;
       courseForm.action="/train/resource/course.do?b_query=link&a_code="+code;
	   courseForm.submit();	
	}  	
}
function checkIsParent(id){
	var isp = false;
	var hashvo = new ParameterSet();
    hashvo.setValue("id",id);
    var request=new Request({method:'post',asynchronous:false,onSuccess:isParent,functionId:'2020030070'},hashvo);
	function isParent(outparamters){
		if(outparamters){
		   var temp1=outparamters.getValue("isParent");
		   if("yes" == temp1)
				isp = true;
	   	}
	} 
   	return isp;
}

function upholdComment(courseid){
	var url = "/train/resource/mylessonscomment.do?b_comment=link`opt=comment`moduleFlag=1`lesson="+courseid;
	var iframesrc="/general/query/common/iframe_query.jsp?src="+$URL.encode(url);
	var return_vo= window.showModalDialog(iframesrc, false, 
	"dialogWidth:520px; dialogHeight:500px;resizable:no;center:yes;scroll:yes;status:no");

}
 function searchability(r5000){
	 courseForm.action="/train/resource/course/showability.do?b_query=link&r5000="+r5000;
	 courseForm.submit();
 }

 function addability()
 {
	var sel = "", selid="";
	var sels=document.getElementsByName("r5000");
	var s = document.getElementsByName("r5022");
	var k = 1;
	for(var i=0;i<sels.length;i++){
		if(sels[i].checked){
			if(s[i].value != "04"){
				alert(TRAIN_COURSE_ABILITY_PUB);
				return null;
			}else{
				selid+=sels[i].value+",";
				sel+=sels[i].value+",";
			}
			if(i==(k*1000-1)){
				selid+=";";
				k++;
			}
		}
	}
	
	if(sel == null|| sel.length<1){
		alert(TRAIN_COURSE_ABILITY_SELECT);
		return;
	}
	
	if(checkIsParent(sel)){
		alert(TRAIN_COURSE_VIEW_ERROR);
		return false;
	}
	
	var vo = new ParameterSet();
	vo.setValue("lessonids", selid);
	var request = new Request( {asynchronous:false, onSuccess:check_ok, functionId:'20200130014'}, vo);
	
	function check_ok(outparamters){
	    if (outparamters == null)
			return;
		
	    var flag = outparamters.getValue("msg");
	    sel = outparamters.getValue("lid");
	    
		if(sel == null || sel.length<1){
			if ("true" != flag)
				alert(flag);
			return;
		}
		var infos = new Array();
		infos[0] = "";
		infos[1] = "35";
		var thecodeurl = "/performance/kh_system/kh_template/init_kh_item.do?br_selectpoint=query";
		var iframe_url = "/general/query/common/iframe_query.jsp?src=" + thecodeurl;
		var points = window.showModalDialog(iframe_url, infos,
					"dialogWidth:450px; dialogHeight:360px;resizable:no;center:yes;scroll:yes;status:no");
		if (points == undefined || points == '') {
			return;
		}

		var hashvo = new ParameterSet();
		hashvo.setValue("lessonids", sel);
		hashvo.setValue("pointids", points);
		var request = new Request( {asynchronous:false, onSuccess:additem_ok, functionId:'20200130013'}, hashvo);

		if ("true" != flag) {
			alert(flag);
		}
	}
}
	function additem_ok(outparamters) {
		if (outparamters != null) {
			var flag = outparamters.getValue("flag");
			if ("ture" == flag) {
				var a_code = document.getElementsByName("a_code")[0].value;
				courseForm.action = "/train/resource/course.do?b_query=link&a_code=" + a_code;
				courseForm.submit();
			} else {
				alert(TRAIN_COURSE_ABILITY_FAIL);
			}
		}
	}
//弹出图标
 window.onload=function(){
        var box = document.getElementById("table");
        var imglist= byClass("pic",box);
        for(var i=0;i<imglist.length;i++){
            (function(i){
                var img=imglist[i];
                var body=document.getElementsByTagName("body")[0];
                img.onmouseover=function(e){
                    var element=getEventTarget(e);
                    if(element){
                        e =window.event || e;
                        var x= e.clientX;
                        var y= e.clientY;//鼠标所在元素的Y轴的坐标（top的距离）
                        var div=document.createElement("div");//创建一个div
                        div.setAttribute("id","pic-pop");//给div设置一个属性
                        div.style.backgroundColor="white";
                        div.innerHTML='<img src="'+img.getAttribute('src')+'" width="240" height="120"style="margin:2px"  align="center"/>';//div填充内容
                        div.style.position="absolute";//div定位
                        div.style.top=y+"px";//div距离top的距离
                        div.style.left=document.body.scrollWidth-335+"px";//div距离left的距离
                        body.appendChild(div);//添加到body
                    }
                };
                img.onmouseout=function(){//绑定鼠标事件
                    var pop=document.getElementById("pic-pop");
                    if(pop) body.removeChild(pop);//移除创建的元素
                };
            })(i);
        }
    };
    //获取事件源的对象
    //返回元素对象
    var getEventTarget = function(event) {
        var e =window.event || event;
        return e.srcElement || e.target;
    };
    //根据class和父级获取对应的元素
    //返回元素对象集合
    var byClass = function(className, parentNode) {
        var tags = !parentNode ? (document.getElementsByTagName('*') || parentNode.all) : (parentNode.getElementsByTagName('*') || document.all);
        var elements = [];
        for (var i = 0; i < tags.length; i++) {
            if (tags[i].className && typeof tags[i].className == "string") {
                var cs = tags[i].className.indexOf(" ") >= 0 ? tags[i].className.split(' ') : [tags[i].className];
                for (var j = 0; j < cs.length; j++) {
                    if (cs) {
                        if (className == cs[j]) {
                            elements.push(tags[i]);
                            break;
                        }
                    }
                }
            }
        }
        return elements
    };
    
    
    
    
  

    
</script>
<html:form action="/train/resource/course">
	<html:hidden name="courseForm" property="searchstr" />
    <html:hidden name="courseForm" property="a_code" />
	<html:hidden name="courseForm" property="a_code1" />
	
	<%
		int i = 0;
		String classname = "t_cell_locked2";
	%>
	<table border="0" cellpadding="0" cellspacing="0">
	<tr><td>
	<div class="tbl-container" id="tbl-container">
	<table width="100%" border="0" cellspacing="0" align="center" id="table" cellpadding="0" >
		
		<tr ><!-- class="fixedHeaderTr" -->
			<td align="center" class="TableRow <%=classname %> noleft" style="border-top: none;">
			<input type="checkbox" name="selbox" onclick="batch_select(this,'r5000');" title='<bean:message key="label.query.selectall"/>' />
			</td>
			
			<hrms:priv func_id="32306C20" >
			     <td align="center" class="<%=classname %> common_border_color" nowrap>&nbsp;<bean:message key="train.resource.mylessons.coursecomment"/>&nbsp;</td>
			</hrms:priv> 
			<logic:equal value="true" name="courseForm" property="flag">
			<hrms:priv func_id="32306C22" >
			 <td align="center" class="<%=classname %> common_border_color" nowrap>&nbsp;<bean:message key="train.course.ability"/>&nbsp;</td>
			</hrms:priv> 
			</logic:equal>
			<logic:iterate id="element" name="courseForm"
				property="itemlist">
				<logic:equal value="true" name="element" property="visible">
				<td align="center" class="<%=classname %> common_border_color" nowrap>
					&nbsp;<bean:write name="element" property="itemdesc" filter="true" />&nbsp;
				</td>
					<logic:equal value="r5003" name="element" property="itemid" >
						<% classname="t_header_locked"; %>
					</logic:equal>
				</logic:equal>
			</logic:iterate>
		</tr>
		<hrms:paginationdb id="element2" name="courseForm"
			sql_str="courseForm.strsql" table="" where_str="courseForm.strwhere"
			columns="courseForm.columns" page_id="pagination"
			pagerows="${courseForm.pagerows}" order_by=" order by norder,r5000">
			<%
				classname="t_cell_locked";
				if (i % 2 == 0) {
			%>
			<tr class="trShallow" onclick="javascript:tr_onclick(this,'')">
				<%
					} else {
				%>
			
			<tr class="trDeep" onclick="javascript:tr_onclick(this,'E4F2FC')">
				<%
					}
					i++;
				%>
				<logic:notEqual value="01" name="element2" property="r5022">
					<bean:define id="idid" name="element2" property="r5000" />
					<bean:define id="codeid" name="element2" property="r5004" />
					<bean:define id="bbbb" name="element2" property="r5020" />
					<%String r5000 = SafeCode.encode(PubFunc.encrypt(idid.toString())); 
					  String r5004 = SafeCode.encode(PubFunc.encrypt(codeid.toString())); %>
					<td align="center" class="<%=classname %> common_border_color" nowrap>
						&nbsp;&nbsp;&nbsp;
						<input type="checkbox" name="r5000" value='<%=r5000 %>' />
						<input type="hidden" name="r5022" value = '<bean:write name="element2" property="r5022" filter="false" />'/>
						<input type="hidden" name="r5004" value='<%=r5004 %>' />
						&nbsp;
					</td>
					<hrms:priv func_id="32306C20">
					    
					     <td align="center" class="<%=classname %> common_border_color" nowrap>&nbsp;
					       <logic:notEqual value="0" name="element2" property="commentnum">
					         <img align="middle" src="/images/edit.gif" onclick="upholdComment('<%=r5000 %>')" style="cursor:hand;">
					       </logic:notEqual> &nbsp;
					     </td>
					    
					</hrms:priv>
					<logic:equal value="true" name="courseForm" property="flag">
					<hrms:priv func_id="32306C22" >
					     <td align="center" class="<%=classname %> common_border_color" nowrap>&nbsp;
					         <img align="middle" src="/images/view.gif" onclick="searchability('<%=r5000 %>')" style="cursor:hand;">
					     </td>
					</hrms:priv>
					</logic:equal>
					<logic:iterate id="element1" name="courseForm" property="itemlist">
						<logic:equal value="true" name="element1" property="visible">
							<bean:define id="nid" name="element1" property="itemid" />
							<bean:define id="img" name="element2" property="imageurl" />
							<logic:equal name="element1" property="itemtype" value="M">
								<td align="left" class="<%=classname %> common_border_color" nowrap>
									<!-- 
									<bean:define id="m" name="element2" property="${nid}"></bean:define>
									&nbsp;  m!=null?m.toString().replaceAll("\r\n","<br/>"):""  &nbsp;
									 -->
									 <%int j=1; %>
									 <hrms:priv func_id="32306C06" module_id="">
									 	<%j=0; %>
									 </hrms:priv>
									 <hrms:coursesortisparent codeid="${bbbb}" isParent="1">
										&nbsp;<a href='javascript:editMemoFild("<%=r5000 %>","${nid}","1");'>
										<bean:write name="element2" property="${nid}" filter="false"/>
										</a>&nbsp;
									</hrms:coursesortisparent>
									<logic:equal name="element2" property="r5022" value="04">
									<hrms:coursesortisparent codeid="${bbbb}" isParent="0">
										&nbsp;<a href='javascript:editMemoFild("<%=r5000 %>","${nid}","1");'>
										<bean:write name="element2" property="${nid}" filter="false"/>
										
										</a>&nbsp;
									</hrms:coursesortisparent>
									</logic:equal>
									<logic:notEqual name="element2" property="r5022" value="04">
									<hrms:coursesortisparent codeid="${bbbb}" isParent="0">
										&nbsp;<a href='javascript:editMemoFild("<%=r5000 %>","${nid}","<%=j %>");'>
										<bean:write name="element2" property="${nid}" filter="false"/>
										</a>&nbsp;
									</hrms:coursesortisparent>
									</logic:notEqual>
								</td>
							</logic:equal>
							<logic:notEqual name="element1" property="itemtype" value="M">
							
								<logic:equal name="element1" property="itemtype" value="N">
									<td align="right" class="<%=classname %> common_border_color" nowrap>&nbsp;
								</logic:equal>
								<logic:equal name="element1" property="itemtype" value="D">
									<td align="right" class="<%=classname %> common_border_color" nowrap>&nbsp;
								</logic:equal>
								<logic:equal name="element1" property="itemtype" value="A">
									<td align="left" class="<%=classname %> common_border_color" nowrap>&nbsp;
								</logic:equal>
	
								<logic:equal name="element1" property="itemtype" value="A">
									<logic:equal name="element1" property="codesetid" value="0">
										<logic:equal name="element1" value="r5004" property="itemid">
											<!-- <script>getR5004("<%=r5004 %>");</script> -->
											<hrms:codetoname codeid="55" name="element2" codevalue="r5004" codeitem="codeitem" scope="page"/>  	      
	          	    	   					 <bean:write name="codeitem" property="codename" />&nbsp;  
										</logic:equal>
									<logic:equal name="element1" value="imageurl" property="itemid">
											     <%if(!"".equals(img.toString())&&null!=img){%>
												<img align="middle" src="/servlet/vfsservlet?fileid=<%=img %>"  width="45" height="25" class="pic"/>
												<% }%>
											</logic:equal>
										<hrms:coursesortisparent codeid="${bbbb}" isParent="0">
											<logic:equal name="element1" value="oper" property="itemid">
											  <hrms:priv func_id="32306C06" module_id="">
											  	<img align="middle" src="/images/edit.gif" border="0" onclick="edite('<%=r5000 %>','<%=r5004 %>');" style="cursor:hand;">
											  </hrms:priv>
											</logic:equal>
											<logic:equal name="element1" value="course" property="itemid">
												<img align="middle" src="/images/book.gif" border="0" onclick="courseware('<%=r5000 %>','<%=r5004 %>');" style="cursor:hand;">
											</logic:equal>
											<logic:equal name="element1" value="detail" property="itemid">
											  <hrms:priv func_id="32306C06" module_id="">
											    <a href="/train/resource/course/showdetail.do?b_showdetail=link&id=<%=r5000 %>&a_code=${courseForm.a_code}">
											  	    <img align="middle" src="/images/view.gif" border="0" style="cursor:hand;">
											  	</a>
											  </hrms:priv>
											</logic:equal>
										</hrms:coursesortisparent>
										<hrms:coursesortisparent codeid="${bbbb}" isParent="1">
											<logic:equal name="element1" value="oper" property="itemid">
											  <hrms:priv func_id="32306C06" module_id="">
											  	<img align="middle" src="/images/view.gif" border="0" onclick="edite('<%=r5000 %>','<%=r5004 %>');" style="cursor:hand;">
											  </hrms:priv>
											</logic:equal>
											<logic:equal name="element1" value="course" property="itemid">
												<img align="middle" src="/images/book1.gif" border="0" onclick="courseware('<%=r5000 %>','<%=r5004 %>');" style="cursor:hand;">
											</logic:equal>
										</hrms:coursesortisparent>
											
										<logic:notEqual value="r5004" name="element1" property="itemid">
										<logic:notEqual value="imageurl" name="element1" property="itemid">
										<logic:notEqual value="oper" name="element1" property="itemid">
										<logic:notEqual value="course" name="element1" property="itemid">
											<logic:equal value="r5003" name="element1" property="itemid">
												<a border="0" href="javascript:edite('<%=r5000 %>','<%=r5004 %>');" style="cursor:hand;"><bean:write name="element2" property="${nid}" filter="false" /></a>
											   <%classname="t_cell_locked_b"; %>
											</logic:equal>
											<logic:notEqual value="r5003" name="element1" property="itemid">
												<bean:write name="element2" property="${nid}" filter="false" />
											</logic:notEqual>
										</logic:notEqual>
										</logic:notEqual>
										</logic:notEqual>
										</logic:notEqual>
									</logic:equal>
									<logic:notEqual name="element1" property="codesetid" value="0">
										<logic:notEqual name="element1" property="codesetid" value="UN">
											<bean:define id="codesetid" name="element1" property="codesetid" />
											<hrms:codetoname codeid="${codesetid}" name="element2"
												codevalue="${nid}" codeitem="codeitem" scope="page" />
											<bean:write name="codeitem" property="codename" />
										</logic:notEqual>
										<logic:equal name="element1" property="codesetid" value="UN">
												<hrms:codetoname codeid="UN" name="element2"
													codevalue="${nid}" codeitem="codeitem" scope="page" />
												<bean:write name="codeitem" property="codename" />
										</logic:equal>
									</logic:notEqual>
								</logic:equal>
								<logic:notEqual name="element1" property="itemtype" value="A">
									<bean:write name="element2" property="${nid}" filter="false" />
								</logic:notEqual>
								&nbsp;<script>document.write("</td>");</script>
							</logic:notEqual>
						</logic:equal>
					</logic:iterate>
				</logic:notEqual>
				<logic:equal value="01" name="element2" property="r5022">
					<logic:notEqual value="1" name="element2" property="r5037">
						<bean:define id="idid" name="element2" property="r5000" />
						<bean:define id="codeid" name="element2" property="r5004" />
						<bean:define id="bbbb" name="element2" property="r5020" />
						<%String r5000 = SafeCode.encode(PubFunc.encrypt(idid.toString()));
					      String r5004 = SafeCode.encode(PubFunc.encrypt(codeid.toString())); %>
						<td align="center" class="<%=classname %> common_border_color" nowrap>
							&nbsp;&nbsp;&nbsp;
							<input type="checkbox" name="r5000" value='<%=r5000 %>' />
							<input type="hidden" name="r5022" value = '<bean:write name="element2" property="r5022" filter="false" />'/>
							<input type="hidden" name="r5004" value='<%=r5004 %>' />
							&nbsp;
						</td>
						
						<hrms:priv func_id="32306C20">
					     <td align="center" class="<%=classname %> common_border_color" nowrap>&nbsp;
					       <logic:notEqual value="0" name="element2" property="commentnum">
					         <img align="middle" src="/images/edit.gif" onclick="upholdComment('<%=r5000 %>')" style="cursor:hand;">
					       </logic:notEqual> &nbsp;
					     </td>
					    </hrms:priv>
					    <logic:equal value="true" name="courseForm" property="flag">
						<hrms:priv func_id="32306C22" >
					        <td align="center" class="<%=classname %> common_border_color" nowrap>&nbsp;
					             <img align="middle" src="/images/view.gif" onclick="searchability('<%=r5000 %>')" style="cursor:hand;">
					        </td>
					    </hrms:priv> 
					    </logic:equal>
						<logic:iterate id="element1" name="courseForm" property="itemlist">
							<logic:equal value="true" name="element1" property="visible">
								<bean:define id="nid" name="element1" property="itemid" />
								<bean:define id="img" name="element2" property="imageurl" />
								<logic:equal name="element1" property="itemtype" value="M">
									<td align="left" class="<%=classname %> common_border_color" nowrap>
										<!-- 
										<bean:define id="m" name="element2" property="${nid}"></bean:define>
										&nbsp;  m!=null?m.toString().replaceAll("\r\n","<br/>"):""  &nbsp;
										 -->
										 <%int j=1; %>
										 <hrms:priv func_id="32306C06" module_id="">
										 	<%j=0; %>
										 </hrms:priv>
										 <hrms:coursesortisparent codeid="${bbbb}" isParent="1">
											&nbsp;<a href='javascript:editMemoFild("<%=r5000 %>","${nid}","1");'>
											<bean:write name="element2" property="${nid}" filter="false"/>
											</a>&nbsp;
										</hrms:coursesortisparent>
										<logic:equal name="element2" property="r5022" value="04">
										<hrms:coursesortisparent codeid="${bbbb}" isParent="0">
											&nbsp;<a href='javascript:editMemoFild("<%=r5000 %>","${nid}","1");'>
											<bean:write name="element2" property="${nid}" filter="false"/>
											
											</a>&nbsp;
										</hrms:coursesortisparent>
										</logic:equal>
										<logic:notEqual name="element2" property="r5022" value="04">
										<hrms:coursesortisparent codeid="${bbbb}" isParent="0">
											&nbsp;<a href='javascript:editMemoFild("<%=r5000 %>","${nid}","<%=j %>");'>
											<bean:write name="element2" property="${nid}" filter="false"/>
											</a>&nbsp;
										</hrms:coursesortisparent>
										</logic:notEqual>
									</td>
								</logic:equal>
								<logic:notEqual name="element1" property="itemtype" value="M">
									<logic:equal name="element1" property="itemtype" value="N">
										<td align="right" class="<%=classname %> common_border_color" nowrap>&nbsp;
									</logic:equal>
									<logic:equal name="element1" property="itemtype" value="D">
										<td align="right" class="<%=classname %> common_border_color" nowrap>&nbsp;
									</logic:equal>
									<logic:equal name="element1" property="itemtype" value="A">
										<td align="left" class="<%=classname %> common_border_color" nowrap>&nbsp;
									</logic:equal>
		
									<logic:equal name="element1" property="itemtype" value="A">
										<logic:equal name="element1" property="codesetid" value="0">
											<logic:equal name="element1" value="r5004" property="itemid">
												<!-- <script>getR5004("<%=r5004 %>");</script> -->
												<hrms:codetoname codeid="55" name="element2" codevalue="r5004" codeitem="codeitem" scope="page"/>  	      
		          	    	   					 <bean:write name="codeitem" property="codename" />&nbsp;  
											</logic:equal>
											
											<logic:equal name="element1" value="imageurl" property="itemid">
											        <%if(!"".equals(img.toString())&&null!=img){%>
												<img align="middle" src="/servlet/vfsservlet?fileid=<%=img %>"  width="45" height="25" class="pic"/>
												<% }%>
											</logic:equal>
											<hrms:coursesortisparent codeid="${bbbb}" isParent="0">
												<logic:equal name="element1" value="oper" property="itemid">
												  <hrms:priv func_id="32306C06" module_id="">
												  	<img align="middle" src="/images/edit.gif" border="0" onclick="edite('<%=r5000 %>','<%=r5004 %>');" style="cursor:hand;">
												  </hrms:priv>
												</logic:equal>
												<logic:equal name="element1" value="course" property="itemid">
													<img align="middle" src="/images/book.gif" border="0" onclick="courseware('<%=r5000 %>','<%=r5004 %>');" style="cursor:hand;">
												</logic:equal>
												<logic:equal name="element1" value="detail" property="itemid">
												  <hrms:priv func_id="32306C06" module_id="">
												  <a href="/train/resource/course/showdetail.do?b_showdetail=link&id=<%=r5000 %>&a_code=${courseForm.a_code}">
												  	<img align="middle" src="/images/view.gif" border="0" style="cursor:hand;">
												  </a>
												  </hrms:priv>
												</logic:equal>
											</hrms:coursesortisparent>
											<hrms:coursesortisparent codeid="${bbbb}" isParent="1">
												<logic:equal name="element1" value="oper" property="itemid">
												  <hrms:priv func_id="32306C06" module_id="">
												  	<img align="middle" src="/images/view.gif" border="0" onclick="edite('<%=r5000 %>','<%=r5004 %>');" style="cursor:hand;">
												  </hrms:priv>
												</logic:equal>
												<logic:equal name="element1" value="course" property="itemid">
													<img align="middle" src="/images/book1.gif" border="0" onclick="courseware('<%=r5000 %>','<%=r5004 %>');" style="cursor:hand;">
												</logic:equal>
											</hrms:coursesortisparent>
												
											<logic:notEqual value="r5004" name="element1" property="itemid">
											<logic:notEqual value="oper" name="element1" property="itemid">
											<logic:notEqual value="imageurl" name="element1" property="itemid">
											<logic:notEqual value="course" name="element1" property="itemid">
												<logic:equal value="r5003" name="element1" property="itemid">
													<a border="0" href="javascript:edite('<%=r5000 %>','<%=r5004 %>');" style="cursor:hand;"><bean:write name="element2" property="${nid}" filter="false" /></a>
												   <%classname="t_cell_locked_b"; %>
												</logic:equal>
												<logic:notEqual value="r5003" name="element1" property="itemid">
													<bean:write name="element2" property="${nid}" filter="false" />
												</logic:notEqual>
											</logic:notEqual>
											</logic:notEqual>
											</logic:notEqual>
											</logic:notEqual>
										</logic:equal>
										<logic:notEqual name="element1" property="codesetid" value="0">
											<logic:notEqual name="element1" property="codesetid" value="UN">
												<bean:define id="codesetid" name="element1" property="codesetid" />
												<hrms:codetoname codeid="${codesetid}" name="element2"
													codevalue="${nid}" codeitem="codeitem" scope="page" />
												<bean:write name="codeitem" property="codename" />
											</logic:notEqual>
											<logic:equal name="element1" property="codesetid" value="UN">
													<hrms:codetoname codeid="UN" name="element2"
														codevalue="${nid}" codeitem="codeitem" scope="page" />
													<bean:write name="codeitem" property="codename" />
											</logic:equal>
										</logic:notEqual>
									</logic:equal>
									<logic:notEqual name="element1" property="itemtype" value="A">
										<bean:write name="element2" property="${nid}" filter="false" />
									</logic:notEqual>
									&nbsp;<script>document.write("</td>");</script>
									</logic:notEqual>
								</logic:equal>
						</logic:iterate>
					</logic:notEqual>
			    </logic:equal>
			</tr>
		</hrms:paginationdb>
	</table>
	</div>
	</td>
	</tr>
	<tr>
		<td>
				<table width="100%" class="RecordRowP" align="center">
					<tr>
						<td valign="bottom" class="tdFontcolor">
							<hrms:paginationtag name="courseForm"
								pagerows="${courseForm.pagerows}" property="pagination"
								scope="page" refresh="true"></hrms:paginationtag>
						</td>
						<td align="right" nowrap class="tdFontcolor">
							<p align="right">
								<hrms:paginationdblink name="courseForm"
									property="pagination" nameId="courseForm" scope="page">
								</hrms:paginationdblink>
						</td>
					</tr>
				</table>
		</td>
	</tr>
		<tr>
			
			<td align="left" style="padding-top: 5px;">
			<hrms:priv func_id="32306C05" module_id="">
				<input type="button" class="mybutton" value='<bean:message key="button.insert" />' onclick="add();" />
		    </hrms:priv>
		    <hrms:priv func_id="32306C07" module_id="">
				<input type="button" class="mybutton" value='<bean:message key="button.delete" />' onclick="del();"/>
			</hrms:priv>
			<logic:notEmpty name="courseForm" property="diyType">
				<hrms:priv func_id="32306C18" module_id="">
					<input type="button" class="mybutton" value='<bean:message key="info.appleal.state3" />' onclick="approve();"/>
				</hrms:priv>
				<hrms:priv func_id="32306C19" module_id="">
					<input type="button" class="mybutton" value='<bean:message key="info.appleal.state2" />' onclick="reject();"/>
				</hrms:priv>
			</logic:notEmpty>
			<hrms:priv func_id="32306C08" module_id="">
				<input type="button" class="mybutton" value="发布" onclick="pub();"/>
			</hrms:priv>
			<hrms:priv func_id="32306C09" module_id="">
				<input type="button" class="mybutton" value="暂停" onclick="susp();"/>
			</hrms:priv>			
			<hrms:priv func_id="32306C17" module_id="">
				<input type="button" class="mybutton" value="查询" onclick="searchInform();"/>
			</hrms:priv>
			<hrms:priv func_id="32306C10" module_id="">
				<input type="button" class="mybutton" value="调整顺序" onclick="sortRecord();"/>
			</hrms:priv>
			<hrms:priv func_id="32306C16" module_id="">
				<input type="button" class="mybutton" value="推送" onclick="push();"/>
			</hrms:priv>
			<logic:equal value="true" name="courseForm" property="flag">
			<hrms:priv func_id="32306C23" module_id="">
				<input type="button" class="mybutton" value="<bean:message key='train.course.addability'/>" onclick="addability();"/>
			</hrms:priv>
			</logic:equal>
				<logic:equal value="dxt" name="courseForm" property="returnvalue">
					<input type="button" class="mybutton" value='<bean:message key='reportcheck.return'/>' onclick="returnFirst();" />
				</logic:equal>
			</td>
		</tr>
	</table>
<script>
    ///zhangcq  2016/5/11 判断分页只能输入数字
    var bt= document.getElementsByName("pagerows")[0];
    if(document.addEventListener){ //DOM level 2 
			bt.addEventListener("keyup",eventFun,true);
		}
		else if(window.attachEvent){ //ie 5
		
			bt.attachEvent("onkeyup",eventFun);
		}
		
		function eventFun(){
			bt.value=bt.value.replace(/\D/gi,"")
		}
</script>
</html:form>