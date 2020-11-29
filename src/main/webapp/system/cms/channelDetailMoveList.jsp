<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>

<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String bosflag= userView.getBosflag();//得到系统的版本号
    
%>  


<html:form action="/sys/cms/channelDetailMoveList">
<div id="first" style="filter:alpha(Opacity=100);display=block;">
<%
    if(bosflag!=null&&!bosflag.equals("hcm")){
%>
<Br>
<br>
<%
}
%>
<table width="65%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="button.change_content_sort"/>
            </td>            	        	        	        
           </tr>
   	  </thead>
   	  <tr>
   	   <td class="RecordRow">
   	   <table width="100%" >
   	   <tr>
   	   <td>
   	            <html:select name="channelContentDetailForm" property="right_fields" multiple="multiple" size="10" style="height:230px;width:100%;font-size:9pt">
                      <html:optionsCollection property="move_list" value="dataValue" label="dataName"/>
                    </html:select>   	     
   	   </td>
   	    <td width="8%" align="center">
                   <html:button  styleClass="mybutton" property="b_up" onclick="upItem($('right_fields'));">
            		     <bean:message key="button.previous"/> 
	           </html:button >
	           <br>
	           <br>
	           <html:button  styleClass="mybutton" property="b_down" onclick="downItem($('right_fields'));">
            		     <bean:message key="button.next"/>    
	           </html:button >	     
             </td>      
   	  </tr>
   	  </table>
   	  </td></tr>
          <tr>
          <td align="center"  nowrap  colspan="3">
              <html:submit styleClass="mybutton" property="b_savemove" onclick="setselectitem('right_fields');" style="margin-top:5px;">
            		      <bean:message key="button.save"/>
	      </html:submit> 	       
          </td>
          </tr>   
</table>
</div>
</html:form>
