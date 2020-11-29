<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.gz.gz_data.*,java.util.*"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
	}
	
	SalaryDataForm salaryDataForm=(SalaryDataForm)session.getAttribute("salaryDataForm"); 
	String returnFlag = salaryDataForm.getReturnFlag();
	String isLeafOrg = salaryDataForm.getIsLeafOrg();
	String isAllDistri = salaryDataForm.getIsAllDistri();
	String salaryid = salaryDataForm.getSalaryid();
	String theyear = salaryDataForm.getTheyear();
	String themonth = salaryDataForm.getThemonth();
	String operOrg = salaryDataForm.getOperOrg();
 	String isOnlyLeafOrgs = salaryDataForm.getIsOnlyLeafOrgs();
 	String isOrgCheckNo = salaryDataForm.getIsOrgCheckNo();
 	
 	String showUnitCodeTree=salaryDataForm.getShowUnitCodeTree();  //是否按操作单位来显示树
 	if(showUnitCodeTree!=null&&showUnitCodeTree.equals("1"))
	{
		String unitcodes=userView.getUnit_id();  //UM010101`UM010105`
 		if(unitcodes==null||unitcodes.length()==0||unitcodes.equalsIgnoreCase("UN"))
 			showUnitCodeTree="0";
	}
	String url = "/gz/gz_data/gz_table.do?b_query=link&salaryid="+salaryid;
	if(returnFlag.equals("1"))
		url+="&returnFlag=1&theyear="+theyear+"&themonth="+themonth+"&orgcode="+operOrg+"&isleafOrg="+isLeafOrg+"&isAllDistri="+isAllDistri+"&isOnlyLeafOrgs="+isOnlyLeafOrgs+"&isOrgCheckNo="+isOrgCheckNo;
%>
<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link href="/css/xtree.css" rel="stylesheet" type="text/css">
<hrms:themes />
<html:form action="/gz/gz_data/gz_org_tree"> 

  <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >   
	 <tr align="left">
		<td valign="top"></td>
	 </tr>          
     <tr>        
           <td align="left"> 
                 <hrms:orgtree action="<%=url %>" target="mil_body" flag="0"   loadtype="1" priv="${salaryDataForm.priv}" showroot="false" dbpre="" rootaction="1" rootPriv="0" nmodule="1"/>			           
           </td>
      </tr>            
   </table>
</html:form>
<script>
<% if(request.getParameter("b_opt")==null){ %>
	
	root.openURL();
	
<%  } %>
</script>
