<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html>
  <head>
    
  </head>
  <script language='javascript' >
  	if("${param.oper}"=='close')
  	{
  	// 多浏览器兼容回调 chent 20171227 add
  		if(parent && parent.parent && parent.parent.importTarget_ok){
	  		parent.parent.importTarget_ok(true);
  		} else {
  			var thevo=new Object();
			thevo.flag="true";
			window.returnValue=thevo;
			window.close();
  		}
  	}
    
    function imports()
    {
    	var fileEx = decPerformanceForm.file.value;
        if(fileEx == ""){
        	alert("<bean:message key='jx.import.select'/>！");
        	return ;
        }
       
        // 防止上传漏洞
		var isRightPath = validateUploadFilePath(fileEx);
		if(!isRightPath)	
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
    		if(confirm('您确定要导入'+JX_KHPLAN_TARGETCARD+'吗？此操作会将当前'+JX_KHPLAN_TARGETCARD+'中的数据清除!'))
			{
   				document.decPerformanceForm.action="/performance/objectiveManage/objectiveDecision/dec_performance_list.do?b_importData=link&oper=close";
  				document.decPerformanceForm.submit();
  			}
    	}
    	else
    	{
    		alert("<bean:message key='jx.import.error'/>！");
    	}
    }

  </script>
  <body>
  <form name="decPerformanceForm" method="post" action="/performance/objectiveManage/objectiveDecision/dec_performance_list.do" enctype="multipart/form-data" >
    <br>
	<br>	
    <fieldset align='center' style="margin:0 auto;width:90%;">
        <legend ><bean:message key='jx.import.selectfile'/></legend>
        <table border="0" cellspacing="0"  align="center" cellpadding="0" >
          <tr > 
            <td> <Br><bean:message key='jx.import.file'/>
              <input type="file" name="file" class="text6" style='height:25px;width:350px;'>
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
            <td align="center"> <input type="button" name="b_update" value='<bean:message key="button.import"/>' class="mybutton" onClick="imports()"> 
		
               </td>
          </tr>
	
	</table>
  </form>
  </body>
</html>
