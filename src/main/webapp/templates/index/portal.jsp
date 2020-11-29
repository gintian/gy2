<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
	<!-- <link rel="stylesheet" type="text/css" href="../../ext/resources/css/ext-all.css" />
	<script type="text/javascript" src="../../ext/ext-all.js"></script>
	<script type="text/javascript" src="../../ext/ext-lang-zh_CN.js"></script> -->
	<hrms:linkExtJs frameDegradeId="framedegrade"/>
	<% if("true".equals(framedegrade)){ %>
	<script type="text/javascript" src="Portal-old.js"></script>
	<% }else{ %>
	<script type="text/javascript" src="Portal.js"></script>
	<%} %>
	<link rel="stylesheet" type="text/css" href="portal.css" />	
	<script type="text/javascript" src="../../ext/rpc_command.js"></script> 
	<script type="text/javascript" src="Portal2.js"></script>
    <script type="text/javascript" src="/js/constant.js"></script>
    <script type="text/javascript" src="/jquery/jquery-3.5.1.min.js"></script>
    <script type="text/javascript" src=/module/system/portal/jobtitle/JobtitlePortal.js></script>
    <script type='text/javascript' src='/ajax/basic.js'></script>
	  	
	<style type="text/css">
		.x-panel-tl{background:transparent url(../../ext/resources/images/default/panel/h3_bg.gif) repeat-x 0 0;padding-left:6px;zoom:1;border-bottom:1px solid #99bbe8;}
		.x-panel-tr{background:transparent url(../../ext/resources/images/default/panel/h3_bg.gif) no-repeat right 0;zoom:1;padding-right:6px;}
		.x-panel-tc{background:transparent url(../../ext/resources/images/default/panel/h3_bg.gif) repeat-x 0 0;overflow:hidden;}
		.x-panel-tl{background:transparent url(../../ext/resources/images/default/panel/h3_bg.gif) repeat-x 0 0;padding-left:6px;zoom:1;border-bottom:1px solid #99bbe8;}
		.x-panel-tr{background:transparent url(../../ext/resources/images/default/panel/h3_bg.gif) no-repeat right 0;zoom:1;padding-right:6px;}
	</style>
	<style type="text/css">
        .x-panel-mc .x-panel-body {
            background:white;
            /*background-color:#F4F7F7;
            background-image:url(/images/tal_jb.png);*/
            line-height: 50px;          
            border:0;  
            background-repeat:repeat-x;  
			text-align: left;			
			background-position:top right; 
        }
       .my-icon{ background-image: url(../../images/hmc.gif) 0 6px no-repeat !important; }  
       .x-panel-header-text {
       	font-size:12px
       	font:bold 12px tahoma,arial,verdana,sans-serif;
       }  
       .x-toolbar-default{background-color:#f5f5f5;background-image:none;}
    </style>	
    <link href="../../ext/hr6.css" rel="stylesheet" type="text/css" />
<script type="text/javascript">
//防止打开多个页面造成数据混乱 guodd 2015-12-18
window.document.oncontextmenu = function(){return false;};
<!--
	function openlink(url)
	{
    	//var dbpre=Ext.get('dbpre').value //$F("dbpre");
    	var hidobj=document.getElementById('dbpre');
    	var dbpre=hidobj.value;    	
    	url=url+"&dbpre="+dbpre;
    	window.open(url,"_self","toolbar=no,location=no,directories=0,status=no,menubar=no,scrollbars=no,resizable=no","true");
	}
//-->
</script>   
</head>
  <body>
<html:form action="/templates/index/portal">
<br>
<input id="htmlparam" type="hidden" name="html_param"/>
<html:hidden name="homeForm" property="dbpre"/>

<script type="text/javascript">
Ext.BLANK_IMAGE_URL="/images/s.gif";

<% if("true".equals(framedegrade)){ %>
Ext.ux.Portlet = Ext.extend(Ext.Panel, {
    anchor: '100%',
    frame:true,
    collapsible:true,
    draggable:true,
    cls:'x-portlet'
});
Ext.reg('portlet', Ext.ux.Portlet);

Ext.ux.PortalColumn = Ext.extend(Ext.Container, {
    layout: 'anchor',
    autoEl: 'div',
    defaultType: 'portlet'
});
Ext.reg('portalcolumn', Ext.ux.PortalColumn);
<% }else{ %>
Ext.define('Ext.ux.Portlet', {
	extend : 'Ext.panel.Panel',

	alias: 'widget.portlet', 

    layout: 'fit', 
    anchor: '100%', 
    frame: true, 
    collapsible: true, 
    animCollapse: true, 
	draggable: {  
        moveOnDrag: false 
    },  

    cls:'x-portlet'
});
 
 Ext.define('Ext.ux.PortalColumn', {
	extend : 'Ext.container.Container',

	alias: 'widget.portalcolumn',  
    layout: 'anchor',
    autoEl: 'div',
    defaultType: 'portlet',
	cls: 'x-portal-column'
}); 
<% } %>
 



Ext.onReady(function(){

    Ext.state.Manager.setProvider(new Ext.state.CookieProvider());
    <hrms:extportal portalid="02" />	
  	<hrms:sysmessage></hrms:sysmessage>
  	});
</script>
	<div id='wait' style='position:absolute;top:200px;left:250px;display:none;'>
  <table border="1" width="300px" cellspacing="0" cellpadding="4" class="table_style" height="87px" align="center">
           <tr>

             <td class="td_style" height=24px><bean:message key="classdata.isnow.wiat"/></td>

           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center>
               <marquee class="marquee_style" direction="right" width="300px" scrollamount="5" scrolldelay="10">
                 <table cellspacing="1" cellpadding="0">
                   <tr height=8px>
                     <td bgcolor=#3399FF width=8px></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8px></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8px></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8px></td>
                         <td></td>
                    </tr>
                  </table>
               </marquee>
             </td>
          </tr>
        </table>
</div>
</html:form>
  </body>
  <script language="javascript" type="text/javascript">
//<!--
/*********************************************
  - Marquee 演示
*********************************************/
//new Marquee({obj : 'MyMarqueeY',mode : 'y'});
//-->
</script>
</html>