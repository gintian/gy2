Ext.define("ResumeFilter.showResumeFilterList",{
	z0301:'',//职位id
	commonWidth:'',//当前页面宽度
	temNum:'',//用于下面给日期图标后的div  id赋值
	temDateNum:'',//datefield 的id
	temNumberNum:'',//numberfield 的id
	temCodeCount:'',//代码项（只有一级代码且不超过五个）的统计数
	temPanelId:'',//多层代码所在panel id
	temTextFieldId:'',//多层代码存放代码 id 的textfield
	temFilterCount:'',//筛选器计数
	oldFilterName:'',//筛选器更改前名称
	msg:'',//标志位，用来标注是否当前处于编辑状态
	isBlur:'',//判断过滤器名称编辑框是否失去焦点
	temJsonObj:'',//默认筛选器json对象
	filterItemId:'',//筛选指标id
	defaultFilterName:'',//默认筛选器名称
	enableDelFilterRule:"0",//有删除筛选规则权限
	isReload: false, //是重新加载页面操作
	constructor:function(config){
		resumeFilter = this;
		resumeFilter.z0301 = config.z0301;
		resumeFilter.commonWidth = Ext.getBody().getWidth() * 0.9;
		this.initParamValue();
		this.init();
	},
	//初始化参数
	initParamValue:function(){
		resumeFilter.temNum = 0;
		resumeFilter.temDateNum = 0;
		resumeFilter.temCodeCount = 0;
		resumeFilter.temPanelId = "";
		resumeFilter.temTextFieldId = "";
		resumeFilter.temFilterCount = 0;
		resumeFilter.oldFilterName = "";
		resumeFilter.msg = true;
		resumeFilter.isBlur = false;
		resumeFilter.isSave = true;
		resumeFilter.temJsonObj = "";
		resumeFilter.filterItemId = "";
		resumeFilter.defaultFilterName = "";
	},
	init:function(){
		var map = new HashMap();
		map.put("z0301", resumeFilter.z0301);
	    Rpc({functionId:'ZP0000002562',async:false,success:resumeFilter.generatePage},map);
	},
	//生成页面
	generatePage:function(response){
		var result = Ext.decode(response.responseText);
		var delPriv = result.delPriv;
		if (!Ext.isEmpty(delPriv))
			resumeFilter.enableDelFilterRule = delPriv;
		
		var rule = Ext.decode(result.jsonStr);
		
		//当前职位没有筛选器,自动弹出添加筛选指标界面
		if(Ext.isEmpty(rule[0].s1)) {
			if (!resumeFilter.isReload)
				window.parent.addFilterItem();
			
			return;
		}
		
		var items = new Array();
		for(var i = 1;i<4;i++){
			var tem = eval("rule[0].s"+i);
			if(Ext.isEmpty(tem))
				break;
			if(i == 1)//将默认筛选器即第一个筛选器json暂时保存，用于后面点击新增筛选器使用
				resumeFilter.temJsonObj = tem;
			items[i] = resumeFilter.createEachFilter(tem);
		}
		//记录已选筛选指标id
		for(var i=0;i<resumeFilter.temJsonObj.rule.length;i++){
			resumeFilter.filterItemId += resumeFilter.temJsonObj.rule[i].itemid + "`";
		}
		resumeFilter.filterItemId = resumeFilter.filterItemId.substring(0, resumeFilter.filterItemId.length-1);
		//记录默认筛选器的名称
		resumeFilter.defaultFilterName = resumeFilter.temJsonObj.name;
		
		if(items.length == 0)
			return;
		
		var a = Ext.create('Ext.container.Viewport',{
			padding:"0 5 0 5",
			layout:'fit',
			id:"resumePanel_viewport",
			items:{
				xtype:'panel',
				id:'resumePanel_viewport_panel',
				border:false,
				scrollable:true,
				items:items
			},
			autoDestroy:true,
			listeners:{
				//处理多层代码筛选后自适应页面的问题
				resize:{
					fn:function(port,width,height,oldWidth,oldHeight){
						resumeFilter.commonWidth = width;
						for(var i = 0;i<=resumeFilter.temCodeCount;i++){
							if(!Ext.isEmpty(Ext.getCmp("panel"+i))){
								Ext.getCmp("panel"+i).maxWidth = width*0.5;
								//Ext.getCmp("panel"+i).updateLayout({defer:true,isRoot:true});
							}
						}
						//更改文本域宽度
						var textareas = port.query("textarea");
						Ext.each(textareas,function(textarea){
							textarea.setWidth(width * 0.6);
						});
					}
				}
			}
	    });
		//a.getScrollable().scrollTo(50, 400);
		
	},
	//创建每个itemid对应的页面元素
	createItem:function(tem,index){
		
		//统一生成代码型id序号 筛选器序号+指标在筛选器中的序号
		var idstr = ""+resumeFilter.temFilterCount+index;
		
		if(Ext.isEmpty(tem))
			return;
		if(Ext.isString(tem))
			tem = Ext.decode(tem);
		//代码项类型
		var types,id,width,minHeight;
		if(tem.itemtype == "A")
			types = "textfield";
		else if(tem.itemtype == "N"){
			id = "number"+resumeFilter.getNumId();
			types = "numberfield";
		}else if(tem.itemtype == "D"){
			types = "datefield";
			id = "date"+resumeFilter.getDateId();
		}else{
			types = "textarea";
			width = resumeFilter.commonWidth * 0.6;
			minHeight = 100;
		}
		var field;
		//代码项
		if(tem.codesetid != "0" && !Ext.isEmpty(tem.codesetid)){
			//tem.showway == "stretch" 说明当前代码项只有一级代码且不超过五个，此时需要罗列出所有的代码项
			if(Ext.isEmpty(tem.showway)){
				var paramName = tem.itemid+idstr;
				var linkTem;
				if("UM,UN,@K".indexOf(tem.codesetid) != -1)
					linkTem = ' onclick="resumeFilter.toSelectUnit(this,'+idstr+',\''+tem.codesetid+'\')"';
				else 
					linkTem = ' plugin="deepcodeselector" multiple="true" afterfunc="resumeFilter.getDeepCodeValue" codesetid="'+tem.codesetid+'" inputname="'+paramName+'_view"  onclick="resumeFilter.setClickId('+idstr+');"';
				field = Ext.widget('panel',{
					border:false,
					layout:'hbox',
					cls:'hj-zm-cj-zwmc',
					style:'width:100px',
					items:[
					       {xtype:'label',width:100,style:'margin-top:10px;',border:false,labelSeparator:null,text:tem.itemdesc},
					       {
								xtype:'textfield',
								id:'textfield'+idstr,
								name:tem.itemid,
								value:Ext.isEmpty(tem.queryvalue1) ? '' : tem.queryvalue1,
								hidden:true
							},
							{
								xtype:'panel',
								border:false,
								id:'panel'+idstr,
								maxWidth:resumeFilter.commonWidth*0.6
							},
					       {border:false,style:'margin-left:10px;margin-top:10px;',html:'<input type="hidden" name="'+paramName+'_value"/><input type="text" name="'+paramName+'_view" style="border:0px;width:0px;margin-top:-18px;height:22px"/>'+
								'<a id="deep'+resumeFilter.temCodeCount+'" src="/module/recruitment/image/xiala2.png" href="javascript:void(0)" '+
								linkTem+' >添加'+tem.itemdesc+'</a>'}
					      ],
					 listeners:{
						'render':function(){
							var idList = new Array();
							for(var i = 0;i<=resumeFilter.temCodeCount;i++)
								idList[i] = "deep"+i;
							//动态关联deepCodeSelector   使用 setDeepEleConnect方法
							setDeepEleConnect(idList);
						}
					 }
					
				});
				var temids;
				//如果已有保存值，则显示
				if(!Ext.isEmpty(tem.queryvalue1)){
					for(var k = 0;k<tem.queryvalue1.split(",").length;k++){
						temids = tem.queryvalue1.split(",")[k].replace(/'/g,"");
						if(Ext.isEmpty(temids))
							continue;
						//代码型删除id 生成规则 筛选计数器+在筛选器中的序号
						var ids = 'id'+temids+'panel'+'textfield'+ idstr;
						if(!Ext.isDefined(Ext.getCmp(ids))){
							var label = Ext.create('Ext.form.Label',{
								style:'margin-right:10px;',
								html:tem.showHTML[k-1],
								id:ids
							});
							Ext.getCmp('panel'+idstr).insert(label);
						}
					}
				}
				resumeFilter.temCodeCount++;
			}else if(tem.showway == "stretch"){//罗列出所有的codeitem
				field = Ext.widget('checkboxgroup',{
					fieldLabel:tem.itemdesc,
					labelSeparator:null,
					labelStyle:'padding-left:10px',
					labelPad:12,
					name:tem.itemid,
					style:'margin-top:10px',
					hideLabel:false,
					columnWidth:100,
					columns:8,
					vertical:true,
					items:tem.items
				});
				//将checkboxgroup中的每个checkbox中的getValue覆写，以便于form提交时获取到的值是自定义值，而不是默认的checked返回的布尔值
				for(var i = 0;i<field.getBoxes().length;i++){
					var box = field.getBoxes()[i];
					Ext.override(box,{
						getValue:function(){
							if(this.checked)
								return this.inputValue;
							else 
								return "";
						}
					});
				}
					
			}
		}else{
			var formats = "Y-m-d";
			if(tem.datetype == "1")
				formats = "Y";
			else if(tem.datetype == "2")
				formats = "m";
			else if(tem.datetype == "3")
				formats = "d";
			if(tem.flag!="1"){
				var updateTip = function (field, t) {
					if(field.getValue() == null || field.getValue().length<=0 ){
						if(Ext.QuickTips)
							Ext.QuickTips.destroy();
						return;
					}
						
					Ext.QuickTips.init();
					Ext.QuickTips.register({ 
						target: field.el, 
						text: field.getValue() 
					}) 
				};
					
				field = Ext.widget(types,{
					fieldLabel:tem.itemdesc,
					labelSeparator:null,
					labelWidth:130,
					width:Ext.isEmpty(width) ? 280 : width,
					name:tem.itemid,
					minHeight:Ext.isEmpty(minHeight) ? 10 : minHeight,
					maxLength:280,
					id:Ext.isEmpty(id) ? "" : id,//日期（数值）型指标 赋值id
					hideTrigger:false,//数值型指标需要隐藏自带的按钮
					cls:'hj-zm-cj-zwmc',
					style:'margin-top:10px;',
					format:formats,
					value: Ext.isEmpty(tem.queryvalue1) ? '' : tem.queryvalue1,
					listeners:{
						 // 鼠标移动到文本框显示悬浮框
					    render : function(p) {
					        p.getEl().on('mouseover', function(p1) {
					        		updateTip(p);
					        });
					    },
						
					 	blur:{//校验截止值大于起始值
					 		fn:function(comp){
					 			var id = comp.getId();
					 			if(types == "datefield")
					 				resumeFilter.validate(id,id+"1","D","start");
					 			else if(types == "numberfield")
					 				resumeFilter.validate(id,id+"1","N","start");
					 				
				 			}
				 		}
				 	}
				});
			}
			//数值型指标
			if(tem.itemtype == "N"){
				field.setHideTrigger(true);
				
				var field1 = Ext.widget('numberfield',{
		    	    labelSeparator:null,
					maxLength:250,
					name:tem.itemid,
					id:id+'1',
					hideTrigger:true,//数值型指标需要隐藏自带的按钮
					cls:'hj-zm-cj-zwmc',
					style:'margin-top:10px',
					value: Ext.isEmpty(tem.queryvalue2) ? '' : tem.queryvalue2,
					listeners:{
					 	blur:{//校验截止日期大于起始日期
					 		fn:function(comp){
					 			var id = comp.getId();
					 			resumeFilter.validate(id.substring(0,id.length-1),id,"N","end");
				 			}
				 		}
				 	}
				});
				//重写field的校验
				resumeFilter.overriteValid(field, "resumeFilter.validate('"+id+"','"+id+"1','N','start')");
				resumeFilter.overriteValid(field1, "resumeFilter.validate('"+id+"','"+id+"1','N','end')");
				
				field = Ext.widget('panel',{
					border:false,
					layout:'hbox',
					items:[
					         field,{border:false,style:'margin-left:5px;padding-left:5px;padding-top:12px;margin-top:10px',html:'至'},field1
					      ]
				});
			}else if(tem.itemtype == "D"){//日期型指标
				var divId = resumeFilter.getDivId();
				if(tem.flag=="0"){
					field.setFieldLabel(field.fieldLabel+"<a href='javascript:void(0);'  onclick='resumeFilter.generateDateTypes(\""+divId+"\",\""+tem.itemdesc+"\",\""+tem.itemid+"\",\""+tem.queryvalue1+"\",\""+tem.queryvalue2+"\")'>"
							+"<img dropdownName='dropdownBox' id='img"+divId+"' class='img-middle'	src='/module/recruitment/image/jiantou.png' style='margin-left:5px'	/></a>" 
							+"<div id='div"+divId+"' style='width:0px;height:3px;border:0px;background-color:red;float:left;'></div>");
					
					var field1 = Ext.widget('datefield', {
			    	    labelSeparator:null,
						maxLength:250,
						name:tem.itemid,
						id:id+'1',//日期型指标 赋值id
						cls:'hj-zm-cj-zwmc',
						style:'margin-top:10px',
						format:formats,
						value: Ext.isEmpty(tem.queryvalue2) ? '' : tem.queryvalue2,
						listeners:{
						 	blur:{//校验截止日期大于起始日期
						 		fn:function(comp){
						 			var id = comp.getId();
						 			resumeFilter.validate(id.substring(0,id.length-1),id,"D","end");
					 			}
					 		}
					 	}
			         });
					//重写field的校验
					resumeFilter.overriteValid(field, "resumeFilter.validate('"+id+"','"+id+"1','D','start')");
					resumeFilter.overriteValid(field1, "resumeFilter.validate('"+id+"','"+id+"1','D','end')");
					
					field = Ext.widget('panel',{
						border:false,
						id:'destroy'+divId,
						layout:'hbox',
						items:[
						         field,{border:false,style:'margin-left:5px;padding-left:5px;padding-top:12px;margin-top:10px',html:'至'},
						         {
									xtype:'textfield',
									name:tem.itemid+'datetype',
									id:'datetype'+divId,
									value:Ext.isEmpty(tem.datetype) ? '0' : tem.datetype,
									hidden:true
								 },field1
						    ]
					});
				}else{
					if(tem.queryvalue1 == "" && tem.queryvalue2 == ""){
						tem.flag = "2";
					}
					
					items = resumeFilter.createitems(divId,tem.itemdesc,'numberfield',tem.flag,tem.itemid,tem.queryvalue1,tem.queryvalue2);
					field = Ext.widget('panel',{
						border:false,
						id:'destroy'+divId,
						value:Ext.isEmpty(tem.datetype) ? '1' : tem.datetype,
						layout:'hbox',
						items:items
					});
				}
			}
		}
		return field;
	},
	//归属单位、部门、岗位赋值
	toSelectUnit:function(btn,id,codesetid){
		resumeFilter.setClickId(id);
		
		var adddepartment,addunit;
		if("UM" == codesetid){
			adddepartment = true;
			addunit = false;
		}else if("UN" == codesetid){
			adddepartment = false;
			addunit = true;
		}
		var picker = new PersonPicker({
					multiple: true,
					text: "添加",
					nbases: 'oth', // 人员库范围字符串，空为默认全部。如：Usr,Ret
					//orgid: examhall_me.b0110, // 组织机构，不传代表全部
					addunit:addunit, //是否可以添加单位
					adddepartment:adddepartment, //是否可以添加部门
					callback: function (c) {
						resumeFilter.getDeepCodeValue(c,2);
					}
				}, btn);
		picker.open();
	
	},
	//校验范围值
	validate:function(id,id1,flag,showPos){
		var res = true;
		var startValue = Ext.getCmp(id).getValue();
		var endValue = Ext.getCmp(id+"1").getValue();
		if(Ext.isEmpty(startValue) || Ext.isEmpty(endValue))
			return res;
		if(flag == "D"){
			//必须确保两个datefield中的format一致，否则在创建Date对象前必须再调用Date.format进行格式化
 			var startDate = new Date(startValue);
 			var endDate = new Date(endValue);
 			
 			if(Date.parse(startDate) > Date.parse(endDate)){
 				res = false;
 				if(showPos == "start")
 					Ext.getCmp(id).markInvalid("起始日期不能大于截止日期");
 				else
 					Ext.getCmp(id1).markInvalid("截止日期不能小于起始日期");
 			}else{//当输入值校验通过后移除错误提示框
 				Ext.getCmp(id).isValid();
 				Ext.getCmp(id1).isValid();
 			}
 				
		}else if(flag == "N"){
			if(startValue > endValue){
				res = false;
 				if(showPos == "start")
 					Ext.getCmp(id).markInvalid("起始值不能大于截止值");
 				else
 					Ext.getCmp(id1).markInvalid("截止值不能小于起始值");
 			}else{//当输入值校验通过后移除错误提示框
 				Ext.getCmp(id).isValid();
 				Ext.getCmp(id1).isValid();
 			}
		}
		return res;
	},
	//重写ext field的校验规则,其中field是ext中form field对象，res是自定义校验方法（必须返回boolean）
	overriteValid:function(field,res){
		Ext.override(field,{
			validate:function(){
				var me = this,
	            isValid = me.isValid();
				if (isValid !== me.wasValid) {
		            me.wasValid = isValid;
		            me.fireEvent('validitychange', me, isValid && eval(res));
		        }
				return isValid && eval(res);
			}
		});
	},
	//生成div id
	getDivId:function(){
		resumeFilter.temNum++;
		//若已存在该元素，重新计算
		if(!Ext.isEmpty(Ext.getDom("div"+resumeFilter.temNum)))
			resumeFilter.getDivId();
		
		return resumeFilter.temNum;	
	},
	//生成datefield id
	getDateId:function(){
		resumeFilter.temDateNum++;
		//若已存在该元素，重新计算
		if(!Ext.isEmpty(Ext.getDom("date"+resumeFilter.temDateNum)))
			resumeFilter.getDateId();
		
		return resumeFilter.temDateNum;	
	},
	//生成numberfield id
	getNumId:function(){
		resumeFilter.temNumberNum++;
		//若已存在该元素，重新计算
		if(!Ext.isEmpty(Ext.getDom("number"+resumeFilter.temNumberNum)))
			resumeFilter.getNumId();
		
		return resumeFilter.temNumberNum;	
	},
	//生成日期类型指标筛选类型下拉菜单
	generateDateTypes:function(divId,itemdesc,itemid,queryvalue1,queryvalue2){
		if(Ext.get('div'+divId).getHtml()){
			var combId = Ext.getDom('div'+divId).childNodes[0].id;
			var pick = Ext.getCmp(combId).getPicker();
			if(pick.isHidden())
				pick.show();
			else
				pick.hide();
			return;
		}
		var store = Ext.create('Ext.data.Store',{
			fields:['dataName','dataValue'],
			data:[
			      {'dataName':'默认(日期)','dataValue':'1'},
			      {'dataName':'年限','dataValue':'2'},
			      {'dataName':'月','dataValue':'3'},
			      {'dataName':'日','dataValue':'4'}
			     ]
		});
		
		var box = Ext.widget('combo',{
			width:60,
			store: store,
			multiSelect:false,//为true 第二个参数record是array否则就是选中的record
			readOnly:true,
			queryMode:'local',
			displayField: 'dataName',
	  	    valueField: 'dataValue',
	  	    renderTo:'div'+divId,
	  	    listeners:{
		   		blur:{
					fn:function(combox){
						var res = store.find("dataValue",combox.getValue());
						if(res==-1){//无效输入值
							combox.setValue("");
						}
						combox.getPicker().hide();
					}
				},
				expand:{
					fn:function(combox){
						Ext.getDom(combox.getPicker().getId()).style.width = combox.width;
						//重新定位下拉菜单显示位置和div显示位置(防止显示时将页面其他元素覆盖)
						var img = Ext.get("img"+divId);
						Ext.get("div"+divId).setX(img.getX());
						Ext.get("div"+divId).setY(img.getY()-8);
						combox.getPicker().showAt(img.getX(),img.getY()-10);
					}
				},
				afterrender:{
					fn:function(combox){
						//	Ext 6下面隐藏输入框后，输入框外层div会显示出来问题
						Ext.getDom(combox.getInputId()).parentNode.style.border = "0px";
						Ext.get(combox.getInputId()).hide();
						combox.expand();
					}
				},
				select:{//变更日期类型时触发
					fn:function(combox,records){
						var value = records.data.dataValue;
						if(value == "2"){
							Ext.getCmp('destroy'+divId).removeAll(true);
							var items = resumeFilter.createitems(divId,itemdesc,'numberfield',value,itemid,queryvalue1,queryvalue2);
							Ext.getCmp('destroy'+divId).add(items);
						}else{
							Ext.getCmp('destroy'+divId).removeAll(true);
							if(value=="1"){
								var items = resumeFilter.createitems(divId,itemdesc,'datefield',value,itemid,queryvalue1,queryvalue2);
								Ext.getCmp('destroy'+divId).add(items);
								Ext.getCmp("date"+divId).format = "Y-m-d";
								Ext.getCmp("date"+divId+"1").format = "Y-m-d";
							}else if(value == "3"){
								var items = resumeFilter.createitems(divId,itemdesc,'numberfield',value,itemid,queryvalue1,queryvalue2);
								Ext.getCmp('destroy'+divId).add(items);
								Ext.getCmp("date"+divId).maxValue = 12;
								Ext.getCmp("date"+divId).minValue = 1;
								Ext.getCmp("date"+divId+"1").maxValue = 12;
								Ext.getCmp("date"+divId+"1").minValue = 1;
//								Ext.getCmp("date"+divId).format = "m";
//								Ext.getCmp("date"+divId+"1").format = "m";
							}else if(value == "4"){
								var items = resumeFilter.createitems(divId,itemdesc,'numberfield',value,itemid,queryvalue1,queryvalue2);
								Ext.getCmp('destroy'+divId).add(items);
								Ext.getCmp("date"+divId).maxValue = 31;
								Ext.getCmp("date"+divId).minValue = 1;
								Ext.getCmp("date"+divId+"1").maxValue = 31;
								Ext.getCmp("date"+divId+"1").minValue = 1;
//								Ext.getCmp("date"+divId).format = "d";
//								Ext.getCmp("date"+divId+"1").format = "d";
							}
							
							Ext.getCmp("date"+divId).setValue();
							Ext.getCmp("date"+divId+"1").setValue();
						}
						combox.getPicker().hide();
					}
				}
	  		 }
		});
	},
	/**创建指定简历筛选器 规则对应的formPanel   
	*obj对应的是generatePage方法中从后台获取json中  rule元素对象
	*obj格式大致为[
     *             {
     *                 "itemid":"A0107",
     *                 "fieldsetid":"A01",
     *                 "itemtype":"A",
     *                 "codesetid":"AX",
     *                 "datatype": "",
     *                 "queryvalue1":"1,2",
     *                 "queryvalue2":""
     *             },
     *             {
     *                 "itemid":"C0101",
     *                 "fieldsetid":"A01",
     *                 "itemtype":"N",
     *                 "codesetid":"0",
     *                 "datatype": "",
     *                 "queryvalue1":"30",
     *                 "queryvalue2":"40"
     *             }
     *         ]
     *         id是筛选器名称所在div的id  格式为  title+……
	*/
	createFormPanel:function(obj,id,filterName){
		if(Ext.isEmpty(obj))
			return;
		if(Ext.isString(obj))
			obj = Ext.decode(obj);
		
		var items = new Array();
		for(var i=0;i<obj.length;i++){
			var temp = obj[i];
			items[i] = resumeFilter.createItem(temp,i);
		}
		items[obj.length] = Ext.widget('textfield',{
			name:"filterid",
			value:temp.filterid,
			hidden:true
		});
		items[obj.length+1] = Ext.widget('textfield',{
    	   xtype:'textfield',
		   name:'name',
		   id:id.replace("title","name"),
		   value:filterName	,
		   hidden:true
		});
		return Ext.widget('form',{
			bodyPadding: 5,
			border:false,
			id:Ext.isEmpty(id) ? "" : id.replace("title","formPanel"),
			style:'margin-left:50px;',
			//通过ajax提交到此url
			url:'',
			// 表单域 Fields 将被竖直排列, 占满整个宽度
		    layout: 'anchor',
		    items:items
		});
	},
	/**创建每个筛选器
	 * obj格式："1":[{
     *         "name":"简历筛选器1",
     *         "rule":[
     *             {
     *                 "itemid":"A0107",
     *                 "fieldsetid":"A01",
     *                 "itemtype":"A",
     *                 "codesetid":"AX",
     *                 "datatype": "",
     *                 "queryvalue1":"1,2",
     *                 "queryvalue2":""
     *             },
     *             {
     *                 "itemid":"C0101",
     *                 "fieldsetid":"A01",
     *                 "itemtype":"N",
     *                 "codesetid":"0",
     *                 "datatype": "",
     *                 "queryvalue1":"30",
     *                 "queryvalue2":"40"
     *             }
     *         ]
     *     }]
	 */
	createEachFilter:function(obj){
		var id = "title"+resumeFilter.temFilterCount;
		var filterId = "filterPanel_"+resumeFilter.temFilterCount;
		resumeFilter.temFilterCount++;
		
		return Ext.widget('panel',{
			width:"90%",
			id:filterId,
			border:false,
			items:[
			       {
			    	   html:'<div class="hj-zm-hj-one" id="'+id+'" width="90%" '
			    	   					+'onmousemove="resumeFilter.showDel(\''+id+'\')" onmouseout="resumeFilter.hideDel(\''+id+'\')" ondblclick="resumeFilter.renameFilter(\''+id+'\')">'
			    		    +'<h2><font  style="margin-left:25px">'+obj.name+'</font></h2>'
			    	   		+'<div style="float:right;margin-right:25px;margin-top:-25px;display:none;">'
			    	   		+  '<a href="javascript:void(0);" onclick="resumeFilter.delFilter(\''+filterId+'\',\''+obj.rule[0].filterid+'\')">删除</a>'
			    	   		+'</div>'
			    	   		+'</div>',
			    	   border:false
			       },
			       resumeFilter.createFormPanel(obj.rule,id,obj.name)
			      ]
		});
	},
	//显示删除操作
	showDel:function(id){
		//第一个筛选器不显示删除
		if(parseInt(id.substring(5,id.length)) == 0)
			return;
		
		//有删除权限时显示
		if(resumeFilter.enableDelFilterRule==="1")
		    Ext.getDom(id).childNodes[1].style.display = "block";
	},
	//隐藏删除操作
	hideDel:function(id){
		Ext.getDom(id).childNodes[1].style.display = "none";
	},
	//删除筛选器
	delFilter:function(id,filterid){
		Ext.Msg.confirm("提示信息","确认删除当前筛选器吗？",function(btn){
			if(btn == "yes"){
				if(Ext.isEmpty(filterid))//如果删除的是新增的筛选器（未保存到数据库）则不用给出操作成功与否提示
					Ext.getCmp(id).destroy();
				else{
					Ext.getCmp(id).destroy();
					resumeFilter.saveFilter(1);
				}
			}
		});
	},
	//重命名筛选器名称
	renameFilter:function(id){
		if(!resumeFilter.msg)
			return;
		var h2 = Ext.getDom(id).childNodes[0];
		resumeFilter.oldFilterName = h2.childNodes[0].innerHTML;
		
		var input = "<input type='text' id='text_"+id+"' onkeypress=\"resumeFilter.EnterPress(event,this,'"+id+"')\"  onblur=\"resumeFilter.removeInput(this,'"+id+"')\" value='"+resumeFilter.oldFilterName+"'/> (按回车键或点击页面其他地方保存)";
		h2.innerHTML = input;
		Ext.getDom('text_'+id).focus();
		resumeFilter.msg = false;
	},
	//更新筛选器名称
	EnterPress:function(e,obj,id){
		if(e==null || e=='')
			e = window.event;
		if(e.keyCode == 13 || resumeFilter.isBlur){ 
			var value = Ext.String.trim(obj.value);
			if(Ext.isEmpty(obj.value)){
				Ext.Msg.alert("提示信息","筛选器名称不能为空");
				resumeFilter.isBlur = false; 
				resumeFilter.isSave = false; 
				return;
			}else if(resumeFilter.IsOverStrLength(obj.value,100)){
				Ext.Msg.alert("提示信息","筛选器名称长度不能大于100");
				resumeFilter.isSave = false;
				return;
			}else{
				Ext.getDom(id).childNodes[0].innerHTML = "<font  style='margin-left:25px'>"+value+"</font>";
				Ext.getCmp(id.replace("title","name")).setValue(value);
				resumeFilter.msg = true;
			}
			resumeFilter.isBlur = false;
			resumeFilter.isSave = true;
		}
	},
	//失去焦点触发
	removeInput:function(obj,id){
		resumeFilter.isBlur = true;
		resumeFilter.EnterPress('', obj, id);
	},
	//判断是否超过最大长度
	IsOverStrLength:function(str,len){
	   return str.replace(/[^\x00-\xff]/g,"**").length>len
	   
	},
	//将选择的代码进行展示     tem 为2时代表选择的是组织机构
	getDeepCodeValue:function(array,tem){
		var flag = '';
		var textFieldId = resumeFilter.temTextFieldId;
		var value = Ext.getCmp(textFieldId).getValue();
		
		for(var i=0;i<array.length;i++){
			var id=array[i].value+"panel"+textFieldId;
			var text = array[i].text;
			if(!Ext.isEmpty(tem)){
				id = array[i].id+"panel"+textFieldId;
				text = array[i].name;
			}
			var idz='label'+id;
			var html ="<span style='display:inline-block'><dl style='padding-top:10px;padding-right:15px;text-align:center;color:black;margin-left:5px'>";
					html += '<dt onmouseover="resumeFilter.onMouseover(this)" onmouseleave="resumeFilter.onMouseleave(this)" id=\''+id+'\' >' +text+
								'<img style="display:none;width: 15px; height: 15px;float:left;" class="deletePic" onclick="resumeFilter.removeInfo(this,\''+id+'\',\''+textFieldId+'\')" src="/workplan/image/remove.png" >' +
								'</dt>';
					html += '<dt style="display:none" id='+idz+'>'+id+'</dt>';
					html += "</dl></span>";
			if(!Ext.isDefined(Ext.getCmp('id'+id))){
				var label = Ext.create('Ext.form.Label',{
					style:'margin-right:10px;',
					html:html,
					id:'id'+id
				});
				
				Ext.getCmp(resumeFilter.temPanelId).insert(label);
				if(Ext.isEmpty(tem))
					value=value+","+array[i].value+"";
				else if(tem == 2)
					value=value+","+array[i].id+"";
			}else{
				if(Ext.isEmpty(tem))
					flag=flag+array[i].text+",";
				else
					flag = flag + array[i].name+",";
			}
		}
		Ext.getCmp(resumeFilter.temTextFieldId).setValue(value);
	},
	//鼠标移出图片时隐藏删除图标
   	onMouseleave:function(obj){
		if (!Ext.isEmpty(obj.childNodes[1]))
   		    obj.childNodes[1].style.display="none";
 	},
	//鼠标移入图片时显示删除图标
	onMouseover:function(obj){
 		if (!Ext.isEmpty(obj.childNodes[1])){
 			obj.childNodes[1].style.display="";
 			var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
 		    if(userAgent.indexOf("Firefox") > -1) { //判断是否Firefox浏览器
 		    	obj.childNodes[1].className="newdeletePic";
 		    }
 		}
 		
   		    
	},
	//删除已选代码项
	removeInfo:function(obj,id,textfieldid){
		var str = Ext.get('label'+id).getHtml()+'';
		var value=Ext.getCmp(textfieldid).getValue();
		//截取codeitemid
		str = trim(str.substring(0,str.indexOf("panel")));
		
		value = value.replace(","+str+"",'');
		Ext.getCmp(textfieldid).setValue(value);
		Ext.getCmp("id"+id).destroy();
	},
	//将当前点击的连接所在panel 的id暂时保存
	setClickId:function(ids){
		resumeFilter.temPanelId = "panel"+ids;
		resumeFilter.temTextFieldId = "textfield"+ids;
	},
	//新增简历筛选器
	addFilter:function(){
		if(Ext.isEmpty(resumeFilter.filterItemId)){
			Ext.Msg.alert("提示信息","请预先设置一个筛选器!");
			return;
		}
		if(Ext.getCmp("resumePanel_viewport_panel").items.length >= 3){
			Ext.Msg.alert("提示信息","最多可添加3个筛选器!");
			return;
		}
		resumeFilter.clearDefaultValues();
		Ext.getCmp("resumePanel_viewport_panel").add(resumeFilter.createEachFilter(resumeFilter.temJsonObj));
	},
	//新增模板前先清空默认筛选器中的一些默认值
	clearDefaultValues:function(){
		resumeFilter.temJsonObj.name = resumeFilter.defaultFilterName+(Ext.getCmp("resumePanel_viewport_panel").items.length+1);
		//清除默认筛选器中的筛选器一些默认值
		for(var i=0;i<resumeFilter.temJsonObj.rule.length;i++){
			resumeFilter.temJsonObj.rule[i].filterid = "";
			resumeFilter.temJsonObj.rule[i].queryvalue1 = "";
			resumeFilter.temJsonObj.rule[i].queryvalue2 = "";
			resumeFilter.temJsonObj.rule[i].datetype = "";
			if(!Ext.isEmpty(resumeFilter.temJsonObj.rule[i].items)){
				for(var j = 0;j<resumeFilter.temJsonObj.rule[i].items.length;j++){
					if(resumeFilter.temJsonObj.rule[i].items[j].checked)
						resumeFilter.temJsonObj.rule[i].items[j].checked = false;
				}
			}
		}
	},
	//保存   flag 为1 表示直接点击保存或删除进行保存           2表示设置指标前的保存（保存后不提示信息）
	saveFilter:function(flag){
		var filterid ="";
		var filterObj = new Array();
		var num = 0;
		var form;
		
		if(!resumeFilter.isSave && flag != 1){
			Ext.showAlert(FILTER_CANNOT_BE_EMPTY);
			return;
		}
		
			
		//获取每个form中提交值组成的对象
		for(var i = 0;i<resumeFilter.temFilterCount;i++){
			if(Ext.isEmpty(Ext.getCmp("formPanel"+i)))
				continue;
			form = Ext.getCmp("formPanel"+i).getForm();
			//表单校验
			if(!form.isValid())
				return;
			
			//获取每个筛选器(formPanel中的每项值)
			var formObj = form.getFieldValues(false);
			//只记录目前页面存在的且数据库也存在的筛选器id
			if(!Ext.isEmpty(formObj.filterid))
				filterid += ","+formObj.filterid;
			filterObj[num] = formObj;
			num++;
		}
		if(filterObj.length == 0)
			return;
		
		filterid = filterid.substring(1, filterid.length);

		for(var i = 0;i<filterObj.length;i++){
			var map = new HashMap();
			//只有当默认（第一个）筛选器进入后台时将数据库中多余的筛选器删除
			if(i == 0)
				map.put("filterid", filterid)
			map.put("flag", Ext.isEmpty(flag) ? 1 : flag);
			map.put("obj", Ext.encode(filterObj[i]));
			map.put("z0301", resumeFilter.z0301);
			map.put("itemid", resumeFilter.filterItemId);
			Rpc({functionId:'ZP0000002563',async:false,success:resumeFilter.updateSuccess},map);
		}
	},
	updateSuccess:function(response){
		var result = Ext.decode(response.responseText);
		var flag = result.flag;
		if(Ext.isEmpty(flag) || flag != 2){
			if(!result.infos){
				Ext.Msg.alert("提示信息","保存失败!");
			}else{
				  Ext.Msg.alert("提示信息","保存成功!");
				 //Ext.getCmp('').setDisabled(false);
			}
		}
	},
//	//设置筛选指标
//	addFilterItem:function(){
//		//保存当前页面中已填写的值或已新增的筛选器
//		if(!Ext.isEmpty(resumeFilter.filterItemId))
//			resumeFilter.saveFilter(2);
//		
//		Ext.Loader.setConfig({
//			enabled: true,
//			paths: {
//				'EHR': '/components/'
//			}
//		});
//		if(undefined==Ext.getCmp('addWindow')){
//			Ext.require('EHR.fielditemmultiselector.Selector', function(){
//				Ext.create("EHR.fielditemmultiselector.Selector",{fieldset:'A',module:'ZP',items:Ext.encode(resumeFilter.temJsonObj.rule),afterfunc:'resumeFilter.refreshPage'});
//			});
//		}
//	},
	//设置指标后刷新页面
	refreshPage:function(jsonData){
		//resumeFilter.initParamValue();
		
		var map = new HashMap();
		map.put("jsonStr", jsonData);
		map.put("z0301", resumeFilter.z0301);
		map.put("itemid", resumeFilter.filterItemId);
		
		Rpc({functionId:'ZP0000002564',async:false,success:resumeFilter.reloadPage},map);
	},
	//设置指标后重新加载页面
	reloadPage:function(response){
		var result = Ext.decode(response.responseText);
		var rule = Ext.decode(result.jsonStr);
		
		//更新默认筛选器指标
		resumeFilter.filterItemId = "";
		for(var i = 0;i<rule.length;i++)
			resumeFilter.filterItemId += "`"+rule[i].itemid; 
		resumeFilter.filterItemId = resumeFilter.filterItemId.substring(1,resumeFilter.filterItemId.length);
		
		//非首次进行设置指标则先清空viewport
		if(!Ext.isEmpty(Ext.getCmp("resumePanel_viewport")))
			Ext.getCmp("resumePanel_viewport").destroy();
		
		resumeFilter.initParamValue();
		resumeFilter.isReload = true;
		resumeFilter.init();
	},
	//专门用于处理年限和其他日期格式的显示框  types：numberfield，datefield
	createitems:function(divId,itemdesc,types,flag,itemid,queryvalue1,queryvalue2){
		var value = '';
		var descString = '';
		if(flag==2){
			value='1';
			boolean = true;
			descString="(年限)"
		}else{
			if(flag=="1"){
				value = "0";
				boolean = false;
			}else if(flag == "3"){
				value = "2";
				boolean = true;
				descString="(月)"
			}else if(flag == "4"){
				value = "3";
				boolean = true;
				descString="(日)"
			}
		}
		field = Ext.widget(types,{
			fieldLabel:itemdesc,
			labelSeparator:null,
			labelWidth:130,
			width:280,
			name:itemid,
			minHeight:10,
			maxLength:280,
			id:'date'+divId,//日期（数值）型指标 赋值id
			hideTrigger:boolean,//数值型指标需要隐藏自带的按钮
			cls:'hj-zm-cj-zwmc',
			style:'margin-top:10px;',
			allowExponential:false,
			value:Ext.isEmpty(queryvalue1) ? '' : queryvalue1,
			listeners:{
			 	blur:{//校验截止值大于起始值
			 		fn:function(comp){
			 			var id = comp.getId();
			 			if(types == "datefield")
			 				resumeFilter.validate(id,id+"1","D","start");
			 			else if(types == "numberfield")
			 				resumeFilter.validate(id,id+"1","N","start");
		 			}
		 		}
		 	}
		});
		
		field.setFieldLabel(field.fieldLabel+descString+"<a href='javascript:void(0);'  onclick='resumeFilter.generateDateTypes(\""+divId+"\",\""+itemdesc+"\",\""+itemid+"\",\""+queryvalue1+"\",\""+queryvalue2+"\")'>" 
				+"<img dropdownName='dropdownBox' id='img"+divId+"' class='img-middle'	src='/module/recruitment/image/jiantou.png' style='margin-left:5px'	/></a>" 
				+"<div id='div"+divId+"' style='width:0px;height:3px;border:0px;background-color:red;float:left;'></div>");
		var field1 = Ext.widget(types,{
    	    labelSeparator:null,
			maxLength:250,
			name:itemid,
			id:"date"+divId+"1",
			hideTrigger:boolean,//数值型指标需要隐藏自带的按钮
			cls:'hj-zm-cj-zwmc',
			style:'margin-top:10px',
			value:Ext.isEmpty(queryvalue2) ? '' : queryvalue2,
			listeners:{
			 	blur:{//校验截止日期大于起始日期
			 		fn:function(comp){
			 			var id = comp.getId();
			 			if(types == "datefield")
			 				resumeFilter.validate(id.substring(0,id.length-1),id,"D","end");
			 			else if(types == "numberfield")
			 				resumeFilter.validate(id.substring(0,id.length-1),id,"N","end");
		 			}
		 		}
		 	}
		});
		var items = [field,
		         {	
					border:false,
					style:'margin-left:5px;padding-left:5px;padding-top:12px;margin-top:10px',
					html:'至'
		         },
		         {
					xtype:'textfield',
					id:'datetype'+divId,
					name:itemid+'datetype',
					value:value,
					hidden:true
				 },
				 field1];
		return items;
	}
});