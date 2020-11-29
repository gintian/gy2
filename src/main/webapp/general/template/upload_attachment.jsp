<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<% 
int i = 0;
String ins_id=request.getParameter("ins_id");
String task_id=request.getParameter("task_id");
String sp_batch_temp=request.getParameter("sp_batch_temp");
String infor_type=request.getParameter("infor_type");
String objectid=request.getParameter("objectid");
String basepre=request.getParameter("basepre");
String attachmenttype=request.getParameter("attachmenttype");

if(!"1".equals(sp_batch_temp)){
	sp_batch_temp="0";
	ins_id="0";
	task_id="0";
}
%>
<hrms:themes></hrms:themes>
<style type="text/css"> 
.scroll_box {
    height: 20px;
    width: 100%;      
    overflow: auto;   
   	BORDER-BOTTOM: #C4D8EE 1pt solid;
    BORDER-LEFT: #C4D8EE 1pt solid;
    BORDER-RIGHT: #C4D8EE 1pt solid;
    BORDER-TOP: 0px ;
    border-collapse: collapse;
}
</style>

<script type="text/javascript">
    
function validatefilepath()
{
	var mediapath=document.getElementsByName("content")[0].value;
	return (mediapath.length>2)&&validateUploadFilePath(mediapath);
}
//上传附件
function importfile(ins_id,task_id,sp_batch_temp,infor_type,objectid,basepre,attachmenttype)
{
	var theURL="/general/template/upload_attachment.do?b_select=link`infor_type="+infor_type+"`objectid="+objectid+"`basepre="+basepre+"`attachmenttype="+attachmenttype;
	var iframe_url="/general/query/common/iframe_query.jsp?src="+theURL;
	var objlist =window.showModalDialog(iframe_url,null,"dialogWidth=550px;dialogHeight=210px;resizable=yes;status=no;");  
	if(objlist)
	{
		var str="/general/template/upload_attachment.do?b_query=link&objectid="+objectid+"&attachmenttype="+attachmenttype;
		if(infor_type=='1')
		{
			str+="&basepre="+basepre+"&infor_type=1";
		}
		else if(infor_type=='2')
		{
			str+="&basepre=&infor_type=2";
		}
		else if(infor_type=='3')
		{
			str+="&basepre=&infor_type=3";
		}
		if(sp_batch_temp=="1")
		{
			str+="&ins_id="+ins_id+"&task_id="+task_id+"&sp_batch_temp=1"
		}
		document.templateForm.action=str;
		document.templateForm.submit();
		
	}
	
}
//删除附件
function deletefile(file_id)
{
	if(confirm("确认删除?"))
	{
		var hashvo=new ParameterSet();   	     
 		hashvo.setValue("file_id",file_id);
 		var request=new Request({asynchronous:false,onSuccess:deleteSuccess,functionId:'0570010162'},hashvo);
	}
}
function deleteSuccess(outparamters)
{
	var flag=outparamters.getValue("ok");
	if(flag=='0')
	{
		alert("文件没有删除成功！");
	}
	else
	{
		var ins_id='<%=ins_id %>';
		var task_id='<%=task_id %>';
		var sp_batch_temp='<%=sp_batch_temp %>';
		var infor_type='<%=infor_type %>';
		var objectid='<%=objectid %>';
		var basepre='<%=basepre %>';
		var attachmenttype='<%=attachmenttype %>';
		var str="/general/template/upload_attachment.do?b_query=link&objectid="+objectid+"&attachmenttype="+attachmenttype;
		if(infor_type=='1')
		{
			str+="&basepre="+basepre+"&infor_type=1";
		}
		else if(infor_type=='2')
		{
			str+="&basepre=&infor_type=2";
		}
		else if(infor_type=='3')
		{
			str+="&basepre=&infor_type=3";
		}
		if(sp_batch_temp=="1")
		{
			str+="&ins_id="+ins_id+"&task_id="+task_id+"&sp_batch_temp=1"
		}
		document.templateForm.action=str;
		document.templateForm.submit();
	}
}

</script>

<form action="/general/template/upload_attachment.do" name="templateForm" method="post" enctype="multipart/form-data">
	<br>
	
	<table width="90%" cellpadding="0" cellspacing="0" border="0"> 
		<tr>
			<td>
				<logic:equal name="templateForm" property="uploadattach" value="1">
					<a href= "javascript:importfile('<%=ins_id %>','<%=task_id %>','<%=sp_batch_temp %>','<%=infor_type %>','<%=objectid %>','<%=basepre %>','<%=attachmenttype %>');" >上传附件</a>
				</logic:equal>
			</td>
		</tr>
		
		<tr>
			<td></td>
		</tr>
		
		<tr>
			<td>
				<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable" style="border-collapse: collapse;">
					
					<thead>
						<tr>
						
							<td align="center" class="TableRow" nowrap>
								名 称
							</td>
							<td align="center" class="TableRow" nowrap>
								创建日期
							</td>
							<td align="center" class="TableRow" nowrap>
								创建人
							</td>
							<td align="center" class="TableRow" nowrap >
								操作
							</td>
						</tr>
					</thead>
					
					<logic:iterate  id="element"    name="templateForm"  property="affixList" indexId="index">
			          <%
			          if(i%2==0)
			          {
			          %>
						<tr class="trShallow">
							<%}
			          else
			          {%>
						<tr class="trDeep">
							<%
			          }
			          i++;          
			          %>
							<td align="center" class="RecordRow" nowrap>
								<bean:write name="element" property="attachmentname" filter="true" />
							</td>
							<td align="center" class="RecordRow" nowrap>
			     				<bean:write name="element" property="create_time" filter="true" />
			                    
							</td>
							<td align="center" class="RecordRow" nowrap>
								<bean:write name="element" property="create_user" filter="true" />
							</td>
							<td align="center" class="RecordRow" nowrap >
								<logic:equal name="element" property="candelete" value="1">
								<a href="javascript:deletefile('<bean:write name="element" property="file_id" filter="true" />');" >删除</a>
								</logic:equal>
								<a href="/servlet/AffixDownLoad?ext_file_id=<bean:write name="element" property="file_id" filter="true" />&modeflag=2" target=_blank>查看</a>
							</td>
			          </tr>
					</logic:iterate>
				</table>
			</td>
		</tr>
		
	</table>
</form>

