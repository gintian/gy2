function exit()
{
	employPortalForm.target='_self';
	employPortalForm.action="/hire/employNetPortal/search_zp_position.do?b_exit=exit";
	employPortalForm.submit();
}
function resumeBrowse(dbname,a0100)
{
	window.open("/hire/employNetPortal/search_zp_position.do?b_resumeBrowse=browse&entryType=1","_blank");
		
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
   document.employPortalForm.action="/hire/employNetPortal/search_zp_position.do?b_active=login&setID=0&opt=1";
   document.employPortalForm.submit();
}
function del(zp_pos_id,a0100,dbName)
{
	var hashvo=new ParameterSet();
	var In_paramters="a0100="+a0100;  
	hashvo.setValue("zp_pos_id",zp_pos_id);
	hashvo.setValue("dbname",dbName);
	hashvo.setValue("opt","del");
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfoONE,functionId:'3000000172'},hashvo);
	
}
function returnInfoONE(outparamters)
{
	var info=outparamters.getValue("info");
	alert(info);
	document.location.reload();
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
  //应聘岗位页面js结束-------------------------------------------------
function more(id,isQueryCondition)
	{
		if(isQueryCondition=='0')
			document.employPortalForm.action="/hire/employNetPortal/search_zp_position.do?br_more=link&isAll=1&unitCode="+id;
		else
			document.employPortalForm.action="/hire/employNetPortal/search_zp_position.do?b_more=link&isAll=1&unitCode="+id;
		document.employPortalForm.submit();
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
		window.open("/hire/employNetPortal/search_zp_position.do?b_showContent=show&b0110="+b0110,"_blank");
	}
	
function T_BUTTOM()
{
 document.employPortalForm.action="/hire/employNetPortal/search_zp_position.do?b_register=register";
 document.employPortalForm.submit();
}
function TR_BUTTON()
{
  document.employPortalForm.action="/hire/employNetPortal/search_zp_position.do?br_license=license";
  document.employPortalForm.submit();
}
function pf_ChangeFocus() 
    { 
      key = window.event.keyCode;     
      if ( key==0xD)
      {
      	if(event.srcElement.name=='loginName'||event.srcElement.name=='password')
      			login();
      	else if(event.srcElement.name!=null&&event.srcElement.name.length>14&&event.srcElement.name.substring(0,14)=='conditionField')
      			query()	
      			
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
		    if(document.getElementById("remenberme").checked)
		    {
    			setDaysCookie("hjsjloginName",document.employPortalForm.loginName.value,60);
	    		setDaysCookie("hjsjpassword",document.employPortalForm.password.value,60);
	    	}
			document.employPortalForm.action="/hire/employNetPortal/search_zp_position.do?b_login=login";
			document.employPortalForm.submit();
		}
	}
function deleteRecord(i9999)
	{
		if(confirm(COFIRM_TO_DELETE_INFO+"？"))
		{
			employPortalForm.action="/hire/employNetPortal/search_zp_position.do?b_delete=delete&i9999="+i9999;
			employPortalForm.submit();
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
     employPortalForm.action = "/hire/employNetPortal/search_zp_position.do?b_uploade=upload&finished=1";
  else
     employPortalForm.action = "/hire/employNetPortal/search_zp_position.do?b_upload=upload";
  employPortalForm.submit();
     //employPortalForm.action = "/hire/employNetPortal/search_zp_position.do?b_upload=upload";
     //employPortalForm.submit();
}
function deleteattach(a0100,i9999,nbase)
{
     if(confirm(CONFIRM_DELETE_FILE))
     {
        employPortalForm.action = "/hire/employNetPortal/search_zp_position.do?b_delattach=del&a0100="+a0100+"&i9999="+i9999+"&nbase="+nbase;
        employPortalForm.submit();
     }
}//------------------------------
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
		var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
		var return_vo= window.showModalDialog(iframe_url,infos, 
       		 "dialogWidth:550px; dialogHeight:350px;resizable:no;center:yes;scroll:yes;status:no");
       	if(return_vo)
       	{
	        alert(ADD_SUCCESS+"！");
	        window.location.reload();
	    }
}
 function openPhoto()
   {
   		window.open("/hire/employNetPortal/search_zp_position.do?br_showPhoto=show","_blank");
   
   }
function executeOutFile(workExperience,a0100,persontype)
   {
        var hashvo=new ParameterSet();
        hashvo.setValue("workExperience",workExperience);   
        hashvo.setValue("a0100",a0100);
        hashvo.setValue("persontype",persontype);  
        var request=new Request({method:'post',onSuccess:showfile,functionId:'3000000211'},hashvo);
	    //var In_paramters="a0100=${employPortalForm.a0100}";  
	   //	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showfile,functionId:'3000000211'});
		
	}
function showfile(outparamters)
	{
		var outName=outparamters.getValue("outName");
		outName = decode(outName);
		var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName);
	
	}
	//-----------------
function sendmail()
{
   var zze=document.getElementById("zzee").value;
   if(trim(zze).length<=0)
   {
      alert("注册邮箱输入不能为空！");
      return;
   }
   
    var mm=/^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
    if(!mm.test(zze))
    {
        alert("请输入正确的邮箱地址，以方便帮您找回密码！");
        return;
    }
   var hashvo=new ParameterSet();
   hashvo.setValue("email",getEncodeStr(zze)); 
   var request=new Request({method:'post',asynchronous:true,onSuccess:returnInfoTHREE,functionId:'30200710250'},hashvo);
   
}
function returnInfoTHREE(outparameters)
{
  var msg = outparameters.getValue("msg");
  msg = getDecodeStr(msg);
  if(msg=='1')
  {
     alert("已将您的密码发送到您的注册邮箱，请您注意查收！");
     window.close();
  }
  else
  {
    alert(msg);
  }
}
function subEDIT(dbname,a0100)
	{	
		var a1=eval('t1')
		a1.innerHTML="";
		var a2=eval('t2')
		a2.innerHTML="";
		
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
		if(document.employPortalForm.pwd1.value.length<6||document.employPortalForm.pwd1.value.length>8)
		{			
			a2.innerHTML=PASSWORD_LENGTH_MUST_SIX_TO_EIGHT;
			flag=1;		
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
             employPortalForm.interviewingCodeValue.value=obj[i].value;
             num++;
         }
      }
      if(num==0)
      {
         alert("请选择回复状态后再提交！");
         return;
      }
      employPortalForm.action="/hire/employNetPortal/search_zp_position.do?b_save=save&entery=3";
      employPortalForm.submit(); 
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
	       document.employPortalForm.action="/hire/employNetPortal/search_zp_position.do?b_register=register";
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
  obj1.innerHTML="<input type=\"button\" id=\"js1\" name=\"button2\" onclick=\"subagreement('1');\" value=\"接受\"  class=\"hj_xkrz_but_d\" /><input type=\"button\" id=\"js2\" onclick=\"subagreement('0');\" name=\"button3\" value=\"不接受\"  class=\"hj_xkrz_but_d\" />";
}  
function goback()
	{
		document.employPortalForm.action="/hire/employNetPortal/search_zp_position.do?b_query=link";
		document.employPortalForm.submit();
	}
function returnInfo2(outparamters)
	{
		var info=outparamters.getValue("info");  //2: 申请成功   3:已超过了申请职位的最大数量3  4:简历资料必填项没填
		var userName=outparamters.getValue("userName");
		if(info=='10')
		{
			alert("该用户已入职，不允许继续申请！");return;
		}
		if(info=='2')
		{
			alert(POSITION_APPLY_SUCCESS_PLEASE_WAIT+"！");
			document.location="/hire/employNetPortal/search_zp_position.do?b_query=link&operate=init";
		}
		else if(info=='4')
		{
			alert(RESUME_IS_NOT_COMPLETE_NOT_APPLY+"！");
			document.employPortalForm.action="/hire/employNetPortal/search_zp_position.do?b_showResumeList=show&setID=0&opt=1";
			document.employPortalForm.submit();
		}
		else
		{
			var infos=new Array();
			infos[0]=info;    //1:为已申请 2: 申请成功   3:已超过了申请职位的最大数量3  4:简历资料必填项没填
			infos[1]=userName;
			var flag= window.showModalDialog("alert.jsp?infos="+infos[0], infos, 
			        "dialogWidth:385px; dialogHeight:250px;resizable:no;center:yes;scroll:no;status:no");	
		}
	
	}
	
function apply(isApplyedPos,a0100,userName,posID,z0301,person_type,loginName)
	{
		var infos=new Array();
		infos[0]=0;    //1:为已申请 2: 申请成功   3:已超过了申请职位的最大数量3  4:申请失败
		infos[1]=userName; 
		if(a0100!=null&&a0100.length>2)
		{
			if(isApplyedPos==1)
			{
				infos[0]="1";
				var flag= window.showModalDialog("alert.jsp?infos="+infos[0], infos, 
			        "dialogWidth:385px; dialogHeight:250px;resizable:no;center:yes;scroll:no;status:no");		
			}
			else
			{
			
				var hashvo=new ParameterSet();
				hashvo.setValue("posID",posID);
				hashvo.setValue("z0301",z0301);
				hashvo.setValue("person_type",person_type);
				hashvo.setValue("loginName",loginName);
				hashvo.setValue("userName",userName);
				var In_paramters="a0100="+a0100;  
				var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo2,functionId:'3000000170'},hashvo);
			}
		}
		else
		{
			document.employPortalForm.action="/hire/employNetPortal/search_zp_position.do?br_disembark=link";
			document.employPortalForm.submit();
		
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
function subRegister(isDefinitionActive,cultureCodeItem,isDefinitionCulture,paramFlag,blackField,blackFieldDesc,onlyNameDesc,isDefineWorkExperience,workExperienceDesc,person_type,itemLength)
	{	
		var a1=eval('t1')
		a1.innerHTML="";
		var a2=eval('t2')
		a2.innerHTML="";
		var a3=eval('t3')
		a3.innerHTML="";
		var flag=0;
		if(!fucEmailchk(document.employPortalForm.txtEmail.value))
		{	
			a1.innerHTML=EMAIL_ADDRESS_IS_WRONG;
			flag=1;
		}	
		if(trim(document.employPortalForm.pwd1.value).length==0)
		{			
			a2.innerHTML=PASSWORD_MUST_FILL_BUT_NOT_NULL;
			flag=1;		
		}
		if(document.employPortalForm.pwd1.value.length<6||document.employPortalForm.pwd1.value.length>8)
		{			
			a2.innerHTML=PASSWORD_LENGTH_MUST_SIX_TO_EIGHT;
			flag=1;		
		}
		if(document.employPortalForm.pwd1.value!=document.employPortalForm.pwd2.value)
		{			
			a2.innerHTML=PASSWORD_IS_NOT_UNANIMOUS;
			flag=1;					
		}
		if(trim(document.employPortalForm.txtName.value).length==0)
		{			
			a3.innerHTML=NAME_MUST_FILL;
			flag=1;		
		}
		var ida=isDefinitionActive;
		if(ida=='1')
		{
		   if(trim(document.employPortalForm.belongUnithName.value).length==0)
		   {
		      var a4=eval('t4');
		      a4.innerHTML="";
		      a4.innerHTML="限制浏览单位为必填项";
		      flag=1;
		   }
		}
		if(IsOverStrLength(document.employPortalForm.txtName.value,itemLength))
		{
			a3.innerHTML=NAME_LENGTHER_THAN+itemLength;
			flag=1;				
		}
		var codeitem=cultureCodeItem;
		var isDefinitinn=isDefinitionCulture;
		if(isDefinitinn!=null&&isDefinitinn=='1')
		{
	    	if(codeitem!=employPortalForm.hiddenCode.value)
	    	{
	    	    alert(CULTURE_TYPE_NOT_TO_HANDLE);
	    	    return;
	    	}
	    }
	    if(blackField!="-1"&&paramFlag!="3")
	    {
	        var t5=document.getElementById("t5");
	        if(trim(document.employPortalForm.blackFieldValue.value).length==0)
	        {
	            t5.innerHTML=blackFieldDesc+"为必填项";
	            flag=1;
	        }
	    }
	    if(paramFlag!="1")
	    {
	       var t7=document.getElementById("t7");
	        if(trim(document.employPortalForm.onlyValue.value).length==0)
	        {
	            t7.innerHTML=onlyNameDesc+"为必填项";
	            flag=1;
	        }
	        var de=onlyNameDesc;
	        if(de=='身份证号')
	        {
	           if(trim(document.employPortalForm.onlyValue.value).length!=15&&trim(document.employPortalForm.onlyValue.value).length!=18)
	           {
	                t7.innerHTML=onlyNameDesc+"的长度为15或者18位";
	                flag=1;
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
	          var a6=eval('t6');
		      a6.innerHTML="";
		      a6.innerHTML=workExperienceDesc+"为必填项";
	        }
	    }
		if(flag==0)
		{
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
		    hashvo.setValue("paramFlag",paramFlag);
		    hashvo.setValue("onlyNameDesc",onlyNameDesc);
		    hashvo.setValue("person_type",person_type);
		    var In_paramters="txtEmail="+document.employPortalForm.txtEmail.value;  
	     	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInforegister,functionId:'3000000161'},hashvo);
		}
	}
	
	
	function returnInforegister(outparamters)
	{
		var info=outparamters.getValue("info");
		var onlyNameDesc=outparamters.getValue("onlyNameDesc");
		var pert=outparamters.getValue("person_type");
	    var acountBeActived=outparamters.getValue("acountBeActived");//=1 要进行邮箱验证
		var a1=eval('t1')
		a1.innerHTML="";
		if(info=='1')
		{
			a1.innerHTML=EMAIL_EXIST_FILL_AGIN;
		}
		else if(info=='2')
		{
		    alert("您已经被系统列入黑名单，不能提交简历！");
		    return;
		}
		else if(info=='3')
		{
		   alert("不能重复注册！");
		   return;
		}
		else{
			var person_type="0"; //应聘
			if(person_type=='1')
				person_type="1";  //后备人才
			if(document.getElementById("remenberme").checked)
			{
		    	setDaysCookie("hjsjloginName",document.employPortalForm.txtEmail.value,60);
	    		setDaysCookie("hjsjpassword",document.employPortalForm.pwd1.value,60);
	    	}
			document.employPortalForm.action="/hire/employNetPortal/search_zp_position.do?b_enroll=login&active=1&person_type="+person_type;
			document.employPortalForm.submit();
			if(acountBeActived=='1')
			{
			document.employPortalForm.action= "/hire/employNetPortal/search_zp_position.do?b_query=link&operate=init&active=1"
			document.employPortalForm.submit();
			//alert("帐号注册成功，已将激活帐号邮件发送到注册邮箱，请到注册邮箱中激活帐号！");
			}
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
    var thecodeurl="/hire/employNetPortal/search_zp_position.do?br_prompt=inti"; 
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
    
    //------------------------------
    
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
		//window.opener.location="/hire/employActualize/employResume.do?b_query=link";
		//window.close();   		
   	}
   	
   	 function setSelectValue(opt,statusdesc,a0100)
   {  
   	   var	recordIDs=new Array();
       var zpPosIDs=eval("document.employPortalForm.zpPosID");
       var a_zpPosID="";
       if(zpPosIDs)
       {
       if(zpPosIDs.length)
       {
    	   var j = 0;
	       for(var i=0;i<zpPosIDs.length;i++)
	       {
	       		if(zpPosIDs[i].checked==true) {
	       			a_zpPosID=zpPosIDs[i].value;	
	       			recordIDs[j]=a0100+"/"+a_zpPosID;
	       			j++;
	       		}
	       }
	    }
	    else
	    {
	    	a_zpPosID=document.employPortalForm.zpPosID.value;
	    	recordIDs[0]=a0100+"/"+a_zpPosID;
	    }
	    }else
	    {
	      a_zpPosID="-1";
	    }
       
  		if(confirm(CONFIRM_TO_INSTALL+statusdesc+HIRE_STATUS+"？"))
  		{
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
   
    //在initStr寻找所有的AFindText替换成ARepText
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
   if(tableid==null||tableid == ''||tableid =='#')
	{
	      alert(NOT_CONFIGURE_TABLE+"！");
	      return;
	}
	tabid=tableid;
   window.setTimeout('previewTableByActive()',3000);   
}
function show(eventdiv,showdiv)
{
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

	with(showdiv.style)
	{
		left=x;
		top=y;
		visibility='';
	}
}
function hide(hidediv)
{
	hidediv.style.visibility='hidden';
}

function getcontent(chl_no)
{    
     var hashvo=new ParameterSet();

     hashvo.setValue("chl_no",chl_no);	
      
     var request=new Request({asynchronous:false,onSuccess:isSuccess,functionId:'1010021201'},hashvo);        
}

function isSuccess(outparamters)
{
	var cms_txt=outparamters.getValue("cms_txt");
	AjaxBind.bind($('cms_pnl'),cms_txt);
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