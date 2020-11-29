<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hjsj.hrms.businessobject.sys.SysParamBo"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc" %>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript" src="/js/validate.js"></script>
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="../../../module/utils/js/template.js"></script>
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

<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
    String bosflag="";
    String themes="default";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  
          bosflag=userView.getBosflag(); 
      /*xuj added at 2014-4-18 for hcm themes*/
      themes=SysParamBo.getSysParamValue("THEMES",userView.getUserName());	 
	}

%>
<!-- 引入ext      wangb 20171117 -->
<script language="JavaScript" src="/module/utils/js/template.js"></script>
<script type="text/javascript">
function delete_stat()
{
   var currnode=parent.frames['mil_menu'].Global.selectedItem;
   if(currnode==null)
       return;
   if(currnode.uid=="root")
     	return;  
    if(currnode.uid==currnode.text)
    	return; 
    if(!ifdelete(currnode.text))
       return;
    var action=currnode.action;
    action = $URL.decode(action);//路径需要转义回正常链接地址，否则无法删除 bug 56588
    var statid=""; 
    var norder="";    
    var type="0";  
    if(action.indexOf("statid")!=-1)
    {
      statid=action.substring(action.indexOf("statid")+7);
       if(statid.indexOf("&")!=-1)
         statid=statid.substring(0,statid.indexOf("&"));
       if(statid.indexOf("`")!=-1)
         statid=statid.substring(0,statid.indexOf("`"));
    }   
    if(action.indexOf("norder")!=-1)
    {
       norder=action.substring(action.indexOf("norder")+7);       
       if(norder.indexOf("&")!=-1)
         norder=norder.substring(0,norder.indexOf("&"));        
       if(norder!=null&&norder!="")
       {
          type="1";
       }
    }
    var hashvo=new ParameterSet();
    hashvo.setValue("statid",statid);	
    hashvo.setValue("norder",norder);	  
    hashvo.setValue("type",type);	    
    var request=new Request({asynchronous:false,onSuccess:delete_ok,functionId:'11080204054'},hashvo);  	
}
function delete_ok(outparamters)
{
    var opflag=outparamters.getValue("opflag");  
    if(opflag=="true")
    {
      alert("删除操作成功！");
      var currnode=parent.frames['mil_menu'].Global.selectedItem;
      var preitem=currnode.getPreviousSibling();
      currnode.remove();
      if(preitem.childNodes.length>0){
	      preitem.select(preitem);
	      currnode=currnode.parent;
	      var action=currnode.action
	      if(action=="javascript:void(0)")
	         action="/general/static/commonstatic/statshow.do?b_reini=link";
	      statForm.action=action;
	      statForm.target="mil_body";
	      statForm.submit();
      }else{
      	if(preitem.uid==preitem.text){
      		preitem.remove();
      		var rootpreitem=preitem.getPreviousSibling();
      		rootpreitem.select(preitem);
		      var action=rootpreitem.action
		      if(action=="javascript:void(0)")
		         action="/general/static/commonstatic/statshow.do?b_reini=link";
		      statForm.action=action;
		      statForm.target="mil_body";
		      statForm.submit();
      	}else{
      		 var action="/general/static/commonstatic/statshow.do?b_reini=link";
		      statForm.action=action;
		      statForm.target="mil_body";
		      statForm.submit();
      	}
      }
    }else
    {
       alert("删除操作失败！");
    }
     

     
}
function add_stat()//新增只能新增统计项
{
   var currnode=parent.frames['mil_menu'].Global.selectedItem; 
   if(currnode==null)
       return;    
   var action=currnode.action;
   var opid=currnode.uid; 
   if(action==""||action=="undefined")
   {
      return;
   }   
   if(action.indexOf("/general/muster/hmuster/processBar.jsp")!= -1 || action.indexOf("b_doubledata")!= -1)//二维 和多维统计 不允许添加统计项
   {
   	  alert("请选择一维统计条件！");
   	  return;
   }
   var statid="";    
   if(action.indexOf("statid")!=-1)
   {
      statid=action.substring(action.indexOf("statid")+7);
      if(statid.indexOf("&")!=-1)
         statid=statid.substring(0,statid.indexOf("&"));
      
   }   
   if(opid!="root"&&action.indexOf("norder")!=-1) 
   {	 
       var theurl="/general/static/commonstatic/editstatic.do?b_edit=link`count=1`statid="+statid+"`editid=`infor_Flag=${statForm.infokind}`opflag=new`chart_type=${statForm.chart_type}";
       var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
       var dw=620,dh=320,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
       if(isIE6()){
          dw += 10;
          dh += 10;
       }
       if(getBrowseVersion()){
       		var return_vo= window.showModalDialog(iframe_url,0, 
        	"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+(dw+100)+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:no;status:no");
        	//window.open(iframe_url);        
       		if(return_vo)
        	 if(return_vo.flag=="true")
        	 {
            	 var action=return_vo.action;
            	 var text=return_vo.legend;
           	  	 var uid=return_vo.uid;
            	 var imgurl="/images/prop_ps.gif";
            	 parent.frames['mil_menu'].add(uid,text,action,"mil_body",text,imgurl,"");
            	 statForm.action=action;
           		 statForm.target="mil_body";
           	  	 statForm.submit();
         	}
       }else{//非IE浏览器 调用弹窗 wangb 20180127
       		var dialog=[];dialog.dw=dw+50;dialog.dh=dh;dialog.iframe_url=iframe_url;
       		openWin(dialog,'新增统计项');
       }
       
   }else if(opid!="root"&&statid!="") 
   {
        var theurl="/general/static/commonstatic/editstatic.do?b_edit=link`count=2`statid="+statid+"`editid=`infor_Flag=${statForm.infokind}`opflag=new`chart_type=${statForm.chart_type}";
       var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
       var dw=620,dh=520,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
       if(isIE6()){
          dw += 10;
          dh += 10;
       }
       if(getBrowseVersion()){
       		var return_vo= window.showModalDialog(iframe_url,0, 
        	"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+(dw+100)+"px; dialogHeight:"+dh+"px;resizable:yes;center:yes;scroll:yes;status:yes");
       	 //window.open(iframe_url);
     	  if(return_vo)
         	if(return_vo.flag=="true")
         	{
             	var action=return_vo.action;
            	var text=return_vo.legend;
             	var uid=return_vo.uid;
             	var imgurl="/images/prop_ps.gif";
             	parent.frames['mil_menu'].addparent(uid,text,action,"mil_body",text,imgurl,"");
             	statForm.action=action;
             	statForm.target="mil_body";
             	statForm.submit();
         	}
       }else{//非IE浏览器 调用弹窗 wangb 20180127
       		var dialog=[];dialog.dw=dw+50;dialog.dh=dh;dialog.iframe_url=iframe_url;
       		openWin(dialog,'新增统计项');
       }
   }else
   {
    	var categories="";
   		 var currn=parent.frames['mil_menu'].Global.selectedItem;
   		 if(currn){
   		 	if(currn.uid==currn.text)
   		 		categories=currn.uid; 
   		 }
      var theurl="/general/static/commonstatic/editstatic.do?b_add=link&infor_Flag=${statForm.infokind}&opflag=new&categories="+getEncodeStr(categories)+"&count=3";
      theurl = theurl.replace(/&/g,"`");
      var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
      var return_vo;
      var dw=352,dh=270,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
      if(getBrowseVersion()){
      		 return_vo= window.showModalDialog(iframe_url,'_blank','dialogLeft:'+dl+'px;dialogTop:'+dt+'px;dialogHeight:320px;dialogWidth:620px;center:yes;help:no;resizable:no;scroll:no;status:no;');
      		//window.open(theurl);
      		if(return_vo)
         	if(return_vo.flag=="true")
         	{
         		var categories=return_vo.categories;
         		if(categories.length>0){
         			var action=return_vo.action;
	             	var text=return_vo.text;
	             	var uid=return_vo.uid;
	             	var xml=return_vo.xml;
	             	var imgurl="/images/groups.gif";
	             	//var currnode=parent.frames['mil_menu'].Global.selectedItem;
	             	parent.frames['mil_menu'].addNameCategories(uid,text,action,"mil_body",categories,imgurl,xml,categories);
            	}else{
	             	var action=return_vo.action;
	             	var text=return_vo.text;
	             	var uid=return_vo.uid;
	             	var xml=return_vo.xml;
	             	var imgurl="/images/groups.gif";
	             	//var currnode=parent.frames['mil_menu'].Global.selectedItem;
	             	parent.frames['mil_menu'].addName(uid,text,action,"mil_body",text,imgurl,xml);
             	}
            }
      }else{//非IE浏览器 调用弹窗 wangb 20180127
      		var dialog=[];dialog.dw=620;dialog.dh=310;dialog.iframe_url=iframe_url;
       		openWin(dialog);
      }
   }
}
//非IE浏览器弹窗方法
function openWin(dialog){
		//兼容非IE浏览器 弹窗替换用 open弹窗  wangb 20171205
		var iTop = (window.screen.availHeight - 30 - dialog.dh) / 2;  //获得窗口的垂直位置
		var iLeft = (window.screen.availWidth - 10 - dialog.dw) / 2; //获得窗口的水平位置 
		window.open(dialog.iframe_url,"","width="+dialog.dw+",height="+dialog.dh+",resizable=no,scroll=no,status=no,left="+iLeft+",top="+iTop);
		/*
		Ext.create("Ext.window.Window",{
		    	id:'stattree',
		    	width:dialog.dw+40,
		    	height:dialog.dh,
		    	title:titles,
		    	resizable:false,
		    	modal:true,
		    	autoScroll:true,
		    	renderTo:Ext.getBody(),
		    	html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='100%' width='100%' src='"+dialog.iframe_url+"'></iframe>"
		 }).show();	
		 */
}
/*
function winClose(){
	Ext.getCmp('stattree').close();
}*/
/*  wangb 20180127
	return_vo  返回值
	count 表示执行什么操作值
	1:新增统计类
	2:新增统计项
	3:新增统计条件
	4:修改统计项
	5:统计条件
	order : 排序
	exeshow : 信息集设置
	sformula : 统计设置
*/
function openReturn(return_vo,count){
	if(count==1){
		if(return_vo)
        	 if(return_vo.flag=="true")
        	 {
            	 var action=return_vo.action;
            	 var text=return_vo.legend;
           	  	 var uid=return_vo.uid;
            	 var imgurl="/images/prop_ps.gif";
            	 parent.frames['mil_menu'].add(uid,text,action,"mil_body",text,imgurl,"");
            	 statForm.action=action;
           		 statForm.target="mil_body";
           	  	 statForm.submit();
         	}
	}else if(count==2){
		if(return_vo)
         	if(return_vo.flag=="true")
         	{
             	var action=return_vo.action;
            	var text=return_vo.legend;
             	var uid=return_vo.uid;
             	var imgurl="/images/prop_ps.gif";
             	parent.frames['mil_menu'].addparent(uid,text,action,"mil_body",text,imgurl,"");
             	statForm.action=action;
             	statForm.target="mil_body";
             	statForm.submit();
         	}
	}else if(count==3){
		if(return_vo)
         	if(return_vo.flag=="true")
         	{
         		var categories=return_vo.categories;
         		if(categories.length>0){
         			var action=return_vo.action;
	             	var text=return_vo.text;
	             	var uid=return_vo.uid;
	             	var xml=return_vo.xml;
	             	var imgurl="/images/groups.gif";
	             	//var currnode=parent.frames['mil_menu'].Global.selectedItem;
	             	parent.frames['mil_menu'].addNameCategories(uid,text,action,"mil_body",categories,imgurl,xml,categories);
            	}else{
	             	var action=return_vo.action;
	             	var text=return_vo.text;
	             	var uid=return_vo.uid;
	             	var xml=return_vo.xml;
	             	var imgurl="/images/groups.gif";
	             	//var currnode=parent.frames['mil_menu'].Global.selectedItem;
	             	parent.frames['mil_menu'].addName(uid,text,action,"mil_body",text,imgurl,xml);
             	}
            }
	}else if(count==4){
		if(return_vo)
         if(return_vo.flag=="true")
         {
         	 var currnode=parent.frames['mil_menu'].Global.selectedItem; 
             var action=return_vo.action;
             var legend=return_vo.legend;
             currnode.setText(legend);            
             currnode.setAction(action);
             statForm.action=action;
             statForm.target="mil_body";
             statForm.submit();
         }
	}else if(count==5){
		if(return_vo)
         if(return_vo.flag=="true")
         {
         	if(return_vo.categories==return_vo.oldcategories){
             var text=return_vo.text;
             var currnode=parent.frames['mil_menu'].Global.selectedItem;             
		     currnode.setText(text);
		     currnode.setTitle(text);
		   }else{
		   	refresh();
		   	if(return_vo.categories.length>0){
		   		var currnode=parent.frames['mil_menu'].Global.selectedItem;
				if(currnode==null)
					return;
				currnode=currnode.root();
				for(var i=0;i<=currnode.childNodes.length-1;i++){
					if(return_vo.categories.toUpperCase()==currnode.childNodes[i].uid.toUpperCase()){
						currnode.childNodes[i].expand();
						break;
					}
				}
		   	}
		   }
		   currnode.openURL();
         }
	}else if(count == "order"){
		if(return_vo&&return_vo=="ok")
     	 {
     		var statid="";
    		var currnode=parent.frames['mil_menu'].Global.selectedItem; 
    		if(currnode!=null) {
	    		var action=currnode.action;  
	    		if(action!=""&&action!="undefined")
	    		{
		    		if(action.indexOf("statid")!=-1)
		    		{
		       			statid=action.substring(action.indexOf("statid")+7);
		       			if(statid.indexOf("&")!=-1)
		          			statid=statid.substring(0,statid.indexOf("&"));
		       
		       			if(statid.indexOf("`")!=-1)
		          			statid=statid.substring(0,statid.indexOf("`"));
		    		} 
	    		}
	    	}
    	  	if (""!=statid && currnode!=null) {
    		  	currnode.openURL();
          	}
          
           	var centerIFrame=parent.document.getElementById("center_iframe");                       
         	if(centerIFrame==null)
          	   	return;  
          
          	centerIFrame.src="/general/static/commonstatic/statitemtree.jsp";          
      	}
	}else if(count=='exeshow'){
		if(return_vo&&return_vo.length>0){
			var currnode=parent.frames['mil_menu'].Global.selectedItem;
			var pnode=currnode.parent;
			while(pnode.uid!='root'&&pnode.uid!=pnode.text){
				currnode=pnode;
				pnode=pnode.parent;
			}
			currnode.openURL();
		}
	}else if(count=='sformula'){
		if(return_vo){
			var currnode=parent.frames['mil_menu'].Global.selectedItem;
			currnode.openURL();
		}
	}
}
	
function update_stat()
{
   var currnode=parent.frames['mil_menu'].Global.selectedItem; 
   if(currnode==null)
       return;   
   var action=$URL.decode(currnode.action);  
   if(action==""||action=="undefined")
   {
      return;
   }   
   var statid="";
   var opid=currnode.uid;  
   if(opid=="root")
   {
      alert("请选择统计项！");
      return false;
   }
   if(action.indexOf("statid")!=-1)
   {
      statid=action.substring(action.indexOf("statid")+7);
      if(statid.indexOf("&")!=-1)
         statid=statid.substring(0,statid.indexOf("&"));
      if(statid.indexOf("`")!=-1)
         statid=statid.substring(0,statid.indexOf("`"));
      
   }   
   if(opid!="root"&&action.indexOf("norder")!=-1)
   {
       var norder=action.substring(action.indexOf("norder")+7);
       if(norder.indexOf("&")!=-1)
         norder=norder.substring(0,norder.indexOf("&"));       
       var theurl="/general/static/commonstatic/editstatic.do?b_edit=link`statid="+statid+"`editid="+norder+"`infor_Flag=${statForm.infokind}`opflag=edit`count=4";
       var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
       var dw=660,dh=560,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
       if(isIE6()){
          dw += 10;
          dh += 10;
       }
       if(getBrowseVersion()){
       		var return_vo= window.showModalDialog(iframe_url,0,
       		"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:no;status:no");
      		// var return_vo;
       		//window.open(iframe_url);
       		if(return_vo)
        		 if(return_vo.flag=="true")
         	{
             	var action=return_vo.action;
             	var legend=return_vo.legend;
             	currnode.setText(legend);            
             	currnode.setTitle(legend);            
             	currnode.setAction(action);
             	statForm.action=action;
             	statForm.target="mil_body";
             	statForm.submit();
         	}
       }else{//非IE浏览器 调用弹窗 wangb 20180127
       		var dialog=[];dialog.dw=dw+70;dialog.dh=dh;dialog.iframe_url=iframe_url;
       		openWin(dialog);
       }
       
        
   }else
   {
   	var currn=parent.frames['mil_menu'].Global.selectedItem;
    var theurl;
    var dw=620,dh=320,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
   	if(currn){
   		if(currn.uid==currn.text){//修改统计分类  wangb 20190704
   			theurl="/general/static/commonstatic/editstatic.do?b_add=link&infor_Flag=${statForm.infokind}&opflag=edit&categories="+getEncodeStr(currn.text)+"&type=categories&count=5";
   			dh=170;//调整修改统计分类页面高度 wangbo 2019-12-07 bug 50725
   		}else{
   		 	theurl="/general/static/commonstatic/editstatic.do?b_add=link&infor_Flag=${statForm.infokind}&opflag=edit&statid="+statid+"&count=5";
   		}
   	}
    //var theurl="/general/static/commonstatic/editstatic.do?b_add=link&infor_Flag=${statForm.infokind}&opflag=edit&statid="+statid+"&count=5";
	 theurl = theurl.replace(/&/g,"`");
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
       var return_vo;
       //window.open(theurl);
       if(getBrowseVersion()){
       	return_vo= window.showModalDialog(iframe_url,'_blank','dialogLeft:'+dl+'px;dialogTop:'+dt+'px;dialogHeight:'+dh+'px;dialogWidth:'+dw+'px;center:yes;scroll:no;help:no;resizable:no;status:no;');
      	if(return_vo)
         if(return_vo.flag=="true"){
         	if(return_vo.categories==return_vo.oldcategories){
               var text=return_vo.text;
               var currnode=parent.frames['mil_menu'].Global.selectedItem;             
		       currnode.setText(text);
		       currnode.setTitle(text);
		    }else{
		   		refresh();
		   		if(return_vo.categories.length>0){
		   			var currnode=parent.frames['mil_menu'].Global.selectedItem;
					if(currnode==null)
						return;
					currnode=currnode.root();
					for(var i=0;i<=currnode.childNodes.length-1;i++){
						if(return_vo.categories.toUpperCase()==currnode.childNodes[i].uid.toUpperCase()){
							currnode.childNodes[i].expand();
							break;
						}
					}
		   		}
		    }
		    currnode.openURL();
         }
      }else{//非IE浏览器 调用弹窗 wangb 20180127
       		var dialog=[];dialog.dw=dw;dialog.dh=dh;dialog.iframe_url=iframe_url;
       		openWin(dialog);	
      }
   }
    
}


	function refresh(){
		var currnode=parent.frames['mil_menu'].Global.selectedItem;
		if(currnode==null)
			return;
		//alert(currnode.uid);
		currnode=currnode.root();
		if(currnode.load)
		while(currnode.childNodes.length){
			//alert(currnode.childNodes[0].uid);
			currnode.childNodes[0].remove();
		}
		currnode.load=true;
		currnode.loadChildren();
		currnode.reload(1);
	}
function order_stat()
{
	var statid="";
	var currnode=parent.frames['mil_menu'].Global.selectedItem;
	if(currnode!=null) {
		var action=currnode.action;
        action = $URL.decode(action)
		if(action!=""&&action!="undefined")
	    {
		    if(action.indexOf("statid")!=-1)
		    {
		       statid=action.substring(action.indexOf("statid")+7);
		       if(statid.indexOf("&")!=-1)
		          statid=statid.substring(0,statid.indexOf("&"));
		       
		       if(statid.indexOf("`")!=-1)
		          statid=statid.substring(0,statid.indexOf("`"));
		    } 
	    }
    }
	   
	  var dw=400,dh=400,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
      var theurl="/general/static/commonstatic/editstatic.do?b_order=link`infor_Flag=${statForm.infokind}`count=order";//没有完全转义   
	  if (""!=statid)
		  theurl = theurl + "`statid="+statid;
	  else
		  theurl = theurl + "`statid=-1";
	
      var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
      if(getBrowseVersion()){
      	var returnVaule= window.showModalDialog(iframe_url,0, 
         "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:400px; dialogHeight:400px;resizable:no;center:yes;scroll:no;status:no");
      		if(returnVaule && returnVaule == 'ok'){
	     	 	openReturn("ok",'order');
      		}
      }else{//非IE浏览器 调用弹窗 wangb 20180127
      		var dialog=[];dialog.dw=dw+150;dialog.dh=dh;dialog.iframe_url=iframe_url;//添加非IE浏览器进入标识 wangb 20180803 bug 39334
       		openWin(dialog);	
      }
}

function infosetup(){
	var currnode=parent.frames['mil_menu'].Global.selectedItem;
   if(currnode==null)
       return;
    //2014-11-14 liuy 信息集设置不能对统计分类使用 start
    if(currnode.uid==currnode.text){
    	alert("信息集设置不能对统计分类使用！");
   		return;
    }
    //2014-11-14 liuy end 
   // alert(currnode.uid+currnode.text);
    if(currnode.uid=='root'){   
    	//alert("dddd"); 
		var theurl="/general/static/commonstatic/statshowsetup.do?b_query=link&inforkind=${statForm.infokind}";
		theurl = theurl.replace(/&/g,"`");
		var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
	    var return_vo;
	    var dw=650,dh=500,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
	    //return_vo= window.showModelessDialog(theurl,'_blank','dialogHeight:500px;dialogWidth:650px;center:yes;scroll:no;help:no;resizable:no;status:no;');
		window.open(iframe_url,"_blank",'toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top='+dt+',left='+dl+',width='+dw+',height='+dh);
	}else{
		var pnode=currnode.parent;
		while(pnode.uid!='root'&&pnode.uid!=pnode.text){
			currnode=pnode;
			pnode=pnode.parent;
		}
		var hashvo=new ParameterSet();
		hashvo.setValue("id",currnode.uid);
		hashvo.setValue("name",currnode.text);
		var request=new Request({method:'post',onSuccess:exeshow,functionId:'11080204105'},hashvo);
	}
}
//liuy 2014-11-14 控制多维交叉统计也可以设置信息集 start
var titlename;//通过统计条件控制 title wangb 20180806 bug 39412
function exeshow(outparamters){
	var msg=outparamters.getValue("msg");
	var id=outparamters.getValue("id");
	var name=outparamters.getValue("name");
	if(msg!="error"){
		var theurl="/general/static/commonstatic/statshowsetup.do?b_setup=link&id="+id+"&name="+getEncodeStr(name)+"&infokind=${statForm.infokind}&type="+msg+"&count=exeshow";
		theurl = theurl.replace(/&/g,"`");
		var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
		var return_vo;
		var dw=600,dh=500;
		if(msg=="2"||msg=="3"){
			dw=450;
			dh=300;
		}
		var dl=(screen.width-dw)/2,dt=(screen.height-dh)/2;
   		if(getBrowseVersion()){
   			return_vo= window.showModalDialog(iframe_url,name,'dialogLeft:'+dl+'px;dialogTop:'+dt+'px;dialogHeight:'+(dh+20)+'px;dialogWidth:'+(dw+20)+'px;center:yes;scroll:no;help:no;resizable:no;status:no;');
			if(return_vo&&return_vo.length>0){
				var currnode=parent.frames['mil_menu'].Global.selectedItem;
				var pnode=currnode.parent;
				while(pnode.uid!='root'&&pnode.uid!=pnode.text){
					currnode=pnode;
					pnode=pnode.parent;
				}
				currnode.openURL();
			}	
   		}else{//非IE浏览器 调用弹窗 wangb 20180127
   		   titlename = name;
   			var dialog=[];dialog.dw=dw+10;dialog.dh=dh;dialog.iframe_url=iframe_url;
       		openWin(dialog);
   		}
	}
}
//liuy 2014-11-14 end
//liuy 2014-11-14  控制多维交叉统计不能设置统计方式 start
function sformula(){
	var currnode=parent.frames['mil_menu'].Global.selectedItem;
   if(currnode==null)
       return; 
   // alert(currnode.uid+currnode.text);
    if(currnode.uid!='root'&&currnode.uid!=currnode.text){
		var pnode=currnode.parent;
		while(pnode.uid!='root'&&pnode.uid!=pnode.text){
			currnode=pnode;
			pnode=pnode.parent;
		}
		var hashvo=new HashMap();
		hashvo.put("id",currnode.uid);
		hashvo.put("name",currnode.text);
		//var request=new Request({method:'post',onSuccess:statisSet,functionId:'11080204105'},hashvo);
		Rpc({functionId:'11080204105',async:false,success:statisSet},hashvo);//update by xiegh on date20180322 bug35759
		function statisSet(outparamters){
		var result =Ext.decode(outparamters.responseText);
		var msg=result.msg;
		var id=result.id;
		var name=result.name;
		if(msg=="1"||msg=="2"){//在这里只有一维和二维才能设置统计方式
			var theurl="/general/static/commonstatic/statshowsetup.do?b_sformula=link&id="+id+"&name="+getEncodeStr(name)+"&count=sformula&infokind=${statForm.infokind}";
			theurl = theurl.replace(/&/g,"`");
			var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
   			
			var return_vo;
			var dw=600,dh=525,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
			if(getBrowseVersion()){
			  	return_vo= window.showModalDialog(iframe_url,'_blank','dialogLeft:'+dl+'px;dialogTop:'+dt+'px;dialogHeight:'+dh+'px;dialogWidth:600px;center:yes;scroll:no;help:no;resizable:no;status:no;');
				if(return_vo){
					currnode.openURL();
				}
			}else{//非IE浏览器 调用弹窗 wangb 20180127
				var dialog=[];dialog.dw=dw;dialog.dh=dh;dialog.iframe_url=iframe_url;
       			openWin(dialog);
			}
			
		}else
			alert("多维统计暂不支持设置统计方式！");
	}
	}else{
		alert("请选择统计项！");
	}
}

//liuy 2014-11-14 end
function isShow(infokind) {
	if(infokind == '1,2,3') {
		var piframe = parent.document.getElementById('top_iframe');
		piframe.height = 0;		
		var td = piframe.parentNode;
		td.style.height=0;
		/* 首页-常用统计-more-脚本报错问题 xiaoyun 2014-8-4 start  */
		//piframe.reload();
		/* 首页-常用统计-more-脚本报错问题 xiaoyun 2014-8-4 end  */
	}
}
</script>
<style>
<!--
	body {
	margin-left: 0px;
	margin-top: 0px;
	margin-right: 0px;
	margin-bottom: 0px;
	}
	
	img { margin-left:0px; }
-->
</style>
<hrms:themes />
<body border="0" cellspacing="0" cellpadding="0" onload="isShow('${statForm.infokind}');">
<html:form action="/general/static/commonstatic/statshow" > 
<!-- 非兼容模式浏览器兼容修改：按钮看不全被遮住一半，去掉margin-left guodd 2018-03-01 -->
   <table width="1000" style="margin-top:-4px;" border="0" cellspacing="0"  align="center" cellpadding="0" >     
   <tr align="left" class="toolbar">
		<td valign="middle" align="left" style="padding-left:4px;">
		<logic:equal name="statForm" property="infokind" value="2">
		   <hrms:priv func_id="231702">         
			<img src="/images/add.gif" border=0 title='<bean:message key="button.insert"/>' style="cursor:pointer;" onclick="add_stat();"/> 
		   </hrms:priv>
		   <hrms:priv func_id="231703">
			<img src="/images/del.gif" border=0 title='<bean:message key="button.delete"/>' style="cursor:pointer;" onclick="delete_stat();"/>
		   </hrms:priv>
		   <hrms:priv func_id="231704">
		    <img src="/images/edit.gif" border=0 title='<bean:message key="button.edit"/>' style="cursor:pointer;" onclick="update_stat();"/>		
		   </hrms:priv>
		   <hrms:priv func_id="231705">
		    <img src="/images/sort.gif" border=0 title='<bean:message key="kq.item.change"/>' style="cursor:pointer;" onclick="order_stat();"/>		
		   </hrms:priv>
		   <hrms:priv func_id="231706">
		    <img src="/images/sys_config.gif" border=0 title='<bean:message key="button.info.setup"/>' style="cursor:pointer;" onclick="infosetup();"/>		
		   </hrms:priv>
		   <hrms:priv func_id="231707">
		    <img src="/images/img_q.gif" border=0 title='<bean:message key="button.info.sformula"/>' style="cursor:pointer;" onclick="sformula();"/>		
		   </hrms:priv>
		</logic:equal>
		<logic:equal name="statForm" property="infokind" value="1">
		   <hrms:priv func_id="2602302,04010102">         
			<img src="/images/add.gif" border=0 title='<bean:message key="button.insert"/>' style="cursor:pointer;" onclick="add_stat();"/>
		   </hrms:priv>
		   <hrms:priv func_id="2602303,04010103">
			<img src="/images/del.gif" border=0 title='<bean:message key="button.delete"/>' style="cursor:pointer;" onclick="delete_stat();"/>
		   </hrms:priv>
		   <hrms:priv func_id="2602304,04010104">
		    <img src="/images/edit.gif" border=0 title='<bean:message key="button.edit"/>' style="cursor:pointer;" onclick="update_stat();"/>
		   </hrms:priv>
		   <hrms:priv func_id="2602305,04010105">
		    <img src="/images/sort.gif" border=0 title='<bean:message key="kq.item.change"/>' style="cursor:pointer;" onclick="order_stat();"/>
		   </hrms:priv>
		   <hrms:priv func_id="2602306,04010106">
		    <img src="/images/sys_config.gif" border=0 title='<bean:message key="button.info.setup"/>' style="cursor:pointer;" onclick="infosetup();"/>
		   </hrms:priv>
		   <hrms:priv func_id="2602308">
		    <img src="/images/img_q.gif" border=0 title='<bean:message key="button.info.sformula"/>' style="cursor:pointer;" onclick="sformula();"/>
		   </hrms:priv>
		</logic:equal>
		<logic:equal name="statForm" property="infokind" value="3">
		    <hrms:priv func_id="2311032">         
			  <img src="/images/add.gif" border=0 title='<bean:message key="button.insert"/>' style="cursor:pointer;" onclick="add_stat();"/> 
		    </hrms:priv>
		   <hrms:priv func_id="2311033">
			<img src="/images/del.gif" border=0 title='<bean:message key="button.delete"/>' style="cursor:pointer;" onclick="delete_stat();"/>
		   </hrms:priv>
		   <hrms:priv func_id="2311034">
		    <img src="/images/edit.gif" border=0 title='<bean:message key="button.edit"/>' style="cursor:pointer;" onclick="update_stat();"/>		
		   </hrms:priv>
		   <hrms:priv func_id="2311035">
		    <img src="/images/sort.gif" border=0 title='<bean:message key="kq.item.change"/>' style="cursor:pointer;" onclick="order_stat();"/>		
		   </hrms:priv>
		   <hrms:priv func_id="2311036">
		    <img src="/images/sys_config.gif" border=0 title='<bean:message key="button.info.setup"/>' style="cursor:pointer;" onclick="infosetup();"/>		
		   </hrms:priv>
		   <hrms:priv func_id="2311037">
		    <img src="/images/img_q.gif" border=0 title='<bean:message key="button.info.sformula"/>' style="cursor:pointer;" onclick="sformula();"/>		
		   </hrms:priv>
		</logic:equal>
		</td>
	</tr> 	

    </table>
</html:form>
</body>
<script language="javascript">
  initDocument();
if(!getBrowseVersion()){ //非IE浏览器兼容性   wangb 20180127
	var form = document.getElementsByName('statForm')[0];
	var table = form.getElementsByTagName('table')[0];
	table.style.marginLeft='';
}
</script>