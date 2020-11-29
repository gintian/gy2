<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/general/sys/hjaxmanage.js"></script>
<script language="JavaScript" src="/hire/hireNetPortal/employNetPortal.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<%@ page import="com.hjsj.hrms.actionform.hire.employNetPortal.EmployPortalForm,
			     org.apache.commons.beanutils.LazyDynaBean,com.hrms.hjsj.utils.Sql_switcher,com.hrms.hjsj.sys.Constant,
			     com.hjsj.hrms.utils.PubFunc,
			     java.util.*"%>
			     <%@ page import="com.hrms.hjsj.sys.ResourceFactory,com.hjsj.hrms.utils.PubFunc"%>
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
	String isHeadhunter ="";
	if(userView!=null){
	  userViewName=userView.getUserName();
	  isHeadhunter = (String)userView.getHm().get("isHeadhunter");
	}
  %>
<script language='javascript'>
var a0100 = "${employPortalForm.a0100}";
var cardid = "${employPortalForm.admissionCard}";
var nbase = "${employPortalForm.dbName}";
function initCard()
{
      var rl = document.getElementById("hostname").href;     
      var aurl=rl;//tomcat路径
      var DBType="<%=dbtype%>";//1：mssql，2：oracle，3：DB2
      var UserName="<%=userViewName%>";
      var obj = document.getElementById('CardPreview1');   
      var superUser="1";
      var menuPriv="";
      var tablePriv="";
      if(obj==null)
      {
         return false;
      }
      obj.SetSuperUser(superUser);  // 1为超级用户,0非超级用户
      obj.SetUserMenuPriv(menuPriv);  // 指标权限, 逗号分隔, 空表示全权
      obj.SetUserTablePriv(tablePriv);  // 子集权限, 逗号分隔, 空表示全权         
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
<body onKeyDown="return pf_ChangeFocus();"  >
<div id='chajian' ></div>
<html:form action="/hire/hireNetPortal/search_zp_position">
<a href="<%=url_p%>" style="display:none" id="hostname">for vpn</a>
			<%
			EmployPortalForm employPortalForm=(EmployPortalForm)session.getAttribute("employPortalForm");
			HashMap hm = employPortalForm.getFormHM();
	        String channelName = (String)hm.get("channelName");
			ArrayList fieldSetList=employPortalForm.getFieldSetList();			
			int index=Integer.parseInt((String)employPortalForm.getCurrentSetID());
			if(employPortalForm.getUserName()==null||employPortalForm.getUserName().equals("")){
				employPortalForm.setA0100("");
			}
			String a0100=employPortalForm.getA0100();
			String dbName=employPortalForm.getDbName();
			String writeable=employPortalForm.getWriteable();
			ArrayList applyedPositionList=employPortalForm.getApplyedPosList();
			String max_count=employPortalForm.getMax_count();
			String hireChannel=employPortalForm.getHireChannel();
			String hireMajor=employPortalForm.getHireMajor();
			String zpUnitCode=employPortalForm.getZpUnitCode();
			String userName =employPortalForm.getUserName()==null?"":employPortalForm.getUserName();
			ArrayList unitList=employPortalForm.getUnitList();
           	LazyDynaBean kbean=null;
            ArrayList kll=new ArrayList();
            int hi=0;
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
            		if(userName.length()<=4){
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
            String z0329 = "";
            String z0333 = "";
            String z0315 = "";
            if(appliedPosItems.contains("resume_state")){
            	resume_state = "1";
            }
            if(appliedPosItems.contains("z0329")){
            	z0329 = "1";
            }
            if(appliedPosItems.contains("z0333")){
            	z0333 = "1";
            }
            if(appliedPosItems.contains("z0315")){
            	z0315 = "1";
            }
		    %>
	 <html:hidden name="employPortalForm" property="isDefinitionActive"/>
	 <html:hidden name="employPortalForm" property="hireChannel"/>
     <html:hidden name="employPortalForm" property="zpUnitCode"/>
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
				    	
	        <div class="tcenter">
	        	<div class="center_bg" id='cms_pnl'>
				<div class="left"	style="margin-bottom:120px">
             	<div class="login">
                 	 <div class="dl_1">
                 	 <div class="we"><b><bean:message key="hire.welcome.you"/>,
                           <%if(userName.length()>6){ %>
                           </b><b>
                           <% } %>
                           ${employPortalForm.userName}</b><bean:message key="hire.welcome.you.hint"/></div>
           	      <ul class="dl_list">
           	      <%
           	      		if(isHeadhunter!=null&&isHeadhunter.equals("1")){//进来的用户是猎头身份 
           	      	%>
           	      	<li><a href="/hire/hireNetPortal/recommend_resume.do?b_recommendResume=query"><bean:message key="hire.out.resume.recommend"/></a></li><!-- 推荐简历 -->
           	      	<!--<li><a href=""><bean:message key="hire.out.position.recommend"/></a></li><!-- 推荐岗位 -->
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
           	        <li><a href='/hire/hireNetPortal/search_zp_position.do?b_applyedPosition=query' class='els'><bean:message key='hire.apply.position'/>
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
                 		  <div class="firstDiv"><table><tr><td align="left" valign="middle"><img src="/images/tree_collapse.gif" border="0" id="<bean:write name="unit" property="id_img"/>" style="cursor:hand" onclick='changeDisplay("<bean:write name="unit" property="codeitemid"/>","<bean:write name="unit" property="count"/>");'/><a href="/hire/hireNetPortal/search_zp_position.do?b_query=link&isAllPos=1&operate=init&hireChannel=${employPortalForm.hireChannel}&zpUnitCode=<bean:write name="unit" property="codeitemid"/>"><font class="firstFont"><bean:write name="unit" property="codeitemdesc"/></font> </a></td></tr></table></div>
                 		 <ul class="col">
                    <logic:iterate id="UnitSub" name="unit" property="list" indexId="indexid">
                    			<logic:equal value="<%=zpUnitCode%>" name="UnitSub" property="codeitemid">
                    				<li id="<bean:write name="UnitSub" property="id_r"/>"><a  title='<bean:write name="UnitSub" property="altdesc"/>' class="one" href="/hire/hireNetPortal/search_zp_position.do?b_query=link&isAllPos=1&operate=init&hireChannel=${employPortalForm.hireChannel}&zpUnitCode=<bean:write name="UnitSub" property="codeitemid"/>"><bean:write name="UnitSub" property="codeitemdesc"/></a></li>
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
                    				<li id="<bean:write name="UnitSub" property="id_r"/>"><a  title='<bean:write name="UnitSub" property="altdesc"/>' class="one" href="/hire/hireNetPortal/search_zp_position.do?b_query=link&isAllPos=1&operate=init&hireChannel=${employPortalForm.hireChannel}&zpUnitCode=<bean:write name="UnitSub" property="codeitemid"/>"><bean:write name="UnitSub" property="codeitemdesc"/></a></li>
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
              <% 
                if(hireChannel.equals("headHire")){//暂时显示成这样,等做成图片后换 上来 
                	// out.println(" <div class=\"right3\"  id='rg'  style='min-height:"+hi+"px;margin-bottom:30px'>");
                	 out.println(" <div class=\"right3\"  id='rg'  >");
                 }else{
                %>
                <div class="right4"  id='rg'  >
                <h2><%=channelName %></h2>
                <%} %>
             	 <div class="jj zw">
                 	<h2><span>已申请职位</span></h2>
				<div class="nr">
                 	<table  border="0" cellspacing="0" cellpadding="0" class="table">
                       <TBODY>                    
                   <tr  align='center' > 
                     <td height="25" class="hire_posTable_head" nowrap>
                     <b>
                    	<bean:message key="hire.out.position.desc"/>
                    </b>
                   	</td>
                <%
                 if(resume_state.equalsIgnoreCase("1")){
                %>
                   	<td class="hire_posTable_head"><!-- 简历状态-->
                   	<b>
                   		<bean:message key="hire.employActualize.resumeState"/>    
                   		</b>   			
                   	</td>
               <%
              	}
                if(z0329.equalsIgnoreCase("1")){
               %>
                  	<td class="hire_posTable_head"><!-- 开始日期 -->
                  	<b>
                   		<bean:message key="lable.zp_plan.start_date"/> 
                   		</b>        			
                   	</td>
                   	<!-- 
                   	<td class=rptHead nowrap
                         background=/images/r_titbg01.gif >
                   		<font color='#DF0024'>结束日期</font>          			
                   	</td>
                   	-->
               <%
              	}
                if(z0333.equalsIgnoreCase("1")){
               %>
                   	<td class="hire_posTable_head"><!-- 工作地点 -->
                   	<b>
                   		<bean:message key="lable.zp_plan_detail.domain"/>    
                   		</b>          			
                   	</td>
               <%
              	}
                if(z0315.equalsIgnoreCase("1")){
               %>
                   	<!-- 招聘人数 -->
                   	<td class="hire_posTable_head">
                   	<b>
                   		<bean:message key="lable.zp_plan_detail.amount"/>   
                   		</b>       			
                   	</td>
                <%} %>	
                   	<logic:notEqual value="1" name="employPortalForm" property="max_count">
                   	<td class="hire_posTable_head">
                   	<b>
                   		<bean:message key="hire.wish.order"/>  
                   		</b>            			
                   	</td>
                   	</logic:notEqual>
                   	<td class="hire_posTable_head">
                   	<b>
                   		<bean:message key="system.infor.oper"/> 
                   		</b>          			
                   	</td>
                   </tr>
                   <%
                   for(int i=0;i<applyedPositionList.size();i++)
                   {
                   	LazyDynaBean abean=(LazyDynaBean)applyedPositionList.get(i);
                   	Boolean canDelPos = (Boolean)abean.get("canDelPos");//判断能否删除对应职位的申请记录
                   	String zp_pos_id=(String)abean.get("zp_pos_id");
			  String styleClass="hj_zhaopin_list_tab_titletwo";
			  int description=(Integer)abean.get("description");
                   	out.println("<tr>");
                   	if(i%2!=0){
                   		out.println("<td  class='hire_posTable_body2'>"+(String)abean.get("posName")+"&nbsp;</td>");
                   		if(resume_state.equalsIgnoreCase("1")){
                   			out.println("<td  class='hire_posTable_body2'>"+(String)abean.get("unitName")+"&nbsp;"+"</td>");//状态 
                   		} 
                   		if(z0329.equalsIgnoreCase("1")){
                    		out.println("<td  class='hire_posTable_body2'>"+(String)abean.get("z0329")+"&nbsp;"+"</td>");//开始日期
                    		//out.println("<td class=rptItemMain nowrap >"+(String)abean.get("z0331")+"</td>");//结束日期
                   		} 
                   		if(z0333.equalsIgnoreCase("1")){
                    		out.println("<td  class='hire_posTable_body2' >"+(String)abean.get("z0333")+"&nbsp;"+"</td>");//工作地点
                   		} 
                   		if(z0315.equalsIgnoreCase("1")){
                    		out.println("<td  align='right' class='hire_posTable_body2_2' >"+(String)abean.get("z0315")+"&nbsp;&nbsp;&nbsp;&nbsp;"+"</td>");//招聘人数
                   		}
                   	 	if(!max_count.equals("1"))
                   		{
                       	out.println("<td  class='hire_posTable_body2'>");
                       	out.println(" <select name='"+(String)abean.get("zp_pos_id")+"' > ");
                       	int thenumber=Integer.parseInt((String)abean.get("thenumber"));
                       	for(int a=1;a<=applyedPositionList.size();a++)
                       	{
                    	    	out.print("<option value='"+a+"' ");
                       		if(a==thenumber)
                    	    		out.print(" selected ");
                    	    	out.print(" >"+ResourceFactory.getProperty("label.page.serial")+a+ResourceFactory.getProperty("hire.wish")+"</option>");
                    	
                        	}
                       	out.print("</select>");
                    //	out.print("<input size=8  class='textbox_noSize' name='"+(String)abean.get("zp_pos_id")+"'  type='text'  value='"+(String)abean.get("thenumber")+"' >");
                    	
                       	out.print("</td>");
                      	}
                    	if(canDelPos)
                        	out.println("<td  class='hire_posTable_body2'><a href='javascript:del(\""+zp_pos_id+"\",\""+a0100+"\",\""+dbName+"\")' >取消应聘</a></td>");
                    	else
                        	out.println("<td class='hire_posTable_body2'>取消应聘</td>");
                   		out.print("</tr>");
                   	}else{
                   		out.println("<td class='hire_posTable_body1'>"+(String)abean.get("posName")+"&nbsp;</td>");
                   		if(resume_state.equalsIgnoreCase("1")){
                    		out.println("<td class='hire_posTable_body1' >"+(String)abean.get("unitName")+"&nbsp;"+"</td>");//状态
                   		} 
                   		if(z0329.equalsIgnoreCase("1")){
                    		out.println("<td class='hire_posTable_body1' >"+(String)abean.get("z0329")+"&nbsp;"+"</td>");//开始日期
                    		//out.println("<td class=rptItemMain nowrap >"+(String)abean.get("z0331")+"</td>");//结束日期
                   		} 
                   		if(z0333.equalsIgnoreCase("1")){
                    		out.println("<td class='hire_posTable_body1' >"+(String)abean.get("z0333")+"&nbsp;"+"</td>");//工作地点
                   		} 
                   		if(z0315.equalsIgnoreCase("1")){
                    		out.println("<td align='right' class='hire_posTable_body1_1'>"+(String)abean.get("z0315")+"&nbsp;&nbsp;&nbsp;&nbsp;"+"</td>");//招聘人数
                   		}
                   	 	if(!max_count.equals("1"))
                   		{
                       	out.println("<td class='hire_posTable_body1'>");
                       	out.println(" <select name='"+(String)abean.get("zp_pos_id")+"' > ");
                       	int thenumber=Integer.parseInt((String)abean.get("thenumber"));
                       	for(int a=1;a<=applyedPositionList.size();a++)
                       	{
                    	    	out.print("<option value='"+a+"' ");
                       		if(a==thenumber)
                    	    		out.print(" selected ");
                    	    	out.print(" >"+ResourceFactory.getProperty("label.page.serial")+a+ResourceFactory.getProperty("hire.wish")+"</option>");
                    	
                        	}
                       	out.print("</select>");
                    //	out.print("<input size=8  class='textbox_noSize' name='"+(String)abean.get("zp_pos_id")+"'  type='text'  value='"+(String)abean.get("thenumber")+"' >");
                    	
                       	out.print("</td>");
                      	}
                    	if(canDelPos)
                        	out.println("<td class='hire_posTable_body1'><a href='javascript:del(\""+zp_pos_id+"\",\""+a0100+"\",\""+dbName+"\")' >取消应聘</a></td>");
                    	else
                        	out.println("<td class='hire_posTable_body1'>取消应聘</td>");
                    		
                    	out.print("</tr>");
                   	}
                     
                  
                   }
                   %>
                   
                  </TBODY>
                </TABLE>
                <table width="100%"  >
               	<TR><TD colspan="10">&nbsp;</TD></TR>
                   	 <%
                   	out.println("<TR>");
              	  	if(applyedPositionList.size()>0&&!max_count.equals("1")){
                			
                			out.println("<TD STYLE='TEXT-ALIGN:right'  colspan='5'>"); 
                			out.println("<img src='/images/hire/savep.gif' border='0' onclick='order(\""+a0100+"\")' value='"+ResourceFactory.getProperty("hire.save.order")+"' style='cursor:pointer;'/>");
                			out.println("</TD>"); 
                		}
                		
                		if(isHeadhunter!=null&&isHeadhunter.equals("1")){ 
                				if(applyedPositionList.size()>0&&!max_count.equals("1"))
                  			out.println("<TD STYLE='TEXT-ALIGN:left'  colspan='5'>");
 							else
 								out.println("<TD STYLE='TEXT-ALIGN:center'    colspan='10'>");
                				out.println("<img src='/images/hire/return.gif' border='0' onclick='javascript :headerBack();'  style='cursor:pointer;'/>");	
                				out.println("</TD>");
                		}
                		out.println("</TR>");
                		%>
                </table>
                 	</div>
                 	<div class='foodsfs' style='margin-top:20px;' > &nbsp;&nbsp;
                 	<table  border="0" cellspacing="0" cellpadding="0" class='table'>
                     <TBODY>                    
                 <%
                    int number =0;
	              	for(int i=0;i<applyedPositionList.size();i++)
	                {
	                	LazyDynaBean abean=(LazyDynaBean)applyedPositionList.get(i);
	                	Boolean canDelPos = (Boolean)abean.get("canDelPos");//判断能否删除对应职位的申请记录
				        int description=(Integer)abean.get("description");
				        if(1==description){
				            number++;
				        }
	                }
                 	for(int i=0;i<applyedPositionList.size();i++)
                   {
                   	LazyDynaBean abean=(LazyDynaBean)applyedPositionList.get(i);
                   	Boolean canDelPos = (Boolean)abean.get("canDelPos");//判断能否删除对应职位的申请记录
                	String posName=(String)abean.get("posName");
                	posName=posName.replaceAll("<br>", "");
			        int description=(Integer)abean.get("description");
			        if(1==description){
			            out.println("<div style='margin-top:20px;'>");
                   	    out.println("<tr>");
                   		out.println(" <td height='25' class='hire_noticeTable_head'>"+posName+"&nbsp;消息通知</td>");
                   		out.print("</tr>");
                   	    out.println("<tr>");
                  		out.println(" <td class='hire_noticeTable_body'>"+(String)abean.get("descripValue")+"</td>");
                  		out.print("</tr>");
                  		if(i!=applyedPositionList.size()-1 && number !=1){
                  		out.println("<tr>");
                    	out.println(" <td height='25'class='hire_noticeTable_body1'  ></td>");
                    	out.print("</tr>");
                  		}
                  		out.println("</div>");
			        }
                   }
                   	%>
                   	 </TBODY>  
                   </table>
                 	</div>
                 	</div>
             	</div>
             	<div class='footer' style='height:0px;' > &nbsp;&nbsp;</div>   
</div>
</div></div>

</html:form>
</body>
</html>
 <script language="javascript">
         
         initCard();
         
       	 Ext.Loader.setConfig({
    			enabled: true,
    			paths: {
    				'EHR.ToolTipUL.ToolTip': '/module/recruitment/js'
    			}
    		});
       	feedback = function (a0100, nbase, zp_pos_id){
     		Ext.require('EHR.ToolTipUL.ToolTip.feedback', function(){
     			Ext.create('EHR.ToolTipUL.ToolTip.feedback',{
     				nbase:nbase,a0100:a0100,zp_pos_id:zp_pos_id,tipId:'imgid'+zp_pos_id});
     		});
			}
</script> 