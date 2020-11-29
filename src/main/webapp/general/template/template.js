var isHeadCountControl=true; //编制控制
var selectHint="";//判断是否选中的提示信息 selectAll=5时用到 批量审批 wangrd 2014-01-16
//发表意见  model: 6:报备  7 加签
function pubOpinion(taskid,model)
{

	var thecodeurl="/general/template/edit_page.do?b_queryOpinion=link`model="+model;
   	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
	var obj_vo =window.showModalDialog(iframe_url,null,"dialogWidth=640px;dialogHeight=460px;status=no");
	 
	if(obj_vo){
		 var hashvo=new ParameterSet();   	     
 		 hashvo.setValue("taskid",taskid);
 	     hashvo.setValue("topic",getEncodeStr(obj_vo));
 	     hashvo.setValue("model",model); 
 		 var request=new Request({asynchronous:false,onSuccess:issuccess_opinion,functionId:'0570010158'},hashvo); 
		
	}
}


function issuccess_opinion(outparamters)
{
	//window.location.reload();
	parent.window.close();
	return;
}




//是否自动将索引条件下的 数据载入模板
 function validateIndex()
 {
	
     reloop();
	 if(num!=1)
 		return;
 	/*不知道什么意思 跟70不一样 暂时屏蔽
 	if(returnflag=='list')
		return; 
    if(returnflag=='listhome'||returnflag=='warnhome') 
 		return;
 		*/
 	if(setname=='templet_'+tabid)
 		return;
 	if(promptIndex_template=='false')	
 		return;
 	if(trim(sys_filter_factor).length==0)
 		return;	
 	if(trim(operationtype)=='0')
 		return;
 	var flag='0';  //0:不引入数据  1:清空当前人员,重新引入  2:不清空,引入符合条件的数据
 	
 	var recordNum=0;
 	var temps=document.getElementsByName("obj");
 	for(var i=0;i<temps.length;i++)
 	{
 		if(temps[i].type=='checkbox')
 		{
 			recordNum++;
 			break;
 		}
 	}
 	if(recordNum>0)
 	{
 		var theURL="/general/template/templatelist/importIndexMen.jsp";
		var iframe_url="/general/query/common/iframe_query.jsp?src="+theURL;
		var return_vo =window.showModalDialog(iframe_url,null,"dialogWidth=500px;dialogHeight=200px;resizable=yes;status=no;");  
 		if(return_vo!=null){
 			if(return_vo!='0')
 				flag=return_vo;
 		}
 	}
 	else
 	{
 		if(confirm('模板中没有人员,是否按模板的检索条件选人?'))
 		{
 			flag="1";
 		}
 	}
 	
 	if(flag!='0')
 	{
 		var hashvo=new ParameterSet();   	     
 		 hashvo.setValue("tabid",tabid);
 		  hashvo.setValue("flag",flag);
 		  displayProcessBar();
 		var request=new Request({asynchronous:true,onSuccess:issuccess_card,functionId:'0570040008'},hashvo); 
 	}
 }

function issuccess_card(outparamters)
{
   templateForm.action="/general/template/edit_page.do?b_query=link&pageno="+pageno+"&tabid="+tabid;
   templateForm.submit();
}



//
var bz_dataset;
function copydata(setname,ins_id)
{
        var datapilot,dataset,value,temp,field_name,field_label,field;
        datapilot=$(setname);
        dataset=datapilot.getDataset();
        var editor=dataset.getActiveEditor();
        if(editor)
        {
	        field_name=editor.getField();
	        if(field_name=="photo"||field_name=="ext")
		        	return;
	        if(field_name.indexOf("_1")>0)//??????????????
	          return;
	        field=dataset.getField(field_name);
	        if (!confirm(lbl_template_selected+field.getLabel()+lbl_template_curr))
	          return;
	        value=dataset.getValue(field_name);
		    var record=dataset.getFirstRecord();	
		    
		    
		   var table=$("obj_table");
		   var a0100_str="/";	     
		   for (var i=table.rows.length-1; i>0; i--)
		   {
		        var thetr = table.rows[i];
		        var thechkbox=thetr.cells[0].children[0];
		       	if(!thechkbox.checked)
		        		continue;
		        if(operationtype=='8'||(infor_type==2&&operationtype=='9'))
		       		a0100=thetr.cells[3].innerHTML;
		        else
			        a0100=thetr.cells[2].innerHTML;
		        a0100_str+=a0100.toLowerCase()+"/";
		   }	
		  
		//   return; 
		    
		    while (record) 
		    {		     
		     	if(infor_type=='1')
		        {
		   		   var basepre=record.getValue("basepre");
   			 	   var a0100=record.getValue("a0100");
		   		   if(a0100_str.indexOf("/"+basepre.toLowerCase()+"|"+a0100)!=-1)
				   {
					   	  record.setValue(field_name,value);
					   	  record.setState("modify");
				   }
				}
				if(infor_type=='2'||infor_type=='3')
				{   
				   var keyValue="";
				   if(infor_type=='2')
					   keyValue=record.getValue("B0110").toLowerCase();
   				   if(infor_type=='3')
   				 	   keyValue=record.getValue("E01A1").toLowerCase();
   				  
		   		   if(a0100_str.indexOf("/"+keyValue+"|"+keyValue)!=-1)
				   {
					   	  record.setValue(field_name,value);
					   	  record.setState("modify");
				   }
				 
				}  
			    record=record.getNextRecord();
		    }
	    }
	    else
	    {
	    	alert(SELECTEDITGRID);
	    }
}


function showList(returnflag,w_id,operationname)
{
    isSelectedObj();
	if(returnflag=="1"||returnflag=="list"||returnflag=="7"||returnflag=="10"||returnflag=="8"||returnflag=="bi"||returnflag=="3"||returnflag=="6"||returnflag=="9")
	{
		 
		if(sp_batch==1)
		{
		   var win=window.open("/general/template/templatelist.do?b_init=init&isInitData=0&sp_flag=1&ins_id="+ins_id+"&returnflag="+returnflag+"&tasklist_str="+batch_task+"&tabid="+tabid+"&index_template=1&selectAll="+selectAll,"_parent");
		}
		else
		{
			var win=window.open("/general/template/templatelist.do?b_init=init&isInitData=0&sp_flag=1&ins_id="+ins_id+"&returnflag="+returnflag+"&task_id="+taskid+"&tabid="+tabid+"&index_template=1&selectAll="+selectAll,"_parent");
	     }
	    return; 
	    
	}
	if(returnflag=="5")
	{
				var win=window.open("/general/template/templatelist.do?b_init=init&isInitData=0&sp_flag=1&ins_id=0&returnflag=warnhome&task_id=0&tabid="+tabid+"&warn_id="+w_id+"&selectAll="+selectAll,"_parent");
				return;    
	}
	
}

function returnbrowseprint(returnflag,w_id,operationname,bosflag)
{
	   autoSaveData();//bug 32816 人事异动业务模板忘记保存，希望点击返回自动保存。
	   if(returnflag=='list')
	   {
	   		 parent.location='/general/template/templatelist.do?b_init=query'+employee+'&fromModel=mb&tabid='+tabid;	
	   }
	   else if(returnflag=='listhome'||returnflag=='warnhome')
	   {
	   		 parent.location='/general/template/templatelist.do?b_init=query'+employee+'&fromModel=mb&sp_flag='+sp_flag+'&ins_id='+ins_id+'&returnflag='+returnflag+'&task_id='+taskid+'&tabid='+tabid;	
	   }
	   else  if(returnflag=='bi')
	   {
	   		 parent.location='/templates/index/bi_portal.do?br_query=link';	
	   }
	   else
	   {	
		
	       var tab; 
	       if(returnflag=='12')
	       {
	           parent.window.close();
	           return;
	       }
	       
	       if(returnflag=="5")
	       {
				var win=window.open("/system/warn/result_manager.do?b_query=link&warn_id="+w_id,"_parent");
				return;    
	       }
	       if(returnflag=="6")//来自自助用户的业务申请（菜单中的）
	       {
	    	   /*
	    	    if(modeType=='23'||modeType=='24') //如果业务申请带了参数     23：考勤业务办理  24：非考勤业务(业务申请不包含考勤信息)   标准版本应该注释掉，特殊要求再放开
	    	    {
	    	    	if(typeof(sp_flag)!="undefined"&&sp_flag=='2')
		       			var win=window.open("/general/template/myapply/busidesktop.do?br_query=link&type="+modeType+"&operate=dtask2","_parent");
		       		else
						var win=window.open("/general/template/myapply/busidesktop.do?br_query=link&type="+modeType,"_parent");
	    	    }
	    	    else */
	    	    {
		       		if(typeof(sp_flag)!="undefined"&&sp_flag=='2')
		       		{
		       			if(businessModel_yp&&businessModel_yp=='3')  //已办任务选项卡进入
		       				var win=window.open("/general/template/myapply/busidesktop.do?br_query=link&businessModel="+businessModel+"&operate=dtask2","_parent");  
		       			else  //我的申请选项卡进入
		       				var win=window.open("/general/template/myapply/busidesktop.do?br_query=link&operate=dtask4","_parent");
		       		}
		       		else if(typeof(sp_flag)=="undefined"){//来自自助申请待办 xucs2014-6-5
		       			var win=window.open("/general/template/myapply/busidesktop.do?br_query=link&businessModel="+businessModel+"&operate=dtask3","_parent");  
		       		}else{//自助用户的业务申请
		       			var win=window.open("/general/template/myapply/busidesktop.do?br_query=link&operate=dtask1","_parent");
		       		}
						
	    	    }
		       	return;    
	       }     
	       if(returnflag=="7")//返回4.0首页界面
	       {
	          var win=window.open("/system/home.do?b_query=link","_parent");
				return;   
	       }    
	       if(returnflag=="8")//返回5.0首页界面
	       {
	    	  if(bosflag&&bosflag=="hcm"){//返回7.0hcm首页
	    		  var win=window.open("/templates/index/hcm_portal.do?b_query=link","_parent");
	    		  return;
	    	  }else{
	    		  var win=window.open("/templates/index/portal.do?b_query=link","_parent");
				  return;  
	    	  }
	       }    
	       if(returnflag=='9')
	       {
	       	  document.templateForm.action="/general/template/search_module.do?b_query=link&operationname="+operationname+"&staticid="+staticid;
	          document.templateForm.target='mil_body'
	          document.templateForm.submit();   	
	       	  return;
	       }
	     
	       if(returnflag=='10')
	       { 
	       	   parent.location='/general/template/matterList.do?b_query=link';
	       	   return;
	       }
	       if(returnflag=='11')
	       { 
	       	   parent.location='/general/template/myapply/businessApplyList.do?b_query=link';
	       	   return;
	       }
	        if(returnflag=='hire')
	       { 
     	  	   parent.close();
	       	   return;
	       } 

	       
	       if(returnflag=='Y')//党组织人员管理 xuj 2010-2-25
	       {
	       	  var win=window.open("/dtgh/party/person/searchbusinesslist.do?b_search=link&politics="+operationname+"&param=Y&a_code="+w_id,"nil_body");
				return; 
	       }
	       if(returnflag=='V')//团组织人员管理 xuj 2010-2-25
	       {
	       	  var win=window.open("/dtgh/party/person/searchbusinesslist.do?b_search=link&politics="+operationname+"&param=V&a_code="+w_id,"nil_body"); 	
	       	  return;
	       }
	       if(returnflag=="2")
	         tab="task5";
	       else if(returnflag=="3")
	         tab="task4";
	       else if(returnflag=="4")
	         tab="task2";
	       else
	         tab="task1"; 
	       if(modeType.charAt(0)=='t') 
	       {
	    	   document.templateForm.action="/general/template/task_desktop.do?b_query=link&templateid=" +modeType+"&operate=" + tab;
	    	   document.templateForm.target="_parent";
	       }
	       else if(modeType=='23') //考勤业务申请
	       {
	    	   document.templateForm.action="/general/template/task_desktop.do?b_query=link&modeType=23&businessModel="+businessModel+"&operate=" + tab;
	    	   document.templateForm.target="_parent";
	       }
	       else
	       {
	    	   document.templateForm.action="/general/template/task_desktop.do?b_query=link&businessModel="+businessModel+"&operate=" + tab;
	           document.templateForm.target="_parent";
	       }
	       document.templateForm.submit();      
	       
	 }
}	

function showSelectBox(srcobj)
{
	var temp1 = document.getElementById("a0101");
	var temp = document.getElementsByName("a0101_box");
	if(temp[0].childNodes.length>0&&temp1.value.length>0){
      Element.show('a0101_pnl');   
      var pos=getAbsPosition(srcobj);
	  with($('a0101_pnl'))
	  {
	        style.position="absolute";
    		style.posLeft=pos[0]-1;
 		    style.posTop=pos[1]-1+srcobj.offsetHeight;
 		   
		    style.width=(srcobj.offsetWidth<150)?150:srcobj.offsetWidth+1;
      }     
	}else{
		 Element.hide('a0101_pnl');
	}
} 


function showPDFcard(outparamters)
{
 
    var url=outparamters.getValue("url");
    var win=open("/servlet/DisplayOleContent?filename="+url,"pdf");	
}
function excecutePDF(setname,tabid)
{
    var table,dataset,basepre,a0100;
    table=$(setname);
    dataset=table.getDataset();
    basepre=dataset.getValue("basepre");
    a0100=dataset.getValue("a0100"); 
    var hashvo=new ParameterSet();
    hashvo.setValue("nid",a0100);
    hashvo.setValue("cardid",tabid);
    hashvo.setValue("cyear","2000");
    hashvo.setValue("userpriv","noinfo");
    hashvo.setValue("istype","1");        
    hashvo.setValue("cmonth","12");
    hashvo.setValue("season","1");
    hashvo.setValue("ctimes","1");
    hashvo.setValue("cdatestart","2000.12.1");
	hashvo.setValue("cdateend","2000.12.1");
	hashvo.setValue("infokind","1");
	hashvo.setValue("querytype","1");
	hashvo.setValue("userbase",basepre);      
    var In_paramters="exce=PDF";  
    var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showPDFcard,functionId:'07020100005'},hashvo);
}
	
function showA0101(outparamters)
{
	var objlist=outparamters.getValue("objlist");
	var str_value=outparamters.getValue("str_value");
	if(objlist!=null)
	{		
		if(objlist.length>0)
		{
			Element.show('a0101_box');   
			AjaxBind.bind($('a0101_box'),objlist);		
			
			if(typeof(str_value)!="undefined"&&str_value.length>0)
			{ 
				var objs=str_value.split("`");
				var _objs= new Array();
				for(var i=0;i<objs.length;i++)
				{
					var temp=objs[i].split("~");
					_objs["_"+temp[1]]=temp[0];
				}
				
				var obj=document.getElementsByName("a0101_box");
				for(var i=0;i<obj[0].options.length;i++)
	  			{
	  				var _value=obj[0].options[i].value;
	  				var desc=_objs["_"+_value];
	  				if(typeof(desc)!="undefined")
	  				{
	  					obj[0].options[i].title=desc;
	  				}
	  			}
			}
			
			
		}
		else
		{
			Element.hide('a0101_box');         
		}		  
	}		
}
//xyy 20141226处理input键盘想下事件 让上下能选select中的option
var optionNum = -1;  //记录向上或向下的的位置
function updown(e){
	var keycode;
	if(navigator.appName == "Microsoft Internet Explorer"){
		 keycode = event.keyCode;  
    }else{
    	 keycode = e.which;  
    }
	
	var elem2 = document.getElementsByName("a0101_box");
	var optionLength = elem2[0].childNodes.length; //select 下拉框选项的个数
	if(optionLength>0){
		 switch (keycode) {
		    case 38://up键
		        	optionNum--;
		            if(optionNum<0)
		            	optionNum=optionLength-1;
		            elem2[0].value = elem2[0].childNodes[optionNum].value;
		       
		        break;
		    case 40://down键
		        	optionNum++;
		            if(optionNum>=optionLength) 
		            	optionNum=0;
		            elem2[0].value = elem2[0].childNodes[optionNum].value;
		       
		        break;
		    case 13:
					var objid;
					if(optionNum<0)//bug 32561 用户没有选人，optionNum为-1，会报错。人为置为0选第一个。
						optionNum=0;
					objid = elem2[0].childNodes[optionNum].value;
					if(objid){
						if(objid.length>3||infor_type == 2||infor_type == 3){
							var objlist=new Array();
							objlist.push(objid);
							addObject(objlist,setname);
							
						}else{
							e.returnValue=false; // 在人员库回车键不做任何操作
							
						}
					}
		    	
		    	break;
		}
	}
	      
}  
//xyy 20141226 处理select回车事件
function selectupdown(e){
	if(navigator.appName == "Microsoft Internet Explorer"){
		var keycode = event.keyCode;  
		
    }else{
    	var keycode = e.which;  
    }
	if(keycode==13){
		var objid,i;
	       var obj=$('a0101_box');
	   	   for(i=0;i<obj.options.length;i++)
	       {
	          if(obj.options[i].selected)
	            objid=obj.options[i].value;
	       }       
	       if(objid)
	       {
	    	   if(objid.length>3||infor_type == 2||infor_type == 3){
		       	   var objlist=new Array();
		       	   objlist.push(objid);
				   addObject(objlist,setname);	
	    	   }else{
					e.returnValue=false; //
				}
	       }
	}
	
}

function query()
{
   	 var a0101=$F('a0101').trim();
   	 var hashvo=new ParameterSet();	
     hashvo.setValue("a0101",a0101);	
     hashvo.setValue("templateid",tabid);		
     hashvo.setValue("infor_type",infor_type);
     hashvo.setValue("filter_factor","");
     hashvo.setValue("nflag","0");
     hashvo.setValue("isVisibleUM","1");
     hashvo.setValue("isfilter_select",isfilter_select);
     
     var request=new Request({asynchronous:false,onSuccess:showA0101,functionId:'0570010128'},hashvo); 
}
function query(filter_factor)
{
	var keycode;
	var temp = document.getElementsByName("a0101_box");
	if(navigator.appName == "Microsoft Internet Explorer"){
		keycode = event.keyCode;  

    }else{
    	keycode = e.which;  
    }
	if(keycode==38||keycode==40||keycode==13){ //当上下回车的时候不进交易类
		return;
	}
	optionNum = -1; //当输入的时候讲select不选中
	if( temp[0].options[0]){
		temp[0].value = temp[0].options[0].value;  
	}
	
   	 var a0101=$F('a0101').trim();
   	 if(a0101.length<=0){
   		 return;
   	 }
   	 var hashvo=new ParameterSet();	
     hashvo.setValue("a0101",getEncodeStr(a0101));	
     hashvo.setValue("templateid",tabid);
     hashvo.setValue("infor_type",infor_type);
     hashvo.setValue("filter_factor",filter_factor);
     hashvo.setValue("nflag","0");	
     hashvo.setValue("isVisibleUM","1");
     hashvo.setValue("isfilter_select",isfilter_select);	
     hashvo.setValue("modeType",modeType);

     var request=new Request({asynchronous:false,onSuccess:showA0101,functionId:'0570010128'},hashvo); 
}
///批量修改	
function batchupdatefields(tabid,pageno)
{
	var thecodeurl="/general/template/batch_update.do?b_query=link`taskid="+taskid+"`tabid="+tabid+"`sp_batch="+sp_batch;
	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
//	var obj=eval("document.accountingForm.filterWhl");
    var dialogWidth="500px";
    var dialogHeight="510px";
    if (isIE6()){
    	dialogWidth="550px";
    	dialogHeight="550px";
    } 
//    Ext.Loader.setConfig({
//		enabled: true,
//		paths: {
//			'TemplateFileURL': '/general/template'
//		}
//	});
//    var obj=new Object();
//    obj.taskid=taskid;
//    obj.tabid=tabid;
//    obj.sp_batch=sp_batch;
//    Ext.require('TemplateFileURL.batch_update1', function(){
//		SalaryTemplateGlobal = Ext.create("TemplateFileURL.batch_update1",obj);
//	});
	var retvo= window.showModalDialog(iframe_url,"", 
		        "dialogWidth:"+dialogWidth+"; dialogHeight:"+dialogHeight+";resizable:no;center:yes;scroll:yes;status:no");			
  	 if(retvo)
	 {
	  	var fielditem_array=new Array();
	  	var fieldvalue_array=new Array();
	  	var fieldtype_array=new Array();
	  	var temp1=retvo.fielditem_array;
	  	var m1=temp1.length;
	  	for(var i=0;i<m1;i++)
	  	{
	  		fielditem_array[i]=temp1[i];
	  	}
	  	var temp2=retvo.fieldvalue_array;
	  	var m2=temp2.length;
	  	for(var j=0;j<m2;j++)
	  	{
	  		fieldvalue_array[j]=temp2[j];
	  	}
	  	var temp3=retvo.fieldtype_array;
	  	var m3=temp3.length;
	  	for(var k=0;k<m3;k++)
	  	{
	  		fieldtype_array[k]=temp3[k];
	  	}
	  	var selchecked=retvo.isOnlySelected;
	  	
		var hashvo=new ParameterSet();
		if(infor_type=='1')
	     {
		     var basepre=bz_dataset.getValue("basepre");
		     var a0100=bz_dataset.getValue("a0100");	
		     hashvo.setValue("basepre",basepre);
		     hashvo.setValue("a0100",a0100);
	     }
	     else if(infor_type=='2')
	     {
	     	 var B0110=bz_dataset.getValue("B0110");	
		     hashvo.setValue("B0110",B0110);
	     }
	     else if(infor_type=='3')
	     {
	    	 var E01A1=bz_dataset.getValue("E01A1");	
		     hashvo.setValue("E01A1",E01A1);
	     }
		hashvo.setValue("table_name",setname);
		hashvo.setValue("task_id",taskid);
		hashvo.setValue("selchecked",selchecked);
		hashvo.setValue("tabid",tabid);
		hashvo.setValue("sp_batch",sp_batch);
		hashvo.setValue("batch_task", batch_task);
		hashvo.setValue("fielditem_array",fielditem_array);
		hashvo.setValue("fieldvalue_array",fieldvalue_array);
		hashvo.setValue("fieldtype_array",fieldtype_array);
	  	var request=new Request({method:'post',asynchronous:false,onSuccess:refreshPage,functionId:'0570040009'},hashvo);
	 }
}
///这个方法稍微修改一下就可以通用
function refreshPage(outparamters)
{
	bz_dataset=setname;
	var flag=outparamters.getValue("flag");
	if(flag=="0")
		return;
	alert("修改成功!");
    if(infor_type=='1')
    {
	   	var basepre=outparamters.getValue("basepre");
	   	var a0100=outparamters.getValue("a0100");
	 	templateForm.action="/general/template/edit_page.do?b_query=link&pageno="+pageno+"&tabid="+tabid+"&a0100="+a0100+"&basepre="+basepre;
	}
	else if(infor_type=='2')
	{
		var B0110=outparamters.getValue("B0110");	
	 	templateForm.action="/general/template/edit_page.do?b_query=link&pageno="+pageno+"&tabid="+tabid+"&b0110="+B0110;
	}
	else if(infor_type=='3')
    {
		var E01A1=outparamters.getValue("E01A1");
	 	templateForm.action="/general/template/edit_page.do?b_query=link&pageno="+pageno+"&tabid="+tabid+"&e01a1="+E01A1;
	}
	if(sp_batch=='1')
		templateForm.action="/general/template/edit_page.do?b_query=link&pageno="+pageno+"&tabid="+tabid+"&sp_batch=1"
		
	templateForm.submit();
}
function refreshData(outparamters)
{
	var flag=outparamters.getValue("succeed");
	if(flag=="false")
		return;	
	alert("计算成功!");		
   	
   	var tabid=outparamters.getValue("tabid");
   	var pageno=outparamters.getValue("pageno");
  
    if(infor_type=='1')
    {
	   	var basepre=outparamters.getValue("basepre");
	   	var a0100=outparamters.getValue("a0100");
	 	templateForm.action="/general/template/edit_page.do?b_query=link&pageno="+pageno+"&tabid="+tabid+"&a0100="+a0100+"&basepre="+basepre;
	}
	else if(infor_type=='2')	
	{
		var B0110=outparamters.getValue("B0110");
	 	templateForm.action="/general/template/edit_page.do?b_query=link&pageno="+pageno+"&tabid="+tabid+"&b0110="+B0110;
	}
	else if(infor_type=='3')
    {
		var E01A1=outparamters.getValue("E01A1");
	 	templateForm.action="/general/template/edit_page.do?b_query=link&pageno="+pageno+"&tabid="+tabid+"&e01a1="+E01A1;
	}
	if(sp_batch=='1')
		templateForm.action="/general/template/edit_page.do?b_query=link&pageno="+pageno+"&tabid="+tabid+"&sp_batch=1"
	
	templateForm.submit();   
    //bz_dataset.flushData();
}

/* 去掉对无表达式的临时变量赋值 2016-05-04
function bz_computer(setname,tabid,ins_id,pageno)
{
	if(!autoSaveData())
	 	return;
   	 var hashvo=new ParameterSet();	
	 bz_dataset=setname;
     
     if(infor_type=='1')
     {
	     var basepre=bz_dataset.getValue("basepre");
	     var a0100=bz_dataset.getValue("a0100");	
	     hashvo.setValue("basepre",basepre);
	     hashvo.setValue("a0100",a0100);
     }
     else if(infor_type=='2')
     {
     	 var B0110=bz_dataset.getValue("B0110");	
	     hashvo.setValue("B0110",B0110);
     }
     else if(infor_type=='3')
     {
    	 var E01A1=bz_dataset.getValue("E01A1");	
	     hashvo.setValue("E01A1",E01A1);
     }
     hashvo.setValue("tabid",tabid);	
     hashvo.setValue("ins_id",ins_id);
     hashvo.setValue("ins_ids",ins_ids);
     hashvo.setValue("pageno",pageno);
     var request=new Request({asynchronous:false,onSuccess:bz_computer2,functionId:'0570010156'},hashvo); 
}

function bz_computer2(outparamters)
{
		var tabid=outparamters.getValue("tabid");
		var ins_id=outparamters.getValue("ins_id");
		var ins_ids=outparamters.getValue("ins_ids"); 
	    var message=getDecodeStr(outparamters.getValue("message"));
	 	var pageno=outparamters.getValue("pageno"); 
	 	
	 	
		var midValue="";
		var iscontinue="1";
		if(message.length>0)
		{
			var temps=message.split(",");
			for(var i=0;i<temps.length;i++)
			{
				var temp=temps[i].split(":");
				var theURL="/general/template/templatelist/setMidVarValue.jsp?var="+getEncodeStr(temp[0])+"`type="+temp[1]+"`alength="+temp[2];
				var iframe_url="/general/query/common/iframe_query.jsp?src="+theURL;
			 	var dialogWidth="400px";
				var dialogHeight="200px";
			    if (isIE6()){
			    	dialogWidth="430px";
			    	dialogHeight="200px";
			    } 
				var return_vo =window.showModalDialog(iframe_url,null,"dialogWidth="+dialogWidth+";dialogHeight="+dialogHeight+";resizable=yes;status=no;"); 
				
				if(return_vo!=null&&trim(return_vo).length>0){
					midValue=midValue+","+temp[0]+":"+return_vo;
				}else
				{
					iscontinue="0";
					break;
				} 
			}
	 	}
	 	if(iscontinue==0)
	 		return;
	   	 var hashvo=new ParameterSet();	 
	     if(infor_type=='1')
	     { 
		     hashvo.setValue("basepre",outparamters.getValue("basepre"));
		     hashvo.setValue("a0100",outparamters.getValue("a0100"));
	     }
	     else if(infor_type=='2')
	     { 
		     hashvo.setValue("B0110",outparamters.getValue("B0110"));
	     }
	     else if(infor_type=='3')
	     { 
		     hashvo.setValue("E01A1",outparamters.getValue("E01A1"));
	     }
	     hashvo.setValue("midValue",getEncodeStr(midValue));
	     hashvo.setValue("tabid",tabid);	
	     hashvo.setValue("ins_id",ins_id);
	     hashvo.setValue("ins_ids",ins_ids);
	     hashvo.setValue("pageno",pageno);
	     var request=new Request({asynchronous:false,onSuccess:refreshData,functionId:'0570010132'},hashvo); 
      
}

*/

function bz_computer(setname,tabid,ins_id,pageno)
{
	if(!autoSaveData())
	 	return;
    var hashvo=new ParameterSet();  
	hashvo.setValue("tabid",tabid);    
	hashvo.setValue("ins_id",ins_id);
	hashvo.setValue("ins_ids",ins_ids);
	hashvo.setValue("pageno",pageno);	
	hashvo.setValue("midValue","");
	var request=new Request({asynchronous:false,onSuccess:refreshData,functionId:'0570010132'},hashvo); 

}

function upload_picture(setname){
    var table=$(setname);
    var dataset=table.getDataset();
	var record =dataset.getCurrent();
	if(record==null)
	{
		alert(NOT_HAVE_RECORD);
		return;
	}    
    var basepre=dataset.getValue("basepre");
    var a0100=dataset.getValue("a0100");
    var ins_id=0;
    if(dataset.getField("ins_id"))
    	ins_id=dataset.getValue("ins_id");
    var thecodeurl ="/general/template/upload_picture.do?b_query=link`basepre="+basepre+"`a0100="+a0100+"`tablename="+setname+"`ins_id="+ins_id; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;     
 	var dialogWidth="500px";
	var dialogHeight="270px";
    if (isIE6()){
    	dialogHeight="310px";
    } 
    var return_vo= window.showModalDialog(iframe_url, "", 
              "dialogWidth:"+dialogWidth+"; dialogHeight:"+dialogHeight+";resizable:no;center:yes;scroll:yes;status:no");  
	//dataset.flushData();
	if(return_vo)
	{

		record.setValue("photo",return_vo);
	}

}
function upload_picture(setname){
    var table=$(setname);
    var dataset=table.getDataset();
	var record =dataset.getCurrent();
	if(record==null)
	{
		alert(NOT_HAVE_RECORD);
		return;
	}    
    var basepre=dataset.getValue("basepre");
    var a0100=dataset.getValue("a0100");
    var ins_id=0;
    if(dataset.getField("ins_id"))
    	ins_id=dataset.getValue("ins_id");
    var thecodeurl ="/general/template/upload_picture.do?b_query=link`basepre="+basepre+"`a0100="+a0100+"`tablename="+setname+"`ins_id="+ins_id; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;     
    var return_vo= window.showModalDialog(iframe_url, "", 
              "dialogWidth:500px; dialogHeight:270px;resizable:no;center:yes;scroll:yes;status:no");      
	//dataset.flushData();
	if(return_vo)
	{

		record.setValue("photo",return_vo);
	}

}

function setObjectState(chkobj,basepre,a0100,ins_id,setname)
{
 
  	    var submitflag;
        if(chkobj.checked)
          submitflag="1";
        else
          submitflag="0";
       	var hashvo=new ParameterSet();
       	hashvo.setValue("ins_id",ins_id);
       	hashvo.setValue("setname",setname);	
        hashvo.setValue("a0100",a0100);	 
        hashvo.setValue("basepre",basepre);	
        hashvo.setValue("submitflag",submitflag);	
        hashvo.setValue("task_id",taskid);
        hashvo.setValue("infor_type",infor_type); 
        hashvo.setValue("sp_batch",sp_batch);
   	    var request=new Request({asynchronous:false,functionId:'0570010134'},hashvo);   
}


function selectAllRecord()
{
	var c_value="1";
	if(!document.templateForm.all.checked)
		c_value="0";
	var hashvo=new ParameterSet();
    hashvo.setValue("ins_ids",ins_ids);
    hashvo.setValue("ins_id",ins_id);
    if(ins_id!="0")
    {
    	var temps=document.getElementsByName("obj");
		var ids="";
		for(var i=0;i<temps.length;i++)
		{
    		ids+="/"+temps[i].value;
    	}
    	hashvo.setValue("ids",ids);	
    }
    hashvo.setValue("setname",setname);	
	hashvo.setValue("submitflag",c_value);	
	hashvo.setValue("filterStr",filterStr); 
	hashvo.setValue("infor_type",infor_type);
	hashvo.setValue("task_id",taskid);
	  hashvo.setValue("sp_batch",sp_batch);
	     hashvo.setValue("batch_task",batch_task);
	var request=new Request({asynchronous:false,onSuccess:selectAllSuccess,functionId:'0570010146'},hashvo);  
	
}

function selectAllSuccess(outparamters)
{
	var c_value=true;
	if(!document.templateForm.all.checked)
		c_value=false;
    var temps=document.getElementsByName("obj");
	for(var i=0;i<temps.length;i++)
	{
		temps[i].checked=c_value; 
	//	temps[i].fireEvent('onclick');
	}


	 if(batchsignatureid=="0"&&c_value)
	 { 
	       batchsignatureid="1"; 
	      
      }
      else
	  {
		   batchsignatureid="0";  
      }
	 
}

function pointRecord()
{
	var obj=document.getElementsByName("_name");
	var name_desc=trim(obj[0].value);
	if(name_desc.length==0)
	{
		if(infor_type==1)
			alert("请填写用户姓名!");
		else if(infor_type==2)
			alert("请填写组织名称!");
		else if(infor_type==3)
			alert("请填写岗位名称!");	
		return;
	}
	var table=$("obj_table");
	var o_value="";
	var temps;
	var n=0;
	var top_index=0;
	if(pageCount>1)
		top_index=1;
    for (var i=top_index+1; i<table.rows.length; i++)
    {
        var thetr = table.rows[i];
        o_value=thetr.cells[1].innerHTML;
        
		temps=thetr.cells[2].innerHTML.split("|");
		
		var index_num=o_value.indexOf("\">");
		//alert(o_value+"  "+name_desc+"  "+o_value.substring(index_num+2));
		var index_end = o_value.indexOf("</A>");
		if(index_num!=-1&&index_end>index_num&&o_value.substring(index_num+2,index_end).indexOf(name_desc)!=-1)      
		{
		     if (temps.length==4){
                ins_id=temps[3]; 
             }
			 locaterec(temps[0],temps[1],ins_id);
		     n=1;
		     break;
		}
 
    }	 
    
	if(n==0)
	{
		if(pageCount>1)
		{	
			var hashvo=new ParameterSet();
			hashvo.setValue("name",name_desc);	
			hashvo.setValue("opt","1"); //opt: 1按名字查  2:按id查
			hashvo.setValue("tabid",tabid);	
			hashvo.setValue("ins_id",ins_id); 
			var request=new Request({asynchronous:false,onSuccess:pointRecordSuccess,functionId:'0570010168'},hashvo);
		}
		else
		{	
			alert("没有符合条件的记录!");
			return;
		}
	}
}


function pointRecordSuccess(outparamters)
{
	var returnInfo=outparamters.getValue("returnInfo"); 
	if(returnInfo.length==0)
	{
	//	alert("没有符合条件的记录!");
	}
	else
	{
		var temps=returnInfo.split("`");
		window.parent.test(temps[0],temps[1],temps[2]);
		var href =window.location.href; 
		var index_number=href.indexOf("&page_num=");
		if(index_number==-1)
			index_number=href.length;
		self.location=href.substring(0,index_number)+"&page_num="+temps[3];	 
	}

}



//翻页  1：上页   0：选择   2 ：下一页
function turnPage(opt)
{
	
	if(dataset.modified)
	{
		if(!confirm("修改过的内容没有执行保存操作，是否继续执行翻页操作？"))
			return;
	}
	var value=document.getElementsByName("pages")[0].value;
	var href =window.location.href; // window.parent.location.href; 
	var index_number=href.indexOf("&page_num=");
	if(index_number==-1)
		index_number=href.length;
	if(opt==1&&value!=1)
	{  
	 	self.location=href.substring(0,index_number)+"&page_num="+(value*1-1);	
	}
	else if(opt==0)
	{ 
		self.location=href.substring(0,index_number)+"&page_num="+value;	
	}
	else if(opt==2&&value!=document.getElementsByName("pages")[0].options.length)
	{ 
		self.location=href.substring(0,index_number)+"&page_num="+(value*1+1);	
	}
	
}



//显示左侧人员姓名列表
function showObjectList(divpnl,setname,ins_id,tablename)
{
	bz_dataset=setname;
    var pnlobj=$(divpnl);
    var  elem=document.createElement("table");
    if(infor_type=='1')
	    elem.setAttribute("width","140px"); 
    else
    {
    	if(operationtype=='8'||(infor_type==2&&operationtype=='9'))
       	    elem.setAttribute("width","180px");
    	else
	    	elem.setAttribute("width","160px");
    }
    elem.setAttribute("valign","top");
    elem.setAttribute("align","center");
    elem.setAttribute("cellSpacing","0");
    elem.setAttribute("cellPadding","0");
    elem.setAttribute("id","obj_table");
    var a0101;
    if(infor_type=='1')
    {
		a0101=bz_dataset.getField("A0101_1");
		if(a0101==null)
			a0101=bz_dataset.getField("A0101_2");
    }
    else if(infor_type=='2'||infor_type=='3')
    {
		a0101=bz_dataset.getField("codeitemdesc_1");
		if(a0101==null)
			a0101=bz_dataset.getField("codeitemdesc_2");
    } 
   
	   /**head*/ 
    var tr = elem.insertRow(elem.rows.length);
//	tr.className="TableRow_lock";
	var td = tr.insertCell(tr.cells.length);
    td.className="find_head_lock common_background_color common_border_color";
    if(operationtype=='8'||(infor_type==2&&operationtype=='9'))
 		td.setAttribute("colSpan","4");
	else
	    td.setAttribute("colSpan","3");	
	var size=10;
	if(infor_type!='1')
		size=15;
    td.innerHTML="<table><tr><td><input type='text' class='TEXT4' style='background-color:#FFFFFF;' name='_name' size='"+size+"' ></td><td><span><img src='/images/code.gif' style='position:relative;top:0px;' title='查询对象' onclick='pointRecord()' /></span></td></table> ";
	 
    if(pageCount>1)
    {
	    tr = elem.insertRow(elem.rows.length);
	    var td = tr.insertCell(tr.cells.length);
	    td.className="title_head_lock_left common_background_color common_border_color";
	    if(operationtype=='8'||(infor_type==2&&operationtype=='9'))
	 		td.setAttribute("colSpan","4");
		else
		    td.setAttribute("colSpan","3");	
	    td.setAttribute("height","20");
	    td.setAttribute("valign","middle");
	    td.setAttribute("align","center");
	    var desc="<table width='60%' ><tr><td>";
	    if(pageNum!=1)
	    	desc+="<a href='javascript:turnPage(1)' >";
	    desc+="<img src='/images/jt1.png' title='上一页' border='0'     />";
	    if(pageNum!=1)
	    	desc+="</a>";
	    desc+="</td><td align='center' ><SELECT name='pages'  onchange='turnPage(0)' >";
	    for(var n=1;n<=pageCount;n++)
	    {
	    	desc+=" <OPTION VALUE="+n+" ";
	    	if(n==pageNum)
	    		desc+=" selected ";
	    	desc+="  >第"+n+"页</OPTION>";
	    }
	    desc+="</SELECT>";
	    desc+="</td><td align='left' >"; 
	    if(pageNum!=pageCount)
	    	desc+="<a href='javascript:turnPage(2)' >";
	    desc+="<img src='/images/jt2.png' title='下一页'  border='0'   />";
	    if(pageNum!=pageCount)
	    	desc+="</a>";
	    desc+="</td></tr></table>";
	    td.innerHTML=desc;
    }
    
   
    var record=bz_dataset.getFirstRecord();	
	tr = elem.insertRow(elem.rows.length);
//	tr.className="TableRow_lock";
    td = tr.insertCell(tr.cells.length);
    td.className="title_head_lock_left common_background_color common_border_color";
    
    td.innerHTML="<input type=\"checkbox\" name=\"all\"  title=\""+SELECT_ALL+"\"  onclick=\"selectAllRecord()\"   />";   // LBL_SELECT;//"选择";
    td.setAttribute("align","center");
    
    if(operationtype=='8'||(infor_type==2&&operationtype=='9'))	
  	    td.setAttribute("width","20%");   
    else 
	    td.setAttribute("width","30%");   
    
    if( operationtype=='8'||(infor_type==2&&operationtype=='9'))
    {
	     td = tr.insertCell(tr.cells.length);
	     td.className="title_head_lock_right common_background_color common_border_color";
		 td.innerHTML="组号";
		 td.setAttribute("width","20%");
	     td.setAttribute("align","center");		
    }
    
    td = tr.insertCell(tr.cells.length);
   	td.className="title_head_lock_right common_background_color common_border_color";
    if(infor_type=='1')
	    td.innerHTML=LBL_NAME;//"姓名";
    else if(infor_type=='2')
		td.innerHTML="组织名称";
	else if(infor_type=='3')
		td.innerHTML="岗位名称";
    td.setAttribute("align","center");
    td = tr.insertCell(tr.cells.length);
    td.style.display='none';
	/*row*/
   
    var url,submitflag;
    var irow=0;
    while (record) 
	{		        
		++irow;
	  	tr = elem.insertRow(elem.rows.length);
	   	var key='';
       	if(infor_type=='2')
			key=record.getValue("B0110");
       	else if(infor_type=='3')
			key=record.getValue("E01A1");
	  	if(typeof(priv_obj)!="undefined"&&priv_obj.length>0)
	  	{
			for(var n=0;n<priv_obj.length;n++)
			{
				if(key==priv_obj[n])
					tr.className="trShallow2";
			}	  
		}
	//  if(irow%2==0)
	//  	 tr.className="trShallow";
	//  else
	//  	  tr.className="trDeep";
  
		var td = tr.insertCell(tr.cells.length);
		td.className="data_td_left common_border_color";
		submitflag=record.getValue("submitflag");
		if(ins_id!="0")
			ins_id=record.getValue("ins_id");
     
		var temp_value="";
		if(infor_type=='1')
		{
			temp_value=record.getValue("basepre")+record.getValue("a0100")
			if(submitflag==1)
				td.innerHTML="<input type=\"checkbox\" name=\"obj\"  value='"+temp_value+"' checked =\"true\" onclick=\"setObjectState(this,'"+record.getValue("basepre")+"','"+record.getValue("a0100")+"','"+ins_id+"','"+tablename+"')\">";//"选择"checked =\"true\";
			else
				td.innerHTML="<input type=\"checkbox\" name=\"obj\"  value='"+temp_value+"'  onclick=\"setObjectState(this,'"+record.getValue("basepre")+"','"+record.getValue("a0100")+"','"+ins_id+"','"+tablename+"')\">";//"选择"checked =\"true\";;
		}
		else if(infor_type=='2')
		{
			temp_value=record.getValue("B0110")
			if(submitflag==1)
				td.innerHTML="<input type=\"checkbox\" name=\"obj\"  value='"+temp_value+"' checked =\"true\" onclick=\"setObjectState(this,'"+record.getValue("B0110")+"','"+record.getValue("B0110")+"','"+ins_id+"','"+tablename+"')\">";//"选择"checked =\"true\";
			else
      			td.innerHTML="<input type=\"checkbox\" name=\"obj\"  value='"+temp_value+"'  onclick=\"setObjectState(this,'"+record.getValue("B0110")+"','"+record.getValue("B0110")+"','"+ins_id+"','"+tablename+"')\">";//"选择"checked =\"true\";;
		}
		else if(infor_type=='3')
		{
			temp_value=record.getValue("E01A1");
			if(submitflag==1)
      			td.innerHTML="<input type=\"checkbox\" name=\"obj\"  value='"+temp_value+"' checked =\"true\" onclick=\"setObjectState(this,'"+record.getValue("E01A1")+"','"+record.getValue("E01A1")+"','"+ins_id+"','"+tablename+"')\">";//"选择"checked =\"true\";
        	else
      			td.innerHTML="<input type=\"checkbox\" name=\"obj\"  value='"+temp_value+"'  onclick=\"setObjectState(this,'"+record.getValue("E01A1")+"','"+record.getValue("E01A1")+"','"+ins_id+"','"+tablename+"')\">";//"选择"checked =\"true\";;
		}
		td.setAttribute("align","center");	    
      
		if(operationtype=='8'||(infor_type==2&&operationtype=='9'))
		{
			td = tr.insertCell(tr.cells.length);
			td.className="data_td_right common_border_color";
     		var key='';
      		if(infor_type=='2')
				key=record.getValue("B0110");
         	else if(infor_type=='3')
				key=record.getValue("E01A1");
			if(typeof(group_arr[key])!="undefined")
			{
         		td.innerHTML=group_arr[key].split("`")[0];
			}
         	else
         		td.innerHTML="&nbsp;";
         	td.setAttribute("align","center");
		}
      
		td = tr.insertCell(tr.cells.length);
		td.className="data_td_right common_border_color";
		if(infor_type=='1')																											//考勤休假/申请登记/业务申请/请假申请/业务处理/编辑 左侧人员列表，人员名称为空时，表格没有边框 jingq upd 2014.08.28
			url="<a  href=\"javascript:locaterec('"+record.getValue("basepre")+"','"+record.getValue("a0100")+"','"+ins_id+"');\">"+(record.getValue(a0101.getName())!=""?record.getValue(a0101.getName()):"&nbsp")+"</a><span id='"+""+record.getValue("basepre")+record.getValue("a0100")+ins_id+"' style='display:none;'>&nbsp;<img src=\"/images/select.gif\" border=0 align=\"absmiddle\"></span>"
		else if(infor_type=='2')
			url="<a  href=\"javascript:locaterec('"+record.getValue("B0110")+"','"+record.getValue("B0110")+"','"+ins_id+"');\">"+(record.getValue(a0101.getName())!=""?record.getValue(a0101.getName()):"&nbsp")+"</a><span id='" +""+record.getValue("B0110")+record.getValue("B0110")+ins_id+"' style='display:none;'>&nbsp;<img src=\"/images/select.gif\" border=0 align=\"absmiddle\"></span>"
		else if(infor_type=='3')
			url="<a  href=\"javascript:locaterec('"+record.getValue("E01A1")+"','"+record.getValue("E01A1")+"','"+ins_id+"');\">"+(record.getValue(a0101.getName())!=""?record.getValue(a0101.getName()):"&nbsp")+"</a><span id='"+""+record.getValue("E01A1")+record.getValue("E01A1")+ins_id+"' style='display:none;'>&nbsp;<img src=\"/images/select.gif\" border=0 align=\"absmiddle\"></span>"
	  
		td.innerHTML=url;
		td.setAttribute("align","left");
		td = tr.insertCell(tr.cells.length);
		td.style.display='none';  
		if(infor_type=='1')
		{
			if(ins_id!="0")    
				td.innerHTML=record.getValue("basepre")+"|"+record.getValue("a0100")+"|"+record.getValue("task_id")+"|"+ins_id;
			else
				td.innerHTML=record.getValue("basepre")+"|"+record.getValue("a0100");
		}
		else if(infor_type=='2')
		{
			if(ins_id!="0")    
				td.innerHTML=record.getValue("B0110")+"|"+record.getValue("B0110")+"|"+record.getValue("task_id")+"|"+ins_id;
			else
				td.innerHTML=record.getValue("B0110")+"|"+record.getValue("B0110");
		}
		else if(infor_type=='3')
		{
			if(ins_id!="0")    
				td.innerHTML=record.getValue("E01A1")+"|"+record.getValue("E01A1")+"|"+record.getValue("task_id")+"|"+ins_id;
			else
				td.innerHTML=record.getValue("E01A1")+"|"+record.getValue("E01A1");
		}
		record=record.getNextRecord();
    }///while finish   
    pnlobj.appendChild(elem);
}
//当修改姓名后，失去焦点触发的事件
function refreshA0101(a0100,a0101)
{
	var table=$("obj_table");
	var oldvalue;	
    for (var i=table.rows.length-1; i>0; i--)
    {
        var thetr = table.rows[i];
        //因添加的人数大于40条时，系统自动添加一行，用于显示页数，这一行在一个td中，导致获取第二列数据时出错，所以应添加一个判断 20150911 liuzy
        if(thetr.cells[2]!=null){
	        oldvalue=thetr.cells[2].innerHTML;
	        if(ins_id!="0"&&(infor_type=='2'||infor_type==3))    
	        {
	        	oldvalues=oldvalue.split("|");
	        	oldvalue=oldvalues[0]+"|"+oldvalues[1];
	        }
			if(a0100==oldvalue)
			{
			   var hrefobj=thetr.cells[1].children[0]
		       hrefobj.innerHTML=a0101;
			}
        }
    }		
}
//用于鼠标触发的某一链接
var curObjA= null;
function locaterec(basepre,a0100,ins_id)
{
	
   var fromShowCard=window.parent.showCard;
  
   var record;
   if(infor_type=='1')
   {
	   var findarr=new Array("a0100");
	   findarr.push("basepre");
	   if(ins_id!=0)
		   findarr.push("ins_id");
	   var findvalue=new Array(a0100);
	   findvalue.push(basepre);
	   if(ins_id!=0)
		   findvalue.push(ins_id);
	   record=bz_dataset.find(findarr,findvalue,null);
	}
	else if(infor_type=='2')
	{
	   var findarr=new Array("B0110"); 
	   if(ins_id!=0)
		   findarr.push("ins_id");
	   var findvalue=new Array(a0100);
	   if(ins_id!=0)
		   findvalue.push(ins_id);
	   record=bz_dataset.find(findarr,findvalue,null);
	}
	else if(infor_type=='3')
	{
	   var findarr=new Array("E01A1"); 
	   if(ins_id!=0)
		   findarr.push("ins_id");
	   var findvalue=new Array(a0100);
	   if(ins_id!=0)
		   findvalue.push(ins_id);
	   record=bz_dataset.find(findarr,findvalue,null);
	}
	
 	var inner_basepre ="";
 	var inner_a0100 ="";
    if(record){
      	    window.parent.test(basepre,a0100,ins_id);
		    bz_dataset.setRecord(record);  	   
		    if(signxml.length>0)
		    {
			    if(infor_type=='1')
		   		{
		   		 initDocsignature(record.getValue("basepre"),record.getValue("a0100"));
		   		}
		    	else if(infor_type=='2')
				{
				 initDocsignature(record.getValue("B0110"),record.getValue("B0110"));
				}
				else if(infor_type=='3')
				{
				 initDocsignature(record.getValue("E01A1"),record.getValue("E01A1"));
				}
		    }
			if(sp_batch=="1"){
			 	ins_id=record.getValue("ins_id");
			 	taskid=record.getValue("task_id");
			}
			
			if(infor_type=='1')
			{
				inner_basepre=getEncodeStr(record.getValue("basepre"));
				inner_a0100=getEncodeStr(record.getValue("a0100"));
			}
			else if(infor_type=='2')
			{
				inner_basepre=getEncodeStr(record.getValue("B0110"));
				inner_a0100=getEncodeStr(record.getValue("B0110"));
			}
			else if(infor_type=='3')
			{
				inner_basepre=getEncodeStr(record.getValue("E01A1"));
				inner_a0100=getEncodeStr(record.getValue("E01A1"));
			}
	 }
	 else
	 {
		 if(fromShowCard&&trim(fromShowCard)=='1')
		 {
			 showCard(basepre,a0100,ins_id); 
		 }
		 else if(dataset.getFirstRecord()){
		    var record=dataset.getFirstRecord(); 
		     
		    if(infor_type=='1')
	   		{
	   			a0100=record.getValue("a0100");
	    		basepre=record.getValue("basepre");
	   		}
	    	else if(infor_type=='2')
			{
				a0100=record.getValue("B0110");
	    		basepre=record.getValue("B0110");
			}
			else if(infor_type=='3')
			{
			 	a0100=record.getValue("E01A1");
			 	basepre=record.getValue("E01A1");
			} 
	    	initDocsignature(basepre,a0100);
	    	if(sp_batch=="1"){
	    		ins_id=record.getValue("ins_id");
	    		taskid=record.getValue("task_id");
	    		cur_ins_id= ins_id;
			}
	    	window.parent.test(basepre,a0100,ins_id); 
			if(infor_type=='1')
			{
				inner_basepre=getEncodeStr(record.getValue("basepre"));
				inner_a0100=getEncodeStr(record.getValue("a0100"));
			}
			else if(infor_type=='2')
			{
				inner_basepre=getEncodeStr(record.getValue("B0110"));
				inner_a0100=getEncodeStr(record.getValue("B0110"));
			}
			else if(infor_type=='3')
			{
				inner_basepre=getEncodeStr(record.getValue("E01A1"));
				inner_a0100=getEncodeStr(record.getValue("E01A1"));
			}
	   	}
	 }
	
    if(curObjA!=null)
		curObjA.style.display="none";  
	curObjA=document.getElementById(""+basepre+a0100+ins_id);  
	if(curObjA!=null)
	   curObjA.style.display='inline';
	//////////////////处理附件 开始//////////
	if(record){//按照后台的处理逻辑,应该是真正的有人员才会重新加载这个数据
		for(var ii=0;ii<attachment_count;ii++)
		{
			var attachtypevalue=attachmentArray[ii];
			var attachmenturl="";
			if(sp_batch=="1")///如果是批量处理
			{
				if(infor_type=='1')
				{
					attachmenturl = "<iframe src=\"/general/template/upload_attachment.do?b_query=link&ins_id="+ins_id+"&task_id="+taskid+"&sp_batch_temp=1&objectid="+inner_a0100+"&basepre="+inner_basepre+"&attachmenttype="+attachtypevalue+"&infor_type=1\" scrolling=\"auto\" width=\"100%\" height=\"100%\" frameborder=\"0\"></iframe>";
				}
				else if(infor_type=='2')
				{
					attachmenturl = "<iframe src=\"/general/template/upload_attachment.do?b_query=link&ins_id="+ins_id+"&task_id="+taskid+"&sp_batch_temp=1&objectid="+inner_a0100+"&basepre=&attachmenttype="+attachtypevalue+"&infor_type=2\" scrolling=\"auto\" width=\"100%\" height=\"100%\" frameborder=\"0\"></iframe>";
				}
				else if(infor_type=='3')
				{
					attachmenturl = "<iframe src=\"/general/template/upload_attachment.do?b_query=link&ins_id="+ins_id+"&task_id="+taskid+"&sp_batch_temp=1&objectid="+inner_a0100+"&basepre=&attachmenttype="+attachtypevalue+"&infor_type=3\" scrolling=\"auto\" width=\"100%\" height=\"100%\" frameborder=\"0\"></iframe>";
				}
			}///如果是批量处理 结束
			else///如果是单个处理
			{
				if(infor_type=='1')
				{
				 	attachmenturl = "<iframe src=\"/general/template/upload_attachment.do?b_query=link&objectid="+inner_a0100+"&basepre="+inner_basepre+"&attachmenttype="+attachtypevalue+"&infor_type=1\" scrolling=\"auto\" width=\"100%\" height=\"100%\" frameborder=\"0\"></iframe>";
				}
				else if(infor_type=='2')
				{
				 	attachmenturl = "<iframe src=\"/general/template/upload_attachment.do?b_query=link&objectid="+inner_a0100+"&basepre=&attachmenttype="+attachtypevalue+"&infor_type=2\" scrolling=\"auto\" width=\"100%\" height=\"100%\" frameborder=\"0\"></iframe>";
				}
				else if(infor_type=='3')
				{
				 	attachmenturl = "<iframe src=\"/general/template/upload_attachment.do?b_query=link&objectid="+inner_a0100+"&basepre=&attachmenttype="+attachtypevalue+"&infor_type=3\" scrolling=\"auto\" width=\"100%\" height=\"100%\" frameborder=\"0\"></iframe>";
				}
			}///如果是单个处理 结束
			var tempobj=document.getElementById("attachmentid"+ii);
			if(tempobj!=null)
			{
				tempobj.innerHTML =attachmenturl;
			}
		}///循环 结束
		//////////////////处理附件 结束//////////  
	}
	setCalcItemGrid(inner_basepre,inner_a0100);

}


/**
 * 从列表点击某记录展开卡片页面
 * @param basepre
 * @param a0100
 */
function showCard(_basepre,_a0100,_ins_id)
{	
		var hashvo=new ParameterSet();
		hashvo.setValue("basepre",_basepre);	
		hashvo.setValue("a0100",_a0100);	
		hashvo.setValue("opt","2"); //opt: 1按名字查  2:按id查
		hashvo.setValue("tabid",tabid);	
		hashvo.setValue("ins_id",_ins_id); 
		var request=new Request({asynchronous:false,onSuccess:pointRecordSuccess,functionId:'0570010168'},hashvo);
}

 

  
    //计算公式 wangrd 2013-12-30
function setCalcItemGrid(inner_basepre,inner_a0100)
 {
    var objFormula = document.getElementsByName("item_calformula"); 
    if (objFormula!=null){  
      //  template_refresh="true";//保存后 自动刷新
	 	var ctabname =bz_dataset.id;
	    for (var i=0;i<objFormula.length;i++){
	       var obj =objFormula[i];	     
	       var gridid = obj.id; 
	   	   var hashvo=new ParameterSet();	
		   hashvo.setValue("gridid",gridid);	
		   hashvo.setValue("tabname",ctabname);	
		   hashvo.setValue("tabid",tabid);	
		   hashvo.setValue("taskid",taskid);
		   hashvo.setValue("ins_id",ins_id);
		//   hashvo.setValue("sp_batch",sp_batch);
		   hashvo.setValue("infor_type",infor_type);
		   hashvo.setValue("inner_basepre",inner_basepre);
		   hashvo.setValue("inner_a0100",inner_a0100);
		   hashvo.setValue("pageno",pageno);
		   var request=new Request({asynchronous:false,
		        onSuccess:setCalcGrid,functionId:'0570010166'},hashvo);       
	    }
    }
 }
 
 
function setCalcGrid(outparamters)
 {
  	var calcValue=outparamters.getValue("calcValue"); 
  	var gridid=outparamters.getValue("gridid"); 
    var objFormula = document.getElementById(gridid); 
    if (objFormula!=null){      
    	objFormula.value =calcValue;    
    }	
 
 }

function isSelectedObjRecord()
{
	var table=$("obj_table");
    var objarr=new Array();
    var bflag=false;	     
    for (var i=table.rows.length-1; i>1; i--)
    {
        var thetr = table.rows[i];
        var thechkbox=thetr.cells[0].children[0];
       	if(thechkbox.checked)
       	{
       		bflag=true;
         	break;
        }
    }		
    return bflag;
}


//判断勾选记录的情况。用全局变量selectAll控制
function isSelectedObj()
{ 
	   selectAll=0;    // 0:有未选择的记录（不是全选）: 1:全选  2:表中没有数据 4：一条也没有选中（全不选） 5:来源于同一单据的不支持部分审批  
    	    if(sp_batch!="1"&&pageCount*1==1)//如果不是批量处理	
    	    { 
    		 	var temps=document.getElementsByName("obj"); 
				var selectedCount=0;
				for(var i=0;i<temps.length;i++)
				{
					if(temps[i].checked)  
						selectedCount++;
				} 
    		 	if(temps.length==0)
    		 		selectAll=2;
    		 	else if(temps.length>0&&selectedCount==0)
    		 		selectAll=4;
    		 	else if(temps.length!=selectedCount)
    		 		selectAll=0;
    		 	else if(temps.length==selectedCount)
    		 		selectAll=1; 	
    		 	return;	
    		
    	    }
    	    if(sp_batch=="1"&&pageCount*1==1)
    	    {
    	   		 var temps=document.getElementsByName("obj"); 
			var selectedCount=0;
			for(var i=0;i<temps.length;i++)
			{
				if(temps[i].checked)  
					selectedCount++;
			} 
    		 	if(temps.length==0)
    		 		selectAll=2;  
    		 	else if(temps.length==selectedCount)
    		 		selectAll=1; 
    		 	 if(selectAll==2||selectAll==1)
	    		 	return;	 
    	    } 
   	    var hashvo=new ParameterSet();	
	    hashvo.setValue("tabid",tabid);	
	    hashvo.setValue("taskid",taskid);
	    hashvo.setValue("sp_batch",sp_batch);
	    hashvo.setValue("batch_task",batch_task);
	    hashvo.setValue("selfapply","false");
	    var request=new Request({asynchronous:false,onSuccess:isSelectedObj2,onFailure:dealOnError,functionId:'0570040051'},hashvo); 
}
 function isSelectedObj2(outparamters)
 {
    	var flag=outparamters.getValue("flag"); 
    	if(flag=='1')
    		selectAll=1;
    	else if(flag=='2')
    		selectAll=2;
    	else if(flag=='4')
    		selectAll=4;
    	else if(flag=='5'){
    	   selectAll=5;
    	   selectHint=getDecodeStr(outparamters.getValue("selectHint"));
    	}
    	else
    		selectAll=0; 
 }

 
 function isSelectedObjBySign()
 {
 	var table=$("obj_table");
     var objarr=new Array();
     var bflag=false;	     
     for (var i=table.rows.length-1; i>1; i--)
     {
         var thetr = table.rows[i];
         var thechkbox=thetr.cells[0].children[0];
        	if(thechkbox.checked)
        	{
        		bflag=true;
          	break;
         }
     }		
     return bflag;
 }
 
function getObjectList()
{
	var table=$("obj_table");
    var objarr=new Array();
    var a0100;	     
    for (var i=table.rows.length-1; i>0; i--)
    {
        var thetr = table.rows[i];
        var thechkbox=thetr.cells[0].children[0];
       	if(!thechkbox.checked)
        		continue;
        if(operationtype=='8'||(infor_type==2&&operationtype=='9'))
          a0100=thetr.cells[3].innerHTML;
        else			
      	  a0100=thetr.cells[2].innerHTML;
        if(trim(a0100).length>0)
	        objarr.push(a0100);
    }	
 
    return objarr;	
}
function delete_obj(setname,tabid,ins_id,pageno){
	disableButtons();
	setTimeout(function(){delete_obj_ProcessBar(setname,tabid,ins_id,pageno);closeProcessBar();},100);   
}
function delete_obj_ProcessBar(setname,tabid,ins_id,pageno)
{

	isSelectedObj();
	if(selectAll==4)
	 {
		closeProcessBar();
	    alert(NOT_HAVE_OBJECT2);
	    return;
	  }
	 else  if(selectAll==2)
	 {
		closeProcessBar();
    	alert(NOTING_SELECT);
    	return;
     } 
    if(!confirm(BOLISH_INFO)){
		closeProcessBar();
	    return;	
	}
    var objarr=getObjectList();
    if(objarr.length>0)
    {
   	   var hashvo=new ParameterSet();
       hashvo.setValue("a0100s",objarr);	
       hashvo.setValue("ins_id",ins_id);
       hashvo.setValue("pageno",pageno);
       hashvo.setValue("tabid",tabid);
       hashvo.setValue("setname",setname);
       hashvo.setValue("infor_type",infor_type);
       var request=new Request({asynchronous:false,onSuccess:delete_obj2,onFailure:dealOnError,functionId:'0570010145'},hashvo);    
    }	 
}   


function delete_obj2(outparamters)
{
	   var from_msg=outparamters.getValue("from_msg");
	   var isDelMsg="0";
	   if(from_msg=='1')
	   {
	   		if(infor_type=='1')
	   		{
		   		if(confirm("撤销人员中有其他模板下通知单后自动加入的人员，是否将通知单同时撤销？"))
		   			isDelMsg="1";
		   	}
		   	if(infor_type=='2')
	   		{
		   		if(confirm("撤销组织中有其他模板下通知单后自动加入的组织，是否将通知单同时撤销？"))
		   			isDelMsg="1";
		   	}
		   	if(infor_type=='3')
	   		{
		   		if(confirm("撤销岗位中有其他模板下通知单后自动加入的岗位，是否将通知单同时撤销？"))
		   			isDelMsg="1";
		   	}
		   	
		   	
	   }
	   var hashvo=new ParameterSet();
       hashvo.setValue("a0100s",outparamters.getValue("a0100s"));	
       hashvo.setValue("ins_id",outparamters.getValue("ins_id"));
       hashvo.setValue("pageno",outparamters.getValue("pageno"));
       hashvo.setValue("tabid",outparamters.getValue("tab_id"));
       hashvo.setValue("setname",outparamters.getValue("setname"));
       
       hashvo.setValue("sp_batch",sp_batch);
       hashvo.setValue("batch_task",batch_task);
       hashvo.setValue("task_id",taskid);
       hashvo.setValue("ins_ids",ins_ids);
       hashvo.setValue("infor_type",infor_type);
       hashvo.setValue("isDelMsg",isDelMsg);
       hashvo.setValue("operationtype",operationtype);
       hashvo.setValue("selectAll",selectAll);//selectAll=1时，表示全选
       var request=new Request({asynchronous:false,onSuccess:isSuccess,onFailure:dealOnError,functionId:'0570010133'},hashvo);    

}
function submitData(setname,tabid){
	disableButtons();
	setTimeout(function(){submitData_processBar(setname,tabid);},100);   
}

	function submitData_processBar(setname,tabid)
	{
		isSelectedObj();
		if(selectAll==0&&taskid!=0)
   	    {
				closeProcessBar();
              if(infor_type=='1'){
	            	alert(NOT_HAVE_OBJECT);
	            }
              else{
	              	alert(NOT_HAVE_ORGRECORD);
	            }
        	return;   	    
   	    }  else  if(selectAll==2){
				closeProcessBar();
        	alert(NOTING_SELECT);
        	return;
        } 
        
         if(taskid==0&&selectAll==4)
        {
				closeProcessBar();
            alert(NOT_HAVE_OBJECT2);
	        return;  
        }
        
        
		if(!ifqrzx()){
			closeProcessBar();
			return;
		}
		if(!autoSaveData()){
			closeProcessBar();
			return; 
		}
		 //var hashvo=new ParameterSet();
   	  //   hashvo.setValue("tabid",tabid/*"${templateForm.tabid}"*/);
   	    // hashvo.setValue("taskid",taskid/*"${templateForm.taskid}"*/);
   	    // hashvo.setValue("ins_id",ins_id/*"${templateForm.ins_id}"*/);
  		// hashvo.setValue("sp_flag",sp_flag/*${templateForm.sp_flag}"*/);
  		// hashvo.setValue("flag","1");
  	   //  hashvo.setValue("a0100","a0100");
  	   //  hashvo.setValue("pre","pre");
  	   //  hashvo.setValue("id","id");  	      
  	  //   hashvo.setValue("infor_type",infor_type);
		// var request=new Request({asynchronous:false,onSuccess:subsuccess,functionId:'0570010131'},hashvo);   
		   
		/*
		var objarr=getObjectList();,以后可新增单个提交
    	if(objarr.length==0)
        {
            alert(NOT_HAVE_OBJECT);
        	return;
        }
        */
		isHeadCountControl=true;
   	    validateHeadCount("0",tabid);
        if(!isHeadCountControl){
            closeProcessBar();
            return;        
        }
       displayProcessBar();   
       setTimeout(function(){dealSubmitData()},100);  
    
}

/*
提交操作
*/
function dealSubmitData()
{
  var hashvo=new ParameterSet();
  hashvo.setValue("tabid",tabid/*"${templateForm.tabid}"*/);
  hashvo.setValue("taskid",taskid/*"${templateForm.taskid}"*/);
  hashvo.setValue("ins_id",ins_id/*"${templateForm.ins_id}"*/);
  hashvo.setValue("sp_flag",sp_flag/*${templateForm.sp_flag}"*/);
  hashvo.setValue("flag","1");
  hashvo.setValue("a0100","a0100");
  hashvo.setValue("pre","pre");
  hashvo.setValue("id","id");         
  hashvo.setValue("infor_type",infor_type);
  var request=new Request({asynchronous:false,onSuccess:subsuccess,onFailure:dealOnError,functionId:'0570010131'},hashvo);   
           
}

function ifqrzx()
{
	return ( confirm('您确认要执行此操作吗?') );
}

function subsuccess(outparamters)
{
      var judgeisllexpr=outparamters.getValue("judgeisllexpr");
      if(judgeisllexpr!="1"){
         closeProcessBar();
         alert(judgeisllexpr);
      }  
	  else
	  {
	    
	    var isSendMessage=outparamters.getValue("isSendMessage");
	    if(isSendMessage!=0)
	    {
	    	var thecodeurl="/general/template/submit_form.do?b_send=link`pt_type=1`isSendMessage="+isSendMessage+"`tabid="+tabid;
   	  	    var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
	    	var obj_vo =window.showModalDialog(iframe_url,null,"dialogWidth=650px;dialogHeight=480px;status=no");   
	    
	    } 
		 var hashvo=new ParameterSet();
	     hashvo.setValue("tabid",tabid);
	     hashvo.setValue("setname",setname);
	     hashvo.setValue("selectAll",selectAll);//=1 全选
	        //	hashvo.setValue("a0100s",objarr);
	         
	     isHeadCountControl=true;
    	 validateHeadCount("0",tabid);
         if(!isHeadCountControl){
             closeProcessBar();
             return;        
         }
                  
         var request=new Request({asynchronous:false,onSuccess:isSuccess2,onFailure:dealOnError,functionId:'0570010118'},hashvo); 
        
	  }
}




 function isSuccess2(outparamters)
    {
		closeProcessBar();
    	 var msgs = outparamters.getValue("msgs");
	    if(msgs!=null&&msgs!="yes"&&msgs.length>3)
	    alert(msgs);
    	var basepre=outparamters.getValue("basepre");
    	var a0100=outparamters.getValue("a0100");
   // 	var isSendMessage=outparamters.getValue("isSendMessage");
   //  	if(isSendMessage==0)
    	{
    		if(businessModel=='2')
    		{
    		    closeProcessBar();
    			alert("提交成功!");
		    	var ins_id=outparamters.getValue("ins_id");
		    	window.parent.returnValue=ins_id;
		  		window.parent.close();
    		}
    		else
    		{
    			templateForm.action="/general/template/edit_page.do?b_query=link&pageno="+pageno+"&tabid="+tabid+"&a0100="+a0100+"&basepre="+basepre;
	    		templateForm.submit();
	    	}
	    	
	    }
	/*    else 
	    {
	    	var temps=document.getElementsByName("obj");
			var objs="";
			for(var i=0;i<temps.length;i++)
			{
				if(temps[i].checked)
					objs=objs+"/"+temps[i].value;
	    	}
	    
	    	var thecodeurl="/general/template/submit_form.do?b_send=link`objs="+objs+"`isSendMessage="+isSendMessage+"`tabid="+tabid;
   	  	    var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
	    	var obj_vo =window.showModalDialog(iframe_url,null,"dialogWidth=650px;dialogHeight=480px");   
	    	templateForm.action="/general/template/edit_page.do?b_query=link&pageno="+pageno+"&tabid="+tabid+"&a0100="+a0100+"&basepre="+basepre;
	    	templateForm.submit();
	    }   
	    */
    }


function getprintobjContent(ins_id,objlist)
{
	var content="";
	var strarr;
	var contentlist=new Array();
	if(ins_id!=0)
	{
	  for(var i=0;i<objlist.length;i++)
	  {
	  	strarr=objlist[i].split("|");

	  	content=content+"<INS_ID>"
	  	content=content+ins_id;
	  	content=content+"</INS_ID>"	  	
	  	
	  	content=content+"<NBASE>"
	  	content=content+strarr[0];
	  	content=content+"</NBASE>"

	  	content=content+"<A0100>"
	  	content=content+strarr[1];
	  	content=content+"</A0100>"
	  	contentlist.push(content);
	  	content="";
	  }//for i loop end.
	}
	else
	{
	  for(var i=0;i<objlist.length;i++)
	  {
	  	strarr=objlist[i].split("|");
	  	content=content+"<NBASE>"
	  	content=content+strarr[0];
	  	content=content+"</NBASE>"

	  	content=content+"<A0100>"
	  	content=content+strarr[1];
	  	content=content+"</A0100>"
	    contentlist.push(content);
	    content="";
	  }//for i loop end.
	}
	return contentlist;
}


function judgelexpr()
{
        if(!AxManager.setup("TmplPreview1div", "TmplPreview1", 0, 0, judgelexpr, AxManager.tmplpkgName))
           return;

		var hashvo=new ParameterSet();    
	    hashvo.setValue("tabid",tabid);
   	    hashvo.setValue("task_id",taskid);
   	    hashvo.setValue("ins_id",ins_id);
   	    hashvo.setValue("batch_task",batch_task);
  	    hashvo.setValue("sp_batch",sp_batch); 	 
  	    hashvo.setValue("infor_type",infor_type);  
  	    hashvo.setValue("setname",setname); 
  	    hashvo.setValue("task_sp_flag",sp_flag); 
  	    hashvo.setValue("businessModel",businessModel);
  	    hashvo.setValue("businessModel_yp",businessModel_yp);
	    var request=new Request({asynchronous:false,onSuccess:printout,functionId:'0570010154'},hashvo);
}

/*
 =0 业务人员申请
 =1 审批
 =2 员工申请
*/
function printout(outparamters){
	 var judgeisllexpr=outparamters.getValue("judgeisllexpr");
	 var objStr=outparamters.getValue("objStr");
	 var remove_a0100="";
	 if(judgeisllexpr!=null && judgeisllexpr!="1"){
	 		remove_a0100=outparamters.getValue("remove_a0100")+",";
	 	    alert(judgeisllexpr);
	 }
	  var obj = document.getElementById("TmplPreview1");
      var isload = isLoad(obj);
      if(isload==true){
    	  loadOkPrintout(obj,objStr,remove_a0100);
      }
}
function loadOkPrintout(obj,objStr,remove_a0100){
	/**每次都初始化一下吧**/
	initCard(hosturl,dbtype,username,userFullName,superUser,nodeprive,tables,'TmplPreview1',_version,usedday);
	obj.SetTemplateID(tabid);
    /* 卡片类型：
       1: 模板
       2: 模板归档信息
       3: 员工申请临时表, g_templet_模板号
       4: 审批临时表, templet_模板号
    */
    var cardtype=1;
    if(ins_id!=0)
      cardtype=4; 
    obj.SetTemplateType(cardtype); 
    obj.ClearObjs(); 
    var objarr=new Array();

    var temps=objStr.split("`");
    var _objarr=new Array();
    
    if(objStr.length>0){
  	  for(var i=0;i<temps.length;i++)
  		  _objarr.push(temps[i]); 
    }
    
    if(remove_a0100!=null && remove_a0100!=""){
	     for(var i=0;i<_objarr.length;i++){  
					var strarr=_objarr[i].split("|"); 
					if(remove_a0100.indexOf(","+strarr[0].toLowerCase()+strarr[1]+",")==-1)
	       				objarr.push(_objarr[i]);
	     } 
	     if(objarr.length==0) 
	      return;
	}
	else{
	 	 objarr=_objarr;
	}
	  
    if(ins_id!="0")
        ins_id=dataset.getValue("ins_id");
    
    if(objarr.length==0){
    		alert("请选择需要打印的记录!");
    		return;
    }
    
    if(objarr.length>0){
      /* A0100参数格式：
       模板, 员工申请临时表:
       <NBASE></NBASE><A0100></A0100>
     模板归档:
       <ArchiveID></ArchiveID><NBASE></NBASE><A0100></A0100>
     审批临时表:
  	  <INS_ID>实例号</INS_ID><NBASE></NBASE><A0100></A0100>      
	      */
	      var contentlist;
	      if(ins_id!='0'&&ins_ids.split(",").length>=3)  //&&sp_mode=='1')
	      {
	      	  contentlist=getprintobjContent2(objarr);
	      }
	      else
		      contentlist=getprintobjContent(ins_id,objarr);
    	  for(var i=0;i<contentlist.length;i++)
    	  {
    	    obj.AddObj(contentlist[i]);
    	  }
    	  CreateSignatureJif("1");
    	  try { obj.SetJSessionId(AxManager.getJSessionId()); } catch(err) {}
	      obj.ShowCardModal();
    }	  	
}
/**用于判断插件是否加载完成**/
function isLoad(obj){
	var flag = true;
	try{
		obj.SetUrl("test");
	}catch(e){
		flag = false;
	}
	return flag;
}

function getprintobjContent2(objarr)
{
	//dataset中取不到其他页的选中数据，导致报错，改成从后台查出前台直接使用。 hej 20170113
	var record;
	var contentlist=new Array();
	var strarr;
  //  while (record) 
   // {		       
   //			var ins_id=record.getValue("ins_id");
   //			var basepre="";
	//		var a0100="";
   //			if(infor_type==1){
   //			 basepre=record.getValue("basepre");
	//		 a0100=record.getValue("a0100");
   	//		}else if(infor_type==2){
   //			basepre=record.getValue("B0110");
	//		 a0100=record.getValue("B0110");
   //			}else if(infor_type==2){
   //			basepre=record.getValue("E01A1");
	//		 a0100=record.getValue("E01A1");
   //			}
	//		var task_id=record.getValue("task_id")
			//这里为什么要反着循环？上面record取第一条记录,下面却取倒数第一条记录,这。。。xcs2014-10-27
			//for(var i=objarr.length-1;i>=0;i--)
			for(var i=0;i<objarr.length;i++)
			{
	//			if(objarr[i]==(basepre+"|"+a0100+"|"+task_id))
	//			{
				 strarr=objarr[i].split("|");
				 if(i==0){
				 	if (strarr.length!=3)
						record=dataset.getFirstRecord();
				 }
				 
				 var ins_id=0;
				 if (strarr.length==3){
					 ins_id = strarr[2];
				 }else{
				 	 ins_id = record.getValue("ins_id");	
				 }
				 
					var content="";
					content=content+"<INS_ID>"
				  	content=content+ins_id;
				  	content=content+"</INS_ID>"	  	
				  	
				  	content=content+"<NBASE>"
				  	content=content+strarr[0];
				  	content=content+"</NBASE>"
			
				  	content=content+"<A0100>"
				  	content=content+strarr[1];
				  	content=content+"</A0100>"
				  	contentlist.push(content);
				  	content="";
				  	if (strarr.length!=3)
				  	    record=record.getNextRecord();	
	//			  	break;
	//			}				
			}
   	//  record=record.getNextRecord();
   // }    
	return contentlist;
}


function setSelectValue(setname)
{
       var objid,i;
       var obj=$('a0101_box');
   	   for(i=0;i<obj.options.length;i++)
       {
          if(obj.options[i].selected)
            objid=obj.options[i].value
       }       
       if(objid)
       {
    	   if(objid.length>3||infor_type == 2||infor_type == 3){
	       	   var objlist=new Array();
	       	   objlist.push(objid);
			   addObject(objlist,setname);	
    	   }
       }
       Element.hide('a0101_pnl');         
}

function setFormula(tabid){
	   var thecodeurl="/general/salarychange/fomulatemplate.do?b_query=link`tableid="+tabid;
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
  var dialogWidth="400px";
  var dialogHeight="420px";
  if (isIE6()){
 	dialogWidth="430px";
 	dialogHeight="450px";
  } 
    var return_vo= window.showModalDialog(iframe_url, "", 
           	"dialogWidth:"+dialogWidth+"; dialogHeight:"+dialogHeight+";resizable:no;center:yes;scroll:yes;status:no");
}

	function setTempVar(tabid){
			var thecodeurl="/general/template/iframvartemp.jsp?nflag=0`state="+tabid;
			 var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
		     var dialogWidth="900px";
		     var dialogHeight="570px";
		     if (isIE6()){
		    	dialogWidth="900px";
		    	dialogHeight="630px";
		     } 
			var return_vo= window.showModalDialog(iframe_url, "", 
	           	"dialogWidth:"+dialogWidth+"; dialogHeight:"+dialogHeight+";resizable:no;center:yes;scroll:yes;status:no");
	}  

  function chkFormula(tabid)
  {
	   var thecodeurl="/general/chkformula/setformula.do?b_query=link`flag=0`tableid="+tabid;
	   var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
	   var dialogWidth="800px";
	   var dialogHeight="560px";
	   if (isIE6()){
	    	dialogWidth="820px";
	    	dialogHeight="580px";
	   } 
    var return_vo= window.showModalDialog(iframe_url, "", 
           	"dialogWidth:"+dialogWidth+"; dialogHeight:"+dialogHeight+";resizable:no;center:yes;scroll:yes;status:no");
 }  
  
    
   function add_newobj(setname,tabid)
   {
        var hashvo=new ParameterSet();
		hashvo.setValue("setname",setname);
		hashvo.setValue("tabid",tabid);
	   	var request=new Request({asynchronous:false,onSuccess:isSuccess,functionId:'0570010209'},hashvo); 
   }
   ///手工选人
   function get_hand_query(setname)
   {
     	var return_vo;
 		if(infor_type=='1'){
 			if(modeType=='23') //考勤业务
 				return_vo=select_org_emp_dialog2_rsjd("1","1","3","1",isfilter_select,"1",generalmessage); 
 			else
 				return_vo=select_org_emp_dialog2_rsjd("1","1","0","1",isfilter_select,"1",generalmessage); 
 		
 		}
 		if(infor_type=='2'){
 			 return_vo=select_org_emp_dialog2("0","1","0","1","0","1");  
 		}
 		if(infor_type=='3'){
 			return_vo=select_org_emp_dialog2("0","1","0","1","0","0");
 		}  
	 	 if(return_vo)
		 {
		    var sid=return_vo.content;
		    var objlist=sid.split(",");
		    
		     if(infor_type=='3'){
			    for(var i=0;i<objlist.length;i++){
			    	if(objlist[i].indexOf("UM")!=-1||objlist[i].indexOf("UN")!=-1){
			    	  alert("您只能选择职位！请不要选择单位或部门！");
			    	  return;
			    	  }
			    }
		    }
		    if(infor_type=='3'||infor_type=='2'){
		    	for(var i=0;i<objlist.length;i++){
			    	if(objlist[i].length>0){
			    		objlist[i]=objlist[i].substr(2);
			    	}
			    }
		    }
		    
		    addObject(objlist,setname);	
	 	}	
   }   

   function get_common_query(setname,dbpre,query_type,sys_filter_factor)
   {
        var dbpre_arr=new Array();
        dbpre_arr=dbpre.split(",");
        
        
        /*
		var objlist=common_query("1",dbpre_arr,query_type,getDecodeStr(sys_filter_factor));
		if(objlist&&objlist.length>0)
		{
		  for(var i=0;i<objlist.length;i++)
		  {
		    objlist[i]=objlist[i];
		  }	
		  addObject(objlist,setname);	 
		}*/
		if(isfilter_select=='0')
			sys_filter_factor='';
		var strdb="";
	    if(dbpre)
	      strdb=dbpre.toString();
	    var priv_ctrl="";
	    if(no_priv_ctrl=="1")
	    	priv_ctrl="`priv=0";
	    var strurl="/general/query/common/select_query_fields.do?b_init=link`isGetSql=1"+priv_ctrl+"`type="+infor_type+"`show_dbpre="+strdb+"`query_type="+query_type 
	      + "`filter_factor="+sys_filter_factor+'`tabid='+tabid;
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
	    
		/* 去掉薪资变动-选人-查询页面的滚动条 xiaoyun 2014-9-1 start */
	    //var height=430;
		var height=500;
		/* 去掉薪资变动-选人-查询页面的滚动条 xiaoyun 2014-9-1 end */
	 //   if(query_type==2)
	//		height=600;	    
		/* 去掉薪资变动-选人-查询页面的滚动条 xiaoyun 2014-9-1 start */
	    //var _sql=window.showModalDialog(iframe_url,null,"dialogWidth=600px;dialogHeight="+height+"px;resizable=no;status=no;");
		var _sql=window.showModalDialog(iframe_url,null,"dialogWidth=620px;dialogHeight="+height+"px;resizable=no;status=no;");
		/* 去掉薪资变动-选人-查询页面的滚动条 xiaoyun 2014-9-1 end */  
		if(_sql&&_sql.length>0)
		{
			var hashvo=new ParameterSet();
	        hashvo.setValue("_sql", getEncodeStr(_sql));
			hashvo.setValue("setname",setname);
			hashvo.setValue("tabid",tabid);
		   	var request=new Request({asynchronous:false,onSuccess:isSuccess,functionId:'0570010109'},hashvo); 
		}
		
   }     

    function showDOCData(outparamters)
    {
       var filename=outparamters.getValue("filename");
       var win=open("/servlet/DisplayOleContent?filename="+filename,"doc");	
    }   
    
    function assignSuc(outparamters)
    {
    	
    	var unDealedTaskIds = outparamters.getValue("unDealedTaskIds");
    	if (unDealedTaskIds!=null){//批量审批，有未处理的单据 需要刷新
    	   var beginRejectFlag = outparamters.getValue("beginRejectFlag");
    	  // var url="/general/template/edit_form.do?b_query=link&sp_batch=1&businessModel=0";   
    	   //var url ="/general/template/task_list.do?b_batch=link&unDealedTaskIds="+unDealedTaskIds;
    	   //为了兼容首页/待办任务 与 人事异动/批量审批 ，当多个待办任务没有全部操作完的时候，不经过task_list页面之间进行刷新 liuzy 20151125
    	   var url="/general/template/edit_form.do?b_query=link&sp_batch=1&businessModel=0&homeflag=1&batch_task="+getDecodeStr(unDealedTaskIds); 
    	    parent.window.open(url,"_self");  
    	   if(beginRejectFlag=='true'){
    	       var contentTip = outparamters.getValue("contentTip");
    	   	   alert(contentTip);
    	   }
    	   return;
    	}
    	
    	var msgs = outparamters.getValue("msgs");
	    if(msgs!=null&&msgs!="yes"&&msgs.length>3)
	    	alert(msgs);
    //	if(trim(pre_pendingID).length==0)
    	if(window.parent.parent.name.length>0)
    	{   
    	   if(modeType!=null&&modeType.charAt(0)=='t') //个性化开发，如链接带此参数，只显示该模板下的记录
 	       {
    		   window.open("/general/template/task_desktop.do?b_query=link&templateid=" +modeType,"_parent");  
    		   return;
 	       } 
 	       if(outparamters.getValue("returnflag")=="7")//返回4.0首页界面
	       {
	          var win=window.open("/system/home.do?b_query=link","_parent");
				return;   
	       }    
	       if(outparamters.getValue("returnflag")=="8")//返回5.0首页界面
	       {
	    	  if(bosflag&&bosflag=="hcm"){//返回7.0hcm首页
	    		  var win=window.open("/templates/index/hcm_portal.do?b_query=link","_parent");
	    		  return;
	    	  }else{
	    		  var win=window.open("/templates/index/portal.do?b_query=link","_parent");
				  return;  
	    	  }
	       }
		   if(outparamters.getValue("returnflag")=="6")
				window.open("/general/template/myapply/busidesktop.do?br_query=link","_parent");  	
			else
				window.open("/general/template/task_desktop.do?b_query=link","_parent");  
		}
		else
		{
			 alert('审批完成!');
			 window.parent.close();  //普天代办完成后关闭窗口 
    	}
    }    
    
    function print_applet(inparamters)
    {
		window.open("/general/template/print_template.do","_parent");  	
    }   
    
   function showPdf(outparamters)
   {
		var judgeisllexpr=outparamters.getValue("judgeisllexpr");
		if(judgeisllexpr!=null && judgeisllexpr!="1")
		    alert(judgeisllexpr);
	     else 
	     {
	            var filename=outparamters.getValue("filename"); 
		   		// var win=open("/servlet/DisplayOleContent?filename="+filename,"pdf");
		 		window.location.target="_blank";
			  	window.location.href="/servlet/DisplayOleContent?filename="+filename;
		 }		
   }    
   
    function showPDF(outparamters)
    {
      var url=outparamters.getValue("url");
    //var win=open("/servlet/DisplayOleContent?filename="+url,"pdf");	
      window.location.target="_blank";
	  window.location.href="/servlet/DisplayOleContent?filename="+url;
    }    

   function printInform(ins_id,tabid){//打印高级花名册
   		var ins_id =ins_id; //'${templateForm.ins_id}';
   		var spflag = '1';
   		if(ins_id!='0'){
   			spflag='2';
   		}
   		/**高级花名册过滤人员的sql已经被后台处理，前台不需要传递了xcs2014-9-25**/
   		var url = "/general/muster/hmuster/searchHroster.do?b_search=link`nFlag=5`spflag="+spflag+"`relatTableid="+tabid+"`closeWindow=2`print="+otherObjectPrint;
   		var framesurl = "/general/query/common/iframe_query.jsp?src="+url;
    	var return_vo =window.showModalDialog(framesurl,"",
			"dialogWidth=980px;dialogHeight=700px;resizable=no;scroll:yes;center:yes;status=no;");
   } 
   
   function addObject(objlist,setname)
   {	
        var hashvo=new ParameterSet();
        hashvo.setValue("objlist",objlist);
		hashvo.setValue("setname",setname);
		hashvo.setValue("tabid",tabid/*'${templateForm.tabid}'*/);
	   	var request=new Request({asynchronous:false,onSuccess:isSuccess,functionId:'0570010109'},hashvo); 
   }	 
   
   function aaaa(setname)
   {
   		var objlist=['010117','010121','010129','010115','010119','010123'];
   		 var hashvo=new ParameterSet();
   		 hashvo.setValue("objlist",objlist);
		hashvo.setValue("setname",setname);
		hashvo.setValue("tabid",tabid);
	   	var request=new Request({asynchronous:false,onSuccess:isSuccess,functionId:'0570010109'},hashvo); 
   }

   /**yj=0(正表)，=1审批意见表 */
   function outpdf(setname,ins_id,flag,yj,pageno)
   {
        var table,dataset,basepre,a0100;
        table=$(setname);
        dataset=table.getDataset();
        if(infor_type==1){
        	basepre=dataset.getValue("basepre");
        	a0100=dataset.getValue("a0100"); 
        }
        else if(infor_type==2)
        	a0100=dataset.getValue("b0110"); 
        else if(infor_type==3)
           a0100=dataset.getValue("e01a1"); 
        else{
           basepre=dataset.getValue("basepre");
           a0100=dataset.getValue("a0100"); 
        } 
        var cur_taskid="0";
        var cur_insid="0";
        if(ins_id!="0")
        {
          cur_insid=dataset.getValue("ins_id");
          cur_taskid=dataset.getValue("task_id");
        }  
        /*
        var objarr=getObjectList();
        if(flag==2&&objarr.length==0)
        {
        	if(infor_type==1)
        		alert("请选择需要生成PDF的人员!");
            if(infor_type==2)
            	alert("请选择需要生成PDF的机构!");
            if(infor_type==3)
            	alert("请选择需要生成PDF的岗位!");
        	return;
        } */
	//	if(objarr.length>0||flag==0||flag==1) 
        {
   
	        var hashvo=new ParameterSet();
	        hashvo.setValue("ins_id",cur_insid/*ins_id*/);
	        hashvo.setValue("task_id",taskid);//不能使用记录中的
			hashvo.setValue("setname",setname);
			hashvo.setValue("tabid",tabid/*'${templateForm.tabid}'*/);
			hashvo.setValue("flag",flag);
			hashvo.setValue("basepre",basepre);
			hashvo.setValue("a0100",a0100);
			hashvo.setValue("pageno",pageno);
			hashvo.setValue("yj",yj);	
		    hashvo.setValue("sp_batch",sp_batch/*"${templateForm.sp_batch}"*/);
		    hashvo.setValue("batch_task",batch_task/*"${templateForm.batch_task}"*/);
		    hashvo.setValue("infor_type",infor_type);
		    hashvo.setValue("task_sp_flag",sp_flag);
		    hashvo.setValue("businessModel_yp",businessModel_yp);
		    CreateSignatureJif("0");			
		   	var request=new Request({asynchronous:false,onSuccess:showPdf,functionId:'0570010119'},hashvo); 
		   
		} 
   }
   
   
   
   
   var a_tabid="";
   var a_setname="";
   function printActive(id,objname,setname)
   {
       if(!AxManager.setup("CardPreview1div", "CardPreview1", 0, 0, printActive, AxManager.cardpkgName))
          return;

       if(id)  // 回调时id为undefined
          a_tabid=id;
       if(setname)
          a_setname=setname       
       var hashvo=new ParameterSet();
       var objarr=getObjectList();
       if(objarr.length<=0){
    	   alert(NO_SELECT_PRINT_OBJECT);
       }
       if(objarr.length>0)
       {

       	hashvo.setValue("objarr",objarr);
       	hashvo.setValue("setname",a_setname);
       	hashvo.setValue("infor_type",infor_type);
       	hashvo.setValue("batch_task",batch_task);
  	    hashvo.setValue("sp_batch",sp_batch);
  	     hashvo.setValue("ins_id",ins_id);
  	    if(ins_id!=0){
	     var table,dataset;
        table=$(a_setname);
        dataset=table.getDataset();
        cur_taskid=dataset.getValue("task_id");
         hashvo.setValue("task_id",taskid);//不能使用记录中的
         }
         CreateSignatureJif("1");
         hashvo.setValue("task_sp_flag",sp_flag);
         hashvo.setValue("businessModel_yp",businessModel_yp);
       	var request=new Request({method:'post',onSuccess:printCard,functionId:'0570010139'},hashvo);
   	   }
   }   
   var CardPreview1Flag = 0;
   function printCard(outparamters)
   {
       var personlist=outparamters.getValue("personlist");  
       var table=$(setname);//$('${templateForm.setname}');    
       var dataset=table.getDataset(); 
       var  a0100,basepre;
        if(infor_type==1){
        basepre=dataset.getValue("basepre");
        a0100=dataset.getValue("a0100"); 
        }else if(infor_type==2)
        	a0100=dataset.getValue("b0110"); 
         else if(infor_type==3)
           a0100=dataset.getValue("e01a1"); 
         else{
          basepre=dataset.getValue("basepre");
          a0100=dataset.getValue("a0100"); 
         } 
       var card = document.getElementById("CardPreview1"); 
       var cardobj = isLoad(card);
       if(cardobj==true){
    	   CardPreview1Flag++;
    	   printCardLoadOk(card,basepre,personlist);
       }else{
          var timer = setInterval(function(){ 
	          CardPreview1Flag++;
	          card= document.getElementById('CardPreview1');  
	          cardobj = isLoad(card);
	      	  if(cardobj==true){
	       		printCardLoadOk(card,basepre,personlist);
	       		clearInterval(timer);
	       	  }else if(CardPreview1Flag==5){
	       		alert("插件加载失败！");
	       		CardPreview1Flag=0;
	       		clearInterval(timer);
	       	  }    
	       	},2000);
       }	
    }
    /**卡片打印调用**/
   function printCardLoadOk(obj,basepre,personlist){
	   initAciveCard(hosturl,dbtype,username,userFullName,superUser,fields,tables,'CardPreview1');
	   obj.SetCardID(a_tabid);
       obj.SetDataFlag("1");
       obj.SetNBASE(basepre);
       obj.ClearObjs();
       for(var i=0;i<personlist.length;i++)
       {
          obj.AddObjId(personlist[i].dataValue);          
       }
       try { obj.SetJSessionId(AxManager.getJSessionId()); } catch(err) {}
       obj.ShowCardModal();
   }
   	function printsuccess(outparamters)
   	{
   	
      var judgeisllexpr=outparamters.getValue("judgeisllexpr");
      var id=outparamters.getValue("id");
      var flag=outparamters.getValue("flag");
      var questionid=outparamters.getValue("questionid");
      var current_id=outparamters.getValue("current_id");
	  if(judgeisllexpr!="1")
	  {
	     alert(judgeisllexpr);
	  }
	  else
	  {
	     printexecute(id,flag,tabid/*"${templateForm.tabid}"*/,questionid,current_id);
	  }
   	}
   	
	function printexecute(id,flag,tabid,questionid,current_id)
	{
		
	  var hashvo=new ParameterSet();
	  hashvo.setValue("tabid",tabid);
	  hashvo.setValue("id",id);
	  hashvo.setValue("flag",flag);
	  hashvo.setValue("questionid", questionid);
	  hashvo.setValue("current_id", current_id);
	  if("1"==flag)
	  {
	    var request=new Request({asynchronous:false,onSuccess:outPutTemplateData,functionId:'0571000001'},hashvo);  
	  }else
	  {
             excecutePDF(setname/*'${templateForm.setname}'*/,id);
	  }
	    
	}   	
    function print(id,flag,tabid)
   	{
    	var questionid="130223197801121416";
		var CURRENT_ID="00000600";
   	     var hashvo=new ParameterSet();   	     
   	     if("1"==flag)
	     {
  	    	hashvo.setValue("flag","1");
	     }
	     else
	     {
	      hashvo.setValue("flag","0");
	     }
	     var table=$(setname);//$('${templateForm.setname}');
         var dataset=table.getDataset();
             
	    hashvo.setValue("tabid",tabid/*"${templateForm.tabid}"*/);
   	    hashvo.setValue("taskid",taskid/*"${templateForm.taskid}"*/);
   	    hashvo.setValue("ins_id",ins_id/*"${templateForm.ins_id}"*/);
   	    hashvo.setValue("batch_task",batch_task/*"${templateForm.batch_task}"*/);
  	    hashvo.setValue("sp_flag",sp_flag/*"${templateForm.sp_flag}"*/);
  	    hashvo.setValue("sp_batch",sp_batch/*"${templateForm.sp_flag}"*/);
  	    if(infor_type=='1')
  	    {
	  	    var basepre=dataset.getValue("basepre");
	        var a0100=dataset.getValue("a0100");     
	  	    hashvo.setValue("a0100",a0100);
	  	    hashvo.setValue("pre",basepre);
  	    }
  	    else if(infor_type=='2')
  	    {
  	    	var b0110=dataset.getValue("B0110");
  	    	hashvo.setValue("b0110",b0110);
  	    }
  	    else if(infor_type=='3')
  	    {
  	    	var e01a1=dataset.getValue("E01A1");
  	    	hashvo.setValue("e01a1",e01a1);
  	    }
  	    hashvo.setValue("id",id);  
  	    hashvo.setValue("infor_type",infor_type);
  	    hashvo.setValue("questionid", questionid);
  	    hashvo.setValue("current_id", CURRENT_ID);
	    var request=new Request({asynchronous:false,onSuccess:printsuccess,functionId:'0570010131'},hashvo); 
   	}    
   	
	function outPutTemplateData(outparamters)
	{
		var questionid=outparamters.getValue("questionid");
	    var current_id=outparamters.getValue("current_id");
	    var templatefile=outparamters.getValue("templatefile");
	    var tabid=outparamters.getValue("tabid");
	   
	    var sp_batch=outparamters.getValue("sp_batch"); //"${templateForm.sp_batch}";
	    //var batch_task=outparamters.getValue("batch_task");//"${templateForm.batch_task}";
	    var ins_id=outparamters.getValue("ins_id");//"${templateForm.ins_id}";
	    var table=$(setname);//$('${templateForm.setname}');
        var dataset=table.getDataset();
        var basepre="";
        var a0100=""; 
         if(infor_type=='1')
  	    {
	  	    basepre=dataset.getValue("basepre");
           a0100=dataset.getValue("a0100"); 
  	    }
  	    else if(infor_type=='2')
  	    {
  	    	a0100=dataset.getValue("B0110");
  	    }
  	    else if(infor_type=='3')
  	    {
  	    	a0100=dataset.getValue("E01A1");
  	    }
       var win=window.open("/servlet/OutputTemplateDataServlet?templatefile="+templatefile+"&questionid="+ questionid+"&current_id="+current_id+ "&tabid=" + tabid+"&ins_id="+ins_id+"&taskid="+taskid+"&sp_batch="+sp_batch+"&batch_task="+batch_task+"&pre="+basepre+"&a0100="+a0100,"_blank");
	}   	

	function outPutDocData(outparamters)
	{
	    //alert("asfdsda");
	    var templatefile=outparamters.getValue("templatefile");
	    var tabid=outparamters.getValue("tabid");
	    var sp_batch=sp_batch;//"${templateForm.sp_batch}";
	    var batch_task=batch_task;//"${templateForm.batch_task}";
	    var ins_id=ins_id;//"${templateForm.ins_id}";
	    var hashvo=new ParameterSet();
	    hashvo.setValue("templatefile",templatefile);
	    hashvo.setValue("tabid",tabid);
	    hashvo.setValue("sp_batch",sp_batch);
	    hashvo.setValue("batch_task",batch_task);
	    hashvo.setValue("ins_id",ins_id);
	    var request=new Request({asynchronous:false,onSuccess:showDOCData,functionId:'0571000002'},hashvo);  
	}
	///报批结束后的处理
    function isSuccess(outparamters)
    {
		closeProcessBar();
    	 var msgs = outparamters.getValue("msgs");
 	    if(msgs!=null&&msgs!="yes"&&msgs.length>3){
	    	closeProcessBar();
	    	alert(msgs);
	    }
	    else
	    {
		    var indexnames = outparamters.getValue("indexnames");
		    if(indexnames!=null&&indexnames.length>3){
		    	closeProcessBar();
		    	alert("检索条件限制了以下人员的引入:\r\n"+indexnames.substring(0,indexnames.length-1)+"！");
		    }
	    }
    	if(businessModel=='2')
    	{
    		closeProcessBar();
    		alert("报批成功!");
	    	var ins_id=outparamters.getValue("ins_id");
	    	window.parent.returnValue=ins_id;
	  		window.parent.close();
    	}
    	else
    	{
    		if(sp_batch=='1')
    		{
    			templateForm.action="/general/template/edit_page.do?b_query=link&pageno=0&tabid="+tabid+"&sp_batch=1";
			    templateForm.submit();
    		}
    		else
    		{
		    	var basepre=outparamters.getValue("basepre");
		    	var a0100=outparamters.getValue("a0100");
		    	window.parent.test(basepre,a0100);
		    	if(infor_type=='1')
			    	templateForm.action="/general/template/edit_page.do?b_query=link&pageno="+pageno+"&tabid="+tabid+"&a0100="+a0100+"&basepre="+basepre;
			    else if(infor_type=='2')
			    	templateForm.action="/general/template/edit_page.do?b_query=link&pageno="+pageno+"&tabid="+tabid+"&b0110="+a0100;
			    else if(infor_type=='3')
			    	templateForm.action="/general/template/edit_page.do?b_query=link&pageno="+pageno+"&tabid="+tabid+"&e01a1="+a0100;
			    templateForm.submit();
			}
		 }   
    }
   	//因为进度条的原因，必须有延迟才能显示进度条
   	function apply(setname,sp_mode){
   	    var no_sp_yj=document.getElementById("no_sp_yj").value;
 	    if (sp_mode!="1" && no_sp_yj==1){
	        	displayProcessBar();
	        	setTimeout(function(){applyWithProgress(setname,sp_mode);closeProcessBar();},100);   
	    }
	    else {
			 disableButtons();
			setTimeout(function(){applyWithProgress(setname,sp_mode);closeProcessBar();},100); 
			
	    }
   	}
    ///报批
   	function applyWithProgress(setname,sp_mode)
   	{
   		if(!autoSaveData()){
   		  closeProcessBar();
   		  return;
   		}   
   		isSelectedObj();
		if(selectAll==0&&taskid!=0)//如果taskid不是0，那么必须选中全部记录。
   	    {
				closeProcessBar();
			   if(infor_type=='1'){
	            	alert(NOT_HAVE_OBJECT);
	            }else{
	              	alert(NOT_HAVE_ORGRECORD);
	            }
        	return;   	    
   	    }  else  if(selectAll==2){//如果表（用户_templet_模板号）中没有数据
   	    	closeProcessBar();
   	    	alert(NOTING_SELECT);
        	return;  
        }
         
        if(taskid==0&&selectAll==4)//如果一条数据也没有选中
        {
        	closeProcessBar();
        	alert(NOT_HAVE_OBJECT2);
	        return;  
        }
        //如果全部符合条件，接下来判断业务规则。如校验公式等。
  	        var hashvo=new ParameterSet();
   	        hashvo.setValue("tabid",tabid/*"${templateForm.tabid}"*/);
   	        hashvo.setValue("taskid",taskid/*"${templateForm.taskid}"*/);
   	        hashvo.setValue("ins_id",ins_id/*"${templateForm.ins_id}"*/);
  			hashvo.setValue("sp_flag",sp_flag/*${templateForm.sp_flag}"*/);
  			hashvo.setValue("flag","1");
  	        hashvo.setValue("a0100","a0100");
  	        hashvo.setValue("pre","pre");
  	        hashvo.setValue("id","id");  
  	        hashvo.setValue("infor_type",infor_type);	    
	        var request=new Request({asynchronous:false,onSuccess:applysuccess,onFailure:dealOnError,functionId:'0570010131'},hashvo); 
	   	
   	}    
	//判断是否符合业务规则
   	function applysuccess(outparamters)
   	{
      var judgeisllexpr=outparamters.getValue("judgeisllexpr");
	  if(judgeisllexpr!="1"){
		  closeProcessBar(); 
	     alert(judgeisllexpr);
	  }   
	  {
	     applyexecute(setname/*'${templateForm.setname}'*/,sp_mode/*'${templateForm.sp_mode}'*/);
	  }
   	}
   	
   	
   	function finishedTask(ins_id,taskid)
	{
   			if(!autoSaveData())
   				return;
	 		isSelectedObj();
	  	    if(selectAll==0)
	   	    {
	             if(infor_type=='1'){
	            	alert(NOT_HAVE_OBJECT);
	            }else{
	              	alert(NOT_HAVE_ORGRECORD);
	            }
	        	return;   	    
	   	    }  else  if(selectAll==2){
	        	alert(NOTING_SELECT);
	        	return;  
	        } 	  
	  	    var no_sp_yj=document.getElementById("no_sp_yj").value;
	   	    var theurl="/general/template/apply_form.do?b_apply=link`modeType="+modeType+"`tabid="+tabid+"`message=0`sp_batch="+sp_batch+"`flag=1`noObject=1`ins_id="+ins_id+"&view_type=0";
	   	    var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
		    var dialogWidth="650px";
		    var dialogHeight="500px";
		    if (isIE6()){
		    	dialogWidth="670px";
		    	dialogHeight="530px";
		    } 
	   	    var obj_vo=window.showModalDialog(iframe_url,null,"dialogWidth="+dialogWidth+";dialogHeight="+dialogHeight+";resizable:yes;center:yes;scroll:yes;status:no");
			if(obj_vo)
			{
			
			    var param=new Object();
			    param.flag="1";
			    param.pri=obj_vo.pri;
			    param.content=getEncodeStr(obj_vo.content);
			    param.sp_yj=obj_vo.sp_yj;
			    		
	        	var hashvo=new ParameterSet();
	        	hashvo.setValue("actor",param);
	        	hashvo.setValue("ins_id",ins_id);
	        	hashvo.setValue("tabid",tabid);
	        	hashvo.setValue("taskid",taskid/*"${templateForm.taskid}"*/);
	        	
	       		
	       		var request=new Request({asynchronous:false,onSuccess:assignSuc,functionId:'0570010143'},hashvo); 
	       	}
	}
   	/*
   	 * 上会职称评审会议
   	 * */
	function subMeeting()
   	{  	
        if(!autoSaveData())
    	 	return;
   	    isSelectedObj();
		if(selectAll==0)
   	    {
   	    	if(infor_type=='1')
   	    	{
	   	       alert('请选中全部记录');
		     }
		     else
		     {		    
		         alert(NOT_HAVE_ORGRECORD);
		     }
        	return;   	    
   	    } else  if(selectAll==2){
        	alert(NOTING_SELECT);
        	return;  
        } else  if(selectAll==5){//批量审批 另走其他提示信息
            alert(selectHint);
            return;  
        }  
		Ext.require('JobtitleUL.SubMeeting',function(){
			Ext.create("JobtitleUL.SubMeeting",{
				tabid:tabid,
				ins_id:ins_id,
				taskid:taskid,
				sp_batch:sp_batch,
				batch_task:batch_task,
				returnflag:returnflag
			}
			);

		});  

   	}
		
   	
   	//因为进度条的原因，必须有延迟才能显示进度条
   	function assign(setname,ins_id,type,selfObj){   	
   		var no_sp_yj=document.getElementById("no_sp_yj").value;
 	    if (sp_mode!="1" && no_sp_yj==1){
	        	displayProcessBar();
	        	setTimeout(function(){assignWithProgress(setname,ins_id,type,selfObj);closeProcessBar();},100);   
	    }
	    else {
		  disableButtons();
	      setTimeout(function(){assignWithProgress(setname,ins_id,type,selfObj);closeProcessBar();},100);   
	    } 	
   	}
   	
   	var hashvo0=null;
   	/**继续报批。type=2:驳回 type=1：报批、提交*/
   	function assignWithProgress(setname,ins_id,type,selfObj)
   	{
   	    //var sp_mode=sp_mode;//"${templateForm.sp_mode}"; 
     	var dataset;
	    var table=$(setname);//$('${templateForm.setname}');
        dataset=table.getDataset();     	
        if(!autoSaveData()){
            closeProcessBar();
    	 	return;
        }
    /*  	if(dataset.getState()=="modify")
     	{
      		 if(!ifcontinue())
      		   return;
     	}	   
     */
   	   isSelectedObj();
		if(selectAll==0)
   	    {
			closeProcessBar();
			if(infor_type=='1')
   	    	{
	   	    	if(type==2)
	   				alert(NOT_HAVE_REJECTOBJECT);    	
	   	    	else
		           // alert(NOT_HAVE_OBJECT);
		            alert(GENERAL_TEMPLATE_NEEDAPPROVALAll);
		     }
		     else
		     {
		    	 if(type==2)
	   				alert(NOT_HAVE_REJECTRECORD);    	
	   	    	else
		            alert(NOT_HAVE_ORGRECORD);
		     }
        	return;   	    
   	    } else  if(selectAll==2){
   	    	closeProcessBar();
        	alert(NOTING_SELECT);
        	return;  
        } else  if(selectAll==5){//批量审批 另走其他提示信息
        	closeProcessBar();
        	alert(selectHint);
            return;  
        }else if(selectAll==4){
        	closeProcessBar();
	    	alert(NOT_HAVE_OBJECT2);
	    	return;
	    }   
   	 
   	    var no_sp_yj=document.getElementById("no_sp_yj").value;
   	    if(sp_mode=="1")//如果是手工指派   0：自动流转    1：手工指派
   	    {
   	    	var theurl="/general/template/apply_form.do?b_apply=link`modeType="+modeType+"`type="+type+"`sp_batch="+sp_batch+"`tabid="+tabid+"`noObject=0`ins_id="+ins_id+"&view_type=0"; //20140929 dengcan 注释 &from=rwjk
   	    	if(no_sp_yj==1){
   	    		if(allow_def_flow_self=='true'){
   	    			theurl="/general/template/apply_form.do?b_applynosp=link`modeType="+modeType+"`taskid="+taskid+"`def_flow_self=1`tabid="+tabid+"`operationtype="+operationtype+"`taskid="+taskid+"`type="+type+"`tabid="+tabid+"`noObject=0`ins_id="+ins_id+"`allow_def_flow_self="+allow_def_flow_self+"&view_type=0"; //20140929 dengcan 注释 &from=rwjk
   	    		}else{
   	    			theurl="/general/template/apply_form.do?b_applynosp=link`modeType="+modeType+"`type="+type+"`sp_batch="+sp_batch+"`tabid="+tabid+"`noObject=0`ins_id="+ins_id+"`allow_def_flow_self="+allow_def_flow_self+"&view_type=0"; //20140929 dengcan 注释 &from=rwjk
   	    		}
   	    	}else{
   	    		if(allow_def_flow_self=='true')
   	   	        	theurl="/general/template/apply_form.do?b_apply=link`modeType="+modeType+"`taskid="+taskid+"`def_flow_self=1`tabid="+tabid+"`operationtype="+operationtype+"`taskid="+taskid+"`type="+type+"`tabid="+tabid+"`noObject=0`ins_id="+ins_id+"&view_type=0"; //20140929 dengcan 注释 &from=rwjk
   	    	}
   	    	var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
   	        var obj_vo;
   	        if(no_sp_yj==1){
   	        	obj_vo =window.showModalDialog(iframe_url,null,"dialogWidth=500px;dialogHeight=250px;resizable:no;center:yes;scroll:yes;status:no");
   	        }else{
			    var dialogWidth="650px";
			    var dialogHeight="600px";
			    if (isIE6()){
			    	dialogWidth="700px";
			    	dialogHeight="650px";
			    } 	
   	        	obj_vo =window.showModalDialog(iframe_url,null,"dialogWidth="+dialogWidth+";dialogHeight="+dialogHeight+";resizable:no;center:yes;scroll:yes;status:no");
   	        }
			if(obj_vo)
			{
				
				
				if(obj_vo.flag!='2')
				{
					isHeadCountControl=true;
		        	validateHeadCount2(taskid,tabid,sp_batch,ins_id,batch_task);
		       		if(!isHeadCountControl)
		       	 			return;  
				}
				
				
			    var param=new Object();
			    param.name=obj_vo.name;
			    param.fullname=obj_vo.fullname;
			    param.objecttype=obj_vo.objecttype;
			    param.pri=obj_vo.pri;
			    param.content=getEncodeStr(obj_vo.content);
			    param.flag=obj_vo.flag;
			    param.sp_yj=obj_vo.sp_yj;
	        	var hashvo=new ParameterSet();
	        	hashvo.setValue("actor",param);
	        	hashvo.setValue("ins_id",ins_id);
	        	hashvo.setValue("tabid",tabid/*"${templateForm.tabid}"*/);
	        	hashvo.setValue("sp_mode",sp_mode);
	        	hashvo.setValue("sp_batch",sp_batch/*"${templateForm.sp_batch}"*/);	        	
	        	hashvo.setValue("taskid",taskid/*"${templateForm.taskid}"*/);
	        	hashvo.setValue("batch_task",batch_task/*"${templateForm.batch_task}"*/);	   
	        	hashvo.setValue("returnflag",returnflag/*"${templateForm.returnflag}"*/);   
	        	hashvo.setValue("url_s",url_s);  
	        	hashvo.setValue("isSendMessage",obj_vo.isSendMessage);
	        	hashvo.setValue("specialOperate",obj_vo.specialOperate); 
	        	hashvo.setValue("rejectObj",obj_vo.rejectObj);
	        	
	        	hashvo.setValue("pre_pendingID",pre_pendingID);
	       		if(obj_vo.isSendMessage!=0)
	       		{
	       			hashvo.setValue("user_h_s",obj_vo.user_h_s);
	       			hashvo.setValue("email_staff_value",obj_vo.email_staff_value);
	       			
	       		}
	        	
	        	hashvo0=hashvo; 
 				var hashvo1=new ParameterSet();
			    hashvo1.setValue("sp_mode","1");
			    hashvo1.setValue("tabid",tabid);
			    hashvo1.setValue("objecttype",obj_vo.objecttype);
			    hashvo1.setValue("name",obj_vo.name);
		   		var request=new Request({asynchronous:false,onSuccess:getNextNode3,functionId:'0570010155'},hashvo1);  
		   	//  var request=new Request({asynchronous:false,onSuccess:assignSuc,functionId:'0570010113'},hashvo); 
			}
		}
		else //如果是自动流转
		{					 
						 var flag=type;
						if(type==1)
						{
						 
							if(selfObj&&selfObj.value!='报送&amp;确认'&&(selfObj.value.indexOf("提交")!=-1||selfObj.value.indexOf("确认")!=-1))
								flag="3";
							else
								flag="1";
							/*
							if(selfObj&&(selfObj.value.indexOf("报送")!=-1||selfObj.value.indexOf("报批")!=-1))
								flag="1";
							else
								flag="3";
								*/
						} 
					    var hashvo=new ParameterSet();
		        	    hashvo.setValue("task_id",taskid); 
		        	    hashvo.setValue("sp_batch",sp_batch);
		        	    hashvo.setValue("batch_task",batch_task);
		        		hashvo.setValue("tabid",tabid);
		        		hashvo.setValue("ins_id",ins_id);
		        		hashvo.setValue("flag",flag);
		        		hashvo.setValue("type",type);
		        		hashvo.setValue("sp_mode",sp_mode);
		        		var request=new Request({asynchronous:false,onSuccess:getNextNode2,onFailure:dealOnError,functionId:'0570010155'},hashvo); 	
		}
   	}   
   	
   	//如果是手工审批
    function getNextNode3(outparamters)
    {
     				var specialRoleRoleId=outparamters.getValue("specialRoleRoleId");  
     				
     				if(trim(specialRoleRoleId).length>2&&specialRoleRoleId.substring(0,2)=='$$')//如果是特殊角色
    				{  
			         
			        	   hashvo0.setValue("specialRoleUserStr",specialRoleRoleId.substring(2));
			        	   var request=new Request({asynchronous:false,onSuccess:assignSuc,functionId:'0570010113'},hashvo0); 
    				}
    				else
    				{
			        	if(specialRoleRoleId.length>0)
			        	{
			        		var temp0=specialRoleRoleId.split("`");
			        		var obj_vo =window.showModalDialog("/general/template/myapply/busiPage.do?b_select=link&selfapply=0&roleid="+temp0[0]+"&role_property="+temp0[1]+"&sp_mode=1&tabid="+tabid,null,"dialogWidth=650px;dialogHeight=450px;status=no");  
			        		if(obj_vo&&obj_vo.length>0)
			        		{ 
			        			 hashvo0.setValue("specialRoleUserStr",obj_vo);
			        		    var request=new Request({asynchronous:false,onSuccess:assignSuc,functionId:'0570010113'},hashvo0); 
			        		}
			        	}
			        	else//如果是具体的审批人
			        	{
			        		 hashvo0.setValue("specialRoleUserStr","");
			        		 var request=new Request({asynchronous:false,onSuccess:assignSuc,functionId:'0570010113'},hashvo0); 
			        	}  
			        }
    }
   	
   	//如果是自动流转
   	function getNextNode2(outparamters)
    {
     				nextNodeStr=outparamters.getValue("nextNodeStr");
     				var flag=outparamters.getValue("flag");
     				var type=outparamters.getValue("type");
     				var _controlHeadCount=outparamters.getValue("controlHeadCount"); // 1:控制人员编制
     				if(typeof(_controlHeadCount)!="undefined"&&_controlHeadCount=='0')
     					controlHeadCount=_controlHeadCount;
    				var specialRoleNodeId="";
    				if(trim(nextNodeStr).length>2&&nextNodeStr.substring(0,2)=='$$')
    				{   
    					continueAssign(flag,type,nextNodeStr.substring(2)); 
    				}
    				else
    				{
			        	if(trim(nextNodeStr).length>0)
			        	{
			        		var _array0=nextNodeStr.split("`");
			        		for(var i=0;i<_array0.length;i++)
			        		{
			        			var _array1=_array0[i].split(":");
			        			if(_array1[1]=='1')
			        			{
			        				specialRoleNodeId+="`"+_array1[0];
			        			}
			        		}
			        	} 
			        	 
			        	if(specialRoleNodeId.length>0&&type!='2')
			        	{
			        		var obj_vo =window.showModalDialog("/general/template/myapply/busiPage.do?b_select=link&selfapply=0&sp_mode=0&tabid="+tabid+"&specialRoleNodeId="+specialRoleNodeId+"&batch_task="+batch_task,null,"dialogWidth=650px;dialogHeight=450px;status=no");  //bug33055 单子中人有多个直接领导，7x包60锁查询的不是勾选人，而是列表中最后一个人的。
			        		if(obj_vo&&obj_vo.length>0)
			        		{
			        			continueAssign(flag,type,obj_vo);
			        		}
			        	}
			        	else
			        	{
			        	
			        		continueAssign(flag,type,'');
			        	} 
		        	}
		        	
    
    }
    
   	function continueAssign(flag,type,specialRoleUserStr)
   	{
  	 	   
			var obj_vo; 
			var hashvo=new ParameterSet();
			var no_sp_yj=document.getElementById("no_sp_yj").value;
			var isOpenWindow=false; //是否弹出了意见填写窗口
	    if(isSendMessage=='0'&&no_sp_opinion=='true'&&type==1)
	    {
	        		obj_vo=new Object();
	        		obj_vo.sp_yj='01'
	        		obj_vo.pri='1';
	        		obj_vo.content='';
	        		obj_vo.isSendMessage=0;
	        		obj_vo.specialOperate='0';
	        		
	    }
	    else
			{
	        	if(no_sp_yj==1){
	        		obj_vo=new Object();
	        		obj_vo.sp_yj='01'
	        		obj_vo.pri='1';
	        		obj_vo.content='';
	        		obj_vo.isSendMessage=0;
	        		obj_vo.specialOperate='0';
	        	}else{
	        		var theurl="/general/template/apply_form.do?b_apply=link`modeType="+modeType+"`type="+type+"`sp_batch="+sp_batch+"`isApplySpecialRole="+isApplySpecialRole+"`tabid="+tabid+"`flag="+flag+"`noObject=1`ins_id="+ins_id+"&view_type=0";
					var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
				    var dialogWidth="650px";
				    var dialogHeight="600px";
				    if (isIE6()){
				    	dialogWidth="680px";
				    	dialogHeight="650px";
				    } 
					obj_vo=window.showModalDialog(iframe_url,null,"dialogWidth="+dialogWidth+";dialogHeight="+dialogHeight+";resizable:yes;center:yes;scroll:yes;status:no"); 
	        		isOpenWindow=true;
	        	}
				  
			}
			if(obj_vo)
			{ 
				    var param=new Object();
				    param.flag=type;
				    param.pri=obj_vo.pri;
				    param.content=getEncodeStr(obj_vo.content);
				    param.sp_yj=obj_vo.sp_yj;
				    		
	        	
	        	hashvo.setValue("actor",param);
	        	hashvo.setValue("ins_id",ins_id);
	        	hashvo.setValue("tabid",tabid/*"${templateForm.tabid}"*/);
	        	hashvo.setValue("sp_mode",sp_mode);
	        	hashvo.setValue("taskid",taskid/*"${templateForm.taskid}"*/);
	        	hashvo.setValue("sp_batch",sp_batch/*"${templateForm.sp_batch}"*/);
	        	hashvo.setValue("returnflag",returnflag/*"${templateForm.returnflag}"*/);
	        	hashvo.setValue("batch_task",batch_task/*"${templateForm.batch_task}"*/);
	        	hashvo.setValue("url_s",url_s);
	        	hashvo.setValue("isSendMessage",obj_vo.isSendMessage);
	        	hashvo.setValue("specialOperate",obj_vo.specialOperate); 
	        	hashvo.setValue("rejectObj",obj_vo.rejectObj);
	       		if(obj_vo.isSendMessage!=0)
	       		{
	       			hashvo.setValue("user_h_s",obj_vo.user_h_s);
	       			hashvo.setValue("email_staff_value",obj_vo.email_staff_value);
	       			
	       		}
	        	hashvo.setValue("pre_pendingID",pre_pendingID);
	        	hashvo.setValue("specialRoleUserStr",specialRoleUserStr);  
		        if(type=='1'/*&&(isOpenWindow||ifqrzx())*/)  //报批、提交 不弹确认框 wangrd 20151128
		        {  
		        	if(controlHeadCount=="1")
		        	{
			        	isHeadCountControl=true;
			        	validateHeadCount2(taskid,tabid,sp_batch,ins_id,batch_task);
			       		if(!isHeadCountControl)
			       	 			return;  
			       	 }
			    	 var request=new Request({asynchronous:false,onSuccess:assignSuc,onFailure:dealOnError,functionId:'0570010113'},hashvo); 
		        }		
		        if(type=='2')
		        {
		        	if (ifqrreject()){
				   	    var request=new Request({asynchronous:false,onSuccess:assignSuc,onFailure:dealOnError,functionId:'0570010113'},hashvo); 
		        	}
		        	else {
		        		closeProcessBar();		        	
		        	}
		        }
		    }	
   	}
   	
   		
 
 
   	/*如果符合业务规则，那么就开始为弹出对话框做准备。*/
	function applyexecute(setname,sp_mode)
	{
      var dataset;
      //dataset=${templateForm.setname};
      var table=$(setname);//$('${templateForm.setname}');
      dataset=table.getDataset();     	
      if(!autoSaveData())
  	 	return; 
   /*
      if(dataset.getState()=="modify")
      {
      	 if(!ifcontinue())
      	   return;
      }	  	 
      */
      
      var no_sp_yj=document.getElementById("no_sp_yj").value;
//      alert(no_sp_yj);
	  if(sp_mode=="1")//如果是手动流转
	  {
	    var theurl="/general/template/submit_form.do?b_apply=link`tabid="+tabid+"`noObject=0";//去更改这个弹框，当 no_sp_yj:审批不填写意见  1:选中   时给予页面不显示审批意见文本
	    
	    if(no_sp_yj==1){
//	    	alert(allow_def_flow_self);
	    	theurl="/general/template/submit_form.do?b_applynosp=link`task_id="+taskid+"`sp_batch="+sp_batch+"`tabid="+tabid+"`noObject=0`allow_def_flow_self="+allow_def_flow_self;
	    }else{
	    	if(allow_def_flow_self=='true')//自定义审批流程
		    	theurl="/general/template/submit_form.do?b_apply=link`tabid="+tabid+"`noObject=1";
	    }
//	    alert(theurl);
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
	    var obj_vo;
		if(no_sp_yj==1){
			obj_vo=window.showModalDialog(iframe_url,null,"dialogWidth=500px;dialogHeight=250px;resizable:no;center:yes;scroll:yes;status:no");
		}else{
		    var dialogWidth="650px";
		    var dialogHeight="600px";
		    if (isIE6()){
		    	dialogWidth="700px";
		    	dialogHeight="630px";
		    } 
			obj_vo =window.showModalDialog(iframe_url,null,"dialogWidth=650px;dialogHeight=600px;resizable:no;center:yes;scroll:yes;status:no");
		}    
		if(obj_vo)
		{
		    var param=new Object();
		    param.name=obj_vo.name;
		    param.fullname=obj_vo.fullname;		    
		    param.objecttype=obj_vo.objecttype;
		    param.pri=obj_vo.pri;
		    param.content=getEncodeStr(obj_vo.content);
		    param.sp_yj=obj_vo.sp_yj
		    //param.tabid="${templateForm.tabid}";
        	var hashvo=new ParameterSet();
        	hashvo.setValue("actor",param);
        	//hashvo.setValue("setname",setname);
       		hashvo.setValue("tabid",tabid/*"${templateForm.tabid}"*/);        	
       		hashvo.setValue("sp_mode","1");
       		hashvo.setValue("url_s",url_s);
       		hashvo.setValue("isSendMessage",obj_vo.isSendMessage);
       		hashvo.setValue("specialOperate",obj_vo.specialOperate);
       		if(obj_vo.isSendMessage!=0)
       		{
       			hashvo.setValue("user_h_s",obj_vo.user_h_s);
       			hashvo.setValue("email_staff_value",obj_vo.email_staff_value);
       			
       		}
       		
       		isHeadCountControl=true;
       		validateHeadCount("0",tabid);
       		if(!isHeadCountControl)
       	 			return; 
       		
       		hashvo0=hashvo;
	       	var hashvo1=new ParameterSet();
			hashvo1.setValue("sp_mode","1");
			hashvo1.setValue("tabid",tabid);
			hashvo1.setValue("objecttype",obj_vo.objecttype);
			hashvo1.setValue("name",obj_vo.name);
		    var request=new Request({asynchronous:false,onSuccess:getNextNode4,functionId:'0570010155'},hashvo1); 
       		 
	   	 //  var request=new Request({asynchronous:false,onSuccess:isSuccess,functionId:'0570010108'},hashvo);
	   	   
		}
	  }
	  else//如果是自动流转
	  {
	      //  if(iftqsp())
	        {
	        		var hashvo=new ParameterSet();
		        	hashvo.setValue("task_id",taskid);
		        	hashvo.setValue("tabid",tabid);
		        	hashvo.setValue("sp_batch",sp_batch);
		        	hashvo.setValue("batch_task",batch_task);
		        	hashvo.setValue("ins_id",ins_id);
		        	hashvo.setValue("sp_mode",sp_mode);
		        	var request=new Request({asynchronous:false,onSuccess:getNextNode,onFailure:dealOnError,functionId:'0570010155'},hashvo);  
        	}
	  }
    }  
    
    //手工流转的时候，报给特殊角色和报给普通人要分开处理。开始发起任务，把审批流写入表中。
     function getNextNode4(outparamters)
    {
    				var specialRoleRoleId=outparamters.getValue("specialRoleRoleId");   
    				if(trim(specialRoleRoleId).length>2&&specialRoleRoleId.substring(0,2)=='$$')
    				{  
			        	  hashvo0.setValue("specialRoleUserStr",specialRoleRoleId.substring(2));
			        	  var request=new Request({asynchronous:false,onSuccess:isSuccess,functionId:'0570010108'},hashvo0); 
    				}
    				else
    				{
    				
			        	if(specialRoleRoleId.length>0)
			        	{
			        		var temp0=specialRoleRoleId.split("`"); 
			        		var obj_vo =window.showModalDialog("/general/template/myapply/busiPage.do?b_select=link&selfapply=0&roleid="+temp0[0]+"&role_property="+temp0[1]+"&sp_mode=1&tabid="+tabid,null,"dialogWidth=650px;dialogHeight=450px;status=no");  
			        		if(obj_vo&&obj_vo.length>0)
			        		{ 
			        			 hashvo0.setValue("specialRoleUserStr",obj_vo);
			        		      var request=new Request({asynchronous:false,onSuccess:isSuccess,functionId:'0570010108'},hashvo0); 
			        		}
			        	}
			        	else
			        	{
			        		 hashvo0.setValue("specialRoleUserStr","");
			        		 var request=new Request({asynchronous:false,onSuccess:isSuccess,functionId:'0570010108'},hashvo0); 
			        	}  
			        }
    }
    
    ///处理下一个节点信息
    function getNextNode(outparamters)
    {
     				nextNodeStr=outparamters.getValue("nextNodeStr");
    				var specialRoleNodeId="";
    				if(trim(nextNodeStr).length>2&&nextNodeStr.substring(0,2)=='$$')
    				{  
    					continue_apply(nextNodeStr.substring(2));
    				}
    				else
    				{
			        	if(trim(nextNodeStr).length>0)
			        	{
			        		var _array0=nextNodeStr.split("`");
			        		for(var i=0;i<_array0.length;i++)
			        		{
			        			var _array1=_array0[i].split(":");
			        			if(_array1[1]=='1')
			        			{
			        				specialRoleNodeId+="`"+_array1[0];
			        			}
			        		}
			        	} 
			        	if(specialRoleNodeId.length>0)
			        	{
			        		var obj_vo =window.showModalDialog("/general/template/myapply/busiPage.do?b_select=link&selfapply=0&sp_mode=0&specialRoleNodeId="+specialRoleNodeId,null,"dialogWidth=650px;dialogHeight=450px;status=no");  
			        		if(obj_vo&&obj_vo.length>0)
			        		{
			        			continue_apply(obj_vo); 
			        		}
			        	}
			        	else
			        	{
			        	
			        		continue_apply('');
			        	} 
			        }
        	
    }
    
    
    ///准备弹出对话框，并准备把审批流数据写入数据库表
    function continue_apply(specialRoleUserStr)
   	{
		    	var hashvo=new ParameterSet();
		    	var no_sp_yj=document.getElementById("no_sp_yj").value;
   				var obj_vo; 
   				//问题号【15860】，感觉这个isSendMessage=='0'判断不应该放这，所以注释掉
	        	if(/*isSendMessage=='0'&&*/no_sp_opinion=='true')
	        	{
	        		obj_vo=new Object();
	        		obj_vo.sp_yj='01'
	        		obj_vo.pri='1';
	        		obj_vo.content='';
	        		obj_vo.isSendMessage=0;
	        		obj_vo.specialOperate='0';
	        		
	                if(!iftqsp()){
                        closeProcessBar();
                        return;
                    }
	        		
	        	}
	        	else
	        	{//自动流转的弹框
	        		if(no_sp_yj==1){
	        			obj_vo=new Object();
		        		obj_vo.sp_yj='01'
		        		obj_vo.pri='1';
		        		obj_vo.content='';
		        		obj_vo.isSendMessage=0;
		        		obj_vo.specialOperate='0';
		        		if(!ifqrzx()){
		        			return;
		        		}
	        		}else{
	        			var theurl="/general/template/submit_form.do?b_apply=link`isApplySpecialRole="+isApplySpecialRole+"`tabid="+tabid+"`noObject=1";
		        		var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
					    var dialogWidth="650px";
					    var dialogHeight="600px";
					    if (isIE6()){
					    	dialogWidth="680px";
					    	dialogHeight="650px";
					    } 
		        		
		        		obj_vo =window.showModalDialog(iframe_url,null,"dialogWidth="+dialogWidth+";dialogHeight="+dialogHeight+";status=no");   
	        		}
				}
				if(obj_vo)
				{
				
					var param=new Object();
					param.pri=obj_vo.pri;
				    param.content=getEncodeStr(obj_vo.content);
				    param.sp_yj=obj_vo.sp_yj
					
	        	  
	        	    hashvo.setValue("actor",param);
	        		hashvo.setValue("sp_mode","0");
	        		hashvo.setValue("url_s",url_s);
	        		hashvo.setValue("tabid",tabid/*"${templateForm.tabid}"*/);	        		
	        		hashvo.setValue("isSendMessage",obj_vo.isSendMessage);
	        		hashvo.setValue("specialOperate",obj_vo.specialOperate);
		       		if(obj_vo.isSendMessage!=0)
		       		{
		       			hashvo.setValue("user_h_s",obj_vo.user_h_s);
		       			hashvo.setValue("email_staff_value",obj_vo.email_staff_value);
		       			
		       		}
		       		isHeadCountControl=true;
		       		validateHeadCount("0",tabid);
		       		if(!isHeadCountControl)
		       	 			return; 
		       		 hashvo.setValue("specialRoleUserStr",specialRoleUserStr);
		       		 hashvo.setValue("selectAll",selectAll);//=1 全选
		   	         var request=new Request({asynchronous:false,onSuccess:isSuccess,onFailure:dealOnError,functionId:'0570010108'},hashvo); 
		   	        
	        	}
   	}
    
    
    
    function validateHeadCount(task_id,tabid)
    {
    	var hashvo=new ParameterSet();
    	hashvo.setValue("tabid",tabid);
    	hashvo.setValue("task_id",task_id);
    	var request=new Request({asynchronous:false,onSuccess:headCountResult,onFailure:dealOnError,functionId:'0570010167'},hashvo); 
    }
    function validateHeadCount2(task_id,tabid,sp_batch,ins_id,batch_task)
    {
    	var hashvo=new ParameterSet();
    	hashvo.setValue("tabid",tabid);
    	hashvo.setValue("task_id",task_id);
    	hashvo.setValue("sp_batch",sp_batch);
    	hashvo.setValue("ins_id",ins_id);
    	hashvo.setValue("batch_task",batch_task);
    	var request=new Request({asynchronous:false,onSuccess:headCountResult,onFailure:dealOnError,functionId:'0570010167'},hashvo); 
    }
    
    function headCountResult(outparamters)
    {
    	var msgs=outparamters.getValue("msgs");
    	var flag=outparamters.getValue("flag");
    	if(flag=='ok')
    		isHeadCountControl=true;
    	else if(flag=='warn')
    	{
    		if(confirm(msgs))
    			isHeadCountControl=true;
    		else{
    		    closeProcessBar();
    			isHeadCountControl=false;
    		}
    	}
    	else if(flag=='error')
    	{
    		closeProcessBar();
    		alert(msgs);
    		isHeadCountControl=false;
    	}
    	
    }
    
     	
/*
 dbtype=> 1: MSSQL, 2: ORACLE, 3: DB2
*/
function initCard(url,dbtype,username,userFullName,bsuper,fieldpriv,tablepriv,objname,_version,usedday)
{
      var obj = $(objname);      
      obj.SetURL(url);
      obj.SetDBType(dbtype);
      obj.SetUserName(username);     
      obj.SetSuperUser(bsuper);  // 1为超级用户,0非超级用户
      obj.SetUserMenuPriv(fieldpriv);  // 指标权限, 逗号分隔, 空表示全权
      obj.SetUserTablePriv(tablepriv);  // 子集权限, 逗号分隔, 空表示全权
      obj.SetHrpVersion(_version);
      obj.SetTrialDays(usedday,"30");
      
}
function initAciveCard(aurl,DBType,UserName,userFullName,superUser,menuPriv,tablePriv,objname)
{
      var obj = $(objname);       
      if(obj==null)
      {
         return false;
      }
      obj.SetSuperUser(superUser);  // 1为超级用户,0非超级用户
      obj.SetUserMenuPriv(menuPriv);  // 指标权限, 逗号分隔, 空表示全权
      obj.SetUserTablePriv(tablePriv);  // 子集权限, 逗号分隔, 空表示全权         
      obj.SetURL(aurl);
      obj.SetDBType(DBType);
      obj.SetUserName(UserName);
      obj.SetUserFullName(userFullName);
}

//查看审批意见
function open_showyj()
{
    var dialogWidth="630px";
    var dialogHeight="500px";
    if (isIE6()){
    	dialogWidth="650px";
    	dialogHeight="530px";
    } 
window.showModalDialog("/general/template/apply_form.do?b_spyj=spyj&cur_task_id="+taskid+"&cur_ins_id="+cur_ins_id,"", 
              "dialogWidth:"+dialogWidth+"; dialogHeight:"+dialogHeight+";resizable:no;center:yes;scroll:yes;status:no");
}
// ---------xieguiquan人员筛选
function to_person_filter(tabid)
	{
		
		sql_str=bankdisk_personFilter(tabid,1,1,infor_type);
		if(sql_str==null||trim(sql_str).length==0)
		{
	 
		}
		else
		{
	    /*
		  	document.getElementById("filterStr").value=sql_str; 
	   	    var href = parent.location.href; 
	   	    href=href.replace("b_init","br_init")+"&index_template=1"; //将filterStr放入userview中
		 	parent.location.href=href+"&isInitData=0";		
		*/ 
			var _href =self.location.href;
			self.location.href=_href;
		}
	}
//人员筛选
function bankdisk_personFilter(tabid,type,flag,infor_type){
	var name = getEncodeStr(setname);
	var theURL="/general/template/personFilter.do?b_select=select"+"`setname="+name+"`tabid="+tabid+"`flag="+flag+"`infor_type="+infor_type;
	var iframe_url="/general/query/common/iframe_query.jsp?src="+theURL;
    var dialogWidth="620px";
    var dialogHeight="400px";
    if (isIE6()){
    	dialogWidth="620px";
    	dialogHeight="430px";
    } 
	var objlist =window.showModalDialog(iframe_url,null,"dialogWidth="+dialogWidth+";dialogHeight="+dialogHeight+";resizable=yes;status=no;");  
	var obj=new Object();
	if(objlist==null)
	{
	obj.sql="";
	}else{
  obj.isclose=objlist.isclose;
  obj.sql=objlist.sql;
  obj.condid=objlist.condid;
  cond_id_str=obj.condid;
  if(parseInt(type)==1)
  {
  if(parseInt(obj.isclose)==2)
  {
   //  bankdisk_changeCondList(tabid);
  }
  }
}
return obj.sql;
}
function combine()
   {
   		
       
      	   var hashvo=new ParameterSet();          
      	   hashvo.setValue("infor_type", infor_type);
      	   hashvo.setValue("table_name", setname);
      	     hashvo.setValue("operationtype", operationtype);
      	      hashvo.setValue("tabid",tabid);
           var In_paramters="";
           var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:execombine,functionId:'0570040044'},hashvo);
   }
   function execombine(outparamters){
   		var msg=outparamters.getValue('msg');
   		if(msg=="equals"){
   			if(operationtype=="8"){
   			alert(ORG_ORGINFO_hebing);
   			}else if(operationtype=="9"){
   			alert(ORG_ORGINFO_INFO01);
   			}
   		}else if(msg=="ok"){
   			var name = getEncodeStr(setname);
	   		var maxstartdate=outparamters.getValue('maxstartdate');
	   		if(operationtype=="8"){
	   		var iframe_url="/general/query/common/iframe_query.jsp?src=/general/template/templatelist.do?b_combine=link`from=t`table_name="+name+"`maxstartdate="+maxstartdate+"`infor_type="+infor_type+"`tabid="+tabid;
 		 return_vo= window.showModalDialog(iframe_url, 'trainClass_win1', 
      				"dialogWidth:700px; dialogHeight:280px;resizable:no;center:yes;scroll:yes;status:no");		
	   		}else if(operationtype=="9"){
	   		var iframe_url="/general/query/common/iframe_query.jsp?src=/general/template/templatelist.do?b_transfer=link`from=t`table_name="+name+"`maxstartdate="+maxstartdate+"`infor_type="+infor_type+"`tabid="+tabid;
 		 return_vo= window.showModalDialog(iframe_url, 'trainClass_win1', 
      				"dialogWidth:700px; dialogHeight:280px;resizable:no;center:yes;scroll:yes;status:no");		
	   		}
	   		
	   		if(return_vo){
	   		var href = parent.location.href;
		 	parent.location.href=href+"&index_template=1";
		 	window.close();
	   		}
	   	//	orgInfoForm.action="/workbench/orginfo/searchorginfodata.do?b_combine=link&maxstartdate="+maxstartdate;
	    //    orgInfoForm.submit(); 
        }
         else if(msg=="date"){
        alert("你选择了有效日期为当日机构,不允许此操作!");
        }
        else{
        if(msg.length>5){
        alert(msg);
        }else
        	alert("检查能否此操作时失败，不允许此操作！");
        }
   }
 function filloutSequence(){
//		if(!isSelectedObjRecord())
//		   	    {
//		            alert(NOT_HAVE_SEQUENCE);
//		        	return;   	    
//		   	    } 	  
	   var hashvo=new ParameterSet();
      hashvo.setValue("setname",setname);
		hashvo.setValue("tabid",tabid);
		hashvo.setValue("infor_type",infor_type);
		hashvo.setValue("ins_id",ins_id/*ins_id*/);
	    hashvo.setValue("task_id",taskid);   
	    hashvo.setValue("sp_batch",sp_batch);
	    hashvo.setValue("batch_task",batch_task);
	   	var request=new Request({asynchronous:false,onSuccess:isSuccess,functionId:'0570010211'},hashvo); 
}
function reloop(){         
	var time=new Date();
	 var lg=time.getTime();
	 DocumentID=lg;
	setTimeout("reloop()",1);
            
 }	
  function validateSize()
       {
          var f_obj=document.getElementsByName("picturefile");
          if(f_obj)
          {
             var value=f_obj[0].value;            
             var photoEx=value.substring(value.lastIndexOf(".")); 
             photoEx=photoEx.toLowerCase();
             if(photoEx!=".jpg"&&photoEx!=".bmp"&&photoEx!=".jpeg"){ 
                   alert("上传文件类型不正确！仅限于"+"\".jpg\"，\" .bmp\"，\".jpeg\" 文件");
                   return false;
             }
             // 防止上传漏洞
			var isRightPath = validateUploadFilePath(value);
			if(!isRightPath)	
				return false;
             var  obj=document.getElementById('FileView'); 
             if (obj != null){
                obj.SetFileName(value);
                var facSize=obj.GetFileSize();                  
                var  photo_maxsize="${pictureReportForm.photo_maxsize}"   
                if(parseInt(photo_maxsize,10)>0&&parseInt(photo_maxsize,10)<parseInt(facSize,10)/1024)
                {  
                   
                   alert("上传文件大小超过管理员定义大小，请修正！上传文件上限"+photo_maxsize+"KB");
                   return false;
                }     
             }
          }
       }
  //组织机构树如果显示人员，则先显示人员库
	function select_org_emp_dialog2_rsjd(flag,selecttype,dbtype,priv,isfilter,loadtype,generalmessage)
	{
		 if(dbtype!=1&&dbtype!=3)
		 	dbtype=0;
		 if(priv!=0)
		    priv=1;
	     var theurl="/system/logonuser/org_employ_tree.do?b_query=link`flag="+flag+"`showDb=1`tabid="+tabid+"`selecttype="+selecttype+"`dbtype="+dbtype+
	                "`priv="+priv + "`isfilter=" + isfilter+"`loadtype="+loadtype+"`generalmessage="+generalmessage;
	     if (modeType=="23"){
		     	theurl=theurl+"`privtype=kq"
		  }
	     var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;  
		     var dialogWidth="300px";
		     var dialogHeight="400px";
		     if (isIE6()){
		     	dialogWidth="320px";
		     	dialogHeight="450px";
		     } 
		     var return_vo= window.showModalDialog(iframe_url,1, 
		        "dialogWidth:"+dialogWidth+"; dialogHeight:"+dialogHeight+";resizable:no;center:yes;scroll:no;status:no");
		 return return_vo;
	}
	
//自定义审批流程	
function showDefFlowSelf(tabid)
{
	var win=window.open("/general/template/def_flow_self.do?b_query=init"
	      +"&task_id=0&ins_id=-1&node_id=0&tabid="+tabid+"&fromflag=card"    
	      ,"_parent");
	
}
//校验大文本的长度 xyy 2015-04-01
function jugeLength(elem,length){
	var value = elem.value;
	var vaLength = elem.value.length;
	if(vaLength>length){
		alert("该文本的字数不超过"+length+"个");
		elem.value=value.substring(0,length);
	}
}

function setapp_date()
{
 	var thecodeurl ="/gz/gz_accounting/setapp_date.do?b_query=link"; 
 	
 	 var dialogWidth="397px";
     var dialogHeight="340px";
     if (isIE6()){
    	dialogWidth="410px";
    	dialogHeight="340px";
     } 
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:"+dialogWidth+"; dialogHeight:"+dialogHeight+";resizable:no;center:yes;scroll:yes;status:no");
}

//显示进度条
function displayProcessBar(){
	   disableButtons();
	  var x=(window.screen.width-700)/2;
	  var y=(window.screen.height-500)/2; 
	  var waitInfo=document.getElementById("wait");
	  if (waitInfo!=null){
		waitInfo.style.top=y;
		waitInfo.style.left=x;
	  	waitInfo.style.display="block";
	  }

}
	//关闭进度条	
   function closeProcessBar(){
      enableButtons();
	  var waitInfo=document.getElementById("wait");	 
	  if (waitInfo!=null)
	  	waitInfo.style.display="none";
}
//交易类后台报错后调用的方法
function dealOnError(outparamters)
{
    closeProcessBar()
}
	function disableButtons(){
	   var submitButton=document.getElementById("submitButton");
	   var applyButton=document.getElementById("applyButton");
	   var regectButton=document.getElementById("regectButton");
	   var abolishButton=document.getElementById("abolishButton");
	   if(submitButton){
	   submitButton.disabled="disabled";
	   }
	   if(applyButton){
		   applyButton.disabled="disabled";
	   }
	   if(regectButton){
		   regectButton.disabled="disabled";
	   }
	   if(abolishButton){
		   abolishButton.disabled="disabled";
	   }
   }
   function enableButtons(){
	   var submitButton=document.getElementById("submitButton");
		  var applyButton=document.getElementById("applyButton");
		  var regectButton=document.getElementById("regectButton");
		  var abolishButton=document.getElementById("abolishButton");
		  if(submitButton){
			   submitButton.disabled="";
		  }
		   if(applyButton){
			   applyButton.disabled="";
		   }
		   if(regectButton){
			   regectButton.disabled="";
		   }
		   if(abolishButton){
			   abolishButton.disabled="";
		   }
   }
