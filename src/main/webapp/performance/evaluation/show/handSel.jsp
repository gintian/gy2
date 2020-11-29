<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<hrms:themes />
<link rel="stylesheet" type="text/css" href="../../ajax/skin.css"></link>
<link href="../../css/xtree.css" rel="stylesheet" type="text/css">
<SCRIPT LANGUAGE=javascript src="../../js/xtree.js"></SCRIPT>
<SCRIPT LANGUAGE=javascript src="../../js/validate.js"></SCRIPT>
<STYLE type=text/css>
.div2
{
 overflow:auto; 
 width: 200px;height: 230px;
 line-height:15px; 
 border-width:1px; 
 border-style: groove;
 border-width :thin ;
 
  border: inset 1px #C4D8EE;
 BORDER-BOTTOM: #C4D8EE 1pt solid; 
 BORDER-LEFT: #C4D8EE 1pt solid; 
 BORDER-RIGHT: #C4D8EE 1pt solid; 
 BORDER-TOP: #C4D8EE 1pt solid;
 
 scrollbar-base-color:#C3D3FD; 
 scrollbar-face-color:none;
 scrollbar-arrow-color:none;
 scrollbar-track-color:#ffffff;
 scrollbar-3dlight-color:#ffffff;
 scrollbar-darkshadow-color:#ffffff;
 scrollbar-highlight-color:#e5c8e5;
 scrollbar-shadow-color:#e5c8e5"
 SCROLLBAR-DARKSHADOW-COLOR: #ffffff;
 BORDER-BOTTOM: #ffccff 1px dotted;
}
</STYLE>
<script language="javascript">
var pointSel;
var pointDisp;
var type;
function getSelPoint(code,name,isLeafNode)
{
	pointSel = code;
	pointDisp = name;
	type=isLeafNode;
}
function addObj()
{
  if(type!='1')
  {
  	alert('请选择考核对象结点！');
  	return;
  }
  var right_vo= document.getElementById('right_fields');  
  for(i=0;i<right_vo.options.length;i++)
  {
  	if(right_vo.options[i].value==pointSel)
  	{
  		alert('已经选择该考核对象!');
  		return;
  	}    
  }

   var no = new Option();
   no.value=pointSel;
   no.text=pointDisp;
   right_vo.options[right_vo.options.length]=no;
}
function sub()
{
  var str = '';
  var right_vo= document.getElementById('right_fields');  
  for(i=0;i<right_vo.options.length;i++)
  	str+=',\''+right_vo.options[i].value+'\'';
  window.returnValue=str.substring(1);
  window.close();
}
</script>
<html:form action="/performance/evaluation/performanceEvaluation">
	<br>
	<table width="96%" border="0" cellspacing="0" align="center"
		cellpadding="0" valign="center" height="90%" class="ListTableF">
		<tr>
			<td align="center" class="TableRow" nowrap colspan="3">
				<bean:message key="jx.eval.handSel" />
				&nbsp;&nbsp;
			</td>
		</tr>
		<tr>
			<td align="center" class="RecordRow" nowrap colspan="3">
				<table width="100%" align="center" border="0">
					<tr>
						<td align="center" width="38%">
							<table width="100%" align="center">
								<tr>
									<td width="100%" align="left">
										<bean:message key="jx.eval.khobj1" />
										&nbsp;&nbsp;
									</td>
								</tr>
								<tr>
									<td align="left">
										<div id="treemenu"
											style="overflow:auto;width:190px;height:243px;" class="div2"></div>
									</td>
								</tr>
							</table>
						</td>

						<td width="16%" align="center">
							<html:button styleClass="mybutton" property="b_addfield"
								onclick="addObj('right_fields');">
								<bean:message key="button.setfield.addfield" />
							</html:button>
							<br>
							<br>
							<html:button styleClass="mybutton" property="b_delfield"
								onclick="removeitem('right_fields');">
								<bean:message key="button.setfield.delfield" />
							</html:button>
						</td>


						<td width="42%" align="center">
							<table width="100%">
								<tr>
									<td width="100%" align="left">
										<bean:message key="jx.eval.khobj2" />
										&nbsp;&nbsp;
									</td>
								</tr>
								<tr>
									<td width="100%" align="left">										
											<select name="right_fields" id="right_fields" multiple="true"
												size="15" ondblclick="removeitem('right_fields');" 
												style="MARGIN:-1px;height:249px;width:191.999px;font-size:9pt" />										
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td align="center" class="RecordRow" nowrap colspan="3" style="height:35px">
				<input type='button' value='<bean:message key="kq.formula.true"/>'
					onclick='sub()' class="mybutton" />
				&nbsp;&nbsp;
				<input type="button" value="<bean:message key="button.cancel"/>"
					onclick="window.close();" class="mybutton">
			</td>
		</tr>
	</table>
</html:form>
<script language="javascript">  
  var m_sXMLFile = "/performance/evaluation/show/handSelTree.jsp?planID=${param.planid}";		
  var root=new xtreeItem("root","组织结构","javascript:void(0)","_self","组织结构","/images/unit.gif",m_sXMLFile);
  Global.showroot=false;	
  root.setup(document.getElementById("treemenu"));  
</script>
