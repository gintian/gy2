<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hrms.frame.utility.CodeItem"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%
	 int i=0;
%>
<%
  UserView userView=(UserView)session.getAttribute(WebConstant.userView);
  String url="";
  if(userView != null)
  {
     url=userView.getBosflag();
  
  }
  String home=(String)session.getAttribute("home");
  home = home==null?"":home;
  String crosstabtype=(String)session.getAttribute("crosstabtype");
  crosstabtype = crosstabtype==null?"":crosstabtype;
%>
<script src="/phone-app/jquery/jquery-3.5.1.min.js" type="text/javascript"></script>
<script type="text/javascript" src="/js/bigphoto.js"></script>
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
<script language="javascript">
function winhref(a0100,returnvalue,target)
{
   if(a0100=="")
      return false;
   var o_obj=document.getElementById('a0100');   
   if(o_obj)
     o_obj.value=a0100;
    statForm.action="/workbench/browse/showselfinfo.do?b_search=link&userbase=${statForm.userbase}&flag=notself&returnvalue="+returnvalue+"&home=<%=home%>";
    statForm.target=target;
    statForm.submit();
}
function winhref1(dbpre,a0100,returnvalue,target)
{
   if(a0100=="")
      return false;
   var o_obj=document.getElementById('a0100');   
   if(o_obj)
     o_obj.value=a0100;
    statForm.action="/workbench/browse/showselfinfo.do?b_search=link&userbase="+dbpre+"&flag=notself&returnvalue="+returnvalue+"&home=<%=home%>";
    statForm.target=target;
    statForm.submit();
}
function openSelfInfo(dbpre,a0100,returnvalue)
{
	url="/workbench/browse/showselfinfo.do?b_search=link`userbase="+dbpre+"`flag=notself`returnvalue="+returnvalue+"`a0100="+a0100;
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(url+"`width="+(window.screen.width-10)+"`height="+(window.screen.height-90));
	if(getBrowseVersion()){
		var return_vo= window.showModalDialog(iframe_url,"", 
	          "dialogWidth="+window.screen.width+"px;dialogHeight="+window.screen.height+"px;resizable=yes;scroll=no;status=no;");
	}else{//兼容非IE浏览器弹窗  wangb 20180806 bug 39420
		window.open(iframe_url,"","width="+window.screen.width+",height="+window.screen.height+",resizable=yes,scrollable=no");
	}
} 
document.oncontextmenu = function() {return false;}

$(checkScreenWidth);
/* 
 * 根据屏幕宽度动态修改照片墙的样式  
 */
function checkScreenWidth(){
	if(window.screen.width < 1024) {
		$("#photo_tab").css({'margin-left':'-10px'});
	}
} 
</script>
<hrms:themes/>

<style>
<!--
.photoTr{
	padding-left: 3px;
}
-->
</style>
<body>
<script type="text/javascript" src="/js/wz_tooltip.js"></script>
<html:form action="/general/static/commonstatic/view_photo">
<input type="hidden" name="a0100" id="a0100">
<table id="photo_tab" width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" style="margin-top: -4px;">
          <hrms:paginationdb id="element" name="statForm" sql_str="statForm.strsql" table="" where_str="statForm.cond_str" columns="statForm.columns"  order_by="statForm.order_by" distinct="" pagerows="20" page_id="pagination">
          <%
          if(i%4==0)
          {
          %>
          <tr class="photoTr">
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
          <td align="center" nowrap>
           <logic:equal name="statForm" property="home" value="0">  
             <logic:equal name="statForm" property="flag" value="jgfx"> 
               <ul class="photos">
	          		<li>
	          			  <% if("".equals(crosstabtype)){ %>
	          			<hrms:ole name="element" photoWall="true" dbpre="${db }" a0100="a0100" scope="page" height="120" width="85" href="###" onclick="winhref1('${db}','${name}','55','il_body');"/>
				       	  <%}else{ %>
	          			<hrms:ole name="element" photoWall="true" dbpre="${db }" a0100="a0100" scope="page" height="120" width="85" href="###" onclick="openSelfInfo('${db}','${name}','55');"/>
					      <%} %>
	          			<div class="detail">
	          				<p>
	          				  <% if("".equals(crosstabtype)){ %>
	          					<a href="###" onclick="winhref('${name}','jgfx_p','nil_body');"><strong><bean:write name="element" property="a0101" filter="true"/></strong></a>
					       	  <%}else{ %>
	          					<a href="###" onclick="openSelfInfo('${db}','${name}','jgfx_p');"><strong><bean:write name="element" property="a0101" filter="true"/></strong></a>
						      <%} %>
	          				</p>
	          				<p class="linehg">
	          				<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="dept" scope="page"/>  	      
	          				<hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="job" scope="page"/>
	          				<hrms:photoviewInfo name="element" a0100="a0100" nbase="${nbase}" isNotSetQuota="true" departName="${dept.codename}" jobName="${job.codename}" itemid="complexInterfaceForm.photo_other_view" scope="page"/>
	                		</p>
	          			</div>
	          		</li>
	          	</ul>  
             </logic:equal>
             <logic:notEqual name="statForm" property="flag" value="jgfx">
               <!-- 统计分析 照片墙 测试 xiaoyun 2014-6-14 start -->
	          	<ul class="photos">
	          		<li>
	          			  <% if("".equals(crosstabtype)){ %>
	          			<hrms:ole name="element" photoWall="true" dbpre="${db }" a0100="a0100" scope="page" height="120" width="85" href="###" onclick="winhref1('${db}','${name}','55','il_body');"/>
				       	  <%}else{ %>
	          			<hrms:ole name="element" photoWall="true" dbpre="${db }" a0100="a0100" scope="page" height="120" width="85" href="###" onclick="openSelfInfo('${db}','${name}','55');"/>
					      <%} %>
	          			<div class="detail">
	          				<p>
	          				  <% if("".equals(crosstabtype)){ %>
	          					<a href="###" onclick="winhref1('${db}','${name}','55','il_body');"><strong><bean:write name="element" property="a0101" filter="true"/></strong></a>
					       	  <%}else{ %>
	          					<a href="###" onclick="openSelfInfo('${db}','${name}','55');"><strong><bean:write name="element" property="a0101" filter="true"/></strong></a>
						      <%} %>
	          				</p>
	          				<p class="linehg">
	          				<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="dept" scope="page"/> 
	          				<hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="job" scope="page"/>
	          				<hrms:photoviewInfo name="element" a0100="a0100" nbase="${nbase}" isNotSetQuota="true" departName="${dept.codename}" jobName="${job.codename}" itemid="complexInterfaceForm.photo_other_view" scope="page"/>
	                		</p>
	          			</div>
	          		</li>
	          	</ul>
              <!-- 统计分析 照片墙 测试 xiaoyun 2014-6-14 end -->  
             </logic:notEqual>          	
           </logic:equal>
           <logic:equal name="statForm" property="home" value="1">         
            <% if(url!=null&&(url.equalsIgnoreCase("hl4")||url.equalsIgnoreCase("hl"))) {%>            
             <ul class="photos">
	          		<li>
	          			<hrms:ole name="element" photoWall="true" dbpre="${db }" a0100="a0100" scope="page" height="120" width="85" href="###"   onclick="winhref1('${db}','${name}','881','il_body');"/>
	          			<div class="detail">
	          				<p>
	          					<a href="###" onclick="winhref1('${db}','${name}','881','il_body');"><strong><bean:write name="element" property="a0101" filter="true"/></strong></a>
	          				</p>
	          				<p class="linehg">
	          				<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="dept" scope="page"/>  
	          				<hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="job" scope="page"/>
	          				<hrms:photoviewInfo name="element" a0100="a0100" nbase="${nbase}" isNotSetQuota="true" departName="${dept.codename}" jobName="${job.codename}" itemid="complexInterfaceForm.photo_other_view" scope="page"/>
	                		</p>
	          			</div>
	          		</li>
	          	</ul>
            <%}else{ %>
                 <ul class="photos">
	          		<li>
	          			<!-- 1758 xiaoyun 2014-7-3 start -->
	          			<%
            			if(url!=null&&url.equals("hcm")){ %> 
	          			<hrms:ole name="element" photoWall="true" dbpre="statForm.userbase" a0100="a0100" scope="page" height="120" width="85" href="###"   onclick="winhref('${name}','88','il_body');"/>
	          			<%}else{%>
	          			<hrms:ole name="element" photoWall="true" dbpre="statForm.userbase" a0100="a0100" scope="page" height="120" width="85" href="###"   onclick="winhref('${name}','88','i_body');"/>
	          			<%
	          			} %>
	          			<!-- 1758 xiaoyun 2014-7-3 start -->
	          			<div class="detail">
	          				<p>
	          					<a href="###" onclick="winhref('${name}','88','i_body');"><strong><bean:write name="element" property="a0101" filter="true"/></strong></a>
	          				</p>
	          				<p class="linehg">
		          				<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="dept" scope="page"/>  
		          				<hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="job" scope="page"/>
		          				<hrms:photoviewInfo name="element" a0100="a0100" nbase="${nbase}" isNotSetQuota="true" departName="${dept.codename}" jobName="${job.codename}" itemid="complexInterfaceForm.photo_other_view" scope="page"/>
	                		</p>
	          			</div>
	          		</li>
	          	</ul>
             <%} %>
           </logic:equal>
            <logic:equal name="statForm" property="home" value="2">         
             <ul class="photos">
	          		<li>
	          			<hrms:ole name="element" photoWall="true" dbpre="statForm.userbase" a0100="a0100" scope="page" height="120" width="85" href="javascript:winhref('${name}','fx66','i_body');" />
	          			<div class="detail">
	          				<p>
	          					<a href="###" onclick="winhref('${name}','821','i_body');"><strong><bean:write name="element" property="a0101" filter="true"/></strong></a>
	          				</p>
	          				<p class="linehg">
		          				<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="dept" scope="page"/>  	
		          				<hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="job" scope="page"/>
		          				<hrms:photoviewInfo name="element" a0100="a0100" nbase="${nbase}" isNotSetQuota="true" departName="${dept.codename}" jobName="${job.codename}" itemid="complexInterfaceForm.photo_other_view" scope="page"/>
	                		</p>
	          			</div>
	          		</li>
	          	</ul>
           </logic:equal>
           <logic:equal name="statForm" property="home" value="6">         
             <%if(url!=null&&url.equalsIgnoreCase("bi")){ %>
             	 <ul class="photos">
	          		<li>
	          			<hrms:ole name="element" photoWall="true" dbpre="statForm.userbase" a0100="a0100" scope="page" height="120" width="85" href="javascript:winhref('${name}','fx66','i_body');" />
	          			<div class="detail">
	          				<p>
	          					<a href="###" onclick="winhref('${name}','fx66','i_body');"><strong><bean:write name="element" property="a0101" filter="true"/></strong></a>
	          				</p>
	          				<p class="linehg">
		          				<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="dept" scope="page"/>  
		          				<hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="job" scope="page"/>
		          				<hrms:photoviewInfo name="element" a0100="a0100" nbase="${nbase}" isNotSetQuota="true" departName="${dept.codename}" jobName="${job.codename}" itemid="complexInterfaceForm.photo_other_view" scope="page"/>
	                		</p>
	          			</div>
	          		</li>
	          	</ul>
             <%}else{ %>
              <ul class="photos">
	          		<li>
	          			<hrms:ole name="element" photoWall="true" dbpre="statForm.userbase" a0100="a0100" scope="page" height="120" width="85" href="javascript:winhref('${name}','55','il_body');" />
	          			<div class="detail">
	          				<p>
	          					<a href="###" onclick="winhref('${name}','55','il_body');"><strong><bean:write name="element" property="a0101" filter="true"/></strong></a>
	          				</p>
	          				<p class="linehg">
		          				<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="dept" scope="page"/>  
		          				<hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="job" scope="page"/>
		          				<hrms:photoviewInfo name="element" a0100="a0100" nbase="${nbase}" isNotSetQuota="true" departName="${dept.codename}" jobName="${job.codename}" itemid="complexInterfaceForm.photo_other_view" scope="page"/>
	                		</p>
	          			</div>
	          		</li>
	          	</ul>
             <%} %>          	
           </logic:equal>
           <logic:equal name="statForm" property="home" value="5">         
             <ul class="photos">
	          		<li>
	          			<hrms:ole name="element" photoWall="true" dbpre="${db}" a0100="a0100" scope="page" height="120" width="85" href="javascript:winhref1('${db}','${name}','55','il_body');" />
	          			<div class="detail">
	          				<p>
	          					<a href="###" onclick="winhref1('${db}','${name}','55','il_body');"><strong><bean:write name="element" property="a0101" filter="true"/></strong></a>
	          				</p>
	          				<p class="linehg">
	  	        				<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="dept" scope="page"/>  
	    	      				<hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="job" scope="page"/>
	        	  				<hrms:photoviewInfo name="element" a0100="a0100" nbase="${nbase}" isNotSetQuota="true" departName="${dept.codename}" jobName="${job.codename}" itemid="complexInterfaceForm.photo_other_view" scope="page"/>
	                		</p>
	          			</div>
	          		</li>
	          	</ul>
           </logic:equal>        
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
		          <p align="right"><hrms:paginationdblink name="statForm" property="pagination" nameId="statForm" scope="page">
				</hrms:paginationdblink>
		       </td>
		</tr>
</table>
<table  width="70%" align="center">
          <tr>
            <td align="center"><%-- bug 39232 返回按钮居中显示  wangb 20180730--%>
            <input type="hidden" name="home" value="<%=home %>">       
         	<hrms:submit styleClass="mybutton" property="br_return">
            		<bean:message key="button.return"/>
	 	    </hrms:submit>
            </td>
          </tr>          
</table>
</html:form>
</body>
