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
  <html:form action="/hire/hireNetPortal/recommend_positionResume">
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
    String zpUnitCode=employPortalForm.getZpUnitCode();
    String z0301 = employPortalForm.getRecommendZ0301();
    String posName = employPortalForm.getRecommendPosName();
    ArrayList recommendTbaleList = employPortalForm.getRecommendTbaleList();
   	String containAge = "false";
   	String containPhone = "false";
   	for(int i =0;i<recommendTbaleList.size();i++){
   		if(containAge.equals("true")&&containPhone.equals("true")){
   			break;
   		}
   		LazyDynaBean bean=(LazyDynaBean)recommendTbaleList.get(i);
   		String itmeid = (String)bean.get("itemid");
   		if("C0101".equals(itmeid)){
   			containAge = "true";
   		}
   		if("C0104".equals(itmeid)){
   			containPhone="true";
   		}
   	}
   	pageContext.setAttribute("containAge", containAge);
   	pageContext.setAttribute("containPhone", containPhone);
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
					<table width="100%"  cellspacing="0" cellpadding="0" class="newHeadHunterTable">
						<tr align="center" class="fixedTr">
							<td class="newtable_line_title"  width="20%">
								<input type="checkbox" name="selbox" onclick="batch_select(this,'recommendUserListForm.select');"
								 title='<bean:message key="label.query.selectall"/>'>
							</td>
							<logic:iterate id="column" name="employPortalForm" property="recommendTbaleList"  indexId="number" >
								<logic:equal name="column" property="itemid" value="A0101">
									<td class="newtable_line_title" width="20%">
										<bean:write name="column" property="itemdesc"/>
									</td>
								</logic:equal> 
								<logic:notEqual name="column" property="itemid" value="A0101">
									<logic:equal name="column" property="itemid" value="operation">
									</logic:equal>
									<logic:notEqual name="column" property="itemid" value="operation">
									
										<logic:equal name="column" property="itemid" value="recommendPositions">
											<td class="newtable_line_title" width="70px">
												<bean:write name="column" property="itemdesc"/>
											</td>
										</logic:equal>
										
										<logic:notEqual name="column" property="itemid" value="recommendPositions">
											<td class="newtable_line_title">
												<bean:write name="column" property="itemdesc"/>
											</td>
										</logic:notEqual>
										
									</logic:notEqual>
								</logic:notEqual>
									
							</logic:iterate>
						</tr>
						<hrms:extenditerate id="element" name="employPortalForm" property="recommendUserListForm.list" indexes="indexes"
								pagination="recommendUserListForm.pagination" pageCount="5" scope="session" >
								<tr>
									<td class="newtable_line_nomral">
										<hrms:checkmultibox name="employPortalForm" property="recommendUserListForm.select" value="true" indexes="indexes" />
										<input type="hidden" id='select<bean:write name="indexes"/>'value='<bean:write name="element" property="a0100" filter="false"/>' viewvalue='<bean:write name="element" property="A0101" filter="false" />'/>
									</td>
									<td class="newtable_line_nomral">
										<bean:write name="element" property="A0101" filter="false" />
									</td>
									<logic:present name="element" property="A0107">
										<td class="newtable_line_nomral"><bean:write name="element" property="A0107" filter="false" /></td>
									</logic:present>
									<logic:equal name="containAge" value="true">
										<td class="newtable_line_nomral"><bean:write name="element" property="C0101" filter="false"  /></td>
									</logic:equal>
									<logic:equal name="containPhone" value="true">
										<td class="newtable_line_nomral"><bean:write name="element" property="C0104" filter="false"  /></td>
									</logic:equal>
									<td class="newtable_line_nomral">
										<bean:write name="element" property="recommendPositions" filter="false"/>
									</td>
								</tr>
						</hrms:extenditerate>
					</table>
					 
					<table cellspacing="0" cellpadding="0" style="border-top:0px;" class="newHeadHunterTable">
						<tr>
							<td valign="middle" class="tdFontcolor">
						            <bean:message key="label.page.serial"/>
									<bean:write name="employPortalForm" property="recommendUserListForm.pagination.current" filter="true" />
									<bean:message key="label.page.sum"/>
									<bean:write name="employPortalForm" property="recommendUserListForm.pagination.count" filter="true" />
									<bean:message key="label.page.row"/>
									<bean:write name="employPortalForm" property="recommendUserListForm.pagination.pages" filter="true" />
									<bean:message key="label.page.page"/>
									 <!--每页显示<html:text styleClass="text4" property="pagerows" name="employPortalForm" size="3"></html:text>条&nbsp;&nbsp;<a href="javascript:_refrash();">刷新</a>-->
							</td>
					        <td  align="right" nowrap class="tdFontcolor">
						          <p align="right">
						          <hrms:paginationlink name="employPortalForm" property="recommendUserListForm.pagination" nameId="recommendUserListForm" >
								</hrms:paginationlink>
						    </td>
					  </tr>
					</table>
					<div  style="position:relative;left:0.5%;top:15px;">
						<table>
							<tr>
								<td><input type="button" class="imageButton" value = "<bean:message key="hire.out.position.recommend"/>" onclick="batchRecommendValiateForposition('<%=z0301%>','<%=posName%>')"/></td>
							</tr>
						</table>
					</div>
                </div>
			</div>
		</div>	                
  	</div>
  </html:form>
  </body>
</html>