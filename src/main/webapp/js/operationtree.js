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
  	if(currnode==null){
	 	return;
	 }
  	var uid=currnode.uid;
  	
  	if(uid=="root"){
  	 add_codeset(0);
  	 
  	}else{
  	 if(currnode.parent.uid=="root")
  		add_codeitem(0);
  	}
   }
   function updateObject(){
    var currnode=Global.selectedItem;
  	var uid=currnode.uid;
	if(uid=="root"){
	return;
	}else{
  		update_codeset();
	}
   }
  function add_codeset_ok(outparamters)
  {
        var currnode=Global.selectedItem;  
        var uid=currnode.uid;
     	var operationcode=outparamters.getValue("operationcode");
     	var operationname=outparamters.getValue("operationname");
     	var statid=outparamters.getValue("statid");
     	var operationid=outparamters.getValue("operationid");
	 	var tmp = new xtreeItem(operationid+"/"+operationcode,
	 	operationname,
	 	("/general/operation/showtable.do?b_query=link&operationcode="+operationcode),
	 	"mil_body",
	 	operationname,
	 	"/images/open.png",
	 	"/servlet/OperationTree?params=child&statid="+statid+"&target=mil_body&operationcode="+ operationcode);
	 	currnode.add(tmp);
   }
  function add_codeset(flag)
  {
 	 var currnode=Global.selectedItem; 
 	 currnode=Global.selectedItem;
 	 if(currnode==null){
	 	return;
	 }
  	 var bflag=false;
     var currname=1;
     var return_vo= window.showModalDialog("/general/operation/operationtree.do?b_add=link&query=query&root=root", bflag, 
        "dialogWidth:500px; dialogHeight:300px;resizable:no;center:yes;scroll:yes;status:no");       
     if(return_vo==null)
  	 	return ;
     var operationvo=new Object();
     operationvo.operationid=return_vo.operationid;
     operationvo.operationcode=return_vo.operationcode;
     operationvo.operationname=return_vo.operationname;
     operationvo.statid=return_vo.statid;
     var hashvo=new ParameterSet();
     hashvo.setValue("operationvo",operationvo);
     hashvo.setValue("operationname",return_vo.operationname);
     hashvo.setValue("statid",return_vo.statid);
     hashvo.setValue("operationcode",return_vo.operationcode);
     hashvo.setValue("operationid",return_vo.operationid);
   	 var request=new Request({asynchronous:false,onSuccess:add_codeset_ok,functionId:'1010070002'},hashvo); 
  }
  function update_codeset_ok(outparamters)
  {
        var currnode=Global.selectedItem;  
     	var codesetname=outparamters.getValue("operationname");
        currnode.setText(codesetname);
	   	currnode.reload(1);
  }
  function update_codeset()
  {
     var currnode=Global.selectedItem; 
     if(currnode==null){
	 	return;
	 }
     var bflag=true;
     var uid=currnode.uid;
     var uids=uid.split("/");
     var theurl="/general/operation/operationtree.do?b_update=link&query=query&operationid="+uids[0];
     var return_vo= window.showModalDialog(theurl, bflag, 
        "dialogWidth:500px; dialogHeight:300px;resizable:no;center:yes;scroll:yes;status:no");
  	 if(return_vo==null)
  	 	return ;
  	 /*????????????????????,??????????Object??????*/
     var operationvo=new Object();
     operationvo.operationid=return_vo.operationid;
     operationvo.operationcode=return_vo.operationcode;
     operationvo.operationname=return_vo.operationname;
     operationvo.statid=return_vo.statid;
     var hashvo=new ParameterSet();
     hashvo.setValue("operationvo",operationvo);
     hashvo.setValue("operationcode",return_vo.operationcode);
     hashvo.setValue("operationname",return_vo.operationname);
     hashvo.setValue("operationid",return_vo.operationid);
   	 var request=new Request({asynchronous:false,onSuccess:update_codeset_ok,functionId:'1010070003'},hashvo);       
  }
 
  function add_codeitem_ok(outparamters)
  {
        var currnode=Global.selectedItem;  
        var operationid=outparamters.getValue("operationid");
     	var operationcode=outparamters.getValue("operationcode");
     	var operationname=outparamters.getValue("operationname");
     	
	 	var tmp = new xtreeItem(operationid+"/"+operationcode,
	 	operationname,
	 	("/general/operation/showtable.do?b_query=link&operationcode="+operationcode),
	 	"mil_body",
	 	operationname,
	 	"/images/open.png",
	 	"");
	 	if(currnode.load){
	 	currnode.add(tmp);
	 	}else{
	 	currnode.expand();
	 	}
   }
  function add_codeitem(flag)
  {
  	 var currnode=Global.selectedItem; 
  	 currnode=Global.selectedItem;
  	 if(currnode==null){
	 	return;
	 }
 	 var uid=currnode.uid;
 	 var uids=uid.split("/");
  	 var bflag=false;
     var currname=1;
     var return_vo= window.showModalDialog("/general/operation/operationtree.do?b_add=link&query=query&operationcode="+uids[1], bflag, 
        "dialogWidth:500px; dialogHeight:300px;resizable:no;center:yes;scroll:yes;status:no");       
     if(return_vo==null)
  	 	return ; 
     var operationvo=new Object();
     operationvo.operationid=return_vo.operationid;
     operationvo.operationcode=return_vo.operationcode;
     operationvo.operationname=return_vo.operationname;
     operationvo.statid=return_vo.statid;
     var hashvo=new ParameterSet();
     hashvo.setValue("operationvo",operationvo);
     hashvo.setValue("operationcode",return_vo.operationcode);
     hashvo.setValue("operationname",return_vo.operationname);
     hashvo.setValue("operationid",return_vo.operationid);
   	 var request=new Request({asynchronous:false,onSuccess:add_codeitem_ok,functionId:'1010070002'},hashvo);         
  }