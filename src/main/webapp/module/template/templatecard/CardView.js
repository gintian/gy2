/**
 *卡片显示数据、样式 及些前台操作事件。
 * 
 * */
 
/**
 *双击子集，显示子集window编辑
 * */
function showSubSet(uniqueId,columnId,tabid){
	var recordSet=templateCard_me.getCurRecordSet();
	if(recordSet){
		var valueItem= recordSet.getField(uniqueId);			
		var fldItem= recordSet.fieldSet.getField(uniqueId);	
		var title = fldItem.fldDesc;
		var data_xml="";
		if (valueItem!=null){
			data_xml=replaceAll(valueItem.keyValue,"````","18364#234#449"); 
			//data_xml=replaceAll(data_xml,"~","～"); //~在编码是被替换，导致子集显示不出
			data_xml=getDecodeStr(data_xml);//子集xml解码 2016 10 13   
			data_xml=replaceAll(data_xml,"18364#234#449","````");
			
			data_xml=replaceAll(data_xml,"＜","<");
			data_xml=replaceAll(data_xml,"＞",">");
			data_xml=replaceAll(data_xml,"＇","'");
			data_xml=replaceAll(data_xml,"＂",'"');
			data_xml=replaceAll(data_xml,"&","");
			//解决XML2String会抛出异常报“不支持此接口”问题。
			/*var XMLDoc = loadXMLString(data_xml);  
			XMLDoc.async=false;
			var rootNode = XMLDoc.documentElement;//fields那层节点 
			try
			{
				if(rootNode)
				{
					var recNodes = XMLDoc.getElementsByTagName("record"); 
					for(var i=0;i<recNodes.length;i++){
						var node = recNodes[i];	
						var nodeValue = "";
						if(node.firstChild){
							nodeValue = node.firstChild.nodeValue;
							//nodeValue = replaceAll(nodeValue," ","");
							node.firstChild.nodeValue = nodeValue;
						}
					}
				}
				data_xml=XML2String(XMLDoc);	
			}
			catch(e)
			{
				Ext.showAlert(e.message);
			}*/
		}
		var arr= templateCard_me.object_id.split("`");
	    var basepre=arr[0];
	    var a0100=arr[1];
	    var map = new HashMap();
	    
	    map.put("view_type","card");
	    map.put("basepre",basepre); 
	    if(templateMain_me.templPropety.view_type=="card"){
	    	map.put("ins_id",templateCard_me.cur_ins_id);
	    	map.put("task_id",templateCard_me.cur_task_id);//回传当前人的task_id
	    }
		map.put("approveflag",templateMain_me.templPropety.approve_flag);
		map.put("a0100",a0100);
		map.put("table_name",templateMain_me.templPropety.table_name);   //表名
		map.put("multimedia_maxsize",templateMain_me.templPropety.multimedia_maxsize);   //文件限制大小
		map.put("rootDir",templateMain_me.templPropety.rootDir);   //是否设置了文件根目录
		map.put("tabid",tabid);
		map.put("columnName",columnId);    //字段名
		map.put("title",title);
		map.put("chgState",fldItem.chgState);//变化前后
		map.put("rwPriv",fldItem.rwPriv);    //是否可编辑（读写权限）
		map.put("nodePriv",templateCard_me.nodePriv);    //是否受节点权限授权控制
		map.put("data_xml",data_xml);
		map.put("uniqueId",uniqueId);
		map.put("saveFunc",synSubSetView); //编辑完子集内容需在卡片界面同步显示，回调方法
		map.put("objectid",templateCard_me.object_id); //卡片子集直接保存到数据库
		Ext.require('TemplateSubsetUL.TemplateSubsets',function(){
			var re = Ext.create("TemplateSubsetUL.TemplateSubsets",{map:map});
		})
	}
 }

//模板中显示子集 lis 20160504
function showSubView(uniqueId,columnId,tabid,tdElement,rootNode){
    var i =uniqueId.indexOf("_");
    var temp= uniqueId.substr(i+1,uniqueId.length);
    i =temp.indexOf("_");
    var pageId= temp.substr(0,i);
    var cellId= temp.substr(i+1,temp.length);
	if(rootNode)
	{
		var divid=uniqueId+"_div";
		var divElement=document.getElementById(divid);
		/*
        if(!divElement)
        {
            divElement=document.createElement("div");
            divElement.style.width="100%";
            divElement.style.height="100%";
            divElement.id=divid;
            tdElement.appendChild(divElement);
        }
        */
	}
	var p = Ext.getCmp(uniqueId + "subset");
	if(p){
		//偶尔出现p的destroy方法丢失,暂时原因未知
		try{
			p.destroy();
		}catch(e){}
	}
	var recordSet=templateCard_me.getCurRecordSet();
	if(recordSet){
		var valueItem= recordSet.getField(uniqueId);
		var fldItem= recordSet.fieldSet.getField(uniqueId);	
		var title = fldItem.fldDesc;
		var data_xml="";
		if (valueItem!=null){
			data_xml=valueItem.keyValue;
		}
		if(templateCard_me.object_id){
			var arr= templateCard_me.object_id.split("`");
			var basepre=arr[0];
			var a0100=arr[1];
			var map = new HashMap();
			map.put("view_type","card"); 
			map.put("isShowWin","0");
			map.put("title",title);
			map.put("basepre",basepre); 
			if(templateMain_me.templPropety.view_type=="card")
		    	map.put("ins_id",templateCard_me.cur_ins_id);
			map.put("approveflag",templateMain_me.templPropety.approve_flag);
			map.put("a0100",a0100);
			map.put("table_name",templateMain_me.templPropety.table_name);   //表名
			map.put("multimedia_maxsize",templateMain_me.templPropety.multimedia_maxsize);   //文件限制大小
			map.put("rootDir",templateMain_me.templPropety.rootDir);   //是否设置了文件根目录
			map.put("tabid",tabid);
			map.put("columnName",columnId);    //字段名
			map.put("chgState",fldItem.chgState);//变化前后
			map.put("rwPriv",fldItem.rwPriv);    //是否可编辑（读写权限）
			map.put("nodePriv",templateCard_me.nodePriv);    //是否受节点权限授权控制
			map.put("data_xml",data_xml);
			map.put("uniqueId",uniqueId);
			map.put("dataChangeFunc",dataChange); //编辑完子集内容需在卡片界面同步显示，回调方法
			map.put("enlargeFunc",showSubSet); //放大按钮，回调方法
			Ext.require('TemplateSubsetUL.TemplateSubsets',function(){
				var re = Ext.create("TemplateSubsetUL.TemplateSubsets",{map:map});
			})
		}
	}
 }

/* 同步卡片子集xmldata */
function dataChange(_uniqueId,_xmldata)
{ 
/*
    var recordSet=templateCard_me.getCurRecordSet(); 
    var fieldSet=recordSet.fieldSet;  
    for (var i=0;i<fieldSet.getFieldCount();i++){
		var field=fieldSet.fields[i];
     	if(field.uniqueId==_uniqueId)
     	{
     		templateCard_me.recordSet.getField(_uniqueId).keyValue=_xmldata;
     			
			break;
		}
	}
	
	*/
	
	var valueItem= templateCard_me.getValueItem(_uniqueId);
	valueItem.setValue(getEncodeStr(_xmldata));//子集xml转码 2016 10 13 
}

/* 同步卡片子集数据 */
function synSubSetView(_uniqueId,_subDatajson,_xmldata)
{ 
    var recordSet=templateCard_me.getCurRecordSet(); 
    var fieldSet=recordSet.fieldSet;  
    var valueItem =recordSet.getField(_uniqueId);
    for (var i=0;i<fieldSet.getFieldCount();i++){
		var field=fieldSet.fields[i];
     	if(field.uniqueId==_uniqueId)
     	{
		    recordSet.getField(_uniqueId).disValue=_subDatajson;  
		    //recordSet.getField(_uniqueId).keyValue=_xmldata;
		    dataChange(_uniqueId,getDecodeStr(_xmldata));//lis 20160709
		    var element =Ext.getDom(field.uniqueId);//根据唯一值取得页面对应元素
		    if(_subDatajson){
		    	try{
		    		var recordRoot=Ext.decode(getDecodeStr(_subDatajson));  
		    		var tabid = templateMain_me.templPropety.tab_id;
		    		showSubView(_uniqueId,valueItem.fldName,tabid,element,recordRoot)
		    		//showSubDomainView(recordSet.id,element,recordRoot,field);
		    	}catch (e) {
					// TODO: handle exception
				}
		    }
			break;
		}
	}
}

//显示附件
function showAttachment(uniqueId,attachmenttype,ins_id,tabid,rwPriv,sp_batch,object_id){
	var recordSet=templateCard_me.getCurRecordSet();
	if(recordSet){
	    var map = new HashMap();
	    map.put("object_id",object_id); 
		map.put("ins_id",ins_id);   //实例id
		map.put("tabid",tabid);//模板号
		map.put("attachmenttype",attachmenttype);    //字段名
		map.put("rwPriv",rwPriv);//0无，1只读，2编辑
	//	map.put("sp_batch",sp_batch);//是否是批量审批  //20160905 dengcan 无用了，ins_id表示当前选中记录的单号
		map.put("uniqueId",uniqueId);
		map.put("module_id",templateCard_me.templPropety.module_id);//传递模块号：9是自助申请。
		map.put("infor_type",templateCard_me.templPropety.infor_type); //编辑完子集内容需在卡片界面同步显示，回调方法
		Ext.require('TemplateCardUL.TemplateAttachment',function(){
			var re = Ext.create("TemplateCardUL.TemplateAttachment",{map:map});
		})
	}
 }
 var oldValue="";//记录单元格focus时的值，代码型click事件会判断这个值是不是数值型。
/*模板单元格的onfocus事件**/
function processFocus(obj){
    //if(window.codeSelector && window.codeSelector.selector)
    //	window.codeSelector.finish_destory(true);
	oldValue=obj.value;
	templateCard_me.activeElement=obj;
	var field=templateCard_me.getCurFieldItem();
	if (field.rwPriv!="2"){//无写权限
       return;
    }
	if(field.codeSetId!='0'&&field.codeSetId!=''){
		obj.click();
	}
}

/*模板单元格的onclick事件**/
function processClick(obj)
{
	templateCard_me.activeElement=obj;
	//获取指标
	var field=templateCard_me.getCurFieldItem();
	var valueField=templateCard_me.getCurValueItem();
	if (field.rwPriv!="2"){//无写权限
       return;
    }
    //代码型指标如果原来没有值，或者focus时有值且不为数值型但click时有值为数值型，就清空当前单元格的值。
    if((field.codeSetId!='' && field.codeSetId!='0')&&(valueField.keyValue==""||trim(valueField.keyValue).length==0||!isNaN(obj.value)&&isNaN(oldValue)))
    {
    	obj.value="";
    }
    if ((field.fldType=="A" && (field.codeSetId =='' || field.codeSetId == '0')) || field.fldType=="N"|| field.fldType=="M") {//字符或数字型
		 if((field.fldType=="A"|| field.fldType=="M") &&field.imppeople){//启用了选人组件判断是否启用了唯一性指标。
			 if(templateMain_me.templPropety.isValidOnlyname==undefined||templateMain_me.templPropety.isValidOnlyname=='false'){
				 Ext.showAlert("请设置并且启用唯一性指标！")
				 return false;
			 }
			var	 defaultSelectedPerson=new Array();
			 if(obj.value!=undefined&&obj.value!=''){//根据现有的人员姓名和唯一性指标值，反查库前缀和人员编号。
				 var hashvo = new HashMap();
					hashvo.put("ids",obj.value);
					hashvo.put("tabid",templateMain_me.templPropety.tab_id);
				 Rpc( {functionId : 'MB00002030',async:false,success:function(form,action){//
						var result = Ext.decode(form.responseText);	
						if(!result.resultValue.succeed){
				            Ext.showAlert(result.resultValue.Msg);
				            return;
			            }else{
			            	defaultSelectedPerson=result.resultValue.value;
			            }
			 		}}, hashvo);
			 }
			var temIsPrivExpression="";
			var isPrivExpression=false;
			var filter_factor="";
			var orgId="";
			/*if(templateMain_me.templPropety.orgId)//bug 43518 启用选人组件不应控制范围
				orgId=templateMain_me.templPropety.orgId;
			else
				orgId="";
			if(templateMain_me.templPropety.filter_by_factor==1)
			{
				temIsPrivExpression=templateMain_me.templPropety.isPrivExpression;
				filter_factor=templateMain_me.templPropety.filter_factor;
			}
			if(temIsPrivExpression!=null&&typeof(temIsPrivExpression)!='underfined'&&!temIsPrivExpression)////是否启用人员范围
			{
				isPrivExpression=temIsPrivExpression;
				orgId='';
			}*/
			var f = document.getElementById("getHandTemp");
			var p = new PersonPicker({
				addunit:false, //是否可以添加单位
				adddepartment:false, //是否可以添加部门
				multiple: true,//为true可以多选
				orgid:orgId,
				isPrivExpression:isPrivExpression,//是否启用人员范围（含高级条件）
				extend_str:"template/"+templateMain_me.templPropety.tab_id,
				validateSsLOGIN:false,//是否启用认证库
				selectByNbase:true,//是否按不同人员库显示
				deprecate :'',//不显示的人员
				//nbases:templateMain_me.templPropety.nbases,
				defaultSelected:defaultSelectedPerson,
				text: "确定",
				callback: function (c) {
					var staffids = "";
					var errerMsg="";
					for (var i = 0; i < c.length; i++) {
						if(c[i].onlyName==undefined||c[i].onlyName==''){
							if(errerMsg.length>0){
								errerMsg+="、";
							}
							errerMsg+=c[i].name;
						}else{
							staffids += c[i].name + ":"+c[i].onlyName+"、";
						}
						
					}
					 var value = staffids.substring(0,staffids.length-1);
				     //校验内容长度
					 var vaLength = getWordsTrueLength(value,true);
					 if(field.fldType=="A"&&!Ext.isEmpty(field.limitlength)){
						 if(value.length>0){
						    var a = value.split(""); 
						    for (var i=0;i<a.length;i++){ 
						        if (a[i].charCodeAt(0)<299) {
						        	vaLength++; 
						        } else { 
						        	vaLength+=3; 
						        } 
						    }
						 }
					 }
					 var limitlength = 0;
					 if(!Ext.isEmpty(field.limitlength)){
						 limitlength = field.limitlength;
					 }else{
						 limitlength = field.fldLength;
					 }
					 
				     if(limitlength!=0&&limitlength!=10&&vaLength>limitlength){
				        Ext.showAlert("该文本的字数限制"+limitlength+"个！",function(){
				        	//截取字符串按'`'符号截取，到长度小于限制长度
				        	var value_ = subStringValue(value,limitlength,vaLength);
				        	obj.value=value_;	
							var valueItem =templateCard_me.getValueItem(field.uniqueId);
							valueItem.setValue(value_);
							if(errerMsg.length>0){
								Ext.showAlert(errerMsg+"的唯一性指标值为空，不能保存。");
							}
				        });
				     }else{
				    	obj.value=value;	
						var valueItem =templateCard_me.getValueItem(field.uniqueId);
						valueItem.setValue(value);
						if(errerMsg.length>0){
							Ext.showAlert(errerMsg+"的唯一性指标值为空，不能保存。");
						}
				     }
				}
			}, f);
			p.open();
		 }else{
	  	 //obj.select();//默认选中
	}
	}
	if (field.codeSetId!='' && field.codeSetId!='0') {//代码型
	 	var idList= new Array();
	 	var flagUM = false;
	 	var flagUN = false;
		var recordSet = templateCard_me.getCurRecordSet();
		for (var i=0;i<recordSet.getFieldCount();i++){
		    var record = recordSet.fields[i];
		    if(!(record.pageId==templateCard_me.getCurrPageId()))
		    {
		    	continue;
		    }
		    if(field.fldName.toLowerCase()=='e0122'){
		        if(record.fldName.toLowerCase() =='b0110_2'){
		        	var keyvalue = record.keyValue;
		        	if(keyvalue!=''){
		        		templateCard_me.activeElement.setAttribute('parentid',keyvalue);
		        	}
		        	var id = templateCard_me.activeElement.getAttribute("id");
		        	if(id!=''){
		        		break;
		        	}
		        }
		    }
		    else if(field.fldName.toLowerCase()=='e01a1'){
		    	if(record.fldName.toLowerCase()=='e0122_2'&&!flagUM){//部门
		        	var keyvalue = record.keyValue;
		        	if(keyvalue!=''){
		        		flagUM=true;
		        		templateCard_me.activeElement.setAttribute('parentid',keyvalue);
		        	}
		        	var id = templateCard_me.activeElement.getAttribute("id");
		        	if(id!=''){
		        	}
		        	if(flagUN&&flagUM){
		        		break;
		        	}
		        }
		        else if(record.fldName.toLowerCase()=='b0110_2'&&!flagUN){//单位
		        	var keyvalue = record.keyValue;
		        	if(keyvalue!=''){
		        		flagUN=true;
		        		templateCard_me.activeElement.setAttribute('parentid',keyvalue);
		        	}
		        	var id = templateCard_me.activeElement.getAttribute("id");
		        	if(id!=''){
		        	}
		        	if(flagUN&&flagUM){
		        		break;
		        	}
		        }
		    }
		    if(field.fatherRelationField!=''&&field.fatherRelationField!=undefined){//如果与其他指标联动，获取联动指标的值作为父节点
				if(record.uniqueId.toLowerCase()==field.fatherRelationField.toLowerCase()){
					var keyvalue = record.keyValue;
					if(keyvalue!=''){
						templateCard_me.activeElement.setAttribute('parentid',keyvalue);
						break;
		}
	}
			}
		}
	}
	else if (field.fldType=="D"){//日期型
		templateCard_me.isafterselect=false;
	    var unique_id =templateCard_me.activeElement.getAttribute("id");
		var idList= new Array();
		idList.push(unique_id);
		setDateEleConnect(idList);
	}
}
function subStringValue(value,limitlength,vaLength){
	var index = value.lastIndexOf("、");
	var value_ = value.substring(0,index);
	//var vaLength = getWordsTrueLength(value_,true);
	if(vaLength>limitlength){
		value_=subStringValue(value_,limitlength);
	}else{
		return value_;
	}
	return value_;
}
/*代码型指标回调函数**/
function afterSelectCode(id,text,isEnter)
{
	//获取指标
	var field=templateCard_me.getCurFieldItem();
	//获取数据record
	var valueField=templateCard_me.getCurValueItem();
	//liuyz bug25617 begin 同一个模板中插入了相同的单位、部门指标，例如输入了其中一个单位指标，另一个单位指标的值没有自动同步过来，需要点击保存或者计算按钮才能出来
	var recordSet = templateCard_me.getCurRecordSet();
	if(id!=null&&text!=null&&id.length>0&&text.length>0)
	{
		if(field.fldName.toUpperCase()=="B0110")
		{
			for(var i=0;i<recordSet.getFieldCount();i++){
			    var record = recordSet.fields[i];
			    if(valueField.fldName.toUpperCase()==record.fldName.toUpperCase()&&valueField.uniqueId!=record.uniqueId&&valueField.pageId==record.pageId)
			    {
			    	document.getElementById(record.uniqueId).value=text;
			    	record.setValue(id);
			    }
			}
		}

        if(valueField.keyValue!=id)//liuyz 
		{
			if(!templateCard_me.isHaveChange)
			{
			    templateCard_me.isHaveChange=true;
			}
		}
		//liuyz bug 25617 end
		if(field.codeSetId!=''&& field.codeSetId!='0'){//普通字符型
			valueField.setValue(id);
			if (field.fldName.toUpperCase()=="B0110"){
				
			}
			//liuyz 选择岗位部门不自动级联
			if((field.codeSetId=='UM'&&field.fldName.toLowerCase()=='e0122')||
				(field.codeSetId=='@K'&&field.fldName.toLowerCase()=='e01a1')||
				(field.codeSetId=='UN'&&field.fldName.toLowerCase()=='b0110')||
				(field.codeSetId=='UM'&&field.fldName.toLowerCase()=='e0122_2')||
				(field.codeSetId=='@K'&&field.fldName.toLowerCase()=='e01a1_2')||
				(field.codeSetId=='UN'&&field.fldName.toLowerCase()=='b0110_2')){
				templateCard_me.downToUp(field.codeSetId,id,text);
			}
			if(field.codeSetId!=''&&field.codeSetId!='0'&&field.fatherRelationField!=undefined&&field.fatherRelationField!=''){//如果被其他指标关联，清空关联指标的值
				templateCard_me.downToUpOthers(field.codeSetId,id,text,field.fatherRelationField,field.uniqueId);
			}
			if(field.codeSetId!=''&&field.codeSetId!='0'&&field.childRelationField!=''&&field.childRelationField!=undefined){
				var chidlRelationList=field.childRelationField.split(",");
				for(var index=0;index<chidlRelationList.length;index++){
					clearValueById(chidlRelationList[index]);
				}
			}
			//templateCard_me.activeElement.focus();//选择后获得焦点  暂时去掉
			//templateCard_me.activeElement.value = text;
		}
	}
	if(isEnter!=null&&typeof(isEnter)!= "undefined"&&isEnter)
	{
		getNextElement(field.uniqueId);
		var treepanelitem = Ext.ComponentQuery.query('treepanel');
		if(treepanelitem.length>0){
			Ext.each(treepanelitem,function(e){
				if(e.style.indexOf('z-index:100000000')!=-1)
				{
					e.close();
				}
			});
		}
		
	}
}

/*日期型指标回调函数**/
function afterSelectDate(id)
{
	var uniqueid =templateCard_me.activeElement.getAttribute("id");
	//获取指标
    var field=templateCard_me.getCurFieldItem();
	//获取数据record
	var valueField=templateCard_me.getCurValueItem();
	valueField.setValue(id);
	templateCard_me.isafterselect=true;//是否是选择后赋值
	//格式化日期
	var date= parseTemplateDate(id);
	var disValue = formatTemplateDate(date,field.format);
	templateCard_me.activeElement.value=disValue;
	var recordSet = templateCard_me.getCurRecordSet();
	if(recordSet.getField(valueField.uniqueId).disValue+""!=disValue+"")
	{
		if(!templateCard_me.isHaveChange)
   		{
   			templateCard_me.isHaveChange=true;
   		}
	}
}

/**
 * 日期 onchange事件触发的方法
 * @param {} obj
 */
function contentChange(obj){
	//获取指标
	var field=templateCard_me.getCurFieldItem();
	if (field.rwPriv!="2"){//无写权限
	   return;
	}
	//获取数据record
	var valueField=templateCard_me.getCurValueItem();
	if(field.fldType == "D"){
			var tempvalue=obj.value;
			var recordSet = templateCard_me.getCurRecordSet();
			if(recordSet.getField(field.uniqueId).disValue!=obj.value)
			{
				if(!templateCard_me.isHaveChange)
	    		{
	    			templateCard_me.isHaveChange=true;
	    		}
			}
			if(tempvalue){
				//获取指标
				var field=templateCard_me.getCurFieldItem();
				if(valueField.keyValue==""||templateCard_me.isafterselect==false){
	    			var ischeck = checkDateFormat(tempvalue,field.format);
	    			if(ischeck=="false"){
	    				valueField.setValue("");
	    				var uniqueId = valueField.uniqueId;
	    				document.getElementById(uniqueId).value='';
	    			}else{
	    				tempvalue = ischeck;
	    				//valueField.setValue(tempvalue);
	    				var uniqueId = valueField.uniqueId;
	    				document.getElementById(uniqueId).value=tempvalue;
	    				//获取当前年月日 时分
    				    var date = new Date();
					    var seperator1 = "-";
					    var seperator2 = ":";
					    var month = date.getMonth() + 1;
					    var strDate = date.getDate();
					    if (month >= 1 && month <= 9) {
					        month = "0" + month;
					    }
					    if (strDate >= 0 && strDate <= 9) {
					        strDate = "0" + strDate;
					    }
					    var currentdate = '';
					    if(field.format==25){
					    	currentdate = date.getFullYear() + seperator1 + month + seperator1 + strDate
            								+ " " + date.getHours() + seperator2 + date.getMinutes();
					    }else
					    	currentdate = date.getFullYear() + seperator1 + month + seperator1 + strDate;
					    var date = getTemplateDate(tempvalue,field.format,currentdate);
					    if(date){
							var format = null;
							if(field.format==25){
			        			format = "yyyy-MM-dd hh:mm";
					        }else{
					        	format = "yyyy-MM-dd";
					        }
							var newdate = dateFormate(date,format);
							valueField.setValue(newdate);
						}
	    			    //valueField.setValue(replaceAll(tempvalue,".","-"));
	    			}
				}else{
					var date = getTemplateDate(tempvalue,field.format,valueField.keyValue);
					if(date){
						var format = null;
						if(field.format==25){
		        			format = "yyyy-MM-dd hh:mm";
				        }else{
				        	format = "yyyy-MM-dd";
				        }
						var newdate = dateFormate(date,format);
						valueField.setValue(newdate);
					}
				}
			}else{
				valueField.setValue("");
			}
	}
}

/*模板单元格的onblur事件**/
function processBlur(obj)
{ 
	if (getBrowseVersion() && !isCompatibleIE()){
		templateMain_me.canToSave = true;
	}
	//获取指标
	var field=templateCard_me.getCurFieldItem();
	if (field.rwPriv!="2"){//无写权限
	   return;
	}
	//获取数据record
	var valueField=templateCard_me.getCurValueItem();
	var desc=field.fldDesc;  
	var recordSet = templateCard_me.getCurRecordSet();
	switch(field.fldType){
		case "A":///字符串型指标，如姓名			
			if(field.codeSetId==''|| field.codeSetId=='0'){//普通字符型
				var tempvalue=obj.value;
				/*var tempindex=tempvalue.indexOf(",");
				if(tempvalue.indexOf(",")>0){
					tempvalue=replaceAll(tempvalue,",", "`g`g" );
				}
				if(tempvalue.indexOf("，")>0){
					tempvalue=replaceAll(tempvalue,"，", "`g`g" );
				}*/
				if(recordSet.getField(field.uniqueId).keyValue!=tempvalue)
				{
					if(!templateCard_me.isHaveChange)
		    		{
		    			templateCard_me.isHaveChange=true;
		    		}
				}
				valueField.setValue(tempvalue);
				if(field.fldName == "A0101" || field.fldName == "codeitemdesc"){//实时更新姓名
					if(!!templateCard_me){
						templateCard_me.changedA0101 = tempvalue;
						templateCard_me.changeA0101(tempvalue);
					}
				}
			}
			else  //代码型指标前台置空时，需销毁
			{  
				var tempvalue=obj.value;
				if(tempvalue==''){
					valueField.setValue('');
					obj.setAttribute('parentid','');
					if(field.codeSetId=='UM'||field.codeSetId=='UN'){
					var recordSet = templateCard_me.getCurRecordSet();
					for (var i=0;i<recordSet.getFieldCount();i++){
					    var record = recordSet.fields[i];
					    if(!(record.pageId==templateCard_me.getCurrPageId()))
					    {
					    	continue;
					    }
					    if(field.codeSetId=='UM'&&(field.fldName.toLowerCase()=='e0122'||field.fldName.toLowerCase()=='e0122_2')){
					        if(record.fldName.toLowerCase()=='b0110_2'){
					        	var id = record.uniqueId;					
						        	if(document.getElementById(id).getAttribute('selectcode')=='1'){
							        	var valueItem = templateCard_me.getValueItem(id);
							        	valueItem.setValue('');
							        	document.getElementById(id).value='';
							        	document.getElementById(id).setAttribute('parentid','');
							        	document.getElementById(id).setAttribute('selectcode','0');
						        	}
					        }
					        if(record.fldName.toLowerCase()=='e01a1_2'||record.fldName.toLowerCase()=='e0122_2'){
				        	   	var id = record.uniqueId;
				        	   	var valueItem = templateCard_me.getValueItem(id);
					        	valueItem.setValue('');
					        	document.getElementById(id).value='';
									document.getElementById(id).setAttribute('parentid','');
					        }
					    }
					    else if(field.codeSetId=='UN'&&(field.fldName.toLowerCase()=='b0110'||field.fldName.toLowerCase()=='b0110_2')){
					    	if(record.fldName.toLowerCase()=='e0122_2'||record.fldName.toLowerCase()=='e01a1_2'||record.fldName.toLowerCase()=='b0110_2'){//部门
								var id = record.uniqueId;
								var valueItem = templateCard_me.getValueItem(id);
					        	valueItem.setValue('');
					        	document.getElementById(id).value='';
									document.getElementById(id).setAttribute('parentid','');
					        }
					    }
					}
				}
					if(field.childRelationField!=''&&field.childRelationField!=undefined){
						var chidlRelationList=field.childRelationField.split(",");
						for(var index=0;index<chidlRelationList.length;index++){
							clearValueById(chidlRelationList[index]);
						}
					}
					
				}else{//syl 57387  人事异动表单中的代码项指标，可以随便编辑且可以保存，提交，只是提交后库当中没数据 
					if((field.codeSetId!='' && field.codeSetId!='0')&&(valueField.keyValue==""||trim(valueField.keyValue).length==0||!isNaN(obj.value)&&isNaN(oldValue)))
				    {
				    	obj.value="";
				    }
				}
			}
			break;
		case "M"://备注型指标
			var tempvalue=obj.value;
			/*var tempindex=tempvalue.indexOf(",");
			if(tempvalue.indexOf(",")>0){
				tempvalue=replaceAll(tempvalue,",", "`g`g" );
			}
			if(tempvalue.indexOf("，")>0){
				tempvalue=replaceAll(tempvalue,"，", "`g`g" );
			}*/
			if(recordSet.getField(field.uniqueId).keyValue!=tempvalue)
			{
				if(!templateCard_me.isHaveChange)
	    		{
	    			templateCard_me.isHaveChange=true;
	    		}
			}
			valueField.setValue(tempvalue);
			break;
		case "N"://数值型指标
			if((obj.value.length>0)&&!checkIsNum(obj.value)){//处理将数字型为空的除外
				validateFlag=false;
				Ext.showAlert('请输入数字！');
				return;			
			}
			var length=field.format;//field.fldDecLength;
			if (length>0){//float型
				if(obj.value.length>0){
					obj.value=Digit.round(obj.value, length)
				}
			}
			else {//整形
				if(isIntOrNull(obj.value)){
					obj.setAttribute("keyValue",obj.value);
				}
				else {			
					validateFlag=false;
					Ext.showAlert(desc+"只能是整数！");
					/**bug52281 V76人事异动 将数值型指标小数点位数设为0，表单中对指标维护带小数位数的数据，首次保存提示只能为整数，再次点保存不提示*/
					obj.value=Digit.round(obj.value)
					return;
				}
			}	
			if(recordSet.getField(field.uniqueId).keyValue!=obj.value)
			{
				if(!templateCard_me.isHaveChange)
	    		{
	    			templateCard_me.isHaveChange=true;
	    		}
			}
			valueField.setValue(obj.value);
			break;
	}
}
//根据id值递归查找有没有孩子节点，并且清空字段值。
function clearValueById(id){
	var field=templateCard_me.getFieldItem(id);
	if(field&&field.chgState=='2'){
	var valueItem = templateCard_me.getValueItem(id);
	valueItem.setValue('');
	if(document.getElementById(id)){
		document.getElementById(id).value='';
		document.getElementById(id).setAttribute('parentid','');
	}
	if(field&&field.childRelationField!=''&&field.childRelationField!=undefined){
		var chidlRelationList=field.childRelationField.split(",");
		for(var index=0;index<chidlRelationList.length;index++){
			clearValueById(chidlRelationList[index]);
		}
	}
	}
}

//校验备注型文本的长度 
function jugeLength(elem,length){
    var value = elem.value;
    var vaLength = getWordsTrueLength(value,true);
    if(vaLength>length){
    	if (getBrowseVersion() && !isCompatibleIE()){//非兼容模式
    		templateMain_me.canToSave = false;
    	}
        Ext.showAlert("该文本的字数不超过"+length+"个,目前已输入"+vaLength+"个！",function(){
        	elem.focus();
        });
    }else
    	processBlur(elem);
}
//校验字符型长度255转为备注型文本的长度 
function jugeNormalLength(elem,length){
    var val = elem.value;
    var vaLength = 0;
		if(val.length>0){
	    var a = val.split(""); 
	    for (var i=0;i<a.length;i++){ 
	        if (a[i].charCodeAt(0)<299) {
	        	vaLength++; 
	        } else { 
	        	vaLength+=3; 
	        } 
	    }//计算长度，汉字算3个长度
		}
    if(vaLength>length){
    	if (getBrowseVersion() && !isCompatibleIE()){//非兼容模式
    		templateMain_me.canToSave = false;
    	}
        Ext.showAlert("该文本的字数不超过"+length+"个,目前已输入"+vaLength+"个！",function(){
        	elem.focus();
        });
    }else
    	processBlur(elem);
}

 

/**
 *上传照片
 * */
function upload_picture(uniqueId){ 
	if (templateCard_me.object_id==null||templateCard_me.object_id==""){
		return;
	}
	var field= templateCard_me.getFieldItem(uniqueId);
	if (field.rwPriv!="2"){//无写权限
       return;
    }
	var element =Ext.getDom(uniqueId);
	var templPropety =templateCard_me.templPropety;
    var object_id=templateCard_me.object_id;   
	Ext.require('TemplateCardUL.AddPhoto',function(){
		Ext.create("TemplateCardUL.AddPhoto",{templPropety:templPropety,img_id:uniqueId,object_id:object_id});
	})

}

//----------------------------------打印--------开始
var templateObjId="TmplPreview1";
/**用于判断插件是否加载完成**/
function isLoad(obj){
	var flag = true;
	try{
		obj.SetUrl("test");
	}catch(e){
		flag = false;
	}
	return flag;
}

/**
 *打印前检查及准备数据
 * */
function print(){
	if(!Ext.isIE){
		Ext.showAlert("此功能需要插件支持，请在IE浏览器下使用此功能！");
		return;
	}
	templateTool_me.save('true');//打印之前执行保存
	if(!AxManager.setup("printPreviewdiv", templateObjId, 0, 0, print,AxManager.tmplpkgName))
		return;
	var obj = document.getElementById(templateObjId);
    var isload = isLoad(obj);
    if (!isload) {
        return;
    }
	var templPropety,object_id='',signatureType ;
	//liuyz 2016-12-28 列表打印 判断templateCard_me是否定义，确定是列表状态还是卡片状态调用的打印
	if(templateMain_me.templPropety.view_type=="list"){
		object_id = TemplateList.object_id;
		signatureType = TemplateList.signatureType
    	templPropety = TemplateList.templPropety;
    }else{
    	object_id = templateCard_me.object_id;
		signatureType = templateCard_me.signatureType
    	templPropety = templateCard_me.templPropety;
    }
	var map = new HashMap();
	initPublicParam(map,templPropety);
	if(signatureType==0&&Ext.isIE)
		CreateJgkjSignatureJif(signatureType,object_id,templPropety.infor_type,"","1");
	Rpc( {
		functionId : 'MB000020012',
		async : false,
		success : printOK
	}, map);
}
/**
 *调用打印控件
 * */
function printOK(form){
	var result = Ext.decode(form.responseText);
	if (result.succeed) {
		var judgeisllexpr = result.judgeisllexpr;
		if (judgeisllexpr != null && judgeisllexpr != "") {
			Ext.showAlert(judgeisllexpr);
			return;
		}
		var objStr = result.objStr;
		var hosturl = result.hosturl;
		var dbtype = result.dbtype;
		var username = result.username;
		var userFullName = result.userFullName;
		var superUser = result.superUser;
		var nodepriv = result.nodepriv;
		var tablepriv = result.tablepriv;
		var _version = result._version;
		var usedday = result.usedday;
		
		/**每次都初始化一下吧**/
		var obj = document.getElementById(templateObjId);
		obj.SetURL(hosturl);
		obj.SetDBType(dbtype);// dbtype=> 1: MSSQL, 2: ORACLE, 3: DB2
		obj.SetUserName(username);     
		obj.SetSuperUser(superUser);  // 1为超级用户,0非超级用户
		obj.SetUserMenuPriv(nodepriv);  // 指标权限, 逗号分隔, 空表示全权
		obj.SetUserTablePriv(tablepriv);  // 子集权限, 逗号分隔, 空表示全权
		obj.SetHrpVersion(_version);
		obj.SetTrialDays(usedday,"30");
		//开始调用打印控件
		loadPrintObj(obj,objStr)
	} else {
		Ext.showAlert(result.message);
	}
	
}
/*
* 调用打印控件
 * A0100参数格式： 模板, 员工申请临时表: <NBASE></NBASE><A0100></A0100> 
 模板归档: <ArchiveID></ArchiveID><NBASE></NBASE><A0100></A0100>
 * 审批临时表: <INS_ID>实例号</INS_ID><NBASE></NBASE><A0100></A0100>
 */
function loadPrintObj(obj,objStr){
	//liuyz 2016-12-28 列表打印 判断templateCard_me是否定义，确定是列表状态还是卡片状态调用的打印
	var templPropety;
	if(templateMain_me.templPropety.view_type=="list"){
    	templPropety = TemplateList.templPropety;
    }else{
    	templPropety = templateCard_me.templPropety;
    }
	//卡片类型： 1: 模板 2: 模板归档信息 3: 员工申请临时表, g_templet_模板号 4: 审批临时表, templet_模板号
	var cardtype = 1;
	if (templPropety.module_id=="9"){
	   cardtype = 3;
	}
	if (templPropety.task_id != "0")
		cardtype = 4;
	var objarr = new Array();
	var temps = objStr.split("`");
	for ( var i = 0; i < temps.length; i++){
	  objarr.push(temps[i]);
	}
		
		
    obj.SetTemplateID(templPropety.tab_id);
    obj.SetTemplateType(cardtype);
    obj.ClearObjs();
	if (objarr.length > 0) {
		var contentlist = getprintobjContent(objarr);
		for ( var i = 0; i < contentlist.length; i++) {
			obj.AddObj(contentlist[i]);
		}
		// CreateSignatureJif("1"); todo 生成签章图片
		try { obj.SetJSessionId(AxManager.getJSessionId()); } catch(err) {}
		obj.ShowCardModal();
	}	  	
}

function getprintobjContent(objlist){
	var contentlist=new Array();
	for(var i=0;i<objlist.length;i++)
	{
		var strarr;
		var content="";
		strarr=objlist[i].split("|");
		if (strarr[2]!="0"){
	 	content=content+"<INS_ID>"
	      content=content+strarr[2];
	      content=content+"</INS_ID>"  
		}
		content=content+"<NBASE>"
		content=content+strarr[0];
		content=content+"</NBASE>"
	
		content=content+"<A0100>"
		content=content+strarr[1];
		content=content+"</A0100>"
		contentlist.push(content);
		content="";
	}//for i loop end.
	
	return contentlist;
}


//----------------------------------打印--------结束

//----------------------------------导出PDF--------开始


function outPdf(flag,downtype,fileType)
{
	//liuyz 2016-12-28 列表增加打印输出菜单 判断templateCard_me是否定义，确定是列表状态还是卡片状态调用的输出
	templateTool_me.save('true');//导出之前执行保存
	var templPropety,object_id='',signatureType,cur_task_id ;
	if(templateMain_me.templPropety.view_type=="list"){
		object_id = TemplateList.object_id;
		cur_task_id = TemplateList.cur_task_id;
		signatureType = TemplateList.signatureType
    	templPropety = TemplateList.templPropety;
		if(flag==1){
			var sel = templateList_me.templateListGrid.tablePanel.getSelectionModel();
			if(sel.getCount()==1){
				object_id = sel.getSelection()[0].data.objectid_e;
				cur_task_id = sel.getSelection()[0].data.realtask_id_e;
			}
		}
    }else{
    	object_id = templateCard_me.object_id;
    	cur_task_id = templateCard_me.cur_task_id;
		signatureType = templateCard_me.signatureType
    	templPropety = templateCard_me.templPropety; 
    }
	var map = new HashMap();
    initPublicParam(map,templPropety);
	map.put("infor_type", templPropety.infor_type);
	map.put("flag", flag+"");
	map.put("object_id", object_id);
	map.put("outtype", "0");
	map.put("officeOrWps",fileType);
	map.put("downtype", downtype+"");
	map.put("out_pages",templateMain_me.out_pages);//导出页控制
	if(flag==1)
		map.put("cur_task_id", cur_task_id+'');
	map.put("allNum",templateTool_me.getTotalCount());
	if(signatureType==0&&Ext.isIE)
		CreateJgkjSignatureJif(signatureType,object_id,templPropety.infor_type,flag+"","0");
	Ext.MessageBox.wait("正在执行导出PDF操作，请稍候...", "等待");
	var isarchive = getTemplPropetyOthParam("isarchive");
    var functionid = "MB000020014";
    if(isarchive=='0'){
    	functionid = "MB00008005";
    }
	Rpc( {
	    functionId : functionid,
	    async : true,
	    success : showPdf
	}, map);   
}

function showPdf(form){
	  Ext.MessageBox.close();
      var result = Ext.decode(form.responseText);
      if (result.succeed) {
         var judgeisllexpr = result.judgeisllexpr;
         if(judgeisllexpr!=null && judgeisllexpr!="1")
            Ext.showAlert(judgeisllexpr);
         else 
         {
           var filename=result.filename; 
           //业务申请 直接打开
           //if (templateCard_me.templPropety.module_id=="9" && templateCard_me.templPropety.task_id=="0" ){
           if (true){
        	 if(Ext.isSafari){//safari 浏览器无法预览 改为直接下载
        		 window.location.target="_blank";
                 window.location.href="/servlet/vfsservlet?fromjavafolder=true&fileid="+filename;   
        	 }else{
        		 var url = "/servlet/vfsservlet?fromjavafolder=true&fileid="+filename;
        		 var win=open(url,"pdf");
        	 }
           }
           else {
             window.location.target="_blank";
             window.location.href="/servlet/vfsservlet?fromjavafolder=true&fileid="+filename;   
           }
         }      
      } else {
    	  var message = result.message;
		  if(message&&message.indexOf("拆分审批")!=-1){
			  templateTool_me.checkSpllit(message);
		  }else
			  Ext.showAlert(result.message);
      }
}

 //----------------------------------导出PDF--------结束
   
function printInform(){//打印高级花名册
	
	var templPropety,selectSize='';
	if(templateMain_me.templPropety.view_type=="list"){
    	templPropety = TemplateList.templPropety;
    	selectSize = templateList_me.templateListGrid.tablePanel.getSelectionModel().getSelection().length;
    }else{
    	templPropety = templateCard_me.templPropety;
    	selectSize = templateCard_me.personListGrid.tablePanel.getSelectionModel().getSelection().length;
    }
    if(selectSize==null||selectSize==""||typeof(selectSize)=='undefined'||selectSize==0)
    {
    	Ext.showAlert("请选择要打印的人员！");
    	return;
    }
	
    var tabid=templPropety.tab_id;
    var ins_id=templPropety.ins_id;
    var spflag = '1';
    if(ins_id!='0'){
        spflag='2';
    }
    /**高级花名册过滤人员的sql已经被后台处理，前台不需要传递了xcs2014-9-25**/
    var url = "/general/muster/hmuster/searchHroster.do?b_search=link`nFlag=5`spflag="+spflag+"`relatTableid="+tabid+"`closeWindow=2`print=1";
    var framesurl = "/general/query/common/iframe_query.jsp?src="+$URL.encode(url);
    var return_vo = window.open(framesurl,"","left=0,top=0,width="+screen.availWidth+",height="+screen.availHeight*0.95+",scrollbars=yes,center=yes,toolbar=no,menubar=no,location=no,resizable=yes,status=no");  //29106  高度全屏会遮挡新窗口下方的滚动条
} 

//导出word
function outword(flag,downtype,fileType){
	templateTool_me.save('true');//导出之前执行保存
	var templPropety,object_id='',signatureType,cur_task_id ;
	if(templateMain_me.templPropety.view_type=="list"){
		object_id = TemplateList.object_id;
		cur_task_id = TemplateList.cur_task_id;
		signatureType = TemplateList.signatureType
    	templPropety = TemplateList.templPropety;
		if(flag==1){
			var sel = templateList_me.templateListGrid.tablePanel.getSelectionModel();
			if(sel.getCount()==1){
				object_id = sel.getSelection()[0].data.objectid_e;
				cur_task_id = sel.getSelection()[0].data.realtask_id_e;
			}
		}
    }else{
    	object_id = templateCard_me.object_id;
    	cur_task_id = templateCard_me.cur_task_id;
		signatureType = templateCard_me.signatureType
    	templPropety = templateCard_me.templPropety; 
    }
	var map = new HashMap();
    initPublicParam(map,templPropety);
	map.put("infor_type", templPropety.infor_type);
	map.put("flag", flag+"");
	map.put("object_id", object_id);
	map.put("outtype", "1");
	map.put("officeOrWps",fileType);
	map.put("downtype", downtype+"");
	if(flag==1)
		map.put("cur_task_id", cur_task_id+'');
	map.put("allNum",templateTool_me.getTotalCount());
	map.put("out_pages",templateMain_me.out_pages);//导出页控制
	if(signatureType==0&&Ext.isIE)
		CreateJgkjSignatureJif(signatureType,object_id,templPropety.infor_type,flag+"","0");
	Ext.MessageBox.wait("正在执行导出WORD操作，请稍候...", "等待");
	var isarchive = getTemplPropetyOthParam("isarchive");
    var functionid = "MB000020014";
    if(isarchive=='0'){
    	functionid = "MB00008005";
    }
	Rpc( {
	    functionId : functionid,
	    async : true,
	    success : showDoc
	}, map);  
}

function showDoc(form){
	  Ext.MessageBox.close();
      var result = Ext.decode(form.responseText);
      if (result.succeed) {
         var judgeisllexpr = result.judgeisllexpr;
         if(judgeisllexpr!=null && judgeisllexpr!="1")
            Ext.showAlert(judgeisllexpr);
         else 
         {
           var filename=result.filename; 
           //业务申请 直接打开
             window.location.target="_blank";
             window.location.href="/servlet/vfsservlet?fromjavafolder=true&fileid="+filename;   
         }      
      } else {
    	  var message = result.message;
		  if(message&&message.indexOf("拆分审批")!=-1){
			  templateTool_me.checkSpllit(message);
		  }else
			  Ext.showAlert(result.message);
      }
}

//liuyz  登记表导出pdf start
function printcardpdf(tabid,flag){
	var templPropety,object_id='',signatureType,cur_task_id ;
	if(templateMain_me.templPropety.view_type=="list"){
		object_id = templateList_me.object_id;
		cur_task_id = templateList_me.cur_task_id;
		signatureType = templateList_me.signatureType;
    	templPropety = templateList_me.templPropety;
    }else{
    	object_id = templateCard_me.object_id;
    	cur_task_id = templateCard_me.cur_task_id;
		signatureType = templateCard_me.signatureType;
    	templPropety = templateCard_me.templPropety; 
    }
    if(object_id==null||object_id==""||typeof(object_id)=='undefined')
    {
    	Ext.showAlert("请选择需要生成PDF的人员!");
    	return;
    }
	var map = new HashMap();
    initPublicParam(map,templPropety);
	map.put("infor_type", templPropety.infor_type);
	map.put("flag", flag+"");
	map.put("object_id", object_id);
	map.put("cardid",tabid);
	if(flag==1)
		map.put("cur_task_id", cur_task_id+'');
	if(signatureType==0&&Ext.isIE)
		CreateJgkjSignatureJif(signatureType,object_id,templPropety.infor_type,flag+"","0");
	Ext.MessageBox.wait("正在执行导出PDF操作，请稍候...", "等待");
	Rpc( {
	    functionId : 'MB00002025',
	    async : false,
	    success : showPdf
	}, map);   
}
var a_tabid="";
var a_setname="";
function printActive(id,flag){
	if(!Ext.isIE){
		Ext.showAlert("此功能需要插件支持，请在IE浏览器下使用此功能！");
		return;
	}
	if(!AxManager.setup("printPreviewdiv", templateObjId, 0, 0, print,AxManager.tmplpkgName))
		return;
	var obj = document.getElementById(templateObjId);
    var isload = isLoad(obj);
    if (!isload) {
        return;
    }
	
	if(id)
	{
		a_tabid=id;
	}
	var templPropety,object_id='',signatureType,selectSize='';
	//liuyz 2016-12-28 列表打印 判断templateCard_me是否定义，确定是列表状态还是卡片状态调用的打印
	if(templateMain_me.templPropety.view_type=="list"){
		object_id = templateList_me.object_id;
		signatureType = templateList_me.signatureType;
    	templPropety = templateList_me.templPropety;
    	selectSize = templateList_me.templateListGrid.tablePanel.getSelectionModel().getSelection().length;
    }else{
    	object_id = templateCard_me.object_id;
		signatureType = templateCard_me.signatureType;
    	templPropety = templateCard_me.templPropety;
    	selectSize = templateCard_me.personListGrid.tablePanel.getSelectionModel().getSelection().length;
    }
    //liuyz 没有选中人员提示用户为选中打印对象
    if(selectSize==null||selectSize==""||typeof(selectSize)=='undefined'||selectSize==0)
    {
    	Ext.showAlert("未选中打印对象!");
    	return;
    }
	var map = new HashMap();
	initPublicParam(map,templPropety);
	if(signatureType==0&&Ext.isIE)
		CreateJgkjSignatureJif(signatureType,object_id,templPropety.infor_type,"","1");
	Rpc( {
		functionId : 'MB00002026',
		async : false,
		success : printCard
	}, map);
}
var CardPreview1Flag = 0;
function printCard(outparamters)
{
	var result = Ext.decode(outparamters.responseText);
    var personlist=result.personlist;  
	var object_id="";
    if(templateMain_me.templPropety.view_type=="list"){
		object_id = templateList_me.object_id;
    }else{
    	object_id = templateCard_me.object_id;
    }
    if(object_id==null||object_id==""||typeof(object_id)=='undefined')
    {
    	Ext.showAlert("请选择需要打印登记表的人员!");
    	return;
    }
    var arr=object_id.split("`");
    var basepre=arr[0];
    var a0100=arr[1];
    var card = document.getElementById(templateObjId);
    var cardobj = isLoad(card);
    if(cardobj==true){
	   CardPreview1Flag++;
	   printCardLoadOk(card,basepre,personlist,result);
    }else{
       var timer = setInterval(function(){ 
	       CardPreview1Flag++;
	       card= document.getElementById(templateObjId);
	       cardobj = isLoad(card);
	   	   if(cardobj==true){
	    		printCardLoadOk(card,basepre,personlist,result);
	    		clearInterval(timer);
	       }else if(CardPreview1Flag==5){
	    		Ext.showAlert("插件加载失败！");
	    		CardPreview1Flag=0;
	    		clearInterval(timer);
	       }    
    	},2000);
    }	
}
  /**卡片打印调用**/
function printCardLoadOk(obj,basepre,personlist,result){
	var hosturl = result.hosturl;
	var dbtype = result.dbtype;
	var username = result.username;
	var userFullName = result.userFullName;
	var superUser = result.superUser;
	var tables = result.tablepriv;
	var _version = result._version;
	var usedday = result.usedday;
	var fields=result.nodepriv;
    initAciveCard(hosturl,dbtype,username,userFullName,superUser,fields,tables,'CardPreview1');
    obj.SetCardID(a_tabid);
    obj.SetDataFlag("1");
    obj.SetNBASE(basepre);
    obj.ClearObjs();
    for(var i=0;i<personlist.length;i++)
    {
        obj.AddObjId(personlist[i].dataValue);          
    } 
    try { obj.SetJSessionId(AxManager.getJSessionId()); } catch(err) {}
    obj.ShowCardModal();
}
   
function initAciveCard(aurl,DBType,UserName,userFullName,superUser,menuPriv,tablePriv,objname){
     var obj = document.getElementById(templateObjId);       
     if(obj==null)
     {
        return false;
     }
     obj.SetSuperUser(superUser);  // 1为超级用户,0非超级用户
     obj.SetUserMenuPriv(menuPriv);  // 指标权限, 逗号分隔, 空表示全权
     obj.SetUserTablePriv(tablePriv);  // 子集权限, 逗号分隔, 空表示全权         
     obj.SetURL(aurl);
     obj.SetDBType(DBType);
     obj.SetUserName(UserName);
     obj.SetUserFullName(userFullName);
}
	
var isHtmlGlob="";//liuyz 单人模版多人模版支持word输出，是否是转换为html后上传的。
function printPdf(id,flag,tabid,filetype,isHtml,isPrintWord){
	isHtmlGlob=isHtml;
	 var templPropety,object_id='',signatureType,cur_task_id ;
	 if(templateMain_me.templPropety.view_type=="list"){
		object_id = templateList_me.object_id;
		cur_task_id = templateList_me.cur_task_id;
		signatureType = templateList_me.signatureType;
	    templPropety = templateList_me.templPropety;
	 }else{
	    object_id = templateCard_me.object_id;
	    cur_task_id = templateCard_me.cur_task_id;
		signatureType = templateCard_me.signatureType;
	    templPropety = templateCard_me.templPropety; 
	 }
  	 var hashvo=new HashMap(); 
  	 initPublicParam(hashvo,templPropety);       	     
  	 if("1"==flag)
     {
 	    hashvo.put("flag","1");
     }
     else
     {
      	hashvo.put("flag","0");
     }
     hashvo.put("tabid",tabid);
	 hashvo.put("taskid",templPropety.task_id);
	 hashvo.put("ins_id",typeof(templPropety.ins_id)=='undefined'?"0":templPropety.ins_id);
	 hashvo.put("batch_task","");
     hashvo.put("sp_flag",'1');
     hashvo.put("sp_batch","");
     hashvo.put("object_id",object_id);
     hashvo.put("id",id);  	 
     hashvo.put("filetype",filetype);  	 
     hashvo.put("infor_type",templPropety.infor_type);  
     hashvo.put("isPrintWord",isPrintWord); //是否导出word “1” PDF，“0” word
     hashvo.put("allNum",templateTool_me.getTotalCount());
     if(isHtml=="false"){
    	 Ext.MessageBox.wait("正在执行导出PDF操作，请稍候...", "等待");
    		Rpc( {
    		    functionId : 'MB00002029',
    		    async : false,
    		    success : showPdf
    		}, hashvo);  
     }
     else{
    	 Rpc( {functionId : '0570010131',async : false,success : printsuccess}, hashvo);
     }
}
function printsuccess(outparamters){
   	  var result = Ext.decode(outparamters.responseText);
      var judgeisllexpr=result.judgeisllexpr;
      var id=result.id;
      var flag=result.flag;
      var tabid=result.tabid;
      var filetype=result.filetype;
	  if(judgeisllexpr!="1")
	  {
	     Ext.showAlert(judgeisllexpr);
	  }
	  else
	  {
	     printexecute(id,flag,tabid,filetype);
	  }
}
	   	
function printexecute(id,flag,tabid,filetype){
	  var hashvo=new HashMap();
	  hashvo.put("tabid",tabid);
	  hashvo.put("id",id);
	  hashvo.put("flag",flag);
	  hashvo.put("filetype",filetype);
	  if("1"==flag)
	  {
	  	  Rpc( {functionId : '0571000001',async : false,success : outPutTemplateData}, hashvo);
	  }else
	  {
          excecutePDF(table_name,id);
	  }
}  
	 
function outPutTemplateData(outparamters){
     var templPropety,object_id='',signatureType,cur_task_id;
	 if(templateMain_me.templPropety.view_type=="list"){
		object_id = templateList_me.object_id;
		cur_task_id = templateList_me.cur_task_id;
		signatureType = templateList_me.signatureType;
	    templPropety = templateList_me.templPropety;
	 }else{
	    object_id = templateCard_me.object_id;
	    cur_task_id = templateCard_me.cur_task_id;
		signatureType = templateCard_me.signatureType;
	    templPropety = templateCard_me.templPropety; 
	 }
	 var result = Ext.decode(outparamters.responseText);
     var templatefile=result.templatefile;
     var filetype=result.filetype;
     var tabid=result.tabid;
     var fileName=result.fileName;
     var sp_batch="0";
     var batch_task=result.batch_task;
     var ins_id=typeof(templPropety.ins_id)=='undefined'?"0":templPropety.ins_id;
     // var win=window.open("/servlet/OutputTemplateDataServlet?templatefile="+templatefile + "&tabid=" + tabid+"&ins_id="+ins_id+"&sp_batch="+sp_batch+"&=batch_task="+batch_task+"&pre="+basepre+"&a0100="+a0100,"_blank");
     window.location.target="_blank";
     window.location.href = "/servlet/OutputTemplateDataServlet?templatefile="+templatefile + "&tabid=" + tabid+"&ins_id="+ins_id+"&taskid="+templPropety.task_id+"&sp_batch="+sp_batch+"&batch_task="+batch_task+"&object_id="+object_id+"&filetype="+filetype+"&isHtml="+isHtmlGlob+"&fileName="+fileName;
     isHtmlGlob="";
} 
 //liuyz  登记表导出pdf end