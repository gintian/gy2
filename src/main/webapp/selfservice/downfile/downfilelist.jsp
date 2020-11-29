<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.updownfile.DownFileForm,java.util.*"%>
<%@ page import="com.hrms.struts.valueobject.UserView" %>
<%@ page import="com.hjsj.hrms.utils.PubFunc" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%
	int i=0;
	DownFileForm downFileForm=(DownFileForm)session.getAttribute("downFileForm"); 
	String fromPage=downFileForm.getFromPage();
	
	//用于自助服务导航图返回取值判断（从导航图进去显示返回，从左侧树菜单进去隐藏返回）
	UserView userView = (UserView)request.getSession().getAttribute("userView");
	String returnvalue = request.getParameter("returnvalue")==null?"":request.getParameter("returnvalue");
%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<html:form action="/selfservice/downfile/downfilelist">
<table border="0" width="80%" cellspacing="0"  align="center" cellpadding="0" style="margin-top:8px;">
<tr> 
<td>
 <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
                  
            <td align="center" class="TableRow" nowrap>
		<bean:message key="conlumn.resource_list.name"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="conlumn.resource_list.descrption"/>&nbsp;
	    </td>
                    
            <td align="center" class="TableRow" nowrap>
		<bean:message key="conlumn.resource_list.createdate"/>&nbsp;            	
	    </td>
           	    	    		        	        	        
           </tr>
   	  </thead>
          <hrms:extenditerate id="element" name="downFileForm" property="downFileForm.list" indexes="indexes"  pagination="downFileForm.pagination" pageCount="10" scope="session">
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
             
        <bean:define id="contentid" name="element" property="string(contentid)"></bean:define>       
            <bean:define id="fileid" name="element" property="string(fileid)"></bean:define>  
        <td align="left" class="RecordRow" nowrap width="300" style="word-break:break-all">
            <logic:equal name="element" property="string(status)" value="1">
                <logic:notEqual name="element" property="string(ext)" value="">
	                <!-- 20/3/4 xus vfs改造  -->
	                <logic:empty name="element" property="string(fileid)">
	                	&nbsp;<bean:write name="element" property="string(name)" filter="true"/>&nbsp;
	                </logic:empty>
	                <logic:notEmpty name="element" property="string(fileid)">
	                	&nbsp;<a href="/servlet/vfsservlet?fileid=<%=fileid%>" target="_blank"><bean:write name="element" property="string(name)" filter="true"/></a>&nbsp;
	                </logic:notEmpty>
                </logic:notEqual> 
            </logic:equal> 
	    </td>
         
        <td align="left" class="RecordRow" nowrap width="500" style="word-break:break-all">
                   &nbsp;<a href="/selfservice/propose/viewresourcefile.do?b_query=link&encryptParam=<%=PubFunc.encrypt("a_id="+contentid) %>"><bean:write name="element" property="string(name)" filter="true"/></a>&nbsp;
            	<!--
                   <bean:write  name="element" property="string(description)" filter="false"/>&nbsp;
                   -->
	    </td>
            <td align="left" class="RecordRow" width="50px" nowrap>
            	              	    
                   &nbsp;<bean:write  name="element" property="string(createdate)" filter="true"/>&nbsp;
	    </td>
           
          
            	    	    		        	        	        
          </tr>
        </hrms:extenditerate>
        
</table>
</td>
</tr>
<tr>
<td>
<table  width="100%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="downFileForm" property="downFileForm.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="downFileForm" property="downFileForm.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="downFileForm" property="downFileForm.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right">
		          <hrms:paginationlink name="downFileForm" property="downFileForm.pagination"
				nameId="downFileForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
</td>
</tr>
</table>
<table  width="70%" align="center">
          <tr>
            <td align="center">
         	
         	&nbsp;
         	<% if(fromPage!=null&&fromPage.trim().equals("front")){ %>
         	
         <input type="button" value='返回' class="mybutton" onclick="javacript:location.href='/templates/index/portal.do?b_query=link';"/>


         	
         	<% } %>
        
            </td>
          </tr>          
</table>
<table width="70%" align="center">
	<tr>
		<td align="center">
		<%
		if(userView!=null && userView.getBosflag()!=null && returnvalue.equals("dxt"))
		{
		%>
			<!-- 自助服务导航图返回 -->
			<input type="button" name="b_return" value="<bean:message key="button.return"/>" class=mybutton  onclick="hrbreturn('selfinfo','il_body','downFileForm')">
		<%} %>
		</td>
	</tr>
</table>
</html:form>
<script>
if(parent.myNewBody!=null)
 {
  parent.myNewBody.cols="*,0"
 }
 
  
 
</script>

