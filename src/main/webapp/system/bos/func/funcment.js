  function changeCycle(target_id)
  {
  		document.achievementTaskForm.action="/performance/achivement/achivementTask.do?b_search=query1&onePage=1&target_id="+target_id;
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
       var theurl="/performance/achivement/achivementTask.do?br_initBatchupdate=new`cycle="+document.achievementTaskForm.cycle.value+"`target_id="+target_id;
    	var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
   		var retvo= window.showModalDialog(iframe_url, 'template_win', 
      				"dialogWidth:350px; dialogHeight:200px;resizable:no;center:yes;scroll:yes;status:no");
  		if(retvo!=null)
  		{
  			document.achievementTaskForm.action="/performance/achivement/achivementTask.do?b_search=query1&target_id="+target_id;
	  		document.achievementTaskForm.submit();
  		}
  }


  //批量修改
  function batchUpdate(target_id)
  {
  		if(document.achievementTaskForm.point_value.value.length==0)
  			window.close();		
  		document.achievementTaskForm.action="/performance/achivement/achivementTask.do?b_updateValue=save&opt=1&target_id="+target_id;
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
			parent.mil_body.location="/system/bos/func/functionMain.do?b_search=query&onePage=1&target_id=-1";
		
		}
		
	}
	
	
	function alterTreeNode(root_url)
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
				
				var imgurl="/images/open.png";
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
			var tmp = new xtreeItem(temps[3].split("`")[0],temps[3].split("`")[1],"/performance/achivement/achivementTask.do?b_search=query&onePage=1&target_id="+temps[3].split("`")[0],"mil_body",temps[3].split("`")[1],imgurl,"");
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
		 
		var txt="<select  name='"+point_id+"_2' >";
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
  	
  	//取得条件内容
  	function getContext()
  	{
  		var txt="<table width='100%'    ><tr><td> <div id='a3'><table width='100%'  style='margin-top:-1'    border='0' cellspacing='1'  id='a_table' align='center' cellpadding='1' class='ListTable'   >";
  		txt+="<thead><tr   style='position:relative;top:expression(this.offsetParent.scrollTop-1);' ><td align='center'  class='TableRow' nowrap>逻辑符</td><td align='center'  class='TableRow' nowrap>查询指标</td><td align='center'  class='TableRow' nowrap>关系符</td><td align='center'  class='TableRow' nowrap>查询值</td></tr> </thead>";
  		for(var i=0;i<document.achievementTaskForm.right_fields.options.length;i++)
  		{
  			txt+=getRowTxt(document.achievementTaskForm.right_fields.options[i].value,document.achievementTaskForm.right_fields.options[i].text,i);
  			
  		}
  		txt+="</table></div> </td></tr><tr><td align='center' ><br>";
  		
  		txt+="<Input type='button' value='"+UP_GO+"'  class='mybutton'  onclick='up()'  />&nbsp;&nbsp;";
  		txt+="<Input type='button' value='"+SUBMIT+"'  class='mybutton'  onclick='subSearch()'  />&nbsp;&nbsp;";
  		txt+="<Input type='button' value='"+CANCEL+"'  class='mybutton'  onclick='javscript:window.close()'  />";
  		
  		txt+="</td></tr></table>";
  		return txt;
  	}
  	
  	//提交查询
  	function subSearch()
  	{
  		var str_sql="";
  		for(var i=0;i<document.achievementTaskForm.right_fields.options.length;i++)
  		{
  			var temp=document.achievementTaskForm.right_fields.options[i].value;
  			
  			var obj0=eval('document.achievementTaskForm.'+temp+"_0");
  			var obj1=eval('document.achievementTaskForm.'+temp+"_1");
  			var obj2=eval('document.achievementTaskForm.'+temp+"_2"); 
  			if(temp=='kh_cyle'&&obj2.value=='-1')
  				continue;
  			
  			if(trim(obj2.value).length>0)
  			{
	  			if(obj0.value=='0')
	  					str_sql+=" and  " 
	  			else
	  					str_sql+=" or  ";
	  			if(temp=='a0101')
	  			{
	  				str_sql+="a0101 like '%"+obj2.value+"%'"
	  			}
	  			else if(temp=='kh_cyle')
	  			{
	  				str_sql+="kh_cyle ='"+obj2.value+"'"
	  			}
	  			else
	  			{
	  				if(!checkIsNum(obj2.value))
			  		{
			  			obj2.value='';
			  			obj2.focus();
			  			alert(ACHIEVEMENT_INFO1+"!");
			  			return;
			  		}	
			  		str_sql+="T_"+temp+obj1.value+obj2.value;		
	  			}
  			}
  		}
  		if(str_sql.length==0)
  		{
  			returnValue="";
  		}
  		else
  		{
  			returnValue=str_sql.substring(4);	
  			
  		}
  		window.close();
  	}
  	
  	
  	//生成table行代码
  	function getRowTxt(point_id,point_name,i)
  	{
  	
  		
  		var text="<tr ";
  		if(i%2==0)
  			text+="  class='trShallow' ><td align='center' class='RecordRow' ><select";
  		else
  			text+="  class='trDeep' ><td  align='center' class='RecordRow' ><select";
  			
  		if(i==0)
  			text+=" style='display:none' ";
  		text+=" name='"+point_id+"_0' ><option value='0'>"+GENERAL_AND+"</option><option value='1'>"+GENERAL_OR+"</option></select></td>";
  		text+="<td class='RecordRow' >"+point_name+"</td>";
  		if(point_id=='a0101')
  		{
  			text+="<td class='RecordRow' ><select name='"+point_id+"_1' ><option value='like'>=</option></select> </td>";
  			text+="<td class='RecordRow' ><input type='text' name='"+point_id+"_2' value='' /> </td>";
  		}	
  		else if(point_id=='kh_cyle')
  		{
  			text+="<td class='RecordRow' ><select name='"+point_id+"_1' ><option value='='>=</option></select> </td>";
  			text+="<td class='RecordRow' >";
  			text+=getSelectTxt(point_id);
  			text+="</td>";
  		}
  		else
  		{
  			text+="<td class='RecordRow' ><select name='"+point_id+"_1' ><option value='='>=</option><option value='>'>></option><option value='<'><</option><option value='>='>>=</option><option value='<='><=</option></select> </td>";
  			text+="<td class='RecordRow' ><input type='text' name='"+point_id+"_2' value='' /> </td>";
  		}
  		text+="</tr>";
  		
  		return text;
  	}
  
  
  
  //指标排序
  function setPointSort(target_id)
  {
  		var theurl="/performance/achivement/achivementTask.do?b_showPointList=show`target_id="+target_id;
    	var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
   		var retvo= window.showModalDialog(iframe_url, 'template_win', 
      				"dialogWidth:380px; dialogHeight:350px;resizable:no;center:yes;scroll:yes;status:no");
  		if(retvo==null)
  	 		return ;		        
		if(retvo=="1")
		{
			document.achievementTaskForm.action="/performance/achivement/achivementTask.do?b_search=query&target_id="+target_id;
	  		document.achievementTaskForm.submit();
		}
  }
 
 
 //指标排序
 function subPointSort()
   {	
   		setselectitem('right_fields');
   		document.achievementTaskForm.action="/performance/achivement/achivementTask.do?b_savePointSort=save";
   		document.achievementTaskForm.submit();
   }


//保存业绩任务书标准值
  function saveData()
  {
 		document.achievementTaskForm.action="/performance/achivement/achivementTask.do?b_saveTaskData=save";
	  	document.achievementTaskForm.submit();
  }
  
  //检验数字类型
  function checkValue(obj)
  {
  	if(obj.value.length>0)
  	{
  		if(!checkIsNum(obj.value))
  		{
  			obj.value='';
  			obj.focus();
  		}
  	}
  
  }
 
   function openEdit(id)
  {
  	    var theurl="/system/bos/func/functionMain.do?b_editFunc=new`functionid="+id;
    	var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
   		var retvo= window.showModalDialog(iframe_url, 'template_win', 
      				"dialogWidth:500px; dialogHeight:250px;resizable:no;center:yes;scroll:yes;status:no");
  		
  		if(retvo!=null&&retvo!='undefined')
  		{
  			parent.mil_menu.alterTreeNode(retvo)
  		}
  }

  
  //修改任务书
  function editTarget(target_id)
  {
  	    var theurl="/performance/achivement/achivementTask.do?b_newTarget=new`opt=edit`target_id="+target_id;
    	var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
   		var retvo= window.showModalDialog(iframe_url, 'template_win', 
      				"dialogWidth:550px; dialogHeight:350px;resizable:no;center:yes;scroll:yes;status:no");
  		
  		if(retvo!=null&&retvo!='undefined')
  		{
  			parent.mil_menu.alterTreeNode(retvo)
  		}
  }
  
  
  //任务分配
  function allocateTask(target_id)
  {
  		var theurl="/performance/achivement/achivementTask.do?b_allocateTask=allocate`target_id="+target_id;
    	var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
   		var retvo= window.showModalDialog(iframe_url, 'template_win', 
      				"dialogWidth:550px; dialogHeight:390px;resizable:no;center:yes;scroll:yes;status:no");
  		if(retvo==null)
  	 		return ;		        
		if(retvo=="1")
		{
			document.achievementTaskForm.action="/performance/achivement/achivementTask.do?b_search=query&target_id="+target_id;
	  		document.achievementTaskForm.submit();
		}
  }
  
  
  
  //删除任务书
  function delTarget(target_id)
  {
  		
  	if(confirm(ACHIEVEMENT_INFO3+'?'))
  	{
  		 var In_paramters="target_id="+target_id;  	 	
   	     var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'9020020109'});  
  	}
  	
  //	parent.mil_menu.delTarget2(target_id);
  }
 
  function showFieldList(outparamters)
  {
		var fieldlist=outparamters.getValue("fieldlist");	
		AjaxBind.bind(achievementTaskForm.left_fields,fieldlist);
  }
     

 
	