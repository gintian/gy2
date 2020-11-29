/**
 * 
 */
function changeQuery(flag){
	
	workPlanTeamForm.action="/performance/workplan/workplanteam/workplan_team_list.do?b_query=link&flag="+flag;
	workPlanTeamForm.submit();
}
//全选
function operateCheckBox(obj)
   {
   		var value=obj.checked;
   		for(var i=0;i<document.workPlanTeamForm.elements.length;i++)
   		{
	   		
   			if(document.workPlanTeamForm.elements[i].type=='checkbox'&&document.workPlanTeamForm.elements[i].name.length>18&&document.workPlanTeamForm.elements[i].name.substring(0,18)=='pagination.select[')
   				document.workPlanTeamForm.elements[i].checked=value;
   		
   		}
   		if(obj.checked)
	   		document.workPlanTeamForm.isSelectedAll.value="1";
   	    else
   			document.workPlanTeamForm.isSelectedAll.value="0";
   
   }
//未填的全选
function noFillOperateCheckBox(obj,name)
  {
        
        batch_select(obj,name);
        if(obj.checked)
	   		document.workPlanTeamForm.isSelectedAll.value="1";
   	    else
   			document.workPlanTeamForm.isSelectedAll.value="0";
  }


