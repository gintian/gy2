<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*, org.apache.commons.beanutils.LazyDynaBean,com.hjsj.hrms.actionform.performance.ScoreAjustForm" %>

<%
	ScoreAjustForm implementForm=(ScoreAjustForm)session.getAttribute("scoreAjustForm");	
	ArrayList mainBodyList=implementForm.getMainBodyList();
	String adjustEvalRange=(String)implementForm.getAdjustEvalRange(); //调整范围：0=指标，1=总分.默认为0
%>
<script type="text/javascript">

var IVersion=getBrowseVersion();

if(IVersion==8){
  document.writeln("<link href=\"/performance/objectiveManage/objectiveCard/objectiveCard_8.css\" rel=\"stylesheet\" type=\"text/css\">");
}
else{
  document.writeln("<link href=\"/performance/objectiveManage/objectiveCard/objectiveCard.css\" rel=\"stylesheet\" type=\"text/css\">");
}

</script>
<style> 
.ListTable_self {
    BACKGROUND-COLOR: #FFFFFF !important;
    border: inset 1px #94B6E6;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
    
 }  
 
  .Input_self2{                                                                    
  font-size:   12px;                                              
  font-weight:   bold;                                                          
          
  letter-spacing:   1px;                      
  text-align:   right;           
  width:   90%;    
  height:  25px;                                
  border:   1px   solid   #94B6E6;           
  cursor:   hand;                                     
  } 
 
 
 .Input_self{                                                                    
  font-size:   12px;                                              
  font-weight:   bold;                                                          
        
  letter-spacing:   1px;                      
  text-align:   right;                        
  height:   90%;                                    
  width:   90%;                                    
  border:   1px   solid   #94B6E6;           
  cursor:   hand;                                     
  } 
div#tbl-container {	
	width:90%;
	overflow:auto;
	BORDER-BOTTOM:#94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid; 
}
</style>
<script LANGUAGE=javascript src="/js/function.js"></script>
<script type="text/javascript">
var mainbodys = new Array();
var n=0;
<%
for(int i=0;i<mainBodyList.size();i++)
{
	LazyDynaBean abean = (LazyDynaBean)mainBodyList.get(i);
	String mainbody_id = (String)abean.get("mainbody_id");
%>
	mainbodys[n++]='<%=mainbody_id%>';
<%
}
%>
function tempCompute()
{
	var hashvo=new ParameterSet(); 
  	hashvo.setValue("plan_id","${scoreAjustForm.plan_id}");
  	hashvo.setValue("object_id","${scoreAjustForm.object_id}"); 
  	var request=new Request({asynchronous:false,onSuccess:save_ok2,functionId:'9023000324'},hashvo); 
}

function save_ok2(outparameters)
{ 
  	scoreAjustForm.action="/selfservice/performance/scoreAjust.do?b_ajust=adjust&&plan_id=${scoreAjustForm.plan_id}&object_id=${scoreAjustForm.object_id}";
	scoreAjustForm.submit();
}

function checkKeyCode()
{
    var code=window.event.keyCode;
    var ret=true;
    if(code==8||code==46||code==9||code==190||code==110||code==13)
    {
        if(code==13)
        window.event.keyCode=9;
    }
    else if(96<=code&&code<=105)
    {      
    }else if(48<=code&&code<=57)
    {
    }
    else
    { 
        if((window.event.shiftKey)&&(code==48||code==49||code==57||code==56||code==187))
        {
        }
        else if(window.event.shiftKey&&code==189)
        {
           window.event.returnValue=false;
        }
        else if(code==189||code==109)
        {
        }
        else
        {
           window.event.returnValue=false;
        }     
   }   
}

function IsDigit(obj) 
{
	if ( !(((window.event.keyCode >= 48) && (window.event.keyCode <= 57)) 
			|| (window.event.keyCode == 13) || (window.event.keyCode == 46) 
			|| (window.event.keyCode == 45)))
	{
		window.event.keyCode = 0 ;
		return false;
	} 
	return true;
	
/*	if((event.keyCode >= 46) && (event.keyCode <= 57) && event.keyCode!=47)
	{
		var values=obj.value;
		if((event.keyCode == 46) && (values.indexOf(".")!=-1))//有两个.
			return false;
		if((event.keyCode == 46) && (values.length==0))//首位是.
			return false;	
		return true;
	}
	return false;	
*/	
}
//检验数字类型
function checkValue(obj)
{
  	if(obj.value.length>0)
  	{
  		if(!checkIsNum2(obj.value))
  		{
  			alert('请输入数值！');
  			obj.value='';
  			obj.focus();
  		}
  	} 
}

	function isNullVal(theObj)
	{
		if(ltrim(rtrim(theObj.value))=='')
		{
			theObj.value='0';
		}
	}
	function returnList()
	{		
		scoreAjustForm.action="/selfservice/performance/scoreAjust.do?b_query=return";
		scoreAjustForm.submit();
	}
	function saveAjust(theOper)
	{		
		var pointname='';
	    var pointScore=new Array();
	    
	    var logoSign=true;
	    var pointInput=document.getElementsByTagName("input");
	    if(pointInput!=null && pointInput.length>0)
		{
			for(var i=0;i<pointInput.length;i++)
			{
				if(pointInput[i].type=='text')
					logoSign=false;				
			}
		}			    		
		if(logoSign)
		{
			var points=document.getElementsByTagName("select");
			for(var i=0;i<points.length;i++)
			{	
				var pScore;
				var pOldScore;
				if(document.getElementById(points[i].name+'_score')!=null)
					pScore = document.getElementById(points[i].name+'_score').value;
				if(document.getElementById(points[i].name+'_oldscore')!=null)
					pOldScore = document.getElementById(points[i].name+'_oldscore').value;
						  		
	 			pointScore[i]=points[i].name+':'+points[i].value+':'+pScore+':'+pOldScore;
	 		}
	  	}else
		{
			var points=document.getElementsByTagName("input");
			if(points!=null && points.length>0)
			{
				var j=0;
				for(var i=0;i<points.length;i++)
				{
					if(points[i].type=='text')
					{	
						var pScore;
						var pOldScore;
						if(points[i].name=='totalScore')
						{
							if(document.getElementById(points[i].name+'_score')!=null)
								pScore = document.getElementById(points[i].name+'_score').value;
							if(document.getElementById(points[i].name+'_oldscore')!=null)
								pOldScore = document.getElementById(points[i].name+'_oldscore').value;
					
							pointScore[j]=points[i].name+':'+points[i].value+':'+pScore+':'+pOldScore;
						}else
						{
							if(document.getElementById(points[i].name+'_score')!=null)
								pScore = document.getElementById(points[i].name+'_score').value;
							if(document.getElementById(points[i].name+'_oldscore')!=null)
								pOldScore = document.getElementById(points[i].name+'_oldscore').value;
							
							pointScore[j]=points[i].name+':'+points[i].value+':'+pScore+':'+pOldScore;
						}
						j++;
					}					
				}
			}
		}
		var theflag=false;
		var info = "您确定提交吗？";		
		if(theOper==2 &&confirm(info))
			theflag	=true;
		else if(theOper==1)
			theflag	=true;
		
		if(theflag)
		{
			var hashvo=new ParameterSet();
  			hashvo.setValue("oper",theOper);
  			hashvo.setValue("plan_id","${scoreAjustForm.plan_id}");
  			hashvo.setValue("object_id","${scoreAjustForm.object_id}");
  			hashvo.setValue("pointScore",pointScore);  	  
  			var request=new Request({asynchronous:false,onSuccess:save_ok,functionId:'9023000320'},hashvo);
		} 	
	}
	function save_ok(outparameters)
	{  
  		var oper =outparameters.getValue("oper");
  		var totalScore =outparameters.getValue("totalScore");
  		var resultdesc =outparameters.getValue("resultdesc");
  		var ordering =outparameters.getValue("ordering");
  		
  		if(totalScore.length>0)
  		{  			
   			if(document.getElementById('totalScore_td')!=null)
   				document.getElementById('totalScore_td').innerText=totalScore;
   			if(document.getElementById('resultdesc_td')!=null)
   				document.getElementById('resultdesc_td').innerText=resultdesc;
   			if(document.getElementById('ordering_td')!=null)
   				document.getElementById('ordering_td').innerText=ordering;
  		}   
  		if(oper=='1')
  		{
  			alert('保存成功！');
  			scoreAjustForm.action="/selfservice/performance/scoreAjust.do?b_ajust=${scoreAjustForm.ajustOper}&plan_id=${scoreAjustForm.plan_id}&object_id=${scoreAjustForm.object_id}";
			scoreAjustForm.submit();
  		}  			
   		else
   		{
   			alert('提交成功！');
   			document.getElementById('bt1').style.display='none';
   			document.getElementById('bt2').style.display='none';
   			if(document.getElementById('bt0'))
	   			document.getElementById('bt0').style.display='none';
   			document.getElementById('bt4').style.display='none';
   			var points=document.getElementsByTagName("select");
			if(points&&points.length>0)
			{
				for(var i=0;i<points.length;i++)			  		
	 				points[i].disabled=true;
	  		}else
			{
				points=document.getElementsByTagName("input");
				var j=0;
				for(var i=0;i<points.length;i++)
				{
					if(points[i].type=='text')				
						points[i].disabled=true;										
				}
			}
   		}
   					
	}
	function returnMark()
	{
		if(mainbodys.length<=0)
		{
			alert("没有要退回的考核主体！");
			return;
		}
		if(mainbodys.length==1)
		{
			if(confirm('您确定要退回此考核主体吗？'))
	   		{
				returnMark_ok(mainbodys);
	   		}else
	   			return;
	   	}
		if(mainbodys.length>1)
		{
			var strurl='/selfservice/performance/scoreAjust.do?b_showbodys=link';
			var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
			var config = {
			      	width:550,
			      	height:450,
			      	title:'退回',
			      	id:'returnMarkWin'
		    	}
		   		modalDialog.showModalDialogs(iframe_url,'template_win',config,returnMark_ok);
			
		}
	}
	function returnMark_ok(return_vo){
		if(return_vo==null)
			return;
		mainbodys=return_vo;
		if(mainbodys.length==0)
			return;
		var tempBodys = new Array();
		for(var i=0;i<mainbodys.length;i++){
			tempBodys[i]=mainbodys[i];	
		}
		
		var hashvo=new ParameterSet();
  		hashvo.setValue("plan_id","${scoreAjustForm.plan_id}");
  		hashvo.setValue("object_id","${scoreAjustForm.object_id}"); 
		hashvo.setValue("opt",'30');
		hashvo.setValue("mainbodys",tempBodys);
		var request=new Request({method:'post',asynchronous:false,onSuccess:alertInfo,functionId:'9023000003'},hashvo);	
	}
	function alertInfo(outparameters)
	{
	  	var flag =outparameters.getValue("flag");
	  	if(flag=='1')
	  	{
	  		alert('已成功退回!');
	  		scoreAjustForm.action="/selfservice/performance/scoreAjust.do?b_ajust=${scoreAjustForm.ajustOper}&plan_id=${scoreAjustForm.plan_id}&object_id=${scoreAjustForm.object_id}";
			scoreAjustForm.submit();
	  	}
			
	}
	function changeScore(theObj)
	{
	    var pointScore=new Array();	    
		pointScore[0]=theObj.name+':'+theObj.value+':'+document.getElementById(theObj.name+'_score').value+':'+document.getElementById(theObj.name+'_oldscore').value;
		var hashvo=new ParameterSet();
  		hashvo.setValue("oper","3");
  		hashvo.setValue("plan_id","${scoreAjustForm.plan_id}");
  		hashvo.setValue("object_id","${scoreAjustForm.object_id}");
  		hashvo.setValue("pointScore",pointScore);  	
  		hashvo.setValue("point_id",theObj.name.substring(2));    
  		var request=new Request({asynchronous:false,onSuccess:setScore,functionId:'9023000320'},hashvo); 
	}
	function setScore(outparameters)
	{ 
		var point_id =outparameters.getValue("point_id");
		var adjustScore=outparameters.getValue("adjustScore");
		document.getElementById(point_id+'_rela').innerText=adjustScore;
	}	
	
	function dispRule(theObj)
	{
		if(theObj.checked)
			document.getElementById('rule').style.display='block';
		else
			document.getElementById('rule').style.display='none';
	}
</script>
 <hrms:themes />
<html:form action="/selfservice/performance/scoreAjust">	
<script language='javascript' >
<% if(adjustEvalRange!=null&&adjustEvalRange.equals("1")){%>
var theHeight = document.body.clientHeight-120;
<% }else{%>
var theHeight = document.body.clientHeight-80;
<% } %>

</script>	
	${scoreAjustForm.scoreAjustHtml}
</html:form>
<script language='javascript'>
var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
var isEdge = userAgent.indexOf("Edge") > -1; //判断是否Edge浏览器
if(isEdge) {//Edge浏览器下表格线条显示问题
    var lockTds = document.querySelectorAll("td[class*='RecordRow_self_locked']");
    for(var i=0;i<lockTds.length;i++){
        lockTds[i].style.position='static';
    }
}
</script>