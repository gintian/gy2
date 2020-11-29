	function verifyFormula()
	{
		var waitInfo=eval("wait");		
	   waitInfo.style.display='block';
	   document.getElementById("wait_desc").innerHTML="正在进行审核操作,请稍候...";
	   setState0(true);
	   
	   var hashvo=new ParameterSet();
	   hashvo.setValue("a_code",a_code);
	   hashvo.setValue("condid",condid);
	   hashvo.setValue("salaryid",salaryid);
	   hashvo.setValue("type","0");
	   hashvo.setValue("reportSQL",filterWhl);
	   var request=new Request({asynchronous:false,onSuccess:checked_ok,functionId:'3020070016'},hashvo);
	}
	function checked_ok1(outparameters)
	{
	  var waitInfo=eval("wait");	
		waitInfo.style.display="none";
		setState0(false)
	  var msg=outparameters.getValue("msg");
	  if(msg=='0' || msg=='no')//审核通过
	  {
		  alert("审核通过！");
	  }
	  else{//审核不通过
	     var filename=outparameters.getValue("fileName");
	     var fieldName = getDecodeStr(filename);
		 var win=open("/servlet/vfsservlet?fileid="+fieldName+"&fromjavafolder=true","excel");
	  }
	}
   //报审  1:驳回 2：报审
	function report()
	{
		var desc="您确定要报审吗";	
		if(confirm(desc+"？"))
		{
			if(verify_ctrl=='1'){//进行审核公式控制,lis添加
				var waitInfo=eval("wait");		
			   waitInfo.style.display='block';
			   document.getElementById("wait_desc").innerHTML="正在进行审核操作,请稍候...";
			   setState0(true);
			   
			   var hashvo=new ParameterSet();
			   hashvo.setValue("a_code",a_code);
			   hashvo.setValue("condid",condid);
			   hashvo.setValue("salaryid",salaryid);
			   hashvo.setValue("type","0");
			   hashvo.setValue("reportSQL",filterWhl);
			   var request=new Request({asynchronous:false,onSuccess:checked_ok,functionId:'3020070016'},hashvo);	
			}else{//不进行审核公式控制
				document.salaryDataForm.action="/gz/gz_data/gz_table.do?b_report=appeal&opt=2"+url;
				document.salaryDataForm.submit();	
			}
		}
	}
	
	//lis添加
	function checked_ok(outparameters)
	{
	  var waitInfo=eval("wait");	
		waitInfo.style.display="none";
		setState0(false)
	  var msg=outparameters.getValue("msg");
	  if(msg=='0' || msg=='no')//审核通过
	  {
		  document.salaryDataForm.action="/gz/gz_data/gz_table.do?b_report=appeal&opt=2"+url;
		  document.salaryDataForm.submit();	
	  }
	  else{//审核不通过
	     var filename=outparameters.getValue("fileName");
	     var fieldName = getDecodeStr(filename);
		 var win=open("/servlet/vfsservlet?fileid="+fieldName+"&fromjavafolder=true","excel");
	  }
	}
	
	//lis添加，按钮是否可用
	function setState0(_boolean)
	{
		var bt=document.getElementById("buttondownload");
		if(bt)
			bt.disabled=_boolean;
		bt=document.getElementById("buttonimport");
		if(bt)
			bt.disabled=_boolean;
		bt=document.getElementById("buttonsavedata");
		if(bt)
			bt.disabled=_boolean;	
		bt=document.getElementById("buttoncomputer");
		if(bt)
			bt.disabled=_boolean;	
		bt=document.getElementById("buttongoback");
		if(bt)
			bt.disabled=_boolean;	
			
		bt=document.getElementById("buttonprintMuster");
		if(bt)
				bt.disabled=_boolean;
		bt=document.getElementById("buttonappeal2");
		if(bt)
				bt.disabled=_boolean;
		bt=document.getElementById("buttongoback2");
		if(bt)
				bt.disabled=_boolean;
	}
	
	function report2(theyear,themonth,operOrg)
	{	
		var desc="您确定要上报吗";	
		if(confirm(desc+"？"))
		{
				document.salaryDataForm.action="/gz/gz_data/gz_table.do?b_report=appeal&opt=3"+url;
				document.salaryDataForm.submit();		
		}
	}
	function go_back()
	{
		salaryDataForm.target="il_body";
		salaryDataForm.action="/gz/gz_data/gz_set_list.do?b_query=link";
		salaryDataForm.submit();
	}
	function go_back2(theyear,themonth,operOrg,returnFlag,isLeafOrg,isOrgCheckNo)
	{   
		salaryDataForm.target="il_body";
		salaryDataForm.action="/gz/premium/premium_allocate/monthPremiumList.do?b_query=link&theYear="+theyear+"&theMonth="+themonth+"&orgcode="+operOrg+'&returnFlag='+returnFlag+'&isLeafOrg='+isLeafOrg+'&isOrgCheckNo='+isOrgCheckNo;
		salaryDataForm.submit();
	}
function search_gz_data_byitem(salaryid,setobj)
{
   var itemid;
   for(i=0;i<setobj.options.length;i++)
   {
      if(setobj.options[i].selected)
      {
    	itemid=setobj.options[i].value;
    	break;
      }   
   }	
   if(itemid!="new")
   {    
	    document.getElementById("proright_str").value="";	
	    //salaryDataForm.action="/gz/gz_data/gz_table.do?b_query=link&salaryid="+salaryid;
	    salaryDataForm.action="/gz/gz_data/gz_table.do?b_query=link"+url;
		salaryDataForm.submit();    		
   }	
   else
   {
   		var thecodeurl ="/gz/gz_accounting/gzprofilter.do?b_query=link&opt=2&salaryid="+salaryid;
    	var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:480px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
   		if(return_vo!=null)
   		{
   			document.getElementById("proright_str").value=return_vo[0];
   			var obj=document.getElementById("itemid");
   			obj.options[obj.options.length-1].value=return_vo[1];   
            obj.options[obj.options.length-1].text=getDecodeStr(return_vo[2]);  
   			obj.options[obj.options.length-1].selected=true;   			
		
	    	//salaryDataForm.action="/gz/gz_data/gz_table.do?b_query=link&salaryid="+salaryid;	   
	    	salaryDataForm.action="/gz/gz_data/gz_table.do?b_query=link"+url;     
  	    	salaryDataForm.submit();
   		}else
   		{
   			changeItemidList(salaryid,'0');
   		}		
   }
}
function changeItemidList(salaryid,model)
{
		 var hashVo=new ParameterSet();
		 hashVo.setValue("salaryid",salaryid);
		 hashVo.setValue("model",model);
		 var In_parameters="opt=1";
		 var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:change_itemlist_ok,functionId:'3020070203'},hashVo);			
		    
}
	function change_itemlist_ok(outparameters)
	{
		  var filterList = outparameters.getValue("itemfilterlist");
		  var model=outparameters.getValue("model");
		  var salaryid=outparameters.getValue("salaryid");
		  AjaxBind.bind(salaryDataForm.itemid,filterList); 
		  var obj=$("itemid"); 
		  for(var i=0;i<obj.options.length;i++)
		  {
		      obj.options[i].text=getDecodeStr(obj.options[i].text);
		  }
		  if(obj.options.length==2)
		  {
		    if(model=='1')
		    {
		       salaryDataForm.action="/gz/gz_accounting/gz_sptable.do?b_query=link&salaryid="+salaryid;
		    }
		    else
		    {
		        // salaryDataForm.action="/gz/gz_data/gz_table.do?b_query=link&salaryid="+salaryid;
		           salaryDataForm.action="/gz/gz_data/gz_table.do?b_query=link"+url;   
		    }
		    salaryDataForm.submit();
		  }
		  else
		  {
		     for(var i=0;i<obj.options.length;i++)
		     {
		        if(obj.options[i].value==prv_project_id)
		        {
		            obj.options[i].selected=true;
		            return;
		        }
		     }
		    if(model=='1')
		    {
		       salaryDataForm.action="/gz/gz_accounting/gz_sptable.do?b_query=link&salaryid="+salaryid;
		    }
		    else
		    {
		         //salaryDataForm.action="/gz/gz_data/gz_table.do?b_query=link&salaryid="+salaryid;
		           salaryDataForm.action="/gz/gz_data/gz_table.do?b_query=link"+url;   
		    }
			salaryDataForm.submit();
		  }     
	}
//导出摸板
function downLoadTemp(salaryid)
{	
	var hashvo=new ParameterSet();	
	hashvo.setValue("salaryid",salaryid);
	hashvo.setValue("itemid",$F('projectFilter'));
	hashvo.setValue("sqlStr",getEncodeStr(salaryDataForm.sql.value));
	hashvo.setValue("proright_str",salaryDataForm.proright_str.value); 	
	var request=new Request({method:'post',asynchronous:false,onSuccess:showfile1,functionId:'3020071020'},hashvo);
}
function showfile1(outparamters)
{
	var outName=outparamters.getValue("outName");
	var fieldName = getDecodeStr(filename);
	window.location.target="_blank";
	window.location.href = "/servlet/vfsservlet?fileid="+fieldName+"&fromjavafolder=true";
}
//导入模板数据
function importTempData(salaryid)
{	
	document.salaryDataForm.action="/gz/gz_data/gz_table.do?br_import=init&salaryid="+salaryid;
  	document.salaryDataForm.submit();
}
//打印高级花名册
function outMuster(tabid,salaryid)
{
	var strurl="/gz/gz_accounting/report/gz_org_tree.do?b_query=link`";
	strurl+="salaryid="+salaryid+"`tabid="+tabid;
	strurl+="`opt=int`a_code=UN`conid=all`";
	 	 	strurl+="gz_module=1`reset=1`";
	 	 	strurl+="model=0";	 	 	
	 	 	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
		     var flag=window.showModalDialog(iframe_url,"","dialogWidth="+window.screen.width+"px;dialogHeight="+window.screen.height+"px;resizable=yes;scroll=no;status=no;");  
}
//计算
function get_formula(salaryid)
{	
	var premium_str="no";
	if(returnFlag&&returnFlag=='1')
		premium_str="yes";	
	var thecodeurl="/gz/gz_accounting/formulalist.do?b_query=link`premium="+premium_str+"`salaryid="+salaryid; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
	var retvo= window.showModalDialog(iframe_url, null, 
		        "dialogWidth:460px; dialogHeight:550px;resizable:no;center:yes;scroll:yes;status:no");			
  	 if(retvo==null)
  	 	return ;		        
	if(retvo.success=="1")
	{
	    var salaryid=retvo.salaryid;
		salaryDataForm.action="/gz/gz_data/gz_table.do?b_query=link&salaryid="+salaryid+url;
		salaryDataForm.submit();    	
	}
}
function search_gz_data_bycond(salaryid,setobj,tableName)
{
       var itemid,i;
	   for(i=0;i<setobj.options.length;i++)
	   {
	      if(setobj.options[i].selected)
	      {
	    	itemid=setobj.options[i].value;
	    	break;
	      }   
	   }	
	   if(itemid!="new")
	   {
	   		document.getElementById("empfiltersql").value="";
			salaryDataForm.action="/gz/gz_data/gz_table.do?b_query=link&salaryid="+salaryid+url;
			salaryDataForm.submit();    		
	   }	
	   else
	   {
			sql_str=bankdisk_personFilter(salaryid,tableName,1);
			if(sql_str==null||trim(sql_str).length==0)
			{
				bankdisk_changeCondList(salaryid);
			}else
			{
			    salaryDataForm.cond_id_str.value=cond_id_str;
				document.getElementById("empfiltersql").value=sql_str;
			 	salaryDataForm.action="/gz/gz_data/gz_table.do?b_query=link&salaryid="+salaryid+url;
				salaryDataForm.submit(); 
			}
		  		
	   }
}
function bankdisk_personFilter(salaryid,tableName,type)
{
var model=document.getElementById("gm").value;

var theURL="/gz/gz_accountingt/bankdisk/personFilter.do?b_select=select"+"`salaryid="+salaryid+"`tableName="+tableName+"`model="+model;
var iframe_url="/gz/gz_accounting/bankdisk/iframe_bank_disk.jsp?src="+$URL.encode(theURL);
var objlist =window.showModalDialog(iframe_url,null,"dialogWidth=600px;dialogHeight=400px;resizable=yes;status=no;");  
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
  if(parseInt(obj.isclose)==2)//点取消按钮返回
  {
     bankdisk_changeCondList(salaryid);
  }
  }
}
return obj.sql;
}
function bankdisk_changeCondList(salaryid)
{
	var hashVo=new ParameterSet();
	hashVo.setValue("isclose","2");
	hashVo.setValue("salaryid",salaryid);
		 //var In_parameters="opt=1";
    var request=new Request({method:'post',asynchronous:false,onSuccess:change_condlist_ok,functionId:'3020100017'},hashVo);			
}
	function change_condlist_ok(outparameters)
	{
		  var filterList = outparameters.getValue("filterCondList");
		  AjaxBind.bind(salaryDataForm.condid,filterList); 
		  var obj=$("condid"); 
		  if(obj.options.length==2)
		  {
		    salaryDataForm.action="/gz/gz_data/gz_table.do?b_query=link&salaryid="+salaryid+url;
		    salaryDataForm.submit();
		  }
		  else
		  {
		     for(var i=0;i<obj.options.length;i++)
		     {
		        if(obj.options[i].value==prv_filter_id)
		        {
		            obj.options[i].selected=true;
		            return;
		        }
		     }
		  }     
	}