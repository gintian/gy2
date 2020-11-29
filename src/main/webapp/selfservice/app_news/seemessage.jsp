<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page errorPage="error.jsp"%>
<%@ page import="javax.servlet.ServletException"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="javascript" src="/js/validateDate.js"></script>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script language="javascript">
    	var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	var ViewProperties=new ParameterSet();
</script>
<script language="javascript">
	function goback()
	{
		var type = '${appNewsForm.type}';
		if(type=="receive"){
			appNewsForm.action="/selfservice/app_news/appmessage2.do?b_query2=link&type=receive&isdraft=1&news_id=";
			appNewsForm.submit();
		}
		if(type=="select"){
			appNewsForm.action="/selfservice/app_news/appmessage.do?b_query=link&type=select&isdraft=1";
			appNewsForm.submit();
		}
		
	}
</script>
<script type="text/javascript" src="/fckeditor/fckeditor.js"></script>
<html:form action="/selfservice/app_news/appmessage" enctype="multipart/form-data">
<br>
<br>
	<table width="700" border="0" cellpadding="0" cellspacing="0" align="center">
		<br>
		<tr height="20">
			<!--  <td width=10 valign="top" class="tableft"></td>
			<td width=130 align="center" class="tabcenter">
				&nbsp;
				<bean:message key="self.app_news.inceptnews" />
				&nbsp;
			</td>
			<td width=10 valign="top" class="tabright"></td>
			<td valign="top" width="780" class="tabremain"></td>-->
			<td  align="center" class="TableRow">
				&nbsp;
				<bean:message key="self.app_news.inceptnews" />
				&nbsp;
			</td>
		</tr>
		<tr>
			<td  class="framestyle9">
				<br>
				<table border="0" cellpmoding="0" cellspacing="0" class="DetailTable" cellpadding="0" width="600">
					<tr>
						<td height="10"></td>
					</tr>
					<tr class="list3">
						<TD align="right"  >
							<bean:message key="slef.app_news.messagename" />&nbsp;:&nbsp;&nbsp;
						</TD>
						<TD>
							<bean:write name="appNewsForm" property="title" filter="false"/>
						</TD>
					</tr>
					<tr>
						<td height="15"></td>
					</tr>
					<tr class="list3">
						<td align="right" nowrap valign="top" width="20%">
							<bean:message key="conlumn.board.content" />
							:&nbsp;&nbsp;
						</td>

						<td align="left"  nowrap>
							<bean:write name="appNewsForm" property="constant" filter="false"/>
						</td>
					</tr>
					<tr>
						<td height="15"></td>
					</tr>
					<logic:notEqual name="appNewsForm" property="affixindex" value="1">
						<bean:write name="appNewsForm" property="affixstr" filter="false"/>
					</logic:notEqual>
					
				</table>
			</td>
		</tr>

		<tr class="list3">
			<td align="center" style="height:35px;">				
				<INPUT type="button" value="<bean:message key="button.return" />" Class="mybutton" onclick="goback()">
			</td>
		</tr>
		
	</table>
</html:form>
<script language="javascript">
   var dropDownDate=createDropDown("dropDownDate");
   var __t=dropDownDate;
   __t.type="date";
   __t.tag="";
   _array_dropdown[_array_dropdown.length]=__t;
   initDropDown(__t);
</script>
<script language="javascript">
    var dropdownCode=createDropDown("dropdownCode");
    var __t=dropdownCode;
    __t.type="custom";
    __t.path="/general/muster/select_code_tree.do";
    __t.readFields="codeitemid";
    //__t.writeFields="xxxx";
    __t.cachable=true;__t.tag="";
    _array_dropdown[_array_dropdown.length]=__t;
    initDropDown(__t);
</script>
<script language="javascript">
  initDocument();
</script>
