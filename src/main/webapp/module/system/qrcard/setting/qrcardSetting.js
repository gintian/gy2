Ext.define('QRCard.setting.qrcardSetting', {
	extend : 'Ext.panel.Panel',
	id:'qrcardSetting',
	title : qr.printQrcard,//打印二维码
	qrCardData : '',
	bodyStyle : 'overflow-x:hidden; overflow-y:scroll',
/*	scrollable:'y',*/
	modal : true,
	//设置滚动条
	constrain : true,
	listeners : {
		render:function(){
			Ext.getCmp('qrcardSetting_header').setStyle({borderStyle:'hidden hidden solid hidden'});
		}},
	//构造方法
	initComponent : function() {
		var me = this;
		me.callParent();
		me.initFrameConfig();
		me.tools = [{
		xtype : 'image',
		style : 'width:15px; height:15px;',
		src : '/module/system/qrcard/images/close_mouseout.png',
		listeners : {
				//设置当光标在当前这个panel时显示删除图标，光标不在这个panel时光标消失
				render:function(){
					this.getEl().on('mouseover',function(){
						this.getEl().dom.src='/module/system/qrcard/images/close_mouseover.png';
					},this);
					this.getEl().on('mouseout',function(){
						this.getEl().dom.src='/module/system/qrcard/images/close_mouseout.png';
					},this);
					},
				click:{
					element : 'el',
					fn:function() {
					this.destroy();
					window.location.href = "searchqrcard.html";
					}
				}
			}
		}];
	},
	//编辑框
	createCkEdit : function() {
		var me = this;
        var config = {};
        config.height = 58;
        config.language = "zh-cn";
        config.resize_enabled = false;// 是否使用“拖动以改变大小”功能
        config.removePlugins = "elementspath";// 去掉底部路径显示栏
        config.image_previewText = ' ';// 图片预览区域显示内容
        config.baseFloatZIndex = 19900;// 保证弹出菜单在最上层，不会被Ext菜单遮住
        config.toolbar = [
        	   {name : 'document',items : ['Source']}, 
               {name : 'styles',items : ['Font', 'FontSize']}, 
        	   {name : 'basicstyles',items : ['Bold', 'Italic','Underline']}, 
        	   {name : 'paragraph',items : ['JustifyLeft', 'JustifyCenter','JustifyRight', 'JustifyBlock']}, 
               {name : 'colors',items : ['TextColor', 'BGColor']},
               {name : 'tools',items : ['Maximize']}
        ];
        var CKEditor = Ext.create("EHR.ckEditor.CKEditor", {
            id : 'ckeditorid',
            width : '100%',
            margin:'5 20 0 20',
            value : me.qrCardData.detail_description,
            style:{
            	border:'1px solid #B5B8C8'
            },
            ckEditorConfig : config
        });
        var ckeditAndUseRidoContainer = Ext.create('Ext.Container',{
        	id:'description',
        	hidden:'true',
            layout:{
            	type:'vbox'
            },
            width:'100%'
        });
        ckeditAndUseRidoContainer.add(CKEditor);
        return ckeditAndUseRidoContainer;
	},
	//保存描述内容
	saveDescription : function() {
		var me = this;
		//获取组件id
		var qrid = me.qrCardData.qrid;
		//获取present下itemId为description的组件的内容（表单内容描述）
		var description = Ext.getCmp('ckeditorid').getHtml();
		//将表单内容和对应的表单id传入后台
		var vo = new HashMap();
		vo.put("qrid", qrid);
		vo.put("description", description);
		Rpc({
					functionId : 'SYS00001003',
					async : false,
					success : function(form, action) {
							var result = Ext.decode(form.responseText).result;
							if (result) {
								Ext.MessageBox.alert(qr.remind,qr.saveSuccess);
							}else{
								Ext.MessageBox.alert(qr.remind,qr.saveFailure);
							}
					},
					scope : me
				}, vo);
	},
	//进行界面的切换
	showOrHide:function(itemId){
		var me = this;
		//获取所有需要隐藏或显示的组件
		//获取组件的父组件
		var showPanel = me.query('#show')[0];
		//获取表单内容描述编辑框
		var descriptionPanel = Ext.getCmp('description');
		//获取取消按钮
		var cancelButton = showPanel.query('#cancel')[0];
		//获取保存按钮
		var saveButton = showPanel.query('#save')[0];
		//获取编辑按钮
		var editButton = showPanel.query('#edit')[0];
		//获取表单内容显示框
		var showDescriptionPanel= showPanel.query('#detail_description')[0];
		//获取表单名称
		var name = showPanel.query('#name')[0];
		//获取二维码图标
		var qrcode = showPanel.query('#qrcode')[0];
		//获取二维码提示信息
		var qrcardRemind = showPanel.query('#qrcardRemind')[0];
		
		//当在编辑状态时
		if(itemId=='edit'){
			editButton.hide();
			showDescriptionPanel.hide();
			saveButton.show();
			cancelButton.show();
			descriptionPanel.show();	
		//在保存状态
		}else if(itemId=='save'){
			cancelButton.hide();
			descriptionPanel.hide();
			saveButton.hide();
			editButton.show();
			showDescriptionPanel.show();
			showPanel.query('#detail_description')[0].setHtml(Ext.getCmp('ckeditorid').getHtml());
		//在取消状态时	
		}else if(itemId=='cancel'){
			saveButton.hide();
			descriptionPanel.hide();
			cancelButton.hide();
			editButton.show();
			showDescriptionPanel.show();
		}
	},
	//主函数，入口
	initFrameConfig : function() {
		var me = this;
		//设置导出和打印按钮
		var exportButton = Ext.widget('container',{
			scope:me,
			width:70,
			height:21,
			style:'background:url(/module/system/qrcard/images/buttonground-blue.png);font-size:12px;color:white;cursor:pointer;line-height:21px;text-align:center',
			html:qr.takeOut,
			listeners : {
				element:'el',
				click:function() {
					//点击导出先进行数据保存（如果在编辑状态下转换成非编辑状态）
					me.showOrHide('save');
					//将刚更新的数据赋值到显示框中
					var businessDescription = Ext.getCmp('ckeditorid').getHtml();
					var imgHTML = me.query('#qrcode')[0].config.html;
					//获取二维码提示信息
					var qrcardRemind = me.query('#qrcardRemind')[0].config.html;
					var qrid = me.qrCardData.qrid;
					var name = me.qrCardData.name;
					//将所需数据进行封装并执行pdf导出类
					var vo = new HashMap();
					//zhangh 2020-1-10 【54460】Oracle19C+was8.5：入职登记二维码制作，打印二维码/导出报会话超时
					vo.put("html", getEncodeStr(businessDescription+imgHTML+qrcardRemind));
					vo.put("qrid", qrid);
					vo.put("name", name);
					Rpc({
							functionId : 'SYS00001004',
							async : false,
							success : function(form, action) {
							//获取导出文件的姓名 
							var pdfName = Ext.decode(form.responseText).pdfName;
							//先获取download标签对象
							var download = document.getElementById('download');
							//xus 20/4/20 vfs改造
//							download.href='/servlet/DisplayOleContent?filename='+pdfName;
							download.href='/servlet/vfsservlet?fromjavafolder=true&fileid='+pdfName;
							download.click();
						},
						scope : this
					}, vo);	
				}
			}
		});
		var printButton = Ext.widget('container',{
			scope:me,
			width:70,
			height:21,
			margin:'0 0 0 30',
			style:'background:url(/module/system/qrcard/images/buttonground-blue.png);font-size:12px;color:white;cursor:pointer;line-height:21px;text-align:center',
			html:qr.print,
			listeners : {
				element:'el',
				click:function() {
					//点击打印先进行数据保存
					me.showOrHide('save');
					var businessDescription = Ext.getCmp('ckeditorid').getHtml();
					//二维码的内容
					var imgHTML = me.query('#qrcode')[0].config.html;
					//获取二维码提示信息
					var qrcardRemind = me.query('#qrcardRemind')[0].config.html;
					//获取printHTML标签
					var printHTML = document.getElementById('printHTML');
					//将内容添加到printHTML标签
					printHTML.innerHTML = businessDescription+"<br><br>"+imgHTML+qrcardRemind;
					//获取printIframe标签
					var printIframe = document.getElementById('printIframe');
					//将printHTML标签的内容放到printIframe标签中
					printIframe .contentWindow.document.body.innerHTML=printHTML.innerHTML;
					//进行延时打印操作解决二维码不加载的问题
					setTimeout(function(){
						printIframe.contentWindow.focus();
						printIframe.contentWindow.print();
					},20);
				}
			}
		});
		//说明Panel
		var explainPanel = Ext.create("Ext.panel.Panel", {
			width : '100%',
			height : 130,
			border : false,
			margin:'10 0 0 0',
			html : "<div style='text-indent:1.5em;font-size:20px;color:#ff9224;'>"+qr.explain +"</div><br>"//说明
					+ "<div style='text-indent:4em;font-size:15px;'>"+qr.qrcardContentOne+"</div><br>"//1.您可以打印二维码，当员工入职时，显示员工扫码
					+ "<div style='text-indent:4em;font-size:15px;'>"+qr.qrcardContentTwo+"</div><br>"//2.员工扫码后，可在线填写入职登记表
					+"<iframe id='printIframe' style='width:0px;height:0px;' frameborder=0></iframe>"
					+"<div id='printHTML' style='width:0px;height:0px;display:none;'></div>"
					+"<a style='display:none;' target='_blank' id='download'></a>"
		})
	var buttonPanel =  Ext.create('Ext.panel.Panel', {
		xtype : 'panel',
		layout : {
			pack: 'end',
			type : 'hbox'
		},
       border : false,
       height:40,
       width :'97%',
       items:[exportButton,printButton]
	})
	me.add(explainPanel);
	me.add(buttonPanel);
	me.display();
	},
	//展示内容
	display : function() {
		var me = this;
		var ckedit = me.createCkEdit();
		var show = Ext.create('Ext.panel.Panel', {
			width : '100%',
			margin : '0 1 0 0',//上右下左
			itemId :'show',
			border : true,
			items : [{
						xtype : 'image',
						itemId:'edit',
						src : '/module/system/qrcard/images/setting_editor.jpg',
						style : 'float:right;cursor:pointer;',
						margin : '20 20 0 0',//上右下左
						listeners : {
							click : {
								element : 'el',
								fn : function() {
									me.showOrHide('edit');
								}
							}
						}
					}, {
						xtype : 'component',
						itemId:'save',
						html : "<div style='color:#4089D2;font-size:15px'>"+qr.save+"</div>",//保存
						hidden:'true',
						height : 20,
						style : 'float:right;cursor:pointer;',
						margin : '20 20 0 0',//上右下左
						listeners : {
							click : {
								element : 'el',
								fn : function() {
									me.showOrHide('save');
									me.saveDescription();
								}
							}
						}
					}, {
						xtype : 'component',
						itemId:'cancel',
						html : "<div style='color:#4089D2;font-size:15px'>"+qr.cancel+"</div>",//取消
						height : 20,
						hidden:'true',
						style : 'float:right;cursor:pointer;',
						margin : '20 20 0 0',//上右下左
						listeners : {
							click : {
								element : 'el',
								fn : function() {
									me.showOrHide('cancel');
								}
							}
						}
					},{
						xtype : 'component',
						itemId:'name',
						width : '100%',
						height : '30%',
						margin : '50 0 0 0',//上右下左
						html : "<div style='text-align:center;font-size:20px;'>"+me.qrCardData.name+"</div><br>"
					}, {
						xtype : 'component',
						itemId : 'detail_description',
						style:'margin:0 auto',
						width : '100%',
						html : me.qrCardData.detail_description
					},ckedit,{
						xtype:'component',
						itemId:'qrcode',
						margin : '10 0 0 0',// 上右下左
						width:'100%',
						height :118,
						html:'<div style="width:100%;text-align:center;"><img src="'
								+me.qrCardData.hrp_logon_url
								+'/servlet/TwodimensionCodeServlet?url='
								+encodeURI(encodeURI(me.qrCardData.hrp_logon_url
								+ '/module/system/qrcard/mobilewrite/qrcardmain.jsp?qrid='
								+ me.qrCardData.qrid))+'&width=118&height=118"/></div>'
					},{
						xtype : 'component',
						itemId:'qrcardRemind',
						style : 'float:center',
						width : '100%',
						height : '30%',
						margin : '0 0 10 0',// 上右下左
						html : "<div style='text-align:center; font-family: Microsoft YaHei'>"+qr.qrcardRemind+"</div>"//手机扫一扫，填写个人信息
					}]
		});
		var bottomPanel = Ext.create("Ext.panel.Panel", {
					layout : {
						type : 'vbox'
					},
					width : '94%',// 宽
					style:'margin:10px auto',
					border:false,
					items :show
				});
		me.add(bottomPanel);
	}
})