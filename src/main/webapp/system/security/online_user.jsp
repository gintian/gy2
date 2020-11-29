<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="com.hjsj.hrms.actionform.sys.OnLineForm"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient"%>
<%@ page import="com.hrms.struts.admin.OnlineUserView"%>
<%@ page import="com.hrms.struts.valueobject.UserView,org.apache.log4j.Category,java.lang.StringBuffer"%>
<%
	 String sessionId = session.getId();
	 UserView userView=(UserView)session.getAttribute("userView");
	 int i=0;
	 EncryptLockClient lockclient=(EncryptLockClient)session.getServletContext().getAttribute("lock");
	 OnLineForm onLineForm = (OnLineForm)session.getAttribute("onlineUser");
	 String filename = onLineForm.getFilename();
	 //xus 20/4/18 vfs改造
	 filename = PubFunc.encrypt(filename);
	 Category log = Category.getInstance(EncryptLockClient.class.getName());
	 StringBuffer sb = new StringBuffer();
	 int module_count[] = lockclient.module_count;
	 for(int n=0;n<module_count.length;n++){
	 	sb.append(module_count[n]+",");
	 }
	 log.debug("lockclient.module_count#####"+sb.toString());
	 sb.setLength(0);
	  String module_names[] = lockclient.module_names;
	 for(int n=0;n<module_names.length;n++){
	 	sb.append(module_names[n]+",");
	 }
	 log.debug("lockclient.module_names#####"+sb.toString());
	 sb.setLength(0);
	   String module_user[] = lockclient.module_user;
	 for(int n=0;n<module_user.length;n++){
	 	sb.append(module_user[n]+",");
	 }
	 log.debug("lockclient.module_user#####"+sb.toString());
%>

<style id=iframeCss>
div{
	cursor:hand;font-size:12px;
   }
a{
text-decoration:none;color:black;font-size:12px;
}

a.a1:active {
	color: #003100;
	text-decoration: none;
}
a.a1:hover {
	color: #FFCC00;
	text-decoration: none;
}
a.a1:visited {	
	text-decoration: none;
}
a.a1:link {
	color: #003100;
	text-decoration: none;
}
</style>
<script type="text/javascript">	
//导出
function outputexcel(){
	//xus 20/4/18 vfs改造
	var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid=<%=filename%>","excel");
}	

//删除在线用户
function deleteOnline(){
	var dd=false;
	var selectid=new Array();
   	for(var i=0;i<document.onlineUser.elements.length;i++){
	   if(document.onlineUser.elements[i].type=='checkbox'&&document.onlineUser.elements[i].name!="selbox"){	  
		  if(document.onlineUser.elements[i].checked){
			selectid[i]=document.onlineUser.elements[i];
		  	dd=true;
		  	break;
		  }
	   }
    }
   	if(dd){
   		return confirm("确认注销在线用户？");		
   	}else{
   		alert("请选择在线用户!");
   	 	return dd;   		
   	}
}
</script>
<!-- 【6557】在线用户刷新报500  jingq  add 2015.01.07 -->
<body oncontextmenu="return false">
<html:form action="/system/security/online_user">
<table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
         <td align="center" class="TableRow" nowrap>
				<input type="checkbox" name="selbox" onclick="batch_select(this,'userlistform.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;
         </td>
            <td align="center" class="TableRow" nowrap width="10%">
		<bean:message key="column.sys.org"/>&nbsp;
	    </td>         
            <td align="center" class="TableRow" nowrap width="15%">
		<bean:message key="column.sys.dept"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap width="15%">
		<bean:message key="column.sys.pos"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap width="30%">
		<bean:message key="column.sys.name"/>&nbsp;
            </td>
            <td align="center" class="TableRow" nowrap >
		<bean:message key="column.sys.ipaddr"/>&nbsp;
            </td>
            <td align="center" class="TableRow" nowrap >
		<bean:message key="column.sys.logindate"/>&nbsp;
            </td>                                 		        	        	        
           </tr>
   	  </thead>
          <hrms:extenditerate id="element" name="onlineUser" property="userlistform.list" indexes="indexes"  pagination="userlistform.pagination" pageCount="20" scope="session">
          <%
          if(!userView.hasTheFunction("30048")) //交易类授权功能号无法控制，改为无权限不显示数据
				 break;
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
          <%
            //【9358】系统管理：在线用户，直接关闭浏览器退出系统后，再重新登录系统，在线用户页面同一个用户会出现多次记录，不对   jingq upd 2015.05.05
         	OnlineUserView it=(OnlineUserView)pageContext.getAttribute("element");
          	if(sessionId.equals(it.getSession().getId())){
          %>
          <td align="center" class="RecordRow" nowrap>
               &nbsp;
          </td>
          <%}else{ %>
          <td align="center" class="RecordRow" nowrap>
               <hrms:checkmultibox name="onlineUser" property="userlistform.select" value="true" indexes="indexes"/>&nbsp;
          </td>  
           <%} %>
          <td align="left" class="RecordRow" nowrap>
          	<hrms:codetoname codeid="UN" name="element" codevalue="orgname" codeitem="codeitem" scope="page"/>  	      
          	&nbsp;&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;
	   	 </td>            
           <td align="left" class="RecordRow" nowrap>
          	<hrms:codetoname codeid="UM" name="element" codevalue="dept" codeitem="codeitem" scope="page"/>  	      
          	&nbsp;&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;
	   	 </td>         
            <td align="left" class="RecordRow" nowrap>
          	<hrms:codetoname codeid="@K" name="element" codevalue="pos" codeitem="codeitem" scope="page"/>  	      
          	&nbsp;&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;
            </td>
            <td align="left" class="RecordRow" >
                 &nbsp;&nbsp;<bean:write name="element" property="username" filter="true"/>&nbsp;
                 <% 
                   OnlineUserView item=(OnlineUserView)pageContext.getAttribute("element");
                   String sss=lockclient.getTheUserAccessModule(item.getUserId());
                   if(sss.length()>0)
                   {
                     sss="("+sss+")";
                   }
                 %>
                 <%=sss%>	    		   
	        </td>   
            <td align="center" class="RecordRow" nowrap>
                 <bean:write name="element" property="ip_addr" filter="true"/>&nbsp;             
	    </td>
            <td align="center" class="RecordRow" nowrap>
                 <bean:write name="element" property="login_date" filter="true"/>&nbsp;             
	    </td>	                
          </tr>
        </hrms:extenditerate>
        
</table>
<table  width="80%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="onlineUser" property="userlistform.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="onlineUser" property="userlistform.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="onlineUser" property="userlistform.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="onlineUser" property="userlistform.pagination"
				nameId="userlistform" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
<table  width="80%" align="center" border="0" cellpadding="0" cellspacing="0">
		<tr style="height: 35px;" align="center"><td align="center">			
			<hrms:submit styleClass="mybutton" property="b_delete" onclick="return deleteOnline();">
            		注销在线用户
	 		</hrms:submit>	 		
			&nbsp;<button class=mybutton onclick="outputexcel();">导出</button>
		</td></tr></table>
</html:form>
</body>