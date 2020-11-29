<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*, 
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant,
				 com.hrms.hjsj.utils.Sql_switcher,
				 com.hrms.hjsj.sys.EncryptLockClient" %>

<%
	int dbtype=Sql_switcher.searchDbServer();
	String aurl = (String) request.getServerName();
	String port = request.getServerPort() + "";
	String url_p = "HTTP://" + aurl + ":" + port;	

	String userName = null;
	UserView userView = (UserView) session
			.getAttribute(WebConstant.userView);
	if (userView != null) {
		userName=userView.getUserName();
	}
	
	// 判断锁中是否有"机读"功能  JinChunhai 2011.09.20
	EncryptLockClient lock=(EncryptLockClient)pageContext.getServletContext().getAttribute("lock"); 
	int machinRead = 0; 		 
 	if(lock.isHaveBM(21))
 	{
 		machinRead = 1; //1表示有机读，0表示没有机读
 	}
 	else
 	{
 		machinRead = 0; 
 	}		
	
 %>
<html>
  <head>
  </head>
<script type="text/javascript" src="/general/sys/hjaxmanage.js"></script>
  <script language='javascript' >
  
  function InitOmr()
  {
      var aurl="<%=url_p%>";
      var DBType="<%=dbtype%>";
      var UserName="<%=userName%>";
      var PlanId=${dataGatherForm.planId};
      <% if(request.getParameter("objs")!=null){ %>
          var Objs='<%=(request.getParameter("objs"))%>'
      <% }else{  %>
      var Objs="${dataGatherForm.object_id}";
      <% } %>
      
      var obj = document.getElementById('orm');
      obj.SetURL(aurl);    /*设置URL*/
      obj.SetDBType(DBType);  /* 设置数据库类型 0:access 1: sqlserver 2:oracle 3:db2 */
      obj.SetPlanId(PlanId); /*考核计划设置计划  编号*/
      obj.SetUserName(UserName); 
      <% if(request.getParameter("readerType")!=null && request.getParameter("readerType").equals("0")){ //设备为光标阅读%>
     	 obj.SetAssessObjIDs(Objs);  /*设置考核对象ID，多个对象中间用“,”隔开*/
      <% } %>
      obj.SetOMRLisence("<%=machinRead%>");   // 加密锁是否有机读模块,1有,0没有
      obj.InitOMRX();  /*初始化机读插件状态*/
  }
  
  

  </script>
  <body onload='InitOmr();' bgColor='#F7F7F7' scroll='no' topMargin='0' leftMargin='0' >
   <html:form action="/performance/implement/dataGather">
<script type="text/javascript">
    AxManager.write("orm", 477, 300, AxManager.omrreaderPkgName, "<%=url_p%>");
</script>
</html:form>
  </body>
</html>
