Ext.define('Standard.StandardList',{
	extend:'Ext.panel.Panel',
	requires:['Standard.Standard','Standard.StandardStructure'],
	layout:'fit',
	config :{
		exparam:"",
	},
	initComponent:function(){
		StandardList = this;
		var pkg_id_e=StandardList.getExparam();
		this.callParent();
		this.loadData(pkg_id_e);
	},
	loadData:function(pkg_id_e){
		var vo = new HashMap();
		vo.put('pkg_id',pkg_id_e);
		Rpc({functionId:'GZ00001210',success:this.getTableOK,scope:this},vo);
	},
	getTableOK:function(form,action){
        var me = this;
        var result = Ext.decode(form.responseText);
        var return_code = result.return_code;
        //运行出现错误时，打印错误信息
        if(return_code == 'fail'){
                Ext.Msg.alert(sd_parameter.remind,result.return_msg);
                return;
        }
        var return_data = result.return_data;
        return_priv = result.return_priv;
        var obj = Ext.decode(return_data.gridConfig);
        obj.beforeBuildComp = function (grid) {
            grid.tableConfig.viewConfig={markDirty : false};
        };
        var tableObj = new BuildTableObj(obj);
        StandardList.gridPanel = tableObj.tablePanel;
        me.addEvent();
        me.add(tableObj.getMainPanel());
    },
    //薪资标准表名称列自定义渲染函数
    renderNameColumnFunc:function(value, metaData, record, rowIndex, colIndex, store, view){
    	// var html = "<a onclick="+StandardList.nameColumnClick(record.get('id_e_e'),record.get('pkg_id_e'))+" href='javascript:void(0);'>" + value + "</a>";
		if(return_priv["isEdit"]=="0"){
			var html=  value;
		}else{
    		var html= '<span style="color:#1B4A98;cursor: pointer" onclick="javascript:void(0);StandardList.nameColumnClick(\''+record.get('id_e_e')+'\',\''+record.get('pkg_id_e')+'\',\''+record.get('id')+'\')">' + value + '</span>'
		}
    	return html;
    },
	nameColumnClick:function(stanardId_e,pkgId,stanardId){
		Ext.getCmp('StandardList_mainPanel').setHidden(true);
		if(Standard.pkg_id){
			StandardList.remove(Standard);
		}
		var standard = Ext.create('Standard.Standard',{
			pkg_id:pkgId,
			stand_id:stanardId_e,
			viewType:'edit',
			isHaveEdit:return_priv[stanardId]
		});
		StandardList.add(standard);
	},
    //薪资标准表所属组织列自定义渲染函数
    renderB0110ColumnFunc:function(value, metaData, record, rowIndex, colIndex, store, view) {
    	if (!value) {
            return "全部";
        }
        if (value.indexOf("`") > -1) {
            return value.split("`")[1];
        }
        var vo = new HashMap();
        vo.put("ordIds", value);
        Rpc({functionId: 'GZ00001219', success: function (res) {
            var resData = Ext.decode(res.responseText);
            var return_code = resData.return_code;
            if (return_code == "success") {
                if (resData.return_data.orgDesc) {
                    value = resData.return_data.orgDesc;
                } else {
                    value = "";
                }
            }
        }, scope: this, async: false }, vo);
        record.set("orgdesc",value);
        return value;

    },
    //返回标准表沿革界面要调用的方法
    returnStandardPackage: function(){
    	window.location.href=("Standard.html");
    },
    //利用正则表达式判断s中是否有特殊字符
    containSpecial: function (s){
    	//正则表达式
		var containSpecial = RegExp(/[(\ )(\~)(\!)(\@)(\#)(\￥)(\`) (\$)(\%)(\^)(\&)(\*)(\()(\))(\-)(\_)(\+)(\=) (\[)(\])(\{)(\})(\|)(\\)(\;)(\:)(\')(\")(\,)(\.)(\/) (\<)(\>)(\?)(\)]+/);
		return (containSpecial.test(s));
    },
    addEvent: function () {
    	StandardList.gridPanel.on('edit', function(editor, e) {
    		var org = "";//修改后归属组织列的值
	        var name = "";//修改后名称列的值
	        var stand_id = e.record.data.id_e_e;//加密后的薪资标准表的id号
	        var pkg_id_e = StandardList.getExparam();//加密后的历史沿革套序号
	        var vo = new HashMap();
	        var dataMap = new HashMap();
    		if (e.value) {
    			if(e.field == 'name'){//薪资标准表名称列
    				if(StandardList.containSpecial(e.value)){
    					StandardList.gridPanel.getStore().load();//重新刷新store
    					Ext.Msg.alert(gz.standard.tip,gz.standard.sd.containSpecial);
    					return;
    				}
    				name = e.value;
    				org = e.record.data.b0110;
    			}
    			if(e.field == 'b0110'){//薪资标准表归属单位列
    				name = e.record.data.name;
    				org = e.value;
    			}
    	        dataMap.put("owner_org",org);
    	        dataMap.put("standName",name);
    	        vo.put("stand_id",stand_id);
    	        vo.put("pkg_id",pkg_id_e);
    	        vo.put("standInfo",dataMap);
    	        Rpc({functionId: 'GZ00001220', success: function (res) {
    	            var resData = Ext.decode(res.responseText);
    	            var return_code = resData.return_code;
    	            var return_msg = resData.return_msg;
    	            if (return_code == "fail") {
    	            	Ext.Msg.alert(gz.standard.tip,return_msg);
    	            }
    	        }, scope: this, async: false }, vo);
    	        //归属单位为空保存为空
            } else if(!e.value&&e.field == 'b0110'){
            	org = e.value;
            	name = e.record.data.name;
            	dataMap.put("owner_org",org);
    	        dataMap.put("standName",name);
    	        vo.put("stand_id",stand_id);
    	        vo.put("pkg_id",pkg_id_e);
    	        vo.put("standInfo",dataMap);
    	        Rpc({functionId: 'GZ00001220', success: function (res) {
    	            var resData = Ext.decode(res.responseText);
    	            var return_code = resData.return_code;
    	            var return_msg = resData.return_msg;
    	            if (return_code == "fail") {
    	            	Ext.Msg.alert(gz.standard.tip,return_msg);
    	            }
    	        }, scope: this, async: false }, vo);
            }
    	}),
    	StandardList.gridPanel.on('render', function (t) {
    		if(return_priv["isEdit"]=="0"){
				return;
			}
            var organizationColumn = t.getColumnManager().getHeaderByDataIndex("b0110");
            if (organizationColumn) {
                var eidtior = {
                    xtype: "codecomboxfield", codesetid: "UM", onlySelectCodeset: false,
                    ctrltype: "3", nmodule: "1", multiple: true
                };
                organizationColumn.setEditor(eidtior);
            }
    	});
    	StandardList.gridPanel.on('beforeedit', function (editor, context, eOpts) {
            if (return_priv[context.record.data.id] == "0") {
                return false;
            }
            if(return_priv["reName"]=="0"&&context.column.dataIndex=="name"){//控制名称列是否可编辑
				return false;
			}
			if(return_priv["isEdit"]=="0"&&context.column.dataIndex=="b0110"){//控制组织机构列是否可以编辑
				return false;
			}
            if (context.value) {
                if (context.field == 'b0110' && context.value.split('`').length < 2) {

                    context.value = context.record.data.b0110 + "`" + context.record.data.orgdesc;
                }
            }
			return true;
		});
    },
    //删除薪资标准表
    deleteStandList:function(){
		//获取选中记录
   	    var selectedStandList = StandardList.gridPanel.getSelectionModel().getSelection();
   	    for(var i = 0;i<selectedStandList.length;i++){
			if(return_priv[selectedStandList[i].data.id]=="0"){
				Ext.Msg.alert(gz.standard.tip,gz.standard.deleteFailByPriv);
				return;
			}
		}
   	    //未选中记录
   	    if (selectedStandList.length <= 0) {
   	    	Ext.Msg.alert(gz.standard.tip,gz.label.delNoRecord);
   	    //选中记录
   	    } else {
   	    	Ext.Msg.confirm(gz.standard.tip, gz.standard.sd.isSureDeleteSelectList, function (btnId) {
			       	if (btnId == "yes") {
						var delMap = new HashMap();
						var delId = "";
						var delPkg_Id = StandardList.getExparam();
						delMap.put("operate_type", "deleteStandList");
						if(selectedStandList.length == 1){
							delId = selectedStandList[0].data.id_e_e;
						} else {
							for(var i = 0;i < selectedStandList.length;i++){
									delId += (selectedStandList[i].data.id_e_e+",");
							}
							delId = delId.substring(0,delId.length-1);
						}
						delMap.put("id", delId);
						delMap.put("pkg_id", delPkg_Id);
						Rpc({functionId: 'GZ00001218', success: function(form,action){
							var result = Ext.decode(form.responseText);
					        var return_code = result.return_code;//运行是否成功的标识
					        var return_msg = result.return_msg;//错误信息
					        if(return_code == "fail"){
					        	var tip  =return_msg;
					        	if(result.noDelStand){
					        		tip = '编号为('+Ext.util.Format.substr(result.noDelStand,0,result.noDelStand.length-1)+')的标准表在薪资计算公式中引用不能删除！';
								}
					        	Ext.Msg.alert(gz.standard.tip,tip);
					        } 
							StandardList.gridPanel.getStore().load();//重新刷新store
						}}, delMap);
					}
				});
   	    }
    	
    },
	/**
	 * 标准表列表导出Excel
	 * */
	exportStandardListExcel:function () {
		//获取选中记录
		var selectedStandList = StandardList.gridPanel.getSelectionModel().getSelection();
		if(selectedStandList.length<1){
			Ext.Msg.alert(gz.standard.tip,gz.standard.tipValue);
			return;
		}
		var stand_ids = "";
		var map = new HashMap();
		for(var i = 0; i<selectedStandList.length;i++){
			stand_ids+=selectedStandList[i].data.id_e_e+",";
		}
		stand_ids=stand_ids.substring(0,stand_ids.length-1);
		map.put("stand_ids",stand_ids);
		map.put("pkg_id",StandardList.getExparam());
		Rpc({functionId: 'GZ00001216', success: function(form){
			var result = Ext.decode(form.responseText);
			var return_code = result.return_code;
			if(return_code == "fail"){
				Ext.Msg.alert(gz.standard.tip,result.return_msg);
				return;
			}
			var return_data = result.return_data;
			var file_Name = return_data.file_Name;
			window.open("/servlet/vfsservlet?fileid=" + file_Name + "&fromjavafolder=true");
		}}, map);
	},
	/**
	 * 新增标准表
	 */
	addStandardFunc:function () {
		Ext.create('Standard.StandardStructure',{
			afterEnterFunc:StandardList.afterEnterFunc,
			viewType:'create'
		});
	},
	afterEnterFunc:function (standStructInfor) {
		Ext.getCmp('StandardList_mainPanel').setHidden(true);
		if(Standard.pkg_id){
			StandardList.remove(Standard);
		}
		var standard = Ext.create('Standard.Standard',{
			pkg_id:StandardList.getExparam(),
			stand_id:"",
			viewType:'create',
			standStructInfor:standStructInfor
		});
		StandardList.add(standard);
	}
});
