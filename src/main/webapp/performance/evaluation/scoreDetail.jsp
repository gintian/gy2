<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.utils.ResourceFactory"%>
<hrms:themes />
				 				 
<html>
<head>

<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language='javascript'>
var selectflag="${evaluationForm.showWays}";
function selectChecked()
{
	if(selectflag==0)
		document.getElementById("score").checked=true;
	if(selectflag==1)
		document.getElementById("percent").checked=true;
	if(selectflag==2)
		document.getElementById("biaodu").checked=true;
}
var IVersion=getBrowseVersion();

if(IVersion==8)
{
	document.writeln("<link href=\"/performance/evaluation/locked-column-new.css\" rel=\"stylesheet\" type=\"text/css\">");
}else
{
	document.writeln("<link href=\"../../css/locked-column-new.css\" rel=\"stylesheet\" type=\"text/css\">");
}
function sub(o)
{
	//用于切换 分数 百分制 标度的
	var recheckObjectid="${evaluationForm.recheckObjectid}";
	evaluationForm.action="/performance/evaluation/performanceEvaluation.do?b_scoreDetail=link&recheckObjectid="+recheckObjectid+"&showWays="+o;
	evaluationForm.submit();
}
function exportExcel()
{
	//导出EXCEL
	var hashvo=new ParameterSet();
	var plan_id="${evaluationForm.planid}";
	var method="${evaluationForm.method}";
	var template_id="${evaluationForm.templateid}";
	var recheckObjectid="${evaluationForm.recheckObjectid}";
	var showWays="${evaluationForm.showWays}";
	var plan_name="${evaluationForm.plan_name}";
	var objectName="${evaluationForm.objectName}";
	var object_type="${evaluationForm.object_type}";
	hashvo.setValue("plan_id",getEncodeStr(plan_id));
	hashvo.setValue("method",getEncodeStr(method));
	hashvo.setValue("template_id",getEncodeStr(template_id));
	hashvo.setValue("recheckObjectid",getEncodeStr(recheckObjectid));
	hashvo.setValue("showWays",getEncodeStr(showWays));
	hashvo.setValue("plan_name",getEncodeStr(plan_name));
	hashvo.setValue("objectName",getEncodeStr(objectName));
	hashvo.setValue("object_type",getEncodeStr(object_type));
	var request=new Request({method:'post',asynchronous:false,onSuccess:outIsOk,functionId:'9024000295'},hashvo);
	
}
function outIsOk(outparamters)
{

	var filename=outparamters.getValue("filename");
	window.location.target="_blank";
	window.location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid="+filename;

}	
</script>
</head>

<style>
	body {TEXT-ALIGN: center;}
	div#tbl-container {	
	width:expression(document.body.offsetWidth-50 + "px");
	height:500;
	overflow:auto;
	BORDER-BOTTOM:#94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid; 
}
</style>
<%
	String prompt=ResourceFactory.getProperty("jx.show.scoreDetail");
	String score=ResourceFactory.getProperty("kh.field.score");
	String percent=ResourceFactory.getProperty("kh.field.percent");
	String biaodu=ResourceFactory.getProperty("kh.field.bd");
%>  
<body>
	<html:form action="/performance/evaluation/performanceEvaluation">

	 <table align="center" width="100%">
	 	<tr>
	  		<td align="left">
	  			<Input type="button" class="mybutton" value="<bean:message key='button.export'/>" onclick="exportExcel();"  />
	  		</td>
	  		<td align="center" nowrap>
	  			<span style="font-size:18;color:blue;">${evaluationForm.plan_name}：${evaluationForm.objectName}--<%=prompt %></span>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	  			<bean:message key='jx.khplan.param2.title15'/>:
	  			<Input type='radio' name='showWays' id="score" value='0' onclick="sub(0)"  /><%=score %>&nbsp;&nbsp; 
	  			<Input type='radio' name='showWays' id="percent"  value='1' onclick="sub(1)"  /><%=percent %>&nbsp;&nbsp; 
	  			<Input type='radio' name='showWays' id="biaodu" value='2' onclick="sub(2)"  /><%=biaodu %>&nbsp;&nbsp;&nbsp;&nbsp;  
	  		</td>
	  		<td align="right">
	  		</td>
	 	</tr>
	 </table>
	 <table align="center">
	    <tr>
	 		<td>
				 <div id="tbl-container"  class='common_border_color'>
				    ${evaluationForm.tableHtml}
				 </div>
				 <script type="text/javascript">
				 	// lium: 给表头td添加公共样式: common_background_color common_border_color
				 	var c = document.getElementById("tbl-container");
					var tbody = c.getElementsByTagName("tbody")[0];
				 	if (c) {
				 		var a = c.getElementsByTagName("thead")[0].getElementsByTagName("td") || [];
				 		for (var i = 0; i < a.length; i++) {
				 			a[i].className += " common_background_color common_border_color";
				 		}
				 		// 表体td边框颜色
						if(tbody){
							var b = tbody.getElementsByTagName("td") || [];
							for (var i = 0; i < b.length; i++) {
								b[i].className += " common_border_color";
								b[i].style.borderLeftWidth = "0px";
							}
						}

				 	}
				 	// 去掉边框重合的td的borderStyle
				 	var g = document.getElementById("g");
				 	if (g) {
				 		g.style.borderLeftStyle = "none";
				 		g.style.borderTopStyle = "none";
				 		g.style.borderBottomWidth = "1px";
				 	}
				 	if(tbody){
						var rows = tbody.rows;
						if (rows) {
							for (var i = 0; i < rows.length; i++) {
								var cells = rows[i].cells;
								if (cells) {
									for (var j = 0; j < cells.length; j++) {
										if (i === 0) {
											if (j !== cells.length - 1) {
												cells[j].style.borderTopStyle = "none";
											}
										}
										if (j === 0) {
											cells[j].style.borderLeftStyle = "none";
										}
									}
								}
							}
						}
					}
				 </script>
			</td>
	    </tr>
	 </table>
	 <table  width="50%" align="center">
          <tr>
            <td align="center">
            	<input type="button" class="mybutton" name="button" value="<bean:message key="button.close"/>" onclick="window.close();"	/>
            </td>
          </tr>          
	</table>
   </html:form>
  </body>
<script language="javascript">
selectChecked();
</script>
</html>
