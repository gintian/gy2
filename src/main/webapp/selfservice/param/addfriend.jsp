<%@ page errorPage="error.jsp"%>
<%@ page import="javax.servlet.ServletException"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<SCRIPT   LANGUAGE="JavaScript">   
function chkImgType(){
  var fileObJA=document.getElementsByName("file");
  var obj=fileObJA[0];
  var filename = obj.value;
  var ext = filename.substr(filename.length-3,3).toLowerCase();
  var fObj=document.getElementsByName("b_save"); 
  var fo1=fObj[0]; 
  if(ext== "jpg"||ext == "gif"||ext == "bmp"||filename == ""){    
    return true;
  }else{
    alert("图片不能为空且只能提交jpg或gif,bmp格式文件！")
    obj.focus();    
    return false
  }
}

</SCRIPT>
<html:form action="/selfservice/param/addfriend" enctype="multipart/form-data">
      <br>
     <table width="500" border="0" cellpadding="0" cellspacing="0" align="center" class="ftable">
          <tr height="20">
       		<td  colspan="2" align="left" class="TableRow"><bean:message key="lable.friend.manager"/></td>
          </tr> 
         <tr>
                     <td align="right" nowrap ><bean:message key="selfservice.param.friend.url"/></td>
                     <td><html:text styleClass="text4" name="friendForm" size="50" property="friendvo.string(url)"/></td> 
         </tr>
         <tr>
	                 <td align="right" nowrap ><bean:message key="label.friend.imageup"/></td> 
                      <td align="left"  nowrap><html:file style="width:305px;" name="friendForm" property="file" onchange="chkImgType();"/>&nbsp</td>
         </tr>  
         <tr>
                     <td align="right" nowrap><bean:message key="selfservice.param.friend.name"/></td>
                     <td><html:text styleClass="text4" name="friendForm" size="50" property="friendvo.string(name)"/></td> 
         </tr> 
          <tr >
            <td align="center"  colspan="2" style="height: 35px">
         	<hrms:submit styleClass="mybutton" property="b_save" onclick="document.friendForm.target='_self';validate( 'R','friendvo.string(url)','网址','R','friendvo.string(name)','网站名称');return (document.returnValue && chkImgType() && ifqrbc());">
            		<bean:message key="button.save"/>
	 	    </hrms:submit>
		    <html:reset styleClass="mybutton" property="reset"><bean:message key="button.clear"/></html:reset>	 	
         	<hrms:submit styleClass="mybutton" property="br_return">
            		<bean:message key="button.return"/>
	 	    </hrms:submit>            
            </td>
          </tr>                            
      </table>
</html:form>
