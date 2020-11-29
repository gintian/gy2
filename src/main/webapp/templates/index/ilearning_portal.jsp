
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

	<link rel="stylesheet" type="text/css" href="/jquery/plugin/portal.css">	
	<script type="text/javascript" src="/templates/index/jquery.menutool-1.0.js"></script>     
<style>
	.panel-header{
	    padding:5px;
	    line-height:15px;
	    color:#15428b;
	    font-weight:bold;
	    font-size:12px;
	    background:url('/images/footer_bg.jpg') scroll;
	    position:relative;
	    //border:0px solid #99BBE8;
	    border-bottom-width: 1px;
	}
</style>

<body>
<html:form action="/templates/index/ilearning_portal">
<div id="ilearning_portal" style="height:100%;width:100%;overflow:hidden;margin:0;padding:0;"></div> 
<script type="text/javascript">
$(document).ready(function(){
    <hrms:extportal portalid="03" portaltype="jquery"/>	
    /**portals为标签输出门户对象*/
	$('#ilearning_portal').addPortal(portals);
	//$('body').addPortal(portals);
});
</script>

</html:form>
  
    
</body>
<script type="text/javascript" src="/jquery/plugin/jquery.portal.js"></script>
