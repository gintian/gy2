var code_desc; 
 function setSelectCodeValue()
   {
     if(code_desc)
     {
        var vos= document.getElementsByName('dict_box');
        var dict_vo=vos[0];
        var isC=true;
        for(var i=0;i<dict_vo.options.length;i++)
        {
          if(dict_vo.options[i].selected)
          {
            code_desc.value=dict_vo[i].text;
            var code_name=code_desc.name;
            if(code_name!="")
            {
               var code_viewname=code_name.substring(0,code_name.indexOf("."));
               var view_vos= document.getElementsByName(code_viewname+".value");
               var view_vo=view_vos[0];    
               if(dict_vo[i].value!=null)          
                 view_vo.value=dict_vo[i].value.substring(2);
               view_vo.fireEvent("onchange");   
             }
          }
        }        
        Element.hide('dict'); 
        event.srcElement.releaseCapture(); 
     }
  }
  
function winhref(url)
{
   if(url=="")
      return false;
   pieceRateForm.action=url;
   pieceRateForm.submit();
}  
   function inputType(obj,event)
  {
     var keyCode = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;     
     if(keyCode==13)
     {
       setSelectCodeValue();
     }     
  }
  function inputType2(obj,event)
  {
    var keyCode = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;
    if(keyCode == 40)
    {
       var vos= document.getElementsByName('dict');
       var vos1=vos[0];       
       if(vos1.style.display!="none")
       {
          var vos= document.getElementsByName('dict_box');
          var dict_vo=vos[0];          
          dict_vo.focus(); 
       }
    }
  }
  function styleDisplay(obj)
  {
     var obj_name=obj.name;
     if(code_desc)
     {
        var code_name=code_desc.name;
        if(code_name!=obj_name)
        {
          Element.hide('dict');
        }
     }
  }
 
   function changetitle(obj){
  	var hashvo=new ParameterSet();
    hashvo.setValue("codeitemid",obj.value);
    hashvo.setValue("uplevel",'0');
    var request=new Request({method:'post',onSuccess:changetitlevalue,functionId:'02010001016'},hashvo);
  	function changetitlevalue(outparamters){
  		var name=outparamters.getValue("name");
  		var targetobj=document.getElementsByName(obj.name.replace('.value','.viewvalue'))[0];
  		targetobj.title=name;
  	}
  } 
  
 function checkDict(code,obj)
  {
    var code_name=obj.name;
    var code_viewname=code_name.substring(0,code_name.indexOf("."));
    var view_vos= document.getElementsByName(code_viewname+".value");
    var view_vo=view_vos[0];  
    if(view_vo==null||view_vo=="")
    {
      obj.value="";
      return false;
    }
    var isC=false;
    for(var i=0;i<g_dm.length;i++)
    {
		dmobj=g_dm[i];		 
		if(dmobj.ID==(code+view_vo))
		{
		    isC=true;
		    break;
		}
   } 
   if(!isC)
   {
      obj.value="";
      return false;
   } 
   obj.focus();
}
Element.hide('dict');
function reloadMenu(a0100,setname,actiontype)
{
  if(actiontype=="update")
     return;
   if(a0100!=null&&a0100!=""&&a0100!="A0100"&&a0100!="a0100"&&a0100!="su")
   {
      if(setname=="A01")
        parent.mil_menu.location.reload();
   }     
}

function toggles(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "block";
	}
} 
function hides(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "none";
	}
}

function toggles1(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "inline";
	}
} 

function change_style(targetId,csstype){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
        target.setAttribute("class",csstype);//Mozilla设置class的方法
        target.setAttribute("className",csstype);//IE设置class的方法    
	}

}

function getselectids(){
	//检查是否被选中了   如果选中，得到选中的项的id号
	var strIds="";
	var dd=false;
	var index=0;
	var  obj = document.getElementsByName("s0100ids");
	for(var i=0;i<document.pieceRateForm.elements.length;i++)
	{			
   		if((document.pieceRateForm.elements[i].type=='checkbox')&&(document.pieceRateForm.elements[i].name!="selbox" ))      
  		{	
	  		if(document.pieceRateForm.elements[i].checked)
  			{
  				dd=true;
  				
  				if (strIds=="" )
  				  strIds=obj[index].value;
  				else 
			    	strIds=strIds+","+obj[index].value;
			}
		 index++;
		}
	}
	return strIds;

}


 function addtask(){
	var thecodeurl = "/gz/gz_accounting/piecerate/search_piecerate.do?b_addtask=link&model=add&s0100=";
	window.location.href = thecodeurl;
}

 function edittask(){
   var ids=getselectids();
   if (ids=="" )
   {
    	alert(NOTING_SELECT);
    	return;
   }
   if (ids.indexOf(",")>-1)
   {
        alert(GZ_BUDGET_INFO5);
   		return;
   }   
   var thecodeurl = "/gz/gz_accounting/piecerate/search_piecerate.do?b_addtask=link&model=edit&s0100="+ids;
   window.location.href = thecodeurl;
}

function deletetask(){
   var ids=getselectids();
   if (ids=="" )
   {
    alert(NOTING_SELECT);
   }else{
	   if (confirm(GZ_REPORT_CONFIRMDELETE)) {
	   var thecodeurl = "/gz/gz_accounting/piecerate/search_piecerate.do?b_delete=link&s0100="+ids;
	   window.location.href = thecodeurl;
   }
   }
}

function approval(){
   var ids=getselectids();
   if (ids=="" )
   {
    alert(NOTING_SELECT);
   }else{
   
   var thecodeurl = "/gz/gz_accounting/piecerate/search_piecerate.do?b_approval=link&flag=approval&s0100="+ids;
   window.location.href = thecodeurl;
   }

}

function reporting(){
   var ids=getselectids();
   if (ids=="" )
   {
    alert(NOTING_SELECT);
   }else{
   
   var thecodeurl = "/gz/gz_accounting/piecerate/search_piecerate.do?b_approval=link&flag=reporting&s0100="+ids;
   window.location.href = thecodeurl;
   }
}
function reject(){
   var ids=getselectids();
   if (ids=="" )
   {
    alert(NOTING_SELECT);
   }else{
   
   var thecodeurl = "/gz/gz_accounting/piecerate/search_piecerate.do?b_approval=link&flag=reject&s0100="+ids;
   window.location.href = thecodeurl;
   }
}
function setUp(){
	var target_url = "/gz/gz_accounting/piecerate/search_piecerate.do?b_setUp=link";
	var newwindow = window.showModalDialog(target_url, "", "dialogWidth:600px; dialogHeight:320px;resizable:no;center:yes;scroll:yes;status:no");

}

function show(flag){//与薪资类别关联，周期部分的显示和隐藏等操作的处理。
	if(flag.value=="1"){
		toggles("ss");
		document.getElementsByName("zhouqi")[0].value=flag.value;
	}else{
		hides("ss");
		document.getElementsByName("zhouqi")[0].value=flag.value;
	}
}
function show1(flag){//与薪资类别关联，周期部分的显示和隐藏等操作的处理。
	if(flag=="1"){
		toggles("ss");
		document.getElementsByName("zhouqi")[0].value=flag.value;
	}else{
		hides("ss");
		document.getElementsByName("zhouqi")[0].value=flag.value;
	}
}
function show2(flag,flag2){//与薪资类别关联，周期部分的显示和隐藏等操作的处理。
	if(flag=="1"){
		document.getElementById("y1").checked=true;
		document.getElementsByName("expression_str")[0].value=flag2;
		
	}else{
		document.getElementById("y2").checked=true;
		var zq =document.getElementsByName("zq"); 
		document.getElementsByName("delayTime2")[0].value=flag;
		document.getElementsByName("delayTime1")[0].value=flag-1;
		zq[1].value=flag;
		document.getElementsByName("expression_str")[0].value=flag2;
	}
}
function simpleCondition() {
	var strExpression = getEncodeStr(document.getElementsByName("expression_str")[0].value);
	var formurl="/gz/gz_accounting/piecerate/search_piecerate.do?b_datarange=link&strExpression="+strExpression;
	var strExpression= window.showModalDialog(formurl, false, 
        "dialogWidth:480px; dialogHeight:420px;resizable:no;center:yes;scroll:yes;status:no");// modify by xiaoyun 2014-8-25
     if(strExpression)
	   {
	       document.getElementsByName("expression_str")[0].value=strExpression;
	   }
	     else
	       document.getElementsByName("expression_str")[0].value="";
}
function saveSetUp(){

		var tabid = "";
		var codeitemid = "";
		var select = document.getElementsByTagName("select");
		var codeitemid1=document.getElementsByName("codeitemid");
		for (var i = 0; i < select.length; i++) {
			tabid=tabid+select[i].value+"/";
		}
		for (var j = 0; j < codeitemid1.length; j++) {
			codeitemid=codeitemid+codeitemid1[j].value+"/";
		}			
		var hashvo=new ParameterSet();
		hashvo.setValue("tabid",tabid);	
		hashvo.setValue("codeitemid",codeitemid);	
		var request=new Request({asynchronous:false,onSuccess:window.close,functionId:'3020091042'},hashvo);	
}
   function search_kh_data()
   {
      var  start_date = document.getElementById("start_date");
	  var  end_date = document.getElementById("end_date");
	  var  tasktype = document.getElementsByName("tasktype")[0].value;
	  var  sp_status = document.getElementsByName("sp_status")[0].value;
	  var aa = start_date.value;
	  var bb = end_date.value;
   	  aa=aa.replace(new  RegExp("-", "gm" ),"")
   	  bb=bb.replace(new  RegExp("-", "gm" ),"")
   	  if(aa>bb)
   	  {
   	  	alert(KHPLAN_INFO1);
   	  	return false;
   	  }else{
   	  	    var thecodeurl = "/gz/gz_accounting/piecerate/search_piecerate.do?b_search_kh_data=link&start_date="+start_date.value+"&end_date="+end_date.value+"&tasktype="+tasktype+"&sp_status="+sp_status;
   			window.location.href = thecodeurl;
   	  }
   } 
   //与薪资类别关联部分
   function mincrease(obj_name,obj,theMax) 
	{
      var objs =document.getElementsByName(obj_name);
      var obj1 =document.getElementsByName(obj);
      var zq =document.getElementsByName("zq");      
  	  if(objs==null)
  		 return false;
  	  var obj=objs[0];
  	  var obj2=obj1[0];
  	  if(parseInt(obj.value)<theMax)
		obj.value = (parseInt(obj.value)+1)+'';
		obj2.value = obj.value-1;
		zq[1].value=obj.value;
	}
   function msubtract(obj_name,obj,theMin) 
	{
      var objs =document.getElementsByName(obj_name); 
      var obj1 =document.getElementsByName(obj);
      var zq =document.getElementsByName("zq");      
  	  if(objs==null)
  		 return false;
  	  var obj=objs[0];
  	  var obj2=obj1[0];
  	  if(parseInt(obj.value)>theMin)
		obj.value = (parseInt(obj.value)-1)+'';
		obj2.value = obj.value-1;
		zq[1].value=obj.value;
	}
function addtable(){
		var zhib1 = document.getElementsByName("zhib1")[0].value;
		var zhib2 = document.getElementsByName("zhib2")[0].value;
		var zhib11 = document.getElementsByName("zhib1")[0].options[document.getElementsByName("zhib1")[0].selectedIndex].text;
		var zhib22 = document.getElementsByName("zhib2")[0].options[document.getElementsByName("zhib2")[0].selectedIndex].text;
		var table1 = document.getElementById("table1");
		if(zhib1==""||zhib2==""){
			alert(ITEM_NOT_EMPTY);
			document.getElementsByName("zhib1")[0].value = "";
			document.getElementsByName("zhib2")[0].value = "";
			return;
		}
		var aa = zhib11.split(":");
		var bb = zhib22.split(":");
		var tr = table1.insertRow();
		var td0 = tr.insertCell(0);
		td0.setAttribute("align","center");
		td0.className = "RecordRow";
		td0.innerHTML = "<input type='checkbox' name='quanxuan' />";
		var td1 = tr.insertCell(1);
		td1.setAttribute("align","center");
		td1.className = "RecordRow";
		td1.innerHTML = zhib11+"<input type='hidden' name='zhib3' value='"+aa[0]+"'/>";
		var td2 = tr.insertCell(2);
		td2.setAttribute("align","center");
		td2.className = "RecordRow";
		td2.innerHTML = zhib22+"<input type='hidden' name='zhib4' value='"+bb[0]+"'/>";
		 
		document.getElementsByName("zhib1")[0].value = "";
		document.getElementsByName("zhib2")[0].value = "";
	}
function deltable(){
	var quan = document.getElementsByName("quanxuan");
	for(var i=quan.length-1;i>=0;i--)
	{			
	  		if(quan[i].type=="checkbox"&&quan[i].checked==true)
  			{

  				  var tr = quan[i].parentNode.parentNode;
  				  var rowNum = tr.rowIndex;
				  table1.deleteRow(rowNum);
				

			}
	}
}
function selall(){
	var quan = document.getElementsByName("quanxuan");
	for(var i=0;i<quan.length;i++){
		if(quan[i].type=="checkbox"&&quan[i].checked==true){
			quan[i].checked=false;
		}else{
			quan[i].checked=true;
		}
	}
}
function on(obj){
        if (obj.checked){  
           str=obj.value;  
           document.getElementsByName("zhouqi1")[0].value=str;
        }  	
}
function ok(){
	var zhibiao = "";
	var zhouq = document.getElementsByName("zq");
	var zhouq1 = document.getElementsByName("sp_status")[0].value;
	var zhib1 = document.getElementsByName("zhib1")[0].value;
	var zhib2 = document.getElementsByName("zhib2")[0].value;
	var expression_str = document.getElementsByName("expression_str")[0].value;
	var zhibiao1 = document.getElementsByName("zhib3");
	var zhibiao2 = document.getElementsByName("zhib4");
	for(var i=0;i<zhouq.length;i++){
		if(zhouq[i].checked){
			str = zhouq[i].value;
		}
	}
	for(var i=0;i<zhibiao1.length;i++){
		zhibiao = zhibiao + zhibiao1[i].value+"="+zhibiao2[i].value+",";
	}
	if(zhib1!=""&&zhib2!=""){
		zhibiao = zhibiao + zhib1+"="+zhib2;
	}
	   	var result=new Array();
  		result[0]=expression_str;
	  	result[1]=zhouq1;
	  	result[2]=str;
	  	result[3]=zhibiao;
  		returnValue=result;
	    window.close();
}
function check(obj){
	var zhibiao1 = document.getElementsByName("zhib3");
	for(var i=0;i<zhibiao1.length;i++){
		if(zhibiao1[i].value==obj.value.toUpperCase()){
			alert("计件指标不能重复");
			document.getElementsByName("zhib1")[0].value = "";
			
		}
	}
}
function check2(obj){
	var zhibiao2 = document.getElementsByName("zhib4");
	for(var i=0;i<zhibiao2.length;i++){
		if(zhibiao2[i].value==obj.value.toUpperCase()){
			alert("薪资指标不能重复");
			document.getElementsByName("zhib2")[0].value = "";
			
		}
	}
}
function testNum(obj){
	var a = obj.value;
	if(a>28){
		obj.value="28";
		document.getElementsByName("delayTime1")[0].value=obj.value-1;
		document.getElementsByName("zq")[1].value=obj.value;
	}else if(a<2){
		obj.value="2";
		document.getElementsByName("delayTime1")[0].value=obj.value-1;
		document.getElementsByName("zq")[1].value=obj.value;
	}else{
		document.getElementsByName("delayTime1")[0].value=obj.value-1;
		document.getElementsByName("zq")[1].value=obj.value;
	}
}

//与薪资类别相关联部分结束
function print(id){
	var hashvo=new ParameterSet();
    hashvo.setValue("s0100",id);
    hashvo.setValue("flag","jobtable");
    var request=new Request({method:'post',onSuccess:openwins,functionId:'3020091044'},hashvo);
}
function print1(){
	var hashvo=new ParameterSet();
	var id = pieceRateForm.tasktype.value;
	if(id=="00"){
	    alert("请先选择作业类别");
	    return false;
	}
    hashvo.setValue("s0102",id);
    hashvo.setValue("flag","signtable");
    var request=new Request({method:'post',onSuccess:openwins1,functionId:'3020091044'},hashvo);
}
function openwins(outparamters){
    var hashvo=new ParameterSet();
    var id = outparamters.getValue("jobtable");
    var s0100 = outparamters.getValue("s0100");
    if(id=="00"){
    	alert("作业票未设置");
    	return false;
    }
   var url="/system/options/customreport/displaycustomreportservlet?ispriv=1&id="+id+'&inputParam=s0100:'+s0100;
   window.open(url,"_blank","left=0,top=0,width="+screen.availWidth+",height="+screen.availHeight+",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");

   
}
function openwins1(outparamters){
    var hashvo=new ParameterSet();
    var id = outparamters.getValue("signtable");
    if(id=="00"){
    	alert("考勤签到表未设置");
    	return false;
    }
 var url="/system/options/customreport/displaycustomreportservlet?ispriv=1&id="+id;
   window.open(url,"_blank","left=0,top=0,width="+screen.availWidth+",height="+screen.availHeight+",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");
   
}
function showSelect(outparamters) { 
    var waitInfo=eval("wait");	   
    waitInfo.style.display="none";
    var url = outparamters.getValue("url");
    
    var filename = outparamters.getValue("filename");
    url = url + "?filename=" +filename;
    var html = document.getElementById("htmlparam");
    html.value = getDecodeStr(outparamters.getValue("htmlparam"));
    window.open(url,"_blank","left=0,top=0,width="+screen.availWidth+",height="+screen.availHeight+",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");
    
 	}
function add_formula() {
	var winFeatures = "dialogHeight:300px; dialogLeft:200px;";
	var target_url = "/gz/gz_budget/budget_rule/formula.do?b_add=link";
	var newwindow = window.showModalDialog(target_url, "", "dialogWidth:600px; dialogHeight:300px;resizable:no;center:yes;scroll:yes;status:no");
	if (newwindow != null) {
		var tab_id = 0;
		budgetformulaForm.action="/gz/gz_budget/budget_rule/formula.do?b_search=link&tab_id="+tab_id;
		budgetformulaForm.submit();
	}
}

function defCheck(s0100){
	if(s0100==null||s0100.length<1){
      return;
    }
    changebox(s0100);
}

function changebox(checkvalue){
	checkvalue=checkvalue+"";
	if(checkvalue!=null&&checkvalue.length>0){
		tr_bgcolor(checkvalue);
	}
}

function tr_bgcolor(s0100){
	var tablevos=document.getElementsByName("s0100ids");
	var bfinded=false;
	for(var i=0;i<tablevos.length;i++)
	{
    	var cvalue = tablevos[i];
    	var tr = cvalue.parentNode.parentNode;
    	if (cvalue.value==s0100) {
    	    tr.style.backgroundColor = '#FFF8D2' ;
    	    bfinded=true;
    	}
    	else
    	{
    	  tr.style.backgroundColor = '';
    	}

    }
    if (!bfinded){
		for(var i=0;i<tablevos.length;i++)
		{
	    	var cvalue = tablevos[i];
	    	var tr = cvalue.parentNode.parentNode;	
    	    tr.style.backgroundColor = '#FFF8D2' ;
    	    break;	
	
	    }
    }
}
//田野添加 在计件薪资作业单页面添加进入统计页面的名为‘报表’的按钮
function reportable(startDate,endDate){
	 var thecodeurl = "/gz/gz_accounting/piecerate/search_piecerate_tj_report.do?b_search=link&startDate="+startDate+"&endDate="+endDate+"&defId=-1";
	 window.location.href = thecodeurl;
	
}

