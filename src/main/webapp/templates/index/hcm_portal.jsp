<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.SystemConfig,com.hjsj.hrms.businessobject.sys.SysParamBo" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%  
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
//主题皮肤
String themes = "default";
if(userView != null){ 
    themes = SysParamBo.getSysParamValue("THEMES",userView.getUserName());   
 }
%>

	 <link rel="stylesheet" type="text/css" href="../../ext/resources/css/ext-all.css" />
	 <!--link rel="stylesheet" type="text/css" href="../../ext/resources/css/slate.css" /> 
	<script type="text/javascript" src="../../ext/adapter/ext/ext-base.js"></script>-->
	<link rel="stylesheet" type="text/css" href="/components/homewidget/funwindow.css" />
	
	<script type="text/javascript" src="../../ext/ext-all.gzjs"></script>
	<script type="text/javascript" src="../../ext/rpc_command.js"></script> 
	<script type="text/javascript" src="Portal.js"></script>
	<script type="text/javascript" src="Portal2.js"></script>
    <script type="text/javascript" src="/js/constant.js"></script>
	<link rel="stylesheet" type="text/css" href="portal.css" />	
	<link rel="stylesheet" type="text/css" href="hcm_portal.css" />	
	
	<link rel="stylesheet" type="text/css" href="/css/hcm/themes/<%=themes %>/hcm_portal.css" />
	<script type='text/javascript' src='/module/system/portal/jobtitle/JobtitlePortal.js'></script>
	<script type='text/javascript' src='/ajax/basic.js'></script>
	<style type="text/css">
		.x-toolbar-default{background-color:#f5f5f5;background-image:none;}
		.x-tool-close{
   		 	margin-left:38px;
		}
		a{
			font-size:14px;
		}
		A:visited{
			color: #000000;
			TEXT-DECORATION:none;
			font-size:14px;
		}
		a.tt:hover {color: #FFAE00;font-size:14px !important}/**申报信息链接鼠标悬浮变色修改**/
		a:hover {color: #FFAE00;font-size:14px !important}/** bug:51686 */
		A:link{
			color: #000000 ;
			TEXT-DECORATION:none;
			font-size:14px;
		}
		
		.x-panel {
			border-color: #C5D7E6;
			border-style: solid;
			border-width: 0px;
			background: white;
		}
		
		.backgroundColor {
			background:white; 
		}
	</style>
<script type="text/javascript">
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
<html:form action="/templates/index/portal">
<br>
<input id="htmlparam" type="hidden" name="html_param"/>
<html:hidden name="homeForm" property="dbpre"/>
<a style="display:none" id="more_a" href="###" target="_blank"></a>
<script type="text/javascript">
//防止打开多个页面造成数据混乱 guodd 2015-12-18
window.document.oncontextmenu = function(){return false;};
Ext.BLANK_IMAGE_URL="../../ext/resources/images/default/s.gif";
Ext.define('Ext.ux.Portlet', {
	extend : 'Ext.panel.Panel',

	alias: 'widget.portlet', 

    layout: 'fit', 
    anchor: '100%', 
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
Ext.Loader.setPath("EHR","/components");
Ext.require("EHR.homewidget.WorkTable");
Ext.require("EHR.homewidget.ServiceHall");
Ext.require("EHR.homewidget.TemplateTable");
Ext.onReady(function(){
	
    Ext.state.Manager.setProvider(new Ext.state.CookieProvider());
    <hrms:extportal portalid="02" />	
  	<hrms:sysmessage></hrms:sysmessage>
	if(!viiewportal)
  		window.viiewportal = Ext.getCmp('viiewportal');
  	
  	function _more(id){
  		var more_href=document.getElementById(id).getAttribute('more_href');
  		var more_target=document.getElementById(id).getAttribute('more_target');
  		if(more_href){
  			document.getElementById('more_a').target=more_target;
  			document.getElementById('more_a').href=more_href;
  			document.getElementById('more_a').click();
  		}else{
  			alert("请定义more功能链接!");
  		}
  	}
});//.dom.children[0]

/* 
 * 移除指定element下样式class为clsName的element
 * clsName:给定类名 
 * tag：给定的HTML元素 element
*/ 
function removeElementsByClassName(clsName, tag) { 
	//tag.setVisible (false);
	tag = document.getElementById(tag.getId())
    if(tag.className.indexOf(clsName)){
        tag.style.display='none';
    }else{
        var selElements = tag.children; 
        for(var i=0;i<selElements.length;i++) {
            //alert(selElements[i].className);
            if (selElements[i].className.indexOf(clsName)!=-1) {            
                //tag.removeChild(selElements[i]);
                selElements[i].style.display='none';
                break;
            } else{
                if(selElements[i].children.length>0)
                    removeElementsByClassName1(clsName, selElements[i]);
            }
        }
    }
} 
function removeElementsByClassName1(clsName, tag) {
    selElements = tag.children; 
    for(var i=0;i<selElements.length;i++) {
        //alert(selElements[i].className);
        if (selElements[i].className.indexOf(clsName)!=-1) {        	
        	//tag.removeChild(selElements[i]);
        	selElements[i].style.display='none';
        	break;
        } else{
        	if(selElements[i].children.length>0)
        		removeElementsByClassName1(clsName, selElements[i]);
        }
    }   
} 

function showElementsByClassName(clsName, tag) { 
    selElements = tag.children;  
    for(var i=0;i<selElements.length;i++) {
        if (selElements[i].className.indexOf(clsName)!=-1) {  
            //alert(selElements[i].className);
            //tag.removeChild(selElements[i]);
            selElements[i].style.display='block';
            break;
        } else{
            if(selElements[i].children.length>0)
            	showElementsByClassName(clsName, selElements[i]);
        }
    }   
} 
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
