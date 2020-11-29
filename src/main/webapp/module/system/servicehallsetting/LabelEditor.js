Ext.define("ServiceHall.LabelEditor",{
	extend:'Ext.Component',
	alias:'widget.labeleditor',
	//纯文本
	text:'',
	//是否可编辑
	readOnly:false,
	deletable:true,
	scope:undefined,
	//初始化控件
	initComponent:function(){
		this.callParent();
		this.update(this.text);
		this.initMenu();
	},
	initMenu:function(){
		//菜单只创建一个，如果有了不再创建
		if(Ext.getCmp("servicehall_label_menu"))
			return;
		//初始化菜单选项
		Ext.widget('container',{
				layout:'vbox',
				id:'servicehall_label_menu',
				shadow:false,
				floating:true,
				items:[{
					xtype:'container',
					style: {borderColor:'#c5c5c5', borderStyle:'solid', borderWidth:'1px',backgroundColor:'white'},
					defaults:{
					   listeners:{
						   render:function(){
							   var qq = this.up('#servicehall_label_menu');
							   this.getEl().on('click',qq.funHandler,qq,this.itemId);
						   }
					   }	
					},
					items:[
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
					if(c=='remove'){
					 this.setVisible(false);
					 this.label.fireEvent("remove",this.label);
					}else{
						var position = this.label.getPosition();
						this.setPosition(position[0]-4,position[1]-27);
					}
				},
				startEditing:function(label){
					var width = label.getWidth()+10,
					    position = label.getPosition(),
					    textfield = this.child('textfield');
					if(!label.deletable){
						this.child('container').setVisible(false);
						this.setPosition(position[0]-4,position[1]);
					}else{
						this.child('container').setVisible(true);
						this.setPosition(position[0]-4,position[1]-29);
					}
					
					var text = label.text;
					textfield.setValue(text);
					width = width>100?width:100;
					textfield.setWidth(width);
					textfield.setHeight(label.getHeight()<20?20:label.getHeight());
					this.setVisible(true);
					textfield.focus();
					textfield.edit = true;
					textfield.setReadOnly(false);
					textfield.getEl().clearListeners( ); 
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
					var text = me.child('textfield').value;
					text = Ext.String.trim(text);
					if(text.length<1 || text==me.label.text)
						return;
					
					if(text && text.length>0)
						me.label.setText(text);
					me.label.fireEvent("completeedit",me.label);
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
		var editor = Ext.getCmp("servicehall_label_menu");
		if(editor){
			editor.startEditing(me);
		}
	},
	setText:function(text){
		this.text = text;
		this.update(text);
	}
});