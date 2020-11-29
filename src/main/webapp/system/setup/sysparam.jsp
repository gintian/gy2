<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView" %>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<style type="text/css">
body {
	
	margin:0px;
}
</style>
<script type="text/javascript">
function IsDigit() 
{ 
    return ((event.keyCode >= 47) && (event.keyCode <= 57)); 
} 
</script>

<html:form action="/sysconfig/param/sysparam"  onsubmit="return validate();">
<table width="80%" border="0" cellspacing="0" style="margin-top:10px;" align="center" cellpadding="0" class="RecordRow">
   	  <thead>
           <tr>
            <td align="left" class="TableRow" colspan="2" style="border-right:none;">
		帐号规则&nbsp;&nbsp;<html:hidden name="systemParamForm" property="module"/>
            </td>            	        	        	        
           </tr>
   	  </thead> 
   	  <tr>
         <td align="right"  width="36%" class="RecordRow" nowrap="nowrap">
           登录是否显示验证码&nbsp;
         </td>
         <td width="64%" align="left" class="RecordRow" nowrap="nowrap" style="border-right:none;">
         	 <table cellspacing=0>
         	  <tr nowrap><td>
         	  <html:select name="systemParamForm" property="paramMap(validatecode)" size="1" onchange="validateItemsShow()">
                   <html:option value="false">否</html:option>
                   <html:option value="true">是</html:option>
              </html:select>&nbsp;
              </td><td>
              <div id="validatecodeItem">
              <html:radio name="systemParamForm" property="paramMap(validatecode_type)" value="0" onclick="checkselect(this)">图片验证</html:radio>
              <html:radio name="systemParamForm" property="paramMap(validatecode_type)" value="1" onclick="checkselect(this)">短信验证</html:radio>
              &nbsp; 有效期<html:text styleId="effectsecond" name="systemParamForm" property="paramMap(validatecode_effect_second)" size="5" onkeypress="event.returnValue=IsDigit();"  onblur="_onblur(this);" styleClass="text4"/>秒
              </div>
              </td></tr>
         	 </table>
         </td>  
      </tr>
       <tr>
         <td align="right"  width="36%" class="RecordRow" nowrap="nowrap">
           登录是否显示忘记密码&nbsp;
         </td>
         <td width="64%" align="left" class="RecordRow" nowrap="nowrap" style="border-right:none;">
              <html:select name="systemParamForm" property="paramMap(retrieving_password)" size="1">
                   <html:option value="false">否</html:option>
                   <html:option value="true">是</html:option>
              </html:select>&nbsp;       
         </td>  
      </tr> 
       <tr>
         <td align="right"  width="36%" class="RecordRow" nowrap="nowrap">
           登录密码传输类型&nbsp;
         </td>
         <td width="64%" align="left" class="RecordRow" nowrap="nowrap" style="border-right:none;">
              <html:select name="systemParamForm" property="paramMap(password_trans_encrypt)" size="1">
                   <html:option value="false">明文</html:option>
                   <html:option value="true">加密</html:option>
              </html:select>&nbsp;       
         </td>  
      </tr>
      <tr>
         <td align="right"  width="36%" class="RecordRow" nowrap="nowrap">
             密码复杂度&nbsp;
         </td>
         <td width="64%" align="left" class="RecordRow" nowrap="nowrap" style="border-right:none;">
              <html:select name="systemParamForm" property="paramMap(passwordrule)" onclick="changeView(this,'tr_passwordlength')" size="1">
                   <html:option value="0">低</html:option>
                   <html:option value="1">中</html:option>
                   <html:option value="2">高</html:option>
              </html:select>&nbsp;“低”不做限制；“中”必须含字母、数字；“高”必须含字母、数字、特殊符号，且不能重复     
         </td>  
      </tr>
       <tr id="tr_passwordlength" style="display: none">
         <td align="right"  width="36%" class="RecordRow" nowrap="nowrap">
             密码最小长度&nbsp;
         </td>
         <td width="64%" align="left" class="RecordRow" nowrap="nowrap" style="border-right:none;">
              <html:text name="systemParamForm" property="paramMap(passwordlength)" size="5" onkeypress="event.returnValue=IsDigit();" onblur="_onblur(this);" styleClass="text4"/>
              输入为整数值，如果密码复杂度为“中”或“高”密码长度默认空则为8位    
         </td>  
      </tr>
      <tr>
         <td align="right"  width="36%" class="RecordRow" nowrap="nowrap">
             首次密码必须修改&nbsp;
         </td>
         <td width="64%" align="left" class="RecordRow" nowrap="nowrap" style="border-right:none;">
              <html:select name="systemParamForm" property="paramMap(login_first_chang_pwd)" size="1">
                   <html:option value="0">否</html:option>
                   <html:option value="1">是</html:option>
              </html:select>&nbsp;       
         </td>  
      </tr> 
      <tr>
         <td align="right"  width="36%" class="RecordRow" nowrap="nowrap">
            重新设置的新密码不能和前N次密码相同&nbsp;
         </td>
         <td width="64%" align="left" class="RecordRow" nowrap="nowrap" style="border-right:none;">
              <html:text name="systemParamForm" style="ime-mode:disabled;" property="paramMap(login_history_pwd)" size="5" onkeypress="event.returnValue=IsDigit();" styleClass="text4" onblur="_onblur(this);" onkeyup="this.value=this.value.replace(/\D/g,'')"/>
              输入为整数值，默认不做控制        
         </td>  
      </tr>	  
      <tr>
         <td align="right"  width="36%" class="RecordRow" nowrap="nowrap" style="border-right:none;">
            设置密码
         </td>
         <td width="64%" align="left" class="RecordRow" nowrap="nowrap" style="border-right:none;border-left:none;">
              <html:text name="systemParamForm" style="ime-mode:disabled;" property="paramMap(passworddays)" size="5" onkeypress="event.returnValue=IsDigit();" onblur="_onblur(this);" styleClass="text4" onkeyup="this.value=this.value.replace(/\D/g,'')"/>
              天后发邮件提醒用户更改密码
         </td> 
      </tr> 
      <tr>
         <td align="right"  width="36%" class="RecordRow" nowrap="nowrap" style="border-right:none;">
            设置密码
         </td>
         <td width="64%" align="left" class="RecordRow" nowrap="nowrap" style="border-right:none;border-left:none;">
              <html:text name="systemParamForm" style="ime-mode:disabled;" property="paramMap(password_lock_days)" size="5" onkeypress="event.returnValue=IsDigit();" onblur="_onblur(this);" styleClass="text4" onkeyup="this.value=this.value.replace(/\D/g,'')"/>
              天后锁定帐号（说明：0或空，表示密码永不过期。单位：天）       
         </td>  
      </tr>
      <tr>
      <td align="center" colspan="2" class="RecordRow" nowrap="nowrap" style="border-right:none;">
             在&nbsp;<html:text name="systemParamForm" style="ime-mode:disabled;" property="paramMap(account_logon_interval)" size="5" onkeypress="event.returnValue=IsDigit();" onblur="onleave(this);" styleClass="text4" onkeyup="this.value=this.value.replace(/\D/g,'')"/>&nbsp;分钟之内，连续登录错误&nbsp;<html:text name="systemParamForm" style="ime-mode:disabled;" property="paramMap(account_logon_failedcount)" size="5" onkeypress="event.returnValue=IsDigit();" onblur="_onblur(this);" styleClass="text4" onkeyup="this.value=this.value.replace(/\D/g,'')"/>&nbsp;次，帐号将被锁定
         &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
         </td>
      </tr>
      <tr>
         <td align="right"  width="36%" class="RecordRow" nowrap="nowrap">
             同帐号同一时间点仅允许登录一次&nbsp;
         </td>
         <td width="64%" align="left" class="RecordRow" nowrap="nowrap" style="border-right:none;">
              <html:select name="systemParamForm" property="paramMap(only_logon_one)" size="1">
                   <html:option value="false">否</html:option>
                   <html:option value="true">是</html:option>
              </html:select>&nbsp;       
         </td>  
      </tr>
      <tr>
         <td align="right"  width="36%" class="RecordRow" nowrap="nowrap">
         帐号密码加密&nbsp;
         </td>
         <td width="64%" align="left" class="RecordRow" nowrap="nowrap" style="border-right:none;">
              <html:select name="systemParamForm" property="encryPwd" size="1">
                   <html:option value="0">否</html:option>
                   <html:option value="1">是</html:option>
              </html:select>&nbsp;       
         </td>  
      </tr>
      <tr>
         <td align="right"  width="36%" class="RecordRow" nowrap="nowrap">
        登录启用U盾验证&nbsp;
         </td>
         <td width="64%" align="left" class="RecordRow" nowrap="nowrap" style="border-right:none;">
              <html:select name="systemParamForm" property="paramMap(enbleUsbControl)" size="1">
                   <html:option value="false">否</html:option>
                   <html:option value="true">是</html:option>
              </html:select>&nbsp;       
         </td>  
      </tr>
      <tr>
               <td align="center" class="RecordRow" nowrap style="height: 35px;border-right:none;"  colspan="3">
                <button onclick="bsubmit();" class="mybutton"><bean:message key="button.save"/></button>
              </td>
          </tr>   
</table>
</html:form>
<div id="ly" style="position:absolute;top:0px;FILTER:alpha(opacity=0);background-color:#FFF;z-index:2;left:0px;display:none;"></div> 
<div id='wait' style='position:absolute;top:285;left:280;display:none;'>
        <table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
            <tr>
                <td class="td_style" height=24>
                    <bean:message key='org.autostatic.mainp.calculation.wait'/>
                </td>
            </tr>
            <tr>
                <td style="font-size:12px;line-height:200%" align=center>
                    <marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10">
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
    </div>
<script type="text/javascript">
//<!--
var passwordrule=document.getElementsByName("paramMap(passwordrule)")[0].value;
if(passwordrule=="1"||passwordrule=="2"){
    document.getElementById("tr_passwordlength").style.display="block";
}

function changeView(obj,trid){
	var _value = obj.value;
	if(_value=="true"||_value=="1"||_value=="2"){
	    document.getElementById(trid).style.display="block";
	}
	if(_value=="false"||_value=="0"){
        document.getElementById(trid).style.display="none";
    }
}

function onleave(obj){
	if(obj.value>0){
		document.getElementById("paramMap(account_logon_failedcount)").value=5;
	}else{
		document.getElementById("paramMap(account_logon_failedcount)").value="";
		obj.value='';
	}
}

function _onblur(obj){
	if(obj.value==0)
		obj.value='';
		
}

function bsubmit(){
	jindu();
	//将按钮置为不可用状态，防止二次操作
	var btns = document.getElementsByTagName("button");
	for(var i=0;i<btns.length;i++)
		btns[i].disabled = true;
    systemParamForm.action="/sysconfig/param/sysparam.do?b_save=link&encryptParam=<%=PubFunc.encrypt("frompath=/sysconfig/param/sysparam.do?b_sys_param=link")%>";
    systemParamForm.submit();
}

function jindu(){
    //新加的，屏蔽整个页面不可操作
    /*document.all.ly.style.display="block";   
    document.all.ly.style.width=document.body.clientWidth;   
    document.all.ly.style.height=document.body.clientHeight; */
    
    var waitInfo=eval("wait");
    waitInfo.style.display="block";
}

function checkselect(ele){
	if(ele.value==0){
		document.getElementById("effectsecond").disabled = true;
	}else
		document.getElementById("effectsecond").disabled = false;
}
/*xus 17-6-5 初始化有效期*/
function  initpar(){
	validity=document.getElementsByName("paramMap(validatecode_effect_second)")[0].value;
	if(validity==""){
		document.getElementById("paramMap(validatecode_effect_second)").value=60;
	}
	
	var numbe=document.getElementsByName("paramMap(validatecode_type)")[0];
	if(numbe.checked){
		document.getElementById("effectsecond").disabled = true;
	}else
		document.getElementById("effectsecond").disabled = false;
	//初始化登录验证码子集信息
	validateItemsShow();
}
/*xus 17-8-18 登录验证码子集信息是否显示*/
function validateItemsShow(){
	var index=document.getElementsByName("paramMap(validatecode)")[0].options.selectedIndex;
	if(index==1){
		document.getElementById("validatecodeItem").style.display="block";
	}else{
		document.getElementById("validatecodeItem").style.display="none";
	}
}
initpar();
//-->
</script>
