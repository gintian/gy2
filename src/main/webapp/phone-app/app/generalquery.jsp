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

		function test(nbase,a0100,a0101){	
			//alert(nbase+"  "+a0100+" "+a0101);
			var map = new HashMap();
		    map.put("nbase", nbase);
		    map.put("a0100", a0100);
		    map.put("a0101", a0101);
		   　Rpc({functionId:'9101000005',success:searchok},map);	
		}
		
		function searchok(html)
		{
				var value=html;
				var map=JSON.parse(value);
				//alert(value);
				//alert(map.html);
				if(map.succeed)
				{
					//alert(map.html);
				    //$("#scard").empty();
				    $("#s").html(map.html);
					$("#smain").listview({"inset": true});//.listview('refresh');//listview({"inset": true});
					//$.mobile.changePage('smain');	
					$.mobile.changePage($('#smain'));	
				}
		}
		
		function shownext(id){
			sphoneForm.action="/phone-app/app/generalquery.do?b_query=link&a_code="+id;
			sphoneForm.submit();
		}
		
		function showpersons(id){
			sphoneForm.action="/phone-app/app/generalquery.do?b_query=link&dbpre="+id;
			sphoneForm.submit();
		}
		function getCard(nbase,a0100,a0101){
			//alert(nbase+a0100);
			window.location.href="/general/inform/synthesisbrowse/synthesiscard.do?b_search=link&userbase="+nbase+"&a0100="+a0100+"&inforkind=1&tabid=${sphoneForm.cardid}&multi_cards=-1&isMobile=2";
		}
		</script>
	 <script type="text/javascript" src="../jquery/jquery.mobile-1.0a2.min.js"></script>	
</head>
<body>
<input type=hidden id="queryid" />
<html:form action="/phone-app/app/generalquery">
<div data-role="page" id="mainbar"> 
	<div data-role="header"  data-position="fixed" data-position="inline"> 
		<logic:equal value="1" name="sphoneForm" property="showstyle">
		<logic:empty name="sphoneForm" property="a_code">
			<a href="/phone-app/mainpanel.do?br_query=link" data-role="button" data-icon="forward" data-rel="dialog" data-transition="pop" rel="external">返回</a>
			<h1>常用查询</h1>
		</logic:empty>
		<logic:notEmpty name="sphoneForm" property="a_code">
			<a href="/phone-app/app/generalquery.do?b_query=link&a_code=&dbpre=" data-role="button" data-icon="forward" data-rel="dialog" data-transition="pop" rel="external">返回</a>
			<h1>人员类别</h1>
		</logic:notEmpty>
		</logic:equal>
		 <logic:equal value="2" name="sphoneForm" property="showstyle">
		 	<%if("1".equals(dbsize)){ %>
			 	<a href="/phone-app/app/generalquery.do?b_query=link&a_code=&dbpre=" data-role="button" data-icon="forward" data-rel="dialog" data-transition="pop" rel="external">返回</a>
				<h1>${sphoneForm.html }&nbsp;</h1>
			<%}else{ %>
				<a href="/phone-app/app/generalquery.do?b_query=link&dbpre=" data-role="button" data-icon="forward" data-rel="dialog" data-transition="pop" rel="external">返回</a>
				<h1>${sphoneForm.html }&nbsp;</h1>
			<%} %>
		 </logic:equal>
	</div>	
	<div data-role="content">
		 <logic:equal value="2" name="sphoneForm" property="showstyle">
	     	<ul data-role="listview">
	     </logic:equal>
	     <logic:equal value="1" name="sphoneForm" property="showstyle">
	     	<ul data-role="listview" data-inset="true">
	     </logic:equal>
	      
	     <hrms:extenditerate id="element" name="sphoneForm" property="sphoneForm.list" indexes="indexes"  pagination="sphoneForm.pagination" pageCount="10" scope="session">
          	 <logic:equal value="1" name="sphoneForm" property="showstyle">
          	 	<logic:empty name="sphoneForm" property="a_code">
		          	<li>
		          		<a href="javascript:shownext('<bean:write name="element" property="id"/>');" onclick=""><bean:write name="element" property="name"/></a> <span class="ui-li-count"><bean:write name="element" property="count"/></span>
			        </li>  
		        </logic:empty>
		        <logic:notEmpty name="sphoneForm" property="a_code">
		        	<li>
		          		<a href="javascript:showpersons('<bean:write name="element" property="id"/>');" onclick=""><bean:write name="element" property="name"/></a> <span class="ui-li-count"><bean:write name="element" property="count"/></span>
			        </li> 
		        </logic:notEmpty>
	         </logic:equal>
	         <logic:equal value="2" name="sphoneForm" property="showstyle">
	         	<li>
		         		<bean:define id="nbase" name="element" property="nbase" scope="page"></bean:define>
		         		<bean:define id="a0100" name="element" property="a0100" scope="page"></bean:define>
		         		<hrms:ole name="element" dbpre="${nbase }" a0100="a0100" scope="page" width="85" />
		         		<a href="javascript:test('${nbase }','${a0100 }','<bean:write name="element" property="a0101"/>');" onclick="" rel="external"><STRONG><bean:write name="element" property="a0101"/></STRONG></a>
		         		<p><br/><hrms:personMainInfo dbpre="${nbase }" a0100="${a0100 }" /></p>
	         	</li>
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
<span id="s">
<div data-role="page" id="smain" >
	 <div data-role="header"> 
		<a href="#mainbar" data-role="button" data-icon="forward">返回</a>
		<h1>王广言</h1>
	 </div>
	<div data-role="content" id="scard"> 
		<div data-role="collapsible">
			<h3>人员基本信息</h3>
			<img src="/images/aaa.gif" height="60px"  width="85px" border=0 style="position: relative"/>
			<span style="position: relative">男，蒙古族，55岁(1953-10-26出生)，山西省太原市市辖区，[入党 时间]入党，1977-12-26参加工作。</span>
		</div> 
		<div data-role="collapsible" data-collapsed="true">
			<h3>学历子集</h3>
		 	学历：本科<br/>毕业时间：2009-6-30<br/>毕业院校：河北华电
		 	<hr>
		 	学历：本科<br/>毕业时间：2009-6-30<br/>毕业院校：河北金融学院
		</div> 
	</div> 
</div>
</span>
</html:form>
</body>
</html>