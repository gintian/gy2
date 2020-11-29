<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript">
	
	/**验证2次密码是否一致 wangb  20170916 31639*/
	function valSave(){
		var password = document.getElementsByName('log_pwd')[0].value+'';
		var repassword = document.getElementsByName('log_repwd')[0].value+'';
		
		/* 只验证密码不一致  wangb 20170925 31769
		if(password == ''){
			alert('请输入密码！');
			return false;
		}
		if(repassword == ''){
			alert('请输入确认密码！');
			return false;
		}
		*/
		if(password != repassword ){
			alert('2次密码输入不一致，请重新输入！');
			return false;
		}	
		return true;
	}

</script>

<html:form action="/system/options/stmp_options" onsubmit="return valSave()">
      <table width="500" border="0" cellpadding="0" cellspacing="0" align="center" class="ftable" style="margin-top:7px;">
          <tr height="20">
       		<td align="left" class="TableRow" colspan="2"><bean:message key="label.stmp.server"/>&nbsp;</td>
          </tr> 
                      <tr class="list3">
                	   <td align="right" nowrap ><bean:message key="label.stmp.address"/></td>
                	   <td align="left" nowrap >
                	      	<html:text name="stmpForm" property="stmp_addr" size="40" maxlength="400" styleClass="text"/>    	      
                          </td>
                      </tr>
                       <tr class="list3">
                	   <td align="right" nowrap ><bean:message key="system.options.adport"/></td>
                	   <td align="left" nowrap >
                	      	<html:text name="stmpForm" property="port" size="40" maxlength="10" styleClass="text"/>    	      
                          </td>
                      </tr>
                      <tr class="list3">
                      <!-- 修改表格文字位置不一致   jingq  upd   2014.5.7 -->
                	   <td align="right" nowrap ><bean:message key="label.mail.username"/></td>
                	   <td align="left"  nowrap>
                	      	<html:text name="stmpForm" property="log_user" size="40" maxlength="400" styleClass="text"/>
                          </td>
                      </tr>
                      <tr class="list3">
                	   <td align="right" nowrap ><bean:message key="label.stmp.fromaddr"/></td>
                	   <td align="left" nowrap >
                	      	<html:text name="stmpForm" property="from_addr" size="40" maxlength="400" styleClass="text"/>    	      
                          </td>
                      </tr>
                      <tr class="list3">
                	   <td align="right" nowrap >发件人昵称</td>
                	   <td align="left" nowrap >
                	      	<html:text name="stmpForm" property="sendername" size="40" maxlength="400" styleClass="text"/>
                          </td>
                      </tr>
                      <tr class="list3">
                	      <td align="right" nowrap ><bean:message key="label.mail.password"/></td>
                	      <td align="left"  nowrap>
                	      	<html:password name="stmpForm" property="log_pwd" size="40" maxlength="400" styleClass="text"/>
                          </td>
                      </tr>
                       <!-- 添加确认密码项  wangb 20170916 31639 -->
                      <tr class="list3">
                	      <td align="right" nowrap >确认密码</td>
                	      <td align="left"  nowrap>
                	      	<html:password name="stmpForm" property="log_repwd" size="40" maxlength="400" styleClass="text"/>
                          </td>
                      </tr>
                      <tr class="list3">
                	      <td align="right" nowrap ><bean:message key="lable.smtp.maxsend"/></td>
                	      <td align="left"  nowrap>
                	      	<html:text name="stmpForm" property="maxsend" size="40" maxlength="400" styleClass="text"/>
                          </td>
                      </tr>
                                           
                      <tr class="list3">
                	  <td align="right"  nowrap>
                              <bean:message key="label.mail.authy"/>&nbsp;
                          </td>
                	  <td align="left"  nowrap>                          
				<html:checkbox name="stmpForm" property="authy" value="1"/>       
                          </td>				                   
                      </tr>                       
                  <tr class="list3">
	                  <td align="right"  nowrap>
	                              加密
	                          </td>
	                	  <td align="left"  nowrap>                          
							<html:select property="encryption" name="stmpForm">
							<html:option value="" >不加密</html:option>
							   <html:option value="tls" >TLS</html:option>
							   <html:option value="ssl" >SSL</html:option>
							 <!--   <html:option value="ssl">SSL</html:option> -->
							</html:select>
	                  </td>				                   
                  </tr>
                                                     
          <tr class="list3">
            <td align="center" colspan="2" style="height:35px">
         	<hrms:submit styleClass="mybutton" property="b_save">
            		<bean:message key="button.save"/>
	 	</hrms:submit>
         
            </td>
          </tr>          
      </table>
</html:form>
