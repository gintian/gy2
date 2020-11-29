<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.ArrayList,com.hrms.struts.valueobject.UserView,org.apache.commons.beanutils.LazyDynaBean,com.hjsj.hrms.actionform.smartphone.SPhoneForm"%>
<%
	int i=0;
	SPhoneForm sPhoneForm = (SPhoneForm)session.getAttribute("sphoneForm");
	int allcount=Integer.parseInt(sPhoneForm.getAllcount());
	//System.out.println(allcount);
	ArrayList dbprelist = ((UserView)session.getAttribute("userView")).getPrivDbList();
	String dbsize=dbprelist.size()+"";
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>移动助手</title>
	 <link rel="stylesheet" href="../jquery/css/jquery.mobile-1.0a2.min.css" type="text/css">
	 <script type="text/javascript" src="../jquery/jquery-3.5.1.min.js"></script>
	 <script type="text/javascript" src="../jquery/rpc_command.js"></script>
	 <script type="text/javascript">

		function shownext(id){
			sphoneForm.action="/phone-app/app/hroster.do?b_query=link&sortid="+id+"&flag=2";
			sphoneForm.submit();
		}
		
		function showpersons(id){
			//sphoneForm.action="/phone-app/app/hroster.do?b_query=link&a_code="+id+"&dbpre=";
			sphoneForm.action="/general/muster/hmuster/select_muster_name.do?b_next2=b_next2&isGetData=1&clears=1&operateMethod=direct&modelFlag=3&returnflag=mobile&tabID="+id+"&cardid=${sphoneForm.cardid }&isMobile=1&dbpre="
			//sphoneForm.action="/general/muster/hmuster/select_muster_name.do?b_view=link&isGetData=1&modelFlag=3&res=1&clears=1&operateMethod=direct&returnflag=mobile&tabID="+id+"&cardid=${sphoneForm.cardid }&isMobile=1";
			sphoneForm.submit();
		}
		 <logic:equal value="0" name="sphoneForm" property="allcount">
	     	alert("您没有可使用的员工名册！");
	     </logic:equal> 
		</script>
	 <script type="text/javascript" src="../jquery/jquery.mobile-1.0a2.min.js"></script>
</head>
<body>
<html:form action="/phone-app/app/hroster">
<div data-role="page" id="mainbar"> 
	<div data-role="header"  data-position="fixed" data-position="inline"> 
		<logic:equal value="1" name="sphoneForm" property="showstyle">
				<logic:empty name="sphoneForm" property="a_code">
					<logic:empty name="sphoneForm" property="sortid">
						<!-- a href="/phone-app/mainpanel.do?br_query=link" data-role="button" data-icon="forward" data-rel="dialog" data-transition="pop" rel="external">返回</a>
						<h1>员工名册</h1> -->
					</logic:empty>
					<logic:notEmpty name="sphoneForm" property="sortid">
						<!-- a href="/phone-app/app/hroster.do?b_query=link&a_code=&dbpre=&sortid=&flag=1" data-role="button" data-icon="forward" data-rel="dialog" data-transition="pop" rel="external">返回</a>
						<h1>${sphoneForm.html }&nbsp;</h1> -->
					</logic:notEmpty>
					<a href="/phone-app/mainpanel.do?br_query=link" data-role="button" data-icon="forward" data-rel="dialog" data-transition="pop" rel="external">返回</a>
					<h1>员工名册</h1>
				</logic:empty>
		</logic:equal>
		 <logic:equal value="2" name="sphoneForm" property="showstyle">
		 	
		 </logic:equal>
	</div>	
	<div data-role="content">
	     <logic:equal value="1" name="sphoneForm" property="showstyle">
	     	<ul data-role="listview" data-inset="true">
	     </logic:equal>
	     <hrms:extenditerate id="element" name="sphoneForm" property="sphoneForm.list" indexes="indexes"  pagination="sphoneForm.pagination" pageCount="10" scope="session">
          	 <logic:equal value="1" name="sphoneForm" property="showstyle">
          	 	<logic:empty name="sphoneForm" property="a_code">
	          	 	<logic:notEmpty name="element" property="sortid">
			          	<li>
			          		<!-- img src="/images/open.png" alt="" class="ui-li-icon"> -->
			          		<a href="javascript:shownext('<bean:write name="element" property="sortid"/>');" onclick=""><bean:write name="element" property="name"/></a>
				        </li>  
			        </logic:notEmpty>
			        <logic:notEmpty name="element" property="tabid">
			        	<li>
			        		<!-- img src="/images/overview_n_obj.gif" alt="" class="ui-li-icon"> -->
			          		<a href="javascript:showpersons('<bean:write name="element" property="tabid"/>');" onclick=""><bean:write name="element" property="name"/></a> <span class="ui-li-count"><bean:write name="element" property="count"/></span>
				        </li> 
			        </logic:notEmpty>
		        </logic:empty>
	         </logic:equal>
	          	
            </hrms:extenditerate>
            <%if(allcount>10){ %>
	            <li>
			            <bean:message key="label.page.serial"/>
						<bean:write name="sphoneForm" property="sphoneForm.pagination.current" filter="true" />
						<bean:message key="label.page.sum"/>
						<bean:write name="sphoneForm" property="sphoneForm.pagination.count" filter="true" />
						<bean:message key="label.page.row"/>
						<bean:write name="sphoneForm" property="sphoneForm.pagination.pages" filter="true" />
						<bean:message key="label.page.page"/>
				</li>				
				
			<%} %>
   		 </ul>
   	</div>
   	 <%if(allcount>10){ %>
   	 		<hrms:paginationdblink name="sphoneForm" property="sphoneForm.pagination" nameId="sphoneForm" scope="session" isMobile="2"></hrms:paginationdblink> 
   	<%} %>
</div>
</html:form>
</body>
</html>