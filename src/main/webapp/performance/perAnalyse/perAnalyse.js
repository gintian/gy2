	function executeExcel(model)
	{
		var obj=document.getElementById("chart").firstChild;
	//	alert(obj.src.substring(obj.src.indexOf("=")+1));
		var hashvo=new ParameterSet();
		hashvo.setValue("model",model);
		hashvo.setValue("picName",obj.src.substring(obj.src.indexOf("=")+1));
		var request=new Request({method:'post',asynchronous:false,onSuccess:showFile,functionId:'9026000014'},hashvo);
	}
	
	function showFile(outparamters)
	{
		//zhangh 2020-4-7 下载改为使用VFS
		var outName=outparamters.getValue("filename");
		outName = decode(outName);
		var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName);
	}
	
	function showMenu()
	{
		 var obj=document.getElementById('menu_');
		 obj.style.display="block";
		 obj.style.position="absolute";
		 obj.style.left=event.clientX;	
		 obj.style.top=event.clientY;
		 document.getElementById("menu_").focus();
		 
	}
	
	function hiddenElement()
	{
   		 setTimeout("closeMenu()",500);
		
	}
	function closeMenu()
	{
		 var obj=document.getElementById('menu_');
		 obj.style.display="none";
	}

function setPlans(busitype)
{	
		var temps=document.getElementsByName("plan_id");
		var planIds="";
		for(var i=0;i<temps.length;i++)
		{
			if(temps[i].checked==true)
				planIds+=","+temps[i].value;
		}	
		var return_value=new Array();
		return_value[0]="1";
		if(planIds.length>0)
			return_value[1]=planIds.substring(1);
		else
			return_value[1]="";
				
		if(busitype==null || busitype.length<=0 || busitype=='0')
		{	
			setCookie("plansSel",return_value[1]);
			setCookie("plansSel3",return_value[1]);
		}
		else
		{
			setCookie("modalPlansSel",return_value[1]);
			setCookie("modalPlansSel3",return_value[1]);
		}
		if(!window.showModalDialog){
			parent.window.opener.setPlan_OK(return_value);
		}else{
            parent.window.returnValue=return_value;
		}
	parent.window.close();
}