<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<hrms:themes></hrms:themes>
<link rel="stylesheet" href="/css/tabpane.css" type="text/css">
 <SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>  
  <SCRIPT LANGUAGE=javascript src="/ajax/common.js"></SCRIPT> 
<script language="JavaScript">
	function selchange()
	{
		var service=$F('service');
        interParamForm.action="/system/sms/interface_param_wg.do?b_query=link&service="+service;
        interParamForm.submit();		
	}
</script>
	

<html:form action="/system/sms/interface_param_wg" >

 <table width="535" border="0" cellpadding="0" cellspacing="0" align="center" class="ftable"  style="margin-top:7px;margin-left:2px;">

       <tr class="list3">
           <td align="right" nowrap ><bean:message key="sms.interface.service"/></td>
           <td align="left" nowrap >
              <html:select name="interParamForm" property="service" value="${interParamForm.service}" size="1" onchange="selchange();">
               <html:option  value="BKKJ">博客空间</html:option>
               <html:option  value="HJSJ">集成接口</html:option>
               <html:option  value="CMPP2">中国移动CMPP2</html:option>
               <html:option  value="GSTX">光闪通讯</html:option> 
               <html:option  value="JGJK">金格接口</html:option>               
            </html:select> 
              </td>
             </tr>
             <logic:notEqual name="interParamForm" property="service" value="JGJK">
             <logic:notEqual name="interParamForm" property="service" value="HJSJ">
             <tr class="list3">
				<logic:notEqual name="interParamForm" property="service" value="HJSJ">
                	      <td align="right" nowrap><bean:message key="label.username"/></td>
                	      <td align="left"  nowrap>
                	      		<html:text name="interParamForm" property="userName" size="40" maxlength="400" styleClass="text"/>
                          </td>
				</logic:notEqual>
              </tr>                 
              <tr class="list3">
              	<logic:notEqual name="interParamForm" property="service" value="HJSJ">
                	      <td align="right"  nowrap>
                              <bean:message key="label.mail.password"/>
                        </td>
                	       <td align="left"  nowrap>                          
			 	             	<html:password name="interParamForm" property="password" size="40" maxlength="400" styleClass="text"/>      
                      </td>				                   
              	</logic:notEqual>
               </tr> 
               </logic:notEqual>
                   <tr class="list3">
                	     <td align="right" nowrap valign="middle">
                	     	<logic:equal name="interParamForm" property="service" value="CMPP2">
                	           <bean:message key="sms.interface.cmpp2.ip"/>
							</logic:equal>  
              	            <logic:notEqual name="interParamForm" property="service" value="CMPP2">	
 								<bean:message key="sms.interface.upurl"/>             	            
              	            </logic:notEqual>              	            						              	           
                	     </td>
                	       <td align="left"  nowrap>                          
			 	             <html:text name="interParamForm" property="upUrl" size="40" maxlength="400" styleClass="text"/>       
                         </td>				                   
               </tr>  
               <tr class="list3">
                	     <td align="right" nowrap valign="middle">
                	     	<logic:equal name="interParamForm" property="service" value="CMPP2">
                	           <bean:message key="sms.interface.cmpp2.port"/>
							</logic:equal>   
              	            <logic:notEqual name="interParamForm" property="service" value="CMPP2">	
                	           <bean:message key="sms.interface.downurl"/>          	            
              	            </logic:notEqual> 							               	     
                	     </td>
                	       <td align="left"  nowrap>                          
			 	             <html:text name="interParamForm" property="downUrl" size="40" maxlength="400" styleClass="text"/>       
                   </td>				                   
               </tr> 
               <logic:notEqual name="interParamForm" property="service" value="HJSJ">  
                  <tr class="list3">
                  	<logic:notEqual name="interParamForm" property="service" value="HJSJ">
                	       <td align="right" nowrap valign="middle">
              	              <logic:notEqual name="interParamForm" property="service" value="CMPP2">	                	       
                	             <bean:message key="sms.interface.channelid"/>
              	              </logic:notEqual> 
                	     	  <logic:equal name="interParamForm" property="service" value="CMPP2">
                	           <bean:message key="sms.interface.cmpp2.incode"/>
							  </logic:equal>                	                              	           
                	       </td>
                	       <td align="left"  nowrap>                          
			 	             <html:text name="interParamForm" property="channelId" size="40" maxlength="400" styleClass="text"/>       
                   </td>				                   
					</logic:notEqual>
               </tr> 
               </logic:notEqual> 
               </logic:notEqual>
               <logic:equal name="interParamForm" property="service" value="JGJK">
               		<tr class="list3">
                	     <td align="right" nowrap valign="middle">
                	           <bean:message key="sms.interface.cmpp3.ip"/>
              	       							               	     
                	     </td>
                	       <td align="left"  nowrap>                          
			 	             <html:text name="interParamForm" property="userName" size="40" maxlength="400" styleClass="text"/>       
                   			</td>				                   
               		</tr>
               		<tr class="list3">
                	     <td align="right" nowrap valign="middle">
                	           <bean:message key="sms.interface.cmpp3.port"/> 
              	       							               	     
                	     </td>
                	       <td align="left"  nowrap>                          
			 	             <html:text name="interParamForm" property="password" size="40" maxlength="400" styleClass="text"/>       
                   			</td>				                   
               		</tr>
               </logic:equal>
               <logic:equal name="interParamForm" property="service" value="CMPP2">            	
               		<tr class="list3">
                	     <td align="right" nowrap valign="middle">
                	           <bean:message key="sms.interface.cmpp3.spname"/>
              	       							               	     
                	     </td>
                	       <td align="left"  nowrap>                          
			 	             <html:text name="interParamForm" property="spname" size="40" maxlength="400" styleClass="text"/>       
                   			</td>				                   
               		</tr>
               </logic:equal>
                          <tr class="list3">
                	     <td align="right" nowrap ><bean:message key="parttime.param.flag"/></td>
                	       <td align="left"  nowrap>                          
			 	             <html:checkbox name="interParamForm" property="qy" value="1"/>       
                      </td>				                   
               </tr>  
                                    
                                                 
          <tr class="list3">
            <td align="center" colspan="2" style="height:35px">
            	<hrms:submit styleClass="mybutton" property="b_save">
            		<bean:message key="button.save"/>
	 	          </hrms:submit>
            </td>
          </tr>          
      </table>
  
</html:form>


