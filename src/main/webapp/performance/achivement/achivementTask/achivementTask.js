
//条件选人
function conditionselect()
{
	
		var theurl="/performance/implement/kh_object/condition_select.do?b_query=link`db=Usr";
    	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);

   		
    	var config = {
	      	width:550,
	      	height:450,
	      	type:'2'
    	}
   		modalDialog.showModalDialogs(iframe_url,'template_win',config,function(sql_str){
   			if(sql_str!=null)
			{
				var hashvo=new ParameterSet();
				hashvo.setValue("sql_str",sql_str.sql);
				var request=new Request({method:'post',asynchronous:false,onSuccess:showFieldList,functionId:'9020020112'},hashvo);  
			}
   		});
}
function conditionselect_ok(sql_str){
   			if(sql_str!=null)
			{
				var hashvo=new ParameterSet();
				hashvo.setValue("sql_str",sql_str.sql);
				var request=new Request({method:'post',asynchronous:false,onSuccess:showFieldList,functionId:'9020020112'},hashvo);  
			}
   		}

function showFieldList(outparamters)
{
		var fieldlist=outparamters.getValue("fieldlist");	
		AjaxBind.bind(achievementTaskForm.right_fields,fieldlist);
}


//分配任务-删除对象
function delObject()
{
	   			 vos= document.getElementsByName("right_fields");
				 if(vos==null)
				  	return false;
				 right_vo=vos[0];
				 for(var i=right_vo.options.length-1;i>=0;i--)
				 {
				    if(right_vo.options[i].selected)
				    {
						right_vo.options.remove(i);
				    }
				 }
}
   		
//分配任务-添加对象   		
function addObject(flag)
{
   				if(root.getSelected()=="")
				{
						alert("请选择考核对象！");
						return;
				}	
				var temp_str=root.getSelected();
				var temp_str2=root.getSelectedTitle();
				
				var num=0;
				var temps=temp_str.split(",");
   				for(var i=0;i<temps.length;i++)
   				{
   					if(temps[i].length>0)
   					{
   						if(flag=='1'&&temps[i].substring(0,2)!='Us')
   						{
   							alert("请选择人员!");
   							return;
   						}
   						num++;
   					}
   				}
   				var temps2=temp_str2.split(",");
   				for(i=0;i<temps.length;i++)
				{
				    if(temps[i].length>0)
				    {
				    	var isExist=0;
				    	for(var j=0;j<document.achievementTaskForm.right_fields.options.length;j++)
				    	{
				    		if(document.achievementTaskForm.right_fields.options[j].value==temps[i])
				    		{	isExist=1;
				    			break;
				    		}
				    	}
				    	if(isExist==1)
				    		continue;
				        var no = new Option();
				    	no.value=temps[i];
				    	no.text=temps2[i];
				    	document.achievementTaskForm.right_fields.options[document.achievementTaskForm.right_fields.options.length]=no;
				    }
				}
}

//提交分配任务	
function subAllocate()
{
   			vos= document.getElementsByName("right_fields");
			if(vos==null)
				  return false;
			right_vo=vos[0];
			if(right_vo.options.length==0)
			{	
				alert("请选择考核对象!");
				return;
			}
			setselectitem('right_fields');
			document.achievementTaskForm.action="/performance/achivement/achivementTask.do?b_suballocate=sub";
			document.achievementTaskForm.submit();
}	