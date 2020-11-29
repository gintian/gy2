<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean,com.hjsj.hrms.actionform.smartphone.SPhoneForm"%>
<%
	int i=0;
	SPhoneForm sPhoneForm = (SPhoneForm)session.getAttribute("sphoneForm");
	int allcount=Integer.parseInt(sPhoneForm.getAllcount());
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>移动助手</title>

	 <link rel="stylesheet" href="../jquery/css/jquery.mobile-1.0a2.min.css" type="text/css">
	 <script type="text/javascript" src="../jquery/jquery-3.5.1.min.js"></script>
	 <script type="text/javascript" src="../jquery/jquery.mobile-1.0a2.min.js"></script>	
	 <script type="text/javascript" src="../jquery/rpc_command.js"></script>	 
</head>
<body>
<html:form action="/phone-app/app/contacts">
<div data-role="page" id="mainbar">	<!--  data-fullscreen="true" -->
	<div data-role="header" data-position="fixed" data-position="inline">
		<a href="/phone-app/mainpanel.do?br_query=link" data-role="button" data-icon="forward" data-rel="dialog" data-transition="pop" rel="external">返回</a>
		<h1>企业通讯录</h1>
		<a href="javascript:show()" data-role="button" data-icon="star" >查询</a>		
	</div>	
	<div data-role="content"> <!-- style="margin-top: 40px"  -->
		<div data-role="fieldcontain" id="spnl" style="display: none">
			<input type="search" name="queryitem" id="search" value="" onchange="query();"/>
		</div>	
	     <ul data-role="listview" data-split-icon="gear" data-split-theme="d">
   	   		<hrms:paginationdb id="element" name="sphoneForm" sql_str="sphoneForm.strsql" table="" where_str="sphoneForm.strwhere" columns="sphoneForm.columns" order_by=" order by a0000" page_id="pagination" pagerows="15" distinct="" keys="">
          <%
             	LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
             	String a0100=(String)abean.get("a0100"); 
                String nbase=(String)abean.get("nbase");              	
                request.setAttribute("name",a0100); 
                request.setAttribute("nbase",nbase);    	                           
          %> 	            
	            <li>
					<hrms:ole name="element" dbpre="${nbase}" a0100="a0100" scope="page" width="85" />				    
	                <h3><a href="tel:<bean:write name="element" property="phone" filter="true"/>"><bean:write name="element" property="a0101" filter="true"/></a></h3>
	                <p>移动电话：<bean:write name="element" property="phone" filter="true"/></p>
	                <a href="javascript:searchbya0100('<bean:write name="element" property="nbase" filter="true"/>','<bean:write name="element" property="a0100" filter="true"/>')" style="width: 50px;"></a>                
	            </li>              	
            </hrms:paginationdb>
            <%if(allcount>15){ %>
			<li>
		            <bean:message key="label.page.serial"/>
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page"/>   				
			</li>
			<%} %>
   		 </ul>
	</div>
	<%if(allcount>15){ %>
		<hrms:paginationdblink name="sphoneForm" property="pagination" nameId="sphoneForm" scope="page" isMobile="1"></hrms:paginationdblink>   		 
	<%} %>
</div>
<div data-role="page" data-fullscreen="true" id="smain">
	<div data-role="header" data-position="fixed" data-position="inline">
		<a href="#mainbar" data-role="button" data-icon="forward">返回</a>
		<h1>&nbsp;</h1>
	</div>	
	<div data-role="content" style="margin-top: 40px" id="scard">
		<ul data-role="listview" data-inset="true"><li style="height:60px"><img src="/images/1.jpg"/></li><li><h3>部门:</h3></li><li><h3>姓名:张普发</h3></li><li><h3>电话:13611273634</h3><a href="tel:13611273634"></a></li><li><h3>邮箱:bjyczj@263.net13611273634</h3><a href="mailto:bjyczj@263.net"></a></li></ul>	
	</div>
</div>


<script type="text/javascript">
function show()
{
	$("#spnl").show();
}

/**按姓名查询*/
function query()
{
	sphoneForm.action="/phone-app/app/contacts.do?b_query=link";
	sphoneForm.submit();  
}

function searchok(html)
{
		var value=html;
		var map=JSON.parse(value);
		if(map.succeed)
		{
			//alert(map.html);
		    //$("#scard").empty();
		    $("#scard").html(map.html);
		    $("#myul").listview({"inset": true});
			//$.mobile.changePage('smain');	
			$.mobile.changePage($('#smain'));		
		}
}

function searchbya0100(nbase,a0100)
{

    var map = new HashMap();
    map.put("nbase", nbase);
    map.put("a0100", a0100);
   　Rpc({functionId:'9101000002',success:searchok},map);  
}

</script>
</html:form>
</body>
</html>