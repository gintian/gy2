<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<%@ page import="com.hjsj.hrms.actionform.hire.employNetPortal.EmployPortalForm,com.hrms.hjsj.utils.Sql_switcher,com.hrms.hjsj.sys.Constant,
			     org.apache.commons.beanutils.LazyDynaBean,com.hjsj.hrms.utils.PubFunc,
			     java.util.*"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hjsj.hrms.businessobject.hire.ParameterXMLBo"%>
<%@page import="com.hrms.frame.utility.AdminDb"%>
<%@page import="java.sql.Connection"%>
<html>
<head>	
<script language="JavaScript" src="/general/sys/hjaxmanage.js"></script>
 <script language="JavaScript" src="/hire/hireNetPortal/employNetPortal.js"></script>
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
		Connection conn = null;
		String complexPassword="";//是否使用复杂密码 0未使用 1使用
		String passwordMinLength="";//密码最小长度
		String passwordMaxLength="";//密码最大长度
		try{
			
			conn = AdminDb.getConnection();
			ParameterXMLBo parameterXMLBo=new ParameterXMLBo(conn);
			HashMap map=parameterXMLBo.getAttributeValues();		

			if(map!=null&&map.get("complexPassword")!=null)
			{
				complexPassword=(String)map.get("complexPassword");
			}
			if(map!=null&&map.get("passwordMinLength")!=null)
			{
				passwordMinLength=(String)map.get("passwordMinLength");
			}
			if(map!=null&&map.get("passwordMaxLength")!=null)
			{
				passwordMaxLength=(String)map.get("passwordMaxLength");
			}
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
  %>
<script language='javascript'>
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
	<%
			EmployPortalForm employPortalForm=(EmployPortalForm)session.getAttribute("employPortalForm");
		   HashMap hm = employPortalForm.getFormHM();
		   String channelName = (String)hm.get("channelName");
			String isQueryCondition=employPortalForm.getIsQueryCondition();
			if(employPortalForm.getUserName()==null||employPortalForm.getUserName().equals("")){
				employPortalForm.setA0100("");
			}
			String a0100=employPortalForm.getA0100();
			String dbName=employPortalForm.getDbName();
			String hireChannel=employPortalForm.getHireChannel();
			String zpUnitCode=employPortalForm.getZpUnitCode();
			String hirechannel=employPortalForm.getHireChannel();
			String userName = employPortalForm.getUserName()==null?"":employPortalForm.getUserName();
			ArrayList unitList=employPortalForm.getUnitList();
            String username=employPortalForm.getUserName()==null?"":employPortalForm.getUserName();
           	LazyDynaBean kbean=null;
            ArrayList kll=new ArrayList();
            int hi=0;
             int lis=0;
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
<body onKeyDown='return pf_ChangeFocusTWO("<%=dbName%>","<%=a0100%>");'  >
<div id='chajian' ></div>
<html:form action="/hire/hireNetPortal/search_zp_position"> 
	<html:hidden name="employPortalForm" property="isDefinitionActive"/>
	    	<a href="<%=url_p%>" style="display:none" id="hostname">for vpn</a>
                     <%
          			if(a0100==null||(a0100!=null&&a0100.trim().length()==0)||a0100.equals("")){
          				%>
          				<script language='javascript'>
          					alert("请先登录!");
          					window.location="/hire/hireNetPortal/search_zp_position.do?b_login=login";
          				</script>
          				<%
          				//response.sendRedirect("/hire/hireNetPortal/search_zp_position.do?b_login=login");
          			}
     				 %>
   	<div class="body">

        <div class="tcenter" id='tc'>
       	<div class="center_bg" id='cms_pnl'>
		    <div class="left" style="margin-bottom:120px;">
           		
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
         	      	<!--  <li><a href="###"><bean:message key="hire.out.position.recommend"/></a></li><!-- 推荐岗位 -->
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
         	        <li><a href="javascript:void(0);" onclick='hasresume();'><bean:message key="hire.browsed.position"/></a></li>
         	        <li><a href="/hire/hireNetPortal/search_zp_position.do?b_applyedPosition=query"><bean:message key="hire.apply.position"/>
         	        </a></li>
         	        <li><a href="/hire/hireNetPortal/search_zp_position.do?br_editPassword=edit"  class="els"><bean:message key="label.banner.changepwd"/></a></li>
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
			<div class="muen">
				<h2>&nbsp;&nbsp;&nbsp;&nbsp;招聘单位</h2>
				<logic:iterate id="unit" name="employPortalForm" property="unitList" indexId="indexx">
					<logic:equal value="<%=zpUnitCode%>" name="unit" property="codeitemid">
					<div class="firstDiv">
					<table>
						<tr><td align="left" valign="middle">
						<img src="/images/tree_collapse.gif" border="0" id="<bean:write name="unit" property="id_img"/>" style="cursor:hand" onclick='changeDisplay("<bean:write name="unit" property="codeitemid"/>","<bean:write name="unit" property="count"/>");'/>
						<a href="/hire/hireNetPortal/search_zp_position.do?b_query=link&isAllPos=1&operate=init&hireChannel=${employPortalForm.hireChannel}&zpUnitCode=<bean:write name="unit" property="codeitemid"/>"><font class="firstFont">
						<bean:write name="unit" property="codeitemdesc"/></font> 
						</a>
						</td>
						</tr>
					</table></div>
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
						<div class="firstDiv">
							<table>
							<tr><td align="left" valign="middle">
							<img src="/images/tree_collapse.gif" border="0" id="<bean:write name="unit" property="id_img"/>" style="cursor:hand" onclick='changeDisplay("<bean:write name="unit" property="codeitemid"/>","<bean:write name="unit" property="count"/>");'/>
							<a href="/hire/hireNetPortal/search_zp_position.do?b_query=link&isAllPos=1&operate=init&hireChannel=${employPortalForm.hireChannel}&zpUnitCode=<bean:write name="unit" property="codeitemid"/>">
							<font class="firstFont"><bean:write name="unit" property="codeitemdesc"/></font>
							</a>
							</td></tr>
							</table>
						</div>
						<ul class="col">
						<logic:iterate id="UnitSub" name="unit" property="list" indexId="indexid">
							<logic:equal value="<%=zpUnitCode%>" name="UnitSub" property="codeitemid">
							<li id="<bean:write name="UnitSub" property="id_r"/>">
							<a class="one" title='<bean:write name="UnitSub" property="altdesc"/>' href="/hire/hireNetPortal/search_zp_position.do?b_query=link&isAllPos=1&operate=init&hireChannel=${employPortalForm.hireChannel}&zpUnitCode=<bean:write name="UnitSub" property="codeitemid"/>">
							<bean:write name="UnitSub" property="codeitemdesc"/>
							</a>
							</li>
							</logic:equal>
							<logic:notEqual value="<%=zpUnitCode%>" name="UnitSub" property="codeitemid">
							<li id="<bean:write name="UnitSub" property="id_r"/>"><a title='<bean:write name="UnitSub" property="altdesc"/>' href="/hire/hireNetPortal/search_zp_position.do?b_query=link&isAllPos=1&operate=init&hireChannel=${employPortalForm.hireChannel}&zpUnitCode=<bean:write name="UnitSub" property="codeitemid"/>"><bean:write name="UnitSub" property="codeitemdesc"/></a></li>
							</logic:notEqual>
						</logic:iterate>
						</ul>
					</logic:notEqual>
				</logic:iterate>                   
			</div>
           </div>
           <!-- 右侧“社会/校园/猎头  招聘” -->
            <% 
                if(hirechannel.equals("headHire")){//暂时显示成这样,等做成图片后换 上来 
                	// out.println(" <div class=\"right3\"  id='rg'  style='min-height:"+hi+"px;margin-bottom:30px'>");
                	 out.println(" <div class=\"right3\"  id='rg'  >");
                 }else{
                %>
                <div class="right4"  id='rg'  >
                <h2><%=channelName %></h2>
                <%} %>
               <div class="jj zw">
               	<h3>修改密码</h3>
               		<div class="nr">
               	 <TABLE cellSpacing=0 cellPadding=0 width="100%" align=center border=0>
                      <TBODY>             
                       
                        <tr>
                         <TD class="table_border_class3" align='left'height='26'>&nbsp;&nbsp;
                       	</TD>
                      	 </tr>
                       
                      </TBODY>
                     </TABLE>
                  <table width="0" border="0" cellspacing="0" cellpadding="0" class="table">
                     <TBODY>
                     <tr>
	    <td align="right" width='35%'>&nbsp;</td>
		<td  width='25%'>&nbsp;</td>
		<td  width='40%'></td>
	  </tr>
                    	 <TR>
                       	 <td width="217" height="34" align="right"">
                        	 <font class="class_text">
                        	 	<bean:message key="hire.old.password"/>：
                        	 </font>
                        </td>
                      	 <td class='nowrap'>
                    	    <div class='input_bg1 reg_input_div'>
                    	   		<INPUT  id=pwd0  type=password name=pwd0 autocomplete="off" class="reg_input">&nbsp;
                    	   		 <label class="reg_item_must">*</label> 
                          	</div>
                         
                        </td>
                        <td>
                        		 <FONT color=red><span id='t1' class='editpassword_woke'></span></FONT>
		 	</td>
                       </TR>
                       <%if(complexPassword.equals("1")){%>
                       <TR>
                        <td width="217" height="34" align="right"">
                         <font class='class_text'>
                        		 <bean:message key="hire.new.password"/>：
                         </font>
                        </TD>
                        <td class='nowrap'>
                         <div class='input_bg1 reg_input_div'>
                         	<INPUT  id=pwd1  type=password name=pwd1 autocomplete="off" class="reg_input">
                         	 &nbsp;<label class="reg_item_must">*</label>
                         </div>
                       		
                        </TD>
                        <td style="float:left;text-algin:left;">
                        		 <b class="els" id='t3'>
                        		 	<font class='FontStyle'>                           
                       				&nbsp;&nbsp;<%=passwordMinLength%>-<%=passwordMaxLength%>位的字母、数字、特殊字符的组合
                       			 </font>
                       		</b>
                       	 <FONT color=red><span id='t2' class='editpassword_woke'></span></FONT>
		 	</td>
                       </TR>
                     <TR>
                         <td width="217" height="34" align="right""><font class="class_text"><bean:message key="hire.repeat.password"/>：</font>
                         </TD>
                          <td class='nowrap'>
                           <div class='input_bg1 reg_input_div'>
                          	 <INPUT   id=pwd2  type=password name=pwd2 autocomplete="off" class="reg_input">
                          	  &nbsp;<label class="reg_item_must">*</label> 
                           </div>
                         	
                         </TD>
                        </TR>
                       <%}else{ %> 
                     <TR>
                        <td width="217" height="34" align="right"">
                         <font class='class_text'>
                        		 <bean:message key="hire.new.password"/>：
                         </font>
                        </TD>
                        <td class='nowrap'>
                         <div class='input_bg1 reg_input_div'>
                         	<INPUT  id=pwd1 maxlength=8 type=password name=pwd1 autocomplete="off" class="reg_input">
                         	 &nbsp;<label class="reg_item_must">*</label> 
                         </div>
                       		
                        </TD>
                        <td style="float:left;text-algin:left;margin-left:12px;">
                        		 <b class="els" id='t3'>
                        		 	<font class='FontStyle'>&nbsp;&nbsp;<bean:message key="hire.password.length"/>	                           
                       				<bean:message key="hire.six.eight"/><bean:message key="hire.locate"/>
                       			 </font>
                       		</b>
                       	 <FONT color=red><span id='t2' class='editpassword_woke'></span></FONT>
		 	</td>
                       </TR>
                     <TR>
                         <td width="217" height="34" align="right""><font class="class_text"><bean:message key="hire.repeat.password"/>：</font>
                         </TD>
                          <td class='nowrap'>
                           <div class='input_bg1 reg_input_div'>
                          	 <INPUT  maxlength=8 id=pwd2  type=password name=pwd2 autocomplete="off" class="reg_input">
                          	  &nbsp;<label class="reg_item_must">*</label> 
                           </div>
                         	
                         </TD>
                        </TR>
                        <%} %>
                        <tr>
                        <td>
                        &nbsp;&nbsp;&nbsp;&nbsp;
                        </td>
                        </tr>
                 	  </TBODY>
                   </TABLE>
            
                  <table width="0" border="0" cellspacing="0" cellpadding="0" class="table table3">
                     <TBODY>
                    	 <TR>
                        <TD style="text-align:center" >
                          <br>
                         <a href='javascript:subEDIT("<%=dbName%>","<%=a0100%>","<%=complexPassword%>","<%=passwordMinLength%>","<%=passwordMaxLength%>")' >
                         <IMG  src="/images/hire/sub.gif" border=0 >
                         </a>
                        </TD>
                       </TR>
                       </TBODY>
                     </TABLE>
               	</div>
               </div>
       		</div>
       		<div class='footer' style="height:0px;"> &nbsp;&nbsp;</div>
       		</div>
       		</div>
        </div>


</html:form>

</body>
</html>
 <script language="javascript">
         
         initCard();
</script> 

