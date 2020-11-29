<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hrms.frame.utility.CodeItem"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.query.QueryInterfaceForm"%>
<%
	int i=0;
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	QueryInterfaceForm queryInterfaceForm=(QueryInterfaceForm)session.getAttribute("queryInterfaceForm");
	String dbpre=queryInterfaceForm.getDbpre();
	String url="";
	if(userView != null)
	{
	    url=userView.getBosflag();
	}
	String returnvalue="66";
	if(url!=null&&url.equals("hl4"))
	{
	   returnvalue="64";
	}else
	{
	   if(queryInterfaceForm.getHome()==null||!queryInterfaceForm.getHome().equals("4"))
	     returnvalue="64";
	}
	request.setAttribute("returnvalue",returnvalue); 
	
%>
<script src="/phone-app/jquery/jquery-3.5.1.min.js" type="text/javascript"></script>
<script type="text/javascript" src="/js/bigphoto.js"></script>
<script type="text/javascript" src="/js/wz_tooltip.js"></script> 

<script language="javascript">
/*非IE浏览器调用不到此方法 放到最下面可以调用到  bug 34384 wangb 20180201
function winhref(nbase,a0100,returnvalue)
{
     if(a0100=="")
      return false;
    queryInterfaceForm.action="/workbench/browse/showselfinfo.do?b_search=link&userbase="+nbase+"&a0100="+a0100+"&flag=notself&returnvalue="+returnvalue+"";
    queryInterfaceForm.submit();
}
*/
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
<style>
<!--
.detail h1{
	font-size:10px;
	height:16px;
	line-height:16px;
	text-align: left;
	margin-left: 68px;
	font-family: fantasy;
}
.title{
	padding-left: 10px;
}
-->
</style>
<hrms:themes></hrms:themes>
<html:form action="/workbench/query/view_photo_result">
<table id="photo_tab" width="70%" border="0" cellspacing="0"  align="center" cellpadding="0" style="margin-top: -3px;">
          <hrms:paginationdb id="element" name="queryInterfaceForm" sql_str="queryInterfaceForm.strsql" table="" where_str="queryInterfaceForm.strwhere" columns="queryInterfaceForm.columns" order_by="queryInterfaceForm.order" page_id="pagination" pagerows="20" distinct="${queryInterfaceForm.distinct}" keys="${queryInterfaceForm.keys}">
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
          <td align="center" nowrap>
        
          <!-- 照片墙统一风格（快速查询）xiaoyun 2014-6-14 start -->
	     <ul class="photos">
	       <li>
	      	<logic:equal name="queryInterfaceForm" property="home" value="4">                           
          		<hrms:ole name="element" photoWall="true" dbpre="${nbase}" href="javascript:;"  onclick="javascript:winhref('${nbase}','${name}','${returnvalue}');" a0100="a0100" scope="page" height="120" width="85"/>
          		<div class="detail">
          		<logic:equal name="queryInterfaceForm" property="photolength" value="">
          			<p><a href="javascript:;" onclick="javascript:winhref('${nbase}','${name}','${returnvalue}');" ><strong><bean:write name="element" property="a0101" filter="true"/></strong></a></p>
	          		<p class="linehg">
	          	    	<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="dept" scope="page"/>  	      
	                	<hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>  	
	          	    	<hrms:photoviewInfo name="element" a0100="" nbase="" itemid="" isNotSetQuota="true" departName="${dept.codename}" jobName="${codeitem.codename}" scope="page"/>
	          	    </p> 
          		</logic:equal>
          		<logic:notEqual name="queryInterfaceForm" property="photolength" value="">
          			<p>
                   		<hrms:photoviewInfo name="element" a0100="a0100" params="${nbase},${name},${returnvalue }" nbase="${nbase}" itemid="queryInterfaceForm.photo_other_view" scope="page"/>
                   	</p>
          		</logic:notEqual>
          		</div>
            </logic:equal>
            <logic:notEqual name="queryInterfaceForm" property="home" value="4">
          		<hrms:ole name="element" photoWall="true" dbpre="${nbase}" href="javascript:;"  a0100="a0100" scope="page" height="120" width="85" onclick="javascript:winhref('${nbase}','${name}','33');" />
            	<div class="detail">
             	<logic:equal name="queryInterfaceForm" property="photolength" value="">
          			<p><a href="javascript:;" onclick="javascript:winhref('${nbase}','${name}','${returnvalue}');" ><strong><bean:write name="element" property="a0101" filter="true"/></strong></a></p>
	          		<p class="linehg">
	          	    	<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="dept" scope="page"/> 
	                	<hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="job" scope="page"/>
	                	<hrms:photoviewInfo name="element" a0100="" nbase="" itemid="" isNotSetQuota="true" departName="${dept.codename}" jobName="${job.codename}" scope="page"/>                	               	
	          	    </p> 
          		</logic:equal>
          		<logic:notEqual name="queryInterfaceForm" property="photolength" value="">
          	       	<p>
          				<a href="javascript:;" onclick="javascript:winhref('${nbase}','${name}','${returnvalue}');">
                   		<hrms:photoviewInfo name="element" a0100="a0100" params="${nbase},${name},${returnvalue }" nbase="${nbase}" itemid="queryInterfaceForm.photo_other_view" scope="page"/>
                   		</a>
                   	</p>
          		</logic:notEqual>
          	</div>
          	</logic:notEqual>
          	 </li>
	     </ul> 
          <!-- 照片墙统一风格（快速查询）xiaoyun 2014-6-14 end -->
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
		          <p align="right"><hrms:paginationdblink name="queryInterfaceForm" property="pagination" nameId="queryInterfaceForm" scope="page">
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
<script>
//挪到最下面 非IE浏览器可以调用   wangb 20180201 bug 34384
function winhref(nbase,a0100,returnvalue)
{
     if(a0100=="")
      return false;
    queryInterfaceForm.action="/workbench/browse/showselfinfo.do?b_search=link&userbase="+nbase+"&a0100="+a0100+"&flag=notself&returnvalue="+returnvalue+"";
    queryInterfaceForm.submit();
}
</script>
</html:form>
