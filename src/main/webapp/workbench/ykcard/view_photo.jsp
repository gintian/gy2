<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.ykcard.CardTagParamForm"%>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page import="java.util.HashMap" %> 
<%
	int i=0;
	CardTagParamForm cardTagParamForm=(CardTagParamForm)session.getAttribute("cardTagParamForm");
	HashMap partMap=(HashMap)cardTagParamForm.getPart_map();
%>
<script language="javascript">
function winhref(a0100)
{
   if(a0100=="")
      return false;
   var o_obj=document.getElementById('a0100');   
   if(o_obj)
     o_obj.value=a0100;
   
   var pre = '${cardTagParamForm.userbase}';
   cardTagParamForm.action="/module/gz/mysalary/MySalaryMain.html?b_query=link&pre="+encodeURI(pre)+"&a0100="+encodeURI(a0100);
   cardTagParamForm.target="mil_body";
   cardTagParamForm.submit();
}
  document.oncontextmenu = function() {return false;}
</script>
<hrms:themes></hrms:themes>
<html:form action="/workbench/ykcard/view_photo">
<input type="hidden" name="a0100" id="a0100">
<!-- //liuy 2015-3-18 7976：自助服务/员工信息/员工薪酬，显示照片，怎么不是照片墙呢？另外每行的照片个数也不对。 begin -->
<table id="photo_tab" width="80%" border="0" cellspacing="0" align="center" cellpadding="0" style="margin-top: -2px;" >	
	<hrms:paginationdb id="element" name="cardTagParamForm" sql_str="cardTagParamForm.strsql" table="" where_str="cardTagParamForm.cond_str" columns="A0100,B0110,E0122,E01A1,A0101,UserName," order_by="cardTagParamForm.order_by" pagerows="20" page_id="pagination" keys="">
	<%if(i%4==0){%>
		<tr valign="middle" align="center">
	<%}%>             
	<%
		LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
		String a0100=(String)abean.get("a0100"); 
		request.setAttribute("name",a0100); 
	%>
	<hrms:parttime a0100="${name}" nbase="${cardTagParamForm.userbase}" part_map="<%=partMap%>" name="element" scope="page" code="${cardTagParamForm.code}" kind="${cardTagParamForm.kind}" uplevel="${cardTagParamForm.uplevel}"  b0110_desc="b0110_desc" e0122_desc="e0122_desc" part_desc="part_desc" partInfo="partInfo" deptCode="deptCode" unitCode="unitCode"/>
	<td align="center" nowrap>
		<ul class="photos">
			<li>
				<hrms:ole name="element"  photoWall="true" dbpre="cardTagParamForm.userbase" href="###" target="nil_body" a0100="a0100" div="" scope="page" height="120" width="85" onclick="winhref('${name}','nil_body')" />
				<div class="detail">
					<p><a href="###" onclick="winhref('${name}','nil_body')"><strong><bean:write name="element" property="a0101" filter="true"/></strong></a></p>
					<p class="linehg">
						<hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>
						<hrms:photoviewInfo name="element" a0100="" nbase="" itemid="" isNotSetQuota="true" jobCode="${codeitem.codeitem}" partInfo="${partInfo}" deptCode="${deptCode}" unitCode="${unitCode}" isInfoView="true"  scope="page"/>
		    		</p>
				</div>
			</li>
		</ul>
	</td> 
	<%if((i+1)%4==0){%>
		</tr>
	<%}i++;%>         
	</hrms:paginationdb>
</table>
<!-- liuy 2015-3-18 end -->
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
		          <p align="right"><hrms:paginationdblink name="cardTagParamForm" property="pagination" nameId="cardTagParamForm" scope="page">
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
