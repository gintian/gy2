<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,com.hrms.struts.constant.WebConstant,com.hrms.struts.taglib.CommonData,
				 com.hjsj.hrms.actionform.hire.parameterSet.ParameterForm, com.hrms.struts.valueobject.UserView,org.apache.commons.beanutils.LazyDynaBean"%>

<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script type="text/javascript">
<!--
function sub()
{
     var cardsObj=document.getElementsByName("cardis");
     var ids="";
     if(cardsObj)
     {
        for(var i=0;i<cardsObj.length;i++)
        {
           ids+="`"+cardsObj[i].value;
        }
     }
     window.returnValue=ids.substring(1);
     window.close();
}
function closeW()
{
     window.returnValue="no";
     window.close();
}
//-->
</script>
<html:form action="/hire/parameterSet/configureParameter">
   <table border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" style="margin-top:5px;">
   <tr>
     <td align="left" class="TableRow">设置登记表</td>
   </tr>
   <tr><td align="center" class="RecordRow">
   <div style="width: 430px; overflow: auto;">
    <table border="0" cellspacing="0" align="center" cellpadding="0">
        <%
          ParameterForm parameterForm=(ParameterForm)session.getAttribute("parameterForm2");
          ArrayList cardList=parameterForm.getCardList();
          ArrayList previewTableList=parameterForm.getPreviewTableList();
          for(int i=0;i<cardList.size();i++)
          {
              LazyDynaBean bean =(LazyDynaBean)cardList.get(i);
              String desc=(String)bean.get("codeitemdesc")+"登记表";
              String value=(String)bean.get("value");
           %>
           <tr height="30"><td align="right" width="25%" style="padding-right: 5px;"><%=desc%></td>
           <td align="left" width="75%">
           <select name="cardis">
           <%
              for(int j=0;j<previewTableList.size();j++)
              {
                   CommonData cd = (CommonData)previewTableList.get(j);
                   String cdesc=cd.getDataName();
                   String avalue=cd.getDataValue();
                   String select="";
                   
                   String omdesc="";
                   if(cdesc.length() > 20)
                       omdesc = cdesc.substring(0,20)+"...";
                   else
                       omdesc = cdesc;
                   
                   if(avalue.equalsIgnoreCase(value)) {
                      select="selected";
                      %>
                      <script type="text/javascript">
                      document.getElementsByName("cardis")[<%=i%>].title='<%=cdesc%>';
                      </script>
                  <%}
               %>
                <option value="<%=avalue%>" title="<%=cdesc%>" <%=select%>><%=omdesc%></option>
               <%
              }
            %>
            </select>
           </td>
           </tr>
            
        <%      
          }
         %>
      </table>
      </div>
      </td>
      </tr>
         <tr><td align="center" class="RecordRow" style="padding-top:5px;padding-bottom:3px;">
         <input type="button" class="mybutton" name="ok" value="<bean:message key="button.ok"/>" onclick="sub();"/>
         <input type="button" class="mybutton" name="calcal" value="<bean:message key="button.cancel"/>" onclick="closeW();" style="margin-left:0px;"/>
         </td>
   </table>
</html:form>