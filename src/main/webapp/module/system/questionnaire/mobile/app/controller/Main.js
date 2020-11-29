Ext.define('Questionnaire.controller.Main',{
	extend:'Ext.app.Controller',
	requires:['Ext.MessageBox','Questionnaire.view.CodeSelectField'],
	finishmsg:"",
	advanceendmsg:"",
	config:{
		refs:{
			main:'mainView',
			saveButton:'#savebutton',
			preCloseButton:'#preCloseButton'
		},
		control:{
			main:{
				initialize:'onMainLoad'
			},
			saveButton:{
				tap:'onButtonTap'
			},
			preCloseButton:{
				tap:'onPreCloseTap'
			}
		}
	},
	onMainLoad:function(view){
		var clientWidth = document.documentElement.clientWidth;
		var me = this,
			vo = new HashMap();
		if(planid2!='null'&&planid2!=null){
			planid = planid2;
		}
		vo.put("planid", planid);
		vo.put("transType", "loadData");
		vo.put("cip",cip);
		vo.put("configObj",configObj);
		Rpc({functionId:'QN70000002',success:function(res){
			var resultObj = Ext.decode(res.responseText),
				errormessage = resultObj.errormessage,
				instruction = resultObj.instruction,
				qnlongname = resultObj.qnlongname,
				oneip = resultObj.oneip,
				status = resultObj.status,
				index = 0;
				//zhangh 2020-2-7【57795】V771封版：问卷调查，设置了“不登录不允许答题”，扫二维码是空白界面，希望给出提示：您未登录，不允许答题！
				if(errormessage !=undefined){
					//目前没有移动端登录页面，未登录时无法自动跳转到登录页，在页面上给出提示信息
					document.write('<div style="color:#157efb;font-size:1.1em;position: absolute;top:50%;left:50%;margin-left:-95px;margin-top:-80px; ">您未登录，不允许答题！</div>');
					return;
				}
			if(status!=null){
				Ext.getCmp("mobileMainView").add({
					cls:'component-title2',
					margin:(document.body.clientHeight-200)/2+' 15 0 15',
					html:status,
					height:document.documentElement.clientWidth				
					});
				return;
			}
			if(oneip!=null){
				Ext.getCmp("mobileMainView").add({
					cls:'component-title2',
					margin:(document.body.clientHeight-200)/2+' 15 0 15',
					html:oneip,
					height:document.documentElement.clientWidth				
					});
				return;
			}	
			if(errormessage!=null){
				view.add(Ext.create("Questionnaire.view.MobileLogin",{
					html:'<iframe src="../../../../module/system/questionnaire/mobile/mobileLogin.jsp?planid='+resultObj.planid+'" width="100%" height="'+(document.documentElement.clientHeight-5)+'" frameborder="0" ></iframe>'
				}));
				return;
			}
			view.add({maxWidth:clientWidth,cls:'component-title',html:'<div style="font-color:blue;width:100%;height:100%;text-align:center;">'+(qnlongname==undefined?"":qnlongname)+'</div>'});
			view.add({maxWidth:clientWidth-1,xtype:'component',html:'<div style="width:100%;height:100%;margin-left:5px;margin-right:15px;">'+instruction+'</div>'});
			
			
			me.finishmsg = resultObj.finishmsg,
			me.advanceendmsg = resultObj.advanceendmsg,
			quesList = me.quesList = resultObj.params,
			quesIndex = 0,
			numberArr = [];
			 	
			for ( var i = 0; i < quesList.length; i++) {
				var ques = quesList[i];
				var typekind = ques.typekind;
				if(ques.typekind==10 || ques.typekind==11)
					continue;
				
				if(ques.typekind==9){
					view.add(me.createDescription(ques));
					continue;
				}
				
				quesIndex++;
				//单多选
				if(typekind == 1 || typekind == 2){
					view.add(me.createRadioOrMultiple(ques, quesIndex));
				}else if(typekind==3||typekind==4){//填空
					view.add(me.createGapFilling(ques, quesIndex,numberArr));
				}else if(typekind==5||typekind==6){//图片单多选
					view.add(me.createImgSelect(ques, quesIndex));
				}else if(typekind==7||typekind==8||typekind==14||typekind==15){//矩阵题
					view.add(me.createMatrix(ques, quesIndex));
				}else if(typekind==12){//打分
					view.add(me.createJudge(ques, quesIndex));
				}else if(typekind==13){//量表
					view.add(me.createQuantization(ques, quesIndex));
				}
				
				ques.quesIndex=quesIndex;
			}
			
			if(isPreview != "true"){
				view.add({
					xtype:'button',
					id:'savebutton',
					text:'<font color="#fff">提交</font>',
					margin:'10 5 10 5',
					maxWidth:clientWidth,
					height:40,
					style:{
						backgroundColor:'#4CD964',
						border:'1px solid #4CD964'
					}
					});
			}else{
				view.add({
					xtype:'button',
					id:'preCloseButton',
					text:'<font color="#fff">关闭</font>',
					margin:'10 5 10 5',
					maxWidth:clientWidth,
					height:40,
					style:{
						backgroundColor:'#4CD964',
						border:'1px solid #4CD964'
					}
					});
			}
		}}, vo);
	},
	/**
	 * 创建矩阵题
	 * @param obj
	 * @param index
	 * @returns
	 */
	createMatrix:function(obj, index){
		var clientWidth = document.documentElement.clientWidth;
		var typekind = obj.typekind,
			options = obj.optionList,
			set = obj.set,
			extrainput = set.extrainput,
			required = set.required,
			list = [],
			sig = "",
			answer = obj.answer||{},//xiegh bug:29672 date 20170713
			field, 
			xtype = 'radiofield';
		if((set.skip!=null&&set.skip=="false")||(set.required!=null&&set.required=="true")){
			sig = "<font color='red'>*</font>";
		}
		list.push({maxWidth:clientWidth,xtype:'component',docked:'top',cls:'component-title',html:sig+'Q'+index+'.'+obj.name});
		
		if(typekind==8)
			xtype = 'checkboxfield';
		if(typekind==7||typekind==8){
			var levels = obj.levelList;
				for ( var i = 0; i < options.length; i++) {
					var l = [];
					//if(i==0)//xiegh bug:29672 date 20170713  and bug 39639 矩阵题 项 名称显示不全 wangb 20180816 
						list.push({maxWidth:clientWidth,xtype:'component',cls:'option-title',html:(i+1)+'、'+options[i].optname});
					for ( var j = 0; j < levels.length; j++) {
						var checked = answer[i+"_"+j]?true:false;
						list.push({
							labelWrap:true,maxWidth:clientWidth,xtype:xtype,label:levels[j].optname,
							name:'Q'+obj.questionid+'_'+options[i].optid,value:levels[j].optid,checked:checked
						});
					}
				}
		} else if(typekind==14){
			var leftdesc = set.leftdesc,
				middledesc = set.middledesc,
				rightdesc = set.rightdesc,
				minscore = set.minscore,
				maxscore = set.maxscore;
			for ( var i = 0; i < options.length; i++) {
				var score = answer[i]?answer[i]:minscore;
				list.push({margin:"0 0 0 10",xtype:'label',html:(i+1)+'、'+options[i].optname});
				list.push({maxWidth:clientWidth,xtype:'sliderfield',
					name:'Q'+obj.questionid+'_'+options[i].optid,minValue:parseInt(minscore),maxValue:parseInt(maxscore),value:parseInt(score)});
				list.push({
					maxWidth:clientWidth,xtype:'container',style:'background-color:#fff',layout:'hbox',padding:'0 15 0 15',
					defaults:{maxWidth:clientWidth,xtype:'component',flex:1},
					items:[{html:leftdesc},
					       {maxWidth:clientWidth,html:'<div style="width:100%;height:100%;text-align:center;">'+middledesc+'</div>'},
					       {maxWidth:clientWidth,html:'<div style="width:100%;height:100%;text-align:right;">'+rightdesc+'</div>'}
					]
				});
					       
			}
		    
		} else if(typekind==15){
			var levels = set.levels,
				showscore = set.showscore;
			for ( var i = 0; i < options.length; i++) {
				list.push({maxWidth:clientWidth,xtype:'component',cls:'option-title',html:(i+1)+'、'+options[i].optname});
				for ( var j = 0; j < levels.length; j++) {
					var labelScore = showscore=='true'?'('+levels[j].score+'分)':'';
					var becheck = answer[i] && answer[i]==levels[j].score;
					list.push({
						labelWrap:true,maxWidth:clientWidth,xtype:'radiofield',label:levels[j].text+labelScore,
						value:levels[j].score,name:'Q'+obj.questionid+'_'+options[i].optid,checked:becheck
					});
				}
			}
		}
		
		if(extrainput=="true"){
			var requireText = required=='true'?"，必填":"";
			list.push({maxWidth:clientWidth,xtype: 'textareafield',style:'border:1px solid #c5c5c5',placeHolder:'说明'+requireText,name : 'Q'+obj.questionid+"_desc",selectOnFocus : true,value:answer.desc});
		}
		field = Ext.create('Ext.Container',{
			style:'border-bottom:1px solid #c5c5c5',
			id:'qn_'+obj.questionid,
			skip:set.skip,//题目是否可以跳过
			required:set.required,//填充框是否必填
			style:'border-bottom:1px solid #c5c5c5',
			maxWidth:clientWidth,
			minHeight:100, 
			layout:{
				type:'vbox',
				align:'stretch'
			},
			defaults:{
				labelWidth:'88%',
				labelAlign:'left'
			},
			items:list
		});
		return field;
	},
	/**
	 * 创建单多选题
	 * @param obj
	 * @param index
	 * @returns
	 */
	createRadioOrMultiple:function(obj, index){
		var clientWidth = document.documentElement.clientWidth;
		var typekind = obj.typekind,
			options = obj.optionList,
			levels = obj.levelList,
			list = [],
			sig ="",
			set = obj.set,
			answer = obj.answer,
			random = set.random,
			field;
			if(set.skip!=null&&set.skip=="false"){
				sig = "<font color='red'>*</font>";
			}
			list.push({maxWidth:clientWidth,xtype:'component',cls:'component-title',html:sig+'Q'+index+'.'+obj.name});
			for(i = 0;i<options.length;i++){
				if(typekind == 1){
					// 先验证answer值是否为空，不为空继续验证answer[i+1]是否为空 wangb 2017-4-19 
					if(answer&&answer[i+1])
						list.push({labelWrap:true,maxWidth:clientWidth,xtype: 'radiofield',name : 'Q'+obj.questionid,value: options[i].optid,label: options[i].optname,checked:true});
					else
						list.push({labelWrap:true,maxWidth:clientWidth,xtype: 'radiofield',name : 'Q'+obj.questionid,value: options[i].optid,label: options[i].optname});
				}else{
					// 先验证answer值是否为空，不为空继续验证answer[i+1]是否为空 wangb 2017-4-19
					if(answer&&answer[i+1])
						list.push({labelWrap:true,maxWidth:clientWidth,xtype: 'checkboxfield',name : 'Q'+obj.questionid,value: options[i].optid,label: options[i].optname,checked:true});
					else
						list.push({labelWrap:true,maxWidth:clientWidth,xtype: 'checkboxfield',name : 'Q'+obj.questionid,value: options[i].optid,label: options[i].optname});
				}
			}
			if(random=="true"){
				var arr=[];
    			for(var i=0;i<list.length;i++){
    				if(i==0)
    					continue;
    				else
    					arr[i-1]=list[i];
        		}
    			arr.sort(function(){ return 0.5 - Math.random() });
    			Array.prototype.insert = function (index, item) { 
					this.splice(index, 0, item); 
				};
    			arr.insert(0,list[0]);
    			list = arr;
			}
		var warnMsg = "";	
		if(obj.set.minselect>0)
			warnMsg = "最少选"+obj.set.minselect+"项";
		if(obj.set.maxselect>0){
			warnMsg = warnMsg.length>0?warnMsg+";":warnMsg;
			warnMsg += "最多选"+obj.set.maxselect+"项";
		}
		if(warnMsg.length>0){
			list.push({xtype:'component',style:'padding:0px 0px 10px 10px;color:red;background:white',html:"*此题"+warnMsg});
		}
		field = Ext.create('Ext.Container',{
			id:'qn_'+obj.questionid,
			style:'border-bottom:1px solid #c5c5c5',
			skip:set.skip,//题目是否可以跳过
			minHeight:100,
			maxWidth:clientWidth, 
			layout:{
				type:'vbox',
				align:'stretch'
			},
			defaults:{
				labelWidth:'88%',
				labelAlign:'left'
			},
			items:list
		});
		return field;
	},
	/**
	 * 创建填空题
	 * @param obj
	 * @param index
	 * @returns
	 */
	createGapFilling:function(obj, index, numberArr){
		var clientWidth = document.documentElement.clientWidth;
		var typekind = obj.typekind,
			inputset = obj.set,
			options = obj.optionList,
			answer = obj.answer||{},//xiegh bug:29672 date 20170713
			levels = obj.levelList,
			list = [],
			inputheight = 4,
			maxlength = 20,
			sig = "",
			me = this,
			field;
			if(inputset.skip!=null&&inputset.skip=="false"){
				sig = "<font color='red'>*</font>";
			}
			list.push({maxWidth:clientWidth,xtype:'component',cls:'component-title',html:sig+'Q'+index+'.'+obj.name});
			if(JSON.stringify(answer) == "{}"){//xiegh date 20170713 bug:29698
				answer='';
			}
			if(typekind == 3){
				var quesName = 'D'+obj.questionid;
				switch (inputset.inputtype) {
					case "1" :
						maxlength = parseInt(inputset.length,10);
						list.push({
							name : quesName,
							maxWidth:clientWidth,
							xtype: 'textfield',
							maxLength:maxlength,
							placeHolder:'在此填写内容',
							value: answer,
							selectOnFocus : true
						});
						break;
					case "2" :
						//if(inputset.skip!=null&&inputset.skip=="false")
						//	numberArr.push('Q'+obj.questionid);
						list.push({
							name : quesName,
							maxWidth:clientWidth,
							xtype: 'numberfield', 
							anchor: '100%',
							placeHolder:'在此填写内容',
							maxValue: 99,
							minValue: 0,
							selectOnFocus : true,
							value:parseInt(answer)
						});
				 	   	break;
				 	case "3" :
				 	    list.push({
				 	    	name : quesName,
				 	    	maxWidth:clientWidth,
				 	    	xtype: 'superdatetimepickerfield',
				 	    	picker:{ 
				 	    			slotOrder: ['year', 'month','day']
				 	    			},
				 	    	name : 'D'+obj.questionid,
				 	    	dateFormat: 'Y.m.d'
				 	    });
				 	   	break;
				 	case "4" :
				 		list.push({
				 			name : quesName,
				 			maxWidth:clientWidth,xtype:'codeselectfield',
				 			codesetid:inputset.codeset,placeHolder:'在此填写内容'
				 		});
				 	   	break;
				 	case "5" :
				 		maxlength = parseInt(inputset.length,10);
				 		inputheight = parseInt(inputset.inputheight,10);
				 		//判断文本是否限制长度 27932  wangb 20170531
				 		if(inputset.limit==1){
							list.push( {name : quesName,maxWidth:clientWidth,maxLength:maxlength,xtype: 'textareafield',maxRows:inputheight-1,placeHolder:'在此填写内容',selectOnFocus : true,value:answer});
				 		}else{
					 		list.push( {name : quesName,maxWidth:clientWidth,xtype: 'textareafield',maxRows:inputheight-1,placeHolder:'在此填写内容',selectOnFocus : true,value:answer});
				 		}
				 	   	break;
				}
			}else{
				for(i = 0;i<options.length;i++){
					list.push({maxWidth:clientWidth,xtype:'component',cls:'option-title',html:(i+1)+'、'+options[i].optlongname});
					var optName = 'D'+obj.questionid+"_"+options[i].optid;
					switch (inputset.inputtype) {
						case "1" :
							maxlength = parseInt(inputset.length,10);
							list.push({
								name : optName,xtype: 'textfield',grow:true,
								maxWidth:clientWidth,maxLength:maxlength,
								placeHolder:'在此填写内容',value: answer[i],selectOnFocus : true
							});
							break;
						case "2" :
							//if(inputset.skip!=null&&inputset.skip=="false")
							//	numberArr.push('D'+obj.questionid);
							list.push({
								name : optName,maxWidth:clientWidth,
								xtype: 'numberfield', anchor: '100%',placeHolder:'在此填写内容',
								maxValue: 99,minValue: 0,selectOnFocus : true,
								value:parseInt(answer[i])
							});
				 	   		break;
				 		case "3" :
				 			list.push({
				 			name : optName,
				 			maxWidth:clientWidth,
				 	    	xtype: 'superdatetimepickerfield',
				 	    	picker:{ 
				 	    			slotOrder: ['year', 'month','day']
				 	    			},
				 	    	dateFormat: 'Y.m.d'
				 	    	,selectOnFocus : true
				 	   		});
				 	   		break;
				 		case "4" :
				 			list.push({
				 				name : optName,maxWidth:clientWidth,xtype:'codeselectfield',
				 				codesetid:inputset.codeset,placeHolder:'在此填写内容'
				 			});
				 	   		break;
				 		case "5" :
				 			maxlength = parseInt(inputset.length,10);
				 			inputheight = parseInt(inputset.inputheight,10);
				 			list.push( {
				 				name : optName,maxWidth:clientWidth,maxLength:maxlength,xtype: 'textareafield',
				 				maxRows:inputheight-1,placeHolder:'在此填写内容',selectOnFocus : true,
				 				value:answer[i]
				 			});
				 	   		break;
					}
				}
			}
		field = Ext.create('Ext.Container',{
			id:'qn_'+obj.questionid,
			maxWidth:clientWidth,
			style:'border-bottom:1px solid #c5c5c5',
			layout:{
				type:'vbox',
				align:'stretch'
			},
			defaults:{
				labelWidth:'88%',
				labelAlign:'left'
			},
			items:list
		});
		return field;
	},
	/**
	 *创建图片选择题
	 *
	 *
	 **/
	createImgSelect:function(obj, index){
		var clientWidth = document.documentElement.clientWidth;
		var typekind = obj.typekind,
			inputset = obj.set,
			options = obj.optionList,
			answer = obj.answer||{},
			levels = obj.levelList,
			list = [],
			littlelist = [],
			field,
			sig = "",
			littleField;
			if(inputset.skip!=null&&inputset.skip=="false"){
				sig = "<font color='red'>*</font>";
			}
			var length = (document.documentElement.clientWidth*24)/100;
			list.push({maxWidth:clientWidth,xtype:'component',cls:'component-title',html:sig+'Q'+index+'.'+obj.name});
			var defaultValue = typekind==6?{}:"";
			for(i = 0;i<options.length;i++){
			var showImg;
			if(typekind==5){
					showImg = Ext.create('Ext.Container',{
						maxWidth:clientWidth,
						style:'background:white',
						width:length,
						height:length+12,
						padding:'10 10 10 10',
						items:[{
							id:"Q"+obj.questionid+"_"+options[i].optid+"_"+'radio'+i,
							xtype:'image',
							cls:'img-border-radius',
							optid:options[i].optid,
//						 	src :"/servlet/DisplayOleContent?filePath="+options[i].imgurl.replace(/\//g,'`')+"&mobile=1&filename=1&bencrypt=true&caseNullImg=/images/photo.jpg",//bencrypt=false 改为true  路径为加密路径，为false时不加密无法解析路径?
							//xus 20/3/2 vfs改造
						 	src :"/servlet/vfsservlet?fileid="+options[i].imgurl,
						 	width:length-20,
						 	height:length-20,
						 	margin:'0 0 0 5',
						 	listeners:{
						 		tap:function(){
						 			for(var k = 0;k<options.length;k++){
						 				Ext.getCmp("Q"+obj.questionid+"_"+options[k].optid+"_"+"radio"+k).removeCls("img-selected");
						 				Ext.getCmp("sel"+obj.questionid+"_"+options[k].optid+"_"+"radio"+k).hide();
						 			}
						 			this.addCls("img-selected")
						 			Ext.getCmp('sel'+(this.id).substring(1)).show();
						 			Ext.getCmp('Q'+obj.questionid+'_value').setValue(this.optid);
								}
						 	}
						},
						{
							id:"sel"+obj.questionid+"_"+options[i].optid+"_"+'radio'+i,
							xtype:'image',
							hidden:true,
							maxWidth:clientWidth,
							top:(20-length),
							cls:'img-border-radius sel-img',
						 	src :'../../questionnaire/images/selectedd.png',
						 	width:length-20,
						 	height:length-20,
						 	position:'Relative',
						 	top:10,
						 	left:10
						},{
							width:length-10,
							html:'<div style="height:100%;text-align:center;">'+options[i].optname+'</div>'
						}]
					});
					if(answer[i]){
						Ext.getCmp("Q"+obj.questionid+"_"+options[i].optid+"_"+'radio'+i).addCls("img-selected");
						Ext.getCmp("sel"+obj.questionid+"_"+options[i].optid+"_"+'radio'+i).show();
						defaultValue=options[i].optid;
					}
			}else if(typekind==6){
					showImg = Ext.create('Ext.Container',{
						style:'background:white',
						width:length,
						height:length+12,
						padding:'10 10 10 10',
						items:[{
							id:"Q"+obj.questionid+"_"+options[i].optid+"_"+'checkbox'+i,
							optid:options[i].optid,
							xtype:'image',
							cls:'img-border-radius',
							maxWidth:clientWidth,
//						 	src :"/servlet/DisplayOleContent?filePath="+options[i].imgurl.replace(/\//g,'`')+"&mobile=1&filename=1&bencrypt=true&caseNullImg=/images/photo.jpg",//bencrypt=false 改为true
							src :"/servlet/vfsservlet?fileid="+options[i].imgurl,
						 	width:length-20,
						 	height:length-20,
						 	listeners:{
						 		tap:function(){
						 			this.addCls("img-selected");
						 			Ext.getCmp('sel'+(this.id).substring(1)).show();
						 			
						 			var valueBox = Ext.getCmp('Q'+obj.questionid+'_value');
						 			var value = valueBox.getValue();
						 			value[this.optid] = 1;
						 			valueBox.setValue(value);
						 			
								}
						 	}
						},{
							id:"sel"+obj.questionid+"_"+options[i].optid+"_"+'checkbox'+i,
							xtype:'image',
							optid:options[i].optid,
							hidden:true,
							cls:'img-border-radius sel-img',
							src :'../../questionnaire/images/selectedd.png',						 	
							width:length-20,
						 	height:length-20,
						 	maxWidth:clientWidth,
						 	position:'Relative',
						 	top:10,
						 	left:10,
						 	listeners:{
						 		tap:function(){
						 			Ext.getCmp('Q'+(this.id).substring(3)).removeCls("img-selected");
						 			Ext.getCmp(this.id).hide();
						 			
						 			var valueBox = Ext.getCmp('Q'+obj.questionid+'_value');
						 			var value = valueBox.getValue();
						 			value[this.optid] = 0;
						 			valueBox.setValue(value);
								}
						 	}
						},{
							width:length-10,
							html:'<div style="height:100%;text-align:center;">'+options[i].optname+'</div>'
						}]
					});
					defaultValue[options[i].optid] = 0;
					if(answer[i]){
						Ext.getCmp("Q"+obj.questionid+"_"+options[i].optid+"_"+'checkbox'+i).addCls("img-selected");
						Ext.getCmp("sel"+obj.questionid+"_"+options[i].optid+"_"+'checkbox'+i).show();
						defaultValue[options[i].optid] = 1;
					}
				}
					littlelist.push(showImg);
					if((i+1)%4==0||i==(options.length-1)){
						if(i==(options.length-1)&&(options.length)%4!=0){
							var k = 0;
							k = 4-((i+1)%4);
							for(j =0;j<k;j++){
								var addI = Ext.create('Ext.Container',{
									style:'background:white',
									width:length-20,
									height:length-20,
									maxWidth:clientWidth
								});
								littlelist.push(addI);
							}
						}
						littleField = Ext.create('Ext.Container',{
							style:'background:white',
							layout:{
								type:'hbox'
							},
							maxWidth:clientWidth,
							height:length+12,
							items:littlelist
						});
						list.push(littleField);
						littlelist.length = 0;
					}
			}

		list.push({
			xtype:'hiddenfield',
			id:"Q"+obj.questionid+"_value",	
			name:"Q"+obj.questionid,
			value:defaultValue	
		});
		
		var warnMsg = "";	
		if(obj.set.minselect>0)
			warnMsg = "最少选"+obj.set.minselect+"项";
		if(obj.set.maxselect>0){
			warnMsg = warnMsg.length>0?warnMsg+";":warnMsg;
			warnMsg += "最多选"+obj.set.maxselect+"项";
		}
		if(warnMsg.length>0){
			list.push({xtype:'component',style:'padding-left:5px;color:red',html:"*此题"+warnMsg});
		}
		field = Ext.create('Ext.panel.Panel',{
			style:'background:white',
			id:'qn_'+obj.questionid,
			skip:inputset.skip,//题目是否可以跳过
			required:inputset.required,//填充框是否必填
			style:'border-bottom:1px solid #c5c5c5',
			maxWidth:clientWidth,
			layout:{
				type:'vbox',
				align:'stretch'
			},
			defaults:{
				labelWidth:'88%',
				labelAlign:'left'
			},
			items:list
		});
		return field;
	},
	/**
	 * 创建描述题
	 * @param obj
	 * @param index
	 * @returns
	 */
	createDescription:function(obj, index){
		var clientWidth = document.documentElement.clientWidth;
		var typekind = obj.typekind,
			options = obj.optionList,
			answer = obj.answer||{},
			levels = obj.levelList,
			list = [],
			field;
			list.push({maxWidth:clientWidth,xtype:'component',cls:'component-title',html:obj.longname});
		field = Ext.create('Ext.Container',{
			id:'qn_'+obj.questionid,
			style:'border-bottom:1px solid #c5c5c5',
			layout:{
				type:'vbox',
				align:'stretch'
			},
			defaults:{
				labelWidth:'88%',
				labelAlign:'left'
			},
			items:list
		});
		return field;
	},
	createJudge:function(obj,index){
		var clientWidth = document.documentElement.clientWidth;
		var typekind = obj.typekind,
			options = obj.optionList,
			levels = obj.levelList,
			answer = obj.answer,
			set = obj.set,
			list = [],
			sig = "",
			field;
			//【60936】问卷调查：问卷设计中矩阵打分题和矩阵量表题设置了必填，在移动端答题时必填标识不显示
			if((set.skip!=null&&set.skip=="false")||(set.required!=null&&set.required=="true")){
				sig = "<font color='red'>*</font>";
			}
			var leftdesc = set.leftdesc,
				middledesc = set.middledesc,
				rightdesc = set.rightdesc,
				minscore = set.minscore,
				maxscore = set.maxscore,
				extrainput = set.extrainput,
				required = set.required;
				list.push({maxWidth:clientWidth,xtype:'component',cls:'component-title',html:sig+'Q'+index+'.'+obj.name});
				list.push({maxWidth:clientWidth,xtype:'sliderfield',labelAlign:'top',label:'',
					name:'Q'+obj.questionid,minValue:parseInt(minscore),maxValue:parseInt(maxscore),value:parseInt(answer==null?minscore:answer.score)});//xiegh 20170502 bug27216
				list.push({maxWidth:clientWidth,xtype:'container',style:'background-color:#fff',layout:'hbox',padding:'0 15 0 15',
					defaults:{maxWidth:clientWidth,xtype:'component',flex:1},items:[{html:leftdesc},
					       {maxWidth:clientWidth,html:'<div style="width:100%;height:100%;text-align:center;">'+middledesc+'</div>'},
					       {maxWidth:clientWidth,html:'<div style="width:100%;height:100%;text-align:right;">'+rightdesc+'</div>'}]});
				if(extrainput=="true"){
					var requireText = required=='true'?"，必填":"";
					list.push({maxWidth:clientWidth,xtype: 'textareafield',style:'border:1px solid #c5c5c5',placeHolder:'说明'+requireText,name : 'Q'+obj.questionid+"_desc",selectOnFocus : true,value:answer.desc});
				}
		field = Ext.create('Ext.Container',{
			id:'qn_'+obj.questionid,
			style:'border-bottom:1px solid #c5c5c5',
			maxWidth:clientWidth,
			layout:{
				type:'vbox',
				align:'stretch'
			},
			items:list
		});
		return field;
	},
	//量标题
	createQuantization:function(obj,index){
		var clientWidth = document.documentElement.clientWidth;
		var typekind = obj.typekind,
			options = obj.optionList,
			answer = obj.answer||{},
			set = obj.set,
			list = [],
			sig = "",
			field;
			var levels = set.levels,
				required = set.required,
				column = set.column,
				extrainput = set.extrainput,
				levelcount = set.levelcount,
				sig="",
				type = set.type,
				skip = set.skip,
				required = set.required;
				if((set.skip!=null&&set.skip=="false")||(set.required!=null&&set.required=="true")){
				sig = "<font color='red'>*</font>";
				}
			list.push({maxWidth:clientWidth,xtype:'component',cls:'component-title',html:sig+'Q'+index+'.'+obj.name});
			for(i = 0;i<levels.length;i++){
				list.push({labelWrap:true,maxWidth:clientWidth,xtype: 'radiofield',name : 'Q'+obj.questionid,value: levels[i].score,label: levels[i].text+'('+levels[i].score+'分)'});
			}
			if(extrainput=="true"){
				var requireText = required=='true'?"，必填":"";
				list.push({maxWidth:clientWidth,xtype: 'textareafield',style:'border:1px solid #c5c5c5',placeHolder:'说明'+requireText,name : 'Q'+obj.questionid+"_desc",selectOnFocus : true,value:answer.desc});
			}
			field = Ext.create('Ext.Container',{
			id:'qn_'+obj.questionid,
			width:"99.5%",
			maxWidth:clientWidth,
			layout:{
				type:'vbox',
				align:'stretch'
			},
			defaults:{
				labelWidth:'88%',
				labelAlign:'left'
			},
			items:list
		});
		return field;
	},
	onButtonTap:function(){
		var mainp = this.getMain();
		var formValue = mainp.getValues();
		var matrixList = [];
		var mtScoreList = [];
		var quesList = this.quesList;
		var finishmsg = this.finishmsg;
		var advanceendmsg = this.advanceendmsg;
		var quesIndex = 0;
		var isSkip = false;
		var isBlank = false;
		var isWrongSelect = false;
		for(var indx=0;indx<quesList.length;indx++){
			var ques = quesList[indx];
			var typekind = ques.typekind;
			//描述题、分割符和分页符 直接跳过
			if(typekind==9 || typekind==10 || typekind==11){
				continue;
			}
			var skip = ques.set.skip;
			
			//单选
			if(typekind==1 || typekind==3 || typekind==5 || typekind==13){
				var idPre = typekind==3?'D':'Q';
				var value = formValue[idPre+ques.questionid];
				value = value==undefined?'':value;
				if(value.length<1 && skip!='true'){
					isSkip = true;
					quesIndex = ques.quesIndex;
					break;
				}
			  	formValue[idPre+ques.questionid] = value;
			}else if(typekind==2){//多选
				var value = formValue["Q"+ques.questionid];
				value = value==undefined?[]:value;
				if(value.length<1 && skip!='true'){
					isSkip = true;
					quesIndex = ques.quesIndex;
					break;
				}
				var newValue = [];
				if(Ext.isArray(value)){
					for(var i=0;i<value.length;i++){
						if(value[i]!=undefined)
							newValue.push(value[i]);
					}
				}else{
					newValue.push(value);
				}
				if(ques.set.minselect>0 && newValue.length<ques.set.minSelect){
					isWrongSelect = true;
					quesIndex = ques.quesIndex;
					break;
				}
				if(ques.set.maxselect>0 && newValue.length>ques.set.maxselect){
					isWrongSelect = true;
					quesIndex = ques.quesIndex;
					break;
				}
				formValue["Q"+ques.questionid] = newValue;
			}else if(typekind==4){
				for(var i=0;i<ques.optionList.length;i++){
					var opt = ques.optionList[i].optid
					value = formValue["D"+ques.questionid+"_"+opt];
					if(value.length<1 && skip!='true'){
						isSkip = true;
						break;
					}
				}
				if(isSkip){
					quesIndex = ques.quesIndex;
					break;
				}
			}else if(typekind==6){
				var value = formValue["Q"+ques.questionid];
				var newValue = [];
				for(var key in value){
					if(value[key]==1)
						newValue.push(key);
				}
				if(newValue.length<1 && skip!='true'){
					isSkip = true;
					quesIndex = ques.quesIndex;
					break;
				}
				if(ques.set.minselect>0 && newValue.length<ques.set.minSelect){
					isWrongSelect = true;
					quesIndex = ques.quesIndex;
					break;
				}
				if(ques.set.maxselect>0 && newValue.length>ques.set.maxselect){
					isWrongSelect = true;
					quesIndex = ques.quesIndex;
					break;
				}
				formValue["Q"+ques.questionid] = newValue;
			}else if(typekind==7 || typekind==15){
				for(var i=0;i<ques.optionList.length;i++){
					var opt = ques.optionList[i].optid;
					var value = formValue["Q"+ques.questionid+"_"+opt];
					value = value==undefined?'':value;
					if(value.length<1 && skip!='true'){
						isSkip = true;
						break;
					}
				  	formValue["Q"+ques.questionid+"_"+opt] = value;
				}
				if(isSkip){
					quesIndex = ques.quesIndex;
					break;
				}
				
				matrixList.push(ques.questionid);
				if(typekind==15)
					mtScoreList.push(ques.questionid);
			}else if(typekind==8){
				for(var i=0;i<ques.optionList.length;i++){
					var opt = ques.optionList[i].optid;
					var value = formValue["Q"+ques.questionid+"_"+opt];
					value = value==undefined?[]:value;
					if(value.length<1 && skip!='true'){
						isSkip = true;
						break;
					}
					var newValue = [];
					if(Ext.isArray(value)){
						for(var k=0;k<value.length;k++){
							if(value[k]!=undefined)
								newValue.push(value[k]);
						}
					}else{
						newValue.push(value);
					}
					formValue["Q"+ques.questionid+"_"+opt]=newValue;
				}
				if(isSkip){
					quesIndex = ques.quesIndex;
					break;
				}
				matrixList.push(ques.questionid);
			}else if(typekind==12){
				var value = formValue["Q"+ques.questionid];
				if(!Ext.isArray(value) && skip!='true'){
					isSkip = true;
					quesIndex = ques.quesIndex;
					break;
				}
				
				if(Ext.isArray(value))
					value = value[0];
					
				formValue["Q"+ques.questionid] = value;	
				
			}else if(typekind==14){
				for(var i=0;i<ques.optionList.length;i++){
					var optid = "Q"+ques.questionid+"_"+ques.optionList[i].optid;
					var value = formValue[optid];
					if(!Ext.isArray(value) && skip!='true'){
						isSkip = true;
						break;
					}
					
					if(Ext.isArray(value))
						value = value[0];
						
					formValue[optid] = value;	
				}
				
				if(isSkip){
					quesIndex = ques.quesIndex;
					break;
				}
				matrixList.push(ques.questionid);
				mtScoreList.push(ques.questionid);
			}
			//附加栏必填
			if(ques.set.extrainput=='true' && ques.set.required=='true' && formValue["Q"+ques.questionid+"_desc"].length<1){
				isBlank = true;
				quesIndex = ques.quesIndex;
				break;
			}
		}
		
		if(isSkip){
			alert("第"+quesIndex+"题为必答题！");
			return;
		}
		if(isWrongSelect){
			alert("第"+quesIndex+"题选择个数不符合要求！");
			return;
		}
		if(isBlank){
			alert("第"+quesIndex+"题说明栏必须填写！");
			return;
		}
		
		var vo = new HashMap();
		vo.put("values",formValue)
		vo.put("matrixList",matrixList);
		vo.put("mtScoreList",mtScoreList);
		vo.put("transType","saveData");
		vo.put("planid", planid);
		vo.put("cip",cip);
		vo.put("configObj",configObj);
		
		Rpc({functionId:'QN70000002',success:function(res){
			//zhangh判断是否提前结束了，根据结束状态不同，显示不同的提示信息
			var result = Ext.decode(res.responseText);
			var	whiteRoll = result.whiteRoll;
			Ext.getCmp("mobileMainView").removeAll();
			Ext.getCmp("mobileMainView").add({
				cls:'component-title2',
				margin:(document.body.clientHeight-200)/2+' 15 0 15',
				html:whiteRoll==true?this.advanceendmsg:this.finishmsg,
				height:document.documentElement.clientWidth				
			});
			
			if(module=="jobtitle")
				this.questionRs('1');
		},scope:this}, vo);
	},
	onPreCloseTap:function(){
		Ext.getCmp("mobileMainView").removeAll(); 
		if(module=="jobtitle")
			this.questionRs('0');
	},
	// 调查问卷回调，更新专家状态标识
	questionRs:function(state){
		url = unescape(url);
		url = this.replaceAll(url, '／', '/');
		url = this.replaceAll(url, '？', '?');
		url = this.replaceAll(url, '＆', '&');
		url = this.replaceAll(url, '＝', '=');
			
		if(state == "" || state == "0"){
			window.location.href = url;
			return ;
		} else if(state == "1"){//交卷
			var map = new HashMap();
			map.put("type", "2");//专家状态标识
			map.put("state", "2");
			map.put("w0501",w0501);
			map.put("w0301", w0301);
		    Rpc({functionId:'ZC00003011',async:false,success:function(form,action){
		    	var task = new Ext.util.DelayedTask(function(){
			    	window.location.href = url;
				}, this);
				task.delay(1000);
		    }},map);
		}
	},
	replaceAll:function ( str, from, to ) {
	    var idx = str.indexOf( from );
	    while ( idx > -1 ) {
	        str = str.replace( from, to ); 
	        idx = str.indexOf( from );
	    }
	   
	    return str;
   }
});