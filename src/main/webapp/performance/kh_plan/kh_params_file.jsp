<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<hrms:themes />
<script>
	if("${param.oper}"=="close")
	{
			var thevo=new Object();
			thevo.flag="true";
        parent.window.returnValue=thevo;
			alert("调入成功！");
        if(window.showModalDialog) {
            parent.window.close();
        }else{
            window.top.opener.inputFile_window_ok(thevo);
            window.open("about:blank","_top").close();
        }
	}
    function upload() 
    { 
        var fileEx = examPlanForm.file.value;
        if(fileEx == "")
        {
        	alert("<bean:message key='jx.khplan.upload'/>");
        	return;
        }
        
        // 防止上传漏洞
		var isRightPath = validateUploadFilePath(fileEx);
		if(!isRightPath)	
			return;
        
        examPlanForm.action="/performance/kh_plan/kh_params_file.do?b_save=link&plan_id=${param.plan_id}&oper=close";
        examPlanForm.target="_self";
		examPlanForm.submit();
            
    }  
</script>
<html>
<hrms:themes />
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>HRPWEB3</title>
	</head>
	<body>
		<form name="examPlanForm" method="post"
			action="/performance/kh_plan/kh_params_file" enctype="multipart/form-data">
			<br>
			 <fieldset align="center" style="width:90%;height:80px;">
        <legend ><bean:message key='jx.import.selectfile'/></legend>
        <table border="0" cellspacing="0"  align="center" cellpadding="0" >
          <tr > 
            <td> <Br><bean:message key='jx.import.file'/>
              <input type="file" name="file" class="text6" style="height:25px;">
              <br>              
              </td>
          </tr>	  
        </table>
	</fieldset>
	<table width="90%" align="center">
		     <tr > 
            <td> </td>
          </tr>	
		  <tr> 
            <td align="center"> <input type="button" name="b_update" value='<bean:message key="lable.fileup"/>' class="mybutton" onClick="upload() "> 
		
               </td>
          </tr>
	
	</table>
			</form>
			
					
	</body>
</html>
