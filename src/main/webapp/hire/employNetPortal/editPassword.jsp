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

<html>
<head>	
<LINK href="/css/employNetStyle.css" type=text/css rel=stylesheet>
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
 <script language="JavaScript" src="/hire/employNetPortal/employNetPortal.js"></script>
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
	<%
			EmployPortalForm employPortalForm=(EmployPortalForm)session.getAttribute("employPortalForm");
			String isQueryCondition=employPortalForm.getIsQueryCondition();
			String a0100=employPortalForm.getA0100();
			String dbName=employPortalForm.getDbName();
			String hireChannel=employPortalForm.getHireChannel();
		    %>
<body onKeyDown='return pf_ChangeFocusTWO("<%=dbName%>","<%=a0100%>");'  >
<html:form action="/hire/employNetPortal/search_zp_position"> 
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
			ArrayList conditionFieldList=employPortalForm.getConditionFieldList();
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
                        align=center border=0>
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
						              	<td height=1 class="NavigationMenuSeparator">
						              	  &nbsp;<IMG height=1 src="/images/l_8_T.gif" width=140 > 
						              	</td>
						              </TR>
						              <TR>
						                <TD class=gary12 align=left width="32%" height=15>
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
						                <td height=1 class="NavigationMenuSeparator">
						              	  &nbsp;<IMG height=1  src="/images/l_8_T.gif" width=140 > 
						              	</td>  
						              </tr>
						              <TR>
						                <TD class=gary12 align=left width="32%" height=15>
						                	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						                	<a href='/hire/employNetPortal/search_zp_position.do?b_showResumeList=show&setID=0&opt=1'> 
						                	<IMG height=10 border=0 src="/images/forumme.gif" width=10 >&nbsp;&nbsp;<bean:message key="hire.my.resume"/>
						                	</a>
						                	</TD>
						              		</TR>
						              		    
						              <TR>
						                <td height=1 class="NavigationMenuSeparator">
						              	  &nbsp;<IMG height=1  src="/images/l_8_T.gif" width=140 > 
						              	</td>  
						              </tr>
						              <TR>
						                <TD class=gary12 align=left width="32%" height=15>
						                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						                <a href="/hire/employNetPortal/search_zp_position.do?b_applyedPosition=query" >
						                <IMG height=10 border=0  src="/images/forumme.gif" width=10 >&nbsp;
						                  <%if(hireChannel.equals("01")){ %>
						                 <bean:message key="hire.apply.position1"/>
						                <%}else{ %>
						               		<bean:message key="hire.apply.position"/>
						                <%} %>
						                </a>
						                </TD>
						              <TR>
						                <td height=1 class="NavigationMenuSeparator">
						              	  &nbsp;<IMG height=1  src="/images/l_8_T.gif" width=140 > 
						              	</td>  
						              </tr>
						              <TR>
						                <TD class=gary12 align=left width="32%" height=15>
						                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						                <a href='/hire/employNetPortal/search_zp_position.do?br_editPassword=edit' >
						                <IMG height=10 border=0 src="/images/forumme.gif" width=10 >&nbsp;&nbsp;
						                  <font color='red'><bean:message key="label.banner.changepwd"/></font>
						                </a>
						                </TD>
						                </TR>
						                  <logic:equal value="1" name="employPortalForm" property="isDefinitionActive">
						                    <TR>
						                <td height=1 class="NavigationMenuSeparator">
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
						                <td height=1 class="NavigationMenuSeparator">
						              	  &nbsp;<IMG height=1  src="/images/l_8_T.gif" width=140 > 
						              	</td>  
						              </tr>
						              <TR>
						                <TD class=gary12 align=left width="32%" height=15>
						                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						                <a href="javascript:exit()" >
						                <IMG height=10 border=0 src="/images/forumme.gif" width=10 >&nbsp;&nbsp;<bean:message key="hire.exit.login"/></a>
						                </TD>
						              	
						              <TR>
						                <td height=1 class="NavigationMenuSeparator">
						              	 &nbsp; <IMG height=1  src="/images/l_8_T.gif" width=140 > 
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
<td width='85%' valign='top' >

<table cellSpacing=0 cellPadding=0 width='100%' ><tr>
        <td width='10%' >&nbsp;</td>
		<td align='center' width='90%' valign='top' >	
				 
			<TABLE  cellSpacing=0 cellPadding=0  width="100%">	
			<tr>
              <td class="zpaboutHJ_mainTD"><font class='FontStyle'>修改密码</font></td>
              </tr>
              <tr height='3'>
              <td>&nbsp;</td>
              </tr>
             <TR>
             <TD align=middle>	
				<TABLE cellSpacing=2 cellPadding=2 width="100%" align=center border=0 class='border01'>
                          <TBODY>
                         
                            <TD class="RecordRow_net"><font class='FontStyle'><bean:message key="hire.password.length"/></font><SPAN 
                              class=cf00><bean:message key="hire.six.eight"/></SPAN><font class='FontStyle'><bean:message key="hire.locate"/></font></TD></TR>
                          </TBODY></TABLE><BR>
                        <TABLE class=border01 cellSpacing=1 cellPadding=2 
                        width=100% border=0>
                          <TBODY>
                          <TR>
                            <TD class=tdTitle width="20%"><font class="fieldDescriptionColor"><bean:message key="hire.old.password"/>：</font></TD>
                            <TD class=tdValue><INPUT class=textbox id=pwd0 maxlength=8
                               type=password 
                              name=pwd0 >&nbsp;<FONT 
                              color=red>* <span id='t1'></span>  </FONT> 
                            </TD></TR>
                          <TR>
                            <TD class=tdTitle><font class="fieldDescriptionColor"><bean:message key="hire.new.password"/>：</font></TD>
                            <TD class=tdValue><INPUT class=textbox id=pwd1 maxlength=8
                               type=password 
                              name=pwd1 >&nbsp;<FONT color=red>* <span id='t2'></span></FONT>
                            </TD></TR>
                          <TR>
                            <TD class=tdTitle><font class="fieldDescriptionColor"><bean:message key="hire.repeat.password"/>：</font></TD>
                            <TD class=tdValue><INPUT class=textbox maxlength=8
                              id=pwd2  type=password 
                              name=pwd2>&nbsp;<FONT color=red>*</FONT> 
                             
                            </TD></TR>
                        </TBODY></TABLE>
                        <TABLE id=Table2 cellSpacing=0 cellPadding=0 
                        width="60%" border=0>
                          <TBODY>
                          <TR>
                            <TD style="text-align:center">
                            <a href='javascript:subEDIT("<%=dbName%>","<%=a0100%>")' >
                            <IMG  src="/images/tj.gif" border=0 >
                            </a>
                            </TD>
                            </TR></TBODY></TABLE>
				</td></tr>
				</TABLE>
				
				
				
				
				
				
				
		</td></tr></table>


</td></tr></table>


<Br>

</html:form>

</body>
<script language="javascript" src="/general/sys/hjaxmanage.js"></script>
<script language="javascript">AxManager.writeCard();</script>
 <script language="javascript"> 
         initCard();
</script> 
</html>