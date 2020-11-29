var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	var ViewProperties=new ParameterSet();
	var webserver=1;



 var dropDownDate=createDropDown("dropDownDate");
   var __t=dropDownDate;
   __t.type="date";
   __t.tag="";
   _array_dropdown[_array_dropdown.length]=__t;
   initDropDown(__t);
   
   var dropDownList=createDropDown("dropDownList");
   var __t=dropDownList;
   __t.type="list";
   __t.tag="";
   _array_dropdown[_array_dropdown.length]=__t;
   initDropDown(__t); 

 var dropdownCode=createDropDown("dropdownCode");
    var __t=dropdownCode;
    __t.type="custom";
    __t.path="/general/muster/select_code_tree.do";
	//__t.path="/system/gcodeselect.jsp";    
    __t.readFields="codeitemid";
    //__t.writeFields="xxxx";
    __t.cachable=true;__t.tag="";
    _array_dropdown[_array_dropdown.length]=__t;
    initDropDown(__t);
    
    
     initDocument();
function openwin(){
		var tabid = document.getElementById("customid");
	    var hashvo=new ParameterSet();
	    hashvo.setValue("ispriv","1");	
       	hashvo.setValue("id",tabid.value);
       	
       	var frmID=document.getElementById("form1"); 
		
     	var waitInfo=eval("wait");
     	if (waitInfo.style.display == "block") {
     		return ;
     	}
		var top = document.getElementById("topss");   
        waitInfo.style.display="block";
        waitInfo.style.top=(screen.availHeight - top.clientHeight-100)/2;
        waitInfo.style.left=(screen.availWidth-waitInfo.clientWidth)/2;
        //hashvo.setValue("url",document.getElementById("url").value);
        //var request=new Request({method:'post',asynchronous:true,onSuccess:showSelect,functionId:'10100103413'},hashvo);
        frmID.submit();
	   
	}
	
	function showSelect(outparamters) {
	     var waitInfo=eval("wait");	   
	     waitInfo.style.display="none";
	     var url = outparamters.getValue("url");
	     //var html = window.opener.document.getElementById("htmlparam");
	     //html.value = getDecodeStr(outparamters.getValue("htmlparam"));
	     document.getElementById("form1").action = url;
	     document.getElementById("form1").submit();
  	}