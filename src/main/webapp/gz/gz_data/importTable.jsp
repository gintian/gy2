<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.gz.gz_data.*"%>
<%
	SalaryDataForm salaryDataForm=(SalaryDataForm)session.getAttribute("salaryDataForm"); 
	String returnFlag = salaryDataForm.getReturnFlag();
	String isLeafOrg = salaryDataForm.getIsLeafOrg();
	String isAllDistri = salaryDataForm.getIsAllDistri();
	String isOnlyLeafOrgs = salaryDataForm.getIsOnlyLeafOrgs();
	String isOrgCheckNo = salaryDataForm.getIsOrgCheckNo();
%>
<html>
	<script language='javascript'>
	var theyear = '${salaryDataForm.theyear}';
	var themonth = '${salaryDataForm.themonth}';
	var operOrg = '${salaryDataForm.operOrg}';
	var isAllDistri='<%=isAllDistri%>';
	var isLeafOrg='<%=isLeafOrg%>';
	var returnFlag='<%=returnFlag%>';
	var salaryid = '${salaryDataForm.salaryid}';
	var isOnlyLeafOrgs = '<%=isOnlyLeafOrgs%>';
	var isOrgCheckNo = '<%=isOrgCheckNo%>';
	var url='&returnFlag='+returnFlag+"&salaryid="+salaryid+"&theyear="+theyear+"&themonth="+themonth+"&orgcode="+operOrg+"&isleafOrg="+isLeafOrg+"&isAllDistri="+isAllDistri+"&isOnlyLeafOrgs="+isOnlyLeafOrgs+"&isOrgCheckNo="+isOrgCheckNo;
	function goback1()
	{
		//document.salaryDataForm.action="/gz/gz_data/gz_table.do?b_query=link&salaryid=${salaryDataForm.salaryid}";	   	
		document.salaryDataForm.action="/gz/gz_data/gz_table.do?b_query=link"+url;	   		 	 
		document.salaryDataForm.submit();
	}    
    function imports()
    {
    	var fileEx = salaryDataForm.file.value;
        if(fileEx == "")
        {
        	alert("请选择需导入的文件!");
        	return ;
        }
        if(!validateUploadFilePath(fileEx))
           return;
       	flag=true;
		var temp=fileEx;
		while(flag)
    	{
	    	temp=temp.substring(temp.indexOf(".")+1)
	    	if(temp.indexOf(".")==-1)
	    		flag=false;
    	}
    	if(temp.toLowerCase()=='xls' || temp.toLowerCase()=='xlsx')
    	{    	
			//document.salaryDataForm.action="/gz/gz_data/gz_table.do?b_getTemplData=get&oper=fafang&salaryid=<%=request.getParameter("salaryid")%>";
			document.salaryDataForm.action="/gz/gz_data/gz_table.do?b_getTemplData=get&oper=fafang"+url;
  			document.salaryDataForm.submit();			 		
    	}
    	else
    	{
    		alert("导入的文件必须为excel格式");
    	}
    }
  </script>
	<body>
		<form name="salaryDataForm" method="post" action="/gz/gz_data/gz_table.do" enctype="multipart/form-data">

			<table border="0" cellspacing="0" align="center" cellpadding="0" style="width:50%;margin-top:60px;">
					<tr>
						<td  align="center">
							<font size=2>说明：请用下载的Excel模板来导入数据！模板格式不允许修改！</font>
						</td>
					</tr>
			</table>  

			<fieldset align="center" style="width:50%;">
				<legend>
					选择导入文件
				</legend>
				<table border="0" cellspacing="0" align="center" cellpadding="0">
					<tr>
						<td width="400">
							<Br>
							文件
							<input type="file" name="file" size="40" class="inputtext">
							<br>
							<br>

						</td>
					</tr>
					<tr>
						<td>
							&nbsp;
						</td>
					</tr>					
				</table>
			</fieldset>

			<table border="0" cellspacing="0" align="center" cellpadding="0" style="width:50%;">
					<tr>
						<td align="center" height="35px;">
							<input type="button" name="b_update" value="上传" class="mybutton"
								onClick="imports()">
							<input type="button" name="b_update" value="返回" class="mybutton"
								onClick="goback1()">
						</td>
					</tr>
			</table> 
          <html:hidden name="salaryDataForm" property="proright_str"/> 
          <html:hidden name="salaryDataForm"  property="itemid"/>         
         				   
		</form>
	</body>
</html>
