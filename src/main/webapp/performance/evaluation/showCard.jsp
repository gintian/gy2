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
<script language='javascript'>

var IVersion=getBrowseVersion();

if(IVersion==8)
{
	document.writeln("<link href=\"/performance/evaluation/locked-column-new.css\" rel=\"stylesheet\" type=\"text/css\">");
}else
{
	document.writeln("<link href=\"../../css/locked-column-new.css\" rel=\"stylesheet\" type=\"text/css\">");
}
function change()
{
	var flag = "0";
	if(evaluationForm.scoreExplainFlag.checked)
	{
		flag="1";
	}
	evaluationForm.action="/performance/evaluation/performanceEvaluation.do?b_showCard=link&scoreExplainFlag="+flag;
	evaluationForm.submit();
	//strurl="/performance/evaluation/performanceEvaluation.do?b_showCard=link`object_id="+object_id+"`scoreExplainFlag="+flag;
	//iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
	//window.showModalDialog(iframe_url,arguments,"dialogWidth=800px;dialogHeight=600px;resizable=no;scroll=no;status=no;"); 
}
function showExplain(reasonsWhole)
{
	var infos=new Array();
	infos[0]=reasonsWhole;
	var strurl="/performance/evaluation/showExplain.jsp"
    var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl;
	//window.showModalDialog(iframe_url,infos,"dialogWidth=400px;dialogHeight=300px;resizable=no;scroll=no;status=no;");
	var height = 300;
    if (!window.showModalDialog){
        window.dialogArguments = infos;
        height=350;
    }
    var config = {
	    width:400,
	    height:height,
        dialogArguments:infos,
	    type:'1',
	    id:'showExplain_win'
	}

	modalDialog.showModalDialogs(iframe_url,"showExplain_win",config,"");
}

function showCard_close(){
	if(!window.showModalDialog){
		var win = parent.parent.Ext.getCmp('showCard_win');
   		if(win) {
    		win.close();
   		}
	}
	parent.window.close();
}

</script>
</head>

<style>
	body {TEXT-ALIGN: center;}
	div#tbl-container {	
	width:780;
	height:500;
	overflow:auto;
	BORDER-BOTTOM:#94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid; 
}
</style>
  <hrms:themes />
	<body>
		<html:form action="/performance/evaluation/performanceEvaluation">
			<table align="center">
				<tr><td>
					<bean:message key="lable.appraisemutual.examineobject"/>
					<hrms:optioncollection name="evaluationForm" property="object_list" collection="list" />
					<html:select name="evaluationForm" property="cardObject_id" size="1" style="width:152px;" onchange="change();">
						<html:options collection="list" property="dataValue" labelProperty="dataName"/>
					</html:select>
				<logic:equal name="evaluationForm" property="scoreExplain" value="1">
						&nbsp;&nbsp;
						<html:checkbox styleId="scoreExplainFlag" name="evaluationForm" property="scoreExplainFlag" value="1" onclick="change();" /> <bean:message key="jx.scoreExplain"/>
				</logic:equal>
				<logic:notEqual name="evaluationForm" property="scoreExplain" value="1">
						<html:checkbox styleId="scoreExplainFlag" name="evaluationForm" property="scoreExplainFlag" value="1" onclick="change();" style="display:none;"/> 
				</logic:notEqual>
				</td></tr>	
				
				<tr><td>
				 <div id='tbl-container'   >
				    ${evaluationForm.cardHtml}
				 </div>
				 </td></tr>
			</table>
		<table  width="50%" align="center">
          <tr>
            <td align="center">
            	<input type="button" class="mybutton" name="button" value="<bean:message key="button.close"/>" onclick="showCard_close();"	/>
            </td>
          </tr>          
		</table>
	   </html:form>
  </body>
</html>
