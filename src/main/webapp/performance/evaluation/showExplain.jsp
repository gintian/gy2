<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<hrms:themes />
				 				 
<html>
<head>

<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>

</head>

<style>
	body {TEXT-ALIGN: center;}
	div#tbl-container {	
	width:320;
	height:220;
	overflow:auto;
	BORDER-BOTTOM:#94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid; 
}
</style>
<script language="javascript">
function sh_close(){
	if(!window.showModalDialog){
		var win = parent.parent.Ext.getCmp('showExplain_win');
   		if(win) {
    		win.close();
   		}
	}
	parent.window.close();
}
</script>
	<body>
		<html:form action="/performance/evaluation/performanceEvaluation">
			<table align="center">
				<tr><td>
					<bean:message key='lable.performance.DeductMark'/>
				</td></tr>	
				
				<tr><td>
				 <div id='tbl-container'  class="common_border_color" >
				 	<script language="javascript">
				 		var info = window.dialogArguments || parent.parent.dialogArguments;
				 		document.writeln(info[0]);
				 	</script>
				 </div>
				 </td></tr>
			</table>
		<table  width="50%" align="center">
          <tr>
            <td align="center">
            	<input type="button" class="mybutton" name="button" value="<bean:message key="button.close"/>" onclick="sh_close();"	/>
            </td>
          </tr>          
		</table>
	   </html:form>
  </body>
</html>
