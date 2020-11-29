/**
  * SelectSpecialRoleUser.js
  * 如果特殊角色有多个，需弹出界面，供报批人员选择，只能选择一个。
  * 
  */
Ext.define('TemplateApplyUL.SelectSpecialRoleUser', {
    tab_id: '',
    templPropety: "",
    specialNodeList: "",
    callBackFunc:"",
    constructor: function(config) { //构造方法
        selectSpecialRoleUser_me = this;
        this.templPropety = config.templPropety;
        this.specialNodeList = config.specialNodeList;
        var specialUserList = config.specialUserList;
        this.callBackFunc=config.callBackFunc;
        //构造单元组数组
        var nodeList = new Array();
        for (var i = 0; i < this.specialNodeList.length; i++) {
            if (i>0){//中间加条横线
	            var panelObj = new Object();
	            panelObj.border = true;
	            panelObj.bodyStyle ="border-left-style:none;border-bootom-style:none;border-right-style:none;margin-top:10px";
	            panelObj.xtype = 'panel';
	            panelObj.width=400;
	            panelObj.height=20;
	            nodeList.push(panelObj);
            }
            
            var node = this.specialNodeList[i];
            var nodeObj = new Object();
            nodeObj.id = "radio_" + node.node_id;
            nodeObj.fieldLabel = node.node_name;
            nodeObj.labelSeparator = '：';
            //29315 fieldLabel靠右，boxLabel靠左，
            nodeObj.labelAlign = 'right';
            nodeObj.columns = 1;
            nodeObj.xtype = 'radiogroup';
            nodeObj.labelWidth=(node.node_name.length+nodeObj.labelSeparator.length)*15>100?100:(node.node_name.length+nodeObj.labelSeparator.length)*15;//liuyz bug32393 原来走默认值100，字少时label左侧空白太多。
            var userList = new Array();
            var bcheck = true; //默认选中第一个上级
            for (var j = 0; j < specialUserList.length; j++) {
                var user = specialUserList[j];
                if (user.node_id != node.node_id) { //不是当前角色下的人员
                    continue
                }
                var userObj = new Object();
                userObj.labelSeparator = '';
                userObj.labelAlign = 'left';
                //userObj.name ="R_"+node.node_id;
                userObj.inputValue = user.userId + "`" + user.a0101;
                userObj.boxLabel = user.displayName;
                userObj.checked = bcheck;
                userList.push(userObj);
                bcheck = false; //
            }
            nodeObj.items = userList;
            nodeList.push(nodeObj);
        }
        this.initForm(nodeList);
    },
    /**
     * 初始化界面
     * nodeList：单选面板及按钮集合
     */
    initForm: function(nodeList) {
        var win = Ext.create('Ext.window.Window', {
            title: '选择审批用户',
            width: 430,
            height: 300,
            resizable: false,
            closeAction:'destroy',
            modal: true,
            border: false,
            items: [{
                xtype: 'panel',
                layout: 'column',
                border: false,
                style: 'margin-left:5px;margin-top:3px',
                items: nodeList
            }],
            bbar: [
                   {xtype: 'tbfill'},
		           {
		                text: common.button.ok,
		                listeners: {
		                    'click': function() {
		                        var userIds = "";
		                        for (var i = 0; i < selectSpecialRoleUser_me.specialNodeList.length; i++) {
		                            var node = selectSpecialRoleUser_me.specialNodeList[i];
		                            var radioId = "radio_" + node.node_id;
		                            var userId = Ext.getCmp(radioId).getChecked()[0].inputValue;
		                            if (userIds != "") userIds = userIds + ",";
		                            userIds = userIds+node.node_id + ":" + userId;
		                        }
		                        Ext.callback(eval(selectSpecialRoleUser_me.callBackFunc),null,[userIds]);
		                        win.close();
		                    }
		                }
		            },
		            {
		                text: common.button.cancel,
		                style: 'margin-left:5px',
		                listeners: {
		                    'click': function() {
		                        win.close();
		                    }
		                }
		            },
		            {xtype: 'tbfill'}
            ]

        });
        win.show();
    }
});