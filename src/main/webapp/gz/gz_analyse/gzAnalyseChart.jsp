<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.hjsj.utils.Sql_switcher"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%@ page import="com.hrms.hjsj.sys.IResourceConstant,com.hrms.struts.exception.GeneralException,com.hrms.struts.exception.GeneralExceptionHandler"%>

<%
	String aurl = (String)request.getServerName();
    String port=request.getServerPort()+"";
    String prl=request.getProtocol();
    int idx=prl.indexOf("/");
    
    prl=prl.substring(0,idx);
    /*
    String url_p=prl+"://"+aurl+":"+port;
    */
    String url_p=SystemConfig.getCsClientServerURL(request);

    String dbtype=String.valueOf(Sql_switcher.searchDbServer());
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);

	String username=userView.getUserName();
	String superUser="0";
	
	if(userView.isSuper_admin())
	  superUser="1";
	String tbids=userView.getResourceString(IResourceConstant.GZ_CHART);//工资分析图表
	
	String dbpres=userView.getDbpriv().toString();//库前缀串
%>

<html:form action="/gz/gz_analyse/gzAnalyseChart" >

<script type="text/javascript" src="/general/sys/hjaxmanage.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script type="text/javascript">
<%if(!userView.isSuper_admin()&&!userView.getGroupId().equals("1")&&(tbids==null||tbids.equals(""))){%>
document.location="/gz/gz_analyse/gzAnalyseChart.do?br_error=error&what=tabid";
<%}%>
<%if(!userView.isSuper_admin()&&!userView.getGroupId().equals("1")&&(dbpres==null||dbpres.equals(""))){%>
document.location="/gz/gz_analyse/gzAnalyseChart.do?br_error=error&what=pre";
<%}%>

window.onresize = function()
{
		 var obj=document.getElementById("salarychartview");
		 obj.width=document.body.clientWidth-22;
		 obj.height=document.body.clientHeight-24;
}

function InitChartView()
{  
	 var obj=document.getElementById("salarychartview");
	 if(obj==null)
	 {
	    window.setTimeout('InitChartView()',1000);   
	 }
     if (obj!=null)
     {
        var url="<%=url_p%>";
        var dbtype="<%=dbtype%>";
        var username="<%=username%>";
        var tbids="<%=tbids%>";
        var dbpres="<%=dbpres%>";
        var superUser="<%=superUser%>";
        obj.SetURL(url);
        obj.SetDBType(dbtype);
        obj.SetUserName(username);
        obj.SetStatTabs(tbids);
        obj.SetNBase(dbpres);
        obj.SetSuperUser(superUser);
        obj.InitSalaryChartView(); 
     }
}
</script>

<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0"> 
<tr>
<td width="100%" align="center">
<script type="text/javascript">
    AxManager.write("salarychartview", document.body.clientWidth-22, document.body.clientHeight-24, 
     				AxManager.salarychartviewxPkgName, "<%=url_p%>");
</script>
</td>
</tr>
<tr>
<td align="left">
<hrms:tipwizardbutton flag="compensation" target="il_body" formname="sysForm"/> 
</td>
</tr>
</table>

<script type="text/javascript">
InitChartView();
</script>
</html:form>
