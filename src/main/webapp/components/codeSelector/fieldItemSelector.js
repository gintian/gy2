/***
 * 	<textarea rows="10" cols="100" name="bb_text"></textarea><br/>
	<input type="hidden" name="bb_value"/><br/>
	<input type="hidden" name="bb_num" value="1"/>
	<input type="text" name="bb_view" plugin="fieldItemselector" fieldItemId="A`B`K`Y:Z01" formula="true" inputname="bb_view"  entityFn="recruit"/>
 *
 *根据按钮生成
 *	<button onclick = "" buttonWin="true" plugin="fieldItemselector" fieldItemId="A`B`K`Y:Z01" formula="true" inputname="dd_view" entityFn="recruit">弹出指标选择面板</button>
	<input type="hidden" name=""/>
	
 *A:人员信息集
 *B:部门信息集
 *K：职位信息集
 *Y:(表名):业务字典
 *
 *entityFn:自定义模板   recruit：招聘
 *plugin：标签标识   fieldItemselector：标签类型
 *formula: true 公式型
 */
Ext.onReady(function(){
	var buttonEles = Ext.query('button[plugin=fieldItemselector]');
	if(buttonEles.length<1)
	{return;}
	 var imgEles = Ext.query('input[plugin=fieldItemselector]');
	 if(imgEles.length<1){
		 return;
	 }
	 var fieldItemSelector = new FieldItemSelector();
		 
	 for(var i=0;i<imgEles.length;i++){
		 var ele       = imgEles[i],
		     inputName = ele.getAttribute("inputname"),
		     fieldItemId = ele.getAttribute("fieldItemId"),
		     afterfunc=ele.getAttribute("afterfunc"),
		     entityFn = ele.getAttribute("entityFn"); 
		     formula = ele.getAttribute("formula");
		     viewEles  = document.getElementsByName(inputName);
		     
		     //alert(inputName+"\n"+fieldItemId+"\n"+fieldItemsource+"\n"+nmodule+"\n"+ctrltype+"\n"+ele.getAttribute("plugin"));
		 if(viewEles.length<1)
			 continue;
		 ele.style.cursor='pointer';	
		 //绑定鼠标点击事件
		 Ext.EventManager.addListener(ele,'click','showSelector',fieldItemSelector,[inputName,fieldItemId,afterfunc,'',entityFn,formula]);
		 Ext.EventManager.addListener(viewEles[0],'keyup','searchCode',fieldItemSelector,[inputName,fieldItemId,afterfunc,entityFn,formula]);
		 Ext.EventManager.addListener(viewEles[0],'focus','saveValue',fieldItemSelector);
	 }
	 /**从按钮生成指标选择菜单**/
	 for(var i=0;i<buttonEles.length;i++)
	 {
	 	var ele       = buttonEles[i],
		     fieldItemId = ele.getAttribute("fieldItemId"),
		     entityFn = ele.getAttribute("entityFn"),
		     inputName = ele.getAttribute("inputname");
	    	 //alert(inputName+"\n"+fieldItemId+"\n"+fieldItemsource+"\n"+nmodule+"\n"+ctrltype+"\n"+ele.getAttribute("plugin"));
	    	 //alert(buttonWin);
	    	 Ext.EventManager.addListener(ele,'click','showPanelSelector',fieldItemSelector,[fieldItemId,entityFn,inputName]);
	 }
	 //初始化事件
	 fieldItemSelector.initEvent(); 
});

/**
 *主动关联 代码选择器
 * @param idList 要绑定FieldItemSelector的元素id
 */
function setFiledEleConnect(idList){
	var fieldItemSelector = new FieldItemSelector();
	for(var index=0;index<idList.length;index++){
	 var ele    = document.getElementById(idList[index]);
	 if(!ele)continue;
	 var inputName = ele.getAttribute("inputname"),
	     fieldItemId = ele.getAttribute("fieldItemId"),
	     afterfunc = ele.getAttribute("afterfunc"),
	     entityFn = ele.getAttribute("entityFn"); 
	     formula = ele.getAttribute("formula");
	     viewEles  = document.getElementsByName(inputName);
	 if(viewEles.length<1)
		 continue;
	 ele.style.cursor='pointer';
		 //绑定鼠标点击事件
		 Ext.EventManager.addListener(ele,'click','showSelector',fieldItemSelector,[inputName,fieldItemId,afterfunc,'',entityFn,formula]);
		 Ext.EventManager.addListener(viewEles[0],'keyup','searchCode',fieldItemSelector,[inputName,fieldItemId,afterfunc,entityFn,formula]);
		 Ext.EventManager.addListener(viewEles[0],'focus','saveValue',fieldItemSelector);
	}
	//初始化事件
	 fieldItemSelector.initEvent(); 
}
/***
 * 从方法生成指标选择器
 * @param {} fieldItemId  A`B`K`Y:tablename&tablename...
 * @param {} entityFn  recruit招聘模块
 * @param {} method  回调函数
 * @param {} param  传入值
 */
function setPanelFiledEleConnect(fieldItemId,entityFn,method,param){
	var me        = this,
	    fieldItemId = fieldItemId,
	    entityFn = entityFn
	//文本框内的值发生变化时是否执行查询事件
	var ChangeEffective = true;
	var codestore = Ext.create('Ext.data.TreeStore',{
    	fields: ['text','id','fieldItemId','fieldItemType','fieldSetId'], 
        proxy: {   
            type: 'ajax',  
            extraParams:{
            	fieldItemId  : fieldItemId,
            	entityFn   : entityFn
            },
            url: '/servlet/gridtable/GetFieldItemServlet'
        }  
    });
    codestore.load();
    var treePanel = Ext.widget("window",{
        	   modal:true,
        	   title:'请选择指标',
               layout:'fit',
               height:480,
               width:350,
               border:0,
               items:[{
            	   padding: 5,
            	   border:0,
	               items: [{
	               	   id:'fieldtext',
	                   xtype: 'textfield',
	                   value:'',
	                   width:330,
	                   listeners : {       
		                    change : function(obj,event,opt){   
	            	   		if(!ChangeEffective)
	            	   			return;
	            	   		
		                   	codestore.proxy.extraParams.searchtext=encodeURI(obj.value);
							codestore.load();
	                   	}}
	               },{
	               		id:'fieldItemId',
	               		xtype:'hidden',
	               		value:''
	               }, {
	                   xtype: 'treepanel',
	                   width: 330,
	           	       height: 370,
	                   store: codestore,
	                   rootVisible: false,
				       toFrontOnShow:true,
					   listeners:{
							//选中树节点事件
							itemclick:function(a,record){
								if(record.data.leaf==false){
									return;
								}
								//给text框赋值
								ChangeEffective = false;
								Ext.getCmp('fieldtext').setValue(record.data.text);
								Ext.getCmp('fieldItemId').setValue(record.data.fieldSetId+":"+record.data.id);
								ChangeEffective = true;
							}
						}
	               }]
               }],
               resizable:false,
               buttonAlign: 'center',
               buttons:[
                         {text:"确定",
                         id:"",
                         handler:function(){
                         	if(Ext.getCmp('fieldItemId').value!=""&&Ext.getCmp('fieldtext').value!="")
                         	{
	                         	method(Ext.getCmp('fieldItemId').value+"`"+Ext.getCmp('fieldtext').value,param);
                         	}else{
                         		method("",param);
                         	}
                         	treePanel.close();
                         }},
                         {text:"关闭",handler:function(){treePanel.close();}}
                       ]
        }).show();
}

//根据button生成指标选择器
FieldItemSelector.prototype.showPanelSelector = function(evt,evtEle,opt){
	
	var me        = this,
	    fieldItemId = opt[0],//fieldItemId
	    entityFn = opt[1],
	    inputName = opt[2]
	var codestore = Ext.create('Ext.data.TreeStore',{
    	fields: ['text','id','fieldItemId','fieldItemType','fieldSetId'], 
        proxy: {   
            type: 'ajax',  
            extraParams:{
            	fieldItemId  : fieldItemId,
            	entityFn   : entityFn
            },
            url: '/servlet/gridtable/GetFieldItemServlet'
        }  
    });
    codestore.load();
    var treePanel = Ext.widget("window",{
        	   modal:true,
        	   title:'请选择指标',
               layout:'fit',
               value:'',
               height:480,
               width:350,
               border:0,
               items:[{
            	   padding: 5,
            	   border:0,
	               items: [{
	               	   id:'fieldtext',
	                   xtype: 'textfield',
	                   value:'',
	                   width:330,
	                   enableKeyEvents:true,
	                   listeners : {       
		                   keyup : function(obj,event,opt){   
		                   	codestore.proxy.extraParams.searchtext=encodeURI(obj.value);
							codestore.load();
	                   	}}
	               },{
	               		id:'fieldItemId',
	               		xtype:'hidden',
	               		value:''
	               }, {
	                   xtype: 'treepanel',
	                   width: 330,
	           	       height: 370,
	                   store: codestore,
	                   rootVisible: false,
				       toFrontOnShow:true,
					   listeners:{
							//选中树节点事件
							itemclick:function(a,record){//alert(record.data.fieldItemId+"--"+record.data.id+"---"+record.data.text);
								if(record.data.leaf==false){
									return;
								}
								//给text框赋值
								Ext.getCmp('fieldtext').setValue(record.data.text);
								Ext.getCmp('fieldItemId').setValue(record.data.fieldSetId+":"+record.data.id);
							}
						}
	               }]
               }],
               resizable:false,
               buttonAlign: 'center',
               buttons:[
                         {text:"确定",
                         id:"",
                         handler:function(){
                         	if(Ext.getCmp('fieldItemId').value!=""&&Ext.getCmp('fieldtext').value!="")
                         	{
	                         	document.getElementsByName(inputName)[0].value=Ext.getCmp('fieldItemId').value+"`"+Ext.getCmp('fieldtext').value;
                         	}else{
                         	    document.getElementsByName(inputName)[0].value="";
                         	}
                         	treePanel.close();
                         }},
                         {text:"关闭",handler:function(){treePanel.close();}}
                       ]
        }).show();
}
FieldItemSelector.prototype.searchPanelCode = function(evt,evtEle,opt){
	var me = this;
	if(!me.selector){
		opt.push(evtEle.value);
		me.showSelector(evt, evtEle, opt)
	}else{
		me.selector.store.proxy.extraParams.searchtext=encodeURI(evtEle.value);
		me.selector.store.load();
	}
	
};

function  FieldItemSelector(){
    this.selector = undefined;
    this.viewEle = undefined;
    this.valueEle = undefined;
    this.fieldItemId= undefined;
    this.defaulttext = undefined;
    this.entityFn = undefined;
    this.buttonWin = undefined;
    this.formula = undefined;
};

/**
 * 初始化事件
 */
FieldItemSelector.prototype.initEvent = function(){
	var me = this;
	//页面单击事件
	Ext.getDoc().on("mousedown",function(e,t,o){
		 //如果selector存在 && 触发单击事件元素不是文本框输入框  && 触发单击事件元素 不是 selector和selector的子元素   则销毁selector
		 if(me.selector && t!=me.viewEle && !me.selector.owns(t)){
			 me.finish_destory(false);
		  }
	 
	 });
	Ext.getDoc().on("mousewheel",function(a,b,c){
		if(c.selector && !c.selector.owns(b))
			c.selector.hide();
	},window,me);
};

FieldItemSelector.prototype.showSelector = function(evt,evtEle,opt){
	if(this.selector){
		this.selector.close();
		this.selector==undefined;
	}
	if(this.selector!=null)
	{
		this.selector.close();
		this.selector==undefined;
	}
	var me        = this,
	    inputName = opt[0],//关联的文本框 name
	    fieldItemId = opt[1],//fieldItemId
	    codeCallBack = opt[2],
	    searchtext= opt[3],
	    entityFn = opt[4],
	    formula = opt[5],
	    viewEles  = document.getElementsByName(inputName),
	    valueName = inputName.substring(0,inputName.length-4)+"value",
	    valueEles = document.getElementsByName(valueName);
	    textName = inputName.substring(0,inputName.length-4)+"text",
	    textEles = document.getElementsByName(textName);
	    numName = inputName.substring(0,inputName.length-4)+"num",
	    munEles = document.getElementsByName(numName);
	if(viewEles.length<1 || valueEles.length<1)
		return;
	var viewEle  = viewEles[0],
	    valueEle = valueEles[0],
	    textEle = textEles[0],
	    munEle = munEles[0],
	    position = Ext.get(viewEle).getXY();//获取 文本框 坐标
	    width    = viewEle.offsetWidth,
	    height   = 300,
	    this.defaulttext = viewEle.value;
	var codestore = Ext.create('Ext.data.TreeStore',{
    	fields: ['text','id','fieldItemId','fieldItemType','fieldSetId'], 
        proxy: {   
            type: 'ajax',  
            extraParams:{
            	fieldItemId  : fieldItemId,
            	entityFn   : entityFn,
            	searchtext : encodeURI(searchtext)
            },
            url: '/servlet/gridtable/GetFieldItemServlet'
        }  
    });
	
    var selector = Ext.widget('treepanel',{
    	floating:true,
    	width:width,
    	height:height,
    	store:codestore,
    	rootVisible: false,
    	toFrontOnShow:true,
		listeners:{
			//选中树节点事件
			itemclick:function(a,record){//alert(record.data.fieldItemId+"--"+record.data.id+"---"+record.data.text);
				//alert(record.data.leaf);
				if(record.data.leaf==false){
					return;
				}
				//给text框赋值
				viewEle.value  = record.data.text;
				if(record.data.fieldItemId=="sys")//系统自定义指标
				{
					if(formula=="true")
					{
						if(codeCallBack)
							Ext.callback(eval(codeCallBack),null,[record.data.id,record.data.text]);
						else
						    textEle.value =record.data.text;
					}else{
						textEle.value += "$"+munEle.value+":sys."+record.data.text+"$";
					}
				}else{		
					if(formula=="true")
					{
						valueEle.value = record.data.fieldSetId+":"+record.data.id;
						if(codeCallBack){
							Ext.callback(eval(codeCallBack),null,[valueEle.value,record.data.text]);
						}else{
							textEle.value += record.data.text;
						}
					}else{
						valueEle.value += record.data.fieldSetId+":"+record.data.id+"`";
						textEle.value += "$"+munEle.value+":"+record.data.text+"$"
						munEle.value ++;
					}
				}
				//alert(record.data.id+"---"+record.data.fieldItemId);
				me.finish_destory(true);
			}
		}
    });
    //查询代码类名称（codesetdesc）
    Ext.Ajax.request({
        url: '/servlet/gridtable/GetFieldItemServlet?fieldItemId='+fieldItemId,
        params: {
        	fieldItemId:me.fieldItemId,
            istitle:'true'
        },
        success: function(response){
            var text = response.responseText;
            selector.setTitle(text);
        }
    });
    //渲染selector
    selector.render(document.body);
    selector.toFront(true);
    //设置selector坐标
    if(document.body.clientHeight<position[1]+selector.getHeight()){
    	position[1] = position[1]-selector.getHeight()-viewEle.offsetHeight;
    }//alert(position[0]+"\n"+position[1]);
	selector.setPagePosition(position[0],position[1]+viewEle.offsetHeight);
	me.viewEle = viewEle;
	me.valueEle = valueEle;
	me.selector = selector;
	me.fieldItemId = fieldItemId;
	
};
FieldItemSelector.prototype.saveValue = function(evt,evtEle,opt){
	this.defaulttext = evtEle.value;
};
FieldItemSelector.prototype.searchCode = function(evt,evtEle,opt){
	var me = this;
	if(!me.selector){
		opt.push(evtEle.value);
		me.showSelector(evt, evtEle, opt)
	}else{
		me.selector.store.proxy.extraParams.searchtext=encodeURI(evtEle.value);
		me.selector.store.load();
	}
	
};
FieldItemSelector.prototype.finish_destory = function(beselect){
	var me = this;
	if(!beselect){
		if(me.viewEle.value.length==0)
			me.valueEle.value="";
		else
			me.viewEle.value = me.defaulttext;
	}
	this.selector.close();
	this.selector = undefined;
    this.viewEle = undefined;
    this.valueEle = undefined;
    this.fieldItemId= undefined;
    this.defaulttext = undefined;
    this.entityFn = undefined;
    this.buttonWin = undefined;
    this.formula = undefined;
};


