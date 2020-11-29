/*
显示角色用户，我的申请、任务监控使用
*/
function displayRoleInfo(parentobj,tabId,taskId) {
    //取得显示的位置
    var left =getElementLeft(parentobj)+26-400-26-20;
    var top =getElementTop(parentobj)+parentobj.offsetHeight-50;
    top=top+"px";
    left=left+"px";
    
    var roleInfoDivContent=document.getElementById("roleInfoDivContent");
    roleInfoDivContent.style.display ="none"; 
    //显示等待信息
    var waitDiv=document.getElementById("roleInfoDivWait");
    waitDiv.style.display ="block";
    var roleInfoDiv=document.getElementById("roleInfoDiv");
    roleInfoDiv.style.top=top;
    roleInfoDiv.style.left=left;   
    roleInfoDiv.style.display ="block";
    var task_right=document.getElementById("task_right");
    task_right.style.top="30px";
    task_right.style.left="399px";   
    task_right.style.display ="block";
    
    var hashvo = new ParameterSet();
    hashvo.setValue("tabId",tabId);   
    hashvo.setValue("taskId", taskId);
    var request=new Request({method:'post',asynchronous:true,
          onSuccess:displayRole_ok,functionId:'0570010159'},hashvo);
   
}

   function displayRole_ok(outparamters) {
       var approvePeople = outparamters.getValue("approvePeople");
       approvePeople=getDecodeStr(approvePeople);
       var box=document.getElementById("role_div_people");
       box.innerHTML=approvePeople;
       var approveContent = outparamters.getValue("approveContent");
       approveContent=getDecodeStr(approveContent);
       var box=document.getElementById("role_div_content");
       box.innerHTML=approveContent;
       
      var waitDiv=document.getElementById("roleInfoDivWait");
      waitDiv.style.display ="none";
      
      var roleInfoDivContent=document.getElementById("roleInfoDivContent");
      roleInfoDivContent.style.display ="block";
 }  

function hideDiv(obj){
    document.getElementById(obj).style.display="none";
}

function getElementLeft(element){
    var actualLeft = element.offsetLeft;
    var current = element.offsetParent;
    while (current !== null){
        actualLeft += current.offsetLeft;
        current = current.offsetParent;
    }
    return actualLeft;
}
function getElementTop(element){
    var actualTop = element.offsetTop;
    var current = element.offsetParent;
    while (current !== null){
        actualTop += current.offsetTop;
        current = current.offsetParent;
    }
    return actualTop;
}
