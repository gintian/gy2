//@import url(xx.css);
var invitationPanel;
var method;
/***
 * 邀请评价
 * @param {} function_str方法名
 * @param {} link_id流程id
 * @param {} node_id环节id
 * @param {} a0100被操作人员编号集
 * @param {} name被操作人员名
 * @param {} c0102邮箱地址
 */
invitationEvaluation = function(param){
    var value = param.responseText;
	var map = Ext.decode(value);
	var data=Ext.decode(map.templateList);
	var title=map.subject;
	var content=map.content;
	var sub_module=map.sub_module;
	var nModule=map.nModule;
	var z0301=map.z0301;
	var a0100s=map.a0100s;
	var nbases=map.nbases;
	var titleStr="邀请简历评价";
	if(map.title!=undefined){
		titleStr = map.title;
	}
	method=map.method;
	
	var fileName = "";
	var store = new Ext.data.ArrayStore({
	fields: ['value', 'name'],
	data : data
	});
	if(data.length==0){
		Ext.showAlert(SET_APPRAISAL_RESUME_TEMPLATE);
		return;
	}
	var value = data[0][0];
	var combo = Ext.create('Ext.form.ComboBox', {
		id:'combo',
		injectCheckbox:1,
		autoSelect:true,
		editable:false,
		store: store,
		fieldLabel : "邮件模板",
		labelSeparator:"  ",
	    labelWidth:60,
	    width:350,
	    anchor : "80%",
	    value:value,
	    valueField:'value',
		displayField:'name',//store字段中你要显示的字段，多字段必选参数，默认当mode为remote时displayField为undefine，当 select列表时displayField为”text”
		mode: 'local',//因为data已经取数据到本地了，所以’local’,默认为”remote”，枚举完
		emptyText:'请选择一个模板',
		applyTo: 'combo',
		autoloader:true,
		listeners:{
			"select":function(combo,record,index){
				var map = new HashMap();
		    	map.put("sub_module", sub_module);
		    	map.put("nModule", nModule);
		    	map.put("a0100", a0100s);
		    	map.put("nbase", nbases);
		    	map.put("z0301", z0301);
		    	map.put("id", record.get("value"));
		    	//console.log(map);return;
		    	Rpc( {
		    		functionId : 'ZP0000002400',
		    		success : getEvaluationEmailBean
		    	}, map);
		}
	}
	});
     invitationPanel = Ext.widget('window',{
    	   modal:true,
    	   title:titleStr,
           height:450,
		   width:800,
		   layout:'fit',
           border:0,
           items:[{
		        layout:{
		        	type:'vbox'
		        	//align:'stretch'
		        },
		        border:0,
	            padding:5,
		        items:[{
	            xtpe:'panel',
	            height:70,
	            width:'100%',
	            border:0,
	            layout:{type:'hbox',align:'middle'},
	            items:[{
	             	xtype:'label',
	             	text:'评价人选',
	             	//labelAlign:"right",
	             	width:60
	             	},{
	             	xtype:'panel',
	             	border:0,
	             	height:70,
	             	autoScroll: true,
	             	flex:10,
	             	html:'<table style="height:70px;width:100%;"><tr>' +
	             			'<td nowrap="nowrap">' +
	             			'<div id="personPanel" style="float: left;padding-left:3px;"></div>' +
	             			'<div id="addPanel"  style="height:65px;float: left;padding-top:24px;padding-right:15px;text-align:center"><a href="###;" onclick="addPerson(this)">添加</a></div>' +
	             			'</td>' +
	             			'</tr></table>'
	             }]
	          },combo,{
		        xtype:"hidden",
		        fieldLabel : "人员编号",
		        labelSeparator:"  ",
		        width:800,
		        labelWidth:60,
		        value: "",
		        id:"userId"
		       },{
		        xtype:"hidden",
		        fieldLabel : "候选人编号",
		        labelSeparator:"  ",
		        width:800,
		        labelWidth:60,
		        value: a0100s,
		        id:"a0100s"
		       },{
		        xtype:"hidden",
		        fieldLabel : "候选人人员库",
		        labelSeparator:"  ",
		        width:800,
		        labelWidth:60,
		        value: nbases,
		        id:"nbase"
		       },{
		        xtype:"hidden",
		        fieldLabel : "职位编号",
		        labelSeparator:"  ",
		        width:800,
		        labelWidth:60,
		        value: z0301,
		        id:"z0301s"
		       },{
		        xtype:"textfield",
		        fieldLabel : "邮件标题",
		        labelSeparator:"  ",
		        width:350,
		        labelWidth:60,
		        value: title,
		        id:"title"
		       },{
	             xtype: "htmleditor",
	             height : 220,
	             width:760,
	             fieldLabel : "邮件内容",
	             labelWidth:60,
	             id:"content",
	             value: content,
				 labelSeparator:"  ",
			   	 enableAlignments: false,//是否启用对齐按钮，包括左中右三个按钮 
				 enableColors: false,//是否启用前景色背景色按钮，默认为true
				 enableFont: false,//是否启用字体选择按钮 默认为true
				 enableFontSize: false,//是否启用字体加大缩小按钮 
				 enableFormat: false,//是否启用加粗斜体下划线按钮
				 enableLists: false,//是否启用列表按钮
				 enableSourceEdit: false,//是否启用代码编辑按钮
				 enableLinks:false,
				 fontFamilies: ["宋体","隶书", "黑体","楷体"]
	           	}]
           	}],
           resizable:false,
           buttonAlign: 'center',
           buttons:[
                     {text:"确定",
                     id:"sendEmail",
                     handler:function(){	
                     var userId = Ext.getCmp("userId").getValue();
					 if(userId.length==0)
					 {
				 	   Ext.MessageBox.alert("提示信息","请添加需要邀请的评选人选！");
				       return;
					 }
					 var a0100 = Ext.getCmp("a0100s").getValue();
						var nbase = Ext.getCmp("nbase").getValue();
						var z0301 = Ext.getCmp("z0301s").getValue();
						var title = Ext.getCmp("title").getValue();
						var content = Ext.getCmp("content").getValue();
						var id = Ext.getCmp("combo").getValue();
						var map = new HashMap();
						map.put("userId", userId);
						map.put("a0100", a0100);
						map.put("nbase", nbase);
						map.put("z0301", z0301);
						map.put("title", title);
						map.put("content", content);
						map.put("z0301", z0301);
						map.put("id", id);
						//console.log(map);return;
						Rpc( {
							functionId : 'ZP0000002401',
							success : successInvitation
						}, map);
					}},
                    {text:"取消",handler:function(){
                    		invitationPanel.close();
                    	}}
                   ]
    }).show();
}
//鼠标移出图片时隐藏删除图标
onMouseleave=function(obj){
    obj.childNodes[1].style.display="none";
}
//鼠标移入图片时显示删除图标
onMouseover=function(obj){
	obj.childNodes[1].style.display="";
}
//删除人员信息
removePerson=function(obj,userNo){
	var node =obj.parentNode.parentNode;
	Ext.getDom("personPanel").removeChild(node);
	Ext.getCmp("userId").setValue(Ext.getCmp("userId").getValue().replace("`"+userNo,""));
}
//邀请邮件提交成功
successInvitation=function(param){
	var value = param.responseText;
	var map = Ext.decode(value);
	var msg = map.msg;
	Ext.MessageBox.alert("提示信息",msg,function(){
		if(method!="")
		{
			method=eval(method);
			method();
		}
		invitationPanel.close();
	});return;
}
/****
 * 添加邀请评价人员
 * @param {} btn
 */
addPerson=function(btn){
	var nbaseA0100s = Ext.getCmp("userId").getValue();//document.getElementById("userNo").value;
	var arr = new Array();
	arr = nbaseA0100s.split("`");
	var dd=$("#personPanel");
	var add=$("#addPanel");
	var picker = new PersonPicker({
		multiple: true,
		isPrivExpression:false,
		deprecate: arr,
		callback: function (ck) {
			for(var i=0;i<ck.length;i++){
				var c=ck[i];
				var html ="<dl style='float: left;padding-top:7px;padding-right:15px;text-align:center'>";
				html += '<dt onmouseover="onMouseover(this)" onmouseleave="onMouseleave(this)" ><img src="'+c.photo+'" width="32px" height="32px;" class="img-circle"/>' +
				'<img style="display:none;width: 15px; height: 15px;float:left;" class="deletePic" onclick="removePerson(this,\''+c.id+'\')" src="/workplan/image/remove.png" >' +
				'</dt>';
				html += '<dd>'+c.name;
				html += "</dl>"; 
				dd.append(html);
				dd.append(add);
				Ext.getCmp("userId").setValue(Ext.getCmp("userId").getValue()+"`"+c.id);
			}
		}
	}, btn);
	picker.open();
	
}
/***
 * 动态选择邮件模板
 * @param {} param
 */
getEvaluationEmailBean = function(param)
{
	var value = param.responseText;
	var map = Ext.decode(value);
	var title=map.subject;
	var content=map.content;
	Ext.getCmp("title").setValue(title);
	Ext.getCmp("content").setValue(content);
}