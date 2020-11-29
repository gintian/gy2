<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.WebConstant" %>
<%@ page import="com.hrms.struts.valueobject.UserView" %>
<%
	String subsys_id=request.getParameter("subsys_id");
%>
<script type="text/javascript">
<!--
function save()
{
   
    var templateid = khTemplateForm.templateid.value;
    if(templateid==null || templateid=="") {
    	alert("模板编号不能为空！");
    	return;
    }
    if(templateid.length>30)
    {
    	alert("模板编号长度不能超过30位！");
    	return;
    }
    var name=khTemplateForm.templatename.value;
    if(name.length>30)
    {
    	alert("模板名称长度不能超过30位！");
    	return;
    }
    if(trim(name).length==0)
    {
       alert("<bean:message key="label.kh.template.namenotnull"/>");
       return;
    }
    var score =khTemplateForm.topscore.value;
    var myReg =/^(-?\d+)(\.\d+)?$/;
     if(!myReg.test(score)){
         alert("<bean:message key="label.kh.template.scoreinvilad"/>"+"!");
         return;
      }
    if(trim(score).length==0)
    {
         alert("<bean:message key="label.kh.template.scoreinvilad"/>"+"!");
         return;
    }
     var templatesetid = khTemplateForm.templatesetid.value;
      var parentsetid = khTemplateForm.parentsetid.value;
       var type = khTemplateForm.type.value;
   var hashvo=new ParameterSet();
   hashvo.setValue("opt",'1');
   hashvo.setValue("templatename",getEncodeStr(name));
   hashvo.setValue("templateid",templateid);
   hashvo.setValue("oldid",templatesetid);
   hashvo.setValue("type",type);
   hashvo.setValue("parentsetid",parentsetid);
   var request=new Request({asynchronous:false,onSuccess:check_ok,functionId:'9021001026'},hashvo);
}
function check_ok(outparameters)
{
   var msg = outparameters.getValue("msg");
   if(msg=='1')
   {
      alert("<bean:message key ="label.kh.template.exist"/>");
      return;
   }
   var obj = new Object();
   var templateid=khTemplateForm.templateid.value;
   var templatename=khTemplateForm.templatename.value;
   var topscore=khTemplateForm.topscore.value;
   var tt = document.getElementsByName("status");
   var status = "";
   for(var i=0;i<tt.length;i++)
   {
       if(tt[i].checked)
       {
           status=tt[i].value;
       }
   }
   var templatesetid = khTemplateForm.templatesetid.value;
   var parentsetid = khTemplateForm.parentsetid.value;
   obj.id=templateid;
   obj.name=getEncodeStr(templatename);
   obj.topscore = topscore;
   obj.status=status;
   obj.setid=templatesetid
   obj.parentsetid=parentsetid
   obj.refresh="0";
   obj.type=khTemplateForm.type.value;;
    parent.window.returnValue=obj;
    if(window.showModalDialog) {
        parent.window.close();;
    }else{
        parent.window.opener.window.addTemplate_Ok(obj);
        window.open("about:blank","_top").close();
    }
   
}
function winclose() {
    if(window.showModalDialog) {
        parent.window.close();;
    }else{
        window.open("about:blank","_top").close();
    }
}

function selecttemplateset()
{
   var infos=new Array();
	infos[0]="-1";
	infos[1]=<%=subsys_id%>;
   var thecodeurl="/performance/kh_system/kh_template/init_kh_item.do?br_selecttemplateset=query"; 
   var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;

    /* if(window.showModalDialog){
        var return_vo= window.showModalDialog(iframe_url, infos,
            "dialogWidth:450px; dialogHeight:360px;resizable:no;center:yes;scroll:no;status:no");
        selecttemplateset_Ok(return_vo);
    }else{
        window.dialogArguments=infos;
        window.open(iframe_url, 'selecttemplateset', "width=450; height=360;resizable=no;center=yes;scroll=no;status=no");
    } */
    
    var config = {
   	    width:450,
   	    height:360,
   	    type:'2',
   	    dialogArguments:infos,
   	    id:"selecttemplateset_win"
   	}
   	if(!window.showModalDialog)
   		window.dialogArguments = infos;

   	modalDialog.showModalDialogs(iframe_url,"selecttemplateset_win",config,selecttemplateset_Ok);
}

function selecttemplateset_Ok(return_vo) {
    if(return_vo)
    {
        if(return_vo=='undefined'||return_vo=='')
        {
            return;
        }
        var infoArray = return_vo.split(",");
        khTemplateForm.parentsetid.value = infoArray[0];
        khTemplateForm.setname.value = infoArray[1];
    }
}
//-->
</script>
<html:form action="/performance/kh_system/kh_template/add_or_edit_template">
<%
	UserView userView = (UserView) session.getAttribute(WebConstant.userView);
	String bosFlag = userView.getBosflag();
	int top = 0;
	int left = 5;
	top = !"hcm".equalsIgnoreCase(bosFlag) ? 10 : top;
	left = !"hcm".equalsIgnoreCase(bosFlag) ? 5 : left;
%>
	<table width="500" border="0" style="margin-left: 0px;" cellspacing="0" align="left" cellpadding="0">
		<tr>
			<td>
				<fieldset align="center">
					<legend>
						<logic:equal name="khTemplateForm" property="type" value="0">
							<bean:message key="label.kh.new.template" />
						</logic:equal>
						<logic:equal name="khTemplateForm" property="type" value="1">
							<bean:message key="label.kh.edit.template" />
						</logic:equal>
						<logic:equal name="khTemplateForm" property="type" value="3">
							<bean:message key="label.kh.edit.saveastemplate" />
						</logic:equal>
					</legend>
					<table width="100%" border="0" cellspacing="0" align="center"
						cellpadding="5">
						<tr height="35">
							<td width="30%" align="right" nowrap><bean:message key="label.kh.template.seq" /></td>
							<td align="left" nowrap><logic:equal value="1" name="khTemplateForm" property="templateUsed">
									<html:text property="templateid" name="khTemplateForm" size="50px" styleClass="textColorWrite" readonly="true"></html:text>
								</logic:equal> <logic:equal value="0" name="khTemplateForm" property="templateUsed">
									<html:text property="templateid" name="khTemplateForm" styleClass="textColorWrite" size="50px"></html:text>
								</logic:equal></td>
						</tr>
						<logic:equal name="khTemplateForm" property="type" value="3">
							<tr height="35">
								<td width="30%" align="right" nowrap>模板分类</td>
								<td align="left" nowrap><html:text property="setname" name="khTemplateForm" size="50px" styleClass="textColorWrite"
										readonly="true"></html:text> <input type="button" value="..." onclick='selecttemplateset();' style="vertical-align: middle;">
								</td>
							</tr>
						</logic:equal>
						<tr height="35">
							<td width="30%" align="right" nowrap><bean:message key="label.kh.template.name" /></td>
							<td align="left" nowrap>
								<html:text property="templatename" name="khTemplateForm" styleClass="textColorWrite" size="50px"></html:text>
							</td>
						</tr>

						<tr height="35">
							<td width="30%" align="right" nowrap><bean:message key="label.kh.template.total" /></td>
							<td align="left" nowrap><logic:equal value="1" name="khTemplateForm" property="templateUsed">
									<html:text property="topscore" name="khTemplateForm" styleClass="textColorWrite" size="50px" maxlength="10" readonly="true"></html:text>
								</logic:equal> <logic:equal value="0" name="khTemplateForm" property="templateUsed">
									<html:text property="topscore" name="khTemplateForm" styleClass="textColorWrite" size="50px" maxlength="10"></html:text>
								</logic:equal></td>
						</tr>
						<tr height="35">
							<td width="30%" align="right" nowrap><bean:message key="label.kh.template.type" /></td>
							<td align="left" nowrap><logic:notEqual property="type" name="khTemplateForm" value="3">
									<logic:equal value="1" name="khTemplateForm" property="templateUsed">
										<html:radio name="khTemplateForm" property="status" value="0" disabled="true"></html:radio>
										<bean:message key="jx.param.mark" />
										<html:radio name="khTemplateForm" property="status" value="1" disabled="true"></html:radio>
										<bean:message key="label.kh.template.qz" />
									</logic:equal>
									<logic:equal value="0" name="khTemplateForm" property="templateUsed">
										<html:radio name="khTemplateForm" property="status" value="0"></html:radio>
										<bean:message key="jx.param.mark" />
										<html:radio name="khTemplateForm" property="status" value="1"></html:radio>
										<bean:message key="label.kh.template.qz" />
									</logic:equal>
								</logic:notEqual> <logic:equal property="type" name="khTemplateForm" value="3">
									<html:radio name="khTemplateForm" property="status" value="0" disabled="true"></html:radio>
									<bean:message key="jx.param.mark" />
									<html:radio name="khTemplateForm" property="status" value="1" disabled="true"></html:radio>
									<bean:message key="label.kh.template.qz" />
								</logic:equal></td>
						</tr>
					</table>
				</fieldset>
			</td>
			<html:hidden name="khTemplateForm" property="templatesetid" />
			<html:hidden name="khTemplateForm" property="type" />
			<html:hidden name="khTemplateForm" property="parentsetid" />
		</tr>
		<tr height="35">
			<td align="center" style="padding-top: 5px;">
				<input type="button" name="ok" value="<bean:message key="button.ok"/>" onclick="save();" class="mybutton" /> 
				<input type="button" name="cancel" value="<bean:message key="button.cancel"/>" onclick="winclose();" class="mybutton" />
			</td>
		</tr>
	</table>
</html:form>