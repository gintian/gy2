<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.kq.team.KqShiftForm"%>
<% KqShiftForm kqShiftForm = (KqShiftForm)session.getAttribute("kqShiftForm"); %>
<script language="JavaScript" src="/js/meizzDate.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="javascript">
  function takefile()
  {
     var thevo=new Object();
     var Obj=document.getElementById("file_url");
     var file_url=Obj.value;     
     if(file_url=="")
     {
       alert("请选择文件！");
       return false;
     }else if(!validateUploadFilePath(file_url)){
    	 alert("请上传正确的文件！");
         return false;
     }else
     {
        var indexInt = file_url.lastIndexOf(".");            
		var ext = file_url.substring(indexInt + 1, file_url.length);
        if(ext!="xls" && ext!="xlsx")
        {
          alert("上传文件类型不正确，请确认是Excel文件！");
          return false;
        }

        document.getElementById("importBtn").disabled = "disabled";
        document.getElementById("closeBtn").disabled = "disabled";
        document.getElementById("waiting").style.display = "block";
        kqShiftForm.action="/kq/team/array/excel_shift_data.do?b_input=link";
        kqShiftForm.submit();
     }
  }

</script>

<html:form action="/kq/team/array/excel_shift_data" enctype="multipart/form-data" method="post">  
<div class="fixedDiv3" >
 <table width="100%" border="0" cellpmoding="0" cellspacing="0" cellpadding="0" align="center" valign="middle" >   
     <tr height="20">
        <td align=center class="TableRow">Excel排班文件</td>       		           	      
     </tr>                                         
     <tr>
	    <td width="100%"   class="framestyle9">
	    <table>
     	    <tr>
	          <td>
	            &nbsp;<bean:message key="kq.rule.text.url"/>&nbsp;		                	     
	   	      </td>
	   	      <td height='100px'>		                	      
	   	        &nbsp;
	   	        <input name="file" type="file" id="file_url" size="40" class="text4">
	   	     </td>
	   	    <tr>	                	
	    </table>		                  
	  </td>		            
	</tr>		             	
      <tr>
         <td height="35" align="center">		                
            <input type="button" id="importBtn" name="b_ftake" value='确定' onclick="takefile();" class="mybutton">
            <input type="button" id="closeBtn" name="btnreturn" value='关闭' onclick="window.close();" class="mybutton">						      
          </td>
      </tr>
    </table>    
    </div>            
</html:form>
<div id='waiting' style='position:absolute;top:70;left:20;display:none;'>
  <table border="1" width="400" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
      <tr>
        <td class="td_style common_background_color" height=24>正在导入排班数据，请稍候...</td>
      </tr>
      <tr>
        <td style="font-size:12px;line-height:200%" align=center>
          <marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10">
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
<logic:equal name="kqShiftForm" property="checkClose" value="close">
<script language="JavaScript">
var thevo=new Object();
thevo.flag="true";
window.returnValue=thevo;
window.close();
</script>
</logic:equal>