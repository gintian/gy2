<%@ page contentType="text/html; charset=UTF-8"%>
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
 <script type="text/javascript" src="/ext/ext-all.js" ></script>
    <script type="text/javascript" src="/ext/ext-lang-zh_CN.js" ></script> 
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/module/recruitment/parameter/sorting.js"></script>
<link  rel="stylesheet" type="text/css"  href="/module/recruitment/css/newParameterSet.css"/>
<style type="text/css">
.mybutton{
	cursor:pointer;
	color:#000;
    border:1px solid #c5c5c5;
    background-color:#f9f9f9;
    height:23px;
    line-height:20px;
    background-repeat:no-repeat;
    text-align:center
 }
#dis_sort_table { /* border: 1px solid #C4D8EE;*/
	height: 230px;
	width: 240px;
	overflow: auto;
	/*margin: 1em 1;*/
}
</style>
<script type="text/javascript">
/* Ext.onReady(function(){
	if(!Ext.isIE){
		Ext.getDom("dis_sort_table").style.marginTop="10px";
	}
}); */
  function check(){
	  var flag = addfield();
	  if(flag){
		  removeleftitem('itemid');
	  }
  }
  function sub(){
		var sortitem = document.getElementById("sortitem").value;
		if(sortitem!=null&&sortitem.length>0){
			window.returnValue=sortitem;
	  	}else{
	  		window.returnValue="not";
	  	}
		if(window.parent.me){//针对新招聘
			window.parent.me.setCallBack({returnValue:returnValue});
	   	    window.parent.Ext.getCmp('window').close();
   	   	}else
   	   		window.close();
	}
	function closeWindow(){
		if(window.parent.me)
	    	window.parent.Ext.getCmp('window').close();
		else
			window.close();
	}
</script>
<%
	if ("hcm".equalsIgnoreCase(bosflag)) {
%>
<style>
.ListTable {
	width: expression(document . body . clientWidth-10);
}
</style>
<%
	} else {
%>
<style>
.ListTable {
	margin-top: 10px;
	width: expression(document . body . clientWidth-10);
}
</style>
<%
	}
%>
<html:form action="/recruitment/parameter/configureParameter">
<div class="fixedDiv2" style="height: 100%;border: none">
	<table width='100%' border="0" cellspacing="0" align="center"
		cellpadding="0" class="ListTable">
		
		<tr>
			<td width="100%" align="center" class="RecordRow" nowrap>
				<table border="0" cellspacing="0" align="center"
		cellpadding="0">
					<tr>
						<td align="left" valign="bottom">
							<bean:message key="selfservice.query.queryfield" />
						</td>
						<td align="left" valign="bottom">
						</td>
						<td width="100%" align="left" valign="bottom"  style="padding-left:5px;">
							<bean:message key="selfservice.query.queryfieldselected"/>
						</td>
						<td width="100%" align="left" valign="bottom">
						</td>
					</tr>
					<tr>
						<td align="center" valign="center" width="44%">
							<table align="center" width="100%">
								<logic:equal name="moudleParameterForm" property="checkflag" value="1">
									<tr>
										<td align="center">
											<html:select name="moudleParameterForm" property="fieldid"
												styleId="fieldid" onchange="changeField();"
												style="width:100%;font-size:9pt">
												<html:optionsCollection property="fieldlist"
													value="dataValue" label="dataName" />
											</html:select>
										</td>
									</tr>
									<tr>
										<td align="center">
											<hrms:optioncollection name="moudleParameterForm" property="itemlist"
												collection="list" />
											<html:select name="moudleParameterForm" property="itemid"
												multiple="multiple"
												ondblclick="addfield();removeleftitem('itemid');"
												style="height:195px;width:100%;font-size:9pt">
												<html:options collection="list" property="name"
													labelProperty="label" />
											</html:select>
										</td>
									</tr>
								</logic:equal>
								<logic:notEqual name="moudleParameterForm" property="checkflag" value="1">
									<tr>
										<td align="center">
											<html:select name="moudleParameterForm" property="itemid"
												multiple="multiple"
												ondblclick="addfield();removeleftitem('itemid');"
												style="height:230px;width:100%;font-size:9pt">
												<html:optionsCollection property="itemlist"
													value="dataValue" label="dataName" />
											</html:select>
										</td>
									</tr>
								</logic:notEqual>
							</table>
						</td>
						<td width="48px" align="center">
							<table border="0" cellspacing="0"  align="center" cellpadding="0" style="margin-left:5px;">
		               			<tr>
		               				<td align="center">
							            <html:button styleClass="mybutton" property="b_addfield" onclick="check();">
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
											onclick="deletefield();">
											<bean:message key="button.setfield.delfield" />
										</html:button>
		               				</td>
		               			</tr>
		               		</table>
						</td>
						<td width="44%" align="center" valign="center">
							<div id="dis_sort_table" class="RecordRow"
								style="margin-left:5px;padding:0px;height:228px;top: 30px;width: 230px;"> <!-- modify by xiaoyun 薪资管理HCM7.0页面处理 -->
								<table width="230px" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
									<tr>
										<td class="TableRow"  align="left">
											&nbsp;
										</td>
										<td class="TableRow"  align="center">
											<bean:message key="field.label" />
										</td>
										<td class="TableRow"  align="center">
											<bean:message key="label.query.baseDesc" />
										</td>
									</tr>
								</table>
							</div>
						</td>
						<td width="48px" align="center" style="padding-left: 15px;">
							<table border="0" cellspacing="0"  align="center" cellpadding="0">
		               			<tr>
		               				<td align="center">
							            <html:button styleClass="mybutton" property="b_up"
											onclick="upSort();">
											<bean:message key="button.previous" />
										</html:button>
						            </td>
		               			</tr>
		               			<tr>
		               				<td height="30px"></td>
		               			</tr>
		               			<tr>
		               				<td align="center">
							           <html:button styleClass="mybutton" property="b_down"
											onclick="downSort();">
											<bean:message key="button.next" />
										</html:button>
		               				</td>
		               			</tr>
		               		</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td  style="height: 35px;padding-top:15px;padding-left:200px">
				<logic:equal value="xuj" name="moudleParameterForm" property="xuj">
					<hrms:priv func_id="3240215,3271217,3270217,3250217">
						<html:button styleClass="mybutton" property="b_defOrder"
							onclick="defOrder()">
							<bean:message key="infor.button.sortitem" />
						</html:button>

          </hrms:priv>
				</logic:equal>
				<html:button styleClass="mybutton" property="b_next" onclick="sub()" style="margin-top:5px;">
					<logic:equal value="xuj" name="moudleParameterForm" property="xuj">
            	临时排序
            </logic:equal>
					<logic:notEqual value="xuj" name="moudleParameterForm" property="xuj">
						<bean:message key="button.ok" />
					</logic:notEqual>
				</html:button>
				<html:button styleClass="mybutton" property="b_return"
					onclick="closeWindow()">
					<bean:message key="button.cancel" />
				</html:button>
			</td>
		</tr>
	</table>
	</div>
	<input type="hidden" name="sortitemid" id="sortitemid">
	<html:hidden name="moudleParameterForm" property="sortitem" styleId="sortitem" />
	<html:hidden name="moudleParameterForm" property="flag" styleId="flag" />
	<html:hidden name="moudleParameterForm" property="salaryid" styleId="salaryid" />
	<logic:equal name="moudleParameterForm" property="checkflag" value="1">
		<script language="javascript">
defField();
changeField();
</script>
	</logic:equal>
	<logic:notEqual name="moudleParameterForm" property="checkflag" value="1">
		<script language="javascript">
defField();
</script>
	</logic:notEqual>
</html:form>