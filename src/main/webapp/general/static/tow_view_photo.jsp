<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hrms.frame.utility.CodeItem"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%
	 int i=0;
%>
<script src="/phone-app/jquery/jquery-3.5.1.min.js" type="text/javascript"></script>
<script type="text/javascript" src="/js/bigphoto.js"></script>
<script type="text/javascript" src="/js/wz_tooltip.js"></script> 
<script type="text/javascript">
<!--
$(checkScreenWidth);
/* 
 * 根据屏幕宽度动态修改照片墙的样式  
 */
function checkScreenWidth(){
	if(window.screen.width < 1024) {
		$("#photo_tab").css({'margin-left':'-8px'});
	}
} 
//-->
</script>
<hrms:themes />
<html:form action="/general/static/tow_view_photo">
<table id="photo_tab" width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" style="margin-top: -5px;">
          <hrms:paginationdb id="element" name="staticFieldForm" sql_str="staticFieldForm.strsql" table="" where_str="staticFieldForm.cond_str" columns="A0100,B0110,E0122,E01A1,A0101,UserName,db"  order_by="staticFieldForm.order_by" pagerows="20" page_id="pagination">
          <%
          if(i%4==0)
          {
          %>
          <tr>
          <%
          }
          %>    
          <%
             	LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
             	String a0100=(String)abean.get("a0100"); 
                request.setAttribute("name",a0100); 
                 String db=(String)abean.get("db"); 
                request.setAttribute("db",db.substring(1));                            
          %>  
          <%
          if((i+1)%4==0)
          {%>
          <td align="center" nowrap>
          <%
          }else {%>
          <td align="center" width="18%" nowrap>
          <%}
          %>
            <!-- 照片墙（二维统计） xiaoyun 2014-6-14 start -->
            <ul class="photos">
	          		<li>
	          			<a href="/workbench/browse/showselfinfo.do?b_search=link&userbase=${db }&a0100=<bean:write name="element" property="a0100" filter="true"/>&flag=notself&returnvalue=tow" target="il_body">
	          				<hrms:ole name="element" photoWall="true" dbpre="${db }" href="/workbench/browse/showselfinfo.do?b_search=link&userbase=${db }&a0100=${name}&flag=notself&returnvalue=tow&result=${staticFieldForm.result}" target="il_body" a0100="a0100" scope="page" height="120" width="85"/>
	          			</a>
	          			<div class="detail">
	          			<logic:equal name="staticFieldForm" property="photo_other_view" value=""> 
	          				<p>
	          				<a href="/workbench/browse/showselfinfo.do?b_search=link&userbase=${db }&a0100=<bean:write name="element" property="a0100" filter="true"/>&flag=notself&returnvalue=tow&result=${staticFieldForm.result}" target="il_body">
	          				<strong><bean:write name="element" property="a0101" filter="true"/></strong>
	          				</a>
	          				</p>
	          				<p class="linehg">
          						<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="dept" scope="page"/>	      
          						<hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="job" scope="page"/>
          						<hrms:photoviewInfo name="" a0100="" nbase="" isNotSetQuota="true" departName="${dept.codename}" jobName="${job.codename}" itemid="" scope="page"/>
	                		</p>
	                	</logic:equal>
	                	<logic:notEqual name="staticFieldForm" property="photo_other_view" value="">
	                		<p>
	                		<hrms:photoviewInfo name="element" isTowStatic="true" params="/workbench/browse/showselfinfo.do?b_search=link&userbase=${db }&a0100=${name}&flag=notself&returnvalue=tow" a0100="a0100" nbase="${db }" itemid="staticFieldForm.photo_other_view" scope="page"/>
	                		</p>
	                	</logic:notEqual>
	          			</div>
	          		</li>
	          </ul>               
             <!-- 照片墙（二维统计） xiaoyun 2014-6-14 end -->
            </td> 
          <%
          if((i+1)%4==0)
          {%>
          </tr>
          <%
          }
          i++;          
          %>         
        </hrms:paginationdb>
        
</table>
<table  width="70%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor">第
					<bean:write name="pagination" property="current" filter="true" />
					页
					共
					<bean:write name="pagination" property="count" filter="true" />
					条
					共
					<bean:write name="pagination" property="pages" filter="true" />
					页
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="staticFieldForm" property="pagination" nameId="staticFieldForm" scope="page">
				</hrms:paginationdblink>
		       </td>
		</tr>
</table>
<table  width="70%" align="center">
          <tr>
            <td align="left">       
             <html:hidden name="staticFieldForm" property="result" styleClass="text"/>
         	    <hrms:submit styleClass="mybutton" property="br_return">
            		<bean:message key="button.return"/>
	 	        </hrms:submit>
            </td>
          </tr>          
</table>
</html:form>
<script>
var buttons= document.getElementsByTagName('input');
for( var i = 0 ; i < buttons.length ; i++){
	var type = buttons[i].getAttribute('type');
	if(type == 'submit' ){
		buttons[i].focus();
		buttons[i].blur();
	}
}
</script>