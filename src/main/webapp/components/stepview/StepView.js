/**
参数说明：

stepData:步骤数据，格式：[{name:'步骤名称',desc:'描述'},...]。注意：步骤对象不仅限于name、desc属性，可根据使用情况添加自定义属性，例如id等
currentIndex:当前激活步骤下标，默认为0
freeModel:是否自由模式。自由模式可点击切换步骤，非自由模式 只能通过nextStep、previousStep方法切换步骤
 largeIcon: 设置为true 则使用大图标，大字体。

方法说明：

nextStep()    :下一步
previousStep():上一步

事件说明：
stepchange: 切换步骤后触发，参数：1、stepview对象  2、当前激活步骤数据对象 3、当前激活步骤下标


**/
Ext.define("EHR.stepview.StepView",{
	extend:'Ext.Component',
	xtype:'stepview',
	config:{
		stepData:undefined,
		currentIndex:0,
		freeModel:false,
		largeIcon:false
	},
	iconPath:rootPath+"/components/stepview/image/",
	renderTpl:[
				'<table style="width:100%"><tr>',
						'{% values.$comp.renderStep(values,out);%}',
				'</tr></table>'],
	stepTpl:[
				'<td width="{widthPercent}%" valign="top" >',
					'<table border=0 width=100% style="color:{textColor}">',
						'<tr>',
							'<td width=24>',
								'<tpl switch="state">',
						            '<tpl case="finish">',
						                '<div flag="index" role="step" stepIndex="{stepIndex}" style="cursor:{cursor};width:{iconSize}px;height:{iconSize}px;float:left;background:url({iconPath}finish.png) no-repeat;background-size:100% 100%;text-align:center;line-height:{iconSize}px;color:white;"><span style="display:none;font-size:{iconSize/2}px">{stepIndex+1}</span></div>',
						            '<tpl case="current">',
						                '<div flag="index" role="step" stepIndex="{stepIndex}" style="cursor:{cursor};width:{iconSize}px;height:{iconSize}px;float:left;background:url({iconPath}current.png) no-repeat;background-size:100% 100%;text-align:center;line-height:{iconSize}px;color:white;"><span style="font-size:{iconSize/2}px">{stepIndex+1}</span></div>',
						            '<tpl default>',
						                '<div flag="index" role="step" stepIndex="{stepIndex}" style="cursor:{cursor};width:{iconSize}px;height:{iconSize}px;float:left;background:url({iconPath}wait.png) no-repeat;background-size:100% 100%;text-align:center;line-height:{iconSize}px;color:gray;"><span style="font-size:{iconSize/2}px">{stepIndex+1}</span></div>',
						        '</tpl>',
							'</td>',
							'<tpl if= "stepIndex===0"><td nowrap="nowrap" style="cursor:{cursor};background-color:white;line-height:{iconSize}px;font-size:{fontSize}px;padding:0 10px 0 10px; font-weight: 600"></tpl>',
							'<tpl if= "stepIndex!==0"><td nowrap="nowrap" style="cursor:{cursor};background-color:white;line-height:{iconSize}px;font-size:{fontSize}px;padding:0 10px 0 10px; color:#666"></tpl>',
							'<div role="step" stepIndex="{stepIndex}">{step.name}</div> ',
							'</td>',
							'<td width=100%>',
								'<tpl if="!isEnd">',
									'<div style="background-color:{lineColor};width:100%;height:1px;"></div>',
								'</tpl>',
							'</td>',
						'</tr>',
						
						'</tr>',
							'<td></td>',
							'<td colspan=2 style="padding:4px 0 0 4px">{step.desc}</td>',
						'</tr>',
					'</table>',
					
					//'<tpl if="!isEnd">',
					//	'<div style="background-color:{lineColor};width:100%;height:1px;position:relative;top:13px;z-index:-1;"></div>',
					//'</tpl>',
					/*
					'<tpl switch="state">',
			            '<tpl case="finish">',
			                '<div role="step" stepIndex="{stepIndex}" style="cursor:{cursor};width:24px;height:24px;float:left;background:url({iconPath}/finish.png);text-align:center;line-height:22px;color:white;"><span style="display:none;">{stepIndex+1}</span></div>',
			            '<tpl case="current">',
			                '<div role="step" stepIndex="{stepIndex}" style="cursor:{cursor};width:24px;height:24px;float:left;background:url({iconPath}/current.png);text-align:center;line-height:22px;color:white;"><span>{stepIndex+1}</span></div>',
			            '<tpl default>',
			                '<div role="step" stepIndex="{stepIndex}" style="cursor:{cursor};width:24px;height:24px;float:left;background:url({iconPath}/wait.png);text-align:center;line-height:22px;color:gray;"><span>{stepIndex+1}</span></div>',
			        '</tpl>',
			        '<table style="display:inline;color:{textColor};width:100%;" border=1>',
			        '<tr>',
			        	'<td>{step.name}',
			        	'</td>',
			        	'<td>',
			        		'<div style="background-color:{lineColor};width:100%;height:1px;"></div>',
			        	'</td>',
			        '</tr>',
			        '<tr>',
			        	'<td colspan=2>{step.desc}</td>',
			        '</tr>',
			        '</table>',*/
			        /*
					'<div style="display:inline;color:{textColor}">',
						'<span role="step" stepIndex="{stepIndex}" style="cursor:{cursor};background-color:white;font-weight:bold;line-height:24px;font-size:14px;padding:0 10px 0 4px;">{step.name}</span>',
						'<div style="background-color:{lineColor};width:100%;height:1px;position:relative;top:-25px;"></div>',
						//'<br>',
						'<div style="margin-left:28px;">{step.desc}</div>',
					'</div>',
					*/
				'</td>'
			],
	renderStep:function(values,out){
		var stepTpl = Ext.XTemplate.getTpl(this, 'stepTpl');
		var stepValues = {};
		var iconSize=this.largeIcon?40:24;
		if(this.largeIcon){
			this.iconPath+="large_";
		}
		for(var i=0;i<this.stepData.length;i++){
			var state;
			if(i<this.currentIndex && !this.freeModel)
				state = "finish";
			else if(this.currentIndex==i)
				state = "current";
			else
				state = "wait";
			var stepValues={
				step:this.stepData[i],
				stepIndex:i,
				widthPercent:(100/this.stepData.length),
				isEnd:i==this.stepData.length-1?true:false,
				cursor:this.freeModel?'pointer':'',
				textColor:(i==this.currentIndex)?'#000000':'#999',
				lineColor:(i<this.currentIndex || this.freeModel)?'#2d8cf0':'#999',
				state:state,
				iconPath:this.iconPath,
				iconSize:iconSize,
				fontSize:iconSize===40?16:14
			};
			stepTpl.applyOut(stepValues,out,values);
		}
	},
	afterRender: function() {
        var me = this;
        me.callParent();
        /*添加点击事件*/
        if(me.freeModel){
	        me.mon(me.el, {
	            scope: me,
	            click: me.handleEvent
	        });
        }
    },
    handleEvent:function(et,ele){
    		if(ele.getAttribute("role")!='step' && ele.parentNode.getAttribute("role")!='step')
    			return;
    		var stepIndex = ele.getAttribute("stepIndex")||ele.parentNode.getAttribute("stepIndex");
    		this.currentIndex = stepIndex;
    		//切换步骤
    		this.changeStep(this.currentIndex);
    },
    nextStep:function(){
    		if(this.currentIndex==this.stepData.length-1)
    			return;
    		this.changeStep(++this.currentIndex);
    },
    previousStep:function(){
    		if(this.currentIndex==0)
    			return;
    		this.changeStep(--this.currentIndex);
    },
	changeStep:function(index){
		var stepList = this.el.query("div[flag=index]");
		for(var i=0;i<stepList.length;i++){
			//步骤小圆圈 div 元素
			var stepEle = stepList[i];
			//步骤文字div 元素
			var textEle = stepEle.parentNode.nextSibling.childNodes[0];
			//线条 div元素
			var lineArray = stepEle.parentNode.nextSibling.nextSibling.childNodes;
			var lineEle = lineArray.length>0?lineArray[0]:undefined;
			//var textEle = stepEle.nextSibling;
			if(i<index && !this.freeModel){//非自由模式 已完成步骤样式
				stepEle.style.background='url('+this.iconPath+'finish.png) no-repeat';
				stepEle.childNodes[0].style.display="none";
				lineEle.style.backgroundColor='#2d8cf0';
				textEle.style.color='#666';
				textEle.style.fontWeight='normal';
			}else if(i==index){//当前步骤样式
    				stepEle.style.background='url('+this.iconPath+'current.png) no-repeat';
    				stepEle.style.color="white";
    				stepEle.childNodes[0].style.display="block";
    				if(lineEle && !this.freeModel)
    					lineEle.style.backgroundColor='#c5c5c5';
    				textEle.style.color='#000000';
                textEle.style.fontWeight='600';
    			}else{//未完成步骤样式
    				stepEle.style.background='url('+this.iconPath+'wait.png) no-repeat';
    				stepEle.style.color="gray";
    				stepEle.childNodes[0].style.display="block";
    				if(lineEle && !this.freeModel)
    					lineEle.style.backgroundColor='#c5c5c5';
    				textEle.style.color='#999';
                textEle.style.fontWeight='normal';
    			}
    			
    		}
    		this.fireEvent("stepchange",this,this.stepData[index],index);
	}
});