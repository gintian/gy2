Ext.define('ServiceHall.EditService', {
	requires:["EHR.carousel.Carousel","EHR.templateSelector.TemplateSelector"],
	extend:'Ext.window.Window',
	id:'editServiceWin',
    layout:'fit',
    width:475,
    height:250,
    modal : true,//遮罩
    resizable:false,//禁止拉伸
    closable:true,//允许关闭按钮
	initComponent:function(){
		this.callParent(arguments);
		this.init();
	},
	init:function(){
		var me = this;
		me.values = {};
		Ext.apply(me.values,me.serviceData);
		if(!me.serviceCmpId)
			me.values.icon='fuwudating.png';
		var form = this.getMainPanel();
		this.add(form);
		this.show();
	},
 
    /**
     * 定义服务模板页panel
     */
   	getMainPanel:function(){
    	var me = this;
    	var templatePanel = Ext.create("Ext.form.Panel",{
    		itemId:'serviceTemplatePage',
    		padding:'15 0 0 15',
    		border:false,
            items:[{
                xtype:'textfield',
                id:'serivce_name_input',
                allowBlank:false,
                beforeLabelTextTpl:"<font color='red'>* </font>",
                fieldLabel:"<span>"+shs.servicename+"</span>",//服务名称
                value:me.values.tabname,
                labelWidth:50,
                width:370
              },{
                  xtype:'container',
                  layout:'hbox',
                  border:false,
                  padding:'5 0 0 0',
                  items:[{
                      xtype : 'component',
                      id  : 'icon',
                      style:'width:55px;',
                      html: "<span style=color:red;>* </span>"+shs.serviceicon,//服务图标
                  },{
                  	xtype:'component',
                  	html:'<div style="background:#f6f6f6;height:75px;width:160px;">'+
                  		 '<img width="36" height="36" id="service_icon_img" style="margin:19px 0px 0px 10px;" src="'+rootPath+'/components/homewidget/images/serviceicon/'+me.values.icon+'"/>'+
                  		 '</div>'
                  },{
                  	xtype : 'component',
                  	margin:'0 0 0 10',
                  	html: shs.selecticon,//选择图标
                  	style:"cursor:pointer;color:#0099cc;",
                  	listeners:{
                  		click:{
                  			element:'el',
                  			fn:me.changeTempIcon,
                  			scope:me
                  		}
                  	}
                  }]
              },{
              	xtype:'container',
              	margin:'10 0 0 0',
              	layout:'hbox',
              	items:[{
                        xtype : 'textfield',
                        id  : 'template_input',
                        beforeLabelTextTpl:"<font color='red'> * </font>",
                        fieldLabel: "<span>"+shs.servicetemplate+"</span>",//服务模板
                        readOnly:me.values.type==3?false:true,
                        labelWidth:50,
                        allowBlank: false,
                        fieldStyle:me.values.type==3?'':'background-color:#d9d9d9',//输入框样式
                        value:me.values.type==3?me.values.linkurl:me.values.tempname,
                        width:370
                    },{
                        xtype:'button',
                        text: shs.selecttemplate,
                        id:'select_template',
                        margin:'0 0 0 10',
                        menu:{
                            items: [{
                              text: shs.businessform,
                              templateType:1,
                              handler:this.selectTemplate,
                              scope:this
                            },{
                              text: shs.registryform,
                              templateType:2,
                              handler:this.selectTemplate,
                              scope:this
                            },{
                              text: shs.customurl,
                              templateType:3,
                              handler:this.selectTemplate,
                              scope:this
                            }]
                        }
                    }]
              }],
              buttonAlign:'center',
              buttons:[{
                text:shs.ok,
                formBind:true,
                handler:me.saveTemplate,
                scope:me
            }]
    	});
    	return templatePanel;
    },
    //创建选择图片window
    changeTempIcon:function(){
    	var me = this;
    	var selectWin = Ext.widget('window',{
            title:shs.selecticon,//选择服务
            id:'selectIconWin',
            resizable:false,//禁止拉伸
            modal : true,//遮罩
            bodyPadding:10,
            height:200,
            width:360,
            layout:'fit',
            listeners:{
            	click:{
            		element:'el',
            		delegate:'img[iconName]',
            		fn:function(evt,targetEle){
            			var icon = targetEle.getAttribute('iconName');
            			Ext.getDom('service_icon_img').src=rootPath+"/components/homewidget/images/serviceicon/"+icon+".png";
            			Ext.getCmp('selectIconWin').close();
            			this.values.icon = icon+".png";
            		},
            		scope:me
            	}
            }
        });
        
        var iconArray = ['fuwudating','chuguo','diaodong','fangxue','jiaban','jinxiu','shangchuan',
        				 'jixiao','kaohe','lizhi','nianmokaohe','peixun','qingjia','ruzhi',
        				 'shenbao','shenqing','tiaoxin','zhicheng','zhuangang','zhuanzheng','jingpin'];
        
        var iconsHtml = "";
        for(var i=0;i<iconArray.length;i++){
            //一套图片同时适用于PC端和手机端时，PC端就需要设置图标大小了
        	iconsHtml+='<img width="36" height="36" style="float:left;margin:5px;cursor:pointer;" iconName="'+iconArray[i]+'" src="'+rootPath+'/components/homewidget/images/serviceicon/'+iconArray[i]+'.png"/>';
        }
        
        var iconBox = Ext.widget('component',{
        	html:iconsHtml
        });
        /**
        var pp = {xtype:'container',layout:'hbox',items:[]};
        for(var i=1;i<=iconArray.length;i++){
        	if(i>1 && (i-1)%10==0){
        		panelArray.push(pp);
        		pp = {xtype:'container',layout:'hbox',items:[]};
        	}
        	pp.items.push({
        		xtype:'component',style:'cursor:pointer;',margin:'10 5 0 5',width:36,height:36,
        		html:'<img iconName="'+iconArray[i-1]+'" src="'+rootPath+'/components/homewidget/images/serviceicon/'+iconArray[i-1]+'.png"/>'
        	});
        }
        panelArray.push(pp);
        var carousel = Ext.create('EHR.carousel.Carousel',{
            items:panelArray
        });
        */
        selectWin.add(iconBox);
        selectWin.show();
    },
    
    /**
     * 选择服务模板的点击事件
     * @param {} menuitem
     */
    selectTemplate:function(menuitem){
        var me = this;
        var templateType = menuitem.templateType;
        //me.values.type=templateType;
        //input.setValue("");
        var input = Ext.getCmp('template_input');
        if(templateType==3){
        	input.setReadOnly(false);
        	input.setFieldStyle("background-color:transparent");
        	me.values.type = templateType;
        	return;
        }
        input.setReadOnly(true);
        input.setFieldStyle("background-color:#d9d9d9");
        Ext.widget('window',{
            title:shs.selecttemplate,
            height:400,
            width:300,
            layout:'fit',
            modal : true,
            items:{
                xtype:'templateselector',
                dataType:templateType,
                listeners:{
                    itemclick:function(tree,node){
                        if(node.get('leaf')){
                        	var tabid =  node.get('id');
                        	if(me.serviceMap[this.dataType+"|"+tabid] && this.dataType+"|"+tabid != me.serviceData.type+"|"+me.serviceData.tabid){
                        		Ext.showAlert(shs.templateexist);
                        		return;
                        	}
                        	
                            Ext.getCmp('template_input').setValue(node.get('text'));
                            var input = Ext.getCmp('serivce_name_input');
                            if(input.getValue()==null || input.getValue().length<1)
                            	input.setValue(node.get('text'));
                            me.values.tabid = node.get('id');
                            me.values.tempname = node.get('text');
                            me.values.type = this.dataType;
                            tree.up('window').close();
                        }
                    }
                }
            }
        }).show();
    },
    saveTemplate:function(){
    	var name = Ext.getCmp('serivce_name_input').getValue();
    	/*输入名称过滤js和html*/
    	var name = Ext.getCmp('serivce_name_input').getValue();
        name = name.replace(/(\n)/g, "");    
        name = name.replace(/(\t)/g, "");    
        name = name.replace(/(\r)/g, "");    
        name = name.replace(/<\/?[^>]*>/g, "");    
        name = name.replace(/\s*/g, "");
    	if(name.length<1|| name.indexOf("<")!=-1 ||name.indexOf(">")!=-1){
    		Ext.showAlert(shs.errorname);
    		return;
    	}
    	this.values.tabname = name;
    	if(this.values.type==undefined||this.values.type==3){
            this.values.type=3;
    		this.values.tabid='';
    		this.values.linkurl = Ext.getCmp('template_input').getValue();
    	}
    	
    	if(this.serviceCmpId && (this.values.type!=this.serviceData.type || this.values.tabid!=this.serviceData.tabid)){
    		delete this.serviceMap[this.serviceData.type+"|"+this.serviceData.tabid];
    	}
    	Ext.apply(this.serviceData,this.values);
    	
    	this.fireEvent('editcomplete',this.serviceData,this.serviceCmpId);
    	this.close();
    }
    
});
