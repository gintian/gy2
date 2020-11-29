<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<SCRIPT LANGUAGE="javascript">
    function getArguments(up_base)
    {
    	var up_node,base_id,val;
    	var paraArray=dialogArguments;
    	up_node = paraArray[0];
    	if(up_node==null)
    	   return;
    	base_id=up_node.uid;
    	val=MM_findObj_(up_base);
    	if(val==null)
    	  return;
    	val.value=base_id;
    	//alert(base_id);
    }	
</SCRIPT>  

<base id="mybase" target="_self">
<html:form action="/selfservice/lawbase/law_into_base" enctype="multipart/form-data">
      <table width="300" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr height="20">
       		<!-- <td width=10 valign="top" class="tableft"></td>
       		<td width=130 align=center class="tabcenter">&nbsp;<bean:message key="label.lawfile.repair"/>&nbsp;</td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="300"></td>  -->  
       		<td  align=center class="TableRow">&nbsp;<bean:message key="label.lawfile.repair"/>&nbsp;</td>           	      
          </tr> 
          <tr>
	 	<td class="framestyle9">
               <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0"> 
	 	<tr><td width="60"><bean:message key="lable.lawfile.upfile"/></td>    <td>
	 	
	 	<bean:write name="lawbaseForm" property="lawFileVo.string(name)" filter="true"/>&nbsp;
	 	</td></tr>
	 	</table>     
              </td>
          </tr>
          
          <tr>
            <td colspan="4" class="framestyle">
               <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">     
                      <tr class="list3">
                	  <td  nowrap valign="top" width="60"><bean:message key="lable.lawfile.title"/></td>
                	  <td align="left"  nowrap>
                	      	<html:text name="lawbaseForm" property="lawFileVo.string(title)"/>
                          </td>
                      </tr> 

                 </table>     
              </td>
          </tr>
                    
            <tr>
            <td colspan="4" class="framestyle">
               <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0"> 
	 	<tr><td width="60"><bean:message key="lable.lawfile.typenum"/></td><td><html:text name="lawbaseForm" property="lawFileVo.string(type)"/></td></tr>
	 	
                 </table>     
              </td>
          </tr>
           <tr>
            <td colspan="4" class="framestyle">
               <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0"> 
	 	<tr><td width="60"><bean:message key="lable.lawfile.contenttype"/></td><td><html:text name="lawbaseForm" property="lawFileVo.string(content_type)"/></td></tr>
	 	  </table>     
              </td>
          </tr>
          <tr>
          <td colspan="4" class="framestyle">
               <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0"> 
	 	<tr><td width="60"><bean:message key="lable.lawfile.valid"/></td><td>
	 	<html:select name="lawbaseForm" property="lawFileVo.string(valid)">
				<html:option value="1"><bean:message key="lable.lawfile.availability"/></html:option>
				<html:option value="0"><bean:message key="lable.lawfile.invalidation"/></html:option>
				<html:option value="2"><bean:message key="lable.lawfile.nowmodify"/></html:option>
				<html:option value="3"><bean:message key="lable.lawfile.other"/></html:option>
		  </html:select>      
	 	</td></tr>
	 	</table>     
              </td>
          </tr>
          <tr>
	 	<td colspan="4" class="framestyle">
               <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0"> 
	 	<tr><td width="60"><bean:message key="lable.lawfile.notenum"/></td><td><html:text name="lawbaseForm" property="lawFileVo.string(note_num)"/></td></tr>
	 	</table>     
              </td>
          </tr>
          <tr>
	 	<td colspan="4" class="framestyle">
               <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0"> 
	 	<tr><td width="60"><bean:message key="lable.lawfile.issue_org"/></td><td><html:text name="lawbaseForm" property="lawFileVo.string(issue_org)"/></td></tr>
	 	</table>     
              </td>
          </tr>
          <tr>
	 	<td colspan="4" class="framestyle">
               <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0"> 
	 	<tr><td width="60"><bean:message key="lable.lawfile.note"/></td><td><html:text name="lawbaseForm" property="lawFileVo.string(notes)"/></td></tr>
	 	</table>     
              </td>
          </tr>
          <tr>
	 	<td colspan="4" class="framestyle">
	 	
               <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0"> 
	 	<tr><td width="60"><bean:message key="lable.lawfile.printmandate"/></td><td>	<html:text  property="first_date.year" size="4" maxlength="4"  styleClass="text" /><bean:message key="datestyle.year"/>&nbsp;
            				<html:text  property="first_date.month" size="2" maxlength="2" styleClass="text" /><bean:message key="datestyle.month"/>&nbsp;
            				<html:text  property="first_date.date" size="2" maxlength="2" styleClass="text" /><bean:message key="datestyle.day"/></td></tr>
	 	</table>     
              </td>
          </tr>
          <tr>
	 	<td colspan="4" class="framestyle">
               <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0"> 
	 	<tr><td width="60"><bean:message key="lable.lawfile.actualizedate"/></td><td>	<html:text  property="second_date.year" size="4" maxlength="4"  styleClass="text" /><bean:message key="datestyle.year"/>&nbsp;
            				<html:text  property="second_date.month" size="2" maxlength="2" styleClass="text" /><bean:message key="datestyle.month"/>&nbsp;
            				<html:text  property="second_date.date" size="2" maxlength="2" styleClass="text" /><bean:message key="datestyle.day"/></td></tr>
	 	</table>     
              </td>
          </tr>
          <tr>
	 	<td colspan="4" class="framestyle">
               <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0"> 
	 	<tr><td width="60"><bean:message key="lable.lawfile.invalidationdate"/></td><td>	<html:text  property="third_date.year" size="4" maxlength="4"  styleClass="text" /><bean:message key="datestyle.year"/>&nbsp;
            				<html:text  property="third_date.month" size="2" maxlength="2" styleClass="text" /><bean:message key="datestyle.month"/>&nbsp;
            				<html:text  property="third_date.date" size="2" maxlength="2" styleClass="text" /><bean:message key="datestyle.day"/></td></tr>
	 	</table>     
              </td>
          </tr>
           
                                                   
          <tr class="list3">
            <td align="center" style="height:35px;">
         	<hrms:submit styleClass="mybutton" property="b_save" onclick="validate( 'R','lawFileVo.string(title)','标题','RD','first_date.','实施日期');return (document.returnValue && ifqrbc());">
            		<bean:message key="button.ok"/>
	 	</hrms:submit>
		<html:submit styleClass="mybutton" property="br_return"><bean:message key="button.cancel"/></html:submit>	 	
			 	
            </td>
          </tr>  
          
      </table>
</html:form>
<script language="JavaScript">

 //window.opener.href="/selfservice/lawbase/law_maintenance0.do";
//window.opener.reload();
</script>
