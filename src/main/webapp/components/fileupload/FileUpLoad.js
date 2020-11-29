/**
 * 文件上传组件
 * 利用jQuery-File-Upload.js 控件上传文件
 * @author zhanghua
 * @version1.0
 * 2018年1月26日 14:42:37
 */
/*
使用jQuery-File-Upload.js 基于jquery的上传控件 v9.25.1
控件GitHub地址:https://github.com/blueimp/jQuery-File-Upload
Api 地址https://github.com/blueimp/jQuery-File-Upload/wiki
兼容：
Google Chrome
Apple Safari 4.0+
Mozilla Firefox 3.0+
Opera 11.0+
Microsoft Internet Explorer 6.0+

 1、单文件上传，显示形式类似form提交：
 Ext.create("SYSF.FileUpLoad",{
	//指定为单文件上传
	upLoadType:1,
	//加密过后的文件保存路径
	savePath:'xxxxxxxx',
	//渲染容器id
	renderTo:"uploadBox",
	//提示信息
	emptyText:"请输入文件路径或选择文件",
	//上传完成后回调方法
	success:function(files){
		var file = files[0];
		//参数格式
		file = {
			localname:'xxxx.txt',
			path:'xxxxxxxxxx',//文件加密路径
			filename:'xxxxxxx'//文件加密名称
		}
	},
	//回调方法scope对象
	callBackScope:window
});
 2、多文件上传,以表格方式显示文件列表：
  Ext.create("SYSF.FileUpLoad",{
	//指定为多文件上传
	upLoadType:2,
	//加密过后的文件保存路径
	savePath:'xxxxxxxxxx',

	//已有文件列表
	fileList:[{
		id:'xxxxxxxx',//必须唯一
		path:'xxxxxxx'//文件加密路径，如果为空，则不能删除和下载
		localname:'xxxx.text',//文件显示名称
		filename:'xxxxxxx',//文件加密名称
		size:'10MB', //文件大小
		fileType:'' //文件分类，视具体情况，非必填
	}],

	//文件类型，上传后可以对文件类型进行维护的可选类型
	fileTypeMapList:[
	{dataValue:'pri',dataName:'机密文件'},
	{dataValue:'pub',dataName:'公开文件'}
	],

	isDownload:false,//是否可以下载，默认为false
	isShowOrEdit:'0',//功能状态，0：只能浏览 1：能浏览、上传和删除


	//上传完成后回调方法
	success:function(files){
		var file = files[0];
		//参数格式
		file = {
			localname:'xxxx.txt',
			path:'xxxxxxxxxx',//文件加密路径
			filename:'xxxxxx',//文件加密名称
			id:'xxxxxxx',//文件id
			fileType:'pri',//文件分类
			size:'10MB'
		}
	},
	//回调方法scope对象
	callBackScope:window
});

 3、简单单文件上传，使用方法和参数和单文件上传一样，只是显示较为简单


 4、其他参数说明：
 	fileExt：可选文件，格式*.jpg;*.jpeg;*.gif等，默认为*.*
 	extControl:加密的可上传文件类型列表，如不填此参数，走默认白名单
*/
//Ext.Loader.setConfig({enabled: true});
Ext.Loader.loadScript({url:'/js/validate.js',scope:this});
Ext.Loader.loadScript({url:'/ajax/basic.js',scope:this});
Ext.Loader.loadScript({url:'/jquery/jquery-3.5.1.min.js',scope:this});
Ext.Loader.loadScript({url:'/jquery/jquery_ui/jquery.ui.widget.js',scope:this});
Ext.Loader.loadScript({url:'/components/fileupload/jQueryFileUpload/js/jquery.iframe-transport.js',scope:this});
Ext.util.CSS.swapStyleSheet('theme','/components/fileupload/jQueryFileUpload/css/jquery.fileupload.css');
//文件类型
var VfsFiletypeEnum={
    //多媒体
    multimedia:'multimedia',
    //文档
    doc:'doc',
    //培训课件
    videostreams:'videostreams',
    //分布集成
    asyn:'asyn',
    //其他
    other:'other'
};
//所属模块
var VfsModulesEnum={
    //组织机构
    JG:'JG',
    //能力素质
    NL:'NL',
    //员工管理
    YG:'YG',
    //招聘管理
    ZP:'ZP',
    //人事异动
    RS:'RS',
    //证照管理
    ZZ:'ZZ',
    //出国管理
    CG:'CG',
    //职称评审
    ZC:'ZC',
    //合同管理
    HT:'HT',
    //考勤休假
    KQ:'KQ',
    //绩效管理
    JX:'JX',
    //关键目标
    MB:'MB',
    //薪资管理
    GZ:'GZ',
    //保险管理
    BX:'BX',
    //培训管理
    PX:'PX',
    //文档管理
    WD:'WD',
    //报表管理
    BB:'BB',
    //自助服务
    FW:'FW',
    //内部竞聘
    JP:'JP',
    //问卷调查
    WJ:'WJ',
    //分步同步
    TB:'TB',
    //系统管理
    XT:'XT',
    //登记表
    DJ:'DJ',
    //花名册
    HM:'HM',
    //不用登录就可以下载的模块
    NOLOGIN:'NOLOGIN'
}
//文件所属类型
var VfsCategoryEnum={
    //人员文件
    personnel:'personnel',
    //机构文件
    unit:'unit',
    //岗位文件
    post:'post',
    //其他
    other:'other'
}
Ext.define("SYSF.FileUpLoad",{
    // 继承
    extend: "Ext.Component",
    //别名，方便引用
    xtype:"fileupload",
    emptyText : "请选择文件",
    // 按钮text
    buttonText:"浏览...",
    // 回调方法,上传成功时调用
    success:Ext.emptyFn,
    // 回调方法,上传时调用
    upLoading:Ext.emptyFn,
    // 上传进度
    uploadProgress:Ext.emptyFn,
    //回调方法，失败
    error:Ext.emptyFn,
    // 回调函数scope
    callBackScope:undefined,
    // 宽度
    width :600,
    // 高度
    height: 30,
    // 开发者使用此名称获取上传前的路径以及文件
    name:"uploadFile",
    // 上传组件类型，1为单文件上传，2为多文件上传,3上传菜单（可以将上传按钮渲染到任何地方，只有上传按钮）
    upLoadType:1,
    // 上传路径,默认保存的路径为临时目录，如果指定目录，将保存到指定的目录下(必须为服务器的绝对路径)。
    savePath:'',
    //单个上传文件的单位，默认为MB,还可以使用GB、KB
    fileSizeLimit:"50MB",
    // 文件的扩展名，限定上传文件的类型,默认是任意类型（*.*）,多个文件类型用分号隔开，例如*.jpg;*.jpeg;*.gif
    fileExt:"*.*",
    extControl:'',
    // // 需要传入的其他参数
    // postParams:{},
    // 文件类型的描述，默认为“文件类型”
    fileTypesDesc: "文件类型",
    // // 上传时需要禁用哪些按钮，
    // forbiddenButtons:[],
    // 返回值
    returnValue:undefined,
    //是否通过控件删除文件
    realDelete:true,
    //浏览按钮宽度
    btWidth:55,
    // 按钮高度
    btHeight:22,
    // 按钮上边距
    btMarginLeft:10,
    // 路径显示框的宽度
    readInputWidth:245,
    // 路径显示框的高度
    readInputHeight:22,
    // 进度条高度
    progressHeight:3,
    //进度条颜色
    progressColor:"green",
    // 文件列表,格式[{id:'',filename:'',localname:'',size:'',path:'',fileType:''},{}],isDownload为true时，path不能为空，path为空，则此文件不可以下载，path必须为服务器的绝对路径
    fileList:[],
    // 是否允许下载
    isDownload:false,

    //查看和编辑标识 1是编辑，0是查看 默认1编辑  只用于多文件上传  hej add 2016-04-21
    isShowOrEdit:1,

    loadFlag:true,

    //文件类型集合 [{fileType:fileTypeName},{}] 文件类型
    fileTypeMapList:null,

    //文件名称长度限制 默认100个字符即50个汉字
    fileNameMaxLength:100,

    //是否为临时文件 true是，false不是
    isTempFile:null,

    //关联VfsFiletypeEnum 文件类型 例：VfsFiletypeEnum.doc
    VfsFiletype:null,

    //关联VfsModulesEnum 模块id 例：VfsModulesEnum.CARD
    VfsModules:null,

    //关联VfsCategoryEnum 文件所属类型 例：VfsCategoryEnum.personnel
    VfsCategory: null,

    //所属类型guidkey
    CategoryGuidKey: null,

    //文件扩展标识（特殊情况才需要，平常可传空字符，不为空时，长度不得少于6位）
    filetag:'',

    onRender:function(){
        this.callParent(arguments);
        Ext.Loader.loadScript({
            url:'/components/fileupload/jQueryFileUpload/js/jquery.fileupload.js',
            onLoad:this.buildComp,
            scope:this
        });
    },

    buildComp:function(){
        var me = this;
        Ext.Loader.loadScript({url:'/components/fileupload/jQueryFileUpload/js/jquery.fileupload-process.js',scope:this});
        Ext.Loader.loadScript({url:'/components/fileupload/jQueryFileUpload/js/jquery.fileupload-validate.js',scope:this,onLoad:function(){
                if (me.upLoadType == 1) {
                    me.singleUpLoadInit()
                } else if (me.upLoadType == 2){
                    if(me.fileTypeMapList&&me.fileTypeMapList.length==0){//文件类型集合为【】时设置为null
                        me.fileTypeMapList=null;
                    }
                    me.muiltyUploadInit();
                } else if (me.upLoadType == 3) {
                    me.menuUploadInit();
                }
            }});
    },

    /**
     * 单文件上传初始化组件
     */
    singleUpLoadInit:function(){
        var me = this;
        // 浏览按钮
        me.browseBt = Ext.create("Ext.Panel",{
            //text:me.buttonText,
            width:me.btWidth,
            height:me.btHeight,
            html:"<span class='btn btn-success fileinput-button' style='width:"+me.btWidth+"px;height: "+me.btHeight+"px;' >" +
                "<span style='display:inline-block;width:"+me.btWidth+"px;height: "+me.btHeight+"px;text-align:center;'>"+me.buttonText+"</span>" +
                "<input id='fileupload' unselectable='on' type='file' style='width:"+me.btWidth+"px;height: "+me.btHeight+"px;'  name='files[]' multiple >" +
                " </span>",

            listeners:{
                render:function(){
                    $('#fileupload').fileupload({
                        dataType: 'json',
//                        url:'/components/fileupload/upload',
                        url:'/servlet/vfsservlet',
                        done: function (e, data) {//上传成功事件
                            var objValue = data.result;
                            if(objValue.successed) {
                                me.progress.ownerCt.remove(me.progress, false);//删除进度条
                                me.progressNum.hide();
                                me.info.update("文件上传成功");
                                me.info.show();
                                setTimeout(function () {
                                    me.info.hide();
                                }, 2000);
                            }else{
                                me.progress.ownerCt.remove(me.progress,false);//删除进度条
                                me.progressNum.hide();
                                // 插入文本消息
                                if(!data.msg) {
                                    data.msg="文件上传失败";
                                }
                                if(me.fucCheckLength(data.msg)>40){
                                    Ext.showAlert(data.msg);
                                }else {
                                    me.info.setWidth(250);
                                    me.info.update(data.msg);
                                    me.pathInput.setValue("");
                                    me.info.show();
                                    setTimeout(function(){me.info.hide();}, 2000);
                                }
                            }
                        },fail: function(e, data) {//服务器响应失败事件
                            me.progress.ownerCt.remove(me.progress,false);//删除进度条
                            me.progressNum.hide();
                            // 插入文本消息
                            if(!data.msg) {
                                data.msg="文件上传失败";
                            }
                            if(me.fucCheckLength(data.msg)>40){
                                Ext.showAlert(data.msg);
                            }else {
                                me.info.setWidth(250);
                                me.info.update(data.msg);
                                me.pathInput.setValue("");
                                me.info.show();
                                setTimeout(function(){me.info.hide();}, 2000);
                            }

                        },always: function(e, data) {//上传完成事件，无论成功或失败
                            if(data.textStatus='success') {
                                var filesize = (me.filesize / (1024 * 1024)).toFixed(2);
                                var sizeStr = filesize + "MB";
                                if (filesize == 0.00) {
                                    sizeStr = (me.filesize / (1024)).toFixed(2) + "KB";
                                }
                                data.result.size = sizeStr;
                                data.result.localname = data.originalFiles[0].name;
                            }

                            if(data.result.successed){
                                if (me.callBackScope) {
                                    Ext.callback(me.success, me.callBackScope,[[data.result]]);
                                } else {
                                    Ext.callback(me.success,null,[[data.result]]);
                                }
                            }else{
                                if(me.error) {
                                    if (me.callBackScope) {
                                        Ext.callback(me.error, me.callBackScope, [[data.result]]);
                                    } else {
                                        Ext.callback(me.error, null, [[data.result]]);
                                    }
                                }else{
                                    Ext.showAlert(data.result.msg);
                                }
                            }

                        },progress: function(e, data) {//进度条回调
                            var progress = parseInt(data.loaded / data.total * 100, 10);
                            me.greenPanel.setWidth(progress*me.readInputWidth);
                            me.progressNum.update(progress + "%");
                        },add:function(e,data){//文件加入队列事件
                            var filesize=0;
                            me.filesize=0;
                            var name=data.files[0].name;
                            var fileType=name.substring(name.lastIndexOf(".")).toLowerCase();
                            if(me.fileExt!=""&&me.fileExt!="*.*"&&me.fileExt.toLowerCase().indexOf("*"+fileType)==-1) {//文件类型判断
                                if (me.fileExt.length > 17) {
                                    Ext.showAlert("请上传 "+me.fileExt.replace(";",",")+" 类型的文件！");
                                } else
                                {
                                    me.info.setWidth(250);
                                    me.info.update("上传失败,请上传 " + me.fileExt.replace(";",",") + " 类型的文件！");
                                    me.info.show();
                                    me.pathInput.setValue("");
                                    setTimeout(function () {
                                        me.info.hide();
                                    }, 2000);
                                }
                                return false;

                            }

                            try {//获取文件大小
                                if (Ext.isIE) {
                                    var fso = new ActiveXObject("Scripting.FileSystemObject");
                                    var realpath = data.fileInput[0].value;
                                    filesize = fso.GetFile(realpath).size;
                                } else {
                                    filesize = data.originalFiles[0]['size'];
                                }
                            }catch (e) {
                                //Ext.showAlert("若使用IE浏览器上传文件，请在安全设置中启用ActiveX控件！");
                            }
                            var maxSize=0;
                            if(me.fileSizeLimit==undefined ||me.fileSizeLimit=='')
                                me.fileSizeLimit="50mb";
                            if(me.fileSizeLimit.toLowerCase().indexOf("mb")>-1){
                                maxSize=me.fileSizeLimit.substring(0,me.fileSizeLimit.length-2);
                                maxSize=maxSize*1024*1024;
                            }else if(me.fileSizeLimit.toLowerCase().indexOf("gb")>-1){
                                maxSize=me.fileSizeLimit.substring(0,me.fileSizeLimit.length-2);
                                maxSize=maxSize*1024*1024*1024;
                            }else if(me.fileSizeLimit.toLowerCase().indexOf("kb")>-1){
                                maxSize=me.fileSizeLimit.substring(0,me.fileSizeLimit.length-2);
                                maxSize=maxSize*1024;
                            }
                            data.maxFileSize=maxSize;
                            me.filesize=filesize;
                            if(filesize> maxSize) {//文件大小判断
                                me.info.setWidth(250);
                                me.info.update("上传失败,上传文件不能大于"+me.fileSizeLimit+"！");
                                me.info.show();
                                me.pathInput.setValue("");
                                setTimeout(function(){me.info.hide();}, 2000);
                                return false;
                            }
                            me.pathInput.setValue(name);
                            //插入进度条时路径框高度不足
                            me.mixInputProgress.setHeight(me.mixInputProgress.getHeight()+me.progressHeight);
                            me.mixInputProgress.insert(0, me.progress);
                            me.greenPanel.setWidth("0%");
                            me.progressNum.update("0%");
                            me.progressNum.show();

                            data.submit();

                        }
                        // ,start:function(e){
                        //
                        // }
                        // ,change:function (e,data) {
                        //
                        // }
                    });
                    $('#fileupload').bind('fileuploadsubmit', function (e, data) {//传到后台的参数
                        data.formData=[
                            {name:'extControl',value:me.extControl},
                            {name:'maxFileSize',value:data.maxFileSize},
                            {name:'savePath',value:me.savePath},
                            {name:'fileNameMaxLength',value:me.fileNameMaxLength},
                            {name:'fileName',value:getEncodeStr(data.files[0].name)},
                            //vfs新增参数
                            {name:'isTempFile',value:me.isTempFile},
                            {name:'VfsFiletype',value:me.VfsFiletype},
                            {name:'VfsModules',value:me.VfsModules},
                            {name:'VfsCategory',value:me.VfsCategory},
                            {name:'CategoryGuidKey',value:me.CategoryGuidKey},
                            {name:'filetag',value:me.filetag}];
                    });
                }
            }
        });

        me.buttonSplit = me.progressNum = Ext.create("Ext.Panel",{
            height: me.btHeight,
            width:me.btMarginLeft,
            border:0
        });

        // 路径显示框（只读）
        me.pathInput = Ext.create("Ext.form.field.Text", {
            name:me.name + "_old",
            width:me.readInputWidth,
            height:me.readInputHeight-3,
            readOnly:true,
            emptyText:me.emptyText,  //暂时去掉
            padding:"0 0 0 0"

        });

        // 上传后的路径保存框（隐藏）
        me.pathHidden = Ext.create("Ext.form.field.Hidden", {
            name:me.name + "_new"

        });

        // 显示进度的greenpanel
        me.greenPanel = Ext.create("Ext.Panel",{
            height:me.progressHeight,
            width:'0%',
            id:'',
            border:0,
            bodyStyle: {
                background: me.progressColor
            }

        });

        me.progressNum = Ext.create("Ext.Panel",{
            height:15,
            width:35,
            header:false,
            floating:true,
            shadow:false,
            border:0,
            style:{
                zIndex:9999999
            },
            listeners:{
                render:function(){
                    this.setPosition(me.readInputWidth/2 -15,-18);
                }
            }

        });

        me.info = Ext.create("Ext.Panel",{
            height:15,
            width:100,
            border:0,
            floating:true,
            shadow:false,
            html:"",
            style:{
                zIndex:9999999
            },
            listeners:{
                render:function(){
                    this.setPosition(0,-15);
                }
            }

        });

        // 进度条
        me.progress = Ext.create("Ext.Panel",{
            height:me.progressHeight,
            width:me.readInputWidth,
            border:1,
            bodyStyle:{
                "border-bottom":"0px"
            },
            items:[me.greenPanel,me.progressNum],
            style:{
                zIndex:0
            }
        });

        // 只读路径框与进度条
        me.mixInputProgress = Ext.create("Ext.Panel",{
            height:me.readInputHeight,
            width:me.readInputWidth,
            border:0,
            layout:{
                type:"vbox",
                align:"bottom"

            },
            items:[me.pathInput,me.info]
        });

        // 整体控件
        me.mypanel = Ext.create("Ext.Panel", {
            width: me.width ,
            height: me.height,
            border:0,
            layout:{type:"hbox", align:"bottom"},
            renderTo: this.el,
            items: [me.mixInputProgress,me.buttonSplit,me.browseBt,me.pathHidden],
            style:{
                zIndex:1
            }
        });
    },
    /**
     * 多文件上传初始化组件 2016-4-21 hej 添加查看和编辑区分显示（参数 isShowOrEdit）
     */
    muiltyUploadInit:function(){
        var me = this;
        if (me.buttonText == "" || me.buttonText == "浏览...") {
            me.buttonText = "上传文件";
        }

        // if (me.btWidth == 45) {
        //     me.btWidth = 60;
        // }
        me.btWidth=38;

        if (me.height == 30) {
            me.height = 300;
        }
        var hiddenDel = false;
        var a= 0;
        if(me.isShowOrEdit==0){//查看
            hiddenDel = true;
            a=0.18;
        }else{
            a=0.1;
        }

        // 全选框
        me.selectAllCheck = Ext.create("Ext.form.field.Checkbox",{
            inputId:'selectAll',
            name:"selectAll",
            margin:'3 0 0 5',
            /**handler:function(f,checked){
				me.dataPanel.items.each(function(obj){
					obj.items.items[0].items.items[0].setValue(checked);
				});
			},**/
            listeners:{
                render:function(){
                    this.getEl().on("click",function(){
                        var h = this;
                        me.dataPanel.items.each(function(obj){
                            obj.items.items[0].items.items[0].setValue(h.getValue());
                        });
                    },this);
                }
            }
        });
        /**
         me.headPanelCont = Ext.create('Ext.Panel', {
            header:false,
            height: 30,
            columnWidth: 1,
            border:1,
            layout:{
                type:'column',
                align:"center"
            },
            bodyStyle:{
                "border-top":"solid 0px",
                "border-right":"solid 0px",
                "border-left":"solid 0px",
                "border-bottom":"solid 0px"
            },
            items:[{
                xtype:"panel",
                border:0,
                width:24,
                height:30,
                layout:{
                    type:"hbox",
                    align:"middle"
                },
                items:[me.selectAllCheck],
                bodyStyle:{
                    "text-align":"center",
                    "line-height":"30px"
                }
            },
                {
                    xtype:"panel",
                    border:1,
                    layout:{
                        type:"hbox",
                        align:"middle"
                    },
                    bodyStyle:{
                        "text-align":"center",
                        "line-height":"30px",
                        "border-top":"solid 0px",
                        "border-right":"solid 0px",
                        "border-bottom":"solid 0px"
                    },
                    html:"文件名称",
                    columnWidth: (me.fileTypeMapList!=null)?0.15+a:0.25+a,
//		        width:(me.width - 24) * 0.35,
                    height:30

                },{//changxy
                    xtype:"panel",
                    border:1,
                    html: '文件类型',
                    columnWidth: 0.15,
                    hidden:(me.fileTypeMapList!=null)?false:true,
//              width:(me.width - 24) * 0.15,
                    bodyStyle:{
                        "text-align":"center",
                        "line-height":"30px",
                        "border-top":"solid 0px",
                        "border-right":"solid 0px",
                        "border-bottom":"solid 0px"
                    },
                    height:30
                },{
                    xtype:"panel",
                    border:1,
                    html: '文件大小',
                    columnWidth: (me.fileTypeMapList!=null)?0.1:0.15,
//		        width:(me.width - 24) * 0.15,
                    bodyStyle:{
                        "text-align":"center",
                        "line-height":"30px",
                        "border-top":"solid 0px",
                        "border-right":"solid 0px",
                        "border-bottom":"solid 0px"
                    },
                    height:30
                },{
                    xtype:"panel",
                    border:1,
                    html: '上传进度',
                    columnWidth: 0.3,
//		        width:(me.width - 24) * 0.3,
                    bodyStyle:{
                        "text-align":"center",
                        "line-height":"30px",
                        "border-top":"solid 0px",
                        "border-right":"solid 0px",
                        "border-bottom":"solid 0px"
                    },
                    height:30
                },{
                    xtype:"panel",
                    border:1,
                    html: '状态',
                    columnWidth: 0.1,
//		        width:(me.width - 24) * 0.1,
                    bodyStyle:{
                        "text-align":"center",
                        "line-height":"30px",
                        "border-top":"solid 0px",
                        "border-right":"solid 0px",
                        "border-bottom":"solid 0px"
                    },
                    height:30
                },{
                    xtype:"panel",
                    border:1,
                    hidden:hiddenDel,
                    html: '删除',
                    columnWidth: 0.1,
//		        width:(me.width - 24) * 0.1,
                    bodyStyle:{
                        "text-align":"center",
                        "line-height":"30px",
                        "border-top":"solid 0px",
                        "border-right":"solid 0px",
                        "border-bottom":"solid 0px"
                    },
                    height:30
                },{xtype:'box',hidden:true,html:'文件地址',height:30}]

        });
         // 上传文件列表表头
         me.headerPanel = Ext.create('Ext.Panel', {
            header:false,
            border:0,
            height: 30,
            width:me.width-14,

//		    columnWidth: 1,
//		    overflowY:'auto',
            layout:{
                type:'column',
                align:"center"
            },
            bodyStyle:{
                "border-top":"solid 0px",
                "border-right":"solid 0px",
                "border-left":"solid 0px"
            },
            items: [me.headPanelCont]

        });
         */
        /*bug:50179 有滚动条时表头与内容对不齐。表头太复杂了，重构一下，简化html结构 guodd 2019-07-17*/
        me.headerPanel = Ext.widget('container',{
            height:30,
            layout:{type:'column'},
            defaults:{
                style:'border-left: 1px solid #c5c5c5;text-align:center;line-height:30px'
            },
            items:[{
                xtype:'container',width:24,style:'line-height:30px;',
                items:me.selectAllCheck
            },{
                xtype:'box',
                columnWidth:1,
                height:30,html:'文件名称'
            },{
                xtype:'box',width:80,height:30,html:'文件类型',hidden:(me.fileTypeMapList!=null)?false:true
            },{
                xtype:'box',width:60,height:30,html:'文件大小'
            },{
                xtype:'box',width:150,height:30,html:'上传进度'
            },{
                xtype:'box',width:60,height:30,html:'状态'
            },{
                xtype:'box',width:60,height:30,html:'删除',hidden:hiddenDel
            }]


        })
        // 上传文件列表
        me.dataPanel = Ext.create('Ext.Panel', {
            header: false,
            border: 1,
            layout:{type:'vbox',align:'stretch'},
            bodyStyle: {
                "border-top": "solid #c5c5c5 1px",
                "border-right": "solid 0px",
                "border-left": "solid 0px",
                "border-bottom": "solid 0px"
            }
        });


        /*if (me.dataPanel.items.items.length <= 6) {
            me.dataPanel.setWidth(me.width);
            me.headerPanel.setWidth(me.width);
        }*/


        me.upbutton = Ext.create("Ext.Panel",{//上传按钮
            //text:me.buttonText,
            width:me.btWidth,
            height:me.btHeight,
            html:"<span class='btn btn-success fileinput-button' style='cursor:pointer;background:#f9f9f9; width:"+me.btWidth+"px;height: "+me.btHeight+"px;' onmouseover=\"this.style.backgroundColor='#e2e1e1';\" onmouseout=\"this.style.backgroundColor='#f9f9f9';\">" +
                "<span style='display:inline-block;width:"+me.btWidth+"px;height: "+me.btHeight+"px;padding-left: 6px;padding-top: 2px'>"+me.buttonText+"</span>" +
                "<input id='fileupload' unselectable='on' type='file' style='width:"+me.btWidth+"px;height: "+me.btHeight+"px;'  name='files[]' multiple >" +
                " </span>",

            listeners:{
                render:function(){
                    $('#fileupload').fileupload({
                        dataType: 'JSON',
//                        url:'/components/fileupload/upload',
                        url:'/servlet/vfsservlet',
                        multipart:true,
                        done: function (e, data) {//上传成功事件
                            var objValue = data.result;
                            if(objValue.successed) {//上传成功
                                me.dataPanel.items.each(function (obj) {
                                    if (obj.panelId == "panel" + objValue.fileListId) {
                                        obj.returnValue = objValue;
                                        obj.flag = '1';
                                        var count = 0;
                                        var tem = obj.items.items[1].localName;
                                        //重命名文件名，若上传了相同的文件，则将后面的加上(1)的后缀
                                        me.dataPanel.items.each(function (o) {
                                            var t = o.items.items[1].localName;
                                            if (t == tem || (t.indexOf("(") != -1 &&
                                                (t.substring(0, t.lastIndexOf("(")) + t.substring(t.lastIndexOf(")") + 1)) == tem))
                                                count += 1;
                                        });
                                        if (count > 1)
                                            obj.returnValue.localname = tem.substring(0, tem.lastIndexOf(".")) + "(" + (count - 1) + ")" + tem.substring(tem.lastIndexOf("."));
                                        else
                                            obj.returnValue.localname = tem;
                                        //(me.fileTypeMapList!=null) 有文件类型时 第三列后的下标后移一位 changxy
                                        if (me.fileTypeMapList && me.filelist) {//fileList.fileType
                                            var filetype = obj.items.items[2].items.items[0].items.items[0];
                                            obj.returnValue.fileType = filetype.getValue();
                                        }
                                        /*start 多文件上传 添加文件大小回传   add  hej  2016-4-21     */
                                        var size = obj.items.items[3].getEl().dom.innerText;
                                        obj.returnValue.size = size;
                                        /*end 多文件上传 添加文件大小回传   add  hej  2016-4-21       */

                                        // 进度条设置为100%
                                        var tt = obj.items.items[4].items.items[0];
                                        tt.items.items[0].setWidth("100%");
                                        //更新进度数字
                                        obj.items.items[4].items.items[1].update("100%");

                                        // 状态图片
                                        obj.items.items[5].items.items[0].setSrc("/components/fileupload/imags/gou.png");
                                        //添加删除图片
                                        var tt = obj.items.items[6].items.items[0];
                                        tt.setSrc("/images/del.gif");

                                        tt.getEl().on("click", function () {// 添加删除事件
                                            Ext.Msg.show({
                                                title: "提示信息",
                                                msg: "确认删除所选记录？",
                                                buttons: Ext.Msg.YESNO,
                                                fn: function (btn) {
                                                    if (btn == 'yes') {
                                                        tt.ownerCt.ownerCt.ownerCt.remove(tt.ownerCt.ownerCt);
                                                        //文件假删除
//                                                        me.deleImgHandler(me.realDelete);
                                                        if(me.realDelete == true){
                                                            me.deleteRealFile(objValue.fileid);
                                                        }
                                                    } else {
                                                        return;
                                                    }
                                                },
                                                icon: Ext.MessageBox.QUESTION
                                            });

                                        }, tt);
                                        // 添加勾选框
                                        me.addCheckbox(obj, objValue.id);
                                        // 下载

                                        obj.items.items[1].update(me.muilty_filename_handler(obj.returnValue.localname, objValue.path, objValue.filename));

                                    }
                                });
                            }else{//上传失败
                                me.dataPanel.items.each(function(obj){
                                    if (obj.panelId =="panel" +objValue.fileListId) {
                                        // 状态图片
                                        obj.items.items[5].items.items[0].setSrc("/components/fileupload/imags/tan.png");
                                        // 添加错误信息
                                        var errormsg = '';
                                        if(objValue.msg)
                                            errormsg = objValue.msg;
                                        else
                                            errormsg="上传失败";
                                        //取消图片提示信息 修改为弹窗 changxy 20170109
                                        Ext.showAlert(errormsg);
                                        //添加删除图片
                                        var tt = obj.items.items[6].items.items[0];
                                        tt.setSrc("/images/del.gif");

                                        tt.getEl().on("click", function () {// 添加删除事件
                                            tt.ownerCt.ownerCt.ownerCt.remove(tt.ownerCt.ownerCt);
                                            me.deleImgHandler(me.realDelete);

                                        }, tt);

                                        // 添加勾选框
                                        me.addCheckbox(obj, objValue.id);

                                    }
                                });

                            }
                        },
                        fail: function(e, data) {
                            /*var objValue = Ext.decode(me.getDecodeStr(data.jqXHR.responseText));
                            var isSuccess = (objValue.successed?true:false);
                            if(!isSuccess){非ie浏览器可以拿到jqXHR.responseText属性，ie下拿不到，暂时找不到办法。*/
                            me.dataPanel.items.each(function(obj){
                                if (obj.panelId =="panel" +data.fileListId) {
                                    // 状态图片
                                    obj.items.items[5].items.items[0].setSrc("/components/fileupload/imags/tan.png");
                                    //添加删除图片
                                    var tt = obj.items.items[6].items.items[0];
                                    tt.setSrc("/images/del.gif");
                                    tt.getEl().on("click", function () {// 添加删除事件
                                        Ext.Msg.show({
                                            title: "提示信息",
                                            msg: "确认删除所选记录？",
                                            buttons: Ext.Msg.YESNO,
                                            fn: function (btn) {
                                                if (btn == 'yes') {
                                                    tt.ownerCt.ownerCt.ownerCt.remove(tt.ownerCt.ownerCt);
                                                    me.deleImgHandler(me.realDelete);
                                                } else {
                                                    return;
                                                }
                                            },
                                            icon: Ext.MessageBox.QUESTION
                                        });

                                    }, tt);
                                }
                            });
                            //Ext.showAlert(objValue.msg);
                            // }
                            Ext.showAlert("未知错误,上传失败！");
                        },
                        progress: function(e, data) {//进度条回调
                            var progress = parseInt(data.loaded / data.total * 100, 10);
                            var name=data.files[0].name;
                            me.dataPanel.items.each(function(obj){
                                if (obj.panelId =="panel" + getEncodeStr(name)) {
                                    // 更新进度
                                    var tt = obj.items.items[4].items.items[0];
                                    var percent = progress * tt.getWidth();
                                    tt.items.items[0].setWidth(percent);

                                    //更新进度数字
                                    obj.items.items[4].items.items[1].update(progress + "%");

                                    return false;
                                }
                            });
                        },
                        add:function(e,data){//文件加入上传队列事件
                            var filesize=0;
                            var name=data.files[0].name;
                            var fileType=name.substring(name.lastIndexOf(".")).toLowerCase();
                            if(me.fileExt!=""&&me.fileExt!="*.*"&&me.fileExt.toLowerCase().indexOf("*"+fileType)==-1){//文件类型判断
                                Ext.showAlert("请上传 "+me.fileExt.replace(";",",")+" 类型的文件！");
                                return false;
                            }

                            try {
                                if (Ext.isIE) {
                                    var fso = new ActiveXObject("Scripting.FileSystemObject");//获取文件大小
                                    var realpath = data.fileInput[0].value;
                                    filesize = fso.GetFile(realpath).size;
                                } else {
                                    filesize = data.originalFiles[0]['size'];
                                }
                                var filesize_ = (filesize/(1024*1024)).toFixed(2);
                                var sizeStr = filesize_ + "MB";
                                if (filesize_ == 0.00) {
                                    sizeStr = (filesize/(1024)).toFixed(2) + "KB";
                                }
                            }catch (e) {
                                //Ext.showAlert("若使用IE浏览器上传文件，请在安全设置中启用ActiveX控件！")
                            }
                            var maxSize=0;
                            if(me.fileSizeLimit==undefined ||me.fileSizeLimit=='')
                                me.fileSizeLimit="50mb";
                            if(me.fileSizeLimit.toLowerCase().indexOf("mb")>-1){
                                maxSize=me.fileSizeLimit.substring(0,me.fileSizeLimit.length-2);
                                maxSize=maxSize*1024*1024;
                            }else if(me.fileSizeLimit.toLowerCase().indexOf("gb")>-1){
                                maxSize=me.fileSizeLimit.substring(0,me.fileSizeLimit.length-2);
                                maxSize=maxSize*1024*1024*1024;
                            }else if(me.fileSizeLimit.toLowerCase().indexOf("kb")>-1){
                                maxSize=me.fileSizeLimit.substring(0,me.fileSizeLimit.length-2);
                                maxSize=maxSize*1024;
                            }
                            data.maxFileSize=maxSize;
                            if(filesize> maxSize) {//文件大小判断
                                Ext.showAlert("上传文件不能大于"+me.fileSizeLimit+"！");
                                return false;
                            }
                            var count=0;
                            me.dataPanel.items.each(function(o){
                                var t = o.items.items[1].localName;
                                if(t==name && t.indexOf("(")==-1 || (t.indexOf("(")!=-1 &&
                                    (t.substring(0,t.lastIndexOf("("))+t.substring(t.lastIndexOf(")")+1)) == name))
                                    count += 1;
                            });
                            if(count>0)
                                name = name.substring(0,name.lastIndexOf("."))+"("+count+")"+name.substring(name.lastIndexOf("."));
                            data.fileListId=me.uuid();
                            me.dataPanel.add(me.createFileList(data.fileListId,0,name,sizeStr,"0%",false,false));
                            data.submit();
                        }
                    });
                    $('#fileupload').bind('fileuploadsubmit', function (e, data) {//向后台传递的参数
                        data.formData=[{name:'fileListId',value:data.fileListId},
                            {name:'extControl',value:me.extControl},
                            {name:'maxFileSize',value:data.maxFileSize},
                            {name:'savePath',value:me.savePath},
                            {name:'fileNameMaxLength',value:me.fileNameMaxLength},
                            {name:'fileName',value:getEncodeStr(data.files[0].name)},
                            //vfs新增参数
                            {name:'isTempFile',value:me.isTempFile},
                            {name:'VfsFiletype',value:me.VfsFiletype},
                            {name:'VfsModules',value:me.VfsModules},
                            {name:'VfsCategory',value:me.VfsCategory},
                            {name:'CategoryGuidKey',value:me.CategoryGuidKey},
                            {name:'filetag',value:me.filetag}];
                    });
                }
            }
        });
        me.delbutton = Ext.create("Ext.button.Button",{
            text:"<font color='black'>删除</font>",
            margin:'5 0 0 20',
            handler:function(){
                var temp = 0;
                var totalRecords = me.dataPanel.items.length;
                if(totalRecords>0){
                    me.dataPanel.items.each(function(obj){
                        var tt = obj.items.items[0].items.items[0];
                        if (!tt.getValue())
                            temp += 1;
                    });
                    if(temp == totalRecords)
                        Ext.showAlert("请选择需要删除的记录!");
                    else
                        Ext.Msg.show({
                            title:"提示信息",
                            msg: "确认删除所选记录？",
                            buttons: Ext.Msg.YESNO,
                            fn: function(btn){
                                if(btn!='yes')
                                    return;
                                else
                                    me.dataPanel.items.each(function(obj){
                                        var tt = obj.items.items[0].items.items[0];
                                        if (tt.getValue()) {
                                            var path = obj.returnValue.path;
                                            obj.ownerCt.remove(obj);
                                            //批量删除记录时不传filename 无法获取fileid
//                                            me.deleImgHandler(me.realDelete,path,obj.returnValue.filename);
                                            me.deleteRealFile(obj.fileid);
                                            if(me.selectAllCheck.getValue())//全选时 取消全选
                                                me.selectAllCheck.setValue(false);//
                                        }
                                    });
                            },
                            icon: Ext.MessageBox.QUESTION
                        });
                }
                else{
                    Ext.showAlert('请选择需要删除的记录!');
                }
            }
        });
        me.conformbutton = Ext.create("Ext.button.Button",{
            text:"<font color='black'>确定</font>",
            margin:'5 0 0 20',
//    		height:20,
            handler:function(){
                //me.closeHandler(null);

                me.mywindow.close();
            }
        });
        me.buttons = Ext.create('Ext.Panel', {
            header:false,
            //width:200,
            columnWidth: 1,
            height:30,
            border:0,
            margin:'0 0 0 0',
            layout:{
                type:'hbox',
                align:'center'
            },items:[{
                xtype:'panel',
                width:me.isShowOrEdit==0?250:200,
                border:0
            }
            ],
            style:{
                marginLeft:(me.width - 200)/2 +""//,
//		    	opacity:0
            }//,
//		    bodyStyle:'filter:alpha(opacity=0)'

        });
        if(me.isShowOrEdit==1){//编辑
            me.buttons.add([me.upbutton,me.delbutton,me.conformbutton]);
        }
        else if(me.isShowOrEdit==0){//查看
            me.buttons.setMargin('0 0 0 0');
            me.buttons.add([me.conformbutton]);
        }

        //整体上传页面,浮动，居中
        me.muiltyPanel = Ext.widget("container",{
            scrollable:'y',
            layout:{
                type:'vbox',
                align:'stretch'
            },
            width:me.width,
            height:me.height - 73,
            style:{zIndex:9999,borderBottom:'solid  #c5c5c5 1px'},
            items:[me.headerPanel,me.dataPanel]
        });

        me.mywindow = Ext.create('Ext.window.Window', {
            title: '文件上传',
            height: me.height,
            width: me.width,
            shadow:false,
            bodyBorder:false,
            border:true,
            modal: true,
            layout:{
                type:'vbox',
                align:'stretch'
            },
            items: [me.muiltyPanel,me.buttons],
            resizable:false,
            //autoShow:true,
            listeners:{
                close:function(obj){
                    return me.closeHandler(obj);
                }
            }
        });
        me.mywindow.show().center();
        me.addFileListByData();
    },
    /**
     * 创建一个上传文件记录
     * @param fileName
     * @param fileSize
     * @param type 类型，0选择上传文件后添加的，1为初始化时添加的，
     * @param progress
     * @param status
     * @param deleted
     * @returns
     */
    createFileList:function(id,type,fileName,fileSize,progress,status,deleted,returnValue){
        var me = this;
        var storeFiledType = Ext.create('Ext.data.Store',{
            fields:['dataValue', 'dataName']
        });
        if(me.fileTypeMapList){//文件类型store
            Ext.each(me.fileTypeMapList,function(obj,index){
                storeFiledType.insert(index,obj);
            });
        }

        var hiddenDel = false;
        var a = 0;
        if(me.isShowOrEdit==0){//查看
            hiddenDel = true;
            a=0.18;
        }else{
            a=0.1;
        }

        // 上传文件列表表头
        var panelList =  Ext.create('Ext.Panel', {
            panelId:"panel" + id,
            header:false,
            upType:type,
            height: 30,
            //columnWidth: 1,
            border:1,
            layout:{
                type:'column'
            },
            bodyStyle:{
                "border-top":"solid 0px",
                "border-right":"solid 0px",
                "border-left":"solid 0px"
            },
            items: [{
                xtype:"container",
                width:24,
                height:30,
                bodyStyle:{
                    "text-align":"center",
                    "line-height":"30px"
                }
            },
                {
                    xtype:"panel",
                    border:1,
                    layout:{
                        type:"hbox",
                        align:"middle"
                    },
                    bodyStyle:{
                        "text-align":"left",
                        "line-height":"30px",
                        "border-top":"solid 0px",
                        "border-right":"solid 0px",
                        "padding-left":"5px"
                    },
                    html:fileName,
                    localName:fileName,
                    columnWidth:1,
                    //columnWidth:(me.fileTypeMapList!=null)?0.15+a:0.25+a,
//		    	width:(me.width - 24) * 0.35,
                    height:30,
                    listeners:{
                        afterrender: function(hh) {
                            var tip = Ext.create('Ext.tip.ToolTip', {
                                target: hh.getEl(),
                                html: fileName,
                                //delegate: '.x-grid-cell-inner:not(.x-grid-cell-inner-treecolumn)',
                                // Moving within the row should not hide the tip.
                                trackMouse: true,
                                bodyStyle:"background-color:white;border:1px solid #c5c5c5",
                                border:true
                            });

                        }
                    }

                },{//changxy 文件类型--上传文件内容
                    xtype:"panel",
                    border:1,
                    // html: fileSize,
                    height:30,
                    width: 80,
                    hidden:(me.fileTypeMapList!=null)?false:true,
                    layout:{
                        type:'column',
                        align:"center",
                        pack: 'center'
                    },
//              width:(me.width - 24) * 0.15,
                    bodyStyle:{
                        "text-align":"left",
                        "line-height":"30px",
                        "border-top":"solid 0px",
                        "border-right":"solid 0px",
                        "padding-left":"5px"
                    },items:[{xtype:'panel',
                        height:'26',
                        border:0,
                        columnWidth:0.9,
                        margin:'3 0 0 1',
                        items:[
                            (me.fileTypeMapList!=null)?{xtype:'combobox',
                                height:'100%',
                                border:0,
                                width:'100%',
                                store:storeFiledType,
                                editable:false,
                                disabled:me.isShowOrEdit==1?false:true,//liuyz bug25357 只读不能改变文件类型
                                queryMode:'local',
                                matchFieldWidth:false,
                                displayField: 'dataName',
                                valueField: 'dataValue',
                                listeners:{//为空时设置默认第一个 changxy
                                    afterRender:function(combo){
                                        if(returnValue&&returnValue.fileType){
                                            combo.setValue(returnValue.fileType);
                                        }else{
                                            combo.setValue(storeFiledType.data.items[0].data.dataValue);
                                        }
                                    }
                                }
                            }:{}
                        ]
                    }
                    ]
                },{
                    xtype:"panel",
                    border:1,
                    html: fileSize,
                    height:30,
                    width: 60,
//		        width:(me.width - 24) * 0.15,
                    bodyStyle:{
                        "text-align":"center",
                        "line-height":"30px",
                        "border-top":"solid 0px",
                        "border-right":"solid 0px"
                    }
                },{
                    xtype:"panel",
                    border:1,
                    height:30,
                    width: 150,
//		    	width:(me.width - 24) * 0.3,
                    layout:{
                        type:'column',
                        align:"center",
                        pack: 'center'
                    },
                    bodyStyle:{
                        "text-align":"left",
                        "line-height":"30px",
                        "border-top":"solid 0px",
                        "border-right":"solid 0px",
                        "padding-left":"5px"
                    },
                    items:[{
                        xtype:"panel",
                        border:1,
                        columnWidth:0.7,
                        height:20,
                        margin:'5 0 0 0',
                        items:[{
                            xtype:"panel",
                            border:0,
                            width:progress,

                            height:20,
                            bodyStyle:{
                                "background-color":"green"
                            }
                        }]
                    },{
                        xtype:"panel",
                        html:progress,
                        border:0,
                        columnWidth:0.3,
                        height:30,
                        margin:'0 0 0 2'

                    }]
                },{
                    xtype:"panel",
                    border:1,
                    height:30,
                    width: 60,
//		        width:(me.width - 24) * 0.1,
                    layout:{
                        type:"hbox",
                        align:"middle",
                        pack: 'center'
                    },
                    bodyStyle:{
                        "text-align":"center",
                        "line-height":"30px",
                        "border-top":"solid 0px",
                        "border-right":"solid 0px"
                    },
                    items:[{
                        xtype:"image",
                        border:0,
                        width:20,
                        height:20,
                        src:'/components/fileupload/imags/quan.png',
                        style:{
                            "margin-left":"auto",
                            "margin-right":"auto"
                        },
                        listeners:{
                            render:function(){
                                if (status) {
                                    this.setSrc("/components/fileupload/imags/gou.png");
                                } else {
                                    this.setSrc("/components/fileupload/imags/quan.png");
                                }
                            }
                        }
                    }]
                },{
                    xtype:"panel",
                    border:1,
                    height:30,
                    width: 60,
                    hidden:hiddenDel,
//		    	width:(me.width - 24) * 0.1,
                    layout:{
                        type:"hbox",
                        align:"middle",
                        pack: 'center'
                    },
                    bodyStyle:{
                        "text-align":"center",
                        "line-height":"30px",
                        "border-top":"solid 0px",
                        "border-right":"solid 0px"
                    },
                    items:[{
                        xtype:"image",
                        border:1,
                        width:20,
                        height:20,
                        src:'/images/del.gif',
                        style:{
                            cursor: 'pointer'
                        },
                        listeners:{
                            render:function(){
                                if (deleted){
                                    var hi = this;
                                    this.setSrc("/images/del.gif");
                                    this.getEl().on("click",function(){
                                        Ext.Msg.show({
                                            title:"提示信息",
                                            msg: "确认删除记录？",
                                            buttons: Ext.Msg.YESNO,
                                            fn: function(btn){
                                                if(btn=='yes'){
                                                    hi.ownerCt.ownerCt.ownerCt.remove(hi.ownerCt.ownerCt);
                                                    // me.deleImgHandler(true,returnValue.path,returnValue.filename);
                                                }else{
                                                    return;
                                                }

                                            },
                                            icon: Ext.MessageBox.QUESTION
                                        });
                                    },hi);
                                } else {
                                    this.setSrc(Ext.BLANK_IMAGE_URL);
                                    // 移除所有事件
                                    Ext.EventManager.removeAll(this.getEl());
                                }


                            }
                        }
                    }]
                }]

        });

        if (status) {
            me.addCheckbox(panelList,id);

        }

        if (returnValue) {
            panelList.flag='0';
            panelList.returnValue = returnValue;
            panelList.items.items[1].localName = returnValue.localname;
        }

//         if (me.dataPanel.items.items.length + 1 == 6) {
// //			me.headerPanel.setOverflowXY('hidden','scroll');alert(me.getScrollWidth());
//             me.dataPanel.setWidth(me.dataPanel.getWidth()+ 18);
//             me.headerPanel.setWidth(me.width - 10);
//         }

        return panelList;
    },
    /**
     * 文件名称处理
     */
    muilty_filename_handler:function(localname, path,filename){
        var me = this;
        if (!me.isDownload) {
            return localname;
        } else {
            if (path) {
//                return "<a target='_blank' href='/components/fileupload/upload?down=true&path="+path+"&filename="+filename+"&localname="+getEncodeStr(localname)+"'>" + localname + "</a>";
                return "<a target='_blank' href='/servlet/vfsservlet?fileid="+ filename + "'>" + localname + "</a>";
            }else {
                return localname;
            }

        }
    },
    getDecodeStr :function(str) {
        return ((str)?this.decode(this.getValidStr(str)):"");
    },
    reNew :function(str){
        var re;
        re=/%26amp;/g;
        str=str.replace(re,"&");
        re=/%26apos;/g;
        str=str.replace(re,"'");
        re=/%26lt;/g;
        str=str.replace(re,"<");
        re=/%26gt;/g;
        str=str.replace(re,">");
        re=/%26quot;/g;
        str=str.replace(re,"\"");
        re=/%25/g;
        str=str.replace(re,"%");
        re=/````/g;
        str=str.replace(re,",");
        return(str);
    },

    getValidStr :function(str){
        str += "";
        if (str=="undefined" || str=="null" || str=="NaN")
            return "";
        else
            return this.reNew(str);
    },
    decode :function(strIn){
        var intLen = strIn.length;
        var strOut = "";
        var strTemp;

        for(var i=0; i<intLen; i++){
            strTemp = strIn.charAt(i);
            switch (strTemp){
                case "~":{
                    strTemp = strIn.substring(i+1, i+3);
                    strTemp = parseInt(strTemp, 16);
                    strTemp = String.fromCharCode(strTemp);
                    strOut = strOut+strTemp;
                    i += 2;
                    break;
                }
                case "^":{
                    strTemp = strIn.substring(i+1, i+5);
                    strTemp = parseInt(strTemp,16);
                    strTemp = String.fromCharCode(strTemp);
                    strOut = strOut+strTemp;
                    i += 4;
                    break;
                }
                default:{
                    strOut = strOut+strTemp;
                    break;
                }
            }

        }
        return (strOut);
    },
    addCheckbox:function(obj,id) {
        var me = this;
        checkb = Ext.create("Ext.form.field.Checkbox", {
            inputId: "check" + id,
            name: "selectCheck",
            margin: '3 0 0 5',
            handler: function (f, checked) {
                if (checked == false) {
                    me.selectAllCheck.setValue(checked);
                }
            }

        });
        obj.items.items[0].add(checkb);
    },
    deleImgHandler:function(deleteflag,path,filename){
        var me = this;
        if (me.dataPanel.items.items.length <= 6) {

            //me.headerPanel.setOverflowXY('hidden','hidden');

            me.dataPanel.setWidth(me.width);
            me.headerPanel.setWidth(me.width);
        }
        if(deleteflag==true){
            Ext.Ajax.request({
//                url : '/components/fileupload/upload',
                url : '/servlet/vfsservlet',
                method: 'GET',
                params : {
                    deleteflag :deleteflag,
                    path:path,
                    filename:filename,
                    fileid:filename,
                    //vfs新增参数
                    isTempFile: me.isTempFile,//是否为临时文件 true是，false不是
                    VfsFiletype: me.VfsFiletype,//关联VfsFiletypeEnum 文件类型
                    VfsModules: me.VfsModules,//关联VfsModulesEnum 模块id
                    VfsCategory: me.VfsCategory,//文件所属类型
                    CategoryGuidKey: me.CategoryGuidKey,//所属类型guidkey
                    filetag:me.filetag
                },
                success : function(res) {
                },
                scope:me
            });
            /**
             * Ext.Ajax.request({
		          url : '/components/fileupload/upload',
		          method: 'GET',
		          params : {
		              deleteflag :"true",
		              filename:fileid,
		              fileid:fileid,
		          },
		          success : function(res) {
		          }
		      });
             */
        }

    },
    /**
     * 关闭页面时需要处理
     */
    closeHandler:function(obj) {
        var me = this;
        me.destroy();
        me.muilty_success_handler();
    },
    muilty_success_handler:function(){
        var me = this;
        if (me.callBackScope) {
            Ext.callback(me.success, me.callBackScope,[me.getAllData()]);
        } else {
            Ext.callback(me.success,null,[me.getAllData()]);
        }
    },
    getAllData:function(){
        var me = this;
        var tt = [];
        me.dataPanel.items.each(function(obj){
            if (obj.returnValue) {
                var o = obj.returnValue;
                if(me.fileTypeMapList)//如果有文件类型列 则returnValue添加fileType
                    o.fileType=obj.items.items[2].items.items[0].items.items[0].getValue();
                o.localname = obj.items.items[1].localName;
                tt.push(o);
            }
        });

        return tt;
    },
    /**
     * 菜单上传按钮渲染
     */
    menuUploadInit:function(){
        var me = this;
        me.filesize=0;
        var tempPanel="";
        var id=Math.floor(Math.random()*1000);
        id="fileupload_"+id;
        //文件上传按钮实现方式：
        //若是谷歌浏览器，且渲染目标是个ext button,则使用panel 覆盖在渲染目标上，并且模拟文件上传input的点击事件，绑定在渲染目标的点击事件上。
        //若是ie浏览器，且渲染目标是个ext button，则将渲染目标设置为禁用，将文件上传的input 覆盖在渲染目标上，将背景颜色设置为透明。
        //若渲染目标不是个ext button，则直接将文件上传的input 覆盖在目标上，背景颜色设置为透明。
        if(Ext.isChrome) {
            tempPanel=Ext.create("Ext.panel.Panel", {
                html: me.buttonText,
                width: me.width,
                height: me.height,
                renderTo: this.el,
                bodyStyle: {"background": "transparent"},
                border: 0
            });
        }
        me.browseBt = Ext.create("Ext.panel.Panel",{
            width:me.width,
            height:me.height,
            renderTo:this.el,
            border:0,
            bodyStyle:{"background": "transparent",'position':'relative'},
            html:"<span class='btn btn-success fileinput-button' style='position: relative;display: block;width:"+me.width+"px;height: "+me.height+"px;' >" +
                "<span style='display:inline-block;width:"+me.width+"px;height: "+me.height+"px;text-align:center;position: relative;display: block;cursor:pointer'>"+me.buttonText+"</span>" +
                "<input id='"+id+"' unselectable='on' type='file' style='width:"+me.width+"px;height: "+me.height+"px;cursor:pointer'  name='files[]' multiple >" +
                " </span>",
            listeners:{
                render:function(comp) {
                    var renderId = this.renderTo.dom.parentElement.id;
                    if (Ext.getCmp(renderId)!= undefined && Ext.getCmp(renderId).xtype == "button") {
                        $("#"+id).click(function (event) {
                            event.stopPropagation();//取消冒泡
                        });
                        if (Ext.isIE&&document.documentMode<=8) {
                            Ext.getCmp(renderId).setDisabled(true);
                            Ext.getCmp(renderId).removeCls('x-btn-disabled');//取消掉禁用按钮之后的灰色样式
                        } else {
                            comp.setStyle("top:500px");
                            Ext.getCmp(renderId).on('click', function () {
                                document.getElementById(id).click();
                            });
                        }
                    }else if(tempPanel!=""){
                        tempPanel.destroy();
                    }
                    $('#'+id).fileupload({
                        dataType: 'json',
//                        url:'/components/fileupload/upload',
                        url:'/servlet/vfsservlet',
                        done: function (e, data) {//上传成功事件
                            var obj = data.result;
                            var objValue = data.result;
                            if(objValue.successed) {
                                me.returnValue = obj;
                                me.returnValue.localname = me.getDecodeStr(obj.yfileName);
                            }
                            else{
                                Ext.showAlert(data.result.msg);
                            }
                        },
                        fail: function(e, data) {//响应失败的回调
                            Ext.showAlert("上传失败！");
                        },
                        progress: function(e, data) {//进度条
                            var progress = parseInt(data.loaded / data.total * 100, 10);
                            var name=data.files[0].name;
                            if (me.uploadProgress) {
                                if (me.callBackScope) {
                                    Ext.callback(me.uploadProgress, me.callBackScope,[progress,name,me.filesize]);
                                } else {
                                    Ext.callback(me.uploadProgress,null,[progress,name,me.filesize]);
                                }
                            }
                        },
                        always: function(e, data) {//文件上传完成事件，无论成功或失败
                            if(data.textStatus='success') {
                                var filesize = (me.filesize / (1024 * 1024)).toFixed(2);
                                var sizeStr = filesize + "MB";
                                if (filesize == 0.00) {
                                    sizeStr = (me.filesize / (1024)).toFixed(2) + "KB";
                                }
                                data.result.size = sizeStr;
                                data.result.localname = data.originalFiles[0].name;
                            }

                            if(data.result.successed){
                                if (me.callBackScope) {
                                    Ext.callback(me.success, me.callBackScope,[[data.result]]);
                                } else {
                                    Ext.callback(me.success,null,[[data.result]]);
                                }
                            }else{
                                if(me.error) {
                                    if (me.callBackScope) {
                                        Ext.callback(me.error, me.callBackScope, [[data.result]]);
                                    } else {
                                        Ext.callback(me.error, null, [[data.result]]);
                                    }
                                }else{
                                    Ext.showAlert(data.result.msg);
                                }
                            }
                        },
                        add: function(e, data) {//文件添加到上传队列的事件
                            var filesize=0;
                            var name=data.files[0].name;
                            var fileType=name.substring(name.lastIndexOf(".")).toLowerCase();
                            if(me.fileExt!=""&&me.fileExt!="*.*"&&me.fileExt.toLowerCase().indexOf("*"+fileType)==-1){//文件类型判断
                                Ext.showAlert("上传失败,请上传 "+me.fileExt.replace(";",",")+" 类型的文件！");
                                return false;
                            }
                            var maxSize=0;
                            if(me.fileSizeLimit==undefined ||me.fileSizeLimit=='')
                                me.fileSizeLimit="50mb";
                            if(me.fileSizeLimit.toLowerCase().indexOf("mb")>-1){
                                maxSize=me.fileSizeLimit.substring(0,me.fileSizeLimit.length-2);
                                maxSize=maxSize*1024*1024;
                            }else if(me.fileSizeLimit.toLowerCase().indexOf("gb")>-1){
                                maxSize=me.fileSizeLimit.substring(0,me.fileSizeLimit.length-2);
                                maxSize=maxSize*1024*1024*1024;
                            }else if(me.fileSizeLimit.toLowerCase().indexOf("kb")>-1){
                                maxSize=me.fileSizeLimit.substring(0,me.fileSizeLimit.length-2);
                                maxSize=maxSize*1024;
                            }
                            data.maxFileSize=maxSize;
                            try {
                                if (Ext.isIE) {
                                    var fso = new ActiveXObject("Scripting.FileSystemObject");
                                    var realpath = data.fileInput[0].value;
                                    filesize = fso.GetFile(realpath).size;
                                } else {
                                    filesize = data.originalFiles[0]['size'];
                                }
                            }catch (e) {
                                //Ext.showAlert("若使用IE浏览器上传文件，请在安全设置中启用ActiveX控件！");
                            }
                            me.filesize=filesize;
                            if(filesize> maxSize) {//文件大小判断
                                Ext.showAlert("上传失败,上传文件不能大于"+me.fileSizeLimit+"！");
                                return false;
                            }
                            data.submit();
                            if (me.upLoading) {
                                if (me.callBackScope) {
                                    Ext.callback(me.upLoading, me.callBackScope,[name,filesize]);
                                } else {
                                    Ext.callback(me.upLoading,null,[name,filesize]);
                                }
                            }

                        }
                    });
                    $('#'+id).bind('fileuploadsubmit', function (e, data) {//向后台传递的参数
                        data.formData=[
                            {name:'extControl',value:me.extControl},
                            {name:'maxFileSize',value:data.maxFileSize},
                            {name:'savePath',value:me.savePath},
                            {name:'fileNameMaxLength',value:me.fileNameMaxLength},
                            {name:'fileName',value:getEncodeStr(data.files[0].name)},
                            //vfs新增参数
                            {name:'isTempFile',value:me.isTempFile},
                            {name:'VfsFiletype',value:me.VfsFiletype},
                            {name:'VfsModules',value:me.VfsModules},
                            {name:'VfsCategory',value:me.VfsCategory},
                            {name:'CategoryGuidKey',value:me.CategoryGuidKey},
                            {name:'filetag',value:me.filetag}];
                    });
                }
            },
            scope:me
        });
    },
    uuid:function () {//获取唯一id方法。
        var s = [];
        var hexDigits = "0123456789abcdef";
        for (var i = 0; i < 12; i++) {
            s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
        }
        s[14] = "4";  // bits 12-15 of the time_hi_and_version field to 0010
        s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1);  // bits 6-7 of the clock_seq_hi_and_reserved to 01
        s[8] = s[13] = s[18] = s[23] = "-";

        var uuid = s.join("");
        var timestamp = (new Date()).valueOf();
        uuid+=timestamp;
        return uuid;
    },
    fucCheckLength:	function (strTemp){//获取字符串长度
        var i,sum;
        sum=0;
        for(i=0;i<strTemp.length;i++){
            if ((strTemp.charCodeAt(i)>=0) && (strTemp.charCodeAt(i)<=255)){
                sum=sum+1;
            }else{
                sum=sum+2;
            }
        }
        return sum;
    },
    addFileListByData:function(){
        var me = this;
        for (var i = 0; i < me.fileList.length; i++) {
            var tt = me.fileList[i];
            var name = me.muilty_filename_handler(tt.localname,tt.path,tt.filename);
            me.dataPanel.add(me.createFileList(tt.id,1,name,tt.size,"100%",true,true,tt));
        }
    },
    /**
     * xus 20/5/15 vfs真实删除文件方法
     */
    deleteRealFile:function(fileid){
        Ext.Ajax.request({
            url : '/components/fileupload/upload',
            method: 'GET',
            params : {
                deleteflag :"true",
                filename:fileid,
                fileid:fileid,
            },
            success : function(res) {
            }
        });
    }

});
