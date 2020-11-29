<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script language="javascript" src="../../../ajax/constant.js"></script>
<script language="javascript" src="../../../ajax/basic.js"></script>
<script language="javascript" src="../../../ajax/common.js"></script>
<script language="javascript" src="../../../ajax/control.js"></script>
<script language="javascript" src="../../../ajax/dataset.js"></script>
<script language="javascript" src="../../../ajax/editor.js"></script>
<script language="javascript" src="../../../ajax/dropdown.js"></script>
<script language="javascript" src="../../../ajax/table.js"></script>
<script language="javascript" src="../../../ajax/menu.js"></script>
<script language="javascript" src="../../../ajax/tree.js"></script>
<script language="javascript" src="../../../ajax/pagepilot.js"></script>
<script language="javascript" src="../../../ajax/command.js"></script>
<script language="javascript" src="../../../ajax/format.js"></script>
<script language="javascript" src="../../../js/validate.js"></script>
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
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

<link rel="stylesheet" href="/css/xtree.css" type="text/css">
<hrms:themes></hrms:themes>
<body style="margin-left:0px;margin-top:0px">
	<script language="javascript" type="text/javascript"
		src="/js/wz_tooltip.js"></script>
<html:form action="/kq/options/sign_point/setsign_point">  
           <table width="1000" border="0" cellspacing="0"  align="center" cellpadding="0" style="margin-top: 0">
		    	<%if(version){ %>
		    	<tr align="left" class="toolbar" style="padding-left:2px;">
				<td valign="middle" align="left">
				&nbsp;
					<hrms:priv func_id="2703702">
						<a href="###" onclick="deleteSignPoint()"><img src="/images/del.gif" alt="删除考勤点" border="0"></a>               
					</hrms:priv>
			  		<hrms:priv func_id="2703703">                   
						<a href="###" onclick="editSignPoint()"><img src="/images/edit.gif" border=0 alt="修改考勤点名称"></a>
				    </hrms:priv>
				    <hrms:priv func_id="2703707">
				    	<a href="###" onclick="setKqOrg()"><img src="/images/img_a.gif" border=0 alt="机构管理"></a>
				    </hrms:priv>
				    	<a href="###" onclick="showPerson()"><img src="/images/group_p.gif" border=0 alt="查看人员"></a>
				    <hrms:priv func_id="2703704">
				    	<a href="###" onclick="setKqArguments()"><img src="/images/settings.png" border=0 alt="参数设置"></a>
				    	
					</hrms:priv>
				</td>
				</tr>  
		    	<%} %>
		   	 <tr align="left" style="padding-left:10px;">
				<td>
				<input class="toldname text4" type="text" id="oldname" name="name" style="width:185;color:#ccc; margin-top:2px;"
					onkeyup="bsearch('oldname')" onclick="show();"  onkeydown="if(event.keyCode==13)return false;"
					value="搜索考勤点"
					onfocus="TextFocus()" onblur="TextBlur()" autocomplete="off"/>
				<!-- 解决浏览器自带的历史记录下拉框 -->
					<div id="date_panel" style="display: none;">
						<select id="date_box" name="namelist" multiple="multiple"
							style="width:185" size="6" ondblclick="okSelect();"
							onmouseout="hide();tt_HideInit();" onclick="outContent();">
							<!-- 调用wz_tooltip控件 -->

						</select>
					</div>
				</td>
				</tr>       
		    </table>
            <div id="treemenu" style="width:800px"> 
               <SCRIPT LANGUAGE=javascript> 
               var root=new xtreeItem("root","考勤点","/kq/options/sign_point/setsign_point.do?b_showpoint=link&pid=all","mil_body","考勤点","/images/spread_all.gif","/kq/options/signpoint/getSignTreeXml.jsp");
               root.setup(document.getElementById("treemenu"));

                <%
               	if("2".equals(org_expand_level)){
               	%>
					root.expand2level();
				 <%}
               %>
             </SCRIPT>		
             </div>         
</html:form>

<script>

var personObj;


  function deleteSignPoint(){
	  var currnode=Global.selectedItem;
	  var pid = currnode.uid;
	  if(pid.indexOf("P")!=0){
		  return;
	  }
	  
	  if(!confirm("您确认要删除此考勤点吗？"))
		  return;
	  
	    var hashvo=new ParameterSet();
	    hashvo.setValue("pid",pid);	
	    hashvo.setValue("changeFlag","del");
	    var request=new Request({asynchronous:false,onSuccess:deleteRs,functionId:'151211001124'},hashvo);  
  }
  function deleteRs(outparam){
	  var changeRs = outparam.getValue("changeRs");
	  if(changeRs=='1'){
		  var currnode=Global.selectedItem;
		  var root = currnode.parent;
		  if(root.childNodes.length == 1)
			  root.remove();
		  else
		      currnode.remove();
		  
		  parent.frames['mil_body'].cleanOverlays();
		  alert("删除成功！");
	  }else
		  alert("删除失败！");
  }
  
  
  
  
  function editSignPoint(){
	  var currnode=Global.selectedItem;
	  var pid = currnode.uid;
	  var name = currnode.text;
	  if(pid.indexOf("P")!=0){
		  return;
	  }
	  var theurl = "/kq/options/signpoint/editSignPoint.jsp?pid="+pid+"&name="+$URL.encode(name);
	  var return_vo = window.showModalDialog(theurl,0,"dialogHeight:250px;dialogWidth:350px;scroll:no;status:no");
	  if(return_vo == undefined)
		  return;
	  Global.selectedItem.setText(return_vo);
	  var hashvo=new ParameterSet();
	    hashvo.setValue("pid",pid);	
	    hashvo.setValue("point_name",return_vo);
	    hashvo.setValue("changeFlag","edit");
	    var request=new Request({asynchronous:false,onSuccess:editRs,functionId:'151211001124'},hashvo);  
  }
  
  function editRs(outparam){
	  var changeRs = outparam.getValue("changeRs");
	  if(changeRs=='1'){
		  
	  }
  }
  
  function showPerson(){
	  var currnode=Global.selectedItem;
	  var pid = currnode.uid;
	  if(pid.indexOf("P")!=0){
		  return;
	  }
	  kqSignPointForm.action="/kq/options/sign_point/person_point.do?b_searchperson=link&pid="+pid;
	  kqSignPointForm.target = "mil_body";
	  kqSignPointForm.submit();
  }
  
  
  function setKqArguments(){
	  var theurl = "/kq/options/sign_point/setsign_point.do?b_argument=link";
	  window.showModalDialog(theurl,0,"dialogHeight:200px;dialogWidth:300px;scroll:no;status:no");
  }

  
  function addTreeNode(pid,name,city){
	  var Cnode = null;
	  for(var i=0;i<root.childNodes.length;i++){
		  var node = root.childNodes[i];
		  if(node.text == city){
			  Cnode = node;
			  break;
		  }
	  }
	  if(Cnode==null){
          var action = "/kq/options/sign_point/setsign_point.do?b_showpoint=link&pid="+$URL.encode("C"+city);
		  
		  var cityNode = new xtreeItem("C"+city,city,action,"mil_body",city,"/images/open.png","/kq/options/signpoint/getSignTreeXml.jsp?city=C"+city);
		  root.add(cityNode);
		  
		  cityNode.expand();
          
		  
	  }
	  if(Cnode!=null && Cnode.load){
		  var action = "/kq/options/sign_point/setsign_point.do?b_showpoint=link&pid="+pid;
		  
		  var tmp = new xtreeItem(pid,name,action,"mil_body",name,"/images/table.gif");
		  Cnode.add(tmp);
	  }
	  
  }
  	//设置机构的考勤点
  	function setKqOrg(){
  		var currnode=Global.selectedItem;
  		var curruid = currnode.uid;
  		if(curruid=="root"){
  			alert("根节点不能设置考勤机构！");
  			return;
  		} else if(curruid.indexOf("P")!=-1){
  			var pid = curruid.substring(1,curruid.length);
  			var hashvo=new ParameterSet();
	    	hashvo.setValue("pid",pid);	
	    	hashvo.setValue("cflag","0");//cflag=0 只执行查询
	    	var request = new Request({asynchronous:false,onSuccess:selOk,functionId:'151211001129'},hashvo);
  		} else {
  			alert("请选择考勤点！");
  			return;
  		}
  	}
  	
  	function selOk(outparameters){
  		var orglist = outparameters.getValue("orglist");
  		var pid = outparameters.getValue("pid");
  		var obj = new Object();		
  		obj.pid = pid;
  		obj.orglist = orglist;
  		var theurl = "/kq/options/signpoint/sign_org_tree.jsp";
  		var dw=300,dh=400,dl=(screen.width-dw)/2,dt=(screen.height-dh)/2;
		var return_vo= window.showModalDialog(theurl,obj, 
        "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
		if(return_vo==undefined){
  			return;
  		}
  		var hashvo=new ParameterSet();
		hashvo.setValue("pid",return_vo.pid);
	    hashvo.setValue("orglist",return_vo.orglist);
	    hashvo.setValue("cflag","1");//cflag=1 执行添加和删除操作
  		var request = new Request({asynchronous:false,onSuccess:changeOk,functionId:'151211001129'},hashvo);
  	}
  	
  	function changeOk(outparameters){
		var cflag = outparameters.getValue("cflag");
		if(cflag=="88"){	//cflag=88  操作成功
			alert('<bean:message key="label.common.success"/>');
		} else {
			alert('<bean:message key="kq.machine.error"/>');
		}
	}
  
  
  // 搜索考勤点
   function bsearch(obj){
  	if($F('oldname')==""){
  		Element.hide('date_panel');
  		return false;
  	}
  	date_desc = document.getElementById(obj);
  	Element.show('date_panel');
  	var pos = getAbsPosition(date_desc);
	with($('date_panel')){				//定位下拉文本框位置
    	style.position = "absolute";
		style.posLeft  = pos[0];
		style.posTop   = pos[1] - date_desc.offsetHeight + 39;
		style.width    = (date_desc.offsetWidth < 20) ? 150 : date_desc.offsetWidth + 1;
    }
  	var oldname = document.getElementById(obj).value;
  	// 遇到特殊字符；不请求后台，防止sql报错
  	var s = "";					
	for(var j = 0; j < oldname.length; j++){
		s = oldname.substring(j, j+1);
		if(s == ';'){
			return;
		}
	}
	oldname = getEncodeStr(oldname);
  	var hashvo = new ParameterSet();
  	hashvo.setValue("oldname", oldname);
  	var request = new Request({asynchronous:false,onSuccess:search_ok,functionId:'151211001130'},hashvo);
  }
  
  //将查询出的集合放入list
  function search_ok(outparameters){
  	var list = outparameters.getValue("pointList");
  	//处理特殊字符
  	for ( var i = 0; i < list.length; i++) {
		var temp = list[i];
		var dataname = temp.dataName;
		var datavalue = temp.dataValue;
		dataname = getDecodeStr(dataname);
		datavalue = getDecodeStr(datavalue);
		dataname = testStr(dataname);
		datavalue = testStr(datavalue);
		temp.dataName = dataname;
		temp.dataValue = datavalue;
		list[i] = temp;
	}
	//为select_box附加数据
  	AjaxBind.bind($('date_box'),list);	
  		
  }
  
   function TextFocus(){
  	var obj = document.getElementById("oldname");
  	if(obj.value!="搜索考勤点"){
  		return;
  	} else {
  		obj.value = "";
  		obj.style.color="#000";
  	}
  }
  
  function TextBlur(){
   	var obj = document.getElementById("oldname");
  	if(obj.value==""){
  		obj.value = "搜索考勤点";
  		obj.style.color="#ccc";
  	}
  }
  
  //处理' " ” “
  function testStr(str){
  	var b = "";
  	var s = "";
  	for(var j=0;j<str.length;j++){
		s = str.substring(j,j+1);
		if(s=="＇"){		
			b+="\'";
		} else if(s=="＂"){
			b+="\"";
		} else if(s=="”"){
			b+="\”";
		} else if(s=="“"){
			b+="\“";
		} else {
			b+=s;
		}
	}
	return b;
  }
  
    //双击事件    双击后根据选择的用户名执行操作
  function okSelect(){
  	var obj = document.getElementById("date_box");
  	var oldname,fname,temp;
  	var root = Global.selectedItem;
  	while(root.uid!="root"){	//判断节点是否为根节点
  		root = root.root();
  	}
  	root.collapseChildren();	//关闭所有子节点
	var list = root.childNodes;
    if(obj){
		for(var i=0;i<obj.options.length;i++){		
	    	if(obj.options[i].selected){
	            var t=obj.options[i].value;
	            if(t.indexOf("(")!=-1){
	            	temp = t.substring(0,t.indexOf("("));
	            	fname = t.substring(t.indexOf("("),t.length);
	            } else {
	            	temp = t;
	            	fname = "";
	            }
	            var arr = temp.split(":");					//将用户组名和用户名截取，存入数组
	            for ( var j = 0; j < arr.length; j++) {			
					for ( var x = 0; x < list.length; x++) {
						if(list[x].text==arr[j]){			//遍历数组，找到对应的树节点
							if(j==arr.length-1){			//如果是数组的最后一个，执行节点的action，否则展开节点
								oldname = list[x].text;
								list[x].select();
							} else {
								list[x].expand();
								list = list[x].childNodes;
							}
							break;
						}
					}
				}
	        }
	    }
	}
	// document.getElementById("oldname").value = oldname + fname;	//文本框显示选中的用户名
	document.getElementById("date_panel").style.display = "none";		//下拉文本框消失
  }
  
   //文本框单击事件    文本框有内容，且下拉框隐藏，则查询
  function show(){
  	var text = document.getElementById("oldname").value;
  	if(text.length == 0){
  		Element.hide('date_panel');
  	} else if(document.getElementById("date_panel").style.display != "none"){
  		Element.hide('date_panel');
  	} else {
  		bsearch('oldname');
  	}
  }
  
   function hide(){	//当tooltip存在时，不隐藏下拉列表
  	if(document.getElementById("WzTtDiV").style.visibility=="hidden"){
  		Element.hide('date_panel');
  	} else {
  		return false;
  	}
  }
  
   function outContent(){
  	document.getElementById("WzTtDiV").style.visibility="hidden";//点击时先隐藏漂浮模块
  	var obj = document.getElementById("date_box");
  	if(obj){
		for(var i=0;i<obj.options.length;i++){		
	    	if(obj.options[i].selected){		//判断路径是否显示完全
	            var t=obj.options[i].value;
	            var f = obj.options[i].text;
	            if(t!=f){
	            	config.FontSize='10pt';//提示信息中的字体大小
					Tip(t,STICKY,true);
	            }
	        }
	    }
	}
  }
</script>
</body>
