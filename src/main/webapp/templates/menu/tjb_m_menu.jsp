<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.constant.SystemConfig" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%

    String userName = null;
    String css_url="/css/css1.css";
    String isturn=SystemConfig.getPropertyValue("Menutogglecollapse");
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
           //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
%>

<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link rel="stylesheet" href="/css/leftmenu.css" type="text/css">
<SCRIPT language="JavaScript1.2" src="/js/menu.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript">
   if(window.screenTop==128)//fullscreen status
	   var  divHeight = window.screen.availHeight - window.screenTop -(5*28+21);  
   else
	   var  divHeight = window.screen.availHeight - window.screenTop -(5*28+21+20); //add other height
   function turn()
   {
     <% 
	  if(isturn==null||!isturn.equals("false"))
	     out.println("parent.menupnl.toggleCollapse(false);");
	%>
   }    
</SCRIPT>

<body class=menuBodySet style="margin:0 0 0 0">
<table cellpadding=0 cellspacing=0 >
<tr>
  <td width="162" id="menucol">  
	<hrms:priv func_id="29010,29011">
	    <hrms:priv func_id="2901">
		<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="1">
			<tr style="cursor:hand;">
				<td align="center" class=menu_title id=menuTitle1 onclick="menuChange(menu1,divHeight,menuTitle1,arrow1);">
					<span><span id=arrow1><img src="/images/darrow.gif" border=0></span>自动生成</span>
				</td>
			</tr>
			<tr>
				<td>
					<div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display=block" id=menu1>
						<form name="static" action="" method="post">
							<table cellpadding=2 cellspacing=3 align=center width="100%" class="DetailTable" style="position:relative;top:10px;">
								<hrms:priv func_id="29010">
									<tr>
										<td align="center" class="loginFont">
											<hrms:link href="/report/auto_fill_report/reportlist.do?b_query=link&sortId=-1&print=5&checkFlag=0" target="il_body" function_id="xxx">
												<img src="/images/tx.gif" border=0>
											</hrms:link>
										</td>
									</tr>
									<tr>
										<td align="center" class="loginFont">
											<hrms:link href="/report/auto_fill_report/reportlist.do?b_query=link&sortId=-1&print=5&checkFlag=0" target="il_body" function_id="xxx">
												<font id="a001" class="menu_a">提取数据</font>
											</hrms:link>
										</td>
									</tr>
								</hrms:priv>
								<hrms:priv func_id="29011">
									<tr>
										<td align="center" class="loginFont">
											<a href="/report/auto_fill_report/options.do?b_query=link" target="il_body"><img src="/images/jgbm.gif" border=0></a>
										</td>
									</tr>
									<tr>
										<td align="center" class="loginFont">
											<a href="/report/auto_fill_report/options.do?b_query=link" target="il_body"><font id="a003" class="menu_a">取数范围</font></a>
										</td>
									</tr>
								</hrms:priv>
							</table>
						</form>
					</div>
				</td>
			</tr>
		</table>
		</hrms:priv>
	</hrms:priv>
	
	<hrms:priv func_id="2902">
		<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="2">
			<tr style="cursor:hand;">
				<td align="center" class=menu_title id=menuTitle2 onclick="menuChange(menu2,divHeight,menuTitle2,arrow2);">
					<span><span id=arrow2><img src="/images/darrow.gif" border=0></span>编辑报表</span>
				</td>
			</tr>
			<tr>
				<td>
					<div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;" id=menu2>
						<form name="static" action="" method="post">
							<table cellpadding=2 cellspacing=3 align=center width="100%" class="DetailTable" style="position:relative;top:10px;">

								<tr>
									<td align="center" class="loginFont">
										<hrms:link href="/report/edit_report/reportSettree.do" target="il_body" function_id="xxx" onclick="turn();">
											<img src="/images/edit_info.gif" border=0>
										</hrms:link>
									</td>
								</tr>
								<tr>
									<td align="center" class="loginFont">
										<hrms:link href="/report/edit_report/reportSettree.do" target="il_body" function_id="xxx" onclick="turn();">
											<font id="a001" class="menu_a">编辑报表</font>
										</hrms:link>
									</td>
								</tr>
								<hrms:priv func_id="29024">
									<tr>
										<td align="center" class="loginFont">
											<hrms:link href="/report/edit_report/receive_report/receive_report.jsp?editflag=1" target="il_body" function_id="xxx">
												<img src="/images/ld.gif" border=0>
											</hrms:link>
										</td>
									</tr>
									<tr>
										<td align="center" class="loginFont">
											<hrms:link href="/report/edit_report/receive_report/receive_report.jsp?editflag=1" target="il_body" function_id="xxx">
												<font id="a001" class="menu_a">接收报盘</font>
											</hrms:link>
										</td>
									</tr>
								</hrms:priv>
							</table>
						</form>
					</div>
				</td>
			</tr>
		</table>
	</hrms:priv>
	
	<hrms:priv func_id="29030,29031,29032,29033,29034">
	<hrms:priv func_id="2903">
		<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="3">
			<tr style="cursor:hand;">
				<td align="center" class=menu_title id=menuTitle3 onclick="menuChange(menu3,divHeight,menuTitle3,arrow3);">
					<span><span id=arrow3><img src="/images/darrow.gif" border=0></span>报表汇总</span>
				</td>
			</tr>
			<tr>
				<td>
					<div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;" id=menu3>
						<form name="static" action="" method="post">
							<table cellpadding=2 cellspacing=3 align=center width="100%" class="DetailTable" style="position:relative;top:10px;">
								<hrms:priv func_id="29030">
									<tr>
										<td align="center" class="loginFont">
											<hrms:link href="/report/edit_report/receive_report/receive_report.jsp" target="il_body" function_id="xxx">
												<img src="/images/ld.gif" border=0>
											</hrms:link>
										</td>
									</tr>
									<tr>
										<td align="center" class="loginFont">
											<hrms:link href="/report/edit_report/receive_report/receive_report.jsp" target="il_body" function_id="xxx">
												<font id="a001" class="menu_a">接收报盘</font>
											</hrms:link>
										</td>
									</tr>
								</hrms:priv>
								<hrms:priv func_id="29033">
									<tr>
										<td align="center" class="loginFont">
											<hrms:link href="/report/edit_report/sendReceiveView.do?b_query=b_query" target="il_body" function_id="xxx">
												<img src="/images/ld.gif" border=0>
											</hrms:link>
										</td>
									</tr>

									<tr>
										<td align="center" class="loginFont">
											<hrms:link href="/report/edit_report/sendReceiveView.do?b_query=b_query" target="il_body" function_id="xxx">
												<font id="a001" class="menu_a">表式收发</font>
											</hrms:link>
										</td>
									</tr>

								</hrms:priv>
		
								<hrms:priv func_id="29031">
									<tr>
										<td align="center" class="loginFont">
											<hrms:link href="/report/edit_collect/reportCollect.do?b_initCollect=link&sortid=@" target="il_body" function_id="xxx">
												<img src="/images/px.gif" border=0>
											</hrms:link>
										</td>
									</tr>
									<tr>
										<td align="center" class="loginFont">
											<hrms:link href="/report/edit_collect/reportCollect.do?b_initCollect=link&sortid=@" target="il_body" function_id="xxx">
												<font id="a001" class="menu_a">报表汇总</font>
											</hrms:link>
										</td>
									</tr>
								</hrms:priv>
								<hrms:priv func_id="29032">
									<tr>
										<td align="center" class="loginFont">
											<hrms:link href="/report/report_collect/reportOrgCollecttree.do?b_init=int" target="il_body" function_id="xxx" onclick="turn();">
												<img src="/images/edit_info.gif" border=0>
											</hrms:link>
										</td>
									</tr>
									<tr>
										<td align="center" class="loginFont">
											<hrms:link href="/report/report_collect/reportOrgCollecttree.do?b_init=int" target="il_body" function_id="xxx" onclick="turn();">
												<font id="a001" class="menu_a">编辑报表</font>
											</hrms:link>
										</td>
									</tr>
								</hrms:priv>
								
									
								<hrms:priv func_id="29034">	
									<tr>
										<td align="center" class="loginFont">
											<hrms:link href="/report/report_pigeonhole/reportBatchPigeonhole.do?b_int=int" target="il_body" function_id="xxx">
												<img src="/images/edit_info.gif" border=0>
											</hrms:link>
										</td>
									</tr>
									<tr>
										<td align="center" class="loginFont">
											<hrms:link href="/report/report_pigeonhole/reportBatchPigeonhole.do?b_int=int" target="il_body" function_id="xxx">
												<font id="a001" class="menu_a">报表归档</font>
											</hrms:link>
										</td>
									</tr>
								</hrms:priv>
							</table>
						</form>
					</div>
				</td>
			</tr>
		</table>
	</hrms:priv>
	</hrms:priv>
	<hrms:priv func_id="29040">
	<hrms:priv func_id="2904">
		<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="4">
			<tr style="cursor:hand;">
				<td align="center" class=menu_title id=menuTitle4 onclick="menuChange(menu4,divHeight,menuTitle4,arrow4);">
					<span><span id=arrow4><img src="/images/darrow.gif" border=0></span>报表分析</span>
				</td>
			</tr>
			<tr>
				<td>
					<div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;" id=menu4>
						<form name="static" action="" method="post">
							<table cellpadding=2 cellspacing=3 align=center width="100%" class="DetailTable" style="position:relative;top:10px;">
								<hrms:priv func_id="29040">
									<tr>
										<td align="center" class="loginFont">
											<hrms:link href="/report/report_analyse/reportunittree.do" target="il_body" function_id="xxx" onclick="turn();">
												<img src="/images/mc.gif" border=0>
											</hrms:link>
										</td>
									</tr>
									<tr>
										<td align="center" class="loginFont">
											<hrms:link href="/report/report_analyse/reportunittree.do" target="il_body" onclick="turn();">
												<font id="a001" class="menu_a">报表浏览</font>
											</hrms:link>
										</td>
									</tr>
								</hrms:priv>
							</table>
						</form>
					</div>
				</td>
			</tr>
		</table>
	</hrms:priv>
	</hrms:priv>
	
	
	<hrms:priv func_id="290601,290602,290603,290604">
	<hrms:priv func_id="2906">
	<table cellpadding=0 cellspacing=0 width="159"  class=menu_table index="6">
			<tr style="cursor:hand;">
				<td align="center" class=menu_title id=menuTitle6 onclick="menuChange(menu6,divHeight,menuTitle6,arrow6);">
					<span><span id=arrow6><img src="/images/darrow.gif" border=0></span>精算报表</span>
				</td>
			</tr>
			<tr>
				<td valign="top">
					<div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;" id=menu6>
						<form name="static" action="" method="post">
							<table cellpadding=2 cellspacing=3 align=center width="100%" class="DetailTable" style="position:relative;top:10px;">
								<hrms:priv func_id="290601">
									<tr>
										<td align="center" class="loginFont">
											<hrms:link href="/report/actuarial_report/fill_cycle.do?b_query=query" target="il_body" function_id="xxx">
												<img src="/images/ll.gif" border=0>
											</hrms:link>
										</td>
									</tr>
									<tr>
										<td align="center" class="loginFont">
											<hrms:link href="/report/actuarial_report/fill_cycle.do?b_query=query" target="il_body">
												<font id="a001" class="menu_a">填报周期</font>
											</hrms:link>
										</td>
									</tr>
								</hrms:priv>
								<hrms:priv func_id="290602">
									<tr>
										<td align="center" class="loginFont">
											<hrms:link href="/report/actuarial_report/edit_report/editreportlist.do?b_query=lisk" target="il_body" function_id="xxx">
												<img src="/images/edit_info.gif" border=0>
											</hrms:link>
										</td>
									</tr>
									<tr>
										<td align="center" class="loginFont">
											<hrms:link href="/report/actuarial_report/edit_report/editreportlist.do?b_query=lisk" target="il_body">
												<font id="a001" class="menu_a">编辑报表</font>
											</hrms:link>
										</td>
									</tr>
								</hrms:priv>
								<hrms:priv func_id="290603">
									<tr>
										<td align="center" class="loginFont">
											<hrms:link href="/report/actuarial_report/report_collect.do?b_query0=query" target="il_body" function_id="xxx" onclick="turn();">
												<img src="/images/px.gif" border=0>
											</hrms:link>
										</td>
									</tr>
									<tr>
										<td align="center" class="loginFont">
											<hrms:link href="/report/actuarial_report/report_collect.do?b_query0=query" target="il_body" onclick="turn();">
												<font id="a001" class="menu_a">报表汇总</font>
											</hrms:link>
										</td>
									</tr>
								</hrms:priv>
								<hrms:priv func_id="290604">	
									<tr>
										<td align="center" class="loginFont">
											<hrms:link href="/report/actuarial_report/validate_rule.do?br_query=query" target="il_body" function_id="xxx" onclick="turn();">
												<img src="/images/pg.gif" border=0>
											</hrms:link>
										</td>
									</tr>
									<tr>
										<td align="center" class="loginFont">
											<hrms:link href="/report/actuarial_report/validate_rule.do?br_query=query" target="il_body" onclick="turn();">
												<font id="a001" class="menu_a">校验规则</font>
											</hrms:link>
										</td>
									</tr>
								</hrms:priv>
								
							</table>
						</form>
					</div>
				</td>
			</tr>
		</table>
	</hrms:priv>
	</hrms:priv>
	<hrms:priv func_id="29050,29052,29053">
	<hrms:priv func_id="2905">
		<table cellpadding=0 cellspacing=0 width="159"  class=menu_table index="5">
			<tr style="cursor:hand;">
				<td align="center" class=menu_title id=menuTitle5 onclick="menuChange(menu5,divHeight,menuTitle5,arrow5);">
					<span><span id=arrow5><img src="/images/darrow.gif" border=0></span>填报单位</span>
				</td>
			</tr>
			<tr>
				<td valign="top">
					<div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;" id=menu5>
						<form name="static" action="" method="post">
							<table cellpadding=2 cellspacing=3 align=center width="100%" class="DetailTable" style="position:relative;top:10px;">
								<hrms:priv func_id="29050">
									<tr>
										<td align="center" class="loginFont">
											<hrms:link href="/report/org_maintenance/reportunittree.do?returnvalue=" target="il_body" function_id="xxx">
												<img src="/images/organization.gif" border=0>
											</hrms:link>
										</td>
									</tr>
									<tr>
										<td align="center" class="loginFont">
											<hrms:link href="/report/org_maintenance/reportunittree.do?returnvalue=" target="il_body">
												<font id="a001" class="menu_a">填报单位维护</font>
											</hrms:link>
										</td>
									</tr>
								</hrms:priv>

								<hrms:priv func_id="29052">
									<tr>
										<td align="center" class="loginFont">
											<hrms:link href="/report/report_state/reportunittree.do" target="il_body" function_id="xxx" onclick="turn();">
												<img src="/images/px.gif" border=0>
											</hrms:link>
										</td>
									</tr>
									<tr>
										<td align="center" class="loginFont">
											<hrms:link href="/report/report_state/reportunittree.do" target="il_body" onclick="turn();">
												<font id="a001" class="menu_a">按组织机构<Br>查阅报表状态</font>
											</hrms:link>
										</td>
									</tr>
								</hrms:priv>								
								
								<hrms:priv func_id="29053">
								<tr>
										<td align="center" class="loginFont">
											<hrms:link href="/report/report_status.do?b_query=query&opt=init" target="il_body" function_id="xxx" onclick="turn();">
												<img src="/images/px.gif" border=0>
											</hrms:link>
										</td>
									</tr>
									<tr>
										<td align="center" class="loginFont">
											<hrms:link href="/report/report_status.do?b_query=query&opt=init" target="il_body" onclick="turn();">
												<font id="a001" class="menu_a">按报表分类<Br>查阅报表状态</font>
											</hrms:link>
										</td>
									</tr>
								</hrms:priv>
							</table>
						</form>
					</div>
				</td>
			</tr>
		</table>
	</hrms:priv>
	</hrms:priv>
	
	
	
	
</td>

</tr>
</table>	
	<script language="javascript">
	showFirst();
</script>