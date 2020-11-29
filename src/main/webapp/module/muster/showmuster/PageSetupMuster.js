/**
 * 页面设置
 */
Ext.define('SetupschemeUL.PageSetupMuster',{
	
	showMuster:'',
	tabid:'',
	exportPageSetScope:undefined,
	//构造
    constructor:function(config){
    	pageSetScope = this;
    	exportPageSetScope=this;
    	tabid=config.tabid;
		pageSetScope.callbackfn = config.callbackfn;//回调方法
		pageSetScope.result = config.result;//页面参数
		this.init();
    },
	
    //创建页面panel
	init:function(){
		var result = pageSetScope.result;
        pagetabs = Ext.create('Ext.tab.Panel', {
		    width : 550,
		    height : 400,
			id : 'pageTab',
			plain : true,
			//全部页签
			items : [ {
				id:"pagesetup",
				title : page_setup.text.pagesetup,//页面设置	
				bodyPadding : 5
			}, {
				id:"title",
				title : page_setup.text.title,//标题					
				bodyPadding : 5
			}, {
				title : page_setup.text.pagehead,//页头内容
				id:"pagehead",				
				bodyPadding : 10
			}, {
				title : page_setup.text.pagetail,//页尾内容				
				id:"pagetail",		
				bodyPadding : 10
			}, {
				title : page_setup.text.body,//正文				
				id:"text",		
				bodyPadding : 10
			} ]
		});		
       //调用页面设置页签js文件	
       Ext.require( 'EHR.exportPageSet.pageSet.PageSetup', function(){//页面设置
    		Ext.create( 'EHR.exportPageSet.pageSet.PageSetup' ,{result:result});
    	});
       Ext.require( 'SetupschemeUL.PageTitle', function(){//标题
    		Ext.create( 'SetupschemeUL.PageTitle',{result:result} );
    	});
       Ext.require( 'EHR.exportPageSet.pageSet.PageHead', function(){//页头内容
    		Ext.create( 'EHR.exportPageSet.pageSet.PageHead',{result:result} );
    	});     
       Ext.require( 'EHR.exportPageSet.pageSet.PageTail', function(){//页尾内容
    		Ext.create( 'EHR.exportPageSet.pageSet.PageTail',{result:result} );
    	});     
       Ext.require( 'SetupschemeUL.PageText', function(){//正文
    		Ext.create( 'SetupschemeUL.PageText',{result:result} );
    	}); 
       
       //页面窗口的panel
       var formcs = Ext.widget({
			xtype : 'form',
			border : false,
			items : pagetabs,
			id : 'formPanel',
			buttons : ['->',{
					text : page_setup.button.save,//确定
					handler : function() {
						pageSetScope.validation();
					}
				},{
					text : page_setup.button.initData,//初始化
					handler : function() {
						pageSetScope.initData();
					}
					
				},{
					text : page_setup.button.close,//取消
					handler : function() {
						win.close();
					}
				},'->']
		});
       
       //将页面作为窗口展现出来
       var win = Ext.widget("window", {
			title : page_setup.text.pagesetup,//" 页面设置 ",
			id:'pageSetWin',
			minButtonWidth : 45,//按钮的最小宽度
			resizable : false,//是否可调整大小的
			border : false,//边框去掉
			modal : true,//模态窗口
			closeAction : 'destroy',//窗口销毁			  
			items : [formcs]
	   });
	   win.show();	
	},
	
	//验证数据合法
	validation:function(){
		var pagesetupValue = Ext.getCmp("pagesetupid").getForm().getValues(); //页面设置
		if(Ext.getCmp('pagetype').getValue()==null){
			Ext.showAlert(page_setup.pagetype_notnull);//纸张大下不可以为空！
			return false;
		}
        var titleValue = Ext.getCmp("titleid").getForm().getValues();  //标题设置 									
		var pageheadValue = Ext.getCmp("pageheadid").getForm().getValues(); //页头设置
		var pagetailValue = Ext.getCmp("pagetailid").getForm().getValues(); //页尾设置
		var textValueValue = Ext.getCmp("textid").getForm().getValues();  //正文设置
		var pw = Ext.getCmp('pagewidth').getValue().replace(/\s*/g,"");
		var pwNb=Number(pw);
        var ph = Ext.getCmp('pageheight').getValue().replace(/\s*/g,"");
        var phNb=Number(ph);
        var pt = Ext.getCmp('pagetop').getValue().replace(/\s*/g,"");
        var ptNb=Number(pt);
        var pb = Ext.getCmp('pagebottom').getValue().replace(/\s*/g,"");
        var pbNb=Number(pb);
        var pl = Ext.getCmp('pageleft').getValue().replace(/\s*/g,"");
        var plNb =Number(pl);	
        var pr = Ext.getCmp('pageright').getValue().replace(/\s*/g,"");
        var prNb =Number(pr);
        if(pw.indexOf(".")!=-1||ph.indexOf(".")!=-1||pl.indexOf(".")!=-1||pr.indexOf(".")!=-1){
        	Ext.showAlert(page_setup.validation.v31);//输入的值不可以为小数！
			return false;
        }
        if(""==pr||""==pl){
        	Ext.showAlert(page_setup.validation.v30);//页面设置左右边距请输入非负整数！
			return false;
        }
        var titlepage = Ext.getCmp('titlepage').getValue();
        var head_left = Ext.getCmp('head_left').getValue();
        var head_center = Ext.getCmp('head_center').getValue();
        var head_right = Ext.getCmp('head_right').getValue();
        var tail_left = Ext.getCmp('tail_left').getValue();
        var tail_center = Ext.getCmp('tail_center').getValue();
        var tail_right = Ext.getCmp('tail_right').getValue();
        var page_range = Ext.getCmp("pagesetupid").getForm().findField('Orientation').getGroupValue();
        var rl_w = plNb+prNb;							       
		var tb_h = ptNb + pbNb;	
		var match = /^[0-9]+$/ ;
		if(!match.test(phNb)) {
			Ext.showAlert(page_setup.validation.v0);//页面的高请输入大于110的整数！
			return false;
		}
		if(!match.test(pwNb)) {
			Ext.showAlert(page_setup.validation.v1);//页面的宽请输入大于80整数！
			return false;
		}
		if(pwNb<80) {
			Ext.showAlert(page_setup.validation.v1);//页面的宽请输入大于80整数！
			return false;
		}
		if(phNb<110) {
			Ext.showAlert(page_setup.validation.v0);//页面的高请输入大于110的整数！
			return false;
		}
		if(!match.test(ptNb)) {
			Ext.showAlert(page_setup.validation.v2);//上边距请输入非负整数！
			return false;
		}
		if(!match.test(pbNb)) {
			Ext.showAlert(page_setup.validation.v3);//下边距请输入非负整数！
			return false;
		}
		if(!match.test(plNb)) {
			Ext.showAlert(page_setup.validation.v4);//"左边距请输入非负整数！"
			return false;
		}
		if(!match.test(prNb)) {
			Ext.showAlert(page_setup.validation.v5);//"右边距请输入非负整数！"
			return false;
		}
	/*	if (ptNb < 15) {
			Ext.showAlert(page_setup.validation.v8);//上边距不能小于15！
			return false;
		}*/
		if (pbNb < 15||pt.indexOf(".")!=-1||pb.indexOf(".")!=-1||ptNb < 15) {
			Ext.showAlert(page_setup.validation.v32);//"上下边距请输入大于等于15的整数！"
			return false;
		}
		if (plNb < 0) {
			Ext.showAlert(page_setup.validation.v10);//"左边距不能小于0！"
			return false;
		}
		if (prNb < 0) {
			Ext.showAlert(page_setup.validation.v11);//"右边距不能小于0！"
			return false;
		}
		if(page_range=='0') {
			var pb = pwNb < phNb?pwNb:phNb;
			 if((pb - plNb - prNb)<80) {
				 Ext.showAlert(page_setup.validation.v12);//"纵向情况下，宽高较短方减去左右边距不能小于80，否则列显示不全！"
				 return false;
			 }
		}
		if(page_range=='1') {
			var pn = pwNb < phNb?phNb:pwNb;
			 if((pn - plNb - prNb)<80) {
				 Ext.showAlert(page_setup.validation.v13);//"横向情况下，宽高较长方减去左右边距不能小于80，否则列显示不全！"
				 return false;
			 }
		}
		if (rl_w >= pwNb) {
			Ext.showAlert(page_setup.validation.v14);//"页面设置左右页面总边距不能大于页面的宽！"
			return false;
		} else if (tb_h >= phNb) {
			Ext.showAlert(page_setup.validation.v15);//"页面设置上下页面总边距不能大于页面的高！"
			return false;
		}
		if (titlepage.length > 50) {
			Ext.showAlert(page_setup.validation.v16);//"标题内容的字数不能超过50个字!"
			return false;
		} else if (titlepage.indexOf("\"") != -1){
			Ext.showAlert(page_setup.validation.v17);//"标题内容不能包含双引号!"
			return false;
		}
		if (head_left.length > 50) {
			Ext.showAlert(page_setup.validation.v18);//"页头上左内容的字数不能超过50个字!"
			return false;
		} else if (head_center.length > 50) {
			Ext.showAlert(page_setup.validation.v19);//"页头上中内容的字数不能超过50个字!"
			return false;
		} else if (head_right.length > 50) {
			Ext.showAlert(page_setup.validation.v20);//"页头上右内容的字数不能超过50个字!"
			return false;
		} else if (tail_left.length > 50) {
			Ext.showAlert(page_setup.validation.v21);//"页尾下左内容的字数不能超过50个字!"
			return false;
		} else if (tail_center.length > 50) {
			Ext.showAlert(page_setup.validation.v22);//"页尾下中内容的字数不能超过50个字!"
			return false;
		} else if (tail_right.length > 50) {
			Ext.showAlert(page_setup.validation.v23);
			return false;
		}
		if (head_left.indexOf("\"") != -1) {
			Ext.showAlert(page_setup.validation.v24);//"页头上左内容不能包含双引号!"
			return false;
		} else if (head_center.indexOf("\"") != -1) {
			Ext.showAlert(page_setup.validation.v25);//"页头上中内容不能包含双引号!"
			return false;
		} else if (head_right.indexOf("\"") != -1) {
			Ext.showAlert(page_setup.validation.v26);//"页头上右内容不能包含双引号!"
			return false;
		} else if (tail_left.indexOf("\"") != -1) {
			Ext.showAlert(page_setup.validation.v27);//"页尾下左内容不能包含双引号!"
			return false;
		} else if (tail_center.indexOf("\"") != -1) {
			Ext.showAlert(page_setup.validation.v28);//"页尾下中内容不能包含双引号!"
			return false;
		} else if (tail_right.indexOf("\"") != -1) {
			Ext.showAlert(page_setup.validation.v29);//"页尾右中内容不能包含双引号!"
			return false;
		}
		Ext.getCmp('pageSetWin').close();
		
		if(pageSetScope.callbackfn) {
			Ext.callback(eval(pageSetScope.callbackfn),null,[pagesetupValue,titleValue,pageheadValue,pagetailValue,textValueValue]);
		}
	},
	
	initData:function(){
		var map=new HashMap();
		map.put('opt','3');
		map.put('tabid',tabid);
		Rpc({functionId : 'MM01020003',success: function(form,action){
			var result = Ext.decode(form.responseText);
			Ext.getCmp('pageSetWin').close();
			showMuster.pageSetup();
		}}, map);
	},
	//获得光标位置
	getCursorPosition:function (textAreaId) {
		var rulearea = Ext.getCmp(textAreaId);
   		var el = rulearea.inputEl.dom;//得到当前textarea对象
   		if(Ext.isIE){
   		    el.focus(); 
   		    if(document.selection!=null)
   		    {
	   		    var r = document.selection.createRange(); //返回当前网页中的选中内容的TextRange对象
	   		    exportPageSetScope.range=r;
	   		    if (r == null) {
	   		    	exportPageSetScope.selectionStart = 0; 
	   		    }
	   		    var re = el.createTextRange(); //选中内容
	   		    var rc = re.duplicate(); //所有内容 
	   		    try{
		   		    //定位到指定位置
		   		    re.moveToBookmark(r.getBookmark());  
	   		    	//【为了保持选区】rc的开始端不动，rc的结尾放到re的开始
	   		    	rc.setEndPoint('EndToStart', re); 
	   		    }catch(e){
	   		    	//表格控件点击刷新页面按钮后，此时鼠标焦点拿不到 lis 20160704
	   		    }
	   		    var text = rc.text;
	   		    text = text.replace(/[\r]/g," ");//替换回车符 lis 20160701   	
	   		    exportPageSetScope.selectionIndex = text.length; //光标位置
		   		exportPageSetScope.selectionStart = rc.text.length; 
		   		exportPageSetScope.selectionEnd = exportPageSetScope.selectionStart + re.text.length;
   		    }
   		    else
   		    {
   		    	exportPageSetScope.selectionIndex = el.selectionStart; //光标位置
   		    	exportPageSetScope.selectionStart = el.selectionStart; 
   		    	exportPageSetScope.selectionEnd = el.selectionEnd;
   		    }
   		}
	}
	
})