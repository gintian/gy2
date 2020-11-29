//计划说明
	function planDescript(planid)
	{
        var thecodeurl="/performance/objectiveManage/myObjective/my_objective_list.do?b_reject=link`opt=2`plan_id="+planid;
        var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
        var config = {
            width:585,
            height:410,
            type:'2'
        }
        modalDialog.showModalDialogs(iframe_url,'planDescriptwin',config);
		
	}

//赋分原因
function scoreReason(plan_id,object_id,userID,point_id,opt)
{
	window.point_id = point_id;
	var strurl="/selfservice/performance/singleGrade.do?b_initScoreCause=query`type=0`plan_id="+plan_id+"`opt="+opt+"`objectid="+object_id+"`userID="+userID+"`point_id="+point_id;
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
	//var reject_cause=window.showModalDialog(iframe_url,arguments,"dialogWidth=460px;dialogHeight=380px;resizable=yes;scroll=no;status=no;");  
	var config = {
			      	width:460,
			      	height:380,
			      	title:'评分说明',
			      	type:'1',
			      	id:'scoreReasonWin'
		    	}
	modalDialog.showModalDialogs(iframe_url,'template_win',config,scoreReason_ok);
}
function scoreReason_ok(reject_cause){
	if(reject_cause!=null && reject_cause!='undefined') // 评分说明可以制为空 并且也刷新 JinChunhai 2011.12.08
	{
		var all_r=reject_cause;
		all_r=replaceAll(all_r,"<br>","\r\n");
//		all_r=replaceAll(all_r," ","&nbsp;");
			
		if(reject_cause!=null && reject_cause.length>40)
			reject_cause=reject_cause.substring(0,40)+"......";
		reject_cause=replaceAll(reject_cause,"<br>","\r\n");
		reject_cause=replaceAll(reject_cause," ","&nbsp;");
		document.getElementById('r_'+window.point_id).innerHTML=reject_cause;
		document.getElementById('r_'+window.point_id).title=all_r;
		//清除全局变量
		window.point_id=undefined;
	}}
function executeGradeHtml()
{
	var titlename;
	for(var i=0;i<document.singleGradeForm.dbpre.options.length;i++)
	{
		if(document.singleGradeForm.dbpre.options[i].selected==true)
			titlename=document.singleGradeForm.dbpre.options[i].text;
	}
	var tabHeight = document.documentElement.clientHeight||document.body.clientHeight;
	tabHeight = tabHeight-50;
	var a_object=eval("singleGradeForm.titleName");	
	a_object.value=titlename;
	document.singleGradeForm.action="/selfservice/performance/selfGrade.do?b_query2=b_query2&dbpre="+document.singleGradeForm.dbpre.value+"&operate=selfgrade&tabsetHeight="+tabHeight;
	document.singleGradeForm.submit();
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


function showWindow(plan_id,object_id,mainbody_id)
{
	var theurl = "/performance/markStatus/markStatusList.do?b_edit3=edit`planID="+plan_id+"`objectID="+object_id+"`mainbodyID="+mainbody_id;
   	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
//	var win=window.open(",null,"width=530,height=450,top=50,left=50,resizable=no,status=no,toolbar=no,scrollbars=no,menubar=no,location=no");
	//var win=open("/performance/markStatus/markStatusList.do?b_edit3=edit&planID="+plan_id+"&objectID="+object_id+"&mainbodyID="+mainbody_id,"info");
    var config = {
	      	width:530,
	      	height:450,
			type:'1',
			title:'总体评价',
			id:'showWindowWin'
    	}
   modalDialog.showModalDialogs(iframe_url,null,config);
}


function indiviPerformance(plan_id,object_id,mainbody_id)
{
	singleGradeForm.action="/selfservice/performance/singleGrade.do?b_individual=search&operate=1&plan_id="+plan_id+"&object_id="+object_id+"&mainbody_id="+mainbody_id;
	singleGradeForm.submit();

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
			var select_arr=eval("singleGradeForm.a"+objectID);
		//	if(performanceType=='0')  //如果为绩效考评
			var description = outparamters.getValue("description");
			var reasons = outparamters.getValue("reasons");
			if((description=="" || description==null) && reasons=="123"){
				alert("请先填写不评价原因！");
				window.location.href=window.location.href;
				return;
			}
			{ 
				if(isNoMark==4)
				{ 
					if(PointEvalType=='1'&&scoreflag==1)
					{
						var points=pointIDs.split("/");  
						for(var i=0;i<points.length;i++)
						{
							var point_id=points[i];
							if(trim(point_id)=='')
								continue; 
							var obj=document.getElementsByName(point_id); 
							if(obj[0].type=="text")
							{
								obj[0].value='';
								obj[0].disabled=true;
							}
							else
							{
								for(var j=0;j<obj.length;j++)
								{
									 obj[j].checked=false; 
									 obj[j].disabled=true;
								}  
							}
							
						}
					
					}
					else
					{
				
						if(select_arr&&select_arr.length)
						{ 
							for(var i=0;i<select_arr.length;i++)
							{
						
								if(select_arr[i].type=='select-one')
								{
									select_arr[i].options[0].selected=true;
								}
								if(select_arr[i].type=='text')
								{
									select_arr[i].value='';
								}
								
								select_arr[i].disabled=true;
							}
						}
						else if(select_arr)  //20141205 dengcan
						{
							if(select_arr.type=='select-one')
							{
								select_arr.options[0].selected=true;
							}
							if(select_arr.type=='text')
							{
								select_arr.value='';
							}
							select_arr.disabled=true;
						}
						// 清空对应的评分描述 by 刘蒙
						var points=pointIDs.substring(1).split("/");
						for(var i=0;i<points.length;i++) {
							var point_id=points[i];
							var td = document.getElementById("r_" + point_id);
							if (td) {
								td.innerHTML = "";
							}
						}
					}
						if(document.singleGradeForm.konwDegree)
						{
							document.singleGradeForm.konwDegree.options[0].selected=true;
							document.singleGradeForm.konwDegree.disabled=true;
							
						}
						if(document.singleGradeForm.wholeEval)
						{
							document.singleGradeForm.wholeEval.options[0].selected=true;
							document.singleGradeForm.wholeEval.disabled=true;
						}
			/*			if(document.getElementById("buttons"))  20141205 dengcan 不评价无需隐藏保存、提交按钮
						{
						
							document.getElementById("buttons").style.display='none';
						} */
						var whole_scoreId = document.getElementById("wholeEvalScoreId");
						if(whole_scoreId!=null){
							whole_scoreId.value='0.0';
							whole_scoreId.disabled=true;
						}	
						var totalScore = document.getElementById("ascore");
						if(totalScore!=null){
							totalScore.innerHTML="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;0.0";
							totalScore.style.color='blue';
						}
				}
				else 
				{
				
					if(PointEvalType=='1'&&scoreflag==1)
					{
						var points=pointIDs.split("/"); 
						var index=0;
						for(var i=0;i<points.length;i++)
						{
							var point_id=points[i];
							if(trim(point_id)=='')
								continue; 
							
							var haspriv="0";
							if(index<pointContrls.length)
							{
								if(pointContrls[index]=='1')
									haspriv="1";
							}
							else
								haspriv="1";
							
							if(haspriv=='1')
							{
								var obj=document.getElementsByName(point_id); 
								if(obj[0].type=="text")
								{ 
									obj[0].disabled=false;
								}
								else
								{
									for(var j=0;j<obj.length;j++)
									{ 
										 obj[j].disabled=false;
									}  
								}
							}
							index++;
						}
					
					}
					else
					{ 
						if(select_arr&&select_arr.length)
						{
							for(var i=0;i<select_arr.length;i++)
							{
								if(i<pointContrls.length)
								{
									if(pointContrls[i]=='1')
										select_arr[i].disabled=false;
								}
								else
									select_arr[i].disabled=false;
							}
						}
						else if(select_arr)
						{
							if(pointContrls=='1')
								select_arr.disabled=false;
						}
					}
					if(document.singleGradeForm.konwDegree)
					{
							document.singleGradeForm.konwDegree.disabled=false;
							
					}
					if(document.singleGradeForm.wholeEval)
					{
							document.singleGradeForm.wholeEval.disabled=false;
					}
					if(document.getElementById("buttons"))
					{
						
							document.getElementById("buttons").style.display='block';
					}
					var whole_scoreId = document.getElementById("wholeEvalScoreId");
					if(whole_scoreId!=null){
						whole_scoreId.disabled=false;
					}
				}
			}
	
	}
	

	function windowOpen(url)
	{
		var new_obj=window.open(url,'_blank','height=460, width=590')
	
	}
	
	function go_up(ite){
		var flag=false;
		for(var i=0;i<document.singleGradeForm.elements.length;i++)
		{		
			if(document.singleGradeForm.elements[i+1]==ite)
			{
				flag=true;
				
			}	
			if(flag)
			{
				for(var j=i;j>=2;j--)
				{
					if(document.singleGradeForm.elements[j].type=='text'||document.singleGradeForm.elements[j].type=='select-one')
					{
						document.singleGradeForm.elements[i].focus();
						flag=false;
						break;
					}
				}
				break;
			}
		}
	  }
	  
	  
	function go_down(ite){
		var flag=false;
		for(var i=0;i<document.singleGradeForm.elements.length;i++)
		{			
			
			if(flag&&(document.singleGradeForm.elements[i].type=='text'||document.singleGradeForm.elements[i].type=='select-one'))
			{			
				document.singleGradeForm.elements[i].focus();
				flag=false;
				break;
			}
			if(document.singleGradeForm.elements[i]==ite)
			{
				flag=true;
				
			}
		}
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
	        
	        style.left=(window.screen.availWidth/2-350)+"px";
	        style.top=(pos[1]-15+srcobj.offsetHeight)+"px";  
      }  
      var In_paramters="point_id="+srcobj.id.substring(1); 		
	 
	  var dataHtml="";
	  dataHtml+="<table width='500' border='0' cellspacing='0' bgColor='#FFFFFF'  align='center' cellpadding='1' class='ListTable'   > ";
	  dataHtml+="<thead><tr> <td  width='60'  align='center' class='TableRow' nowrap >"+P_STANDPOINT+"</td>";
	  dataHtml+="<td width='260' align='center' class='TableRow' nowrap > &nbsp;&nbsp;&nbsp;"+P_POINTDESC+"&nbsp;&nbsp;&nbsp;</td>";			
	  dataHtml+="<td width='60' align='center' class='TableRow' nowrap > &nbsp;&nbsp;&nbsp;"+PERCENT+"&nbsp;&nbsp;&nbsp;</td>";			
	  dataHtml+="<td width='60' align='center' class='TableRow' nowrap > &nbsp;&nbsp;"+P_UVALUE+"&nbsp;&nbsp;</td>";
	  dataHtml+="<td width='60' align='center' class='TableRow' nowrap >&nbsp;&nbsp;"+P_BVALUE+"&nbsp;&nbsp;</td> </tr>  </thead>";
	  if(typeof(point_grade)!="undefined")
	  	 var gradeList=point_grade['p'+srcobj.id.substring(1)];
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
			
			if(temp.subsys_id!="undefined" && temp.subsys_id=='35')			
				dataHtml+="><td align='left' class='RecordRow'  nowrap >"+per_competencedegree[temp.gradecode].gradedesc+"</td>";
			else
				dataHtml+="><td align='left' class='RecordRow'  nowrap >"+per_standdegree[temp.gradecode].gradedesc+"</td>";
			dataHtml+="<td align='left'  class='RecordRow' >"+temp.gradedesc+"</td>";
			dataHtml+="<td  align='left' class='RecordRow' nowrap >"+temp.gradevalue+"</td>";
			dataHtml+="<td  align='left' class='RecordRow' nowrap >"+temp.top_value+"</td>";
			dataHtml+="<td  align='left' class='RecordRow' nowrap >"+temp.bottom_value+"</td></tr>";
	  
	  }
	  dataHtml+="</table>";	
	 
	 
	  dataHtml=replaceAll(dataHtml,"#@#","<br>");	
	  date_panel.innerHTML=dataHtml;
	  date_panel.innerHTML=date_panel.innerHTML+"<iframe src=\"javascript:false\" style=\"position:absolute; visibility:inherit; top:0px; left:0px;"
   			 				+"width:"+date_panel.offsetWidth+"; height:"+date_panel.offsetHeight+"; " 					    	
   			 				+"z-index:-1;filter='progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)';\"></iframe>";   				
	  var pos=getAbsPosition(srcobj);
	  var pos0=pos[0];
	  var pos1=pos[1];
	  var srcobj_width=srcobj.offsetWidth;
	  var srcobj_height=srcobj.offsetHeight;
      var op=eval('date_panel');	
       with($('date_panel'))
		  {
		        style.position="absolute";
		        style.left=15+"px";	
			    if(window.document.body.offsetHeight<(window.event.y+op.offsetHeight))
			    {
			    	style.top=(window.event.y-20+document.body.scrollTop-op.offsetHeight)+"px";			    	
			    }
			    else
			    {
			    	style.top=(window.event.y+20+document.body.scrollTop)+"px";
			    }
			 
	      }  
   }
   
   
   
   
   
   
   var evt = null;
   function showDateSelectBox(srcobj)
   {
	   evt = srcobj;
      var pos=getAbsPosition(srcobj);

      var hashvo=new ParameterSet();
	  hashvo.setValue("pos0",pos[0]);
	  hashvo.setValue("pos1",pos[1]);
      hashvo.setValue("srcobj_width",srcobj.offsetWidth);
      hashvo.setValue("srcobj_height",srcobj.offsetHeight);
      
      
      var In_paramters="point_id="+srcobj.id.substring(1); 		
	  var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo2,functionId:'90100160009'},hashvo);
   }
   
   
   
   
	function returnInfo2(outparamters)
	{
	    Element.show('date_panel');   
		var fieldlist=outparamters.getValue("dataList");	
		var dataHtml=getDecodeStr(outparamters.getValue("dataHtml"));	
		dataHtml=replaceAll(dataHtml,"#@#","<br>");		
		var op=eval('date_panel');
		date_panel.innerHTML=dataHtml;
		date_panel.innerHTML=date_panel.innerHTML;
		/* 弹出层暂时不受影响，注释掉iframe by lium 2014-12-03
		date_panel.innerHTML=date_panel.innerHTML+"<iframe src=\"javascript:false\" style=\"position:absolute; visibility:inherit; top:0px; left:0px;"
   			 				+"width:"+op.offsetWidth+"; height:"+op.offsetHeight+"; " 					    	
   			 				+"z-index:-1;filter='progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)';\"></iframe>";   				
	 	*/
		
	      var pos0=outparamters.getValue("pos0")*1;
	      var pos1=outparamters.getValue("pos1")*1;
		  var srcobj_width=outparamters.getValue("srcobj_width")*1;
		  var srcobj_height=outparamters.getValue("srcobj_height")*1;
          var op=eval('date_panel');
		  with($('date_panel'))
		  {
		        style.position="absolute";
		        style.left="15px";
		        
		        // 弹出层定位逻辑修改 by lium 2014-12-02
		        var b = window.document.body;
		        var e = evt || window.event;//兼容火狐浏览器
		        
		        var x = e.x?e.x:e.offsetLeft;
		        var y = e.y?e.y:e.offsetTop;
		        style.left = (x + 70)+"px";
		        if (b.offsetHeight < op.clientHeight) {
		        	// 弹出层实际高度(非可见高度)高于body高度，则将其定位到顶部10px处，下方超出部分无视
		        	style.top = 10+"px";
		        } else {
		        	// 弹出层的左上角离鼠标最好不要太远
		        	if ((y + op.clientHeight) > b.offsetHeight) {
		        		// 鼠标所在页面高度 + 弹出层实际高度 > body高度，将弹出层下边缘与body底部平齐
		        		style.top = (b.offsetHeight - op.clientHeight - 10)+"px";
		        	} else {
		        		// 将弹出层放置在鼠标右侧附近
		        		style.top = (y - 20)+"px";
		        	}
		        }
		        /*
	 		    if(window.document.body.offsetHeight<(window.event.y+op.offsetHeight))
			    {
			    	style.posTop=window.event.y-20+document.body.scrollTop-op.offsetHeight;			    	
			    }
			    else 
			    {
			    	style.posTop=window.event.y+20+document.body.scrollTop;
			    }
			    */
			    		  
	      }  
	}
	
	function del(opt)
	{
		if(document.singleGradeForm.summary.value.length==0)
			document.singleGradeForm.summary.value=" ";
		if(document.singleGradeForm.goalContext&&document.singleGradeForm.goalContext.value.length==0)
			document.singleGradeForm.goalContext.value=" ";
		document.singleGradeForm.action="/selfservice/performance/selfGrade.do?b_extrafile2=save&opt="+opt;
		document.singleGradeForm.submit();
	}
	
	function del(opt,article_id,plan_id)
	{
	    if(!confirm("请确认执行删除操作?"))
		{
			return;
		}
		if(document.singleGradeForm.summary.value.length==0)
			document.singleGradeForm.summary.value=" ";
		if(document.singleGradeForm.goalContext&&document.singleGradeForm.goalContext.value.length==0)
			document.singleGradeForm.goalContext.value=" ";
		document.singleGradeForm.action="/selfservice/performance/selfGrade.do?b_extrafile2=save&plan_id="+plan_id+"&article_id="+article_id+"&opt="+opt;
		document.singleGradeForm.submit();
	}
	
	
	
	
	
   function upload(opt)
	{
		if(opt==7||opt==8)
		{
			if(!confirm("提交后将不能再编辑,请确认提交?"))
			{
				return;
			}
		}	
		
		if(opt==1||opt==4)
	     {
	     	if(!validateSize(opt))
	     		return;
	     }
		
		if((opt==1&&trim(document.singleGradeForm.fileName.value).length==0)||(opt==4&&trim(document.singleGradeForm.goalfileName.value).length==0))
		{
			
			if(opt==4)
			{	
				var temp_url=document.singleGradeForm.goalfile.value;
				document.singleGradeForm.goalfileName.value=temp_url.substring(temp_url.lastIndexOf ("\\")+1);
				
			}
			else
			{
				var temp_url=document.singleGradeForm.file.value;
			   document.singleGradeForm.fileName.value=temp_url.substring(temp_url.lastIndexOf ("\\")+1);
			   
			} 
		}
		if(document.singleGradeForm.summary.value.length==0)
			document.singleGradeForm.summary.value=" ";
		if(document.singleGradeForm.goalContext&&document.singleGradeForm.goalContext.value.length==0)
			document.singleGradeForm.goalContext.value=" ";
		if(opt==1)
			document.singleGradeForm.action="/selfservice/performance/selfGrade.do?b_extrafile2=save&opt="+opt;
		if(opt==4||opt==5||opt==8)
		{
			if(opt==8)
			{
				if(goalFileSize==0&&document.singleGradeForm.goalContext&&document.singleGradeForm.goalContext.value.length==0)
				{
					alert("绩效目标没有填写完整!");
					return;
				}
			}
			document.singleGradeForm.action="/selfservice/performance/selfGrade.do?b_extrafile2=save&opt="+opt;
		}
		else if(opt==2||opt==7)
		{
			if(opt==7)
			{
				if(summaryFileSize==0&&document.singleGradeForm.summary.value.length==0)
				{
					alert("绩效报告没有填写完整!");
					return;
				}
			}
			document.singleGradeForm.action="/selfservice/performance/selfGrade.do?b_saveSummary=save&opt="+opt;
		}
		document.singleGradeForm.submit();
	}
   
   function saveSummary()
   {
   		document.singleGradeForm.action="/selfservice/performance/selfGrade.do?b_saveSummary=save";	
		document.singleGradeForm.submit();
   }
   
   function executeGradeHtml2()
   {
		var titlename;
		if(document.singleGradeForm.dbpre.options.length<2)
			return;		
		document.singleGradeForm.dbpre.options[1].selected=true;
		titlename=document.singleGradeForm.dbpre.options[1].text;
		var tabHeight = document.documentElement.clientHeight||document.body.clientHeight;
	    tabHeight = tabHeight-50;
		var a_object=eval("singleGradeForm.titleName");
		a_object.value=titlename;
		document.singleGradeForm.action="/selfservice/performance/selfGrade.do?b_query2=b_query2&operate=selfgrade&tabsetHeight="+tabHeight;
		document.singleGradeForm.submit();
	}
	
	// smk 2015.12.01 能力素质查看标度、指标解释
	function showDateSelectBox4(srcobj)
   {
      var twid = srcobj.parentNode.parentNode.parentNode.width;
      var pos=getAbsPosition(srcobj);
      var hashvo=new ParameterSet();
      hashvo.setValue("twid",twid);
	  hashvo.setValue("pos0",pos[0]);
	  hashvo.setValue("pos1",pos[1]);
	  hashvo.setValue("pos2",document.body.clientWidth);
      hashvo.setValue("srcobj_width",srcobj.offsetWidth);
      hashvo.setValue("srcobj_height",srcobj.offsetHeight);
      //alert("网页可见区域宽="+document.body.clientWidth);
      //alert("屏幕分辨率的宽="+window.screen.width);
      var In_paramters="point_id="+srcobj.id.substring(1); 		
	  var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo4,functionId:'90100160023'},hashvo);
   }
   
   
   
   
	function returnInfo4(outparamters)
	{
		Element.show('nlsz_panel');   
	    var pos0=outparamters.getValue("pos0")*1;
	    var pos1=outparamters.getValue("pos1")*1;
	    var twid=outparamters.getValue("twid")*1;
		var srcobj_width=outparamters.getValue("srcobj_width")*1;
		var srcobj_height=outparamters.getValue("srcobj_height")*1;
        var op=eval('nlsz_panel');
		var fieldlist=outparamters.getValue("dataList");	
		var dataHtml=getDecodeStr(outparamters.getValue("dataHtml"));	
		dataHtml=replaceAll(dataHtml,"#@#","<br>");		
		nlsz_panel.innerHTML=dataHtml;
	    
		with($('nlsz_panel')){
		    style.position="absolute";
		    // 弹出层定位逻辑修改 by lium 2014-12-02
		    var b = window.document.body;
		    //style.posLeft = e.x + 30;
		    style.left =parseInt(twid,10)+85+"px";
		   if (b.offsetHeight < op.clientHeight) {
		        	// 弹出层实际高度(非可见高度)高于body高度，则将其定位到顶部10px处，下方超出部分无视
		        	style.top = 10+"px";
		        } else {
		        	// 弹出层的左上角离鼠标最好不要太远
		        	if ((pos1 + op.clientHeight) > b.offsetHeight) {
		        		// 鼠标所在页面高度 + 弹出层实际高度 > body高度，将弹出层下边缘与body底部平齐
		        		style.top = (b.offsetHeight - op.clientHeight - 50)+"px";
		        	} else {
		        		// 将弹出层放置在鼠标右侧附近
		        		style.top = (pos1 - 20)+"px";
		        	}
		        }
	    }  
	}
   function hidden_nlsz(){
   		Element.hide('nlsz_panel');
   }
   