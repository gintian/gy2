/**
 * 职责&岗位说明配置
 * sheny 2020-5-7
 */
Ext.define('Instructions.Instructions', {
    extend: 'Ext.panel.Panel',
    layout: 'vbox',
    border: false,
    bodyStyle:'overflow-x:auto',
    initComponent: function () {
        Instructions = this;
        this.callParent();
        this.loadInstructions();
    },
    createTip : function(){
        var tip = Ext.create('Ext.panel.Panel', {
            height:37,
            width: '100%',
            border:false,
            bodyStyle:'padding:10px;font-size:14px;border-width: 0 0 1px 0;border-style: solid;',
            layout:'hbox',
            items:[{
                xtype: 'label',
                flex: 30,
                text:instructions.title
            },{
                xtype: 'label',
                flex: 1,
                html: '<a href="javascript:Instructions.saveInstructions();" style="color:#1b4a98;font-size:13px;position: fixed;right: 15px;">'+instructions.sava+'</a> ',
            }]
        })
        Instructions.add(tip);
    },
    createInstructionPanel:function (fieldLabel,store,src,id,text) {
        var styleHtml = "style='cursor:pointer;height:25px;' ";
        var clickHtml = "onclick='Instructions.accessoryImg("+id+")'";
        var widthView = Instructions.body.lastBox.width-5;
        var instructionPanel = Ext.create('Ext.form.Panel', {
            layout: 'hbox',
            height:50,
            width:widthView,
            bodyStyle:id=='department'?'padding:10px 0 0 40px;border-width: 1px 0 0 0;border-style: solid;':'padding:10px 0 0 40px;border-width: 0 0 0 0;',
            id:id,
            margin: '0 0 10 0',
            items:[{
                xtype: 'combobox',
                itemId:'instruCombo',
                fieldLabel: fieldLabel,
                width:350,
                height:25,
                store:store,
                queryMode: 'local',
            }, {
                xtype: 'label',
                margin:'3 0 0 20',
                text:text
            },{
                xtype: 'component',
                itemId:'accessorySrc',
                margin:'0 0 0 10',
                html:'<img src=\''+src+'\'' + styleHtml + clickHtml + '/>'
            }]
        });
        Instructions.add(instructionPanel);
    },
    loadInstructions: function () {
        var param = new HashMap();
        Rpc({
            functionId: 'ZJ100000401',
            success: Instructions.initInstructionsData,
            scope: Instructions
        }, param);
    },
    initInstructionsData:function(req){
        var request = Ext.decode(req.responseText);
        var instructionsData = request.return_data;
        var return_code = request.return_code;
        if(return_code==="success"){
            //部门职责说明书是否显示附件
            Instructions.departmentAccessoryFlag = instructionsData.DepartmentAccessoryFlag;
            //岗位职责说明书是否显示附件
            Instructions.positionAccessoryFlag = instructionsData.PositionAccessoryFlag;
            //基准岗位说明书是否显示附件
            Instructions.standardAccessoryFlag = instructionsData.StandardAccessoryFlag;
            //部门职责说明书显示模板
            var departmentValue = instructionsData.DepartmentValue;
            //岗位职责说明书显示模板
            var positionValue = instructionsData.PositionValue;
            //基准岗位说明书显示模板
            var standardValue = instructionsData.StandardValue;
            //部门职责说明书选择模板
            var departmentData = instructionsData.DepartmentData;
            //岗位职责说明书选择模板
            var positionData = instructionsData.PositionData;
            //基准岗位说明书选择模板
            var standardData = instructionsData.StandardData;
            var positionSrc = "./images/close.png";
            var standardSrc = "./images/close.png";
            if (Instructions.positionAccessoryFlag==="true"){
                positionSrc = "./images/open.png";
            }
            if (Instructions.standardAccessoryFlag==="true"){
                standardSrc = "./images/open.png";
            }
            Instructions.createTip();
            Instructions.createInstructionPanel(instructions.department,departmentData,"","department","");
            Instructions.createInstructionPanel(instructions.position,positionData,positionSrc,"position",instructions.accessory);
            Instructions.createInstructionPanel(instructions.standard,standardData,standardSrc,"standard",instructions.accessory);
            if(departmentValue===""){
                departmentValue = instructions.choose;
            }
            if(positionValue===""){
                positionValue = instructions.choose;
            }
            if(standardValue===""){
                standardValue = instructions.choose;
            }
            Ext.getCmp("department").query('#instruCombo')[0].setValue(departmentValue);
            Ext.getCmp("position").query('#instruCombo')[0].setValue(positionValue);
            Ext.getCmp("standard").query('#instruCombo')[0].setValue(standardValue);
            Ext.getCmp("department").query('#instruCombo')[0].getStore().insert(0,{"field1":instructions.choose});
            Ext.getCmp("position").query('#instruCombo')[0].getStore().insert(0,{"field1":instructions.choose});
            Ext.getCmp("standard").query('#instruCombo')[0].getStore().insert(0,{"field1":instructions.choose});
        } else {
            Ext.Msg.alert(instructions.tip,instructions.faildInit);
        }

    },
    accessoryImg:function (idName) {
        var id = idName.id;
        var accessoryFlag = "";
        if(id==="position"){
            accessoryFlag = Instructions.positionAccessoryFlag;
        } else {
            accessoryFlag = Instructions.standardAccessoryFlag;
        }
        var styleHtml = "style='cursor:pointer;height:25px;' ";
        var clickHtml = "onclick='Instructions.accessoryImg("+id+")'";
        if (accessoryFlag==="true"){
            if(id==="position"){
                Instructions.positionAccessoryFlag = "false";
            } else {
                Instructions.standardAccessoryFlag = "false";
            }
            Ext.getCmp(id).query('#accessorySrc')[0].update("<img src=./images/close.png " + styleHtml + clickHtml + "/>");
        } else {
            if(id==="position"){
                Instructions.positionAccessoryFlag = "true";
            } else {
                Instructions.standardAccessoryFlag = "true";
            }
            Ext.getCmp(id).query('#accessorySrc')[0].update("<img src=./images/open.png " + styleHtml + clickHtml + "/>");
        }
    },
    saveInstructions:function(){
        var param = new HashMap();
        var departmentValue = Ext.getCmp("department").query('#instruCombo')[0].getValue();
        var positionValue = Ext.getCmp("position").query('#instruCombo')[0].getValue();
        var standardValue = Ext.getCmp("standard").query('#instruCombo')[0].getValue();

        param.put("positionAccessoryFlag",Instructions.positionAccessoryFlag);
        param.put("standardAccessoryFlag",Instructions.standardAccessoryFlag);
        param.put("departmentValue",departmentValue);
        param.put("positionValue",positionValue);
        param.put("standardValue",standardValue);
        Rpc({
            functionId: 'ZJ100000402',
            success: function(req){
                var request = Ext.decode(req.responseText);
                var return_code = request.return_code
                if(return_code==="success"){
                    Ext.Msg.alert(instructions.tip,instructions.successSave);
                } else {
                    Ext.Msg.alert(instructions.tip,instructions.faildSave);
                }

            },
            scope: Instructions
        }, param);
    }
})