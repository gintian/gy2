function showCard(dbname,a0100){
	window.open("/hire/hireNetPortal/zp_showcard.do?b_showcard=link&inforkind=1&returnvalue=home5&&home=5&ver=5&a0100="+a0100+"&dbname="+dbname,"blank");
}

function exit()
{
	window.location="/servlet/hirelogin/HireCloseBrowse";
	// document.employPortalForm.target='_self';
	// document.employPortalForm.action="/hire/hireNetPortal/search_zp_position.do?b_exit=exit";
	// document.employPortalForm.submit();
}
function resumeBrowse(dbname,a0100)
{
	window.open("/hire/hireNetPortal/search_zp_position.do?b_resumeBrowse=browse&entryType=1","_blank");
		
}
function resumeBrowseForHeader(dbname,a0100){
	window.open("/hire/hireNetPortal/search_zp_position.do?b_resumeBrowse=browse&dbName="+dbname+"&a0100="+a0100,"_blank");
}
function activeResume(dbname,a0100,value)
{
   if(value=='1')
   {
      if(!confirm("确认将简历置为关闭状态？"))
      {
         return;
      }
      
   }
   document.employPortalForm.action="/hire/hireNetPortal/search_zp_position.do?b_active=login&setID=0&opt=1";
   document.employPortalForm.submit();
}
function del(zp_pos_id,a0100,dbName)
{
	Ext.Msg.confirm('提示信息', '您确定要取消应聘此职位吗？', function(btn) {
		if (btn == 'yes') {
		  var hashvo=new ParameterSet();
	      var In_paramters="a0100="+a0100;  
		  hashvo.setValue("zp_pos_id",zp_pos_id);
		  hashvo.setValue("dbname",dbName);
		  hashvo.setValue("opt","del");
		  /** 用于区别是来自于招聘外网还是招聘自助* */
		  hashvo.setValue("from", "zpww");
		  var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfoONE,functionId:'3000000172'},hashvo);
		}
	});
	
}
function returnInfoONE(outparamters)
{
	var info=outparamters.getValue("info");
	Ext.Msg.show({
		title:"提示信息",
		message:info,
		buttons: Ext.Msg.OK,
	    icon: Ext.Msg.INFO,
	    fn: function(btn) {
	    	if (btn === 'ok') {
	    		document.location.reload()
	    	}
	    }
	});
}
function order(a0100)
	{
		var hashvo=new ParameterSet();
		var In_paramters="a0100="+a0100;  
		var avalue="";
		var values="";
		for(var i=0;i<document.employPortalForm.elements.length;i++)
		{
			var number=/^[+]?\d+$/;
			
			if(document.employPortalForm.elements[i].type=='select-one')
			{
			   if(trim(document.employPortalForm.elements[i].value).length!=0)
			   {
				if(values.indexOf(document.employPortalForm.elements[i].value)!=-1)
				{
					alert(POSITION_NUMBER_NOT_REPEAT+"！");
					return;
				}
				else
				{
					values=values+'#'+document.employPortalForm.elements[i].value;
					avalue=avalue+'#'+document.employPortalForm.elements[i].name+"/"+document.employPortalForm.elements[i].value;
				}
				}
			}
		}
		hashvo.setValue("value",avalue);
		hashvo.setValue("opt","order");
		/** 用于区别是来自于招聘外网还是招聘自助* */
		hashvo.setValue("from", "zpww");
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfoOrder,functionId:'3000000172'},hashvo);
	}
		function returnInfoOrder(outparamters)
	{
		var info=outparamters.getValue("info");
		alert(info);
		document.location.reload();
	}
	function pf_ChangeFocus() 
    { 
      key = window.event.keyCode; 
      
      if ( key==0xD)
      {
      	if(event.srcElement.tagName=='SELECT')
      			order();     
      }
   }   
  // 应聘岗位页面js结束-------------------------------------------------
function more(id)
	{
		
		document.employPortalForm.action="/hire/hireNetPortal/search_zp_position.do?b_more=link&isAllPos=1&isAll=1&unitCode="+id;
		document.employPortalForm.submit();
	}
function more_q(id,isAll)
{
	var ss=getCookie('hjsjpos');
	ss=getEncodeStr(ss);
	if(ss!=null&&ss.length!=0){
		var url="/hire/hireNetPortal/search_zp_position.do?b_query=link&z0301="+ss+"&returnType=resume&isAllPos="+isAll+"&unitCode="+id;
		document.employPortalForm.action=url;
		window.setTimeout("document.employPortalForm.submit()",0);// 采用延时加载是为了使ie6能够跳转
		
	}else{
		alert("您还没浏览过任何职位或专业！");
		return;
	}	

	// /document.employPortalForm.action="/hire/hireNetPortal/search_zp_position.do?b_query=link&isAllPos="+isAll+"&unitCode="+id;
    // /document.employPortalForm.submit();
}
		
function isValidDate(day, month, year) {
    if (month < 1 || month > 12) {
            return false;
        }
        if (day < 1 || day > 31) {
            return false;
        }
        if ((month == 4 || month == 6 || month == 9 || month == 11) &&
            (day == 31)) {
            return false;
        }
        if (month == 2) {
            var leap = (year % 4 == 0 &&
                       (year % 100 != 0 || year % 400 == 0));
            if (day>29 || (day == 29 && !leap)) {
                return false;
            }
        }
        return true;
    }
function openwindow(url)
	{
	   if(url.toLowerCase().indexOf("http")==-1)
	       url="http://"+url;
		window.open(url,"_blank");
	
	}
function openwindow2(b0110)
	{
		window.open("/hire/hireNetPortal/search_zp_position.do?b_showContent=show&b0110="+b0110,"blank","width="+window.screen.width+",height="+window.screen.height+"top=0,left=0,toolbar=no,menubar=no,scrollbars=yes,resizable=no,location=no,status=no");
	}
	
function T_BUTTON()
{
 document.employPortalForm.action="/hire/hireNetPortal/search_zp_position.do?b_register=register";
 document.employPortalForm.submit();
}
function TR_BUTTON()
{
  document.employPortalForm.action="/hire/hireNetPortal/search_zp_position.do?br_license=license";
  document.employPortalForm.submit();
}
function pf_ChangeFocus() 
    { 
      key = window.event.keyCode;     
      if ( key==0xD)// 回车
      {
      	if(event.srcElement.name=='loginName'||event.srcElement.name=='password'||event.srcElement.name=='validatecode')
      			hireloginvalidate(0)// /login();
      	else if(event.srcElement.name!=null&&event.srcElement.name.length>14&&event.srcElement.name.substring(0,14)=='conditionField')
      			query()	
      			
      }
    }   
	function login1(flag,isApplyedPos,posID,z0301,posname)
	{
		if(flag=='2'){
			var loginName=document.getElementById("loginNamey");
			var password=document.getElementById("passwordy");
			if(!fucEmailchk(loginName.value))
			{
				alert(EMAIL_ADDRESS_IS_WRONG+"！");
				return;
			}
			
			
			if(password.value==''||password.value==' ')
			{
				alert(PASSWORD_IS_NOT_FILL+"！");
				return;
			}
			document.employPortalForm.loginName.value=loginName.value;
			document.employPortalForm.password.value=password.value;
			var hashvo=new ParameterSet();
			hashvo.setValue("posID",posID);
			hashvo.setValue("z0301",z0301);
			hashvo.setValue("loginName",loginName.value);
			hashvo.setValue("password",password.value);
			hashvo.setValue("login2","login2");
			hashvo.setValue("posname",posname);
			var In_paramters="operate=ajax";  
		   	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfoThr,functionId:'3000000159'},hashvo);
	   	}
	   	if(flag=='3'){
	   		if(!fucEmailchk(document.employPortalForm.loginName.value))
			{
				alert(EMAIL_ADDRESS_IS_WRONG+"！");
				return;
			}
			if(document.employPortalForm.password.value==''||document.employPortalForm.password.value==' ')
			{
				alert(PASSWORD_IS_NOT_FILL+"！");
				return;
			}
			var hashvo=new ParameterSet();
			hashvo.setValue("loginName",document.employPortalForm.loginName.value);
			hashvo.setValue("password",document.employPortalForm.password.value);
			var In_paramters="operate=ajax";  
		   	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfoTWO,functionId:'3000000159'},hashvo);
	   	}
	   	
		
	}
	function login()
	{
		if(!fucEmailchk(document.employPortalForm.loginName.value))
		{
			alert(EMAIL_ADDRESS_IS_WRONG+"！");
			return;
		}
		if(document.employPortalForm.password.value==''||document.employPortalForm.password.value==' ')
		{
			alert(PASSWORD_IS_NOT_FILL+"！");
			return;
		}
		var hashvo=new ParameterSet();
		hashvo.setValue("loginName",document.employPortalForm.loginName.value);
		hashvo.setValue("password",document.employPortalForm.password.value);
		var In_paramters="operate=ajax";  
	   	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfoTWO,functionId:'3000000159'},hashvo);
		
	}
	
function returnInfoTWO(outparamters)
	{
		var info=outparamters.getValue("info");
		if(info==0)
		{
			alert(EMAIL_OR_PASSWORD_IS_WRONG_CONFIRM_AGIN+"!");
		}else if(info=='5')
			{
			  alert("该帐号未激活，请到注册邮箱中点击激活链接，激活帐号！");
			  return;
			}
		else
		{
		    if(document.getElementById("remenberme")&&document.getElementById("remenberme").checked)
		    {
    			setDaysCookie("hjsjloginName",document.employPortalForm.loginName.value,60);
	    		setDaysCookie("hjsjpassword",document.employPortalForm.password.value,60);
	    	}
	   
			document.employPortalForm.action="/hire/hireNetPortal/search_zp_position.do?b_login=login";
			document.employPortalForm.submit();
		}
	}
	function returnInfoThr(outparamters)
	{
		var info=outparamters.getValue("info");
		if(info==0)
		{
			alert(EMAIL_OR_PASSWORD_IS_WRONG_CONFIRM_AGIN+"!");
		}else if(info=='5')
		{
			  document.employPortalForm.loginName.value="";
			  document.employPortalForm.password.value="";
			  alert("该帐号未激活，请到注册邮箱中点击激活链接，激活帐号！"); 
			  return;
		}
		else
		{
			 if(document.getElementById("remenberme")&&document.getElementById("remenberme").checked)
			 {
	    			setDaysCookie("hjsjloginName",document.getElementById("loginnamey").value,60);
		    		setDaysCookie("hjsjpassword",document.getElementById("passwordy").value,60);
		     }
			if(info=='4'){
				alert(RESUME_IS_NOT_COMPLETE_NOT_APPLY+"！");
				document.employPortalForm.action="/hire/hireNetPortal/search_zp_position.do?b_showResumeList=show&setID=0&opt=1&login=2";
				document.employPortalForm.submit();
			}else{
			   
		    	var a0100=outparamters.getValue("a0100");
		    	var posID=outparamters.getValue("posID");
		    	var z0301=outparamters.getValue("z0301");
		    	var username=outparamters.getValue("userName");
		    	var person_type=outparamters.getValue("person_type");
		    	var posname=outparamters.getValue("posname");
				document.employPortalForm.action="/hire/hireNetPortal/search_zp_position.do?b_posDesc=link&login2=login2&a0100="+a0100+"&posID="+posID+"&z0301="+z0301+"&username="+getEncodeStr(username)+"&posName="+getEncodeStr(posname);
				document.employPortalForm.submit();
			}
		}
	}
function deleteRecord(i9999)
	{
		if(confirm(COFIRM_TO_DELETE_INFO+"？"))
		{
			document.employPortalForm.action="/hire/hireNetPortal/search_zp_position.do?b_delete=delete&i9999="+i9999;
			document.employPortalForm.submit();
		}
	}
function upload(flag)
{
    var path=document.getElementById("fff").value;
    if(trim(path).length<=0)
   {
      alert(SELECT_FIELD);
      return;
   }
    
  if(flag=='1')
     document.employPortalForm.action = "/hire/hireNetPortal/search_zp_position.do?b_uploade=upload&finished=1";
  else
     document.employPortalForm.action = "/hire/hireNetPortal/search_zp_position.do?b_upload=upload";
  document.employPortalForm.submit();
}// 【11482】外网简历上传改为保存文件到文件夹，外网删除文件时，不能再使用i9999，改为使用文件名删除 jingq upd 2015.08.05
function deleteattach(a0100,filename,nbase,id)
{
     if(confirm(CONFIRM_DELETE_FILE))
     {
        document.employPortalForm.action = "/hire/hireNetPortal/search_zp_position.do?b_delattach=del&a0100="+a0100+"&filename="+filename+"&nbase="+nbase+"&id="+id;
        document.employPortalForm.submit();
     }
}// ------------------------------
 function browserinfo(){
        var Browser_Name=navigator.appName;
        var Browser_Version=parseFloat(navigator.appVersion);
        var Browser_Agent=navigator.userAgent;
        
        var Actual_Version;
        var is_IE=(Browser_Name=="Microsoft Internet Explorer");
        if(is_IE){
            var Version_Start=Browser_Agent.indexOf("MSIE");
            var Version_End=Browser_Agent.indexOf(";",Version_Start);
            Actual_Version=Browser_Agent.substring(Version_Start+5,Version_End)
        }
       return Actual_Version;
    }
function review(a0100)
{
        var infos=new Array();
   		infos[0]=a0100;
		var thecodeurl="/hire/employActualize/reviews/reviews.do?b_query=link`person_type="+perT+"`personid="+a0100; 
		var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
		var return_vo= window.showModalDialog(iframe_url,infos, 
       		 "dialogWidth:500px; dialogHeight:390px;resizable:no;center:yes;scroll:yes;status:no");
       	if(return_vo)
       	{
	        alert(ADD_SUCCESS+"！");
	        window.location.reload();
	    }
}
 function openPhoto()
   {
   		window.open("/hire/hireNetPortal/search_zp_position.do?br_showPhoto=show","_blank");
   
   }
function executeOutFile(hireChannel,workExperience,a0100)
   {
        var hashvo=new ParameterSet();
        hashvo.setValue("workExperience",workExperience);   
        hashvo.setValue("a0100",a0100); 
        hashvo.setValue("hireChannel",hireChannel); 
        var request=new Request({method:'post',onSuccess:showfile,functionId:'3000000211'},hashvo);
	    // var In_paramters="a0100=${employPortalForm.a0100}";
	   // var request=new
		// Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showfile,functionId:'3000000211'});
		
	}
function showfile(outparamters)
	{
		var outName=outparamters.getValue("outName");
		outName = decode(outName);
		var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName);
	
	}
	// -----------------
function sendmail()
{
   var zze=document.getElementById("zzee").value;
   var validatecode=document.getElementById("validatecode").value;
   var validates=document.getElementById("validates").value;
   var hireChannel
   var sendmails=document.getElementById("sendmails");
   if(trim(zze).length<=0)
   {
      alert("注册邮箱输入不能为空！");
      return;
   }
   if(validatecode==''||validatecode==' ')
    {
            alert("验证码不能为空！");
            return;
    }
    var mm=/^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
    if(!mm.test(zze))
    {
        alert("请输入正确的邮箱地址，以方便帮您找回密码！");
        return;
    }
    if(validatecode.toUpperCase()!=validates.toUpperCase())
    {
        alert("请输入正确的验证码！");
        return;
    }
   sendmails.setAttribute("style","this.disabled=true")
   sendmails.setAttribute("onclick","");
    var str="<form action='/servlet/GetPasswordServlet' method=post name=formx1 target='hireGetPassword' style='display:none'>";
    str+="<input type='hidden' name='email' value="+getEncodeStr(zze)+" />";
    str+="<input type='hidden' name='validatecode' value="+getEncodeStr(validatecode)+" />";
    str+="</form>";
    
    document.getElementById("biaodan").innerHTML=str;
    document.formx1.submit();
    // window.close();
   // var hashvo=new ParameterSet();
   // hashvo.setValue("email",getEncodeStr(zze));
   // hashvo.setValue("validateInfo",getEncodeStr(validatecode));
   // hashvo.set("session",getSession);
   // var request=new
	// Request({method:'post',asynchronous:true,onSuccess:returnInfoTHREE,functionId:'30200710250'},hashvo);
   
}
function returnInfoTHREE(outparameters)
{
  var msg = outparameters.getValue("msg");
  msg = getDecodeStr(msg);
  if(msg=='1')
  {
     alert("已将您的密码发送到您的注册邮箱，请您注意查收！");
     document.employPortalForm.action = "/hire/hireNetPortal/search_zp_position.do?br_getpassword=get";// 重新刷新当前界面，用于验证码重新生成
     document.employPortalForm.submit();
  }
  else
  {
    alert(msg);
  }
}
function subEDIT(dbname,a0100,complexPassword,passwordMinLength,passwordMaxLength)
	{	
		var a1=document.getElementById('t1');
		a1.innerHTML="";
		var a2=document.getElementById('t2')
		a2.innerHTML="";
		var a3=document.getElementById('t3')
		a3.style.display="none";
		var flag=0;
		if(document.employPortalForm.pwd0.value=="")
		{
			a1.innerHTML=PLEASE_FILL_OLD_PASSWORD;
			flag=1;		
		}
		
		if(document.employPortalForm.pwd1.value=="")
		{			
			a2.innerHTML=PLEASE_FILL_NEW_PASSWORD;
			flag=1;		
		}
		if(complexPassword==1){
			if(document.employPortalForm.pwd1.value.length<passwordMinLength||document.employPortalForm.pwd1.value.length>passwordMaxLength)
			{			
				a2.innerHTML="密码长度为"+passwordMinLength+"-"+passwordMaxLength+"位";
				flag=1;		
			}else {
			  var numasc = 0;
		      var charasc = 0;
		      var otherasc = 0;
			  for (var i = 0; i < document.employPortalForm.pwd1.value.length; i++) {
                var asciiNumber = document.employPortalForm.pwd1.value.substr(i, 1).charCodeAt();
                if (asciiNumber >= 48 && asciiNumber <= 57) {
                    numasc += 1;
                }
                if ((asciiNumber >= 65 && asciiNumber <= 90)||(asciiNumber >= 97 && asciiNumber <= 122)) {
                    charasc += 1;
                } // /%$#@!~^&*\'()\"
                if ((asciiNumber >= 33 && asciiNumber <= 42)||asciiNumber == 64 ||asciiNumber == 94 ||asciiNumber == 126 ) {
                    otherasc += 1;
                }
            }
            if(0==numasc)  {
           	    a2.innerHTML="密码必须含有数字";
				flag=1;		
            }else if(0==charasc){
                a2.innerHTML="密码必须含有字母";
				flag=1;		
            }else if(0==otherasc){
                a2.innerHTML="密码必须含有特殊字符(%$#@!~^&*\'()\")";
				flag=1;	
			}	
		}
			
		}else{
			if(document.employPortalForm.pwd1.value.length<6||document.employPortalForm.pwd1.value.length>8)
			{			
				a2.innerHTML=PASSWORD_LENGTH_MUST_SIX_TO_EIGHT;
				flag=1;		
			}
		}

		if(document.employPortalForm.pwd1.value!=document.employPortalForm.pwd2.value)
		{			
			a2.innerHTML=PASSWORD_IS_NOT_UNANIMOUS;
			flag=1;					
		}
		if(flag==0)
		{
			var hashvo=new ParameterSet();
			hashvo.setValue("pw0",document.employPortalForm.pwd0.value);
			hashvo.setValue("pw1",document.employPortalForm.pwd1.value);
			hashvo.setValue("dbname",dbname);
			var In_paramters="a0100="+a0100;  
			var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfoFOUR,functionId:'3000000169'},hashvo);
		}
	}
	
	
	function returnInfoFOUR(outparamters)
	{
		var a3=document.getElementById('t3')
		a3.style.display="block";
		var info=outparamters.getValue("info");
		alert(info);
		document.getElementById("pwd0").value='';
		document.getElementById("pwd1").value='';
		document.getElementById("pwd2").value='';
	}
	function pf_ChangeFocusTWO(dbname,a0100) 
    { 
      key = window.event.keyCode;     
      if ( key==0xD)
      {
      	if(event.srcElement.name=='pwd0'||event.srcElement.name=='pwd1'||event.srcElement.name=='pwd2')
      			subEDIT(dbname,a0100);
      		
      }
    }   
	function submitInterview()
    {
      var obj = document.getElementsByName("codevalue");
      var num=0;
      for(var i=0;i<obj.length;i++)
      {
         if(obj[i].checked)
         {
             document.employPortalForm.interviewingCodeValue.value=obj[i].value;
             num++;
         }
      }
      if(num==0)
      {
         alert("请选择回复状态后再提交！");
         return;
      }
      document.employPortalForm.action="/hire/hireNetPortal/search_zp_position.do?b_save=save&entery=3";
      document.employPortalForm.submit(); 
    }
	function subagreement(agreement)
	{
	    if(agreement=='0')
	    {
	    
	       alert(NOT_ACCEPT_AGREEMENT_NOT_REGISTER+"！");
	       return;
	    }
	    else
	    {
	       document.employPortalForm.action="/hire/hireNetPortal/search_zp_position.do?b_register=register";
		   document.employPortalForm.submit();
	    }
	}
	function pf_ChangeFocusTHREE() 
    { 
      key = window.event.keyCode;     
      if ( key==0xD)
      {
      	if(event.srcElement.name=='loginName'||event.srcElement.name=='password')
      			login();
      	else if(event.srcElement.name=='txtEmail'||event.srcElement.name=='pwd1'||event.srcElement.name=='pwd2'||event.srcElement.name=='txtName')
      			subagreement('1');
      			
      }
    } 
function changeSRC()
{
  var obj1=document.getElementById("js");
  obj1.innerHTML="<img src='/images/hire/acceptt.gif' style='cursor:hand;' id=\"js1\" border='0' onclick=\"subagreement('1');\" />&nbsp;&nbsp;<img src='/images/hire/bujs.gif' border='0'  style='cursor:hand;' id=\"js2\" onclick=\"subagreement('0');\"/>";
}  
function goback(type)
{
	if(type=='1'){
		document.employPortalForm.action="/hire/hireNetPortal/search_zp_position.do?b_query=link";
		document.employPortalForm.submit();
	}else if(type==='recommend'){// 这个参数代表着是猎头选择人员进入职位列表时,查看职位详情后点返回
		document.employPortalForm.action='/hire/hireNetPortal/recommend_resume.do?b_recommendPosition=add&reCommendoption='+reCommendoption+'&a0100='+recommendA0100s+"&userName="+recommendUserNames;
		document.employPortalForm.submit();
	}else if(type==='headHunter'){
		document.employPortalForm.action='/hire/hireNetPortal/search_zp_position.do?b_query=link&returnType=headHunter';
		document.employPortalForm.submit();
	}
	else{
		if(type=='search'){
			document.employPortalForm.action="/hire/hireNetPortal/search_zp_position.do?b_query=link&returnType=search&selunitcode="+selunitcode;
		    document.employPortalForm.submit();
		}else if(type=='resume'){
			var ss=getCookie('hjsjpos');
			var url="/hire/hireNetPortal/search_zp_position.do?b_query=link&z0301="+$URL.encode(ss)+"&returnType=resume";
			document.employPortalForm.action=url;
			document.employPortalForm.submit();
		}else{
			document.employPortalForm.action="/hire/hireNetPortal/search_zp_position.do?b_searpos=init";
	    	document.employPortalForm.submit();
		}
	    
	}
}
var gloleft = (window.screen.availWidth-385-10)/2;// 计算窗口距离屏幕左侧的间距
													// 540是窗口宽度，10是边框大小
var glotop = (window.screen.availHeight-250-30)/2;// 窗口距离屏幕上方的间距
													// 400是窗口高度，30是边框和标题栏大小(20)
function returnInfo2(outparamters)
{
	var info=outparamters.getValue("info");  // 2: 申请成功 3:已超过了申请职位的最大数量3
												// 4:简历资料必填项没填
	var userName=outparamters.getValue("userName");
	var type=outparamters.getValue("returnType");
	if(info=='10')
	{
		Ext.showAlert("该用户已入职，不允许继续申请！");return;
	}
	if(info=='before'){
		Ext.showAlert("尚未到该职位的报名起始日期，不允许继续申请！");
		return;
	}
	if(info=='after'){
		Ext.showAlert("已超过该职位的报名截止日期，不允许继续申请！");
		return;
	}
	if(info=='2')
	{
		Ext.Msg.show({
			title:"提示信息",
			message:POSITION_APPLY_SUCCESS_PLEASE_WAIT+"！",
			buttons: Ext.Msg.OK,
		    icon: Ext.Msg.INFO,
		    fn: function(btn) {
		    	if (btn === 'ok') {
		    		if(type=='resume'){
		    			var ss=getCookie('hjsjpos');
		    			var url="/hire/hireNetPortal/search_zp_position.do?b_query=link&z0301="+ss+"&returnType=resume";
		    			document.employPortalForm.action=url;
		    			document.employPortalForm.submit();
		    			
		    		}else if(type=='search'){
		    			document.employPortalForm.action="/hire/hireNetPortal/search_zp_position.do?b_query=link&returnType=search&selunitcode="+selunitcode;
		    			document.employPortalForm.submit();
		    			
		    		}else{
		    			document.location="/hire/hireNetPortal/search_zp_position.do?b_query=link&operate=init";
		    		}
		    	}
		    }
		});
	}
	else if(info=='6')
	{
		Ext.Msg.show({
			title:"提示信息",
			message:"您的岗位申请已被接受，请等待通知！",
			buttons: Ext.Msg.OK,
		    icon: Ext.Msg.INFO,
		    fn: function(btn) {
		    	if (btn === 'ok') {
		    		if(type=='resume'){
		                var ss=getCookie('hjsjpos');
		                var url="/hire/hireNetPortal/search_zp_position.do?b_query=link&z0301="+ss+"&returnType=resume";
		                document.employPortalForm.action=url;
		                document.employPortalForm.submit();
		        
		            }else if(type=='search'){
		                    document.employPortalForm.action="/hire/hireNetPortal/search_zp_position.do?b_query=link&returnType=search&selunitcode="+selunitcode;
		                    document.employPortalForm.submit();
		            }else{
		                document.location="/hire/hireNetPortal/search_zp_position.do?b_query=link&operate=init";
		            }
		    	}
		    }
		});
    }else if(info.indexOf('-')>-1){
		var arr = info.split("-");
		var message = "";
		if(arr.length>2)
			message = arr[1]+arr[2];
		else
			message = arr[1]+"必须填写！";
		
		Ext.Msg.show({
			title:"提示信息",
			message:message,
			buttons: Ext.Msg.OK,
		    icon: Ext.Msg.INFO,
		    fn: function(btn) {
		    	if (btn === 'ok') {
		    		var opt = 1;
		    		if("a00" == arr[0]) {
		    			arr[0] = "-1";
		    			opt = 2;
		    		}
		    		
		    		document.employPortalForm.action="/hire/hireNetPortal/search_zp_position.do?b_showResumeList=show&setID="+arr[0]+"&opt=" + opt;
		    		document.employPortalForm.submit();
		    	}
		    }
		});
		
	}
	else
	{
		var infos=new Array();
		infos[0]=info;    // 1:为已申请 2: 申请成功 3:已超过了申请职位的最大数量3 4:简历资料必填项没填
		infos[1]=userName;
		if(isIE())
			var flag= window.showModalDialog("alert.jsp?infos="+infos[0], infos, 
			        "dialogWidth:385px; dialogHeight:250px;resizable:no;center:yes;scroll:no;status:no");
		else
			window.open("alert.jsp?infos="+infos[0], "", 
		               "width=385px,height=250px,top="+glotop+",left="+gloleft+",resizable=no,center=yes,scroll=yes,location=no,status=no");
	}

}
function isIE() { // ie?
    if (!!window.ActiveXObject || "ActiveXObject" in window)  
        return true;  
    else  
        return false;  
 }
/**
 * 校验是否符合职位筛选规则
 * @param a0100 人员id
 * @param z0301 职位id
 * @returns
 */
function apply(isApplyedPos,a0100,userName,posID,z0301,person_type,loginName,returntype,hireChannel){
	if(a0100!=null&&a0100.length>2)
	{
		var map = new HashMap();
		map.put("a0100", a0100);
		map.put("z0301", z0301);
		map.put("isApplyedPos", isApplyedPos);
		map.put("userName", userName);
		map.put("posID", posID);
		map.put("person_type", person_type);
		map.put("loginName", loginName);
		map.put("returntype", returntype);
		map.put("hireChannel", hireChannel);
		map.put("filter","true");
		Rpc({functionId : '3000000170',success:showFilterInfo}, map);
	}else{
		Ext.showAlert("您还没有登录,请先登录!");
	}
}
function showFilterInfo(outparamters){
	var filterFlag = true;
	var result = Ext.decode(outparamters.responseText);
	if(result.a0100!=null&&result.a0100.length>2)
	{
		if(result.filterFlag){
			var hashvo=new ParameterSet();
			hashvo.setValue("posID",result.posID);
			hashvo.setValue("z0301",result.z0301);
			hashvo.setValue("person_type",result.person_type);
			hashvo.setValue("userName",result.userName);
			hashvo.setValue("returnType",result.returntype);
			hashvo.setValue("hireChannel",result.hireChannel);
			hashvo.setValue("a0100",result.a0100);
			var request=new Request({method:'post',asynchronous:false,onSuccess:returnInfo2,functionId:'3000000170'},hashvo);
		}else{
			var ruleFilter = result.ruleFilter;
			var jobTybe = result.jobTybe;
			var checkApplyQualify = result.checkApplyQualify;
			var info = result.info;
			if(info=='10')
			{
				Ext.showAlert("该用户已入职，不允许继续申请！");
				return;
			}
			if(info=='before'){
				Ext.showAlert("尚未到该职位的报名起始日期，不允许继续申请！");
				return;
			}
			if(info=='after'){
				Ext.showAlert("已超过该职位的报名截止日期，不允许继续申请！");
				return;
			}
			var msg = "无法申请该职位。\n";
			if(ruleFilter) {
				if(ruleFilter.length==1){
					msg += "您注册时选择的“"+result.ruleFilter[0];
					if(jobTybe!="")
						msg +="”与本职位的“"+jobTybe+"”要求不符合。\n";
					else
						msg +="”不符合本职位的要求。\n";
				}else{
					for(var i = 0; i<result.ruleFilter.length; i++){
						msg += (i+1)+"、"+result.ruleFilter[i]+"。\n";
					}
					
				}
			}else if(checkApplyQualify){
				Ext.showAlert(checkApplyQualify);
				return;
			}
			
			msg +="请您试着申请其它感兴趣的职位。";
			Ext.showAlert(msg);
		}
	}
	else
	{
		Ext.showAlert("您还没有登录,请先登录!");
		/*
		 * var bg = document.getElementById("mapBgLayer"); var con =
		 * document.getElementById("mapLayer");
		 * 
		 * 
		 * var w =document.body.clientWidth; var h1=document.body.scrollTop
		 * var bodySize = []; with(document.documentElement) { bodySize[0] =
		 * (scrollWidth>clientWidth)?scrollWidth:clientWidth;//如果滚动条的宽度大于页面的宽度，取得滚动条的宽度，否则取页面宽度
		 * bodySize[1] =
		 * (scrollHeight>clientHeight)?scrollHeight:clientHeight;//如果滚动条的高度大于页面的高度，取得滚动条的高度，否则取高度
		 *  } bg.style.display = "block"; bg.style.width = bodySize[0] +
		 * "px"; bg.style.height = (bodySize[1]+100) + "px";
		 * con.style.display = "block"; con.focus(); var zc =
		 * document.getElementById("cms_pnl");
		 * 
		 * zc.focus();
		 */
	}
}
function enter()
{
    window.returnValue="1";
    window.close();
}
function qxl()
{
   window.returnValue="0";
   window.close();
}
function subRegister(isDefinitionActive,cultureCodeItem,isDefinitionCulture,paramFlag,blackField,blackFieldDesc,onlyNameDesc,isDefineWorkExperience,
workExperienceDesc,person_type,itemLength,complexPassword,passwordMinLength,passwordMaxLength,candidate_status_item,candidate_status_desc,id_type_item,id_type_desc)
{	
		if(isEnrol==1){
			Ext.showAlert("正在注册简历信息,请稍后......")
			return;
		}
		var a1=document.getElementById('t1')
		a1.innerHTML="";
		var a2=document.getElementById('t2')
		a2.innerHTML="";
		var a3=document.getElementById('t3');
		
		if(!a3)
			a3 = document.getElementById("t7");
		a3.innerHTML="";
		
		var candidatehint = document.getElementById("candidatehint"); 
		if(candidatehint)
			candidatehint.innerHTML="";
		var flag=0;
		if(!fucEmailchk(document.employPortalForm.txtEmail.value))
		{	
			a1.innerHTML=EMAIL_ADDRESS_IS_WRONG;
			a1.style.textAlign="left";
			a1.style.paddingLeft="10px";
			flag=1;
		}	
		if(trim(document.employPortalForm.pwd1.value).length==0)
		{			
			a2.innerHTML=PASSWORD_MUST_FILL_BUT_NOT_NULL;
			a2.style.textAlign="left";
			a2.style.paddingLeft="10px";
			flag=1;		
		}
		
		var name;
		if(document.employPortalForm.txtName)
			name = trim(document.employPortalForm.txtName.value);
		else
			name = document.getElementById("onlyv").value;
			
		if(name.toLowerCase()=="true"||name.toLowerCase()=="false"){
				a3.innerHTML="姓名不能为布尔型数据";
				a3.style.textAlign="left";
				a3.style.paddingLeft="10px";
				flag=1;		
		}
		var numtype =/^(-?\d+)(\.\d+)?$/;
		if(numtype.test(name)){
				a3.innerHTML="姓名不能为数字";
				a3.style.textAlign="left";
				a3.style.paddingLeft="10px";
				flag=1;		
		}
		var riqi=/((^((1[8-9]\d{2})|([2-9]\d{3}))([-\/\._])(10|12|0?[13578])([-\/\._])(3[01]|[12][0-9]|0?[1-9])$)|(^((1[8-9]\d{2})|([2-9]\d{3}))([-\/\._])(11|0?[469])([-\/\._])(30|[12][0-9]|0?[1-9])$)|(^((1[8-9]\d{2})|([2-9]\d{3}))([-\/\._])(0?2)([-\/\._])(2[0-8]|1[0-9]|0?[1-9])$)|(^([2468][048]00)([-\/\._])(0?2)([-\/\._])(29)$)|(^([3579][26]00)([-\/\._])(0?2)([-\/\._])(29)$)|(^([1][89][0][48])([-\/\._])(0?2)([-\/\._])(29)$)|(^([2-9][0-9][0][48])([-\/\._])(0?2)([-\/\._])(29)$)|(^([1][89][2468][048])([-\/\._])(0?2)([-\/\._])(29)$)|(^([2-9][0-9][2468][048])([-\/\._])(0?2)([-\/\._])(29)$)|(^([1][89][13579][26])([-\/\._])(0?2)([-\/\._])(29)$)|(^([2-9][0-9][13579][26])([-\/\._])(0?2)([-\/\._])(29)$))/ig;   
		if(riqi.test(name)){
				a3.innerHTML="姓名不能为日期";
				a3.style.textAlign="left";
				a3.style.paddingLeft="10px";
				flag=1;		
		}
		if(complexPassword==1){
			if(document.employPortalForm.pwd1.value.length<passwordMinLength||document.employPortalForm.pwd1.value.length>passwordMaxLength)
			{			
				a2.innerHTML="密码长度为"+passwordMinLength+"-"+passwordMaxLength+"位";
				flag=1;		
			}else {
			  var numasc = 0;
		      var charasc = 0;
		      var otherasc = 0;
			  for (var i = 0; i < document.employPortalForm.pwd1.value.length; i++) {
                var asciiNumber = document.employPortalForm.pwd1.value.substr(i, 1).charCodeAt();
                if (asciiNumber >= 48 && asciiNumber <= 57) {
                    numasc += 1;
                }
                if ((asciiNumber >= 65 && asciiNumber <= 90)||(asciiNumber >= 97 && asciiNumber <= 122)) {
                    charasc += 1;
                } // /%$#@!~^&*\'()\"
                if ((asciiNumber >= 33 && asciiNumber <= 42)||asciiNumber == 64 ||asciiNumber == 94 ||asciiNumber == 126 ) {
                    otherasc += 1;
                }
            }
            if(0==numasc)  {
           	    a2.innerHTML="密码必须含有数字";
				flag=1;		
            }else if(0==charasc){
                a2.innerHTML="密码必须含有字母";
				flag=1;		
            }else if(0==otherasc){
                a2.innerHTML="密码必须含有特殊字符(%$#@!~^&*\'()\")";
				flag=1;	
			}	
		}
	}else{
			if(document.employPortalForm.pwd1.value.length<6||document.employPortalForm.pwd1.value.length>8)
			{			
				a2.innerHTML=PASSWORD_LENGTH_MUST_SIX_TO_EIGHT;
				flag=1;		
			}		
	}

		if(document.employPortalForm.pwd1.value!=document.employPortalForm.pwd2.value)
		{			
			a2.innerHTML=PASSWORD_IS_NOT_UNANIMOUS;
			flag=1;					
		}
		if(trim(name).length==0)
		{			
			a3.innerHTML=NAME_MUST_FILL;
			a3.style.textAlign="left";
			a3.style.paddingLeft="10px";
			flag=1;		
		}
		var ida=isDefinitionActive;
		if(ida=='1')
		{
		   if(trim(document.employPortalForm.belongUnit.value).length==0)
		   {
		      var a4=eval('t4');
		      a4.innerHTML="";
		      a4.innerHTML="限制浏览单位为必填项";
		      a4.style.textAlign="left";
		      a4.style.paddingLeft="10px";
		      flag=1;
		   }
		}
		if(IsOverStrLength(name,itemLength))
		{
			a3.innerHTML=NAME_LENGTHER_THAN+itemLength;
			flag=1;				
		}
		var codeitem=cultureCodeItem;
		var isDefinitinn=isDefinitionCulture;
		if(isDefinitinn!=null&&isDefinitinn=='1')
		{
	    	if(codeitem!=document.employPortalForm.hiddenCode.value)
	    	{
	    		Ext.showAlert(CULTURE_TYPE_NOT_TO_HANDLE);
	    	    return;
	    	}
	    }
		
		if(candidate_status_item&&"false"!=candidate_status_item){
			var candidate_status = document.employPortalForm.candidate_status.value;
			if(!candidate_status){
				//alert("您选择的"+candidate_status_desc+"不符合系统条件，注册失败");
				candidatehint.innerHTML=candidate_status_desc + "为必填项";
				flag=1;
			}
		}
		
	    var onlyFieldId = document.getElementById("cardid");
	    var filedvalue = "";
	    if(document.getElementById("onlyv")!=null&&document.getElementById("onlyv")!=undefined)
	    {
			filedvalue = document.getElementById("onlyv").value;
	    }
	    if(onlyFieldId){
	    	filedvalue = onlyFieldId.value;
	    }
	    if(blackField!="-1"&&paramFlag!="3")
	    { 
	        var t5=document.getElementById("t5");
	        filedvalue = document.getElementById("blackValue").value;
	        if(trim(filedvalue).length==0)
	        {
	            t5.innerHTML=blackFieldDesc+"为必填项";
	            t5.style.paddingLeft="10px";
	            t5.style.textAlign="left";
	            flag=1;
	        }else{
	        	if(document.getElementById("blackValue") && cardid!=null && 
	        			cardid != '' && cardid!="#"&& blackField == cardid )
	        	{
	        		var tt;
	        		if(onlyFieldId!=null && onlyFieldId!=undefined && 
	        				(!id_type||(id_type&&id_type.value == idTypeValue))){
	        			tt==true
	        		}else{
	        			tt=cidInfo(trim(filedvalue));
	        		}
	        		
	           		if(tt==true){
	           			
	           		}else{
	           			Ext.showAlert(tt);
	           			return;
	           		}
	        	}
	        }
	    }
	    if(paramFlag!="1")
	    { 
	       var t7=document.getElementById("t7");
	        if(trim(filedvalue).length==0)
	        {
	            t7.innerHTML=onlyNameDesc+"为必填项";
	            t7.style.textAlign="left";
	            t7.style.paddingLeft="10px";
	            flag=1;
	        }
	        var de=onlyNameDesc;
	        var id_type = document.employPortalForm.id_type;
	        if(onlyFieldId!=null && onlyFieldId!=undefined && (!id_type||(id_type&&id_type.value == idTypeValue)))
	        {
	        	var filedvalue = onlyFieldId.value;
	        	if(filedvalue.length!=15&&filedvalue.length!=18)
	        	{
	        		t7.innerHTML=de+"的长度为15或者18位";
	                flag=1;
                }else{
	           		var tt=cidInfo(filedvalue);
	           		if(tt==true){
	           			
	           		}else{
	           			Ext.showAlert(tt);
	           			return;
	           		}
	           }
	        }
	    }
	    if(isDefineWorkExperience=='1')
	    {
	        var workObj=document.getElementsByName("workExperience");
	        var worknum=0;
	        if(workObj)
	        {
	           for(var i=0;i<workObj.length;i++)
	           {
	               if(workObj[i].checked)
	                  worknum++;
	           }
	        }
	        if(worknum==0)
	        {
	           flag=1;
	          var a6=document.getElementById('t6');
		      a6.innerHTML="";
		      a6.style.textAlign="left";
		      a6.style.paddingLeft="10px";
		      a6.innerHTML=workExperienceDesc+"为必填项";
	        }
	    }
		if(flag==0)
		{
			isEnrol = 1;
			var hashvo=new ParameterSet();
			if(blackField!="-1"&&paramFlag!="3")
			{
		        hashvo.setValue("blackFieldValue",trim(document.employPortalForm.blackFieldValue.value));
		    }
		    else
		    {
		      hashvo.setValue("blackFieldValue","-1");
		    }
		    if(paramFlag!="1")
		    {
		         hashvo.setValue("onlyValue",trim(document.employPortalForm.onlyValue.value));
		    }
		    usernames = name;
		    hashvo.setValue("paramFlag",paramFlag);
		    hashvo.setValue("onlyNameDesc",onlyNameDesc);
		    hashvo.setValue("person_type",person_type);
		    hashvo.setValue("password",document.employPortalForm.pwd1.value);
		    hashvo.setValue("txtName",name);
		    var In_paramters="txtEmail="+document.employPortalForm.txtEmail.value;  
	     	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInforegister,functionId:'3000000161'},hashvo);
		}
	}
	var usernames = "";
	var aCity={11:"北京",12:"天津",13:"河北",14:"山西",15:"内蒙古",21:"辽宁",22:"吉林",23:"黑龙江 ",31:"上海",32:"江苏",33:"浙江",34:"安徽",35:"福建",36:"江西",37:"山东",41:"河南",42:"湖北 ",43:"湖南",44:"广东",45:"广西",46:"海南",50:"重庆",51:"四川",52:"贵州",53:"云南",54:"西藏 ",61:"陕西",62:"甘肃",63:"青海",64:"宁夏",65:"新疆",71:"台湾",81:"香港",82:"澳门",91:"国外 "}
	//设置了证件号唯一性 验证
	function cidInfo(sId){
		iSum += (Math.pow(2,i) % 11) * parseInt(sId.charAt(17 - i),11)			
		var iSum=0;
		var info="";
		   // 错误信息
		if (trim(sId)=="") {
			info="请填写您的身份证号码";     
			return info;
		}
		if(trim(sId).length!=15&&trim(sId).length!=18){
			info="请填写正确的身份证号码";     
			return info;
		}
		if(aCity[parseInt(sId.substr(0,2))]==null)
			return "1~2位地区不存在！";
		if (sId.length==15){
			isIDCard1=/^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$/;
			if (!isIDCard1.test(sId)) {
				info="身份证号码错误！";
				return info;
			}
			    
			if(aCity[parseInt(sId.substr(0,2))]==null){
				info="身份证号码地区不存在！";  
				return info;
			}
			        
			sBirthday="19"+sId.substr(6,2)+"-"+Number(sId.substr(8,2))+"-"+Number(sId.substr(10,2));
			var d=new Date(sBirthday.replace(/-/g,"/"))
			
			if(sBirthday!=(d.getFullYear()+"-"+ (d.getMonth()+1) + "-" + d.getDate())){
				info="身份证号码生日不正确！";
				return info;
			}
			return true;
		}
		    
		if (sId.length==18){
			if(!/^\d{17}(\d|x)$/i.test(sId)){
				info="身份证号码错误！";   
				return info;
			}
			sId=sId.replace(/x$/i,"a");
			if(aCity[parseInt(sId.substr(0,2))]==null){
				info="身份证号码地区不存在！"; 
				return info;
			}
			sBirthday=sId.substr(6,4)+"-"+Number(sId.substr(10,2))+"-"+Number(sId.substr(12,2));
			var d=new Date(sBirthday.replace(/-/g,"/"))
			if(sBirthday!=(d.getFullYear()+"-"+ (d.getMonth()+1) + "-" + d.getDate())){
				info="身份证号码生日错误！";
				return info;
			}
			for(var i = 17;i>=0;i--) 
				iSum += (Math.pow(2,i) % 11) * parseInt(sId.charAt(17 - i),11);
			if(iSum%11!=1){
				info="身份证号码不正确！"; 
				return info;
			}
			return true;
		}		
	}
	
	function returnInforegister(outparamters)
	{
		isEnrol = 0;
		var info=outparamters.getValue("info");
		var onlyNameDesc=outparamters.getValue("onlyNameDesc");
		var pert=outparamters.getValue("person_type");
		var acountBeActived=outparamters.getValue("acountBeActived");//=1 要进行邮箱验证
		var hireChannel=outparamters.getValue("hireChannel");
		var a1=document.getElementById('t1');
		a1.innerHTML="";
		if(info=='1')
		{
			a1.innerHTML=EMAIL_EXIST_FILL_AGIN;
			return;
		}
		else if(info=='2')
		{
			Ext.showAlert("您已经被系统列入黑名单，不能提交简历！");
		    return;
		}
		else if(info=='3')
		{
			Ext.showAlert("该"+onlyNameDesc+"已注册过用户，不能重复注册！");
		   return;
		}else if(info.length>3){
			Ext.showAlert(info);
			return;
		}
		else{
			isEnrol = 1;
			var person_type="0"; // 应聘
			if(person_type=='1')
				person_type="1";  // 后备人才
	  		 var oo=document.getElementById("remenberme");
			if(oo&&oo.checked)
			{
		    	setDaysCookie("hjsjloginName",document.employPortalForm.txtEmail.value,60);
	    		setDaysCookie("hjsjpassword",document.employPortalForm.pwd1.value,60);
	    	}
			if(acountBeActived=='1')
			{
				Ext.showAlert("简历账号注册成功，请进入邮箱激活账户后，继续更新个人简历。");	
			}
			var id_type = "";
			if(document.employPortalForm.id_type)
				id_type = document.employPortalForm.id_type.value;
			document.employPortalForm.action="/hire/hireNetPortal/search_zp_position.do?b_enroll=login&person_type="+person_type+"&idTypeValue="+id_type;
			document.employPortalForm.submit();
		}
	}
	function pf_ChangeFocusRegister(isDefinitionActive,cultureCodeItem,isDefinitionCulture,paramFlag,blackField,blackFieldDesc,onlyNameDesc,isDefineWorkExperience,workExperienceDesc,person_type,itemLength) 
    { 
      key = window.event.keyCode;     
      if ( key==0xD)
      {
      	if(event.srcElement.name=='loginName'||event.srcElement.name=='password')
      			login();
      	else if(event.srcElement.name=='txtEmail'||event.srcElement.name=='pwd1'||event.srcElement.name=='pwd2'||event.srcElement.name=='txtName')
      			subRegister(isDefinitionActive,cultureCodeItem,isDefinitionCulture,paramFlag,blackField,blackFieldDesc,onlyNameDesc,isDefineWorkExperience,workExperienceDesc,person_type,itemLength);
      			
      }
    }   
	 function prompt_content(isPrompt)
	{
		 if(isPrompt=="1"){
	    var thecodeurl="/hire/hireNetPortal/search_zp_position.do?br_prompt=inti"; 
		var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
		var values= window.showModalDialog(iframe_url,null, 
			        "dialogWidth:500px; dialogHeight:250px;resizable:yes;center:yes;scroll:yes;status:no");	
	    if(values)
	    {
	      if(values=='1')
	         openInputCodeDialogText('UN','belongUnithName','belongUnit');
	      else
	         return;
	    }
	    }else{
	        openInputCodeDialogText('UN','belongUnithName','belongUnit');
	    }
	}	
 	function pf_ChangeFocusmark() 
    { 
      key = window.event.keyCode;     
      if ( key==0xD)
      {
      	if(event.srcElement.name=='loginName'||event.srcElement.name=='password')
      			login();
      }
    } 
    
    // ------------------------------
    
    function showDateSelectBox(srcobj)
   {
      
      date_desc=srcobj;      
      Element.show('date_panel');   
      for(var i=0;i<document.employPortalForm.date_box.options.length;i++)
  	  {
  	  	document.employPortalForm.date_box.options[i].selected=false;
  	  }
      var pos=getAbsPosition(srcobj);
	  with($('date_panel'))
	  {
	        style.position="absolute";
    		style.posLeft=pos[0]-1;
			style.posTop=pos[1]-1+srcobj.offsetHeight-85;
			style.width=110;
      }                 
      
   }
   function switchPersonType(a0100)
   {
   		 if(confirm(CONFIRM_SWITCH_TO_TALENTED+"？"))
   		 {
   	   		var hashvo = new ParameterSet();
			hashvo.setValue("a0100",a0100);
			var In_parameters="operate=switch"; 
			var request = new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:returnInfo3Browse,functionId:'3000000206'},hashvo);
		}
   }
   
   function returnInfo3Browse(outparamters)
   {
   		alert(SWITCH_TO_TALENTED_SUCCESS+"！");
		window.opener.location.href=window.opener.location.href;
		window.close();   		
   	}
   	
   	 function setSelectValue(opt,statusdesc,a0100)
   {  
       var zpPosIDs=eval("document.employPortalForm.zpPosID");
       var a_zpPosID="";
       if(zpPosIDs)
       {
       if(zpPosIDs.length)
       {
	       for(var i=0;i<zpPosIDs.length;i++)
	       {
	       		if(zpPosIDs[i].checked==true)
	       			a_zpPosID=zpPosIDs[i].value;	
	       
	       }
	    }
	    else
	    {
	    	a_zpPosID=document.employPortalForm.zpPosID.value;
	    
	    }
	    }else
	    {
	      a_zpPosID="-1";
	    }
       
  		if(confirm(CONFIRM_TO_INSTALL+statusdesc+HIRE_STATUS+"？"))
  		{
  						
  						recordIDs=new Array();
  						recordIDs[0]=a0100+"/"+a_zpPosID;
  						validateState(recordIDs,opt,a0100);
  				
  		}
  				
   }
   
   
   function validateState(recordIDs,state,a0100)
   {
   		
   		var hashvo = new ParameterSet();
		hashvo.setValue("recordIDs",recordIDs);
		hashvo.setValue("a0100",a0100);
		var In_parameters="state="+state; 
		var request = new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:returnInfobrowse,functionId:'3000000204'},hashvo);
   }
   
    // 在initStr寻找所有的AFindText替换成ARepText
	function strReplaceAll(initStr,AFindText,ARepText){
	
	  var raRegExp = new RegExp(AFindText,"g");
	
	  return initStr.replace(raRegExp,ARepText);
	
	}
   
   function returnInfobrowse(outparamters)
   {
  	 	var flag = outparamters.getValue("flag");
  	 	var state=outparamters.getValue("state");
  	 	var a0100=outparamters.getValue("a0100");
  	 	var zpPosIDs=eval("document.employPortalForm.zpPosID");
        var a_zpPosID="";
        if(zpPosIDs)
        {
        if(zpPosIDs.length)
       {
	        for(var i=0;i<zpPosIDs.length;i++)
	        {
	       		if(zpPosIDs[i].checked==true)
	       			a_zpPosID=zpPosIDs[i].value;	
	       
	        }
	    }
	    else
	    	a_zpPosID=document.employPortalForm.zpPosID.value;
	   }
	   else
	     a_zpPosID="-1";
  	 	if(flag=='1')
  	 	{
  	 		var info = outparamters.getValue("info");
  	 		info=strReplaceAll(info,"<br>","\r\n")
  	 		alert(info);
  	 	}
  	 	else
  	 	{
  	 		var hashvo = new ParameterSet();
			hashvo.setValue("a0100",a0100);
			hashvo.setValue("zp_pos_id",a_zpPosID);
			hashvo.setValue("operate","set");
			var In_parameters="state="+state; 
			var request = new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:returnInfo2browse,functionId:'3000000206'},hashvo);
  	 	}
   }
function delBrowse(a0100)
   {
   			if(!confirm(GZ_REPORT_CONFIRMDELETE))
   				return;
   		  var zpPosIDs=eval("document.employPortalForm.zpPosID");
         var a_zpPosID="";
         if(zpPosIDs)
         {
         if(zpPosIDs.length)
         {
	         for(var i=0;i<zpPosIDs.length;i++)
	          {
	         		if(zpPosIDs[i].checked==true)
	       		    	a_zpPosID=zpPosIDs[i].value;	
	       
	         }
	      }
	      else
	      {
	        	a_zpPosID=document.employPortalForm.zpPosID.value;
	    
	       }
	       }
	       else
	       {
	        a_zpPosID="-1";
	       }
   			var hashvo = new ParameterSet();
			hashvo.setValue("a0100",a0100);
			hashvo.setValue("zp_pos_id",a_zpPosID);
			hashvo.setValue("operate","del");
			var In_parameters="state=dd"; 
			var request = new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:returnInfo2browse,functionId:'3000000206'},hashvo);
           
   }
function ysmethod(tableid)
{
	// alert(tableid)
   if(tableid==null||tableid == '' || tableid=="#")
	{
	      alert(NOT_CONFIGURE_TABLE+"！");
	      return;
	}
	tabid=tableid;
	 
   window.setTimeout('previewTableByActive()',0);   
}
function show(eventdiv,showdiv)
{
   if(eventdiv){
 
	  with(eventdiv)
	  {
		x=offsetLeft;
		y=offsetTop;
		objParent=offsetParent;
		while(objParent.tagName.toUpperCase()!= "BODY")
		{
			x+=objParent.offsetLeft;
			y+=objParent.offsetTop;
			objParent = objParent.offsetParent;
		}
		y+=offsetHeight-1;
	   }
	  }
    if(showdiv){
    
	  with(showdiv.style)
	  {
		pixelLeft=x;
		pixelTop=y;
		visibility='';
	  }
	}
	var id=eventdiv.id.substring(3,eventdiv.id.length);

	if(document.getElementById("img"+id)){
		var img= document.getElementById("img"+id);
		var src=img.src;
		var type=src.substr(src.lastIndexOf(".")+1);
		if(src.indexOf("_1."+type)!=-1){
			
		}else{
			var ss=src.substring(0,src.indexOf("."+type));
			img.src=ss+"_1."+type;
		}
	}
}
function getInternet()    
{    
	if(navigator.userAgent.indexOf("MSIE")>0) {    
	              return "MSIE";       // IE浏览器
	}  
	if(isFirefox=navigator.userAgent.indexOf("Firefox")>0){    
	              return "Firefox";     // Firefox浏览器
	}  
	if(isSafari=navigator.userAgent.indexOf("Safari")>0) {    
	              return "Safari";      // Safan浏览器
	}  
	if(isCamino=navigator.userAgent.indexOf("Camino")>0){    
	              return "Camino";   // Camino浏览器
	}  
	if(isMozilla=navigator.userAgent.indexOf("Gecko/")>0){    
	              return "Gecko";    // Gecko浏览器
	}    
}   
	try { 
	  request = new XMLHttpRequest(); 
	} catch (trymicrosoft) { 
	  try { 
	    request = new ActiveXObject("Msxml2.XMLHTTP");// 支持microsoft
	  } catch (othermicrosoft) { 
	    try { 
	      request = new ActiveXObject("Microsoft.XMLHTTP");// 支持非microsoft
	    } catch (failed) { 
	      request = false;  
	    }  
	} 
} 
if(!request) 
  alert("Error!!游览器不安全,请选择较高版本游览器!");


function show1(eventdiv)
{
	var isie=getInternet();
	var id=eventdiv.id.substring(3,eventdiv.id.length);
	
	if(document.getElementById("img"+id)){
		var img= document.getElementById("img"+id);
		var src=img.src;
		var type=src.substr(src.lastIndexOf(".")+1);
		
		
		if(src.indexOf("_1."+type)!=-1){
			
		}else{
			var ss=src.substring(0,src.indexOf("."+type));
			img.src=ss+"_1."+type;
			var co=document.getElementById("a"+id);
			if(isie=='MSIE')
				co.className='els';
			else
				co.setAttribute('class',"els");
			
		}
	}
	if(document.getElementById("ul"+id)){
		document.getElementById("ul"+id).style.display="block";
		document.getElementById("ul"+id).style.zIndex=1000;
	}
	
}
function hide(hidediv)
{
    if(hidediv){
	   hidediv.style.visibility='hidden';
	}
}
function hide1(obj,hidediv)
{
    if(hidediv){
	   hidediv.style.visibility='hidden';
	}
	var id=obj.id.substring(3,obj.id.length);
	if(document.getElementById("img"+id)){
		var img= document.getElementById("img"+id);
		var src=img.src;
		var type=src.substr(src.lastIndexOf(".")+1);
		var ss=src.substring(0,src.indexOf("_1."+type));
		img.src=ss+"."+type;
	}
}
function hide2(obj,flag)
{
	if(parent.isHide!=obj.id||flag){
		var isie=getInternet();
		var id=obj.id.substring(3,obj.id.length);
		if(document.getElementById("img"+id)){
			var img= document.getElementById("img"+id);
			var src=img.src;
			var type=src.substr(src.lastIndexOf(".")+1);
			if(src.indexOf("_1.")>-1){
				var ss=src.substring(0,src.indexOf("_1."+type));
				img.src=ss+"."+type;
			}
			var co=document.getElementById("a"+id);
			
			if(isie=='MSIE')
				co.className='yes';
			else
				co.setAttribute('class',"yes");
		}
		if(document.getElementById("ul"+id))
			document.getElementById("ul"+id).style.display="none";
	}
}

function getcontent(chl_no)
{    
	var content=document.getElementById("nav1");
	var item=content.getElementsByTagName('ul');
	var items=item[0].getElementsByTagName('li');
	for(var n=0;n<items.length;n++){
		var node = items[n];
		if((node.id!="div"+chl_no)&&node.getElementsByTagName('a')[0].className=="els")
			hide2(node,true);
	}
	parent.isHide = "div"+chl_no;
     var hashvo=new ParameterSet();
     hashvo.setValue("chl_no",chl_no);	
     var request=new Request({asynchronous:false,onSuccess:isSuccess,functionId:'1010021201'},hashvo);        
}

function isSuccess(outparamters)
{
	var cms_txt=outparamters.getValue("cms_txt");
	var cc=document.getElementById('cms_pnl');
	if(document.getElementById('winpop')){
	document.getElementById('winpop').style.display='none';
	}

	if(document.getElementById('cms_pnl'))
		document.getElementById('cms_pnl').style.minHeight='800px';
	else
		document.getElementById('content').style.minHeight='800px';
	
	if(document.getElementById('cms_pnl'))
		AjaxBind.bind(document.getElementById('cms_pnl'),cms_txt);
	else
		AjaxBind.bind(document.getElementById('content'),cms_txt);
}
function changeDisplay(unitCode,j)
{
    /** 图片对象 */
    var trone=document.getElementById(unitCode+"_img");
    for(var i=0;i<j;i++)
    {  
      var ig=document.getElementById(unitCode+"_"+i);
      if(ig.style.display=='none')
      {
        ig.style.display='block';
        trone.src='/images/tree_collapse.gif';
      }
      else
      {
         ig.style.display='none';
         trone.src='/images/tree_expand.gif';
      }
    }
}
function getPasswordZPnew(dbName,userNameCloumn,passWordCloumn)
{
	window.location="/module/hire/resetPassword.html"; 
// var
// strurl="/hire/hireNetPortal/search_zp_position.do?br_getpassword=get`dbname="+dbName+"`userC="+userNameCloumn+"`passC="+passWordCloumn;
// var iframe_url="/templates/index/iframe_query.jsp?src="+strurl;
// if(isIE())
// var flag= window.showModalDialog(strurl, arguments, "dialogWidth:450px;
// dialogHeight:250px;resizable:no;center:yes;scroll:no;status:no;");
// else
// window.open(strurl,
// arguments,"width=450px,height=250px,top="+glotop+",left="+gloleft+",resizable=no,center=yes,scroll=yes,location=no,status=no");
	// window.location.href=window.location.href;
   
}
function closeMap() {
	var bg = document.getElementById("mapBgLayer");
	var con = document.getElementById("mapLayer");
	

	bg.style.display = "none";
	con.style.display = "none";
}
function rediract(str,z0311,z0301,posname,unitname,type,dmlunitcode){
		var ss=getCookie('hjsjpos');
		if(ss!=null&&ss.length!=0){
			if(ss.indexOf("`"+z0301+"`")!=-1){
			}else{
				ss+=z0301+"`";
				setCookie('hjsjpos',ss);
			}

		}else{
			setCookie('hjsjpos',"`"+z0301+"`");
		}
		z0311=getEncodeStr(z0311);
		z0301=getEncodeStr(z0301);
		var url="/hire/hireNetPortal/search_zp_position.do?b_posDesc=link"+str+"&posID="+z0311+"&z0301="+z0301+"&posName="+$URL.encode(posname)+"&unitName="+$URL.encode(unitname)+"&returnType="+type;
		if(dmlunitcode!=null&&dmlunitcode.length!=0)
		url=url+"&selunitcode="+dmlunitcode;
		document.employPortalForm.action=url;
		document.employPortalForm.submit();
}
function rediractForRecommend(z0301,posname,unitname,type,dmlunitcode){
	var ss=getCookie('hjsjpos');
	if(ss!=null&&ss.length!=0){
		if(ss.indexOf("`"+z0301+"`")!=-1){
		}else{
			ss+=z0301+"`";
			setCookie('hjsjpos',ss);
		}

	}else{
		setCookie('hjsjpos',"`"+z0301+"`");
	}
	z0301=getEncodeStr(z0301);
	var url="/hire/hireNetPortal/search_zp_position.do?b_posDesc=link&posID=&reCommendoption="+reCommendoption+"&z0301="+z0301+"&posName="+posname+"&unitName="+unitname+"&returnType="+type;
	if(dmlunitcode!=null&&dmlunitcode.length!=0)
	url=url+"&selunitcode="+dmlunitcode;
	document.employPortalForm.action=url;
	document.employPortalForm.submit();
}
function getCookie(Key){
	var search = Key + "=";
	begin = document.cookie.indexOf(search);
	if (begin != -1) {
	  begin += search.length;
	  end = document.cookie.indexOf(";",begin);
	  if (end == -1) end = document.cookie.length;
	  return document.cookie.substring(begin,end);
	}
}


var today = new Date();
var expireDay = new Date();
var msPerMonth = 24*60*60*1000*31;
expireDay.setTime( today.getTime() + msPerMonth );
function setCookie(Key,value) {
	document.cookie = Key + "=" + value + ";expires=" + expireDay.toGMTString();
}
function hasresume(){
	var ss=getCookie('hjsjpos');
		ss=getEncodeStr(ss);
	if(ss!=null&&ss.length!=0){
		var url="/hire/hireNetPortal/search_zp_position.do?b_query=link&z0301="+ss+"&returnType=resume";
		document.employPortalForm.action=url;
		window.setTimeout("document.employPortalForm.submit()",0);// 采用延时加载是为了使ie6能够跳转
		
	}else{
		alert("您还没浏览过任何职位或专业！");
		return;
	}
}
function changebg(url,id){
	if(document.getElementById("img"+id)){
		var img=document.getElementById("img"+id);
		var src=img.src;
		var type=src.substr(src.lastIndexOf(".")+1);
		if(src.indexOf("_1."+type)!=-1){
			
		}else{
			var ss=src.substring(0,src.indexOf("."+type));
			img.src=ss+"_1."+type;
		}
		if(document.employPortalForm){
			if(url.indexOf('.jsp?')!=-1){
				document.employPortalForm.action=url+"&chl_id="+id;;
			}else{
				document.employPortalForm.action=url+"&chl_id="+id;
			}
			
			document.employPortalForm.submit();
		}else{
			if(url.indexOf('.jsp?')!=-1){
				window.location.href=url+"&chl_id="+id;;
			}else{
				if(url.indexOf('.jsp')!=-1){
					window.location.href=url;
				}else{
					window.location.href=url+"&chl_id="+id;
				}
			}
		
		}
	
	}else{
		if(document.employPortalForm){
			if(url.indexOf('jsp')!=-1){
				document.employPortalForm.action=url;
			}else{
				document.employPortalForm.action=url+"&chl_id="+id;
			}
			document.employPortalForm.submit();
		}else{
			if(url.indexOf('jsp')!=-1){
				window.location.href=url;
			}else{
				window.location.href=url+"&chl_id="+id;
			}
		}
	}
}
 
 function showdiv(code){
 	var lastpage=document.getElementById("lastpage").value;

 		 	
 	
 	if(code=='up'){
 	var html="";
 		if(document.getElementById("content"+(parseInt(currentpage)-1))){	
	 		if(document.getElementById("content"+parseInt(currentpage)))
		 	{
		 		var currcon=document.getElementById("content"+parseInt(currentpage))
		 		currcon.style.display='none';
		 	}
		 	
	 		var cont =document.getElementById("content"+(parseInt(currentpage)-1));
	 		cont.style.display="block";
	 		
	 		
	 		html=pain(parseInt(currentpage),code,maxbutton,lastpage,4)
 				var buttonlist=document.getElementById("buttonlist");
	 			buttonlist.innerHTML='';
	 			buttonlist.innerHTML=html;
 			currentpage=parseInt(currentpage)-1;
 		}else{
 			if((parseInt(currentpage)-1)==0){
 				alert("已是第一页！");
 			}else
 				alert("没有第"+(parseInt(currentpage)-1)+"页的内容！");
 			return;
 		}
 	}else if(code=='next'){
 		if((parseInt(currentpage)+1)-maxbutton>=1&&parseInt(currentpage)<=lastpage){
 			var html="";
 			if(parseInt(currentpage)==lastpage){
 				alert("已经是最后一页！");
 				return;
 			}
 			var pa =parseInt(currentpage)+1;
 			
 			if(document.getElementById("content"+(pa))){	
		 		if(document.getElementById("content"+parseInt(currentpage)))
			 	{
			 		var currcon=document.getElementById("content"+parseInt(currentpage))
			 		currcon.style.display='none';
			 	}
			 	
		 		var cont =document.getElementById("content"+(pa));
		 		cont.style.display="block";
	 		}else{
	 			alert("没有第"+(parseInt(currentpage)+1)+"页的内容！");
	 			return;
	 		}
 	
			html=pain(parseInt(currentpage),code,maxbutton,lastpage,3)
 			var buttonlist=document.getElementById("buttonlist");
 			buttonlist.innerHTML='';
 			buttonlist.innerHTML=html;
 			if(currentpage<lastpage){
 				currentpage=parseInt(currentpage)+1;
			}

 		}else{
	 		if(document.getElementById("content"+(parseInt(currentpage)+1))){	
		 		if(document.getElementById("content"+parseInt(currentpage)))
			 	{
			 		var currcon=document.getElementById("content"+parseInt(currentpage))
			 		currcon.style.display='none';
			 	}
			 	
		 		var cont =document.getElementById("content"+(parseInt(currentpage)+1));
		 		cont.style.display="block";
		 		var xx=document.getElementById("Ppage"+(parseInt(currentpage)));
		 		 xx.setAttribute('className', 'Pmodern1')
		 		currentpage=parseInt(currentpage)+1;
	 		}else{
	 			alert("没有第"+(parseInt(currentpage)+1)+"页的内容！");
	 			return;
	 		}
	 		 var xx=document.getElementById("Ppage"+parseInt(currentpage));
			 xx.setAttribute('className', 'Pmodern')
 		}
 	}else if(code=='tz'){
 		var tt=document.getElementsByName('PageV');
 		var value=tt[0].value;
 		var html="";
 		if(trim(value).length==0){
 			alert("请输入调跳转页码！");
 			return;
 		}
 		if(isqcode(value)){
 			value=zuzhuang(value);
 			var r = /^[0-9]*[1-9][0-9]*$/;	
			if(r.test(value)){
			
			}else{			
				alert("请输入正确的页码！");
				return;
			}
 		}else{
 			var r = /^[0-9]*[1-9][0-9]*$/;	
			if(r.test(value)){
			
			}else{			
				alert("请输入正确的页码！");
				return;
			}
 		}
	 	if(document.getElementById("content"+value)){
	 		if(document.getElementById("content"+parseInt(currentpage)))
		 	{
		 		var currcon=document.getElementById("content"+parseInt(currentpage))
		 		currcon.style.display='none';
		 	}
 			var cont =document.getElementById("content"+value);
 			cont.style.display="block";
 			if(parseInt(currentpage)<maxbutton&&value<=parseInt(currentpage)){
 				 var xx=document.getElementById("Ppage"+value);
				 xx.setAttribute('className', 'Pmodern')
				 var xx=document.getElementById("Ppage"+parseInt(currentpage));
				xx.setAttribute('className', '');
 			}else{
	 			html=pain(parseInt(currentpage),value,maxbutton,lastpage,2)
	 			
	 			if(html==null||trim(html).length==0){
	 				 var xx=document.getElementById("Ppage"+value);
					 xx.setAttribute('className', 'Pmodern');
					 var xx=document.getElementById("Ppage"+parseInt(currentpage));
					 xx.setAttribute('className', '');
	 			}else{
	 				var buttonlist=document.getElementById("buttonlist");
	 				buttonlist.innerHTML='';
	 				buttonlist.innerHTML=html;
	 			
	 			 }
 			}
	 		currentpage=value;
	 	}else{
	 		alert("没有第"+value+"页的内容!！");
	 		return;
	 	}
	 	
 	}else{
 		var html="";
 		if(code=='qsl'||code=='hsl'){
 			return;
 		}
	 	if(document.getElementById("content"+code)){

		 	if(document.getElementById("content"+currentpage))
		 	{
		 		var currcon=document.getElementById("content"+parseInt(currentpage))
		 		currcon.style.display='none';
		 	}		
	 		var cont =document.getElementById("content"+code);
	 		cont.style.display="block";
	 		
	 		html=pain(parseInt(currentpage),code,maxbutton,lastpage,2)
	 		if(html!=null&&html.length!=0){
		 		var buttonlist=document.getElementById("buttonlist");
	 			buttonlist.innerHTML='';
	 			buttonlist.innerHTML=html;
 			}else{
 				var xx=document.getElementById("Ppage"+currentpage);
	 			 xx.setAttribute('className', 'Pmodern1')
 			}currentpage=code;
	 	}else{	 		 		
	 			alert("没有第"+code+"页的内容！");
	 			return;	 			
	 	}
	 	
	 }
	var ot=document.getElementById("cms_pnl");ot.focus();

 }
 
 function pain(currentpage,code,maxbutton,lastpage,flag){
 	var html='';
 	if(flag=='1'){
 		
 		if(code==1){
	 		for(var i=1;i<=maxbutton;i++){		
				html=html+"<span class=\"Ppage\">";		
				html=html+"<a href=\"javascript:void(0);\" onclick='showdiv("+(i)+");' id='Ppage"+(i)+"'"
				if(i==code){
					html=html+"class=\"Pmodern\" target=\"_self\">"+(i)+"</a>";
				}else{
					html=html+"target=\"_self\">"+(i)+"</a>";
				}
				html=html+"</span>";
		 	}
 			if(maxbutton<lastpage){
 				html=html+"<span class=\"Ppage\"><a href=\"javascript:void(0);\" onclick=\"showdiv('hsl')\" id='Pagesl' target='_self'>...</a></span>";
 			}
 			
 		}
 		if(code==lastpage){
 			if(maxbutton<lastpage){
 				html=html+"<span class=\"Ppage\"><a href=\"javascript:void(0);\" onclick=\"showdiv('qsl')\" id='Pagesl' target='_self'>...</a></span>";
 			}
 			for(var i=maxbutton-1;i>=0;i--){		
					html=html+"<span class=\"Ppage\">";		
					html=html+"<a href=\"javascript:void(0);\" onclick='showdiv("+(code-i)+");' id='Ppage"+(code-i)+"'"
					if(i==0){
						html=html+"class=\"Pmodern\" target=\"_self\">"+(code-i)+"</a>";
					}else{
						html=html+"target=\"_self\">"+(code-i)+"</a>";
					}
					html=html+"</span>";				
	 		}
 		}
 		if(code>currentpage-maxbutton&&code<currentpage){
 			
 		}
 	}
 	if(flag=='2'){
 		if(code>parseInt(currentpage)-maxbutton&&code<parseInt(currentpage)){
 		}else{
 			if(code<maxbutton){
 				for(var i=1;i<=maxbutton;i++){	
					html=html+"<span class=\"Ppage\">";		
					html=html+"<a href=\"javascript:void(0);\" onclick='showdiv("+(i)+");' id='Ppage"+(i)+"'"
					if(i==code){
						html=html+"class=\"Pmodern\" target=\"_self\">"+(i)+"</a>";
					}else{
						html=html+"target=\"_self\">"+(i)+"</a>";
					}
					html=html+"</span>";
	 			}
	 			html=html+"<span class=\"Ppage\"><a href=\"javascript:void(0);\" onclick=\"showdiv('hsl')\" id='Pagesl' target='_self'>...</a></span>";
 			}else{	
 				if(code-maxbutton>=1){
 					html=html+"<span class=\"Ppage\"><a href=\"javascript:void(0);\" onclick=\"showdiv('qsl')\" id='Pagesl' target='_self'>...</a></span>";
 				}
 				for(var i=maxbutton-1;i>=0;i--){		
					html=html+"<span class=\"Ppage\">";		
					html=html+"<a href=\"javascript:void(0);\" onclick='showdiv("+(code-i)+");' id='Ppage"+(code-i)+"'"
					if(i==0){
						html=html+"class=\"Pmodern\" target=\"_self\">"+(code-i)+"</a>";
					}else{
						html=html+"target=\"_self\">"+(code-i)+"</a>";
					}
					html=html+"</span>";				
	 			}
	 			if(code<lastpage){
 					html=html+"<span class=\"Ppage\"><a href=\"javascript:void(0);\" onclick=\"showdiv('qsl')\" id='Pagesl' target='_self'>...</a></span>";
 				}
 			}
 		}
 	}
 	
 	if(flag=='3'){
 		if(parseInt(currentpage)+1-maxbutton>0){
 			html=html+"<span class=\"Ppage\"><a href=\"javascript:void(0);\" onclick=\"showdiv('qsl')\" id='Pagesl' target='_self'>...</a></span>";
 		}
 		for(var i=maxbutton-1;i>=0;i--){		
			html=html+"<span class=\"Ppage\">";		
			html=html+"<a href=\"javascript:void(0);\" onclick='showdiv("+(parseInt(currentpage)+1-i)+");' id='Ppage"+(parseInt(currentpage)+1-i)+"'"
			if(i==0){
				html=html+"class=\"Pmodern\" target=\"_self\">"+(parseInt(currentpage)+1-i)+"</a>";
			}else{
				html=html+"target=\"_self\">"+(parseInt(currentpage)+1-i)+"</a>";
			}
			html=html+"</span>";
 		}
 		if(currentpage!=lastpage-1){
 			html=html+"<span class=\"Ppage\"><a href=\"javascript:void(0);\" onclick=\"showdiv('hsl')\" id='Pagesl' target='_self'>...</a></span>";
		}
 	}
 	if(flag=='4'){
 			if(parseInt(currentpage)-1<=maxbutton){
 				for(var i=1;i<=maxbutton;i++){		
						html=html+"<span class=\"Ppage\">";		
						html=html+"<a href=\"javascript:void(0);\" onclick='showdiv("+(i)+");' id='Ppage"+(i)+"'"
						if(i==currentpage-1){
							html=html+"class=\"Pmodern\" target=\"_self\">"+(i)+"</a>";
						}else{
							html=html+"target=\"_self\">"+(i)+"</a>";
						}
						html=html+"</span>";
	 				}
 			}else{
 				html=html+"<span class=\"Ppage\"><a href=\"javascript:void(0);\" onclick=\"showdiv('qsl')\" id='Pagesl' target='_self'>...</a></span>";
 				for(var i=maxbutton-1;i>=0;i--){		
					html=html+"<span class=\"Ppage\">";		
					html=html+"<a href=\"javascript:void(0);\" onclick='showdiv("+(parseInt(currentpage)-i)+");' id='Ppage"+(parseInt(currentpage)-i)+"'"
					if(i==0){
						html=html+"class=\"Pmodern\" target=\"_self\">"+(parseInt(currentpage)-1-i)+"</a>";
					}else{
						html=html+"target=\"_self\">"+(parseInt(currentpage)-1-i)+"</a>";
					}
					html=html+"</span>";
 				}
 			}
 			html=html+"<span class=\"Ppage\"><a href=\"javascript:void(0);\" onclick=\"showdiv('qsl')\" id='Pagesl' target='_self'>...</a></span>";
 	}
 	return html;
 	
 }
 function hasLongin(){
 	var name=getCookie("hjsjloginName");
 	var word=getCookie("hjsjpassword");
 	document.employPortalForm.loginName.value=name;
	document.employPortalForm.password.value=word;
	document.employPortalForm.action="/hire/hireNetPortal/search_zp_position.do?b_login=login";
	document.employPortalForm.submit();
 }
 function isqcode(Str){
	for(var   i=0;i <Str.length;i++) 
        { 
      		strCode=Str.charCodeAt(i); 
            if((strCode> 65248)||(strCode==12288)) 
            { 
               return true; 
            } 
        } 
}
function zuzhuang(str){
	var   result= "";
    for   (var   i   =   0;   i   <   str.length;   i++) 
    { 
            if(str.charCodeAt(i)==12288) 
            { 
                result+=   String.fromCharCode(str.charCodeAt(i)-12256); 
                continue; 
            } 
            if(str.charCodeAt(i)> 65280   &&   str.charCodeAt(i) <65375) 
                    result+=String.fromCharCode(str.charCodeAt(i)-65248); 
            else 
            result+=String.fromCharCode(str.charCodeAt(i)); 
    }       
    return result; 
}
function downfujian(id){
	window.location="/selfservice/welcome/downboardview?id="+id;
}
	function showcont(id,flag){
	if(flag=='1'){
		var fir=document.getElementById("ficont");
		fir.style.display="none";
		var sec=document.getElementById(id+"dml");
		sec.style.display='block';
	}else{
		var fir=document.getElementById("ficont");
		fir.style.display="none";
		var sec=document.getElementById(id+"dmliner");
		sec.style.display='block';
	}
		
	}
	function  hideandshow(id,flag){
		if(flag=='1'){
			var fir=document.getElementById("ficont");
			fir.style.display="block";
			var sec=document.getElementById(id+"dml");
			sec.style.display='none'
		}else{
			var fir=document.getElementById("ficont");
			fir.style.display="block";
			var sec=document.getElementById(id+"dmliner");
			sec.style.display='none'
		}
		
	}
	
 document.write("<script type='text/javascript' src='../../ext/aes.js'></script>");
 function  hireloginvalidate(activeNum){
	var loginName=document.getElementById("loginName").value;
	var password=document.getElementById("password").value;
	var validatecode=document.getElementById("validatecode").value;
	var hour = document.getElementById("hour").value;
	password = "MD5`" + CryptoJS.MD5(password + hour);
	var hireChannel = "";
	if(document.getElementsByName("hireChannel")[0]!=null)
		hireChannel = document.getElementsByName("hireChannel")[0].value;
	if(hireChannel!="headHire"&&!fucEmailchk(document.employPortalForm.loginName.value))
	{
		Ext.showAlert(EMAIL_ADDRESS_IS_WRONG+"！");
			return;
	}
	if(document.employPortalForm.password.value==''||document.employPortalForm.password.value==' ')
	{
		Ext.showAlert(PASSWORD_IS_NOT_FILL+"！");
			return;
	}
	if(validatecode==''||validatecode==' ')
	{
		Ext.showAlert("验证码不能为空！");
			return;
	}
	var str="<form action='/servlet/hirelogin/HireLoginValidate' method=post name=formx1 style='display:none'>";
	str+="<input type='hidden' name='loginName' value="+getEncodeStr(loginName)+" />";
	str+="<input type='hidden' name='password' value="+getEncodeStr(password)+" />";
	str+="<input type='hidden' name='validatecode' value='"+getEncodeStr(validatecode)+"' />";
	str+="<input type='hidden' name='activeNum' value='"+getEncodeStr(activeNum)+"' />";
	str+="</form>";
	
	document.getElementById("biaodan").innerHTML=str;
	document.formx1.submit();
	// /window.location.href="/servlet/hirelogin/HireLoginValidate?loginName="+getEncodeStr(loginName)+"&password="+getEncodeStr(password)+"&validatecode="+getEncodeStr(validatecode)+"&activeNum="+activeNum;
	if(activeNum==1){
		jindu1();
	}
  }
 function jindu1(){
	// 新加的，屏蔽整个页面不可操作
	document.all.ly.style.display="";   
	document.all.ly.style.width=document.body.clientWidth;   
	document.all.ly.style.height=document.body.clientHeight; 
	
	var x=(window.screen.width-700)/2;
    var y=(window.screen.height-500)/2; 
// / var waitInfo=eval("wait1");
// / waitInfo.style.top=y;
// / waitInfo.style.left=x;
// / waitInfo.style.display="";
}
	var count = 0;
	var timeID;
	var gpersonlist=null;
	
	// 打印准考证
	function printExamNo (){
		if(cardid==null||cardid == '' || cardid=="#")
		{
		      alert(NOT_CONFIGURE_TABLE+"！");
		      return;
		}
		printPDF(cardid);
	}
	// 导出PDF文件
	function printPDF (cardId){
		if(cardId==null||cardId == '' || cardId=="#")
		{
		      alert(NOT_CONFIGURE_TABLE+"！");
		      return;
		}
		var hashvo=new ParameterSet();
		hashvo.setValue("nid",a0100);
		hashvo.setValue("userbase",nbase);
		hashvo.setValue("userpriv","zpselfinfo");
		hashvo.setValue("fieldpurv","1");
		hashvo.setValue("istype","1");        
		hashvo.setValue("queryType","1");
		hashvo.setValue("infokind","1");
		hashvo.setValue("cardid",cardId);
		var In_paramters="exce=PDF";  
	    var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showPDF,functionId:'ZP0000002557'},hashvo);
	}
	
	function showPDF(outparamters)
	{
	 
	    var url=outparamters.getValue("url");
	    var win=open("/servlet/DisplayOleContent?openflag=false&filename="+url,"pdf");	
	}
   function showPrint(outparamters)
{
   if(outparamters)
      gpersonlist=outparamters.getValue("personlist");
   var personlist=gpersonlist;
   if(!AxManager.setup("chajian", "CardPreview1", 0, 0, showPrint, AxManager.cardpkgName, AxManager.showDownloadDialog))
      return;
   var obj = document.getElementById('CardPreview1');  
   if(obj==null)
   {
      alert("没有下载打印控件，请设置IE重新下载！");
      return;
   }
   try {
   initCard();
   obj.SetCardID(tabid);
   obj.SetDataFlag("1");
   obj.SetNBASE("${employPortalForm.dbName}");
   obj.ClearObjs();   
   if(personlist!=null&&personlist.length>0)
   {
     for(var i=0;i<personlist.length;i++)
     {
       obj.AddObjId(personlist[i].dataValue);
     }
   }
   try { obj.SetJSessionId(AxManager.getJSessionId()); } catch(err) {}
   obj.ShowCardModal();
   }catch (e) {

}
   
}


// 猎头招聘添加新的功能
function addPersonResume(){
	document.employPortalForm.action="/hire/hireNetPortal/recommend_resume.do?b_addPersonResume=add&opt=add";
	document.employPortalForm.submit();
}
function deletePersonResume(){
	var str="";
	var viewstr="";
	for(var i=0;i<document.employPortalForm.elements.length;i++)
	{
		if(document.employPortalForm.elements[i].type=="checkbox")
		{					
			var ff = employPortalForm.elements[i].name.substring(0,28);						
			if(document.employPortalForm.elements[i].checked==true && ff=='recommendUserListForm.select')
			{
				var indexselect=employPortalForm.elements[i].name.substring(29,30);
				var hiddenInput = document.getElementById("select"+indexselect);
				var hiddenvalue=hiddenInput.value;
				var hiddenViewvalue=hiddenInput.getAttribute("viewvalue");
				str = str+hiddenvalue+",";
				viewstr =viewstr+hiddenViewvalue+",";
			}
		}
	}
	if(str.length==0)
	{									
		alert("请选择要删除的记录！");				
		return false;
	}
	else
	{
		  if(confirm('确认删除所选择的人员吗？')){
			  // 这里先使用异步进行判断一下,是否能删除,不能删除直接进行js提示
			  var hashvo=new ParameterSet();
			  hashvo.setValue("selecta0100s",str);
			  hashvo.setValue("views",viewstr);
		      var request=new Request({asynchronous:false,onSuccess:deleteResume,functionId:'90100170103'},hashvo); 
		  }
	}
	
}
function deleteResume(outparamters){
	var infor = outparamters.getValue("information");
	if(infor.length>0){
		alert(infor);
		return;
	}
	document.employPortalForm.action='/hire/hireNetPortal/recommend_resume.do?b_deletePersonResume=delete';
	document.employPortalForm.submit();
}
function recommendResume(a0100,userName){
	 var hashvo=new ParameterSet();
	 hashvo.setValue("a0100s",a0100);
	 hashvo.setValue("userNames",userName);
     var request=new Request({asynchronous:false,onSuccess:continueRecommend,functionId:'90100170105'},hashvo); 
}
function batchRecommendValiate(){
	var str="";
	var viewstr="";
	for(var i=0;i<document.employPortalForm.elements.length;i++){
		if(document.employPortalForm.elements[i].type=="checkbox"){					
			var ff = employPortalForm.elements[i].name.substring(0,28);						
			if(document.employPortalForm.elements[i].checked==true && ff=='recommendUserListForm.select'){
				var indexselect=employPortalForm.elements[i].name.substring(29,employPortalForm.elements[i].name.length-1);
				var hiddenInput = document.getElementById("select"+indexselect);
				var hiddenvalue=hiddenInput.value;
				var hiddenViewvalue=hiddenInput.getAttribute("viewvalue");
				str = str+hiddenvalue+",";
				viewstr =viewstr+hiddenViewvalue+",";
			}
		}
	}
	if(str.length==0){									
		alert("请选择要推荐的记录！");				
		return false;
	}else{
	  var hashvo=new ParameterSet();
	  hashvo.setValue("a0100s",str);
	  hashvo.setValue("userNames",viewstr);
      var request=new Request({asynchronous:false,onSuccess:continueRecommend,functionId:'90100170105'},hashvo); 
	}
}
/** 校验成功跳转到选择职位界面* */
function continueRecommend(outparamters){
	var infor = outparamters.getValue("infor");
	var a0100 = outparamters.getValue("a0100s");
	var userName = outparamters.getValue("userNames");
	var from = outparamters.getValue("from");
	if(infor.length>0){
		alert(infor);
		return;
	}
	if(from=='one'){
		document.employPortalForm.action='/hire/hireNetPortal/recommend_resume.do?b_recommendPosition=add&reCommendoption=one&a0100='+a0100+"&userName="+userName;
	}else{
		document.employPortalForm.action='/hire/hireNetPortal/recommend_resume.do?b_recommendPosition=add&reCommendoption=more';
	}
	document.employPortalForm.submit();
}
/** 检查当前选中的人员中是否有人申请了改职位* */
function recommendCheck(z0301,posName){
		var hashvo=new ParameterSet();
		hashvo.setValue("reCommendoption",reCommendoption);
		 hashvo.setValue("a0100s",recommendA0100s);
		 hashvo.setValue("userNames",recommendUserNames);
		 hashvo.setValue("z0301",z0301);
		 hashvo.setValue("posName",posName);
	    var request=new Request({asynchronous:false,onSuccess:checkRecommendOk,functionId:'90100170107'},hashvo); 
}
function checkRecommendOk(outparamters){
	var infor = outparamters.getValue("infor");
	var a0100 = outparamters.getValue("a0100s");
	var userName = outparamters.getValue("userNames");
	var z0301 = outparamters.getValue("z0301");
	var from = outparamters.getValue("from");
	if(infor.length>0){
		alert(infor);
		return;
	}
	if(from=='one'){
		document.employPortalForm.action="/hire/hireNetPortal/recommend_resume.do?b_recommendAddPosition=add&reCommendoption=one&z0301="+z0301+"&a0100s="+a0100+"&userName="+userName;
	}else{
		document.employPortalForm.action="/hire/hireNetPortal/recommend_resume.do?b_recommendAddPosition=add&reCommendoption=more&z0301="+z0301;
	}
	
	document.employPortalForm.submit();
}
function headerBack(){
	document.employPortalForm.action="/hire/hireNetPortal/recommend_resume.do?b_recommendResume=back";
	document.employPortalForm.submit();
}
/** 开始为职位推荐人员* */
function recommendForposition(z0301,posName){
	document.employPortalForm.action="/hire/hireNetPortal/recommend_positionResume.do?b_recommendResume=recommend&z0301="+z0301+"&posName="+posName;
	document.employPortalForm.submit();
}
function batchRecommendValiateForposition(z0301,posName){
	var str="";
	var viewstr="";
	for(var i=0;i<document.employPortalForm.elements.length;i++){
		if(document.employPortalForm.elements[i].type=="checkbox"){					
			var ff = employPortalForm.elements[i].name.substring(0,28);						
			if(document.employPortalForm.elements[i].checked==true && ff=='recommendUserListForm.select'){
				var indexselect=employPortalForm.elements[i].name.substring(29,30);
				var hiddenInput = document.getElementById("select"+indexselect);
				var hiddenvalue=hiddenInput.value;
				var hiddenViewvalue=hiddenInput.getAttribute("viewvalue");
				str = str+hiddenvalue+",";
				viewstr =viewstr+hiddenViewvalue+",";
			}
		}
	}
	if(str.length==0){									
		alert("请选择要推荐的记录！");				
		return false;
	}else{
	  var hashvo=new ParameterSet();
	  hashvo.setValue("a0100s",str);
	  hashvo.setValue("userNames",viewstr);
	  hashvo.setValue("z0301",z0301);
	  hashvo.setValue("posName",posName);
      var request=new Request({asynchronous:false,onSuccess:continueRecommendposition,functionId:'90100170109'},hashvo); 
	}
}
function continueRecommendposition(outparamters){
	var infor = outparamters.getValue("infor");
	var z0301 = outparamters.getValue("z0301");
	if(infor.length>0){
		alert(infor);
		return;
	}
	document.employPortalForm.action="/hire/hireNetPortal/recommend_positionResume.do?b_recommendAddPosition=add&reCommendoption=more&z0301="+z0301;
	document.employPortalForm.submit();
}

function validataCodeReload(){
	var url = document.getElementById('vaildataCode').src;
	document.getElementById('vaildataCode').src = url+"&id=" + Math.random(); 
}

function getZpAccounts() {
	window.location="/module/hire/GetZpAccounts.html";    
}

function getShow(showId,hiddenId,hiddenIdOther) {
	document.getElementById(showId).style.display = "block";
	document.getElementById(hiddenId).style.display = "none";
	document.getElementById(hiddenIdOther).style.display = "none";
}

function KeyDown()
{
    var theEvent = window.event || arguments.callee.caller.arguments[0]; //谷歌能识别event，火狐识别不了，所以增加了这一句，chrome浏览器可以直接支持event.keyCode
    var code = theEvent.keyCode;
    if (code == 13)
    {
        hireloginvalidate(0);
    }
}
