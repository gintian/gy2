//liuyz 列表增加打印输出，加载打印控件
Ext.Loader.loadScript({url:rootPath+'/general/sys/hjaxmanage.js'});
/**
 * 模板列表类 TemplateList.js
 **/
Ext.define('TemplateListUL.TemplateList',{

    mainPanel:'',//主界面
    templPropety:'',//模板的所有属性 模板号，实例号等
    templateListGrid:'',//本页面的表格对象（人事异动列表主页面）
    result:'',
    prefix:"templlist",//模板列表前缀
    current_object_id:"", //列表当前选中对象
    //opinion_field:"",//liuyz bug31563
    constructor:function(config){
        templateList_me=this;
        templateList_me.group_arr = null;
		this.templPropety=config.templPropety;
		this.init(config);
    },
    
    //栏目设置保存 lis 20160405
	schemeSave:function(){
    	templateTool_me.refreshAll();
	},
	
   	init:function(config){	
		var map = new HashMap();
		initPublicParam(map,this.templPropety);
	    Rpc({functionId:'MB00003001',async:false,success:this.getTableOK,scope:this},map);
	},
	// 加载表单
	getTableOK:function(form,action){
		var me=this;
	    var result = Ext.decode(form.responseText);
	    this.result=result;
	    templateList_me.fieldsArray=result.fieldsArray;
	    var pageList=result.pageList;
	    //templateList_me.opinion_field=result.opinion_field; //liuyz bug31563
	    //29955 校验如果查询下拉数据为空，则列为空，所以直接返回不加载
        if (pageList.length<1){
            Ext.showAlert("您无权查看和使用模板!请检查设置 ①此页是否显示 ②此页是否适用手机 ③子集&指标权限!",function(){templateTool_me.returnBack(true);});// bug 44120 
            this.mainPanel = Ext.create('Ext.panel.Panel', {
                id:"templatelist_mainPanel",
                border : 0,
                layout:"border",  
                items: []           
            });
            
            return;
        }
	    templateList_me.fieldsMap=result.fieldsMap;
	    templateMain_me.deprecate=result.deprecate;
	    templateList_me.nodePriv = result.nodePriv;//是否走节点权限控制
	    templateList_me.changeInfoList = result.changeInfoList;//变动记录
		var conditions=result.tableConfig;
		this.obj = Ext.decode(conditions);
		me.prefix = this.obj.prefix;//表格控件前缀
		
		//配置多选框
		this.obj.beforeBuildComp=function(config){
			config.tableConfig.selModel={
					selType:'checkboxmodel',
					lockableScope:'both',
					checkOnly: true,     //只能通过checkbox选择
					enableAllSelect:true};
			config.tableConfig.viewConfig.getRowClass=function(record,rowIndex,rowParams,store){//改变行颜色
				if(rowIndex%2==0)
					return 'x-grid-row-selected-white';
				else
					return 'x-grid-row-selected-grey';
			};
		};
		templatelistObj = new BuildTableObj(this.obj);
		templateList_me.addGridListens(templatelistObj);//修改查询无作用问题
        this.templateListGrid=templatelistObj;
        templateList_me.addOrgListens(templatelistObj);//单位部门岗位级联
        
        templateList_me.subModuleId=templatelistObj.subModuleId;
        //templatelistObj.bodyPanel.header.setHeight(0);
		this.mainPanel = templatelistObj.getMainPanel();
		
		//默认选中第一行
        new Ext.util.DelayedTask(function(){
    		templateMain_me.selecRowChangeCss(templatelistObj.tablePanel,0);
		}).delay(300);
	}, 
	
	/**
     * 快速查询回调函数
     * @param {} config
     */
	loadStore:function(config){
		var condsql=config.condsql;
		if(Ext.isEmpty(condsql) || 'undefined' == condsql){
              condsql="";
        }
        //转码
        condsql = getEncodeStr(condsql);
        //other_param参数扩展方法
		setTemplPropetyOthParam('search_sql', condsql);
		var map = new HashMap();
		initPublicParam(map,templateList_me.templPropety);
		var store = Ext.data.StoreManager.lookup('templatelist_dataStore');
	    Rpc({functionId:'MB00003001',async:false,success:function(){
	    	store.loadPage(1);//bug 39992 快速搜索后没有跳转到第一页，不显示人员
	    },scope:this},map);
	},
	
	// 【组号】渲染
	renderGroupColumn:function(value, metaData, record, rowIndex, colIndex, store, view){
		var _to_id="";
		var group_no=0;
		if(rowIndex == 0){
			templateList_me.group_arr = new HashMap();
			store.each(function(record,index){
				var key_value = record.get('objectid_e');
				var to_id = record.get('to_id');
                if(to_id == "" || to_id==null)
                { 
                	templateList_me.group_arr.put(key_value,"");
                }else{
	                if(_to_id != to_id)
	                {
	                	group_no++;
	                	_to_id = to_id;
	                }
	            	templateList_me.group_arr.put(key_value,group_no);
                }
			})
		}
		return templateList_me.group_arr.get(record.get('objectid_e'));
	},
	//HTML编辑器linbz 
	showHtmlEditRender:function(value, metaData, Record){
		var columnId=metaData.column.dataIndex;
		var title=metaData.column.tooltip;
		//是否有编辑权限1只读，2编辑
		var rwPriv = Record.get("htm_" +columnId);
        var imgName="row_view.png";
        if (rwPriv==2){
          imgName="row_edit.png";
        }
        //27746要修改的记录下标
        var rowIndex = metaData.rowIndex;
        //HTML转码传参
        var content = getEncodeStr(value);
		var html = "<a href=javascript:void(0); onclick=templateList_me.showHtmlEdit('"+rwPriv+"','"+columnId+"','"+title+"','"+content+"','"+rowIndex+"');><img src="+rootPath+"/images/new_module/"+imgName+" border=0></a>"; 
		return html;
	},
	showHtmlEdit:function(rwPriv,columnId,title,content,rowIndex){
		//解码
		content = getDecodeStr(content);
		content=replaceAll(content,"＜","<");
		content=replaceAll(content,"＞",">");
		content=replaceAll(content,"＇","'");
		content=replaceAll(content,"＂",'"');
		content=replaceAll(content,"；",";");
		
		content=replaceAll(content,"&lt;","<");
		content=replaceAll(content,"&gt;",">");
		//content=replaceAll(content,"&nbsp;"," ");
		content=replaceAll(content,"&quot;",'\"');
		content=replaceAll(content,"&#39;","\'");
		//if(templateList_me.opinion_field&&templateList_me.opinion_field.toUpperCase==columnId.toUpperCase)
		//{
			if(content.indexOf("<br />")==-1)//liuyz 如果已经替换过\n就不再替换
			{
				content=replaceAll(content,"\n","<br />");//liuyz bug31563
			}
		//}
	    var config = {};
		config.toolbar = [
				['Source','-','Undo','Redo'],
				['Find','Replace','-','SelectAll','RemoveFormat'],
				['Bold','Italic','Underline'],
				['JustifyLeft','JustifyCenter','JustifyRight','JustifyFull'],
				['SpecialChar','Subscript','Superscript'],
				['Style','FontFormat','FontName','FontSize'],
				['TextColor','BGColor','-','Image']];
				
		config.enterMode = CKEDITOR.ENTER_BR;
        config.height=470;
        
        if(rwPriv == 2){
            config.readOnly = false;
            //linbz 27871 增加延时，解决ckEditor.js参数无效问题
            setTimeout(function(){},200);
        }else{
            config.readOnly = true;
            setTimeout(function(){},200);
        }
        
		var CKEditor = Ext.getCmp('ckeditorid');
		if(CKEditor){
			CKEditor.setData(content);
		}else{
			 CKEditor = Ext.create("EHR.ckEditor.CKEditor",{
				id:'ckeditorid',
				border:0,
				padding:'30 0 0 0',
				hidden:false,
				value:content,
				ckEditorConfig:config
			});
		}
		
   		var editWin = Ext.getCmp('ckeditorWin');
   		if(editWin){
   		}else{
   			editWin = Ext.widget('window',{
				id:'ckeditorWin',
		    	title:title,
		    	buttonAlign:'center', 
		    	height:550,
		    	width:800,
				modal:true,
				maximizable:true, 
		    	closable:true,
		    	constrain : true,
				layout:{type:'vbox',align:'center'},
		    	items:[{id:'winedit',
		    			xtype:'panel',
		    			width:750,
					    height:470,
					    border:1,
					    items:[CKEditor]}
			    	],
		    	buttons:[{text:'确定',handler:function(){
		    		        var CKEditor = Ext.getCmp('ckeditorid');
		    		        if(!CKEditor.ckEditorConfig.readOnly){
			    				var htl = Ext.getCmp('ckeditorid').getHtml();
			    				var store=templateList_me.templateListGrid.tablePanel.getStore();
			    				var records = store.getAt(rowIndex);
			    				records.set(columnId,htl);
		    		        }
							editWin.close();
		    			}},
		    			{text:'取消',handler:function(){
		    					editWin.close();
		    			}}],
    			listeners:{
					'resize':function(){
							var widthwin = Ext.getCmp('ckeditorWin').getWidth();
							Ext.getCmp('winedit').setWidth(widthwin-50);
							Ext.getCmp('ckeditorid').setWidth(widthwin-50);
							var heightwin = Ext.getCmp('ckeditorWin').getHeight();
							Ext.getCmp('winedit').setHeight(heightwin-80);
							Ext.getCmp('ckeditorid').setHeight(heightwin-80);
					},
	               "close":function(){
	               	    var CKEditor = Ext.getCmp('ckeditorid');
		    		    if(!CKEditor.ckEditorConfig.readOnly){
							var htl = Ext.getCmp('ckeditorid').getHtml();
							var store=templateList_me.templateListGrid.tablePanel.getStore();
		    				var records = store.getAt(rowIndex);
		    				records.set(columnId,htl);
	    				}
						editWin.destroy();
	               }
	             }
		    });
	    editWin.show();
   		}
	},
    // 人事异动界面【子集】按钮渲染
	showSubsetRender:function(value, metaData, Record){
		var columnId=metaData.column.dataIndex;
		var basepre=Record.data.basepre; 
		var title=metaData.column.text;
		var a0100=Record.data.a0100;
		var objectid = Record.data.objectid_e;
		var realtask_id=Record.data.realtask_id_e;  
        if(!!!realtask_id)
           	realtask_id = "0";
		var ins_id = Record.get('ins_id');
		if(!!!ins_id)
			ins_id = "0"
		var rwPriv = Record.get("sub_" +columnId);//子集是否有编辑权限
		var chgState = Record.get("chg_" +columnId);//子集 变化前或者变化后
        var imgName="row_view.png";
        if (rwPriv==2){
          imgName="row_edit.png";
        }
		var html = "<a href=javascript:templateList_me.showSubset('"+chgState+"','"+rwPriv+"','" + basepre+"','"+a0100+"','"+columnId+"','"+title+"','"+ins_id+"','"+objectid+"','"+realtask_id+"');><img src="+rootPath+"/images/new_module/"+imgName+" border=0></a>"; 
		return html;
	},
	//附件渲染
	showAttachmentRender:function(value, metaData, Record){
		var dataindex = metaData.column.dataIndex;
		var columnId=dataindex.split("_k_")[0];
		var uniqueId=dataindex.split("_k_")[1];
		var basepre=Record.data.basepre; 
		var title=metaData.column.tooltip;
		var a0100=Record.data.a0100;
		var objectid = Record.data.objectid_e;
		var ins_id = Record.get('ins_id');
		if(!!!ins_id)
			ins_id = "0"
		var rwPriv = Record.get("att_" +dataindex);//附件是否有编辑权限
        var imgName="row_view.png";
        if (rwPriv==2){
          imgName="row_edit.png";
        }
		var html = "<a href=javascript:templateList_me.showAttachment('"+rwPriv+"','"+columnId+"','"+title+"','"+ins_id+"','"+objectid+"','"+uniqueId+"');><img src="+rootPath+"/images/new_module/"+imgName+" border=0></a>"; 
		return html;
	},
	//显示附件
	showAttachment:function(rwPriv,columnId,title,ins_id,objectid,uniqueId){
		var map = new HashMap();
		map.put("view_type","list");
		map.put("isshowwin","1");
		map.put("title",title);
	    map.put("object_id",objectid); 
		map.put("ins_id",ins_id);   //实例id
		map.put("tabid",templateMain_me.templPropety.tab_id);//模板号
		map.put("attachmenttype",columnId.substring(columnId.indexOf("_")+1,columnId.length));    
		map.put("rwPriv",rwPriv);//0无，1只读，2编辑
		map.put("uniqueId",uniqueId);
		map.put("infor_type",templateList_me.templPropety.infor_type); //编辑完子集内容需在卡片界面同步显示，回调方法
		Ext.require('TemplateCardUL.TemplateAttachment',function(){
			var re = Ext.create("TemplateCardUL.TemplateAttachment",{map:map});
		})
	},
	//单位类型渲染
	codesetidRender:function(value){
		var codeType = "";
		if(value == 'UM')
			codeType = "部门";
		else if(value == 'UN')
			codeType = "单位";
		else if(value == '@K')
			codeType = "岗位";
		return codeType;
	},
	
	//显示子集
    showSubset:function(chgState,rwPriv,basepre,a0100,columnId,title,ins_id,objectid,task_id){   	
        var map = new HashMap();
        map.put("view_type","list");
		map.put("basepre",basepre);
		map.put("tabid",templateMain_me.templPropety.tab_id); 
		map.put("ins_id",ins_id);
		map.put("multimedia_maxsize",templateMain_me.templPropety.multimedia_maxsize);   //文件限制大小
		map.put("rootDir",templateMain_me.templPropety.rootDir);   //是否设置了文件根目录
		map.put("rwPriv",rwPriv);    //是否可编辑
		map.put("chgState",chgState);//变化前后标识
		map.put("nodePriv",templateList_me.nodePriv);//是否走节点权限控制
		map.put("approveflag",templateMain_me.templPropety.approve_flag);
		map.put("a0100",a0100);
		map.put("table_name",this.templPropety.table_name);   //表名
		map.put("columnName",columnId);    //字段名
		map.put("data_xml",'');
		map.put("title",title); 
		map.put("objectid",objectid); 
		map.put("task_id",task_id); 
		
		Ext.require('TemplateSubsetUL.TemplateSubsets',function(){
			var re = Ext.create("TemplateSubsetUL.TemplateSubsets",{map:map});
		})
    },
    //单位部门岗位级联 hej add 2016-6-2
    addOrgListens:function(grid){
    	var tablePanel = grid.tablePanel;
    	var grid = tablePanel;
    	var columnUNs = grid.query('gridcolumn[dataIndex=b0110_2]');
    	var columnUMs = grid.query('gridcolumn[dataIndex=e0122_2]');
    	var columnKs = grid.query('gridcolumn[dataIndex=e01a1_2]');
    	/* if(columnUNs.length>0||columnUMs.length>0||columnKs.length>0){  */
			grid.on('beforeedit', function(editor, context, eOpts){
	    		var UNid = '',UMid='';
	    		var column = context.column;
				var record = context.record;
				var imppeople=column.imppeople;//是否启用选人组件
				if(imppeople&&(column.columnType=='A'||column.columnType=='M')){
					if(templateMain_me.templPropety.isValidOnlyname==undefined||templateMain_me.templPropety.isValidOnlyname=='false'){
						Ext.showAlert("请设置并且启用唯一性指标！")
						return false;
					}
					var field = context.field;
					var	defaultSelectedPerson=new Array();
					if(record.get(field)!=undefined&&record.get(field)!=''){
						var hashvo = new HashMap();
						   hashvo.put("ids",record.get(field));
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
						deprecate : '',//不显示的人员
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
							var value = staffids.substring(0,staffids.length-1);
						     //校验内容长度
							 var vaLength = getWordsTrueLength(value,true);
							 var limitlength = 0;
							 if(column.columnType=="M"&&!Ext.isEmpty(column.limitlength)){
								 limitlength = column.limitlength;
							 }else{
								 limitlength = column.columnLength;
							 }
							 
						     if(limitlength!=0&&limitlength!=10&&vaLength>limitlength){
						        Ext.showAlert("该文本的字数限制"+limitlength+"个！",function(){
						        	//截取字符串按'`'符号截取，到长度小于限制长度
						        	var value_ = templateList_me.subStringValue(value,limitlength);
						        	record.set(field,value_);	
									if(errerMsg.length>0){
										Ext.showAlert(errerMsg+"的唯一性指标值为空，不能保存。");
									}
						        });
						     }else{
						    	record.set(field,value);	
								if(errerMsg.length>0){
									Ext.showAlert(errerMsg+"的唯一性指标值为空，不能保存。");
								}
						     }
						}
					}, f);
					p.open();
					return false;
				}
	    		var columnUN = grid.query('gridcolumn[dataIndex=b0110_2]');
	    		var columnUM = grid.query('gridcolumn[dataIndex=e0122_2]');
	    		if(columnUN.length>0){
	    			UNid = columnUN[0].dataIndex;
	    		}
	    		if(columnUM.length>0){
	    			UMid = columnUM[0].dataIndex;
	    		}
	    		if(column.codesetid=='UM'&&column.dataIndex=='e0122_2'){
	    			column.getEditor().afterCodeSelectFn = templateList_me.afterCodeSelect;
			        if(columnUN.length>0){
			        	var value = record.get(UNid);
			        	if(value!=''&&value!='`'&&value!=undefined)
			        		column.getEditor().parentid=value.split('`')[0];
			        }
			    }else if(column.codesetid=='@K'&&column.dataIndex=='e01a1_2'){
			    	column.getEditor().afterCodeSelectFn = templateList_me.afterCodeSelect;
			       	if(columnUM.length>0){
			       		var value = record.get(UMid);
			        	if(value!=''&&value!='`'&&value!=undefined){
	    					column.getEditor().parentid=value.split('`')[0];
	    				}else if(columnUN.length>0){
	    					var value = record.get(UNid);
			        		if(value!=''&&value!='`'&&value!=undefined)
	    						column.getEditor().parentid=value.split('`')[0];
	    				}
			        }
			        else if(columnUN.length>0){
			        	var value = record.get(UNid);
			        	if(value!=''&&value!='`')
	    					column.getEditor().parentid=value.split('`')[0];
			        }
			    }else if(column.codesetid!=''&&column.codesetid!='0'){
					if(column.fatherRelationField!=undefined&&column.fatherRelationField!=''){
						var columns = grid.query('gridcolumn[dataIndex='+column.fatherRelationField+']');
						if(columns.length>0){
							var value = record.get(column.fatherRelationField);
							if(value!=''&&value!='`'&&value!=undefined){
								column.getEditor().parentid=value.split('`')[0];
							}
						}
					}
				}
			});
	    	grid.on('edit', function(editor, context, eOpts) {
	    		var searchlevel = '', UNid = '',UMid='',Kid='';
	    		var rowIdx = context.rowIdx;//编辑的行
	    		var colIdx = context.colIdx;//编辑的列
	    		var field = context.field;//当前编辑的列名
	    		var record = context.record;
	    		var data = record.data;//
	    		var column = context.column;
	    		var value = context.value;//改动的值
	    		var originalValue = context.originalValue;//原来值
	    		var codesetid = column.codesetid;
	    		var columnUN=new Array();
	    		var columnUM=new Array();
	    		var columnK=new Array();
				//Ext.each(columnUN,function(e){var UNid = e.dataIndex;console.log(UNid);})
				//兼容除了b0110_2、e0122_2、e01a1_2之外关联UM的指标级联
				if(column.dataIndex.toUpperCase()=='E01A1_2'||column.dataIndex.toUpperCase()=='E0122_2'||column.dataIndex.toUpperCase()=='B0110_2'){
					 columnUN = grid.query('gridcolumn[dataIndex=b0110_2]');
					 columnUM = grid.query('gridcolumn[dataIndex=e0122_2]');
					 columnK = grid.query('gridcolumn[dataIndex=e01a1_2]');
					if(columnUN.length>0){
						UNid = columnUN[0].dataIndex;
					}
					if(columnUM.length>0){
						UMid = columnUM[0].dataIndex;
					}
					if(columnK.length>0){
						Kid = columnK[0].dataIndex;
					}
					
					if(column.codesetid=='@K'&&column.dataIndex=='e01a1_2'){
						if(columnUN.length>0){
							searchlevel+='UN,';
						}
						if(columnUM.length>0){
							searchlevel+='UM,';
						}
					}
					else if(column.codesetid=='UM'&&value!=originalValue&&column.dataIndex=='e0122_2'){
						if(columnK.length>0){
							record.set(Kid,'');
						}
						if(value=='`'){
							if(columnUN.length>0&&columnUN[0].selectCode=='1'){
								columnUN[0].getEditor().parentid='';
								columnUN[0].selectCode='0';
								record.set(UNid,'');
							}
							if(columnK.length>0){
								columnK[0].getEditor().parentid='';
								record.set(Kid,'');
							}
						}
						if(columnUN.length>0){
							searchlevel+='UN';
						}
					}
					else if(column.codesetid=='UN'&&value!=originalValue&&column.dataIndex=='b0110_2'){
						if(columnUM.length>0){
							record.set(UMid,'');
						}
						if(columnK.length>0){
							record.set(Kid,'');
						}
						if(value=='`'){
							if(columnUM.length>0){
								columnUM[0].getEditor().parentid='';
								record.set(UMid,'');
							}
							if(columnK.length>0){
								columnK[0].getEditor().parentid='';
								record.set(Kid,'');
							}
						}
					}
				}
				//清空级联的孩子指标值
				if(column.codesetid!=''&&column.codesetid!='0'&&value!=originalValue&&column.childRelationField!=''&&column.childRelationField!=undefined){
					var childRelationFieldStr=column.childRelationField;
					if(childRelationFieldStr!=undefined&&childRelationFieldStr.length>0){
						var childRelationFieldList=childRelationFieldStr.split(",");
						for(var index=0;index<childRelationFieldList.length;index++){
							templateList_me.clearValue(childRelationFieldList[index],record,grid);
						}
					}
				}
					var map = new HashMap();
					map.put('codesetid',column.codesetid);
					map.put('itemid',value.substring(0,value.indexOf('`')));
					map.put('searchlevel',searchlevel);
					Rpc({functionId:'MB00004004',async:false,success:function(form){
						var result = Ext.decode(form.responseText);
						var returnlist = result.returnlist;
						var isHasFather=true;
						var fatherRelationFieldStr=column.fatherRelationField;
						Ext.each(returnlist,function(e){
							if(e.codesetid=='UM'&&columnUM.length>0){
							    var codeitemdesc = e.codeitemdesc;
								var layerdesc = e.layerdesc;
								if(layerdesc!=''&&layerdesc!=undefined){
									codeitemdesc = layerdesc;
								}
								//if(record.get(UMid)==''||record.get(UMid)=='`'||record.get(UMid)==undefined){
									record.set(UMid,e.codeitemid+'`'+codeitemdesc);
								//}
							}
							if(e.codesetid=='UN'&&columnUN.length>0){
								//if(record.get(UNid)==''||record.get(UNid)=='`'||record.get(UNid)==undefined){
								if(codesetid!='UN')
									record.set(UNid,e.codeitemid+'`'+e.codeitemdesc);
								//}
							}
							//由下向上级联
							if(e.codesetid!=undefined&&e.codesetid!=''&&e.codesetid!='0'&&column.fatherRelationField!=''&&column.fatherRelationField!=undefined){
								var columns= grid.query('gridcolumn[dataIndex='+fatherRelationFieldStr+']');
								if(columns.length>0){
									column=columns[0];
								}else{
									isHasFather=false;
								}
								if(column&&column.editableValidFunc!="false"){
									if(column.codesetid.toUpperCase()=='UN'&&e.codesetid=='UN'||column.codesetid.toUpperCase()=='UM'&&e.codesetid=='UM'||column.codesetid.toUpperCase()=='UM'&&e.codesetid=='UN'||column.codesetid.toUpperCase()=='@K'&&e.codesetid=='@K'||((column.codesetid!=''||column.codesetid!=undefined)&&column.codesetid.toUpperCase()!='UM'&&column.codesetid.toUpperCase()!='UN'&&column.codesetid.toUpperCase()!='@K')){
										record.set(fatherRelationFieldStr,e.codeitemid+'`'+e.codeitemdesc);
										if(column.fatherRelationFieldStr!=undefined&&column.fatherRelationFieldStr.length>0&&isHasFather){
											 fatherRelationFieldStr=column.fatherRelationField;
										}
									}
								}
							}
						});
					},scope:this},map);
			});
		//}
    },
    afterCodeSelect:function(dataindex,value){
   		var column = templatelistObj.tablePanel.query('gridcolumn[dataIndex=b0110_2]');
   		if(column.length>0){
   			column[0].selectCode = '1';
   		}
    },
    //选中事件、样式改变事件等等 lis 20160411
    addGridListens:function(grid){
    	var tablePanel = grid.tablePanel;
    	var grid = tablePanel;
    	if(tablePanel.enableLocking){//如果当前grid是锁列的 
			grid = grid.lockedGrid;//锁列所在的grid
		}
    	//点击列头事件
    	grid.on('headerclick',function(ct, column, e, t, eOpts){
	        	//第0列
			    var propety =  this.templPropety;
	        	if(column.getIndex() == 0){
	        			var task = new Ext.util.DelayedTask(function(){
    				    var sel = grid.getSelectionModel();
	       			    var records = [];
	        			grid.getStore().getData().each(function(record,index){
	        				records.push(record.data);
	        				var objectid=record.data.objectid_e;
	        	       		templateList_me.object_id=objectid;
	        			})
	        		    var submitflag = "0";
	        			if(sel.getCount() == records.length)
	        		    	submitflag = "1";
	        		    var map = new HashMap();
	        		    setTemplPropetyOthParam('sub_moduleId', templateList_me.subModuleId);
	        		    //选中
	        		    initPublicParam(map,propety);
	        		    //28838
	        		    map.put("doSelectAll","0");
	        		    map.put("submitflag",submitflag);
//	        		    map.put("selectRecords",records);
	        		    Rpc({functionId:'MB00003005',async:false,success:function(){}},map);
	        		});
	        		task.delay(1);
	        	}
	        },templateList_me);
		//复选框选中状态   
    	grid.on('cellclick',function(view, td, cellIndex, record, tr, rowIndex, e, eOpts ){
			//第0列
	       	if(cellIndex == 0){
		       	 var a0100=record.data.a0100;
		         var nbase=record.data.basepre;
		         var seqnum=record.data.seqnum;  
		         var insid=record.data.ins_id;
		         var realtask_id=record.data.realtask_id_e;  
		         if(!!!realtask_id)
	                	realtask_id = "0";
		         var objectid=record.data.objectid_e;
		         templateList_me.object_id=objectid;//liuyz 记录当前选中人导出odf使用
		         //templateList_me.current_object_id=nbase+"`"+a0100;
		         templateList_me.current_object_id=record.data.objectid;
		         var objectid=record.data.objectid_e;
		       		templateList_me.object_id=objectid;
		         var selectionMode = tablePanel.getSelectionModel();
		         var isSelect = selectionMode.isSelected(record);
		         var submitflag = "0";
	                if(isSelect){
	                    submitflag = "1";
	                }
		         var map = new HashMap();
		         //var objectid = nbase +"`" + a0100;
		         initPublicParam(map,this.templPropety);//初始化参数
		         //28838
		         map.put("doSelectAll","1");
		         map.put("objectid",objectid);
		         map.put("submitflag",submitflag);
		         map.put("task_id",realtask_id);
				// map.put("tabid",templateCard_me.templPropety.tab_id);
				 //map.put("basepre",nbase);
				 //map.put("ins_id",insid);
				 //map.put("a0100",a0100);
				 //map.put("seqnum",seqnum);
				 //map.put("sp_batch",templateCard_me.templPropety.sp_batch);
				 //map.put("infor_type",templateCard_me.templPropety.infor_type);
				 Rpc({functionId:'MB00003005',async:false,success:function(){}},map);
	       	}
	       	else
	       	{
	       		//liuyz 记录当前选中人导出odf使用
	       		var objectid=record.data.objectid_e;
	       		templateList_me.object_id=objectid;
	       	}
       },templateList_me);
		//复选框选中状态	
/*		templatelistObj.tablePanel.on('select',function(Datathis,record, index, eOpts){
	         var a0100=record.data.a0100;
	         var nbase=record.data.basepre;
	         var seqnum=record.data.seqnum;  
	         var insid=record.data.ins_id;
	         var realtask_id=record.data.realtask_id;  
	         templateList_me.current_object_id=nbase+"`"+a0100;
	         var map = new HashMap();
	         map.put("basepre",nbase);
			 map.put("tabid",me.templPropety.tab_id);
			 map.put("task_id",realtask_id);
			 map.put("ins_id",insid);
			 map.put("a0100",a0100);
			 map.put("seqnum",seqnum);
			 map.put("submitflag","1");
			 map.put("sp_batch",me.templPropety.sp_batch);
			 map.put("infor_type",me.templPropety.infor_type);
			// map.put("setname",tablename);   //表名
			 Rpc({functionId:'MB00003005',async:false,success:function(){}},map);
        });*/
		/*//复选框取消选中状态
        templatelistObj.tablePanel.on('deselect',function(Datathis,record, index, eOpts){
	         var a0100=record.data.a0100;
	         var nbase=record.data.basepre;
	         var seqnum=record.data.seqnum;
	         var insid=record.data.ins_id;
	         var realtask_id=record.data.realtask_id;
	         var map = new HashMap();
	         map.put("basepre",nbase);
			 map.put("tabid",me.templPropety.tab_id);
			 map.put("task_id",realtask_id);
			 map.put("ins_id",insid);
			 map.put("a0100",a0100);
			 map.put("seqnum",seqnum);
			 map.put("submitflag","0");
			 map.put("sp_batch",me.templPropety.sp_batch);  
			 map.put("infor_type",me.templPropety.infor_type);
			// map.put("setname",tablename);   //表名
			 Rpc({functionId:'MB00003005',async:false,success:function(){}},map);
        });*/
        //todoliuzy 对于在流程中的数据，不应该这样判断，templet_tabid表中的Submitflag已经无效，被t_wf_task_objlink中submitflag替代
        tablePanel.getStore().on('load',function(store,records){
		    var store_me = this;
	        var selectModel = store_me.getSelectionModel();
	        var arrRecords=new Array();
		    for(var i=0;i<records.length;i++){
	           if(records[i].get("submitflag2")=='1'){
			      arrRecords.push(records[i]); 
			      //templateList_me.current_object_id=records[i].data.basepre+"`"+records[i].data.a0100;
			      templateList_me.current_object_id=records[i].data.objectid;
			      templateList_me.object_id=records[i].data.objectid_e;
	           }
		    }
		    selectModel.select(arrRecords,false,true);
		    //29032 linbz 修改栏目设置名称时，同步更新快速查询框的下拉数据
		    templateMain_me.createSearchBox();
		    
		    //29235 linbz 增加选人控件不显示的人员参数
		    var map = new HashMap();
            initPublicParam(map,templateList_me.templPropety);
            setTemplPropetyOthParam('deprecate_flag', "1");   
            Rpc({functionId:'MB00003001',async:false,success:function(form){
                var result = Ext.decode(form.responseText);
                templateMain_me.deprecate=result.deprecate;
                setTemplPropetyOthParam('deprecate_flag', "");   
            },scope:this},map);
            
	    },tablePanel); 
        
        //行颜色改变事件
        templateMain_me.rowCssChangeEvent(tablePanel,templateList_me,false);
        //分页栏添加监听
        var pagingtool = Ext.getCmp('templatelist_pagingtool');
        pagingtool.on({
            beforechange: function (e, pageData, eOpts) {
    	    	var store = Ext.data.StoreManager.lookup('templatelist_dataStore');
    			var addOrUpdateList = store.getModifiedRecords();
    			if(addOrUpdateList.length>0){
    				templateTool_me.save('true','true');
    			}
            }
        });
    },
	/**
	 * 当前类主面板，供其他类调用
	 * */
	getMainPanel:function(){
		return this.mainPanel;
	},
	
	//获得表格控件生成的json字符串
	getObj:function(){
		return this.obj;
	},showNumberRender:function(disValue,meta,record){
		templateList_me.setLogColor(meta,record);
		//北理工优化 有小数位显示，无小数位不显示.00
		if(disValue!=0&&disValue!=null&&disValue.length>0)
		{
			disValue=disValue.toString();
			var decimal=disValue.substring(disValue.indexOf(".")+1);
		    if(parseInt(decimal,10)==0)//这样就不用判断小数位数了
	        {
	         	disValue=disValue.substring(0,disValue.indexOf("."));
	        }
        }
		return disValue;
	},addindexof:function(){
		if (!Array.prototype.indexOf)
			{
			  Array.prototype.indexOf = function(elt /*, from*/)
			  {
			    var len = this.length >>> 0;
			    var from = Number(arguments[1]) || 0;
			    from = (from < 0)
			         ? Math.ceil(from)
			         : Math.floor(from);
			    if (from < 0)
			      from += len;
			    for (; from < len; from++)
			    {
			      if (from in this &&
			          this[from] === elt)
			        return from;
			    }
			    return -1;
			  };
			}
	}, clearValue : function(id,record,grid){
		var columns = grid.query('gridcolumn[dataIndex='+id+']');
		if(columns.length>0){
			var column=columns[0];
			if(column&&column.editableValidFunc!="false"){
			record.set(id,'');
			column.getEditor().parentid='';
			var childRelationFieldStr=column.childRelationField;
			if(childRelationFieldStr!=undefined&&childRelationFieldStr.length>0){
				var childRelationFieldList=childRelationFieldStr.split(",");
				for(var index=0;index<childRelationFieldList.length;index++){
					templateList_me.clearValue(childRelationFieldList[index],record,grid);
				}
			}
			}
		}
	},
	subStringValue:function (value,limitlength){
		var index = value.lastIndexOf("、");
		var value_ = value.substring(0,index);
		var vaLength = getWordsTrueLength(value_,true);
		if(vaLength>limitlength){
			value_= templateList_me.subStringValue(value_,limitlength);
		}else{
			return value_;
		}
		return value_;
	},
	showRender:function(value,meta,record){
		templateList_me.setLogColor(meta,record);
		var columnType = meta.column.columnType;
		var codesetid = meta.column.codesetid;
		if(columnType=='A'&&codesetid!='0'){
			var valuearr = value.split("`");
    		if(valuearr.length>=2)
    			value = valuearr[1];
    		else
    			value='';
		}
		return value;
	},
	/**
	 * 设置变动内容颜色
	 */
	setLogColor:function(meta,record){
		var columnId = meta.column.dataIndex;
		var basepre = record.data.basepre; 
		var a0100 = record.data.a0100;
		var realtask_id = record.data.realtask_id==''?'0':record.data.realtask_id;
		var ins_id = record.data.ins_id==''?'0':record.data.ins_id;
		var changeValue = basepre+a0100+':'+ins_id+':'+realtask_id+':'+columnId;
		for(var i=0;i<templateList_me.changeInfoList.length;i++){
			var changeInfo = templateList_me.changeInfoList[i];
			if(changeInfo==changeValue){
				meta.style += ' color:'+templateMain_me.templPropety.autoLogColor+';';
			}
		}
	}
});

