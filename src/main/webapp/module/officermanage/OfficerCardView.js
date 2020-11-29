Ext.Loader.loadScript({ url: '/module/officermanage/MonthPicker.js' })
//Ext.Loader.loadScript({ url: '/jquery/jquery-1.8.0.min.js' })
/**
 * 干部任免表显示界面组件
 * 根据传入数据创建左侧人员列表，
 * 加载人员数据，返回指定格式数据用于保存操作
 *
 * */
Ext.define('OfficerMange.CardView', {
  extend: 'Ext.panel.Panel',
  alias: 'widget.officerCardView',
  requires: ['EHR.extWidget.field.CodeTreeCombox','EHR.extWidget.field.BigTextField'],
  data: undefined,
  idArry: [],
  width: '100%',
  height: '100%',
  layout: 'fit',
  closePanelId:undefined,
  fieldMap:undefined,//干部任免表对应系统代码项
  cardType: undefined,
  refreshed:undefined,//非编辑模式下默认进入刷新
  searchValue: '', // 快速查询内容
  constructor: function(config) {
    this.callParent()
    Ext.apply(this, config)
    cardView = this
    if(this.closePanelId){
    	this.setTitle('<div><div style="float:left">干部任免审批表</div>'+
    	  		'<div onclick="cardView.closePanel()" title="关闭" style="float:right;width:17px;height:17px;cursor:pointer;background:url(/module/serviceclient/images/close_mouseover.png)  no-repeat 0px 0px;"></div></div>')
    }else{
    	this.setTitle('干部任免审批表')
    }
    
    this.idArry = ['a0101', 'sex', 'birthdate', 'photo', 'nation', 'nativeplace',
      'birthplace', 'joinpartydate', 'joinjobdate', 'health', 'majorpost',
      'majorspecialty', 'currentpost', 'preparepost', 'terminalpost',
      'rewardsandpenalties', 'assessment', 'postreason', 'id_number',
      'education', 'degree', 'school', 'educationmajor','report_unit']
    this.createCssStyle()
    if (this.cardType === 'edit') { // 编辑状态下显示工具栏
      this.createtools('batch')// 工具栏
    } else {
      this.createtools()// 工具栏
    }

    this.createMainPanel()// 主面板
  },
  closePanel:function(){
	  var me=this
	  if(me.closePanelId){
		  Ext.getCmp(me.closePanelId).destroy()
	  }
  },
  getRenderData: function(data) { // 查看模式下渲染卡片
    var me = this
    var map = new HashMap()
    if (!data) {
      map.put('guidkey', this.data.guidkey)
      map.put('nbase', this.data.nbase)
      map.put('A0100', this.data.A0100)
    } else {
      this.data={"guidkey":data.guidkey,"nbase":data.nbase,"A0100":data.A0100}
      map.put('guidkey', data.guidkey)
      map.put('nbase', data.nbase)
      map.put('A0100', data.A0100)
    }
    map.put('flag', 'search')
    Rpc({ functionId: 'OM000000006', async: false, success: function(res) {
      var res = Ext.decode(res.responseText)
      if (res.flag) {
    	this.fieldMap=res.fieldMap
        this.renderData(res.data)
      } else {
        Ext.showAlert(res.errMsg)
      }
    }, scope: me }, map)
  },
  showDataPanel:function(id){
	  var me=this
	  var value
	  var date_value=document.getElementById(id+"_input").value
	  if(date_value){
		  value=[parseInt(date_value.substring(4,6))-1,parseInt(date_value.substring(0,4))]
	  }else{
		  value=new Date()
	  }
	  if(Ext.getCmp("select_month_id")){
		  Ext.getCmp("select_month_id").destroy();
	  }
		//选择月份
	  var month=Ext.create({
				    xtype: 'monthpicker',
				    shadow:false,
				    id:'select_month_id',
				    renderTo: Ext.getBody(),
				    floating:true,
				    value:value,
				    onSelect: function() {
				    	var date=this.value[1]+"";
				    	if(this.value[0]+1<10){
				    		date+='0'+(this.value[0]+1)+""
				    	}else{
				    		date+=(this.value[0]+1)+""
				    	}
				    	var flag=false
				    	if(id==='birthdate'){
				    		var year="";
				    		var d_year = new Date().getFullYear()// 系统当前年月
				            var d_month = new Date().getMonth()
				             if (d_month - this.value[0] < 0) {
					          year = d_year - this.value[1] - 1
					        } else {
					          year = d_year - this.value[1]
					        }
				    		if(year<18||year>=100){
				    			flag=true
				    			year=""
				    		}
				    		document.getElementById("birthdate_year").innerHTML="（"+year+"岁）"
				    		
				    	}
				    	document.getElementById(id+"_input").value=date;
					    this.destroy();
					    me.inputBlur(id);
					    if(flag){
					    	Ext.showAlert(common.label.validateYear)
					    }
				    },
				    listeners: {
				        okclick: 'onSelect',
				        monthdblclick: 'onSelect',
				        yeardblclick: 'onSelect',
				        cancelclick: function () {
				            this.destroy();
				        }
				    }
			});
	  month.alignTo(Ext.get(id),'tl-bl?',undefined);
	  Ext.getDoc().on("mousedown",function(e,t,o){//添加监听事件 鼠标点击当前对象外区域自动销毁对象
	    	if(month&&!month.owns(t)){
	    		month.destroy()
	    	}
		 },month);
  },
  renderData: function(data) {
    var arry = this.idArry
    for (var i = 0; i < arry.length; i++) {
      if (arry[i] === 'birthdate') { // 出生日期
        Ext.get(arry[i]).setHtml('')
        if (data[arry[i]]==undefined||data[arry[i]] === '') {
          var ss = '<div style="width:100%;height:50%" id="birthdate_data" >' +
						'<div style="width:100%;height:100%;" id="birthdate_D">'+
						 	'<font id="birthdate_value" style="margin:0;text-align: center;vertical-align: middle"></font>'+
						 '</div>' +
					'</div>' +
					'<div style="width:100%;height:50%;valign:bottom" id="birthdate_year">（       岁）</div>'
          Ext.get(arry[i]).insertHtml('beforeEnd', ss)
          continue
        }
        var d_year = new Date().getFullYear()// 系统当前年月
        var d_month = new Date().getMonth() + 1
        var d_date = new Date().getDate()
        var b_year = data[arry[i]].replace(/-/g, '').substring(0, 4)
        var b_month = data[arry[i]].replace(/-/g, '').substring(4, 6)
        var year = ''
        if (d_month - b_month < 0) {
          year = d_year - b_year - 1
        } else {
          year = d_year - b_year
        }
        if (d_month < 10) { 
        	d_month = '0' + d_month 
        }
        if(d_date<10) {
        	d_date='0'+d_date
        }
        Ext.get('yearData').setHtml('')//计算年龄日期
        Ext.get('yearData').insertHtml('beforeEnd', d_year + '' + d_month + '' + d_date)
        var ss = '<div style="width:100%;height:50%" id="birthdate_data" >' +
					'<div style="width:100%;height:100%;line-height:25px" onclick="cardView.cellclick(\''+"birthdate"+'\')" id="birthdate_D">'+
						'<font id="birthdate_value" style="text-align: center;vertical-align: middle">' + data[arry[i]].replace(/-/g, '').substring(0, 6) + '</font>'+
						'<input style="display:none;width:100%;height:100%;cursor:pointer;border:none;" id="birthdate_input" onblur="cardView.inputBlur(\''+"birthdate"+'\')" value='+data[arry[i]].replace(/-/g, '').substring(0, 6)+'  onclick="cardView.showDataPanel(\''+"birthdate"+'\')"	>'+
					'</div>' +
				'</div>' +
				'<div style="width:100%;height:50%;valign:bottom" id="birthdate_year">（' + year + '岁）</div>'
        Ext.get(arry[i]).insertHtml('beforeEnd', ss)
      } else if (arry[i] === 'photo') {
    	  
        Ext.get('photo').setHtml('')
        Ext.get('photo').insertHtml('beforeEnd', "<div style=\"margin:-9px 0;padding:0;border:0;width:100%;height:100%;min-width:145px;min-height:190px\"><img style=\"width:100%;height:100%;min-height: 190px;\" src='" + data[arry[i]] + "'/></div>")
        
      } else if (arry[i] === 'education' || arry[i] === 'degree' ||
					arry[i] === 'school' || arry[i] === 'educationmajor') {
        Ext.get(arry[i] + '_full').setHtml('')
        Ext.get(arry[i] + '_work').setHtml('')
        var text_align = "center";
        if(arry[i] === 'school'||arry[i] === 'educationmajor'){
        	text_align = "left";
        }
        Ext.get(arry[i] + '_full').insertHtml('beforeEnd', '<font id="' + arry[i] + '_full_value" style="margin:0;display:table-cell;text-align:'+text_align+';vertical-align: middle;">' + ((data[arry[i]] !== undefined && data[arry[i]] !== '') ? data[arry[i]].split('`')[0] : '') + '</font>')
        Ext.get(arry[i] + '_work').insertHtml('beforeEnd', '<font id="' + arry[i] + '_work_value" style="margin:0;display:table-cell;text-align:'+text_align+';vertical-align: middle;">' + ((data[arry[i]] !== undefined && data[arry[i]] !== '') ? data[arry[i]].split('`')[1] : '') + '</font>')
        if(arry[i] !== 'school'&&arry[i] !== 'educationmajor'){
        	Ext.get(arry[i] + '_full').insertHtml('beforeEnd', "<input  id=\"" + arry[i] + '_full_input" value="' + ((data[arry[i]] !== undefined && data[arry[i]] !== '') ? data[arry[i]].split('`')[0] : '') + '" style="display:none;text-align:left;width:100%;border:none;height:100%;cursor:pointer;"/>')
        	Ext.get(arry[i] + '_work').insertHtml('beforeEnd', "<input  id=\"" + arry[i] + '_work_input" value="' +  ((data[arry[i]] !== undefined && data[arry[i]] !== '') ? data[arry[i]].split('`')[1] : '') + '" style="display:none;text-align:left;width:100%;border:none;height:100%;cursor:pointer;"/>')
        }
      } else {
        Ext.get(arry[i]).setHtml('')
        var value = ''
        value = data[arry[i]]?data[arry[i]]:""
    	if(arry[i]==='birthplace'||arry[i]==='nativeplace'){
    		value = value.replace(/省/g,"").replace(/市/g,"");
        }
        if (arry[i] === 'joinjobdate') {
          value = value.replace(/-/g, '').substring(0, 6)
        }else if(arry[i]==='a0101'){
        	if(value.length==2){
        		value=value[0]+"&nbsp;"+value[1];
        	}
        }
        if (arry[i] === 'majorpost' || arry[i] === 'majorspecialty' || arry[i] === 'currentpost' ||
				 arry[i] === 'preparepost' || arry[i] === 'terminalpost' || arry[i] === 'rewardsandpenalties' ||
				 arry[i] === 'assessment' || arry[i] === 'postreason') {
          Ext.get(arry[i]).insertHtml('beforeEnd', '<font id="' + arry[i] + '_value" style="margin:0;text-align:center">' + value.replace(/\\r/g, '&nbsp;&nbsp;&nbsp;&nbsp;').replace(/\\n/g, '<br>') + '</font>')
        } else {
        	if(arry[i]==='birthplace'||arry[i]==='nativeplace'){
        		Ext.get(arry[i]).insertHtml('beforeEnd', '<font id="' + arry[i] + '_value" style="margin:0;text-align:left;font-size:16px">' + value + '</font>')
        	}else{
        		Ext.get(arry[i]).insertHtml('beforeEnd', '<font id="' + arry[i] + '_value" style="margin:0;text-align:center;font-size:16px">' + value + '</font>')
        	}
        }
        if(arry[i]==='sex'||arry[i]==='nation'||this.fieldMap[arry[i]]){															
        	Ext.get(arry[i]).insertHtml('beforeEnd', "<input  id=\"" + arry[i] + "_input\" onblur=\"cardView.inputBlur('"+arry[i]+"')\" value=\"" + value + "\" style=\"display:none;text-align:center;width:100%;border:none;height:100%;cursor:pointer;\"/>")
        }else if(arry[i] === 'joinjobdate'){
        	Ext.get(arry[i]).insertHtml('beforeEnd', '<input  style="display:none;width:100%;height:100%;cursor:pointer;border:none;" id="'+arry[i]+'_input" onblur="cardView.inputBlur(\''+arry[i]+'\')" value='+value+'  onclick="cardView.showDataPanel(\''+arry[i]+'\')"	>')
        }
      }
    }

    this.renderResume(data.resume)
    this.renderfamily(data.family)
  },
  showCodeSelect:function(inputId,id){
	var me=this
	var codeSet=this.fieldMap[id]
	var code=Ext.create('OfficerMange.OfficerCodeSelect',{
			    renderId:inputId,
			    codeSet:codeSet,
			    callbackFn:"cardView.inputBlur('"+id+"')",
			    renderTo: Ext.getBody()
			})
		code.alignTo(Ext.get(id),'tl-bl?',undefined);
	   if(codeSet&&codeSet!==''){
		   Ext.get(inputId).on('keyup',function(e,t){
			  var value=t.value
			  var store = Ext.data.StoreManager.lookup('officer_codeSelect_store')
			  if(value==undefined){
				  value=''
			  }
			  store.getProxy().setExtraParams({
											    codesetid  : codeSet,
								            	codesource : '',
								            	nmodule    : "8",
								            	ctrltype   : "3",
								            	parentid   : '',
								            	searchtext : value!==''?encodeURI(value):'',
								            	onlySelectCodeset:'',
								            	multiple:false,
								            	isShowLayer:'1',
								            	isHideTip:true
								            })
			  store.load();
		   },me)
	   }
  },
  renderfamily: function(family) { // 渲染家庭主要成员
	var store=Ext.create('Ext.data.Store', {
	    storeId: 'familyStore',
	    fields:[ 'appellation', 'name', 'birthdate','status','workUnit']
	});
	for (var i = 0; i < 10; i++) {
    	if(!family[i]||family[i]===''){
    		family[i]={ChengWei:'',XingMing:'',ChuShengRiQi:'',ZhengZhiMianMao:'',GongZuoDanWeiJiZhiWu:'',};
    	}
    	store.add({"ChengWei":family[i].ChengWei,"XingMing":family[i].XingMing,"ChuShengRiQi":family[i].ChuShengRiQi,"ZhengZhiMianMao":family[i].ZhengZhiMianMao,"GongZuoDanWeiJiZhiWu":family[i].GongZuoDanWeiJiZhiWu})
    	this.familyHtml(i + 1,family[i]);

      }
  },
  familyHtml:function(index,family_obj){
      Ext.get('ChengWei' + (index)).setHtml('')
      Ext.get('XingMing' + (index)).setHtml('')
      Ext.get('ChuShengRiQi' + (index)).setHtml('')
      Ext.get('ZhengZhiMianMao' + (index)).setHtml('')
      Ext.get('GongZuoDanWeiJiZhiWu' + (index)).setHtml('')
      var name=family_obj.XingMing
      if(name.length==2){
    	  name=name[0]+"&nbsp;"+name[1]
      }
      Ext.get('ChengWei' + (index)).insertHtml('beforeEnd', '<font id="' + 'ChengWei' + (index) + '_value" style="margin:0;text-align:center">' + family_obj.ChengWei+ '</font>')
      Ext.get('XingMing' + (index)).insertHtml('beforeEnd','<font id="' + 'XingMing' + (index) + '_value" style="margin:0;text-align:center">' + name + '</font>' )
      Ext.get('ChuShengRiQi' + (index)).insertHtml('beforeEnd','<font id="' +'ChuShengRiQi' + (index)+ '_value" style="margin:0;text-align:center">' + family_obj.ChuShengRiQi+ '</font>' )
      Ext.get('ZhengZhiMianMao' + (index)).insertHtml('beforeEnd','<font id="' + 'ZhengZhiMianMao' + (index) + '_value" style="margin:0;text-align:center">' + family_obj.ZhengZhiMianMao+ '</font>' )
      Ext.get('GongZuoDanWeiJiZhiWu' + (index)).insertHtml('beforeEnd','<font id="' + 'GongZuoDanWeiJiZhiWu' + (index) + '_value" style="margin:0;text-align:center">' + family_obj.GongZuoDanWeiJiZhiWu+ '</font>' )

  },
  renderResume: function(resume) { // 简历渲染显示
    Ext.get('resume').setHtml('')
    if(resume==undefined){
		Ext.get('resume').insertHtml('beforeEnd', '')
		return 
	}
   
    resume =Ext.decode(resume) //resume.split(/\\n/g)
	var store=Ext.create('Ext.data.Store', {
				    storeId: 'resumeStore',
				    fields:[ 'startDate', 'endDate', 'desc']
				});
    for (var i = 0; i < resume.length; i++) {
      //resume[i]=resume[i].replace(/\\n/,'<br>')
      var date = resume[i].substring(0, 16)
      var startDate = ''
      var endDate = ''
      if (date.indexOf('--') > -1) { // 有日期存在
        startDate = date.substring(0, date.indexOf('--'))
        endDate = date.substring(date.indexOf('--') + 2, date.length)
      }
      var desc = resume[i].substring(16, resume[i].length)
      store.add({"startDate":startDate,"endDate":endDate,"desc":desc.replace(/^\s+|\s+$/g, "")})
      desc=desc.replace(/\n/g,'<br>')
      this.resumeHtml(startDate,endDate,desc)
    }
  },
  resumeHtml:function(startDate,endDate,desc){
	  desc=desc.replace("（","(").replace("）",")").replace("：",":").replace(/<br>/g,'')
	  var desc_='';
	  if(!(startDate==""||endDate=="")){
		  if(desc.indexOf("(其中:")>-1){
			 var start_des=desc.split("(其中:")[0]+"<br>";
			 var content_des= "(其中:"+desc.split("(其中:")[1]
			 desc_=start_des+content_des;
		  }else if(desc.indexOf("(其间:")>-1){
			  var start_des=desc.split("(其间:")[0]+"<br>";
			  var content_des= "(其间:"+desc.split("(其间:")[1]
			  desc_=start_des+content_des;
		  }else{
			  desc_=desc;
		  }
	  }else{
		  desc_=desc;
	  }
	  var html = '<div style="width:100%;min-height:20px;margin-top:0px;">' +
		'<table width="100%" height="100%" style="border:none !important;font-size:16px;font-family:宋体">' +
			'<tr style="height:20px;border:none !important">' +
				'<td width="57px" valign="top" align="right" style="border:none !important;min-Width:55px;padding-right:0px" >' + startDate + '</td>' +
				(startDate === '' ? '<td width="23px" valign="top" align="center" style="border:none !important;min-Width:23px" ></td>' : '<td width="20px" valign="top" align="center" style="border:none !important" >--</td>') +
				'<td width="57px" valign="top" align="left" style="border:none !important;min-Width:55px;padding-left:0px" >' + endDate + '</td>' +
				'<td width="465px" valign="top" style="text-align:left;border:none !important;">' + desc_ + '</td>' +
			'</tr>' +
		'</table>' +
		'</div>'
	 Ext.get('resume').insertHtml('beforeEnd', html)
  },
  resumeBtnTool:function(){
	 var me=this
	 var toolbar=[
			 		{
			 			text:common.label.addBtn,
			 			handler:function(){
			 				var store=Ext.data.StoreManager.lookup('resumeStore')
			 				store.add({"startDate":"","endDate":"","desc":""})
			 			}
			 		},
			 		{
			 			text:common.button.insert,
			 			handler:function(){
			 				var panel=Ext.getCmp("grid_panel_sub")
			 				var records=panel.getSelection()
			 				var store=Ext.data.StoreManager.lookup('resumeStore')
			 				var index=0
			 				if(records.length>0){
			 					index=store.indexOf(records[0])
			 				}
			 				store.insert(index,{"startDate":"","endDate":"","desc":""})
			 			}
			 		},
			 		{
			 			text:common.label.deleteBtn,
			 			handler:function(){
			 				var store=Ext.data.StoreManager.lookup('resumeStore')
			 				var panel=Ext.getCmp("grid_panel_sub")
			 				store.remove(panel.getSelection())
			 			}
			 		},
			 		{
			 			text:common.label.save,
			 			handler:function(){
			 				//保存前先清除简历区域上次内容
			 				var resume=document.getElementById('resume')
			 				while(resume.hasChildNodes()){
			 					resume.removeChild(resume.lastChild)
			 				}
			 				//清除内容后重新添加子元素
			 				var store=Ext.data.StoreManager.lookup('resumeStore')
			 				for(var i=0;i<store.getCount();i++){
			 					var desc=store.getAt(i).get('desc').replace(/\n/g,'\n')
			 					store.getAt(i).commit()
			 					var startDate=store.getAt(i).get('startDate')
			 					var endDate=store.getAt(i).get('endDate')
			 					desc=desc.replace(/\n/g,'<br>')
			 					if(startDate!==''||endDate!==''||desc!==''){
			 						me.resumeHtml(startDate,endDate,desc)
			 					}
			 				}
			 			}
			 		}
		 		]
	 return toolbar
  },
  showGridPanel:function(id){//简历双击弹出编辑框
	  if(this.cardType!= 'batch'&&this.cardType!='edit'){
		  return
	  }
	  var me=this
	  var columns;
	  var store;
	  var title;
	  if(id==='resume'){
		  title='简历编辑'
	  }else{
		  title='家庭成员编辑'
	  }
	  if(id==='resume'){
		  store= Ext.data.StoreManager.lookup('resumeStore')
		  columns=[
		        { 
		        	text: common.label.startDate, 
		        	align:'center',
		        	dataIndex: 'startDate',
		        	sortable:false,
		        	hideable:false,
		        	editor: {
		                xtype: 'customMonthPicker'
				    } 
		        },
		        { 
		        	text: common.label.endDate, 
		        	align:'center',
		        	sortable:false,
		        	hideable:false,
		        	dataIndex: 'endDate',
		        	editor: {
		                xtype: 'customMonthPicker'
		        	} 
		        },
		        { 
		        	text: common.label.descText, 
		        	dataIndex: 'desc',
		        	sortable:false,
		        	hideable:false,
		        	editor: {
		                xtype:'bigtextfield',
		                width:'100%'
		        	},
		        	renderer:function(value){
		        		return value.replace(/</g,'&lt;').replace(/\n/g,'<br>').replace(/\s+/g,'&nbsp;')
		        	},
		        	flex: 1 
		        }
		    ]
	  }else{
		  store= Ext.data.StoreManager.lookup('familyStore')
		  columns=[
		        { 
		        	text: "称谓", 
		        	align:'center',
		        	dataIndex: 'ChengWei',
		        	sortable:false,
		        	hideable:false,
		        	editor: {
		                xtype: 'comFieldItemSelect',
		                codesetId:'ChengWei',
		                customTree:true
				    } 
		        },
		        { 
		        	text: "姓名", 
		        	align:'center',
		        	sortable:false,
		        	hideable:false,
		        	dataIndex: 'XingMing',
		        	editor: {
		        		 xtype: 'textfield',
		                 allowBlank: true
		        	} 
		        },
		        { 
		        	text: "出生日期", 
		        	align:'center',
		        	dataIndex: 'ChuShengRiQi', 
		        	sortable:false,
		        	hideable:false,
		        	editor: {
		                xtype:'customMonthPicker',
		                format:'Ym'
		               
		        	}
		        },
		        { 
		        	text: "政治面貌", 
		        	align:'center',
		        	dataIndex: 'ZhengZhiMianMao',
		        	sortable:false,
		        	hideable:false,
		        	editor: {
		        		xtype: 'comFieldItemSelect',
		        		codesetId:'ZhengZhiMianMao',
		                customTree:true
		        	}
		        },
		        { 
		        	text: "工作单位及职务", 
		        	dataIndex: 'GongZuoDanWeiJiZhiWu',
		        	sortable:false,
		        	hideable:false,
		        	editor: {
		                xtype: Ext.widget('bigtextfield',
		                		{isFilterSpecialKey:true}),
		                width:'100%'
		        	}, 
		        	flex: 1 
		        }
		    ]
	  }
	  Ext.util.CSS.createStyleSheet(".x-grid-cell-inner{max-height:60px}");
	  var gridpanel=Ext.create('Ext.grid.Panel', {
					    store:store,
					    columnLines:true,
					    id:'grid_panel_sub',
					   	tbar:(id==='resume'?this.resumeBtnTool():''),
					   	rowLines:true,
					    selModel: {
					          selType:(id==='resume'?'checkboxmodel':'')
					    },
					    plugins: {
					    	ptype: 'cellediting',
					        clicksToEdit: 1
					    },
					    viewConfig: {
							plugins: {
								ptype: "gridviewdragdrop",
								dragText: "可用鼠标拖拽进行上下排序"
							}
						},
					    columns: columns,
					    listeners: {
					    render: function(panel) {
			    		 var view = gridpanel.getView()
			    		 var tips = Ext.create('Ext.tip.ToolTip', {
							    target: panel.body,
							    delegate:"td > div.x-grid-cell-inner",
							    shadow:false,
							    trackMouse: true,
							    maxWidth:800,
							    renderTo: Ext.getBody(),
							    bodyStyle:"background-color:white;border:1px solid #c5c5c5;",
							    listeners: {
							        beforeshow: function updateTipBody(tip) {
							        	    var div = tip.triggerElement
							        	    if (Ext.isEmpty(div))
							        	    	return false
								        	if(div.offsetWidth < div.scrollWidth || div.offsetHeight < div.scrollHeight-4){
								        		tip.update("<div style='white-space:nowrap;overflow:hidden;'>"+div.innerHTML+"</div>")
								        	}else
								        		return false
							        }
							    }
				    		})
					    }
					  }
					});
		Ext.widget('window',{
					title:title,
					width:800,
					height:430,
					modal:true,
					layout:'fit',
					resizable:false,
					items:[gridpanel],
					listeners:{
						'beforedestroy':function(e,opt){
							if(id!='resume'){//家庭成员子集关闭后自动保存
								var store= Ext.data.StoreManager.lookup('familyStore')
								for(var i=0;i<store.getCount();i++){
									store.getAt(i).commit();
									var index=i+1
									var family_obj={
													"ChengWei":store.getAt(i).get('ChengWei'),
													"XingMing":store.getAt(i).get('XingMing'),
													"ChuShengRiQi":store.getAt(i).get('ChuShengRiQi'),
													"ZhengZhiMianMao":store.getAt(i).get('ZhengZhiMianMao'),
													"GongZuoDanWeiJiZhiWu":store.getAt(i).get('GongZuoDanWeiJiZhiWu')
												   };
									me.familyHtml(index,family_obj)
			 					}
							}
						}
					}
		}).show()
  },
  createTitleDesc: function(id) {
    if (this.cardType !== 'edit') { return }
    var desc = eval('common.label.' + id)
    var tip = Ext.create('Ext.tip.ToolTip', {
		    target: Ext.getDom(id),
		    trackMouse: true,
		    shadow:false,
			floating:true,
		    bodyStyle: 'background-color:white;border:1px solid #c5c5c5;',
		    html: '<div style="background-color:white;">' + desc + '</div>'
    })
  },
  createCssStyle: function() { // 任免表使用样式
	   if(!!!Ext.util.CSS.getRule(".descCss")){
		   Ext.util.CSS.createStyleSheet('.descCss{font-size:14pt;font-family:宋体;background:#E5E5E5;}')
	   }
	   if(!!!Ext.util.CSS.getRule(".bigTextfieldCss")){
		   Ext.util.CSS.createStyleSheet('.bigTextfieldCss{width:100%;height:100%;font-family:宋体}')
	   }
	   if(!!!Ext.util.CSS.getRule(".inputCssTR")){
		   Ext.util.CSS.createStyleSheet('.inputCssTR{border:1px solid #C5C5C5;font-family:宋体}')
	   } 
	   if(!!!Ext.util.CSS.getRule(".inputCssTD")){
		   Ext.util.CSS.createStyleSheet('.inputCssTD{text-align:center;font-family:宋体;border:1px solid #C5C5C5;font-size:16px}')
	   }
	   if(!!!Ext.util.CSS.getRule(".td1")){
		   Ext.util.CSS.createStyleSheet('.td1{font-size:14pt;font-family:宋体;height:45px;border:1px solid #C5C5C5;width:60px;text-align:center;}')
	   }
	   if(!!!Ext.util.CSS.getRule(".td2")){
		   Ext.util.CSS.createStyleSheet('.td2{font-size:16px;font-family:宋体;height:45px;border:1px solid #C5C5C5;width:90px;text-align:center;border:1px solid #C5C5C5;}')
	   }
	   if(!!!Ext.util.CSS.getRule(".td3")){
		   Ext.util.CSS.createStyleSheet('.td3{font-size:14pt;font-family:宋体;text-align:center;height:45px;border:1px solid #C5C5C5;width:150px;border:1px solid #C5C5C5;}')
	   }
	   if(!!!Ext.util.CSS.getRule(".titleCss")){
		   Ext.util.CSS.createStyleSheet('.titleCss{font-size:14pt;font-family:宋体;text-align:center;border:1px solid #C5C5C5;background:#E5E5E5;}')
	   }
	   if(!!!Ext.util.CSS.getRule(".secPage")){
		   Ext.util.CSS.createStyleSheet('.secPage{width:50px;font-size:14pt;font-family:宋体;border:1px solid #C5C5C5;text-align:center;background:#E5E5E5;}')
	   }
  },
  createMainPanel: function() { // 主面板
    var mainPanel = Ext.create('Ext.container.Container', {
      width: '100%',
      id: 'mainPanel',
      height: '100%',
      layout: 'hbox'
    })
    if (this.cardType === 'edit') { // //编辑状态下显示工具栏
      mainPanel.insert(0, this.createGridPanel())
    }
    mainPanel.add(this.createCardPanel())
    this.add(mainPanel)
  },
  createCardPanel: function(res) { // 创建右侧任免表面板
    var me = this
    var cardPanel = Ext.create('Ext.container.Container', {
      height: '100%',
      id: 'officerCardView_id',
      width: (this.cardType === 'edit' ? '81%' : '100%'),
      scrollable: true,
      html: this.cardHtml(),
      listeners: {
        afterrender: function() {
          if (me.cardType !== 'edit') { // 非编辑模式下 调入
        	if(me.refreshed == "true"){
        		Ext.MessageBox.wait(common.label.refreshMSG, common.label.expWait)
    			me.refresh("0")
        	}else{
        		me.getRenderData()
        	}
          }
          for (var i = 0; i < me.idArry.length; i++) {
            if (me.idArry[i] === 'education' || me.idArry[i] === 'degree' ||
							me.idArry[i] === 'school' || me.idArry[i] === 'educationmajor') { continue }
            me.resizeFonts(me.idArry[i],me.idArry[i] + '_value')
          }
        }
      }
    })
    return cardPanel
  },
  resizeFonts: function(td,value) { // 重新计算字体
    var me = this
    if (!$('#' + td) || !$('#' + value)) { return }
    var $td = $('#' + td)[0].height
	var $content = $('#' + value)
	var size = parseInt($content.css('font-size'))
	if (size <= 12) { return }
	if ($content.height() >= $td) {
		    $content.css('font-size', parseInt($content.css('font-size')) - 1 + 'px')
		    me.resizeFonts(td,value)
	} else {
	    return
	}
  },
  searchPanel: function() { // 快速查询
    var searchText = Ext.create('Ext.form.field.Text', {
      name: 'names',
      width: '90%',
      height: 20,
      id: 'search',
      enableKeyEvents: true,
      emptyText: common.label.cardPersonNameMsg,
      listeners: {// enter 键 查询
		 		specialkey: function(field, e) {
		 			if (e.getKey() == e.ENTER) {
		 				var store = Ext.data.StoreManager.lookup('officerSimpleStore')
		 				store.getProxy().setExtraParams({ 'searchValue': field.lastValue })
		 				store.load({page:1})
          }
		 		}
		 	}
    })
    // 查询图片
    var findImg = Ext.create('Ext.container.Container', {
      width: "10%",
      height: 22,
      border: 0,
      style: 'border:1px solid #c5c5c5;border-right:none;background:url(/images/hcm/themes/gray/search_fdj2.png) no-repeat center left;'
    })
    // 隐藏 输入框左右边框
    Ext.util.CSS.createStyleSheet('#search div{border-left:none !important;border-right:none !important;border-top:none !important;border-bottom:none !important;}')
    var container = Ext.create('Ext.container.Container', {
      width: '100%',
      height:25,
      border: 1,
      style: {
				borderColor: '#C5C5C5',
				borderStyle: 'solid'
      },
      layout: {
        type: 'hbox',
        align: 'center'
      },
      items: [findImg, searchText]
    })
    return container
  },
  createGridPanel: function(res) {
    var me = this
    Ext.define('officerStore', {
		     extend: 'Ext.data.Model',
		     fields: [
		         { name: 'a0101', type: 'string' },
		         { name: 'guidkey', type: 'string' },
		         { name: 'a0101', type: 'string' },
		         { name: 'b0110', type: 'string' },
		         { name: 'e0122', type: 'string' },
		         { name: 'nbase', type: 'string' }
		     ],
		     idProperty: 'record_internalId'
		 })

    Ext.create('Ext.data.Store', {
      id: 'officerSimpleStore',
	  model: 'officerStore',
      autoLoad: true,
      pageSize: 20,
		    fields: ['a0101', 'guidkey', 'a0101', 'nbase', 'b0110', 'e0122'],
		    proxy: {
			    type: 'transaction',
		        timeout: 80000,
		        functionId: 'OM000000008',
		        extraParams: { 'searchValue': '' },
		        reader: {
		            type: 'json',
		            root: 'dataobjs',
		            totalProperty: 'totalCount',
		            idProperty: 'record_internalId'
		        }
      },
      loadPage: function(page, options) {
        var me = this
		            var size = me.getPageSize()
			        me.currentPage = page
			        options = Ext.apply({
			            page: page,
			            start: (page - 1) * size,
			            limit: size,
			            addRecords: !me.getClearOnPageLoad()
			        }, options)

			        me.read(options)
      },
      listeners: {
        load: function(store, records, successful, operation, eOpts) {
          if (records.length > 0) {
            var data = { guidkey: records[0].get('guidkey'), nbase: records[0].get('nbase').split('`')[1], A0100: records[0].get('a0100') }
            me.getRenderData(data)
          }
        }
      }
    })

	 var grid = Ext.create('Ext.grid.Panel', {
		    store: 'officerSimpleStore',
		    id:'officer_panel',
		    columns: [
		        { text: '姓名',
		          dataIndex: 'a0101',
		          sortable:false,
		          hideable:false,
		          width: '90%',
		          renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
		        	var map = new HashMap()
		        	map.put('flag', 'photo')
		        	map.put('nbase', record.get('nbase').split('`')[1])
		        	map.put('A0100', record.get('a0100'))
		        	var url = ''
		        	Rpc({ functionId: 'OM000000006', async: false, success: function(res) {
		      			var rs = Ext.decode(res.responseText)
		      			url = rs.photoUrl
		      		}, scope: this }, map)
		        	  var div = "<div style='width: 100%;height:50px;'>" +
		      		"<div style='margin-top:5px;;width:50px;height:40px;;float:left;border-radius:50%; overflow:hidden;'><img style='width:50px;height:40px;' src='" + url + "'></img></div>" +// 图片
		      		"<div style='width:69%;height:100%;float:right;margin-top:0px'>" +
		      		"<div style='width:100%;height:50%;margin-top:5px'>" + "<font style='color:#434343;;font-weight:bold;font-family:宋体;font-size:11pt;'>" + record.get('a0101') + '</font></div>' +// 姓名
		      		"<div style='width:100%;height:49%;overflow: hidden;text-overflow: ellipsis;white-space: nowrap;'><font color='#555050' >" + record.get('b0110') + '/' + record.get('e0122') + '</font></div>' +// 单位部门
		      		'</div></div>'
		        	  return div
		          } }
		    ], selModel: {
		          selType: 'checkboxmodel',
		          listeners: {
		        	  selectionchange: function(model, selected, eOpts) {
		        		  if (selected.length == 20)// 全选时不加载数据 防止全选时影响加载速度
		 					 { return }
		        		  if (selected && selected.length > 0) {
		        			  var records = selected[selected.length - 1]
		        			  var data = { guidkey: records.get('guidkey'), nbase: records.get('nbase').split('`')[1], A0100: records.get('a0100') }
		        			  me.getRenderData(data)
		        		  }
		        	  }
		          }
		      },
		    height: '100%',
		    minWidth:265,
		    style: 'border-top:none;',
		    dockedItems: [{
		        xtype: 'pagingtoolbar',
		        store: 'officerSimpleStore', // same store GridPanel is using
		        dock: 'bottom',
		        simpleModel: true,
		        displayInfo: false
		    },
		    {
		        xtype: 'toolbar',
		        dock: 'top',
		        style: 'border-top:none',
		        align:'center',
		        items: [this.searchPanel()]// 快速查询框放置上方
		    }
		    ],
		    width: '15%',
		    listeners: {
		    	render: function(panel) {
		    		var view = grid.getView()
		    		 var tips = Ext.create('Ext.tip.ToolTip', {
		    		     target: panel.body,
		    		     delegate: view.itemSelector,
		    		     bodyStyle: 'background-color:white;border:1px solid #c5c5c5;',
		    		     trackMouse: true,
		    		     listeners: {
		    		         beforeshow: function updateTipBody(tip) {
		    		        	 if(view.getRecord(tip.triggerElement)&&view.getRecord(tip.triggerElement).get('nbase')){
		    		        		 tips.update("<div style='white-space:nowrap;overflow:hidden;'>" +
		    		        				 '<div>' + view.getRecord(tip.triggerElement).get('nbase').split('`')[2] + '</div>' +
		    		        		 '</div>')
		    		        	 }else{
		    		        		 return;
		    		        	 }
		    		         }
		    		     }
		    		 })
        }
		    }
    })
	 return grid
  },
  createtools: function(flag) { // 工具栏
	var me=this
    var btnSave=Ext.widget('button',{
    	width:100,
    	icon:'/images/save_edit.gif',
    	iconCls: 'btn-Img-icon',
    	text:common.label.save,
    	handler:function(){
    		me.saveData()
    	}
    })
    var btnPdf=me.createPdfBtn(flag)
    var btnWord=me.createWordBtn(flag)
    var btnLRMX=me.createLrmxBtn(flag)
    var refreshBtn=me.createRefreshBtn(flag)
    
    var toolbar = Ext.create('Ext.toolbar.Toolbar', {
      width: '100%',
      height: 40
    })
    if (flag === 'batch') {
      toolbar.add(btnSave)
      toolbar.add('-')
      toolbar.add(btnLRMX)
      toolbar.add('-')
      toolbar.add(btnWord)
      toolbar.add('-')
      toolbar.add(btnPdf)
      toolbar.add('-')
      toolbar.add(refreshBtn)
      toolbar.add(this.changeViewBtn())
    } else {
      toolbar.add(btnLRMX)
     // toolbar.add({text:common.label.save});
	  toolbar.add('-')
	  toolbar.add(btnWord)
	  toolbar.add('-')
	  toolbar.add(btnPdf)
	  toolbar.add('-')
	  toolbar.add(refreshBtn)
    }
	
    this.addDocked(toolbar)
  },
  changeViewBtn:function(){//横排 竖排切换按钮
	  var btn = Ext.create('Ext.Button', {
				width: 100,
				text: common.label.virView,
				//icon: '/images/outword.png',
				//iconCls: 'btn-Img-icon',
				handler: function() {
					if(this.text===common.label.virView){
						document.getElementById("cardView_dom").style.width="100%"
						this.setText(common.label.horView)
					}else{
						document.getElementById("cardView_dom").style.width="1340px"
						this.setText(common.label.virView)
					}
				}
	  })
	  return btn
  },
  createWordBtn:function(flag){
	var me=this
    var currentPdf = common.button.cardOneFile.replace('{0}', 'PDF').replace('{1}', (this.inforkind == '2') ? common.button.cardOrg : common.button.cardPerson)// 当前人员生成pdf
    var allPdf = common.button.cardAllFiles.replace('{0}', 'PDF').replace('{1}', (this.inforkind == '2') ? common.button.cardOrg : common.button.cardPerson)// 全部人员生成PDF
    var partPersonPdf = common.button.cardFiles.replace('{0}', 'PDF').replace('{1}', (this.inforkind == '2') ? common.button.cardOrg : common.button.cardPerson)// 部分人员生成PDF
    var onePersonOneDoc = common.button.cardDocument.replace('{0}', (this.inforkind == '2') ? common.button.cardOneOrgFile : common.button.cardPersonFile)// 一人一文档
    var allPersonDoc = common.button.cardDocument.replace('{0}', (this.inforkind == '2') ? common.button.cardAllOrgFile : common.button.cardAllPersonFile)// 多人一文档
    var currentWord = currentPdf.replace('PDF', 'WORD')// 当前人员生成Word
    var allWord = allPdf.replace('PDF', 'WORD')// 全部人员生成Word
    var partPersonWord = partPersonPdf.replace('PDF', 'WORD')// 部分人员生成Word
    var menusWord = Ext.create('Ext.menu.Menu', {
		width: 140,
	    plain: true,
	    floating: true,
	    items: [
		   {
			   text: currentWord,
			   handler: function() {
				   Ext.MessageBox.wait(common.label.expMSG, common.label.expWait)
				   me.outFile("word","exp_current","1")
			   }
		   },
		   {
			   text: partPersonWord,
			   menu: [
					   {
			              text: onePersonOneDoc,
			              handler: function() {
			            	  Ext.MessageBox.wait(common.label.expMSG, common.label.expWait)
						      me.outFile("word","false","1")
						   }
					   },
					   {
			              text: allPersonDoc,
			              handler: function() {
			            	  Ext.MessageBox.wait(common.label.expMSG, common.label.expWait)
						      me.outFile("word","false","all")
						     }
						    }
					    ]
			   },
			 {
			   text: allWord,
			   menu: [
				   {
		              text: onePersonOneDoc,
		              handler: function() {
		            	  Ext.MessageBox.wait(common.label.expMSG, common.label.expWait)
				          me.outFile("word","all","1")
					   }
				   },
				   {
		              text: allPersonDoc,
		              handler: function() {
		            	    Ext.MessageBox.wait(common.label.expMSG, common.label.expWait)
				        	me.outFile("word","all","all")
					    }

				   }
				    ]
		   }
			   ]
})
	var btnWord = Ext.create('Ext.Button', {
				width: 100,
				text: common.button.toexport + 'WORD',
				icon: '/images/outword.png',
				iconCls: 'btn-Img-icon',
				menu: (flag === 'batch'||flag==='edit')? menusWord : '',
				handler: function() {
					if (flag !== 'batch'&&flag!=='edit') {
						Ext.MessageBox.wait(common.label.expMSG, common.label.expWait)
						me.outFile("word","exp_current","1")
					}
				}
	})
	return btnWord
  },
  createPdfBtn:function(flag){
	var me=this
    var currentPdf = common.button.cardOneFile.replace('{0}', 'PDF').replace('{1}', (this.inforkind == '2') ? common.button.cardOrg : common.button.cardPerson)// 当前人员生成pdf
    var allPdf = common.button.cardAllFiles.replace('{0}', 'PDF').replace('{1}', (this.inforkind == '2') ? common.button.cardOrg : common.button.cardPerson)// 全部人员生成PDF
    var partPersonPdf = common.button.cardFiles.replace('{0}', 'PDF').replace('{1}', (this.inforkind == '2') ? common.button.cardOrg : common.button.cardPerson)// 部分人员生成PDF
    var onePersonOneDoc = common.button.cardDocument.replace('{0}', (this.inforkind == '2') ? common.button.cardOneOrgFile : common.button.cardPersonFile)// 一人一文档
    var allPersonDoc = common.button.cardDocument.replace('{0}', (this.inforkind == '2') ? common.button.cardAllOrgFile : common.button.cardAllPersonFile)// 多人一文档
    var currentWord = currentPdf.replace('PDF', 'WORD')// 当前人员生成Word
    var allWord = allPdf.replace('PDF', 'WORD')// 全部人员生成Word
    var partPersonWord = partPersonPdf.replace('PDF', 'WORD')// 部分人员生成Word
	    var menusPdf = Ext.create('Ext.menu.Menu', {
			   width: 125,
			   plain: true,
			   floating: true,
			   items: [
				   {
					   text: currentPdf,
					   handler: function() {
						    Ext.MessageBox.wait(common.label.expMSG, common.label.expWait)
				        	me.outFile("pdf","exp_current","1")
					   }
				   },
				   {
					   text: partPersonPdf,
					   menu: [
						    {
						     text: onePersonOneDoc,
							 handler: function() {
								 Ext.MessageBox.wait(common.label.expMSG, common.label.expWait)
						         me.outFile("pdf","false","1")
							   }
						   },
						   {
							 text: allPersonDoc,
							 handler: function() {
								 Ext.MessageBox.wait(common.label.expMSG, common.label.expWait)
						         me.outFile("pdf","false","all")	
							   }	

						   }
					        ]
				   },
				   {
					   text: allPdf,
					   menu: [
						   {
							   text: onePersonOneDoc, 
							   handler: function() {
								    Ext.MessageBox.wait(common.label.expMSG, common.label.expWait)
						        	me.outFile("pdf","all","1")
							   }
						   },
						   { 
							   text: allPersonDoc,
							   handler: function() {
								    Ext.MessageBox.wait(common.label.expMSG, common.label.expWait)
						        	me.outFile("pdf","all","all")
							   }

						   }
						    ]
				   }
				   ]
 })


	 var btnPdf = Ext.create('Ext.Button', {
				      width: 100,
				      text: common.button.toexport + 'PDF',
				      icon: '/images/outpdf.png',
				      iconCls: 'btn-Img-icon',
				      menu: (flag === 'batch'||flag==='edit')?menusPdf : '',
				      handler: function() {
				        if (flag !== 'batch'&&flag!=='edit') {
				        	Ext.MessageBox.wait(common.label.expMSG, common.label.expWait)
				        	me.outFile("pdf","exp_current","1")
				        }
				      }
	})
 	return btnPdf
  },
  createLrmxBtn:function(flag){
	  var me=this
	  var menuLRMX=Ext.widget('menu',{
	    	width: 125,
		    plain: true,
		    floating: true,
		    items:[
		    	{
		    		text:common.button.outIndex,
		    		handler:function(){
		    		  Ext.MessageBox.wait(common.label.expMSG, common.label.expWait)
		      		  me.outFile("xml","exp_current")
		    		}
		    	},
		    	{
		    		text:common.button.outPart,
		    		handler:function(){    
		    			Ext.MessageBox.wait(common.label.expMSG, common.label.expWait)
		    			me.outFile("xml","false")
		    		}
		    	},
		    	{
		    		text:common.button.outAll,
		    		handler:function(){
		    			Ext.MessageBox.wait(common.label.expMSG, common.label.expWait)
		    			me.outFile("xml","all")
		    		}
		    	}
		    ]
	    })
	var btnLRMX = Ext.create('Ext.Button', {
					width: 100,
					icon:'../../images/export.gif',
			    	iconCls: 'btn-Img-icon',
					text:common.label.outLRMXBtn,
					menu: (flag === 'batch'||flag==='edit')?menuLRMX:'',
					handler: function() {
						if(flag !== 'batch'&&flag!=='edit'){
							  Ext.MessageBox.wait(common.label.expMSG, common.label.expWait)
							  me.outFile("xml","exp_current")
						}
					}
	})
	return btnLRMX
  },
  createRefreshBtn:function(flag){
	  var me=this
	  var menusrefresh=Ext.widget('menu',{
	    	width: 125,
		    plain: true,
		    floating: true,
		    items: [
		    	{
	    		text:common.button.refreshIndex,
	    		handler:function(){
	    			Ext.MessageBox.wait(common.label.refreshMSG, common.label.expWait)
	    			me.refresh("0")
	    		}
		    	},
		    	{
		    		text:common.button.refreshPart,
		    		handler:function(){
		    			Ext.MessageBox.wait(common.label.refreshMSG, common.label.expWait)
		    			me.refresh("1")
		    		}
		    	},
		    	{
		    		text:common.button.refreshAll,
		    		handler:function(){
		    			Ext.MessageBox.wait(common.label.refreshMSG, common.label.expWait)
		    			me.refresh("all")
		    		}
		    	}
		    ]
	    })
	    //刷新按钮
	    var refreshBtn=Ext.widget('button',{
	    	width:100,
	    	icon:'../../images/refresh.gif',
	    	iconCls: 'btn-Img-icon',
	    	text:common.button.refresh,
	    	menu: (flag === 'batch'||flag==='edit')?menusrefresh : '',
	        handler: function() {
		        if (flag !== 'batch'&&flag!=='edit') {
		        	Ext.MessageBox.wait(common.label.refreshMSG, common.label.expWait)
		        	me.refresh("0")
		         }
	       }
	    	
	    })
	    return refreshBtn
  },
  refresh:function(flag){//刷新 flag 0 加载当前人员 1 加载选中人员 all 加载全部
	  var me=this
	  var map=new HashMap()
	  if(flag=='0'){
		  map.put('flag',"false")
		  map.put("data",[this.data.nbase+this.data.A0100])
	  }else if(flag=='1'){
		var panel = Ext.getCmp('officer_panel')
		map.put('flag',"false")  
    	if(panel){
    		var arry=panel.getSelection();
    		var data_arry=[];
    		if(arry.length>0){
    			for(var i=0;i<arry.length;i++){
    				data_arry.push(arry[i].data.nbase.split('`')[1]+arry[i].data.a0100)
    			}
    			map.put('data', data_arry)
    		}else{
    			Ext.MessageBox.close()
    			Ext.showAlert(common.label.outFileMsg)
    			return
    		}
    	}else{
    		Ext.MessageBox.close()
    		Ext.showAlert(common.label.outFileMsg)
    		return
    	}
	  }else{
		  map.put('flag',"all")  
	  }
	  Rpc({ functionId: 'OM000000003', success:function(res){
		  res = Ext.decode(res.responseText)
		  Ext.MessageBox.close()
		  if(!res.typeFlag){
			  Ext.showAlert(res.errorMsg)
		  }else{
			  me.getRenderData(this.data)
		  }
	  }, scope: this }, map)
  },
  saveData:function(){//保存数据
	  var map=new HashMap()
	  var data_map=new HashMap()
	  Ext.MessageBox.wait(common.label.saveDataMsg, common.label.expWait)
	  for(var i=0;i<this.idArry.length;i++){
		  var key=this.idArry[i]
		  if(key=='education'||key=='degree'||
			 key=='school'||key=='educationmajor'){
			 var obj_full=document.getElementById(key+"_full_value")
			 var obj_work=document.getElementById(key+"_work_value")
			 data_map.put(key,obj_full.innerHTML.replace(/&nbsp;/g,' ')+"`"+obj_work.innerHTML.replace(/&nbsp;/g,' '))
		  }
		
		  var obj=document.getElementById(key+"_value")
		  if(obj){
			  if(key=='birthdate'){
				  if(obj.innerHTML!=""){
					  var birthdate=obj.innerHTML;
					  data_map.put(key,birthdate.substring(0,4)+"-"+birthdate.substring(4,6)+"-01")
				  }
			  }else{
				  if(key=='a0101'){
					  data_map.put(key,obj.innerHTML.replace(/&nbsp;/,''))
				  }else{
					  if(key === 'majorpost' || key === 'majorspecialty' || key === 'currentpost' ||
						 key === 'preparepost' || key === 'terminalpost' || key === 'rewardsandpenalties' ||
						 key === 'assessment' || key === 'postreason'||key ==='majorspecialty'||
						 key === 'currentpost' ||key === 'preparepost' ||
						 key === 'terminalpost' ){
						  var value = obj.innerHTML.replace(/&nbsp;&nbsp;&nbsp;&nbsp;/g, '\\r').replace(/<br>/g, '\\n').replace(/<BR>/g, '\\n').replace(/&nbsp;/g,' ')
						  data_map.put(key,value)
						}else{
							data_map.put(key,obj.innerHTML)
						}
				  }
			  }
		  }
	  }
	  //简历
	  var store=Ext.data.StoreManager.lookup('resumeStore')
	  var resumeStr='';
	  for(var i=0;i<store.getCount();i++){
		  var startDate=store.getAt(i).get("startDate")
		  var endDate=store.getAt(i).get("endDate")
		  var desc=store.getAt(i).get("desc")
		  if(startDate==''&&endDate==''&&desc==''){
			  continue
		  }
		  if(startDate!=''||endDate!=''){
			  resumeStr+=(startDate==""?'       ':startDate)+"--"+(endDate==""?'       ':endDate)
		  }else{
			  resumeStr+='                '
		  }
		  resumeStr+="  "+desc+'\\n'
	  }
	  data_map.put("resume",resumeStr);
	  //家庭关系
	  var familyandrelation="";
	  var store_family= Ext.data.StoreManager.lookup('familyStore')
		for(var i=0;i<store_family.getCount();i++){
			familyandrelation+="<Item>" +
				"<XingMing>"+store_family.getAt(i).get('XingMing')+"</XingMing>" +
				"<ChengWei>"+store_family.getAt(i).get('ChengWei')+"</ChengWei>" +
				"<ChuShengRiQi>"+store_family.getAt(i).get('ChuShengRiQi')+"</ChuShengRiQi>" +
				"<ZhengZhiMianMao>"+store_family.getAt(i).get('ZhengZhiMianMao')+"</ZhengZhiMianMao>" +
				"<GongZuoDanWeiJiZhiWu>"+store_family.getAt(i).get('GongZuoDanWeiJiZhiWu')+"</GongZuoDanWeiJiZhiWu>" +
			    "</Item>"
		}
	  data_map.put("familyandrelation",familyandrelation)
	  map.put("flag","save")
	  map.put("data_obj",data_map)//干部任免表存储数据
	  map.put("data",this.data)//人员id
	  Rpc({ functionId: 'OM000000006', success:function(res){
		  res = Ext.decode(res.responseText)
		  Ext.MessageBox.close()
		  if(!res.flag){
			  Ext.showAlert(res.errMsg)
		  }else{
			  
		  }
	  }, scope: this }, map)
  },
//flag:false 导出部分 all 导出全部  exp_current 选中人员导出   filetype: all 多人一文档 1：一人一文档 
//type xml word pdf  
  outFile: function(type,flag,filetype) { 
    var map = new HashMap()
    if(flag=='false'){
    	var panel = Ext.getCmp('officer_panel')
    	if(panel){
    		var arry=panel.getSelection();
    		var data_arry=[];
    		if(arry.length>0){
    			for(var i=0;i<arry.length;i++){
    				arry[i].data.nbase=arry[i].data.nbase.split('`')[1]
    				data_arry.push(arry[i].data)
    			}
    			map.put('data', data_arry)
    		}else{
    			Ext.MessageBox.close()
    			Ext.showAlert(common.label.outFileMsg)
    			return
    		}
    	}else{
    		Ext.MessageBox.close()
    		Ext.showAlert(common.label.outFileMsg)
    		return
    	}
    }else{
    	map.put('data', this.data)
    }
    map.put('flag', flag)//'exp_current'
    map.put("type",type)//xml
    map.put("filetype",filetype)
    Rpc({ functionId: 'OM000000002', success: this.outSuccess, scope: cardView }, map)
  },
  outSuccess:function(res){
	  Ext.MessageBox.close()
      res = Ext.decode(res.responseText)
      if (res.Typeflag) {
		window.location.target="_blank";
		window.location.href="/servlet/vfsservlet?fileid="+res.filename+"&fromjavafolder=true"; 
      } else {
        Ext.showAlert(res.errorMsg)
      }
    
  },
  cellclick: function(id,maxLength) { // 单元格单击显示编辑框
	var me=this
	if(this.cardType!=='edit'){
		  return;
	}   
	if(id==='sex'||id==='nation'||
	   id==='education_full'||id==='degree_full'||
	   id==='education_work'||id==='degree_work'||
	   id==='birthdate'||id==='joinjobdate'||(this.fieldMap.nativeplace&&id==='nativeplace')||
	   (this.fieldMap.birthplace&&id==='birthplace')||(this.fieldMap.health&&id==='health')){
		if(Ext.get(id+'_value')){
			document.getElementById(id+'_value').style.display="none"
		}
		document.getElementById(id+'_input').style.display="block"
		//显示文本框 光标位置默认放置最后 
		var t=$("#"+id+'_input').val(); 
		$("#"+id+'_input').val("").focus().val(t); 
		//
		if(id!=='birthdate'&&id!=='joinjobdate'){
			this.showCodeSelect(id+'_input',id)
		}else{
			me.showDataPanel(id)
		}
	}else{
		me.createTextArea(id,maxLength)
	}
  },
  createTextArea:function(id,maxLength){//取消调用大文本输入组件，调用组件ie下会有问题
	  var me=this
	  if(Ext.getCmp("texfield_area")){
		  Ext.getCmp("texfield_area").destroy()
	  }
	  var value_Obj=document.getElementById(id+'_value')
	  var value=value_Obj.innerHTML
	  if(!value){
		value=''
	  }else{
		  if(id === 'majorpost' || id === 'majorspecialty' || id === 'currentpost' ||
			 id === 'preparepost' || id === 'terminalpost' || id === 'rewardsandpenalties' ||
			 id === 'assessment' || id === 'postreason'){
			  value = value.replace(/&nbsp;/g, ' ').replace(/<br>/g, '\n')
		  }
		  
	  }
	  var width=document.getElementById(id).offsetWidth
	  var height=document.getElementById(id).offsetHeight
	  var panle=Ext.create('Ext.panel.Panel', {
			        width:width,
			        height:height,
			        id:'texfield_area',
			        floating: true,
				    shadow:false,
				    minHeight:150,
				    layout:'fit',
				    header:false,
				    border:0,
			        floating:true,
			        renderTo: Ext.getBody(),
			        listeners:{
						'focusleave':function(e,t,o){
							var re = /^[0-9]+.?[0-9]*$/;
							var value=panle.getComponent('officer_bigText').getValue()
							if(id=='joinpartydate'){
								if(value.indexOf(";")>-1||value.indexOf("；")>-1){
									value=value.replace('；',";")
									var valueArry=value.split(";")
									if(valueArry.length>2){
										Ext.showAlert(common.label.joinpartyError)
										return
									}else{
										if(re.test(valueArry[0])){
											if(!me.checkDate(valueArry[0])){
												return
											}
										}else if(re.test(valueArry[0])){
											if(!me.checkDate(valueArry[1])){
												return
											}
										}
										
									}
									
								}else{
									if(!re.test(value)){
										Ext.showAlert(common.label.errorDate)
										return
									}else{
										if(!me.checkDate(value)){
											return
										}
									}
								}
							}else{
								value=value.replace(/ /g, '&nbsp;').replace(/\n/g, '<br>')
							}
							panle.destroy()
							value_Obj.innerHTML=value
							me.resizeFonts(id,id + '_value')
						}
					},
			        items: [{
			            xtype: 'textarea',
			            itemId:'officer_bigText',
			            padding:'1 0 2 0',//解决ie8，9 缺线问题
			            enableKeyEvents:true,
		 	            maxLength:maxLength,
		 	            listeners:{
		 	            	'keydown':function(e,t,o){
		 	            		if(t.keyCode==13&&!(id === 'rewardsandpenalties' ||
		 	            				 id === 'assessment' || id === 'postreason')){//屏蔽回车键
		 	            			t.keyCode=0
		 	            			t.browserEvent.returnValue=false;
		 	            		}
		 	            	}
		 	            }
			        }]
			    });
	  panle.alignTo(Ext.get(id),'tl?',undefined)
	  var text=panle.getComponent('officer_bigText')
	  value=value.replace(/&nbsp;/g, ' ').replace(/<br>/g, '\n').replace(/<BR>/g, '\n')
	  text.setValue(value)
	  text.focus()
  },
  inputBlur: function(id) { // 失去焦点时触发事件
    var input = document.getElementById(id + '_input')
    input.style.display="none";
    value = input.value
    if(id==='joinjobdate'||id==="birthdate"||id==='sex'||id==='nation'||
       id==='education_full'||id==='degree_full'||id==='education_work'||
       id==='degree_work'||this.fieldMap[id]){
    	if(id==='birthdate'||id==='joinjobdate'){
    		if(!this.checkDate(value)){
    			value=''
    		}
    	}
    	if(Ext.get(id+'_value')){
    		document.getElementById(id+'_value').style.display='block'
    		document.getElementById(id+'_value').innerHTML=value
		}
    }else{
    	var font = document.getElementById(id + '_value')
    	font.style.display = 'block'
    	font.style.fontSize = '16px'
    	font.innerHTML = value
    }

    this.resizeFonts(id,id + '_value')
  },
  checkDate:function(data){//校验日期是否合法
	  if(data==''){
		  return true
	  }
	  var date_now=new Date()
	 if(Ext.Date.parse(data,'Ym')){
		 return true
	 }else{
		 Ext.showAlert(common.label.errorDate)
		 return false
	 }
	 
  },
  cardHtml: function() {
	var flag=false;
	if(this.cardType=='edit'){
		flag=true;
	}
    var html = '<div id="cardView_dom" style="width:1340px;height:100%">' +
		'<div style="float:left;width:660px;min-height:1050px;">' +
	'<div style="min-height:1050px;;width:660px;margin-top:20px">' +
		'<div style="margin-left:20px;width:630px;min-height:1050px;">' +
			'<div style="width:100%;min-height:540px;">' +
			'<table cellpadding="0" style="width:630px;height:540px;border:1px solid #C5C5C5;border-collapse: collapse">' +
				'<tr height="60px">' +
					'<td class="td1" id="a0101_desc" style="background:#E5E5E5;" onmouseover="cardView.createTitleDesc(\'a0101_desc\')">姓  名</td> ' +
					" <td class=\"td2\" id=\"a0101\" height=\"62\" valign=\"middle\" align=\"center\" >" +
					' </td> ' +
					' <td style="background:#E5E5E5;" id="sex_desc" onmouseover="cardView.createTitleDesc(\'sex_desc\')" class="td1">性别</td>' +
					" <td class=\"td2\" id=\"sex\" height=\"62\" valign=\"middle\" style=\""+(flag?"cursor:pointer":"")+"\" align=\"center\" onclick=\"cardView.cellclick('sex')\" >" +
					' </td>' +
					' <td style="background:#E5E5E5;" id="birthdate_desc" onmouseover="cardView.createTitleDesc(\'birthdate_desc\')" class="td2">出生年月(岁)</td>' +
					" <td class=\"td2\" id=\"birthdate\"  height=\"62\"  >" +
					' </td>' +
					' <td class="td3" rowspan="3" style="margin:0 auto;border:0;padding:0" id="photo"></td>' +// 照片
				'</tr>' +
				'<tr>' +
					' <td class="td1" id="nation_desc" onmouseover="cardView.createTitleDesc(\'nation_desc\')" style="background:#E5E5E5;">民  族</td>' +
					" <td class=\"td2\" id=\"nation\" height=\"62\" style=\""+(flag?"cursor:pointer":"")+"\" onclick=\"cardView.cellclick('nation')\"  ></td> " +
					' <td style="background:#E5E5E5;" id="nativeplace_desc" onmouseover=\"cardView.createTitleDesc(\'nativeplace_desc\')\" class="td1"> 籍贯</td>' +
					" <td class=\"inputCssTD\" height=\"62\" style=\"font-size:14pt;font-family:宋体;text-align:center;"+(flag?"cursor:pointer":"")+"\" id=\"nativeplace\" onclick=\"cardView.cellclick('nativeplace',18)\" ></td>" +
					' <td style="background:#E5E5E5;" id="birthplace_desc" onmouseover="cardView.createTitleDesc(\'birthplace_desc\')" class="td2">出生地</td> ' +
					" <td class=\"td2\" id=\"birthplace\" height=\"62\" style=\"text-align:center;"+(flag?"cursor:pointer":"")+"\" onclick=\"cardView.cellclick('birthplace',18)\" ></td>" +
				'</tr>' +
				'<tr>' +
					' <td class="td1" id="joinpartydate_desc" onmouseover="cardView.createTitleDesc(\'joinpartydate_desc\')" style="background:#E5E5E5;">入党</br>时间</td>' +
					" <td class=\"td2\" id=\"joinpartydate\" style=\""+(flag?"cursor:pointer":"")+"\" onclick=\"cardView.cellclick('joinpartydate')\" height=\"62\"  ></td>" +
					' <td style="background:#E5E5E5;" id="joinjobdate_desc" onmouseover="cardView.createTitleDesc(\'joinjobdate_desc\')" class="td1">参加工作时间</td>' +
					" <td id=\"joinjobdate\" height=\"62\" onclick=\"cardView.cellclick('joinjobdate')\" style=\"font-size:16px;font-family:宋体;text-align:center;"+(flag?"cursor:pointer":"")+"\" ></td>" +
					' <td style="background:#E5E5E5;" id="health_desc" onmouseover="cardView.createTitleDesc(\'health_desc\')" class="td2">健康状况</td>' +
					" <td class=\"td2\" id=\"health\" style=\""+(flag?"cursor:pointer":"")+"\" onclick=\"cardView.cellclick('health',18)\" height=\"62\" ></td>" +
				'</tr>' +
				'<tr height="60px">' +
					' <td class="td1" id="majorpost_desc" onmouseover="cardView.createTitleDesc(\'majorpost_desc\')" style="background:#E5E5E5;">专业技术职务</td>' +
					" <td class=\"td2\" colspan=\"2\" id=\"majorpost\" style=\""+(flag?"cursor:pointer":"")+";text-align:center\" onclick=\"cardView.cellclick('majorpost',110)\"  height=\"57\" ></td>" +
					' <td style="background:#E5E5E5;" id="majorspecialty_desc" onmouseover="cardView.createTitleDesc(\'majorspecialty_desc\')" class="td2">熟悉专业有何特长</td>' +
					" <td colspan=\"3\" id=\"majorspecialty\" style=\""+(flag?"cursor:pointer":"")+";text-align:center\" onclick=\"cardView.cellclick('majorspecialty',110)\" height=\"57\" class=\"td2\" ></td>" +
				'</tr>' +
				"<tr id=\"education_degree\"  >" +
					' <td rowspan="2" id="education_degree_desc" onmouseover="cardView.createTitleDesc(\'education_degree_desc\')" style="background:#E5E5E5;" class="td1">学历</br>学位</td>' +
					' <td style="background:#E5E5E5;font-size:14pt" class="td2">全日制</br>教&nbsp;育</td>' +
					' <td class="inputCssTD" valign="middle" colspan="2" >' +  
						'<div id="education_full" onclick="cardView.cellclick(\''+"education_full"+'\')" style="'+(flag?"cursor:pointer":"")+';min-height:23px;display:table;width:100%;height:50%;text-align:left;border-style:none none dashed none;border-color:#d0d0d0;border-width:0 0 1px 0"></div>' +
						'<div id="degree_full" onclick="cardView.cellclick(\''+"degree_full"+'\')" style="'+(flag?"cursor:pointer":"")+';min-height:23px;;display:table;width:100%;text-align:left;height:50%"></div>' +
					' </td>' +
					' <td style="background:#E5E5E5;font-size:14pt" class="td2">毕业院校系及专业</td>' +
					' <td id="school_educationmajor" colspan="2" style="border:1px solid #C5C5C5;font-family:宋体;font-size:16px">' +
						'<div id="school_full" onclick="cardView.cellclick(\''+"school_full"+'\',90)"  style="'+(flag?"cursor:pointer":"")+';min-height:23px;display:table;width:100%;height:50%;text-align:left;border-style:none none dashed none;border-color:#d0d0d0;border-width:0 0 1px 0"></div>' +
						'<div id="educationmajor_full" onclick="cardView.cellclick(\''+"educationmajor_full"+'\',90)" style="'+(flag?"cursor:pointer":"")+';min-height:23px;;display:table;width:100%;text-align:left;height:50%"></div>' +
					' </td>' +
				'</tr>' +
				"<tr id=\"education_degree_1\" >" +
					' <td class="td2"  style="background:#E5E5E5;font-size:14pt">在&nbsp;职</br>教&nbsp;育</td>' +
					' <td class="inputCssTD" colspan="2">' +
						'<div id="education_work" onclick="cardView.cellclick(\''+"education_work"+'\')" style="'+(flag?"cursor:pointer":"")+';min-height:23px;display:table;width:100%;height:50%;text-align:left;border-style:none none dashed none;border-color:#d0d0d0;border-width:0 0 1px 0"></div>' +
						'<div id="degree_work" onclick="cardView.cellclick(\''+"degree_work"+'\')" style="'+(flag?"cursor:pointer":"")+';min-height:23px;display:table;width:100%;height:50%;text-align:left"></div>' +
					' </td>' +
					' <td style="background:#E5E5E5;font-size:14pt;" class="td2">毕业院校系及专业</td>' +
					' <td id="school_educationmajor_work" colspan="2" style="font-size:16px;border:1px solid #C5C5C5;font-family:宋体;">' +
						'<div id="school_work" onclick="cardView.cellclick(\''+"school_work"+'\',90)" style="'+(flag?"cursor:pointer":"")+';min-height:23px;display:table;width:100%;height:50%;text-align:left;border-style:none none dashed none;border-color:#d0d0d0;border-width:0 0 1px 0"></div>' +
						'<div id="educationmajor_work" onclick="cardView.cellclick(\''+"educationmajor_work"+'\',90)" style="'+(flag?"cursor:pointer":"")+';min-height:23px;display:table;width:100%;text-align:left;height:50%"></div>' +
					' </td>' +
				'</tr>' +
				'<tr>' +
					' <td colspan="2"style="background:#E5E5E5;" id="currentpost_desc" onmouseover="cardView.createTitleDesc(\'currentpost_desc\')" class="td1">现&nbsp;任&nbsp;职&nbsp;务</td>' +
					" <td id=\"currentpost\" onclick=\"cardView.cellclick('currentpost',324)\" height=\"55\" style=\"font-size:16px;font-family:宋体;text-align:left;"+(flag?"cursor:pointer":"")+"\"  class=\"inputCssTD\" colspan=\"5\"></td>" +
				'</tr>' +
				'<tr>' +
					' <td colspan="2" id="preparepost_desc" onmouseover="cardView.createTitleDesc(\'preparepost_desc\')" style="background:#E5E5E5;" class="td1">拟&nbsp;任&nbsp;职&nbsp;务</td>' +
					" <td id=\"preparepost\" onclick=\"cardView.cellclick('preparepost',324)\" height=\"55\" style=\"font-size:16px;font-family:宋体;text-align:left;"+(flag?"cursor:pointer":"")+"\"  class=\"inputCssTD\" colspan=\"5\"></td>" +
				'</tr>' +
				'<tr>' +
					' <td colspan="2" id="terminalpost_desc" onmouseover="cardView.createTitleDesc(\'terminalpost_desc\')" style="background:#E5E5E5;" class="td1">拟&nbsp;免&nbsp;职&nbsp;务</td>' +
					" <td id=\"terminalpost\" onclick=\"cardView.cellclick('terminalpost',324)\" height=\"55\" style=\"font-size:16px;font-family:宋体;text-align:left;"+(flag?"cursor:pointer":"")+"\"  class=\"inputCssTD\" colspan=\"5\"></td>" +
				'</tr>' +

			'</table>' +
			'<table style="width:630px;height:500px;border-collapse: collapse;border:1px solid #C5C5C5;border-top:none">' +
				'<tr style="height:100%">' +
					' <td onmouseover="cardView.createTitleDesc(\'resume_desc\')" id="resume_desc" style="font-family:宋体;font-size:14pt;width:40px;background:#E5E5E5;">&nbsp;简&nbsp;</br>&nbsp;</br>&nbsp;</br>&nbsp;</br>&nbsp;历</td>' +
					" <td id=\"resume\" onclick=\"cardView.showGridPanel('resume')\"  valign=\"top\" style=\"width:590px;height:100%;"+(flag?"cursor:pointer":"")+"\">" +
					'</td>' +
				'</tr>' +
			'</table>' +
			'</div>' +
		'</div>' +
	'</div>' +
	'</div>' +
		'<div style="float:left;width:660px;height:1150px; padding-bottom:20px">' +
			'<div style="width:630px;height:1040px;margin-left:20px;margin-top:20px;">' +
				'<table style="width:100%;height:330px;border-bottom:none;border-collapse: collapse;">' +
					'<tr>' +
						' <td class="secPage" id="rewardsandpenalties_desc" onmouseover="cardView.createTitleDesc(\'rewardsandpenalties_desc\')" >奖惩情况</td>' +
						" <td id=\"rewardsandpenalties\" onclick=\"cardView.cellclick('rewardsandpenalties',552)\" height=\"110\"  class=\"inputCssTD\" style=\""+(flag?"cursor:pointer":"")+";text-align:left !important;width:580px;font-size:16px;font-family:宋体;\"></td>" +
					'</tr>' +
					'<tr>' +
						' <td class="secPage" onmouseover="cardView.createTitleDesc(\'assessment_desc\')" id="assessment_desc" >年度考核结果</td>' +
						" <td id=\"assessment\" onclick=\"cardView.cellclick('assessment',414)\" height=\"110\" align=\"left\"  class=\"inputCssTD\" style=\""+(flag?"cursor:pointer":"")+";text-align:left !important;width:580px;font-size:16px;font-family:宋体;\"></td>" +
					'</tr>' +
					'<tr  style="border-top:none;border-bottom:none">' +
						' <td class="secPage" onmouseover="cardView.createTitleDesc(\'postreason_desc\')" id="postreason_desc" style="border-top:none;border-bottom:none">任免理由</td>' +
						" <td id=\"postreason\" onclick=\"cardView.cellclick('postreason',552)\" height=\"110\" align=\"left\"   class=\"inputCssTD\" style=\""+(flag?"cursor:pointer":"")+";text-align:left !important;border-top:none;border-bottom:none;width:580px;font-size:16px;font-family:宋体;\"></td>" +
					'</tr>' +
				'</table>' +
				"<table style=\"width:100%;height:600px;border-collapse: collapse;"+(flag?"cursor:pointer":"")+"\" onclick=\"cardView.showGridPanel('familyandrelation')\" id=\"familyandrelation\" >" +
					'<tr height="60px"  onmouseover="cardView.createTitleDesc(\'familyandrelation_desc\')" id="familyandrelation_desc">' +
						' <td class="secPage" style="padding-left:0;padding-right:0;border-right:none;' + (Ext.isChrome||Ext.isGecko ? 'width:53px' : '') + '"  rowspan="11">家庭主要成员及重要社会关系</td>' +
						' <td class="titleCss" style="width:50px">称&nbsp;谓</td>' +
						' <td class="titleCss" style="width:80px">姓&nbsp;名</td>' +
						' <td class="titleCss" style="width:'+(Ext.isGecko?'80px':'100px')+'">出生日期</td>' +
						' <td class="titleCss" style="width:70px">政&nbsp;治<br>面&nbsp;貌</td>' +
						' <td class="titleCss" style="width:'+(Ext.isGecko?'278px':'280px')+'">工&nbsp;作&nbsp;单&nbsp;位&nbsp;及&nbsp;职&nbsp;务</td>' +
					'</tr>' +
					'<tr id="Item1" height="54px">' + 
						'<td id="ChengWei1"  class="inputCssTD"></td>' +
						'<td id="XingMing1"  class="inputCssTD"></td>' +
						'<td id="ChuShengRiQi1"  class="inputCssTD"></td>' +
						'<td id="ZhengZhiMianMao1"  class="inputCssTD"></td>' +
						'<td id="GongZuoDanWeiJiZhiWu1"  style="text-align:left" class="inputCssTD"></td>' +
					'</tr>' +
					'<tr id="Item2" height="54px">' +
						'<td id="ChengWei2"  class="inputCssTD"></td>' +
						'<td id="XingMing2"  class="inputCssTD"></td>' +
						'<td id="ChuShengRiQi2"  class="inputCssTD"></td>' +
						'<td id="ZhengZhiMianMao2"  class="inputCssTD"></td>' +
						'<td id="GongZuoDanWeiJiZhiWu2"  style="text-align:left" class="inputCssTD"></td>' +
					'</tr>' +
					'<tr id="Item3" height="54px">' +
						'<td id="ChengWei3"  class="inputCssTD"></td>' +
						'<td id="XingMing3"  class="inputCssTD"></td>' +
						'<td id="ChuShengRiQi3"  class="inputCssTD"></td>' +
						'<td id="ZhengZhiMianMao3"  class="inputCssTD"></td>' +
						'<td id="GongZuoDanWeiJiZhiWu3"  style="text-align:left" class="inputCssTD"></td>' +
					'</tr>' +
					'<tr id="Item4" height="54px">' +
						'<td id="ChengWei4"  class="inputCssTD"></td>' +
						'<td id="XingMing4"  class="inputCssTD"></td>' +
						'<td id="ChuShengRiQi4"  class="inputCssTD"></td>' +
						'<td id="ZhengZhiMianMao4"  class="inputCssTD"></td>' +
						'<td id="GongZuoDanWeiJiZhiWu4"  style="text-align:left" class="inputCssTD"></td>' +
					'</tr>' +
					'<tr id="Item5" height="54px">' +
						'<td id="ChengWei5"  class="inputCssTD"></td>' +
						'<td id="XingMing5"  class="inputCssTD"></td>' +
						'<td id="ChuShengRiQi5"  class="inputCssTD"></td>' +
						'<td id="ZhengZhiMianMao5"  class="inputCssTD"></td>' +
						'<td id="GongZuoDanWeiJiZhiWu5"  style="text-align:left" class="inputCssTD"></td>' +
					'</tr>' +
					'<tr id="Item6" height="54px">' +
						'<td id="ChengWei6"  class="inputCssTD"></td>' +
						'<td id="XingMing6"  class="inputCssTD"></td>' +
						'<td id="ChuShengRiQi6"  class="inputCssTD"></td>' +
						'<td id="ZhengZhiMianMao6"  class="inputCssTD"></td>' +
						'<td id="GongZuoDanWeiJiZhiWu6"  style="text-align:left" class="inputCssTD"></td>' +
					'</tr>' +
					'<tr id="Item7" height="54px">' +
						'<td id="ChengWei7"  class="inputCssTD"></td>' +
						'<td id="XingMing7"  class="inputCssTD"></td>' +
						'<td id="ChuShengRiQi7"  class="inputCssTD"></td>' +
						'<td id="ZhengZhiMianMao7"  class="inputCssTD"></td>' +
						'<td id="GongZuoDanWeiJiZhiWu7"  style="text-align:left" class="inputCssTD"></td>' +
					'</tr>' +
					'<tr id="Item8" height="54px">' +
						'<td id="ChengWei8"  class="inputCssTD"></td>' +
						'<td id="XingMing8"  class="inputCssTD"></td>' +
						'<td id="ChuShengRiQi8"  class="inputCssTD"></td>' +
						'<td id="ZhengZhiMianMao8"  class="inputCssTD"></td>' +
						'<td id="GongZuoDanWeiJiZhiWu8"  style="text-align:left" class="inputCssTD"></td>' +
					'</tr>' +
					'<tr id="Item9" height="54px">' +
						'<td id="ChengWei9"  class="inputCssTD"></td>' +
						'<td id="XingMing9"  class="inputCssTD"></td>' +
						'<td id="ChuShengRiQi9"  class="inputCssTD"></td>' +
						'<td id="ZhengZhiMianMao9"  class="inputCssTD"></td>' +
						'<td id="GongZuoDanWeiJiZhiWu9"  style="text-align:left" class="inputCssTD"></td>' +
					'</tr>' +
					'<tr id="Item10" height="54px">' +
						'<td id="ChengWei10" onclick=\"cardView.cellclick(\''+"ChengWei10"+'\')\" class="inputCssTD"></td>' +
						'<td id="XingMing10" onclick=\"cardView.cellclick(\''+"XingMing10"+'\',18)\" class="inputCssTD"></td>' +
						'<td id="ChuShengRiQi10" onclick=\"cardView.cellclick(\''+"ChuShengRiQi10"+'\')\" class="inputCssTD"></td>' +
						'<td id="ZhengZhiMianMao10" onclick=\"cardView.cellclick(\''+"ZhengZhiMianMao10"+'\')\" class="inputCssTD"></td>' +
						'<td id="GongZuoDanWeiJiZhiWu10" onclick=\"cardView.cellclick(\''+"GongZuoDanWeiJiZhiWu10"+'\',124)\" style="text-align:left" class="inputCssTD"></td>' +
					'</tr>' +
				'</table>' +
				'<table style="width:100%;height:55px;border-collapse: collapse;">' +
					'<tr style="border-top:none;border-bottom:none">' +
						'<td class="secPage" onmouseover="cardView.createTitleDesc(\'report_unit_desc\')" id="report_unit_desc" style="border-top:none;border-bottom:none">呈报单位</td>' +
						"<td id=\"report_unit\" onclick=\"cardView.cellclick('"+"report_unit"+"',252)\"   class=\"inputCssTD\" style=\""+(flag?"cursor:pointer":"")+";border-top:none;border-bottom:none;width:580px\"></td>" +
					'</tr>' +
				'</table>' +
				'<table style="width:100%;height:55px;border-collapse: collapse;">' +
					'<tr>' +
					   '<td  style="width:80px" onmouseover="cardView.createTitleDesc(\'id_number_desc\')" id="id_number_desc" class="titleCss">身份</br>证号</td>' +
					   "<td id=\"id_number\"   class=\"inputCssTD\" style=\"width:100px\"></td>" +
					   '<td style="width:80px" id="yearData_desc" onmouseover="cardView.createTitleDesc(\'yearData_desc\')" class="titleCss">计算年龄时间</td>' +
					   "<td id=\"yearData\"  class=\"inputCssTD\" style=\"width:100px\"></td>" +
					   '<td style="width:50px" onmouseover="cardView.createTitleDesc(\'write_Data_desc\')" id ="write_Data_desc" class="titleCss">填表时间</td>' +
					   "<td id=\"write_Data\"  class=\"inputCssTD\" style=\"width:80px\"></td>" +
					   '<td style="width:70px" class="titleCss" onmouseover="cardView.createTitleDesc(\'write_person_desc\')" id="write_person_desc">填表人</td>' +
					   "<td id=\"write_person\"  class=\"inputCssTD\" style=\"width:100px\"></td>" +
					 '</tr>' +
				'</table>' +
			'</div>' +
		'</div>' +
	'</div>'
    return html
  }
})
