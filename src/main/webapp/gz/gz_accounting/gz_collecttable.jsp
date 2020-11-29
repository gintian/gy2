<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.gz.gz_accounting.CollectForm"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
	UserView userView = (UserView) request.getSession().getAttribute(
	WebConstant.userView);
	String username = userView.getUserName();
	String userid = userView.getUserId();
	
	CollectForm collectForm=(CollectForm)session.getAttribute("collectForm");
	String sp_actor_str=collectForm.getSp_actor_str();
    String sp_actor_name=collectForm.getSpActorName();
    String relation_id=collectForm.getRelation_id();
    String appealName="个别报批";
    if(sp_actor_name.length()>0)
    	appealName="个别报["+sp_actor_name+"]审批";
    boolean isApprove=true;
    boolean isAppeal=true;
    if(relation_id!=null&&relation_id.length()>0&&sp_actor_str.length()>0)
    	isApprove=false;
    if(relation_id!=null&&relation_id.length()>0&&sp_actor_str.length()==0)
    	isAppeal=false;
	
	
 %>
<html>
	<script language="javascript" src="/js/dict.js"></script>
	<script type="text/javascript" src="/gz/collect.js"></script>

	<script type="text/javascript">
	var verify_ctrl='${collectForm.verify_ctrl}';
	var isTotalControl='${collectForm.isTotalControl}';
	var salaryid='${collectForm.salaryid}';
	var gz_module='${collectForm.gz_module}';
	var sp_actor_str='${collectForm.sp_actor_str}'
	var isSendMessage="${collectForm.isSendMessage}";
<!--
function searchdata(salaryid)
{
		collectForm.action="/gz/gz_accounting/gz_collect_table.do?b_query=link&salaryid="+salaryid;
		collectForm.submit();    		
}
 function excecuteExcel()
   {
   var hashvo=new ParameterSet();			
	hashvo.setValue("salaryid","${collectForm.salaryid }");
	hashvo.setValue("sum_fields_str","${collectForm.sum_fields_str }");
	var request=new Request({method:'post',asynchronous:false,onSuccess:showExcel,functionId:'3020111018'},hashvo);
   
	}	
   function showExcel(outparamters)
   {
	var url=outparamters.getValue("excelfile");	
	if(url==""){
		alert("导出Excel失败！");
		return;
	}
	//alert(url);
	var fieldName = getDecodeStr(url);
	window.location.href="/servlet/vfsservlet?fileid="+fieldName+"&fromjavafolder=true";
   }
	
-->
</script>
<link href="/css/css1.css" rel="stylesheet" type="text/css">
	<style>
.selectPre {
	position: absolute;
	left: 3px;
	z-index: 9;
	top:30px;
}
.selectPre1 {
	position: absolute;
	left: 3px;
	z-index: 10;
}
.Rr {
	border: 1px solid #8EC2E6;
	BORDER-BOTTOM:  #B9D2F5 1pt solid; 
	BORDER-LEFT:  #B9D2F5 1pt solid; 
	BORDER-RIGHT:  #B9D2F5 1pt solid; 
	BORDER-TOP: #B9D2F5 1pt solid;
	font-size: 12px;
}
</style>
<hrms:themes />
	<body>
	
	
	
	<div id='wait' style='position:absolute;z-index:15;top:130;left:300;display:none;'>
		<table border="1" width="100" cellspacing="0" cellpadding="4"  class="table_style"  height="87" align="center">
			<tr>
				<td  class="td_style"  id='wait_desc'   height=24>
					<bean:message key="label.gz.submitData"/>......
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee  class="marquee_style"  direction="right" width="300" scrollamount="5" scrolldelay="10" >
						<table cellspacing="1" cellpadding="0">
							<tr height=8>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
							</tr>
						</table>
					</marquee>
				</td>
			</tr>
		</table>
		<iframe src="javascript:false" style="position:absolute; visibility:inherit; top:0px; left:0px;width:315; height:87; 					    	
			   			 				z-index:-1;filter='progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)';"></iframe>	
	</div>
	
	
	
	
		<html:form action="/gz/gz_accounting/gz_collect_table">
		<input type="hidden" id="selectGzRecords" name="selectGzRecords" value="" />
		<input type="hidden" id="rejectCause" name="rejectCause" value="" />
			<table  border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td>
						<table id="selectprename" class="">
							<tr>
								<td>
						<input type="button" name="button1"
							value='<bean:message key="label.gz.listtable"/>' Class="mybutton"
							onclick="window.open('/gz/gz_accounting/gz_sp_orgtree.do?b_query=link&ori=0&salaryid=${collectForm.salaryid }','_parent');">
						
						<logic:equal name="collectForm" property="gz_module" value="0">
							<hrms:priv func_id="3240301,3270301">
							<% if(isAppeal){ %>	
								<input type="button" name="button2"
									value='<%=appealName%>'
									Class="mybutton" onclick="appeal('<%=userid %>','appeal','${collectForm.salaryid}','${collectForm.gz_module}','${collectForm.tempTableName }')">
							 <% } %>
							</hrms:priv>
							<hrms:priv func_id="3240303,3270303">
								<input type="button" name="button2"
									value='<bean:message key="button.reject"/>'
									Class="mybutton" onclick="optSalary('reject','<%=userid %>','${collectForm.tempTableName }');">
							</hrms:priv>
							<hrms:priv func_id="3240302,3270302">
							<% if(isApprove){ %>
								<input type="button" name="button2"
									value='个别<bean:message key="button.approve"/>'
									Class="mybutton" onclick="optSalary('confirm','<%=userid %>','${collectForm.tempTableName }')">
							<% } %>
							</hrms:priv>
						</logic:equal>
						<logic:equal name="collectForm" property="gz_module" value="1">
							<hrms:priv func_id="3250301,3271301">
							<% if(isAppeal){ %>	
								<input type="button" name="button2"
									value='<%=appealName%>'
									Class="mybutton" onclick="appeal('<%=userid %>','appeal','${collectForm.salaryid}','${collectForm.gz_module}','${collectForm.tempTableName }')">
							<% } %>
							</hrms:priv>
							<hrms:priv func_id="3250303,3271303">
								<input type="button" name="button2"
									value='<bean:message key="button.reject"/>'
									Class="mybutton" onclick="optSalary('reject','<%=userid %>','${collectForm.tempTableName }');">
							</hrms:priv>
							<hrms:priv func_id="3250302,3271302">
							<% if(isApprove){ %>
								<input type="button" name="button2"
									value='个别<bean:message key="button.approve"/>'
									Class="mybutton" onclick="optSalary('confirm','<%=userid %>','${collectForm.tempTableName }')">
							<% } %>
							</hrms:priv>
						</logic:equal>
						
						<input type="button" name="button2"
							value='<bean:message key="goabroad.collect.educe.excel"/>'
							Class="mybutton" onclick="excecuteExcel();">
					</td>
								<td nowrap>
									&nbsp;<bean:message key="label.gz.appdate" />
								</td>
								<td>
									<html:select name="collectForm" property="bosdate" size="1"
										onchange="searchdata('${collectForm.salaryid}');">
										<html:optionsCollection property="datelist" value="dataValue"
											label="dataName" />
									</html:select>
								</td>
								<td nowrap>
									<bean:message key="label.gz.count" />
								</td>
								<td>
									<html:select name="collectForm" property="count" size="1"
										onchange="searchdata('${collectForm.salaryid}');">
										<html:optionsCollection property="countlist" value="dataValue"
											label="dataName" />
									</html:select>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr  >
					<td >
						<hrms:dataset setname="gz_sp_report" scope="session" 
							property="fieldlist" sql="${collectForm.sql }" name="collectForm" pagerows="${collectForm.pagerows}"
							readonly="true" select="true" buttons="bottom">
						</hrms:dataset>
					</td>
				</tr>
				<tr>
					
				</tr>
			</table>
			
			<Input type='hidden' name='sendMen'  value='' />
			
		</html:form>
<script language="javascript">
<!--
	function tablegz_sp_report_b0110_onRefresh(cell,value,record){
		if(record!=null&&record!=""){	
			var values = record.getValue("b0110");
			if(values.indexOf("sum")!=-1)	
				cell.innerHTML="<center><strong>合&nbsp;&nbsp;计</strong></center>";	
			else
				cell.innerHTML=value;
		}
	}
	function tablegz_sp_report_select_onRefresh(cell,value,record){
		//alert(cell.innerHTML);
		//alert(value+"1");
		//alert(record.getValue("select"));
		if(record!=null&&record!=""){	
			var values = record.getValue("b0110");
			if(values.indexOf("sum")!=-1)	
				cell.innerHTML="";	
			else{
				if(value==""&&value!="false"){
					cell.childNodes[0].value=values;
					cell.childNodes[0].checked=false;
				}else if(value=="true")
					cell.childNodes[0].checked=true;
				
			}
		}
	}
	function tablegz_sp_report_sp_flag_onRefresh(cell,value,record){
		if(record!=null&&record!=""){	
			var values = record.getValue("b0110");
			if(values.indexOf("sum")!=-1)	
				cell.innerHTML="";
			else
				cell.innerHTML=value;	
		}
	}
-->
</script>
	</body>
</html>