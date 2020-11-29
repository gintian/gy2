
 function returnMain(fromflag)
 {
    //同步 因为层级可能有断层，需重新设置层级
	var hashvo=new ParameterSet();
	hashvo.setValue("tabid",tabid);
	hashvo.setValue("taskid",taskid);
	hashvo.setValue("ins_id",ins_id);
	hashvo.setValue("node_id",node_id);
	hashvo.setValue("strXml",strXml);
	
	hashvo.setValue("oprflag","synSpLevel");
	if (fromflag=="card"){	
		var request=new Request({asynchronous:false,onSuccess:synSpLevelOk1,functionId:'0570010165'},hashvo);	
	}
	else if (fromflag=="list") {
		var request=new Request({asynchronous:false,onSuccess:synSpLevelOk2,functionId:'0570010165'},hashvo);		
	}
	else if (fromflag=="myapply") {
		var request=new Request({asynchronous:false,onSuccess:synSpLevelOk3,functionId:'0570010165'},hashvo);		
	}
 }   

 function synSpLevelOk3()
 { 
    var url ="/general/template/myapply/busiTemplate.do?b_query=link&tabid="+tabid
    	   +"&ins_id=0&returnflag=6";
  
   location=url;
 
}
 function synSpLevelOk1()
 { 
    var url ="/general/template/edit_form.do?b_query=link&tabid="+tabid
    	   +"&businessModel=0&isInitData=0&ins_id="+fromins_id+"&taskid="+fromtaskid
    	   +"&sp_flag=1&returnflag="+returnflag
    	   +"&sp_batch="+sp_batch+"&index_template=1";
  
   location=url;
 
}

 function synSpLevelOk2()
 {      
	if(returnflag=="7"||returnflag=="10"||returnflag=="8"||returnflag=="bi"
	      ||returnflag=="3"||returnflag=="6"||returnflag=="9")
	{
		 
		if(sp_batch==1)
		{
		   url ="/general/template/templatelist.do?b_init=init&isInitData=0&sp_flag=1&ins_id="
		   +fromins_id+"&returnflag="+returnflag+"&tasklist_str="+batch_task+"&tabid="+tabid+"&index_template=1";
		}
		else
		{
			url= "/general/template/templatelist.do?b_init=init&isInitData=0&sp_flag=1&ins_id="
			+fromins_id+"&returnflag="+returnflag+"&task_id="+fromtaskid+"&tabid="+tabid+"&index_template=1";
	     }
	}
	else // returnflag=="5"
	{
			url="/general/template/templatelist.do?b_init=init&isInitData=0&"
			 +"sp_flag=1&ins_id=0&returnflag=warnhome&task_id=0&tabid="+tabid+"&warn_id="+warn_id;  
	}
	location=url;
   
}

 function addLevel()
 { 
	var hashvo=new ParameterSet();
	hashvo.setValue("tabid",tabid);
	hashvo.setValue("taskid",taskid);
	hashvo.setValue("ins_id",ins_id);
	hashvo.setValue("node_id",node_id);
	hashvo.setValue("strXml",strXml);
	
	hashvo.setValue("oprflag","addLevel");
	var request=new Request({asynchronous:false,onSuccess:addLevelOk,functionId:'0570010165'},hashvo);	
 
}

function addLevelOk(outparamters)
{	strXml = outparamters.getValue("strXml");
	var levelnum = outparamters.getValue("levelnum");
	var leveldesc= outparamters.getValue("leveldesc");
	if (levelnum==null) {levelnum="0"};

    var curTable = document.getElementById("tblDefFlowSelf");	
    var rows = curTable.rows;
    var rCount = rows.length;
	var tRow = curTable.insertRow(rCount-1);
//	tRow.setAttribute("onclick",'tr_onclick(this,"#F3F5FC");');
	//tRow.onclick=tr_onclick(input,"#F3F5FC");
	tRow.onclick=function() { 
        tr_onclick(this,"#F3F5FC");  }  

	
	var td0 = tRow.insertCell(0);
	td0.setAttribute("align","center");
	td0.className = "RecordRow";
	td0.innerHTML = "<input type='checkbox' name='select'/>";
	
	var td1 = tRow.insertCell(1);
	td1.setAttribute("align","left");
	td1.className = "RecordRow";
	td1.innerHTML = '&nbsp; <input type="hidden" name="levels" value="'+levelnum+'"/> '+leveldesc;
	
	var td2 = tRow.insertCell(2);
	td2.setAttribute("align","left");
	td2.className = "RecordRow";
	td2.innerHTML = "&nbsp;";	
	
	var td3 = tRow.insertCell(3);
	td3.setAttribute("align","center");
	td3.className = "RecordRow";
	td3.innerHTML = ' <img src="/images/edit.gif" onclick="javascript:addPerson(1,'+levelnum+')" ' 
	                  +'   border=0 style="cursor:hand" />';	

}
function delLevel()
{	var strLevels="";
	var bHaveSel=false;
	var index=0;
	var  obj = document.getElementsByName("levels");
	for(var i=0;i<document.defFlowSelfForm.elements.length;i++)
	{			
   		if(document.defFlowSelfForm.elements[i].type=='checkbox'&&document.defFlowSelfForm.elements[i].name!="selbox")       
  			{	
	  		if(document.defFlowSelfForm.elements[i].checked)
	  			{
	  				bHaveSel=true;
					strLevels=strLevels+obj[index].value+",";
				}
				index++;
		}
	}
	if(!bHaveSel)
	{
	    alert(GENERAL_TEMPLATE_NOSELECT_SPLEVEL);
	    return;
   	}
   	else
   	{
   		strLevels = strLevels.substring(0,strLevels.length-1);
		if(confirm(GENERAL_TEMPLATE_CONFIRM_DELSPLEVEL))
		{
			var hashvo=new ParameterSet();
			hashvo.setValue("tabid",tabid);
			hashvo.setValue("taskid",taskid);
			hashvo.setValue("ins_id",ins_id);
			hashvo.setValue("node_id",node_id);
			hashvo.setValue("strXml",strXml);
			
			hashvo.setValue("oprflag","delLevel");
     		hashvo.setValue("levels",strLevels);
     	//	hashvo.setValue("levels",getEncodeStr(levels));
			var request=new Request({asynchronous:false,onSuccess:delLevelOk,functionId:'0570010165'},hashvo);	
		}
   	}
}	


function delLevelOk(outparamters)
{	strXml = outparamters.getValue("strXml");
	var leveldescs= outparamters.getValue("leveldescs");
	var leveldescObj =leveldescs.split(",");
	
    var curTable = document.getElementById("tblDefFlowSelf");	
    var index =0;

	var  obj = document.getElementsByName("levels");
	for(var i=document.defFlowSelfForm.elements.length-1;i>=0;i--)
	{			
   		if(document.defFlowSelfForm.elements[i].type=='checkbox'&&document.defFlowSelfForm.elements[i].name!="selbox")       
	  	{
	  		if(document.defFlowSelfForm.elements[i].checked)
  			{  	
  			    var tr = document.defFlowSelfForm.elements[i].parentNode.parentNode;
 			    var rowNum = tr.rowIndex;  					 
			    curTable.deleteRow(rowNum);
			}	
		}		
	}
	
    var levelnum =1;  
	for(var i=0;i<document.defFlowSelfForm.elements.length;i++)
	{			
   		if(document.defFlowSelfForm.elements[i].type=='checkbox'&&document.defFlowSelfForm.elements[i].name!="selbox")       
  		{	var tr = document.defFlowSelfForm.elements[i].parentNode.parentNode;
 			var rowNum = tr.rowIndex;	  		
		    if (leveldescObj.length>0) {
				var td1 =tr.cells[1];
				td1.setAttribute("align","left");
				td1.className = "RecordRow";
				td1.innerHTML = '&nbsp; <input type="hidden" name="levels" value="'+levelnum+'"/> '+leveldescObj[levelnum-1];
			
				var td3 =tr.cells[3];
				td3.setAttribute("align","center");
				td3.className = "RecordRow";
				td3.innerHTML = ' <img src="/images/edit.gif" onclick="javascript:addPerson(1,'+levelnum+')" ' 
				                  +'   border=0 style="cursor:hand" />';	
				
				levelnum++;				  
			}
		
		}
	}
}

    
 function addPerson(bs_flag,levelnum)
 { 
	var right_fields="";	
	var opt = 11;
	var infos=new Array();
	infos[0]="0";
	infos[1]="12";
	

   // var strurl="/performance/handSel.do?b_query=link`planid=`opt=12`flag0=2";
    var strurl="/performance/handSel.do?b_query=link`planid=`opt=12`flag0=2`flag=1`showDb=1`tabid=`selecttype=1`dbtype=0`priv=0`isfilter=0`loadtype=1";
	var iframe_url="/gz/gz_accounting/bankdisk/iframe_bank_disk.jsp?src="+strurl; 
	var objList=window.showModalDialog(iframe_url,infos,"dialogWidth=600px;dialogHeight=480px;resizable=yes;scroll=no;status=no;");  
    if(objList==null)
		return false;	

	if(objList.length>0)
	{
		   	for(var i=0;i<objList.length;i++)
		   	{
		   		right_fields+=","+objList[i];		   		
		   	}	
		  // 	alert(right_fields);
		   	
			var hashvo=new ParameterSet();
			hashvo.setValue("tabid",tabid);
			hashvo.setValue("taskid",taskid);
			hashvo.setValue("ins_id",ins_id);
			hashvo.setValue("node_id",node_id);
			hashvo.setValue("strXml",strXml);
			
			hashvo.setValue("oprflag","addPerson");
			hashvo.setValue("bs_flag",bs_flag);
			hashvo.setValue("levelnum",levelnum);     	
			hashvo.setValue("A0100s",right_fields);     	

			var request=new Request({asynchronous:false,onSuccess:addPersonOk,functionId:'0570010165'},hashvo);	
	}
	 
}

function addPersonOk(outparamters)
{	strXml = outparamters.getValue("strXml");
	var A0101Descs= outparamters.getValue("A0101Descs");
	var ids= outparamters.getValue("ids");
	var bs_flag= outparamters.getValue("bs_flag");
	var levelnum= outparamters.getValue("levelnum");
	var A0101DescsObj =A0101Descs.split("`");
	var idsObj =ids.split("`");
	if (idsObj.length<1) 
	  return;
    var curTable = document.getElementById("tblDefFlowSelf");	
    var rows = curTable.rows;
    var tr =null;
    if (bs_flag==1){
       tr = rows[levelnum];
    }
    else {
       tr = rows[rows.length-1];
    }    
    
	var td2 =tr.cells[2];
	td2.setAttribute("align","left");
	td2.className = "RecordRow";
	for (var i=0;i<idsObj.length;i++){	
		if (idsObj[i]=="undefined") continue;
		if (idsObj[i]=="") continue;
		td2.innerHTML = td2.innerHTML + "&nbsp;"+A0101DescsObj[i]+'<img src="/images/icon_fbyjs.gif" '
		               +' onclick="javascript:delPerson('+idsObj[i]+');" '
		               +' border=0 style="cursor:hand"> ';
	}	

}


 function delPerson(id)
 { 
    //alert(id);
	if(confirm(GENERAL_TEMPLATE_CONFIRM_DELSPPERSON))
	{		   	
		var hashvo=new ParameterSet();
		hashvo.setValue("tabid",tabid);
		hashvo.setValue("taskid",taskid);
		hashvo.setValue("ins_id",ins_id);
		hashvo.setValue("node_id",node_id);
		hashvo.setValue("strXml",strXml);
		
		hashvo.setValue("oprflag","delPerson");
		hashvo.setValue("id",id);     			

		var request=new Request({asynchronous:false,onSuccess:delPersonOk,functionId:'0570010165'},hashvo);	
	}
	 
}

function delPersonOk(outparamters)
{	strXml = outparamters.getValue("strXml");
	var A0101Descs= outparamters.getValue("A0101Descs");
	var ids= outparamters.getValue("ids");
	var bs_flag= outparamters.getValue("bs_flag");
	var levelnum= outparamters.getValue("levelnum");
	var A0101DescsObj =A0101Descs.split("`");
	var idsObj =ids.split("`");
    var curTable = document.getElementById("tblDefFlowSelf");	
    var rows = curTable.rows;
    var tr =null;
    if (bs_flag==1){
       tr = rows[levelnum];
    }
    else {
       tr = rows[rows.length-1];
    }  
      
	var td2 =tr.cells[2];
	td2.setAttribute("align","left");
	td2.className = "RecordRow";
	td2.innerHTML ="&nbsp";
	td2.innerTEXT ="";
	if (ids=="") {return;}
	for (var i=0;i<idsObj.length;i++){	
		if (idsObj[i]=="undefined") continue;
		if (idsObj[i]=="") continue;
		//alert(td2.innerHTML);
		td2.innerHTML = td2.innerHTML + "&nbsp;"+A0101DescsObj[i]+' <img src="/images/icon_fbyjs.gif" '
		               +' onclick="javascript:delPerson('+idsObj[i]+');" '
		               +' border=0 style="cursor:hand"> ';
	}	

}

    