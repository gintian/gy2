<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript">
function checkvalue(){
	if(document.getElementsByName('ip_vo.string(ip_addr)')[0].value==""){
		return false;
	}else{
		return ( confirm('确认保存吗？') )
	}
}
</script>
<html:form action="/system/security/ip_addr_maintenance">
      <table width="500" border="0" cellpadding="0" cellspacing="0" align="center" class="ftable" style="margin-top:6px;">
          <tr height="20">
       		<td align="left" class="TableRow" colspan="2"><bean:message key="label.sys.ip_addr_maintenance"/>&nbsp;</td>
          </tr> 
                      <tr class="list3">
                	      <td align="right" nowrap ><bean:message key="column.sys.ip_addr"/></td>
                	      <td align="left" nowrap >
                	      	<html:text name="ipaddrForm" property="ip_vo.string(ip_addr)" size="15" maxlength="15" styleClass="text" style="width:330px;"/>    	      
                          </td>
                      </tr>
                      <!--  修改ip地址维护界面 jingq  upd  2014.5.16 -->
                      <tr class="list3">
                	      <td align="right" nowrap><bean:message key="column.sys.description"/></td>
                	      <td align="left"  nowrap>
                	      	<html:text name="ipaddrForm" property="ip_vo.string(description)" size="50" maxlength="100" styleClass="text" style="width:330px;"/>
                          </td>
                      </tr>
                      <tr class="list3">
                	      <td align="right"  nowrap>
                                <bean:message key="column.sys.invalid"/>				               	      
                              </td>
                              <td align="left">
                              	<html:radio name="ipaddrForm" property="ip_vo.string(valid)" value="1"/><bean:message key="datestyle.yes"/>&nbsp;<html:radio name="ipaddrForm" property="ip_vo.string(valid)" value="0"/> <bean:message key="datesytle.no"/>
                              </td>
                      </tr>                       

          <tr class="list3">
            <td align="center" colspan="2" style="height:35px">
         	<hrms:submit styleClass="mybutton" property="b_save" onclick="document.ipaddrForm.target='_self';validate('R','ip_vo.string(ip_addr)','ip地址');return (checkvalue());return (document.returnValue && ifqrbc());" >
            		<bean:message key="button.save"/>
	 	</hrms:submit>
         	<hrms:submit styleClass="mybutton" property="br_return">
            		<bean:message key="button.return"/>
	 	</hrms:submit>            
            </td>
          </tr>          
      </table>
</html:form>
