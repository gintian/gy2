<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
	UserView userView = (UserView) session
			.getAttribute(WebConstant.userView);
	String bosflag = "";
	if (userView != null) {
		bosflag = userView.getBosflag();
	}
%>
<link href="/css/css1.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="/js/validate.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="JavaScript">

function showSetList(outparamters)
	{
		var setlist=outparamters.getValue("setlist");
		AjaxBind.bind(staticFieldForm.setlist,setlist);
		if($('setlist').options.length>0)
		{
		  $('setlist').options[0].selected=true;
		  try{
    	   if (navigator.appName.indexOf("Microsoft")!= -1) { 
				$('setlist').fireEvent('onchange');
		         //ie  
		    }else{ 
		        $('setlist').onchange();
		    }  
			}catch(e){
			}
		}
	}
	function showFieldList(outparamters)
	{
		var fieldlist=outparamters.getValue("fieldlist");
		AjaxBind.bind(staticFieldForm.left_fields,fieldlist);
	}
				
	function searchFieldList()
	{
	   var tablename=$F('setlist');
	   var In_paramters="tablename="+tablename;
   	   var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showFieldList,functionId:'05301010002'});
	}
	function MusterInitData(infor)
	{
	   var pars="base="+infor;
   	   var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:showSetList,functionId:'05301010001'});
	}
</script>
<hrms:themes />
<%
	if ("hl".equalsIgnoreCase(bosflag)) {
%>
<style>
.selectstaticfieldTable {
	margin-top: 10px;
}
</style>
<%
	}
%>
<html:form action="/general/static/select_static_fields">
	<table width="700px" align="center" border="0" cellpadding="0"
		cellspacing="0" class="selectstaticfieldTable">
		<tr>
			<td valign="top">
				<table width="100%" border="0" cellspacing="0" align="center"
					cellpadding="0" class="RecordRow"
					style="border-collapse: separate;border-right: none;">
					<thead>
						<tr>
							<td align="left" class="TableRow" style="border-top: none;border-left: none;" nowrap colspan="3">
								<bean:message key="static.select" />
							</td>
						</tr>
					</thead>
					<!-- modify by xiaoyun 员工统计-通用统计-ie9样式修改 2014-8-12  start -->
					<tr>
						<td align="left" width="48%"><bean:message key="selfservice.query.queryfield" /></td>
						<td width="4%" align="center"></td>
						<td align="left" width="48%" style="border-right-style: solid;border-right-width: 1pt;"><bean:message key="static.ytarget" /></td>
					</tr>
					<tr>
						<td height="250px">
							<table width="100%">
								<tr><td height="10%" align="left" valign="top">
									<select name="setlist" size="1" style="width: 100%"
														onchange="searchFieldList();">
														<option value="1111">
															#
														</option>
									</select>
								</td></tr>
								<tr><td height="225px" align="left">
									<select name="left_fields" multiple="multiple"
											ondblclick="additem('left_fields','right_fields');"
											style="height:100%; width: 100%; font-size: 9pt;">
									</select>
								</td></tr>
							</table>
						</td>
						<td>
							<table>
								<tr><td align="center">
									<html:button styleClass="mybutton" property="b_addfield"
													onclick="additem('left_fields','right_fields');">
													<bean:message key="button.setfield.addfield" />
									</html:button>
								</td></tr>
								<tr><td height="30px"></td></tr>
								<tr><td align="center">
									<html:button styleClass="mybutton" property="b_delfield"
													onclick="removeitem('right_fields');">
													<bean:message key="button.setfield.delfield" />
									</html:button>
								</td></tr>
							</table>
						</td>
						<td height="250px" style="border-right-style: solid;border-right-width: 1px;">
						<hrms:optioncollection name="staticFieldForm" property="selectedlist" collection="selectedlist"/> 
							<html:select property="right_fields" multiple="multiple" size="10"
								ondblclick="removeitem('right_fields');"
								style="height:100%;width:100%;font-size:9pt;">
								<html:options collection="selectedlist"  property="dataValue" labelProperty="dataName"/>
							</html:select>
						</td>
					</tr>
					<!-- modify by xiaoyun 员工统计-通用统计-ie9样式修改 2014-8-12  end -->					
					<tr>
						<td align="center" class="RecordRow" nowrap colspan="3"
							style="height: 35;border-left: none;border-bottom: none;">
							<%-- <hrms:submit styleClass="mybutton" property="b_next"
								onclick="setselectitem('right_fields');">
								<bean:message key="static.next" />
							</hrms:submit> --%>
							<input type='button' value='<bean:message key="static.next" />' class="mybutton" onclick="setselectitem('right_fields');submitForm();">
							<hrms:tipwizardbutton flag="emp" target="il_body"
								formname="staticFieldForm" />
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</html:form>
<script language="javascript">
   MusterInitData('<bean:write name="staticFieldForm"  property="infor_Flag"/>');
   
   
   function submitForm(){//add by xiegh on date 20180503 bug36850 submit标签提交 有问题 右边指标为空时 会读取缓存数据
		var right_fields = document.getElementsByName('right_fields');
		if(right_fields[0].length == 0){
			alert('未定义查询指标！');
			return;
		}
		staticFieldForm.action="/general/static/select_static_fields.do?b_next=link";
		staticFieldForm.submit();
	}
	if(!getBrowseVersion() || getBrowseVersion() == 10){//兼容非IE浏览器 样式  wangb 20180206  bug 34609  and 处理ie11 不加兼容视图样式  wangb 20190307
		var table = document.getElementsByName('staticFieldForm')[0].getElementsByClassName('RecordRow')[0];
		table.style.borderCollapse='';
		table.style.borderRight='';
		var trs = table.children[1].children;
		trs[0].children[2].style.borderRightStyle='';		
		trs[1].children[2].style.borderRightStyle='';		
		trs[2].children[0].style.borderRightColor='';
		var right_fields = document.getElementsByName('right_fields')[0];
		right_fields.style.width="99%";
		right_fields.style.height="98%";		
	}
</script>