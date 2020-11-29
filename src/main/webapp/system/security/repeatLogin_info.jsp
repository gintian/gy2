<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.sys.AccountForm" %>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%AccountForm accountForm = (AccountForm) session.getAttribute("accountForm");
  String repeatID = accountForm.getRepeatID();
%>
<script type="text/javascript">
 //保存 
function save(){
	var username = document.getElementsByName('reportUser_vo.string(username)')[0].value;
	var repeatID="<%=repeatID%>";
  	var reg=/^[\\`~!#\$%^&\*()\+\{\}\|:"<>\?\-=/']*$/;
  	for(var i=0;i<username.length;i++){
		 var c=username.substring(i,i+1);
		 if(reg.test(c)){
		 	alert('用户名不能是特殊字符!\n\`~!#$%^&*()+{}|\\:"<>?-=/\'');
		 	return false;
		 }
  	}
  	var hashvo=new ParameterSet(); 
  	hashvo.setValue("username",username);
    hashvo.setValue("repeatID",repeatID);
    var request=new Request({method:'post',asynchronous:false,onSuccess:save_ok,functionId:'1010010116'},hashvo);
}
 //保存的回调方法
function save_ok(outparamters){   
	 var flag=outparamters.getValue("flag");   
	 if(flag=="exist"){	   
		 alert("用户名已存在！");  
		 return false;	
	 }else if(flag=="ok"){		 
		 top.returnValue="true";	     
		 top.close();			   	  
	 }
}
</script>
<html:form action="/system/security/repeatLogin_info">
      <table width="340" border="0" cellpadding="0" cellspacing="0" align="center" class="ftable">
          <tr>
          	<td colspan="2">
          	<hrms:codetoname codeid="UN" name="accountForm" codevalue="reportUser_vo.string(b0110)" codeitem="codeitem"/>  	      
          	<bean:write name="codeitem" property="codename" />&nbsp;
          	<hrms:codetoname codeid="UM" name="accountForm" codevalue="reportUser_vo.string(e0122)" codeitem="codeitem"/>  	      
          	<bean:write name="codeitem" property="codename" />&nbsp;    
          	<hrms:codetoname codeid="@K" name="accountForm" codevalue="reportUser_vo.string(e01a1)" codeitem="codeitem"/>  	      
          	<bean:write name="codeitem" property="codename" />&nbsp;    
          	</td>
          </tr>		
          <tr height="20">
       		<td  align="left" class="TableRow" colspan="2"><bean:write name="accountForm" property="reportUser_vo.string(a0101)" />&nbsp;</td>
          </tr> 
          <tr class="list3">
             <td align="right" nowrap ><bean:message key="label.username"/></td>
             <td align="left" nowrap >
             	 <html:text name="accountForm" property="reportUser_vo.string(username)" size="20" maxlength="${accountForm.userlen}" styleClass="text"/>    	      
             </td>
            </tr>                                  
          <tr class="list3">
            <td align="center" colspan="2" style="height:35px">
         		<html:button styleClass="mybutton" property="" onclick="save();">
                    <bean:message key="button.save"/>
         		</html:button>	
		 		<html:button styleClass="mybutton" property="" onclick="top.close();">
                    <bean:message key="button.close"/>
        		</html:button>		    
            </td>
          </tr>          
      </table>
</html:form>
