
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
  String returnvalue=(String)request.getParameter("returnvalue");
  
 %>
<script language="javascript">
  function showfile(type,flag)
  {
      var hashvo=new ParameterSet();
      hashvo.setValue("type",type);
      hashvo.setValue("flag",flag);
      var request=new Request({method:'post',onSuccess:getFile,functionId:'9078006040'},hashvo);
  }
  function getFile(outparamters)
  {
      var file=outparamters.getValue("file");  
      var flag=outparamters.getValue("flag");  
      var type=outparamters.getValue("type"); 
      if(file=="yes")
      {
         policeForm.action ="/pos/police/work_file?flag="+flag+"&type="+type;
	      policeForm.submit();
      }else
      {
         if(flag=="1")
            alert("没有上传分监区工作流程文件！");
         else if(flag=="2")
            alert("没有上传独立工作环节文件！");
      }
  }
  function returnTO()
  {
     window.location="/templates/attestation/police/wizard.do?br_work_wizard=link";
  }
</script>
<html:form action="/pos/police/work_system">
<br>
<br>
<br>

<table border="0" id="PanelTable" width="400" height="0" cellspacing="5" cellpadding="0" align="center" class="framestyle0">
  <caption align="center" class="captionheader">工作制度</caption>
  <tr>
    <hrms:priv func_id="570101">  
    <td align="center" width="33%">
      <a href="javascript:showfile('police','1');">
        <img src="/images/jgbm.gif" border="0" width="40" height="40" />
      </a>
    </td>
    </hrms:priv>
     <hrms:priv func_id="570102">  
    <td align="center">
      <a href="javascript:showfile('police','2');">
        <img src="/images/card.gif" border="0" width="40" height="40" />
      </a>
    </td>
    </hrms:priv>
  </tr>
  <tr>
    <hrms:priv func_id="570101">  
    <td align="center" valign="top">
      <a href="javascript:showfile('police','1');">分监区工作流程</a>
    </td>
    </hrms:priv>
     <hrms:priv func_id="570102">  
    <td align="center" valign="top">
      <a href="javascript:showfile('police','2');"">独立工作环节</a>
    </td>
    </hrms:priv>
  </tr>  
</table>
<table  border="0"  width="400" height="0" cellspacing="5" cellpadding="0" align="center">
  <tr>
   <td align="center">
      <%
      if(returnvalue!=null&&returnvalue.equalsIgnoreCase("police"))      
      { 
         out.print("<input type='button' name='b_save' value='返回' onclick='returnTO();' class='mybutton'>");
      }
      %>
      
   </td>
  </tr>
</table>
</html:form>
