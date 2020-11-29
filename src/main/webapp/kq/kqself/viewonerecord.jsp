<%@ page errorPage="error.jsp"%>
<%@ page import="javax.servlet.ServletException"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.hjsj.sys.FieldItem"%>
<%@ page import="com.hjsj.hrms.actionform.kq.kqself.KqSelfForm"%>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>

<script language="javascript" src="/ajax/format.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="javascript">
   function printT(id)
    {
       var tab_name="${kqselfForm.table}";       
       var win=window.open("/servlet/OutputKqTemplateDataServlet?tab_name="+tab_name+"&id="+id,"_blank");
    }
</script>
<html:form action="/kq/kqself/search_kqself">
   <br><br>
    
  <table width="400" border="0" cellpadding="1" cellspacing="0" align="center">
          <tr height="20">
       	    <!--  <td width=10 valign="top" class="tableft"></td>
       	    <td width=130 align=center class="tabcenter"><bean:message key="lable.overtime"/></td>
       	    <td width=10 valign="top" class="tabright"></td>
       	    <td valign="top" class="tabremain" width="500"></td> -->  
		<td align=center class="TableRow"><bean:message key="lable.overtime"/></td>          	      
          </tr> 
          <tr>
          <td class="framestyle9" style="border-top-style: solid;border-top-width: 0px;">
            <table border="0" cellpmoding="0" cellspacing="5"  class="DetailTable"  cellpadding="0" >   
                <tr><td height="10"></td></tr>   
                <logic:iterate id="element" name="kqselfForm"  property="fieldlist" indexId="index"> 
                      <logic:equal name="element" property="visible" value="true">
                           <tr> 
                              <td align="right" class="tdFontcolor" nowrap >                
                                 <bean:write  name="element" property="itemdesc" filter="true"/>:
                              </td>             
                              <!--日期型 -->                            
                              <logic:equal name="element" property="itemtype" value="D">
                                <td align="left" class="tdFontcolor" nowrap> 
                                 <bean:write name="kqselfForm" property='<%="fieldlist["+index+"].value"%>' filter="true"/>
                                </td>  
                              </logic:equal>
                              <!--备注型 -->                              
                              <logic:equal name="element" property="itemtype" value="M">
                                 <td align="left" class="tdFontcolor" nowrap>                
                                      <bean:write name="kqselfForm" property='<%="fieldlist["+index+"].value"%>' filter="true"/>
                                 </td>                           
                              </logic:equal>
                              <!--字符型 -->                                                    
                              <logic:equal name="element" property="itemtype" value="A">
                                 <td align="left" class="tdFontcolor" nowrap>
                                    <logic:notEqual name="element" property="codesetid" value="0">
                            
                            <bean:write name="kqselfForm" property='<%="fieldlist["+index+"].viewvalue"%>' filter="true"/>
                          </logic:notEqual> 
                          <logic:equal name="element" property="codesetid" value="0">
                             <bean:write name="kqselfForm" property='<%="fieldlist["+index+"].value"%>' filter="true"/>                              
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
                                       <logic:notEqual name="element" property="itemid" value="q1104"> 
                                        <logic:notEqual name="element" property="itemid" value="q1504"> 
                                        <logic:notEqual name="element" property="itemid" value="q1304">
                                          <bean:write name="kqselfForm" property='<%="fieldlist["+index+"].value"%>' filter="true"/> &nbsp;   
                                        </logic:notEqual>  
                                        </logic:notEqual>   
                                       </logic:notEqual> 
                                   </td>                           
                                </logic:equal>                           
                              </tr>   
                        </logic:equal>  
                </logic:iterate>
           <tr><td height="10"></td></tr>    
            <tr class="list3">
             <td align="center" colspan="4">   
             
         	 
             </td>
             </tr>    
            <tr><td height="10"></td></tr>         
 	    </table>	            	
           </td>
           </tr>
 
                      
      </table>
      <table align="center">
      	<tr>
      		<td style="height:35px;">
      			<logic:equal name="kqselfForm" property="isTemplate" value="1">    
	 	        <input type="button" name="btnreturn" value='打印' onclick="printT('${kqselfForm.id}');" class="mybutton">	
	 	       </logic:equal>      			 	
	 	<input type="button" name="btnreturn" value='<bean:message key="kq.emp.button.return"/>' onclick="history.back();" class="mybutton">						      
      		</td>
      	</tr>
      </table>
</html:form>

