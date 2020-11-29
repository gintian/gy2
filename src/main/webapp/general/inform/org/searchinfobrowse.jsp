<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.hjsj.sys.FieldItem"%>
<%@ page import="com.hrms.frame.dao.RecordVo"%>
<script type="text/javascript" src="/js/wz_tooltip.js"></script>  
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    boolean version = false;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String bosflag="";	
	if(userView != null){
	  css_url=userView.getCssurl();
	  bosflag=userView.getBosflag();   
      if(userView.getVersion()>=50){//版本号大于等于50才显示这些功能
		version = true;
	  }	 
    }
	
%>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
<script language="javascript">
function exeReturn(returnStr,target)
{
  target_url=returnStr;
  window.open(target_url,target); 
}
</script>
<html:form action="/general/inform/org/searchorgbrowse"> 
<logic:equal value="relation" name="infoBrowseForm" property="returnvalue">
	&nbsp;&nbsp;&nbsp;&nbsp;<input type="button" name="returnbutton"  value="<bean:message key="button.close"/>" class="mybutton" onclick="top.close();">                 
</logic:equal>
<table width="100%" cellpadding="0" cellspacing="0" border="0" style="margin:0px;"><!--update by xiegh ondate20180309 bug35289  -->
  <tr>
    <td>
      <hrms:infobrowse nid="${infoBrowseForm.nid}" infokind="${infoBrowseForm.infokind}" orgtype="${infoBrowseForm.orgtype}" pre="usr" isinfoself="1" setflag="1"/>
    </td>
  </tr>
   <tr style="padding-left:1px;">
    <td>

<%if(bosflag!=null&&(bosflag.equals("hl") || bosflag.equals("hcm"))) {//6.0版本%>
<logic:equal name="infoBrowseForm" property="returnvalue" value="scan">
            <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/orginfo/searchorginfodata.do?b_query=link&code=${infoBrowseForm.return_codeid}','_self')">                 
</logic:equal>
<logic:equal name="infoBrowseForm" property="returnvalue" value="dxt">
 <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="hrbreturn('org','2','infoBrowseForm');">                 
</logic:equal>
<logic:equal name="infoBrowseForm" property="returnvalue" value="leaderdxt">
 <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="hrbreturn('leader','2','infoBrowseForm');">                 
</logic:equal>
<%}else{//5.0版本 %>
<logic:equal name="infoBrowseForm" property="returnvalue" value="scan">
          <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/orginfo/searchorginfodata.do?b_query=link&code=${infoBrowseForm.return_codeid}','_self')">                 
</logic:equal>
<%} %>
    </td>
  </tr>
</table>
</html:form>
<!-- zxj 上边infobrowse标签中使用了css1,所以换肤标签只能放这里了 -->
<hrms:themes></hrms:themes>
<!-- FCKeditor 输出大号字体重叠问题    BEGIN  -->
<script>
   var pELE = document.getElementsByTagName("p");
   for(var k=0;k<pELE.length;k++)
     pELE[k].style.lineHeight='normal';
if(!getBrowseVersion()){//兼容非IE浏览器样式问题 
	var childFrame = parent.document.getElementById('childFrame');//调整iframe的高度不出现2个滚动条    wangb 20180207
	childFrame.style.height = '940px';
}
     
     
</script>
<!-- END -->