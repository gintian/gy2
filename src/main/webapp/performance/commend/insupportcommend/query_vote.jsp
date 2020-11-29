<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
 <html:form action="/performance/commend/insupportcommend/query_vote">
	<Br>
	<br>
<table width="90%" border="0" cellspacing="1"  align="center" cellpadding="1">
<tr>
<td align="left" nowrap>

(<bean:message key="columns.archive.unit"/>:<bean:write name="inSupportCommendForm" property="b0110"/>&nbsp;&nbsp;
<bean:message key="columns.archive.um"/>:<bean:write name="inSupportCommendForm" property="e0122"/>&nbsp;&nbsp;
<bean:message key="columns.archive.name"/>:<bean:write name="inSupportCommendForm" property="a0101"/>
)
</td>
<tr>
<td><!-- 【5802】干部考察：结果分析/后备推荐结果分析和投票状况分析显示界面线太粗    jingq upd 2014.12.22 -->
	<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
	 <tr>
            <td align="center" class="TableRow" nowrap>
            推荐时间
             </td>
            <td align="center" class="TableRow" nowrap>
	        推荐名称
	   	 	</td>
            <td align="center" class="TableRow" nowrap>
	       得票数
	   		 </td>		     	        	        
        </tr>
   	  </thead>
   	  <% int i=0;%>
   	   <logic:iterate id="element" name="inSupportCommendForm" property="yearList"  offset="0"> 
	   	       <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow">
          <%}
          else
          {%>
          <tr class="trDeep">
          <%
          }
          i++;          
          %> 
            <td align="right" class="RecordRow" nowrap>
            &nbsp;<bean:write name="element" property="p0205" />&nbsp; 
	         
	   	 	</td>
            <td align="left" class="RecordRow" nowrap>
	          &nbsp;<bean:write name="element" property="p0203" />&nbsp;	 
	   		 </td>
                   
		    <td align="right" class="RecordRow" nowrap>
			&nbsp;<bean:write name="element" property="p0304" />&nbsp;
		    </td>	           
         </tr>
   	     </logic:iterate>
   	  </table>
   	  </td>
   	  </tr>
   	  <tr>
   	  <td align="center" nowrap>
   	  <input type="button" class="mybutton" value="<bean:message key="button.close"/>" name="clo" onclick="window.close()"/>
   	  </td>
   	  </tr>
   	  </table>
   	  </html:form>