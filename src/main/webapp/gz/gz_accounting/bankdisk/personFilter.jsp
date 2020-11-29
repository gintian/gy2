<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<link href="/css/css1.css" rel="stylesheet" type="text/css">
<hrms:themes />
<script language="Javascript" src="/gz/salary.js"></script>
<%
    String model=(String)request.getParameter("model");
%>


<script type="text/javascript">

var model="${bankDiskForm.model}";//history 表示为薪资历史数据分析进入
<% if("history".equalsIgnoreCase(model)){ %>
model="history";
<%}%>
var value="";
function queryOptions()
{
   var key=window.event.keyCode;
   var obj=document.getElementById("itemid");
   var idvalue=document.getElementById("queryvalue").value;
    if(idvalue=='')
       return;
    if(value!=idvalue)
    {
       if(key!=8&&key!=46)
       {
         for(var i=0;i<obj.options.length;i++)
         {
            if(obj.options[i].value.toUpperCase().indexOf(idvalue.toUpperCase())!=-1||obj.options[i].text.toUpperCase().indexOf(idvalue.toUpperCase())!=-1)
            {
               obj.options[i].selected=true;
            }
            else
            {
               obj.options[i].selected=false;
            }
          }
        }
      value=idvalue;
    }
    
}
//-->
</script>
<base id="mybase" target="_self">
<html:form action="/gz/gz_accountingt/bankdisk/personFilter">
<table width="590px;" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
<THEAD>
<tr>
<td class="TableRow_lrt">
<bean:message key="gz.bankdisk.personfilter"/>
</td>
</tr>
</THEAD>
<tr>
<td class="RecordRow">
<table>
<tr>
<td width="45%">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
                <tr>
                    <td align="left">
                      <bean:message key="gz.bankdisk.preparefield"/>&nbsp;<input type="text" name="query" size="20" id="queryvalue" onkeyup="queryOptions();" class="inputtext"/>
                    </td>
                    </tr>
                   <tr>
                     <td align="center">
                         <hrms:optioncollection name="bankDiskForm" property="allList" collection="list"/>
		              <html:select name="bankDiskForm" size="10" property="itemid" multiple="multiple" styleClass="complex_border_color" ondblclick="additem('itemid','right_fields');" style="height:230px;width:100%;font-size:9pt">
		              <html:options collection="list" property="dataValue" labelProperty="dataName"/>
		        </html:select>		
                  </td>
              </tr>
</table>
</td>
<td width="10%" align="center">
<html:button  styleClass="mybutton" property="b_addfield" onclick="additem('itemid','right_fields');">
            		     <bean:message key="button.setfield.addfield"/> 
	           </html:button >
	           <br>
	           <br>
	           <html:button  styleClass="mybutton" property="b_delfield" onclick="removeitem('right_fields');">
            		     <bean:message key="button.setfield.delfield"/>    
	           </html:button >	     
</td>
<td width="45%">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
                  <tr>
                  <td width="100%" align="left">
                     <bean:message key="gz.bankdisk.selectedfield"/>&nbsp;&nbsp;
                  </td>
                  </tr>
                  <tr>
                  <td width="100%" align="left">
                   <hrms:optioncollection name="bankDiskForm" property="selectedFieldList" collection="list"/>
		              <html:select name="bankDiskForm" size="10" property="right_fields" multiple="multiple" ondblclick="removeitem('right_fields');" style="height:230px;width:100%;font-size:9pt">
		             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
		        </html:select>	
                  </td>
                  </tr>
                  </table> 
</td>
</tr>
</table>
</td>
</tr>
<tr>
<td  class="RecordRowP" nowrap align="center" style="height:35px;">
<input type="button" name="open" class="mybutton" value="<bean:message key="gz.bankdisk.read"/>" onclick="delete_cond('${bankDiskForm.salaryid}');"/>
<input type="button" name="query" class="mybutton" value="<bean:message key="gz.bankdisk.nextstep"/>" onclick="bankdisk_choose('<bean:message key="gz.bankdisk.noreselect"/>');">
	           <input type="button" name="cancel" value="<bean:message key="button.close"/>" class="mybutton" onclick="window.close()"/>
	           <input type="hidden" name="rightFields" value="">
	           <input type="hidden" name="salaryid" value="${bankDiskForm.salaryid}"/>
	           <html:hidden name="bankDiskForm" property="filterCondId"/>
<input type="hidden" name="tableName" value="${bankDiskForm.tableName}"/>
</td>
</tr>
</table>
</html:form>