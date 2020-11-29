<%@page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="java.util.*"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<% int i=0;%>
<script type="text/javascript">
function to_my_apply()
{
	document.getElementById("returnflag").value = "false";
	jingpinForm.action="/hire/jp_contest/apply/my_apply_pos.do?b_query=link";
	jingpinForm.submit(); 	
}
function checkmax(z0700)
{
	var maxpos = $F('maxpos');
	var choicepos = $F('choicepos');
	if(choicepos>=maxpos){
		alert(JP_INFO01)
		return;
	}
	else{
	jingpinForm.action="/hire/jp_contest/apply/apply_jp_pos.do?b_query=link&z0700="+z0700;
	jingpinForm.submit();
	}
}
</script>
<hrms:themes></hrms:themes>
<html:form action="/hire/jp_contest/apply/apply_jp_pos"> 
<html:hidden name="jingpinForm" property="returnflag" />
<br>
<br>
		<table  border="0" cellspacing="0" align="center" cellpadding="0"  width="70%" class="ListTable">
			<tr>
				<logic:iterate id="element1" name="jingpinForm" property="fieldlist">
					<td class="TableRow" align="center" nowrap>
						<bean:write name="element1" property="itemdesc"/>
					</td>
				</logic:iterate>
				<td  class="TableRow" align="center" nowrap>
					<bean:message key="column.operation"/>
				</td>
			</tr>
			
			 <hrms:paginationdb id="element" name="jingpinForm" sql_str="jingpinForm.selectsql" table="" where_str="jingpinForm.wheresql" columns="jingpinForm.column"  pagerows="15" page_id="pagination" indexes="indexes">	
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
			          
				    <logic:iterate id="element2" name="jingpinForm" property="fieldlist">
				      <%
			          LazyDynaBean bean=(LazyDynaBean)pageContext.getAttribute("element")
			          String a0100_encrypt=(String)bean.get("z0701");
			          request.setAttribute("a0100_encrypt", a0100_encrypt);
			          %> 
						<td class="RecordRow" nowrap>
							<logic:equal value="z0701"  name="element2" property="itemid">
								<hrms:codetoname codeid="@K" name="element" codevalue="z0701" codeitem="codeitem" scope="page"/>
								<a href="/workbench/browse/showposinfo.do?b_browse=link&a0100=${a0100_encrypt}&userbase=Usr&npage=1" target="_blank"> <bean:write name="codeitem" property="codename" />&nbsp; </a >         
		        				<logic:equal value=""  name="codeitem" property="codename">
		        					<bean:write name="element" property="z0701"/>
		        				</logic:equal>
							</logic:equal>
							<logic:equal value="z0706"  name="element2" property="itemid">
								<bean:write name="element" property="z0706"/>
							</logic:equal>
							<logic:equal value="z0711"  name="element2" property="itemid">							
								<hrms:codetoname codeid="UN" name="element" codevalue="z0711" codeitem="codeitem" scope="page"/>         
			        			<bean:write name="codeitem" property="codename" />&nbsp;
			        			<logic:equal value=""  name="codeitem" property="codename">
									<hrms:codetoname codeid="UM" name="element" codevalue="z0711" codeitem="codeitem" scope="page"/>         
			        				<bean:write name="codeitem" property="codename" />&nbsp;	
								</logic:equal>	
							</logic:equal>
							<logic:equal value="z0713"  name="element2" property="itemid">
								<hrms:codetoname codeid="23" name="element" codevalue="z0713" codeitem="codeitem" scope="page"/>         
				        		<bean:write name="codeitem" property="codename" />&nbsp;
							</logic:equal>
							<logic:equal value="z0703"  name="element2" property="itemid">
								<bean:write name="element" property="z0703"/>
							</logic:equal>
							<logic:equal value="z0705"  name="element2" property="itemid">
								<bean:write name="element" property="z0705"/>
							</logic:equal>
							<logic:equal value="z0709"  name="element2" property="itemid">
								<bean:write name="element" property="z0709"/>
							</logic:equal>
							<logic:equal value="z0714"  name="element2" property="itemid">							
								<hrms:codetoname codeid="UN" name="element" codevalue="z0714" codeitem="codeitem" scope="page"/>         
			        			<bean:write name="codeitem" property="codename" />&nbsp;
			        			<logic:equal value=""  name="codeitem" property="codename">
									<hrms:codetoname codeid="UM" name="element" codevalue="z0714" codeitem="codeitem" scope="page"/>         
			        				<bean:write name="codeitem" property="codename" />&nbsp;	
								</logic:equal>	
							</logic:equal>
						</td>
					</logic:iterate>
					<logic:empty name="element" property="state">
						<td class="RecordRow" nowrap>				    
				    		<a href="javascript:checkmax('<bean:write name="element" property="z0700"/>')" ><bean:message key="button.app" /></a>
				    	</td>
					</logic:empty>
				    <logic:notEmpty name="element" property="state">
				    	<td class="RecordRow" nowrap>				    
				    		<a href="/hire/jp_contest/apply/apply_jp_pos.do?b_query=link&z0700=<bean:write name="element" property="z0700"/>" ><bean:message key="general.mediainfo.view" /></a>
				    	</td>
				    </logic:notEmpty>
			    </tr>
			    
	    	</hrms:paginationdb>

	    	
	    	
		</table>
<table  width="70%" align="center" class="RecordRowP">
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
            <p align="right"><hrms:paginationdblink name="jingpinForm" property="pagination" nameId="jingpinForm" scope="page">
    </hrms:paginationdblink>
   </td>
  </tr>
</table>
<table width="70%" align="center">
<logic:equal value="yes"  name="jingpinForm" property="applyflag">
				<tr align="left">
					<td align="left" valign="top">
						<html:button styleClass="mybutton" property="apply" onclick="to_my_apply();">
					  		<bean:message key="tab.label.myapply"/>
						</html:button>	   
					</td>
				</tr>
			</logic:equal>
</table>
<input type="hidden" name="maxpos" value="${jingpinForm.maxpos}" />
<input type="hidden" name="choicepos" value="${jingpinForm.choicepos}" />
</html:form>

  	 


    