<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
	}
%>
<style type="text/css">
   .toldname{
	width:290px;
	border: 1px solid #C4D8EE;
	color:#ccc;
	}
   .toldname input{
	width:288px;
	border: 0px;
	color:#ccc;
	}
	#treemenu {  
	height: 300px;
	overflow: auto;
	border: 1px solid;
	width:290px;
	}
 </style>
<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<script language="javascript">
	function getuser()
	{
     var selectflag='<bean:write name="logonUserForm" property="treeSelectType" filter="false"/>';
     if(selectflag=='1'){
     	 var currnode=Global.selectedItem; 
	     var iconurl=currnode.icon;    
		 var selectedValue = root.getSelected();
		 sv = selectedValue.split(","); 
		 if(selectedValue == ''&&report!="1"){
		 	alert('<bean:message key="error.notselect.object"/>');
	     	return;	
		 }
		 for(var i = 0; i< sv.length ; i++){
		 	var temp = sv[i];
		 	if(temp.indexOf('@')!=-1){
		 		alert('<bean:message key="error.exist.group"/>');
	     		return;	
		 	}
		 }
		 var thevo=new Object();
		 thevo.content=root.getSelected();
		 thevo.title=root.getSelectedTitle();
		 window.returnValue=thevo;
		 parent.vo = thevo;
		 closeWin();
     }else{
     	 var currnode=Global.selectedItem; 
	     var iconurl=currnode.icon;    
	/**  if(!(iconurl=="/images/not_admin.gif"||iconurl=="/images/admin.gif"))
	     {
	     	alert('<bean:message key="error.notselect.object"/>');
	     	return;	
	     }*/
		 var thevo=new Object();
		 thevo.content=root.getSelected();
		 thevo.title=root.getSelectedTitle();
		 window.returnValue=thevo;
		 parent.vo = thevo;
		 closeWin();
     }
    
	}
	function closeWin(){
		if(parent.Ext&&parent.Ext.getCmp("searchPersonnelWin")){
			parent.Ext.getCmp("searchPersonnelWin").close();
		}else{
			window.close();
		}
	}
    Global.defaultInput='<bean:write name="logonUserForm" property="treeSelectType" filter="false"/>';
    Global.showroot=false;
    Global.defaultradiolevel=2;//bug 36664 用户组不需要显示单选框
    
    function bsearch(obj){
  		if($F('oldname')==""){
  		Element.hide('date_panel');
  		return false;
  		}
  		date_desc = document.getElementById(obj);
  		Element.show('date_panel');
  		var pos=getAbsPosition(date_desc);
		with($('date_panel')){				//定位下拉文本框位置
    		style.position="absolute";
			style.posLeft=pos[0];
			style.posTop=pos[1]-date_desc.offsetHeight+39;
			style.width=(date_desc.offsetWidth<20)?150:date_desc.offsetWidth+1;
    	}
  		var oldname = document.getElementById(obj).value;
  		var s="";					//处理特殊字符
		var b = "";
		for(var j=0;j<oldname.length;j++){
			s = oldname.substring(j,j+1);
			if(s=='#'){
				b+="nbspa";
			} else if(s=="；"){
				b+="quanjiao;hao";
			} else {
				b+=s;
			}	
		}
		oldname = b;
		oldname = getEncodeStr(oldname);
  		var hashvo = new ParameterSet();
  		hashvo.setValue("oldname", oldname);
  		//查询标识，为1时，节点名称优先顺序为A0101>fullname>username
  		hashvo.setValue("isfilter", "1");
  		var request = new Request({asynchronous:false,onSuccess:search_ok,functionId:'1010010091'},hashvo);
  	}
  //将查询出的用户名集合放入list
  function search_ok(outparameters){
  	var list = outparameters.getValue("namelist");
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
  	AjaxBind.bind($('date_box'),list);//为select_box附加数据
  }
  
	//双击事件    双击后根据选择的用户名执行操作,运行时间O(m+n)
	function okSelect() {
	    var oldname;
	    // 找到根节点
	    var root = Global.selectedItem;
	    while (root.uid != "root") {
	        root = root.root();
	    }
	    // 关闭所有子节点
	    root.collapseChildren();
	    // 关闭复选框
	    var checkitems;
	    var defaultinput = Global.defaultInput;
	    if (defaultinput == "1") {
	        checkitems = document.getElementsByName("treeItem-check"); //多选框
	        for (var i = 0; i < checkitems.length; i++) { //取消所有多选框
	            if (checkitems[i].checked == true) {
	                checkitems[i].checked = false;
	            }
	        }
	    } else if (defaultinput == "2") {
	        checkitems = document.getElementsByName("treeItem-radio"); //单选按钮
	        for (var i = 0; i < checkitems.length; i++) { //取消单选框
	            if (checkitems[i].checked == true) {
	                checkitems[i].checked = false;
	                break;
	            }
	        }
	    }
	    // 下拉框中列表
	    var obj = document.getElementById("date_box");
	    if (obj) {
	        // 找到选中的数据
	        for (var i = 0; i < obj.options.length; i++) {
	            if (obj.options[i].selected) {
	                var value = obj.options[i].value;
	                value = reStr(value);
	                var list = value.split("\\\\");
	                var oldname = list[list.length - 1];
	                // 文本框显示选中的用户名
	                document.getElementById("oldname").value = oldname;
	                // 下拉文本框消失
	                document.getElementById("date_panel").style.display = "none";
	                // 数据格式为UserName(FullName)或UserName(FullName,A0101)的需要分解处理
	                if (oldname.length == oldname.indexOf(")") + 1) {
	                    // 截取替换
	                    oldname = oldname.substring(0, oldname.indexOf("("));
	                    list[list.length - 1] = oldname; 
	                }
	                // 展开节点
	                openNodeByPath(root.childNodes, list);
	                return;
	            }
	        }
	    }
	}

	/**
	  * 展开节点
	  * childList 子节点
	  * selectList 经过处理的用户双击选中数据
	  */
	function openNodeByPath(childList, selectList) {
	    // 安全机制
	    if (selectList.length == 0) return;
	    var value = selectList[0];
	    //移除最前一个元素并返回该元素值，数组中元素自动前移
	    selectList.shift();
	    var uid;
	    for (var i = 0; i < childList.length; i++) { //展开节点
	        uid = childList[i].uid;
	        // 去掉业务组的@符号
	        uid = uid.substring(uid.indexOf("@") + 1, uid.length);
	        if (uid == value) {
	            // 没有下级可查节点
	            if (selectList.length == 0) {
	                //选中
	                childList[i].select();
	                // 勾选复选框
	                var checkitems;
	                var defaultinput = Global.defaultInput;
	                if (defaultinput == "1") { //多选框
	                    checkitems = document.getElementsByName("treeItem-check");
	                } else if (defaultinput == "2") { //单选按钮
	                    checkitems = document.getElementsByName("treeItem-radio");
	                }
	                for (var i = 0; i < checkitems.length; i++) {
	                    if (uid == checkitems[i].value) {
	                    	// 勾选复选框,完成操作
	                        checkitems[i].checked = true;
	                        return;
	                    }
	                }
	            } else {
	                // 展开子节点
	                childList[i].expand();
	                openNodeByPath(childList[i].childNodes, selectList);
	            }
	            return;
	        }
	    }
	}
  
  //文本框单击事件    文本框有内容，且下拉框隐藏，则查询
  function show(){
  	var text = document.getElementById("oldname").value;
  	if(text.length==0){
  		Element.hide('date_panel');
  	} else if(document.getElementById("date_panel").style.display!="none"){
  		Element.hide('date_panel');
  	} else {
  		bsearch('oldname');
  	}
  }
  
  function TextFocus(){
  	var obj = document.getElementById("oldname");
  	if(obj.value!="<bean:message key="search.user_group.text"/>"){
  		return;
  	} else {
  		obj.value = "";
  		obj.style.color="#000";
  	}
  }
  function TextBlur(){
   	var obj = document.getElementById("oldname");
  	if(obj.value==""){
  		obj.value = "<bean:message key="search.user_group.text"/>";
  		obj.style.color="#ccc";
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
  function hide(obj){	//当tooltip存在时，不隐藏下拉列表
  	if(document.getElementById("WzTtDiV").style.visibility=="hidden"&&"SELECT"!=obj.tagName){
  		Element.hide('date_panel');
  	} else {
  		return false;
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
  //处理~ | & ^
  function reStr(str){
  	var b = "";
  	var s = "";
  	for(var j=0;j<str.length;j++){
		s = str.substring(j,j+1);
		if(s=="~"){		
			b+="～";
		} else if(s=="|"){
			b+="l";
		} else if(s=="&"){
			b+="＆";
		} else if(s=="^"){
			b+="︿";
		} else {
			b+=s;
		}
	}
	return b;
  }
</script>
   <hrms:themes></hrms:themes>
<body>
<script language="javascript" type="text/javascript" src="/js/wz_tooltip.js"></script>
<html:form action="/system/logonuser/select_user_obj"> 
   <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" >
   		<tr>
   			<td>
   				<input type="hidden" id="usrlist" name="usrlist">
   				<div class="toldname common_border_color">
		 			<input type="text" id="oldname" name="name" style="line-height: 16px" onkeyup="bsearch('oldname')" onclick="show();" value="<bean:message key="search.user_group.text"/>"
		 			onfocus="TextFocus()" onblur="TextBlur()" autocomplete="off"></input><!-- 解决浏览器自带的历史记录下拉框 -->
   				</div>
	 			<div id="date_panel" style="display: none;">
					<select id="date_box" name="namelist" multiple="multiple" style="width: 237" size="6" ondblclick="okSelect();"
					 onmouseout="hide(this);tt_HideInit();" onclick="outContent();">	<!-- 调用wz_tooltip控件 -->
						
					</select>
				</div>
	 		</td>
   		</tr>   
         <tr>
           <td align="left" style="padding-top:4px;"> 
            <div id="treemenu" class="complex_border_color"> 
             <SCRIPT LANGUAGE=javascript>      
             	Global.checkvalue='<bean:write name="logonUserForm" property="reportUser" filter="false"/>'; 	      
               <bean:write name="logonUserForm" property="userTree" filter="false"/>
               var groupid1 = '<bean:write name="logonUserForm" property="groupid1" filter="false"/>';                              
               var temps=groupid1.split(",");
               var report = '<bean:write name="logonUserForm" property="report" filter="false"/>'; 
               if(report=="1"){
               		initTreeNode();
               }              
			function initTreeNode()
			{
				var obj=root;
				for(var i=0; i<temps.length;i++)
				{
					obj.expand();
					for(var j=0;j<obj.childNodes.length;j++)
					{
						if(obj.childNodes[j].text==temps[i])
						{
							obj.childNodes[j].expand();
						}
					}
				}				
			}
             </SCRIPT>
             </div>             
           </td>
           </tr> 
         <tr>
            <td align="center" colspan="2" height="35px;">
         	<html:button styleClass="mybutton" property="b_save" onclick="getuser();">
            		<bean:message key="button.ok"/>
	 	    </html:button>
         	<html:button styleClass="mybutton" property="br_return" onclick="closeWin();">
            		<bean:message key="button.close"/>
	 	    </html:button>            
            </td>         
         </tr>                         
   </table>

</html:form>
</body>