	// 刷新事件对象类型
	function refreshTree(obj)
	{
		document.manageKeyMatterForm.objecType.value = obj.value
		manageKeyMatterForm.action="/performance/objectiveManage/manageKeyMatter/orgTree.do?b_query=link&action=keyMatterList.do&treetype=duty&kind=0&objecType="+obj.value;
		manageKeyMatterForm.target="il_body";
		manageKeyMatterForm.submit();
	}	
	// 按事件对象名称查询关键事件
	function checkObjectKey()
	{
//		document.manageKeyMatterForm.objecType.value = obj.value
		manageKeyMatterForm.action="/performance/objectiveManage/manageKeyMatter/keyMatterList.do?b_query=search";
		manageKeyMatterForm.submit();
	}	
	
	// 生效关键事件
	function compare()
	{
		var str="";
		for(var i=0;i<document.manageKeyMatterForm.elements.length;i++)
		{
			if(document.manageKeyMatterForm.elements[i].type=="checkbox")
			{					
				var ff = manageKeyMatterForm.elements[i].name.substring(0,18);						
				if(document.manageKeyMatterForm.elements[i].checked==true && ff=='setlistform.select')
				{
					str+=document.manageKeyMatterForm.elements[i+1].value+"/";
				}
			}
		}
		if(str.length==0)
		{
			alert(KEYMATTER_INFO4);
			return;
		}
		else
		{
//			if (confirm(IS_DEL_NOT))
//			{
				document.manageKeyMatterForm.action="/performance/objectiveManage/manageKeyMatter/keyMatterList.do?b_compare=link&comparestr="+str;
				document.manageKeyMatterForm.submit();	
//			}
		}
	}
	// 退回已生效的关键事件
	function spBack()
	{
		var str="";
		for(var i=0;i<document.manageKeyMatterForm.elements.length;i++)
		{
			if(document.manageKeyMatterForm.elements[i].type=="checkbox")
			{					
				var ff = manageKeyMatterForm.elements[i].name.substring(0,18);						
				if(document.manageKeyMatterForm.elements[i].checked==true && ff=='setlistform.select')
				{
					str+=document.manageKeyMatterForm.elements[i+1].value+"/";
				}
			}
		}
		if(str.length==0)
		{
			alert(KEYMATTER_INFO5);
			return;
		}
		else
		{
//			if (confirm(IS_DEL_NOT))
//			{
				document.manageKeyMatterForm.action="/performance/objectiveManage/manageKeyMatter/keyMatterList.do?b_spBack=link&spBackstr="+str;
				document.manageKeyMatterForm.submit();	
//			}
		}
	}
	
/***********************************************************************************************************************************************************************************/	
	
	
	function replaceAll(str, sptr, sptr1)
	{
		while (str.indexOf(sptr) >= 0)
		{
   			str = str.replace(sptr, sptr1);
		}
		return str;
	}
	function add()
	{
		var dbname = $F('dbname');
		var objecType = $F('object_Types');
		
		if((dbname=='false') && (objecType=='2'))
		{
			alert("当前用户没有在职人员库的权限，不能新增事件！");
			return;
		}		
		
/*		var objecType = $F('objecType');
		var kind = $F('kind');
		
		if(objecType=='2' && kind!='3')
		{
			alert(KEYMATTER_INFO1);
			return;
		}
		if(objecType=='1' && kind!='1' && kind!='2')
		{
			alert(KEYMATTER_INFO2);
			return;
		}
*/		
		var target_url="/performance/objectiveManage/manageKeyMatter/keyMatterAdd.do?b_add=link";
 		var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
	    
	    // 多浏览器兼容showModalDialog改为Ext.window形式 chent 20171226 add
	    if(/msie/i.test(navigator.userAgent)){
			var return_vo= window.showModalDialog(iframe_url, "KeyMatterAdd", "dialogWidth:530px; dialogHeight:420px;resizable:no;center:yes;scroll:no;status:no");	
			if(return_vo.flag=="true"){
				search();
			}
			return ;
		} else {
		    function openWin(){
			    Ext.create("Ext.window.Window",{
			    	id:'addedit_win',
			    	width:580,
			    	height:450,
			    	title:'新增',
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
		}
		
	}
	function addedit_ok(flag){
		setTimeout(function(){
			if(flag=="true") {
				search();
			}
		}, 500);
	}
	function addeditWinClose(){
		Ext.getCmp('addedit_win').close();
	}
	function edit(eventId)
	{
		var target_url="/performance/objectiveManage/manageKeyMatter/keyMatterAdd.do?b_add=link`eventId="+eventId;
 		var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
		// 多浏览器兼容showModalDialog改为Ext.window形式 chent 20171226 add
	    if(/msie/i.test(navigator.userAgent)){
			var return_vo= window.showModalDialog(iframe_url, "KeyMatterAdd", "dialogWidth:530px; dialogHeight:390px;resizable:no;center:yes;scroll:no;status:no");	
			if(return_vo.flag=="true"){
				search();
			}
			return ;
		} else {
		    function openWin(){
			    Ext.create("Ext.window.Window",{
			    	id:'addedit_win',
			    	width:580,
			    	height:450,
			    	title:'编辑',
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
		}
	}
	function search()
	{
		manageKeyMatterForm.action="/performance/objectiveManage/manageKeyMatter/keyMatterList.do?b_query=link&refreshKey=saveKey";
		manageKeyMatterForm.submit();
	}
	function del()
	{
		var str="";
		for(var i=0;i<document.manageKeyMatterForm.elements.length;i++)
		{
			if(document.manageKeyMatterForm.elements[i].type=="checkbox")
			{					
				var ff = manageKeyMatterForm.elements[i].name.substring(0,18);						
				if(document.manageKeyMatterForm.elements[i].checked==true && ff=='setlistform.select')
				{
					str+=document.manageKeyMatterForm.elements[i+1].value+"/";
				}
			}
		}
		if(str.length==0)
		{
			alert(KEYMATTER_INFO3);
			return;
		}
		else
		{
			if (confirm(IS_DEL_NOT))
			{
			//	document.manageKeyMatterForm.action="/performance/objectiveManage/manageKeyMatter/keyMatterList.do?b_delete=link&deletestr="+str;
			//	document.manageKeyMatterForm.submit();	
				
				var hashvo=new ParameterSet();
		  		hashvo.setValue("deletestr",str);		
		  		var request=new Request({asynchronous:false,onSuccess:delKeyValue_ok,functionId:'9028000501'},hashvo);
			}
		}
	}
	function delKeyValue_ok(outparameters)
	{    		
   		var msg = getDecodeStr(outparameters.getValue("msg"));
   		if(msg=='have03')
   			alert("生效的记录不允许删除！请先退回再删除！");
     	document.manageKeyMatterForm.action="/performance/objectiveManage/manageKeyMatter/keyMatterList.do?b_query=link&refreshKey=saveKey";
		document.manageKeyMatterForm.submit();
   		  
	}
	
	
	
	function isNumber(theData)
	{
  		var checkOK = "-0123456789.";
 		var checkStr = theData;
  		var allValid = true;
  		var decPoints = 0;
  		var allNum = "";
  		if (checkStr=="")
  			return true;
  		var count = 0;
  		var theIndex = 0;
  		for (i = 0;  i < checkStr.length;  i++)
	    {
    		ch = checkStr.charAt(i);
    		if(ch=='-')
    		{
    			count=count+1;
    			theIndex=i+1;
    		}
    		for (j = 0;  j < checkOK.length;  j++)
     	    if (ch == checkOK.charAt(j))
       			 break;
    		if (j == checkOK.length)
   		    {
  			   allValid = false;
   			   break;
  		    }
    		if (ch == ".")
    		{
     			 allNum += ".";
     			 decPoints++;
  			 }
    	  else if (ch != ",")
      		allNum += ch;
  		}
  		if(count>1 || (count==1 && theIndex>1))
  			return false;
 	 	if (!allValid)
    	return false;
  		if (decPoints > 1) 
  		  return false;    
  		return true;
	}