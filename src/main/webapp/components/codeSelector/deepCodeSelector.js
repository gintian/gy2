/**
 * 使用方法：
 * 引入ext依赖文件和此js文件
 * 在<img/>标签中加入 plugin="deepcodeselector" 和 codesetid、inputname（input框的名字）
 * 可选属性：
 * multiple（是否可以多选true/false）
 * afterfunc：选择代码成功后的回调函数,参数为1：value 2：text；如果multiple=true，参数为[{value:value,text:text}]
 * 例如：
 * <input type="hidden" name="test_value"/>(用来保存代码id)
 * <input type="text" name="test_view"/>（用来显示代码描述）
 * <img src="/xx/xx/xx" plugin="deepcodeselector" codesetid="" title="" inputname= "test_view"/>
 * 
 *  如果需要动态关联，则使用 setDeepEleConnect方法。参数为需要关联的id的集合：
 *  <img　src='xxx' codesetid='xx' inputname='xx' id='imgID' />
 *  js动态绑定:
 *    setDeepEleConnect(['imgID']);
 */
Ext.onReady(function(){
	Ext.Loader.setPath("EHR","/components");
	var imgEles = Ext.query('img[plugin=deepcodeselector]');
	if(imgEles.length<1){
		 return;
	 }
	window.deepCodeSelector = new DeepCodeSelector();
	
	 for(var i=0;i<imgEles.length;i++){
		 var ele       = imgEles[i],
		      param = {
		         inputName:ele.getAttribute("inputname"),
		         codesetid : ele.getAttribute("codesetid"),
			     multiple  : ele.getAttribute("multiple"),
			     afterfunc : ele.getAttribute("afterfunc"),
			     title:ele.getAttribute("title")
		      },
		     viewEles  = document.getElementsByName(param.inputName);
		     
		 if(viewEles.length<1)
			 continue;
		 ele.style.cursor='pointer';	 
		 //绑定鼠标点击事件
		 Ext.get(ele).on('click','showSelector',deepCodeSelector,param);
		 
		 //Ext.EventManager.addListener(ele,'click','showSelector',deepCodeSelector,[inputName,codesetid,multiple,afterfunc]);
	 }
	 //初始化事件
	 deepCodeSelector.initEvent(); 
});
/**
 *主动关联 代码选择器
 * @param idList 要绑定deepcodeselector的元素id
 */
function setDeepEleConnect(idList){
	if(!window.deepCodeSelector){
		window.deepCodeSelector = new DeepCodeSelector();
		//初始化事件
		window.deepCodeSelector.initEvent(); 
	}
	if(!Ext.isArray(idList))
	    idList = [idList];
	for(var index=0;index<idList.length;index++){
	 var ele    = document.getElementById(idList[index]);
	 if(!ele)continue;
	 var param = {
		         inputName:ele.getAttribute("inputname"),
		         codesetid : ele.getAttribute("codesetid"),
			     multiple  : ele.getAttribute("multiple"),
			     afterfunc : ele.getAttribute("afterfunc"),
			     title:ele.getAttribute("title")
		      },
	     viewEles  = document.getElementsByName(param.inputName);
	 if(viewEles.length<1)
		 continue;
	 ele.style.cursor='pointer';	 
	 //绑定鼠标点击事件
	 Ext.get(ele).on('click','showSelector',deepCodeSelector,param);
	}
	
}
function DeepCodeSelector(){
	this.codesetid = undefined;
	this.selector  = undefined;
	this.viewEle   = undefined;
	this.valueEle  = undefined;
	
	this.checkPanel= undefined;
    this.selecteds = undefined;
}
/**
 * 初始化事件
 */ 
DeepCodeSelector.prototype.initEvent = function(){
	var me = this;
	//页面单击事件
	Ext.getDoc().on("mousedown",function(e,t,o){
	      if(!me.selector)
	           return;
		 if(me.checkPanel && me.checkPanel.owns(t)){
			 return;
		 }
		 if(me.checkPanel && me.selector.owns(t)){
		     me.checkPanel.destroy();
		     delete me.checkPanel;
		     return;
		 }
		 
		 if(me.selector.owns(t) || t == me.viewEle){
		     return;
		 }
		 
		 me.finish_destory();
	 
	 });
	Ext.getDoc().on("mousewheel",function(a,b,c){
		if((c.selector && c.selector.owns(b)) || (c.checkPanel && c.checkPanel.owns(b))){
			return;
		}
		
		if(c.selector){
			c.selector.close();
			delete c.selector;
		}
		if(c.checkPanel){
			c.checkPanel.destroy();
			delete c.checkPanel;
		}
		
	},window,me);
};


DeepCodeSelector.prototype.showSelector = function(evt,evtEle,param){

   var me        = this,
        inputName = param.inputName,//关联的文本框 name
        codesetid = param.codesetid,//codesetid
        title = param.title?param.title:"",
        viewEles  = document.getElementsByName(inputName),
        valueName = inputName.substring(0,inputName.length-4)+"value",
        valueEles = document.getElementsByName(valueName);
	 me.afterfunc = param.afterfunc;
	if(viewEles.length<1 || valueEles.length<1)
		return;
	var viewEle  = viewEles[0],
        valueEle = valueEles[0],
        width    = viewEle.offsetWidth,//+evtEle.offsetWidth,//获取文本框+图片宽度
        height   = 300;
        
        var selector  = Ext.widget('panel',{
		   height:300,
		   width:600,
		   title:"请选择"+title,
		   draggable:true,
		   tools:[{type:'close',handler:me.finish_destory,scope:me}],
		   floating:true,
		   shadow:false,
		   style:'z-index:100000000',
		   layout:'fit',
		   autoScroll:true,
		   renderTo:document.body
	  });
	  var selectedsContainer = Ext.widget('container',{
	  		xtype:'container',
		     dock:'top',
		     layout:{
		        type:'table',columns:5,tdAttrs:{width:118}
		     },
		     style:'background-color:white;border:1px #c5c5c5 solid',
		     minHeight:40
	  });
	  if(param.multiple == 'true'){
	  	selector.addDocked([{
			 xtype:'toolbar',
			 dock:'bottom',
			 style:'background-color:white;padding:5 10 5 10px',
			 items:['->',
			        {xtype:'button',text:'确定',handler:me.finishSelect,scope:me},
			         {xtype:'button',text:'取消',handler:me.finish_destory,scope:me},
			 ]
		 },selectedsContainer]);
	  }
	  
	//定位  
	selector.alignTo(viewEle,'tl-bl?',undefined);

	var codestore = Ext.create('Ext.data.Store',{
    	fields: ['text','id','child'], 
        proxy:Ext.create("EHR.extWidget.proxy.TransactionProxy",{   
            extraParams:{
            	codesetid:codesetid,
            	level:'1'
            },
            functionId:'ZJ100000132',
            reader:{
            	type:'json',
            	root:'data'
            }
        })
    });
    
	codestore.on('load',me.createTable,me);
	codestore.load();
	
	me.viewEle = viewEle;
	me.valueEle = valueEle;
	me.multiple = param.multiple;
	me.selector = selector;
	me.selectedsContainer = selectedsContainer;
	me.codesetid = codesetid;
	me.selecteds = {};
}

DeepCodeSelector.prototype.createTable = function(store,records){
	var me = this,
	     isshowdock = false,
	     tableEle  = document.createElement("table");
	 tableEle.setAttribute("border", "0");
	 tableEle.setAttribute("cellspacing", "0");
	 tableEle.cellspacing="0";
	 tableEle.setAttribute("width", "100%");
	 for(var i =0;i<store.getCount();i++){
		 var item = store.getAt(i).data;
		 var tbodyEle = document.createElement("tbody");
		 var trEle = document.createElement("tr");
		 if((i+1)%2==1)
			 trEle.style.backgroundColor='rgb(241,241,241)';
		 //一级代码单元格
		 var td1stEle = me.createItemTdEle("25%");
	     var becontinue = false;
		 if(!item.child || item.child.length<1){
			 becontinue = true;
		 }
		 var label = me.createItemTextEle(item.id,item.text);
		 td1stEle.appendChild(label);
		 
		 //二级代码 单元格
		 var td2ndEle = me.createItemTdEle("75%");
		 //二级节点表格
		 if(!becontinue){
			 var table2ndEle  = document.createElement("table");
			 table2ndEle.setAttribute("width", "100%");
			 var childTbodyEle = document.createElement("tbody");
			 var childTrEle = document.createElement("tr");
			 var index;
			 //输出二级节点
			 for(index=0;index<item.child.length;index++){
				 var child = item.child[index];
				 var childTdEle = me.createItemTdEle("33%");
				 var label = me.createItemTextEle(child.id,child.text,child.childcount>0);
				 childTdEle.appendChild(label);
				 childTrEle.appendChild(childTdEle);
				 
				 //每行三个
				 if((index+1)%3==0 && index!=0 && index<item.child.length-1){
					 childTbodyEle.appendChild(childTrEle);
					 table2ndEle.appendChild(childTbodyEle);
					 childTbodyEle = document.createElement("tbody");
					 childTrEle = document.createElement("tr");
				 }
			 }
			 for(var b = 3-((index+1)%3);b>0&&b!=3;b--){
				 var td = document.createElement("td");
				 childTrEle.appendChild(td);
			 }
			 childTbodyEle.appendChild(childTrEle);
			 table2ndEle.appendChild(childTbodyEle);
			 td2ndEle.appendChild(table2ndEle);
		 }else{
			 td2ndEle.innerHTML="<div style='height:100%;padding:5px 10px 5px 5px'>&nbsp;</div>";
		 }
		 trEle.appendChild(td1stEle);
		 trEle.appendChild(td2ndEle);
		 tbodyEle.appendChild(trEle);
		 tableEle.appendChild(tbodyEle);
	 }
	 var dom = me.selector.getTargetEl();
	 dom.appendChild(tableEle);
	 //return tableEle;
}
DeepCodeSelector.prototype.createItemTdEle = function(width){
	var tdEle = document.createElement("td");
		 tdEle.setAttribute("width", width);
		 tdEle.setAttribute("align", "left");
		 tdEle.setAttribute("valign", "middle");
		 tdEle.style.color='#1B4A98';
		 tdEle.style.padding="2px 5px 2px 5px";
	 return tdEle;
}
DeepCodeSelector.prototype.createItemTextEle = function(codeid,text,expand){
     var me = this;
     var div = document.createElement("div");
     if(expand){
         var expander = document.createElement("img");
         expander.setAttribute('src','/images/jiahao.png');
         expander.setAttribute('codeid',codeid);
         expander.setAttribute('text',text);
         expander.style.marginRight='5px';
         expander.style.cursor='pointer';
         expander.onclick=me.showDeepCode.bind(me);
		div.appendChild(expander);
     }
     var a = document.createElement("a");
     a.setAttribute('codeid',codeid);
     a.title=text;
     a.innerHTML=text;
     a.style.fontSize='12px';
     a.style.cursor='pointer';
     a.onclick = me.setCheck.bind(me);
     div.appendChild(a);
     return div;
}
DeepCodeSelector.prototype.showDeepCode=function(e){
     var me       = this,
     codeid,text,position;
     if(e){
	     codeid   = e.target.getAttribute('codeid'),
         text = e.target.getAttribute('text'),
	     position = Ext.get(e.target).getXY();
	 }else{
	     codeid = event.srcElement.getAttribute('codeid');
	     text = event.srcElement.getAttribute('text'),
	     position = Ext.get(event.srcElement).getXY();
	 }
	     
	 var codestore = Ext.create('Ext.data.Store',{
	    	fields: ['text','id'], 
	        proxy: {   
	            type: 'transaction',  
	            extraParams:{
	            	codesetid:me.codesetid,
	            	codeid:codeid,
	            	level:'3'
	            },
	            functionId:'ZJ100000132',
	            reader:{
	            	type:'json',
	            	root:'data'
	            }
	        }  
	    });
		codestore.on('load',function(store){
			
			var items = [{xtype:'box',style:'font-size:12px;padding:3px;border-bottom:1px solid #c5c5c5;',html:text,colspan:2}];
			for(var i=0;i<store.getCount();i++){
				var record = store.getAt(i);
				var item = {
					xtype:'box',
					text:record.data.text,
					codeid:record.data.id,
					style:'color:#1B4A98;font-size:12px;padding:5px',
                    html:'<a  style="font-size:12px;cursor:pointer" title="'+record.data.text+'">'+record.data.text+'</a>',
                    listeners:{
                        render:function(){
                            var item = this;
                            item.getEl().on('click',function(){
                                  var param = {target:{codeid:item.codeid,innerHTML:item.text,getAttribute:function(key){return this[key]}}};
                                  me.setCheck(param);
                            });
                        }
                    }
				};
				items.push(item);
			}
			
			var checkPanel = Ext.widget('container',{
					 maxHeight:400,
					 minHeight:100,
					 width:260,
					 floating:true,
					 shadow:false,
					 style:'background-color:white;border:1px rgb(190,214,229) solid;z-index:100000001;',
					 autoScroll:true,
					 layout:{
						type:'table',
						tableAttrs:{width:'100%'},
						columns:2,
						tdAttrs:{width:"130",align:'left'}
					 },
					 items:items,
					 renderTo:document.body
			});
			checkPanel.setPagePosition(position[0]-6,position[1]-6);
			me.checkPanel = checkPanel;
		});
		codestore.load();
}
DeepCodeSelector.prototype.setCheck = function(e){
    var me = this;
    var codeid,text;
    if(e){
    	codeid = e.target.getAttribute('codeid');
    	text = e.target.innerHTML;
    }else{
    	codeid = event.srcElement.getAttribute('codeid');
    	text = event.srcElement.innerHTML;
    }
    
	me.selecteds[codeid] = text;
   
     if(me.multiple!='true'){
     	 me.finishSelect();
     	 return;
     }
     
     
     me.selectedsContainer.add({
     	  xtype:'container',
     	  style:{border:"solid #c5c5c5 1px",backgroundColor:'#f8f8f8'},
          margin:1,
          width:116,
          height:19,
          layout:'hbox',
          items:[{xtype:'component',flex:10,
          	autoEl: {
		        tag: 'div',
		        style:'white-space:nowrap; text-overflow:ellipsis;overflow: hidden;padding-left:2px',
		        html:text,
		        title:text
		    }
          },{
             xtype:'component',width:19,height:17,padding:'1 0 0 6',
             html:'X',
             codeid:codeid,
             style:'color:#e4393c;cursor:pointer',
             listeners:{
          		render:function(){
          		    var delButton = this;
          			this.getEl().on('click',function(){
          				delete me.selecteds[delButton.codeid];
          			   delButton.ownerCt.destroy();
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
}

DeepCodeSelector.prototype.finishSelect = function(){
      var values='',texts='',obj = [];
      for(var key in this.selecteds){
            texts+='，'+this.selecteds[key];
            values+=','+key;
            obj.push({value:key,text:this.selecteds[key]});
      }
      values = values.substring(1);
      texts = texts.substring(1);
      this.viewEle.value = texts;
      this.valueEle.value = values;
      if(!this.multiple){
          obj = [values,texts];
      }
      this.finish_destory();
      if(this.afterfunc)
				Ext.callback(eval(this.afterfunc),null,[obj]);
}

DeepCodeSelector.prototype.finish_destory = function(){
	this.selector.close();
	if(this.checkPanel)
	  this.checkPanel.destroy();
	delete this.selector;
	delete this.checkPanel;
	this.codesetid = undefined;
	this.selector  = undefined;
	this.viewEle   = undefined;
	this.valueEle  = undefined;
	
	this.checkPanel= undefined;
	this.value     = undefined;
	this.text      = undefined;
};
