<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7">
 <meta http-equiv="content-type" content="text/html; charset=UTF-8">
</head>
<script language="JavaScript" src="../../module/utils/js/template.js"></script>
<script type="text/javascript">
Ext.Loader.setConfig({
    enabled: true,
    paths: {
        "EHR":rootPath+"/components",
        'OfficerMange': '/module/officermanage'
    	}
    });
Ext.onReady(function(){
	Ext.require('OfficerMange.Setting',function(){
        officeSetGoal = Ext.create("OfficerMange.Setting",{
       	    
        });
    });
});
</script>






