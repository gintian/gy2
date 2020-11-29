<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<style>
.keyMatterDiv 
{ 
	overflow:auto; 
	height:270;
	width:100%;	
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; 
} 
.MyListTable 
{
	border:0px solid #C4D8EE;
	border-collapse:collapse; 
	BORDER-BOTTOM: medium none; 
    BORDER-LEFT: medium none; 
    BORDER-RIGHT: medium none; 
    BORDER-TOP: medium none; 
    margin-top:-1px;
    margin-left:-1px;
    margin-right:-1px;
}  
</style>

<body>
<html:form action="/performance/warnPlan/selfNoScoreorCardList">
<br>
<table align="center" width="90%" >
  <tr>
     <td valign='top'> 
     <div class="keyMatterDiv common_border_color">      
	   <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" class="MyListTable">
	   	  <thead>
            <tr height="25">            
            	<td align="left" class="TableRow" colspan="2" >            
					&nbsp;<bean:message key="system.options.message.register" />
				</td>
            </tr>
          </thead>            
            <logic:iterate id="element" name="warnPlanForm" property="planList">									
				<tr>
					<td align="center" width="10%" class="RecordRow" nowrap >
						&nbsp;<img src="/images/forumme.gif">
					</td>
					<td align="left" width="90%" class="RecordRow" >
						&nbsp;<bean:write name="element" property="name" filter="true" />
					</td>
				</tr>									
			</logic:iterate>                     
        </table>
        </div>
     </td>     
  </tr>
  <tr>
    <td align="center">     
      <input type="button" name="btnreturn" value='<bean:message key="button.close"/>' onclick="window.close();" class="mybutton">						      
    </td>
  </tr>
</table>
</html:form>
</body>