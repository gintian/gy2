/////////////////////////显示子集 todo以后此文件废弃 调用子集类 目前仅弹出窗口使用了子集类////

/**显示插入子集内容*/
function showSubDomainView(recordId,tdElement,xmlcontent,subSet)
{
    var field_name=tdElement.getAttribute("field");//子集名称。格式为t_a19_1
    var bread=true;
    rootNode=xmlcontent;
	if(rootNode)
	{
		var divid=subSet.uniqueId+"_div";
		var divElement=document.getElementById(divid);
		if(!divElement)
		{
			divElement=document.createElement("div");
			divElement.style.width="100%";
			divElement.style.height="100%";
		//	divElement.style.overflow="auto"; //google浏览器会多出滚动条，所以屏蔽此属性
		//	divElement.className="fixedDiv";
			divElement.id=divid;
			tdElement.appendChild(divElement);
		}
		var subview=new SubSetView(recordId,tdElement,divElement,rootNode,subSet);
		subview.showViewByExt(false);
		
	}
}
/**子集类*/
function SubSetView(recordId,tdElement,divElement,rootNode,subSet) {
	var recNodes = rootNode.records; 
	var reordFields=rootNode.columns;
	this._recordId=recordId;//record的标识 用于找到record
	this._element=tdElement;//最外层td
	this._parent=divElement;//外层div的对象
	this._field_name=subSet.uniqueId;//子集的名字
	this._row=recNodes.length;//记录条数
	this._recNodes=recNodes;//各个record记录	
	//数据里面的指标 可能与目前子集设置的指标个数不同 展现数据以此为准 代码型数据 todo wangrd
	
	this._rwPriv="2";//子集的读写权限
	this._field_list=new Array();
	var fieldsPriv="";
	var fieldsWidth="";
	var fieldsTitle="";
	var fields="";
	for (var i=0;i<subSet.subFields.length;i++){
		var subField =subSet.subFields[i];
		var fldname =subField.fldName;
		var fldTitle =subField.fldTitle;
		var fldWidth=subField.fldWidth; 
		if(/^[0-9]*[1-9][0-9]*$/.test(fldWidth)) //如果是整数
			fldWidth=parseInt(fldWidth); 
		else
			fldWidth=80;
		var newFldObj=new Object();
		newFldObj.fldName=fldname;
		newFldObj.fldType="A";
		newFldObj.codesetId="0";
		newFldObj.flddesc=fldTitle;
		newFldObj.fldPriv=fldTitle;
		
		if (fields.length>0)
			fields=fields+","+fldname;
		else 
			fields=fldname;
		
		if (fieldsPriv.length>0)
			fieldsPriv=fieldsPriv+","+"2";
		else 
			fieldsPriv="2";
		
		if (fieldsWidth.length>0)
			fieldsWidth=fieldsWidth+","+fldWidth; //+"80";
		else 
			fieldsWidth=fldWidth+""; //"80";
		
		if (fieldsTitle.length>0)
			fieldsTitle=fieldsTitle+","+fldTitle;
		else 
			fieldsTitle=fldTitle;
		this._field_list[i]=newFldObj;
	}
	//var fieldarr=fields.split("`");
	this._recordColumn=reordFields.split("`");
	var fieldarr=fields.split(",");
	this._col=fieldarr.length;//列数
	this._column=fieldarr;//指标列的字符串数组
	
	this._fieldsPrivArr=fieldsPriv.split(",");//每个指标的读写权限。如果子集是写，而某个指标是读，那么指标是只读模式
	this._fieldsWidthArr=fieldsWidth.split(",");//每个指标的列宽。以像素为单位
	this._fieldsTitleArr=fieldsTitle.split(",");//每个指标的自定义列头
	this._fieldsPriv=fieldsPriv;//子集指标失去焦点时，重新组装xml时用到。（populateSubXml（）方法。）
	this._fieldsWidth=fieldsWidth;//子集指标失去焦点时，重新组装xml时用到。（populateSubXml（）方法。）
	this._fieldsTitle=fieldsTitle;//子集指标失去焦点时，重新组装xml时用到。（populateSubXml（）方法。）
}


/*
 * 使用ext显示表格
 */
SubSetView.prototype.showViewByExt = function(chgmode) {
	//清除当前div的已有元素
	//var tableDiv =Ext.getDom(this._field_name+'_div');
	var p = Ext.getCmp(this._field_name+"_mainPanel");
	
	if(p){
		//偶尔出现p的destroy方法丢失,暂时原因未知
		try{
			p.destroy();
		}catch(e){}
	}
	//if (tableDiv!=null && tableDiv !=undefined)
	//	tableDiv.innerHTML="";
	var bCanEdit=false; //表格可编辑
	var bCanSelect=false;//显示选择框
	//表格列头
	var tableColumns=this.getSubSetTableColumns(chgmode);
	//表格内容
	var storeData=this.getSubSetTableData(chgmode);	
	//表格数据列
	var dataFields=this.getSubSetFields(chgmode);	
	var tablename = this._field_name;
	//构建表格
	var configs={
	    	prefix:this._field_name,
	    	pagesize:20,
	    	tdMaxHeight:-1,//iE8 多点几个人报错 bug9001
	    //	editable:bCanEdit,
	    	lockable:false,
	    //	selectable:bCanSelect,
	    //	customtools:toolBar,
	    	datafields:dataFields,
	    	//viewConfig:{tablename:tablename},
	    	beforeBuildComp:function(config){
				   config.tableConfig.viewConfig={tablename:tablename};
			},
	    	storedata:storeData,
	    	toolPosition:"bottom",
	    	tablecolumns:tableColumns
	};

	var tablegrid=new BuildTableObj(configs);

	tablegrid.getMainPanel().render(this._field_name+'_div');
	
}

/*
 * 获取表格显示列
 */
SubSetView.prototype.getSubSetTableColumns = function(chgmode) {
	var tableColumns=[];
	for(var i=0;i<this._col;i++)
    {	
   		var currentPriv=this._fieldsPrivArr[i];
   		var currentWidth=this._fieldsWidthArr[i];
   		currentWidth=parseInt(currentWidth);
   		var currentTitle="";
   		if (this._fieldsTitleArr.length==this._col)
   		  currentTitle=this._fieldsTitleArr[i];
   		if(currentPriv=="0"){// 指标无权限 不显示
   			continue;
   		}
   		var strformat="";
   		var strType="";
   		var strEditType="";
   		var strCodeSetid ="";
   		var strAlign="left";
   		var maxlength=100;
   		var fmobj=this._field_list[i];
   		if (fmobj){
   			if(fmobj.T=="D")
   			{
   				strType="datecolumn";
   				strEditType="datetimefield";
   				strAlign="right";
   				strformat="Y.m.d";//改成用.做分隔符
   				maxlength=fmobj.L;
   			}
   			else if(fmobj.T=="N"){
   				strType="numbercolumn";
   				strEditType="numberfield"
   				strAlign="right";
   				maxlength=fmobj.L+fmobj.D+1;
   			}
   			else if(fmobj.T=="M"){
   				strType="bigtextcolumn";
   				strEditType="bigtextfield";   				
   				maxlength=1000000;
   				if (fmobj.L!=0 && fmobj.L!=10 ){//设置备注长度大于50才控制。
   					maxlength=fmobj.L;
   				}
   			}
   			else {
   				strType="";// "textfield"
   				if (fmobj.C!="0"){
   					strType="codecolumn";
   					strCodeSetid=fmobj.C;
   					strEditType="codecomboxfield";// 树 列表方式
   					maxlength=100;
   				}
   				else {
   					maxlength=fmobj.L;
   				}
   			}
   		};
   		
   		var columnObj={
   				xtype:strType,
   				text:currentTitle,
   	   			width:currentWidth,
   	   			dataIndex:this._column[i],
   	   			editablevalidfunc:null,
   	   			format:strformat,   		
   	   			align:strAlign
   		};
   		if(currentTitle=='附件')
   		{
   		   columnObj.renderer=showrender;
   		}

   		if (currentPriv=="2"){
   			var editorObj={
   					xtype:strEditType,
   					maxLength:maxlength,
   					codesetid:strCodeSetid,
   					allowBlank:true,
   					maxValue:null,
   					format:strformat,
   					validator:null	
   			};
   			if(currentTitle!='附件'){
   			    columnObj.editor=editorObj;
   			}
   		}
   		tableColumns.push(columnObj);
    }
	return tableColumns;
}

//当有文件附件时，可以点击打开，显示具体的文件 liuzy 20151017
showrender=function(value, metaData, Record,a,b,c,tableView){
        var index=metaData.recordIndex;
		var html = "<a href=javascript:showfiles('"+tableView.tablename+"','"+index+"');><img src="+rootPath+"/images/file.png  border=0></a>";
		return html;
}

/*
 * 显示文件附件 liuzy 20151019
 */
function showfiles(tablename,index){
       var storeId=tablename+"_dataStore";    
       var store = Ext.data.StoreManager.lookup(storeId);  //利用Ext.data.StoreManager的lookup()方法可以根据storeId得到对应的store
       var record = store.getAt(index);
       var value=record.data.attach;
       var edit = 0;
       var list=new Array();
       var rootPath="/multimedia/template/template_1"///+this.tabid+"/";
       if(typeof(value)!='undefined' && value!=''){
         var i= value.indexOf(',');
         if(i!=-1)
         {
            var lists=value.split(',');
            for(n=0; n<lists.length; n++ )   
            {  
               var listn=lists[n].split('|');
               var map = new HashMap();
               map.put('filename',listn[0]);
               map.put('path',listn[1]);
               map.put('localname',listn[2]);
               map.put('size',listn[3]);
               map.put('id',listn[4]);
               map.put('successed',true);
               list.push(map);
            }           
         }else{
               var listn=value.split('|');
               var map = new HashMap();
               map.put('filename',listn[0]);
               map.put('path',listn[1]);
               map.put('localname',listn[2]);
               map.put('size',listn[3]);
               map.put('id',listn[4]);
               map.put('successed',true);
               list.push(map);
         }
       }
       if(list.length>0){//已有上传文件
	       Ext.create("SYSF.FileUpLoad",{
				renderTo:Ext.getBody(),
				emptyText:"请输入文件路径或选择文件",
				upLoadType:2,
				uploadUrl:rootPath,
				isDownload:true,
				savePath:rootPath,
				fileList:list,
				isShowOrEdit:edit,
				fileExt:"*.doc;*.docx;*.xlsx;*.xls;*.rar;*.zip;*.ppt;*.jpg;*.jpeg;*.png;*.bmp;*.txt;*.wps;*.pptx",
				buttonText:'上传',
				success:function(list){
				    if(list.length!=0){
						var valuestr='';
					    for(var m=0;m<list.length;m++){
							var filename = list[m].filename;  //编码后文件名
							var id = list[m].id;              //文件唯一标识      
							var localname=list[m].localname;  //原始文件名 
							var size = list[m].size.replace(" \r\n","");          //文件大小
							var path = list[m].path;          //文件上传路径
							var successed=list[m].successed;  //是否成功标识
							var text=filename+'|'+path+'|'+localname+'|'+size+'|'+id+'|'+m ;
	                        valuestr+=text+',';
						}
						valuestr=valuestr.substring(0,valuestr.length-1);
					    record.set('attach',valuestr);
				    }else{
				        record.set('attach','');
				    }
				},
				//回调方法，失败
	 			error:function(){
	  				Ext.MessageBox.show({  
						title : '文件上传',  
						msg : "文件上传失败 ！", 
						icon: Ext.MessageBox.INFO  
				    })
	 			},
				fileSizeLimit:'20MB',
				isDelete:true
		 }); 
	 }else{
		Ext.create("SYSF.FileUpLoad",{
			renderTo:Ext.getBody(),
			emptyText:"请输入文件路径或选择文件",
			upLoadType:2,
			uploadUrl:rootPath,
			isDownload:true,
			savePath:rootPath,
			isShowOrEdit:edit,
			fileExt:"*.doc;*.docx;*.xlsx;*.xls;*.rar;*.zip;*.ppt;*.jpg;*.jpeg;*.png;*.bmp;*.txt;*.wps;*.pptx",
			buttonText:'上传',
			success:function(list){
				if(list.length!=0){
				    var valuestr='';
				    for(var m=0;m<list.length;m++){
						var filename = list[m].filename;  //编码后文件名
						var id = list[m].id;              //文件唯一标识      
						var localname=list[m].localname;  //原始文件名 
						var size = list[m].size.replace(" \r\n","");          //文件大小
						var path = list[m].path;          //文件上传路径
						var successed=list[m].successed;  //是否成功标识
						var text=filename+'|'+path+'|'+localname+'|'+size+'|'+id+'|'+m ;
                        valuestr+=text+',';
					}
					valuestr=valuestr.substring(0,valuestr.length-1);
				    record.set('attach',valuestr);
				}else{
				    record.set('attach','');
				}
			},
			//回调方法，失败
 			error:function(){
  				Ext.MessageBox.show({  
					title : '文件上传',  
					msg : "文件上传失败 ！", 
					icon: Ext.MessageBox.INFO  
			    })
 			},
			fileSizeLimit:'20MB',
			isDelete:true
		});
	}
}

/*
 * 获取表格显示data
 */
SubSetView.prototype.getSubSetTableData = function(chgmode) {
	var storeData=[];
    for (var i=0; i<this._recNodes.length; i++)
	{
		var record={};
		var recNode = this._recNodes[i];
		var keyid = recNode.I9999;		
		var state=recNode.state;	
		if (state==null) state="";		
		
		record.I9999=keyid;
		record.delState=state;
		var value = recNode.contentValue;
		var valuearr=value.split("`");
		for(var j=0;j<this._column.length;j++)
		{
			record[this._column[j]]="";
		}
		for(var j=0;j<valuearr.length;j++)
		{
			var tmp=valuearr[j];  
			var fmobj=this._field_list[j];
			if (fmobj){
			  	if(fmobj.codeSetid!="0"&&tmp.length>0)	{  //这里的C为codeset
			  		var val=tmp.split('||');
			  		valuearr[j]=val[1];
			  		tmp=val[0];
			  		tmp=valuearr[j]+"`"+tmp;
			  	}
			}
			record[this._recordColumn[j]]=tmp;
		}
		storeData.push(record);
	}
	return storeData;
}

/*
 * 获取表格数据列
 */
SubSetView.prototype.getSubSetFields = function(chgmode) {
	var strfields ="'I9999','delState','canEdit'";
	for(var i=0;i<this._col;i++){	
		strfields=strfields+","+"'"+this._column[i]+"'";
    }
	//strfields=strfields.substr(1);
	strfields="["+strfields+"]";
	var dataFields=Ext.decode(strfields);
	return dataFields;
}
/////////////////////////显示子集结束/////////////////////////////////////////////

