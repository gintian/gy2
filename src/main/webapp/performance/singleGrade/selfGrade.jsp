 <%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="com.hrms.hjsj.sys.ResourceFactory"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ taglib uri="/WEB-INF/tlds/FCKeditor.tld" prefix="fck"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="java.util.*,
                 com.hjsj.hrms.actionform.performance.singleGrade.SingleGradeForm
                 " %>
<%
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	  userView.getHm().put("fckeditorAccessTime", new Date().getTime());
	}
String tabsestHeight = "";
if(request.getParameter("tabsetHeight")!=null){
	tabsestHeight = (String)request.getParameter("tabsetHeight");
}
SingleGradeForm singleGradeForm=(SingleGradeForm)session.getAttribute("singleGradeForm");
String isnullAffix=singleGradeForm.getIsnullAffix();
Hashtable paramTable=(Hashtable)singleGradeForm.getParamTable();
String  _KeepDecimal ="1" ; 
String SummaryFlag="True";
if(paramTable!=null)
{
	if(paramTable.get("KeepDecimal")!=null&&((String)paramTable.get("KeepDecimal")).trim().length()>0)
    	_KeepDecimal=(String) paramTable.get("KeepDecimal"); // 小数位
    if(paramTable.get("SummaryFlag")!=null)
	    SummaryFlag=((String)paramTable.get("SummaryFlag")).toLowerCase();     //个人总结报告
}

String relatingTargetCard=singleGradeForm.getRelatingTargetCard();
String g_rejectCause=singleGradeForm.getG_rejectCause();
String s_rejectCause=singleGradeForm.getS_rejectCause();
String performanceType=singleGradeForm.getPerformanceType();
String employRecordUrl=singleGradeForm.getEmployRecordUrl();
String PointEvalType=singleGradeForm.getPointEvalType();
String errMsg=singleGradeForm.getErrorMsg();
//是否自动保存 值为时间（0：不自动保存 1： 1分钟  2：2分钟）
	String grading_auto_saving="0";
	if(SystemConfig.getPropertyValue("grading_auto_saving")!=null&&SystemConfig.getPropertyValue("grading_auto_saving").length()>0)
		grading_auto_saving=SystemConfig.getPropertyValue("grading_auto_saving");


String file_max_size="512";
if(SystemConfig.getPropertyValue("appendix_size")!=null&&SystemConfig.getPropertyValue("appendix_size").trim().length()>0)
{
	file_max_size=SystemConfig.getPropertyValue("appendix_size").trim();
	if(file_max_size.toLowerCase().indexOf("k")!=-1)
	file_max_size=file_max_size.substring(0,file_max_size.length()-1);
}


String title="";
String title2=ResourceFactory.getProperty("lable.performance.perGoal");
if(performanceType.equals("0"))
	title=ResourceFactory.getProperty("lable.performance.perSummary");;
if(performanceType.equals("1"))
	title=ResourceFactory.getProperty("lable.performance.personalReport");;
String goalstate=singleGradeForm.getGoalState()!=null?singleGradeForm.getGoalState():"0";
String goaldesc=ResourceFactory.getProperty("performance.singlegrade.haveNotSubmit");
if(goalstate.equals("1"))
	goaldesc=ResourceFactory.getProperty("performance.singlegrade.haveSubmit");
else if(goalstate.equals("2"))
	goaldesc=ResourceFactory.getProperty("performance.singlegrade.haveApprove");
else if(goalstate.equals("3"))
	goaldesc=ResourceFactory.getProperty("performance.singlegrade.reject");
title2+="("+goaldesc+")";	
String summarystate=singleGradeForm.getSummaryState()!=null?singleGradeForm.getSummaryState():"0";
String summarydesc=ResourceFactory.getProperty("performance.singlegrade.haveNotSubmit");
if(summarystate.equals("1"))
	summarydesc=ResourceFactory.getProperty("performance.singlegrade.haveSubmit");
else if(summarystate.equals("2"))
	summarydesc=ResourceFactory.getProperty("performance.singlegrade.haveApprove");
else if(summarystate.equals("3"))
	summarydesc=ResourceFactory.getProperty("performance.singlegrade.reject");	
title+="("+summarydesc+")";
%>
<html>

<hrms:themes />
 <style>

.TEXT_NB {
	BACKGROUND-COLOR:transparent;
	
	BORDER-BOTTOM: #94B6E6 1pt solid;
	BORDER-LEFT: medium none; 
	BORDER-RIGHT: medium none; 
	BORDER-TOP: medium none;
}

</style>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/pergrade.js"></script>
<script type="text/javascript" src="/fckeditor/fckeditor.js"></script>
<script language="JavaScript" src="/performance/singleGrade/selfGrade.js"></script>
<script language="JavaScript"src="../../../js/showModalDialog.js"></script>
<script language="JavaScript">
var summarystateJs = "<%=summarystate%>";
var pointIDs="${singleGradeForm.pointIDs}";
var dataArea="${singleGradeForm.dataArea}"; //各指标的数值范围
var scoreflag="${singleGradeForm.scoreflag}";   //=2混合，=1标度
var DegreeShowType="${singleGradeForm.degreeShowType}";   //1-标准标度 2-指标标度 3-采集标准标度，显示指标标度
var PointEvalType='<%=PointEvalType%>';
var performanceType="${singleGradeForm.performanceType}";	//考核形式   0：绩效考评  1：干部任免
var pointContrl="${singleGradeForm.pointContrl}";
var pointContrls=pointContrl.split("/");
var  _photo_maxsize="<%=(file_max_size)%>"   
var grading_auto_saving='<%=grading_auto_saving%>';
var errMsg='<%=errMsg%>'
    if(errMsg!='')
    	alert(errMsg);

<% 
	String goalFileSize="1";
	if(singleGradeForm.getGoalFileIdsList()==null||singleGradeForm.getGoalFileIdsList().size()==0)
		goalFileSize="0";
	String summaryFileSize="1";
	if((singleGradeForm.getSummaryFileIdsList()==null||singleGradeForm.getSummaryFileIdsList().size()==0)&&"true".equalsIgnoreCase(singleGradeForm.getAllowUploadFile()))
		summaryFileSize="0";	
%>


var goalFileSize=<%=goalFileSize%>;
var summaryFileSize=<%=summaryFileSize%>;
var _KeepDecimal="<%=_KeepDecimal%>";    //保留小数位
var old = ""; // smk 上一个等级单元格id
function secondPage()
{
	var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
	var isIE = userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1; //判断是否IE<11浏览器
	if(!isIE){
		if($('#tabset_pageset')){
			$('#tabset_pageset').tabs('select',1);
		}
	}else{
		var obj=$('pageset');
		if(obj){
			obj.setSelectedTab("tab1");
		}
	}
}
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

function validateSize(opt)
{
		  var fileurl;
          var f_obj;
          if(opt==1)
          {
	          f_obj=document.getElementsByName("file");
	          fileurl=document.singleGradeForm.file.value;
	      }
    	  else if(opt==4)
    	  {
    	      f_obj=document.getElementsByName("goalfile");
    	      fileurl=document.singleGradeForm.goalfile.value;
    	  }
          if(f_obj)
          {
          	// 防止上传漏洞
			var isRightPath = validateUploadFilePath(fileurl);
			if(!isRightPath)	
				return false;
          
          /*
             var value=f_obj[0].value;            
             var photoEx=value.substring(value.lastIndexOf(".")); 
             photoEx=photoEx.toLowerCase();
             var  obj=document.getElementById('FileView'); 
             if (obj != null)
             {
                obj.SetFileName(value);
                var facSize=obj.GetFileSize();                  
                var  photo_maxsize=_photo_maxsize;   
                if(parseInt(photo_maxsize,10)>0&&parseInt(photo_maxsize,10)<parseInt(facSize,10)/1024)
                {  
                   
                   alert("上传文件大小超过管理员定义大小，请修正！上传文件上限"+photo_maxsize+"KB");
                   return false;
                }     
             }
             */
          }
          return true;
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

function check(n,autoScore)
{
	var dataArea="${singleGradeForm.dataArea}";
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
	var noGradeItem="${singleGradeForm.noGradeItem}";
	var isEntireysub="${singleGradeForm.isEntireysub}";
	var EvalOutLimitStdScore="${singleGradeForm.evalOutLimitStdScore}";
	var wholeEvalMode="${singleGradeForm.wholeEvalMode}";
	var topscore="${singleGradeForm.topscore}";
	var mustFillWholeEval = "${singleGradeForm.mustFillWholeEval}";
	var appitem_id = "${singleGradeForm.appitem_id}";
	
	if(isNull=='1')
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
		alert(desc+"\n\r "+BATCHGRADE_INFO1+"！");
		return;
	}
	
/*	var userValue="";
	if(PointEvalType=='1' && scoreflag==1)
	{
		var points=pointIDs.split("/"); 
		for(var i=0;i<points.length;i++)
		{
			var point_id=points[i];
			if(trim(point_id)=='')
				continue; 
			var obj=document.getElementsByName(point_id); 
			var _value="null";
			for(var j=0;j<obj.length;j++)
			{
				if(obj[j].checked)
				{
					_value=obj[j].value.toLocaleUpperCase();
					break;
				}
			} 
			userValue+="/"+_value;
		} 
	}
*/	
	// JinChunhai 2011.06.13
	var userValue="";
	if(PointEvalType=='1' && scoreflag==1 && (DegreeShowType=='1' || DegreeShowType=='2' || DegreeShowType=='3'))
	{
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
	{
	
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
								    var a_singleValue=singleValue.split("*"); 
							/*	    alert(a_singleValue[0]+"   "+a_value+"      "+a_singleValue[1]);
								    if(object[i].type!='hidden'&&object[i].disabled!=true&&(a_value<a_singleValue[0]*1||a_value>a_singleValue[1]*1))
								    {
								    	if(EvalOutLimitStdScore=='false')
								    	{
								    		alert(DI+(i+1)+BATCHGRADE_INFO6+"！");
								    		object[i].focus();
								    		return;
								    	}
								    	if(EvalOutLimitStdScore=='true')
								    		userValue+="/"+a_value.toLocaleUpperCase();
								    }
								    else*/
								    {
				
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
									if(scoreflag!='4')   
								    {							
										var singleValue=aa_pointScore[0]; 	
									    var a_singleValue=singleValue.split("*"); 
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
	

	
	if(nodeKnowDegree=='true' && isEntireysub=='true' && n==2 && document.singleGradeForm.konwDegree.value=='')
	{
		alert(BATCHGRADE_INFO2+"！");
		return;
	}
	

	if(wholeEvalMode=='0'&&wholeEval=='true'&&isEntireysub=='true'&&n==2&&document.singleGradeForm.wholeEval.value=='' && (totalAppFormula==null || totalAppFormula==''))
	{
		alert(BATCHGRADE_INFO2+"！");
		return;
	}
	
	if(wholeEvalMode=='1' && document.getElementById("wholeEvalScoreId")!=null){
		var score = document.singleGradeForm.wholeEvalScore.value;
		if(score==null || score==""){
			if(mustFillWholeEval=='True'){
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
	if(autoScore)
	{
		hashvo.setValue("autoScore",autoScore);
	}
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
	hashvo.setValue("appraiseArrayList",appArray);
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
	
	
}

 //显示员工日志
 function showWordDiary(plan_id,a0100,startDate,endDate)
 {
 		 var _url="/performance/workdiary/workdiaryshow.do?b_query=link&timeflag=1&logo=1&plan_id="+plan_id+"&a0100="+a0100+"&start_date="+startDate+"&end_date="+endDate;
		 var _width=window.screen.width-200;
		 window.open(_url,null,"width="+_width+",height=700,left=50,top=50,resizable=no,status=no,toolbar=no,scrollbars=yes,menubar=no,location=no"); 
 
 }

 function openWin(url)
 {
 	// window.open(url,'aa','fullscreen');  
 	 window.open(url,'aa');
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
	var flag=outparamters.getValue("flag");
	var BlankScoreOption=outparamters.getValue("BlankScoreOption"); // 指标未打分时，0 按未打分处理，1 计为最高分，默认值为按未打分处理

    var autoScore=outparamters.getValue("autoScore");  //是否自动保存  
    
    
    if(autoScore=='1')
	{
		info=replaceAll(info,'<br>','\n\r');
		if(info==(SAVESUCCESS+'！'))
		{
			var isShowTotalScore="${singleGradeForm.isShowTotalScore}"
			if(isShowTotalScore=='true')
			{
				var ascore=$('ascore');
				ascore.innerHTML="<font color='blue'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+score+"</font>";				
			}
			if(totalAppValue!=null && totalAppValue.length>0)
			{
				var totalApp=$('totalAppValue');
				totalApp.innerHTML=totalAppValue;
			}
		}
		else
			alert(info);
	}
	else
	{
    
		if(BlankScoreOption=='1')
		{
			document.singleGradeForm.action=document.location;
			document.singleGradeForm.submit();
		}
		else
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
					var whole_scoreId = document.getElementById("wholeEvalScoreId");
					if(whole_scoreId!=null){
						whole_scoreId.disabled=true;
					}
				}
				clearTimeout(t);
			}
			var isShowTotalScore="${singleGradeForm.isShowTotalScore}"
			if(isShowTotalScore=='true')
			{
				var ascore=$('ascore');
				ascore.innerHTML="<font color='blue'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+score+"</font>";				
			}
			if(totalAppValue!=null && totalAppValue.length>0)
			{
				var totalApp=$('totalAppValue');
				totalApp.innerHTML=totalAppValue;
			}
			}
		}
		if(info==(SUBSUCCESS+'！')){
			window.location="/selfservice/performance/selfGrade.do?b_query=link&bint=int&model=0";
		} else {
			alert(info);
		}
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
		//var theurl="/selfservice/performance/batchGrade.do?b_historyScore=link`flag=1`planid="+planid;
	    //var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
	   	//var return_vo= window.showModalDialog(iframe_url, 'template_win', 
	    //  				"dialogWidth:550px; dialogHeight:450px;resizable:no;center:yes;scroll:yes;status:yes");
		var theurl="/selfservice/performance/batchGrade.do?b_historyScore=link&flag=1&planid="+planid;
		var iTop = (window.screen.availHeight-400)/2; //获得窗口的垂直位置;
		var iLeft = (window.screen.availWidth-580)/2; //获得窗口的水平位置;
		var params="height=450,width=550,top="+iTop+",left="+iLeft+",Resizable=no,scrollbars=no,toolbar=no,menubar=no,location=no,status=no";
	   	window.open(theurl,'',params);
	}
	
	
	/**author:zangxj *day:2014-06-07 *绩效模板文件下载按钮 */
	function downAffix(){
		var hashvo=new ParameterSet();
		hashvo.setValue("opt","down");
		hashvo.setValue("plan_id","${singleGradeForm.dbpre}");
		var request=new Request({method:'post',asynchronous:false,onSuccess:showFieldList,functionId:'90100160022'},hashvo);
	}	
	function showFieldList(outparamters){
		var outName=outparamters.getValue("outname");
		var isnullAffix=outparamters.getValue("isnullAffix");
		if(isnullAffix=="null"){
		alert("没有上传模板");
			return;
		}
		if(outName!=null&&outName.length>1)
			window.open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName);
	}
</script>


<style>

.ListTable_self {

    BACKGROUND-COLOR: #FFFFFF;
    border: solid 1px #94B6E6;
	/* BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid; */
 }   
/* 【4456】干部考察/民主评议/自我评价，线条太粗了  jingq add 2015.01.07 */
/* .TableRow_2rows{
	border-right:none;
}
.RecordRow{
	border-right:none;
} */
.TableRow_2rows{
	border-color:#C4D8EE;
}
</style>



<body  <%=(request.getParameter("bint")!=null?"onload='executeGradeHtml2();stop_onmousewheel()'":"onload='secondPage();stop_onmousewheel()'"  )%>   >

<form name="singleGradeForm" method="post" action="/selfservice/performance/selfGrade.do" enctype="multipart/form-data" >
<%if("hl".equals(hcmflag)){ %>
<br>
<%} %>
<bean:message key="lable.performance.perPlan"/>：    	
	     	<hrms:optioncollection name="singleGradeForm" property="dblist" collection="list"   />
	             <html:select name="singleGradeForm"  property="dbpre" size="1" onchange="executeGradeHtml()"  >
	             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
	        </html:select>
&nbsp;&nbsp;
<logic:equal name="singleGradeForm" property="showHistoryScore" value="True">
	<input type="button" class="mybutton" value="历次得分表" onclick="historyScore()"/>
</logic:equal>
${singleGradeForm.targetDeclare} &nbsp; &nbsp;
${singleGradeForm.individualPerformance}&nbsp; &nbsp;

<% if(employRecordUrl!=null&&employRecordUrl.trim().length()>0){
		out.print(employRecordUrl + "&nbsp;&nbsp;");
   }
 %>
<logic:notEqual  name="singleGradeForm"  property="dbpre"  value="0">
	<logic:notEqual  name="singleGradeForm"  property="isSelfMark"  value="0">
		
		<logic:equal  name="singleGradeForm"  property="noteIdioGoal"  value="true">
			<% if(relatingTargetCard.equalsIgnoreCase("True") || relatingTargetCard.equalsIgnoreCase("2")){ %>
				${singleGradeForm.goalComment}
			<% }else{ %>
			<a href="/selfservice/performance/selfGrade.do?b_querySummary=link&optUrl=goal">
			<bean:message key="lable.performance.perGoal"/>
			</a>
			<% } %>
		</logic:equal>
		
		<% if(SummaryFlag.equalsIgnoreCase("True")){ %>
		&nbsp;&nbsp;<a href="/selfservice/performance/selfGrade.do?b_querySummary=link&optUrl=summary">
		    <logic:equal name="singleGradeForm" property="performanceType" value="0">
		    
		        <%
	       		    String info=SystemConfig.getPropertyValue("per_examineInfo");
					if(info==null||info.length()==0)
					{			 
					%>
					<bean:message key="lable.performance.perSummary"/>
					<%
					}		
					else
						out.print(info);
								 
	       		%>
       			
       			
			</logic:equal>
			<logic:equal name="singleGradeForm" property="performanceType" value="1">
				<bean:message key="lable.performance.personalReport"/>
			</logic:equal>
		</a>
		<% } %>
	</logic:notEqual>
</logic:notEqual>

<input type='hidden' name='object_id' value="${singleGradeForm.object_id}" >

&nbsp;&nbsp;&nbsp;     

<table border='0' style="margin-left:-3px;"><tr><td>
${singleGradeForm.gradeHtml}
</td></tr></table>

<input type="hidden" name="titleName" />
<div id="date_panel">

</div>
<div id="nlsz_panel">
   			
</div>
<logic:equal  name="singleGradeForm"  property="isSelfMark"  value="0">
<table  align='left'><tr><td>&nbsp;</td><td>


		 <hrms:tabset name="pageset" width="600" height="<%=tabsestHeight%>" type="false">
		<logic:equal  name="singleGradeForm"  property="noteIdioGoal"  value="true">
		
		<hrms:tab name="tab2" label="<%=title2%>" visible="true">
       		<table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">     
                      <tr class="list3">
	                	  <td align="left" nowrap >
	                 	     <html:textarea name="singleGradeForm" property="goalContext"   style="margin:5px 0 0 3px;"    cols="80" rows="22"/>
	                      </td>
                      </tr>
                    
                    <tr class="list3">
                	  	 <td align="left"  valign='top' nowrap >
                	  	 
                	  	 <table   width='100%' ><tr><Td width='40%' valign='top' >
                	  	 
                 	     	<table border=0 ><tr><td valign='top'>
                 	   		  &nbsp;<bean:message key="label.zp_employ.uploadfile"/>：
                 	   		  </td></tr>
                 	   		  <tr>
                 	   		  <td valign='top' >
                 	   		  
                 	   		   <table border="0" cellpmoding="0" cellspacing="0" cellpadding="0"  >
                 	   		   <logic:iterate id="element" name="singleGradeForm" property="goalFileIdsList" >
                 	   		   <tr><td>&nbsp;&nbsp;
                 	   		    <a href='/servlet/performance/fileDownLoad?article_id=<bean:write name="element" property="id" />'  target="_blank"  border='0' >  
                 	   			 <bean:write name="element" property="name" />
                 	   		  	</a>
                 	   		  	</td><td>
                 	   		  	<% if(goalstate.equals("0")||goalstate.equals("3")){ %>
                 	   		 &nbsp;<a href="javascript:del(6,<bean:write name="element" property="id" />,${singleGradeForm.dbpre})">
                 	   		  		<image src='/images/del.gif' border=0 title='<bean:message key="label.zp_employ.deletefile"/>' >
                 	   		  	</a>
                 	   		  	<% } %>
								</td></tr>
                 	   		  </logic:iterate>
                 	   		  </table>
                 	   		  
                 	     	  </td>
                 	     	  </tr></table>
                 	     	  
                 	     	  </td><td algin='right' width='60%' >
                 	     	  <% if(g_rejectCause!=null&&g_rejectCause.trim().length()>0){ %>
									<bean:message key="performance.singlegrade.objectRejectReason"/>:<br>
									<html:textarea  name="singleGradeForm"  cols="45" rows="5"  readonly="true"  property="g_rejectCause"></html:textarea>
								
								<% } %>
                 	     	  </td></tr></table>
                 	     	  
                 	     	  
                          </td>
                          
             	  </tr>
                 	<% if(goalstate.equals("0")||goalstate.equals("3")){ %> 
	             <tr class="list3">
	                	  	 <td align="left" nowrap ><Br>
	                	  	<fieldset align="center" style="width:98%;">
    							 <legend><bean:message key="label.zp_employ.uploadfileInfo"/><%=(file_max_size)%>K</legend>
	                	  	
	                 	 	   &nbsp;<bean:message key="column.law_base.filename"/>:<input type='text' style='border:1pt solid #C4D8EE !important;margin-bottom:1px;' maxLength=30  class='TEXT_NB' size='20'     name='goalfileName' />
	                 	 	   <Br>&nbsp;&nbsp;<input name="goalfile"   onchange='upload(4)'  type="file" size="40">
	                 	    	
                 	    	    <br>&nbsp;
                 	    	    
                 	    	  </fieldset>  
	                          </td>
	              </tr>	
	              <% } %>
	              <tr>
                 	<td><Br>&nbsp;
                 		<% if(goalstate.equals("0")||goalstate.equals("3")){ %>
                    	<html:button styleClass="mybutton" style="margin-bottom:2px;" property="br_home" onclick="upload(5)">
		            		<bean:message key="button.save"/>
						</html:button>
						<html:button styleClass="mybutton" style="margin-bottom:2px;" property="br_home" onclick="upload(8)">
		            		<bean:message key="button.submit"/>
						</html:button>
						<%-- bug 36723 and 36722 按钮与边框线重叠 添加br换行与上面对齐   wangb 20180417 --%>
						<Br>
						<% } %>
                    </td>
                  </tr>
                    
                 </table>     
              
		</hrms:tab> 
		
		</logic:equal>
		
		<hrms:tab name="tab1" label="<%=title%>" visible="true">
       
       
               <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">     
                      <tr class="list3">
	                	  <td align="left" nowrap >
	                 	     <html:textarea name="singleGradeForm" property="summary" cols="80" rows="22" style="margin:5px 0 0 3px;"  />
                          </td>
                      </tr>
                    
                    <tr class="list3">
                	  	 <td align="left"  valign='top' nowrap >
                	  	  <table   width='100%' ><tr><Td width='40%' valign='top' >
                	  	 
                	  	 
                 	     	<table border=0 >
                 	    <logic:equal name="singleGradeForm" property="allowUploadFile" value="true">
                 	     	<tr><td valign='top'>
                 	   		  &nbsp;<bean:message key="label.zp_employ.uploadfile"/>：
                 	   		  </td></tr>
                 	   	</logic:equal>	  
                 	   		  <tr>
                 	   		  <td>
                 	   		  
                 	   		  <table border="0" cellpmoding="0" cellspacing="0" cellpadding="0"  >
                 	   		  <logic:iterate id="element" name="singleGradeForm" property="summaryFileIdsList" >
                 	   		    <tr><td>&nbsp;&nbsp; 
                 	   		    <a href='/servlet/performance/fileDownLoad?article_id=<bean:write name="element" property="id" />'  target="_blank"  border='0' >  
                 	   			 <bean:write name="element" property="name" />
                 	   		  	</a>
                 	   		  	</td><td>&nbsp;
                 	   		  		<% if(summarystate.equals("0")||summarystate.equals("3")){ %>
                 	   		  	<a href="javascript:del(3,<bean:write name="element" property="id" />,${singleGradeForm.dbpre})">
                 	   		  		<image src='/images/del.gif' border=0 title='<bean:message key="label.zp_employ.deletefile"/>' >
                 	   		  	</a>
                 	   		  	<% } %>
                 	   		   </td></tr>
                 	   		  </logic:iterate>
                 	   		  </table>
                 	     	  </td>
                 	     	  </tr></table>
                 	     	  
                 	     	  
                 	     	   </td><td algin='right' width='60%' >
                 	     	  <%  if(s_rejectCause!=null&&s_rejectCause.trim().length()>0){ %>
									<bean:message key="performance.singlegrade.reportRejectReason"/>:<br>
									<html:textarea  name="singleGradeForm"  cols="45" rows="5"  readonly="true"  property="s_rejectCause"></html:textarea>
								
								<% } %>
                 	     	  </td></tr></table>
                 	     	  
                 	     	  
                          </td>
             	  </tr>
               <% if(summarystate.equals("0")||summarystate.equals("3")){ %>
               	<logic:equal name="singleGradeForm" property="allowUploadFile" value="true">
	             <tr class="list3">
	                	  	 <td align="left" nowrap style="padding-top: 4px;">
	                	  	   <fieldset align="center" style="width:98%;">
    							 <legend><bean:message key="label.zp_employ.uploadfileInfo"/><%=(file_max_size)%>K</legend>
	                	  	
	                 	 	   &nbsp;&nbsp;<span style='position:relative;bottom:3px;'><bean:message key="column.law_base.filename"/></span>&nbsp;&nbsp;<input type='text' style='border:1pt solid #C4D8EE !important;margin-bottom:1px;' maxLength=30  class='TEXT_NB'  size='20'      name='fileName' />
	                 	 	   <Br>&nbsp;&nbsp;<input name="file" type="file"   onchange='upload(1)'    size="40">
	                 	       
                 	    	   <br>&nbsp;
                 	    	  </fieldset>
	                       </td>
	              </tr>	
	             </logic:equal> 
	             <% } %>
                 <tr>
                 	<td>
                 	<Br>&nbsp;
                 	<% if(summarystate.equals("0")||summarystate.equals("3")){ %>
                    	<html:button styleClass="mybutton" style="margin-bottom:2px;" property="br_home" onclick="upload(2)">
		            		<bean:message key="button.save"/>
						</html:button>
						<!-- 绩效模板文件上传按钮 author:zangxj  day:2014-06-07 -->
						<%if(isnullAffix != "null"){%>
						<html:button styleClass="mybutton" style="margin-bottom:2px;" property="br_home" onclick="downAffix()">
		            	<bean:message key='train.job.export'/>
						</html:button>
						<%} %>
						<html:button styleClass="mybutton" style="margin-bottom:2px;" property="br_home" onclick="upload(7)">
		            		<bean:message key="button.submit"/>
						</html:button>
						<%-- bug 36723 and 36722 按钮与边框线重叠  添加br标签与上面对齐  wangb 20180417 --%>
						<Br>
					<% } %>
                    </td>
                  </tr>
                 </table>     
              
		</hrms:tab> 
		
        </hrms:tabset>   
          
                                                            
      
     
</td>     
<td>&nbsp;</td>     
<td valign='top' >
	
</td>
</tr></table>      
</logic:equal>



</form>
</body>

<script language="javascript">
   Element.hide('date_panel');
   
   var lay="${singleGradeForm.lay}";

  
   function hidden()
   {
   	Element.hide('date_panel');
   }
   window.status="";

  	 var t=null;
  	<logic:notEqual  name="singleGradeForm"  property="dbpre"  value="0">
	<logic:notEqual  name="singleGradeForm"  property="isSelfMark"  value="0">
	
	<logic:notEqual name="singleGradeForm" property="objectStatus" value="2">
	<logic:notEqual name="singleGradeForm" property="objectStatus" value="7">
	<logic:notEqual name="singleGradeForm" property="objectStatus" value="4">
	
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
   			t=window.setInterval("check('1','1')",autoType);
   		}
   	}
   	</logic:notEqual>	
   	</logic:notEqual>	 
   	</logic:notEqual>
	
	</logic:notEqual>	
   	</logic:notEqual>	
  	 
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
	   
</script>

</html>


