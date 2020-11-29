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
	//System.out.println(allcount);
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>移动助手</title>
	 <link rel="stylesheet" href="../jquery/css/jquery.mobile-1.0a2.min.css" type="text/css">
	 <script type="text/javascript" src="../jquery/jquery-3.5.1.min.js"></script>
	 <script type="text/javascript" src="../jquery/rpc_command.js"></script>
	 <script type="text/javascript">
		function show()
		{
			$("#spnl").show();
		}

		function test(nbase,a0100,a0101){	
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
					//$.mobile.pageLoading(true);
					//$.mobile.activePage();
					$.mobile.changePage($('#smain'));
					//$.mobile.changePage('smain');		
				}
		}
		
		function shownext(a_code){
			//alert(a_code);
			sphoneForm.action="/phone-app/app/myteam.do?b_query=link&a_code="+a_code;
			sphoneForm.submit(); 
		}
		
		function getQueryPage(){
			var map = new HashMap();
		   　Rpc({functionId:'9101000006',success:querypageok},map);	
			function querypageok(html){
				var value=html;
				var map=JSON.parse(value);
				//alert(value);
				//alert(map.html);
				if(map.succeed)
				{
					//alert(map.html);
				    //$("#scard").empty();
				    $("#queryfield").html(map.html);
					$("#query").listview({"inset": true});//.listview('refresh');//listview({"inset": true});
					//$.mobile.changePage('query');
					$.mobile.changePage($('#query'));		
				}
			}
		}
		function searchCodeItem(itemid,value){
			//alert("searchCodeItem");
			var map = new HashMap();
			map.put("type","codesetid");
			map.put("value",value);
			map.put("itemid",itemid);
			//alert(value+"  "+itemid);
		   　Rpc({functionId:'9101000007',success:showcodeitemok},map);	
			function showcodeitemok(html){
				var value=html;
				var map=JSON.parse(value);
				if(map.succeed)
				{
					//alert(map.html);
				    //$("#scard").empty();
				    $("#codeitemspan").html(map.html);
					//$("#codeitempage").listview({"inset": true});//.listview('refresh');//listview({"inset": true});
					//$.mobile.changePage('codeitempage');	
					$.mobile.changePage($('#codeitempage'));	
				}
			}	
		}
		function getChildCode(itemid,codeitemid,pageid){
			var map = new HashMap();
			map.put("type","codeitemid");
			map.put("value",codeitemid);
			map.put("itemid",itemid);
			map.put("pageid",pageid);
		   　Rpc({functionId:'9101000007',success:showchildcodeitemok},map);	
			function showchildcodeitemok(html){
				var value=html;
				var map=JSON.parse(value);
				if(map.succeed)
				{
					//alert(map.html);
					if('childcodeitempage'==pageid){
				    	$("#childcodeitemspan").html(map.html);
						//$("#childcodeitempage").listview({"inset": true});//.listview('refresh');//listview({"inset": true});
						//$.mobile.changePage('childcodeitempage');		//,'slideup'
						$.mobile.changePage($('#childcodeitempage'));
					}else{
						$("#childcodeitemspan1").html(map.html);
						//$.mobile.changePage('childcodeitempage1');
						$.mobile.changePage($('#childcodeitempage1'));
					}
				}
			}	
		}
		function getParentCode(itemid,codeitemid,pageid){
			var backflag=document.getElementById('backflag').value;
			if(backflag>codeitemid.length){
				//$.mobile.changePage('codeitempage');
				$.mobile.changePage($('#codeitempage'));
			}else{
				var map = new HashMap();
				map.put("type","back");
				map.put("value",codeitemid);
				map.put("itemid",itemid);
				map.put("pageid",pageid);
			   　Rpc({functionId:'9101000007',success:showparentcodeitemok},map);	
				function showparentcodeitemok(html){
					var value=html;
					var map=JSON.parse(value);
					if(map.succeed)
					{
						if('childcodeitempage'==pageid){
					    	$("#childcodeitemspan").html(map.html);
							//$("#childcodeitempage").listview({"inset": true});//.listview('refresh');//listview({"inset": true});
							//$.mobile.changePage('childcodeitempage');		//,'slideup'
							$.mobile.changePage($('#childcodeitempage'));
						}else{
							$("#childcodeitemspan1").html(map.html);
							//$.mobile.changePage('childcodeitempage1');
							$.mobile.changePage($('#childcodeitempage1'));
						}
					}
				}	
			}
		}
		function setCodevalue(itemid,codeitemid,codeitemdesc){
			document.getElementById('q'+itemid).value=codeitemid;
			document.getElementById('q'+itemid+'view').value=codeitemdesc;
			//$.mobile.changePage('query');
			$.mobile.changePage($('#query'));
		}
		
		function queryP(){
			var selectField="${sphoneForm.selectField }";
			var fieldtype=document.getElementById('fieldtype').value;
			var selectFields=selectField.split(",");
			var fieldtypes=fieldtype.split(",");
			var queryValue="";
			//alert(selectFields+"  "+fieldtypes);
			if(selectFields.length==fieldtypes.length){
				for(var i=0;i<selectFields.length;i++){
					var itemid=selectFields[i];
					var itemtype=fieldtypes[i];
					var v1="";
					var v2="";
					if("A"==itemtype){
						v1=document.getElementById("q"+itemid).value;
						queryValue+=":q"+v1;
					}else if("N"==itemtype){
						v1=document.getElementById("q"+itemid).value;
						v2=document.getElementsByName("q"+itemid+"c");
						for(n=0;n<v2.length;n++){
							if(v2[n].checked){
								queryValue+=":q"+v1+","+v2[n].value;
								break;
							}
						}
					}else if("D"==itemtype){
						v1=document.getElementById("q"+itemid+"star").value;
						v2=document.getElementById("q"+itemid+"end").value;
						queryValue+=":q"+v1+","+v2;
					}
				}
				//alert(queryValue);
				document.getElementById("queryValueid").value=queryValue.substring(1);
				sphoneForm.action="/phone-app/app/myteam.do?b_search=link";
				sphoneForm.submit();
			}
		}
		function getCard(nbase,a0100,a0101){
			//alert(nbase+a0100);
			window.location.href="/general/inform/synthesisbrowse/synthesiscard.do?b_search=link&userbase="+nbase+"&a0100="+a0100+"&inforkind=1&tabid=${sphoneForm.cardid}&multi_cards=-1&isMobile=2";
		}
	 </script>
	 <script type="text/javascript" src="../jquery/jquery.mobile-1.0a2.min.js"></script>	
</head>
<body>
<html:form action="/phone-app/app/myteam">
<html:hidden name="sphoneForm" property="queryValue" styleId="queryValueid"/>
<div data-role="page" id="mainbar"> 
	<div data-role="header"  data-position="fixed" data-position="inline"> 
		<logic:empty name="sphoneForm" property="a_code">
			<a href="/phone-app/mainpanel.do?br_query=link" data-role="button" data-icon="forward" data-rel="dialog" data-transition="pop" rel="external">返回</a>
			<h1>我的团队</h1>
		</logic:empty>
		<logic:notEmpty name="sphoneForm" property="a_code">
			<a href="/phone-app/app/myteam.do?b_query=link&a_code=${sphoneForm.p_a_code }" data-role="button" data-icon="forward" data-rel="dialog" data-transition="pop" rel="external">返回</a>
			<h1>&nbsp;<bean:write name="sphoneForm" property="p_codeitemdesc"/></h1>
		</logic:notEmpty>
		<logic:equal value="True" name="sphoneForm" property="canQuery">
			<a href="javascript:getQueryPage();" data-role="button" data-icon="star" >查询</a>
		</logic:equal>		
	</div>	
	<div data-role="content">
	     <ul data-role="listview"> 
	     	<!-- <li><img src="images/gf.png" alt="France" class="ui-li-icon"><a href="index.html">France</a> <span class="ui-li-count">4</span></li> -->
   	   		<hrms:paginationdb id="element" name="sphoneForm" sql_str="sphoneForm.strsql" table="" where_str="sphoneForm.strwhere" columns="sphoneForm.columns" order_by="" page_id="pagination" pagerows="10" distinct="" keys="">
          	<logic:equal value="1" name="sphoneForm" property="showstyle">
          	<li>
          		<logic:equal value="org" name="element" property="flag">
          			<logic:equal value="UN" name="element" property="codesetid">
          				<img src="/images/unit.gif" alt="<bean:write name="element" property="codeitemdesc"/>" class="ui-li-icon">
          			</logic:equal>
          			<logic:equal value="UM" name="element" property="codesetid">
          				<img src="/images/dept.gif" alt="<bean:write name="element" property="codeitemdesc"/>" class="ui-li-icon">
          			</logic:equal>
          			<logic:equal value="@K" name="element" property="codesetid">
          				<img src="/images/pos_l.gif" alt="<bean:write name="element" property="codeitemdesc"/>" class="ui-li-icon">
          			</logic:equal>
          			<a href="javascript:shownext('<bean:write name="element" property="codesetid"/><bean:write name="element" property="codeitemid"/>');" onclick=""><bean:write name="element" property="codeitemdesc"/></a> <span class="ui-li-count"><bean:write name="element" property="count"/></span>
          		</logic:equal>  
          		<logic:equal value="vorg" name="element" property="flag">
          			<logic:equal value="UN" name="element" property="codesetid">
          				<img src="/images/vroot.gif" alt="<bean:write name="element" property="codeitemdesc"/>" class="ui-li-icon">
          			</logic:equal>
          			<logic:equal value="UM" name="element" property="codesetid">
          				<img src="/images/vdept.gif" alt="<bean:write name="element" property="codeitemdesc"/>" class="ui-li-icon">
          			</logic:equal>
          			<logic:equal value="@K" name="element" property="codesetid">
          				<img src="/images/vpos_l.gif" alt="<bean:write name="element" property="codeitemdesc"/>" class="ui-li-icon">
          			</logic:equal>
          			<a href="javascript:shownext('<bean:write name="element" property="codesetid"/><bean:write name="element" property="codeitemid"/>');" onclick=""><bean:write name="element" property="codeitemdesc"/></a> <span class="ui-li-count"><bean:write name="element" property="count"/></span>
          		</logic:equal>  
          		<logic:equal value="per" name="element" property="flag">
          			<img src="/images/img_b.gif" alt="<bean:write name="element" property="codeitemdesc"/>" class="ui-li-icon"><!-- img_b.gif -->
          			<bean:define id="nbase" name="element" property="codesetid" scope="page"></bean:define>
		         	<bean:define id="a0100" name="element" property="codeitemid" scope="page"></bean:define>
					<a href="javascript:test('${nbase }','${a0100 }','<bean:write name="element" property="codeitemdesc"/>');" onclick="" rel="external"><bean:write name="element" property="codeitemdesc"/></a>
          		</logic:equal>           
	         </li>  
	         </logic:equal>
	         <logic:equal value="2" name="sphoneForm" property="showstyle">
	         	<li>
	         		<logic:equal value="per" name="element" property="flag">
		         		<bean:define id="nbase" name="element" property="codesetid" scope="page"></bean:define>
		         		<bean:define id="a0100" name="element" property="codeitemid" scope="page"></bean:define>
		         		<hrms:ole name="element" dbpre="${nbase }" a0100="codeitemid" scope="page" width="85" />
		         		<a href="javascript:test('${nbase }','${a0100}','<bean:write name="element" property="codeitemdesc"/>');" onclick="" rel="external"><STRONG><bean:write name="element" property="codeitemdesc"/></STRONG></a>
		         		<p><br/><hrms:personMainInfo dbpre="${nbase }" a0100="${a0100 }" /></p>
		         	</logic:equal>
	         	</li>
	         </logic:equal>   	
            </hrms:paginationdb>
            <%if(allcount>10){ %>
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
   	 <%if(allcount>10){ %>	
   	 			<hrms:paginationdblink name="sphoneForm" property="pagination" nameId="sphoneForm" scope="page" isMobile="1"></hrms:paginationdblink>   		 
		 		<!-- div data-role="footer">
					<a style="cursor: " href="javascript:void(0);" data-role="button" data-icon="arrow-l" title="上一页">&nbsp;</a> 
					<a href="index.html" data-role="button" data-icon="arrow-r" title="下一页">&nbsp;</a>
					&nbsp;&nbsp;&nbsp;
					<input name="slider" id="slider" value="0" class="ui-input-text ui-body-null ui-corner-all ui-shadow-inset ui-body-c ui-slider-input">
					<a href="javascript:gopage();" data-role="button" data-icon="refresh" title="跳转">&nbsp;</a>
					<a href="index.html" data-role="button" data-icon="arrow-u" title="回到页头">Up</a>
				</div> -->
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
			<p>
			<img src="/images/aaa.gif" width="85px" border=0 style="position:relative;float: left;"/>
			</p>
			男，蒙古族，55岁(1953-10-26出生)，山西省太原市市辖区，[入党时间]入党，1977-12-26参加工作。
		</div> 
		<div>
		<div data-role="collapsible" data-collapsed="true">
			<h3>学历子集</h3>
			<table border="0">
				<tr>
					<td align="right" nowrap="nowrap"></td><td align="left"></td>
				</tr>
			</table>
		 	学历：本科<br/>毕业时间：2009-6-30<br/>毕业院校：河北华电
		 	<hr>
		 	学历：本科<br/>毕业时间：2009-6-30<br/>毕业院校：河北金融学院
		</div>
		</div> 
	</div> 
</div>
</span>
<span id="queryfield">

</span>
<span id="codeitemspan">
	<div data-role="page" id="codeitempage" data-inset="true"> 
		<div data-role="header">
			<a href="#query" data-role="button" data-icon="forward">返回</a>
			<h1>&nbsp;</h1>
		</div>
		<div data-role="content"> 
			<ul data-role="listview"> 
					<li> 
						<img src="/images/icon_wsx.gif"  class="ui-li-icon"/> 
						<a href="#query">Broken Bells</a>
						<a href="index.html">Broken Bells</a>  
					</li> 
					<li> 
						<img src="/images/icon_wsx.gif"  class="ui-li-icon"/> 
						<a href="#query">Warning</a>
					</li> 
			</ul> 
		</div>	
	</div>
</span>
<span id="childcodeitemspan">
	<div data-role="page" id="childcodeitempage"  data-inset="true"><div data-role="header"><a href="javascript:" data-role="button" data-icon="forward">返回</a><h1>北京市市辖区&nbsp;</h1></div><div data-role="content"><ul data-role="listview"><li><img src="/images/icon_wsx.gif"  class="ui-li-icon"/><a href="#query">北京市东城区</a></li><li><img src="/images/icon_wsx.gif"  class="ui-li-icon"/><a href="#query">北京市西城区</a></li><li><img src="/images/icon_wsx.gif"  class="ui-li-icon"/><a href="#query">北京市崇文区</a></li><li><img src="/images/icon_wsx.gif"  class="ui-li-icon"/><a href="#query">北京市宣武区</a></li><li><img src="/images/icon_wsx.gif"  class="ui-li-icon"/><a href="#query">北京市朝阳区</a></li><li><img src="/images/icon_wsx.gif"  class="ui-li-icon"/><a href="#query">北京市丰台区</a></li><li><img src="/images/icon_wsx.gif"  class="ui-li-icon"/><a href="#query">北京市石景山区</a></li><li><img src="/images/icon_wsx.gif"  class="ui-li-icon"/><a href="#query">北京市海淀区</a></li><li><img src="/images/icon_wsx.gif"  class="ui-li-icon"/><a href="#query">北京市门头沟区</a></li><li><img src="/images/icon_wsx.gif"  class="ui-li-icon"/><a href="#query">北京市房山区</a></li><li><img src="/images/icon_wsx.gif"  class="ui-li-icon"/><a href="#query">北京市通州区</a></li><li><img src="/images/icon_wsx.gif"  class="ui-li-icon"/><a href="#query">北京市顺义区</a></li><li><img src="/images/icon_wsx.gif"  class="ui-li-icon"/><a href="#query">北京市昌平区</a></li><li><img src="/images/icon_wsx.gif"  class="ui-li-icon"/><a href="#query">北京市大兴区</a></li><li><img src="/images/icon_wsx.gif"  class="ui-li-icon"/><a href="#query">北京市怀柔区</a></li><li><img src="/images/icon_wsx.gif"  class="ui-li-icon"/><a href="#query">北京市平谷区</a></li></ul></div></div>
</span>
<span id="childcodeitemspan1">
	<div data-role="page" id="childcodeitempage1"  data-inset="true"><div data-role="header"><a href="javascript:" data-role="button" data-icon="forward">返回</a><h1>北京市市辖区&nbsp;</h1></div><div data-role="content"><ul data-role="listview"><li><img src="/images/icon_wsx.gif"  class="ui-li-icon"/><a href="#query">北京市东城区</a></li><li><img src="/images/icon_wsx.gif"  class="ui-li-icon"/><a href="#query">北京市西城区</a></li><li><img src="/images/icon_wsx.gif"  class="ui-li-icon"/><a href="#query">北京市崇文区</a></li><li><img src="/images/icon_wsx.gif"  class="ui-li-icon"/><a href="#query">北京市宣武区</a></li><li><img src="/images/icon_wsx.gif"  class="ui-li-icon"/><a href="#query">北京市朝阳区</a></li><li><img src="/images/icon_wsx.gif"  class="ui-li-icon"/><a href="#query">北京市丰台区</a></li><li><img src="/images/icon_wsx.gif"  class="ui-li-icon"/><a href="#query">北京市石景山区</a></li><li><img src="/images/icon_wsx.gif"  class="ui-li-icon"/><a href="#query">北京市海淀区</a></li><li><img src="/images/icon_wsx.gif"  class="ui-li-icon"/><a href="#query">北京市门头沟区</a></li><li><img src="/images/icon_wsx.gif"  class="ui-li-icon"/><a href="#query">北京市房山区</a></li><li><img src="/images/icon_wsx.gif"  class="ui-li-icon"/><a href="#query">北京市通州区</a></li><li><img src="/images/icon_wsx.gif"  class="ui-li-icon"/><a href="#query">北京市顺义区</a></li><li><img src="/images/icon_wsx.gif"  class="ui-li-icon"/><a href="#query">北京市昌平区</a></li><li><img src="/images/icon_wsx.gif"  class="ui-li-icon"/><a href="#query">北京市大兴区</a></li><li><img src="/images/icon_wsx.gif"  class="ui-li-icon"/><a href="#query">北京市怀柔区</a></li><li><img src="/images/icon_wsx.gif"  class="ui-li-icon"/><a href="#query">北京市平谷区</a></li></ul></div></div>
</span>
</html:form>
</body>
</html>