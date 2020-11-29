

function openCodeCustomReportDialog(codeid,targetName,hiddenName,flag) {
	var codevalue,thecodeurl,target_name,hidden_name,hiddenobj,features;
	if (!codeid) {
		codeid = "@K";
	}
	if(targetName == null) {
      return;
	}
	
	if (!flag) {
		flag = "0";
	}
	
	if (flag == "0") {
		features = "";
	} else if (flag == "1") {
		features = "";
	} else if (flag == "2") {
		features = "";
	} else if (flag == "3") {
		features = "";
	}
	
	
    //根据代码显示的对象名称查找代码值名称	
    target_name = document.getElementsByName(targetName)[0].name;
    hidden_name = hiddenName;
    var hiddenInputs = document.getElementsByName(hidden_name);
    if(hiddenInputs != null) {
    	hiddenobj = hiddenInputs[0];
    	codevalue = "";
    }
    var obj = document.getElementsByName(targetName)[0];
    var theArr = new Array(codeid,codevalue,obj,hiddenobj,features,flag);
    thecodeurl = "/train/postAnalyse/codeselectpotrain.jsp?codesetid=" + codeid + "`codeitemid=ALL"; 
	var iframe_url = "/general/query/common/iframe_query.jsp?src=" + $URL.encode(thecodeurl);
	 
    var popwin = window.showModalDialog(iframe_url, theArr, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
      
}