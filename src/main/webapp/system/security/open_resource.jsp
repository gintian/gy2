<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.sys.ResourceForm"%>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page import="java.util.*"%>
<%
	int i=0;
%>
<script language="javascript">
function refuc(query){
var obj = event.srcElement ? event.srcElement : event.target;
  if(query=='12'){
    with(obj)
     if(value=="请输入编号或薪资类别名称") value="";
     //if(style.color=='gray') style.color='black';
    }
   if(query=='18'){
    with(obj)
     if(value=="请输入编号或保险类别名称") value=""
  }
}
function reblu(query){
var obj = event.srcElement ? event.srcElement : event.target;
  if(query=='12'){
    with(obj)
     if(value=="") value="请输入编号或薪资类别名称"
  }
   if(query=='18'){
    with(obj)
     if(value=="") value="请输入编号或保险类别名称"
  }
}
function query()
{
   var buttonflag="1";
   resourceForm.action="/system/security/open_resource.do?b_query=link&buttonflag="+buttonflag;
   resourceForm.submit();
}
//19/3/27 xus 屏蔽输入框Enter提交表单
document.body.onkeydown = function(e) {
	if (13 == e.keyCode && (document.activeElement.id == "searchInput" || document.activeElement.name == 'pageSelect')) {
		e.preventDefault ? e.preventDefault() : e.returnValue = false;
	};
}
</script>
<html:form action="/system/security/open_resource">
<!-- 添加查询功能 hej -->
<logic:equal name="resourceForm" property="res_flag" value="12">
 <table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0" >
 <tr>
 <td align="left">
 <logic:equal name="resourceForm" property="searchparamgz" value="">
   <html:text styleId="searchInput" name="resourceForm" property="searchparamgz" value="请输入编号或薪资类别名称" onfocus="refuc('12');" onblur="reblu('12');" size="31" maxlength="31" styleClass="textbox" style="color:gray;" />
 </logic:equal>
 <logic:notEqual name="resourceForm" property="searchparamgz" value="">
   <html:text styleId="searchInput" name="resourceForm" property="searchparamgz"  onfocus="refuc('12');" onblur="reblu('12');" size="31" maxlength="31" styleClass="textbox" style="color:gray;" />
 </logic:notEqual>
  <Input type='button' value="<bean:message key="infor.menu.query"/>" onclick="query();" class='mybutton' />  &nbsp;&nbsp; 
 </td>
 </tr>
 </table>
</logic:equal>
<logic:equal name="resourceForm" property="res_flag" value="18">
 <table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0" >
 <tr>
 <td align="left">
  <logic:equal name="resourceForm" property="searchparambx" value="">
     <html:text styleId="searchInput" name="resourceForm" property="searchparambx"  value="请输入编号或保险类别名称" onfocus="refuc('18');" onblur="reblu('18');" size="31" maxlength="31" styleClass="textbox" style="color:gray;"/>
  </logic:equal> 
  <logic:notEqual name="resourceForm" property="searchparambx" value="">
   <html:text styleId="searchInput" name="resourceForm" property="searchparambx"   onfocus="refuc('18');" onblur="reblu('18');" size="31" maxlength="31" styleClass="textbox" style="color:gray;"/>
  </logic:notEqual>
  <Input type='button' value="<bean:message key="infor.menu.query"/>" onclick="query();" class='mybutton' />  &nbsp;&nbsp; 
 </td>
 </tr>
 </table>
</logic:equal>
<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" style="margin-top:5px;">
   	  <thead>
        <tr>
            <td align="center" class="TableRow" nowrap width="20%">
  		      <bean:message key="column.name"/>&nbsp;
  		      <!-- 通过操作标识opt判断保存操作是否成功，页面给出成功提示   jingq   add   2014.5.7 -->
  		      <logic:equal name="resourceForm" property="opt" value="1">
  		      	<script language="javascript">
  		      		alert('<bean:message key="label.save.success"/>');
  		      	</script>
  		      </logic:equal>
	        </td>
            <td align="center" class="TableRow" nowrap width="10%">
				<input type="checkbox" name="selbox" onclick="batch_select(this,'reslistform.pagination.curr_page_list');" title='<bean:message key="label.query.selectall"/>'>
	        </td>                   		        	        	        
        </tr>
   	  </thead>
          <hrms:extenditerate id="element" name="resourceForm" property="reslistform.list" indexes="indexes"  pagination="reslistform.pagination" pageCount="15" scope="session">
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
          %>  
            <td align="left" class="RecordRow" nowrap>
                <bean:write name="element" property="name" filter="true"/>&nbsp;
	        </td>          
            <td align="center" class="RecordRow" nowrap>
			    <html:checkbox name="resourceForm" property='<%="reslistform.pagination.curr_page_list["+i+"].c0"%>' value="1"></html:checkbox>                              
 	        </td>            
	        <%i++;%>   
          </tr>
        </hrms:extenditerate>
</table>
<table  width="90%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="resourceForm" property="reslistform.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="resourceForm" property="reslistform.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="resourceForm" property="reslistform.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	        <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="resourceForm" property="reslistform.pagination"
				nameId="reslistform" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>

<table  width="70%" align="center">
          <tr>
            <td align="center" height="35px;">
         	<hrms:submit styleClass="mybutton" property="b_save">
            		<bean:message key="button.save"/>
	 		</hrms:submit>
            </td>
          </tr>          
</table>
</html:form>