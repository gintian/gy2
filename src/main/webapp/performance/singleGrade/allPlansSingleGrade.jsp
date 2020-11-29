<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.SystemConfig" %>
<%
String grading_auto_saving="0";
if(SystemConfig.getPropertyValue("grading_auto_saving")!=null&&SystemConfig.getPropertyValue("grading_auto_saving").length()>0)
	grading_auto_saving=SystemConfig.getPropertyValue("grading_auto_saving");
%>
<hrms:themes />
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/performance/singleGrade/grade.js"></script>

<script language="javascript">

	///隐藏和显示打分表格用
	function hideShowTable(id)
	{
		var tableobj=document.getElementById(id+"table1");
		if(tableobj!=null)
		{
			if(tableobj.style.display=="none")//如果是隐藏的，就让它显示。同时图片变成减号
			{
				tableobj.style.display="";//显示
				var imgobj=document.getElementById(id+"img");
				if(imgobj!=null)
				{
					imgobj.src="/images/Rminus.gif";
				}
			}
			else
			{
				tableobj.style.display="none";//隐藏
				var imgobj=document.getElementById(id+"img");
				if(imgobj!=null)
				{
					imgobj.src="/images/Rplus.gif";
				}
			}
		}
		
	}
	var isFailing=0;///判断前面提交的那些计划是否有失败的。
	var submitPrompt=0;//提交之前是否提示过
	var exitSubmitPrompt=0;//如果提交前的确认  选择了取消  ，就不再循环了。
	var loopindex=0;
	function batch_check(n,autoScore)
	{
		//当第二次点击保存或提交时，重新初始化这三个参数
		isFailing=0;///判断前面提交的那些计划是否有失败的。
		submitPrompt=0;//提交之前是否提示过
		exitSubmitPrompt=0;//如果提交前的确认  选择了取消  ，就不再循环了。
		loopindex=0;
		///点击保存或提交按钮，先让按钮变灰
		if(document.getElementsByName("b_save")&&document.getElementsByName("b_save")[0])
			document.getElementsByName("b_save")[0].disabled=true;
		if(document.getElementsByName("b_refer")&&document.getElementsByName("b_refer")[0])
			document.getElementsByName("b_refer")[0].disabled=true;
		if(document.getElementsByName("b_save")&&document.getElementsByName("b_save")[1])
			document.getElementsByName("b_save")[1].disabled=true;
		if(document.getElementsByName("b_refer")&&document.getElementsByName("b_refer")[1])
			document.getElementsByName("b_refer")[1].disabled=true;
				
		var jsonobj = eval('${allPlansSingleGradeForm.jsonStr}');///json对象
		for(var f=0;f<jsonobj.length;f++)//将json数组循环
		{
			if(isFailing==1)///如果前面有计划失败了
			{
				break;
			}
			if(exitSubmitPrompt==1)
				break;
			loopindex++;
			var scoreStatus=jsonobj[f].scoreStatus;
			if(scoreStatus==2 || scoreStatus==3 || scoreStatus==7 || scoreStatus==4)///如果是已提交或不用打分
				continue;
			var pointIDs=jsonobj[f].pointIDs;
			var PointEvalType=jsonobj[f].pointEvalType;
			var pointContrl=jsonobj[f].pointContrl;
			var pointContrls=pointContrl.split("/");
			var noGradeItem=jsonobj[f].noGradeItem;
			var grading_auto_saving='<%=grading_auto_saving%>';
			var scoreflag=jsonobj[f].scoreflag;
			var DegreeShowType=jsonobj[f].degreeShowType;
			var _KeepDecimal=jsonobj[f].keepDecimal;
			var dataArea=jsonobj[f].dataArea;
			var isNull=jsonobj[f].isNull;
			var object_id=jsonobj[f].object_id;
			var wholeEval=jsonobj[f].wholeEval;
			var totalAppFormula=jsonobj[f].totalAppFormula;
			var limitation=jsonobj[f].limitation;
			var nodeKnowDegree=jsonobj[f].nodeKnowDegree;
			var isEntireysub=jsonobj[f].isEntireysub;
			var EvalOutLimitStdScore=jsonobj[f].evalOutLimitStdScore;
			var scoreBySumup=jsonobj[f].scoreBySumup;
			var template_id=jsonobj[f].template_id;
			var gradeClass=jsonobj[f].gradeClass;
			var status=jsonobj[f].status;
			var plan_id=jsonobj[f].plan_id;
			var plan_name=jsonobj[f].plan_name;
			var object_name=jsonobj[f].object_name;
			var wholeEvalMode=jsonobj[f].wholeEvalMode;
			var topscore=jsonobj[f].topscore;
			
			
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
				alert(object_name+" 在 "+plan_name+" 计划下 "+desc+"\n\r "+BATCHGRADE_INFO1+"！");///该指标的标度没有上下限
				isFailing=1;
				break;
			}///指标范围是否为空  结束
	
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
								alert(object_name+" 在 "+plan_name+" 计划下 "+DI+(index+1)+BATCHGRADE_INFO4+"！");
								obj[0].focus();
								isFailing=1;
								break;
							}
							else
							{
								var singleValue=aa_pointScore[0]; 	
								var a_singleValue=singleValue.split("*"); 
									 
								if(obj[0].type!='hidden'&&obj[0].disabled!=true&&(a_value<a_singleValue[0]*1||a_value>a_singleValue[1]*1))
								{
									alert(object_name+" 在 "+plan_name+" 计划下 "+DI+(index+1)+BATCHGRADE_INFO6+"！");
									obj[0].focus();
									isFailing=1;
									break;
								}
								else
								{
									_value=obj[0].value.toLocaleUpperCase();				    	
								}									 
							}
						} ///obj[0].value!=''&&obj[0].value!=' '&&obj[0].value!='null'&&obj[0].value!=undefined  结束
						else
						{
							if(n==2 && obj[0].disabled==false && isEntireysub=='true')
							{					
								alert(object_name+" 在 "+plan_name+" 计划下 "+BATCHGRADE_INFO2+"！");
								isFailing=1;
								break;			
							}										
						} ///否则 obj[0].value!=''&&obj[0].value!=' '&&obj[0].value!='null'&&obj[0].value!=undefined  结束			
					}///obj[0].type=="text"  结束
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
								alert(object_name+" 在 "+plan_name+" 计划下 "+BATCHGRADE_INFO2+"！");
								isFailing=1;
								break;
							}			
						}	///n==2 && _value=='null' && isEntireysub=='true'  结束									
					}///否则 ///obj[0].type=="text"  结束  
					index++;
					userValue+="/"+_value;
				} ///for(var i=0;i<points.length;i++)  结束
			} ///单选按钮  结束
			else
			{ ///如果是下拉
	
				var pointScores=dataArea.split("/");
				var object=document.getElementsByName("a"+plan_id+""+object_id);
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
								alert(object_name+" 在 "+plan_name+" 计划下 "+DI+(i+1)+BATCHGRADE_INFO4+"！");
								object[i].focus();
								isFailing=1;
								break;
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
								    	alert(object_name+" 在 "+plan_name+" 计划下 "+DI+(i+1)+BATCHGRADE_INFO5+"！");
								    	object[i].focus();
								    	isFailing=1;
								    	break;
								    }
								    else
								    {				
								    		    	
								        if(object[i].type!='hidden'&&object[i].disabled!=true&&a_value.toLocaleUpperCase().charCodeAt(0)<a_singleValue[0].toLocaleUpperCase().charCodeAt(0)  )
								    	{
								    		alert(object_name+" 在 "+plan_name+" 计划下 "+DI+(i+1)+BATCHGRADE_INFO6+"！");
								    		object[i].focus();
								    		isFailing=1;
								    		break;
								    	}
								        else
								        {		
								        	userValue+="/"+a_value.toLocaleUpperCase();
								        }							    
								    }							   							
								} ///temp1==1  结束
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
							}	/// 否则 temp1==0&&temp2==0 结束
						} ///scoreflag=='2'  结束
						else
						{
							if(object[i].type=="text")
							{
							
								if(temp2==0)
								{
									alert(object_name+" 在 "+plan_name+" 计划下 "+DI+(i+1)+BATCHGRADE_INFO4+"！");
									object[i].focus();
									isFailing=1;
									break;
								}
								else
								{
									var singleValue=aa_pointScore[0]; 	
								    var a_singleValue=singleValue.split("*"); 
									if(scoreflag!='4')   
								    {
									    if(object[i].type!='hidden'&&object[i].disabled!=true&&(a_value<a_singleValue[0]*1||a_value>a_singleValue[1]*1))
									    {
									    		alert(object_name+" 在 "+plan_name+" 计划下 "+DI+(i+1)+BATCHGRADE_INFO6+"！");
									    		object[i].focus();
									    		isFailing=1;
									    		break;
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
						}	///否则 scoreflag=='2' 结束
					} ///a_value!=null && trimStr(a_value).length>0 && a_value!='undefined'  结束
					else
					{
						if(n==2&&object[i].type!='hidden'&&object[i].disabled==false&&isEntireysub=='true' && (totalAppFormula==null || totalAppFormula==''))
							{

								alert(object_name+" 在 "+plan_name+" 计划下 "+BATCHGRADE_INFO2+"！");
								isFailing=1;
								break;
					
							}
						userValue+="/null";
			
					}  ///否则 a_value!=null && trimStr(a_value).length>0 && a_value!='undefined' 结束
				}   ///for(var i=0;i<object.length;i++)  结束
	
			}  ///下拉列表方式 结束
	
			///了解程度
			var kdtemp=plan_id+""+object_id+"konwDegree";
			var kdtempobj=document.getElementsByName(kdtemp)[0];
			var kdtempvalue="";
			if(kdtempobj!=null)
				kdtempvalue=kdtempobj.value;
			if(nodeKnowDegree=='true' && n==2 && isEntireysub=='true' && kdtempvalue=='')
			{
				alert(object_name+" 在 "+plan_name+" 计划下 "+BATCHGRADE_INFO2+"！");
				isFailing=1;
				break;
			}
			///总体评价
			var wetemp=plan_id+""+object_id+"wholeEval";
			var wetempobj=document.getElementsByName(wetemp)[0];
			var wetempvalue="";
			if(wetempobj!=null)
				wetempvalue=wetempobj.value;
			if(wholeEvalMode=='0'&&wholeEval=='true' && n==2 && isEntireysub=='true' && wetempvalue=='' && (totalAppFormula==null || totalAppFormula==''))
			{
				alert(object_name+" 在 "+plan_name+" 计划下 "+BATCHGRADE_INFO2+"！");
				isFailing=1;
				break;
			}
			if(wholeEvalMode='1' && document.getElementById("wholeEvalScoreId")!=null){
				var score = document.singleGradeForm.wholeEvalScore.value;
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
			if(isFailing==0)
			{
				var hashvo=new ParameterSet();
				hashvo.setValue("object_id",object_id);
				hashvo.setValue("templateId",template_id);
				hashvo.setValue("scoreflag",scoreflag);
				hashvo.setValue("userValue",userValue.substring(1)); 
				hashvo.setValue("plan_id",plan_id);
				hashvo.setValue("nodeKnowDegree",nodeKnowDegree);
				hashvo.setValue("wholeEval",wholeEval);
				hashvo.setValue("scoreBySumup",scoreBySumup);
				hashvo.setValue("limitation",limitation);
				hashvo.setValue("gradeClass",gradeClass);
				hashvo.setValue("status",status);
				hashvo.setValue("plan_name",plan_name);
				hashvo.setValue("object_name",object_name);
				hashvo.setValue("wholeEvalMode",wholeEvalMode);
	
				if(1==0)//让自动保存失效
				{
					hashvo.setValue("autoScore",autoScore);
				}
				if(nodeKnowDegree=='true')
				{
					hashvo.setValue("nodeKnowDegree_value",kdtempvalue);
				}
				if(wholeEval=='true'&& wholeEvalMode=='0')
				{		
					hashvo.setValue("wholeEval_value",wetempvalue);
				}
				if(wholeEvalMode='1' && document.getElementById("wholeEvalScoreId")!=null){
					hashvo.setValue("wholeEvalScore",score);
				}
				var In_paramters="flag="+n; 	
				if(n==2)
				{
					if(submitPrompt==0)//为0时才提示一下
					{
						if(!confirm(BATCHGRADE_INFO3+"!"))
						{
							exitSubmitPrompt=1;
							isFailing=1;
							break;
						}
						else
							submitPrompt=1;
					}
					
				}
	
				var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onFailure:errorInfo,onSuccess:returnInfo,functionId:'90100160004'},hashvo);
				
			}
			
			 
		} ///json循环  结束
		
		if(isFailing==1)///如果最后一个打分表格提交失败了，那么大循环就进不去了，所以还要在循环结束时判断一下。
		{
			if(n==2)
				setPreviousStatus(loopindex);//把前面已经提交的状态变为保存
		}
		else if(isFailing==0)
		{
			if(n==1)
			{
				alert("保存成功!");
			}
				
			else if(n==2)
			{
				alert("提交成功");
				allPlansSingleGradeForm.action="/selfservice/performance/allPlansSingleGrade.do?b_query=link&fromModel=menu&model=0";  ///刷新一下页面
				allPlansSingleGradeForm.submit();
			}
				
		}
		if(n==1 || (n==2 && isFailing==1) || (n==2 && exitSubmitPrompt==1))
		{
			///如果是保存，不管成不成功，让要按钮恢复可用    提交失败也要让按钮恢复可用
			if(document.getElementsByName("b_save")&&document.getElementsByName("b_save")[0])
				document.getElementsByName("b_save")[0].disabled=false;
			if(document.getElementsByName("b_refer")&&document.getElementsByName("b_refer")[0])
				document.getElementsByName("b_refer")[0].disabled=false;
			if(document.getElementsByName("b_save")&&document.getElementsByName("b_save")[1])
				document.getElementsByName("b_save")[1].disabled=false;
			if(document.getElementsByName("b_refer")&&document.getElementsByName("b_refer")[1])
				document.getElementsByName("b_refer")[1].disabled=false;	
		}
		
	} ///批量保存方法 结束
//一轮循环 保存成功
function returnInfo(outparameters)
{
	isFailing=0;
}
//一轮循环 保存失败
function errorInfo(outparameters)
{
	isFailing=1;
}
function openWin(url)
 {
 	// window.open(url,'aa','fullscreen');  
       window.open(url,'aa');  
 }
///显示员工日志
 function showWordDiary(plan_id,a0100,startDate,endDate)
 {
 		 var _url="/performance/workdiary/workdiaryshow.do?b_query=link&timeflag=1&logo=1&plan_id="+plan_id+"&a0100="+a0100+"&start_date="+startDate+"&end_date="+endDate;
		 var _width=window.screen.width-200;
		 window.open(_url,null,"width="+_width+",height=700,left=50,top=50,resizable=no,status=no,toolbar=no,scrollbars=yes,menubar=no,location=no"); 
 
 }
 ///隐藏标度说明
 function hidden()
 {
	Element.hide('date_panel');
 }
 ///某个计划打分出错，就把之前提交的状态设为保存
 function setPreviousStatus(m)
 {
 	var jsonobj = eval(${allPlansSingleGradeForm.jsonStr});///json对象
	for(var g=0;g<m;g++)//将json数组循环
	{
		var object_id=jsonobj[g].object_id;
		var plan_id=jsonobj[g].plan_id;
		var hashvo=new ParameterSet();
		hashvo.setValue("object_id",object_id);
		hashvo.setValue("plan_id",plan_id);
		var request=new Request({method:'post',asynchronous:false,onSuccess:setStatusOk,functionId:'90100160021'},hashvo);
	}
 }
 function setStatusOk(outparameter)
 {
 	
 }
</script>

<html>
  <head>
  </head>
  
  <body>
  
  <html:form action="/selfservice/performance/allPlansSingleGrade">
    <table width='80%' border='0' cellspacing='0' align='left' cellpadding='0'>
    <logic:equal name="allPlansSingleGradeForm" property="isHasSaveButton" value="1">
    	<tr height="5">
			<td></td>
		</tr>
		<tr>
			<td align="left">
				&nbsp;
			</td>
			<td align="left">
				<input type="button" class="mybutton" name="b_save" value="保存" onclick="batch_check(1)" />
				&nbsp;<input type="button" class="mybutton" name="b_refer" value="提交" onclick="batch_check(2)" />
			</td>
		</tr>
		</logic:equal>
		<tr height="5">
			<td></td>
		</tr>
	    <tr>
	    	<td align="left">
				&nbsp;&nbsp;
			</td>
		    <td align="left">
				${allPlansSingleGradeForm.gradeHtml}
			</td>
		</tr>
		<logic:equal name="allPlansSingleGradeForm" property="isHasSaveButton" value="1">
		<tr>
			<td align="left">
				&nbsp;
			</td>
			<td align="left">
				<input type="button" class="mybutton" name="b_save" value="保存" onclick="batch_check(1)" />
				&nbsp;<input type="button" class="mybutton" name="b_refer" value="提交" onclick="batch_check(2)" />
			</td>
		</tr>
		</logic:equal>
	</table>
	<div id="date_panel">
   			
	</div>
	</html:form>
  </body>
</html>
