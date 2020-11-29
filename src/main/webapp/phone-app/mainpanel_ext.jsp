<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>
	 <link rel="stylesheet" href="../phone-app/sencha/css/sencha-touch.css" type="text/css">
	 <script type="text/javascript" src="../phone-app/sencha/sencha-touch.js"></script>
	 <style>
		.myicon {background: url(/phone-app/images/myicon.png) left top no-repeat !important;}	 
	 </style>
</head>
<body>
  
<script type="text/javascript">
			function runner(button, event)
			{
			  alert(button.ui);
			  //alert(button);
			   window.open('test.jsp','_blank'); 
			};

Ext.setup({

    onReady: function() {

           var panel = new Ext.Panel({fullscreen: true,padding:'10 10 10 10',defaults: {layout: {type: 'hbox',align:'left'}, flex: 1, defaults: {xtype: 'button',  flex : 1}},layout: {type : 'vbox',align:'top'}, items:[{items:[{margin:'5 5 5 5',ui:'action',height: 64, width:64,text:'',handler:runner},{margin:'5 5 5 5',ui:'action',height: 64, width:64,text:'',handler:runner},{margin:'5 5 5 5',ui:'action',height: 64, width:64,text:''},{margin:'5 5 5 5',ui:'action',height: 64, width:64,text:'',iconCls:'myicon'}]},{items:[{margin:'5 5 5 5',ui:'action',height: 64, width:64,text:''},{margin:'5 5 5 5',ui:'action',height: 64, width:64,text:''},{margin:'5 5 5 5',ui:'action',height: 64, width:64,text:''},{margin:'5 5 5 5',ui:'action',height: 64, width:64,text:''}]},{items:[{margin:'5 5 5 5',ui:'action',height: 64, width:64,text:''},{margin:'5 5 5 5',ui:'action',height: 64, width:64,text:''}]}]});



    }
});

</script>
</body>
</html>