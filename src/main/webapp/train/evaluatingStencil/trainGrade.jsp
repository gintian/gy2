<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>


 <link href="../../css/css1.css" rel="stylesheet" type="text/css">
 <hrms:themes></hrms:themes>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<%UserView userView = (UserView) session.getAttribute(WebConstant.userView); %>
<script language="JavaScript">

function lookResult(r3101,templateid)
{
	
	tranEvaluationFrame.action="/train/evaluatingStencil.do?b_analyse=link&r3101="+r3101+"&templateid="+templateid+"&rerurn=<%=userView.getBosflag()%>";
	tranEvaluationFrame.submit();
}

function check(n)
{
	
	var r3101='<%=request.getParameter("r3101")%>';
	var dataArea="${tranEvaluationFrame.dataArea}";
	var isNull="${tranEvaluationFrame.isNull}";
	var templateId='<%=request.getParameter("id")%>';
	var object_id="${tranEvaluationFrame.objectID}";
	var scoreflag="${tranEvaluationFrame.scoreflag}";   //=2混合，=1标度
	if(isNull=='1')
	{
		alert("由于某些指标项的标度上下限没有设定，所以不予操作！");
		return;
	}
	var pointScores=dataArea.split("/");
	var _object=document.getElementsByName("a"+object_id);        //eval("document.tranEvaluationFrame.a"+object_id);
	var userValue=""; 
	for(var i=0;i<_object.length;i++)
	{
	 
		var a_pointScore=pointScores[i];		
		var aa_pointScore=a_pointScore.split("#"); 
		var a_value=_object[i].value;
		var is_over=0;   //值是否超出指标范围
		if(a_value!=''&&a_value!=' '&&a_value!='null')
		{				 
				   var temp2=fucNumchk(a_value);    //是否为数字		  
				   if(scoreflag=='1')   //标度打分			
					{
						if(_object[i].type=="text")
						{
							if(temp2==0)
							{
								alert("第"+(i+1)+"行指标打分输入的值不正确！");
								return;
							}
							else
							{
								var singleValue=aa_pointScore[0]; 	
							    var a_singleValue=singleValue.split("*"); 
							    if(a_value<a_singleValue[0]*1||a_value>a_singleValue[1]*1)
							    {
							    		alert("第"+(i+1)+"行指标打分输入的值不在标度范围内！");
							    		return;
							    }
							    else
							    {
			
								    userValue+="/"+a_value.toLocaleUpperCase();				    	
							     }
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
			if(n==2&&_object[i].type!='hidden')
			{
				alert("所有打分指标项为必填项！");
				return;
			}
			userValue+="/null";
		
		}
	}
	var hashvo=new ParameterSet();
	hashvo.setValue("r3101",r3101);
	hashvo.setValue("object_id",object_id);
	hashvo.setValue("templateId",templateId);
	hashvo.setValue("scoreflag",scoreflag);
	hashvo.setValue("userValue",userValue.substring(1)); 
	hashvo.setValue("status","${tranEvaluationFrame.status}");	// 0:分值  1:权重
	var In_paramters="flag=ww";
	
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'10300130013'},hashvo);
}

function returnInfo(outparamters)
{
	var info=outparamters.getValue("info");
	alert(info);
	
	// 屏蔽提交按钮 lium
	var submit = document.getElementById("idSubmit");
	if (submit) {
		submit.parentNode.removeChild(submit);
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
</style>
<html:form action="/train/evaluatingStencil">
&nbsp;
<table border='0'>
<tr><td>&nbsp;&nbsp; </td>
<td>
${tranEvaluationFrame.gradeHtml}
</td></tr></table>

<div id="date_panel">
   			
</div>

</html:form>
<script language="javascript">
   Element.hide('date_panel');
   
   var lay="${tranEvaluationFrame.lay}";
   
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
				style.posLeft=pos[0]+srcobj.offsetWidth+110;
		    	style.posTop=pos[1]-15+srcobj.offsetHeight;
		    }			  
      }  
      
      var In_paramters="point_id="+srcobj.id; 		
	  var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo2,functionId:'90100160009'});
   }
   
   
   
   
	function returnInfo2(outparamters)
	{
		var fieldlist=outparamters.getValue("dataList");	
		var dataHtml=outparamters.getValue("dataHtml");		
		date_panel.innerHTML=replaceAll(getDecodeStr(dataHtml),"#@#","<br>");
	}
   
   function replaceAll( str, from, to ) {
	    var idx = str.indexOf( from );
	    while ( idx > -1 ) {
	        str = str.replace( from, to ); 
	        idx = str.indexOf( from );
	    }
	   
	    return str;
	}
   
  
   
   
   
   function hidden()
   {
   	Element.hide('date_panel');
   }
   window.status="";
   
   
   function goBack()
   {
   var flag="${tranEvaluationFrame.enteryType}";
   var home="${tranEvaluationFrame.home}";
     if(flag=='0')
     {
    	 document.location="/train/evaluationdetails.do?b_query=link";
     }
     else
     {
       if(home=='5'){
    	   var tar='<%=userView.getBosflag()%>';
	       if(tar=="hl"){//6.0首页
		        document.location="/templates/index/portal.do?b_query=link";
	       }else if(tar=="hcm"){//7.0首页
		        document.location="/templates/index/hcm_portal.do?b_query=link";
	       }
       }else
           window.close();
     }
   		
   }
   
   
</script>
