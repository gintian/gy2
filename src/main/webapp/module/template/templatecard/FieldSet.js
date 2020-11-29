/////////////////////////定义fieldset开始/////////////////////////////////////////////
/*创建FieldSet**/
function createFieldSet()
{
    var fieldSet=new Object();
    fieldSet.fields=new Array();//所有的字段
    fieldSet.fields.fieldCount=0;//fields中字段的个数。
    fieldSet.addField=fieldset_addField;//向fields属性添加值
    fieldSet.getField=fieldset_getField;
    fieldSet.getFieldCount=fieldset_getFieldCount;
    return fieldSet;
}

/*向fieldset中添加字段。添加完之后，fields就初始化好了。**/
function fieldset_addField(obj)
{
    var fieldset=this;
    try
    {
        var field=new Object;
        field.inputType=obj.inputType;//大文本编辑器类型。
        field.flag=obj.flag;
        field.uniqueId=obj.uniqueId;//唯一值
        field.pageId=obj.pageId;//模板页
        field.fldName=obj.fldName;//指标编号
        field.chgState=obj.chgState;//变化前、后
        field.rwPriv=obj.rwPriv;//读写权限
        field.fldDesc=obj.fldDesc; //指标名称
        field.fldType=obj.fldType;//指标类型 clob blob A N D M 
        field.format=obj.format;//指标显示格式
        field.codeSetId=obj.codeSetId;//是否是代码
        field.fldLength=obj.fldLength;//长度
        field.fldDecLength=obj.fldDecLength;//小数位
        field.subFlag=obj.subFlag;//是否子集
       // field.subXml=obj.subXml;//子集指标xml参数
        field.subFields= new Array();//子集指标列参数
        field.attachmentType=obj.attachmentType;//附件类型
        if (field.subFlag){
          //  parserSubXml(field.subFields,field.subXml);
        }
        field.visible=true; //指标是否显示 目前不知道用处
        field.fatherRelationField=obj.fatherRelationField;//关联的父级指标
        field.childRelationField=obj.childRelationField;//关联的子集指标
        field.defaultValue=obj.defaultValue;//默认值
        field.imppeople=obj.imppeople;//是否启用选人组件
        field.limitlength = obj.limitlength;//大文本限制长度
        field.ismobile = obj.ismobile;//是否是手机页指标
        var i=fieldset.fields.length;
        fieldset.fields[i]=field;
        fieldset.fields[field.uniqueId.toLowerCase()]=i;//方便找下标     
        fieldset.fields.fieldCount++;
        
        return field;
    }
    catch(e)
    {
        Ext.showAlert(e.message);
    }
}

/**解析子集指标属性 xmlcontent为子集的表结构 todo子集类重构后 ，使用子集类展现子集*/
/*此方法已经不用了 有子集类解析
function parserSubXml(subFields,Xml_param)
{
    var xmlrec=getDecodeStr(Xml_param);
    xmlrec=replaceAll(xmlrec,"＜","<");
    xmlrec=replaceAll(xmlrec,"＞",">");
    xmlrec=replaceAll(xmlrec,"＇","'");
    xmlrec=replaceAll(xmlrec,"＂",'"');
    xmlrec=replaceAll(xmlrec,"&","");
    var XMLDoc = loadXMLString(xmlrec);  //位于外部 JavaScript 中，用于加载 XML 字符串
    XMLDoc.async=false;
    var rootNode = XMLDoc.documentElement;//fields那层节点 
    try
    {
        if(rootNode)
        {
            var recNodes = xmlDoc.getElementsByTagName("field"); 
            for(var i=0;i<recNodes.length;i++){
                var node = recNodes[i];                     
                var field={};
                field.fldName=node.getAttribute("fldName");
                field.fldTitle=node.getAttribute("fldTitle");
                field.fldWidth=node.getAttribute("fldWidth");
                field.format=node.getAttribute("format");;//指标显示格式
                field.codeSetId=node.getAttribute("codeSetId");;//是否是代码
                field.fldLength=node.getAttribute("fldLength");;//长度
                field.fldDecLength=node.getAttribute("fldDecLength");;//小数位
                field.align=node.getAttribute("align");;//水平对齐
                field.valign=node.getAttribute("valign");;//垂直对齐
                field.need=node.getAttribute("need");;//必填
                field.defaultValue=node.getAttribute("defaultValue");;//默认值
                field.pre=node.getAttribute("pre");;//前缀        
                field.fldType=node.getAttribute("fldType");;//类型
                field.rwPriv=node.getAttribute("rwPriv");;//权限
                
                subFields[i]= field;
            }
        }
    }
    catch(e)
    {
        Ext.showAlert(e.message);
    }
}
*/
/*根据字段名字或下标取得整个字段对象**/
function fieldset_getField(name)
{
    var fieldSet=this;
    return _fieldset_getField(fieldSet.fields, name);
}

/*根据字段名字或下标取得整个字段对象  辅助函数**/
function _fieldset_getField(fields, name)
{
    var field=null;
    if (typeof(name)=="number")// 根据下表找
    {
        field=fields[name];
    }
    else if (typeof(name)=="string")
    {
        var fieldIndex=fields[name.toLowerCase()];
        if (!isNaN(fieldIndex))
        field=fields[fieldIndex];
    }
    return field;
}

/*得到fieldset中字段的个数**/
function fieldset_getFieldCount()
{
  return this.fields.fieldCount;
}
/////////////////////////定义fieldset结束/////////////////////////////////////////////




/////////////////////////定义recordset开始/////////////////////////////////////////////
/*创建record 初始化record 包含所有字段的值**/
function createRecordSet(ID)
{
    var recordSet=new Object();
    //var fieldSet=getFieldSet(ID);
    recordSet.fieldSet=templateCard_me.fieldSet;
    recordSet.fields=new Array();//所有的字段
    recordSet.fields.fieldCount=0;//fields中字段的个数。
    recordSet.modified=false;///如果为false，那么点击保存时无任何反应。暂时保留
    recordSet.addField=recordset_addField;//向fields属性添加值
    recordSet.getField=recordset_getField;
    recordSet.setValue=recordset_setValue;
    recordSet.getFieldCount=recordset_getFieldCount;
    return recordSet;
}


/*向fieldsetet中添加字段。添加完之后，fields就初始化好了。**/
function recordset_addField(obj)
{
    var record=this;
    try
    {
        var i=record.fields.length;
        var valueItem=new Object;
        valueItem.recordSet= record;
        valueItem.uniqueId=obj.uniqueId;//唯一键值
        valueItem.pageId=obj.pageId;//模板页
        valueItem.fldName=obj.fldName;//指标名称
        valueItem.keyValue=obj.keyValue;//实际值
        valueItem.disValue=obj.disValue;//前台显示值
        valueItem.modified=false;//未修改 保存时 考虑不传送未修改数据 需考虑系统指标必须传送 todo
        valueItem.setValue=record_setValue;
        valueItem.length=obj.length;
        valueItem.isAutoLog=obj.isAutoLog;//是否有日志记录，true有变色，false无。
        record.fields[i]=valueItem;
        record.fields[valueItem.uniqueId.toLowerCase()]=i;//方便找下标   
        record.fields.fieldCount++;     
        return valueItem;
    }
    catch(e)
    {
        Ext.showAlert(e.message);
    }
}

/*根据字段名字或下标取得整个字段对象**/
function recordset_getField(name)
{
    var record=this;
    return _recordset_getField(record.fields, name);
}

/*根据字段名字或下标取得整个字段对象  辅助函数**/
function _recordset_getField(fields, name)
{
    var field=null;
    if (typeof(name)=="number")// 根据下表找
    {
        field=fields[name];
    }
    else if (typeof(name)=="string")
    {
        var fieldIndex=fields[name.toLowerCase()];
        if (!isNaN(fieldIndex))
        field=fields[fieldIndex];
    }
    
    if(field==null){//支持按照fldname那么查找 20160824 支持两个变化后子集的情况。保存在查到的第一个单元格中。
	    for(var j = 0;j<fields.length;j++){
	    	var _field = fields[j];
	    	if(_field.fldName==name){
	    		field=_field;
	    		break;
	   		}
	    }
    
    }
    return field;
}


/*得到fieldsetet中字段的个数**/
function recordset_getFieldCount()
{
    return this.fields.fieldCount;
}


/*向record中某字段赋值**/
function recordset_setValue(fieldName, value)
{
    //alert(fieldName+" "+value);
    try
    {
	    var field=this.getField(fieldName);
	    field.keyValue=value;
	    field.modified=true;//此指标打上修改标记
	    field.recordSet.modified=true;//此数据集打上修改标记
    }
    catch(e)
    {
    
    }
}
/*向record中某字段赋值 有valueItem.setValue调用**/
function record_setValue(value)
{
    try
    {
        this.keyValue=value;
        this.modified=true;//此指标打上修改标记
        this.recordSet.modified=true;//此数据集打上修改标记
        //if (this.fldName=='a0101_2'){
           // templateCard_me.changeA0101(this.keyValue);
       // }
    }
    catch(e)
    {
    
    }
}

/////////////////////////定义recordSet结束/////////////////////////////////////////////


/////////////////////////对 fieldSet recordSet操作的一些方法////////////////

/**
 * 初始化生成指标集对象 存放在指标集数组
 * */
function initFieldSet(fieldItemList){ 
    if (templateCard_me.fieldSet ==""){
        templateCard_me.fieldSet=createFieldSet(); 
    }
    var fieldSet=templateCard_me.fieldSet;
    //组装field 添加到fieldSet
    for (var i=0;i<fieldItemList.length;i++){
        var obj =fieldItemList[i];
        fieldSet.addField(obj);
    }
} 


/**
 * 初始化生成指标集对象 存放在指标集数组
 * */
 /*
function initPageField(pageId,fieldItemList){ 
    if (templateCard_me.fieldSet ==""){
        templateCard_me.fieldSet=createFieldSet(); 
    }
    var fieldSet=templateCard_me.fieldSet;
    if (fieldSet.id==""){
        fieldSet.id=pageId;
    }
    else {
        fieldSet.id=+","+pageId;
    }
    //组装field 添加到fieldSet
    for (var i=0;i<fieldItemList.length;i++){
        var obj =fieldItemList[i];
        fieldSet.addField(obj);
    }
    setAllElementStyle(fieldSet,pageId);
} 
*/
/**
 * 初始化生成数据集对象 存放在数据集数组
 * */
function initRecordSet(valueItemList){ 
    if (templateCard_me.recordSet ==""){
        templateCard_me.recordSet=createRecordSet(); 
    }
    var recordSet=templateCard_me.recordSet;
    for (var i=0;i<valueItemList.length;i++){
        var valutItem =valueItemList[i];
        recordSet.addField(valutItem);
    }   
} 

/**
 * 初始化生成数据集对象 存放在数据集数组
 * */
 /*
function initPageRecord(pageId,valueItemList){ 
    if (templateCard_me.recordSet ==""){
        templateCard_me.recordSet=createRecordSet(); 
    }
    var recordSet=templateCard_me.recordSet;
    if (recordSet.id==""){
        recordSet.id=pageId;
    }
    else {
        recordSet.id=+","+pageId;
    }
    for (var i=0;i<valueItemList.length;i++){
        var valutItem =valueItemList[i];
        recordSet.addField(valutItem);
    }   
    initElementValue(recordSet,pageId);
} 
*/

/**
 * 设置单元格样式 
 * */
function setAllElementStyle(fieldSet,pageId){ 
    for (var i=0;i<fieldSet.getFieldCount();i++){
        var field=fieldSet.fields[i];
        if (pageId!=field.pageId) 
          continue;
        var element =Ext.getDom(field.uniqueId);
        if (element !=null){
            var extra= element.getAttribute('extra');            
            if (field.chgState=="1" || field.rwPriv=="0" || field.rwPriv=="1"){//变化前、无权限，临时变量（依据参数控制是否可编辑）
                element.readOnly = true;                    
                //不判断是否有editor,只要变化前,无权限或者读权限统一设置成边框为0
                //if (extra=="editor"){
                    //element.style.backgroundColor = "white";
                //}
                element.style.border=0;
            }
            else if (field.rwPriv=="0" )    {
            	element.style.border=0;
                //element.style.backgroundColor = "white";
            } 
        }
    }
} 

/**
 * 初始化所有的单元格数据，切换人员及切换页签时都需加载。
 * */
function initElementValue(recordSet,pageId){
    var fieldSet=recordSet.fieldSet;
    //电子签章初始化数据使用
    var fields = recordSet.fields;
    if(templateCard_me.signXml==''){
	    for(var j = 0;j<fields.length;j++){
	    	var field = fields[j];
	    	if(field.fldName=='signature'){
	    		templateCard_me.signXml = getDecodeStr(field.disValue);
	    		break;
	   		}
	    }
    }
    for (var i=0;i<fieldSet.getFieldCount();i++){
        var field=fieldSet.fields[i];        
        if (pageId!=field.pageId) 
          continue;
          
        if(field.flag=="S"&&(field.rwPriv=="0"||field.rwPriv=="1")){//如果是签章且没有写权限不提示双击签章
        	document.getElementById(field.uniqueId).title="";
        }
        if (field.rwPriv=="0"){//无权限 不能查看
             continue;                   
        }
        var uniqueId=field.uniqueId;
        var element =Ext.getDom(uniqueId);//根据唯一值取得页面对应元素
        //开始赋值
        if (element !=null){
            var valueItem =recordSet.getField(uniqueId); 
            if (valueItem!=null){
            	if(valueItem.isAutoLog){
            		element.style.color=templateMain_me.templPropety.autoLogColor;
            	}else{
            		element.style.color='';
            	}
                if (field.fldType=="clob"){//子集
                    var disValue=getDecodeStr(valueItem.disValue);
                    var recordRoot=Ext.decode(disValue);
                    var tabid = templateMain_me.templPropety.tab_id;
                    showSubView(uniqueId,valueItem.fldName,tabid,element,recordRoot);
                    //showSubDomainView(recordSet.id,element,recordRoot,field);
                }
                else if(field.flag == "F"){//附件
                	var ins_id = "0";
                	if(templateCard_me.personListCurRecord)
                		ins_id = templateCard_me.personListCurRecord.get("ins_id");
                	if(!!!ins_id)
                		ins_id = "0";
                	 var tabid = templateMain_me.templPropety.tab_id;
                	 var rwPriv = field.rwPriv;
                //	 var sp_batch = templateMain_me.templPropety.sp_batch; //20160905 dengcan 无用了，ins_id表示当前选中记录的单号
                	 var object_id = "";
                	 if(templateCard_me.personListCurRecord)
                	 		object_id = templateCard_me.personListCurRecord.get("objectid_e");
        	 		
                	 showAttachment(uniqueId,field.attachmentType,ins_id,tabid,rwPriv,"0",object_id);
                }
                else {
                    var disValue=valueItem.disValue; 
                    //北理工优化 有小数位显示，无小数位不显示.00
                    if(field.fldType=="N")
                    {
                    	var decimal=disValue.substring(disValue.indexOf(".")+1);
                    	if(parseInt(decimal,10)==0&&disValue.indexOf(".")!=-1)//这样就不用判断小数位数了
                    	{
                    		disValue=disValue.substring(0,disValue.indexOf("."))
                    	}
                    }    
                    disValue=replaceAll(disValue,"````", "," );
                    disValue=replaceAll(disValue,"`","<br />");//子集指标多条换行
                    var extra= element.getAttribute('extra');
                    if(extra=="editor"){
                        element.value=disValue; 
                    }else if (extra=="textarea"){
                    	element.value=disValue;
                    	//liuyz 解决富文本编辑器setData延时执行可能造成数据错误和报没有权限错误。
                    	//解决：获取原来的配置信息，重新绑定控件。在用户“疯狂”来回切换人员时config可能会丢失走默认的配置.
                        var editor=CKEDITOR.instances[uniqueId];
                        if(editor!=undefined)
                        {
                        	var config=editor.config;
                        	if(config.toolbar!=undefined)//当撤销选人时程序会走两次这里，第二次获取到的config不正确，会导致显示默认设置。
                        	{
                        		if(disValue.length==0){//IE兼容性视图下只能点第一行进入编辑状态。默认填充四行，方便用户点击。
									disValue="<br /><br /><br /><br />";
								}
	                        	//CKEDITOR.remove(editor);
	                        	if(templateCard_me.opinion_field&&templateCard_me.opinion_field.toUpperCase==field.fldName.toUpperCase)
								{
									if(disValue.indexOf("<br />")==-1)//liuyz 如果已经替换过\n就不再替换
									{
										disValue=replaceAll(disValue,"\n","<br />");//liuyz bug31563
									}
								}		
	                        	if(editor&&editor.config&&editor.config.readOnly==true)
	                        	{
									//bug 33750 进入表单页面，一直在加载....
	                        		try{
										editor.setReadOnly(false);
		                        		editor.setData(disValue);
		                        		editor.setReadOnly(true);
	                        		}
	                        		catch(ex){
	                        			editor.setData(disValue);
	                        		}
	                        	}
	                        	else
	                        	{
	                        		editor.setData(disValue);
	                        	}
                        	}
                        }
                    }else if (extra=="photo"){
                        element.src=valueItem.disValue; 
                    }else if (extra=="panel"){
                        element.innerHTML=replaceAll(disValue,'\r\n','<br \>'); 
                    }
                }
                valueItem.modified=false;//快速切人富文本编辑器赋值异步实现，可能会导致数据错乱，这里打上没有修改标记。
            }
            if(field.flag=='S'){
            	//创建签章对象（金格科技）
				/*if(templateCard_me.signatureType==0&&Ext.isIE){
					initJgkjSignObject();
				}*/
            }
        }
    }
	//加载电子签章
	if(templateCard_me.signXml!=''){
		initSignRecord(pageId);
	}
		
}
/**
 * 绑定ckeditor监听事件。
 * @param {} editor
 */
function  setCkeditorListens(editor)
{
	editor.on( 'change', function( event ) {   
	    var data = this.getData();//内容
	    document.getElementById(event.sender.name).value=data;
	    var valueItem = templateCard_me.getValueItem(event.sender.name);
	    var recordSet = templateCard_me.getCurRecordSet();
		if(recordSet.getField(valueItem.uniqueId).keyValue+""!=data+"")
		{
			if(!templateCard_me.isHaveChange)
	   		{
	   			templateCard_me.isHaveChange=true;
	   		}
		}
	    valueItem.setValue(data);
	});
	editor.on( 'blur', function( event ) { 
	 	 var valueItem = templateCard_me.getValueItem(event.sender.name); 
	 	 var length=valueItem.length;
		 var data = this.getData();//内容
		 var value=this.document.getBody().getText();
		 var vaLength = getWordsTrueLength(value);
	     if(length!=0&&length!=10&&vaLength>length){
	    	 Ext.showAlert("该文本的字数不超过"+length+"个,目前已输入"+vaLength+"个！",function(){
	    		 editor.focus();
	         	return;
	         });
	        
	     }
		 var recordSet = templateCard_me.getCurRecordSet();
		 if(recordSet.getField(valueItem.uniqueId).keyValue+""!=data+"")
		 {
			if(!templateCard_me.isHaveChange)
	   		{
	   			templateCard_me.isHaveChange=true;
	   		}
		 }
		 valueItem.setValue(data);
	});
	editor.on( 'focus', function( event ) { 
		 var valueItem = templateCard_me.getValueItem(event.sender.name);  
		 templateCard_me.activeElement=document.getElementById(valueItem.uniqueId);
	})
}
