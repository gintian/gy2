	/**
		 * 判断当前浏览器是否为ie6
		 * 返回boolean 可直接用于判断 
		 * @returns {Boolean}
		 */
		function isIE6() 
		{ 
			if(navigator.appName == "Microsoft Internet Explorer") 
			{
				if(navigator.userAgent.indexOf("MSIE 6.0")>0) 
				{ 
					return true;
				}else{
					return false;
				}
			}else{
				return false;
			}
		}
		
	//新增运算
	function add_budget(){	
		var zhuangTai=budgetAllocationForm.budgetStatus.value;
		if(zhuangTai==01||zhuangTai==05){
			alert(GZ_BUDGET_ALERT01);
	        return ;
		}
		target_url="/gz/gz_budget/budget_allocation/budget_allocation.do?b_add=link";
		target_url ='/general/query/common/iframe_query.jsp?src='+$URL.encode(target_url);
		var return_vo;
		if(isIE6()){
			return_vo=window.showModalDialog(target_url,null,"dialogWidth:469px; dialogHeight:254px;resizable:no;center:yes;scroll:no;status:no");
		}else{
			return_vo=window.showModalDialog(target_url,null,"dialogWidth:464px; dialogHeight:254px;resizable:no;center:yes;scroll:no;status:no");
		}
	    	if(return_vo==null)
			return ;
			
	    var vo=new Object();
	    	 vo.yearnum=return_vo.yearnum;
		     vo.budgettype=return_vo.budgettype;
		     vo.firstmonth=return_vo.firstmonth;
		     vo.bb203=return_vo.bb203;
	         var hashvo = new ParameterSet();
		     hashvo.setValue("vo",vo);
		     var request=new Request({asynchronous:false,onSuccess:add_budget_ok,functionId:'302001020502'},hashvo); 

    }
    function add_budget_ok(outparamters){

    	budgetAllocationForm.action="/gz/gz_budget/budget_allocation/budget_allocation.do?b_init=init";
    	budgetAllocationForm.target="il_body";
    	budgetAllocationForm.submit();
    }
    //撤销运算
    function delete_budget(){
    	var budget_id=budgetAllocationForm.budget_id.value;
    	var vo=new Object();
    	vo.budget_id=budget_id;
    	if(confirm(GZ_BUDGET_CONFIRM01)){
    	var hashvo = new ParameterSet();
    	hashvo.setValue("vo",vo);
    	var request=new Request({asynchronous:false,onSuccess:delete_budget_ok,functionId:'302001020507'},hashvo); 
    	}
    
    }
    function delete_budget_ok(outparamters){
        var tishi1="";
    	var tishi2="";
	    tishi1=outparamters.getValue("tishi1");
		tishi2=outparamters.getValue("tishi2");
		if(tishi1!=null){
			alert(tishi1);
			return;
		}
		if(tishi2!=null){
			alert(tishi2);
			return;
		}
		
    	budgetAllocationForm.action="/gz/gz_budget/budget_allocation/budget_allocation.do?b_init=init";
    	budgetAllocationForm.target="il_body";
    	budgetAllocationForm.submit();
    }
    
    //设置预算参数
    	function setParams_budget(){
 		var a_code=budgetAllocationForm.b0110.value;
		if(a_code==""||a_code.length==0){
			alert(GZ_BUDGET_SELECT);
			return;
		}
 	    var budgetid=budgetAllocationForm.budget_id.value;
    	var unitid=selectedUnit;// 全局变量
		var hashvo=new ParameterSet();
	    hashvo.setValue("budgetid",budgetid);
	    hashvo.setValue("unitid",unitid);
	    hashvo.setValue("flag","checkonly"); 
	    var request=new Request({asynchronous:false,onSuccess:setParams_budgetOk,functionId:'302001020514'},hashvo);	
 	}
 	function setParams_budgetOk(outparamters){
 	    var flag=outparamters.getValue("flag");
		var msg=outparamters.getValue("msg");
    	if(flag=="0")  
    	{
    		alert(msg);
    		return;
    	}
    	var b0110=budgetAllocationForm.b0110.value;
        var target_url="/gz/gz_budget/budget_allocation/budget_allocation/setParams.do?b_add=link`a_code="+b0110;
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
        window.showModalDialog(iframe_url,null,"dialogWidth:400px; dialogHeight:500px;resizable:no;center:yes;scroll:no;status:no"); //注意这种弹窗
	    
 	}
 	
 	
    // 预算分发
    function distributeBudget()
    {
    	var budgetid=budgetAllocationForm.budget_id.value;
    	var unitid=selectedUnit;// 全局变量
		var hashvo=new ParameterSet();
	    hashvo.setValue("budgetid",budgetid);
	    hashvo.setValue("unitid",unitid);
	    hashvo.setValue("flag","checkonly"); // 仅检查能否分发
	    var request=new Request({asynchronous:false,onSuccess:checkCanDistribute,functionId:'302001020508'},hashvo);	
    }
    
    // 检查是否能分发
    function checkCanDistribute(outparamters)
    {
	    var flag=outparamters.getValue("flag");
		var msg=outparamters.getValue("msg");
    	if(flag=="0")  // 不能分发
    	{
    		alert(msg);
    		return;
    	}
    	if(confirm(GZ_BUDGET_DISTRIBUTE_CONFIRM))
    	{
	    	var budgetid=budgetAllocationForm.budget_id.value;
			var unitid=selectedUnit;
			var hashvo=new ParameterSet();
		    hashvo.setValue("budgetid",budgetid);
		    hashvo.setValue("unitid",unitid);
		    hashvo.setValue("flag","distribute"); // 执行分发(同一个交易类)
		    var request=new Request({asynchronous:false,onSuccess:afterDistribute,functionId:'302001020508'},hashvo);	
    	}
    }
    
    function afterDistribute(outparamters)
    {
	    var flag=outparamters.getValue("flag");
		var msg=outparamters.getValue("msg");
    	if(flag=="0")// 分发失败
    	{
    		alert(msg);
    		return;
    	}
    	else
    	{   // 刷新机构树节点
    		var tree = parent.frames['mil_menu'].Global;
			var currnode=tree.selectedItem;
			if(currnode.load){
				while(currnode.childNodes.length){
					currnode.childNodes[0].remove();
				}
				currnode.load=true;
				currnode.loadChildren();
				currnode.reload(1);
			}
			if(currnode.uid!="root")
			{                
				var desc= "("+GZ_BUDGET_ALLOCATION_STATUS_DISTRIBUTE+")";  // 已发布 
				currnode.setText(currnode.title +desc);
			}

    		currnode.select();  // 触发事件，刷新表格数据
    		// 以下刷新会导致机构树收起来
	    	//budgetAllocationForm.action="/gz/gz_budget/budget_allocation/budget_allocation.do?b_init=init";
	    	//budgetAllocationForm.target="il_body";
	    	//budgetAllocationForm.submit();
    	}
    }
    
    // 预算退回
    function rejectBudget()
    {
    	var budgetid=budgetAllocationForm.budget_id.value;
    	var unitid=selectedUnit;// 全局变量
		var hashvo=new ParameterSet();
	    hashvo.setValue("budgetid",budgetid);
	    hashvo.setValue("unitid",unitid);
	    hashvo.setValue("flag","checkonly"); // 仅检查能否退回
	    var request=new Request({asynchronous:false,onSuccess:checkCanReject,functionId:'302001020509'},hashvo);	
    }
    
    // 检查是否能退回
    function checkCanReject(outparamters)
    {
	    var flag=outparamters.getValue("flag");
		var msg=outparamters.getValue("msg");
    	if(flag=="0")  // 不能退回
    	{
    		alert(msg);
    		return;
    	}
   		var tree = parent.frames['mil_menu'].Global;
		var currnode=tree.selectedItem;
		var root=currnode.root();
    	var s;
    	if(currnode==root||currnode.parent==root)
    		s = GZ_BUDGET_REJECTALL_CONFIRM;
    	else
    		s = GZ_BUDGET_REJECTONE_CONFIRM;
    	if(confirm(s))
    	{
	    	var budgetid=budgetAllocationForm.budget_id.value;
			var unitid=selectedUnit;
			var hashvo=new ParameterSet();
		    hashvo.setValue("budgetid",budgetid);
		    hashvo.setValue("unitid",unitid);
		    hashvo.setValue("flag","reject"); // 执行退回(同一个交易类)
		    var request=new Request({asynchronous:false,onSuccess:afterReject,functionId:'302001020509'},hashvo);	
    	}
    }
    
    function afterReject(outparamters)
    {
	    var flag=outparamters.getValue("flag");
		var msg=outparamters.getValue("msg");
    	if(flag=="0")// 退回失败
    	{
    		alert(msg);
    		return;
    	}
    	else
    	{   // 刷新机构树节点
    		var tree = parent.frames['mil_menu'].Global;
			var currnode=tree.selectedItem;
			var root=currnode.root();
			var refreshCurrNode=currnode.parent==root;
			if(refreshCurrNode)  // 选中节点为顶级单位节点，才需要刷新
			{
				var dmobj=g_dm["_2301"]; // 起草
				var desc="";
				if(dmobj!=null&&dmobj!="undefined")
		  			desc= "("+dmobj.V+")";
				currnode.setText(currnode.title +desc);
			}
			if(currnode.load){
				while(currnode.childNodes.length){
					currnode.childNodes[0].remove();
				}
				currnode.load=true;
				currnode.loadChildren();
				currnode.reload(1);
			}
    		currnode.select();  // 触发事件，刷新表格数据
    	}
    }

    // 新增预算单位
    function addBudgetUnit()
    {
    	var budgetid=budgetAllocationForm.budget_id.value;
    	var unitid=selectedUnit;// 全局变量
		var hashvo=new ParameterSet();
	    hashvo.setValue("budgetid",budgetid);
	    hashvo.setValue("unitid",unitid);
	    hashvo.setValue("flag","checkonly"); // 仅检查能否新增
	    var request=new Request({asynchronous:false,onSuccess:checkCanAdd,functionId:'302001020510'},hashvo);	
	    
	    // 检查是否能新增
	    function checkCanAdd(outparamters)
	    {
		    var flag=outparamters.getValue("flag");
			var msg=outparamters.getValue("msg");
	    	if(flag=="0")  // 不能
	    	{
	    		alert(msg);
	    		return;
	    	}

	    	var budgetid=budgetAllocationForm.budget_id.value;
			var unitid=selectedUnit;
			var hashvo=new ParameterSet();
		    hashvo.setValue("budgetid",budgetid);
		    hashvo.setValue("unitid",unitid);
		    hashvo.setValue("flag","add"); // 执行新增(同一个交易类)
		    var request=new Request({asynchronous:false,onSuccess:afterAdd,functionId:'302001020510'},hashvo);	
	    }	    

	    function afterAdd(outparamters)
	    {
		    var flag=outparamters.getValue("flag");
			var msg=outparamters.getValue("msg");
	    	if(flag=="0")// 失败
	    	{
	    		alert(msg);
	    		return;
	    	}
	    	else
	    	{   // 刷新机构树节点
	    		var tree = parent.frames['mil_menu'].Global;
				var currnode=tree.selectedItem;
				             // _+代码类+代码
				var dmobj=g_dm["_2301"];   // 起草 
				var desc="";
				if(dmobj!=null&&dmobj!="undefined")
		  			desc= "("+dmobj.V+")";
				currnode.setText(currnode.title +desc);  // TODO 点击下级节点的加号,会出现空白行
				currnode.select();  // 触发事件，刷新表格数据
				
	    	}
	    }
    }
    
    // 返回表格中选中的单位编号(逗号分隔)
    function getSelectUnits()
    {
        var table=$(dataset_tableid);  // 全局变量
        var dataset=table.getDataset();
        var record=dataset.getFirstRecord();
        var units="";	
    	while (record) 
    	{
	     	if (record.getValue("select"))
    		{							
	     		units+=record.getValue("b0110")+",";	    
    		}
    		record=record.getNextRecord();
	   	}  	
   	    return units;
    }
    
    // 删除预算单位
    function delBudgetUnit()
    {
    	var budgetid=budgetAllocationForm.budget_id.value;
    	var units=getSelectUnits();
   	    if(units=="")
    	{
  	    	alert(GZ_BUDGET_ALLOCATION_DEL_UNIT_HINT1);
   	   		return;
   	    }
		var hashvo=new ParameterSet();
	    hashvo.setValue("budgetid",budgetid);
	    hashvo.setValue("units",units);
	    hashvo.setValue("flag","checkonly"); // 仅检查能否删除
	    var request=new Request({asynchronous:false,onSuccess:checkCanDel,functionId:'302001020511'},hashvo);	

	    // 检查是否能删除
	    function checkCanDel(outparamters)
	    {
		    var flag=outparamters.getValue("flag");
			var msg=outparamters.getValue("msg");
	    	if(flag=="0")  // 不能
	    	{
	    		alert(msg);
	    		return;
	    	}

	    	if(confirm(GZ_BUDGET_DEL_UNIT_CONFIRM))
	    	{
		    	var budgetid=budgetAllocationForm.budget_id.value;
				var unitid=selectedUnit;
				var hashvo=new ParameterSet();
			    hashvo.setValue("budgetid",budgetid);
			    hashvo.setValue("units",units);
			    hashvo.setValue("flag","del"); // 执行新增(同一个交易类)
			    var request=new Request({asynchronous:false,onSuccess:afterDel,functionId:'302001020511'},hashvo);
			}
	    }	    

	    function afterDel(outparamters)
	    {
		    var flag=outparamters.getValue("flag");
			var msg=outparamters.getValue("msg");
	    	if(flag=="0")// 失败
	    	{
	    		alert(msg);
	    		return;
	    	}
	    	else
	    	{   // 刷新机构树节点
	    		var tree = parent.frames['mil_menu'].Global;
				var currnode=tree.selectedItem;
				if(currnode.load){
					while(currnode.childNodes.length){
						currnode.childNodes[0].remove();
					}
					currnode.load=true;
					currnode.loadChildren();
					currnode.reload(1);
				}
	    		currnode.select();  // 触发事件，刷新表格数据	    	    	
	    		currnode.setText(currnode.title);
			}
	    }
    }        