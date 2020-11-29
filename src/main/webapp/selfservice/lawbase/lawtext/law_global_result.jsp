<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc" %>
<%
int i = 0;
%>
<script language="javascript">
   function exeAdd(addStr)
   {
       target_url=addStr;
       window.open(target_url, 'il_body'); 
   }
   var o_term;
    function ajaxcheck(file_id,query,term)
   {
      o_term=term;      
      var hashvo=new ParameterSet();
      hashvo.setValue("file_id",file_id);    
      hashvo.setValue("query",query); 
      var request=new Request({method:'post',asynchronous:false,onSuccess:showCheckFlag,functionId:'10400201035'},hashvo);
   }
   function showCheckFlag(outparamters)
   {
      var sturt=outparamters.getValue("sturt");
      var file_id=outparamters.getValue("file_id");
      var query=outparamters.getValue("query");
      if(sturt=="false")
      {
        alert("您没有浏览权限，请与管理员联系！");
        return false;
      }
      if(query=="download")
      {
         window.open("/servlet/DigestDownLoad?id="+file_id,"_blank");
      }else if(query=="downlawbase")
      {
        window.open("downlawbase?id="+file_id+"&term="+o_term,"_blank");
      }
      else if(query=="original")
      {
      	window.open("/servlet/DigestDownLoad?id="+file_id+"&type=original","_blank");
      }else if(query=="affix")
      {
         location.href="/selfservice/lawbase/lawtext/affix_digest.do?b_affix=link&file_id="+file_id+"&result=global";
      }else if(query=="view")
      {
          location.href="/selfservice/lawbase/lawtext/law_view_base.do?b_query=link&viewFlag=globalsearch&flag=2&a_id="+file_id;
      }
   }
   
   function exeAdd(addStr)
   {
       target_url=addStr;
       window.open(target_url, '_self'); 
   }
</script>
<html>
	<body>
		<form name="GlobalSearchForm" method="post"
			action="/selfservice/lawbase/lawtext/globalsearch.do">
			<table width="100%" border="0" cellspacing="0" align="center"
				cellpadding="1" class="ListTable">
					<tr>
						<logic:empty name="lawbaseForm" property="field_str_item">
							<td align="center" class="TableRow" nowrap>
							<bean:message key="column.law_base.title" />
							&nbsp;
							</td>
							<logic:equal name="lawbaseForm" property="basetype" value="5">
								<logic:equal name="lawbaseForm" property="sign" value="1">
									<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",originalfile,">           						
									<td align="center" class="TableRow" nowrap>
										<bean:message key="column.law_base.original" />
										&nbsp;
									</td>
									</logic:notMatch>
								</logic:equal>
							</logic:equal>
							<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",note_num,">           						
							<td align="center" class="TableRow" nowrap>
								<bean:message key="column.law_base.notenum" />
								&nbsp;
							</td>
							</logic:notMatch>
							<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",issue_date,">           						
							<td align="center" class="TableRow" nowrap>
								<bean:message key="column.law_base.issuedate" />
								&nbsp;
							</td>
							</logic:notMatch>
							<logic:notEqual name="lawbaseForm" property="basetype" value="5">
							<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",implement_date,">           						
							<td align="center" class="TableRow" nowrap>
								<bean:message key="column.law_base.impdate" />
								&nbsp;
							</td>
							</logic:notMatch>
							</logic:notEqual>
							<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",viewcount,">
								<td align="center" class="TableRow" nowrap>
									浏览次数
								</td>
							</logic:notMatch>
							<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",extfile,">
				                <td align="center" class="TableRow" nowrap>
									&nbsp;
									附件
									&nbsp;
								</td>
							</logic:notMatch>
						</logic:empty>
						
						<logic:notEmpty name="lawbaseForm" property="field_str_item">
							<logic:empty name="lawbaseForm" property="file_str_item">
								<logic:iterate id="element" name="lawbaseForm" property="useableFileItem">
									<td align="center" class="TableRow" nowrap>
										<bean:write name="element" property="itemdesc"/>
									</td>
								</logic:iterate>
							</logic:empty>
							<logic:notEmpty name="lawbaseForm" property="file_str_item">
							<logic:iterate id="element" name="lawbaseForm" property="table_field">
								<td align="center" class="TableRow" nowrap>
									<bean:write name="element" property="itemdesc"/>
								</td>
							</logic:iterate>
							</logic:notEmpty>
						</logic:notEmpty>
						
					</tr>
					<hrms:extenditerate id="element" name="GlobalSearchForm"
						property="paginationForm.list" indexes="indexes"
						pagination="paginationForm.pagination" pageCount="${GlobalSearchForm.pagerows}"
						scope="session">
						<bean:define id="fileid" name="element" property="string(file_id)"/>
						<%
						  fileid = PubFunc.encrypt((String)fileid);
						
								if (i % 2 == 0)
								{
						%>
						<tr class="trShallow">
							<%
									} else
									{
							%>
						
						<tr class="trDeep">
							<%
									}
									i++;
							%>
							<logic:empty name="lawbaseForm" property="field_str_item">
								<td align="left" class="RecordRow" nowrap>
									&nbsp;<a href="###" onclick="ajaxcheck('<%=fileid %>','view')">
									<bean:write name="element" property="string(title)" filter="false" />&nbsp;</a>&nbsp;
								</td>
								<logic:equal name="lawbaseForm" property="basetype" value="5">
								<logic:equal name="lawbaseForm" property="sign" value="1">
									<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",originalfile,">           						
									<td align="center" class="RecordRow" nowrap>
										<logic:notEqual name="element" property="String(originalext)" value="">
											&nbsp;<a href="###" onclick="ajaxcheck('<%=fileid %>','original');">
											<img src="/images/view.gif" border=0></a>&nbsp;
										</logic:notEqual>
									</td>
									</logic:notMatch>
									</logic:equal>
								</logic:equal>
								<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",note_num,">           						
								<td align="left" class="RecordRow" nowrap>
									&nbsp;<bean:write name="element" property="string(note_num)" filter="true" />&nbsp;
								</td>
								</logic:notMatch>
								<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",issue_date,">           						
								<td align="left" class="RecordRow" nowrap>
									&nbsp;<bean:write name="element" property="string(issue_date)" filter="true" />&nbsp;
								</td>
								</logic:notMatch>
								<logic:notEqual name="lawbaseForm" property="basetype" value="5">
								<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",implement_date,">           						
								<td align="left" class="RecordRow" nowrap>
									&nbsp;<bean:write name="element" property="string(implement_date)" filter="true" />&nbsp;
								</td>
								</logic:notMatch>
								</logic:notEqual>
					            <logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",viewcount,">
	                   				<td align="right" class="RecordRow" nowrap>&nbsp;
										&nbsp;<bean:write name="element" property="string(viewcount)" filter="true" />&nbsp;
									</td>
								</logic:notMatch>
								<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",extfile,">
									<logic:equal name="element" property="string(digest)" value="1"> 
										<td align="left" class="RecordRow" nowrap>&nbsp;
								 			<a href="###" onclick="ajaxcheck('<%=fileid %>','affix');">附件</a>&nbsp;
							        	</td>
							    	</logic:equal>
							    	<logic:notEqual name="element" property="string(digest)" value="1"> 
										<td align="left" class="RecordRow" nowrap>
								 		&nbsp;
							        	</td>
							   		</logic:notEqual>  
						   		</logic:notMatch>
							</logic:empty>
							
							<logic:notEmpty name="lawbaseForm" property="field_str_item">
								<logic:empty name="lawbaseForm" property="file_str_item">
									<logic:iterate id="info" name="lawbaseForm" property="useableFileItem">
										<td align="left" class="RecordRow" nowrap>
										<logic:notEqual value="extfile"  name="info" property="itemid">
										<logic:notEqual value="ext"  name="info" property="itemid">
										<logic:notEqual value="originalext"  name="info" property="itemid">
										<logic:equal value="title" name="info" property="itemid">
											<a href="###" onclick="ajaxcheck('<%=fileid %>','view')"><bean:write name="element" property="string(${info.itemid})" filter="false"/></a>
										</logic:equal>
										<logic:notEqual value="title" name="info" property="itemid" >
											<logic:equal value="0" name="info" property="codesetid">
												<bean:write name="element" property="string(${info.itemid})" filter="true"/>
											</logic:equal>
											<logic:notEqual value="0" name="info" property="codesetid">
												<hrms:codetoname  codeid="${info.codesetid}" name="element" codevalue="string(${info.itemid})" codeitem="codeitem" scope="page"/>
												&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;										
											</logic:notEqual>
										</logic:notEqual>
										</logic:notEqual>
										</logic:notEqual>
										</logic:notEqual>
										<logic:equal value="ext"  name="info" property="itemid">
											<logic:notEmpty name="element" property="String(ext)">
											<a href="###" onclick="ajaxcheck('<%=fileid %>','view')">文件&nbsp;</a>
											</logic:notEmpty>
										</logic:equal>
										<logic:equal value="originalext"  name="info" property="itemid">
										<logic:notEmpty name="element" property="String(originalext)">
											<a href="###" onclick="ajaxcheck('<%=fileid %>','original');"><img src="/images/view.gif" border=0></a>&nbsp;
										</logic:notEmpty>
										</logic:equal>
										<logic:equal value="extfile"  name="info" property="itemid">
										<logic:notEmpty name="element" property="String(digest)">
											<a href="###" onclick="ajaxcheck('<%=fileid %>','affix');">附件</a>&nbsp;
										</logic:notEmpty>
										</logic:equal>
										</td>
									</logic:iterate>
								</logic:empty>
								<logic:notEmpty name="lawbaseForm" property="file_str_item">
									<logic:iterate id="info" name="lawbaseForm" property="table_field">
										<td align="left" class="RecordRow" nowrap>
										<logic:notEqual value="extfile"  name="info" property="itemid">
										<logic:notEqual value="ext"  name="info" property="itemid">
										<logic:notEqual value="originalext"  name="info" property="itemid">
										<logic:equal value="title" name="info" property="itemid">
											<a href="###" onclick="ajaxcheck('<%=fileid %>','view')"><bean:write name="element" property="string(${info.itemid})" filter="false"/></a>
										</logic:equal>
										<logic:notEqual value="title" name="info" property="itemid" >
											<logic:equal value="0" name="info" property="codesetid">
												<bean:write name="element" property="string(${info.itemid})" filter="true"/>
											</logic:equal>
											<logic:notEqual value="0" name="info" property="codesetid">
												<hrms:codetoname  codeid="${info.codesetid}" name="element" codevalue="string(${info.itemid})" codeitem="codeitem" scope="page"/>
												&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;
											</logic:notEqual>
										</logic:notEqual>
										</logic:notEqual>
										</logic:notEqual>
										</logic:notEqual>
										<logic:equal value="ext"  name="info" property="itemid">
											<logic:notEmpty name="element" property="String(ext)">
											<a href="###" onclick="ajaxcheck('<%=fileid %>','download')">文件&nbsp;</a>
											</logic:notEmpty>
										</logic:equal>
										<logic:equal value="originalext"  name="info" property="itemid">
										<logic:notEmpty name="element" property="String(originalext)">
											<a href="###" onclick="ajaxcheck('<%=fileid %>','original');"><img src="/images/view.gif" border=0></a>&nbsp;
										</logic:notEmpty>
										</logic:equal>
										<logic:equal value="extfile"  name="info" property="itemid">
										<logic:notEmpty name="element" property="String(digest)">
											<a href="###" onclick="ajaxcheck('<%=fileid %>','affix');">附件</a>&nbsp;
										</logic:notEmpty>
										</logic:equal>
										</td>
									</logic:iterate>
								</logic:notEmpty>
							</logic:notEmpty>
						</tr>
					</hrms:extenditerate>
			</table>
			<table width="100%" align="center" class="RecordRowP">
				<tr>
					<td valign="bottom" class="tdFontcolor">
						<hrms:paginationtag name="GlobalSearchForm" pagerows="${GlobalSearchForm.pagerows}" property="paginationForm.pagination" scope="session" refresh="true"></hrms:paginationtag>
					</td>
					<td align="right" nowrap class="tdFontcolor">
						<p align="right">
							<hrms:paginationlink name="GlobalSearchForm"
								property="paginationForm.pagination" nameId="paginationForm"
								propertyId="paginationProperty">
							</hrms:paginationlink>
					</td>
				</tr>
			</table>
			<table width="100%" align="center" cellpadding="0" cellspacing="0" style="padding-top:5px;">
				<tr>
					<td align="left">

						<INPUT type="button"
							onclick="exeAdd('./globalsearch.jsp?encryptParam=<%=PubFunc.encrypt("a_base_id=" + request.getParameter("a_base_id"))%>')"
							value="<bean:message key="law_maintenance.requery" />"
							class="mybutton">
						<bean:define id="baseid" name="lawbaseForm" property="base_id"/>
						<INPUT type="button" value="<bean:message key="button.return" />"
							class="mybutton"
							onclick="exeAdd('/selfservice/lawbase/lawtext/law_maintenance.do?b_query=link&encryptParam=<%=PubFunc.encrypt("a_base_id=" + (String)baseid) %>')">

					</td>
				</tr>
			</table>
		</form>
	</dody>
	<script language='javascript'>
		if(!getBrowseVersion()){//兼容非IE浏览器  wangb 20171123  
			var GlobalSearchForm = document.getElementsByName('GlobalSearchForm')[0];//设置form表单 width样式 
			GlobalSearchForm.style.width='99.5%';
		}
	</script>
</html>
