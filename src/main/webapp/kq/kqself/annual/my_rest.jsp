<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Frameset//EN" "http://www.w3.org/TR/html4/frameset.dtd">
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html>
	<head>
		<title></title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta http-equiv="X-UA-Compatible" content="IE=7;IE=8;IE=9;">
		<script language="JavaScript" src="/module/utils/js/template.js"></script>
	    <link href='/ext/ext6/resources/ext-theme.css' rel='stylesheet' type='text/css'><link>
	    <hrms:priv func_id="0B10">
		<script type="text/javascript">
		Ext.onReady(function(){  
		  var vp = new Ext.Viewport({  
			enableTabScroll:true,      
			layout:"fit",
			padding: "5,0,0,0",
			items: [
			{
			        xtype: 'tabpanel',
			        heigth: '100%',
			        width: '100%',	
			        bodyStyle: 'border-width: 0px 1px 1px 1px;',
			        items: [
					{
					    xtype: 'panel',
					    title: '我的假期',
					    padding: '5',
					    html: '<iframe scrolling="auto" frameborder="0" width="100%" height="100%" src="/kq/kqself/annual/my_n_annual.do?b_query=link&table=Q17"> </iframe>'
					},
					{
						xtype: 'panel',
					    title: '休假明细',
					    padding: '5',
					    html: '<iframe scrolling="auto" frameborder="0" width="100%" height="100%" src="/kq/kqself/annual/my_n_annual.do?b_query=link&table=Q15"> </iframe>'
					}
					<logic:equal name="myAnnualForm" property="isshow" value="1">
					,
					{
						xtype: 'panel',
					    title: '我的调休',
					    padding: '5',
					    html: '<iframe scrolling="auto" frameborder="0" width="100%" height="100%" src="/kq/kqself/annual/my_n_annual.do?b_query=link&table=Q33"> </iframe>'
					}
					</logic:equal>
			        ]
			}]
		  }); 
		});
		</script>
		</hrms:priv>
	</head>
	<body>
    </body>
</html>
