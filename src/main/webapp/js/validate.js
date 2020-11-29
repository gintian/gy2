//动态引入showModalDialog.js,防止影响其他模块的功能 haosl add 20180102
if(!window.modalDialog){
	insertJS("/js/showModalDialog.js","js",function(){});
}
 //防止上传漏洞
 function validateUploadFilePath(filePath)
 {
 	var jyhz= [ 
	'html','htm','php','php2','php3','php4','php5','phtml','pwml','inc','asp','aspx','ascx','jsp','cfm','cfc','pl',
	'bat','exe','com','dll','vbs','js','reg','cgi','htaccess','asis','sh','shtml','shtm','phtm'];
 
    filePath=filePath.replace(/^\s+|\s+$/, ''); 
	if(filePath.split('./').length>1||filePath.split('.\\').length>1)
	{
		alert("文件名含有./或.\号，禁止上传！");
  		return false;
	} 
    var sExtension = filePath.substr( filePath.lastIndexOf('.') + 1 ).toLowerCase().replace(/^\s+|\s+$/, '') ;
	for(var i=0;i<jyhz.length;i++)
    {
  		if(jyhz[i]==sExtension)
  		{
  			alert("您上传的是非法文件!");
  			return false;
  		}
    }
 	return true;
 }







  function replaceAll( str, from, to ) {
	    var idx = str.indexOf( from );
	    while ( idx > -1 ) {
	        str = str.replace( from, to ); 
	        idx = str.indexOf( from );
	    }
	   
	    return str;
   }
  
  
  
    //过滤特殊字符，防止XSS跨站,SQL注入漏洞
	function  keyWord_filter(value)
	{ 
	 //   return value;
	  
		if (value == null||trim(value).length==0) {
            return value;
        }   
        var result="";
        for (var i=0; i<value.length; ++i) {
            switch (value.charAt(i)) {
	            case '<':
	                result+="＜";
	                break;
	            case '>': 
	                result+="＞";
	                break;
	            case '"': 
	                result+="＂";
	                break;
	            case '\'': 
	                result+="＇";
	                break; 
	            case ';': 
	                result+="；";
	                break;
	            case '(': 
	                result+="〔";
	                break;
	            case ')': 
	                result+="〕";
	                break; 
	            case '+':
	                result+="＋";
	                break;
	            default:
	                result+=value.charAt(i);
	                break;
            }    
        } 
     	
 		result=replaceAll(result,"--", "－－");
        result=replaceAll(result,"%3C","＜");
        result=replaceAll(result,"%3c","＜");
        result=replaceAll(result,"%3E","＞");
        result=replaceAll(result,"%3e","＞");
        result=replaceAll(result,"%22","＂");
        result=replaceAll(result,"%27","＇");
		result=replaceAll(result,"%3B","；");
		result=replaceAll(result,"%3b","；");
		result=replaceAll(result,"%28","〔");
		result=replaceAll(result,"%29","〕");
		result=replaceAll(result,"%2B","＋");
		result=replaceAll(result,"%2b","＋");   
        return result;
	}
	function replaceKeyWord(value)
	{
	   if (value == null||trim(value).length==0) {
            return value;
        }   
        var result="";
        for (var i=0; i<value.length; ++i) {
            switch (value.charAt(i)) {
	            case '<':
	                result+="≮";
	                break;
	            case '>': 
	                result+="≯";
	                break;
	            case '"': 
	                result+="≡";
	                break;
	            case '\'': 
	                result+="∪";
	                break; 
	            case ';': 
	                result+="∈";
	                break;
	            case '(': 
	                result+="∵";
	                break;
	            case ')': 
	                result+="⊙";
	                break; 
	            case '+':
	                result+="≌";
	                break;
	            default:
	                result+=value.charAt(i);
	                break;
            }    
        } 
     	
 		result=replaceAll(result,"--", "∥∥");
        result=replaceAll(result,"%3C","≮");
        result=replaceAll(result,"%3c","≮");
        result=replaceAll(result,"%3E","≯");
        result=replaceAll(result,"%3e","≯");
        result=replaceAll(result,"%22","≡");
        result=replaceAll(result,"%27","∪");
		result=replaceAll(result,"%3B","∈");
		result=replaceAll(result,"%3b","∈");
		result=replaceAll(result,"%28","∵");
		result=replaceAll(result,"%29","⊙");
		result=replaceAll(result,"%2B","≌");
		result=replaceAll(result,"%2b","≌");   
        return result;
	}
  
   //特殊字符还原，java类获得参数，遇到特殊字符需要还原
	function  keyWord_reback(value)
	{ 
	
		if (value == null||trim(value).length==0) {
            return value;
        }   
        value = value.replaceAll(value,"＜", "<");
        value = value.replaceAll(value,"＞", ">");
        value = value.replaceAll(value,"＂", "\"");
        value = value.replaceAll(value,"＇", "'");
        value = value.replaceAll(value,"；", ";");
        value = value.replaceAll(value,"〔", "(");
        value = value.replaceAll(value,"〕", ")");
        value = value.replaceAll(value,"＋", "+");
        value = value.replaceAll(value,"－－", "--");
       return value;
	}
  
  
//通过控件名字得到object
function MM_findObj_(n, d)
{
	var p,i,x;

	if(!d)
		d=document;

	if((p=n.indexOf("?"))>0&&parent.frames.length)
	{
		d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);
	}
	if( !(x=d[n]) && d.all )
		x=d.all[n];

	for (i=0;!x&&i<d.forms.length;i++)
		x=d.forms[i][n];

	for(i=0;!x&&d.layers&&i<d.layers.length;i++)
		x=MM_findObj_(n,d.layers[i].document);

	return x;
}

//本地替换url()
function loactionOpen(url){
	parent.i_body.location.replace(url);
}
//去掉前台空格
function trim(s)
{ 
	return s.replace(/^\s+|\s+$/, ''); 
}
//去掉左边空格
function ltrim(s)
{ 
	return s.replace(/^\s+/, ''); 
} 

//去掉右边空格
function rtrim(s)
{ 
	return s.replace(/\s+$/, ''); 
} 
//控件数据校验
function validate1()
{
	  var i,myValue1,myValue2,myValue3,myValue4,myValue5,errors='';

	  var val,val1;

	  var controlAttribute,putInfo1,putInfo2;

	  var args = validate1.arguments;

	  var checkMailAddress = /\w@\w{3,}\.\w{3,}/;  //创建正则表达式校验邮件地址对象

	  var checkInteger = /^[+-]?\d+$/;  //创建正则表达式校验整数对象

	  var checkFloat = /^[+-]?\d+(\.\d+)?$/;  //创建正则表达式校验浮点数对象

	for (i=0; i<(args.length-2); i+=3)
	{
		controlAttribute = args[i];
		putInfo1 = args[i+1];
		putInfo2 = args[i+2];
		//add for period check ; should add at the end of all validate invokes
		putInfo3 = args[i+3];
		if( controlAttribute.indexOf('D')>=0 || controlAttribute.indexOf('Y')>=0 ||  controlAttribute.indexOf('T')>=0 )
			val = MM_findObj_( putInfo1+"year" );
		else
			val = MM_findObj_( putInfo1 );

		//如果控件不存在，直接跳过
		if( !val )
			continue;

		myValue1 = val.value;


		//对非空的处理
		if( controlAttribute.indexOf('R') >= 0 )
		{
			if( controlAttribute.indexOf('D8')>=0 )
			{
				val = MM_findObj_( putInfo1+'year' );
				myValue1 = MM_findObj_( putInfo1+'year' ).value;
				myValue2 = MM_findObj_( putInfo1+'monthfrom' ).value;
				myValue3 = MM_findObj_( putInfo1+'monthto' ).value;
				if( myValue1 == '' || myValue2 == '' || myValue3 == '' )
				{
					errors = putInfo2+'不能为空';
					break;
				}
			}
			else if( controlAttribute.indexOf('D6')>=0 )
			{
				val = MM_findObj_( putInfo1+'year' );
				myValue1 = MM_findObj_( putInfo1+'year' ).value;
				myValue2 = MM_findObj_( putInfo1+'month' ).value;
				//myValue3 = MM_findObj_( putInfo1+'monthto' ).value;
				if( myValue1 == '' || myValue2 == '' )
				{
					errors = putInfo2+'不能为空';
					break;
				}
			}
			else if( controlAttribute.indexOf('DH')>=0 )
			{
				val = MM_findObj_( putInfo1+'year' );
				myValue1 = MM_findObj_( putInfo1+'year' ).value;
				myValue2 = MM_findObj_( putInfo1+'month' ).value;
				myValue4 = MM_findObj_( putInfo1+'hour' ).value;
				val1 = MM_findObj_( putInfo1+'day' );

				if( val1 )
					myValue3 = MM_findObj_( putInfo1+'day' ).value;
				else
					myValue3 = MM_findObj_( putInfo1+'date' ).value;

				if( myValue1 == '' || myValue2 == '' || myValue3 == '' || myValue4 == '')
				{
					errors = putInfo2+'不能为空';
					break;
				}
			}
			else if( controlAttribute.indexOf('D')>=0 )
			{
				val = MM_findObj_( putInfo1+'year' );
				myValue1 = MM_findObj_( putInfo1+'year' ).value;
				myValue2 = MM_findObj_( putInfo1+'month' ).value;
				val1 = MM_findObj_( putInfo1+'day' );

				if( val1 )
					myValue3 = MM_findObj_( putInfo1+'day' ).value;
				else
					myValue3 = MM_findObj_( putInfo1+'date' ).value;

				if( myValue1 == '' || myValue2 == '' || myValue3 == '' )
				{
					errors = putInfo2+'不能为空';
					break;
				}
			}			
			else if( controlAttribute.indexOf('S')>=0 )
			{

				val = MM_findObj_( putInfo1 );
				myValue1 = MM_findObj_( putInfo1 ).value;
				if( myValue1 == '#' ||myValue1=="")
				{
					errors = '请选择'+putInfo2;
					break;
				}
			}
			else
			{
				val = MM_findObj_( putInfo1 );
				myValue1 = MM_findObj_( putInfo1 ).value;
				if(trim(myValue1)== '' )
				{
					errors = putInfo2+'不能为空';
					break;
				}
			}

		}
		//对起止时间的验证
		/**
		if( controlAttribute.indexOf('M')>=0 ) {
			//errors = putInfo1 +'起始日期不正确';
			//break;
			if(!checkDateTime( MM_findObj_( putInfo1+'year').value +'-'+MM_findObj_( putInfo1+'QSmonth' ).value+'-'+MM_findObj_( putInfo1+'QSdate' ).value )) {
				errors = putInfo2 + '不正确';
				break;
			}
		}
		if( controlAttribute.indexOf('N')>=0 ) {
			if(!checkDateTime( MM_findObj_( putInfo1+'year').value +'-'+MM_findObj_( putInfo1+'JZmonth' ).value+'-'+MM_findObj_( putInfo1+'JZdate' ).value )) {
				errors = putInfo2 + '不正确';
				break;
			}
		}
		*/
		//对有效期的处理 一个年 两个月两个日
		if( controlAttribute.indexOf('T')>=0 )
	 	{
	 		
	 		val = MM_findObj_( putInfo1+'year' );
	 		val1 = MM_findObj_( putInfo1+'day' );
	 		if( val1 )
	 		{
	 			myValue1 = new Date (MM_findObj_( putInfo1+"year" ).value , MM_findObj_( putInfo1+"QSmonth" ).value , MM_findObj_( putInfo1+"QSday" ).value);
	 			myValue2 = new Date (MM_findObj_( putInfo1+"year" ).value , MM_findObj_( putInfo1+"JZmonth" ).value , MM_findObj_( putInfo2+"JZday" ).value);
	 		}
	 		else
	 		{
	 			myValue1 = new Date (MM_findObj_( putInfo1+"year" ).value , MM_findObj_( putInfo1+"QSmonth" ).value , MM_findObj_( putInfo1+"QSdate" ).value);
	 			myValue2 = new Date (MM_findObj_( putInfo1+"year" ).value , MM_findObj_( putInfo2+"JZmonth" ).value , MM_findObj_( putInfo2+"JZdate" ).value);
	 		}



    			if( myValue1 > myValue2 )
			{
				errors = '起始日期不能大于截止日期';
				break;
			}
			else
			{//add for period check
				if((controlAttribute.indexOf('P')>=0)&&( myValue2 - myValue1 > (putInfo3)*24*60*60*1000))
				{
					errors = '结束时间不能大于开始时间超过 ' + putInfo3 + ' 天';
					break;
				}
			}
	 	}

	 	//对有效期的处理
	 	if( controlAttribute.indexOf('Y')>=0 )
	 	{
	 		val = MM_findObj_( putInfo1+'year' );
	 		val1 = MM_findObj_( putInfo1+'day' );
	 		if( val1 )
	 		{
	 			myValue1 = new Date (MM_findObj_( putInfo1+"year" ).value , MM_findObj_( putInfo1+"month" ).value , MM_findObj_( putInfo1+"day" ).value);
	 			myValue2 = new Date (MM_findObj_( putInfo2+"year" ).value , MM_findObj_( putInfo2+"month" ).value , MM_findObj_( putInfo2+"day" ).value);
	 		}
	 		else
	 		{
	 			myValue1 = new Date (MM_findObj_( putInfo1+"year" ).value , MM_findObj_( putInfo1+"month" ).value , MM_findObj_( putInfo1+"date" ).value);
	 			myValue2 = new Date (MM_findObj_( putInfo2+"year" ).value , MM_findObj_( putInfo2+"month" ).value , MM_findObj_( putInfo2+"date" ).value);
	 		}



    			if( myValue1 > myValue2 )
			{
				errors = '开始时间不能大于结束时间';
				break;
			}
			else
			{//add for period check
				if((controlAttribute.indexOf('P')>=0)&&( myValue2 - myValue1 > (putInfo3)*24*60*60*1000))
				{
					errors = '结束时间不能大于开始时间超过 ' + putInfo3 + ' 天';
					break;
				}
			}
	 	}

	 	//对其他属性的处理
	 	if( myValue1 != '' )
	 	{

		 	if ( controlAttribute.indexOf('I') >= 0 )
		 	{
		 		if((!checkInteger.test(myValue1))||myValue1>2147483647||myValue1<-2147483648)
				{
					errors = putInfo2+'只能是小于(2147483647)整数';
					break;
				}
		 	}
		 	else if ( controlAttribute.indexOf( 'Z' ) >= 0 )
		 	{
		 		if( !checkInteger.test(myValue1) || myValue1.indexOf('-') >= 0 )
				{
					
					errors = putInfo2+'只能是正整数';
					break;
				}				
		 	}
		 	else if(  controlAttribute.indexOf('F') >= 0 )
			{
				if( !checkFloat.test(myValue1) )
				{
					errors = putInfo2+'只能是实数';
					break;
				}
			}
			else if( controlAttribute.indexOf('D8') >= 0 )
			{
				myValue4 = new Date ( myValue1 , MM_findObj_( putInfo1+'monthfrom' ).value ,'01');
				myValue5 = new Date ( myValue1 , MM_findObj_( putInfo1+'monthto' ).value ,'01');
				if( myValue1.length !=4 || !checkDateTime( myValue1 +'-'+MM_findObj_( putInfo1+'monthfrom' ).value+'-'+'01' ) || !checkDateTime( myValue1 +'-'+MM_findObj_( putInfo1+'monthto' ).value+'-'+'01' ))
				{
					errors = putInfo2+'时间不正确';
					break;
				}
				else if( myValue4 > myValue5 )
				{
					errors = putInfo2+'开始时间不能大于结束时间';
					break;
				}
				else if ( myValue1<1900 || myValue1>2100 )
				{
					errors = putInfo2+'不能小于1900年，大于2100年';
					break;
				}
			}
			else if( controlAttribute.indexOf('D6') >= 0 )
			{
				myValue4 = new Date ( myValue1 , MM_findObj_( putInfo1+'month' ).value ,'01');
				//myValue5 = new Date ( myValue1 , MM_findObj_( putInfo1+'monthto' ).value ,'01');
				if( myValue1.length !=4 || !checkDateTime( myValue1 +'-'+MM_findObj_( putInfo1+'month' ).value+'-'+'01' ) )
				{
					errors = putInfo2+'时间不正确';
					break;
				}

				else if ( myValue1<1900 || myValue1>2100 )
				{
					errors = putInfo2+'不能小于1900年，大于2100年';
					break;
				}
			}
			else if( controlAttribute.indexOf('DH')>=0 )
			{
				val1 =  MM_findObj_( putInfo1+'day' );
				myValue4 = '';
				myValue5 = MM_findObj_( putInfo1+'hour' ).value;
				if( val1 )
					myValue4 = MM_findObj_( putInfo1+'day' ).value;
				else
					myValue4 = MM_findObj_( putInfo1+'date' ).value;				

				if( myValue1.length!=4 || !checkDateTime( myValue1+'-'+MM_findObj_( putInfo1+'month' ).value+'-'+myValue4 ) || !checkInteger.test(myValue5) || myValue5<0 || myValue5>23 )
				{
					errors = putInfo2+'时间不正确';
					break;
				}				
				else if ( myValue1<1900 || myValue1>2100 )
				{
					errors = putInfo2+'不能小于1900年，大于2100年';
					break;
				}
			}
		    	else if( controlAttribute.indexOf('D')>=0 )
			{
				val1 =  MM_findObj_( putInfo1+'day' );
				myValue4 = '';
				if( val1 )
					myValue4 = MM_findObj_( putInfo1+'day' ).value;
				else
					myValue4 = MM_findObj_( putInfo1+'date' ).value;

				if( myValue1.length!=4 || !checkDateTime( myValue1+'-'+MM_findObj_( putInfo1+'month' ).value+'-'+myValue4 ) )
				{
					errors = putInfo2+'时间不正确';
					break;
				}
				else if ( myValue1<1900 || myValue1>2100 )
				{
					errors = putInfo2+'不能小于1900年，大于2100年';
					break;
				}
			}			
			else if( controlAttribute.indexOf('E')>=0 )
			{
				if ( !checkMailAddress.test(myValue1) )
				{
					errors = putInfo2+'不是邮件地址';
					break;
				}
			}
			if ( controlAttribute.indexOf( 'N' ) >= 0 )
			{
				if( !checkInteger.test(myValue1) )
				{
					errors = putInfo2+'只能是整数';
					break;
				}
				else if ( myValue1<1900 || myValue1>2100 )
				{
					errors = putInfo2+'不能小于1900年，大于2100年';
					break;
				}
			}
		}

		//长度判断
		if( controlAttribute.indexOf('L|') >= 0 && controlAttribute.indexOf('L|') < controlAttribute.indexOf('-') )
		{

			if( !controlAttribute.indexOf('R') >= 0 && myValue1.length==0 )
				break;

			val = MM_findObj_( putInfo1 );
			myValue4 = controlAttribute.substring(controlAttribute.indexOf("L|")+2,controlAttribute.indexOf('-'));
			myValue5 = controlAttribute.substring(controlAttribute.indexOf("-")+1,controlAttribute.length);


			if( myValue5.indexOf('|') < 0 )
				break;

			myValue5 = myValue5.substring(0,myValue5.indexOf('|'));

			if ( myValue1.length < myValue4 || myValue1.length > myValue5 )
			{
				errors = putInfo2+'长度不正确';
				break;
			}
		}
	}


  	if ( errors != '' )
  	{
  		alert(errors);
  		if( !val.disabled )
  			val.focus();
		document.returnValue = (errors == '');
  		return false;
  	}else
  	{
		document.returnValue = (errors == '');
  	   return true;
  	}
  	
}

//控件数据校验
function validate()
{
	  var i,myValue1,myValue2,myValue3,myValue4,myValue5,errors='';
	  var val,val1;

	  var controlAttribute,putInfo1,putInfo2;

	  var args = validate.arguments;
	  var checkMailAddress = /\w@\w{3,}\.\w{3,}/;  //创建正则表达式校验邮件地址对象

	  var checkInteger = /^[+-]?\d+$/;  //创建正则表达式校验整数对象

	  var checkFloat = /^[+-]?\d+(\.\d+)?$/;  //创建正则表达式校验浮点数对象
	for (i=0; i<(args.length-2); i+=3)
	{
		controlAttribute = args[i];
		putInfo1 = args[i+1];
		putInfo2 = args[i+2];
		//add for period check ; should add at the end of all validate invokes
		putInfo3 = args[i+3];
		if( controlAttribute.indexOf('D')>=0 || controlAttribute.indexOf('Y')>=0 ||  controlAttribute.indexOf('T')>=0 )
			val = MM_findObj_( putInfo1+"year" );
		else
			val = MM_findObj_( putInfo1 );

		//如果控件不存在，直接跳过
		if( !val )
			continue;

		myValue1 = val.value;


		//对非空的处理
		if( controlAttribute.indexOf('R') >= 0 )
		{
			if( controlAttribute.indexOf('D8')>=0 )
			{
				val = MM_findObj_( putInfo1+'year' );
				myValue1 = MM_findObj_( putInfo1+'year' ).value;
				myValue2 = MM_findObj_( putInfo1+'monthfrom' ).value;
				myValue3 = MM_findObj_( putInfo1+'monthto' ).value;
				if( myValue1 == '' || myValue2 == '' || myValue3 == '' )
				{
					errors = putInfo2+'不能为空';
					break;
				}
			}
			else if( controlAttribute.indexOf('D6')>=0 )
			{
				val = MM_findObj_( putInfo1+'year' );
				myValue1 = MM_findObj_( putInfo1+'year' ).value;
				myValue2 = MM_findObj_( putInfo1+'month' ).value;
				//myValue3 = MM_findObj_( putInfo1+'monthto' ).value;
				if( myValue1 == '' || myValue2 == '' )
				{
					errors = putInfo2+'不能为空';
					break;
				}
			}
			else if( controlAttribute.indexOf('DH')>=0 )
			{
				val = MM_findObj_( putInfo1+'year' );
				myValue1 = MM_findObj_( putInfo1+'year' ).value;
				myValue2 = MM_findObj_( putInfo1+'month' ).value;
				myValue4 = MM_findObj_( putInfo1+'hour' ).value;
				val1 = MM_findObj_( putInfo1+'day' );

				if( val1 )
					myValue3 = MM_findObj_( putInfo1+'day' ).value;
				else
					myValue3 = MM_findObj_( putInfo1+'date' ).value;

				if( myValue1 == '' || myValue2 == '' || myValue3 == '' || myValue4 == '')
				{
					errors = putInfo2+'不能为空';
					break;
				}
			}
			else if( controlAttribute.indexOf('D')>=0 )
			{
				val = MM_findObj_( putInfo1+'year' );
				myValue1 = MM_findObj_( putInfo1+'year' ).value;
				myValue2 = MM_findObj_( putInfo1+'month' ).value;
				val1 = MM_findObj_( putInfo1+'day' );

				if( val1 )
					myValue3 = MM_findObj_( putInfo1+'day' ).value;
				else
					myValue3 = MM_findObj_( putInfo1+'date' ).value;

				if( myValue1 == '' || myValue2 == '' || myValue3 == '' )
				{
					errors = putInfo2+'不能为空';
					break;
				}
			}			
			else if( controlAttribute.indexOf('S')>=0 )
			{

				val = MM_findObj_( putInfo1 );
				myValue1 = MM_findObj_( putInfo1 ).value;
				if( myValue1 == '#' ||myValue1=="")
				{
					errors = '请选择'+putInfo2;
					break;
				}
			}
			else
			{
				val = MM_findObj_( putInfo1 );
				myValue1 = MM_findObj_( putInfo1 ).value;
				if(trim(myValue1)== '' )
				{
					errors = putInfo2+'不能为空';
					break;
				}
			}

		}
		//对起止时间的验证
		/**
		if( controlAttribute.indexOf('M')>=0 ) {
			//errors = putInfo1 +'起始日期不正确';
			//break;
			if(!checkDateTime( MM_findObj_( putInfo1+'year').value +'-'+MM_findObj_( putInfo1+'QSmonth' ).value+'-'+MM_findObj_( putInfo1+'QSdate' ).value )) {
				errors = putInfo2 + '不正确';
				break;
			}
		}
		if( controlAttribute.indexOf('N')>=0 ) {
			if(!checkDateTime( MM_findObj_( putInfo1+'year').value +'-'+MM_findObj_( putInfo1+'JZmonth' ).value+'-'+MM_findObj_( putInfo1+'JZdate' ).value )) {
				errors = putInfo2 + '不正确';
				break;
			}
		}
		*/
		//对有效期的处理 一个年 两个月两个日
		if( controlAttribute.indexOf('T')>=0 )
	 	{
	 		
	 		val = MM_findObj_( putInfo1+'year' );
	 		val1 = MM_findObj_( putInfo1+'day' );
	 		if( val1 )
	 		{
	 			myValue1 = new Date (MM_findObj_( putInfo1+"year" ).value , MM_findObj_( putInfo1+"QSmonth" ).value , MM_findObj_( putInfo1+"QSday" ).value);
	 			myValue2 = new Date (MM_findObj_( putInfo1+"year" ).value , MM_findObj_( putInfo1+"JZmonth" ).value , MM_findObj_( putInfo2+"JZday" ).value);
	 		}
	 		else
	 		{
	 			myValue1 = new Date (MM_findObj_( putInfo1+"year" ).value , MM_findObj_( putInfo1+"QSmonth" ).value , MM_findObj_( putInfo1+"QSdate" ).value);
	 			myValue2 = new Date (MM_findObj_( putInfo1+"year" ).value , MM_findObj_( putInfo2+"JZmonth" ).value , MM_findObj_( putInfo2+"JZdate" ).value);
	 		}



    			if( myValue1 > myValue2 )
			{
				errors = '起始日期不能大于截止日期';
				break;
			}
			else
			{//add for period check
				if((controlAttribute.indexOf('P')>=0)&&( myValue2 - myValue1 > (putInfo3)*24*60*60*1000))
				{
					errors = '结束时间不能大于开始时间超过 ' + putInfo3 + ' 天';
					break;
				}
			}
	 	}

	 	//对有效期的处理
	 	if( controlAttribute.indexOf('Y')>=0 )
	 	{
	 		val = MM_findObj_( putInfo1+'year' );
	 		val1 = MM_findObj_( putInfo1+'day' );
	 		if( val1 )
	 		{
	 			myValue1 = new Date (MM_findObj_( putInfo1+"year" ).value , MM_findObj_( putInfo1+"month" ).value , MM_findObj_( putInfo1+"day" ).value);
	 			myValue2 = new Date (MM_findObj_( putInfo2+"year" ).value , MM_findObj_( putInfo2+"month" ).value , MM_findObj_( putInfo2+"day" ).value);
	 		}
	 		else
	 		{
	 			myValue1 = new Date (MM_findObj_( putInfo1+"year" ).value , MM_findObj_( putInfo1+"month" ).value , MM_findObj_( putInfo1+"date" ).value);
	 			myValue2 = new Date (MM_findObj_( putInfo2+"year" ).value , MM_findObj_( putInfo2+"month" ).value , MM_findObj_( putInfo2+"date" ).value);
	 		}



    			if( myValue1 > myValue2 )
			{
				errors = '开始时间不能大于结束时间';
				break;
			}
			else
			{//add for period check
				if((controlAttribute.indexOf('P')>=0)&&( myValue2 - myValue1 > (putInfo3)*24*60*60*1000))
				{
					errors = '结束时间不能大于开始时间超过 ' + putInfo3 + ' 天';
					break;
				}
			}
	 	}

	 	//对其他属性的处理
	 	if( myValue1 != '' )
	 	{

		 	if ( controlAttribute.indexOf('I') >= 0 )
		 	{
		 		if((!checkInteger.test(myValue1))||myValue1>2147483647||myValue1<-2147483648|| myValue1.indexOf('+') >= 0)//去除正号 
				{
					errors = putInfo2+'只能是小于(2147483647)整数';
					break;
				}
		 	}
		 	else if ( controlAttribute.indexOf( 'Z' ) >= 0 )
		 	{
		 		if( !checkInteger.test(myValue1) || myValue1.indexOf('-') >= 0 || myValue1==0 )//正整数不包括0  11682 wangb 20170519 
				{
					errors = putInfo2+'只能是正整数';
					break;
				}				
		 	}
		 	else if(  controlAttribute.indexOf('F') >= 0 )
			{
				if( !checkFloat.test(myValue1) )
				{
					errors = putInfo2+'只能是实数';
					break;
				}
			}
			else if( controlAttribute.indexOf('D8') >= 0 )
			{
				myValue4 = new Date ( myValue1 , MM_findObj_( putInfo1+'monthfrom' ).value ,'01');
				myValue5 = new Date ( myValue1 , MM_findObj_( putInfo1+'monthto' ).value ,'01');
				if( myValue1.length !=4 || !checkDateTime( myValue1 +'-'+MM_findObj_( putInfo1+'monthfrom' ).value+'-'+'01' ) || !checkDateTime( myValue1 +'-'+MM_findObj_( putInfo1+'monthto' ).value+'-'+'01' ))
				{
					errors = putInfo2+'时间不正确';
					break;
				}
				else if( myValue4 > myValue5 )
				{
					errors = putInfo2+'开始时间不能大于结束时间';
					break;
				}
				else if ( myValue1<1900 || myValue1>2100 )
				{
					errors = putInfo2+'不能小于1900年，大于2100年';
					break;
				}
			}
			else if( controlAttribute.indexOf('D6') >= 0 )
			{
				myValue4 = new Date ( myValue1 , MM_findObj_( putInfo1+'month' ).value ,'01');
				//myValue5 = new Date ( myValue1 , MM_findObj_( putInfo1+'monthto' ).value ,'01');
				if( myValue1.length !=4 || !checkDateTime( myValue1 +'-'+MM_findObj_( putInfo1+'month' ).value+'-'+'01' ) )
				{
					errors = putInfo2+'时间不正确';
					break;
				}

				else if ( myValue1<1900 || myValue1>2100 )
				{
					errors = putInfo2+'不能小于1900年，大于2100年';
					break;
				}
			}
			else if( controlAttribute.indexOf('DH')>=0 )
			{
				val1 =  MM_findObj_( putInfo1+'day' );
				myValue4 = '';
				myValue5 = MM_findObj_( putInfo1+'hour' ).value;
				if( val1 )
					myValue4 = MM_findObj_( putInfo1+'day' ).value;
				else
					myValue4 = MM_findObj_( putInfo1+'date' ).value;				

				if( myValue1.length!=4 || !checkDateTime( myValue1+'-'+MM_findObj_( putInfo1+'month' ).value+'-'+myValue4 ) || !checkInteger.test(myValue5) || myValue5<0 || myValue5>23 )
				{
					errors = putInfo2+'时间不正确';
					break;
				}				
				else if ( myValue1<1900 || myValue1>2100 )
				{
					errors = putInfo2+'不能小于1900年，大于2100年';
					break;
				}
			}
		    	else if( controlAttribute.indexOf('D')>=0 )
			{
				val1 =  MM_findObj_( putInfo1+'day' );
				myValue4 = '';
				if( val1 )
					myValue4 = MM_findObj_( putInfo1+'day' ).value;
				else
					myValue4 = MM_findObj_( putInfo1+'date' ).value;

				if( myValue1.length!=4 || !checkDateTime( myValue1+'-'+MM_findObj_( putInfo1+'month' ).value+'-'+myValue4 ) )
				{
					errors = putInfo2+'时间不正确';
					break;
				}
				else if ( myValue1<1900 || myValue1>2100 )
				{
					errors = putInfo2+'不能小于1900年，大于2100年';
					break;
				}
			}			
			else if( controlAttribute.indexOf('E')>=0 )
			{
				if ( !checkMailAddress.test(myValue1) )
				{
					errors = putInfo2+'不是邮件地址';
					break;
				}
			}
			if ( controlAttribute.indexOf( 'N' ) >= 0 )
			{
				if( !checkInteger.test(myValue1) )
				{
					errors = putInfo2+'只能是整数';
					break;
				}
				else if ( myValue1<1900 || myValue1>2100 )
				{
					errors = putInfo2+'不能小于1900年，大于2100年';
					break;
				}
			}
		}

		//长度判断
		if( controlAttribute.indexOf('L|') >= 0 && controlAttribute.indexOf('L|') < controlAttribute.indexOf('-') )
		{

			if( !controlAttribute.indexOf('R') >= 0 && myValue1.length==0 )
				break;

			val = MM_findObj_( putInfo1 );
			myValue4 = controlAttribute.substring(controlAttribute.indexOf("L|")+2,controlAttribute.indexOf('-'));
			myValue5 = controlAttribute.substring(controlAttribute.indexOf("-")+1,controlAttribute.length);


			if( myValue5.indexOf('|') < 0 )
				break;

			myValue5 = myValue5.substring(0,myValue5.indexOf('|'));

			if ( myValue1.length < myValue4 || myValue1.length > myValue5 )
			{
				errors = putInfo2+'长度不正确';
				break;
			}
		}
	}


  	if ( errors != '' )
  	{
  		alert(errors);
  		if( !val.disabled && val.style.display != 'none' )
  			val.focus();
		document.returnValue = (errors == '');
  		return false;
  	}else
  	{
		document.returnValue = (errors == '');
  	   return true;
  	}
  	
}




function checkDateTime( str )
{
	var reg = /^(\d{1,4})-(\d{1,2})-(\d{1,2})$/; //创建正则表达式校验时间对象
	var r = str.match(reg);

	if(r==null)
		return false;
	else
	{
		var d= new Date(r[1], --r[2],r[3]);
		if(d.getFullYear()!=r[1])
			return false;
		if(d.getMonth()!=r[2])
			return false;
		if(d.getDate()!=r[3])
			return false;
	}
	return true;
}




//清除form里面的控件值
function clearAll( )
{
	var formId = '';
	var id;
	var args = clearAll.arguments;
	if( args.length == 0 )
		for( var i = 0 ; i < document.forms.length ; i++ )
			formId = formId + i +',';
	else
		formId = args[0]+',';

	while( formId.indexOf(',') > 0 )
	{
		id = formId.substring(0,formId.indexOf(','));
		formId = formId.substring(formId.indexOf(',')+1);
		for( i = 0 ;i < document.forms[id].elements.length ; i ++ )
		{

			if( document.forms[id].elements[i].type=='text' && document.forms[id].elements[i].disabled==false )
				document.forms[id].elements[i].value = '';
			
			else if( document.forms[id].elements[i].type=='textarea' && document.forms[id].elements[i].disabled==false )
				document.forms[id].elements[i].value = '';
			else if(document.forms[id].elements[i].type=='select-one')
				document.forms[id].elements[i].value = document.forms[id].elements[i].options[0].value;

		}
	}
}


//克隆数据
function cloneData()
{
	 var args = cloneData.arguments;
	 var souVal = args[0];
	 var myValue = souVal.value;
	 var i,deVal;

	 for( i = 1 ;i < args.length ; i++ )
	 {
	 	deVal = MM_findObj_( args[i] );
	 	deVal.value= myValue;
	 }
}



//多个控件只能有一个能取值的校验
function onlyOneValue()
{
	var i,j,val,myValue;
	var flag = 0,radioValue = -1;
	var controlName,controlValue;
	var args = onlyOneValue.arguments;
	var warnInfo = args[0];

	for ( i=1; i < (args.length-1); i+=2)
	{
		controlName = args[i];
		controlValue = args[i+1];
		radioValue = -1;

		if( controlName.indexOf("(radio)")>0)
		{
			controlName = controlName.substring(0,controlName.length-7);
			radioValue = 0;
		}

		val = MM_findObj_( controlName );
		for ( j = 0; j < val.length ; j++ )
			if( val[j].checked )
				radioValue = j;

		if( val )
		{
			if( radioValue >= 0 )
				myValue = val[radioValue].value;
			else
				myValue = val.value;

			if( myValue != controlValue)
				flag ++;
		}
	}

  	if ( flag != 1 )
  	{
  		alert( warnInfo+'必须选择一种并且只能选择一种，请确认。');
  		return false;
  	}
  	return true;
}

//打开帮助系统
function openHelp(codeType,val)
{
	if( val.type=='text' )
		window.open('/xtwh/help/CodeTextHelp.jsp?controlName='+val.name+'&codeType='+codeType+'&codeValue='+val.value+'','help','toolbar=no,status=no,location=no,directories=no,menubar=no,scrollbars=yes,resizable=no,width=500,height=530');
	else if( val.type=='select-one' )
		window.open('/xtwh/help/CodeSelectHelp.jsp?controlName='+val.name+'&codeType='+codeType+'','help','toolbar=no,status=no,location=no,directories=no,menubar=no,scrollbars=yes,resizable=no,width=500,height=530');
}

//获得帮助系统代码
function setCodeValue( cname )
{
	var myCodeValue = document.valueForm.codeValue.value;
	for( var i = 0 ;i < window.opener.document.forms.length; i ++)
	{
		for( var j = 0 ; j < window.opener.document.forms[i].elements.length ; j++ )
		{
			if( window.opener.document.forms[i].elements[j].name == cname)
			{

				window.opener.document.forms[i].elements[j].value = myCodeValue;
				if ( window.opener.document.forms[i].elements[j].value == '' )
					window.opener.document.forms[i].elements[j].value = '#';
				break;
			}
		}
	}
}

//显示层
function showDiv( divName )
{
	divName.style.display='';
}

//隐含层
function hiddenDiv( divName )
{
	divName.style.display='none';
}
/****************/
function hiddenTDiv(divName1,divName2) {
	divName1.style.display='none';
	divName2.style.display='none';
}
function showTDiv(divName1,divName2) {
	divName1.style.display='';
	divName2.style.display='';
}
function showDDiv(divName1,divName2) {
	divName1.style.display='';
	divName2.style.display='none';
}
function hiddenDDiv(divName1,divName2) {
	divName1.style.display='none';
	divName2.style.display='';
}
/**************/
//获得帮助系统
function getHelp( codeType , control )
{
	if(event.keyCode==113)
		getCode(codeType,control);
	else if(event.keyCode==120)
		openHelp(codeType,control);
	else if(event.keyCode==123)
		getCnCode(codeType,control);
}

//以下全为点击按纽的提示
function ifdel()
{
	return ( confirm('确认删除选择的项目？') );
}

function ifdelete(info)
{
	return ( confirm("确认要删除["+info+"]？") );
}
function ifdelinfo()
{
	return ( confirm('确认删除？') );
}
function ifqrbd()
{
	return ( confirm('你确认要补档吗？') );
}

function ifqrxh()
{
	return ( confirm('确认销毁吗？') );
}

function ifqbhk()
{
	return ( confirm('确认全部验收吗？') );
}
function ifzdkp()
{
	return ( confirm('确认要做自动考评？') );
}

function ifrelease()
{
	return ( confirm('确认发布选择的项目？') );
}

function ifcancel()
{
	return ( confirm('确认撤消选择的项目？') );
}

function ifback1()
{
	return ( confirm('确认退还选择的项目？') );
}

function ifback2()
{
	return ( confirm('确认要退还上一审批人？') );
}

function ifend()
{
	return ( confirm('确认结束审批吗？') );
}

function iftqsp()
{
	return ( confirm('请再次确认所有项目均已填写,提交审批吗？') );
}

function ifqrbc()
{
	return ( confirm('确认保存吗？') );
}
function ifqrup()
{
	return ( confirm('确认修改吗？') );
}

function ifqrreject()
{
	return ( confirm('您确认要驳回吗？') );
}

function ifdisagree()
{
	return ( confirm('确认本文书不予批复。\n确认后，不予批复结果将全局生效') );
}

function ifagree()
{
	return ( confirm('确认发布本文书审批结果。\n确认后，审批结果将全局生效。') );
}
function ifinure()
{
	return ( confirm('您要生效此文书吗?') );
}

function ifcontinue()
{
	alert("表单中数据未保存,请保存！");
	return false;
	//return ( confirm('表单中数据未保存，是否继续?') );
}


function ifqrtj()
{
	return ( confirm('您确认要提交吗?') );
}


function ifuninure()
{
	return ( confirm('您确定要撤销生效此文书吗？') );
}
function ifwcwlhx()
{
	return ( confirm('您要核销此文书吗?') );
}


function ifSubmitNext()
{
	return ( confirm('确认提交下环节？') );
}

function ifSubmitPrev()
{
	return ( confirm('确认返回上环节？') );
}


function ifjs()
{
	return ( confirm('确认结算吗？') );
}

function ifqrss()
{
	return ( confirm('确认实施吗？') );
}

function ifqrgh()
{
	return ( confirm('确认归还吗？') );
}

function ifrysx()
{
	return ( confirm('确定要人员筛选吗？') );
}

function iffpzw()
{
	return ( confirm('确定要分配职位吗？') );
}

function ifmshj()
{
	return ( confirm('确定要移动面试环节吗？') );
}
function ifmsdel()
{
	return ( confirm('确定要删除吗？') );
}
function ifyiku()
{
	return ( confirm('确定要移库吗？') );
}
function ifjsho()
{
	return ( confirm('确定要结束活动吗？') );
}
function ifjsjh()
{
	return ( confirm('确定要结束计划吗？') );
}
function ifygxq()
{
	return ( confirm('确定要引入用工需求吗？') );
}
function ifyosq()
{
	return ( confirm('确定要引入用工申请吗？') );
}

function ifdelall()
{
	return ( confirm('确定要全部清空吗？') );
}

function if_resend()
{
	return ( confirm('确定要重发吗？') );
}

function changeDiv()
{
	var args = changeDiv.arguments;
	if( args.length < 1 )
		return;

	var myValue = args[0].value;
	var i,divName;
	for( i = 1 ; i < args.length; i+=2)
	{
		if( myValue == args[i] )
			showDiv(args[i+1]);
		else
			hiddenDiv(args[i+1]);
	}
}



//自动计算返回值
function getZdValue()
{
	var args = getZdValue.arguments;
	if( args.length == 0 )
		return '0';

	var srcExp = args[0];
	var repVal = /@\w+/i;
	for( var i = 1 ; i < args.length ;i++ )
		srcExp = srcExp.replace(repVal,args[i]);

	value=eval(srcExp);

	if(value=='Infinity')
	{
	    document.returnValue = false;
	    return '除零错误!!!';
	}
    else
        document.returnValue = true;
	str=value.toFixed(2);
	var res=str.substr(str.indexOf(".")+1,2);
	str=str.substr(0,str.length-3);
	str=formatNumber(str);
	str=str+"."+res;
	return str;
}

//格式化数字
function formatNumber(v)
{
	var re=/(\d+)(\d{3})/;
	var s=v.toString();

	while(re.test(s))
		s=s.replace(re,"$1,$2");
	return s;
}

// 校验身份证号码
function validateID(a,b)
{
   if(document.returnValue== false)
   {
      return;
	}
   lr = MM_findObj_(a);
   hm = MM_findObj_(b);

   if(!lr || !hm)
	{
	   return;
	}
   if(lr.value == '1')
   {
     if(!(hm.value.length == 15 || hm.value.length == 18))
	 {
	      alert('身份证号码位数不对');
  		  hm.focus();
		  document.returnValue=false;
	}
  }
}
// 比较两个日期的大小
function validateDate(putInfo1,putInfo2,c)
{
   if(document.returnValue== false)
   {
      return;
	}
	if(!MM_findObj_( putInfo1+'year' ) || !MM_findObj_( putInfo2+'year' ))
	{
		return;
	}
	 		val = MM_findObj_( putInfo1+'year' );
	 		val1 = MM_findObj_( putInfo2+'day' );
	 		if( val1 )
	 		{
	 			myValue1 = new Date (MM_findObj_( putInfo1+"year" ).value , MM_findObj_( putInfo1+"month" ).value , MM_findObj_( putInfo1+"day" ).value);
	 			myValue2 = new Date (MM_findObj_( putInfo2+"year" ).value , MM_findObj_( putInfo2+"month" ).value , MM_findObj_( putInfo2+"day" ).value);
	 		}
	 		else
	 		{
	 			myValue1 = new Date (MM_findObj_( putInfo1+"year" ).value , MM_findObj_( putInfo1+"month" ).value , MM_findObj_( putInfo1+"date" ).value);
	 			myValue2 = new Date (MM_findObj_( putInfo2+"year" ).value , MM_findObj_( putInfo2+"month" ).value , MM_findObj_( putInfo2+"date" ).value);
	 		}
    		if( myValue1 > myValue2 )
			{
	      alert(c);
  		  val.focus();
		  document.returnValue=false;
			}

}
// 如果第一个参数的值不为空，则第二个参数的值也不能为空，ｃ为用户提示信息
function validateNull(putInfo1,putInfo2,c)
{
   if(document.returnValue== false)
   {
      return;
	}
		val = MM_findObj_( putInfo1);
		val1 = MM_findObj_( putInfo2);
		//如果控件不存在，直接跳过
		if( !val || !val1 )
			return;

		if(!( val.value== ''|| val.value == '#'))
		{
			if( val1.value== '' || val1.value == '#')
			{
			  alert(c);
			  val1.focus();
			  document.returnValue=false;
			}
		}

}
// 校验用户输入的是否是一个日期（天）
function validateDay(a)
{
	if(document.returnValue== false)
	{
	  return;
	}
	val=MM_findObj_(a);
	if( !val )
		return;
	if((document.returnValue == true) && (val.value<1|| val.value>31))  {
		alert('日期不正确');
		val.focus();
		document.returnValue = (false);
	}
}


//设置内帧的高度为比较合适的高度
function iframeScrollHeight()
{

   if(top.location != self.location && window.parent!=null)
   {
      var a = window.parent.document.getElementsByTagName('iframe');
      for (var i=0; i<a.length; i++)
      {
         if (a[i].name == self.name) 
         {
			a[i].height = document.body.scrollHeight; 
          	return;
         }
       }
   }  

}

//两个单选按钮，分别显示不同的层
function show2Div(showName1,showName2,hiddenName1,hiddenName2) {
	showName1.style.display='';
	showName2.style.display='';
	hiddenName1.style.display='none';
	hiddenName2.style.display='none';
}
// 重定向
function redirect(URLStr)
{ location = URLStr; }

/*
 *codeid,相关代码类
 *mytarget,选中的代码值需填充的Element,for examples input text
 */
function openInputCodeDialog(codeid,mytarget,isAccord) 
{
	 if(isNotPC()){
		 gPopupMask = document.getElementById("popupMask");
		 gPopupContainer = document.getElementById("popupContainer");
		 gPopFrame = document.getElementById("popupFrame");	
		 loadcssfile("/js/subModal-1.6/style.css" ,"css");
		 loadcssfile("/js/subModal-1.6/subModal.css" ,"css");
		insertJS("/js/subModal-1.6/common.js","js" ,function(){
			insertJS("/js/subModal-1.6/subModal.js","js",function(){
					 gPopupIsShown = false;
					 gDefaultPage = "/js/subModal-1.6/loading.html";
					 gHideSelects = false;
					 gTabIndexes = new Array();
					 gTabbableTags = new Array("A","BUTTON","TEXTAREA","INPUT","IFRAME");	
					if (!document.all) {
						document.onkeypress = keyDownHandler;
					}
					if(null==gPopupContainer&&null==gPopupMask&&null==gPopFrame)
					initPopUp();
					var thecodeurl='/system/newcodeselect.jsp?codesetid='+codeid+'&codeitemid=&mytarget='+mytarget+'&isAccord='+isAccord;
					showPopWin(thecodeurl, 400, 400, returnRefresh);//tianye update 兼容移动产品
				});
		});
		return; 
	 }
	
    var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
    if(mytarget==null)
      return;
    var oldInputs=document.getElementsByName(mytarget);    
    oldobj=oldInputs[0];
    //根据代码显示的对象名称查找代码值名称	
    target_name=oldobj.name;
    hidden_name=target_name.replace(".hzvalue",".value");//调用该方法时用到.hzvalue
    hidden_name=hidden_name.replace(".viewvalue",".value");
    var hiddenInputs=document.getElementsByName(hidden_name);
    if(hiddenInputs!=null)
    {
    	hiddenobj=hiddenInputs[0];
    	codevalue="";
    }
    if(isAccord!="0"&&isAccord!="3")//isAccord=="3"兼职
      isAccord="1";//??·? codeseitid ±?????·????????????? codesetid?è??????????·?????  1???è??  0?????è??
   var theArr=new Array(codeid,codevalue,oldobj,hiddenobj,isAccord); 
    thecodeurl="/system/codeselect.jsp?codesetid="+codeid+"&codeitemid="; 
    //xuj update 2011-5-11 兼容firefox、chrome
    var dw=300,dh=400,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
    
	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;    
	var dialogWidth="300";
	var dialogHeight="400";
	if(!window.showModalDialog){
     	dialogWidth='290';
     	dialogHeight="420";
  		window.dialogArguments = theArr;
    }
    var config = {
		width:dialogWidth,
        height:dialogHeight,
        dialogArguments:theArr,
        title:'选择机构',
        id:'openCondCodeDialogWin'
    }
    
	return modalDialog.showModalDialogs(iframe_url,null,config);

}

//支持过滤无效代码项  isValidCtr="0"是否过滤有效无效。0不控制，1控制
function openInputCodeDialogValidCtr(codeid,mytarget,isAccord,isValidCtr) 
{
    var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
    if(mytarget==null)
      return;
    var oldInputs=document.getElementsByName(mytarget);    
    oldobj=oldInputs[0];
    //根据代码显示的对象名称查找代码值名称	
    target_name=oldobj.name;
    hidden_name=target_name.replace(".hzvalue",".value");//调用该方法时用到.hzvalue
    hidden_name=hidden_name.replace(".viewvalue",".value");
    var hiddenInputs=document.getElementsByName(hidden_name);
    if(hiddenInputs!=null)
    {
    	hiddenobj=hiddenInputs[0];
    	codevalue="";
    }
    if(isAccord!="0"&&isAccord!="3")//isAccord=="3"兼职
      isAccord="1";//??·? codeseitid ±?????·????????????? codesetid?è??????????·?????  1???è??  0?????è??
   var theArr=new Array(codeid,codevalue,oldobj,hiddenobj,isAccord); 
    thecodeurl="/system/codeselect.jsp?codesetid="+codeid+"&codeitemid=&isValidCtr="+isValidCtr; 
    //xuj update 2011-5-11 兼容firefox、chrome
    var dw=300,dh=400,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
    var popwin= window.showModalDialog(thecodeurl, theArr, 
        "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
}
//支持多选  isCheckBox 是否多选 1是 0否，hirechannel   招聘渠道，用于外网招聘
function openInputCodeDialog2(codeid,mytarget,isAccord,hirechannel,isCheckBox) 
{
    var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
    if(mytarget==null)
      return;
    var oldInputs=document.getElementsByName(mytarget);    
    oldobj=oldInputs[0];
    //根据代码显示的对象名称查找代码值名称	
    target_name=oldobj.name;
    hidden_name=target_name.replace(".hzvalue",".value");//调用该方法时用到.hzvalue
    hidden_name=hidden_name.replace(".viewvalue",".value");
    var hiddenInputs=document.getElementsByName(hidden_name);
    if(hiddenInputs!=null)
    {
    	hiddenobj=hiddenInputs[0];
    	codevalue="";
    }
    if(isAccord!="0"&&isAccord!="3")//isAccord=="3"兼职
      isAccord="1";//??·? codeseitid ±?????·????????????? codesetid?è??????????·?????  1???è??  0?????è??
    var theArr=new Array(codeid,codevalue,oldobj,hiddenobj,isAccord,isCheckBox); 
    thecodeurl="/system/codeselect.jsp?codesetid="+codeid+"&codeitemid=&hirechannel="+hirechannel; 
    //xuj update 2011-5-11 兼容firefox、chrome
    var dw=300,dh=400,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
    var popwin= window.showModalDialog(thecodeurl, theArr, 
        "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
}

function openTrainInputCodeDialog(codeid,mytarget) 
{
    var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
    if(mytarget==null)
      return;
    var oldInputs=document.getElementsByName(mytarget+"viewvalue");    
    oldobj=oldInputs[0];
    //根据代码显示的对象名称查找代码值名称	
    target_name=oldobj.name;
    hidden_name=target_name.replace("viewvalue","");
    var hiddenInputs=document.getElementsByName(mytarget);
    if(hiddenInputs!=null)
    {
    	hiddenobj=hiddenInputs[0];
    	codevalue="";
    }
    var theArr=new Array(codeid,codevalue,oldobj,hiddenobj); 
    thecodeurl="/system/traincodeselect.jsp?codesetid="+codeid+"&codeitemid="; 
    //xuj update 2011-5-11 兼容firefox、chrome
    var dw=300,dh=400,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
    var popwin= window.showModalDialog(thecodeurl, theArr, 
        "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
}
/*
 * 培训课程分类与培训课程名称查询
 */
function openTrainLessonInputCodeDialog(codeid,mytarget,isAccord,childid) 
{
    var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
    if(mytarget==null)
      return;
    var oldInputs=document.getElementsByName(mytarget);    
    oldobj=oldInputs[0];
    //根据代码显示的对象名称查找代码值名称	
    target_name=oldobj.name;
    hidden_name=target_name.replace(".hzvalue",".value");//调用该方法时用到.hzvalue
    hidden_name=hidden_name.replace(".viewvalue",".value");
    var hiddenInputs=document.getElementsByName(hidden_name);
    if(hiddenInputs!=null)
    {
    	hiddenobj=hiddenInputs[0];
    	codevalue=childid;
    }
    if(isAccord!="0"&&isAccord!="3")//isAccord=="3"兼职
      isAccord="1";//??·? codeseitid ±?????·????????????? codesetid?è??????????·?????  1???è??  0?????è??
    var theArr=new Array(codeid,codevalue,oldobj,hiddenobj,isAccord); 
    thecodeurl="/system/codeselect.jsp?codesetid="+codeid+"&codeitemid=&khtargetcard=targetcard"; 
    //xuj update 2011-5-11 兼容firefox、chrome
    var dw=300,dh=400,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
    var popwin= window.showModalDialog(thecodeurl, theArr, 
        "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
}

/*
 *目标卡制订专用控件  JinChunhai 2011.08.20
 */
function openKhTargetCardInputCode(codeid,mytarget,isAccord) 
{
    var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
    if(mytarget==null)
      return;
    var oldInputs=document.getElementsByName(mytarget);    
    oldobj=oldInputs[0];
    //根据代码显示的对象名称查找代码值名称	
    target_name=oldobj.name;
    hidden_name=target_name.replace(".hzvalue",".value");//调用该方法时用到.hzvalue
    hidden_name=hidden_name.replace(".viewvalue",".value");
    var hiddenInputs=document.getElementsByName(hidden_name);
    if(hiddenInputs!=null)
    {
    	hiddenobj=hiddenInputs[0];
    	codevalue="";
    }
    if(isAccord!="0"&&isAccord!="3")//isAccord=="3"兼职
      isAccord="1";//??·? codeseitid ±?????·????????????? codesetid?è??????????·?????  1???è??  0?????è??
    var theArr=new Array(codeid,codevalue,oldobj,hiddenobj,isAccord); 
    thecodeurl="/system/codeselect.jsp?codesetid="+codeid+"&codeitemid=&khtargetcard=targetcard"; 
    var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl; 
    //xuj update 2011-5-11 兼容firefox、chrome
    var dw=300,dh=400,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
	
	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
    var dialogWidth="300";
    if(!window.showModalDialog){
    	dialogWidth='315';
  		window.dialogArguments = theArr;
    }
	var dialogHeight="430";
    var config = {
		width:dialogWidth,
        height:dialogHeight,
        dialogArguments:theArr,
        type:'1', 
        title:'选择代码',
        id:'openCondCodeDialogWin'
    }
   
    modalDialog.showModalDialogs(iframe_url,null,config)
    
    //var popwin= window.showModalDialog(thecodeurl, theArr, 
    //    "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
}

function openInputCodeDialog1(codeid,mytarget,isAccord) 
{
    var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
    if(mytarget==null)
      return;
    var oldInputs=document.getElementById(mytarget);
    oldobj=oldInputs;
    //根据代码显示的对象名称查找代码值名称	
    var hiddenInputs=document.getElementById(mytarget+"value");
    if(hiddenInputs!=null)
    {
    	hiddenobj=hiddenInputs;
    	codevalue="";
    }
    if(isAccord!="0"&&isAccord!="3")//isAccord=="3"兼职
      isAccord="1";//??·? codeseitid ±?????·????????????? codesetid?è??????????·?????  1???è??  0?????è??
    var theArr=new Array(codeid,codevalue,oldobj,hiddenobj,isAccord); 
    thecodeurl="/system/codeselect.jsp?codesetid="+codeid+"&codeitemid="; 
    var popwin= window.showModelessDialog(thecodeurl, theArr, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
}




/**
*填报单位机构树
*/
function openReportorgCodeDialog(mytarget,report_type,unitcode) 
{
    var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
    if(mytarget==null)
      return;
    var oldInputs=document.getElementsByName(mytarget);    
    oldobj=oldInputs[0];
    //根据代码显示的对象名称查找代码值名称	
    target_name=oldobj.name;
    hidden_name=target_name.replace("unitDesc","unit");
    var hiddenInputs=document.getElementsByName(hidden_name);
    if(hiddenInputs!=null)
    {
    	hiddenobj=hiddenInputs[0];
    	codevalue="";
    }
    
    var theArr=new Array(oldobj,hiddenobj); 
    thecodeurl="/system/report_orgcode.jsp?unitcode="+unitcode+"&report_type="+report_type; 
    var popwin= window.showModelessDialog(thecodeurl, theArr, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
}














/**
*虚拟机构树
*/
function openVorgCodeDialog(mytarget) 
{
    var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
    if(mytarget==null)
      return;
    var oldInputs=document.getElementsByName(mytarget);    
    oldobj=oldInputs[0];
    //根据代码显示的对象名称查找代码值名称	
    target_name=oldobj.name;
    hidden_name=target_name.replace(".viewvalue",".value");
    var hiddenInputs=document.getElementsByName(hidden_name);
    if(hiddenInputs!=null)
    {
    	hiddenobj=hiddenInputs[0];
    	codevalue="";
    }
    
    var theArr=new Array(oldobj,hiddenobj); 
    thecodeurl="/system/vorgcode.jsp?codesetid=&codeitemid=&type=org"; 
    var popwin= window.showModelessDialog(thecodeurl, theArr, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
}
function openInputCodeDialogS_value(codeid,mytarget) 
{
    var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
    if(mytarget==null)
      return;
    var oldInputs=document.getElementsByName(mytarget);
    
    oldobj=oldInputs[0];
    //根据代码显示的对象名称查找代码值名称	
    target_name=oldobj.name;
    hidden_name=target_name.replace(".view_s_value",".s_value");
    var hiddenInputs=document.getElementsByName(hidden_name);
    if(hiddenInputs!=null)
    {
    	hiddenobj=hiddenInputs[0];
    	codevalue="";
    }
    var theArr=new Array(codeid,codevalue,oldobj,hiddenobj); 
    thecodeurl="/system/codeselect.jsp?codesetid="+codeid+"&codeitemid="; 
    var popwin= window.showModelessDialog(thecodeurl, theArr, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
}
function openInputCodeDialogE_value(codeid,mytarget) 
{
    var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
    if(mytarget==null)
      return;
    var oldInputs=document.getElementsByName(mytarget);
    
    oldobj=oldInputs[0];
    //根据代码显示的对象名称查找代码值名称	
    target_name=oldobj.name;
    hidden_name=target_name.replace(".view_e_value",".e_value");
    var hiddenInputs=document.getElementsByName(hidden_name);
    if(hiddenInputs!=null)
    {
    	hiddenobj=hiddenInputs[0];
    	codevalue="";
    }
    var theArr=new Array(codeid,codevalue,oldobj,hiddenobj); 
    thecodeurl="/system/codeselect.jsp?codesetid="+codeid+"&codeitemid="; 
    var popwin= window.showModelessDialog(thecodeurl, theArr, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
}

function openInputRelCodeDialog(codeid,mytarget,fieldtable,fieldid,fielddesc,orgid) 
{
    var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
    if(mytarget==null)
      return;
    var oldInputs=document.getElementsByName(mytarget);
    oldobj=oldInputs[0];
    //根据代码显示的对象名称查找代码值名称	
    target_name=oldobj.name;
    hidden_name=target_name.replace(".viewvalue",".value");
    var hiddenInputs=document.getElementsByName(hidden_name);
    if(hiddenInputs!=null)
    {
    	hiddenobj=hiddenInputs[0];
    	codevalue="";
    }
    var theArr=new Array(codeid,codevalue,oldobj,hiddenobj,fieldtable,fieldid,fielddesc,orgid); 
    thecodeurl="/system/relcodeselect.jsp?codesetid="+fieldtable+"&codeitemid="; 
    var popwin= window.showModelessDialog(thecodeurl, theArr, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
}

function openInputCodeDialogText(codeid,mytarget,hidden_name) 
{
    var codevalue,thecodeurl,target_name,hiddenobj;
    if(mytarget==null)
      return;
    var oldInputs=document.getElementsByName(mytarget);
    
    oldobj=oldInputs[0];
    //根据代码显示的对象名称查找代码值名称	
    target_name=oldobj.name;
    var hiddenInputs=document.getElementsByName(hidden_name);
    if(hiddenInputs!=null)
    {
    	hiddenobj=hiddenInputs[0];
    	codevalue="";
    }
    var theArr=new Array(codeid,codevalue,oldobj,hiddenobj); 
    thecodeurl="/system/codeselect.jsp?codesetid="+codeid+"&codeitemid="; 
    var dw=300,dh=400,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
    if(getBrowseVersion()){
		var popwin= window.showModalDialog(thecodeurl, theArr, 
        "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
    }else{//非ie浏览器   使用ext弹窗
    	var win = Ext.create('Ext.window.Window',{
			id:'openInputCodeDialogText',
			title:'机构查询',
			width:dw+20,
			height:dh+20,
			resizable:'no',
			modal:true,
			autoScoll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+thecodeurl+'"></iframe>',
			renderTo:Ext.getBody()
		 }); 
		 win.theArr = theArr;
    }

}
function openInputCodeDialogTextIncludeSetid(codeid,mytarget,hidden_name) 
{
    var codevalue,thecodeurl,target_name,hiddenobj;
    if(mytarget==null)
      return;
    var oldInputs=document.getElementsByName(mytarget);
    
    oldobj=oldInputs[0];
    //根据代码显示的对象名称查找代码值名称	
    target_name=oldobj.name;
    var hiddenInputs=document.getElementsByName(hidden_name);
    if(hiddenInputs!=null)
    {
    	hiddenobj=hiddenInputs[0];
    	codevalue="";
    }
    var theArr=new Array(codeid,codevalue,oldobj,hiddenobj); 
    thecodeurl="/system/codeselect_includesetid.jsp?codesetid="+codeid+"&codeitemid="; 
    var popwin= window.showModalDialog(thecodeurl, theArr, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");/*update by xiegh on 20180518 bug37281领导桌面：多次点击机构能弹出好多选择机构的窗体*/
}
/*
 *通用查询接口
 *tablename: 表名   fieldList:列信息集合 数组( 数组[ 0:列名 1:列描述  2:列的类型 3:如果为代码型,则为代码值,否则为空 ] )
 */
function generalQuery(tablename,fieldList)
{
	var infos=new Array();
	infos[0]=tablename;
	infos[1]=fieldList;
	var thecodeurl="/general/query/general/initGeneralQuery.jsp"; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
	var whl_str= window.showModalDialog(iframe_url, infos, 
		        "dialogWidth:560px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");			
	return whl_str;		
	
}

/*
 *通用排序接口
 *tablename: 表名   fieldList:列信息集合 数组( 数组[ 0:列名 1:列描述  2:列的类型 3:如果为代码型,则为代码值,否则为空 ] )
 */
function taxisDialog(tablename,fieldList)
{
	var infos=new Array();
	infos[0]=tablename;
	infos[1]=fieldList;
	var thecodeurl="/hire/demandPlan/positionDemand/positionDemandTree.do?br_taxis=taxis"; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
	var whl_str= window.showModalDialog(iframe_url, infos, 
		        "dialogWidth:550px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");			
	return whl_str;		
	
}



/*
 *批量设置招聘安排信息
 *
 */

function batch_selectInfo()
{
	var objarr=new Array();
	var thecodeurl="/hire/interviewEvaluating/interviewArrange.do?br_batchSelect=select"; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
	var objlist= window.showModalDialog(iframe_url, arguments, 
        "dialogWidth:550px; dialogHeight:310px;resizable:no;center:yes;scroll:yes;status:no");
        if(objlist)
	{ 
	   for(var i=0;i<objlist.length;i++)
	       objarr[i]=objlist[i];
	}	
	return objarr;
}


/*
*	选择时间段
*/

function selectTimeArea()
{
	var objarr=new Array();
	var thecodeurl="/train/plan/selectTimeArea.jsp"; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
	var objlist= window.showModalDialog(iframe_url, arguments, 
        "dialogWidth:450px; dialogHeight:350px;resizable:no;center:yes;scroll:yes;status:no");
    if(objlist)
	{ 
	   for(var i=0;i<objlist.length;i++)
	       objarr[i]=objlist[i];
	}	
	return objarr;

}


/*
*选择培训计划
*/

function selectTrainPlan(selectID)
{
	var objarr=new Array();
	var thecodeurl="/train/plan/searchCreatPlanList.do?b_selectPlan=query`selectID="+selectID; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
	var obj= window.showModalDialog(iframe_url, arguments, 
        "dialogWidth:700px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
	return obj;

}



/*
 *手工选择
 *infor,  1为选人 2为选单位 3为选部门 4为选择职位
 *dbpre_arr:应前库前缀数组(对人员而言)
 */
function handwork_selectObject(infor,dbpre)
{
	var objarr=new Array();
	var thecodeurl="/general/query/handworkSelect.do?b_init=link`type="+infor+"`show_dbpre="+dbpre; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
	var objlist= window.showModalDialog(iframe_url, arguments, 
        "dialogWidth:550px; dialogHeight:450px;resizable:no;center:yes;scroll:yes;status:no");
        if(objlist)
	{ 
	   for(var i=0;i<objlist.length;i++)
	       objarr[i]=objlist[i];
	}	
	return objarr;
}
/*
 *手工选择2
 *infor,  1为选人 2为选单位 3为选部门 4为选择职位
 *dbpre_arr:应前库前缀数组(对人员而言)
 *和手工选择1不同的地方在于此处选择单位的时候需按照管理范围过滤
 */
function handwork_selectObject2(infor,dbpre)
{
	var objarr=new Array();
	var thecodeurl="/general/query/handworkSelect.do?b_init=link`type="+infor+"`show_dbpre="+dbpre+"`manageFlag=1"; 
	if(window.$URL)
		thecodeurl = $URL.encode(thecodeurl);
	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
	var h = "500px";
	if(isIE6())
		h = "530px";
	var objlist= window.showModalDialog(iframe_url, arguments, 
        "dialogWidth:550px; dialogHeight:"+h+";resizable:no;center:yes;scroll:yes;status:no");
        if(objlist)
	{ 
	   for(var i=0;i<objlist.length;i++)
	       objarr[i]=objlist[i];
	}	
	return objarr;
}
/**考核实施/手工选人专用*/
function handwork_selectObject3(infor,dbpre,plan_b0110)
{
	var objarr=new Array();
	var thecodeurl="/general/query/handworkSelect.do?b_init2=link`type="+infor+"`show_dbpre="+dbpre+"`b0110="+plan_b0110; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
	var objlist= window.showModalDialog(iframe_url, arguments, 
        "dialogWidth:580px; dialogHeight:450px;resizable:no;center:yes;scroll:yes;status:no");
        if(objlist)
	{ 
	   for(var i=0;i<objlist.length;i++)
	       objarr[i]=objlist[i];
	}	
	return objarr;
}
/*
*infor :信息群种类 =1人员,=2单位,=3职位
*dbpre_arr:应前库前缀数组(对人员而言)
*return 返回值为对象(单位,人员,职位)键值的数组
*对人员:库前缀+A0100
*/
function quick_query(infor,dbpre_arr)
{
   var strdb="";
   if(dbpre_arr)
     strdb=dbpre_arr.toString();
   var strurl="/general/query/quick/quick_query.do?b_init=link`type="+infor+"`show_dbpre="+strdb;
	if(window.$URL)
		strurl = $URL.encode(strurl);
   var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl;
   var objlist =window.showModalDialog(iframe_url,arguments,"dialogWidth=600px;dialogHeight=550px;status=no;");  
   /*传回来的对象类型发生变化啦 instanceof Array*/
   if(objlist)
   {
     var objarr=new Array();
     for(var i=0;i<objlist.length;i++)
         objarr[i]=objlist[i];
   }
   return objarr;
}
/*通用查询及简单查询
 *query_type查询类型 =1简单查询 =2通用查询
 *
 */
function common_query(infor,dbpre_arr,query_type)
{
   var strdb="";
   if(dbpre_arr)
     strdb=dbpre_arr.toString();
   var strurl="/general/query/common/select_query_fields.do?b_init=link`type="+infor+"`show_dbpre="+strdb+"`query_type="+query_type;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
   var objlist =window.showModalDialog(iframe_url,null,"dialogWidth=600px;dialogHeight=450px;resizable=yes;status=no;");  
   /*传回来的对象类型发生变化啦 instanceof Array*/
   if(objlist)
   {
     var objarr=new Array();
     for(var i=0;i<objlist.length;i++)
         objarr[i]=objlist[i];
   }
   return objarr;
}
/*通用查询及简单查询
 *query_type查询类型 =1简单查询 =2通用查询
 *
 */
function common_query_comrow(infor,dbpre_arr,query_type,row_num)
{
   var strdb="";
   if(dbpre_arr)
     strdb=dbpre_arr.toString();
     var dw=600,dh=450,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
   var strurl="/general/query/common/select_query_fields.do?b_init=link`type="+infor+"`show_dbpre="+strdb+"`query_type="+query_type+"`row_num="+row_num;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
   if(isIE6()){
   		dw += 10;
        dh += 10;
   }
   var objlist =window.showModalDialog(iframe_url,null,"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth="+dw+"px;dialogHeight="+dh+"px;resizable=yes;status=no;");  
   /*传回来的对象类型发生变化啦 instanceof Array*/
   if(objlist)
   {
     var objarr=new Array();
     for(var i=0;i<objlist.length;i++)
         objarr[i]=objlist[i];
   }
   return objarr;
}

/*?¨???é???°?ò???é??
 *query_type?é???à?? =1?ò???é?? =2?¨???é??
 *
 */
function common_query(infor,dbpre_arr,query_type,sys_filter_factor)
{
   var strdb="";
   if(dbpre_arr)
     strdb=dbpre_arr.toString();
   var strurl="/general/query/common/select_query_fields.do?b_init=link`type="+infor+"`show_dbpre="+strdb+"`query_type="+query_type + "`filter_factor=" + sys_filter_factor;
   var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
   var objlist =window.showModalDialog(iframe_url,null,"dialogWidth=600px;dialogHeight=450px;resizable=yes;status=no;");  
   /*传回来的对象类型发生变化啦 instanceof Array*/
   if(objlist)
   {
     var objarr=new Array();
     for(var i=0;i<objlist.length;i++)
         objarr[i]=objlist[i];
   }
   return objarr;
}
/*通用查询及简单查询
 *query_type查询类型 =1简单查询 =2通用查询
 *
 */
function common_query_pri(infor,dbpre_arr,query_type,pri)
{
   var strdb="";
   if(dbpre_arr)
     strdb=dbpre_arr.toString();
   var strurl="/general/query/common/select_query_fields.do?b_init=link`type="+infor+"`show_dbpre="+strdb+"`query_type="+query_type+"`priv="+pri;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
  	if(getBrowseVersion()){
  		 var objlist =window.showModalDialog(iframe_url,null,"dialogWidth=600px;dialogHeight=450px;resizable=yes;status=no;");  
   		/*传回来的对象类型发生变化啦 instanceof Array*/
   		if(objlist)
   		{
     		var objarr=new Array();
     		for(var i=0;i<objlist.length;i++)
         		objarr[i]=objlist[i];
   		}
   		return objarr;
  	}else{
  		//兼容非IE浏览器 弹窗替换用 open弹窗  wangb 20180209 bug 34769
		var iTop = (window.screen.availHeight - 30 - 450) / 2;  //获得窗口的垂直位置
		var iLeft = (window.screen.availWidth - 10 - 600) / 2; //获得窗口的水平位置 
		window.open(iframe_url,"","width=600px,height=450px,resizable=no,scrollbars=no,status=no,left="+iLeft+",top="+iTop);
  	}
}
//open弹窗返回数据  bug 34769 wangb 20180209
function common_query_pri_returnvalue(objlist){
	 /*传回来的对象类型发生变化啦 instanceof Array*/
   var objarr=new Array();
   if(objlist)
   {
     for(var i=0;i<objlist.length;i++)
         objarr[i]=objlist[i];
   }
   return objarr;
}
/*
function generalExpressionDialog(infor,dbPre,query_type,expr){

	var strurl="/general/query/common/expression/select_query_fields.do?b_init=link&type="+info+"&show_dbpre="+dbPre+"&query_type="+queryType;
	var arguments=new Array(oldExpress);
	var strExpression =window.showModalDialog(strurl,arguments,"dialogWidth=600px;dialogHeight=450px;resizable=yes;status=yes;menubar=yes;");  
	return strExpression;
	
   var arguments=new Array(expr);     
   var strurl="/general/query/common/select_query_fields.do?b_init=link`type="+infor+"`show_dbpre="+dbPre+"`query_type="+query_type+"`expr="+expr;
   var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
   var dw=700,dh=450,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
   if(getBrowseVersion()){
	   var ss=window.showModalDialog(iframe_url,arguments,"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth=600px;dialogHeight=450px;resizable=no;status=no;");  
	   return ss;
   }else{//非IE浏览器下使用Ext.window wangb 20180125
   		function openWin(){
		    Ext.create("Ext.window.Window",{
		    	id:'selectpoint_win',
		    	width:dw,
		    	height:dh+100,
		    	title:'请选择',
		    	resizable:false,
		    	modal:true,
		    	autoScroll:true,
		    	renderTo:Ext.getBody(),
		    	html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='100%' width='100%' src='"+iframe_url+"'></iframe>"
		    }).show();	
		}
		
		if(typeof window.Ext == 'undefined'){
			insertFile("/ext/ext6/resources/ext-theme.css","css", function(){
				insertFile("/ext/ext6/ext-all.js","js" ,openWin);
			});
			
		} else {
			openWin();
		}
		return undefined;
   }
}
*/
/*
方法中添加一参数isEncode
isEncode为true时, 表示要对src路径整体加密,同时在iframe_query.jsp路径加上isEncode参数 ,在iframe_query.jsp 进行解密   wangb 20180308
isEncode为false时,还是走原来的规则
*/
function generalExpressionDialog(infor,dbPre,query_type,expr,setid,setdesc,isEncode){
/*
	var strurl="/general/query/common/expression/select_query_fields.do?b_init=link&type="+info+"&show_dbpre="+dbPre+"&query_type="+queryType;
	var arguments=new Array(oldExpress);
	var strExpression =window.showModalDialog(strurl,arguments,"dialogWidth=600px;dialogHeight=450px;resizable=yes;status=yes;menubar=yes;");  
	return strExpression;
*/
   var arguments=new Array(expr);     
   var strurl="/general/query/common/select_query_fields.do?b_init=link`type="+infor+"`setdesc="+setdesc+"`setid="+setid+"`show_dbpre="+dbPre+"`query_type="+query_type+"`expr="+expr;
   if(isEncode){//加密url路径
       strurl="/general/query/common/select_query_fields.do?b_init=link&type="+infor+"&setdesc="+setdesc+"&setid="+setid+"&show_dbpre="+dbPre+"&query_type="+query_type+"&expr="+expr;
       strurl=encode(strurl);
   }
  
   if(window.$URL)
	   strurl = $URL.encode(strurl);
   var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl;
 //路径添加加密参数isEncode
   if(isEncode) {
	   if(!window.$URL){
		   iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
	   }
	   
	   iframe_url= iframe_url + "&isEncode=1"
   }
   var dw=700,dh=450,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
    if(getBrowseVersion()){
	    var ss=window.showModalDialog(iframe_url,arguments,"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth=610px;dialogHeight=460px;resizable=no;scroll:no;status=no;");  
   		return ss;
   }else{//非IE浏览器下使用Ext.window wangb 20180125
   		function openWin(){
		    Ext.create("Ext.window.Window",{
		    	id:'selectpoint_win',
		    	width:dw,
		    	height:dh+100,
		    	title:'请选择',
		    	resizable:false,
		    	modal:true,
		    	autoScroll:true,
		    	renderTo:Ext.getBody(),
		    	html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='100%' width='100%' src='"+iframe_url+"'></iframe>"
		    }).show();	
		}
		
		if(typeof window.Ext == 'undefined'){
			insertFile("/ext/ext6/resources/ext-theme.css","css", function(){
				insertFile("/ext/ext6/ext-all.js","js" ,openWin);
			});
			
		} else {
			openWin();
		}
		return undefined;
   }
  
}


/*
 *通用复杂条件 设置;(薪资导入项目)
 * flag  0:包含常用条件操作   1:不包含常用条件操作
 * itemType  0:无返回值公式  A: N: D:
 */
function generalComplexConditionDialog2(express,flag,titleName,itemType,heapFlag)
{
  		var arguments=new Array(express,flag,titleName,itemType,heapFlag);     
	    var strurl="/general/query/common/complexCondition.do?br_init=link";
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
	    var ss=window.showModalDialog(iframe_url,arguments,"dialogWidth=860px;dialogHeight=500px;resizable=yes;scroll=no;status=no;");  
	    return ss;
}



/*
 *通用复杂条件 设置
 * flag  0:包含常用条件操作   1:不包含常用条件操作
 * itemType  0:无返回值公式  A: N: D:
 */
function generalComplexConditionDialog(express,flag,titleName,itemType)
{
  		var arguments=new Array(express,flag,titleName,itemType);     
	    var strurl="/general/query/common/complexCondition.do?br_init=link";
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
	    if(isIE6() ){
	    var ss=window.showModalDialog(iframe_url,arguments,"dialogWidth=890px;dialogHeight=500px;resizable=yes;scroll=no;status=no;"); 
	    }else{
	    var ss=window.showModalDialog(iframe_url,arguments,"dialogWidth=860px;dialogHeight=500px;resizable=yes;scroll=no;status=no;"); 
	    }
	     
	    return ss;
}
/*
 *通用复杂条件 设置
 * flag  0:包含常用条件操作   1:不包含常用条件操作
 * itemType  0:无返回值公式  A: N: D:
 * param 
 */
function generalComplexConditionDialogByParam(express,flag,titleName,itemType,param)
{
  		var arguments=new Array(express,flag,titleName,itemType);     
	    var strurl="/general/query/common/complexCondition.do?br_init=link`mode="+param;
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
	    //var ss=window.showModalDialog(iframe_url,arguments,"dialogWidth=860px;dialogHeight=500px;resizable=yes;scroll=no;status=no;"); 
	    var config = {
    	    width:870,
    	    height:500,
    	    type:'1',
    	    id:'complexCondition_win',
    	    dialogArguments:arguments
    	}
	    if(!window.showModalDialog)
	    	window.dialogArguments = arguments;
    	var ss = modalDialog.showModalDialogs(iframe_url,"complexCondition_win",config,"");
	    return ss;
}
/*
 *通用定义累计项 设置
 * fieldSetID 子标集
 * addUpFlag  累计方式 
 *
 */
function generalAddUpDialog(express,fieldSetID,addUpFlag,titleName)
{
		
  		var arguments=new Array(express,fieldSetID,addUpFlag,titleName);     
	    var strurl="/general/query/common/complexCondition.do?br_initAdd=link";
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
		
	    var ss=window.showModalDialog(iframe_url,arguments,"dialogWidth=850px;dialogHeight=500px;resizable=yes;scroll=no;status=no;");  
	    return ss;
}

/***/
function locate_dialog(infor,type,tabid,dataset)
{
   var arguments=new Array(dataset);
   var strurl="/general/muster/find.do?b_query=link`type="+type+"`tabid="+tabid+"`tabid="+infor;
   
    var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
   var return_vo =window.showModelessDialog(iframe_url,arguments,"dialogWidth=550px;dialogHeight=250px");  
   return return_vo;
}
function select_org_emp_dialog(flag,selecttype,dbtype,priv,isfilter,loadtype,selectedValues)
{
    if(dbtype!=1)
	 	dbtype=0;
	 if(priv!=0)
	    priv=1;
     var theurl="/system/logonuser/org_employ_tree.do?flag="+flag+"`selecttype="+selecttype+"`dbtype="+dbtype+
                "`priv="+priv + "`isfilter=" + isfilter+"`loadtype="+loadtype+"`selectedValues="+selectedValues;
     if(window.$URL)
    	 theurl = $URL.encode(theurl);
     var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;         
     var dw=300,dh=400,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
     
     if (isIE6()){
         dw=350,dh=450;
     }
     if(getBrowseVersion()){
	     var return_vo= window.showModalDialog(iframe_url,1,
             "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:no;status:no");
    	 return return_vo;
     }else{//非ie浏览器 ext弹窗显示  wangb 20190318
		var win = Ext.create('Ext.window.Window',{
			id:'select_org_emp',
			title:'选择机构',
			width:300,
			height:410,
			resizable:'no',
			modal:true,
			autoScoll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+iframe_url+'"></iframe>',
			renderTo:Ext.getBody()
		 });
     }
     
}

function select_org_emp_dialog_busi(flag,selecttype,dbtype,priv,isfilter,loadtype,param,prompt)//tiany 添加prompt参数 用来判断是否提示用户更新或是追加选择的机构1 代表提示
{
	 if(dbtype!=1)
	 	dbtype=0;
	 if(priv!=0)
	    priv=1;
     var theurl="/system/logonuser/org_employ_tree.do?b_querycheckvalue=link`flag="+flag+"`selecttype="+selecttype+"`dbtype="+dbtype+
                "`priv="+priv + "`isfilter=" + isfilter+"`loadtype="+loadtype+"`param="+param+"`prompt="+prompt;
     var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;         
     var return_vo= window.showModalDialog(iframe_url,1, 
        "dialogWidth:300px; dialogHeight:370px;resizable:no;center:yes;scroll:yes;status:no");
	 return return_vo;
}

function select_kpiorg_dialog(flag,selecttype,dbtype,priv,isfilter,loadtype,viewunit,nmodule)
{
	 if(dbtype!=1)
	 	dbtype=0;
	 if(priv!=0)
	    priv=1;
     var theurl="/system/logonuser/org_employ_tree.do?flag="+flag+"`selecttype="+selecttype+"`dbtype="+dbtype+
                "`priv="+priv + "`isfilter=" + isfilter+"`loadtype="+loadtype+"`viewunit="+viewunit+"`nmodule="+nmodule;
     if(window.$URL)
    	 theurl = $URL.encode(theurl);
     var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
     var height = 410;
     if(window.showModalDialog){
         height = 370;
     }
     var config = {
		width:290,
        height:height,
        dialogArguments:1,
        title:'选择机构',
        id:'select_kpiorg_dialog_win'
     }
        if(!window.showModalDialog){
      		window.dialogArguments = 1;
        }
      return modalDialog.showModalDialogs(iframe_url,null,config);
}
function select_org_emp_dialog8(flag,selecttype,dbtype,priv,isfilter,loadtype,checklevel)
{
	 if(dbtype!=1)
	 	dbtype=0;
	 if(priv!=0)
	    priv=1;
     var theurl="/system/logonuser/org_employ_tree.do?flag="+flag+"`selecttype="+selecttype+"`dbtype="+dbtype+
                "`priv="+priv + "`isfilter=" + isfilter+"`loadtype="+loadtype;
     if (checklevel) {
     	theurl = theurl + "`checklevel=" + checklevel;
     }
     var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;         
     var return_vo= window.showModalDialog(iframe_url,1, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
	 return return_vo;
}
function select_org_emp_byname_dialog(flag,selecttype,dbtype,priv,isfilter,loadtype)
{
	 if(dbtype!=1&&dbtype!=2)
	 	dbtype=0;
	 if(priv!=0)
	    priv=1;
     var theurl="/system/logonuser/org_employbyname_tree.do?flag="+flag+"`selecttype="+selecttype+"`dbtype="+dbtype+
                "`priv="+priv + "`isfilter=" + isfilter+"`loadtype="+loadtype;
     if(window.$URL){
    	 theurl = $URL.encode(theurl);
     }
      var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;  
      var winFeature="dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no";
      if (isIE6())
     	 winFeature="dialogWidth:305px; dialogHeight:430px;resizable:no;center:yes;scroll:yes;status:no";
   	  if(getBrowseVersion()){
	      var return_vo= window.showModalDialog(iframe_url,1, winFeature);
		  return return_vo;
   	  }else{
		  var iTop = (window.screen.height-30-400)/2;       //获得窗口的垂直位置;
		  var iLeft = (window.screen.width-10-300)/2;        //获得窗口的水平位置;
		  window.open(iframe_url,'','height=400, width=300,top='+iTop+',left='+iLeft+',toolbar=no,menubar=no,scrollbars=no,resizable=no,location=no,status=no');
   	  }
}
//组织机构树如果显示人员，则先显示人员库
function select_org_emp_dialog2(flag,selecttype,dbtype,priv,isfilter,loadtype)
{
	 if(dbtype!=1)
	 	dbtype=0;
	 if(priv!=0)
	    priv=1;
     var theurl="/system/logonuser/org_employ_tree.do?flag="+flag+"`showDb=1`selecttype="+selecttype+"`dbtype="+dbtype+
                "`priv="+priv + "`isfilter=" + isfilter+"`loadtype="+loadtype;
      var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);  
      var dw=300,dh=400,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2; 
      if (checkBrowser().indexOf("MSIE|6") != -1) {
      	dw="320";
      	dh="440";
       }  
      var return_vo= window.showModalDialog(iframe_url,1, 
    		 "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
	 return return_vo;
}


function select_org_emp_dialog3(flag,selecttype,dbtype,priv,showSelfNode,isfilter,loadtype)
{
	 if(dbtype!=1)
	 	dbtype=0;
	 if(priv!=0)
	    priv=1;
     var theurl="/system/logonuser/org_employ_tree.do?flag="+flag+"`showSelfNode="+showSelfNode+"`selecttype="+selecttype+"`dbtype="+dbtype+
                "`priv="+priv + "`isfilter=" + isfilter+"`loadtype="+loadtype;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;        
                
     var return_vo= window.showModalDialog(iframe_url,1, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
	 return return_vo;
}

function select_org_emp_dialog5(flag,selecttype,dbtype,priv,showSelfNode,isfilter,loadtype,showDb)
{
	 if(dbtype!=1)
	 	dbtype=0;
	 if(priv!=0)
	    priv=1;
     var theurl="/system/logonuser/org_employ_tree.do?flag="+flag+"`showSelfNode="+showSelfNode+"`showDb="+showDb+"`selecttype="+selecttype+"`dbtype="+dbtype+
                "`priv="+priv + "`isfilter=" + isfilter+"`loadtype="+loadtype;
     var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;     
  	var dialogWidth="300px";
	var dialogHeight="450px";
    if (checkBrowser().indexOf("MSIE|6") != -1) {
    	dialogWidth="320px";
    	dialogHeight="480px";
    } 
     var return_vo= window.showModalDialog(iframe_url,1, 
        "dialogWidth:"+dialogWidth+"; dialogHeight:"+dialogHeight+";resizable:no;center:yes;scroll:yes;status:no");
	 return return_vo;
}

//组织机构树如果显示人员，则先显示人员库
function select_org_emp_dialog4(flag,selecttype,dbtype,priv,isfilter,loadtype,chitemid,orgcode,dbpre,showroot){
	 if(dbtype!=1)
	 	dbtype=0;
	 if(priv!=0)
	    priv=1;
     var theurl="/system/logonuser/org_tree.do?flag="+flag+"`showDb=1`selecttype="+selecttype+"`dbtype="+dbtype+
                "`priv="+priv + "`isfilter=" + isfilter+"`loadtype="+loadtype
                +"`chitemid="+chitemid+"`orgcode="+orgcode+"`dbpre="+dbpre+"`showroot="+showroot;
                
         var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;   
                     
     var return_vo= window.showModalDialog(iframe_url,1, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
	 return return_vo;
}

//组织机构树如果显示人员，则先显示人员库,当节点显示checkbox时，支持级联选中反选子节点checkbox
function select_org_emp_dialog6(flag,selecttype,dbtype,priv,isfilter,loadtype,cascade,checklevel)
{
	 if(dbtype!=1)
	 	dbtype=0;
	 if(priv!=0)
	    priv=1;
	 
     var theurl="/system/logonuser/org_employ_tree.do?flag="+flag+"`showDb=1`selecttype="+selecttype+"`dbtype="+dbtype+"`viewunit=0"+
                "`priv="+priv + "`isfilter=" + isfilter+"`loadtype="+loadtype+"`cascade="+cascade+"`checklevel="+checklevel;
      
      var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;  
      var dw=300,dh=400,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
      var return_vo= window.showModalDialog(iframe_url,1, 
              "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
      return return_vo;
}

function select_org_dialog(flag,selecttype,dbtype,priv,isfilter,loadtype)
{
	 if(dbtype!=1)
	 	dbtype=0;
	 if(priv!=0)
	    priv=1;
     var theurl="/system/logonuser/org_tree.do?flag="+flag+"`selecttype="+selecttype+"`dbtype="+dbtype+
                "`priv="+priv + "`isfilter=" + isfilter+"`loadtype="+loadtype;
     if(window.$URL)
    	 theurl = $URL.encode(theurl);
     var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
     var dw=310,dh=420,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;//xuj update 2011-5-11 兼容firefox、chrome
     if (checkBrowser().indexOf("MSIE|6") != -1) {
     	dw=300;
     	dh=440;
     }
     if(getBrowseVersion()){//IE
         var return_vo= window.showModalDialog(iframe_url,1,
             "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+(dw+10)+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:no;status:no");
         return return_vo;
     }else{
         //兼容非IE浏览器  wangbs 20190315
         Ext.create("Ext.window.Window",{
			id:"select_org_dialog1_win",
			title:'选择机构',
			width:dw+20,
			height:dh+40,
			resizable:false,
			modal:true,
			autoScroll:false,
			autoShow:true,
			autoDestroy:true,
			renderTo:Ext.getBody(),
			html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='100%' width='100%' src='"+iframe_url+"'></iframe>"
		});
	 }
}
function select_org_dialog1(flag,selecttype,dbtype,priv,isfilter,loadtype,nmodule)
{
	 if(dbtype!=1)
	 	dbtype=0;
	 if(priv!=0)
	    priv=1;
     var theurl="/system/logonuser/org_tree.do?flag="+flag+"`selecttype="+selecttype+"`dbtype="+dbtype+
                "`priv="+priv + "`isfilter=" + isfilter+"`loadtype="+loadtype+"`nmodule="+nmodule;
     
     if(window.$URL)
    	 theurl = $URL.encode(theurl);
     var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
     if(window.showModalDialog){
 		var chart= window.showModalDialog(iframe_url, 1, "dialogWidth:310px; dialogHeight:400px;resizable:no;center:yes;scroll:no;status:no");
 		return chart;
 	} else {
 	    function openWin(){
 		    Ext.create("Ext.window.Window",{
 		    	id:'select_org_dialog1_win',
 		    	width:320,
 		    	height:430,
 		    	title:'选择机构',
 		    	resizable:false,
 		    	modal:true,
 		    	autoScroll:true,
 		    	renderTo:Ext.getBody(),
 		    	html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='100%' width='100%' src='"+iframe_url+"'></iframe>"
 		    }).show();
 		}

 		if(typeof window.Ext == 'undefined'){
 			insertFile("/ext/ext6/resources/ext-theme.css","css", function(){
 				insertFile("/ext/ext6/ext-all.js","js" ,openWin);
 			});

 		} else {
      		window.dialogArguments = 1;
 			openWin();
 		}
 	}
}
function select_org_dialog2(flag,selecttype,dbtype,priv,isfilter,loadtype,nmodule,viewunit)
{
	 if(dbtype!=1)
	 	dbtype=0;
	 if(priv!=0)
	    priv=1;
     var theurl="/system/logonuser/org_tree.do?flag="+flag+"`selecttype="+selecttype+"`dbtype="+dbtype+
                "`priv="+priv + "`isfilter=" + isfilter+"`loadtype="+loadtype+"`nmodule="+nmodule+"`viewunit="+viewunit;      
     var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
     var dw=310,dh=400,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;//xuj update 2011-5-11 兼容firefox、chrome 
     var return_vo= window.showModalDialog(iframe_url,1, 
        "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
	 return return_vo;
}
function select_user_dialog()
{
     var theurl="/system/logonuser/select_user_obj.do?b_query=link`isShowFullName=1";
     var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
 	 var dialogWidth="300px";
	 var dialogHeight="400px";
     if (checkBrowser().indexOf("MSIE|6") != -1) {
    	dialogWidth="350px";
    	dialogHeight="460px";
     } 
     var return_vo= window.showModalDialog(iframe_url,1, 
        "dialogWidth:"+dialogWidth+"; dialogHeight:"+dialogHeight+";resizable:no;center:yes;scroll:yes;status:no");
	 return return_vo;
}
function select_user_dialog(priv)
{
     var theurl="/system/logonuser/select_user_obj.do?b_query=link`isShowFullName=1`priv=" + priv;
     var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
 	 var dialogWidth="300px";
	 var dialogHeight="400px";
     if (checkBrowser().indexOf("MSIE|6") != -1) {
    	dialogWidth="350px";
    	dialogHeight="460px";
     } 
     var return_vo= window.showModalDialog(iframe_url,1, 
        "dialogWidth:"+dialogWidth+"; dialogHeight:"+dialogHeight+";resizable:no;center:yes;scroll:yes;status:no");
	 return return_vo;
}

function select_user_dialog(priv,treeselecttype)///**用户树显示类型(=0(default),=1(checkbox),=2(radio))*/
{
     var theurl="/system/logonuser/select_user_obj.do?b_query=link`isShowFullName=1`priv=" + priv+"`treeselecttype="+treeselecttype;
     var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
 	 var dialogWidth="300px";
	 var dialogHeight="400px";
     if (checkBrowser().indexOf("MSIE|6") != -1) {
    	dialogWidth="320px";
    	dialogHeight="460px";
     } 
     var return_vo= window.showModalDialog(iframe_url,1, 
        "dialogWidth:"+dialogWidth+"; dialogHeight:"+dialogHeight+";resizable:no;center:yes;scroll:yes;status:no");
	 return return_vo;
}


/** treeselecttype 用户树显示类型(=0(default),=1(checkbox),=2(radio))
    isself         是否显示自己   1:显示  0:不显示	
    isShowFullName   树节点是否显示全名 0：不显示  1：显示	
*/
function select_user_dialog(priv,treeselecttype,isself)
{
     var theurl="/system/logonuser/select_user_obj.do?b_query=link`isShowFullName=1`priv=" + priv+"`treeselecttype="+treeselecttype+"`isself="+isself;
     var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
 	 var dialogWidth="300px";
	 var dialogHeight="400px";
     if (checkBrowser().indexOf("MSIE|6") != -1) {
    	dialogWidth="350px";
    	dialogHeight="460px";
     } 
     var return_vo= window.showModalDialog(iframe_url,1, 
        "dialogWidth:"+dialogWidth+"; dialogHeight:"+dialogHeight+";resizable:no;center:yes;scroll:yes;status:no");
	 return return_vo;
}

/** treeselecttype 用户树显示类型(=0(default),=1(checkbox),=2(radio))
    isself         是否显示自己   1:显示  0:不显示	
    isShowFullName   树节点是否显示全名 0：不显示  1：显示	
    salaryid       薪资类别id
    展现有该薪资类别权限的用户目录树
*/
function select_user_dialog(priv,treeselecttype,isself,salaryid)
{
     var theurl="/system/logonuser/select_user_obj.do?b_query=link`isShowFullName=1`salaryid="+salaryid+"`priv=" + priv+"`treeselecttype="+treeselecttype+"`isself="+isself;
     if(window.$URL)
    	 theurl = $URL.encode(theurl);
     var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
 	 var dialogWidth="300px";
	 var dialogHeight="400px";
     if (checkBrowser().indexOf("MSIE|6") != -1) {
    	dialogWidth="350px";
    	dialogHeight="460px";
     } 
     var return_vo= window.showModalDialog(iframe_url,1, 
        "dialogWidth:"+dialogWidth+"; dialogHeight:"+dialogHeight+";resizable:no;center:yes;scroll:yes;status:no");
	 return return_vo;
}
function select_user_dialog1(priv,treeselecttype,uc)
{
     var theurl="/system/logonuser/select_user_obj.do?b_query=link`isShowFullName=1`priv=" + priv+"`treeselecttype="+treeselecttype+"`report=1`uc="+uc;
     var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
     var return_vo= window.showModalDialog(iframe_url,1, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
	 return return_vo;
}


function select_role_dialog(selecttype)
{
     var theurl="/general/template/select_role_dialog.do?b_query=link";
     var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
     var dw=520,dh=440,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
     if (checkBrowser().indexOf("MSIE|6") != -1) {
     	dw=560;
     	dh=480;
     } 
     var return_vo= window.showModalDialog(iframe_url,1, 
             "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
	 return return_vo;
}

/*
 *代码型指标选项控件（多选）
 */
function select_codeTree_dialog(codesetid)
{
	
     var theurl="/system/logonuser/org_code_tree.jsp?codesetid="+codesetid;
     var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
     var dw=300,dh=400,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
     var return_vo= window.showModalDialog(iframe_url,1, 
    		 "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
	 return return_vo;
}





/*
 * 单位部门和职位级联
 *codeid,相关代码类
 *mytarget,选中的代码值需填充的Element,for examples input text
 */
function openInputCodeDialogOrg(codeid,mytarget,managerstr,flag) 
{
   
    var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
    if(mytarget==null)
      return;
    var oldInputs=document.getElementsByName(mytarget);
    oldobj=oldInputs[0];
    //根据代码显示的对象名称查找代码值名称	
    target_name=oldobj.name;
    hidden_name=target_name.replace(".viewvalue",".value");
    hidden_name=hidden_name.replace(".hzvalue",".value");
        
    var hiddenInputs=document.getElementsByName(hidden_name);
	
    if(hiddenInputs!=null)
    {
    	hiddenobj=hiddenInputs[0];
    	codevalue=managerstr;
    }
    
    var theArr=new Array(codeid,codevalue,oldobj,hiddenobj,flag); 
    thecodeurl="/system/codeselectpos.jsp?codesetid="+codeid+"&codeitemid="; 
    var popwin= window.showModelessDialog(thecodeurl, theArr, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
}
/*
 * 单位部门和职位级联
 *codeid,相关代码类
 *mytarget,选中的代码值需填充的Element,for examples input text
 */
function openInputCodeDialogOrgInputPos(codeid,mytarget,managerstr,flag) 
{
	 if(isNotPC()){
		 gPopupMask = document.getElementById("popupMask");
		 gPopupContainer = document.getElementById("popupContainer");
		 gPopFrame = document.getElementById("popupFrame");	
		 loadcssfile("/js/subModal-1.6/style.css" ,"css");
		 loadcssfile("/js/subModal-1.6/subModal.css" ,"css");
		insertJS("/js/subModal-1.6/common.js","js" ,function(){
			insertJS("/js/subModal-1.6/subModal.js","js",function(){
					 gPopupIsShown = false;
					 gDefaultPage = "/js/subModal-1.6/loading.html";
					 gHideSelects = false;
					 gTabIndexes = new Array();
					 gTabbableTags = new Array("A","BUTTON","TEXTAREA","INPUT","IFRAME");	
					if (!document.all) {
						document.onkeypress = keyDownHandler;
					}
					if(null==gPopupContainer&&null==gPopupMask&&null==gPopFrame)
					initPopUp();
					thecodeurl="/system/newcodeselectposinputpos.jsp?codesetid="+codeid+"&codeitemid="+managerstr+"&isfirstnode=" + flag+"&isAccord=1&mytarget="+mytarget;
					showPopWin(thecodeurl, 400, 400, returnRefresh);//tianye update 兼容ipad产品
				});
		});
		return; 
	 }
	 
    var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
    if(mytarget==null)
      return;
    var oldInputs=document.getElementsByName(mytarget);
    oldobj=oldInputs[0];
    //根据代码显示的对象名称查找代码值名称	
    target_name=oldobj.name;
    hidden_name=target_name.replace(".viewvalue",".value"); 
    hidden_name=hidden_name.replace(".hzvalue",".value");
    hidden_name=hidden_name.replace("name1","namevalue");   
    var hiddenInputs=document.getElementsByName(hidden_name);
    if(hiddenInputs!=null&&hiddenInputs.length>0)
    {
    	hiddenobj=hiddenInputs[0];
    	codevalue=managerstr;
    }else{
    	hiddenobj=document.getElementById(hidden_name);
    	codevalue=managerstr;
    }
     var theArr=new Array(codeid,codevalue,oldobj,hiddenobj,flag); 
    thecodeurl="/system/codeselectposinputpos.jsp?codesetid="+codeid+"&codeitemid=&isfirstnode=" + flag; //xuj update 2011-5-11 兼容firefox、chrome
    var dw=300,dh=400,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
 	var dialogWidth="300px";
	var dialogHeight="400px";
    if (checkBrowser().indexOf("MSIE|6") != -1) {
    	dialogWidth="320px";
    	dialogHeight="440px";
    } 
    if(window.showModalDialog){
	    var popwin= window.showModalDialog(thecodeurl, theArr, 
    	 "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dialogWidth+"; dialogHeight:"+dialogHeight+";resizable:no;center:yes;scroll:yes;status:no");
    }else if(Ext){//ext 弹窗  wangb 20190320
		var win = Ext.create('Ext.window.Window',{
			id:'select_code',
			title:'请选择',
			width:dw+20,
			height:dh+20,
			resizable:'no',
			modal:true,
			autoScoll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+thecodeurl+'"></iframe>',
			renderTo:Ext.getBody()
		 }); 
		 win.dialogArguments = theArr;
    } else if(parent.Ext){
    	var win = parent.Ext.create('Ext.window.Window',{
    		id:'select_code',
    		title:'请选择',
    		width:dw+20,
    		height:dh+20,
    		resizable:'no',
    		modal:true,
    		autoScoll:false,
    		autoShow:true,
    		autoDestroy:true,
    		html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+thecodeurl+'"></iframe>',
    		renderTo:parent.Ext.getBody()
    	}); 
    	win.dialogArguments = theArr;
	}


}
/*
 * 单位部门和职位级联
 *codeid,相关代码类
 *mytarget,选中的代码值需填充的Element,for examples input text
 */
function openInputCodeDialogOrgInputPosForBatchUpdate(codeid,mytarget,managerstr,flag,isAccord) 
{	 
    var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
    if(mytarget==null)
      return;
    var oldInputs=document.getElementsByName(mytarget);
    oldobj=oldInputs[0];
    //根据代码显示的对象名称查找代码值名称	
    target_name=oldobj.name;
    hidden_name=target_name.replace(".viewvalue",".value"); 
    hidden_name=hidden_name.replace(".hzvalue",".value");
    hidden_name=hidden_name.replace("name1","namevalue");   
    var hiddenInputs=document.getElementsByName(hidden_name);
    if(hiddenInputs!=null&&hiddenInputs.length>0)
    {
    	hiddenobj=hiddenInputs[0];
    	codevalue=managerstr;
    }else{
    	hiddenobj=document.getElementById(hidden_name);
    	codevalue=managerstr;
    }
     var theArr=new Array(codeid,codevalue,oldobj,hiddenobj,flag,isAccord); 
     thecodeurl="/system/codeselectposinputpos.jsp?codesetid="+codeid+"&codeitemid=&isfirstnode=" + flag; //xuj update 2011-5-11 兼容firefox、chrome
     var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl; 
     var dialogWidth=300;
	 var dialogHeight=400;
     if(!window.showModalDialog){
     	dialogWidth=315;
     	dialogHeight=425;
  		window.dialogArguments = theArr;
    }
    var config = {
		width:dialogWidth,
        height:dialogHeight,
        dialogArguments:theArr,
        title:'选择机构',
        id:'modaldialogid'
    }
  	modalDialog.showModalDialogs(iframe_url,null,config);
    if(document.getElementById("E01A1")!=null||document.getElementById("B0110")!=null||document.getElementById("E01A1")!=null){
    	if(codeid=="UN"&&ba_up_viewB0110.value!=""){
    		document.getElementById("edit"+ba_up_indexB0110).checked="checked";
    		if(ba_up_viewE0122!=null){
    			ba_up_viewE0122.value="";
    			document.getElementById("edit"+ba_up_indexE0122).checked="";
    			ba_up_e0122.value="";
    			
    		}
    		if(ba_up_viewE01a1!=null){
    			ba_up_viewE01a1.value = "";
    			ba_up_e01a1.value="";
    			document.getElementById("edit"+ba_up_indexE01a1).checked="";
    			
    		}
    	}else if(codeid =="UM"&&ba_up_viewE0122.value!=""){
    		
    		var hashvo=new ParameterSet();
    		hashvo.setValue("codeitemid", ba_up_e0122.value);
    		hashvo.setValue("codeid", codeid);
    		var request=new Request({asynchronous:false,onSuccess:isSuccess1,functionId:'0570040054'},hashvo);
    		function isSuccess1(outparam){
    			
    			if(ba_up_viewB0110!=null){
    				ba_up_viewB0110.value = outparam.getValue("b0110desc");
    				ba_up_b0110.value = outparam.getValue("b0110");
    				document.getElementById("edit"+ba_up_indexB0110).checked="checked";
    				
    			}
    			document.getElementById("edit"+ba_up_indexE0122).checked="checked";
    			
    			if(ba_up_viewE01a1!=null){
    				ba_up_e01a1.value="";
    				ba_up_viewE01a1.value = "";
    				document.getElementById("edit"+ba_up_indexE01a1).checked="";
    				
    			}
    			
    			
    		}
    		
    		
    	}else if(codeid =="@K"&&ba_up_viewE01a1.value!=""){
    		var hashvo=new ParameterSet();
    		hashvo.setValue("codeitemid", ba_up_e01a1.value);
    		hashvo.setValue("codeid", codeid);
    		var request=new Request({asynchronous:false,onSuccess:isSuccess2,functionId:'0570040054'},hashvo);
    		function isSuccess2(outparam){
    			if(outparam.getValue("codeid")=="@K"){
    				
    				if(ba_up_viewB0110!=null){
    					ba_up_viewB0110.value = outparam.getValue("b0110desc");
    					ba_up_b0110.value = outparam.getValue("b0110");
    					document.getElementById("edit"+ba_up_indexB0110).checked="checked";
    					
    				}
    				if(ba_up_viewE0122!=null){
    					ba_up_viewE0122.value = outparam.getValue("e0122desc");
    					ba_up_e0122.value = outparam.getValue("e0122");
    					document.getElementById("edit"+ba_up_indexE0122).checked="checked";
    					
    				}
    				
    				document.getElementById("edit"+ba_up_indexE01a1).checked="checked";
    			}
    			
    		}
    	}
    	
    }

}
function returnRefresh(returnVal) {
	 // window.document.reload();
	}
function isNotPC(){
	var sUserAgent = navigator.userAgent.toLowerCase(); 
    var IsIpad = sUserAgent.match(/ipad/i) == "ipad";   
    var IsIphoneOs = sUserAgent.match(/iphone os/i) == "iphone os"; 
    var IsMidp = sUserAgent.match(/midp/i) == "midp"; 
    var IsUc7 = sUserAgent.match(/rv:1.2.3.4/i) == "rv:1.2.3.4"; 
    var IsUc = sUserAgent.match(/ucweb/i) == "ucweb"; 
    var IsAndroid = sUserAgent.match(/android/i) == "android"; 
    var IsCE = sUserAgent.match(/windows ce/i) == "windows ce"; 
    var IsWM = sUserAgent.match(/windows mobile/i) == "windows mobile";
	if(IsIpad || IsIphoneOs||IsMidp||IsUc7||IsUc||IsUc||IsAndroid||IsCE||IsWM) {
		return true;
 	} else {
		return false;
	}
}
//tianye add 动态加载某css样式
function loadcssfile(filename,filetype){

   if(filetype == "css" ){
        var fileref = document.createElement('link');
        fileref.setAttribute("rel","stylesheet");
        fileref.setAttribute("type","text/css");
        fileref.setAttribute("href",filename);
    }
   if(typeof fileref != "undefined"&&null==gPopupContainer&&null==gPopupMask&&null==gPopFrame){
        document.getElementsByTagName("head")[0].appendChild(fileref);
    }
}
//tianye add 动态加载某js文件方法
function insertJS(src,filetype, callback){
		  var script;
		  script = document.createElement("SCRIPT"), done = false;
		 if ( filetype=="js"){ //判断文件类型 
		 script.type = "text/javascript"; 
		 script.src = src; 
		 script.charset = "GB2312";
		 //if(null==gPopupContainer&&null==gPopupMask&&null==gPopFrame)
		 document.getElementsByTagName("head")[0].appendChild(script);
		 } 
	 
		 script.onload = script.onreadystatechange =
			 function(){ 
			   if ( !done && (!this.readyState || this.readyState == "loaded" || this.readyState == "complete") ){  
				    done = true;  
				    callback();
			     } 
			   
		     }; 
	}
// chent add 动态加载js或css 20180103
function insertFile(src, filetype, callback) {
	var tag, done = false;

	if (filetype == "js") { // 判断文件类型
		tag = document.createElement("SCRIPT");
		tag.type = "text/javascript";
		tag.src = src;
	} else if (filetype == "css") {
		tag = document.createElement("link");
		tag.type = "text/css";
		tag.rel = "stylesheet";
		tag.href = src;
	}
	tag.charset = "GB2312";
	document.getElementsByTagName("head")[0].appendChild(tag);
	
	tag.onload = tag.onreadystatechange = function() {
		if (!done && (!this.readyState || this.readyState == "loaded" || this.readyState == "complete")) {
			done = true;
			callback();
		}

	};
}
function openInputCodeDialogOrgInputPos00(codeid,mytarget,managerstr,flag) 
{
    var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
    if(mytarget==null)
      return;
    var oldInputs=document.getElementsByName(mytarget);
    oldobj=oldInputs[0];
    //根据代码显示的对象名称查找代码值名称	
    target_name=oldobj.name;
    hidden_name=target_name.replace(".viewvalue",".value"); 
    hidden_name=hidden_name.replace(".hzvalue",".value");
    hidden_name=hidden_name.replace("name1","namevalue");   
    var hiddenInputs=document.getElementsByName(hidden_name);
    if(hiddenInputs!=null&&hiddenInputs.length>0)
    {
    	hiddenobj=hiddenInputs[0];
    	codevalue=managerstr;
    }else{
    	hiddenobj=document.getElementById(hidden_name);
    	codevalue=managerstr;
    }
    var theArr=new Array(codeid,codevalue,oldobj,hiddenobj,flag); 
    thecodeurl="/system/untrain.jsp?codesetid="+codeid+"&codeitemid=&isfirstnode=" + flag; //xuj update 2011-5-11 兼容firefox、chrome
    var dw=300,dh=400,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
    var popwin= window.showModalDialog(thecodeurl, theArr, 
        "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
}

function openInputCodeDialogOrgInputPosQuestion(codeid,mytarget,mytargetvalue,managerstr,flag) 
{
    var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
    if(mytarget==null)
      return;
    var oldInputs=document.getElementsByName(mytarget);
    oldobj=oldInputs[0];
    //根据代码显示的对象名称查找代码值名称	
    target_name=oldobj.name;
    hidden_name=target_name.replace(".viewvalue",".value"); 
    hidden_name=hidden_name.replace(".hzvalue",".value");
    hidden_name=hidden_name.replace("name1","namevalue");   
    var hiddenInputs=document.getElementsByName(mytargetvalue);
    if(hiddenInputs!=null&&hiddenInputs.length>0)
    {
    	hiddenobj=hiddenInputs[0];
    	codevalue=managerstr;
    }else{
    	hiddenobj=document.getElementById(mytargetvalue);
    	codevalue=managerstr;
    }
    var theArr=new Array(codeid,codevalue,oldobj,hiddenobj,flag,0); 
    thecodeurl="/system/codeselectposinputpos.jsp?codesetid="+codeid+"&codeitemid=&isfirstnode=" + flag; //xuj update 2011-5-11 兼容firefox、chrome
    var dw=300,dh=400,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
    var popwin= window.showModalDialog(thecodeurl, theArr, 
        "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
}
/**绩效模块专用 显示用户权限范围内的机构图*/
function openInputOrgCodeDialogOrg(codeid,mytarget,managerstr,flag) 
{
    var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
    if(mytarget==null)
      return;
    var oldInputs=document.getElementsByName(mytarget);
    oldobj=oldInputs[0];
    //根据代码显示的对象名称查找代码值名称	
    target_name=oldobj.name;
    hidden_name=target_name.replace(".viewvalue",".value"); 
    hidden_name=hidden_name.replace(".hzvalue",".value");
    hidden_name=hidden_name.replace("name1","namevalue");   
    var hiddenInputs=document.getElementsByName(hidden_name);
    if(hiddenInputs!=null)
    {
    	hiddenobj=hiddenInputs[0];
    	codevalue=managerstr;
    }
    var theArr=new Array(codeid,codevalue,oldobj,hiddenobj,flag); 
    thecodeurl="/performance/kh_plan/codeselectposinputpos.jsp?codesetid="+codeid+"`codeitemid=`isfirstnode=" + flag; 
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
    var dialogWidth="300";
    var dialogHeight="440";
     if(!window.showModalDialog){
     	dialogWidth='315';
         dialogHeight="460";
  		window.dialogArguments = theArr;
    }

    var config = {
		width:dialogWidth,
        height:dialogHeight,
        dialogArguments:theArr,
        title:'选择机构',
        id:'openInputOrgCodeDialogOrgWin'
    }
   
  	modalDialog.showModalDialogs(iframe_url,null,config);
}
function openInputCodeDialogOrgInputPos4(codeid,mytarget,managerstr,flag) 
{
    var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
    if(mytarget==null)
      return;
    var oldInputs=document.getElementsByName(mytarget);
    oldobj=oldInputs[0];
    //根据代码显示的对象名称查找代码值名称	
    target_name=oldobj.name;
    hidden_name=target_name.replace("_viewvalue","_value"); 
    var hiddenInputs=document.getElementsByName(hidden_name);
    if(hiddenInputs!=null)
    {
    	hiddenobj=hiddenInputs[0];
    	codevalue=managerstr;
    }
    var theArr=new Array(codeid,codevalue,oldobj,hiddenobj,flag); 
    thecodeurl="/system/codeselectposinputpos.jsp?codesetid="+codeid+"&codeitemid=&isfirstnode=" + flag; 
    var popwin= window.showModelessDialog(thecodeurl, theArr, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
}
function openInputCodeDialogOrgInputPos3(codeid,mytarget,managerstr,flag) 
{
    var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
    if(mytarget==null)
      return;
    var oldInputs=document.getElementById(mytarget);
    oldobj=oldInputs;
    //根据代码显示的对象名称查找代码值名称	
    var hiddenInputs=document.getElementById(mytarget+"value");
    if(hiddenInputs!=null)
    {
    	hiddenobj=hiddenInputs;
    	codevalue=managerstr;
    }
    var theArr=new Array(codeid,codevalue,oldobj,hiddenobj,flag); 
    thecodeurl="/system/codeselectposinputpos.jsp?codesetid="+codeid+"`codeitemid=`isfirstnode=" + flag; 
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl); 
    var dialogWidth="320";
	var dialogHeight="420";
    var config = {
		width:dialogWidth,
        height:dialogHeight,
        dialogArguments:theArr,
        title:'请选择',
        id:'modaldialogid'
    }
    if(!window.showModalDialog){
  		window.dialogArguments = theArr;
    }
    modalDialog.showModalDialogs(iframe_url,null,config)
}

function openInputCodeDialogOrgInputPos1(codeid,mytarget,managerstr,flag) 
{
    var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
    if(mytarget==null)
      return;
    var oldInputs=document.getElementsByName(mytarget);
    oldobj=oldInputs[0];
    //根据代码显示的对象名称查找代码值名称	
    target_name=oldobj.name;
    hidden_name=target_name.replace(".viewvalue",".value"); 
    hidden_name=hidden_name.replace(".hzvalue",".value");
       
    var hiddenInputs=document.getElementsByName(hidden_name);
    
    if(hiddenInputs!=null)
    {
    	hiddenobj=hiddenInputs[0];
    	codevalue=managerstr;
    }
    var theArr=new Array(codeid,codevalue,oldobj,hiddenobj,flag); 
    thecodeurl="/system/codeselectposinputpos1.jsp?codesetid="+codeid+"&codeitemid=&isfirstnode=" + flag; 
    var popwin= window.showModalDialog(thecodeurl, theArr, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
}

function openInputCodeDialogOrgInputPos2(codeid,mytarget,managerstr,flag) 
{
    var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
    if(mytarget==null)
      return;
    var oldInputs=document.getElementsByName(mytarget);
    oldobj=oldInputs[0];
    //根据代码显示的对象名称查找代码值名称	
    target_name=oldobj.name;
    hidden_name=target_name.replace(".viewvalue",".value"); 
    hidden_name=hidden_name.replace(".hzvalue",".value");
       
    var hiddenInputs=document.getElementsByName(hidden_name);
    
    if(hiddenInputs!=null)
    {
    	hiddenobj=hiddenInputs[0];
    	codevalue=managerstr;
    }
    var theArr=new Array(codeid,codevalue,oldobj,hiddenobj,flag); 
    thecodeurl="/system/codeselectposinputpos2.jsp?codesetid="+codeid+"&codeitemid=&isfirstnode=" + flag; 
    var popwin= window.showModalDialog(thecodeurl, theArr, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
}
/*
 * 单位部门和职位级联
 *codeid,相关代码类
 *mytarget,选中的代码值需填充的Element,for examples input text
 */
function openInputCodeDialogOrg_handwork(codeid,mytarget,managerstr,flag) 
{   
    var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
    if(mytarget==null)
      return;
    var oldInputs=document.getElementsByName(mytarget);
    oldobj=oldInputs[0];
    //根据代码显示的对象名称查找代码值名称	
    target_name=oldobj.name;
    hidden_name=target_name.replace(".viewvalue",".value");
    hidden_name=hidden_name.replace(".hzvalue",".value");
        
    var hiddenInputs=document.getElementsByName(hidden_name);
    var codevalue_name="";
    var rootvalue="";
    if(hiddenInputs!=null)
    {
    	hiddenobj=hiddenInputs[0];
    	if(managerstr.indexOf("~")!=-1)
    	{// 管理范围格式: 1 机构编码('`'分隔)~根节点名称
    	 //              2 机构编码('`'分隔)~根节点名称~根节点编号
    		var temps=managerstr.split("~");
    		codevalue=temps[0];
    		codevalue_name=temps[1];
    		if(temps.length=3)
    			rootvalue=temps[2];
    		else
    			rootvalue=temps[0];
    	}
    	else {
	    	codevalue=managerstr;
	    	rootvalue=managerstr;
	    }
    }
    var theArr=new Array(codeid,codevalue,oldobj,hiddenobj,flag); 
    thecodeurl="/system/codeselectpos_handwork.jsp?codesetid="+codeid+"&codeitemid="; 
    if(codevalue_name.length>0)
    	thecodeurl+="&codevalueName="+codevalue_name;
    if(rootvalue.length>0)
    	thecodeurl+="&codevalue="+rootvalue;
    var width = 300;
    var height = 400;
    if(isIE6()){
    	width += 10;
    	height +=10;
    }
    var popwin= window.showModelessDialog(thecodeurl, theArr, 
        "dialogWidth:"+width+"px; dialogHeight:"+height+"px;resizable:no;center:yes;scroll:yes;status:no");
}


function openInputCodeDialogOrg_1(codeid,mytarget,mytarget_hidden,managerstr) 
{
    var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
    if(mytarget==null)
      return;
    var oldInputs=document.getElementsByName(mytarget);
    oldobj=oldInputs[0];
    //根据代码显示的对象名称查找代码值名称	
    target_name=oldobj.name;
    var hiddenInputs=document.getElementsByName(mytarget_hidden);
    if(hiddenInputs!=null)
    {
    	hiddenobj=hiddenInputs[0];
    	codevalue=managerstr;
    }
    var theArr=new Array(codeid,codevalue,oldobj,hiddenobj); 
    thecodeurl="/system/codeselectpos.jsp?codesetid="+codeid+"&codeitemid="+codevalue; 
    var popwin= window.showModelessDialog(thecodeurl, theArr, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
}
/*
 *codeid,相关代码类
 *mytarget,选中的代码值需填充的Element,for examples input text
 */
function openCodeDialog(codeid,mytarget) 
{
    var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
    if(mytarget==null)
      return;
    //根据代码显示的对象名称查找代码值名称	
    target_name=mytarget.name;
    if(!target_name)
    	target_name = mytarget;
    hidden_name=target_name.replace(".viewvalue",".value");
    hidden_name=hidden_name.replace(".hzvalue",".value");
    var hiddenInputs=document.getElementsByName(hidden_name);
    if(hiddenInputs!=null)
    {
    	hiddenobj=hiddenInputs[0];
    	codevalue="";
    }
    
    //var theArr=new Array(codeid,codevalue,mytarget,hiddenobj); 
    var theArr=new Array(codeid,codevalue,document.getElementById(target_name),hiddenobj); //修改传入值，不对   wangb 20190323
    thecodeurl="/system/codeselect.jsp?codesetid="+codeid+"&codeitemid=ALL"; 
    //var popwin= window.showModelessDialog(thecodeurl, theArr, 
      //  "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
    //改用ext 弹窗显示  wangb 20190318
    var win = Ext.create('Ext.window.Window',{
			id:'openCodeDialog',
			title:'请选择',
			width:320,
			height:410,
			resizable:'no',
			modal:true,
			autoScoll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+thecodeurl+'"></iframe>',
			renderTo:Ext.getBody()
		 });  
      win.theArr = theArr;
}

function openCodeCustomReportDialog(codeid,mytarget,features) 
{
    var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
    if(mytarget==null)
      return;
    //根据代码显示的对象名称查找代码值名称	
    target_name=document.getElementsByName(mytarget)[0].name;
    hidden_name=target_name.replace("report","");
    var hiddenInputs=document.getElementsByName(hidden_name);
    if(hiddenInputs!=null)
    {
    	hiddenobj=hiddenInputs[0];
    	codevalue="";
    }
    var obj = document.getElementsByName(mytarget)[0];
    var theArr=new Array(codeid,codevalue,obj,hiddenobj,features);
    thecodeurl="/system/codeselectforreport.jsp?codesetid="+codeid+"`codeitemid=ALL"; 
	 var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
	 
    var popwin= window.showModalDialog(iframe_url, theArr, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
      
}
function openCodeCustomReportCascadetree(codeid,mytarget,priv,checkmodel,level) 
{
    var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
    if(mytarget==null)
      return;
    //根据代码显示的对象名称查找代码值名称	
    target_name=document.getElementsByName(mytarget)[0].name;
    hidden_name=target_name.replace("report","");
    var hiddenInputs=document.getElementsByName(hidden_name);
    if(hiddenInputs!=null)
    {
    	hiddenobj=hiddenInputs[0];
    	codevalue="";
    }
    var obj = document.getElementsByName(mytarget)[0];
    var theArr=new Array(codeid,codevalue,obj,hiddenobj,priv,checkmodel,level);
    thecodeurl="/system/options/customreport/cascadetree/codeselectforreport.jsp?codesetid="+codeid+"`codeitemid=ALL`priv="+priv; 
     var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
     if (checkBrowser().indexOf("MSIE|6") != -1) {
    var popwin= window.showModalDialog(iframe_url, theArr, 
        "dialogWidth:350px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
        } else {
        var popwin= window.showModalDialog(iframe_url, theArr, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
        	
        }
      
}

//  检测浏览器及版本,返回Opera|7.54、MSIE|7.0、SAFARI|3.2、Konqueror|2.0、Mozilla|2.3、Netscape|4.1、Other其中的一个
 function checkBrowser() {
	var userAgent = navigator.userAgent;
	var browserVersion = parseFloat(navigator.appVersion);
	var browserName = navigator.appName;
	if (userAgent.indexOf("Opera") != -1) {//Opera浏览器
		if (navigator.appName == "Opera") {
			return "Opera|" + navigator.appVersion;
		} else {
			var reg = new RegExp("Opera (\\d+\\.\\d+)");
			reg.test(userAgent);
			return "Opera|" + RegExp["$1"];
		}
	} else if (userAgent.indexOf("compatible") != -1 && userAgent.indexOf("MSIE") != -1) {
		var reg = new RegExp("MSIE (\\d+\\.\\d+)");
		reg.test(userAgent);
		return "MSIE|" + RegExp["$1"];
	} else if (userAgent.indexOf("KHTML") != -1 || userAgent.indexOf("Konqueror") != -1 
				|| userAgent.indexOf("AppleWebKit") != -1) {
		if (userAgent.indexOf("AppleWebKit") != -1) {
			var reg = new RegExp("AppleWebKit\\/(\\d+(\\.\\d*)?)");
			reg.test(userAgent);
			return "SAFARI|" + RegExp["$1"];
		}
		if (userAgent.indexOf("Konqueror") != -1) {
			var reg = new RegExp("Konqueror\\/(\\d+(\\.\\d+(\\.\\d+)?)?)");
			reg.test(userAgent);
			return "Konqueror|" + RegExp["$1"];
		}
	} else if (userAgent.indexOf("Gecko") != -1) {
		var reg = new RegExp("rv:(\\d+\\.\\d(\\.\\d+)?)");
		reg.test(userAgent);
		return "Mozilla|" + RegExp["$1"];
	} else if (userAgent.indexOf("Mozilla") == 0 && browserName == "Netscape" 
			&& browserVersion >= 4.0 && browserVersion < 5.0) {
		return "Netscape|" + browserVersion;
	} else {
		return "Other";
	}
}

/**********************************************
*codeid:相关代码类
*mytarget:可以是对象，也可以是对名称
***********************************************/
function openCondCodeDialog(codeid,mytarget) 
{	
    var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
    if(mytarget==null)
      return;
    //根据代码显示的对象名称查找代码值名称
    if(typeof mytarget!="object")
    {
      var oldInputs=document.getElementsByName(mytarget);
      mytarget=oldInputs[0];    	
    }
    target_name=mytarget.name;
    if(!target_name)
    	target_name=mytarget;
    hidden_name=target_name.replace(".hzvalue",".value");
    var hiddenInputs=document.getElementsByName(hidden_name);
    if(hiddenInputs!=null&&hiddenInputs.length>0)
    {
    	hiddenobj=hiddenInputs[0];
    	codevalue="";
    }else{
    	hiddenobj=document.getElementById(hidden_name);
    	codevalue="";
    }
    var theArr=new Array(codeid,codevalue,mytarget,hiddenobj); 
    thecodeurl="/system/codeselect.jsp?codesetid="+codeid+"`codeitemid=ALL"; //xuj update 2011-5-11 兼容firefox、chrome
    if(window.$URL)
		thecodeurl = $URL.encode(thecodeurl);
    var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
    var dialogWidth="300";
    if(!window.showModalDialog){
    	dialogWidth='315';
  		window.dialogArguments = theArr;
    }
	var dialogHeight="430";
    var config = {
		width:dialogWidth,
        height:dialogHeight,
        dialogArguments:theArr,
        title:'选择代码',
        id:'openCondCodeDialogWin'
    }
   
    modalDialog.showModalDialogs(iframe_url,null,config)
}
//加上管理范围筛选
function openCondCodeDialogsx(codeid,mytarget) 
{	
    var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
    if(mytarget==null)
      return;
    //根据代码显示的对象名称查找代码值名称
    if(typeof mytarget!="object")
    {
      var oldInputs=document.getElementsByName(mytarget);
      mytarget=oldInputs[0];    	
    }
    target_name=mytarget.name;
    hidden_name=target_name.replace(".hzvalue",".value");
    var hiddenInputs=document.getElementsByName(hidden_name);
    if(hiddenInputs!=null)
    {
    	hiddenobj=hiddenInputs[0];
    	codevalue="";
    }
    var theArr=new Array(codeid,codevalue,mytarget,hiddenobj); 
    thecodeurl="/system/codeselectfilter.jsp?codesetid="+codeid+"&codeitemid=ALL"; 
 	var dialogWidth="300px";
	var dialogHeight="400px";
    if (checkBrowser().indexOf("MSIE|6") != -1) {
    	dialogWidth="320px";
    	dialogHeight="440px";
    }     
    var popwin= window.showModelessDialog(thecodeurl, theArr, 
        "dialogWidth:"+dialogWidth+"; dialogHeight:"+dialogHeight+";resizable:no;center:yes;scroll:yes;status:no");
}
/*****************************
*代码字段内容修改时，相应修改对应的hidden字段的内容
*
******************************/
function fieldcode(sourceobj,flag)
{
var targetobj,target_name,hidden_name,hiddenobj;
    target_name=sourceobj.name;
    if(flag==1)
      hidden_name=target_name.replace(".hzvalue",".value");
    else
      hidden_name=target_name.replace(".viewvalue",".value");       	
    var hiddenInputs=document.getElementsByName(hidden_name);    
    if(hiddenInputs!=null)
    {
    	hiddenobj=hiddenInputs[0];    	
    	codevalue="";
    }   
    hiddenobj.value=sourceobj.value;
}
function clearvalue(sourceobj,flag)
{
	sourceobj.value='';
    var targetobj,target_name,hidden_name,hiddenobj;
    target_name=sourceobj.name;
    if(flag==1)
      hidden_name=target_name.replace(".hzvalue",".value");
    else
      hidden_name=target_name.replace(".viewvalue",".value");       	
    var hiddenInputs=document.getElementsByName(hidden_name);    
    if(hiddenInputs!=null)
    {
    	hiddenobj=hiddenInputs[0];  
    	hiddenobj.value=sourceobj.value;  	
    }
}


/*
 * 单位部门和职位级联
 *codeid,相关代码类
 *mytarget,选中的代码值需填充的Element,for examples input text
 */

function hireZpOption(codeid,mytarget,managerstr,flag) {
    var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
    if(mytarget==null)
      return;
    var oldInputs=document.getElementsByName(mytarget);
    oldobj=oldInputs[0];
    //根据代码显示的对象名称查找代码值名称	
    target_name=oldobj.name;
    hidden_name=target_name+"id"; 
       
    var hiddenInputs=document.getElementsByName(hidden_name);
    
    if(hiddenInputs!=null)
    {
    	hiddenobj=hiddenInputs[0];
    	codevalue=managerstr;
    }
    
    var theArr=new Array(codeid,codevalue,oldobj,hiddenobj,flag); 
    thecodeurl="/system/hireselectpos.jsp?codesetid="+codeid+"&codeitemid=&isfirstnode=" + flag; 
    var popwin= window.showModelessDialog(thecodeurl, theArr, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
}
function projectZpOption(fielditem,formula) {
    var thecodeurl,target_name,hidden_name,hiddenobj,formulaobj;

	var stat_methods = document.getElementById("stat_methods").value;
    if(fielditem==null||stat_methods==0)
      return;
    var oldInputs=document.getElementsByName(fielditem);
    oldobj=oldInputs[0];
    //根据代码显示的对象名称查找代码值名称	
    target_name=oldobj.name;
    hidden_name=target_name+"id"; 
       
    var hiddenInputs=document.getElementsByName(hidden_name);
    
    if(hiddenInputs!=null){
    	hiddenobj=hiddenInputs[0];
    }
	
	var hiddentextarea = document.getElementsByName(formula);
	if(hiddentextarea!=null){
    	formulaobj=hiddentextarea[0];
    }

    var theArr=new Array(oldobj,hiddenobj,formulaobj); 
    thecodeurl="/org/autostatic/mainp/projectselectpos.jsp"; 
    var popwin= window.showModelessDialog(thecodeurl,theArr, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
}
function gzZpOption(fielditem,formula) {
    var thecodeurl,target_name,hidden_name,hiddenobj,formulaobj;

    var oldInputs=document.getElementsByName(fielditem);
    oldobj=oldInputs[0];
    //根据代码显示的对象名称查找代码值名称	
    target_name=oldobj.name;
    hidden_name=target_name+"id"; 
       
    var hiddenInputs=document.getElementsByName(hidden_name);
    
    if(hiddenInputs!=null){
    	hiddenobj=hiddenInputs[0];
    }
	
	var hiddentextarea = document.getElementsByName(formula);
	if(hiddentextarea!=null){
    	formulaobj=hiddentextarea[0];
    }

    var theArr=new Array(oldobj,hiddenobj,formulaobj); 
    thecodeurl="/org/autostatic/mainp/projectselectpos.jsp"; 
    var popwin= window.showModelessDialog(thecodeurl,theArr, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
}



/*
 * 单位部门和职位级联
 *codeid,相关代码类
 *mytarget,选中的代码值需填充的Element,for examples input text
 */

function totalZpOption(codeid,mytarget,managerstr,flag) 
{
    var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
    if(mytarget==null)
      return;
    var oldInputs=document.getElementsByName(mytarget);
    oldobj=oldInputs[0];
    //根据代码显示的对象名称查找代码值名称	
    target_name=oldobj.name;
    hidden_name=target_name+"id"; 
       
    var hiddenInputs=document.getElementsByName(hidden_name);
    
    if(hiddenInputs!=null)
    {
    	hiddenobj=hiddenInputs[0];
    	codevalue=managerstr;
    }
    
    var theArr=new Array(codeid,codevalue,oldobj,hiddenobj,flag); 
    thecodeurl="/system/totalselectpos.jsp?codesetid="+codeid+"&codeitemid=&isfirstnode=" + flag; 
    var popwin= window.showModelessDialog(thecodeurl, theArr, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
}


/***************************************************
 *打开无模式对话框
 *theArr:从源页面传过来的参数
 *target_url:目标网页
  ***************************************************/
function openDialog(theArr,target_url) 
{
    var popwin = window.showModalDialog(target_url, theArr, 
        "dialogWidth:600px; dialogHeight:400px;resizable:no;center:yes;scroll:no;status:no");
}
/******************************
 *两上select上的对象的源-->目标
 *
 ******************************/
function additemfield(sourcebox_id,targetbox_id)
{
  var left_vo,right_vo,vos,i;
  vos= document.getElementsByName(sourcebox_id);

  if(vos==null)
  	return false;
  left_vo=vos[0];
  vos= document.getElementsByName(targetbox_id);  
  if(vos==null)
  	return false;
  right_vo=vos[0];
  for(i=0,j=0;i<left_vo.options.length;i++)
  {
    if(left_vo.options[i].selected)
    {
    	j++;
    	var no = new Option();
    	no.value=left_vo.options[i].value;
    	no.text=left_vo.options[i].text;
    	right_vo.options[right_vo.options.length]=no;
    }
  }
  
  //设为要可选状态
   for(i=0;i<right_vo.options.length-j;i++)
  {
     right_vo.options[i].selected=false;
  }
  for(i=right_vo.options.length-j;i<right_vo.options.length;i++)
  {
     right_vo.options[i].selected=true;
  }
  
 return true;	  	
}

/***************************************
 *指标顺序上调
 ***************************************/
function upItem(selectbox)
{
  if(selectbox==null)
     return;
   var idx=getSelectedIndex(selectbox);
   if(idx==-1)
     return;
   if(idx==0)
     return;
   var currvalue=selectbox.options[idx].value;
   var currtext=selectbox.options[idx].text;
   selectbox.options[idx].value=selectbox.options[idx-1].value;
   selectbox.options[idx].text=selectbox.options[idx-1].text;
   selectbox.options[idx-1].value=currvalue;
   selectbox.options[idx-1].text=currtext;
   selectbox.options[idx].selected=false;
   selectbox.options[idx-1].selected=true;	
}

function getSelectedIndex(selectbox)
{
   var idx=-1;
   for(i=0;i<selectbox.options.length;i++)
   {  
    if(selectbox.options[i].selected)
    {
       idx=i;
       break;
    }   	
   }
   return idx;
}
/**
*许硕 多个指标顺序上调
*16/09/27
**/
function upItems(selectbox)
{
   if(selectbox==null)
     return;
   var idx=getSelectedIndexs(selectbox);
   if(idx==null||idx=="")
     return;
   if(idx[0]==0)
     return;
   for(var i=0;i<idx.length;i++){  
	   var currvalue=selectbox.options[idx[i]].value;
	   var currtext=selectbox.options[idx[i]].text;
	   selectbox.options[idx[i]].value=selectbox.options[idx[i]-1].value;
	   selectbox.options[idx[i]].text=selectbox.options[idx[i]-1].text;
	   selectbox.options[idx[i]-1].value=currvalue;
	   selectbox.options[idx[i]-1].text=currtext;
	   selectbox.options[idx[i]].selected=false;
	   selectbox.options[idx[i]-1].selected=true;
   }	
}
/**
*许硕 获取多个选中项
*16/09/27
**/
function getSelectedIndexs(selectbox)
{
   var idx=[];
   var index=0;
   for(i=0;i<selectbox.options.length;i++)
   {  
    if(selectbox.options[i].selected)
    {
       idx[index++]=i;
    }   	
   }
   return idx;
}
/***************************************
 *指标顺序下调
 ***************************************/
function downItem(selectbox)
{
   if(selectbox==null)
     return;
   var idx=getSelectedIndex(selectbox);
   if(idx==-1)
     return;
   if(idx==selectbox.options.length-1)
     return;
   var currvalue=selectbox.options[idx].value;
   var currtext=selectbox.options[idx].text;
   selectbox.options[idx].value=selectbox.options[idx+1].value;
   selectbox.options[idx].text=selectbox.options[idx+1].text;
   selectbox.options[idx+1].value=currvalue;
   selectbox.options[idx+1].text=currtext;
   selectbox.options[idx].selected=false;
   selectbox.options[idx+1].selected=true;
}
/**
*许硕 多个指标顺序下调
*16/09/27
**/
function downItems(selectbox)
{
   if(selectbox==null)
     return;
   var idx=getSelectedIndexs(selectbox);
   if(idx==null||idx=="")
     return;
   var i=(idx.length-1);
   if(idx[i]==selectbox.options.length-1)
     return;
   for(i;i>=0;i--){
	   var currvalue=selectbox.options[idx[i]].value;
	   var currtext=selectbox.options[idx[i]].text;
	   selectbox.options[idx[i]].value=selectbox.options[idx[i]+1].value;
	   selectbox.options[idx[i]].text=selectbox.options[idx[i]+1].text;
	   selectbox.options[idx[i]+1].value=currvalue;
	   selectbox.options[idx[i]+1].text=currtext;
	   selectbox.options[idx[i]].selected=false;
	   selectbox.options[idx[i]+1].selected=true;
   }
}

/******************************
 *两上select上的对象的源-->目标
 *
 ******************************/
function additem(sourcebox_id,targetbox_id)
{
  var left_vo,right_vo,vos,i;
  vos= document.getElementsByName(sourcebox_id);

  if(vos==null)
  	return false;
  left_vo=vos[0];
  vos= document.getElementsByName(targetbox_id);  
  if(vos==null)
  	return false;
  right_vo=vos[0];
  var flag = false;
  for(i=0;i<left_vo.options.length;i++)
  {
    if(left_vo.options[i].selected)
    {
        var no = new Option();
    	no.value=left_vo.options[i].value;
    	no.text=left_vo.options[i].text;
    	right_vo.options[right_vo.options.length]=no;
    	flag=true;
    }
  }
  
  //设为要可选状态
  /*
  for(i=0;i<right_vo.options.length;i++)
  {
     right_vo.options[i].selected=true;
  }
 	*/
 return flag;	  	
}

  function additem2(sourcebox_id,targetbox_id)
  {
	  var left_vo,right_vo,vos,i;
	  vos= document.getElementsByName(sourcebox_id);
	
	  if(vos==null)
	  	return false;
	  left_vo=vos[0];
	  vos= document.getElementsByName(targetbox_id);  
	  if(vos==null)
	  	return false;
	  right_vo=vos[0];
	  for(i=0;i<left_vo.options.length;i++)
	  {
	    if(left_vo.options[i].selected)
	    {
	    	var isExist=0;
	    	for(var j=0;j<right_vo.options.length;j++)
	    	{
	    		if(right_vo.options[j].value==left_vo.options[i].value)
	    			isExist=1;
	    	}
	    	if(isExist==0)
	    	{
		        var no = new Option();
		    	no.value=left_vo.options[i].value;
		    	no.text=left_vo.options[i].text;
		    	right_vo.options[right_vo.options.length]=no;
		    }
	    }
	  }
   	}


  function batch_select(obj,name)
  {
  	if(obj.checked)
  	  setCheckState(1,name);
  	else
  	  setCheckState(2,name);
  }
  
  function setCheckState(flag,name)
  {
      var chklist,objname,i,typeanme;
      chklist=document.getElementsByTagName('INPUT');
      if(!chklist)
        return;
	  for(i=0;i<chklist.length;i++)
	  {
	     typeanme=chklist[i].type.toLowerCase();
	     if(typeanme!="checkbox")
	        continue;	  
	     if(chklist[i].disabled)
	     	continue;
	     objname=chklist[i].name;
	     if(!objname.match(name))
	        continue;
	     if(flag=="1")
  	       chklist[i].checked=true;
  	     else  
  	       chklist[i].checked=false;
	  }   
  }  
  function batch_select_all(obj)
  {
  	if(obj.checked)
  	  setCheckState_all(1);
  	else
  	  setCheckState_all(2);
  }
  
  function setCheckState_all(flag)
  {
      var chklist,objname,i,typeanme;
      chklist=document.getElementsByTagName('INPUT');
      if(!chklist)
        return;
	  for(i=0;i<chklist.length;i++)
	  {
	     typeanme=chklist[i].type.toLowerCase();
	     if(typeanme!="checkbox")
	        continue;	       
	     if(flag=="1")
  	       chklist[i].checked=true;
  	     else  
  	       chklist[i].checked=false;
	  }   
  }  
function removeitem(sourcebox_id)
{
  var vos,right_vo,i;
  var isCorrect = false;
  vos= document.getElementsByName(sourcebox_id);
  if(vos==null)
  	return false;
  right_vo=vos[0];
  for(i=right_vo.options.length-1;i>=0;i--)
  {
    if(right_vo.options[i].selected)
    {
    	right_vo.options.remove(i);
    	isCorrect = true;
    }
  }
  if(!isCorrect)
  {
  	//liuy 修改花名册选择指标提示信息  2014-8-1 begin
	 alert("请选择需要操作的对象！");
	 //liuy 修改花名册选择指标提示信息  2014-8-1 end
	 return false;
  }
  //设为要可选状态
  /*
  for(i=0;i<right_vo.options.length;i++)
  {
	right_vo.options[i].selected=true;
  }  
  */  
  return true;	  	
}
function removeleftitem(sourcebox_id)
{
  var vos,right_vo,i;
  var isCorrect = false;
  vos= document.getElementsByName(sourcebox_id);
  if(vos==null)
  	return false;
  right_vo=vos[0];
  for(i=right_vo.options.length-1;i>=0;i--)
  {
    if(right_vo.options[i].selected)
    {
    	right_vo.options.remove(i);
    	isCorrect = true;
    }
  }
  if(!isCorrect)
  {
	 alert("请选择需要添加的对象！");
	 return false;
  }
  return true;	  	
}
/**********************************
*设置为选中状态
***********************************/
function setselectitem(listbox)
{
    if(document.getElementsByName('left_fields')[0])
    {
        left_vo=document.getElementsByName('left_fields')[0];
        for(i=0;i<left_vo.options.length;i++)
        {
          if(left_vo.options[i].selected)
              left_vo.options[i].selected=false;
        }
    }
  var vos,right_vo,i;
  vos=document.getElementsByName(listbox);
  if(vos==null || vos[0].length==0)
      return;    
  
  //设为要可选状态
  right_vo=vos[0];  
  for(i=0;i<right_vo.options.length;i++)
  {
	right_vo.options[i].selected=true;
  }     	
}
function unsetselectitem(listbox)
{
  var vos,right_vo,i;
  vos= document.getElementsByName(listbox);
  if(vos==null)
  	return;
  //设为要可选状态
  right_vo=vos[0];  
  for(i=0;i<right_vo.options.length;i++)
  {
	right_vo.options[i].selected=false;
  }     	
}
/************************************************
*操作Cookie函数
*************************************************/
function setCookie(name,value)
{
  var Days = 30; //此 cookie将被保存 30 天
  var exp  = new Date();    //new Date("December 31, 9998");
  exp.setTime(exp.getTime() + Days*24*60*60*1000);
  document.cookie = name + "="+ escape(value) +";expires="+ exp.toGMTString();
}
function setDaysCookie(name,value,days)
{
	var exp = new Date();
	exp.setTime(exp.getTime()+days*24*60*60*1000);
	document.cookie=name+"="+escape(value)+";expires="+exp.toGMTString()+";path=/";
}
function getCookie(name)
{

  var arr = document.cookie.match(new RegExp("(^| )"+name+"=([^;]*)(;|$)"));
  if(arr != null) 
     return unescape(arr[2]); 
  return null;
}

function delCookie(name)
{
  var exp = new Date();
  exp.setTime(exp.getTime() - 1);
  var cval=getCookie(name);
  if(cval!=null) 
    document.cookie=name +"="+cval+";expires="+exp.toGMTString();
}

/*
 *报表参数
 *state,  0为考勤 1为其他报表 
 *tid:对应的数据表主见
 */
function oageoptions_selete(state,tid)
{
    tid=getEncodeStr(tid);
	var thecodeurl="/general/print/page_options.do?b_edit=link`state="+state+"`id="+tid; 
	if(window.$URL)
		thecodeurl = $URL.encode(thecodeurl);
	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
	var iWidth=800; //弹出窗口的宽度;
	var iHeight=450; //弹出窗口的高度;
	var iTop = (window.screen.availHeight-30-iHeight)/2; //获得窗口的垂直位置;
	var iLeft = (window.screen.availWidth-10-iWidth)/2; //获得窗口的水平位置;
	var win = window.open(iframe_url,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,height='+iHeight+', width='+iWidth+', top='+iTop+', left='+iLeft);
	win.focus();
}
//用于鼠标触发的某一行
var curObjTr= null;
var oldObjTr_c= "";
function tr_onclick(objTr,bgcolor)
{
	/*
	if(curObjTr!=null)
		curObjTr.style.background=oldObjTr_c;
	curObjTr=objTr;
	oldObjTr_c=bgcolor;
	curObjTr.style.background='FFF8D2';
	* */
	if(curObjTr!=null)
		curObjTr.style.backgroundColor="";
	curObjTr=objTr;
	oldObjTr_c="FFF8D2";
	curObjTr.style.backgroundColor='#FFF8D2';
	//curObj.style.color='#ffdead'; 
}
//行色跟随鼠标滑动改变颜色
function changTRColor(objTr,changeColor)
{
	if(curObjTr!=null)
		curObjTr.style.background="";
	curObjTr=objTr;
	oldObjTr_c="FFF8D2";
	curObjTr.style.background='#FFF8D2';
}
//jfreechart设置
function jfreechartSet(chartTitle,controlNames){
	var chartseturl="/general/deci/browser/chartset/chartset.do?br_chartset=link"; 		
	var obj = chartTitle+"#" + controlNames+"# ";
	 var iframe_url="/general/query/common/iframe_query.jsp?src="+chartseturl;
	
	var chart= window.showModalDialog(iframe_url, obj, "dialogWidth:450px; dialogHeight:330px;resizable:no;center:yes;scroll:yes;status:no");      	
	return chart; 
}

//jfreechart设置
function jfreechartSet(chartTitle,controlNames,chartSets){
	var chartseturl="/general/deci/browser/chartset/chartset.do?br_chartset=link"; 		
	var obj = chartTitle+"#" + controlNames+"#"+chartSets;
	 var iframe_url="/general/query/common/iframe_query.jsp?src="+chartseturl;
	
	var chart= window.showModalDialog(iframe_url, obj, "dialogWidth:450px; dialogHeight:330px;resizable:no;center:yes;scroll:yes;status:no");      	
	return chart; 
}

//jfreechart设置(针对 绩效分析dengcan)
function jfreechartSet(chartTitle,controlNames,chartSets,opt){
	var chartseturl="/general/deci/browser/chartset/chartset.do?br_chartset=link`opt="+opt; 		
	var obj = chartTitle+"#" + controlNames+"#"+chartSets
	var iframe_url="/general/query/common/iframe_query.jsp?src="+chartseturl;
	
	var chart= window.showModalDialog(iframe_url, obj, "dialogWidth:450px; dialogHeight:330px;resizable:no;center:yes;scroll:yes;status:no");      	
	return chart; 
}

//jfreechart设置(针对 绩效分析dengcan   可以设置图片大小 和 分值序列)
function jfreechartSet2(chartTitle,controlNames,chartSets,opt,length_width){
	var chartseturl="/general/deci/browser/chartset/chartset.do?br_chartset=link`opt="+opt+"`chartTitle="+chartTitle+"`controlNames="+controlNames+"`chartSets="+chartSets+"`length_width="+length_width; 		
	var obj = chartTitle+"#" + controlNames+"#"+chartSets+"#"+length_width;
	if(window.$URL)
		chartseturl = $URL.encode(chartseturl);
	var iframe_url="/general/query/common/iframe_query.jsp?src="+chartseturl;
	
	// 多浏览器兼容showModalDialog改为Ext.window形式 chent 20171226 add
    if(/msie/i.test(navigator.userAgent)){
		var chart= window.showModalDialog(iframe_url, obj, "dialogWidth:450px; dialogHeight:330px;resizable:no;center:yes;scroll:yes;status:no");     
		return chart; 
	} else {
	    function openWin(){
		    Ext.create("Ext.window.Window",{
		    	id:'chart_win',
		    	width:450,
		    	height:330,
		    	title:'配置选项',
		    	resizable:false,
		    	modal:true,
		    	autoScroll:true,
		    	border:false,
		    	renderTo:Ext.getBody(),
		    	html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='100%' width='100%' src='"+iframe_url+"'></iframe>"
		    }).show();	
		}
		
		if(typeof window.Ext == 'undefined'){
			insertFile("/ext/ext6/resources/ext-theme.css","css", function(){
				insertFile("/ext/ext6/ext-all.js","js" ,openWin);
			});
			
		} else {
			openWin();
		}
	}
}
function chartWinClose(){
	if(Ext){
		Ext.getCmp('chart_win').close();
	}
}

function jfreechartSet2_ok(info){
	set_ok(info);
}

//对考勤错误页面的处理
function kqErrorProcess(error_flag,error_message,error_return)
{
	var str = "";
        str += "<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" align=center>"
	str += "<tr>"
	str += "<td width=300 height='30' align=center><br><br><br>"
	str += "信息提示："+error_message
	str += "</td>"
	str += "</tr>"
	str += "<td align=center height='50'>"
	str += "<input type='button' name='return' value='返回' onclick=\"kqErrorReturn('"+error_flag+"','"+error_return+"');\" class='mybutton'>"
	str += "</td></tr></table>"
	return str;
}
function kqErrorReturn(error_flag,error_return)
{
	if(error_return==null||error_return.length<=0)
	{
		alert("错误报告连接错误!");
		return false;
	}
	if(error_flag=="4")
	{
	   newwindow=window.open(error_return,'i_body'); 
	}else if(error_flag=="3")
	{
	   newwindow=window.open(error_return,'il_body'); 
	}else if(error_flag=="2")
	{
	   newwindow=window.open(error_return,'mil_body'); 
	}else{
	  history.back();	
	}
}


//干部任免表WORD输出
function cadrermtoword(userName ,dbPre ,cadreIds){
	window.open("/servlet/CadreRMServlet?username="+userName+"&dbpre="+dbPre+"&cadreids="+cadreIds);
}

//干部任免表ZIP输出
function cadrermtozip(userName ,dbPre ,cadreIds){

	window.open("/servlet/CadreRMDownloadServlet?username="+userName+"&dbpre="+dbPre+"&cadreids="+cadreIds);
}


/*
 *指标选择
 *label  
 *labelValue (一般以hidden存在)
 *flag 指标集标识 (A B K ,ALL)
 *type 指标类型( A字符型 D日期型 N数值型 M备注型 (格式'D,N') 或 NC 非代码类型 或AC代码类型  ) 
 */
function selectFieldItems(label,labelValue,flag,type){
 	var url="/general/deci/definition/add_definition.do?b_select=link`object="+flag+"`type="+type+"`party="; 
 	if(window.$URL)
 		url = $URL.encode(url);
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+url;		
	var parameter = type;
	var obj= window.showModalDialog(iframe_url, parameter, "dialogWidth:300px; dialogHeight:180px;resizable:no;center:yes;scroll:yes;status:no");      	
	if(obj == null){
	}else{
		//alert(obj);
		info = obj.split("/");
		labelValue.value=info[2];
		label.value=info[3];
	}
 }

 /*
 *指标选择
 *flag 指标集标识 (A B K ,ALL)
 *type 指标类型( A字符型 D日期型 N数值型 M备注型 (格式'D,N') 或 NC 非代码类型非日期非代码非浮点数 ) 
 */
function selectFieldItem(flag,type){
 	var url="/general/deci/definition/add_definition.do?b_select=link`object="+flag+"`type="+type+"`party="; 	
 	if(window.$URL)
 		url = $URL.encode(url);
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+url;
 		
	var parameter = type;
	if(getBrowseVersion()){
		var obj= window.showModalDialog(iframe_url, parameter, "dialogWidth:330px; dialogHeight:200px;resizable:no;center:yes;scroll:no;status:no");
		if(obj == null){
			return "";
		}else{
			return obj;
			//info = obj.split("/");
			//labelValue.value=info[0];
			//label.value=info[1];
		}
	}else{//非IE浏览器 用 ext 弹窗 wangb 20190319
		var win = Ext.create('Ext.window.Window',{
			id:'select_item',
			title:'选择指标',
			width:350,
			height:220,
			resizable:'no',
			modal:true,
			autoScoll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+iframe_url+'"></iframe>',
			renderTo:Ext.getBody()
		 });  
		win.parameter = parameter;
	}
 }
 
/***********************权限检索*******************************/
/*
* modeflag 用户标识(自助/业务)1,2
* 自助用户 
* 	dbpre  人员库前缀
* 	name   
* 业务用户
*	dbpre 空
*	name 用户名
*/ 
function userPopedom(modeflag , dbpre, name){
	var iWidth=780; //弹出窗口的宽度;
	var iHeight=600; //弹出窗口的高度;
	var iTop = (window.screen.availHeight-30-iHeight)/2; //获得窗口的垂直位置;
	var iLeft = (window.screen.availWidth-10-iWidth)/2; //获得窗口的水平位置;
		window.open("/system/options/userpopedom.do?b_query=link&operatorflag=1&modeflag="+modeflag+"&dbpre="+dbpre+"&name="+name,
		'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,height='+iHeight+', width='+iWidth+', top='+iTop+', left='+iLeft);
}

/*
*role_id   角色ID
*role_flag 类别( 1 角色 0 用户组 2 单位,部门,职位) 
*/
function orgPopedom(role_id,role_flag){
	var iWidth=780; //弹出窗口的宽度;
	var iHeight=600; //弹出窗口的高度;
	var iTop = (window.screen.availHeight-30-iHeight)/2; //获得窗口的垂直位置;
	var iLeft = (window.screen.availWidth-10-iWidth)/2; //获得窗口的水平位置;
	window.open("/system/options/userpopedom.do?b_query=link&operatorflag=2&role_id="+role_id+"&role_flag="+role_flag,
	'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,height='+iHeight+', width='+iWidth+', top='+iTop+', left='+iLeft);
}
 
 /*
 *
 */
 function select_array_emp_dialog(flag,selecttype,dbtype,priv)
{
	 if(dbtype!=1)
	 	dbtype=0;
	 if(priv!=0)
	    priv=1;
     var theurl="/kq/team/array_group/load_array_emp_data.do?flag="+flag+"`selecttype="+selecttype+"`dbtype="+dbtype+
                "`priv="+priv;
     if(window.$URL)
  		theurl = $URL.encode(theurl);           
     var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
     var return_vo= window.showModalDialog(iframe_url,1, 
        "dialogWidth:310px; dialogHeight:420px;resizable:no;center:yes;scroll:yes;status:no");
	 return return_vo;
}

/*
 *函数向导
 */
function function_Wizard(formula){
    var thecodeurl ="/org/autostatic/mainp/function_Wizard.do?b_query=link"; 
    if(window.$URL)
    	thecodeurl = $URL.encode(thecodeurl);
    var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
    
    var return_vo= window.showModalDialog(iframe_url, "", 
              "dialogWidth:400px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
    if(return_vo!=null){
  	 	if(document.getElementById(formula).pos!=null){
			document.getElementById(formula).pos.text=return_vo;
		}else{
			document.getElementById(formula).value +=return_vo;
		}
  	}else{
  		return ;
  	}
}
/*
 *函数向导
 */
function function_Wizard(formula,param){
    var thecodeurl ="/org/autostatic/mainp/function_Wizard.do?b_query=link`mode="+param; 
    if(window.$URL)
    	thecodeurl = $URL.encode(thecodeurl);
    var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
    
    var return_vo= window.showModalDialog(iframe_url, "", 
              "dialogWidth:400px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
    if(return_vo!=null){
  	 	if(document.getElementById(formula).pos!=null){
			document.getElementById(formula).pos.text=return_vo;
		}else{
			document.getElementById(formula).value +=return_vo;
		}
  	}else{
  		return ;
  	}
}
/**obj中的值+1*/
function inc_year(obj){
	var value=getInt(obj.value);
	value = value+1;
	obj.value = value;
}
/**obj中的值减-1*/
function dec_year(obj){
	    var value=getInt(obj.value);
		value = value-1;
		if(value<=1990)
			value=1990;
		obj.value = value;
}

/**obj中的值+1*/
function inc_month(obj){
	
	var value=obj.value;
	if(value.substring(0,1)=="0")
	   value=value.substring(1,value.length);
   	value=getInt(value);      	   
	value = value+1;
	if(value>12)
	  value=12;
	if(value<10){
		value = "0"+value;
	}
	obj.value = value;
}
/**obj中的值减-1*/
function dec_month(obj){
	    var value=obj.value;
		if(value.substring(0,1)=="0")
	   	   value=value.substring(1,value.length);	
	   	value=getInt(value);       
		value = value-1;
		if(value<=0)
			value=1;
		if(value<10){
			value = "0"+value;
		}	
		obj.value = value;
}
/**取得本地机器ip地址*/
function getLocalIPAddress()
{
    var obj = null;
    var rslt = "";   
    try
    {
        obj = new ActiveXObject("rcbdyctl.Setting");
        rslt = obj.GetIPAddress;        
        obj = null;
    }
    catch(e)
    {
        //异常发生
    }
    return rslt;
}
function  getComputerName()
{ 
    var obj=null;
    var c_name="";
    try
    {
    	obj=new ActiveXObject("WScript.network");
    	c_name=obj.ComputerName;
    	obj=null;
    }catch(E)
    {
    	//
    }   
    return c_name;
}


/**打开职务编码设置，需要传递代码类ID*/
function toCodeitem(codesetid,codeitem)
{
	var iframe_url = "/pos/posbusiness/searchposbusinesstree.do?b_all=link&codesetid="+codesetid+"&codeitem="+codeitem;
	var return_vo= window.open(iframe_url, 'newwindow', 
	"height=600, width=800, top=50,left=100, toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no");
}
/**新增代码类*/
function toAddCodeSet()
{
	var bflag=false;
	var currname=1;
	var return_vo= window.showModalDialog("/system/codemaintence/add_edit_codeset.do?b_query=link&query=query", bflag, 
	"dialogWidth:500px; dialogHeight:300px;resizable:no;center:yes;scroll:yes;status:no");       
	if(return_vo==null)
	return ;
	var codesetvo=new Object();
	codesetvo.codesetid=return_vo.codesetid;
	codesetvo.codesetdesc=return_vo.codesetdesc;
	codesetvo.maxlength=return_vo.maxlength;
	codesetvo.status=return_vo.status;
	var hashvo=new ParameterSet();
	hashvo.setValue("codesetvo",codesetvo);
	hashvo.setValue("flag","0");
	hashvo.setValue("codestname",codesetvo.codesetdesc);
	hashvo.setValue("codesetid",codesetvo.codesetid);
	var request=new Request({asynchronous:false,onSuccess:to_toAddCodeSet_ok,functionId:'1010050008'},hashvo);    
}
function to_toAddCodeSet_ok(outparamters)
{
	var codesetid  = outparamters.getValue("codesetid");
	toCodeitem(codesetid);
}
function checkNumber(obj){
	var evt = window.event||event;
	/**
	*xus 页码不允许输入".","`"(46,96)
	*16/09/28
	**/
	try{
    	if (evt.keyCode<48 || evt.keyCode>57)
    	{
    		evt.returnValue=false;
    		evt.preventDefault();
    	}
	}catch(e){
    alert(e.description);
	}
}
/**get parent's id value*/
function getDeptParentId(value)
{
	var itemid,currid,codeid,idx,lay,lay1,posobj;
	itemid="";
    for(var i=0;i<g_dm.length;i++)
    {
		dmobj=g_dm[i];	
		currid=dmobj.ID.substr(2);
		codeid=dmobj.ID.substr(0,2);
		if(codeid!="UM")
			continue;
		lay=dmobj.L;
		posobj=g_dm["_@K"+value];
		if(typeof(posobj)=="undefined")
			break;
		lay1=posobj.L;
		idx=value.indexOf(currid);
		if(idx==0&&(lay1-lay)==1)
		{
					itemid=currid;
					break;
		}
   } 
   return itemid;
}

function getUpDeptId(value)
{
	var itemid,currid,codeid,idx,lay,lay1,posobj;
	itemid="";
    posobj=g_dm["_"+value];
	if(typeof(posobj)=="undefined")
			return "";
	lay1=posobj.L;

    for(var i=0;i<g_dm.length;i++)
    {
		dmobj=g_dm[i];	
		currid=dmobj.ID.substr(2);
		codeid=dmobj.ID.substr(0,2);
		if(codeid!="UM")
			continue;
		lay=dmobj.L;
		idx=value.indexOf(currid);
		if(idx!=-1&&(lay1-lay)==1)
		{
					itemid=currid;
					break;
		}
   } 
   return itemid;
}

function getUpDeptDesc(value,uplevel)
{
	var itemid,currid,codeid,idx,lay,lay1,posobj,currdesc;
	itemid="";
    posobj=g_dm["_"+value];
	if(typeof(posobj)=="undefined")
		return "";
	lay1=posobj.L;
	currdesc=posobj.V;
	var descarr=new Array(uplevel);
	descarr[0]=currdesc;
    for(var i=0;i<g_dm.length;i++)
    {
		dmobj=g_dm[i];	
		currid=dmobj.ID.substr(2);
		codeid=dmobj.ID.substr(0,2);
		if(codeid!="UM")
			continue;
		lay=dmobj.L;
		idx=value.indexOf(currid);
	
		if(idx!=-1&&((lay1-lay)<uplevel&&(lay1-lay)>0))
		{
				descarr[(lay1-lay)]=dmobj.V;
				//break;
		}
   } 
   descarr=descarr.reverse();
   var ih=0;
   for(var i=0;i<descarr.length;i++)
   {
   	 	if(descarr[i])
   	 	{

   	 		if(ih==0)
   	 		    currdesc=descarr[i];
   	 		else
   	 			currdesc=currdesc+"/"+descarr[i];
   	 		++ih;   	 		
   	 	}
   }
   
   return currdesc;
}

function getUnitParentId(value)
{
	var itemid,currid,codeid,idx,fitemid;
	itemid="";
	fitemid="";
	var un_arr = new Array();
    for(var i=0;i<g_dm.length;i++)
    {
		dmobj=g_dm[i];	
		currid=dmobj.ID.substr(2);
		codeid=dmobj.ID.substr(0,2);
		if(codeid!="UN")
			continue;
		if(value.length>=currid.length)
		{	
		//	idx=value.indexOf(currid);
		//	if(idx!=-1)
			if(value.substr(0,currid.length)==currid)
			{
				un_arr.push(currid);
			}
		}
   } 
   var flen=0;
   for (var i = 0; i < un_arr.length; i++)
   {
		fitemid=un_arr[i];
		idx=fitemid.length;
		if(idx>flen)
		{
			itemid=fitemid;
			flen=idx;
		}
   }
   return itemid;
}
 function getPassword(logintype,bosflag)
    {


    	 var dw=390,dh=200,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;//xuj update 2011-5-11 ????firefox??chrome
    	 var username=document.getElementsByName("username")[0].value;
         var strurl="/gz/gz_analyse/historydata/salary_set_list.do?br_getpassword=get`logintype="+logintype+"`username="+getEncodeStr(username)+"`bosflag="+bosflag;
         if(window.$URL)
        	 strurl = $URL.encode(strurl);
		 var iframe_url="/templates/index/iframe_query.jsp?src="+strurl;

		/*改成ext的window*/
		window.passWindow = Ext.widget('window',{
			title:'找回密码',
			shadow:false,
			html:'<iframe style="height:220px;width:100%;background:white;" frameborder="0" src="'+iframe_url+'"></iframe>',
			height:226,width:396,
			resizable:false
		}).show();
		/*
		 //忘记密码兼容非IE浏览器  wangb 20190524 bug 44221
		 if(getBrowseVersion()){
			 window.showModalDialog(iframe_url,arguments, "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:no;status:no");  
		 }else{
	         window.open(iframe_url,'','height='+dh+'px,width='+ dw + 'px,top='+ dt +'px,left='+ dl +',status=no,toolbar=no,menubar=no,location=no,resizable=no,scrollbars=0,titlebar=no');
		 }
		*/
    }
function getPasswordZP(dbName,userNameCloumn,passWordCloumn)
{
    var strurl="/hire/employNetPortal/search_zp_position.do?br_getpassword=get`dbname="+dbName+"`userC="+userNameCloumn+"`passC="+passWordCloumn;
    if(window.$URL)
   	 strurl = $URL.encode(strurl);
	var iframe_url="/templates/index/iframe_query.jsp?src="+strurl; 
	var flag=window.showModalDialog(iframe_url,arguments,"dialogWidth=390px;dialogHeight=150px;resizable=yes;scroll=no;status=no;");  
}
//字符规则  判断字符是否是 英文、下划线、数字、汉字
String.prototype.regexStr = function () {
	var re = /^([\w|\u4e00-\u9fa5])*$/;
	return re.exec(this);
}
/*
author: dengcan cs调用这个方法会报错,直接写到调用控件的页面就没问题.  人事异动的页面改了 ,登记表的没改,页面太多.
function AxGetCodeDesc(CodeSetId, CodeItemId)
{
    var tmp="_"+CodeSetId+CodeItemId;
    if(!(g_dm[tmp]=="undefined"||g_dm[tmp]==null))
	{
		if(CodeSetId=="UM"&&!(g_dm[tmp].P=="undefined"||g_dm[tmp].P==null||g_dm[tmp].P.length==0))
			value=g_dm[tmp].P;
		else
		    value=g_dm[tmp].V;
	}
	else
	{
		if(CodeSetId=="UN")
		{
			tmp="_UM"+CodeItemId;
			if(!(g_dm[tmp]=="undefined"||g_dm[tmp]==null))
	   			value=g_dm[tmp].V;
	   		else
	   			value="";
		}
		else
			value="";
	}
     return  value;
}
*/
/**取IE版本，如果返回0为其他浏览器*/
function getBrowseVersion()
{
   var  version=0;
   if(navigator.appName.indexOf("Internet Explorer") != -1)
   {
      var temp=navigator.appVersion.split("MSIE");
      version=parseFloat(temp[1]);
   }
    /*IE 10 以后appName是NetScape了，IE内核为Trident，使用此标识判断是否IE guodd 2019-03-01*/
    if(navigator.appVersion.indexOf("Trident") != -1 && navigator.appName.indexOf("Netscape") != -1) {
        version = 10;
    }
   return version;
}
/**取非兼容性ie11版本*/
function getIE11Version(){
    if(navigator.appName.indexOf("Netscape") != -1){
        var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
        var isIE11 = userAgent.indexOf('Trident') > -1 && userAgent.indexOf("rv:11.0") > -1;
        if(isIE11)
            return true;
    }
    return false;
}
/**判断是否兼容模式*/
function isCompatibleIE(){
	var userAgent = window.navigator.userAgent; //取得浏览器的userAgent字符串  
	var isOpera = userAgent.indexOf("Opera") > -1;
	if(userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1 && !isOpera)
		return true;
	else
		return false;
}
//只能在IE兼容模式下的页面中调用此方法校验浏览器是否是IE浏览器，如果不是给出提示信息
function CheckBrowserCompatibility(){
	if(!isCompatibleIE())
		alert(ONLYDISPLAY_IE_COMPATIBLE);
}

//只能使用IE功能提醒, 提示可以显示到element元素（div）
function CheckIE(element){
	// 是IE，不处理
	if(getBrowseVersion()>0)
		return;
	
	//不是IE，加提醒
	var msg = "此页面只能在IE浏览器显示！"
	if(element)
		element.innerHTML = msg;
	else
		alert(msg);
}


/*******　输入的字符是在0-9之间，或者是backspace、DEL键***********/	
  function IsDigit() 
  { 
  
    return (((event.keyCode >47) && (event.keyCode <= 57))|(event.keyCode==8)||(event.keyCode==46)); 
  }
  
  /**
   * 判断文件大小
   * @param target  input file对象  element
   * @param maxsize  文件最大上传大小 M 默认20m
   * @returns {Boolean}
   */
  function checkFileMaxSize(target,maxsize) { 
	  if(!maxsize)
		  maxsize=20;
	  var isIE = /msie/i.test(navigator.userAgent) && !window.opera;    
	  var fileSize = 0;          
	  if (isIE && !target.files) {  
		  try {
	          var filePath = target.value;      
	          var fileSystem = new ActiveXObject("Scripting.FileSystemObject");         
	          var file = fileSystem.GetFile (filePath);      
	          fileSize = file.Size;
		  } catch(e) {
			  fileSize = 10;
		  }
	  } else {     
	   fileSize = target.files[0].size;      
	  } 
	  
	  var size = fileSize / (1024*(maxsize/10));     
	  if(size>10000){   
	     alert("上传文件不能大于"+(maxsize)+"M！");  
	     target.outerHTML=target.outerHTML.replace(/(value=\").+\"/i,"$1\""); 
	     return false;
	  } else {
		  return true;
	  }
	     
	}   
/**
 * 判断当前浏览器是否为ie6
 * 返回boolean 可直接用于判断 
 * @returns {Boolean}
 */
function isIE6() 
{ 
	if(navigator.appName == "Microsoft Internet Explorer") 
	{ 
		if(navigator.userAgent.indexOf("MSIE 6.0")>0) 
		{ 
			return true;
		}else{
			return false;
		}
	}else{
		return false;
	}
}

function validataCodeReload(){
	var url = document.getElementById('vaildataCode').src;
	document.getElementById('vaildataCode').src = url+"&id=" + Math.random(); 
}

function openCodeCustomReportDialog(codeid,targetName,hiddenName,flag) {
	openCodeCustomReportDialog(codeid,targetName,hiddenName,flag,0);
}
function openCodeCustomReportDialog(codeid,targetName,hiddenName,flag,type) {
	var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
	if (!codeid) {
		codeid = "@K";
	}
	if(targetName == null) {
      return;
	}
	
	if (!flag) {
		flag = "1";
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
    var theArr = new Array(codeid,codevalue,obj,hiddenobj,flag,type);
    thecodeurl = "/system/CodeMoreSelect.jsp?codesetid=" + codeid + "`codeitemid=ALL"; 
    if(window.$URL)
    	thecodeurl = $URL.encode(thecodeurl);
	var iframe_url = "/general/query/common/iframe_query.jsp?src=" + thecodeurl;
	//19/3/23 xus 浏览器兼容 历史时点-查询-高级 谷歌不弹窗
	if(!window.showModalDialog){
		var win = parent.Ext.create('Ext.window.Window',{
			id:'history_select_code',
			title:'请选择',
			width:320,
			height:450,
			resizable:'no',
			modal:true,
			autoScoll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+iframe_url+'"></iframe>',
			renderTo:parent.Ext.getBody()
		 }); 
		 win.dialogArguments = theArr;
	}else{
		var popwin = window.showModalDialog(iframe_url, theArr, 
        	"dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
	}
    
}
//执行删除则删除全部多选的代码类
function deleteData (obj,hideId) {
	if(8 == event.keyCode || 46 == event.keyCode) {
		if(document.getElementById(hideId).value) {
			obj.value="";
			document.getElementById(hideId).value = "";
		}
	}
}
//二级按钮，二次密码校验  wangbo 2019-12-07 bug 55918
function validateClick(view, record, item, index, e,obj) {
	var validateType = record.raw.validateType;
	var target_to_use = record.raw.targetToUse;
	var url_to_use = record.raw.urlToUse;
	if("1"==validateType){ 
		check(target_to_use,url_to_use);
		return false;
	}else if("2"==validateType || "1,2"==validateType){
		smscheck(target_to_use,url_to_use,("1,2"==validateType));
		return false;
	}
	return true;
}
//密码验证 
function check(target_to_use,url_to_use){
	var xpoint = document.body.clientWidth/2-100;
	Ext.widget('panel',{
		floating:true,
		id:'passchecker',
		header:{
				xtype:'container',height:37,style:'background:white;color:black;line-height:37px',layout:'hbox',
				items:[{
					xtype:'component',html:'安全验证',style:'float:left;'
				},{
					xtype:'component',height:15,width:15,cls:'safebox-close actionbutton',overCls:'safebox-close-over'
				},{
					xtype:'component',flex:1
				},{
					xtype:'panel',html:'<a href="javascript:void(0);" style="font-size:14px;">关闭</a>',border:0,
					listeners:{
						click:{
							element:'el',
							fn:function(){
								var box = Ext.getCmp('passchecker');
								box.destroy();
							}
						}
					}
				}]
		},
		bodyStyle:'border:1px solid #c5c5c5;border-width:1 0 0 0;',
		width:300,
		height:200,
		modal:true,
		layout:'fit',
		items:{
			xtype:'container',
			padding:'16 0 0 50',
			items:[{
  				xtype:'label',
  				text:'请输入您的密码：'
  			},{
  				xtype:'textfield',margin:'10 0 0 0',width:200,inputType:'password'
  			},{
  				xtype:'box',margin:'10 0 0 0',width:200,
  				html:'提示：请输入您的登录密码，校验认证通过以后，才能进入该模块。'
  			},{xtype:'box',height:20},{
  				xtype:'box',width:46,cls:'actionbutton',
  				style:'border:1px solid #c5c5c5;background:#f1f1f1;cursor:pointer;padding:3px 10px 3px 10px;margin-left:70',
  				html:'确定'
  			}]
		},
		listeners:{
			click:{
				element:'el',
				delegate:'div .actionbutton',
				fn:function(e){
					var box = Ext.getCmp('passchecker');
					if(e.target.className.indexOf('safebox-close')>-1){
						box.destroy();
						return;	
					}
					var password = box.query('textfield')[0].getValue();
					var vo = new HashMap();
					vo.put("password",password);
					vo.put("transType","pwdcheck");
					Rpc({functionId:'0202011021',success:function(response){
						var backparam = Ext.decode(response.responseText);
						if(backparam.result){
							box.destroy();
							if(target_to_use != null && target_to_use != ""){
								window.open(url_to_use,target_to_use);
							}
						}else{
							alert("密码输入不正确！");
						}
					}},vo);
				}
			}
		}
	}).showAt(xpoint,100);
	return false;
}
//短信验证
function smscheck(target_to_use,url_to_use,checkPassword){
	var phone;
	var delaytime;
	var vo = new HashMap();
	vo.put("transType","getPhone");
	Rpc({functionId:'0202011021',async:false,success:function(response){
		var backparam = Ext.decode(response.responseText);
	  	if(backparam.result){
	  		phone = backparam.phone;
	  		delaytime = backparam.delaytime;
	  	}else{
	  		alert(backparam.msg);
	  	}
	}},vo);
	
	if(!phone || phone.length<1)
		return;
	var xpoint = document.body.clientWidth/2-100;
	Ext.widget('panel',{
		floating:true,
		id:'passchecker',
		header:{
				xtype:'container',height:37,style:'background:white;color:black;line-height:37px',
				items:[{
					xtype:'component',html:'安全验证',style:'float:left;'
				},{
					xtype:'component',height:15,width:15,cls:'safebox-close actionbutton',overCls:'safebox-close-over'
				},{
					xtype:'component',flex:1
				},{
					xtype:'panel',html:'<a href="javascript:void(0);" style="font-size:14px;">关闭</a>',border:0,
					listeners:{
						click:{
							element:'el',
							fn:function(){
								var box = Ext.getCmp('passchecker');
								box.destroy();
							}
						}
					}
				}]
		},
		bodyStyle:'border:1px solid #c5c5c5;border-width:1 0 0 0;',
		width:400,
		height:checkPassword?360:300,
		modal:true,
		layout:'fit',
		items:{
			xtype:'container',
			padding:'16 0 0 20',
			items:[{
 				xtype:'label',
 				text:'手机号码: '+phone
 			},{
 				xtype:'container',layout:'hbox',height:50,
 				items:[{
 					xtype:'textfield',itemId:'codebox',margin:'10 0 10 0',fieldLabel:'验证码',labelWidth:56,width:120,labelAlign:'right'
 				},{
 					xtype:'button',width:70,margin:'10 0 0 10',text:'发送验证码',
 					handler:function(btn){
 						//发送验证码
 						var vo = new HashMap();
 						vo.put("phoneNumber",phone);
 						Rpc({functionId:'0202011020',success:function(response){
   	  					var backparam = Ext.decode(response.responseText);
   	  					//发送失败，提示
   	  					if(backparam.error==1)
						{
							alert(backparam.content);
							return;
						}
						
						var codebox = btn.ownerCt.child('#codebox');
						codebox.realcode = backparam.content;
						//发送成功，隐藏发送按钮，显示倒计时
   	  					btn.setText('重新发送');
   	  					btn.setVisible(false);
   	  					var timerBox = btn.ownerCt.child('#timer');
   	  					timerBox.setVisible(true);
   	  					timerBox.update(delaytime+'s');
   	  					timerBox.delaytime = delaytime;
   	  					//开始倒计时
   	  					var forcode = setInterval(function(){
   	  						timerBox.delaytime--;
   	  						timerBox.update(timerBox.delaytime+'s');
   	  						//当倒计时为0时，隐藏倒计时，显示重新发送按钮
   	  						if(timerBox.delaytime==0){
   	  							window.clearInterval(forcode);
   	  							btn.setVisible(true);
   	  							timerBox.setVisible(false);
   	  						}
   	  					},1000);
   	  				}},vo);
 					}
 				},{
 					xtype:'box',margin:'14 0 0 10',hidden:true,itemId:'timer'
 				}]
 			},
 			checkPassword?{xtype:'textfield',inputType:'password',itemId:'pwdbox',margin:'10 0 0 0',fieldLabel:'密码',labelWidth:56,width:200,labelAlign:'right'}:undefined,
 			{
 				xtype:'component',margin:'0 0 0 0',width:360,
 				html:'<table booder="0"><tr><td align="left" Colspan="2" style="font-size:12px;">提示：</td></tr>'+
	           '<tr> <td align="right">&nbsp;&nbsp;&nbsp;&nbsp;</td><td  align="left" style="font-size:12px;">1.请确认上述手机号码是否为接收短信验证码的正确号码。如有误，请不要点击“获取验证码”，并尽快联系您的人力资源主管进行信息更正。</td></tr>'+
	           '<tr><td align="right">&nbsp;&nbsp;&nbsp;&nbsp;</td><td align="left" style="font-size:12px;">2.点击获取验证码后，短信可能由于网路等原因有所延迟，如果您在'+delaytime+'秒内手机没有收到短信验证码，请重新获取。</td></tr>'+
	           (checkPassword?'<tr><td align="right">&nbsp;&nbsp;&nbsp;&nbsp;</td><td align="left" style="font-size:12px;">3.请输入您的登录密码</td></tr>':'')+
                  '<tr><td align="right">&nbsp;&nbsp;&nbsp;&nbsp;</td><td align="left" style="font-size:12px;">'+
                  (checkPassword?'4':'3')+'.验证通过后才能进入该模块。</td></tr>'+
	   		   '</table>'
 			},{
  				xtype:'box',width:46,cls:'actionbutton',
  				style:'border:1px solid #c5c5c5;background:#f1f1f1;cursor:pointer;padding:3px 10px 3px 10px;margin-left:150',
  				html:'确定'
  			}]
		
		},
		listeners:{
			click:{
				element:'el',
				delegate:'div .actionbutton',
				fn:function(e){
					var box = Ext.getCmp('passchecker');
					if(e.target.className.indexOf('safebox-close')>-1){
						box.destroy();
						return;	
					}
					var codefield = box.queryById('codebox');
					var smscode = codefield.getValue();
	  				var realcode = codefield.realcode;
	  				if(smscode!=realcode){
	  					alert('验证码错误');
	  					return;
	  				}
	  				
	  				var pwdfield = box.queryById('pwdbox');
	  				if(pwdfield){
	  					var vo = new HashMap();
	  					vo.put("password",pwdfield.getValue());
	  					vo.put("transType","pwdcheck");
	  					Rpc({functionId:'0202011021',success:function(response){
    	  					var backparam = Ext.decode(response.responseText);
    	  					if(backparam.result){
    	  						box.destroy();
    	  						if(target_to_use != null && target_to_use != "")
    								window.open(url_to_use,target_to_use);
    	  					}else{
    	  						alert("密码错误！");
    	  					}
    	  					
    	  				}},vo);
	  				
	  				}else{
	  					box.destroy();
	  					if(isCollapse=='true')
	  						accordion.toggleCollapse(false);
	  					if(target_to_use != null && target_to_use != "")
							window.open(url_to_use,target_to_use);
	  				}
				}
			}
		}
	}).showAt(xpoint,100);
}


