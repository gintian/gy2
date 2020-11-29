<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<script language="javascript">

  function saves()
  {
    var tas=trim($F('class_name'));    
    if(tas==null||tas.length<=0)
    {
       alert("输入值不能为空！");
       return false;
     }else{         
       var hashvo=new ParameterSet(); 
       hashvo.setValue("class_name",tas);
       hashvo.setValue("class_flag","${kqclassForm.class_flag}");
       hashvo.setValue("class_id","${kqclassForm.class_id}");
       var request=new Request({method:'post',onSuccess:setSelect,functionId:'15211000004'},hashvo);    
       //kqclassForm.action="/kq/options/class/kq_class_tree.do?b_save=link";
       //kqclassForm.target="il_body";       
       //kqclassForm.submit();
     }
   }
   function setSelect(outparamters)
  {
    //window.opener.parent.location.href="/kq/options/class/kq_class.do";
    var codesetvo=new Object();
    codesetvo.class_id = outparamters.getValue("class_id");
    codesetvo.name = outparamters.getValue("class_name");
    codesetvo.class_flag = outparamters.getValue("class_flag");
    window.returnValue=codesetvo;
    window.close();
  }  
         
  </script>
<html:form action="/kq/options/class/kq_class_tree">
<div class="fixedDiv3">
  <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" class="ftable">
    <tr height="20">
      <!--  <td width=10 valign="top" class="tableft"></td>
      <td width=130 align=center class="tabcenter">
      <logic:equal name="kqclassForm" property="class_flag" value="add"> 
         <bean:message key="kq.class.edti.add"/>   
      </logic:equal>
      <logic:equal name="kqclassForm" property="class_flag" value="up"> 
         <bean:message key="kq.class.edti.update"/>   
       </logic:equal>
      
      </td>
      <td width=10 valign="top" class="tabright"></td>
      <td valign="top" class="tabremain" width="250"></td> -->  
      <td  align=center colspan="2" class="TableRow">
      <logic:equal name="kqclassForm" property="class_flag" value="add"> 
         <bean:message key="kq.class.edti.add"/>   
      </logic:equal>
      <logic:equal name="kqclassForm" property="class_flag" value="up"> 
         <bean:message key="kq.class.edti.update"/>   
       </logic:equal>
      
      </td>           	      
     </tr> 
    <tr>
              <td align="right" nowrap height="40" width="30%"><bean:message key="kq.class.name"/></td>
                   
             <td align="left"  nowrap valign="center">
           	 <html:text name="kqclassForm" property="class_name" styleClass="inputtext" maxlength="20"/>    	      
                 <html:hidden name="kqclassForm" property="class_flag"/> 
                 <html:hidden name="kqclassForm" property="class_id"/> 
             </td>
            </tr>
                                                                         
       <tr>
         <td align="center" colspan="2" style="height:35px;border: none">
	        <input type="button" name="br_return" value="<bean:message key="button.save"/>" class="mybutton" onclick="javascript:saves();">     
	       <input type="button" name="br_return" value="<bean:message key="button.cancel"/>" class="mybutton" onclick="window.close();;">     
        </td>
       </tr>          
    </table>
    </div>
</html:form>