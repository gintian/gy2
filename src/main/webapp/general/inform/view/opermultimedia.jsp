<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page
	import="com.hjsj.hrms.actionform.general.inform.MInformForm,java.util.*"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%
	// 在标题栏显示当前用户和日期 2004-5-10 
	String userName = null;
	String css_url = "/css/css1.css";
	UserView userView = (UserView) session
			.getAttribute(WebConstant.userView);
	if (userView != null) {
		css_url = userView.getCssurl();
		if (css_url == null || css_url.equals(""))
			css_url = "/css/css1.css";
	}
	MInformForm mInformForm = (MInformForm) session
			.getAttribute("mInformForm");
	String inputchinfor = mInformForm.getInputchinfor();
	String approveflag = mInformForm.getApproveflag();
	ArrayList li = (ArrayList) mInformForm.getMultimedialist();
	boolean fK = true;
	if (li.size() > 0) {
		fK = false;
	}

	String code = (String) session.getAttribute("code");
%>

<%
	int i = 0;
%>
<script language="javascript">
function bdelete()
{
	var isCorrect=false;
       var len=document.mInformForm.elements.length;
       var i;
       for (i=0;i<len;i++)
       {//zhaogd 删除时没有选中进行提示，这里原来判断缺少条件只有：document.mInformForm.elements[i].type=="checkbox"
          if(document.mInformForm.elements[i].type=="checkbox"&&document.mInformForm.elements[i].name!='selbox')
          {
              if(document.mInformForm.elements[i].checked==true)
              {
                isCorrect=true;
                break;
              }
          }
      }   
      if(!isCorrect)
      {
         alert("请选择项目!");
         return false;
      }   
	if(ifdel())
	{
		document.mInformForm.action="/general/inform/emp/view/deletemultimedia.do?b_query=link";
		document.mInformForm.submit(); 
	}
}
function edit(i9999,filetitle)
{
	document.getElementById("i9999").value=i9999;
	document.getElementById("filetitle").value=filetitle;
	document.mInformForm.action="/general/inform/emp/view/opermultimedia.do?br_update=link.do?b_query=link";
	document.mInformForm.submit(); 
}
function init()
{
	document.getElementsByName("multimediaflag")[0].value="${mInformForm.multimediaflag}";  
}

function approve(state) {
	var isCorrect=false;
 	var len=document.mInformForm.elements.length;
 	var i;
 	for (i=0;i<len;i++)
 	{//zhaogd 删除时没有选中进行提示，这里原来判断缺少条件只有：document.mInformForm.elements[i].type=="checkbox"
	   	 if(document.mInformForm.elements[i].type=="checkbox"&&document.mInformForm.elements[i].name!='selbox')
	    	{
	        if(document.mInformForm.elements[i].checked==true)
	        {
	          isCorrect=true;
	          break;
	        }
	    }
	}   
	if(!isCorrect)
	{
	   alert("请选择项目!");
	   return false;
	}
	app = "";
	if (state == "2") {
		app = "您确定要退回所选项目吗？";
	} else {
		app = "您确定要批准所选项目吗？";
	}
	if (confirm(app)) {
		document.mInformForm.action="/general/inform/emp/view/opermultimedia.do?b_approve=link&state=" + state;
		document.mInformForm.submit();
	}
	
}
//add by wangchaoqun on 2014-9-12 begin
function addFileWrong(){
    var fileHasPro = document.getElementById("fileHasPro").value;
    if(fileHasPro=='true'){
        alert("文件存在问题，新增文件失败！");
    }
    document.getElementById("fileHasPro").value='false';
}
//add by wangchaoqun on 2014-9-12 end
</script>
<hrms:themes/>
<body onload="addFileWrong()">
<html:form action="/general/inform/emp/view/opermultimedia">
	<html:hidden name="mInformForm" property="i9999" styleId="i9999"/>
	<html:hidden name="mInformForm" property="filetitle" styleId="filetitle"/>
	<html:hidden name="mInformForm" property="multimediaflag" styleId="multimediaflag"/>
	<html:hidden name="mInformForm" property="isvisible" styleId="isvisible"/>
	<html:hidden name="mInformForm" property="fileHasPro" styleId="fileHasPro"/>
	<table width="100%" border="0" cellspacing="0" align="center"
		cellpadding="0">
		<tr>
			<td align="left" nowrap>
				(
				<logic:equal value="6" name="mInformForm" property="kind">
					<bean:message key="label.title.org" />: <bean:write
						name="mInformForm" property="unit" filter="true" />&nbsp;
        		<bean:message key="label.title.dept" />: <bean:write
						name="mInformForm" property="pos" filter="true" />&nbsp;
      			<bean:message key="label.title.name" />: <bean:write
						name="mInformForm" property="a0101" filter="true" />&nbsp;
			</logic:equal>
				<logic:notEqual value="6" name="mInformForm" property="kind">
					<logic:equal value="0" name="mInformForm" property="kind">
						<bean:message key="e01a1.label" />: <bean:write name="mInformForm"
							property="pos" filter="true" />&nbsp;
				</logic:equal>
					<logic:notEqual value="0" name="mInformForm" property="kind">

						<logic:equal value="9" name="mInformForm" property="kind">
							基准岗位: <bean:write
								name="mInformForm" property="pos" filter="true" />&nbsp;
				    </logic:equal>

						<logic:notEqual value="9" name="mInformForm" property="kind">
							<bean:message key="label.title.org" />: <bean:write
								name="mInformForm" property="unit" filter="true" />&nbsp;  
				    </logic:notEqual>

					</logic:notEqual>
				</logic:notEqual>
				)

			</td>
		</tr>
	</table>

	<table width="100%" border="0" cellspacing="0" align="center"
		cellpadding="0" class="ListTable">

		<thead>
			<tr>
				<td align="center" class="TableRow" width="30" nowrap>
					<input type=checkbox name=selbox onclick=batch_select(this,'recordListForm.select');
						 title=<bean:message key='label.query.selectall' />
						width=15>
				</td>
				<logic:equal name="mInformForm" property="display_state" value="yes">
					<td align="center" class="TableRow" nowrap>
						<bean:message key="info.appleal.statedesc" />
						&nbsp;
					</td>
				</logic:equal>
				<td align="center" class="TableRow" nowrap>
					<bean:message key="general.mediainfo.type" />
					&nbsp;
				</td>
				<td align="center" class="TableRow" nowrap>
					<bean:message key="general.mediainfo.title" />
					&nbsp;
				</td>
				<!-- hrms:priv func_id="03050103,2606103" -->
				<td align="center" class="TableRow" width="50" nowrap>
					<bean:message key="label.edit" />
				</td>
				<!-- /hrms:priv -->
			</tr>
		</thead>
		<hrms:extenditerate id="element" name="mInformForm"
			property="recordListForm.list" indexes="indexes"
			pagination="recordListForm.pagination" pageCount="20" scope="session">
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

				<td align="center" class="RecordRow" width="30" nowrap>
					<logic:equal value="6" name="mInformForm" property="kind">
						<logic:equal name="element" property="state" value="1">
							<hrms:checkmultibox name="mInformForm"
								property="recordListForm.select" value="true" indexes="indexes" />
						</logic:equal>
					</logic:equal>
					<logic:notEqual value="6" name="mInformForm" property="kind">
						<hrms:checkmultibox name="mInformForm"
							property="recordListForm.select" value="true" indexes="indexes" />
					</logic:notEqual>
				</td>

				<logic:equal name="mInformForm" property="display_state" value="yes">
					<td align="center" class="RecordRow" nowrap>
						<logic:equal name="element" property="state" value="0">
							<bean:message key="info.appleal.state0" />&nbsp;
	               </logic:equal>
						<logic:equal name="element" property="state" value="1">
							<bean:message key="info.appleal.state1" />&nbsp;
	               </logic:equal>
						<logic:equal name="element" property="state" value="2">
							<bean:message key="button.rejeect2" />&nbsp;
	               </logic:equal>
						<logic:equal name="element" property="state" value="3">
							<bean:message key="info.appleal.state3" />&nbsp;
	               </logic:equal>
						<logic:notEqual name="element" property="state" value="0">
							<logic:notEqual name="element" property="state" value="1">
								<logic:notEqual name="element" property="state" value="2">
									<logic:notEqual name="element" property="state" value="3">
										<bean:message key="info.appleal.state0" />&nbsp;
	                      </logic:notEqual>
								</logic:notEqual>
							</logic:notEqual>
						</logic:notEqual>
					</td>
				</logic:equal>



				<td align="left" class="RecordRow" nowrap>
					<bean:write name="element" property="sortname" filter="true" />
					&nbsp;
				</td>

				<td align="left" class="RecordRow" nowrap>
					<a
					    href="/servlet/vfsservlet?fileid=<bean:write name="element" property="fileid" filter="true"/>"
						target="_blank">
						<bean:write name="element" property="title" filter="true" />
					</a>
				</td>

				<td align="center" class="RecordRow" nowrap width="50">
				    <logic:equal name="mInformForm" property="is_yewu" value="all">
				            <logic:equal value="6" name="mInformForm" property="kind">
								<hrms:priv func_id="2606503,01030103">
									<a
										href="javascript:edit('<bean:write  name="element" property="i9999" filter="true"/>','<bean:write  name="element" property="title" filter="true"/>')"><img
											src="/images/edit.gif" border=0>
									</a>
								</hrms:priv>
							</logic:equal>
							<logic:notEqual value="6" name="mInformForm" property="kind">
							<logic:equal value="0" name="mInformForm" property="kind">
								<hrms:priv func_id="2506403,07090113">
									<a
										href="javascript:edit('<bean:write  name="element" property="i9999" filter="true"/>','<bean:write  name="element" property="title" filter="true"/>')"><img
											src="/images/edit.gif" border=0>
									</a>
								</hrms:priv>
							</logic:equal>
							<logic:notEqual value="0" name="mInformForm" property="kind">
								<hrms:priv func_id="2306603,050104010203">
									<a
										href="javascript:edit('<bean:write  name="element" property="i9999" filter="true"/>','<bean:write  name="element" property="title" filter="true"/>')"><img
											src="/images/edit.gif" border=0>
									</a>
								</hrms:priv>
							</logic:notEqual>
						</logic:notEqual>
				    </logic:equal>
				    <logic:notEqual name="mInformForm" property="is_yewu" value="all">
				         <logic:equal name="mInformForm" property="is_yewu" value="yes">
							<logic:equal value="6" name="mInformForm" property="kind">
								<hrms:priv func_id="2606503">
									<a
										href="javascript:edit('<bean:write  name="element" property="i9999" filter="true"/>','<bean:write  name="element" property="title" filter="true"/>')"><img
											src="/images/edit.gif" border=0>
									</a>
								</hrms:priv>
							</logic:equal>
							<logic:notEqual value="6" name="mInformForm" property="kind">
								<logic:equal value="0" name="mInformForm" property="kind">
									<hrms:priv func_id="2506403">
										<a
											href="javascript:edit('<bean:write  name="element" property="i9999" filter="true"/>','<bean:write  name="element" property="title" filter="true"/>')"><img
												src="/images/edit.gif" border=0>
										</a>
									</hrms:priv>
								</logic:equal>
								<logic:notEqual value="0" name="mInformForm" property="kind">
									<hrms:priv func_id="2306603">
										<a
											href="javascript:edit('<bean:write  name="element" property="i9999" filter="true"/>','<bean:write  name="element" property="title" filter="true"/>')"><img
												src="/images/edit.gif" border=0>
										</a>
									</hrms:priv>
								</logic:notEqual>
							</logic:notEqual>
						</logic:equal>
						<logic:notEqual name="mInformForm" property="is_yewu" value="yes">
							<logic:equal value="6" name="mInformForm" property="kind">
								<hrms:priv func_id="01030103">
									<a
										href="javascript:edit('<bean:write  name="element" property="i9999" filter="true"/>','<bean:write  name="element" property="title" filter="true"/>')"><img
											src="/images/edit.gif" border=0>
									</a>
								</hrms:priv>
							</logic:equal>
							<logic:notEqual value="6" name="mInformForm" property="kind">
								<logic:equal value="0" name="mInformForm" property="kind">
									<hrms:priv func_id="07090113">
										<a
											href="javascript:edit('<bean:write  name="element" property="i9999" filter="true"/>','<bean:write  name="element" property="title" filter="true"/>')"><img
												src="/images/edit.gif" border=0>
										</a>
									</hrms:priv>
								</logic:equal>
								<logic:notEqual value="0" name="mInformForm" property="kind">
									<hrms:priv func_id="050104010203">
										<a
											href="javascript:edit('<bean:write  name="element" property="i9999" filter="true"/>','<bean:write  name="element" property="title" filter="true"/>')"><img
												src="/images/edit.gif" border=0>
										</a>
									</hrms:priv>
								</logic:notEqual>
							</logic:notEqual>
						</logic:notEqual>
				    </logic:notEqual>
				    
				    
				</td>

			</tr>
		</hrms:extenditerate>

	</table>
	<table width="100%" align="center" class="RecordRowP">
		<tr>
			<td valign="bottom" class="tdFontcolor">
				<bean:message key="label.page.serial" />
				<bean:write name="mInformForm"
					property="recordListForm.pagination.current" filter="true" />
				<bean:message key="label.page.sum" />
				<bean:write name="mInformForm"
					property="recordListForm.pagination.count" filter="true" />
				<bean:message key="label.page.row" />
				<bean:write name="mInformForm"
					property="recordListForm.pagination.pages" filter="true" />
				<bean:message key="label.page.page" />
			</td>
			<td align="right" nowrap class="tdFontcolor">
				<p align="right">
					<hrms:paginationlink name="mInformForm"
						property="recordListForm.pagination" nameId="recordListForm">
					</hrms:paginationlink>
			</td>
		</tr>
	</table>
	<logic:equal value="9" name="mInformForm" property="kind">
		<table width="88%" align="left">
			<tr>
				<td align="left">
					<logic:equal value="K" name="mInformForm" property="multimediaflag">
						<%
							if (fK) {
						%>
						<hrms:submit styleClass="mybutton" property="b_add">
							<bean:message key="button.insert" />
						</hrms:submit>
						<%
							}
						%>
					</logic:equal>
					<logic:notEqual value="K" name="mInformForm"
						property="multimediaflag">
						<hrms:submit styleClass="mybutton" property="b_add">
							<bean:message key="button.insert" />
						</hrms:submit>
					</logic:notEqual>

					<html:button styleClass="mybutton" property="b_delete"
						onclick="bdelete()">
						<bean:message key="menu.gz.delete" />
					</html:button>  
					
					
					<%
					   if(code.indexOf("HH")!=-1){
					%>
					 <input type="button" name="returnbutton"
							value="<bean:message key="button.return"/>" class="mybutton"
							onclick="exeReturn('/dtgh/party/searchpartybusinesslist.do?b_query=link&a_code=<%=code.substring(2)%>','mil_body')">
					<%
					   }
					%>
				</td>
			</tr>
		</table>
	</logic:equal>
	<logic:notEqual value="9" name="mInformForm" property="kind">
		<table width="88%" align="left">
			<tr>
				<td align="left">
				    <logic:equal name="mInformForm" property="is_yewu" value="all">
				           <logic:equal name="mInformForm" property="check_main" value="yes">
							<logic:equal value="6" name="mInformForm" property="kind">
								<logic:equal value="1" name="mInformForm" property="approveflag">
									<logic:equal value="1" name="mInformForm"
										property="inputchinfor">
										<hrms:priv func_id="2606505,03050105">
											<input type="button" name="pi" class="mybutton"
												value="<bean:message key="button.approve"/>"
												onclick="approve('3')" />
										</hrms:priv>
										<hrms:priv func_id="2606504,03050104">
											<input type="button" name="bo" class="mybutton"
												value="<bean:message key="button.rejeect2"/>"
												onclick="approve('2')" />
										</hrms:priv>
									</logic:equal>
								</logic:equal>
								<hrms:priv func_id="2606501,01030101">
									<hrms:submit styleClass="mybutton" property="b_add">
										<bean:message key="button.insert" />
									</hrms:submit>
								</hrms:priv>
								<hrms:priv func_id="2606502,01030102">
									<html:button styleClass="mybutton" property="b_delete"
										onclick="bdelete()">
										<bean:message key="menu.gz.delete" />
									</html:button>
								</hrms:priv>

							</logic:equal>
							<logic:notEqual value="6" name="mInformForm" property="kind">
								<logic:equal value="0" name="mInformForm" property="kind">
									<hrms:priv func_id="2506401,07090111">
										<!-- //xuj 2010-4-20 ，k代号已成为多媒体岗位说明书固定分类,但此分类中只能上传一条记录 -->
										<logic:equal value="K" name="mInformForm"
											property="multimediaflag">
											<%
												if (fK) {
											%>
											<hrms:submit styleClass="mybutton" property="b_add">
												<bean:message key="button.insert" />
											</hrms:submit>
											<%
												}
											%>
										</logic:equal>
										<logic:notEqual value="K" name="mInformForm"
											property="multimediaflag">
											<logic:notEqual value="1" name="mInformForm" property="buttonflag">
												<hrms:submit styleClass="mybutton" property="b_add">
													<bean:message key="button.insert" />
												</hrms:submit>
											</logic:notEqual>
										</logic:notEqual>
									</hrms:priv>
									<hrms:priv func_id="2506402,07090112">
										<html:button styleClass="mybutton" property="b_delete"
											onclick="bdelete()">
											<bean:message key="menu.gz.delete" />
										</html:button>
									</hrms:priv>
								</logic:equal>
								<logic:notEqual value="0" name="mInformForm" property="kind">
									<hrms:priv func_id="2306601,050104010201">
										<hrms:submit styleClass="mybutton" property="b_add">
											<bean:message key="button.insert" />
										</hrms:submit>
									</hrms:priv>
									<hrms:priv func_id="2306602,050104010202">
										<html:button styleClass="mybutton" property="b_delete"
											onclick="bdelete()">
											<bean:message key="menu.gz.delete" />
										</html:button>
									</hrms:priv>
								</logic:notEqual>
							</logic:notEqual>
						</logic:equal>
						<logic:notEqual value="yes" name="mInformForm"
							property="check_main">
							<logic:equal value="0" name="mInformForm" property="kind">
								<hrms:priv func_id="2506401">
								   <logic:notEqual value="1" name="mInformForm" property="buttonflag">
									<hrms:submit styleClass="mybutton" property="b_add">
										<bean:message key="button.insert" />
									</hrms:submit>
								   </logic:notEqual>
								</hrms:priv>
								<hrms:priv func_id="2506402">
									<html:button styleClass="mybutton" property="b_delete"
										onclick="bdelete()">
										<bean:message key="menu.gz.delete" />
									</html:button>
								</hrms:priv>
							</logic:equal>
						</logic:notEqual>
				    </logic:equal>
				    <logic:notEqual name="mInformForm" property="is_yewu" value="all">
				           <logic:equal name="mInformForm" property="is_yewu" value="yes">

								<logic:equal name="mInformForm" property="check_main" value="yes">
									<logic:equal value="6" name="mInformForm" property="kind">
										<logic:equal value="1" name="mInformForm" property="approveflag">
											<logic:equal value="1" name="mInformForm"
												property="inputchinfor">
												<hrms:priv func_id="2606505,03050105">
													<input type="button" name="pi" class="mybutton"
														value="<bean:message key="button.approve"/>"
														onclick="approve('3')" />
												</hrms:priv>
												<hrms:priv func_id="2606504,03050104">
													<input type="button" name="bo" class="mybutton"
														value="<bean:message key="button.rejeect2"/>"
														onclick="approve('2')" />
												</hrms:priv>
											</logic:equal>
										</logic:equal>
										<hrms:priv func_id="2606501,01030101">
											<hrms:submit styleClass="mybutton" property="b_add">
												<bean:message key="button.insert" />
											</hrms:submit>
										</hrms:priv>
		
									</logic:equal>
									<logic:notEqual value="6" name="mInformForm" property="kind">
										<logic:equal value="0" name="mInformForm" property="kind">
											<hrms:priv func_id="2506401">
												<!-- //xuj 2010-4-20 ，k代号已成为多媒体岗位说明书固定分类,但此分类中只能上传一条记录 -->
												<logic:equal value="K" name="mInformForm"
													property="multimediaflag">
													<%
														if (fK) {
													%>
													<hrms:submit styleClass="mybutton" property="b_add">
														<bean:message key="button.insert" />
													</hrms:submit>
													<%
														}
													%>
												</logic:equal>
												<logic:notEqual value="K" name="mInformForm"
													property="multimediaflag">
													<logic:notEqual value="1" name="mInformForm" property="buttonflag">
														<hrms:submit styleClass="mybutton" property="b_add">
															<bean:message key="button.insert" />
														</hrms:submit>
													</logic:notEqual>
												</logic:notEqual>
											</hrms:priv>
										</logic:equal>
										<logic:notEqual value="0" name="mInformForm" property="kind">
											<hrms:priv func_id="2306601">
												<hrms:submit styleClass="mybutton" property="b_add">
													<bean:message key="button.insert" />
												</hrms:submit>
											</hrms:priv>
										</logic:notEqual>
									</logic:notEqual>
								</logic:equal>
								<logic:notEqual value="yes" name="mInformForm"
									property="check_main">
									<logic:equal value="0" name="mInformForm" property="kind">
										<hrms:priv func_id="2506401">
										   <logic:notEqual value="1" name="mInformForm" property="buttonflag">
											<hrms:submit styleClass="mybutton" property="b_add">
												<bean:message key="button.insert" />
											</hrms:submit>
										   </logic:notEqual>
										</hrms:priv>
									</logic:equal>
								</logic:notEqual>
		
							</logic:equal>
							<logic:notEqual name="mInformForm" property="is_yewu" value="yes">
		
								<logic:equal name="mInformForm" property="check_main" value="yes">
									<logic:equal value="6" name="mInformForm" property="kind">
										<logic:equal value="1" name="mInformForm" property="approveflag">
											<logic:equal value="1" name="mInformForm"
												property="inputchinfor">
												<hrms:priv func_id="03050105">
													<input type="button" name="pi" class="mybutton"
														value="<bean:message key="button.approve"/>"
														onclick="approve('3')" />
												</hrms:priv>
												<hrms:priv func_id="03050104">
													<input type="button" name="bo" class="mybutton"
														value="<bean:message key="button.rejeect2"/>"
														onclick="approve('2')" />
												</hrms:priv>
											</logic:equal>
										</logic:equal>
										<hrms:priv func_id="01030101">
											<hrms:submit styleClass="mybutton" property="b_add">
												<bean:message key="button.insert" />
											</hrms:submit>
										</hrms:priv>
		
									</logic:equal>
									<logic:notEqual value="6" name="mInformForm" property="kind">
										<logic:equal value="0" name="mInformForm" property="kind">
											<hrms:priv func_id="07090111">
												<hrms:submit styleClass="mybutton" property="b_add">
													<bean:message key="button.insert" />
												</hrms:submit>
											</hrms:priv>
										</logic:equal>
										<logic:notEqual value="0" name="mInformForm" property="kind">
											<hrms:priv func_id="050104010201">
												<hrms:submit styleClass="mybutton" property="b_add">
													<bean:message key="button.insert" />
												</hrms:submit>
											</hrms:priv>
										</logic:notEqual>
									</logic:notEqual>
								</logic:equal>
		
							</logic:notEqual>
		
		
							<logic:equal name="mInformForm" property="is_yewu" value="yes">
		
								<logic:equal name="mInformForm" property="check_main" value="yes">
									<logic:equal value="6" name="mInformForm" property="kind">
										<hrms:priv func_id="2606502,01030102">
											<html:button styleClass="mybutton" property="b_delete"
												onclick="bdelete()">
												<bean:message key="menu.gz.delete" />
											</html:button>
										</hrms:priv>
									</logic:equal>
									<logic:notEqual value="6" name="mInformForm" property="kind">
										<logic:equal value="0" name="mInformForm" property="kind">
											<hrms:priv func_id="2506402">
												<html:button styleClass="mybutton" property="b_delete"
													onclick="bdelete()">
													<bean:message key="menu.gz.delete" />
												</html:button>
											</hrms:priv>
										</logic:equal>
										<logic:notEqual value="0" name="mInformForm" property="kind">
											<hrms:priv func_id="2306602">
												<html:button styleClass="mybutton" property="b_delete"
													onclick="bdelete()">
													<bean:message key="menu.gz.delete" />
												</html:button>
											</hrms:priv>
										</logic:notEqual>
									</logic:notEqual>
		
								</logic:equal>
								<logic:notEqual value="yes" name="mInformForm"
									property="check_main">
									<logic:equal value="0" name="mInformForm" property="kind">
										<hrms:priv func_id="2506402">
											<html:button styleClass="mybutton" property="b_delete"
												onclick="bdelete()">
												<bean:message key="menu.gz.delete" />
											</html:button>
										</hrms:priv>
									</logic:equal>
								</logic:notEqual>
							</logic:equal>
							<logic:notEqual name="mInformForm" property="is_yewu" value="yes">
		
								<logic:equal name="mInformForm" property="check_main" value="yes">
									<logic:equal value="6" name="mInformForm" property="kind">
										<hrms:priv func_id="01030102">
											<html:button styleClass="mybutton" property="b_delete"
												onclick="bdelete()">
												<bean:message key="menu.gz.delete" />
											</html:button>
										</hrms:priv>
									</logic:equal>
									<logic:notEqual value="6" name="mInformForm" property="kind">
										<logic:equal value="0" name="mInformForm" property="kind">
											<hrms:priv func_id="07090112">
												<html:button styleClass="mybutton" property="b_delete"
													onclick="bdelete()">
													<bean:message key="menu.gz.delete" />
												</html:button>
											</hrms:priv>
										</logic:equal>
										<logic:notEqual value="0" name="mInformForm" property="kind">
											<hrms:priv func_id="050104010202">
												<html:button styleClass="mybutton" property="b_delete"
													onclick="bdelete()">
													<bean:message key="menu.gz.delete" />
												</html:button>
											</hrms:priv>
										</logic:notEqual>
									</logic:notEqual>
								</logic:equal>
		
							</logic:notEqual>
				    </logic:notEqual>
				    
				    


					<logic:equal value="0" name="mInformForm" property="isvisible">
						<html:button styleClass="mybutton" property="b_return"
							onclick="parent.parent.window.close();">
							<bean:message key="button.close" />
						</html:button>
					</logic:equal>
					<logic:equal value="0" name="mInformForm" property="isself">
						<%
							if (code != null) {
											if (code.indexOf("U") != -1) {
						%>
						<input type="button" name="returnbutton"
							value="<bean:message key="button.return"/>" class="mybutton"
							onclick="exeReturn('/workbench/orginfo/searchorginfodata.do?b_query=link&code=<%=code.substring(1)%>&isroot=1','nil_body')">
						<%
							} else if (code.indexOf("@") != -1) {
						%>
						<input type="button" name="returnbutton"
							value="<bean:message key="button.return"/>" class="mybutton"
							onclick="exeReturn('/workbench/dutyinfo/searchdutyinfodata.do?b_query=link&code=<%=code.substring(1)%>','nil_body')">
						<%
							}
										}
						%>
					</logic:equal>
				</td>
			</tr>
		</table>
	</logic:notEqual>
</html:form>
</body>
<script language="javascript">
init();
  function exeReturn(returnStr,target)
{
   mInformForm.action=returnStr;
   mInformForm.target=target;
   mInformForm.submit();
}
</script>