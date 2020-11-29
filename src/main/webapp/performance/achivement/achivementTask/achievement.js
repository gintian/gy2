  function changeCycle(target_id)
  {
  	document.achievementTaskForm.action="/performance/achivement/achivementTask.do?b_search=query1&onePage=1&target_id="+target_id;
  	document.achievementTaskForm.submit();
  }
  function returnBigData(target_id)
  {
  	document.achievementTaskForm.action="/performance/achivement/achivementTask.do?b_search=query&onePage=1&target_id="+target_id;
  	document.achievementTaskForm.target="mil_body";
  	document.achievementTaskForm.submit();
  }
  //跳转到新单指标页面
  function editBig(object_id,target_id,nbase,object_type)
  {	
	document.achievementTaskForm.action="/performance/achivement/achivementTask.do?b_querys=link&hjsoft=hjsj&object_id="+object_id+"&target_id="+target_id+"&nbase="+nbase+"&object_type="+object_type;
 	document.achievementTaskForm.submit();	
  }
  
  //保留单指标页面链接端口
  function edit(object_id,target_id,nbase)
  {
	document.achievementTaskForm.action="/performance/achivement/achivementTask/singletObjectTask.do?b_query=link&object_id="+object_id+"&target_id="+target_id+"&nbase="+nbase;
	document.achievementTaskForm.submit();	
  }

  function changeBigCycle(target_id)
  {
  	document.achievementTaskForm.action="/performance/achivement/achivementTask.do?b_query=query1&onePage=1&paramd=1&target_id="+target_id;
  	document.achievementTaskForm.submit();
  }
	
  function go_left(obj)
  {
  	var temps=pointDescs.split(",");
  	var to=obj.name.indexOf("].");
  	var point_name=obj.name.substring(to+2);
  	var a_name=obj.name.substring(0,to+2);
  	for(var i=0;i<temps.length;i++)
  	{
  		if(temps[i]==point_name&&i>0)
  		{
  		   var temp=temps[i-1];
  		   var new_obj=document.getElementsByName(a_name+temp);
  		   new_obj[0].focus();
  			break;
  		}
  	}
  	
  }

  function go_right(obj)
  {
  	var temps=pointDescs.split(",");
  	var to=obj.name.indexOf("].");
  	var point_name=obj.name.substring(to+2);
  	var a_name=obj.name.substring(0,to+2);
  	for(var i=0;i<temps.length;i++)
  	{
  		if(temps[i]==point_name&&i<(temps.length-1))
  		{
  		   var temp=temps[i+1];
  		   var new_obj=document.getElementsByName(a_name+temp);
  		   new_obj[0].focus();
  			break;
  		}
  	}
  }
  
  function go_up(obj)
  {
  	var to=obj.name.indexOf("].");
  	var index=obj.name.substring(15,to)*1;
  	if(index>0)
  		index--;
  	var new_obj=document.getElementsByName(obj.name.substring(0,15)+index+obj.name.substring(to));
  	if(new_obj.length==1)
  	{
  		new_obj[0].focus();
  		new_obj[0].parentNode.parentNode.fireEvent("onClick");
  	}
  }
  
  function go_down(obj)
  {
  	var to=obj.name.indexOf("].");
  	var index=obj.name.substring(15,to)*1;
  	if(index>0)
  		index++;
  	var new_obj=document.getElementsByName(obj.name.substring(0,15)+index+obj.name.substring(to));
	
  	if(new_obj.length==1)
  	{
  		new_obj[0].focus();
  		new_obj[0].parentNode.parentNode.fireEvent("onClick");
  	}
  }





	
  //批量修改
  function batch_update(target_id)
  {
       var theurl="/performance/achivement/achivementTask.do?br_initBatchupdate=new`cycle="+document.achievementTaskForm.cycle.value+"`target_id="+target_id+"`callbackFunc=batch_update_ok";
    	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
  		var config = {
	      	width:500,
	      	height:300,
	      	type:'1',
	      	title:'批量修改',
	      	id:'batchUpdateWin'
    	}
   		modalDialog.showModalDialogs(iframe_url,'template_win',config,function(retvo){
	   		if(retvo!=null)
			{
				document.achievementTaskForm.action="/performance/achivement/achivementTask.do?b_search=query1&target_id="+target_id;
				document.achievementTaskForm.submit();
			}
   		});
  }
  function batch_update_ok(retvo,target_id){
  	if(retvo!=null)
	{
		parent.parent.document.achievementTaskForm.action="/performance/achivement/achivementTask.do?b_search=query1&target_id="+target_id;
		parent.parent.document.achievementTaskForm.submit();
	}
  }
  //多指标批量修改
  function batchBigupdate(target_id)
  {
       var theurl="/performance/achivement/achivementTask.do?br_initBatchupdate=new`cycle="+document.achievementTaskForm.cycle.value+"`target_id="+target_id+"`callbackFunc=batchBigupdate_ok";
    	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
  		var config = {
	      	width:500,
	      	height:300,
	      	type:'1',
	      	title:"多指标批量修改",
      		id:'batchUpdateWin'
    	}
   		modalDialog.showModalDialogs(iframe_url,'template_win',config,function(retvo){
   			if(retvo!=null){
				document.achievementTaskForm.action="/performance/achivement/achivementTask.do?b_query=query1&paramd=1&target_id="+target_id;
		  		document.achievementTaskForm.submit();
			}
   		});
  }
  function batchBigupdate_ok(retvo,target_id){
  	if(retvo!=null){
		parent.parent.document.achievementTaskForm.action="/performance/achivement/achivementTask.do?b_query=query1&paramd=1&target_id="+target_id;
  		parent.parent.achievementTaskForm.submit();
	}
  }
  
  //批量修改
  function batchUpdate(target_id)
  {
  		if(document.achievementTaskForm.point_value.value.length==0){
  			if(window.showModalDialog){
   				window.close();
   			}else{
   				parent.parent.Ext.getCmp("batchUpdateWin").close();
   			}		
  		}
  		document.achievementTaskForm.action="/performance/achivement/achivementTask.do?b_updateValue=save&opt=1&target_id="+target_id+"&callbackFunc="+callbackFunc;
   		document.achievementTaskForm.submit();	
  
  }


//自动选中绩效任务书
	function autoSelectNode()
	{
		var obj=root;
		var obj1=obj.childNodes[0];  //团队
		var obj2=obj.childNodes[1];  //个人
		if(obj1.childNodes.length>0)
		{
		     obj=obj1.childNodes[0];
			 obj.childNodes[0].select();
			 selectedClass("treeItem-text-"+obj.childNodes[0].id);
	     	 //selectedClass("treeItem-text-"+(Global.id-1));
		}
		else if(obj2.childNodes.length>0)
		{
			obj=obj2.childNodes[0];
			obj.childNodes[0].select();
			selectedClass("treeItem-text-"+obj.childNodes[0].id);
		}
		else
		{
			parent.mil_body.location="/performance/achivement/achivementTask.do?br_search=search";
		
		}
		
	}
//获取统一打分指标数量 Jinchunhai
	var num = 0;
	function unityGrade()
	{
		var hashvo=new ParameterSet();     
	    hashvo.setValue("opt",'1');
	    var request=new Request({method:'post',asynchronous:false,onSuccess:isHaveBodys,functionId:'9020020287'},hashvo);
	}	
	function isHaveBodys(outparamters)
	{				
    	var list=outparamters.getValue("list");  
    	for(var i=0;i<list.length;i++)
    	{
    		var number = list[i];
    		if(number >= 50)
    		{
    			num = 1;
    		}
    	}     		 	
    }	
	
	function alterTreeNode(root_url,bigobj)
	{
		var temps=root_url.split("/");
		var obj=root;
		for(var j=0;j<obj.childNodes.length;j++)
		{
					if(obj.childNodes[j].text==temps[1])
					{
						obj=obj.childNodes[j];
						break;
					}
		}
		
		//判断是否存在年度
		var isYear=0;
		for(var j=0;j<obj.childNodes.length;j++)
		{
				if(obj.childNodes[j].text==temps[2]+ACHIEVEMENT_YEAR)
				{
							obj=obj.childNodes[j];
							isYear=1;
							break;
				}
		}
		if(isYear==0)
		{
				
			/*
				var imgurl="/images/admin.gif";
				var tmp = new xtreeItem(temps[2],temps[2]+ACHIEVEMENT_YEAR,"","mil_body",temps[2]+ACHIEVEMENT_YEAR,imgurl,"/performance/achivement/achivementTask/achivement_task_tree.jsp?opt=1&codeid="+temps[2]);
	 			if(obj.childNodes.length==0)
	 			{
	 				obj.add(tmp);
	 				obj=obj.childNodes[0];
	 			}
	 			else
	 			{
	 				obj.add(tmp);
	 				obj=obj.childNodes[obj.childNodes.length-1];
	 			}	
	 		*/
	 		obj.clearChildren();
	 		obj.loadChildren();
			obj.expand();	
	 		
	 		var _obj;
	 		for(var j=0;j<obj.childNodes.length;j++)
			{
//				obj.childNodes[j].loadChildren();
				obj.childNodes[j].expand();
				if(obj.childNodes[j].text==temps[2]+ACHIEVEMENT_YEAR)
				{
					_obj=obj.childNodes[j];
					isYear=1;
				}
			}	
	 		obj=_obj;
	 			
		}
		//判断是否存在当前节点
		var isNode=0;
		var parent_obj=obj;
		for(var j=0;j<obj.childNodes.length;j++)
		{
				if(obj.childNodes[j].uid==temps[3].split("`")[0])
				{
							obj=obj.childNodes[j];
							isNode=1;
							break;
				}
		}
		
	//	if(obj!=null&&obj!='undefined')
		parent_obj.expand();
		if(isNode==1)
		{
			var a_parent=parent_obj;   //obj.parent;
			a_parent.clearChildren();
			a_parent.loadChildren();
			for(var j=0;j<a_parent.childNodes.length;j++)
			{
				
					if(a_parent.childNodes[j].uid==temps[3].split("`")[0])
					{
								obj=a_parent.childNodes[j];
								break;
					}
			}
		}
		else
		{		
			var imgurl="/images/img_l.gif";
			var tmp;
	
			if(bigobj==1)
			{
				tmp = new xtreeItem(temps[3].split("`")[0],temps[3].split("`")[1],"/performance/achivement/achivementTask.do?b_search=query&onePage=1&target_id="+temps[3].split("`")[0],"mil_body",temps[3].split("`")[1],imgurl,"");
			}else{
				tmp = new xtreeItem(temps[3].split("`")[0],temps[3].split("`")[1],"/performance/achivement/achivementTask.do?b_querys=link&hjsoft=hj&onePage=1&paramd=0&target_id="+temps[3].split("`")[0],"mil_body",temps[3].split("`")[1],imgurl,"");
			}
	 		if(obj.childNodes.length==0)
	 		{
	 				obj.add(tmp);
	 				obj=obj.childNodes[0];
	 		}
	 		else 
	 		{
	 				obj.add(tmp);
	 				obj=obj.childNodes[obj.childNodes.length-1];
	 		}	
		}
		obj.select();
		selectedClass("treeItem-text-"+obj.id);
	}
	






   function getSelectTxt(point_id)
	{
		 
		var txt="<select style=\"width:135px\" name='"+point_id+"_2' >";
		txt+="<option value='-1'>"+ACHIEVEMENT_ALL+"</option>";
		if(cycle=='0')
		{
			txt+="<option value='01'>"+theyear+"</option>";
		}
		else if(cycle=='1')
		{
			txt+="<option value='1'>"+ACHIEVEMENT_UPYEAR+"</option>";
			txt+="<option value='2'>"+ACHIEVEMENT_DOWNYEAR+"</option>";
		}
		else if(cycle=='2')
		{
			txt+="<option value='01'>"+FIRST_QUARTER+"</option>";
			txt+="<option value='02'>"+SECOND_QUARTER+"</option>";
			txt+="<option value='03'>"+THREE_QUARTER+"</option>";
			txt+="<option value='04'>"+FOUR_QUARTER+"</option>";
		}
		else if(cycle=='3')
		{
			for(var i=1;i<10;i++)
				txt+="<option value='0"+i+"'>"+i+GZ_ANLAYSE_SETINFOR_MONTH+"</option>";
			txt+="<option value='10'>10"+GZ_ANLAYSE_SETINFOR_MONTH+"</option>";
			txt+="<option value='11'>11"+GZ_ANLAYSE_SETINFOR_MONTH+"</option>";
			txt+="<option value='12'>12"+GZ_ANLAYSE_SETINFOR_MONTH+"</option>";
		}
		txt+="</select>";
		return txt
	}    
    
    //下一页
  	function next()
  	{
  		if(document.achievementTaskForm.right_fields.options.length==0)
  		{
  			alert(GENERAL_SELECT_ITEMNAME+"!");
  			return;
  		}
  		document.getElementById('a1').style.display='none';
  		document.getElementById('a2').style.display='block';
  		
  		document.getElementById('a2').innerHTML=getContext();
  		
  	}
  	
  	//上一页
  	function up()
  	{	
  		document.getElementById('a1').style.display='block';
  		document.getElementById('a2').style.display='none';
  	}
  	
  	function closeWin(){
  		if(window.showModalDialog){
	  		parent.window.close();
  		}else{
  			parent.parent.Ext.getCmp("searchPersonnelWin").close();
  		}
  		
  	}
  	
  	//取得条件内容
  	function getContext()
  	{
  		var txt="<table width='90%' border='0' cellspacing='1' align='center' cellpadding='1' class='ListTable'   ><tr><td> <div id='a3' class='common_border_color'><table width='100%' border='0' cellspacing='0'  id='a_table' align='center' cellpadding='1' class='ListTable'   >";
  		txt+="<thead><tr   ";
  		//非ie下不使用锁列样式（会导致火狐下边框无法显示出来）
  		if(/msie/i.test(navigator.userAgent)){
  			txt+="style='position:relative;top:expression(this.offsetParent.scrollTop-2);'"; 
  		}
  		txt+="><td align='center'  class='TableRowConition common_background_color common_border_color' style='border-left:0px' nowrap>逻辑符</td><td align='center'  class='TableRowConition common_background_color common_border_color' nowrap>查询指标</td><td align='center'  class='TableRowConition common_background_color common_border_color' nowrap>关系符</td><td align='center'  class='TableRowConition common_background_color common_border_color' nowrap>查询值</td></tr> </thead>";
  		for(var i=0;i<document.achievementTaskForm.right_fields.options.length;i++)
  		{
  			txt+=getRowTxt(document.achievementTaskForm.right_fields.options[i].value,document.achievementTaskForm.right_fields.options[i].text,i);
  			
  		}
  		txt+="</table></div> </td></tr><tr><td align='center' style='height:35px' >";
  		
  		txt+="<Input type='button' value='"+UP_GO+"'  class='mybutton'  onclick='up()'  />";
  		txt+="<Input type='button' value='"+SUBMIT+"'  class='mybutton'  onclick='subSearch()'  />";
  		txt+="<Input type='button' value='"+CANCEL+"'  class='mybutton'  onclick='closeWin()'  />";
  		
  		txt+="</td></tr></table>";
  		return txt;
  	}
  	
  	//提交查询
  	function subSearch()
  	{
  		var str_sql="";
  		var exists="";
  		for(var i=0;i<document.achievementTaskForm.right_fields.options.length;i++)
  		{
  			var temp=document.achievementTaskForm.right_fields.options[i].value;			
  			
  		 
  			if(exists.search("/"+temp+"/")!=-1)
  			{
  				continue;
  			}
  			 
  			exists+="/"+temp+"/";
  			
  			var obj0=document.getElementsByName(temp+"_0");   // eval('document.achievementTaskForm.'+temp+"_0");
  			var obj1=document.getElementsByName(temp+"_1");   // eval('document.achievementTaskForm.'+temp+"_1");
  			var obj2=document.getElementsByName(temp+"_2");   // eval('document.achievementTaskForm.'+temp+"_2"); 
  			 
  				for(var j=0;j<obj0.length;j++)
  				{
  					
  					if(temp=='kh_cyle'&&obj2[j].value=='-1')
  	  	  				continue;
  					
  					if(trim(obj2[j].value).length>0)
  		  			{
  			  			if(obj0[j].value=='0')
  			  					str_sql+=" and  " 
  			  			else
  			  					str_sql+=" or  ";
  			  			if(temp=='a0101')
  			  			{
  			  				str_sql+="per_target_mx.a0101 like '%"+obj2[j].value+"%'"
  			  			}
  			  			else if(temp=='kh_cyle')
  			  			{
  			  				str_sql+="kh_cyle ='"+obj2[j].value+"'"
  			  			}
  			  			else
  			  			{
  			  				if(!checkIsNum(obj2[j].value))
  					  		{
  					  			obj2[j].value='';
  					  			obj2[j].focus();
  					  			alert(ACHIEVEMENT_INFO1+"!");
  					  			return;
  					  		}	
  					  		str_sql+="T_"+temp+obj1[j].value+obj2[j].value;		
  			  			}
  		  			}
  				}
  			 
  		}
  		if(window.showModalDialog){
	  		if(str_sql.length==0)
	  		{
	  			window.returnValue="";
	  		}
	  		else
	  		{
	  		    //此处非兼容模式要求严格，因为有两层iframe 所以 window 应该改为window.parent haosl
	  			window.parent.returnValue=str_sql.substring(4);
	  			
	  		}
  		}else{
  			if(str_sql.length>0)
	  		{
  				parent.parent.searchPersonnel_ok(str_sql.substring(4));
	  		}
  		}
  		closeWin();
  	}
  	
  	
  	//生成table行代码
  	function getRowTxt(point_id,point_name,i)
  	{
  	
  		
  		var text="<tr ";
  		if(i%2==0)
  			text+="  class='trShallow' ><td align='center' class='RecoTdConition common_border_color' style='border-left:0px'><select";
  		else
  			text+="  class='trDeep' ><td  align='center' class='RecoTdConition common_border_color' style='border-left:0px'><select";
  			
  		if(i==0)
  			text+=" style='display:none' ";
  		text+=" name='"+point_id+"_0' ><option value='0'>"+GENERAL_AND+"</option><option value='1'>"+GENERAL_OR+"</option></select></td>";
  		text+="<td class='RecoTdConition common_border_color' >"+point_name+"</td>";
  		if(point_id=='a0101')
  		{
  			text+="<td class='RecoTdConition common_border_color' ><select style='width:50px' name='"+point_id+"_1' ><option value='like'>=</option></select> </td>";
  			text+="<td class='RecoTdConition common_border_color' ><input type='text' name='"+point_id+"_2' value='' class='inputtext'/> </td>";
  		}	
  		else if(point_id=='kh_cyle')
  		{
  			text+="<td class='RecoTdConition common_border_color' ><select style='width:50px' name='"+point_id+"_1' ><option value='='>=</option></select> </td>";
  			text+="<td class='RecoTdConition common_border_color' >";
  			text+=getSelectTxt(point_id);
  			text+="</td>";
  		}
  		else
  		{
  			text+="<td class='RecoTdConition common_border_color' ><select style='width:50px' name='"+point_id+"_1' ><option value='='>=</option><option value='>'>></option><option value='<'><</option><option value='>='>>=</option><option value='<='><=</option><option value='<>'><></option></select> </td>";
  			text+="<td class='RecoTdConition common_border_color' ><input type='text' name='"+point_id+"_2' value=''  class='inputtext'/> </td>";
  		}
  		text+="</tr>";
  		
  		return text;
  	}
  
  
  
  //指标排序
  function setPointSort(target_id)
  {
  		var theurl="/performance/achivement/achivementTask.do?b_showPointList=show`target_id="+target_id;
    	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
   		var config = {
	      	width:340,
	      	height:430,
	      	type:'1',
	      	title:"指标排序",
	      	id:'setPointSortWin'
    	}
   		modalDialog.showModalDialogs(iframe_url,'template_win',config,function(retvo){
   			if(retvo=="1")
			{
				document.achievementTaskForm.action="/performance/achivement/achivementTask.do?b_search=query&target_id="+target_id;
		  		document.achievementTaskForm.submit();
			}
   		});
  }
  function setPointSort_ok(retvo,target_id){
	if(retvo=="1")
	{
		document.achievementTaskForm.action="/performance/achivement/achivementTask.do?b_search=query&target_id="+target_id;
  		document.achievementTaskForm.submit();
	}
}
 //多指标排序
  function setBigPointSort(target_id)
  {
  		var theurl="/performance/achivement/achivementTask.do?b_showPointList=show`target_id="+target_id;
    	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
  		
		var config = {
	      	width:380,
	      	height:430,
	      	type:'1',
	      	title:'多指标排序'
    	}
   		modalDialog.showModalDialogs(iframe_url,'template_win',config,function(retvo){
   			if(retvo==null)
  	 			return ;		        
			if(retvo=="1")
			{
				document.achievementTaskForm.action="/performance/achivement/achivementTask.do?b_query=query&paramd=1&target_id="+target_id;
		  		document.achievementTaskForm.submit();
			}
   		});
  }
  function setBigPointSort_ok(retvo){
   			if(retvo==null)
  	 			return ;		        
			if(retvo=="1")
			{
				document.achievementTaskForm.action="/performance/achivement/achivementTask.do?b_query=query&paramd=1&target_id="+target_id;
		  		document.achievementTaskForm.submit();
			}
   		}
 
 //指标排序
 function subPointSort(target_id)
   {	
   		setselectitem('right_fields');
   		document.achievementTaskForm.action="/performance/achivement/achivementTask.do?b_savePointSort=save&target_id="+target_id;
   		document.achievementTaskForm.submit();
   }


//保存业绩任务书标准值
  function saveData()
  {
 		document.achievementTaskForm.action="/performance/achivement/achivementTask.do?b_saveTaskData=save";
	  	document.achievementTaskForm.submit();
  }
//保存业绩任务书单对象标准值
  function saveDataObject()
  {
 		document.achievementTaskForm.action="/performance/achivement/achivementTask/singletObjectTask.do?b_save=save";
	  	document.achievementTaskForm.submit();
  }
//保存业绩任务书标准值
  function saveBigData()
  {
 		document.achievementTaskForm.action="/performance/achivement/achivementTask.do?b_saveBigTarget=save&paramd=1";
	  	document.achievementTaskForm.submit();
  }
  
//取消保存业绩任务书标准值
  function cancelObject()
  {
 		document.achievementTaskForm.action="/performance/achivement/achivementTask.do?b_search=search";
	  	document.achievementTaskForm.submit();
  }  
  //检验数字类型
  function checkValue(obj)
  {
  	if(obj.value.length>0)
  	{
  		if(!checkIsNum(obj.value))
  		{
  			alert('请输入数值');
  			obj.value='';
  			obj.focus();
  		}
  	} 
  }
  
  
  //新建任务书
  function newTarget(big)
  {
  	    var theurl="/performance/achivement/achivementTask.do?b_newTarget=new`opt=new`callbackFunc=newTarget_ok";
    	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
    	var config = {
	      	width:800,
	      	height:550,
	      	title:'新建任务书',
	      	id:'newTargetWin'
    	}
   		modalDialog.showModalDialogs(iframe_url,'template_win',config,function(retvo){
   			if(retvo!=null&&retvo!='undefined'){		 				
				parent.mil_menu.alterTreeNode(retvo,big)
			}
   		});
  }
  function newTarget_ok(retvo){
	if(retvo!=null&&retvo!='undefined'){		 				
		parent.parent.parent.mil_menu.alterTreeNode(retvo,"1")
	}
  }
  //新建多指标任务书
  function newBigTaskTarget(big)
  {
  	    var theurl="/performance/achivement/achivementTask.do?b_newTarget=new`opt=new`callbackFunc=newBigTaskTarget_ok";
    	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
  	
  		var config = {
	        width:800,
	      	height:550,
	      	title:'新建多指标任务书',
	      	id:'newTargetWin'
    	}
   		modalDialog.showModalDialogs(iframe_url,'template_win',config,function(retvo){
   			if(retvo!=null&&retvo!='undefined')
			{		 				
				parent.parent.mil_menu.alterTreeNode(retvo,big)
			}
   		});
  }
  function newBigTaskTarget_ok(retvo){
	if(retvo!=null&&retvo!='undefined')
	{		 				
		parent.parent.parent.parent.mil_menu.alterTreeNode(retvo,"2")
	}
 }
  //修改任务书
  function editTarget(target_id,big)
  {
  	    var theurl="/performance/achivement/achivementTask.do?b_newTarget=new`opt=edit`target_id="+target_id+"`callbackFunc=newTarget_ok";
    	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
  		var config = {
	      	 width:800,
	      	 height:550,
	      	 title:'修改任务书',
	      	 id:'newTargetWin'
    	}
   		modalDialog.showModalDialogs(iframe_url,'template_win',config,function(retvo){
			if(retvo!=null&&retvo!='undefined')
	  		{
	  			parent.mil_menu.alterTreeNode(retvo,big)
	  		}
   		});
  }
  //修改多指标任务书
  function editBigTarget(target_id,big)
  {
  	    var theurl="/performance/achivement/achivementTask.do?b_newTarget=new`opt=edit`target_id="+target_id+"`callbackFunc=editBigTarget_ok";
    	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);

    	var config = {
	      	width:800,
	      	height:550,
	      	title:'修改多指标任务书',
	      	id:'newTargetWin'
    	}
   		modalDialog.showModalDialogs(iframe_url,'template_win',config,function(retvo){
			if(retvo!=null&&retvo!='undefined')
	  		{
	  			parent.parent.mil_menu.alterTreeNode(retvo,big)
	  		}
   		});
  }
  function editBigTarget_ok(retvo){
			if(retvo!=null&&retvo!='undefined')
	  		{
	  			parent.parent.mil_menu.alterTreeNode(retvo,"2");
	  		}
   		}
  //任务分配
  function allocateTask(target_id)
  {
  		var theurl="/performance/achivement/achivementTask.do?b_allocateTask=allocate`target_id="+target_id;
    	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
    	if(window.showModalDialog){
            var returnVal = window.showModalDialog(iframe_url, 'template_win', 
      				"dialogWidth:600px; dialogHeight:450px;resizable:no;center:yes;scroll:no;status:no");
            allocateTask_callBack(returnVal,target_id);//父页面提供的全局回调
        }else{
            var w = 600;
            var h = 420;
            var top= window.screen.availHeight-h>0?window.screen.availHeight-h:0;
            var left= window.screen.availWidth-w>0?window.screen.availWidth-w:0;
            top = top/2;
            left = left/2;
            var sSourceURL = theurl.replace(/／/g, "/").replace(/？/g, "?").replace(/＝/g, "=").replace(/`/g,"&");  ;  //20140901  dengcan
            window.open(sSourceURL,"template_win_","width=600,height=420,resizable=no,location=no,top="+top+",left="+left+",scrollbars=no,status=no,toolbar=no, menubar=no,modal=yes");
        }
  		
  }
  //任务分配回调
  function allocateTask_callBack(retvo,target_id){
      if(retvo==null)
          return ;                
      if(retvo=="1")
      {
          document.achievementTaskForm.action="/performance/achivement/achivementTask.do?b_search=query&target_id="+target_id;
          document.achievementTaskForm.submit();
      } 
  }
  //多指标任务分配
  function allocateBigTask(target_id)
  {
  		var theurl="/performance/achivement/achivementTask.do?b_allocateTask=allocate`target_id="+target_id;
    	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
    	if(window.showModalDialog){
            var returnVal = window.showModalDialog(iframe_url,"template_win","dialogWidth:600px;dialogHeight:420px;resizable:no;center:yes;scroll:no;status:no");
            allocateBigTask_callBack(returnVal,target_id);//父页面提供的全局回调
        }else{
            var w = 600;
            var h = 420;
            var top= window.screen.availHeight-h>0?window.screen.availHeight-h:0;
            var left= window.screen.availWidth-w>0?window.screen.availWidth-w:0;
            top = top/2;
            left = left/2;
            var sSourceURL = theurl.replace(/／/g, "/").replace(/？/g, "?").replace(/＝/g, "=").replace(/`/g,"&");  ;  //20140901  dengcan
            var win = window.open(sSourceURL,"template_win_","width=600,height=420,resizable=no,location=no,top="+top+",left="+left+",scrollbars=no,status=no");
           win.allocateBigTask = "true";
        }
   		
  }
  
  function allocateBigTask_callBack(retvo,target_id){
      if(retvo==null)
          return ;                
      if(retvo=="1")
      {
          document.achievementTaskForm.action="/performance/achivement/achivementTask.do?b_querys=query&hjsoft=hj&paramd=0&target_id="+target_id;
          document.achievementTaskForm.target="mil_body";
          document.achievementTaskForm.submit();
      }
  }
  //多指标撤销任务
  function delBigTask()
  {
//  	var personPression="${achievementTaskForm.orgCode}";
//  	alert(personPression+'brtoto');
  	Ext.showConfirm(ACHIEVEMENT_INFO2+'?',function(flag){
		 if(flag=="yes"){
    		document.achievementTaskForm.action="/performance/achivement/achivementTask.do?b_delBigTask=del&paramd=1";
			document.achievementTaskForm.target="mil_body";
			document.achievementTaskForm.submit();	
		 } 
	})
  }
  //撤销任务
  function delTask()
  {
  		var objs=document.getElementsByName("ids");
  		var values="";
  		for(var i=0;i<objs.length;i++)
  		{
  			if(objs[i].checked)
  			{
  				values+="`"+objs[i].value;
  			}
  		}
  		if(values.length==0)
  		{
  			Ext.showAlert(PLASE_SELECT_RECORD+"!");
  			return;
  		}
  		Ext.showConfirm(ACHIEVEMENT_INFO2+'?',function(flag){
  			if(flag=="yes"){
  				document.achievementTaskForm.selectedIds.value=values;
		  		document.achievementTaskForm.action="/performance/achivement/achivementTask.do?b_delTask=del";
		  		document.achievementTaskForm.submit();
  			}
  		});
  }
  
  //删除任务书
  function delTarget(target_id)
  {
	Ext.showConfirm(ACHIEVEMENT_INFO3+'?',function(flag){
		 if(flag=="yes"){
    		 var In_paramters="target_id="+target_id;  	 	
   	    	 var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'9020020109'});
		 } 
	})
  //	parent.mil_menu.delTarget2(target_id);
  }
  
  function returnInfo(outparamters)
  {
        //    document.achievementTaskForm.action="/performance/achivement/achivementTask.do?br_init=int";
        //    document.achievementTaskForm.target="il_body";
	  	//	  document.achievementTaskForm.submit();
	  //	parent.mil_menu.location.reload();
	  	var str=parent.mil_menu.location.href;
	    parent.mil_menu.location=str;
  }
  
  //多指标删除任务书
  function delBigTarget(target_id)
  { 
  	Ext.showConfirm(ACHIEVEMENT_INFO3+'?',function(flag){
		 if(flag){
    		  var In_paramters="target_id="+target_id;  	 	
   	     var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfoto,functionId:'9020020109'});  
		 } 
	})
  } 
  function returnInfoto(outparamters)
  {      
	  var str=parent.parent.mil_menu.location.href;
	  parent.parent.mil_menu.location=str;
  }
  
  
  
   function selectPoint(obj)
  {
  	if(obj.value.length>0)
  	{
	   var In_paramters="pointsetid="+obj.value;  	 	
   	   var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showFieldList,functionId:'9020020103'});  
  	}
  	else
  	{
  		 var In_paramters="pointsetid=-1";  	 	
   	    var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showFieldList,functionId:'9020020103'});  
  	}
  }
  
  function showFieldList(outparamters)
  {
		var fieldlist=outparamters.getValue("fieldlist");	
		AjaxBind.bind(achievementTaskForm.left_fields,fieldlist);
  }
  //判断任务书中的指标是否重复
  function tagetRepeat()
  {
  		var left_name= document.getElementsByName('left_fields');
		var num=0;
	    if(left_name==null)
	    {
	   		return false;
	   	}	   	
		var left_num=left_name[0];				
		for(i=0;i<left_num.options.length;i++)
		{
		    if(left_num.options[i].selected)
		    {
		    	var a_value=left_num.options[i].value;
		    	
		    	for(var j=0;j<document.achievementTaskForm.right_fields.options.length;j++)
		    	{
		    		if(document.achievementTaskForm.right_fields.options[j].value==a_value)
		    			num++;		    
		    	}
		    }
  		}
		if(num>0)
		{
			alert(ITEM_NOT_RESET+"!");
			return;
		}else{
		    additem('left_fields','right_fields');
		    removeitem('left_fields');
		} 		
  }
  
  function sub()
  {
  	   
	    for(var i=0;i<document.achievementTaskForm.right_fields.options.length;i++)
	    {
	    	var num=0;
		    var a_value=document.achievementTaskForm.right_fields.options[i].value;
		    for(var j=0;j<document.achievementTaskForm.right_fields.options.length;j++)
		    {
		    	if(document.achievementTaskForm.right_fields.options[j].value==a_value)
		    		num++;
		    
		    }
		    if(num>1)
		    {
		    	alert(ITEM_NOT_RESET+"!");
		    	return;
		    }
		}
		
		
		if(trim(document.getElementsByName("targetColumnList["+nameIndex+"].value")[0].value).length==0)
		{
			alert("任务书名称不能为空");
			return;
		}
		if(document.achievementTaskForm.right_fields.options.length==0)
		{
			alert(ACHIEVEMENT_INFO4+"!");
			return;
		}
  		setselectitem('right_fields');
  		document.achievementTaskForm.action="/performance/achivement/achivementTask.do?b_saveTarget=save&callbackFunc_sub="+callbackFunc;
  		document.achievementTaskForm.submit();
  }
  function downloadTemp(target_id){
  	var hashvo=new ParameterSet();
  	hashvo.setValue("target_id",target_id);
  	hashvo.setValue("cycle",achievementTaskForm.cycle.value);
  	hashvo.setValue("sql_whl2",sql_whl2);
  	var request=new Request({method:'post',asynchronous:false,onSuccess:showfile1,functionId:'9020020114'},hashvo);
  }
  function showfile1(outparamters){
  	var outName=outparamters.getValue("outName");
//  	outName=getDecodeStr(outName);
//  	var name=outName.substring(0,outName.length-1)+".xls";
//  	name=getEncodeStr(name);
  	window.location.target="_blank";
  	//20/3/5 xus vfs改造
  	window.location.href = "/servlet/vfsservlet?fileid="+outName+"&fromjavafolder=true";
  }
  function importdata(target_id){
		var uploadObj =  Ext.create("SYSF.FileUpLoad",{
			renderTo:Ext.getBody(),
			emptyText:"请输入文件路径或选择文件",
			upLoadType:1,
			fileExt:"*.xlsx;*.xls;",
			isTempFile:false,
			VfsFiletype:VfsFiletypeEnum.doc,
			VfsModules:VfsModulesEnum.FW,
			VfsCategory:VfsCategoryEnum.other,
			CategoryGuidKey:'',
			success:function(list){
				var obj = list[0];
				var hashvo=new HashMap();     
	   			hashvo.put("filename",obj.filename);
				hashvo.put("path",obj.path);
				hashvo.put("target_id",target_id);
				//20/3/5 xus vfs改造
				hashvo.put("fileid",obj.fileid);
	    		Rpc({functionId:'9020020115',async:false,success:function(res){
	    			Ext.getCmp("importWin").close();
	    			var data = Ext.decode(res.responseText);
	    			if(data.succeed) {
		    			if(Ext.isEmpty(data.errorname))
						{
							Ext.showAlert('成功导入'+data.okcount+'条！',function(){
								document.achievementTaskForm.action="/performance/achivement/achivementTask.do?b_search=query&target_id="+target_id;
								document.achievementTaskForm.target="mil_body";
								document.achievementTaskForm.submit();
							});
						}else{
							Ext.showAlert('导入失败！',function(){
								document.achievementTaskForm.action="/performance/achivement/achivementTask.do?b_search=query&target_id="+target_id;
								document.achievementTaskForm.target="mil_body";
								document.achievementTaskForm.submit();
							});
						}
	    			}else {
	    				Ext.showAlert(data.message);
	    			}
	    		}},hashvo);
			}
		});
		Ext.widget("window",{
			id:'importWin',
  			title: '导入数据',
            modal:true,
            border:false,
            resizable:false,
            width:380,
  			height: 130,
            items:[{
               xtype: 'panel',
               border:false,
        	   layout:{  
	             	type:'vbox',  
	             	padding:'15 0 0 30', //上，左，下，右 
	             	pack:'center',  
	              	align:'middle'  
	            },
               items:[uploadObj]
           },{
           		xtype: 'label',
           		style:'top:22px;left:22px',
           		html:'<font style="color:#757575;">说明：请用下载的Excel模板来导入数据(模板格式不允许修改)！</font>'
           }]
	    }).show(); 
  }
  
  