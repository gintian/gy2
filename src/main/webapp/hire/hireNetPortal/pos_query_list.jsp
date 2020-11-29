 <%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
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
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
<script type="text/javascript" src="/jquery/jquery-3.3.1.min.js"></script>

<%@ page import="com.hjsj.hrms.actionform.hire.employNetPortal.EmployPortalForm,
			     org.apache.commons.beanutils.LazyDynaBean,com.hrms.hjsj.utils.Sql_switcher,com.hrms.hjsj.sys.Constant,
			     java.util.*,com.hrms.hjsj.sys.ResourceFactory,com.hjsj.hrms.businessobject.sys.SysParamBo,com.hjsj.hrms.businessobject.sys.SysParamConstant"%>
<%@ page import="com.hrms.struts.constant.SystemConfig,com.hrms.frame.codec.SafeCode" %>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>

<html>
<head>
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
<script type="text/javascript">
var JQuery=$.noConflict();//将jquery起个别名防止和系统中自定义的$()方法冲突
var a0100 = "${employPortalForm.a0100}";
var cardid = "${employPortalForm.admissionCard}";
var nbase = "${employPortalForm.dbName}";
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
</head>
<body onKeyDown="return pf_ChangeFocus();" >
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
<%
            EmployPortalForm employPortalForm=(EmployPortalForm)session.getAttribute("employPortalForm");
			if(employPortalForm.getUserName()==null||employPortalForm.getUserName().equals("")){
				employPortalForm.setA0100("");
			}
			String a0100=employPortalForm.getA0100();
			String dbName=employPortalForm.getDbName();
			ArrayList conditionFieldList=employPortalForm.getConditionFieldList();
			String hirechannel=employPortalForm.getHireChannel();
			String zpUnitCode=employPortalForm.getZpUnitCode();
			 ArrayList unitList=employPortalForm.getUnitList();
            	LazyDynaBean kbean=null;
            ArrayList kll=new ArrayList();
             int hi=0;  
            int lis=0;
            String username= employPortalForm.getUserName()==null?"":employPortalForm.getUserName();
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
				            	<div class="left" style="margin-bottom:120px">
            		
                	<div class="login">
                    	<h2>&nbsp;&nbsp;&nbsp;&nbsp;用户登录</h2>
                        <div class="dl">
								<table width="206px" border="0" cellspacing="0" cellpadding="0">
								  <tr>
								    <td>&nbsp;</td>
								  </tr>
								  <tr>
								    <td><div class="input_bg"><span>邮&nbsp;&nbsp; 箱</span><input class=s_input id=loginName onkeydown="KeyDown()"  name=loginName></div></td>
								  </tr>
								  <tr>
								    <td><div class="input_bg"><span>密&nbsp;&nbsp; 码</span><input class=s_input id=password type=password  onkeydown="KeyDown()"				                 
						             name=password></div></td>
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
						                
						                </span></td>
								  </tr>
								</table>
                        </div>
                    </div>
                    <%}  else { %>
                    <div class="body">
				    	
				        <div class="tcenter" id='tc'>
				        	<div class="center_bg" id='cms_pnl'>
				            	<div class="left" style="margin-bottom:120px">
				            		
                	<div class="login">
                    	 <div class="dl_1">
                    	   <div class="we"><b><bean:message key="hire.welcome.you"/>,${employPortalForm.userName}</b>请认真填写您的简历，填写完成后您可以投递感兴趣的职位。</div>
		            	      <ul class="dl_list">
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
		            	          <li><a href="javascript:void(0);"  onclick='hasresume();'>已浏览<%if(hirechannel.equals("01")){ %>专业 <%}else{ %>岗位<%} %></a></li>
		            	         <%if(hirechannel.equals("01")){ %>
		            	        <li><a href="/hire/hireNetPortal/search_zp_position.do?b_applyedPosition=query"><bean:message key="hire.apply.position1"/></a></li>
		            	         <%}else{ %>
		            	            <li><a href="/hire/hireNetPortal/search_zp_position.do?b_applyedPosition=query"><bean:message key="hire.apply.position"/>
		            	            </a></li>
		            	         <%} %>
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
                <div class="right" style='min-height:<%=hi %>px;'>
                <h2></h2>
                <%}else{ %>
                 <div class="right1" style='min-height:<%=hi %>px;'>
                 <h1></h1>
                <%} %>
                	  <div class="jj zw">
						<h3><%if(hirechannel.equals("02")){ %>招聘岗位<%}else{ %>招聘专业<%}%></h3>
						
						<div class="nr">
                	  <% 
				  
						  String positionNumber=employPortalForm.getPositionNumber();
						  String hireMajor=employPortalForm.getHireMajor();
						  String hireChannel=employPortalForm.getHireChannel();
						  ArrayList posFieldList  = employPortalForm.getPosFieldList();
						  ArrayList posList=employPortalForm.getZpPosList();
						  int pcount=Integer.parseInt(positionNumber);
				          LazyDynaBean abean=(posList==null||posList.size()==0)?null:(LazyDynaBean)posList.get(0);
				          String unitName="unitName";
		              %>
		              
		          
                              <table width="100%" border="0" cellspacing="0" cellpadding="0" class="table">
		                       <tr align="center" >
		                        <%
		                         int colspan=5;
		                        if(posFieldList!=null&&posFieldList.size()>0){
		                            colspan=posFieldList.size();
		                           int width=10;
                                   if(posFieldList.size()<5)
                                       width=15;
                                   for(int y=0;y<posFieldList.size();y++)
                                   {
                                     LazyDynaBean bbean = (LazyDynaBean)posFieldList.get(y);
                                     String itemid=(String)bbean.get("itemid");
                                     String itemdesc=(String)bbean.get("itemdesc");
                                      String itemtype = (String)bbean.get("itemtype");
                                     String stylec = "hj_zhaopin_list_tab_titleone";
                                     if(y==0)
                                          stylec="hj_zhaopin_list_tab_titleone_1";
                                     if(y==posFieldList.size()-1)
                                          stylec="hj_zhaopin_list_tab_titleone_r";
                                     if(itemid.equalsIgnoreCase("z0303"))
                                        width=15;
                                     else if(itemtype.equalsIgnoreCase("N"))
                                        width=10;
                                     else
                                        width=15;
                                     if(itemid.equalsIgnoreCase("z0311")){
                                         if(hireChannel.equals("01")&&!hireMajor.equals("-1"))
                                         {
                                             itemdesc=ResourceFactory.getProperty("hire.employActualize.interviewProfessional");
                                         }
                                         else
                                         {
                                            itemdesc=ResourceFactory.getProperty("lable.zp_plan_detail.pos_id");
                                         }
                                         %>
                                            <td height="36" class='table_line_title' width="30%" nowrap><b><%=itemdesc%></b></td>
                                         <%
                                     }else{
                                     %>
                                     		<td class='table_line_title'  width="<%=width+"%"%>" nowrap><b><%=itemdesc%></b></td>
                                     <% 
                                     } 
                                  	}
                               %>
                        
                      
                          		<%}else{ %>
				                        <td width="40%" height="36" class='table_line_title' ><b>
				                        <%if(hireChannel.equals("01")&&!hireMajor.equals("-1")){ %>
				                        <bean:message key="hire.employActualize.interviewProfessional"/>
				                        <%}else{ %>
				                        <bean:message key="lable.zp_plan_detail.pos_id"/>
				                        <%} %></b>
				                        </td>
				                        <td class='table_line_title' width="15%"><b> <bean:message key="lable.zp_plan_detail.domain"/></b> </td>
				                        <td class='table_line_title' width="15%"><b> <bean:message key="label.zp_release_pos.valid_date"/></b></td>
				                        <%if(!SystemConfig.getPropertyValue("zp_visibletype").equals("1")){ %>
				                             <td class='hire_posTable_head'  width="15%"><b> <bean:message key="hire.applay.personcount"/></b> </td>
				                        <%}else{ %>
				                        <td class='table_line_title'  width="10%"><b>需求人数</b></td>
				                        <%}%>
				                        <td class='table_line_title'  width="10%"><b><bean:message key="hire.column.applay"/></b> </td>
		                        <%} %>
		                 </tr>
								
								  <%  
								   if(posList==null)
								      posList=new ArrayList();
								    if(posFieldList!=null&&posFieldList.size()>0)
								    {
								         for(int i=0;i<posList.size();i++) 
						                 {
						                    out.println("<tr>");
						  	            	LazyDynaBean bean=(LazyDynaBean)posList.get(i);
						  	             	String state=(String)bean.get("state");
						  	             	String z0301=(String)bean.get("z0301");
						  	            	String z0311=(String)bean.get("z0311");
						  	            	String p="";
						  	            	if(hireChannel.equals("01")&&!hireMajor.equals("-1"))
						  	             	{
						  		              //posName=(String)bean.get(hireMajor.toLowerCase());
						  		              p=bean.get("major")==null?"":"&major="+(String)bean.get("major");
						  		            }
						  		           String posName=(String)bean.get("z0311Name");
						  		           posName = StringUtils.isEmpty(posName) ? "" : posName;
						  		           String isNewPos = (String)bean.get("isNewPos");
						  		           for(int y=0;y<posFieldList.size();y++)
						  		           {
						  	             	    LazyDynaBean bbean=(LazyDynaBean)posFieldList.get(y);
						  		                String itemid=((String)bbean.get("itemid")).toLowerCase();
						  		                String itemtype=(String)bbean.get("itemtype");
						  		                if(itemid.equalsIgnoreCase("z0321")||itemid.equalsIgnoreCase("z0311"))
						  		                    itemid=itemid+"Name";
						  		                if(itemid.equalsIgnoreCase("z0311Name")&&hireChannel.equals("01")&&!hireMajor.equals("-1"))
						  		                {
						  		                    itemid=hireMajor.toLowerCase();
						  		                    posName=(String)bean.get(itemid);
						  		                 	 posName = StringUtils.isEmpty(posName) ? "" : posName;
						  		                }
						  		                String value=(String)bean.get(itemid);
						  		                if(value==null||value.equals(""))
						  		                   value="&nbsp;";
						  		                if(itemid.equalsIgnoreCase("z0311")||itemid.equalsIgnoreCase("ypljl")||itemid.equalsIgnoreCase(hireMajor)||itemid.equalsIgnoreCase("z0311Name"))
						  		                      value=" <a onclick=\"rediract('"+p+"','"+z0311+"','"+z0301+"','"+SafeCode.encode(posName)+"','"+unitName+"','"+0+"','')\"  class='hire_posTable_href'> "+value+"</a>";
						  		                if((itemid.equalsIgnoreCase("z0311Name")&&hireChannel.equals("02"))||(itemid.equalsIgnoreCase(hireMajor)&&hireChannel.equals("01")))
						  		                {
						  		                  if(state.equals("1")){ 
						  		                       value+="<IMG border=0 src='/images/hot.gif' >&nbsp;"; 
						  		                  }
						  		                  if(isNewPos.equals("1")){value+="<IMG border=0 src='/images/new0.gif' >";}
						  		                }
						  		                String align="align=\"center\"";
						  		                if(itemtype.equalsIgnoreCase("N"))
						  		                     align="align=\"center\"";
						  		                
						  		                String styleClass="hj_zhaopin_list_tab_titletwo";
						  		                if(y==0)
						  		                   styleClass="hj_zhaopin_list_tab_titletwo_1";
						  		                if(y==posFieldList.size()-1)
						  		                   styleClass="hj_zhaopin_list_tab_titletwo_r";
						  		               %>
						  		                  
						  		                   
						  		              <%if(i%2==0) {%>
								  		     	<td height="36" class='hire_posTable_body1'><%=value%></td>
								  		     <%}else{ %>
								  		     	<td height="36" class='hire_posTable_body2' ><%=value%></td>
								  		     <%} %>     <%    
						  		          }
						  		          out.println("</tr>");	
						               }   
								    }
								    else
							    	{
							    	  	for(int i=0;i<posList.size();i++)
								     	{
								   		   
								     		LazyDynaBean bean=(LazyDynaBean)posList.get(i);
								  	    	String posName=(String)bean.get("posName");
								  	    	posName = StringUtils.isEmpty(posName) ? "" : posName;
								  	    	String z0333=(String)bean.get("z0333");
								  	    	String Z0331=(String)bean.get("z0331");
								      		String Z0329=(String)bean.get("z0329");
								      		String z0301=(String)bean.get("z0301");
								      		String z0311=(String)bean.get("z0311");
								      		String state=(String)bean.get("state");
								     		String count=(String)bean.get("count");
								     		String z0313=(String)bean.get("z0313");
								     		String isNewPos=(String)bean.get("isNewPos");
								     		if(z0333==null||z0333.trim().equals(""))
						  		                z0333="&nbsp;";
						  	            	if(Z0329==null||Z0329.trim().equals(""))
						  		                Z0329="&nbsp;";
						  	            	if(count==null||count.trim().equals(""))
						  		                count="&nbsp;";
						  	            	if(z0313==null||z0313.trim().equals(""))
						  		                 z0313="&nbsp;";
								     		String p="";
								      		if(hireChannel.equals("01")&&!hireMajor.equals("-1"))
								     		{
						  		                posName=(String)bean.get(hireMajor.toLowerCase());
						  		              	posName = StringUtils.isEmpty(posName) ? "" : posName;
						  		                 p=bean.get("major")==null?"":"&major="+(String)bean.get("major");
						  		           }
						  		           
						  		            String styleClass="hj_zhaopin_list_tab_titletwo";
								      %>
								     <%if(i%2==0){ %>
				                      <tr > 
				                        <td class='hire_posTable_body1'>   
				                         &nbsp;&nbsp; <a href="/hire/hireNetPortal/search_zp_position.do?b_posDesc=link<%=p%>&posID=<%=z0311%>&z0301=<%=z0301%>&posName=<%=SafeCode.encode(posName)%>&unitName=<%=unitName%>&returnType=0"  class='hire_posTable_href'> 
				                             <%=posName%>
				                             <% if(state.equals("1")){ out.print("<IMG border=0 src='/images/hot.gif' >&nbsp;");  }
				                                if(isNewPos.equals("1")){out.print("<IMG border=0 src='/images/new0.gif' >");}
				                              %>
				                             
				                          </a> 
				                        </td>
				                        <td class='hire_posTable_body1' ><%=z0333%></td>
				                        <td class='hire_posTable_body1'><%=Z0329%></td>
				                         <%if(!SystemConfig.getPropertyValue("zp_visibletype").equals("1")){ %>
		                                <td  class='hire_posTable_body1'><%=count%>&nbsp;
		                                </td>
		                               <%}else{ %>
		                               <td  class='hire_posTable_body1'><%=z0313%>&nbsp;
		                               </td>
		                               <%} %>
				                       <td  class='hire_posTable_body1'>
		                         			<a href="/hire/hireNetPortal/search_zp_position.do?b_posDesc=link<%=p%>&posID=<%=z0311%>&z0301=<%=z0301%>&posName=<%=SafeCode.encode(posName)%>&unitName=<%=unitName%>&returnType=0" class='hire_posTable_href'> 应聘  </a>
                      				   </td>
		                     		 </tr>
		                      		<%}else{ %>
		                      		 <tr > 
				                        <td class='hire_posTable_body2'>   
				                         &nbsp;&nbsp; <a href="/hire/hireNetPortal/search_zp_position.do?b_posDesc=link<%=p%>&posID=<%=z0311%>&z0301=<%=z0301%>&posName=<%=SafeCode.encode(posName)%>&unitName=<%=unitName%>&returnType=0" class='hire_posTable_href'> 
				                             <%=posName%>
				                             <% if(state.equals("1")){ out.print("<IMG border=0 src='/images/hot.gif' >&nbsp;");  }
				                                if(isNewPos.equals("1")){out.print("<IMG border=0 src='/images/new0.gif' >");}
				                              %>
				                             
				                          </a> 
				                        </td>
				                        <td  class='hire_posTable_body2' ><%=z0333%></td>
				                        <td  class='hire_posTable_body2'><%=Z0329%></td>
				                         <%if(!SystemConfig.getPropertyValue("zp_visibletype").equals("1")){ %>
		                                <td  class='hire_posTable_body2' ><%=count%>&nbsp;</td>
		                               <%}else{ %>
		                               <td  class='hire_posTable_body2'><%=z0313%>&nbsp;</td>
		                               <%} %>
				                         <td class='hire_posTable_body2'>
		                         			<a href="/hire/hireNetPortal/search_zp_position.do?b_posDesc=link<%=p%>&posID=<%=z0311%>&z0301=<%=z0301%>&posName=<%=SafeCode.encode(posName)%>&unitName=<%=unitName%>&returnType=0" class='hire_posTable_href'> 应聘  </a>
                      			 		</td>
		                      		</tr>
			                   	 	 <%
			                   	  		}}
			                   	  		}
			                   	  	%>
			                   	 
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
</html:form>
</body>
</html>
 <script language="javascript">
         
         initCard();
</script>