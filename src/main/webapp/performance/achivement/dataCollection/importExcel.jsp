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
			window.opener.returnValue=thevo;
		}
		window.close();
  	}
    
    function imports()
    {
    	var fileEx = dataCollectForm.file.value;
        if(fileEx == "")
        {
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
    		<logic:notEqual name="dataCollectForm" property="isShowTargetTrace" value="1">
    			jinduo_tiao();
   				document.dataCollectForm.action="/performance/achivement/dataCollection/importExcel.do?b_importData=link&oper=close";
   			</logic:notEqual>	
   			<logic:equal name="dataCollectForm" property="isShowTargetTrace" value="1">
   				document.dataCollectForm.action="/performance/achivement/dataCollection/importExcel.do?b_importData2=link&oper=close";
   			</logic:equal>
  			document.dataCollectForm.submit();
    	}
    	else
    	{
    		alert("<bean:message key='jx.import.error'/>！");
    	}
    }
	
	function jinduo_tiao()
	{
	 	var x=document.body.scrollLeft+80;
	    var y=document.body.scrollTop+50; 
		var waitInfo;
		waitInfo=eval("wait");	
		waitInfo.style.top=y;
		waitInfo.style.left=x;	
		waitInfo.style.display="block";
	}
   </script>
   <body>
	  <form name="dataCollectForm" method="post" action="/performance/achivement/dataCollection/importExcel.do" enctype="multipart/form-data" >
	    <br>
		<br>	
	    <fieldset align="center" style="width:90%;">
	        <legend ><bean:message key='jx.import.selectfile'/></legend>
	        <table border="0" cellspacing="0"  align="center" cellpadding="0" >
	          <tr > 
	            <td> <Br><bean:message key='jx.import.file'/>
	              <input type="file" name="file" styleClass="text6" >
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
		
		<div id='wait' style='position:absolute;top:160;left:250;display:none;'>
	  		<table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
	           <tr>
	             <td id="wait_desc" class="td_style" height=24>正在导入数据，请稍候....</td>
	           </tr>
	           <tr>
	             <td style="font-size:12px;line-height:200%" align=center>
	               <marquee class="marquee_style" direction="right" width="260" scrollamount="5" scrolldelay="10" >
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
		</div>
		
	  </form>
   </body>
</html>
