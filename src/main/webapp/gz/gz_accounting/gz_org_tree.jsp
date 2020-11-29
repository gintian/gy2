<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.gz.gz_accounting.AcountingForm,java.util.*"%>
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
	
	AcountingForm form=(AcountingForm)session.getAttribute("accountingForm"); 
	String returnFlag = form.getReturnFlag();
	String theyear = form.getTheyear();
	String themonth = form.getThemonth();
	String operOrg = form.getOperOrg();
	String salaryid = form.getSalaryid();
	String showUnitCodeTree=form.getShowUnitCodeTree();  //是否按操作单位来显示树
	if(showUnitCodeTree!=null&&showUnitCodeTree.equals("1"))
	{
		String unitcodes=userView.getUnit_id();  //UM010101`UM010105`
 		if(unitcodes==null||unitcodes.length()==0||unitcodes.equalsIgnoreCase("UN"))
 			showUnitCodeTree="0";
	}
	
	String url = "/gz/gz_accounting/gz_table.do?b_query=link&salaryid="+salaryid;
	if(returnFlag.equals("1"))
		url+="&gz_module=0&returnFlag=1&theyear="+theyear+"&themonth="+themonth+"&operOrg="+operOrg;
	else if(returnFlag.equals("3"))//若为3 则不显示发放页面返回按钮 zhanghua 2017-8-17
		url+="&returnFlag=3";

%>

<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link href="/css/xtree.css" rel="stylesheet" type="text/css">
<hrms:themes />
<html:form action="/gz/gz_accounting/gz_org_tree"> 

  <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >   
	 <tr align="left">
		<td valign="top"></td>
	 </tr>          
     <tr>        
           <td align="left"> 
                 <hrms:orgtree action="<%=url %>" target="mil_body" flag="0"  loadtype="1" priv="${accountingForm.priv}" showroot="false" dbpre="" rootaction="1" rootPriv="0" nmodule="1"/>			           
           </td>
      </tr>            
   </table>
</html:form>
<script>
<% if(request.getParameter("b_opt")==null){ %>
	
	root.openURL();
	
<%  } %>
</script>
