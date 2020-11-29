<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/meizzDate.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="javascript">
  function takefile()
  {
     var file_num=$F('file_num');
     var Obj=document.getElementById("file_url");
     var file_url=Obj.value;     
     if(file_num=="")
     {
       alert("请选择文件规则！");
       return false;
     }else if(file_url=="")
     {
       alert("请选择文件！");
       return false;
     }else if(validateUploadFilePath(file_url))
     {
        kqCardDataForm.action="/kq/machine/search_card_data.do?b_ftake=link";
        kqCardDataForm.target="mil_body";
        kqCardDataForm.submit();	
     }else{
     	return false;
     }
  }
  function maintenance()
  {
      var num=$F('file_num');  
      if(num==""||num==null)
      {
         alert("请选择考勤文件规则！");
         return false;
      }else
      {
        var target_url;
        var winFeatures = "dialogHeight:450px; dialogLeft:320px;"; 
        target_url="/kq/machine/kq_rule_data.do?b_update=link&rule_id="+num;
        var iWidth = 456;
        var iHeight = 374;
        var iTop = (window.screen.availHeight-30-iHeight)/2; //获得窗口的垂直位置;
        var iLeft = (window.screen.availWidth-10-iWidth)/2; //获得窗口的水平位置;
        newwindow=window.open(target_url,'rr',
                'toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,'
                + 'top='+iTop+',left='+iLeft+',width='+iWidth+',height='+iHeight); 
      }      
  }
</script>

<html:form action="/kq/machine/search_card_data" enctype="multipart/form-data" method="post">  
<br><br><br>
	<table width="500" border="0" cellpmoding="0" cellspacing="0"
		class="DetailTable" cellpadding="0" align="center" valign="middle">
		<tr height="20">

			<td align=center class="TableRow">
				<bean:message key="select.kq.rule.file" />
			</td>
		</tr>
		<tr>
			<td width="100%" class="framestyle9"
				style="border-top-style: solid; border-top-width: 0px;">
				<table>
					<tr>
						<td height="30" align="right">
							&nbsp;
							<bean:message key="kq.rule.case" />
							
						</td>
						<td height="30">

							&nbsp;
							<hrms:optioncollection name="kqCardDataForm" property="file_list"
								collection="list" />
							<html:select name="kqCardDataForm" property="file_num" size="1">
								<html:options collection="list" property="dataValue"
									labelProperty="dataName" />
							</html:select>
							&nbsp;
							<hrms:priv func_id="27061">
								<input type="button" name="b_ftake"
									value='<bean:message key="kq.rule.maintenance"/>'
									onclick="maintenance();" class="mybutton">
							</hrms:priv>
						</td>
						<tr>
							<tr>
								<td align="right">
									&nbsp;
									<bean:message key="kq.rule.text.url" />
									
								</td>
								<td>
									&nbsp;
									<input name="file" type="file" id="file_url" size="20" class="text6">
								</td>
								<tr>
				</table>
			</td>
		</tr>
		<tr>
			<td height="40" align="center" colspan="4">
				<input type="button" name="b_ftake"
					value='<bean:message key="kq.machine.take"/>' onclick="takefile();"
					class="mybutton">
				<input type="button" name="btnreturn"
					value='<bean:message key="kq.emp.button.return"/>'
					onclick="history.back();" class="mybutton">
			</td>
		</tr>
	</table>
</html:form>