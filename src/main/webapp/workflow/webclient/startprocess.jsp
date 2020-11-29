<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>


<html:form action="/workflow/webclient/startprocess">
      <table width="70%" border="0" cellpadding="0" cellspacing="1" class="mainbackground">
          <tr class="list2" height="30">
    	      <td align="center" nowrap colspan="4">请假单</td>
          </tr>      
          <tr class="list3">
    	      <td align="right" nowrap>姓名：</td>
    	      <td align="left" nowrap >
    	      <html:text name="nodeForm" property="leave_vo.string(staff_name)" size="20" maxlength="20" styleClass="text"/>    	      
              </td>
    	      <td align="right" nowrap>请假天数：</td>
    	      <td align="left"  nowrap >
    	      	<html:text name="nodeForm" property="leave_vo.string(days)" styleClass="text"/>
              </td>    
          </tr>
          <tr class="list3">
    	      <td align="right" nowrap>事由：</td>
    	      <td align="left"  nowrap colspan="3">
    	      	<html:textarea name="nodeForm" property="leave_vo.string(whys)" cols="80" rows="6" styleClass="text"/>
              </td>
          </tr>
          <tr class="list3">
            <td align="center" colspan="4">
    			<input type="submit" name="b_start" value="确定">
    			<input type="submit" name="br_return" value="返回">
            </td>
          </tr>          
      </table>
</html:form>
