<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
   <link href="common.css" rel="stylesheet" type="text/css" id="skin">
   <script language="JavaScript" src="designer.js"></script>   
	<style type="text/css" media="screen">
		div.base {
			position: absolute;
			overflow: hidden;
			white-space: nowrap;
			font-family: Arial;
			font-size: 8pt;
		}
		div.base#graph {
			border-style: solid;
			border-color: #F2F2F2;
			border-width: 1px;
			background: url('images/grid.gif');
		}
	</style>   
<script type="text/javascript">
<!--

//-->
</script>
<html:form action="/general/template/flowdesigner/designer" >

<div class="mxWindow" style="Z-INDEX: 1; LEFT: 50px; TOP: 2px">
<table style="Height: 20px;width:300px" class="mxWindow" align="center">
	<tr>
		<td class="mxWindowTitle" style="CURSOR: move" width="10">
		</td>
		<td  class="mxWindowPane" valign="middle" align="left">
			<div class="mxWindowPane" style="WIDTH: 100%; HEIGHT: 100%">
				<div style="PADDING-RIGHT: 0px; PADDING-LEFT: 5px; PADDING-BOTTOM: 5px; PADDING-TOP: 4px">
					<img src="./images/save.gif" height="16" width="16" title="hello" class="mxToolbarItem" onclick="init('svgmap');">
					<img src="./images/delete.gif" height="16" width="16" title="hello" class="mxToolbarItem">
				</div>
			</div>
		</td>		
	</tr>

</table>
</div>
<div class="mxWindow" style="Z-INDEX: 1; LEFT: 11px; TOP: 32px">
<table style="WIDTH: 30px" class="mxWindow" align="center">
	<tr>
		<td class="mxWindowTitle" style="CURSOR: move">
			工具
		</td>
	</tr>
	<tr>
		<td  class="mxWindowPane">
			<div class="mxWindowPane" style="WIDTH: 100%; HEIGHT: 100%">
				<div style="PADDING-RIGHT: 0px; PADDING-LEFT: 5px; PADDING-BOTTOM: 5px; PADDING-TOP: 8px">
					<img src="./images/select.gif" height="16" width="16" title="hello" class="mxToolbarItem" onclick="init('svgmap');">
					<img src="./images/start.gif" height="16" width="16" title="hello" class="mxToolbarItem">
					<img src="./images/end.gif" height="16" width="16" title="hello" class="mxToolbarItem">
					<img src="./images/actor.gif" height="16" width="16" title="hello" class="mxToolbarItem">
					<img src="./images/auto.gif" height="16" width="16" title="hello" class="mxToolbarItem">
					<img src="./images/and_split.gif" height="16" width="16" title="hello" class="mxToolbarItem">
					<img src="./images/and_join.gif" height="16" width="16" title="hello" class="mxToolbarItem">
					<img src="./images/or_split.gif" height="16" width="16" title="hello" class="mxToolbarItem">
					<img src="./images/or_join.gif" height="16" width="16" title="hello" class="mxToolbarItem">
					<hr size="1" style="MARGIN-RIGHT: 6px">
					<img src="./images/pan.gif" height="16" width="16" title="hello" class="mxToolbarItem">
					<img src="./images/zoomin.gif" height="16" width="16" title="放大" class="mxToolbarItem">
					<img src="./images/zoomout.gif" height="16" width="16" title="缩小" class="mxToolbarItem">
				</div>
			</div>
		</td>
	</tr>
</table>
</div>
<div id="graph" class="base" style="LEFT: 50px; width:expression(document.body.clientWidth-60); TOP: 32px; height:expression(document.body.clientHeight-36)">
  <embed  id="svgmap" pluginspage="http://www.adobe.com/svg/viewer/install/" src="default.svg"  style="width: 1024;height:768" type="image/svg+xml">
  </embed> 	
</div>	
<script>
<!--

//-->
</script>
</html:form>