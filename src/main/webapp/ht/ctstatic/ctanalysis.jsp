<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.taglib.CommonData"%>

<%@ page import="com.hjsj.hrms.actionform.ht.ctstatic.StAnalysisForm"%>
<style type="text/css"> 
body {margin-top: 10px; margin-left: 10px;}
fieldset {padding: 5px;}
#scroll_box {
    height: 280px;    
    width: 260px;            
    overflow: auto;            
    /*BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid;*/ 
    BORDER-TOP: #94B6E6 1pt solid ;
}
#scroll_dbname {
    border: 1px solid #eee;
    height: 50px;    
    width: 250px;            
    overflow: auto;            
    margin: 1em 1;
}
</style>
<script type="text/javascript" src="/js/hjsjUrlEncode.js"></script>
<script type="text/javascript">
	function testchart(e) {
		var name = e.name;
		var yValue = e.value;
		if (yValue == "" || yValue == 0)
			return;
		if(e.data.id && e.data.id.length>0)
			name = e.data.id;
		if(name != "") {
	      	//name = getEncodeStr(name);
	      	var thecodeurl = "/ht/ctstatic/ht_static_detail.do?b_query=link&dbname=${stAnalysisForm.dbname}";
			thecodeurl+="&a_code=${stAnalysisForm.orgcode}&itemid=${stAnalysisForm.itemid}&mark=tu&itemvalue="+$URL.encode(name);
			stAnalysisForm.action = thecodeurl;
			stAnalysisForm.submit();
   		}
	}
</script>
<html:form action="/ht/ctstatic/ctanalysis">
<%
StAnalysisForm stAnalysisForm = (StAnalysisForm)session.getAttribute("stAnalysisForm");
 %>
 <html:hidden name="stAnalysisForm" property="dbname"/>
<table border="0" width="96%" align="center">
<tr><td align="center">
<fieldset align="center" style="width:100%;">
<legend>统计分析</legend> 
<table border="0" width="100%">
	<tr>
		<td width="30%" valign="top">
			<table border="0" width="100%">
				<tr>
					<td>
						<fieldset align="center" style="width:97%;heigth:100%">
						<legend>人员库</legend> 
						<table border="0" width="100%">
						<tr><td >
						<logic:iterate id="element" name="stAnalysisForm" property="dblist" indexId="index">
						<%
							CommonData item=(CommonData)pageContext.getAttribute("element");
            				String id=item.getDataValue();
            				String desc=item.getDataName();
            				String dbname = stAnalysisForm.getDbname();
            				if(dbname.indexOf(id)!=-1){
						%>
						<input type="checkbox" name="<%=id %>" value="<%=id %>" onclick="searchProject('0');" checked><%=desc%>
						<%}else{%>
							<input type="checkbox" name="<%=id %>" value="<%=id %>" onclick="searchProject('0');"><%=desc%>
						<%}%>
						</logic:iterate>
						</td></tr>
						</table>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td>
					<fieldset align="center" style="width:97%;">
					<legend>统计项目</legend> 
					<table border="0" width="100%">
						<tr>
							<td>子集
							<html:select name="stAnalysisForm" property="setid" onchange="searchProject('1');" style="width:150">
    							<html:optionsCollection property="setlist" value="dataValue" label="dataName" />
 							</html:select>
							</td>
						</tr>
						<tr>
							<td>指标
							<html:select name="stAnalysisForm" property="itemid" onchange="searchProject('0');" style="width:150">
    							<html:optionsCollection property="itemlist" value="dataValue" label="dataName" />
 							</html:select>
							</td>
						</tr>
					</table>
					</fieldset>
					</td>
				</tr>
				<tr>
					<td>
					<fieldset align="center" style="width:97%;">
					<legend>统计结果</legend>
					<div id="scroll_box" style="width: 100%;border: 1px solid;" >
						${stAnalysisForm.tablestr}
					</div>
					</fieldset>
					</td>
				</tr>
			</table>
		</td>
		<td>
			<fieldset align="center" style="width:97%;height:100%">
			<legend>统计分析图</legend> 
			<table border="0" width="100%" height="100%">
			<tr><td>
			<hrms:chart name="stAnalysisForm" title="${stAnalysisForm.charname}"
					scope="session" legends="valuelist" data="" width="800"
					height="400" chart_type="11" pointClick="testchart">
			</hrms:chart>
			</td></tr></table>
			</fieldset>
		</td>
	</tr>
</table>
</fieldset>
</td></tr>
</table>
<logic:equal value="dxt" name="stAnalysisForm" property="returnvalue">
  <div style="position:relative; width:50px; margin-top:550px!important; margin-top:5px;left:50%;margin-left:-0px; ">
    <html:button styleClass="mybutton" property="bc_btn1" onclick="hrbreturn('contract','il_body','stAnalysisForm');">
               返回
    </html:button>
  </div>
</logic:equal>
</html:form>

<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="javascript">
function  searchProject(reset){
	setCheckBoxValue();
	stAnalysisForm.action="/ht/ctstatic/ctanalysis.do?b_query=link&a_code=${stAnalysisForm.orgcode}&reset="+reset;
	stAnalysisForm.submit();
}
function setCheckBoxValue(){
	var dbnameVlaue = "";
	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++){
		if(tablevos[i].type=="checkbox"){
			if(tablevos[i].checked){
				dbnameVlaue+=tablevos[i].value+",";
			}
      	 }
   	}
   	document.getElementById("dbname").value=dbnameVlaue;
}
function viewItemvalue(itemvalue){
    itemvalue = getEncodeStr(itemvalue);
    var thecodeurl="/ht/ctstatic/ht_static_detail.do?b_query=link&dbname=${stAnalysisForm.dbname}";
    
	thecodeurl+="&a_code=${stAnalysisForm.orgcode}&itemid=${stAnalysisForm.itemid}&itemvalue="+itemvalue;
    stAnalysisForm.action = thecodeurl;
	stAnalysisForm.submit();
}

</script>
