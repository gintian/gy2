<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<html>
<head>
	<title>jquery培训</title>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>

<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>

<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript" src="/js/validate.js"></script>
<script language="javascript" src="/js/constant.js"></script>

 	<style>
    	a.test { font-weight: bold; }
 	</style>
	

</head>
   <body style="height:100%;overflow:hidden;margin:0;padding:0">
<table width="100%" height="100%" align="center" border="0" cellpadding="0" cellspacing="0">
  <tr>  
    <td valign="top" style="overflow:auto;">  
      <form action=""  style="height:100%;width:100%">

		<link type="text/css" rel="stylesheet" href="/jquery/themes/default/easyui.css"><link type="text/css" rel="stylesheet" href="/jquery/themes/icon.css"><script type="text/javascript" language="javascript" src="/jquery/jquery-3.5.1.min.js"></script><script type="text/javascript" language="javascript" src="/jquery/jquery.easyui.min.js"></script>
		<script language="javascript" src="/jquery/jquery.hjsofttool.js"></script>		
      <div class="easyui-tabs" fit="true" plain="true" id="hjtab">
	
	   </div>
      </form>		
    </td>
  </tr>
</table>
<script type="text/javascript">
	$(document).ready(function(){
			var mainmenuobj=[{id:'aaa',text:'aaa1',href:'/system/security/about_hrp.do?b_query=link',selected:true,iconCls:''},{id:'bbb',text:'bbb1',href:'http://www.sina.com',selected:true,iconCls:''}];
			$('#hjtab').addTab(mainmenuobj);
	});
</script>
<script type="text/javascript">
	$(document).ready(function(){
		alert("hello world!");
	});
</script>
   </body>
</html>