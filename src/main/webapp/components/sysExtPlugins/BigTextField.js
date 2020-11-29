Ext.define('SYSP.BigTextField', {
        extend: 'Ext.form.field.Picker',
        alias: 'widget.bigtextfield',
     // editable: false,//此处影响在ie下切换输入法 lis 2016-03-10
        maxLength:Number.MAX_VALUE,
        hideTrigger:true,
        inputAttrTpl:'style="background-image:none;border:none;filter: alpha(opacity=0); opacity:0; "',
        
        createPicker: function() {
          var me = this;
 	      return Ext.create('Ext.panel.Panel', {
 	      	ownerLayout: me.getComponentLayout(),
 	    	    floating: true,
 	    	    shadow:false,
 	        minHeight:150,
 	        //width:this.width<300?300:this.width,
 	        layout:'fit',
 	        header:false,
 	        border:0,
 	        items:{
 	          	xtype:'textareafield',itemId:'bigText',
 	        	    grow:true,
 	        	    maxLength:me.maxLength,enforceMaxLength:true,border:false,
 	        	    listeners:{
 	        		    change:function(me,value){
 	        			    if(value.length>me.maxLength)
 	        				    me.setValue(value.substr(0,me.maxLength));
 	        		}
 	        	   }
 	        }
 	       });
 	    },
 	    /*
 	     * picker 收缩时 获取textarea输入值，并取消控件编辑状态
 	     */
 	    onCollapse:function(e){
 	       var me = this,
 	           value = this.picker.getComponent('bigText').getValue();
 	       me.setValue(value);
 		   me.triggerBlur(e);
 	    },
 	    /*
 	     * 获取焦点 时 展开picker，给textarea赋值,修改picker位置
 	     */
 	    onFocus:function(){
	 	    	this.expand();//展开picker
	 	    	var  width = this.width<300?300:this.width;
	 	    	var textarea = this.picker.getComponent('bigText');
	 	    	this.picker.setWidth(width);
	 	    	textarea.setWidth(width);
	 	    	textarea.setValue(this.getValue());
	 	    	this.picker.setPosition(0,0,false);
	 	    	textarea.focus();//给textarea 输入焦点
 	    	
 	    },
 	    getValue:function(){
 	    	return this.value;
 	    },
 	    setValue:function(value){
 	    	if(value){
 	    	   value = value.replace(/<br>/g,'\n');
 	    	   value = value.replace(/&nbsp;/g,' ');
 	    	}
 	    	this.value = value;
 	    }
    });