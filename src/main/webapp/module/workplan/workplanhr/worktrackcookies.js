/**
 * 
 * @param name  当前用户
 * cvalue 要存放的期间类型
 * @returns
 */
function setCookie(name,cvalue){
	delCookie(name);
	var cookie = document.cookie;
	var Days = 30;
	var exp = new Date();
	//cookie有效期
	exp.setTime(exp.getTime() + Days * 24 * 60 * 60 * 1000 * 6);
	//存cookie
	document.cookie = "worktrack_" + getEncodeStr(name) + "=" + cvalue
			+ ";expires=" + exp.toGMTString();
}
/**
 * 获取cookie
 * name  当前用户
 */
function getCookieValue(name) 
{ 
	var name = "worktrack_" + getEncodeStr(name);
	var cookieValue = ""
	var strCookie = document.cookie;
	var arrCookie = strCookie.split(";");
	for (var i = 0; i < arrCookie.length; i++) {
		var arr = arrCookie[i].split("=");
		
		if (arr[0].indexOf(name) > -1) {
			 cookieValue = arr[1];
			 break;
		}
	}
	return cookieValue;
}
/**
 * 删除cookie
 * @param name  当前用户
 * @returns
 */
function delCookie(name) 
{ 
    var exp = new Date(); 
    exp.setTime(exp.getTime() - 1); 
    var cval=getCookieValue(name);
    if(cval!="") 
        document.cookie= "worktrack_"+getEncodeStr(name) + "="+cval+";expires="+exp.toGMTString(); 
} 