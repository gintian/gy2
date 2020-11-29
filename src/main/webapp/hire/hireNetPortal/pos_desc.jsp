<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
                                                                                              
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link> 
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="JavaScript" src="/components/codeSelector/codeSelector.js"></script>
<script language="JavaScript" src="/components/extWidget/proxy/TransactionProxy.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/general/sys/hjaxmanage.js"></script>
<script language="JavaScript" src="/hire/hireNetPortal/employNetPortal.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
<script type="text/javascript" src="/jquery/jquery-3.3.1.min.js"></script>
<style>
	.input {
	    width: 200px;
	    float: left;
	    background: url(../../images/hire/input_l.gif) no-repeat left;
	    position: relative;
	    white-space: nowrap;
	    z-index:1;
    }
</style>
<%@ page import="com.hjsj.hrms.actionform.hire.employNetPortal.EmployPortalForm,com.hrms.struts.constant.SystemConfig,
			     org.apache.commons.beanutils.LazyDynaBean,com.hrms.hjsj.utils.Sql_switcher,com.hrms.hjsj.sys.Constant,
			     java.util.*,com.hrms.frame.codec.SafeCode,com.hjsj.hrms.utils.PubFunc,com.hjsj.hrms.businessobject.sys.SysParamBo,com.hjsj.hrms.businessobject.sys.SysParamConstant"%>
<html>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>

<%
	String dbtype="1";
  if(Sql_switcher.searchDbServer()== Constant.ORACEL)
  {
    dbtype="2";
  }
  else if(Sql_switcher.searchDbServer()== Constant.DB2)
  {
    dbtype="3";
  }
  String aurl = (String)request.getServerName();
	    String port=request.getServerPort()+"";
	    String prl=request.getScheme();
	    String url_p=prl+"://"+aurl+":"+port;
	    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	    String userViewName="";
	    String isHeadhunter ="";
	    if(userView!=null){
	        userViewName=userView.getUserName();
	        isHeadhunter = (String)userView.getHm().get("isHeadhunter");
	    }
	    Calendar c = Calendar.getInstance();
	    int hour = c.get(Calendar.HOUR_OF_DAY); 
	    
	    String passwordTransEncrypt = SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, "password_trans_encrypt");
  %>
   <style type="text/css">
	.img-middle{vertical-align:middle;}
</style>
<script language='javascript'>
var JQuery=$.noConflict();//将jquery起个别名防止和系统中自定义的$()方法冲突
var a0100 = "${employPortalForm.a0100}";
var cardid = "${employPortalForm.admissionCard}";
var nbase = "${employPortalForm.dbName}";
	function query()
	{
		<% int m=0;  %>
		<logic:iterate  id="element"    name="employPortalForm"  property="conditionFieldList" indexId="index"> 
		<% if(index<=2){ %>
				<logic:equal name="element" property="itemtype" value="D">
					var a<%=m%>=document.getElementsByName("conditionFieldList[<%=m%>].value")
					if(trim(a<%=m%>[0].value).length!=0)
					{						
						 var myReg =/^(-?\d+)(\.\d+)?$/
						 if(IsOverStrLength(a<%=m%>[0].value,10))
						 {
							 alert("<bean:write  name="element" property="itemdesc"/> "+DATE_FORMAT_IS_NOT_RIGHT+"！");
							 return;
						 }
						 else
						 {
						 	if(trim(a<%=m%>[0].value).length!=10)
						 	{
						 		 alert("<bean:write  name="element" property="itemdesc"/> "+DATE_FORMAT_IS_NOT_RIGHT+"！");
								 return;
						 	}
							var year=a<%=m%>[0].value.substring(0,4);
							var month=a<%=m%>[0].value.substring(5,7);
							var day=a<%=m%>[0].value.substring(8,10);
							if(!myReg.test(year)||!myReg.test(month)||!myReg.test(day)) 
						 	{
								 alert("<bean:write  name="element" property="itemdesc"/> "+DATE_FORMAT_IS_NOT_RIGHT+"！");
								 return;
						 	}
						 	if(year<1900||year>2100)
						 	{
						 		 alert("<bean:write  name="element" property="itemdesc"/> "+YEAR_SCORPE+"！");
								 return;
						 	}
						 	
						 	if(!isValidDate(day, month, year))
						 	{
								 alert("<bean:write  name="element" property="itemdesc"/> "+DATE_FORMAT_IS_NOT_RIGHT+"！");
								 return;
						 	}
						 }
					 }
				</logic:equal>				
				<logic:equal name="element" property="itemtype" value="N">
					var a<%=m%>=document.getElementsByName("conditionFieldList[<%=m%>].value")
					if(trim(a<%=m%>[0].value).length!=0)
					{						
						 var myReg =/^(-?\d+)(\.\d+)?$/
						 if(!myReg.test(a<%=m%>[0].value)) 
						 {
							alert("<bean:write  name="element" property="itemdesc"/>"+PLEASE_INPUT_NUMBER+"！");
							return;
						 }
					 }
				</logic:equal>		
				<logic:equal name="element" property="itemtype" value="A">
					<logic:equal name="element" property="codesetid" value="0">
						var a<%=m%>=document.getElementsByName("conditionFieldList[<%=m%>].value")
						if(trim(a<%=m%>[0].value).length!=0)
						{
							if(IsOverStrLength(a<%=m%>[0].value,<bean:write  name="element" property="itemlength"/>))
							{
								alert("<bean:write  name="element" property="itemdesc"/>"+OVER_LENGTH_SCOPE+"!");
								return;
							}
						}
					
					</logic:equal>
					<logic:notEqual name="element" property="codesetid" value="0">
						<logic:equal name="element" property="isMore" value="1">
						var a<%=m%>=document.getElementsByName("conditionFieldList[<%=m%>].value")
						var aa<%=m%>=document.getElementsByName("conditionFieldList[<%=m%>].viewvalue_view")
						if(trim(aa<%=m%>[0].value).length==0)
						{							
							a<%=m%>[0].value="";
						}
						</logic:equal>
					</logic:notEqual>
					
				</logic:equal>			
			<% m++;} %>	
		</logic:iterate>	
	
		document.employPortalForm.action="/hire/hireNetPortal/search_zp_position.do?b_query=link&returnType=search";
		document.employPortalForm.submit();
	}
	function change1(code,name,value,tt){
		var span=document.getElementById("spanf"+code);
		var floor= document.getElementById("floorf"+code);
		var input=document.getElementById("inputf"+code);
		var img=document.getElementById("img2f");
		var con=0;
		var ert=0;
		var has=false;
		if(name.length>=6){
			span.innerText =name.substring(0,7);
		}else{
			span.innerText =name;;
		}
			floor.style.display='none';
			var inpt=document.getElementsByName(tt)[0];
		inpt.value=value;
		}
		function change(code,name,value,tt){
		var span=document.getElementById("span"+code);
		var floor= document.getElementById("floor"+code);
		var input=document.getElementById("input"+code);
		var img=document.getElementById("img2");
		var con=0;
		var ert=0;
		var has=false;
		if(name.length>=6){
			span.innerText =name.substring(0,7);
		}else{
			span.innerText =name;;
		}
			floor.style.display='none';
			var inpt=document.getElementsByName(tt)[0];
		inpt.value=value;
		}
	function hide(code){
			Element.hide("floor"+code);
			var input=document.getElementById("input"+code)
			input.style.zIndex='0';
		}	
			
		function showlist(code){
			var floor= document.getElementById("floor"+code);
			floor.style.display='block';
				var input=document.getElementById("input"+code)
				input.style.zIndex='10000'
			
			floor.focus();
	}
	function showlist1(code){
			var floor= document.getElementById("floorf"+code);
			floor.style.display='block';
			var input=document.getElementById("inputf"+code)
			input.style.zIndex='10000'
			
			floor.focus();
	}
	function initCard()
{
      var rl = document.getElementById("hostname").href;     
      var aurl=rl;
      var DBType="<%=dbtype%>";
      var UserName="<%=userViewName%>";
      var obj = document.getElementById('CardPreview1');   
      var superUser="1";
      var menuPriv="";
      var tablePriv="";
      if(obj==null)
      {
         return false;
      }
      obj.SetSuperUser(superUser);
      obj.SetUserMenuPriv(menuPriv);
      obj.SetUserTablePriv(tablePriv);
      obj.SetURL(aurl);
      obj.SetDBType(DBType);
      obj.SetUserName(UserName);
      obj.SetUserFullName("su");
}	
  function previewTableByActive()
  {
   var hashvo=new ParameterSet();
   hashvo.setValue("dbname","${employPortalForm.dbName}");   
   hashvo.setValue("inforkind","1"); 
   hashvo.setValue("flag","hire");
   hashvo.setValue("id","${employPortalForm.a0100}"); 
   var request=new Request({method:'post',onSuccess:showPrint,functionId:'07020100078'},hashvo);
  }
 
</script>
<%
          EmployPortalForm employPortalForm=(EmployPortalForm)session.getAttribute("employPortalForm");
			HashMap hm = employPortalForm.getFormHM();
			String channelName = (String)hm.get("channelName");
			String isQueryCondition=employPortalForm.getIsQueryCondition();
			if(employPortalForm.getUserName()==null||employPortalForm.getUserName().equals("")){
				employPortalForm.setA0100("");
			}
			String a0100=employPortalForm.getA0100();
			ArrayList posDescFiledList=employPortalForm.getPosDescFiledList();
			String max_count=employPortalForm.getMax_count();
			String dbName=employPortalForm.getDbName();
			String hireChannel=employPortalForm.getHireChannel();
			String hireMajor=employPortalForm.getHireMajor();
			String hireMajorCode=employPortalForm.getHireMajorCode();
			String zpUnitCode=employPortalForm.getZpUnitCode();
			String type=request.getParameter("returnType");
			String hirechannel=employPortalForm.getHireChannel();
			String userName =employPortalForm.getUserName()==null?"":employPortalForm.getUserName();
			ArrayList conditionFieldList=employPortalForm.getConditionFieldList();
			String  reCommendoption = request.getParameter("reCommendoption");
		    String recommendA0100s = employPortalForm.getRecommendA0100s();
		    String recommendUserNames = employPortalForm.getRecommendUserNames();
			if(type==null)
			    type="1";
			String selunitcode=(String)request.getParameter("selunitcode");
			ArrayList unitList=employPortalForm.getUnitList();            
            LazyDynaBean kbean=null;
            ArrayList kll=new ArrayList();
            int hi=0;
            String username=employPortalForm.getUserName()==null?"":employPortalForm.getUserName();
            ArrayList boardlist=employPortalForm.getBoardlist();
            int lis=0;
           
            if(unitList!=null&&unitList.size()>0){
            	kbean=(LazyDynaBean)unitList.get(0);
            	for(int k=0;k<unitList.size();k++){
            		kbean=(LazyDynaBean)unitList.get(0);
            		kll=(ArrayList)kbean.get("list");
            		lis+=lis+kll.size()+1;
            	}
            	
           		if(a0100==null||(a0100!=null&&a0100.trim().length()==0)){
            			hi=lis*30+500;
	            		if(lis<=7){
	            			hi=7*30+500;
	            		}
            		
            	}else{
            		if(username.length()<=4){
	            		hi=lis*30+615;;
		            	if(lis<=7){
		            		hi=7*30+615;
		            	}
	            	}else{
		            	hi=lis*30+615;;
		            	if(lis<=7){
		            		hi=7*30+615;
		            	}
	            	}
	            	
            	}
            }else{
            	if(a0100==null||(a0100!=null&&a0100.trim().length()==0))
            		hi=7*30+500;
            	else{
            		hi=7*30+615;
            	}
            }
            
            String appliedPosItems = employPortalForm.getAppliedPosItems();
            String resume_state = "";
            String z0329_date = "";
            String z0333_domain = "";
            String z0315_amount = "";
            if(appliedPosItems.contains("resume_state")){
            	resume_state = "1";
            }
            if(appliedPosItems.contains("z0329")){
            	z0329_date = "1";
            }
            if(appliedPosItems.contains("z0333")){
            	z0333_domain = "1";
            }
            if(appliedPosItems.contains("z0315")){
            	z0315_amount = "1";
            }
 %>
 <script language='javascript'>
 	var selunitcode="<%=selunitcode%>";
 	var reCommendoption = "<%=reCommendoption%>";
 //	if(reCommendoption=='one')
 	{
 		var recommendA0100s='<%=recommendA0100s%>';
 		var recommendUserNames='<%=recommendUserNames%>';
 	}
</script>
 <body>
 <input type="hidden" id="hour" value="<%=hour %>"/>
<input type="hidden" id="pTransEncrypt" value="<%=passwordTransEncrypt %>"/>
 <div id="ly" style="position:absolute;top:0px;FILTER:alpha(opacity=0);background-color:#FFF;z-index:2;left:0px;display:none;"></div> 
<div id='wait1' style='position:absolute;top:285;left:80;display:none;'>
		<table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td class="td_style" height=24>
					激活邮件发送中,请稍候......
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10">
						<table cellspacing="1" cellpadding="0">
							<tr height=8>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
							</tr>
						</table>
					</marquee>
				</td>
			</tr>
		</table>
</div>
<div id="biaodan">


</div>
<div id='chajian' ></div>
<html:form action="/hire/hireNetPortal/search_zp_position"> 
<html:hidden name="employPortalForm" property="hireChannel"/>
<a href="<%=url_p%>" style="display:none" id="hostname">for vpn</a>
<%
          			if(a0100==null||(a0100!=null&&a0100.trim().length()==0)){//未登录时的职位详情是不可能由猎头招聘进来     	
     				 %>
     				 <div class="body">
    	
				         <div class="tcenter" id='tc'>
				        	<div class="center_bg" id='cms_pnl'>
							<div class="left" style="margin-bottom:120px">
              	<div class="login">
                    	<h2>&nbsp;&nbsp;&nbsp;&nbsp;用户登录</h2>
                        <div class="dl">
								<table width="197" border="0" cellspacing="0" cellpadding="0">
								  <tr>
								    <td>&nbsp;</td>
								  </tr>
								  <tr>
								    <td>&nbsp;&nbsp;<div class="input_bg"><span>邮&nbsp;&nbsp; 箱</span><input class="s_input" id="loginName"  type="text" onkeydown="KeyDown()" name="loginName" value='<bean:write name="employPortalForm" property="loginName" />' /></div></td>
								  </tr>
								  <tr>
								    <td><div class="input_bg"><span>密&nbsp;&nbsp; 码</span><input class="s_input" id="password" type="password" onkeydown="KeyDown()" value='<bean:write name="employPortalForm" property="password" />' 				                 
						             name="password" /></div></td>
								  </tr>
								  <tr>
								    <td>
								    	<div class="input_bg"><span>验证码</span><input class="s_input" id="validatecode" type="text" onkeydown="KeyDown()" value="" name="validatecode" /></div>
								    </td>
								  </tr>
								  
								  <tr>
								    <td align="right" style="padding:2px 0 2px 0;border-bottom:dotted 1px #c6c6c6;">
								    <img align="absMiddle" src="/servlet/vaildataCode?channel=0&codelen=4" id="vaildataCode">
								    <img align="absMiddle" src="/images/refresh.png" height="15" width="15" title="换一张" onclick="validataCodeReload()">
								    <img align="absMiddle" style="cursor:hand;" src="/images/hire/dl.gif" title="登录"  onclick='hireloginvalidate(0);'/>&nbsp;</td>
								  </tr>
								  <tr>
								    <td>
								    <span>
								   		 <logic:equal value="0" name="employPortalForm" property="isDefinitinn">
						                   <a href='javascript:T_BUTTON();'>注册</a>
						                  </logic:equal>
						                    <logic:equal value="1" name="employPortalForm" property="isDefinitinn">
						                   <a href='javascript:TR_BUTTON();'>注册</a>
						                  </logic:equal>
						               		<logic:equal value="1" name="employPortalForm" property="acountBeActived">
						                    |<a href="javascript:hireloginvalidate(1);">激活</a>						        
						                  </logic:equal>
											|<a href='javascript:getPasswordZPnew("<%=dbName%>","username","userpassword");'>忘记密码</a>
						             	  <logic:equal value="true" name="employPortalForm" property="accountFlag">
											|<a href='javascript:getZpAccounts();'>忘记帐号</a>
										  </logic:equal>

						                </span>
						             </td>
								  </tr>
								</table>
                        </div>
                    </div>
                    <%}  else { %>
                    <div class="body">
				    	
				        <div class="tcenter">
				        	<div class="center_bg" id='cms_pnl'>
							<div class="left" style="margin-bottom:120px">
				            		
                	<div class="login">
                    	 <div class="dl_1">
                    	      <div class="we"><b><bean:message key="hire.welcome.you"/>,
                           <%if(userName.length()>6){ %>
                           </b><b>
                           <% } %>
                           ${employPortalForm.userName}</b><bean:message key="hire.welcome.you.hint"/>
                           </div>
		            	      <ul class="dl_list">
		            	      	<%
		            	      		if(isHeadhunter!=null&&isHeadhunter.equals("1")){//进来的用户是猎头身份 
		            	      	%>
		            	      	<li><a href="/hire/hireNetPortal/recommend_resume.do?b_recommendResume=query"><bean:message key="hire.out.resume.recommend"/></a></li><!-- 推荐简历 -->
		            	      	<!--<li><a href="###"><bean:message key="hire.out.position.recommend"/></a></li><!-- 推荐岗位 -->
		            	      	<li><a href="/hire/hireNetPortal/search_zp_position.do?b_query=link&returnType=headHunter&hireChannel=headHire"><bean:message key="hire.out.position.employment"/></a></li><!-- 招聘岗位 -->
		            	      	<li><a href="/hire/hireNetPortal/search_zp_position.do?br_editPassword=edit"><bean:message key="label.banner.changepwd"/></a></li><!--修改密码 -->
		            	      	<%
		            	      		}else{
		            	      	%>
		            	        <li><a href='javascript:resumeBrowse("<%=dbName%>","<%=a0100%>")'><bean:message key="hire.browse.resume"/></a></li>
		            	       	<!-- linbz 20160506   屏蔽打印简历功能 -->
		            	       	<!--
		            	       		<logic:equal name="employPortalForm" property="canPrint" value="1">
		            	      		<li><a href='javascript:ysmethod("<bean:write name="employPortalForm" property="previewTableId"/>")'>打印简历</a></li>
		            	      	 	</logic:equal>
		            	      	 -->
		            	      	  <logic:equal value="true" name="employPortalForm" property="canPrintExamno">
									<li>
										<a href='javascript:printExamNo()'><bean:message key="hire.print.examcard"/></a>
									</li>
								</logic:equal>
								<logic:equal value="1" name="employPortalForm" property="canQueryScore">
                                   <li>
                                       <a href='javascript:showCard("","")'><bean:message key="hire.query.score"/></a>
                                   </li>
                                </logic:equal>
		            	        <li><a href="/hire/hireNetPortal/search_zp_position.do?b_showResumeList=show&setID=0&opt=1"><bean:message key="hire.my.resume"/></a></li>
		            	          <li><a href="javascript:void(0);"  onclick='hasresume();'><bean:message key="hire.browsed.position"/></a></li>
		            	        <li><a href="/hire/hireNetPortal/search_zp_position.do?b_applyedPosition=query"><bean:message key="hire.apply.position"/>
		            	        </a></li><!-- 应聘职位 -->
		            	        <li><a href="/hire/hireNetPortal/search_zp_position.do?br_editPassword=edit"><bean:message key="label.banner.changepwd"/></a></li>
		            	         <logic:equal value="1" name="employPortalForm" property="isDefinitionActive">
			            	        <logic:equal value="1" name="employPortalForm" property="activeValue">
			            	        	<li><a href='javascript:activeResume("<%=dbName%>","<%=a0100%>","${employPortalForm.activeValue}");'>关闭简历</a></li>
			            	        </logic:equal>
			            	        <logic:equal value="2" name="employPortalForm" property="activeValue">
			            	        	<li><a href='javascript:activeResume("<%=dbName%>","<%=a0100%>","${employPortalForm.activeValue}");'>激活简历</a></li>
			            	        </logic:equal>
		            	        </logic:equal>
		            	        <%
		            	      		}
		            	        %>
		            	        <li><a href="javascript:exit()">退出登录</a></li>
		          	        </ul>
		          	     </div>
		          	    </div>
                    <%} %>
                 
                    <div class="muen">
                    <h2>&nbsp;&nbsp;&nbsp;&nbsp;招聘单位</h2>
                    
                    	<logic:iterate id="unit" name="employPortalForm" property="unitList" indexId="index">
                    		<logic:equal value="<%=zpUnitCode%>" name="unit" property="codeitemid">
                    		  <div class="firstDiv"><table><tr><td align="left" valign="middle"><img src="/images/tree_collapse.gif" border="0" id="<bean:write name="unit" property="id_img"/>" style="cursor:hand" onclick='changeDisplay("<bean:write name="unit" property="codeitemid"/>","<bean:write name="unit" property="count"/>");'/><a href="/hire/hireNetPortal/search_zp_position.do?b_query=link&isAllPos=1&operate=init&hireChannel=${employPortalForm.hireChannel}&zpUnitCode=<bean:write name="unit" property="codeitemid"/>"><font class="firstFont"><bean:write name="unit" property="codeitemdesc"/></font> </a></td></tr></table></div>
                    		<ul class="col" id>
									<logic:iterate id="UnitSub" name="unit" property="list" indexId="indexid">
				                    			<logic:equal value="<%=zpUnitCode%>" name="UnitSub" property="codeitemid">
				                    				<li id="<bean:write name="UnitSub" property="id_r"/>"><a   class="one" title='<bean:write name="UnitSub" property="altdesc"/>' href="/hire/hireNetPortal/search_zp_position.do?b_query=link&isAllPos=1&operate=init&hireChannel=${employPortalForm.hireChannel}&zpUnitCode=<bean:write name="UnitSub" property="codeitemid"/>"><bean:write name="UnitSub" property="codeitemdesc"/></a></li>
				                    			</logic:equal>
				                    			<logic:notEqual value="<%=zpUnitCode%>" name="UnitSub" property="codeitemid">
				                    				<li id="<bean:write name="UnitSub" property="id_r"/>"><a title='<bean:write name="UnitSub" property="altdesc"/>' href="/hire/hireNetPortal/search_zp_position.do?b_query=link&isAllPos=1&operate=init&hireChannel=${employPortalForm.hireChannel}&zpUnitCode=<bean:write name="UnitSub" property="codeitemid"/>"><bean:write name="UnitSub" property="codeitemdesc"/></a></li>
				                    			</logic:notEqual>
				                    </logic:iterate>
			                    </ul>
                    		</logic:equal>
                    		<logic:notEqual value="<%=zpUnitCode%>" name="unit" property="codeitemid">
                    		 <div class="firstDiv"><table><tr><td align="left" valign="middle"><img src="/images/tree_collapse.gif" border="0" id="<bean:write name="unit" property="id_img"/>" style="cursor:hand" onclick='changeDisplay("<bean:write name="unit" property="codeitemid"/>","<bean:write name="unit" property="count"/>");'/><a href="/hire/hireNetPortal/search_zp_position.do?b_query=link&isAllPos=1&operate=init&hireChannel=${employPortalForm.hireChannel}&zpUnitCode=<bean:write name="unit" property="codeitemid"/>"><font class="firstFont"><bean:write name="unit" property="codeitemdesc"/></font> </a></td></tr></table></div>
                    		<ul class="col" id>
									<logic:iterate id="UnitSub" name="unit" property="list" indexId="indexid">
				                    			<logic:equal value="<%=zpUnitCode%>" name="UnitSub" property="codeitemid">
				                    				<li id="<bean:write name="UnitSub" property="id_r"/>"><a   class="one" title='<bean:write name="UnitSub" property="altdesc"/>' href="/hire/hireNetPortal/search_zp_position.do?b_query=link&isAllPos=1&operate=init&hireChannel=${employPortalForm.hireChannel}&zpUnitCode=<bean:write name="UnitSub" property="codeitemid"/>"><bean:write name="UnitSub" property="codeitemdesc"/></a></li>
				                    			</logic:equal>
				                    			<logic:notEqual value="<%=zpUnitCode%>" name="UnitSub" property="codeitemid">
				                    				<li id="<bean:write name="UnitSub" property="id_r"/>"><a title='<bean:write name="UnitSub" property="altdesc"/>' href="/hire/hireNetPortal/search_zp_position.do?b_query=link&isAllPos=1&operate=init&hireChannel=${employPortalForm.hireChannel}&zpUnitCode=<bean:write name="UnitSub" property="codeitemid"/>"><bean:write name="UnitSub" property="codeitemdesc"/></a></li>
				                    			</logic:notEqual>
				                    </logic:iterate>
			                    </ul>
                    		</logic:notEqual>
                    		
                    	</logic:iterate>                       
                    </div>
                     <div class="promt">
                     ${employPortalForm.promptContent}
                     </div>
                       </div>
                <div class="right4"  id='rg'  >
                <h2><%=channelName %></h2>
                <%
                  if(conditionFieldList!=null&&conditionFieldList.size()>0){ 
                %>
                   
					<div class="search">
						<h3>职位搜索</h3>
                     	 <div class="xia">
		           
								<%
								//人民大学职称要求为多选 z03a2职称要求，z0390职称要求隐藏代码
					            String title_Requirements = SystemConfig.getPropertyValue("title_Requirements");
					            String z03a2 = "";
					            String z0390 = "";
					            if(StringUtils.isNotEmpty(title_Requirements)&&title_Requirements.split(":").length==2) {
					            	z03a2 = title_Requirements.split(":")[0];
					            	z0390 = title_Requirements.split(":")[1];
					            }
								String codevalue="";//为了解决层级型代码选项出现后导致排列错格的问题 hidden也会作为一个页面元素占一个位置
								String selecnum="";
								boolean isprint=false;
								for(int i=0;i<conditionFieldList.size() && i<=2;i++)
								{
							      
									out.print("<span>");
									LazyDynaBean abean=(LazyDynaBean)conditionFieldList.get(i);
									String itemid=(String)abean.get("itemid");
									String itemtype=(String)abean.get("itemtype");
									String codesetid=(String)abean.get("codesetid");
									if("Z0385".equalsIgnoreCase(itemid))
										codesetid = "35";
									else if (z03a2.equalsIgnoreCase(itemid))
										codesetid = "DL";
									String isMore=(String)abean.get("isMore");
									String itemdesc=(String)abean.get("itemdesc");
									String value=(String)abean.get("value");
									String viewvalue=(String)abean.get("viewvalue");
									String viewvalue_view = StringUtils.isEmpty((String)abean.get("viewvalue_view")) ? "" : (String)abean.get("viewvalue_view");
									out.print(""+itemdesc+":</span>");
									if(itemtype.equals("A"))
									{
										if(codesetid.equals("0"))
										{	
											if(itemid.equalsIgnoreCase(hireMajor) && !(hireMajorCode==null || hireMajorCode.equals("-1"))){
												out.print("<div class=\"input_bg2\" style='width:110px;'><input  class='TEXT' type='text' name='conditionFieldList["+i+"].viewvalue_view' value='"+viewvalue_view+"'  size='10'  style='width:95px'     />"
		                                                   +"<span  style='float:right;margin-top:-19px;position:relative;'>"
		                                                   		+"<input type='hidden' name='conditionFieldList["+i+"].viewvalue_value'/>"
		                                                   		+"<img style='float:right;' class='img-middle'  src='/module/recruitment/image/xiala2.png' plugin='codeselector' codesetid='"+hireMajorCode+"' ctrltype='0' inputname='conditionFieldList["+i+"].viewvalue_view'  afterfunc='dealRes(\""+i+"\",\"conditionFieldList\")'/>"
		                                                   +"</span></div>");
												//   out.println("<input type='text' style='display:none;' name='conditionFieldList["+i+"].value' value='"+value+"'  />&nbsp;"); 
												   isprint=true;
									   			   selecnum=selecnum+i+"@"+value+"`";
											}
											else
											  out.println("<div class=\"search_input_bg\"><input name=\"conditionFieldList["+i+"].value\"  class='textbox' type=\"text\" value=\""+value+"\" size='18' /></div>");											
										}
										else
										{
											if("0".equals(isMore)&&false)
											{
												ArrayList options=(ArrayList)abean.get("options");
												out.print("<div class='input' id='input"+i+"'");												
												out.print(" style='width:122px'>");	
												out.print("<img  src='../../images/hire/input_l.gif' width='100%' height='100%' style='position:absolute;z-index:-999'/>");
												out.print(" <div class='floor' id='floor"+i+"' onblur=\" hide('"+i+"');\"> ");
												String selected="";
												String selectedvalue="";
												out.println("<a  onclick=\"javascript:change("+i+",'"+"全部"+"','"+""+"','conditionFieldList["+i+"].value');\" style='FONT-SIZE: 12px;font-family: 微软黑体;color:black;cursor:hand'>");
												out.print("  全部"+"</a><br>");
												for(int n=0;n<options.size();n++)
												{
													LazyDynaBean a_bean=(LazyDynaBean)options.get(n);
													String avalue=(String)a_bean.get("value");
													String aname=(String)a_bean.get("name");
													out.println("<a  onclick=\"javascript:change("+i+",'"+aname+"','"+avalue+"','conditionFieldList["+i+"].value');\" style='FONT-SIZE: 12px;font-family: 微软黑体;color:black;cursor:hand'>");
													if(avalue.equals(value)){
														selected=aname;
														selectedvalue=avalue;
														if(selected.length()>=7){
															selected=selected.substring(0,7);
														}
													}
													out.print(" "+aname+"</a>");
													if(n!=options.size()-1){
														out.print("<br>");
													}
												}
												if(selected.trim().length()==0){
													selected="全部";
												}
												//out.print("</select>");
												out.print("<input type='hidden' name='conditionFieldList["+i+"].value' value='"+selectedvalue+"'/>");
												out.print("</div><span id='spank"+i+"' class='img'>");
												out.print("<a href='javascript:void(0);' onclick='showlist("+i+");'><img src='/images/hire/xia.gif'/></a>");
												out.print(" </span><span style='padding-right:0px;margin-right:-2px;margin-left:1.9px' FONT-SIZE: 12px;font-family: 微软黑体;color:black; id='span"+i+"'>");	
												out.print(selected+" </span></div>");	
											}
											else
											{
												out.print("<div class=\"input_bg2\" style='width:110px;'><input  class='TEXT' type='text' name='conditionFieldList["+i+"].viewvalue_view' value='"+viewvalue_view+"'  size='10'  style='width:95px'     />"
		                                                   +"<span  style='float:right;margin-top:-19px;position:relative;'>");
                                                out.print("<img style='float:right;margin-top:-1px;' class='img-middle' src='/module/recruitment/image/xiala2.png' plugin='codeselector' isHideTip = 'true' codesetid='"+codesetid+"' inputname='conditionFieldList["+i+"].viewvalue_view' ");
   												if("un".equalsIgnoreCase(codesetid)||"um".equalsIgnoreCase(codesetid)||"@K".equalsIgnoreCase(codesetid))
   													out.print(" ctrltype='0' ");
   												//人大要求快速查询只显示有职位的单位
   												if("un".equalsIgnoreCase(codesetid))
   													out.print(" codesource='GetZPOrganization' ");
   												if("z0385".equalsIgnoreCase(itemid)||z03a2.equalsIgnoreCase(itemid))
   													out.print(" multiple='true' ");
   												out.print(" afterfunc='dealRes(\""+i+"\",\"conditionFieldList\")'/>");
   												out.print("<input type='hidden' name='conditionFieldList["+i+"].viewvalue_value'/>");
   												out.print("</span></div>"); 
								             	isprint=true;
								   				selecnum=selecnum+i+"@"+value+"`";
								   				
											}
										
										}
									
									}
									else if(itemtype.equals("D"))
									{
										out.println("<div class=\"input_bg1\" style='width:110px;'><input  name='conditionFieldList["+i+"].value' class='TEXT' type='text' style='width:100px;FONT-SIZE: 12px;font-family: 微软黑体;color:black;'   size='15' value='"+value+"'  onclick='popUpCalendar(this,this, dateFormat,\"\",\"\",true,false)'/></div>");
									
									}
									else if(itemtype.equals("N"))
									{
										out.println("<div class=\"input_bg1\" style='width:110px;'><input name=\"conditionFieldList["+i+"].value\" class='TEXT' type=\"text\" style='width:100px;FONT-SIZE: 12px;font-family: 微软黑体;color:black;    value=\""+value+"\"   size='15'   /></div>");
									}
								
								}
								
								
								%>							
							<a href="javascript:query(1);" style="margin-left:10px" id="img2"><img src="/images/hire/sarch.gif" /></a> 
						         <%
						         		if(isprint){
						         			if(null!=selecnum&&selecnum.indexOf("`")!=-1&&selecnum.indexOf("@")!=-1){
						         				String []temp=selecnum.split("`");
						         				for(int t=0;t<temp.length;t++){
						         				    String tt=temp[t];
						         				    if(tt.trim().length()==0)
						         				    	continue;
						         					String []gg=tt.split("@");
						         					if(gg.length==1){
						         					out.println("<input type='text' style='display:none;' name='conditionFieldList["+gg[0]+"].value' value=''  />&nbsp;"); 
						         					}else{
						         					out.println("<input type='text' style='display:none;' name='conditionFieldList["+gg[0]+"].value' value='"+gg[1]+"'  />&nbsp;"); 
						         					}
						         				
						         				}
						         			}
										
										}
						          %>    	
						             	
                      </div>
                   </div>
                   <%} %>
                    <div class="jj zw">
		                    	<h2><span>职位信息</span></h2>
		                        <div class="nr">
		                        <table width="0" border="0" cellspacing="0" cellpadding="0" class="table table3">
			                       
	             					 <tr > 
				                       <TD align="left" height="25" class='table_backGround'>
				                       <table>
					                       <tr>
					                       	<td align="left" height='26'>
					                       &nbsp;&nbsp;&nbsp;
					                        <font class='hire_posdesc_title'><bean:write name="employPortalForm" property="posDesc"/></font>
					                        <logic:equal value="1" name="employPortalForm" property="isConfigExp">
					                        <logic:equal value="1" name="employPortalForm" property="isHaveExp">
					                      	 <a href='/servlet/performance/fileDownLoad?e01a1=${employPortalForm.positionID}&opt=hire' style="color: #444A58;"  target="_blank"  border='0' >(查看岗位说明书)</a>
					                        </logic:equal>
					                        </logic:equal>
					                      
					                        </td>
				                      	 	</tr>
			                     		</table>
                       			 	</td>
                       				</tr>
                     		<tr > 
                        <td align="center" >   
                          		<table width='100%' border=0 class="hire_posdesc_table" cellpadding=0 cellspacing=0 ><!-- 职位的信息输出 -->
                          			<%
                          			for(int i=0;i<posDescFiledList.size()-1;i++)
									{                          			
                          				LazyDynaBean abean=(LazyDynaBean)posDescFiledList.get(i);
                          				LazyDynaBean nextBean=null;
                          				if((i+1)<posDescFiledList.size())
                          					nextBean=(LazyDynaBean)posDescFiledList.get(i+1);
                          				 String desc=(String)abean.get("desc");
                          				 String desc2="";
                          				 if(desc.length()==2)
                          				    desc=desc.charAt(0)+""+desc.charAt(1);	
                          				 if(nextBean!=null&&((String)nextBean.get("desc")).length()==2)
                          					desc2=((String)nextBean.get("desc")).charAt(0)+""+((String)nextBean.get("desc")).charAt(1);
                          				 else if(nextBean!=null)
								            desc2=((String)nextBean.get("desc"));
								                          			
                          				out.println("<tr>");	
                          				if((((String)abean.get("type")).equals("A")||((String)abean.get("type")).equals("N"))&&nextBean!=null&&(((String)nextBean.get("type")).equals("A")||((String)nextBean.get("type")).equals("N")))	
                          				{
                          					String value="";
                          					String nextValue="";
                          					if(abean.get("value")!=null)
                          						value=(String)abean.get("value");
                          					if(nextBean.get("value")!=null)
                          						nextValue=(String)nextBean.get("value");
                          						if(value==null||value.equals(""))
                          						   value="&nbsp;";
                          						if(nextValue==null||nextValue.equals(""))
                          						   nextValue="&nbsp;";
                          					out.println("<td height='30'  width='15%'class='hire_posdesc_body1'>"+desc+":&nbsp;</td>");
	                          				out.println("<td  width='35%' class='hire_posdesc_body2'>"+value+"</td>");
	                          				out.println("<td  width='10%' class='hire_posdesc_body1'>"+desc2+":&nbsp;</td>");
	                          				out.println("<TD  width='40%'  class='hire_posdesc_body2'>"+nextValue+"</TD>");
	                          				i++;
                          				}
                          				else
                          				{
                          				    String avalue="&nbsp;";
                          				    if(abean.get("value")!=null)
	                          				    avalue=(String)abean.get("value");
                          				    avalue=avalue.replaceAll("\r\n","<br>");
                          				    avalue=avalue.replaceAll("\n\n","<br>");
                          				    if(avalue.equals(""))
                          				        avalue="&nbsp;";
                          				    if(((String)abean.get("type")).equals("M"))
                          				    {
                          						out.println("<td height='30' colspan=1 width='15%' class='hire_posdesc_body1' valign='top'>"+desc+":&nbsp;</td>");
                          						out.println("<td colspan=3 valign='top' class='hire_posdesc_body2' >"+avalue+"</td>");
                          					}
                          					else
                          					{
                          						out.println("<td height='30' colspan=1 class='hire_posdesc_body1' width='15%' nowrap>"+desc+":&nbsp;</td>");
                          						out.println("<td   colspan=3 class='hire_posdesc_body2' >"+avalue+"</td>");
                          					}
                          				}
                          				
                          				out.print("</tr>");
                          			 
									}
									%>                          		
                          		
                          		</table>
                        </td>
                        
                      </tr>
                 
                   <tr><td >
                   <br>
                   <table width="100%" border=0 cellpadding=0 cellspacing=0 >
                    <tbody>
                      <tr> 
                        <td style="TEXT-ALIGN:center"><!-- 申请岗位||返回图片 -->
                        		<%
                        			if(isHeadhunter!=null&&isHeadhunter.equals("1")){//猎头登录,查看职位信息
                        				if("recommend".equals(type)){//推荐岗位 
                        		%>
                       					<img border="0"  src="/images/hire/recommend.gif" alt = "<bean:message key="button.return"/>"  style="cursor:hand" onClick="recommendCheck('<%=request.getParameter("z0301") %>','<%=request.getParameter("posName")%>');">&nbsp;
                        		<%
                        				}
                        		%>
                        			<!-- 这里留下接口 ,防止以后要添加一个功能 -->
                        			
                        			<img border="0"  src="/images/hire/return.gif" alt = "<bean:message key="button.return"/>"  style="cursor:hand" onClick='goback("<%=type%>");'>&nbsp;
                        		<%		
                        			}else if(a0100==null||(a0100!=null&&a0100.trim().length()==0)){//未登录时,不可能是猎头传进来 
                        		%>
                        			<img border="0"  src="/images/hire/iapp.gif" alt = "<bean:message key="hire.jp.apply.apllypos"/>"  style="cursor:hand"  onClick='apply("${employPortalForm.isApplyedPos}","${employPortalForm.a0100}","${employPortalForm.userName}","<%=request.getParameter("posID") %>","<%=request.getParameter("z0301") %>","${employPortalForm.person_type}","${employPortalForm.loginName}","<%=type%>","<%=hireChannel %>");'>
                      				&nbsp;&nbsp;<img border="0"  src="/images/hire/return.gif" alt = "<bean:message key="button.return"/>"  style="cursor:hand" onClick='goback("<%=type%>");'>&nbsp;
                        		<%		
                        			}else if(hireChannel.equals("headHire")&&!(isHeadhunter!=null&&isHeadhunter.equals("1"))){//非猎头登录,在猎头招聘产看职位信息 
                        	    %>
                        	    			<img border="0"  src="/images/hire/return.gif" alt = "<bean:message key="button.return"/>"  style="cursor:hand" onClick='goback("<%=type%>");'>&nbsp;
                        	    <%
                        			}else{//非猎头登录在社会活校园查看职位信息
                        		%>
                        			<logic:equal value="0" name="employPortalForm" property="isApplyedPos">
                        				<img border="0"  src="/images/hire/iapp.gif" alt = "<bean:message key="hire.jp.apply.apllypos"/>"  style="cursor:hand"  onClick='apply("${employPortalForm.isApplyedPos}","${employPortalForm.a0100}","${employPortalForm.userName}","<%=request.getParameter("posID") %>","<%=request.getParameter("z0301") %>","${employPortalForm.person_type}","${employPortalForm.loginName}","<%=type%>","<%=hireChannel %>");'>
                        			</logic:equal>
                      				&nbsp;&nbsp;<img border="0"  src="/images/hire/return.gif" alt = "<bean:message key="button.return"/>"  style="cursor:hand" onClick='goback("<%=type%>");'>&nbsp;
                        		<%
                        			}
                        		%>
                      	</td>
                      </tr>
                    </tbody>
                    </table>
                   </td>
                   </tr>
                     </table>
                     
		             </div>
		           </div>
		            <% if(a0100!=null&&a0100.trim().length()>1&&isHeadhunter!=null&&!isHeadhunter.equals("1")){  %>
		           <div class="jj zw">
		                  <h2><span>已申请职位</span></h2>
		                        <div class="nr">
		              <table width="0" border="0" cellspacing="0" cellpadding="0" class="table table3">
                          <TBODY>                    
                      <tr> 
                        <td  height="25" class='hire_posTable_head'>
	                        <b>
		                       <bean:message key="hire.out.position.desc"/>
	                       </b>
                      	</td>
                       <%
                    if(resume_state.equalsIgnoreCase("1")){
                   %>	
                      	<td height="25" class='hire_posTable_head'>
                      		<b>
                      		<bean:message key="hire.employActualize.resumeState"/>   
                      		</b>      			
                      	</td>
                    <%
                 	}
                   if(z0329_date.equalsIgnoreCase("1")){
                  %>
                      	<td  height="25" class='hire_posTable_head'>
                      		<b>
                      		<bean:message key="lable.zp_plan.start_date"/>  
                      		</b>          			
                      	</td>
                      	
                      	<!-- 
                      	<td class=rptHead 
                            background=/images/r_titbg01.gif >
                      		<font color='#DF0024'>结束日期</font>          			
                      	</td>
                      	-->
                      <%
                 	}
                   if(z0333_domain.equalsIgnoreCase("1")){
                  %>
                      	<td  height="25" class='hire_posTable_head'>
                      	<b>
                      		<bean:message key="lable.zp_plan_detail.domain"/> 
                      		</b>             			
                      	</td>
                    <%
                 	}
                   if(z0315_amount.equalsIgnoreCase("1")){
                  %>
                      	<td  height="25" class='hire_posTable_head'>
                      	<b>
                      		<bean:message key="lable.zp_plan_detail.amount"/>    
                      		</b>      			
                      	</td>
                     <%} %>	
                      	<logic:notEqual value="1" name="employPortalForm" property="max_count">
                      	<td  height="25" class='hire_posTable_head'>
                      		<b>
                      		<bean:message key="hire.wish.order"/>    
                      		<b>       			
                      	</td>
                      	</logic:notEqual>
                      </tr>
                      <%
                      ArrayList  applyedPosList = employPortalForm.getApplyedPosList();
                      for(int i=0;i<applyedPosList.size();i++)
                      {
                          LazyDynaBean abean=(LazyDynaBean)applyedPosList.get(i);
                          String posName=(String)abean.get("posName");//取得是职位名称 
                          String unitName=(String)abean.get("unitName");//取得是简历状态无语了 
                          String z0329=(String)abean.get("z0329");//开始日期
                          String z0333=(String)abean.get("z0333");//工作地点
                          String z0315=(String)abean.get("z0315");//招聘人数 	
						  String styleClass="hj_zhaopin_list_tab_titletwo";
                          if(posName==null||posName.trim().equals(""))
                              posName="&nbsp;";
                          if(unitName==null||unitName.trim().equals(""))
                              unitName="&nbsp;";
                          if(z0329==null||z0329.trim().equals(""))
                             z0329="&nbsp;";
                          if(z0333==null||z0333.trim().equals(""))
                             z0333="&nbsp;";
                          if(z0315==null||z0315.trim().equals(""))
                             z0315="&nbsp;";
                             out.println("<tr>");
                          if(i%2==0){
                           	  
	                          out.println("<td height='36' class='hire_posTable_body1' > "+posName+"</td>");
	                          if(resume_state.equalsIgnoreCase("1")){
	                          	out.println("<td  class='hire_posTable_body1'> "+unitName+"</td>");
	                          }
	                          if(z0329_date.equalsIgnoreCase("1")){
	                          	out.println("<td  class='hire_posTable_body1'> "+z0329+"</td>");
	                          }
	                          if(z0333_domain.equalsIgnoreCase("1")){
	                          	out.println("<td  class='hire_posTable_body1'> "+z0333+"</td>");
	                          }
	                          if(z0315_amount.equalsIgnoreCase("1")){
	                          	out.println("<td class='hire_posTable_body1'> "+z0315+"</td>");
	                          }
	                          if(!max_count.equals("1"))
	                          {
	                              String thenumber=(String)abean.get("thenumber");
	                              if(thenumber==null||thenumber.trim().equals(""))
	                                 thenumber="&nbsp;";
	                              out.println("<td  class='hire_posTable_body1'> "+thenumber+"</td>");
	                          }
                          }else{
                          		
	                          out.println("<td height='36'  class='hire_applyTable_body2'> "+posName+"</td>");
	                          if(resume_state.equalsIgnoreCase("1")){
	                          	out.println("<td  class='hire_posTable_body2'> "+unitName+"</td>");
	                          }
	                          if(z0329_date.equalsIgnoreCase("1")){
	                          	out.println("<td  class='hire_posTable_body2'> "+z0329+"</td>");
	                          }
	                          if(z0333_domain.equalsIgnoreCase("1")){
	                          	out.println("<td  class='hire_posTable_body2'> "+z0333+"</td>");
	                          }
	                          if(z0315_amount.equalsIgnoreCase("1")){
	                          	out.println("<td  class='hire_posTable_body2'> "+z0315+"</td>");
	                          }
	                          if(!max_count.equals("1"))
	                          {
	                              String thenumber=(String)abean.get("thenumber");
	                              if(thenumber==null||thenumber.trim().equals(""))
	                                 thenumber="&nbsp;";
	                              out.println("<td   class='hire_posTable_body2'> "+thenumber+"</td>");
	                          }
                          }
                          
                          out.println("</tr> ");
                      }
                      
                       %>
                     </TBODY>
                   </TABLE>
		                        </div>
		                        </div>
		                        <%} %>
                </div>
                 <div class='footer' style='height:0px;'> &nbsp;&nbsp;</div>
                </div>
               
                </div>
                </div>
                
                </div>
                
                </div>
                
                </div>
                </div>
                
<!-- 调用这一块的功能给去掉了,因此是不会显示了 begin-->  				
<div id="mapLayer" style="display: none;"><!--弹出的登录页面层，此不是登录，而为一个地图页面-->
		<h3><font style='color:white'>登录/注册</font></h3>
			<div class="closeMap" onclick="javascript:closeMap();"><!--登录页面的标题以及关闭按钮-->
		<img src="/images/hire/dmlclose.png" 
			alt="关闭窗口" />
		</div>
		<div class="mapArea"><!--地图区域，这里就可以改为 登录页面 哦-->
			<table style='width:80%;margin-left:100px;margin-top:35px'>
			<tr>
			<td colspan='2'>
				<font style="font-weight:   bold">&gt;&gt;我已经是注册用户</font>
			</td>
			</tr>
			<tr>
			<td align="right">
				<bean:message key="hire.email.address"/>：&nbsp;&nbsp;&nbsp;&nbsp;
			</td>
			<td align="left"> 
				<div class='input_bg1'><INPUT class=s_input id=loginNamey  name=loginNamey size="20">
			</td>
			</tr>
			<tr>
			<td align="right">
				<bean:message key="label.mail.password"/>：&nbsp;&nbsp;&nbsp;&nbsp;
			</td>
			<td align="left"> 
				<div class='input_bg1'><INPUT class=s_input id=passwordy type=password name=passwordy size="20"></div>
			</td>
			</tr>
			<tr>
			<td align="right">
			<input type="checkbox" name="remenber" value="1" id="remenberme"/><font class='FontStyle'>记住我</font>
			</td>
			<td>
			 &nbsp;&nbsp;&nbsp;&nbsp;<a href="javascript:void(0);" onclick='javascript:getPasswordZPnew("<%=dbName%>","username","userpassword");'>忘记密码？</a>
			</td>
			</tr>
			
			<tr>
			<td colspan='2'>
				&nbsp;&nbsp;&nbsp;
			</td>
			</tr>
			<tr>
			<td align="right">
									 <img style="cursor:hand;" src="/images/hire/dl.gif" title="登录"  onclick='login1(2,"${employPortalForm.isApplyedPos}","<%=request.getParameter("posID") %>","<%=request.getParameter("z0301") %>","<%=PubFunc.hireKeyWord_filter(SafeCode.decode(request.getParameter("posName")))%>");'/>
									 &nbsp;
			</td>
			<td align="left"> 
				&nbsp;&nbsp;
			</td>
			</tr>
			<tr>
			<td colspan='2'>
				<img src='/images/hire/hireline.jpg'>
			</td>
			</tr>
			<tr>
				<tr>
			<td colspan='2'>
				&nbsp;&nbsp;&nbsp;
			</td>
			</tr>
			<td colspan='2'>
				<font style="font-weight:   bold">&gt;&gt;我还不是注册用户</font>
			</td>
			</tr>
			<tr>
			<td colspan='2'>
				 &nbsp;&nbsp;马上注册并填写简历
			</td>
			</tr>
			<tr>
			<td colspan='2'>
				&nbsp;&nbsp;&nbsp;
			</td>
			</tr>
			<tr>
			
			<td align="right">
							<logic:equal value="0" name="employPortalForm" property="isDefinitinn">
								<a href='javascript:T_BUTTON();'><img src="/images/hire/zhce.gif" title="注册" /></a>
							</logic:equal>
							<logic:equal value="1" name="employPortalForm" property="isDefinitinn">
								  <a href='javascript:TR_BUTTON();'><img src="/images/hire/zhce.gif" title="注册" /></a>
							</logic:equal>			</td>
			<td align="left"> 
				
			</td>
			</tr>
			</table>
	</div>
	</div>
	<div id="mapBgLayer" style="position:absolute; display: none;"></div><!--屏蔽层，用来透明的屏蔽整个页面-->
	<!-- 调用这一块的功能给去掉了,因此是不会显示了 end-->  	


</html:form>
</body>
</html>
 <script language="javascript">
         initCard();
         function dealRes(i,name){
        	 document.getElementsByName(name+"["+i+"].viewvalue_view")[0].value = replaceAll(document.getElementsByName(name+"["+i+"].viewvalue_view")[0].value, "|", ",");
        		document.getElementsByName(name+"["+i+"].value")[0].value = document.getElementsByName(name+"["+i+"].viewvalue_value")[0].value;
        	}
</script> 