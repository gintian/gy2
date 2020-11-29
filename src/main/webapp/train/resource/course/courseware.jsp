<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hjsj.hrms.businessobject.train.MediaServerParamBo" %>
<%@page import="com.hrms.struts.constant.SystemConfig"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="java.net.URLEncoder"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%
String pathurl="http:\\\\"+request.getServerName()+":"+request.getServerPort();
pathurl=SafeCode.encode(pathurl);

	
	String filepath = request.getSession().getServletContext().getRealPath("/");
	if(SystemConfig.getPropertyValue("webserver").equals("weblogic"))
	{
		filepath = session.getServletContext().getResource("/").getPath();
		//filepath=session.getServletContext().getResource("").getPath();//.substring(0);
	   if(filepath.indexOf(':')!=-1)
		  {
		   filepath=filepath.substring(1);   
		  }
		  else
		  {
			  filepath=filepath.substring(0);      
		  }
	   int nlen=filepath.length();
		  StringBuffer buf=new StringBuffer();
		  buf.append(filepath);
		  buf.setLength(nlen-1);
		  filepath=buf.toString();
	}
	String tmppath = filepath.replaceAll("\\\\","``");
	String openoffice = SystemConfig.getPropertyValue("openoffice");
	String swftools = SystemConfig.getPropertyValue("swftools");
%>
<script language="JavaScript" src="/js/wz_tooltip.js"></script>
<script language="javascript" src="/js/dict.js"></script>
<script type="text/javascript"
	src="/train/resource/course/courseTrain.js"></script>
<script type="text/javascript" src="/train/resource/course/gmsearch.js"></script>
<html:form action="/train/resource/courseware">
	<html:hidden name="coursewareForm" property="a_code" />
	<html:hidden name="courseForm" property="a_code1" />
	<html:hidden name="coursewareForm" property="r5000" />
	<bean:define id="r50id" name="coursewareForm" property="r5000"></bean:define>
	<%
		int i = 0;
	%>
	<table border="0" cellpadding="0" cellspacing="0" align="center" style="margin-top: 10px;" class="ListTable">
	<tr><td class="RecordRow" style="border-top: none;border-bottom: 0px;border-left: 0px; padding: 0;">
	<table width="100%" border="0" cellspacing="0" align="center"
		cellpadding="0" class="ListTableF" style="margin-left:0px;">
		<thead>
			<tr>
				<logic:notEqual value="1" name="coursewareForm" property="isParent">
				<td align="center" class="TableRow" >
					<input type="checkbox" name="selbox" onclick="batch_select(this,'r5100');" title='<bean:message key="label.query.selectall"/>' />
				</td>
				</logic:notEqual>
				<logic:iterate id="element" name="coursewareForm"
					property="itemlist">
					<logic:equal value="true" name="element" property="visible">
						<logic:notEqual value="1" name="coursewareForm" property="isParent">
						<logic:equal value="edit" name="element" property="itemid">
							<td align="center" class="TableRow" style="border-right: 0px;" nowrap>
							&nbsp;<bean:write name="element" property="itemdesc" filter="true" />&nbsp;
							</td>
						</logic:equal>
						</logic:notEqual>
						<logic:equal value="down" name="element" property="itemid">
							<% if ("1".equals(MediaServerParamBo.getIsDownload1(r50id.toString()))){ %>
							<td align="center" class="TableRow" style="border-right: 0px;" nowrap>
							&nbsp;<bean:write name="element" property="itemdesc" filter="true" />&nbsp;
							</td>
							<%} %>
						</logic:equal>
						<logic:notEqual value="edit" name="element" property="itemid">
						<logic:notEqual value="down" name="element" property="itemid">
						<logic:notEqual value="r5115" name="element" property="itemid">
							<td align="center" class="TableRow" style="border-right: 0px;" nowrap>
							&nbsp;<bean:write name="element" property="itemdesc" filter="true" />&nbsp;
							</td>
						</logic:notEqual>
						</logic:notEqual>
						</logic:notEqual>
					</logic:equal>
				</logic:iterate>
			</tr>
		</thead>
	<hrms:paginationdb id="element2" name="coursewareForm"
			sql_str="coursewareForm.strsql" table="" where_str="coursewareForm.strwhere"
			columns="coursewareForm.columns" page_id="pagination"
			pagerows="${coursewareForm.pagerows}" order_by=" order by r5100 desc">
		<%
				if (i % 2 == 0) {
			%>
			<tr class="trShallow">
				<%
					} else {
				%>
			
			<tr class="trDeep">
				<%
					}
								i++;
				%>
				<bean:define id="idid" name="element2" property="r5100" />
				<bean:define id="courseName" name="element2" property="r5103" />
				<%String r5100 = SafeCode.encode(PubFunc.encrypt(idid.toString()));
				String r5103 = SafeCode.encode(courseName.toString());%>
				<logic:notEqual value="1" name="coursewareForm" property="isParent">
				<td align="center" class="RecordRow" nowrap>
					&nbsp;<input type="checkbox" name="r5100" value='<%=r5100 %>' />&nbsp;
				</td>
				</logic:notEqual>
				<logic:iterate id="element1" name="coursewareForm" property="itemlist">
					<logic:equal value="true" name="element1" property="visible">
						<bean:define id="nid" name="element1" property="itemid" />
						<logic:equal name="element1" property="itemtype" value="M">
								
								<logic:notEqual value="r5115" name="nid">
								  <td align="left" width="360" onmouseout="UnTip();" onmouseover="outContent('<%=r5100 %>','${nid }');" class="RecordRow" style="border-right: 0px;" nowrap>
									&nbsp;<bean:write name="element2" property="${nid}" filter="false"/>&nbsp;
								  </td>
								</logic:notEqual>
						</logic:equal>
						<logic:notEqual name="element1" property="itemtype" value="M">

							<logic:equal name="element1" property="itemtype" value="A">
								<logic:equal name="element1" property="codesetid" value="0">
									<logic:notEqual value="1" name="coursewareForm" property="isParent">
									<logic:equal value="edit" name="nid">
									  <td align="left" class="RecordRow" nowrap>&nbsp;
									  <hrms:priv func_id="32306C12" module_id="">
										<img src="/images/edit.gif" border="0" onclick="edit('<%=r5100 %>');" style="cursor:hand;">
									  </hrms:priv>
									  </td>
									</logic:equal>
									</logic:notEqual>
									<logic:equal value="down" name="nid">
										<bean:define id="fileid" name="element2" property="fileid"></bean:define>
										<% if ("1".equals(MediaServerParamBo.getIsDownload1(r50id.toString()))){
											    %>
									 		<td align="left" class="RecordRow" nowrap>
											 	<logic:notEqual value="" name="element2" property="r5113">
												&nbsp;<a href='/servlet/vfsservlet?fileid=<%=fileid%>'>下载</a>&nbsp;
												</logic:notEqual>
											 </td>
											<%} %>
									</logic:equal>
									<logic:equal value="show" name="nid">
									    <td align="left" class="RecordRow" nowrap>
									    <bean:define id="urlr5113"  name="element2" property="r5113"></bean:define>
										<logic:notEqual value="" name="element2" property="r5113">
											<logic:equal value="3" name="element2" property="r5105">
												&nbsp;<a href='javascript:;' onclick="liulan('<%=r5100 %>')">浏览</a>&nbsp;
											</logic:equal>
											<logic:equal value="4" name="element2" property="r5105">
												<%
													String liulanurl1=urlr5113.toString().substring(0,urlr5113.toString().lastIndexOf(System.getProperty("file.separator"))+1);
													liulanurl1 = liulanurl1.replaceAll("&","`")+"`";
													liulanurl1=SafeCode.encode(PubFunc.encrypt(liulanurl1+idid));
												 %>
												 &nbsp;<a href='javascript:;' onclick="liulan2('<%=liulanurl1 %>','<%=r5100 %>');">浏览</a>&nbsp;
											</logic:equal>
											<logic:equal value="6" name="element2" property="r5105">
												 &nbsp;<a href='javascript:;' onclick="liulan6('<bean:write name="element2" property="r5113" filter="false" />');">浏览</a>&nbsp;
											</logic:equal>
											<logic:equal value="1" name="element2" property="r5105">
											<%if(!urlr5113.toString().toLowerCase().endsWith(".zip")){
											//String liulanurl=urlr5113.toString().substring(urlr5113.toString().indexOf("coureware\\"));//,urlr5113.toString().toLowerCase().lastIndexOf("\\")+1);
											String liulanurl=SafeCode.encode(urlr5113.toString());//+idid);
											//if(urlr5113.toString().indexOf(".")!=-1 ){
											%>
											
											<%if (swftools != null && swftools.length() > 0 && openoffice != null && openoffice.length() > 0){ %>
											&nbsp;<a href='javascript:;' onclick="liulan('<%=r5100 %>')">浏览</a>&nbsp;
											<%}else { %>
												<bean:define id="pa" name="element2" property="r5113"></bean:define>
												<% String liupa = filepath.replace("\\","/") + pa.toString().replace("\\","/"); 
													liupa = SafeCode.encode(PubFunc.encrypt(liupa));
												%>
												&nbsp;<a href="javascript:;" onclick="window.open('/DownLoadCourseware?url=<%=liupa %>');">浏览</a>&nbsp;
											<%} %>
											<%} else{
												String liulanurl1=urlr5113.toString().substring(0,urlr5113.toString().lastIndexOf(System.getProperty("file.separator"))+1);
												liulanurl1=SafeCode.encode(liulanurl1+idid);
											%>
											&nbsp;<a href='javascript:;' onclick="liulan1('<%=liulanurl1 %>');">浏览</a>&nbsp;
											<%} %>
											</logic:equal>
										</logic:notEqual>
										<logic:equal value="2" name="element2" property="r5105">
										&nbsp;<a href='javascript:;' onclick="show1('<%=r5100 %>');">浏览</a>&nbsp;
										</logic:equal>
										<input type="hidden" name="url<%=r5100 %>" value='${urlr5113 }' />
										</td>
									</logic:equal>
									<logic:equal value="r5103" name="nid">
									<td align="left" width="120" title="<bean:write name="element2" property="${nid}" filter="false" />" class="RecordRow" nowrap>
										<div STYLE="width: 100px; overflow:hidden;white-space: nowrap;text-overflow:ellipsis">
			 								<span>&nbsp;<bean:write name="element2" property="${nid}" filter="false" />&nbsp;</span>
			 							</div>
			 						</td>	
			 						</logic:equal>
									<logic:notEqual value="edit" name="nid">
									<logic:notEqual value="down" name="nid">
									<logic:notEqual value="show" name="nid">
									<logic:notEqual value="r5103" name="nid">
									    <td align="left" width="120" class="RecordRow" nowrap>
										&nbsp;<bean:write name="element2" property="${nid}" filter="false" />&nbsp;
										</td>
									</logic:notEqual>
									</logic:notEqual>
									</logic:notEqual>
									</logic:notEqual>
								</logic:equal>
								<logic:notEqual name="element1" property="codesetid" value="0">
									<td align="center" width="120" class="RecordRow" nowrap>
									<logic:notEqual name="element1" property="codesetid" value="UN">
										<bean:define id="codesetid" name="element1" property="codesetid" />
										<hrms:codetoname codeid="${codesetid}" name="element2"
											codevalue="${nid}" codeitem="codeitem" scope="page" />
										&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;
									</logic:notEqual>
									<logic:equal name="element1" property="codesetid" value="UN">
											<hrms:codetoname codeid="UN" name="element2"
												codevalue="${nid}" codeitem="codeitem" scope="page" />
											&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;
									</logic:equal>
									</td>
								</logic:notEqual>
							</logic:equal>
							<logic:notEqual name="element1" property="itemtype" value="A">
								<logic:notEqual name="element1" property="itemtype" value="N">
									<td align="center" width="130" class="RecordRow" nowrap>
									&nbsp;<bean:write name="element2" property="${nid}" filter="false" />&nbsp;
									</td>
								</logic:notEqual>
								<logic:equal name="element1" property="itemtype" value="N">
									<logic:equal value="3" name="element2" property="r5105">
										<td align="right" width="75" class="RecordRow" style="border-right: 0px;" nowrap>
										&nbsp;<bean:write name="element2" property="${nid}" filter="false" />&nbsp;
										</td>
									</logic:equal>
									<logic:notEqual value="3" name="element2" property="r5105">
										<td align="center" width="75" class="RecordRow" style="border-right: 0px;color: #cccccc;" nowrap>
										&nbsp;--&nbsp;
										</td>
									</logic:notEqual>
								</logic:equal>
							</logic:notEqual>
						</logic:notEqual>
					</logic:equal>
				</logic:iterate>
			</tr>
	</hrms:paginationdb>
	</table>
	</td>
	</tr>
	<tr>
			<td>
				<table width="100%" class="RecordRowP" align="center">
					<tr>
						<td valign="bottom" class="tdFontcolor">
							<hrms:paginationtag name="coursewareForm"
								pagerows="${coursewareForm.pagerows}" property="pagination"
								scope="page" refresh="true"></hrms:paginationtag>
						</td>
						<td align="right" nowrap class="tdFontcolor">
							<p align="right">
								<hrms:paginationdblink name="coursewareForm"
									property="pagination" nameId="coursewareForm" scope="page">
								</hrms:paginationdblink>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			
			<td align="l" style="padding-top: 5px;">
			<logic:notEqual value="1" name="coursewareForm" property="isParent">
			 <hrms:priv func_id="32306C11" module_id="">
				<input type="button" class="mybutton" value='<bean:message key="button.insert" />' onclick="addcourseware();" />
			 </hrms:priv>
			  <hrms:priv func_id="32306C13" module_id="">
				<input type="button" class="mybutton" value='<bean:message key="button.delete" />' onclick="del();"/>
			  </hrms:priv>
			  </logic:notEqual>
				<input type="button" class="mybutton" value='<bean:message key='reportcheck.return'/>' onclick="returnback1();"/>
			</td>
		</tr>
	</table>
</html:form>
<script language="javascript">

function edit(r5100){
	var a_code=document.getElementsByName("a_code")[0].value;
	var r5000=document.getElementsByName("r5000")[0].value;
	coursewareForm.action="/train/resource/courseware.do?b_add=link&r5100="+r5100+"&a_code="+a_code+"&id="+r5000;
	coursewareForm.submit();
}
function show1(r5100){
	var thecodeurl = "/train/resource/courseware.do?b_show1=link&isParent=${coursewareForm.isParent}&r5100="+r5100;
	window.showModalDialog(thecodeurl, "", "dialogWidth:600px; dialogHeight:500px;resizable:yes;center:yes;scroll:yes;status:no");
}
function del(){
	var sel="";
	var urlsb="";
	var sels=document.getElementsByName("r5100");
	for(var i=0;i<sels.length;i++){
		if(sels[i].checked){
			sel+=sels[i].value+",";
			urlsb+=document.getElementById("url"+sels[i].value).value+",";
		}
	}
	if(sel!=null&&sel.length>0){
		 var hashvo = new ParameterSet();
		 var r5000=document.getElementsByName("r5000")[0].value;
		 hashvo.setValue("id",r5000);
		  var request=new Request({method:'post',asynchronous:false,onSuccess:isSearch,functionId:'202003006101'},hashvo);
		  function isSearch(outparamters){
				if(outparamters){
				   var temp=outparamters.getValue("check")
				   if("yes" == temp){
					   if(sel!=null&&confirm('该课程下存在学员，若删除课件，该课件的学习进度会清空。确认要删除吗？')){
							hashvo=new ParameterSet();
							hashvo.setValue("sel",sel.substring(0,sel.length-1));
							hashvo.setValue("urlsb",getEncodeStr(urlsb.substring(0,urlsb.length-1)));
							var a_code=document.getElementsByName("a_code")[0].value;
							hashvo.setValue("a_code", a_code);
							hashvo.setValue("filepath","<%=SafeCode.encode(tmppath) %>");
							var request=new Request({method:'post',asynchronous:false,onSuccess:showfile,functionId:'2020030062'},hashvo);
						}
				   }else if(!temp || "no" == temp){
					   if(sel!=null&&confirm('确认要删除吗？')){
							hashvo=new ParameterSet();
							hashvo.setValue("sel",sel.substring(0,sel.length-1));
							hashvo.setValue("urlsb",getEncodeStr(urlsb.substring(0,urlsb.length-1)));
							var a_code=document.getElementsByName("a_code")[0].value;
							hashvo.setValue("a_code", a_code);
							hashvo.setValue("filepath","<%=SafeCode.encode(tmppath) %>");
							var request=new Request({method:'post',asynchronous:false,onSuccess:showfile,functionId:'2020030062'},hashvo);
						}
				   }else
					   alert(temp);
				}
		  }
	}else{
		alert("请选择要删除的记录！");
	}
}
function showfile(outparamters){
	if(outparamters!=null){
		var a_code=document.getElementsByName("a_code")[0].value;
		var r5000=document.getElementsByName("r5000")[0].value;
		coursewareForm.action = "/train/resource/courseware.do?b_query=link&a_code=" + a_code+"&id="+r5000;
		coursewareForm.submit();
	}
}

function outContent(id,field){
	var hashvo=new ParameterSet();
	hashvo.setValue("id",id);
	hashvo.setValue("field",field);
	var request=new Request({method:'post',asynchronous:false,onSuccess:viewContent,functionId:'2020020173'},hashvo);
}
function viewContent(outparamters){
	if(outparamters){
		var content=outparamters.getValue("content");
		config.FontSize='10pt';//hint提示信息中的字体大小
		Tip(getDecodeStr(content),STICKY,true);
	}
}

function liulan(strId) {
	var a_code=document.getElementsByName("a_code")[0].value;
	var r5000=document.getElementsByName("r5000")[0].value;
	var url = "/train/resource/mylessons/learncoursebyextjs.jsp?opt=sss&classes=" + a_code
	+ "&lesson=" + r5000 + "&course=" + strId + "&show=1";
	window.open(url,'','fullscreen=no,left=0,top=0,width='+ (screen.availWidth - 10) +',height='+ (screen.availHeight-50) +',scrollbars,resizable=no,toolbar=no,location=no,status=no,menubar=no');
}
function liulan1(strUrl) {
	if(strUrl)
		window.open(getDecodeStr("<%=pathurl%>")+getDecodeStr(strUrl));
}
function liulan2(strUrl,r5100) {
	if (strUrl) {
		window.open("/train/resouce/lessons.do?b_query=link&isLearn=0&classes=<bean:write name="coursewareForm" property="a_code"/>&r5000=<bean:write name='coursewareForm' property='r5000'/>&r5100="+r5100+"&src="+strUrl, "courseLiulan");
	}
}
function liulan6(strUrl) {
	if(strUrl)
		window.open("//"+strUrl.replace('http://',''));
}
</script>
