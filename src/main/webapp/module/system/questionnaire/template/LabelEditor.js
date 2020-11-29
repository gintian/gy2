Ext.define("QuestionnaireTemplate.LabelEditor",{
	extend:'Ext.Component',
	alias:'widget.labeleditor',
	requires:['EHR.ckEditor.CKEditor'],
	//纯文本
	text:'',
	//富文本
	longText:undefined,
	//是否可编辑
	readOnly:false,
	//传入对应功能的具体执行方法
	menuConfig:{
		//ckeditor:false,
		up:undefined,
		down:undefined,
		left:undefined,
		right:undefined,
		remove:undefined,
		scope:undefined
	},
	callback:Ext.emptyFn,
	scope:undefined,
	//初始化控件
	initComponent:function(){
		this.callParent();
		this.longText=this.longText||this.text;
		this.update(this.longText);
		if(this.readOnly)
			return;
		//初始化菜单
		this.initMenu();
	},
	initMenu:function(){
		//菜单只创建一个，如果有了不再创建
		if(Ext.getCmp("qn_questionItemEditor"))
			return;
		//初始化菜单选项
		Ext.widget('container',{
				layout:'vbox',
				id:'qn_questionItemEditor',
				shadow:false,
				floating:true,
				items:[{
					xtype:'container',
					style: {borderColor:'#c5c5c5', borderStyle:'solid', borderWidth:'1px',backgroundColor:'white'},
					defaults:{
					   listeners:{
						   render:function(){
							   var qq = this.up('#qn_questionItemEditor');
							   this.getEl().on('click',qq.funHandler,qq,this.itemId);
						   }
					   }	
					},
					//菜单项（文本编辑、上移、下移、左移、右移、删除），按menuConfig决定是否显示
					items:[
					   {xtype:'image',itemId:'ckeditor',src:rootPath+'/module/system/questionnaire/images/textEdit.png',width:24,height:24},
					   {xtype:'image',itemId:'up',src:rootPath+'/module/system/questionnaire/images/lastquestion.png',width:24,height:24},
					   {xtype:'image',itemId:'down',src:rootPath+'/module/system/questionnaire/images/nextquestion.png',width:24,height:24},
					   {xtype:'image',itemId:'left',src:rootPath+'/module/system/questionnaire/images/left.png',width:24,height:24},
					   {xtype:'image',itemId:'right',src:rootPath+'/module/system/questionnaire/images/right.png',width:24,height:24},
					   {xtype:'image',itemId:'remove',src:rootPath+'/module/system/questionnaire/images/closebutton.png',width:24,height:24}
					]
				},{
					xtype:'textfield'
				}],
				//判断是否隐藏
				hiddenIf: function(e) {
			        var me = this;
			        if(!me.isVisible())
			           return;
			           
			        var windowActived = false;   
			        var htmlWindow = Ext.getCmp('htmlEditWindow');
			        if(htmlWindow && htmlWindow.isVisible())
			           windowActived=true;
			        
			        if (!me.isDestroyed && !e.within(me.bodyEl, false, true) && !me.owns(e.target) && !windowActived) {
			        	    me.completeEdit();
			        }
			    },
				listeners:{
					render:function(){
						//当触发鼠标滚动、点击时判断是否隐藏此控件
						this.mon(Ext.getDoc(), {
			                mousewheel: this.hiddenIf,
			                mousedown: this.hiddenIf,
			                scope: this
			            });
					}
				},
				/*
				 * 菜单监听
				 */
				funHandler:function(a,b,c){
				    var me = this;
				    //xiegh 20170418 bug27014
				    var mainPanelOBJ = Ext.getCmp('mainPanelID'),scrolly =mainPanelOBJ.getScrollY(); 
				    //富文本编辑
				    if(c=='ckeditor'){
				       var value=me.child('textfield').value;
				       if(me.label.longText.indexOf("</")>-1 || me.label.longText.indexOf("&nbsp;")>-1 || me.label.longText.indexOf("<br")>-1){
				          value=me.label.longText;
				       }
				       
				       
				       var htmlEditWindow = Ext.getCmp('htmlEditWindow');
				       if(htmlEditWindow){
				            htmlEditWindow.label = me.label;
				            htmlEditWindow.items.items[0].setValue(value);
				            htmlEditWindow.show();
				       }else{
				            me.createHtmlEditWindow(value);
				       }
				       
				    }
					if(this.label.menuConfig[c]){
						Ext.callback(this.label.menuConfig[c],this.label.menuConfig.scope,[this.label]);
					}
					if(c=='remove'){
					 this.setVisible(false);
					 this.label.fireEvent("remove");
					 mainPanelOBJ.setScrollY(scrolly);
					}else{
						var position = this.label.getPosition();
						this.setPosition(position[0]-4,position[1]-27);
					}
				},
				createHtmlEditWindow:function(value){
					Ext.widget('window',{
				          width:700,
				          height:300,
				          label:this.label,
				          layout:'fit',
				          modal:true,
				          closeAction:'hide',
				          id:'htmlEditWindow',
				          resizable:false,
				          buttonAlign:'center',
				          items:{xtype:'ckeditor',height:200,width:688,functionType:'simple',value:value},
				          buttons:[{text:common.button.ok,handler:function(){
				          			var editor = this.ownerCt.ownerCt.child('ckeditor');
				            	    //两边去空格。ie兼容模式不支持String的trim方法，使用此方法
				            	    var html = editor.getHtml().replace(/^\s\s*/, '' ).replace(/\s\s*$/, '' );
				            	    //<p>标签会搞乱显示，将<p>标签去掉
				            	    html = new RegExp("^<p>").test(html)?html.replace("<p>",""):html;
				            	    html = new RegExp("</p>$").test(html)?html.substring(0,html.length-4):html;
				            	    Ext.getCmp("qn_questionItemEditor").setVisible(false);
				            	    var pureText = editor.getText();
				            	    var label = this.ownerCt.ownerCt.label;
				            	    if(Ext.String.trim(pureText).length>0)
				                    	label.setText(editor.getText(),html);
				                    this.ownerCt.ownerCt.close();
				                    label.fireEvent('completeedit');
				          }},{text:common.button.cancel,handler:function(){this.ownerCt.ownerCt.close();}}],
				          close:function(){
				              var htmlWindow = this;
				              var editor = this.child('ckeditor');
				                    editor.editor.focus();
				              Ext.TaskManager.start({
				              	run: function() {
							         htmlWindow.doClose();
							         delete htmlWindow;
							    },
							    interval: 500,
							    repeat: 1
				              });
				          }
				       }).show();
				},
				
				setMenu:function(config){
					var up = this.queryById('up'),
					    down = this.queryById('down'),
					    left = this.queryById('left'),
					    right = this.queryById('right'),
					    remove = this.queryById('remove');
					//向上移动
				    	if(config.up)
				    		up.setVisible(true);
				    	else
				    		up.setVisible(false);
				    	//向下移动
				    	if(config.down)
				    		down.setVisible(true);
				    	else
				    		down.setVisible(false);
				    	//向左移动
				    	if(config.left)
				    		left.setVisible(true);
				    	else
				    		left.setVisible(false);
				    	//向右移动
				    	if(config.right)
				    		right.setVisible(true);
				    	else
				    		right.setVisible(false);
				    	//删除
				    	if(config.remove)
				    		remove.setVisible(true);
				    	else
				    		remove.setVisible(false);
				    		
				},
				startEditing:function(label){
					this.setMenu(label.menuConfig);
					var width = label.getWidth()+10,
					    position = label.getPosition(),
					    textfield = this.child('textfield');
					this.setPosition(position[0]-4,position[1]-29);
					var text = label.text;
					text = text.replace(/(\n)/g, "");  
					text = text.replace(/(\t)/g, "");
					text = text.replace(/(\r)/g, "");
					text = text.replace(/<\/?[^>]*>/g, ""); 
					text = text.replace(/&nbsp;/g, " ");//此行代码意思 是将多个空格变成一个空格
					//text = text.replace(/\s*/g, ""); //xiegh 20170701  bug:20363     此行代码是将空格去除    
					textfield.setValue(text);
					width = width>100?width:100;
					textfield.setWidth(width);
					textfield.setHeight(label.getHeight());
					this.setVisible(true);
					//如果有html标签，只能调用富文本编辑器
					if(label.longText.indexOf("</")>-1 || label.longText.indexOf("&nbsp;")>-1 || label.longText.indexOf("<br")>-1){
						//textfield.setReadOnly(true);//解决ckeditor报没有权限错误
						textfield.focus();
						textfield.edit = false;
						textfield.getEl().on("click",this.funHandler,this,'ckeditor');
					}else {
						textfield.focus();
						textfield.edit = true;
						textfield.setReadOnly(false);
						textfield.getEl().clearListeners( ); 
					}
					this.label = label;
					
					
				},
				completeEdit:function(){
					var me = this,
					   textfield = me.child('textfield');
					me.setVisible(false);
					if(!textfield.edit){
						me.label.fireEvent("completeedit");
						 return;
					}
					var text = textfield.getValue();
					text = Ext.String.trim(text);
					if(text && text.length>0 && textfield.validate())
						me.label.setText(text);
					me.label.fireEvent("completeedit");
				}
			
		});
	},
	
	
	listeners:{
		render:function(){
			var me = this;
			if(this.readOnly)
				return;
			this.getEl().on("click",me.beEdit,me);
			
			
			
		}

	},
	beEdit:function(){
	    var me = this;
		if(!me.menuConfig)
					return;
		var editor = Ext.getCmp("qn_questionItemEditor");
		if(editor){
					editor.startEditing(me);
		}
	},
	setText:function(text,longText){
		this.text = text;
		this.longText = longText||text;
		this.update(this.longText);
	}
});