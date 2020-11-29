<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.businessobject.sys.SysParamBo" %>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
    String themes = "";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";

        themes= SysParamBo.getSysParamValue("THEMES",userView.getUserName());
	}
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
//新增模板分类
function addTemplateSet(type)
{
//type=0新增，=1修改
    var subsys_id = document.khTemplateForm.subsys_id.value;
    var currnode=Global.selectedItem;
    var id=currnode.uid;
    var ss = id.split("#")[1];//=0模板分类 =1是模板
    if(ss=="1"&&type=="0")
    {
      alert("请选择模板分类,否则无法创建模板分类!");
      return;
    }
    var theurl = "/performance/kh_system/kh_template/add_template_set.do?b_init=init`type="+type+"`templatesetid="+id.split("#")[0]+"`subsys_id="+subsys_id;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
   /*  if(window.showModalDialog){
    	var return_vo= window.showModalDialog(iframe_url, arguments, 
        "dialogWidth:510px; dialogHeight:280px;resizable:no;center:yes;scroll:no;status:no");
        addTemplateSet_Ok(return_vo);
    }else{
        window.open(iframe_url, arguments, "width=510; height=280;resizable=no;center=yes;scroll=no;status=no");
    } */
    
    var config = {
        width:510,
        height:280,
        type:'2',
        dialogArguments:arguments
    }
    if(!window.showModalDialog){
        window.dialogArguments = arguments;
    }
    modalDialog.showModalDialogs(iframe_url,'addTemplateSet_window',config, addTemplateSet_Ok);
}

function addTemplateSet_Ok(return_vo) {
    if(return_vo)
    {
        var currnode=Global.selectedItem;
        var id=currnode.uid;
        var obj = new Object();
        obj.refresh = return_vo.refresh;
        if(obj.refresh == "2")
        {

            obj.fname=getDecodeStr(return_vo.fname);
            obj.templatesetid=return_vo.templatesetid;
            obj.type=return_vo.type;
            obj.subsys_id=return_vo.subsys_id;
            if(currnode.childNodes.length==0)
            {
                var parent=currnode;
                if(currnode.uid!='root')
                    parent=currnode.parent
                parent.clearChildren();
                parent.loadChildren();
                parent.expand();
                for(var i=0;i<parent.childNodes.length;i++)
                {
                    if(parent.childNodes[i].uid==id)
                    {
                        if(obj.type=='0')
                        {
                            parent.childNodes[i].select();
                            selectedClass("treeItem-text-"+parent.childNodes[i].id);
                        }
                        else
                        {
                            parent.childNodes[i].expand();
                            for(var j=0;j<parent.childNodes[i].childNodes.length;j++)
                            {
                                if(parent.childNodes[i].childNodes[j].uid==obj.pointsetid)
                                {
                                    parent.childNodes[i].childNodes[j].select();
                                    selectedClass("treeItem-text-"+parent.childNodes[i].childNodes[j].id);
                                }
                            }
                        }
                    }else{
                        if(id=='root'){
                            parent.childNodes[i].select();
                            selectedClass("treeItem-text-"+parent.childNodes[i].id);
                        }
                    }
                }
            }
            else
            {
                if(return_vo.type=='1')
                {
                    currnode=currnode.parent;
                    currnode.clearChildren();
                    currnode.loadChildren();
                    currnode.expand();
                    for(var i=0;i<currnode.childNodes.length;i++)
                    {
                        if(currnode.childNodes[i].uid==obj.pointsetid)
                        {
                            currnode.childNodes[i].select();
                            currnode.childNodes[i].expand();
                            selectedClass("treeItem-text-"+currnode.childNodes[i].id);
                        }
                    }
                }else
                {
                    currnode.clearChildren();
                    currnode.loadChildren();
                    currnode.expand();
                    for(var i=0;i<currnode.childNodes.length;i++)
                    {
                        if(currnode.childNodes[i].uid==obj.pointsetid)
                        {
                            currnode.childNodes[i].select();
                            selectedClass("treeItem-text-"+currnode.childNodes[i].id);
                        }
                    }
                }
            }
        }
    }

}

function add_ok(outparameter)
{
  var id = outparameter.getValue("id");
  var name= getDecodeStr(outparameter.getValue("name"));
  var subsys_id = outparameter.getValue("subsys_id");
  var b=outparameter.getValue("b0110");
  var type=outparameter.getValue("type");
  var method = document.getElementById("method").value;
  var isVisible=document.getElementById("isVisible").value;
  var persionControl=document.getElementById("persionControl").value;
   var currnode=Global.selectedItem; 
   if(type=="0")
     {
        var imgurl = "/images/open1.png";	 
    	 if(currnode.load)
     	 {

     	    var tmp = new xtreeItem(id+"#0",name,"/performance/kh_system/kh_template/init_kh_item.do?b_query=link&templateid=-1&subsys_id="+subsys_id,"mil_body",name,imgurl,"/servlet/performance/KhTemplateTree?templatesetid="+id+"&subsys_id="+subsys_id+"&b0110="+b+"&isVisible="+isVisible+"&method="+method+"&persionControl="+persionControl);
           currnode.add(tmp);
           tmp.select();
           selectedClass("treeItem-text-"+tmp.id);
         }
         else
         {
         	currnode.expand();
         	for(var i=0;i<currnode.childNodes.length;i++)
         	{
         	    if(currnode.childNodes[i].uid==id+"#1")
         	    {
         	         currnode.childNodes[i].select();
         	         selectedClass("treeItem-text-"+currnode.childNodes[i].id);
         	    }
         	}
         }
      }
      else
      {
       currnode.setText(name);
      }
        self.parent.mil_body.location="/performance/kh_system/kh_template/init_kh_item.do?b_query=link&templateid=-1&subsys_id="+subsys_id;
}
//新建模板
function addTemplate(type)
{
//type=0新增，=1修改 3 另存
    var subsys_id = document.khTemplateForm.subsys_id.value;
    var currnode=Global.selectedItem; 
    var id=currnode.uid;
    if(id=='root')
    {
      alert("请选择模板分类建模板，否则无法创建");
      return;
    }
    var tt=id.substring(0,id.indexOf("#"));
    var ss=id.substring((id.indexOf("#")+1));//=0模板分类 =1是模板
    if(ss=="1"&&type=="0")
    {
      alert("请选择模板分类建模板，否则无法创建");
      return;
    }
    //9021001044
    if(ss=='1'&&type=='1')
    {
        var vo=new ParameterSet();
        vo.setValue("type",type);
        vo.setValue("templatesetid",tt);
        vo.setValue("subsys_id",subsys_id);
        var request=new Request({asynchronous:false,onSuccess:checkOK,functionId:'9021001044'},vo);
        return;
    }
    if(type=='3'&&ss=='0')
    {
       alert("请选择模板进行另存操作！");
       return;
    }
    var theurl ="/performance/kh_system/kh_template/add_or_edit_template.do?b_init=init`type="+type+"`templatesetid="+tt+"`subsys_id="+subsys_id;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);

    /* if(window.showModalDialog){
        var return_vo= window.showModalDialog(iframe_url, arguments,
            "dialogWidth:510px; dialogHeight:270px;resizable:no;center:yes;scroll:no;status:no");
        addTemplate_Ok(return_vo);
    }else{
        window.open(iframe_url, arguments, "width=510; height=270;resizable=no;center=yes;scroll=no;status=no");
    } */
    
    var config = {
        width:510,
        height:270,
        type:'2',
        dialogArguments:arguments
    }
    if(!window.showModalDialog){
        window.dialogArguments = arguments;
    }
    modalDialog.showModalDialogs(iframe_url,'addTemplate_Ok_window',config, addTemplate_Ok);

}


function addTemplate_Ok(return_vo) {

    if(return_vo)
    {
        var type=return_vo.type;

        var subsys_id = document.khTemplateForm.subsys_id.value;
        var currnode=Global.selectedItem;
        var id=currnode.uid;

        var tt=id.substring(0,id.indexOf("#"));
        var ss=id.substring((id.indexOf("#")+1));//=0模板分类 =1是模板

        var obj = new Object();
        obj.refresh=return_vo.refresh;
        if(obj.refresh == '1')
        {
            return;
        }
        obj.id=return_vo.id;
        obj.name=return_vo.name;
        obj.topscore = return_vo.topscore;
        obj.status=return_vo.status;
        obj.setid=return_vo.setid;
        obj.parentsetid=return_vo.parentsetid;
        var hashvo=new ParameterSet();
        hashvo.setValue("opt",'2');
        hashvo.setValue("templatename",obj.name);
        hashvo.setValue("templateid",obj.id);
        hashvo.setValue("templatesetid",tt);
        hashvo.setValue("subsys_id",subsys_id);
        hashvo.setValue("type",type);
        hashvo.setValue("topscore", obj.topscore);
        hashvo.setValue("status",obj.status);
        hashvo.setValue("parentsetid",obj.parentsetid);
        if(type=='3')
        {
            var request=new Request({asynchronous:false,onSuccess:check_ok,functionId:'9021001031'},hashvo);
        }
        else
        {
            var request=new Request({asynchronous:false,onSuccess:check_ok,functionId:'9021001026'},hashvo);
        }
    }
}


function checkOK(outparameters)
{
  var msg=outparameters.getValue("msg");
  //职称评分环节标识，=1 代表模板已经使用，不允许修改
  var msg2=outparameters.getValue("msg2");
  if(msg!="1" && msg2=="1"){
    msg = "1";
  }
 //if(msg=='1')
 //{
    // alert("该模板已经使用，不能修改！");
     //return;
 // }
   var tt=outparameters.getValue("tt");
   var type=outparameters.getValue("type");
   var subsys_id=outparameters.getValue("subsys_id");
     var theurl ="/performance/kh_system/kh_template/add_or_edit_template.do?b_init=init`type="+type+"`templatesetid="+tt+"`subsys_id="+subsys_id+"`templateUsed="+msg;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);

    /* if(window.showModalDialog){
        var return_vo= window.showModalDialog(iframe_url, arguments,
            "dialogWidth:510px; dialogHeight:250px;resizable:no;center:yes;scroll:no;status:no");
        addTemplate_Ok(return_vo);
    }else{
        window.open(iframe_url, arguments, "width=510; height=250;resizable=no;center=yes;scroll=no;status=no");
    } */
    
    var config = {
        width:510,
        height:250,
        type:'2',
        dialogArguments:arguments
    }
    if(!window.showModalDialog){
        window.dialogArguments = arguments;
    }
    modalDialog.showModalDialogs(iframe_url,'checkOK_window',config, addTemplate_Ok);
}
function check_ok(outparameter)
{
   var currnode=Global.selectedItem; 
   var id = outparameter.getValue("id");
   var name=getDecodeStr(outparameter.getValue("name"));
   var subsys_id = outparameter.getValue("subsys_id");
   var type = outparameter.getValue("type");
   var parentsetid = outparameter.getValue("parentsetid");
    var method = "";
    if(document.getElementById("method")){
        method=document.getElementById("method").value;
    }else{
        method=document.getElementsByName("method")[0].value;
    }
    var isVisible = "";
    if(document.getElementById("isVisible")){
        isVisible=document.getElementById("isVisible").value;
    }else{
        isVisible=document.getElementsByName("isVisible")[0].value;
    }
    var persionControl = "";
    if(document.getElementById("persionControl")){
        persionControl=document.getElementById("persionControl").value;
    }else{
        persionControl=document.getElementsByName("persionControl")[0].value;
    }
   if(type=='3')
   {
        if(currnode.parent.uid==parentsetid+"#0")
        {
            var parent=currnode.parent;
	        parent.clearChildren();
	        parent.loadChildren();
	        parent.expand();
	        for(var i=0;i<parent.childNodes.length;i++)
	        {
	           if(parent.childNodes[i].uid==id+"#1")
	           {
	              parent.childNodes[i].select();
	              selectedClass("treeItem-text-"+parent.childNodes[i].id);
	           }
	        }
        }else{
            var hashVo=new ParameterSet();
            hashVo.setValue("subsys_id",subsys_id);
            hashVo.setValue("parentid",parentsetid);
            hashVo.setValue("id",id);
            var request=new Request({asynchronous:false,onSuccess:return_ok,functionId:'9021001050'},hashVo);
        }
   }
   else
   {
    if(type=="0")
     {
         var imgurl = "/images/lock_co_1.gif";   
    	 if(currnode.load)
     	 {

     	   var tmp = new xtreeItem(id+"#1","["+id+"]"+name,"/performance/kh_system/kh_template/init_kh_item.do?b_query=link&templateid="+getEncodeStr("~"+id)+"&subsys_id="+subsys_id,"mil_body","["+id+"]"+name,imgurl,"/servlet/performance/KhTemplateTree?templatesetid="+id+"&subsys_id="+subsys_id+"&b0110=-1&isVisible="+isVisible+"&method="+method+"&persionControl="+persionControl);
           currnode.add(tmp);
           tmp.select();
           selectedClass("treeItem-text-"+tmp.id);
         }
         else
         {
         	currnode.expand();
         	for(var i=0;i<currnode.childNodes.length;i++)
         	{
         	    if(currnode.childNodes[i].uid==id+"#1")
         	    {
         	         currnode.childNodes[i].select();
         	         selectedClass("treeItem-text-"+currnode.childNodes[i].id);
         	    }
         	}
         }
      }
      else
      {
       //currnode.setText("["+id+"]"+name);
        var parent=currnode.parent;
        parent.clearChildren();
        parent.loadChildren();
        parent.expand();
        for(var i=0;i<parent.childNodes.length;i++)
        {
           if(parent.childNodes[i].uid==id+"#1")
           {
              parent.childNodes[i].select();
              selectedClass("treeItem-text-"+parent.childNodes[i].id);
           }
        }
      }
      }
     self.parent.mil_body.location="/performance/kh_system/kh_template/init_kh_item.do?b_query=link&templateid="+getEncodeStr("~"+id)+"&subsys_id="+subsys_id;

}
function return_ok(outparameter)
{
		var orgLink = outparameter.getValue("orgLink");
		var parentsetid = outparameter.getValue("parentsetid");
		var id = outparameter.getValue("id");
		var findNode = false;
	    if(orgLink!=''){
	        var temps=orgLink.split("/");
			var obj=root;
			for(var i=temps.length;i>=0;i--)
			{
				//obj.expand();
				for(var j=0;j<obj.childNodes.length;j++)
				{
					if(obj.childNodes[j].uid==temps[i]+"#0")
					{
						obj=obj.childNodes[j];
						findNode = true;
						break;
					}
				}
			}
			obj.clearChildren();
		    obj.loadChildren();
		    alert("另存模板成功!");
	        return;
			obj.expand();				
			for(var i=0;i<obj.childNodes.length;i++)
	        {
	           if(obj.childNodes[i].uid==id+"#1")
	           {
	              obj.childNodes[i].select();
	              selectedClass("treeItem-text-"+obj.childNodes[i].id);
	           }
	        }
	    }
}
function modify()
{
    var currnode=Global.selectedItem; 
    var id=currnode.uid;
    if(id=="root")
    {
       return;
    }
    var tt=id.substring(0,id.indexOf("#"));
    var ss=id.substring((id.indexOf("#")+1));//=0是模板分类 =1是模板
    if(ss=="1")
    {
        addTemplate('1');
    }
    else
    {
       addTemplateSet('1');
    }
}
function del()
{
    var currnode=Global.selectedItem; 
    var id=currnode.uid;
    var tt=id.substring(0,id.indexOf("#"));
    var ss=id.substring((id.indexOf("#")+1));//=0是模板分类 =1是模板
    if(id=="root")
    {
        return;
    }
   var msg="";
     if(ss==0)
        msg="确认删除模板分类\""+currnode.text+"\"及其子分类和模板吗？";
     else
        msg="确认删除模板\""+currnode.text+"\"?";   
   if(confirm(msg))
   {
      var hashvo=new ParameterSet();
      hashvo.setValue("kind",ss);
      hashvo.setValue("id",tt);
      var request=new Request({asynchronous:false,onSuccess:delete_ok,functionId:'9021001029'},hashvo);
   }
    
}
function delete_ok(outparameter)
{
   var subsys_id = document.khTemplateForm.subsys_id.value;
   var msg = outparameter.getValue("msg");
   if(msg!='0')
   {
       alert(msg);
       return;
   }
   else
   {
     var currnode=Global.selectedItem;
     var preitem=currnode.getPreviousSibling();
     
     var tt="-1";
     //var ss=id.substring((id.indexOf("#")+1));
     if(preitem!=null)
     {
          var id=preitem.uid;
          var ss=preitem.uid.substring((preitem.uid.indexOf("#")+1));//=0是模板分类 =1是模板
          if(ss=='1')
          {
             tt=id.substring(0,id.indexOf("#"));
          }
          preitem.select();
          selectedClass("treeItem-text-"+preitem.id);
     }
     else
     {
        var parent=currnode.parent;
        if(parent!=null)
        {
           parent.select();
           selectedClass("treeItem-text-"+parent.id);
        }
     }
     currnode.remove();
     self.parent.mil_body.location="/performance/kh_system/kh_template/init_kh_item.do?b_query=link&templateid="+getEncodeStr("~"+tt)+"&subsys_id="+subsys_id
   }
}
function config(type)
{
    var currnode=Global.selectedItem; 
    var id=currnode.uid;
    var tt=id.substring(0,id.indexOf("#"));
    var ss=id.substring((id.indexOf("#")+1));//=0是模板分类 =1是模板
    if(id=="root")
    {
        return;
    }
     var hashvo=new ParameterSet();
     hashvo.setValue("type",type);
     hashvo.setValue("id",tt);
     hashvo.setValue("name",getEncodeStr(currnode.text));
     hashvo.setValue("ss",ss);
     var request=new Request({asynchronous:false,onSuccess:config_ok,functionId:'9021001030'},hashvo);
    
}
function config_ok(outparameter)
{ 
  var type=outparameter.getValue("type");
  var id=outparameter.getValue("id");
  var name=getDecodeStr(outparameter.getValue("name"));
  var currnode=Global.selectedItem; 
  var uuid=currnode.uid;
  var tt=currnode.uid.substring(0,currnode.uid.indexOf("#"));
  var ss=currnode.uid.substring((currnode.uid.indexOf("#")+1));//=0是模板分类 =1是模板
  var parent=currnode.parent;
  var subsys_id = document.khTemplateForm.subsys_id.value;
  if(parent!=null)
  {
     parent.clearChildren();
     parent.loadChildren();
     parent.expand();
     for(var i=0;i<parent.childNodes.length;i++)
     {
         if(parent.childNodes[i].uid==uuid)
         {
             parent.childNodes[i].select();
             selectedClass("treeItem-text-"+parent.childNodes[i].id);
         }
     }     
  }

}
function validation()
{
    var currnode=Global.selectedItem; 
    var id=currnode.uid;
    if(id=='root')
    {
        alert("请选择模板进行校验！");
        return; 
    }
    var tt=id.substring(0,id.indexOf("#"));
    var ss=id.substring((id.indexOf("#")+1));//=0是模板分类 =1是模板
    if(ss=='0')
    {
        alert("请选择模板进行校验！");
        return;
    }
    var hashvo=new ParameterSet();
    hashvo.setValue("id",tt);
    var request=new Request({asynchronous:false,onSuccess:validationok,functionId:'9021001036'},hashvo);
}
function validationok(outparameters)
{
     var msg=outparameters.getValue("msg");
     alert(getDecodeStr(msg));
     return;
}
function importCurrentTemplate()
{
    var currnode=Global.selectedItem; 
    var id=currnode.uid;
    if(id=='root')
    {
       alert("请选择模板进行操作！");
       return;
    }
    var tt=id.substring(0,id.indexOf("#"));
    var ss=id.substring((id.indexOf("#")+1));//=0是模板分类 =1是模板
     if(ss=='0')
    {
       alert("请选择模板进行操作！");
       return;
    }
     var hashvo=new ParameterSet();
     hashvo.setValue("id",tt);
     var request=new Request({asynchronous:false,onSuccess:showfile1,functionId:'9021001035'},hashvo);
}


function showfile1(outparameters){
    //zhangh 2020-4-7 素质指标,下载改为使用VFS
    var outName=outparameters.getValue("outName");
    outName = decode(outName);
    var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName);
}

function ddd()
{
       var theurl="/performance/kh_system/kh_template/kh_template_tree.do?b_query=link`subsys_id=33`isVisible=2";
       var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
       var return_vo= window.showModalDialog(iframe_url, arguments,
      "dialogWidth:600px; dialogHeight:500px;resizable:no;center:yes;scroll:yes;status:no");
}
function buttonok()
{	
    var currnode=Global.selectedItem; 
    var id=currnode.uid;
    var tt=id.substring(0,id.indexOf("#"));
    var ss=id.substring((id.indexOf("#")+1));//=0是模板分类 =1是模板
    if(id=="root")
    {
        return;
    }
    if(ss=='0')
    {
      return;
    }
    var val = "";
    var text=currnode.text;
    val = text.substring(text.indexOf("]")+1)+","+tt;
    parent.window.returnValue=val;
    if(window.showModalDialog) {
        parent.window.close();;
    }else{
        parent.window.opener.window.getTemplate1_ok(obj);
        window.open("about:blank","_top").close();
    }
}
function exportD(subsys)
{
	var hashvo=new ParameterSet();
	hashvo.setValue("subsys_id",subsys);
	var request=new Request({asynchronous:false,onSuccess:export_ok,functionId:'9021001038'},hashvo);			  
}
function export_ok(outparameters)
{
	var outName=outparameters.getValue("outName");
	if(outName=='1')
	{
	   alert("所选节点下没有模版和模版分类，不能导出！");
	   return;
	}
    //zhangh 2020-4-7 测评量表,下载改为使用VFS
    outName = decode(outName);
    var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName);
}

/*
function eeeee()
{
   var theurl="/performance/kh_system/kh_template/kh_template_tree.do?br_select=select";
   var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
   var return_vo= window.showModalDialog(iframe_url, "", 
      "dialogWidth:500px; dialogHeight:500px;resizable:no;center:yes;scroll:yes;status:yes");
   if(return_vo)
   {
     var obj=new Object();
     obj.flag=return_vo.flag;
     if(obj.flag=='1')
     {
        ///performance/kh_system/kh_template/kh_template_tree.do?b_query=link&subsys_id=33&isVisible=1&method=0&templateId=-1
        self.parent.location="/performance/kh_system/kh_template/kh_template_tree.do?b_query=link&subsys_id="+khTemplateForm.subsys_id.value+"&isVisible=1&method=0&templateId=-1";
     }
   }
}
*/

function eeeee()
{
	khTemplateForm.action="/performance/kh_system/kh_template/kh_template_tree.do?br_select=select";
	khTemplateForm.target="mil_body";
   	khTemplateForm.submit();	  	   
}

function initTemplate(tid)
{
   initDocument();
   setDrag(true);
  if(tid=='-1')
     return;
  else
  {
       for(var i=0;i<root.childNodes.length;i++)
       {
            var item=root.childNodes[i];
            dg(item,tid);
       }
       var currnode=Global.selectedItem;
       var id=currnode.uid;
       root.collapseAll();
       var parent=currnode.parent;
       while(parent)
       {
           parent.expand();
           for(var i=0;i<parent.childNodes.length;i++)
           {
               if(parent.childNodes[i].uid==id)
               {
                   parent.childNodes[i].select();
                   Global.selectedItem=parent.childNodes[i];
                   selectedClass("treeItem-text-"+parent.childNodes[i].id);
               }
           }
           parent=parent.parent;
       }
  }
}
var x=0;
function dg(node,tid)
{
   var uid=node.uid;
   var tt=uid.substring(0,uid.indexOf("#"));
   var ss=uid.substring((uid.indexOf("#")+1));//=0是模板分类 =1是模板
   if(ss=='1'&& tt==tid)
   {    
        var parent=node.parent;
        while(parent)
        {
            parent.expand();
            parent=parent.parent;
        }
        node.select();
        selectedClass("treeItem-text-"+node.id);
        var currnode=Global.selectedItem;
        x=1;
        return;
   }
   else
   {
        if(x==1)
          return;
        node.expand();
        for(var i=0;i<node.childNodes.length;i++)
        {
           if(x==1)
             continue;
           dg(node.childNodes[i],tid);
        }
   }
}
 
function dragendSort(){
	var currnode=Global.selectedItem; 
	var id=currnode.uid;
    var tt=id.substring(0,id.indexOf("#"));
    var ss=id.substring((id.indexOf("#")+1));//=0是模板分类 =1是模板
    if(ss=='1')
       return;
	if(currnode.dragFrom.uid=='root')
		return false;
	var dragtt=currnode.dragFrom.uid.substring(0,id.indexOf("#"));
    var dragss=currnode.dragFrom.uid.substring((id.indexOf("#")+1));//=0是模板分类 =1是模板
    if(id=='root'&&dragss=='1')
       return;
    var tableName="";
    if(dragss=='1')
        tableName="per_template";
    else
        tableName="per_template_set";
	if(currnode.dragbool){
		var hashvo=new ParameterSet();
		hashvo.setValue("fromid",currnode.dragFrom.uid);
		hashvo.setValue("toid",currnode.uid);
		hashvo.setValue("table",tableName);
		var request=new Request({method:'post',asynchronous:false,functionId:'9021001045'},hashvo);		
		if(currnode.uid=='root')
		{
			currnode.dragFrom.remove();
			currnode.clearChildren();
			currnode.expand();
		}else
		{
			currnode.dragFrom.remove();
			currnode.parent.clearChildren();
			currnode.parent.load=true;
	  		currnode.parent.loadChildren();
	  		currnode.parent.reload(1);
		}		
/*		
		if(currnode.uid=='root')
		{
			currnode.dragFrom.remove();
			currnode.clearChildren();
			currnode.expand();
		}else
		{
			currnode.dragFrom.remove();
			currnode.parent.clearChildren();
			currnode.parent.load=true;
	  		currnode.parent.loadChildren();
	  		currnode.parent.reload(1);
	  		currnode.clearChildren();
	  		currnode.loadChildren();
	  		currnode.reload(1);
	  		currnode.expand();
		}
*/		
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
    var menubarEl = document.getElementById("menubar");
    if(!menubarEl){
        return;
    }
    var menu1 = new Ext.menu.Menu({
        allowOtherMenus: false,
        items: [
            new Ext.menu.Item({
                text:'导入',//kh.field.inport,
                icon:"/images/prop_ps.gif",
                handler: function(){
                    eeeee();
                }
            }),
            new Ext.menu.Item({
                text:'批量导出',//kh.field.export,
                icon:"/images/export.gif",
                handler:function(){
                    exportD('${khTemplateForm.subsys_id}');
                }
            }),
            new Ext.menu.Item({
                text: '导出Excel',//lable.kh.template.importcurrent,
                icon:'/images/export.gif',
                handler:function(){
                    importCurrentTemplate();
                }
            })
        ]
    });
    var menu2 = new Ext.menu.Menu({
        allowOtherMenus: false,
        items: [
            new Ext.menu.Item({
                text:'新建模板分类',//kh.field.new,
                icon:"/images/add.gif",
                handler: function(){
                    addTemplateSet('0');
                }
            }),
            new Ext.menu.Item({
                text:'新建模板',//kh.field.new,
                icon:"/images/add.gif",
                handler: function(){
                    addTemplate('0');
                }
            }),

            new Ext.menu.Item({
                text:'修改',//label.kh.edit,
                icon:"/images/edit.gif",
                handler:function(){
                    modify();
                }
            }),
            new Ext.menu.Item({
                text:'另存模板',//kh.field.new,
                icon:"/images/add.gif",
                handler: function(){
                    addTemplate('3');
                }
            }),
            new Ext.menu.Item({
                text: '删除',//kh.field.delete,
                icon:'/images/del.gif',
                handler:function(){
                    del();
                }
            }),
            new Ext.menu.Item({
                text: '模板校验',//kh.field.sort,
                handler:function(){
                    validation();
                }
            })
        ]
    });
    var menu3 = new Ext.menu.Menu({
        allowOtherMenus: false,
        items: [
            new Ext.menu.Item({
                text:'有效',//kh.field.bzbd,
                icon:"/images/open1.png",
                handler: function(){
                    config('1');
                }
            }),
            new Ext.menu.Item({
                text:'无效',//kh.field.bzbd,
                icon:"/images/open.png",
                handler: function(){
                    config('0');
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
<html:form action="/performance/kh_system/kh_template/kh_template_tree"> 
<logic:equal value="1" name="khTemplateForm" property="isVisible">
<div style="margin-top: 10px;margin-left: 2px;" id="menubar">
</div>
</logic:equal>
<table align="left" border="0" cellpadding="0" cellspacing="0">
<tr>
<td>
   <table align="left" class="mainbackground"  border="0" cellpadding="0" cellspacing="0">
		 	            
         <tr>
           <td align="left" style="padding: 0px; margin: 0px;"> 
            <div id="treemenu" ondragend="dragendSort();"> 
             <SCRIPT LANGUAGE="javascript">                 
               <bean:write name="khTemplateForm" property="tree" filter="false"/>
             </SCRIPT>
             </div>             
           </td>
           </tr>           
    </table>  
    </td>
    </tr>
    <tr>
    <td>
    <html:hidden name="khTemplateForm" property="subsys_id"/>
    <html:hidden name="khTemplateForm" property="isVisible"/>
    <html:hidden name="khTemplateForm" property="method" />
    <html:hidden name="khTemplateForm" property="persionControl"/>
    </td>
    </tr>
    </table>
    
</html:form>

<script language="javascript">
  initTemplate("${khTemplateForm.t_tid}");
</script>
</body>
<br>
<br>

</html>
