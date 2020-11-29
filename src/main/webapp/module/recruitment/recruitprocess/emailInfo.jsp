<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="java.util.HashMap" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
   <link rel="stylesheet" type="text/css" href="../../ext/resources/css/ext-all.css" />
    <script type="text/javascript" src="../../ext/ext-all.js" ></script>
    <script type="text/javascript" src="../../ext/ext-lang-zh_CN.js" ></script> 
    <script type="text/javascript" src="../../ext/rpc_command.js"></script>
<link href="/components/tableFactory/tableGrid-theme/tableGrid-theme-all.css" rel="stylesheet" type="text/css"><link >
    <style type="text/css">
    .x-panel-body-default{border:none;}
    </style>
<script type="text/javascript">
Ext.onReady(function(){
	 var panel=new Ext.form.FormPanel({
		 
		    layout:"form",
		 
		    frame:false,

		    border:0,
		 
		    labelWidth:35,
		    
		    width:475,

		    buttonAlign: 'center',
		    
	        height:450,
	        
		    labelAlign:"left",
		 
		    items:[{
		 
		      layout : "column",

		      border:0,
		 
		      items : [{
		 
		        columnWidth : .0,
		 
		        layout : "form",
		  
		        items : [{
		            
                xtype:"hidden",
         
                fieldLabel : "邮箱",

                labelWidth:35,
         
                anchor : "96%",
         
                value: "<bean:write name='recruitProcessForm' property='emailInfo.c0102'/>",
         
                id:"c0102"
         
               },{
		 
		        xtype:"textfield",
		 
		        fieldLabel : "标题",

		        labelWidth:35,
		 
		        anchor : "96%",
		 
		        value: "<bean:write name='recruitProcessForm' property='emailInfo.title'/>",
		 
		        id:"title"
		 
		       },{
		           
	             xtype:"textarea",
	         
	             height : 370,
	         
	             fieldLabel : "内容",

	             labelWidth:35,
	         
	             id:"content",
	         
	             value: "<bean:write name='recruitProcessForm' property='emailInfo.content'/>",
	         
	             anchor : "96%"
	         
	            }]
		 
		      }]
		 
		   }],
	         buttons:[
	                  {text:"发送",handler:function(){
		                  var c0102 = Ext.getCmp("c0102").getValue();
	                	  var title = Ext.getCmp("title").getValue();
	                	  var content = Ext.getCmp("content").getValue();
	                	  var hashvo=new ParameterSet();
                	      hashvo.setValue("c0102",getEncodeStr(c0102));
                	      hashvo.setValue("title",getEncodeStr(title));
                	      hashvo.setValue("content",getEncodeStr(content));
                	      var request=new Request({method:'post',asynchronous:true,onSuccess:parent.window.Global.returnEmail,functionId:'ZP0000002304'},hashvo);
		                  }},
	                  {text:"关闭",handler:function(){parent.window.Global.emailClose();}}
	                  ] //底部按钮
		});
	   panel.render('panel');

})
</script>
  </head>
  <div id="panel"></div>
  <body style="vertical-align: text-top"></body>
</html>
