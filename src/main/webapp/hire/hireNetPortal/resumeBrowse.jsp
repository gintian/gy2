<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
 <%@ page import="com.hrms.struts.constant.SystemConfig,com.hjsj.hrms.utils.PubFunc"%>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/js/dict.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="JavaScript" src="/general/sys/hjaxmanage.js"></script>
<script language="JavaScript" src="/hire/hireNetPortal/employNetPortal.js"></script>
 <%
  String aurl = (String)request.getServerName();
	    String port=request.getServerPort()+"";
	    String prl=request.getScheme();
	    String url_p=prl+"://"+aurl+":"+port;
  %>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<%@ page import="com.hjsj.hrms.actionform.hire.employNetPortal.EmployPortalForm,
			     org.apache.commons.beanutils.LazyDynaBean,com.hrms.hjsj.utils.Sql_switcher,
			     com.hrms.struts.taglib.CommonData,com.hrms.hjsj.sys.Constant,
			     java.util.*"%>
<LINK 
href="/css/newHireStyle.css" type=text/css rel=stylesheet>

<LINK href="/css/nav.css" type=text/css rel=stylesheet>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
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
  UserView userView=(UserView)session.getAttribute(WebConstant.userView);
   EmployPortalForm employPortalForm=(EmployPortalForm)session.getAttribute("employPortalForm");
   String canPrint=employPortalForm.getCanPrint();
   String admissionCard=employPortalForm.getAdmissionCard();
   String persontype=PubFunc.getReplaceStr2(request.getParameter("personType"));
   String flag=PubFunc.getReplaceStr2(request.getParameter("flag"));
   String a01=employPortalForm.getA0100();
   String name="";
   if(userView!=null){
	    name=userView.getUserName()!=null?userView.getUserName():"";
   }
   
 %>
<html:form action="/hire/employNetPortal/search_zp_position"> 
<html>
  <head>
  <title> dfasdf</title>
  </head>
  <script language='javascript'>
  var perT="<%=persontype%>";
  var a0100 = "${employPortalForm.a0100}";
  var cardid = "${employPortalForm.admissionCard}";
  var nbase = "${employPortalForm.dbName}";
   function returnInfo2browse(outparamters)
   {
   		var operate=outparamters.getValue("operate");
   		alert(OPERATOR_IS_SUCCESS+"！");
   	<% if(flag==null||!flag.equals("photo")){ 
   			if(persontype!=null&&persontype.equals("0")){  %>
   				
   				window.opener.location='/hire/employActualize/employResume.do?b_query=link&personType=0';
   				window.location.reload();
   		<% } 
   			
   			if(persontype!=null&&persontype.equals("1")){ 
   		%>
   				
   				window.opener.location='/hire/employActualize/employResume.do?b_query=link&personType=1';
   				window.location.reload();
   		<% 
   			}
   			
   		}
   		if(flag==null||flag.equals("photo")){
   		%>
   			window.location.reload();
   		<%
   		}
   		
   		%>
   		if(operate=='del')
   		{
   		  window.opener.location.href=window.opener.location.href;
   		  window.close();
   	    }
   }
    //直接调用打印插件来打印简历
    var tabid="";
  function previewTableByActive()
  {
   var hashvo=new ParameterSet();
   hashvo.setValue("dbname","${employPortalForm.dbName}");   
   hashvo.setValue("inforkind","1"); 
   hashvo.setValue("flag","hire");
   hashvo.setValue("id","${employPortalForm.a0100}"); 
   var request=new Request({method:'post',onSuccess:showPrint,functionId:'07020100078'},hashvo);
  }

function initCard()
{
      var rl = document.getElementById("hostname").href;     
      var aurl=rl;
      var DBType="<%=dbtype%>";
      var UserName="<%=name%>";     
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
  <body>
  <div id='chajian' ></div>
    <a href="<%=url_p%>" style="display:none" id="hostname">for vpn</a>
    <%if(a01==null||a01.equals("")){
    %>
    		<script language='javascript'>
          					alert("请先登录!");
          					window.location="/hire/hireNetPortal/search_zp_position.do?b_login=login";
         	</script>
    <%	
    }
    %>
    
    <br> 
    <br>
    <%ArrayList fieldSetList=employPortalForm.getFieldSetList(); %>
<TABLE cellSpacing=0 cellPadding=0 width=70% align=center border=0>
  <TBODY>
  <tr >
     <td width="100%" colspan="2" align="center" > 
     			<div class="zw zw1" style="width:100%;">
                    	<h3 style="width:100%;text-align:left;">预览简历</h3>
                    </div>
  					  <div class="jj" style="width:100%">
                    	<h2 style="width:100%">
                    		<span class="els_r">
		                    	 <logic:equal name="employPortalForm" property="canPrint" value="1">
		                    	 	<logic:notEqual name="employPortalForm" property="previewTableId" value="#">
		                    	 		<input type="button" value="导出" class="buttonCss" style="padding-bottom: 3px;" onclick='printPDF("${employPortalForm.previewTableId}")'/><!-- 招聘外网,涉及到的人员信息加密处理 -->
		                    	 	</logic:notEqual>
		                    	 	<logic:equal name="employPortalForm" property="previewTableId" value="#">
		                    	 		<input type="button" value="导出" class="buttonCss" style="padding-bottom: 3px;" onclick='executeOutFile("${employPortalForm.hireChannel}","${employPortalForm.workExperience}","${employPortalForm.encryptA0100}")'/><!-- 招聘外网,涉及到的人员信息加密处理 -->
		                    	 	</logic:equal>
		                    	 	<logic:notEqual value="#" name="employPortalForm" property="previewTableId">
		                    	 	<% if(persontype!=null) { %>
		                    		  <button onclick='ysmethod("<bean:write name="employPortalForm" property="previewTableId"/>")'>卡片</button>
		                    		  <%} %>
		                    		 </logic:notEqual>
		                    	 </logic:equal>
		                    	  <logic:equal value="true" name="employPortalForm" property="canPrintExamno">
		                    	  	 <button onclick='javascript:printExamNo()'>准考证</button>
		                    	  </logic:equal>
	                    	 </span>
	                    	 <span>
	                    	 	<%
	                    	 	if(fieldSetList != null) {
	                    	 		LazyDynaBean abean=(LazyDynaBean)fieldSetList.get(0);
	                				String setid=(String)abean.get("fieldSetId");
	                      			String setdesc=(String)abean.get("fieldSetDesc");
	                    	 	%>
	                    	 	<%=setdesc %>
	                    	 	<%} else { %>
	                    	 	<bean:message key="gz.report.baseinfomation"/>
	                    	 	<%} %>
	                    	 </span>
                    	 </h2>
                        <div class="nr" style="width:100%">
	                        <TABLE  cellSpacing=0 cellPadding=0 style="width:100%" align=center border=0 class="table1">
					              <TBODY>
					              
					              
					              <%
									
					              	if(fieldSetList==null)
					              		fieldSetList=new ArrayList();
									HashMap resumeBrowseSetMap=employPortalForm.getResumeBrowseSetMap();
									HashMap setShowFieldMap=employPortalForm.getSetShowFieldMap();
									ArrayList remarkList=employPortalForm.getRemarkList();
									ArrayList zpPosList=employPortalForm.getZpPosList();
									String    zpPosID=employPortalForm.getZpPosID();
									ArrayList uploadFIleList = employPortalForm.getUploadFileList();
									String isAttach = employPortalForm.getIsAttach();
									String insideFlag  = employPortalForm.getInsideFlag();
									String anwserSet=employPortalForm.getAnswerSet();
									if(employPortalForm.getUserName()==null||employPortalForm.getUserName().equals("")){
										employPortalForm.setA0100("");
									}
									String a0100=employPortalForm.getA0100();
									ArrayList a01InfoList=(ArrayList)resumeBrowseSetMap.get("a01");
							      	for(int i=0;i<5&&i<a01InfoList.size();i++)
							      	{
							      		LazyDynaBean abean=(LazyDynaBean)a01InfoList.get(i);
							      		String itemdesc=(String)abean.get("itemdesc");
							      		String codesetid=(String)abean.get("codesetid");
							      		String viewvalue=(String)abean.get("viewvalue");
							      		
										String value=((String)abean.get("value")).trim();
							      		if(!codesetid.equals("0"))
							      			value=viewvalue;
							      		
							      		
							      		 out.print("<TR>");
							             out.print("<TD style='TEXT-ALIGN:right' height='26' width='18%'>"+itemdesc+"：</TD>");
						     	         out.print("<TD style='TEXT-ALIGN:left'  height='26' >"+value+"</TD>");
						                
							                
							      		 
							      		 if(i==0)
							      		 {
					                		out.print("<TD class=tdViewTitle  height='26'>&nbsp;</TD><TD style='TEXT-ALIGN:left' width='28%' height='26' rowSpan=5>"); 
					                 		//out.println("<a href='javascript:openPhoto()'  height='26' >");
					                 		%>
					                		<hrms:ole name="employPortalForm" dbpre="${employPortalForm.dbName}" a0100="a0100" scope="session" height="120" width="85"/>
					                		<%
					                		//out.print("</a>");
					                		out.print("</TD>");
							      		 }
							      		  out.print("</TR>");
							      	}
							      	for(int i=5;i<a01InfoList.size();i++)
							      	{
							      		LazyDynaBean abean=(LazyDynaBean)a01InfoList.get(i);
							      		String itemdesc=(String)abean.get("itemdesc");
							      		String codesetid=(String)abean.get("codesetid");
							      		String viewvalue=(String)abean.get("viewvalue");
							      		
										String value=(String)abean.get("value");
							      		if(!codesetid.equals("0"))
							      			value=viewvalue;
							      		out.print("<TR>");
							            out.print("<TD style='TEXT-ALIGN:right'  height='26'>"+itemdesc+"：</TD>");
						                out.print("<TD style='TEXT-ALIGN:l'  height='26'>"+value+"</TD>");
						                
						                String itemdesc2="";
						                String value2="";
						                i++;
						                if(i<a01InfoList.size())
						                {
							                LazyDynaBean abean2=(LazyDynaBean)a01InfoList.get(i);
							                String codesetid2=(String)abean2.get("codesetid");
								      		itemdesc2=(String)abean2.get("itemdesc");
								      		String viewvalue2=(String)abean2.get("viewvalue");
								      		
											itemdesc2+="：";
											value2=(String)abean2.get("value");
											if(!codesetid2.equals("0"))
							      				value2=viewvalue2;
											
						                }
								        out.print("<TD style='TEXT-ALIGN:right'  height='26'>"+itemdesc2+"</TD>");
							            out.print("<TD style='TEXT-ALIGN:left'  height='26'>"+value2+"</TD>");
							            out.print("</TR>");
							      	
							      	}
							      %>   
					           </TBODY>
					        </TABLE>
                        </div>
                    </div>

      <%
      	for(int i=1;i<fieldSetList.size();i++)
      	{
      		LazyDynaBean abean=(LazyDynaBean)fieldSetList.get(i);
			String setid=(String)abean.get("fieldSetId");
      		String setdesc=(String)abean.get("fieldSetDesc");
      		boolean ssf=false;
      		if(anwserSet!=null&&anwserSet.equalsIgnoreCase(setid))
      		   ssf=true;
      		ArrayList dataList=(ArrayList)resumeBrowseSetMap.get(setid.toLowerCase());
      		ArrayList showFieldList=(ArrayList)setShowFieldMap.get(setid.toLowerCase());
      	
      %>
        <div class="jj" style="width:100%">
            <h2 style="width:100%"><span><%=setdesc%></span></h2>
                <div class="nr" style="width:100%">
					<TABLE  cellSpacing=0 cellPadding=0 style="width:100%"  align=center border=0 class="table1">
            			<TBODY>
     
            			  <%
                  			 if(dataList==null)
			                   {
			                        out.println("<TR><td height='30'  width='18%' >&nbsp;</td><td>&nbsp;</td></TR>");		
			                       continue;
			                   }
			                      
					          for(int n=0;n<dataList.size();n++)
					          {
		               		 if(n!=0&&n!=dataList.size())
		               		 {
		               		    if(ssf)
		               		      out.println("<TR><td height='30'  width='100%' colspan='2' class='zp_subset_line'>&nbsp;</td></TR>");		
		               		    else
			               		  out.println("<TR><td height='30'  width='18%' class='zp_subset_line'>&nbsp;</td><td class='zp_subset_line'>&nbsp;</td></TR>");		
			                 }
		                     LazyDynaBean a_bean=(LazyDynaBean)dataList.get(n);	
                     
                              if(showFieldList!=null)
                              {
                          		for(int j=0;j<showFieldList.size();j++)
                          		{
                          			out.println("<TR>");
                          			LazyDynaBean aa_bean=(LazyDynaBean)showFieldList.get(j);
                          			String itemid=(String)aa_bean.get("itemid");
                          			String itemtype=(String)aa_bean.get("itemtype");
                          			String itemdesc=(String)aa_bean.get("itemdesc");
                          			String itemmemo=(String)aa_bean.get("itemmemo");
                          			String value=(String)a_bean.get(itemid);
                          			itemmemo = itemmemo.replace("\r\n","<br>");
                          			if(value==null||value.equals(""))
                          			   value="&nbsp;";
                          			if(ssf)
                          			{
                          			   out.print("<TD valign='top'  style='padding-left:100px;'  height='26' >"+itemmemo+"：</TD><tr>");
                          			   
                          			   out.print("<tr><TD valign='top'  style='padding-left:100px;' align='left' height='26' >"+value+"</TD>");
                          			   out.print("</TR>");
                          			}
                          			else
                          			{
                          			   out.print("<TD valign='top'  style='TEXT-ALIGN:right' width='18%'  height='26'>"+itemdesc+"：</TD>");
                          			   out.print("<TD valign='top'  style='TEXT-ALIGN-left' align='left'  height='26'>"+value+"</TD>");
                          			   out.print("</TR>");
                          			}
                          		}
			                   }
			                }
			       
			              %>
			        	</TBODY>
			        </TABLE>
 				</div>
 			</div>

           <%
     }
     %>
    
       
    
    <%if(isAttach.equals("1")&&insideFlag.equals("1")) {%>
   					 <div class="jj zw" style="width:100%;text-align: left;">
                    	<h3 style="width:100%"><span><bean:message key="hire.resume.attach"/></span></h3>
                        <div class="nr" style="width:100%">
     						 <TABLE cellSpacing=0 cellPadding=0 style="width:100%" align=center border=0 class="table">
        						<TBODY>              
					                <tr  align='center' > 
					                     <td class="hj_zhaopin_list_tab_titleone_1" width="10%">
					                      	<bean:message key="conlumn.mediainfo.info_id"/>         			
					                      	</td>
					                      	
					                     	<td class="hj_zhaopin_list_tab_titleone" width="90%">
					                      		<bean:message key="column.law_base.filename"/>       			
					                      	</td>
					                         
					                      	</tr>
					                      
					        	<%
					                      	for(int k=0;k<uploadFIleList.size();k++)
					                      	{
					                      	 LazyDynaBean a_bean = (LazyDynaBean)uploadFIleList.get(k);
					                      	 out.println("<tr>");
					                      	
											 String	styleClass="hj_zhaopin_list_tab_titletwo";
					                      	out.println("<td align='center' height='25' class='hj_zhaopin_list_tab_titletwo_1'>"+(k+1)+"</td>");//【11482】外网简历上传改为保存文件到文件夹，简历预览下载简历附件时，不能再使用i9999，改为使用文件名  jingq upd 2015.08.05
					                      	out.println("<td class='"+styleClass+"' nowrap ><a href='javascript:void(0);' onclick='javascript:downResume(\""+PubFunc.encrypt((String)a_bean.get("a0100"))+"\",\""+PubFunc.encrypt((String)a_bean.get("fileName"))+"\",\""+PubFunc.encrypt((String)a_bean.get("nbase"))+"\")' >"+(String)a_bean.get("fileName")+"</a></td>");
					                      	out.println("</tr>"); 
					                      	}
					                      	 %>
                      	 	</TBODY>
                      	 </TABLE>
                      	 </div>
                      	 </div>
     					<%} %>
     				<%--
   					 <div class="zw" style="width:100%">
                    	<h3 style="width:100%;text-align: left;" ><span><bean:message key="hire.remark"/></span></h3>
                        <div class="nr" style="width:100%">
                        <TABLE id=rptb cellSpacing=0  cellPadding=0 style="width:100%" align=center  border=0 class="table">
           				 <TBODY>      
            		
                      <tr  align='center' > 
                        <td class="hj_zhaopin_list_tab_titleone_1"  nowrap >
                      		<bean:message key="conlumn.mediainfo.info_id"/>&nbsp;&nbsp;          			
                      	</td>
                      	<td class="hj_zhaopin_list_tab_titleone"  nowrap >
                      		&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="column.law_base.title"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;         			
                      	</td>
                      	<td class="hj_zhaopin_list_tab_titleone" nowrap  >
                      		<bean:message key="label.query.day"/>&nbsp;&nbsp;            			
                      	</td>
                      	<td class="hj_zhaopin_list_tab_titleone" nowrap >
                      		<bean:message key="hire.remark.person"/>&nbsp;&nbsp;
                      		         			
                      	</td>
                      	<% if(persontype!=null&&persontype.equals("1")){  %>
                      	<td class="hj_zhaopin_list_tab_titleone" nowrap>
                      		<bean:message key="hire.remark.level"/>&nbsp;&nbsp;
                      		          			
                      	</td>
                      	<% } %>
                      	<td class="hj_zhaopin_list_tab_titleone" nowrap>
                      		<bean:message key="report.conter"/>
                      	</td>
                      </tr>
    		<%
    			if(remarkList!=null)
    			{
    				for(int x=0;x<remarkList.size();x++)
    				{
    				    LazyDynaBean a_bean=(LazyDynaBean)remarkList.get(x);
    				    String title=(String)a_bean.get("title");
    				    String date=(String)a_bean.get("date");
    				    String user=(String)a_bean.get("user");
    				    String content=(String)a_bean.get("content");
    				    if(content==null||content.equals(""))
    				        content="&nbsp;";
    				    content=content.replaceAll("\r\n","<br>");
    				    String level="";
    				    if(persontype!=null&&persontype.equals("1"))
    				    	level=(String)a_bean.get("level");
    				    
						  String	styleClass="hj_zhaopin_list_tab_titletwo";
    					out.println("<tr align='center'>");
    					out.println("<td  align='left' height='20' class='hj_zhaopin_list_tab_titletwo_1'>"+(x+1)+"</td>");
    					out.println("<td  align='left' class='"+styleClass+"'> "+title+"&nbsp"+" </td>");
    					out.println("<td  align='left' class='"+styleClass+"'> "+date+"&nbsp"+" </td>");
    					out.println("<td  align='left' class='"+styleClass+"'> "+user+"&nbsp"+"</td>");
    					
    					if(persontype!=null&&persontype.equals("1"))
    						out.println("<td  align='left' class='"+styleClass+"'>"+level+"&nbsp"+"</td>");
    					out.println("<td  align='left' class='"+styleClass+"'> "+content+"</td>");
    					out.println("</tr>");
    				}
    			}
    		%>  
     		</TBODY> 
    	</TABLE>  
                        </div>
                        </div>
     				 --%>
                        <div class="an" style="width:100%"><a href="javascript:void(0);"  onclick="window.close();"><img src="/images/hire/close.gif"/></a></div>
    	</td>
    	</tr>
    	
</TBODY></TABLE>

	
  </body>
</html>
</html:form>
<%
   if(canPrint.equals("1")||!admissionCard.equals("#"))
   {
    %>
 <script language="javascript">
         
         initCard();
         function downResume(a0100,filename,nbase){
			window.location.href="/servlet/hirelogin/BrowseFileServlet?a0100="+a0100+"&filename="+filename+"&dbName="+nbase;
         }
</script>  
<%}%>