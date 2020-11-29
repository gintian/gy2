<%@page import="com.hrms.hjsj.sys.ConstantParamter"%>
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script type="text/javascript" src="/ajax/basic.js"></script>
<style>

</style>
<script language="javascript">  
 function save(){
 if(document.getElementById("time").value == "" || document.getElementById("time").value == null){
 	alert("请设置休息时间!");
 }else if(document.getElementById("time").value >30){
 	alert("最大时间不能超过30分钟!");
 }else{
    var hashvo = new ParameterSet();
    var mail = 0;
    var sms = 0;
    var weixin = 0;
    var enable_arch = 0;
    var disable_exam_learning = 0;
    var dingTalk = 0;
    var speed = 0;
    
    if(document.lessonAnalyseForm.mail.checked)
    	mail = 1;
    if(document.lessonAnalyseForm.sms.checked)
    	sms = 1;
   <%if(StringUtils.isNotEmpty(ConstantParamter.getAttribute("wx", "corpid"))){ %>
    if(document.lessonAnalyseForm.weixin.checked)
    	weixin = 1;
    <%}%> 	
//    if(document.lessonAnalyseForm.enable_arch.checked)
//    	enable_arch = 1;
//    if(document.lessonAnalyseForm.disable_exam_learning.checked)
//    	disable_exam_learning = 1;	

    if(document.lessonAnalyseForm.speed.checked)
    	speed = 1;

    <%if(StringUtils.isNotEmpty(ConstantParamter.getAttribute("DINGTALK", "corpid"))){ %>
    if(document.lessonAnalyseForm.dingTalk.checked)
    	dingTalk = 1;
	<%}%>

	var selectItems = getSelectItems();
	
    hashvo.setValue("time",document.getElementById("time").value);
    hashvo.setValue("mail",mail);
	hashvo.setValue("sms",sms);
	hashvo.setValue("weixin",weixin);
	hashvo.setValue("enable_arch",enable_arch);
	hashvo.setValue("disable_exam_learning",disable_exam_learning);
	hashvo.setValue("template",document.lessonAnalyseForm.template.value);
	hashvo.setValue("speed",speed);
	hashvo.setValue("viewItems",selectItems);
	hashvo.setValue("dingTalk",dingTalk);
    var request=new Request({asynchronous:false,onSuccess:ajaxrefresh,functionId:'2020020402'},hashvo);  
    } 
  }

 function getSelectItems() {
	  var vos= document.getElementById("right");       
	     if(vos.length!=0)
	     {
	        var code_fields=new Array();        
	        for(var i=0;i<vos.length;i++)
	        {
	          var valueS=vos.options[i].value;          
	          code_fields[i]=valueS;
	          for(var j=i+1;j<vos.length;j++)
	          {
	          	if(valueS.toUpperCase()==vos.options[j].value.toUpperCase())
	          	{
	          		alert(SAME_SUBSET_RE_SELECT);
	          		return false;
	          	}
	          }
	        }       
	     }
	    var code_fields= "";        
	    for(var i=0;i<vos.length;i++)
	    {
	      var valueS=vos.options[i].value;          
	      code_fields += valueS + ",";
	    }         

	  return code_fields; 
 }
 
  function ajaxrefresh(outparamters){
    if(outparamters!=null)
    {
      var flag=outparamters.getValue("flag"); 
	    if("ok"==flag)
	    {
	      alert("参数设置成功！");
	    }
	    else
	    {
	      alert("操作失败！");
	    }
    }
  }
 
  function selcheck(){
  	if(!document.lessonAnalyseForm.mail.checked&&!document.lessonAnalyseForm.sms.checked
  		<%if(StringUtils.isNotEmpty(ConstantParamter.getAttribute("wx", "corpid"))){ %>
  	  	  	&&!document.lessonAnalyseForm.weixin.checked
  	  	<%}
  		  if(StringUtils.isNotEmpty(ConstantParamter.getAttribute("DINGTALK", "corpid"))){ %>
	  		&&!document.lessonAnalyseForm.dingTalk.checked
		<%}%>
  		){
  		document.lessonAnalyseForm.template.disabled=true;
  	}else if(document.lessonAnalyseForm.mail.checked||document.lessonAnalyseForm.sms.checked
  		<%if(StringUtils.isNotEmpty(ConstantParamter.getAttribute("wx", "corpid"))){ %>
  			||document.lessonAnalyseForm.weixin.checked
  		<%}
  		 if(StringUtils.isNotEmpty(ConstantParamter.getAttribute("DINGTALK", "corpid"))){ %>
	  		||document.lessonAnalyseForm.dingTalk.checked
		<%}%>
  		){
  		document.lessonAnalyseForm.template.disabled=false;
  	}
  }
    function changeItem(existPro){
	  if(document.lessonAnalyseForm.enable_arch.checked){
		  if("false"==existPro){
			  alert("不存在归档的存储过程(prc_train_score_arch)，该项不允许设置！")
			  document.lessonAnalyseForm.enable_arch.checked=false;
			  return;
		  }
		  document.getElementById("releaseSpan").style.display='block';
	  }else{
		  document.getElementById("releaseSpan").style.display='none';
	  }
  }
</script>

<html:form action="/train/setparam/lessonplan">
    <table width="80%" height="260" border="0" style="margin-top: 10px;" cellspacing="0" cellpadding="0" align="center">
    <tr>
    	<td align="center" valign="top">       
    	<fieldset style="width:550px;padding-bottom: 5px;">
          <legend>
            &nbsp;我的课程指标显示&nbsp;
          </legend>
    		<table border="0" cellpadding="0" cellspacing="0">
                <tr>
                 <td align="center"  width="41%">
                   <table align="center" width="100%">
                    <tr>
                    <td align="left">
		            	<bean:message key="gz.bankdisk.preparefield" />&nbsp;&nbsp;
                    </td>
                    </tr>                   
                   <tr>
                    <td align="center">
                      <hrms:optioncollection name="lessonAnalyseForm" property="fieldItemList" collection="list"/> 
     	              <html:select property="left_fields" size="10" multiple="true"
							style="height:230px;width:100%;font-size:9pt"
							ondblclick="additem('left_fields','right_fields');removeitem('left_fields');">
							<html:options collection="list" property="dataValue"
								labelProperty="dataName" />
						</html:select>
                    </td>
                    
                    </tr>
                   
                   </table>
                </td>
               
                <td width="8%" align="center">
                   <html:button  styleClass="mybutton" property="b_addfield" onclick="additem('left_fields','right_fields');removeitem('left_fields');">
            		     <bean:message key="button.setfield.addfield"/> 
	           </html:button >
	           <br>
	           <br>
	           <html:button  styleClass="mybutton" property="b_delfield" onclick="additem('right_fields','left_fields');removeitem('right_fields');">
            		     <bean:message key="button.setfield.delfield"/>    
	           </html:button >	     
                </td>         
                
                
                <td width="41%" align="center">
                 
                 
                 <table width="100%" >
                  <tr>
                  <td width="100%" align="left">
		            	<bean:message key="static.ytarget" />&nbsp;&nbsp;
                  </td>
                  </tr>
                  <tr>
                  <td width="100%" align="left" style="border-right:1px solid #696969;">
                  <hrms:optioncollection name="lessonAnalyseForm" property="viewItemList" collection="viewItemList"/> 
     	           <html:select property="right_fields" size="10" multiple="true"
						style="height:230px;width:100%;font-size:9pt" styleId="right"
						ondblclick="additem('right_fields','left_fields');removeitem('right_fields');">
						<html:options collection="viewItemList" property="dataValue"
							labelProperty="dataName" />
					</html:select>   
                  </td>
                  </tr>
                  </table>             
                </td>
                <td width="8%" align="center">
							<html:button styleClass="mybutton" property="b_up" onclick="upItem($('right_fields'));">
								<bean:message key="button.previous" />
							</html:button>
							<br>
							<br>
							<html:button styleClass="mybutton" property="b_down" onclick="downItem($('right_fields'));">
								<bean:message key="button.next" />
							</html:button>
						</td>                              
                </tr>
              </table> 
           </fieldset>
    	</td>
    </tr>
    <tr>
      <td align="center" valign="bottom">       
        <fieldset style="width:550px;">
          <legend>
            &nbsp;学习进度提醒&nbsp;
          </legend>
          
          <table cellspacing="0" cellpadding="0" border="0">
            <tr>
              <td height='20'>&nbsp;</td>
            </tr>
            <tr>
              <td align="center" height="35">
                <html:checkbox name="lessonAnalyseForm" property="mail" value="1" onclick="selcheck();">
                  <bean:message key="train.message.email"/>
                </html:checkbox>
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <html:checkbox name="lessonAnalyseForm" property="sms" value="1" onclick="selcheck();">
                  <bean:message key="train.message.sms"/>
                </html:checkbox>
                <%if(StringUtils.isNotEmpty(ConstantParamter.getAttribute("wx", "corpid"))){ %>
                	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                <html:checkbox name="lessonAnalyseForm" property="weixin" value="1" onclick="selcheck();">
	                  	微信
	                </html:checkbox>
	            <%} %>
	            <%if(StringUtils.isNotEmpty(ConstantParamter.getAttribute("DINGTALK", "corpid"))){ %>
                	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                <html:checkbox name="lessonAnalyseForm" property="dingTalk" value="1" onclick="selcheck();">
	                  	钉钉
	                </html:checkbox>
	            <%} %>
              </td>            
            </tr> 
            <tr>
              <td height="25">
                &nbsp;<bean:message key="train.message.template" />
                <html:select name="lessonAnalyseForm" property="template" size="1" style="width:210px;vertical-align: text-top;">
                  <html:option value=""></html:option>
                  <html:optionsCollection name="lessonAnalyseForm" property="itemlist" label="dataName" value="dataValue"/>
                </html:select> 
              </td>
            </tr>
            <tr>
              <td height='30'>&nbsp;</td>
            </tr>      
          </table>
         </fieldset>
      </td>
    </tr>
    <tr>
      <td align="center" valign="bottom">       
        <fieldset style="width:550px;">
          <legend>
            &nbsp;休息时间提醒&nbsp;
          </legend>
          
          <table cellspacing="0" cellpadding="0" border="0">
            <tr>
              <td height='20'>&nbsp;</td>
            </tr>
            <tr>
              <td height="25">
                &nbsp;当连续播放时间超过
                	<input type="text" class="TEXT6" name="time" onpropertychange='if(/[^\d*]/.test(this.value)) this.value=this.value.replace(/[^\d*]/,"")' value="<bean:write name='lessonAnalyseForm' property='restTime' />"/>&nbsp;分钟时，弹出提示窗口!
              </td>
            </tr>
            <tr>
              <td height='25'>&nbsp;</td>
            </tr>      
          </table>
         </fieldset>
      </td>
    </tr>
    <!-- 自学考试归档是通过存储过程归档的，目前是个性化需求，暂时不放开  chenxg 2017-04-28
     <tr>
      <td align="center" valign="bottom">       
        <fieldset style="width:450px;height: 60px;">
          <legend>
            &nbsp;自学考试&nbsp;
          </legend>
          <table cellspacing="0" cellpadding="0" style="width: 85%" border="0">
            <tr>
 			<td height="30" valign="middle">            
                <html:checkbox name="lessonAnalyseForm" property="enable_arch" value="1" onclick="changeItem('${lessonAnalyseForm.existPro }')" >
                 	自考及格允许归档
                </html:checkbox>
            </td>
            <td valign="middle" style="margin-left:30px"> 
                <span style="display:none" id="releaseSpan" >
                <html:checkbox name="lessonAnalyseForm" property="disable_exam_learning" value="1"  >
                  	 课程未学完毕不允许自考
                </html:checkbox>
                 </span>
            </td>            
            </tr> 
          </table>
         </fieldset>
      </td>
    </tr>
             -->
      <tr>
      <td align="center" valign="bottom" style="padding-top: 5px;">       
        <fieldset style="width:550px;padding-bottom: 5px;">
          <legend>
            &nbsp;多媒体播放设置&nbsp;
          </legend>
          
          <table cellspacing="0" cellpadding="0" border="0">
            <tr>
              <td height="25" align="left">
              	<html:checkbox name="lessonAnalyseForm" property="speed" value="1">
                  	学习中不允许拖动快进（仅支持red5）！
                </html:checkbox>
              </td>
            </tr>
          </table>
         </fieldset>
      </td>
    </tr>
     <tr>
      <td align="center">
        <input type ='button' value="<bean:message key="button.ok"/>"  onclick="save();" class="mybutton">
      </td>
    </tr>
  </table> 
</html:form>
<script>
selcheck();
//changeItem('${lessonAnalyseForm.existPro }');
</script>