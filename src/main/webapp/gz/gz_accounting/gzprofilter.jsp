<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="java.util.*,com.hrms.struts.constant.WebConstant,com.hrms.struts.valueobject.UserView"%>
 <%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
%>
<%
String opt = (String) request.getParameter("opt");
String model=(String)request.getParameter("model");
%>
<script language="javascript">
	var aid="";
	var aname="";
	var flag ="";//防止新建时点保存多次产生多条记录

	var model="";//history 表示为薪资历史数据分析进入
	<% if("history".equalsIgnoreCase(model)){ %>
		model="history";
	<%}%>
	
	function bsave()
	{
		var rightFiledIDs="";
		var rightFieldNames="";		
		var rightFields=$('proright_fields')
		var type="save";
		if(rightFields.options.length==0)
		{
			 returnValue=null;
			 type="";
			 alert(GENERAL_SELECT_ITEMNAME);
			 return;
		}
		for(var i=0;i<rightFields.options.length;i++)
		{
			rightFiledIDs+=","+rightFields.options[i].value;
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
				alert(a_text+ITEM_NOT_RESET+"！");
				return;
			}
		}
		if(type!="")
		{
//			var thecodeurl ="/gz/gz_accounting/gz_save_pro_filter.jsp"; 
//	    	var return_vo= window.showModalDialog(thecodeurl, "", 
//	              "dialogWidth:300px; dialogHeight:180px;resizable:no;center:yes;scroll:yes;status:yes");
//	    	if(return_vo!=null)
//	    	{
				var porjectname = document.getElementById("porjectname").value;
			    if(porjectname==null||porjectname=='')
			    {
			      alert(GZ_ACCOUNTING_ENTERFILTERNAME+"!");
			      return;
			    }
			    if(porjectname.length>50)   
			    {   
			        alert(GZ_ACCOUNTING_CHARLENGTHFIFTY+"！");   
			        return ;   
			    }
			    var scopeflag='${accountingForm.scopeflag}';
			     if (document.accountingForm.attributeflag[0].checked){
						scopeflag="1";
					}else{
						scopeflag="0";
					}
	    		var operation="save";
	    		var projectname = getEncodeStr(porjectname);  
	    		var scopeflag = scopeflag;  		
	    	//	alert(rightFiledIDs.substring(1)); 
	    		var hashVo=new ParameterSet();
	 			hashVo.setValue("operation",operation);
	 			hashVo.setValue("projectname",projectname);
	 			hashVo.setValue("proright_str",rightFiledIDs.substring(1));
	 			hashVo.setValue("salaryid","${accountingForm.salaryid}");
	 			hashVo.setValue("model",model)
	 			if(flag==""){
	 			hashVo.setValue("chkid","${accountingForm.chkid}");
	 			}else{
	 			hashVo.setValue("chkid",flag);
	 			}
	 			hashVo.setValue("scopeflag",scopeflag);
	 			var request=new Request({method:'post',onSuccess:saveOk,asynchronous:false,functionId:'3020070202'},hashVo);
//	    	}
		}
	}

	function saveOk(outparamters)
	{
		aid=outparamters.getValue("id");
		aname=outparamters.getValue("name");
		var salaryid=outparamters.getValue("salaryid");
		flag=aid;
		<% if(opt.equals("1")){%>
	    	window.returnValue=salaryid;
	    	window.close();
		<%}else{%>
		alert(SAVESUCCESS+"!");
		<%}%>
		//accountingForm.action="/gz/gz_accounting/gzprofilter.do?b_delete=link&salaryid="+salaryid;
		//accountingForm.submit();

	}


	function bdelete()
	{

		var thecodeurl ="/gz/gz_accounting/gzprofilter.do?b_delete=link`model="+model+"`salaryid=${accountingForm.salaryid}";
		var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
    	var vo= window.showModalDialog(iframe_url, "", 
              "dialogWidth:300px; dialogHeight:420px;resizable:no;center:yes;scroll:yes;status:no");
	}
	function sub()
	{
		var rightFiledIDs="";
		var rightFieldNames="";		
		var rightFields=$('proright_fields')
		var type="conmit";
		if(rightFields.options.length==0)
		{
			 returnValue=null;
			 type="";
			 alert(GENERAL_SELECT_ITEMNAME);
			
		}
		for(var i=0;i<rightFields.options.length;i++)
		{
			rightFiledIDs+=","+rightFields.options[i].value;
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
				alert(a_text+ITEM_NOT_RESET+"！");
				return;
			}
		}	
		if(type!="")
		{
		    var obj=new Array();
			var retstr = rightFiledIDs.substring(1);
			obj[0]=retstr;
			obj[1]=aid;
			obj[2]=aname;
			window.returnValue=obj;
		//	window.returnValue=retstr;		
			window.close();
		}
		
	}
	
</script>
<html:form action="/gz/gz_accounting/gzprofilter">
<%if("hl".equals(hcmflag)){ %>
<table width='440px;' border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable" >
<%}else{ %>
<table width='440px;' border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable" style="margin-top:-1px;margin-left:-1px;">
<%} %>
		<tr>
			<td align="left" nowrap>
				<bean:message key="menu.gz.itemfilter" />
				&nbsp;&nbsp;
				<input type="text" name="porjectname" size="35"
					value="<bean:write name="accountingForm" property="chkName"/>" class="inputtext"/>
				<logic:equal name="accountingForm" property="scopeflag" value="1">
					<input type="radio" name="attributeflag" checked>&nbsp;
								<bean:message key="label.gz.private" />
				</logic:equal>
				<logic:equal name="accountingForm" property="scopeflag" value="0">
					<input type="radio" name="attributeflag">&nbsp;
								<bean:message key="label.gz.private" />
				</logic:equal>
				<logic:equal name="accountingForm" property="scopeflag" value="0">
					<input type="radio" name="attributeflag" checked>&nbsp;
								<bean:message key="label.gz.public" />
				</logic:equal>
				<logic:equal name="accountingForm" property="scopeflag" value="1">
					<input type="radio" name="attributeflag">&nbsp;
								<bean:message key="label.gz.public" />
				</logic:equal>
			</td>
		</tr>
		<tr>
			<td align="left" class="TableRow" nowrap>
				<bean:message key="menu.gz.itemfilter" />
				&nbsp;&nbsp;
			</td>
		</tr>
		<html:hidden name="accountingForm" property="proright_str" />
		<html:hidden name="accountingForm" property="chkid" />
		<html:hidden name="accountingForm" property="chkName" />
		<tr>
			<td class="RecordRow" style="border-top=0px">
				<table>
					<tr>
						<td align="center" width="46%">
							<table align="center" width="100%">
								<tr>
									<td align="left">
										<bean:message key="selfservice.query.queryfield" />
										&nbsp;&nbsp;
									</td>
								</tr>
								<tr>
									<td align="center">
									</td>
								</tr>
								<tr>
									<td align="center">
										<hrms:optioncollection name="accountingForm"
											property="profilterlist" collection="list" />
										<html:select name="accountingForm" size="10"
											property="proleft_field" multiple="multiple"
											ondblclick="additem('proleft_field','proright_fields');"
											style="height:230px;width:100%;font-size:9pt">
											<html:options collection="list" property="name"
												labelProperty="label" />
										</html:select>
									</td>
								</tr>
							</table>
						</td>
						<td width="8%" align="center">
							<html:button styleClass="mybutton" property="b_addfield"
								onclick="additem('proleft_field','proright_fields');">
								<bean:message key="gz.acount.filter.add" />
							</html:button>
							<br>
							<br>
							<html:button styleClass="mybutton" property="b_delfield"
								onclick="removeitem('proright_fields');">
								<bean:message key="gz.acount.filter.delete" />
							</html:button>
						</td>


						<td width="46%" align="center">
							<table width="100%">
								<tr>
									<td width="100%" align="left">
										<bean:message key="selfservice.query.queryfieldselected" />
										&nbsp;&nbsp;
									</td>
								</tr>
								<tr>
									<td width="100%" align="left">
										<hrms:optioncollection name="accountingForm"
											property="filterFieldList" collection="list" />
										<html:select name="accountingForm" size="10"
											property="proright_fields" multiple="multiple"
											ondblclick="removeitem('proright_fields');"
											style="height:230px;width:100%;font-size:9pt">
											<html:options collection="list" property="dataValue"
												labelProperty="dataName" />
										</html:select>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td align="center" height="35" class="RecordRow" style="border-top=0px">

				<%
				if (opt != null && opt.equals("2")) {
				%>
				<html:button styleClass="mybutton" property="b_next"
					onclick="sub();">
					<bean:message key="button.ok" />
				</html:button>
				<%
				}
				%>
				<hrms:priv func_id="3240223,3240323,3250323,3250223,031404">
					<html:button styleClass="mybutton" property="b_save"
						onclick="bsave()">
						<bean:message key="button.save" />
					</html:button> 
	      			</hrms:priv>
				<html:button styleClass="mybutton" property="b_return"
					onclick="window.close()">
					<bean:message key="button.cancel" />
				</html:button>

			</td>
		</tr>

	</table>

</html:form>