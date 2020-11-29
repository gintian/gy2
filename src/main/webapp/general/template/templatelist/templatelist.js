var isHeadCountControl=true; //编制控制

//是否自动将索引条件下的 数据载入模板
 function validateIndex()
 {
 	if(num!=1)
 		return;
 	if(table_name=='templet_'+tabid)
 		return;
 	if(trim(sys_filter_factor).length==0)
 		return;	
 	if(trim(operationtype)=='0')
 		return;
 	var flag='0';  //0:不引入数据  1:清空当前人员,重新引入  2:不清空,引入符合条件的数据
 	if(promptIndex_template=='true')
 	{
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
 	}
 	if(flag!='0')
 	{
 		var hashvo=new ParameterSet();   	     
 		 hashvo.setValue("tabid",tabid);
 		  hashvo.setValue("flag",flag);
 		  displayProcessBar();   
 		var request=new Request({asynchronous:true,onSuccess:issuccess_list,functionId:'0570040008'},hashvo); 
 	}
 }




 function print(id,flag,tabid)
 {
   	     var hashvo=new ParameterSet();   	     
   	     if("1"==flag)
	     {
  	    	hashvo.setValue("flag","1");
	     }
	     else
	     {
	      hashvo.setValue("flag","0");
	     }
	   
         var basepre=_basepre;
         var a0100=_a0100;          
	    hashvo.setValue("tabid",tabid/*"${templateForm.tabid}"*/);
   	    hashvo.setValue("taskid",taskid/*"${templateForm.taskid}"*/);
   	    hashvo.setValue("ins_id",ins_id/*"${templateForm.ins_id}"*/);
   	    hashvo.setValue("batch_task",tasklist_str/*"${templateForm.batch_task}"*/);
  	    hashvo.setValue("sp_flag",'1'/*"${templateForm.sp_flag}"*/);
  	    hashvo.setValue("sp_batch",sp_batch/*"${templateForm.sp_flag}"*/);
  	    hashvo.setValue("a0100",_a0100);
  	    hashvo.setValue("pre",_basepre);
  	    hashvo.setValue("id",id);  	 
  	    hashvo.setValue("infor_type",infor_type);   
	    var request=new Request({asynchronous:false,onSuccess:printsuccess,functionId:'0570010131'},hashvo); 
 }    


 function printsuccess(outparamters)
 {
   	
      var judgeisllexpr=outparamters.getValue("judgeisllexpr");
      var id=outparamters.getValue("id");
      var flag=outparamters.getValue("flag");
	  if(judgeisllexpr!="1")
	  {
	     alert(judgeisllexpr);
	  }
	  else
	  {
	     printexecute(id,flag,tabid);
	  }
 }
   	
 function printexecute(id,flag,tabid)
 {
	  var hashvo=new ParameterSet();
	  hashvo.setValue("tabid",tabid);
	  hashvo.setValue("id",id);
	  hashvo.setValue("flag",flag);
	  if("1"==flag)
	  {
	    var request=new Request({asynchronous:false,onSuccess:outPutTemplateData,functionId:'0571000001'},hashvo);  
	  }else
	  {
             excecutePDF(table_name,id);
	  }
	    
 }  
 
 function outPutTemplateData(outparamters)
 {
	    var templatefile=outparamters.getValue("templatefile");
	    var tabid=outparamters.getValue("tabid");
	   
	    var sp_batch=outparamters.getValue("sp_batch"); //"${templateForm.sp_batch}";
	    var batch_task=outparamters.getValue("batch_task");//"${templateForm.batch_task}";
	    var ins_id=outparamters.getValue("ins_id");//"${templateForm.ins_id}";
	     
        var basepre=_basepre;
        var a0100=_a0100; 
      // var win=window.open("/servlet/OutputTemplateDataServlet?templatefile="+templatefile + "&tabid=" + tabid+"&ins_id="+ins_id+"&sp_batch="+sp_batch+"&=batch_task="+batch_task+"&pre="+basepre+"&a0100="+a0100,"_blank");
       window.location.target="_blank";
	window.location.href = "/servlet/OutputTemplateDataServlet?templatefile="+templatefile + "&tabid=" + tabid+"&ins_id="+ins_id+"&taskid="+taskid+"&sp_batch="+sp_batch+"&batch_task="+tasklist_str+"&pre="+basepre+"&a0100="+a0100;
 } 
  	


 function excecutePDF(setname,tabid)
{
        var table,dataset,basepre,a0100;
        var hashvo=new ParameterSet();
        hashvo.setValue("nid",_a0100);
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
	hashvo.setValue("userbase",_basepre);      
    var In_paramters="exce=PDF";  
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showPDFcard,functionId:'07020100005'},hashvo);
}

function showPDFcard(outparamters)
{
 
    var url=outparamters.getValue("url");
    var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+url,"pdf");	
}

function submitData(setname,tabid){
	displayProcessBar();   
	setTimeout(function(){submitData_ProcessBar(setname,tabid);},100); 
}
function submitData_ProcessBar(setname,tabid)
{

		
		isSelectedObj();
   	    if(selectAll==0&&taskid!=0)
   	    {
			closeProcessBar();
		    if(infor_type=='1'){
				alert(NOT_HAVE_OBJECT);
			}else{
				alert(NOT_HAVE_ORGRECORD);
			}
        	return;   	    
   	    } else  if(selectAll==2){
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
     hashvo.setValue("sp_flag",'1'/*${templateForm.sp_flag}"*/);
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
		     var dialogWidth="650px";
		     var dialogHeight="480px";
		     if (isIE6()){
		     	dialogWidth="700px";
		     	dialogHeight="520px";
		     } 
	    	var obj_vo =window.showModalDialog(iframe_url,null,"dialogWidth="+dialogWidth+";dialogHeight="+dialogHeight+";status=no");   
	    
	    }
	  
	    isHeadCountControl=true;
   	 	validateHeadCount("0",tabid);
        validateHeadCount("0",tabid);
        if(!isHeadCountControl){
            closeProcessBar();
            return;  
        }
	    
		 var hashvo=new ParameterSet();
	     hashvo.setValue("tabid",tabid);
	     hashvo.setValue("setname",table_name);	 
	    
         var request=new Request({asynchronous:false,onSuccess:isSuccess2,onFailure:dealOnError,functionId:'0570010118'},hashvo); 
        
	   	
	  }
}




 function isSuccess2(outparamters)
 {	
 		 var msgs = outparamters.getValue("msgs");
        if(msgs!=null&&msgs!="yes"&&msgs.length>3){
            closeProcessBar();
            alert(msgs);
        }
    	var basepre=outparamters.getValue("basepre");
    	var a0100=outparamters.getValue("a0100");
    	
    	document.templateListForm.action="/general/template/templatelist.do?b_query=query&tabid="+tabid+"&a_code="+codeid;
		document.templateListForm.submit();
	    
 }
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
   var a_tabid="";
   function printActive(id,objname,setname)
   {
      if(!AxManager.setup("CardPreview1div", "CardPreview1", 0, 0, printActive, AxManager.cardpkgName))
         return;

      if(id)  // 回调时id为undefined
          a_tabid=id;       
      var cardtype=5;
      var hashvo=new ParameterSet();
      hashvo.setValue("ins_id",ins_id);
      hashvo.setValue("task_id",taskid);
      hashvo.setValue("tabid",tabid);
      hashvo.setValue("cardtype",cardtype);
      hashvo.setValue("sp_batch",sp_batch);
	  hashvo.setValue("batch_task",tasklist_str);
      hashvo.setValue("infor_type",infor_type);
      var request=new Request({asynchronous:false,onSuccess:printCard,functionId:'0570040006'},hashvo);
   }  
   
   var CardPreview1Flag = 0;
   function printCard(outparamters)
   {
       var obj_str=getDecodeStr(outparamters.getValue("a0100_str"));
       var obj_arr=obj_str.split("`");
       var basepre="usr";
         
        var card = document.getElementById("CardPreview1"); 
        var cardobj = isLoad(card);
        if(cardobj==true){
     	   CardPreview1Flag++;
     	   printCardLoadOk(card,basepre,obj_arr);
        }else{
           var timer = setInterval(function(){ 
 	          CardPreview1Flag++;
 	          card= document.getElementById('CardPreview1');  
 	          cardobj = isLoad(card);
 	      	  if(cardobj==true){
 	       		printCardLoadOk(card,basepre,obj_arr);
 	       		clearInterval(timer);
 	       	  }else if(CardPreview1Flag==5){
 	       		alert("插件加载失败！");
 	       		CardPreview1Flag=0;
 	       		clearInterval(timer);
 	       	  }    
 	       	},2000);
        }	
       /**当点击时才去加载控件
       var obj = $("CardPreview1");    
       if(obj==null)
       {
          alert("没有下载打印控件，请设置IE重新下载！");
          return false;
       }    
       obj.SetCardID(a_tabid);
       obj.SetDataFlag("1");
       obj.SetNBASE(basepre);
       obj.ClearObjs();
       for(var i=0;i<obj_arr.length;i++)
       {
          obj.AddObjId(obj_arr[i]);          
       }  
       obj.ShowCardModal();
       //obj.Modal();调用方法出错
       **/
    }
   /**卡片打印调用**/
   function printCardLoadOk(obj,basepre,obj_arr){
	   initAciveCard(hosturl,dbtype,username,userFullName,superUser,fields,tables,'CardPreview1');
	   obj.SetCardID(a_tabid);
       obj.SetDataFlag("1");
       obj.SetNBASE(basepre);
       obj.ClearObjs();
       for(var i=0;i<obj_arr.length;i++)
       {
          obj.AddObjId(obj_arr[i].dataValue);          
       }  
       try { obj.SetJSessionId(AxManager.getJSessionId()); } catch(err) {}
       obj.ShowCardModal();
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
			"dialogWidth="+screen.width+"px;dialogHeight="+screen.height+"px;resizable=yes;scroll:yes;center:yes;status=no;");
   } 

   

function payrollViewHide(tabid,flag){
	var checkview = hireView(tabid,flag);
 	if(checkview.length>0){
 		if(checkview=="ok")
 		checkview=" ";
 		document.getElementById("hiddenItem").value=checkview;
 		templateListForm.action="/general/template/templatelist.do?b_query=link&tabid="+tabid;
		templateListForm.submit();
 	} 

}

function add_newobj_list(setname,tabid)
{
        var hashvo=new ParameterSet();
		hashvo.setValue("setname",setname);
		hashvo.setValue("tabid",tabid);
		hashvo.setValue("codeid",codeid);
	   	var request=new Request({asynchronous:false,onSuccess:issuccess_list,functionId:'0570010209'},hashvo); 
}


/**
itemid 
itemdesc 
checkview 
dbname  
where 
orderby 
**/
function hireView(tabid,flag){
 	var thecodeurl ="/general/template/target_viewHide.do?b_query=link&tabid="+tabid+"&flag="+flag;	
    var dialogWidth="360px";
    var dialogHeight="500px";
    if (isIE6()){
    	dialogWidth="380px";
    	dialogHeight="530px";
    } 	
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:"+dialogWidth+"; dialogHeight:"+dialogHeight+";resizable:no;center:yes;scroll:yes;status:no");
    if(return_vo!=null){
    	return return_vo;
    }else{
    	return "";
    }
}
function payrollLock(tabid,flag){
	var checkview = lockView(tabid,flag);
 	if(checkview.length>0){
 		if(checkview=="ok")
 		checkview=" ";
 		document.getElementById("lockedItemStr").value=checkview;
 		templateListForm.action="/general/template/templatelist.do?b_query=link&tabid="+tabid;
		templateListForm.submit();
 	} 

}
function lockView(tabid,flag){
 	var thecodeurl ="/general/template/target_viewLock.do?b_query=link&tabid="+tabid+"&flag="+flag;
    var dialogWidth="360px";
    var dialogHeight="500px";
    if (isIE6()){
    	dialogWidth="380px";
    	dialogHeight="530px";
    } 			
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:"+dialogWidth+"; dialogHeight:"+dialogHeight+";resizable:no;center:yes;scroll:yes;status:no");
    if(return_vo!=null){
    	return return_vo;
    }else{
    	return "";
    }
}
function payrollSort(tabid){
	var checksort = setSorting(tabid);
 	if(checksort.length>0){
 		document.getElementById("fieldSetSortStr").value=checksort;
 		document.getElementById("lockedItemStr").value="";
 		templateListForm.action="/general/template/templatelist.do?b_query=link&tabid="+tabid;
		templateListForm.submit();
 	} 
}  
/**
排序
itemid 指标id
itemdesc 指标中文名称
dbname  所要排序的数据库表的名称
where 条件
orderby 顺序显示
**/
function setSorting(tabid){
 	var thecodeurl ="/general/template/sorting.do?b_query=link&tabid="+tabid;
    var dialogWidth="330px";
    var dialogHeight="350px";
    if (isIE6()){
    	dialogWidth="350px";
    	dialogHeight="400px";
    } 
 		
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:"+dialogWidth+"; dialogHeight:"+dialogHeight+";resizable:no;center:yes;scroll:yes;status:no");
    if(return_vo!=null){
    	return return_vo;
    }else{
    	return "";
    }
}
	function to_sort_emp1(tabid)
	{
		var thecodeurl ="/general/template/sorting.do?b_search=link&tabid="+tabid;
	    var dialogWidth="555px";
	    var dialogHeight="400px";
	    if (isIE6()){
	    	dialogWidth="555px";
	    	dialogHeight="400px";
	    } 
		var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:"+dialogWidth+"; dialogHeight:"+dialogHeight+";resizable:no;center:yes;scroll:yes;status:no");
			
    	if(return_vo!=null){
    		var orderStr = return_vo.orderStr;
	        var sortitem = return_vo.sortitem;
    		orderStr=orderStr!='not'?orderStr:"";
			document.getElementById("orderStr").value=orderStr;
			document.getElementById("sortitem").value=sortitem;
			templateListForm.action="/general/template/templatelist.do?b_query=link&tabid="+tabid;
  	    	templateListForm.submit();	
  	    		}
	}
	
function addfield(){    
	//var itemid = document.getElementById("itemid").value;
	var sortitem = document.getElementById("sortitem").value;
	sortitem=sortitem!='undefined'?sortitem:"";
	sortitem=sortitem!=null?sortitem:"";
	var id="";
	var vos= document.getElementsByName("itemid");
	if(vos==null)
  		return false;
  	var right_vo=vos[0];
  	for(i=right_vo.options.length-1;i>=0;i--){
  		if(right_vo.options[i].selected){
  			var itemid = right_vo.options[i].value;
  			var itemtext= right_vo.options[i].text;
			sortitem+=itemid+":"+itemtext+":1`";
			id = itemid;
		}
	}
	document.getElementById("sortitem").value = sortitem;
	document.getElementById("dis_sort_table").innerHTML = outTable(sortitem);
	var arr = id.split(":");
		if(arr.length==2)
			tr_bgcolor(arr[0]);
}
function outTable(sortitem){
	var tabelstr = "<table width=\"100%\" border=\"0\" class=\"ListTable1\" cellspacing=\"0\" cellpadding=\"0\" style=\"border-right:0px;border-top:0px;\">";
	tabelstr += "<tr class=\"fixedHeaderTr\" width=\"100%\"  style=\"border-right:0px;border-top:0px;\">";
	tabelstr += "<td class=\"TableRow\" width=\"10%\" align=\"left\" style=\" border-left:0px; border-top:0px;\" >&nbsp;</td>";
	tabelstr += "<td class=\"TableRow\" width=\"65%\" align=\"center\" style=\"border-top:0px;\">指标名称</td>";
	tabelstr += "<td class=\"TableRow\" width=\"25%\" align=\"center\" style=\"padding-right:0px;border-top:0px;border-right:0px;\">升降<td>";
	tabelstr += "</tr>";
	var arr = sortitem.split("`");
	if(arr.length>0){
	if(arr.length>1){
	var bt=document.getElementById("b_next");
	bt.disabled="";	
	}else{
	var bt=document.getElementById("b_next");
	bt.disabled="disabled";	
	}
		var n=1;
		for(var i=0;i<arr.length;i++){
			var arr_item = arr[i].split(":");
			if(arr_item.length==3){
				tabelstr+="<tr>";
				tabelstr+="<td align=\"center\" class=\"RecordRow\" style=\" border-left:0px; border-top:0px;\" onclick=\"tr_bgcolor('";
				tabelstr+=arr_item[0]+"')\" ondblclick=\"deletefield();\" nowrap>";
				tabelstr+=n+"</td>";
				tabelstr+="<td class=\"RecordRow\" style=\"border-top:0px;\" onclick=\"tr_bgcolor('";
				tabelstr+=arr_item[0]+"')\" ondblclick=\"deletefield();\" nowrap>";
				tabelstr+=arr_item[1]+"</td>";
				tabelstr+="<td align=\"left\" class=\"RecordRow\" style=\"padding-left:3px;padding-right:0px;border-top:0px;border-right:0px;\" onclick=\"tr_bgcolor('";
				tabelstr+=arr_item[0]+"')\" ondblclick=\"deletefield();\" nowrap>";
				tabelstr+="<select name="+arr_item[0];
				tabelstr+=" onchange=\"viewHide(this,'"+arr_item[0]+"');\">";
				if(arr_item[2]==1){
					tabelstr+="<option value=1 selected>升序</option>";
					tabelstr+="<option value=0>降序</option>";
				}else{
					tabelstr+="<option value=1>升序</option>";
					tabelstr+="<option value=0 selected>降序</option>";
				}
				tabelstr+="</select>";
				tabelstr+="</td></tr>";
				n++;
			}
		}
	}	
	tabelstr+="</table>";	
	return tabelstr;
}
function tr_bgcolor(itemid){
	var tablevos=document.getElementsByTagName("select");
	for(var i=0;i<tablevos.length;i++){
	    var cvalue = tablevos[i];
	    var td = cvalue.parentNode.parentNode;
	    td.style.backgroundColor = '';
   	}
	var c = document.getElementById(itemid);
	var tr = c.parentNode.parentNode;
	if(tr.style.backgroundColor!=''){
		tr.style.backgroundColor = '' ;
	}else{
		tr.style.backgroundColor = '#fff8d2' ;
	}
	document.getElementById("sortitemid").value=itemid;
}
function deletefield(){
	var sortitem = document.getElementById("sortitem").value;
	var sortitemid = document.getElementById("sortitemid").value;
	var arr = sortitem.split("`");
	var item="";
	var id="";
	var n=0;
	var bid="";
	for(var i=0;i<arr.length;i++){
		var arr_item = arr[i].split(":");
		if(arr_item.length==3){
			if(arr_item[0].toLowerCase()!=sortitemid.toLowerCase()){
			
				item+=arr[i]+"`";
				bid = arr_item[0];
				if(n==1){
					id=arr_item[0];
					n=0;
				}
			}else{
				var itemid=arr_item[0];
				additemright("itemid",itemid,arr_item[1]);
				n=1;
			}
		}
	}
	if(id==null||id.length<1)
		id = bid;
	document.getElementById("sortitemid").value=id;
	item=item!=null?item:"";
	document.getElementById("sortitem").value = item;
	document.getElementById("dis_sort_table").innerHTML = outTable(item);
	if(id!=null&&id.length>0)
		tr_bgcolor(id);
}
function additemright(sourcebox_id,itemid,itemdesc){
	var left_vo,vos,i,flag;
	vos= document.getElementsByName(sourcebox_id);
	if(vos==null)
		return false;
	left_vo=vos[0];
	  for(i=0;i<left_vo.options.length;i++)
		  {
		    	left_vo.options[i].value
		    	if(left_vo.options[i].value.toLowerCase()!=itemid.toLowerCase()){
		    	}else{
		    	flag=2;
		    	break;
		    	}
  		  }
  if(flag!=2){
	var no = new Option();
	no.value=itemid;
	no.text=itemdesc;
	left_vo.options[left_vo.options.length]=no;
	}
}
function viewHide(obj,itemid){
	var sortitem = document.getElementById("sortitem").value;
	var arr = sortitem.split("`");
	var item="";
	for(var i=0;i<arr.length;i++){
		var arr_item = arr[i].split(":");
		if(arr_item.length==3){
			if(arr_item[0].toLowerCase()==itemid.toLowerCase()){
				item+=arr_item[0]+":"+arr_item[1]+":"+obj.value+"`";
			}else{
				item+=arr[i]+"`";
			}
		}
	}
	document.getElementById("sortitem").value = item;
}
function upSort(){
	var sortitem = document.getElementById("sortitem").value;
	var sortitemid = document.getElementById("sortitemid").value;
	if(sortitemid==null||sortitemid.length<1)
		return false;
	
	var arr = sortitem.split("`");
	var item="";
	var n=0;
	for(var i=0;i<arr.length;i++){
		var arr_item = arr[i].split(":");
		if(arr_item.length==3){
			if(arr_item[0].toLowerCase()==sortitemid.toLowerCase()){
				n=i;
				break;
			}
		}
	}
	if(n>0){
		var sortitem = arr[n];
		arr[n]=arr[n-1];
		arr[n-1]=sortitem;
	}
	for(var i=0;i<arr.length;i++){
		if(arr[i].length>0)
			item+=arr[i]+"`";
	}
	item=item!=null?item:"";
	document.getElementById("sortitem").value = item;
	document.getElementById("dis_sort_table").innerHTML = outTable(item);
	tr_bgcolor(sortitemid);
}
function downSort(){
	var sortitem = document.getElementById("sortitem").value;
	var sortitemid = document.getElementById("sortitemid").value;
	if(sortitemid==null||sortitemid.length<1)
		return false;
	var arr = sortitem.split("`");
	var item="";
	var n=-1;
	for(var i=0;i<arr.length;i++){
		var arr_item = arr[i].split(":");
		if(arr_item.length==3){
			if(arr_item[0].toLowerCase()==sortitemid.toLowerCase()){
				n=i;
				break;
			}
		}
	}
	if(n>=0&&n<arr.length-1){
		var sortitem = arr[n];
		arr[n]=arr[n+1];
		arr[n+1]=sortitem;
	}
	for(var i=0;i<arr.length;i++){
		if(arr[i].length>0)
			item+=arr[i]+"`";
	}
	item=item!=null?item:"";
	document.getElementById("sortitem").value = item;
	document.getElementById("dis_sort_table").innerHTML = outTable(item);
	tr_bgcolor(sortitemid);
}
function sub(){
	var sortitem = document.getElementById("sortitem").value;
	if(sortitem!=null&&sortitem.length>0){
	var arr = sortitem.split("`");
	var sortstr="";
	for(var i=0;i<arr.length;i++){
		var arr_item = arr[i].split(":");
		if(arr_item.length==3){
		sortstr+= arr_item[0];
		if(arr_item[2]=="1"){
		sortstr+= " asc";
		}else{
		sortstr+= " desc";
		}
		sortstr+= ",";	
		}
		}
		if(sortstr.length>0){
		sortstr="order by "+sortstr;	
		sortstr = sortstr.substring(0,sortstr.length-1);
		}
			 var person_vo = new Object();
		    person_vo.orderStr = sortstr;
		    person_vo.sortitem = sortitem;
		window.returnValue=person_vo;
  		window.close();
  	}else{
  	 		var person_vo = new Object();
		    person_vo.orderStr = "not";
		    person_vo.sortitem = "";
  		window.returnValue=person_vo;
  		window.close();
  	}
}
function defField(){
	var sortitem = document.getElementById("sortitem").value;
	sortitem=sortitem!='undefined'?sortitem:"";
	sortitem=sortitem!=null?sortitem:"";
	document.getElementById("dis_sort_table").innerHTML = outTable(sortitem);
	var arr = sortitem.split("`");
	if(arr!=null&&arr.length>0){
		var arr_item = arr[0].split(":");
		if(arr_item.length==3){
			tr_bgcolor(arr_item[0]);
		}
	}
}
function to_person_filter(tabid)
	{
		sql_str=bankdisk_personFilter(tabid,1);
		if(sql_str==null||trim(sql_str).length==0)
		{
		//    bankdisk_changeCondList(tabid);
		}
		else
		{
	//	templateListForm.cond_id_str.value=cond_id_str;
	  	document.getElementById("filterStr").value=sql_str;
	 	templateListForm.action="/general/template/templatelist.do?b_query=link&tabid="+tabid+"&pagecurent=1";
  	    templateListForm.submit();		
		}
	}
	function bankdisk_personFilter(tabid,type){

	var theURL="/general/template/personFilter.do?b_select=select"+"`tabid="+tabid;
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
	function bankdisk_changeCondList(tabid)
	{
		 var hashVo=new ParameterSet();
		 hashVo.setValue("isclose","2");
		 hashVo.setValue("tabid",tabid);
		 //var In_parameters="opt=1";
		 var request=new Request({method:'post',asynchronous:false,onSuccess:change_condlist_ok,functionId:'3020100017'},hashVo);			
		    
	}	

	function delete_cond(tabid)
{
   var theUrl="/general/template/personFilter/delete_filter_cond.do?b_query=query`tabid="+tabid;
   var url="/general/query/common/iframe_query.jsp?src="+theUrl;
   var objlist =window.showModalDialog(url,null,"dialogWidth=300px;dialogHeight=500px;resizable=yes;status=no;");  
   if(objlist==null)
   {
      return;
   }
   var obj= new Object();
   obj.ids=objlist.ids;
   obj.type=objlist.type;
   if(parseInt(obj.type)==2)//open
   {
       templateListForm.action="/gz/gz_accounting/bankdisk/delete_filter_cond.do?b_open=open&tabid="+tabid+"&condid="+obj.ids;
       templateListForm.submit();
   }			
 }
 function bankdisk_choose(str)
{
    var rightFiledIDs="";
	//	var rightFieldNames="";
   var rightFields=$('right_fields')
		if(rightFields.options.length==0)
		{
			 return;
	    	
		}
		for(var i=0;i<rightFields.options.length;i++)
		{
			rightFiledIDs+=","+rightFields.options[i].value;
		}
	templateListForm.rightFields.value=rightFiledIDs.substring(1);
	templateListForm.action="/general/template/personFilter.do?b_query=query";
	templateListForm.submit();
}
//批量修改（单指标）
function batch_update(tabid)
{
	var thecodeurl="/general/template/batchupdate.do?b_query=link`taskid="+taskid+"`tabid="+tabid+"`sp_batch="+sp_batch;; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
//	var obj=eval("document.accountingForm.filterWhl");
    var dialogWidth="500px";
    var dialogHeight="520px";
    if (isIE6()){
    	dialogWidth="520px";
    	dialogHeight="570px";
    } 
	var retvo= window.showModalDialog(iframe_url,"", 
		        "dialogWidth:"+dialogWidth+"; dialogHeight:"+dialogHeight+";resizable:no;center:yes;scroll:yes;status:no");			
  	 if(retvo)
	{
	
	   var formula=retvo.formula;
	   var cond=retvo.conditions;
	   var itemid=retvo.itemid;
	   var selchecked=retvo.selchecked;
	   if(retvo.formula.length==0)
		{
		  formula="NULL";
		}
		formula=getEncodeStr(formula);
		var hashvo=new ParameterSet();
		hashvo.setValue("table_name",table_name);
		hashvo.setValue("task_id",taskid);
		hashvo.setValue("itemid",itemid);		
		hashvo.setValue("formula",formula);		
		hashvo.setValue("cond",cond);	
		hashvo.setValue("selchecked",selchecked);
		hashvo.setValue("tabid",tabid);
		hashvo.setValue("whl",document.templateListForm.filterStr.value);
		//js不允许加载sql相关参数,这里去掉needcondition
		hashvo.setValue("sp_batch",sp_batch);
		hashvo.setValue("batch_task",tasklist_str);
	  	var request=new Request({method:'post',asynchronous:true,onSuccess:issuccess_list,functionId:'0570040007'},hashvo);


	}
}

//批量修改（多指标）
function batch_update_fields(tabid)
{
	var thecodeurl="/general/template/batchupdatefields.do?b_query=link`taskid="+taskid+"`tabid="+tabid+"`sp_batch="+sp_batch;;
	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
//	var obj=eval("document.accountingForm.filterWhl");
	
    var dialogWidth="500px";
    var dialogHeight="520px";
    if (isIE6()){
    	dialogWidth="550px";
    	dialogHeight="600px";
    } 
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
		hashvo.setValue("table_name",table_name);
		hashvo.setValue("task_id",taskid);
		hashvo.setValue("selchecked",selchecked);
		hashvo.setValue("tabid",tabid);
		hashvo.setValue("sp_batch",sp_batch);
		hashvo.setValue("batch_task",tasklist_str);
		hashvo.setValue("fielditem_array",fielditem_array);
		hashvo.setValue("fieldvalue_array",fieldvalue_array);
		hashvo.setValue("fieldtype_array",fieldtype_array);
	  	var request=new Request({method:'post',asynchronous:false,onSuccess:issuccess_list,functionId:'0570040009'},hashvo);
	 }
}

function symbol(editor,strexpr){
	document.getElementById(editor).focus();
	var element = document.selection;
	if (element!=null) {
		var rge = element.createRange();
		if (rge!=null)	
		rge.text=strexpr;
	}
}
function showOrClose()
{
		var obj=eval("aa");
	    var obj3=eval("vieworhidd");
		//var obj2=eval("document.browseForm.isShowCondition");
		var obj4=eval("page");
		
//		pageHeight=pageHeight+obj.offsetHeight;
	    if(obj.style.display=='none')
	    {
    		obj.style.display='block'
        	obj3.innerHTML="<a href=\"javascript:showOrClose();\"> 查询隐藏 </a>";
    	}
    	else
	    {
	    	obj.style.display='none';
	    	obj3.innerHTML="<a href=\"javascript:showOrClose();\"> 查询显示 </a>";
	    	
    	}
    	obj4.style.posTop=pageHeight-30+obj.offsetHeight;
}
function selectCheckBox(obj,hiddname)
{
   if(obj.checked==true)
   {
      var vo=document.getElementById(hiddname);
      var Info=eval("info_cue1");	
	   Info.style.display="block";
      if(vo)
         vo.value="1";
   }else
   {
         var vo=document.getElementById(hiddname);
         var Info=eval("info_cue1");	
	   Info.style.display="none";
      if(vo)
         vo.value="0";
   }

}
function fieldCheckBox(hiddenname,id,obj)
{
 //  if(obj.checked==true)//obj 一直为document.getElementsByName('xxx')[0]);这个判断无意义
 //  {
  //    var vo=document.getElementById(hiddenname);
  //    var iv=obj.value;
  //    var value=vo.value;
  //    value="`"+value+"`";
   //   if(value.indexOf("`"+iv+"`")==-1)
  //    {
  //       vo.value=vo.value+"`"+iv;
  //    }
  //    alert(vo.value);
 //  }else
   {
      var vo=document.getElementById(hiddenname);
      var voID=document.getElementsByName(id);      
      var len=voID.length;    
      var value="";
      for (i=0;i<len;i++)
      {
         if(voID[i].checked)
          {
             
            value=value+"`"+voID[i].value;
          }
       }
       vo.value=value;
   }
}
function query(tabid,query)
{
	var hashvo=new ParameterSet();
	hashvo.setValue("resolveby", "xcs");
	hashvo.setValue("check","check");
	hashvo.setValue("tabid", tabid);
	hashvo.setValue("query", query);
	var request=new Request({method:'post',asynchronous:false,onSuccess:SuccessQuery,functionId:'0570040010'},hashvo);

}
function SuccessQuery(outparamters){
	var tabid=outparamters.getValue("tabid");
	var query=outparamters.getValue("query");
	if(query=="1"){//直接点查询
	   document.templateListForm.action="/general/template/personFilterResult.do?b_search=link&tabid="+tabid+"&query=1&pagecurent=1";
	  document.templateListForm.submit();
	   }else{
	   to_person_filter(tabid)
	   }
}
//导出摸板
function downLoadTemp(tabid)
{	
	var hashvo=new ParameterSet();	
	hashvo.setValue("tabid",tabid);
	hashvo.setValue("table_name",document.templateListForm.table_name.value);  
	hashvo.setValue("sqlStr",getEncodeStr(document.templateListForm.filterStr.value));
	hashvo.setValue("tasklist_str",getEncodeStr(document.templateListForm.tasklist_str.value));
	hashvo.setValue("codeid",document.templateListForm.codeid.value);
	hashvo.setValue("orderStr",getEncodeStr(document.templateListForm.orderStr.value));
	hashvo.setValue("operationtype",document.templateListForm.operationtype.value);
	//js不允许使用sql相关数据这里去掉了needcondition,以及hmuster_sql
	hashvo.setValue("infor_type",infor_type);
	hashvo.setValue("hiddenItem",document.templateListForm.hiddenItem.value);
	var request=new Request({method:'post',asynchronous:false,onSuccess:showfile1,functionId:'0570040038'},hashvo);
}
function showfile1(outparamters)
{
	var outName=outparamters.getValue("outName");
	//outName=getDecodeStr(outName);采用统一的要求，进行任意文件下载漏洞处理
	//var name=outName.substring(0,outName.length-1)+".xls";
	//name=getEncodeStr(name);
	window.location.target="_blank";
//	window.location.href = "/servlet/DisplayOleContent?filename="+outName;
	//20/3/18 xus vfs改造
	window.location.href = "/servlet/vfsservlet?fileid="+outName+"&fromjavafolder=true";
	//var win=open("/servlet/DisplayOleContent?filename="+name,"excel");	
}
function exportTempData(tabid)
{
	window.location.href="/general/template/templatelist.do?br_import=init&tabid="+tabid+"&importTempl=1";
	//document.templateListForm.action="/general/template/templatelist.do?br_import=init&tabid="+tabid+"&importTempl=1";
  	//document.templateListForm.submit();
}
function showHistory(tabid){
 parent.location="/general/template/historydata.do?b_query=link&tabid="+tabid+"";
         
}
function showHistory2(tabid,type,res_flag){	
	if(returnflag=="8"||returnflag=="7"||returnflag=="6"){
	parent.location="/general/template/historydata/search_bs_tree.do?b_query=link&type="+type+"&res_flag="+res_flag+"&tabidtemp="+tabid+"&tabid="+tabid+"&module=20&history=2";
	}else{
	 parent.parent.location="/general/template/historydata/search_bs_tree.do?b_query=link&type="+type+"&res_flag="+res_flag+"&tabidtemp="+tabid+"&tabid="+tabid+"&module=20&history=2";
	    }     
}
function deletehistorydata(){
	var dd=false;
	var  obj = document.getElementsByName("ids");
	for(var i=0;i<document.templateHistorydataForm.elements.length;i++)
	{			
	   		if(document.templateHistorydataForm.elements[i].type=='checkbox'&&document.templateHistorydataForm.elements[i].name!="selbox")       
	   			{	
			  		if(document.templateHistorydataForm.elements[i].checked)
			  			{
			  				dd=true;
							break;
						}
				}
	}

		if(!dd)
		{
			alert("请选择要删除的数据!")
		}
		else
		{
			if(confirm('确认删除选择的历史数据吗？'))
			{
				templateHistorydataForm.action="/general/template/historydata.do?b_delete=link";
				templateHistorydataForm.submit();
			}
		}

}
//人事异动历史记录删除方法
function deletehistorydata2(name){
	var dd=false;
	var  obj = document.getElementsByName("ids");
	for(var i=0;i<document.templateHistorydataForm.elements.length;i++)
	{			
	   		if(document.templateHistorydataForm.elements[i].type=='checkbox'&&document.templateHistorydataForm.elements[i].name!="selbox")       
	   			{	
			  		if(document.templateHistorydataForm.elements[i].checked)
			  			{
			  				dd=true;
							break;
						}
				}
	}
		if(!dd)
		{
			alert("请选择要删除的数据!")
		}
		else
		{
			if(confirm('确认删除选择的历史数据吗？'))
			{
				var inputList,typeanme,objname,ids;	
				var idstring='';
				inputList=document.getElementsByTagName('INPUT');
				ids=document.getElementsByName("ids");
				
				for(var i=0;i<inputList.length;i++)
				{
					 typeanme=inputList[i].type.toLowerCase();
				     if(typeanme!="checkbox")
				        continue;	  
				     if(inputList[i].disabled)
				     	continue;
				     objname=inputList[i].name;
				     if(!objname.match(name))
				        continue;
				     if(inputList[i].checked)
				     {
				     	idstring+=ids[inputList[i].value-1].value+",";
				     }
				}
				templateHistorydataForm.action="/general/template/historydata.do?b_delete=link&ids="+idstring;
				templateHistorydataForm.submit();
			}
		}

}

function queryHistoryData()
{
	var startdate =document.getElementsByName("startdate")[0].value;
	var appDate =document.getElementsByName("appDate")[0].value;
	
	if(trim(startdate).length>0)
	{
		if(!validate(document.getElementsByName("startdate")[0],"起始日期"))
   			return;
	}
	if(trim(appDate).length>0)
	{
		if(!validate(document.getElementsByName("appDate")[0],"结束日期"))
   			return;
	}
	templateHistorydataForm.action="/general/template/historydata.do?b_search=link&type=4&startdate="+trim(startdate)+"&appDate="+trim(appDate);
	templateHistorydataForm.submit();
}




function queryhistory()
{
var type=document.getElementsByName("p3")[0].value;
var obj=document.getElementById("timescope");
if(type==4){
	 obj.style.display="block";
  	 /*
  	 var iframe_url="/general/query/common/iframe_query.jsp?src=/general/template/historydata.do?br_query2=link";
 		 return_vo= window.showModalDialog(iframe_url, 'trainClass_win1', 
      				"dialogWidth:350px; dialogHeight:260px;resizable:no;center:yes;scroll:yes;status:yes");		
	  if(return_vo){
	  var startdate =return_vo.startdate;
	  var appDate = return_vo.appDate;
	  */
	  
	  /*
	  var startdate =document.getElementsByName("startdate")[0].value;
	  var appDate =document.getElementsByName("appDate")[0].value;
	  templateHistorydataForm.action="/general/template/historydata.do?b_search=link&type="+type+"&startdate="+startdate+"&appDate="+appDate;
	  templateHistorydataForm.submit();
	  */
	//  }
}
else{
	obj.style.display="none";
    templateHistorydataForm.action="/general/template/historydata.do?b_search=link&type="+type;
	templateHistorydataForm.submit();
	}
}
function downLoadHistory(tabid,name){
	var inputList,typeanme,objname,ids;	
	var idstring='';
	inputList=document.getElementsByTagName('INPUT');
	ids=document.getElementsByName("ids");
	
	for(var i=0;i<inputList.length;i++)
	{
		 typeanme=inputList[i].type.toLowerCase();
	     if(typeanme!="checkbox")
	        continue;	  
	     if(inputList[i].disabled)
	     	continue;
	     objname=inputList[i].name;
	     if(!objname.match(name))
	        continue;
	     if(inputList[i].checked)
	     {
	     	idstring+="'"+ids[inputList[i].value-1].value+"',";
	     }
	}
	var hashvo=new ParameterSet();	
	hashvo.setValue("tabid",tabid);
	hashvo.setValue("table_name",table_name);
	hashvo.setValue("needcondition",condition);
	//hashvo.setValue("sqlStr",getEncodeStr(document.templateListForm.filterStr.value));
	//hashvo.setValue("tasklist_str",getEncodeStr(document.templateListForm.tasklist_str.value));
	hashvo.setValue("codeid",codeid);
	//hashvo.setValue("orderStr",getEncodeStr(document.templateListForm.orderStr.value));
	//hashvo.setValue("operationtype",document.templateListForm.operationtype.value);
	hashvo.setValue("ids",idstring);
	//js不允许使用sql相关参数,这里去掉了needcondition
	var request=new Request({method:'post',asynchronous:false,onSuccess:showfile2,functionId:'0570040043'},hashvo);
}
function showfile2(outparamters)
{
	var outName=outparamters.getValue("outName");
	//outName=getDecodeStr(outName);
	//var name=outName.substring(0,outName.length-1)+".xls";
	//name=getEncodeStr(name);
	window.location.target="_blank";
//	window.location.href = "/servlet/DisplayOleContent?filename="+outName;
	//20/3/18 xus vfs改造
	window.location.href = "/servlet/vfsservlet?fileid="+outName+"&fromjavafolder=true";
	//var win=open("/servlet/DisplayOleContent?filename="+name,"excel");	
}
function showhistoryPdf(id){

window.open("/servlet/HistoryDownLoad?id="+id,"_blank");
}
function returntemplist(isopen_1,type,res_flag){
  if(returnflag=="7")//返回4.0首页界面
	       {
	          var win=window.open("/system/home.do?b_query=link","_parent");
				return;   
	       }    
   if(returnflag=="8")//返回5.0首页界面
	       {
	          var win=window.open("/templates/index/portal.do?b_query=link","_parent");
			  return;   
	       } 
	        if(returnflag=="3")
	       {
	          var win=window.open("/general/template/task_desktop.do?b_query=link","_parent");
			  return;   
	       }   
	        if(returnflag=="6")
	       {
	          var win=window.open("/general/template/myapply/busidesktop.do?br_query=link","_parent");
			  return;   
	       }  
	if(isopen_1=="true"){
	if(type=="21"||type=="12"){
	parent.location="/general/template/search_bs_tree.do?b_query=link&type="+type+"&res_flag="+res_flag+"&module=15";
	}else{
	parent.location="/general/template/search_bs_sort.do?b_search=link&type="+type+"&res_flag="+res_flag+"&module=20";
	}
	}else{
	parent.location="/general/template/search_bs_tree.do?b_query=link&type="+type+"&res_flag="+res_flag+"&module=20";
	}

}
function combine()
   {
   		
       
      	   var hashvo=new ParameterSet();          
      	   hashvo.setValue("infor_type", infor_type);
      	   hashvo.setValue("table_name", table_name);
      	     hashvo.setValue("operationtype", operationtype);
      	     hashvo.setValue("tabid", tabid);
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
   			var name = getEncodeStr(table_name);
	   		var maxstartdate=outparamters.getValue('maxstartdate');
	   		if(operationtype=="8"){
	   		var iframe_url="/general/query/common/iframe_query.jsp?src=/general/template/templatelist.do?b_combine=link`maxstartdate="+maxstartdate+"`infor_type="+infor_type+"`table_name="+name+"`tabid="+tabid;
 		 return_vo= window.showModalDialog(iframe_url, 'trainClass_win1', 
      				"dialogWidth:700px; dialogHeight:280px;resizable:no;center:yes;scroll:yes;status:no");		
	   		}else if(operationtype=="9"){
	   		var iframe_url="/general/query/common/iframe_query.jsp?src=/general/template/templatelist.do?b_transfer=link`maxstartdate="+maxstartdate+"`infor_type="+infor_type+"`table_name="+name+"`tabid="+tabid;
 		 return_vo= window.showModalDialog(iframe_url, 'trainClass_win1', 
      				"dialogWidth:700px; dialogHeight:280px;resizable:no;center:yes;scroll:yes;status:no");		
	   		}
	   		
	   		if(return_vo){
	   		var href = parent.location.href;
		 	parent.location.href=href;
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
   
//////////////////////  dengcan////////////////////////////////
		
	function showSubTable(table_name,a0100,basepre,isAppealTable,seqnum,columnName,sub_domain,obj)
	{
		this.srcobj=obj;
		var hashvo=new ParameterSet();
		hashvo.setValue("tabid",tabid);
		hashvo.setValue("table_name",table_name);
		hashvo.setValue("a0100",a0100);
		hashvo.setValue("basepre",basepre);
		hashvo.setValue("isAppealTable",isAppealTable); 
		hashvo.setValue("seqnum",seqnum); 
		hashvo.setValue("columnName",columnName); 
		hashvo.setValue("sub_domain",sub_domain); 
	    var request=new Request({method:'post',asynchronous:false,onSuccess:showSubTableView,functionId:'0570040002'},hashvo);
	 	
	}
	
	function showAffixfileTable(taskid,isAppealTable,obj)
	{
		this.srcobj=obj;
		var hashvo=new ParameterSet();
		hashvo.setValue("taskid",taskid); 
		hashvo.setValue("tabid",tabid);
		hashvo.setValue("isAppealTable",isAppealTable); 
	    var request=new Request({method:'post',asynchronous:false,onSuccess:showSubTableView2,functionId:'0570040002'},hashvo);
	 	
	}
	
	function selectAllRecord()
	{
		var hashvo=new ParameterSet();
		hashvo.setValue("tabid",tabid);
		hashvo.setValue("table_name",table_name);
		hashvo.setValue("filterStr",document.templateListForm.filterStr.value);
		hashvo.setValue("codeid",codeid);
		if(document.templateListForm.isSelectAll.value=='0')
			hashvo.setValue("isSelectAll","1");
		else
			hashvo.setValue("isSelectAll","0");
		hashvo.setValue("tasklist_str",tasklist_str);
		hashvo.setValue("operationtype",operationtype);
		var request=new Request({method:'post',asynchronous:false,onSuccess:selectSuccess,functionId:'0570040003'},hashvo);
	}
	
	function selectSuccess()
	{
		if(document.templateListForm.isSelectAll.value=='0')
			document.templateListForm.isSelectAll.value="1";
		else
			document.templateListForm.isSelectAll.value="0";
		document.templateListForm.action="/general/template/templatelist.do?b_query=query&selectAll=1&tabid="+tabid+"&a_code="+codeid;
		document.templateListForm.submit();
	}
	
	function showSubTableView2(outparamters)
	{
		var tableHtml=getDecodeStr(outparamters.getValue("tableHtml"));
		Element.show('date_panel2');   
		var pos=getAbsPosition(srcobj);
		with($('date_panel2'))
		{
				
		        style.position="absolute";
		        if((pos[0]+650)<document.body.scrollWidth)
		       		style.posLeft=pos[0]+20;
		        else
			        style.posLeft=pos[0]-480;
			       
			    
			    if((pos[1]+500)<window.screen.availHeight)
		       		style.posTop=pos[1]+20;
		        else
			        style.posTop=pos[1]-200;    
	    }  
	    date_panel2.innerHTML=tableHtml;
	    //date_panel2.focus();//BUG号：16690,gaohy
	}
	
	function showSubTableView(outparamters)
	{
		var tableHtml=getDecodeStr(outparamters.getValue("tableHtml"));
		Element.show('date_panel');   
		var pos=getAbsPosition(srcobj);
		with($('date_panel'))
		{
		//有window.screen.availWidth 改为document.body.scrollWidth 650改为500
		        style.position="absolute";
		        if((pos[0]+500)<document.body.scrollWidth)
		       		style.posLeft=pos[0]+20;
		        else
			        style.posLeft=pos[0]-480;
			       
			    
			    if((pos[1]+500)<window.screen.availHeight)
		       		style.posTop=pos[1]+20;
		        else
			        style.posTop=pos[1]-200;    
	    }  
	    date_panel.innerHTML=tableHtml;
	    date_panel.focus();
	}
	
	function setObjectStatelist(chkobj,basepre,a0100,ins_id,setname,seqnum,task_id)
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
	        hashvo.setValue("seqnum",seqnum);	 
	        hashvo.setValue("task_id",task_id);	
	        hashvo.setValue("submitflag",submitflag);
	        hashvo.setValue("infor_type",infor_type);
	        hashvo.setValue("sp_batch",sp_batch);
	        //hashvo.setValue("FrameForm_templateListForm","templateListForm");
	        formparamters="FrameForm_templateListForm=templateListForm"; 
	   	    var request=new Request({asynchronous:false,parameters:formparamters,functionId:'0570010134'},hashvo);   
	}
	
	//是否对照显示	
	function setShowModel()
	{
		if(document.templateListForm.isCompare.value=='0')
			document.templateListForm.isCompare.value="1";
		else
			document.templateListForm.isCompare.value="0";
		var hashvo=new ParameterSet();
		hashvo.setValue("resolveby", "xcs");
		var request=new Request({method:'post',asynchronous:false,onSuccess:SuccessSetShowModel,functionId:'0570040010'},hashvo);
		
	}
	/**无法解决人事异动中对照，以及查询超时提示页面500错误的解决办法，采用ajax形式强制解决**/
	function SuccessSetShowModel(){
		document.templateListForm.action="/general/template/templatelist.do?b_query=query&tabid="+tabid+"&a_code="+codeid;
		document.templateListForm.submit();
	}
	
	
   	//因为进度条的原因，必须有延迟才能显示进度条
   	function apply_list(setname,sp_mode){   	
   		var no_sp_yj=document.getElementById("no_sp_yj").value;
 	    if (sp_mode!="1" && no_sp_yj==1){
	        	displayProcessBar();
	        	setTimeout(function(){apply_listWithProgress(setname,sp_mode)},100);   
	    }
	    else {
      	disableButtons();
        setTimeout(function(){apply_listWithProgress(setname,sp_mode);closeProcessBar();},100);  
	    } 	
   	}
   	
	function apply_listWithProgress(setname,sp_mode)
   	{
   			isSelectedObj();
   	   	    if(selectAll==0&&taskid!=0)
	   	    {
   	   	    	closeProcessBar();  
   	   	    	if(infor_type=='1'){
		            	alert(NOT_HAVE_OBJECT);
		            }else{
		              	alert(NOT_HAVE_ORGRECORD);
		            }
	        	return;   	    
	   	    } else  if(selectAll==2){
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
        
  	        var hashvo=new ParameterSet();
   	        hashvo.setValue("tabid",tabid);
   	        hashvo.setValue("taskid",taskid);
   	        hashvo.setValue("ins_id",ins_id);
  			hashvo.setValue("sp_flag",'1');
  			hashvo.setValue("flag","1");
  	        hashvo.setValue("a0100","a0100");
  	        hashvo.setValue("pre","pre");
  	        hashvo.setValue("id","id");
  	        hashvo.setValue("infor_type",infor_type);   
	        var request=new Request({asynchronous:false,onSuccess:applysuccess_list,onFailure:dealOnError,functionId:'0570010131'},hashvo); 
	   	
   	}    

   	function applysuccess_list(outparamters)
   	{
      var judgeisllexpr=outparamters.getValue("judgeisllexpr");
      
	  if(judgeisllexpr!="1"){
		     closeProcessBar();
		     alert(judgeisllexpr);
	  }
	  else
	  {
	     applyexecute_list(sp_mode);
	  }
   	}

	
  
   	 	/*发出申请*/
	function applyexecute_list(sp_mode)
	{
		  var no_sp_yj =document.getElementById("no_sp_yj").value;
		  if(sp_mode=="1")//等于1手工指派，弹出窗口
		  {
			  var theurl="/general/template/submit_form.do?b_apply=link&task_id="+taskid+"&sp_batch="+sp_batch+"&tabid="+tabid+"&noObject=0";
//			  alert(allow_def_flow_self);
			  if(no_sp_yj==1){
				  theurl="/general/template/submit_form.do?b_applynosp=link&task_id="+taskid+"&sp_batch="+sp_batch+"&tabid="+tabid+"&noObject=0&allow_def_flow_self="+allow_def_flow_self;
			  }else{
				  if(allow_def_flow_self=='true')
					  theurl="/general/template/submit_form.do?b_apply=link&task_id="+taskid+"&sp_batch="+sp_batch+"&tabid="+tabid+"&noObject=1";
			  }
			var obj_vo;
			if(no_sp_yj==1){
				obj_vo=window.showModalDialog(theurl,null,"dialogWidth=500px;dialogHeight=250px;resizable:no;center:yes;scroll:yes;status:no");
			}else{
			     var dialogWidth="650px";
			     var dialogHeight="600px";
			     if (isIE6()){
			     	dialogWidth="700px";
			     	dialogHeight="630px";
			     } 
					obj_vo =window.showModalDialog(theurl,null,"dialogWidth="+dialogWidth+";dialogHeight="+dialogHeight+";resizable:no;center:yes;scroll:yes;status:no");
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
			    
			    
			    
	        	var hashvo=new ParameterSet();
	        	hashvo.setValue("actor",param);
	       		hashvo.setValue("tabid",tabid);        	
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
	       		
		   	 //   var request=new Request({asynchronous:false,onSuccess:issuccess_list_apply,functionId:'0570010108'},hashvo); 
			}
		  }
		  else
		  {
		   //     if(iftqsp())
		        {
		        		var hashvo=new ParameterSet();
		        	    hashvo.setValue("task_id",taskid);
		        		hashvo.setValue("tabid",tabid);
		        		hashvo.setValue("ins_id",ins_id);
		        		hashvo.setValue("sp_mode",sp_mode);
		        		hashvo.setValue("sp_batch",sp_batch);
		        	    hashvo.setValue("batch_task",tasklist_str);
		        		 var request=new Request({asynchronous:false,onSuccess:getNextNode,onFailure:dealOnError,functionId:'0570010155'},hashvo); 
	        	}
		  }
    }   	
    
    
    function getNextNode4(outparamters)
    {
    				var specialRoleRoleId=outparamters.getValue("specialRoleRoleId");   
    				
    				if(trim(specialRoleRoleId).length>2&&specialRoleRoleId.substring(0,2)=='$$')
    				{  
			        	 hashvo0.setValue("specialRoleUserStr",specialRoleRoleId.substring(2));
			        	 var request=new Request({asynchronous:false,onSuccess:issuccess_list_apply,functionId:'0570010108'},hashvo0); 
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
			        		     var request=new Request({asynchronous:false,onSuccess:issuccess_list_apply,functionId:'0570010108'},hashvo0); 
			        		}
			        	}
			        	else
			        	{
			        		 hashvo0.setValue("specialRoleUserStr","");
			        		  var request=new Request({asynchronous:false,onSuccess:issuccess_list_apply,functionId:'0570010108'},hashvo0); 
			        	} 
			        } 
    }
    
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
			        		var obj_vo =window.showModalDialog("/general/template/myapply/busiPage.do?b_select=link&selfapply=0&sp_mode=0&tabid="+tabid+"&specialRoleNodeId="+specialRoleNodeId,null,"dialogWidth=650px;dialogHeight=450px;status=no");  
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
    
    function continue_apply(specialRoleUserStr)
   	{
   					var obj_vo;
   					var hashvo=new ParameterSet();
   					var no_sp_yj=document.getElementById("no_sp_yj").value;
		        	if(isSendMessage=='0'&&no_sp_opinion=='true')
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
		        	{
		        		if(no_sp_yj==1){
		        			obj_vo=new Object();
			        		obj_vo.sp_yj='01'
			        		obj_vo.pri='1';
			        		obj_vo.content='';
			        		obj_vo.isSendMessage=0;
			        		obj_vo.specialOperate='0';
//			        		if(!ifqrzx()){  //当用户进行报批的时候，不要弹出窗口进行提示 liuzy 20151208
//			        			return;
//			        		}
		        		}else{
						     var dialogWidth="650px";
						     var dialogHeight="550px";
						     if (isIE6()){
						     	dialogWidth="700px";
						     	dialogHeight="600px";
						     } 
		        			obj_vo =window.showModalDialog("/general/template/submit_form.do?b_apply=link&taskid="+taskid+"&sp_batch="+sp_batch+"&isApplySpecialRole="+isApplySpecialRole+"&tabid="+tabid+"&noObject=1",
		        			null,"dialogWidth="+dialogWidth+";dialogHeight="+dialogHeight+";status=no");
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
		        		hashvo.setValue("tabid",tabid);	        		
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
			   	        var request=new Request({asynchronous:false,onSuccess:issuccess_list_apply,onFailure:dealOnError,functionId:'0570010108'},hashvo); 
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
    function delete_obj_list(setname,tabid,task_id,selected){
		disableButtons();
		setTimeout(function(){delete_obj_list_ProcessBar(setname,tabid,task_id,selected);closeProcessBar();},100);
		
	}
	function delete_obj_list_ProcessBar(setname,tabid,task_id,selected)
	{
		isSelectedObj();
		if(selectAll==0)
   	    {
              
   	    }  else  if(selectAll==2){
			closeProcessBar();
        	alert(NOTING_SELECT);
        	return;
        } 
	
	    if(!confirm(BOLISH_INFO)){
			closeProcessBar();
		     return;	
		}
	    
	   	var hashvo=new ParameterSet();
	    hashvo.setValue("task_id",task_id);
	    hashvo.setValue("tabid",tabid);
	    hashvo.setValue("setname",setname);
	    hashvo.setValue("returnflag","list");
	    hashvo.setValue("selected",selected);
	    hashvo.setValue("operationtype",operationtype);
	    var request=new Request({asynchronous:false,onSuccess:delete_obj2_list,onFailure:dealOnError,functionId:'0570010145'},hashvo);    
	}   
	
	
	function delete_obj2_list(outparamters)
	{
		   var from_msg=outparamters.getValue("from_msg");
		   var selected=outparamters.getValue("selected");
		   var isDelMsg="0";
		   var msg="";
		   if(infor_type=="1")
		   msg="撤销人员中有其他模板下通知单后自动加入的人员，是否将通知单同时撤销？";
		   else if(infor_type=="2")
		   msg="撤销机构中有其他模板下通知单后自动加入的机构，是否将通知单同时撤销？";
		    else if(infor_type=="3")
		   msg="撤销职位中有其他模板下通知单后自动加入的职位，是否将通知单同时撤销？";
		   if(from_msg=='1')
		   {
		   		if(confirm(msg))
		   			isDelMsg="1";
		   }
		   var hashvo=new ParameterSet();
	        hashvo.setValue("task_id",taskid);
	        hashvo.setValue("ins_id",ins_id);
		    hashvo.setValue("tabid",tabid);
		    hashvo.setValue("setname",table_name);
	        hashvo.setValue("isDelMsg",isDelMsg);
	        hashvo.setValue("selected",selected);
	        hashvo.setValue("infor_type",infor_type);
	        
	        hashvo.setValue("sp_batch",sp_batch);
      		hashvo.setValue("batch_task",tasklist_str);
	        
	        var request=new Request({asynchronous:false,onSuccess:issuccess_list_del,onFailure:dealOnError,functionId:'0570040004'},hashvo);    
	
	}
    
     function issuccess_list_del(outparamters)
    {
		closeProcessBar();
    	document.templateListForm.action="/general/template/templatelist.do?b_query=query&&tabid="+tabid+"&a_code="+codeid+"&pagecurent=1";
		document.templateListForm.submit();
	}
	 function issuccess_list_query(outparamters)
    {
       var indexnames = outparamters.getValue("indexnames");
	   if(indexnames!=null&&indexnames.length>3)
	    	alert("检索条件限制了以下人员的引入:\r\n"+indexnames.substring(0,indexnames.length-1)+"！");
	   
       document.templateListForm.action="/general/template/templatelist.do?b_query=query&&tabid="+tabid+"&a_code="+codeid+"&pagecurent=1";
	   document.templateListForm.submit();
	}
  	 function issuccess_list_query2(outparamters)
    {
     var indexnames = outparamters.getValue("indexnames");
	    if(indexnames!=null&&indexnames.length>3)
	  	alert("检索条件限制了以下人员的引入:\r\n"+indexnames.substring(0,indexnames.length-1)+"！");
    	var href = parent.location.href;
	 	href=href.replace("b_init","br_init");
	 	parent.location.href=href;
	}
  
 function issuccess_list_apply(outparamters)
    {
    	 var msgs = outparamters.getValue("msgs");
	    if(msgs!=null&&msgs!="yes"&&msgs.length>3)
	    alert(msgs);
    	document.templateListForm.action="/general/template/templatelist.do?b_query=query&&tabid="+tabid+"&a_code="+codeid+"&pagecurent=1";
		document.templateListForm.submit();
	}
    function issuccess_list(outparamters)
    {
    	document.templateListForm.action="/general/template/templatelist.do?b_query=query&tabid="+tabid+"&a_code="+codeid;
		document.templateListForm.submit();
    	/*
    	if(businessmodel=='2')
    	{
    		alert("报批成功!");
	    	var ins_id=outparamters.getvalue("ins_id");
	    	window.parent.returnvalue=ins_id;
	  		window.parent.close();
    	}
    	else
    	{
    		if(sp_batch=='1')
    		{
    			templateform.action="/general/template/edit_page.do?b_query=link&pageno=0&tabid="+tabid+"&sp_batch=1";
			    templateform.submit();
    		}
    		else
    		{
		    	var basepre=outparamters.getvalue("basepre");
		    	var a0100=outparamters.getvalue("a0100");
		    	templateform.action="/general/template/edit_page.do?b_query=link&pageno="+pageno+"&tabid="+tabid+"&a0100="+a0100+"&basepre="+basepre;
			    templateform.submit();
			}
		 }   */
    }
    
    function returnHome()
    {
		closeProcessBar()
    	  var msgs = outparamters.getValue("msgs");
	    if(msgs!=null&&msgs!="yes"&&msgs.length>3){
	    alert(msgs);
		}
    	if(returnflag=='7')
    	{
    	 	parent.location="/system/home.do?b_query=link" ;
			 
    	}
    	else if(returnflag=='8')
    	{
    		if(bosflag&&bosflag=="hcm"){//返回hcm7.0首页
    			parent.location="/templates/index/hcm_portal.do?b_query=link";
    		}else{
    			parent.location="/templates/index/portal.do?b_query=link" ;
    		}
    	 	
		}	 
		else if(returnflag=='3')
    	{
    	 	parent.location="/general/template/task_desktop.do?b_query=link" ;
			 
    	}else if(returnflag=='6')
    	{
    	 	parent.location="/general/template/myapply/busidesktop.do?br_query=link";
			 
    	}
    	else
    	{
	    	if(operationname.length>0)
	    	{ 
	    		parent.location="/general/template/search_module.do?b_query=link&operationname="+operationname+"&staticid="+staticid;
	    	}
	    	else
		    {
		    	if(isEmployee==1)
		    	     parent.location="/general/template/myapply/busidesktop.do?br_query=link";
		    	else
		        	 parent.location="/general/template/task_desktop.do?b_query=link";
	    	}
	    }
    }
    
    function returnWarnHome()
    {
    	parent.location="/system/warn/result_manager.do?b_query=link&warn_id="+warn_id;
    }
    /**
     * returnflag的值
             主页/我的任务 =8
             业务办理/待办任务（人事异动） =1
             业务办理/已办任务（人事异动） =4
             业务办理/我的申请（人事异动） =3
             业务办理/任务监控（人事异动） =2
             业务申请/待办任务（自助服务） =6
             业务申请/已办任务（自助服务） =6
             业务申请/我的申请（自助服务） =6
             业务申请/业务申请（自助服务） =6
     *
     */
    function returnHome(outparamters)
    {
		closeProcessBar()
        var len= arguments.length; 
        if(1 == len) 
        { 
	        var unDealedTaskIds = outparamters.getValue("unDealedTaskIds");
		    if (unDealedTaskIds!=null){  //批量审批，有未处理的单据 需要在页面提示，liuzy 20151209
	    		var beginRejectFlag = outparamters.getValue("beginRejectFlag");
	    	   if(beginRejectFlag=='true'){
	    	   	  var contentTip = outparamters.getValue("contentTip");
	    	   	   alert(contentTip);
	    	   }
	    	}
        }
    	if(returnflag=='7')
    	{
    	 	parent.location="/system/home.do?b_query=link" ;
			 
    	}
    	else if(returnflag=='8')
    	{
    		if(bosflag&&bosflag=="hcm"){//返回hcm7.0首页
    			parent.location="/templates/index/hcm_portal.do?b_query=link";
    		}else{
    			parent.location="/templates/index/portal.do?b_query=link" ;
    		}
			 
    	}else if(returnflag=='3')
    	{
    	 	parent.location="/general/template/task_desktop.do?b_query=link" ;
			 
    	}else if(returnflag=='6')
    	{
    	 	parent.location="/general/template/myapply/busidesktop.do?br_query=link";
			 
    	}
    	else
    	{
	    	 if(operationname.length>0)
	    	{ 
	    		parent.location="/general/template/search_module.do?b_query=link&operationname="+operationname+"&staticid="+staticid;
	    	}
	    	else
		    {
		    	/*if(isEmployee==1)
		    	     parent.location="/general/template/myapply/busidesktop.do?br_query=link";
		    	else*/
		        	 parent.location="/general/template/task_desktop.do?b_query=link";
		     }
     	}
    }
    
    function bz_computer_list(tabid,ins_id)
	{
	   	 var hashvo=new ParameterSet();	
	     hashvo.setValue("tabid",tabid);	
	     hashvo.setValue("ins_id",ins_id);
	     hashvo.setValue("ins_ids","");
	     var request=new Request({asynchronous:false,onSuccess:bz_computer_list2,functionId:'0570010156'},hashvo); 
	}
	function bz_computer_list2(outparamters)
	{
		var tabid=outparamters.getValue("tabid");
		var ins_id=outparamters.getValue("ins_id");
		var ins_ids=outparamters.getValue("ins_ids"); 
	    var message=getDecodeStr(outparamters.getValue("message"));
	 
		var midValue="";
		var iscontinue="1";
		if(message.length>0)
		{
			var temps=message.split(",");
			for(var i=0;i<temps.length;i++)
			{
				var temp=temps[i].split(":");
				var theURL="/general/template/templatelist/setMidVarValue.jsp?var="+getEncodeStr(temp[0])+"`type="+temp[1];
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
				}else{
					iscontinue="0";
					break;
				}
			}
		}
		if(iscontinue==0)
	 		return;
	   	 var hashvo=new ParameterSet();	
	     hashvo.setValue("tabid",tabid);	
	     hashvo.setValue("ins_id",ins_id);
	     hashvo.setValue("ins_ids","");
	     hashvo.setValue("midValue",getEncodeStr(midValue));
	     var request=new Request({asynchronous:false,onSuccess:refreshData,functionId:'0570010132'},hashvo); 
	}
	
	
	
    function refreshData(outparamters)
	{
		var flag=outparamters.getValue("succeed");
		if(flag=="false")
			return;	
		alert("计算成功!");	
    	document.templateListForm.action="/general/template/templatelist.do?b_query=query&tabid="+tabid+"&a_code="+codeid;
		document.templateListForm.submit();
    
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
   		var thecodeurl="/general/template/iframvartemp.jsp?nflag=0&state="+tabid;
	     var dialogWidth="900px";
	     var dialogHeight="570px";
	     if (isIE6()){
	    	dialogWidth="900px";
	    	dialogHeight="630px";
	     } 
   		var return_vo= window.showModalDialog(thecodeurl, "", 
              	"dialogWidth:"+dialogWidth+"; dialogHeight:"+dialogHeight+";resizable:no;center:yes;scroll:yes;status:no");
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
    
    //展现目标卡
    function showCard(ins_id,task_id,a0100,basepre)
    {
        if(sp_batch=='0'){
       	   parent.location="/general/template/edit_form.do?b_query=link&businessModel=0&isEmployee="+isEmployee+"&showCard=1&returnflag="+returnflag+"&taskid="+taskid+"&sp_flag=1&ins_id="+ins_id+"&tabid="+tabid+"&a0100="+a0100+"&basepre="+basepre+"&sp_batch="+sp_batch;
        }else{
           parent.location="/general/template/edit_form.do?b_query=link&businessModel=0&isEmployee="+isEmployee+"&showCard=1&returnflag="+returnflag+"&batch_task="+batch_task+"&tabid="+tabid+"&ins_id="+ins_id+"&sp_flag=1&a0100="+a0100+"&basepre="+basepre+"&sp_batch=1&homeflag=1&index_template=1";
        }
	   // parent.location="/general/template/edit_form.do?b_query=link&businessModel=0&isEmployee="+isEmployee+"&showCard=1&returnflag="+returnflag+"&taskid="+taskid+"&sp_flag=1&ins_id="+ins_id+"&tabid="+tabid+"&a0100="+a0100+"&basepre="+basepre+"&sp_batch="+sp_batch;
         
    }
    
    
    function showCard2(ins_id,task_id)
    {
       
       if(returnflag=='warnhome')
           parent.location="/general/template/edit_form.do?b_query=link&businessModel=0&sp_flag=1&isInitData=0&ins_id=0&returnflag=5&tabid="+tabid+"&warn_id="+warn_id+"&index_template=1";
       else  if(returnflag=='7'||returnflag=='8')
       {
       	  // parent.location="/general/template/edit_form.do?b_query=link&tabid="+tabid+"&businessModel=0&isInitData=0&ins_id="+ins_id+"&taskid="+taskid+"&sp_flag=1&returnflag="+returnflag+"&sp_batch="+sp_batch+"&index_template=1";
           if(sp_batch=='0'){
       	   		parent.location="/general/template/edit_form.do?b_query=link&tabid="+tabid+"&businessModel=0&isInitData=0&ins_id="+ins_id+"&taskid="+taskid+"&sp_flag=1&returnflag="+returnflag+"&sp_batch="+sp_batch+"&index_template=1";
           }else{
                parent.location="/general/template/edit_form.do?b_query=link&tabid="+tabid+"&businessModel=0&ins_id="+ins_id+"&batch_task="+batch_task+"&sp_flag=1&returnflag="+returnflag+"&sp_batch=1&homeflag=1&index_template=1";
           }

       }
       else{
           if(sp_batch=='0'){
       	   		parent.location="/general/template/edit_form.do?b_query=link&isEmployee="+isEmployee+"&businessModel=0&isInitData=0&returnflag="+returnflag+"&taskid="+taskid+"&sp_flag=1&ins_id="+ins_id+"&tabid="+tabid+"&sp_batch="+sp_batch+"&index_template=1";
           }else{
                parent.location="/general/template/edit_form.do?b_query=link&isEmployee="+isEmployee+"&tabid="+tabid+"&businessModel=0&ins_id="+ins_id+"&batch_task="+batch_task+"&sp_flag=1&returnflag="+returnflag+"&sp_batch=1&homeflag=1&index_template=1";
           }
	      // parent.location="/general/template/edit_form.do?b_query=link&isEmployee="+isEmployee+"&businessModel=0&isInitData=0&returnflag="+returnflag+"&taskid="+taskid+"&sp_flag=1&ins_id="+ins_id+"&tabid="+tabid+"&sp_batch="+sp_batch+"&index_template=1";
       }
    }
    
    function isSelectedObjRecord()
    { 
    	for(var i=0;i<document.templateListForm.elements.length;i++)
   		{
   			if(document.templateListForm.elements[i].type=='checkbox'&&document.templateListForm.elements[i].checked)
   				return true;
   		} 
   	 	return false;
    }
    
    function isSelectedObj()
    {
    	selectAll=0;
    /*	for(var i=0;i<document.templateListForm.elements.length;i++)
   		{
   			if(document.templateListForm.elements[i].type=='checkbox'&&document.templateListForm.elements[i].checked)
   				return true;
   		}
   		*/
   		var hashvo=new ParameterSet();	
	     hashvo.setValue("tabid",tabid);	
	     hashvo.setValue("taskid",taskid);
	     hashvo.setValue("sp_batch",sp_batch);
	     hashvo.setValue("batch_task",tasklist_str);
	     hashvo.setValue("selfapply","false");
	     var request=new Request({asynchronous:false,onSuccess:isSelectedObj2,onFailure:dealOnError,functionId:'0570040051'},hashvo); 
   	//	return false;
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
    	else
    		selectAll=0; 
    }
 	/*
   	 * 上会职称评审会议
   	 * */
	function subMeeting()
   	{  	
     	isSelectedObj();
   	    if(selectAll==0)
   	    {
   	    	if(infor_type=='1')
   	    	{
	   	    	if(type==2)
	   				alert(NOT_HAVE_REJECTOBJECT);    	
	   	    	else
		            alert(NOT_HAVE_OBJECT);
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
        	alert(NOTING_SELECT);
        	return;  
        } 	  
		Ext.require('JobtitleUL.SubMeeting',function(){
			Ext.create("JobtitleUL.SubMeeting",{
				tabid:tabid,
				ins_id:ins_id,
				taskid:taskid,
				sp_batch:sp_batch,
				batch_task:tasklist_str,
				returnflag:returnflag
			}
			);

		});  

   	}
		 	
  	//因为进度条的原因，必须有延迟才能显示进度条
   	function assign_list(type,selfObj){   	
   		var no_sp_yj=document.getElementById("no_sp_yj").value;
 	    if (sp_mode!="1" && no_sp_yj==1){
	        	displayProcessBar();
	        	setTimeout(function(){assign_listWithProgress(type,selfObj);},100);   
	    }
	    else {
            disableButtons();
	          setTimeout(function(){assign_listWithProgress(type,selfObj);closeProcessBar();},100);   
	    } 	
   	}
    var hashvo0=null
   	/**报批下一环节*/
   	function assign_listWithProgress(type,selfObj)
   	{
     	isSelectedObj();
   	    if(selectAll==0)
   	    {
   	     	closeProcessBar();
   	    	if(infor_type=='1')
   	    	{
	   	    	if(type==2)
	   				alert(NOT_HAVE_REJECTOBJECT);    	
	   	    	else
		            alert(NOT_HAVE_OBJECT);
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
        } 	  
   	    var no_sp_yj=document.getElementById("no_sp_yj").value;
   	    if(sp_mode=="1")
   	    {
   	         
   	    	var theurl="/general/template/apply_form.do?b_apply=link&modeType="+modeType+"&taskid="+taskid+"&tabid="+tabid+"&operationtype="+operationtype+"&taskid="+taskid+"&type="+type+"&tabid="+tabid+"&noObject=0&ins_id="+ins_id+"&view_type=1"; //20140929 dengcan 注释 &from=rwjk
   	    	if(no_sp_yj==1){
   	    		if(allow_def_flow_self=='true'){
   	    			theurl="/general/template/apply_form.do?b_applynosp=link&modeType="+modeType+"&taskid="+taskid+"&def_flow_self=1&tabid="+tabid+"&operationtype="+operationtype+"&taskid="+taskid+"&type="+type+"&tabid="+tabid+"&noObject=0&ins_id="+ins_id+"&allow_def_flow_self="+allow_def_flow_self+"&view_type=1"; //20140929 dengcan 注释 &from=rwjk
   	    		}else{
   	    			theurl="/general/template/apply_form.do?b_applynosp=link&modeType="+modeType+"&taskid="+taskid+"&tabid="+tabid+"&operationtype="+operationtype+"&taskid="+taskid+"&type="+type+"&tabid="+tabid+"&noObject=0&ins_id="+ins_id+"&allow_def_flow_self="+allow_def_flow_self+"&view_type=1"; //20140929 dengcan 注释 &from=rwjk
   	    		}
   	    	}else{
   	    		if(allow_def_flow_self=='true')
   	   	        	theurl="/general/template/apply_form.do?b_apply=link&modeType="+modeType+"&taskid="+taskid+"&def_flow_self=1&tabid="+tabid+"&operationtype="+operationtype+"&taskid="+taskid+"&type="+type+"&tabid="+tabid+"&noObject=0&ins_id="+ins_id+"&view_type=1"; //20140929 dengcan 注释 &from=rwjk
   	    	}
   	        var obj_vo;
   	        if(no_sp_yj==1){
   	        	obj_vo =window.showModalDialog(theurl,null,"dialogWidth=500px;dialogHeight=250px;resizable:no;center:yes;scroll:yes;status:no");
   	        }else{
			     var dialogWidth="650px";
			     var dialogHeight="600px";
			     if (isIE6()){
			     	dialogWidth="700px";
			     	dialogHeight="630px";
			     } 
   	        	obj_vo =window.showModalDialog(theurl,null,"dialogWidth="+dialogWidth+";dialogHeight="+dialogHeight+";resizable:no;center:yes;scroll:yes;status:no");
   	        }
			if(obj_vo)
			{ 
				if(obj_vo.flag!='2')
				{
					isHeadCountControl=true;
		        	validateHeadCount2(taskid,tabid,sp_batch,ins_id,tasklist_str);
		       		if(!isHeadCountControl){
						closeProcessBar();
		       	 		return;  
					}
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
	        	hashvo.setValue("tabid",tabid);
	        	hashvo.setValue("sp_mode",sp_mode);
	        	hashvo.setValue("sp_batch",sp_batch);	        	
	        	hashvo.setValue("taskid",taskid);
	        	hashvo.setValue("batch_task",tasklist_str);	   
	        	hashvo.setValue("returnflag","list");   
	        	hashvo.setValue("url_s",url_s);  
	        	hashvo.setValue("isSendMessage",obj_vo.isSendMessage);
	        	hashvo.setValue("specialOperate",obj_vo.specialOperate); 
	        	hashvo.setValue("rejectObj",obj_vo.rejectObj); 
	        	hashvo.setValue("pre_pendingID","");  //普天代办 废掉
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
		   		var request=new Request({asynchronous:false,onSuccess:getNextNode3,onFailure:dealOnError,functionId:'0570010155'},hashvo1); 
		  // 	    var request=new Request({asynchronous:false,onSuccess:returnHome,functionId:'0570010113'},hashvo); 
			}
		}
		else
		{
			var flag=type;
			if(type==1)
			{
							if(selfObj&&(selfObj.value.indexOf("报送")!=-1||selfObj.value.indexOf("报批")!=-1))
								flag="1";
							else
								flag="3";
			} 
			var hashvo=new ParameterSet();
		    hashvo.setValue("task_id",taskid);
		    hashvo.setValue("tabid",tabid);
		    hashvo.setValue("ins_id",ins_id);
		    hashvo.setValue("flag",flag);
		    hashvo.setValue("type",type);
		    hashvo.setValue("sp_mode",sp_mode);
		    hashvo.setValue("sp_batch",sp_batch);
		    hashvo.setValue("batch_task",tasklist_str);
		    var request=new Request({asynchronous:false,onSuccess:getNextNode2,onFailure:dealOnError,functionId:'0570010155'},hashvo); 
		 
		}
   	}   	
   	
   	function getNextNode3(outparamters)
    {
     				var specialRoleRoleId=outparamters.getValue("specialRoleRoleId");  
     				if(trim(specialRoleRoleId).length>2&&specialRoleRoleId.substring(0,2)=='$$')
    				{  
			        	 
			        	  hashvo0.setValue("specialRoleUserStr",specialRoleRoleId.substring(2));
			              var request=new Request({asynchronous:false,onSuccess:returnHome,onFailure:dealOnError,functionId:'0570010113'},hashvo0); 
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
			        		    var request=new Request({asynchronous:false,onSuccess:returnHome,onFailure:dealOnError,functionId:'0570010113'},hashvo0); 
			        		}
			        	}
			        	else
			        	{
			        		 hashvo0.setValue("specialRoleUserStr","");
			        		 var request=new Request({asynchronous:false,onSuccess:returnHome,onFailure:dealOnError,functionId:'0570010113'},hashvo0); 
			        	}  
			        }
    }
   	
   	function getNextNode2(outparamters)
    {
     				nextNodeStr=outparamters.getValue("nextNodeStr");
     				var flag=outparamters.getValue("flag");
     				var type=outparamters.getValue("type");
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
			        		var obj_vo =window.showModalDialog("/general/template/myapply/busiPage.do?b_select=link&selfapply=0&sp_mode=0&tabid="+tabid+"&specialRoleNodeId="+specialRoleNodeId+"&batch_task="+tasklist_str,null,"dialogWidth=650px;dialogHeight=450px;status=no");  //bug33055 单子中人有多个直接领导，7x包60锁查询的不是勾选人，而是列表中最后一个人的。
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
	        if(isSendMessage=='0'&&no_sp_opinion=='true'&&type==1)
	        {
	        		obj_vo=new Object();
	        		obj_vo.sp_yj='01'
	        		obj_vo.pri='1';
	        		obj_vo.content='';
	        		obj_vo.isSendMessage=0;
	        		obj_vo.specialOperate='0';
	        }
	        else{
	        	if(no_sp_yj==1){
	        		obj_vo=new Object();
	        		obj_vo.sp_yj='01'
	        		obj_vo.pri='1';
	        		obj_vo.content='';
	        		obj_vo.isSendMessage=0;
	        		obj_vo.specialOperate='0';
	        	}else{
	        		//xucs 注释掉 &from=rwjk 2014-10-27无论进入列表模式还是卡片模式 都已经不用那些参数了
	        		//obj_vo=window.showModalDialog("/general/template/apply_form.do?b_apply=link&modeType="+modeType+"&from=rwjk&taskid="+taskid+"&sp_batch="+sp_batch+"&tabid="+tabid+"&type="+type+"&operationtype="+operationtype+"&isApplySpecialRole="+isApplySpecialRole+"&tabid="+tabid+"&flag="+flag+"&noObject=1&ins_id="+ins_id,null,"dialogWidth=650px;dialogHeight=600px;resizable:yes;center:yes;scroll:yes;status:no");
				     var dialogWidth="650px";
				     var dialogHeight="600px";
				     if (isIE6()){
				     	dialogWidth="700px";
				     	dialogHeight="630px";
				     }
	        		obj_vo=window.showModalDialog("/general/template/apply_form.do?b_apply=link&modeType="+modeType+"&taskid="+taskid+"&sp_batch="+sp_batch+"&tabid="+tabid+"&type="+type+"&operationtype="+operationtype+"&isApplySpecialRole="+isApplySpecialRole+"&tabid="+tabid+"&flag="+flag+"&noObject=1&ins_id="+ins_id+"&view_type=1",null,"dialogWidth="+dialogWidth+";dialogHeight="+dialogHeight+";resizable:yes;center:yes;scroll:yes;status:no");
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
	        	hashvo.setValue("tabid",tabid);
	        	hashvo.setValue("sp_mode",sp_mode);
	        	hashvo.setValue("taskid",taskid);
	        	hashvo.setValue("sp_batch",sp_batch);
	        	hashvo.setValue("returnflag","list");
	        	hashvo.setValue("batch_task",tasklist_str);
	        	hashvo.setValue("url_s",url_s);
	        	hashvo.setValue("isSendMessage",obj_vo.isSendMessage);
	        	hashvo.setValue("specialOperate",obj_vo.specialOperate); 
	        	hashvo.setValue("rejectObj",obj_vo.rejectObj);
	       		if(obj_vo.isSendMessage!=0)
	       		{
	       			hashvo.setValue("user_h_s",obj_vo.user_h_s);
	       			hashvo.setValue("email_staff_value",obj_vo.email_staff_value);
	       			
	       		}
	        	hashvo.setValue("pre_pendingID",""); //普天代办  废掉
	        	hashvo.setValue("specialRoleUserStr",specialRoleUserStr);
//		        if(type=='1'&&ifqrzx())
		        if(type=='1')  //当设置不弹出意见框的时候，页面报批时不需要弹出确认框 liuzy 20151208
		        {
		        	isHeadCountControl=true;
		        	validateHeadCount2(taskid,tabid,sp_batch,ins_id,tasklist_str);
		       		if(!isHeadCountControl)
		       	 			return;  
			   	    var request=new Request({asynchronous:false,onSuccess:returnHome,onFailure:dealOnError,functionId:'0570010113'},hashvo); 
		        }		
		        if(type=='2')
		        {
			       if (ifqrreject()){
			   	     var request=new Request({asynchronous:false,onSuccess:returnHome,onFailure:dealOnError,onFailure:dealOnError,functionId:'0570010113'},hashvo); 
			       }
			       else {
			         closeProcessBar();
			       }
		        }	
		    }	 
   	}
   	
    function finishedTask(ins_id,taskid)
	{
		displayProcessBar();
	    setTimeout(function(){finishedTask_ProcessBar(type,selfObj);},100); 
	}
    //完成任务
   	function finishedTask_ProcessBar(ins_id,taskid)
	{
	 		isSelectedObj();
   	    	if(selectAll==0)
	   	    {
	            if(infor_type=='1'){
	            	alert(NOT_HAVE_OBJECT);
	            }else{
	              	alert(NOT_HAVE_ORGRECORD);
	            }
	        	return;   	    
	   	    } else  if(selectAll==2){
	        	alert(NOTING_SELECT);
	        	return;  
	        }
   	    	var no_sp_yj=document.getElementById("no_sp_yj").value;
   	    	var obj_vo =window.showModalDialog("/general/template/apply_form.do?b_apply=link&from=rwjk&modeType="+modeType+"&taskid="+taskid+"&tabid="+tabid+"&operationtype="+operationtype+"&message=0&flag=1&noObject=1&ins_id="+ins_id+"&view_type=1",null,"dialogWidth=650px;dialogHeight=600px;resizable:yes;center:yes;scroll:yes;status:no");
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
	        	
	       		if(taskid=="0")
		       		var request=new Request({asynchronous:false,onSuccess:issuccess_list,onFailure:dealOnError,functionId:'0570010143'},hashvo); 
				else
					var request=new Request({asynchronous:false,onSuccess:returnHome,onFailure:dealOnError,functionId:'0570010143'},hashvo); 
	       	}
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
				window.showModalDialog("/general/template/templatelist.do?b_showspyj=spyj","", 
		              "dialogWidth:"+dialogWidth+"; dialogHeight:"+dialogHeight+";resizable:no;center:yes;scroll:yes;status:no");
		}
	
	  /**yj=0(正表)，=1审批意见表 */
   function outpdf(setname,ins_id,flag,yj)
   {
        var hashvo=new ParameterSet();
        hashvo.setValue("ins_id",ins_id);
        hashvo.setValue("task_id",taskid);
		hashvo.setValue("setname",table_name);
		hashvo.setValue("tabid",tabid);
		hashvo.setValue("flag",flag);
		hashvo.setValue("yj",yj);	
	    hashvo.setValue("sp_batch",sp_batch);
	    hashvo.setValue("batch_task",tasklist_str);
	    hashvo.setValue("infor_type",infor_type);			
	   	var request=new Request({asynchronous:false,onSuccess:showPdf,functionId:'0570010119'},hashvo); 
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
//			  	window.location.href="/servlet/DisplayOleContent?filename="+filename;
			    //20/3/18 xus vfs改造
				window.location.href = "/servlet/vfsservlet?fileid="+filename+"&fromjavafolder=true";
		 }		
   }     
    function showPDF(outparamters)
    {
      var url=outparamters.getValue("url");
    //var win=open("/servlet/DisplayOleContent?filename="+url,"pdf");	
      window.location.target="_blank";
//	  window.location.href="/servlet/DisplayOleContent?filename="+url;
	  //20/3/18 xus vfs改造
	  window.location.href = "/servlet/vfsservlet?fileid="+url+"&fromjavafolder=true";
    }  
   
   /*
 =0 业务人员申请
 =1 审批
 =2 员工申请
*/

function printout()
{
    if(!AxManager.setup("TmplPreview1div", "TmplPreview1", 0, 0, printout, AxManager.tmplpkgName))
           return;
     
      /* 卡片类型：
         1: 模板
         2: 模板归档信息
         3: 员工申请临时表, g_templet_模板号
         4: 审批临时表, templet_模板号
      */
      var cardtype=1;
      if(ins_id!=0)
        cardtype=4; 
     
      var hashvo=new ParameterSet();
      hashvo.setValue("ins_id",ins_id);
      hashvo.setValue("task_id",taskid);
      hashvo.setValue("tabid",tabid);
      hashvo.setValue("cardtype",cardtype);
      hashvo.setValue("sp_batch",sp_batch);
	  hashvo.setValue("batch_task",tasklist_str);
      hashvo.setValue("infor_type",infor_type);
      var request=new Request({asynchronous:false,onSuccess:printout2,functionId:'0570040006'},hashvo); 
       	
}

/**当点击打印时才加载cs控件**/
function printout2(outparamters)
{
	var obj_str=getDecodeStr(outparamters.getValue("a0100_str"));
	
	var judgeisllexpr=outparamters.getValue("judgeisllexpr");
	if(judgeisllexpr!=null && judgeisllexpr!="1")
		    alert(judgeisllexpr);
	
	var cardtype=outparamters.getValue("cardtype");
	var obj_arr=obj_str.split("`");
	
	  var obj = document.getElementById("TmplPreview1");
     var isload = isLoad(obj);
     if(isload==true){
   	  loadOkPrintout(obj,cardtype,obj_str,obj_arr);
     }
}
function loadOkPrintout(obj,cardtype,obj_str,obj_arr){
	/**每次都初始化一下吧**/
	initCard(hosturl,dbtype,username,userFullName,superUser,nodeprive,tables,'TmplPreview1',_version,usedday);
	obj.SetTemplateID(tabid);
    obj.SetTemplateType(cardtype); 
    obj.ClearObjs(); 
    if(obj_str.length==0)
    	return;
    for(var i=0;i<obj_arr.length;i++)
    {
    	obj.AddObj(obj_arr[i]);
    }
    try { obj.SetJSessionId(AxManager.getJSessionId()); } catch(err) {}
    obj.ShowCardModal();
      	
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


  function get_common_query(setname,dbpre,query_type,sys_filter_factor)
  {
        var dbpre_arr=new Array();
        dbpre_arr=dbpre.split(",");
	//	var objlist=common_query("1",dbpre_arr,query_type,getDecodeStr(sys_filter_factor));
		if(isfilter_select=='0')
			sys_filter_factor='';
		var strdb="";
	    if(dbpre_arr)
	      strdb=dbpre_arr.toString();
	    var priv_ctrl="";
	    if(no_priv_ctrl=="1")
	    	priv_ctrl="`priv=0";
	    var strurl="/general/query/common/select_query_fields.do?b_init=link`isGetSql=1"+priv_ctrl+"`type="+infor_type+"`show_dbpre="+strdb+"`query_type="+query_type 
	      + "`filter_factor="+sys_filter_factor+'`tabid='+tabid;
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
	    
	    var height=500;
	  //  if(query_type==2)
	//		height=600;	    
	    var _sql=window.showModalDialog(iframe_url,null,"dialogWidth=620px;dialogHeight="+height+"px;resizable=yes;status=no;");  
		if(_sql&&_sql.length>0)
		{ 
			var hashvo=new ParameterSet();
	        hashvo.setValue("_sql", getEncodeStr(_sql));
			hashvo.setValue("setname",setname);
			hashvo.setValue("tabid",tabid); 
		   	var request=new Request({asynchronous:false,onSuccess:issuccess_list_query,functionId:'0570010109'},hashvo); 
		}
   }     

 function get_hand_query(setname)
 {
 		var return_vo;
 		 
 		if(infor_type=='1'){
 			if(modeType=='23')
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
	 	}else{
	 	//var href = parent.location.href;
	 	//href=href.replace("b_init","br_init");
	 	//parent.location.href=href;
//	 	window.location.reload();
	 	
	 	
	 	}
 }  


  function addObject(objlist,setname)
  {	
        var hashvo=new ParameterSet();
        hashvo.setValue("objlist",objlist);
		hashvo.setValue("setname",setname);
		hashvo.setValue("tabid",tabid/*'${templateForm.tabid}'*/);
	   	var request=new Request({asynchronous:false,onSuccess:issuccess_list_query2,functionId:'0570010109'},hashvo); 
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
function filloutSequence(){
 
		 
//   	    if(isSelectedObjRecord)
//		   	    {
//		            alert(NOT_HAVE_SEQUENCE);
//		        	return;   	    
//		   	    } 	  
	   var hashvo=new ParameterSet();
      hashvo.setValue("setname",table_name);
		hashvo.setValue("tabid",tabid);
		hashvo.setValue("whl",document.templateListForm.filterStr.value);
		//js中不允许存放sql相关参数，这里去掉了needcondition
		hashvo.setValue("infor_type",infor_type);   
	   	var request=new Request({asynchronous:false,onSuccess:issuccess_list_query2,functionId:'0570010211'},hashvo); 
}
//组织机构树如果显示人员，则先显示人员库
function select_org_emp_dialog2_rsjd(flag,selecttype,dbtype,priv,isfilter,loadtype,generalmessage)
{													//("1","1","0","1","0","1",generalmessage); 
	 if(dbtype!=1&&dbtype!=3)
	 	dbtype=0;
	 if(priv!=0)
	    priv=1;
     var theurl="/system/logonuser/org_employ_tree.do?b_query=link`flag="+flag+"`showDb=1`tabid="+tabid+"`selecttype="+selecttype+"`dbtype="+dbtype+
                "`priv="+priv + "`isfilter=" + isfilter+"`loadtype="+loadtype+"`generalmessage="+generalmessage;
      var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;  
	     var dialogWidth="300px";
	     var dialogHeight="450px";
	     if (isIE6()){
	     	dialogWidth="320px";
	     	dialogHeight="450px";
	     } 
     var return_vo= window.showModalDialog(iframe_url,1, 
        "dialogWidth:"+dialogWidth+"; dialogHeight:"+dialogHeight+";resizable:no;center:yes;scroll:yes;status:no");
	 return return_vo;
}
//自定义审批流程	
function showDefFlowSelf(tabid)
{
	var win=window.open("/general/template/def_flow_self.do?b_query=init"
	      +"&task_id=0&ins_id=-1&node_id=0&tabid="+tabid +"&fromflag=list"    
	      ,"_parent");
	
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
	   enableButtons()
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
	   var rejectButton=document.getElementById("rejectButton");
	   var abolishButton=document.getElementById("abolishButton");
	   if(submitButton)
		   submitButton.disabled="disabled";
	   if(applyButton)
		   applyButton.disabled="disabled";
	   if(rejectButton)
		   rejectButton.disabled="disabled";
	   if(abolishButton)
		   abolishButton.disabled="disabledButton";
   }
   function enableButtons(){
	   var submitButton=document.getElementById("submitButton");
		  var applyButton=document.getElementById("applyButton");
		  var rejectButton=document.getElementById("rejectButton");
		  var abolishButton=document.getElementById("abolishButton");
		  if(submitButton)
			   submitButton.disabled="";
		   if(applyButton)
			   applyButton.disabled="";
		   if(rejectButton)
			   rejectButton.disabled="";
		   if(abolishButton)
			   abolishButton.disabled="";
   }
 