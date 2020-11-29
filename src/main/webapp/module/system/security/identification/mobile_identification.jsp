<%@page import="com.hjsj.hrms.module.system.security.identification.actionform.LoginUserInfoForm"%>
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
	LoginUserInfoForm loginUserInfoForm = (LoginUserInfoForm)session.getAttribute("loginUserInfoForm");
	String manual = (String)loginUserInfoForm.getManual();
%>
<link rel="stylesheet" href="/ext/ext6/resources/ext-theme.css" type="text/css" />
<script type="text/javascript" src="/components/tableFactory/tableFactory.js"></script>
<script type="text/javascript">
	var manual = "<%=manual%>";
	var tableObj = undefined;
	Ext.onReady(function(){
		<hrms:tableFactory title="登录认证规则" autoRender="true" subModuleId="mobile_identification_00001" 
			sqlProperty="sqlstr" jsObjName="tableObj"  constantName="identification/mobile_identification" 
			currentPage="${loginUserInfoForm.pageable.pageNumber }" columnProperty="columns" orderbyProperty="orderby"
			formName="loginUserInfoForm" pagesize="${loginUserInfoForm.pageable.pageSize }">
		</hrms:tableFactory>
		
		var pic = Ext.widget('image',{
			xtype:'image',
			title:'设置',
			scope:this,
			height:17,
			width:17,
			border:0,
			src:'/components/tableFactory/tableGrid-theme/images/Settings.png',
			listeners:{
				render:function(){
					this.getEl().on('click',this.setIden,this);
				}
			},
			setIden:function(){
				var vo = new HashMap();
				Rpc({functionId:'0000003055',success:function(res){
					var resultObj = Ext.decode(res.responseText);
					createWindow(resultObj.result);
				}},vo);
			}
		});
		Ext.getCmp('tableObj_mainPanel').items.items[0].addTool(pic);
		if(manual!="1")
			Ext.getCmp('tableObj_toolbar').hide();
	});
	
	function addInfo(value,record){
		var mobile_is_oauth = record.record.data.mobile_is_oauth;
		if(mobile_is_oauth==1){
			return "<img style=\"width:17px;height:17px;\" src=\"/images/confirm.png\">";
		} else {
			return "<img style=\"width:17px;height:17px;\" src=\"/images/cancel.png\">";
		}
	}
	
	function addParam(value,record){
		var oauth = "<a href=\"javascript:void(0)\" onclick=\"oauth('"+record.record.data.username+"');return false;\">认证</a>&nbsp;"
		var del = "&nbsp;<a href=\"javascript:void(0)\" onclick=\"del('"+record.record.data.username+"');return false;\">删除</a>";
		if(record.record.data.mobile_is_oauth=="1"){
			return del;
		} else {
			return oauth+del;
		}
	}
	
	function oauth(value){
		var vo = new HashMap();
		vo.put("username",value);
		Rpc({functionId:'0000003058',success:function(res){
			var resultObj = Ext.decode(res.responseText);
			if(resultObj.result=="ok"){
				Ext.Msg.alert('提示信息',"认证成功！");
				var store = Ext.data.StoreManager.lookup('tableObj_dataStore');
				store.reload();
			} else {
				Ext.Msg.alert('提示信息',"认证失败！");
				return;
			}
		}},vo);
	}
	
	function del(value){
		Ext.Msg.confirm("提示信息","确认删除吗？",function(id){
			if(id=='yes'){
				var vo = new HashMap();
				vo.put("username",value);
				Rpc({functionId:'0000003057',success:function(res){
					var resultObj = Ext.decode(res.responseText);
					if(resultObj.result!=undefined && !resultObj.result){
						Ext.Msg.alert('提示信息',"删除失败！");
						return;
					} else {
						Ext.Msg.alert('提示信息',"删除成功！");
						var store = Ext.data.StoreManager.lookup('tableObj_dataStore');
						store.reload();
					}
				}},vo);
			}
		});
	}
	
	function saveOauth(){
		var selected = Ext.getCmp('tableObj_tablePanel').getSelectionModel().getSelection();
		if(selected.length<1){
			Ext.Msg.alert('提示信息',"请选择认证数据！");
			return;
		}
		var data = [];
		for(var i=0;i<selected.length;i++){
			data.push(selected[i].data);
		}
		var vo = new HashMap();
		vo.put("oauth", data);
		Rpc({functionId:'0000003058',success:function(res){
			var resultObj = Ext.decode(res.responseText);
			if(resultObj.result=="ok"){
				Ext.Msg.alert('提示信息',"认证成功！");
				var store = Ext.data.StoreManager.lookup('tableObj_dataStore');
				store.reload();
			} else if(resultObj.result=="confirm"){
				Ext.Msg.alert('提示信息',"认证成功，已认证数据不重新认证！");
				var store = Ext.data.StoreManager.lookup('tableObj_dataStore');
				store.reload();
			} else {
				Ext.Msg.alert('提示信息',"认证失败！");
				return;
			}
		}},vo);
	}
	
	function createWindow(param){
		var obj = eval("("+param+")");
		var win = Ext.widget("window",{
			modal:true,
			title:'<font size="3">设置</font>',
			shadow:false,
			width:500,
			resizable:false,
			layout:'fit',
			border:false,
			bbar:['->',{
				xtype:'button',
				text:'确定',
				handler:function(){
					var flag = Ext.getCmp('manual').checked;
					var obj = new HashMap();
					obj.put("mobile",Ext.getCmp('mobile').checked);
					obj.put("manual",flag);
					obj.put("sms",Ext.getCmp('sms').checked);
					Rpc({functionId:'0000003056',success:function(res){
						var resultObj = Ext.decode(res.responseText);
						if(resultObj.result=="ok"){
							win.close();
							if(flag)
								Ext.getCmp('tableObj_toolbar').show();
							else
								Ext.getCmp('tableObj_toolbar').hide();
							Ext.Msg.alert('提示信息',"设置成功！");
						} else {
							Ext.Msg.alert('提示信息',"设置失败！");
						}
					}},obj);
				}
			},{
				xtype:'button',
				text:'取消',
				handler:function(){
					win.close();
				}
			},{xtype:'tbfill'}],
			items:[{
				xtype:'form',
				id:'setForm',
				header:false,
				border:false,
				layout:{
					type:'vbox',
					align:'center'
				},
				defaults:{
					width:400,
					padding:12,
					height:65
				},
				items:[{
					xtype:'fieldset',
					title:'<font size="2">启动登录认证</font>',
					items:[{
						id:'mobile',
						xtype:'checkbox',
						checked:obj.mobile,
						boxLabel:'移动'
					}]
				},{
					xtype:'fieldset',
					title:'<font size="2">认证激活方式</font>',
					layout:'hbox',
					defaults:{
						xtype:'checkbox',
						margin:'0 15 0 0'
					},
					items:[{
						id:'manual',
						checked:obj.manual,
						boxLabel:'人工激活'
					},{
						id:'sms',
						checked:obj.sms,
						boxLabel:'短信激活'
					}]
				}]
			}]
		}).show();
	}
</script>