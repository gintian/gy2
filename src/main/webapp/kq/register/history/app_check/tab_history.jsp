<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hrms.frame.dbstruct.DbWizard"%>
<%@page import="com.hrms.frame.utility.AdminDb"%>
<%@page import="java.sql.Connection" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%
int isShow_q11=0;
int isShow_q15=0;
int isShow_q13=0;
Connection conn = AdminDb.getConnection(); 
try {
	DbWizard dbWizard =new DbWizard(conn);
	if(!dbWizard.isExistTable("Q11_arc", false))
		isShow_q11=1;
	if(!dbWizard.isExistTable("Q15_arc", false))
		isShow_q15=2;
	if(!dbWizard.isExistTable("Q13_arc", false))
		isShow_q13=3;
} catch (Exception e) {
    e.printStackTrace();
} finally {
    if (null != conn) {
        try {
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
%>
<body onload="setPage(${appForm.sub_page});" >
<html:form action="/kq/register/history/app_check/history_check" style="margin-bottom: -0px;">
<hrms:tabset name="cardset" width="100%" height="100%" type="true">
<%if(isShow_q11!=1){ %>
	<hrms:tab name="mitem1" label="加班申请" visible="true" function_id="2705301,0C34701"
		url="/kq/register/history/app_check.do?b_search=link&table=Q11_arc&privtype=kq&sub_page=1">
	</hrms:tab>
<%}  if(isShow_q15!=2){ %>
	<hrms:tab name="mitem2" label="请假申请" visible="true" function_id="2705302,0C34702"
		url="/kq/register/history/app_check.do?b_search=link&table=Q15_arc&privtype=kq&sub_page=2">
	</hrms:tab>
<%}  if(isShow_q13!=3){ %>
	<hrms:tab name="mitem3" label="公出申请" visible="true" function_id="2705303,0C34703"
		url="/kq/register/history/app_check.do?b_search=link&table=Q13_arc&privtype=kq&sub_page=3">
	</hrms:tab>
<%} %>
</hrms:tabset>
</html:form>
</body>
<script type="text/javascript" language="javascript" >
	
function setPage(page)
{
	if(page==2)
	{
	var obj=$('cardset');
	obj.setSelectedTab("mitem2");
	}
	if(page==3)
	{
	var obj=$('cardset');
	obj.setSelectedTab("mitem3");
	}
}

</script>