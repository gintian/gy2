<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean,com.hjsj.hrms.actionform.smartphone.SPhoneForm"%>
<%@ page import="java.util.ArrayList"%>
<%
	int i=0;
	SPhoneForm sPhoneForm = (SPhoneForm)session.getAttribute("sphoneForm");
	LazyDynaBean bean=sPhoneForm.getNordercountbean();
	int allcount=Integer.parseInt(sPhoneForm.getAllcount());
	//System.out.println(allcount);
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>移动助手</title>
	 <link rel="stylesheet" href="../jquery/css/jquery.mobile-1.0a2.min.css" type="text/css">
	 <script type="text/javascript" src="../jquery/jquery-3.5.1.min.js"></script>
	 <script type="text/javascript" src="../jquery/jquery.mobile-1.0a2.min.js"></script>	
	 <script type="text/javascript" src="../jquery/rpc_command.js"></script>
	 <script type="text/javascript" src="/js/validate.js"></script>
	 <script type="text/javascript" src="/ajax/basic.js"></script>
	  <script type="text/javascript">
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
					//$.mobile.changePage('smain');	
					$.mobile.changePage($('#smain'));		
				}
		}
		function getCard(nbase,a0100,a0101){
			//alert(nbase+a0100);
			window.location.href="/general/inform/synthesisbrowse/synthesiscard.do?b_search=link&userbase="+nbase+"&a0100="+a0100+"&inforkind=1&tabid=${sphoneForm.cardid}&multi_cards=-1&isMobile=2";
		}
		</script>	 
</head>
<body>
<div data-role="page" id="mainbar"> 
<html:form action="/phone-app/app/statlist">
	<div data-role="header"> 
	    <logic:equal value="0" name="sphoneForm" property="showstyle">
		  <a href="/phone-app/mainpanel.do?br_query=link" data-role="button" data-icon="forward" data-rel="dialog" data-transition="pop" rel="external">返回</a>
		   <h1>统计分析</h1>
		</logic:equal>
		<logic:equal value="1" name="sphoneForm" property="showstyle">
		  <logic:equal value="true" name="sphoneForm" property="statlabel">
		     <a href="/phone-app/app/statlist.do?b_query=link&statid=&showstyle=0" data-role="button" data-icon="forward" data-rel="dialog" data-transition="pop" rel="external">返回</a>
		  </logic:equal>
		  <logic:notEqual value="true" name="sphoneForm" property="statlabel">
		     <a href="/phone-app/mainpanel.do?br_query=link" data-role="button" data-icon="forward" data-rel="dialog" data-transition="pop" rel="external">返回</a>
		  </logic:notEqual>
		   <h1>统计分析</h1>
		</logic:equal>	
		<logic:equal value="2" name="sphoneForm" property="showstyle">
		  <logic:equal value="home" name="sphoneForm" property="returnvalue">
		     <a href="/phone-app/app/statlist.do?b_query=link&statid=&showstyle=0" data-role="button" data-icon="forward" data-rel="dialog" data-transition="pop" rel="external">返回</a>
		  </logic:equal>
		  <logic:notEqual value="home" name="sphoneForm" property="returnvalue">
		     <a href="/phone-app/app/statlist.do?b_query=link&statid=&showstyle=1" data-role="button" data-icon="forward" data-rel="dialog" data-transition="pop" rel="external">返回</a>
		  </logic:notEqual>
		  <h1><bean:write name="sphoneForm" property="snamedisplay"/></h1>
		</logic:equal>	
		<logic:equal value="3" name="sphoneForm" property="showstyle">
		  <a href="/phone-app/app/statlist.do?b_query=link&statid=${sphoneForm.statid}&showstyle=2" data-role="button" data-icon="forward" data-rel="dialog" data-transition="pop" rel="external">返回</a>
		   <h1><bean:write name="sphoneForm" property="snamedisplay"/></h1>
		</logic:equal>	
		<logic:equal value="2" name="sphoneForm" property="showstyle">
		  <a href="javascript:showset();" data-role="button" data-icon="star" >选项</a>		
		</logic:equal>
		<logic:equal value="3" name="sphoneForm" property="showstyle">
		  
		</logic:equal>
	</div>	
	<div data-role="content">
	   <logic:equal value="0" name="sphoneForm" property="showstyle">
	     <ul data-role="listview" data-split-icon="gear" data-split-theme="d"> 
	     	<!-- <li><img src="images/gf.png" alt="France" class="ui-li-icon"><a href="index.html">France</a> <span class="ui-li-count">4</span></li> -->
   	   		 <hrms:paginationdb id="element" name="sphoneForm" sql_str="sphoneForm.strsql" table="" where_str="sphoneForm.strwhere" columns="sphoneForm.columns" order_by="sphoneForm.order" page_id="pagination" pagerows="50" distinct="" keys="">
          	
          	  <li>          		
          		<img src="/images/static.gif" class="ui-li-icon">
          		<logic:equal value="-1" name="element" property="codeitemid">          		
          		 <h3 style="margin-left:20px;text-valign:center;"><a href="javascript:showstat('<bean:write name="element" property="codeitemid"/>','<bean:write name="element" property="codeitemdesc"/>');"><bean:write name="element" property="codeitemdesc"/></a></h3> 				
			    </logic:equal>
			   <logic:notEqual value="-1" name="element" property="codeitemid"> 
			     <h3 style="margin-left:20px;text-valign:center;"><a href="javascript:showchart('<bean:write name="element" property="codeitemid"/>');"><bean:write name="element" property="codeitemdesc"/></a></h3> 				
				 <a href="javascript:showstat('<bean:write name="element" property="codeitemid"/>','<bean:write name="element" property="codeitemdesc"/>');"></a>    
			   </logic:notEqual>   
			 </li>  
            </hrms:paginationdb>
         </ul>
       </logic:equal>
	   <logic:equal value="1" name="sphoneForm" property="showstyle">
	     <ul data-role="listview" data-split-icon="gear" data-split-theme="d"> 
	     	<!-- <li><img src="images/gf.png" alt="France" class="ui-li-icon"><a href="index.html">France</a> <span class="ui-li-count">4</span></li> -->
   	   		 <hrms:paginationdb id="element" name="sphoneForm" sql_str="sphoneForm.strsql" table="" where_str="sphoneForm.strwhere" columns="sphoneForm.columns" order_by="sphoneForm.order" page_id="pagination" pagerows="50" distinct="" keys="">
          	
          	  <li>          		
          		<img src="/images/static.gif" class="ui-li-icon">
          		<h3 style="margin-left:20px;text-valign:center;"><a href="javascript:showchart('<bean:write name="element" property="id"/>');""><bean:write name="element" property="name"/></a></h3> 				
				<a href="javascript:showlegend('<bean:write name="element" property="id"/>');"></a>          		
          	 </li>  
            </hrms:paginationdb>
         </ul>
       </logic:equal>
       <logic:equal value="2" name="sphoneForm" property="showstyle">
	     <ul data-role="listview"> 
	     	<!-- <li><img src="images/gf.png" alt="France" class="ui-li-icon"><a href="index.html">France</a> <span class="ui-li-count">4</span></li> -->
   	   		 <hrms:paginationdb id="element" name="sphoneForm" sql_str="sphoneForm.strsql" table="" where_str="sphoneForm.strwhere" columns="sphoneForm.columns" order_by="sphoneForm.order" page_id="pagination" pagerows="50" distinct="" keys="">
          	  <%
          	     LazyDynaBean onebean=(LazyDynaBean)  pageContext.getAttribute("element");
          	     String norder=(String)onebean.get("norder");
          	     String count=(String)bean.get(norder);
          	     
          	   %>
          	  <li>          		
          		<img src="/images/view.gif" alt="France" class="ui-li-icon">          					
				<a href="javascript:shownext('<bean:write name="element" property="norder"/>');"><bean:write name="element" property="legend"/></a> <span class="ui-li-count"><%=count %></span>         		
          	 </li>  
            </hrms:paginationdb>
         </ul>
       </logic:equal>
        <logic:equal value="3" name="sphoneForm" property="showstyle">
	     <ul data-role="listview"> 
	     	<!-- <li><img src="images/gf.png" alt="France" class="ui-li-icon"><a href="index.html">France</a> <span class="ui-li-count">4</span></li> -->
   	   		 <hrms:paginationdb id="element" name="sphoneForm" sql_str="sphoneForm.strsql" table="" where_str="sphoneForm.strwhere" columns="sphoneForm.columns" order_by="sphoneForm.order" page_id="pagination" pagerows="10" distinct="" keys="">
          	  
          	  <li>          		
          		   
		           <bean:define id="a0100" name="element" property="a0100" scope="page"></bean:define>
		           <bean:define id="a0101" name="element" property="a0101" scope="page"></bean:define>
		           <hrms:ole name="element" dbpre="${sphoneForm.nbase}" a0100="a0100" scope="page" width="85" />      
		           <a href="javascript:test('${sphoneForm.nbase}','${a0100}','${a0101}');" onclick="" rel="external"><STRONG><bean:write name="element" property="a0101"/></STRONG></a>
		           <p><br/><hrms:personMainInfo dbpre="${sphoneForm.nbase}" a0100="${a0100}" /></p> 		
          	 </li>  
            </hrms:paginationdb>
         </ul>
       </logic:equal>
   	</div>
   	<%if(allcount>10){ %>	
   	 			<hrms:paginationdblink name="sphoneForm" property="pagination" nameId="sphoneForm" scope="page" isMobile="1"></hrms:paginationdblink>   
   	<%} %>
   	<!-- div data-role="footer"> 
		<h4>hjsoft</h4> 
	</div> -->
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
		 	学历：本科<br/>毕业时间：2009-6-30<br/>毕业院校：河北华电
		 	<hr>
		 	学历：本科<br/>毕业时间：2009-6-30<br/>毕业院校：河北金融学院
		</div>
		</div> 
	</div> 
</div>
</span>
<script type="text/javascript">
  function showstat(id,categories)
  {
      if(id!=-1)
      {
         sphoneForm.action="/phone-app/app/statlist.do?b_query=link&statid="+id+"&showstyle=2&categories=&returnvalue=home"; 
	     sphoneForm.submit(); 
      }
      else
      {
           categories=getEncodeStr(categories);
           sphoneForm.action="/phone-app/app/statlist.do?b_query=link&statid="+id+"&showstyle=1&categories="+categories;     
	       sphoneForm.submit(); 
      }
  }
  function showlegend(id)
  {
     sphoneForm.action="/phone-app/app/statlist.do?b_query=link&statid="+id+"&showstyle=2";     
	 sphoneForm.submit(); 
  }
  function showchart(id)
  {
    
     sphoneForm.action="/phone-app/app/statchart.do?b_query=link&statid="+id;     
	 sphoneForm.submit(); 
  }
  function shownext(norder){
     sphoneForm.action="/phone-app/app/statlist.do?b_query=link&statid=${sphoneForm.statid}&norder="+norder+"&showstyle=3";     
	 sphoneForm.submit(); 
  }
  function showset()
  {
     sphoneForm.action="/phone-app/app/statchart.do?b_set=link&returnvalue=list";
	 sphoneForm.submit(); 
  }
</script>
</html:form>

</body>
</html>
