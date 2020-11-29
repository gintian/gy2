<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="java.util.*,com.hjsj.hrms.actionform.performance.batchGrade.BatchGradeForm,
				 com.hrms.struts.taglib.CommonData,
				 org.apache.commons.beanutils.LazyDynaBean,com.hjsj.hrms.utils.PubFunc,
				com.hrms.struts.constant.SystemConfig" %>

<%
	String isEpmLoginFlag="0";	
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  isEpmLoginFlag=(String)userView.getHm().get("isEpmLoginFlag"); 
	  isEpmLoginFlag = (isEpmLoginFlag==null||isEpmLoginFlag.equals(""))?"0":isEpmLoginFlag;
	  hcmflag=userView.getBosflag();
	}

    String performanceType=SystemConfig.getPropertyValue("performanceType");
	String flag=(String)userView.getHm().get("gradeFashion");   //1:下拉框方式  2：平铺方式
	//request.getRequestDispatcher("/selfservice/performance/batchGrade.do?b_query=link" );
	String clientName="";
	String url_str="/templates/attestation/unicom/performance.do?b_query=link";
	if(SystemConfig.getPropertyValue("clientName")!=null)
	{
		clientName=SystemConfig.getPropertyValue("clientName").trim();
		if(clientName.equalsIgnoreCase("bjga"))
			url_str="/templates/attestation/unicom/performance.do?b_score=link";
	}
	if(flag==null)
	{
		flag="2";
		userView.getHm().put("gradeFashion","2");
	
	}
	BatchGradeForm batchGradeForm=(BatchGradeForm)session.getAttribute("batchGradeForm");
	
	Hashtable paramTable=(Hashtable)batchGradeForm.getParamTable();
	String  _KeepDecimal ="1" ; 
    if(paramTable.get("KeepDecimal")!=null&&((String)paramTable.get("KeepDecimal")).trim().length()>0)
    	_KeepDecimal=(String) paramTable.get("KeepDecimal"); // 小数位
	
	
	
	String radioEval="0";   //按单选按钮评分
	
	String pointEvalType="";
	String MutiScoreOnePageOnePoint="";
	String scoreflag="";	
	if(paramTable!=null && paramTable.size()>0)
	{
		if(paramTable.get("PointEvalType")!=null)
			pointEvalType=(String)paramTable.get("PointEvalType");
		if(paramTable.get("MutiScoreOnePageOnePoint")!=null)
	    	MutiScoreOnePageOnePoint=(String)paramTable.get("MutiScoreOnePageOnePoint");
    	scoreflag = (String) paramTable.get("scoreflag"); // =2混合，=1标度
	}
	
	if( pointEvalType.equals("1")&&MutiScoreOnePageOnePoint.equalsIgnoreCase("False")&&scoreflag.equals("1"))	
		radioEval="1";
	String plan_descript_content=(String)batchGradeForm.getPlan_descript_content();
	if(plan_descript_content==null)
		plan_descript_content="";
	else
		plan_descript_content=plan_descript_content.replaceAll("\r\n","<br>").replaceAll("\n","<br>");
	String returnflag=(String)batchGradeForm.getReturnflag();  //8:返回首页  10：返回首页更多列表
	if(returnflag==null)
		returnflag="";
	String isShowOrder=batchGradeForm.getIsShowOrder();
    String plan_descript=batchGradeForm.getPlan_descript();
    String linkType=batchGradeForm.getLinkType(); 
    if(linkType==null)
    	linkType="";
    String showSumRow=batchGradeForm.getShowSumRow();
    if(showSumRow==null)
    	showSumRow="False";
    String togetherCommit=batchGradeForm.getTogetherCommit(); ////多人打分统一提交, Ture, False, 默认为False
    if(togetherCommit==null)
		togetherCommit="False";    
    String score_sumtotal="false";
    if(showSumRow!=null&&showSumRow.equalsIgnoreCase("true"))
		score_sumtotal=showSumRow.toLowerCase();
	
	//是否自动保存 值为时间（0：不自动保村 1： 1分钟  2：2分钟）
	String grading_auto_saving="0";
	if(SystemConfig.getPropertyValue("grading_auto_saving")!=null&&SystemConfig.getPropertyValue("grading_auto_saving").length()>0)
		grading_auto_saving=SystemConfig.getPropertyValue("grading_auto_saving").trim();
	ArrayList dblist=(ArrayList)batchGradeForm.getDblist();
	
	int totalRowValue=0;
	if(SystemConfig.getPropertyValue("totalRowValue")!=null&&SystemConfig.getPropertyValue("totalRowValue").length()>0)
		totalRowValue=Integer.parseInt(SystemConfig.getPropertyValue("totalRowValue").trim());
	
	String isShowUnifyScore="1";
	StringBuffer str2=new StringBuffer(",");
	String dbpre=batchGradeForm.getDbpre()!=null?batchGradeForm.getDbpre():"";
	String planName="";
	boolean isFinished=false;
	if(dblist!=null&&dblist.size()>0)
	{
		for(int i=0;i<dblist.size();i++)
		{
		   			CommonData data=(CommonData)dblist.get(i);
		   			String desc=data.getDataName();
		   			if(data.getDataValue().equals(dbpre))
		   				planName=desc;
		   			if(desc.trim().length()==0)
		   				continue;
		   			str2.append(data.getDataValue()+",");	
		   			if(desc.indexOf("已完成)")==-1&&desc.indexOf("已评价")==-1)
		   				isShowUnifyScore="0";
		   			if(desc.indexOf("已完成)")!=-1)
		   				isFinished=true;
		   			
		}
		
	}
	else
		isShowUnifyScore="0";
	
	if(!isFinished&&isShowUnifyScore.equals("1"))
		isShowUnifyScore="0";
		
		
	String buttonClass="mybutton";
	if(SystemConfig.getPropertyValue("clientName")!=null&&SystemConfig.getPropertyValue("clientName").trim().equalsIgnoreCase("zglt")) 
	{
	     
	        buttonClass="mybuttonBig";
	}
	 
		
%>


<HTML>

<head>

<style>
/* 非超链接字体还原为黑色  bug 39771 haosl */
.cell_locked{
	color:#000000 !important;
}
div#tbl-container {

overflow: auto;
BORDER-BOTTOM:#94B6E6 1pt solid; 
BORDER-LEFT: #94B6E6 1pt solid; 
BORDER-RIGHT: #94B6E6 1pt solid; 
BORDER-TOP: #94B6E6 1pt solid; 
}
.RecordRow {
	BACKGROUND-COLOR: #FFFFFF;
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	font-size: 12px;
	border-collapse:collapse; 
	height:22;
}

.RecordRowLast {
	BACKGROUND-COLOR: #FFFFFF;
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 0pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	font-size: 12px;
	border-collapse:collapse; 
	height:22;
}
.mybuttonBig{
	border:1px solid #84ADC9;
	background-image:url(../../images/shu_bg_bg.gif);
	background-repeat:repeat-x;
	background-position:right;
	font-size:16px;
	line-height:18px;
	padding-left:1px;
	padding-right:1px;
	/*margin-left:1px;*/
	color:#36507E;
	font-weight: bold;	
	background-color: transparent;	
	cursor: hand ; 	
 }

.mybutton{
	border:1px solid #84ADC9;
	background-image:url(../../images/shu_bg_bg.gif);
	background-repeat:repeat-x;
	background-position:right;
	font-size:12px;
	line-height:18px;
	padding-left:1px;
	padding-right:1px;
	/*margin-left:1px;*/
	color:#36507E;
	background-color: transparent;	
	cursor: hand ; 	
	 
 }
 
 .cell_lockedLast {
  /*   background-image:url(/images/listtableheader-4.jpg); */
	background-repeat:repeat;
	background-position : center left;
	BACKGROUND-COLOR: #f4f7f7; 
	border: inset 1px #C4D8EE;
	COLOR : #103B82;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 0pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 0pt solid;
	
	left: expression(document.getElementById("tbl-container").scrollLeft); /*IE5+ only*/
	position: relative;
	z-index: 10;

}

<%  if(SystemConfig.getPropertyValue("clientName")!=null&&SystemConfig.getPropertyValue("clientName").trim().equalsIgnoreCase("zglt")) { %> 
 
.fontStyle_self{
	font-size:21px;
	font-family:楷体_GB2312;
}
.fontStyle_self2{
	font-size:25px;
	font-family:楷体_GB2312;
	font-weight: bold;	
}
.fontStyle_self3{
	font-size:21px;
	font-family:楷体_GB2312;
	font-weight: bold;	
}
<%  }else{  %> 
 /**
.fontStyle_self{
	font-size:14px;
	font-family:楷体_GB2312;
	
}
*/
<%  } %>

.table_class{
border: 1px solid  #5383E5; 
}

.table_td {
	
	BORDER-BOTTOM: #0066cc 1pt dotted; 
	BORDER-LEFT: #0066cc 0pt dotted; 
	BORDER-RIGHT: #0066cc 0pt dotted; 
	BORDER-TOP: #0066cc 0pt dotted;
}


</style>

<script type="text/javascript">
var isFjr=0; 
function myBrowser(){
	var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
	if (userAgent.indexOf("Firefox") > -1)
	{
		///判断是否Firefox浏览器
		document.writeln("<link href=\"/performance/evaluation/fire-locked-column-new.css\" rel=\"stylesheet\" type=\"text/css\">");
	} 
	else if (userAgent.indexOf("Safari") > -1)
	{
		///判断是否Safari浏览器
		document.writeln("<link href=\"/performance/evaluation/locked-column-new.css\" rel=\"stylesheet\" type=\"text/css\">");
	} 
	else
	{
		//如果是IE浏览器
		var IVersion=getBrowseVersion();
		if(IVersion==8||IVersion==0)
		{
			isFjr=1;
		  	document.writeln("<link href=\"/performance/evaluation/locked-column-new.css\" rel=\"stylesheet\" type=\"text/css\">");
		}else
		{
		  	document.writeln("<link href=\"../../css/locked-column-new.css\" rel=\"stylesheet\" type=\"text/css\">");
		}
	}
}
myBrowser();
</script>

<!--
<link href="../../css/locked-column-new.css" rel="stylesheet" type="text/css">  
-->

<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/pergrade.js"></script>
<script language="JavaScript" src="/performance/batchGrade/batchGrade.js"></script>
<script type="text/javascript" src="/js/wz_tooltip.js"></script>  

<script language="JavaScript">
var radioEval='<%=radioEval%>';
var evalOutLimitStdScore='${batchGradeForm.evalOutLimitStdScore}';
var DegreeShowType="${batchGradeForm.degreeShowType}";  //1-标准标度 2-指标标度 3显示指标度，采集标准标度
var fasion='<%=flag%>'
var scoreflag="${batchGradeForm.scoreflag}";		// 2混合，1标度
var pointContrl="${batchGradeForm.pointContrl}";
var performanceType="${batchGradeForm.performanceType}";	//考核形式   0：绩效考评  1：干部任免
var pointContrls=pointContrl.split("/");
var plan_id="${batchGradeForm.dbpre}";
var template_id="${batchGradeForm.template_id}";
var current="${batchGradeForm.current}";
var pointDeformity="${batchGradeForm.pointDeformity}";	
var noGradeItem="${batchGradeForm.noGradeItem}"
var fillCtr="${batchGradeForm.fillCtrs}";      //打分控制串
var isEntiretySub="${batchGradeForm.isEntiretySub}";  //提交是否需要必填
var isKnowWhole="${batchGradeForm.isEntiretySub}";    // 1;有了结程度 2:总评选项  3：两者都有
var mainbodyId='<%=(userView.getA0100())%>';
var score_sumtotal='<%=score_sumtotal%>';
var grading_auto_saving='<%=grading_auto_saving%>';	 
var totalRowValue=<%=totalRowValue%>;
var clientName='<%=clientName%>';
var _isShowOrder="${batchGradeForm.isShowOrder}";			//是否显示排名
var linkType="<%=linkType%>"
var _KeepDecimal="<%=_KeepDecimal%>";    //保留小数位
var objectid = "";
var wholeEvalMode="${batchGradeForm.wholeEvalMode}";//总体评价0：录入等级 1：录入分值
var topscore="${batchGradeForm.topscore}";//模板总分
var mustFillWholeEval = "${batchGradeForm.mustFillWholeEval}";

function returnInfo(outparamters)
{
	buttonDisabled(false); 
	var score_order=outparamters.getValue("score_order");
	var totalAppValue=getDecodeStr(outparamters.getValue("totalAppValue"));
	var isShowOrder="${batchGradeForm.isShowOrder}";			//是否显示排名
	var isShowTotalScore="${batchGradeForm.isShowTotalScore}";  //是否现实总分
	var isAutoCountTotalOrder="${batchGradeForm.isAutoCountTotalOrder}";
	var flag=outparamters.getValue("flag");
//	var BlankScoreOption=outparamters.getValue("BlankScoreOption"); // 指标未打分时，0 按未打分处理，1 计为最高分，默认值为按未打分处理
	 
	//if(flag==2)
	{
		var waitInfo=eval("wait");	
		waitInfo.style.display="none";
	}
	
	if((flag==2||flag==1||flag==8)&&score_sumtotal=='true'&&scoreflag=='2')
	{
			 writeSumScore();
	}
		
	/*
	if(BlankScoreOption=='1'&&flag!='3')
	{
		document.batchGradeForm.action=document.location;
		document.batchGradeForm.submit();
	}
	else */
	{
	 
		if(isShowOrder=="true"||isShowTotalScore=="true")
		{
			if(typeof(score_order)!="undefined"&&score_order!='')  
			{
				var score_order_arr=score_order.split('#');
				for(var i=0;i<score_order_arr.length;i++)
				{
					var values=score_order_arr[i].split('/');
				 	if(isShowOrder=="true")
				 	{
					 	var temp2=eval('pm'+i);
						temp2.innerHTML="<font  color='#2E67B9'   >"+values[1]+"</font>";
				 	}
				 	if(isShowTotalScore=="true")
				 	{
				 		var temp=eval('zf'+i);
				 		temp.innerHTML="<font  color='#2E67B9'   >"+values[0]+"</font>";
				 	}
				}
			}	
		}
		if(totalAppValue!=null && totalAppValue.length>0)
		{
			var wholeEvalValue = totalAppValue.split('#');
			for(var i=0;i<wholeEvalValue.length;i++)
			{
				var values = wholeEvalValue[i];
				var totalApp = eval('totalAppValue'+i);
				totalApp.innerHTML = values;
			}			
		}
		
		var info=getDecodeStr(outparamters.getValue("info"));
		info=replaceAll(info,'<br>','\n\r');
		 
		if(info==(SUBSUCCESS+'!')&&flag!=3)
		{
// 			buttons.style.display='none';
			clearTimeout(t);
		}
		
		if(flag==2)
		{
			alert(info);			
		}
		else if(flag==8)
		{
			alert(info);
		}
		else if(flag==1)
		{
		
			alert(info);
		}
		else
		{
			if(isAutoCountTotalOrder=='true')
				excecuteTable2();
			return;
		}
		
		var datavalue="";
		if(info==(SUBSUCCESS+'!'))
		{		
			var otherInfo=getDecodeStr(outparamters.getValue("otherInfo"));
			if(otherInfo.length>4)
			{
				var a_otherInfo=otherInfo.split("~");
				datavalue=a_otherInfo[0];
				otherInfo=FRIENDINFO+"："+a_otherInfo[1];
				otherInfo=replaceAll(otherInfo,'#','\n\r');
				alert(otherInfo);
			}
		}
		//保存成功也要去提交，为了改也签的状态  2013.11.20 pjf
		if(fasion=='2' && flag==1 && info==(SAVESUCCESS+'!')){
			if(datavalue!="")
		    	window.parent.document.forms[0].action='/selfservice/performance/batchGrade.do?b_tileFrame=link&operate=aaa'+datavalue;
		    else
				window.parent.document.forms[0].action='/selfservice/performance/batchGrade.do?b_tileFrame=link&operate=<%=(request.getParameter("operate"))%>';
			window.parent.document.forms[0].submit();
			window.location.href = window.location.href;
		}
		if(fasion=='2'&&((flag==2&&(info==(SUBSUCCESS+'!')||info==SUBSUCCESS_BJGA))||(flag==8&&info=='打分完成!')))
		{ 
		<%
		 	if(SystemConfig.getPropertyValue("clientName")!=null&&SystemConfig.getPropertyValue("clientName").trim().equalsIgnoreCase("zglt"))
		 	{
		%> 	
		 		if(flag==8&&info=='打分完成!')
		 		{
      				window.parent.opener.location="<%=url_str%>";
					window.parent.close();
		 		} 
		 		else
		 		{
			 		 if(datavalue!="")
				    	window.parent.document.forms[0].action='/selfservice/performance/batchGrade.do?b_tileFrame=link&operate=aaa'+datavalue;
				    else
						window.parent.document.forms[0].action='/selfservice/performance/batchGrade.do?b_tileFrame=link&operate=<%=(request.getParameter("operate"))%>';
					window.parent.document.forms[0].submit();
		 		
			    }
		 <%	
		 	}
		 	else if(linkType.equalsIgnoreCase("liantong"))
		 	{
		 %>
		 		    window.parent.opener.location="<%=url_str%>";
					window.parent.close();
		 <%	
		 	}
		 	else
		 	{			
		%>     
			    if(datavalue!="")
			    	window.parent.document.forms[0].action='/selfservice/performance/batchGrade.do?b_tileFrame=link&operate=aaa'+datavalue;
			    else
					window.parent.document.forms[0].action='/selfservice/performance/batchGrade.do?b_tileFrame=link&operate=<%=(request.getParameter("operate"))%>';
				window.parent.document.forms[0].submit();
				window.location.href = window.location.href;
		<%  } %>
		}
		else if(fasion=='1'&&((flag==2&&info==(SUBSUCCESS+'!'))||(flag==8&&info=='打分完成!')))
		{
		 
			 
			var index="";
			for(var i=0;i<document.batchGradeForm.dbpre.options.length;i++)
			{
				if(document.batchGradeForm.dbpre.options[i].selected==true)
				{	index=i;
					break;
				}
			}
			var left_index=document.batchGradeForm.dbpre.options[index].text.indexOf("(");
			

			if(flag==2)
			{
				document.batchGradeForm.dbpre.options[index].text=document.batchGradeForm.dbpre.options[index].text.substring(0,left_index)+"(已评价)" ; 
				for(var i=0; i<document.batchGradeForm.elements.length; i++)
				{
					if(document.batchGradeForm.elements[i].type=='checkbox')
						document.batchGradeForm.elements[i].disabled=true;
				}
				var isShowSubmittedPlan=outparamters.getValue("isShowSubmittedPlan");
				if(isShowSubmittedPlan=='false')
				{
				   document.batchGradeForm.action="/selfservice/performance/batchGrade.do?b_setFashion=link";
				   document.batchGradeForm.submit();
				}else{
                    //刷新页面
                    batchGradeForm.action="/selfservice/performance/batchGrade.do?b_Desc=query";
                    batchGradeForm.submit();
                }
			}
			else
			{
				 document.batchGradeForm.action="/selfservice/performance/batchGrade.do?b_Desc=query";
				 document.batchGradeForm.submit();
			}
			
			
			 
			
		}else if(flag==2&&info==('存在等级不同总分相同的考核对象，不能提交！')){
			 document.batchGradeForm.action="/selfservice/performance/batchGrade.do?b_Desc="+plan_id+"&selectNewPlan=true&operate=aaa"+plan_id;
			 document.batchGradeForm.submit();
		}	
	}
}
	
function indiviPerformance(plan_id,object_id,mainbody_id)
{
	batchGradeForm.action="/selfservice/performance/singleGrade.do?b_individual=search&operate=3&plan_id="+plan_id+"&operates=<%=(request.getParameter("operate"))%>&object_id="+object_id+"&mainbody_id="+mainbody_id;
	batchGradeForm.submit();

}
function executeGradeHtml(){
      var objectid = "";
      var temps = document.getElementById("objectbox");
        for(i=0;i<temps.options.length;i++)
		{
	        if(temps.options[i].selected)
	        { 
		    	document.getElementById("selectname").value=temps.options[i].text;
	            objectid = temps.options[i].value;
	            Element.hide('date_panel2');
	            if(objectid)
	               break;
		    }
		}
		if(objectid){
			if(document.getElementById("tbl").rows.length>1)
				{
				    var zz = 0;
					for(var i=0;i<document.getElementById("tbl").rows.length;i++)
					{	
					    if(i>1)
					    zz+=document.getElementById("tbl").rows[i].offsetHeight;
						if(document.getElementById("tbl").rows[i].cells.length>0 &&  document.getElementById("tbl").rows[i].cells[0].innerHTML.length>0 && document.getElementById("tbl").rows[i].cells[0].innerHTML.indexOf(objectid)!=-1) 
						{
							document.getElementById("tbl").rows[i].className='cell_yellow';
							document.getElementById("tbl-container").scrollTop=zz-60;
						}else{
						    document.getElementById("tbl").rows[i].className='';
						}
					}		
				}
        }
    }
function excecuteTable2()
{
		
		<% if(request.getParameter("operate")!=null&&request.getParameter("operate").length()>0){ %>
		batchGradeForm.action="/selfservice/performance/batchGrade.do?b_Desc=${batchGradeForm.dbpre}&operate=<%=(request.getParameter("operate"))%>";
		<% }else{ %>
		batchGradeForm.action="/selfservice/performance/batchGrade.do?b_Desc=${batchGradeForm.dbpre}";
		<% } %>
		batchGradeForm.submit();
		
}

function goback()
{	
<%
	String _str="";
	if(flag.equals("2"))
		_str=".parent";
		if(returnflag!=null&&returnflag.equals("8")){
			if(isEpmLoginFlag.equals("1")){
				out.println("parent.parent.location='/templates/index/subportal.do?b_query=link'");
			}else{
				if(hcmflag!=null&&hcmflag.equals("hcm")){//7.0页面返回   zhaoxg 2014-4-9
					out.println("window"+_str+".location='/templates/index/hcm_portal.do?b_query=link'");	
				}else{
			        out.println("window"+_str+".location='/templates/index/portal.do?b_query=link'");			
				}

			}
		}
		else if(returnflag!=null&&returnflag.equals("10")){
			out.println("window"+_str+".location='/general/template/matterList.do?b_query=link'");
		}
		else
		{
			if(performanceType.equals("leader")){
	
		%>
			window.parent.opener.location="<%=url_str%>";
		<% 		 }else{%>
	       window.parent.opener.location="<%=url_str%>";
		<%      }     %>
			window.parent.close();
		<%   } %>
}

function displayIMG(obj1,obj2)//2013.11.09 pjf
{
   var tab=document.getElementById(obj1);
   var ig=document.getElementById(obj2);
   if(tab.style.display=='none')
   {
      tab.style.display='block';
      ig.src='/images/expand_pm.gif';
   }
   else
   {
      tab.style.display='none';
      ig.src='/images/collapse_pm.gif';
   }
}

 function hiddenWin()
 {
 	var iframe=document.getElementById("objectTask");
 	iframe.src="/templates/welcome/welcome.html";
 	with($('objectTask'))
	{
	        style.position="absolute";
	        style.posLeft=document.body.clientWidth;
	        style.posTop=document.body.clientHeight;  
	        style.width=0;
	        style.height=0;
   }  
 	iframe.style.display="none";
 }
		
 function openWin(url)
 { 
 	  Element.show('objectTask');   
	  with($('objectTask'))
	  {
	        style.position="absolute";
	        style.left=150+"px";
	        style.top=5+"px";
	        style.width=document.body.clientWidth-160;
	        <%  if(SystemConfig.getPropertyValue("clientName")!=null&&SystemConfig.getPropertyValue("clientName").trim().equalsIgnoreCase("zglt")) { %>
			        style.height=document.body.clientHeight-150;
      		<%   }else{ %>
      				style.height=document.body.clientHeight-80;
      		<%   }  %>
      
      }  
      var iframe=document.getElementById("objectTask");
      iframe.src=url;
      
 }	

  var t=null;

  function setLayer()
  {
   	if(a_planID!=0)
   	{
        var Browser_Name=navigator.appName;
        var Actual_Version;
        var is_IE=(Browser_Name=="Microsoft Internet Explorer");
        if(is_IE){
	      	var Browser_Agent=navigator.userAgent;
            var Version_Start=Browser_Agent.indexOf("MSIE");
            var Version_End=Browser_Agent.indexOf(";",Version_Start);
            Actual_Version=Browser_Agent.substring(Version_Start+5,Version_End)
			if(Actual_Version<7)
			{
				if(document.getElementById("b")!=null)
				{
			   		var objs=eval("b"); 		
			   		for(var i=0;i<objs.length;i++)
			   		{
			   			
			   			objs[i].innerHTML=objs[i].innerHTML+"<iframe src=\"javascript:false\" style=\"position:absolute; visibility:inherit; top:0px; left:0px;"
			   			 				+"width:"+objs[i].offsetWidth+"; height:"+objs[i].offsetHeight+"; " 					    	
			   			 				+"z-index:-1;filter='progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)';\"></iframe>";   					
			   		}
			   	}
		   	}
   		
   		}

   	//	if(checkIsIntNum(grading_auto_saving))
   		{
   			var autoTime = "0";
   			var autoType;
   			if(grading_auto_saving.indexOf("h")!=-1 || grading_auto_saving.indexOf("H")!=-1) // 时
   			{
   				autoTime = grading_auto_saving.substring(0,grading_auto_saving.length-1);
				autoType = autoTime*1*60*60*1000;
   			
   			}else if(grading_auto_saving.indexOf("m")!=-1 || grading_auto_saving.indexOf("M")!=-1) // 分
   			{
   				autoTime = grading_auto_saving.substring(0,grading_auto_saving.length-1);
				autoType = autoTime*1*60*1000;
   			
   			}else if(grading_auto_saving.indexOf("s")!=-1 || grading_auto_saving.indexOf("S")!=-1) // 秒
   			{
   				autoTime = grading_auto_saving.substring(0,grading_auto_saving.length-1);
				autoType = autoTime*1*1000;
   			}
			else
			{								
				if(checkIsIntNum(grading_auto_saving)) // 默认为分钟
				{
					autoTime = grading_auto_saving;
					autoType = autoTime*1*60*1000;
				}else
				{
					autoTime = "0";
					autoType = "0";
				}
			}
						
   			if(autoTime*1>0)
   			{
   				<logic:notEqual name="batchGradeForm" property="dbpre" value="0">
				<logic:notEqual name="batchGradeForm" property="userIDs" value="">
				<logic:notEqual name="batchGradeForm" property="gradeStatus" value="2">				
					
   					t=window.setInterval('save2()',autoType);  					
   						
   				</logic:notEqual>	
   				</logic:notEqual>	 
   				</logic:notEqual>	 
   			}
   		}
   	}
   }
   
   
   function save2()
   {
   		var hashvo=new ParameterSet();
		var fillCtrs=fillCtr.split("/");
		var users_arry=users.split("/");
		if(pointDeformity=='1')
		{
			var n_ids=noGradeItem.split(",");
			var desc="";
			for(var i=0;i<n_ids.length;i++)
			{
				if(trim(n_ids[i]).length!=0)
				{
					desc+="\n\r."+per_pointArray[n_ids[i]].pointname;
				}
			}
			alert(desc+"\n\r 标度上下限没有设定，不予自动操作！");
			return;
		}
		
		for(var i=0;i<users_arry.length;i++)
		{	
				var str="";
				var userResult=obj_values['_'+users_arry[i]];
				var temp_arr=points.split("/");
				for(var j=0;j<temp_arr.length;j++)
				{
					str+="/"+userResult[temp_arr[j]];
				}
				hashvo.setValue(users_arry[i],str.substring(1));					
		}	
		hashvo.setValue("users",users);
		hashvo.setValue("plan_id",plan_id);
		hashvo.setValue("template_id",template_id);
		hashvo.setValue("status",status);
		hashvo.setValue("scoreflag",scoreflag);
		var In_paramters="flag=1"; 		
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:autoReturnInfo,functionId:'90100150003'},hashvo);	
   }
   
   
    
function autoReturnInfo(outparamters)
{
	var score_order=outparamters.getValue("score_order");
	var totalAppValue=getDecodeStr(outparamters.getValue("totalAppValue"));
	var isShowOrder="${batchGradeForm.isShowOrder}";			//是否显示排名
	var isShowTotalScore="${batchGradeForm.isShowTotalScore}";  //是否现实总分
	var isAutoCountTotalOrder="${batchGradeForm.isAutoCountTotalOrder}";
	var flag=outparamters.getValue("flag");
//	var BlankScoreOption=outparamters.getValue("BlankScoreOption"); // 指标未打分时，0 按未打分处理，1 计为最高分，默认值为按未打分处理
	
	if(score_sumtotal=='true'&&scoreflag=='2')
	{
			 writeSumScore();
	}
	if(isShowOrder=="true"||isShowTotalScore=="true")
	{
			if(score_order!='')  
			{
				var score_order_arr=score_order.split('#');
				for(var i=0;i<score_order_arr.length;i++)
				{
					var values=score_order_arr[i].split('/');
				 	if(isShowOrder=="true")
				 	{
					 	var temp2=eval('pm'+i);
						temp2.innerHTML="<font  color='#2E67B9'   >"+values[1]+"</font>";
				 	}
				 	if(isShowTotalScore=="true")
				 	{
				 		var temp=eval('zf'+i);
				 		temp.innerHTML="<font  color='#2E67B9'   >"+values[0]+"</font>";
				 	}
				}
			}	
	}
	if(totalAppValue!=null && totalAppValue.length>0)
	{
		var wholeEvalValue = totalAppValue.split('#');
		for(var i=0;i<wholeEvalValue.length;i++)
		{
			var values = wholeEvalValue[i];
			var totalApp = eval('totalAppValue'+i);
			totalApp.innerHTML = values;
		}			
	}
		
	var info=getDecodeStr(outparamters.getValue("info"));
	info=replaceAll(info,'<br>','\n\r');
	
	if(info=='保存成功!')
	{
			
	}
	else 
	{
		alert(info);
	}
		
}


 function allSub()
  {
		if(confirm('本次测评是严肃认真的，请您确认后提交!'))
		{
			var hashvo=new ParameterSet();
			hashvo.setValue("planIDs",'<%=(str2.toString())%>');
			var request=new Request({method:'post',asynchronous:false,onSuccess:returnInfoAll,functionId:'90100150009'},hashvo);	
		}
  }
  
  function returnInfoAll()
  {
  		alert("提交成功!");
		
		if(fasion=='2')
		{
			window.parent.document.forms[0].action='/selfservice/performance/batchGrade.do?b_tileFrame=link&model=0&linkType=1&planContext=all';
			window.parent.document.forms[0].submit();
		}
		if(fasion=='1')
		{
			 document.batchGradeForm.action="/selfservice/performance/batchGrade.do?b_Desc=query";
			 document.batchGradeForm.submit();
		
		}
  }
 function showDateSelectDiv(srcobj)
 {
   		if($F('selectname')=="")
   		{
   			Element.hide('date_panel2');
   			return false ;
   		}
      date_desc=document.getElementById(srcobj);
      Element.show('date_panel2');
      var pos=getAbsPosition(date_desc);
	  with($('date_panel2'))
	  {
        style.position="absolute";
        if(typeof(window.addEventListener)=="function")
        {
        	style.left=pos[0];
			style.top=pos[1]-date_desc.offsetHeight+42;
        }
        else
        {
        	style.posLeft=pos[0];
			style.posTop=pos[1]-date_desc.offsetHeight+42;
        }
		
		//alert(pos[1]);
		//alert(date_desc.offsetHeight);
		
		style.width=(date_desc.offsetWidth<20)?150:date_desc.offsetWidth+1;
      }
    
	 
      var hashVo = new ParameterSet();
      hashVo.setValue("name",getEncodeStr(document.getElementById('selectname').value));
      hashVo.setValue("plan_id",plan_id);
      hashVo.setValue("mainBodyID",mainbodyId);
      hashVo.setValue("model","3");
      hashVo.setValue("current",current);
      
      var request=new Request({method:'post',asynchronous:false,onSuccess:shownamelist,functionId:'90100160019'},hashVo);
}
function shownamelist(outparamters)
   {
   		var namelist=outparamters.getValue("namelist");
		if(namelist.length==0){
			Element.hide('date_panel2');
		}
		else{
		    if(namelist.length<6){
		      document.getElementById("date_box").size=namelist.length;
		    }else{
		      document.getElementById("date_box").size = 6;
		    }
			AjaxBind.bind(batchGradeForm.contenttype,namelist);
		}
   }
   
function setSelectValue()
	{
		var temps=document.getElementById("date_box");
		
		for(i=0;i<temps.options.length;i++)
		{
		   
	        if(temps.options[i].selected)
	        { 
		    	document.getElementById("selectname").value=temps.options[i].text;
	            objectid = temps.options[i].value;
	            Element.hide('date_panel2');
		    }
		}
		
	} 
/** 查询 */
	function query()
	{
		if(document.getElementById('selectname').value.length==0)
		{
			alert("请输入姓名信息!");
			return;
		}
		if(objectid){
			if(document.getElementById("tbl").rows.length>1)
			{
			    var zz = 0;
				for(var i=0;i<document.getElementById("tbl").rows.length;i++)
				{	
				    if(i>1)
				    zz+=document.getElementById("tbl").rows[i].offsetHeight;
					if(document.getElementById("tbl").rows[i].cells.length>0 &&  document.getElementById("tbl").rows[i].cells[0].innerHTML.length>0 && document.getElementById("tbl").rows[i].cells[0].innerHTML.indexOf(objectid)!=-1) 
					{
						document.getElementById("tbl").rows[i].className='cell_yellow';
						document.getElementById("tbl-container").scrollTop=zz-60;
					}else{
					    document.getElementById("tbl").rows[i].className='';
					}
				}		
			}
		}
	}
		/** 禁用鼠标滚轮 **/
	function stop_onmousewheel(){
		for(var i=0;i<document.batchGradeForm.getElementsByTagName('select').length;i++){
			document.batchGradeForm.getElementsByTagName('select')[i].onmousewheel = function (){
			return false;}
		}
	}
	function queryRecord()
	{
		var theurl="/selfservice/performance/batchGrade.do?b_ShowRecord=link`planid="+plan_id;
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
	   	//var return_vo= window.showModalDialog(iframe_url, 'template_win', 
	   //   				"dialogWidth:560px; dialogHeight:450px;resizable:no;center:yes;scroll:yes;status:no");
	   	//兼容非IE浏览器 弹窗替换用 open弹窗  wangb 20171205
       var iTop = (window.screen.availHeight - 30 - 560) / 2; //获得窗口的垂直位置
       var iLeft = (window.screen.availWidth - 10 - 450) / 2; //获得窗口的水平位置
	   	window.open(iframe_url, 'template_win',"width=560px,height=450px,resizable=no,scroll=yes,status=no,left="+iLeft+",top="+iTop);
	}
	function historyScore()
	{
		var theurl="/selfservice/performance/batchGrade.do?b_historyScore=link&flag=3&planid="+plan_id;
		var iTop = (window.screen.availHeight-400)/2; //获得窗口的垂直位置;
		var iLeft = (window.screen.availWidth-580)/2; //获得窗口的水平位置;
		var params="height=450,width=550,top="+iTop+",left="+iLeft+",Resizable=no,scrollbars=no,toolbar=no,location=no,menubar=no,status=no";
	   	window.open(theurl,'',params);
	}
</script>

<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<META HTTP-EQUIV="Expires" CONTENT="0">
 <hrms:themes />

</head>
<!--  <BODY  onload='setLayer()'  onresize='resize()' >  -->
<BODY onload='setLayer();stop_onmousewheel()' onresize='resize()' >
<html:form action="/selfservice/performance/batchGrade">
	<table border='0' width='95%'>
	
	
		<tr><td align='left' valign="middle">
			&nbsp;
			<% 
			if(!flag.equals("2")&&!linkType.equalsIgnoreCase("liantong"))
			{
			
			%>
			<span style="vertical-align: middle">
			<bean:message key="lable.performance.perPlan"/>			
			</span>
	     	<hrms:optioncollection name="batchGradeForm" property="dblist" collection="list"   />
	             <html:select name="batchGradeForm"   property="dbpre" size="1" onchange="excecuteTable()"  >
	             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
	        </html:select>
	        
	        <input type="text" value="" id="selectname" onkeyup="showDateSelectDiv('selectname')"/>
			&nbsp;<input type="button" class='<%=buttonClass%>' value="查找" onclick="query()"/>
			&nbsp;
			<span>
		        <logic:equal name="batchGradeForm" property="dayWeekMonthFlag" value="true">
					<input type="button" class='<%=buttonClass%>' value="日志填报情况表" onclick="queryRecord()"/>
				</logic:equal>
		        <%--<logic:equal name="batchGradeForm" property="dayWeekMonthFlag" value="true">--%>
					<logic:equal name="batchGradeForm" property="showHistoryScore" value="True">
						<input type="button" class='<%=buttonClass%>' value="历次得分表" onclick="historyScore()"/>
					</logic:equal>
				<%--</logic:equal>--%>
				<span>
					${batchGradeForm.targetDeclare}
					<logic:notEqual name="batchGradeForm" property="dbpre" value="0">
				    	<logic:notEqual name="batchGradeForm" property="isPage" value="0">
							<hrms:optioncollection name="batchGradeForm" property="pageList" collection="list2"/>
					             <html:select name="batchGradeForm" property="current" onchange="excecuteTable2()"  size="1" >
					             <html:options collection="list2" property="dataValue" labelProperty="dataName"/>
					        </html:select><br>
						</logic:notEqual>
					</logic:notEqual>
		    	</span>
			</span>
			</td>
			<td>
	        <% }else{ %>
	        
	        <div style="position:absolute;z-index:2;left:13px;top:10px;width:121px;height:20px">        
			<input type="text" value="" id="selectname" onkeyup="showDateSelectDiv('selectname')" style="position:absolute;width:121px; height:22px;left:0"/>
			</div>
			<div style="position:absolute;z-index:1;width:137px;left:13px;top:10px;height:20px"> 
			<hrms:optioncollection name="batchGradeForm" property="objectList" collection="list" />
			             <html:select styleId="objectbox" name="batchGradeForm" property="object_id" size="1" onchange="executeGradeHtml()"  style="width:137px;height:20px;clip:rect(0 137 20 121);left:0">
			             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
			        </html:select> 
			</div>
	        <div style="position:absolute;top:10px;width:163px;height:20px;left:157px;">
			<input type="button" class='<%=buttonClass%>' value="查找" onclick="query()"/>
			&nbsp;&nbsp;
	        </div >
	        </td>
			<td>
	        <div style="position:absolute;top:10px;width:400px;left:210px;">
	        <logic:equal name="batchGradeForm" property="dayWeekMonthFlag" value="true">
				<input type="button" class='<%=buttonClass%>' value="日志填报情况表" onclick="queryRecord()"/>
			</logic:equal>
	       <%-- <logic:equal name="batchGradeForm" property="dayWeekMonthFlag" value="true">--%>
		        <logic:equal name="batchGradeForm" property="showHistoryScore" value="True">
				<input type="button" class='<%=buttonClass%>' value="历次得分表" onclick="historyScore()"/>
				</logic:equal>
			<%--</logic:equal>--%>
				<div style="position:absolute;">
				${batchGradeForm.targetDeclare}
					    	<logic:notEqual name="batchGradeForm" property="dbpre" value="0">
		    	<logic:notEqual name="batchGradeForm" property="isPage" value="0">
				<hrms:optioncollection name="batchGradeForm" property="pageList" collection="list2"   />
		             <html:select name="batchGradeForm" property="current" onchange="excecuteTable2()"  size="1" >
		             <html:options collection="list2" property="dataValue" labelProperty="dataName"/>
		        </html:select><br>
		
				</logic:notEqual>
	  	  </logic:notEqual>
	    		</div>

	        </div>
	        <% } %>
	      <div id="date_panel2" style="display:none;z-index:50">
		   <select id="date_box" name="contenttype"  onblur="Element.hide('date_panel2');"  multiple="multiple"  style="width:270" size="6"  ondblclick="setSelectValue();">
           </select>
           </div>
	    </td></tr>
	<%  if(SystemConfig.getPropertyValue("clientName")!=null&&SystemConfig.getPropertyValue("clientName").trim().equalsIgnoreCase("zglt")) { %>     
	    <tr><td align='center' ><font class='fontStyle_self2'> <%=planName%></font> </td></tr>
	<%  } %> 
	
	
	  
	
 </table> 
       
<logic:notEqual name="batchGradeForm" property="dbpre" value="0">
	<%if("hl".equals(hcmflag)){ %>
<br>
<%} %>
	<script type="text/javascript">

		if(typeof(window.addEventListener)=="function")
		{
			var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
			if (userAgent.indexOf("Firefox") > -1)
			{
				//如果是火狐
				<%-- <%  if(SystemConfig.getPropertyValue("clientName")!=null&&SystemConfig.getPropertyValue("clientName").trim().equalsIgnoreCase("zglt")) { %> 
					document.write("<div id='tbl-container'  style='position:absolute;left:5;width:"+(document.body.clientWidth*0.25)+";height:"+(document.body.clientHeight-120)+";margin-top:5px;'>");
				<%}else{%> --%>
					document.write("<div id='tbl-container' onscroll='closeInputs()'  style='position:absolute;left:5;width:"+(document.body.clientWidth*0.25)+";height:"+(document.body.clientHeight-120)+";margin-top:5px;'>");
				<%-- <%}%> --%>
		
			} 
			else if (userAgent.indexOf("Safari") > -1||isFjr==1)
			{
				//如果是sarari
				<%-- <%  if(SystemConfig.getPropertyValue("clientName")!=null&&SystemConfig.getPropertyValue("clientName").trim().equalsIgnoreCase("zglt")) { %> 
					document.write("<div id='tbl-container'  style='position:absolute;left:5;width:95%;height:"+(document.body.clientHeight-120)+";margin-top:5px;'>");
				<%}else{%> --%>
					document.write("<div id='tbl-container' onscroll='closeInputs()' style='position:absolute;left:5px;width:95%;height:"+(document.body.clientHeight-120)+"px;margin-top:5px;'>");
				<%-- <%}%> --%>
			}else{
                <%if("bi".equals(hcmflag)){ %>
                document.write("<div id='tbl-container' onscroll='closeInputs()'  style='position:absolute;left:5;width:95%;height:"+(document.body.clientHeight-120)+"px;margin-top:5px;top:30px;'>");
                <%}else{%>
                document.write("<div id='tbl-container' onscroll='closeInputs()'  style='position:absolute;left:5;width:95%;height:"+(document.body.clientHeight-120)+"px;margin-top:5px;'>");
                <%}%>
            }
			
		}
		else
		{
			<%-- <%  if(SystemConfig.getPropertyValue("clientName")!=null&&SystemConfig.getPropertyValue("clientName").trim().equalsIgnoreCase("zglt")) { %> 
				document.write("<div id='tbl-container'  style='position:absolute;left:5;width:95%;height:"+(document.body.clientHeight-120)+"px;margin-top:5px;'>");
			<%}else{ --%>
			<%if("bi".equals(hcmflag)){ %>
					document.write("<div id='tbl-container' onscroll='closeInputs()'  style='position:absolute;left:5;width:95%;height:"+(document.body.clientHeight-120)+"px;margin-top:5px;top:30px;'>");
				<%}else{%>
					document.write("<div id='tbl-container' onscroll='closeInputs()'  style='position:absolute;left:5;width:95%;height:"+(document.body.clientHeight-120)+"px;margin-top:5px;'>");
				<%}%>
			<%-- <%}%> --%>
		}
	</script>
	
		<table id="tbl"  class='ListTable_locked common_border_color' >
		<!-- 【6698】多人考评，安全问题及界面问题  jingq add 2015.01.20 -->
		   <style>
		   	.ListTable_locked{
		   		cellpadding:0;
		   		cellspacing:0;
		   		border:none;
		   	}
		   	.RecordRow{
		   		border-top:none;
		   		border-left:none;
		   	}
		   </style> 
		
           
			${batchGradeForm.tableHtml}
		</table>
	    
		
		<%  if(SystemConfig.getPropertyValue("clientName")!=null&&SystemConfig.getPropertyValue("clientName").trim().equalsIgnoreCase("zglt")) { %>     
			<br><br>
			<span class='fontStyle_self3' >
			${batchGradeForm.plan_descript_content}
			</span>
			<Br><br>&nbsp;
		<% } %>
		
	</div>
 	
	


	<script type='text/javascript'>
	<%-- <%if("hl".equals(hcmflag)){ %> --%>
	//改用bottom 方式
	document.write("<div id='ff' style='position:absolute;left:5px;bottom:40px;'  >");
	<%-- <%}else{ %>
	document.write("<div id='ff' style='position:absolute;left:5;top:"+((document.body.clientHeight*0.9)-5+5)+"'  >");
	<%} %> --%>
		
	</script>
	
	<%  if(SystemConfig.getPropertyValue("clientName")!=null&&SystemConfig.getPropertyValue("clientName").trim().equalsIgnoreCase("hkyh")) { %>
		<table><tr><td>
	<%  } %>
	
	<logic:notEqual name="batchGradeForm" property="dbpre" value="0">
		<logic:notEqual name="batchGradeForm" property="userIDs" value="">
		 	<span id="buttons">
		 		<logic:notEqual name="batchGradeForm" property="gradeStatus" value="2">
			         <hrms:priv func_id="06060102">
			         	<Input type='button' value=' <bean:message key="lable.performance.ghostScore"/> ' class='<%=buttonClass%>'  name='b_ghostScore'  onclick="ghostScore('${batchGradeForm.dbpre}')" />
					 </hrms:priv>
					 <Input type='button' value=' <bean:message key="button.temporary.save"/> ' title='保存当前页面打分结果' class='<%=buttonClass%>'  name='b_save'  onclick="check(1)" />
					 
				</logic:notEqual>	 
				<%
				if(isShowOrder.equalsIgnoreCase("true"))
				{
					if(!(SystemConfig.getPropertyValue("clientName")!=null&&SystemConfig.getPropertyValue("clientName").trim().equalsIgnoreCase("zglt"))){
				%>
					   
					 <Input type='button' value='<bean:message key="lable.performance.totalScoreOrder"/>'   class='<%=buttonClass%>'  name='b_order'  onclick="check(3)" />
			    <%
			    	}
			    }
			    %>
			    
			    <hrms:priv func_id="06060103"><!-- 计算 -->
			    	<Input type='button' value=' <bean:message key="button.computer"/> ' class='<%=buttonClass%>'  name='b_compuScore'  onclick="compuScore('${batchGradeForm.dbpre}')" />
				</hrms:priv>
					 
			    <logic:notEqual name="batchGradeForm" property="gradeStatus" value="2">	 
			    <% if(!togetherCommit.equalsIgnoreCase("True")){ %><!-- 提交 -->
			     <Input type='button' value=' <bean:message key="button.submit"/> '     class='<%=buttonClass%>'  name='b_sub'  onclick="check(2)" />
			    <% } %>
			     </logic:notEqual> 
			     
				
				 
				<logic:notEqual name="batchGradeForm" property="gradeStatus" value="2">	 
					<% if(togetherCommit.equalsIgnoreCase("True")){ %>
						  <Input type='button' value='打分完成'   class='<%=buttonClass%>'   name='b_finished'  onclick="check(8)" />
				    <% }  %>				
				 </logic:notEqual> 
				 
				 <% 
				 if(!(SystemConfig.getPropertyValue("clientName")!=null&&SystemConfig.getPropertyValue("clientName").trim().equalsIgnoreCase("zglt")))
				 {
				 if(isShowUnifyScore.equals("1")&&togetherCommit.equalsIgnoreCase("True")){ %>
				  <Input type='button' value='统一提交' class='<%=buttonClass%>'  name='b_suball'    onclick="allSub()" />
				 
				 <%
				 }
				  } %>
		    </span>		    		     
		    <%--2013.11.09 pjf --%>
		    <% if(SystemConfig.getPropertyValue("clientName")==null||SystemConfig.getPropertyValue("clientName").trim().equalsIgnoreCase("zglt")|| !SystemConfig.getPropertyValue("clientName").trim().equalsIgnoreCase("hkyh")){ %>
		     <logic:equal name="batchGradeForm" property="plan_descript" value="1">	 
		     <%  String dbpre_code=PubFunc.encryption(dbpre);%>
				 	<Input type='button' value='填表说明' class='<%=buttonClass%>'  name='b_declare'  onclick="planDescript('<%=dbpre_code%>')" />
			</logic:equal>
			<% } %>
			<!--  -->
			<hrms:priv func_id="06060101">
				<Input type='button' value='导出Excel' class='<%=buttonClass%>'  name='b_excel'  onclick="executeExcel('${batchGradeForm.dbpre}')" />
			</hrms:priv>
			
			${batchGradeForm.individualPerformance}
			
			<% 
				String bosflg = userView.getBosflag();
				if("bi".equalsIgnoreCase(bosflg)) {%>
			 		<Input type='button' value=' 返回 '   class='<%=buttonClass%>' name='b_back'  onclick="parent.window.location.href='/templates/index/bi_portal.do?b_query=link'" />
		 	<%}else if(linkType.equalsIgnoreCase("liantong")||returnflag.equals("8")||returnflag.equals("10")){ %>
				  <Input type='button' value=' 返回 '   class='<%=buttonClass%>' name='b_back'  onclick="goback()" />
			<% } %>
			
		  </logic:notEqual>   	    
     </logic:notEqual>
     
     <%-- 2013.11.09 pjf --%>
		<%  if(SystemConfig.getPropertyValue("clientName")!=null&&SystemConfig.getPropertyValue("clientName").trim().equalsIgnoreCase("hkyh")) { %>
		</td><td>
		<logic:equal name="batchGradeForm" property="plan_descript" value="1">	  
		<%=plan_descript_content%>  
		</logic:equal>
		</td></tr></table>
		<%} %>
     
     
     
	</div>

</logic:notEqual>



<input type="hidden" name="titleName" value="" />

<div id="date_panel" style="z-index:30">
   			
</div>


<iframe id="objectTask"   frameborder="no"  name="objectTask" style="display:none;border:1px ridge #C4D8EE;background-color: white;"></iframe>



<div id="options"  onmouseleave="closeDiv()"  >


</div>
	
<div id="inputs"   >


</div>	
<div id='wait' style='position:absolute;top:100;left:400;display:none;'>
		<table border="1" width="100" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td id='wait_desc' class="td_style" height=24>
					正在提交，请稍候......
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee class="marquee_style"  direction="right" width="300" scrollamount="5" scrolldelay="10"  >
						<table cellspacing="1" cellpadding="0">
							<tr height=8>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
							</tr>
						</table>
					</marquee>
				</td>
			</tr>
		</table>
		<iframe src="javascript:false" style="position:absolute; visibility:inherit; top:0px; left:0px;width:315; height:87; 					    	
			   			 				z-index:-1;filter='progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)';"></iframe>	
	</div>
	
</html:form>


<script language="javascript">
   Element.hide('date_panel');
   
   ${batchGradeForm.script_code}



   var ori_height=document.body.clientHeight;
   var a_var=0;
   function resize()
   {
   			var anew=document.body.clientHeight;
   			if(anew!=ori_height&&a_var==0)
   			{
   				document.location.reload();
   				a_var=1;
   				ori_height=document.body.clientHeight;
   			}
   }
   
   
    var a_planID=${batchGradeForm.dbpre};
    window.status="";
   var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
   var isFF = userAgent.indexOf("Firefox") > -1; //判断是否Firefox浏览器
   var isChrome = userAgent.indexOf("Chrome") > -1 && userAgent.indexOf("Safari") > -1; //判断Chrome浏览器
   var obje = document.getElementsByName("dbpre")[0];
    <% if(flag.equals("1")){
    		String operate=request.getParameter("operate");
    		if(operate!=null)
    		{
    			String planid=operate.substring(3);
    		%>
    			var _planid='<%=planid%>';
    			for(var i=0;i<obje.options.length;i++)
    			{
    				if(obje.options[i].value==_planid)
    				{
                        obje.options[i].selected=true;
    					if(!obje.fireEvent){
                            var event = document.createEvent('HTMLEvents');
                            event.initEvent("change", true, true);
                            obje.options[1].dispatchEvent(event);
						}else {
                            obje.fireEvent("onChange");
                        }
    				}
    			}

    		<%
    		}
    		else if(request.getParameter("b_query")!=null)
    		{
    	%>
    			if(a_planID==0 && obje.options.length>1)
    			{
                    obje.options[1].selected=true;
                    if(!obje.fireEvent){
                        var event = document.createEvent('HTMLEvents');
                        event.initEvent("change", true, true);
                        obje.options[1].dispatchEvent(event);
                    }else {
                        obje.fireEvent("onChange");
                    }
    			}
    	<%
    		}
    	}
     %>
     
     function executeExcel(planid)
     {
     	var onlyPram = '${batchGradeForm.onlyFild}';	
		if(onlyPram==null || onlyPram.length<=0)
		{
			alert('系统没有指定唯一性指标,不能下载模板!');
			return;
		} 
		var In_paramters="planid="+planid; 		
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showExcel,functionId:'90100150013'});
     
     }
     
     function showExcel(outparameters)
     {
      	 var fileName=outparameters.getValue("fileName"); 
//	 	 var win=open("/servlet/DisplayOleContent?filename="+fileName,"excel");
       	 //20/3/6 xus vfs改造
       	 var win=open("/servlet/vfsservlet?fileid="+fileName+"&fromjavafolder=true");
     }

 	var aa=document.getElementsByTagName("input");
 	for(var i=0;i<aa.length;i++){
 		if(aa[i].type=="text"){
 			aa[i].className="inputtext";
 		}
 	}
  	/*非IE浏览器样式兼容性 */
	var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
	var isFF = userAgent.indexOf("Firefox") > -1; //判断是否Firefox浏览器
	var isChrome = userAgent.indexOf("Chrome") > -1 && userAgent.indexOf("Safari") > -1 && userAgent.indexOf("Edge") == -1; //判断Chrome浏览器
    var isEdge = userAgent.indexOf("Edge") > -1; //判断是否Edge浏览器
	if(isFF){//firefox 浏览器 样式单独处理   wangb 20171206
		var tbl = document.getElementById('tbl');
		tbl.setAttribute('border','5');
		var tblContainer = document.getElementById('tbl-container');
		tblContainer.style.width = '98%';
		var headTd = tblContainer.getElementsByClassName('cell_locked2')[0];
		headTd.style.top = '0px';
		headTd.style.left = '0px';
		headTd.style.setProperty('background-color', 'transparent', 'important');
		var headTds = tblContainer.getElementsByClassName('header_locked');
		for(var i = 0 ; i < headTds.length ; i ++){
			headTds[i].style.top = '0px';
			headTds[i].style.left = '0px';
			headTds[i].style.setProperty('background-color', 'transparent', 'important');
		}
		var cellTds = tblContainer.getElementsByClassName('cell_locked');
		for(var i = 0 ; i < cellTds.length ; i ++){
			cellTds[i].style.left = '0px';
			cellTds[i].style.setProperty('background-color', 'transparent', 'important');
		}
	}else if(isChrome){//chrome 浏览器 样式单独处理   wangb 20171206
		/* var tblContainer = document.getElementById('tbl-container');
		var cellTd0 = tblContainer.getElementsByClassName('cell_locked')[0];
		var cellTd1 = tblContainer.getElementsByClassName('cell_locked')[1];
		cellTd0.style.setProperty('border-top-width', '1px', 'important');
		cellTd1.style.setProperty('border-top-width', '1px', 'important'); */
		//谷歌浏览器 样式单独处理  bug 35054 xus 18/3/21
		var lockTds = document.querySelectorAll("td[class*='cell_locked']");
        for(i = 0; i < lockTds.length; i++) {
            lockTds[i].style.borderTop='#C4D8EE 1pt solid';
        }
	}else if(isEdge) {
        var lockTds = document.querySelectorAll("td[class*='header_locked']");
        for(var i=0;i<lockTds.length;i++){
            lockTds[i].style.position='static';
        }
        var cellLockTds = document.querySelectorAll("td[class*='cell_locked']");
        for(var i=0;i<cellLockTds.length;i++){
            cellLockTds[i].style.position='static';
        }
    }
	
</script>
</BODY>
</HTML>
<script>
if (!isCompatibleIE()){
    //不是IE兼容模式的样式特殊处理
    var lockTds = document.querySelectorAll("td[class*='header_locked']");
    for(var i=0;i<lockTds.length;i++){
        lockTds[i].style.position='static';
    }
    var cellLockTds = document.querySelectorAll("td[class*='cell_locked']");
    for(var i=0;i<cellLockTds.length;i++){
        cellLockTds[i].style.position='static';
    }
    cellLockTds = document.querySelectorAll("td[class*='cell_locked2']");
    for(var i=0;i<cellLockTds.length;i++){
        cellLockTds[i].style.position='static';
    }
}
if(!getBrowseVersion()){ //兼容非IE浏览器样式 问题  bug 34524 wangb 20180208
	var form = document.getElementsByName('batchGradeForm')[0];
	var table1 = form.getElementsByTagName('table')[0];
	var div2 = table1.getElementsByTagName('td')[1].getElementsByTagName('div')[1]; // 下拉框 显示位置不对
	if(div2) {
        div2.style.top = '0px';
        div2.style.left = '100px';
    }
	var select1 = table1.getElementsByTagName('select')[0]; 
	select1.style.height='22px';
	var selectPage = document.getElementsByName('current')[0];//bug 36830 显示页数下拉框与按钮重叠  wangb 20180420
	if(selectPage)
		selectPage.style.marginLeft='84px';
	
	var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串 
	var isFF = userAgent.indexOf("Firefox") > -1; //判断是否Firefox浏览器
	if(isFF){//火狐浏览器 样式单独处理  bug 35054 wangb 20180228
		//火狐浏览器 样式单独处理  bug 35054 xus 18/3/21
		var lockTds = document.querySelectorAll("td[class*='cell_locked2']");
		for(var i=0;i<lockTds.length;i++){
			lockTds[i].className = lockTds[i].className.replace("cell_locked2","");
			lockTds[i].className = lockTds[i].className.replace("common_background_color","");
		}
		//bug 36830 合计行边框不显示问题处理  wangb 20180420
		/*var tbl = document.getElementById('tbl');
		var lasttr =  tbl.getElementsByTagName('tr')[tbl.getElementsByTagName('tr').length-1];
		var lasttrtd1 = lasttr.getElementsByTagName('td')[0];
		lasttrtd1.style.border = '1px #C4D8EE solid';
		lasttrtd1.style.backgroundColor = 'transparent';*/
	}   
}

function comput_ok(outparameters)
{
    alert("计算完成!");
    <% if(request.getParameter("operate")!=null&&request.getParameter("operate").length()>0){ %>
    batchGradeForm.action="/selfservice/performance/batchGrade.do?b_Desc=query&operate=<%=(request.getParameter("operate"))%>";
    <% }else{ %>
    batchGradeForm.action="/selfservice/performance/batchGrade.do?b_Desc=query";
    <% } %>
    batchGradeForm.submit();
}

</script>


