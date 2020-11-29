<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hjsj.hrms.actionform.hire.ZpFilterForm"%>
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
	}
%>
<%
	int i=0;
	int flag=0;
%>
<script language="javascript">
function exeReturn(returnStr)
{
  target_url=returnStr;
  window.open(target_url,'il_body'); 
}
</script>
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<hrms:themes></hrms:themes>
<html:form action="/hire/zp_filter/browsemaininfo">
<table width="98%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable3">
<logic:iterate  id="element"    name="zpFilterForm"  property="zpfieldlist" indexId="index"> 
        <logic:equal name="element" property="itemtype" value="D">
         <%
          if(flag==0){
             if(i%2==0){
            %>
              <tr class="trShallow1">            
             <%}
             else
             {%>
               <tr class="trDeep1">  
             <%}
             i++;
             flag=1;          
             }else{
               flag=0;           
             }%>
         <td align="right" nowrap valign="top">        
            &nbsp;&nbsp;<bean:write  name="element" property="itemdesc"/>&nbsp;              
         </td>
         <td align="left"  nowrap valign="top">
            <bean:write name="element" property="fieldvalue"/>&nbsp;
         </td> 
        <%if(flag==0){%>           
           </tr>
        <%}else{%>
            <logic:equal name="element" property="rowflag" value="${index}"> 
               <td colspan="2">
               </td>
               </tr>
            </logic:equal>
        <%}%>        
       </logic:equal>
        <logic:equal name="element" property="itemtype" value="A">
          <%
          if(flag==0){
             if(i%2==0){
            %>
              <tr class="trShallow1">            
             <%}
             else
             {%>
               <tr class="trDeep1">  
             <%}
             i++;
             flag=1;          
             }else{
               flag=0;           
             }%>
         <td align="right" nowrap valign="top">        
             &nbsp;&nbsp;<bean:write  name="element" property="itemdesc"/>&nbsp;      
         </td>
         <td align="left"  nowrap valign="top">
            &nbsp;<bean:write name="element" property="fieldvalue"/>&nbsp;
         </td>
         <%if(flag==0){%>
           </tr>
       <%}else{%>
            <logic:equal name="element" property="rowflag" value="${index}"> 
               <td colspan="2">
               </td>
               </tr>
            </logic:equal>
        <%}%> 
       </logic:equal>
        <logic:equal name="element" property="itemtype" value="N">
         <%
          if(flag==0){
             if(i%2==0){
            %>
              <tr class="trShallow1">            
             <%}
             else
             {%>
               <tr class="trDeep1">  
             <%}
             i++;
             flag=1;          
             }else{
               flag=0;           
             }%>
           <td align="right" nowrap valign="top">        
               &nbsp;&nbsp;<bean:write  name="element" property="itemdesc"/>&nbsp;             
           </td>
           <td align="left"  nowrap valign="top">
               &nbsp;<bean:write name="element" property="fieldvalue"/>&nbsp;
           </td>
         <%if(flag==0){%>
           </tr>
         <%}else{%>
            <logic:equal name="element" property="rowflag" value="${index}"> 
               <td colspan="2">
               </td>
               </tr>
            </logic:equal>
        <%}%> 
       </logic:equal>
        <logic:equal name="element" property="itemtype" value="M">
           <%
          if(flag==0){
             if(i%2==0){
            %>
              <tr class="trShallow1">            
             <%}
             else
             {%>
               <tr class="trDeep1">  
             <%}
             i++;
             flag=1;%>
                 <td align="right" nowrap valign="top">        
                    &nbsp;&nbsp;<bean:write  name="element" property="itemdesc"/>&nbsp;            
                 </td>
                 <td align="left"   valign="top"  colspan="3">
                  &nbsp;<bean:write name="element" property="fieldvalue"/>&nbsp;
                 </td> 
             <%       
             }else{
               flag=0;%>               
              <td colspan="2">
              </td>
              </tr>
               
             <%
            if(flag==0){
              if(i%2==0){
             %>
              <tr class="trShallow1">            
             <%}
             else
             {%>
               <tr class="trDeep1">  
             <%}
             i++;
             flag=1;          
             }else{
               flag=0;           
             }%>               
                <td align="right" nowrap valign="top">        
                   &nbsp;&nbsp;&nbsp;&nbsp;<bean:write  name="element" property="itemdesc"/>&nbsp;          
                </td>
                <td align="left"   valign="top" colspan="3">
                  &nbsp;<bean:write name="element" property="fieldvalue"/>&nbsp;
                </td> 
               
               <%         
             }%>
         
           <%flag=0;%>
           
          </tr>
       </logic:equal>  
</logic:iterate>    
</table>
<table width="80%" border="0" cellspacing="1"  align="center" cellpadding="1">
<br>
    <tr>  
       <td align="center">
          <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/hire/zp_filter/browsemaininfo.do?br_returnfilter=query')">
       </td>
    </tr>
 </table>
</html:form>
