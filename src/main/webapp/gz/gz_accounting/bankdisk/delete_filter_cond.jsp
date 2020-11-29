<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="Javascript" src="/gz/salary.js"></script>
<script type="text/javascript">
<!--
function select_ok(type,salaryid)
{
  var arr=document.getElementsByName("itemidArray");
  var ids="";
  var num=0;
  if(arr.length==0)
  {
  return;
  }
  for(var i=0;i<arr.length;i++)
  {
        if(arr[i].checked)
        {
            ids+=","+arr[i].value;
            num++;
        }
  }
 
   if(num==0)
   {
       alert(GZ_BANKDISK_INFO1);
       return;
   }
   var obj=new Object();
  obj.ids=ids.substring(1);
  obj.type=type;
   if(parseInt(type)==1)
   {
     if(ifdel())
     {
      var hashVo=new ParameterSet();
      hashVo.setValue("condid",obj.ids);
      hashVo.setValue("salaryid",salaryid);
      var In_parameters="opt=1";
      var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:delete_cond_ok,functionId:'3020100019'},hashVo);
      }
   }
  if(parseInt(type)==2)//open
  {
    if(num>1)
    {
       alert(GZ_BANKDISK_INFO2);
       return;
    }
 
  
  returnValue=obj;
  window.close();
  }
}
function delete_cond_ok(outparameters)
{
  var salaryid=outparameters.getValue("salaryid");
  var theUrl="/gz/gz_accounting/bankdisk/delete_filter_cond.do?b_query=query&salaryid="+salaryid;
  bankDiskForm.action=theUrl;
  bankDiskForm.submit();
}
//-->
</script>
<html:form action="/gz/gz_accounting/bankdisk/delete_filter_cond">
<Br>

<table width='80%' border="0" cellspacing="1"  align="center" cellpadding="1">

<tr>
<td colspan="2" align="center">
<fieldset align="center">
<legend><bean:message key="gz.bankdisk.filtercond"/></legend>
<div style="overflow:auto;width:220px;height:240px;" >
<table width='100%' border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
<thead>
<tr class="TableRow">
<td align="center" nowrap>
<bean:message key="lable.select"/>
</td>
<td align="center" nowrap>
<bean:message key="gz.bankdisk.condname"/>
</td>
</tr>
</thead>
   	  <% int i=0;%>
   	  <logic:iterate id="element" name="bankDiskForm" property="condbeanlist" offset="0">
   	  <% if(i%2==0){%>
   	    <tr class="trShallow">
   	  <%}else{%>
   	   <tr class="trDeep">
   	  <%}%>
   	  <td width="20%" align="center">
   	   <input type="checkbox" name="itemidArray" value="<bean:write name="element" property="condid"/>"/>
   	  </td>
   	  <td align="left">
   	  &nbsp;&nbsp;<bean:write name="element" property="name"/>&nbsp;&nbsp;
   	  </td>
   	  </tr>
   	  <% i++;%>
   	  </logic:iterate>
   <logic:equal name="bankDiskForm" property="condsize" value="0">
   	  <tr class="trShallow">
   	  <td colspan="2" align="center" nowrap>
   	  <bean:message key="gz.bankdisk.nocond"/>
   	  </td>
   	  </tr>
   	  </logic:equal>
   	  </table>
   	  </div>
   	  </fieldset>
   	  </td>
</tr>
</table>
<table width='100%' border="0" cellspacing="1"  align="center" cellpadding="1">
          <tr>
          <td align="center" nowrap>
            
	      <html:button styleClass="mybutton" property="open" onclick="select_ok('2','${bankDiskForm.salaryid}');">
            		     <bean:message key="button.ok"/>
	      </html:button>
	        <html:button styleClass="mybutton" property="ok" onclick="select_ok('1','${bankDiskForm.salaryid}');">
            		     <bean:message key="button.delete"/>
	      </html:button>
	       <input type="button" name="sort" value="<bean:message key="gz.filter.sort"/>" class="mybutton" onclick='filterSort("${bankDiskForm.salaryid}");'>   
	      <input type="button" name="back" value="<bean:message key="button.close"/>" class="mybutton" onclick="window.close();">   
          </td>
          </tr>  
          
</table>
</html:form>