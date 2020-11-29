<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hrms.frame.utility.CodeItem"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.query.ComplexInterfaceForm" %>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
	int i=0;
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	ComplexInterfaceForm complexInterfaceForm=(ComplexInterfaceForm)session.getAttribute("complexInterfaceForm");
	String dbpre=complexInterfaceForm.getDbpre();
%>
<script src="/phone-app/jquery/jquery-3.5.1.min.js" type="text/javascript"></script>
<script type="text/javascript" src="/js/bigphoto.js"></script>
<script type="text/javascript" src="/js/wz_tooltip.js"></script>
<script language="javascript">
function winhref(nbase,a0100)
{
     if(a0100=="")
      return false;    
    complexInterfaceForm.action="/workbench/browse/showselfinfo.do?b_search=link&userbase="+nbase+"&a0100="+a0100+"&flag=notself&returnvalue=complex_p";
    complexInterfaceForm.target="_self";
    complexInterfaceForm.submit();
}
function document.oncontextmenu() 
   { 
      return　false; 
   } 
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
<hrms:themes></hrms:themes>

<html:form action="/workbench/query/complex_interface_pho">
<table id="photo_tab" width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" style="margin-top: -5px;">
          <hrms:paginationdb id="element" name="complexInterfaceForm" sql_str="complexInterfaceForm.strsql" table="" where_str="" columns="complexInterfaceForm.columns" order_by="complexInterfaceForm.order" pagerows="20" page_id="pagination" keys="">
           <%
          if(i%4==0)
          {
          %>
          <tr align="left">
          <%
          }
          %>             
          
          <%
             	LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
             	String a0100=(String)abean.get("a0100");              	
             	String nbase=(String)abean.get("nbase"); 
             	if(dbpre!=null&&!dbpre.equalsIgnoreCase("All"))    
             	    nbase= dbpre;
                request.setAttribute("name",a0100); 
                request.setAttribute("nbase",nbase);    	                           
          %>
          	 
          	 <!-- 照片墙统一风格（复杂查询） xiaoyun 2014-6-14 start -->
          <td align="center" nowrap>
          <ul class="photos">
          	<li>
          	 <hrms:ole name="element" photoWall="true" dbpre="<%=nbase%>" href="###"  onclick="winhref('${nbase}','${name}');" a0100="a0100" scope="page" height="120" width="85"/>
          	 <div class="detail">
             <logic:equal name="complexInterfaceForm" property="photolength" value="">
        		<p><a href="###" onclick="winhref('${nbase}','${name}');"><strong><bean:write name="element" property="a0101" filter="true"/></strong></a></p>
          		<p class="linehg">
          	    	<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="dept" scope="page"/>
                	<hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="job" scope="page"/>
                	<hrms:photoviewInfo name="element" a0100="a0100" nbase="${nbase}" isNotSetQuota="true" departName="${dept.codename}" jobName="${job.codename}" itemid="complexInterfaceForm.photo_other_view" scope="page"/>
          	    </p>
          	</logic:equal>
          	<logic:notEqual name="complexInterfaceForm" property="photolength" value="">
   				<p>
         			<hrms:photoviewInfo name="element" params="${nbase},${name}" a0100="a0100" nbase="${nbase}" itemid="complexInterfaceForm.photo_other_view" scope="page"/>
         		</p>
          	</logic:notEqual>
          	</div>
          </li>
         </ul>
          	<!-- 照片墙统一风格（复杂查询） xiaoyun 2014-6-14 end -->
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
		          <p align="right"><hrms:paginationdblink name="complexInterfaceForm" property="pagination" nameId="complexInterfaceForm" scope="page">
				</hrms:paginationdblink>
		       </td>
		</tr>
</table>
<table  width="70%" align="center">
          <tr>
            <td align="center">       
         	    <hrms:submit styleClass="mybutton" property="br_return">
            		显示信息
	 	        </hrms:submit>
            </td>
          </tr>          
</table>
</html:form>
