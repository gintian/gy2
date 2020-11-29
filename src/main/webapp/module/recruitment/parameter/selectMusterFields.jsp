<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>

<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
	UserView userView = (UserView) session.getAttribute(WebConstant.userView);
	String bosflag = userView.getBosflag();//得到系统的版本号
	String flag = request.getParameter("flag");
	String selectedFields = request.getParameter("selectedFields");
%>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7; IE=EDGE">
</head>
<script language="JavaScript" src="/js/constant.js"></script>
<link rel="stylesheet" type="text/css" href="/module/recruitment/css/newParameterSet.css" />
<script language="javascript">

function simulateClick(el) {
	var evt;
	if (document.createEvent) {
		evt = document.createEvent("HTMLEvents");
		evt.initEvent("change", true,false);
		el.dispatchEvent(evt);
	} else if (el.fireEvent) {
		el.fireEvent('onchange');
	}
}
	
	function check(sourcebox_id,targetbox_id){
		  var flag = additem(sourcebox_id,targetbox_id);
		  if(flag)
			  removeleftitem(sourcebox_id);
	}	
	/**从后台取得相应的数据,初始化前台*/
	function showSetList(outparamters)
	{
		var setlist=outparamters.getValue("setlist");
		var selectList = outparamters.getValue("selectedList");
		var size=outparamters.getValue("size");
		if(setlist)
		{
		   AjaxBind.bind(parameterForm2.right_fields,selectList);
		   showFieldList(outparamters);
		}
		if(size=='0'){
			alert(NOT_CONFIG_PERSONSTORE+"!");
			window.close();
		}
	}
	/**显示指标*/
	function showFieldList(outparamters)
	{
		var fieldlist=outparamters.getValue("fieldlist");
		AjaxBind.bind(parameterForm2.left_fields,fieldlist);
	}


				
	/**查询指标*/
	function searchFieldList()
	{
	   var hashvo=new ParameterSet();
	   hashvo.setValue("flag",'<%=request.getParameter("flag")%>');
	   var tablename=$F('setlist');
	   var in_paramters="tablename="+tablename;
   	   var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showFieldList,functionId:'ZP0000002371'},hashvo);
	}
	
		
	function sub()
	{
		var rightFiledIDs="";
		var rightFieldNames="";
	
		var rightFields=$('right_fields')
		if(rightFields.options.length==0)
		{
			 returnValue=0;
	    	 window.close();
		}
		var flag='<%=request.getParameter("flag")%>';
		var flag='<%=request.getParameter("flag")%>';
		//控制快速查询指标个数
		if(flag=='2'&&rightFields.options.length>3)
		{
			alert(ONLY_SELECT_THREE_FIELD+"!");
			return;
		}
		for(var i=0;i<rightFields.options.length;i++)
		{
			rightFiledIDs+="`"+rightFields.options[i].value;
			rightFieldNames+=","+rightFields.options[i].text;
			
			var a_value=rightFields.options[i].value;
			var n=0;
			var a_text="";
			for(var j=0;j<rightFields.options.length;j++)
			{
				if(rightFields.options[j].value==a_value)
				{
					n++;
					a_text=rightFields.options[j].text;
				}
			}
			if(n>1)
			{
				alert(a_text+FIELD_NOT_REPEAT);
				return;
			}
		}
		
		var infos=new Array();
		infos[0]=rightFiledIDs.substring(1);
		infos[1]=rightFieldNames.substring(1);
   	    returnValue=infos;
   	 if(window.parent.me){
   	 	window.parent.me.setCallBack({returnValue:returnValue});
   	    window.parent.Ext.getCmp('window').close();
   	 }else
   	   	 window.close();
	}
	
	/**填充花名册指标和排序指标*/
	function filloutData()
	{
	    setselectitem('right_fields');
	    setselectitem('sort_right_fields');		
	}
	
	/**初化数据*/
	function MusterInitData(flag)
	{
	   var pars="flag="+flag;
	   var hashvo=new ParameterSet();
	    hashvo.setValue("selectedFields",'<%=selectedFields%>');
   	   var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:showSetList,functionId:'ZP0000002370'},hashvo);
	}
	
</script>
<html:form action="/hire/parameterSet/configureParameter">
	<%
		if (bosflag != null && !bosflag.equals("hcm")) {
	%>
	<Br>
	<%
		}
	%>
	<table width='530' border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
		<tr>
			<td class="RecordRow" align="center" width="100%" nowrap>
				<table width="100%">
					<tr>
						<td width="100%" align="center" nowrap>
							<table>
								<tr>
									<td align="center" width="46%">
										<table align="center" width="100%">
											<tr>
												<td align="left"><bean:message key="selfservice.query.queryfield" />&nbsp;&nbsp;</td>
											</tr>
											<%--<tr>
												<td align="center">
													<select name="setlist" size="1" style="width:100%" onchange="searchFieldList();">
															<option value="1111">#</option>
													</select>
												</td>
											</tr>
											--%><tr>
												<td align="center">
												<select name="left_fields" multiple="multiple" ondblclick="check('left_fields','right_fields');"
													style="height:230px;width:100%;font-size:9pt">
												</select>
												</td>
											</tr>
										</table>
									</td>

									<td width="8%" align="center">
									<p>
									<html:button styleClass="mybutton" property="b_addfield" onclick="check('left_fields','right_fields');">
										<bean:message key="button.setfield.addfield" />
									</html:button>
									</p>
									<p>
									<html:button styleClass="mybutton" property="b_delfield" onclick="check('right_fields','left_fields');">
										<bean:message key="button.setfield.delfield" />
									</html:button>
									</p>
									</td>

									<td width="46%" align="center" >
										<table width="100%">
											<tr>
												<td width="100%" align="left">
												<bean:message key="selfservice.query.queryfieldselected" />&nbsp;&nbsp;
												</td>
											</tr>
											<tr>
												<td width="100%" align="left">
												<select size="10" name="right_fields" multiple="multiple" ondblclick="check('right_fields','left_fields');"
													style="height:230px;width:100%;font-size:9pt">
												</select></td>
											</tr>
										</table>
									</td>
									<td width="8%" align="center">
									<p>
										<html:button styleClass="mybutton" property="b_up" onclick="upItem($('right_fields'));">
											<bean:message key="button.previous" />
										</html:button>
									</p>
									<p>
										<html:button styleClass="mybutton" property="b_down" onclick="downItem($('right_fields'));">
											<bean:message key="button.next" />
										</html:button>
									</p>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td align="center" class="RecordRow" nowrap style="border:0;padding-top:15px;padding-bottom:3px;">
				<html:button styleClass="mybutton" property="b_next" onclick="sub()">
					<bean:message key="reporttypelist.confirm" />
				</html:button>
			</td>
		</tr>
	</table>

</html:form>
<script language="javascript">
   MusterInitData('<%=flag%>');
</script>