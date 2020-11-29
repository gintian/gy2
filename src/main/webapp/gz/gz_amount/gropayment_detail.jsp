<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.gz.gz_amount.CroPayMentForm"%>
<%@ page import="java.util.*"%>
<%@ page import="com.hrms.hjsj.sys.FieldItem"%>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean" %>
<script type="text/javascript">
<!--
function bak()
{
     croPayMentForm.action = "/gz/gz_amount/gropayment.do?b_query=link&opt=2";
     croPayMentForm.submit();
}
//-->
</script>
<html:form action="/gz/gz_amount/gropayment">
<br>
<br>
<table width="90%" align="center" border="0" cellspacing="0" cellpadding="0">
<tr>
<td align="left">
(${croPayMentForm.unitName }&nbsp;年份:&nbsp;${croPayMentForm.yearnum})
</td>
</tr>
<tr>
<td>
<table width="100%" align="center" border="0" cellspacing="0" cellpadding="0" class="ListTable">
<thead>
<tr>
<%
        CroPayMentForm croPayMentForm = (CroPayMentForm)session.getAttribute("croPayMentForm");
        ArrayList columnList = croPayMentForm.getColumnList();
        ArrayList infoList  = croPayMentForm.getInfoList();
        for(int i=0;i<columnList.size();i++)
        {
          FieldItem cd = (FieldItem)columnList.get(i);

          out.println("<td align=\"center\" nowrap class=\"TableRow\">");
          out.println(cd.getItemdesc());
           out.println("</td>");
         } 
         %>
 
</tr>
</thead>
    <%
       for(int j=0;j<infoList.size();j++)
       {
           String styleclass="trShallow";
           if(j%2==0)
              styleclass="trShallow";
           else
              styleclass="trDeep";
           out.println("<tr class=\""+styleclass+"\">");
           LazyDynaBean bean = (LazyDynaBean)infoList.get(j);
           String align="left";
           for(int i=0;i<columnList.size();i++)
           {
              FieldItem cd = (FieldItem)columnList.get(i);
              if(cd.getItemtype().equalsIgnoreCase("N"))
                  align="right";
              out.println("<td align=\""+align+"\" nowrap class=\"RecordRow\">");
              out.println("&nbsp;"+(String)(bean.get(cd.getItemid().toLowerCase())==null?"":bean.get(cd.getItemid().toLowerCase()))+"&nbsp;");
              out.println("</td>");
         } 
           out.println("</tr>");
       }
     %>

</table>
</td>
</tr>
<tr><td>
   <html:hidden name="croPayMentForm" property="codeitemid"/> 
   <html:hidden name="croPayMentForm" property="code"/>
   <html:hidden name="croPayMentForm" property="yearnum"/>
   <html:hidden name="croPayMentForm" property="filtervalue"/>
</td>
</tr>

<tr>
<td align="center">
<input type="button" value="<bean:message key="button.return"/>" onclick="bak();" class="mybutton"/>
</td>
</tr>
</table>
</html:form>
