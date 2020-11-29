
/**
 * guodd 2015-08-20
 * 调查问卷-创建试题
 */
Ext.define("QuestionnaireTemplate.QuestionItem",{
	extend:"Ext.Component",
	xtype:'questionitem',
	style:'border:#c5c5c5 1px solid',
	requires:["QuestionnaireTemplate.LabelEditor","EHR.extWidget.proxy.TransactionProxy","SYSF.FileUpLoad"],
	imagePrefix:rootPath+'/module/system/questionnaire/images/',
	
	/**
	 * 试题类型
	 */
	questionType:undefined,
	/**
	 * 试题设置参数
	 */
	questionSet:undefined,
	/**
	 * 试题数据对象
	 */
	questionObj:undefined,
	
	/**
	 * 图片选择题 图片保存路径
	 */
	imageSavePath:undefined,
	
	//height:50,
	border:1,
	
	isTool:false,
	isChecked:false,
	//参数设置窗口
	setWindow:undefined,
	pureTextSet:undefined,
	//render后调用
	doEndComp:function(){
	     if(!this.keyArray)
	          return;
         for(var i=0;i<this.keyArray.length;i++){
               Ext.getCmp(this.keyArray[i]).render(this.keyArray[i]+"_qescmpbox");
         }
    },
	
	//关键值数组
	keyArray:undefined,
	
	listeners:{
		/*afterlayout:function(){
			 this.suspendEvent("afterlayout");
		     if(8<this.questionType && this.questionType<12)
		         return;
		     this.resetHeight();
		     
		},*/
		render:function(){
			var me = this;
			me.getEl().on('click',function(){
				if(me.isChecked==true){
					me.isChecked = false;
					me.removeCls('questionCheck');
					var indexEle = Ext.getDom(me.id+"_quesNo");
					if(indexEle)
						indexEle.style.backgroundImage= "url("+me.imagePrefix+"backtitleid.png)";
				} else {
					var items = me.ownerCt.query('questionitem[isChecked=true]');
					for ( var i = 0; i < items.length; i++) {
						items[i].isChecked = false;
						items[i].removeCls('questionCheck');
						var indexEle = Ext.getDom(items[i].id+"_quesNo");
						if(indexEle)
							indexEle.style.backgroundImage= "url("+me.imagePrefix+"backtitleid.png)";
					}
					me.isChecked = true;
					me.addCls('questionCheck');
					var indexEle = Ext.getDom(me.id+"_quesNo");
					if(indexEle)
						indexEle.style.backgroundImage= "url("+me.imagePrefix+"backtitleid_checked.png)";
				}
			});
		}
	},
	
	/**
	 * 初始化控件
	 */
	initComponent:function(){
		this.callParent();
		this.pureTextSet = {};
		//开始生成试题
		this.beginCreateQuestionItem();
	},
	
	
	
	/**
	 * 开始生成试题
	 */
	beginCreateQuestionItem:function(){
		
		if(this.questionObj){
			this.questionType = this.questionObj.typekind;
			this.questionSet = this.questionObj.set;
		}else
		   this.initQuestionSet();
		
		if(this.questionType==9 || this.questionType==10 || this.questionType==11){
			//this.setBodyStyle('padding','10px');
			//设置布局方式
			//this.setLayout(Ext.create("Ext.layout.container.VBox",{align:'stretch'}));
			
			//通过判断questionType生成试题
			this.checkTypeToDoSomething(1);
			
		}else{
			//this.setHeight(200);
			this.initStruts();
			//生成左侧工具按钮
			this.createTool();
			//通过判断questionType生成试题
			this.checkTypeToDoSomething(1);
			this.innerHtml = this.innerHtml.replace("${toolContainer}","");
			this.innerHtml = this.innerHtml.replace("${titleContainer}","");
			this.innerHtml = this.innerHtml.replace("${itemContainer}","");
			this.innerHtml = this.innerHtml.replace("${buttonContainer}","");
			this.setHtml(this.innerHtml);
			
		}
		
		
	},
	
	//初始化结构
	initStruts:function(){
	    this.innerHtml = 
	    "<table  border=0 cellpadding=0 cellspacing=0 extra='mainTable' width='100%'>"+
	    "<tr>"+
	    "<td  height='200' width='30' align='center' valign='top' style='border-right:1px solid #c5c5c5;'>${toolContainer}</td>"+
	    "<td align='left' valign='top' style='padding:10px'>"+
	    "<table width='100%' cellpadding=0 cellspacing=0  style='table-layout:fixed;padding-bottom:10px'>"+
	    "<tr><td style='padding-bottom:10px;'>${titleContainer}</td></tr>"+
	    "<tr><td style='overflow-x:auto;'>${itemContainer}</td></tr>"+
	    "<tr><td style='padding:5px 5px 5px 0px;'>${buttonContainer}</td></tr>"+
	    "</table>"+
	    "</td>"+
	    "</tr>"+
	    "</table>";
	},
	
	/**
	 * 中转方法，根据questionType和forward 判断需要做什么
	 * @param forward
	 * 1:创建试题
	 * 2:创建参数配置window
	 * 3:获取试题参数配置
	 * 4:获取试题具体信息
	 * @param 附加参数
	 */
	checkTypeToDoSomething:function(forward,options){
		
		var returnParam = undefined; 
		//questionType ：试题类型
		switch(this.questionType){
			case 1://单选题
				if(forward==1)
					returnParam = this.createSelectQuestion(1,options);
				else if(forward==2)
					returnParam = this.singleSelectWindow(options);
				else if(forward==3)
					returnParam = this.saveSelectSet(options);
				else if(forward==4)
					returnParam = this.getSelectQuestionObj(options);
				break;
			case 2://多选题
				if(forward==1)
					returnParam = this.createSelectQuestion(2,options);
				else if(forward==2)
					returnParam = this.multiSelectWindow(options);
				else if(forward==3)
					returnParam = this.saveSelectSet(options);
				else if(forward==4)
					returnParam = this.getSelectQuestionObj(options);
				break;
			case 3://单行填空题
				if(forward==1)
					returnParam = this.createFillBlankQuestion(options);
				else if(forward==2)
					returnParam = this.fillBlankWindow(options);
				else if(forward==3)
					returnParam = this.saveFillBlankSet(options);
				else if(forward==4)
					returnParam = this.getFillBlankQuestionObj(options);
				break;
			case 4://多项填空题
				if(forward==1)
					returnParam = this.createFillBlankQuestion(options);
				else if(forward==2)
					returnParam = this.fillBlankWindow(options);
				else if(forward==3)
					returnParam = this.saveFillBlankSet(options);
				else if(forward==4)
					returnParam = this.getFillBlankQuestionObj(options);
				break;
			case 5: //图片单选题	
				if(forward==1)
					returnParam = this.createImageSelectQuestion(options);
				else if(forward==2)
					returnParam = this.imageSelectWindow(options);
				else if(forward==3)
					returnParam = this.saveImageSelectSet(options);
				else if(forward==4)
					returnParam = this.getImageSelectQuestionObj(options);
				break;
			case 6: //图片多选题	
				if(forward==1)
					returnParam = this.createImageSelectQuestion(options);
				else if(forward==2)
					returnParam = this.imageSelectWindow(options);
				else if(forward==3)
					returnParam = this.saveImageSelectSet(options);
				else if(forward==4)
					returnParam = this.getImageSelectQuestionObj(options);
				break;
			case 7://矩阵单选题
				if(forward==1)
					returnParam = this.createMatrixSelectQuestion(options);
				else if(forward==2)
					returnParam = this.maxtrixSelectWindow(options);
				else if(forward==3)
					returnParam = this.saveMaxtrixSelectSet(options);
				else if(forward==4)
					returnParam = this.getMaxtrixSelectQuestionObj(options);
				break;
			case 8://矩阵多选题
				if(forward==1)
					returnParam = this.createMatrixSelectQuestion(options);
				else if(forward==2)
					returnParam = this.maxtrixSelectWindow(options);
				else if(forward==3)
					returnParam = this.saveMaxtrixSelectSet(options);
				else if(forward==4)
					returnParam = this.getMaxtrixSelectQuestionObj(options);
				break;
			case 9://描述
				this.isTool = true;
				if(forward==1)
					returnParam = this.createDescriptionComponent(options);
				else if(forward==4)
					returnParam = this.getToolCompObj(options);
				break;
			case 10:// 分页
				this.isTool = true;
				if(forward==1)
					returnParam = this.createPageComponent(options);
				else if(forward==4)
					returnParam = this.getToolCompObj(options);
				break;
			case 11:// 分隔符
				this.isTool = true;
				if(forward==1)
					returnParam = this.createSplitComponent(options);
				else if(forward==4)
					returnParam = this.getToolCompObj(options);
				break;
			case 12://打分题
				if(forward==1)
					returnParam = this.createScoreQuestion(options);
				else if(forward==2)
					returnParam = this.scoreWindow(options);
				else if(forward==3)
					returnParam = this.saveScoreSet(options);
				else if(forward==4)
					returnParam = this.getScoreQuestionObj(options);
				break;
			case 13://量表题
				if(forward==1)
					returnParam = this.createScaleQuestion(options);
				else if(forward==2)
					returnParam = this.scaleWindow(options);
				else if(forward==3)
					returnParam = this.saveScaleSet(options);
				else if(forward==4)
					returnParam = this.getScaleQuestionObj(options);
				break;
			case 14://矩阵打分题
				if(forward==1)
					returnParam = this.createMatrixScoreQuestion(options);
				else if(forward==2)
					returnParam = this.scoreWindow(options);
				else if(forward==3)
					returnParam = this.saveMaxtrixScoreSet(options);
				else if(forward==4)
					returnParam = this.getMaxtrixScoreQuestionObj(options);
				break;
			case 15://矩阵量表题
				if(forward==1)
					returnParam = this.createMatrixScaleQuestion(options);
				else if(forward==2)
					returnParam = this.matrixScaleWindow(options);
				else if(forward==3)
					returnParam = this.saveMaxtrixScaleSet(options);
				else if(forward==4)
					returnParam = this.getMaxtrixScaleQuestionObj(options);
				break;
		}
		
		return returnParam;
		
	},
	
	/* 初始化试题设置    */
	initQuestionSet:function(){
		this.questionSet = {};
		this.questionSet.skip='true';
	    
		//questionType ：试题类型
		switch(this.questionType){
			case 1://单选题
				this.questionSet.type='1';
				this.questionSet.column='1';
				this.questionSet.random='false';
				break;
			case 2://多选题
				this.questionSet.type='1';
				this.questionSet.column='1';
				this.questionSet.random='false';
				this.questionSet.maxselect='0';
				this.questionSet.minselect='0';
				break;
			case 3://单行填空题
				this.questionSet.inputtype='1'; // 答案类型：字符型
				this.questionSet.inputwidth='200';// 输入框长度：200px
				this.questionSet.inputheight='20';//输入框高度：20px
				this.questionSet.length='20'; // 输入长度 20
				this.questionSet.limit='1'; //是否限制输入长度（只针对文本型：inputtype==5的时候）
				this.questionSet.codeset='';//代码型（inputtype==4）时选择的代码类
				this.questionSet.decimal='0';//小数位数（inputtype==2数值型）
				break;
			case 4://多项填空题
				this.questionSet.inputtype='1'; // 答案类型：字符型
				this.questionSet.inputwidth='200';// 输入框长度：200px
				this.questionSet.inputheight='20';//输入框高度：20px
				this.questionSet.length='20'; // 输入长度 20
				this.questionSet.limit='1'; //是否限制输入长度（只针对文本型：inputtype==5的时候）
				this.questionSet.codeset='';//代码型（inputtype==4）时选择的代码类
				this.questionSet.decimal='0';//小数位数（inputtype==2数值型）
				break;
			case 5: //图片单选题	
				this.questionSet.random='false';
				break;
			case 6: //图片多选题	
				this.questionSet.random='false';
				this.questionSet.maxselect='0';
				this.questionSet.minselect="0";
				break;
			case 7://矩阵单选题
				this.questionSet.random = 'false';
				break;
			case 8://矩阵多选题
				this.questionSet.random = 'false';
				break;
			case 12://打分题
				this.questionSet.minscore='1';
				this.questionSet.maxscore='100';
				this.questionSet.leftdesc=QN.template.lowLevel;
				this.questionSet.middledesc=QN.template.middleLevel;
				this.questionSet.rightdesc=QN.template.hightLevel;
				this.questionSet.extrainput='false';
				this.questionSet.required='false';
				break;
			case 13://量表题
				this.questionSet.type='1';
				this.questionSet.column='1';
				this.questionSet.levels=[{score:'10',text:QN.template.questionOptionText,longname:QN.template.questionOptionText}];
				this.questionSet.extrainput='false';
				this.questionSet.required='false';
				break;
			case 14://矩阵打分题
				this.questionSet.minscore='0';
				this.questionSet.maxscore='100';
				this.questionSet.leftdesc=QN.template.lowLevel;
				this.questionSet.middledesc=QN.template.middleLevel;
				this.questionSet.rightdesc=QN.template.hightLevel;
				this.questionSet.extrainput='false';
				this.questionSet.required='false';
				break;
			case 15://矩阵量表题
				this.questionSet.extrainput = 'false';
				this.questionSet.required = 'false';
				this.questionSet.levels=[{score:'10',text:QN.template.questionOptionText,longname:QN.template.questionOptionText}];
				break;
		}
	},
	
	
	
	
	
	newChildId:function(){
	     return Ext.id(null,this.id+"_qc");
	},
	
	/**
	 * 创建左侧菜单
	 */
	createTool:function(){
		if(this.questionType==10 || this.questionType==11 || this.questionType==9)
			return;
	    
	    var me = this,
			itemConvert = undefined,
			copy = false,
		    prefix = me.imagePrefix;
		
		if(me.questionType==1 || me.questionType==5)
			itemConvert = QN.template.singleToMulti;
		else if(me.questionType==2 || me.questionType==6)
			itemConvert = QN.template.multiToSingle;
		
	    if(me.questionType==7 || me.questionType==8 || me.questionType==14 || me.questionType==15)
			copy = true;
			
		var toolHtml = "<table cellpadding=0 width=34>"+
		"<tr><td id='"+this.id+"_quesNo' height=30 style='background:url("+this.imagePrefix+"backtitleid.png) no-repeat center;color:white;font-size:12px' align='center' valign='middle'>"+
		"</td><tr>"+
	    "<tr><td align='center' valign='middle' ><img func='set'  style='cursor:pointer' title='"+common.button.set+"' src='"+this.imagePrefix+"settings.png'/></td><tr>"+
	    "<tr><td align='center' valign='middle'><img func='pre' style='cursor:pointer' title='"+common.button.previous+"' src='"+this.imagePrefix+"lastquestion.png'/></td><tr>"+
	    "<tr><td align='center' valign='middle'><img func='next' style='cursor:pointer' title='"+common.button.next+"' src='"+this.imagePrefix+"nextquestion.png'/></td><tr>";
	    
	    if(itemConvert){
	    	toolHtml+="<tr><td align='center' valign='middle'><img func='conv' style='cursor:pointer' title='"+itemConvert+"' src='"+this.imagePrefix+"transform.png'/></td><tr>";
	    }
	    
	    if(copy){
	    	toolHtml+="<tr><td align='center' valign='middle'><img func='copy' style='cursor:pointer' title='"+common.button.copy+"' src='"+this.imagePrefix+"u428.png'/></td><tr>";
		}
		
		toolHtml+="<tr><td align='center' valign='middle'><img func='remove' style='cursor:pointer' title='"+common.button.todelete+"' src='"+this.imagePrefix+"closebutton.png'/></td><tr>";
		toolHtml+="</table>";
	    
	    this.innerHtml = this.innerHtml.replace("${toolContainer}",toolHtml);
	},
	
	//初始化事件监听
	onRender:function(){
	    this.callParent(arguments);
	    var root = this.getEl().dom;
	    if(this.questionType==9 || this.questionType==10 || this.questionType==11){
	    	    //添加文字可以编辑监听
	        var texts = Ext.query("span[edittype]",true,root);
	        for(var i=0;i<texts.length;i++){
	        	texts[i].onclick=this.textEditHandler.bind(this);
	        }
	        this.doEndComp();
	    	    return;
	    }
	    //添加左工具按钮监听
	    //var root = this.getEl().dom;
	    var imgs = Ext.query('img[func]',true,root);
        for(var i=0;i<imgs.length;i++){
             imgs[i].onclick=this.toolHandler.bind(this);
        }	
        
        //添加文字可以编辑监听
        var texts = Ext.query("span[edittype]",true,root);
        for(var i=0;i<texts.length;i++){
        	texts[i].onclick=this.textEditHandler.bind(this);
        }
        
        //添加功能按钮监听
        var buttons = Ext.query("a[btn]",true,root);
        for(var i=0;i<buttons.length;i++){
            buttons[i].onclick=this.btnHandler.bind(this);
        }
        
        //计算题号
        var index = this.ownerCt.items.indexOf(this);
		var tools =  this.ownerCt.query('questionitem[isTool=true]');
		    	 	var num = index;
		    	 	for(var i=0;i<tools.length;i++){
		    	 		if(this.ownerCt.items.indexOf(tools[i])<index)
		    	 			num--;
		    	 	}
        Ext.get(this.id+"_quesNo").setHtml("Q"+(num+1));
        
		this.doEndComp();
	},
	
	
	//工具栏按钮监听
	toolHandler:function(e){
	  var func;
	   if(e)
	     func =e.target.getAttribute("func");
	   else
	     func = event.srcElement.getAttribute("func");
	   if(func=='set'){
	     this.showSetWindow();
	   }else if(func=='pre'){
	      var scrollY = this.ownerCt.ownerCt.getScrollY();
	      var index = this.ownerCt.items.indexOf(this);
		  if(index==0)
				return;
		  this.ownerCt.move(index,index-1);
		  this.ownerCt.ownerCt.setScrollY(scrollY);
		  this.orderQuestionNumber(this.ownerCt);
	   }else if(func=='next'){
	   	  var scrollY = this.ownerCt.ownerCt.getScrollY();
	      var index = this.ownerCt.items.indexOf(this);
			if(index==this.ownerCt.items.getCount()-1)
						  return;
			this.ownerCt.move(index,index+1);
			this.ownerCt.ownerCt.setScrollY(scrollY);
			this.orderQuestionNumber(this.ownerCt);
	   }else if(func=='conv'){
	       var selectType = 'radio';
	   		if(this.questionType==1){
				this.questionType=2;
				selectType = 'checkbox';
			}else if(this.questionType==2)
				this.questionType=1;
		    else if(this.questionType==5){
		        this.questionType=6;
		        selectType = 'checkbox';
		    }else if(this.questionType==6)
		        this.questionType=5;
	       var selects =  document.getElementsByName(this.id+"_select");
	       for(var i=0;i<selects.length;i++){
	           var selectDom = selects[i];
	           var replaceDom = document.createElement("input");
	           replaceDom.setAttribute("type",selectType);
	           replaceDom.name = this.id+"_select";
	           selectDom.parentNode.replaceChild(replaceDom,selectDom);
	       }
	       this.initQuestionSet();
	   }else if(func=='copy'){
	   		this.copyMatrix();
	   }else if(func=='remove'){
	        var me = this;
	        Ext.Msg.confirm(QN.template.messageTitle,QN.template.deleteMessage,function(btn){
					  		if(btn=='yes'){
					  		    var scrollY = me.ownerCt.ownerCt.getScrollY();
								var parent = me.ownerCt;
								parent.remove(me,true);
								parent.ownerCt.setScrollY(scrollY);
								me.orderQuestionNumber(parent);
								me.destroy();
							}
			});
	   }
	},
	
	//点击文字编辑
	textEditHandler:function(e){
		var textEle,me = this;
		if(e)
		  textEle = e.target;
	    else
	      textEle = event.srcElement;
	      
	    var tdom = Ext.get(textEle);
	    if(!textEle.getAttribute("pureText")){
	        tdom = tdom.up("span[pureText]");
	    }
	    var edittype = tdom.getAttribute("edittype");
	    var position = tdom.getTrueXY();
	    var editorMenuConfig = edittype=='text'&&this.questionType!=9?this.getEditorMenuConfig(tdom):{};
	    var labelEditor = Ext.widget('labeleditor',{
		    text:me.pureTextSet[tdom.id],
		    longText:tdom.getHtml(),
		    connDom: tdom,
		    cls:'font14',
		    width:tdom.getWidth(),
		    height:tdom.getHeight(),
		    menuConfig:editorMenuConfig,
		    floating:true,
	        x:position[0],
	        y:position[1],
	        renderTo:document.body,
	        listeners:{
	            afterrender:function(){
	                this.beEdit();
	            },
	            completeedit:function(){
	                 me.pureTextSet[this.connDom.id] = this.text;
	                 this.longText = this.longText.replace(/ /g, "&nbsp;");//xiegh 20170701   bug:20363  此行代码意思 是将N个空格转成一个N个&nbsp;
	                 this.connDom.setHtml(this.longText);
	                 me.resetHeight();
	                 me.ownerCt.ownerCt.setScrollY(me.getLocalY());
	                 this.destroy();
	            }
	        }
	        
	    });
	    
	},
	
	getEditorMenuConfig:function(eleDom){
	     var menuConfig;
	     if(this.questionType==5 || this.questionType==6){
	        menuConfig = {
	             left:function(label){
	                   var table = label.connDom.up('table');
	                   var td = table.dom.parentNode;
	                   if(td.cellIndex==0)
	                       return;
	                   
	                   var pretd = td.parentNode.cells[td.cellIndex-1];
	                   td.parentNode.insertBefore(td,pretd);
	             	   var position = label.connDom.getTrueXY();
		              label.setPosition(position);
	             },
	             right:function(label){
	                 var table = label.connDom.up('table');
	                   var td = table.dom.parentNode;
	                   if(td.cellIndex==td.parentNode.cells.length-2)
	                       return;
	                   var nexttd = td.parentNode.cells[td.cellIndex+2];
	                   td.parentNode.insertBefore(td,nexttd);
	                       
	                   var position = label.connDom.getTrueXY();
		              label.setPosition(position);    
	             
	             },
	             remove:function(label){
	             	var td = label.connDom.up('table').dom.parentNode;
	             	td.parentNode.deleteCell(td.cellIndex);
	             	label.destroy();
	             },
	             scope:this
	        };
	     }else if(this.questionType==7 || this.questionType==8){
	          if(eleDom.dom.parentNode.parentNode.rowIndex==0)
	              menuConfig = {
	                     left:function(label){
			                   var table = label.connDom.up('table');
			                   var td = label.connDom.dom.parentNode;
			                   if(td.cellIndex==1)
			                       return;
			                   
			                   var pretd = td.parentNode.cells[td.cellIndex-1];
			                   td.parentNode.insertBefore(td,pretd);
			             	   var position = label.connDom.getTrueXY();
				              label.setPosition(position);
			             },
			             right:function(label){
			                 var table = label.connDom.up('table');
			                   var td = label.connDom.dom.parentNode;
			                   if(td.cellIndex==td.parentNode.cells.length-1)
			                       return;
			                   var nexttd = td.parentNode.cells[td.cellIndex+2];
			                   
			                   if(nexttd)
			                   		td.parentNode.insertBefore(td,nexttd);
			                   else
			                        td.parentNode.appendChild(td);
			                       
			                   var position = label.connDom.getTrueXY();
				              label.setPosition(position);    
			             
			             },
			             remove:function(label){
			             	var cellIndex = label.connDom.dom.parentNode.cellIndex;
			             	var table = label.connDom.up('table').dom;
			             	for(var i=0;i<table.rows.length;i++){
			             	    table.rows[i].deleteCell(cellIndex);
			             	}
			             	label.destroy();
			             },
			             scope:this
	              };
	          else 
	          	menuConfig={
		             up:function(label){
			              var table = label.connDom.up('table');
			              var tr = label.connDom.up("tr");
			              var rowIndex = tr.dom.rowIndex;
			              var tbody = label.connDom.up("tbody");
			              if(tr.dom.rowIndex==1)
			                  return;
			                  
			              var uprow = table.dom.rows[rowIndex-1];
			              tbody.dom.insertBefore(tr.dom,uprow);
			              
			              var position = label.connDom.getTrueXY();
			              label.setPosition(position);
			     
			        },
			        down:function(label){
			             var table = label.connDom.up('table');
			              var tr = label.connDom.up("tr");
			              var rowIndex = tr.dom.rowIndex;
			              var tbody = label.connDom.up("tbody");
			              if(tr.dom.rowIndex==table.dom.rows.length-1)
			                  return;
			              var downrow = table.dom.rows[rowIndex+2];
			              if(downrow)
			                tbody.dom.insertBefore(tr.dom,downrow);
			              else
			                 tbody.dom.appendChild(tr.dom);
			              
			               var position = label.connDom.getTrueXY();
			              label.setPosition(position);
			             
			        },
			        remove:function(label){
			             var table = label.connDom.up('table');
			             var tr = label.connDom.up("tr");
			             table.dom.deleteRow(tr.dom.rowIndex);
			             label.destroy();
			             this.resetHeight();
			        },
			        scope:this
		    };
	     
	     }else{
	     	menuConfig={
		        up:function(label){
		              var table = label.connDom.up('table');
		              var tr = label.connDom.up("tr");
		              var rowIndex = tr.dom.rowIndex;
		              var tbody = label.connDom.up("tbody");
		              if(tr.dom.rowIndex==0)
		                  return;
		                  
		              var uprow = table.dom.rows[rowIndex-1];
		              if(uprow.getAttribute("flag")=="top"){
		                  return;
		              }
		              tbody.dom.insertBefore(tr.dom,uprow);
		              
		              var position = label.connDom.getTrueXY();
		              label.setPosition(position);
		     
		        },
		        down:function(label){
		             var table = label.connDom.up('table');
		              var tr = label.connDom.up("tr");
		              var rowIndex = tr.dom.rowIndex;
		              var tbody = label.connDom.up("tbody");
		              if(tr.dom.rowIndex==table.dom.rows.length-1)
		                  return;
		              var downrow = table.dom.rows[rowIndex+2];
		              if(downrow)
		                tbody.dom.insertBefore(tr.dom,downrow);
		              else
		                 tbody.dom.appendChild(tr.dom);
		              
		               var position = label.connDom.getTrueXY();
		              label.setPosition(position);
		             
		        },
		        remove:function(label){
		             var table = label.connDom.up('table');
		             var tr = label.connDom.up("tr");
		             table.dom.deleteRow(tr.dom.rowIndex);
		             label.destroy();
		             this.resetHeight();
		        },
		        scope:this
		    };
	     }
	
	     return menuConfig;
	},
	
	btnHandler:function(e){
		var ele,scrollY = this.ownerCt.ownerCt.getScrollY();
	    if(e)
		    ele = e.target;
		else
		    ele = event.srcElement;
		    
		if(this.questionType==1 || this.questionType==2){
		      this.selectQuestionBtn(ele);
		}else if(this.questionType==4){
			  this.fillBlankQuestionBtn(ele);
		}else if(this.questionType==7 || this.questionType==8){
		      this.MatrixSelectOrScaleQuestionBtn(ele);
		}else if(this.questionType==14){
		      this.MatrixScoreQuestionBtn(ele);
		      this.ownerCt.ownerCt.setScrollY(scrollY);
		}else if(this.questionType==15){
			  this.MatrixSelectOrScaleQuestionBtn(ele);
			  this.ownerCt.ownerCt.setScrollY(scrollY);
		}
		
	},
	
	MatrixSelectOrScaleQuestionBtn:function(ele){
	    var btnType = ele.getAttribute("btn"),selectType=this.questionType==7 || this.questionType==15?'radio':'checkbox';
	    var scrollY = this.ownerCt.ownerCt.getScrollY();
	    var addHandler = function(value){
		    var itemTable = Ext.getDom(this.id+"_itemTable");
		    if(!value)
					value = QN.template.questionOptionText;
			var textValues = value.split("\n");
			for(var i=0;i<textValues.length;i++){
				if(textValues[i].replace(/\s/g,'').length<1)
							     continue;
			   var row =  itemTable.insertRow();
			   var cell = row.insertCell();
			   cell.align='center';
			   cell.valign='middle';
			   cell.setAttribute("class","matrixTd");
			   var span = this.newESpan('e',textValues[i],textValues[i],'text');
			   cell.appendChild(span);
			   cell.setAttribute("nowrap","nowrap");
			   
			   for(var j=0;j<itemTable.rows[0].cells.length-1;j++){
				   cell = row.insertCell();
				   cell.align='center';
				   cell.valign='middle';
				   cell.setAttribute("class","matrixTd");
				   var input = document.createElement("input");
				   input.name = this.id+"_select";
				   input.setAttribute("type",selectType);
				   cell.appendChild(input);
			   }
			}
			
			var table = Ext.query("table[extra=mainTable]",true,this.getEl().dom);
       		this.setHeight(table[0].scrollHeight);  
       		this.ownerCt.ownerCt.setScrollY(scrollY);
	    };
	    
	    if(btnType=='batchadd'){
		      this.batchAdd(addHandler,this);
		} else if(btnType=='add'){
		      Ext.callback(addHandler,this);
		} else{
		     var itemTable = Ext.getDom(this.id+"_itemTable");
		     if(itemTable.rows[0].cells.length==11){
		         Ext.showAlert("最多只能添加10个选项！");
		         return;
		     }
		     var cell = itemTable.rows[0].insertCell();
		     cell.align='center';
			 cell.valign='middle';
			 cell.setAttribute("class","matrixTd");
		     cell.appendChild(this.newESpan('e',QN.template.questionOptionText,QN.template.questionOptionText,'text'));
		     
		     for(var i=1;i<itemTable.rows.length;i++){
		          
		          cell = itemTable.rows[i].insertCell();
		          cell.align='center';
				  cell.valign='middle';
				  cell.setAttribute("class","matrixTd");
				   var input = document.createElement("input");
				   input.name = this.id+"_select";
				   input.setAttribute("type",selectType);
				   cell.appendChild(input);
		     }
		
			this.ownerCt.ownerCt.setScrollY(scrollY);
		}
	
	
	},
	fillBlankQuestionBtn:function(ele){
		var btnType = ele.getAttribute("btn");
		var addHandler=function(value){
		          var me = this;
		          var scrollY = this.ownerCt.ownerCt.getScrollY();
		          var itemTable = Ext.getDom(this.id+"_itemTable");
		      		if(!value)
							value = QN.template.questionOptionText;
				  var rows = value.split("\n");
			      for(var i=0;i<rows.length;i++){
			      	if(rows[i].replace(/\s/g,'').length<1)
							     continue;
					   var tr = itemTable.insertRow();
					   var textTd = tr.insertCell();
					   var span = this.newESpan('e',rows[i],rows[i],'text');
					   textTd.appendChild(span);
					   textTd.setAttribute("nowrap","nowrap");
					   var cmpTd = tr.insertCell();
					   
			           
			           var cmpConf;
			            if(!me.questionSet){
			                cmpConf = {xtype:'textfield'};
					    }else if(me.questionSet.inputtype==1){
					    	cmpConf = {xtype:'textfield',width:Ext.Number.from(me.questionSet.inputwidth,150)};
				        }else if(me.questionSet.inputtype==2){
				        	cmpConf = {xtype:'numberfield',width:Ext.Number.from(me.questionSet.inputwidth,150)};
				        }else if(me.questionSet.inputtype==3)
				        	cmpConf = {xtype:'datefield',width:Ext.Number.from(me.questionSet.inputwidth,150)};
				        else if(me.questionSet.inputtype==4){
				        	    Ext.require("EHR.extWidget.field.CodeTreeCombox",function(){
				        	    	cmpConf = {xtype:'codecomboxfield',codesetid:me.questionSet.codeset,width:Ext.Number.from(me.questionSet.inputwidth,150)};
				        	    });
				        }else if(me.questionSet.inputtype==5){
				        	cmpConf = {xtype:'textarea',width:Ext.Number.from(me.questionSet.inputwidth,150),height:me.questionSet.inputheight*13};
				        }
				        
				        var sid = me.newChildId();
				        cmpConf.id = sid;
				        var s = Ext.widget(cmpConf);
				        
				        cmpTd.id = sid+"_qescmpbox";
				        s.render(sid+"_qescmpbox");
			      }
			      
			        
			    	var table = Ext.query("table[extra=mainTable]",true,this.getEl().dom);
       				this.setHeight(table[0].scrollHeight);  
       				this.ownerCt.ownerCt.setScrollY(scrollY);
		      };
		         
		      if(btnType=='batchadd'){
		         this.batchAdd(addHandler,this);
		      } else{
		          Ext.callback(addHandler,this);
		      }
	
	},
	
	MatrixScoreQuestionBtn:function(){
	      var itemTable = Ext.getDom(this.id+"_itemTable");
	      var tr = document.createElement("tr");
		  var td = document.createElement("td");
		  td.setAttribute("nowrap","nowrap");
		  var span = this.newESpan('e',QN.template.questionOptionText,QN.template.questionOptionText,'text');
		  td.appendChild(span);
		  tr.appendChild(td);
		  var sid = this.newChildId();
		  var s = Ext.widget('sliderfield',{width:300,value:0,minValue:0,id:sid,
    				maxValue:100,useTips:{style:'border:1px #c5c5c5 solid;background-color:white'}
    			});
		  td = document.createElement("td");
		  td.id=sid+"_qescmpbox";
		  td.setAttribute("colspan","3");
		  tr.appendChild(td);
		  itemTable.getElementsByTagName("tbody")[0].appendChild(tr);
	      s.render(sid+"_qescmpbox");
	      
	      var table = Ext.query("table[extra=mainTable]",true,this.getEl().dom);
       	  this.setHeight(table[0].scrollHeight);  
       	  
	},
	
	selectQuestionBtn:function(ele){
		var btnType,selectType=this.questionType==1?'radio':'checkbox';
		       
		btnType = ele.getAttribute("btn");
		         
		      var addHandler=function(value){
		          var scrollY = this.ownerCt.ownerCt.getScrollY();
		          var itemTable = Ext.getDom(this.id+"_itemTable");//Ext.query("table[flag=qitem]",true,this.getEl().dom)[0];
		      		if(!value)
							value = QN.template.questionOptionText;
				     var rows = value.split("\n");
			      for(var i=0;i<rows.length;i++){
			      	if(rows[i].replace(/\s/g,'').length<1)
							     continue;
			           var tr = itemTable.insertRow();
			           var td = tr.insertCell();
			           td.setAttribute("nowrap","nowrap");
			           var input = document.createElement("input");
			           input.name = this.id+"_select";
			           input.setAttribute("type",selectType);
			           
			           var span = this.newESpan('e',rows[i],rows[i],'text');
			           td.appendChild(input);
			           td.appendChild(span);
			      }
			      
			        
			    	var table = Ext.query("table[extra=mainTable]",true,this.getEl().dom);
       				this.setHeight(table[0].scrollHeight);  
       				this.ownerCt.ownerCt.setScrollY(scrollY);
		      };
		         
		      if(btnType=='batchadd'){
		         this.batchAdd(addHandler,this);
		      } else{
		          Ext.callback(addHandler,this);
		      }
	
	},
	
	resetHeight:function(){
		var table = Ext.query("table[extra=mainTable]",true,this.getEl().dom);
	    if(this.getHeight()!=table[0].scrollHeight){
	    		this.setHeight(table[0].scrollHeight);
	    }
	},
	
	/**
	 * 收集试题参数设置
	 */
	getQuestionSet:function(){
		this.checkTypeToDoSomething(3);
	},
	
	/**
	 * 获取试题对象
	 */
	getQuestionObject:function(){
		return this.checkTypeToDoSomething(4);
	},
	
	/**
	 * 创建参数设置窗口
	 */
	showSetWindow:function(){
		var me = this;
		me.setWindow = Ext.widget("window",{
			title:QN.template.questionSet,
			modal:true,
			bodyStyle:'background-color:white',
			layout:'fit',
			resizable: false,
			height:300,
			width:300
		});
		me.checkTypeToDoSomething(2,me.setWindow);
		
		me.setWindow.show();
	},
	
	
	/**
	 * 批量添加window
	 * @param callback
	 * @param scope
	 */
	batchAdd:function(callback,scope){
		Ext.widget("window",{
			 title:QN.template.batchAddTitle,
			 height:400,
			 border:false,
			 modal:true,
			 width:400,
			 layout:{
				 type:'vbox',
				 align:'stretch'
			 },
			 bodyStyle:'background-color:white',
			 items:[{xtype:'label',text:QN.template.batchAddTip},{xtype:'textarea',style:'margin-bottom:2px;',flex:10}],
			 buttonAlign:'center',
			 buttons:[{text:common.button.ok,handler:function(){
				var value =  this.ownerCt.ownerCt.child('textarea').getValue();
				this.ownerCt.ownerCt.close();
				Ext.callback(callback,scope,[value]);
			 }},{text:common.button.cancel,handler:function(){this.ownerCt.ownerCt.close();}}]
		 }).show();
	},
	
	
	
///////////////////////////////////////单选、多选题///////////////////////////////////	
	
	//生成选择题（单选或多选）
	/**
	 * 生成选择题（单选或多选）
	 * @param type
	 * 1:单选
	 * 2:多选
	 */
	createSelectQuestion:function(type){
	    var selectType="radio",title,longTitle,titleHtml="",itemHtml="<table id='"+this.id+"_itemTable'>";
	    
	     if(type==2)
	    	 	selectType = 'checkbox';
	    	 	
		if(selectType== 'radio'){
	      title = QN.template.selectTypeTitle;
	    }
	    if(selectType=='checkbox'){
	      title = QN.template.selectTypesTitle;
	    }
	    if(this.questionObj){
	    	    title = this.questionObj.name;
	    	    longTitle = this.questionObj.longname;
	    	    var list = this.questionObj.optionList;
	    	    
	    	    for(var i=0;i<list.length;i++){
	    	    	itemHtml+="<tr><td valign='middle' nowrap=nowrap><input name='"+this.id+"_select' type='"+selectType+"'>"+
	    	    	                this.newESpan('s',list[i].optlongname,list[i].optname,'text')+"</td></tr>";
	    	    }
	     }else{
	    	 	itemHtml+="<tr><td valign='middle' nowrap=nowrap><input name='"+this.id+"_select' type='"+selectType+"'>"+
	    	 			         this.newESpan('s',QN.template.questionOptionText,QN.template.questionOptionText,'text')+"</td></tr>";
	     }
	     itemHtml+="</table>";
	     
	     longTitle= longTitle?longTitle:title;
	     this.innerHtml = this.innerHtml.replace("${titleContainer}",this.newESpan('s',longTitle,title,'title',this.id+"_title"));
	     this.innerHtml = this.innerHtml.replace("${itemContainer}",itemHtml);
	     
	     this.innerHtml = this.innerHtml.replace("${buttonContainer}","<a href='###' btn='add' style='font-size:14px'>添加</a>&nbsp;&nbsp;&nbsp;<a href='###' btn='batchadd' style='font-size:14px'>批量添加</a>");
	},
	
	/**
	 * 单选题参数设置
	 * @param setWindow
	 * 
	 * 参数格式：
	 * { 
		skip:'true',  // true/false 是否可以跳过不答
   		type：'1'竖排 '2'横排 '3'下拉 '4'按列>column:列数
		column:'3',
		random: 'true'/'false' 是否随机排列
		}
	 */
	singleSelectWindow:function(setWindow){
		var me = this;
		var layoutSet = Ext.widget('form',{
			bodyBorder:false,
			border:false,
			layout:{
				type:'vbox',
				align:'stretch'
			},
			style:'padding:5px;',
			items:[{xtype:'label',text:QN.template.questionLayout,style:'margin-bottom:10px'},{
				xtype:'container',
				layout:{type:'table',columns:2,tableAttrs:{width:'100%'},tdAttrs:{style:'padding-left:20px;',width:'50%'} },
				items:[{xtype:'radio',boxLabel:QN.template.vArrangeItem,name:'type',inputValue:'1',value:1},
				       {xtype:'radio',boxLabel:QN.template.hArrangeItem,name:'type',inputValue:'2'},
				       {xtype:'radio',boxLabel:QN.template.selectItem,name:'type',inputValue:'3'},
				       {xtype:'container',layout:'hbox',items:[{xtype:'radio',boxLabel:QN.template.byColumn,style:'margin-bottom:10px',name:'type',inputValue:'4'},{xtype:'numberfield',name:'column',width:40,step:1,value:1,minValue:1,maxValue:10}]}]
			},{xtype:'container',html:'<hr/>'},
			{xtype:'checkbox',name:'random',boxLabel:QN.template.randomItem,style:'padding-left:20px;',inputValue:'true',uncheckedValue:'false'},
			{xtype:'checkbox',name:'skip',boxLabel:QN.template.canBeSkip,style:'padding-left:20px;',inputValue:'true',uncheckedValue:'false'},
			{
				border:0,
				style:'padding-top:20px;',
				width:'100%',
				buttons:[{
					formBind:true,
					text:common.button.ok,
					handler:function(){
						me.getQuestionSet();
						me.setWindow.close();
					}
				},{
					text:common.button.cancel,
					handler:function(){
						me.setWindow.close();
					}
				}],
				buttonAlign:'center'
			}]
		});
		if(this.questionSet){
			layoutSet.child("checkbox[name=random]").setValue(this.questionSet.random);
			layoutSet.child("checkbox[name=skip]").setValue(this.questionSet.skip);
			var typeRadio = layoutSet.query("radio[inputValue="+this.questionSet.type+"]");
			if(typeRadio.length>0)
				typeRadio[0].setValue(true);
			var columnComp = layoutSet.query("numberfield");
			if(columnComp.length>0)
				columnComp[0].setValue(this.questionSet.column);
		}
		setWindow.setHeight(260);
		setWindow.add(layoutSet);
	},
	
	/**
	 * 多选题参数设置
	 * @param setWindow
	 * 参数格式：
	 * set:{ 
				skip:'true'/'false' 是否可以跳过不答
       			type：1竖排 2横排 4按列>column:列数
				column:”3”
   		    		random: 'true'/'false' 是否随机排列
		  		maxselect:3, 最多选几项
		  		minselect:4  最少选几项
			}
	 */
	multiSelectWindow:function(setWindow){
		var me = this;
		var layoutSet = Ext.widget('form',{
			bodyBorder:false,
			border:false,
			layout:{
				type:'vbox',
				align:'stretch'
			},
			style:'padding:5px;',
			items:[{xtype:'label',text:QN.template.configLayout,style:'margin-bottom:10px'},{
				xtype:'container',
				layout:{type:'table',columns:2,tableAttrs:{width:'100%'},tdAttrs:{style:'padding-left:20px;',width:'50%'} },
				items:[{xtype:'radio',boxLabel:QN.template.vArrangeItem,name:'type',inputValue:'1',value:1},
				       {xtype:'radio',boxLabel:QN.template.hArrangeItem,name:'type',inputValue:'2'},
				       {xtype:'container',layout:'hbox',items:[{xtype:'radio',boxLabel:QN.template.byColumn,style:'margin-right:10px',name:'type',inputValue:'4'},
				       {xtype:'numberfield',name:'column',width:40,step:1,value:1,allowBlank:false,minValue:1,maxValue:10}]}]
			},{xtype:'container',html:'<hr/>'},
			{xtype:'label',style:'color:red',style:'padding-bottom:2px',text:'* 0=不控制'},
			{xtype:'numberfield',fieldLabel:QN.template.minSelect,minValue:0,maxValue:10,value:0,allowBlank:false,labelWidth:50,name:'minselect'},
			{xtype:'numberfield',fieldLabel:QN.template.maxSelect,minValue:0,maxValue:10,value:0,allowBlank:false,labelWidth:50,name:'maxselect'},
			{xtype:'checkbox',name:'random',boxLabel:QN.template.randomItem,style:'padding-left:20px;',inputValue:'true',uncheckedValue:'false'},
			{xtype:'checkbox',name:'skip',boxLabel:QN.template.canBeSkip,style:'padding-left:20px;',inputValue:'true',uncheckedValue:'false'},
			{
				border:0,
				width:'100%',
				buttons:[{
					formBind:true,
					text:common.button.ok,
					handler:function(){
						me.getQuestionSet();
						me.setWindow.close();
					}
				},{
					text:common.button.cancel,
					handler:function(){
						me.setWindow.close();
					}
				}],
				buttonAlign:'center'
			}]
		});
		
		if(this.questionSet){
			layoutSet.child("checkbox[name=random]").setValue(this.questionSet.random);
			layoutSet.child("checkbox[name=skip]").setValue(this.questionSet.skip);
			var typeRadio = layoutSet.query("radio[inputValue="+this.questionSet.type+"]");
			if(typeRadio.length>0)
				typeRadio[0].setValue(true);
			var columnComp = layoutSet.query("numberfield[name=column]");
			if(columnComp.length>0)
				columnComp[0].setValue(this.questionSet.column);
			var maxComp = layoutSet.query("numberfield[name=maxselect]");
			if(maxComp.length>0)
				maxComp[0].setValue(this.questionSet.maxselect);
			var minComp = layoutSet.query("numberfield[name=minselect]");
			if(minComp.length>0)
				minComp[0].setValue(this.questionSet.minselect);
		}
		setWindow.setHeight(315);
		setWindow.add(layoutSet);
	},
	
	
	
	saveSelectSet:function(){
		this.questionSet = this.setWindow.child("form").getValues(false,false,true,false);
	},
	getSelectQuestionObj:function(){
		var questionObj = {};
		var titleEle = Ext.getDom(this.id+"_title");
		questionObj.name = this.pureTextSet[titleEle.id];
		questionObj.longname = titleEle.innerHTML;
		questionObj.typekind = this.questionType;
		
		var items = Ext.getDom(this.id+"_itemTable").getElementsByTagName("td");
		
		questionObj.optionList=[];
		for(var i=0;i<items.length;i++){
		     var span = items[i].getElementsByTagName("span")[0];
		     questionObj.optionList.push({optname:this.pureTextSet[span.id],optlongname:span.innerHTML,orders:i});
		}
		
		questionObj.set = this.questionSet;
		return questionObj;
		
	},
	
	
///////////////////////////////////////填空题和多项填空题 ///////////////////////////////////
	//生成填空题
	createFillBlankQuestion:function(){
	    
	    var me = this;
	    me.keyArray=[];
	    var longTitle;
	    var title = longTitle = me.questionType==3?QN.template.insertTitle:QN.template.insertsTitle;
	    
	    if(me.questionObj){
	    	 title = me.questionObj.name;
	    	 longTitle = me.questionObj.longname;
	    }

        this.innerHtml = this.innerHtml.replace("${titleContainer}",this.newESpan('s',longTitle,title,'title',this.id+"_title"));
        
        var itemHtml = "<table id='"+this.id+"_itemTable'>";

        var cmpConf;
            if(!me.questionSet){
                cmpConf = {xtype:'textfield'};
		    }else if(me.questionSet.inputtype==1){
		    	cmpConf = {xtype:'textfield',width:Ext.Number.from(me.questionSet.inputwidth)};
	        }else if(me.questionSet.inputtype==2){
	        	cmpConf = {xtype:'numberfield',width:Ext.Number.from(me.questionSet.inputwidth)};
	        }else if(me.questionSet.inputtype==3)
	        	cmpConf = {xtype:'datefield',width:Ext.Number.from(me.questionSet.inputwidth)};
	        else if(me.questionSet.inputtype==4){
	        	    Ext.require("EHR.extWidget.field.CodeTreeCombox",function(){
	        	    	cmpConf = {xtype:'codecomboxfield',codesetid:me.questionSet.codeset,width:Ext.Number.from(me.questionSet.inputwidth)};
	        	    });
	        }else if(me.questionSet.inputtype==5){
	        	cmpConf = {xtype:'textarea',width:Ext.Number.from(me.questionSet.inputwidth),height:me.questionSet.inputheight*13};
	        }
        
        
        
        
        if(me.questionType==3){
            var sid = me.newChildId();
            cmpConf.id = sid;
            var s = Ext.widget(cmpConf);
            itemHtml+="<tr><td id='"+sid+"_qescmpbox'></td></tr></table>";
            me.innerHtml = me.innerHtml.replace("${itemContainer}",itemHtml);
            me.keyArray.push(sid);
            return;
        }
        
        
        
        if(me.questionObj){
	    	    var list = me.questionObj.optionList;
	    	    for(var i=0;i<list.length;i++){
	    	    	 var sid = me.newChildId();
            		cmpConf.id = sid;
	    	        var s = Ext.widget(cmpConf);
	    	        itemHtml+="<tr><td norwrap>"+me.newESpan('s',list[i].optlongname,list[i].optname,'text')+"</td><td id='"+sid+"_qescmpbox'></td></tr>";
	    	        me.keyArray.push(sid);
	    	    }
	    	    itemHtml+="</table>";
	     }else{
		         var sid = me.newChildId();
	             cmpConf.id = sid;
	    		 var s = Ext.widget(cmpConf);;
			     itemHtml+="<tr><td norwrap>"+me.newESpan('s',QN.template.questionOptionText,QN.template.questionOptionText,'text')+"</td><td id='"+sid+"_qescmpbox'></td></tr></table>";
	    	     me.keyArray.push(sid);
	     }
        
        
         me.innerHtml = me.innerHtml.replace("${itemContainer}",itemHtml);
         me.innerHtml = me.innerHtml.replace("${buttonContainer}","<a href='###' btn='add' style='font-size:14px'>添加</a>&nbsp;&nbsp;&nbsp;<a href='###' btn='batchadd' style='font-size:14px'>批量添加</a>");
  
	},
	//填空题参数设置window
	fillBlankWindow:function(setWindow){
		var me = this;
		var inputtypeArray = [{value:'1',name:QN.template.String},{value:'2',name:QN.template.Number},
						      {value:'3',name:QN.template.Date},{value:'4',name:QN.template.Code}];
						      
		if(this.questionType==3)
			inputtypeArray.push({value:'5',name:QN.template.Text});	      
						      
		var form = Ext.widget("form",{
			bodyBorder:false,
			border:false,
			layout:{
				type:'vbox',
				align:'left'
			},
			style:'padding:5px',
			items:[{
				xtype:'container',
				style:'margin:15px 0px 10px 30px',
				layout:'hbox',
				items:[{xtype:'numberfield',name:'inputwidth',labelWidth:65,width:170,
						minValue:0,maxValue:1000,allowBlank:false,fieldLabel:QN.template.inputWidth,labelAlign:'right',value:150},
				       {xtype:'label',text:QN.template.Char}]
			},{
				xtype:'combo',
				style:'margin:0px 0px 10px 30px',
				fieldLabel:QN.template.dataType,
				labelAlign:'right',
				displayField:'name',
				labelWidth:65,
				width:170,
				valueField:'value',
				name:'inputtype',
				editable:false,
				value:'1',
				store:{
					fields:['value','name'],
					data:inputtypeArray
				},
				listeners:{
					change:function(combo,newValue){
						var configComp = combo.up('form').queryById('inputTypeConfig');
						configComp.removeAll(true);
						if(newValue=='1'){
							configComp.add({
								xtype:'numberfield',name:"length",labelAlign:'right',labelWidth:65,
								minValue:0,value:20,allowBlank:false,fieldLabel:QN.template.length,width:170});
						}else if(newValue=='2'){
							configComp.add([
							    {xtype:'numberfield',name:"length",labelAlign:'right',labelWidth:65,
							    	minValue:0,value:8,allowBlank:false,fieldLabel:QN.template.intLength,width:170},
							    {xtype:'numberfield',name:"decimal",style:'margin-top:10px',labelAlign:'right',labelWidth:65,
							    	minValue:0,value:0,allowBlank:false,fieldLabel:QN.template.decLength,width:170}
							]);
						}else if(newValue=='4'){
							configComp.add([{
								xtype:'combo',fieldLabel:QN.template.codesetid,labelWidth:65,width:170,labelAlign:'right',
								allowBlank:false,
								queryMode: 'local',
								displayField:'codesetdesc',
								valueField:'codesetid',
								name:'codeset',
								store:{
									fields:['codesetdesc','codesetid'],
									autoLoad:true,
									proxy:{
										type:'transaction',
										functionId:'QN30000003',
										reader:{
											type:'json',
											root:'codeSetList'
										}
									}
								}
							}]);
						}else if(newValue=='5'){
							configComp.add([{
								xtype:'container',
								layout:'hbox',
								style:'margin:0px 0px 10px 0px',
								items:[
									{xtype:'numberfield',name:"inputheight",labelAlign:'right',labelWidth:65,
										minValue:0,value:5,allowBlank:false,fieldLabel:QN.template.inputHeight,width:170},
									{xtype:'label',text:QN.template.textareaRow}
									]
							},{
								xtype:'container',layout:'hbox',
								style:'margin-left:-15px',
								items:[
								       {xtype:'checkbox',name:'limit', boxLabel:QN.template.inputCount+':',inputValue:'1',uncheckedValue:'2'},
								       {xtype:'numberfield',style:'margin-left:5px',minValue:0,width:100,name:'length'} //changxy
								       ]
							}]);
						}
					}
				}
			},{
				xtype:'container',
				itemId:'inputTypeConfig',
				style:'margin:0px 0px 10px 30px',
				minHeight:50,
				items:{xtype:'numberfield',name:"length",labelAlign:'right',minValue:0,value:20,allowBlank:false,labelWidth:65,fieldLabel:QN.template.length,width:170}
			},{
				xtype:'container',html:'<hr/>',width:'100%'
			},{
				xtype:'checkbox',name:'skip',boxLabel:QN.template.canBeSkip,style:'padding-left:50px',inputValue:'true',uncheckedValue:'false'
			},{
				border:0,
				style:'padding-top:20px',
				width:'100%',
				buttons:[{
					formBind:true,
					text:common.button.ok,
					handler:function(){
						me.getQuestionSet();
						me.setWindow.close();
					}
				},{
					text:common.button.cancel,
					handler:function(){
						me.setWindow.close();
					}
				}],
				buttonAlign:'center'
			}]
		});

		if(this.questionSet){

			var widthComp  = form.query("numberfield[name=inputwidth]")[0];
			widthComp.setValue(this.questionSet.inputwidth);
			form.child("combo[name=inputtype]").setValue(this.questionSet.inputtype);

			var lengthComp = form.query("numberfield[name=length]");
			if(lengthComp.length>0)
				lengthComp[0].setValue(this.questionSet.length);

			if(this.questionSet.inputtype=='2'){
				form.query("numberfield[name=decimal]")[0].setValue(this.questionSet.decimal);
			}else if(this.questionSet.inputtype=='4')
				form.query("combo[name=codeset]")[0].setValue(this.questionSet.codeset);
			else if(this.questionSet.inputtype=='5'){
				form.query("numberfield[name=inputheight]")[0].setValue(this.questionSet.inputheight);
				form.query("checkbox[name=limit]")[0].setValue(this.questionSet.limit);
			}

			form.child("checkbox[name=skip]").setValue(this.questionSet.skip);
		}
		
		setWindow.add(form);
	},
	//收集填空题 参数设置
	saveFillBlankSet:function(){
	    var me = this;
		me.questionSet = this.setWindow.child("form").getValues(false,false,true,false);
       
        var table = Ext.getDom(this.id+"_itemTable");
        if(table.rows.length==0)
            return;
            
        var cmpConf;
		if(!me.questionSet){
			                cmpConf = {xtype:'textfield'};
		}else if(me.questionSet.inputtype==1){
					    	cmpConf = {xtype:'textfield',width:Ext.Number.from(me.questionSet.inputwidth,150)};
		}else if(me.questionSet.inputtype==2){
				        	cmpConf = {xtype:'numberfield',width:Ext.Number.from(me.questionSet.inputwidth,150)};
		}else if(me.questionSet.inputtype==3)
				        	cmpConf = {xtype:'datefield',width:Ext.Number.from(me.questionSet.inputwidth,150)};
		else if(me.questionSet.inputtype==4){
				        	    Ext.require("EHR.extWidget.field.CodeTreeCombox",function(){
				        	    	cmpConf = {xtype:'codecomboxfield',codesetid:me.questionSet.codeset,width:Ext.Number.from(me.questionSet.inputwidth,150)};
				        	    });
		}else if(me.questionSet.inputtype==5){
				        	cmpConf = {xtype:'textarea',width:Ext.Number.from(me.questionSet.inputwidth,150),height:me.questionSet.inputheight*13};
		}
        
        
        if(me.questionType==3){
        	  var sid = me.newChildId();
              cmpConf.id = sid;
              var s = Ext.widget(cmpConf);
              Ext.getCmp(table.rows[0].cells[0].id.replace("_qescmpbox","")).destroy();
              table.rows[0].cells[0].id = sid+"_qescmpbox";
              s.render(sid+"_qescmpbox");
              this.resetHeight();
              return;
        }
            
        for(var i=0;i<table.rows.length;i++){
        	  var sid = me.newChildId();
              cmpConf.id = sid;
              var s = Ext.widget(cmpConf);
              Ext.getCmp(table.rows[i].cells[1].id.replace("_qescmpbox","")).destroy();
              table.rows[i].cells[1].id = sid+"_qescmpbox";
              s.render(sid+"_qescmpbox");
        }
		
	},
	
	//获取填空题 数据
	getFillBlankQuestionObj:function(){
		var questionObj = {};
		var titleEle = Ext.getDom(this.id+"_title");
		questionObj.name = this.pureTextSet[titleEle.id];
		questionObj.longname = titleEle.innerHTML;
		questionObj.typekind = this.questionType;
		
		questionObj.set = this.questionSet;
		
		if(this.questionType==4){//如果是多项填空题，获取填空项信息
			questionObj.optionList = new Array();
			var itemTable = Ext.getDom(this.id+"_itemTable");
			for(var i=0;i<itemTable.rows.length;i++){
				var span = itemTable.rows[i].cells[0].getElementsByTagName("span")[0];
				questionObj.optionList.push({optname:this.pureTextSet[span.id],optlongname:span.innerHTML,orders:i});
			}
		}
		return questionObj;
	},
	
///////////////////////////////图片选择题 ///////////////////////////
	createImageSelectQuestion:function(){
		if(!this.imageSavePath){
			throw new Error("图片选择题必须设置<图片保存路径(imageSavePath)>");
			return;
		}
		
		var me = this,
		    prefix = me.imagePrefix;
		var longTitle;
		var title = longTitle = me.questionType==5?QN.template.selectPriTitle:QN.template.selectPrisTitle;
		var selectXtype = me.questionType==5?'radio':'checkbox';
		if(me.questionObj){
	    	    title = me.questionObj.name;
	    	    longTitle = me.questionObj.longname;
	    }
	    
	    this.innerHtml = this.innerHtml.replace("${titleContainer}",this.newESpan('s',longTitle,title,'title',this.id+"_title"));
	     
	     
		var itemHtml = "<table id='"+this.id+"_itemTable' height=190><tr>";
		if(this.questionObj){
			var options = this.questionObj.optionList;
			 for(var i=0;i<options.length;i++){
			      itemHtml+="<td ><table ><tr><td>";
			      //xus 20/3/2 vfs改造
//			      itemHtml+="<img location='"+options[i].imgurl+"' src='/servlet/DisplayOleContent?filePath="+options[i].imgurl+"' width=120 height=160 style='border:1px solid #c5c5c5;'/> ";
			      itemHtml+="<img location='"+options[i].imgurl+"' src='/servlet/vfsservlet?fileid="+options[i].imgurl+"' width=120 height=160 style='border:1px solid #c5c5c5;'/> ";
			      itemHtml+="</td></tr><tr><td><input name='"+this.id+"_select' type='"+selectXtype+"'>"+me.newESpan('s',options[i].optlongname,options[i].optname,'text')+"</td></tr></table></td>";
			 
			 }
		}
		
		 itemHtml+="<td valign='top' style='padding-top:4px;'><div id='"+this.id+"_imgupload' style='border:1px dashed #c5c5c5;background:url("+prefix+"upload.png) no-repeat center;width:120px;height:160px;'></div></td></tr></table>";
		me.doEndComp = function(){
			Ext.widget("fileupload",{
  	   					upLoadType:3,
  	   					height:160,width:120,
  	   					buttonText:'',
  	   					fileExt:"*.jpg;*.jpeg;*.png;*.bmp",//添加对上传文件类型控制  取消.gif 图片的上传
  	   					isTempFile:false,
  	   					VfsFiletype:VfsFiletypeEnum.multimedia,
  	   					//xus 20/4/20 vfs改造 图片单选题手机端无法显示图片的问题
  	   					VfsModules:VfsModulesEnum.NOLOGIN,
  	   					VfsCategory:VfsCategoryEnum.other,
  	   					CategoryGuidKey:'',
  	   					renderTo:this.id+"_imgupload",
  	   					success:me.afterImageUpLoad,
  	   					callBackScope:this,
  	   					savePath:this.imageSavePath
  	   		});
		};
		
		me.innerHtml = me.innerHtml.replace("${itemContainer}",itemHtml);
		
	},
	afterImageUpLoad:function(value){
		var me = this;
		var selectXtype = 'radio';
	    if(this.questionType==6)
	    	 	selectXtype = 'checkbox';
	    if(value[0].msg){
	    	Ext.Msg.alert('提示信息',value[0].msg);
	    	return;
	    }else{
//    		var filePath = value[0].fullpath,
//			src = "/servlet/DisplayOleContent?filePath="+value[0].fullpath,
	    	//xus 20/3/2 vfs改造
    		var filePath = value[0].fileid,
			src = "/servlet/vfsservlet?fileid="+value[0].fileid,
			itemTable = Ext.getDom(this.id+"_itemTable"),
			optSize = itemTable.rows[0].cells.length,
			newOpt = itemTable.rows[0].insertCell(optSize-1),
			optTable = document.createElement("table"),
			row = optTable.insertRow(),
			cell = row.insertCell();
			cell.innerHTML="<img src='"+src+"' location='"+filePath+"' width=120 height=160 style='border:1px solid #c5c5c5;'/>";
			row = optTable.insertRow();
			cell = row.insertCell();
			var checker = document.createElement("input");
			checker.name = this.id+"_select";
			checker.setAttribute("type",selectXtype);
			cell.appendChild(checker);
			var span = this.newESpan('e',QN.template.questionOptionText,QN.template.questionOptionText,'text');
			cell.appendChild(span);
			newOpt.appendChild(optTable);
	    }
	},
	imageSelectWindow:function(setWindow){
		var me = this;
		var items = new Array();
		if(this.questionType)
		if(this.questionType==6){
			items.push(
					{xtype:'label',style:'color:red;padding:0px 0px 2px 20px',text:'* 0=不控制'},
					{xtype:'numberfield',style:'padding-left:20px;',minValue:0,maxValue:10,value:0,allowBlank:false,fieldLabel:QN.template.minSelect,labelWidth:50,width:200,name:'minselect'},
					{xtype:'numberfield',style:'padding-left:20px;',minValue:0,maxValue:10,value:0,allowBlank:false,fieldLabel:QN.template.maxSelect,labelWidth:50,width:200,name:'maxselect'}		
			);
		}
		items.push(
				{xtype:'checkbox',name:'random',boxLabel:QN.template.randomItem,style:'padding:20px 0px 0px 20px',inputValue:'true',uncheckedValue:'false'},
				{xtype:'checkbox',name:'skip',boxLabel:QN.template.canBeSkip,style:'padding-left:20px;',inputValue:'true',uncheckedValue:'false'}	
		);
		items.push({
				border:0,
				style:'padding-top:20px',
				width:'100%',
				buttons:[{
					formBind:true,
					text:common.button.ok,
					handler:function(){
						me.getQuestionSet();
						me.setWindow.close();
					}
				},{
					text:common.button.cancel,
					handler:function(){
						me.setWindow.close();
					}
				}],
				buttonAlign:'center'
			});
		var form = Ext.widget("form",{
			bodyBorder:false,
			border:false,
			layout:{
				type:'vbox'
			},
			style:'padding:20px 20px 0px 20px',
			items:items
		});
		
		if(this.questionSet){
			form.child("checkbox[name=random]").setValue(this.questionSet.random);
			form.child("checkbox[name=skip]").setValue(this.questionSet.skip);
			if(this.questionType==6){
				form.child("numberfield[name=minselect]").setValue(this.questionSet.minselect);
				form.child("numberfield[name=maxselect]").setValue(this.questionSet.maxselect);
			}
		}
		setWindow.setHeight(270);
		setWindow.add(form);
	},
	
	saveImageSelectSet:function(){
		this.questionSet = this.setWindow.child("form").getValues(false,false,true,false);
	},
	getImageSelectQuestionObj:function(){
		
		var questionObj = {typekind:this.questionType};
		var titleEle = Ext.getDom(this.id+"_title");
		questionObj.name = this.pureTextSet[titleEle.id];
		questionObj.longname = titleEle.innerHTML;
		
		var opts = Ext.getDom(this.id+"_itemTable").getElementsByTagName("table");
		
		questionObj.optionList=[];
		for(var i=0;i<opts.length;i++){
		     var span = opts[i].getElementsByTagName("span")[0];
		     var img = opts[i].getElementsByTagName("img")[0];
		     questionObj.optionList.push({optname:this.pureTextSet[span.id],optlongname:span.innerHTML,imgurl:img.getAttribute("location"),orders:i});
		}
		
		questionObj.set = this.questionSet;
		return questionObj;
	},
	//////////////////////矩阵单选题、矩阵多选题///////////////////////////////////////////////////////
	//创建 矩阵题panel
	createMatrixSelectQuestion:function(){
		//通过this.questionType 判断是单选还是多选  =7单选 =8多选
		var me = this;
		var longTitle;
		var title=longTitle = me.questionType==8?QN.template.selectJZTitle:QN.template.selectJZsTitle;
	    if(me.questionObj){
	    	    title = me.questionObj.name;
	    	    longTitle = me.questionObj.longname;
	    }
	    
	    this.innerHtml = this.innerHtml.replace("${titleContainer}",this.newESpan('s',longTitle,title,'title',this.id+"_title"));
	    
	    
	    var xtype = 'radio';
	    if(me.questionType==8)
	    	xtype="checkbox";
	    
	    var itemHtml = "<table id='"+this.id+"_itemTable' cellpadding='0' cellspacing='0' style='border-collapse:collapse;'><tr><td class='matrixTd'>&nbsp;</td>";
	    
	    
		var tableRows = "";
		var labelArray = new Array();
		if(me.questionObj){
			var levelList = me.questionObj.levelList;
			var collength = levelList.length;
			for ( var i = 0; i < collength; i++) {
			     itemHtml+="<td align='center' valign='middle' class='matrixTd'>"+this.newESpan('s',levelList[i].optlongname,levelList[i].optname,'text')+"</td>";
			}
			itemHtml+="</tr>";
			
			var optionList = me.questionObj.optionList;
			for ( var i = 0; i < optionList.length; i++) {
			    itemHtml+="<tr><td align='center' valign='middle' class='matrixTd'>"+this.newESpan('s',optionList[i].optlongname,optionList[i].optname,'text')+"</td>";
				var rowCols = "";
				for ( var j = 0; j < levelList.length; j++) {
					rowCols+="<td align='center' valign='middle' class='matrixTd'><input name='"+this.id+"_select'  type='"+xtype+"'/></td>";
				}
				itemHtml+=rowCols+"</tr>";
			}
		} else {
			
			itemHtml+="<td align='center' valign='middle' class='matrixTd'>"+this.newESpan('s',QN.template.questionOptionText,QN.template.questionOptionText,'text')+"</td></tr>"+
			                 "<tr><td align='center' valign='middle' class='matrixTd'>"+this.newESpan('s',QN.template.questionOptionText,QN.template.questionOptionText,'text')+
			                 "</td><td align='center' valign='middle' class='matrixTd'><input name='"+this.id+"_select' type='"+xtype+"' /></td></tr>";
		}
		
		itemHtml+="</table>";
		
		me.innerHtml = me.innerHtml.replace("${itemContainer}",itemHtml);
	   me.innerHtml = me.innerHtml.replace("${buttonContainer}","<a href='###' btn='add' style='font-size:14px'>"+QN.template.addRow+
	   "</a>&nbsp;&nbsp;&nbsp;<a href='###' btn='leveladd' style='font-size:14px'>"+QN.template.addOption+
	   "</a>&nbsp;&nbsp;&nbsp;<a href='###' btn='batchadd' style='font-size:14px'>"+QN.template.batchAdd+"</a>");
	},
	/**
	 * 生成矩阵题参数设置window
	 * @param setWindow 
	 * window已存在，在window中添加组件
	 */
	maxtrixSelectWindow:function(setWindow){
		var me = this;
		var items = new Array();
		items.push(
				{xtype:'checkbox',name:'random',boxLabel:QN.template.randomItem,style:'padding:20px 0px 0px 20px',inputValue:'true',uncheckedValue:'false'},
				{xtype:'checkbox',name:'skip',boxLabel:QN.template.canBeSkip,style:'padding-left:20px;',inputValue:'true',uncheckedValue:'false'}
		);
		items.push({
			border:0,
			style:'padding-top:20px',
			width:'100%',
			buttons:[{
				formBind:true,
				text:common.button.ok,
				handler:function(){
					me.getQuestionSet();
					me.setWindow.close();
				}
			},{
				text:common.button.cancel,
				handler:function(){
					me.setWindow.close();
				}
			}],
			buttonAlign:'center'
		});
		var form = Ext.widget("form",{
			bodyBorder:false,
			border:false,
			layout:{
				type:'vbox'
			},
			style:'padding:20px',
			items:items
		});
		
		if(this.questionSet){
			form.child("checkbox[name=random]").setValue(this.questionSet.random);
			form.child("checkbox[name=skip]").setValue(this.questionSet.skip);
		}
		
		setWindow.add(form);
		setWindow.setHeight(220);
	},
	/**
	 * 保存 参数设置
	 */
	saveMaxtrixSelectSet:function(){
		//  获取参数，存到this.questionSet中
		this.questionSet = this.setWindow.child("form").getValues(false,false,true,false);
	},
	/**
	 * 取得矩阵多（单）选题对象
	 */
	getMaxtrixSelectQuestionObj:function(){
	
		var questionObj = {};
		var titleEle = Ext.getDom(this.id+"_title");
		questionObj.name = this.pureTextSet[titleEle.id];
		questionObj.longname = titleEle.innerHTML;
		questionObj.typekind = this.questionType;
		questionObj.set = this.questionSet;
		
		var optionList = new Array();
		var levelList = new Array();
		
		var itemTable = Ext.getDom(this.id+"_itemTable");
		
		var levelRow = itemTable.rows[0];
		for(var i=1;i<levelRow.cells.length;i++){
		     var span = levelRow.cells[i].getElementsByTagName("span")[0];
		     levelList.push({optname:this.pureTextSet[span.id],optlongname:span.innerHTML,orders:i});
		}
		questionObj.levelList = levelList;
		
		for(var i=1;i<itemTable.rows.length;i++){
		    var span = itemTable.rows[i].cells[0].getElementsByTagName("span")[0];
		    optionList.push({optname:this.pureTextSet[span.id],optlongname:span.innerHTML,orders:i});
		}
		questionObj.optionList = optionList;
		
		return questionObj;
	},
	///////////////////////////////////// 描述 说明 ////////////////////////
	createDescriptionComponent:function(){
		var text = QN.template.descriptionText;
		var longText = QN.template.descriptionText;
		if(this.questionObj){
		    text = this.questionObj.name;
		    longText = this.questionObj.longname;
		}
		var span = this.newESpan('s',longText,text,'text');
	    var innerHtml = "<table width='100%' extra='mainTable' height=40><tr><td>"+span+
	                    "</td><td width=25 valign=middle><img style='cursor:pointer' src='"+this.imagePrefix+"closebutton.png'></td></tr></table>";
	    this.setHtml(innerHtml);
	    
	    this.doEndComp = function(){
	           var imgs = Ext.query("img",true,this.getEl().dom);
	           imgs[0].onclick = function(){this.destroy();}.bind(this);
	    };
	},
	//////////////////////////////////////分页标签 和 分割线 ////////////////////////
	//分页标签
	createPageComponent:function(){
		var me = this;
		
		 var innerHtml = "<table width='100%' extra='mainTable' height=40><tr><td align=center><"+QN.template.perpage+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+QN.template.nexpage+"></td><td width=25 valign=middle><img style='cursor:pointer' src='"+this.imagePrefix+"closebutton.png'></td></tr></table>";
	    this.setHtml(innerHtml);
	    
	    this.doEndComp = function(){
	           var imgs = Ext.query("img",true,this.getEl().dom);
	           imgs[0].onclick = function(){this.destroy();}.bind(this);
	    };
		
	},
	//分割线
	createSplitComponent:function(){
		var me = this;
		
		var innerHtml = "<table width='100%' extra='mainTable' height=40><tr><td align=center valign=middle><hr style='border:1px #c5c5c5 dashed;width:100%'/></td><td width=25 valign=middle><img style='cursor:pointer' src='"+this.imagePrefix+"closebutton.png'></td></tr></table>";
	    this.setHtml(innerHtml);
		this.doEndComp = function(){
	           var imgs = Ext.query("img",true,this.getEl().dom);
	           imgs[0].onclick = function(){this.destroy();}.bind(this);
	    };
	},
    
	getToolCompObj:function(){
		var questionObj = {typekind:this.questionType};
		if(this.questionType==9){
			//描述
			var ele = Ext.query("span",true,this.getEl().dom);
			
			questionObj.name = this.pureTextSet[ele[0].id];
			questionObj.longname = ele[0].innerHTML;
		}else if(this.questionType==10){
			//分页
			questionObj.name=QN.template.splitPage;
			questionObj.longname = QN.template.splitPage;
		}else if(this.questionType==11){
			//分隔符
			questionObj.name=QN.template.splitChar;
			questionObj.longname = QN.template.splitChar;
		}
		return questionObj;
	},
	
	//////////////////////////打分题///////////////////////////////
	createScoreQuestion:function(){
		var me = this,
		         title = QN.template.storTitle,
		         longTitle = QN.template.storTitle,
		         itemHtml="<table id='"+this.id+"_itemTable'>";
		var score = 0;
		var displayDescriptor = "none";
		if(me.questionSet&&me.questionSet.extrainput=='true')
			displayDescriptor = "block";
		var keyArray = [];
		if(me.questionObj){
				title = me.questionObj.name;
				longTitle = me.questionObj.longname?me.questionObj.longname:title;longTitle
				
			    itemHtml+='<tr><td class="matrixTd" width="100px" style="border:none"  align="left" valign="middle">'+me.questionSet.leftdesc+'</td>'+
			                     '<td class="matrixTd" width="100px" style="border:none" align="center" valign="middle">'+me.questionSet.middledesc+'</td>'+
			                     '<td class="matrixTd" width="100px" style="border:none" align="right" valign="middle">'+me.questionSet.rightdesc+'</td></tr>';
			    
			    var sid = me.newChildId();
				var s = Ext.widget('sliderfield',{id:sid,width:300,value:score,minValue:Ext.Number.from(me.questionSet.minscore,1),
    					maxValue:Ext.Number.from(me.questionSet.maxscore,100),useTips:{style:'border:1px #c5c5c5 solid;background-color:white'}
    				});
			    itemHtml+="<tr><td colspan=3 id='"+sid+"_qescmpbox' ></td></tr>";
			    keyArray.push(sid);
		}else{
				itemHtml+='<tr><td class="matrixTd" width="100px" style="border:none" align="left" valign="middle">'+QN.template.lowLevel+'</td>'+
			                     '<td class="matrixTd" width="100px" style="border:none" align="center" valign="middle">'+QN.template.middleLevel+'</td>'+
			                     '<td class="matrixTd" width="100px" style="border:none" align="right" valign="middle">'+QN.template.hightLevel+'</td></tr>';
			    var sid = me.newChildId();
			   	var s = Ext.widget('sliderfield',{id:sid,width:300,value:score,minValue:0,
	    				maxValue:100,useTips:{style:'border:1px #c5c5c5 solid;background-color:white'}
	    			});
			    itemHtml+="<tr><td colspan=3 id='"+sid+"_qescmpbox' width=310></td></tr>";
			    keyArray.push(sid);
		}
		
		itemHtml+="</table>";
		itemHtml+="<div id='"+me.id+"_descriptor' style='padding-top:20px;display:"+displayDescriptor+"'>说明：<br><textarea  style='resize:none;' rows=5 cols=50></textarea></div>";
		
		me.keyArray = keyArray;
		me.innerHtml = me.innerHtml.replace("${titleContainer}",this.newESpan('s',longTitle,title,'title',this.id+"_title"));
		me.innerHtml = me.innerHtml.replace("${itemContainer}",itemHtml);
		
	},
	
	/**
	 * （矩阵）打分题 设置窗口
	 * @param setWindow
	 * set:{
				minscore:10 //最低分数
                 maxscore：100 // 最高分数
                 leftdesc:’不清晰’ // 左边文字
                 middledesc:’一般’ //中间文字
                 rightdesc: ‘清晰’  //右边文字
                 extrainput:’true’/’false’ // 是否添加填充框（文本域）
                 required:’true’/’false’ // 填充框是否必填
                 skip：‘true’/‘false’ 是否可以跳过
			} 
	 */
	scoreWindow:function(setWindow){
		var me = this;
		var form = Ext.widget('form',{
			layout:'vbox',
			border:false,
			style:'margin:20px 20px 0px 20px',
			items:[{
				xtype:'numberfield',
				labelWidth:50,
				width:100,
				value:1,
				minValue:0,
				allowBlank:false,
				fieldLabel:QN.template.minScore,
				name:'minscore'
				
			},{
				xtype:'container',
				layout:'hbox',
				items:[{
					xtype:'numberfield',
					labelWidth:50,
					value:100,
					minValue:1,
					allowBlank:false,
					fieldLabel:QN.template.maxScore,
					width:100,
					name:'maxscore'
				}]
			},{
				xtype:'textfield',
				width:250,
				fieldLabel:QN.template.leftSliderText,
				allowBlank:false,
				value:QN.template.lowLevel,
				name:'leftdesc'
			},{
				xtype:'textfield',
				width:250,
				fieldLabel:QN.template.centerSliderText,
				//allowBlank:false,
				value:QN.template.middleLevel,
				name:'middledesc'
			},{
				xtype:'textfield',
				width:250,
				fieldLabel:QN.template.rightSliderText,
				allowBlank:false,
				value:QN.template.hightLevel,
				name:'rightdesc'
			},{
				xtype:'container',
				layout:'hbox',
				items:[{
					xtype:'checkbox',
					boxLabel:QN.template.insertInput,
					name:'extrainput',
					inputValue:'true',
					uncheckedValue:'false',
					id:'insertInput'
				},{xtype:'box',width:20},{
					xtype:'checkbox',
					boxLabel:QN.template.mustFilled,
					name:'required',
					inputValue:'true',
					uncheckedValue:'false',
					listeners:{
						change:function(ck, checked){
							//勾选填充框必填时 此题后添加填充框默认勾选 跳过不填默认不勾选置灰 bug36286
							var insertBox=Ext.getCmp("insertInput")
							var skipBox=Ext.getCmp("canBeSkip");
							if(checked){
								if(!insertBox.getValue()){
									insertBox.setValue(true);
								}
								skipBox.setValue(false);
								skipBox.setDisabled(true);
							}else{
								skipBox.setDisabled(false);
								skipBox.setValue(true);
							}
						}
					}
				}]
			},{
				xtype:'checkbox',
				id:'canBeSkip',
				boxLabel:QN.template.canBeSkip,
				name:'skip',
				inputValue:'true',
				uncheckedValue:'false'
			},{
				border:0,
				width:'100%',
				style:'padding-top:20px',
				buttons:[{
					formBind:true,
					text:common.button.ok,
					handler:function(){
						me.getQuestionSet();
						me.setWindow.close();
					}
				},{
					text:common.button.cancel,
					handler:function(){
						me.setWindow.close();
					}
				}],
				buttonAlign:'center'
			}]
		});
		if(this.questionSet){
			form.query("numberfield[name=minscore]")[0].setValue(this.questionSet.minscore);
			form.query("numberfield[name=maxscore]")[0].setValue(this.questionSet.maxscore);
			form.query("textfield[name=leftdesc]")[0].setValue(this.questionSet.leftdesc);
			form.query("textfield[name=middledesc]")[0].setValue(this.questionSet.middledesc);
			form.query("textfield[name=rightdesc]")[0].setValue(this.questionSet.rightdesc);
			form.query("checkbox[name=extrainput]")[0].setValue(this.questionSet.extrainput);
			form.query("checkbox[name=required]")[0].setValue(this.questionSet.required);
			form.query("checkbox[name=skip]")[0].setValue(this.questionSet.skip);
		}
		
		setWindow.add(form);
	},
	saveScoreSet:function(){
		var scrollY = this.ownerCt.ownerCt.getScrollY();
		
		//  获取参数，存到this.questionSet中
		this.questionSet = this.setWindow.child("form").getValues(false,false,true,false);
		
		var table = Ext.getDom(this.id+"_itemTable");
		table.rows[0].childNodes[0].innerHTML = this.questionSet.leftdesc;
		table.rows[0].childNodes[1].innerHTML = this.questionSet.middledesc;
		table.rows[0].childNodes[2].innerHTML = this.questionSet.rightdesc;
		
		
		var id = table.rows[1].childNodes[0].id.replace("_qescmpbox","");
		var slider = Ext.getCmp(id);
		slider.setMinValue(Ext.Number.from(this.questionSet.minscore,1));
		slider.setMaxValue(Ext.Number.from(this.questionSet.maxscore,100));
		
		var descriptor = Ext.getDom(this.id+"_descriptor");
		if(this.questionSet.extrainput=='true'){
			descriptor.style.display="block";
		}else if(this.questionSet.extrainput=='false'){
			descriptor.style.display="none";
		}
		
		var table = Ext.query("table[extra=mainTable]",true,this.getEl().dom);
       	this.setHeight(table[0].scrollHeight);
       	
       	this.ownerCt.ownerCt.setScrollY(scrollY);
	
	},
	
	getScoreQuestionObj:function(){
	
		var questionObj = {};
		
		var titleEle = Ext.getDom(this.id+"_title");
		questionObj.name = this.pureTextSet[titleEle.id];
		questionObj.longname = titleEle.innerHTML;
		questionObj.typekind = this.questionType;
		questionObj.set = this.questionSet;
		return questionObj;
	},
	
	//////////////////////矩阵打分题/////////////////////////////////////////////////////////
	createMatrixScoreQuestion:function(){
		   var me = this,
		         title = QN.template.matrixTitle,
		         longTitle = QN.template.matrixTitle,
		         itemHtml="<table id='"+this.id+"_itemTable'>";
		var score = 0;
		var displayDescriptor = "none";
		if(me.questionSet&&me.questionSet.extrainput=='true')
			displayDescriptor = "block";
		
		var keyArray = [];
	
		if(me.questionObj){
		
				title = me.questionObj.name;
				longTitle = me.questionObj.longname?me.questionObj.longname:title;longTitle
				
			    itemHtml+='<tr flag="top"><td></td><td class="matrixTd" width="100px" style="border:none"  align="left" valign="middle">'+me.questionSet.leftdesc+'</td>'+
			                     '<td class="matrixTd" width="100px" style="border:none" align="center" valign="middle">'+me.questionSet.middledesc+'</td>'+
			                     '<td class="matrixTd" width="100px" style="border:none" align="right" valign="middle">'+me.questionSet.rightdesc+'</td></tr>';
			    
			var optionList = me.questionObj.optionList;
			for ( var i = 0; i < optionList.length; i++) {
			    var sid = me.newChildId();
				var s = Ext.widget('sliderfield',{id:sid,width:300,value:score,minValue:Ext.Number.from(me.questionSet.minscore,1),
    				maxValue:Ext.Number.from(me.questionSet.maxscore,100),useTips:{style:'border:1px #c5c5c5 solid;background-color:white'}
    			});
			    itemHtml+="<tr><td nowrap>"+this.newESpan('s',optionList[i].optlongname,optionList[i].optname,'text')+"</td>";
			    itemHtml+="<td colspan=3 id='"+sid+"_qescmpbox' ></td></tr>";
			    keyArray.push(sid);
			}
		}else{
				itemHtml+='<tr flag="top"><td></td><td class="matrixTd" width="100px" style="border:none" align="left" valign="middle">'+QN.template.lowLevel+'</td>'+
			                     '<td class="matrixTd" width="100px" style="border:none" align="center" valign="middle">'+QN.template.middleLevel+'</td>'+
			                     '<td class="matrixTd" width="100px" style="border:none" align="right" valign="middle">'+QN.template.hightLevel+'</td></tr>';
			    var sid = me.newChildId();
			   	var s = Ext.widget('sliderfield',{id:sid,width:300,value:score,minValue:0,
    				maxValue:100,useTips:{style:'border:1px #c5c5c5 solid;background-color:white'}
    			});
    			itemHtml+="<tr><td nowrap >"+this.newESpan('s',QN.template.questionOptionText,QN.template.questionOptionText,'text')+"</td>";
			    itemHtml+="<td colspan=3 id='"+sid+"_qescmpbox' width=310></td></tr>";
			    keyArray.push(sid);
		
		}
		
		itemHtml+="</table>";
		itemHtml+="<div id='"+me.id+"_descriptor' style='padding-top:20px;display:"+displayDescriptor+"'>说明：<br><textarea  style='resize:none;' rows=5 cols=50></textarea></div>";
		
		me.keyArray = keyArray;
		
		me.innerHtml = me.innerHtml.replace("${titleContainer}",this.newESpan('s',longTitle,title,'title',this.id+"_title"));
		me.innerHtml = me.innerHtml.replace("${itemContainer}",itemHtml);
		me.innerHtml = me.innerHtml.replace("${buttonContainer}","<a href='###' btn='add' style='font-size:14px'>添加</a>");
	},
	/**
	 * 保存 参数设置
	 */
	saveMaxtrixScoreSet:function(){
	
		var scrollY = this.ownerCt.ownerCt.getScrollY();
		
		//  获取参数，存到this.questionSet中
		this.questionSet = this.setWindow.child("form").getValues(false,false,true,false);
		
		var table = Ext.getDom(this.id+"_itemTable");
		table.rows[0].childNodes[1].innerHTML = this.questionSet.leftdesc;
		table.rows[0].childNodes[2].innerHTML = this.questionSet.middledesc;
		table.rows[0].childNodes[3].innerHTML = this.questionSet.rightdesc;
		
		
		for ( var i = 1; i < table.rows.length; i++) {
			var id = table.rows[i].childNodes[1].id.replace("_qescmpbox","");
			var slider = Ext.getCmp(id);
			slider.setMinValue(Ext.Number.from(this.questionSet.minscore,1));
			slider.setMaxValue(Ext.Number.from(this.questionSet.maxscore,100));
		}
		
		var descriptor = Ext.getDom(this.id+"_descriptor");
		if(this.questionSet.extrainput=='true'){
			descriptor.style.display="block";
		}else if(this.questionSet.extrainput=='false'){
			descriptor.style.display="none";
		}
		
		var table = Ext.query("table[extra=mainTable]",true,this.getEl().dom);
       	this.setHeight(table[0].scrollHeight);
       	
       	this.ownerCt.ownerCt.setScrollY(scrollY);
			
	},
	/**
	 * 取得题目对象
	 * @returns {___anonymous63793_63794}
	 */
	getMaxtrixScoreQuestionObj:function(){
		var questionObj = {};
		
		var titleEle = Ext.getDom(this.id+"_title");
		questionObj.name = this.pureTextSet[titleEle.id];
		questionObj.longname = titleEle.innerHTML;
		questionObj.typekind = this.questionType;
		questionObj.set = this.questionSet;
		
		var optionList = new Array();
		var table = Ext.getDom(this.id+"_itemTable");//Ext.query("table[flag=qitem]",true,this.getEl().dom)[0];
		
		var texts = Ext.query("span[pureText]",true,table);
		for(var i=0;i<texts.length;i++){
			optionList.push({optname:this.pureTextSet[texts[i].id],optlongname:texts[i].innerHTML,orders:i});
		}
		
		questionObj.optionList = optionList;
		
		return questionObj;
	},
	
	
	
	
	////////////////////量表题///////////////////////////////////////////////////////////////////////////
	//创建量表题
	createScaleQuestion:function(){
		 var selectType="radio",title=QN.template.scaleTitle,longTitle,
		     itemHtml="<table id='"+this.id+"_itemTable'>";
		 
		 var displayDescriptor = "none";
			if(this.questionSet&&this.questionSet.extrainput=='true')
				displayDescriptor = "block";
			
	    if(this.questionObj){
	    	    title = this.questionObj.name;
	    	    longTitle = this.questionObj.longname;
	    	    
	    	    
	    	    var levels = this.questionSet.levels;
	    	    for(var i = 0;i<levels.length;i++){
	    	    		itemHtml+="<tr><td valign='middle' nowrap=nowrap><input name='"+this.id+"_select' type='"+selectType+
	    	 	          "'><span style='font-size:14px;' >"+levels[i].text+"</span></td></tr>";
	    	    }
	    	    
	     }else{
	    	 	itemHtml+="<tr><td valign='middle' nowrap=nowrap><input name='"+this.id+"_select' type='"+selectType+
	    	 	          "'><span style='font-size:14px;' >"+QN.template.questionOptionText+"</span></td></tr>";
	     }
	     itemHtml+="</table>";
	     itemHtml+="<div id='"+this.id+"_descriptor' style='padding-top:20px;display:"+displayDescriptor+"'>说明：<br><textarea  style='resize:none;' rows=5 cols=50></textarea></div>";
	     
	     longTitle= longTitle?longTitle:title;
	     this.innerHtml = this.innerHtml.replace("${titleContainer}",this.newESpan('s',longTitle,title,'title',this.id+"_title"));
	     this.innerHtml = this.innerHtml.replace("${itemContainer}",itemHtml);
	},
	
	
	/**
	 * （矩阵）量标题参数window
	 * set:{
	 * 		skip:“true” // 是否可以跳过
	 *		extrainput：“true” // 填充框
	 *		required：“true” //填充框是否必填
	 *		levels：{
	 *			{score:’1’,text:’没有潜力’,longname:’没有潜力’}，
	 *			{score:’2’,text:’潜力较差’,longname:’潜力较差’}，
	 *			{score:’1’,text:’一般’,longname:’一般’}
	 *		}
	 *	}
	 * @param setWindow
	 */
	scaleWindow:function(setWindow){
		var me = this;
		var data = new Array();
		if(this.questionSet){
			data = this.questionSet.levels;
		}else{
			data.push({text:QN.template.questionOptionText,score:10});
		}
		var store = Ext.create("Ext.data.Store",{
			fields:['text','score'],
			data:data
		});
		
		
		var form = Ext.widget("form",{
			bodyBorder:false,
			border:false,
			layout:{
				type:'vbox'
				//align:'stretch'
			},
			style:'padding:5px',
			items:[{xtype:'label',text:QN.template.questionLayout,style:'margin-bottom:10px'},{
				xtype:'container',
				layout:{type:'table',columns:2,tableAttrs:{width:200},tdAttrs:{style:'padding-left:20px;',width:'50%'} },
				items:[{xtype:'radio',boxLabel:QN.template.vArrangeItem,name:'type',inputValue:'1',value:1},
				       {xtype:'radio',boxLabel:QN.template.hArrangeItem,name:'type',inputValue:'2'},
				       {xtype:'radio',boxLabel:QN.template.selectItem,name:'type',inputValue:'3'},
				       {xtype:'container',layout:'hbox',items:[{xtype:'radio',boxLabel:QN.template.byColumn,style:'margin-right:10px;',name:'type',inputValue:'4'},{xtype:'numberfield',name:'column',width:40,step:1,value:1,minValue:1,maxValue:10}]}]
			},{xtype:'container',height:10,width:'100%',html:'<hr/>'},
			{xtype:'checkbox',name:'skip',id:'canBeSkip',boxLabel:QN.template.canBeSkip,style:'padding-left:20px;',inputValue:'true',uncheckedValue:'false'},
			{
				xtype:'container',
				layout:'hbox',
				style:'padding-left:20px;',
				items:[{
					xtype:'checkbox',
					id:'insertInput',
					boxLabel:QN.template.insertInput,
					name:'extrainput',
					inputValue:'true',
					uncheckedValue:'false'
				},{xtype:'box',width:20},{
					xtype:'checkbox',
					boxLabel:QN.template.mustFilled,
					name:'required',
					inputValue:'true',
					uncheckedValue:'false',
					listeners:{
						change:function(ck, checked){
							//勾选填充框必填时 此题后添加填充框默认勾选 跳过不填默认不勾选置灰 bug36286
							var insertBox=Ext.getCmp("insertInput")
							var skipBox=Ext.getCmp("canBeSkip");
							if(checked){
								if(!insertBox.getValue()){
									insertBox.setValue(true);
								}
								skipBox.setValue(false);
								skipBox.setDisabled(true);
							}else{
								skipBox.setDisabled(false);
								skipBox.setValue(true);
							}
						}
					}
				}]
			},
			{
				xtype:'combo',
				fieldLabel:QN.template.selectLevel,
				name:'levelCount',
				editable:false,
				width:200,
				style:'padding:10px 0px 0px 20px',
				displayField:'text',
				valueField:'value',
				value:1,
				store:{
					fields:['text','value'],
					data:[
					{text:QN.template.scaleLevel1,value:1},{text:QN.template.scaleLevel2,value:2},{text:QN.template.scaleLevel3,value:3},
					{text:QN.template.scaleLevel4,value:4},{text:QN.template.scaleLevel5,value:5},{text:QN.template.scaleLevel6,value:6},
					{text:QN.template.scaleLevel7,value:7},{text:QN.template.scaleLevel8,value:8},{text:QN.template.scaleLevel9,value:9},
					{text:QN.template.scaleLevel10,value:10}
					]
				},
				listeners:{
					select:function(comb,selected){
						if(comb.value>store.getCount()){
						    var count = store.getCount();
							var rows = comb.value-store.getCount();
							for(;count<comb.value;count++){
								store.add({text:QN.template.questionOptionText,score:10*(count+1)});
							}
						}else if(comb.value<store.getCount()){
							var rows = store.getCount()-comb.value;
							for(var i=0;i<rows;i++){
								store.removeAt(store.getCount()-1);
							}
						}
						
						
					}
				}
			},
			{
				xtype:'gridpanel',
				width:250,
				height:200,
				style:'padding-left:20px;',
				forceFit:true,
				columns:[
				         {dataIndex:'text',text:QN.template.questionOptionText,editor:{xtype:'textfield',allowBlank:false}},
				         {dataIndex:'score',text:QN.template.score,allowBlank:false,editor:{xtype:'numberfield',allowBlank:false}}
				 ],
				store:store,
				viewConfig:{
				   markDirty:false	
				},
				plugins:{
					ptype: 'cellediting',
			        clicksToEdit: 1
				}
			},{
				border:0,
				dock:'bottom',
				style:'padding-top:20px',
				width:'100%',
				buttons:[{
					formBind:true,
					text:common.button.ok,
					handler:function(){
					   var store =  this.up('form').child('gridpanel').getStore();
					   var scores = {};
					   var valid = true;
					   for(var i=0;i<store.getCount();i++){
					        var re = store.getAt(i);
					        if(scores[re.get('score')]){
					       	   valid = false;
					           break;
					        }
					   		scores[re.get('score')]=1;
					   }
					    if(!valid){
					    		Ext.showAlert("选项的分值不能重复！");
					    		return;
					    }
						me.getQuestionSet();
						me.setWindow.close();
					}
				},{
					text:common.button.cancel,
					handler:function(){
						me.setWindow.close();
					}
				}],
				buttonAlign:'center'
			}]
		});
		
		if(this.questionSet){
				form.child("checkbox[name=skip]").setValue(this.questionSet.skip);
				var typeRadio = form.query("radio[inputValue="+this.questionSet.type+"]");
				if(typeRadio.length>0)
					typeRadio[0].setValue(true);
				var columnComp = form.query("numberfield");
				if(columnComp.length>0)
					columnComp[0].setValue(this.questionSet.column);
				
				form.query("checkbox[name=extrainput]")[0].setValue(this.questionSet.extrainput);
				form.query("checkbox[name=required]")[0].setValue(this.questionSet.required);
				
				form.child('combo').setValue(this.questionSet.levels.length);
		}
		
		setWindow.setHeight(500);
		setWindow.beginDrag=function(){
     	   var plugin = this.child('gridpanel').findPlugin('cellediting');
     	   if(plugin)
     	      plugin.completeEdit( );
  	       this.callParent(arguments);
  	    },
  	    
		setWindow.add(form);
	},
	 //保存 参数设置
	saveScaleSet:function(){
		
		var scrollY = this.ownerCt.ownerCt.getScrollY();
		
		//  获取参数，存到this.questionSet中
		this.questionSet = this.setWindow.child("form").getValues(false,false,true,false);
		var store = this.setWindow.query('gridpanel')[0].getStore();
		var levels = [];
		var itemTable = Ext.getDom(this.id+"_itemTable");
		while(itemTable.rows.length>0)
			itemTable.deleteRow(0);
		store.each(function(re){
			levels.push(re.data);
			var row = itemTable.insertRow();
			var cell = row.insertCell();
			cell.valign="middle";
			cell.setAttribute("nowrap","nowrap");
			cell.innerHTML="<input name='"+this.id+"_select' type='radio'><span style='font-size:14px;' >"+re.get("text")+"</span>";
		});
		
		this.questionSet.levels = levels;
        var textarea = Ext.getDom(this.id+"_descriptor");
		if(this.questionSet.extrainput=='true'){
			textarea.style.display='block';
		}else if(this.questionSet.extrainput=='false'){
			textarea.style.display='none';
		}
		
		
		var table = Ext.query("table[extra=mainTable]",true,this.getEl().dom);
       	this.setHeight(table[0].scrollHeight);
       	
       	this.ownerCt.ownerCt.setScrollY(scrollY);
	},
	//获取量表题对象
	getScaleQuestionObj:function(){
		var questionObj = {};
		var titleEle = Ext.getDom(this.id+"_title");
		questionObj.name = this.pureTextSet[titleEle.id];
		questionObj.longname = titleEle.innerHTML;
		questionObj.typekind = this.questionType;
		questionObj.set = this.questionSet;
		
		return questionObj;
	},
	
	
	
	
	

	
	
	//////////////////////矩阵量表题/////////////////////////////////////////////////////
	createMatrixScaleQuestion:function(){
		var me = this;
		var longTitle;
		var title=longTitle = QN.template.matrixLBTitle;
	    if(me.questionObj){
	    	    title = me.questionObj.name;
	    	    longTitle = me.questionObj.longname;
	    }
	    
	    this.innerHtml = this.innerHtml.replace("${titleContainer}",this.newESpan('s',longTitle,title,'title',this.id+"_title"));
		
	    var itemHtml = "<table id='"+this.id+"_itemTable' cellpadding='0' cellspacing='0' style='border-collapse:collapse;'><tr><td class='matrixTd'>&nbsp;</td>";
	    
		if(me.questionObj){
			var levels = me.questionSet.levels;
			var collength = levels.length;
			for ( var i = 0; i < collength; i++) {
			     itemHtml+="<td align='center' valign='middle' class='matrixTd'><span>"+levels[i].text+"</span></td>";
			}
			itemHtml+="</tr>";
			
			var optionList = me.questionObj.optionList;
			for ( var i = 0; i < optionList.length; i++) {
			    itemHtml+="<tr><td align='center' valign='middle' class='matrixTd'>"+this.newESpan('s',optionList[i].optlongname,optionList[i].optname,'text')+"</td>";
				var rowCols = "";
				for ( var j = 0; j < levels.length; j++) {
					rowCols+="<td align='center' valign='middle' class='matrixTd'><input name='"+this.id+"_select'  type='radio'/></td>";
				}
				itemHtml+=rowCols+"</tr>";
			}
		} else {
			itemHtml+="<td align='center' valign='middle' class='matrixTd'><span>"+QN.template.questionOptionText+"</span></td></tr>"+
			                 "<tr><td align='center' valign='middle' class='matrixTd'>"+this.newESpan('s',QN.template.questionOptionText,QN.template.questionOptionText,'text')+
			                 "</td><td align='center' valign='middle' class='matrixTd'><input name='"+this.id+"_select' type='radio' /></td></tr>";
		}
		
		itemHtml+="</table>";
		
		var displayDescriptor = "none";
		if(this.questionSet&&this.questionSet.extrainput=='true')
			displayDescriptor = "block";
		itemHtml+="<div id='"+this.id+"_descriptor' style='padding-top:20px;display:"+displayDescriptor+"'>说明：<br><textarea  style='resize:none;' rows=5 cols=50></textarea></div>";
		me.innerHtml = me.innerHtml.replace("${itemContainer}",itemHtml);
	    me.innerHtml = me.innerHtml.replace("${buttonContainer}",
			   "<a href='###' btn='add' style='font-size:14px'>"+QN.template.addRow+"</a>&nbsp;&nbsp;&nbsp;<a href='###' btn='batchadd' style='font-size:14px'>"+QN.template.batchAdd+"</a>");
	},
	matrixScaleWindow:function(setWindow){
		var me = this;
		var data = new Array();
		if(this.questionSet){
			data = this.questionSet.levels;
		}else{
			data.push({text:QN.template.questionOptionText,score:'1'});
		}
		
		var store = Ext.create("Ext.data.Store",{
			fields:['text','score'],
			data:data
		});
		
		var form = Ext.widget("form",{
			bodyBorder:false,
			border:false,
			layout:{
				type:'vbox'
			},
			style:'padding:5px;',
			items:[{xtype:'container',
					layout:'hbox',
					style:'padding-left:20px;',
					items:[{
						xtype:'checkbox',name:'skip',
						id:'canBeSkip',
						boxLabel:QN.template.canBeSkip,
						inputValue:'true',
						uncheckedValue:'false'},
						{xtype:'box',width:20},{
						xtype:'checkbox',
						boxLabel:QN.template.showScore,
						name:'showscore',
						inputValue:'true',
						uncheckedValue:'false'
					}]
				},{
					xtype:'container',
					layout:'hbox',
					style:'padding-left:20px;',
					items:[{
						xtype:'checkbox',
						id:'insertInput',
						boxLabel:QN.template.insertInput,
						name:'extrainput',
						inputValue:'true',
						uncheckedValue:'false'
					},{xtype:'box',width:20},{
						xtype:'checkbox',
						boxLabel:QN.template.mustFilled,
						name:'required',
						inputValue:'true',
						uncheckedValue:'false',
						listeners:{
							change:function(ck, checked){
								//勾选填充框必填时 此题后添加填充框默认勾选 跳过不填默认不勾选置灰 bug36286
								var insertBox=Ext.getCmp("insertInput")
								var skipBox=Ext.getCmp("canBeSkip");
								if(checked){
									if(!insertBox.getValue()){
										insertBox.setValue(true);
									}
									skipBox.setValue(false);
									skipBox.setDisabled(true);
								}else{
									skipBox.setDisabled(false);
									skipBox.setValue(true);
								}
							}
						}
					}]
				},{
					xtype:'combo',
					fieldLabel:QN.template.selectLevel,
					name:'levelCount',
					editable:false,
					width:200,
					style:'padding:10px 0px 0px 20px',
					displayField:'text',
					valueField:'value',
					value:1,
					store:{
						fields:['text','value'],
						data:[
						{text:QN.template.scaleLevel1,value:1},{text:QN.template.scaleLevel2,value:2},{text:QN.template.scaleLevel3,value:3},
						{text:QN.template.scaleLevel4,value:4},{text:QN.template.scaleLevel5,value:5},{text:QN.template.scaleLevel6,value:6},
						{text:QN.template.scaleLevel7,value:7},{text:QN.template.scaleLevel8,value:8},{text:QN.template.scaleLevel9,value:9},
						{text:QN.template.scaleLevel10,value:10}
						]
					},
					listeners:{
						select:function(comb,selected){
							if(comb.value>store.getCount()){
								var count = store.getCount();
								var rows = comb.value-store.getCount();
								for(;count<comb.value;count++){
									store.add({text:QN.template.questionOptionText,score:10*(count+1)});
								}
								/*var rows = comb.value-store.getCount();
								for(var i=0;i<rows;i++){
									store.add({text:QN.template.questionOptionText,score:'10'});
								}*/
							}else if(comb.value<store.getCount()){
								var rows = store.getCount()-comb.value;
								for(var i=0;i<rows;i++){
									store.removeAt(store.getCount()-1);
								}
							}
							
							
						}
					}
				},{
					xtype:'gridpanel',
					width:250,
					height:200,
					style:'padding-left:20px;',
					forceFit:true,
					columns:[
					         {dataIndex:'text',text:QN.template.questionOptionText,editor:{xtype:'textfield',allowBlank:false}},
					         {dataIndex:'score',text:QN.template.score,allowBlank:false,editor:{xtype:'numberfield',allowBlank:false}}
					 ],
					store:store,
					viewConfig:{
					   markDirty:false	
					},
					plugins:{
						ptype: 'cellediting',
				        clicksToEdit: 1
					}
				},{
					border:0,
					dock:'bottom',
					style:'padding-top:10px',
					width:'100%',
					buttons:[{
						formBind:true,
						text:common.button.ok,
						handler:function(){
							var store =  this.up('form').child('gridpanel').getStore();
						   var scores = {};
						   var valid = true;
						   for(var i=0;i<store.getCount();i++){
						        var re = store.getAt(i);
						        if(scores[re.get('score')]){
						       	   valid = false;
						           break;
						        }
						   		scores[re.get('score')]=1;
						   }
						    if(!valid){
						    		Ext.showAlert("选项的分值不能重复！");
						    		return;
						    }
							me.getQuestionSet();
							me.setWindow.close();
						}
					},{
						text:common.button.cancel,
						handler:function(){
							me.setWindow.close();
						}
					}],
					buttonAlign:'center'
				}
			]
		});
		
		if(this.questionSet){
			form.query("checkbox[name=skip]")[0].setValue(this.questionSet.skip);
				
			form.query("checkbox[name=extrainput]")[0].setValue(this.questionSet.extrainput);
			form.query("checkbox[name=required]")[0].setValue(this.questionSet.required);
			form.query("checkbox[name=showscore]")[0].setValue(this.questionSet.showscore);
			
				
			form.child('combo').setValue(this.questionSet.levels.length);
		}
		
		setWindow.setHeight(380);
		setWindow.beginDrag=function(){
		   //gridpanel 属于form里面的，当前js获取不到 	wangb1 20170504   21629
     	   var plugin = form.child('gridpanel').findPlugin('cellediting');
     	   if(plugin)
     	      plugin.completeEdit( );
  	       this.callParent(arguments);
  	    },
  	    
		setWindow.add(form);
	},
	/**
	 * 保存 参数设置
	 */
	saveMaxtrixScaleSet:function(){
		
		var scrollY = this.ownerCt.ownerCt.getScrollY();
		
		//  获取参数，存到this.questionSet中
		this.questionSet = this.setWindow.child("form").getValues(false,false,true,false);
		var store = this.setWindow.query('gridpanel')[0].getStore();
		var levels = [];
		
		var itemTable = Ext.getDom(this.id+"_itemTable");
		for(var i=0;i<itemTable.rows.length;i++){
			var cells = itemTable.rows[i].cells;
			while(cells.length>1)
				itemTable.rows[i].deleteCell(1);
		}
		var me = this;
		store.each(function(re){
			levels.push(re.data);
			
			var cell = itemTable.rows[0].insertCell();
			cell.valign="middle";
			cell.align="center";
			cell.setAttribute("class","matrixTd");
			cell.innerHTML="<span>"+re.data.text+"</span>";
			for(var i=1;i<itemTable.rows.length;i++){
				cell = itemTable.rows[i].insertCell();
				cell.valign="middle";
				cell.align="center";
				cell.setAttribute("class","matrixTd");
				cell.innerHTML="<input type='radio' name='"+me.id+"_select'/>";
			}
		});
		
		this.questionSet.levels = levels;
		
		    var textarea = Ext.getDom(this.id+"_descriptor");
			if(this.questionSet.extrainput=='true'){
				textarea.style.display='block';
			}else if(this.questionSet.extrainput=='false'){
				textarea.style.display='none';
			}
			
			
			var table = Ext.query("table[extra=mainTable]",true,this.getEl().dom);
	       	this.setHeight(table[0].scrollHeight);
	       	
	       	this.ownerCt.ownerCt.setScrollY(scrollY);
	},
	/**
	 * 获取矩阵量表题对象
	 * @returns {___anonymous73256_73257}
	 */
	getMaxtrixScaleQuestionObj:function(){
		
		var questionObj = {};
		var titleEle = Ext.getDom(this.id+"_title");
		questionObj.name = this.pureTextSet[titleEle.id];
		questionObj.longname = titleEle.innerHTML;
		questionObj.typekind = this.questionType;
		questionObj.set = this.questionSet;
		
		var itemTable = Ext.getDom(this.id+"_itemTable");
		var optionList = new Array();
		for(var i=1;i<itemTable.rows.length;i++){
		    var span = itemTable.rows[i].cells[0].getElementsByTagName("span")[0];
		    optionList.push({optname:this.pureTextSet[span.id],optlongname:span.innerHTML,orders:i});
		}
		questionObj.optionList = optionList;
		
		return questionObj;
	},
	
	
	
	newESpan:function(objType,text,pureText,edittype,id){
	      var span;
	      if(!id)
	         id = Ext.id(null,this.id+"_textspan");
	      this.pureTextSet[id] = pureText;
	      var fontsize = '14px';
	      if(edittype=='title')
	         fontsize = '15px';
	      if(objType=='e'){
	      	   span = document.createElement("span");
	      	   span.id=id;
			   span.innerHTML=text;
			   span.setAttribute("flag","text");
			   span.setAttribute("pureText",pureText);
			   span.setAttribute("edittype",edittype);
			   span.onclick = this.textEditHandler.bind(this);
			   span.style.cursor="pointer";
			   span.style.fontSize=fontsize;
	      }else{
	           
	           var idAtt = "id= '"+id+"' ";
	           span = "<span "+idAtt+" edittype='"+edittype+"' style='cursor:pointer;font-size:"+fontsize+"' pureText='true'>"+text+"</span>";
	      }
	
	      return span;
	},
	
	/**
	 * 复制题目
	 */
	copyMatrix:function(){
		var questionObj = this.getQuestionObject();
		questionObj.set = this.copyQuestionSet();
		var index = this.ownerCt.items.indexOf(this);
		this.ownerCt.add(index+1,{xtype:'questionitem',style:'margin-top:10px;',questionObj:questionObj,imageSavePath:this.imageSavePath});
		this.orderQuestionNumber(this.ownerCt);
	},
	
	copyQuestionSet:function(){
	     var newSet = {};
	     Ext.apply(newSet,this.questionSet);
	     if(this.questionType==13 || this.questionType==15){
	        var levels = this.questionSet.levels;
	        var newLevs = [];
	        for(var i=0;i<levels.length;i++){
	            newLevs.push(Ext.apply({},levels[i]));
	        }
	        newSet.levels = newLevs;
	     }
	     return newSet;
	},
	/**
	 * 调整题目序号
	 * @param parent 题目的父容器
	 */
	orderQuestionNumber:function(parent){
		var items = parent.query('questionitem');
		var index = 1;
		for ( var i = 0; i < items.length; i++) {
		
			var orderEle = Ext.get(items[i].id+"_quesNo");
			    if(orderEle){
			         orderEle.setHtml("Q"+index);
			         index++;
			    }
			    
		    /*
			var order = items[i].queryById('questionOrder');
			if(order){
				order.getEl().setHtml('<label style="color:white">Q'+index+'</label>');
				index++;
			}else{
			    var orderEle = Ext.get(items[i].id+"_quesNo");
			    if(orderEle){
			         orderEle.setHtml("Q"+index);
			         index++;
			    }
			    
			}
			*/
		}
	},
	//销毁对象时销毁关联对象
	onDestroy:function(){
		var cmps =  Ext.ComponentQuery.query('box[id^='+this.id+'_qc]');
		this.callParent(arguments);
		Ext.destroy(cmps);
	}
});