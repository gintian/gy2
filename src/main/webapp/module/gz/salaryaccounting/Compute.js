Ext.define('SalaryUL.Compute',{
    constructor:function(config){
        salaryid = config.salaryid;
        appdate = config.appdate;
        count = config.count;
        imodule = config.imodule;
        computeScope = this;
        computeScope.viewtype = config.viewtype;
        computeScope.detailsql = config.detailsql;
        computeScope.collectPoint = config.collectPoint;
        computeScope.selectID = config.selectID;
        this.init();
    },
    init:function()
    {
        var store = Ext.create('Ext.data.Store', {
            fields:['itemid','hzname','useflag'],
            proxy:{
                type: 'transaction',

                functionId:'GZ00000013',
                extraParams:{
                    salaryid:salaryid
                },
                reader: {
                    type: 'json',
                    root: 'data'
                }
            },
            autoLoad: true
        });
        var panel = Ext.create('Ext.grid.Panel', {
            store: store,
            width: 390,
            height: 425,
            columnLines:true,
            rowLines:true,
            bufferedRenderer:false,
            columns: [
                {
                    header:'<input name="formulaflag1" type=checkbox id=selall  onclick="computeScope.selectALL(this);" checked/>',
                    flex:10,
                    menuDisabled:true,
                    xtype:'templatecolumn',
                    align:'center',
                    tpl:'<input name="formulaflag" type=checkbox id={itemid} useflag={useflag} onclick="computeScope.alertUseFlag(this);"/>'
                },
                { text: '名称',menuDisabled:true,sortable:false, dataIndex: 'hzname',flex:90}
            ]
        });
        store.on('load',function(store,records,options){
            computeScope.selectFormula();
        });

        computeScope.win=Ext.widget("window",{
            title:'计算公式',
            height:500,
            width:400,
            layout:'fit',
            modal:true,
            resizable:false,
            closeAction:'destroy',
            items: [{
                xtype:'panel',
                border:false,
                items:[panel],
                buttons:[
                    {xtype:'tbfill'},
                    {
                        text:'计算',
                        handler:function(){
                            Ext.MessageBox.wait("正在计算，请稍候...", "提示信息");
                            var itemid = computeScope.computa();
                            var itemids=new Array();
                            for(var i=0;i<itemid.length;i++){
                                itemids[itemids.length]=itemid[i];
                            }
                            if(itemid.length==0){
                                Ext.MessageBox.close();
                                Ext.showAlert("请选择计算公式!");
                                return;
                            }
                            var map = new HashMap();
                            map.put("salaryid",salaryid);
                            map.put("itemids",itemids);
                            map.put("ym",appdate);
                            map.put("count",count);
                            map.put("viewtype",computeScope.viewtype);
                            map.put("detailsql",computeScope.detailsql);
                            map.put("collectPoint",computeScope.collectPoint);
                            map.put("selectID",computeScope.selectID);

                            if('~37lf~39zfDTYe~38PAATTP~33HJDPAATTP'==computeScope.viewtype||'yMs~36zFRuw~36IPAATTP~33HJDPAATTP'==computeScope.viewtype)
                            {
                                var selectData = salaryObj.tablePanel.getSelectionModel().getSelection(true);
                                if(selectData.length>0){
                                    var selectGzRecords = "";
                                    if(selectData.length>50)
                                    {
                                        Ext.showAlert("个别计算仅支持50人以下!");
                                        return;
                                    }

                                    for(var j=0;j<selectData.length;j++){
                                        selectGzRecords += selectData[j].data.a0100_e+"/";
                                        selectGzRecords += selectData[j].data.nbase1_e+"/";
                                        selectGzRecords += selectData[j].data.a00z0+"/";
                                        selectGzRecords += selectData[j].data.a00z1;
                                        selectGzRecords += "#";
                                    }
                                    map.put("selectGzRecords",selectGzRecords);
                                }
                            }
                            else //汇总审批
                            {
                                if(computeScope.detailsql&&computeScope.detailsql.length>0)
                                {
                                    var selectData = spCollectScope.salaryObj.tablePanel.getSelectionModel().getSelection(true);
                                    if(selectData.length>0){
                                        var selectGzRecords = "";
                                        for(var j=0;j<selectData.length;j++){
                                            selectGzRecords += selectData[j].data.a0100_e+"/";
                                            selectGzRecords += selectData[j].data.nbase1_e+"/";
                                            selectGzRecords += selectData[j].data.a00z0+"/";
                                            selectGzRecords += selectData[j].data.a00z1;
                                            selectGzRecords += "#";
                                        }
                                        map.put("selectGzRecords",selectGzRecords);
                                    }
                                }

                            }
                            
                            computeScope.win.close();
                            LRpc({functionId:'GZ00000014',timeout:10000000,success:function(form,action){
                                    Ext.MessageBox.close();
                                    var result = Ext.decode(form.responseText);
                                    var flag=result.succeed;
                                    if(flag==true){
                                        if(result.viewtype=="0"||result.viewtype=="2"){
                                            accounting.loadStore();
                                        }else if(result.viewtype=="1"){
                                            if(computeScope.detailsql&&computeScope.detailsql.length>0){
                                                spCollectScope.loadStore();
                                            }else{
                                                spCollectScope.reload();
                                            }
                                        }
                                    }else{//捕捉后台抛出的异常 throw new GeneralException(ResourceFactory.getProperty("xxxxx"));
                                        Ext.showAlert(result.message);
                                    }
                                }},map);
                        }
                    },
                    {
                        text:'取消',
                        handler:function(){
                            computeScope.win.close();
                        }
                    },
                    {xtype:'tbfill'}
                ]
            }]
        });
        computeScope.win.show();
    },
    alertUseFlag:function(obj){
        var useflag="0";
        if(obj.checked){
            var useflag="1";
        }
        var formulaflag = document.getElementsByName("formulaflag");
        var check=true;
        for(var i=0;i<formulaflag.length;i++){
            if(!formulaflag[i].checked)
                check=false;
        }
        var formulaflag1= document.getElementById("selall");
        formulaflag1.checked=check;


        var map = new HashMap();
        map.put("salaryid",salaryid);
        map.put("itemid",obj.id);
        map.put("flag",useflag);
        map.put("batch","0");
        Rpc({functionId:'GZ00000015',async:false},map);

    },
    selectALL:function(obj){
        var formulaflag = document.getElementsByName("formulaflag");
        for(var i=0;i<formulaflag.length;i++){
            formulaflag[i].checked=obj.checked;
        }
        if(obj.checked)
            computeScope.batch_set_valid("1")
        else
            computeScope.batch_set_valid("0")
    },
    selectFormula:function(){
        var formulaflag = document.getElementsByName("formulaflag");
        if(formulaflag.length!=0){
            for(var i=0;i<formulaflag.length;i++){
                if(formulaflag[i].getAttribute("useflag")=="true"){
                    formulaflag[i].checked=true;
                }else{
                    document.getElementById("selall").checked=false;
                }
            }
        }else{
            document.getElementById("selall").checked=false;
            document.getElementById("selall").setAttribute("disabled","disabled ");
        }
    },
    batch_set_valid:function(flag)
    {
        var map = new HashMap();
        map.put("salaryid",salaryid);
        map.put("flag",flag);
        map.put("batch","1");
        Rpc({functionId:'GZ00000015',async:false},map);
    },
    computa:function(){
        var records = new Array();
        var formulaflag = document.getElementsByName("formulaflag");
        for(var i=0;i<formulaflag.length;i++){
            if(formulaflag[i].checked){
                records[records.length]=formulaflag[i].id;
            }
        }
        return records;
    }
});
