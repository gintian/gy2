<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>


<html:form action="/workflow/webclient/view_leave">
      <table width="100%" border="0" cellpadding="0" cellspacing="1" class="mainbackground">
          <THEAD>
          <tr bordercolor="#C9C9C9" class="list1" height="30">
            <TD colspan="4" align="center" nowrap >
              请假单
            </TD>
          </tr>
          </THEAD>      
   
          <tr class="list3">
    	      <td align="right" nowrap>姓名：</td>
    	      <td align="left" nowrap >
                    <bean:write name="nodeForm" property="leave_vo.string(staff_name)" filter="true"/>&nbsp;    	      
              </td>
    	      <td align="right" nowrap>请假天数：</td>
    	      <td align="left"  nowrap >
                    <bean:write name="nodeForm" property="leave_vo.string(days)" filter="true"/>&nbsp;    	      
              </td>    
          </tr>
          <tr class="list3">
    	      <td align="right" nowrap>事由：</td>
    	      <td align="left"  wrap colspan="3">
                    <bean:write name="nodeForm" property="leave_vo.string(whys)" filter="true"/>&nbsp;    	      
              </td>
          </tr>
      </table>
</html:form>
