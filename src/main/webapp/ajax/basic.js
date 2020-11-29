function processException(e){
	switch (typeof(e))
	{
		case "string":
		{
			if (e!="abort")
			{
				if (e)
					alert(e);
				else
					alert(constErrUnknown);
			}
			break;
		}

		case "object":
		{
			alert(e.description+"\n"+constErrType+":"+(e.number & 0xFFFF));
			break;
		}
	}
}


/*****************************************
 *去掉前后空格
 *****************************************/
function trimStr(str)
{ 
   str=getValidStr(str);	
   return str.replace(/^\s+|\s+$/, ''); 
}

/*******************************
 *去掉左边空格
 *******************************/
function ltrimStr(str)
{ 
   str=getValidStr(str);	
   return str.replace(/^\s+/, ''); 
} 

/********************************
 *去掉右边空格
 ********************************/
function rtrimStr(str)
{ 
   str=getValidStr(str);	
   return str.replace(/\s+$/, ''); 
} 
/*
过滤特殊字符
*/
function filter_xml(str)
{
    var re;
	str=getValidStr(str);
	re=/%/g;
	str=str.replace(re,"%25");	
	re=/&/g;
	str=str.replace(re,"%26amp;");
	re=/'/g;  
	str=str.replace(re,"%26apos;");
	re=/</g;  
	str=str.replace(re,"%26lt;");
	re=/>/g;  
	str=str.replace(re,"%26gt;");
	re=/"/g;  
	str=str.replace(re,"%26quot;");

	re=/,/g;
	str=str.replace(re,"````");
	return(str);	
}

function reNew(str)
{
    var re;
	re=/%26amp;/g;
	str=str.replace(re,"&");
	re=/%26apos;/g;  
	str=str.replace(re,"'");
	re=/%26lt;/g;  
	str=str.replace(re,"<");
	re=/%26gt;/g;  
	str=str.replace(re,">");
	re=/%26quot;/g;  
	str=str.replace(re,"\"");
	re=/%25/g;
	str=str.replace(re,"%");
	re=/````/g;
	str=str.replace(re,",");
	return(str);		
}

function filter_ValidStr(str)
{
    var re;	
	re=/````/g;
	str=str.replace(re,",");
	return(str);	    
}


/****************************
 *取得合法的字符串
 ****************************/
function getValidStr(str) 
{
	str += "";
	if (str=="undefined" || str=="null" || str=="NaN")
		return "";
	else
		return reNew(str);
		
}
/********************************
 *编码规则:1) ~43~48~45~4e~48~41~4f
 *         2) ^7a0b^7389
 *字符编码,解决传输出现乱码问题
 ********************************/
function encode(strIn)
{
	strIn=keyWord_filter(strIn); //过滤特殊字符，防止XSS跨站,SQL注入漏洞  dengcan
	var intLen=strIn.length;
	var strOut="";
	var strTemp;

	for(var i=0; i<intLen; i++)
	{
		strTemp=strIn.charCodeAt(i);
		if (strTemp>255)
		{
			tmp = strTemp.toString(16);
			for(var j=tmp.length; j<4; j++) tmp = "0"+tmp;
			strOut = strOut+"^"+tmp;
		}
		else
		{
			//if (strTemp < 65 || (strTemp > 90 && strTemp < 97) || strTemp > 122)			
			if (strTemp < 48 || (strTemp > 47 && strTemp < 65) || (strTemp > 90 && strTemp < 97) || strTemp > 122)
			{
				tmp = strTemp.toString(16);
				for(var j=tmp.length; j<2; j++) tmp = "0"+tmp;
				strOut = strOut+"~"+tmp;
			}
			else
			{
				strOut=strOut+strIn.charAt(i);
			}
		}
	}
	return (strOut);
}
//全部字符都编码
function encode_v1(strIn)
{
	strIn=keyWord_filter(strIn); //过滤特殊字符，防止XSS跨站,SQL注入漏洞  dengcan
	var intLen=strIn.length;
	var strOut="";
	var strTemp; 
	var tmp_length=0;
	for(var i=0; i<intLen; i++)
	{
		strTemp=strIn.charCodeAt(i);
		if (strTemp>255)
		{
			tmp = strTemp.toString(16);
			tmp_length=tmp.length;
			if(tmp_length<4)
			{
				for(var j=tmp.length; j<4; j++) tmp = "0"+tmp;
			}
			 
			strOut +="^"+tmp;
		}
		else
		{ 
				tmp = strTemp.toString(16);
				tmp_length=tmp.length;
				if(tmp_length<2)
				{
					for(var j=tmp.length; j<2; j++) tmp = "0"+tmp;
				}
				 
				strOut +="~"+tmp; 
		}
	} 
	return (strOut);
}
/******************************************
 *字符串解码,汉字传输过程中出现乱码问题
 *解码规则:1) ~43~48~45~4e~48~41~4f
 *         2) ^7a0b^7389
 ******************************************/
function decode(strIn)
{ 
	var intLen = strIn.length;
	var strOut = "";
	var strTemp; 
	for(var i=0; i<intLen; i++)
	{
		strTemp = strIn.charAt(i);
		switch (strTemp)
		{
			case "~":{
				strTemp = strIn.substring(i+1, i+3);
				strTemp = parseInt(strTemp, 16);
				strTemp = String.fromCharCode(strTemp);
				strOut = strOut+strTemp;
				i += 2;
				break;
			}
			case "^":{
				strTemp = strIn.substring(i+1, i+5);
				strTemp = parseInt(strTemp,16);
				strTemp = String.fromCharCode(strTemp);
				strOut = strOut+strTemp;
				i += 4;
				break;
			}
			default:{
				strOut = strOut+strTemp;
				break;
			}
		}

	}
	 
	return (strOut);
}

/*******************************
 *字符串进行编码
 *******************************/
function getEncodeStr(str) {
	return encode(getValidStr(str));
	//return escape(getValidStr(str));
}

function getDecodeStr(str) {
	return ((str)?decode(getValidStr(str)):"");
}

function compareText(str1, str2){
	str1=getValidStr(str1);
	str2=getValidStr(str2);
	if (str1==str2) return true;
	if (str1=="" || str2=="") return false;
	return (str1.toLowerCase()==str2.toLowerCase());
}

function isTrue(value){
	return (value==true || (typeof(value)=="number" && value!=0) ||
		compareText(value, "true") || compareText(value, "T") ||
		compareText(value, "yes") || compareText(value, "on"));
}

function getStringValue(value){
	if (typeof(value)=="string" || typeof(value)=="object")
		return "\""+getValidStr(value)+"\"";
	else if (typeof(value)=="date")
		return "\""+(new Date(value))+"\"";
	else if (getValidStr(value)=="")
		return "\"\"";
	else
		return value;
}

function getInt(value){
	var result=parseInt(value);
	if (isNaN(result)) result=0;
	return result;
}


function getFloat(value){
	var result=parseFloat(value);
	if (isNaN(result)) result=0;
	return result;
}

function getTypedValue(value, dataType){
	var result="";
	switch (dataType)
	{
		case "string":
		{
			result=getValidStr(value);
			break;
		}
		case "byte":;
		case "short":;
		case "int":;
		case "long":
		{
			result=Math.round(parseFloat(value));
			break;
		}
		case "float":;
		case "double":;
		case "bigdecimal":{
			result=parseFloat(value);
			break;
		}
		case "date":;
		case "time":;
		case "timestamp":
		{
			value=getValidStr(value);
			result=new Date(value.replace(/-/g, "/"));
			break;
		}
		case "boolean":
		{
			result=isTrue(value);
			break;
		}
		default:
		{
			result=getValidStr(value);
			break;
		}
	}
	return result;
}

function doBaseType(value,dataType)
{
   if(value instanceof Date)
   {
   	  return "D"+value.getMilliseconds();
   }
   else if(value instanceof Number)   
   {
   	if((typeOf(dataType)!="undefined")&&(dataType=="int"||dataType=="long"||dataType=="short"||dataType=="byte"))
   	  return "I"+value;   	
	else
   	  return "F"+value;
   }
   else if(value instanceof Boolean)   
   {
   		if(value)
		  return "B1";
		else
		  return "B0";
   }
   else if(value instanceof String)
   {
     return "A"+value;
   }   
   else  //常量非对象,包括字符串\数值\布尔
   {
	if(typeof(value)=="number")
	{
   		if(((typeof(dataType)!="undefined"))&&(dataType=="int"||dataType=="long"||dataType=="short"||dataType=="byte"))
   	 		 return "I"+value;
   	 	else if(((typeof(dataType)!="undefined"))&&(dataType=="string")) 
   	 	 	  return "A"+value;
		else
   	 		 return "F"+value;		
	}
	else if((typeof(dataType)!="undefined")&&typeof(value)=="boolean")
	{
   		if(value)
		  return "B1";
		else
		  return "B0";
	}
	else  
	  return "A"+value;	  
   }
}

function doObject(value)
{
 	var ss;
	var temp;
	var vv;
	var expr="";;
	if(value==null)
	   return "";
	for(ss in value)
	{
		if(ss=="extend")
		  continue;
		temp=value[ss];
		if(temp instanceof Array)
		  vv=doArray(temp);
		else if(temp instanceof Date || temp instanceof String ||temp instanceof Number||temp instanceof Boolean)
		  vv=doBaseType(temp);
		else if(temp instanceof Object)
		  vv=doObject(temp);
		else
		  vv=doBaseType(temp);
		expr=expr+ss+":"+vv+","; 
	}
	expr=expr.substring(0,expr.length-1);
	expr="{"+expr+"}";
	return expr;
}

function doArray(value)
{
	var temp;
	var expr="";
	var vv;
	if(value==null||value.length==0)
	   return "";
	for(var i=0;i<value.length;i++)
	{
		temp=value[i];
		if(temp instanceof Array)
		  vv=doArray(temp);
		else if(temp instanceof Date || temp instanceof String ||temp instanceof Number||temp instanceof Boolean)
		  vv=doBaseType(temp);
		else if(temp instanceof Object)
		  vv=doObject(temp);
		else
		  vv=doBaseType(temp);			
		expr=expr+vv+",";		
	}
	expr=expr.substring(0,expr.length-1);
	expr="["+expr+"]";
	return expr;
}

/***************************************
 *pArray对象构造函数
 *用于保存记录集,最基本的构造项目
 ***************************************/
function pArray()
{
	this.firstUnit=null;
	this.lastUnit=null;
	this.length=0;
	this.getFirstUnit=_pArray_getFirstUnit;
	this.getLastUnit=_pArray_getLastUnit;
	this.getLength=_pArray_getLength;
	this.clearAll=_pArray_clearAll;
	this.insertUnit=_pArray_insertUnit;
	this.insertArray=_pArray_insertArray;
	this.deleteUnit=_pArray_deleteUnit;
	this.insertWithData=_pArray_insertWithData;
	this.deleteByData=_pArray_deleteByData;
}

function _pArray_getFirstUnit() {
	return this.firstUnit;
}

function _pArray_getLastUnit() {
	return this.lastUnit;
}

function _pArray_getLength() {
	return this.length;
}

function pArray_clear(pArray){
	var unit=pArray.firstUnit;
	var _unit;
	while (unit){
		_unit=unit;
		unit=unit.nextUnit;
		if (_unit.data) delete _unit.data;
		delete _unit;
	}
	pArray.firstUnit=null;
	pArray.lastUnit=null;
	pArray.length=0;
}

function _pArray_clearAll(){
	pArray_clear(this);
}

function _unit_getData() {
	return this.data;
}

function _unit_getPrevUnit() {
	return this.prevUnit;
}

function _unit_getNextUnit() {
	return this.nextUnit;
}

function _unit_getData() {
	return this.data;
}

function pArray_insert(pArray, mode, unit, newUnit){
	var u1, u2;
	switch (mode)
	{
		case "begin":
		{
			u1=null;
			u2=pArray.firstUnit;
			break;
		}
		case "before":{
			u1=(unit)?unit.prevUnit:null;
			u2=unit;
			break;
		}
		case "after":
		{
			u1=unit;
			u2=(unit)?unit.nextUnit:null;
			break;
		}
		default:
		{
			u1=pArray.lastUnit;
			u2=null;
			break;
		}
	}
	
	newUnit.getPrevUnit=_unit_getPrevUnit;	
	newUnit.getNextUnit=_unit_getNextUnit;	
	newUnit.getData=_unit_getData;
	newUnit.prevUnit=u1;
	newUnit.nextUnit=u2;
	if (u1) u1.nextUnit=newUnit; else pArray.firstUnit=newUnit;
	if (u2) u2.prevUnit=newUnit; else pArray.lastUnit=newUnit;
	pArray.length++;
}

function _pArray_insertUnit(mode, unit, newUnit){
	pArray_insert(this, mode, unit, newUnit);
}

function pArray_insertArray(pArray, mode, unit, subArray){
	if (!subArray || !subArray.firstUnit) return;

	var u1, u2;
	switch (mode)
	{
		case "begin":
		{
			u1=null;
			u2=pArray.firstUnit;
			break;
		}
		case "before":
		{
			u1=(unit)?unit.prevUnit:null;
			u2=unit;
			break;
		}
		case "after":
		{
			u1=unit;
			u2=(unit)?unit.nextUnit:null;
			break;
		}
		default:
		{
			u1=pArray.lastUnit;
			u2=null;
			break;
		}
	}

	subArray.firstUnit.prevUnit=u1;
	subArray.lastUnit.nextUnit=u2;
	if (u1) u1.nextUnit=subArray.firstUnit; else pArray.firstUnit=subArray.firstUnit;
	if (u2) u2.prevUnit=subArray.lastUnit; else pArray.lastUnit=subArray.lastUnit;
	pArray.length+=subArray.length;
}

function _pArray_insertArray(mode, unit, subArray)
{
	pArray_insertArray(this, mode, unit, subArray);
}

function pArray_delete(pArray, unit)
{
	var u1, u2;
	u1=unit.prevUnit;
	u2=unit.nextUnit;
	if (u1) u1.nextUnit=u2; else pArray.firstUnit=u2;
	if (u2) u2.prevUnit=u1; else pArray.lastUnit=u1;
	pArray.length--;
	delete unit;
}

function _pArray_deleteUnit(unit)
{
	pArray_delete(this, unit);
}

function pArray_ex_insert(pArray, data)
{
	var found=false;
	var _unit=pArray.firstUnit;
	while (_unit){
		if (_unit.data==data){
			found=true;
			break;
		}
		_unit=_unit.nextUnit;
	}

	var newUnit=new Object();
	newUnit.data=data;
	if (!found) pArray_insert(pArray, "end", null, newUnit);
}

function _pArray_insertWithData(data)
{
	pArray_ex_insert(this, data);
}

function pArray_ex_delete(pArray, data)
{
	var _unit=pArray.firstUnit;
	while (_unit)
	{
		if (_unit.data==data)
		{
			pArray_delete(pArray, _unit);
			break;
		}
		_unit=_unit.nextUnit;
	}
}

function _pArray_deleteByData(data)
{
	pArray_ex_delete(this, data);
}

function getWordsTrueLength(str,falg){  
    sLen = 0;  
    try{  
        //先将回车换行符做特殊处理  
        str = str.replace(/(\r\n+|\s+|　+)/g,"龘"); 
        if(!falg){
        	//处理英文字符数字，连续字母、数字、英文符号视为一个单词  
            str = str.replace(/[\x00-\xff]/g,"m");    
            //合并字符m，连续字母、数字、英文符号视为一个单词  
            str = str.replace(/m+/g,"*");  
        }
        //去掉回车换行符  
        str = str.replace(/龘+/g,"");  
        //返回字数  
        sLen = str.length;  
    }catch(e){  
          
    }  
    return sLen;  
} 