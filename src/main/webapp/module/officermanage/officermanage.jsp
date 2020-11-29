<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7">
 <meta http-equiv="content-type" content="text/html; charset=UTF-8">
</head>
<script language="JavaScript" src="../../module/utils/js/template.js"></script>
<script language='JavaScript' src='../../../components/tableFactory/tableFactory.js'></script>
<link rel="stylesheet" href="../../../ext/ext6/resources/ext-theme.css" type="text/css" />
<script language="JavaScript" src="../../jquery/jquery-3.5.1.min.js"></script>
<script type="text/javascript">
Ext.Loader.setConfig({
    enabled: true,
    paths: {
        'Officermanage': '/module/officermanage'
    	}
    });
Ext.onReady(function(){
	Ext.require('Officermanage.ManageView',function(){
        managerGobal = Ext.create("Officermanage.ManageView",{
       	    
        });
    });
});
</script>






