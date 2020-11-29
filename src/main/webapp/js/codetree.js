    var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	var ViewProperties=new ParameterSet();
  function addObject(){
  	var currnode=Global.selectedItem;
  	var uid=currnode.uid;
  	if(uid=="root"){
  	 add_codeset(0);
  	}else{
  	add_codeitem(0);
  	}
   }
   function updateObject(){
    var currnode=Global.selectedItem;
  	var uid=currnode.uid;
  	var temp =uid.split("/");
  	if(temp.length>1){
  	update_codeitem(1);
  	}else{
  	 update_codeset();
  	 }
   }
  function add_codeset_ok(outparamters)
  {
        var currnode=Global.selectedItem;  
    	var codesetid=outparamters.getValue("codesetid");
     	var codesetname=outparamters.getValue("codestname");
	 	var tmp = new xtreeItem(codesetid,codesetid+" "+codesetname,"/system/codemaintence/codetree.do?b_search=link&param=root&codesetid="+codesetid,"mil_body",codesetname,
	 	"/images/groups.gif","/maintence/codetree?params=child&codesetid="+codesetid+"&target=mil_body");
	 	currnode.add(tmp);
		parent.frames["mil_body"].location.reload();
   }
  function add_codeset(flag)
  {
 	 var currnode=Global.selectedItem; 
 	 if(currnode.uid!="root"){
 	 	return;
 	 } 
  	 var bflag=false;
     var currname=1;
     var return_vo= window.showModalDialog("/system/codemaintence/add_edit_codeset.do?b_query=link&query=query", bflag, 
        "dialogWidth:500px; dialogHeight:300px;resizable:no;center:yes;scroll:yes;status:no");       
     if(return_vo==null)
  	 	return ;
  	 /*????????????????????,??????????Object??????*/  	 
     var codesetvo=new Object();
     codesetvo.codesetid=return_vo.codesetid;
     codesetvo.codesetdesc=return_vo.codesetdesc;
     codesetvo.maxlength=return_vo.maxlength;
     codesetvo.status=return_vo.status;
     currname=currnode.uid;
     var hashvo=new ParameterSet();
     hashvo.setValue("codesetvo",codesetvo);
     hashvo.setValue("flag",flag);
     hashvo.setValue("codestname",codesetvo.codesetdesc);
     hashvo.setValue("codesetid",codesetvo.codesetid);
    var request=new Request({asynchronous:false,onSuccess:add_codeset_ok,functionId:'1010050008'},hashvo);    
  }
  function update_codeset_ok(outparamters)
  {
        var currnode=Global.selectedItem;  
    	var codesetid=currnode.uid;
     	var codesetname=outparamters.getValue("codestname");
        currnode.setText(codesetid+" "+codesetname);
	   	currnode.reload(1);
  }
  function update_codeset()
  {
     var currnode=Global.selectedItem; 
     if(currnode.uid=="root")
     	return;
     if(currnode.parent.uid!="root")
        return;
     var bflag=true;
     var theurl="/system/codemaintence/add_edit_codeset.do?b_query=link&codesetid="+currnode.uid;
     var return_vo= window.showModalDialog(theurl, bflag, 
        "dialogWidth:500px; dialogHeight:300px;resizable:no;center:yes;scroll:yes;status:no");
  	 if(return_vo==null)
  	 	return ;
  	 /*????????????????????,??????????Object??????*/
     var codesetvo=new Object();
     codesetvo.codesetid=return_vo.codesetid;
     codesetvo.codesetdesc=return_vo.codesetdesc;
     codesetvo.maxlength=return_vo.maxlength;
     codesetvo.status=return_vo.status;   
     currname=currnode.uid;
     var hashvo=new ParameterSet();
     hashvo.setValue("codesetvo",codesetvo);
     hashvo.setValue("flag",1);
     hashvo.setValue("uid",currname);
     hashvo.setValue("codestname",codesetvo.codesetdesc);
    var request=new Request({asynchronous:false,onSuccess:update_codeset_ok,functionId:'1010050008'},hashvo);        
  }
 
  function add_codeitem_ok(outparamters)
  {
        var currnode=Global.selectedItem;  
        var uid=currnode.uid;
        var uidlength=uid.split("/");
     	var codeitemdesc=outparamters.getValue("codeitemdesc");
     	var codeitemid=outparamters.getValue("codeitemid");
     	var codesetid=outparamters.getValue("codesetid");
     	var parentid=outparamters.getValue("parentid");
	 	var tmp = new xtreeItem(codeitemid+"/"+codesetid+"/"+parentid,
	 	codeitemdesc,
	 	("/system/codemaintence/codetree.do?b_search=link&param=child&codesetid="+codesetid+"&parentid="+codeitemid),
	 	"mil_body",
	 	codeitemdesc,
	 	"/images/admin.gif",
	 	"/maintence/codetree?params=child&parentid="+parentid+"&target=mil_body&codesetid="+codesetid);
	 	if(currnode.load){
	 	  if(uidlength.length==1){
	 	    currnode.add(tmp);
	 	    }else{
	 	    currnode.loadChildren();
	 	    }
	 	}else{
	 	  currnode.expand();
	 	}
	 	parent.frames["mil_body"].location.reload();
   }
  function add_codeitem(flag)
  {
  	if(provide()){
      return;
     }
  	 var currnode=Global.selectedItem; 
 	 if(currnode.uid=="root"){
 	 	return;
 	 } 
  	 var bflag=false;
     var currname=1;
     var return_vo= window.showModalDialog("/system/codemaintence/add_edit_codeitem.do?b_query=link&query=query&control="+currnode.uid, bflag, 
        "dialogWidth:500px; dialogHeight:300px;resizable:no;center:yes;scroll:yes;status:no");       
     if(return_vo==null)
  	 	return ;
  	 /*????????????????????,??????????Object??????*/  	 
     var codeitemvo=new Object();
     codeitemvo.codesetid=return_vo.codesetid;
     codeitemvo.codeitemdesc=return_vo.codeitemdesc;
     codeitemvo.codeitemid=return_vo.codeitemid;
     codeitemvo.parentid=return_vo.parentid;
     codeitemvo.childid=return_vo.childid;     
     curruid=currnode.uid;
     var hashvo=new ParameterSet();
     hashvo.setValue("codeitemvo",codeitemvo);
     hashvo.setValue("flag",flag);
     hashvo.setValue("codesetid",codeitemvo.codesetid);
     hashvo.setValue("curruid",curruid);
     hashvo.setValue("codeitemid",codeitemvo.codeitemid);
     hashvo.setValue("codeitemdesc",codeitemvo.codeitemdesc);
     hashvo.setValue("parentid",return_vo.parentid);
    var request=new Request({asynchronous:false,onSuccess:add_codeitem_ok,functionId:'1010050007'},hashvo);         
  }
  function update_codeitem_ok(outparamters){
  		var currnode=Global.selectedItem;  
    	var codeitemdesc=outparamters.getValue("codeitemdesc");
    	currnode.setText(codeitemdesc);
    	currnode.reload(1);
  }
  function update_codeitem(flag)
  {  
  	 var currnode=Global.selectedItem; 
 	 if(currnode.uid=="root"){
 	 	return;
 	 } 
 	 if(currnode.parent.uid=="root"){
 	  return;
 	  }
  	 var bflag=false;
     var currname=1;
     var return_vo= window.showModalDialog("/system/codemaintence/add_edit_codeitem.do?b_query=link&&codesetid="+currnode.uid, bflag, 
        "dialogWidth:500px; dialogHeight:300px;resizable:no;center:yes;scroll:yes;status:no");       
     if(return_vo==null)
  	 	return ;
  	 /*????????????????????,??????????Object??????*/  	 
     var codeitemvo=new Object();
     codeitemvo.codesetid=return_vo.codesetid;
     codeitemvo.codeitemdesc=return_vo.codeitemdesc;
     codeitemvo.codeitemid=return_vo.codeitemid;
     codeitemvo.parentid=return_vo.parentid;
     codeitemvo.childid=return_vo.childid;
     curruid=currnode.uid;
     var hashvo=new ParameterSet();
     hashvo.setValue("codeitemvo",codeitemvo);
     hashvo.setValue("flag",flag);
     hashvo.setValue("curruid",curruid);
     hashvo.setValue("codeitemid",codeitemvo.codeitemid);
     hashvo.setValue("codeitemdesc",codeitemvo.codeitemdesc);
    var request=new Request({asynchronous:false,onSuccess:update_codeitem_ok,functionId:'1010050007'},hashvo);         
  } 
  function serch_ok(){
  }
  function sercodeset(){
  		var currnode=Global.selectedItem;
        if(currnode.uid=="root")
          return;
        if(currnode.parent.uid!="root")
          return;
  		var bflag=false;
  		var uidtext=currnode.text;
  		var uidtitle=getEncodeStr(currnode.title);
  		var w =(window.screen.width-500)/2;
  	  var h=(window.screen.height-300)/2;
  	   var return_vo= window.showModalDialog("/system/codemaintence/serch_codeset.do?b_query=link&codesetid="+currnode.uid+"&currnodetext="+uidtitle,null, 
        "dialogWidth:500px; dialogHeight:300px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");

  		
//        if(return_vo==null)
//          return;
//        var hashvo=new ParameterSet();
//        hashvo.setValue("seltree",return_vo);
//        alert("ok");
//  		var request=new Request({asynchronous:false,onSuccess:serch_ok,functionId:'1010050005'},hashvo);
  } 
  function outputcode_ok(outparamters){
  window.location.href="/servlet/OutputCode?path="+outparamters.getValue("path");
  }
  function outputcode(){
      var currnode=Global.selectedItem;
      if(currnode.uid=="root")
        return;
      if(currnode.parent.uid!="root")
        return;
	  var temp=currnode.uid.split("/");
	  var codesetid;
	  if(temp.length==1){
		codesetid=temp[0];
	  }
	  else{
		codesetid=temp[1];
	  }
	  if(!checknumber(codesetid))
	  {
		alert("您没有权限输出系统类文件");
		return;
	  }
      var hashvo=new ParameterSet();
      hashvo.setValue("uid",currnode.uid);
      var request=new Request ({asynchronous:false,onSuccess:outputcode_ok,functionId:'1010050010'},hashvo);
  }
   
  function inputcode(){
  	var w =(window.screen.width-420)/2;
  	var h=(window.screen.height-215)/2;
	var thecodeurl="/system/codemaintence/codeinput.do"; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
    var return_vo= window.showModalDialog(iframe_url, false, 
        "dialogWidth:470px; dialogHeight:265px;resizable:no;center:yes;scroll:yes;status:no");
  }
  function checknumber(String) 
	{ 
   	var Letters = "1234567890"; 
   	var i; 
   	var c; 
   	for( i = 0; i < String.length; i ++ ) 
  	{ 
  		c = String.charAt( i ); 
  		if (Letters.indexOf( c ) ==-1) 
  		{ 
  		return true; 
  		} 
  	} 
 		 return false; 
  	} 
 function onchange()
 {
	onclickquery();
    codeMaintenceForm.action="system.codemaintence.code?b_query=link";
    codeMaintenceForm.submit();
 } 
 function onclickquery(){
 	var codesetid=codeMaintenceForm.seltree.value;
    window.parent.mil_body.location.href="/system/codemaintence/codetree.do?b_search=link&codesetid="+codesetid+"&param=root";
 }
 function codeset_search(){
 var codeset=new Object();
 
 codeset=window.showModalDialog("/system/codemaintence/search_code.do?b_search=link", false, 
        "dialogWidth:440px; dialogHeight:250px;resizable:no;center:yes;scroll:yes;status:no");
 var atcodeset=codeMaintenceForm.seltree.value;
 if(codeset==null){
 	return;
 }
 codeMaintenceForm.seltree.value=codeset.codesetid;

 codeMaintenceForm.action="/system/codemaintence/codetree.do?b_query=link&opt=2"; 
 codeMaintenceForm.submit();
 
 window.parent.mil_body.location.href="/system/codemaintence/jindutiao.html";
 

  }