<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>


<%
	String objectid=request.getParameter("objectid");
	String template_id=request.getParameter("template_id");
	String sub_page=request.getParameter("sub_page");

	String url1="/performance/implement/performanceImplement.do?b_mainbody=query&objectid="+objectid+"&template_id="+template_id+"&opt=1";
	String url2="/performance/implement/performanceImplement.do?b_mainbody=query&objectid="+objectid+"&template_id="+template_id+"&opt=2";
	String url3="/performance/implement/performanceImplement.do?b_mainbody=query&objectid="+objectid+"&template_id="+template_id+"&opt=3";
	String url4="/performance/implement/performanceImplement.do?b_mainbody=query&objectid="+objectid+"&template_id="+template_id+"&opt=4";
 %>
<body onload='setPage(<%=sub_page%>)' onResize="refreshPage()">

	<html:form action="/performance/implement/performanceImplement">

		<hrms:tabset name="cardset" width="100%" height="100%" type="true">
		
		  	<hrms:tab name="aa1" label="lable.performance.perMainBody" visible="true" url="<%=url1%>" > </hrms:tab><!-- 考核主体 -->
		  		
			<logic:equal name="implementForm" property="method"  value="1">
				<logic:equal name="implementForm" property="busitype"  value="0">
		 			<hrms:tab name="aa2" label="lable.performance.targetPurview" visible="true" url="<%=url2%>" > </hrms:tab><!-- 指标权限 -->
		 		</logic:equal> 	
			</logic:equal>  
			  
	      	<logic:equal name="implementForm" property="method"  value="2">
	      		<logic:equal name="implementForm"  property="isDistribute"  value="1">
	        		<hrms:tab name="aa3" label="khrelation.standard" visible="true" url="<%=url3%>" > </hrms:tab>	<!-- 标准考核关系 -->
	       	 	</logic:equal>
	   	 	</logic:equal>
	   	 	
	   	 	<logic:equal name="implementForm" property="method"  value="2">
			 	<hrms:tab name="aa4" label="performance.item.priv" visible="true" url="<%=url4%>" >   </hrms:tab>	<!-- 项目权限 -->
			</logic:equal> 
			 
		</hrms:tabset>
	
	
	<script langugage='javascript' >
    /**
     * 兼容IE下tab页签不能自动适应高度 haosl
     */
	function refreshPage(){
	    var tableset = $('#_tabsetpane_cardset');
	    if(tableset && tableset.tabs){
            var tab = tableset.tabs('getSelected');
            var index = tableset.tabs('getTabIndex',tab);
            var height = document.body.clientHeight-20;
            tableset.tabs({
                height : height
            }).tabs('resize');
            tableset.tabs("select",index);
        }

    }
	function setPage(page)
	{
		if(page==2)
		{
			var obj=$('cardset');
			obj.setSelectedTab("aa2");
		}
		if(page==3)
		{
			var obj=$('cardset');
			obj.setSelectedTab("aa3");
		}
		if(page==4)
		{
			var obj=$('cardset');
			obj.setSelectedTab("aa4");
		}
	}

	</script>
	
</html:form>

</body>