//编辑备注字段
function editMemoFild(priFld,memoFldName)
{
	var type = $F('type');
	var target_url="/train/resource/memoFld.do?b_query=link`type="+type+"`priFld="+priFld+"`memoFldName="+memoFldName;
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
	var return_vo= window.showModalDialog(iframe_url, "memoFld_win", 
	              "dialogWidth:390px; dialogHeight:360px;resizable:no;center:yes;scroll:no;status:no");	
	if(!return_vo)
	   	return;	   
   	if(return_vo.flag=="true")
    {       
       trainResourceForm.action="/train/resource/trainRescList.do?b_query=update&type="+type;
	   trainResourceForm.submit();	
	}  	
}
//更新备注字段
 function updateMemoFild()
{	 
	 var itemdesc = $F('itemdesc');
	 if(IsOverStrLength($F('memoFld'),2000)){
			alert(itemdesc+TRAIN_ROOM_MORE_LENGTH1+2000+TRAIN_ROOM_MORE_LENGTH2+1000+TRAIN_ROOM_MORE_LENGTH3);
			return;
	}
	
	var type = $F('type');
	var priFld = $F('priFld');
	var memoFldName = $F('memoFldName');
	var classid = $F('classid');
	var dbname = $F('dbname');
	trainResourceForm.action="/train/resource/memoFld.do?b_save=link&type="+type+"&oper=close&priFld="+priFld+"&memoFldName="+memoFldName+"&itemdesc="+itemdesc+"&classid="+classid+"&dbname="+dbname;
	trainResourceForm.submit();
}
//删除	
function del()
{
	var str="";
	var type = $F('type');
	for(var i=0;i<document.trainResourceForm.elements.length;i++)
	{
		if(document.trainResourceForm.elements[i].type=="checkbox")
		{
			if(document.trainResourceForm.elements[i].checked==true)
			{
				str+=document.trainResourceForm.elements[i+1].value+"/";
			}
		}
	}
	if(str.length==0)
	{
		alert("请选择要删除的记录！");
		return;
	}
	else
	{
		var hashvo=new ParameterSet();
		hashvo.setValue("deletestr",str);
		hashvo.setValue("type",type);
		var request=new Request({method:'post',asynchronous:false,onSuccess:delteacher,functionId:'202003000101'},hashvo);

		function delteacher(outparamters){
			var flag=outparamters.getValue("flag");
			if(flag=="true"){
				if (confirm("确认要删除吗?"))
				{
					document.trainResourceForm.action="/train/resource/trainRescList.do?b_del=link&type="+type+"&deletestr="+str;
					document.trainResourceForm.submit();	
				}
			}else{
				if(type=="1")
					alert(flag+TRAIN_DELETE_RESC1);
				if(type=="2")
					alert(flag+TRAIN_DELETE_RESC2);
				if(type=="3")
					alert(flag+TRAIN_DELETE_RESC3);
				if(type=="4")
					alert(flag+TRAIN_DELETE_RESC4);
				if(type=="5")
					alert(flag+TRAIN_DELETE_RESC5);
			}
		}
	}
}
//办理
function facility(r1101,r1102,r1107)
{
	var type = $F('type');
	trainResourceForm.action="/train/resource/facility/facilityinfo.do?b_query=link&type=" + type + "&number=" + r1107 + "&fieldId=" + r1101 + "&fieldName=" + $URL.encode(getEncodeStr(r1102));
	trainResourceForm.submit();
}
//编辑
function edit(priFldValue)
{
	var type = $F('type');
	var aa = "2";
	trainResourceForm.action="/train/resource/trainRescAdd.do?b_query=link&type="+type+"&priFldValue="+priFldValue+"&aa="+aa;
	trainResourceForm.submit();
}
//浏览
function browse(priFldValue)
{
	var type = $F('type');
	trainResourceForm.action="/train/resource/trainRescAdd.do?b_browse=link&type="+type+"&priFldValue="+priFldValue;
	trainResourceForm.submit();
}
//新建
function add()
{
	var type = $F('type');
	trainResourceForm.action="/train/resource/trainRescAdd.do?b_query=link&type="+type;
	trainResourceForm.submit();
}
//刷新主页面
function freshMain(a_code)
{
	var type = $F('type');
	if (type == '5') {
		trainResourceForm.action = "/train/resource/trainRescList.do?b_query=return&type=" + type + "&a_code=" + a_code;
		trainResourceForm.submit();
	} else {
		trainResourceForm.action = "/train/resource/trainRescList.do?b_query=return&type=" + type;
		trainResourceForm.submit();
	}
}
//保存
function save(oper)
{	
	var type = $F('type');
	if(oper=='saveClose'){
		trainResourceForm.action="/train/resource/trainRescList.do?b_save=link&oper="+oper+"&type="+type; 
		trainResourceForm.submit();}
	else{
		trainResourceForm.action="/train/resource/trainRescAdd.do?b_saveContinue=link&oper="+oper+"&type="+type; 
	trainResourceForm.submit();	
	}
}
//通用查询
function search(msg)
{	
	var type = $F('type');
	var recTable = $F('recTable');
	var thecodeurl ="/train/traincourse/generalsearch.do?b_query=link&fieldsetid="+recTable+"&msg="+msg; 
	var return_vo= window.showModalDialog(thecodeurl, "", 
              	"dialogWidth:750px; dialogHeight:410px;resizable:no;center:yes;scroll:no;status:no");
    if(return_vo!=null) 
    {
    	trainResourceForm.strParam.value=return_vo;
    	trainResourceForm.action="/train/resource/trainRescList.do?b_search=link&type="+type;
  		trainResourceForm.submit();	
    }
}
function search2(msg)
{	
	var code = $F('code');
	var thecodeurl ="/train/traincourse/generalsearch.do?b_query=link&fieldsetid=r13&msg="+msg; 
	var return_vo= window.showModalDialog(thecodeurl, "", 
              	"dialogWidth:750px; dialogHeight:410px;resizable:no;center:yes;scroll:no;status:no");
    if(return_vo!=null) 
    {
    	trainProjectForm.strParam.value=return_vo;
    	trainProjectForm.action="/train/resource/trainProList.do?b_search=link&code="+code;
  		trainProjectForm.submit();	
    }
}
//导出Excel
function exportExcel()
{	
	var hashvo=new ParameterSet();
	hashvo.setValue("tablename",$F('recTable'));
	var request=new Request({method:'post',asynchronous:false,onSuccess:showfile,functionId:'2020040023'},hashvo);
}
//使用详情
function trainroom()
{		
		var type = $F('type');
		document.trainResourceForm.action="/train/resource/trainroom/selftrainroom.do?b_query=link&type=" + type;
		document.trainResourceForm.submit();
}
function showfile(outparamters)
{
	var outName=outparamters.getValue("outName");
	var name=outName;
	var names = outparamters.getValue("names");
	var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+name,"excel");
	
}
function exportExcel2()
{
	var hashvo=new ParameterSet();
	hashvo.setValue("tablename",'r13');
	var request=new Request({method:'post',asynchronous:false,onSuccess:showfile,functionId:'2020040023'},hashvo);
}
//培训项目编辑备注字段
function editMemoFild2(priFld,memoFldName)
{
	var target_url="/train/resource/memoFld.do?b_query=link`type=6`priFld="+priFld+"`memoFldName="+memoFldName;
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
	var return_vo= window.showModalDialog(iframe_url, "memoFld_win", 
	              "dialogWidth:390px; dialogHeight:360px;resizable:no;center:yes;scroll:no;status:no");	
	if(!return_vo)
	   	return;	   
   	if(return_vo.flag=="true")
    {       
       trainProjectForm.action="/train/resource/trainProList.do?b_query=return";
	   trainProjectForm.submit();	
	}  	
}
//培训项目新建
function add2()
{
	var code = $F('code');
	if(code=='')
	{
		alert('请选中上级项目结点！');
		return;
	}
	trainProjectForm.action="/train/resource/trainProAdd.do?b_query=link&code="+code;
	trainProjectForm.submit();
}
//培训项目编辑
function edit2(priFldValue)
{
	var code = $F('code');
	trainProjectForm.action="/train/resource/trainProAdd.do?b_query=link&priFldValue="+priFldValue+"&code="+code;
	trainProjectForm.submit();
}

//刷新主页面
function freshMain2()
{
	var code = $F('code');
	trainProjectForm.action="/train/resource/trainProList.do?b_query=lode&code="+code;
	trainProjectForm.submit();
}

//保存
function save2(oper,type)
{
	var code = $F('code');
	var r1301 = $F('r1301');
	var r3101value  = $F('r3101value');
	var r1302 = $F('r1302').replace(/(^\s*)|(\s*$)/g, "");
	var b0110 = $F('b0110').replace(/(^\s*)|(\s*$)/g, "");
	if(r1302=='')
	{
		alert('请输入项目名称！');
		return;
	}
	if(b0110=='')
	{
		alert(SEL_UN);
		return;
	}
	var flag=true;
	var currnode=parent.frames['mil_menu'].Global.selectedItem;	
	
    if(currnode==null)
		return;
	
	if(!currnode.load)//如果没有展开	currnode.load返回false
		currnode.expand();
		
	if(r3101value==currnode.uid)
	{
		flag=false;			
		if(r1302!=currnode.text)
		{
			currnode.setText(r1302);
			currnode.reload();
		}
	}
	
	for(var i=0;i<currnode.childNodes.length;i++){
		if(r3101value==currnode.childNodes[i].uid)
		{
			flag=false;			
			if(r1302!=currnode.childNodes[i].text)
			{
				currnode.childNodes[i].setText(r1302);
				currnode.reload();
			}
		}								
	}
	if(flag&&type)
	{
		var action='/train/resource/trainProList.do?b_query=link&code='+r3101value;
		var imgurl='/images/close.png';
		var xml='/train/resource/trainProTree.jsp?code='+r3101value;
		parent.frames['mil_menu'].add(r3101value,r1302,action,"mil_body",r1302,imgurl,xml);
	}
	
	var priFldValue=$F('priFldValue');
	if(oper=='saveClose')
		trainProjectForm.action="/train/resource/trainProList.do?b_save=link&oper="+oper+"&code="+code+"&priFldValue="+r3101value; 
	else
		trainProjectForm.action="/train/resource/trainProAdd.do?b_saveContinue=link&oper="+oper+"&code="+code+"&priFldValue="+r3101value; 
	trainProjectForm.submit();	
}
//培训项目删除	
function del2()
{
	var code = $F('code');
	var str="";
	for(var i=0;i<document.trainProjectForm.elements.length;i++)
	{
		if(document.trainProjectForm.elements[i].type=="checkbox")
		{
			if(document.trainProjectForm.elements[i].checked==true)
			{
				str+="/"+document.trainProjectForm.elements[i+1].value;
			}
		}
	}
	if(str.length==0)
	{
		alert("请选择要删除的记录！");
		return;
	}
	else
	{
		var hashvo=new ParameterSet();
		hashvo.setValue("deletestr",str);
		hashvo.setValue("type","0");
		var request=new Request({method:'post',asynchronous:false,onSuccess:delteacher,functionId:'202003000101'},hashvo);

		function delteacher(outparamters){
			var flag=outparamters.getValue("flag");
			if(flag=="true"){
				if (confirm("确认要删除吗?"))
				{
					var In_paramters = "flag=ok";
				    var hashvo=new ParameterSet();	
				    hashvo.setValue("type","6");
					hashvo.setValue("deletestr",str.substring(1));
					var request=new Request(
							{
						     method:'post',
							 asynchronous:false,
						     parameters:In_paramters,
						     onSuccess:DelNode,
						     functionId:'2020030011'
						    },
						    hashvo
						);
				}
			}else
				alert(flag+TRAIN_DELETE_RESC0)
		}
	}
}

function DelNode(outparamters){
	var flag = outparamters.getValue("flag");
	if(flag != 'ok'){
		alert("删除失败");
		return;
	}
		
	var str="";
	for(var i=0;i<document.trainProjectForm.elements.length;i++)
	{
		if(document.trainProjectForm.elements[i].type=="checkbox")
		{
			if(document.trainProjectForm.elements[i].checked==true)
			{
				str+="/"+document.trainProjectForm.elements[i+1].value;
			}
		}
	}
	
	var delSelf = false;
	var currnode = parent.frames['mil_menu'].Global.selectedItem;
	var curRoot = currnode.parent;
    var nodes = str.substring(1).split('/');
    for(var j=0;j<nodes.length;j++)
    {
        if(currnode!=null){ 
            if(nodes[j]==currnode.uid){
                currnode.remove();  
                delSelf = true;
            } else if(currnode.load){           
				for(var i=0;i<=currnode.childNodes.length-1;i++)
				{
					if(nodes[j]==currnode.childNodes[i].uid)
						currnode.childNodes[i].remove();
				 }		
			}			  	
        }			  	
	}
    if(currnode!=null && delSelf)
        curRoot.select();
    else
    	currnode.select();
}
//输入数值型
	function IsDigit(obj) 
	{
		if((event.keyCode >= 46) && (event.keyCode <= 57) && event.keyCode!=47)
		{
			var values=obj.value;
			if((event.keyCode == 46) && (values.indexOf(".")!=-1))//有两个.
				return false;
			if((event.keyCode == 46) && (values.length==0))//首位是.
				return false;	
			return true;
		}
			return false;	
	}
	//输入整数
	function IsDigit2(obj) 
	{
		if((event.keyCode >47) && (event.keyCode <= 57))
			return true;
		else
			return false;	
	}
	
	
function isNumber(obj)
{
	var checkStr = obj.value;
	if (checkStr=="")
		return;
	
	if(isNaN(checkStr) || (checkStr.indexOf('-') != checkStr.lastIndexOf('-'))){
		alert(INPUT_NOTNUMBER_VALUE);
  		obj.focus();
  		obj.value='';
  		return;
	}
} 
function returnFirst(){
   	self.parent.location= "/general/tipwizard/tipwizard.do?br_train=link";
}
function returnFirstPage(){
   	document.location= "/general/tipwizard/tipwizard.do?br_train=link";
}
function uploadFile(r0701,type){
	var target_url="/train/resource/file_upload.do?b_query=link`r0701="+r0701+"`type="+type+"`myself=1";
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
	var return_vo= window.showModalDialog(iframe_url, "memoFld_win", 
	              "dialogWidth:700px; dialogHeight:380px;resizable:no;center:yes;scroll:no;status:no");
	if(return_vo){
		document.getElementById(r0701+"_1").innerHTML="附件<img src='/images/amail_1.gif' border=0>";
	}else{
		document.getElementById(r0701+"_1").innerHTML="附件";
	}
}
//输入float
	function IsDigit1(obj) 
	{
		if((event.keyCode >= 46) && (event.keyCode <= 57) && event.keyCode!=47)
		{
			var values=obj.value;
			if((event.keyCode == 46) && (values.indexOf(".")!=-1))//有两个.
				return false;
			if((event.keyCode == 46) && (values.length==0))//首位是.
				return false;	
			
			if(values.length>=13){
				if((event.keyCode != 46))
					return false;
			}
			return true;
		}
			return false;	
	}
	function showAssess(table,id){
		var target_url="/train/trainCosts/trainAssess.do?b_query=link&table="+table+"&id="+id;
 		//var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
		window.showModalDialog(target_url, "memoFld_win", 
	              "dialogWidth:710px; dialogHeight:400px;resizable:no;center:yes;scroll:no;status:no");	
	}
	
	function seachteach(id){
		trainResourceForm.action="/train/resource/trainRescList/teachlesson.do?b_teachlesson=link&teacherid="+id;
	    trainResourceForm.submit();
	}
	// 校验日期类型数据长度
	function IsOverStrLength(str,len)
	{
	   
	   return str.replace(/[^\x00-\xff]/g,"**").length>len
	   
	}
	// 校验日期类型数据是否符合
	function isValidDate(day, month, year, sum) {
	    if ((month < 1 || month > 12) && sum>4) {
	        return false;
	    }
	    if ((day < 1 || day > 31) && sum>7) {
	        return false;
	    }
	    if ((month == 4 || month == 6 || month == 9 || month == 11) &&
	        (day == 31) && sum>7) {
	         return false;
	    }
	    if (month == 2 && sum > 4) {
	         var leap = (year % 4 == 0 &&
	                 (year % 100 != 0 || year % 400 == 0));
	         if ((day>29 || (day == 29 && !leap)) && sum > 7) {
	             return false;
	         }
	    }
	    return true;
	}
	// 校验日期类型数据
	function checkdate(obj,aitemdesc,val)
	{
		var dd=true;
		var itemdesc="";
		var formd = "";
		var sep = "";
		var sep1 = "";
		if (val>=10 || !val) {
			formd = "yyyy-mm-dd";
		} else if (val=7) {
			formd = "yyyy-mm";
		} else if (val=4) {
			formd = "yyyy";
		}
		if(aitemdesc==null||aitemdesc==undefined)
			itemdesc="日期";
		else 
			itemdesc=aitemdesc;
		var sum = formd.length;
		if(trim(obj.value).length!=0)
		{						
			var myReg =/^(-?\d+)(\.\d+)?$/;
			if(IsOverStrLength(obj.value,sum))
			{
				alert(itemdesc+"格式不正确，正确格式为"+formd+"！");
				return false;
			}
			else
			{
			 	if(trim(obj.value).length!=sum)
			 	{
			 		 alert(itemdesc+"格式不正确，正确格式为"+formd+"！");
					 return false;
			 	}
				var year=obj.value.substring(0,4);
				var month=0;
				if(sum>4)
				{
					sep = obj.value.substring(4,5);
					sep1 = formd.substring(4,5);
					if(sep != sep1){
						 alert(itemdesc+"格式不正确，正确格式为"+formd+"！");
						 return false;
					}
					month=obj.value.substring(5,7);
				}
				var day=0;
				if(sum>7){
					sep = obj.value.substring(7,8);
					sep1 = formd.substring(7,8);
					if(sep != sep1){
						 alert(itemdesc+"格式不正确，正确格式为"+formd+"！");
						 return false;
					}
					day=obj.value.substring(8,10);
				}
				if(!myReg.test(year)||!myReg.test(month)||!myReg.test(day)) 
				{
					 alert(itemdesc+"格式不正确，正确格式为"+formd+"！");
					 return false;
			 	}
			 	
				if(year<1753) {
					alert(itemdesc+"的年份最小为1753！"); return
					false;
				}
				
						 	
			 	if(!isValidDate(day, month, year,sum))
			 	{
					 alert(itemdesc+"错误，无效时间！");
					 return false;
			 	}
			}
		}
		return dd
	}
	//校验数值整数与小数的长度。
	function checkNumItem(id,integral,decimal){
		var obj=document.getElementById(id);
		isNumber(obj);
		var checkStr = obj.value;
		if (checkStr=="")
			return;
		
		if(checkStr.indexOf(".")!=-1){
			var num = checkStr.split(".");
			if(num[0].length > integral){
				alert(TRAIN_NUMBER_TOLENGTH);
				obj.focus();
		  		obj.value='';
			}else if(num[1].length > decimal){
				alert(TRAIN_DECIMAL_TOLENGTH);
				obj.focus();
		  		obj.value='';
			}
		}else{
			if(checkStr.length > integral){
				alert(TRAIN_NUMBER_TOLENGTH);
				obj.focus();
		  		obj.value='';
			}
		}
	}
	
	