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
	if(filetitle.substring(filetitle.length-3).toLocaleLowerCase()=="doc" || filetitle.substring(filetitle.length-4).toLocaleLowerCase()=="docx")
		return true;
	else{
		alert("请选择word文件上传！");
		return false;
	}
}

function upsave() {
	document.policeForm.target='_self';
	if (validate('R','filetitle','标题','R','mediafile','上传文件')){
		if(!(document.returnValue && checkname())){
			return ;
		} else {
			policeForm.action = "/pos/police/team.do?b_save=link";
			policeForm.submit();
		}
	}
}
function upreturn() {
	policeForm.action = "/pos/police/team.do?b_search=link";
	policeForm.submit();
}
</script>
<html:form action="/pos/police/jqdt" enctype="multipart/form-data">
<html:hidden name="policeForm" property="a0100"/>
<table width="500" border="0" cellpadding="0" cellspacing="0" align="center" class="ftable">
    <br>
    <br>
    <tr height="20">
       	
       		<td colspan="2" width=140 align="left"  class="TableRow">&nbsp;<bean:message key="hire.jp.apply.upload"/><logic:equal value="yqdt" name="policeForm" property="cyclename"><bean:message key="police.workinfo.menu.news"/>&nbsp;</logic:equal><logic:notEqual value="yqdt" name="policeForm" property="cyclename"><bean:message key="per.achivement.taskbook"/>&nbsp;</logic:notEqual>
       		</td>
       		             	      
    </tr> 
    <tr>
     
             <td align="right"  nowrap ><bean:message key="general.mediainfo.title"/>&nbsp;</td>
             <td align="left"  nowrap ><html:text name="policeForm" property="filetitle" styleClass="textborder" value="" onkeyup="ValidateValue(this)"/></td>
          </tr>
         <tr>
             <td align="right"  nowrap ><bean:message key="conlumn.mediainfo.filename"/>&nbsp;</td>
             <td align="left"  nowrap ><html:file name="policeForm" property="mediafile" styleClass="textborder"/></td> 
    </tr>
    <tr>
       <td colspan="2" style="height:35px;" align="center">
          	<logic:equal value="team" name="policeForm" property="cyclename">
          		<input name="save" type="button" class="mybutton" value="<bean:message key="button.save"/>" onclick="upsave();"/>
          		<input name="return" type="button" class="mybutton" value="<bean:message key="button.return"/>" onclick="upreturn()"/>
          	</logic:equal>
          	<logic:notEqual value="team" name="policeForm" property="cyclename"> 
	          	<hrms:submit styleClass="mybutton"  property="b_save" onclick="document.policeForm.target='_self';validate('R','filetitle','标题','R','mediafile','上传文件');return (document.returnValue && checkname());">
	                    <bean:message key="button.save"/>
		       	</hrms:submit>    
		        <hrms:submit styleClass="mybutton"  property="b_return">
	                    <bean:message key="button.return"/>
		       	</hrms:submit>
	       	</logic:notEqual>      
        </td>
    </tr>    
  </table>
</html:form>
