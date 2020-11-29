<%@ page contentType="text/html; charset=UTF-8" language="java"%>
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
    var hashvo = new ParameterSet();
    hashvo.setValue("budget",document.getElementById("budget").value);
    var request=new Request({asynchronous:false,onSuccess:ajaxrefresh,functionId:'2020020403'},hashvo);   
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
</script>

<html:form action="/train/setparam/lessonplan">
    <table width="80%" height="250" border="0" cellspacing="0" cellpadding="0" align="center">
    <tr>
      <td align="center" valign="bottom">       
        <fieldset style="width:450px;">
          <legend>
            &nbsp;预算费用指标&nbsp;
          </legend>
          <table cellspacing="0" cellpadding="0" border="0">
            <tr>
              <td height='20'>&nbsp;</td>
            </tr>
            <tr>
              <td height="40">
                &nbsp;剩余费用指标
                <html:select name="setParamForm" property="budget" styleId="budget" size="1" style="width:210px;vertical-align: text-top;">
                  <html:option value=""></html:option>
                  <html:optionsCollection name="setParamForm" property="itemlist" label="itemdesc" value="itemid"/>
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
      <td align="center">
        <input type ='button' value="<bean:message key="button.ok"/>"  onclick="save();" class="mybutton">
      </td>
    </tr>
  </table> 
</html:form>