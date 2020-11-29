Ext.define("ServiceClient.serviceHome.FirstModifyPassword",{
    extend:'Ext.window.Window',
    requires:["ServiceClient.serviceHome.ServiceHome"],
    title:'<div style="height:30px;font-size:17px;font-weight:400;margin-top:6px;margin-left:4px">'+sc.home.modifyPassWord+'</div>',//修改密码
    id:'firstModifyPassword',
    style:'border-radius:7px 7px 7px 7px',
    closable:false,
    modal:true,
    resizable:false,
    draggable : false,
    width:435,
    height:250,
    tools:[{
        xtype:'image',src:'/module/serviceclient/images/index/winClose.png',height:16,width:16,
        style:'cursor:pointer;margin-right:7px',
        listeners:{
            element:'el',
            click:function(){
                VirtualKeyboard.close();
                Ext.getCmp("firstModifyPassword").close();
            },
            scope:this
        }
    }],
    constructor:function(config){
        var me = this;
        Ext.apply(me,config);
        if(me.logonHeight){
            me.y = me.logonHeight;
        }
        if(me.accessType=="modifyPassword"){
            me.setHeight(310);
        }
        me.callParent();
        me.init();
    },
    init:function(){
        var form = this.getMainPanel();
        this.add(form);
    },
    getMainPanel:function(){
        var me = this;
        Ext.util.CSS.createStyleSheet('.radius{border-radius:0px 6px 6px 0px;border-top:1px   #c5c5c5 solid;border-right:1px   #c5c5c5 solid;border-bottom:1px   #c5c5c5 solid;} ');
        var mainPanel = Ext.create("Ext.form.Panel",{
            border:false,
            width:'100%',
            height:'100%',
            items:[{
                xtype:'panel',
                hidden:me.accessType=="firstLogin"? true:false,
                margin:'18 0 0 18',
                border:false,
                layout:{
                    type:'hbox',
                    align:'center'
                },
                items:[{
                    xtype:'image',
                    src:'/module/serviceclient/images/index/password.png',
                    width:42,
                    height:42
                },{
                    xtype : 'textfield',
                    name : 'oldPW',
                    id:'oldPW',
                    emptyText:sc.home.writeOldPw,//请输入原密码
                    inputType:'password',
                    inputWrapCls:Ext.baseCSSPrefix + 'form-text-wrap radius',
                    fieldStyle:'font-size:15px',
                    width :349,
                    height:42,
                    listeners:{
                        focus:function(e){
                            VirtualKeyboard.toggle('oldPW-inputEl', 'softkey');
                            $("#kb_langselector,#kb_mappingselector,#copyrights").css("display", "none");
                            e.emptyText = "";
                            VirtualKeyboard.switchLayout("US US");
                        },
                        blur:function(){
                            VirtualKeyboard.close();
                        }
                    }
                }]
            },{
                xtype:'panel',
                margin:'18 0 0 18',
                border:false,
                layout:{
                    type:'hbox',
                    align:'center'
                },
                items:[{
                    xtype:'image',
                    src:'/module/serviceclient/images/index/password.png',
                    width:42,
                    height:42
                },{
                    xtype : 'textfield',
                    name : 'newPW',
                    id:'newPW',
                    emptyText :sc.home.writeNewPw,//请输入新密码
                    inputType:'password',
                    inputWrapCls:Ext.baseCSSPrefix + 'form-text-wrap radius',
                    fieldStyle:'font-size:15px',
                    width :349,
                    height:42,
                    validator:function(value){
                        var finallyFlag = me.modifyPwValidator(value);
                        if(finallyFlag==true){
                            Ext.getCmp('newPW').config.flag = true;
                        }
                        return finallyFlag;
                    },
                    listeners:{
                        focus:function(e){
                            VirtualKeyboard.toggle('newPW-inputEl', 'softkey');
                            $("#kb_langselector,#kb_mappingselector,#copyrights").css("display", "none");
                            e.emptyText = "";
                            VirtualKeyboard.switchLayout("US US");
                        },
                        blur:function(){
                            VirtualKeyboard.close();
                        }
                    }
                }]
            },{
                xtype:'panel',
                margin:'18 0 0 18',
                border:false,
                layout:{
                    type:'hbox',
                    align:'center'
                },
                items:[{
                    xtype:'image',
                    src:'/module/serviceclient/images/index/password.png',
                    width:42,
                    height:42
                },{
                    xtype : 'textfield',
                    name : 'confirmNewPW',
                    id:'confirmNewPW',
                    emptyText :sc.home.writeConfirmPw,//请确认新密码
                    inputType:'password',
                    inputWrapCls:Ext.baseCSSPrefix + 'form-text-wrap radius',
                    fieldStyle:'font-size:15px',
                    width :349,
                    height:42,
                    listeners:{
                        focus:function(e){
                            VirtualKeyboard.toggle('confirmNewPW-inputEl', 'softkey');
                            $("#kb_langselector,#kb_mappingselector,#copyrights").css("display", "none");
                            e.emptyText = "";
                            VirtualKeyboard.switchLayout("US US");
                        },
                        blur:function(){
                            VirtualKeyboard.close();
                        }
                    }
                }]
            },{
                xtype:'button',//保存按钮
                height:42,
                width:110,
                margin:'18 0 0 160',
                style:'backgroundColor:#00A2FF;',
                html:'<div style="color:white;font-weight:400;font-size:18px;height:40px;padding-top:12px">'+sc.setting.save+'</div>',
                listeners:{
                    click:function(btn){
                        VirtualKeyboard.close();
                        btn.setDisabled(true);
                        if(me.accessType=="firstLogin"){
                            me.firstLoginSaveBtnClick(mainPanel,btn);
                        }else{
                            me.modifyPassswordSaveBtnClick(mainPanel,btn);
                        }
                    }
                }
            }]
        });
        return mainPanel;
    },
    /**
     * 首次登录保存
     * @param {} mainPanel formPanel
     * @param {} btn 按钮
     */
    firstLoginSaveBtnClick:function(mainPanel,btn){
        var me = this;
        var returnFlag = false;
        var values = mainPanel.getValues();
        var newPw = values['newPW'];
        var confirmNewPw = values['confirmNewPW'];
        if(newPw!=confirmNewPw){
            Ext.Msg.alert(sc.home.promptmessage,sc.home.newPwDifferent);//确认密码与新密码不一致
            btn.setDisabled(false);
            return;
        }
        var vo = new HashMap();
        vo.put("newPassword",newPw);
        vo.put("newokpwd",confirmNewPw);
        vo.put("accessType","firstLogin");
        vo.put("transType","saveNewPassword");
        Rpc({functionId:'SC000000001',async:false,success:function(res){
            var info = Ext.decode(res.responseText);
            var promptmessage = "";//alert的信息
            var errorFlag = info.errorFlag;
            if(errorFlag=="sameAsLast"){
                promptmessage = sc.home.newPwStopSameAsOldPw;//新密码不能与原密码相同
                returnFlag = true;
            }else if(errorFlag=="passwordDifferent"){
                promptmessage = sc.home.newPwDifferent;//确认密码与新密码不一致
                returnFlag = true;
            }else if(errorFlag.indexOf("sameAsHistoryPwd")>-1){
                var historyIndex = errorFlag.subString(16);
                promptmessage = sc.home.newPwStopSameAsHistoryPw.replace("{0}",historyIndex);//新密码不能与前{0}次密码相同!
                returnFlag = true;
            }else{
                var map = new HashMap();
                map.put('transType','serve');
                map.put("ip",me.ip);
                Rpc({functionId:'SC000000001',async:false,success:function(res){
                    var info = Ext.decode(res.responseText);
                    var servicesData = info.serviceData;
                    Ext.getCmp("firstModifyPassword").close();
                    //定时
                    ServiceClientSecurity.start();
                    Ext.getDom("banner").style.display="none";
                    Ext.widget('viewport',{
                        layout:'fit',
                        items:Ext.create("ServiceClient.serviceHome.ServiceHome",{
                            servicesData:servicesData,
                            ip:me.ip
                        })
                    });
                }},map);
            }
            if(returnFlag){
                Ext.Msg.alert(sc.home.promptmessage,promptmessage)
                btn.setDisabled(false);
                return;
            }
        }},vo);
    },
    /**
     * 系统内密码修改保存事件
     * @param {} mainPanel formPanel
     * @param {} btn  按钮
     */
    modifyPassswordSaveBtnClick:function(mainPanel,btn){
        var me = this;
        var values = mainPanel.getValues();
        var oldPw = values['oldPW'];
        var newPw = values['newPW'];
        var confirmNewPw = values['confirmNewPW'];
        if(newPw!=confirmNewPw){
            Ext.Msg.alert(sc.home.promptmessage,sc.home.newPwDifferent);//确认密码与新密码不一致
            btn.setDisabled(false);
            return;
        }
        var vo = new HashMap();
        vo.put("oldPw",oldPw);
        vo.put("newPassword",newPw);
        vo.put("newokpwd",confirmNewPw);
        vo.put("transType","saveNewPassword");
        vo.put("accessType","modifyPassword");
        Rpc({functionId:'SC000000001',async:false,success:function(res){
            var info = Ext.decode(res.responseText);
            var errorFlag = info.errorFlag;
            if(errorFlag=="oldPwdError"){
                Ext.Msg.alert(sc.home.promptmessage,sc.home.oldPwdError);//原密码不正确
                btn.setDisabled(false);
                return;
            }else{
                me.close();
                Ext.Msg.alert(sc.home.promptmessage,sc.home.reLoginSystem,function(){//密码修改成功 请重新登录系统
                    window.location.reload();
                });
            }
        }},vo);
    },
    /**
     * 首次密码修改验证方式
     */
    modifyPwValidator:function(value){
        var me = this;
        var finallyFlag;
        var word = value.split('');
        var numChecker = '1234567890';
        var wordChecker = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
        var charCheker = '%$#@!~^&*()+"\',';
        
        if(me.passwordrule=="2"){
            var commonPwRuleFlag = me.commonPwRule(word,numChecker,wordChecker,charCheker);
            if(commonPwRuleFlag==true){
                var mediumAbovePwRuleFlag = me.mediumAbovePwRule(word,numChecker,wordChecker);
                if(mediumAbovePwRuleFlag==true){
                    var maxPwRuleFlag = me.maxPwRule(word,charCheker);
                    finallyFlag = maxPwRuleFlag;
                }else{
                    finallyFlag = mediumAbovePwRuleFlag;
                }
            }else{
                finallyFlag = commonPwRuleFlag;
            }
        }else if(me.passwordrule=="1"){
            var commonPwRuleFlag = me.commonPwRule(word,numChecker,wordChecker,charCheker);
            if(commonPwRuleFlag==true){
                var mediumAbovePwRuleFlag = me.mediumAbovePwRule(word,numChecker,wordChecker);
                finallyFlag = mediumAbovePwRuleFlag;
            }else{
                finallyFlag = commonPwRuleFlag;
            }
        }else{
            var commonPwRuleFlag = me.commonPwRule(word,numChecker,wordChecker,charCheker);
            finallyFlag = commonPwRuleFlag;
        }
        return finallyFlag;
    },
    /**
     * 判断不允许的符号和空格
     * @param {} word 文本框值的分割
     * @param {} numChecker 密码可含的数字
     * @param {} wordChecker 密码可含的字母
     * @param {} charCheker 密码可含的符号
     * @return {} commonPwRuleFlag 是否符合普遍规范
     */
    commonPwRule:function(word,numChecker,wordChecker,charCheker){
        var commonPwRuleFlag;
        for(var i=0;i<word.length;i++){
            if(word[i] == " "){
                Ext.getCmp('newPW').config.flag = false;
                commonPwRuleFlag = sc.home.stopWriteSpace;//请不要输入空格
                break;
            }else if(numChecker.indexOf(word[i])>-1 || wordChecker.indexOf(word[i])>-1){
                commonPwRuleFlag = true;
            }else if(charCheker.indexOf(word[i])==-1){
                commonPwRuleFlag = sc.home.stopNotAllowSymbol;//不允许有  %$#@!~^&*()+"\', 之外的符号
                break;
            }
        }
        if(!commonPwRuleFlag){
            commonPwRuleFlag = true;
        }
        return commonPwRuleFlag;
    },
    /**
     * 校验密码长度以及必须含有字母和数字
     * @param {} word 文本框值的分割
     * @param {} numChecker 密码可含的数字
     * @param {} wordChecker 密码可含的字母
     * @return {} mediumAbovePwRuleFlag 是否符合中度规范
     */
    mediumAbovePwRule:function(word,numChecker,wordChecker){
        var me = this;
        var mediumAbovePwRuleFlag;
        if(word.length<me.passwordlength){
            mediumAbovePwRuleFlag = sc.home.minPwLength+me.passwordlength+sc.home.bit;//密码长度应不少于 ？  位
            return mediumAbovePwRuleFlag;
        }
        for(var i=0;i<word.length;i++){
            if(numChecker.indexOf(word[i])>-1){
                var numCheckerFlag = true;
            }else if(wordChecker.indexOf(word[i])>-1){
                var wordCheckerFlag = true;
            }
        }
        if(numCheckerFlag&&wordCheckerFlag){
            mediumAbovePwRuleFlag = true;
        }else{
            mediumAbovePwRuleFlag = sc.home.mustContainsNumAndWord;//密码必须包含字母和数字
        }
        return mediumAbovePwRuleFlag;
    },
    /**
     * 判断是否含符号且字符不重复
     * @param {} word 文本框值的分割
     * @param {} charCheker 密码可含的符号
     * @return {} maxPwRuleFlag 是否符合最高规范
     */
    maxPwRule:function(word,charCheker){
        var maxPwRuleFlag;
        var tempStr = "";//存放已经判断过的文本框值
        for(var i=0;i<word.length;i++){
            if(i>0 && tempStr.indexOf(word[i])>-1){
                maxPwRuleFlag = sc.home.stopRepeat;//密码中不能有重复的字符
                return maxPwRuleFlag;
            }
            tempStr = tempStr+word[i];
            if(charCheker.indexOf(word[i])>-1){
                maxPwRuleFlag = true;
            }
        }
        if(!maxPwRuleFlag){
            maxPwRuleFlag = sc.home.mustContainsChar;//密码中必须包含符号
        }
        return maxPwRuleFlag;
    }
});