<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript">
 function   ValidateValue(textbox) 
                { 
                          var   IllegalString ="\`~!#$%^&*()+{}|\\:\"<>?-=/,\'"; 
                          var   textboxvalue=   textbox.value; 

                          var   index= textboxvalue.length-1; 
                          
                          var   s   =   textbox.value.charAt(index); 
                          
                          if(IllegalString.indexOf(s)>=0) 
                          { 
                                s   =   textboxvalue.substring(0,index); 
                                textbox.value   =   s; 
                          } 
                } 
function checkname()
{
	var filetitle = document.getElementsByName("mediafile")[0].value;
	if(filetitle.substring(filetitle.length-3).toLocaleLowerCase()=="doc"||filetitle.substring(filetitle.length-4).toLocaleLowerCase()=="docx")
		return true;
	else{
		alert("请选择word文件上传！");
		return false;
	}
}
</script>
<html:form action="/pos/roleinfo/pos_dept_post" enctype="multipart/form-data">
<table width="500" border="0" cellpadding="0" cellspacing="0" align="center" class="ftable">
    <br>
    <br>
    <tr height="20">
       		<td width=140 align="left" colspan="2" class="TableRow">&nbsp;<bean:message key="hire.jp.apply.upload"/><logic:equal value="L" name="posRoleInfoForm" property="modular"><bean:message key="police.workinfo.lianzhengfengxian"/></logic:equal><logic:notEqual value="L" name="posRoleInfoForm" property="modular"><bean:message key="hire.parameterSet.positionDescrible"/></logic:notEqual>&nbsp;</td>            	      
    </tr> 
    <tr>
             <td align="right"  nowrap ><bean:message key="general.mediainfo.title"/>&nbsp;</td>
             <td align="left"  nowrap ><html:text name="posRoleInfoForm" property="filetitle" styleClass="textborder" value="" onkeyup="ValidateValue(this)"/></td>
          </tr>
         <tr>
             <td align="right"  nowrap ><bean:message key="conlumn.mediainfo.filename"/>&nbsp;</td>
             <td align="left"  nowrap ><html:file name="posRoleInfoForm" property="mediafile" styleClass="textborder"/></td> 
    </tr>
    <tr>
       <td  nowrap colspan="2" align="center" style="height:35px;">
               <hrms:submit styleClass="mybutton"  property="b_save" onclick="document.posRoleInfoForm.target='_self';validate('R','filetitle','标题','R','mediafile','上传文件');return (document.returnValue && checkname());">
                    <bean:message key="button.save"/>
	       </hrms:submit>    
	        <hrms:submit styleClass="mybutton"  property="b_return">
                    <bean:message key="button.return"/>
	       </hrms:submit>      
        </td>
    </tr>    
  </table>
</html:form>
