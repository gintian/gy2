<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>


<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript">



function check(n)
{
	var z0127="${interviewExamineForm.z0127}";
	var dataArea="${interviewExamineForm.dataArea}";
	var isNull="${interviewExamineForm.isNull}";
	var objectIDs="${interviewExamineForm.object_id}";
	var hireState="${interviewExamineForm.hireState}"
	var mainBodyID=document.interviewExamineForm.mainBodyId.value;	
	
	var templateId="${interviewExamineForm.templateId}";
	var object_id=objectIDs.split("/");
	var scoreflag="${interviewExamineForm.scoreflag}";   //=2混合，=1标度
	
	
	if(isNull=='1')
	{
		alert(SOMEFIELD_NO_TOP_SO_NO_OPERATOR+"！");
		return;
	}
	var pointScores=dataArea.split("/");
	var object=eval("document.interviewExamineForm.a"+object_id[0]);
	if(object.length==undefined)
	{	
		
		var temp=object;
		object=new Array();
		object[0]=temp;
	}
	
	
	var userValue="";
	for(var i=0;i<object.length;i++)
	{
		var a_pointScore=pointScores[i];		
		var aa_pointScore=a_pointScore.split("#");
		var a_value=object[i].value;
		var is_over=0;   //值是否超出指标范围
		if(a_value!=''&&a_value!=' '&&a_value!='null')
		{				 
				  
				 var temp1=fucPWDchk(a_value);	//是否为字母
				 var temp2=fucNumchk(a_value);   //是否为数字		  
				if(scoreflag=='2')   //混合打分
				{	  		   						
						if(temp1==0&&temp2==0)
						{
							alert(DI+(i+1)+VALUE_IS_NOT_RIGHT+"！");
							return;
						}
						else
						{
							var singleValue="";
							if(temp1==1)
							{							   
							    if(a_value.length>=1)
							    {
							    	alert(DI+(i+1)+INPUT_LETTER_IS_NOT_RIGHT+"！");
							    	return;
							    }
							    else
							    {				
							        	userValue+="/"+a_value.toLocaleUpperCase();						    
							    }							   							
							}
							else if(temp2==1)
							{							  
							    
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
								alert(DI+(i+1)+VALUE_IS_NOT_RIGHT+"！");
								return;
							}
							else
							{
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
		/*	if(n==2&&object[i].type!='hidden')
			{
				alert("所有打分指标项为必填项！");
				return;
			}*/
			userValue+="/null";
		
		}
	}
	var hashvo=new ParameterSet();
	hashvo.setValue("z0127",z0127);
	hashvo.setValue("mainBodyID",mainBodyID);
	hashvo.setValue("object_id",object_id[0]);
	hashvo.setValue("templateId",templateId);
	hashvo.setValue("scoreflag",scoreflag);
	hashvo.setValue("userValue",userValue.substring(1)); 	
	hashvo.setValue("status","${interviewExamineForm.status}");
	hashvo.setValue("hireState",hireState);
	var In_paramters="flag="+n; 
/*	if(userValue.substring(1).indexOf('null')!=-1)
	{
		alert("所有指标项都为必添项！");
		return;
	}*/
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'3000000132'},hashvo);
	
	
}


function returnInfo(outparamters)
{
	var info=outparamters.getValue("info");
	alert(info);
}

function setTPinput(){
    var InputObject=document.getElementsByTagName("input");
    for(var i=0;i<InputObject.length;i++){
        var InputType=InputObject[i].getAttribute("type");
        if(InputType!=null&&(InputType=="text"||InputType=="password")){
            InputObject[i].className=" "+"TEXT4";
        }
    }
}

</script>



<style>

.ListTable_self {

    BACKGROUND-COLOR: #FFFFFF;
    BORDER-BOTTOM: medium none; 
    BORDER-COLLAPSE: collapse; 
    BORDER-LEFT: medium none; 
    BORDER-RIGHT: medium none; 
    BORDER-TOP: medium none; 
 }   
.common_old_color{
    background-color:#F4F7F7;
}

</style>

<hrms:themes></hrms:themes>
<body onload="setTPinput()">
<html:form action="/hire/interviewEvaluating/interviewExamine">
&nbsp;
<input type='hidden' name='object_id' value="${interviewExamineForm.object_id}" >

<table border='0' style="margin-top:-15px;padding-top:0px; padding-left:0px;margin-left:-2px;">
<tr><td>	
	<div style="display=${interviewExamineForm.isSelfGrade};" >
	<bean:message key="hire.parameterSet.interviewer"/>：
	<hrms:optioncollection name="interviewExamineForm" property="gradeUserList" collection="list" />
	<html:select name="interviewExamineForm" property="mainBodyId" size="1"  onchange='gradeView()' value="${interviewExamineForm.mainBodyId }" >
			<html:options collection="list" property="dataValue" labelProperty="dataName"/>
	</html:select>
	</div>
</td></tr>
<tr><td>
${interviewExamineForm.gradeHtml}
</td></tr></table>

<div id="date_panel">
   			
</div>
<input type='hidden' name='extendSql' value="${interviewExamineForm.extendSql}" />
<input type='hidden' name='orderSql' value="${interviewExamineForm.orderSql}" />
</html:form>
</body>
<script language="javascript">
   Element.hide('date_panel');
   
   var lay="${interviewExamineForm.lay}";
   
   function showDateSelectBox(srcobj)
   {
     
      date_desc=srcobj;
      Element.show('date_panel');   
      var pos=getAbsPosition(srcobj);
	  with($('date_panel'))
	  {
	        style.position="absolute";
    		if(lay*1>2)
	        {
	    		style.posLeft=0;
	    		style.posTop=pos[1]-1+srcobj.offsetHeight;
			}
			else
			{
				style.posLeft=pos[0]+srcobj.offsetWidth+115;
		    	style.posTop=pos[1]-15+srcobj.offsetHeight;
		    }			  
      }  
      
      var In_paramters="point_id="+srcobj.id; 		
	  var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo2,functionId:'90100160009'});
   }
   
   
   
   
	function returnInfo2(outparamters)
	{
		var fieldlist=outparamters.getValue("dataList");	
		var dataHtml=  getDecodeStr(outparamters.getValue("dataHtml"));	
		dataHtml=replaceAll(dataHtml,"#@#","<br>");		
		date_panel.innerHTML=dataHtml;
		
		
	}
   
   function replaceAll( str, from, to ) {
	    var idx = str.indexOf( from );
	    while ( idx > -1 ) {
	        str = str.replace( from, to ); 
	        idx = str.indexOf( from );
	    }
	   
	    return str;
	}
   
  
   
   function goBack()
   {         
   			interviewExamineForm.action='/hire/interviewEvaluating/interviewExamine.do?b_query=link&operate=init';
    		interviewExamineForm.submit();
   
   }
   
   
   
   function hidden()
   {
   	Element.hide('date_panel');
   }
   window.status="";
   
   
   

function gradeView()
{
	var objectIDs="${interviewExamineForm.object_id}";
	var object_id=objectIDs.split("/");
	document.interviewExamineForm.action="/hire/interviewEvaluating/interviewExamine.do?b_grade=0&object_id="+object_id[0]+"&mainbodyID="+document.interviewExamineForm.mainBodyId.value;
   	document.interviewExamineForm.submit();

}
   
   
</script>
