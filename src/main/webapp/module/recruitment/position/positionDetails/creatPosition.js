var Global = new Object();
Global.textAreas="";

/****
 * 选择招聘批次时将查询批次对应的信息
 * @param {} id
 */
Global.getBatch = function(id){
	var map = new HashMap();
    map.put("batchId",id);
    Rpc({
		functionId : 'ZP0000002083',
		success :Global.stuffBatchInfo
	}, map);
}
/***
 * 填充批次对应信息
 * @param {} outparamters
 */
Global.stuffBatchInfo = function(outparamters)
{
	var param = Ext.decode(outparamters.responseText);
	var z0151 = param.z0151;//招聘渠道
	var z0153 = param.z0153;//招聘流程
	var z0107 = param.z0107;//招聘开始时间
	var z0109 = param.z0109;//招聘结束时间
	var codeZ0151 = param.codeZ0151;//渠道名称
	
	if(Ext.getCmp("z0101").getValue()=="")
	{
		return;
	}
	if(z0107!="")
	{
		//设置招聘开始时间
		if(Ext.getDom("z0329")!=null)
			Ext.getDom("z0329").value=z0107;
	}
	if(z0109!=""){
		//设置招聘结束时间
		if(Ext.getDom("z0331")!=null)
			Ext.getDom("z0331").value=z0109;
	}
	//选批次时暂不处理渠道
	if(z0151!=""){
		//设置招聘渠道
		if( document.getElementById("z0336")){
		    document.getElementById("z0336").value = z0151;
		    document.getElementById("z0336_view").value = codeZ0151;
		}
	}
	if(z0153!=""){
		//设置招聘流程
		if(Ext.getDom("z0381")!=null)
			Ext.getCmp("z0381").setValue(z0153);
	}
}
/******
 * 清除部门数据
 * @param {} obj
 */
Global.clears =function(){
	var z0321 = Ext.getDom("z0321").value;
	if(Ext.getDom("z0325"))
		Ext.getDom("z0325").value="";
	if(Ext.getDom("z0325value"))
		Ext.getDom("z0325value").value="";
	if(z0321=="")
	{
    	Ext.getDom("z0325aaa").setAttribute("parentid","");
	}
}
/***
 * 查询上级单位
 * @param {} obj
 */
Global.parentCodeId = function(codeId,codeDesc)
{
    var map = new HashMap();
    map.put("codeitemId",codeId);
    Rpc({
		functionId : 'ZP0000002309',
		success :Global.returnValue
	}, map);
}
/****
 * 给下级节点赋值
 * @param {} obj
 */
Global.childCodeId = function(codeId,codeDesc)
{
	Global.clears();
	if(Ext.getDom("z0325aaa")){
	    Ext.getDom("z0325aaa").removeAttribute("parentid");
	    Ext.getDom("z0325aaa").setAttribute("parentid",codeId);
	}
}
//通过传回的值进行动态填充
Global.returnValue = function(outparamters){
	var param = Ext.decode(outparamters.responseText);
    var codeSet = param.getCodeSet;
    var codeUN = param.codeUNName.split("`");
    var codeId = codeUN[0];
    var codeDesc = codeUN[1];
    if(codeDesc == undefined)
    	codeDesc ="";
    Ext.getDom("z0321").value=codeId;
    Ext.getDom("z0321value").value=codeDesc;
}

Global.getSubject = function(obj,itemId)
{
	if(obj.checked)
	{
		Ext.getDom("z0101hide").style.display = "";
		Ext.getDom(itemId+"panl").style.display = "";
	}else{
		Ext.getDom(itemId+"panl").style.display = "none";
	}
}
//生成新建页面
Global.createpageHtml = function(jsonStr){    
    var obj= eval(jsonStr);
    var datafield = obj.datafield;
    var pageData = obj.pageData;
    var elem1 = Ext.getDom('divf');
    elem1.parentNode.style.width="90%";
    elem1.style.marginLeft=(document.body.clientWidth)*0.1+"px";
    elem1.parentNode.style.padding="10px";
    var elem2 = Ext.getDom('choosePesron');
    var selectIds = new Array();
    var dataIds = new Array();
    var umids = new Array();
    var deepids = new Array();
    for ( var i = 0; i < datafield.length; i++) {
        var tablemap = datafield[i];
        var tablevalue = eval("pageData.table"+i);
        var elem3=document.createElement("div");
        var elem4 = document.createElement("div");
        
        elem3.setAttribute("id","table"+i);
        
        elem3.className = "hj-zm-cj-one";
        elem4.className = "hj-zm-cj-zwmc";
        elem3.innerHTML="<h2>　"+tablevalue+"</h2>";
        
        var fieldlist = eval("tablemap.table"+i);
        var table = document.createElement("table");
        table.setAttribute("width", "100%");
        table.setAttribute("border", "1");
        table.setAttribute("cellpadding", "0");
        if(navigator.userAgent.indexOf("MSIE") != -1) {
        	table.setAttribute("cellspacing", "0");
        }else{
        	table.setAttribute("cellspacing", "5");
        	
        }
        
       // td.innerHTML="aaa:<input type='text' ></input>";
        for ( var j = 0; j < fieldlist.length; j++) {
			var fieldkey = fieldlist[j];
			field = eval("pageData."+fieldkey);
			if(field!=null){
			    if(fieldkey.lastIndexOf("hide")>0)
                    continue;
			    
				var tr = table.insertRow(table.rows.length);
				var td3 = tr.insertCell(tr.cells.length);
				td3.className="vertical";
                td3.width="60px";
                td3.align="left";
				var td1 = tr.insertCell(tr.cells.length);
				var td2 = tr.insertCell(tr.cells.length);
				if(i==2){
				    td2.style.paddingLeft="28px";
	                td2.style.border="none";
                    td2.className = "hj-zm-cj-xqbmz";
                }else{
                    td2.style.paddingLeft="50px";
                    td2.style.border="none";
                    td2.className = "hj-zm-cj-xqbmz";
                }
				td1.className="vertical";
				td1.width="60px";
				td1.align="left";
				var id = field.id;
				var type = field.type;
				var desc =field.desc;
				var codeId =field.codeId;
				var level =field.level;
				var codelist = field.codelist;
				var required = field.required;
				var length = field.itemLength;
				//console.log(id+"---"+type+"---"+ desc+"---"+ codeId+"---"+ level+"---"+ codelist+"---"+ required+"---"+ length)
				if(fieldkey.lastIndexOf("hide")>0){
					//td1.innerHTML="<input type='hidden' id='"+id+"' />";
				}else{
					if("y"==required){
						td3.innerHTML="<font color='red' style='white-space:nowrap;'>*&nbsp;</font>";
					}
					if("z0101"==id.toLowerCase()){
						if("y"==required)
							td3.innerHTML="<font color='red' id='z0101hide' style='white-space:nowrap;'>*&nbsp;</font>";
						else
							td3.innerHTML="<font color='red' id='z0101hide' style='white-space:nowrap;display:none;'>*&nbsp;</font>";
					}
					if("79"==codeId)
					{
						if(codelist.length>1)
							td1.innerHTML="&nbsp;<input type='checkbox' id='"+id+"cbo' onclick='Global.getSubject(this,\""+id+"\")'> "+desc+"</input>";
						else
							td1.innerHTML="&nbsp;<input type='checkbox' id='"+id+"cbo' onclick='Global.checkboxOnclick(this)'> "+desc+"</input>";
					}else{
						if("z0375"==id.toLowerCase())
							td1.innerHTML="<font id='z0375id' style='white-space:nowrap;'>"+desc+"</font>";
						else if("z0329"==id.toLowerCase())
							td1.innerHTML="<font id='z0329id' style='white-space:nowrap;'>"+desc+"</font>";
						else
							td1.innerHTML="<font style='white-space:nowrap;'>"+desc+"</font>";
					}
					var html = "";
					if("z0336"==id.toLowerCase()){
						umids.push(fieldkey+"Tree");
						html= "<input type='hidden' name='z0336' id='z0336' />"
						+ '<input name="z0336_view" type="text" id="z0336_view" class="hj-zm-cj-xqbm"  style="height:22px;width:186px;padding-left:3px"/>'
						+ "<img id='"+fieldkey+"Tree' style='margin-left:-19px;' class='img-middle'  src='/module/recruitment/image/xiala2.png' "
						//codesetid 拼接为了解决不能多选的问题
						+ "plugin='codeselector' onlyselectcodeset='false' afterfunc='Global.checkPrivZ0336' codesetid='"+codeId+"' inputname='z0336_view' valuename='z0336'/>";
					}else if("UM"==codeId||"UN"==codeId||"@K"==codeId||level>"3"){
						umids.push(id+"aaa");
						if("UM"==codeId)
						{
							html="<input type='hidden' name='"+id+"_value'  id='"+id+"' /><input id='"+id+"value' style='height:22px;color:#000' class='hj-zm-cj-xqbmz' type='text' name='"+id+"_view'  /><img id='"+id+"aaa' style='margin-left:-19px;' class='img-middle'  src='/module/recruitment/image/xiala2.png' plugin='codeselector' ctrltype='3' nmodule='7' codesetid='"+codeId+"' afterfunc='Global.parentCodeId'   inputname='"+id+"_view'/>";
						}else if("UN"==codeId)
						{
							html="<input type='hidden' name='"+id+"_value'  id='"+id+"' /><input id='"+id+"value' style='height:22px;color:#000' class='hj-zm-cj-xqbmz' type='text'  onchange='Global.clears()' name='"+id+"_view'  /><img id='"+id+"aaa' style='margin-left:-19px;' class='img-middle'  src='/module/recruitment/image/xiala2.png' plugin='codeselector' ctrltype='3' nmodule='7' codesetid='"+codeId+"' afterfunc='Global.childCodeId' inputname='"+id+"_view'/>";
						}else{
							html="<input type='hidden' name='"+id+"_value'  id='"+id+"' /><input id='"+id+"value' style='height:22px;color:#000' class='hj-zm-cj-xqbmz' type='text' name='"+id+"_view'  /><img id='"+id+"aaa' style='margin-left:-19px;' class='img-middle'  src='/module/recruitment/image/xiala2.png' plugin='codeselector' ctrltype='3' nmodule='7' codesetid='"+codeId+"' inputname='"+id+"_view'/>";
						}						
					}else if("M"==type){
						td2.colspan="2";
						td2.width="800px";
						Global.textAreas+=(id+",");
						html="<textarea type='text' class='hj-zm-cj-gzzz' id='"+id+"'  style='overflow:hidden;font-style: normal;font-size:12px;font-family: 微软雅黑, 宋体, tahoma, arial, verdana, sans-serif;width:80%;'></textarea>";
					}else if("D"==type){
						dataIds.push(id+"time");
						var dateFormat ="Y-m-d";
                    	if(length ==4){
                    		dateFormat ="Y";
                    	}else if(length ==7){
                    		dateFormat ="Y-m";
                    	}else if(length ==10){
                    		dateFormat ="Y-m-d";
                    	}else if(length ==16){
                    		dateFormat ="Y-m-d H:i";
                    	}else if(length >=18){
                    		dateFormat ="Y-m-d H:i:s";
                    	}
						html='<input name="'+id+'value" type="text" id="'+id+'" name="'+id+'value" class="hj-zm-cj-xqbm"  style="width:170px" /><img id="'+id+'time" class="img-middle" style="margin-left:-1px;height:23px;" plugin="datetimeselector" inputname="'+id+'value" src="/module/recruitment/image/TIME.bmp" format="'+dateFormat+'" >';
					}else if("N"==type&&level=="0"){
						html="<input onBlur='Global.jugeNum(this,\""+id+"\",\""+desc+"\")' type='text' class='hj-zm-cj-xqbm' id='"+id+"'></input>";
					}else if("45"==codeId&&"1"!=level){
						html='<input type="checkbox" id="'+id+'" value="1"> 是</input>';
					}else if("1"==level){
						if("45"==codeId){
							html='<input type="checkbox" id="'+id+'" value="1"> 是</input>';
						}else{
							var str = "";
							if("79"==codeId)
							{
								str+="<div id='"+id+"panl' style='display:none;'>";
							}
							str+="<select id='"+id+"_'>";
							if(id.substring(0,7) != 'subject')
								str+="<option value='' ><font style='color:#000'>请选择</font></option>";
							
							for ( var int = 0; int < codelist.length; int++) {
								var codestr = codelist[int];
								var codeid =codestr.split("`")[0];
								var codedesc=codestr.split("`")[1];
								if(codelist.length==1&&id!="z0101"){
									str+="<option selected='true' value='"+codeid+"'>"+codedesc+"</option>";
								}else{
									/*if("z0336"==id&&"03"==codeid)
										{
											continue;
										}*/
									str+="<option value='"+codeid+"'>"+codedesc+"</option>";
								}
							}
							
							html=str+"</select>";
							if("79"==codeId)
								html+="<div>";
							
							selectIds.push(id);
						}
					}else if("3"==level||"2"==level){
						deepids.push(id+"deepaaa");
						html="<input type='hidden' name='"+id+"_value' id='"+id+"' />" +
							 "<input id='"+id+"value' style='height:22px;' class='hj-zm-cj-xqfor' type='text' name='"+id+"_view'  />" +
							 "<img id='"+id+"deepaaa' class='img-middle' style='margin-left:-17px;' src='/module/recruitment/image/xiala2.png' plugin='deepcodeselector' codesetid='"+codeId+"' inputname='"+id+"_view'/>";
					}else if("0"==codeId&&"A"==type){
						if("z0333"==id.toLowerCase())
							html="<input maxlength='30' onBlur='Global.jugeLength(this,30,\""+desc+"\")' type='text' class='hj-zm-cj-xqbm' id='"+id+"'></input>";
						else
							html="<input maxlength='" +length + "' onBlur='Global.jugeLength(this,"+length+",\""+desc+"\")' type='text' class='hj-zm-cj-xqbm' id='"+id+"'></input>";
					}else if("0"==level) {
						umids.push(fieldkey+"Tree");
						var hiddenid = "z0384";
						var afterfunc = "Global.replaceData";
						if("Z03A2"==fieldkey.toUpperCase()){
							hiddenid = "z0390";
							afterfunc = "Global.changeDataZ03A2";
						}
						html = "<input type='hidden' name='"+hiddenid+"' id='"+hiddenid+"' />"
							+ '<input name="'+fieldkey+'" type="text" id="'+fieldkey+'" class="hj-zm-cj-xqbm"  style="height:22px;width:168px;padding:0px" readonly="readonly" />'
							+ "<img id='"+fieldkey+"Tree' style='margin-left:-1px;' class='img-middle'  src='/module/recruitment/image/xiala2.png' ";
						html += "plugin='codeselector' afterfunc='"+afterfunc+"' codesetid='"+codeId+"' multiple='true' onlyselectcodeset='false' inputname='"+fieldkey+"' valuename='"+hiddenid+"'/>";
					}
					
					td2.innerHTML=html;
				}
			}   
		}
        elem4.appendChild(table);
        elem3.appendChild(elem4);
        var elemSpace = document.createElement("div");
        elemSpace.className="bh-space";
        //alert(elem1);alert(elem2);alert(elem3);
        if(i==0){//当第一个的时候让它在选人前面
        	elem1.insertBefore(elem3,elem2);
        	elem1.insertBefore(elemSpace,elem2);

        }else{
        	elem1.appendChild(elem3);  
        	elem1.appendChild(elemSpace);
        } 
       //insertAfter(elem3,elem2); 
        
    }
    var operation = document.createElement("div");
    operation.id = "operation";
    operation.style.fontWeight="normal";
    operation.style.right="10px";
    operation.style.position="absolute";
    var html1 = '<a href="javascript:void(0)" style="margin-right:20px" onclick="Global.SaveAndReport()" id="report">保存&报批</a>';
    var html2 = '<a href="javascript:void(0)" style="margin-right:20px" onclick="Global.onlySave()" id="save">保存</a>'
    	+'<a href="javascript:void(0)" style="margin-right:20px"  onclick="Global.toPosition()">取消</a>'
		+'<font style="color:#1b4a98; FONT-SIZE: 12px !important;">';
    var iscontinuehidden = Ext.getDom("iscontinuehidden").value;
    if(iscontinuehidden=="true")
    {
    	html2+='<input id="iscontinue" checked="checked" type="checkbox"/>继续创建新职位</font>';
    }else{
    	html2+='<input id="iscontinue" type="checkbox"/>继续创建新职位</font>';
    }
    if(ispublish=="y")
    	operation.innerHTML=html1+html2;
    else
    	operation.innerHTML=html2;
    
    elem1.appendChild(operation);
    
    for ( var int2 = 0; int2 < selectIds.length; int2++) {
    	var combo = new Ext.form.ComboBox({
    		id:selectIds[int2],
    		emptyText:'请选择',
    		mode:'local',
    		editable: false, 
    		triggerAction:'all',
    		transform:selectIds[int2]+"_"
    	});
    	Ext.getDom(selectIds[int2]).readOnly = 'readonly';
    	Ext.getDom(selectIds[int2]).className=Ext.getDom(selectIds[int2]).className+" x-border-box";
    	Ext.getDom(selectIds[int2]).style.tableLayout="";
    	if(selectIds[int2]=="z0381"||selectIds[int2]=="z0336"){
    		Ext.getDom(selectIds[int2]).style.width="187px";	
    	}
    	if(selectIds[int2]=="z0101")
    	{
    		Ext.getDom(selectIds[int2]).style.width="300px";	
    		combo.addListener('select',function(combo,record,index){    
			 	Global.getBatch(combo.getValue())
			 }    
			);    
    	}
	}
    
    setEleConnect(umids);  //加载树形控件的
    setDeepEleConnect(deepids);//加载层级控件的
	setDateEleConnect(dataIds);//加载时间控件的
};

Global.replaceData = function (codeIds, codeDesc) {
	codeIds = replaceAll(codeIds, "|", ",");
	Ext.getDom("z0384").value = codeIds;
	codeDesc = replaceAll(codeDesc, "|", ",");
	Ext.getDom("z0385").value = codeDesc;
	Global.checkPriv(codeIds,codeDesc,"z0384");
};

Global.checkPrivZ0336 = function (codeIds,codeDesc){
	Global.checkPriv(codeIds,codeDesc,"z0336");
}

Global.checkPriv = function (codeIds,codeDesc,itemid){
	codeIds = codeIds.split(",");
	codeDesc = codeDesc.split(",");
	for(var i = 0; i< codeIds.length;i++){
		var flag = true;
		for(var j = 0; j< privChannel.length;j++){
			if(codeIds[i]==privChannel[j]){
				flag = false;
				break;
			}
		}
		if(flag){
			if("z0384"==itemid){
				Ext.getDom("z0384").value = "";
				Ext.getDom("z0385").value = "";
			}else if("z0336"==itemid){
				Ext.getDom("z0336").value = "";
				Ext.getDom("z0336_view").value = "";
			}
			Ext.showAlert("您没有“"+codeDesc[i]+"”权限！");
			return;
		}
	}
}

Global.changeDataZ03A2 = function (codeIds, codeDesc) {
	codeIds = replaceAll(codeIds, "|", ",");
	Ext.getDom("z0390").value = codeIds;
	codeDesc = replaceAll(codeDesc, "|", ",");
	Ext.getDom("z03a2").value = codeDesc;
};

Global.inputId = "";
Global.jugeNum = function(event,id,name){
    if(!/^\d{0,3}?$/g.test(event.value)){ 
     Ext.showAlert(name+"必须为3位以内整数",function(bt){
    	 event.value="";
     });
    } 
	Global.inputId=id;
};
Global.jugeLength = function(event,length,name){
	if(IsOverStrLength(event.value,length)){
		Ext.showAlert(name+"的长度不能超过" + length + "个英文字符或" +Math.floor(length/2)+ "个汉字！",function(bt){
			event.value=event.value.substring(0,length);
			event.focus();
	    });
	}
};
Global.SaveAndReport = function(){
	var b = Global.judgeNull();
	if(b!=1)
		Global.toSave("report");
};
Global.onlySave = function(){
	var b = Global.judgeNull();
	if(b!=1)
		Global.toSave("save");
};
Global.toPosition = function(){
	var array = Ext.getDom("operation").childNodes;
	for ( var int = 0; int < array.length; int++) {
		array[int].disabled="disabled";
	}
	window.location.href="/recruitment/position/position.do?b_query=link&pageNum=1&flag=1&pagesize=20&positionType=2";
};
//保存职位信息
Global.toSave = function(pram){
	var array = Ext.getDom("operation").childNodes;
	for ( var int = 0; int < array.length; int++) {
		array[int].disabled="disabled";
	}
	var list = new Array();
	var obj = eval(jsonStr);
	var datafield = obj.datafield;
	var pageData = obj.pageData;
	var z0336 = Ext.getDom("z0336").value;
	var z0336_view = Ext.getDom("z0336_view").value;
	var flag = true;
	for(var j = 0; j< privChannel.length;j++){
		if(z0336==privChannel[j]){
			flag = false;
			break;
		}
	}
	if(flag){
		Ext.getDom("z0336").value = "";
		Ext.showAlert("您没有“"+z0336_view+"”权限！");
		Ext.getDom("z0336_view").value = "";
		return;
	}
	
	var jsonObj = new HashMap();
	for ( var i = 0; i < datafield.length; i++) {
		var tablemap = datafield[i];
		var fieldlist = eval("tablemap.table"+i);
		 for ( var j = 0; j < fieldlist.length; j++) {
			 var fieldkey = fieldlist[j];
			 var data = eval("pageData."+fieldkey);
			 if(data==null||data=="undefined"||data=="")
			 	continue;
			 
			 var value = "";
			 if(fieldkey.lastIndexOf("hide")>0)
				 fieldkey = fieldkey.substring(0,fieldkey.length-4);
			 if ("1" == data.level&&"45"!=data.codeId&&"z0336"!=fieldkey) {
				 var temp = fieldkey + "-inputEl";
				 elem = Ext.getCmp(fieldkey);
			 }else {
				 elem = Ext.getDom(fieldkey);
			 }
			 
			 if(elem!=null){
			 	if("45"==data.codeId)
			 	{
			 		if(elem.checked==true){
							value = elem.value;
					 }else{
						 value = "2";  //没选中就代表否
					 }
			 	}else{
					 if ("1" == data.level&&"z0336"!=fieldkey) {
						 if("45"==data.codeId){
							 if(elem.checked==true){
								value = elem.value;
							 }else{
								 value = "2";  //没选中就代表否
							 }
							 elem.checked = false;
						 }else{
							value = elem.getValue();
							elem.setValue("");
						 }
					} else {
						value = elem.value;
						if("UM"==data.codeId||"3"==data.level){
							var elem2 = Ext.getDom(fieldkey+"value");  //需求部门的展示值清空
							if(elem2)
								elem2.value = "";
						}
						elem.value = "";
					}
			 	}
			 	if("M"==data.type&&value!=null){//解决非IE浏览器下文本域中换行导致json拼接错误问题
					 value = value.replace(/\r\n/g, "<br>");
					 value = value.replace(/\n/g, "<br>");
				 }
			 	if(value!=null){
			 		value=trim(value); 
			 	}
				list.push(fieldkey);
				if(fieldkey.indexOf("subject_")!=-1&&!Ext.getDom(fieldkey+"cbo").checked)
				{
					value = "";
				}
				
				if(value!=null && "D"!=data.type)
					value = jsonReplace(value);
				else if(value == null)
					value = "";
				
				 jsonObj.put(fieldkey,value);
			 }
			 
		 }
	}
	
	//得到选人的信息
	var perJson="";
	var responsPosiId="";   //招聘负责人
	var depResponsPosiId="[";  //部门负责人
	var ponsMemberId="["; //招聘成员
	var rpEle = Ext.getDom("responsPosiId");
	responsPosiId = rpEle.value;
	
	var drpEle = document.getElementsByName("depResponsPosiId");
//	var drpEle = Ext.getDom("depResponsPosiId");
	if(drpEle.length>0){
        for(var i=0;i<drpEle.length;i++){
            depResponsPosiId+='\"'+drpEle[i].value+'\"';
            if(i==drpEle.length-1){
                depResponsPosiId+="]";
            }else{
                depResponsPosiId+=",";
            }
        }
    }else{
        depResponsPosiId+="]";
    }
	
	var pmEle = document.getElementsByName("ponsMemberId");
	if(pmEle.length>0){
		for(var i=0;i<pmEle.length;i++){
			ponsMemberId+='\"'+pmEle[i].value+'\"';
			if(i==pmEle.length-1){
				ponsMemberId+="]";
			}else{
				ponsMemberId+=",";
			}
		}
	}else{
		ponsMemberId+="]";
	}
	perJson+='{\"responsPosiId\":\"'+responsPosiId+'\",\"depResponsPosiId\":'+depResponsPosiId+',\"ponsMemberId\":'+ponsMemberId+'}';
	 
	var hashvo=new ParameterSet();
	hashvo.setValue("datastr",Ext.encode(jsonObj)); 
	hashvo.setValue("dataList",list);
	hashvo.setValue("type", pram);
	hashvo.setValue("perJson", perJson);
	var request=new Request({asynchronous:false,onSuccess:Global.saveSuccess,functionId:'ZP0000002076'},hashvo); 
};

Global.saveSuccess = function(outparam){
	if(Ext.getDom("iscontinue").checked==false){
		window.location="/recruitment/position/position.do?b_query=link&pageNum=1&flag=1&pagesize=20&positionType=2";
	}else{
		window.location="/recruitment/position/position.do?b_toadd=link&iscontinue=true";
	}
};
//校验时间
Global.judgeData = function(){
	if(Ext.getDom("z0329")){
		var z0329 =Ext.getDom("z0329").value;
		z0329 = z0329.replace(/-/g,"/");
	}
	
	if(Ext.getDom("z0331")){
		var z0331 =Ext.getDom("z0331").value;
		z0331 = z0331.replace(/-/g,"/");
	}
	
	var z0329Time = new Date(z0329);
	var z0331Time = new Date(z0331);
	
    if(z0329Time.getTime() > z0331Time.getTime()){
    	Ext.showAlert("有效起始日期不能大于有效结束日期");
		 return 1;
    }
	
	if(Ext.getDom("z0375") != null){
		var z0375 =Ext.getDom("z0375").value;
		z0375 = z0375.replace(/-/g,"/");
		var z0375Time = new Date(z0375);
		var z0375name =Ext.getDom("z0375id").innerText;
		var z0329name =Ext.getDom("z0329id").innerText;
		
		if(z0329Time.getTime() > z0375Time.getTime()){
	    	Ext.showAlert(z0375name+"不应小于"+z0329name);
			return 1;
	    }
	}
	
};

//校验不能为空
Global.judgeNull = function(){
	var obj = eval(jsonStr);
	var datafield = obj.datafield;
	var pageData = obj.pageData;
	
	for ( var i = 0; i < datafield.length; i++) {
		var tablemap = datafield[i];
		var fieldlist = eval("tablemap.table"+i);
		 for ( var j = 0; j < fieldlist.length; j++) {
			 var fieldkey = fieldlist[j];
			 var data = eval("pageData."+fieldkey);
			 if(data==null||data=="undefined"||data=="")
			 {
			 	continue;
			 }
			 var length = data.itemLength;
			 if(fieldkey.lastIndexOf("hide")>0)
				 fieldkey = fieldkey.substring(0,fieldkey.length-4);
			 var elem;
			 if("1"==data.level&&"45"!=data.codeId&&"z0336"!=fieldkey){
				 var temp = fieldkey+"-inputEl";
				 elem = Ext.getCmp(fieldkey);
			 }else{
				 elem = Ext.getDom(fieldkey);
			 }
			 if(elem!=null&&"z0301"!=data.id){
				 var value;
				 if("1"==data.level&&"45"!=data.codeId&&"z0336"!=fieldkey)
					 value = elem.getValue();
				 else
					 value = elem.value;
				 
				 if("z0315"==data.id && elem.value <= 0 && elem.value != ""){
					 Ext.showAlert(data.desc+"不能小于等于0");
					 return 1;
				 }
				 
				 if("y"==data.required){
					 if(value ==null || trim(value).length<=0 ||"请选择"==elem.value ){
						 Ext.showAlert(data.desc+"不能为空");
						 return 1;
					 }
				 }
				 if("D"==data.type&&trim(value).length>0&&!Global.judgeDataType(value,length))
				 {
					if(length ==4){
						dataMessage = DATE_FORMAT_YEAR;
					}else if(length ==7){
						dataMessage = DATE_FORMAT_MONTH;
					}else if(length ==10){
						dataMessage = DATE_FORMAT_DAY;
					}else if(length ==16){
						dataMessage = DATE_FORMAT_MINUTE;
					}else if(length >=18){
						dataMessage =DATE_FORMAT_SECOND;
					}
				 	Ext.showAlert(data.desc + dataMessage);
				 	return 1;
				 }
				 
			 }
			 if(fieldkey.indexOf("subject_")!=-1&&Ext.getDom(fieldkey+"cbo").checked)
			 {
			 	if(Ext.getCmp("z0101")=="undefined")
			 	{
			 		Ext.showAlert("未设置招聘批次选项");
						 return 1;
			 	}
			 	
			 	if(Ext.getCmp("z0101").getValue()=="" ||  Ext.getCmp("z0101").getValue()== null)
			 	{
			 		Ext.showAlert("招聘批次不能为空");
						 return 1;
			 	}
			 	 if(Ext.getCmp(fieldkey).getValue()=="" ||  Ext.getCmp(fieldkey).getValue()== null)
			 	 {
			 	 	Ext.showAlert(data.desc+"不能为空");
						 return 1;
			 	 }
			 }
		 }
	}
	return Global.judgeData();
};

//校验日期类型是否正确yyyy(年)-MM(月)-dd(日)
Global.judgeDataType = function(data,length)
{
	var dataType = /^[0-9]{4}-(0?[0-9]|1[0-2])-(0?[1-9]|[12]?[0-9]|3[01])$/;
	if(length ==4){
		dataType = /^[0-9]{4}$/;
	}else if(length ==7){
		dataType = /^[0-9]{4}-(0?[0-9]|1[0-2])$/;
	}else if(length ==10){
		dataType = /^[0-9]{4}-(0?[0-9]|1[0-2])-(0?[1-9]|[12]?[0-9]|3[01])$/;
	}else if(length ==16){
		dataType = /^[0-9]{4}-(0?[0-9]|1[0-2])-(0?[1-9]|[12]?[0-9]|3[01])\s+(20|21|22|23|[0-1]\d):[0-5]\d$/;
	}else if(length >=18){
		dataType = /^[0-9]{4}-(0?[0-9]|1[0-2])-(0?[1-9]|[12]?[0-9]|3[01])\s+(20|21|22|23|[0-1]\d):[0-5]\d:[0-5]\d$/;
	}
	
	if (!dataType.test(data)){
		return false
	}else{
		return true 
	} 
}

Global.addposA01 = new Array();//招聘成员
Global.reposA01 = "";  //招聘负责人
Global.deposA01 = new Array();  //部门招聘负责人

Ext.onReady(function(){
	Global.reposA01=Ext.getDom("responsPosiId").value;
});

//选人
Global.pickPerson = function(btn,type){
	 var arr = Global.getArray();
	 var picker = new PersonPicker({
			multiple: false,
			isPrivExpression:false,//是否启用人员范围（含高级条件）
			validateSsLOGIN:true,
			deprecate: arr,
			callback: function (c) {
				 if(Global.jugeArray(arr,c.id)<0){
					 if (type == 1) {  //招聘负责人
						 var elem1 = Ext.getDom("responsPosiName");
						 var elem2 = Ext.getDom("responsPosiPic");
						 var elem3 = Ext.getDom("responsPosiId");
						 var elem4 = Ext.getDom("responsTitle");
						 var newName = c.name;
                         if(c.name.replace(/[\u4E00-\u9FA5]/g,'aa').length>6){
                        	 newName = Global.cut_str(c.name,3);
                         }
						 elem1.innerHTML = newName;
						 elem2.src = c.photo;
						 elem3.value = c.id;
						 elem4.title= c.name;
						 Global.reposA01=c.id;
					 }else if(type == 2){  //部门负责人
						 var elem = Ext.getDom("addDep");
						 var divid = "divDep";
						 var html = '<dl onmouseover="Global.toRemove(\''+divid+'\')" onmouseleave="Global.toChan(\''+divid+'\')"><dt title="'+c.name+'"><img class="img-circle" src="'+c.photo+'" /><img id="'+divid+'" class="deletePic" class="img-middle" onclick="Global.toDelet(this,2)" style="width: 20px;display: none; height: 20px;" src="/workplan/image/remove.png" complete="complete"></img></dt><dd>'+c.name+'</dd></dl><input name="depResponsPosiId" type="hidden" value="'+c.id+'"></input>';
						 if (elem != null) {
							 elem.className = "hj-nmd-dl";
							 elem.innerHTML = html;
						 } else {
							 var elem1 = Ext.getDom("addTd2");
							 var elem2 = Ext.getDom("addA2");
							 var elem3 = document.createElement("div");
							 elem3.id = "addDep";
							 elem3.className = "hj-nmd-dl";
							 elem3.innerHTML = html;
							 elem1.insertBefore(elem3, elem2);
						 }
						 
						 Global.deposA01=c.id;
					 }
					 
				 }else{
					 Ext.showAlert("同一招聘职位下的招聘负责人、招聘<br>成员以及部门负责人不能为同一个人！");
				 }
			}
		}, btn);
		picker.open();
	 
	
};
/*
 * var picker = new PersonPicker({
	multiple: true,
	callback: function (c) {
		alert(c.length);
	}
}, btn);
picker.open();
 * */
Global.indexAd = 0;
Global.addPerson = function(btn,type){
	var arr = Global.getArray();
	
	var picker = new PersonPicker({
		multiple: true,
		isPrivExpression:false,//是否启用人员范围（含高级条件）
		validateSsLOGIN:true,
		deprecate: arr,
		callback: function (cm) {
		    if (type == 1) {
			var elem1 = Ext.getDom("addTd1");
			var elem2 = Ext.getDom("addA1");
			for(var int2=0;int2<cm.length;int2++){
				var c = cm[int2];
				Global.indexAd++;
				var elem3 = document.createElement("div");
				elem3.name="addDiv";
				elem3.className="hj-nmd-dl";
				var a="N";
				for ( var int = 0; int < arr.length; int++) {
					if(c.id==arr[int]){
						Ext.showAlert("同一招聘职位下的招聘负责人、招聘<br>成员以及部门负责人不能为同一个人！");
						return;
					}
				}
				var newName = c.name;
				if(c.name.replace(/[\u4E00-\u9FA5]/g,'aa').length>6){
					newName = Global.cut_str(c.name,3);
                }
				var divid ="divs"+Global.indexAd;
				
				elem3.innerHTML='<dl onmouseover="Global.toRemove(\''+divid+'\')" onmouseleave="Global.toChan(\''+divid+'\')"><dt title="'+c.name+'"><img class="img-circle" src="'+c.photo+'" /><img id="'+divid+'" class="deletePic" onclick="Global.toDelet(this,1)" class="img-middle" style="width: 20px;display: none; height: 20px;" src="/workplan/image/remove.png" complete="complete"></img></dt><dd>'+newName+'</dd></dl><input name="ponsMemberId" type="hidden" value="'+c.id+'"/>';
				elem1.insertBefore(elem3,elem2);
				if(Global.jugeArray(arr,c.id)<0){
					Global.addposA01.push(c.id);
				}
			}
		} else if(type == 2){
		    var elem1 = Ext.getDom("addTd2");
            var elem2 = Ext.getDom("addA2");
            for(var int2=0;int2<cm.length;int2++){
                var c = cm[int2];
                Global.indexAd++;
                var elem3 = document.createElement("div");
                elem3.name="addDep";
                elem3.className="hj-nmd-dl";
                var a="N";
                for ( var int = 0; int < arr.length; int++) {
                    if(c.id==arr[int]){
                        Ext.showAlert("同一招聘职位下的招聘负责人、招聘<br>成员以及部门负责人不能为同一个人！");
                        return;
                    }
                }
                
                var newName = c.name;
                if(c.name.replace(/[\u4E00-\u9FA5]/g,'aa').length>6){
                	newName = Global.cut_str(c.name,3);
                }
                var divid ="divDep"+Global.indexAd;
                elem3.innerHTML='<dl onmouseover="Global.toRemove(\''+divid+'\')" onmouseleave="Global.toChan(\''+divid+'\')"><dt title="'+c.name+'"><img class="img-circle" src="'+c.photo+'" /><img id="'+divid+'" class="deletePic" class="img-middle" onclick="Global.toDelet(this,2)" style="width: 20px;display: none; height: 20px;" src="/workplan/image/remove.png" complete="complete"></img></dt><dd>'+newName+'</dd></dl><input name="depResponsPosiId" type="hidden" value="'+c.id+'"></input>';
                elem1.insertBefore(elem3,elem2);
                if(Global.jugeArray(arr,c.id)<0){
                    Global.deposA01.push(c.id);
                }
            }
		    
		}
		}
	}, btn);
	picker.open();
};

Global.toDelet=function(elem,id){
	var information =DETERMINE_DELETE_MEMBER;
    //删除元素为部门负责人
    if(id == 2)
        information =DETERMINE_DELETE_PRINCIPAL;
    
	Ext.Msg.confirm(PROMPT_INFORMATION,information,function(btn){ 
		if(btn=="yes"){ 
			var addtdelem = Ext.getDom("addTd"+id);
			var b =elem.parentNode.parentNode.parentNode;
			if(id==2){
			    var arrNode = b.childNodes;
		        var value ="";
		        for ( var int = 0; int < arrNode.length; int++) {
		            if(arrNode[int].tagName=="INPUT"){
		                value= arrNode[int].value;
		                break;
		            }
		            
		        }
		        Global.deposA01.remove(value);
			}else if(id==1){
				var arrNode = b.childNodes;
				var value ="";
				for ( var int = 0; int < arrNode.length; int++) {
					if(arrNode[int].tagName=="INPUT"){
						value= arrNode[int].value;
						break;
					}
					
				}
				Global.addposA01.remove(value);
			}
			addtdelem.removeChild(b);
		}
		});
};

Global.toRemove=function(par){
	var a =Ext.getDom(par);
	a.style.display="";
};

Global.toChan=function(par) {
	var a =Ext.getDom(par);
	a.style.display="none";
};

Global.getArray = function(){
	var arr = new Array();
	 if(Global.reposA01.length>0)
		 arr.push(Global.reposA01);
	 if(Global.deposA01.length>0){
	     for ( var int = 0; int < Global.deposA01.length; int++) {
             arr.push(Global.deposA01[int]);
        }
	 }
	 if(Global.addposA01.length>0){
		 for ( var int = 0; int < Global.addposA01.length; int++) {
			 arr.push(Global.addposA01[int]);
		}
	 }
	return arr;
};

Global.checkboxOnclick = function(checkbox){
	if (checkbox.checked == true){
		Ext.getDom("z0101hide").style.display = "";
	}else{
		var obj = eval(jsonStr);
		var datafield = obj.datafield;
		Ext.getDom("z0101hide").style.display = "none";
		for ( var i = 0; i < datafield.length; i++) {
			var tablemap = datafield[i];
			var fieldlist = eval("tablemap.table"+i);
			for ( var j = 0; j < fieldlist.length; j++) {
				var fieldkey = fieldlist[j];
				if(fieldkey.indexOf("subject_")!=-1&&Ext.getDom(fieldkey+"cbo").checked)
				{
					Ext.getDom("z0101hide").style.display = "";
				}
			}
		}
	}
};

//截取6个字节长度的字符串
Global.cut_str = function (str, len){
    var char_length = 0;
    for (var i = 0; i < str.length; i++){
        var son_str = str.charAt(i);
        encodeURI(son_str).length > 2 ? char_length += 1 : char_length += 0.5;
        if (char_length >= len){
            var sub_len = char_length == len ? i+1 : i;
            return str.substr(0, sub_len);
        }
    }
};

Global.jugeArray =function(arr,param){
	var temp = -1;
	for(var i = 0;i<arr.length;i++){
		if(arr[i]==param){
			temp = i;
			break;
		}
	}
	return temp;
};