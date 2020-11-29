/**
 * sunjian 2018-1-24
 * 导出的页面设置
 */  
Ext.define('EHR.exportPageSet.ExportPageSet',{
	//构造
    constructor:function(config){
    	exportPageSetScope = this;
		exportPageSetScope.rsid = config.rsid;//表类号
		exportPageSetScope.rsdtlid = config.rsdtlid;//具体表号
		exportPageSetScope.title = config.title;//表名（弹窗标题）
		exportPageSetScope.callbackfn = config.callbackfn;//返回值
		exportPageSetScope.result = config.result;
		this.init();
    },
	
    //创建页面panel
	init:function(){
		var result = exportPageSetScope.result;
        pagetabs = Ext.create('Ext.tab.Panel', {
		    width : 550,
		    height : 390,
			activeTab : exportPageSetScope.result.isExcel=='0'?1:0,//excel没有页面设置
			id : 'pageTab',
			plain : true,
					  //全部页签
					items : [ {
						id:"pagesetup",
						title : '页面设置',//页面设置	
						bodyPadding : 5,
						hidden : exportPageSetScope.result.isExcel=='0'?true:false,
					}, {
						id:"title",
						title : '标题',//标题					
						bodyPadding : 5
					}, {
						title : '页头内容',//页头内容
						id:"pagehead",				
						bodyPadding : 10
					}, {
						title : '页尾内容',//页尾内容				
						id:"pagetail",		
						bodyPadding : 10
					}, {
						title : '正文',//正文				
						id:"text",		
						bodyPadding : 10
					} ]

				});		
		       //调用页面设置页签js文件	
		       Ext.require( 'EHR.exportPageSet.pageSet.PageSetup', function(){
	        		Ext.create( 'EHR.exportPageSet.pageSet.PageSetup' ,{result:result});
	        	});
		       Ext.require( 'EHR.exportPageSet.pageSet.PageTitle', function(){
	        		Ext.create( 'EHR.exportPageSet.pageSet.PageTitle',{result:result} );
	        	});
		       Ext.require( 'EHR.exportPageSet.pageSet.PageHead', function(){
	        		Ext.create( 'EHR.exportPageSet.pageSet.PageHead',{result:result} );
	        	});     
		       Ext.require( 'EHR.exportPageSet.pageSet.PageTail', function(){
	        		Ext.create( 'EHR.exportPageSet.pageSet.PageTail',{result:result} );
	        	});     
		       Ext.require( 'EHR.exportPageSet.pageSet.PageText', function(){
	        		Ext.create( 'EHR.exportPageSet.pageSet.PageText',{result:result} );
	        	}); 		       		       
		       //页面窗口的panel
		       var formcs = Ext.widget({
					xtype : 'form',
					border : false,
					items : pagetabs,
					id : 'formPanel',
					minButtonWidth : 50,
					buttons : [
							{
								xtype : 'tbfill'
							},
							{
								text : "保存",//保存
								style : 'margin-right:5px',								
								handler : function() {
									exportPageSetScope.Exporting("0");
								}
							},{
								text : "关闭",//取消
								handler : function() {
									win.close();
								}
							}, {
								xtype : 'tbfill'
							} ]
				});
		       
		       //将页面作为窗口展现出来
		       var win = Ext.widget("window", {
					title : " 页面设置 ",
					id:'ExportWin',
					minButtonWidth : 45,//按钮的最小宽度
					resizable : false,//是否可调整大小的
					border : false,//边框去掉
					modal : true,//模态窗口
					closeAction : 'destroy',//窗口销毁			  
					items : [formcs],
				});
				win.show();			
		
	},
	
	//导出pdf
	Exporting:function(type){
		
		var pagesetupValue = Ext.getCmp("pagesetupid").getForm().getValues(); //页面设置
        var titleValue = Ext.getCmp("titleid").getForm().getValues();  //标题设置 									
		var pageheadValue = Ext.getCmp("pageheadid").getForm().getValues(); //页头设置
		var pagetailValue = Ext.getCmp("pagetailid").getForm().getValues(); //页尾设置
		var textValueValue = Ext.getCmp("textid").getForm().getValues();  //正文设置
		if(type == "0") {
			var pw = Ext.getCmp('pagewidth').getValue();
			var pwNb=Number(pw);
	        var ph = Ext.getCmp('pageheight').getValue();
	        var phNb=Number(ph);
			pagesetupValue['pageheight-input']=phNb;
			pagesetupValue['pagewidth-input']=pwNb;
	        var pt = Ext.getCmp('pagetop').getValue();
	        var ptNb=Number(pt);
	        var pb = Ext.getCmp('pagebottom').getValue();
	        var pbNb=Number(pb);
	        var pl = Ext.getCmp('pageleft').getValue();
	        var plNb =Number(pl);							        
	        var pr = Ext.getCmp('pageright').getValue();
	        var prNb =Number(pr);
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
				Ext.showAlert("页面的高请输入大于110的整数！");
				return false;
			}
			if(!match.test(pwNb)) {
				Ext.showAlert("页面的宽请输入大于80整数！");
				return false;
			}
			if(!match.test(ptNb)) {
				Ext.showAlert("上边距请输入非负整数！");
				return false;
			}
			if(!match.test(pbNb)) {
				Ext.showAlert("下边距请输入非负整数！");
				return false;
			}
			if(!match.test(plNb)) {
				Ext.showAlert("左边距请输入非负整数！");
				return false;
			}
			if(!match.test(prNb)) {
				Ext.showAlert("右边距请输入非负整数！");
				return false;
			}
			if (pwNb < 50) {
				Ext.showAlert("页面设置页面的宽不能小于110！");
				return false;
			}
			if (phNb < 80) {
				Ext.showAlert("页面设置页面的高不能小于110！");
				return false;
			}
			if (ptNb < 15) {
				Ext.showAlert("上边距不能小于15！");
				return false;
			}
			if (pbNb < 15) {
				Ext.showAlert("下边距不能小于15！");
				return false;
			}
			if (plNb < 0) {
				Ext.showAlert("左边距不能小于0！");
				return false;
			}
			if (prNb < 0) {
				Ext.showAlert("右边距不能小于0！");
				return false;
			}
			if(page_range=='0') {
				var pb = pwNb < phNb?pwNb:phNb;
				 if((pb - plNb - prNb)<80) {
					 Ext.showAlert("纵向情况下，宽高较短方减去左右边距不能小于80，否则列显示不全！");
					 return false;
				 }
			}
			if(page_range=='1') {
				var pn = pwNb < phNb?phNb:pwNb;
				 if((pn - plNb - prNb)<80) {
					 Ext.showAlert("横向情况下，宽高较长方减去左右边距不能小于80，否则列显示不全！");
					 return false;
				 }
			}
			if (rl_w >= pwNb) {
				Ext.showAlert("页面设置左右页面总边距不能大于页面的宽！");
				return false;
			} else if (tb_h >= phNb) {
				Ext.showAlert("页面设置上下页面总边距不能大于页面的高！");
				return false;
			}
			if (titlepage.length > 50) {
				Ext.showAlert("标题内容的字数不能超过50个字!");
				return false;
			} else if (titlepage.indexOf("\"") != -1){
				Ext.showAlert("标题内容不能包含双引号!");
				return false;
			}
			if (head_left.length > 50) {
				Ext.showAlert("页头上左内容的字数不能超过50个字!");
				return false;
			} else if (head_center.length > 50) {
				Ext.showAlert("页头上中内容的字数不能超过50个字!");
				return false;
			} else if (head_right.length > 50) {
				Ext.showAlert("页头上右内容的字数不能超过50个字!");
				return false;
			} else if (tail_left.length > 50) {
				Ext.showAlert("页尾下左内容的字数不能超过50个字!");
				return false;
			} else if (tail_center.length > 50) {
				Ext.showAlert("页尾下中内容的字数不能超过50个字!");
				return false;
			} else if (tail_right.length > 50) {
				Ext.showAlert("页尾右中内容的字数不能超过50个字!");
				return false;
			}
			if (head_left.indexOf("\"") != -1) {
				Ext.showAlert("页头上左内容不能包含双引号!");
				return false;
			} else if (head_center.indexOf("\"") != -1) {
				Ext.showAlert("页头上中内容不能包含双引号!");
				return false;
			} else if (head_right.indexOf("\"") != -1) {
				Ext.showAlert("页头上右内容不能包含双引号!");
				return false;
			} else if (tail_left.indexOf("\"") != -1) {
				Ext.showAlert("页尾下左内容不能包含双引号!");
				return false;
			} else if (tail_center.indexOf("\"") != -1) {
				Ext.showAlert("页尾下中内容不能包含双引号!");
				return false;
			} else if (tail_right.indexOf("\"") != -1) {
				Ext.showAlert("页尾右中内容不能包含双引号!");
				return false;
			}
			Ext.getCmp('ExportWin').close();
		}else {
			Ext.getCmp("formPanel").form.reset();
			Ext.getCmp('pagetype').setValue( 'A4');//页面设置初始化
            Ext.getCmp('pageleft').setValue( '31');  
	        Ext.getCmp('pagetop').setValue( '21');
	        Ext.getCmp('pageright').setValue( '31');  
            Ext.getCmp('pageheight').setValue( '297');  
	        Ext.getCmp('pagewidth').setValue( '210');
	        Ext.getCmp('pagebottom').setValue( '21'); 
	        Ext.getCmp('pagewidth').setReadOnly(true);
			Ext.getCmp('pageheight').setReadOnly(true);
			
	        /*Ext.getCmp('colorTitle').setValue("#000000");//标题颜色初始化
	        Ext.getCmp('colorHead').setValue("#000000");//页头颜色
	        Ext.getCmp('colorTail').setValue("#000000");//页尾
	        Ext.getCmp('phead_fc').setValue("#000000");//正文
	        Ext.getCmp('text_fc').setValue("#000000");*/
	        
		}
		
		if(exportPageSetScope.callbackfn) {

			var data={
				Pagetype:pagesetupValue['pagetype-input']?pagesetupValue['pagetype-input']:'',
				Left:pagesetupValue['pageleft-input'],
				Top:pagesetupValue['pagetop-input'],
				Right:pagesetupValue['pageright-input'],
				Bottom:pagesetupValue['pagebottom-input'],
				Orientation:pagesetupValue.Orientation,
				Height:pagesetupValue['pageheight-input']?pagesetupValue['pageheight-input']:0,
				Width:pagesetupValue['pagewidth-input']?pagesetupValue['pagewidth-input']:0,

				title_content:titleValue.titleTextarea,
				title_fontface:titleValue['title_fn-input'],
				title_fontsize:titleValue['title_fz-input'],

				title_fontblob:'',
				title_fontitalic:'',
				title_underline:'',
				title_delline:'',
				title_color:titleValue['colorTitle-input'],

				head_left:pageheadValue.hlTextarea,
				head_center:pageheadValue.hcTextarea,
				head_right:pageheadValue.hrTextarea,
				head_fontblob:'',
				head_fontitalic:'',
				head_underline:'',
				head_delline:'',
				head_fontface:pageheadValue['head_fn-input'],
				head_fontsize:pageheadValue['head_fz-input'],
				head_fc:pageheadValue['colorHead-input'],
				head_flw_hs:'',
				head_fmw_hs:'',
				head_frw_hs:'',

				tail_left:pagetailValue.tlTextarea,
				tail_center:pagetailValue.tcTextarea,
				tail_right:pagetailValue.trTextarea,
				tail_fontblob:'',
				tail_fontitalic:'',
				tail_underline:'',
				tail_delline:'',
				tail_fontface:pagetailValue['tail_fn-input'],
				tail_fontsize:pagetailValue['tail_fz-input'],
				tail_fc:pagetailValue['colorTail-input'],
				tail_flw_hs:'',
				tail_fmw_hs:'',
				tail_frw_hs:'',
				text_fn:textValueValue['text_fn-input'],
				text_fz:textValueValue['text_fz-input'],
				text_fb:'',
				text_fi:'',
				text_fu:'',
				text_fc:textValueValue['text_fc-input'],

				phead_fn:textValueValue['phead_fn-input'],
				phead_fz:textValueValue['phead_fz-input'],
				phead_fb:'',
				phead_fi:'',
				phead_fu:'',
				phead_fc:textValueValue['phead_fc-input'],
			};

			var list=new Array().concat(titleValue.checkboxgroupTitle);

			Ext.Array.each(list,function (record) {
				if(record=='#fb[1]'){
					data.title_fontblob=record;
				}
				if(record=='#fu[1]'){
					data.title_underline=record;
				}
				if(record=='#fs[1]'){
					data.title_delline=record;
				}
				if(record=='#fi[1]'){
					data.title_fontitalic=record;
				}
			});

			list=new Array().concat(pageheadValue.phCheckboxgroup);
			Ext.Array.each(list,function (record) {
				if(record=='#fb[1]'){
					data.head_fontblob=record;
				}
				if(record=='#fu[1]'){
					data.head_underline=record;
				}
				if(record=='#fs[1]'){
					data.head_delline=record;
				}
				if(record=='#fi[1]'){
					data.head_fontitalic=record;
				}
			});
			list=new Array().concat(pagetailValue.ptCheckboxgroup);
			Ext.Array.each(list,function (record) {
				if(record=='#fb[1]'){
					data.tail_fontblob=record;
				}
				if(record=='#fu[1]'){
					data.tail_underline=record;
				}
				if(record=='#fs[1]'){
					data.tail_delline=record;
				}
				if(record=='#fi[1]'){
					data.tail_fontitalic=record;
				}
			});
			list=new Array().concat(pageheadValue.homeShow);
			Ext.Array.each(list,function (record) {
				if(record=='lHeadChecked'){
					data.head_flw_hs=record;
				}
				if(record=='mHeadChecked'){
					data.head_fmw_hs=record;
				}
				if(record=='rHeadChecked'){
					data.head_frw_hs=record;
				}
			});
			list=new Array().concat(pagetailValue.footShow);
			Ext.Array.each(list,function (record) {
				if(record=='lFootChecked'){
					data.tail_flw_hs=record;
				}
				if(record=='mFootChecked'){
					data.tail_fmw_hs=record;
				}
				if(record=='rFootChecked'){
					data.tail_frw_hs=record;
				}
			});
			list=new Array().concat(textValueValue.textCheckboxgroup);
			Ext.Array.each(list,function (record) {
				if(record=='#fb[1]'){
					data.phead_fb=record;
				}
				if(record=='#fi[1]'){
					data.phead_fi=record;
				}
				if(record=='#fu[1]'){
					data.phead_fu=record;
				}

			});
			list=new Array().concat(textValueValue.hiCheckboxgroup);
			Ext.Array.each(list,function (record) {
				if(record=='#fb[1]'){
					data.text_fb=record;
				}
				if(record=='#fi[1]'){
					data.text_fi=record;
				}
				if(record=='#fu[1]'){
					data.text_fu=record;
				}
			});
			Ext.callback(eval(exportPageSetScope.callbackfn),null,[pagesetupValue,titleValue,pageheadValue,pagetailValue,textValueValue,type,data]);
		}
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