<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<jsp:useBean id="proposeForm" class="com.hjsj.hrms.actionform.propose.ProposeForm" scope="session"/>
<script>

	function checkonchick2(xname)
	{	
		var val = xname;
		if(val.checked==true)
		{
			val.value="on";
			<%
			proposeForm.setReplayCheck("on");
			%>
			
		}
		else
		{	
			
			val.value="";
			<%
			proposeForm.setReplayCheck("");
			%>
						
    		}
    		
       }
</script>

<hrms:priv func_id="110501"> 
<html:form action="/selfservice/propose/replypropose">
      <table width="500" border="0" cellpadding="0" cellspacing="0" align="center" class="ftable" style="margin-top: 6px;">
          <tr height="20">
       		<!--  <td width=10 valign="top" class="tableft"></td>
       		<td width=130 align=center class="tabcenter">&nbsp;<bean:message key="label.suggest.box"/>&nbsp;</td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="500"></td>-->  
       		<td align="left" class="TableRow" colspan="2"><bean:message key="label.suggest.box"/>&nbsp;</td>            	      
          </tr> 
          
                      <tr class="list3">
                	      <td align="right" nowrap valign="top"><bean:message key="column.submit.propose"/></td>
                	      <td align="left"  nowrap>
                	      	<html:textarea name="proposeForm" property="proposevo.string(scontent)" cols="80" rows="12" readonly="true"/>
                          </td>
                      </tr> 

               
         
                      <tr class="list3">
                	      <td align="right" nowrap valign="top"><bean:message key="column.reply.content"/></td>
                	      <td align="left"  nowrap>
                	      	<html:textarea name="proposeForm" property="proposevo.string(rcontent)" cols="80" rows="12" />
                          </td>
                      </tr> 

                
          <tr>
            <td class="framestyle9" colspan="2">
            	<logic:equal name="proposeForm" property="proposevo.string(flag)" value="1">
            	<input type="checkbox" name="replayCheck" checked onchick="checkonchick2(this);"><bean:message key="column.public"/>
            	</logic:equal>
            	<logic:notEqual name="proposeForm" property="proposevo.string(flag)" value="1">
            	<input type="checkbox" name="replayCheck"  onchick="checkonchick2(this);"><bean:message key="column.public"/>
            	</logic:notEqual>
		
		
            </td>
          </tr>                                                              
          <tr class="list3">
            <td align="center" style="height:35px;" colspan="2">
         	<hrms:submit styleClass="mybutton" property="b_save" onclick="document.proposeForm.target='_self';validate('R','proposevo.string(rcontent)','答复内容');return (document.returnValue && ifqrbc());">
            		<bean:message key="button.save"/>
	 	</hrms:submit>
         	<hrms:submit styleClass="mybutton" property="b_return">
            		<bean:message key="button.return"/>
	 	</hrms:submit>            
            </td>
          </tr>          
      </table>
</html:form>
</hrms:priv>
