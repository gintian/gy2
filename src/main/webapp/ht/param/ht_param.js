function saveDB()
{
	var dbs = document.getElementsByName('db');
	var dbstr='';
	if(dbs)
	{		
		if(dbs.length)
		{
				for(var i=0;i<dbs.length;i++)
				{
					if(dbs[i].checked==true)
						dbstr+=','+dbs[i].value;	
				}
		}
		else
		{
			if(dbs.checked==true)
				dbstr+=','+dbs.value;	
		}
	}
	contractParamForm.paramStr.value=dbstr.substring(1);
	contractParamForm.action='/ht/param/ht_param_db.do?b_save=link&menuid=1';
	contractParamForm.submit();
}
function saveEmpIndex()
{
	var emps = document.getElementsByName('emp');
	var empstr='';
	if(emps)
	{		
		if(emps.length)
		{
				for(var i=0;i<emps.length;i++)
				{
					if(emps[i].checked==true)
						empstr+=','+emps[i].value;	
				}
		}
		else
		{
			if(emps.checked==true)
				empstr+=','+emps.value;	
		}
	}
	contractParamForm.paramStr.value=empstr.substring(1);
	contractParamForm.action='/ht/param/ht_param_empindex.do?b_save=link&menuid=2';
	contractParamForm.submit();
}
function saveHtSet()
{
	setselectitem('right_fields');
	contractParamForm.action='/ht/param/ht_param_htset.do?b_save=link&menuid=3';
	contractParamForm.submit();
}
/*选择所有选项*/
function checkAll(){
	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++){
		if(tablevos[i].type=="checkbox"){
			tablevos[i].checked=true;
      	 }
   	}
}
/*清除所有选项*/
function clearAll(){
	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++){
		if(tablevos[i].type=="checkbox"){
			tablevos[i].checked=false;
      	 }
   	}
}

/*合同子集发生变化，代码类变化*/
function changeSet() {
	setselectitem('right_fields');
	contractParamForm.action='/ht/param/ht_param_htset.do?b_query=link&menuid=3';
	contractParamForm.submit();
}