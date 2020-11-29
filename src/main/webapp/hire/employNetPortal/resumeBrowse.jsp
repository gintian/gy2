<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hjsj.hrms.actionform.hire.employNetPortal.EmployPortalForm,
			     org.apache.commons.beanutils.LazyDynaBean,com.hrms.hjsj.utils.Sql_switcher,
			     com.hrms.struts.taglib.CommonData,com.hrms.hjsj.sys.Constant,com.hjsj.hrms.utils.PubFunc,
			     java.util.*,com.hrms.frame.codec.SafeCode"%>
 <%@ page import="com.hrms.struts.constant.SystemConfig"%>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/js/dict.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="JavaScript" src="/hire/employNetPortal/employNetPortal.js"></script>
 <%  
 String aurl = (String)request.getServerName();
	    String port=request.getServerPort()+"";
	    String prl=request.getScheme();
	    String url_p=prl+"://"+aurl+":"+port;
	    UserView userView=(UserView)session.getAttribute(WebConstant.userView);	
	    String userFullName="";
		if(userView != null){
			  userFullName=userView.getUserFullName(); 
			}
	    EncryptLockClient lockclient=(EncryptLockClient)session.getServletContext().getAttribute("lock");
	    String license=lockclient.getLicenseCount();
	    int version=userView.getVersion();
	    if(license.equals("0"))
	         version=100+version;
	    int usedday=lockclient.getUseddays();
        String dataflag=SafeCode.encode("<CARDSTYLE>A</CARDSTYLE><SUPER_USER>1</SUPER_USER>"); // 转码加密; SUPER_USER: 不限制用户管理范围
  %>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>

<LINK 
href="/css/employNetStyle.css" type=text/css rel=stylesheet>
<LINK href="/css/main.css" type=text/css rel=stylesheet>
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
   EmployPortalForm employPortalForm=(EmployPortalForm)session.getAttribute("employPortalForm");
   String canPrint=employPortalForm.getCanPrint();
   String admissionCard=employPortalForm.getAdmissionCard();
   String persontype=null;
   if(request.getParameter("personType")!=null)
   	  persontype=PubFunc.getReplaceStr2(request.getParameter("personType"));
   String flag=PubFunc.getReplaceStr2(request.getParameter("flag"));
   
 %>
<html:form action="/hire/employNetPortal/search_zp_position"> 
<html>
  <head>
  <title> dfasdf</title>
  <style type="text/css">
   td{
         word-break:break-all;
     }
     body{
       /* scrollbar-base-color:#C4D8EE; */
     }
  </style>
  </head>
  <script language='javascript'>
  var perT="<%=persontype%>";
   
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
   //招聘内网调用卡片时不用传这个flag了,xcs2014-10-16
   //hashvo.setValue("flag","hire");
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
   initCard();
   obj.SetCardID(tabid);
   obj.SetDataFlag("<%=dataflag%>");
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
      var UserName="<%=userView.getUserName()%>";
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
      obj.SetUserFullName("<%=userFullName%>");
   	  obj.SetHrpVersion("<%=version%>");
      obj.SetTrialDays("<%=usedday%>","30");
}
  </script>
  <body >
   
   
   
   
   <TABLE cellSpacing=0 cellPadding=0 width="100%" border=0 style="margin-top:0px;">
  <TBODY>
  <TR>
    <TD background=""></TD></TR></TBODY></TABLE>
    <a href="<%=url_p%>" style="display:none" id="hostname">for vpn</a>
    <%
           String bosflag= userView.getBosflag();//得到系统的版本号
           String bgcolor="";//为了保留hr中的样式
           if(bosflag!=null&&!bosflag.equals("hcm")){
            bgcolor="#f2f2f2";
    %>
        <br>
        <br>
    <%
        }
    %>
<TABLE cellSpacing=0 cellPadding=0 width=70% align=center border=0 style="margin-top:0px;">
  <TBODY>
  <tr>
              <td width="70%" class="zpaboutHJ_mainTD"><font class='FontStyle'>预览简历</font></td>
              </tr>
<tr>
              <td ><div class="zphr1">
                <hr class="viewhr" />
              </div></td>
            </tr>
 <tr>
    <TD align=middle width="70%">
      <TABLE cellSpacing=1 cellPadding=0 width="100%" align=center  border=0 bgColor='<%=bgcolor%>' >
        <TBODY>
        <TR class="common_background_color">
          <TD style="PADDING-LEFT: 20px; PADDING-BOTTOM: 1px" style="TEXT-ALIGN:left" height=20>
          <table width='100%'><tr><td align='left'><font class='FontStyle'><strong><bean:message key="gz.report.baseinfomation"/></strong></font></td>

           <td style="TEXT-ALIGN:right">
           <logic:equal name="employPortalForm" property="canPrint" value="1">
          
           <Input type='button' value="导出" onclick='executeOutFile("${employPortalForm.workExperience}","${employPortalForm.a0100}","<%=persontype%>")'  class="mybutton"/>
           <logic:notEqual value="#" name="employPortalForm" property="previewTableId">
           <% if(persontype!=null) { %>
           <Input type='button' value="<bean:message key="button.card"/>" onclick='ysmethod("<bean:write name="employPortalForm" property="previewTableId"/>")'  class="mybutton"/>
           <%} %>
           </logic:notEqual>
        </logic:equal>
       <logic:notEqual value="#" name="employPortalForm" property="admissionCard">
        <Input type='button' value="准考证" onclick='ysmethod("<bean:write name="employPortalForm" property="admissionCard"/>")'  class="mybutton"/>
       </logic:notEqual>
         </td></tr></table></TD></TR>
        <TR>
          <TD style="PADDING-LEFT: 100px;word-break:break-word;" bgColor=#ffffff>
            <TABLE id=Table34 cellSpacing=1 cellPadding=2 width="100%" 
            align=center border=0>
              <TBODY>
              
              
              <%
				ArrayList fieldSetList=employPortalForm.getFieldSetList();			
				HashMap resumeBrowseSetMap=employPortalForm.getResumeBrowseSetMap();
				HashMap setShowFieldMap=employPortalForm.getSetShowFieldMap();
				ArrayList remarkList=employPortalForm.getRemarkList();
				ArrayList zpPosList=employPortalForm.getZpPosList();
				String    zpPosID=employPortalForm.getZpPosID();
				ArrayList uploadFIleList = employPortalForm.getUploadFileList();
				String isAttach = employPortalForm.getIsAttach();
				String insideFlag  = employPortalForm.getInsideFlag();
				String anwserSet=employPortalForm.getAnswerSet();
				String a0100=employPortalForm.getA0100();
				ArrayList a01InfoList=(ArrayList)resumeBrowseSetMap.get("a01");
				if(a01InfoList==null)
					a01InfoList=new ArrayList();
		      	for(int i=0;i<5&&i<a01InfoList.size();i++)
		      	{
		      		LazyDynaBean abean=(LazyDynaBean)a01InfoList.get(i);
		      		String itemdesc=(String)abean.get("itemdesc");
		      		String codesetid=(String)abean.get("codesetid");
		      		String viewvalue=(String)abean.get("viewvalue");
		      		if(itemdesc.length()==2)
		      			itemdesc=itemdesc.charAt(0)+"&nbsp;&nbsp;&nbsp;"+itemdesc.charAt(1);
					String value=((String)abean.get("value")).trim();
		      		if(!codesetid.equals("0"))
		      			value=viewvalue;
		      		
		      		
		      		 out.print("<TR>");
		             out.print("<TD style='TEXT-ALIGN:right' width='18%' nowrap><font class='FontStyle'>"+itemdesc+"&nbsp;&nbsp;</font></TD>");
	     	         out.print("<TD style='TEXT-ALIGN:left' ><SPAN id=lbSex><font class='FontStyle'>"+value+"</font></SPAN></TD>");
	                 if(i!=0)
		                 out.print("<TD class=tdViewTitle>&nbsp;</TD></TR>");
		      		 
		      		 if(i==0)
		      		 {
		      		 	out.print("<TD style='TEXT-ALIGN:right' width='18%'>&nbsp;&nbsp;&nbsp;</TD>");
                		out.print("<TD style='TEXT-ALIGN:right' width='28%' rowSpan=5>"); 
                 		out.println("<a href='javascript:openPhoto()' >");
                 		%>
                		<hrms:ole name="employPortalForm" dbpre="${employPortalForm.dbName}" a0100="a0100" scope="session" height="120" width="85"/>
                		<%
                		out.print("</a>");
                		out.print("</TD>");
		      		 }
		      	}
		      	for(int i=5;i<a01InfoList.size();i++)
		      	{
		      		LazyDynaBean abean=(LazyDynaBean)a01InfoList.get(i);
		      		String itemdesc=(String)abean.get("itemdesc");
		      		String codesetid=(String)abean.get("codesetid");
		      		String viewvalue=(String)abean.get("viewvalue");
		      		if(itemdesc.length()==2)
		      			itemdesc=itemdesc.charAt(0)+"&nbsp;&nbsp;&nbsp;"+itemdesc.charAt(1);
					String value=(String)abean.get("value");
		      		if(!codesetid.equals("0"))
		      			value=viewvalue;
		      		out.print("<TR>");
		            out.print("<TD style='TEXT-ALIGN:right' nowrap><font class='FontStyle'>"+itemdesc+"&nbsp;&nbsp;</font></TD>");
	                out.print("<TD style='TEXT-ALIGN:l'><SPAN id=lbNation><font class='FontStyle'>"+value+"</font></SPAN></TD>");
	                
	                String itemdesc2="";
	                String value2="";
	                i++;
	                if(i<a01InfoList.size())
	                {
		                LazyDynaBean abean2=(LazyDynaBean)a01InfoList.get(i);
		                String codesetid2=(String)abean2.get("codesetid");
			      		itemdesc2=(String)abean2.get("itemdesc");
			      		String viewvalue2=(String)abean2.get("viewvalue");
			      		if(itemdesc2.length()==2)
			      			itemdesc2=itemdesc2.charAt(0)+"&nbsp;&nbsp;&nbsp;"+itemdesc2.charAt(1);
						itemdesc2+="&nbsp;&nbsp;";
						value2=(String)abean2.get("value");
						if(!codesetid2.equals("0"))
		      				value2=viewvalue2;
						
	                }
			        out.print("<TD style='TEXT-ALIGN:right' nowrap><font class='FontStyle'>"+itemdesc2+"</font></TD>");
		            out.print("<TD style='TEXT-ALIGN:left'><SPAN  id=lbBirthday><font class='FontStyle'>"+value2+"</font></SPAN></TD>");
		            out.print("</TR>");
		      	
		      	}
		      %>
          
              
             
           </TBODY></TABLE></TD></TR></TBODY></TABLE>
      
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
       <TABLE cellSpacing=1 cellPadding=0 width="100%" align=center  border=0 bgColor=<%=bgcolor%> >
        <TBODY>
        <TR class="common_background_color">
          <TD style="PADDING-LEFT: 20px; PADDING-BOTTOM: 1px;hieght:11px; line-height:11px;text-indent:18px; font-size:12px;*padding-top:4px;font-weight:bold; color:#1E1E1E;" align=left height=20><font class='FontStyle'><strong><%=setdesc%></strong></font></TD></TR>
        <TR>
          <TD 
          style="PADDING-LEFT: 100px; PADDING-BOTTOM: 10px; PADDING-TOP: 10px" bgColor=#ffffff>
    
            <TABLE id=Table34 cellSpacing=1 cellPadding=2 width="100%" align=center border=0 >
            <TBODY>
              <%
                   if(dataList==null)
                   {
                        out.println("<TR><td height='30'  width='18%' >&nbsp;</td><td>&nbsp;</td></TR></TBODY></TABLE></TD></TR></TBODY></TABLE>");		
                       continue;
                   }
                      
               for(int n=0;n<dataList.size();n++)
               {
               		 if(n!=0)
               		 {
               		    if(ssf)
               		      out.println("<TR><td height='30'  width='100%' colspan='2'>&nbsp;</td></TR>");		
               		    else
	               		  out.println("<TR><td height='30'  width='18%' >&nbsp;</td><td>&nbsp;</td></TR>");		
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
                          			   out.print("<TD valign='top'  style='TEXT-ALIGN:left' colspan='2' nowrap><font class='FontStyle'>"+itemmemo+"&nbsp;&nbsp;</font></TD><tr>");
                          			   
                          			   out.print("<tr><TD valign='top'  style='TEXT-ALIGN-left' align='left' colspan='2'><font class='FontStyle'>"+value+"</font></TD>");
                          			   out.print("</TR>");
                          			}
                          			else
                          			{
                          			   out.print("<TD valign='top'  style='TEXT-ALIGN:right' width='18%' nowrap><font class='FontStyle'>"+itemdesc+"&nbsp;&nbsp;</font></TD>");
                          			   out.print("<TD valign='top'  style='TEXT-ALIGN-left' align='left'><font class='FontStyle'>"+value+"</font></TD>");
                          			   out.print("</TR>");
                          			}
                          		}
                          		}
                }
       
              %>
        </TBODY></TABLE>
      </TD></TR></TBODY></TABLE>
      <%
     }
     %>
    <%if(isAttach.equals("1")&&insideFlag.equals("1")) {
    %>
       <TABLE cellSpacing=0 cellPadding=0 width="100%" align=center border=0  >
        <TBODY>
        <TR class="common_background_color" bgColor=<%=bgcolor%>>
          <TD style="PADDING-LEFT: 20px; PADDING-BOTTOM: 1px;hieght:11px; line-height:11px;text-indent:18px; font-size:12px;*padding-top:4px;font-weight:bold; color:#1E1E1E;"  align=left
          height=20><font class='FontStyle'><strong><bean:message key="hire.resume.attach"/></strong></font></TD></TR><!-- 简历附件 -->
        <TR>
          <TD>
         <TABLE  id=rptb cellSpacing=0  cellPadding=0 width="100%" align=center  border=0>
                          <TBODY>                    
                      <tr  align='center' > 
                        <th style="PADDING-LEFT: 40px;" width="40px" align="center">
                      		<bean:message key="conlumn.mediainfo.info_id"/>         <!-- 序号 -->			
                      	</th>
                      	
                     	<th  width="90%"align="center">
                      		<bean:message key="column.law_base.filename"/>       		<!-- 文件名称 -->	
                      	</th>
                         
                      	</tr>
                      
        	<%
                      	for(int k=0;k<uploadFIleList.size();k++)//附件内容
                      	{
                      	 LazyDynaBean a_bean = (LazyDynaBean)uploadFIleList.get(k);
                      	 out.println("<tr>");
                      	
                      	out.println("<td style='PADDING-LEFT: 40px;' align='center' height='25'>"+(String)a_bean.get("seq")+"</td>");
                      	out.println("<td nowrap align='center'><a href='/servlet/hirelogin/BrowseFileServlet?a0100="+(String)a_bean.get("a0100")+"&i9999="+(String)a_bean.get("i9999")+"&dbName="+(String)a_bean.get("nbase")+"'>"+(String)a_bean.get("title")+"</a></td>");
                      	//out.println("<td align='center' class=rptItemMain ><img src='/images/delete.gif' border='0' style='cursor:hand' onclick=\"deleteattach('"+(String)abean.get("a0100")+"','"+(String)abean.get("i9999")+"','"+(String)abean.get("nbase")+"')\"/></td>");
                      	out.println("</tr>"); 
                      	}
                      	 %>
                      	 	</TBODY>
                      	 </TABLE>
                      	 </TD></TR></TBODY></TABLE>
     <%} %>
                           
         <TABLE cellSpacing=0 cellPadding=0 width="100%" align=center border=0>
            <TBODY>   <!-- 评语 -->
                <TR class="common_background_color" bgColor=<%=bgcolor%>>
                    <TD style="PADDING-LEFT: 15px; PADDING-BOTTOM: 1px;hieght:11px; line-height:11px;text-indent:18px; font-size:12px;*padding-top:4px;font-weight:bold; color:#1E1E1E;" align=left height=20>
                    <font class='FontStyle'><strong><bean:message key="hire.remark"/>&nbsp;&nbsp;</strong></font></TD></TR>
                <TR>
                <TD>
                    <TABLE  id=rptb cellSpacing=0  cellPadding=0 width="100%" align=center  border=0>
                    <TBODY> 
                      <tr  align='center'>
                        <th  style="PADDING-LEFT: 40px;" width="40px" align="center">
                      		<bean:message key="conlumn.mediainfo.info_id"/>      <!-- 序号 -->		   			
                      	</th>
                      	<th  nowrap align="center">
                      		<bean:message key="column.law_base.title"/>        <!-- 标题 -->			
                      	</th>
                      	<th nowrap  align="center">
                      		<bean:message key="label.query.day"/>            			<!-- 日期 -->
                      	</th>
                      	<th nowrap align="center">
                      		<bean:message key="hire.remark.person"/><!-- 评审人 -->
                      		         			
                      	</th>
                      	<% //if(persontype!=null&&persontype.equals("1")){  %>
                      	<th nowrap align="center">
                      		<bean:message key="hire.remark.level"/>
                      		          			
                      	</th>
                      	<% //} %>
                      	<th nowrap align="center">
                      		<bean:message key="report.conter"/><!-- 内容 -->
                      	</th>
                      </tr>
    		<%
    			if(remarkList!=null)//评语内容
    			{
    				for(int x=0;x<remarkList.size();x++)
    				{
    				    LazyDynaBean a_bean=(LazyDynaBean)remarkList.get(x);
    				    String title=(String)a_bean.get("title");
    				    String date=(String)a_bean.get("date");
    				    String user=(String)a_bean.get("user");
    				    String content=a_bean.get("content")==null?"":(String)a_bean.get("content");
    				    if(content==null||content.equals(""))
    				        content="&nbsp;";
    				    content=content.replaceAll("\r\n","<br>");
    				    content=content.replaceAll("\n","<br>");
    				    String level="";
    				    //if(persontype!=null&&persontype.equals("1"))
    				    level=(String)a_bean.get("level");
    					out.println("<tr align='center'>");
    					out.println("<td style='PADDING-LEFT: 40px;' style='vertical-align: top;' align='center' height='20'  width='10%'>"+(x+1)+"</td>");
    					out.println("<td  align='center' width='20%' style='vertical-align: top;'> "+title+"&nbsp"+" </td>");
    					out.println("<td  align='center' width='10%' style='vertical-align: top;'> "+date+"&nbsp"+" </td>");
    					out.println("<td  align='center' width='10%' style='vertical-align: top;'> "+user+"&nbsp"+"</td>");
    					
    					//if(persontype!=null&&persontype.equals("1"))
    					out.println("<td  align='center' width='10%' style='vertical-align: top;'>"+level+"&nbsp"+"</td>");
    					out.println("<td  align='left'  width='40%' style='vertical-align: top;'> "+content+"</td>");
    					out.println("</tr>");
    				}
    			}
    		%>
    		</TBODY>
                </TABLE></TD></TR>  
     		</TBODY> 
    	</TABLE>  
    	 <% if(persontype!=null) { %>
     <% if(persontype.equals("0")||persontype.equals("1")||persontype.equals("4")){  %>
     <TABLE class="rptTable common_background_color" id=rptb cellSpacing=0  cellPadding=0 width='100%' align=center  border=0>
                    <tr><td style="PADDING-LEFT: 20px; PADDING-BOTTOM: 1px;hieght:11px; line-height:11px;text-indent:18px; font-size:12px;*padding-top:4px;font-weight:bold; color:#1E1E1E;" align=left height=20><font class='FontStyle'>应聘岗位(专业) ：</strong></font></td>
     </table>
      <TABLE class="hj_zhaopin_list_tab_title common_border_color" id=rptb cellSpacing=0  cellPadding=0 width='100%' align=center  border=0>
            <TBODY>      
             <%
   		if(zpPosList!=null){
	   		for(int i=0;i<zpPosList.size();i++)
	   		{
	   			CommonData data=(CommonData)zpPosList.get(i);	
	   			out.print("<tr align='center'><td align='center' class='common_border_color'><input type='radio' name='zpPosID' disabled  value='"+data.getDataValue()+"' ");
	   			if(zpPosID!=null&&zpPosID.equalsIgnoreCase(data.getDataValue()))
	   				out.print(" checked ");
	   			else if(zpPosID==null||zpPosID.equals("-1"))
	   				out.print(" checked ");
	   			out.print(" /></td><td align='left' class='common_border_color'>&nbsp;"+data.getDataName()+"</td></tr>");
	   		
	   		}
	   	}%>
	   	</TBODY>
   </TABLE>
	  <% 	}
	
   
       	}
   %>
      <% if(persontype!=null) { %>
    
    <table width='100%' ><tr>
    <td style='TEXT-ALIGN:right' nowrap>
    	<% if(persontype.equals("0")){ 
    	ArrayList resumeStateList=employPortalForm.getResumeStateList();
    	String dmlStatus=employPortalForm.getDmlStatus();//dml 2011-03-29
    	if(dmlStatus==null){
    	   dmlStatus="";
    	}
    	if(dmlStatus.equalsIgnoreCase("-3")){
    	}else{
	    	for(int h=0;h<resumeStateList.size();h++)
	    	{
	    	  CommonData data=(CommonData)resumeStateList.get(h);
	    	  if(data.getDataValue().equals("-2")||data.getDataValue().equals("-3")||data.getDataValue().equalsIgnoreCase("-1"))//去掉为选职位按钮 dml2011-03-30
	        	  continue;
	    	  out.print("<input type='button' value='"+data.getDataName()+"' onclick='setSelectValue(\""+data.getDataValue()+"\",\""+data.getDataName()+"\",\""+a0100+"\");' class='mybutton' />");
	
	    	 }
    	 }
    	%>
    	<%if(zpPosList!=null&&zpPosList.size()>0){ %>
    	<input type='button' value='<bean:message key="gz.acount.filter.delete"/>' onclick='delBrowse("${employPortalForm.a0100}")' class="mybutton"  />
    	<%} %>
    	<input type='button' value='<bean:message key="hire.move.personstorehouse"/> ' onclick='switchPersonType("${employPortalForm.a0100}")'  class="mybutton"   />
    	<% } %>
    	<input type='button' value="<bean:message key="hire.employActualize.personnelFilter.comment"/>"  class="mybutton"   onclick='review("${employPortalForm.a0100}");'/> <!-- 评语 -->
    	<input type='button' value="<bean:message key="button.close"/>" onclick='window.close()' class="mybutton"  />
    </td></tr>
    <tr>
    	<td>
    		<br>
    		<br>
    		<br>
    		<br>
    		<br>
    	</td>
    </tr>
    
    </table>
    
    <% } else { %>
     <table width='100%' ><tr>
    <td style='TEXT-ALIGN:center' nowrap>
    <input type='button' value="<bean:message key="button.close"/>" onclick='window.close()' class="mybutton"  />
      </td>
      </tr>
      </table>
     <% } %>
      
      <TABLE class=tbView id=Table3 cellSpacing=0 cellPadding=2 width="100%" 
      border=0>
        <TBODY></TBODY></TABLE>
      <TABLE class=tbView id=Table3 cellSpacing=0 cellPadding=2 width="100%" 
      border=0>
        <TBODY></TBODY></TABLE>
      <TABLE class=tbView id=Table3 cellSpacing=0 cellPadding=2 width="100%" 
      border=0>
        <TBODY></TBODY></TABLE></TD></TR>
  <TR>
    <TD>&nbsp;</TD></TR></TBODY></TABLE>

   
   
   
  
  </body>
</html>
</html:form>
<%
   if(canPrint.equals("1")||!admissionCard.equals("#"))
   {
    %>
<script language="javascript" src="/general/sys/hjaxmanage.js"></script>
<script language="javascript">AxManager.writeCard();</script>
 <script language="javascript">
         
         initCard();
</script>  
<%}%>