Ext.define("QuestionnaireTemplate.PreviewTemplate", {
	extend : 'Ext.container.Container',
    requires:["EHR.extWidget.field.DateTimeField","EHR.extWidget.field.CodeTreeCombox"],	
    cip:'',
    qnId:'',
    planId:'',
    suerveyid:undefined,
	itemId:undefined,	
	TemplatePanel:undefined,
	mainObject:'',
	subObject:'',
	style:'border:1px solid #c5c5c5;padding:10px',
	callback:Ext.emptyFn,
	scope:undefined,
	maxWidth:2000,
	//min-width:900,
	quesIndex:1,
	currentPageIndex:1,
	keyArrayCache:undefined,
	keyArray:undefined,
	initComponent : function() {
			this.callParent();
			//用到Ext组件的key集合
			this.keyArrayCache = [];
			//初始化试卷结构
			this.initStruts();
			//加载试卷
			this.previewTemplatedata();
	},
	//初始化试卷结构
	initStruts:function(){
	    this.innerHtml = 
	    "<table style='font-size:15px;min-width:900px' border=0 cellpadding=10 cellspacing=0>"+
	    "<tr>"+
	    //试卷名称标题
	    "<td  align='center'>${titleContainer}</td>"+
	    "</tr>"+
	    "<tr>"+
	    //试卷描述
	    "<td ><div style='width:900px;'>${descContainer}</div></td>"+
	    "</tr>"+
	    "<tr>"+
	    //试题区域
	    "<td id='questionContainer' style='border-top:3px solid #1C86EE;'>${questionContainer}</td>"+
	    "</tr>"+
	    "<tr>"+
	    //按钮区域
	    "<td align='center'>${buttonContainer}</td>"+
	    "</tr>"+
	    "</table>";
	
	},
	//创建试卷
	createQuestionnaire:function(quesObjs){
		var qnlongname = quesObjs.qnlongname,
		    instruction = quesObjs.instruction,
		    questionList = this.questionList =  quesObjs.questionList;
	    //试题标题
		this.innerHtml = this.innerHtml.replace("${titleContainer}",qnlongname);
		//试题描述
		this.innerHtml = this.innerHtml.replace("${descContainer}",instruction);
		
		//创建试题
		var pageHtml = this.createQuestionPage(questionList);//创建第一页
		this.innerHtml = this.innerHtml.replace("${questionContainer}",pageHtml);
		
		//创建按钮
		var buttonHtml = this.createButtons();
		this.innerHtml = this.innerHtml.replace("${buttonContainer}",buttonHtml);
		
		//渲染试卷
		this.setHtml(this.innerHtml);
		
		//将Ext组件渲染到指定位置
		for(var i=0;i<this.keyArray.length;i++){
		   //keyArray里是容器的id，加上_comp后缀就是Ext组件id
		   Ext.getCmp(this.keyArray[i]+"_comp").render(this.keyArray[i]);
		}
		var me = this;
		//为需要验证的元素添加事件
		var inputs = Ext.dom.Query.select("input[validator]");
		for(var i=0;i<inputs.length;i++){
		    inputs[i].onclick = function(){
		        var ele = this;
		        //根据元素validator属性的值调用相应方法
		        var validator = ele.getAttribute("validator");
		        me[validator](ele);
		    };
		}
		
		//为按钮添加点击事件处理函数
		var buttons = document.getElementsByTagName("a");
		for(var i=0;i<buttons.length;i++){
			if(!buttons[i].getAttribute("action")){
				continue;
			}
		    buttons[i].onmouseover = this.buttonOver;
		    buttons[i].onmouseout = this.buttonFree;
		    buttons[i].onmousedown = this.buttonDown;
		    buttons[i].onmouseup = this.buttonFree;
		    buttons[i].onclick = this.buttonHandler.bind(this);
		}
		
		//刷新视图
		this.updateLayout();
	},
	
	buttonOver:function(){
	  if(this.getAttribute("disable")!='true')
		this.style.backgroundColor="rgb(225,224,224)";
	},
	buttonDown:function(){
	  if(this.getAttribute("disable")!='true')
		this.style.backgroundColor="#c5c5c5";
	},
	buttonFree:function(){
		this.style.backgroundColor="rgb(248,248,248)";
	},
	//创建按钮
	createButtons:function(){
		var prePageStr = "";
		var nextPageStr = "";
		//如果需要分页，创建上下页按钮
		if(this.beNextPage){
			prePageStr = "<td><a href='###' disable='true' id='prePage'   style='display:inline-block;height:22px;border:1px #c5c5c5 solid; padding:2px 10px 4px 10px;background-color:rgb(248,248,248);color:gray !important;' action='prePage'>上页</a></td>";
			nextPageStr = "<td><a href='###' disable='false' id='nextPage'   style='display:inline-block;height:22px;border:1px #c5c5c5 solid; padding:2px 10px 4px 10px;background-color:rgb(248,248,248);color:#333 !important;' action='nextPage'>下页</a></td>";
		}
		
		var buttonStr = "";
		//如果是答卷模式，创建保存提交按钮
		if(this.viewModel=='submit'){
			//允许答完题查看分析
		    if(this.searchanswer==1)
		     	buttonStr+="<td><a href='###' id='analyseBtn'   style='display:none;height:22px;border:1px #c5c5c5 solid; padding:2px 10px 4px 10px;background-color:rgb(248,248,248);color:#333 !important;' action='analyse'>分析</a></td>";
		    //允许保存功能(不提交，下次进入可以接着答题。需要登录)
		    if(this.saveflag!='false')
				buttonStr+="<td><a href='###' id='saveBtn'   style='display:inline-block;height:22px;border:1px #c5c5c5 solid; padding:2px 10px 4px 10px;background-color:rgb(248,248,248);color:#333 !important;' action='save'>保存</a></td>";
		    //提交按钮
			buttonStr+="<td><a href='###' id='subBtn'  style='display:inline-block;height:22px;border:1px #c5c5c5 solid; padding:2px 10px 4px 10px;background-color:rgb(248,248,248);color:#333 !important;' action='submit'>交卷</a></td>";
		}
		
		return buttonStr = "<table><tr>"+prePageStr+buttonStr+nextPageStr+"</tr></table>";
	},
	//按钮处理
	buttonHandler:function(e){
	   var ele;
	   if(e)
	       ele = e.target;
	   else 
	       ele = event.srcElement;
	   var action = ele.getAttribute('action');
	   if(action=='submit')//交卷
	       this.submitAnswer('2');
	   else if(action=='save')//保存
	       this.submitAnswer('1');
	   else if(action=='analyse')//分析
	       this.analyseData();
	   else if(action=='prePage')//上一页
	   	   this.preAnswerPage(ele);
	   else if(action=='nextPage')//下一页
	   	   this.nextAnswerPage(ele);
	},
	
	//加载分析数据
	analyseData:function(){
		var me = this;
		Ext.require('QuestionnaireTemplate.QuestionnaireBuilder', function(){
			me.setBorder(0);
			var parent = me.ownerCt;
			me.ownerCt.removeAll(true);
			var AnalysisBuilder = Ext.create("QuestionnaireTemplate.QuestionnaireBuilder",{
				title:'结果分析',
				planId:me.planId+'',
				height:document.body.clientHeight,
				width:'100%',
				qnId:me.qnId+'',
				hideNavigation:true
			});
			parent.add(AnalysisBuilder);
		});
	},
	
	//翻下页处理
	nextAnswerPage:function(ele){
		//如果按钮是不可用状态，停止执行
	    if(ele.getAttribute('disable')=='true')
	        return;
	    this.ownerCt.setScrollY(0);    
	    //获取当前页div，设置隐藏
	    Ext.getDom("answerPage_"+this.currentPageIndex).style.display='none';
	    //当前页码+1
	    this.currentPageIndex = parseInt(this.currentPageIndex)+1;
	    //获取下页div
	    var nextPage = Ext.getDom("answerPage_"+this.currentPageIndex);
		nextPage.style.display='block';

		/*guodd 2019-12-06
			【45001】 多页动态加载时会导致答卷保存后再进入在第一页直接点提交时，因为后面的页还没加载，导致必填校验无法正确进行
			多页数据加载逻辑已修改，初始化会创建完毕所有页，只显示第一页，其他页隐藏，所以不需要再动态创建了，下面代码注释

	    //如果存在，显示
	    if(nextPage){
	        nextPage.style.display='block';
	    }else{//如果不存在，创建下页数据：
	    
	        //创建下页div 的html代码
			var pageStr = this.createQuestionPage(this.questionList,this.currentPageIndex);
			//获取试题容器
			var quesCon = Ext.getDom("questionContainer")
			//将下页div的html代码 插入到试题容器中
			Ext.dom.Helper.insertHtml("beforeEnd",quesCon,pageStr);
			//根据id将创建的Ext组件渲染到相应的页面元素中
			for(var i=0;i<this.keyArray.length;i++){
			   Ext.getCmp(this.keyArray[i]+"_comp").render(this.keyArray[i]);
			}
			var me = this;
			//有需要验证的 元素，添加点击事件
			var inputs = Ext.dom.Query.select("input[validator]");
			for(var i=0;i<inputs.length;i++){
			    inputs[i].onclick = function(){
			        var ele = this;
			        var validator = ele.getAttribute("validator");
			        me[validator](ele);
			    };
			}
			//如果试题原始参数对象中为0，表示所有试题已创建，当前页为最后一页，记录到totalPage中
			if(this.questionList.length<1)
			    this.totalPage = this.currentPageIndex;
		}
		 */
		
		//如果加载的页为最后一页，将下一页按钮改成灰色并 disable(按钮状态不可用)设置为true
		if(this.currentPageIndex == this.totalPage){
		    var nextbtn = Ext.getDom("nextPage");
				nextbtn.style.cssText="display:inline-block;height:22px;border:1px #c5c5c5 solid;padding:2px 10px 4px 10px;background-color:rgb(248,248,248);color:gray !important;";
				nextbtn.setAttribute('disable','true');
		}
		//将上一页按钮颜色改为正常，disable=false    
		var prebtn = Ext.getDom("prePage");
		    prebtn.style.cssText="display:inline-block;height:22px;border:1px #c5c5c5 solid; padding:2px 10px 4px 10px;background-color:rgb(248,248,248);color:#333 !important;";
		    prebtn.setAttribute('disable','false');
		    
		//刷新视图
		this.updateLayout();
		
		//如果有onNextPage，执行
		if(this.onNextPage)
		   this.onNextPage();
	},
	
	//翻上页处理
	preAnswerPage:function(ele){
	    //按钮不可用，不继续执行
		if(ele.getAttribute('disable')=='true')
	        return;
	    this.ownerCt.setScrollY(0);   
	    //隐藏当前页    
		Ext.getDom("answerPage_"+this.currentPageIndex).style.display='none';
		//页码-1
		this.currentPageIndex = parseInt(this.currentPageIndex)-1;
		//获取上一页并显示
		Ext.getDom("answerPage_"+this.currentPageIndex).style.display='block';
		//如果当前页码=1，上一页按钮变为不可用状态
		if(this.currentPageIndex==1){
		   var prebtn = Ext.getDom("prePage");
		   prebtn.style.cssText="display:inline-block;height:22px;border:1px #c5c5c5 solid; padding:2px 10px 4px 10px;background-color:rgb(248,248,248);color:gray !important;";
		   prebtn.setAttribute('disable','true');
		}
		
		//下一页按钮变为可用状态
		var nextbtn = Ext.getDom("nextPage");
		nextbtn.style.cssText="display:inline-block;height:22px;border:1px #c5c5c5 solid; padding:2px 10px 4px 10px;background-color:rgb(248,248,248);color:#333 !important;";
		nextbtn.setAttribute('disable','false');
		
		//刷新视图
		this.updateLayout();
		
		//调用onPrePage方法
		if(this.onPrePage)
		   this.onPrePage();
	},
	
	//提交答题数据 参数：submitType 1(保存)/ 2(交卷)
	submitAnswer:function(submitType){
	   var me = this;
	   //获取试题答案
	   var answerCollection = this.collectingAnswer(submitType);
	   //如果answerCollection==false说明答案校验有错，放弃保存
	   if(!answerCollection)
	        return;

	   	var vo = new HashMap();
	    	     vo.put("qnid", me.qnId+'');
    	     	 vo.put("mainObject", me.mainObject);
    	     	 vo.put("subObject", me.subObject);
    	     	 vo.put("cip", me.cip);
    	     	 vo.put("flag",submitType);
    	     	 vo.put("planid", me.suerveyid);
    	     	 vo.put("autoclosevalue", me.autoclosevalue+'');
    	     	 vo.put("autocloseselected", me.autocloseselected+'');
    	         vo.put("answer",answerCollection);
    	    var success = function(res){Ext.showAlert('保存成功！');};
    	    var failure = function(res){Ext.showAlert('保存失败！');};
	   if(submitType=='2'){

	       //隐藏保存和交卷按钮
	   	   var saveBtn = Ext.getDom("saveBtn");
	   	   if(saveBtn)saveBtn.style.display = 'none';
	   	   Ext.getDom("subBtn").style.display = 'none';
		   success = function(res){
		   	   //zhangh 2020-1-16 问卷已经提交，但是首页热点调查中问卷没有自动刷新
			   if(window.opener!=null){
				   window.opener.location.reload();
			   }
			   //【56370】问卷调查：提前结束答题的提示信息不对
			   //提交的时候，如果一道题都没有作答,就是提前交卷，结果作废，给出作废的提示信息
			   var showMsg = '';
			   if(Object.keys(answerCollection).length==0){
				   showMsg = me.advanceendmsg;
			   }else{
				   showMsg = me.finishmsg;
			   }
			   if(this.searchanswer!=1){
	           		if(me.mainObject!=''&&me.subObject!=''){
		    	     	          		Ext.showAlert(showMsg,function(){
							  			Ext.callback(me.callback,me.scope,['1','true']); //0是失败，1是成功 xiegh add 20170825 交卷后返回主界面
							  		});
		    	     	}else{
		    	     	          		Ext.showAlert(showMsg,function(){
		    	     	          			//【56476】提交问卷之后，统一隐藏保存和提交按钮，页面不关闭（之前是IE关闭，谷歌、火狐不能关闭）
		    	     	          			   /* top.window.opener = top;
										        top.window.open('','_self','');
										        top.window.close();*/
		    	     	          		});
		    	     	}
		    	     	
		    	     	return;	
	           }
	           
	           me.onNextPage = this.disablePaper;
			   me.onPrePage =this.disablePaper;
	           me.disablePaper();    
	           if(me.viewtype=='1'){
	           		Ext.getDom("analyseBtn").style.display = 'block';
		    	   }
	           Ext.callback(me.callback,me.scope,['1','false']);//xiegh 20170715对于 勾选了“允许答题人提交问卷后可以查看结果 ”的情况，没有更新专家状态
	           
	       };
	       failure = function(){
	           Ext.showAlert('保存失败！');
	       };
	   }
    	   Rpc({
    	   	  functionId:'QN40000002',async: false,scope:this,
    	   	  success:success,failure:failure
	   },vo);
	},
	
	//将所有选项置为不可编辑状态
	disablePaper:function(){
	     var inputs = Ext.dom.Query.select("input");
	     for(var i=0;i<inputs.length;i++){
	        inputs[i].disabled = true;
	     }
	     /********************start*****xiegh add 20170718***********************/
	     var inputAgrees = Ext.query("input[id^='agree_']");
	     var inputagainsts= Ext.query("input[id^='against_']");
	     var inputAbstentions = Ext.query("input[id^='abstentions_']");
	     for(var i =0;i<inputAgrees.length;i++)inputAgrees[i].disabled = false;
	     for(var i =0;i<inputagainsts.length;i++)inputagainsts[i].disabled = false;
	     for(var i =0;i<inputAbstentions.length;i++)inputAbstentions[i].disabled = false;
	     /***********************end********************************/
	     inputs = Ext.dom.Query.select("textarea");
	     for(var i=0;i<inputs.length;i++){
	         inputs[i].disabled = true;
	     }
	     for(var i=0;i<this.keyArrayCache.length;i++){
	         Ext.getCmp(this.keyArrayCache[i]+"_comp").setDisabled(true);
	     }
	     
	},
	
	//收集试卷答案 
	//  参数 1：保存  2：交卷 
	//返回值 JSON：答卷成功 / false：答案验证不通过，答卷失败
	collectingAnswer:function(type){
	   var tables = Ext.dom.Query.select("table[id$=_answerTable]");
	   var quesAnswerCollection = {},ansObj,typekind,skip;
	   for(var i=0;i<tables.length;i++){
	      typekind = tables[i].getAttribute("type");
	      skip = tables[i].getAttribute("skip");
	      if(typekind =='1' || typekind=='2'){
	      	ansObj = this.getSelectQuestionAnswer(tables[i],typekind);
	      }else if(typekind =='3'){
	        ansObj = this.getSingleFillBlankAnswer(tables[i],typekind);//提取填空题中的内容
	        if(!ansObj) return;
	      }else if(typekind =='4'){
	        ansObj = this.getMultipleFillBlankAnswer(tables[i],typekind); 
	      }else if(typekind =='5' || typekind =='6')
	      	ansObj = this.getImageSelectAnswer(tables[i],typekind);
	      else if(typekind =='7' || typekind =='8')
	      	ansObj = this.getMatrixSelectAnswer(tables[i],typekind);
	      else if(typekind =='12')
	      	ansObj = this.getScoreAnswer(tables[i],typekind);
	      else if(typekind =='13')
	      	ansObj = this.getScaleAnswer(tables[i],typekind);
	      else if(typekind =='14')
	      	ansObj = this.getMatrixScoreAnswer(tables[i],typekind);
	      else if(typekind =='15')
	        ansObj = this.getMatrixScaleAnswer(tables[i],typekind); 
	        
	      //如果type=2(交卷才需要校验必答) 并且 empty=true（代表 此题没有做答） 并且  此题不能 不答时，提示
	      if(type=='2' && ansObj.empty && skip!='true'){
	      	 //bug 35290  多选题当min 参数值小于2时，和大于等于2时提示内容不同 wangb 20180705
	      	 var min = tables[i].getAttribute("min");
	      	 if(min < 2)
	         	Ext.showAlert("第"+tables[i].getAttribute("index")+"题为必答题！");
	         else
	         	Ext.showAlert("第"+tables[i].getAttribute("index")+"题答案格式不对!");
	         return false;
	      }
	      
	      //验证通过，删除empty属性，将此题答案放到试卷答案对象中
	      delete ansObj.empty;
	      Ext.apply(quesAnswerCollection,ansObj);
	   }	
	
	   //如果所有试题都创建完成了，或者是保存(因为保存不需要校验必答)直接返回试卷答案对象。 
	   if(this.questionList.length==0 || type=='1'){
	       return quesAnswerCollection;
	   }
	   
	   //questionList中有值，说明有分页，并且答题时没有翻到最后一页就提交了，判断没有答的题有没有必答的
	   var index = this.quesIndex;
	   for(var i=0;i<this.questionList.length;i++){
	        if(this.questionList[i].typekind>8 && this.questionList[i].typekind<12)
	            continue;
	        var set = this.questionList[i].set||{skip:'true'};
	        if(set.skip!='true'){
	        		Ext.showAlert("第"+index+"题为必答题！");
	            return false;
	        }
	        index++;
	   }
	   return quesAnswerCollection;
	},
	
	//创建页数据 需要一次性都加载完全
	createQuestionPage:function(questions){
	    this.keyArray = new Array();
		var pageIndex = 1;
	    var pageHtml = "<div id=answerPage_"+pageIndex+">";
	    this.beNextPage = false;
	    while(questions.length>0){
	    		var ques = questions[0];
	    		//创建一道题，试题集合中移除一道题
	    		questions.splice(0,1);
	    		//如果遇到分页标记,本页加载完毕，跳出循环
	    		if(ques.typekind==10){
	    		   //需要分页标记
		           this.beNextPage = true;
					pageHtml+="</div>";
					pageIndex ++;
					//切换下一页，下页数据隐藏
					pageHtml+="<div id=answerPage_"+pageIndex+" style=\"display:none;\">";
					continue;
	       		}
	       	//创建试题
	       	pageHtml+=this.createQuestionHtml(ques);
	    }
	    
	    pageHtml+="</div>";
	    this.currentPageIndex = 1;
		this.totalPage = pageIndex;

	    if(this.keyArray.length>0)
	    		this.keyArrayCache = this.keyArrayCache.concat(this.keyArray);
	    return pageHtml;
	},
	
	//按试题类型创建试题 参数：试题对象 返回值：试题html
	createQuestionHtml:function(quesObj){
	      var skipStr = '<span style="color:red;font-size:18px;">*</span>';
	      if(quesObj.set && quesObj.set.skip=='true')
	          skipStr = '';
	      
	      var html="<table style='padding-top:10px;'>"+
			"<tr><td>"+skipStr+"Q"+this.quesIndex+". "+quesObj.longname+"</td></tr>";
		  var optStr = "";
	      if(quesObj.typekind==1){
	      	 optStr = this.singleOrMultipleSelectQuestion(quesObj,this.quesIndex);
	      }else if(quesObj.typekind==2){
	      	 optStr = this.singleOrMultipleSelectQuestion(quesObj,this.quesIndex);
	      }else if(quesObj.typekind==3){
	      	 optStr = this.singleFillBlankQuestion(quesObj,this.quesIndex);
	      }else if(quesObj.typekind==4){
	      	 optStr = this.multipleFillBlankQuestion(quesObj,this.quesIndex);
	      }else if(quesObj.typekind==5){
	      	 optStr = this.imageSingleOrMultipleQuestion(quesObj,this.quesIndex);
	      }else if(quesObj.typekind==6){
	      	 optStr = this.imageSingleOrMultipleQuestion(quesObj,this.quesIndex);
	      }else if(quesObj.typekind==7){
	      	 optStr = this.matrixSelectQuestion(quesObj,this.quesIndex);
	      }else if(quesObj.typekind==8){
	      	 optStr = this.matrixSelectQuestion(quesObj,this.quesIndex);
	      }else if(quesObj.typekind==9){
	         return "<div style='margin:20px 0px 20px 0px;width:900px;word-break: break-all;'>"+quesObj.longname+"</div>";
	      }else if(quesObj.typekind==11){
	         return "<div style='width:100%;border-top:2px gray dashed;margin:20px 0px 10px 0px;'></div>";
	      }else if(quesObj.typekind==12){
	      	optStr = this.scoreQuestion(quesObj,this.quesIndex);
	      }else if(quesObj.typekind==13){
	        optStr = this.scaleQuestion(quesObj,this.quesIndex);
	      }else if(quesObj.typekind==14){
	      	optStr = this.matrixScoreQuestion(quesObj,this.quesIndex);
	      }else if(quesObj.typekind==15){
	        optStr = this.matrixScaleQuestion(quesObj,this.quesIndex);
	      }
	      
	      html+="<tr><td>"+optStr+"</td></tr></table>";
	      this.quesIndex++;
	      return html;
	},
	
	
	//单（多）选择题
	singleOrMultipleSelectQuestion:function(quesObj,quesIndex){
	    var inputType = quesObj.typekind==1?'radio':'checkbox',selectValid = '',
			set = quesObj.set,optionList = quesObj.optionList,
			warnStr = "";
		//多选时判断选项个数有没有限制	
		if(quesObj.typekind==2 && set.skip!='true'){
			var max = parseInt(set.maxselect);//0是不控制
			var min = parseInt(set.minselect);//0是不控制
			var valid = true;
			//判断是否控制最少项 =0 和 =1是统一的效果，不控制
			if(min<2)
			    valid = false;
			else
			    warnStr+=" *最少选"+min+"项 ";
			
			//判断是否控制最多项
			if((max==0 || max>=optionList.length) && !valid){
			    valid = false;
			}else{
			    warnStr+=" *最多选"+max+"项 ";
			    valid = true;
			}
			if(valid)
				selectValid = " max="+set.maxselect+" min="+set.minselect+" validator=multipleSelectValidator ";
		}
		
		
		
		var answer = quesObj.answer||{};
		if(set.random=='true')
		    optionList.sort(this.randomsort);
		var tableId = "Q"+quesObj.questionid;
		//单选题必填时默认给0 bug 35290 wangb 20180706
		min = min==undefined? 0:min;
		var optStr = "<table id="+tableId+"_answerTable index="+quesIndex+" skip="+set.skip+"  type="+quesObj.typekind+" min="+min+">";
		
		//排列方式 横向
		if(set.type=='1'){
		   for(var i=0;i<optionList.length;i++){
		     var opt = optionList[i];
		     var checked = answer[opt.optid]?'checked="true"':'';
		     var optid = "Q"+quesObj.questionid+"_"+opt.optid;
		   	 optStr+="<tr><td width=30><input optid='"+opt.optid+"' type="+inputType+" name='"+tableId+"' "+checked+" "+selectValid+"/></td><td>"+opt.optlongname+"</td></tr>";
		   }
		}else if(set.type=='2'){//纵向
			optStr+="<tr>";
		   for(var i=0;i<optionList.length;i++){
		     var opt = optionList[i];
		     var checked = answer[opt.optid]?'checked="true"':'';
		     var optid = "Q"+quesObj.questionid+"_"+opt.optid;
		   	 optStr+="<td ><input optid='"+opt.optid+"' type="+inputType+" name='"+tableId+"' "+checked+" "+selectValid+"/>"+opt.optlongname+"</td>";
		   } 
		   optStr+="</tr>"; 
		}else if(set.type=='3'){
			//xus 单选为下拉选时 显示为下拉选 17/04/24
			var boxid = "Q"+quesObj.questionid+"_Box";
			   optStr+="<tr><td id="+boxid+"></td></tr>";
			   var states = Ext.create('Ext.data.Store', {
				    fields: ['abbr', 'name'],
				    data : []
				});
			   var data=[];
			   var value='';
			   for(var i=0;i<optionList.length;i++){
				   var opt = optionList[i];
				   if(answer[opt.optid]){
					   value=opt.optid;
				   }
				   data.push({"abbr":opt.optid, "name":opt.optlongname});
			   }
			   states.setData(data);
			   if(value=='')
				   value=optionList[0].optid;
			   Ext.widget('combo',{id:boxid+"_comp",store: states,editable:false,queryMode: 'local',displayField: 'name',valueField: 'abbr',value:value});
		       this.keyArray.push(boxid);
		}else if(set.type=='4' && set.column.length>0){//按列
		    var column = set.column;
		    column = column>0?column:1;
		    for(var i=0;i<optionList.length;i++){
		        if(i==0)
		        		optStr+="<tr>";
		        else if(i%column==0)
		        		optStr+="</tr><tr>";
		        
		        var opt = optionList[i];
		        var checked = answer[opt.optid]?'checked="true"':'';
		        var optid = "Q"+quesObj.questionid+"_"+opt.optid;
			    optStr+="<td ><input optid='"+opt.optid+"' type="+inputType+" name='"+tableId+"' "+checked+" "+selectValid+"/>"+opt.optlongname+"</td>";
		    }
		    optStr+="</tr>";
		}
		
		optStr+="</table>";
		if(warnStr.length>0)
		   optStr+="<span id="+tableId+"_warnSpan style='color:red;font-size:14px'>"+warnStr+"</span>";
		return optStr;
	},
	multipleSelectValidator:function(ele){
	     var inputs = document.getElementsByName(ele.name);
	     var max = parseInt(inputs[0].getAttribute("max"));
	     var min = parseInt(inputs[0].getAttribute("min"));
	     
	     var checkedCount = 0;
	     for(var i=0;i<inputs.length;i++)
	         if(inputs[i].checked)
	             checkedCount++;
	             
	     if(max!=0 && max<inputs.length && checkedCount>max){
	         ele.checked = false;
	     }
	     
	     if(checkedCount<min && min>1)
	         document.getElementById(ele.name+"_answerTable").setAttribute("empty",true);
	     else
	         document.getElementById(ele.name+"_answerTable").setAttribute("empty",false);
	},
	
	//获取选择题答案
	getSelectQuestionAnswer:function(table,typekind){
		var items = table.getElementsByTagName("input"),
		    Qid_ = table.id.replace("answerTable",""),
		    answerObj = {empty:true};
		if(typekind=='1'){
			//xus 17/04/25 获取下拉选的值
			if(Ext.getCmp(Qid_+'Box_comp')!=null){
				answerObj.empty = false;//下拉选择有值时 empty置为false
				answerObj[Qid_+"1"]=Ext.get(Qid_+'Box_comp').component.value+"";
			}else{
				for(var i=0;i<items.length;i++){
				    var item = items[i];
				    if(item.checked){
				    		answerObj.empty = false;
				        answerObj[Qid_+"1"]=item.getAttribute("optid");
				        break;
				    }
				}
			}
			return answerObj;
		}
         
		for(var i=0;i<items.length;i++){
		    var item = items[i];
		    if(item.checked){
		    		answerObj.empty = false;
		        answerObj[Qid_+item.getAttribute("optid")]=1;
		    }
		}
		if(table.getAttribute("empty")=='true')
		    answerObj.empty=true;
		return answerObj;
	},
	//单项填空
	singleFillBlankQuestion:function(quesObj,quesIndex){
		var set = quesObj.set,width=new Number(set.inputwidth).valueOf(),deci=set.decimal,
		    boxid = "Q"+quesObj.questionid+"_Box",
		    answer = quesObj.answer||'',
		    optStr = "<table id=Q"+quesObj.questionid+"_answerTable index="+quesIndex+" skip="+set.skip+" type="+quesObj.typekind+">"+
		             "<tr><td id="+boxid+"></td></tr></table>";
			
		this.keyArray.push(boxid);
		if(set.inputtype=='1'){
			Ext.widget('textfield',{id:boxid+"_comp",width:width,value:answer,maxLength:set.length,limit:1,quesIndex:quesIndex});//update by xiegh on date 20180313 bug35339
		}else if(set.inputtype=='2'){
			//最大值方法
			var maxva=1;
			for(var i=0;i<set.length;i++){
				maxva*=10;
			}
			maxva--;
			//zhangh 2020-6-11 原来的逻辑只考虑了整数，没有考虑小数
			var maxxs=1.0;
			for(var i=0;i<set.decimal;i++){
				maxxs*=0.1;
			}
			maxxs = 1-maxxs;
			maxva += maxxs;
			Ext.widget('numberfield',{id:boxid+"_comp",width:width,value:answer,maxValue:maxva,minValue:-maxva,decimalPrecision:deci});
		}else if(set.inputtype=='3'){
			Ext.widget('datetimefield',{id:boxid+"_comp",width:width,format:'Y-m-d',value:answer});
		}else if(set.inputtype=='4'){
			Ext.widget('codecomboxfield',{id:boxid+"_comp",width:width,codesetid:set.codeset,value:answer,ctrltype:'0'});
		}else if(set.inputtype=='5'){
			//判断文本是否限制长度 27932  wangb 20170531
			if(set.limit==1){//add by xiegh on date 20180313 bug35372 ①在对象中加了一个参数limit 保存或提交时会用到  ②当加了高度限制时  grow应该改为false quesIndex:问题序号  
				Ext.widget('textarea',{id:boxid+"_comp",width:width,value:answer,maxLength:set.length,grow:false,limit:set.limit,quesIndex:quesIndex,height:set.inputheight*13});//添加自适应属性不会出现滚动条 wangb 20170831  30807
			}else{
				Ext.widget('textarea',{id:boxid+"_comp",width:width,value:answer,grow:true,height:set.inputheight*13});//添加自适应属性不会出现滚动条 wangb 20170831  30807
			}
		}
		
		return optStr;
	},
	getSingleFillBlankAnswer:function(table,typekind){
		var items = table.getElementsByTagName("input"),
			comTd = table.rows[0].cells[0],
			Qid_ = table.id.replace("answerTable",""),
			answerObj = {empty:true},
			cmp = Ext.getCmp(comTd.id+"_comp"),
			value =  cmp.getValue();
			value = value?value+'':'';
			var quesIndex = "Q" + cmp.quesIndex;
			//add by xiegh on date 20180313 bug35372 如果该填空题设置了最大字符限制 且内容超过最大限制时，不允许保存或提交
			if(cmp.limit && cmp.maxLength && cmp.limit == 1 && cmp.value.length > cmp.maxLength){
		           Ext.Msg.show({
                       title:"提示信息",
                       msg:quesIndex+"的内容长度超出了最大限制字符数！",
                       buttons: Ext.Msg.YES,
                       buttonText: {yes: '确认'}
                   });
		           return;
			}
		if(value.length>0 && value!='`'){
	        if(value.indexOf('`')!=-1)
	             value = value.split('`')[0];
	        answerObj[Qid_+"1"]=value;
			answerObj.empty = false;
	    }
	    /**
	    *许硕 超出范围，不让其保存
	    *16/09/22
	    **/
	    if(value>Ext.getCmp(comTd.id+"_comp").maxValue||value<Ext.getCmp(comTd.id+"_comp").minValue){
	    	alert("第"+table.getAttribute("index")+"题,输入答案有误，请检查！");
	    	return;
	    }
		return answerObj;
	},
	
	//多项填空
	multipleFillBlankQuestion:function(quesObj,quesIndex){
		var set = quesObj.set,width=new Number(set.inputwidth).valueOf(),
		optionList = quesObj.optionList,
		answer = quesObj.answer||{};
		
		
		var optStr = "<table id=Q"+quesObj.questionid+"_answerTable index="+quesIndex+" skip="+set.skip+" type="+quesObj.typekind+">";
		
		if(set.inputtype=='1'){
		    for(var i=0;i<optionList.length;i++){
		    	   var boxid = "Q"+quesObj.questionid+"_"+optionList[i].optid+"_Box";
			   optStr+="<tr><td>"+optionList[i].optlongname+"</td><td id="+boxid+"></td></tr>";
			   var value = answer[optionList[i].optid]||"";
			   Ext.widget('textfield',{id:boxid+"_comp",optid:optionList[i].optid,width:width,value:value});
		       this.keyArray.push(boxid);
		    }
		}else if(set.inputtype=='2'){
			//最大值方法
			var maxva=1;
			for(var i=0;i<set.length;i++){
				maxva*=10;
			}
			maxva--;
			//zhangh 2020-6-11 原来的逻辑只考虑了整数，没有考虑小数
			var maxxs=1.0;
			for(var i=0;i<set.decimal;i++){
				maxxs*=0.1;
			}
			maxxs = 1-maxxs;
			maxva += maxxs;
			for(var i=0;i<optionList.length;i++){
			   var boxid = "Q"+quesObj.questionid+"_"+optionList[i].optid+"_Box";
			   optStr+="<tr><td>"+optionList[i].optlongname+"</td><td id="+boxid+"></td></tr>";
			   var value = answer[optionList[i].optid]||"";
			   Ext.widget('numberfield',{id:boxid+"_comp",optid:optionList[i].optid,width:width,value:value,maxValue:maxva,minValue:-maxva,decimalPrecision:set.decimal});
		       this.keyArray.push(boxid);
		    }
		}else if(set.inputtype=='3'){
			for(var i=0;i<optionList.length;i++){
			   var boxid = "Q"+quesObj.questionid+"_"+optionList[i].optid+"_Box";
			   optStr+="<tr><td>"+optionList[i].optlongname+"</td><td id="+boxid+"></td></tr>";
			   var value = answer[optionList[i].optid]||"";
			   Ext.widget('datetimefield',{id:boxid+"_comp",optid:optionList[i].optid,width:width,format:'Y-m-d',value:value});
		       this.keyArray.push(boxid);
		    }
		}else if(set.inputtype=='4'){
			for(var i=0;i<optionList.length;i++){
			   var boxid = "Q"+quesObj.questionid+"_"+optionList[i].optid+"_Box";
			   optStr+="<tr><td>"+optionList[i].optlongname+"</td><td id="+boxid+"></td></tr>";
			   var value = answer[optionList[i].optid]||"";
			   Ext.widget('codecomboxfield',{id:boxid+"_comp",optid:optionList[i].optid,width:width,codesetid:set.codeset,value:value,ctrltype:'0'});
		       this.keyArray.push(boxid);
		    }
		}
		
		return optStr+="</table>";
	},
	getMultipleFillBlankAnswer:function(table,typekind){
	    var Qid_ = table.id.replace("answerTable",""),answerObj = {empty:true};
	    for(var i=0;i<table.rows.length;i++){
	             var cmpId = table.rows[i].cells[1].id;
	             var cmp = Ext.getCmp(cmpId+"_comp");
	             var value = cmp.getValue();
	             value = value?value+'':'';
	             if(value.length>0 && value!='`'){
	                if(value.indexOf('`')!=-1)
	                   value = value.split('`')[0];
	             	answerObj[Qid_+cmp.optid] = value;
	             	answerObj.empty = false;
	             }
	    }
	    
	    return answerObj;
	},
	
	//图片单（多）选择题
	imageSingleOrMultipleQuestion:function(quesObj,quesIndex){
		var inputType = quesObj.typekind==5?'radio':'checkbox',set = quesObj.set,
	        optionList = quesObj.optionList,
	    		warnStr = "",selectValid = "";
	    		
	    //多选时判断选项个数有没有限制	
	    if(quesObj.typekind==6 && set.skip!='true'){
			var max = parseInt(set.maxselect);//0是不控制
			var min = parseInt(set.minselect);//0是不控制
			//判断是否控制最少项 =0 和 =1是统一的效果，不控制
			if(min<2)
			    valid = false;
			else
			    warnStr+=" *最少选"+min+"项 ";
			
			//判断是否控制最多项
			if((max==0 || max>=optionList.length) && !valid){
			    valid = false;
			}else{
			    warnStr+=" *最多选"+max+"项 ";
			    valid = true;
			}
			if(valid)
				selectValid = " max="+set.maxselect+" min="+set.minselect+" validator=multipleSelectValidator ";
		}
		
		var answer = quesObj.answer||{};
	    if(quesObj.set.random=='true')
	       optionList.sort(this.randomsort);
	    var tableId = "Q"+quesObj.questionid;
	    var optStr="<table id="+tableId+"_answerTable index="+quesIndex+" skip="+quesObj.set.skip+" type="+quesObj.typekind+">";
	    
	    for(var i=0;i<optionList.length;i++){
	       if(i==0)
	          optStr+="<tr>";
	       else if(i%6==0)
	       	  optStr+="</tr><tr>";
	       var option = optionList[i];
	       var checked = answer[option.optid]?' checked="true" ':'';
	       //xus 20/3/2 vfs改造
//	       var url = "/servlet/DisplayOleContent?mobile=zp_noticetemplate_flag&filePath="+option.imgurl+"&caseNullImg=/images/photo.jpg";
	       var url = "/servlet/vfsservlet?fileid="+option.imgurl;
	       optStr+="<td><table width=130><tr><img src="+url+" width=120 height=160></tr><tr><td><input optid="+option.optid+" name="+tableId+" type="+inputType+" "+checked+selectValid+">"+option.optlongname+"</td></tr></table></td>";
	    }
	    
	    if(warnStr.length>0)
		   optStr+="<span id="+tableId+"_warnSpan style='color:red;font-size:14px'>"+warnStr+"</span>";
	    return optStr+="</tr></table>";
	},
	getImageSelectAnswer:function(table,typekind){
	     var items = table.getElementsByTagName("input"),
			 Qid_ = table.id.replace("answerTable",""),
			 answerObj = {empty:true};
		if(typekind=='5'){
			for(var i=0;i<items.length;i++){
			    var item = items[i];
			    if(item.checked){
			        answerObj.empty = false;
			        answerObj[Qid_+"1"]=item.getAttribute("optid");
			        break;
			    }
			}
			
			return answerObj;
		}
		for(var i=0;i<items.length;i++){
		    var item = items[i];
		    if(item.checked){
		        answerObj[Qid_+item.getAttribute("optid")]=1;
		        answerObj.empty = false;
		    }
		}
		if(table.getAttribute("empty")=='true')
		    answerObj.empty = true;
		return answerObj;
	},
	//矩阵单(多)选题
	matrixSelectQuestion:function(quesObj,quesIndex){
	    var optionList = quesObj.optionList,
	    		levelList = quesObj.levelList,
	    		inputType = quesObj.typekind==7?'radio':'checkbox',
	    		answer = quesObj.answer||{};
	    		
	    	if(quesObj.set.random=='true')
	    	    levelList.sort(this.randomsort);
	    
	    var optStr="<table id=Q"+quesObj.questionid+"_answerTable index="+quesIndex+" type="+quesObj.typekind+" skip="+quesObj.set.skip+" border=1 cellpadding=5 style='border-collapse: collapse;' bordercolor='#c5c5c5'>";
	    for(var i=-1;i<optionList.length;i++){
	    		optStr+="<tr>";
	    		var opt;
	    		if(i==-1){
	           optStr+="<td></td>";
	        }else{
	        		opt = optionList[i];
	        		optStr+="<td style='max-width:250px;word-break:break-all;'>"+opt.optlongname+"</td>";
	        }
	        for(var k=0;k<levelList.length;k++){
	           var lev = levelList[k];
	           if(i==-1){
	               optStr+="<td align=center style='max-width:150px;word-break:break-all;'>"+lev.optlongname+"</td>";
	               continue;
	           }
	        	   var checked = answer[opt.optid+"_"+lev.optid]?'checked="true"':'';
	        	   optStr+="<td align=center><input optid="+opt.optid+" levid="+lev.optid+" name=Q"+quesObj.questionid+"_"+opt.optid+" type='"+inputType+"' "+checked+"/></td>";
	        
	        }
	        optStr+="</tr>";
	    }
	    return optStr+="</table>";
	},
	getMatrixSelectAnswer:function(table,typekind,answerObj){
	    var items = table.getElementsByTagName("input"),
			Qid = table.id.replace("_answerTable",""),
		    answerObj = {empty:true};
		
		var answers = new Array();
		var optObj = {};
		for(var i=0;i<items.length;i++){
		    var item = items[i];
		    if(item.checked){
		         var optid = item.getAttribute("optid");
		         if(optObj[optid]){
		             optObj[optid]["C"+(new Number(item.getAttribute("levid")).valueOf()+1)] = 1;
		         }else{
			         var answer = {itemid:Qid.replace("Q",""),optid:item.getAttribute("optid")};
			         answer["C"+(new Number(item.getAttribute("levid")).valueOf()+1)] = 1;
			         optObj[optid] = answer;
			     }
		         answerObj.empty = false;
		    }
		       
		}
		for(var key in optObj){
		    answers.push(optObj[key]);
		}
		answerObj[Qid] = answers;
	    return answerObj;
	},
	//打分题
	scoreQuestion:function(quesObj,quesIndex){
	    var set = quesObj.set,boxid = "Q"+quesObj.questionid+"_Box"
	        answer = quesObj.answer||{score:parseInt(set.minscore),desc:''},
	        optStr="<table id=Q"+quesObj.questionid+"_answerTable index="+quesIndex+" skip="+set.skip+" type="+quesObj.typekind+" ><tr><td align=left>"+set.leftdesc+"</td><td align=center>"+set.middledesc+
	               "</td><td align=right>"+set.rightdesc+"</td></tr><tr><td colspan=3 id="+boxid+"></td></tr>";
	      
	      Ext.widget('sliderfield',{id:boxid+"_comp",width:300,value:answer.score,
			    minValue:Ext.Number.from(parseInt(set.minscore),1),
   				maxValue:Ext.Number.from(parseInt(set.maxscore),100),useTips:{style:'border:1px #c5c5c5 solid;background-color:white'}
   		   });
   		   this.keyArray.push(boxid);  
	
	    optStr+="</table>";
		if(set.extrainput=='true'){
	        optStr+=this.getExtraInput(quesObj);
	    }
	    return optStr;
	},
	getScoreAnswer:function(table,typekind){
		var items = table.getElementsByTagName("input"),
			Qid_ = table.id.replace("answerTable",""),
			scoreCmp = Ext.getCmp(Qid_+"Box_comp"),
			answerObj = {empty:true};
		answerObj[Qid_+"1"]= scoreCmp.getValue();
		answerObj.empty = false;
		var desc = Ext.getDom(Qid_+"desc");
		if(desc){
		    answerObj[Qid_+"desc"]= desc.value;
		    if(desc.getAttribute('required')=='true' && desc.value.length<1)
		          answerObj.empty = true;
		}  
		return answerObj;
	},
	//矩阵打分
	matrixScoreQuestion:function(quesObj,quesIndex){
		var set = quesObj.set,
		    answer = quesObj.answer||{desc:''},
	        optStr="<table id=Q"+quesObj.questionid+"_answerTable index="+quesIndex+" skip="+set.skip+" type="+quesObj.typekind+"><tr><td></td><td align=left>"+set.leftdesc+"</td><td align=center>"+set.middledesc+
	               "</td><td align=right>"+set.rightdesc+"</td></tr>",
	        optionList = quesObj.optionList;
	        
	        for(var i=0;i<optionList.length;i++){
	        		var boxid = "Q"+quesObj.questionid+"_"+optionList[i].optid+"_Box";
	        		optStr+="<tr><td>"+optionList[i].optlongname+"</td><td colspan=3 id="+boxid+"></td></tr>";
	        		var value = answer[optionList[i].optid]?parseInt(answer[optionList[i].optid]):parseInt(set.minscore);
	        		Ext.widget('sliderfield',{id:boxid+"_comp",width:300,optid:optionList[i].optid,
	        		    value:value,
			    		minValue:Ext.Number.from(parseInt(set.minscore),1),
   					maxValue:Ext.Number.from(parseInt(set.maxscore),100),useTips:{style:'border:1px #c5c5c5 solid;background-color:white'}
   		   		});
   		   		this.keyArray.push(boxid);  
	        }
	      
   		   
	
	    optStr+="</table>";
		if(set.extrainput=='true'){
	        optStr+=this.getExtraInput(quesObj);
	    }
	    return optStr;
	
	},
	getMatrixScoreAnswer:function(table,typekind,answerObj){
	    var Qid = table.id.replace("_answerTable",""),
	        answerObj = {empty:true};
	    var desc = Ext.getDom(Qid+"_desc"),descValue = '',descValid = true;
		if(desc){
		    descValue = desc.value;
		    if(desc.getAttribute('required')=='true' && descValue.length<1)
		          descValid = false;
		}
	    var answers = new Array();
	    for(var i=1;i<table.rows.length;i++){
	       var tdId = table.rows[i].cells[1].id;
	       var scoreCmp = Ext.getCmp(tdId+"_comp");
	       answers.push({itemid:Qid.replace("Q",""),optid:scoreCmp.optid,score:scoreCmp.getValue(),C_desc:descValue});
	    }
	    answerObj[Qid] = answers;
	    answerObj.empty = !descValid;
	    return answerObj;
	},
	//量表
	scaleQuestion:function(quesObj,quesIndex){
		var set = quesObj.set,levelList = set.levels,inputType='radio',answer = quesObj.answer||{desc:''};
		
		var optStr = "<table id=Q"+quesObj.questionid+"_answerTable index="+quesIndex+" skip="+set.skip+" type="+quesObj.typekind+">";
		//排列方式
		if(set.type=='1'){
		   for(var i=0;i<levelList.length;i++){
		     var lev = levelList[i];
		     var checked = parseInt(answer.score)==parseInt(lev.score)?'checked="true"':'';
		   	 optStr+="<tr><td width=30><input score="+lev.score+" type="+inputType+" name='Q"+quesObj.questionid+"' "+checked+"/></td><td>"+lev.text+"</td></tr>";
		   }
		}else if(set.type=='2'){
			optStr+="<tr>";
		   for(var i=0;i<levelList.length;i++){
		     var lev = levelList[i];
		     var checked = answer.score+""==lev.score?'checked="true"':'';
		   	 optStr+="<td ><input score="+lev.score+" type="+inputType+" name='Q"+quesObj.questionid+"' "+checked+"/>"+lev.text+"</td>";
		   } 
		   optStr+="</tr>"; 
		}else if(set.type=='4' && set.column.length>0){
			
		    var column = set.column;
		    column = column>0?column:1;
		    for(var i=0;i<levelList.length;i++){
		        if(i==0)
		        		optStr+="<tr>";
		        else if(i%column==0)
		        		optStr+="</tr><tr>";
		        
		        var lev = levelList[i];
		        var checked = answer.score+""==lev.score?'checked="true"':'';
			    optStr+="<td ><input score="+lev.score+" type="+inputType+" name='Q"+quesObj.questionid+"' "+checked+"/>"+lev.text+"</td>";
		    }
		    optStr+="</tr>";
		    //下拉框方式
		}else if(set.type=='3'){
			optStr+= "<tr><td><select>";
			for(var i=0;i<levelList.length;i++){
				var lev = levelList[i];
				optStr+= " <option value =\""+lev.score+"\">"+lev.text+"</option>";
			}
			optStr+= "</select></td></tr>";
		}
		
		optStr+="</table>";
		if(set.extrainput=='true'){
	        optStr+=this.getExtraInput(quesObj);
	    }
	    
		return optStr;
	    
	},
	getScaleAnswer:function(table,typekind){
	    var items = table.getElementsByTagName("input"),
			Qid_ = table.id.replace("answerTable",""),
			answerObj = {empty:true};
		for(var i=0;i<items.length;i++){
		    if(items[i].checked){
		        answerObj.empty = false;
		        answerObj[Qid_+"1"]= items[i].getAttribute("score");
		        break;
		    }
		}
		var desc = Ext.getDom(Qid_+"desc");
		if(desc){
		    answerObj[Qid_+"desc"]= desc.value;
		    if(desc.getAttribute('required')=='true' && desc.value.length<1)
		          answerObj.empty = true;
		}
		//下拉框方式获取值
		var selects = table.getElementsByTagName("select");
		if(selects.length>0){
			answerObj.empty = false;
			answerObj[Qid_+"1"] = selects[0].value;
		}
		return answerObj;
	},
	//矩阵量表
	matrixScaleQuestion:function(quesObj,quesIndex){
		var set = quesObj.set,levelList = set.levels,
			optionList = quesObj.optionList,inputType='radio',
		    answer = quesObj.answer||{desc:''};
	    
	    var optStr="<table id=Q"+quesObj.questionid+"_answerTable index="+quesIndex+" skip="+set.skip+" type="+quesObj.typekind+" border=1 cellpadding=5 style='border-collapse: collapse;' bordercolor='#c5c5c5'>";
	    for(var i=-1;i<optionList.length;i++){
	    		optStr+="<tr>";
	    		var opt;
	    		if(i==-1){
	           optStr+="<td></td>";
	        }else{
	        		opt = optionList[i];
	        		optStr+="<td style='max-width:250px;word-break:break-all;'>"+opt.optlongname+"</td>";
	        }
	        for(var k=0;k<levelList.length;k++){
	           var lev = levelList[k];
	           if(i==-1){
	               var text = lev.text;
	               if(set.showscore=='true')
	                   text+="("+lev.score+"分）";
	               optStr+="<td align=center style='max-width:150px;word-break:break-all;'>"+text+"</td>";
	               continue;
	           }
	        	   var checked = parseInt(answer[opt.optid])==parseInt(lev.score)?'checked="true"':'';
	        	   optStr+="<td align=center><input optid="+opt.optid+" score="+lev.score+" name=Q"+quesObj.questionid+"_"+opt.optid+" type='"+inputType+"' "+checked+"/></td>";
	        
	        }
	        optStr+="</tr>";
	    }
	    optStr+="</table>";
	    
	    if(set.extrainput=='true'){
	        optStr+=this.getExtraInput(quesObj);
	    }
	    return optStr;
	
	},
	
	getMatrixScaleAnswer:function(table,typekind){
	    var Qid = table.id.replace("_answerTable",""),answerObj = {empty:true};
	    var desc = Ext.getDom(Qid+"_desc"),descValue = '',descValid = true;
		if(desc){
		    descValue = desc.value;
		    if(desc.getAttribute('required')=='true' && descValue.length<1)
		          descValid = false;
		}
	    var answers = new Array();
	    var items = table.getElementsByTagName("input");
	    for(var i=0;i<items.length;i++){
	        if(items[i].checked){
	           answers.push({itemid:Qid.replace("Q",""),optid:items[i].getAttribute("optid"),score:items[i].getAttribute("score"),C_desc:descValue});
	        }
	    }
	    answerObj.empty = answers.length>0 && descValid?false:true;   
	    answerObj[Qid] = answers;
	    
	    return answerObj;
	},
	/**
	 * 随机排列数组元素
	 * @param {} a
	 * @param {} b
	 * @return {}
	 */
	randomsort:function(a,b){
		return Math.random()>.5 ? -1 : 1;
	},
	
	//创建试题补充说明 textarea
	getExtraInput:function(quesObj){
	    var set = quesObj.set,fillStr = quesObj.answer&&quesObj.answer.desc?quesObj.answer.desc:'',
	        required = set.skip=='true'?'fasle':set.required,
	    		mustFill = required=='true'?'(<span style="color:red">必填</span>)':'';
	    return "<table cellpadding=0><tr><td>说明"+mustFill+"<br/>"+
	    		   "<textarea required="+required+" id=Q"+quesObj.questionid+"_desc style='font-size:14px;resize: none;' rows=5 cols=42>"+
	    		   fillStr+"</textarea></td></tr></table>";
	},
	
	//从后台加载试卷数据
	previewTemplatedata:function(){
		var me = this;
		if(me.suerveyid==""||me.suerveyid=='undefined'||me.suerveyid==null){//预览
		    this.viewModel = "preview";
			var vo = new HashMap();
		    	vo.put("qnid", me.qnId);
	    	    vo.put("mainObject", me.mainObject);
	    	    vo.put("subObject", me.subObject);
	    	    Rpc({functionId:'QN40000001',success:function(res){
		    		    var resultObj = Ext.decode(res.responseText);
		    		    var quesObj = eval("("+resultObj.jsonobject+")");
		    		    this.saveflag = false;
		    		    me.createQuestionnaire(quesObj);
	    	     }},vo);
		}else{
			this.viewModel = "submit";
			var drcodeurl ="/module/system/questionnaire/template/AnswerQn.jsp"+ "?suerveyid=" + me.suerveyid;
			Ext.Ajax.request({
				url : '/servlet/SearchAnswerTemplateServlet',
				params : {
						 planid :me.suerveyid,
						 forwordurl:drcodeurl,
						 cip:me.cip,
						 mainObject:me.mainObject,
						 subObject:me.subObject
				},
				success : function(res) {
						  var data = Ext.decode(res.responseText);
						  if(data.success=='false'){
						  	if(data.idlogin==1){
						  		window.location.href=rootPath+'/templates/index/hcmlogon.jsp';
						  	}else{
						  		Ext.Msg.alert("提示信息",data.errormessage,function(){
									if(me.mainObject!=''&&me.subObject!=''){
										Ext.callback(me.callback,me.scope,['0']); //0是失败，1是成功
									}else{
										top.window.opener = top;
										top.window.open('','_self','');
										top.window.close();
									}
								});
						  	}
						  }
						var qnset = data.qnset;
						me.planId  = data.planId;
						me.viewtype = data.viewtype;
						me.saveflag= data.saveflag;
						me.finishmsg = data.finishmsg;
						me.advanceendmsg = data.advanceendmsg;
						me.qnId = data.qnid;
						me.autoclosevalue = 0;
						me.autocloseselected = 0;
						me.searchanswer = 0;
						if(qnset!=null){
							me.searchanswer = qnset.searchanswer;
							me.autoclosevalue = qnset.autoclosevalue;
							me.autocloseselected = qnset.autocloseselected;
						}
						if(data.success=='true')//为false时 不应加载后面的内容 changxy  20160825
					      me.createQuestionnaire(data);
				  }   
			});
		}
	},
	
	onDestroy:function(){
	    //销毁问卷对象的时候 连带销毁问卷内的组件
		for(var i=0;i<this.keyArrayCache.length;i++){
		    Ext.getCmp(this.keyArrayCache[i]+"_comp").destroy();
		}
		this.callParent(arguments);
	}


});