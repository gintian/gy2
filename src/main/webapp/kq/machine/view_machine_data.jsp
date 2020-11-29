<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript">

</script>

<html:form action="/kq/machine/search_card_data">   
<div class="fixedDiv3">
  <table width="100%" border="0" cellpadding="1" cellspacing="0" align="center">
   <tr height="20">
   <!--   <td width=10 valign="top" class="tableft"></td>
    <td width=130 align=center class="tabcenter">
     <bean:message key="kq.machine.message"/></td>
    <td width=10 valign="top" class="tabright"></td>
    <td valign="top" class="tabremain" width="500"></td> --> 
    <td  align=center class="TableRow">
     <bean:message key="kq.machine.message"/></td>            	      
   </tr> 
   <tr>
      <td  class="framestyle9">
         <table border="0" cellpmoding="0" cellspacing="5"  class="DetailTable"  cellpadding="0" >   
                <tr><td height="10"></td><td></td></tr>   
                <tr>
                  <td width="30%" align="right">
                    <bean:message key="kq.machine.name"/>&nbsp;&nbsp;&nbsp;&nbsp;
                  </td>
                  <td  align="left">                  
                  <bean:write name="kqCardDataForm" property="machine.string(name)" filter="true"/> 
                  </td>
                </tr>
                <tr>
                  <td width="30%" align="right">
                    <bean:message key="kq.machine.type_id"/>&nbsp;&nbsp;&nbsp;&nbsp;
                  </td>
                  <td  align="left">
                   <logic:equal name="kqCardDataForm" property="machine.string(type_id)" value="1">
                       科密326                   
                   </logic:equal>
                   <logic:equal name="kqCardDataForm" property="machine.string(type_id)" value="2">
                       舒特系列                     
                   </logic:equal>
                   <logic:equal name="kqCardDataForm" property="machine.string(type_id)" value="3">
                       舒特10位                    
                   </logic:equal>
                   <logic:equal name="kqCardDataForm" property="machine.string(type_id)" value="4">
                       华达拉斯485                   
                   </logic:equal>
                   <logic:equal name="kqCardDataForm" property="machine.string(type_id)" value="5">
                      中控指纹机                   
                   </logic:equal>
                  </td>
                </tr>
                <tr>
                  <td width="30%" align="right">
                    出入类型&nbsp;&nbsp;&nbsp;&nbsp;
                  </td>
                  <td  align="left">
                  <logic:equal name="kqCardDataForm" property="machine.string(inout_flag)" value="-1">
                    出
                   </logic:equal>
                   <logic:equal name="kqCardDataForm" property="machine.string(inout_flag)" value="0">
                   不限
                   </logic:equal>
                   <logic:equal name="kqCardDataForm" property="machine.string(inout_flag)" value="1">
                   进
                   </logic:equal>	            
                  </td>
                </tr>
                <tr>
                  <td width="30%" align="right">
                    <bean:message key="kq.machine.location"/>&nbsp;&nbsp;&nbsp;&nbsp;
                  </td>
                  <td  align="left">                 
                  <bean:write name="kqCardDataForm" property="machine.string(location)" filter="true"/>  
                  </td>
                </tr>
                <tr>
                  <td width="30%" align="right">
                    <bean:message key="kq.machine.port"/>&nbsp;&nbsp;&nbsp;&nbsp;
                  </td>
                  <td  align="left">
                   <bean:write name="kqCardDataForm" property="machine.string(port)" filter="true"/>                  
                  </td>
                </tr>
                <tr>
                  <td width="30%" align="right">
                    <bean:message key="kq.machine.baud_rate"/>&nbsp;&nbsp;&nbsp;&nbsp;
                  </td>
                  <td  align="left">
                   <bean:write name="kqCardDataForm" property="machine.string(baud_rate)" filter="true"/>                  
                  </td>
                </tr>
                <tr>
                  <td width="30%" align="right">
                    <bean:message key="kq.machine.ip_address"/>&nbsp;&nbsp;&nbsp;&nbsp;
                  </td>
                  <td  align="left">
                  <bean:write name="kqCardDataForm" property="machine.string(ip_address)" filter="true"/>
                 
                  </td>
                </tr>
                <tr>
                  <td width="30%" align="right">
                    <bean:message key="kq.machine.description"/>&nbsp;&nbsp;&nbsp;&nbsp;
                  </td>
                  <td  align="left">
                  <bean:write name="kqCardDataForm" property="machine.string(description)" filter="true"/>                   
                  </td>
                </tr>
      </td>
   </tr>
   </table>
   </td>
   </tr>
   </table>  
      <table width="400" border="0" cellpadding="1" cellspacing="0" align="center">
     <tr>
        <td align="center" style="height:35px;"><br>           
	    <input type="button" name="btnreturn" value='关闭' onclick="window.close();" class="mybutton">						       
        </td>
     </tr>
   </table>
   </div>
</html:form>
