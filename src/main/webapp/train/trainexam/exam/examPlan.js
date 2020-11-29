function addExamPlan(model,a_code)
{
	var initValue='';
	var readonlyFilds='';
	var hideFilds ='';
	var hidepics = '';
	
	var date = new Date(); 
	var currentYear = date.getFullYear();
	var currentMonth = date.getMonth() + 1;
	var currentDay = date.getDate();
	var now = currentYear + '-' + currentMonth + '-' + currentDay;

  if(model=='1')
    initValue='R5411:01';
  else if(model=='2')
    initValue='R5411:03,r2503:'+currentYear+',r2508:'+now;
    	
	readonlyFilds = 'B0110,R5300,';
	hideFilds ='create_user,create_time,';
  hidepics = '';
	isUnUmRela='true';
	
  var theurl="/train/trainexam/exam/plan.do?b_edit=link`fieldset=R54`a_code=" 
           +a_code+'`initValue='+getEncodeStr(initValue)
           +'`readonlyFilds='+readonlyFilds
           +'`hideFilds='+hideFilds
           +'`hidepics='+hidepics
           +'`isUnUmRela='+isUnUmRela;
  trainExamPlanForm.action=theurl;
  trainExamPlanForm.submit();
/*         		
  if(return_vo==null)
		return false;	 
		  
  if(return_vo.flag=="true")
	{   
    trainExamPlanForm.action="/train/trainexam/exam/plan.do?b_query=link";
    trainExamPlanForm.submit(); 
	} */    					
}

