<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%@ page import="com.hjsj.hrms.businessobject.sys.SysParamBo,com.hjsj.hrms.businessobject.sys.SysParamConstant"%>

<script type="text/javascript">
<!--

	function passEncode(){
		var pass = document.loginBaseForm.password.value;
		if(pass =='#'){
			/*alert("请选择口令指标!");
			return;*/
			pass='userpassword';
		}
	    if(confirm("是否要进行加密操作?")){
		    	var btns = document.getElementsByTagName("button");
		    	for(var i=0;i<btns.length;i++)
		    		btns[i].disabled = true;
		    	var name = document.loginBaseForm.username.value;
		    	var hashvo=new ParameterSet();
	   	    hashvo.setValue("pass",pass);
	   	    hashvo.setValue("name",name);
	   	    var In_paramters="flag=1"; 	
		    var request=new Request({method:'post',asynchronous:true,parameters:In_paramters,onSuccess:passEncodResult,functionId:'1010010060'},hashvo);
		    jindu('block');
	    }else{
	 	    return;
	    }
	}
	
	function passEncodResult(outparamters){
		jindu('none');
		 var info = outparamters.getValue("info");
		 if(info == "ok"){
		 	alert("加密成功!");
		 	window.location.reload();
		 }else{
		 	alert("加密失败!");
		 }
		 var btns = document.getElementsByTagName("button");
		for(var i=0;i<btns.length;i++)
			btns[i].disabled = false;
		
	}
	
	function passDecode(){
		var pass = document.loginBaseForm.password.value;
		if(pass =='#'){
			/*alert("请选择口令指标!");
			return;
			*/
			pass='userpassword';
		}
		 if(confirm("是否要进行解密操作?")){
			 var btns = document.getElementsByTagName("button");
				for(var i=0;i<btns.length;i++)
					btns[i].disabled = true;
		 	var name = document.loginBaseForm.username.value;
		    var hashvo=new ParameterSet();
		    hashvo.setValue("pass",pass);
		    hashvo.setValue("name",name);
		   	var In_paramters="flag=1"; 	
			var request=new Request({method:'post',asynchronous:true,parameters:In_paramters,onSuccess:passDecodResult,functionId:'1010010062'},hashvo);	
			jindu('block');
	    }else{
	 	    return;
	    }
			}
	
	function passDecodResult(outparamters){
		jindu('none');
		 var info = outparamters.getValue("info");
		 if(info == "ok"){
		 	alert("解密成功!");
		 	window.location.reload();
		 }else{
		 	alert("解密失败!");
		 }
		 
		 var btns = document.getElementsByTagName("button");
			for(var i=0;i<btns.length;i++)
				btns[i].disabled = false;
			
	}
	
	function save(){
		var un = document.loginBaseForm.username.value;
		var ps = document.loginBaseForm.password.value;
		var ip_obj = document.loginBaseForm.ip_addr;
		if(ip_obj==null)
		{
		   if((un ==ps)&& (ps!= '#' && un!='#')){
			alert("指定的用户名和口令指标不能相同!");
			return;
		    }else{
			document.loginBaseForm.action="/system/security/login_username_options.do?b_save=save";
			document.loginBaseForm.submit();
                    }
		}else
		{
		  var ip=ip_obj.value;
		  if((un ==ps||un ==ip||ip ==ps)&& (ps!= '#' && un!='#'&& ip!='#')){
			alert("指定的指标不能相同!");
			return;
		  }else{
			document.loginBaseForm.action="/system/security/login_username_options.do?b_save=save";
			document.loginBaseForm.submit();
		  }
		}
	}
	
	function jindu(block){
	    //新加的，屏蔽整个页面不可操作
	    /*document.all.ly.style.display="block";   
	    document.all.ly.style.width=document.body.clientWidth;   
	    document.all.ly.style.height=document.body.clientHeight; */
	    
	    var waitInfo=eval("wait");
	    waitInfo.style.display=block;
	}
//-->
</script>

<html:form action="/system/security/login_username_options">
  <table width="500" border="0" cellpadding="0" cellspacing="0" align="center" class="ftable">
          <tr height="20">
       		<td align="left" class="TableRow" colspan="2"><bean:message key="label.sys.loginuser.options"/>&nbsp;</td>
          </tr> 
                      <tr>
                	      <td align="right" nowrap class="tdFontcolor"><bean:message key="label.sys.login.username"/></td>
                	      <td align="left" nowrap class="tdFontcolor">
                               <hrms:importgeneraldata showColumn="itemdesc" valueColumn="itemid" flag="true" paraValue="" 
                               sql="select itemid,itemdesc from fielditem where fieldsetid='A01' and itemtype='A' and useflag<>'0' and codesetid='0' order by displayid" collection="list" scope="page"/>
                               <html:select name="loginBaseForm" property="username" size="1" style="width:180px;">
                                  <html:option value="#"><bean:message key="label.select.dot"/></html:option>
                                  <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                               </html:select>&nbsp;
                              </td>
                      </tr>
                      <tr>
                	      <td align="right" nowrap class="tdFontcolor"><bean:message key="label.sys.login.password"/></td>
                	      <td align="left" nowrap class="tdFontcolor">
                               <html:select name="loginBaseForm" property="password" size="1" style="width:180px;">
                                  <html:option value="#"><bean:message key="label.select.dot"/></html:option>
                                  <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                               </html:select>&nbsp;
                              
                              </td>
                      </tr>
                    <hrms:priv func_id="0B4">  
                      <tr>
                	      <td align="right" nowrap class="tdFontcolor">登录系统IP</td>
                	      <td align="left" nowrap class="tdFontcolor">
                               <html:select name="loginBaseForm" property="ip_addr" size="1" style="width:180px;">
                                  <html:option value="#"><bean:message key="label.select.dot"/></html:option>
                                  <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                               </html:select>&nbsp;
                              
                              </td>
                      </tr>
                     </hrms:priv> 
                     <%
                     //String account_logon_interval=SystemConfig.getPropertyValue("account_logon_interval");
                    	 String    account_logon_interval=SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.ACCOUNT_LOGON_INTERVAL);
                    	 String    password_lock_days=SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.PASSWORD_LOCK_DAYS);
                     if(account_logon_interval.length()>0||password_lock_days.length()>0){ %>
                     <hrms:importgeneraldata showColumn="itemdesc" valueColumn="itemid" flag="true" paraValue="" 
                               sql="select itemid,itemdesc from fielditem where fieldsetid='A01' and itemtype='A' and codesetid='45' and useflag<>'0' order by displayid" collection="list1" scope="page"/>
					  <tr>
                	      <td align="right" nowrap class="tdFontcolor">帐号锁定指标</td>
                	      <td align="left" nowrap class="tdFontcolor">
                               <html:select name="loginBaseForm" property="lockfield" size="1"  style="width:180px;">
                                  <html:option value="#"><bean:message key="label.select.dot"/></html:option>
                                  <html:options collection="list1" property="dataValue" labelProperty="dataName"/>
                               </html:select>&nbsp;
                          </td>
                      </tr>
          			<%} %>
          <tr class="list3">
            <td colspan="2" style="height:35px" align="center">
               <!-- 
	               <hrms:submit styleClass="mybutton"  property="b_save">
	                    <bean:message key="button.save"/>
			       </hrms:submit>
			       <input type="button" value=""  >
		       -->
		       
		       
		       <button onclick="save()" class="mybutton"><bean:message key="button.save"/></button>
		        <logic:equal name="loginBaseForm" property="flag" value="hidden">
	           		<button type="button" class="mybutton" onClick="passEncode()">口令加密</button>
	           	</logic:equal>
	           	 <logic:equal name="loginBaseForm" property="flag" value="show">
	           		<button type="button" class="mybutton" onClick="passDecode()">口令解密</button>
	            </logic:equal>
	            <html:reset styleClass="mybutton">
	                    <bean:message key="button.clear"/>
		       </html:reset> 	
            </td>
          </tr>  
  </table>
 
</html:form>
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
