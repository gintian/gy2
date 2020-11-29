<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/module/utils/js/template.js"></script>
<script language="JavaScript" src="/competencymodal/postseq_commodal/postmodal.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<html:form action="/competencymodal/postseq_commodal/post_modal_list">
<html:hidden name="postModalForm" property="object_type"/>
<html:hidden name="postModalForm" property="codesetid"/>
<html:hidden name="postModalForm" property="codeitemid"/>
<html:hidden name="postModalForm" property="historyDate"/>

<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
<thead>
<tr>
<td align="center" class="TableRow" width="4%" nowrap><input type="checkbox" name='allSelect' onclick="selectAll(this);"/></td>
<td align='center' class='TableRow' width="12%" nowrap>

 <logic:equal value="3" name="postModalForm" property="object_type">
               岗位名称
               </logic:equal>
               <logic:equal value="1" name="postModalForm" property="object_type">
               职务体系
               </logic:equal>
               <logic:equal value="2" name="postModalForm" property="object_type">
               <bean:message key="competency.modal.postseq"/>
               </logic:equal>
</td>
<td align='center' class="TableRow" width="10%" nowrap>指标分类</td>
<td align='center' class='TableRow' width="10%" nowrap><bean:message key="competency.modal.fieldcode"/></td>
<td align="center" class="TableRow" width="35%" nowrap><bean:message key="kh.field.field_n"/></td>
<td align='center' class='TableRow' width="5%" nowrap><bean:message key="competency.modal.score"/></td>
<td align='center' class='TableRow' width="5%" nowrap><bean:message key="label.kh.template.qz"/></td>
<td align='center' class='TableRow' width="150" nowrap><bean:message key="jx.param.degreepro"/></td>
<td align='center' class='TableRow' width="5%" nowrap><bean:message key="kh.field.edit"/></td>
</tr>
</thead>

 	 <hrms:extenditerate id="element" name="postModalForm" property="postModalListForm.list" indexes="indexes"  pagination="postModalListForm.pagination" pageCount="20" scope="session">
         <tr>
              <td align="center" class="RecordRow" width="4%" nowrap>
              <logic:equal value="1" name="postModalForm" property="isoper">
              <logic:equal value="0" name="element" property="isParent">
              <input type="checkbox" name="idArray" value="<bean:write name="element" property="idArray"/>"/>
            </logic:equal>
            </logic:equal>
            <logic:equal value="1" name="element" property="isParent">
    &nbsp;</logic:equal>
         </td>
               <td align="center" class="RecordRow" width="12%" nowrap>

               &nbsp;<bean:write name="element" property="postname"/>
         </td>
          <td align="left" class="RecordRow" width="10%" nowrap>
         &nbsp;<bean:write name="element" property="codeitemdesc"/>&nbsp;
         </td>
         <td align="left" class="RecordRow" width="10%" nowrap>
         &nbsp;<bean:write name="element" property="point_id"/>&nbsp;
         </td>
         <td align="left" class="RecordRow" width="15%" >
         &nbsp;<bean:write name="element" property="pointname"/>&nbsp;
         </td>
         <td align="right" class="RecordRow" width="5%" nowrap>
         &nbsp;<bean:write name="element" property="score"/>&nbsp;
         </td>
         <td align="right" class="RecordRow" width='5%' nowrap>

        &nbsp;<bean:write name="element" property="rank"/>&nbsp;
         </td>
         <logic:equal value="1" name="element" property="ishave">
        <td align="left" class="RecordRow" width='150' onclick="showFulldesc(this,'<bean:write name="element" property="fulldesc"/>');" onmouseout="hidden();" >

        &nbsp;<bean:write name="element" property="gradedesc"/>
         </td>
        </logic:equal>
         <logic:equal value="0" name="element" property="ishave">
        <td align="left" class="RecordRow" width='150'>
        &nbsp;<bean:write name="element" property="gradedesc"/>
         </td>
        </logic:equal>
         <td align="center" class="RecordRow" width='5%' nowrap>
         <logic:equal value="1" name="postModalForm" property="isoper">
          <logic:equal value="0" name="element" property="isParent">
  <a href='javascript:editPostModal("${postModalForm.object_type}","<bean:write name="element" property="object_id"/>","${postModalForm.codesetid}","<bean:write name="element" property="point_id"/>","${postModalForm.codeitemid}");'>  <img src="/images/edit.gif" border='0'></a>
    </logic:equal>
    </logic:equal>
    <logic:equal value="1" name="element" property="isParent">
    &nbsp;</logic:equal>
         </td>
         </tr>
	</hrms:extenditerate>
<tr>
<td colspan="9" class="RecordRow">
    <table  width="100%" align="center">
		<tr>
		   <td valign="bottom" class="tdFontolor" nowrap>第
		   <bean:write name="postModalForm" property="postModalListForm.pagination.current" filter="true"/>
		   页
		   共
		   <bean:write name="postModalForm" property="postModalListForm.pagination.count" filter="true"/>
		   条
		   共
		   <bean:write name="postModalForm" property="postModalListForm.pagination.pages" filter="true"/>
		   页
		   </td>
		   <td align="right" class="tdFontcolor" nowrap>
		   <p align="right">
		   <hrms:paginationlink name="postModalForm" property="postModalListForm.pagination" nameId="postModalListForm" propertyId="postModalListProperty">
		   </hrms:paginationlink>
		   </td>
		</tr>
</table>
</td>
</tr>
<tr>
<td colspan="9" align="center" style="padding-top:3px;">
<logic:equal value="1" name="postModalForm" property="isoper">
<input type='button' name='new' value='<bean:message key="lable.tz_template.new"/>' class='mybutton' onclick='newPostModal("${postModalForm.object_type}","${postModalForm.codeitemid}","${postModalForm.codesetid}");'>
<input type='button' name='delete' value='<bean:message key="lable.tz_template.delete"/>' class='mybutton' onclick='del("${postModalForm.object_type}","${postModalForm.codeitemid}","${postModalForm.codesetid}");'>
<logic:equal value="3" name="postModalForm" property="object_type">
<input type='button' name='imp_post' value='引用岗位序列模型' class='mybutton' onclick='importModal("1","${postModalForm.object_type}","${postModalForm.codeitemid}","${postModalForm.codesetid}","${postModalForm.historyDate}");'>
<input type='button' name='imp_pos' value='引用职务序列模型' class='mybutton' onclick='importModal("2","${postModalForm.object_type}","${postModalForm.codeitemid}","${postModalForm.codesetid}","${postModalForm.historyDate}");'>
</logic:equal>
</logic:equal>
<input type='button' name='exp' value='<bean:message key="lable.kh.template.importcurrente"/>' class='mybutton' onclick='expOut("${postModalForm.object_type}","${postModalForm.codeitemid}","${postModalForm.codesetid}","${postModalForm.historyDate}");'>
<hrms:tipwizardbutton flag="capability" target="il_body" formname="postModalForm"/>
</td>
</tr>
</table>
<div id="date_panel">
</div>
</html:form>