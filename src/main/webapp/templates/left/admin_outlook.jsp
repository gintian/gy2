
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<!--========================= design by lynn 11-30 ===============================-->
<!--========================= Begin left table ===================================-->
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="/css/stylesheet.css" type="text/css">
</head>
<body>     
<!--========================= 活 动 菜 单 开 始 ====================================-->
      <!--========== 系统运行管理      二级菜单目录 ====================-->
      <table border="0" cellspacing="0" cellpadding="0" height="100%">
        <tr valign="top"> 
          <td bgcolor="#D4DE88" width="140"> 
            <script language="JavaScript" src="/js/outlook-like.js"></script>
            <center>
              <table border="0" cellspacing="0" cellpadding="0" style="width:140;height:100%">
                <tr> 
                  <td> 
                    <script>    
                      outlookbar.otherclass="border=0 cellspacing='0' cellpadding='0' style='height:100%;width:100%;border-bottom:0pt solid #ebf5d6;'valign=middle align=center ";    
                      function setCount(x){    
                      if (document.all==null) return;    
                      document.all("oCount").innerText=x    
                    }
                      function load(form) {
                      var url = form.list.options[form.list.selectedIndex].value;
                      if (url != "") open(url, "_blank");
                      return false;
                    }
                    </script>
                  </td>
                </tr>
                <tr> 
                  <td style="height:100%" id="outLookBarShow" name="outLookBarShow" valign="top" align="center"> 
<!--============================== Ooulook 操作开始部分 ===================================-->
                    <div id="outLookBarDiv" name="outLookBarDiv" style="width=100%;height:100%"> 
<!--========================= 活 动 菜 单 开 始 ====================================-->
                      <table width="100%" border="0" cellspacing="0" cellpadding="0" style="height:100%;width:100%;border-bottom:0pt solid #ebf5d6;" valign="middle" align="center">
                        <tr height="1" bgcolor="#88B68B"> 
                          <td colspan="2" nowrap></td>
                        </tr>
		<!-- 上端金属栏 -->
        <tr height="1" bgcolor="#000000"> 
          <td colspan="2" nowrap></td>
        </tr>
        <tr height="1" bgcolor="#88B68B"> 
          <td colspan="2" nowrap></td>
        </tr>

        <!-- 工作任务 -->
	    <tr name="outlooktitle0" id="outlooktitle0" onMouseOver="this.bgColor='#B1A7FF';" onMouseOut="this.bgColor='#B1A7FF';" bgcolor="#B1A7FF" onClick="switchoutlookBar(0)" nowrap> 
          <td nowrap><img src="/images/pane_01.gif" width="23" height="20" border="0"></td>
          <td nowrap><a href="###" class="w-b">工作任务</a></td>
        </tr>

        <tr height="1" bgcolor="#000000"> 
          <td colspan="2" nowrap></td>
        </tr>

        <tr name="outlookdiv0" id="outlookdiv0" style="width:100%;display:none;height:0%;" bgcolor="#D7E9C0"> 
          <td colspan="2">
<!--=======================================================================================-->
            <div name="outlookdivin0" id="outlookdivin0" style="overflow:auto;width:100%;height:100%"> 
            <table width="100%" cellspacing="0" cellpadding="4" border="0">
              <tr onMouseOver="this.bgColor='#C5CF47';" onMouseOut="this.bgColor='#D7E9C0';" nowrap> 
                <td align="right"><img src="/images/pixel.gif" width="10" height="8" border="0"><img src="/images/ease4.gif" width="11" height="11" border="0"></td>
                <td><hrms:link href="/workflow/webclient/processlist.do?b_query=link"  function_id="070101" styleClass="head" target="i_body">启动新任务</hrms:link></td>
              </tr>              
              <tr onMouseOver="this.bgColor='#C5CF47';" onMouseOut="this.bgColor='#D7E9C0';" nowrap> 
                <td align="right"><img src="/images/pixel.gif" width="10" height="8" border="0"><img src="/images/ease4.gif" width="11" height="11" border="0"></td>
                <td><hrms:link href="/workflow/webclient/tasklist.do?b_query=link"  function_id="070102" styleClass="head" target="i_body">任务中心</hrms:link></td>
              </tr>              
              <tr onMouseOver="this.bgColor='#C5CF47';" onMouseOut="this.bgColor='#D7E9C0';" nowrap> 
                <td align="right"><img src="/images/pixel.gif" width="10" height="8" border="0"><img src="/images/ease4.gif" width="11" height="11" border="0"></td>
                <td><hrms:link href="/workflow/webclient/historylist.do?b_query=link"  function_id="070102" styleClass="head" target="i_body">历史任务</hrms:link></td>
              </tr>              
              <tr onMouseOver="this.bgColor='#C5CF47';" onMouseOut="this.bgColor='#D7E9C0';" nowrap> 
                <td align="right"><img src="/images/pixel.gif" width="10" height="8" border="0"><img src="/images/ease4.gif" width="11" height="11" border="0"></td>
                <td><hrms:link href="/workflow/webclient/monitortask.do?b_query=link"  function_id="070102" styleClass="head" target="i_body">任务监控</hrms:link></td>
              </tr>              

            </table>
<!--=======================================================================================-->
          </div>
          </td>
        </tr>
        <tr height="1" bgcolor="#000000"> 
          <td colspan="2" nowrap></td>
        </tr>
        <tr height="1" bgcolor="#88B68B"> 
          <td colspan="2" nowrap></td>
        </tr>
        <!-- 信息录入 -->
	    <tr name="outlooktitle1" id="outlooktitle1" onMouseOver="this.bgColor='#B1A7FF';" onMouseOut="this.bgColor='#B1A7FF';" bgcolor="#B1A7FF" onClick="switchoutlookBar(1)" nowrap> 
          <td nowrap><img src="/images/pane_01.gif" width="23" height="20" border="0"></td>
          <td nowrap><a href="###" class="w-b">信息录入</a></td>
        </tr>

        <tr height="1" bgcolor="#000000"> 
          <td colspan="2" nowrap></td>
        </tr>

        <tr name="outlookdiv1" id="outlookdiv1" style="width:100%;display:none;height:0%;" bgcolor="#D7E9C0"> 
          <td colspan="2">
<!--=======================================================================================-->
            <div name="outlookdivin1" id="outlookdivin1" style="overflow:auto;width:100%;height:100%"> 
            <table width="100%" cellspacing="0" cellpadding="4" border="0">
              <tr onMouseOver="this.bgColor='#C5CF47';" onMouseOut="this.bgColor='#D7E9C0';" nowrap> 
                <td align="right"><img src="/images/pixel.gif" width="10" height="8" border="0"><img src="/images/ease4.gif" width="11" height="11" border="0"></td>
                <td><hrms:link href="/info_maintain/userfrm.jsp?func=0201" function_id="070201" styleClass="head" target="i_body">人员信息</hrms:link></td>
              </tr>              
              <tr onMouseOver="this.bgColor='#C5CF47';" onMouseOut="this.bgColor='#D7E9C0';" nowrap> 
                          <td align="right"><img src="/images/pixel.gif" width="10" height="8" border="0"><img src="/images/ease4.gif" width="11" height="11" border="0"></td>
                <td><hrms:link href="/pzgl/pzlf/lqlrSelect.do?a_trans=20" function_id="070202" styleClass="head" target="i_body">单位信息</hrms:link></td>
              </tr>              
              <tr onMouseOver="this.bgColor='#C5CF47';" onMouseOut="this.bgColor='#D7E9C0';" nowrap> 
                          <td align="right"><img src="/images/pixel.gif" width="10" height="8" border="0"><img src="/images/ease4.gif" width="11" height="11" border="0"></td>
                <td><hrms:link href="/pzgl/pzlf/lqqrSelect.do?a_trans=21" function_id="070203" styleClass="head" target="i_body">职位信息</hrms:link></td>
              </tr>              

            </table>
<!--=======================================================================================-->
          </div>
          </td>
        </tr>
        <tr height="1" bgcolor="#000000"> 
          <td colspan="2" nowrap></td>
        </tr>
        <tr height="1" bgcolor="#88B68B"> 
          <td colspan="2" nowrap></td>
        </tr>

        
        
      <!-- 票证结报 -->
        <tr name="outlooktitle2" id="outlooktitle2" onMouseOver="this.bgColor='#B1A7FF';" onMouseOut="this.bgColor='#B1A7FF';" bgcolor="#B1A7FF" onClick="switchoutlookBar(2)" nowrap> 
          <td nowrap><img src="/images/pane_01.gif" width="23" height="20" border="0"></td>
          <td nowrap><a href="###" class="w-b">票证结报</a></td>
        </tr>

        <tr height="1" bgcolor="#000000"> 
          <td colspan="2" nowrap></td>
        </tr>

        <tr name="outlookdiv2" id="outlookdiv2" style="width:100%;display:none;height:0%;" bgcolor="#D7E9C0"> 
          <td colspan="2">
           <div name="outlookdivin2" id="outlookdivin2" style="overflow:auto;width:100%;height:100%"> 
            <table width="100%" cellspacing="0" cellpadding="4" border="0">	     
              <tr onMouseOver="this.bgColor='#C5CF47';" onMouseOut="this.bgColor='#D7E9C0';" nowrap> 
                <td align="right"><img src="/images/pixel.gif" width="10" height="1" border="0"><img src="/images/ease4.gif" width="11" height="11" border="0"></td>
                <td><hrms:link href="/pzgl/pzjb/pzRcjbSelect.do?a_link=true" function_id="070301" styleClass="head" target="i_body">日常结报</hrms:link></td>
              </tr>                            
              <tr onMouseOver="this.bgColor='#C5CF47';" onMouseOut="this.bgColor='#D7E9C0';" nowrap> 
                <td align="right"><img src="/images/pixel.gif" width="10" height="1" border="0"><img src="/images/ease4.gif" width="11" height="11" border="0"></td>
                <td><hrms:link href="/pzgl/pzjb/pzssdAdd.do?a_link=true" function_id="070302" styleClass="head" target="i_body">票证损失</hrms:link></td>
              </tr>
              <tr onMouseOver="this.bgColor='#C5CF47';" onMouseOut="this.bgColor='#D7E9C0';" nowrap> 
                <td align="right"><img src="/images/pixel.gif" width="10" height="1" border="0"><img src="/images/ease4.gif" width="11" height="11" border="0"></td>
                <td><hrms:link href="/pzgl/pzjb/pzzfSelect.do?a_link=true" function_id="070303" styleClass="head" target="i_body">票证作废</hrms:link></td>
              </tr>
              <tr onMouseOver="this.bgColor='#C5CF47';" onMouseOut="this.bgColor='#D7E9C0';" nowrap> 
                <td align="right"><img src="/images/pixel.gif" width="10" height="1" border="0"><img src="/images/ease4.gif" width="11" height="11" border="0"></td>
                <td><hrms:link href="/pzgl/pzjb/pzxhSearch.do?a_link=true" function_id="070304" styleClass="head" target="i_body">票证销毁</hrms:link></td>
              </tr>
              <tr onMouseOver="this.bgColor='#C5CF47';" onMouseOut="this.bgColor='#D7E9C0';" nowrap> 
                <td align="right"><img src="/images/pixel.gif" width="10" height="1" border="0"><img src="/images/ease4.gif" width="11" height="11" border="0"></td>
                <td><hrms:link href="/pzgl/pzjb/pzpdSelect.do?a_link=true" function_id="070305" styleClass="head" target="i_body">票证盘点</hrms:link></td>
              </tr>
              <tr onMouseOver="this.bgColor='#C5CF47';" onMouseOut="this.bgColor='#D7E9C0';" nowrap> 
                <td align="right"><img src="/images/pixel.gif" width="10" height="1" border="0"><img src="/images/ease4.gif" width="11" height="11" border="0"></td>
                <td><hrms:link href="/pzgl/pzjb/pzjjSelect.do?a_link=true" function_id="070306" styleClass="head" target="i_body">票证交接</hrms:link></td>
              </tr>

              <tr onMouseOver="this.bgColor='#C5CF47';" onMouseOut="this.bgColor='#D7E9C0';" nowrap> 
                <td align="right"><img src="/images/pixel.gif" width="10" height="1" border="0"><img src="/images/ease4.gif" width="11" height="11" border="0"></td>
                <td><hrms:link href="/pzgl/pzjb/fxczSelect.do" function_id="070307" styleClass="head" target="i_body">反向操作</hrms:link></td>
              </tr>
             </table>
             </div>
          </td>
        </tr> 
        <tr height="1" bgcolor="#000000"> 
          <td colspan="2" nowrap></td>
        </tr>
        <tr height="1" bgcolor="#88B68B"> 
          <td colspan="2" nowrap></td>
        </tr>       
      <!-- 票证账簿 -->
      <tr name="outlooktitle3" id="outlooktitle3" onMouseOver="this.bgColor='#B1A7FF';" onMouseOut="this.bgColor='#B1A7FF';" bgcolor="#B1A7FF" onClick="switchoutlookBar(3)" nowrap> 
          <td nowrap><img src="/images/pane_01.gif" width="23" height="20" border="0"></td>
          <td nowrap><a href="###" class="w-b">账册管理</a></td>
        </tr>

        <tr height="1" bgcolor="#000000"> 
          <td colspan="2" nowrap></td>
        </tr>
        <tr name="outlookdiv3" id="outlookdiv3" style="width:100%;display:none;height:0%;" bgcolor="#D7E9C0"> 
          <td colspan="2">
           <div name="outlookdivin3" id="outlookdivin3" style="overflow:auto;width:100%;height:100%"> 
            <table width="100%" cellspacing="0" cellpadding="4" border="0">	     
	          <tr onMouseOver="this.bgColor='#C5CF47';" onMouseOut="this.bgColor='#D7E9C0';" nowrap> 
                <td align="right"><img src="/images/pixel.gif" width="10" height="1" border="0"><img src="/images/ease4.gif" width="11" height="11" border="0"></td>
                <td><hrms:link href="/pzgl/pzzp/zpjzSelect.do" function_id="070401" styleClass="head" target="i_body">结    账</hrms:link></td>
              </tr>
	          <tr onMouseOver="this.bgColor='#C5CF47';" onMouseOut="this.bgColor='#D7E9C0';" nowrap> 
                <td align="right"><img src="/images/pixel.gif" width="10" height="1" border="0"><img src="/images/ease4.gif" width="11" height="11" border="0"></td>
                <td><hrms:link href="/pzgl/pzzp/zphtSelect.do" function_id="070402" styleClass="head" target="i_body">取消结账</hrms:link></td>
              </tr>
             </table>
             </div>
          </td>
        </tr> 

      <!-- 报表 -->          
        <tr height="1" bgcolor="#000000"> 
          <td colspan="2" nowrap></td>
        </tr>
        <tr height="1" bgcolor="#88B68B"> 
          <td colspan="2" nowrap></td>
        </tr>  

        <tr name="outlooktitle4" id="outlooktitle4" onMouseOver="this.bgColor='#B1A7FF';" onMouseOut="this.bgColor='#B1A7FF';" bgcolor="#B1A7FF" onClick="switchoutlookBar(4)" nowrap> 
          <td nowrap><img src="/images/pane_01.gif" width="23" height="20" border="0"></td>
          <td nowrap><a href="###" class="w-b">报表查询</a></td>
        </tr>

        <tr height="1" bgcolor="#000000"> 
          <td colspan="2" nowrap></td>
        </tr>
        <tr name="outlookdiv4" id="outlookdiv4" style="width:100%;display:none;height:0%;" bgcolor="#D7E9C0"> 
          <td colspan="2">
           <div name="outlookdivin4" id="outlookdivin4" style="overflow:auto;width:100%;height:100%"> 
            <table width="100%" cellspacing="0" cellpadding="4" border="0">	     
	      <tr onMouseOver="this.bgColor='#C5CF47';" onMouseOut="this.bgColor='#D7E9C0';" nowrap> 
                <td align="right"><img src="/images/pixel.gif" width="10" height="1" border="0"><img src="/images/ease4.gif" width="11" height="11" border="0"></td>
                <td><hrms:link href="/report/ListAllReports.do?classId=07" function_id="070501" styleClass="head" target="i_body">票证用存报表查询</hrms:link></td>
              </tr>    
              <tr onMouseOver="this.bgColor='#C5CF47';" onMouseOut="this.bgColor='#D7E9C0';" nowrap> 
                <td align="right"><img src="/images/pixel.gif" width="10" height="1" border="0"><img src="/images/ease4.gif" width="11" height="11" border="0"></td>
                <td><hrms:link href="/query/ListAllQuery.do?classId=07"  function_id="070502" styleClass="head" target="i_body">简单查询</hrms:link></td>
              </tr>
             
             </table>
             </div>
          </td>
        </tr>         
        <!-- 票证维护 -->              
       
        <tr height="1" bgcolor="#000000"> 
          <td colspan="2" nowrap></td>
        </tr>
        <tr height="1" bgcolor="#88B68B"> 
          <td colspan="2" nowrap></td>
        </tr>    
            
        <tr name="outlooktitle6" id="outlooktitle6" onMouseOver="this.bgColor='#B1A7FF';" onMouseOut="this.bgColor='#B1A7FF';" bgcolor="#B1A7FF" onClick="switchoutlookBar(6)" nowrap> 
          <td nowrap><img src="/images/pane_01.gif" width="23" height="20" border="0"></td>
          <td nowrap><a href="###" class="w-b">系统维护</a></td>
        </tr>

        <tr height="1" bgcolor="#000000"> 
          <td colspan="2" nowrap></td>
        </tr>
        <tr name="outlookdiv6" id="outlookdiv6" style="width:100%;display:none;height:0%;" bgcolor="#D7E9C0"> 
          <td colspan="2">
           <div name="outlookdivin6" id="outlookdivin6" style="overflow:auto;width:100%;height:100%"> 
            <table width="100%" cellspacing="0" cellpadding="4" border="0">
	          <tr onMouseOver="this.bgColor='#C5CF47';" onMouseOut="this.bgColor='#D7E9C0';" nowrap> 
                <td align="right"><img src="/images/pixel.gif" width="10" height="1" border="0"><img src="/images/ease4.gif" width="11" height="11" border="0"></td>
                <td><hrms:link href="/pzgl/pzwh/pzdmwhAdd.do?a_link=true" function_id="070601" styleClass="head" target="i_body">票证种类维护</hrms:link></td>
              </tr>        

	          <tr onMouseOver="this.bgColor='#C5CF47';" onMouseOut="this.bgColor='#D7E9C0';" nowrap> 
                <td align="right"><img src="/images/pixel.gif" width="10" height="1" border="0"><img src="/images/ease4.gif" width="11" height="11" border="0"></td>
                <td><hrms:link href="/pzgl/pzwh/yzqySearch.do?a_link=true" function_id="070602" styleClass="head" target="i_body">印制企业管理维护</hrms:link></td>
              </tr>        
	      <tr onMouseOver="this.bgColor='#C5CF47';" onMouseOut="this.bgColor='#D7E9C0';" nowrap> 
                <td align="right"><img src="/images/pixel.gif" width="10" height="1" border="0"><img src="/images/ease4.gif" width="11" height="11" border="0"></td>
                <td><hrms:link href="/pzgl/pzwh/pzkhglyWh.do?a_link=1" function_id="070603" styleClass="head" target="i_body">票证管理员维护</hrms:link></td>
              </tr>        
	      <tr onMouseOver="this.bgColor='#C5CF47';" onMouseOut="this.bgColor='#D7E9C0';" nowrap> 
                <td align="right"><img src="/images/pixel.gif" width="10" height="1" border="0"><img src="/images/ease4.gif" width="11" height="11" border="0"></td>
                <td><hrms:link href="/pzgl/pzlf/initdataSelect.do?a_link=1" function_id="070604" styleClass="head" target="i_body">期初数据录入</hrms:link></td>
              </tr>        
	      <tr onMouseOver="this.bgColor='#C5CF47';" onMouseOut="this.bgColor='#D7E9C0';" nowrap> 
                <td align="right"><img src="/images/pixel.gif" width="10" height="1" border="0"><img src="/images/ease4.gif" width="11" height="11" border="0"></td>
                <td><hrms:link href="/pzgl/pzlf/initJcslSelect.do?a_link=1" function_id="070605" styleClass="head" target="i_body">期初结存数据录入</hrms:link></td>
              </tr>        
                    
             </table>
             </div>
          </td>
        </tr> 
  
        <tr height="1" bgcolor="#000000"> 
          <td colspan="2" nowrap></td>
        </tr>
        <tr height="1" bgcolor="#88B68B"> 
          <td colspan="2" nowrap></td>
        </tr> 
     	<!-- 退出 eBIS e5s 系统 -->
        <tr bgcolor="#B1A7FF"> 
          <td colspan="2" name="关闭" id="close" onClick="parent.window.close()">
		    <!--========== onclick="window.close()" ==========-->
		    <img src="/images/exit_01.jpg" width="140" height="18" border="0" name="Exit" style="cursor:hand;filter:alpha(opacity=100)" onMouseOver="makevisible(this,1)" onMouseOut="makevisible(this,0)"></td>
        </tr>

        <tr height="1" bgcolor="#000000"> 
          <td colspan="2" nowrap></td>
        </tr>
        <tr height="1" bgcolor="#88B68B"> 
          <td colspan="2" nowrap></td>
        </tr>

<!--=====================================================================================-->
                          <td colspan="2" name="blankdiv" valign="top" align="center" id="blankdiv" style="height:100%;width:100%:;display:none;"> 
                            <div style="overflow:auto;width:100%;height:100%"></div>
                          </td>
                        </tr>
                      </table>
                    </div>
<!--============================== Ooulook 操作结束部分 ===================================-->
                    <%
                    String menuindex=(String)request.getParameter("menuindex");
                    if (menuindex==null) menuindex="0";
                    %>

                    <script>
                      //outlookbar.show()
                      switchoutlookBar(<%= menuindex %>);
                    </script>


                  </td>
                </tr>
              </table>
<!--============================== 活动树状菜单结束 ===================================-->
    </td>
    <td height="1" class="toprim"><img src="/images/pixel.gif" width="1" height="1" border="0"></td>
    <td height="1" bgcolor="F4FBE1"><img src="/images/pixel.gif" width="1" height="1" border="0"></td>
    <td height="1" bgcolor="C4E759"><img src="/images/pixel.gif" width="6" height="1" border="0"></td>
    <td height="1" class="toprim"><img src="/images/pixel.gif" width="1" height="1" border="0"></td>
  </tr>
</table>

<!--==============================  End left table ==================================-->
</body>
</html>