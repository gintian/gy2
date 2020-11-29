<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<hrms:themes></hrms:themes>
<script language="javascript">

  function saves()
  {
    var tas=$F('other_name');    
    if(tas==null||tas.length<=0)
    {
       alert("输入名称不能为空！");
       return false;
     }else if(tas.indexOf("\"")!=-1||tas.indexOf("\'")!=-1||tas.indexOf(",")!=-1){
     	alert("请不要粘贴特殊符号");
     	return false;
     }else{         
       var hashvo=new ParameterSet(); 
       hashvo.setValue("other_name",tas);       
       var request=new Request({method:'post',onSuccess:setSelect,functionId:'1010010054'},hashvo);    
       
     }
   }
   function setSelect(outparamters)
  {
    var codesetvo=new Object();   
    codesetvo.name = outparamters.getValue("other_name");   
    var flag = outparamters.getValue("flag");   
    if(flag=="2")
    {
      alert("名称有重复，请重新命名！");
      return false;
    }
    	parent.parent.other_save_success(codesetvo);
  }  
  
   function closeWin(){
	    	parent.parent.closeWin();
	   
   }
   
function estop()
{
	return event.keyCode!=34&&event.keyCode!=39&&event.keyCode!=44;
}
         
  </script>
<html:form action="/system/security/rolesearch">
<table width="290" border="0" cellpadding="0" cellspacing="0" align="center">
    <tr height="20">
      <!--  <td width=10 valign="top" class="tableft"></td>
      <td width=130 align=center class="tabcenter">
      角色另存为
      
      </td>
      <td width=10 valign="top" class="tabright"></td>
      <td valign="top" class="tabremain" width="250"></td> -->
     </tr> 
    <tr>
     <td width="80%">
      <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">     
      	
          <tr class="list3">
              <td align="right" nowrap height="40" width="30%">角色名称</td>
                   
             <td align="left"  nowrap valign="center" style="padding-left:5px;">
           	 <html:text name="sysForm" property="other_name" value="" maxlength="20" onkeypress="event.returnValue=estop(this)" styleClass="text4" style="width:180px;"/>
              
             </td>
            </tr>
                    
          </table>     
        </td>
       </tr>                                                     
       <tr class="list3">
         <td align="center" colspan="2" style="height:35px;">
	        <input type="button" name="br_return" value="<bean:message key="button.save"/>" class="mybutton" onclick="javascript:saves();">     
	       <input type="button" name="br_return" value="<bean:message key="button.cancel"/>" class="mybutton" onclick="javascript:closeWin();">     
        </td>
       </tr>          
    </table>
</html:form>
