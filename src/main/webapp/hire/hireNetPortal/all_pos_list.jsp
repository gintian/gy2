 <%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<script language="JavaScript" src="/general/sys/hjaxmanage.js"></script>
<script language="JavaScript" src="/hire/hireNetPortal/employNetPortal.js"></script>
<script type="text/javascript" src="/jquery/jquery-3.3.1.min.js"></script>

<%@ page import="com.hjsj.hrms.actionform.hire.employNetPortal.EmployPortalForm,
			     org.apache.commons.beanutils.LazyDynaBean,com.hrms.hjsj.utils.Sql_switcher,com.hrms.hjsj.sys.Constant,
			     java.util.*,com.hjsj.hrms.businessobject.sys.SysParamBo,com.hjsj.hrms.businessobject.sys.SysParamConstant"%>
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
   if(userView!=null)
       userViewName=userView.getUserName();
   Calendar c = Calendar.getInstance();
   int hour = c.get(Calendar.HOUR_OF_DAY); 
   
   String passwordTransEncrypt = SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, "password_trans_encrypt");
  %>
<script language='javascript'>
var JQuery=$.noConflict();//将jquery起个别名防止和系统中自定义的$()方法冲突
var a0100 = "${employPortalForm.a0100}";
var cardid = "${employPortalForm.admissionCard}";
var nbase = "${employPortalForm.dbName}";

function searchPos()
{
   document.employPortalForm.action="/hire/hireNetPortal/search_zp_position.do?b_searpos=init";	 
   document.employPortalForm.submit();
}
function showlist(code){
			var floor= document.getElementById("floor"+code);
			var input=document.getElementById("input"+code)
			var tab=document.getElementById('tablee');
			var rows=tab.rows;
			//for(var i=0;i<rows.length;i++){
				
				//if(document.getElementById("input"+i)){
				//	var inp=document.getElementById("input"+i);
				//	if(inp.className=='input'){
				//		if(i<code+4&&i>code){
				//			inp.style.display='none';
				//		}
				//	}
				//}
				
					
				
			//}
			
			floor.style.display='block';
			input.style.zIndex='10000'
			floor.focus();
	}
	function change1(code,name,value,tt){

		var span=document.getElementById("span"+code);
		var floor= document.getElementById("floor"+code);
		var input=document.getElementById("input"+code);
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
		//	var tab=document.getElementById('tablee');
		//	var rows=tab.rows;
		//	alert(code);
		//	for(var i=0;i<rows.length;i++){
				
		//		if(document.getElementById("input"+i)){
			//		var inp=document.getElementById("input"+i);
			//		if(inp.className=='input'){
			//			if(i<code+4&&i!=code){
			//				inp.style.display='block';
			//			}
			//		}
			//	}
			//}
			var input=document.getElementById("input"+code)
			input.style.zIndex='0';
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
<div id="biaodan"></div>
<div id='chajian' ></div>
<form name="employPortalForm" method="post" action="/hire/hireNetPortal/search_zp_position.do">
<%
            EmployPortalForm employPortalForm=(EmployPortalForm)session.getAttribute("employPortalForm");
			if(employPortalForm.getUserName()==null||employPortalForm.getUserName().equals("")){
				employPortalForm.setA0100("");
			}
			String a0100=employPortalForm.getA0100();
			String dbName=employPortalForm.getDbName();
			ArrayList commQueryList=employPortalForm.getCommQueryList();
			String hirechannel=employPortalForm.getHireChannel();
			String zpUnitCode=employPortalForm.getZpUnitCode();
			String hireMajorCode=employPortalForm.getHireMajorCode();
			String hireMajor=employPortalForm.getHireMajor();
			String userName =employPortalForm.getUserName()==null?"":employPortalForm.getUserName();
			ArrayList unitList=employPortalForm.getUnitList();
            LazyDynaBean kbean=null;
            ArrayList kll=new ArrayList();
             int hi=0;  
            int lis=0;
            String username=employPortalForm.getUserName()==null?"":employPortalForm.getUserName();
			ArrayList boardlist=employPortalForm.getBoardlist();
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
		    %>
<html:hidden name="employPortalForm" property="isDefinitionActive"/>
<html:hidden name="employPortalForm" property="hireChannel"/>
<html:hidden name="employPortalForm" property="zpUnitCode"/>
<a href="<%=url_p%>" style="display:none" id="hostname">for vpn</a>
	<%
          			if(a0100==null||(a0100!=null&&a0100.trim().length()==0)){     	
     				 %>
     				 <div class="body">
    	
				         <div class="tcenter" id='tc'>
				        	<div class="center_bg" id='cms_pnl'>
				            	<div class="left">
            		
                	<div class="login">
                    	<h2>&nbsp;&nbsp;&nbsp;&nbsp;用户登录</h2>
                        <div class="dl">
								<table width="206" border="0" cellspacing="0" cellpadding="0">
								  <tr>
								    <td>&nbsp;</td>
								  </tr>
								  <tr>
								    <td><div class="input_bg"><span>邮&nbsp;&nbsp; 箱</span><input class=s_input id=loginName onkeydown="KeyDown()" name=loginName  ></div></td>
								  </tr>
								  <tr>
								    <td><div class="input_bg"><span>密&nbsp;&nbsp; 码</span><input class=s_input id=password type=password  onkeydown="KeyDown()"				                 
						             name=password ></div></td>
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
								  <tr>
								    <td><span>
								    <logic:equal value="0" name="employPortalForm" property="isDefinitinn">
						                   <a href='javascript:T_BUTTON();'>注册</a>| 
						                  </logic:equal>
						                    <logic:equal value="1" name="employPortalForm" property="isDefinitinn">
						                   <a href='javascript:TR_BUTTON();'>注册</a>|
						                  </logic:equal>
						                <a href='javascript:getPasswordZPnew("<%=dbName%>","username","userpassword");'>找回密码</a>| 
						                <logic:equal value="1" name="employPortalForm" property="acountBeActived">
										
						                 <a href="javascript:hireloginvalidate(1);">激活账号</a>| 
						        		
						                 </logic:equal>
						                
						                </span>
						             </td>
								  </tr>
								</table>
                        </div>
                    </div>
                    <%}  else { %>
                    <div class="body">
				    	
				        <div class="tcenter" id='tc'>
				        	<div class="center_bg" id='cms_pnl'>
				            	<div class="left">
				            		
                	<div class="login">
                    	 <div class="dl_1">
                    	   <div class="we"><b><bean:message key="hire.welcome.you"/>,
                           <%if(userName.length()>6){ %>
                           </b><b>
                           <% } %>
                           ${employPortalForm.userName}</b><bean:message key="hire.welcome.you.hint"/></div>
		            	      <ul class="dl_list">
		            	        <li><a href='javascript:resumeBrowse("<%=dbName%>","<%=a0100%>")'><bean:message key="hire.browsed.resume"/></a></li>
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
		            	        <li><a onclick='hasresume();'><bean:message key="hire.browsed.position"/></a></li>
		            	        <li><a href="/hire/hireNetPortal/search_zp_position.do?b_applyedPosition=query"><bean:message key="hire.apply.position"/>
		            	        </a></li>
		            	        <li><a href="/hire/hireNetPortal/search_zp_position.do?br_editPassword=edit"><bean:message key="label.banner.changepwd"/></a></li>
		            	         <logic:equal value="1" name="employPortalForm" property="isDefinitionActive">
			            	        <logic:equal value="1" name="employPortalForm" property="activeValue">
			            	        	<li><a href='javascript:activeResume("<%=dbName%>","<%=a0100%>","${employPortalForm.activeValue}");'>关闭简历</a></li>
			            	        </logic:equal>
			            	        <logic:equal value="2" name="employPortalForm" property="activeValue">
			            	        	<li><a href='javascript:activeResume("<%=dbName%>","<%=a0100%>","${employPortalForm.activeValue}");'>激活简历</a></li>
			            	        </logic:equal>
		            	        </logic:equal>
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
                    		<ul class="col">
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
                    		 <ul class="col">
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
                 <%if(hirechannel.equals("02")){ %>
                <div class="right"   style='min-height:<%=hi %>px;'>
                <h2></h2>
                <%}else{ %>
                 <div class="right1"   style='min-height:<%=hi %>px;'>
                 <h1></h1>
                <%} %>
                	      <div class="jj zw">
							<h3><%if(hirechannel.equals("02")){%>职位<%}else{%>专业<%}%>搜索</h3>
								 <div class="nr" >
                					<table cellSpacing=0 cellPadding=0  width=100% id='tablee' class="table table3">
									<%
									for(int i=0;i<commQueryList.size();i++)
									{
									   
								        out.print("<tr><td width='30%' height='34' align='right' class=tdTitle nowrap>");	
										LazyDynaBean abean=(LazyDynaBean)commQueryList.get(i);
										String itemid=(String)abean.get("itemid");
										String itemtype=(String)abean.get("itemtype");
										String codesetid=(String)abean.get("codesetid");
										String isMore=(String)abean.get("isMore");
										String itemdesc=(String)abean.get("itemdesc");
										String value=(String)abean.get("value");
										String viewvalue=(String)abean.get("viewvalue");
										out.print("<font class='class_text'>"+itemdesc+":&nbsp;</font>");
										out.print("</td><td align='left' width='70%' class=tdValue>");
										if(itemtype.equals("A"))
										{
											if(codesetid.equals("0"))
											{	
												if(itemid.equalsIgnoreCase(hireMajor) && !(hireMajorCode==null || hireMajorCode.equals("-1"))){
													   out.print("<div class=\"input_bg2\" style='width:110px;'><input  class='TEXT' type='text' name='commQueryList["+i+"].viewvalue' value='"+viewvalue+"'  size='10'  style='width:95px'     /><span  style='float:right;margin-top:-17px;'><img style='float:right;valign:top;' src='/images/code.gif' onclick='javascript:openInputCodeDialog(\""+hireMajorCode+"\",\"commQueryList["+i+"].viewvalue\");'/></span></div>");
													//   out.println("<input type='text' style='display:none;' name='conditionFieldList["+i+"].value' value='"+value+"'  />&nbsp;"); 
													   out.println("<input type=\"hidden\" class='class_text' name=\"commQueryList["+i+"].value\"  value=\""+value+"\"   size='20'   />");
														
												}
												else{
													out.println("<div class='input_bg1'  id='input"+i+"'>");
													out.println("<input type=\"text\" class='class_text' name=\"commQueryList["+i+"].value\"  value=\""+value+"\"   size='20'   />");
													out.print("</div>");
												}
												
											}
											else
											{
												if(isMore.equals("0"))
												{
													ArrayList options=(ArrayList)abean.get("options");
													out.print("<div class='input' id='input"+i+"'>");
													out.print(" <div class='floor' tabindex=\"0\" id='floor"+i+"' onblur=\" hide('"+i+"');\"> ");
													
													
													String selected="";
													String selectedvalue="";
													for(int n=0;n<options.size();n++)
													{
														LazyDynaBean a_bean=(LazyDynaBean)options.get(n);
														String avalue=(String)a_bean.get("value");
														String aname=(String)a_bean.get("name");
													
														
														out.println("<a style='cursor:hand;' onclick=\"javascript:change1("+i+",'"+aname+"','"+avalue+"','commQueryList["+i+"].value');\">");
														if(avalue.equals(value)){
															selected=aname;
															selectedvalue=avalue;
														}
														out.print(" "+aname+"</a>");
														if(n!=options.size()-1){
															out.print("<br>");
														}
														
													} 
													out.print("<input type='hidden' name='commQueryList["+i+"].value' value='"+selectedvalue+"'/>");
													out.print("</div><span  style='float:right;top:0px;margin-bottom:-10px;margin-right:-10px;'>");
													out.print("<a href='javascript:void(0);' onclick='showlist("+i+");'><img src='/images/hire/xia.gif'/></a>");
													out.print(" </span><span style='padding-right:0px;margin-right:-2px;margin-left:1.9px; FONT-SIZE: 12px;font-family: 微软黑体;color:black;' id='span"+i+"'>");	
													out.print(selected+" </span>");
													out.print("</div>");
													}
												else
												{
													out.print("<div class='input_bg1'  id='input"+i+"'>");
									              	out.print("<input class=textbox type='text' name='commQueryList["+i+"].viewvalue' value='"+viewvalue+"'    readonly='true'   />");
									              	out.print("<input type='hidden' name='commQueryList["+i+"].value' value='"+value+"'/>");  
									             	
									             	out.print("</div>");
									             	out.print("<img  src='/images/code.gif' onclick='javascript:openInputCodeDialog(\""+codesetid+"\",\"commQueryList["+i+"].viewvalue\");'/>");		
												}
											
											}
										
										}
										else if(itemtype.equals("D"))
										{
											out.println("<div class='input_bg1'  id='input"+i+"'>");
											out.println("<input    name='commQueryList["+i+"].value'  size='20' value='"+value+"'  onclick='popUpCalendar(this,this, dateFormat,\"\",\"\",true,false)'/>");
											out.print("</div>");
										}
										else if(itemtype.equals("N"))
										{
											out.println("<div class='input_bg1'  id='input"+i+"'>");
											out.println("<input  name=\"commQueryList["+i+"].value\"   value=\""+value+"\"   size='20'   />");
											out.print("</div>");
										}
										out.print("</td></tr>");
									
									
									
									}
									%>
							<tr>
							<td align="center" colspan='2' style="background-color:#FFFFFF;">
							 <a href="javascript:searchPos();"><img src="/images/hire/sarch2.gif"  border="0"/></a>
							</td>
							</tr>
						</table>
						</div>
	                  </div>
                  </div>
                    <div class='footer' style="height:0px;"> &nbsp;&nbsp;</div>   
                   </div>
                  
        			</div>
       
        		</div>
        		 
        </div> 
      
            
        </div>
        
        </div>
       
        </div>
</form>
 <script language="javascript">
         
         initCard();
</script>