
function setStatus(obj)
{
	var temp=obj.name.split("_");	
	var obj1=eval("b"+temp[1]);
	var hashvo=new ParameterSet();			
	hashvo.setValue("planID",plan_id);	
	hashvo.setValue("objectID",temp[1]);

	hashvo.setValue("operater","2");
	hashvo.setValue("description","");	
	if(obj.checked)
	{
	
		obj1.style.display="block";
		hashvo.setValue("isNoMark","4");
	}
	else
	{
		obj1.style.display="none";
		hashvo.setValue("isNoMark","1");
	}
	var In_paramters="flag=flag"; 
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnNoMark,functionId:'90100170004'},hashvo);
}
function returnNoMark(outparamters)
{
	var objectID=outparamters.getValue("objectID");  
	var isNoMark=outparamters.getValue("isNoMark"); 
	for(var i=0;i<document.batchGrade_SinglePoint_Form.elements.length;i++)
	{
		var obj=document.batchGrade_SinglePoint_Form.elements[i];
		if(obj.type=='radio'&&obj.name.indexOf(objectID+"~")!=-1)
		{
				 if(isNoMark=='4')
				 {
				 	obj.checked=false;//不作评价时没有默认选中值
				 	obj.disabled=true;
				 }
				 else
				 {
					 obj.disabled=false;
				 }
		
		}
	}

}
function save_next(opt,src_obj)
{
	var obj_values="";
	for(var i=0;i<document.batchGrade_SinglePoint_Form.elements.length;i++)
	{
		if(document.batchGrade_SinglePoint_Form.elements[i].type=='radio'&&document.batchGrade_SinglePoint_Form.elements[i].checked)
		{
		
			   var objectid=document.batchGrade_SinglePoint_Form.elements[i].name.split('~')[0];
			   var noScore_obj=document.getElementsByName("noscore_"+objectid);
			    
		 	   if(noScore_obj!=null && noScore_obj.length!=0 && noScore_obj&&noScore_obj[0].checked)
		 	   {
		 	
		 	   }
		 	   else 
				  obj_values+="/"+document.batchGrade_SinglePoint_Form.elements[i].name+"~"+document.batchGrade_SinglePoint_Form.elements[i].value;
		
		}
	}
	var temps=objects_str.split("/");
	for(var i=0;i<temps.length;i++)
	{
		if(temps[i].length>0)
		{
			var _temp=temps[i].split("~");
			
			var noScore_obj=document.getElementsByName("noscore_"+_temp[0]);
			 
		 	if(noScore_obj!=null && noScore_obj.length!=0 && noScore_obj&&noScore_obj[0].checked)
		 	{
		 	
		 	}
		 	else
		 	{
				if(obj_values.indexOf("/"+_temp[0])==-1)
				{
					alert(_temp[1]+"没有被评分!");
					return;
				}
			}
		}
	 
	}
	if(opt==2)
	{	
		if(!confirm("提交后将不能再评分，您确认执行提交操作吗？ "))
			return;
	}
	
//	src_obj.disabled=true;
	var hashvo=new ParameterSet();
	hashvo.setValue("obj_values",obj_values);
	hashvo.setValue("opt",opt);
	hashvo.setValue("totalNumber",totalNumber);
	hashvo.setValue("point_index",point_index);
	hashvo.setValue("point_id",point_id);
	hashvo.setValue("plan_id",plan_id);
	var request=new Request({method:'post',asynchronous:false,onSuccess:returnInfo,functionId:'90100150012'},hashvo);
}

function windowOpen(url)
{
	var new_obj=window.open(url,'_blank','height=360, width=600')
	
}

function returnInfo(outparamters)
{
	var opt=outparamters.getValue("opt");  //1：保存下一题 2：提交 3保存
	var point_index=outparamters.getValue("point_index");
	if(opt=='1')
	{
		document.batchGrade_SinglePoint_Form.point_index.value=point_index;	
		document.batchGrade_SinglePoint_Form.action="/performance/batchGradeSinglePoint.do?b_Desc=query&random="+parseInt(100*Math.random());
		document.batchGrade_SinglePoint_Form.submit();
	}
	else if(opt=='3')
	{
		alert("保存成功!");
		document.getElementById("save").disabled=false;
	}
	else
	{	
		
		window.parent.document.forms[0].action='/selfservice/performance/batchGrade.do?b_tileFrame=link&random='+parseInt(100*Math.random())+'&operate=aaa'+plan_id;
		window.parent.document.forms[0].submit();
	}
}

function go_up()
{
	document.batchGrade_SinglePoint_Form.point_index.value=document.batchGrade_SinglePoint_Form.point_index.value*1-1;
	document.batchGrade_SinglePoint_Form.action="/performance/batchGradeSinglePoint.do?b_Desc=query&random="+parseInt(100*Math.random());
	document.batchGrade_SinglePoint_Form.submit();
}
// 1:上一步  2:下一步
function updown(opt)
{
	if(opt==2)
		document.batchGrade_SinglePoint_Form.point_index.value=document.batchGrade_SinglePoint_Form.point_index.value*1+1;
	else
		document.batchGrade_SinglePoint_Form.point_index.value=document.batchGrade_SinglePoint_Form.point_index.value*1-1;
	document.batchGrade_SinglePoint_Form.action="/performance/batchGradeSinglePoint.do?b_Desc=query&random="+parseInt(100*Math.random());
	document.batchGrade_SinglePoint_Form.submit();

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
	        
	        style.posLeft=window.screen.availWidth/2-350;
	        style.posTop=pos[1]+srcobj.offsetHeight;  
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
	        
	        style.posLeft=window.screen.availWidth/2-350;
	        style.posTop=pos[1]+srcobj.offsetHeight;  
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
		
		
	}
  
   
   function replaceAll( str, from, to ) {
	    var idx = str.indexOf( from );
	    while ( idx > -1 ) {
	        str = str.replace( from, to ); 
	        idx = str.indexOf( from );
	    }
	   
	    return str;
	}
	
   function hidden()
   {
   	Element.hide('date_panel');
   }
   
    
