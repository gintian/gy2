<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%@ page import="com.hjsj.hrms.businessobject.report.auto_fill_report.AnalyseParams,
                 com.hrms.hjsj.sys.EncryptLockClient,
                 com.hrms.hjsj.sys.VersionControl,
                 com.hjsj.hrms.actionform.sys.SysForm,
                 java.util.*,
                 com.hrms.frame.utility.AdminDb,
                 com.hrms.hjsj.utils.Sql_switcher,
                 com.hrms.hjsj.sys.Constant,
                 java.sql.*"%>

<%
     
     // 在标题栏显示当前用户和日期 2004-5-10 
    UserView userView=(UserView)session.getAttribute(WebConstant.userView); 
    
    EncryptLockClient lock=(EncryptLockClient)pageContext.getServletContext().getAttribute("lock");
    //showtmpls<org,pos>表示显示机构和职位模板
    String ProductId=request.getParameter("ProductId");
    String ProductId_str="";
    if(ProductId!=null&&ProductId.equalsIgnoreCase("ePM"))
        ProductId_str="^ProductId<HJ-ePM>";
    else
        ProductId_str="^ProductId<HJ-eHR>";
    int versionFlag=1;
    //zxj 20160613 报表不再区分标准版专业版
    // if (userView != null)
    //        versionFlag = userView.getVersion_flag(); // 1:专业版 0:标准版        
     
    String url="hrpurl<" + SystemConfig.getCsClientServerURL(request) + ">"; 
    if(SystemConfig.getPropertyValue("clientName")!=null&&SystemConfig.getPropertyValue("clientName").trim().length()>0)
        url+="^clientName<"+SystemConfig.getPropertyValue("clientName").trim()+">"; 
    else
        url+="^clientName<#>";  
    
    url+="^versionFlag<"+versionFlag+">";
    VersionControl ver_ctrl = new VersionControl();
//    url+="^wg<xz,ins,kq,rpt,dm,tr,per,doc,tab,jx,ex,rm>";
    
    String str="";
    if(ver_ctrl.searchFunctionId("23067"))
            str=",org";
    if(ver_ctrl.searchFunctionId("231102"))
            str+=",pos";
    if(str.trim().length()>0)
    {
        url+="^showtmpls<"+str.substring(1)+">";
    }
    else
        url+="^showtmpls<>";
   
    //cs支持电子签章  signature=1 
    if(ver_ctrl.searchFunctionId("3206")){
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

    /**版本控制*/
    if(ver_ctrl.searchFunctionId("290207")){
        if(versionFlag==0){
            url+="^ShowScopeMenu<0>";
        }else
            url+="^ShowScopeMenu<1>";
    }else{
        url+="^ShowScopeMenu<0>";
    }
    // 表格工具权限
    String designers = "";
    if(userView.hasTheFunction("99071"))// 登记表
       designers += ",card";
    if(userView.hasTheFunction("99072"))// 高级花名册
       designers += ",muster";
    if(userView.hasTheFunction("99073"))// 模板
       designers += ",tmpl";
    if(userView.hasTheFunction("99074"))// 统计表
       designers += ",tjb";
    url += "^RepDesigners<"+designers+">";   
    // 基准岗位
    if(ver_ctrl.searchFunctionId("25012")){
        url+="^stdpos<1>";
    }else{
        url+="^stdpos<0>";
    }   
    
    if(ProductId_str.length()>0)
            url+=ProductId_str;
    
    Connection connection=null;
    try
    {
        connection = (Connection) AdminDb.getConnection();
        AnalyseParams analyseParams=new AnalyseParams(connection);
        HashMap   map=analyseParams.getAttributeValues(userView.getUserId());                               //从常量表中取得期统计范围和截止日期
        String startdate=(String)map.get("startdate");  
        String appdate=(String)map.get("appdate");  //截止日期
        if(startdate!=null&&startdate.trim().length()>0)
        {
            startdate=startdate.replaceAll("\\.","-");
            url+="^startdate<"+startdate+">";
        }
        if(appdate!=null&&appdate.trim().length()>0)
        {
            appdate=appdate.replaceAll("\\.","-");
            url+="^busidate<"+appdate+">";
        }
        
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
        if(lock.isHaveBM(35))  // 移动服务
            url+="^mobile_service<1>"; 
        else
            url+="^mobile_service<0>";
            
        // 试用天数
        url+="^TrialDays<"+lock.getUseddays()+">"; 
    }catch(Exception e)
    {
    }finally
    {
      if(connection!=null)
       connection.close();  
    }
 
     
    //   1 sqlserver  2 oracle 9 dameng 此处按实际库dbflag传入，插件中会自动转换成内部的对于dbtype
    String dbType="#";
    dbType = Sql_switcher.dbflag + "";

    String license=lock.getLicenseCount();
    int version= ver_ctrl.getVer_no(); // 可以返回小版本号, 如：61,62 //userView.getVersion();
    if(license==null||license.equals("0")||license.equals(""))
         version=version+100;
 %>
<html>
  <head>
  </head>
  <script src="/general/sys/hjaxmanage.js"></script>
  <script type="text/javascript" src="/js/validate.js"></script>
  <script language='javascript' >
  function InitAx()
  {
      
      var ver_flag='${sysForm.license}';
      if(ver_flag=="0")
      {
            alert("授权模块超过实际购买数量!\n请联系开发商,谢谢!");
            return;
      }
      var obj = document.getElementById('reportDesigners');
      var params = "<%=url%>"+"^"+AxManager.hjdesignerSrvPkgVersionParam()+"^"+AxManager.hjdesignerLowestVersionParam()
                 + "^jsessionid<" + AxManager.getJSessionId()+ ">";
      obj.SetURL(params);
      obj.SetDBType(<%=(dbType)%>);
      obj.SetHrpVersion(<%=version%>);
      obj.SetUserName("<%=(userView.getUserName())%>");
      obj.ShowDesigners();
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
    AxManager.write("reportDesigners", 536, 300, AxManager.hjdesignerpkgName);
</script>
</td>
</tr></table>
</html:form>
</body>
</html>