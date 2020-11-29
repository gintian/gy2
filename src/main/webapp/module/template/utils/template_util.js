//模板参数属性js
document.write("<script type='text/javascript' src='"+rootPath+"/module/template/utils/template_property.js'></script>");
//卡片样式
document.write("<link rel='stylesheet' href='"+rootPath+"/module/template/templatecard/card.css' type='text/css' />");
document.write("<link rel='stylesheet' href='"+rootPath+"/module/template/templatemain/main.css' type='text/css' />");
/**开始 公用控件，列表卡片公用*/
   //上传文件
document.write("<script type='text/javascript' src='"+rootPath+"/components/fileupload/FileUpLoad.js'></script>");
    //选人
document.write("<script type='text/javascript' src='"+rootPath+"/components/personPicker/PersonPicker.js'></script>");
//树形代码选择
document.write("<script type='text/javascript' src='"+rootPath+"/components/codeSelector/codeSelector.js'></script>");
/**结束 公用控件，列表卡片公用*/
document.write("<script type='text/javascript' src='"+rootPath+"/ckeditor/ckeditor.js'></script>");
document.write("<script type='text/javascript' src='"+rootPath+"/ckfinder/ckfinder.js'></script>");
//卡片必须类 ，初始化数据结构等
document.write("<script type='text/javascript' src='"+rootPath+"/module/template/templatecard/FieldSet.js'></script>");
//第一次进入卡片或切换人员及切换页签，返回刷新等 需要初始化单元格数据等 fieldset.js 调用cardview.js中方法找不到，故将CardView.js引用放到此处
document.write("<script type='text/javascript' src='"+rootPath+"/module/template/templatecard/CardView.js'></script>");
//电子签章引用js
document.write("<script type='text/javascript' src='"+rootPath+"/jquery/jquery-3.5.1.min.js'></script>");
document.write("<script type='text/javascript' src='"+rootPath+"/module/template/signature/encryptionlock/websocket.js'></script>");
document.write("<script type='text/javascript' src='"+rootPath+"/module/template/signature/encryptionlock/RockeyArmCtrl.js'></script>");
document.write("<script type='text/javascript' src='"+rootPath+"/module/template/signature/encryptionlock/base64.js'></script>");
document.write("<script type='text/javascript' src='"+rootPath+"/module/template/templatecard/TemplateSignature.js'></script>");
document.write("<script type='text/javascript' src='"+rootPath+"/module/template/templatecard/Signature_BJCA.js'></script>");
document.write("<script type='text/javascript' src='"+rootPath+"/module/template/templatecard/Signature_JGKJ.js'></script>");
document.write("<script type='text/javascript' src='"+rootPath+"/module/template/templatecard/Signature_Mine.js'></script>");
document.write("<script type='text/javascript' src='"+rootPath+"/module/template/signature/encryptionlock/Signature_lock.js'></script>");
//jdk1.8 url传参后台乱码处理引用js
document.write("<script type='text/javascript' src='"+rootPath+"/js/hjsjUrlEncode.js'></script>");
//金格科技电子签章html5
document.write("<link rel='stylesheet' href='"+rootPath+"/module/template/signature/signature_jgkj/kinggrid/dialog/artDialog/ui-dialog.css'>");
document.write("<link rel='stylesheet' href='"+rootPath+"/module/template/signature/signature_jgkj/kinggrid/core/kinggrid.plus.css'>");
document.write("<script type='text/javascript' src='"+rootPath+"/module/template/signature/signature_jgkj/kinggrid/html2canvas.min.js'></script>");
document.write("<script type='text/javascript' src='"+rootPath+"/module/template/signature/signature_jgkj/kinggrid/core/kinggrid.min.js'></script>");
document.write("<script type='text/javascript' src='"+rootPath+"/module/template/signature/signature_jgkj/kinggrid/core/kinggrid.plus.min.js'></script>");
document.write("<script type='text/javascript' src='"+rootPath+"/module/template/signature/signature_jgkj/kinggrid/dialog/artDialog/dialog-min.js'></script>");
document.write("<script type='text/javascript' src='"+rootPath+"/module/template/signature/signature_jgkj/kinggrid/signature.min.js'></script>");
document.write("<script type='text/javascript' src='"+rootPath+"/module/template/signature/signature_jgkj/kinggrid/signature.pc.min.js'></script>");
document.write("<script type='text/javascript' src='"+rootPath+"/module/template/signature/signature_jgkj/kinggrid/password.min.js'></script>");

document.write("<script type='text/javascript' src='"+rootPath+"/module/template/templatecard/Signature_JGKJHTML5.js'></script>");
/**
 * 异动模板公用js
*/

/**
 * 初始化传送参数，将公共参数放入map中，调用交易类前都可先调用此方法。
*/  
function initPublicParam(map,templPropety) {
    map.put("sys_type",templPropety.sys_type);
    map.put("module_id",templPropety.module_id);
    map.put("return_flag",templPropety.return_flag);
    map.put("approve_flag",templPropety.approve_flag);    
    map.put("tab_id",templPropety.tab_id);     
    map.put("task_id",templPropety.task_id);
    map.put("view_type",templPropety.view_type);
    map.put("infor_type",templPropety.infor_type);
    map.put("other_param",templPropety.other_param);
    map.put("isMessage",templPropety.isMessage);
    map.put("sp_flag",templPropety.sp_flag);
}  

/**
 * 解析url中带的参数 返回Object
*/  
function getRequest(url) {  
	  var theRequest = new Object();
	  if (url.indexOf("?") != -1) {
	      var str = url.substr(1);

	      strs = str.split("&");
	      for(var i = 0; i < strs.length; i ++) {
	    	  var param = strs[i];
	    	  var params=param.split("=");
	    	  /** start liubaoqi  针对url中参数含有等于号=的情况   param=pra=1`am=3*/
	    	  if(params.length>2){
	    	  	for(var p =2;p<params.length;p++){
	    	  		params[1] += ("="+params[p]);
	    	  	}
	    	  }
	    	  /** 2017-05-24 16:50:31 end*/
	    	  if (params.length>1){
	            theRequest[params[0]]=params[1];
	          }else      
	        	theRequest[params[0]]="";  
    	      }
    	   }
    	   return theRequest;
  }  

/**
 * title提示
 * @param id
 * @param msg
 * @return
 */
function templateTip(id,msg){
	var tip = Ext.getCmp(id);
	if(!!!tip){
		tip = Ext.create('Ext.tip.ToolTip', {
			id:id+"_tip",
			shadow:false,
		    //trackMouse: true,
			bodyStyle:"background-color:white;border:1px solid #c5c5c5;",
			target: id,
			html: msg
		});
	}
}


/**
 * 解析xml 
*/  
function loadXMLString(dname){
  try //Internet Explorer
  {
	  xmlDoc=new ActiveXObject("Microsoft.XMLDOM");
	  xmlDoc.async="false";
	  xmlDoc.loadXML(dname);
  }
  catch(e)
  {
	  try //Firefox, Mozilla, Opera, etc.
	    {
		    parser=new DOMParser();
		    xmlDoc=parser.parseFromString(dname,"text/xml");
	    }
	  catch(e) {
	  	Ext.showAlert(e.message);
	 }
  }
  return xmlDoc;
} 
/**
 * 将xml document对象转成xml
 * @param elem
 * @returns
 */
function XMLtoString(elem){ 
	var serialized; 
	try { 
		// XMLSerializer exists in current Mozilla browsers 
		serializer = new XMLSerializer(); 
		serialized = serializer.serializeToString(elem); 
	} 
	catch (e) { 
		// Internet Explorer has a different approach to serializing XML 
		serialized = elem.xml; 
	} 
	return serialized; 
}
/*
* 替换字符串中所有要替换的字符串
* text  指定的文本
* replacement 指定的旧字符
* target 指定的新字符
*/
function replaceAll(text,replacement,target){
    if(text==null||text==""){
    	return text;
    }
    if(replacement==null||replacement==""){ 
    	return text;
    }
    if(target==null) target="";
    var returnString="";
    var index=text.indexOf(replacement);
    while(index!=-1){
        if(index!=0) returnString+=text.substring(0,index)+target;
        text=text.substring(index+replacement.length);
        if(index==0) text=target+text;
        index=text.indexOf(replacement);
    }
    if(text!=""){
		returnString+=text;
	}
    return returnString;
}
/*
获取绝对位置
*/

function getAbsPosition(obj, offsetObj){
	var _offsetObj=(offsetObj)?offsetObj:document.body;
	var x=obj.offsetLeft;
	var y=obj.offsetTop;
	var tmpObj=obj.offsetParent;

	while ((tmpObj!=_offsetObj) && tmpObj){
		x += tmpObj.offsetLeft - tmpObj.scrollLeft + tmpObj.clientLeft;
		y += tmpObj.offsetTop - tmpObj.scrollTop + tmpObj.clientTop;
		tmpObj=tmpObj.offsetParent;
	}
	return ([x, y]);
}

/**
*验证是否是数字
**/
function checkIsNum(value)
{
	return /^-?\d+(\.\d+)?$/.test(value);
}

var Digit = {};
/**
 * 四舍五入法截取一个小数
 * @param float digit 要格式化的数字
 * @param integer length 要保留的小数位数
 * @return float
 */
Digit.round = function(digit, length) {
    length = length ? parseInt(length,10) : 0;
    if (length <= 0) return Math.round(digit);
    digit = Math.round(digit * Math.pow(10, length)) / Math.pow(10, length);
    return digit;
};
function isIntOrNull(str){
	if(str==null||typeof(str)=='undefined'){
		return 'undefined';
	}//判断对象是否存在
	return isNull(str)||isInt(str);
}
//必需是整数
function isInt(str){
	var reg = /^(-|\+)?\d+$/ ;
	return reg.test(str);
}
function isNull(str){
	str=strTrim(str);
	if(str.length>0)
		return false;
	return true;
}

function strTrim(str){
	str=str.replace(/^\s+|\s+$/g,'');
	return str;
}

/**
 * 解析日期
 */
function parseTemplateDate(str) {
    if (typeof str == 'string') {
        var results = str.match(/^ *(\d{4})-(\d{1,2})-(\d{1,2}) *$/);
        if (results && results.length > 3){
            return new Date(parseInt(results[1],10), parseInt(results[2],10) - 1, parseInt(results[3],10));
        }
        results = str.match(/^ *(\d{4})-(\d{1,2})-(\d{1,2}) +(\d{1,2}):(\d{1,2}):(\d{1,2}) *$/);
        if (results && results.length > 6){
            return new Date(parseInt(results[1],10), parseInt(results[2],10) - 1, parseInt(results[3],10), 
                parseInt(results[4],10), parseInt(results[5],10), parseInt(results[6],10));
        }
       results = str.match(/^ *(\d{4})-(\d{1,2})-(\d{1,2}) +(\d{1,2}):(\d{1,2}):(\d{1,2})\.(\d{1,9}) *$/);
        if (results && results.length > 7){
            return new Date(parseInt(results[1],10), parseInt(results[2],10) - 1, parseInt(results[3],10), 
            parseInt(results[4],10), parseInt(results[5],10), parseInt(results[6],10), 
            parseInt(results[7],10));
        }
        //匹配格式：2016-03-02 10:30
        results = str.match(/^ *(\d{4})-(\d{1,2})-(\d{1,2})\s+(\d{1,2}):(\d{1,2})*$/);
        var year = parseInt(results[1],10);
        var month = parseint(results[2],10)-1;
        var day = parseint(results[3],10);
        var hour = parseint(results[4],10);
        var min = parseint(results[5],10);
        var date = new Date(year,month,day,hour,min); 
        if (results && results.length > 5){
            return date;
        }
    }
    return null;
}
//字符串转int  区分是否以‘0’开头
function parseint(value){
	if(value.substring(0,1)=='0'){
       	value = parseInt(value,16);
    }else{
       	value = parseInt(value,10);
    }
    return value;
}
function getTwoDigitNumber(value) { 
   var strResult=value;
   if(value<10){
        strResult="0"+value
   }
   return strResult;
}   
/**
*手动输入日期型指标  自动补全以及格式验证
*/ 
function checkDateFormat(dateValue,format){
	if(dateValue=='')
		return;
	format = format + "";
	var ischeck = true;
	var disformat= parseInt(format,10);
	var index = dateValue.indexOf("-");
	var dataArray=dateValue.match(/^ *(\d{0,4}|[〇零一二三四五六七八九十]{0,4}){0,1}[-|.|年|/]{0,1}(\d{1,2}|[〇零一二三四五六七八九十]{1,3}){0,1}[-|.|月|/]{0,1}(\d{1,2}|[〇零一二三四五六七八九十]{1,3}){0,1}[日|]{0,1} ?(\d{1,2}|[〇零一二三四五六七八九十]{1,3}){0,1}[:|时]{0,1}(\d{1,2}|[〇零一二三四五六七八九十]{1,3}){0,1}[:|分]{0,1}(\d{1,2}|[〇零一二三四五六七八九十]{1,3}){0,1}[:|秒]{0,1} *$/);
	var date=new Date();
	var checktest = /^[0-9]*$/;
	var year=date.getFullYear();
	var month=1;
	var day=1;
	var hours=0;
	var minutes=0;
	var seconds=0;
	if(dataArray!=null){
	    year=dataArray[1]==null||dataArray[1].length==0?date.getFullYear():(checktest.test(dataArray[1])?dataArray[1]:exchangCnToNum(dataArray[1]));
		month=dataArray[2]==null||dataArray[2].length==0?month:(checktest.test(dataArray[2])?dataArray[2]:exchangCnToNum(dataArray[2]));
		day=dataArray[3]==null||dataArray[3].length==0?day:(checktest.test(dataArray[3])?dataArray[3]:exchangCnToNum(dataArray[3]));
		hours=dataArray[4]==null||dataArray[4].length==0?hours:(checktest.test(dataArray[4])?dataArray[4]:exchangCnToNum(dataArray[4]));
		minutes=dataArray[5]==null||dataArray[5].length==0?minutes:(checktest.test(dataArray[5])?dataArray[5]:exchangCnToNum(dataArray[5]));
		seconds=dataArray[6]==null||dataArray[6].length==0?seconds:(checktest.test(dataArray[6])?dataArray[6]:exchangCnToNum(dataArray[6]));
		var seperator1 = ".";
	    var seperator2 = ":";
	    if(parseInt(month,10)==0)
	    {
	    	month=1;
	    }
	    if(parseInt(day,10)==0)
	    {
	    	day=1;
	    }
	    if ((disformat==6||disformat==7||disformat==9||disformat==11||disformat==22||disformat==23||disformat==24||disformat==25)&&parseInt(month,10) >= 1 && parseInt(month,10) <= 9) {
	        month = "0" + parseInt(month,10);
	    }
	    else
	    {
	    	 month = parseInt(month,10)+"";
	    }
	    if ((disformat==21||disformat==23||disformat==24||disformat==25||disformat==6)&&parseInt(day,10) >= 1 && parseInt(day,10) <= 9) {
	        day = "0" + parseInt(day,10);
	    }
	    else
	    {
	    	 day = parseInt(day,10)+"";
	    }
	    if (parseInt(hours,10) >= 0 && parseInt(hours,10) <= 9) {
	        hours = "0" + parseInt(hours,10);
	    }
	    if (parseInt(minutes,10) >= 0 && parseInt(minutes,10) <= 9) {
	        minutes = "0" + parseInt(minutes,10);
	    }
	    if (parseInt(seconds,10) >= 0 && parseInt(seconds,10) <= 9) {
	        seconds = "0" + parseInt(seconds,10);
	    }
	    switch (disformat) {
	   		case 12:// 一九九一年一月二日
	        case 14:// 1991年1月2日
	        case 23:// 1999年02月03日
	        case 6: // 1991.12.3  注意：因为这种格式是默认格式 都是按1991.12.03显示。
	        case 24: // 1990.01.03
	            var a = /^(\d{4}).(\d{1,2}).(\d{1,2})$/;
	            var extecp=""
	            if(index>-1)
					extecp = "如YYYY-MM-DD";
				else
				    extecp = "如YYYY.MM.DD";
	        	dateValue=year+seperator1+month+seperator1+day;
				if (a.test(dateValue)) { 
					ischeck= dateValue ;
				}else{
					ischeck= "false" ;
					Ext.showAlert(MB.MSG.inputRightDateFormat+extecp);
				}
				break;
	        case 16:// 91年1月2日
	        case 7: // 91.12.3
	            var a = /^(\d{2}).(\d{1,2}).(\d{1,2})$/;
	            if(index>-1)
					extecp = "如YY-MM-DD";
				else
				    extecp = "如YY.MM.DD";
	            dateValue=(year.length==4?year.substring(2,4):year)+seperator1+month+seperator1+day;
	            if (a.test(dateValue)) { 
					ischeck= dateValue ;
				}else{
					ischeck= "false" ;
					Ext.showAlert(MB.MSG.inputRightDateFormat+extecp);
				}
	            break;
	        case 13:// 一九九一年一月
	        case 15:// 1991年1月
	        case 22:// 1999年02月
	        case 8:// 1992.2
	        case 9:// 1991.02
	            var a = /^(\d{4}).(\d{1,2})$/;
	            if(index>-1)
					extecp = "如YYYY-MM";
				else
				    extecp = "如YYYY.MM";
	            dateValue=year+seperator1+month;
	        	if (a.test(dateValue)) { 
					ischeck= dateValue ;
				}else{
					ischeck= "false" ;
					Ext.showAlert(MB.MSG.inputRightDateFormat+extecp);
				}
	        	break;
	        case 17:// 91年1月
	        case 10:// 92.2
	        case 11:// 92.02
	        	var a = /^(\d{2}).(\d{1,2})$/;
	        	if(index>-1)
					extecp = "如YY-MM";
				else
				    extecp = "如YY.MM";
	        	dateValue=(year.length==4?year.substring(2,4):year)+seperator1+month;
	        	if (a.test(dateValue)) { 
					ischeck= dateValue ;
				}else{
					ischeck= "false" ;
					Ext.showAlert(MB.MSG.inputRightDateFormat+extecp);
				}
	            break;
	        case 18:// 年龄
	        	var date=new Date();
	        	var year=date.getFullYear();
	        	var month=date.getMonth()+1;
	        	var day=date.getDate();
	        	var result=year-parseInt(yy,10);
	        	if(month<parseInt(mm,10))
	        	{
	        		result=result-1>0?result-1:0;
	        	}
	        	else
	        	{
	        		if(month==parseInt(mm,10))
	        		{
	        			if(day<parseInt(dd,10))
	        			{
	        				result=result-1>0?result-1:0;
	        			}
	        		}
	        	}
	        	strResult=result;
	            break;
	        case 19:// 1991（年）
	        	var a = /^(\d{4})$/;
	        	var extecp = "如YYYY";
	        	if (a.test(year)) { 
					ischeck= year ;
				}else{
					ischeck= "false" ;
					Ext.showAlert(MB.MSG.inputRightDateFormat+extecp);
				}
	            break;
	        case 20:// 1 （月）
	        	var a = /^(\d{1,2})$/;
	        	var extecp = "如MM";
	        	if (a.test(month)) { 
					ischeck= month+"" ;
				}else{
					ischeck= "false" ;
					Ext.showAlert(MB.MSG.inputRightDateFormat+extecp);
				}
	            break;
	        case 21:// 23 （日）
	       		var a = /^(\d{1,2})$/;
				var extecp = "如DD";
	      		if (a.test(day)) { 
					ischeck= day+"";
				}else{
					ischeck= "false" ;
					Ext.showAlert(MB.MSG.inputRightDateFormat+extecp);
				}
	            break;
	         case 25: // 1990.01.03 10:10
				var a = c = /^ *(\d{4}).(\d{1,2}).(\d{1,2})\s+([0-1]?[0-9]|[2][0-3]):([0-5][0-9])$/;
				var extecp = "";
				if(index>-1)
					extecp = "如YYYY-MM-DD HH:MM";
				else
				    extecp = "如YYYY.MM.DD HH:MM";
				dateValue=year+seperator1+month+seperator1+day+" "+hours+seperator2+minutes;
				if (a.test(dateValue)) { 
					ischeck= dateValue ;
				}else{
					ischeck= "false" ;
					Ext.showAlert(MB.MSG.inputRightDateFormat+extecp);
				}
	            break;
	     }
	    if(ischeck!='false')
	    	ischeck =  turnToCh(disformat,ischeck);
	}
	else
	{
		ischeck= "false" ;
		Ext.showAlert(MB.MSG.inputRightDateFormat);
	}
	return ischeck;
}

function turnToCh(disformat,dateValue){
	var dateArr = dateValue.split(".");
	var year = dateArr[0];
	var month = dateArr[1];
	var day = dateArr[2];
	switch (disformat) {
		case 12:// 一九九一年一月二日
			dateValue= exchangYearToCn(year)+"年"+exchangMonthToCn(parseInt(month,10))+"月"+exchangDayToCn(parseInt(day,10))+"日";
			break;
		case 13:// 一九九一年一月
		  	dateValue= exchangYearToCn(year)+"年"+exchangMonthToCn(parseInt(month,10))+"月";
		  	break;
		case 14:// 1991年1月2日
            dateValue= year+"年"+month+"月"+day+"日";
            break;
        case 15:// 1991年1月
            dateValue= year+"年"+month+"月";
            break;
        case 16:// 91年1月2日
            dateValue=year+"年"+month+"月"+day+"日";
            break;
        case 17:// 91年1月
            dateValue=year+"年"+month+"月";
            break;	
        case 22:// 1999年02月
            dateValue= year+"年"+month+"月";
            break;
        case 23:// 1999年02月03日
            dateValue= year+"年"+month+"月"+day+"日";
            break;
	}
	return dateValue;
}
/**
 * 得到日期对象
 * @param {} dateValue
 * @param {} format
 * @param {} origDate
 * @return {}
 */
function getTemplateDate(dateValue, format,origDate) {
    format = format + "";
    if(!!!dateValue)
    	return null;
    var origDateArr = origDate.split("-");
	var origYear = origDateArr[0];
	var origMonth = origDateArr[1];
	var origDay = origDateArr[2];
	var origLength = null;
    if(origDate){
    	origLength = origYear.length;
    }else{
    	origLength = 0;
    }	
    var preYY = "";
    if(origLength > 2){
    		preYY = origDate.substring(0,origLength-2);
	}
    var date = null;
    var disformat= parseInt(format,10);
    switch (disformat) {
        case 6: // 1991.12.3  注意：因为这种格式是默认格式 都是按1991.12.03显示。
			date = paraDate(dateValue);
            break;
        case 7: // 91.12.3
	        if(preYY != ""){
		        dateValue = preYY + dateValue;
	     	    date = paraDate(dateValue);
	        }else{
	       		 Ext.showAlert(MB.MSG.selectDateByCompent);
	        }
            break;
        case 8:// 1991.2
	        if(!!!origDay)
	        		origDay = 1;
       		dateValue = dateValue + "." + origDay;
     	    date = paraDate(dateValue);
            break;
        case 9:// 1992.02
	        if(!!!origDay)
	        		origDay = 1;
            dateValue = dateValue + "." + origDay;
     	    date = paraDate(dateValue);
            break;
        case 10:// 92.2
	        if(preYY != ""){
		        if(!!!origDay)
		        		origDay = 1;
         		dateValue = preYY + dateValue + "." + origDay;
	     	    date = paraDate(dateValue);
	        }else{
	       		 Ext.showAlert(MB.MSG.selectDateByCompent);
	        }
            break;
        case 11:// 98.02
	      	 if(preYY != ""){
	  			 if(!!!origDay)
	        			origDay = 1;
       		 	dateValue = preYY + dateValue + "." + origDay;
	     	    date = paraDate(dateValue);
	        }else{
	       		 Ext.showAlert(MB.MSG.selectDateByCompent);
	        }
            break;
        case 12:// 一九九一年一月二日
        	var yearIndex = dateValue.indexOf("年");
        	var monthIndex = dateValue.indexOf("月");
        	var dayIndex = dateValue.indexOf("日");
        	var year = dateValue.substring(0,yearIndex);
        	var month = dateValue.substring(yearIndex+1,monthIndex);
        	var day = dateValue.substring(monthIndex+1,dayIndex);
       		var yy = exchangCnToNum(year);
       		var mm = exchangCnToNum(month);
       		var dd = exchangCnToNum(day);
        	dateValue = yy + "." + mm + "." + dd;
        	date = paraDate(dateValue);
            break;
        case 13:// 一九九一年一月
        	var yearIndex = dateValue.indexOf("年");
        	var monthIndex = dateValue.indexOf("月");
        	var year = dateValue.substring(0,yearIndex);
        	var month = dateValue.substring(yearIndex+1,monthIndex);
       		var yy = exchangCnToNum(year);
       		var mm = exchangCnToNum(month);
       		 if(!!!origDay)
	        			origDay = 1;
        	dateValue = yy + "." + mm + "." + origDay;
        	date = paraDate(dateValue);
            break;
        case 14:// 1991年1月2日
        	var yearIndex = dateValue.indexOf("年");
        	var monthIndex = dateValue.indexOf("月");
        	var dayIndex = dateValue.indexOf("日");
        	var year = dateValue.substring(0,yearIndex);
        	var month = dateValue.substring(yearIndex+1,monthIndex);
        	var day = dateValue.substring(monthIndex+1,dayIndex);
        	dateValue = year + "." + month + "." + day;
        	date = paraDate(dateValue);
            break;
        case 15:// 1991年1月
        	var yearIndex = dateValue.indexOf("年");
        	var monthIndex = dateValue.indexOf("月");
        	var year = dateValue.substring(0,yearIndex);
        	var month = dateValue.substring(yearIndex+1,monthIndex);
        	 if(!!!origDay)
	        			origDay = 1;
        	dateValue = year + "." + month + "."  + origDay;
        	date = paraDate(dateValue);
            break;
        case 16:// 91年1月2日
        	if(preYY != ""){
	        	var yearIndex = dateValue.indexOf("年");
	        	var monthIndex = dateValue.indexOf("月");
	        	var dayIndex = dateValue.indexOf("日");
	        	var year = dateValue.substring(0,yearIndex);
	        	var month = dateValue.substring(yearIndex+1,monthIndex);
	        	var day = dateValue.substring(monthIndex+1,dayIndex);
	        	dateValue = preYY + year + "." + month + "." + day;
	     	    date = paraDate(dateValue);
	        }else{
	       		 Ext.showAlert(MB.MSG.selectDateByCompent);
	        }
            break;
        case 17:// 91年1月
        	if(preYY != ""){
	        	var yearIndex = dateValue.indexOf("年");
	        	var monthIndex = dateValue.indexOf("月");
	        	var year = dateValue.substring(0,yearIndex);
	        	var month = dateValue.substring(yearIndex+1,monthIndex);
	        	 if(!!!origDay)
	        			origDay = 1;
	        	dateValue = preYY + year + "." + month + "." + origDay;
	     	    date = paraDate(dateValue);
	        }else{
	       		 Ext.showAlert(MB.MSG.selectDateByCompent);
	        }
            break;
        case 18:// 年龄
    		var date=new Date();
        	var year=date.getFullYear();
        	var month=date.getMonth()+1;
        	var day=date.getDate();
        	var result=year-parseInt(yy,10);
        	if(month<parseInt(mm,10))
        	{
        		result=result-1>0?result-1:0;
        	}
        	else
        	{
        		if(month==parseInt(mm,10))
        		{
        			if(day<parseInt(dd,10))
        			{
        				result=result-1>0?result-1:0;
        			}
        		}
        	}
        	strResult=result;
            break;
        case 19:// 1991（年）
	        if(preYY != ""){
	        		 if(!!!origDay)
	        			origDay = 1;
	    			 if(!!!origMonth)
	    				origMonth = 1;
		        	dateValue = dateValue + "." + origMonth + "." + origDay;
		     	    date = paraDate(dateValue);
		        }else{
		       		 Ext.showAlert(MB.MSG.selectDateByCompent);
		        }
            break;
        case 20:// 1 （月）
       		   if(preYY != ""){
		        	dateValue = origYear + "." + dateValue + "." + origDay;
		     	    date = paraDate(dateValue);
		        }else{
		       		 Ext.showAlert(MB.MSG.selectDateByCompent);
		        }
            break;
        case 21:// 23 （日）
       		   if(preYY != ""){
		        	dateValue = origYear + "." + origMonth + "." + dateValue;
		     	    date = paraDate(dateValue);
		        }else{
		       		 Ext.showAlert(MB.MSG.selectDateByCompent);
		        }
            break;
        case 22:// 1999年02月
        	var yearIndex = dateValue.indexOf("年");
        	var monthIndex = dateValue.indexOf("月");
        	var year = dateValue.substring(0,yearIndex);
        	var month = dateValue.substring(yearIndex+1,monthIndex);
        	if(!!!origDay)
        		origDay = 1;
        	dateValue = year + "." + month + "." + origDay ;
        	date = paraDate(dateValue);
            break;
        case 23:// 1999年02月03日
        	var yearIndex = dateValue.indexOf("年");
        	var monthIndex = dateValue.indexOf("月");
        	var dayIndex = dateValue.indexOf("日");
        	var year = dateValue.substring(0,yearIndex);
        	var month = dateValue.substring(yearIndex+1,monthIndex);
        	var day = dateValue.substring(monthIndex+1,dayIndex);
        	dateValue = year + "." + month + "." + day;
        	date = paraDate(dateValue);
            break;
        case 24:// 1992.02.01
        	date = paraDate(dateValue);
            break;
        case 25:// 1992.02.01 10:30
        	var results = dateValue.match(/^ *(\d{4}).(\d{1,2}).(\d{1,2})\s+([0-1]?[0-9]|[2][0-3]):([0-5][0-9])$/);
        	if(results){
		        var year = parseInt(results[1],10);
		        var month = parseInt(results[2],10);
		        var day = parseInt(results[3],10);
		        var hour = parseInt(results[4],10);
		         var min = parseInt(results[5],10);
		        dateValue = year + "." + month + "." + day;
	        	date = paraDate(dateValue);
	        	if(date)
	        		date = new Date(year,month-1,day,hour,min); 
        	}else
        		Ext.showAlert(MB.MSG.inputRightDateFormat);
            break;
        default:
            break;
    }
  return date;
}

/**
 * 解析字符型日期为Date型
 * @param {} dateValue
 * @return {}
 */
function paraDate(dateValue){
 	    var results = validateDateRegExp(dateValue);
 	    var date = null;
		if(results==null) {
        	Ext.showAlert(MB.MSG.inputRightDateFormat);
        }else{
        	var dataArray = dateValue.split(".");
        	 date = new Date(parseInt(dataArray[0],10), parseInt(dataArray[1],10) - 1, parseInt(dataArray[2],10));
        } 
        return date;
}
/**
 * 校验日期是否正确
 * @param {} dateValue 日期
 * @return {}
 */
function validateDateRegExp(dateValue){
		var str =  /((^((1[8-9]\d{2})|([2-9]\d{3}))([-\/\._])(10|12|0?[13578])([-\/\._])(3[01]|[12][0-9]|0?[1-9])$)|(^((1[8-9]\d{2})|([2-9]\d{3}))([-\/\._])(11|0?[469])([-\/\._])(30|[12][0-9]|0?[1-9])$)|(^((1[8-9]\d{2})|([2-9]\d{3}))([-\/\._])(0?2)([-\/\._])(2[0-8]|1[0-9]|0?[1-9])$)|(^([2468][048]00)([-\/\._])(0?2)([-\/\._])(29)$)|(^([3579][26]00)([-\/\._])(0?2)([-\/\._])(29)$)|(^([1][89][0][48])([-\/\._])(0?2)([-\/\._])(29)$)|(^([2-9][0-9][0][48])([-\/\._])(0?2)([-\/\._])(29)$)|(^([1][89][2468][048])([-\/\._])(0?2)([-\/\._])(29)$)|(^([2-9][0-9][2468][048])([-\/\._])(0?2)([-\/\._])(29)$)|(^([1][89][13579][26])([-\/\._])(0?2)([-\/\._])(29)$)|(^([2-9][0-9][13579][26])([-\/\._])(0?2)([-\/\._])(29)$))/
		var result = dateValue.match(str); 
		return  result;
}

/**
 * 日期格式化
 * @param {} date
 * @param {} format
 * @return {}
 */
function dateFormate(date, format) {
	    var o = { 
				"M+" : date.getMonth()+1, //month 
				"d+" : date.getDate(), //day 
				"h+" : date.getHours(), //hour 
				"m+" : date.getMinutes(), //minute 
				"s+" : date.getSeconds() //second 
		} 
	
	if(/(y+)/.test(format)) { 
		format = format.replace(RegExp.$1, (date.getFullYear()+"").substr(4 - RegExp.$1.length)); 
	} 
	for(var k in o) { 
		if(new RegExp("("+ k +")").test(format)) { 
			format = format.replace(RegExp.$1, RegExp.$1.length==1 ? o[k] : ("00"+ o[k]).substr((""+ o[k]).length)); 
		} 
	} 
	return format; 
}

/**
 * 格式化日期格式
 * date:日期类型
 * format 日期格式 人事异动专用格式
 */
function formatTemplateDate(date, format) {
    format = format + "";
    var yy = date.getFullYear() + "";//获得完整年份，1988  
    var mm = date.getMonth() + 1;
    var dd = date.getDate();
   // var dd = date.getDay();//星期
    var hh = date.getHours();
    var mi = date.getMinutes();
    var ss = date.getSeconds();
    var strResult="";
   /* if (yy<100){//1900到2000年
        yy=1900+parseInt(yy);
    }*/
    var disformat= parseInt(format,10);
    switch (disformat) {
        case 6: // 1991.12.3  注意：因为这种格式是默认格式 都是按1991.12.03显示。
            strResult= yy+"."+getTwoDigitNumber(mm)+"."+getTwoDigitNumber(dd);
            break;
        case 7: // 91.12.3
            strResult=yy.substring(2);
            strResult=strResult+"."+getTwoDigitNumber(mm)+"."+dd;
            break;
        case 8:// 1991.2
            strResult=yy+"."+mm;
            break;
        case 9:// 1992.02
            strResult= yy+"."+getTwoDigitNumber(mm);
            break;
        case 10:// 92.2
            strResult=yy.substring(2);
            strResult=strResult+"."+mm;
            break;
        case 11:// 98.02
            strResult=yy.substring(2);
            strResult=strResult+"."+getTwoDigitNumber(mm);
            break;
        case 12:// 一九九一年一月二日
            strResult= exchangYearToCn(yy)+"年"+exchangMonthToCn(mm)+"月"+exchangDayToCn(dd)+"日";
            break;
        case 13:// 一九九一年一月
            strResult= exchangYearToCn(yy)+"年"+exchangMonthToCn(mm)+"月";
            break;
        case 14:// 1991年1月2日
            strResult= yy+"年"+mm+"月"+dd+"日";
            break;
        case 15:// 1991年1月
            strResult= yy+"年"+mm+"月";
            break;
        case 16:// 91年1月2日
            strResult=yy.substring(2);
            strResult=strResult+"年"+mm+"月"+dd+"日";
            break;
        case 17:// 91年1月
            strResult=yy.substring(2);
            strResult=strResult+"年"+mm+"月";
            break;
        case 18:// 年龄
    		var date=new Date();
        	var year=date.getFullYear();
        	var month=date.getMonth()+1;
        	var day=date.getDate();
        	var result=year-parseInt(yy,10);
        	if(month<parseInt(mm,10))
        	{
        		result=result-1>0?result-1:0;
        	}
        	else
        	{
        		if(month==parseInt(mm,10))
        		{
        			if(day<parseInt(dd,10))
        			{
        				result=result-1>0?result-1:0;
        			}
        		}
        	}
        	strResult=result;
            break;
        case 19:// 1991（年）
            strResult=yy+"";
            break;
        case 20:// 1 （月）
            strResult=mm+"";
            break;
        case 21:// 23 （日）
            strResult=dd+"";
            break;
        case 22:// 1999年02月
            strResult= yy+"年"+getTwoDigitNumber(mm)+"月";
            break;
        case 23:// 1999年02月03日
            strResult= yy+"年"+getTwoDigitNumber(mm)+"月"+dd+"日";
            break;
        case 24:// 1992.02.01
            strResult= yy+"."+getTwoDigitNumber(mm)+"."+getTwoDigitNumber(dd);
            break;
        case 25:// 1992.02.01 10:30
            strResult= yy+"."+getTwoDigitNumber(mm)+"."+getTwoDigitNumber(dd) + " "+getTwoDigitNumber(hh) + ":" +getTwoDigitNumber(mi);
            break;
        default:
            strResult= yy+"."+getTwoDigitNumber(mm)+"."+getTwoDigitNumber(dd);
            break;
    }
  return strResult;
}

/**
 * 汉化数字转化到阿拉伯数字
 * @param {} cnNum
 * @return {}
 */
function exchangCnToNum(cnNum){
  var strResult="";
  var value = cnNum+"";
  var strLength =  value.length;//汉字‘十’是分割点，长度是1时，’十‘是‘10’，长度是2，‘十一’是‘11’
  for (var i = 0; i < value.length; i++) {
      switch (value.charAt(i)) {
      case '〇':
          strResult=strResult+"0";
          break;
      case '一':
          strResult=strResult+"1";
          break;
      case '二':
          strResult=strResult+"2";
          break;
      case '三':
          strResult=strResult+"3";
          break;
      case '四':
          strResult=strResult+"4";
          break;
      case '五':
          strResult=strResult+"5";
          break;
      case '六':
          strResult=strResult+"6";
          break;
      case '七':
          strResult=strResult+"7";
          break;
      case '八':
          strResult=strResult+"8";
          break;
      case '九':
          strResult=strResult+"9";
          break;
    case '十':
    		if(strLength == 1)
          		strResult=strResult+"10";
      		else if(strLength == 2)
          		strResult=strResult+"1";
          break;
      case '零':
          strResult=strResult+"0";
          break;
      }
  }
  return strResult;
}

function exchangYearToCn(year){
  var strResult="";
  var value = year+"";
  for (var i = 0; i < value.length; i++) {
      switch (value.charAt(i)) {
      case '1':
          strResult=strResult+"一";
          break;
      case '2':
          strResult=strResult+"二";
          break;
      case '3':
          strResult=strResult+"三";
          break;
      case '4':
          strResult=strResult+"四";
          break;
      case '5':
          strResult=strResult+"五";
          break;
      case '6':
          strResult=strResult+"六";
          break;
      case '7':
          strResult=strResult+"七";
          break;
      case '8':
          strResult=strResult+"八";
          break;
      case '9':
          strResult=strResult+"九";
          break;
      case '0':
          strResult=strResult+"零";
          break;
      }
  }
  return strResult;
}


function exchangMonthToCn(month){
  var strResult="";
  switch (month) {
     case 1:
         strResult="一";
         break;
     case 2:
         strResult="二";
         break;
     case 3:
         strResult="三";
         break;
     case 4:
         strResult="四";
         break;
     case 5:
         strResult="五";
         break;
     case 6:
         strResult="六";
         break;
     case 7:
         strResult="七";
         break;
     case 8:
         strResult="八";
         break;
     case 9:
         strResult="九";
         break;
     case 10:
         strResult="十";
         break;
     case 11:
         strResult="十一";
         break;
     case 12:
         strResult="十二";
         break;
  }
  return strResult;
}

function exchangDayToCn(month){
  var strResult="";
  switch (month) {
     case 1:
         strResult="一";
         break;
     case 2:
         strResult="二";
         break;
     case 3:
         strResult="三";
         break;
     case 4:
         strResult="四";
         break;
     case 5:
         strResult="五";
         break;
     case 6:
         strResult="六";
         break;
     case 7:
         strResult="七";
         break;
     case 8:
         strResult="八";
         break;
     case 9:
         strResult="九";
         break;
     case 10:
         strResult="十";
         break;
     case 11:
         strResult="十一";
         break;
     case 12:
         strResult="十二";
         break;
     case 13:
         strResult="十三";
         break;
     case 14:
         strResult="十四";
         break;
     case 15:
         strResult="十五";
         break;
     case 16:
         strResult="十六";
         break;
     case 17:
         strResult="十七";
         break;
     case 18:
         strResult="十八";
         break;
     case 19:
         strResult="十九";
         break;
     case 20:
         strResult="二十";
         break;
     case 21:
         strResult="十一";
         break;
     case 22:
         strResult="十二";
         break; 
     case 23:
         strResult="二三";
         break;
     case 24:
         strResult="二四";
         break;
     case 25:
         strResult="二五";
         break;
     case 26:
         strResult="二六";
         break;
     case 27:
         strResult="二七";
         break;
     case 28:
         strResult="二八";
         break;
     case 29:
         strResult="二九";
         break;
     case 30:
         strResult="三十";
         break;
     case 31:
         strResult="三一";
         break;
  }
  return strResult;

}

/**
 * 其他模块调用人事异动 必须引入的js文件：/module/template/utils/template_util.js
 * @param templateBean 参数对象，必须传的参数为：参考/module/template/utils/template_property.js。格式如下：
	 var obj={
	     module_id:"1",//调用模块标记
	     return_flag:"",//返回模块标记
	     tab_id:'1',//模板号
	     task_id:"0",//任务号 除0以外需加密
	     approve_flag:'1',
	     callBack_init:""//回调函数，生成完界面组件后调用的方法，确保请求的组件已加载完，参考回调方法renderForm
	     
	 }
    function renderForm() {  
        Ext.create('Ext.container.Viewport',{
            autoScroll:false,
            style:'backgroundColor:white',
            layout:'fit',
            items:templateMain_me.mainPanel
        });
    }
 */
function createTemplateForm(templateBean){
     /**
       加载所选js类路径
     */
     Ext.Loader.setConfig({
         enabled: true,
         paths: {
             'TemplateMainUL': rootPath+'/module/template/templatemain'
         }
     });
     
    //Ext.Loader.loadScript({url: "/module/template/utils/template_util.js",onLoad:function(){  
       Ext.require('TemplateMainUL.TemplateMain', function(){
                 Ext.create("TemplateMainUL.TemplateMain", {templPropety:templateBean});
            });
   // }});
}

function XML2String(xmlObject) {
    // for IE
    if (window.ActiveXObject) {       
      return xmlObject.xml;
    }
    // for other browsers
    else {        
      return (new XMLSerializer()).serializeToString(xmlObject);
    }
}

/**
 * 模板TemplPropety的other_param参数扩展方法
 * 如：以`分隔object_id="usr00000000001`";
 * @param {} key object_id
 * @param {} value usr00000000001
 */
function setTemplPropetyOthParam(key, value){
	var templateBean_ = {};
	if(typeof(templateMain_me)!="undefined")
		templateBean_ = templateMain_me.templPropety;
	else if(templateBean)
		templateBean_ = templateBean;
	if(!Ext.isEmpty(templateBean_.other_param)){
            if(templateBean_.other_param.indexOf(key+'=') != -1){
                if(templateBean_.other_param.indexOf('`') != -1){
                    var paramArray = new Array();
                    paramArray = templateBean_.other_param.split('`');
                    templateBean_.other_param = "";
                    for(var i=0; i<paramArray.length; i++){
                        var param = paramArray[i];
                        var paramKey = param.split('=')[0];
                        var paramValue = param.split('=')[1];
                        if(paramKey == key){
                            paramValue = value;
                        }
                        if(i == 0){
                        	templateBean_.other_param += paramKey+"="+paramValue;
                        }else{
                        	templateBean_.other_param += "`"+paramKey+"="+paramValue;
                        }
                    }
                }else{
                	templateBean_.other_param = key+"="+value;
                }
            }else{
            	templateBean_.other_param += "`"+key+"="+value;
            }
        }else{
        	templateBean_.other_param = key+"="+value;
        }
}

/**
 * 获取模板TemplPropety的other_param参数
 * 通过key获取value
 * 如：以`分隔object_id="usr00000000001`";
 * @param {} key object_id
 * @param {} value usr00000000001
 */
function getTemplPropetyOthParam(key){

	var value = '';
    if(!Ext.isEmpty(templateMain_me.templPropety.other_param) && templateMain_me.templPropety.other_param.indexOf(key+'=') != -1){
    	var  strList =templateMain_me.templPropety.other_param.split("`");
        for (var i=0;i<strList.length;i++){
            var strParamList =strList[i].split("=");
            if (strParamList.length==2){
                var param = strParamList[0];
                var paramValue = strParamList[1];
                if (key==param && paramValue.length>0){
                    value= paramValue;
                }
            }
        }
    }
    
    return value;
}
