<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@page import="com.hrms.hjsj.sys.VersionControl"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.businessobject.sys.SysParamBo" %>
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

	String themes = "";
	if(userView!=null)
		themes= SysParamBo.getSysParamValue("THEMES",userView.getUserName());
	VersionControl ver = new VersionControl();
%>
<html>
<head>
	<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7">
<script LANGUAGE=javascript src="/js/xtree.js"></script>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<link href="/css/xtree.css" rel="stylesheet" type="text/css" ></link>
<hrms:themes />
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
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="../../../js/popcalendar.js"></script>
    <script language="javascript" src="../../../module/utils/js/template.js"></script>
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
<script type="text/javascript">
//新建或编辑
function addFieldClass(type)
{
  //type=1 new;type=2 edit;
  var subsys_id = document.khFieldForm.subsys_id.value;
  var currnode=Global.selectedItem; 
  var id=currnode.uid;
  if(id=="root"&&type=="2")
    {
       return;
    }
  var theurl = "/performance/kh_system/kh_field/add_field_class.do?b_init=init`type="+type+"`pointsetid="+id+"`subsys_id="+subsys_id;
  var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);

  if(window.showModalDialog){
    	 var return_vo= window.showModalDialog(iframe_url, arguments,
        "dialogWidth:420px; dialogHeight:220px;resizable:no;center:yes;scroll:no;status:no");
    	 addField_win_OK(return_vo);
    }else{
      var top= window.screen.availHeight-210>0?window.screen.availHeight-210:0;
      var left= window.screen.availWidth-420>0?window.screen.availWidth-420:0;
      top = top/2;
      left = left/2;
      window.open(iframe_url, "addFieldClassWin",
          "width=420,height=210,resizable=no,scroll=no,status=no,top="+top+",left="+left);
    }
}

//新建或编辑窗口关闭事件
function addField_win_OK(return_vo) {

    if(return_vo==null)
        return;
    var currnode=Global.selectedItem;
    var type=return_vo.type;
    var obj = new Object();
    obj.refresh = return_vo.refresh;
    if (obj.refresh == "2") {
        obj.pointsetname = getDecodeStr(return_vo.pointname);
        obj.pointsetid = return_vo.pointsetid;
        obj.type = return_vo.type;
        obj.subsys_id = return_vo.subsys_id;
        if (currnode.childNodes.length == 0) {
            var parent = currnode;
            if (currnode.uid != 'root')
                parent = currnode.parent
            parent.clearChildren();
            parent.loadChildren();
            parent.expand();
            for (var i = 0; i < parent.childNodes.length; i++) {
                if (parent.childNodes[i].uid == id) {
                    if (obj.type == '2') {
                        parent.childNodes[i].select();
                        selectedClass("treeItem-text-" + parent.childNodes[i].id);
                    }
                    else {
                        parent.childNodes[i].expand();
                        for (var j = 0; j < parent.childNodes[i].childNodes.length; j++) {
                            if (parent.childNodes[i].childNodes[j].uid == obj.pointsetid) {
                                parent.childNodes[i].childNodes[j].select();
                                selectedClass("treeItem-text-" + parent.childNodes[i].childNodes[j].id);
                            }
                        }
                    }
                } else {
                    if (id == 'root') {
                        parent.childNodes[i].select();
                        selectedClass("treeItem-text-" + parent.childNodes[i].id);
                    }
                }
            }
        }
        else {

            if (type == '2') {
                currnode = currnode.parent;
                currnode.clearChildren();
                currnode.loadChildren();
                currnode.expand();
                for (var i = 0; i < currnode.childNodes.length; i++) {
                    if (currnode.childNodes[i].uid == obj.pointsetid) {
                        currnode.childNodes[i].select();
                        currnode.childNodes[i].expand();
                        selectedClass("treeItem-text-" + currnode.childNodes[i].id);
                    }
                }
            } else {
                currnode.expand();
                currnode.clearChildren();
                currnode.loadChildren();
                currnode.expand();
                for (var i = 0; i < currnode.childNodes.length; i++) {
                    if (currnode.childNodes[i].uid == obj.pointsetid) {
                        currnode.childNodes[i].select();
                        selectedClass("treeItem-text-" + currnode.childNodes[i].id);
                    }
                }
            }
        }
        self.parent.mil_body.location = "/performance/kh_system/kh_field/init_kh_iframe.do?b_query=link&entery=1&pointsetid=" + obj.pointsetid + "&subsys_id=" + obj.subsys_id;
    }
}



function add_field_ok(outparameters)
{
     var currnode=Global.selectedItem;
     var pointsetid=outparameters.getValue("pointsetid");
     var subsys_id=outparameters.getValue("subsys_id");
     var pointname = outparameters.getValue("pointname");
     var type=outparameters.getValue("type");
     if(type=="1")
     {
        var imgurl = "/images/open.png";	 
    	 if(currnode.load)
     	 {
     	    var tmp = new xtreeItem(pointsetid,pointname,"/performance/kh_system/kh_field/init_kh_iframe.do?b_query=link&entery=1&pointsetid="+pointsetid+"&subsys_id="+subsys_id,"mil_body",pointname,imgurl,"/servlet/performance/KhFieldTree?pointsetid="+pointsetid+"&subsys_id="+subsys_id);
           currnode.add(tmp);
         }
         else
         	currnode.expand();
      }
      else
      {
       currnode.setText(pointname);
      }
}
function deleteFieldClass()
{
     var currnode=Global.selectedItem;
	 if(currnode.uid=="root")
	    return;
     if(!confirm("确认删除指标分类 ["+currnode.text+"] 及其子分类和指标吗?"))
         return;      
     var pointsetid=currnode.uid;
     var subsys_id = document.khFieldForm.subsys_id.value;
     
     var hashvo=new ParameterSet();
     hashvo.setValue("pointsetid",pointsetid);
     hashvo.setValue("subsys_id",subsys_id);
     var request=new Request({asynchronous:false,onSuccess:delete_ok,functionId:'9021001005'},hashvo);
}
function delete_ok(outparameters){
     var subsys_id=outparameters.getValue("subsys_id");
     var msg=outparameters.getValue("msg");
     if(msg =='9')
     {
        alert("您没有该指标分类的编辑权限！");
        return;
     }
     if(msg !='1')
     {
        alert("该指标分类下有已经被使用的指标，不能删除！");
        return;
     }
     var currnode=Global.selectedItem;
     var preitem=currnode.getPreviousSibling();
     if(preitem!=null)
     {
         currnode.remove();
         preitem.select();
         selectedClass("treeItem-text-"+preitem.id);
         self.parent.mil_body.location="/performance/kh_system/kh_field/init_kh_iframe.do?b_query=link&entery=1&pointsetid="+preitem.uid+"&subsys_id="+subsys_id;
     }else
     {
       var parent=currnode.parent;
       current.remove();
       parent.select();
       selectedClass("treeItem-text-"+parent.id);
       var uid="-1";
       if(parent.uid!='root')
          uid=parent.uid;
       self.parent.mil_body.location="/performance/kh_system/kh_field/init_kh_iframe.do?b_query=link&entery=1&pointsetid="+uid+"&subsys_id="+subsys_id;
     }
}
function fieldclass_sort()
{
var subsys_id = document.khFieldForm.subsys_id.value;
  var currnode=Global.selectedItem; 
  var id=currnode.uid;
  var hashvo=new ParameterSet();
  hashvo.setValue("pointsetid",id);
  hashvo.setValue("subsys_id",subsys_id);
  var request=new Request({asynchronous:false,onSuccess:Change_sort,functionId:'9021001043'},hashvo);   
}
function change_sort_ok(outparameters)
{
   var currnode=Global.selectedItem; 
  // if(currnode.parent!=null)
       //currnode=currnode.parent;
   currnode.select();
   currnode.expand();
   currnode.clearChildren();
   currnode.loadChildren();
   currnode.expand();
}
//调整顺序功能
function Change_sort(outparameters)
{
 var id=outparameters.getValue("pointsetid");
 var subsys_id=outparameters.getValue("subsys_id");
 var msg=outparameters.getValue("msg");
 if(msg=='0')
 {
    alert("所选指标分类没有下级节点或者只有一个下级节点，不能进行排序操作！");
    return;
 }
  var theurl = "/performance/kh_system/kh_field/sort_field_class.do?b_search=search`pointsetid="+id+"`subsys_id="+subsys_id;
  var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
  if(window.showModalDialog){
      var return_vo= window.showModalDialog(iframe_url, arguments,
          "dialogWidth:360px; dialogHeight:355px;resizable:no;center:yes;scroll:no;status:no");
      Change_sort_OK(return_vo);
  }else{
	  var top= window.screen.availHeight-320>0?window.screen.availHeight-320:0;
      var left= window.screen.availWidth-510>0?window.screen.availWidth-510:0;
      top = top/2;
      left = left/2;
      window.open(iframe_url, arguments, 
    		  "top="+top+",left="+left+",width=370; height=405;resizable=no;center=yes;scroll=no;status=no");

  }
}
//调整顺序窗口关闭事件
function Change_sort_OK(return_vo) {

    if(return_vo==null)
        return;
    var vo = new Object();
    vo.ids = return_vo.ids;
    vo.pointsetid = return_vo.pointsetid;
    vo.subsys_id = return_vo.subsys_id;
    vo.sorttype=return_vo.sorttype
    var hashvo=new ParameterSet();
    hashvo.setValue("newsortvo",vo);
    var request=new Request({asynchronous:false,onSuccess:change_sort_ok,functionId:'9021001007'},hashvo);

}

//标准标度设置
function gradeConfig()
{
    var theurl = "/performance/kh_system/kh_field/init_grade_template.do?b_init=init";
    var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
    if(window.showModalDialog) {
        window.showModalDialog(iframe_url, "gradeConfigWin",
            "dialogWidth:515px; dialogHeight:320px;resizable:no;center:yes;scroll:yes;status:no");
    }else{
        var top= window.screen.availHeight-320>0?window.screen.availHeight-320:0;
        var left= window.screen.availWidth-510>0?window.screen.availWidth-510:0;
        top = top/2;
        left = left/2;
        window.open(iframe_url, "gradeConfigWin",
            "top="+top+",left="+left+",width=510,height=320,resizable=no,center=yes,scroll=no,status=no");
    }
}
function exportD(subsys)
{
    var hashvo=new ParameterSet();
    hashvo.setValue("subsys_id",subsys);
    hashvo.setValue("export_type","all");
	var request=new Request({asynchronous:true,onSuccess:export_ok,functionId:'9021001019'},hashvo);			

   
}
function export_ok(outparamters)
{
	// 没有可以到处的指标，应给出提示 lium
	var error = outparamters.getValue("error");
	if (error === "true") {
		alert(outparamters.getValue("errorInfo"));
		return;
	}
	
	var outName=outparamters.getValue("outName");
	if(outName=='1')
	{
	   alert("所选节点下没有指标和指标分类，不能导出！");
	   return;
	}
    //zhangh 2020-4-7 素质指标,下载改为使用VFS
    outName = decode(outName);
    var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName);
}

/*
function importD()
{
    var theurl = "/performance/kh_system/kh_field/kh_field_tree.do?br_import1=import`count=1";
    var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
    var return_vo= window.showModalDialog(iframe_url, arguments, 
        "dialogWidth:500px; dialogHeight:500px;resizable:no;center:yes;scroll:yes;status:yes");
    if(return_vo!=null)
    {
       var obj= new Object();
       obj.flag=return_vo.flag;
       (obj.flag=="1")
       {
          self.parent.location="/performance/kh_system/kh_field/kh_field_tree.do?b_query=link&subsys_id="+khFieldForm.subsys_id.value;
         // parent.mil_menu.location="/performance/kh_system/kh_field/kh_field_tree.do?b_query=link&subsys_id="+khFieldForm.subsys_id.value;
       }
    }
    
}
*/

function importD()
{
	var currnode=Global.selectedItem; 
    var id=currnode.uid;
	khFieldForm.action="/performance/kh_system/kh_field/kh_field_tree.do?br_import1=import&count=1&pointsetid="+id;
	khFieldForm.target="mil_body";
   	khFieldForm.submit();	  	   
}

function initFirst()
{
     initDocument();
    if(root.getFirstChild())
    {
       root.getFirstChild().select();
       selectedClass("treeItem-text-"+root.getFirstChild().id);
    }
}
function exportCurrent(subsys)
{
    var currnode=Global.selectedItem; 
    var id=currnode.uid;
    if(id=='root')
    {
      exportD(subsys);
    }
    else
    {
       var hashvo=new ParameterSet();
       hashvo.setValue("subsys_id",subsys);
       hashvo.setValue("export_type","current");
       hashvo.setValue("pointsetid",id);
	   var request=new Request({asynchronous:true,onSuccess:export_ok,functionId:'9021001019'},hashvo);
    }			
   
}
function addorgpoint()
{	
	var orgpoint="${khFieldForm.orgpoint}";
	var khpid="${khFieldForm.khpid}";
	var khpname="${khFieldForm.khpname}";
	if(orgpoint.length>0){
			var thecodeurl ="/performance/kh_system/kh_field/init_grade_template.do?b_query=query"; 
			var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl; 
			var return_vo= window.showModalDialog(iframe_url, "", 
            "dialogWidth:"+screen.width/1+"px; dialogHeight:"+screen.height/1+"px;resizable:yes;center:yes;scroll:yes;status:no");
	}else{
		var theurl = "/performance/kh_system/kh_field/init_grade_template.do?b_add=add`aflag=0";
   		var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
    	var return_vo= window.showModalDialog(iframe_url, arguments, 
        "dialogWidth:520px; dialogHeight:330px;resizable:no;center:yes;scroll:yes;status:no");
   	 	if(return_vo=="ok"){
         	var thecodeurl ="/performance/kh_system/kh_field/init_grade_template.do?b_query=query"; 
			var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl; 
			var return_vo= window.showModalDialog(iframe_url, "", 
          	"dialogWidth:"+screen.width/1+"px; dialogHeight:"+screen.height/1+"px;resizable:yes;center:yes;scroll:yes;status:no");
        }
	}
 	
    
   
}

var themes = "<%=themes %>";
var bgColor="";
var borderColors="";
if(themes=="default"){
    Ext.util.CSS.createStyleSheet(".x-btn-inner-default-toolbar-small{color:#414141 !important;}");
}
bgColor="#F9F9F9";
borderColors = "#C5C5C5";

Ext.util.CSS.createStyleSheet(".x-btn-default-toolbar-small-mc{border:1px solid "+borderColors+";background-color:"+bgColor+" !important;}","menu_ms_bg");
Ext.util.CSS.createStyleSheet(".x-btn-default-toolbar-small-ml{display:none;background:none;}","");
Ext.util.CSS.createStyleSheet(".x-btn-default-toolbar-small-mr{display:none;background:none;}","");
Ext.util.CSS.createStyleSheet(".x-btn-default-toolbar-small{border-color:"+borderColors+" !important;background-color:"+bgColor+" !important;}","menu1");
Ext.util.CSS.createStyleSheet(".x-btn-wrap-default-toolbar-small{background-color:"+bgColor+" !important;}","");
Ext.util.CSS.createStyleSheet(".x-btn-inner-default-toolbar-small{padding:2px 4px !important;}","");
Ext.util.CSS.createStyleSheet("#menubar .x-btn-over .x-btn-default-toolbar-small-br{background-image:none !important;}","");
Ext.util.CSS.createStyleSheet("#menubar .x-btn-over .x-btn-default-toolbar-small-bl{background-image:none !important;}","");
Ext.util.CSS.createStyleSheet("#menubar .x-btn-over .x-btn-default-toolbar-small-tr{background-image:none !important;}","");
Ext.util.CSS.createStyleSheet("#menubar .x-btn-over .x-btn-default-toolbar-small-tl{background-image:none !important;}","");
Ext.util.CSS.createStyleSheet("#menubar .x-frame-tr{background-image:none !important;}","");
Ext.util.CSS.createStyleSheet("#menubar .x-frame-bl{background-image:none !important;}","");
Ext.util.CSS.createStyleSheet("#menubar .x-frame-tl{background-image:none !important;}","");
Ext.util.CSS.createStyleSheet("#menubar .x-frame-br{background-image:none !important;}","");
Ext.util.CSS.createStyleSheet("#menubar .x-frame-tc{background-image:none !important;}","");
Ext.onReady(function(){
    var menu1 = new Ext.menu.Menu({
        allowOtherMenus: false,
        items: [
            new Ext.menu.Item({
                text:'导入',//kh.field.inport,
                icon:"/images/prop_ps.gif",
                handler: function(){
                    importD();
                }
            }),
            new Ext.menu.Item({
                text:'批量导出',//kh.field.export,
                icon:"/images/export.gif",
                handler:function(){
                    exportD('${khFieldForm.subsys_id}');
                }
            }),
            new Ext.menu.Item({
                text: '单个导出',//lable.kh.template.importcurrent,
                icon:'/images/export.gif',
                handler:function(){
                    exportCurrent('${khFieldForm.subsys_id}');
                }
            })
        ]
    });
    var menu2 = new Ext.menu.Menu({
        allowOtherMenus: false,
        items: [
            new Ext.menu.Item({
                text:'新建',//kh.field.new,
                icon:"/images/add.gif",
                handler: function(){
                    addFieldClass('1');
                }
            }),
            new Ext.menu.Item({
                text:'修改',//label.kh.edit,
                icon:"/images/edit.gif",
                handler:function(){
                    addFieldClass('2');
                }
            }),
            new Ext.menu.Item({
                text: '删除',//kh.field.delete,
                icon:'/images/del.gif',
                handler:function(){
                    deleteFieldClass();
                }
            }),
            new Ext.menu.Item({
                text: '调整顺序',//kh.field.sort,
                icon:'/images/add_del.gif',
                handler:function(){
                    fieldclass_sort();
                }
            })
        ]
    });
    var menu3 = new Ext.menu.Menu({
        allowOtherMenus: false,
        items: [
            new Ext.menu.Item({
                text:'标准标度',//kh.field.bzbd,
                icon:"/images/img_o.gif",
                handler: function(){
                    gradeConfig();
                }
            })
        ]
    });

    var toolbar = Ext.create("Ext.Toolbar", {
        renderTo: "menubar",
        width: 200,
        margin:'0 -2 -4 0',
        border:false,
        items:[{
            text: '文件',//kh.field.file,
            menu: menu1,
            height:25,
            width:50
        },{
            text: '编辑',//kh.field.edit,
            menu: menu2,
            height:25,
            width:50
        },{
            text: '设置',//kh.field.config,
            menu: menu3,
            height:25,
            width:50
        }]
    });
});
</script>
 </head>
  <BODY>
<html:form action="/performance/kh_system/kh_field/kh_field_tree">

<div style="margin-top: 10px;margin-left: 2px;" id="menubar">

 </div>
   <table align="left" class="mainbackground" border="0" cellpadding="0" cellspacing="0">
		 	            
         <tr>
           <td align="left" style="margin: 0;padding: 0;"> 
            <div id="treemenu"> 
             <SCRIPT LANGUAGE="javascript">                 
               <bean:write name="khFieldForm" property="tree" filter="false"/>
             </SCRIPT>
             </div>             
           </td>
           </tr>           
    </table>  
    <html:hidden name="khFieldForm" property="subsys_id"/>
</html:form>
<script language="javascript">
  initFirst();
</script>
</body>
</html>
