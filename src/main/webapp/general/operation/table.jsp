<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hrms.hjsj.utils.Sql_switcher,org.apache.commons.beanutils.LazyDynaBean,com.hrms.hjsj.sys.IResourceConstant,com.hjsj.hrms.actionform.general.operation.OperationForm,java.util.HashMap"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>

<%
	int i = 0;
	/**
	 String aurl = (String)request.getServerName();
	String port=request.getServerPort()+"";
	String prl=request.getProtocol();
	int idx=prl.indexOf("/");
	prl=prl.substring(0,idx);    
	String url_s=prl+"://"+aurl+":"+port;
	 **/
	String url_s = SystemConfig.getCsClientServerURL(request);
	// zxj 传给插件的dbtype应传入实际数据库类型
	String dbtype = String.valueOf(Sql_switcher.searchDbServerFlag());
	String curUser = "";
	EncryptLockClient lockclient=(EncryptLockClient)session.getServletContext().getAttribute("lock");
	UserView userView = (UserView) pageContext.getSession().getAttribute(WebConstant.userView);	
	
	int versionFlag = 1;
	//zxj 20160613 人事异动不再区分标准版专业版
	if (userView != null){
		//versionFlag = userView.getVersion_flag(); // 1:专业版 0:标准版	
		curUser = userView.getUserName();
	}
		
	OperationForm operationForm = (OperationForm) session.getAttribute("operationForm");
	String useraa = operationForm.getUsertype();
	int j = 0;
	if(useraa!=null && useraa.equals("1"))
		j=6;
	else
		j=4;
	HashMap spflagmap = operationForm.getSpflagmap();
%>
<%
    String bosflag= userView.getBosflag();//得到系统的版本号
%>  
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script type="text/javascript" src="/general/sys/hjaxmanage.js"></script>
 
<script type="text/javascript">
var oper =0;
	 
function ShowDesigner(tabid){
    if(oper==0){
        alert("程序正在下载插件，请稍候...");
        return;
	}
	
	var TabId = tabid; //业务模板编号
	var strUrl="<%=url_s%>";
    var DBType=<%=dbtype%>;
    var curUser="<%=curUser%>";      
	if(!AxManager.setup(null, 'WorkFlowDesigner', 0, 0, null, AxManager.workFlowPkgName))
  		return false;	
	
	var obj = document.getElementById("WorkFlowDesigner");
	if (obj){
	    obj.OpenWorkFlowDesigner(strUrl,DBType,TabId,curUser);
    }
	
}
function changeoper(){
oper =1;
}
</script>
<html>
<body onload='changeoper()' >
<html:form action="/general/operation/table">
<%
    if(bosflag!=null&&!bosflag.equals("hcm")){
%>
<Br>
<%
}
%>
	<bean:define id="usertype" name="operationForm" property="usertype"/>
	<table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
		<thead>
			<TR >
				<logic:equal value="1" name="operationForm" property="usertype">
					<td align="center" class="TableRow" nowrap>
					<bean:message key="column.select"/>
					</td>
				</logic:equal>
				<td align="center" class="TableRow" nowrap>
					<bean:message key="operation.class"/>
				</td>
				<td align="center" class="TableRow" nowrap>
					<bean:message key="operation.process"/>
				</td>
				<logic:equal value="1" name="operationForm" property="usertype">
				<td align="center" class="TableRow" nowrap>
				<bean:message key="button.edit"/>
				</td>
				</logic:equal>
				<td align="center" class="TableRow" nowrap>
					<bean:message key="operation.approveway"/>
				</td>
				<td align="center" class="TableRow" nowrap>
					<bean:message key="operation.approve.dot"/>
				</td>
			</TR>
		</thead>		
		
		
		<logic:equal value="1" name="operationForm" property="usertype">
		
		<hrms:paginationdb id="element" name="operationForm" sql_str="operationForm.sql" table="" where_str="operationForm.where" columns="operationForm.column" order_by="operationForm.orderby" pagerows="15" page_id="pagination" indexes="indexes">
		<bean:define id="tabid" name="element" property="tabid"></bean:define>
		<bean:define id="sp_flag" name="element" property="sp_flag"></bean:define>
          <%
          
           

	        if (i % 2 == 0) {
	          %>
	          <tr class="trShallow">
	          <%
	          	} else {
	          %>
	          <tr class="trDeep">
	          <%
	          	}
	          			i++;
	          %>  
			<logic:equal value="1" name="operationForm" property="usertype">
				<td align="center" class="RecordRow" nowrap="nowrap">
					<hrms:checkmultibox name="operationForm" property="pagination.select" value="true" indexes="indexes" />
				</td>
			</logic:equal>
			<td align="center" class="RecordRow" nowrap="nowrap">
			<bean:write name="element" property="operationname"/>
			</td>
			<td align="left" class="RecordRow" nowrap="nowrap">&nbsp;
			<bean:write name="element" property="name"/>-${tabid}
			</td>
			<logic:equal value="1" name="operationForm" property="usertype">
				<td align="center" class="RecordRow" nowrap="nowrap">
				<a href="/general/operation/table.do?b_update=link&tabid=${tabid}"><img src="/images/edit.gif" border=0 title='<bean:message key="kq.report.update"/>' style="cursor:hand;"/></a>
				</td>
			</logic:equal>
			<td align="center" class="RecordRow" nowrap="nowrap">
			<a href="/general/operation/updateapproveway.do?b_query=link&tabid=${tabid}&usertype=${usertype}"><img src="/images/edit.gif" border=0 title='<bean:message key="kq.report.update"/>' style="cursor:hand;"/></a>
			</td>
			<td align="center" class="RecordRow" nowrap="nowrap">
			
			<logic:equal value="1" name="element" property="sp_flag">
				 <%
				  	if (userView.getVersion() >= 50&& userView.getVersion()<60 && versionFlag == 1) { 
					 	if (spflagmap != null&& spflagmap.get(tabid) != null&& spflagmap.get(tabid).equals("0")) {
					 %>
					 <a href="javascript:ShowDesigner(${tabid})"><img src="/images/edit.gif" border=0 title='流程设计' style="cursor:hand;"/></a>
					<%
						}
					%>
				 <%
					} 
					else if ( userView.getVersion() >= 60&&spflagmap != null&& spflagmap.get(tabid) != null&& spflagmap.get(tabid).equals("0")) {
						if(lockclient.isHaveBM(41)){
				 %>
					 <a href="javascript:ShowDesigner(${tabid})"><img src="/images/edit.gif" border=0 title='流程设计' style="cursor:hand;"/></a>
			     <%
						 }
						 else
						 {
				 %>	
					 <a href="/general/template/nodedefine/wf_node_define.do?b_search=link&tabid=${tabid}&returnflag=1"><img src="/images/edit.gif" border=0 title='<bean:message key="kq.report.update"/>' style="cursor:hand;"/></a>
				
				 <%	 
						 }
					}
					else {
						if (spflagmap != null&& spflagmap.get(tabid) != null&& spflagmap.get(tabid).equals("0")) {
				%>
				 
				<a href="/general/template/nodedefine/wf_node_define.do?b_search=link&tabid=${tabid}&returnflag=1"><img src="/images/edit.gif" border=0 title='<bean:message key="kq.report.update"/>' style="cursor:hand;"/></a>
				
				<%
						}
				
					    }
							%>
			</logic:equal>
			
			</td>
			</tr>
		 
		</hrms:paginationdb>
	<tr>
	<td colspan="<%=j %>">
	<table width="80%" align="center" class="RecordRowP">
			<tr>
				<td valign="bottom" align="left" class="tdFontcolor" nowrap>
					<bean:message key="label.page.serial" />
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="label.page.sum" />
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.page.row" />
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page" />
				</td>
				<td align="right" nowrap="nowrap" class="tdFontcolor">
		     	 <p align="right">
						<hrms:paginationdblink name="operationForm" property="pagination" nameId="browseRegisterForm" scope="page">
						</hrms:paginationdblink>
				</td>
			</tr>
			<logic:equal value="1" name="operationForm" property="usertype">
			<tr>
			   <td colspan='2' align="center">
			        <hrms:submit styleClass="mybutton" property="b_add">
						<bean:message key="kq.search_feast.new"/>
					</hrms:submit>
					<hrms:submit styleClass="mybutton" property="b_del">
						<bean:message key="kq.search_feast.delete"/>
					</hrms:submit>
			   </td>
			</tr>
		</logic:equal>	
		</table>
	</td>
	</tr>
	</table>	
	</logic:equal>	
	<logic:notEqual value="1" name="operationForm" property="usertype">	
		
	 <hrms:extenditerate id="element" name="operationForm" property="pageListForm.list"   
			indexes="indexes"  pagination="pageListForm.pagination" pageCount="15" scope="session">
			<bean:define id="tabid" name="element" property="tabid"></bean:define>
			<bean:define id="sp_flag" name="element" property="sp_flag"></bean:define>
			<%
			
			if (i % 2 == 0) {%>
					<tr class="trShallow"  onclick='tr_onclick(this,"#F3F5FC");' >
			<%} else {%>
					<tr class="trDeep"  onclick='tr_onclick(this,"#E4F2FC");' >
			<%}%>
		
			<td align="center" class="RecordRow" nowrap="nowrap">
			<bean:write name="element" property="operationname"/>
			</td>
			<td align="left" class="RecordRow" nowrap="nowrap">&nbsp;
			<bean:write name="element" property="name"/>-${tabid}
			</td>
			<logic:equal value="1" name="operationForm" property="usertype">
			<td align="center" class="RecordRow" nowrap="nowrap">
			<a href="/general/operation/table.do?b_update=link&tabid=${tabid}"><img src="/images/edit.gif" border=0 title='<bean:message key="kq.report.update"/>' style="cursor:hand;"/></a>
			</td>
			</logic:equal>
			<td align="center" class="RecordRow" nowrap="nowrap">
			<a href="/general/operation/updateapproveway.do?b_query=link&tabid=${tabid}&usertype=${usertype}"><img src="/images/edit.gif" border=0 title='<bean:message key="kq.report.update"/>' style="cursor:hand;"/></a>
			</td>
			<td align="center" class="RecordRow" nowrap="nowrap">
			
			<logic:equal value="1" name="element" property="sp_flag">
					 <%
				  	if (userView.getVersion() >= 50&& userView.getVersion()<60 && versionFlag == 1) { 
					 	if (spflagmap != null&& spflagmap.get(tabid) != null&& spflagmap.get(tabid).equals("0")) {
					 %>
					 <a href="javascript:ShowDesigner(${tabid})"><img src="/images/edit.gif" border=0 title='流程设计' style="cursor:hand;"/></a>
					<%
						}
					%>
				 <%
					} 
					else if ( userView.getVersion() >= 60&&spflagmap != null&& spflagmap.get(tabid) != null&& spflagmap.get(tabid).equals("0")) {
						if(lockclient.isHaveBM(41)){
				 %>
					 <a href="javascript:ShowDesigner(${tabid})"><img src="/images/edit.gif" border=0 title='流程设计' style="cursor:hand;"/></a>
			     <%
						 }
						 else
						 {
				 %>	
					 <a href="/general/template/nodedefine/wf_node_define.do?b_search=link&tabid=${tabid}&returnflag=1"><img src="/images/edit.gif" border=0 title='<bean:message key="kq.report.update"/>' style="cursor:hand;"/></a>
				
				 <%	 
						 }
					}
					else {
						if (spflagmap != null&& spflagmap.get(tabid) != null&& spflagmap.get(tabid).equals("0")) {
				%>
				 
				<a href="/general/template/nodedefine/wf_node_define.do?b_search=link&tabid=${tabid}&returnflag=1"><img src="/images/edit.gif" border=0 title='<bean:message key="kq.report.update"/>' style="cursor:hand;"/></a>
				
				<%
						}
						}
							%>
			</logic:equal>
			
			</td> 
		
		
		
		
		
		
					</tr>	
		
		<%
				i++;
				%>
		</hrms:extenditerate>
	
	<tr>
	<td colspan="<%=j %>">
	<table width="100%" align="center" class="RecordRowP">
			<tr>
				 <td valign="bottom" class="tdFontcolor">
			            <bean:message key="label.page.serial"/>
						<bean:write name="operationForm" property="pageListForm.pagination.current" filter="true" />
						<bean:message key="label.page.sum"/>
						<bean:write name="operationForm" property="pageListForm.pagination.count" filter="true" />
						<bean:message key="label.page.row"/>
						<bean:write name="operationForm" property="pageListForm.pagination.pages" filter="true" />
						<bean:message key="label.page.page"/>
						
						 每页显示 15 条&nbsp;&nbsp; 
				</td>
		               <td  align="right" nowrap class="tdFontcolor">
			          <p align="right">
			          <hrms:paginationlink name="operationForm" property="pageListForm.pagination" nameId="pageListForm" >
					</hrms:paginationlink>
					 
				</td>
			</tr>
	  </table>
	</td>
	</tr>
	</table>	
	</logic:notEqual>	
		

</html:form>
</body>

</html>



