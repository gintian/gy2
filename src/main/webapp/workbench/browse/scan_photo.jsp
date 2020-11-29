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
<script type="text/javascript" src="/js/wz_tooltip.js"></script>
<script type="text/javascript" src="/js/bigphoto.js"></script>
<script language="javascript">
function winhref(a0100,target)
{
   if(a0100=="")
      return false;
      <logic:equal value="scanstandardduty" property="returnvalue" name="browseForm">
      browseForm.action="/workbench/browse/showselfinfo.do?b_search=link&userbase=${browseForm.userbase}&a0100="+a0100+"&flag=notself&returnvalue=scanstandardduty";
   </logic:equal>
   <logic:notEqual value="scanstandardduty" property="returnvalue" name="browseForm">
   browseForm.action="/workbench/browse/showselfinfo.do?b_search=link&userbase=${browseForm.userbase}&a0100="+a0100+"&flag=notself&returnvalue=scanp";
   </logic:notEqual>
    
    browseForm.target=target;
    browseForm.submit();
   
      
}
   document.oncontextmenu = function() {return false;}
function viewInfo()
{
	<logic:equal value="scanstandardduty" property="returnvalue" name="browseForm">
		browseForm.target="mil_body";
   </logic:equal>
   browseForm.action="/workbench/browse/scaninfodata.do?br_return=link";
   browseForm.submit();
}
$(checkScreenWidth);
/* 
 * 根据屏幕宽度动态修改照片墙的样式  
 */
function checkScreenWidth(){
	if(window.screen.width < 1024) {
		$("#photo_tab").css({'margin-left':'-8px'});
	}
} 
</script>

<hrms:themes />
<html:form action="/workbench/browse/scan_photo">
<center>
<table width="70%" border="0" cellspacing="0"  align="center" cellpadding="0">
          <hrms:paginationdb id="element" name="browseForm" sql_str="browseForm.strsql" table="" where_str="browseForm.cond_str" columns="A0100,B0110,E0122,E01A1,A0101,UserName," order_by="browseForm.order_by" pagerows="${browseForm.pagerows}" page_id="pagination" keys="">
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
          %>
          <td align="center" width="25%" nowrap>
          <!-- 标识：2023 人员信息浏览显示照片墙风格按蓝轩设计公司样式更改 xiaoyun start -->
          <ul class="photos">
          	<li>
          	 <hrms:ole name="element" photoWall="true" dbpre="browseForm.userbase" href="###" target="nil_body" a0100="a0100" div="" scope="page" height="120" width="85" onclick="winhref('${name}','nil_body')" />
             <div class="detail">
             <logic:equal name="browseForm" property="photolength" value="">
	          	<p><a href="###" onclick="winhref('${name}','nil_body')"><strong><bean:write name="element" property="a0101" filter="true"/></strong></a></p>
            	<p class="linehg">
             	<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="dept" scope="page"/>  	   
                <hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="job" scope="page"/>  
                <hrms:photoviewInfo name="" a0100="" nbase="" isNotSetQuota="true" departName="${dept.codename}" jobName="${job.codename}" itemid="" scope="page"/>
              	</p>
          	</logic:equal>
          	<logic:notEqual name="browseForm" property="photolength" value="">
          	  <p>          		
                <hrms:photoviewInfo params="'${name}','nil_body'" name="element" a0100="a0100" nbase="${browseForm.userbase}" itemid="browseForm.photo_other_view" scope="page"/>
	          </p>
          	</logic:notEqual>
          	</div>
          </li>
         </ul>
          <!-- 标识：2023 人员信息浏览显示照片墙风格按蓝轩设计公司样式更改 xiaoyun end -->
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
</center>
<table  width="70%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="browseForm" property="pagination" nameId="browseForm" scope="page">
				</hrms:paginationdblink>
		       </td>
		</tr>
</table>
<table  width="70%" align="center">
          <tr>
            <td align="left">       
         	    <input type="button" name="addbutton" value='<bean:message key="workbench.browse.displayinfo"/>' class="mybutton" onclick='viewInfo();' > 
	 	       
            </td>
          </tr>          
</table>
</html:form>