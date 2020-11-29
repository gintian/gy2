<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*, 
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant,
				 com.hrms.hjsj.sys.EncryptLockClient" %>
<%
	String url="http://"+request.getServerName()+":"+request.getServerPort();
	String p0201=request.getParameter("p0201");
	//out.println(url);
	 
	String userName = null;
	UserView usView = (UserView)session.getAttribute(WebConstant.userView);
	if(usView != null){
		userName=usView.getUserName();
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
<script type="text/javascript" src="/general/sys/hjaxmanage.js"></script>
<script language="javascript">
  // 民主推荐
  function initRm()
  {
      var aurl="<%=url %>";
      var DBType="1";
      var UserName="<%=userName%>";   
      
      var obj = document.getElementById('ocr1'); 
      obj.SetURL(aurl);      
      obj.SetDBType(DBType); 
      obj.SetPlanType(1);    // 计划类型: 0绩效(默认值),1民主推荐
      obj.SetPlanId("<%=p0201%>");      // 民主推荐号P02.P0201
      obj.SetUserName(UserName); 
      obj.SetOMRLisence("<%=machinRead%>");   // 加密锁是否有机读模块,1有,0没有
      obj.InitOMRX();        
  }
</script>
<div style="margin-top: 0px;text-align: center;">
<script type="text/javascript">
    AxManager.write("ocr1", 477, 300, AxManager.omrreaderPkgName, "<%=url%>");
</script>
</div>
<script language="javascript">
	initRm();
</script>