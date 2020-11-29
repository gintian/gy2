/**
 * 退回窗口
 */
Ext.define('KqDataURL.KqDataRejectWindow', {
    constructor: function (config) {
        KqDataReject = this;
        KqDataReject.scheme_id = config.scheme_id;
        KqDataReject.org_id = config.org_id;
        KqDataReject.rejectFunction = config.rejectFunction;
        KqDataReject.viewType = config.viewType;
        KqDataReject.userid = "";
        KqDataReject.role_id = "";
        var jsonStr = {
            type: 'rejectpersonnel',
            scheme_id: config.scheme_id,
            org_id: config.org_id,
            viewType: config.viewType
        };
        var map = new HashMap();
        map.put("jsonStr", JSON.stringify(jsonStr));
        Rpc({
            functionId: 'KQ00021201', success: KqDataReject.init
        }, map);

    },
    
    init: function (form) {
        var result = Ext.decode(form.responseText);
        var returnStr = eval(result.returnStr);
        var return_data=returnStr.return_data;
        
        if(return_data.datalist.length==1){
            var name=return_data.datalist[0].name;
            Ext.showConfirm(kq.msg.isBackToSomeOne.replace('{0}',name), function (value) {
                if (value != "yes")
                    return;
                else{
                    Ext.callback(KqDataReject.rejectFunction, null, [KqDataReject.org_id, return_data.datalist[0].userid, return_data.datalist[0].role_id]);
                }
            });
            return;
        }
        // 修改ext css样式.x-form-cb-label-default margin-top: 4px
    	Ext.util.CSS.updateRule(".x-form-cb-label-default","margin-top","2px");
        // 46898 退回人员数据遍历 改为radio选项
        var radios = [];
        Ext.each(return_data.datalist, function (record) {
        	radios.push({boxLabel: record.name, name: 'one', inputValue: record.role_id, userid:record.userid });
        });
        var radioPanel = Ext.create('Ext.form.Panel', {
            width: 180,
            height: 190,
            bodyPadding: 10,
            border: false,
            items:[{
                xtype: 'radiogroup',
                id: 'checkedUserid',
                columns: 1,
                vertical: true,
                border: false,
                items: radios
            }]
        });
        var window = Ext.widget("window", {
        	title: '<b>'+kq.label.backto+'</b>',
            height: 210,
            width: 280,
            layout: 'fit',
            align: 'stretch',
            closable:false,
            scrollable: false,
            resizable:false,
            modal: true,
            border: false,
            padding:0,
            closeAction: 'destroy',
            items: [radioPanel],
            buttons: [
                {xtype: 'tbfill'},
                {
                    text: kq.label.back,
                    width:10,
                    handler: function () {
                    	var select = Ext.getCmp('checkedUserid').getChecked()[0];
                    	if(!select){
                    		Ext.showAlert(kq.msg.selectBackUser);
                    		return;
                    	}
                        if (select.userid == "") 
                            return;
                        Ext.callback(KqDataReject.rejectFunction, null, [KqDataReject.org_id, select.userid, select.inputValue]);
                        window.close();
                    }
                },
                {
                    text: common.button.cancel,
                    width:20,
                    handler: function () {
                        window.close();
                    }
                },
                {xtype: 'tbfill'}
            ]
        });
        window.show();
    }
});