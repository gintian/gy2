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
  	  	var thevo=new Object();
		thevo.flag="true";
		if(window.showModalDialog){
            parent.window.returnValue=thevo;
		}else{
		    window.opener.returnValue =thevo;
		}
		window.close();
  	}
    
    function imports()
    {
    	var fileEx = kpiOriginalDataForm.file.value;
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
//    		if(confirm('您确定要导入吗？此操作会将当前的数据清除!'))
			{
   				document.kpiOriginalDataForm.action="/performance/achivement/kpiOriginalData/kpiOriginalDataList.do?b_importData=link&oper=close";
  				document.kpiOriginalDataForm.submit();
  			}
    	}
    	else
    	{
    		alert("<bean:message key='jx.import.error'/>！");
    	}
    }

  </script>
  <body>
  <form name="kpiOriginalDataForm" method="post" action="/performance/achivement/kpiOriginalData/kpiOriginalDataList.do" enctype="multipart/form-data" >
    <br>
	<br>	
    <fieldset align="center" style="width:90%;">
        <legend ><bean:message key='jx.import.selectfile'/></legend>
        <table border="0" cellspacing="0" align="center" cellpadding="0">
          <tr > 
            <td> <Br><bean:message key='jx.import.file'/>
              <input type="file" name="file" class="text6" >
              </br></br>         
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
