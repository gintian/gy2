	function openwin(url)
	{
	  var win=open(url,"info");
	 
	}
    

	 //显示员工日志
	 function showWordDiary(plan_id,a0100,startDate,endDate)
	 {
	 		  var _width=window.screen.width-200;
	 		 var _url="/performance/workdiary/workdiaryshow.do?b_query=link&timeflag=1&logo=1&plan_id="+plan_id+"&a0100="+a0100+"&start_date="+startDate+"&end_date="+endDate;
			 //在新窗口中打开 否则firefox 会默认替换已打开过的窗口  haosl 2018-3-26
			 window.open(_url,"_blank","width="+_width+",height=700,top=50,left=50,resizable=no,status=no,toolbar=no,scrollbars=yes,menubar=no,location=no"); 
	 
	 }

	//计划说明
	function planDescript(planid)
	{
		var thecodeurl="/performance/objectiveManage/myObjective/my_objective_list.do?b_reject=link`opt=2`plan_id="+planid; 
		var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
		// var retvo= window.showModalDialog(iframe_url, null,
		// 				        "dialogWidth:585px; dialogHeight:500px;resizable:no;center:yes;scroll:yes;status:no");
		var config = {
		    width:585,
            height:410,
            type:'2'
        }
		modalDialog.showModalDialogs(iframe_url,'planDescriptwin',config);
	}


   
    function changeColor(obj)
    {
    	obj.color='#980034'
    }
    
    function changeColor2(obj)
    {
    	obj.color='#0158AF'
    }
    
    
    function showDateSelectBox2(srcobj)
   {
     
      date_desc=srcobj;
      Element.show('date_panel');   
      var pos=getAbsPosition(srcobj);
      window.screen.availWidth
	  with($('date_panel'))
	  {
	        style.position="absolute";
	        if(typeof(window.addEventListener)=="function")
	        {
	        	style.left=window.screen.availWidth/2-350;
	        	style.top=pos[1]+srcobj.offsetHeight;  
	        }
	        else
	        {
	        	style.posLeft=window.screen.availWidth/2-350;
	        	style.posTop=pos[1]+srcobj.offsetHeight;  
	        }
	        
      }  
      var In_paramters="point_id="+srcobj.id; 		
	 
	  var dataHtml="";
	  dataHtml+="<table width='500' border='0' cellspacing='0' bgColor='#FFFFFF'  align='center' cellpadding='0' class='ListTable'   > ";
	  dataHtml+="<thead><tr> <td  width='60'  align='center' class='TableRow' nowrap >"+P_STANDPOINT+"</td>";
	  dataHtml+="<td width='260' align='center' class='TableRow' nowrap > &nbsp;&nbsp;&nbsp;"+P_POINTDESC+"&nbsp;&nbsp;&nbsp;</td>";			
	  dataHtml+="<td width='60' align='center' class='TableRow' nowrap > &nbsp;&nbsp;&nbsp;"+PERCENT+"&nbsp;&nbsp;&nbsp;</td>";			
	  dataHtml+="<td width='60' align='center' class='TableRow' nowrap > &nbsp;&nbsp;"+P_UVALUE+"&nbsp;&nbsp;</td>";
	  dataHtml+="<td width='60' align='center' class='TableRow' nowrap >&nbsp;&nbsp;"+P_BVALUE+"&nbsp;&nbsp;</td> </tr>  </thead>";
	  var gradeList=point_grade['p'+srcobj.id];
	  if(typeof(gradeList)=="undefined")
	  {
				alert(P_FRESHDATA2+"!");
				return;
	  }	
	  for(var i=0;i<gradeList.length;i++)
	  {
	       var temp=gradeList[i];
	       
	  		dataHtml+="<tr";
			if(i%2==0)
				dataHtml+=" background-color: #FFFFFF; ";
			else
				dataHtml+=" class='trDeep' ";
				
			if(temp.subsys_id!="undefined" && temp.subsys_id=='35')
			{
				if(typeof(per_competencedegree[temp.gradecode])=="undefined")
				{
					alert(P_NOSAME+"!");
					return;
				}
			}else
			{
				if(typeof(per_standdegree[temp.gradecode])=="undefined")
				{
					alert(P_NOSAME+"!");
					return;
				}
			}
			//给table 左边边框不显示，添加border-left 样式 wangb 20180109
			if(temp.subsys_id!="undefined" && temp.subsys_id=='35')			
				dataHtml+="><td align='left' style='border-left:solid 1px #C4D8EE;'  class='RecordRow'  nowrap >"+per_competencedegree[temp.gradecode].gradedesc+"</td>";
			else
				dataHtml+="><td align='left' style='border-left:solid 1px #C4D8EE;' class='RecordRow'  nowrap >"+per_standdegree[temp.gradecode].gradedesc+"</td>";
			dataHtml+="<td align='left'  class='RecordRow' >"+temp.gradedesc+"</td>";
			dataHtml+="<td  align='left' class='RecordRow' nowrap >"+temp.gradevalue+"</td>";
			dataHtml+="<td  align='left' class='RecordRow' nowrap >"+temp.top_value+"</td>";
			dataHtml+="<td  align='left' class='RecordRow' nowrap >"+temp.bottom_value+"</td></tr>";
	  
	  }
	  dataHtml+="</table>";	
	 
	 
	  dataHtml=replaceAll(dataHtml,"#@#","<br>");	
	  date_panel.innerHTML=dataHtml;
	  //非IE浏览器下，iframe 显示边框 添加display样式不显示iframe   wangb 20180109
	  date_panel.innerHTML=date_panel.innerHTML+"<iframe src=\"javascript:false\" style=\"position:absolute; visibility:inherit; top:0px; left:0px;display:none;"
   			 				+"width:"+date_panel.offsetWidth+"; height:"+date_panel.offsetHeight+"; " 					    	
   			 				+"z-index:-1;filter='progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)';\"></iframe>";   				
		

   }
   
    
     function showDateSelectBox(srcobj)
   {
     
      date_desc=srcobj;
      Element.show('date_panel');   
      var pos=getAbsPosition(srcobj);
      window.screen.availWidth
	  with($('date_panel'))
	  {
	        style.position="absolute";
	        if(typeof(window.addEventListener)=="function")
	        {
	        	style.left=window.screen.availWidth/2-350;
	        	style.top=pos[1]+srcobj.offsetHeight;  
	        }
	        else
	        {
	        	style.posLeft=window.screen.availWidth/2-350;
	        	style.posTop=pos[1]+srcobj.offsetHeight;  
	        }
	        
      }  
      
      var In_paramters="point_id="+srcobj.id; 		
	  var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo2,functionId:'90100160009'});
   }
   
   
   
   
	function returnInfo2(outparamters)
	{
		var fieldlist=outparamters.getValue("dataList");	
		var dataHtml=getDecodeStr(outparamters.getValue("dataHtml"));
		dataHtml=replaceAll(dataHtml,"#@#","<br>");	
		date_panel.innerHTML=dataHtml;
		date_panel.innerHTML=date_panel.innerHTML+"<iframe src=\"javascript:false\" style=\"position:absolute; visibility:inherit; top:0px; left:0px;"
   			 				+"width:"+date_panel.offsetWidth+"; height:"+date_panel.offsetHeight+"; " 					    	
   			 				+"z-index:-1;filter='progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)';\"></iframe>";   				
		//alert(date_panel.offsetWidth+"   "+date_panel.offsetHeight)
		// 显示的指标解释 界面,缺少边线  bug 35522 wangb 20180322 
		var datePanel = document.getElementById('date_panel');
	    var tablePanel = datePanel.getElementsByTagName('table')[0];
        tablePanel.setAttribute('border','1');
        tablePanel.setAttribute('bordercolor','#C4D8EE');
        var iframePanel = datePanel.getElementsByTagName('iframe')[0];
		iframePanel.style.display = 'none';
		
	}
  
   
   function replaceAll( str, from, to ) {
	    var idx = str.indexOf( from );
	    while ( idx > -1 ) {
	        str = str.replace( from, to ); 
	        idx = str.indexOf( from );
	    }
	   
	    return str;
	}
	
   function hiddenData()
   {//为了适应火狐而加的方法
   	Element.hide('date_panel');
   }
   function hidden()
   {
   	Element.hide('date_panel');
   }
    
    
    
	function windowOpen(url)
	{
		var new_obj=window.open(url,'_blank','height=460, width=600')
	
	}
	
	
		
	function showWindow(plan_id,object_id,mainbody_id)
	{
		 //在新窗口中打开 否则firefox 会默认替换已打开过的窗口  haosl 2018-3-26
		var win=window.open("/performance/markStatus/markStatusList.do?b_edit3=edit&planID="+plan_id+"&objectID="+object_id+"&mainbodyID="+mainbody_id,"_blank","width=530,height=450,top=50,left=50,resizable=no,status=no,toolbar=no,scrollbars=yes,menubar=no,location=no"); 
		//var win=open("/performance/markStatus/markStatusList.do?b_edit3=edit&planID="+plan_id+"&objectID="+object_id+"&mainbodyID="+mainbody_id,"info");
	}
	
	
	
    
    
    
    function setStatus(obj)
	{
		var temp=obj.name.split("_");	
		var obj1=eval(temp[0]);
		
		var hashvo=new ParameterSet();			
		hashvo.setValue("planID",temp[1]);	
		hashvo.setValue("objectID",temp[0].substring(1));
		hashvo.setValue("mainbodyID",temp[2]);
		hashvo.setValue("operater","3");	
		hashvo.setValue("performanceType",performanceType);	
		if(obj.checked==true)
		{
			obj1.style.display="block";
			hashvo.setValue("isNoMark","4");
			hashvo.setValue("reasons","123");
		}
		else
		{
			//obj1.style.display="none";
			hashvo.setValue("isNoMark","0");
		}
		var In_paramters="flag=flag"; 
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo3,functionId:'90100170004'},hashvo);
	}
	
	
	
function returnInfo3(outparamters)
{
		var objectID=outparamters.getValue("objectID");
		var isNoMark=outparamters.getValue("isNoMark");
		var temp_arr=points.split("/");
		var description = outparamters.getValue("description");
		var reasons = outparamters.getValue("reasons");
		if((description=="" || description==null) && reasons=="123"){
			alert("请先填写不评价原因！");
			window.location.href=window.location.href;
			return;
		}
		if(isNoMark==4)
		{
					var is_per_degreedesc=0;
					for(var i=0;i<temp_arr.length;i++)
					{
						
						if(temp_arr[i]!='per_degreedesc')
						{
							
							if(temp_arr[i]=='per_know')
							{
								document.getElementById(objectID+"_"+temp_arr[i]).innerHTML="";
							}
							else
							{
								if(radioEval=='1')
								{
									var obj=document.getElementsByName(objectID+"~"+temp_arr[i].substring(1));
									for(var n=0;n<obj.length;n++)
									{
										obj[n].checked=false;
										obj[n].disabled=true;
									}								
								}
								else
								{
									if(document.getElementById(objectID+"_"+temp_arr[i].substring(1)))
										document.getElementById(objectID+"_"+temp_arr[i].substring(1)).innerHTML="";
								}						
							}
							var temp_arry=obj_result["_"+objectID][temp_arr[i]].split("/");
							if(temp_arr[i]!='per_know')
							{
								obj_result["_"+objectID][temp_arr[i]]="null/"+temp_arry[1]+"/0/"+temp_arry[3];
							}
							else
							{
								obj_result["_"+objectID][temp_arr[i]]="null/0";
							}
							obj_values['_'+objectID][temp_arr[i]]="null";
							
						}
						else
							is_per_degreedesc=1;
					}
					
					if(is_per_degreedesc==1)
					{
						
						document.getElementById("a"+objectID).options[0].selected=true;
						document.getElementById("a"+objectID).disabled=true;
						obj_result["_"+objectID]["per_degreedesc"]="null/0";
						obj_values['_'+objectID]["per_degreedesc"]="null";
					}
					if(wholeEvalMode=="1"){
						var id= 'wholeEvalScoreId_'+objectID;
						var whole_scoreId = document.getElementById(id);
						if(whole_scoreId!=null){
							whole_scoreId.value='0.0';
							whole_scoreId.disabled=true;
						}
					}
		}
		else 
		{
				for(var i=0;i<temp_arr.length;i++)
				{
						var is_per_degreedesc=0;
						if(temp_arr[i]=='per_degreedesc')
							is_per_degreedesc=1;
						var temp_arry=obj_result["_"+objectID][temp_arr[i]].split("/");
						if(temp_arr[i]!='per_degreedesc'&&temp_arr[i]!='per_know')
						{	
							obj_result["_"+objectID][temp_arr[i]]=temp_arry[0]+"/"+temp_arry[1]+"/1/"+temp_arry[3];
							if(radioEval=='1')
							{
									var obj=document.getElementsByName(objectID+"~"+temp_arr[i].substring(1));
									for(var n=0;n<obj.length;n++)
									{
										obj[n].disabled=false;
									}								
							}
								
						}
						else
							obj_result["_"+objectID][temp_arr[i]]="null/1";
				}
				
				if(is_per_degreedesc==1)
				{
					document.getElementById("a"+objectID).disabled=false;
				}	
				if(wholeEvalMode=="1"){
					var id= 'wholeEvalScoreId_'+objectID;
					var whole_scoreId = document.getElementById(id);
					if(whole_scoreId!=null){
						whole_scoreId.disabled=false;
					}
				}
			window.location.href=window.location.href;	
		}


}
	


		
	function go_left(objid,pointid){
		var index=0;
	   	for(var i=0;i<point_arr.length;i++)
	    {
	    	if('p'+pointid==point_arr[i])
	    		index=i-1;
	    }
	    if(index!=-1)
	    {
	    	var userResult=obj_values['_'+objid];
			var temp_arry=obj_result["_"+objid][point_arr[index]].split("/");	
	    	if(point_arr[index]=='per_degreedesc'||point_arr[index]=='per_know'||(temp_arry[1]=='0'&&temp_arry[2]=='0')||(point_grade[point_arr[index]][0].pointkind=="0"&&scoreflag=="1"))
	    	{
	    	
	    	}
	    	else
		    {
		    	
		    	setValue2(document.getElementById("in_put"),objid,pointid);
		    	
		    	var obj=document.getElementById(objid+"_"+point_arr[index].substring(1));
		    	if(typeof(window.addEventListener)=="function")
		    	{
		    		obj.onclick();
		    	}	
		    	else
		    		obj.fireEvent("onclick");
		    	
		    	
		    }
	    }
	    
	   
	 }
	  
	function go_right(objid,pointid){
	 	var index=0;
	   	for(var i=0;i<point_arr.length;i++)
	    {
	    	if('p'+pointid==point_arr[i])
	    		index=i+1;
	    }
	    if(index<point_arr.length)
	    {
	    	
	        var userResult=obj_values['_'+objid];
			var temp_arry=obj_result["_"+objid][point_arr[index]].split("/");	
	    	if(point_arr[index]=='per_degreedesc'||point_arr[index]=='per_know'||(temp_arry[1]=='0'&&temp_arry[2]=='0')||(point_grade[point_arr[index]][0].pointkind=="0"&&scoreflag=="1"))
	    	{
	    	}
	    	else
	    	{
	    		setValue2(document.getElementById("in_put"),objid,pointid);
	    		
		    	var obj=document.getElementById(objid+"_"+point_arr[index].substring(1));
		    	if(typeof(window.addEventListener)=="function")
		    		obj.onclick();
		    	else
		    		obj.fireEvent("onclick");
		    	
	    	}
	    }
	 }
	  
	  
	function go_up(objid,pointid){
	    var index=0;
	   	for(var i=0;i<user_arr.length;i++)
	    {
	    	if(objid==user_arr[i])
	    		index=i-1;
	    }
	    if(index!=-1)
	    {
	    	setValue2(document.getElementById("in_put"),objid,pointid);
	    	
	    	var obj=document.getElementById(user_arr[index]+"_"+pointid);
	    	if(typeof(window.addEventListener)=="function")
		    	obj.onclick();
		    else
		    	obj.fireEvent("onclick");
	    	
	    }
	 }
	  
	  
	function go_down(objid,pointid){
	 	var index=0;
	   	for(var i=0;i<user_arr.length;i++)
	    {
	    	if(objid==user_arr[i])
	    		index=i+1;
	    }
	    if(index<user_arr.length)
	    {
	    	setValue2(document.getElementById("in_put"),objid,pointid);
	    	
	    	var obj=document.getElementById(user_arr[index]+"_"+pointid);
	    	if(typeof(window.addEventListener)=="function")
		    	obj.onclick();
		    else
		    	obj.fireEvent("onclick");
	    	
	    }
	 
	}


function excecuteTable()
{
	var titlename;
	for(var i=0;i<document.batchGradeForm.dbpre.options.length;i++)
	{
		if(document.batchGradeForm.dbpre.options[i].selected==true)
			titlename=document.batchGradeForm.dbpre.options[i].text;
	}
	var objects=eval("batchGradeForm.titleName");	
	objects.value=titlename;
	batchGradeForm.action="/selfservice/performance/batchGrade.do?b_Desc=query&selectNewPlan=true";
	batchGradeForm.submit();
}

function replaceAll( str, from, to ) {
	    var idx = str.indexOf( from );
	    while ( idx > -1 ) {
	        str = str.replace( from, to ); 
	        idx = str.indexOf( from );
	    }
	   
	    return str;
}




function showInfo(planid)
{
	var win=open("/servlet/performance/markStatus/showIndexInfo?plan_id="+planid,"info");
	
}




	var old_obj;
        var old_var;
	
	
	function show(a_var ,objs)
	{
	   if(eval(a_var+"_1").style.display!="block")
	   {
		if(old_obj!=null&&old_var!=null)
		{
			hiddens(old_var,old_obj);
		}
		var a_value=objs.innerHTML;	
		if(a_value.indexOf("<DIV")>=0)	
			objs.innerHTML=a_value.substring(a_value.indexOf("<DIV"));	
		if(a_value.indexOf("<input")>=0)	
			objs.innerHTML=a_value.substring(a_value.indexOf("<input"));
		eval(a_var+"_1").style.display="block"; 		
		old_obj=objs;
		old_var=a_var;
	   }	
	}
	
	function hidden_view()
	{
		if(old_obj!=null&&old_var!=null)
		{
			
			hiddens(old_var,old_obj);
		}
	}
	
	function hiddens(a_var,objs)
	{
		
		var t_arr=a_var.split("_");		
		var a_object3=eval("batchGradeForm."+t_arr[0]);
		
		if(a_object3[t_arr[1]].type=='select-one')
		{
			for(var i=0;i<a_object3[t_arr[1]].options.length;i++)
			{
				if(a_object3[t_arr[1]].options[i].selected==true)
				{
					
					objs.innerHTML=a_object3[t_arr[1]].options[i].text+objs.innerHTML;
					//a_object2.innerHTML=a_object3[t_arr[1]].options[i].text;
				}
				
			
			}
		}
		else
		{
			objs.innerHTML=a_object3[t_arr[1]].value+objs.innerHTML;
		
		}			
		eval(a_var+"_1").style.display="none"; 
	}	
	
	
	
	
	
	//////////////////////////////////////////////////////////////
	


	
function selectRow(obj)
{
		var table=document.getElementById("selectOption");
		var rowCount = table.rows.length;
		for(var i=0;i<rowCount;i++)
			table.rows[i].className='trWhite';
		obj.className='trSelect';
		
}
	
	
function setValue(a0100,pointID,value,valuename)
{
		//alert(a0100+"  "+pointID+"  "+value+"   "+valuename);
		if(value!="#")
		{
			var userResult=obj_values['_'+a0100];
			if(pointID=='per_degreedesc'||pointID=='per_know')
				userResult[pointID]=value;
			else
				userResult['p'+pointID]=value;	
			document.getElementById(a0100+"_"+pointID).innerHTML=valuename;
		}
		Element.hide('options');
}


function setScorevalue(obj)
{
	
	var temp= obj.name.split("~");
	var userResult=obj_values['_'+temp[0]];
	if(temp[1]=='per_degreedesc'||temp[1]=='per_know')
				userResult[temp[1]]=obj.value;
	else
	{
				userResult['p'+temp[1]]=obj.value;	
			
	}
}


	
	
function setValue2(obj,a0100,pointID)
{
		a_num++;
		if(a_num==1)
		{
			var userResult=obj_values['_'+a0100];
			if(trim(obj.value).length>0)
			{
				
				var tempArray=point_grade['p'+pointID];
				var temp_arry=obj_result["_"+a0100]['p'+pointID].split("/");
				if(tempArray[0].pointkind=="0")  //定性指标
				{
					var isTrue=0;
					for(var i=0;i<tempArray.length;i++)
					{
					
					    if(temp_arry[3]!="#")
						{
							  	 	var up=temp_arry[3].split("~")[0];
							  	 	var down=temp_arry[3].split("~")[1];
							  	 	var num=tempArray[i].gradecode.toLowerCase().charCodeAt(0);
							  	    if(up.toLowerCase().charCodeAt(0)>num||down.toLowerCase().charCodeAt(0)<num)
									     continue;
						}
						if(checkIsNum2(obj.value))   //输入的是分值
						{
							 
							if(!checkNUM2(obj,10,_KeepDecimal))
							{
								userResult['p'+pointID]="null";
								document.getElementById(a0100+"_"+pointID).innerHTML="";	
								Element.hide('inputs');	
								return;
							}
						   /*   if(tempArray[i].top_value*pointScore['p'+pointID]>=obj.value*1&&tempArray[i].bottom_value*pointScore['p'+pointID]<=obj.value*1)
						      {
						       
						      	isTrue=1;
						      	continue;
						      }
						      
						      if(isTrue==0&&evalOutLimitStdScore=='true')
						      {
						      	isTrue=1;
						      	continue;
						      }*/
						      isTrue=1;
						      	continue;
						}
						else                        //输入的是标度
						{
							if(tempArray[i].gradecode.toLowerCase()==obj.value.toLowerCase())
							{
								isTrue=1;
								 break;
							}
						}
						
					}
					if(isTrue==0)
					{
						//obj.value="";
						userResult['p'+pointID]="null";
						document.getElementById(a0100+"_"+pointID).innerHTML="";	
						Element.hide('inputs');	
						alert(NOPERGRADE+"!");
					}
					else
					{
						userResult['p'+pointID]=obj.value.toUpperCase();
						Element.hide('inputs');
						document.getElementById(a0100+"_"+pointID).innerHTML=obj.value.toUpperCase();
						if(score_sumtotal=='true'&&scoreflag=='2')
						{
								 writeSumScore();
								 if(clientName=='zglt'&&_isShowOrder=="true")
								 	setUserOrder();
						}
					}
				}
				else if(tempArray[0].pointkind=="1")  //定量指标
				{
					if(!checkIsNum(obj.value))
					{
						if(userResult['p'+pointID]!='null')	
							document.getElementById(a0100+"_"+pointID).innerHTML=userResult['p'+pointID];
						else
							document.getElementById(a0100+"_"+pointID).innerHTML="";		
							
						Element.hide('inputs');		
					}
					else
					{
						userResult['p'+pointID]=obj.value.toUpperCase();
						document.getElementById(a0100+"_"+pointID).innerHTML=obj.value.toUpperCase();
						Element.hide('inputs');
					}
				}
				
				
			}
			else
			{
				Element.hide('inputs');
				document.getElementById(a0100+"_"+pointID).innerHTML="";
				userResult['p'+pointID]="null";
				
				if(score_sumtotal=='true'&&scoreflag=='2')
				{
								 writeSumScore();
								 if(clientName=='zglt'&&_isShowOrder=="true")
								 	setUserOrder();
				}
				
			}
		}	
		a_num++;
}


function setUserOrder()
{
	var users_arry=users.split("/");
	if(users_arry.length<=30)
	{
	
		var usersScoreOrder=new Array();
		var orderScore=new Array();
		for(var i=0;i<users_arry.length;i++)
		{	
				var str="";
				var userResult=obj_values['_'+users_arry[i]];			
				var temp_arr=points.split("/");
				var sum=0;
				for(var j=0;j<temp_arr.length;j++)
				{
					
					if(temp_arr[j]!='per_degreedesc'&&temp_arr[j]!='per_know'&&trim(userResult[temp_arr[j]]).length>0&&trim(userResult[temp_arr[j]])!='null')
					{
						sum+=userResult[temp_arr[j]]*1;	
					}
					
				}
				usersScoreOrder[i]=[users_arry[i],sum,1];		
				
				var isValue=0;
				for(var j=0;j<orderScore.length;j++)
				{
					if(orderScore[j]==sum)
						isValue=1;
				}
				if(isValue==0)
						orderScore[orderScore.length]=sum;					
		}	
	
		var _temp2=new Array();
		for(var i=0;i<orderScore.length;i++)
		{
			if(i==0)
				_temp2[0]=orderScore[i];
			else
			{
				var isValue=0;
				for(var j=0;j<_temp2.length;j++)
				{
					if(orderScore[i]>_temp2[j])
					{
						for(var e=_temp2.length-1;e>=j;e--)
						{
							_temp2[e+1]=_temp2[e];
						}
						_temp2[j]=orderScore[i];
						isValue=1;
						break;
					}
				}
				if(isValue==0)
					_temp2[_temp2.length]=orderScore[i];		
			}
		}
		
		for(var i=0;i<usersScoreOrder.length;i++)
		{
			var _temp=usersScoreOrder[i];
			for(var j=0;j<_temp2.length;j++)
			{
				if(_temp2[j]==_temp[1])
				{
					usersScoreOrder[i][2]=j+1;
					break;
				}
			}
			var temp2=eval('pm'+i);
			temp2.innerHTML="<font  color='#2E67B9'   >"+usersScoreOrder[i][2]+"</font>";
		} 
	}
}





function setValue3(obj,a0100,pointID)
{
		
		var userResult=obj_values['_'+a0100];
		if(pointID=='per_degreedesc'||pointID=='per_know')
			userResult[pointID]=obj.value.toUpperCase();
		else
			userResult['p'+pointID]=obj.value.toUpperCase();
		
}
	

function closeDiv()
{
		Element.hide('options');
}
function closeInputs()
{
    Element.hide('inputs');
}
	
var a_num=0;
	 
 
function showSetBox(obj,pointID,a0100)
{
		//通过点击输入分值是，输入框的onblur事件有时候不触发，这里手动触发
		var inPutElement = document.getElementById("in_put")
		if(inPutElement){
			inPutElement.blur()
		}

			a_num=0;
			var tempArray;
			if(typeof(point_grade)=="undefined")
			{
				alert(P_FRESHDATA+"!");
				return;
			}
			
			if(pointID=='per_know')
				tempArray=per_know;		
			else
				tempArray=point_grade['p'+pointID];
			
			if(pointID!='per_know'&&typeof(point_grade['p'+pointID])=="undefined"){
				alert(P_FRESHDATA+"!");
				return;
			}
			var userResult=obj_values['_'+a0100];
			var temp_arry;
			if(pointID=='per_know')
				temp_arry=obj_result["_"+a0100][pointID].split("/");
			else
				temp_arry=obj_result["_"+a0100]['p'+pointID].split("/");
			
			if(temp_arry[1]!='0'&&temp_arry[2]!='0')
			{
				if(pointID=='per_know'||(tempArray[0].pointkind=="0"&&scoreflag=="1")) //下拉框
				{
					
					
					  Element.show('options');   
				      var pos=getAbsPosition(obj);
				      window.screen.availWidth
					  with($('options'))
					  {
					        style.position="absolute";
					        if(typeof(window.addEventListener)=="function")
					        {
					        	style.left=pos[0];
					        	style.top=pos[1];  
					        }
					        else
					        {
					        	style.posLeft=pos[0];
					        	style.posTop=pos[1];  
					        }
					        
				      }  
	      
					  var inner="<table id='selectOption'       class='table_class' bgColor='#ffffff' >";
					 
					  inner+="<tr   "; 
					  inner+=" onclick='setValue(\""+a0100+"\",\""+pointID+"\",\"null\",\"\")'  ><td style='cursor:pointer;width:"+(obj.offsetWidth-9)+";' class='table_td' >&nbsp;";
					  inner+="</td></tr>";
					  
					  if(pointID=='per_know'||temp_arry[3]!='0')
					  {
						  for(var i=0;i<tempArray.length;i++)
						  {
						    if(pointID!='per_know'&&temp_arry[3]!="#")
						  	{
						  	 	var up=temp_arry[3].split("~")[0];
						  	 	var down=temp_arry[3].split("~")[1];
						  	 	var num=tempArray[i].gradecode.toLowerCase().charCodeAt(0);
						  	    if(up.toLowerCase().charCodeAt(0)>num||down.toLowerCase().charCodeAt(0)<num)
								     continue;
						  	}
						  
						  	inner+="<tr "; //onmouseenter='selectRow(this)'  ";
						  	
						  	if(pointID=='per_know')
						  	{
						  		if(tempArray[i].gradecode==userResult[pointID])
						  			inner+="  bgColor='#FFF8D2'  ";
						  	}
						  	else
						  	{
						  		if(tempArray[i].gradecode==userResult['p'+pointID])
						  		{
						  			inner+="  bgColor='#FFF8D2'  ";
						  			
						  		}
						  	}
						  	//解决firfiex 浏览器下，弹窗width 固定，改为文本不自动换行一行显示在td添加white-space样式    wangb 20180109     
						  	if(pointID=='per_know')
						  	{
						  		inner+=" onclick='setValue(\""+a0100+"\",\""+pointID+"\",\""+tempArray[i].gradecode.toUpperCase()+"\",\""+tempArray[i].gradedesc+"\")'  ><td  style='cursor:pointer;white-space:nowrap;' class='table_td'  >&nbsp;&nbsp;&nbsp;";
						  		inner+=tempArray[i].gradedesc
						  	}
						  	else
						  	{
						  		
						  		if(DegreeShowType=='1')
							  	{	
							  		if(tempArray[i].subsys_id!="undefined" && tempArray[i].subsys_id=='35')
							  		{
								  		inner+=" onclick='setValue(\""+a0100+"\",\""+pointID+"\",\""+tempArray[i].gradecode.toUpperCase()+"\",\""+per_competencedegree[tempArray[i].gradecode].gradedesc+"\")'  ><td  style='cursor:pointer;white-space:nowrap;' class='table_td'  >&nbsp;&nbsp;&nbsp;";
							  		    inner+=per_competencedegree[tempArray[i].gradecode].gradedesc
						  			}
						  			else
						  			{
						  				inner+=" onclick='setValue(\""+a0100+"\",\""+pointID+"\",\""+tempArray[i].gradecode.toUpperCase()+"\",\""+per_standdegree[tempArray[i].gradecode].gradedesc+"\")'  ><td  style='cursor:pointer;white-space:nowrap;' class='table_td'  >&nbsp;&nbsp;&nbsp;";
						  		    	inner+=per_standdegree[tempArray[i].gradecode].gradedesc
						  			}
						  		}
						  		else if(DegreeShowType=='2')
							  	{	
							  		inner+=" onclick='setValue(\""+a0100+"\",\""+pointID+"\",\""+tempArray[i].gradecode.toUpperCase()+"\",\""+tempArray[i].gradedesc+"\")'  ><td  style='cursor:pointer;white-space:nowrap;' class='table_td'  >&nbsp;&nbsp;&nbsp;";
						  		    inner+=tempArray[i].gradedesc
						  		
						  		}
						  		else if(DegreeShowType=='3')
							  	{
							  		if(tempArray[i].subsys_id!="undefined" && tempArray[i].subsys_id=='35')
							  		{
								  		inner+=" onclick='setValue(\""+a0100+"\",\""+pointID+"\",\""+tempArray[i].gradecode.toUpperCase()+"\",\""+per_competencedegree[tempArray[i].gradecode].gradedesc+"\")'  ><td  style='cursor:pointer;white-space:nowrap;' class='table_td'  >&nbsp;&nbsp;&nbsp;";
								  		inner+=tempArray[i].gradedesc
						  			}
						  			else
						  			{
						  				inner+=" onclick='setValue(\""+a0100+"\",\""+pointID+"\",\""+tempArray[i].gradecode.toUpperCase()+"\",\""+per_standdegree[tempArray[i].gradecode].gradedesc+"\")'  ><td  style='cursor:pointer;white-space:nowrap;' class='table_td'  >&nbsp;&nbsp;&nbsp;";
								  		inner+=tempArray[i].gradedesc
						  			}
						  		}
						  	}
						  	
						  	inner+="&nbsp;&nbsp;&nbsp;</td></tr>";
						  }
					  }
					  
					  inner+="</table>";	
					  
					  options.innerHTML=inner;
					  //非IE浏览器下 iframe 显示边框，添加display 样式 隐藏iframe，不显示边框
					  options.innerHTML=options.innerHTML+"<iframe src=\"javascript:false\" style=\"position:absolute; visibility:inherit; top:0px; left:0px;display:none;"
		   			 			+"width:"+options.offsetWidth+"; height:"+options.offsetHeight+"; " 					    	
		   			 			+"z-index:-1;filter='progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)';\"></iframe>";   					
					  
					
				}
				else //输入框
				{
					 Element.show('inputs');   
				     var pos=getAbsPosition(obj);
				 /*    
					 if((pos[0]+10)>document.getElementById("tbl-container").clientWidth)
					  	return;
					  if((pos[1])>document.getElementById("tbl-container").clientHeight)
					  	return;*/
					  with($('inputs'))
					  {
					        style.position="absolute";
					        if(typeof(window.addEventListener)=="function")
					        {
					        	style.left=pos[0]+2;
					        	style.top=pos[1]+obj.offsetHeight/2-10;
					        }
					        else
					        {
					        	style.posLeft=pos[0]+2;
					        	style.posTop=pos[1]+obj.offsetHeight/2-10;
					        }
					        
				      }  
				
					var tobj   =   document.createElement("input");   
					tobj.id="in_put";
					tobj.type   =   "text";					
					tobj.size=(obj.offsetWidth/10);   
					tobj.name   =   "textbox1";   
					if(userResult['p'+pointID]!='null')
				 	  tobj.value=userResult['p'+pointID];
				 	  if(typeof(window.addEventListener)=="function")
				 	  {
				 	  		tobj.addEventListener("keydown", keyDownFunction(window.event,a0100,pointID),false);
					 		tobj.addEventListener("blur", onblurFunction(tobj,a0100,pointID),false);
				 	  }
				 	  else
				 	  {
				 	  		tobj.attachEvent("onkeydown", keyDownFunction(event,a0100,pointID)); 
					 		tobj.attachEvent("onblur", onblurFunction(tobj,a0100,pointID)); 
				 	  }
					 
					 
				     var a_input=document.getElementById("inputs");
				     a_input.innerHTML="";
					 a_input.appendChild(tobj);
					 
					 var userAgent = navigator.userAgent;
					 if (userAgent.indexOf("Safari") > -1)
					 {
					 	Element.show('inputs');
					 }
				     tobj.focus();
					 tobj.select();
				
				}
			}
}

var onblurFunction= function(tobj,a0100,pointID)
{
	return function()
	{
		setValue2(tobj,a0100,pointID);
	}
}

var keyDownFunction = function(e,a0100,pointID)
{
  return function(e)
  {
	var currKey=0;
	currKey=e.keyCode||e.which||e.charCode;
  	if (currKey==13)  go_right(a0100,pointID);   //Enter
   	if (currKey==37)  go_left(a0100,pointID);    //left arrow
   	if (currKey==39)  go_right(a0100,pointID);   //right arrow
   	if (currKey==38)  go_up(a0100,pointID);      //up arrow
   	if (currKey==40)  go_down(a0100,pointID);    //down arrow
  }
}



function check(flag)//操作类型 1：保存  2：提交  3:保存（排序）  8打分完成
{
	var hashvo=new ParameterSet();
	var fillCtrs=fillCtr.split("/");
	
	var users_arry=users.split("/");
	if(pointDeformity=='1')
	{
		var n_ids=noGradeItem.split(",");
		var desc="";
		for(var i=0;i<n_ids.length;i++)
		{
			if(trim(n_ids[i]).length!=0)
			{
				desc+="\n\r."+per_pointArray[n_ids[i]].pointname;
			}
		}
		alert(desc+"\n\r "+BATCHGRADE_INFO1+"！");
		return;
	}
	if(wholeEvalMode=="1"){
		//for(var k=0;k<objectWholeScores.length;k++)//2013.12.25 pjf
		for(var k=0;k<users_arry.length;k++)
		{
			//var id= 'wholeEvalScoreId_'+k;
			var id= 'wholeEvalScoreId_'+users_arry[k];
			var score = document.getElementById(id).value;
			if(score==null || score==""){
				if(mustFillWholeEval=='True'){
					alert(WHOLESCOREMUSTFILL);
					return;
				}
				else
					//hashvo.setValue('wholeEvalScore_'+k,0);    pjf 2013.12.25
					hashvo.setValue('wholeEvalScore_'+users_arry[k],0);
			}
			else {
				var temp2=fucNumchk(score);   //是否为数字
				if(temp2==0){
					alert("请输入数字！");
					return;
				}
				//var score = objectWholeScores[k];
				score=parseFloat(score);
				topscore=parseFloat(topscore);
				if(score<0 || score>topscore)
				{
					//alert(objectNames[k]+"总体评价的分值不在0~"+topscore+"之间!");    pjf 2013.12.25
					alert(obj_result['_'+users_arry[k]]['name']+"总体评价的分值不在0~"+topscore+"之间!");
					return;
				}
				else
					//hashvo.setValue('wholeEvalScore_'+k,score);   2013.12.25  pjf
					hashvo.setValue('wholeEvalScore_'+users_arry[k],score);
			}
		}
		hashvo.setValue("wholeEvalMode",wholeEvalMode);
	}
	//定义一个info_array数组接收输出信息
	var info_array = new Array(); 
	for(var i=0;i<users_arry.length;i++)
	{	
			var str="";
			var userResult=obj_values['_'+users_arry[i]];
			var temp_arr=points.split("/");
			 
			if(trim(points).length>0)
			{
				for(var j=0;j<temp_arr.length;j++)
				{
					
					if((flag==2||flag==8)&&obj_result['_'+users_arry[i]]['status']!='2'&&temp_arr[j]!='per_degreedesc'&&isEntiretySub=='true'&&(trim(userResult[temp_arr[j]]).length==0||trim(userResult[temp_arr[j]])=='null'))
					{
							var check_obj=eval("document.batchGradeForm.b"+users_arry[i]+"_"+plan_id+"_"+mainbodyId);
							var temp_arry=obj_result["_"+users_arry[i]];
							var values=temp_arry[temp_arr[j]].split("/");
							if(check_obj)
							{
								if(!check_obj.checked)
								{
									if(temp_arr[j]!='per_know'&&temp_arr[j]!='per_degreedesc'&&(values[1]!='0'&&values[2]!=0))///绩效指标为空 且需要打分的指标提示出来
									{
										//alert(" "+BATCHGRADE_INFO2+"！");
										var temppoint_id = temp_arr[j].substring(1,temp_arr[j].length);
										var point_name = per_pointArray[temppoint_id]['pointname'];
										info_array[info_array.length] = {object_name:temp_arry["name"],point_name:point_name};
										/*if(flag==8)
										{
											flag=1;
										}												
										else
											return;*/
									}else if(temp_arr[j]=='per_know'||temp_arr[j]=='per_degreedesc'){//了解程度、总体评价为空
										var point_name="";
										if(temp_arr[j]=='per_know')
										 point_name= "了解程度";
										if(temp_arr[j]=='per_degreedesc')
										 point_name= "总体评价";
										info_array[info_array.length] = {object_name:temp_arry["name"],point_name:point_name};
									}
								}
							}
							else
							{
									if(temp_arr[j]!='per_know'&&temp_arr[j]!='per_degreedesc'&&(values[1]!='0'&&values[2]!=0))///绩效指标为空 且需要打分的指标提示出来
									{
										//alert(" "+BATCHGRADE_INFO2+"！");
										var temppoint_id = temp_arr[j].substring(1,temp_arr[j].length);
										var point_name = per_pointArray[temppoint_id]['pointname'];
										info_array[info_array.length] = {object_name:temp_arry["name"],point_name:point_name};
										/*if(flag==8)
										{
											flag=1;
										}												
										else
											return;*/
									}else if(temp_arr[j]=='per_know'||temp_arr[j]=='per_degreedesc'){//了解程度、总体评价为空
										var point_name="";
										if(temp_arr[j]=='per_know')
										 point_name= "了解程度";
										if(temp_arr[j]=='per_degreedesc')
										 point_name= "总体评价";
										info_array[info_array.length] = {object_name:temp_arry["name"],point_name:point_name};
									}
							}
					}
					
					str+="/"+userResult[temp_arr[j]];
				}
			}
			//alert(users_arry[i]+"  "+str.substring(1));
			if(wholeEvalMode=="1")
				str+="/"+null;
			if(str=="")
				hashvo.setValue(users_arry[i],"");
			else
				hashvo.setValue(users_arry[i],str.substring(1));					
	}
	if(info_array.length>0){
		if(info_array.length<20){
			var info_str="";
			for(var i=0;i<info_array.length;i++){
				if(info_array[i]["point_name"]=="了解程度"||info_array[i]["point_name"]=="总体评价"){
					info_str+=info_array[i]["object_name"]+"  的  "+info_array[i]["point_name"]+"  未填写\r\n";
				}else{
					info_str+=info_array[i]["object_name"]+"  的  "+info_array[i]["point_name"]+"  指标未打分\r\n";
				}

			}
			info_str+="请核对!";
			alert(info_str);
		} else {
			alert(" "+BATCHGRADE_INFO2+"！");
		}
		if(flag==8)
		{
			flag=1;
		}	
		else
			return;
	}
	hashvo.setValue("users",users);
	hashvo.setValue("plan_id",plan_id);
	hashvo.setValue("template_id",template_id);
	hashvo.setValue("status",status);
	hashvo.setValue("scoreflag",scoreflag);
	hashvo.setValue("linkType",linkType);
	var In_paramters="flag="+flag; 		
	
	if(flag==2)
	{
		if(!confirm(BATCHGRADE_INFO3+"!"))//本次考评是公正严谨的，请您确认后再提交，提交后不能更改
			return;
	}
	
	
	if((flag==2||flag==1||flag==8)&&score_sumtotal=='true'&&scoreflag=='2')
	{
			 writeSumScore();
	}
	
	if(totalRowValue!=0){ 
			if(score_sumtotal=='true'&&scoreflag=='2'){
				if(flag==8)
				{
					if(!validateTotalRowValue(totalRowValue))
						return;
				}
			}
	}
	
	if(flag==2)
	{
		var waitInfo=eval("wait");	
		waitInfo.style.display="block";
	}
	buttonDisabled(true); 	
	var request=new Request({method:'post',asynchronous:true,parameters:In_paramters,onFailure:errorFunc,onSuccess:returnInfo,functionId:'90100150003'},hashvo);

}	


 function errorFunc(outparamters)
 {
 	buttonDisabled(false); 
 }


function buttonDisabled(str)
{
	if(document.getElementsByName("b_save")&&document.getElementsByName("b_save")[0])
		document.getElementsByName("b_save")[0].disabled=str;
	if(document.getElementsByName("b_order")&&document.getElementsByName("b_order")[0])
		document.getElementsByName("b_order")[0].disabled=str;
	if(document.getElementsByName("b_sub")&&document.getElementsByName("b_sub")[0])
		document.getElementsByName("b_sub")[0].disabled=str
	if(document.getElementsByName("b_finished")&&document.getElementsByName("b_finished")[0])
		document.getElementsByName("b_finished")[0].disabled=str
	if(document.getElementsByName("b_suball")&&document.getElementsByName("b_suball")[0])
		document.getElementsByName("b_suball")[0].disabled=str	 
}
		
		
function writeSumScore()	
{
   for(var j=0;j<point_arr.length;j++)
   {
       if(point_arr[j]!='per_know'&&point_arr[j]!='per_degreedesc'){
       
   			var sum=0;
           for(var i=0;i<user_arr.length;i++){
              if(obj_values["_"+user_arr[i]][point_arr[j]].split("/")[0]!='null') 
               {
                	if(checkIsNum(obj_values["_"+user_arr[i]][point_arr[j]].split("/")[0]))
	                 	sum+=obj_values["_"+user_arr[i]][point_arr[j]].split("/")[0]*1; 
            	
            	}
            }   
            if(sum!=0)
              document.getElementById('sum_'+point_arr[j]).innerHTML=cheng(sum,2);
            else
              document.getElementById('sum_'+point_arr[j]).innerHTML="";  
       } 
   }
}
	
	
function validateTotalRowValue(totalRowValue)
{
	
	for(var j=0;j<point_arr.length;j++)
	{
		if(point_arr[j]!='per_know'&&point_arr[j]!='per_degreedesc'){
				       
				              if(checkIsNum(trim(document.getElementById('sum_'+point_arr[j]).innerHTML)))
				              {
				              	if(trim(document.getElementById('sum_'+point_arr[j]).innerHTML)*1!=totalRowValue*1)
				              	{
				              		alert("合计行的值不为"+totalRowValue+"分,不能执行完成操作！");
				              		return false;
				              	}
				              }
		 }
	}

	return true;
}

// 一键评分
function ghostScore(planid)
{	
	var retvo;
	var thecodeurl = "/selfservice/performance/batchGrade.do?b_ghostScore=link&plan_id="+planid; 
	//兼容非IE浏览器 弹窗替换用 open弹窗  wangb 20171205
    var iTop = (window.screen.availHeight - 30 - 370) / 2; //获得窗口的垂直位置
    var iLeft = (window.screen.availWidth - 10 - 800) / 2; //获得窗口的水平位置
	window.open(thecodeurl,"","resizable=no,scrollbars=yes,width=800px,height=370px,toolbar=no,status=no,location=no,left="+iLeft+",top="+iTop);
	return;
	/*
	if(typeof(window.addEventListener)=="function")
	{
		//如果是火狐
		var thecodeurl = "/selfservice/performance/batchGrade.do?b_ghostScore=link&plan_id="+planid; 
		if (window.showModalDialog) 
		{
			retvo = window.showModalDialog(thecodeurl, "", 
        	"dialogWidth:450px; dialogHeight:370px;resizable:no;center:no;scroll:no;status:no");
		}
		else
		{
			//retvo = window.showModalDialog(thecodeurl, "", 
        //	"dialogWidth:450px; dialogHeight:370px;resizable:no;center:no;scroll:no;status:no");
		}
		
	}
	else
	{
		var thecodeurl = "/selfservice/performance/batchGrade.do?b_ghostScore=link`plan_id="+planid; 
		var iframe_url = "/general/query/common/iframe_query.jsp?src="+thecodeurl;
		retvo = window.showModalDialog(iframe_url, "",
					"dialogWidth=800px; dialogHeight=370px;toolbar=no;menubar=no;scroll=yes;resizable=no; location=no;status=no");
	}
	if(retvo==null)
	    return false;	   
	 
	if(retvo.flag=="true")
	{   		
		saveGhostScore('1',retvo.scoreObject,retvo.grade_id);		
	}
	*/										
}
/*open弹窗调用方法  wangb 20171206*/
function openFlag(retvo){
	if(retvo.flag=="true")
	{   		
		saveGhostScore('1',retvo.scoreObject,retvo.grade_id);		
	}
}

function saveGhostScore(flag,scoreObject,grade_id)
{
	var hashvo=new ParameterSet();
	var fillCtrs=fillCtr.split("/");
	var users_arry=scoreObject.split("/");
	if(pointDeformity=='1')
	{
		var n_ids=noGradeItem.split(",");
		var desc="";
		for(var i=0;i<n_ids.length;i++)
		{
			if(trim(n_ids[i]).length!=0)
			{
				desc+="\n\r."+per_pointArray[n_ids[i]].pointname;
			}
		}
		alert(desc+"\n\r "+BATCHGRADE_INFO1+"！");
		return;
	}
	 
	for(var i=0;i<users_arry.length;i++)
	{	
		var str="";
		var userResult=obj_values['_'+users_arry[i]];			
		var temp_arr=points.split("/");
			 
		if(trim(points).length>0)
		{
			for(var j=0;j<temp_arr.length;j++)
			{	
				if(typeof(point_grade)=="undefined")
				{
					alert(P_FRESHDATA+"!");
					return;
				}
				var tempArray = point_grade[temp_arr[j]];
				if(temp_arr[j]=='per_know' || temp_arr[j]=='per_degreedesc' || (tempArray!=null && tempArray[0].pointkind=="1"))	// 定量指标												
					str+="/"+userResult[temp_arr[j]];
				else
				{
					// ???????????¨????±ê???????ü??·??? bug
				//	if(userResult[temp_arr[j]]!=null && userResult[temp_arr[j]].length>0 && userResult[temp_arr[j]]!='null')
						str+="/"+grade_id;
				//	else
				//		str+="/"+userResult[temp_arr[j]];
				}
			}
		}
		if(wholeEvalMode=="1")
			str+="/"+null;
		// alert(users_arry[i]+"  "+str.substring(1));
		if(str=="")
			hashvo.setValue(users_arry[i],"");
		else
			hashvo.setValue(users_arry[i],str.substring(1));					
	}
/*	
	for(var i=0;i<users_arry.length;i++)
	{	
		var str="";
		var userResult=obj_values['_'+users_arry[i]];			
		var temp_arr=points.split("/");
			 
		if(trim(points).length>0)
		{
			for(var j=0;j<temp_arr.length;j++)
			{					
				if((flag==2||flag==8)&&obj_result['_'+users_arry[i]]['status']!='2'&&temp_arr[j]!='per_degreedesc'&&isEntiretySub=='true'&&(trim(userResult[temp_arr[j]]).length==0||trim(userResult[temp_arr[j]])=='null'))
				{
					var check_obj=eval("document.batchGradeForm.b"+users_arry[i]+"_"+plan_id+"_"+mainbodyId);
					var temp_arry=obj_result["_"+users_arry[i]];
					var values=temp_arry[temp_arr[j]].split("/");
					if(check_obj)
					{
						if(!check_obj.checked)
						{
							if(temp_arr[j]=='per_know'||temp_arr[j]=='per_degreedesc'||(values[1]!='0'&&values[2]!=0))
							{
								alert(" "+BATCHGRADE_INFO2+"！");
								if(flag==8)
								{
									flag=1;
								}												
								else
									return;
							}
						}
					}
					else
					{
						if(temp_arr[j]=='per_know'||temp_arr[j]=='per_degreedesc'||(values[1]!='0'&&values[2]!=0))
						{																		
							alert(" "+BATCHGRADE_INFO2+"！");
							if(flag==8)
							{
								flag=1;
							}	
							else
								return;
						}
					}
				}
				var tempArray = point_grade[temp_arr[j]];
				if(temp_arr[j]=='per_know' || temp_arr[j]=='per_degreedesc' || (tempArray!=null && tempArray[0].pointkind=="1"))	// 定量指标												
					str+="/"+userResult[temp_arr[j]];
				else
				{
					if(userResult[temp_arr[j]]!=null && userResult[temp_arr[j]].length>0 && userResult[temp_arr[j]]!='null')
						str+="/"+grade_id;
					else
						str+="/"+userResult[temp_arr[j]];
				}					
			//	str+="/"+userResult[temp_arr[j]];
			}
		}
		//alert(users_arry[i]+"  "+str.substring(1));
		if(str=="")
			hashvo.setValue(users_arry[i],"");
		else
			hashvo.setValue(users_arry[i],str.substring(1));					
	}
*/	
	hashvo.setValue("users",scoreObject);
	hashvo.setValue("plan_id",plan_id);
	hashvo.setValue("template_id",template_id);
	hashvo.setValue("status",status);
	hashvo.setValue("scoreflag",scoreflag);
	hashvo.setValue("linkType",linkType);
	var In_paramters="flag="+flag; 		
	var request=new Request({method:'post',asynchronous:true,parameters:In_paramters,onFailure:errorFunc,onSuccess:ghostReturnInfo,functionId:'90100150003'},hashvo);
	
}

function ghostReturnInfo(outparamters)
{		
	var info=getDecodeStr(outparamters.getValue("info"));
	info=replaceAll(info,'<br>','\n\r');
	
	if(info=='保存成功!')
	{
		alert(info);
		
		batchGradeForm.action="/selfservice/performance/batchGrade.do?b_Desc=query";
		batchGradeForm.submit();	
	}
	else 
	{
		alert(info);
	}
		
}

// 多人考评计算   
function compuScore(plan_id)
{
	var hashvo=new ParameterSet(); 
  	hashvo.setValue("plan_id",plan_id);
  //hashvo.setValue("object_id","${scoreAjustForm.object_id}"); 
  	var request=new Request({asynchronous:false,onSuccess:comput_ok,functionId:'90100150024'},hashvo); 
}

