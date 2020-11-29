
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="java.util.*"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<hrms:themes />
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script type="text/javascript">
	var urlParams1 = '&returnFlag=${taxTableForm.returnFlag}&theyear=${taxTableForm.theyear}&themonth=${taxTableForm.themonth}&operOrg=${taxTableForm.operOrg}';
function init()
{
    var error = document.getElementById("error").value
    if(error=="2")
    {
    	alert('缺乏"所得项目","税率","人数",这三个必须导出的指标,无法上传');
    }
    if(error=="3")
    {
    	alert("文件已存在,请更换文件名");
    }	
	document.getElementById("error").value="0";
}
function to_cancel()
{
	taxTableForm.action="/gz/gz_accounting/tax/search_tax_table.do?b_query=link&a_code=${taxTableForm.a_code}&init=date"+urlParams1;
	taxTableForm.submit();	
}

function to_upload()
{
	 var fileEx = taxTableForm.tempalefile.value;
	 if(fileEx == "")
	 {
    	alert("请选择文件!");
    	return ;
     }
     if(!validateUploadFilePath(fileEx))
           return;
    if(fileEx.substring(fileEx.length-3)=="xls" || fileEx.substring(fileEx.length-4)=="xlsx" || fileEx.substring(fileEx.length-3)=="xlt" )
    {
    	taxTableForm.action="/gz/gz_accounting/tax/tax_export_template.do?b_upload=link";
		taxTableForm.submit();
    }else{
	    alert("文件类型不对,无法上传,请选择Excel文件");
    }
		
}
function to_delete()
{
	if(ifdel())
	{
		taxTableForm.action="/gz/gz_accounting/tax/tax_export_template.do?b_delete=link";
		taxTableForm.submit();
	}	
}
function to_export()
{
	var  path =  document.getElementById("path").value;		
	var  templateName =  document.getElementById("templateName").value;	
	var  declaredate =  document.getElementById("declaredate").value;	
	var  a_code = '${taxTableForm.a_code}';	
	var  condtionsql = document.getElementById("condtionsql").value;
	var filterByMdule=document.getElementById("filterByMdule").value;	
	var fromTable=document.getElementById("tax_tablename").value;	
	var hashvo=new ParameterSet();
	hashvo.setValue("declaredate",declaredate);
	hashvo.setValue("a_code",a_code);
	hashvo.setValue("condtionsql",condtionsql);
	hashvo.setValue("path",path);
	hashvo.setValue("templateName",templateName);
	hashvo.setValue("filterByMdule",filterByMdule);
	hashvo.setValue("fromtable",fromTable);
	var In_paramters="flag=1"; 	
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showfile,functionId:'3020091008'},hashvo);
}
function showfile(outparamters){
	var outName=outparamters.getValue("outName");
	var fieldName = getDecodeStr(outName);
	var win=open("/servlet/vfsservlet?fileid="+fieldName+"&fromjavafolder=true","excel");
}
</script>

<form name="taxTableForm" method="post" action="/gz/gz_accounting/tax/tax_export_template.do" enctype="multipart/form-data" >
<html:hidden name="taxTableForm" property="error"/>
<html:hidden name="taxTableForm" property="declaredate"/>
<html:hidden name="taxTableForm" property="path"/>
<html:hidden name="taxTableForm" property="condtionsql"/>
<html:hidden name="taxTableForm" property="filterByMdule"/>
<html:hidden name="taxTableForm" property="tax_tablename"/>

<table  border="0" cellpadding="0" cellspacing="0" align="center" class="ListTable" style="width:700px;margin-top:60px;">
	<thead>
	<tr>
	<td  class="TableRow" align="left"  colspan=2  nowrap>
		<bean:message key="gz_tax_count_template"/>
	</td>
	
	</tr>
	</thead>
	
	<hrms:extenditerate id="element" name="taxTableForm" property="recordListForm.list" indexes="indexes"  pagination="recordListForm.pagination" pageCount="30" scope="session">
  	<tr>
  	<td align="center" class="RecordRow" width="15%" nowrap>
		<hrms:checkmultibox name="taxTableForm" property="recordListForm.select"  value="ture" indexes="indexes"/>&nbsp;
  	</td>
  	<td align="left" class="RecordRow" nowrap>
  	&nbsp;
		<bean:write  name="element" property="filename" filter="true"/>&nbsp;	
  	</td>
  	</tr>
	</hrms:extenditerate>

	<tr>
	<td  class="RecordRow" align="right" nowrap>
		&nbsp;<bean:message key="gz_tax_template_file"/>&nbsp;
	</td>
	<td  class="RecordRow" align="left" nowrap>
	&nbsp;
		<input type="file" name="tempalefile" size="15" class="inputtext" >
		
	</td>	
	</tr>
	
	<tr>
	<td  class="RecordRow" align="right" nowrap>
		&nbsp;<bean:message key="gz_tax_apply_template"/>&nbsp;
	</td>
	<td  class="RecordRow" align="left" style="padding-top: 2px;" nowrap>
	&nbsp;
		<hrms:optioncollection name="taxTableForm" property="templateList" collection="list" />
	    <html:select name="taxTableForm" property="templateName" size="1" >
	    <html:options collection="list" property="filename" labelProperty="filename"/>
	    </html:select>
	    &nbsp;
		<hrms:priv func_id="3240407">    	    
			<html:button styleClass="mybutton" property="apply" onclick="to_export();">
		  		<bean:message key="sys.export.derived"/>
			</html:button>
		</hrms:priv> 
		
		<hrms:priv func_id="3240407"> 
			<html:button styleClass="mybutton" property="apply" onclick="to_upload();">
		  		<bean:message key="lable.fileup"/>
			</html:button>
		</hrms:priv> 	
		
		<hrms:priv func_id="3240407"> 
			
			<html:button styleClass="mybutton" property="apply" onclick="to_delete();">
		  		<bean:message key="lable.tz_template.delete"/>
			</html:button>          	    
		</hrms:priv> 	
			
		<html:button styleClass="mybutton" property="apply" onclick="to_cancel();">
	  		<bean:message key="button.return"/>
		</html:button>
	    	
	</td>	
	</tr>
	
	
</table>
<table width="51%" border="0" cellpadding="0" cellspacing="0" align="center" class="ListTable">
	
</table>
</form>
<script type="text/javascript">
init();
</script>