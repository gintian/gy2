Ext.define('EHR.templateSelector.TemplateSelector',{
    extend:'Ext.tree.Panel',
    requires:['EHR.extWidget.proxy.TransactionProxy'],
    xtype:'templateselector',
    dataType:2,//数据类型：1=业务模板，2=登记表
    multiSelect:false,
    selectValue:'',
    Static:'', //
    filterStatic:'',//
    rnameFlag:'',
    templateType:'',//选择业务模板分类 选择多个模板中间以，间隔    没值时默认显示全部模板 wangb 20180807
    childTemplateType:'',//
    rootVisible:false,
    initComponent:function(){
        this.store = {
            proxy:{
                type: 'transaction',
                functionId:'ZJ100000191',
                extraParams:{
                    dataType:this.dataType,
                    multiSelect:this.multiSelect,
                    templateType:this.templateType,
                    Static:this.Static,
                    filterStatic:this.filterStatic,
                    rnameFlag:this.rnameFlag,
                    childTemplateType:this.childTemplateType
                },
                reader: {
                    type: 'json',
                    root: 'child'
                }
            }
        };
        if(this.multiSelect)
            this.on('afteritemexpand',this.onExpandItem,this);
        this.callParent();
        this.getSelectionModel().on('beforeselect',function(me, record, index, eOpts){
            if(!record.data.leaf){
                return false;
            }
        },this);
    },
    onExpandItem:function(node){
        var selectValue = this.selectValue;
        selectValue=","+selectValue+",";
        node.eachChild(function(child){
            if(selectValue.indexOf(","+child.get("id")+",")!=-1)
                child.set("checked",true);

        });
    }

});