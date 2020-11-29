<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<style>
    .titletd {
        width: 80px;
    }
</style>
<html:form action="/selfservice/infomanager/board/addboard" method="get">
    <table width="800px" border="0" cellpadding="0" cellspacing="0" align="center" class="ftable"
           style="margin-top:6px;">

        <tr height="20">
            <td align="left" colspan="2" class="TableRow" style="margin-left:5px;"><bean:message
                    key="lable.board.manager"/></td>
        </tr>
        <tr>
            <td align="right" class="titletd" nowrap valign="middle"><bean:message key="conlumn.board.topic"/></td>
            <td align="left" nowrap valign="middle">
                <bean:write name="boardForm" property="boardvo.string(topic)" filter="true"/>

            </td>
        </tr>
        <tr height="200">
            <td align="right" class="titletd" nowrap valign="middle"><bean:message key="conlumn.board.content"/></td>
            <td align="left" valign="middle">
                <bean:write name="boardForm" property="boardvo.string(content)" filter="false"/>
            </td>
        </tr>
        <tr>
            <td colspan="2" align="center" valign="middle">
                <bean:message key="conlumn.board.approve"/>
                <html:radio name="boardForm" property="boardvo.string(approve)" value="1"/><bean:message
                    key="datestyle.yes"/>
                <html:radio name="boardForm" property="boardvo.string(approve)" value="0"/><bean:message
                    key="datesytle.no"/>
            </td>
        </tr>

        <tr>
            <td align="center" colspan="2" style="height: 35px">
                <hrms:submit styleClass="mybutton" property="b_save"
                             onclick="document.boardForm.target='_self';return (document.returnValue && ifqrbc());">
                    <bean:message key="button.save"/>
                </hrms:submit>

                <hrms:submit styleClass="mybutton" property="br_return">
                    <bean:message key="button.return"/>
                </hrms:submit>
            </td>
        </tr>
    </table>
</html:form>
