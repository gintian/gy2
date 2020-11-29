<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.actionform.propose.ProposeForm"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView" %>
<%
	//用于自助服务导航图返回取值判断（从导航图进去显示返回，从左侧树菜单进去隐藏返回）
	UserView userView = (UserView)request.getSession().getAttribute("userView");
	String returnvalue = request.getParameter("returnvalue")==null?"":request.getParameter("returnvalue");
 %>
 
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script> 
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript" src="/js/validate.js"></script>
<script language="javascript" src="/js/constant.js"></script>
<script language="javascript" src="propose.js"></script>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script>
function changes(){
	  
    var start_date=document.getElementById("start_date").value;
    var end_date=document.getElementById("end_date").value;
    
    if(isDate(start_date) && isDate(end_date) && checkDate(start_date,end_date)){
  	  proposeForm.action="/selfservice/propose/searchpropose.do?b_query=link";
        proposeForm.submit();
    }else{
    document.getElementById("start_date").value="${proposeForm.start_date}";
    document.getElementById("end_date").value="${proposeForm.end_date}";
    }
    
}

</script>
<jsp:useBean id="proposeForm" class="com.hjsj.hrms.actionform.propose.ProposeForm" scope="session"/>

<%
   int i=1;
%>

<html:form action="/selfservice/propose/searchpropose">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" >
  <tr height="35">
    <td align="left" valign="middle" nowrap>
             <input type="hidden" name="date_flag" value="1" id="date_flag"/>
			<bean:message key="label.from"/>
   	  	 	<input type="text" name="start_date" id="start_date" value="${proposeForm.start_date}" extra="editor" class="text4" id="editor1"  dropDown="dropDownDate">
   	  	 	<bean:message key="label.to"/>
   	  	 	<input type="text" name="end_date" id="end_date" value="${proposeForm.end_date}" extra="editor" class="text4"  dropDown="dropDownDate">
            <span style="vertical-align: middle;">  &nbsp;<input type="button" name="br_return" value='<bean:message key="button.query"/>' class="mybutton" onclick="changes();"></span>
    </td>         
 </tr>

</table>
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
            <input type="checkbox" name="selbox" onclick="batch_select(this,'proposeForm.select');" title='<bean:message key="label.query.selectall"/>'>	    
            </td>           
            <td align="center" class="TableRow" nowrap>
		<bean:message key="column.submit.man"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="column.submit.date"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="column.submit.propose"/>&nbsp;
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
	    <td align="center" class="TableRow" nowrap>
		<bean:message key="column.sys.status"/>            	
	    </td>
           </tr>
   	  </thead>
          <hrms:extenditerate id="element" name="proposeForm" property="proposeForm.list" indexes="indexes"  pagination="proposeForm.pagination" pageCount="10" scope="session">
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
            <td align="center" class="RecordRow" nowrap>
            	  
            	 <logic:equal name="proposeForm" property="userAdmin" value="false"> 
            	<logic:equal name="element" property="string(createuser)" value="${userView.userFullName}">
        		 <hrms:checkmultibox name="proposeForm" property="proposeForm.select" value="true" indexes="indexes"/>
	 	 </logic:equal>
	   	</logic:equal>
	   	 <logic:equal name="proposeForm" property="userAdmin" value="true"> 
	   	 	<hrms:checkmultibox name="proposeForm" property="proposeForm.select" value="true" indexes="indexes"/>
	   	 </logic:equal>
	    </td>            
            <td align="left" class="RecordRow" nowrap>
            	   <logic:equal name="element" property="string(annymous)" value="1">
            	   	&nbsp;匿名
            	   </logic:equal>
            	   <logic:notEqual name="element" property="string(annymous)" value="1">
                   &nbsp;<bean:write name="element" property="string(createuser)" filter="true"/>&nbsp;
                   </logic:notEqual>
	    </td>
         
            <td align="left" class="RecordRow" nowrap>
                    &nbsp;<bean:write  name="element" property="string(createtime)" filter="true"/>&nbsp;
	    </td>
            <td align="left" class="RecordRow" nowrap>
                    &nbsp;<a href="/selfservice/propose/viewpropose.do?b_query=link&a_id=<bean:write name="element" property="string(id)" filter="true"/>">
                       <bean:write  name="element" property="string(scontent)" filter="true"/>&nbsp;
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
            	<a href="/selfservice/propose/viewpropose.do?b_query=link&a_id=<bean:write name="element" property="string(id)" filter="true"/>"><img src="/images/view.gif" border=0></a>
	    </td>
	    -->
            <td align="center" class="RecordRow" nowrap>
            
            	<logic:equal name="element" property="string(createuser)" value="${userView.userFullName}">
            	  <logic:empty name="element" property="string(replyuser)">
            	     <a href="/selfservice/propose/addpropose.do?b_query=link&a_id=<bean:write name="element" property="string(id)" filter="true"/>"><img src="/images/edit.gif" border=0></a>
	   	          </logic:empty>
	   	       </logic:equal>
	    </td>
        <td align="center" class="RecordRow" nowrap>
           	<logic:notEqual name="element" property="string(createuser)" value="${userView.userFullName}">
           		<logic:empty name="element" property="string(replyuser)">
           	       <hrms:priv func_id="110501">
           	          <a href="/selfservice/propose/replypropose.do?b_query=link&a_id=<bean:write name="element" property="string(id)" filter="true"/>&bread=1"><img src="/images/edit.gif" border=0></a>
    	           </hrms:priv> 
    	        </logic:empty>
	    	</logic:notEqual>
	    </td>	    	    		        	        	        
	    <td align="left" class="RecordRow" nowrap>
	    	<logic:equal name="element" property="string(bread)" value="1">
	    		&nbsp;<bean:message key="conlumn.propose.reading"/>
	    	</logic:equal>
	    	<logic:notEqual name="element" property="string(bread)" value="1">
	    		&nbsp;
	    	</logic:notEqual>
	    </td>
          </tr>
        </hrms:extenditerate>
        
</table>
<table  width="100%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="proposeForm" property="proposeForm.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="proposeForm" property="proposeForm.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="proposeForm" property="proposeForm.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="proposeForm" property="proposeForm.pagination"
				nameId="proposeForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
<table  width="100%"  style="margin-top: 5px;" align="center" >
          <tr>
            <td align="center">
            <hrms:priv func_id="110503">
	         	<hrms:submit styleClass="mybutton" property="b_add">
	            		<bean:message key="button.insert"/>
		 	    </hrms:submit>
	 	    </hrms:priv>
	 	<input type="button" name="b_delete" class="mybutton" value="<bean:message key="button.delete" />" onclick="javascript:deletes();">
	 <hrms:priv func_id="110502">
        <input type="button" class="mybutton" value="<bean:message key="goabroad.collect.educe.excel"/>" onclick="javascript:outExcel();">
     </hrms:priv>
     	<% 
		if(userView!=null && userView.getBosflag()!=null && returnvalue.equals("dxt"))
		{
		%>
			<!-- 自助服务导航图返回 -->
			<input type="button" name="b_return" value="<bean:message key="button.return"/>" class=mybutton  onclick="hrbreturn('selfinfo','il_body','proposeForm')">
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
</script>
<script language="javascript">
   var dropDownDate=createDropDown("dropDownDate");
   var __t=dropDownDate;
   __t.type="date";
   __t.tag="";
   _array_dropdown[_array_dropdown.length]=__t;
   initDropDown(__t);
   
   var dropDownList=createDropDown("dropDownList");
   var __t=dropDownList;
   __t.type="list";
   __t.tag="";
   _array_dropdown[_array_dropdown.length]=__t;
   initDropDown(__t);   
</script>
<script language="javascript">
  initDocument();
  if(!getBrowseVersion()){//兼容非IE浏览器  wangb 20171123  
	  var form = document.getElementsByName('proposeForm')[0];//设置form表单 width样式  and  ie浏览器中 proposeForm 表单变量重名 删除按钮和查询按钮无效 bug 34354 34352   wangb 20180131
	  form.style.width='99.5%';
	  //今天日期文字 大小和显示位置处理   wangb 20171130
	  var lblToday = document.getElementById('lblToday');
	  lblToday.style.lineHeight='';
  	  var td = lblToday.parentNode;
  	  td.style.position = 'relative';
  	  var a = lblToday.getElementsByTagName('a')[0];
  	  a.style.fontSize = '12px';
  	  a.style.position = 'absolute';	
  	  a.style.right = '-7px';	
 	  a.style.top = '4px';	
  	  a.style.transform = 'scale(0.75)';
  	  var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串  
  	  var isSafari = userAgent.indexOf("Safari") > -1 && userAgent.indexOf("Chrome") == -1; //判断是否Safari浏览器 
	  if(isSafari){// safari 浏览器 特殊处理    今天日期文字显示位置处理   wangb 20171130
		 var stimeIframe = document.getElementById('stime');
		 stimeIframe.setAttribute('height','30');
		 a.style.right = '-10px';	
	 	 a.style.top = '12px';
	 	 a.style.fontSize='13px';
	  }
  }
</script>

