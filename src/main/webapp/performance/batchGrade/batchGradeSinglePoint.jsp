<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="java.util.*,com.hjsj.hrms.actionform.performance.batchGrade.BatchGrade_SinglePoint_Form,
				 com.hrms.struts.taglib.CommonData,
				com.hrms.struts.constant.SystemConfig" %>
<%
	BatchGrade_SinglePoint_Form batchGrade_SinglePoint_Form=(BatchGrade_SinglePoint_Form)session.getAttribute("batchGrade_SinglePoint_Form");
	int totalNumber=Integer.parseInt(batchGrade_SinglePoint_Form.getTotalNumber());
	int point_index=Integer.parseInt(batchGrade_SinglePoint_Form.getPoint_index());
	String isAllSub=batchGrade_SinglePoint_Form.getIsAllSub();
 %>
				
<html>
  <head>
  <META HTTP-EQUIV="pragma" CONTENT="no-cache"> 
  <META HTTP-EQUIV="Cache-Control" CONTENT="no-cache, must-revalidate"> 
  <META HTTP-EQUIV="expires" CONTENT="Wed, 26 Feb 1997 08:21:57 GMT">
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  
<style>

div#tbl-container 
{
	width:${batchGrade_SinglePoint_Form.tableWidth};
	height: 94%;	
	overflow: auto;
	BORDER-BOTTOM:#94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid; 
}

.RecordRow 
{
	border: inset 1px #94B6E6;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	BACKGROUND-COLOR: #FFFFFF; 
	font-size: 12px;
	border-collapse:collapse; 
	height:26;
}

.fontStyle_self
{
	font-size:14px;
	font-family: "宋体";
}
.fontStyle_self2
{
	font-size:16px;
	font-family: "宋体";
	COLOR : #103B82;
}

</style>
<hrms:themes />
<script type="text/javascript">

var IVersion=getBrowseVersion();
if(IVersion==8)
{
  	document.writeln("<link href=\"/performance/batchGrade/batchGradeCard_8.css\" rel=\"stylesheet\" type=\"text/css\">");
}else
{
  	document.writeln("<link href=\"/performance/batchGrade/batchGradeCard.css\" rel=\"stylesheet\" type=\"text/css\">");
}

</script>

<script language="JavaScript" src="/js/pergrade.js"></script>

<script language='javascript' >
var point_id='${batchGrade_SinglePoint_Form.point_id}'
var objects_str='${batchGrade_SinglePoint_Form.objects_str}/'
var totalNumber='<%=totalNumber%>'
var point_index='<%=point_index%>'
var plan_id='${batchGrade_SinglePoint_Form.plan_id}';


//赋分原因
function scoreReason(plan_id,object_id,userID,point_id,opt)
{
	var strurl="/selfservice/performance/singleGrade.do?b_initScoreCause=query`type=0`plan_id="+plan_id+"`opt="+opt+"`objectid="+object_id+"`userID="+userID+"`point_id="+point_id;
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
	var reject_cause=window.showModalDialog(iframe_url,arguments,"dialogWidth=460px;dialogHeight=350px;resizable=yes;scroll=no;status=no;");  

	if(reject_cause!=null && reject_cause!='undefined') // 评分说明可以制为空 并且也刷新 JinChunhai 2011.12.08
	{
		reject_cause=replaceAll(reject_cause,"<br>","\r\n");
		reject_cause=replaceAll(reject_cause," ","&nbsp;");
		document.getElementById('r_'+point_id+"_"+object_id).innerHTML=reject_cause;
	}
}

function replaceAll( str, from, to ) 
{
	var idx = str.indexOf( from );
	while ( idx > -1 ) 
	{
		str = str.replace( from, to ); 
	    idx = str.indexOf( from );
	}	   
	return str;
}

</script>
<script language="JavaScript" src="/performance/batchGrade/batchGradeSinglePoint.js"></script>
  </head>
  <body>
   <html:form action="/performance/batchGradeSinglePoint">
   <div id="tbl-container"  style='position:absolute;left:20;top:10'  >
   	${batchGrade_SinglePoint_Form.batchGradeHtml}
   </div>
   <script language='javascript'>
		document.write("<div id='ff' style='position:absolute;left:20;top:"+(document.body.clientHeight*0.9)+"'  >");
   </script>
   	<% 
   		 if(!isAllSub.equals("1"))
   		 {
	   		 if(point_index!=0){
	   			out.print("<input type='button' onclick='go_up()'  class='mybutton' value='上一题' />&nbsp;");
	   		 }
	   		 
	   		 if(point_index==totalNumber-1)
	   		 {
	   			 out.print("<input type='button' id='save' class='mybutton' onclick='save_next(1,this)'  value='保 存' />&nbsp;");
	   		 	 out.print("<input type='button' id='sub' class='mybutton' onclick='save_next(2,this)'  value='提 交' />");
	   		 }
	   		 else
	   		 {
	   		 	out.print("<input type='button' id='next'  class='mybutton' onclick='save_next(1,this)'  value='下一题' />&nbsp;");
	   		 	out.print("<input type='button' id='save'  class='mybutton' onclick='save_next(3,this)'  value='保存' />&nbsp;");
	   		 }
	   	}
	   	else
	   	{
	   		if(point_index!=0){
	   			out.print("<input type='button' onclick='updown(1)'  class='mybutton' value='上一题' />&nbsp;");
	   		}  		
	   	    if(point_index!=totalNumber-1)
	   		 		out.print("<input type='button' class='mybutton' onclick='updown(2)'  value='下一题' />&nbsp;");
	   		
	   	}
   	 %>
		<Input type='hidden' name='point_index'  value='<%=point_index%>' />
	</div>
   <div id="date_panel">
   			
   </div>
   </html:form>
  </body>
</html>
