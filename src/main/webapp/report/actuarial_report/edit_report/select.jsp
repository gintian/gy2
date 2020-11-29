<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>


<html>
<hrms:themes />
<br><br>
<table width="90%" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr height="20">
       		<!--  <td width="8" valign="top" class="tableft"></td>
       		
          <td width="131" align=center class="tabcenter">导出说明</td>
       		<td width="8" valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="69%"></td>--> 
       		<td align=center class="TableRow">导出说明</td>             	      
          </tr> 
          <tr>
            <td class="framestyle9">
  <table width="100%" border="0" align="center"  cellpadding="0" cellspacing="5"  class="DetailTable" cellpmoding="0" >
        <tr>
            <td colspan="3" height="15">
              &nbsp;&nbsp;                   
            </td>
           </tr>
         <tr> 
          <td width="14" align="right" nowrap class="tdFontcolor"></td>
          <td width="80%" align="left" nowrap class="tdFontcolor">"是"导出当前机构下所有单位信息</td>
          <td></td>
        </tr> 
         <tr> 
          <td width="14" align="right" nowrap class="tdFontcolor"></td>
          <td width="80%" align="left" nowrap class="tdFontcolor">"否"仅导出当前单位信息</td>
          <td></td>
        </tr>
         <tr> 
          <td width="14" align="right" nowrap class="tdFontcolor"></td>
          <td width="80%" align="left" nowrap class="tdFontcolor">"取消"退出导出界面</td>
          <td></td>
        </tr> 
        <tr>
            <td colspan="3" height="15">
              &nbsp;&nbsp;                   
            </td>
           </tr>      
      </table>	            	
     </td>
          </tr>
  <tr align="center" class="list3"> 
    <td height="35" align="center"> 
      <input type="button" size="100" name="b_shift" value=" <bean:message key="kq.emp.change.yes"/> " class="mybutton" onclick="window.returnValue=1; window.close();">&nbsp;
               <input type="reset" name="bc_clear" value=" <bean:message key="kq.emp.change.no"/> " class="mybutton" onclick="window.returnValue=2; window.close();">&nbsp;
                <input type="reset" name="bc_clear" value="<bean:message key="button.cancel"/>" class="mybutton" onclick="window.close();">
    </td>
          </tr>  
  </table>
</html>