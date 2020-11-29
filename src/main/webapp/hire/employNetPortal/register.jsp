<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<script language="JavaScript" src="/hire/employNetPortal/employNetPortal.js"></script>
<%@ page import="com.hjsj.hrms.actionform.hire.employNetPortal.EmployPortalForm,
			     org.apache.commons.beanutils.LazyDynaBean,
			     com.hrms.hjsj.sys.DataDictionary,
			     com.hrms.hjsj.sys.FieldItem,com.hrms.frame.codec.SafeCode,com.hjsj.hrms.utils.PubFunc,
			     java.util.*"%>
<html>
<head>			     
<LINK 
href="/css/employNetStyle.css" type=text/css rel=stylesheet>
<LINK href="/css/main.css" type=text/css rel=stylesheet>
<LINK href="/css/nav.css" type=text/css rel=stylesheet>
<%
    EmployPortalForm employPortalForm=(EmployPortalForm)session.getAttribute("employPortalForm");
	FieldItem item=DataDictionary.getFieldItem("a0101");
	int itemLength=20;
	if(item!=null)
	   itemLength=item.getItemlength();
	String isPrompt=employPortalForm.getIsPrompt();
	String person_type=request.getParameter("person_type");
	if(person_type==null)
	    person_type="0";
	String isAll=PubFunc.getReplaceStr2(request.getParameter("isAll"));
	String aurl = (String)request.getServerName();
	 String port=request.getServerPort()+"";
	 String prl=request.getScheme();
	 String url_p=prl+"://"+aurl+":"+port;
%>
<script type="text/javascript">
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
		<%if(request.getParameter("isAll")!=null){%>
		    document.employPortalForm.action="/hire/employNetPortal/search_zp_position.do?b_query=link&isAll=<%=isAll%>";
		    document.employPortalForm.submit();
		<%}else{%>
	    	document.employPortalForm.action="/hire/employNetPortal/search_zp_position.do?b_query=link";
		    document.employPortalForm.submit();
		<%}%>
	}	

</script>
</head>
<body onKeyDown='pf_ChangeFocusRegister("${employPortalForm.isDefinitionActive}","${employPortalForm.cultureCodeItem}","${employPortalForm.isDefinitionCulture}","${employPortalForm.paramFlag}","${employPortalForm.blackField}","${employPortalForm.blackFieldDesc}","${employPortalForm.onlyNameDesc}","${employPortalForm.isDefineWorkExperience}","${employPortalForm.workExperienceDesc}","<%=person_type%>","<%=itemLength%>");'  >
<html:form action="/hire/employNetPortal/search_zp_position"> 
			<%
			
			String isQueryCondition=employPortalForm.getIsQueryCondition();
			String a0100=employPortalForm.getA0100();
			String dbName=employPortalForm.getDbName();
		    %>


<table width='94%'  border="0" cellpadding="0" cellspacing="0">

<tr>

<html:hidden name="employPortalForm" property="isDefinitionActive"/>
	<td height='37' width='23' class="SearchLeftHead" >&nbsp; </td>
	<input type="hidden" name="url_addr40" value="<%=url_p%>"/>
	
	<script language='javascript' >
		var awidth=Math.round(window.screen.width*0.85*0.9);	
		document.write("<td height='37' class='SearchBackColor' width="+awidth+" >");
	</script>
	
		<table  border="0" width='100%' cellpadding="0" cellspacing="0">  
		<tr>
			<td valign="middle" class="cx">
		   <font class='FontStyle'> <strong>职位搜索：</strong></font>
			</td>
			<td valign='middle' width='77%' class="cx">
			<table width='100%' cellpadding="0" cellspacing="0"  border="0"  ><tr>
			<%
			ArrayList conditionFieldList=employPortalForm.getConditionFieldList();
			int j=0;
			for(int i=0;i<conditionFieldList.size();i++)
			{
				out.print("<td width='30%' align='center' nowrap");
				LazyDynaBean abean=(LazyDynaBean)conditionFieldList.get(i);
				String itemid=(String)abean.get("itemid");
				String itemtype=(String)abean.get("itemtype");
				String codesetid=(String)abean.get("codesetid");
				String isMore=(String)abean.get("isMore");
				String itemdesc=(String)abean.get("itemdesc");
				String value=(String)abean.get("value");
				String viewvalue=(String)abean.get("viewvalue");
				value=PubFunc.getReplaceStr2(value);
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
                            		<TABLE cellSpacing=0 cellPadding=0 width="100%" border=0 class='search_w'>
						              <TBODY>
						              <td colspan='2' class="NavigationTopColor">
						              	<IMG hspace=5 src="/images/fp1.jpg" style="margin:9px 0px 15px 8px;"> 
						              </td>
						              <TR>
						                <TD  style="TEXT-ALIGN:right" width="32%" height=26><font class='FontStyle'><bean:message key="hire.email.address"/>：</font></TD>
						                <TD style="TEXT-ALIGN:left" width="68%"><INPUT class=s_input id=loginName 
						                  name=loginName></TD></TR>
						              <TR>
						                <TD style="TEXT-ALIGN:right" height=26><font class='FontStyle'><bean:message key="label.mail.password"/>：</font></TD>
						                <TD style="TEXT-ALIGN:left"><INPUT class=s_input id=password type=password 
						                  name=password></TD></TR>
						              <TR>
						               
						                  
						                <TD style="TEXT-ALIGN:center" colSpan=2 height=35>
						                 <input type="button" name="dl" value="登录" class="s_btn" onclick='login();' />
						                     <logic:equal value="0" name="employPortalForm" property="isDefinitinn">
						                  <input type="button" name="zc" value="注册" class="s_btn" onclick="T_BUTTOM();"/>
						                  </logic:equal>
						                    <logic:equal value="1" name="employPortalForm" property="isDefinitinn">
						                  <input type="button" name="zc" value="注册" class="s_btn" onclick="TR_BUTTON();"/>
						                  </logic:equal>
						                  </TD></TR>
						                  <TR>
						                <TD  style="TEXT-ALIGN:center" height=26 colspan="2">
						                <input type="checkbox" name="remenber" value="1" id="remenberme"/><font class='FontStyle'>记住我</font>&nbsp;&nbsp;&nbsp;
						                <a href='javascript:getPasswordZP("<%=dbName%>","username","userpassword");'>忘记密码？</a></TD>
						                </TR>
						                <tr height='3'><td>&nbsp;</td>
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
<td width='85%' valign='top' >

<table cellSpacing=0 cellPadding=0 width='100%' ><tr>
<td width='10%' >&nbsp;</td>
		<td align='center' width='90%' valign='top' >	
				 
			<TABLE  cellSpacing=0 cellPadding=0  width="100%">	
			 <tr>
              <td class="zpaboutHJ_mainTD"><font class='FontStyle'>注册用户</font></td>
              </tr>
               <tr height='3'>
              <td>&nbsp;</td>
              </tr>
             <TR>
             <TD align='middle' width='100%'>	
				<TABLE cellSpacing=2 cellPadding=2 width="100%"  align=center border=0 class='border01'>
                          <TBODY>
                          
                          <TR>
                            <TD class="RecordRow_net"><bean:message key="hire.register.explanation"/> </TD>
                          </TR>
                          </TBODY></TABLE><BR>
                        <TABLE class=border01 cellSpacing=1 cellPadding=2  border=0 width='100%'>
                          <TBODY>
                          <TR>
                            <TD class='tdTitle' 
                              width="30%"><bean:message key="hire.register.email"/>： </TD>
                            <TD class=tdValue><INPUT class=textbox id=txtEmail name=txtEmail >&nbsp;<FONT 
                              color=red>* <span id='t1'></span>  </FONT> 
                            </TD></TR>
                          <TR>
                            <TD class=tdTitle><bean:message key="label.login.password"/>：</TD>
                            <TD class=tdValue><INPUT class=textbox id=pwd1 
                               type=password 
                              name=pwd1 >&nbsp;<FONT color=red>* <span id='t2'></span></FONT>
                            </TD></TR>
                          <TR>
                            <TD class=tdTitle><bean:message key="hire.zp_persondb.okpassword"/>：</TD>
                            <TD class=tdValue><INPUT class=textbox 
                              id=pwd2  type=password 
                              name=pwd2>&nbsp;<FONT color=red>*</FONT> 
                             
                            </TD></TR>
                          <TR>
                            <TD class=tdTitle><bean:message key="hire.user.name"/>：</TD>
                            <TD class=tdValue><INPUT class=textbox id=txtName 
                               name=txtName><FONT 
                              color=#ff0000>&nbsp;* <span id='t3'></span>
                              </FONT></TD></TR>
                             <logic:notEqual value="3" name="employPortalForm" property="paramFlag">
                              <logic:notEqual value="-1" name="employPortalForm" property="blackField">
                              
                              <TR>
                                <TD class=tdTitle><bean:write name="employPortalForm" property="blackFieldDesc"/>：</TD>
                                <TD class=tdValue><INPUT class=textbox id=blackValue name=blackFieldValue maxLength="${employPortalForm.blackFieldSize}"><FONT color=#ff0000>&nbsp;* <span id='t5'></span> </FONT></TD>
                              </TR>
                              </logic:notEqual>
                              </logic:notEqual>
                              
                              <html:hidden property="paramFlag" name="employPortalForm"/>
                              <logic:notEqual value="1" name="employPortalForm" property="paramFlag">
                               <TR>
                                <TD class=tdTitle><bean:write name="employPortalForm" property="onlyNameDesc"/>：</TD>
                                <TD class=tdValue><INPUT class=textbox id=onlyv name=onlyValue maxLength="${employPortalForm.onlySize}"><FONT color=#ff0000>&nbsp;* <span id='t7'></span> </FONT></TD>
                              </TR>
                              </logic:notEqual>
                              <html:hidden name="employPortalForm" property="isDefinitionActive"/>
                              <logic:equal value="1" name="employPortalForm" property="isDefinitionActive">
                              <TR>
                            <TD class=tdTitle>现工作单位：</TD>
                            <TD class=tdValue>
                             <INPUT type="text" class=textbox name=belongUnithName readOnly>
                      <img src="/images/code.gif" border="0"  onclick='prompt_content("<%=isPrompt%>");'/>
                             
                            <INPUT type="hidden" class=textbox id=belongUnit 
                               name=belongUnit value=""><FONT color=#ff0000>&nbsp;* <span id='t4'></span>
                              </FONT></TD></TR>
                              </logic:equal>
                              <logic:equal value="1" name="employPortalForm" property="isDefinitionCulture">
                               <TR>
                            <TD class=tdTitle><bean:message key="hire.culture.type"/>：</TD>
                            <TD class=tdValue>
                             <hrms:optioncollection name="employPortalForm" property="cultureList" collection="list" />
						 <html:select styleClass="textbox" name="employPortalForm" property="hiddenCode" size="1" >
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>
                             
                 
                               <FONT color=#ff0000>&nbsp;* 
                              </FONT></TD></TR>
                              </logic:equal>
                              <html:hidden name="employPortalForm" property="isDefineWorkExperience"/>
                               <logic:equal value="1" name="employPortalForm" property="isDefineWorkExperience">
                               <TR>
                            <TD class=tdTitle>${employPortalForm.workExperienceDesc}：</TD>
                            <TD class=tdValue>
                           <logic:iterate id="element" property="workExperienceCodeList" name="employPortalForm" indexId="index">
                           <input type="radio" name="workExperience" value="<bean:write name="element" property="codeitemid"/>"/><bean:write name="element" property="codeitemdesc"/>
                           
                           </logic:iterate>(应届毕业生请选此项)
                             
                             
                               <FONT color=#ff0000>&nbsp;* <span id='t6'></span>
                              </FONT></TD></TR>
                              </logic:equal>
                              
                              
                              
                              
                              
                              
                              
                              
                              
                              
                               <TR>
                            <TD class=tdTitle>
                           <input type="checkbox" name="remenber" value="1" id="remenberme"/><font class='FontStyle'>记住我</font>&nbsp;&nbsp;<a href='javascript:getPasswordZP("<%=dbName%>","username","userpassword");'>忘记密码？</a></TD><td class=tdTitle>&nbsp;</td>
                            </TR>
                           
                              </TBODY></TABLE>
                        <TABLE id=Table2 cellSpacing=0 cellPadding=0   width="80%" border=0>
                          <TBODY>
                          <TR>
                            <TD style="TEXT-ALIGN:center">
                            <IMG  onclick='subRegister("${employPortalForm.isDefinitionActive}","${employPortalForm.cultureCodeItem}","${employPortalForm.isDefinitionCulture}","${employPortalForm.paramFlag}","${employPortalForm.blackField}","${employPortalForm.blackFieldDesc}","${employPortalForm.onlyNameDesc}","${employPortalForm.isDefineWorkExperience}","${employPortalForm.workExperienceDesc}","<%=person_type%>","<%=itemLength%>");' src="/images/tj.gif" border=0 style="cursor:hand"/>
                        
                            </TD>
                            </TR></TBODY></TABLE>
				</td></tr>
				</TABLE>
				
				
				
				
				
				
				
		</td></tr></table>


</td></tr></table>


<Br>

</html:form>

</body>
</html>