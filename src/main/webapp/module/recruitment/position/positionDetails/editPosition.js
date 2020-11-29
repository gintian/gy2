var Global = new Object();
Global.depid = "";  //部门负责人id memberid
Global.repoid ="";  //招聘负责人id memberid
Global.textAreas="";
Global.endBatch = new Object();//已结束的批次

/**** * 选择招聘批次时将查询批次对应的信息
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
	if(Ext.getCmp("z0101").getValue()=="")
	{
		return;
	}
	if(z0107!="")
	{
		//设置招聘开始时间
		if(Ext.getDom("z0329Value")!=null)
			Ext.getDom("z0329Value").value=z0107;
	}
	if(z0109!=""){
		//设置招聘结束时间
		if(Ext.getDom("z0331Value")!=null)
			Ext.getDom("z0331Value").value=z0109;
	}
	//选批次时暂不处理渠道
	/*if(z0151!=""){
		//设置招聘渠道
		if(Ext.getDom("z0336")!=null)
			Ext.getCmp("z0336").setValue(z0151);
	}*/
	if(z0153!=""){
		//设置招聘流程
		var personNum = Ext.getDom("personNum").value;
		if(Ext.getDom("z0381")!=null && personNum <= 0)
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
    	Ext.getDom("z03251aaa").setAttribute("parentid","");
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
	if(Ext.getDom("z0325" + Global.imgindex + "aaa")){
	    Ext.getDom("z0325" + Global.imgindex + "aaa").removeAttribute("parentid");
	    Ext.getDom("z0325" + Global.imgindex + "aaa").setAttribute("parentid",codeId);
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
		Ext.getDom(itemId+"panl").style.display = "";
	}else{
		Ext.getDom(itemId+"panl").style.display = "none";
	}
}

Global.createdatapageHtml= function(jsonStr){
	var obj = eval(jsonStr);
	var datafield = obj.datafield;
    var pageData = obj.pageData;
    Global.endBatch = obj.endBatch;
    var dataPosition = obj.dataPosition;
    var elem1 = Ext.getDom('divf');
    elem1.style.marginLeft="10px";
    var elem2 = Ext.getDom('choosePesron');
    //先给招聘人员赋值
    var responsPosi = dataPosition.responsPosi;
    if(responsPosi!=null){
    	Global.toViewPerson(responsPosi,"responsPosiName","responsPosiPic","responsPosiId");
    	Global.repoid = responsPosi.id+"";
    	Global.reposA01 = responsPosi.a0100;
    }
    
    var depResponsPosi =dataPosition.depResponsPosi;
    if(depResponsPosi!=null){
        Global.toShowdepRes(depResponsPosi);
        
    	/*Global.toViewPerson(depResponsPosi,"depResponsPosiName","depResponsPosiPic","depResponsPosiId");
    	Global.depid = depResponsPosi.id+"";    	
    	Global.deposA01.push(depResponsPosi.a0100);*/

    }
    	
    	
    var responsMember=dataPosition.responsMember;
    if(responsMember!=null)
    	Global.toShowMember(responsMember);
    

	for ( var i = 0; i < datafield.length; i++) {
		var tablemap = datafield[i];
		var tablevalue = eval("pageData.table" + i);
		var elem3 = document.createElement("div");
		var elem4 = document.createElement("div");

		elem3.setAttribute("id", "table" + i);

		elem3.className = "hj-zm-xq-one";
		elem4.className = "hj-zm-cj-zwmc";
		elem3.innerHTML = "<h2 style='padding-left: 25px;'>　" + tablevalue + "</h2>";
		
		var fieldlist = eval("tablemap.table" + i);
		var table = document.createElement("table");
		table.setAttribute("width", "100%");
		table.setAttribute("border", "1");
		table.setAttribute("cellpadding", "0");
		if(navigator.userAgent.indexOf("MSIE") != -1) {
			table.setAttribute("cellspacing", "0");
        }else{
        	table.setAttribute("cellspacing", "5");
        	
        }

		for ( var j = 0; j < fieldlist.length; j++) {
			var fieldkey = fieldlist[j];
			field = eval("pageData."+fieldkey);
			if(field!=null){
				var tr = table.insertRow(table.rows.length);
				var td3 = tr.insertCell(tr.cells.length);
				td3.className="vertical";
				td3.width="60";
				td3.align="left";
				var td1 = tr.insertCell(tr.cells.length);
				td1.className="vertical";
				td1.width="60";
				var td2 = tr.insertCell(tr.cells.length);
				var id = field.id;
				var type = field.type;
				var desc =field.desc;
				var codeId =field.codeId;
				var level =field.level;
				var codelist = field.codelist;
				var required = field.required;
				var dataValue;
				
				if(fieldkey.lastIndexOf("hide")>0){
					dataValue =  eval("dataPosition."+fieldkey.substring(0,fieldkey.length-4));
					td1.innerHTML="<input type='hidden' id='"+id+"' value='"+dataValue+"' />";
				}else{
					dataValue =  eval("dataPosition."+fieldkey);
					if(id.indexOf("subject_")!=-1&&"79"==codeId)
					{						
						if(dataValue != null&&codelist.length>=1)
							td1.innerHTML="&nbsp;<input type='checkbox' checked='checked'  id='"+id+"cbo' onclick='Global.checkboxOnclick(this)' > "+desc+"</input>&nbsp;&nbsp;&nbsp;";
						else
							td1.innerHTML="&nbsp;<input type='checkbox' id='"+id+"cbo' onclick='Global.checkboxOnclick(this)' > "+desc+"</input>&nbsp;&nbsp;&nbsp;";					
					}else{
						td1.innerHTML="<font style='white-space:nowrap;'>"+desc+"&nbsp;&nbsp;&nbsp;</font>";
						
						
					}
				
					td1.id=fieldkey+"td1";
					td2.id=fieldkey+"td";
					td3.id=fieldkey+"td3";
					var html=Global.createHtml(type,level,codeId,dataValue,codelist,id);
					if("M"==type){
						td2.colspan="2";
						td2.width = "800";
					}
					td2.innerHTML=html;
				}
				
				
			}
		}
		elem4.appendChild(table);
        elem3.appendChild(elem4);
        var elemSpace = document.createElement("div");
        elemSpace.className="bh-space";
        
        if(i==0){//当第一个的时候让它在选人前面
        	elem1.insertBefore(elem3,elem2);
        	elem1.insertBefore(elemSpace,elem2);

        }else{
        	elem1.appendChild(elem3);  
        	elem1.appendChild(elemSpace);
        } 
        Global.resizeTextarea(fieldlist,pageData);
	}
};
//给每个文本域添加自适应大小事件
Global.resizeTextarea = function(fieldlist,pageData){
	for ( var j = 0; j < fieldlist.length; j++) {
		var fieldkey = fieldlist[j];
		field = eval("pageData."+fieldkey);
		if(field!=null){
			var id = field.id;
			var type = field.type;
			if("M"==type)
				resize(id);
		}
	}
}

Global.createHtml=function(type,level,codeId,dataValue,codelist,id){
	var html = "";
	if("D"==type){
		html="<div>"+dataValue+"</div>";
	}else if("M"==type){
		Global.textAreas+=id+",";
		html="<div class='hj-wzm-six-bottom-er' style='height:auto;'><textarea type='text' readOnly='readOnly' class='hj-zm-cj-gzzzz' id='"+id+"' >"+dataValue+"</textarea><div>";
	}else if("1"==level){
		for ( var int = 0; int < codelist.length; int++) {
				var codestr = codelist[int];
				var codeid =codestr.split("`")[0];
				var codedesc=codestr.split("`")[1];
				var newdataValue = "";
				if(dataValue)
					newdataValue = dataValue.split("`")[0];
				
				if(codeid==newdataValue)
				{
					html="<div>"+codedesc+"</div>";
				}else if("z0101"==id){
					for(var i=0;i<Global.endBatch.length;i++){
						codestr = Global.endBatch[i];
						codeid =codestr.split("`")[0];
						codedesc=codestr.split("`")[1];
						if(codeid==newdataValue)
						{
							html="<div>"+codedesc+"</div>";
						}
					}
				}
		}
	}else{
		if("UM"==codeId||"UN"==codeId||"@K"==codeId||level>"3"){
			if(dataValue!=null&&dataValue.length>0)
				html="<div>"+dataValue.split("`")[1].replace(/`/g,"")+"</div>";
			else
				html="<div>"+dataValue.replace(/</g,"＜").replace(/`/g,"")+"</div>";
		}else{
			if("1"==dataValue.replace(/</g,"＜")&&codeId!="0")
			{
				html="<div>是</div>";
			}else if("2"==dataValue.replace(/</g,"＜")&&codeId!="0")
			{
				html="<div>否</div>";
			}else{
				html="<div>"+dataValue.replace(/</g,"＜").replace(/`/g,"")+"</div>";
			}
		}
	}
	return html;
};

Global.toViewPerson = function(obj,name,pic,poid){
	var elem1 = Ext.getDom(name);
	var elem2 = Ext.getDom(pic);
	var elem4 =Ext.getDom("responsTitle");
	var newName = obj.name;
    if(obj.name.replace(/[\u4E00-\u9FA5]/g,'aa').length>6){
         newName = Global.cut_str(obj.name,3);
    }
	
	elem1.innerHTML = newName;
	elem2.src = obj.url;
	elem4.title= obj.name;
};

Global.indexAd=0;
Global.toShowMember = function(obj){
	for ( var int = 0; int < obj.length; int++) {
		Global.indexAd++;
		var elem1 = Ext.getDom("addTd1");
    	var elem2 = Ext.getDom("addA1");
    	var elem3 = document.createElement("div");
		var data = obj[int];
		var newName = data.name;
        if(data.name.replace(/[\u4E00-\u9FA5]/g,'aa').length>6){
             newName = Global.cut_str(data.name,3);
        }
		
		Global.addposA01.push(data.a0100);
		elem3.className="hj-nmd-dl";
		var divid = "divs"+Global.indexAd;
		if(z0319=="04")
			elem3.innerHTML='<dl><dt title="'+data.name+'"><img src="'+data.url+'"  class="img-middle img-circle"/></dt><dd>'+newName+'</dd></dl><input name="ponsMemberId" type="hidden" value="'+data.a0100+'"></input>';
		else
			elem3.innerHTML='<dl onmouseover="Global.toRemove(\''+divid+'\')" onmouseleave="Global.toChan(\''+divid+'\')"><dt title="'+data.name+'"><img src="'+data.url+'"  class="img-middle img-circle" /><img  id="'+divid+'" class="deletePic" '+'onclick=\'Global.deleteP'+'(this,"'+data.id+'",1)\''+'  style="width: 20px; height: 20px;display:none;" src="/workplan/image/remove.png" /></dt><dd>'+newName+'</dd></dl><input name="ponsMemberId" type="hidden" value="'+data.a0100+'"></input>';
		
		if(elem2!=null)
    		elem1.insertBefore(elem3,elem2);
    	else
    		elem1.appendChild(elem3);
	}
};

Global.indexAd=0;
Global.toShowdepRes = function(obj){
    for ( var int = 0; int < obj.length; int++) {
        Global.indexAd++;
        var elem1 = Ext.getDom("addTd2");
        var elem2 = Ext.getDom("addA2");
        var elem3 = document.createElement("div");
        var data = obj[int];
        Global.deposA01.push(data.a0100);
        elem3.className="hj-nmd-dl";
        var divid = "divs"+Global.indexAd;
        var newName = data.name;
        if(data.name.replace(/[\u4E00-\u9FA5]/g,'aa').length>6){
             newName = Global.cut_str(data.name,3);
        }
        
        if(z0319=="04")
            elem3.innerHTML='<dl><dt title="'+data.name+'"><img src="'+data.url+'"  class="img-middle img-circle"/></dt><dd>'+newName+'</dd></dl><input name="ponsMemberId" type="hidden" value="'+data.a0100+'"></input>';
        else
            elem3.innerHTML='<dl onmouseover="Global.toRemove(\''+divid+'\')" onmouseleave="Global.toChan(\''+divid+'\')"><dt title="'+data.name+'"><img src="'+data.url+'"  class="img-middle img-circle" /><img  id="'+divid+'" class="deletePic" '+'onclick=\'Global.deleteP'+'(this,"'+data.id+'",2)\''+'  style="width: 20px; height: 20px;display:none;" src="/workplan/image/remove.png" /></dt><dd>'+newName+'</dd></dl><input name="ponsMemberId" type="hidden" value="'+data.a0100+'"></input>';
        
        if(elem2!=null)
            elem1.insertBefore(elem3,elem2);
        else
            elem1.appendChild(elem3);
    }
};

Global.toRemove=function(par){
	var a =Ext.getDom(par);
	a.style.display="";
};

Global.toChan=function(par) {
	var a =Ext.getDom(par);
	a.style.display="none";
};

//type等于2的时候为部门负责人，等于1的时候为招聘成员
Global.deleteP=function(elem,id,type){
    var information =DETERMINE_DELETE_MEMBER;
    //删除元素为部门负责人
    if(type == 2)
        information =DETERMINE_DELETE_PRINCIPAL;
    
	Ext.Msg.confirm(PROMPT_INFORMATION,information,function(btn){ 
		if(btn=="yes"){ 
			var addtdelem = Ext.getDom("addTd"+type);
			var b =elem.parentNode.parentNode.parentNode;
			if(type==2){
				//Global.deposA01 = "";
			    var arrNode = b.childNodes;
                var value ="";
                for ( var int = 0; int < arrNode.length; int++) {
                    if(arrNode[int].tagName=="INPUT"){
                        value= arrNode[int].value;
                        break;
                    }
                    
                }
                Global.deposA01.remove(value);
			}else if(type==1){
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
			var hashvo=new ParameterSet();
			hashvo.setValue("id", id);
			//判断元素是部门负责人还是招聘成员
			hashvo.setValue("num",type);
			var request=new Request({asynchronous:false,onSuccess:Global.deleSuccess,functionId:'ZP0000002077'},hashvo); 
		} 
	});
};

Global.deleSuccess=function(para){
	if(para.getValue("num")==2)
		Global.depid="";
};

Global.imgindex = 0;

var selectIdnew = new Array();
Global.toEdit= function(){
	var selectIds = new Array();
	this.imgindex++;
	var elem1 = Ext.getDom('divf');
	var obj = eval(jsonStr);
	var datafield = obj.datafield;
    var pageData = obj.pageData;
    var dataPosition = obj.dataPosition;
    var dataIds = new Array();
    var umid = "";
    var styheight = "";
    var umids = new Array();
    var deepids = new Array();
	styheight="style='height:20px;padding:0px'";
	for ( var i = 0; i < datafield.length; i++) {
		var tablemap = datafield[i];
		var fieldlist = eval("tablemap.table" + i);
		for ( var j = 0; j < fieldlist.length; j++) {
			var fieldkey = fieldlist[j];
			field = eval("pageData." + fieldkey);
			if (field != null) {
				var id = field.id;
				var type = field.type;
				var desc = field.desc;
				var codeId = field.codeId;
				var level = field.level;
				var codelist = field.codelist;
				var required = field.required;
				var length = field.itemLength;
				if(fieldkey.lastIndexOf("hide")<0){
					var dataValue =  eval("dataPosition."+fieldkey);
					var tdelem = Ext.getDom(fieldkey+"td");
                    tdelem.style.border="none";
	                tdelem.className = "hj-zm-cj-xqbmz";
					var html = "";
					var td3 = Ext.getDom(fieldkey+"td3");
					if("y"==required){
						 td3.innerHTML="<font color='red' style='white-space:nowrap;'>*&nbsp;</font>";
					}
					if("z0101"==id.toLowerCase()){
						if("y"==required)
							td3.innerHTML="<font color='red' id='z0101hide' style='white-space:nowrap;'>*&nbsp;</font>";
						else
							td3.innerHTML="<font color='red' id='z0101hide' style='white-space:nowrap;display:none;'>*&nbsp;</font>";
					}
					if("z0336"==id||"Z0336"==id){
						umids.push(fieldkey+"Tree");
						html= "<input type='hidden' name='z0336' id='z0336' value='"+dataValue.split("`")[0]+"' />"
						+ '<input name="z0336_view" type="text" id="z0336_view" class="hj-zm-cj-xqbm"  style="width:186px;padding-left:3px" value="'+dataValue.split("`")[1]+'" />'
						+ "<img id='"+fieldkey+"Tree' style='margin-left:-19px;height:25px' class='img-middle'  src='/module/recruitment/image/xiala2.png' "
						//codesetid 拼接为了解决不能多选的问题
						+ "plugin='codeselector' onlyselectcodeset='false' afterfunc='Global.checkPrivZ0336' codesetid='"+codeId+"' inputname='z0336_view' valuename='z0336'/>";
					}else if("UM"==codeId||"UN"==codeId||"@K"==codeId||level>"3"){
						if("UM"==codeId)
						{
							if(dataValue!=null&&dataValue.length>0)
								html="<input type='hidden' name='"+id+"_value'  id='"+id+"' value='"+dataValue.split("`")[0]+"' /><input "+styheight+" id='"+id+"value' class='hj-zm-cj-xqbmz' type='text' name='"+id+"_view' value='"+dataValue.split("`")[1]+"' /><img ctrltype='3' nmodule='7'  class='img-middle' style='margin-left:-19px;' id='"+id+this.imgindex+"aaa' src='/module/recruitment/image/xiala2.png' plugin='codeselector' afterfunc='Global.parentCodeId'  codesetid='"+codeId+"' inputname='"+id+"_view'/>";
							else
								html="<input type='hidden' name='"+id+"_value'  id='"+id+"' /><input "+styheight+" id='"+id+"value' class='hj-zm-cj-xqbmz' type='text' name='"+id+"_view' /><img ctrltype='3' nmodule='7'  class='img-middle' style='margin-left:-19px;' id='"+id+this.imgindex+"aaa' src='/module/recruitment/image/xiala2.png' plugin='codeselector' afterfunc='Global.parentCodeId'  codesetid='"+codeId+"' inputname='"+id+"_view'/>";
						}else if("UN"==codeId)
						{
							if(dataValue!=null&&dataValue.length>0)
								html="<input type='hidden' name='"+id+"_value'  id='"+id+"' value='"+dataValue.split("`")[0]+"' /><input "+styheight+" id='"+id+"value' class='hj-zm-cj-xqbmz' type='text'  onchange='Global.clears()' name='"+id+"_view' value='"+dataValue.split("`")[1]+"' /><img ctrltype='3' nmodule='7'  class='img-middle' style='margin-left:-19px;' id='"+id+this.imgindex+"aaa' src='/module/recruitment/image/xiala2.png' plugin='codeselector'  afterfunc='Global.childCodeId' codesetid='"+codeId+"' inputname='"+id+"_view'/>";
							else
								html="<input type='hidden' name='"+id+"_value'  id='"+id+"' /><input "+styheight+" id='"+id+"value' class='hj-zm-cj-xqbmz' type='text'  onchange='Global.clears()' name='"+id+"_view' /><img ctrltype='3' nmodule='7'  class='img-middle' style='margin-left:-19px;' id='"+id+this.imgindex+"aaa' src='/module/recruitment/image/xiala2.png' plugin='codeselector'  afterfunc='Global.childCodeId' codesetid='"+codeId+"' inputname='"+id+"_view'/>";
						}else{
							if(dataValue!=null&&dataValue.length>0)
								html="<input type='hidden' name='"+id+"_value' id='"+id+"' value='"+dataValue.split("`")[0]+"' /><input "+styheight+" id='"+id+"value' class='hj-zm-cj-xqbmz' type='text' name='"+id+"_view' value='"+dataValue.split("`")[1]+"' /><img ctrltype='3' nmodule='7'  class='img-middle' style='margin-left:-19px;' id='"+id+this.imgindex+"aaa' src='/module/recruitment/image/xiala2.png' plugin='codeselector' codesetid='"+codeId+"' inputname='"+id+"_view'/>";
							else
								html="<input type='hidden' name='"+id+"_value' id='"+id+"'  /><input id='"+id+"value' "+styheight+" class='hj-zm-cj-xqbmz' type='text' name='"+id+"_view'  /><img ctrltype='3' nmodule='7' class='img-middle' style='margin-left:-19px;' id='"+id+this.imgindex+"aaa' src='/module/recruitment/image/xiala2.png' plugin='codeselector' codesetid='"+codeId+"' inputname='"+id+"_view'/>";
						}
						umids.push(id+this.imgindex+"aaa");
						umid=id+"value";
					}else if("M"==type){
						html="<textarea type='text' class='hj-zm-cj-gzzz' id='"+id+"' style='overflow:hidden;font-style: normal;font-size:12px;font-family: 微软雅黑, 宋体, tahoma, arial, verdana, sans-serif;width:80%;' >"+dataValue+"</textarea>";
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
						
                    	if(dataValue!=null&&dataValue.length>0)
							html='<input type="text" id="'+id+'Value"  class="hj-zm-cj-xqbm" style="width:170px;padding:0px" name="'+id+'value" value="'+dataValue+'"/><img style="margin-left:-1px;height:25px;"  class="img-middle" id="'+this.imgindex+id+'time" plugin="datetimeselector" inputname="'+id+'value" format= "'+dateFormat+'" src="/module/recruitment/image/TIME.bmp">';
						else
							html='<input type="text" id="'+id+'Value"  class="hj-zm-cj-xqbm" style="width:170px;padding:0px" name="'+id+'value" /><img id="'+this.imgindex+id+'time"  class="img-middle" style="margin-left:-1px;height:25px;" plugin="datetimeselector" inputname="'+id+'value" format="'+dateFormat+'" src="/module/recruitment/image/TIME.bmp">';
						
					}else if("45"==codeId&&"1"!=level){
							if(dataValue=="1")
								html='<input type="checkbox" id="'+id+'" value="1" checked="true"> 是</input>';
							else
								html='<input type="checkbox" id="'+id+'" value="1" > 是</input>';
				
					}else if("1"==level){
							if("45"==codeId){
								if(dataValue.split("`")[0]=="1")
									html='<input type="checkbox" id="'+id+'" value="1" checked="true"> 是</input>';
								else
									html='<input type="checkbox" id="'+id+'" value="1" > 是</input>';

							}else{
								var str = "";
								if("79"==codeId)
								{
									if(Ext.getDom(id+"cbo").checked)
									{
										str+="<div id='"+id+"panl' >";
										Ext.getDom("z0101hide").style.display = "";
									}else{
										str+="<div id='"+id+"panl' style='display:none;'>";
									}
									
									if(codelist.length>1)
										Ext.getDom(id+"cbo").setAttribute("onclick","Global.getSubject(this,'"+id+"')");
								}
								str+= "<select id='"+id+"_' >";
								if(id.substring(0,7) != 'subject')
									str+="<option value=''>请选择</option>";
								
								for ( var int = 0; int < codelist.length; int++) {
									var codestr = codelist[int];
									var codeid =codestr.split("`")[0];
									var codedesc=codestr.split("`")[1];
                                    var dataValueId ="false";
                                    if (dataValue != null) { 
                                        dataValueId = dataValue.split("`")[0]; 
                                        } 
                                    
									if(codeid==dataValue || codeid == dataValueId || codestr == dataValue ||codelist.length==1){
										if("z0101"==id&&codeid!=dataValue){
											for(var i=0;i<Global.endBatch.length;i++){
												var cstr = Global.endBatch[i];
												var cid =cstr.split("`")[0];
												var cdesc=cstr.split("`")[1];
												if(cid==dataValue)
												{
													codeid = cid;
													codedesc = cdesc;
												}
											}
										}
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
								{
									html+="<div>";
								}
								selectIds.push(id);
							}
					}else if("3"==level||"2"==level){
						if(""==dataValue)
							dataValue="`";
						html="<input type='hidden' name='"+id+"_value' id='"+id+"' value='"+dataValue.split("`")[0]+"' /><input "+styheight+" id='"+id+"value' class='hj-zm-cj-xqfor' type='text' name='"+id+"_view' value='"+dataValue.split("`")[1]+"' /><img  class='img-middle' style='margin-left:-19px;' id='"+id+this.imgindex+"deepaaa' src='/module/recruitment/image/xiala2.png' plugin='deepcodeselector' codesetid='"+codeId+"' inputname='"+id+"_view'/>";
						deepids.push(id+this.imgindex+"deepaaa");
					}else if("0"==codeId&&"A"==type||"N"==type){
						if("N"==type)
							html="<input "+styheight+" onBlur='Global.jugeNum(this,\""+id+"\",\""+desc+"\")' type='text' class='hj-zm-cj-xqbm' id='"+id+"'></input>";
						else{
							if("z0333"==id.toLowerCase())
								html="<input "+styheight+" maxlength='30' onBlur='Global.jugeLength(this,30,\""+desc+"\")' type='text' class='hj-zm-cj-xqbm' id='"+id+"'></input>";
							else
								html="<input "+styheight+" maxlength='" + length + "' onBlur='Global.jugeLength(this,"+length+",\""+desc+"\")' type='text' class='hj-zm-cj-xqbm' id='"+id+"'></input>";
						}
					} else if("0"==level) {
						umids.push(fieldkey+"Tree");
						var hiddenid = "z0384";
						var hiddenValue =  dataPosition.z0384;
						var afterfunc = "Global.changeData";
						if("Z03A2"==fieldkey.toUpperCase()){
							hiddenid = "z0390";
							hiddenValue = dataPosition.z0390;
							afterfunc = "Global.changeDataZ03A2";
						}
						html = "<input type='hidden' name='"+hiddenid+"' id='"+hiddenid+"' value='" + hiddenValue + "'/>"
							+ '<input name="'+fieldkey+'" type="text" id="'+fieldkey+'" value="' + dataValue + '" class="hj-zm-cj-xqbmz"  style="width:168px;padding:0px" readonly="readonly" />'
							+ "<img id='"+fieldkey+"Tree' style='margin-left:-1px;height:25px;' class='img-middle'  src='/module/recruitment/image/xiala2.png' ";
						html += "plugin='codeselector' afterfunc='"+afterfunc+"' codesetid='"+codeId+"' multiple='true' onlyselectcodeset='false' inputname='"+fieldkey+"' valuename='"+hiddenid+"'/>";
					}
					
					tdelem.innerHTML=html;
					if("0"==codeId&&"A"==type||"N"==type) {
						var obj = Ext.getDom(id);
						if(obj != null)
							obj.value=dataValue;
					}
					
				}
				
			}

		}
		
		Global.resizeTextarea(fieldlist,pageData);
		
		
	}//forend
	setDeepEleConnect(deepids);
	toInnitTextArea();
	setEleConnect(umids);
	for ( var int2 = 0; int2 < dataIds.length; int2++) {
		setDateEleConnect([this.imgindex+dataIds[int2]]);
	}
	initDocument();//在common.js /ehr/hrms/ajax/common.js 中的 用来重新初始化时间控件
	selectIdnew = selectIds;
	for ( var int2 = 0; int2 < selectIds.length; int2++) {
    	var combo = new Ext.form.ComboBox({
    		id:selectIds[int2],
    		emptyText:'请选择',
    		mode:'local',
    		triggerAction:'all',
    		transform:selectIds[int2]+"_"
    	});
    	if(selectIds[int2]=="z0381"||selectIds[int2]=="z0336"){//如果当前职位下含有人员，则不允许进行更换流程
    		Ext.getDom(selectIds[int2]).style.width="190px";	
    		var personNum = Ext.getDom("personNum").value;
    		if(personNum>0)
    		{
    			combo.setFieldStyle('color:gray');
	    		combo.setReadOnly(true);
    		}
    	}
    	
    	if(selectIds[int2]=="z0101") {
    		var bachid = combo.getValue();
    		if(Global.endBatch){
    			for(var i=0;i<Global.endBatch.length;i++){
    				if(bachid==Global.endBatch[i].split("`")[0]){
    					combo.setFieldStyle('color:gray');
    					combo.setReadOnly(true);
    					break;
    				}
    			}
    		}
    		Ext.getDom(selectIds[int2]).style.width="302px";	
    		combo.addListener('select',function(combo,record,index){ 
			 	Global.getBatch(combo.getValue())
			 }    
			);    
    	}
    	Ext.getDom(selectIds[int2]).readOnly = 'readonly';
    	Ext.getDom(selectIds[int2]).className=Ext.getDom(selectIds[int2]).className+" x-border-box";
	}
};

Global.changeData = function (codeIds, codeDesc) {
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

Global.jugeLength = function(event,length,name){
	if(IsOverStrLength(event.value,length)){
		Ext.showAlert(name+"的长度不能超过" + length + "个英文字符或" +Math.floor(length/2)+ "个汉字！",function(bt){
			event.value=event.value.substring(0,length);
			event.focus();
	    });
	}
	
	
};
Global.inputId = "";
Global.jugeNum = function(event,id,desc){
    if(!/^\d{0,3}?$/g.test(event.value)){ 
     Ext.showAlert(desc+"必须为3位以内整数",function(bt){
    	 event.value="";
     });
    } 
	Global.inputId=id;
};
//取消编辑
Global.toView = function() {
	Global.textAreas="";
	for ( var int2 = 0; int2 < selectIdnew.length; int2++) {
		var combo = Ext.getCmp(selectIdnew[int2]);
		combo.destroy();
	}
	var obj = eval(jsonStr);
	var datafield = obj.datafield;
    var pageData = obj.pageData;
    var dataPosition = obj.dataPosition;
    var selectIds = new Array();

    for ( var i = 0; i < datafield.length; i++) {
		var tablemap = datafield[i];
		var fieldlist = eval("tablemap.table" + i);
		for ( var j = 0; j < fieldlist.length; j++) {
			var fieldkey = fieldlist[j];
			field = eval("pageData." + fieldkey);
			if (field != null) {
				var id = field.id;
				var type = field.type;
				var desc = field.desc;
				var codeId = field.codeId;
				var level = field.level;
				var codelist = field.codelist;
				var required = field.required;
				if(fieldkey.lastIndexOf("hide")<0){
					var td3 = Ext.getDom(fieldkey+"td3");
					if("y"==required){
						td3.innerHTML="";
					}
					var td1 = Ext.getDom(fieldkey+"td1");
					if(id.indexOf("subject_")!=-1&&"79"==codeId)
					{						
						if(dataValue != null&&codelist.length>=1)
							td1.innerHTML="&nbsp;<input type='checkbox' checked='checked'  id='"+id+"cbo' onclick='return false;'> "+desc+"</input>&nbsp;&nbsp;&nbsp;";
						else
							td1.innerHTML="&nbsp;<input type='checkbox' id='"+id+"cbo' onclick='return false;'> "+desc+"</input>&nbsp;&nbsp;&nbsp;";					
					}
					var dataValue =  eval("dataPosition."+fieldkey);
					var tdelem = Ext.getDom(fieldkey+"td");
					var html = Global.createHtml(type,level,codeId,dataValue,codelist,id);
					tdelem.innerHTML= html;
				}
				
			}
			
		}
		
	}
	toInnitTextArea();
	Ext.getDom("z0101hide").style.display = "none";
};

//返回//
Global.returnPos =function(pageNum,searchStr,pagesize){
	var from = Ext.getDom("from").value;
	var arrPage = pageDesc.split("`");
	if(from.length>0){
		parent.window.location="/recruitment/resumecenter/searchresumecenter.do?b_search=link&current="+arrPage[0]+"&pagesize="+arrPage[2]+"&schemeValues="+arrPage[1]+"&from="+from+"&back=true";
	}else{
		parent.window.location="/recruitment/position/position.do?b_query=link&pageNum="+arrPage[0]+"&searchStr="+arrPage[1]+"&pagesize="+arrPage[2];
	}
};


Global.addposA01 = new Array();//招聘成员
Global.reposA01 = "";  //招聘负责人
Global.deposA01 = new Array();  //部门招聘负责人


//选人
Global.pickPerson = function(btn,type){
	var arr= Global.getArray();
	
	var picker = new PersonPicker({
		multiple: false,
		isPrivExpression:false,//是否启用人员范围（含高级条件）
		validateSsLOGIN:true,
		deprecate: arr,
		callback: function (c) {
			if(Global.jugeArray(arr, c.id)<0){
	    		 var memberid="";
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
	    			 memberid=Global.repoid;
	    			 elem4.title= c.name;
	    			 Global.reposA01= c.id;
	    		 }else if(type == 3){  //部门负责人
	    			 var elem = Ext.getDom("addDep");
	    			 var elem1 = Ext.getDom("depResponsPosiName");
	    			 var elem2 = Ext.getDom("depResponsPosiPic");
	    			 if(elem!=null){
	    				 elem1.innerHTML = c.name;  
	    				 elem2.src = c.photo;
	    			 }else{
	    				 var elem1 = Ext.getDom("addTd2");
	    				 var elem2 = Ext.getDom("addA2");
	    				 var elem3 = document.createElement("div");
	    				 elem3.id="addDep";
	    				 elem3.className="hj-nmd-dl";
	    				 elem1.insertBefore(elem3,elem2);
	    			 }
	    			 
	    			 memberid=Global.depid;
	    			 Global.deposA01.push(c.id);	    			
	    		 }
	    		 
	    		 var hashvo=new ParameterSet();
	    		 hashvo.setValue("id", c.id);
	    		 hashvo.setValue("type", type);
	    		 hashvo.setValue("func", "update");
	    		 hashvo.setValue("z0301", Ext.getDom("z0301").value);
	    		 hashvo.setValue("b0110", c.b0110);
	    		 hashvo.setValue("e0122", c.e0122);
	    		 hashvo.setValue("e01a1", c.e01a1);
	    		 hashvo.setValue("a0101", c.name);
	    		 hashvo.setValue("memberId",memberid);
	    		 hashvo.setValue("photo", c.photo);
	    		 var request=new Request({asynchronous:false,onSuccess:Global.updSuccess,functionId:'ZP0000002078'},hashvo); 
	    		 
	    	 }else{
	    		 Ext.showAlert("同一招聘职位下的招聘负责人、招聘<br>成员以及部门负责人不能为同一个人！");
	    	 }
		}
	}, btn);
	picker.open();
};

Global.updSuccess = function(param){
	var func= param.getValue("func");
	var name = param.getValue("name");
	var newName = param.getValue("name");
	if(name.replace(/[\u4E00-\u9FA5]/g,'aa').length>6){
	    var newName = Global.cut_str(name,3);
    }
	var photo = param.getValue("photo");
	var memid = param.getValue("memid");
	if("insert"==func && param.getValue("type")==2){
		Global.indexAd++;
		var elem1 = Ext.getDom("addTd1");
    	var elem2 = Ext.getDom("addA1");
    	var elem3 = document.createElement("div");
    	elem3.className="hj-nmd-dl";
    	var divid = "divs"+Global.indexAd;
    	var mousHtml = 'onmouseover="Global.toRemove(\''+divid+'\')" onmouseleave="Global.toChan(\''+divid+'\')"';
    	var html='<img id="'+divid+'" class="deletePic" '+'onclick=\'Global.deleteP'+'(this,"'+memid+'",1)\''+' fullName="'+name+'" style="width: 20px; height: 20px;display:none;" src="/workplan/image/remove.png" />';
    	elem3.innerHTML='<dl '+mousHtml+'><dt title="'+name+'"><img class="img-circle" src="'+photo+'" />'+html+'</dt><dd>'+newName+'</dd></dl><input name="ponsMemberId" type="hidden" value="'+param.getValue("id")+'"></input>';
    	elem1.insertBefore(elem3,elem2);
	}else if("insert"==func && param.getValue("type")==3){
	    Global.indexAd++;
        var elem1 = Ext.getDom("addTd2");
        var elem2 = Ext.getDom("addA2");
        var elem3 = document.createElement("div");
        elem3.className="hj-nmd-dl";
        var divid = "divs"+Global.indexAd;
        var mousHtml = 'onmouseover="Global.toRemove(\''+divid+'\')" onmouseleave="Global.toChan(\''+divid+'\')"';
        var html='<img id="'+divid+'" class="deletePic" '+'onclick=\'Global.deleteP'+'(this,"'+memid+'",2)\''+' fullName="'+name+'" style="width: 20px; height: 20px;display:none;" src="/workplan/image/remove.png" />';
        elem3.innerHTML='<dl '+mousHtml+'><dt title="'+name+'"><img class="img-circle" src="'+photo+'" />'+html+'</dt><dd>'+newName+'</dd></dl><input name="ponsMemberId" type="hidden" value="'+param.getValue("id")+'"></input>';
        elem1.insertBefore(elem3,elem2);       
    }else if("update"==func&&param.getValue("type")==3){
		var elem = Ext.getDom("addDep");
		Global.depid=memid;
		var html = '<img class="deletePic" onclick="Global.deleteP(this,\''+Global.depid+'\',2)" id="divDep" style="width: 20px; height: 20px;display:none;" src="/workplan/image/remove.png" />';		
    	elem.innerHTML='<dl onmouseover="Global.toRemove(\'divDep\')" onmouseleave="Global.toChan(\'divDep\')"><dt title="'+name+'"><img id="depResponsPosiPic"  class="img-middle img-circle" src="'+photo+'" />'+html+'</dt><dd id="depResponsPosiName">'+newName+'</dd></dl>';
    	
	}
};
 
Global.addPerson = function(btn,flag){
	var arr= Global.getArray();
	var picker = new PersonPicker({
		multiple: true,
		isPrivExpression:false,//是否启用人员范围（含高级条件）
		validateSsLOGIN:true,
		deprecate: arr,
		callback: function (cm) {
		    if(flag==1){
			for ( var int2 = 0; int2 < cm.length; int2++) {
				var c = cm[int2];
				var a="N";
				for ( var int = 0; int < arr.length; int++) {
					if(c.id==arr[int]){
						a="Y";
						Ext.showAlert("同一招聘职位下的招聘负责人、招聘<br>成员以及部门负责人不能为同一个人！");
						return;
					}
				}
	    		if(Global.jugeArray(arr,c.id)<0){
	    			Global.addposA01.push(c.id);
	    		}
	    		
	    		var hashvo=new ParameterSet();
	    		hashvo.setValue("id", c.id);
	    		hashvo.setValue("type", "2");
	    		hashvo.setValue("func", "insert");
	    		hashvo.setValue("z0301", Ext.getDom("z0301").value);
	    		hashvo.setValue("a0101", c.name);
	    		hashvo.setValue("photo", c.photo);
	    		var request=new Request({asynchronous:false,onSuccess:Global.updSuccess,functionId:'ZP0000002078'},hashvo); 
	    	
			}
		    }
		    
		    if(flag==2){
	            for ( var int2 = 0; int2 < cm.length; int2++) {
	                var c = cm[int2];
	                var a="N";
	                for ( var int = 0; int < arr.length; int++) {
	                    if(c.id==arr[int]){
	                        a="Y";
	                        Ext.showAlert("同一招聘职位下的招聘负责人、招聘<br>成员以及部门负责人不能为同一个人！");
	                        return;
	                    }
	                }
	                if(Global.jugeArray(arr,c.id)<0){
	                    Global.deposA01.push(c.id);
	                }
	                
	                var hashvo=new ParameterSet();
	                hashvo.setValue("id", c.id);
	                hashvo.setValue("type", "3");
	                hashvo.setValue("func", "insert");
	                hashvo.setValue("z0301", Ext.getDom("z0301").value);
	                hashvo.setValue("a0101", c.name);
	                hashvo.setValue("photo", c.photo);
	                var request=new Request({asynchronous:false,onSuccess:Global.updSuccess,functionId:'ZP0000002078'},hashvo); 
	            
	            }
	            }
		    
			
		}
	}, btn);
	picker.open();
};

Global.onlySave = function(){
	var inputValue = Ext.getDom(Global.inputId);
	if(inputValue!=null)
	{
		inputValue=inputValue.value;
		if(!/^\d{0,3}?$/g.test(inputValue)){ 
	    	 return 1;
	    } 
	}
	var b = Global.judgeNull();
	if(b!=1)
		Global.toSave();
	else
		return 1;
};

//校验时间
Global.judgeData = function(){
	if(Ext.getDom("z0329Value")){
		var z0329 =Ext.getDom("z0329Value").value;
		z0329 = z0329.replace(/-/g,"/");
	}
	
	if(Ext.getDom("z0331Value")){
		var z0331 =Ext.getDom("z0331Value").value;
		z0331 = z0331.replace(/-/g,"/");
	}
	
	var z0329Time = new Date(z0329);
	var z0331Time = new Date(z0331);
	
    if(z0329Time.getTime() > z0331Time.getTime()){
    	Ext.showAlert("有效起始日期不能大于有效结束日期");
		 return 1;
    }
    if(Ext.get("z0375Value") != null){
    	var z0375 =Ext.getDom("z0375Value").value;
		z0375 = z0375.replace(/-/g,"/");
		var z0375Time = new Date(z0375);
		var z0375name =Ext.getDom("z0375td1").innerText;
		var z0329name =Ext.getDom("z0329td1").innerText;
		if(z0329Time.getTime() > z0375Time.getTime()){
	    	Ext.showAlert(z0375name+"不应小于"+z0329name);
			 return 1;
	    }
	}
	
};

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
			 }else if("D"==data.type){
				 elem = Ext.getDom(fieldkey+"Value");
			 }else{
				 elem = Ext.getDom(fieldkey);
			 }
			 if(elem!=null){
				 if(data.id!="z0301"){
					 var value;
					 if("1"==data.level&&"45"!=data.codeId&&"z0336"!=fieldkey)
						 value = elem.getValue();
					 else
						 value = elem.value;
					 
					 if("z0315"==data.id && elem.value <= 0  && elem.value != ""){
						 Ext.showAlert(data.desc+"不能小于等于0");
						 return 1;
					 }
					 
					 if("y"==data.required){
						 if(value ==null || trim(value).length<=0 ||"请选择"==elem.value ){
							 Ext.showAlert(data.desc+"不能为空");
							 return 1;
						 }
					 }
					 
					 if ("z0351"==data.id) {
						 if(IsOverStrLength(value,length)){
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
			 	 if(Ext.getCmp(fieldkey).getValue()=="" || Ext.getCmp(fieldkey).getValue()==null)
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

//保存职位信息
Global.toSave = function(){
	var list = new Array();
	var obj = eval(jsonStr);
	var datafield = obj.datafield;
	var pageData = obj.pageData;
	
	var str = "{";
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
			 var value = "";
			 if(fieldkey.lastIndexOf("hide")>0)
				 fieldkey = fieldkey.substring(0,fieldkey.length-4);
			 if("D"==data.type){
				 elem = Ext.getDom(fieldkey+"Value");
			 }else if ("1" == data.level&&"45"!=data.codeId&&"z0336"!=fieldkey) {
				 var temp = fieldkey + "-inputEl";
				 elem = Ext.getCmp(fieldkey);
				 
			 }else {
				 elem = Ext.getDom(fieldkey);
				 
			 }
			 
			 if(elem!=null){
			 	/*if("45"==data.codeId)
			 	{
			 		if(elem.checked==true){
							value = elem.value;
					 }else{
						 value = "2";  //没选中就代表否
					 }
			 	}else{
			 		if ("1" == data.level) {
						 if("45"==data.codeId){
							 if(elem.checked==true){
								value = elem.value;
							 }else{
								 value = "2";  //没选中就代表否
							 }
						 }else{
							value = elem.getValue();
						 }
					} else {
						value = elem.value;
					}
			 	}*/
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
						 }else{
							value = elem.getValue();
						 }
					} else {
						value = elem.value;
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
				
				var temp = '\"' + fieldkey + '\"' + ':' + '\"' + value + '\"'
						+ ',';
				str += temp;
			 } 
		 }
	}
	if(str.length>1)
		str = str.substring(0, str.length-1);
	str+="}";
	
	
	var hashvo=new ParameterSet();
	hashvo.setValue("datastr",encode(str));
	hashvo.setValue("dataList",list);
	hashvo.setValue("type", "edit");
	var request=new Request({asynchronous:false,onSuccess:Global.saveSuccess,functionId:'ZP0000002076'},hashvo); 
	
};

Global.saveSuccess = function(outparam){
	parent.window.location.reload();
};

Global.getArray = function(){
	var arr = new Array();
	 if(Global.reposA01.length>0)
		 arr.push(Global.reposA01);
	 if(Global.deposA01.length>0){
	     for ( var int = 0; int <Global.deposA01.length; int++) {
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