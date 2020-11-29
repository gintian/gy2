<%@page import="com.hjsj.hrms.utils.FuncVersion"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient"%>
<%@ page import="com.hrms.hjsj.sys.VersionControl"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%@ page import="com.hjsj.hrms.actionform.sys.AboutHrpForm"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
   EncryptLockClient lockclient=(EncryptLockClient)session.getServletContext().getAttribute("lock");
   String license=lockclient.getLicenseCount();
   String temp=lockclient.getAllModuleName();
   UserView userView=(UserView)session.getAttribute(WebConstant.userView);	
   //【39990】 根据锁版本生成模块明细信息 guodd 2019-05-18
   FuncVersion funcv = new FuncVersion(userView,lockclient.getVersion_flag());
   temp = funcv.getRealModuleNames(temp);
   
   AboutHrpForm aboutform=(AboutHrpForm)session.getAttribute("aboutForm");
   String status=aboutform.getStatus(); //for 统一到一个自助服务平台
   if(status==null)
   		status="1";
   String regflag=SystemConfig.getPropertyValue("regflag");
   if(regflag.equalsIgnoreCase(""))
   	regflag="0";
   int ilocktype=lockclient.getIlocktype();
   int nver_s=lockclient.getVersion_flag();
   int nver=lockclient.getVersion();
   VersionControl ver=new VersionControl();
   String vern=ver.getBuildver();
   String strv="";//"世纪eHR"+vern;
   
   String hcm = "eHR";
   if (userView != null) {
   	if ("hcm".equalsIgnoreCase(userView.getBosflag())) {
   		hcm = "HCM";
   	}
   }
   
  // if(nver>=50)
   {
	 //71以后版本专业版为HCM，标准版为eHR。锁版本为70及以上，程序版本大于等于71 并且 是标准版，显示eHR guodd 2017-10-13
	 if(nver>=70 && nver_s==0 && ver.getVer_no()>=71){//nver_s=1 专业版        =0 标准版
			 hcm = "eHR";
	 }
	 
	 if(nver>=70)
	 {
	  	strv="HJ-" + hcm + " " +vern;
	 }
	 else
	 {
	 
	  	 switch(nver_s)
	  	 {
	  	 case 0:
	  		 strv="" + hcm + "-标准版"+vern;
	  		 break;
	  	 case 1:
	  		 strv="" + hcm + "-专业版"+vern;
	  		 break;
	  	 default:
	  		 strv="" + hcm +vern;
	  		 break;
	  	 }
	  	 
	  	if(status.equalsIgnoreCase("2"))
	    {
	      strv="HJ-eHR"+vern;

	    }
	    if(ilocktype==0)
	    {
	      strv="HRP WEB3.0";
	    }
	  	 
	 }
   }   
 //  boolean bcurrent=ver.isBCurrent();
   
%>
<html:form action="/system/security/about_hrp">
<center>
<table width="70%" border="0"  align="center" cellpadding="0" cellspacing="0" class="ListTable2" style="margin-top:6px;">
    <tr>
      <td height="18" nowrap class="TableRow"  align="left" colspan="2"><bean:message key="label.sys.about.product"/>&nbsp;</td>           
    </tr>
  <tr>
    <td width="15%" class="RecordRow" align="right"><bean:message key="label.sys.about.version"/></td>
    <td width="85%" class="RecordRow"><!-- <IMG src="/images/ver.jpg">--><%=strv%></td>
  </tr>
    <%
      if(regflag.equals("1"))
      {
    %>  
  <tr>
    <td width="15%" class="RecordRow" align="right"><bean:message key="label.sys.about.productno"/></td>
    <td width="85%" class="RecordRow">${aboutForm.productno}</td>
  </tr>  
    <%
    }
    %>  
  <tr>
    <td class="RecordRow" align="right"><bean:message key="label.sys.about.versiontype"/></td>
    <%
      if(license.equals("0"))
      {
    %>
       <td class="RecordRow"><bean:message key="label.sys.about.test"/></td>
    <%
    }
    else
    {
    %>
       <td class="RecordRow"><bean:message key="label.sys.about.buy"/></td>       
    <%
    }
    %>
  </tr>
  <tr>
    <td class="RecordRow" align="right"><bean:message key="label.sys.about.module"/></td>
    <td class="RecordRow" style="line-height:20px;padding:2 0 2 4;letter-spacing:1px;"><%=temp%></td>
  </tr>
  <tr>
    <td class="RecordRow" align="right"><bean:message key="label.sys.about.priv"/></td>
    <td class="RecordRow"><bean:message key="label.sys.about.content"/></td>
  </tr>
  <tr>
    <td class="RecordRow" align="right"><bean:message key="label.sys.about.lx"/></td>
    <td class="RecordRow"><a href="Mailto:hjsj@hjsoft.com.cn" style="line-height:22px;">hjsj@hjsoft.com.cn</a><br>
    <a href="http://www.hjsoft.com.cn" target="_blank" style="line-height:22px;">http://www.hjsoft.com.cn</a><br>
    <!-- 小工具条，版本界面 添加微信扫描二维码  jingq add 2014.11.14 -->
    <img src="/images/weixin.png">
    </td>
  </tr>  
</table>
<logic:equal name="aboutForm" property="status" value="1">
<%
	if(!ver.isBCurrent())
	{
%>

<table  width="50%" align="center">
          <tr>
            <td align="center">
         	<hrms:submit styleClass="mybutton" property="b_priv">
            		<bean:message key="button.priv"/>
	 		</hrms:submit>
            </td>
          </tr>          
</table>
<%}%>
</logic:equal>
</center>
</html:form>
