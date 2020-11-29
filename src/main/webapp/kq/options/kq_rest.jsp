<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>

<%
    String type = (String) request.getParameter("type");
    String isshow = (String)request.getParameter("isshow");
    if(isshow == null){
        isshow = "2"; //首次进入为空 为防止抛异常 随意赋值一个不为空的参数即可
    }
%>

<style>
  .week_td { width: 100px;  }
</style>

<script type="text/javascript">
<!--
<%
if(isshow.equals("1")){
%>
alert("保存成功!");
window.close();
<%
}
%>
function save(){
    kqRestForm.action = "/kq/options/kq_rest.do?b_update=link&isshow=1";
    kqRestForm.submit();
}
//-->
</script>
<html:form action="/kq/options/kq_rest">
    <br>
    <div align="center">
    <fieldset align="center" style="width: 50%; padding:10px;">
        <legend>
            <bean:message key="kq.kq_rest.shuoming" />
        </legend>
        <br>
        &nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="kq.kq_rest.info" />
    </fieldset>
    <br>
    <br>
    <fieldset align="center" style="width: 50%;">
        <legend>
            <bean:message key="kq.kq_rest.title" />
        </legend>
        <table border="0" cellspacing="0" align="center" cellpadding="0">
            <tr>
                <td class="week_td">
                    <html:multibox name="kqRestForm" property="rest_weeks" value="1" />
                    <bean:message key="kq.kq_rest.monday" />
                </td>
                <td class="week_td">
                    <html:multibox name="kqRestForm" property="rest_weeks" value="2" />
                    <bean:message key="kq.kq_rest.tuesday" />
                </td>
                <td class="week_td">
                    <html:multibox name="kqRestForm" property="rest_weeks" value="3" />
                    <bean:message key="kq.kq_rest.wednesday" />
                </td>
                <td class="week_td">
                    <html:multibox name="kqRestForm" property="rest_weeks" value="4" />
                    <bean:message key="kq.kq_rest.thursday" />
                </td>
            </tr>
            <tr>
                <td class="week_td">
                    <html:multibox name="kqRestForm" property="rest_weeks" value="5" />
                    <bean:message key="kq.kq_rest.firday" />
                </td>
                <td class="week_td">
                    <html:multibox name="kqRestForm" property="rest_weeks" value="6" />
                    <bean:message key="kq.kq_rest.Saturday" />
                </td>
                <td class="week_td">
                    <html:multibox name="kqRestForm" property="rest_weeks" value="7" />
                    <bean:message key="kq.kq_rest.sunday" />
                </td>
                <td></td>
            </tr>


        </table>
        <br />
    </fieldset>
    </div>
    <table border="0" cellspacing="0" align="center" cellpadding="0">
        <tr>
            <td align="center" height="35px">
                <!--    <hrms:submit styleClass="mybutton" property="b_update">
                       <bean:message key="button.save"/>
                </hrms:submit>  -->
                <input type="button" value="保存" onclick="save();  "class="mybutton" />
                <html:reset styleClass="mybutton">
                    <bean:message key="kq.kq_rest.reset" />
                </html:reset>
                <hrms:tipwizardbutton flag="workrest" target="il_body"
                    formname="kqRestForm" />
                <%
                	String gw_flag = request.getParameter("flag");
                	if(gw_flag != null){
                %>
		   	     <input type="button" value="<bean:message key="button.close"/>" class="mybutton" onclick="window.close();"/>
                <%
                	}
                %>
            </td>
        </tr>
    </table>



</html:form>


