/**
 * 查询备选指标fieldsArray 格式：
 * [{
 * 		type:'A',       字段类型
		itemid:'a0101', 字段id
		itemdesc:'姓名', 字段描述
		operationData:[{dataName:'',dataValue:''}], 如果有此数据，此字段为下拉字段，其他参数都不起作用。此数据为备选数据
		//日期型指标参数
		format:'Y-m-d', 日期类型
		//代码型指标参数
		codesetid:'UN', 代码类
		codesource:'',  代码数据来源
		ctrltype:'3',   控制类型
		nmodule:'4',    业务模块号
		parentid:'01'   父节点id
		codesetValid:true 是否根据codesetid控制选择。比如代码类为UM，如果为false，可以选UN，true则只能选UM。默认true
		
 * }]
 */
Ext.define('EHR.querybox.QueryBox',{
   extend:'Ext.container.Container',
   requires:['EHR.querybox.QueryField'],
   xtype:'querybox',
   padding:'0 0 0 16',
   style:'border:1px solid #c5c5c5;background:url(/images/hcm/themes/gray/search_fdj2.png) no-repeat center left;',
   height:22,
   layout:'hbox',
   spacer:undefined,
   queryKeyPanel:undefined,
   
   //唯一id，用于保存方案
   subModuleId:undefined,
   //方案查询字段
   fieldsArray:[],
   //自定义参数
   customParams:undefined,
   // 隐藏查询方案
   hideQueryScheme:false,
   //查询callback函数
   success:Ext.emptyFn,
   //callback 的 scope
   callBackScope:undefined,
   //交易类号
   funcId:undefined,
   //提示文字
   emptyText : "请输入查询关键字",
   //宽度设置（输入框的宽度，不是控件的宽度）
   queryBoxWidth:310,
    //查询条件标签宽度
    queryItemWidth:80,
    //动态调整输入框宽度数组
    changeWidthList:undefined,
   
   initComponent:function(){
      this.callParent(arguments);
      this.doStart();
   },
   doStart:function(){
      var me = this;
      me.queryKeyPanel = Ext.widget('container',{
          shadow:false,
          layout:'hbox',
          listeners:{
             add:me.doQuickSearch,
             remove:me.doQuickSearch,
             scope:me
          }
      });
      me.add(me.queryKeyPanel);
      if(!Ext.util.CSS.getRule('.myPickTrigger'))
    	  	Ext.util.CSS.createStyleSheet('.myPickTrigger{background:url(/components/querybox/images/trigger.png) !important;border:none !important;}'+
		                              ' .myPickInputWrap{border:none !important;background:none !important;position:relative;top:1px;}');
      me.queryField = Ext.widget('queryfield',{
      	  funcId:me.funcId,
	      subModuleId:me.subModuleId,
	      fieldsArray:me.fieldsArray,
	      customParams:me.customParams,
	      success:me.success,
	      callBackScope:me.callBackScope,
	      hideTrigger:me.hideQueryScheme,
	      triggerCls:'myPickTrigger',
	      width:me.queryBoxWidth,
	      margin:'0 -1 0 1',
	      inputWrapCls:Ext.baseCSSPrefix + 'form-text-wrap myPickInputWrap',
	      emptyText:me.emptyText,
	      listeners: {
             specialkey:{
                scope:me,
                fn:me.addQueryKey
             },
             render:function(){
            	 if(this.emptyText.length>23)
            		 this.bodyEl.dom.title=this.emptyText;
             }
          }
      });
      me.add(me.queryField);

      me.changeWidthList=new Array();
      var tempWidth=me.queryBoxWidth;
      for( var i=0;i<4;i++){
          if(tempWidth-me.queryItemWidth>120){
              me.changeWidthList[i]=me.queryItemWidth;
              tempWidth-=me.queryItemWidth;
          }else{
              me.changeWidthList[i]=0;
          }
      }

      
   },
   addQueryKey:function(field, e){
      if((e.getKey() != e.ENTER && e.getKey() != e.TAB) || field.getValue().length<1)
         return;
      var keyLength = this.queryKeyPanel.items.getCount();
	  if(keyLength==4){
		  Ext.Msg.alert('提示信息','检索条件个数最多4个！');
		  return;
	  }
      this.queryField.collapse();   
      this.queryKeyPanel.add(this.createKeyItem(field.getValue()));
      field.setValue('');
      if(this.changeWidthList[keyLength]>0){
          field.setWidth(field.width-this.changeWidthList[keyLength]);
      }

      e.stopEvent( );
      if(this.ownerCt.xtype=='toolbar' && this.ownerCt.getScrollable()){
          this.ownerCt.getScrollable().scrollTo(0,0);
      }
      field.focus();
   },
   removeQueryKey:function(keyItem,me){
       var conut=me.queryKeyPanel.items.getCount()-1;
       me.queryField.setWidth(me.queryField.width+me.changeWidthList[conut]);
   	  keyItem.destroy();
   },
   createKeyItem:function(value){
       var me = this;
       var con = Ext.widget('container',{
          style:{border:"solid #c5c5c5 1px",backgroundColor:'#f8f8f8'},
          margin:1,
          width:80,
          height:19,
          value:value,
          layout:'hbox',
          items:[{xtype:'component',flex:10,
          	autoEl: {
		        tag: 'div',
		        style:'white-space:nowrap; text-overflow:ellipsis;overflow: hidden;padding-left:2px',
		        html:value,
		        title:value
		    }
          },{
             xtype:'component',width:19,height:17,padding:'1 0 0 6',
             html:'X',
             style:'color:#e4393c;cursor:pointer;',
             listeners:{
          		render:function(){
          		    var delButton = this;
          			this.getEl().on('click',function(){
          			   me.removeQueryKey(delButton.ownerCt,me);
          			},this);
          			this.getEl().on('mouseover',function(){
          			   delButton.ownerCt.hadFocus();
          			   this.dom.style.backgroundColor='#e4393c';
          			   this.dom.style.color='white';
          			});
          			this.getEl().on('mouseout',function(){
          				delButton.ownerCt.lostFocus();
          			   this.dom.style.backgroundColor='#f8f8f8';
          			   this.dom.style.color='#e4393c';
          			});
          		}
          	}
          	
          }],
          hadFocus:function(){
		     this.setStyle({
		     	border:"solid #e4393c 1px"
		     });
          },
          lostFocus:function(){
          	this.setStyle({
		     	border:"solid #c5c5c5 1px"
		     });
          }
          
       });
       return con;
   },
   doQuickSearch:function(){
       var values = []; 
       this.queryKeyPanel.items.each(function(c){
            values.push(c.value);
       });
       if(!this.funcId || this.funcId.length<1){
          Ext.callback(this.success,this.callBackScope,[{type:1,inputValues:values}]);
          return;
       }
       
       var map = new HashMap();
	   map.put("customParams", this.customParams);
	   map.put('subModuleId',this.subModuleId);
	   map.put("type","1");//1为输入查询，2为方案查询
	   map.put("inputValues",values);
	   Rpc({functionId:this.funcId,success:this.searchResult,scope:this},map);
   },
   searchResult:function(responseValue){
   	    var me = this;
		var value=responseValue.responseText;
		var map=Ext.decode(value);
		if (me.success)
				Ext.callback(me.success, me.callBackScope,[map]);
   },
   setCustomParams:function(obj){
		var me = this;
		Ext.apply(me.customParams,obj);
		if(me.queryField)
			Ext.apply(me.queryField.customParams,obj);
   },
   setQueryBoxWidth:function(width){
       this.queryField.setWidth(width);
   },
   // 清空所有key
	removeAllKeys:function(silence){
	
		if(silence)
			this.queryKeyPanel.suspendEvent("remove");	
		this.queryKeyPanel.removeAll(true);
		if(silence)
			this.queryKeyPanel.resumeEvent("remove");	

	},
	//用于回显查询项
    showQueryKey:function(values){
    	if(values.length<1){
    		return;
    	}
	    this.queryField.collapse(); 
	    //得到当前查询框中现有的查询项
	    var currentvalues = ""; 
        this.queryKeyPanel.items.each(function(c){
        	currentvalues+="`"+c.value;
        });
        if(currentvalues!=''){
        	currentvalues+="`";
        }
	    var valuearr = values.split("`");
	    for(var i=0;i<valuearr.length;i++){
	    	var value = valuearr[i];
	    	if(value.length>0){
	    		if(currentvalues.length>0&&currentvalues.indexOf("`"+value+"`")>-1){
	    		}else{
	    			this.queryKeyPanel.add(this.createKeyItem(value));
	    		}
	    	}
	    	this.queryField.setValue('');
	    }
	    if(this.ownerCt.xtype=='toolbar' && this.ownerCt.getScrollable()){
	        this.ownerCt.getScrollable().scrollTo(0,0);
	    }
	    this.queryField.setWidth(this.queryBoxWidth-this.queryItemWidth);
	    this.changeWidthList=new Array();
	    var tempWidth=this.queryBoxWidth;
	    for( var i=0;i<valuearr.length;i++){
	        if(tempWidth-this.queryItemWidth>120){
	            this.changeWidthList[i]=this.queryItemWidth;
	            tempWidth-=this.queryItemWidth;
	        }else{
	            this.changeWidthList[i]=0;
	        }
	    }
	    this.queryField.focus();
    }

});