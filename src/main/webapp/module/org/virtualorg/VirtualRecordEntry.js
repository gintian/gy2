Ext.define("Virtualorg.VirtualRecordEntry",{
	tablePanel:undefined,
	code:undefined,
	constructor:function(config) {
		globalVirOrg = this;
		var vo = new HashMap();
		globalVirOrg.code=globalVirOrg.getCode();
		vo.put("code",globalVirOrg.getCode());
		Rpc({functionId:'ORG0000001',async:false,success:this.createTablePanel,scope:this},vo);
	},
	//获取组织机构编码
	getCode:function(){
		var hrefs=self.location.href;
        if(hrefs.indexOf("&")>-1)
        	return hrefs.split("&")[1]/*.split("=")[1]*/;
        else
        	return "";
	},
	//创建表格
	createTablePanel:function(param){
		var result = Ext.decode(param.responseText);
		globalVirOrg.code = result.code;
		globalVirOrg.tablePanel = new BuildTableObj(result.tableConfig);
		globalVirOrg.tablePanel.toolBar.insert(6,{
                                                          xtype: 'checkbox',
                                                          fieldLabel: ' 显示当前机构所有人员',
                                                          labelWidth:120,
                                                          id:'radioselect',
                                                          checked:true,
                                                          listeners:{
                                                                click: {
                                                                        element: 'el', 
                                                                        fn: function(){
                                                                                   var flag=Ext.getCmp('radioselect').checked;
                                                                                       var map=new HashMap();
                                                                                       if(flag)
                                                                                       map.put('checkflag',"1");
                                                                                       else
                                                                                       map.put('checkflag',"0");
                                                                                       map.put('code',globalVirOrg.code);
                                                                                       Rpc({functionId:'ORG0000015',async:false,success:function(){
                                                                                            globalVirOrg.tablePanel.tablePanel.getStore().reload();
                                                                                       },scope:globalVirOrg},map);
                                                                            }
                                                                    }
                                                          }
                                                   });
	},
	//新增员工
	newMember:function(){
		var picker = new PersonPicker({
			multiple : true,
			text:"选择",
            titleText:"选择",
			isMiddle:true,
			isPrivExpression:false,//不启用高级权限
			callback : function(c) {
				globalVirOrg.pickerCallBack(c);
			}
		},this);
		picker.open();
	},
	//选人控件回调函数
	pickerCallBack:function(c){
		var vo = new HashMap();
		vo.put("selectPersons",c);
		vo.put("code",globalVirOrg.code);
		Rpc({functionId:"ORG0000007",async:false,success:globalVirOrg.refreshTableData,scope:this},vo);
	},
	//表格控件刷新store数据
	refreshTableData:function(){
		
		globalVirOrg.tablePanel.tablePanel.getStore().reload();
	},
	//删除虚拟组织人员
	cancelMember:function(){
		var selections = globalVirOrg.tablePanel.tablePanel.getSelectionModel().getSelection();
		if(selections.length<=0){
			Ext.Msg.alert("提示信息","请选择要撤销的人员！");
			return;
		}
		Ext.Msg.confirm ( "提示信息" , "确定要将选中人员从所属虚拟机构中撤销吗？" , function(buttonid,value,opt){
			if(buttonid=='no') return;
			else if(buttonid=='yes'){
				var datas = [];
				for(var i=0;i<selections.length;i++){
					datas.push({
						nbase:selections[i].data.nbase_e,
						a0100:selections[i].data.a0100_e,
						i9999:selections[i].data.i9999
					});
				}
				var vo = new HashMap();
				vo.put("datas",datas);
				Rpc({functionId:'ORG0000003',async:false,success:globalVirOrg.refreshTableData,scope:this},vo);
			}
		} , this);
	},
	//虚拟角色
	virtualRole:function(){
		new Virtualorg.VirturalRoleTrans({});			
	},
	validateVorg:function(record){
		var vo = new HashMap();
		vo.put("","");
		if(record.data.orgtype!="vorg")return false;
		else return record;
	},
	getParentId:function(index,record){
	    if(!record)
	       return "";
	    return  record.get(index.substring(0,3)+"02").split('`')[0];
	}
});