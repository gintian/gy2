<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
                 com.hjsj.hrms.actionform.performance.singleGrade.SingleGradeForm,
                 com.hjsj.hrms.utils.ResourceFactory,
                 com.hrms.struts.constant.SystemConfig" %>
                 
 <%
SingleGradeForm singleGradeForm=(SingleGradeForm)session.getAttribute("singleGradeForm");     

String returnflag=(String)singleGradeForm.getReturnflag();  //8:返回首页  10：返回首页更多列表
String fromModel=(String)singleGradeForm.getFromModel();//从哪里进入的
if(returnflag==null)
	returnflag="";
		
Hashtable paramTable=(Hashtable)singleGradeForm.getParamTable();
String  _KeepDecimal ="1" ; 
if(paramTable!=null)
{
	if(paramTable.get("KeepDecimal")!=null&&((String)paramTable.get("KeepDecimal")).trim().length()>0)
    	_KeepDecimal=(String) paramTable.get("KeepDecimal"); // 小数位
}
String model=singleGradeForm.getModel();
String performanceType=singleGradeForm.getPerformanceType();
String employRecordUrl=singleGradeForm.getEmployRecordUrl();
String PointEvalType=singleGradeForm.getPointEvalType();
String object_id=singleGradeForm.getObject_id();
//是否自动保存 值为时间（0：不自动保存 1： 1分钟  2：2分钟）
	String grading_auto_saving="0";
	if(SystemConfig.getPropertyValue("grading_auto_saving")!=null&&SystemConfig.getPropertyValue("grading_auto_saving").length()>0)
		grading_auto_saving=SystemConfig.getPropertyValue("grading_auto_saving");
    
    String clientName="";
    String url_str="/templates/attestation/unicom/performance.do?b_query=link";
	if(SystemConfig.getPropertyValue("clientName")!=null)
	{
		clientName=SystemConfig.getPropertyValue("clientName").trim();
		if(clientName.equalsIgnoreCase("bjga"))
			url_str="/templates/attestation/unicom/performance.do?b_score=link";
	}
 	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}     
 %>
 

<hrms:themes />
	<link rel="stylesheet" type="text/css" href="../../ext/resources/css/ext-all.css" />
	<script type="text/javascript" src="../../ext/ext-all.js"></script>
	<script type="text/javascript" src="../../ext/ext-lang-zh_CN.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/showModalDialog.js"></script>
<script language="JavaScript" src="/performance/singleGrade/grade.js"></script>
<script language="JavaScript">
var pointIDs="${singleGradeForm.pointIDs}";
var PointEvalType='<%=PointEvalType%>';
var pointContrl="${singleGradeForm.pointContrl}";
var pointContrls=pointContrl.split("/");
var performanceType="${singleGradeForm.performanceType}";	//考核形式   0：绩效考评  1：干部任免
var noGradeItem="${singleGradeForm.noGradeItem}";
var grading_auto_saving='<%=grading_auto_saving%>';	 
var scoreflag="${singleGradeForm.scoreflag}";   //=2混合，=1标度
var DegreeShowType="${singleGradeForm.degreeShowType}";   //1-标准标度 2-指标标度 3-采集标准标度，显示指标标度
var clientName='<%=clientName%>';
var _KeepDecimal="<%=_KeepDecimal%>";    //保留小数位
var dataArea="${singleGradeForm.dataArea}"; ////各指标的数值范围
var old = ""; // smk 上一个等级单元格id

//smk 2015.12.01 输入分值，自动计算等级
function autograde(obj){
	var ScaleToDegreeRule = document.getElementById("ScaleToDegreeRule").value;
		
	var id = obj.id;
	var pscore = parseFloat(obj.value,10);
	var a_pointScore;
	var dengji = document.getElementById(id+"_dj");
	var score=parseFloat(document.getElementById(id+"_df").value,10);
	
	var topgradedesc,topvalue;
	topvalue=0;
	var rank = (pscore/score).toFixed(2);
	for ( var i in per_competencedegree){
		var top_value = per_competencedegree[i].top_value;
		var bottom_value = per_competencedegree[i].bottom_value;
		if (top_value==bottom_value && top_value==rank){
			if (old != "")
				document.getElementById(old).style.color = "black";
			dengji.style.color = "red";
			dengji.innerText=per_competencedegree[i].gradedesc;
			old = id+"_dj";	
			return;
		}else if (ScaleToDegreeRule=="1"){
			if ((top_value>rank)&&(rank>=bottom_value)){
				if (old != "")
					document.getElementById(old).style.color = "black";
				dengji.style.color = "red";
				dengji.innerText=per_competencedegree[i].gradedesc;
				old = id+"_dj";	
				return;
			}
		}else{
			if ((top_value>=rank)&&(rank>bottom_value)){
				if (old != "")
					document.getElementById(old).style.color = "black";
				dengji.style.color = "red";
				dengji.innerText=per_competencedegree[i].gradedesc;
				old = id+"_dj";	
				return;
			}
		}
		
		if (top_value>topvalue){
			topvalue=top_value;
			topgradedesc=per_competencedegree[i].gradedesc;
		}
	}
	if (pscore>=score){
		dengji.innerText=topgradedesc;
	}
}

function check(n,autoScore)///n=1保存  2提交
{
	var dataArea="${singleGradeForm.dataArea}"; ////各指标的数值范围
	var isNull="${singleGradeForm.isNull}";
	var objectIDs="${singleGradeForm.object_id}";
	var mainBodyID="${singleGradeForm.mainBodyId}";
	var templateId="${singleGradeForm.templateId}";
	var object_id=objectIDs.split("/");
	
	var wholeEval="${singleGradeForm.wholeEval}";
	var totalAppFormula="${singleGradeForm.totalAppFormula}";
	var limitation="${singleGradeForm.limitation}";
	var nodeKnowDegree="${singleGradeForm.nodeKnowDegree}";
	var fillCtrl="${singleGradeForm.fillCtrl}";
	var isEntireysub="${singleGradeForm.isEntireysub}";
	var noGradeItem="${singleGradeForm.noGradeItem}";///没有设置上下限值的指标名称
	var EvalOutLimitStdScore="${singleGradeForm.evalOutLimitStdScore}";
	var wholeEvalMode="${singleGradeForm.wholeEvalMode}"; //总体评价采集方式: 0-录入等级，1-录入分值
	var topscore="${singleGradeForm.topscore}";
	var mustFillWholeEval = "${singleGradeForm.mustFillWholeEval}";
	var appitem_id = "${singleGradeForm.appitem_id}";
	
	if(isNull=='1')///指标范围是否为空 0：不为空  1：为空
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
		alert(desc+"\n\r "+BATCHGRADE_INFO1+"！");///该指标的标度没有上下限
		return;
	}
	
	var userValue="";
	if(PointEvalType=='1' && scoreflag==1 && (DegreeShowType=='1' || DegreeShowType=='2' || DegreeShowType=='3'))
	{ ///如果是单选按钮
		var pointScores=dataArea.split("/"); 
		var points=pointIDs.split("/"); 
		var index=0;
		for(var i=0;i<points.length;i++)
		{
			var point_id=points[i];
			if(trim(point_id)=='')
				continue; 
			var obj=document.getElementsByName(point_id); 
			var _value="null";
			var a_pointScore=pointScores[index];
			var aa_pointScore=a_pointScore.split("#"); 
			
			if(obj[0].type=="text")
			{		 
				if(obj[0].value!=''&&obj[0].value!=' '&&obj[0].value!='null'&&obj[0].value!=undefined)
				{
					var temp2=fucNumchk(obj[0].value);   //是否为数字	
					if(temp2==0)
					{
						alert(DI+(index+1)+BATCHGRADE_INFO4+"！");
						obj[0].focus();
						return;
					}
					else
					{
						var singleValue=aa_pointScore[0]; 	
						var a_singleValue=singleValue.split("*"); 
									 
						if(obj[0].type!='hidden'&&obj[0].disabled!=true&&(a_value<a_singleValue[0]*1||a_value>a_singleValue[1]*1))
						{
							alert(DI+(index+1)+BATCHGRADE_INFO6+"！");
							obj[0].focus();
							return;
						}
						else
						{
							_value=obj[0].value.toLocaleUpperCase();				    	
						}									 
					}
				}else
				{
					if(n==2 && obj[0].disabled==false && isEntireysub=='true')
					{					
						alert(BATCHGRADE_INFO2+"！");
						return;					
					}										
				}			
			}
			else
			{
				for(var j=0;j<obj.length;j++)
				{
					if(obj[j].checked)
					{
						_value=obj[j].value.toLocaleUpperCase();
						break;
					}
				}
				
				if(n==2 && _value=='null' && isEntireysub=='true')
				{	
					var isTrue=true;
					for(var j=0;j<obj.length;j++)
					{
						if(obj[j].type=='hidden'||obj[j].disabled==true)
							isTrue=false;
					}
				 	if(isTrue)
				 	{	
						alert(BATCHGRADE_INFO2+"！");
						return;		
					}			
				}										
			}
			index++;
			userValue+="/"+_value;
		} 
	}
	else
	{ ///如果是下拉
	
		var pointScores=dataArea.split("/");
		var object=document.getElementsByName("a"+object_id[0]);
		if(!object.length&&object)
		{
			var temp=object;
			object=new Array();
			object[0]=temp;
		} 
		for(var i=0;i<object.length;i++)
		{
			var a_pointScore=pointScores[i];
			var aa_pointScore=a_pointScore.split("#");
			var a_value=object[i].value;
			var is_over=0;   //值是否超出指标范围
			
			//if(a_value!=''&&a_value!=' '&&a_value!='null'&&a_value!=undefined)
			if(a_value!=null && trimStr(a_value).length>0 && a_value!='undefined')
			{
					
					 var temp1=fucPWDchk(a_value);	//是否为字母
					 var temp2=fucNumchk(a_value);   //是否为数字		  
	
					if(scoreflag=='2')   //混合打分
					{	  		   						
							if(temp1==0&&temp2==0)
							{
								alert(DI+(i+1)+BATCHGRADE_INFO4+"！");
								object[i].focus();
								return;
							}
							else
							{
								var singleValue="";
								if(temp1==1)
								{							   
								    singleValue=aa_pointScore[1]; 
								    var a_singleValue=singleValue.split("*"); 
								    if(a_value.length>1)
								    {
								    	alert(DI+(i+1)+BATCHGRADE_INFO5+"！");
								    	object[i].focus();
								    	return;
								    }
								    else
								    {				
								    		    	
								        if(object[i].type!='hidden'&&object[i].disabled!=true&&a_value.toLocaleUpperCase().charCodeAt(0)<a_singleValue[0].toLocaleUpperCase().charCodeAt(0)  )
								    	{
								    		alert(DI+(i+1)+BATCHGRADE_INFO6+"！");
								    		object[i].focus();
								    		return;
								    	}
								        else
								        {		
								        	userValue+="/"+a_value.toLocaleUpperCase();
								        }							    
								    }							   							
								}
								else if(temp2==1)
								{							  
								    singleValue=aa_pointScore[0]; 	
								 // var a_singleValue=singleValue.split("*"); 
								 	if(!checkNUM2(object[i],10,_KeepDecimal))
								 	{
								 		object[i].value="";
								 		object[i].focus();
								 		return;
								 	}
								    userValue+="/"+a_value.toLocaleUpperCase();				
								}			
							}	
						}
						else
						{
							if(object[i].type=="text")
							{
							
								if(temp2==0)
								{
									alert(DI+(i+1)+BATCHGRADE_INFO4+"！");
									object[i].focus();
									return;
								}
								else
								{
									var singleValue=aa_pointScore[0]; 	
								    var a_singleValue=singleValue.split("*"); 
									if(scoreflag!='4')   
								    {
									    if(object[i].type!='hidden'&&object[i].disabled!=true&&(a_value<a_singleValue[0]*1||a_value>a_singleValue[1]*1))
									    {
									    		alert(DI+(i+1)+BATCHGRADE_INFO6+"！");
									    		object[i].focus();
									    		return;
									    }
									    else
									    {
										    userValue+="/"+a_value.toLocaleUpperCase();				    	
									    }
									 }
									 else
									 	 userValue+="/"+a_value.toLocaleUpperCase();	
								}
							}	
							else
							{
								 userValue+="/"+a_value.toLocaleUpperCase();		
							}				
						}	
			}
			else
			{
				if(n==2&&object[i].type!='hidden'&&object[i].disabled==false&&isEntireysub=='true' && (totalAppFormula==null || totalAppFormula==''))
				{

						alert(BATCHGRADE_INFO2+"！");
						return;
					
				}
				userValue+="/null";
			
			}
		}
	
	} 
	var konwDegrees = document.getElementsByName("konwDegree");
	var konwDegree = null;
	if(konwDegrees && konwDegrees.length>0){
		konwDegree = konwDegrees[0];
	}
	if(konwDegree && konwDegree.disabled==false && nodeKnowDegree=='true' && n==2 && isEntireysub=='true' && document.singleGradeForm.konwDegree.value=='')
	{
		alert(BATCHGRADE_INFO2+"！");
		return;
	}
	
	var wholeEvalScore = document.getElementsByName("wholeEvalScoreId");
	if(wholeEvalScore && wholeEvalScore.disabled==false && wholeEvalMode=='0'&&wholeEval=='true' && n==2 && isEntireysub=='true' && document.singleGradeForm.wholeEval.value=='' && (totalAppFormula==null || totalAppFormula==''))
	{
		alert(BATCHGRADE_INFO2+"！");
		return;
	}
	if(wholeEvalMode=='1'  && document.getElementById("wholeEvalScoreId")!=null){
		var score = document.singleGradeForm.wholeEvalScore.value;
		if(score==null || score==""){
			if(mustFillWholeEval=='True' && n==2){
				alert(WHOLESCOREMUSTFILL);
				return;
			}
			else
				score = 0;
		}
		else{
			var temp2=fucNumchk(score);   //是否为数字
			if(temp2==0){
				alert("请输入数字！");
				return;
			}
			score=parseFloat(score);
			///总体评价录入分值 按计算规则控制小数位控制  zzk 2014/2/11
		 	if(!checkNUM2(document.singleGradeForm.wholeEvalScore,10,_KeepDecimal))
		 	{
		 		document.singleGradeForm.wholeEvalScore.value="";
		 		document.singleGradeForm.wholeEvalScore.focus();
		 		return;
		 	}
			topscore=parseFloat(topscore);
			if(score<0 || score>topscore){
				alert("总体评价的分值必须在0~"+topscore+"之间");
				return;
			}
		}
	}
	
	var appArray = new Array();
	if(performanceType!=null && performanceType=='1')
	{
		var app_ids = appitem_id.split("/");		
		for(var t=0;t<app_ids.length;t++)
		{
			var app_id = app_ids[t];
			var str = $("appItemid"+app_id).value;
			if(str==null || trimStr(str).length<=0)	
				str = 'null';
			
			if((str.indexOf('~')!=-1) || (str.indexOf('&')!=-1) || (str.indexOf('<')!=-1) || (str.indexOf('>')!=-1))
			{
				alert('您填写的内容中存在特殊字符：~&<> 请检查！');
      			return;
			}
			if(trim(str).length>500)
			{
				alert('您填写的某一项答案内容过长，请控制在500字以内！');
      			return;
			}
			
			str=str.replace(/[\r\n]/g,"br");
 			appArray.push(app_id+"~"+str);			
		}		
	}
						
	var hashvo=new ParameterSet();
	hashvo.setValue("object_id",object_id[0]);
	hashvo.setValue("templateId",templateId);
	hashvo.setValue("scoreflag",scoreflag);
	hashvo.setValue("userValue",userValue.substring(1)); 
	hashvo.setValue("plan_id","${singleGradeForm.dbpre}");
	hashvo.setValue("nodeKnowDegree",nodeKnowDegree);
	hashvo.setValue("wholeEval",wholeEval);
	hashvo.setValue("scoreBySumup","${singleGradeForm.scoreBySumup}");
	hashvo.setValue("limitation",limitation);
	hashvo.setValue("gradeClass","${singleGradeForm.gradeClass}");
	hashvo.setValue("status","${singleGradeForm.status}");
	hashvo.setValue("wholeEvalMode",wholeEvalMode);
	hashvo.setValue("appraiseArrayList",appArray);
	 
	if(autoScore)
	{
		hashvo.setValue("autoScore",autoScore);
	}
	if(nodeKnowDegree=='true')
	{
		hashvo.setValue("nodeKnowDegree_value",document.singleGradeForm.konwDegree.value);
	}
	if(wholeEval=='true' && wholeEvalMode=='0')
	{		
		hashvo.setValue("wholeEval_value",document.singleGradeForm.wholeEval.value);
	}
	if(wholeEvalMode='1' && document.getElementById("wholeEvalScoreId")!=null){
		hashvo.setValue("wholeEvalScore",score);
	}
	var In_paramters="flag="+n; 	
	if(n==2)
	{
		if(!confirm(BATCHGRADE_INFO3+"!"))
			return;
	}
	
	if(document.getElementsByName("b_save")&&document.getElementsByName("b_save")[0])
		document.getElementsByName("b_save")[0].disabled=true;
	if(document.getElementsByName("b_refer")&&document.getElementsByName("b_refer")[0])
		document.getElementsByName("b_refer")[0].disabled=true;
	
	
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onFailure:errorInfo,onSuccess:returnInfo,functionId:'90100160004'},hashvo);
	
	
}///check函数 结束


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

function goback()
{	
<%  if(returnflag!=null&&returnflag.equals("8")){
%>
       		if("hcm"=='<%=hcmflag%>'){
 	      		 window.location='/templates/index/hcm_portal.do?b_query=link';      		
       		}else{
 	       		window.location='/templates/index/portal.do?b_query=link';      		
       		}
<%	}else if(returnflag!=null&&returnflag.equals("10")){	%>
		window.location='/general/template/matterList.do?b_query=link';
<%	}else if(fromModel.equals("frontPanel")){ %>
	window.close();
<%  }else{	%>

	window.parent.opener.location="<%=url_str%>";
    window.close();
<%	} %>
}

 function errorInfo(outparamters)
 {
 	if(document.getElementsByName("b_save")&&document.getElementsByName("b_save")[0])
		document.getElementsByName("b_save")[0].disabled=false;
	if(document.getElementsByName("b_refer")&&document.getElementsByName("b_refer")[0])
		document.getElementsByName("b_refer")[0].disabled=false;
 }

function returnInfo(outparamters)
{
	if(document.getElementsByName("b_save")&&document.getElementsByName("b_save")[0])
		document.getElementsByName("b_save")[0].disabled=false;
	if(document.getElementsByName("b_refer")&&document.getElementsByName("b_refer")[0])
		document.getElementsByName("b_refer")[0].disabled=false;
		
	var info=getDecodeStr(outparamters.getValue("info"));
	var score=outparamters.getValue("score");
	var totalAppValue=getDecodeStr(outparamters.getValue("totalAppValue"));
	var autoScore=outparamters.getValue("autoScore");  //是否自动保存  
	var flag=outparamters.getValue("flag");
	var BlankScoreOption=outparamters.getValue("BlankScoreOption"); // 指标未打分时，0 按未打分处理，1 计为最高分，默认值为按未打分处理
	var otherInfo=getDecodeStr(outparamters.getValue("otherInfo"));
	var isRefresh = true;
	if(autoScore=='1')
	{
		info=replaceAll(info,'<br>','\n\r');
		if(info==(SAVESUCCESS+'！'))
		{
			var isShowTotalScore="${singleGradeForm.isShowTotalScore}"
			if(isShowTotalScore=='true')
			{
				var ascore=$('ascore');
				ascore.innerHTML="<font color='blue'>&nbsp;"+score+"</font>";					
			}
			if(totalAppValue!=null && totalAppValue.length>0)
			{
				var totalApp=$('totalAppValue');
				totalApp.innerHTML=totalAppValue;
			}
		}
		else{
			alert(info);
			isRefresh = false;
		}
	}
	else
	{
	
	/*	if(BlankScoreOption=='1')
		{
			document.singleGradeForm.action=document.location;
			document.singleGradeForm.submit();
		}
		else */
		{
			info=replaceAll(info,'<br>','\n\r');
			if(info==(SUBSUCCESS+'！'))
			{
			//	buttons.style.display="none";
				Element.hide('buttons');
				if(flag=='2')
				{
					for(var i=0;i<document.singleGradeForm.elements.length;i++)
					{
						if(document.singleGradeForm.elements[i].type=='checkbox')
							document.singleGradeForm.elements[i].disabled=true;
					}
				}
				clearTimeout(t);
				
				if(clientName=='bjga')
				{
				
					window.opener.location="<%=url_str%>";
					alert(info);
   					window.close();
   					return;
				}
				
			}else{
				alert(info);
				isRefresh = false;
			}
			var isShowTotalScore="${singleGradeForm.isShowTotalScore}"
			if(isShowTotalScore=='true')
			{
				var ascore=$('ascore');
				ascore.innerHTML="<font color='blue'>"+score+"</font>";				
			}
			if(totalAppValue!=null && totalAppValue.length>0)
			{
				var totalApp=$('totalAppValue');
				totalApp.innerHTML=totalAppValue;
			}
		}			
	}
	if(flag=='2'&&otherInfo.length>0)
	{
		 
			otherInfo=FRIENDINFO+"："+otherInfo;
			otherInfo=replaceAll(otherInfo,'#','\n\r');
			alert(otherInfo); 
	}
	if((flag=='2'||flag=='1') && isRefresh){// 提交刷新页面 zzk
		/* document.singleGradeForm.action="/selfservice/performance/singleGrade.do?b_query=link&fromModel=menu&model=0";
		document.singleGradeForm.submit(); */
		window.location.href = window.location.href;
		
	}
}




 function openWin(url)
 {
 	// window.open(url,'aa','fullscreen');  
       window.open(url,'aa');  
 }
 var t=null;

 function autoScore()
 {
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
   			    <logic:notEqual name="singleGradeForm" property="objectStatus" value="2">
				<logic:notEqual name="singleGradeForm" property="objectStatus" value="7">
				<logic:notEqual name="singleGradeForm" property="objectStatus" value="4">
   				t=window.setInterval("check('1','1')",autoType);
   				</logic:notEqual>	
   				</logic:notEqual>	 
   				</logic:notEqual>
   				
   				
   			}
   		}
 
 }

 //显示员工日志
 function showWordDiary(plan_id,a0100,startDate,endDate)
 {
 		 var _url="/performance/workdiary/workdiaryshow.do?b_query=link&timeflag=1&logo=1&plan_id="+plan_id+"&a0100="+a0100+"&start_date="+startDate+"&end_date="+endDate;
		 var _width=window.screen.width-200;
		 window.open(_url,null,"width="+_width+",height=700,left=50,top=50,resizable=no,status=no,toolbar=no,scrollbars=yes,menubar=no,location=no"); 
 
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
		style.posLeft=pos[0];
		style.posTop=pos[1]-date_desc.offsetHeight+42;
		style.width=(date_desc.offsetWidth<20)?150:date_desc.offsetWidth+1;
      }
      var plan_id = "${singleGradeForm.dbpre}";
      var mainBodyID = "${singleGradeForm.mainBodyId}";
      var hashVo = new ParameterSet();
      hashVo.setValue("name",getEncodeStr(document.getElementById('selectname').value));
      hashVo.setValue("plan_id",plan_id);
      hashVo.setValue("mainBodyID",mainBodyID);
      hashVo.setValue("model","2");
      
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
			AjaxBind.bind(singleGradeForm.contenttype,namelist);
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
		    	var a_object=eval("singleGradeForm.searchname");	
	            a_object.value=temps.options[i].value;
		    	Element.hide('date_panel2');	
		    }
		}
		document.singleGradeForm.action="/selfservice/performance/singleGrade.do?b_query2=b_query2&typeflag=1";	
	    document.singleGradeForm.submit();
	}
/** 查询 */
	function query()
	{
		if(document.getElementById('selectname').value.length==0)
		{
			alert("请输入姓名信息!");
			return;
		}
		document.singleGradeForm.action="/selfservice/performance/singleGrade.do?b_query2=b_query2&typeflag=1";	
	    document.singleGradeForm.submit();
	}		
	
	
	/** 禁用鼠标滚轮 **/
	function stop_onmousewheel(){
		for(var i=0;i<document.singleGradeForm.getElementsByTagName('select').length;i++){
			document.singleGradeForm.getElementsByTagName('select')[i].onmousewheel = function (){
			return false;}
		}
	}
	
	function historyScore()
	{
		var planid = "${singleGradeForm.dbpre}";
		//var theurl="/selfservice/performance/batchGrade.do?b_historyScore=link`flag=2`planid="+planid;
	    //var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
	   	//var return_vo= window.showModalDialog(iframe_url, 'template_win', 
	   //   				"dialogWidth:550px; dialogHeight:450px;resizable:no;center:yes;scroll:yes;status:yes");
	   	var theurl="/selfservice/performance/batchGrade.do?b_historyScore=link&flag=2&planid="+planid;
		var iTop = (window.screen.availHeight-400)/2; //获得窗口的垂直位置;
		var iLeft = (window.screen.availWidth-580)/2; //获得窗口的水平位置;
		var params="height=450,width=550,top="+iTop+",left="+iLeft+",Resizable=no,scrollbars=no,toolbar=no,menubar=no,location=no,status=no";
	   	window.open(theurl,'',params);
	}
</script>


<style>

.ListTable_self {
    BACKGROUND-COLOR: #FFFFFF;
    border: inset 1px #94B6E6;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
    
 }   


</style>

<BODY onload='autoScore();stop_onmousewheel()'  >

<html:form action="/selfservice/performance/singleGrade">
<table cellpadding="0" cellspacing="0" border="0"><tr><td height="30px;" valign="middle">
<html:hidden name="singleGradeForm" property="searchname" />
<%if("hl".equals(hcmflag)){ %>
<%} %>
<bean:message key="lable.performance.perPlan"/><!-- 考核计划 -->
     	
     	<hrms:optioncollection name="singleGradeForm" property="dblist" collection="list"   />
             <html:select name="singleGradeForm" property="dbpre" size="1" onchange="excecuteGradeObject()"  >
             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
        </html:select>
</td><tr><td valign="middle" height="30px;">

<bean:message key="lable.appraisemutual.examineobject"/><!-- 考核对象 -->
<%if("hcm".equals(hcmflag)){ %><!-- 【6153】单人考评，界面错位   jingq upd 2014.12.22 -->
<div style="position:absolute;z-index:1;width:137px;left:58px;top:45px;"> 
<%}else{ %> 
<div style="position:absolute;z-index:1;width:137px;left:58px;top:37px;"> 
<%} %>
<hrms:optioncollection name="singleGradeForm" property="objectList" collection="list" />
             <html:select styleId="objectbox" name="singleGradeForm" property="object_id" size="1" onchange="executeGradeHtml()">
             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
        </html:select> 
       
</div>
<%if("hcm".equals(hcmflag)){ %>
<div style="position:absolute;top:45px;width:450px;height:22px;left:200px;">  
<%}else{ %>  
<div style="position:absolute;top:37px;width:450px;height:22px;left:200px;"> 
<%} %>
<%-- 
<input type="button" class="mybutton" value="查找" onclick="query()"/>
&nbsp;&nbsp;
--%>
<div style="position:absolute;top:0px;">
&nbsp;&nbsp;
<logic:equal name="singleGradeForm" property="showHistoryScore" value="True">
	<input type="button" class="mybutton" value="历次得分表" onclick="historyScore()"/>
</logic:equal>&nbsp;&nbsp;
${singleGradeForm.targetDeclare}&nbsp;&nbsp;
${singleGradeForm.individualPerformance}&nbsp;&nbsp;

<% if(employRecordUrl!=null&&employRecordUrl.trim().length()>0){
		out.print(employRecordUrl);
   }
 %>
<logic:equal  name="singleGradeForm" property="noteIdioGoal"  value="true">
 <% if(model==null||(!model.equals("2")&&!model.equals("4"))){ %>
  		${singleGradeForm.goalComment}&nbsp;&nbsp;
  <% } %>
</logic:equal>
<logic:equal  name="singleGradeForm" property="model"  value="1">
<logic:equal  name="singleGradeForm" property="optObject"  value="2">
	${singleGradeForm.personalComment}&nbsp;&nbsp;
</logic:equal>
</logic:equal>
<logic:equal  name="singleGradeForm" property="model"  value="0">
	${singleGradeForm.personalComment}&nbsp;&nbsp;
</logic:equal>

<% if(model.equals("3")) {%>
	${singleGradeForm.personalComment}&nbsp;&nbsp;
<% } %>
   </div>  
</div>


<span id='title'>
</span>
</td></tr><tr><td>
<table border='0' style="margin-left:-3px;"><tr><td id="datatd">
${singleGradeForm.gradeHtml}
</td></tr></table>
<script>
//调整table边线，解决粗细不一（每一行最右边设置border为0）
    var tables = document.getElementById("datatd").getElementsByTagName("table");
   	//解决google下表格右侧没有边框的问题
	if(tables.length>0 && getBrowseVersion()){
	    var table = tables[0];
	    for(var i=0;i<table.rows.length;i++){
	    	var row = table.rows[i];
	    	var cell = row.cells[row.cells.length-1];
	    	cell.style.borderRightWidth="0px";
	    }
    }
</script>
<div id="date_panel2" style="display:none;">

		<select id="date_box" name="contenttype"  onblur="Element.hide('date_panel2');"  multiple="multiple"  style="width:138" size="6" ondblclick="setSelectValue();" onmousewheel="return false" >
        </select>
</div>
<div id="date_panel">
   			
</div>
<div id="nlsz_panel">
   			
</div>
<input type="hidden" name="titleName" />
</td></tr></table>
</html:form>
</BODY>

<script language="javascript">

var combo = Ext.create('Ext.form.field.ComboBox',
	{
		emptyText:'请选择',
		mode:'local',
		triggerAction:'all',
		transform:'objectbox',
		valueField:'iddd',
//		matchFieldWidth:false,
		hiddenName:'object_id'	
	}
);
combo.on('select',function(comboBox){
	var a_object=eval("singleGradeForm.titleName");	
	a_object.value=comboBox.getValue();
	document.singleGradeForm.action="/selfservice/performance/singleGrade.do?b_query2=b_query2";	
	document.singleGradeForm.submit();
}
);
    
   Element.hide('date_panel');
   var lay="${singleGradeForm.lay}";
   
   
   
   
   
   window.status="";
   
   function hidden()
   {
   	Element.hide('date_panel');
   }
   
   <% if(request.getParameter("b_query1")!=null&&request.getParameter("b_query1").equalsIgnoreCase("b_query1")){  %>
   executeGradeHtml();
/**   if(document.getElementsByName("object_id")!=null&&document.getElementsByName("object_id")[0].options.length>1)
   {	
   		
   		var bool=false;
   		var objectid='<%=object_id%>';///zzk 提交刷新修改
   		for(i=0;i<document.getElementsByName("object_id")[0].options.length;i++){
   			if(bool||objectid=='0')
			continue;
   			var value=document.getElementsByName("object_id")[0].options[i].value;
			if(objectid.substring(0,objectid.indexOf('/'))==value.substring(0,value.indexOf('/'))){
				document.getElementsByName("object_id")[0].options[i].selected=true;
				bool=true;
			}
   		}
   		if(!bool){
   			document.getElementsByName("object_id")[0].options[1].selected=true;
   		}
   		//document.getElementsByName("object_id")[0].fireEvent("onChange");
   		var t = document.getElementsByName("object_id")[0];
   		 if(   document.all   )  
		  {  
		          t.fireEvent(   "onchange"   );  
		  }  
		  else  
		  {  
		          var   evt   =   document.createEvent('HTMLEvents');  
		          evt.initEvent('change',true,true);  
		          t.dispatchEvent(   evt   );  
		  }
   }*/
   
   <% }  %>
/**   if(document.getElementsByName("object_id")!=null&&document.getElementsByName("object_id")[0].options.length>1)
   {	
        var temps = document.getElementById("objectbox");
        for(i=0;i<temps.options.length;i++)
		{
		   
	        if(temps.options[i].selected)
	        { 
		    	document.getElementById("selectname").value=temps.options[i].text;
		    	var a_object=eval("singleGradeForm.searchname");
	            a_object.value = temps.options[i].value;
	            Element.hide('date_panel2');
		    }
		}
   }*/
   		var aa=document.getElementsByTagName("input");
		for(var i=0;i<aa.length;i++){
			if(aa[i].type=="text"){
				aa[i].className="inputtext";
			}
		}
	
	<%
	ArrayList appContantList = singleGradeForm.getAppContantList();
	if(performanceType!=null && performanceType.equalsIgnoreCase("1"))
	{
		for(int p=0;p<appContantList.size();p++)
		{
			String eassy = (String)appContantList.get(p);
			String [] eassyArray = eassy.split("`");
	%>
		var eassy_point = "appItemid<%=eassyArray[0] %>";
		var eassy_desc = "<%=eassyArray[1] %>";
		eassy_desc=eassy_desc.replace(/br/g,"\r\n");
		document.getElementById(eassy_point).value=eassy_desc;
	<%}}%>
	
	
	//此处导致表格头变口残缺  haosl 2018-2-26
	/* if(!getBrowseVersion()){//兼容非IE浏览器  页面样式问题   wangb 20171207 
		var RecordRow = document.getElementsByClassName('RecordRow')[0]; //chrome下 表格有边框不显示  colspan 属性值 设置大了  
		RecordRow.setAttribute('colspan','3');
		var wholeEval = document.getElementsByName('wholeEval')[0]; //chrome  select 下拉框 太靠下
		if(wholeEval)
			wholeEval.style.marginBottom = '5px';
	} */
</script>
