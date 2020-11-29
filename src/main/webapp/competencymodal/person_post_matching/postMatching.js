function changeDegree()
{
   //object_id,postCode,planId,postScope
   personPostMatchingForm.action="/competencymodal/person_post_matching/person_post_matching.do?b_query=query&oper=1";
   personPostMatchingForm.submit();
}
function selectTree(codeid,mytarget)
{
  var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
    
    if(mytarget==null)
      return;
    var oldInputs=document.getElementsByName(mytarget);
   
    oldobj=oldInputs[0];
    target_name=oldobj.name;
    hidden_name=target_name.replace(".viewvalue",".value");
    var hiddenInputs=document.getElementsByName(hidden_name);
    if(hiddenInputs!=null)
    {
    	hiddenobj=hiddenInputs[0];
    	codevalue="";
    }
    codevalue="";
   var theArr=new Array(codeid,codevalue,oldobj,hiddenobj,3,0);
   thecodeurl="/system/codeselectposinputpos.jsp?codesetid="+codeid+"&codeitemid=&isfirstnode=1";
    var popwin= window.showModalDialog(thecodeurl, theArr, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no;");
   
   if(document.getElementById("postScope").value!=document.getElementById("postScopeID").value){
      var postScope = document.getElementById("postScopeID").value;
      var postScopeV = document.getElementById("postScopevID").value;
      document.getElementById("postScope").value=postScope;
      document.getElementById("postScopeDesc").value=postScopeV;
      personPostMatchingForm.action="/competencymodal/person_post_matching/person_post_matching.do?b_query=query&oper=1";
      personPostMatchingForm.submit();
   }
}
function queryObject(postCode,object_id,plan_id,objType)
{
	if(objType=="1")
	{
   	  	var oldValue=document.getElementById("postCode").value
	  	var trElement=document.getElementById(oldValue);
	  	if(trElement)
	  	{
	      	trElement.style.backgroundColor="";
	  	}
	  	trElement=document.getElementById(postCode);
	  	if(trElement)
	  	{
	     	trElement.style.backgroundColor="#FFF8D2";
	  	}
	  	document.getElementById("postCode").value=postCode;
	  	
   	}else if(objType=="2")
   	{
   	  	var oldValue=document.getElementById("object_id").value
  		var trElement=document.getElementById(oldValue);
  		if(trElement)
  		{
      		trElement.style.backgroundColor="";
  		}
  		trElement=document.getElementById(object_id);
  		if(trElement)
  		{
     		trElement.style.backgroundColor="#FFF8D2";
  		}
  		document.getElementById("object_id").value=object_id;
   	}
   	var isShowPercentVal = "";
  	if(document.getElementById('isShowLevel').checked)
		isShowPercentVal="2";
	else
		isShowPercentVal="0";
  	document.main.location="/competencymodal/person_post_matching/person_post_matching.do?b_chart=chart&planId="+plan_id+"&object_id="+object_id+"&postCode="+postCode+"&isShowPercentVal="+isShowPercentVal;
}
function hiddenElement()
{
	setTimeout("closeMenu()",500);	
}
function showMenu()
{
	var obj=document.getElementById('menu_');
	obj.style.display="block";
	obj.style.position="absolute";
	obj.style.posLeft=event.clientX;	
	obj.style.posTop=event.clientY;
	document.getElementById("menu_").focus();
}
function closeMenu()
{
	var obj=document.getElementById('menu_');
	obj.style.display="none";
}