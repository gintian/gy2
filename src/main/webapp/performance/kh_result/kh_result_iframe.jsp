<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.SystemConfig,
				 com.hjsj.hrms.actionform.performance.kh_result.KhResultForm,
				 com.hrms.struts.constant.WebConstant,
				 com.hrms.struts.valueobject.UserView,
				 com.hjsj.hrms.utils.PubFunc" %>
<%
		UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    	KhResultForm khResultForm=(KhResultForm)session.getAttribute("khResultForm");
    	String firstLink=khResultForm.getFirstLink();
    	String drawId=khResultForm.getDrawId();
    	String  mdobj=khResultForm.getObject_id();

%>  

<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<SCRIPT LANGUAGE=javascript src="/performance/objectiveManage/objectiveCard/objectiveCard.js"></SCRIPT>
<style>
<!--
.RecordRow_Result 
{
	border: inset 1px #94B6E6;
	BORDER-BOTTOM: #94B6E6 0pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 0pt solid;
	font-size: 12px;
	border-collapse:collapse; 
	height:22;
}
td{
    overflow: visible !important;
}
-->
</style>
<script type="text/javascript">
var aclientHeight=document.body.clientHeight;
var IVersion=getBrowseVersion();
var firstLinkJS="<%=firstLink%>";
<%if(drawId.equals("2")){%>
     firstLinkJS+="&screenWidth="+window.screen.width;
<%}%>
<!--
function isParagraph()
{
    var drawId = "";
    var obj = document.getElementsByName("drawId")[0];
    for(var i=0;i<obj.options.length;i++)
    {
    	if(obj.options[i].selected)
        {
             drawId = obj.options[i].value;
             break;
        }
    }
    var hashvo=new ParameterSet(); 
	hashvo.setValue("drawId",drawId);
   	var request=new Request({asynchronous:false,onSuccess:displayOrHide,functionId:'90100130032'},hashvo);
     
}
function changeDraw(obj)
{
    var drawId = "";
    for(var i=0;i<obj.options.length;i++)
    {
    	if(obj.options[i].selected)
        {
             drawId = obj.options[i].value;
             break;
        }
    }
    var hashvo=new ParameterSet(); 
	hashvo.setValue("drawId",drawId);
   	var request=new Request({asynchronous:false,onSuccess:displayOrHide,functionId:'90100130032'},hashvo);
     
}
function displayOrHide(outparameters)
{

	var isChart = outparameters.getValue("isChart");//判断是否是图形 如果是图形才显示右边的图形类别下拉框
	var drawId = outparameters.getValue("drawId");
	if(isChart==1)
	{
		if(document.getElementById("graphType")!=null)
			document.getElementById("graphType").style.display="";
	}else if(isChart==0)
	{
		if(document.getElementById("graphType")!=null)
			document.getElementById("graphType").style.display="none";
	}
	//添加绩效、能力素质区分  chent 20151210 start
	var busitype="${khResultForm.busitype}";//0:绩效 1:能力素质
	if(busitype == "1"){
		// 岗位分析
		drawId="9";
		// 能力素质不显示下拉框
		document.getElementsByName("drawId")[0].style.display="none";
		document.getElementById("graphType").style.display="none";
	}
	//添加绩效、能力素质区分  chent 20151210 end
	if(drawId=="0")
     {
          window.main.location="/performance/kh_result/kh_result_tables.do?b_init=link";
        
     }else if(drawId=="2")
     {
       window.main.location="/performance/kh_result/kh_result_reviews.do?b_init=init&screenWidth="+100;
     }
     else if(drawId=="3")
     {
         window.main.location="/performance/kh_result/kh_result_overallrating.do?b_init=link&opt=1";
     }
     else if(drawId == "1")
     {
          window.main.location="/performance/kh_result/kh_result_figures.do?b_init=link&opt=2&typechart=1";
     }
     else if(drawId =="4")
     {
        
          var object_id="${khResultForm.object_id}";
          window.main.location="/performance/showkhresult/showDirectionAnalyse.do?b_init=link&objectid="+object_id+"&operate=0";
     }
     else if(drawId =="5")
     {
     	
           window.main.location="/performance/kh_result/kh_result_figures.do?b_objective=link&from_flag=0";
     }
     else if(drawId == "6")
     {
           window.main.location="/performance/kh_result/kh_result_interview.do?b_interview=link&object_id=${khResultForm.object_id}&plan_id=${khResultForm.planid}";
         
     }
     else if(drawId == "7")
     {
     		window.main.location="/performance/kh_result/evaluateBlind.do?b_query=link&object_id=${khResultForm.object_id}&plan_id=${khResultForm.planid}";
     }
     else if(drawId == "9") // 能力素质、岗位分析
     {
     		window.main.location="/performance/perAnalyse.do?b_personStation=query&opt=1&returnflag=9&busitype=1&a0100=${khResultForm.object_id}&planIds=${khResultForm.planid}&encode=true&isfromKhResult=1";
     }
     else
     {
        window.main.location="/performance/kh_result/kh_result_muster.do?b_init=link&tabid="+drawId;
     }
}
function ret(code,model,distinctionFlag,nbase)
{
	if(model=='3')
	{
   		khResultForm.action="/performance/kh_result/org_kh_plan.do?b_init=init&distinctionFlag=0&model=3";
   		khResultForm.submit();
	}
	else
	{
   		khResultForm.action="/performance/kh_result/kh_plan_list.do?b_init=init&opt=1&a0100="+code+"&model="+model+"&distinctionFlag="+distinctionFlag;
   		khResultForm.submit();
   	}  
}
function changeGraphType()
{
	var graphType=khResultForm.graphType.value;
	//khResultForm.graphType.value=graphType;
	//alert("${khResultForm.graphType}");
	if(graphType==1)
	{
		window.main.location="/performance/kh_result/kh_result_figures.do?b_init=link&opt=2&graphType="+graphType+"&chartParameters=null";
	}
	else if(graphType==2)
	{
		window.main.location="/performance/kh_result/kh_result_figures.do?b_init=link&opt=2&graphType="+graphType+"&chartParameters=null";
	}
	else if(graphType==3)
	{                                                    
		window.main.location="/performance/kh_result/kh_result_figures.do?b_init=link&opt=2&graphType="+graphType+"&chartParameters=null";
	}
}
//-->
</script>
<script type="text/javascript">
<!--
if(IVersion==8){
  	document.writeln("<link href=\"/performance/objectiveManage/objectiveCard/objectiveCard_8.css\" rel=\"stylesheet\" type=\"text/css\">");
}else{
  	document.writeln("<link href=\"/performance/objectiveManage/objectiveCard/objectiveCard.css\" rel=\"stylesheet\" type=\"text/css\">");
}
//-->
</script>
<hrms:themes />
<html:form action="/performance/kh_result/kh_result_figures">
<table align="center" border="0" width="90%" cellpmoding="0" cellspacing="0" cellpadding="0">
<tr>
<td align="left" nowrap>
<html:hidden name="khResultForm" property="object_id"/>
<table align="left" border="0" width="30%" cellpmoding="0" cellspacing="0" cellpadding="0" style="margin-bottom: 2px">
<tr>
<td style="height:20px" nowrap>
	<html:select styleId="drawId" name="khResultForm" property="drawId" size="1" onchange="changeDraw(this);">
		<html:optionsCollection property="drawList" value="dataValue" label="dataName"/>
	</html:select>
	<logic:notEmpty name="khResultForm" property="drawList">
		<hrms:optioncollection name="khResultForm" property="graphTypeList" collection="list" />
		<html:select name="khResultForm" property="graphType" size="1" styleId="graphType" onchange="changeGraphType();">
		<html:options collection="list" property="dataValue" labelProperty="dataName"/>
		</html:select>
  	</logic:notEmpty>
	<input type="button" name="ll" value="<bean:message key="button.return"/>" onclick='ret("<%=mdobj%>","${khResultForm.model}","${khResultForm.distinctionFlag}","${khResultForm.nbase}");' class="mybutton"/>
</td>
</tr>
</table>
</td>
</tr>
<tr>
<td class="RecordRow">
<logic:notEmpty name="khResultForm" property="drawList">
	<script language='javascript' >
		document.write("<iframe src=\""+firstLinkJS+"\" width=\"100%\" height=\""+(aclientHeight-70)+"\" scrolling=\"auto\" frameborder=\"0\" name=\"main\"></iframe>");
		isParagraph();
   </script>
  </logic:notEmpty>
</td>
</tr>
</table>
</html:form>