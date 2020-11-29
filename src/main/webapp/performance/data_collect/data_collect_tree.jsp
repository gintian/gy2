<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
	}
%>

<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<hrms:themes />
<link href="/css/xtree.css" rel="stylesheet" type="text/css">
<body style="margin-left:0px;margin-top:0px">
<html:form action="/performance/data_collect/data_collect"> 

  <table width="600" border="0" cellspacing="1"  align="center" cellpadding="1"  style="padding-left:2px;width:expression(document.body.clientWidth);overflow: auto;">   

	 			<tr  class="toolbar"   >
				<td valign="middle" align="left">					
					<hrms:priv func_id="0608060111">  
  				   <a href="/gz/tempvar/viewtempvar.do?b_query=link&state=<bean:write name='data_collectForm' property='fieldsetid'/>&type=5&nflag=5" target="mil_body"><img src="/images/copy.gif " alt="临时变量" border="0" align="middle"></a>  
  				   </hrms:priv>  
					<hrms:priv func_id="0608060112"> 
  				     <a href="/gz/formula/viewformula.do?b_query=link&salaryid=-2&state=<bean:write name='data_collectForm' property='fieldsetid'/>" target="mil_body"><img src="/images/past.gif " alt="计算公式" border="0" align="middle"></a>   
  				     </hrms:priv>  
                     <hrms:priv func_id="0608060113">
					<a href="/performance/data_collect/data_collect.do?b_setpama=link" target="mil_body"><img src="/images/img_o.gif"  alt="参数设置" border="0" align="middle"></a> 
					</hrms:priv>  
				</td>
			</tr>        
     <tr>        
           <td align="left"> 
                 <hrms:orgtree action="/performance/data_collect/data_collect.do?b_query=link" target="mil_body" flag="0"  loadtype="1" priv="1"  showroot="false" dbpre="" rootaction="1" rootPriv="0" />			           
           </td>
      </tr>            
   </table>
</html:form>
</body>
<script>
	 root.openURL();
</script>