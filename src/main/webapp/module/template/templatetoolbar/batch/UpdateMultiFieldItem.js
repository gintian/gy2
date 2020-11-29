 Ext.define('TemplateBatchUL.UpdateMultiFieldItem',{
     constructor:function(config){//构造方法
        thisMulti=this;
        thisMulti.data='';
        thisMulti.batchtempl=config.batchtempl;
    	thisMulti.tab_id=thisMulti.batchtempl.tab_id;
    	thisMulti.ins_id=thisMulti.batchtempl.ins_id;
    	thisMulti.task_id=thisMulti.batchtempl.task_id;
    	thisMulti.view_type=thisMulti.batchtempl.view_type;
    	thisMulti.allNum=thisMulti.allNum;
    	thisMulti.init();
     },
        //初始化数据
	 init:function(form,action){
    	//变化后指标store
		thisMulti.store = Ext.create('Ext.data.Store',
				{
					fields:['itemname','field_value','field_type','field_item','disformat'],
					proxy:{
					    	type: 'transaction',
					    	functionId:'MB00002003',
					        extraParams:{					        		
				        		tab_id:thisMulti.tab_id,		
							    ins_id:thisMulti.ins_id,		
							    task_id:thisMulti.task_id,
							    approve_flag:thisMulti.batchtempl.approve_flag,
							    module_id:thisMulti.batchtempl.module_id,
							    transType:"init"
					        },
					        reader: {
					            type: 'json',
					            root: 'data'         	
					        }
					},
					autoLoad: true
				});
		//主界面panel
		var gridTop=new Ext.grid.GridPanel({
    		height:300,
	        stateful:true,
	        stateId:'stateGrid',
	        border:1,
	        stripeRows:true,//隔行换色
	        forceFit:true,//让每列自动填满表格，可以根据columns中设置的width按比例分配
    		store:thisMulti.store,
    		enableHdMenu:false,//是否显示表头的上下文菜单，默认为true
    		enableColumnHide:false,//是否允许通过标题中的上下文菜单隐藏列，默认为true
    		enableColumnMove:false,//是否允许拖放列，默认为true
    		enableColumnResize:false,//是否允许改变列宽，默认为true
    		columnLines: true,//是否显示列分割线，默认为false
    		loadMask:true,//在store.load()完成之前是否显示遮罩效果，true会一直显示"Loading...",
    	
    		multiSelect:false,//支持多选
    		selModel: {
    			selType: 'checkboxmodel',
            	columnSelect: true,
            	checkboxSelect: true,
            	pruneRemoved: false,
    	        mode: "SIMPLE",     //"SINGLE"/"SIMPLE"/"MULTI"
    	        checkOnly: true     //只能通过checkbox选择
    	    },
    	    plugins:[
    	             
    		         Ext.create('Ext.grid.plugin.CellEditing',{
    		        	 clicksToEdit:1//设置鼠标点击1次进入编辑状态
    		         })
    		         ],
    		columns:[
    		         //自动显示行号，也可以用new Ext.grid.RowNumberer()
    		         {	header:'指标名称',
         		        dataIndex:'itemname',
         		        width:'50%',
    		        	sortable:false
    		        	},
    	             {
    		        	header:'修改值',
    		        	id:'dg',
    		        	dataIndex:'field_value',
    		        	width:'50%',
    		        	sortable:false,
    		        	//field:'textfield',
    		        	editor:{
    		        		xtype:'textfield'
    		        	},
    		        	renderer:function(value,cellmeta,record,rowIndex,columnIndex,stroe){//处理显示修改值
	    		        	if(value!=null){//处理点击某个单元格有null值情况
    		        			var v = value + "";//indexOf方法必须是string类型
		    		        	if(v.indexOf("`")!=-1){//代码型，只要进入编辑状态，无论有无值都会有`
		    		        				var st=value.indexOf("`");
		    		        				var en=value.length;
		    		        				if(st>0){//有值
		    		        					return value.substring(st+1);
		    		        				}else//无值
		    		        					return "";
		    		        			}else{//不是代码型
		    		        				return value;
		    		        			}
		    		        	}else{
		    		        		return "";
		    		        	}
	    		        	}
    		        },
    		        {	header:'指标类型',
     		        	dataIndex:'field_type',
     		        	hidden:true,
		        		sortable:false
		        	},
		        	{	header:'指标编码',
     		        	dataIndex:'field_item',
     		        	hidden:true,
		        		sortable:false
		        	},
		        	{	header:'指标格式',
     		        	dataIndex:'disformat',
     		        	hidden:true,
		        		sortable:false
		        	}]
    			});
    		//单选组开始
             var radiogroup = new Ext.form.RadioGroup({
                 x:50,
                 y:313,
                 id:'selection',
                 items: [{
                     name: 'select',
                     inputValue: '0',
                     boxLabel: '全部'
                 }, {
                     name: 'select',
                     inputValue: '1',
                     boxLabel: '选中记录',
                     checked: true
                 }]
             });
             //横线
		    var line=new Ext.Panel({
			    border:false,
			    x:0,
                y:338,
			    html:'<hr style="border-top:1px solid #CCCCCC; border-bottom:0px; border-left:0px; ">'             
		    });
             //整体布局
             var form = new Ext.form.FormPanel({
		          height:400,
		          border: false,
            	  layout:'absolute',
	              items: [gridTop,radiogroup,line]
	                
	             });
        	//监听，进入编辑状态前改变编辑类型
             gridTop.on("beforeedit", function(edit,e){ 
        		var record = e.grid.getStore().getAt(e.rowIdx); // 获得当前行的记录
				//获取指标类型
				if(record.get("imppeople")){//启用选人组件调用选人
					if(templateMain_me.templPropety.isValidOnlyname==undefined||templateMain_me.templPropety.isValidOnlyname=='false'){
						Ext.showAlert("请设置并且启用唯一性指标！")
						return false;
					}
				   var	 defaultSelectedPerson=new Array();
					if(record.get("field_value")!=undefined&&record.get("field_value")!=''){
						var hashvo = new HashMap();
						   hashvo.put("ids",record.get("field_value"));
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
						defaultSelected:defaultSelectedPerson,
						nbases:templateMain_me.templPropety.nbases,
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
							record.set("field_value",staffids.substring(0,staffids.length-1));	
							record.commit();
							if(errerMsg.length>0){
								Ext.showAlert(errerMsg+"的唯一性指标值为空，不能保存。");
							}
						}
					}, f);
					p.open();
					return false;
				}
        		if(record.get("field_type").indexOf("|")>0){
        			thisMulti.data=record.get("field_type").substring(record.get("field_type").indexOf("|")+1);
        		}else
        			thisMulti.data=record.get("field_type");
       			if(record.get("field_item") == "codesetid_2"){
	       			e.column.setEditor({
		             	xtype:"codecomboxfield",codesource:"GetSpecialTemplateSetTree",codesetid:"codesetid"
		            });
       			}else if(thisMulti.data=='N'){
       				 var disformat = record.get("disformat");
       				 e.column.setEditor({
       					 xtype:'numberfield',
       					 hideTrigger:true,
       					 maxLength:30,
       					 decimalPrecision: disformat //bug 38576
       				 }); 
           		}else if(thisMulti.data=='D'){
           			var disformat = record.get("disformat");
           			e.column.setEditor({
      					xtype:'datetimefield',
					    //editable:false,//放开编辑功能 update 20180109
					    allowBlank:false,//不加这个，当点击日期编辑框而不选择的话，再点其他类型编辑框会报错
					    format:disformat==25?'Y-m-d H:i':'Y-m-d'
      				 });
           		}else if(thisMulti.data=='M'){
           			e.column.setEditor({xtype:'bigtextfield'});
           			
           		}else if(thisMulti.data=='A'){
           			Ext.getCmp('dg').setEditor({
      					 xtype:'textfield',
      					 maxLength:30,
      					 validator: function (val) {//添加指标长度校验
					        	var itemlength = record.get('itemlength');
      					 		var l = 0;
							    if(val.length>0){
								    var a = val.split(""); 
								    for (var i=0;i<a.length;i++){ 
								        if (a[i].charCodeAt(0)<299) {
								            l++; 
								        } else { 
								           l+=2; 
								        } 
								    }
								    if(l>itemlength){
								    	var errorMsg = "当前指标的长度不能超过"+itemlength+"!<br>注：一个汉字的长度是2";
								    	return errorMsg;
								    }else{
								    	return true;
								    }
							    }else{
							    	return true;
							    }
						    }
      				 });
           		}else{
        			var limitManagePriv=record.get("limitManagePriv");
        			var ctrltype="0";
        			var nmodule="8";
        			if (limitManagePriv=="1"){
        			    ctrltype="3";
        			}
        			//liuyz bug 28563 UN不能选UM,UM可以选UN
        			var onlySelectCodeset=false;
        			if(thisMulti.data=='UN'||thisMulti.data=='@K')
        				onlySelectCodeset=true;
           			e.column.setEditor({codesetid:thisMulti.data,xtype:'codecomboxfield',onlySelectCodeset:onlySelectCodeset,ctrltype:ctrltype,nmodule:nmodule});
           			//单位部门岗位级联
           			var UNRecord = undefined,UMRecord = undefined,KRecord = undefined;
           			var field_item = record.get("field_item");//当前行指标代码
           			thisMulti.current_field_item = field_item;
           			var field_value = record.get("field_value");//当前行指标值
           			var fatherRelationField = record.get("fatherRelationField");//联动的父级指标
           			for(var i=0;i<e.store.totalCount;i++){
           				var record_ = e.store.getAt(i);//每一行的record
           				var field_item_ = record_.get("field_item");
           				if(field_item_.toUpperCase()=="B0110_2")//单位
           					UNRecord = record_;
           				if(field_item_.toUpperCase()=="E0122_2")//部门
           					UMRecord = record_;
           				if(field_item_.toUpperCase()=="E01A1_2")//岗位
           					KRecord = record_;
           			}
           			if(field_item.toUpperCase()=="B0110_2"){
           				if(e.column.getEditor()){
		        			e.column.getEditor().afterCodeSelectFn = thisMulti.afterCodeSelect;
			        	}else
			        		e.column.setEditor({afterCodeSelectFn:thisMulti.afterCodeSelect,codesetid:thisMulti.data,xtype:'codecomboxfield',onlySelectCodeset:onlySelectCodeset,ctrltype:ctrltype,nmodule:nmodule});
           			}
           			if(field_item.toUpperCase()=="E0122_2")//当前是部门的话
       				{
           				var value ='';
           				if(UNRecord!=undefined){//没有插入单位，此值为空，直接get报错
           					value=UNRecord.get("field_value");
           				}
			        	if(value!=''&&value!='`'&&value!=undefined){
			        		if(e.column.getEditor()){
			        			e.column.getEditor().parentid=value.split('`')[0];
			        			e.column.getEditor().afterCodeSelectFn = thisMulti.afterCodeSelect;
				        	}else
				        		e.column.setEditor({afterCodeSelectFn:thisMulti.afterCodeSelect,parentid:value.split('`')[0],codesetid:thisMulti.data,xtype:'codecomboxfield',onlySelectCodeset:onlySelectCodeset,ctrltype:ctrltype,nmodule:nmodule});
				        }
       				}
           			if(field_item.toUpperCase()=="E01A1_2")//当前是岗位的话
       				{
           				var value = UMRecord.get("field_value");
			        	if(value!=''&&value!='`'&&value!=undefined){
			        		if(e.column.getEditor())
			        			e.column.getEditor().parentid=value.split('`')[0];
			        		else
			        			e.column.setEditor({parentid:value.split('`')[0],codesetid:thisMulti.data,xtype:'codecomboxfield',onlySelectCodeset:onlySelectCodeset,ctrltype:ctrltype,nmodule:nmodule});	
			        	}
					}
					if(fatherRelationField!=undefined&&fatherRelationField!=''){
						for(var i=0;i<e.store.totalCount;i++){
							var record_ = e.store.getAt(i);//每一行的record
							var field_item_ = record_.get("field_item");
							if(field_item_.toUpperCase()==fatherRelationField.toUpperCase()){
								var value = record_.get("field_value");
			        			if(value!=''&&value!='`'&&value!=undefined){
									if(e.column.getEditor()){
										e.column.getEditor().parentid=value.split('`')[0];
										e.column.getEditor().afterCodeSelectFn = thisMulti.afterCodeSelect;
									}else
										e.column.setEditor({afterCodeSelectFn:thisMulti.afterCodeSelect,codesetid:thisMulti.data,xtype:'codecomboxfield',onlySelectCodeset:onlySelectCodeset,ctrltype:ctrltype,nmodule:nmodule});
									break;
								}
							}
						}
					}  
           		}
          	});
          	//编辑后
             gridTop.on("edit", function(edit,e){
     			var record = e.grid.getStore().getAt(e.rowIdx);
     			//单位部门岗位级联
     			var searchlevel = '';
       			var UNRecord = undefined,UMRecord = undefined,KRecord = undefined;
       			var field_item = record.get("field_item");//当前行指标代码
       			var field_value = record.get("field_value");//当前行指标值
				   var childRelationField = record.get("childRelationField");//联动的孩子指标
				   var stroe=e.store;
       			for(var i=0;i<e.store.totalCount;i++){
       				var record_ = e.store.getAt(i);//每一行的record
       				var field_item_ = record_.get("field_item");
       				if(field_item_.toUpperCase()=="B0110_2")//单位
       					UNRecord = record_;
       				if(field_item_.toUpperCase()=="E0122_2")//部门
       					UMRecord = record_;
       				if(field_item_.toUpperCase()=="E01A1_2")//岗位
       					KRecord = record_;
       			}
       			if(field_item.toUpperCase()=="E01A1_2"){
       				if(UNRecord){
						searchlevel+='UN,';
					}
					if(UMRecord){
						searchlevel+='UM,';
					}
   				}
       			else if(field_item.toUpperCase()=="E0122_2"){
       				if(KRecord&&UMRecord.data.selectCode=='1'){
       					KRecord.set('field_value','');
					}
					if(field_value=='`'){
						if(UNRecord){
							e.column.getEditor().parentid='';
							UNRecord.set('field_value','');
						}
						if(KRecord){
							e.column.getEditor().parentid='';
							KRecord.set('field_value','');
						}
					}
					if(UMRecord.data.selectCode=='1')
						UMRecord.data.selectCode='0';
					if(UNRecord){
						searchlevel+='UN';
					}
       			}
       			else if(field_item.toUpperCase()=="B0110_2"){
					if(UMRecord&&UNRecord.data.selectCode=='1'){
						UMRecord.set('field_value','');
					}
					if(KRecord&&UNRecord.data.selectCode=='1'){
						KRecord.set('field_value','');
					}
					if(field_value=='`'){
						if(UMRecord){
							e.column.getEditor().parentid='';
							UMRecord.set('field_value','');
						}
						if(KRecord){
							e.column.getEditor().parentid='';
							KRecord.set('field_value','');
						}
					}
					if(UNRecord.data.selectCode=='1')
						UNRecord.data.selectCode='0';
				}
				//清空联动的孩子指标值
				if(childRelationField!=undefined&&childRelationField!=''){
					var chidlRelationList=childRelationField.split(",");
					for(var index=0;index<chidlRelationList.length;index++){
						thisMulti.clearValueById(chidlRelationList[index],e);
					}
				}
					var map = new HashMap();
					map.put('codesetid',thisMulti.data);
					map.put('itemid',field_value.substring(0,field_value.indexOf('`')));
					map.put('searchlevel',searchlevel);
					Rpc({functionId:'MB00004004',async:false,success:function(form){
						var result = Ext.decode(form.responseText);
						var returnlist = result.returnlist;
						var arrRecords=new Array();
						var selectModel = gridTop.getSelectionModel();
						for(var j=0;j<selectModel.selected.items.length;j++){
							var record = selectModel.selected.items[j];
							arrRecords.push(record);
						}
						if(returnlist.length>0){
							if(field_item.toUpperCase()=="E01A1_2"){
								arrRecords.push(UMRecord);
								arrRecords.push(KRecord);
							}else if(field_item.toUpperCase()=="E0122_2"){
								arrRecords.push(UMRecord);
							}
							arrRecords.push(UNRecord);
						}
						Ext.each(returnlist,function(e){
							if(field_item.toUpperCase()=="B0110_2"||field_item.toUpperCase()=="E0122_2"||field_item.toUpperCase()=="E01A1_2"){
								if(UMRecord&&e.codesetid=='UM'){
									if(UMRecord.get('field_value')==''||UMRecord.get('field_value')=='`'||UMRecord.get('field_value')==undefined){
										UMRecord.set('field_value',e.codeitemid+'`'+e.codeitemdesc);
									}
								}
								if(UNRecord&&e.codesetid=='UN'){
									if(UNRecord.get('field_value')==''||UNRecord.get('field_value')=='`'||UNRecord.get('field_value')==undefined){
										UNRecord.set('field_value',e.codeitemid+'`'+e.codeitemdesc);
									}
								}
							}
							if(e.codesetid!=undefined&&e.codesetid!=''&&e.codesetid!='0'){
								if(record){
									record.commit();
									var fatherRelationFieldStr=record.get('fatherRelationField');
									var isHaveFather=false;
									if(fatherRelationFieldStr!=null){//bug 44389
										for(var i=0;i<stroe.totalCount;i++){
											var record_ = stroe.getAt(i);//每一行的record
											var field_item_ = record_.get("field_item");
											var codeSetId = record_.get("field_type").split("|")[1];
											if(codeSetId.toUpperCase()=='UN'&&e.codesetid=='UN'||codeSetId.toUpperCase()=='UM'&&e.codesetid=='UM'||codeSetId.toUpperCase()=='UM'&&e.codesetid=='UN'||codeSetId.toUpperCase()=='@K'&&e.codesetid=='@K'||((codeSetId!=''||codeSetId!=undefined)&&codeSetId.toUpperCase()!='UN'&&codeSetId.toUpperCase()!='UM'&&codeSetId.toUpperCase()!='@K')){
												if(field_item_.toUpperCase()==fatherRelationFieldStr.toUpperCase()){//单位
													record=record_;
													record.set('field_value',e.codeitemid+'`'+e.codeitemdesc);
													record.commit();
													isHaveFather=true;
													break;
												}
											}
										}
									}
									if(!isHaveFather){
										record=null;
									}
								}
							}
						});
						selectModel.select(arrRecords,false,true);
					},scope:this},map);
				
     			e.record.commit();//编辑后提交，防止出现编辑标识
     		});
			//创建一个窗口
		   	win=Ext.widget("window",{
		          title:'批量修改多个指标', 
		          border: false,
		          width:400,
		          height:420, 
		          layout:'fit',
		          resizable:false,//是否允许改变窗口大小 
				  modal:true,//模态窗口,窗口遮住的页面不可编辑
				  closable:true,//是否显示关闭按钮
				  closeAction:'destroy',//控制按钮是销毁（destroy）还是隐藏（hide）
				  border: false,
				  plain:true,//true则主体背景透明，false则主体有小差别的背景色，默认为false
		          items: [{
		         		xtype:'panel',
		         		border: false,
						items:[form]
		          }],
		          bbar:[
			          		{xtype:'tbfill'},
			          		{
			          			text:'确定',
			          			margin:'0 5 0 0',
			          			handler:function(){
			          			//1.获取所有的字段，2.获取所有的字段值，3.获取所有字段类型
			          			var fielditem_array=new Array();//记录所有的字段
			          			var fieldvalue_array=new Array();//记录字段的值
			          			var fieldtype_array=new Array();//记录字段的类型
			          			var disformat_array=new Array();//记录字段的格式
			          			var isSelected=0;///判断是否有记录选中
			          			
		          				var rows=gridTop.getSelectionModel().getSelection();
		          				if(rows.length==0){
		          				    Ext.showAlert(MB.MSG.select_to_update_items);
	                                return;
		          				}
		          				
		          				for ( var i = 0; i < rows.length; i++) {
		          					fielditem_array[i]=rows[i].get('field_item');
		          					var fld_value = rows[i].get('field_value')+"";//强转为字符串，数值型substring报错
		          					var disformat = rows[i].get('disformat');
		          					var fieldtype = "";
		          					if(rows[i].get('field_type').indexOf("|")>0){
		          						fieldtype = rows[i].get('field_type').substring(0,rows[i].get('field_type').indexOf("|"));
		          						fieldtype_array[i] =  fieldtype;
		          					}else{
		          						fieldtype = rows[i].get('field_type');
		          						fieldtype_array[i] =  fieldtype;
		          					}
		          					
		          					if(fld_value && fld_value!='null'){//文本类型默认是null bug:22191 
			          					if(fld_value.indexOf("`")>0){
			          						fieldvalue_array[i]=fld_value.substring(0,fld_value.indexOf("`"));
			          					}else{
			          						if(fieldtype=="D"&&disformat==25&&fld_value.length==16){
				          						fld_value += ":00";
				          					}
			          						fieldvalue_array[i]=fld_value;
			          					}
		          					}else{
			          						fieldvalue_array[i]="";
		          					}
		          					disformat_array[i] = disformat;
								}
		          				var selectValue=Ext.getCmp("selection").getValue().select;//选中模版，或者全部模版
		          				
		          				var map = new HashMap();
		          				map.put("tab_id",thisMulti.tab_id);		
		          				map.put("ins_id",thisMulti.ins_id);		
		          				map.put("task_id",thisMulti.task_id);
		          				map.put("selchecked",selectValue);
		          				map.put("fielditem_array",fielditem_array);
		          				map.put("fieldvalue_array",fieldvalue_array);
		          				map.put("fieldtype_array",fieldtype_array);
		          				map.put("disformat_array",disformat_array);
		          				map.put("transType", "ok");
		          				map.put("allNum",thisMulti.allNum);
		          		    	Rpc({functionId:'MB00002003',async:false,success:function(form){
          		    					var result = Ext.decode(form.responseText);
			        					if(result.succeed){
		          		    					templateTool_me.refreshCurrent();
		          		    					win.close();
		          		    					Ext.showAlert("修改成功！");
			        					}else{
			        						var message = result.message;
			        						if(message&&message.indexOf("拆分审批")!=-1){
			        							templateTool_me.checkSpllit(message);
			        						}else
			        							Ext.showAlert(result.message);
			        					}
		          		    	}},map);//点击确定，修改模版指标
		          				} 
			          		},          		
			          		{
			          			text:'取消',
			          			handler:function(){
			          				win.close();
			          			}
			          		},
			          		{xtype:'tbfill'}
			           ]
		    }); 

		    win.show();  
		 },
		 afterCodeSelect:function(dataindex,value){
			 var UNRecord = undefined,UMRecord=undefined;
			 for(var i=0;i<thisMulti.store.totalCount;i++){
    				var record_ = thisMulti.store.getAt(i);//每一行的record
    				var field_item_ = record_.get("field_item");
    				if(field_item_.toUpperCase()=="B0110_2"&&thisMulti.current_field_item=="B0110_2")//单位
    					UNRecord = record_;
    				if(field_item_.toUpperCase()=="E0122_2"&&thisMulti.current_field_item=="E0122_2")//部门
    					UMRecord = record_;
			 }
			 if(UNRecord)
				 UNRecord.data.selectCode='1';
			 if(UMRecord)
				 UMRecord.data.selectCode='1';
		 }, clearValueById:function(id,e){
			for(var i=0;i<e.store.totalCount;i++){
				var record_ = e.store.getAt(i);//每一行的record
				var field_item_ = record_.get("field_item");
				if(field_item_.toUpperCase()==id.toUpperCase()){
					e.column.getEditor().parentid='';
					record_.set('field_value','');
					record_.commit();
					var childRelationField = record_.get("childRelationField");//当前行指标值
					if(childRelationField!=''&&childRelationField!=undefined){
						var chidlRelationList=childRelationField.split(",");
						for(var index=0;index<chidlRelationList.length;index++){
							thisMulti.clearValueById(chidlRelationList[index],e);
						}
					}
				}
			}
		}

 });
