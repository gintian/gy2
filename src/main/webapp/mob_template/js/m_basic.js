锘匡豢function processException(e){
	switch (typeof(e))
	{
		case "string":
		{
			if (e!="abort")
			{
				if (e){
					if(window.Android!=null){
						window.Android.showToast(e);
					}else{
						var parameters = e;
						// parameters =escape(parameters).replace(/%u/gi,'\\u');
						document.location="objc::showToast::"+parameters; 
					}
				}else{
					if(window.Android!=null){
						window.Android.showToast(constErrUnknown);
					}else{
						var parameters = constErrUnknown;
						// parameters =escape(parameters).replace(/%u/gi,'\\u');
						document.location="objc::showToast::"+parameters; 
					}
				}
			}
			break;
		}

		case "object":
		{
			if(window.Android!=null){
				window.Android.showToast("杩滅鏈嶅姟鍑洪敊鍟�...");
				break;
			}else{
				var parameters = "杩滅鏈嶅姟鍑洪敊鍟�...";
				// parameters =escape(parameters).replace(/%u/gi,'\\u');
				document.location="objc::showToast::"+parameters; 
				break;
			}
		}
	}
}


/*****************************************
 *鍘绘帀鍓嶅悗绌烘牸
 *****************************************/
function trimStr(str)
{ 
   str=getValidStr(str);	
   return str.replace(/^\s+|\s+$/, ''); 
}

/*******************************
 *鍘绘帀宸﹁竟绌烘牸
 *******************************/
function ltrimStr(str)
{ 
   str=getValidStr(str);	
   return str.replace(/^\s+/, ''); 
} 

/********************************
 *鍘绘帀鍙宠竟绌烘牸
 ********************************/
function rtrimStr(str)
{ 
   str=getValidStr(str);	
   return str.replace(/\s+$/, ''); 
} 
/*
杩囨护鐗规畩瀛楃
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
 *鍙栧緱鍚堟硶鐨勫瓧绗︿覆
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
 *缂栫爜瑙勫垯:1) ~43~48~45~4e~48~41~4f
 *         2) ^7a0b^7389
 *瀛楃缂栫爜,瑙ｅ喅浼犺緭鍑虹幇涔辩爜闂
 ********************************/
function encode(strIn)
{
	strIn=keyWord_filter(strIn); //杩囨护鐗规畩瀛楃锛岄槻姝SS璺ㄧ珯,SQL娉ㄥ叆婕忔礊  dengcan
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
//鍏ㄩ儴瀛楃閮界紪鐮�
function encode_v1(strIn)
{
	strIn=keyWord_filter(strIn); //杩囨护鐗规畩瀛楃锛岄槻姝SS璺ㄧ珯,SQL娉ㄥ叆婕忔礊  dengcan
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
			//if (strTemp < 48 || (strTemp > 47 && strTemp < 65) || (strTemp > 90 && strTemp < 97) || strTemp > 122)
			//{
				tmp = strTemp.toString(16);
				for(var j=tmp.length; j<2; j++) tmp = "0"+tmp;
				strOut = strOut+"~"+tmp;
			//}
			//else
			//{
			//	strOut=strOut+strIn.charAt(i);
			//}
		}
	}
	return (strOut);
}
/******************************************
 *瀛楃涓茶В鐮�,姹夊瓧浼犺緭杩囩▼涓嚭鐜颁贡鐮侀棶棰�
 *瑙ｇ爜瑙勫垯:1) ~43~48~45~4e~48~41~4f
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
 *瀛楃涓茶繘琛岀紪鐮�
 *******************************/
function getEncodeStr(str) {
	return encode(getValidStr(str));
	//return escape(getValidStr(str));
}

function getDecodeStr(str) {
	return ((str)?decode(getValidStr(str)):"");
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
/*鍥犱负鍦ㄥ悗鍙扮敓鎴愮殑鍊间腑锛屽彧瑕佸惈鏈夐�楀彿锛屽氨鐢╜```鏇挎崲浜嗐�傝繖閲岃杩樺師鏈�鍒濈殑闈㈢洰銆�**/
function filter_ValidStr(str)
{
    var re;	
	re=/````/g;
	str=str.replace(re,",");
	return(str);	    
}

/*杩斿洖true鎴杅alse**/
function isTrue(value)
{
	return (value==true || (typeof(value)=="number" && value!=0) ||
		compareText(value, "true") || compareText(value, "T") ||
		compareText(value, "yes") || compareText(value, "on"));
}

/*姣旇緝涓や釜鍙傛暟鏄惁鐩稿悓**/
function compareText(str1, str2)
{
	str1=getValidStr(str1);
	str2=getValidStr(str2);
	if (str1==str2) return true;
	if (str1=="" || str2=="") return false;
	return (str1.toLowerCase()==str2.toLowerCase());
}

 /*鍙栧緱鍚堟硶鐨勫瓧绗︿覆**/
function getValidStr(str) 
{
	str += "";
	if (str=="undefined" || str=="null" || str=="NaN")
		return "";
	else
		return reNew(str);
		
}
/*寰楀埌鏈�鍒濈殑瀛楃涓�**/
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
/*寰楀埌鏁村瀷**/
function getInt(value){
	var result=parseInt(value);
	if (isNaN(result)) result=0;
	return result;
}

/*寰楀埌娴偣鍨�**/
function getFloat(value){
	var result=parseFloat(value);
	if (isNaN(result)) result=0;
	return result;
}

//杩囨护鐗规畩瀛楃锛岄槻姝SS璺ㄧ珯,SQL娉ㄥ叆婕忔礊
function  keyWord_filter(value)
{ 
	if (value == null||ltrimStr(value).length==0) {
          return value;
      }   
      var result="";
      for (var i=0; i<value.length; ++i) {
          switch (value.charAt(i)) {
            case '<':
                result+="锛�";
                break;
            case '>': 
                result+="锛�";
                break;
            case '"': 
                result+="锛�";
                break;
            case '\'': 
                result+="锛�";
                break; 
            case ';': 
                result+="锛�";
                break;
            case '(': 
                result+="銆�";
                break;
            case ')': 
                result+="銆�";
                break; 
            case '+':
                result+="锛�";
                break;
            default:
                result+=value.charAt(i);
                break;
          }    
      } 
	result=replaceAll(result,"--", "锛嶏紞");
    result=replaceAll(result,"%3C","锛�");
    result=replaceAll(result,"%3c","锛�");
    result=replaceAll(result,"%3E","锛�");
    result=replaceAll(result,"%3e","锛�");
    result=replaceAll(result,"%22","锛�");
    result=replaceAll(result,"%27","锛�");
	result=replaceAll(result,"%3B","锛�");
	result=replaceAll(result,"%3b","锛�");
	result=replaceAll(result,"%28","銆�");
	result=replaceAll(result,"%29","銆�");
	result=replaceAll(result,"%2B","锛�");
	result=replaceAll(result,"%2b","锛�");   
    return result;
}
var Digit = {};
/**
 * 鍥涜垗浜斿叆娉曟埅鍙栦竴涓皬鏁�
 * @param float digit 瑕佹牸寮忓寲鐨勬暟瀛�
 * @param integer length 瑕佷繚鐣欑殑灏忔暟浣嶆暟
 * @return float
 */
Digit.round = function(digit, length) {
    length = length ? parseInt(length) : 0;
    if (length <= 0) return Math.round(digit);
    digit = Math.round(digit * Math.pow(10, length)) / Math.pow(10, length);
    return digit;
};
function isIntOrNull(str){
	if(str==null||typeof(str)=='undefined'){
		return 'undefined';
	}//鍒ゆ柇瀵硅薄鏄惁瀛樺湪
	return isNull(str)||isInt(str);
}
//蹇呴渶鏄暣鏁�
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