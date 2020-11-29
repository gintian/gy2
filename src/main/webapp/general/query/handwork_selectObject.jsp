<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page
	import="com.hrms.struts.valueobject.UserView,com.hrms.frame.utility.AdminCode"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page
	import="com.hjsj.hrms.actionform.general.query.HandworkSelectForm"%>
<%@ page import="org.apache.commons.lang.StringUtils"%>
<%@ page
	import="com.hrms.struts.constant.WebConstant,com.hjsj.hrms.businessobject.sys.SysParamBo"%>

<%
	// 在标题栏显示当前用户和日期 2004-5-10 
	String css_url = "/css/css1.css";
	String bosflag = "";
	String themes = "default";
	UserView userView = (UserView) session
			.getAttribute(WebConstant.userView);
	if (userView != null) {
		css_url = userView.getCssurl();
		if (css_url == null || css_url.equals(""))
			css_url = "/css/css1.css";
		//out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");
		bosflag = userView.getBosflag();
		/*xuj added at 2014-4-18 for hcm themes*/
		themes = SysParamBo.getSysParamValue("THEMES", userView
				.getUserName());
	}
	String manager = userView.getManagePrivCodeValue();
	/**这个参数控制什么？*/
	String manageFlag = request.getParameter("manageFlag");
	/**
	 * 由先前的按人员管理范围控制改成按如下规则进行控制:
	 * 人员、单位和岗位按业务范围-操作单位-人员管理范围优先级进行控制 
	 * cmq changed at 2012-09-29
	 */
	HandworkSelectForm form = (HandworkSelectForm) request
			.getAttribute("handworkSelectForm");
	boolean bmulti = false;
	if (form.getInfor().equalsIgnoreCase("1")
			|| form.getInfor().equalsIgnoreCase("2")
			|| form.getInfor().equalsIgnoreCase("3")
			|| form.getInfor().equalsIgnoreCase("4"/*职位*/)) {
		manager = userView.getUnitIdByBusi("4");
		String[] valuearr = StringUtils.split(manager, "`");
		StringBuffer value = new StringBuffer();
		for (int i = 0; i < valuearr.length; i++) {
			if (i != 0)
				value.append("`");
			value.append(valuearr[i].substring(2));
		}
		if (valuearr.length > 1)
			bmulti = true;
		manager = value.toString();
		/**? for 单位或岗位按权限范围进行控制*/
		manageFlag = "1";
	}
	//end.

	if (manager.length() > 0) {
		//		if(bmulti)
		manager += "~组织机构~root"; // 格式: 机构编码('`'分隔)~根节点名称~根节点编号
		//		else									// 不能用getManagePrivCodeValue()，因为manager中可能是业务范围授权
		//			manager+="~"+AdminCode.getCodeName(userView.getManagePrivCode(),userView.getManagePrivCodeValue());
	}

	if (manageFlag == null)
		manageFlag = "0";
	String rootdesc = form.getRootdesc();
	session.setAttribute("rootdesc", rootdesc);
%>
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link rel="stylesheet" href="/css/xtree.css" type="text/css">
<hrms:themes></hrms:themes>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript">
  
	
	var infor,managerstr,dbpre_arr;
	infor="${handworkSelectForm.infor}";
	
/*	managerstr="${handworkSelectForm.managerstr}";
       由于树控件不够完善，所以将managerstr设为空值
*/
	managerstr="";	
	if('<%=manageFlag%>'=='1')	
		managerstr="<%=manager%>";
	dbpre_arr="${handworkSelectForm.dbpre_arr}";

	function showFieldList(outparamters)
	{
		var fieldlist=outparamters.getValue("fieldlist");	
		AjaxBind.bind(handworkSelectForm.left_fields,fieldlist);
	}

	function searchFieldList()
	{		
	   var hashvo=new ParameterSet();	
	   var codeItemID=document.getElementsByName("codeitem.value");
	   var obj=codeItemID[0];
	   var In_paramters="dbpre_arr="+dbpre_arr;  	 	  
	   hashvo.setValue("codeid",infor);
	   hashvo.setValue("codeItemID",obj.value);

   	   var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showFieldList,functionId:'0202001013'},hashvo);
	}

	function savecode()
   	{		
   		var objlist=new Array(); 	 	
   	 	for(var i=0;i<handworkSelectForm.right_fields.options.length;i++)
   	 	{
   	 		objlist.push(handworkSelectForm.right_fields.options[i].value); 			
   	 	}	   	 	
   	   	returnValue=objlist;
	        window.close();	
    	  	
   	}
   	
   	
   	function additem2(sourcebox_id,targetbox_id)
	{
	  var left_vo,right_vo,vos,i;
	  vos= document.getElementsByName(sourcebox_id);
	
	  if(vos==null)
	  	return false;
	  left_vo=vos[0];
	  vos= document.getElementsByName(targetbox_id);  
	  if(vos==null)
	  	return false;
	  right_vo=vos[0];
	  for(i=0;i<left_vo.options.length;i++)
	  {
	    if(left_vo.options[i].selected)
	    {
	    	var isExist=0;
	    	for(var j=0;j<right_vo.options.length;j++)
	    	{
	    		if(right_vo.options[j].value==left_vo.options[i].value)
	    			isExist=1;
	    	}
	    	if(isExist==0)
	    	{
		        var no = new Option();
		    	no.value=left_vo.options[i].value;
		    	no.text=left_vo.options[i].text;
		    	right_vo.options[right_vo.options.length]=no;
		    }
	    }
	  }
   	}
   	
   	
</script>
<%
	if ("hcm".equalsIgnoreCase(bosflag)) {
%>
<style>
.ListTable {
	height: expression(document.body.clientHeight-30);
	width: expression(document.body.clientWidth-10);
}
</style>
<%
	} else {
%>
<style>
.ListTable {
	margin-top: 10px;
	width: expression(document.body.clientWidth-10);
	height: expression(document.body.clientHeight-30);
}
</style>
<%
	}
%>
<base id="mybase" target="_self">
<html:form action="/general/query/handworkSelect">
	<table width="100%" align="center" border="0" cellpadding="0"
		cellspacing="0">
		<tr>
			<td valign="top" align="center">
				<table  border="0" cellspacing="0" align="center"
					cellpadding="0" class="ListTable" style="margin-left: 5px;">
					<thead>
						<tr>
							<td align="left" class="TableRow" nowrap>
								<bean:message key="lable.performance.handworkselect" />
							</td>
						</tr>
					</thead>
					<tr>
						<td width="100%" align="center" class="RecordRow" nowrap>
							<table border="0" cellspacing="0" align="center" cellpadding="0"
								style="margin-bottom: 5px; margin-top: 5px;">
								<tr>
									<td align="center" width="48%">
										<table align="center" width="100%" border="0" cellspacing="0"
											cellpadding="0">
											<tr>
												<td align="left">
													<bean:message key="lable.performance.preparePerMainBody" /><bean:message key="lable.performance.object" />
												</td>
											</tr>
											<tr>
												<td align="left">
													<input type="hidden" name="posparentcode" value="01">
													<input type="hidden" name="codeitem.value" value="01">
													<input type="text" name='codeitem.viewvalue' readonly
														onChange="searchFieldList()" class="text6" size="28" />
													<script language="JavaScript">                   
							                     		if(infor=="1")
							                     		{
							                     			document.write('<img  src="/images/code.gif" onclick=\'javascript:openInputCodeDialogOrg_handwork("@K","codeitem.viewvalue","'+managerstr+'","s");\' align="middle"  /> ');
							                     		}
							                     		else if(infor=="2")
							                     		{
							                     			document.write('<img  src="/images/code.gif" onclick=\'javascript:openInputCodeDialogOrg_handwork("UM","codeitem.viewvalue","'+managerstr+'","s");\' align="middle"  /> ');
							                     		}
							                     		else if(infor=="3"||infor=="5")
							                     		{
							                     			document.write('<img  src="/images/code.gif" onclick=\'javascript:openInputCodeDialogOrg_handwork("UN","codeitem.viewvalue","'+managerstr+'","s");\' align="middle"  /> ');
							                     		}
							                     		else if(infor=="4")
							                     		{
							                     			document.write('<img  src="/images/code.gif" onclick=\'javascript:openInputCodeDialogOrg_handwork("UM","codeitem.viewvalue","'+managerstr+'","s");\' align="middle"  /> ');
							                     		}
							                        </script>
												</td>
											</tr>
											<tr><td height="5px"></td></tr>
											<tr>
												<td align="center">
													<select name="left_fields" multiple="multiple"
														ondblclick="additem2('left_fields','right_fields');"
														style="height: 351px; width: 100%; font-size: 9pt">
													</select>
												</td>
											</tr>
										</table>
									</td>

									<td width="48px" align="center">
										<table border="0" cellspacing="0" align="center"
											cellpadding="0">
											<tr>
												<td align="center">
													<html:button styleClass="mybutton" property="b_addfield"
														onclick="additem2('left_fields','right_fields');" style="margin-left:7px;">
														<bean:message key="button.setfield.addfield" />
													</html:button>
												</td>
											</tr>
											<tr>
												<td height="30px"></td>
											</tr>
											<tr>
												<td align="center">
													<html:button styleClass="mybutton" property="b_delfield"
														onclick="removeitem('right_fields');" style="margin-left:7px;">
														<bean:message key="button.setfield.delfield" />
													</html:button>
												</td>
											</tr>
										</table>
									</td>
									<td width="48%" align="center">
										<table width="100%" border="0" cellspacing="0" cellpadding="0"
											align="center">
											<tr>
												<td width="100%" align="left">
													<bean:message key="lable.performance.selectedPerMainBody" /><bean:message key="lable.performance.object" />
												</td>
											</tr>
											<tr>
												<td width="100%" align="left">
													<select name="right_fields" multiple="multiple" size="10"
														ondblclick="removeitem('right_fields');"
														style="height: 370px; width: 100%; font-size: 9pt">
													</select>
												</td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td align="center" class="RecordRowP" nowrap style="height: 35px">
							<html:button styleClass="mybutton" property="b_save"
								onclick="savecode()">
								<bean:message key="button.ok" />
							</html:button>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</html:form>
