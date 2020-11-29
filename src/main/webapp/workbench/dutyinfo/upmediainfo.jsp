<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html:form action="/workbench/duty/upmediainfo" enctype="multipart/form-data">
<table width="500" border="0" cellpadding="0" cellspacing="0" align="center" class="ftable">
    <br>
    <br>
    <tr height="20">
       		<!--  <td width=10 valign="top" class="tableft"></td>
       		<td width=140 align=center class="tabcenter">&nbsp;<bean:message key="conlumn.mediainfo.titleinfo"/>&nbsp;</td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="500"></td>-->  
       		<td width=140 align="left" colspan="2" class="TableRow">&nbsp;<bean:message key="conlumn.mediainfo.titleinfo"/>&nbsp;</td>            	      
    </tr> 
    <tr>
            <td align="right"  nowrap><bean:message key="conlumn.mediainfo.info_sort"/>&nbsp;</td>
            <td align="left"  nowrap ><hrms:importgeneraldata showColumn="sortname" valueColumn="flag" flag="false" paraValue="" 
                                      sql="${dutyInfoForm.sortcond}" collection="list" scope="page"/>
                <html:select name="dutyInfoForm" property="filesort" size="1">
                	<html:options collection="list" property="dataValue" labelProperty="dataName"/>
              	</html:select>&nbsp;
            </td>
         </tr>
          <tr>
             <td align="right"  nowrap ><bean:message key="conlumn.mediainfo.info_title"/>&nbsp;</td>
             <td align="left"  nowrap ><html:text name="dutyInfoForm" property="filetitle" styleClass="textborder" value=""/></td>
          </tr>
         <tr>
             <td align="right"  nowrap ><bean:message key="conlumn.mediainfo.filename"/>&nbsp;</td>
             <td align="left"  nowrap ><html:file name="dutyInfoForm" property="mediafile" styleClass="textborder"/></td>
         </tr>        
    <tr>
       <td  nowrap colspan="2" align="center" style="height:35px;">
               <hrms:submit styleClass="mybutton"  property="b_save" onclick="document.dutyInfoForm.target='_self';validate('R','filetitle','标题','R','mediafile','上传文件');return document.returnValue;">
                    <bean:message key="button.save"/>
	       </hrms:submit>   
	        <hrms:submit styleClass="mybutton"  property="b_clear">
                    <bean:message key="button.clear"/>
	       </hrms:submit>    
	        <hrms:submit styleClass="mybutton"  property="b_return">
                    <bean:message key="button.return"/>
	       </hrms:submit>      
        </td>
    </tr>    
  </table>
</html:form>
