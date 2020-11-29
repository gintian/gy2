 <%@ page contentType="text/html; charset=UTF-8"%>
 <%
 response.setHeader("Pragma","No-cache"); 
 response.setHeader("Cache-Control","no-store,no-cache"); 
 response.setHeader("Expires", "0"); 
 response.setDateHeader("Expires", 0);  %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
 
<%@ page import="com.hjsj.hrms.actionform.hire.employNetPortal.EmployPortalForm,
			     org.apache.commons.beanutils.LazyDynaBean,
			     java.util.*,com.hrms.hjsj.sys.ResourceFactory"%>
<%@ page import="com.hrms.struts.constant.SystemConfig,com.hrms.frame.codec.SafeCode" %>
<%@ page import="com.hjsj.hrms.actionform.hire.employNetPortal.EmployPortalForm,
			     org.apache.commons.beanutils.LazyDynaBean,com.hrms.hjsj.utils.Sql_switcher,
			     com.hrms.struts.taglib.CommonData,com.hrms.hjsj.sys.Constant,
			     java.util.*"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta  http-equiv="Expires"  CONTENT="0">    
<meta  http-equiv="Cache-Control"  CONTENT="no-cache">    
<meta  http-equiv="Pragma"  CONTENT="no-cache">
<meta  http-equiv="X-UA-Compatible" content="IE=EmulateIE7" /> 
</head>
  <body>
  <html:form action="/hire/hireNetPortal/recommend_resume">
  	<%
  	EmployPortalForm employPortalForm=(EmployPortalForm)session.getAttribute("employPortalForm");
  	String isQueryCondition=employPortalForm.getIsQueryCondition();
  	if(employPortalForm.getUserName()==null||employPortalForm.getUserName().equals("")){
		employPortalForm.setA0100("");
	}
	String a0100=employPortalForm.getA0100();
	if(a0100==null||(a0100!=null&&a0100.trim().length()==0)){//非登录状态查询时 清空cookies信息
	%>
		<script language='javascript'>
			var date1 = new Date(); 
			date1.setTime(date1.getTime() - 10000); 
			document.cookie ="hjsjpos=" + "555" + ";expires=" + date1.toGMTString();
		</script >
	<%				
		Cookie[] ck=request.getCookies();
		if(ck!=null){
			for(int k=0;k<ck.length;k++){
				ck[k].setMaxAge(0);	
				Cookie cookie = new Cookie(ck[k].getName(), null);   
				response.addCookie(cookie);	
			}
	   }
	}
	String dbName=employPortalForm.getDbName();
	String hirechannel=employPortalForm.getHireChannel();
    String userName =employPortalForm.getUserName()==null?"":employPortalForm.getUserName();
    ArrayList unitList=employPortalForm.getUnitList();
    String type=request.getParameter("returnType");
    ArrayList conditionFieldList=employPortalForm.getConditionFieldList();
    String hireMajor=employPortalForm.getHireMajor();
    String hireMajorCode=employPortalForm.getHireMajorCode();
    String zpUnitCode=employPortalForm.getZpUnitCode();
    String recommendA0100s = employPortalForm.getRecommendA0100s();
    String recommendUserNames = employPortalForm.getRecommendUserNames();
    
    String reCommendoption = request.getParameter("reCommendoption");
    %>
    <script language='javascript'>
		var reCommendoption = '<%=reCommendoption%>'; 
		var recommendA0100s = '<%=recommendA0100s%>'; 
		var recommendUserNames = '<%=recommendUserNames%>'; 
		function query(flag,id)
		{
			if(flag==1){
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
									alert("<bean:write  name="element" property="itemdesc"/>"+OVER_LENGTH_SCOPE);
									return;
								}
							}
						
						</logic:equal>
						<logic:notEqual name="element" property="codesetid" value="0">
							<logic:equal name="element" property="isMore" value="1">
							var a<%=m%>=document.getElementsByName("conditionFieldList[<%=m%>].value")
							var aa<%=m%>=document.getElementsByName("conditionFieldList[<%=m%>].viewvalue")
							if(trim(aa<%=m%>[0].value).length==0)
							{							
								a<%=m%>[0].value="";
							}
							</logic:equal>
						</logic:notEqual>
						
					</logic:equal>			
				<% m++;} %>	
			</logic:iterate>	
			<%if(request.getParameter("isAll")!=null){%>
			   
			    document.employPortalForm.action="/hire/hireNetPortal/search_zp_position.do?b_query=link&isAll=<%=request.getParameter("isAll")%>&returnType=search";
			    document.employPortalForm.submit();
			<%}else{%>
		    	document.employPortalForm.action="/hire/hireNetPortal/search_zp_position.do?b_query=link&returnType=search";
			    document.employPortalForm.submit();
			<%}%>
			}else{
				document.employPortalForm.action="/hire/hireNetPortal/search_zp_position.do?b_query=link&operate=init&hireChannel=${employPortalForm.hireChannel}&zpUnitCode=${employPortalForm.zpUnitCode}&isAllPosOk=1&returnType=search&selunitcode="+id;
				  document.employPortalForm.submit();
			}
		}
	</script >
    <%
    LazyDynaBean kbean=null;
    ArrayList kll=new ArrayList();
    int hi=0;  
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
    
   // if(type==null)
	//	    type="1";
  	%>
  	<html:hidden name="employPortalForm" property="hireChannel"/>
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
                    	   		<li><a href="/hire/hireNetPortal/recommend_resume.do?b_recommendResume=query"><bean:message key="hire.out.resume.recommend"/></a></li><!-- 推荐简历 -->
		            	      	<!--<li><a href="###"><bean:message key="hire.out.position.recommend"/></a></li><!-- 推荐岗位 -->
		            	      	<li><a href="/hire/hireNetPortal/search_zp_position.do?b_query=link&returnType=headHunter&hireChannel=headHire"><bean:message key="hire.out.position.employment"/></a></li><!-- 招聘岗位 -->
		            	      	<li><a href="/hire/hireNetPortal/search_zp_position.do?br_editPassword=edit"><bean:message key="label.banner.changepwd"/></a></li><!--修改密码 -->
		            	      	<li><a href="javascript:exit()">退出登录</a></li>
                    	   	</ul>
						</div>
					</div>
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
                    	<!-- 这个 promptContent 代表什么一直没看明白 -->
                     <div class="promt" id="board">
                     	${employPortalForm.promptContent}
                     </div>
				</div>
				<div class="right3"  id='rg'  style='min-height:<%=hi%>px;margin-bottom:30px'>
					<h1></h1>
					<%if(conditionFieldList!=null&&conditionFieldList.size()>0){  %>
					<div class="search">
						<h3>职位搜索</h3>
                     	 <div class="xia">
								<%
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
									String isMore=(String)abean.get("isMore");
									String itemdesc=(String)abean.get("itemdesc");
									String value=(String)abean.get("value");
									String viewvalue=(String)abean.get("viewvalue");
									out.print(""+itemdesc+"</span>");
									if(itemtype.equals("A"))
									{
										if(codesetid.equals("0"))
										{//是非代码类
											if(itemid.equalsIgnoreCase(hireMajor) && !(hireMajorCode==null || hireMajorCode.equals("-1"))){//是招聘专业指标
												out.print("<div class=\"input_bg2\" style='width:110px;'><input  class='TEXT' type='text' name='conditionFieldList["+i+"].viewvalue' value='"+viewvalue+"'  size='10'  style='width:85px'  /><span  style='float:right;margin-top:-17px;'><img style='float:right;valign:top;' src='/images/code.gif' onclick='javascript:openInputCodeDialog2(\""+hireMajorCode+"\",\"conditionFieldList["+i+"].viewvalue\",\"\",\""+hirechannel+"\",\"1\");'/></span></div>");
												isprint=true;
									   			selecnum=selecnum+i+"@"+value+"`";
											}
											else
												out.println("<div class=\"search_input_bg\" style='width:110px;'><input name=\"conditionFieldList["+i+"].value\"  class='textbox' type=\"text\" value=\""+value+"\" size='18' /></div>");
										}
										else
										{
											if(isMore.equals("0")&&false)
											{
												ArrayList options=(ArrayList)abean.get("options");												
												out.print("<div class='input' id='input"+i+"'");												
												out.print(" style='width:122px'>");
												out.print(" <div class='floor' style='outline:none' tabindex=\"0\" id='floor"+i+"' onblur=\" hide3('"+i+"');\"> ");
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
												out.print("<input type='hidden' name='conditionFieldList["+i+"].value' value='"+selectedvalue+"'/>");
												out.print("</div><span id='spank"+i+"' class='img'>");
												out.print("<a href='javascript:void(0);' onclick='showlist("+i+");'><img src='/images/hire/xia.gif'/></a>");
												out.print(" </span><span style='overflow:overflow;padding-right:0px;margin-right:-2px;margin-left:1.9px' FONT-SIZE: 12px;font-family: 微软黑体;color:black; id='span"+i+"'>");	
												out.print(selected+" </span></div>");	
											}
											else
											{
								              	out.print("<div class=\"input_bg2\" style='width:110px;'><input  class='' type='text' name='conditionFieldList["+i+"].viewvalue' value='"+viewvalue+"'  size='10'  style='width:85px'  /><span  style='float:right;margin-top:-17px;margin-left:10px'><img style='float:right;valign:top;' src='/images/code.gif' onclick='javascript:openInputCodeDialog2(\""+codesetid+"\",\"conditionFieldList["+i+"].viewvalue\",\"\",\""+hirechannel+"\",\"1\");'/></span></div>");
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
										out.println("<div class=\"input_bg1\" style='width:110px;'><input  name=\"conditionFieldList["+i+"].value\" style='width:100px;FONT-SIZE: 12px;font-family: 微软黑体;color:black;  class='TEXT' type=\"text\"  size='15'/></div>");
									}
								}
								
								%>
								<!-- <td align="right">
									<a href="javascript:query();"><img src="/images/hire/sarch.gif" hspace="10"/></a>
								</td>
							</tr>
						</table>   --> 								
							<a href="javascript:query(1);" style="margin-left:10px" id="img2"><img src="/images/hire/sarch.gif" /></a> <!-- 这个界面的搜索连接一会要修改一下 -->
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
                   	 <h3><span>招聘职位</span></h3>
                   	 	 <div class="nr" >
                   	 	 <%if(type!=null&&type.length()!=0&&type.equalsIgnoreCase("search")){ //岗位搜索 
						  String positionNumber=employPortalForm.getPositionNumber();
						
						  String hireChannel=employPortalForm.getHireChannel();
						  ArrayList posFieldList  = employPortalForm.getPosFieldList();
						  String selunit=employPortalForm.getSelunit();
						  int pcount=Integer.parseInt(positionNumber);
				  		  if(request.getParameter("unitCode")==null)
				  		  {
					  		ArrayList sunitList=employPortalForm.getSunitlist();
					  		int n=0;
					  		for(Iterator t=sunitList.iterator();t.hasNext();)
					  		{
						  		LazyDynaBean aBean=(LazyDynaBean)t.next();
					  			String unitName=(String)aBean.get("name");
					  			String id=(String)aBean.get("id");
					  			ArrayList posList=(ArrayList)aBean.get("list");
					  			String content=(String)aBean.get("content");
					  			String contentType=(String)aBean.get("contentType");
                  %>
                  		<!-- 每一个单位创建一个table,每一个岗位创建一个tr -->
		                  <table   cellSpacing=0 cellPadding=1 width="100%" align="center" style='margin-top:10px' border=0 >
		                    <tbody>
		                      <tr align="left"><!-- 输出每个单位的名字 --> 
		                        <td colspan="5" class="hj_zhaopin_h1"><%=unitName%>
		                         <%
		                       		if(n!=0)
		                        		out.print("<br>");
		                        	n++;
		                        %>
		                        </td>
		                      </tr>
		                      <!-- 然后挨着输出该单位的需求职位 -->
		                      <tr><td width="100%">
		                      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="table">
		                      <tr align="center" > 
		                         <%if(posFieldList!=null&&posFieldList.size()>0){//所有前台要显示的字段 (输出表头)
		                         		for(int y=0;y<posFieldList.size();y++){
		                           		 LazyDynaBean abean = (LazyDynaBean)posFieldList.get(y);
			                             String itemid=(String)abean.get("itemid");
			                             String itemdesc=(String)abean.get("itemdesc");
			                             if(itemid.equalsIgnoreCase("Z0351")){
			                                  itemdesc=ResourceFactory.getProperty("hire.out.employ.position");
		                         %>
			                               <td width="20%" class='table_line_title'><b><%=itemdesc%></b></td>
			                     <%
			                            }else{
			                     %>
			                               <td class='table_line_title'><b><%=itemdesc%></b></td>
			                     <%
			                            }
			                          }  
			                     %>
		                      
		                      <%}else{ %>
		                      		<td width="40%" class='table_line_title' >
		                      			<B><!-- 如果没配置外网显示指标,显示默认的指标 -->
				                       		<bean:message key="hire.out.employ.position"/>
			                        	</b>
		                        	</td>
		                        	<td width="15%" class='table_line_title'><b><bean:message key="lable.zp_plan_detail.domain"/></b> </td><!-- 工作地点 -->
		                        	<td width="15%" class='table_line_title'><b><bean:message key="label.zp_release_pos.valid_date"/></b></td><!-- 发布日期 -->
			                        <!--推荐人数 -->
			                        <td width="15%" class='table_line_title'><b> <bean:message key="hire.out.headhunter.recommend.count"/></b> </td>
					                <td width="10%" class='table_line_title'><b> <bean:message key="hire.out.headhunter.recommend"/></b> </td><!-- 推荐 -->
		                         </tr>
		                      <%}%>
		                      <%  
								  int colspan=5;
								  if(posFieldList!=null&&posFieldList.size()>0){
								     colspan=posFieldList.size();;
								     for(int i=0;i<posList.size();i++){//每一个招聘岗位的相关信息 
								        if((i>(pcount-1)&&request.getParameter("isAllPosOk")==null)||(i>(pcount-1)&&request.getParameter("isAllPosOk")!=null)){
									        if(selunit!=null&&selunit.indexOf(","+id+",")!=-1){//如果超过了显示的个数，但是这个是要查询的招聘职位信息,那么也要显示出来 
									        
									        }else
								  				break;
								  		}
								  		out.println("<tr>");
								  		LazyDynaBean bean=(LazyDynaBean)posList.get(i);
								  		String state=(String)bean.get("state");
								  		String isNewPos = (String)bean.get("isNewPos");
								  		String z0301=(String)bean.get("z0301");
								  		String p="";
								  		String posName=(String)bean.get("Z0351");
								  		for(int y=0;y<posFieldList.size();y++){
								  		    LazyDynaBean abean=(LazyDynaBean)posFieldList.get(y);
								  		    String itemid=((String)abean.get("itemid")).toLowerCase();
								  		    String itemtype=(String)abean.get("itemtype");
								  		    String value=(String)bean.get(itemid);
								  		    if(value==null||value.equals(""))
								  		       value="&nbsp;";
								  		    if(itemid.equalsIgnoreCase("Z0351")){
								  		       value="<a onclick=\"rediractForRecommend('"+z0301+"','"+SafeCode.encode(posName)+"','"+unitName+"','recommend','"+id+"')\"  class='hire_posTable_href'> "+value+"</a>";
								  		    }else if(itemid.equalsIgnoreCase("tjjl")){
								  		       value="<a onclick=\"recommendCheck('"+z0301+"','"+SafeCode.encode(posName)+"')\"  class='hire_posTable_href'> "+value+"</a>";
								  		    }
								  		    if(y==0){
								  		       if(state.equals("1")){ 
								  		          value+="<IMG border=0 src='/images/hot.gif' >"; 
								  		        }
								  		        if(isNewPos.equals("1")){
								  		               value+="<IMG border=0 src='/images/new0.gif' />";
								  		         }
								  		    }
								  		     String align="align=\"center\"";
								  		     %>
								  		     <%if(i%2==0) {%>
								  		     	<td height="36" <%=align%>><%=value%></td>
								  		     <%}else{ %>
								  		     	<td height="36" <%=align%>  class='table_line_single' ><%=value%></td>
								  		     <%}    
								  		}
								  		out.println("</tr>");	
								     }   
								  }else{//如果没有指定招聘外网显示的字段，就显示默认的
								  	for(int i=0;i<posList.size();i++){
								  		if(i>(pcount-1)&&request.getParameter("isAllPosOk")==null){
								  			
								  			 if(selunit!=null&&selunit.indexOf(","+id+",")!=-1){
									        
									        }else
								  				break;
								  		}
								  		LazyDynaBean bean=(LazyDynaBean)posList.get(i);
								  		String posName=(String)bean.get("posName");//职位名称 
								  		String z0333=(String)bean.get("z0333");//工作地点 
								  		String Z0331=(String)bean.get("z0331");//结束日期
								  		String Z0329=(String)bean.get("z0329");//有效起始日期
								  		String z0301=(String)bean.get("z0301");//序号
								  		String z0311=(String)bean.get("z0311");//需求岗位
								  		String state=(String)bean.get("state");
								  		String isNewPos=(String)bean.get("isNewPos");
								  		String count=(String)bean.get("count");
								  		String z0313=(String)bean.get("z0313");
								  		if(z0333==null||z0333.trim().equals(""))
								  		       z0333="&nbsp;";
								  		if(Z0329==null||Z0329.trim().equals(""))
								  		      Z0329="&nbsp;";
								  		if(count==null||count.trim().equals(""))
								  		      count="&nbsp;";
								  		if(z0313==null||z0313.trim().equals(""))
								  		    z0313="&nbsp;";
								  		String p="";
								  %>
									 <tr >
								  <%
								  	if(i%2==0) {
								   %><!--不同的行显示不同的背景色  -->
					                        <td align="center" height='36'>   
					                          &nbsp;&nbsp;                          
						                          <a onclick="rediractForRecommend('<%=z0301%>','<%=SafeCode.encode(posName)%>','<%=unitName%>','recommend','<%=id %>');"  class='hire_posTable_href'> 
						                           <%=posName%> 
						                           <% if(state.equals("1")){ out.print("<IMG border=0 src='/images/hot.gif' >");  } %><!-- 热门职业 -->
						                           <%if(isNewPos.equals("1")) { %>
								  		               <IMG border=0 src='/images/new0.gif' />
								  		           <%} %>
						                         </a>                           
					                        </td>
					                       	<td height="20" align="center" height='36'><%=z0333%></td>
					                      	<td align="center" height='36'><%=Z0329%></td>
							                <td style="TEXT-AlIGN:center" height='36'><%=count%>&nbsp;</td>
							                <td style="text-align:center" height='36'>
							                    <a onclick="recommendCheck('<%=z0301%>','<%=SafeCode.encode(posName)%>');"  class='hire_posTable_href'>
							                      <bean:write name="hire.out.headhunter.recommend" />
							                    </a>
							                </td>
				                 <%	
					                }else{ 
					             %>
				                         	<td align="center"  class='table_line_single' height='36'>   
				                          	&nbsp;&nbsp;                          
					                          <a onclick="rediractForRecommend('<%=z0301%>','<%=SafeCode.encode(posName)%>','<%=unitName%>','recommend','<%=id %>');"  class='hire_posTable_href'> 
					                            <%=posName%> 
					                           <% if(state.equals("1")){ out.print("<IMG border=0 src='/images/hot.gif' >");  } %>
					                           <%if(isNewPos.equals("1")) {%>
								  		               <IMG border=0 src='/images/new0.gif' />
								  		         <%} %>
					                         </a>                           
					                        </td>
					                       	<td height="20" align="center"    class='table_line_single'><%=z0333%></td>
					                      	<td align="center"   class='table_line_single'><%=Z0329%></td>
						                    <td style="TEXT-AlIGN:center"  class='table_line_single'><%=count%>&nbsp;</td>
						                    <td style="text-align:center" class='table_line_single'>
						                       <a onclick="recommendCheck('<%=z0301%>','<%=SafeCode.encode(posName)%>');"  class='hire_posTable_href'> 
						                      			 	<bean:write name="hire.out.headhunter.recommend" />
						                       </a>
						                    </td>
		                         <%
			                      } 
			                     %>
		                         	</tr>
		                         
		                         <%
			                   	  	}
			                   	  %>
			                        </table>
			                        	</td>
			                      </tr>
			                        <% 	if((posList.size()>(pcount)&&request.getParameter("isAllPosOk")==null)||(posList.size()>(pcount)&&request.getParameter("isAllPosOk")!=null))
			                   	  		{ 
											if(selunit!=null&&selunit.indexOf(","+id+",")!=-1){
										        
										   	}else{%>	
			                   	  	
			                   	   <tr> 
			                       		<td height="20" style="TEXT-ALIGN:right" width="715px"> 
			                      	  		<a onclick="query(2,'<%=id %>');return false;" href="javascript:void(0);"><img src="/images/hire/more.gif" border="0"/></a>
			                      		</td>
			                      </tr>
			                       <% }} %>
			                      
			                    </tbody>
			                  </table>
			                  
	                   <%
		                  }
		     			}%>
                     <%
                     }
				  		  }else{//开始处理默认进来的情况：不是查询岗位  也不是 已浏览岗位 
			       %>
			       <% 
									  String positionNumber=employPortalForm.getPositionNumber();
									
									  String hireChannel=employPortalForm.getHireChannel();
									  ArrayList posFieldList  = employPortalForm.getPosFieldList();
									  String selunit=employPortalForm.getSelunit();
									  int pcount=Integer.parseInt(positionNumber);
							  		  if(request.getParameter("unitCode")==null)//当点击more的时候会传递more所对应单位的部门过来 
							  		  {
								  		ArrayList zpPosList=employPortalForm.getZpPosList();
								  		int n=0;
								  		for(Iterator t=zpPosList.iterator();t.hasNext();)
								  		{
									  		LazyDynaBean aBean=(LazyDynaBean)t.next();
								  			String unitName=(String)aBean.get("name");
								  			String id=(String)aBean.get("id");
								  			ArrayList posList=(ArrayList)aBean.get("list");
								  			String content=(String)aBean.get("content");
								  			String contentType=(String)aBean.get("contentType");
			                  %>
					                  <table   cellSpacing=0 cellPadding=1 width="100%" align="center" style='margin-top:10px' border=0 >
					                    <tbody>
					                      <tr align="left"> 
					                        <td colspan="5" class="hj_zhaopin_h1"><%=unitName%>
					                         <%
					                       		if(n!=0)
					                        		out.print("<br>");
					                        	n++;
					                         %>
					                        </td>
					                      </tr>
					                      <tr><td width="100%">
					                      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="table">
					                      <tr align="center" > 
					                           <%if(posFieldList!=null&&posFieldList.size()>0){//设置了招聘外网显示指标 ,生成表头
					                        		 int width=15;
					                         		if(posFieldList.size()<5)
					                            		width=15;
					                         		for(int y=0;y<posFieldList.size();y++){
					                           		 LazyDynaBean abean = (LazyDynaBean)posFieldList.get(y);
						                             String itemid=(String)abean.get("itemid");
						                             String itemdesc=(String)abean.get("itemdesc");
						                             if(itemid.equalsIgnoreCase("Z0351")){
						                                  itemdesc=ResourceFactory.getProperty("hire.out.employ.position");
					                          		%>
						                               <td width="20%" class='table_line_title'><b><%=itemdesc%></b></td>
						                      		<%
						                            }else{
						                     		%>
						                               <td class='table_line_title'><b><%=itemdesc%></b></td>
						                      		<%
						                            }
						                         }
						                       %>
					                      <%}else{//没有外网显示指标,用默认的指标生成表头   %>
					                      		<td width="40%" class='table_line_title' >
					                      		   <B><bean:message key="hire.out.employ.position"/></B>
					                        	</td>
					                        	<td width="15%" class='table_line_title'><b><bean:message key="lable.zp_plan_detail.domain"/></b> </td>
					                        	<td width="15%" class='table_line_title'><b><bean:message key="label.zp_release_pos.valid_date"/></b></td>
						                        <td width="15%" class='table_line_title'><b> <bean:message key="hire.out.headhunter.recommend.count"/></b> </td>
						                        <td width="10%" class='table_line_title'>
							                      <b> 
							                        <bean:message key="hire.out.headhunter.recommend"/>
							                      </b> 
						                        </td>
					                       	<%
					                        	} 
					                        %>
					                         </tr>
					                          <%  
											  int colspan=5;
											  if(posFieldList!=null&&posFieldList.size()>0){//展现外网显示指标 
											     colspan=posFieldList.size();;
											     for(int i=0;i<posList.size();i++) 
											     {
											        if((i>(pcount-1)&&request.getParameter("isAllPosOk")==null)||(i>(pcount-1)&&request.getParameter("isAllPosOk")!=null)){
												        if(selunit!=null&&selunit.indexOf(","+id+",")!=-1){
												        
												        }else
											  				break;
											  		}
											  		out.println("<tr>");
											  		LazyDynaBean bean=(LazyDynaBean)posList.get(i);
											  		String state=(String)bean.get("state");
											  		String isNewPos = (String)bean.get("isNewPos");
											  		String z0301=(String)bean.get("z0301");
											  		String posName=(String)bean.get("z0351");//显示职位的信息
											  		String p="";
											  		for(int y=0;y<posFieldList.size();y++){
											  		    LazyDynaBean abean=(LazyDynaBean)posFieldList.get(y);
											  		    String itemid=((String)abean.get("itemid")).toLowerCase();
											  		    String itemtype=(String)abean.get("itemtype");
											  		    String value=(String)bean.get(itemid);
											  		    
											  		  	String z0321Name =(String) bean.get("z0321Name");
											  		  	if(itemid.equalsIgnoreCase("z0321")){
											  		    	value=z0321Name;
											  		    }
											  		  	
											  		    if(value==null||value.equals(""))
											  		       value="&nbsp;";
											  		    
											  		    if(itemid.equalsIgnoreCase("z0351")){
											  		    	value="<a onclick=\"rediractForRecommend('"+z0301+"','"+SafeCode.encode(posName)+"','"+unitName+"','recommend','"+id+"')\"  class='hire_posTable_href'> "+value+"</a>";
											  		    }else if(itemid.equalsIgnoreCase("tjjl")){
											  		    	value="<a onclick=\"recommendCheck('"+z0301+"','"+SafeCode.encode(posName)+"')\"  class='hire_posTable_href'> "+value+"</a>";
											  		    }
											  		    if(y==0){
											  		       if(state.equals("1")){ 
											  		          value+="<IMG border=0 src='/images/hot.gif' >"; 
											  		        }
											  		       
											  		         if(isNewPos.equals("1"))
											  		         {
											  		               value+="<IMG border=0 src='/images/new0.gif' />";
											  		         }
											  		    }
											  		     String align="align=\"center\"";
											  		     if(itemtype.equalsIgnoreCase("N"))
											  		          align="align=\"center\"";
											  		     %>
											  		     <%if(i%2==0) {%>
											  		     		<td height="36" <%=align%> >&nbsp;&nbsp;<%=value%>&nbsp;&nbsp;</td>
											  		     <%}else{ %>
											  		     		<td height="36" <%=align%>  class='table_line_single1' >&nbsp;&nbsp;<%=value%>&nbsp;&nbsp;</td>
											  		     <%}    
											  		}
											  		out.println("</tr>");	
											     }   
											  }
											  else{//未设置招聘外网展现指标采用默认的 
												  if(posList!=null&&posList.size()>0){
													  	for(int i=0;i<posList.size();i++){
													  		if(i>(pcount-1)&&request.getParameter("isAllPosOk")==null){
													  			
													  			 if(selunit!=null&&selunit.indexOf(","+id+",")!=-1){
														        
														        }else
													  				break;
													  		}
													  		LazyDynaBean bean=(LazyDynaBean)posList.get(i);
													  		String posName=(String)bean.get("posName");//显示职位的信息 
													  		String z0333=(String)bean.get("z0333");
													  		String Z0331=(String)bean.get("z0331");
													  		String Z0329=(String)bean.get("z0329");
													  		String z0301=(String)bean.get("z0301");
													  		String state=(String)bean.get("state");
													  		String isNewPos=(String)bean.get("isNewPos");
													  		String count=(String)bean.get("count");
													  		String z0313=(String)bean.get("z0313");
													  		if(z0333==null||z0333.trim().equals(""))
													  		       z0333="&nbsp;";
													  		if(Z0329==null||Z0329.trim().equals(""))
													  		      Z0329="&nbsp;";
													  		if(count==null||count.trim().equals(""))
													  		      count="&nbsp;";
													  		if(z0313==null||z0313.trim().equals(""))
													  		    z0313="&nbsp;";
													  		String p="";
													  %>
														 <tr > 
														 <%if(i%2==0) { %>
										                        <td align="center" height='36'>   
										                          &nbsp;&nbsp;                          
											                          <a onclick="rediractForRecommend('<%=z0301%>','<%=SafeCode.encode(posName)%>','<%=unitName%>','recommend','<%=id %>');"  class='hire_posTable_href'> 
											                           <%=posName%> <!-- 职位的描述 -->
											                           <% if(state.equals("1")){ out.print("<IMG border=0 src='/images/hot.gif' >");  } %><!-- 热点职位 -->
											                           <% if(isNewPos.equals("1")){ out.print("<IMG border=0 src='/images/new0.gif' />");  } %><!-- 新职位 -->
											                         </a>                           
										                        </td>
										                        
										                       	<td height="20" align="center" height='36'><%=z0333%></td>
										                       	
										                      	<td align="center" height='36'><%=Z0329%></td>
											                    <td style="TEXT-AlIGN:center" height='36'><%=count%>&nbsp;</td>
											                    <td style="text-align:center" height='36'>
											                       <a onclick="recommendCheck('<%=z0301%>','<%=SafeCode.encode(posName)%>');"  class='hire_posTable_href'>
											                         <bean:message key="hire.out.headhunter.recommend"/>
											                        </a>
											                     </td>
									                         <%
										                      }else{ 
										                      %>
									                         	<td align="center"  class='table_line_single' height='36'>   
									                          &nbsp;&nbsp;                          
										                          <a onclick="rediractForRecommend('<%=z0301%>','<%=SafeCode.encode(posName)%>','<%=unitName%>','recommend','<%=id %>');"  class='hire_posTable_href'> 
										                            <%=posName%> 
										                           <% if(state.equals("1")){ out.print("<IMG border=0 src='/images/hot.gif' >");  } %>
										                           <%if(isNewPos.equals("1")) { out.print("<IMG border=0 src='/images/new0.gif' />");  } %>
										                         </a>                           
										                        </td>
										                        
										                       	<td height="20" align="center"    class='table_line_single'><%=z0333%></td>
										                       	
										                      	<td align="center"   class='table_line_single'><%=Z0329%></td>
											                    <td style="TEXT-AlIGN:center"  class='table_line_single'><%=count%>&nbsp;</td>
											                    <td style="text-align:center" class='table_line_single'>
											                        <a onclick="recommendCheck('<%=z0301%>','<%=SafeCode.encode(posName)%>');"  class='hire_posTable_href'> 
											                         	<bean:message key="hire.out.headhunter.recommend"/>
											                        </a>
											                    </td>
									                         <%
										                      } 
										                     %>
							                         	</tr>
							                         
							                         <%
								                   	  	}
												  }

						                   	  	}
						                   	  %>
						                        	</table>
						                        	</td>
						                      
						                      </tr>
						                        <% 	
						                        if(posList!=null&&posList.size()>0){
							                        if((posList.size()>(pcount)&&request.getParameter("isAllPosOk")==null)||(posList.size()>(pcount)&&request.getParameter("isAllPosOk")!=null))
						                   	  		{ 
														if(selunit!=null&&selunit.indexOf(","+id+",")!=-1){
													        
													   	}else{%>	
						                   	  	
						                   	   <tr> 
						                       		<td height="20" style="TEXT-ALIGN:right" width="715px"> 
						                      	  		<a onclick="query(2,'<%=id %>');return false;" href="javascript:void(0);"><img src="/images/hire/more.gif" border="0"/></a>
						                      		</td>
						                      </tr>
						                       <% }
						                        }
												}
												%>
						                      
						                    </tbody>
						                  </table>
						                  
				                   <%
					                  }
					     			}
					     			%>
			      					<%
			      					}
                     				%>
                </div>
			</div>
		</div>	                
  	</div>
  </html:form>
  </body>
</html>
