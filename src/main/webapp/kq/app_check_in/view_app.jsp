<%@ page errorPage="error.jsp"%>
<%@ page import="javax.servlet.ServletException"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.kq.kqself.KqSelfForm"%>
<%@ page import="com.hrms.hjsj.sys.FieldItem"%>
<script language="javascript">
   function toallapp(table)
   {
    appForm.action="/kq/app_check_in/all_app_data.do?b_search2=link&table="+table+"&dotflag=1";
    appForm.submit();    
   }
</script>
<html:form action="/kq/app_check_in/view_app">
   <br><br>
  <table width="400" border="0" cellpadding="0" cellspacing="0" align="center">
    <tr height="20">
    <td colspan="4">
        <table width="100%" border="0" cellpadding="0" cellspacing="0" align="left">
          <tr>
             <!--  <td height="20" width=10 valign="top" class="tableft"></td>
       	    <td width=130 align=center class="tabcenter"><bean:message key="lable.view_overtime"/></td>
       	    <td width=10 valign="top" class="tabright"></td>
       	    <td valign="top" class="tabremain" width="500"></td> -->
       	    <td  align=center class="TableRow"><bean:message key="lable.view_overtime"/></td>
          </tr>
        </table>
    </td>
    </tr> 
    <tr>
    <td colspan="4" class="framestyle9">
            <table border="0" cellpmoding="5" cellspacing="0"  class="DetailTable"  cellpadding="0" style="margin-left: 15px;">      
                <br><br>              
                <logic:iterate id="element" name="appForm"  property="viewlist" indexId="index"> 
                   <logic:equal name="element" property="visible" value="true">
                    <tr>           
                     <td width="30%" align="right" class="tdFontcolor" nowrap >                
                        <bean:write  name="element" property="itemdesc" filter="true"/>:
                     </td>
                     <!--日期型 -->                            
                     <logic:equal name="element" property="itemtype" value="D">
                        <td align="left" class="tdFontcolor" nowrap>                
                           <bean:write name="appForm" property='<%="viewlist["+index+"].value"%>' filter="true"/>
                        </td>                           
                     </logic:equal>
                     <!--备注型 -->                              
                     <logic:equal name="element" property="itemtype" value="M">
                        <td align="left" class="tdFontcolor">                
                            <bean:write name="appForm" property='<%="viewlist["+index+"].value"%>' filter="true" />                
                        </td>                           
                     </logic:equal>
                     <!--字符型 -->                                                    
                     <logic:equal name="element" property="itemtype" value="A">
                        <td align="left" class="tdFontcolor" nowrap>
                          <logic:notEqual name="element" property="codesetid" value="0">
                            <html:hidden name="appForm" property='<%="viewlist["+index+"].value"%>' styleClass="text"/>                               
                            <bean:write name="appForm" property='<%="viewlist["+index+"].viewvalue"%>' filter="true"/>
                          </logic:notEqual> 
                          <logic:equal name="element" property="codesetid" value="0">
                             <bean:write name="appForm" property='<%="viewlist["+index+"].value"%>' filter="true"/>                              
                          </logic:equal>                               
                        </td>                           
                      </logic:equal> 
                      <!--数据值-->                            
                      <logic:equal name="element" property="itemtype" value="N">
                         <td align="left" class="tdFontcolor" nowrap>     
                         <logic:equal name="element" property="itemid" value="q1104">   
                               <%
             	                           FieldItem item=(FieldItem)pageContext.getAttribute("element");
             	                           String id=(String)item.getValue();       	                           
                               %>
                                        <hrms:kqclassname classid="<%=id%>"></hrms:kqclassname>
                          </logic:equal> 
                           <logic:equal name="element" property="itemid" value="q1504">   
                               <%
             	                           FieldItem item=(FieldItem)pageContext.getAttribute("element");
             	                           String id=(String)item.getValue();       	                           
                               %>
                                        <hrms:kqclassname classid="<%=id%>"></hrms:kqclassname>
                          </logic:equal> 
                          <logic:equal name="element" property="itemid" value="q1304">   
                               <%
             	                           FieldItem item=(FieldItem)pageContext.getAttribute("element");
             	                           String id=(String)item.getValue();       	                           
                               %>
                                        <hrms:kqclassname classid="<%=id%>"></hrms:kqclassname>
                          </logic:equal> 
                          <logic:notEqual name="element" property="itemid" value="q1504">
                          <logic:notEqual name="element" property="itemid" value="q1104">  
                          <logic:notEqual name="element" property="itemid" value="q1304"> 
                          <bean:write name="appForm" property='<%="viewlist["+index+"].value"%>' filter="true"/>       
                          </logic:notEqual>                     
                          </logic:notEqual>          
                          </logic:notEqual>     
                          </td>                           
                      </logic:equal>                           
                    </tr>   
                   </logic:equal>
                 </logic:iterate>                
             </table>
             <br>
               <tr class="list3">
                 <td align="center" colspan="4" style="height:35px;">
               <logic:equal name="appForm" property="table" value="Q11"> 
	 	              <html:button  styleClass="mybutton" property="b_return" onclick="toallapp('Q11');">
            	      <bean:message key="button.return"/>    
	                </html:button >
	            </logic:equal>  
	           <logic:equal name="appForm" property="table" value="Q15"> 
	 	              <html:button  styleClass="mybutton" property="b_return" onclick="toallapp('Q15');">
            	        <bean:message key="button.return"/>    
	            </html:button >
	           </logic:equal>  
	           <logic:equal name="appForm" property="table" value="Q13"> 
	 	          <html:button  styleClass="mybutton" property="b_return" onclick="toallapp('Q13');">
            	        <bean:message key="button.return"/>    
	            </html:button >
	           </logic:equal>  
	         </td>
           </tr>
         </table>            	
        
</html:form>



