<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String bosflag="";
    if(userView!=null){
    	bosflag = userView.getBosflag();
    }
%>
<html>
<head>
	<title></title>
	<style  type="text/css" >
		img{position:relative}
		area {position:relative; display:block;}
	</style>
	<link href="/css/css1.css" rel="stylesheet" type="text/css">   
</head>
	
<body class="body_sec">
	<!-- hcm版本 -->
	<%if("hcm".equals(bosflag)){ %>
		<table width="80%" align="center" >
			<tr>
				<td align="center"><img src="hcm/selfinfo.jpg"  border="0" usemap="#Map"></td>
			</tr>
		</table>
		<map name="Map">
		<!-- 
			第一行导航条
		 -->
		<area shape="rect" coords="28,70,155,100" href="/general/card/searchshowcard.do?returnvalue=dxts&b_showcard=link&userbase=&a0100=&inforkind=1&tabid=1&multi_cards=-1" alt="我的档案">
		<area shape="rect" coords="190,70,320,100" href="/general/card/searchshowcard.do?returnvalue=dxts&b_showcard=link&userbase=&a0100=&inforkind=1&tabid=2&multi_cards=-1" alt="我的简历">
		<area shape="rect" coords="355,70,482,100" href="/general/card/searchshowcard.do?returnvalue=dxts&b_showcard=link&userbase=&a0100=&inforkind=1&tabid=3&multi_cards=-1" alt="获奖业绩">
		<area shape="rect" coords="517,70,645,100" href="/general/card/searchshowcard.do?returnvalue=dxts&b_showcard=link&userbase=&a0100=&inforkind=1&tabid=4&multi_cards=-1" alt="学习培训">
		<area shape="rect" coords="681,70,808,100" href="/general/card/searchshowcard.do?returnvalue=dxts&b_showcard=link&userbase=&a0100=&inforkind=1&tabid=5&multi_cards=-1" alt="薪酬绩效">
		<!-- 
			第二行导航条
		 -->
		<area shape="rect" coords="110,248,238,280" href="/general/template/matterList.do?returnvalue=dxt&b_query=link&ver=5" alt="我的任务">
		<area shape="rect" coords="274,248,404,280" href="/system/warn/info_all.do?returnvalue=dxt&br_query=link&ver=5" alt="预警提示">
		<area shape="rect" coords="439,248,568,280" href="/selfservice/welcome/hot_topic.do?returnvalue=dxt&b_more=more&home=5&ver=5&discriminateFlag=rese" alt="热点调查">
		<area shape="rect" coords="602,248,732,280" href="/selfservice/lawbase/lawtext/law_maintenance0.do?returnvalue=zdxt&b_init=link&amp;basetype=4" alt="知识中心">
		 <!-- 
			第三行导航条第一列
		 -->
		<area shape="rect" coords="193,453,318,469" href="/selfservice/downfile/downfilelist.do?returnvalue=dxt&b_query=link&amp;fileflag=2" alt="办事流程">
		<area shape="rect" coords="193,480,318,495" href="/selfservice/downfile/downfilelist.do?returnvalue=dxt&b_query=link&amp;fileflag=1" alt="人事表格">
		<area shape="rect" coords="193,506,318,524" href="/selfservice/propose/searchpropose.do?returnvalue=dxt&b_query=link" alt="意见箱">
		<area shape="rect" coords="193,534,318,552" href="/selfservice/propose/searchconsulant.do?returnvalue=dxt&b_query=link" alt="咨询台">
		<area shape="rect" coords="193,562,318,580" href="/selfservice/addressbook/initqueryaddressbook.do?returnvalue=dxt&b_init=link&amp;query=1&amp;action=queryaddressbook.do&amp;target=mil_body" alt="通讯录">
		<area shape="rect" coords="193,590,318,606" href="/selfservice/welcome/boardTheMore.do?returnvalue=dxt&b_more=link" alt="公告栏">
		 <!-- 
			第三行导航条第二列
		 -->
		<area shape="rect" coords="358,453,482,469" href="/workbench/browse/showinfo.do?returnvalue=dxt&b_search=link&action=showinfodata.do&target=nil_body&userbase=&flag=noself&isUserEmploy=0&isphotoview=" alt="信息浏览">
		<area shape="rect" coords="358,480,482,495" href="/workbench/info/showinfo.do?returnvalue1=zdxt&b_search=link&action=showinfodata.do&target=nil_body&flag=noself&userbase=" alt="信息维护">
		 <!-- 
			第三行导航条第三列
		 -->
		<area shape="rect" coords="523,453,648,469" href="/general/card/searchshowcard.do?returnvalue=dxts&b_showcard=link&userbase=&a0100=&inforkind=1&tabid=6&multi_cards=-1" alt="工资查询">
		<area shape="rect" coords="523,480,648,496" href="/general/card/searchshowcard.do?returnvalue=dxts&b_showcard=link&userbase=&a0100=&inforkind=1&tabid=7&multi_cards=-1" alt="社保查询">
		<area shape="rect" coords="523,506,648,524" href="/general/card/searchshowcard.do?returnvalue=dxts&b_showcard=link&userbase=&a0100=&inforkind=1&tabid=8&multi_cards=-1" alt="公积金查询">
		</map>
		<!-- 其他版本（hr版本） -->
	<%}else{ %>
		<table width="80%" align="center" >
			<tr>
				<td align="center"> <img src="hcm/selfinfo.jpg"  border="0" usemap="#Map"> </td>
			</tr>
		</table>
		<map name="Map">
		<!-- 
			第一行导航条
		 -->
		<area shape="rect" coords="28,70,155,100" href="/general/inform/synthesisbrowse/mycard.do?returnvalue=dxts&b_mysearch=link&userbase=&a0100=&multi_cards=-1&inforkind=1&userpriv=selfinfo&tabid=1&flag=infoself" alt="基本信息">
		<area shape="rect" coords="190,70,320,100" href="/general/inform/synthesisbrowse/mycard.do?returnvalue=dxts&b_mysearch=link&userbase=&a0100=&multi_cards=-1&inforkind=1&userpriv=selfinfo&tabid=2&flag=infoself" alt="工作任职">
		<area shape="rect" coords="355,70,482,100" href="/general/inform/synthesisbrowse/mycard.do?returnvalue=dxts&b_mysearch=link&userbase=&a0100=&multi_cards=-1&inforkind=1&userpriv=selfinfo&tabid=3&flag=infoself" alt="业绩信息">
		<area shape="rect" coords="517,70,645,100" href="/general/inform/synthesisbrowse/mycard.do?returnvalue=dxts&b_mysearch=link&userbase=&a0100=&multi_cards=-1&inforkind=1&userpriv=selfinfo&tabid=4&flag=infoself" alt="学习培训">
		<area shape="rect" coords="681,70,808,100" href="/general/inform/synthesisbrowse/mycard.do?returnvalue=dxts&b_mysearch=link&userbase=&a0100=&multi_cards=-1&inforkind=1&userpriv=selfinfo&tabid=5&flag=infoself" alt="绩效评估">
		<!-- 
			第二行导航条
		 -->
		<area shape="rect" coords="110,248,238,280" href="/general/template/matterList.do?returnvalue=dxt&b_query=link&ver=5" alt="我的任务">
		<area shape="rect" coords="274,248,404,280" href="/system/warn/info_all.do?returnvalue=dxt&br_query=link&ver=5" alt="预警提示">
		<area shape="rect" coords="439,248,568,280" href="/selfservice/welcome/hot_topic.do?returnvalue=dxt&b_more=more&home=5&ver=5&discriminateFlag=rese" alt="热点调查">
		<area shape="rect" coords="602,248,732,280" href="/selfservice/lawbase/lawtext/law_maintenance0.do?returnvalue=zdxt&b_init=link&amp;basetype=4" alt="知识中心">
		 <!-- 
			第三行导航条第一列
		 -->
		<area shape="rect" coords="193,453,318,469" href="/selfservice/downfile/downfilelist.do?returnvalue=dxt&b_query=link&amp;fileflag=2" alt="办事流程">
		<area shape="rect" coords="193,480,318,495" href="/selfservice/downfile/downfilelist.do?returnvalue=dxt&b_query=link&amp;fileflag=1" alt="人事表格">
		<area shape="rect" coords="193,506,318,524" href="/selfservice/propose/searchpropose.do?returnvalue=dxt&b_query=link" alt="意见箱">
		<area shape="rect" coords="193,534,318,552" href="/selfservice/propose/searchconsulant.do?returnvalue=dxt&b_query=link" alt="咨询台">
		<area shape="rect" coords="193,562,318,580" href="/selfservice/addressbook/initqueryaddressbook.do?returnvalue=dxt&b_init=link&amp;query=1&amp;action=queryaddressbook.do&amp;target=mil_body" alt="通讯录">
		<area shape="rect" coords="193,590,318,606" href="/selfservice/welcome/boardTheMore.do?returnvalue=dxt&b_more=link" alt="公告栏">
		 <!-- 
			第三行导航条第二列
		 -->
		<area shape="rect" coords="358,453,482,469" href="/workbench/browse/showinfo.do?returnvalue=dxt&b_search=link&action=showinfodata.do&target=nil_body&userbase=&flag=noself&isUserEmploy=0&isphotoview=" alt="信息浏览">
		<area shape="rect" coords="358,480,482,495" href="/workbench/info/showinfo.do?returnvalue1=zdxt&b_search=link&action=showinfodata.do&target=nil_body&flag=noself&userbase=" alt="信息维护">
		 <!-- 
			第三行导航条第三列
		 -->
		<area shape="rect" coords="523,453,648,469" href="/general/card/searchshowcard.do?returnvalue=dxts&b_showcard=link&userbase=&a0100=&inforkind=1&tabid=6&multi_cards=-1" alt="工资查询">
		<area shape="rect" coords="523,480,648,496" href="/general/card/searchshowcard.do?returnvalue=dxts&b_showcard=link&userbase=&a0100=&inforkind=1&tabid=7&multi_cards=-1" alt="社保查询">
		<area shape="rect" coords="523,506,648,524" href="/general/card/searchshowcard.do?returnvalue=dxts&b_showcard=link&userbase=&a0100=&inforkind=1&tabid=8&multi_cards=-1" alt="公积金查询">
		</map>
	<%} %>
</body>
</html>
