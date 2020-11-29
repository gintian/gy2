<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page
	import="com.hjsj.hrms.utils.components.emailtemplate.actionform.TemplateSetForm"%>
<%@ page import="java.util.*"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">


	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<link href="/module/recruitment/css/style.css" rel="stylesheet"
			type="text/css" />
		<script language="JavaScript"src="/components/tableFactory/tableFactory.js"></script>
		<script language="JavaScript"src="/components/emailtemplate/searchTemplate/emailTemplateList.js"></script>
		<script language="javascript">
		
	var tablegrid = undefined;
 Ext.onReady(function(){
        <hrms:tableFactory  jsObjName="tablegrid" sqlProperty="str_sql"  orderbyProperty="orderbystr"   constantName="recruitment/template" 
    		subModuleId="RE_Email_00000001" columnProperty="columns" formName="templateReForm" currentPage="${templateReForm.current}" pagesize="${templateReForm.pagesize}">
        	<hrms:buttonTag buttonsProperty="buttonList" />
    	</hrms:tableFactory>    
    	Global.opt = '${templateReForm.opt}';
    	Global.isShowModuleType = '${templateReForm.isShowModuleType}';
    	Global.isShowInsertFormula = '${templateReForm.isShowInsertFormula}';
    	Global.isShowModifyFormula = '${templateReForm.isShowModifyFormula}';
    	Global.isShowItem = '${templateReForm.isShowItem}';
    	Global.isShowAttachId = '${templateReForm.isShowAttachId}';
    	var params = new Object();
        params.opt=Global.opt;
        params.isShowModuleType=Global.isShowModuleType;
        params.isShowInsertFormula=Global.isShowInsertFormula;
        params.isShowModifyFormula=Global.isShowModifyFormula;
        params.isShowItem=Global.isShowItem;
        params.isShowAttachId=Global.isShowAttachId;
        Ext.getCmp("tablegrid_querybox").setCustomParams(params);//区分快速查询的时候是哪个模块进入的
    	tablegrid.setBorderLayoutRegion("center");
    	//tablegrid.renderTo("table1"); 
    	 Ext.widget('viewport',{
 	    	layout:'border',
 	    	padding:"0 5 0 5",
 	    	style:'backgroundColor:white',
 	    	items:[{
 	    			  xtype:'panel',title:Global.fromName,
 	    			  html:"<div id='topPanel'></div>",
 	    			  region:'north',height:40,border:false
 	    			},
 	    	       tablegrid.getMainPanel()]
 	    });     
 	   document.getElementById('topPanel').appendChild(document.getElementById('funcDiv'));
 	   document.getElementById('funcDiv').style.display="block";
 	  Ext.create('Ext.panel.Panel',{
	       width: 300,
	       height: 22,
	       layout:'column',
	       style:'margin-top:-11px',
	       items: [{
	           xtype: 'image',
	           columnWidth: .1,
	           src: '/module/recruitment/image/aaa.png',
	           imgCls: 'img-zoom-pos',
	           listeners: {
	               click: {
	                   element: 'el', //bind to the underlying el property on the panel
	                   fn: function(){  Global.fastSearch(); }
	               }
	           }
	           
	       },{
	           id: 'boxtext',
	           xtype: 'textfield',
	           columnWidth: .85,
	           fieldStyle:'border:0px solid',
	           emptyText:"请输入模板类型、名称或邮件主题…",
	           inputWrapCls: 'border-width-style',
	           listeners:{    
	               specialkey:function(field,e){    
	                   if (e.getKey()==Ext.EventObject.ENTER){  
	                       Global.fastSearch();
	                   }    
	               } ,
	               blur:function(){
	               	Global.fastSearch();
	               }  
	           }  
	       }],
	         renderTo: Ext.get('fastsearch')
	   });
    });
   


 </script>
 <style>
.border-width-style {
	border: 0px solid;
	line-height: 22px;
	letter-spacing: 0px;
	text-align: left;
	word-spacing: 0px
}

.img-zoom-pos {
	zoom: 0.9;
	margin: 2px 1px 0 1px;
}
</style>
	</head>
	<body>

		<form id="form2"
			action="/recruitment/emailtemplate/emailTemplateList.do">


			<!--  
				<div style="float: left; width: 200; margin-left: 0 px;">

							<logic:equal name="templateReForm" property="id" value="32">
								<bean:message key="lable.tz_template.zploop1" />
								<hrms:optioncollection name="templateReForm"
									property="zpLoop_list" collection="list" />
								<html:select name="templateReForm" property="zpLoop" size="1"
									onchange="ZploopSearchType();">
									<html:options collection="list" property="dataValue"
										labelProperty="dataName" />
								</html:select>
							</logic:equal>
</div>
-->

			<div id="funcDiv" style="display: none">

			</div>
		</form>

	</body>
</html>
