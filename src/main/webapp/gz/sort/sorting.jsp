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
	//自助服务、员工信息、信息维护 打卡人员排序 参数   wangb 20180206
	String mark = request.getParameter("mark")==null? "":request.getParameter("mark");
%>
 <script type="text/javascript" src="/ext/ext-all.js" ></script>
    <script type="text/javascript" src="/ext/ext-lang-zh_CN.js" ></script> 
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/gz/sort/sorting.js"></script>
<style type="text/css">
#dis_sort_table { /* border: 1px solid #C4D8EE;*/
	height: 230px;
	width: 240px;
	overflow: auto;
	/*margin: 1em 1;*/
}
</style>
<script type="text/javascript">
  function check(){
	  var flag = addfield();
	  if(flag){
		  addObject();
		  removeleftitem('itemid');
	  }
  }
  function sub(){
		var sortitem = document.getElementById("sortitem").value;
		
		if('<%=mark%>' && '<%=mark%>' == 'zzfw'){//自助服务、员工信息、信息维护 打卡人员排序 参数   wangb 20180206
			//19/3/15 xus ie 非兼容模式 选择排序指标 值不回填bug
			var returnStr = null;
			if(sortitem!=null&&sortitem.length>0){
				returnStr=sortitem;
	  		}else{
	  			returnStr="not";
	  		}
			if(window.showModalDialog){
				parent.parent.returnValue=returnStr;
				parent.parent.window.close();
			}else{
				parent.parent.returnSort(returnStr);
				parent.parent.opener.close();
			}
		}else{
			//薪资高级花名册排序
			var sortWindow;
        	if(parent.document.getElementById('sortWindowId')){
            	sortWindow=parent.Ext.getCmp('sortWindowId');
        	}
			if(sortWindow){
				if(sortitem!=null&&sortitem.length>0){
				parent.return_Value=sortitem;
		  		}else{
		  			parent.return_Value="not";
		  		}
		  		sortWindow.close();
		  	
			}else{
		
				if(sortitem!=null&&sortitem.length>0){
					if(getBrowseVersion()){//IE
						window.returnValue=sortitem;
					}else{
						if(parent.opener)
							parent.opener.returnSort(sortitem); 
						
					}
		  		}else{
		  			window.returnValue="not";
		  		}
			  		if(window.parent.me){//针对新招聘
					window.parent.me.setCallBack({returnValue:returnValue});
		   	    	window.parent.Ext.getCmp('window').close();
	   	   		}else
	   	   			window.close();
			}
		}
	}
	function closeWindow(){
		if('<%=mark%>' && '<%=mark%>' == 'zzfw'){//自助服务、员工信息、信息维护 打卡人员排序 参数   wangb 20180206
			if(getBrowseVersion()){//IE浏览器弹窗关闭
				parent.parent.window.close();
			}else{//非IE浏览器 ext 弹窗关闭   
				parent.parent.winClose();
			}
		}else{
			var sortWindow;
			if(parent.document.getElementById('sortWindowId')){
				sortWindow=parent.Ext.getCmp('sortWindowId');
	    	}
	    
			if(window.parent.me)
	    		window.parent.Ext.getCmp('window').close();
			else if(sortWindow)
				sortWindow.close();
			else
				window.close();
		}
		
	}
	//记录选择的左侧指标所属子集，fieldObject格式{指标:子集}
	function addObject(){
		var vos= document.getElementsByName("itemid");
		if(vos==null)
			return false;
		
		var vo = vos[0];
		//有的是没有fieldid选择框，左侧最上面的选择子集框
		if(document.getElementById("fieldid")) {
			var fieldsetid = document.getElementById("fieldid").value;	
			for(var i = 0; i <  vo.options.length; i++) {
				if(vo.options[i].selected){
					var itemId = vo.options[i].value.split(":")[0];
					eval("fieldObject." + itemId + "='" + fieldsetid + "'");
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
}
</style>
<%
	}
%>
<html:form action="/gz/sort/sorting" style="">
<div class="fixedDiv2" style="height: 100%;border: none">
	<table width='100%' border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
		<tr>
			<td width="100%" align="center" class="RecordRow" nowrap>
				<table border="0" cellspacing="0" align="center" cellpadding="0">
					<tr>
						<td align="left" style="width：150px;" id="tdId" valign="bottom">
							<bean:message key="selfservice.query.queryfield" />
						</td>
						<td align="left" width="40" valign="bottom">
						</td>
						<td width="210" align="left" valign="bottom"  style="padding-left:5px;">
							<bean:message key="selfservice.query.queryfieldselected"/>
						</td>
						<td width="56" align="left" valign="bottom">
						</td>
					</tr>
					<tr>
						<td align="center" valign="center">
							<table align="center" width="100%">
								<logic:equal name="sortForm" property="checkflag" value="1">
									<tr>
										<td align="center">
											<html:select name="sortForm" property="fieldid"
												styleId="fieldid" onchange="changeField();"
												style="width:100%;font-size:9pt">
												<html:optionsCollection property="fieldlist"
													value="dataValue" label="dataName" />
											</html:select>
										</td>
									</tr>
									<tr>
										<td align="center">
											<hrms:optioncollection name="sortForm" property="itemlist"
												collection="list" />
											<html:select name="sortForm" property="itemid"
												multiple="multiple"
												ondblclick="addfield();addObject();removeleftitem('itemid');"
												style="height:195px;width:100%;font-size:9pt">
												<html:options collection="list" property="name"
													labelProperty="label" />
											</html:select>
										</td>
									</tr>
								</logic:equal>
								<logic:notEqual name="sortForm" property="checkflag" value="1">
									<tr>
										<td align="center" style="border-right: solid;border-right-width: 1px;border-right-color:#c5c5c5;border-bottom-color: black;">
											<html:select name="sortForm" property="itemid"
												multiple="multiple"
												ondblclick="addfield();addObject();removeleftitem('itemid');"
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
						<td width="210px" align="center" valign="center">
							<table align="center" width="100%" border="0" height="100%" style="margin-top:0px;">
								<tr>
									<td >
										<div id="dis_sort_table" class="RecordRow"
											style="margin-left:5px;padding:0px;height:220px;top: 20px;width: 210px;margin-top: 10px;">
											<table width="210" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
												<tr>
													<td class="TableRow" style="border-top: none !important;"  align="left">
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
								</tr>
							</table>
						</td>
						<td width="48px" align="center" >
						<table border="0" cellspacing="0"  align="left" cellpadding="0" style="margin-left:5px;">
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
				<logic:equal value="xuj" name="sortForm" property="xuj">
					<hrms:priv func_id="3240215,3271217,3270217,3250217">
						<html:button styleClass="mybutton" property="b_defOrder"
							onclick="defOrder()">
							<bean:message key="infor.button.sortitem" />
						</html:button>
                    </hrms:priv>
				</logic:equal>
				<html:button styleClass="mybutton" property="b_next" onclick="sub()" style="margin-top:5px;">
					<logic:equal value="xuj" name="sortForm" property="xuj">
            	                       临时排序
                    </logic:equal>
					<logic:notEqual value="xuj" name="sortForm" property="xuj">
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
	<html:hidden name="sortForm" property="sortitem" styleId="sortitem" />
	<html:hidden name="sortForm" property="flag" styleId="flag" />
	<html:hidden name="sortForm" property="salaryid" styleId="salaryid" />
	<logic:equal name="sortForm" property="checkflag" value="1">
		<script language="javascript">
defField();
changeField();
</script>
	</logic:equal>
	<logic:notEqual name="sortForm" property="checkflag" value="1">
		<script language="javascript">
defField();
</script>
	</logic:notEqual>
</html:form>
<script>
	var disSortTable = document.getElementById('dis_sort_table');
if('<%=mark%>' && '<%=mark%>' == 'zzfw'){//自助服务、员工信息、信息维护 打卡人员排序 参数   wangb 20180206
	if(getBrowseVersion()){
		disSortTable.style.height = '215px';
		if(isCompatibleIE()){
			disSortTable.style.marginTop = '5px';
			var select = document.getElementById('fieldid');
			select.style.width = "150px";
		} else {
			disSortTable.style.marginTop = '10px';
		}
	}
}else{//高级花名册   样式修改       
	if(getBrowseVersion() && getBrowseVersion() == 10){//ie11 非兼容视图  wangb 20180307
		disSortTable.style.top ='23px';
		disSortTable.style.width = '224px';
		disSortTable.style.height = '210px';
	}
	
	if(isCompatibleIE()){
		var select = document.getElementById('itemid');
		select.style.width = "160px";
	}
}

</script>