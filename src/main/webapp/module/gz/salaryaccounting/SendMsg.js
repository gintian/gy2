/**
 *发送通知
 */
Ext.define('SalaryUL.SendMsg',{
    constructor:function(config){
        this.salaryid = config.salaryid;
        this.appdate = config.appdate;
        this.count=config.count;
        sendMsgScope=this;
        sendMsgScope.createMsg('init');//参数为'init'时，则为初次加载
        sendMsgScope.Send_ok=3;//邮件状态 0未发送，1已发送，2失败，3全部
    },
    createMsg:function(init){
        var templateId='';
        var map = new HashMap();
        if(init=='init2'){//为'init2'时，模板发生变化时触发，为'init'时，则为初次加载
            templateId=Ext.getCmp('templatefieldId').getValue();
            if(templateId==null||templateId==''){
                Ext.showAlert('请选择邮件模板！');
                return;
            }
            map.put("templateId",templateId);
        }
        map.put("salaryid",salaryid);
        map.put("appdate",appdate);
        map.put("count",count);
        map.put("init",init);
        Rpc({functionId:'GZ00000141',success: function(form,action){
                var result = Ext.decode(form.responseText);
                if(result.succeed){
                    sendMsgScope.createMsgOK(result,form,action,init);
                }else{
                    Ext.showAlert(result.message);
                }
            }},map);
    },
    createMsgOK:function(result,form,action,init){
        var conditions=result.tableConfigSendMsg;
        sendMsgScope._appdate = result._appdate;//业务日期明文,用来页面业务日期查询使用 zhaoxg add 2016-9-1
        sendMsgScope._iniAppdate=result._appdate;//明码业务日期。

        if(sendMsgScope._iniAppdate.length==10)
            sendMsgScope._iniAppdate=sendMsgScope._iniAppdate.substring(0,7);
        var obj = Ext.decode(conditions);
        sendMsgScope.msgTable = new BuildTableObj(obj);
        var params = new Object();
        params.salaryid=this.salaryid;
        params.appdate=this.appdate;
        params._appdate=sendMsgScope._appdate;
        params.init='init';
        params.subModuleId="salaryaccountingsendmsg";
        Ext.getCmp("salaryaccountingsendmsg_querybox").setCustomParams(params);
        var mainPanel = sendMsgScope.msgTable.getMainPanel();
        var vs = Ext.getBody().getViewSize();
        var window1=Ext.widget("window",{
            title:'发送通知',
            height:vs.height,
            width:vs.width,
            layout:'fit',
            scrollable:false,
            modal:true,
            id:'mainWin',
            border:false,
            closeAction:'destroy',
            items: [mainPanel]
        });
        window1.show();

        //选择模板的store，需要从后台查询。
        var templateStore = Ext.create('Ext.data.Store',{
            fields:['name','id'],
            proxy:{
                type: 'transaction',
                functionId:'GZ00000142',
                extraParams:{
                    salaryid:salaryid,
                    opt:'1'
                },
                reader: {
                    type: 'json',
                    root: 'data'
                }
            },
            autoLoad: true
        });
        if(init=="init"){
            //选择模板加载之后默认选中第一项
            templateStore.on('load',function(){
                Ext.getCmp('templatefieldId').select(templateStore.getAt(0));
            });
        }
        //表格控件上方的按钮
        var tool=Ext.getCmp('toolbar');//处理跨页面id重复
        if(tool)
            tool.destroy();

        if(Ext.util.CSS.getRule(".x-form-text-default"))
            Ext.util.CSS.updateRule(".x-form-text-default","padding-bottom","1px");

        var toolbar  = Ext.create('Ext.toolbar.Toolbar',{
            border:0,
            id:"toolbar",
            height:23,
            padding:0,
//						 dock:'top',
            items:[{
                xtype:'combobox',
                id:'templatefieldId',
                store:templateStore,
                displayField:'name',
                valueField:'id',
                fieldLabel:'选择模板',
                queryMode : 'local',
                repeatTriggerClick : true,
                blankText:'请选择……',
                width:250,
                labelAlign:'left',
                editable:false,
                labelWidth:60,
                listeners:{
                    'select':function(){
                        Ext.getCmp('salaryaccountingsendmsg_querybox').removeAllKeys();
                        sendMsgScope.loadStore(3);
                    }
                }
            }]
        });

        //判断是否具有默认业务日期
        if(sendMsgScope._appdate==''){
            var myDate = new Date();
            sendMsgScope.year = myDate.getFullYear();
            sendMsgScope.month = parseInt(myDate.getMonth())+1;
        }
        else{
            var t=sendMsgScope._appdate.split("-");
            var myDate=new Date(t[0],(t[1]-1).toString());
            sendMsgScope.year = myDate.getFullYear();
            sendMsgScope.month = parseInt(myDate.getMonth())+1;
        }
        sendMsgScope.sendValue = "3";
        Ext.getCmp("salaryaccountingsendmsg_toolbar").insert(0,toolbar);
        var style = "color: green;text-decoration: underline";
        var html = '<div style="height:20;margin:5 0 5 0">查询方案:&nbsp;<a style="'+style+'" href="javascript:sendMsgScope.loadStore(3);">全部</a>&nbsp;&nbsp;';
        html += '<a style="'+style+'" href="javascript:sendMsgScope.loadStore(1);"><img src="/images/new_module/icon_tgsx.gif" border="0"/>成功</a>&nbsp;&nbsp;';
        html += '<a style="'+style+'" href="javascript:sendMsgScope.loadStore(0);"><img src="/images/new_module/icon_fbyjs.gif" border="0"/>未发</a>&nbsp;&nbsp;';
        html += '<a style="'+style+'" href="javascript:sendMsgScope.loadStore(2);"><img src="/images/new_module/icon_wtgsx.gif" border="0"/>未成功</a>&nbsp;&nbsp;';
        html+='<a style="'+style+'" href="javascript:sendMsgScope.loadStore(4);"><img src="/images/icon_published.gif" border="0"/>发送中</a>&nbsp;|&nbsp;';
        html += "<a id = 'dateymid' href='javascript:sendMsgScope.click();' title='业务日期'><span id='timetitle'>"+sendMsgScope.year+"</span>年 <span id='monthtitle'>"+sendMsgScope.month+"</span>月 <img id='jt' src='/workplan/image/jiantou.png' /></a>&nbsp;</div>";
        var toolbar  = Ext.create('Ext.toolbar.Toolbar',{
            border:0,
            id:'status',
            padding:0,
            height:20,
            items:[{
                xtype:'panel',
                border:false,
                width:400,
                html:html
            }]
        });
        sendMsgScope.msgTable.insertItem(toolbar, 0);
    },
    getSelect:function (grid, col) { //获取选中grid的列
        var st="";
        for (var i = 0; i < grid.getSelectionModel().getSelection().length; i++) {
            if(grid.getSelectionModel().getSelection()[i].get(col)!=null && grid.getSelectionModel().getSelection()[i].get(col).length>0){
                st+=grid.getSelectionModel().getSelection()[i].get(col)+",";
            }
        }
        st=st.substring(0,st.length-1);
        return st;
    },
    getSelectedId:function(grid) { //初次生成通知时，前台拼接personid的方法
        var st="";
        for (var i = 0; i < grid.getSelectionModel().getSelection().length; i++) {
            st+=grid.getSelectionModel().getSelection()[i].get('nbase')+""+grid.getSelectionModel().getSelection()[i].get('a0100')+"~1,";
        }
        st=st.substring(0,st.length-1);
        return st;
    },
    getMsgcontent:function(){ //生成通知按钮的方法，成功后返回重新加载主页面
        var store=Ext.data.StoreManager.lookup("salaryaccountingsendmsg_dataStore");
        if(store.data.length==0){
            Ext.showAlert("没有可生成通知的人员！");
            return;
        }
        var templateId = Ext.getCmp('templatefieldId').getValue();
        var selectedid='-1';//选中的序号，格式为nbase+""+a0100~1
        if(templateId==null||templateId==''){
            //templateId='2';
            Ext.showAlert('请选择邮件模板！');
            return;
        }

        var type='0';//默认为未选中，为生成所有通知
        if(Ext.getCmp("salaryaccountingsendmsg_tablePanel").getSelectionModel().getSelection().length>0){
            type='1';//此情况下为选中
            selectedid = sendMsgScope.getSelectedId(Ext.getCmp("salaryaccountingsendmsg_tablePanel"));
        }
        var map = new HashMap();
        map.put("salaryid",salaryid);
        map.put("templateId",templateId);
        map.put("selectedid",selectedid);
        map.put("appdate",appdate);
        map.put("count",count);
        map.put("type",type);
        var datetime=sendMsgScope._appdate;
        if(datetime.length==10)
            datetime=datetime.substring(0,7);
        if(datetime!=sendMsgScope._iniAppdate){//若当前业务日期已经改变
            Ext.Msg.confirm(common.button.promptmessage,"仅可生成发放薪资当月的通知即"+sendMsgScope._iniAppdate+"，是否继续生成？&nbsp;",function( text){
                if(text=='yes') {
                    Ext.MessageBox.wait("<p style='text-align:center;'>生成邮件中......</p>", "等待");
                    Rpc({functionId: 'GZ00000143', async: true, success: sendMsgScope.getMsgcontentOK}, map);
                }
            });

        }else {
            Ext.MessageBox.wait("<p style='text-align:center;'>生成邮件中......</p>", "等待");
            Rpc({functionId: 'GZ00000143', async: true, success: sendMsgScope.getMsgcontentOK}, map);
        }
    },
    getMsgcontentOK:function(response){//重新加载主页面
        var result = Ext.decode(response.responseText);
        var flag=result.succeed;
        Ext.MessageBox.close();
        if(flag==true){
            var store = Ext.data.StoreManager.lookup('salaryaccountingsendmsg_dataStore');
            store.load();
        }else{
            //	Ext.MessageBox.alert(common.button.promptmessage,result.message);
            Ext.showAlert(result.message);
            var store = Ext.data.StoreManager.lookup('salaryaccountingsendmsg_dataStore');
            store.load();
        }
    },
    getMsgTemplate:function(id,subject,a0100){//点击主题一列的弹出窗口
        var map = new HashMap();
        map.put("salaryid",salaryid);
        map.put("id",id);
        map.put("a0100",getEncodeStr(a0100));
        Rpc({functionId:'GZ00000144',async:false,success:sendMsgScope.getMsgTemplateOK},map);
    },
    getMsgTemplateOK:function(response){//发送通知预览窗口
        var value = response.responseText;
        var map = Ext.decode(value);
        var subject=map.subject;//主题
        var content = map.content;//邮件主体
        if(content == null)
            content = '';
        var address = map.address;//发送到
        var a0101 = map.a0101;//当前人员
        var width = document.body.clientWidth;
        var height = document.body.clientHeight;
        var win=Ext.create('Ext.Window',{
            title:'发送给"'+a0101+'"的通知内容',
            id:'getMsgTemplateId',
            width: width*0.47,  //700
            height: height*0.72,  //650
            resizable: false,
            modal: true,
            items:[{
                layout:'column',
                xtype:'panel',
                border:false,
                items:[
                    //	{xtype:'label',text:'内容主体',style:'margin-left:20px;margin-top:5px'},
                    {
                        xtype:'panel',
                        labelSeparator:null,
                        //id:'contentId',
                        style:'margin-left:10px;padding-top:10px;',
                        html:'<div id="contentId" style="white-space: pre;"></div>',
                        labelAlign:'left',
                        labelWidth:60,
                        width:width*0.45,
                        height:height*0.55,
                        autoScroll:true
                    }
                ],
                listeners:{
                    'afterrender':function(){
                        //加载后页面中的组件为只读
                        //Ext.getCmp('contentId').setReadOnly(true);
                        //加载后为几个组件赋值
                        //Ext.getCmp('contentId').setValue(content);
                        //使图片等以html拼接的能够显示出来
                        var  reg = /<[^>]+>/g;
                        if(reg.test(content))
                            document.getElementById("contentId").innerHTML = content;
                        else
                            document.getElementById("contentId").innerText = content;
                    }
                }
            }],
            buttonAlign:'center',
            buttons:[{
                text:'关闭',
                listeners:{
                    'click':function(){
                        win.close();
                    }
                }
            }]

        });
        //如果当前人员不为空，才弹出消息明细的窗口
        if(a0101==null || a0101==''){
            return;
        }else{
            win.show();
        }
    },
    showSendMode:function(config){//打开发送类型选择窗口
        Ext.require('SalaryUL.SendMode',function(){
            Ext.create("SalaryUL.SendMode",{backFun:sendMsgScope.sendMsgDtl,dd_corpid:config.dd_corpid,corpid:config.corpid,mobile:config.mobile});
        })
    },
    sendMsgDtl:function(e_m_type,type){//发送通知ajax
        var ids;
        if (type==1){
            if(Ext.getCmp("salaryaccountingsendmsg_tablePanel").getSelectionModel().getSelection().length==0){
                Ext.showAlert(gz.msg.selectPerson);
                return;
            }
            ids=sendMsgScope.getSelect(Ext.getCmp("salaryaccountingsendmsg_tablePanel"),'personid');
            var _grid=Ext.getCmp("salaryaccountingsendmsg_tablePanel");
            var num=0;
            for (var i = 0; i < _grid.getSelectionModel().getSelection().length; i++) {
                if(_grid.getSelectionModel().getSelection()[i].get("send_ok")!=null && _grid.getSelectionModel().getSelection()[i].get("send_ok").length>0){
                    num++;
                }
            }
            if(num==0)
            {
                Ext.showAlert("需生成通知内容再执行发送操作!");
                return;
            }
        }
        else
            ids='';
        var templateId=Ext.getCmp('templatefieldId').getValue();
        if(templateId==null||templateId==''){
            Ext.showAlert('请选择邮件模板！');
            return;
        }
        Ext.MessageBox.wait("<p style='text-align:center;'>通知发送中......</p>", "等待");
        var map = new HashMap();
        map.put("salaryid",salaryid);
        map.put("ids",ids);//选中的数据，全部时为''
        map.put("templateId",templateId);
        map.put("e_m_type",e_m_type);
        map.put("type",type);
        map.put("Send_ok",sendMsgScope.Send_ok);//邮件状态
        map.put("appdate",appdate);
        map.put("count",count);
        map.put("isSend","1");// 1为发送信息 0为查询发送结果
        Rpc({functionId:'GZ00000145',async:true,success:function(response){

                var result = Ext.decode(response.responseText);
                if(result.succeed) {
                    var count = result.count;
                    if (parseInt(result.n) == 1 || result.flag == true) {//发送正常的情况下
                        if (e_m_type != "0") {
                            var wait = 0;
                            if (count < 10)
                                wait = 4000;
                            else
                                wait = 6000;
                            var wait = setTimeout(function () {
                                Ext.MessageBox.close();
                                sendMsgScope.sendMsgOK(response);
                            }, wait);
                        } else {
                            sendMsgScope.getSendStatus(ids,templateId,type,count,0,10);
                        }
                    }else{
                        Ext.MessageBox.close();
                        sendMsgScope.sendMsgOK(response);
                    }
                }else{
                    Ext.MessageBox.close();
                    Ext.showAlert(result.message);
                }
            }
        },map);
    },
    //轮询获取邮件是否发送完成 zhanghua
    //ids 所选记录，templateId 邮件模板号,type 邮件发送方式，count总条数,num 当前轮询次数，maxNum最大轮询次数
    getSendStatus:function(ids,templateId,type,count,num,maxNum){
        var wait = 3000;//每次等待3秒
        if(num<=maxNum) {
            num++;
            setTimeout(function () {
                map = new HashMap();
                map.put("salaryid", this.salaryid);
                map.put("ids", ids);//选中的数据，全部时为''
                map.put("templateId", templateId);
                map.put("type", type);
                map.put("Send_ok", sendMsgScope.Send_ok);//邮件状态
                map.put("appdate", appdate);
                map.put("count", count);
                map.put("isSend", "0");
                Rpc({
                    functionId: 'GZ00000145', async: false, success: function (response1) {
                        var result1 = Ext.decode(response1.responseText);
                        if (result1.succeed) {
                            if (result1.isOk == "1") {
                                Ext.MessageBox.close();
                                sendMsgScope.sendMsgOK(response1);
                            } else {
                                sendMsgScope.getSendStatus(ids,templateId,type,count,num,maxNum);
                            }
                        } else {
                            Ext.MessageBox.close();
                            Ext.showAlert(result1.message);
                        }
                    }
                }, map);
            }, wait);
        }else{
            Ext.MessageBox.close();
            Ext.showAlert("发送耗时较长，请稍后重新查询发送状态，请勿尝试多次发送邮件！");
            var store = Ext.data.StoreManager.lookup('salaryaccountingsendmsg_dataStore');
            store.reload();
        }
    },
    sendMsgOK:function(response){//发送通知回调函数

        var value = response.responseText;
        var map = Ext.decode(value);
        var n = map.n;
        var code = map.code;
        var e_m_type = map.e_m_type;
        var flag = map.flag;
        if( map.succeed){
            if(parseInt(n)==2&&parseInt(e_m_type)==0)
            {
                Ext.Msg.alert(common.button.promptmessage,"邮件发送失败,请检查邮箱设置和网络连接");
                return;
            }else if(parseInt(n)==2&&parseInt(e_m_type)==1)
            {
                Ext.Msg.alert(common.button.promptmessage,"短信发送失败,请检查电话指标设置和网络连接");
                return;
            }else if(parseInt(n)==2&&parseInt(e_m_type)==2){
                Ext.Msg.alert(common.button.promptmessage,"微信发送失败,请检查网络连接");
                return;
            }else if(parseInt(n)==3&&parseInt(e_m_type)==3){
                Ext.Msg.alert(common.button.promptmessage,"钉钉消息发送失败,请检查网络连接");
                return;
            }
            if(parseInt(n)==1 || flag == true)
            {
                Ext.Msg.alert(common.button.promptmessage,'消息发送成功');
            }
//							var store = Ext.data.StoreManager.lookup('salaryaccountingsendmsg_dataStore');
//							store.reload();
        }else
        {
            Ext.showAlert(map.message);
        }
        var store = Ext.data.StoreManager.lookup('salaryaccountingsendmsg_dataStore');
        store.reload();
    },
    deleteMsg:function(){//删除历史记录
        if(Ext.getCmp("salaryaccountingsendmsg_tablePanel").getSelectionModel().getSelection().length<=0){
            Ext.showAlert("请选择记录！");
            return;
        }
        Ext.Msg.confirm(common.button.promptmessage,"确认要删除记录吗？",function(btn){
            if(btn=="yes"){
                // 确认触发，继续执行后续逻辑。
                //selectid选中的记录
                var selectid  = sendMsgScope.getSelect(Ext.getCmp("salaryaccountingsendmsg_tablePanel"),'personid');
                var templateId=Ext.getCmp('templatefieldId').getValue();
                if(templateId==null||templateId==''){
                    Ext.showAlert('请选择邮件模板！');
                    return;
                }
                var map = new HashMap();
                map.put("salaryid",salaryid);
                map.put("selectid",selectid);
                map.put("templateId",templateId);
                Rpc({functionId:'GZ00000146',async:false,success:sendMsgScope.deleteMsgOK},map);
            }
        });
    },
    deleteMsgOK:function(response){//删除后重新加载页面
        var value = response.responseText;
        var map = Ext.decode(value);
        var store = Ext.data.StoreManager.lookup('salaryaccountingsendmsg_dataStore');
        store.reload();
    },
    showSendOkImg:function(send_ok){//页面发送状态替换为图片的方法
        if(send_ok=='0'){
            send_ok='<img src="/images/new_module/icon_fbyjs.gif" border="0"/>';
        }
        if(send_ok=='1'){
            send_ok='<img src="/images/new_module/icon_tgsx.gif" border="0"/>';
        }
        if(send_ok=='2'){
            send_ok='<img src="/images/new_module/icon_wtgsx.gif" border="0"/>';
        }
        if(send_ok=='4'){
            send_ok='<img src="/images/icon_published.gif" border="0"/>';
        }
        return send_ok;
    },
    showMsgTemplate:function(value,metaData,Record){//点击"主题"一列时，弹出窗口，预览需要发送的消息明细
        var id=Record.data.aid;
        var subject=Record.data.subject;
        var a0100=Record.data.personid;
        subject='<a href="javascript:void(0);" onclick="sendMsgScope.getMsgTemplate(\''+id+'\',\''+subject+'\',\''+a0100+'\');"  >'+subject+'</a>';
        return subject;
    },
    showFilterColumn:function(){//发送状态一列过滤条件需要定义的数据
        var dataList = [{dataValue:'0',dataName:'未发'},{dataValue:'1',dataName:'成功'},{dataValue:'2',dataName:'未成功'}];
        return dataList;
    },
    loadStore:function(value){
        var templateId='';
        var map = new HashMap();
        sendMsgScope.Send_ok=value;
        templateId=Ext.getCmp('templatefieldId').getValue();

        if(templateId==null||templateId==''){
            Ext.showAlert('请选择邮件模板！');
            return;
        }
        sendMsgScope.sendValue = value;
        var params = new Object();
        params.salaryid=salaryid;
        params.appdate=appdate;
        params.init='init2';
        params.sendOk=value+"";
        params.templateId=templateId;
        params._appdate=sendMsgScope._appdate;
        params.subModuleId="salaryaccountingsendmsg";
        Ext.getCmp("salaryaccountingsendmsg_querybox").setCustomParams(params);

        map.put("templateId",templateId);
        map.put("salaryid",salaryid);
        map.put("appdate",appdate);
        map.put("count",count);
        map.put("_appdate",sendMsgScope._appdate);
        map.put("sendOk",value+"");
        map.put("init","init2");
        Rpc({functionId:'GZ00000141',success: function(form,action){
                var result = Ext.decode(form.responseText);
                if(result.succeed){
                    var store= sendMsgScope.msgTable.tablePanel.getStore();
                    store.reload();
                }else{
                    Ext.showAlert(result.message);
                }
            }},map);
    },
    click:function(){
        //点击window外
        Ext.get(document).on('click', function (evt, el){
            if(sendMsgScope.datewin&&el.id!='dateymid'&&el.id!='timetitle'&&el.id!='monthtitle'&&el.id!='jt'&&el.id!='btnleft'&&el.id!='btnright'){
                sendMsgScope.datewin.close();
            }
        });
        Ext.util.CSS.swapStyleSheet("theme1","/module/gz/tax/DateYm.css");
        if(sendMsgScope.datewin)
        {
            sendMsgScope.datewin.close();
        }
        var string = '';
        for(var i=1;i<13;i++){
            var mon = '';
            var month = '';
            if(i<10){
                mon = "&nbsp;"+i;
            }else{
                mon = i;
            }
            string+="<li ><a href='###' onclick='sendMsgScope.selectPeriodMonth("+i+")'>"+mon+"月</a></li>";
        };
        var ye = Ext.getDom("timetitle").innerHTML
        sendMsgScope.datewin=Ext.create('Ext.window.Window',
            {
                id:'win',
                header:false,
                resizable:false,
                x:Ext.get("monthtitle").getX()-115,
                y:Ext.get("monthtitle").getY()+20,
                width:210,
                height:115,
                html:
                "<div class='hj-wzm-clock dropdownlist' style='margin-top:0px' id='monthlist' >" +
                "<ul style='text-align:center'>" +
                "<span style='color:#549FE3;'>" +
                "<a  dropdownName='monthbox' href='javascript:sendMsgScope.yearchange(-1);'><img id='btnleft' dropdownName='monthbox' src='/workplan/image/left2.gif' /></a>" +
                "<span id='myeartitle'>"+ye+"</span>年  " +
                "<a  dropdownName='monthbox' href='javascript:sendMsgScope.yearchange(1);'><img id='btnright' dropdownName='monthbox' src='/workplan/image/right2.gif' /></a>" +
                "</span></ul>" +
                "<ul id='months'>" +string+
                "</ul></div>"
            });
        sendMsgScope.datewin.show();
    },
    //选择年份
    yearchange:function(ch){
        var year = Ext.getDom('myeartitle');
        year.innerHTML = Number(year.innerHTML)+ch;
    },
    //选择月份
    selectPeriodMonth:function(month){
        var year=Ext.getDom("myeartitle").innerHTML;
        Ext.get("timetitle").setHtml(year);
        Ext.get("monthtitle").setHtml(month);
        Ext.getCmp('win').close();
        if(month<10){
            month = "0"+month;
        }
        sendMsgScope._appdate = year+"-"+month
        sendMsgScope.loadStore(sendMsgScope.sendValue);
    }
});
