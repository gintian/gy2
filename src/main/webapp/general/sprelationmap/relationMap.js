/**
 * 汇报关系图js
 */
function changeTree(type){
	self.parent.location="/general/sprelationmap/select_tree.do?b_init=link&relationType="+type;
}
function menuClose(){
	
	document.getElementById("menupanel").style.display="none";
}
function changeBg(obj){
	document.getElementById(obj).style.background="FFE4C4";
}
function backBg(obj){
	document.getElementById(obj).style.background="";
}
function expendChild(showQueryButton){
	with(document.getElementById("menupanel"))
	{
    	style.display="none";
	 }
	relationMapForm.action="/general/sprelationmap/relation_map_drawable.do?b_query=query&showQueryButton="+showQueryButton;
    relationMapForm.submit();
}
function openInfoBase(){
   var currId=document.getElementById("currentNodeId").value;
   var arr= currId.split("`");
   
   with(document.getElementById("menupanel"))
	{
    	style.display="none";
	 }
   var dbpre=arr[1].substring(0,3);
   var a0100=arr[1].substring(3);
   //田野修改查询基本信息后的部门后带有放大镜图标进行查看部门信息
   //var src="/workbench/browse/showselfinfo.do?b_search=link`userbase="+dbpre+"`flag=notself`returnvalue=100000`a0100="+a0100;修改前代码
   var src="/workbench/browse/showselfinfo.do?b_search=link`userbase="+dbpre+"`flag=notself`returnvalue=relation`a0100="+a0100;
   if(window.$URL){
		src = $URL.encode(src);
	}
   var iframe_url="/general/query/common/iframe_query.jsp?src="+src;
   var values= window.showModalDialog(iframe_url,null, 
		        "dialogWidth:"+(window.screen.width)+"px; dialogHeight:"+(window.screen.height)+"px;resizable:no;center:yes;scroll:yes;status:no");			
	

}
function configParam(flag){
	var url = '/general/sprelationmap/param_config.do?b_init=init';
	if(flag=='isyfiles')
		url = '/general/sprelationmap/show_report_map.do?b_setparam=link';
    /*var config = {
        width:600,
        height:500,
        type:'1'
    };
    */
    configParam_flag=flag;
    var iframeurl="/general/query/common/iframe_query.jsp?src="+url;
  //  modalDialog.showModalDialogs(iframeurl,'configWindow',config,configParam_ok);
   //  var values= window.showModalDialog(iframeurl,"configWindow", "dialogWidth:600px; dialogHeight:500px;resizable:no;center:yes;scroll:no;status:no");
	// console.log(values);
    
    var iTop = (window.screen.height-30-536)/2; //获得窗口的垂直位置;
  	var iLeft = (window.screen.width-10-600)/2;  //获得窗口的水平位置;
  	window.open(iframeurl,'','height=536, width=600,top='+iTop+',left='+iLeft+',toolbar=no,menubar=no,scrollbars=no,resizable=no,location=no,status=no');
}

function configParam_ok(thevo) {
    if (thevo == "1") {
        if (configParam_flag == 'isyfiles') {
            self.parent.mil_body.location = "/general/sprelationmap/show_report_map.do?b_search=link&showQueryButton=showQueryButton";
        }
        else
            self.parent.mil_body.location = "/general/sprelationmap/relation_map_drawable.do?b_init=init&showQueryButton=showQueryButton";
    }
}
function saveParam(){
    	 var show_pic=document.getElementsByName("chartParam.show_pic");
		if(show_pic){
	    	if(!show_pic[0].checked){
		    	show_pic[0].value="false";
	    	}
		}
		relationMapForm.action="/general/sprelationmap/param_config.do?b_save=save&isClose=1&show_pic="+show_pic[0].value;
		relationMapForm.submit();
}
function relationClose(){
	window.close();
}
var selectItem_id;
function selectItem(id){
	var src="/general/sprelationmap/param_config.do?b_initItem=init";
	var items=document.getElementById(id).value;
	src+="`items="+items+"`opt=init`kind=1";
	if(window.$URL){
		src = $URL.encode(src);
	}
	var iframeurl="/general/query/common/iframe_query.jsp?src="+src;
	if(getBrowseVersion()){
		var values= window.showModalDialog(iframeurl,null, 
		        "dialogWidth:650px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");	
    	if(values){
    		var obj = new Array();
    		obj[0]=values[0];
    		obj[1]=values[1];
    		document.getElementById(id).value=obj[0];
    		document.getElementById(id+"_desc").value=obj[1];
    	}
	}else{//非IE浏览器 使用open弹窗  wangb 20190315
		var iTop = (window.screen.height-30-400)/2;       //获得窗口的垂直位置;
		var iLeft = (window.screen.width-10-650)/2;        //获得窗口的水平位置;
		selectItem_id = id;
		window.open(iframeurl,'','height=400, width=650,top='+iTop+',left='+iLeft+',toolbar=no,menubar=no,scrollbars=no,resizable=no,location=no,status=no');
	}
    
}
//非ie浏览器 弹回调方法  wangb 20190315
function selectItemReturn(values){
	if(values){
    	var obj = new Array();
    	obj[0]=values[0];
    	obj[1]=values[1];
    	document.getElementById(selectItem_id).value=obj[0];
    	document.getElementById(selectItem_id+"_desc").value=obj[1];
    }
}
function printBrowse(){
	var chartObj2 = getChartFromId("sampleChart");   
	var chart1div=document.getElementById("chart1div");
    document.getElementById("printXmlData").value=getEncodeStr(chartObj2.getXMLData());
    document.myprintform.submit();
	var src="/general/sprelationmap/print_browse.jsp";
	var iframeurl="/general/query/common/iframe_query.jsp?src="+src;
    var values= window.showModalDialog(iframeurl,null, 
		        "dialogWidth:"+window.screen.width+"px; dialogHeight:"+window.screen.height+"px;resizable:no;center:yes;scroll:yes;status:no");	
}

function clearCond()
{
     document.getElementById("one").value="";
	 document.getElementById("two").value="";
	 document.getElementById("three").value="";
	 document.getElementById("four").value="";
	 
	 document.getElementsByName("field[0].hzvalue")[0].value="";
	 document.getElementsByName("field[1].hzvalue")[0].value="";
	 document.getElementsByName("field[2].hzvalue")[0].value="";
	 document.getElementsByName("field[4].value")[0].value="";
	 var vos= document.getElementsByName('upId');
	 var emp_vo=vos[0]; 
	 var a_option;
     for(i=0;i<emp_vo.options.length;i++)
     {
     	if(emp_vo.options[i].selected)
     		a_option=emp_vo.options[i];
     }
     
     for(i=emp_vo.options.length-1;i>=0;i--)
     {
     	 emp_vo.options.remove((emp_vo.options.length-1));
     }
     
     //选择部门、单位或职位时下拉类别为空添加判断，避免js脚本错误
     if(emp_vo.options.length=0){
    	 emp_vo.options[0]=a_option;
    	 emp_vo.options[0].selected=true; 
     }
}

function queryUpPerson(){
	var un=document.getElementById("one").value;
	var um=document.getElementById("two").value;
	var kk=document.getElementById("three").value;
	var aa=document.getElementById("four").value;
	if(un==''&&um==''&&kk==''&&aa==''){
		alert("请设置查询条件！");
		return;
	}
	var vos= document.getElementsByName('upId');
    var emp_vo=vos[0];
    var str="";
    for(i=0;i<emp_vo.options.length;i++)
    {
    	str+="/"+emp_vo.options[i].value;
       
    }
	var hashvo = new ParameterSet();
	hashvo.setValue("un",un);
	hashvo.setValue("um",um);
	hashvo.setValue("kk",kk);
	hashvo.setValue("aa",aa);
	hashvo.setValue("selected",str);
	hashvo.setValue("opt","query");
	var request=new Request({method:'post',onSuccess:query_ok,functionId:'302001020252'},hashvo);
}
function query_ok(param){
	var str=param.getValue("str");
	if(str!=''){
		var arr=str.split("@");
		var arrValue=arr[0].split("`");
		var arrName=arr[1].split("`");
		var vos= document.getElementsByName('upId');
	    var emp_vo=vos[0];
	    for(var i=0;i<arrValue.length;i++){
	    	var no = new Option();
	    	no.value=arrValue[i];
	    	no.text=arrName[i];
	       emp_vo.options[emp_vo.options.length]=no;
	    }
	}
}
function getDown(){
	var vos= document.getElementsByName('upId');
    var emp_vo=vos[0];
    var upId="";
    for(var i=0;i<emp_vo.options.length;i++){
    	if(emp_vo.options[i].selected)
    	{
    		upId=emp_vo.options[i].value;
    	}
    }
    if(upId=='')
    	return;
	var hashvo = new ParameterSet();
	hashvo.setValue("opt","getdown");
	hashvo.setValue("upId",upId);
	var request=new Request({method:'post',onSuccess:get_ok,functionId:'302001020252'},hashvo);
}
function get_ok(param){
	var spRelationList=param.getValue("spRelationList");
	var downPersonList=param.getValue("downPersonList");
	AjaxBind.bind(relationMapForm.spRelationId,spRelationList);
	AjaxBind.bind(relationMapForm.downId,downPersonList);
}
var date_desc;
function showDateSelectBox(srcobj)
{
	if($F('a0101')=="")
	{
		Element.hide('date_panel');
		return false ;
	}
   date_desc=document.getElementsByName(srcobj)[0];
   Element.show('date_panel');
   var pos=getAbsPosition(date_desc);
   with($('date_panel'))
   {
        style.position="absolute";
		style.left=pos[0]-1 +'px';
		if(!getBrowseVersion())
			style.top=pos[1]-90-date_desc.offsetHeight+'px';
		else
			style.top=pos[1]-78-date_desc.offsetHeight+'px';
		style.width='450px';
   }
   var hashvo = new ParameterSet();
   hashvo.setValue("selname",getEncodeStr(date_desc.value));
   hashvo.setValue("checkflag","18");
   //田野添加控制权限标记
   hashvo.setValue("priv","1");
   var request=new Request({method:'post',onSuccess:shownamelist,functionId:'10400201045'},hashvo);
}
function shownamelist(outparamters)
{
		var namelist=outparamters.getValue("namelist");
		if(namelist.length==0){
			Element.hide('date_panel');
		}
		else{
			AjaxBind.bind(relationMapForm.contenttype,namelist);
		}
}

function setSelectValue()
{
	if(date_desc)
	{
		var no = new Option();
	    no.value=$F('date_box');
	    var aaa = $('date_box');
	    var text = aaa[aaa.selectedIndex].text;
	    no.text=text;
	    var vos= document.getElementsByName('downId');
	    var emp_vo=vos[0];
	    var isC=true;
	    for(i=0;i<emp_vo.options.length;i++)
	    {
	       var select_ob=emp_vo.options[i];
	       if($F('date_box')==select_ob.value)
	       {
	          isC=false;
	       }
	    }
	    if(isC)
	    {
	      emp_vo.options[emp_vo.options.length]=no;
	    }
	    var name = $('selectname'); 
	    name.value ="";
		Element.hide('date_panel'); 
	}
}
function remove()
{
	Element.hide('date_panel');
}
function save(){
	var vos= document.getElementsByName('downId');
    var emp_vo=vos[0];
    var object_ids="";
    for(i=0;i<emp_vo.options.length;i++)
    {
       var select_ob=emp_vo.options[i];
       object_ids+="`"+select_ob.value;
    }
    var up=document.getElementsByName("upId");
    var upvo = up[0];
    var mainbody_id="";
    for(i=0;i<upvo.options.length;i++)
    {
       var select_ob=upvo.options[i];
       if(select_ob.selected){
           mainbody_id=select_ob.value;
           break;
       }
    }
    var rel=document.getElementsByName("spRelationId");
    var relat=rel[0];
    var relation_id="";
    for(i=0;i<relat.options.length;i++)
    {
       var select_ob=relat.options[i];
       if(select_ob.selected){
    	   relation_id=select_ob.value;
           break;
       }
    }
    if(relation_id=='')
    {
    	alert("请选择汇报关系！");
    	return;
    }
    if(mainbody_id=='')
    {
    	alert("请选择审批人！");
    	return;
    }
  /*  if(object_ids=='')   //允许选空
    {
    	alert("请选择下级人员！");
    	return;
    }*/
    object_ids=object_ids.substring(1);
    var hashvo = new ParameterSet();
	hashvo.setValue("opt","save");
	hashvo.setValue("object_ids",object_ids);
	hashvo.setValue("mainbody_id",mainbody_id);
	hashvo.setValue("relation_id",relation_id);
	var request=new Request({method:'post',onSuccess:save_ok,functionId:'302001020252'},hashvo);
}
function save_ok(param){
	var mess=param.getValue("mess");
	if(mess=='0'){
		alert("保存成功！");
		if(parent.opener && parent.opener.returnValue){
			parent.opener.returnValue("saved");
			parent.window.close();
		}else{
			window.returnValue="saved";
			window.close();
		}
	}else if(mess=='1'){
		alert("保存失败！");
		return;
	}else {
		alert(mess);
		return;
	}
}
function selectCopyTo(){
	 var return_vo=select_org_emp_byname_dialog(1,2,2,1);  
	 if(getBrowseVersion()){ 
	 	if(!return_vo)
	 	{
			 return;
	 	}	
	 	var isClear="0";
	 	if(confirm("确认是否清除复制对象原有汇报关系?"))
			 isClear="1";
		var to=return_vo.content;
		var vos= document.getElementsByName('downId');
    	var emp_vo=vos[0];
    	var object_ids="";
    	for(i=0;i<emp_vo.options.length;i++)
    	{
       		var select_ob=emp_vo.options[i];
       		object_ids+="`"+select_ob.value;
    	}
    	var up=document.getElementsByName("upId");
    	var upvo = up[0];
    	var mainbody_id="";
    	for(i=0;i<upvo.options.length;i++)
    	{
       		var select_ob=upvo.options[i];
       		if(select_ob.selected){
           		mainbody_id=select_ob.value;
           		break;
       		}
    	}
    	var rel=document.getElementsByName("spRelationId");
    	var relat=rel[0];
    	var relation_id="";
    	for(i=0;i<relat.options.length;i++)
    	{
       		var select_ob=relat.options[i];
       		if(select_ob.selected){
    	   		relation_id=select_ob.value;
           		break;
       		}
    	}
    	if(relation_id=='')
    	{
	    	alert("请选择汇报关系！");
    		return;
    	}
    	if(mainbody_id=='')
    	{
    		alert("请选择复制对象！");
    		return;
    	}
    	if(object_ids=='')
    	{
	    	alert("请选择下级人员！");
    		return;
    	}
    	var hashvo = new ParameterSet();
		hashvo.setValue("opt","save");
		hashvo.setValue("object_ids",object_ids);
		hashvo.setValue("from",mainbody_id);
		hashvo.setValue("to",to);
		hashvo.setValue("isClear",isClear);
		hashvo.setValue("relation_id",relation_id);
		var request=new Request({method:'post',onSuccess:copy_ok,functionId:'302001020253'},hashvo);
	}
}
//open 弹窗回调方法 wangb20190330
function openReturnValue(return_vo){
	if(!return_vo)
	 {
		 return;
	 }	
	 var isClear="0";
	 if(confirm("确认是否清除复制对象原有汇报关系?"))
		 isClear="1";
	var to=return_vo.content;
	var vos= document.getElementsByName('downId');
    var emp_vo=vos[0];
    var object_ids="";
    for(i=0;i<emp_vo.options.length;i++)
    {
       var select_ob=emp_vo.options[i];
       object_ids+="`"+select_ob.value;
    }
    var up=document.getElementsByName("upId");
    var upvo = up[0];
    var mainbody_id="";
    for(i=0;i<upvo.options.length;i++)
    {
       var select_ob=upvo.options[i];
       if(select_ob.selected){
           mainbody_id=select_ob.value;
           break;
       }
    }
    var rel=document.getElementsByName("spRelationId");
    var relat=rel[0];
    var relation_id="";
    for(i=0;i<relat.options.length;i++)
    {
       var select_ob=relat.options[i];
       if(select_ob.selected){
    	   relation_id=select_ob.value;
           break;
       }
    }
    if(relation_id=='')
    {
    	alert("请选择汇报关系！");
    	return;
    }
    if(mainbody_id=='')
    {
    	alert("请选择复制对象！");
    	return;
    }
    if(object_ids=='')
    {
    	alert("请选择下级人员！");
    	return;
    }
    var hashvo = new ParameterSet();
	hashvo.setValue("opt","save");
	hashvo.setValue("object_ids",object_ids);
	hashvo.setValue("from",mainbody_id);
	hashvo.setValue("to",to);
	hashvo.setValue("isClear",isClear);
	hashvo.setValue("relation_id",relation_id);
	var request=new Request({method:'post',onSuccess:copy_ok,functionId:'302001020253'},hashvo);
}
function copy_ok(param){
	var mess=param.getValue("mess");
	if(mess=='0')
	{
		alert("复制成功！");
	}else  if(mess=='1'){
		alert("复制失败！");
	}
	var isClear=param.getValue("isClear");
	if(isClear=='1'){
		//var spRelationList=param.getValue("spRelationList");
		var downPersonList=param.getValue("downPersonList");
		//AjaxBind.bind(relationMapForm.spRelationId,spRelationList);
		AjaxBind.bind(relationMapForm.downId,downPersonList);
	}
}
function changeDown(){
	var vos= document.getElementsByName('upId');
    var emp_vo=vos[0];
    var upId="";
    for(var i=0;i<emp_vo.options.length;i++){
    	if(emp_vo.options[i].selected)
    	{
    		upId=emp_vo.options[i].value;
    	}
    }
    var rel=document.getElementsByName("spRelationId");
    var relat=rel[0];
    var relation_id="";
    for(i=0;i<relat.options.length;i++)
    {
       var select_ob=relat.options[i];
       if(select_ob.selected){
    	   relation_id=select_ob.value;
           break;
       }
    }
    if(upId=='')
    	return;
	var hashvo = new ParameterSet();
	hashvo.setValue("opt","getdownself");
	hashvo.setValue("upId",upId);
	hashvo.setValue("relation_id",relation_id);
	var request=new Request({method:'post',onSuccess:getdown_ok,functionId:'302001020252'},hashvo);
}
function getdown_ok(param){
	var downPersonList=param.getValue("downPersonList");
	AjaxBind.bind(relationMapForm.downId,downPersonList);
}
//tianye add 动态加载某js文件方法
function insertJS(src, callback){
	 var script = document.createElement("SCRIPT"), done = false;
	  script.type = "text/javascript"; 
	  script.src = src; 
	  script.charset = "UTF-8";
	  script.onload = script.onreadystatechange = function(){ 
	   if ( !done && (!this.readyState || this.readyState == "loaded" || this.readyState == "complete") )
	    {  
	    done = true;  
	     callback(); 
	      } 
	      }; 
	      document.getElementsByTagName("head")[0].appendChild(script);
	}
//tianye add点击查询时直接展现查出结果人的审批关系图
function queryTreePerson(){
		var name =document.getElementById("queryTreePersonName").value;
		if(name==""){
			alert("查询内容不能为空！");
			return;
		}
		var hashVo ,request;
	 	insertJS("/ajax/common.js", function(){ 
	 		hashVo = new ParameterSet();
	 		insertJS("/js/codetree.js",function(){
	 				insertJS("/ajax/command.js"	,function(){ 
	 					hashVo.setValue("name","/"+getEncodeStr(name));
	 					hashVo.setValue("dbname",'Usr');
	 					request=new Request({method:'post',asynchronous:false,onSuccess:queryTreePerson_ok,functionId:'302001020255'},hashVo);
	 				});
	 		});
	 	});
	}
function queryTreePerson_ok(outparameters){
	var orgLinks = outparameters.getValue("orgLinks");
	var a_code = "" ;
	if(orgLinks.length==0)
	{
		alert("没有找到"+document.getElementById("queryTreePersonName").value+"!");	
	}
	else
	{
		var findNode = false;
		var mil_menu=window.parent.window.frames["mil_menu"];
		var obj=mil_menu.root; //只找出第一个将其展开
		obj.collapseAll();
		obj.expand();
		for(var k=0;k<orgLinks.length;k++)
		{
			var orgLink = orgLinks[k];
			var temps=orgLink.split("/");
			//var obj=mil_menu.root;//展开查询出的所有的相匹配的人员，但审批关系图显示的是第一个人的关系图(有需要可恢复)
			for(var i=temps.length-1;i>=0;i--)
			{
				for(var j=0;j<obj.childNodes.length;j++)
				{
					if(obj.childNodes[j].text==temps[i])
					{
						obj=obj.childNodes[j];
						obj.expand();
						findNode = true;
						if(a_code==""){
							a_code= temps[temps.length-1];//取出所有人员中第一个人的编号（形如：Usr0000000009）
						}
						break;
					}
				}
			}
			
		}
		if(findNode){
			var action = obj.action.substring(0,obj.action.indexOf("&"));
			//var action = "/general/sprelationmap/relation_map_drawable.do?b_init=init&relationType=1&showQueryButton=showQueryButton&a_code="+a_code;
			//从新汇报关系图进入，参数在/general/sprelationmap/yFilesMap/show_report_map.jsp中定义
			//if(obj.action.indexOf("sssss"))
			//	action = "/general/sprelationmap/show_report_map.do?b_search=link&showQueryButton=showQueryButton&a_code="+a_code;
			relationMapForm.action=action+"&showQueryButton=showQueryButton&a_code="+a_code;
			relationMapForm.submit();
		}	
		if(findNode == false)	
			alert("没有找到"+document.getElementById("queryTreePersonName").value+"!");	
	}

	}
//tianye add 由于审批关系图控件的jquery.min.js与common.js冲突只有在使用时加载common.js所以在查询前加载
function showDataSelectBoxBefore(srcobj){
	insertJS("/ajax/common.js",function(){
		insertJS("/js/codetree.js",function(){
			insertJS("/ajax/command.js"	,function(){ 
				showDataSelectBox(srcobj);
			});
		});
	});

}
//tianye add查询匹配人员放在输入框下的面板上
function showDataSelectBox(srcobj){
	if($F('a0101')=="")
	{	
		Element.hide('date_panel');
		return false ;
	}
   date_desc=document.getElementById(srcobj);
   var pattern=/^[^/(%]*$/; 
   if(!pattern.test(date_desc.value)){ 
	   alert("这里禁止输入特殊字符 '/','%'和'(' ");
	   document.getElementById(srcobj).value="";
	   return ;  
	   } 
   Element.show('date_panel');
   var pos=getAbsPosition(date_desc);
   with($('date_panel'))
   {//设置面板位置
       style.position="absolute";
       style.left=pos[0]-1;
       style.top=pos[1]+24;

       //谷歌设置posLeft、posTop属性不管用 宽度会被子元素撑开 wangbs 20190315
		// style.posLeft=pos[0]-1;
		// style.posTop=pos[1]+20;
		// style.width=450;
   }
   var hashVo = new ParameterSet();
   hashVo.setValue("name",getEncodeStr(date_desc.value));
   hashVo.setValue("dbname",'Usr');
   hashVo.setValue("priv",'1');
   request=new Request({method:'post',asynchronous:false,onSuccess:showDataSelectBox_ok,functionId:'302001020255'},hashVo);
}

function showDataSelectBox_ok(outparamters)
{		
		var namelist=outparamters.getValue("personList");
		if(namelist.length==0){
			Element.hide('date_panel');
		}
		else{
			AjaxBind.bind(relationMapForm.contenttype,namelist);
		}
		var namelist=outparamters.getValue("personList");
}
//tianye add从面板上选择人后查询人员并展开关系树和显示关系图
function setSelectPerson()
{
	if(date_desc)
	{
	    var aaa = $('date_box');
	    var text = aaa[aaa.selectedIndex].text;
	    document.getElementById("queryTreePersonName").value=text;
		Element.hide('date_panel'); 
	}
	   var hashVo = new ParameterSet();
	   hashVo.setValue("name",text);
	   hashVo.setValue("dbname",'Usr');
	   hashVo.setValue("priv",'1');
	request=new Request({method:'post',asynchronous:false,onSuccess:queryTreePerson_ok,functionId:'302001020255'},hashVo);
}