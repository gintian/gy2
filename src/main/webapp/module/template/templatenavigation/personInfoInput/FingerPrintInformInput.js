// var BestFacePath = 'D:/Tomcat/Tomcat-9.0.22/webapps/ehr/module/template/templatenavigation/personInfoInput/images/best_face.jpg';
var markUrl = '../../../../module/template/templatenavigation/personInfoInput/images/mark.png';
var chahaoUrl = '../../../../module/template/templatenavigation/personInfoInput/images/chahao.png';
var cloudwalkobj; //document.createElement("object");
var width=640;	//640  400  320
var height=480;	//宽高比为4：3
var g_iCamera = 0; //要打开的摄像头索引。-- jinfude add。2018-08-14
var rotatemode = 0;	//0正常、1顺时针90度、2逆时针90度。3-已经工作在镜子模式的摄像头
var CW_nLiveNum = 0; //活体检测次数
var CW_nLiveLevel = 1;	//活体难度
var n_LiveTime = 0;
var n_LiveOptionNum = 6; //常量：可检测的动作个数有 6 个。
var g_optarr = [0,0,0,0,0,0]; //标记每个动作是否已经使用过
var strLicense = "MDQzOTA5bm9kZXZpY2Vjd2F1dGhvcml6ZZXn5+bn5+bq/+bg5efm4+f65ubn4Obg5Yjm5uvl5ubrkeXm5uvl5uai6+Xm5uvl5uTm6+Xm5uDm1efr5+vn6+er4Ofr5+vn6/vn5+bn4uTi";
var g_bNeedHack = false; //是否需要后端防 HACK
Ext.Loader.loadScript({url:rootPath+'/module/template/templatenavigation/personInfoInput/js/jquery.js'});
Ext.Loader.loadScript({url:rootPath+'/module/template/templatenavigation/personInfoInput/js/detect.js'});
Ext.Loader.loadScript({url:rootPath+'/module/template/templatenavigation/personInfoInput/js/common.js'});
Ext.Loader.loadScript({url:rootPath+'/module/template/templatenavigation/personInfoInput/js/json2.js'});
Ext.define('FingerprintInformInputUL.FingerPrintInformInput', {
    extend:"Ext.window.Window",
    layout:'border',
    bodyStyle:'background:#FFFFFF',
    overflowY:'auto',
    overflowX:'hidden',
    border:false,
    resizable :false,
    maximized:true,
    constructor: function (config) {
        if(cloudwalkobj != undefined){
            cloudwalkobj =undefined;
        }
        FingerprintInform = this;
        FingerprintInform.tab_id = config.tab_id,//模板id
        FingerprintInform.name = '',//姓名
        FingerprintInform.jobNumber = '',//工号
        FingerprintInform.faceData = '',//人脸数据 base64编码
        FingerprintInform.faceCheckState = '0',//人脸状态  0:未录入人脸 1:已录入人脸未提交 2:已录入人脸并提交
        FingerprintInform.fingercheckState = '',//判断指纹是否全部审核
        FingerprintInform.fingercheckFHState = '0',//判断指纹是否已经最终复核 0:未最终复核 1:其他状态 2:已复核
        FingerprintInform.fingerInfo = {
            fingerInfo: "",
        },
        FingerprintInform.fingerModel= {},//指纹数据
        FingerprintInform.BestFramePath = 'C:/Users/chunyu/Desktop/ZJRL/best_frame.jpg';
        FingerprintInform.BestFacePath = 'C:/Users/chunyu/Desktop/ZJRL/best_face.jpg';
        FingerprintInform.callParent(arguments);

        FingerprintInform.init();// 初始化界面
        this.show();
    },

    listeners: {
        beforeclose:function(){
            var showStr = '';
            if(FingerprintInform.faceCheckState == '1') {//已录入人脸未提交
                showStr = '人脸数据未提交,您确认要关闭吗';
            }
            debugger;
            if (FingerprintInform.fingercheckFHState == '1') {
                showStr = '指纹数据未复核,您确认要关闭吗';
            }
            if(showStr != ''){
                if(confirm(showStr))
                {
                    return true
                }else{
                    return false
                }
           }
        }
    },
    //初始加载页面
    init: function () {
        Ext.EventManager.onWindowResize(function () {
            var width = Ext.getBody().getWidth();
            var height = Ext.getBody().getHeight();
            FingerprintInform.setWidth(width);
            FingerprintInform.setHeight(height);
        },this)
        var map=new HashMap();
        map.put("method","initData");
        map.put("tab_id",FingerprintInform.tab_id);
        Rpc({functionId:'SYS20200521',async:true,success:function(res){
            var resultObj = Ext.decode(res.responseText);
            var success = resultObj.returnStr.return_code;
            if(success == "success"){
                FingerprintInform.jobNumber = resultObj.jobNumber;
                FingerprintInform.name = resultObj.name;
                // FingerprintInform.BestFramePath = resultObj.bestPath+'best_frame.jpg';
                // FingerprintInform.BestFacePath = resultObj.bestPath+'best_face.jpg';
                var titleStr = "&nbsp;&nbsp;&nbsp;&nbsp;"+"姓名&nbsp:&nbsp"+FingerprintInform.name+"&nbsp;&nbsp;&nbsp;&nbsp"+"工号&nbsp: "+FingerprintInform.jobNumber;
                this.setTitle(titleStr);
                FingerprintInform.onPageLoad();
            }else{
                var msg = resultObj.returnStr.return_msg;
                Ext.showAlert("初始化失败!"+msg);
                return;
            }
        },scope:this},map);
        this.getFingerprint();
    },

    /**
     * 指纹人脸录入界面
     */
    getFingerprint:function(){

        var me = this;
        //指纹区域
        var zbottomPanel = Ext.create('Ext.form.Panel', {
            style: 'padding-left: 100px;padding-right: 100px;padding-top: 30px;',
            border: 0,
            items: [{
                xtype:'component',
                html:'<OBJECT CLASSID="CLSID:94793CDE-C768-449B-BE87-40147B56032D" codebase="./libFPDev_TESO.ocx"  style="height: 1px" id="ObjFinger"></OBJECT>'
            }, {
                xtype: 'panel',
                border: 0,
                defaults: {
                    style: 'margin-top:9px',
                    border: 0,
                },
                items: [{
                    xtype: 'panel',
                    layout: 'hbox',
                    items: [{
                        xtype:'component',
                        style:'margin-left:20px;margin-right:13px',
                        html:'右手拇指'
                    },{
                        xtype: 'button',
                        text: '录入',
                        width: '90px',
                        id: 'finger0',
                        iconAlign:'right',
                        style: {
                            borderRadius: '15px'
                        },
                        handler: function (btn) {
                            FingerprintInform.handleCollect(0,btn)
                        }
                    }, {
                        xtype:'component',
                        style:'margin-left:20px;margin-right:13px',
                        html:'左手拇指'
                    },{
                        xtype: 'button',
                        text: '录入',
                        width: '90px',
                        id: 'finger1',
                        iconAlign:'right',
                        style: {
                            borderRadius: '15px'
                        },
                        handler: function (btn) {
                            FingerprintInform.handleCollect(1,btn)
                        }
                    }]
                }, {
                    xtype: 'panel',
                    layout: 'hbox',
                    items: [{
                        xtype:'component',
                        style:'margin-left:20px;margin-right:13px',
                        html:'右手食指'
                    },{
                        xtype: 'button',
                        text: '录入',
                        width: '90px',
                        id: 'finger2',
                        iconAlign:'right',
                        style: {
                            borderRadius: '15px'
                        },
                        handler: function (btn) {
                            FingerprintInform.handleCollect(2,btn)
                        }
                    }, {
                        xtype:'component',
                        style:'margin-left:20px;margin-right:13px',
                        html:'左手食指'
                    },{
                        xtype: 'button',
                        text: '录入',
                        width: '90px',
                        id: 'finger3',
                        iconAlign:'right',
                        style: {
                            borderRadius: '15px'
                        },
                        handler: function (btn) {
                            FingerprintInform.handleCollect(3,btn)
                        }
                    }]
                }, {
                    xtype: 'panel',
                    layout: 'hbox',
                    items: [{
                        xtype:'component',
                        style:'margin-left:20px;margin-right:13px',
                        html:'右手中指'
                    },{
                        xtype: 'button',
                        text: '录入',
                        width: '90px',
                        id: 'finger4',
                        iconAlign:'right',
                        style: {
                            borderRadius: '15px'
                        },
                        handler: function (btn) {
                            FingerprintInform.handleCollect(4,btn)
                        }
                    },{
                        xtype:'component',
                        style:'margin-left:20px;margin-right:13px',
                        html:'左手中指'
                    },{
                        xtype: 'button',
                        text: '录入',
                        width: '90px',
                        id: 'finger5',
                        iconAlign:'right',
                        style: {
                            borderRadius: '15px'
                        },
                        handler: function (btn) {
                            FingerprintInform.handleCollect(5,btn)
                        }
                    }]
                }, {
                    xtype: 'panel',
                    layout: 'hbox',
                    items: [{
                        xtype:'component',
                        style:'margin-left:9px;margin-right:13px',
                        html:'右手无名指'
                    },{
                        xtype: 'button',
                        text: '录入',
                        width: '90px',
                        id: 'finger6',
                        iconAlign:'right',
                        style: {
                            borderRadius: '15px'
                        },
                        handler: function (btn) {
                            FingerprintInform.handleCollect(6,btn)
                        }
                    },{
                        xtype:'component',
                        style:'margin-left:9px;margin-right:12px',
                        html:'左手无名指'
                    },{
                        xtype: 'button',
                        text: '录入',
                        width: '90px',
                        id: 'finger7',
                        iconAlign:'right',
                        style: {
                            borderRadius: '15px'
                        },
                        handler: function (btn) {
                            FingerprintInform.handleCollect(7,btn)
                        }
                    }]
                }, {
                    xtype: 'panel',
                    layout: 'hbox',
                    items: [{
                        xtype:'component',
                        style:'margin-left:20px;margin-right:13px',
                        html: '右手小指'
                    },{
                        xtype: 'button',
                        text: '录入',
                        width: '90px',
                        id: 'finger8',
                        iconAlign:'right',
                        style: {
                            borderRadius: '15px'
                        },
                        handler: function (btn) {
                            FingerprintInform.handleCollect(8,btn)
                        }
                    },{
                        xtype:'component',
                        style:'margin-left:20px;margin-right:13px',
                        html: '左手小指'
                    },{
                        xtype: 'button',
                        text: '录入',
                        width: '90px',
                        id: 'finger9',
                        iconAlign:'right',
                        style: {
                            borderRadius: '15px'
                        },
                        handler: function (btn) {
                            FingerprintInform.handleCollect(9,btn)
                        }
                    }]
                },{
                    xtype: 'panel',
                    layout: 'hbox',
                    style: 'margin-top:40px;margin-left:80px',
                    items: [{
                        xtype: 'button',
                        id:'leftButton',
                        text: '清空记录',
                        border: true,
                        handler: function () {
                            if (FingerprintInform.fingerInfo.fingerInfo == '') {
                                var finger0 = Ext.getCmp('finger0');
                                finger0.setDisabled(false);
                                finger0.setIcon('');
                                var finger1 = Ext.getCmp('finger1');
                                finger1.setDisabled(false);
                                finger1.setIcon('');
                                var finger2 = Ext.getCmp('finger2');
                                finger2.setDisabled(false);
                                finger2.setIcon('');
                                var finger3 =  Ext.getCmp('finger3');
                                finger3.setDisabled(false);
                                finger3.setIcon('');
                                var finger4 = Ext.getCmp('finger4');
                                finger4.setDisabled(false);
                                finger4.setIcon('');
                                var finger5 = Ext.getCmp('finger5');
                                finger5.setDisabled(false);
                                finger5.setIcon('');
                                var finger6 = Ext.getCmp('finger6');
                                finger6.setDisabled(false);
                                finger6.setIcon('');
                                var finger7 = Ext.getCmp('finger7');
                                finger7.setDisabled(false);
                                finger7.setIcon('');
                                var finger8 = Ext.getCmp('finger8');
                                finger8.setDisabled(false);
                                finger8.setIcon('');
                                var finger9 = Ext.getCmp('finger9');
                                finger9.setDisabled(false);
                                finger9.setIcon('');

                                FingerprintInform.fingerModel= {}//指纹数据
                            }else{
                                //重置指纹
                                var map=new HashMap();
                                map.put("method","FingerReset");
                                map.put("tab_id",FingerprintInform.tab_id);
                                map.put("jobNumber",FingerprintInform.jobNumber);
                                Rpc({functionId:'SYS20200521',async:true,success:function(res){
                                        Ext.MessageBox.close();
                                        var resultObj = Ext.decode(res.responseText);
                                        var success = resultObj.returnStr.return_code;
                                        if(success == "success"){
                                            FingerprintInform.fingercheckState = '',//判断指纹是否全部审核
                                            FingerprintInform.fingerInfo = {
                                                fingerInfo: "",
                                            },
                                            Ext.getCmp('leftButton').setText('清空记录');
                                            Ext.getCmp('submitFingerBut').setText('提交');
                                            Ext.getCmp('submitFingerBut').setDisabled(false);
                                            FingerprintInform.fingerModel= {};//指纹数据
                                            var finger0 = Ext.getCmp('finger0');
                                            finger0.setDisabled(false);
                                            finger0.setIcon('');
                                            finger0.setText('录入');
                                            var finger1 = Ext.getCmp('finger1');
                                            finger1.setDisabled(false);
                                            finger1.setIcon('');
                                            finger1.setText('录入');
                                            var finger2 = Ext.getCmp('finger2');
                                            finger2.setDisabled(false);
                                            finger2.setIcon('');
                                            finger2.setText('录入');
                                            var finger3 =  Ext.getCmp('finger3');
                                            finger3.setDisabled(false);
                                            finger3.setIcon('');
                                            finger3.setText('录入');
                                            var finger4 = Ext.getCmp('finger4');
                                            finger4.setDisabled(false);
                                            finger4.setIcon('');
                                            finger4.setText('录入');
                                            var finger5 = Ext.getCmp('finger5');
                                            finger5.setDisabled(false);
                                            finger5.setIcon('');
                                            finger5.setText('录入');
                                            var finger6 = Ext.getCmp('finger6');
                                            finger6.setDisabled(false);
                                            finger6.setIcon('');
                                            finger6.setText('录入');
                                            var finger7 = Ext.getCmp('finger7');
                                            finger7.setDisabled(false);
                                            finger7.setIcon('');
                                            finger7.setText('录入');
                                            var finger8 = Ext.getCmp('finger8');
                                            finger8.setDisabled(false);
                                            finger8.setIcon('');
                                            finger8.setText('录入');
                                            var finger9 = Ext.getCmp('finger9');
                                            finger9.setDisabled(false);
                                            finger9.setIcon('');
                                            finger9.setText('录入');
                                            Ext.MessageBox.alert('提示信息', '重置指纹数据成功!');
                                        }else{
                                            Ext.MessageBox.alert('提示信息', '重置指纹数据失败!');
                                        }

                                    },scope:this},map);
                            }

                        }
                    },{
                        xtype: 'button',
                        style: 'margin-left: 17px;',
                        id:'submitFingerBut',
                        text: '提交',
                        // disabled:true,
                        border: true,
                        handler: function (btn) {
                            if (FingerprintInform.fingerInfo.fingerInfo == '') {
                                var count = 0;
                                for(var i in FingerprintInform.fingerModel) {
                                    if(FingerprintInform.fingerModel.hasOwnProperty(i)) {
                                        count++;
                                    }
                                }
                                if(count < 2){
                                    Ext.showAlert("请至少录入两个手指的指纹信息!");
                                    return;
                                }
                                var map=new HashMap();
                                map.put("method","FingerSave");
                                map.put("fingerModel",FingerprintInform.fingerModel);
                                map.put("tab_id",FingerprintInform.tab_id);
                                map.put("name",FingerprintInform.name);
                                map.put("jobNumber",FingerprintInform.jobNumber);

                                Ext.MessageBox.wait("正在提交中,请稍候...", "等待");
                                Rpc({functionId:'SYS20200521',async:true,success:function(res){
                                        Ext.MessageBox.close();
                                        var resultObj = Ext.decode(res.responseText);
                                        var success = resultObj.returnStr.return_code;
                                        if(success == "success"){
                                            FingerprintInform.initFingerInfo(resultObj.checkMap)
                                            Ext.MessageBox.alert('提示信息', '指纹录入成功，请继续完成复核步骤！');
                                            FingerprintInform.fingercheckFHState = '1';//进入复核状态
                                            //指纹提交成功后 将人脸提交按钮放开
                                            /*var submitFaceBut = Ext.getCmp('submitFaceBut');//提交按钮
                                            submitFaceBut.setDisabled(false);*/
                                            templateTool_me.refreshCurrent();//刷新当前，不考虑左侧人员列表，姓名参与计算无意义
                                        }else{
                                            Ext.MessageBox.alert('提示信息', '提交指纹数据失败!');
                                        }

                                    },scope:this},map);
                            }else{
                                if (FingerprintInform.fingercheckState != FingerprintInform.fingerInfo.fingerInfo) {
                                    Ext.showAlert('请将所有已经录入指纹的手指复核完毕后再次提交！');
                                    return;
                                }else{
                                    /*审核方法*/
                                    var map=new HashMap();
                                    map.put("method","FingerReview");
                                    map.put("tab_id",FingerprintInform.tab_id);
                                    map.put("fingerModel",FingerprintInform.fingerModel);
                                    map.put("jobNumber",FingerprintInform.jobNumber);
                                    Rpc({functionId:'SYS20200521',async:true,success:function(res){
                                            Ext.MessageBox.close();
                                            var resultObj = Ext.decode(res.responseText);
                                            var success = resultObj.returnStr.return_code;
                                            if(success == "success"){
                                                Ext.showAlert('复核成功！');
                                                FingerprintInform.fingercheckFHState = '2';//进入复核状态
                                                Ext.getCmp('submitFingerBut').setText('已复核');
                                                Ext.getCmp('submitFingerBut').setDisabled(true);
                                            }else{
                                                Ext.showAlert('复核失败！');
                                            }

                                        },scope:this},map);
                                }
                            }

                        }
                    }
                    ]
                }]
            }]

        });
        //人脸区域
        var ybottomPanel = Ext.create('Ext.form.Panel', {
            border: 0,
            layout: 'vbox',
            items: [{
                xtype:'component',
                style: 'font-size: 23px;padding-left: 100px;padding-right: 100px;padding-top: 45px;padding-bottom: 20px;',
                html: '请正对摄像头，确保光线充足;请确保是&nbsp;<span id="red" style="color:#ce4242;">账户本人</span>&nbsp;操作'
            },{
                xtype:'component',
                style:'margin-left:100px;margin-top:25px',
                html:'<div style="font-weight: 400; font-size: 16px;Float:left">摄像头:&nbsp;</div><select id="cbCameras" style="width:180px"></select>'
            },{
                xtype: 'image',
                id:'bestFaceImg',
                height: '200px',
                width: '150px',
                style: 'margin-left: 160px;margin-bottom: 60px;margin-top: 30px;cursor:pointer;',
                src: '../../../../module/template/templatenavigation/personInfoInput/images/TakingPicture.jpg',
                listeners: {
                    click: {
                        element: 'el',
                        fn: function () {
                            /*var count = 0;
                            for(var i in FingerprintInform.fingerModel) {
                                if(FingerprintInform.fingerModel.hasOwnProperty(i)) {
                                    count++;
                                }
                            }
                            if(count < 2){
                                Ext.showAlert("请先录入并提交指纹数据！");
                                return;
                            }*/
                            var takePhotoWin = Ext.create('Ext.window.Window', {
                                id: 'takePhotoWin',
                                title: '开始人脸录入',
                                height: 650,
                                width: 640,
                                items:[{
                                    xtype:'component',
                                    html:'<div id="cloudwalkwebobj" style="visibility: hidden; margin:0 auto;" align="center"></div>'
                                }],
                                listeners: {
                                    beforeclose:function(){
                                        cloudwalkobj.cwStopCamera();//关闭相机
                                        cloudwalkobj.cwStoptLiveDetect();//停止活体检测
                                    }
                                }
                            })
                            //如果是多次录入需改图片路径为默认
                            var submitFaceBut = Ext.getCmp('submitFaceBut');//提交按钮
                            submitFaceBut.setDisabled(false);
                            var bestFaceImg = Ext.getCmp('bestFaceImg');
                            bestFaceImg.setSrc('../../../../module/template/templatenavigation/personInfoInput/images/TakingPicture.jpg');
                            takePhotoWin.show();

                            var targetE = document.getElementById("cloudwalkwebobj");
                            targetE.appendChild(cloudwalkobj);
                            //开始活体检测
                            FingerprintInform.livedetectfunction()
                        }
                    }
                }
            }, {
                xtype: 'button',
                id:'submitFaceBut',
                text: '提交',
                // disabled:true,
                style: 'margin-left: 100px',
                handler: function () {
                    if(FingerprintInform.faceData.length == 0){
                        Ext.MessageBox.alert('提示信息', '人脸信息未录入!');
                        return
                    }
                    Ext.showConfirm('您确认要提交吗',function(value){
                        if(value=="yes"){
                            Ext.MessageBox.wait("正在提交中,请稍候...", "等待");
                            var map=new HashMap();
                            map.put("method","faceSave");
                            map.put("faceData",FingerprintInform.faceData);
                            map.put("jobNumber",FingerprintInform.jobNumber);
                            map.put("tab_id",FingerprintInform.tab_id);
                            Rpc({functionId:'SYS20200521',async:true,success:function(res){
                                    Ext.MessageBox.close();
                                    var resultObj = Ext.decode(res.responseText);
                                    var success = resultObj.returnStr.return_code;
                                    if(success == "success"){
                                        Ext.MessageBox.alert('提示信息', '提交人脸数据成功!');
                                        FingerprintInform.faceCheckState = '2';
                                        var submitFaceBut = Ext.getCmp('submitFaceBut');//提交按钮
                                        submitFaceBut.setDisabled(true);
                                        templateTool_me.refreshCurrent();//刷新当前，不考虑左侧人员列表，姓名参与计算无意义
                                    }else{
                                        Ext.MessageBox.alert('提示信息', '提交人脸数据失败');
                                    }

                                },scope:this},map);
                        }else{
                            return;
                        }
                    });


                }
            }
            ]
        });
        var totalPanel = Ext.create('Ext.panel.Panel',{
            border : 0,
            layout: 'hbox',
            items:[{
                xtype: 'panel',
                width: Ext.getBody().getWidth()/2,
                height:Ext.getBody().getHeight(),
                items: [{
                    xtype: 'container',
                    html: '<div style="margin-left:20px;margin-top:10px;font-size: 23px">指纹录入</div>'
                }, {
                    xtype: 'panel',
                    border: 0,
                    style: 'font-size: 23px;padding-left: 100px;padding-right: 100px;padding-top: 45px;',
                    html: '<span style="font-size: 23px">提示：进行指纹采集时请保持手指干燥，并保证至少录入两个手指信息,每个手指需要连续采集三次。</span>'
                },zbottomPanel],
            },{
                xtype: 'panel',
                width: Ext.getBody().getWidth()/2,
                height:Ext.getBody().getHeight(),
                items: [{
                    xtype: 'container',
                    html: '<div style="margin-left:20px;margin-top:10px;font-size: 23px">人脸录入</div>'
                },ybottomPanel],
            }]
        })

        FingerprintInform.add(totalPanel);
    },
    //页面加载后调用控件
    onPageLoad:function()
    {
        var bSuccess = FingerprintInform.createPlugin();
        if(bSuccess)
        {
            //1、设置图片保存位置
            var jsInfo = new Object();
            jsInfo.BestFramePath = FingerprintInform.BestFramePath;
            jsInfo.BestFacePath = FingerprintInform.BestFacePath;
            var last=JSON.stringify(jsInfo);
            var nRet = cloudwalkobj.cwSetConfig(last);
            if(nRet)
            {
                Ext.showAlert('保存图片位置出错!');
            }
            //2、给相机设备下拉框赋值
            var cbCameras = document.getElementById("cbCameras");
            var strJson = cloudwalkobj.cwQueryCamera();
            var jsonObj = $.parseJSON(strJson);
            for(i = 0; i < jsonObj.count; i++)
            {
                var item = document.createElement('option');
                item.text = "[" + i + "] " + jsonObj.list[i].DeviceName;
                item.value = jsonObj.list[i].DeviceId;
                //注：IE7 不支持 add() 的第二个参数。
                cbCameras.add(item);
            }
        }

    },
    initFingerInfo:function(checkMap){
        FingerprintInform.fingerModel = {};
        var checkFinger =  checkMap.checkFinger;
        for (var i = 0; i < checkFinger.length; i++) {
            var tempCheckfinger = checkFinger[i];
            Ext.getCmp(tempCheckfinger).setDisabled(false);
            Ext.getCmp(tempCheckfinger).setIcon('');
            Ext.getCmp(tempCheckfinger).setText('复核');
            FingerprintInform.fingerInfo.fingerInfo +="-"
        }
        var noCheckFinger =  checkMap.noCheckFinger;

        for (var i = 0; i < noCheckFinger.length; i++) {
            var tempCheckfinger = noCheckFinger[i];
            Ext.getCmp(tempCheckfinger).setDisabled(true);
            Ext.getCmp(tempCheckfinger).setIcon('');
        }
        Ext.getCmp('submitFingerBut').setText('复核');
        Ext.getCmp('leftButton').setText('重置指纹');

    },
/* 提示错误信息 */
ShowErr:function(nErr, sCmd)
{
    if(nErr)
    {
        alert(sCmd + "失败：" + nErr);
    }
    else
    {
        alert(sCmd + "成功！");
    }
},
    /* 命令执行 */
handleCollect:function(nCmd,btn)
{
    /*获取界面上的设置属性*/
    var nPortNo = "0";		// 端口SLCT
    var dwWaitTime = "18000";	// 超时EDIT
    var nLevel = "3";		// 安全级SLCT

    var nRet = -1; var sDvSn = "";

    if (typeof(ObjFinger) == "undefined") {
        Ext.showAlert("控件未找到，请检查HTMl代码里的OBJECT");
        return -1;
    }

    /*------设备驱动配置信息，除非您清楚，不建议随意改动------*/
    // 0x30 拆分格式
    // ObjFinger.nSucStrFmtTyp = 0;
    // 商行协议
    //ObjFinger.nComProtocol = 2;
    // USB协议
    //ObjFinger.nUsbProtocol = 0;
    // 是否检测抬起------cuizong007 add by 2014/3/6-------------->(0是不检测，1是检测)
    ObjFinger.nRegChkToLv = 0;
    // 是否显示 注册指纹模板时的对话框
    //ObjFinger.nRegShowDlg = 1;
    // 是否显示 验证指纹特征时的对话框
    //ObjFinger.nVerShowDlg = 1;
    // 是否禁用自动提速  0-启用自动提速， 4= 固定9600波特率
    //ObjFinger.nNotSpeedUp = 4;
    //ObjFinger.nComShwOnOcx = 10;


    /*------调用具体的各种方法------*/

    // 通用方法
    //当前按钮状态，判断采集模板或是采集特征值
    if (FingerprintInform.fingerInfo.fingerInfo == '') {
        //调用指纹仪设备采集【模板】
        switch(nCmd)
        {
            case 0: //
                nRet = ObjFinger.FPIGetTemplate(nPortNo, dwWaitTime);
                if (nRet) {
                    Ext.showAlert("注册指纹模板失败。错误码:[" + nRet + "]");
                } else {
                    FingerprintInform.fingerModel.finger0  = ObjFinger.FPIGetFingerInfo();
                    btn.setIcon(markUrl);
                    btn.setDisabled(true);
                }
                break;

            case 1: //
                nRet = ObjFinger.FPIGetTemplate(nPortNo, dwWaitTime);
                if (nRet) {
                    Ext.showAlert("注册指纹模板失败。错误码:[" + nRet + "]");
                } else {
                    FingerprintInform.fingerModel.finger1 = ObjFinger.FPIGetFingerInfo();
                    btn.setIcon(markUrl);
                    btn.setDisabled(true);
                }
                break;

            case 2: //
                nRet = ObjFinger.FPIGetTemplate(nPortNo, dwWaitTime);
                if (nRet) {
                    Ext.showAlert("注册指纹模板失败。错误码:[" + nRet + "]");
                } else {
                    FingerprintInform.fingerModel.finger2 = ObjFinger.FPIGetFingerInfo();
                    btn.setIcon(markUrl);
                    btn.setDisabled(true);
                }
                break;

            case 3:
                nRet = ObjFinger.FPIGetTemplate(nPortNo, dwWaitTime);
                if (nRet) {
                    Ext.showAlert("注册指纹模板失败。错误码:[" + nRet + "]");
                } else {
                    FingerprintInform.fingerModel.finger3 = ObjFinger.FPIGetFingerInfo();
                    btn.setIcon(markUrl);
                    btn.setDisabled(true);
                }
                break;

            case 4: //
                nRet = ObjFinger.FPIGetTemplate(nPortNo, dwWaitTime);
                if (nRet) {
                    Ext.showAlert("注册指纹模板失败。错误码:[" + nRet + "]");
                } else {
                    FingerprintInform.fingerModel.finger4 = ObjFinger.FPIGetFingerInfo();
                    btn.setIcon(markUrl);
                    btn.setDisabled(true);
                }
                break;

            case 5: //
                nRet = ObjFinger.FPIGetTemplate(nPortNo, dwWaitTime);
                if (nRet) {
                    Ext.showAlert("注册指纹模板失败。错误码:[" + nRet + "]");
                } else {
                    FingerprintInform.fingerModel.finger5 = ObjFinger.FPIGetFingerInfo();
                    btn.setIcon(markUrl);
                    btn.setDisabled(true);
                }
                break;

            case 6://
                nRet = ObjFinger.FPIGetTemplate(nPortNo, dwWaitTime);
                if (nRet) {
                    Ext.showAlert("注册指纹模板失败。错误码:[" + nRet + "]");
                } else {
                    FingerprintInform.fingerModel.finger6 = ObjFinger.FPIGetFingerInfo();
                    btn.setIcon(markUrl);
                    btn.setDisabled(true);
                }
                break;
            case 7://
                nRet = ObjFinger.FPIGetTemplate(nPortNo, dwWaitTime);
                if (nRet) {
                    Ext.showAlert("注册指纹模板失败。错误码:[" + nRet + "]");
                } else {
                    FingerprintInform.fingerModel.finger7 = ObjFinger.FPIGetFingerInfo();
                    btn.setIcon(markUrl);
                    btn.setDisabled(true);
                }
                break;
            case 8://
                nRet = ObjFinger.FPIGetTemplate(nPortNo, dwWaitTime);
                if (nRet) {
                    Ext.showAlert("注册指纹模板失败。错误码:[" + nRet + "]");
                } else {
                    FingerprintInform.fingerModel.finger8 = ObjFinger.FPIGetFingerInfo();
                    btn.setIcon(markUrl);
                    btn.setDisabled(true);
                }
                break;
            case 9://
                nRet = ObjFinger.FPIGetTemplate(nPortNo, dwWaitTime);
                if (nRet) {
                    Ext.showAlert("注册指纹模板失败。错误码:[" + nRet + "]");
                } else {
                    FingerprintInform.fingerModel.finger9 = ObjFinger.FPIGetFingerInfo();
                    btn.setIcon(markUrl);
                    btn.setDisabled(true);
                }
                break;
            default:
                alert("错误的命令");
        }
    }else{
        //调用指纹仪设备采集【特征值】
        switch(nCmd)
        {
            case 0: //
                nRet = ObjFinger.FPIGetFeature(nPortNo, dwWaitTime);
                if (nRet) {
                    Ext.showAlert("验证指纹特征失败。错误码:[" + nRet + "]");
                } else {
                    //调用接口进行特征值比对
                    Ext.MessageBox.wait("正在复核中,请稍候...", "等待");
                    var FingerInfo = ObjFinger.FPIGetFingerInfo();

                    var map=new HashMap();
                    map.put("method","FingerCheck");
                    map.put("tab_id",FingerprintInform.tab_id);
                    map.put("finger","finger0");
                    map.put("jobNumber",FingerprintInform.jobNumber);
                    map.put("featureFinger",FingerInfo);
                    Rpc({functionId:'SYS20200521',async:true,success:function(res){
                            Ext.MessageBox.close();
                            var resultObj = Ext.decode(res.responseText);
                            var success = resultObj.returnStr.return_code;
                            if(success == "success"){
                                FingerprintInform.fingerModel.finger0 = FingerInfo;
                                FingerprintInform.fingercheckState += '-'
                                btn.setIcon(markUrl);
                                btn.setDisabled(true);
                            }else{
                                btn.setIcon(chahaoUrl);
                            }

                        },scope:this},map);

                }
                break;

            case 1: //
                nRet = ObjFinger.FPIGetFeature(nPortNo, dwWaitTime);
                if (nRet) {
                    Ext.showAlert("验证指纹特征失败。错误码:[" + nRet + "]");
                } else {
                    //调用接口进行特征值比对
                    Ext.MessageBox.wait("正在复核中,请稍候...", "等待");
                    var FingerInfo = ObjFinger.FPIGetFingerInfo();

                    var map=new HashMap();
                    map.put("method","FingerCheck");
                    map.put("tab_id",FingerprintInform.tab_id);
                    map.put("finger","finger1");
                    map.put("jobNumber",FingerprintInform.jobNumber);
                    map.put("featureFinger",FingerInfo);
                    Rpc({functionId:'SYS20200521',async:true,success:function(res){
                            Ext.MessageBox.close();
                            var resultObj = Ext.decode(res.responseText);
                            var success = resultObj.returnStr.return_code;
                            if(success == "success"){
                                FingerprintInform.fingerModel.finger1 = FingerInfo;
                                FingerprintInform.fingercheckState += '-'
                                btn.setIcon(markUrl);
                                btn.setDisabled(true);
                            }else{
                                btn.setIcon(chahaoUrl);
                            }

                        },scope:this},map);

                }
                break;

            case 2: //
                nRet = ObjFinger.FPIGetFeature(nPortNo, dwWaitTime);
                if (nRet) {
                    Ext.showAlert("验证指纹特征失败。错误码:[" + nRet + "]");
                } else {
                    //调用接口进行特征值比对
                    Ext.MessageBox.wait("正在复核中,请稍候...", "等待");
                    var FingerInfo = ObjFinger.FPIGetFingerInfo();

                    var map=new HashMap();
                    map.put("method","FingerCheck");
                    map.put("tab_id",FingerprintInform.tab_id);
                    map.put("finger","finger2");
                    map.put("jobNumber",FingerprintInform.jobNumber);
                    map.put("featureFinger",FingerInfo);
                    Rpc({functionId:'SYS20200521',async:true,success:function(res){
                            Ext.MessageBox.close();
                            var resultObj = Ext.decode(res.responseText);
                            var success = resultObj.returnStr.return_code;
                            if(success == "success"){
                                FingerprintInform.fingerModel.finger2 = FingerInfo;
                                FingerprintInform.fingercheckState += '-'
                                btn.setIcon(markUrl);
                                btn.setDisabled(true);
                            }else{
                                btn.setIcon(chahaoUrl);
                            }

                        },scope:this},map);

                }
                break;

            case 3:
                nRet = ObjFinger.FPIGetFeature(nPortNo, dwWaitTime);
                if (nRet) {
                    Ext.showAlert("验证指纹特征失败。错误码:[" + nRet + "]");
                } else {
                    //调用接口进行特征值比对
                    Ext.MessageBox.wait("正在复核中,请稍候...", "等待");
                    var FingerInfo = ObjFinger.FPIGetFingerInfo();

                    var map=new HashMap();
                    map.put("method","FingerCheck");
                    map.put("tab_id",FingerprintInform.tab_id);
                    map.put("finger","finger3");
                    map.put("jobNumber",FingerprintInform.jobNumber);
                    map.put("featureFinger",FingerInfo);
                    Rpc({functionId:'SYS20200521',async:true,success:function(res){
                            Ext.MessageBox.close();
                            var resultObj = Ext.decode(res.responseText);
                            var success = resultObj.returnStr.return_code;
                            if(success == "success"){
                                FingerprintInform.fingerModel.finger3 = FingerInfo;
                                FingerprintInform.fingercheckState += '-'
                                btn.setIcon(markUrl);
                                btn.setDisabled(true);
                            }else{
                                btn.setIcon(chahaoUrl);
                            }

                        },scope:this},map);

                }
                break;

            case 4: //
                nRet = ObjFinger.FPIGetFeature(nPortNo, dwWaitTime);
                if (nRet) {
                    Ext.showAlert("验证指纹特征失败。错误码:[" + nRet + "]");
                } else {
                    //调用接口进行特征值比对
                    Ext.MessageBox.wait("正在复核中,请稍候...", "等待");
                    var FingerInfo = ObjFinger.FPIGetFingerInfo();

                    var map=new HashMap();
                    map.put("method","FingerCheck");
                    map.put("tab_id",FingerprintInform.tab_id);
                    map.put("finger","finger4");
                    map.put("jobNumber",FingerprintInform.jobNumber);
                    map.put("featureFinger",FingerInfo);
                    Rpc({functionId:'SYS20200521',async:true,success:function(res){
                            Ext.MessageBox.close();
                            var resultObj = Ext.decode(res.responseText);
                            var success = resultObj.returnStr.return_code;
                            if(success == "success"){
                                FingerprintInform.fingerModel.finger4 = FingerInfo;
                                FingerprintInform.fingercheckState += '-'
                                btn.setIcon(markUrl);
                                btn.setDisabled(true);
                            }else{
                                btn.setIcon(chahaoUrl);
                            }

                        },scope:this},map);

                }
                break;

            case 5: //
                nRet = ObjFinger.FPIGetFeature(nPortNo, dwWaitTime);
                if (nRet) {
                    Ext.showAlert("验证指纹特征失败。错误码:[" + nRet + "]");
                } else {
                    //调用接口进行特征值比对
                    Ext.MessageBox.wait("正在复核中,请稍候...", "等待");
                    var FingerInfo = ObjFinger.FPIGetFingerInfo();

                    var map=new HashMap();
                    map.put("method","FingerCheck");
                    map.put("tab_id",FingerprintInform.tab_id);
                    map.put("finger","finger5");
                    map.put("jobNumber",FingerprintInform.jobNumber);
                    map.put("featureFinger",FingerInfo);
                    Rpc({functionId:'SYS20200521',async:true,success:function(res){
                            Ext.MessageBox.close();
                            var resultObj = Ext.decode(res.responseText);
                            var success = resultObj.returnStr.return_code;
                            if(success == "success"){
                                FingerprintInform.fingerModel.finger5 = FingerInfo;
                                FingerprintInform.fingercheckState += '-'
                                btn.setIcon(markUrl);
                                btn.setDisabled(true);
                            }else{
                                btn.setIcon(chahaoUrl);
                            }

                        },scope:this},map);

                }
                break;

            case 6://
                nRet = ObjFinger.FPIGetFeature(nPortNo, dwWaitTime);
                if (nRet) {
                    Ext.showAlert("验证指纹特征失败。错误码:[" + nRet + "]");
                } else {
                    //调用接口进行特征值比对
                    Ext.MessageBox.wait("正在复核中,请稍候...", "等待");
                    var FingerInfo = ObjFinger.FPIGetFingerInfo();

                    var map=new HashMap();
                    map.put("method","FingerCheck");
                    map.put("tab_id",FingerprintInform.tab_id);
                    map.put("finger","finger6");
                    map.put("jobNumber",FingerprintInform.jobNumber);
                    map.put("featureFinger",FingerInfo);
                    Rpc({functionId:'SYS20200521',async:true,success:function(res){
                            Ext.MessageBox.close();
                            var resultObj = Ext.decode(res.responseText);
                            var success = resultObj.returnStr.return_code;
                            if(success == "success"){
                                FingerprintInform.fingerModel.finger6 = FingerInfo;
                                FingerprintInform.fingercheckState += '-'
                                btn.setIcon(markUrl);
                                btn.setDisabled(true);
                            }else{
                                btn.setIcon(chahaoUrl);
                            }

                        },scope:this},map);

                }
                break;
            case 7://
                nRet = ObjFinger.FPIGetFeature(nPortNo, dwWaitTime);
                if (nRet) {
                    Ext.showAlert("验证指纹特征失败。错误码:[" + nRet + "]");
                } else {
                    //调用接口进行特征值比对
                    Ext.MessageBox.wait("正在复核中,请稍候...", "等待");
                    var FingerInfo = ObjFinger.FPIGetFingerInfo();

                    var map=new HashMap();
                    map.put("method","FingerCheck");
                    map.put("tab_id",FingerprintInform.tab_id);
                    map.put("finger","finger7");
                    map.put("jobNumber",FingerprintInform.jobNumber);
                    map.put("featureFinger",FingerInfo);
                    Rpc({functionId:'SYS20200521',async:true,success:function(res){
                            Ext.MessageBox.close();
                            var resultObj = Ext.decode(res.responseText);
                            var success = resultObj.returnStr.return_code;
                            if(success == "success"){
                                FingerprintInform.fingerModel.finger7 = FingerInfo;
                                FingerprintInform.fingercheckState += '-'
                                btn.setIcon(markUrl);
                                btn.setDisabled(true);
                            }else{
                                btn.setIcon(chahaoUrl);
                            }

                        },scope:this},map);

                }
                break;
            case 8://
                nRet = ObjFinger.FPIGetFeature(nPortNo, dwWaitTime);
                if (nRet) {
                    Ext.showAlert("验证指纹特征失败。错误码:[" + nRet + "]");
                } else {
                    //调用接口进行特征值比对
                    Ext.MessageBox.wait("正在复核中,请稍候...", "等待");
                    var FingerInfo = ObjFinger.FPIGetFingerInfo();

                    var map=new HashMap();
                    map.put("method","FingerCheck");
                    map.put("tab_id",FingerprintInform.tab_id);
                    map.put("finger","finger8");
                    map.put("jobNumber",FingerprintInform.jobNumber);
                    map.put("featureFinger",FingerInfo);
                    Rpc({functionId:'SYS20200521',async:true,success:function(res){
                            Ext.MessageBox.close();
                            var resultObj = Ext.decode(res.responseText);
                            var success = resultObj.returnStr.return_code;
                            if(success == "success"){
                                FingerprintInform.fingerModel.finger8 = FingerInfo;
                                FingerprintInform.fingercheckState += '-'
                                btn.setIcon(markUrl);
                                btn.setDisabled(true);
                            }else{
                                btn.setIcon(chahaoUrl);
                            }

                        },scope:this},map);

                }
                break;
            case 9://
                nRet = ObjFinger.FPIGetFeature(nPortNo, dwWaitTime);
                if (nRet) {
                    Ext.showAlert("验证指纹特征失败。错误码:[" + nRet + "]");
                } else {
                    //调用接口进行特征值比对
                    Ext.MessageBox.wait("正在复核中,请稍候...", "等待");
                    var FingerInfo = ObjFinger.FPIGetFingerInfo();

                    var map=new HashMap();
                    map.put("method","FingerCheck");
                    map.put("tab_id",FingerprintInform.tab_id);
                    map.put("finger","finger9");
                    map.put("jobNumber",FingerprintInform.jobNumber);
                    map.put("featureFinger",FingerInfo);
                    Rpc({functionId:'SYS20200521',async:true,success:function(res){
                            Ext.MessageBox.close();
                            var resultObj = Ext.decode(res.responseText);
                            var success = resultObj.returnStr.return_code;
                            if(success == "success"){
                                FingerprintInform.fingerModel.finger9 = FingerInfo;
                                FingerprintInform.fingercheckState += '-'
                                btn.setIcon(markUrl);
                                btn.setDisabled(true);
                            }else{
                                btn.setIcon(chahaoUrl);
                            }

                        },scope:this},map);

                }
                break;
            default:
                alert("错误的命令");
        }
    }

},
//targetE: 是 DOM 中的一个节点。
createPlugin:function()
{
    //因为点击按钮时，IE 8 会重复调用到这个函数。2018-08-17
    var bIsNull = false;
    if(cloudwalkobj === undefined)
        bIsNull = true;

    if(!bIsNull)
    {
        return;
    }
    var bDet = BrowserDetect.browser;
    if("Explorer" == bDet)
    {
        //IE: use OCX;
        cloudwalkobj = document.createElement("object");
        cloudwalkobj.id = "IE_CloudWalkSDKPlugin";
        cloudwalkobj.classid = "CLSID:B1597418-A51E-4140-8698-EE865439755C";
    }
    else
    {
        //NetScape: firefox, chrome; use NPAPI plugin;
        cloudwalkobj = document.createElement("embed");
        cloudwalkobj.id = "FF_CloudWalkSDKPlugin";
        cloudwalkobj.type = "application/x-cloudface-sdk3.0";
        //cloudwalkobj = document.getElementById("FF_CloudWalkSDKPlugin");
    }
    if(rotatemode == 0 || rotatemode == 3)
    {
        cloudwalkobj.width = width;
        cloudwalkobj.height = height + 130;
    }
    else
    {
        cloudwalkobj.width = height;
        cloudwalkobj.height = width + 130;
    }
    if(!cloudwalkobj.valid)
    {
        Ext.showAlert("内存不足！并稍等片刻。");
        return false;
    }
    return true;
},
/*单个指纹校验*/
checkFingerFeature:function(featureFinger){
    var map=new HashMap();
    map.put("method","FingerCheck");
    map.put("tab_id",FingerprintInform.tab_id);
    map.put("featureFinger",featureFinger);
    Rpc({functionId:'SYS20200521',async:true,success:function(res){
            Ext.MessageBox.close();
            var resultObj = Ext.decode(res.responseText);
            var success = resultObj.returnStr.return_code;
            if(success == "success"){
                return "success"
            }else{
                return "fail"
            }

        },scope:this},map);
},

//开始活体检测
startRandomLiveDetect:function()
{
    FingerprintInform.showocx();
    var cbCameras = document.getElementById("cbCameras");
    g_iCamera = eval(cbCameras.value);
    //打开摄像头
    var nRet = cloudwalkobj.cwStartCamera(g_iCamera, rotatemode);
    if(nRet != 0)
    {
        var msg;
        switch(nRet)
        {
            case CW_ERR_CameraNotOpen:
                msg = "摄像头未打开！";
                break;
            case CW_ERR_CameraOpenError:
                msg = "摄像头打开失败！";
                break;
            case CW_ERR_CameraOpenAdy:
                msg = "摄像头已经打开！";
                break;
            default:
                msg = "摄像头打开失败 ！错误码："+ nRet;
                break;
        }
        Ext.showAlert(msg);
        return;
    }
    // cloudwalkobj.cwSaveBestFacePic(1);
    if(g_bNeedHack && CW_nLiveNum <= 1)
        cloudwalkobj.cwNeedHack(1);
    var opt = FingerprintInform.getRandomIndexPOC();
    //开始活体检测
    cloudwalkobj.cwStartLiveDetect(opt);
},

//检测完回调方法
liveDetectEvent:function(jsonStr)
{
    var takePhotoWin = Ext.getCmp('takePhotoWin');//图片组件

    var jsonobject = $.parseJSON(jsonStr);
    if(jsonobject.Result == 0)
    {

        if(n_LiveTime >= CW_nLiveNum)
        {
            if(takePhotoWin){
                var recvInfo = cloudwalkobj.cwGetBestFace();//获取最佳人脸图片
                cloudwalkobj.cwStopCamera();//关闭相机
                cloudwalkobj.cwStoptLiveDetect();//停止活体检测
                var bestFaceImg = Ext.getCmp('bestFaceImg');
                var obj = JSON.parse(recvInfo);
                bestFaceImg.setSrc("data:image/jpeg;base64,"+obj.data);
                //解析人脸数据
                // FingerprintInform.faceData = recvInfo;
                FingerprintInform.faceData = obj.data;
                takePhotoWin.close();
                takePhotoWin.destroy();
                FingerprintInform.faceCheckState = '1';
                Ext.showAlert('人脸录入完成!');
            }


        }
        else
        {
            if(g_bNeedHack && (n_LiveTime == (CW_nLiveNum - 1)))
                cloudwalkobj.cwNeedHack(1);
            var opt = getRandomIndexPOC();
            cloudwalkobj.cwStartLiveDetect(opt);
        }
    }
    else
    {
        //注意，cwStoptLiveDetect 接口名称里面多写了一个 t -- comment by jinfude
        cloudwalkobj.cwStopCamera();
        cloudwalkobj.cwStoptLiveDetect();
        takePhotoWin.close();
        FingerprintInform.toErrorPage(jsonobject.Result);
        return;
    }
},

toErrorPage:function(rt){
    switch(rt)
    {
        case CW_NoFace:
            //未检测到人脸
            Ext.showAlert('未检测到人脸');
            break;
        case CW_LostFace:
            //人脸丢失
            Ext.showAlert('人脸丢失');
            break;
        case CW_ShakeFace:
            //人脸晃动
            Ext.showAlert('人脸晃动');
            break;
        case CW_DetectLieveTimeOut:
            //检测超时
            Ext.showAlert('检测超时');
            break;
        default:
            Ext.showAlert('其他错误!');
            break;
    }
},


livedetectfunction:function()
{
    var el = document.getElementById("cloudwalkwebobj");
    FingerprintInform.createPlugin(el);

    registerCallBack(cloudwalkobj, "cwLivesInfoCallBack", FingerprintInform.liveDetectEvent);
    var nret = cloudwalkobj.cwInit(strLicense);
    if(nret != 0)
    {
        var msg = "初始化SDK失败错误码：" + nret;
        if('20009'==nret){
            Ext.showAlert('初始化SDK失败,请检查是否正确安装驱动!');
        }else{
            Ext.showAlert(msg);
        }
        var takePhotoWin = Ext.getCmp('takePhotoWin');//图片组件
        takePhotoWin.close();
        takePhotoWin.destroy();
        return;
    }
    cloudwalkobj.cwSetLiveDetectLevel(CW_nLiveLevel);
    FingerprintInform.InitArry();
    FingerprintInform.startRandomLiveDetect();
},


getRandomIndexPOC:function()
{
    //动作个数为 0，则只需要做一个准备阶段即可；
    if(CW_nLiveNum == 0)
    {
        n_LiveTime = n_LiveTime + 1;
        return 0;
    }

    //产生下一个动作POC
    var opt = -1;
    if(CW_nLiveNum == 1)
    {
        var myarray = [1,6]; //张嘴眨眼取一个
        opt = getRandomNumInArray(myarray);
    }
    else if(CW_nLiveNum == 2)
    {
        if(g_optarr[0] == 1)
        {
            opt = 6;
        }
        else if(g_optarr[5] == 1)
        {
            opt = 1;
        }
        else
        {
            var myarray = [1,6];
            opt = getRandomNumInArray(myarray);
        }
    }
    else
    {
        if(g_optarr[0] != 1 && g_optarr[5] != 1 && (CW_nLiveNum - n_LiveTime) == 2)
        {
            var myarray = [1,6];
            opt = getRandomNumInArray(myarray);
        }
        else if((CW_nLiveNum - n_LiveTime) == 1 && (g_optarr[0] == 0 || g_optarr[5] == 0))
        {
            if(g_optarr[0] == 0)
            {
                opt = 1;
            }
            else if(g_optarr[5] == 0)
            {
                opt = 6;
            }
        }
        else
        {
            var myarray = new Array();
            for(var i = 0; i < n_LiveOptionNum; i++)
            {
                if(g_optarr[i] == 0)
                {
                    myarray.push(i+1);
                }
            }
            opt = getRandomNumInArray(myarray);
        }
    }

    if(opt >= 1)
    {
        var index = opt - 1;
        g_optarr[index] = 1;
    }
    n_LiveTime = n_LiveTime + 1;
    return opt;
},

getRandomNumInArray:function(arr)
{
    var nLength = arr.length;
    var nrandom = Math.random()*nLength;
    nrandom = Math.floor(nrandom);
    var opt = arr[nrandom];
    return opt;
},

InitArry:function()
{
    for(var i = 0; i < n_LiveOptionNum; i++)
    {
        g_optarr[i] = 0;
    }
    //poc 开启
    //g_optarr[2] = 1; //点头去掉
},

showocx:function()
{
    var node1 = document.getElementById("cloudwalkwebobj");
    node1.style.display = "block";
    node1.style.visibility = "visible";
    node1.style.width = cloudwalkobj.width;
    node1.style.height = cloudwalkobj.height;
},

hideocx:function()
{
    var node1 = document.getElementById("cloudwalkwebobj");
    node1.style.display = "block";
    node1.style.visibility = "hidden";
    node1.style.width = 1;
    node1.style.height = 1;
}

});
