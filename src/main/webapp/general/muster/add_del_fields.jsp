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
<META HTTP-EQUIV='pragma' CONTENT='no-cache'>

<script language="javascript">

	
	/**从后台取得相应的数据,初始化前台*/
	function showSetList(outparamters)
	{
		var setlist=outparamters.getValue("setlist");
		AjaxBind.bind(musterForm.setlist,/*$('setlist')*/setlist);
		if($('setlist').options.length>0)
		{
		  $('setlist').options[0].selected=true;
		  $('setlist').fireEvent("onchange");
		}
	}
	/**显示指标*/
	function showFieldList(outparamters)
	{
		var fieldlist=outparamters.getValue("fieldlist");
		AjaxBind.bind(musterForm.left_fields,fieldlist);
		removeSelectItem('left_fields','right_fields');
	}

				
	/**查询指标*/
	function searchFieldList()
	{
	   var tablename=$F('setlist');
	   var in_paramters="tablename="+tablename;
   	   var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showFieldList,functionId:'0520000002'});
	}
	
	/*刷新数据*/
	function refreshData(outparamters)
	{
	   //musterForm.action="/general/muster/fillout_musterdata.do";
	   //musterForm.submit(); 
	   returnValue="ssss";
	   window.close();		
	}
	
	/**增减指标*/
	function add_del_Fields(setname)
	{
		var rightFields=$('right_fields')	
		for(var i=0;i<rightFields.options.length;i++)
		{
			var a_value=rightFields.options[i].value;			
			var n=0;
			var a_text="";
			for(var j=0;j<rightFields.options.length;j++)
			{				
				if(rightFields.options[j].value.toUpperCase()==a_value.toUpperCase())
				{
					n++;
					a_text=rightFields.options[j].text;
				}
			}		
			if(n>1)
			{
				alert(a_text+ITEM_NOT_RESET);
				return;
			}
		}
		if(rightFields.options.length==0)
		{
			alert(ITEM_NOT_EMPTY);
			return;
		}
	
	   /*设置全部选中*/
	   setselectitem('right_fields');
	   
	   var fields=$F('right_fields');
	   var hashvo=new ParameterSet();
	   hashvo.setValue("fields",fields);
	   hashvo.setValue("setname",setname);
	   hashvo.setValue("infor_Flag",'<%=(request.getParameter("infor_Flag"))%>');
	   var request=new Request({asynchronous:false,onSuccess:refreshData,functionId:'0521010006'},hashvo);
	}
	
	/**初化数据*/
	function MusterInitData(infor)
	{
	   var pars="base="+infor;
   	   var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:showSetList,functionId:'0520000001'});
	}
	
	function removeSelectItem(sourcebox_id,targetbox_id){
  		var left_vo,right_vo,vos,i;

  		vos= document.getElementsByName(targetbox_id);  
  		if(vos==null)
  			return false;
  		right_vo=vos[0];
  		for(i=0;i<right_vo.options.length;i++){
    		var rightvalue=right_vo.options[i].value;
    		vos= document.getElementsByName(sourcebox_id);
    		left_vo=vos[0];
    		for(var j=0;j<left_vo.options.length;j++){
    			var leftvalue=left_vo.options[j].value;
    			if(leftvalue.toUpperCase()==rightvalue.toUpperCase()){
    				left_vo.options.remove(j);
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
<html:form action="/general/muster/add_del_fields">
	<html:hidden property="infor_Flag" />
	<!--花名册指标-->
	<div id="first" style="filter: alpha(Opacity =   100);">
		<table width="90%" border="0" cellspacing="0" align="center"
			cellpadding="0" class="ListTable">
			<thead>
				<tr>
					<td align="left" class="TableRow_lrt" nowrap>
						<bean:message key="label.query.selectfield" />
					</td>
				</tr>
			</thead>
			<tr>
				<td width="100%" align="center" class="RecordRow" nowrap>
					<table border="0" cellspacing="0" align="center" cellpadding="0">
						<tr>
							<td align="center" width="46%">
								<table align="center" width="100%" border="0" cellspacing="0"
									cellpadding="0" style="border-top: 0;">
									<tr>
										<td align="left">
											<bean:message key="selfservice.query.queryfield" />
										</td>
									</tr>
									<tr>
										<td align="center">
											<select name="setlist" size="1" style="width: 100%"
												onchange="searchFieldList();">
												<option value="1111">
													#
												</option>
											</select>
										</td>
									</tr>
									<tr><td height="6px"></td>
											</tr>
									<tr>
										<td align="center">
											<select name="left_fields" multiple="multiple"
												ondblclick="additem('left_fields','right_fields');removeitem('left_fields');"
												style="height: 216px; width: 100%; font-size: 9pt">
											</select>
										</td>
									</tr>
								</table>
							</td>

							<td width="48px" align="center">
								<table border="0" cellspacing="0" align="center" cellpadding="0">
									<tr>
										<td align="center">
											<html:button styleClass="mybutton" property="b_addfield"
												onclick="additem('left_fields','right_fields');removeitem('left_fields');">
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
												onclick="additem('right_fields','left_fields');removeitem('right_fields');">
												<bean:message key="button.setfield.delfield" />
											</html:button>
										</td>
									</tr>
								</table>
							</td>
							<td width="46%" align="center">
								<table width="100%" border="0" cellspacing="0" cellpadding="0"
									align="center" style="margin-top:5px;">
									<tr>
										<td width="100%" align="left">
											<bean:message key="selfservice.query.queryfieldselected" />
											&nbsp;&nbsp;
										</td>
									</tr>
									<tr>
										<td width="100%" align="left">
											<html:select name="musterForm" property="right_fields"
												multiple="multiple" size="10"
												ondblclick="additem('right_fields','left_fields');removeitem('right_fields');"
												style="height:250px;width:100%;font-size:9pt">
												<html:optionsCollection property="mfieldlist"
													value="dataValue" label="dataName" />
											</html:select>
										</td>
									</tr>
								</table>
							</td>
							<td width="48px" align="center">
								<table border="0" cellspacing="0" align="center" cellpadding="0">
									<tr>
										<td align="center">
											<html:button styleClass="mybutton" property="b_up"
												onclick="upItem($('right_fields'));">
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
												onclick="downItem($('right_fields'));">
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
				<td align="center" class="RecordRowP" nowrap style="height: 35">
					<html:button styleClass="mybutton" property="b_ok"
						onclick="add_del_Fields('${musterForm.mustername}');">
						<bean:message key="button.ok" />
					</html:button>
				</td>
			</tr>
		</table>
	</div>

</html:form>
<script language="javascript">
   MusterInitData('<bean:write name="musterForm"  property="infor_Flag"/>');
   
</script>