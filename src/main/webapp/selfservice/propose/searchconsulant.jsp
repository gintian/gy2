<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
	int i=0;

	//用于自助服务导航图返回取值判断（从导航图进去显示返回，从左侧树菜单进去隐藏返回）
	UserView userView = (UserView)request.getSession().getAttribute("userView");
	String returnvalue = request.getParameter("returnvalue")==null?"":request.getParameter("returnvalue");

%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script> 
<script language="javascript">
  function deletes()
  {
     var len=document.consulantForm.elements.length;
     var isCorrect=false;
     for (i=0;i<len;i++)
     {
           if (document.consulantForm.elements[i].type=="checkbox")
            {
              if( document.consulantForm.elements[i].checked==true && "selbox" != document.consulantForm.elements[i].name)
                isCorrect=true;
            }
     }
    if(!isCorrect)
    {
          alert("请选择记录！");
          return false;
     }
     if(confirm("确认要删除该记录？"))
     {
          consulantForm.action = "/selfservice/propose/searchconsulant.do?b_delete=link";
          consulantForm.submit();
     }
  }
</script>
<html:form action="/selfservice/propose/searchconsulant">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" style="margin-top:6px;">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
            <input type="checkbox" name="selbox" onclick="batch_select(this,'consulantForm.select');" title='<bean:message key="label.query.selectall"/>'>	    
            </td>           
            <td align="center" class="TableRow" nowrap>
		<bean:message key="column.submit.man"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="column.submit.date"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="column.submit.consult"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="column.reply.man"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="column.reply.date"/>&nbsp;
	    </td>
	    
            <td align="center" class="TableRow" nowrap>
		<bean:message key="column.reply.content"/>&nbsp;
	    </td>	
	    <!--   	    	    
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.view"/>            	
	    </td>
	    -->
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.edit"/>            	
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.reply"/>            	
	    </td>	    	    		        	        	        
           </tr>
   	  </thead>
          <hrms:extenditerate id="element" name="consulantForm" property="consulantForm.list" indexes="indexes"  pagination="consulantForm.pagination" pageCount="10" scope="session">
          <bean:define id="id" name="element" property="string(id)"/>
          <%
          id = PubFunc.encrypt((String)id);
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
            <td align="center" class="RecordRow" nowrap>
            	    <logic:equal name="consulantForm" property="userAdmin" value="false">
            	     <logic:equal name="element" property="string(createuser)" value="${userView.userFullName}">
     		  	 <hrms:checkmultibox name="consulantForm" property="consulantForm.select" value="true" indexes="indexes"/>
	    	    </logic:equal>
	    	    </logic:equal>
	    	    <logic:equal name="consulantForm" property="userAdmin" value="true"> 
	    	    	<hrms:checkmultibox name="consulantForm" property="consulantForm.select" value="true" indexes="indexes"/>
	    	    </logic:equal>	
	    </td>            
            <td align="left" class="RecordRow" nowrap>
                   &nbsp;<bean:write name="element" property="string(createuser)" filter="true"/>&nbsp;
	    </td>
         
            <td align="left" class="RecordRow" nowrap>
                    &nbsp;<bean:write  name="element" property="string(createtime)" filter="true"/>&nbsp;
	    </td>
            <td align="left" class="RecordRow" nowrap>
                &nbsp;<a href="/selfservice/propose/viewconsulant.do?b_query=link&a_id=<%=id %>">                    
                    <bean:write  name="element" property="string(ccontent)" filter="true"/>&nbsp;
                </a>
	    </td>
            <td align="left" class="RecordRow" nowrap>
                    &nbsp;<bean:write  name="element" property="string(replyuser)" filter="true"/>&nbsp;
	    </td> 	                
            <td align="left" class="RecordRow" nowrap>
                    &nbsp;<bean:write  name="element" property="string(replytime)" filter="true"/>&nbsp;
	    </td>
            <td align="left" class="RecordRow" nowrap>
                    &nbsp;<bean:write  name="element" property="string(rcontent)" filter="true"/>&nbsp;
	    </td>
	    <!--	    
            <td align="center" class="RecordRow" nowrap>
            	<a href="/selfservice/propose/viewconsulant.do?b_query=link&a_id=<bean:write name="element" property="string(id)" filter="true"/>"><img src="/images/view.gif" border=0></a>
	    </td>
	    -->
            <td align="center" class="RecordRow" nowrap>
            	<logic:equal name="element" property="string(createuser)" value="${userView.userFullName}">
            		<a href="/selfservice/propose/addconsulant.do?b_query=link&a_id=<%=id %>"><img src="/images/edit.gif" border=0></a>
	    	</logic:equal>
	    </td>
            <td align="center" class="RecordRow" nowrap>
              <logic:notEqual name="element" property="string(createuser)" value="${userView.userFullName}">
           	<hrms:priv func_id="110601">
            	  <a href="/selfservice/propose/replyconsulant.do?b_query=link&a_id=<%=id %>"><img src="/images/edit.gif" border=0></a>
	   	</hrms:priv> 
	      </logic:notEqual>
	    </td>	    	    		        	        	        
          </tr>
        </hrms:extenditerate>
        
</table>
<table  width="100%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="consulantForm" property="consulantForm.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="consulantForm" property="consulantForm.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="consulantForm" property="consulantForm.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="consulantForm" property="consulantForm.pagination"
				nameId="consulantForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
<table  width="70%" align="center">
          <tr>
            <td align="center" height="35px;">
         	<hrms:submit styleClass="mybutton" property="b_add">
            		<bean:message key="button.insert"/>
	 	</hrms:submit>
	 	<input type="button" name="b_delete" class="mybutton" value="<bean:message key="button.delete" />" onclick="javascript:deletes();">
        <% 
		if(userView!=null && userView.getBosflag()!=null && returnvalue.equals("dxt"))
		{
		%>
	        <!-- 自助服务导航图返回 -->
	        <input type="button" name="b_return" value="<bean:message key="button.return"/>" class=mybutton  onclick="hrbreturn('selfinfo','il_body','consulantForm')">
		<%} %>
            </td>
          </tr>          
</table>

</html:form>
<script>
if(parent.myNewBody!=null)
 {
    parent.myNewBody.cols="*,0"
  }
if(!getBrowseVersion()){//兼容非IE浏览器  wangb 20171123  
	  var form = document.getElementsByName('consulantForm')[0];//设置form表单 width样式   and  ie下consulantForm js变量重名，删除操作无效  bug 34373 wangb 20180131  
	  form.style.width='99.5%';
}
</script>

