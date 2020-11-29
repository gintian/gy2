function saveBaseParam()
{	
	var dbs = document.getElementsByName('db');
	var dbstr='';
	if(dbs)
	{		
		if(dbs.length)
		{
				for(var i=0;i<dbs.length;i++)
				{
					if(dbs[i].checked==true)
						dbstr+=','+dbs[i].value;	
				}
		}
		else
		{
			if(dbs.checked==true)
				dbstr+=','+dbs.value;	
		}
	}
	bonusParamForm.paramStr.value=dbstr.substring(1);
	bonusParamForm.action='/gz/bonus/param/baseparam.do?b_save=link&menuid='+bonusParamForm.menuid.value;
	bonusParamForm.submit();
}
function addCodeItem()
{
	 var target_url="/gz/bonus/param/otherparam.do?b_add=link&menuid="+bonusParamForm.menuid.value+"&codeitemid=0";
	 var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
	 var return_vo=window.showModalDialog(target_url,'glWin','dialogWidth:400px; dialogHeight:130px;resizable:no;center:yes;scroll:no;status:no');
	 if(return_vo==null)
		return false;	   
	 if(return_vo.flag=="true") 
	 {
		bonusParamForm.action='/gz/bonus/param/otherparam.do?b_query=link&menuid='+bonusParamForm.menuid.value;
		bonusParamForm.submit();
	 }
}
function update(codeitemid)
{
	 var target_url="/gz/bonus/param/otherparam.do?b_add=link&menuid="+bonusParamForm.menuid.value+"&codeitemid="+codeitemid;
	 var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
	 var return_vo=window.showModalDialog(target_url,'glWin','dialogWidth:400px; dialogHeight:130px;resizable:no;center:yes;scroll:no;status:no');
	 if(return_vo==null)
		return false;
	 if(return_vo.flag=="true") 
	 {
		bonusParamForm.action='/gz/bonus/param/otherparam.do?b_query=link&menuid='+bonusParamForm.menuid.value;
		bonusParamForm.submit();
	 }	
}

var isSave = false;
function qxFunc()
{
	if(isSave)
	{
		var thevo=new Object();
		thevo.flag="true";
		window.returnValue=thevo;
		window.close();
	}
	else
	window.close();
}
function beforSave(type)
{
	if(document.getElementById('codeitemid').value=='')
	{
		alert(GZ_BONUS_INFO1);
		document.getElementById('codeitemid').focus();
		return;
	}else if(document.getElementById('codeitemid').value.length<2)
	{
		alert(GZ_BONUS_INFO4);
		document.getElementById('codeitemid').focus();
		return;
	}
	if(!isNums(document.getElementById('codeitemid').value))
	{
		alert(GZ_BONUS_INFO11);
		document.getElementById('codeitemid').focus();
		return;
	}
	if(document.getElementById('name').value=='')
	{
		alert(GZ_BONUS_INFO2);
		document.getElementById('name').focus();
		return;
	}
	//验证代码或代码名称已经存在的情况
	var hashvo=new ParameterSet();
	hashvo.setValue("menuid",bonusParamForm.menuid.value);
	hashvo.setValue("codeitemid",document.getElementById('codeitemid').value);
	hashvo.setValue("name",getEncodeStr(document.getElementById('name').value));
	hashvo.setValue("type",type);
	var request=new Request({method:'post',asynchronous:false,onSuccess:beforSave2,functionId:'3020130033'},hashvo);
}
function beforSave2(outparamters)
{
	var type=outparamters.getValue("type");
	var flag=outparamters.getValue("flag");
	if(flag=='1')
	{
		alert(GZ_BONUS_INFO3);
		return;
	}
	else if(flag='0')
		save(type);
}


function save(type)
{	
	var hashvo=new ParameterSet();
	hashvo.setValue("menuid",bonusParamForm.menuid.value);
	hashvo.setValue("codeitemid",document.getElementById('codeitemid').value);
	hashvo.setValue("name",getEncodeStr(document.getElementById('name').value));
	hashvo.setValue("type",type);
	var request=new Request({method:'post',asynchronous:false,onSuccess:afterSave,functionId:'3020130032'},hashvo);
}
function afterSave(outparamters)
{
	var type=outparamters.getValue("type");
	isSave=true;
	if(type!='2')
		qxFunc();
	else
	{
		document.getElementById('codeitemid').value='';
		document.getElementById('name').value='';
	}
}
function del()
{
	var dbs = document.getElementsByName('codeitemid');
	var dbstr='';
	if(dbs)
	{		
		if(dbs.length)
		{
				for(var i=0;i<dbs.length;i++)
				{
					if(dbs[i].checked==true)
						dbstr+=','+dbs[i].value;	
				}
		}
		else
		{
			if(dbs.checked==true)
				dbstr+=','+dbs.value;	
		}
	}
	if(dbstr=='')
	{
		alert(CHOISE_DELETE_NOT);
		return;
	}
	if(confirm(GZ_BONUS_INFO5))
	{
		bonusParamForm.paramStr.value=dbstr.substring(1);
		bonusParamForm.action='/gz/bonus/param/otherparam.do?b_del=link&menuid='+bonusParamForm.menuid.value;
		bonusParamForm.submit();
	}
}
function checkNuNS(obj){
 	if(!isNums(obj.value)){
 		obj.value='';
 		return;
 	}
}
function isNums(i_value){
    re=new RegExp("[^A-Za-z0-9]");
    var s;
    if(s=i_value.match(re)){
        return false;
     }
    return true;
}
