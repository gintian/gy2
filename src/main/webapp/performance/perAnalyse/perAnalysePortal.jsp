<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.businessobject.performance.PerformanceAnalyseBo"%>
<%@ page import="com.hrms.frame.utility.AdminDb"%>
<%@ page import="java.sql.Connection,java.util.*"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<body onresize="refreshPage()">
<style type="text/css">
    #_tabsetpane_cardset{
        margin-left:5px;
    }

</style>
<%
	String busitype = "0"; // 业务分类字段 =0(绩效考核); =1(能力素质)
	if(request.getParameter("busitype")!=null)
		busitype=request.getParameter("busitype");

	String fromModule = "analyse";
	if(request.getParameter("fromModule")!=null)
		fromModule=request.getParameter("fromModule");		
		
	if(request.getParameter("br_query")!=null&&request.getParameter("br_query").equals("link"))
	{
		String plan_id="";
		if(request.getParameter("plan_id")!=null&&request.getParameter("plan_id").length()>0)
			plan_id=request.getParameter("plan_id");
		%>
		<script language='javascript' >				
			document.location="/performance/perAnalyse.do?br_query=query&busitype=<%=busitype%>&plan_id=<%=plan_id%>&fromModule=<%=fromModule%>&width="+(document.body.clientWidth-160)+"&height="+(document.body.clientHeight);
				
		</script>
		<% 
	}
	else
	{
		String height=request.getParameter("height");
		String width=request.getParameter("width");width=Integer.toString(Integer.parseInt(width)-100);
		String plan_id=request.getParameter("plan_id");
		String url="/performance/perAnalyse.do?b_singlePointAnalyse0=query0&busitype="+busitype+"&fromModule="+fromModule+"&chartWidth="+width+"&chartHeight="+(Integer.parseInt(request.getParameter("height"))-80);
		String url2="/performance/perAnalyse.do?b_multiplePointAnalyse0=query0&busitype="+busitype+"&fromModule="+fromModule+"&chartWidth="+width+"&chartHeight="+(Integer.parseInt(request.getParameter("height"))-120);
 		String url3="/performance/perAnalyse.do?b_statAnalyse=query0&busitype="+busitype+"&fromModule="+fromModule+"&chartWidth="+width+"&chartHeight="+(Integer.parseInt(request.getParameter("height"))-80);
 	
 		String url4="/performance/perAnalyse.do?b_contrastAnalyse0=query0&busitype="+busitype+"&opt=1&fromModule="+fromModule+"&chartWidth="+width+"&chartHeight="+(Integer.parseInt(request.getParameter("height"))-120);
 		String url5="/performance/perAnalyse.do?b_mcontrastAnalyse0=query0&busitype="+busitype+"&opt=2&fromModule="+fromModule+"&chartWidth="+width+"&chartHeight="+(Integer.parseInt(request.getParameter("height"))-120);
 		String url6="/performance/perAnalyse.do?b_perResultTable0=query0&busitype="+busitype+"&fromModule="+fromModule;
 		String url7="/performance/perAnalyse.do?b_perMainbodyAnalyse0=query0&busitype="+busitype+"&fromModule="+fromModule+"&chartWidth="+width+"&chartHeight="+(Integer.parseInt(request.getParameter("height"))-120);		
 		String url8="/performance/perAnalyse.do?b_knowAnalyse0=query0&busitype="+busitype+"&fromModule="+fromModule+"&chartWidth="+width+"&chartHeight="+(Integer.parseInt(request.getParameter("height"))-120);
 		String url9="/performance/perAnalyse.do?b_perRemark0=query0&busitype="+busitype+"&fromModule="+fromModule;
 		String url10="/performance/perAnalyse.do?b_voteStat0=query0&busitype="+busitype+"&fromModule="+fromModule;
 		String url11="/competencymodal/personPostModal/orgTree.do?b_query=link&busitype="+busitype+"&fromModule="+fromModule+"&chartWidth="+width+"&chartHeight="+(Integer.parseInt(request.getParameter("height"))-120);
 		Connection conn = null;		
 		try{
 		conn = AdminDb.getConnection();
 		PerformanceAnalyseBo bo=new PerformanceAnalyseBo(conn);
 		ArrayList planlist = new ArrayList();
 		UserView userView=(UserView)session.getAttribute(WebConstant.userView);
 		if(plan_id!=null&&plan_id.length()>0)
 		{
 			url4+="&plan_id="+plan_id;
 			url5+="&plan_id="+plan_id;
 			url6+="&plan_id="+plan_id;
 			url7+="&plan_id="+plan_id;
 			url8+="&plan_id="+plan_id;
 			url9+="&plan_id="+plan_id;
 			url10+="&plan_id="+plan_id;
 			url11+="&plan_id="+plan_id;
 		}
 		
 	%>
	
		<hrms:tabset name="cardset" width="99%" height="100%" type="true">
			 <hrms:tab name="aa4" label="label.performance.singleConstrantAnalyse" visible="true" url="<%=url4%>" ><!-- 单人对比分析 -->
			 </hrms:tab>	
			 <hrms:tab name="aa6" label="label.performance.mulConstrantAnalyse" visible="true" url="<%=url5%>" ><!-- 多人对比分析 -->
			 </hrms:tab>	
		 <% if(plan_id!=null&&plan_id.length()>0){ 
		 	planlist=bo.getPlanList_commonData("7",0,1,userView,plan_id,busitype);
		 	if(planlist.size()>0){
		 %>
			 <hrms:tab name="aa5" label="lable.statistic.colligategradetable" visible="true" url="<%=url6%>" ><!-- 综合测评表 -->
			 </hrms:tab>	
		<% 	}
			}else{ 
		%>
			 <hrms:tab name="aa5" label="lable.statistic.colligategradetable" visible="true" url="<%=url6%>" >
			 </hrms:tab>	
		<% 	} 
			
			if(busitype==null || busitype.trim().length()<=0 || busitype.equals("0"))
		 	{
		%>
			 <hrms:tab name="aa9" label="hire.employActualize.personnelFilter.comment" visible="true" url="<%=url9%>" ><!-- 评语 -->
			 </hrms:tab>		 	 
		<% 	
			}
			if(plan_id==null||plan_id.trim().length()==0)
			{ 
		%>
			<hrms:tab name="aa3" label="label.performance.statAnalyse" visible="true" url="<%=url3%>" ><!-- 统计分析 -->
			</hrms:tab>
			<hrms:tab name="aa1" label="label.performance.singlePointAnalyse" visible="true" url="<%=url%>" ><!-- 单指标趋势分析 -->
			</hrms:tab>	
			<hrms:tab name="aa2" label="label.performance.mulPointAnalyse" visible="true" url="<%=url2%>" ><!-- 多指标趋势分析 -->
			</hrms:tab>
		<% } 					
		%>
			 <hrms:tab name="aa7" label="label.performance.mainbodySettAnalyse" visible="true" url="<%=url7%>" ><!-- 主体分类对比分析 -->
			 </hrms:tab>	
			 
		 <% 
		 if(busitype==null || busitype.trim().length()<=0 || busitype.equals("0"))
		 {
		 	if(plan_id!=null&&plan_id.length()>0)
		 	{ 
		 		planlist=bo.getPlanList_commonData("7",2,1,userView,plan_id,busitype);
		 		if(planlist.size()>0)
		 		{
		 %>	 		 
			 <hrms:tab name="aa8" label="label.performance.knowAnalyse" visible="true" url="<%=url8%>" ><!-- 了解程度对比分析 -->
			 </hrms:tab>
			<% }}else{ %>	 
			 <hrms:tab name="aa8" label="label.performance.knowAnalyse" visible="true" url="<%=url8%>" >
			 </hrms:tab>
		 <% }
		 	if(plan_id!=null&&plan_id.length()>0)
		 	{ 
			 	planlist=bo.getPlanList_commonData("7",1,0,userView,plan_id,busitype);
			 	if(planlist.size()>0){
		 %>
			 <hrms:tab name="aa10" label="lable.performance.votestat" visible="true" url="<%=url10%>" ><!-- 选票统计 -->
			 </hrms:tab>	
		<% 		} 
			}else
			{
		%>
			 <hrms:tab name="aa10" label="lable.performance.votestat" visible="true" url="<%=url10%>" >
			 </hrms:tab>	
		<% 	}
			}
			
			if(busitype!=null && busitype.trim().length()>0 && busitype.equals("1"))
		 	{ 
		%>
			<hrms:tab name="aa11" label="lable.performance.competDegreePic" visible="true" url="<%=url11%>" ><!-- 能力等级分析图 -->
			</hrms:tab>
		
		<% 	} %>
		</hrms:tabset>
	<%
	}catch(Exception ex)
		{
			ex.printStackTrace();
		}finally{
			try
			{
				if(conn!=null)
					conn.close();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	} 
	%>
<script type="text/javascript">
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
                width: document.body.clientWidth*0.99,
                height: document.body.clientHeight*0.98
            }).tabs('resize');
            tableset.tabs("select",index);
        }

    }
   /* if($('#_tabsetpane_cardset')){
        $(window).resize(function () {
            var tab = $('#_tabsetpane_cardset').tabs('getSelected');
            var index = $('#_tabsetpane_cardset').tabs('getTabIndex',tab);
            $('#_tabsetpane_cardset').tabs({
                width: document.body.clientWidth*0.99,
                height: document.body.clientHeight*0.98
            });
            $('#_tabsetpane_cardset').tabs('select', index);

        })
    }*/
</script>
</body>
