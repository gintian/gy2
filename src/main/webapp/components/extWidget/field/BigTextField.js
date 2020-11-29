Ext.define('EHR.extWidget.field.BigTextField', {
        extend: 'Ext.form.field.Picker',
        alias: 'widget.bigtextfield',
        maxLength:Number.MAX_VALUE,
        hideTrigger:true,
        inputAttrTpl:'style="background-image:none;border:none;filter: alpha(opacity=0); opacity:0; "', 
        inputWrapCls:'',
        isFilterSpecialKey:undefined,//是否过滤回车键标识符
        initComponent:function(){
        		this.fieldBlank = this.allowBlank;
        		this.allowBlank = this.allowBlank?this.allowBlank:true;
        		this.callParent();
        },
        createPicker: function() {
          var me = this;
 	      return Ext.create('Ext.panel.Panel', {
 	      	ownerLayout: me.getComponentLayout(),
	 	    	floating: true,
	 	    	shadow:false,
 	        minHeight:150,
 	        layout:'fit',
 	        header:false,
 	        border:0,
 	        items:{
 	          	xtype:'textareafield',itemId:'bigText',padding:'1 0 2 0',//解决ie8，9 缺线问题
 	        	    maxLength:me.maxLength,enforceMaxLength:true,border:false,
 	        	    allowBlank:me.fieldBlank,
 	        	    validator:me.validator,
 	        	    enableKeyEvents:me.isFilterSpecialKey,
 	        	    listeners:{
 	        		    change:function(me,value){
 	        			    if(value.length>me.maxLength)
 	        				    me.setValue(value.substr(0,me.maxLength));
 	        		},
 	        		'keydown':function(e,t,o){
 	        			if(me.isFilterSpecialKey){
 	        				if(t.keyCode==13){//屏蔽回车键
 	        					t.keyCode=0
 	        					t.browserEvent.returnValue=false;
 	        				}
 	        			}
 	            	}
 	        	   }
 	        },
 	        alignTo:function(element, position, offsets,animate){
 	           var me = this,
	            el = me.el;
				position = "tl?";
				var target = element.up("td");
				
		        return me.setXY(me.getAlignToXY(target, position, offsets),
		                el.anim && !!animate ? el.anim(animate) : false);
 	        }
 	       });
 	    },
 	    onCollapse:function(e){
 	       var me = this,
 	           value = this.picker.getComponent('bigText').getValue();
 	       if(!me.fieldBlank && value.length<1)
 	       	return;
 	       me.setValue(value);
 	    },
 	    onFocus:function(){
 	      this.callParent(arguments);
 	      this.expand( );
	 	  var textarea = this.picker.getComponent('bigText');
	 	  //haosl 动态设置编辑框的宽度。
	 	  var width = this.getWidth();
	 	  width = width<300?300:width;
	 	  this.picker.setWidth(width);
          textarea.setWidth(width);
          textarea.setValue(this.getValue());
          if((this.getX( )+width)>document.documentElement.clientWidth)
              this.picker.setX(document.documentElement.clientWidth-width);
          textarea.focus();
 	    },
 	    setValue:function(value){
 	    	if(!Ext.isEmpty(value)){
	 	        value = value.replace(/&lt;/g,'<').replace(/<br>/g,'\n').replace(/&nbsp;/g,' ');
 	    	}
 	        this.value = value;
 	    },
 	    getValue:function(){
 	        return this.value;
 	    }
 	    
    });