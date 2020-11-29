<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<html>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String codeitemid="-1";
	String codesetid="CZDW";

	String userName=userView.getUserName();
	String screenWidth=request.getParameter("screenWidth");
	String screenHeight=request.getParameter("screenHeight");
	/* 薪资审批/审批发放/薪资报表，人员机构工资分析表显示不全 xiaoyun 2014-10-17 start */
	int height = Integer.parseInt(screenHeight)-10;
	/* 薪资审批/审批发放/薪资报表，人员机构工资分析表显示不全 xiaoyun 2014-10-17 end */
	String rsid=request.getParameter("rsid");
	String rsdtlid=request.getParameter("rsdtlid");
	String groupValues=request.getParameter("groupValues");
//	String sql = request.getParameter("s");
	
%> 
<script type="text/javascript">
<!--
  function test()
  {
  	var obj=$('earchive');
  	var name=obj.HelloWorld("chenmengqing");
  	alert(name);
  }
 
  function dataExport(filename)
  {
	 filename = getDecodeStr(filename);
  	 var dd=open("/servlet/vfsservlet?fileid="+filename+"&fromjavafolder=true","excel");
  }
 
  
  function closeWindow()
  {
  	window.close();
  	if(parent.winScope)
  	parent.winScope.closeWin();
  }
  
  function cal() {
	    var strurl="/js/counter.html";
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
		var ss=window.showModelessDialog(iframe_url,arguments,"dialogWidth=350px;dialogHeight=263px;resizable=no;scroll=no;status=no;");  
	   
  }
  function pageOptions(rsid,rsdtlid)
  {
      var thecodeurl="/general/print/page_options.do?b_edit=link`state=4`id=-1`rsid="+rsid+"`rsdtlid="+rsdtlid; 
	  var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
	  
	  var xmltype= window.showModalDialog(iframe_url, arguments, 
        "dialogWidth:800px; dialogHeight:450px;resizable:no;center:yes;scroll:yes;status:no");        
      
  }
 
 
//-->
</script>
<body bgColor='#F7F7F7' scroll='no' topMargin='0' leftMargin='0' >
<html:form action="/gz/gz_accounting/report/open_gzbanner">
    <jsp:plugin  type="applet" name="earchive" archive="hj_client.jar,struts_extends.jar,hessian-3.1.3.jar,command.jar,rowset.jar,jai_codec.jar,jai_core.jar,commons-beanutils.jar,commons-logging-api.jar,jsuite_swing_all.jar,plugin.jar" code="com.hjsj.hrms.client.gz.report.GzReportApplet.class"    width="<%=screenWidth%>"    height="<%=height%>" 
   codebase="/client">  
   		<jsp:params> 
          <jsp:param  name="salaryid"  value="${gzReportForm.salaryid}"/>  
		  <jsp:param  name="codeitemid"  value="<%=codeitemid%>"/>  
		  <jsp:param  name="codesetid"  value="<%=codesetid%>"/>  
          <jsp:param  name="rsid"  value="<%=rsid%>"/>  
          <jsp:param  name="rsdtlid"  value="<%=rsdtlid%>"/>  
          <jsp:param  name="useName"  value="<%=userName%>"/> 
          <jsp:param  name="groupValues"  value="<%=groupValues%>"/> 
          <jsp:param name="filterSql" value="${gzReportForm.filterWhl}"/>
          <jsp:param name="noManagerFilterSql" value="${gzReportForm.noManagerFilterSql}"/>
          <jsp:param name="privDb" value="${gzReportForm.privDb}"/>
          <jsp:param name="role" value="${gzReportForm.role}"/>
          <jsp:param name="privCode" value="${gzReportForm.privCode}"/>
          <jsp:param name="privCodeValue" value="${gzReportForm.privCodeValue}"/>
          <jsp:param name="address" value="${gzReportForm.address}"/>
          <jsp:param name="priv_mode" value="${gzReportForm.priv_mode}"/>
          <jsp:param name="model" value="${gzReportForm.model}"/>
          <jsp:param name="boscount" value="${gzReportForm.boscount}"/>
          <jsp:param name="bosdate" value="${gzReportForm.bosdate}"/>
          <jsp:param name="MAYSCRIPT" value="true"/>
        </jsp:params>
           <jsp:fallback>
                 <p>Unable to start plugin.</p>
           </jsp:fallback>	           
   </jsp:plugin>
</html:form>
</body>
</html>