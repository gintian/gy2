function SetCookie(name, value)
{ //设置名称为name,值为value的Cookie 
	var Days = 7; 
	var exp  = new Date();
	value=getEncodeStr(value);
	exp.setTime(exp.getTime() + Days*24*60*60*1000);
	document.cookie = name + "="+ value/*escape (value)*/ + ";expires=" + exp.toGMTString()+";path=/";
	//alert( document.cookie );
}

function SetAppdateCookie(name, value)
{
	/*var Days = 7; 
	var exp  = new Date();
	exp.setTime(exp.getTime() + Days*24*60*60*1000);
	document.cookie = name + "="+ value + ";expires=" + exp.toGMTString()+";path=/";	*/
	var Days = 30; //此 cookie将被保存 30 天
  var exp  = new Date();    //new Date("December 31, 9998");
  exp.setTime(exp.getTime() + Days*24*60*60*1000);
  document.cookie = name + "="+ escape(value) +";expires="+ exp.toGMTString()+";path=/";
}
			//删除名称为name的Cookie 
function DeleteCookie (name) 
{ 
	var exp = new Date(); 
	exp.setTime (exp.getTime() - 1); 
	var cval = GetCookie (name); 
	document.cookie = name + "=" + cval + "; expires=" + exp.toGMTString()+";path=/"; 
}
function GetCookie (name)
{ //取得名称为name的cookie值 
	var arg = name + "="; 
	var alen = arg.length; 
	var clen = document.cookie.length; 
	var i = 0; 
	while (i < clen) 
	{ 
		var j = i + alen; 
		//alert( document.cookie.substring(i, j) );
		if (document.cookie.substring(i,j) == arg) 
			return getCookieVal (j); 
		i = document.cookie.indexOf(" ", i) + 1; 
		if (i == 0)		 break; 
	} 
	return null; 
}
///cookie操作
function getCookieVal (offset) 	
{ //取得项名称为offset的cookie值 
	var endstr = document.cookie.indexOf (";", offset); 
	if (endstr == -1) 
		endstr = document.cookie.length; 
	return unescape(document.cookie.substring(offset, endstr)); 
} 

//记录用户名及密码
function saveUser()
{
	if( document.all.chk&&document.all.chk.checked )
	{
		SetCookie( "RecordName", document.all.username.value );
		SetCookie( "RecordPwd",  document.getElementById('passwordInput').value );		
	}
	else
	{
		DeleteCookie("RecordName");
		DeleteCookie("RecordPwd");
	}
}

//清除用户
function clearUser()
{
	DeleteCookie("RecordName");
	DeleteCookie("RecordPwd");
	document.all.chk.checked = false;
	document.all.username.value = "";
	document.all.password.value = "";
}

function initData(){
	if(!document.all.chk)
		return;
	var rn = GetCookie("RecordName");
	var rp = GetCookie("RecordPwd");
	if(rn && rp){
		document.logonForm.username.value = getDecodeStr(rn);
		//document.logonForm.passwordInput.value = getDecodeStr(rp);
		document.getElementById('passwordInput').value = getDecodeStr(rp);
		document.all.chk.checked=true;
	}
}
