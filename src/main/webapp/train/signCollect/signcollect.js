function checkTime(times){
 	var result=times.match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/);
 	if(result==null) return false;
 	var d= new Date(result[1], result[3]-1, result[4]);
 	return (d.getFullYear()==result[1]&&(d.getMonth()+1)==result[3]&&d.getDate());
}
function timeCheck(obj){
	if(!checkTime(obj.value)){
		obj.value='';
	}
}
function loadclass(loadclass,courseplan){
		var hashvo=new ParameterSet(); 
		var class_id=document.getElementById("classplan").value;
		
		hashvo.setValue("classplan",document.getElementById("classplan").value);
		hashvo.setValue("flag","1"); 
		hashvo.setValue("loadclass",loadclass);
		hashvo.setValue("courseplan",courseplan);
    	var request=new Request({method:'post',onSuccess:showSelectOk,functionId:'2020020234'},hashvo);
}
function showSelectOk(outparamters){
	if(outparamters){
		var csp=document.getElementById("courseplan");
		var load=outparamters.getValue("loadclass");
		csp.options.length = 0;
		var value1=outparamters.getValue("value");
		var text1=outparamters.getValue("text");
		var courseplan=outparamters.getValue("courseplan");
		if(value1!=null&&value1.length>0){
			var val1s=value1.split(",");
			var txs=text1.split(",");
			for(var i=0;i<val1s.length;i++){			    
				var varItem = new Option(txs[i],val1s[i]);
				csp.options.add(varItem);				
				if(val1s[i]==courseplan)
					csp.options[i].selected=true;
			}							
			if(load=="true")
			 searchinfo();	
		}
		
	}
}
function searchinfo()
{
    var obj=document.getElementById("courseplan");
    var csp=obj.value;       
    trainAtteForm.action="/train/signCollect/signcollect.do?b_search=link";
    trainAtteForm.submit();
}
function change(flag)
{
   if(flag=='nn')
      trainAtteForm.action="/train/signCollect/signcollect.do?b_query=link&classplan="
   else
     trainAtteForm.action="/train/signCollect/signcollect.do?b_query=link";
   trainAtteForm.submit();
}
function collectData(){
   var obj=document.getElementById("sort");
   var sort=obj.value;  
   if(sort=="2")
   {
      var obj=document.getElementById("classplan");
      var classplan=obj.value;            
      var hashvo=new ParameterSet();
      hashvo.setValue("classplan",classplan);	 
      hashvo.setValue("sort",sort);     
      var request=new Request({method:'post',onSuccess:otherCollect,functionId:'20200203005'},hashvo);     
   }else
   {
      if(sort=="1")
      {
        var waitInfo=eval("wait");	   
        waitInfo.style.display="block";
        trainAtteForm.action="/train/signCollect/signcollect.do?b_collect=link";
        trainAtteForm.submit();
      }else if(sort=="3")
      {
         change();
      }
   }   
}
function otherCollect(outparamters)
{
   var tes=outparamters.getValue("type");
   if(tes=="ok")
   {
      var waitInfo=eval("wait");	   
      waitInfo.style.display="block";
      trainAtteForm.action="/train/signCollect/signcollect.do?b_collect=link";
      trainAtteForm.submit();
   }else
   {
      alert("请先汇总人员签到数据！");
      return false;
   }
}
function selectTerm(){
	var dh="360px";
	if(navigator.appVersion.indexOf('MSIE 6') != -1){
		dh="410px";
	}
    var sort=document.getElementById("sort").value;
    var thecodeurl ="/train/attendance/trainsearch.do?b_search=link&t_type=r47&sort="+sort;
    var return_vo= window.showModalDialog(thecodeurl, "", 
             	"dialogWidth:700px; dialogHeight:360px;resizable:no;center:yes;scroll:no;status:no");
    if(return_vo!=null&&return_vo.length>0)
    {
	    document.getElementById("search").value=return_vo;
	    if(sort=="1"){
	    	trainAtteForm.action="/train/signCollect/signcollect.do?b_search=link";
	    }else{
	       trainAtteForm.action="/train/signCollect/signcollect.do?b_query=link";
	    }
	   	trainAtteForm.submit();
    }
}
function showExportInfo(outparamters){
	if(outparamters){
		var name=outparamters.getValue("filename");
		MusterInitData();
		window.location.target="_blank";
		window.location.href="/servlet/vfsservlet?fromjavafolder=true&fileid="+name;
	}
}
function MusterInitData()
{
	   var waitInfo=eval("wait");	
	   waitInfo.style.display="none";
 }
 
 function timeFlagChange(obj){
	var timeflag = obj.value;
	if(timeflag=='04'){
		toggles("viewtime");
		return false;
	}else{
		document.getElementById("startime").value="";
		document.getElementById("endtime").value="";
		change('nn');			
	}
}
function IsDigit() 
{ 
    return ((event.keyCode >= 46) && (event.keyCode <= 57)); 
}
function checkValue(obj,item,nbase,a0100)
{
      check_obj=obj;
      var values=obj.value;      
      var obj=document.getElementById("courseplan");
      var courseplan=obj.value;  
      var hashvo=new ParameterSet();
      hashvo.setValue("value",values);
      hashvo.setValue("item",item);
      hashvo.setValue("nbase",nbase);
      hashvo.setValue("a0100",a0100);  
      hashvo.setValue("courseplan",courseplan);      
      var request=new Request({method:'post',asynchronous:false,onSuccess:showCheckFlag,functionId:'20200203007'},hashvo);
 } 
 function showCheckFlag(outparamters)
 {
      var check_flag=outparamters.getValue("flag");
      var check_mess=outparamters.getValue("mess");
      if(check_flag=="false")
      {
         alert(check_mess);         
         //check_obj.focus(); 
      }
 }