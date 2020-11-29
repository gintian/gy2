<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
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
<%
	String remindType = request.getParameter("remindType");
%>
<body>
<br>
<table align="center" width="90%" >
  <tr>
     <td valign='top'> 
     <div class="keyMatterDiv common_border_color">      
	   <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" class="MyListTable">
	   	  <thead>
            <tr height="35">            
            	<td align="left" class="TableRow" colspan="2" style="border-left: none;border-right: none">            
					&nbsp;<bean:message key="system.options.message.register"/>
				</td>
            </tr>
          </thead>     
          	<tr>            
            	<td align="center">
            		<br/><br/><br/><br/><br/>
            		<%if("1".equals(remindType)) {%>            
						您今天有缺刷卡，请及时补刷卡或填写请假单！

					<%} else {%>
						您还未打卡（签到），请抓紧时间打卡（签到）！
					<%} %>
				</td>
            </tr>                       
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
</body>