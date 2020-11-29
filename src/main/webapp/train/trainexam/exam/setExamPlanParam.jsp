<%@page import="com.hrms.hjsj.sys.ConstantParamter"%>
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script type="text/javascript" src="/ajax/basic.js"></script>
<script type="text/javascript" src="/js/function.js"></script>
<script type="text/javascript" src="/js/constant.js"></script>
<style>
   .Stable td{
       padding-left:20px;
   }
   .text4{
   		text-align: right;
   }
</style>
<script language="javascript">  
  function save()
  {
    var hashvo = new ParameterSet(); 
    hashvo.setValue("r5400","${trainExamPlanForm.r5400}");    
    
    var len=document.trainExamPlanForm.elements.length;
    var uu;
    for (var i=0;i<len;i++)
    {
        var element = document.trainExamPlanForm.elements[i]; 
        if (element.type=="checkbox")
        {
           if(element.name=="emailEnable")
           {
             if(element.checked)
               hashvo.setValue("email","true");
             else
               hashvo.setValue("email","false");
           }
           else if(element.name=="smsEnable")
           {
             if(element.checked)
               hashvo.setValue("sms","true");
             else
               hashvo.setValue("sms","false");
           }else if(element.name=="weixinEnable"){
        	   if(element.checked)
        		   hashvo.setValue("weixin","true");
        	   else
        		   hashvo.setValue("weixin","false");
           }else if(element.name=="dingTalk"){
        	   if(element.checked)
        		   hashvo.setValue("dingTalk","true");
        	   else
        		   hashvo.setValue("dingTalk","false");
           }else if(element.name=="autoCompute"){
        	   if(element.checked)
                   hashvo.setValue("autoCompute","true");
               else
                   hashvo.setValue("autoCompute","false");
           }else if(element.name=="autoRelease"){
        	   if(element.checked)
        		   hashvo.setValue("autoRelease","true");
               else
                   hashvo.setValue("autoRelease","false");
           }else if(element.name=="enabled"){
        	   if(element.checked)
        		   hashvo.setValue("enabled","true");
               else
                   hashvo.setValue("enabled","false");
           }else if(element.name=="pendingTask"){
        	   if(element.checked)
        		   hashvo.setValue("pendingTask","true");
               else
                   hashvo.setValue("pendingTask","false");
           }
        }
    }

	if(!checkNUM2(document.all.times,'3','0',RAIN_TRAINEXAM_EXAM_TIMESERROR))
		return false;
    hashvo.setValue("tmp",document.all.messageTmp.value);
	hashvo.setValue("tmp1",document.all.messageSue.value);
	hashvo.setValue("times",document.all.times.value);
    var request=new Request({asynchronous:false,onSuccess:ajaxrefresh,functionId:'2020081010'},hashvo);   
  }
  
  function ajaxrefresh(outparamters){
    if(outparamters!=null)
    {
      var flag=outparamters.getValue("flag"); 
	    if("ok"==flag) {
	      window.close();
	    } else {
	      alert("操作失败！");
	    }
    }
  }
  
  function selcheck(){
  	if(!document.trainExamPlanForm.emailEnable.checked&&!document.trainExamPlanForm.smsEnable.checked
  			<%if(!"".equals(ConstantParamter.getAttribute("wx", "corpid"))){ %>
  	  		  	&&!document.trainExamPlanForm.weixinEnable.checked
  	  		<%}
  	  		  if(StringUtils.isNotEmpty(ConstantParamter.getAttribute("DINGTALK", "corpid"))){ %>
	  		&&!document.trainExamPlanForm.dingTalk.checked
		<%}%>
  		){
  		document.trainExamPlanForm.messageTmp.disabled=true;
  		document.trainExamPlanForm.messageSue.disabled=true;
  	}else{
  		document.trainExamPlanForm.messageTmp.disabled=false;
  		document.trainExamPlanForm.messageSue.disabled=false;
  	}
  }
  
  function changeItem(){
	  if(document.trainExamPlanForm.autoCompute.checked){
		  document.getElementById("releaseSpan").style.display='block';
		  if(document.trainExamPlanForm.autoRelease.checked)
			  document.getElementById("reexam").style.display='block';
	  }else{
		  document.getElementById("releaseSpan").style.display='none';
		  document.getElementById("reexam").style.display='none';
	  }
	  
  }
  function changeReexam(){
	  if(document.trainExamPlanForm.autoRelease.checked && document.trainExamPlanForm.autoCompute.checked){
		  document.getElementById("reexam").style.display='block';
	  }else
		  document.getElementById("reexam").style.display='none';
  }
  function changeTimes(){
	  if(document.trainExamPlanForm.enabled.checked){
		  document.trainExamPlanForm.times.disabled=false;
	  }else
		  document.trainExamPlanForm.times.disabled=true;
  }
</script>
<hrms:themes/>
<html:form action="/train/trainexam/exam/plan">
    <table width="95%" height="200" border="0" cellspacing="0" cellpadding="0" align="center">
    <tr>
      <td align="center" valign="bottom">  
        <fieldset style="width:100%">
          <legend>
             <bean:message key="train.examplan.param"/>
          </legend>  
          <table width="95%">
              <tr>
                <td>
                     <fieldset style="width:100%">
			          <legend>
			            <bean:message key="train.examplan.message" />
			          </legend>
			          
			          <table cellspacing="0" cellpadding="0" border="0" width=90% class="Stable">
			            <tr>
			              <td align="left" height="35">
			                <html:checkbox name="trainExamPlanForm" property="smsEnable" onclick="selcheck();">
			                  <bean:message key="train.message.sms"/>通知
			                </html:checkbox>
			                &nbsp;&nbsp;&nbsp;
			                <html:checkbox name="trainExamPlanForm" property="emailEnable" onclick="selcheck();">
			                  <bean:message key="train.message.email"/>通知
			                </html:checkbox>
			                <%if(!"".equals(ConstantParamter.getAttribute("wx", "corpid"))){ %>
			                	&nbsp;&nbsp;&nbsp;
				                <html:checkbox name="trainExamPlanForm" property="weixinEnable" onclick="selcheck();">
				                 	 微信通知
				                </html:checkbox>
			                <%} %>
			                <%if(StringUtils.isNotEmpty(ConstantParamter.getAttribute("DINGTALK", "corpid"))){ %>
			                	&nbsp;&nbsp;&nbsp;
				                <html:checkbox name="trainExamPlanForm" property="dingTalk" onclick="selcheck();">
				                 	 钉钉通知
				                </html:checkbox>
			                <%} %>
			                <html:checkbox name="trainExamPlanForm" property="pendingTask" onclick="selcheck();">
				                 	 待办通知
				            </html:checkbox>
			              </td>            
			            </tr> 
			            <tr>
			              <td height="25">
			                &nbsp;启动考试<bean:message key="train.message.template" />
			                <html:select name="trainExamPlanForm" property="messageTmp" size="1" style="width:150px;">
			                  <html:option value=""></html:option>
			                  <html:optionsCollection name="trainExamPlanForm" property="messageTmpList" label="dataName" value="dataValue"/>
			                </html:select> 
			              </td>
			            </tr> 
			            <tr>
			              <td height="25">
			                &nbsp;发布成绩<bean:message key="train.message.template" />
			                <html:select name="trainExamPlanForm" property="messageSue" size="1" style="width:150px;">
			                  <html:option value=""></html:option>
			                  <html:optionsCollection name="trainExamPlanForm" property="messageTmpList" label="dataName" value="dataValue"/>
			                </html:select> 
			              </td>
			            </tr> 
			            <tr>
			              <td height='10'></td>
			            </tr>      
			          </table>
			         </fieldset>
                </td>
              </tr>
              <tr>
                <td>
                    <fieldset>
                       <legend>
                           <bean:message key="train.examplan.examset"/>
                       </legend>
                       <table border=0 class="Stable">
                          <tr>
                            <td height="30" valign="top">
                               <html:checkbox name="trainExamPlanForm" property="autoCompute" onclick="changeItem()">
			                     <bean:message key="train.examplan.autocompute"/>
			           		   </html:checkbox>
                            </td>
                            <td valign="top">
                               <span style="display:none" id="releaseSpan" >
			           		      <html:checkbox name="trainExamPlanForm" property="autoRelease" onclick="changeReexam()">
                                    <bean:message key="train.examplan.autorelease"/>
                                  </html:checkbox>
			           		   </span>
                            </td>
                          </tr>
                          <tr>
                            <td height="30" valign="top" colspan="2" id="reexam" style="display: none;">
                               <html:checkbox name="trainExamPlanForm" property="enabled" onclick="changeTimes()">
			                   		<bean:message key="train.trainexam.exam.mytest.reexam"/>
			                     <html:text name="trainExamPlanForm" property="times" maxlength="3" styleClass="text4" style="width: 30px;" disabled="true"/>
			                     <bean:message key="train.trainexam.exam.mytest.times"/>
			           		   </html:checkbox>
                            </td>
                          </tr>
                       </table>
                       
                    </fieldset>
                    <br>
                </td>
              </tr>
          </table>
          
        </fieldset>
        
      </td>
    </tr>
    <tr>
      <td align="center">
        <input type ='button' value="<bean:message key="button.ok"/>" class="mybutton" onclick='save();'>
        <input type ='button' value="<bean:message key="button.cancel"/>" class="mybutton" onclick='window.close();'>
      </td>
    </tr>
  </table> 
</html:form>
<script>
selcheck();
changeItem();
changeReexam();
changeTimes();
</script>