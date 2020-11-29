<%@ page contentType="text/html; charset=UTF-8"%>
<!DOCTYPE HTML>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page
	import="com.hjsj.hrms.utils.components.emailtemplate.actionform.TemplateSetForm"%>
<html>
	<head>
		<title>新增模板</title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

		<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7"> 

		<link href="/module/recruitment/css/style.css" rel="stylesheet"
			type="text/css" />
			<!-- 
		<link href="/ext/resources/css/ext-all.css" rel="stylesheet" type="text/css" />
			 -->
		<script language="JavaScript"
			src="/components/tableFactory/tableFactory.js"></script>
		<script language="JavaScript" src="/module/utils/js/template.js"></script>
		<script language="JavaScript"
			src="/components/emailtemplate/newTemplate/addEmailTemplate.js"></script>
			<script language="JavaScript"
			src="/components/emailtemplate/newTemplate/addAttach.js"></script>
			<script language="JavaScript"
			src="/components/emailtemplate/newTemplate/addFieldItem.js"></script>
			<script language="JavaScript"
			src="/components/emailtemplate/newTemplate/addFormula.js"></script>
			<script language="JavaScript"
			src="/components/emailtemplate/newTemplate/editFormula.js"></script>
			<script language="JavaScript" src="/components/extWidget/field/CodeTreeCombox.js"></script>
			<script language="JavaScript"
			src="/components/codeSelector/fieldItemSelector.js"></script>
		 <script language="JavaScript"
			src="/components/codeSelector/codeSelector.js"></script>
		<script language="JavaScript" src="/components/extWidget/proxy/TransactionProxy.js"></script>
		<!-- 
		<script type="text/javascript"
			src="/components/codeSelector/deepCodeSelector.js"></script>
		 -->
		 <script src="/components/fileupload/FileUpLoad.js" ></script>
	<style>
		.x-text-readonly { background:#E6E6E6;color:#E6E6E6;cursor:#E6E6E6;}
		.x-grid-record-gray{background: #c3daf9;}
		.x-form-text-default-bb_view{
			color: #000;
			padding: 1px 3px 2px;
			border:1px #c5c5c5 solid;
			background-color: #fff;
			background-image: none;/*url(images/form/text-bg.gif);*/
			font: normal 12px/17px 微软雅黑,宋体,tahoma,arial,verdana,sans-serif;
			min-height: 20px
		}
	
		
	</style>
	<script type="text/javascript" charset="UTF-8">
 	    function getUrlParam(param) {
			var params = Ext.urlDecode(location.search.substring(1));
			return param ? params[param] : params;
		} 
	//屏蔽  招聘管理/招聘设置/通知模板，点击新增之后，进入界面，然后再点击系统管理，页面报错，需要调试时最好去掉
		window.onerror=function(){return true;} 
		

 Ext.onReady(function(){
	 	var ieSelectionBookMark = null;
	 	//类别从后台传，一遍其他模块调用
		/*var store = Ext.create('Ext.data.SimpleStore',{
 	  		 	 fields:['id','desc'],  
           		 data:[['10','接受职位申请通知'],['11','拒绝职位申请通知'],
           		 ['20','面试安排通知（申请人）'],
           		 ['40','面试通知（通过）'],['50','面试通知（淘汰）'],
           		 ['60','Offer'],['70','入职通知（管理人员）'],['80','简历评价通知（评价人）'],
           		 ['81','转发简历通知'], ['90','其它通知模板']]
 	  		 });
  		 store.load();*/
  		 /*
  		 通知模板的panel
	  		
  		 模板类别、模板名称、邮箱的panel
  		 */
  		var map = new HashMap();
  		Global.dataStore1 = null;
  		Global.store = null;
  		
  		Global.subModule = "";//模板类别
  		Global.other_flag = "";//招聘环节
  		Global.name = "";//模板名称
  		Global.template_id = "";//模板id隐藏字段
  		Global.fieldid = "";
  		Global.returnAddress = "";//回复邮箱
  		Global.subject = "";//邮件标题
  		Global.content = "";//邮件内容
  		Global.isParent = "";//是上级模板
  		Global.ownflag = "";
  		Global.isReadOnly = false;//是否是是编辑还是新增
        
  		Global.opt = ${templateReForm.opt};
  		Global.isShowItem = ${templateReForm.isShowItem};	
  		Global.isShowModuleType = ${templateReForm.isShowModuleType};	
  		Global.isShowInsertFormula = ${templateReForm.isShowInsertFormula};	
  		Global.isShowModifyFormula = ${templateReForm.isShowModifyFormula};
  		Global.isShowAttachId = ${templateReForm.isShowAttachId};
  		map.put("opt",Global.opt + "");
  		Rpc({functionId:'ZP0000002342',async:false,success: function(form){
  			var result = Ext.decode(form.responseText);
  			Global.dataStore1 = Ext.create('Ext.data.Store', {
  				storeId:'storeid',
  				fields:['dataName','dataValue'],
  			    data : result.processlist
  			 });
  			Global.store = Ext.create('Ext.data.Store', {//获取类别属性集合
  				fields:['dataName','dataValue'],
  			    data : result.typeList
  			 });
  		}}, map);
  		var template_id = getUrlParam('template_id');
        if (template_id != undefined && template_id != null && template_id.length > 0) {
            var hashvo = new ParameterSet();
            hashvo.setValue("template_id",template_id);
            map = new HashMap();
            map.put("template_id",template_id);
            map.put("opt",Global.opt + "");
            Rpc({functionId : 'ZP0000002344',success : function(response){
            	Global.isReadOnly = true;
            	var value = response.responseText;
                var map = Ext.decode(value);
                Global.ownflag = map.ownflag;
                Global.isParent = map.isParent;
               /*  document.getElementById('buttonSave').innerHTML = '编辑';
                //是上级模板
                if (isParent)
                    Ext.get('buttonSave').hide(); */
                Global.subModule = map.subModule==null?"":map.subModule + "";
                Global.other_flag = map.other_flag==null?"":map.other_flag + "";
                Global.name = map.name==null?"":map.name;
                Global.template_id = map.template_id==null?"":map.template_id + "";
                Global.fieldid = map.fieldid==null?"":map.fieldid + "";
                Global.returnAddress = map.returnAddress==null?"":map.returnAddress;
                Global.subject = map.subject==null?"":map.subject;
                Global.content = map.content==null?"":map.content;

                if (Global.content != null && Global.content.length > 0) {
                	Global.content = Global.content.replace(/href=/g, 'href="##" value=');
                }
                Global.createHtml();
            }},map);
        }else {
            Global.createHtml();
        }
    });
 
   Global.createHtml = function(){
     var filterPanel = Ext.create('Ext.panel.Panel',{
         border : false,
         id : 'addEmail',
         region : 'center',
         style : 'backgroundColor:white',
         bodyStyle : 'padding:10',
         labelWidth : 40,
         defaultType : "textfield",
         scrollable : true,
         title : "<div style='float:left'>通知模板</div><div id='titilPanel' style='font-weight:normal'></div>",
         items : [{
                     xtype : 'container',
                     layout : {
                         type : 'hbox',
                         align : 'middle'
                     },
                     padding : "15 0 0 0",
                     width : 1000,
                     items : [{
                                 xtype : 'combo',
                                 id : 'subModuleId',
                                 store : Global.store,
                                 displayField : 'dataName',
                                 valueField : 'dataValue',
                                 fieldLabel : '模板类别',
                                 mode : 'local', // 设置local，combox将从本地加载数据
                                 triggerAction : 'all',// 触发此表单域时,查询所有
                                 selectOnFocus : true,
                                 anchor : '90%',
                                 typeAhead : true,// 设置true，完成自动提示 
                                 blankText : '请选择…',
                                 forceSelection : true,
                                 width : 500,
                                 labelAlign : 'right',
                                 labelSeparator : null,
                                 hidden:Global.isShowModuleType==1?true:false,
                                 beforeLabelTextTpl : "<font color='red'> * </font>",
                                 fieldStyle : Global.isReadOnly?"background:#E6E6E6":"",//设置不能选择
                                 readOnly : Global.isReadOnly,
                                 value : Global.subModule,
                                 listeners : {
                                     change : function(thisobj,newValue,oldValue,eOpts) {
                                    	 //当模板为招聘批次通知时，插入指标，插入，修改公式按钮禁用 
                                    	 if(newValue == "92"){
                                    		 Ext.getCmp("fielditemid").disable();
                                    		 Ext.getCmp("formulaaddid").disable();
                                    		 Ext.getCmp("formulaeditid").disable();
                                    	 }else{
                                 	   		Ext.getCmp("fielditemid").enable();
                                	   		Ext.getCmp("formulaaddid").enable();
                                	   		Ext.getCmp("formulaeditid").enable();
                                	   	}
                                    	 
                                         if (newValue == 90) {
                                             Ext.getCmp("other_flagId").show();
                                         } else {
                                             Ext.getCmp('other_flagId').clearValue();
                                             Ext.getCmp("other_flagId").hide();
                                         }
                                     },
                                     afterRender : function(combo) {
                                         if(Global.store.totalCount > 0){//如果store没有值，则不执行显示第一个操作
											combo.setValue(Global.subModule);
                                        	if(Global.subModule == "90")
                                        		Ext.getCmp("other_flagId").show();
                                         }
                                     }
                                 }
                             },
                             {
                                 xtype : 'combo',
                                 id : 'other_flagId',
                                 store : Global.dataStore1,
                                 displayField : 'dataName',
                                 valueField : 'dataValue',
                                 fieldLabel : '招聘环节',
                                 labelWidth : 80,
                                 hidden : true,
                                 triggerAction : 'all',// 触发此表单域时,查询所有
                                 selectOnFocus : true,
                                 anchor : '90%',
                                 typeAhead : true,// 设置true，完成自动提示 
                                 forceSelection : true,
                                 width : 350,
                                 labelAlign : 'right',
                                 labelSeparator : null,
                                 blankText : '请选择',// 该项如果没有选择，则提示错误信息
                                 beforeLabelTextTpl : "<font color='red'> * </font>",
                                 fieldStyle : Global.isReadOnly?"background:#E6E6E6":"",//设置不能选择
                                 readOnly : Global.isReadOnly,
                                 value : Global.other_flag,
                                 listeners : {
                                     afterRender : function(combo) {
                                         combo.setValue(Global.other_flag);//同时下拉框会将与name为firstValue值对应的 text显示
                                     }
                                 }
                         } ]
                 },
                 {
                     xtype : 'textfield',
                     id : 'templateName',
                     allowBlank : false,
                     blankText : '请输入模板名称',
                     fieldLabel : '模板名称',
                     labelAlign : 'right',
                     labelSeparator : null,
                     style : 'margin-top:20px',
                     beforeLabelTextTpl : "<font color='red'> * </font>",
                     width : 500,
                     fieldStyle : Global.isReadOnly?"background:#E6E6E6":"",//设置不能选择
                     readOnly : Global.isReadOnly,
                     value : Global.name
                 },
                 {
                     xtype : 'textfield',
                     id : 'returnAddress',
                     blankText : '请输入邮箱地址',
                     fieldLabel : '回复邮箱',
                     maxLength : 100,
                     labelAlign : 'right',
                     style : 'margin-top:20px',
                     width : 500,
                     enforceMaxLength : true,
                     labelSeparator : null,
                     vtype : 'email',
                     fieldStyle : Global.isReadOnly?"background:#E6E6E6":"",//设置不能选择
                     readOnly : Global.isReadOnly,
                     value : Global.returnAddress
                 },
                 {
                     xtype : 'hidden',
                     id : 'tempalteId',
                     width : 500,
                     blankText : 'id',
                     fieldLabel : 'id',
                     value : Global.template_id
                 },
                 {
                     xtype : 'hidden',
                     id : 'tempalteFieldId',
                     width : 500,
                     blankText : 'id',
                     fieldLabel : 'tempalteFieldId',
                     value : Global.fieldid
                 },
                 {
                     xtype : 'hidden',
                     id : 'ownflagId',
                     width : 500,
                     blankText : 'ownflag',
                     fieldLabel : 'ownflag',
                     value : Global.ownflag
                 },
                 {
                     xtype : 'label',
                     width : '100%',
                     html : '<div class="hj-zm-hj-one" style="width: 90%; margin-left: 1%;margin-top:20px;margin-bottom:20px"><h2>通知邮件</h2></div>'
                 },
                 {
                     xtype : 'textfield',
                     width : 500,
                     id : 'emailName',
                     labelAlign : 'right',
                     blankText : '请输入邮件标题',
                     labelSeparator : null,
                     enableKeyEvents : true,
                     fieldLabel : '邮件标题',
                     beforeLabelTextTpl : "<font color='red'> * </font>",
                     fieldStyle : Global.isReadOnly?"background:#E6E6E6":"",//设置不能选择
                     readOnly : Global.isReadOnly,
                     value : Global.subject
                 },
                 {
                     xtype : "htmleditor",
                     id : 'contentId',
                     name : "cc_text",
                     fieldLabel : "邮件内容",
                     height : 280,
                     width : 850,
                     labelAlign : 'right',
                     style : 'margin-top:20px;',
                     labelSeparator : null,
                     fontFamilies : ["宋体","隶书","黑体","楷体","Arial","Verdana","Georgia" ],
                     readOnly : Global.isReadOnly,
                     value : Global.content
                 }, Global.getButtons() ],
         renderTo : Ext.getBody(),
         listeners : {
             afterrender : function() {
                 var html1 = "";
                 if(!Global.isParent)
                     html1 = '<a id="buttonSave" href="###" onclick="Global.addTemplate()" >'+(Global.isReadOnly?'编辑':'保存')+'</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="###" onclick="Global.cancel()" >返回</a>';
                 document.getElementById('titilPanel').innerHTML = html1;
                 document.getElementById('titilPanel').style.marginLeft = document.body.clientWidth - 150 + "px";
                 if (Global.content != null && Global.content.length > 0) {
                     var textfield = Ext.create('Ext.form.field.Text', {
                         value : 1,
                         id : 'buttonTextfieldId',
                         hidden : true
                     });
                     Ext.getCmp('addEmail').insert(textfield);
                 } 
                 
                 /*修改页面时，获取URL中传递的template_id，并赋值给相应的组件*/
                 /*var template_id = getUrlParam('template_id');
                 if (template_id != null && template_id.length > 0) {
                     var hashvo = new ParameterSet();
                     hashvo.setValue("template_id",template_id);
                     var map = new HashMap();
                     map.put("template_id",template_id);
                     map.put("opt",Global.opt + "");
                     Rpc({functionId : 'ZP0000002344',success : Global.toLoadData},map);
                 } */
             }
         }
     });
     new Ext.Viewport({
         layout : 'border',
         id : 'viewPor1',
         title : '通知模板',
         padding : "0 5 0 5",
         renderTo : Ext.getBody(),
         style : 'backgroundColor:white',
         items : [ filterPanel ],
         listeners:{
             'resize':function(){
                 document.getElementById('titilPanel').style.marginLeft = document.body.clientWidth - 150 + "px";
             }
         }

     });
  }
		   //生成按钮
		   Global.getButtons = function(){
		       var item = "contentId";//判断插入指标时是插入到标题还是插入到内容 
		       var buttons=Ext.create('Ext.Panel',{
		           layout : 'column',
		           width : 438,
		           height : 35,
		           id : 'buttonPanelId',
		           style : 'backgroundColor:white;margin-left:415px',
		           bodyStyle : 'padding:10',
		           labelAlign : 'right',
		           defaultType : "textfield",
		           border : false,
		           buttons : [{
		                       text : '插入指标',
		                       id : 'fielditemid',
		                       width : 80,
		                       height : 30,
		                       buttonAlign : 'right',
		                       hidden : Global.isReadOnly?true:(Global.isShowItem==1?true:false),
		                       visible : !Global.isReadOnly,
		                       listeners : {//添加监听事件 可以结合handler测试这两个事件哪个最先执行
		                    	   afterrender:function(e){
		                               if(Ext.isIE){//ie 下绑定mouseover事件
		                                   e.getEl().on("mouseover",function(){
		                                      markBook("contentId"); //获得光标位置
		                                   })
		                               }
		                           },
		                           "click" : function() {
		                               var wobj = Ext.getCmp(item);
		                               // wobj.focus();
		                               if (!isIE()) {
		                                   //获取当前光标的位置
		                                   ieSelectionBookMark = wobj.inputEl.dom.selectionStart;
		                               }
		                               if (item == "contentId") {
		                                   if (isIE()) {
		                                       if (document.selection) {
		                                           //获取当前光标的位置,这时候htmleditor已经失去了焦点
		                                           // var rangeObj = wobj.getDoc().selection.createRange();
		                                           // ieSelectionBookMark = rangeObj.getBookmark();
		                                           //markBook("contentId");
		                                       }
		                                   }
		
		                               }
		                               if (item == "emailName") {
		                                   if (isIE()) {
		                                       //获取当前光标的位置这时候htmleditor已经失去了焦点
		                                       //var rangeObj = document.selection.createRange();
		                                       //ieSelectionBookMark = rangeObj.getBookmark();
		                                       //markBook("contentId");
		                                   }
		                               }
		                               var reg = new RegExp('\\\$[0-9]+:[a-zA-Z0-9\u4e00-\u9fa5]+\\\$','g');
		                               var context = Ext.getCmp(item).getValue();
		                               var arr = context.match(reg);
		                               var arrField = new Array();
		                               var num = 1;
		                               if (arr != null) {
		                                   for ( var i = 0; i < arr.length; i++) {
		                                       arrField[i] = arr[i].substring(arr[i].indexOf("$") + 1,arr[i].indexOf(":"));
		                                   }
		                                   num = parseInt(Math.max.apply(null,arrField)) + 1;
		                               }
		                               var value = [ ieSelectionBookMark,item,num ];
		                               if(Global.opt == '9')
		                                   setPanelFiledEleConnect("A","performance",gzemail_chooseFieldOk,value);
		                               else
		                                   setPanelFiledEleConnect("A","recruit",gzemail_chooseFieldOk,value);
		                           }
		                       }
		                   },
		                   {
		                       text : '插入公式',
		                       width : 80,
		                       height : 30,
		                       buttonAlign : 'right',
		                       id : 'formulaaddid',
		                       style : 'margin-left:20px',
		                       hidden : Global.isReadOnly?true:(Global.isShowInsertFormula==1?true:false),
		                       visible : !Global.isReadOnly,
		                       listeners : {//添加监听事件 可以结合handler测试这两个事件哪个最先执行
		                    	   afterrender:function(e){
                                       if(Ext.isIE){//ie 下绑定mouseover事件
                                           e.getEl().on("mouseover",function(){
                                              markBook("contentId"); //获得光标位置
                                           })
                                       }
                                   },
		                           'click' : function() {
		                               var wobj = Ext.getCmp("contentId");
		                               wobj.focus();
		                               if (!isIE()) {
		                                   //获取当前光标的位置
		                                   ieSelectionBookMark = document.getElementById('contentId').selectionStart;
		                               } 
		                               addFormat('1',ieSelectionBookMark);
		                           },
		                           "beforerender" : function() {
                                       Ext.getCmp('formulaAttachId').setVisible(false);
                                   }
		                       }
		                   },
		                   {
		                       text : '修改公式',
		                       width : 80,
		                       height : 30,
		                       buttonAlign : 'right',
		                       style : 'margin-left:20px',
		                       id : 'formulaeditid',
		                       hidden : Global.isReadOnly?true:(Global.isShowModifyFormula==1?true:false),
		                       visible : !Global.isReadOnly,
		                       listeners : {
		                    	   afterrender:function(e){
                                       if(Ext.isIE){//ie 下绑定mouseover事件
                                           e.getEl().on("mouseover",function(){
                                              markBook("contentId"); //获得光标位置
                                           })
                                       }
                                   },
		                           "click" : function() {
		                               editF();
		                           },
		                           "beforerender" : function() {
                                       Ext.getCmp('formulaAttachId').setVisible(false);
                                   }
		                       }
		                   },
		                   {
		                       text : '插入附件',
		                       width : 80,
		                       height : 30,
		                       buttonAlign : 'right',
		                       style : 'margin-left:20px',
		                       id : 'formulaAttachId',
		                       visible : false,
		                       listeners : {
		                           "click" : function() {
		                               upload();
		                           },
		                           "beforerender" : function() {
		                               Ext.getCmp('formulaAttachId').setVisible(false);
		                           }
		
		                       }
		                   } ]
		       });
		       return buttons;
		   }
		
		Ext.QuickTips.init();
	</script>
	</head>
	<body scroll=no>
	</body>
</html>