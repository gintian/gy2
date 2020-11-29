<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/meizzDate.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="javascript">
<%
  String op = request.getParameter("op");
%>

 function rulesave()
 {
   var rule_name=document.getElementById("rule_name").value;   
   if(rule_name=="")
   {
     alert("规则文件名不能为空！");
   }else
   {
       var hashvo=new ParameterSet();
       if(!rule_name.regexStr()){
       		alert("规则文件名只允许字符、下划线、数字、汉字！");
       }else{
       	hashvo.setValue("name",rule_name);
       	var request=new Request({method:'post',asynchronous:false,onSuccess:showExamineRule,functionId:'15211002105'},hashvo);
      }
   }
 }
  function showExamineRule(outparamters)
  {
     var flag=outparamters.getValue("flag");
     var name = outparamters.getValue("name");
     if(flag=="null")
     {
        alert("规则文件名不能为空！");
        return false;
     }else if(flag=="exist")
     {
        alert("规则文件名已存在！");
        return false;
     }else if(flag=="ok")
     {
        //kqRuleDataForm.action="/kq/machine/kq_rule_data.do?b_save=link&tran_flag=2";
        //kqRuleDataForm.target="il_body";
        //kqRuleDataForm.submit();
        var codesetvo=new Object();
        //codesetvo.code = $F("code");
        codesetvo.name = name;
        codesetvo.tran_flag = "2";
        //codesetvo.flag = "1";
	    //codesetvo.codeitemid = "${kqItemForm.codeitemid}";
	    //codesetvo.mes = "${kqItemForm.mes}";
        window.returnValue=codesetvo;
        window.close();
     }
      
  }
</script>
<html:form action="/kq/machine/kq_rule_data">  
<br><br><br>
 <table  width="80%"  border="0" cellspacing="0"  class="DetailTable"  cellpadding="0" align="center"  valign="middle" >   
                     <tr height="20">
	 		                <!--  <td width=1 valign="top" class="tableft1"></td>
	 		               <td width=130 align=center class="tabcenter"><bean:message key="kq.file.rule"/></td>   
	 		               <td width=10 valign="top" class="tabright"></td>
	 		               <td valign="top" class="tabremain" width="300"></td>--> 
	 		               <td  align=center class="TableRow"><bean:message key="kq.file.rule"/></td>       		           	      
                     </tr>                                         
                     <tr>
		                  <td width="100%"  class="framestyle9">
		                       <table>
			                	   <tr>
			                	     <td>
			                	      	&nbsp;<bean:message key="kq.rule.name"/>&nbsp;		                	     
			                	     </td>
			                	     <%if(op.equalsIgnoreCase("edit")) {%>
				                	     <td>
				                	        &nbsp;<html:text name="kqRuleDataForm" property='rule_name' value="${kqRuleDataForm.rule_name}" size="25"  styleClass="text4" /> 
				                	     </td>
			                	     <%} else if(op.equalsIgnoreCase("add")){%>
				                	      <td>
				                	        &nbsp;<html:text name="kqRuleDataForm" property='rule_name'  size="25"  styleClass="text4" /> 
				                	     </td>
			                	     <%} %>
			                	   </tr>
		                	   </table>  
		                  </td>
              	    </tr>
	               	<tr>
			            <td>&nbsp; </td>
			        </tr>
		        
		         <tr>
		            <td  align="center" style="height:35px;">		                
	                    <input type="button" name="btnreturn" value='<bean:message key="button.ok"/>' onclick="rulesave();" class="mybutton">
		                <input type="button" name="btnreturn" value='<bean:message key="button.close"/>' onclick="window.close();" class="mybutton">
		           </td>
		        </tr>
	 </table>                
</html:form>