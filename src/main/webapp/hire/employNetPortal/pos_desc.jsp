<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/hire/employNetPortal/employNetPortal.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<%@ page import="com.hjsj.hrms.actionform.hire.employNetPortal.EmployPortalForm,
			     org.apache.commons.beanutils.LazyDynaBean,com.hrms.hjsj.utils.Sql_switcher,com.hrms.hjsj.sys.Constant,
			     java.util.*,com.hrms.frame.codec.SafeCode,com.hjsj.hrms.utils.PubFunc"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>

<html:form action="/hire/employNetPortal/search_zp_position"> 
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
								alert("<bean:write  name="element" property="itemdesc"/>"+OVER_LENGTH_SCOPE+"!");
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
<table width='94%'  border="0" cellpadding="0" cellspacing="0"   >
<a href="<%=url_p%>" style="display:none" id="hostname">for vpn</a>
<tr>
	<td height='37' width='23' class="SearchLeftHead">&nbsp; </td>
	<script language='javascript' >
		var awidth=Math.round(window.screen.width*0.85*0.9);	
		document.write("<td height='37' class='SearchBackColor' width="+awidth+" >");
	</script>
		<table  border="0" width='100%' cellpadding="0" cellspacing="0"    >  
		<tr>
			<td valign="bottom" class="cx" width='13%' >
			<font class='FontStyle'> &nbsp;<strong>岗位(专业)搜索：</strong></font>
			</td>
			<td valign='top' width='77%' class="cx">
			
			<table width='100%' cellpadding="0" cellspacing="0"  border="0"  ><tr>
			<%
			
			EmployPortalForm employPortalForm=(EmployPortalForm)session.getAttribute("employPortalForm");
			String PName=employPortalForm.getPosDesc();
			String isQueryCondition=employPortalForm.getIsQueryCondition();
			String a0100=employPortalForm.getA0100();
			ArrayList posDescFiledList=employPortalForm.getPosDescFiledList();
			String max_count=employPortalForm.getMax_count();
			String dbName=employPortalForm.getDbName();
			String hireChannel=employPortalForm.getHireChannel();
			String hireMajor=employPortalForm.getHireMajor();
			ArrayList conditionFieldList=employPortalForm.getConditionFieldList();
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
				out.print("<font class='FontStyle'>"+itemdesc+":&nbsp;</font>");
				if(itemtype.equals("A"))
				{
					if(codesetid.equals("0"))
					{
						out.println("<input class='TEXT' type=\"text\" name=\"conditionFieldList["+i+"].value\"   value=\""+value+"\"   size='12'   />");
					}
					else
					{
						if(isMore.equals("0"))
						{
							ArrayList options=(ArrayList)abean.get("options");
							out.print("<select name='conditionFieldList["+i+"].value'  style='width:100;font-size:9pt;color:#666'  ><option value=''>全部</option> ");
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
							out.println("<input type='hidden' name='conditionFieldList["+i+"].value'   />&nbsp;");  
			              	out.print("<input class='TEXT' type='text' name='conditionFieldList["+i+"].viewvalue'  size='12'  readonly='true'   />");
			             	out.print("<img  src='/images/code.gif' onclick='javascript:openInputCodeDialog(\""+codesetid+"\",\"conditionFieldList["+i+"].viewvalue\");'/>");		
						}
					
					}
				
				}
				else if(itemtype.equals("D"))
				{
					out.println("<input class='TEXT' type='text'  name='conditionFieldList["+i+"].value'  size='12'   onclick='popUpCalendar(this,this, dateFormat,\"\",\"\",true,false)'   />");
				
				}
				else if(itemtype.equals("N"))
				{
					out.println("<input class='TEXT' type=\"text\" name=\"conditionFieldList["+i+"].value\"   value=\"\"   size='12'   />");
				}
				
				out.print("</td>");
			
			
			}
			%>
				</tr></table>
			</td>
			<td  width='10%' valign='bottom' align='right' class='cx'>
			 <input type="button" name="q" id="button" onclick='query();' value="查询" class="hj_zhaopin_list_tab_but"/>
			</td></tr></table>
			</td>
			
			<td height='37' width='23' class="SearchRightHead">&nbsp;</td>
</tr>
</table>
<table width='94%' class="c_bgn">
<tr height='5'><td>&nbsp;</td></tr>
<tr>
<td width='15%'  valign='top' align="center">
	
	<%
          if(a0100==null||(a0100!=null&&a0100.trim().length()==0)){
                        	
      %>
	<TABLE cellSpacing=0 cellPadding=0 width="150" align=center border=0 class="cb">
                          <TBODY>
                          
                          <TR>
                            <TD  valign='top' align='center' >
                            
                            	
                            		<TABLE cellSpacing=0 cellPadding=0 width="100%" border=0 class='search_w'>
						              <TBODY>
						              <TR> 
						              <td colspan='2'>
						              	<IMG  hspace=5 src="/images/fp1.jpg" style="margin:9px 0px 15px 8px;"> 
						              </td>
						              </TR>
						              <TR>
						                <TD style="TEXT-ALIGN:right" width="32%" height=26><font class='FontStyle'><bean:message key="hire.email.address"/>：</font></TD>
						                <TD style="TEXT-ALIGN:left" width="68%"><INPUT class=s_input id=loginName  name=loginName></TD></TR>
						              <TR>
						                <TD style="TEXT-ALIGN:right" width="32%" height=26><font class='FontStyle'><bean:message key="label.mail.password"/>：</font></TD>
						                <TD style="TEXT-ALIGN:left" width="68%"><INPUT class=s_input id=password type=password 				                 
						             name=password></TD></TR>
						                  
						                 
						                  
						              <TR>
						                <TD style="TEXT-ALIGN:center" colspan="2" height=35>
						                <input type="button" name="dl" value="登录" class="s_btn" onclick='login();' />
                                        
						                  
						                  <logic:equal value="0" name="employPortalForm" property="isDefinitinn">
						                  <input type="button" name="zc" value="注册" class="s_btn" onclick="T_BUTTOM();"/>
						                  </logic:equal>
						                    <logic:equal value="1" name="employPortalForm" property="isDefinitinn">
						                  <input type="button" name="zc" value="注册" class="s_btn" onclick="TR_BUTTON();"/>
						                  </logic:equal>
						                  </TD></TR>
						                   <TR>
						                <TD style="TEXT-ALIGN:center" height=26 colspan="2">
						                <input type="checkbox" name="remenber" value="1" id="remenberme"/><font class='FontStyle'>记住我</font>&nbsp;&nbsp;&nbsp;
						                <a href='javascript:getPasswordZP("<%=dbName%>","username","userpassword");'>忘记密码？</a></TD>
						                </TR>
						                  
						                  
						                  </TBODY>
						                </TABLE>
						                </TD>
						                </TR>
						                </TBODY>
						                </TABLE>
                            		<% } else { %>
                            			<TABLE cellSpacing=0 cellPadding=0 width="150" align=center border=0 class="cb">
                                    <TBODY>
                          
                                   <TR>
                                   <TD valign='top' align='center' >
                            			
                            			
                            			<TABLE cellSpacing="0" cellPadding="0" width="100%" border="0" class='search_w_long'>
						              <TBODY>
						              <tr>
						              <td>
						              <table>
						              <tr>
						              	<td align='center' class="welcomYouColor">
						              		<IMG height=20 hspace=7 src="/images/group_p.gif" width=20 >
						              		<bean:message key="hire.welcome.you"/> ${employPortalForm.userName}					    	
						              	</td>
						              	</tr>
						              	</table>
						              	</td>
						              </tr>
						              <tr>
						              	<td align='center' >         		
						              		<table>
						              			<tr><td class='blue12' ><bean:message key="hire.fill.resume"/></td></tr>
						              			<tr><td class='blue12' ><bean:message key="hire.mailing.inteserted"/></td></tr>
						              			<tr><td class='blue12' ><bean:message key="hire.position"/></td></tr>
						              		</table>
						              	</td>
						              </tr>
						              <TR >
						              	<td height=5 class="NavigationMenuSeparator">
						              	  &nbsp;<IMG height=1  src="/images/l_8_T.gif" width=140 >
						              	</td>
						              </TR>
						              <TR >
						                <TD class=gary12 align=left width="32%">
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
						                <td height=5 class="NavigationMenuSeparator">
						              	  &nbsp;<IMG height=1  src="/images/l_8_T.gif" width=140 >
						              	</td>  
						              </tr>
						              <TR>
						                <TD class=gary12 align=left width="32%">
						                	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						                	<a href='/hire/employNetPortal/search_zp_position.do?b_showResumeList=show&setID=0&opt=1'> 
						                	<IMG height=10 border=0 src="/images/forumme.gif" width=10 >&nbsp;&nbsp;<bean:message key="hire.my.resume"/>
						                	</a>
						                	</TD>
						              		</TR>
						              <TR>
						                <td height=5 class="NavigationMenuSeparator">
						              	  &nbsp;<IMG height=1  src="/images/l_8_T.gif" width=140 >
						              	</td>  
						              </tr>
						              <TR>
						                <TD class=gary12 align=left width="32%">
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
						                </TR>
						              <TR>
						                <td height=5 class="NavigationMenuSeparator">
						              	  &nbsp;<IMG height=1  src="/images/l_8_T.gif" width=140 > 
						              	</td>  
						              </tr>
						              <TR>
						                <TD class=gary12 align=left width="32%">
						                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						                <a href='/hire/employNetPortal/search_zp_position.do?br_editPassword=edit' >
						                <IMG height=10 border=0 src="/images/forumme.gif" width=10 >&nbsp;&nbsp;<bean:message key="label.banner.changepwd"/>
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
						                <TD class=gary12 align=left width="32%">
						                	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						                	<a href='javascript:activeResume("<%=dbName%>","<%=a0100%>","${employPortalForm.activeValue}");'> 
						                	<IMG height=10 border=0 src="/images/forumme.gif" width=10 >&nbsp;&nbsp;<logic:equal value="1" name="employPortalForm" property="activeValue">关闭简历</logic:equal><logic:equal value="2" name="employPortalForm" property="activeValue">激活简历	</logic:equal>						                						                							                							                	
						                	</a>
						                	</TD>
						              		</TR>
						                </logic:equal>
						                
						                
						                
						              <TR>
						                <td height=5 class="NavigationMenuSeparator">
						              	  &nbsp;<IMG height=1  src="/images/l_8_T.gif" width=140 > 
						              	</td>  
						              </tr>
						              <TR>
						                <TD class=gary12 align=left width="32%">
						                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						                <a href="javascript:exit()" >
						                <IMG height=10 border=0 src="/images/forumme.gif" width=10 >&nbsp;&nbsp;<bean:message key="hire.exit.login"/></a>
						                </TD>
						                </TR>
						                 <TR>
						                <td height=5 class="NavigationMenuSeparator">
						              	  &nbsp;<IMG height=1  src="/images/l_8_T.gif" width=140 > 
						              	</td>  
						              </tr>
						              <TR>
						             </TBODY>
						              </TABLE>     
						              </TD>
			              </TR>
                            </TBODY>
                          </TABLE>               
                            		<% } %>
  		 
			       			

<table>
<tr>
<td>
${employPortalForm.promptContent}
</td>
</tr>
</table>
</td>
<td align='center' width='85%' >
<table width='100%'   ><tr>
		<td align='left' width='10%' >&nbsp;</td>
		<td width='90%'>	
		          <table width="100%" border=0 cellpadding=0 cellspacing=0 >
             
<tr>
              <td class="zpaboutHJ_mainTD"><font class='FontStyle'>岗位信息</font></td>
              </tr>
<tr>
              <td ><div class="zphr1">
                <hr class="viewhr" />
              </div></td>
            </tr>
                      <tr > 
                        <td width="100%"  align='center' >
                       	<table class="hj_zwxx" height='25' width='100%' ><tr><th  align="left" > 
                        <%=PName%><logic:equal value="1" name="employPortalForm" property="isConfigExp"><logic:equal value="1" name="employPortalForm" property="isHaveExp">
                       <a href='/servlet/performance/fileDownLoad?e01a1=${employPortalForm.positionID}&opt=hire'  target="_blank"  border='0' >(查看岗位说明书)</a>
                        </logic:equal></logic:equal>
                        </th></tr></table>
                        </td>
                       </tr>
						
                     <tr > 
                        <td align="center">   
                          		<table width='100%' border=0 class="hj_zwxx" cellpadding=0 cellspacing=0 >
                          			<%
                          			for(int i=0;i<posDescFiledList.size();i++)
									{                          			
                          				LazyDynaBean abean=(LazyDynaBean)posDescFiledList.get(i);
                          				LazyDynaBean nextBean=null;
                          				if((i+1)<posDescFiledList.size())
                          					nextBean=(LazyDynaBean)posDescFiledList.get(i+1);
                          				 String desc=(String)abean.get("desc");
                          				 String desc2="";
                          				 String tmp="";
                          				 if(desc.length()>6)
                          				 {
                          				    for(int xx=0;xx<desc.length();xx++)
                          				    {
                          				       if(xx%6==0&&xx!=0)
                          				          tmp=tmp+"<br>";
                          				       tmp=tmp+desc.charAt(xx)+"";
                          				    }
                          				    desc=tmp;
                          				 }
                          				 if(desc.length()==2)
                          				    desc=desc.charAt(0)+"&nbsp;&nbsp;&nbsp;&nbsp;"+desc.charAt(1);	
                          				 if(nextBean!=null&&((String)nextBean.get("desc")).length()==2)
                          					desc2=((String)nextBean.get("desc")).charAt(0)+"&nbsp;&nbsp;&nbsp;&nbsp;"+((String)nextBean.get("desc")).charAt(1);
                          				 else if(nextBean!=null)
                          				 {
								            desc2=((String)nextBean.get("desc"));
								            if(desc2.length()>6)
                          				   {
                          				     String tmp2="";
                          				    for(int xx=0;xx<desc2.length();xx++)
                          				    {
                          				       if(xx%6==0&&xx!=0)
                          				          tmp2=tmp2+"<br>";
                          				       tmp2=tmp2+desc2.charAt(xx)+"";
                          				    }
                          				    desc2=tmp2;
                          				   }
								            
								         }
								                        			
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
                          					out.println("<td height='30' align='right' width='15%' nowrap><b>"+desc+":</b></td>");
	                          				out.println("<td  width='35%' align='left' valign='bottom'>"+value+"</td>");
	                          				out.println("<td  width='10%' align='right' nowrap><b>"+desc2+":</b></td>");
	                          				out.println("<TD  width='40%' align='left' valign='bottom'>"+nextValue+"</TD>");
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
                          						out.println("<td height='30' colspan=1 align='right' width='15%' valign='top' nowrap><b>"+desc+":</b></td>");
                          						out.println("<td  align='left' colspan=3 valign='bottom' >"+avalue+"</td>");
                          					}
                          					else
                          					{
                          						out.println("<td height='30' colspan=1 align='right' width='15%' nowrap><b>"+desc+":</b></td>");
                          						out.println("<td  align='left' colspan=3  valign='bottom'>"+avalue+"</td>");
                          					}
                          				}
                          				
                          				out.print("</tr>");
                          			 
									}
									%>                          		
                          		
                          			
                          		
                          		</table>
                        </td>
                        
                      </tr>
                 
                   <tr><td>
                   <br><br>
                   <table width="100%" border=0 cellpadding=0 cellspacing=0 >
                    <tbody>
                      <tr> 
                        <td style="TEXT-ALIGN:center">
                      			<img border="0"  src="/images/part_an.gif" alt = "<bean:message key="hire.jp.apply.apllypos"/>"  style="cursor:hand"  onClick='apply("${employPortalForm.isApplyedPos}","${employPortalForm.a0100}","${employPortalForm.userName}","${employPortalForm.posID}","${employPortalForm.requireId}","${employPortalForm.person_type}","${employPortalForm.loginName}");'">
                      			&nbsp;&nbsp;<img border="0"  src="/images/fh2.gif" alt = "<bean:message key="button.return"/>"  style="cursor:hand" onClick="goback();">&nbsp;  
                      	</td>
                      </tr>
                    </tbody>
                    </table>
                   </td>
                   </tr>
                   <% if(a0100!=null&&a0100.trim().length()>1){  %>
                   <tr><td>
                   <br>
                   <TABLE  id=rptb cellSpacing=0 
                        cellPadding=0 width="100%" align=center  border=0>
                        
                    <tr>
                     <td class="zpaboutHJ_mainTD"><font class='FontStyle'>已申请岗位</font></td>
                     </tr>
                    </table>
                   </td></tr>
                   <tr>
                   <td>
                    <TABLE id=rptb cellSpacing=0 
                        cellPadding=0 width="100%" align=center  border=0 class="hj_zhaopin_list_tab_title">
                          <TBODY>                    
                      <tr  align='center' > 
                        <th height='25'>
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
                      	<th>
                      		<bean:message key="hire.employActualize.resumeState"/>         			
                      	</th>
                      	<th>
                      		<bean:message key="lable.zp_plan.start_date"/>            			
                      	</th>
                      	<!-- 
                      	<td class=rptHead 
                            background=/images/r_titbg01.gif >
                      		<font color='#DF0024'>结束日期</font>          			
                      	</td>
                      	-->
                      	<th>
                      		<bean:message key="lable.zp_plan_detail.domain"/>              			
                      	</th>
                      	<th>
                      		<bean:message key="lable.zp_plan_detail.amount"/>          			
                      	</th>
                      	<logic:notEqual value="1" name="employPortalForm" property="max_count">
                      	<th>
                      		<bean:message key="hire.wish.order"/>           			
                      	</th>
                      	</logic:notEqual>
                      </tr>
                      <%
                      ArrayList  applyedPosList = employPortalForm.getApplyedPosList();
                      for(int i=0;i<applyedPosList.size();i++)
                      {
                          LazyDynaBean abean=(LazyDynaBean)applyedPosList.get(i);
                          String posName=(String)abean.get("posName");
                          String unitName=(String)abean.get("unitName");
                          String z0329=(String)abean.get("z0329");
                          String z0333=(String)abean.get("z0333");
                          String z0315=(String)abean.get("z0315");
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
                          out.println("<tr align=\"center\"  > ");
                          out.println("<td  align=\"left\" height='20'> "+posName+"</td>");
                          out.println("<td  align=\"left\" height='20'> "+unitName+"</td>");
                          out.println("<td  align=\"left\" height='20'> "+z0329+"</td>");
                          out.println("<td  align=\"left\" height='20'> "+z0333+"</td>");
                           out.println("<td  align=\"left\" height='20'> "+z0315+"</td>");
                          if(!max_count.equals("1"))
                          {
                              String thenumber=(String)abean.get("thenumber");
                              if(thenumber==null||thenumber.trim().equals(""))
                                 thenumber="&nbsp;";
                              out.println("<td  align=\"left\" height='20'> "+thenumber+"</td>");
                          }
                          out.println("</tr> ");
                      }
                      
                       %>
                     </TBODY>
                   </TABLE>
                    <% } %>
                </td>
                </tr>
  				</table>
  				</td>
  				</tr>
  				</table>
  				</td>
  				</tr>
  				</table>
  				
<script language="javascript" src="/general/sys/hjaxmanage.js"></script>
<script language="javascript">AxManager.writeCard();</script>
 <script language="javascript"> 
         initCard();
</script> 
</html:form>
