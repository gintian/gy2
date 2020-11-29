<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean,com.hrms.frame.dao.RecordVo"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%@ page import="com.hjsj.hrms.actionform.sys.AccountForm" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.businessobject.sys.SysParamBo,com.hjsj.hrms.businessobject.sys.SysParamConstant"%>

<link rel="stylesheet" href="/css/css1.css" type="text/css">
<hrms:themes></hrms:themes>
<script language="javascript">
//下拉列表选择
function change(){
	accountForm.action="/system/security/role_repeat.do?b_repeat=link";
	accountForm.submit();
}
//修改
function edit(a0100){
	var dw=350,dh=180,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
	var target_url="/system/security/repeatLogin_info.do?b_query=link`repeatID="+a0100;
	//使用iframe_url防止点击按钮弹出新页面
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
	var return_vo=showModalDialog(iframe_url,null,'dialogLeft:'+dl+'px;dialogTop:'+dt+'px;dialogHeight:'+dh+'px;dialogWidth:'+dw+'px;center:yes;help:no;resizable:no;status:no;');
	if(return_vo=="true"){ 
		reflesh(); 
	}else		  
		return false;
}
//刷新表单
function reflesh(){	
	document.accountForm.action="/system/security/role_repeat.do?b_repeat=link";
    document.accountForm.submit();
}
</script>
<%int i=0;%>
<html:form action="/system/security/role_repeat">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" style="margin-top:5px;">
 <tr>
    <td align="left" nowrap>
        <bean:message key="label.user"/>
        <%--下拉列表--%>
        <hrms:optioncollection name="accountForm" property="repeatList" collection="list" />
        <html:select name="accountForm" property="repeatTable" size="1" onchange="change();">
             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
        </html:select>
    </td>         
 </tr>
</table>
<%	AccountForm accountForm = (AccountForm) session.getAttribute("accountForm");
	String repeatTable = accountForm.getRepeatTable();
	//通过repeatTable判断显示的表单
	if(repeatTable.equals("OperUser")){
%>
<div class="fixedDiv2" style="border-top:none;margin-top:5px;">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
			<td align="center" class="TableRow" nowrap style="border-left:none;">
           		<bean:message key="label.user.group"/>        	
	  	    </td> 
			<%-- 姓名--%>
            <td align="center" class="TableRow" nowrap style="border-right:none;">      		        
           		 <bean:message key="label.title.name"/>  	
	  	    </td>        		    	        	    	    	    		        	        	        
           </tr>
   	  </thead>
   	  <%--查询数据库分页标签--%>
   	  <hrms:paginationdb id="element" name="accountForm" sql_str="accountForm.repeatSql_str" table="" where_str="accountForm.repeatWhere_str" columns="${accountForm.repeatColumns}" order_by="${accountForm.repeatOrder_by}" pagerows="${accountForm.pagerows}"  page_id="pagination" indexes="indexes"  >
          <%LazyDynaBean item=(LazyDynaBean)pageContext.getAttribute("element");
            String desc=(String)item.get("e0122");
            if(i%2==0){
          %>
          <tr class="trShallow" onClick="javascript:tr_onclick(this,'')">
          <%}else{%>
          <tr class="trDeep" onClick="javascript:tr_onclick(this,'')">
          <%}i++;%>  
	    	<td align="left" class="RecordRow" nowrap style="border-left:none;">
             &nbsp;&nbsp;<bean:write name="element" property="position" filter="true"/>&nbsp;
	    	</td>    
         <td align="left" class="RecordRow" nowrap>
             &nbsp;&nbsp;<bean:write name="element" property="name" filter="true"/>&nbsp;
	    </td>
	   </tr>
    </hrms:paginationdb>
</table>
</div>
<% 	
}else{
%>
<div class="fixedDiv2" style="border-top:none;margin-top:5px;"> 
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
            <%-- 单位名称--%>
         	 <td align="center" class="TableRow" nowrap style="border-left:none;">
          		<hrms:fieldtoname name="accountForm" fieldname="b0110" fielditem="fielditem"/>
	      		<bean:write name="fielditem" property="dataValue" />&nbsp;
			</td>
			<%-- 部门--%>
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="accountForm" fieldname="e0122" fielditem="fielditem"/>
             <bean:write name="fielditem" property="dataValue" />&nbsp;
	 	   </td>
	    	<%-- 岗位名称--%>
	    	<td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="accountForm" fieldname="e01a1" fielditem="fielditem"/>
	    	 <bean:write name="fielditem" property="dataValue" />&nbsp;
			</td>
			<%-- 姓名--%>
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="accountForm" fieldname="A0101" fielditem="fielditem"/>
	         <bean:write name="fielditem" property="dataValue" />&nbsp;          	
	   	 	</td> 
<% 
	String repeatFlag = accountForm.getRepeatFlag();
	//通过repeatTable判断显示的表单
	if(repeatFlag.equals("true")){
	
%>
			<td align="center" class="TableRow" nowrap>
             	登录账号          	
	   	 	</td>
<%} %>
	  	  <%-- 设置账号--%>
	  	  <hrms:priv func_id="3003201,08030101">
          <td align="center" class="TableRow" nowrap style="border-right:none;">
			<bean:message key="setaccount.label"/>
			</td>
	    </hrms:priv>        		    	        	    	    	    		        	        	        
           </tr>
   	  </thead>
   	  <%--查询数据库分页标签--%>
   	  <hrms:paginationdb id="element" name="accountForm" sql_str="accountForm.repeatSql_str" table="" where_str="accountForm.repeatWhere_str" columns="${accountForm.repeatColumns}" order_by="${accountForm.repeatOrder_by}" page_id="pagination" pagerows="${accountForm.pagerows}" indexes="indexes" >
          <%LazyDynaBean item=(LazyDynaBean)pageContext.getAttribute("element");
            String desc=(String)item.get("e0122");
            if(i%2==0){
          %>
          <tr class="trShallow" onClick="javascript:tr_onclick(this,'')">
          <%}else{%>
          <tr class="trDeep" onClick="javascript:tr_onclick(this,'')">
          <%}i++;%>
	    	<td align="left" class="RecordRow" nowrap style="border-left:none;">
          	<hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>  	      
          	&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;
	    </td>            
        <td align="left" class="RecordRow"  title="<hrms:orgtoname codeitemid='<%=desc%>' level="10"/>" nowrap>
          	<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" scope="page" uplevel="${accountForm.uplevel}"/>  	      
          	&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;    
	    </td>
        <td align="left" class="RecordRow" nowrap title=''>
            <hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>  	      
          	&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;     
	    </td>
         <td align="left" class="RecordRow" nowrap>
             &nbsp;<bean:write name="element" property="a0101" filter="true"/>&nbsp;
	    </td>
<% 	
	//通过repeatTable判断显示的表单
	if(repeatFlag.equals("true")){	
%>
			<td align="left" class="RecordRow" nowrap>
             &nbsp;<bean:write name="element" property="username" filter="true"/>&nbsp;
	   		 </td>
<%} %>
	    <hrms:priv func_id="3003201,08030101">
            <td align="center" class="RecordRow" nowrap style="border-right:none;">
            <a onclick="edit('<bean:write name="element" property="a0100" filter="true"/>');"><img src="/images/edit.gif" border=0 style="cursor:hand;"></a>       	
	    </td>
	    </hrms:priv>
	   </tr>
    </hrms:paginationdb>
</table>
</div>
<%} %>
<div class="fixedDiv3">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            	<hrms:paginationtag name="accountForm"
								pagerows="${accountForm.pagerows}" property="pagination"
								scope="page" refresh="true"></hrms:paginationtag>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="accountForm" property="pagination" nameId="accountForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table>
</div>
<%--<table  width="100%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
			<td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="accountForm" property="pagination" nameId="accountForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table>
--%></html:form>