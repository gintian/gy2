<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="java.util.HashMap"%>
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
	String path=request.getParameter("path");
  	path=path==null?"":path;
  	if("bi".equals(path)){
  		request.setAttribute("returnvalue","100001"); 
  	}else{
		request.setAttribute("returnvalue","191"); 
	}
  	
  	QueryInterfaceForm queryInterfaceForm=(QueryInterfaceForm)session.getAttribute("queryInterfaceForm");
	HashMap partMap=(HashMap)queryInterfaceForm.getPart_map();
	
%>
<script src="/phone-app/jquery/jquery-3.5.1.min.js" type="text/javascript"></script>
<script type="text/javascript" src="/js/bigphoto.js"></script>
<script language="javascript">
function winhref(a0100,nbase,target,returnvalue)
{
   if(a0100=="")
      return false;
    <%if("bi".equals(path)){%>
    	queryInterfaceForm.action="/workbench/browse/showselfinfo.do?b_search=link&userbase="+nbase+"&a0100="+a0100+"&flag=notself&returnvalue="+returnvalue";
    <%} else {%>
    	queryInterfaceForm.action="/workbench/browse/showselfinfo.do?b_search=link&userbase="+nbase+"&a0100="+a0100+"&flag=notself&returnvalue="+returnvalue;
    <%}%>
    queryInterfaceForm.target=target;
    queryInterfaceForm.submit();
   
      
}
//返回时，action路径设置成原来的路径 wangb 20170602   修改返回链接 chagnxy 20170922
function back(){
	queryInterfaceForm.action="/workbench/query/query_interface1_photo.do?br_return=link";
	queryInterfaceForm.target="_self";
	queryInterfaceForm.submit();
}
//$("#photo_tab").on("onload", checkScreenWidth());
$(checkScreenWidth);
/* 
 * 根据屏幕宽度动态修改照片墙的样式  
 */
function checkScreenWidth(){
	if(window.screen.width < 1300) {
		$("#photo_tab").css({'margin-left':'-4px'});
	} else {
		$("#photo_tab").css({'margin-left':'15px'});
	}
}


</script>
<hrms:themes></hrms:themes>
<html:form action="/workbench/query/query_interface1_photo">
<table id="photo_tab" width="70%" border="0" cellspacing="1"  align="center" cellpadding="1">
          <hrms:paginationdb indexes="index" id="element" name="queryInterfaceForm" sql_str="queryInterfaceForm.strsql" table="" where_str="queryInterfaceForm.cond_str" columns="queryInterfaceForm.columns" order_by="queryInterfaceForm.order_by" page_id="pagination" pagerows="20" distinct="" keys="">
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
             	String nbase=(String)abean.get("db"); 
             	//人员库不知道为什么要截掉一位导致查询照片的时候报表不存在
             //	nbase=nbase.substring(1);
                request.setAttribute("name",a0100); 
                request.setAttribute("nbase",nbase); 
                      	                           
          %>  
          <hrms:parttime a0100="${name}" nbase="${nbase}" part_map="<%=partMap%>" name="element" scope="page" code="" kind="" uplevel=""  b0110_desc="b0110_desc" e0122_desc="e0122_desc" part_desc="part_desc" partInfo="partInfo" deptCode="deptCode" unitCode="unitCode"/>
          <td align="center" NOWRAP>
            <logic:equal name="queryInterfaceForm" property="photolength" value="">
          		<ul class="photos">
          		<li>
          			<hrms:ole name="element"  photoWall="true" dbpre="${nbase}" href="###" target="il_body" a0100="a0100" div="" scope="page" height="120" width="85" onclick="winhref('${name}','${nbase}','il_body','${returnvalue}')" />
          			<div class="detail">
          				<p><a href="###" onclick="winhref('${name}','${nbase}','il_body','${returnvalue}')"><strong><bean:write name="element" property="a0101" filter="true"/></strong></a></p>
          				<p class="linehg">
          				<hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>
          				<!-- 标识：3081 照片墙增加兼职信息说明 xiaoyun 2014-7-18 start -->
          				<hrms:photoviewInfo name="element" a0100="" nbase="" itemid="" isNotSetQuota="true" jobCode="${codeitem.codeitem}" partInfo="${partInfo}" deptCode="${deptCode}" unitCode="${unitCode}" isInfoView="true"  scope="page"/>
          				<!-- 标识：3081 照片墙增加兼职信息说明 xiaoyun 2014-7-18 end -->
                		</p>
          			</div>
          		</li>
          		</ul>
          	</logic:equal>
          	<logic:notEqual name="queryInterfaceForm" property="photolength" value="">
          		<bean:define id="isHref" value="true"></bean:define>
          		<ul class="photos">
          			<li>
          			<hrms:ole name="element" photoWall="true" dbpre="${nbase}" href="###" target="il_body" a0100="a0100" div="" scope="page" height="120" width="85" onclick="winhref('${name}','${nbase}','il_body','${returnvalue}')" />
          			<div class="detail"> 	
          				<p>
                			<hrms:photoviewInfo name="element" a0100="a0100" params="${name},${nbase},il_body,${returnvalue}"  nbase="${nbase}" itemid="queryInterfaceForm.photo_other_view" scope="page"/>
                		</p>
	          		</div>
		          </li>
	          	</ul>
          	</logic:notEqual>
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
            <td align="left">
       			<!-- 添加点击事件 wangb 20170602 28065 onclick="back()" 取消click 添加后无法返回 changxy 20170920 -->
         	    <hrms:submit styleClass="mybutton" property="br_return" onclick="back()"  >
            		<bean:message key="button.return"/>
	 	    </hrms:submit>
            </td>
          </tr>          
</table>
<input type="hidden" name="path" value="<%=path %>" />
</html:form>
