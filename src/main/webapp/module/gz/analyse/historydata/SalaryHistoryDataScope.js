/**
 * 薪资历史数据范围页面（归档&还原&删除功能）
 * 2020-1-13
 */
Ext.define('GzAnalyse.historydata.SalaryHistoryDataScope', {
    extend: 'Ext.window.Window',
    height: 170,
    width: 410,
    resizable: false, //是否可以改变大小
    constrain: true, //不能移出窗口之外
    modal: true, //模态窗口
    layout: 'fit',
	initComponent: function () {
		this.callParent();
		this.loadComp();//加载组件
	},
	loadComp: function () {
		var me = this;
		var datePanel = Ext.create('Ext.panel.Panel', {
			border: false,
			margin: '53 0 0 10',
			layout: {
				type: 'hbox',
				align: 'center'
			},
			hidden: true,
			items: [{
				xtype: 'component',
				margin: '0 10 0 0',
				html: gz.historyData.from//从
			}, {
				xtype: 'datefield',
				itemId: 'start',
				width: 105,
				format: 'Y-m-d',
				editable: false
			}, {
				xtype: 'component',
				margin: '0 10 0 10',
				html: gz.historyData.to//至
			}, {
				xtype: 'datefield',
				itemId: 'end',
				width: 105,
				format: 'Y-m-d',
				editable: false
			}]
		});
		var mainPanel = Ext.create("Ext.Panel", {
			border: false,
			layout: 'hbox',
			items: [{
				xtype: 'radiogroup',//单选组件
				margin: '25 0 0 25',
				layout: 'vbox',
				items: [{
					boxLabel: gz.historyData.all,//'全部'
					name: 'kind',
					inputValue: 'all',
					itemId: 'all',
					checked: true
				}, {
					boxLabel: gz.historyData.timeLimit,//'时间范围'
					name: 'kind',
					inputValue: 'other',
					itemId: 'other',
				}],
				listeners: {
					change: function () {
						if (datePanel.isHidden()) {
							datePanel.show();
						} else {
							datePanel.hide();
						}
					}
				}
			}, datePanel],
			buttonAlign: 'center',
			buttons: [{
				text: gz.historyData.confirm,//'确定'
				handler: function () {
					var allFlag = mainPanel.query("#all")[0].getValue();
					var start = datePanel.query("#start")[0].getValue();
					var end = datePanel.query("#end")[0].getValue();
					me.hint(allFlag, start, end);
				}
			}, {
				text: gz.historyData.cancel,//'取消'
				handler: function () {
					me.close();
				}
			}]
		});
		me.add(mainPanel);
	},
	/**
	 * 进行时间规范判断提示及还原数据结构判断提示
	 */
	hint: function (allFlag, start, end) {
		var me = this;
		var type = '0';//全部
		if (!allFlag) {
			if (!start) {
				Ext.Msg.alert(gz.historyData.hintmsg, gz.historyData.nostart);
				return;
			} else if (!end) {
				Ext.Msg.alert(gz.historyData.hintmsg, gz.historyData.noend);
				return;
			} else if (end < start) {
				Ext.Msg.alert(gz.historyData.hintmsg, gz.historyData.start_big_end);
				return;
			}
			type = '1';//时间范围
		}
		start = Ext.Date.format(start, 'Y-m-d');
		end = Ext.Date.format(end, 'Y-m-d');
		var title = this.getTitle();
		var param = new HashMap();
		param.put("type", type);
		param.put("startDate", start);
		param.put("endDate", end);
		param.put("salaryId", SalaryHistoryData.salaryId);
		if (title == gz.historyData.revert_title) {
			me.strutsIsChange(title, param);
			return;
		}
		if (title == gz.historyData.archive_title) {
			Ext.Msg.confirm(gz.historyData.hintmsg, gz.historyData.confirm + title + '？', function (btn) {
				if (btn == 'yes') {
					me.save(title, param);
				}
			});
			return;
		}
		if(title == gz.historyData.delete_title){
			Ext.Msg.confirm(gz.historyData.hintmsg,gz.historyData.delete_hint,function (btn) {
				if(btn == 'yes'){
					Ext.Msg.confirm(gz.historyData.hintmsg, gz.historyData.confirm + title + '？', function (btn) {
						if (btn == 'yes') {
							me.save(title, param);
						}
					})
				}
			})
			return;
		}
	},
    /**
     * 判断数据结构是否改变
     */
    strutsIsChange:function(title,param){
    	var me=this;
    	param.put("strut",'0');
    	Rpc({
            functionId: 'GZ00001306',
            success: function(result) {
                // 获取参数
                var para = Ext.decode(result.responseText).returnStr;
                if(para.return_data.msg=='1'){
                	Ext.Msg.confirm(gz.historyData.hintmsg,gz.historyData.change_struct,function(btn){
                		if(btn=='yes'){
                			param.put("strut",'1');
                    		me.save(title,param);
                		}else{
                			return;
                		}
                	});
                }else{
                	Ext.Msg.confirm(gz.historyData.hintmsg, gz.historyData.confirm + title + '？',function(btn){
                		if(btn=='yes'){
                			param.put("strut",'1');
                        	me.save(title,param);
                		}else{
                			return;
                		}
                	});
                }
            },
            scope: this
        }, param);
    },
    /**
     * 进行数据归档/删除/还原
     */
    save:function(title,param){
    	var me=this;
    	if(title==gz.historyData.archive_title){
    		var functionId='GZ00001305';
    	}else if(title==gz.historyData.revert_title){
    		var functionId='GZ00001306';
    	}else if(title==gz.historyData.delete_title){
    		var functionId='GZ00001307';
    	}
		var wait = Ext.Msg.wait(gz.historyData.standing + title + '...');
    	Rpc({
            functionId: functionId,
            success: function(result) {
                // 获取参数
                var para = Ext.decode(result.responseText).returnStr;
                if(para.return_code=='success'){
                	wait.close();
                    me.close();
                    var return_data=para.return_data;
                    if(return_data && return_data.fileName){
                    	var fileName = getDecodeStr(return_data.fileName);
                    	var win=open("/servlet/vfsservlet?fileid="+fileName+"&fromjavafolder=true","excel");
                    }
                    //调用方法刷新表格
					SalaryHistoryData.loadData();
                }else{
                	Ext.Msg.alert(gz.historyData.tip,eval(para.return_msg));
                }
            },
            scope: this
        }, param);
    }
});