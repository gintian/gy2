<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
UserView uv = (UserView)session.getAttribute(WebConstant.userView);
String isEditable = "true";
if(!uv.hasTheFunction("3110407"))
	isEditable = "false";
%>
<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7">
<link rel="stylesheet" href="/ext/ext6/resources/ext-theme.css" type="text/css" />
<script type="text/javascript" src="/components/tableFactory/tableFactory.js"></script>
<style>
/*自动换行 word-wrap : break-word; white-space:normal;*/
.x-grid-cell-inner {
            vertical-align:top;
            
}


</style>

<script>
   function toprepage(){
	    window.location.href="/recruitment/headhuntermanage/searchheadhuntergroup.do?b_search=link";
   }
   function namerender(value,meta,record){
	   if(!<%=isEditable%>)
		   return value;
	   return "<a href='javascript:edithunter(\""+record.data.username+"\")'>"+value+"</a>";
   }
   function edithunter(username){
	   var store = Ext.data.StoreManager.lookup('headhunteruser_dataStore');
	   var data = {};
	   var index = store.findExact('username',username);
	   Ext.apply(data,store.getAt(index).data);//复制对象
	   
	   data.isused =  data.isused.indexOf('1')==0?true:false;
	   data.isleader =  data.isleader.indexOf('1')==0?true:false;
	   createFormWindow('update',data,index);
   }
   
   
   function inserthunter(){
	   createFormWindow('insert');
   }
   
   function actionRow(){
	   if(!<%=isEditable%>)
		   return;
	   
	   var rowdata = {}; 
	   Ext.apply(rowdata,arguments[1].data);//复制对象
	   rowdata.isused =  rowdata.isused.indexOf('1')==0?true:false;
	   rowdata.isleader =  rowdata.isleader.indexOf('1')==0?true:false;
	   createFormWindow('update',rowdata,arguments[3]);
   }
   
   function createFormWindow(type,inputdata,index){
	   var tablekey = headhunteruser.getTableConfig().tablekey; 
	   var title="修改账号信息";
	   var buttons = [{
		   xtype:'button',
		   text:'保存',
		   id:'savebutton',
		   //width:100,
		   formBind:true,
		   //height:30,
		   disabled:true,
		   handler:function(){
			   var values = this.up('form').getForm().getFieldValues();
			   var vo = new HashMap();
			   vo.put("commitdata",values);
			   vo.put("committype",type);
			   vo.put("tablekey",tablekey);
			   vo.put("huntergroupid","${headHunterGroupForm.huntergroupid}");
			   Rpc({functionId:'ZP0000002184',success:function(res){
				   var param = Ext.decode(res.responseText);
				   if(!param.result){
					   alert("保存失败");
					   return;
				   }
				   /**var store = Ext.data.StoreManager.lookup('headhunteruser_dataStore');
				   store.reload();
				   insertWin.close();**/
				   window.location= window.location;
				   
			   }},vo);
		   }
	   }];
	   if(type=='insert'){
		   title="新增账号";
		   buttons.push({xtype:'component',width:30});
		   buttons.push({
			   xtype:'button',
			   text:'保存&继续',
			   id:'nextbutton',
			   //width:100,
			   formBind:true,
			   //height:30,
			   handler:function(){
				   var values = this.up('form').getForm().getFieldValues();
				   var vo = new HashMap();
				   vo.put("commitdata",values);
				   vo.put("committype",type);
				   vo.put("tablekey",tablekey);
				   vo.put("huntergroupid","${headHunterGroupForm.huntergroupid}");
				   Rpc({functionId:'ZP0000002184',success:function(res){
					   var param = Ext.decode(res.responseText);
					   if(!param.result){
						   alert("保存失败");
						   return;
					   }
					   var store = Ext.data.StoreManager.lookup('headhunteruser_dataStore');
					   values.isused = values.isused?'1`是':'2`否';
						values.isleader = values.isleader?'1`是':'2`否';
						
					   if(type=='update'){
						   store.getAt(index).data=values;
						   Ext.getCmp("headhunteruser_tablePanel").getView().refresh();
						   insertWin.close();
						   return;
					   }
					   
					    Ext.define('TG.model.DataModel',{
							extend:'Ext.data.Model'
						});
						var newRecord = new TG.model.DataModel();
						
						Ext.apply(newRecord.data,values);
						
						store.insert(store.getCount(),newRecord);
					   insertWin.queryById('inputForm').getForm().reset();
				   }},vo);
				   
			   }
		   });
	   }
	   
	   buttons.push({xtype:'component',width:30});
	   buttons.push({
		   xtype:'button',
		   text:'关闭',
		   handler:function(){insertWin.close();}
	   });
	   
	   var insertWin = Ext.widget("window",{
		   modal:true,
		   title:title,
		   id:'editWindow',
		   height:500,
		   shadow:false,
		   width:500,
		   resizable:false,
		   layout:'fit',
		   border:false,
		   items:[{
			   xtype:'form',
			   itemId:'inputForm',
			   header:false,
			   layout: {
			        type: 'vbox',
			        align: 'center',
			        padding:'20 0 20 0'
			    },
			    border:false,
			   items:[{
				   xtype:'textfield',
				   fieldLabel:'账号',
				   name:'username',
				   vtype:'alphanum',
				   labelSeparator:null,
				   inputId:'unfield',
				   maxLength:50,
				   enforceMaxLength:true,
				   labelWidth:60,
				   allowBlank:false,
				   beforeLabelTextTpl:"<font color='red'> * </font>",
				   readOnly:type=='insert'?false:true,
				   width:350,
				   margin:'20 0 0 0',
				   labelAlign:'right',
				   listeners:{
					   render:function(){ if(type!='insert')document.getElementById("unfield").disabled=true;},
					   change:function(self,newValue){
						   var hh = this;
						   if(newValue=='' || type!='insert')
							   return;
						   if(!Ext.getDom('warningSpan')){
							   var tbody = document.createElement("tbody"); 
						      var tr = document.createElement("tr"); 
						      var td = document.createElement("td"); 
						      td.style.paddingLeft="35px";
						      td.colSpan=2;
						      td.innerHTML="<span id=\"warningSpan\" style=\"color:red;padding-left:30px;\"></span>";
						      tr.appendChild(td);
						      tbody.appendChild(tr);
						      self.getEl().appendChild(tbody); 
						   }
						   
						   var vo = new HashMap();
						   vo.put("checkType","username");
						   vo.put("value",newValue);
						   Rpc({functionId:'ZP0000002183',success:function(res){
							   var resultObj = Ext.decode(res.responseText);
							   if( typeof resultObj.result=="string"){
								   Ext.getDom('warningSpan').innerHTML= resultObj.result;
								   Ext.getCmp('savebutton').setDisabled(true);
								   Ext.getCmp('nextbutton').setDisabled(true);
							   }else{
								   Ext.getDom('warningSpan').innerText= "";
								   Ext.getCmp('savebutton').setDisabled(!hh.ownerCt.ownerCt.queryById('inputForm').isValid());
							   	   Ext.getCmp('nextbutton').setDisabled(!hh.ownerCt.ownerCt.queryById('inputForm').isValid());
							   }
								   
						   }},vo);
					   }
				   }
			   },{
				   xtype:'textfield',
				   fieldLabel:'密码',
				   name:'password',
				   labelSeparator:null,
				   maxLength:50,
				   enforceMaxLength:true,
				   allowBlank:false,
				   vtype:'alphanum',
				   beforeLabelTextTpl:"<font color='red'> * </font>",
				   labelWidth:60,
				   width:350,
				   margin:'30 0 0 0',
				   labelAlign:'right'
			   },{
				   xtype:'textfield',
				   fieldLabel:'姓名',
				   labelSeparator:null,
				   name:'name',
				   maxLength:50,
				   enforceMaxLength:true,
				   labelWidth:60,
				   allowBlank:false,
				   beforeLabelTextTpl:"<font color='red'> * </font>",
				   width:350,
				   margin:'20 0 0 0',
				   labelAlign:'right',
				   listeners:{
					    blur:{
							fn:function(obj){
								obj.setValue(obj.getValue().replace(/</g,"＜"));
							}
					    }
				   }
			   },{
				   xtype:'textfield',
				   fieldLabel:'邮箱',
				   allowBlank:false,
				   labelSeparator:null,
				   beforeLabelTextTpl:"<font color='red'> * </font>",
				   name:'email',
				   vtype:'email',
				   maxLength:100,
				   enforceMaxLength:true,
				   labelWidth:60,
				   width:350,
				   margin:'20 0 0 0',
				   labelAlign:'right',
				   listeners:{
					   change:function(self,newValue){
						   var hh = this;
						   if(newValue=='' || type!='insert')
							   return;
						   if(!Ext.getDom('emalWarningSpan')){
							   var tbody = document.createElement("tbody"); 
						      var tr = document.createElement("tr"); 
						      var td = document.createElement("td"); 
						      td.style.paddingLeft="35px";
						      td.colSpan=2;
						      td.innerHTML="<span id=\"emalWarningSpan\" style=\"color:red;padding-left:30px;\"></span>";
						      tr.appendChild(td);
						      tbody.appendChild(tr);
						      self.getEl().appendChild(tbody); 
						   }
						   
						   var vo = new HashMap();
						   vo.put("checkType","email");
						   vo.put("value",newValue);
						   Rpc({functionId:'ZP0000002183',success:function(res){
							   var resultObj = Ext.decode(res.responseText);
							   if( typeof resultObj.result=="string"){
								   Ext.getDom('emalWarningSpan').innerHTML= resultObj.result;
								   Ext.getCmp('savebutton').setDisabled(true);
								   Ext.getCmp('nextbutton').setDisabled(true);
							   }else{
								   Ext.getDom('emalWarningSpan').innerText= "";
							   	   Ext.getCmp('savebutton').setDisabled(!hh.ownerCt.ownerCt.queryById('inputForm').isValid());
							   	   Ext.getCmp('nextbutton').setDisabled(!hh.ownerCt.ownerCt.queryById('inputForm').isValid());
							   }
								   
						   }},vo);
					   }
				   }
			   },{
				   xtype:'textfield',
				   fieldLabel:'固定电话',
				   labelSeparator:null,
				   name:'tel',
				   labelWidth:60,
				   regex:new RegExp('^(0[0-9]{2,3}\-)?([2-9][0-9]{6,7})+(\-[0-9]{1,4})?$'),
				   regexText:'请输入电话号码',
				   width:350,
				   margin:'30 0 0 0',
				   labelAlign:'right'
			   },{
				   xtype:'textfield',
				   fieldLabel:'移动电话',
				   labelSeparator:null,
				   name:'phone',
				   labelWidth:60,
				   regex:new RegExp('^1[0-9]{10}$'),
				   regexText:'请输入正确手机号',
				   width:350,
				   margin:'30 0 0 0',
				   labelAlign:'right'
			   },{
				   xtype:'container',
				   margin:'30 0 0 0',
				   layout:'hbox',
				   items:[{
					   xtype:'checkboxfield',
					   fieldLabel:'启用',
					   name:'isused',
					   checked:true,
					   labelAlign:'right'
				   },{
					   xtype:'checkboxfield',
					   fieldLabel:'主账号',
					   name:'isleader',
					   labelAlign:'right'
				   }]
			   },{
				   xtype:'container',
				   margin:'30 0 0 0',
				   layout:'hbox',
				   items:buttons
			   }]
		   }]
	   }).show();
	   if(type=='update')
		   Ext.getCmp('editWindow').queryById('inputForm').getForm().setValues(inputdata);
   }
   Ext.onReady(function(){
	   <hrms:tableFactory title="账号管理"   subModuleId="zp_headhunter_00002" constantName="${headHunterGroupForm.constantxml}" jsObjName="headhunteruser" autoRender="true" columnProperty="usercolumns" formName="headHunterGroupForm" paginationProperty="counterpageable"   currentPage="${headHunterGroupForm.counterpageable.pageNumber }" sqlProperty="sqlstr" ></hrms:tableFactory>
   });
</script>
