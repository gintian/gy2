function getSelectYear(addyear){
	var yearnum = new Date().getFullYear()+addyear;
	var str="<select name=\"year"+addyear+"\" style='margin-left:3px;width:80px;'>";
	for(var i=yearnum+5;i>yearnum-15;i--){
		if(i==yearnum){
			str+="<option value="+i+" selected>";
		}else{
			str+="<option value="+i+">";
		}
		str+=i+GZ_ANLAYSE_SETINFOR_YEAR;
		str+="</option>";
	}
	str+="</select>"
	return str;
}
function getSelectYear2(){
	var yearnum = new Date().getFullYear()-1;
	var str="<select name=\"year1\" style='margin-left:3px;width:80px;'>";
	for(var i=yearnum+5;i>yearnum-15;i--){
		if(i==yearnum){
			str+="<option value="+i+" selected>";
		}else{
			str+="<option value="+i+">";
		}
		str+=i+GZ_ANLAYSE_SETINFOR_YEAR;
		str+="</option>";
	}
	str+="</select>"
	return str;
}
function getSelectMonth(addmonth){
	var monthnum = new Date().getMonth()+1;
	var str="<select name=\"month"+addmonth+"\" style='margin-left:3px;width:80px;'>";
	for(var i=1;i<13;i++){
		if(i==monthnum){
			str+="<option value="+i+" selected>";
		}else{
			str+="<option value="+i+">";
		}
		str+=i+GZ_ANLAYSE_SETINFOR_MONTH;
		str+="</option>";
	}
	str+="</select>"
	return str;
}
function getSelectNumber(){
	var str="<select name=\"number\" style='margin-left:3px;width:80px;'>";
	for(var i=1;i<10;i++){
		str+="<option value="+i+">";
		str+=i+GZ_ANLAYSE_SETINFOR_NUM;
		str+="</option>";
	}
	str+="</select>"
	return str;
}
function getSelect(timevalue){
	switch (timevalue){
		case 1:
			document.getElementById("viewyear").innerHTML='';
			document.getElementById("viewmonth").innerHTML='';
			document.getElementById("viewnumber").innerHTML='';
			document.getElementById("viewinterval").innerHTML='';
			break;
		case 2:
			document.getElementById("viewmonth").innerHTML='';
			document.getElementById("viewnumber").innerHTML='';
			document.getElementById("viewinterval").innerHTML='';
			var str=getSelectYear(0);
			document.getElementById("viewyear").innerHTML=str;
			break;
		case 3:
			document.getElementById("viewyear").innerHTML='';
			document.getElementById("viewnumber").innerHTML='';
			document.getElementById("viewinterval").innerHTML='';
			var str="";
			str+=getSelectYear(0);
			str+=getSelectMonth(0);
			document.getElementById("viewmonth").innerHTML=str;
			break;
		case 4:
			document.getElementById("viewyear").innerHTML='';
			document.getElementById("viewmonth").innerHTML='';
			document.getElementById("viewinterval").innerHTML='';
			var str="";
			str+=getSelectYear(0);
			str+=getSelectMonth(0);
			str+=getSelectNumber();
			document.getElementById("viewnumber").innerHTML=str;
			break;
		case 5:
			document.getElementById("viewyear").innerHTML='';
			document.getElementById("viewmonth").innerHTML='';
			document.getElementById("viewnumber").innerHTML='';
			var str="";
			str+=getSelectYear2();
			str+=getSelectMonth(0);
			str+=getSelectYear(0);
			str+=getSelectMonth(1);
			document.getElementById("viewinterval").innerHTML=str;
			break;
	}
}
function change(obj,tabid){
	if(obj.value=="new"){
		var thecodeurl = "/gz/gz_analyse/newcond.do?b_query=link`tabid="+tabid;
		  var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl); 
		     /*var return_vo=window.showModalDialog(thecodeurl,"window2",
		       "dialogWidth:400px;dialogHeight:320px;resizable:no;center:yes;scroll:yes;status:no");*/
		     var config = {
		      width:450,
		      height:360,
		      type:'2',
		      id:'change_win'
		  }
		  var ss = modalDialog.showModalDialogs(iframe_url,"change_win",config,"");
   		setSelectCond(tabid);
	}		
}
function setSelectCond(tabid){
  	var hashvo=new ParameterSet();
	hashvo.setValue("tabid",tabid);
	var request=new Request({method:'post',asynchronous:false,onSuccess:setSelectCondOk,functionId:'0521010012'},hashvo);	
}
function setSelectCondOk(outparamters){
	var conditionslist = outparamters.getValue("conditionslist");
	AjaxBind.bind(gzReportForm.conditions,conditionslist);
}
function selectPageRow(curr){
	switch (curr){
		case 1:
			hides("pageRowsView");
			break;
		case 2:
			toggles("pageRowsView");
			break;
	}
}
function openTable(reset){
	var year,month,num,timevalue;
	var tabid=document.getElementsByName("tabid")[0].value;
	var gz_module=document.getElementsByName("gz_module")[0].value;
	var dbname=document.getElementsByName("dbname")[0].value;
	var titlename=document.getElementsByName("titlename")[0].value;
	var category=document.getElementsByName("category")[0].value;
	var archive = document.getElementsByName("archive")[0].value;
	var selecttime =  document.getElementsByName("selecttime");
	for(var i=0;i<selecttime.length;i++){
		if(selecttime[i].checked)
			timevalue=selecttime[i].value;
	}
	
	switch (timevalue){
		case '1':
			year="";
			month="";
			num="";
			break;
		case '2':
			year=document.all.year0[0].value;
			month="";
			num="";
			break;
		case '3':
			year=document.all.year0[1].value;
			month=document.all.month0[0].value;
			month=month>9?month:"0"+month;
			num="";
			break;
		case '4':
			year=document.all.year0[2].value;
			month=document.all.month0[1].value;
			month=month>9?month:"0"+month;
			num=document.all.number.value;
			break;
		case '5':
			year=document.all.year1.value+"-"+document.all.year0[3].value;
			var month0=document.all.month0[2].value;
			month0=month0>9?month0:"0"+month0;
			var month1=document.all.month1.value;
			month1=month1>9?month1:"0"+month1;
			month = month0+"-"+month1
			num="";
			break;
	}
	var zeroPrint = "0";
	var printGrid = "0";
	var summary = document.getElementsByName("summary")[0].value;
	var inputObjects = document.getElementsByTagName("input");
	for(var i = 0;i < inputObjects.length;i++){
		var setname=inputObjects[i].name;
		if('zeroPrint'==inputObjects[i].name&&inputObjects[i].checked){
			zeroPrint="1";
		}
		if('printGrid'==inputObjects[i].name&&inputObjects[i].checked){
			printGrid="1";
		}
	}
	
	
	var conditions = document.getElementsByName("conditions")[0].value;
	
	var isAutoCountvalue =  '';
	var isAutoCount =  document.getElementsByName("isAutoCount");
	for(var i=0;i<isAutoCount.length;i++){
		if(isAutoCount[i].checked)
			isAutoCountvalue=isAutoCount[i].value;
	}

	var pageRows = '';
	if(isAutoCountvalue=='1')
		 pageRows = document.getElementsByName("pageRows")[0].value;
	var theArr=new Array(window.screen.width,window.screen.height); 
	//"`zeroPrint="+zeroPrint+"`printGrid="+printGrid+"`pageRows="+pageRows+新版不要了，打印零线等
	var strurl="/gz/gz_accounting/report/open_gzbanner.do?b_report=link`checksalary=analysis`opt=int`tabid=";
	strurl+=tabid+"`a_code=`year="+year+"`month="+month+"`num="+num+"`selecttime="+timevalue+"`conditions="+conditions;
	strurl+="`summary="+summary+"`reset="+reset+"`gz_module="+gz_module;
	strurl+="`dbname="+dbname+"`category="+category+"`archive="+archive+"`titlename="+titlename;
	var iframe_url="/gz/gz_accounting/report/iframe_report.jsp?src="+$URL.encode(strurl); 
	var width = window.screen.width;
	var height = window.screen.height;
	var config = {
	    width:width,
	    height:height,
	    type:'2',
	    id:'openTable_win',
	    dialogArguments:theArr
	}
    if(!window.showModalDialog)
    	window.dialogArguments = theArr;
	var ss = modalDialog.showModalDialogs(iframe_url,"openTable_win",config,"");
}

function setinfo_close() {
	var win
	if(parent.Ext){
		win=parent.Ext.getCmp('showMusterOpen_win');
	}else{
		win= parent.parent.Ext.getCmp('showMusterOpen_win');
	}
  	
  	if(win) {
		win.close();
  	}else {
  		parent.window.close();
  	}
}
