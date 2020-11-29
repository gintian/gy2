/**
 *学科组和评委会新建|编辑页面
 *
 *@author: haosl
 *
 *@date 20170522
 **/
Ext.define("CommoncompURL.CommonCompEdit",{
	extend:'Ext.window.Window',
	width:600,
	height:430,
	modal: true,
	layout:'fit',
	border:false,
	theId:"",//学科组或评委会ID
	pageType:'committee',//页面类型    committee | subjects
	opt:'1', //=1  新建 =2 编辑
	constructor:function(config){
		this.callParent(arguments);
		this.init(config);
	},
	init:function(config){
		this.pageType = config.pageType;
		this.theId = config.theId;
		this.opt = config.opt;
		var form = this.getMainPanel();
		//设置初始值，回显表单数据
		this.echoForm(form,this.theId);
		this.add(form);
	},
	getMainPanel:function(){
		var me = this;
		var title = "";
		if(me.opt == "1")
			title="新建";
		else
			title="编辑";
		var comsubName = "";
		if(this.pageType == 'committee'){
			comsubName = zc.menu.committeeshowtext+"名称";
			title +=zc.menu.committeeshowtext;
		}else{
			comsubName = zc.menu.subjectsshowtext+"名称";
			title += zc.menu.subjectsshowtext;
		}
		me.setTitle(title);//设置标题
		
		// 类别 数据源
		var store = new Ext.data.ArrayStore({
            fields: ['myId','displayText'],
            data: [[1, '高评委'], [2, '中评委']]
        });
		// 加载插件
		Ext.require(['EHR.extWidget.field.CodeTreeCombox']);
		var optionpanels = Ext.widget('codecomboxfield',{
			border : false,
			width:334,
			id : 'select_b0110',
			onlySelectCodeset:false,
			codesetid : "UM",
			ctrltype : "3",
			nmodule : "9",
			emptyText:'选择组织机构',
//			editable:false,
			allowBlank: false,
			listeners : {
				//校验组织机构的正确性
				'blur':function(comb,e){
					if(Ext.isEmpty(comb.value)){
						comb.setRawValue("");
						//刷新下拉数据    haosl 2017-07-24
						comb.treeStore.proxy.extraParams.searchtext = encodeURI(this.getRawValue());
						comb.treeStore.load();
					}
				}
			}
		});
		// 所属组织
		var container = Ext.create('Ext.container.Container', {
		    layout: {
		        type: 'hbox'
		    },
		    margin:'0 0 20 38',
		    border: false,
		    items: [{
		        xtype: 'label',
		        html: '<font color="red"> * </font>所属机构',
		        margin:'0 7 0 0'
		    },optionpanels]
		});
		return formPanel = Ext.create('Ext.form.Panel', {
		    bodyPadding: '20',
		    margin:'0 auto',
			border:false,
		    layout: 'anchor',
		    defaults: {
		        anchor: '80%'
		    },
		    defaultType: 'textfield',
		    items: [{
		    	id:'comsubName',
		        fieldLabel: comsubName,
		        labelSeparator:'',
		        beforeLabelTextTpl:"<font color='red'> * </font>",
		        labelAlign:'right',
		        maxLength:50,
		        allowBlank: false,
		        margin:'0 0 20 0'
		    },me.pageType=='committee'?{
		    	xtype:'combo',
		    	id:'type1',
		    	fieldLabel: '类别',
		    	labelSeparator:'',
		    	store:store,
		        forceSelection :true,
		        valueField: 'myId',
		        displayField: 'displayText',
		        editable:false,
		        labelAlign:'right',
		        allowBlank: false,
		        margin:'0 0 20 0',
		        listeners: {  
					afterRender: function(combo) {
		    			if(combo.isVisible()){
		    				var i = 0;
			    			if(me.opt == "2"){//编辑
			    				i = 1;
			    			}
							var firstValue = store.data.items[i];
							combo.setValue(firstValue);
		    			}
		            }    
		        }
		    }:undefined,{
		        xtype : 'textareafield',
		        id  : 'description',
		        fieldLabel: '描述',
		        labelSeparator:'',
		        labelAlign:'right',
		        grow : false,
		        height:100,
		        margin:'0 0 20 0'
		    },container,{
		    	id:'create_fullname',
		        fieldLabel: '创建人',
		        labelSeparator:'',
		        labelAlign:'right',
		        readOnly:true,
		        editable : false,
		        allowBlank: false,
		        margin:'0 0 20 0',
		        fieldStyle:'background-color:#EEEEEE;'
		    },{
		    	id:'create_time',
		        fieldLabel: '创建日期',
		        labelSeparator:'',
		        labelAlign:'right',
		        readOnly:true,
		        editable : false,
		        allowBlank: false,
		        fieldStyle:'background-color:#EEEEEE;'
		    }],
		    buttonAlign:'center',
		    buttons: [{
		        text: '确定',
		        handler: function() {
		        	var value = (Ext.getDom('comsubName-inputEl').value).replace(/(^\s*)|(\s*$)/g, "");//名称不能为空或只为空格
		        	if(Ext.isEmpty(value)){
		        		Ext.getDom('comsubName-inputEl').value = '';
		        	}
		            var form = this.up('form').getForm();
		            if (form.isValid()) {
		            	var values = form.getValues();
		            	values['select_b0110-inputEl'] = optionpanels.value+"`"+optionpanels.rawValue;
		            	if(me.opt == "1"){//新建
		            		
		            		if(me.pageType=="committee"){
		            			committeeGloble.currentPageClickState = '';
		            			me.newCommittee(values);
		            		}else
		            			me.newSubjects(values);
		            	}else if(me.opt == "2"){//编辑
		            		if(me.pageType=="committee")
		            			me.editCommittee(values,me.theId);
		            		else
		            			me.editSubjects(values,me.theId);
		            	}
		            	this.up('window').close();
		            }
		        }
		    },{
		        text: '取消',
		        handler: function() {
		            this.up('window').close();
		        }
		    }],
		    renderTo: Ext.getBody()
		});
	},
	/**
	 * 页面编辑时回显数据
	 * form 表单panel
	 * id 评委会或者学科组id
	 */
	echoForm:function(form,id){
		var me = this
		var info = '';
		var map = new HashMap();
		map.put("pageType",me.pageType);
		map.put('type', me.opt);//1:新建 2：编辑
		map.put('id', id);
		Rpc({functionId:'ZC00002106',async:false,success:function(data){
			var me = this;
			info = Ext.decode(data.responseText).infos;
		},scope:this},map);
		form.getForm().setValues(info);
	},
	//新建聘委会
	newCommittee:function(values){
		var me = this;
		var map = new HashMap();
		map.put("type", "1");
		map.put("committee_name", keyWord_filter(values['comsubName-inputEl']));
		map.put("committee_type", values['type1-inputEl']);
		map.put("description", values['description-inputEl']);
		map.put("b0110", values['select_b0110-inputEl'].split("`")[0]);
		map.put("create_fullname", values['create_fullname-inputEl']);
		map.put("create_time", values['create_time-inputEl']);
		Rpc({functionId:'ZC00002103',async:false,success:function(form,action){
			var data = Ext.decode(form.responseText);
			committeeGloble.currentCommitteeId = data.committee_id;
			var committee_name = data.committee_name;
			var committeePanel = Ext.getCmp('committeePanel');
			committeePanel.setVisible(true);
			committeeGloble.currentPage = 1;//committeeGloble.pageNum;//没有在最后一页增加评委会时，把页码定位到最后一页
			//刷新评委会列表 2017-06-28
			committee_me.getCommitteeList();
			committee_me.getCommittee();
			committee_me.getCommitteePerson(committeeGloble.currentCommitteeId, committeeGloble.ishistory);
		},scope:this},map);
	},
	//编辑聘委会
	editCommittee:function(values, cid){
		var me = this;
		var map = new HashMap();
		map.put("type", "2");
		map.put("committee_id", cid);
		map.put("committee_name", keyWord_filter(values['comsubName-inputEl']));
		map.put("committee_type", values['type1-inputEl']);
		map.put("description", values['description-inputEl']);
		map.put("b0110", values['select_b0110-inputEl'].split("`")[0]);
		map.put("create_fullname", values['create_fullname-inputEl']);
		map.put("create_time", values['create_time-inputEl']);
		Rpc({functionId:'ZC00002103',async:false,success:function(form,action){
			var label = Ext.getCmp("comName_"+cid);
			var newText = me.convertStr(values['comsubName-inputEl']);
			label.setText(newText);//更新文本
			var photo = Ext.getCmp("pho_"+cid);//更新提示信息
			photo.setTitle(values['comsubName-inputEl']+" 归属于 "+values['select_b0110-inputEl'].split("`")[1]);
		},scope:this},map);
	},
	//新建学科组
	newSubjects:function(values){
		var me = this;
		var map = new HashMap();
		map.put("type", "1");
		map.put("subjectsName", keyWord_filter(values['comsubName-inputEl']));
		map.put("description", values['description-inputEl']);
		map.put("b0110", values['select_b0110-inputEl'].split("`")[0]);
		map.put("create_fullname", values['create_fullname-inputEl']);
		map.put("create_time", values['create_time-inputEl']);
		Rpc({functionId:'ZC00002203',async:false,success:function(form,action){
			var data = Ext.decode(form.responseText);
			subjectsListGloble.currentGroup_Id = data.group_id;
			var subjectsPanel = Ext.getCmp('subjectsPanel');
			subjectsPanel.setVisible(true);
			subjectsListGloble.currentPage = 1;//subjectsListGloble.pageNum;//没有在最后一页增加评委会时，把页码定位到最后一页
			//刷新学科组列表 haosl 2017-06-28
			subjectsList_me.getSubjectsList();
			subjectsList_me.getSubjects();
			subjectsList_me.getSubjectsPerson(subjectsListGloble.currentGroup_Id, subjectsListGloble.isshowall);
		},scope:this},map);
	},
	//编辑学科组
	editSubjects:function(values, cid){
		var me = this;
		var map = new HashMap();
		map.put("type", "2");
		map.put("group_id", cid);
		map.put("subjectsName", keyWord_filter(values['comsubName-inputEl']));
		map.put("description", values['description-inputEl']);
		map.put("b0110", values['select_b0110-inputEl'].split("`")[0]);
		map.put("create_fullname", values['create_fullname-inputEl']);
		map.put("create_time", values['create_time-inputEl']);
		Rpc({functionId:'ZC00002203',async:false,success:function(form,action){
			var label = Ext.getCmp("subName_"+cid);
			var newText = this.convertStr(values['comsubName-inputEl']);
			label.setText(newText);//更新文本
			var photo = Ext.getCmp("pho_"+cid);//更新提示信息
			var titleValue = photo.getTitle();
			var title = values['comsubName-inputEl']+" 归属于 "+values['select_b0110-inputEl'].split("`")[1];
			photo.setTitle(title);
		},scope:this},map);
	},
	convertStr : function(str){
		var reStr = str;
		
		var maxwidth = 22;//字母排列的话最多占的个数
		var index = 0;
		var useWidth = 0;
		for(i=0; i<str.length; i++){
			 if(this.checknum(str.charAt(i))) {//字母或数字
			 	useWidth += 1;
			 } else {//汉字
			 	useWidth += 2;//每个汉字占宽度约为字母的2倍
			 }
			 if(useWidth >= maxwidth && index == 0){
			 	index = i;
			 }
		} 
		//checknum
		if(useWidth > maxwidth){
			reStr = str.substring(0, index);
			reStr += '...';
		}
		return reStr;
	},
	// 判断是否是字母或数字
	checknum : function(value) {
		var flg = false;
        var Regx = /^[A-Za-z0-9]*$/;
        if (Regx.test(value)) {
            flg =  true;
        }
        return flg;
    },
	listeners:{
		'show':function(){
			Ext.getCmp("comsubName").focus();//自动聚焦编辑框haosl 20161024
		}
	}
	
	
		
});
