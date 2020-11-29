<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient"%>
<%@ page import="com.hjsj.hrms.businessobject.report.auto_fill_report.AnalyseParams,
				 com.hrms.hjsj.sys.EncryptLockClient,
				 com.hrms.hjsj.sys.VersionControl,
				 com.hjsj.hrms.actionform.sys.SysForm,
				 java.util.*,
				 com.hrms.frame.utility.AdminDb,
				 java.sql.*"%>

<%
	 
	 // 在标题栏显示当前用户和日期 2004-5-10 
	UserView userView=(UserView)session.getAttribute(WebConstant.userView); 
	String prl=request.getProtocol();
	
	String _prl=request.getScheme(); 
	String ProductId=request.getParameter("ProductId");
	String ProductId_str="";
	if(ProductId!=null&&ProductId.equalsIgnoreCase("ePM"))
		ProductId_str="^ProductId<HJ-ePM>";
	else
		ProductId_str="^ProductId<HJ-eHR>";
	
	int idx=prl.indexOf("/");
	prl=prl.substring(0,idx);
	if(_prl.equalsIgnoreCase("https"))
		prl=_prl;
	
	String url="hrpurl<"+SystemConfig.getCsClientServerURL(request)+">";
	
    if(SystemConfig.getPropertyValue("percentile_calcmethod")!=null&&SystemConfig.getPropertyValue("percentile_calcmethod").trim().length()>0)
		url+="^percentile_calcmethod<"+SystemConfig.getPropertyValue("percentile_calcmethod").trim()+">";
	
	if(SystemConfig.getPropertyValue("imis_url_1")!=null&&SystemConfig.getPropertyValue("imis_url_1").trim().length()>0)
		url+="^imis_url_1<"+SystemConfig.getPropertyValue("imis_url_1").trim()+">";
	if(SystemConfig.getPropertyValue("imis_url_2")!=null&&SystemConfig.getPropertyValue("imis_url_2").trim().length()>0)
		url+="^imis_url_2<"+SystemConfig.getPropertyValue("imis_url_2").trim()+">";
	if(SystemConfig.getPropertyValue("dbserver_name")!=null&&SystemConfig.getPropertyValue("dbserver_name").trim().length()>0)
    	url+="^dbserver_name<"+SystemConfig.getPropertyValue("dbserver_name").trim()+">";
    	
    if(SystemConfig.getPropertyValue("clientName")!=null&&SystemConfig.getPropertyValue("clientName").trim().length()>0)
    	url+="^clientName<"+SystemConfig.getPropertyValue("clientName").trim()+">";	
    else
    	url+="^clientName<#>";	
    	
    if(SystemConfig.getPropertyValue("oracleRAC")!=null&&SystemConfig.getPropertyValue("oracleRAC").trim().length()>0)
    	url+="^oracleRAC<"+SystemConfig.getPropertyValue("oracleRAC").trim().replaceAll(":","=")+">";	
     
    // Oracle 透明加密	
    if(SystemConfig.getPropertyValue("wallet")!=null && SystemConfig.getPropertyValue("wallet").equalsIgnoreCase("true")) {	
    	url+="^wallet<true>";
        if (SystemConfig.getPropertyValue("wallet_key_cs")!=null && SystemConfig.getPropertyValue("wallet_key_cs").trim().length()>0) {
        	url+="^wallet_key_cs<"+ SystemConfig.getPropertyValue("wallet_key_cs").trim() +">";
        }
    }else{
    	url+="^wallet<false>";
    }
    	
    // 是否显示虚拟机构
    if(SystemConfig.getPropertyValue("vorganization")!=null&&SystemConfig.getPropertyValue("vorganization").trim().length()>0)
        url+="^vorg<"+SystemConfig.getPropertyValue("vorganization").trim()+">"; 
    else
        url+="^vorg<false>";  

    int versionFlag = 1;
    //zxj 20160613 人事异动、薪资部分功能不再区分标准版专业版
	//if (userView != null)
	//		versionFlag = userView.getVersion_flag(); // 1:专业版 0:标准版		
    url+="^versionFlag<"+versionFlag+">";
    
    //cs支持电子签章  signature=1
        VersionControl ver_ctrl=new VersionControl();
    if(ver_ctrl.searchFunctionId("3206"))
		{
		if(versionFlag==0){
			url+="^signature<0>";
		}else
			url+="^signature<1>";
		}	
	else
	    url+="^signature<0>";	
	  if(SystemConfig.getPropertyValue("unit_property")!=null&&SystemConfig.getPropertyValue("unit_property").trim().length()>0)
		url+="^unit_property<"+SystemConfig.getPropertyValue("unit_property").trim()+">";		
	else
	    url+="^unit_property<>";
    //生成随机账号口令要放到通版中， BS加设置传给后台业务
    if(SystemConfig.getPropertyValue("randomcreator")!=null&&SystemConfig.getPropertyValue("randomcreator").trim().length()>0)
		url+="^randomcreator<"+SystemConfig.getPropertyValue("randomcreator").trim()+">";
	else
		url+="^randomcreator<>";	
    	
	// 移动应用配置  2013-12-10
    if(SystemConfig.getPropertyValue("MobileConfig")!=null&&
    		(SystemConfig.getPropertyValue("MobileConfig").trim().equalsIgnoreCase("true")||SystemConfig.getPropertyValue("MobileConfig").trim().equals("1")) )
        url+="^MobileConfig<1>";
    else
        url+="^MobileConfig<0>";    

    // 隐藏子集, 放到INFOM节
    if(SystemConfig.getPropertyValue("HIDESET_INFOM")!=null&&SystemConfig.getPropertyValue("HIDESET_INFOM").trim().length()>0 )
        url+="^INFOM::HIDESET_INFOM<"+SystemConfig.getPropertyValue("HIDESET_INFOM").trim()+">";
    else
        url+="^INFOM::HIDESET_INFOM<>";    
        
			        /**版本控制*/
	if(ver_ctrl.searchFunctionId("290207")){
		if(versionFlag==0){
			url+="^ShowScopeMenu<0>";
		}else
			url+="^ShowScopeMenu<1>";
	}else{
		url+="^ShowScopeMenu<0>";
	}
	
			        /**计件工资-产品目录*/
	if(ver_ctrl.searchFunctionId("32421")){
		if(versionFlag==0){
			url+="^ShowCPML<0>";
		}else
			url+="^ShowCPML<1>";
	}else{
		url+="^ShowCPML<0>";
	}
	
			        /**工资-财务凭证*/
	if(ver_ctrl.searchFunctionId("32417")){
		if(versionFlag==0){
			url+="^ShowPZDY<0>";
		}else
			url+="^ShowPZDY<1>";
	}else{
		url+="^ShowPZDY<0>";
	}
	
    if(ProductId_str.length()>0)
    		url+=ProductId_str;
    
    Connection connection=null;
    try
    {
   	    connection = (Connection) AdminDb.getConnection();
	  	AnalyseParams analyseParams=new AnalyseParams(connection);
		HashMap   map=analyseParams.getAttributeValues(userView.getUserId());								//从常量表中取得期统计范围和截止日期
 		String startdate=(String)map.get("startdate");	
 		if(startdate!=null&&startdate.trim().length()>0)
 		{
 			startdate=startdate.replaceAll("\\.","-");
 			url+="^startdate<"+startdate+">";
 		}
 		
 		EncryptLockClient lock=(EncryptLockClient)pageContext.getServletContext().getAttribute("lock");
 		if(lock.isHaveBM(29))
 		{
 			url+="^per_target<1>"; //1表示有目标管理，0表示没有目标管理
 		}
 		else
 		{
 			url+="^per_target<0>"; 
 		}
 		
 		if(lock.isHaveBM(21))
 		{
 			url+="^machine_readable<1>"; //1表示有机读，0表示没有机读
 		}
 		else
 		{
 			url+="^machine_readable<0>"; 
 		}
 	
 	}catch(Exception e)
	{
	}finally
	{
	  if(connection!=null)
	   connection.close();	
	}
 
 %>
<html>
  <head>
  </head>
  <script src="/general/sys/hjaxmanage.js"></script>
  <script src="/js/validate.js"></script>
  <script language='javascript' >  
  function InitAx()
  {
      var ver_flag='${sysForm.license}';
   	  if(ver_flag=="0")
   	  {
   		    alert("授权模块超过实际购买数量!\n请联系开发商,谢谢!");
   		    return;
   	  }
      var obj = document.getElementById('hrms'); 
      <%  
      String nCtrl="1";
      if(request.getParameter("cs_module")!=null&&request.getParameter("cs_module").equals("10"))
      {
      		if(request.getParameter("nCtrl")!=null&&request.getParameter("nCtrl").length()>0)
      		{
	    		  nCtrl=request.getParameter("nCtrl");   
      		}
      }
      %>
      var params = "<%=url%>"+"^"+AxManager.busiAxSrvPkgVersionParam()+"^"+AxManager.busiAxLowestVersionParam();
      obj.ShowHrms(${sysForm.cs_app_str},<%=(request.getParameter("cs_module"))%>,<%=nCtrl%>,"${sysForm.appDate}",params);
      
  	  //window.opener=null;//不会出现提示信息
  	  //window.close();
  	  <%if(request.getParameter("cs_module")!=null&&request.getParameter("cs_module").equals("10")){%>//zgd 2014-5-13 表格录入返回后回到导航图
	  	  var objt = eval("document.sysForm");
	  	  objt.action = "/general/tipwizard/tipwizard.do?br_employee=link";
		  objt.target = "il_body";
	      objt.submit();
      <%}%>
  }
  

  </script>
<body onload='InitAx();' class="body_sec" scroll='no' topMargin='0' leftMargin='0' >
<div id="hint"></div>
<html:form action="/templates/menu/busi_m_menu">
<table width='100%' heigth='100%' >
<tr><td><Br><br>&nbsp; <Br></td></tr>
<tr><td width='100%' heigth='100%'  align='center' vAlign='middle ' >
<script type="text/javascript">
    CheckIE(document.getElementById("hint"));
    AxManager.write("hrms", 536, 300, AxManager.busiAXpkgName);
</script>
</td></tr></table>
</html:form>
</body>
</html>
