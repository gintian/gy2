/**
 * 
 */
function changeQuery()
{	
	workPlanViewForm.action="/performance/workplan/workplanview/workplan_view_list.do?b_query=link&searchflag=view";
	workPlanViewForm.submit();
}
//log_type=1计划=2总结
//opt =0查看 =1填报
function writePlan(opt,nbase,a0100,log_type,p0100,state,p0115,year_num,quarter_num,month_num,week_num,day_num)
{
	workPlanViewForm.action="/performance/workplan/workplanview/workplan_view_list.do?b_write=write&mdopt="+opt+"&mdnbase="+nbase+"&mda0100="+a0100+"&log_type="+log_type+"&mdp0100="+p0100+"&state="+state+"&p0115="+p0115+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num;
	workPlanViewForm.submit();
}

// 如果是中文则代表两个字节
String.prototype.realLength = function() 
{ 
	return this.replace(/[^\x00-\xff]/g, "**").length; 
} 

var maxRow=12;//记录最多计划条数
var row_max_num=new Array();
function scanKey(obj,row,row_num)
{
	
	var keyCode = window.event.keyCode;
	var value = obj.value;
	
//	alert(keyCode+"--------------"+value.length);
	
	if(row>=maxRow && keyCode==13 && row==maxRow)//大于等于12格外新增加一行
	{
		var tabobj=document.getElementById("tabPut");
    	var newTrIndex=tabobj.rows.length;
    	//添加一行
    	var newTr = tabobj.insertRow(newTrIndex);
     	newTr.id=(row+1)+"_1";
     	row_max_num[row]=1;
    	//添加两列
    	var newTd0 = newTr.insertCell(0);
    	var newTd1 = newTr.insertCell(1);
    	//设置列内容和属性
    	maxRow++;
    	newTd0.innerText= PERFORMANCE_WORKPLAN_WORKPLANVIEW_DI+(row+1)+PERFORMANCE_WORKPLAN_WORKPLANVIEW_TIAO;
    	newTd0.className = "RecordRow_big";
    	newTd0.align="right";
    	newTd0.width="60";
    	newTd1.className = "RecordRow_big";
    	newTd1.innerHTML = " <input type=\"text\" class=\"TEXT_big\" size=\"120\" id=\""+(row+1)+"_1_put\" name=\"name_"+(row+1)+"\" onkeydown=\"scanKey(this,"+(row+1)+",1);\" maxlength=\"132\"/>";
    	
    	//if(keyCode!=13)
        	//document.getElementById((row+1)+"_1_put").focus();
	}
	else if(keyCode==13 || (value.realLength()>=115 && keyCode!=8 && keyCode!=46 && keyCode!=37 && keyCode!=38 && keyCode!=39 && keyCode!=40))
	{
    	var tabobj=document.getElementById("tabPut");
    	var newTrIndex=0;
    	var tabrows=tabobj.rows;
    	for(var i=0;i<tabrows.length;i++){
    		if(tabrows[i].id==(row+"_"+row_num))
    			newTrIndex=(i+1);
    	}
    	//添加一行
    	
    	var newTr = tabobj.insertRow(newTrIndex);
     	newTr.id=row+"_"+(row_max_num[row-1]+1);
    	//添加两列
    	var newTd0 = newTr.insertCell(0);
    	var newTd1 = newTr.insertCell(1);
    	//设置列内容和属性
    	newTd0.className = "RecordRow_big";
    	newTd0.innerText= '  ';
    	newTd1.className = "RecordRow_big";
    	newTd1.innerHTML = " <input type=\"text\" class=\"TEXT_big\" size=\"120\" id=\""+row+"_"+(row_max_num[row-1]+1)+"_put\" name=\"name_"+row+"\" onkeydown=\"scanKey(this,"+row+","+(row_max_num[row-1]+1)+");\" maxlength=\"132\"/>";
    	if(keyCode!=13)
        	document.getElementById(row+"_"+(row_max_num[row-1]+1)+"_put").focus();
    	row_max_num[row-1]++;
	}
	else if(keyCode==8 && value.length<=0)
	{
		
		
		
	}
	
}

// 保存或报批 flag=1 保存 flag=2 报批
function savePlan(curr_user,flag,target,pendingCode)
{
	var log_type = document.getElementById("log_type").value;		
	var allStr = "";	
	if(log_type!=null && log_type=='1')
	{
		for(var i=1;i<=maxRow;i++)
		{
			var everyStr="";
			var everyRowArr=document.getElementsByName("name_"+i);
			for(var j=0;j<everyRowArr.length;j++)
			{
				if(everyRowArr[j].value!=null && everyRowArr[j].value!='')
				{
					everyStr+=everyRowArr[j].value;
					if(j!=everyRowArr.length-1)
						everyStr+="\r\n";
				}
			}
			if(everyStr!="")
				allStr+="≡"+everyStr;
		}
	}else
		allStr="≡"+document.getElementById("SummaryStrid").value;
	
	
	
	getA0101(curr_user,flag,allStr,pendingCode);
	
		
	/*if(flag==2)
	{ 
		if(confirm("确认报批吗？"))
		{
			
		}else
		{
			flag = 1 ;
			return;
		}		
	}
		
	if(flag==1)
	{
		document.getElementById("planContent_Str").value=allStr.substring(1);
		workPlanViewForm.action="/performance/workplan/workplanview/workplan_view_list.do?b_save=save&flag="+flag+"&appbody_id="+curr_user;
		workPlanViewForm.submit();
	}else
	{
	   document.getElementById("planContent_Str").value=allStr.substring(1);
	   workPlanViewForm.action="/performance/workplan/workplanview/workplan_view_list.do?b_backtomain=link&flag="+flag+"&appbody_id="+curr_user+"&isBack=1";				 		
	   workPlanViewForm.submit();
	}*/
}

function getA0101(curr_user,flag,allStr,pendingCode){
     var hashvo = new ParameterSet();
     hashvo.setValue("allStr",getEncodeStr(allStr));
     hashvo.setValue("flag",flag);
     hashvo.setValue("pendingCode",pendingCode);
     hashvo.setValue("curr_user",getEncodeStr(curr_user));
     var request=new Request({method:'post',asynchronous:false,onSuccess:return_ok,functionId:'90100170062'},hashvo);
}
function return_ok(outparamters){
     var a0101 = outparamters.getValue("a0101");
     var curr_user = outparamters.getValue("curr_user");
     curr_user = getDecodeStr(curr_user);
     var flag = outparamters.getValue("flag");
     var allStr = outparamters.getValue("allStr");
     var pendingCode = outparamters.getValue("pendingCode");
     allStr = getDecodeStr(allStr);
     if(flag==2)
     {
       if(confirm(PERFORMANCE_WORKPLAN_WORKPLANVIEW_CONFIRM01+a0101+PERFORMANCE_WORKPLAN_WORKPLANVIEW_CONFIRM02)){
        
       }
       else
	   {
		flag = 1 ;
		return;
	   }
     }
     if(flag==1)
	{
		document.getElementById("planContent_Str").value=allStr.substring(1);
		workPlanViewForm.action="/performance/workplan/workplanview/workplan_view_list.do?b_save=save&flag="+flag+"&appbody_id="+curr_user;
		workPlanViewForm.submit();
	}else
	{
	   var isBack=1;
	   if(pendingCode!=null && pendingCode!="" && pendingCode!='null'){
	     isBack=0;
	     document.getElementById("doneFlag").value="1";
	   }
	   document.getElementById("planContent_Str").value=allStr.substring(1);
	   workPlanViewForm.action="/performance/workplan/workplanview/workplan_view_list.do?b_backtomain=link&flag="+flag+"&appbody_id="+curr_user+"&isBack="+isBack;				 		
	   workPlanViewForm.submit();
	}
     
}

function init_row_max_num(helpScript)
{
	//row_max_num
	
	var arr=helpScript.split(":");
	maxRow=arr.length;
	for(var i=1;i<=arr.length;i++)
	{
		row_max_num[i-1]=arr[i-1];
	}
}
function isValidDate(day, month, year) 
{
    if (month < 1 || month > 12) 
    {
    	return false;
    }
    if (day < 1 || day > 31) {
        return false;
    }
    if ((month == 4 || month == 6 || month == 9 || month == 11) &&
            (day == 31)) {
            return false;
    }
    if (month == 2) 
    {
    	var leap = (year % 4 == 0 &&
                       (year % 100 != 0 || year % 400 == 0));
        if (day>29 || (day == 29 && !leap)) {
                return false;
        }
    }
    return true;
}
    
function upload()
{
	var strPath = document.workPlanViewForm.formFile.value;	
	// 防止上传漏洞
	var isRightPath = validateUploadFilePath(strPath);
	if(!isRightPath)	
		return;
	if(trim(document.workPlanViewForm.fileName.value).length==0)
	{		
		if(strPath==null || trim(strPath).length==0)
			return;		
	}	
	document.workPlanViewForm.action="/performance/workplan/workplanview/workplan_view_list.do?b_saveattach=save&opt=1";
	document.workPlanViewForm.submit();
}
function selectCopyTo(p0100,workNbase)
{
	/*var target_url="/selfservice/lawbase/add_law_text_role.do?b_relating1=link&pri=0&chkflag=11&p0100="+p0100+"&nbase="+workNbase;
	
    var return_vo= window.showModalDialog(target_url, 'trainClass_win2', 
		      				"dialogWidth:600px; dialogHeight:430px;resizable:no;center:yes;scroll:yes;status:yes");*/
    //var return_vo = select_org_emp_dialog(1,1,1,0,0,1);
	var return_vo = select_org_emp_dialog2_jh("1","1","1","0","0","1","");		      	      					
	if(return_vo!=null)
	{
		var recordstr = "";
		/*for(var i=0;i<return_vo.length;i++)
		{
			if(return_vo[i]!=null&&return_vo[i].length>0){
				recordstr+=return_vo[i]+"`";
			}
		}*/
		recordstr = return_vo.content.replaceAll(",","`");
		var hashvo=new ParameterSet();
		hashvo.setValue("personstr",recordstr);
		var request=new Request({method:'post',onSuccess:changeOK,functionId:'90100170053'},hashvo);
	}
}
String.prototype.replaceAll = function(s1,s2) { 
    return this.replace(new RegExp(s1,"gm"),s2); 
}
function changeOK(outparameters)
{
	var a0100=outparameters.getValue("a0100");
	var name=outparameters.getValue("name");
	document.getElementById("ctn").value=name;
	document.getElementById("cts").value=a0100;
}


function hides(hide1,hiede2,hide3)
{
	//Element.hide(hide1);
	//Element.hide(hide3);
	//Element.toggle(hiede2);
	document.getElementById(hide1).style.display='none';
	document.getElementById(hide3).style.display='none';
	document.getElementById(hiede2).style.display='';
}
function toggles(toggles1,toggles2,toggles3)
{
	//Element.toggle(toggles1);
	//Element.toggle(toggles3);
	//Element.hide(toggles2);
	document.getElementById(toggles1).style.display='';
	document.getElementById(toggles3).style.display='';
	document.getElementById(toggles2).style.display='none';
}
//2016/1/27 wangjl撤回 
function recall(){
	 if(confirm("您确定撤回吗？")){
		 workPlanViewForm.action="/performance/workplan/workplanview/workplan_view_list.do?b_save=link&flag=3&mdopt=MQ~3d~3d";				 		
		 workPlanViewForm.submit();
	 }
}

//2016/1/27 wangjl查询
function search(){
	workPlanViewForm.action="/performance/workplan/workplanview/workplan_view_list.do?b_query=link&flag=3&searchflag=view";				 		
	workPlanViewForm.submit();
}



