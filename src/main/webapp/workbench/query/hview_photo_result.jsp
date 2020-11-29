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
function winhref(nbase,a0100,returnvalue)
{
   if(a0100=="")
      return false;
   var o_obj=document.getElementById('a0100');   
   if(o_obj)
     o_obj.value=a0100;
    highQueryForm.action="/workbench/browse/showselfinfo.do?b_search=link&userbase="+nbase+"&flag=notself&returnvalue="+returnvalue+"";
    highQueryForm.submit();
}
document.oncontextmenu = function(e) {return false;}
$(checkScreenWidth);
/* 
 * 根据屏幕宽度动态修改照片墙的样式  
 */
function checkScreenWidth(){
	if(window.screen.width < 1024) {
		$("#photo_tab").css({'margin-left':'-6px'});
	}
}
</script>
<hrms:themes />

<html:form action="/workbench/query/hview_photo_result">
<input type="hidden" name="a0100" id="a0100">
<table id="photo_tab" width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" style="margin-top: -6px;">
          <hrms:paginationdb id="element" name="highQueryForm" sql_str="highQueryForm.strsql" table="" where_str="highQueryForm.strwhere" columns="highQueryForm.columns" order_by="highQueryForm.order" page_id="pagination" pagerows="20" distinct="${highQueryForm.distinct}" keys="${highQueryForm.keys}">
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
                String nbase=(String)abean.get("nbase");              	
                request.setAttribute("name",a0100); 
                request.setAttribute("nbase",nbase);    	                           
          %>   
          
          <td align="center" nowrap>
          	<!-- 照片墙统一风格（简单查询） xiaoyun 2014-6-14 start -->
	      	 
	     <ul class="photos">
	       <li>
          	<hrms:ole name="element" photoWall="true" dbpre="${nbase}" a0100="a0100" href="###" onclick="winhref('${nbase}','${name}','44');" scope="page" height="120" width="85"/>
             <div class="detail">
             <logic:equal name="highQueryForm" property="photolength" value="">
          		<p><a href="###" onclick="winhref('${nbase}','${name}','44');"><strong><bean:write name="element" property="a0101" filter="true"/></strong></a></p>
          		<p class="linehg">
          	   		<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="dept" scope="page"/>           	   		
                	<hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="job" scope="page"/>
                	<hrms:photoviewInfo name="element" a0100="a0100" nbase="${nbase}" isNotSetQuota="true" departName="${dept.codename}" jobName="${job.codename}" itemid="highQueryForm.photo_other_view" scope="page"/>
          	    </p> 
          	</logic:equal>
          	<logic:notEqual name="highQueryForm" property="photolength" value="">
           		<p>
                	<hrms:photoviewInfo name="element" params="${nbase},${name},44" a0100="a0100" nbase="${nbase}" itemid="highQueryForm.photo_other_view" scope="page"/>
                </p>
          	</logic:notEqual>
          	</div>
          </li>
         </ul>
          	<!-- 照片墙统一风格（简单查询） xiaoyun 2014-6-14 end -->
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
		          <p align="right"><hrms:paginationdblink name="highQueryForm" property="pagination" nameId="highQueryForm" scope="page">
				</hrms:paginationdblink>
		       </td>
		</tr>
</table>
<table  width="70%" align="center">
          <tr>
            <td align="center">
         	    <hrms:submit styleClass="mybutton" property="br_return">
            		<bean:message key="button.return"/>
	 	    </hrms:submit>
            </td>
          </tr>          
</table>
</html:form>
