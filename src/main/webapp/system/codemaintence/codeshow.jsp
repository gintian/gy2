<%@page import="com.hjsj.hrms.actionform.sys.codemaintence.CodeMaintenceForm"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="org.apache.commons.beanutils.DynaBean"%>
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<SCRIPT LANGUAGE=javascript src="/ajax/basic.js"></SCRIPT> 
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
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
<script language="JavaScript" src="../../../../module/utils/js/template.js"></script>
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
	CodeMaintenceForm codeMaintenceForm = (CodeMaintenceForm)session.getAttribute("codeMaintenceForm");
	String status = codeMaintenceForm.getStatus();
	String categories =codeMaintenceForm.getCategories();
 %>
<style>
<!--
.RecordRowL {
	border: inset 1px #94B6E6;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid;
	BORDER-RIGHT: none; 
	BORDER-TOP: #94B6E6 1pt solid;
	font-size: 12px;
	border-collapse:collapse; 
	height:22;
}
.RecordRowR {
	border: inset 1px #94B6E6;
	BORDER-BOTTOM: #94B6E6 1pt solid;
	BORDER-LEFT: none;
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	font-size: 12px;
	border-collapse:collapse; 
	height:22;
}
-->
</style>
<hrms:themes></hrms:themes>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="javascript" src="/js/codetree.js"></script>
<script type="text/javascript">
<!--
function bak()
{
   codeMaintenceForm.action="/system/codemaintence/codeshow.do?br_return=return";
   codeMaintenceForm.target="il_body";
   codeMaintenceForm.submit();
}

function addset()
{
 var return_vo= window.showModalDialog("/system/codemaintence/add_edit_codeitem.do?b_query=link&encryptParam=<%=PubFunc.encrypt("query=query&control=")%>", false, "dialogWidth:500px; dialogHeight:300px;resizable:no;center:yes;scroll:yes;status:no");   
}

  function inputcode(){
  	var dw=470,dh=200,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
	var thecodeurl="/system/codemaintence/codeinput.do?encryptParam=<%=PubFunc.encrypt("flag=open")%>"; 
    var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
    // var return_vo= window.showModalDialog(iframe_url, false,
    //     "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
      return_vo ='';
      var theUrl = iframe_url;
      Ext.create('Ext.window.Window', {
          id:'inputcode',
          height: 250,
          width: 500,
          title:'代码接收',
          resizable:false,
          modal:true,
          autoScroll:false,
          autoShow:true,
          html:'<iframe style="background-color:#ffffff " frameborder="0" scrolling="no" height="100%" width="100%" src="'+theUrl+'"></iframe>',
          renderTo:Ext.getBody(),
          listeners: {
              'close': function () {
                  if (this.return_vo == 'seccess') {
                      window.open('/system/codemaintence/codetree.do?b_query=aa','il_body');
                  }
              }
          }

      });
  	// if(return_vo=='seccess')
  	// window.open('/system/codemaintence/codetree.do?b_query=aa','il_body');
  }
  function selectCode()
  {
  	codeMaintenceForm.action="/system/codemaintence/codetree.do?b_search=link&status=0&sel="+$URL.encode(getEncodeStr($F('selcodesetid')));
  	codeMaintenceForm.submit();
  }
  function codeset(uid){
  	//var codesetid  = outparamters.getValue("codesetid");

  	codeMaintenceForm.action="/pos/posbusiness/searchposbusinesslist.do?b_query=link&full=1&a_code="+uid;
  	codeMaintenceForm.submit();
  }
  function update_codeset(uid,oldcate,oldstatus)
  {
     var bflag=true;
     var theurl="/system/codemaintence/add_edit_codeset.do?b_query=link&showtitle=0&codesetid="+uid;
     // var return_vo= window.showModalDialog(theurl, bflag,
     //    "dialogWidth:500px; dialogHeight:300px;resizable:no;center:yes;scroll:yes;status:no");
     //    if(return_vo)
  	//  		update_codeset_ok(return_vo,oldcate,oldstatus);
      return_vo ='';
      Ext.create('Ext.window.Window', {
          id:'add_codeset',
          height: 310,
          width: 570,
          resizable:false,
          modal:true,
		  title:'<bean:message key="codemaintence.codeset.update" />',
          autoScroll:false,
          autoShow:true,
          html:'<iframe style="background-color:#ffffff " frameborder="0" scrolling="no" height="100%" width="100%" src="'+theurl+'"></iframe>',
          renderTo:Ext.getBody(),
          listeners: {
              'close': function () {
                  if (this.return_vo) {
                      update_codeset_ok(this.return_vo,oldcate,oldstatus);
                  }
              }
          }

      });
  }
  function update_codeset_ok(codesetvo,oldcate,oldstatus)
  {
        var currnode=parent.frames['mil_menu'].Global.selectedItem;  
        var codesetid  = codesetvo.codesetid;
     	var codesetname=codesetvo.codesetdesc;
     	var flag = codesetvo.flag;
     	codeMaintenceForm.vflag = flag;//修改 新增标识 wangb 20180810
     	var states=codesetvo.status;
     	var categories=codesetvo.categories;
     	
     	/*currnode=currnode.root();
     	if(currnode.load)
		while(currnode.childNodes.length){
			currnode.childNodes[0].remove();
		}
		currnode.load=true;
		currnode.loadChildren();
		currnode.reload(1);*/
		//var tmpflag=false;
		//var rootnode=currnode.root();
     	/*if(oldstatus!=1&&oldstatus!=2&&(states==1||states==2)){
     		if(oldcate==''){
	     				var tmpnode;
		     			tmpnode=rootnode.childNodes[1];
		     			if(tmpnode.load){
			     			for(var i=tmpnode.childNodes.length-1;i>=0;i--){
			     				if(tmpnode.childNodes[i].uid==codesetid){
			     					tmpnode.childNodes[i].remove();
			     					break;
			     				}
			     			}
		     			}
	     		}else{
		     		if(currnode.uid!=oldcate){
		     			var tmpnode=rootnode.childNodes[1];
		     			if(tmpnode.load)
		     			for(var i=tmpnode.childNodes.length-1;i>=0;i--){
		     				if(tmpnode.childNodes[i].uid==oldcate){
		     					currnode=tmpnode.childNodes[i];
		     					break;
		     				}
		     			}
		     		}
		     		if(currnode.uid==oldcate&&!currnode.load){
		     			currnode.expand();
		     			if(currnode.childNodes.length==0)
		     				currnode.remove();
		     		}else if(currnode.uid==oldcate){
			     		if(currnode.childNodes.length>1){
			     			for(var i=currnode.childNodes.length-1;i>=0;i--){
			     				if(codesetid==currnode.childNodes[i].uid){
			     					currnode.childNodes[i].remove();
			     					break;
			     				}
			     			}
			     		}else{
			     			currnode.remove();
			     		}
		     		}
     		}
     		tmpflag=true;
     	}
     	if(!tmpflag){
	     	if(oldcate!=categories){
	     		if(oldcate==''){
	     				var tmpnode;
		     			if(oldstatus!=1&&oldstatus!=2){
		     				tmpnode=rootnode.childNodes[1];
		     			}else{
		     				tmpnode=rootnode.childNodes[0];
		     			}
		     			//alert(tmpnode.load);
		     			if(tmpnode.load)
		     			for(var i=tmpnode.childNodes.length-1;i>=0;i--){
		     				if(tmpnode.childNodes[i].uid==codesetid){
		     					tmpnode.childNodes[i].remove();
		     					break;
		     				}
		     			}
	     		}else{
		     		if(currnode.uid!=oldcate){
		     			var tmpnode;
		     			if(oldstatus!=1&&oldstatus!=2){
		     				tmpnode=rootnode.childNodes[1];
		     			}else{
		     				tmpnode=rootnode.childNodes[0];
		     			}
		     			if(tmpnode.load)
		     			for(var i=tmpnode.childNodes.length-1;i>=0;i--){
		     				if(tmpnode.childNodes[i].uid==oldcate){
		     					currnode=tmpnode.childNodes[i];
		     					break;
		     				}
		     			}
		     		}
		     		//alert(currnode.uid);
		     		//alert(currnode.load);
		     		if(currnode.uid==oldcate&&!currnode.load){
		     			currnode.expand();
		     			if(currnode.childNodes.length==0)
		     				currnode.remove();
		     		}else if(currnode.uid==oldcate){
			     		//alert(currnode.childNodes.length);
			     		//return;
			     		if(currnode.childNodes.length>1){
			     			for(var i=currnode.childNodes.length-1;i>=0;i--){
			     				if(codesetid==currnode.childNodes[i].uid){
			     					currnode.childNodes[i].remove();
			     					break;
			     				}
			     			}
			     		}else{
			     			currnode.remove();
			     		}
		     		}
	     		}
	     	}
     	}*/
     	//return;
     	currnode=currnode.root();
     	if(oldcate!=categories){
     	if(oldcate.length==0){
     		if(currnode.load)
		     			for(var i=currnode.childNodes.length-1;i>=0;i--){
		     				if(currnode.childNodes[i].uid==codesetid){
		     					currnode.childNodes[i].remove();
		     					break;
		     				}
		     			}
     	}else{
     		if(currnode.load)
		     			for(var i=currnode.childNodes.length-1;i>=0;i--){
		     				if(currnode.childNodes[i].uid==oldcate){
		     					var tmpnode=currnode.childNodes[i];
		     					if(tmpnode.load){
						     		if(tmpnode.childNodes.length>1){
						     			for(var i=tmpnode.childNodes.length-1;i>=0;i--){
						     				if(codesetid==tmpnode.childNodes[i].uid){
						     					tmpnode.childNodes[i].remove();
						     					break;
						     				}
						     			}
						     			break;
						     		}else{
						     			tmpnode.remove();
						     			break;
						     		}
					     		}else{
					     			break;
					     		}
		     				}
		     			}
     	}
     	/*if(states==1||states==2){
     		currnode= currnode.childNodes[0];
     	}else{
     		currnode = currnode.childNodes[1];
     	}*/
     	if(currnode.load){
     			var ishave=false;
     			//alert(currnode.childNodes.length);
     			if(categories.length>0){
	     			for(var i=currnode.childNodes.length-1;i>=0;i--){
	     				//alert(currnode.childNodes[i].icon+"   "+categories);
	     				if(currnode.childNodes[i].uid==categories&&currnode.childNodes[i].icon=='/images/close.png'){
	     					ishave=true;
	     					currnode=currnode.childNodes[i];
	     					break;
	     				}
	     			}
	     			//alert(ishave);
	     			if(ishave){
	     				//var tmp = new xtreeItem(codesetid,codesetid+" "+codesetname,"/pos/posbusiness/searchposbusinesslist.do?b_query=link&full=1&a_code="+codesetid,"mil_body",codesetname,"/images/prop_ps.gif","/servlet/codesettree?flag=3&codesetid="+codesetid+"&parentid=-1");
						//currnode.add(tmp);
						if(currnode.load)
						while(currnode.childNodes.length){
							//alert(currnode.childNodes[0].uid);
							currnode.childNodes[0].remove();
						}
						currnode.load=true;
						currnode.loadChildren();
						currnode.reload(1);
	     			}else{
	     				//var tmp = new xtreeItem(categories,categories,"/system/codemaintence/codetree.do?b_search=link&status=${codeMaintenceForm.status}&categories="+getEncodeStr(categories),"mil_body",categories,"/images/close.png","/servlet/codesettree?flag=c&status=1&categories="+getEncodeStr(categories));
	     				//currnode.add(tmp);
	     				if(currnode.load)
						while(currnode.childNodes.length){
							//alert(currnode.childNodes[0].uid);
							currnode.childNodes[0].remove();
						}
						currnode.load=true;
						currnode.loadChildren();
						currnode.reload(1);
	     			}
     			}else{
     				//var tmp = new xtreeItem(codesetid,codesetid+" "+codesetname,"/pos/posbusiness/searchposbusinesslist.do?b_query=link&full=1&a_code="+codesetid,"mil_body",codesetname,"/images/prop_ps.gif","/servlet/codesettree?flag=3&codesetid="+codesetid+"&parentid=-1");
					//currnode.add(tmp);
					//tmp.expand();
					if(currnode.load)
						while(currnode.childNodes.length){
							//alert(currnode.childNodes[0].uid);
							currnode.childNodes[0].remove();
						}
						currnode.load=true;
						currnode.loadChildren();
						currnode.reload(1);
     			}
     	}
     	}
     	if(oldcate == categories)//没有修改分类
     		codeMaintenceForm.action = "/system/codemaintence/codetree.do?b_search=link&status=${codeMaintenceForm.status}&categories="+$URL.encode('<%=categories%>');/*+getEncodeStr(categories)*///加载的代码树 分类数据 wangb 20180813
        else //修改了分类
     		codeMaintenceForm.action = "/system/codemaintence/codetree.do?b_search=link&status=${codeMaintenceForm.status}&categories="+$URL.encode(getEncodeStr(categories));//加载的代码树 分类数据 wangb 20180813
      codeMaintenceForm.submit();
      //currnode=currnode.root();
     	/*var currnode1 = currnode.childNodes[0];
     	var currnode2 = currnode.childNodes[1];
		if(states==1||states==2){//系统代码
			currnode1.expand();
			if(categories.length>0){
				for(var i=0;i<=currnode1.childNodes.length-1;i++){
						if(categories==currnode1.childNodes[i].uid){
							currnode1.childNodes[i].expand();
							break;
						}			
				}
			}
		}else{
			currnode2.expand();
			if(categories.length>0){
				for(var i=0;i<=currnode2.childNodes.length-1;i++){
						if(categories==currnode2.childNodes[i].uid){
							currnode2.childNodes[i].expand();
							break;
						}			
				}
			}
		}*/
     	
     	//alert(currnode.uid=="root");
     	/*if(currnode.uid=="root"){
     		var currnode1 = currnode.childNodes[0];
     		var currnode2 = currnode.childNodes[1];
     		var currnodetemp = new Object();
     		//alert(flag);
     		if(flag==0)
     		{//修改系统代码类
     			if(categories.length==0){
	     			if(currnode1.load)
						for(var i=0;i<=currnode1.childNodes.length-1;i++){
							if(codesetid==currnode1.childNodes[i].uid)
								currnode1.childNodes[i].setText(codesetid+" "+codesetname);
			   			}
			   		if(currnode2.load)
						for(var i=0;i<=currnode2.childNodes.length-1;i++){
							if(codesetid==currnode2.childNodes[i].uid)
								currnode2.childNodes[i].setText(codesetid+" "+codesetname);
			   			}
		   		}else{
		   			if(oldcate!=categories){
		   				if(states==1||states==2)
     					{//用户代码
     					
     					}else{
     					
     					}
		   			}
		   		}
     		}
     		else
     		{	//alert(states);
     			if(states==1||states==2)
     			{//用户代码
     				if(currnode2.load){
						for(var i=0;i<=currnode2.childNodes.length-1;i++){
							if(codesetid==currnode2.childNodes[i].uid){
								currnodetemp.uid = currnode2.childNodes[i].uid;
								currnodetemp.action = currnode2.childNodes[i].action;
								currnodetemp.title = codesetname;
								currnodetemp.Xml = currnode2.childNodes[i].Xml;
								currnodetemp.text = codesetid+" "+codesetname;
								currnode2.childNodes[i].remove();
								break;
							}
			   			}
			   			if(currnode1.load){
			   				currnode1.select(true);
		   					parent.frames['mil_menu'].add(currnodetemp.uid,currnodetemp.text,currnodetemp.action,"mil_body",currnodetemp.title,"/images/close.png",currnodetemp.Xml);
				   			currnode.select(true);
		   				}
		   			}
		   			else{
			   			if(currnode1.load){
				   			currnode1.load = false;
				   			currnode1.expand();
				   			currnode.select(true);
		   				}
	   				}
     			}else
     			{
     				if(currnode1.load){
						for(var i=0;i<=currnode1.childNodes.length-1;i++){
							if(codesetid==currnode1.childNodes[i].uid){
								currnodetemp.uid = currnode1.childNodes[i].uid;
								currnodetemp.action = currnode1.childNodes[i].action;
								currnodetemp.title = codesetname;
								currnodetemp.Xml = currnode1.childNodes[i].Xml;
								currnodetemp.text = codesetid+" "+codesetname;
								currnode1.childNodes[i].remove();
							}
			   			}
			   			if(currnode2.load){
			   				currnode2.select(true);
		   					parent.frames['mil_menu'].add(currnodetemp.uid,currnodetemp.text,currnodetemp.action,"mil_body",currnodetemp.title,"/images/close.png",currnodetemp.Xml);
				   			currnode.select(true);
		   				}
			   		}else
		   			{
			   			if(currnode2.load){
				   			currnode2.load = false;
				   			currnode2.expand();
				   			currnode.select(true);
		   				}
	   				}
     			}
     		}
	        
   		}else
   		{
   			//alert(flag);
   			if(flag==0){
	   			if(currnode.load)
					for(var i=0;i<=currnode.childNodes.length-1;i++){
						if(codesetid==currnode.childNodes[i].uid)
							currnode.childNodes[i].setText(codesetid+" "+codesetname);
					}
			}else
			{
				var currnodetemp = new Object();
				var root = currnode.root();
				var currnode1 = root.childNodes[0];
     			var currnode2 = root.childNodes[1];
     			//alert(currnode.uid == currnode1.uid);
     			if(currnode.uid == currnode2.uid)
     			{	//alert(currnode1.title);
     				//alert(currnode1.load);
     				if(currnode1.load){
						for(var i=0;i<=currnode1.childNodes.length-1;i++){
							if(codesetid==currnode1.childNodes[i].uid){
								currnodetemp.uid = currnode1.childNodes[i].uid;
								currnodetemp.action = currnode1.childNodes[i].action;
								currnodetemp.title = codesetname;
								currnodetemp.Xml = currnode1.childNodes[i].Xml;
								currnodetemp.text = codesetid+" "+codesetname;
								//alert(currnodetemp.uid+"**"+currnodetemp.action+"**"+currnodetemp.title+"**"+currnodetemp.Xml);
								currnode1.childNodes[i].remove();
								break;
							}
			   			}
			   			if(currnode2.load){
			   				currnode2.select(true);
		   					parent.frames['mil_menu'].add(currnodetemp.uid,currnodetemp.text,currnodetemp.action,"mil_body",currnodetemp.title,"/images/close.png",currnodetemp.Xml);
				   			//currnode2.load = false;
				   			//currnode2.expand();
				   			currnode1.select(true);
		   				}
		   			}else{
			   			if(currnode2.load){
				   			currnode2.load = false;
				   			currnode2.expand();
				   			currnode1.select();
			   			}
		   			}
     			}else if(currnode.uid == currnode1.uid)
     			{	
     				//alert("ccc");
     				//alert(currnode2.load);
     				if(currnode2.load){
						for(var i=0;i<=currnode2.childNodes.length-1;i++){
							if(codesetid==currnode2.childNodes[i].uid){
								currnodetemp.uid = currnode2.childNodes[i].uid;
								currnodetemp.action = currnode2.childNodes[i].action;
								currnodetemp.title = codesetname;
								currnodetemp.Xml = currnode2.childNodes[i].Xml;
								currnodetemp.text = codesetid+" "+codesetname;
								currnode2.childNodes[i].remove();
								break;
							}
			   			}
			   			if(currnode1.load){
			   				currnode1.select(true);
		   					parent.frames['mil_menu'].add(currnodetemp.uid,currnodetemp.text,currnodetemp.action,"mil_body",currnodetemp.title,"/images/close.png",currnodetemp.Xml);
				   			//currnode1.load = false;
				   			//currnode1.expand();
				   			currnode2.select(true);
		   				}
		   			}else{
			   			if(currnode1.load){
				   			currnode1.load = false;
				   			currnode1.expand();
				   			currnode2.select(true);
			   			}
		   			}
     			}
			}
   		}*/
   		
   		
  }
  function add_codeset()
  {
  	 var bflag=false;
     var currname=1;
     var currnode=parent.frames['mil_menu'].Global.selectedItem; 
     //alert(currnode.uid);
     var currid=currnode.uid;
     var tmpcategories="";
     if(currid!="root"&&currid!="00"&&currid!="02")
     	tmpcategories=currid;
     //alert(tmpcategories);
     var dw=540,dh=290,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
     // var return_vo= window.showModalDialog("/system/codemaintence/add_edit_codeset.do?b_query=link&query=query&categories="+tmpcategories, bflag,
     //    "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
      //alert(return_vo);
      return_vo ='';
      var theUrl = "/system/codemaintence/add_edit_codeset.do?b_query=link&query=query&categories="+$URL.encode(tmpcategories);
      Ext.create('Ext.window.Window', {
          id:'add_codeset',
          height: 290,
          width: 570,
          title:'<bean:message key="codemaintence.codeset.add" />',
          resizable:false,
          modal:true,
          autoScroll:false,
          autoShow:true,
          html:'<iframe style="background-color:#ffffff " frameborder="0" scrolling="no" height="100%" width="100%" src="'+theUrl+'"></iframe>',
          renderTo:Ext.getBody(),
          listeners: {
              'close': function () {
                  if (this.return_vo) {
                      var codesetid = this.return_vo.codesetid;
                      var codesetname = this.return_vo.codesetdesc;
                      var states = this.return_vo.status;
                      var categories = this.return_vo.categories;

                      currnode = currnode.root();
                      /*if(currnode.load)
                      while(currnode.childNodes.length){
                      //alert(currnode.childNodes[0].uid);
                      currnode.childNodes[0].remove();
                      }
                      currnode.load=true;
                      currnode.loadChildren();
                      currnode.reload(1);*/

                      if (currnode.uid == "root") {
                          if (categories.length == 0) {
                              parent.frames['mil_menu'].add(codesetid, codesetid + " " + codesetname, "/pos/posbusiness/searchposbusinesslist.do?b_query=link&full=1&a_code=" + codesetid, "mil_body", codesetname,
                                  "/images/prop_ps.gif", "/servlet/codesettree?flag=3&codesetid=" + codesetid + "&parentid=-1");
                              codeMaintenceForm.action = "/system/codemaintence/codetree.do?b_search=link&encryptParam=<%=PubFunc.encrypt("status="+status+"&categories=")%>";//currnode2.action.replace('link','aa');
                              codeMaintenceForm.submit();
                          } else {
                              var ishave = false;
                              for (var i = 0; i <= currnode.childNodes.length - 1; i++) {
                                  if (categories.toUpperCase() == currnode.childNodes[i].uid.toUpperCase()) {
                                      if (currnode.childNodes[i].load)
                                          for (var j = 0; j <= currnode.childNodes[i].childNodes.length - 1; j++) {
                                              currnode.childNodes[i].childNodes[j].remove();
                                          }
                                      if (currnode.childNodes[i].load) {
                                          while (currnode.childNodes[i].childNodes.length) {
                                              currnode.childNodes[i].childNodes[0].remove();
                                          }
                                          currnode.childNodes[i].load = true;
                                          currnode.childNodes[i].loadChildren();
                                          currnode.childNodes[i].reload(1);
                                      } else
                                          currnode.childNodes[i].expand();
                                      ishave = true;
                                          codeMaintenceForm.action = "/system/codemaintence/codetree.do?b_search=link&status=${codeMaintenceForm.status}&categories=" + $URL.encode(getEncodeStr(categories));
                                          codeMaintenceForm.submit();
                                      break;
                                  }
                              }
                              if (!ishave)
                                  parent.frames['mil_menu'].add2(categories, categories, "/system/codemaintence/codetree.do?b_search=link&status=${codeMaintenceForm.status}&categories=" + $URL.encode(getEncodeStr(categories)), "mil_body", categories,
                                      "/images/close.png", "/servlet/codesettree?flag=c&status=1&categories=" + $URL.encode(getEncodeStr(categories)));
                              codeMaintenceForm.action =  "/system/codemaintence/codetree.do?b_search=link&status=${codeMaintenceForm.status}&categories=" + $URL.encode(getEncodeStr(categories));
                              codeMaintenceForm.submit();
                          }

                          var currnode1 = currnode.childNodes[1];
                          var currnode2 = currnode.childNodes[0];
                          //if(states=="2")
                          if (states == "2" || states == "1") {//系统代码
                              currnode2.select();
                              if (currnode2.load) {
                                  if (categories.length == 0) {
                                      parent.frames['mil_menu'].add(codesetid, codesetid + " " + codesetname, "/pos/posbusiness/searchposbusinesslist.do?b_query=link&full=1&a_code=" + codesetid, "mil_body", codesetname,
                                          "/images/prop_ps.gif", "/servlet/codesettree?flag=3&codesetid=" + codesetid + "&parentid=-1");
                                      codeMaintenceForm.action = "/system/codemaintence/codetree.do?b_search=link&encryptParam=<%=PubFunc.encrypt("status="+status+"&categories=")%>";//currnode2.action.replace('link','aa');
                                      codeMaintenceForm.submit();
                                  } else {
                                      var ishave = false;
                                      for (var i = 0; i <= currnode2.childNodes.length - 1; i++) {
                                          if (categories.toUpperCase() == currnode2.childNodes[i].uid.toUpperCase()) {
                                              if (currnode2.childNodes[i].load)
                                                  for (var j = 0; j <= currnode2.childNodes[i].childNodes.length - 1; j++) {
                                                      currnode2.childNodes[i].childNodes[j].remove();
                                                  }
                                              if (currnode2.childNodes[i].load) {
                                                  while (currnode2.childNodes[i].childNodes.length) {
                                                      currnode2.childNodes[i].childNodes[0].remove();
                                                  }
                                                  currnode2.childNodes[i].load = true;
                                                  currnode2.childNodes[i].loadChildren();
                                                  currnode2.childNodes[i].reload(1);
                                              } else
                                                  currnode2.childNodes[i].expand();
                                              ishave = true;
                                              codeMaintenceForm.action =  "/system/codemaintence/codetree.do?b_search=link&status=${codeMaintenceForm.status}&categories=" + $URL.encode(getEncodeStr(categories));
                                              codeMaintenceForm.submit();
                                              break;
                                          }
                                      }
                                      if (!ishave)
                                          parent.frames['mil_menu'].add2(categories, categories, "/system/codemaintence/codetree.do?b_search=link&status=${codeMaintenceForm.status}&categories=" + $URL.encode(getEncodeStr(categories)), "mil_body", categories,
                                              "/images/close.png", "/servlet/codesettree?flag=c&status=1&categories=" + $URL.encode(getEncodeStr(categories)));
                                      codeMaintenceForm.action = "/system/codemaintence/codetree.do?b_search=link&status=${codeMaintenceForm.status}&categories=" + $URL.encode(getEncodeStr(categories));
                                      codeMaintenceForm.submit();
                                  }
                              }
                          } else {
                              currnode1.select();
                              if (currnode1.load) {
                                  if (categories.length == 0) {
                                      parent.frames['mil_menu'].add(codesetid, codesetid + " " + codesetname, "/pos/posbusiness/searchposbusinesslist.do?b_query=link&full=1&a_code=" + codesetid, "mil_body", codesetname,
                                          "/images/prop_ps.gif", "/servlet/codesettree?flag=3&codesetid=" + codesetid + "&parentid=-1");
                                      codeMaintenceForm.action = "/system/codemaintence/codetree.do?b_search=link&encryptParam=<%=PubFunc.encrypt("status="+status+"&categories=")%>";//currnode1.action.replace('link','aa');
                                      codeMaintenceForm.submit();
                                  } else {
                                      var ishave = false;
                                      for (var i = 0; i <= currnode1.childNodes.length - 1; i++) {
                                          if (categories.toUpperCase() == currnode1.childNodes[i].uid.toUpperCase()) {
                                              if (currnode1.childNodes[i].load)
                                                  for (var j = 0; j <= currnode1.childNodes[i].childNodes.length - 1; j++) {
                                                      currnode1.childNodes[i].childNodes[j].remove();
                                                  }
                                              if (currnode1.childNodes[i].load) {
                                                  while (currnode1.childNodes[i].childNodes.length) {
                                                      currnode1.childNodes[i].childNodes[0].remove();
                                                  }
                                                  currnode1.childNodes[i].load = true;
                                                  currnode1.childNodes[i].loadChildren();
                                                  currnode1.childNodes[i].reload(1);
                                              } else
                                                  currnode1.childNodes[i].expand();
                                              ishave = true;
                                              codeMaintenceForm.action = "/system/codemaintence/codetree.do?b_search=link&status=${codeMaintenceForm.status}&categories=" + $URL.encode(getEncodeStr(categories));
                                              codeMaintenceForm.submit();
                                              break;
                                          }
                                      }
                                      if (!ishave)
                                          parent.frames['mil_menu'].add2(categories, categories, "/system/codemaintence/codetree.do?b_search=link&status=${codeMaintenceForm.status}&categories=" + $URL.encode(getEncodeStr(categories)), "mil_body", categories,
                                              "/images/close.png", "/servlet/codesettree?flag=c&status=2&categories=" + $URL.encode(getEncodeStr(categories)));
                                      codeMaintenceForm.action  = "/system/codemaintence/codetree.do?b_search=link&status=${codeMaintenceForm.status}&categories=" + $URL.encode(getEncodeStr(categories));
                                      codeMaintenceForm.submit();
                                  }
                              }
                          }
                      } else {
                          if (currnode.load) {
                              parent.frames['mil_menu'].add(codesetid, codesetid + " " + codesetname, "/pos/posbusiness/searchposbusinesslist.do?b_query=link&full=1&a_code=" + codesetid, "mil_body", codesetname,
                                  "/images/prop_ps.gif", "/maintence/codetree?params=child&codesetid=" + codesetid + "&target=mil_body");
                              codeMaintenceForm.action = currnode.action.replace('link', 'aa');
                              codeMaintenceForm.submit();
                          }
                      }
                  }
              }
          }

      });
     // if(!return_vo||return_vo==null)
  	//  	return ;
       
        <%--var codesetid  = return_vo.codesetid;--%>
     	<%--var codesetname=return_vo.codesetdesc;--%>
     	<%--var states = return_vo.status;--%>
     	<%--var categories=return_vo.categories;--%>
     	<%----%>
     <%--currnode = currnode.root();	--%>
		<%--/*if(currnode.load)--%>
		<%--while(currnode.childNodes.length){--%>
			<%--//alert(currnode.childNodes[0].uid);--%>
			<%--currnode.childNodes[0].remove();--%>
		<%--}--%>
		<%--currnode.load=true;--%>
		<%--currnode.loadChildren();--%>
		<%--currnode.reload(1);*/--%>

     	<%--if(currnode.uid=="root"){--%>
     		<%--if(categories.length==0){--%>
	     				<%--parent.frames['mil_menu'].add(codesetid,codesetid+" "+codesetname,"/pos/posbusiness/searchposbusinesslist.do?b_query=link&full=1&a_code="+codesetid,"mil_body",codesetname,--%>
		 				<%--"/images/prop_ps.gif","/servlet/codesettree?flag=3&codesetid="+codesetid+"&parentid=-1");--%>
		 				<%--codeMaintenceForm.document.location = "/system/codemaintence/codetree.do?b_search=link&encryptParam=<%=PubFunc.encrypt("status="+status+"&categories=")%>";//currnode2.action.replace('link','aa');--%>
	 				<%--}else{--%>
	 					<%--var ishave=false;--%>
	 					<%--for(var i=0;i<=currnode.childNodes.length-1;i++){--%>
							<%--if(categories.toUpperCase()==currnode.childNodes[i].uid.toUpperCase()){--%>
								<%--if(currnode.childNodes[i].load)--%>
								<%--for(var j=0;j<=currnode.childNodes[i].childNodes.length-1;j++){--%>
										<%--currnode.childNodes[i].childNodes[j].remove();--%>
								<%--}--%>
								<%--if(currnode.childNodes[i].load){--%>
										<%--while(currnode.childNodes[i].childNodes.length){--%>
											<%--currnode.childNodes[i].childNodes[0].remove();--%>
										<%--}--%>
										<%--currnode.childNodes[i].load=true;--%>
										<%--currnode.childNodes[i].loadChildren();--%>
										<%--currnode.childNodes[i].reload(1);--%>
								<%--}else--%>
									<%--currnode.childNodes[i].expand();--%>
								<%--ishave=true;--%>
								<%--codeMaintenceForm.document.location = "/system/codemaintence/codetree.do?b_search=link&status=${codeMaintenceForm.status}&categories="+getEncodeStr(categories);;--%>
								<%--break;--%>
							<%--}--%>
						<%--}--%>
						<%--if(!ishave)--%>
	 					<%--parent.frames['mil_menu'].add2(categories,categories,"/system/codemaintence/codetree.do?b_search=link&status=${codeMaintenceForm.status}&categories="+getEncodeStr(categories),"mil_body",categories,--%>
		 				<%--"/images/close.png","/servlet/codesettree?flag=c&status=1&categories="+getEncodeStr(categories));--%>
		 				<%--codeMaintenceForm.document.location = "/system/codemaintence/codetree.do?b_search=link&status=${codeMaintenceForm.status}&categories="+getEncodeStr(categories);--%>
	 				<%--}--%>
	 				<%----%>
	 				<%--return;--%>
     		<%--var currnode1 = currnode.childNodes[1];--%>
     		<%--var currnode2 = currnode.childNodes[0];--%>
     		<%--//if(states=="2")--%>
     		<%--if(states=="2"||states=="1"){//系统代码--%>
     			<%--currnode2.select();--%>
     			<%--if(currnode2.load){--%>
     				<%--if(categories.length==0){--%>
	     				<%--parent.frames['mil_menu'].add(codesetid,codesetid+" "+codesetname,"/pos/posbusiness/searchposbusinesslist.do?b_query=link&full=1&a_code="+codesetid,"mil_body",codesetname,--%>
		 				<%--"/images/prop_ps.gif","/servlet/codesettree?flag=3&codesetid="+codesetid+"&parentid=-1");--%>
		 				<%--codeMaintenceForm.document.location = "/system/codemaintence/codetree.do?b_search=link&encryptParam=<%=PubFunc.encrypt("status="+status+"&categories=")%>";//currnode2.action.replace('link','aa');--%>
	 				<%--}else{--%>
	 					<%--var ishave=false;--%>
	 					<%--for(var i=0;i<=currnode2.childNodes.length-1;i++){--%>
							<%--if(categories.toUpperCase()==currnode2.childNodes[i].uid.toUpperCase()){--%>
								<%--if(currnode2.childNodes[i].load)--%>
								<%--for(var j=0;j<=currnode2.childNodes[i].childNodes.length-1;j++){--%>
										<%--currnode2.childNodes[i].childNodes[j].remove();--%>
								<%--}--%>
								<%--if(currnode2.childNodes[i].load){--%>
										<%--while(currnode2.childNodes[i].childNodes.length){--%>
											<%--currnode2.childNodes[i].childNodes[0].remove();--%>
										<%--}--%>
										<%--currnode2.childNodes[i].load=true;--%>
										<%--currnode2.childNodes[i].loadChildren();--%>
										<%--currnode2.childNodes[i].reload(1);--%>
								<%--}else--%>
									<%--currnode2.childNodes[i].expand();--%>
								<%--ishave=true;--%>
								<%--codeMaintenceForm.document.location = "/system/codemaintence/codetree.do?b_search=link&status=${codeMaintenceForm.status}&categories="+getEncodeStr(categories);;--%>
								<%--break;--%>
							<%--}--%>
						<%--}--%>
						<%--if(!ishave)--%>
	 					<%--parent.frames['mil_menu'].add2(categories,categories,"/system/codemaintence/codetree.do?b_search=link&status=${codeMaintenceForm.status}&categories="+getEncodeStr(categories),"mil_body",categories,--%>
		 				<%--"/images/close.png","/servlet/codesettree?flag=c&status=1&categories="+getEncodeStr(categories));--%>
		 				<%--codeMaintenceForm.document.location = "/system/codemaintence/codetree.do?b_search=link&status=${codeMaintenceForm.status}&categories="+getEncodeStr(categories);--%>
	 				<%--}--%>
	 			<%--}--%>
     		<%--}else{--%>
     			<%--currnode1.select();--%>
		        <%--if(currnode1.load){--%>
		        	<%--if(categories.length==0){--%>
			        	<%--parent.frames['mil_menu'].add(codesetid,codesetid+" "+codesetname,"/pos/posbusiness/searchposbusinesslist.do?b_query=link&full=1&a_code="+codesetid,"mil_body",codesetname,--%>
		 				<%--"/images/prop_ps.gif","/servlet/codesettree?flag=3&codesetid="+codesetid+"&parentid=-1");--%>
		 				<%--codeMaintenceForm.document.location = "/system/codemaintence/codetree.do?b_search=link&encryptParam=<%=PubFunc.encrypt("status="+status+"&categories=")%>";//currnode1.action.replace('link','aa');--%>
		 			<%--}else{--%>
		 				<%--var ishave=false;--%>
	 					<%--for(var i=0;i<=currnode1.childNodes.length-1;i++){--%>
							<%--if(categories.toUpperCase()==currnode1.childNodes[i].uid.toUpperCase()){--%>
								<%--if(currnode1.childNodes[i].load)--%>
								<%--for(var j=0;j<=currnode1.childNodes[i].childNodes.length-1;j++){--%>
										<%--currnode1.childNodes[i].childNodes[j].remove();--%>
								<%--}--%>
								<%--if(currnode1.childNodes[i].load){--%>
										<%--while(currnode1.childNodes[i].childNodes.length){--%>
											<%--currnode1.childNodes[i].childNodes[0].remove();--%>
										<%--}--%>
										<%--currnode1.childNodes[i].load=true;--%>
										<%--currnode1.childNodes[i].loadChildren();--%>
										<%--currnode1.childNodes[i].reload(1);--%>
								<%--}else--%>
									<%--currnode1.childNodes[i].expand();--%>
								<%--ishave=true;--%>
								<%--codeMaintenceForm.document.location = "/system/codemaintence/codetree.do?b_search=link&status=${codeMaintenceForm.status}&categories="+getEncodeStr(categories);;--%>
								<%--break;--%>
							<%--}--%>
						<%--}--%>
						<%--if(!ishave)--%>
		 				<%--parent.frames['mil_menu'].add2(categories,categories,"/system/codemaintence/codetree.do?b_search=link&status=${codeMaintenceForm.status}&categories="+getEncodeStr(categories),"mil_body",categories,--%>
		 				<%--"/images/close.png","/servlet/codesettree?flag=c&status=2&categories="+getEncodeStr(categories));--%>
		 				<%--codeMaintenceForm.document.location = "/system/codemaintence/codetree.do?b_search=link&status=${codeMaintenceForm.status}&categories="+getEncodeStr(categories);;--%>
		 			<%--}--%>
	 			<%--}--%>
		   	<%--}--%>
   		<%--}else--%>
   		<%--{--%>
   			<%--if(currnode.load){--%>
				<%--parent.frames['mil_menu'].add(codesetid,codesetid+" "+codesetname,"/pos/posbusiness/searchposbusinesslist.do?b_query=link&full=1&a_code="+codesetid,"mil_body",codesetname,--%>
	 				<%--"/images/prop_ps.gif","/maintence/codetree?params=child&codesetid="+codesetid+"&target=mil_body");--%>
	 			<%--codeMaintenceForm.document.location = currnode.action.replace('link','aa');--%>
	 		<%--}--%>
   		<%--}--%>
  }
  function deletecodes()
  {
  	var len=document.codeMaintenceForm.elements.length;
		var uu;
		var list = new Array();
		var x = 0;
		for (i=0;i<len;i++)
		{
		   if (document.codeMaintenceForm.elements[i].type=="checkbox")
		   {
		      if(document.codeMaintenceForm.elements[i].checked==true)
		      {
		      	if(document.codeMaintenceForm.elements[i].name=="selbox")
		      		continue;
		        uu="dd";
                  if(!getBrowseVersion() || getBrowseVersion() =='10'){
                      list[x] = document.codeMaintenceForm.elements[i].parentElement.nextElementSibling.innerText.trim();
                  }else{
                      list[x] = (document.codeMaintenceForm.elements[i].parentElement.nextSibling.innerHTML).substring(6);
                  }
		        x=x+1;
		       }
		   }
		}
		if(uu!="dd")
		{
		  alert(CHOISE_DELETE_NOT);
		  return false;
		}
		if(!confirm(CONFIRMATION_DEL))
  			return false;
		var hashvo=new ParameterSet();
		hashvo.setValue("list",list);
		var request=new Request({asynchronous:false,onSuccess:deletes,functionId:'1010050014'},hashvo)
  }
  function deletecode()
  {
		var len=document.codeMaintenceForm.elements.length;
		var uu;
		var list = new Array();
		var x = 0;
		for (i=0;i<len;i++)
		{
		   if (document.codeMaintenceForm.elements[i].type=="checkbox")
		   {
		      if(document.codeMaintenceForm.elements[i].checked==true)
		      {
		      	if(document.codeMaintenceForm.elements[i].name=="selbox")
		      		continue;
		        uu="dd";
		        if(!getBrowseVersion() || getBrowseVersion() =='10'){
                    list[x] = document.codeMaintenceForm.elements[i].parentElement.nextElementSibling.innerText.trim();
                }else{
                    list[x] = (document.codeMaintenceForm.elements[i].parentElement.nextSibling.innerHTML).substring(6);
                }
		        x=x+1;
		       }
		   }
		}
		if(uu!="dd")
		{
		  alert(CHOISE_DELETE_NOT);
		  return false;
		}
		/*if(!confirm(CONFIRMATION_DEL))
  			return false;*/
		var hashvo=new ParameterSet();
		hashvo.setValue("list",list);
		var request=new Request({asynchronous:false,onSuccess:delete_ok,functionId:'1010050009'},hashvo)
  }
  function deletes(outparamters)
  {
  	var flag=outparamters.getValue("msg");
  	if(flag=="0")
  	{
  		alert("系统代码类不允许删除");
  		return;
  	}
  	if(flag=="1")
  	{
  		deletecode();
  	}
  }
  function delete_ok(outparamters)
  {
	var flag=outparamters.getValue("delflag");
	if(flag=="0"){
		alert('<bean:message key="codemaintence.code.delmessagesuc" />');
	}
	if(flag=="1")
	{
		alert('<bean:message key="codemaintence.code.delmessagefal" />');
		return;
	}
	var currnode=parent.frames['mil_menu'].Global.selectedItem;
	//currnode = currnode.root();
	var list = outparamters.getValue("list");
	for(var i=0;i<list.length;i++){
		var codesetid = trim(list[i]);
		/*if(currnode.uid=="root"){
			var currnode1 = currnode.childNodes[0];
			var currnode2 = currnode.childNodes[1];
		    if(currnode1.load){
		    	//currnode = currnode1;
				for(var j=0;j<=currnode1.childNodes.length-1;j++){
					if(codesetid==currnode1.childNodes[j].uid){
						currnode1.childNodes[j].remove();
						break;
					}else{
						var ishave=false;
						if(currnode1.childNodes[j].load){
							for(var n=0;n<=currnode1.childNodes[j].childNodes.length-1;n++){
								if(codesetid==currnode1.childNodes[j].childNodes[n].uid){
									currnode1.childNodes[j].childNodes[n].remove();
									ishave=true;
									break;
								}
							}
						}
						if(ishave){
							if(currnode1.childNodes[j].childNodes.length==0){
								currnode1.childNodes[j].remove();
							}
							break;
						}
					}
				}
			}
			if(currnode2.load){
				//currnode = currnode2;
				for(var j=0;j<=currnode2.childNodes.length-1;j++){
					if(codesetid==currnode2.childNodes[j].uid)
					{
						currnode2.childNodes[j].remove();
						break;
					}else{
						var ishave=false;
						if(currnode2.childNodes[j].load){
							for(var n=0;n<=currnode2.childNodes[j].childNodes.length-1;n++){
								if(codesetid==currnode2.childNodes[j].childNodes[n].uid){
									currnode2.childNodes[j].childNodes[n].remove();
									ishave=true;
									break;
								}
							}
						}
						if(ishave){
							if(currnode2.childNodes[j].childNodes.length==0){
								currnode2.childNodes[j].remove();
							}
							break;
						}
					}
				}
			}
		}else*/
		{
			if(currnode.load)
			for(var j=0;j<=currnode.childNodes.length-1;j++){
				if(codesetid==currnode.childNodes[j].uid)
				{
					currnode.childNodes[j].remove();
					break;
				}else{
						var ishave=false;
						if(currnode.childNodes[j].load){
							for(var n=0;n<=currnode.childNodes[j].childNodes.length-1;n++){
								if(codesetid==currnode.childNodes[j].childNodes[n].uid){
									currnode.childNodes[j].childNodes[n].remove();
									ishave=true;
									break;
								}
							}
						}
						if(ishave){
							if(currnode.childNodes[j].childNodes.length==0){
								currnode.childNodes[j].remove();
							}
							break;
						}
				}
			}
		}
	}
	codeMaintenceForm.action = currnode.action.replace('link','bb');
	codeMaintenceForm.submit();
  }
  function outputcode(){
      var len=document.codeMaintenceForm.elements.length;
      var x = 0;
      var uid; 
      for (i=0;i<len;i++)
	  {
		   if (document.codeMaintenceForm.elements[i].type=="checkbox"&&document.codeMaintenceForm.elements[i].name!='selbox')
		   {
		      if(document.codeMaintenceForm.elements[i].checked==true)
		      {
		        x=x+1;
                  if(!getBrowseVersion() || getBrowseVersion() =='10'){
                      uid = document.codeMaintenceForm.elements[i].parentElement.nextElementSibling.innerText.trim();
                  }else{
                      uid = document.codeMaintenceForm.elements[i].parentElement.nextSibling.innerHTML;
                  }
		        // uid = document.codeMaintenceForm.elements[i].parentElement.nextSibling.innerHTML;
		       }
		   }
	  }
	  if(x==0)
	  	alert("请选择一条记录");
	  else if(x>1)
	  	alert("抱歉，只能选择一条记录进行导出");
	  else if(x=1)
	  {
	  	var hashvo=new ParameterSet();
      	hashvo.setValue("uid",uid);
      	var request=new Request ({asynchronous:false,onSuccess:outputcode_ok,functionId:'1010050010'},hashvo);
      }
  }
  function outputcode_ok(outparamters){
	//xus 20/4/18 vfs改造
  	window.open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outparamters.getValue("fileid"));
  }
  function sercodeset(uid,title){
  	var uidtitle=getEncodeStr(title);
  	var dw=502,dh=300,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
    // var return_vo= window.showModalDialog("/system/codemaintence/serch_codeset.do?b_query=link&codesetid="+getEncodeStr(trim(uid))+"&currnodetext="+uidtitle,null,
    // "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
      var theUrl ="/system/codemaintence/serch_codeset.do?b_query=link&codesetid="+getEncodeStr(trim(uid))+"&currnodetext="+uidtitle;
      theUrl = encodeURI(theUrl);
      Ext.create('Ext.window.Window', {
          id:'sercodeset',
          height: 320,
          width: 520,
          resizable:false,
          modal:true,
          autoScroll:false,
          autoShow:true,
          html:'<iframe style="background-color:#ffffff " frameborder="0" scrolling="no" height="100%" width="100%" src="'+theUrl+'"></iframe>',
          renderTo:Ext.getBody()
      });
  }
  
  function nosercodeset(){
  var strurl="/system/codemaintence/serch_codeset.do?b_norelation=link";
   var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl;
   var dw=600,dh=500,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
	// var return_vo= window.showModalDialog(iframe_url,null,
     // "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
      if(!getBrowseVersion() || getBrowseVersion()=='10'){
          dw = 610;
          dh = 540;
      }else{
          dw = 630;
          dh = 560;
      }
      var theUrl = iframe_url;
      Ext.create('Ext.window.Window', {
          id:'nosercodeset',
          title:'未关联指标代码类',
          height: dh,
          width: dw,
          resizable:false,
          modal:true,
          autoScroll:false,
          autoShow:true,
          html:'<iframe style="background-color:#ffffff " frameborder="0" scrolling="no" height="100%" width="100%" src="'+theUrl+'"></iframe>',
          renderTo:Ext.getBody(),
      });
  }
  function selectNode(codesetid){
  	var currnode=parent.frames['mil_menu'].Global.selectedItem;
  	if(codesetid==currnode.uid)
  		return;
  	if(currnode.uid=="root"){
  		var currnode1=null;
  		if(/^\d*$/.test(codesetid)){
  			currnode1=currnode.childNodes[0];
  		}else{
  			currnode1=currnode.childNodes[1];
  		}
		currnode1.openURL();
		currnode1.expand();
		var nodes = currnode1.childNodes;
		for(var i=0;i<nodes.length;i++){
			if(nodes[i].uid==codesetid){
				var node = nodes[i];
				node.select();
			}
		}
	}else{
		currnode.openURL();
		currnode.expand();
		var nodes = currnode.childNodes;
		for(var i=0;i<nodes.length;i++){
			if(nodes[i].uid==codesetid){
				var node = nodes[i];
				node.select();
			}
		}
	}
  }
    function   NoExec()   
  	{   
          if(event.keyCode==13||event.keyCode==222)   event.returnValue=false; 
          document.onkeypress=NoExec;     
  	} 
//-->
</script>
<%
	int i=0;
%>
<html:form action="/system/codemaintence/codeshow"> 
	<table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0">
	 <tr>
	    <td align="left" nowrap>
	        代码类
	        <html:text name="codeMaintenceForm" property="selcodesetid" size="20" maxlength="50" onkeydown="NoExec()" styleClass="text4"></html:text>
	        <html:button styleClass="mybutton" style="position:absolute;" property="b_all" onclick="selectCode()"><bean:message key="button.query"/></html:button>  
	    </td>         
	 </tr>
	</table>
    <table width="80%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable" style="margin-top:5px;">
		  <THEAD>
		  	<tr>
            	<td align="center" class="TableRow" nowrap>
					<input type="checkbox" name="selbox" onclick="batch_select(this,'pagination.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;	        
				</td> 		  	
				<td align="center" class="TableRow" nowrap>
					<bean:message key="codemaintence.codeset.id" />
				</td>
				<td align="center" class="TableRow" nowrap>
					<bean:message key="codemaintence.codeset.desc" />
				</td>
				<td align="center" class="TableRow" nowrap>
					<bean:message key="codemaintence.codeset.maxlength" />
				</td>
				<td align="center" class="TableRow" nowrap>
					<bean:message key="codemaintence.codeset.status" />
				</td>
				<hrms:priv func_id="3007202">
		        <td align="center" class="TableRow" nowrap>
					<bean:message key="label.edit"/>            	
			    </td>	
			    </hrms:priv>			
			    <td align="center" class="TableRow" nowrap>
					<bean:message key="codemaintence.codeset.fieldname"/>            	
			    </td>
			</TR>
			</THEAD>
			<hrms:paginationdb id="element" name="codeMaintenceForm" sql_str="codeMaintenceForm.sqlf" table="" where_str="codeMaintenceForm.wheref" columns="codeMaintenceForm.columnf" order_by="order by codesetid" pagerows="${codeMaintenceForm.pagerows }" page_id="pagination" indexes="indexes">
			          <%
			          if(i%2==0)
			          {
			          %>
			          <tr class="trShallow" id="<%=i%>">
			          <%}
			          else
			          {%>
			          <tr class="trDeep" id="<%=i%>">
			          <%
			          }
			          i++;          
			          %>
		          	
		          	<td align="center" class="RecordRow" nowrap>
			             <hrms:checkmultibox name="codeMaintenceForm" property="pagination.select" value="true" indexes="indexes"/>&nbsp;
		            </td>			
					<td align="left" class="RecordRow" nowrap>
							&nbsp;<bean:write name="element" property="codesetid" />
					</td>
					<%
						DynaBean bean = (DynaBean)pageContext.getAttribute("element");
						String a_code = (String)bean.get("codesetid");
					 %>	
					<td align="left" class="RecordRow" nowrap>
							&nbsp;<a href="/pos/posbusiness/searchposbusinesslist.do?b_query=link&encryptParam=<%=PubFunc.encrypt("full=1&a_code="+a_code+"&param=") %>" onclick="return selectNode('<bean:write name="element" property="codesetid" />');"><bean:write name="element" property="codesetdesc" /></a>
					</td>										
					<td align="left" class="RecordRow" nowrap>
							&nbsp;<bean:write name="element" property="maxlength" />
					</td>	
					<td align="left" class="RecordRow" nowrap>
					<!-- 【4900】代码体系，代码类状态显示不对  jingq upd 2014.11.10 -->
					<logic:equal value="1" name="element" property="status" >
							    &nbsp;<bean:message key="codemaintence.code.unsys"/>
					</logic:equal>
					<logic:notEqual name="element" property="status" value="2">
						<logic:notEqual name="element" property="status" value="1">
							&nbsp;<bean:message key="codemaintence.code.user"/>
						</logic:notEqual>
				   　</logic:notEqual>
				    <logic:equal value="2" name="element" property="status" >
							    &nbsp;<bean:message key="codemaintence.code.sys"/>
					</logic:equal>
					</td>
					<hrms:priv func_id="3007202">
			        <td align="center" class="RecordRow" nowrap>
						<a href="javascript:update_codeset('<bean:write name="element" property="codesetid" />','<bean:write name="element" property="categories" />','<bean:write name="element" property="status" />')"><img src="/images/edit.gif" border=0></a>&nbsp;
				    </td>	
				    </hrms:priv>				
				    <td align="center" class="RecordRow" nowrap>
						<a href="javascript:sercodeset('<bean:write name="element" property="codesetid" />','<bean:write name="element" property="codesetdesc" />')"><img src="/images/view.gif" border=0></a>&nbsp;
				    </td>
		     </tr>
			</hrms:paginationdb>
			<tr>
				<td colspan='3' valign="bottom" align="left" nowrap class="tdFontcolor complex_border_color">
				<!-- 系统管理，代码体系，分页标签支持设置每页条数  jingq upd 2014.12.06 -->
					<hrms:paginationtag name="codeMaintenceForm" pagerows="${codeMaintenceForm.pagerows}" property="pagination" scope="page" refresh="true"></hrms:paginationtag>
				</td>
				<td colspan='4' align="right" nowrap class="tdFontcolor complex_border_color">
					<p align="right">
						<hrms:paginationdblink name="codeMaintenceForm" property="pagination" nameId="browseRegisterForm" scope="page">
						</hrms:paginationdblink>
					</p>
				</td>
			</tr>
		</table>
	<table width="80%" align="center">
		<tr>
		<td align="center" nowrap height="35px;">
			<hrms:priv func_id="3007201">
			<input type="button" name="ret" value="<bean:message key="button.new.add"/>" onclick="add_codeset()" class="mybutton"/>
			</hrms:priv>
			<hrms:priv func_id="3007203">
			<input type="button" name="ret" value="<bean:message key="button.delete"/>" onclick="deletecodes()" class="mybutton"/>
			</hrms:priv>
			<hrms:priv func_id="3007204">
			<input type="button" name="ret" value="<bean:message key="button.export"/>" onclick="outputcode()" class="mybutton"/>
			</hrms:priv>
			<hrms:priv func_id="3007205">
			<input type="button" name="ret" value="<bean:message key="button.import"/>" onclick="inputcode();" class="mybutton"/>
			</hrms:priv>
			<input type="button" name="ret" value="未关联指标代码类" onclick="nosercodeset();" class="mybutton"/>
		</td>
		</tr>
		</table>

</html:form>
