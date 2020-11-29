<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/hire/employNetPortal/employNetPortal.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<%@ page import="com.hjsj.hrms.actionform.hire.employNetPortal.EmployPortalForm,com.hrms.hjsj.utils.Sql_switcher,com.hrms.hjsj.sys.Constant,
			     org.apache.commons.beanutils.LazyDynaBean,com.hjsj.hrms.utils.PubFunc,
			     java.util.*"%>
			     <%@ page import="com.hrms.hjsj.sys.ResourceFactory"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<html>
<head>	
<LINK 
href="/css/employNetStyle.css" type=text/css rel=stylesheet>
<LINK href="/css/main.css" type=text/css rel=stylesheet>
<LINK href="/css/nav.css" type=text/css rel=stylesheet>
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

  %>
<script language='javascript'>
function query()
	{
		
		<% int m=0;  %>
		<logic:iterate  id="element"    name="employPortalForm"  property="conditionFieldList" indexId="index"> 
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
			<% m++; %>	
		</logic:iterate>	
		
		document.employPortalForm.action="/hire/employNetPortal/search_zp_position.do?b_query=link";
		document.employPortalForm.submit();
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
  function showPrint(outparamters)
{
   var personlist=outparamters.getValue("personlist");  
   var obj = document.getElementById('CardPreview1');    
   if(obj==null)
   {
      alert("没有下载打印控件，请设置IE重新下载！");
      return false;
   }
   obj.SetCardID(tabid);
   obj.SetDataFlag("1");
   obj.SetNBASE("${employPortalForm.dbName}");
   obj.ClearObjs();   
   if(personlist!=null&&personlist.length>0)
   {
     for(var i=0;i<personlist.length;i++)
     {
       obj.AddObjId(personlist[i].dataValue);
     }
   }
   try { obj.SetJSessionId(AxManager.getJSessionId()); } catch(err) {}
   obj.ShowCardModal();
   
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
</script>
</head>
<body onKeyDown="return pf_ChangeFocus();"  >
<html:form action="/hire/employNetPortal/search_zp_position"> 
			<%
			EmployPortalForm employPortalForm=(EmployPortalForm)session.getAttribute("employPortalForm");
			ArrayList fieldSetList=employPortalForm.getFieldSetList();			
			int index=Integer.parseInt((String)employPortalForm.getCurrentSetID());
			String a0100=employPortalForm.getA0100();
			String dbName=employPortalForm.getDbName();
			String writeable=employPortalForm.getWriteable();
			ArrayList applyedPositionList=employPortalForm.getApplyedPosList();
			String max_count=employPortalForm.getMax_count();
			String hireChannel=employPortalForm.getHireChannel();
			String hireMajor=employPortalForm.getHireMajor();
		    %>

<table width='94%'  border="0" cellpadding="0" cellspacing="0">
<tr>
<html:hidden name="employPortalForm" property="isDefinitionActive"/>
<a href="<%=url_p%>" style="display:none" id="hostname">for vpn</a>
	<td height='37' width='23' class="SearchLeftHead" >&nbsp; </td>
	
	<script language='javascript' >
		var awidth=Math.round(window.screen.width*0.85*0.9);	
		document.write("<td height='37' class='SearchBackColor' width="+awidth+" >");
	</script>
	
		<table  border="0" width='98%' cellpadding="0" cellspacing="0">  
		<tr>
			<td valign="middle" width='13%'class="cx">
			<font class='FontStyle'>&nbsp; <strong>岗位(专业)搜索：</strong></font>
			</td>
			<td valign='middle' width='77%' class="cx">
			<table width='100%' cellpadding="0" cellspacing="0"  border="0"  ><tr>
			<%
			String isQueryCondition=employPortalForm.getIsQueryCondition();
			ArrayList conditionFieldList=employPortalForm.getConditionFieldList();
			int j=0;
			for(int i=0;i<conditionFieldList.size();i++)
			{
				out.print("<td width='30%' align='center' nowrap");
				
				//if(i!=0)
					//out.print("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
				
				LazyDynaBean abean=(LazyDynaBean)conditionFieldList.get(i);
				String itemid=(String)abean.get("itemid");
				String itemtype=(String)abean.get("itemtype");
				String codesetid=(String)abean.get("codesetid");
				String isMore=(String)abean.get("isMore");
				String itemdesc=(String)abean.get("itemdesc");
				String value=(String)abean.get("value");
				value=PubFunc.getReplaceStr2(value);
				String viewvalue=(String)abean.get("viewvalue");
				viewvalue=PubFunc.getReplaceStr2(viewvalue);
				//if(itemdesc.length()>=5)
				 // j++;
				//if(j!=0&&j%2==0)
				  // out.print("<br>");
				out.print("<font class='FontStyle'>"+itemdesc+":&nbsp;</font>");
				if(itemtype.equals("A"))
				{
					if(codesetid.equals("0"))
					{
						out.println("<input  class='TEXT' type=\"text\" name=\"conditionFieldList["+i+"].value\"  value=\""+value+"\"   size='18'   />");
					}
					else
					{
						if(isMore.equals("0"))
						{
							ArrayList options=(ArrayList)abean.get("options");
							out.print("<select name='conditionFieldList["+i+"].value'  style='width:100;font-size:9pt;color:#666'   ><option value=''>全部</option> ");
							for(int n=0;n<options.size();n++)
							{
								LazyDynaBean a_bean=(LazyDynaBean)options.get(n);
								String avalue=(String)a_bean.get("value");
								String aname=(String)a_bean.get("name");
								out.println("<option value='"+avalue+"' ");
								if(avalue.equals(value))
									out.print(" selected ");
								out.print(" >"+aname+"</option>");
							}
							out.print("</select>");
						}
						else
						{
							out.println("<input type='hidden' name='conditionFieldList["+i+"].value' value='"+value+"'  />&nbsp;");  
			              	out.print("<input  class='TEXT' type='text' name='conditionFieldList["+i+"].viewvalue' value='"+viewvalue+"'  size='15'   readonly='true'   />");
			             	out.print("<img  src='/images/code.gif' onclick='javascript:openInputCodeDialog(\""+codesetid+"\",\"conditionFieldList["+i+"].viewvalue\");'/>");		
						}
					
					}
				
				}
				else if(itemtype.equals("D"))
				{
					out.println("<input  class='TEXT' type='text'  name='conditionFieldList["+i+"].value'  size='15' value='"+value+"'  onclick='popUpCalendar(this,this, dateFormat,\"\",\"\",true,false)'/>");
				
				}
				else if(itemtype.equals("N"))
				{
					out.println("<input class='TEXT' type=\"text\" name=\"conditionFieldList["+i+"].value\"   value=\""+value+"\"   size='15'   />");
				}
				out.print("</td>");
			
			
			
			}
			%>
             </tr></table>
			</td>
			<td align='right' height='37' width='10%' class="cx">
			 <input type="button" name="q" id="button" onclick='query();' value="查询" class="hj_zhaopin_list_tab_but"/>
			</td></tr></table>
	</td>
	<td height='37' width='23' class="SearchRightHead">&nbsp;</td>
	</tr>
</table>
<table width='94%' border='0' class="c_bgn">
<tr height='5'><td>&nbsp;</td></tr>
<tr><td width='15%'  valign='top'>
	
	<TABLE cellSpacing=0 cellPadding=0 width="150" 
                        align=center border=0 class="cb">
                          <TBODY>
                          
                          <TR>
                            <TD valign='top' align='center' >
                            
                            			<TABLE cellSpacing=0 cellPadding=0 width="100%" border=0 class='search_w_long'>
						              <TBODY>
						              <tr>
						              <td>
						              <table>
						              <tr>
						              	<td align='center'  class="welcomYouColor">
						              		<IMG height=20 hspace=5 src="/images/group_p.gif" width=20 >
						              		<font class='FontStyle'><bean:message key="hire.welcome.you"/> ${employPortalForm.userName}	</font>				    	
						              	</td>
						              	</tr>
						              	</table>
						              	</td>
						              </tr>
						              <tr>
						              	<td align='left' >         		
						              		<table>
						              			<tr><td class='blue12' ><bean:message key="hire.fill.resume"/></td></tr>
						              			<tr><td class='blue12' ><bean:message key="hire.mailing.inteserted"/></td></tr>
						              			<tr><td class='blue12' ><bean:message key="hire.position"/></td></tr>
						              		</table>
						              	</td>
						              </tr>
						              <TR >
						              	<td height=5 valign='top' class="NavigationMenuSeparator">
						              	  &nbsp;<IMG height=1  src="/images/l_8_T.gif" width=140 >
						              	</td>
						              </TR>
						              <TR>
						                <TD class=gary12 align=left width="32%" height=15 valign='bottom' >
						                  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						                  <a href='javascript:resumeBrowse("<%=dbName%>","<%=a0100%>")' />
						                  <IMG border=0 height=10  src="/images/forumme.gif" width=10 >&nbsp;&nbsp;<bean:message key="hire.browse.resume"/>
						                  </a>
						                </TD>
						                </TR>
						                     <logic:equal name="employPortalForm" property="canPrint" value="1">
						                       <TR >
						              	<td style='padding-top:0px'>
						              	 <IMG border= '0' height=1  src="/images/l_8_T.gif" width=140 >
						              	</td>
						              </TR>
						              <TR >
						                <TD align=left width="32%">
						                  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						                  <a href='javascript:ysmethod("<bean:write name="employPortalForm" property="previewTableId"/>");' />
						                  <IMG border=0 height=10  src="/images/forumme.gif" width=10 >&nbsp;&nbsp;打印简历
						                  </a>
						                </TD>
						                </TR>
		            	      	 		</logic:equal>
		            	      	 		<logic:notEqual value="#" name="employPortalForm" property="admissionCard">
		            	      	 		  <TR >
						              	<td style='padding-top:0px'>
						              	 <IMG border= '0' height=1  src="/images/l_8_T.gif" width=140 >
						              	</td>
						            	  </TR>
						            	  <TR >
						                <TD align=left width="32%">
						                  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						                  <a href='javascript:ysmethod("<bean:write name="employPortalForm" property="admissionCard"/>");' />
						                  <IMG border=0 height=10  src="/images/forumme.gif" width=10 >&nbsp;&nbsp;打印准考证
						                  </a>
						                </TD>
						                </TR>
		            	      	 		</logic:notEqual>
						              <TR>
						                <td height=5  valign='top' class="NavigationMenuSeparator" >
						              	  &nbsp;<IMG height=1  src="/images/l_8_T.gif" width=140 > 
						              	</td>  
						              </tr>
						              <TR>
						                <TD class=gary12 align=left width="32%" height=15>
						                	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						                	<a href='/hire/employNetPortal/search_zp_position.do?b_showResumeList=show&setID=0&opt=1'> 
						                	<IMG height=10 border=0 src="/images/forumme.gif" width=10 >&nbsp;&nbsp;<bean:message key="hire.my.resume"/></TD>
						              		</a>
						              <TR>
						                <td height=5 valign='top'  class="NavigationMenuSeparator">
						              	  &nbsp;<IMG height=1  src="/images/l_8_T.gif" width=140 >
						              	</td>  
						              </tr>
						              <TR>
						                <TD class=gary12 align=left width="32%" height=15>
						                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						                <a href="/hire/employNetPortal/search_zp_position.do?b_applyedPosition=query" >
						                <IMG height=10 border=0 src="/images/forumme.gif" width=10 >&nbsp;
						              	<font color='red'>  
						               <%if(hireChannel.equals("01")){ %>
						                 <bean:message key="hire.apply.position1"/>
						                <%}else{ %>
						               		<bean:message key="hire.apply.position"/>
						                <%} %>
							             </font>
						                </a>
						                </TD>
						              <TR>
						                <td height=5 valign='top'  class="NavigationMenuSeparator">
						              	  &nbsp;<IMG height=1  src="/images/l_8_T.gif" width=140 >
						              	</td>  
						              </tr>
						              <TR>
						                <TD class=gary12 align=left width="32%" height=15>
						                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						                <a href='/hire/employNetPortal/search_zp_position.do?br_editPassword=edit' >
						                <IMG border=0 height=10  src="/images/forumme.gif" width=10 >&nbsp;&nbsp;<bean:message key="label.banner.changepwd"/>
						                </a>
						                </TD>
						                </TR>
						                <logic:equal value="1" name="employPortalForm" property="isDefinitionActive">
						                    <TR>
						                <td height=5 class="NavigationMenuSeparator">
						              	  &nbsp;<IMG height=1  src="/images/l_8_T.gif" width=140 >
						              	</td>  
						              </tr>
						                 <TR>
						                <TD class=gary12 align=left width="32%" height=15>
						                	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						                	<a href='javascript:activeResume("<%=dbName%>","<%=a0100%>","${employPortalForm.activeValue}");'> 
						                	<IMG height=10 border=0 src="/images/forumme.gif" width=10 >&nbsp;&nbsp;<logic:equal value="1" name="employPortalForm" property="activeValue">关闭简历</logic:equal><logic:equal value="2" name="employPortalForm" property="activeValue">激活简历	</logic:equal>						                						                							                							                	
						                	</a>
						                	</TD>
						              		</TR>
						                </logic:equal>
						              <TR>
						                <td height=5  valign='top' class="NavigationMenuSeparator">
						              	  &nbsp;<IMG height=1  src="/images/l_8_T.gif" width=140 >
						              	</td>  
						              </tr>
						              <TR>
						                <TD class=gary12 align=left width="32%" height=15>
						                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						                <a href="javascript:exit()" >
						                <IMG height=10 border=0  src="/images/forumme.gif" width=10 >&nbsp;&nbsp;<bean:message key="hire.exit.login"/>
						                </a>
						                </TD>
						              <TR>
						                <td height=5  valign='top' class="NavigationMenuSeparator">
						              	  &nbsp;<IMG height=1  src="/images/l_8_T.gif" width=140 >
						              	</td>  
						              </tr>
						             
						             
						             
						             </TBODY>
						              </TABLE>
                            			
			       			</TD>
			              </TR>
                            </TBODY>
                          </TABLE>

<table>
<tr>
<td>
${employPortalForm.promptContent}
</td>
</tr>
</table>
</td>
<td width='85%' valign='top' align='center' >
<table cellSpacing=0 cellPadding=0 width='100%' ><tr>
<td width='10%' >&nbsp;</td>
		<td align='center' width='90%' valign='top' >	


	<table cellSpacing=0 cellPadding=0 width='100%' >
	  <tr>
              <td width="70%" class="zpaboutHJ_mainTD"><font class='FontStyle'>已申请岗位</font></td>
              </tr>
       <tr height='3'>
              <td>&nbsp;</td>
              </tr>
	<tr>
		<td align='center' width='100%' valign='top' >			
                    <TABLE id=rptb cellSpacing=0 
                        cellPadding=0 width='100%' align=center  border=0 class="hj_zhaopin_list_tab_title">
                          <TBODY>                    
                      <tr  align='center' > 
                        <th height='25' nowrap>
                       <logic:equal value="1" name="employPortalForm" property="hasXiaoYuan">
                       <bean:message key="e01a1.label"/>|<bean:message key="hire.employActualize.interviewProfessional"/>
                       </logic:equal>
                       <logic:equal value="0" name="employPortalForm" property="hasXiaoYuan">
                      		<bean:message key="e01a1.label"/>        
                      		</logic:equal>	
                      		 <logic:equal value="2" name="employPortalForm" property="hasXiaoYuan">
                       <bean:message key="hire.employActualize.interviewProfessional"/>
                       </logic:equal>
                      	</th>
                      	<th  nowrap>
                      		<bean:message key="hire.employActualize.resumeState"/>       			
                      	</th>
                     	<th nowrap>
                      		<bean:message key="lable.zp_plan.start_date"/>         			
                      	</th>
                      	<!-- 
                      	<td class=rptHead nowrap
                            background=/images/r_titbg01.gif >
                      		<font color='#DF0024'>结束日期</font>          			
                      	</td>
                      	-->
                      	<th nowrap>
                      		<bean:message key="lable.zp_plan_detail.domain"/>              			
                      	</th>
                      	<th nowrap>
                      		<bean:message key="lable.zp_plan_detail.amount"/>          			
                      	</th>
                      	<logic:notEqual value="1" name="employPortalForm" property="max_count">
                      	<th nowrap>
                      		<bean:message key="hire.wish.order"/>              			
                      	</th>
                      	</logic:notEqual>
                      	<logic:equal value="0" name="employPortalForm" property="writeable">
                      	<th nowrap>
                      		<bean:message key="system.infor.oper"/>           			
                      	</th>
                      	</logic:equal>
                      </tr>
                      
                      <%
                      for(int i=0;i<applyedPositionList.size();i++)
                      {
                      	LazyDynaBean abean=(LazyDynaBean)applyedPositionList.get(i);
                      	String zp_pos_id=(String)abean.get("zp_pos_id");
                      	out.println("<tr>");
                        out.println("<td align='left' height='25' >"+(String)abean.get("posName")+"&nbsp;"+"</td>");
                      	out.println("<td align='left' >"+(String)abean.get("unitName")+"&nbsp;"+"</td>");
                      	out.println("<td   nowrap >"+(String)abean.get("z0329")+"&nbsp;"+"</td>");
                      //	out.println("<td class=rptItemMain nowrap >"+(String)abean.get("z0331")+"</td>");
                      	out.println("<td align='left'  >"+(String)abean.get("z0333")+"&nbsp;"+"</td>");
                      	out.println("<td align='right'   >"+(String)abean.get("z0315")+"&nbsp;"+"</td>");
                      	if(!max_count.equals("1"))
                      	{
                         	out.println("<td align='center'  >");
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
                      	if(writeable.equals("0"))
                          	out.println("<td align='center'  ><a href='javascript:del(\""+zp_pos_id+"\",\""+a0100+"\",\""+dbName+"\")' >"+ResourceFactory.getProperty("lable.tz_template.delete")+"</a>&nbsp;&nbsp;</td>");
                      	out.print("</tr>");
                      }
                      %>
                      	
                     </TBODY>
                   </TABLE>
                  
					
					
				  
		</td>
		</tr>
		<TR><TD>&nbsp;</TD></TR>
		 <%
                   if(applyedPositionList.size()>0&&!max_count.equals("1"))
                   		out.println("<TR><TD STYLE='TEXT-ALIGN:CENTER'> <input type='button' style='font-family:verdana;border:darkgray 1px solid;font-size:9pt;cursor:hand;height:22px;background-color:#f2f2f2' onclick='order(\""+a0100+"\")' value='"+ResourceFactory.getProperty("hire.save.order")+"' /></TD></TR> ");
                   %>
	</table>
	</td>
	</tr>
	</table>
</td></tr></table>

</html:form>

</body>
</html>
<script language="javascript" src="/general/sys/hjaxmanage.js"></script>
<script language="javascript">AxManager.writeCard();</script>
 <script language="javascript"> 
         initCard();
</script> 