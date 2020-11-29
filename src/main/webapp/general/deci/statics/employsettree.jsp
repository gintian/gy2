<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hjsj.hrms.actionform.general.deci.statics.MakeupAnalyseForm"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
%>
<hrms:themes></hrms:themes>
<html:form action="/general/deci/statics/employmakeupanalysebrowse"> 
   <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" background="/images/back1.jpg" >
      <% MakeupAnalyseForm makeupAnalyseForm=(MakeupAnalyseForm)session.getAttribute("makeupAnalyseForm");
      if(makeupAnalyseForm.getInfosetlist()!=null && makeupAnalyseForm.getInfosetlist().size()>0){%>     
          <tr>
           <td align="left"  >
             &nbsp;<hrms:ole name="makeupAnalyseForm" dbpre="makeupAnalyseForm.dbpre" a0100="a0100" scope="session" height="120" width="85"/>
           </td>
          </tr>   
       <%}%>   
       
      <logic:iterate  id="setlist"   name="makeupAnalyseForm"  property="infosetlist"> 
      <logic:equal name="setlist" property="fieldsetid" value="A01">   
         <tr>
           <td align="left"  nowrap>
              &nbsp; <a href="/general/deci/statics/employmakeupanalysebrowse.do?b_main=link&a0100=${makeupAnalyseForm.a0100}&setname=${setlist.fieldsetid}" target="mil_body"><font styleClass="settext"> <bean:write  name="setlist" property="customdesc"/></font></a>
             </td>
           </tr>
          </logic:equal>
           <logic:notEqual name="setlist" property="fieldsetid" value="A01">   
               <logic:notEqual name="setlist" property="fieldsetid" value="A00">   
                <tr>
                  <td align="left"  nowrap>
                   &nbsp; <a href="/general/deci/statics/employmakeupanalysebrowse.do?b_detail=link&a0100=${makeupAnalyseForm.a0100}&setname=${setlist.fieldsetid}" target="mil_body"><font styleClass="settext"> <bean:write  name="setlist" property="customdesc"/></font></a>
                  </td>
                 </tr>
               </logic:notEqual>
              <logic:equal name="setlist" property="fieldsetid" value="A00">   
                <tr>
                  <td align="left"  nowrap>
                   &nbsp; <a href="/general/deci/statics/employmakeupanalysebrowse.do?b_media=link&a0100=${makeupAnalyseForm.a0100}&setname=A00" target="mil_body"><font styleClass="settext"> <bean:write  name="setlist" property="customdesc"/></font></a>
                  </td>
                 </tr>
               </logic:equal>
            </logic:notEqual>          
         </logic:iterate>        
   </table>
</html:form>
