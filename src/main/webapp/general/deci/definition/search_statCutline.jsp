<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="com.hrms.frame.dao.RecordVo"%>
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<hrms:themes></hrms:themes>
<style>

.ListTable_self {
    BACKGROUND-COLOR: #FFFFFF;
    BORDER-BOTTOM: medium none; 
    BORDER-COLLAPSE: collapse; 
    BORDER-LEFT: medium none; 
    BORDER-RIGHT: medium none; 
    BORDER-TOP: medium none; 
    
 }
 </style>


<%int i = 0;

			%>
<script language="javascript">
 
   function change()
   {
      statCutlineForm.action="/general/deci/definition/statCutline/searchStatCutline.do?b_query=link";
      statCutlineForm.submit();
   }
   
   function checkdelete(){
			var str="";
			for(var i=0;i<document.statCutlineForm.elements.length;i++)
			{
				if(document.statCutlineForm.elements[i].type=="checkbox")
				{
					
					if(document.statCutlineForm.elements[i].checked==true)
					{
						str+=document.statCutlineForm.elements[i+1].value+"/";
					}
				}
			}
			if(str.length==0)
			{
				alert("请选择指标！");
				return false;
			}else{
				if(window.confirm("您确认要删除所选记录？")){
				    document.forms[0].submit();
				    return true;
				}else
					return false;
			}
	  }
   
   
   function searchItemtype()
   {
   		statCutlineForm.action="/general/deci/definition/statCutline/searchItemtype.do?b_query=link";      
        statCutlineForm.submit();
   }
   function binit()
   {
      var sv="";
      var obj=document.getElementsByName("typeid");    
      var vos=obj[0];       
      if(vos==null)
      {
         alert("统计图例分类没有指标，请添加！");
        return false;
      }
      
      for(var i=0;i<vos.options.length;i++)
      { 
        if(vos.options[i].selected)
        {
          sv=vos.options[i].value;
        }
      }
      if(sv!="")
      {
        statCutlineForm.action="/general/deci/definition/statCutline/searchStatCutline.do?b_init=link";
        statCutlineForm.submit();
      }else
      {
        alert("请选择统计图例分类");
      
      }
   }   
</script>

<html:form action="/general/deci/definition/statCutline/searchStatCutline">
<table width="85%" border="0" cellspacing="0" align="center" cellpadding="0" style="margin-top:6px;">
 <tr>
  <td>
     <table width="100%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
		<tr >
			<td align="left" nowrap colspan="6" style="padding-bottom:5px;">
				<bean:message key="general.defini.object" />
				&nbsp;
				<hrms:optioncollection name="statCutlineForm" property="objectList" collection="list" />
				<html:select name="statCutlineForm" property="object" size="1" onchange="change();">
					<html:options collection="list" property="dataValue" labelProperty="dataName" />
				</html:select>
				&nbsp;
				<bean:message key="general.defini.statCutlineSort" />
				&nbsp;
				<hrms:optioncollection name="statCutlineForm" property="typeList" collection="list" />
				<html:select name="statCutlineForm" property="typeid" size="1" onchange="change();">
					<html:options collection="list" property="dataValue" labelProperty="dataName" />
				</html:select>
				&nbsp;

				<html:button styleClass="mybutton" property="br_handworkSelect" onclick="searchItemtype()">
					<bean:message key="kq.search_feast.modify" />
				</html:button>

			</td>
		</tr>
		<tr>
			<td align="center" class="TableRow" nowrap>
            <input type="checkbox" name="selbox" onclick="batch_select(this,'statCutlinelistform.select');" title='<bean:message key="label.query.selectall"/>'>	    
			</td>
			<td align="center" class="TableRow" nowrap>
				<bean:message key="kq.item.name" />
			</td>
			<td align="center" class="TableRow" nowrap>
				<bean:message key="general.defini.xname" />
			</td>
			<td align="center" class="TableRow" nowrap>
				<bean:message key="general.defini.code" />
			</td>
			<td align="center" class="TableRow" nowrap>
				<bean:message key="general.defini.keySeci" />
			</td>
			<td align="center" class="TableRow" nowrap>
				<bean:message key="kq.item.edit" />
			</td>
		</tr>
		<hrms:extenditerate id="element" name="statCutlineForm" property="statCutlinelistform.list" indexes="indexes" pagination="statCutlinelistform.pagination" pageCount="10" scope="session">
			<%if (i % 2 == 0) {

			%>
			<tr class="trShallow">
				<%} else {%>
			<tr class="trDeep">
				<%}
			i++;

		%>
				<td align="center" class="RecordRow" nowrap>
					<hrms:checkmultibox name="statCutlineForm" property="statCutlinelistform.select" value="true" indexes="indexes" />
				</td>
				<td align="left" class="RecordRow" nowrap>
					&nbsp;<bean:write name="element" property="string(itemname)" filter="true" />
					&nbsp;
				</td>
				<td align="left" class="RecordRow" nowrap>

					<hrms:fieldtoname name="element" fieldname="string(field_name)" fielditem="fielditem" />
					&nbsp;<bean:write name="fielditem" property="dataValue" />
					&nbsp;

				</td>
				<td align="center" class="RecordRow" nowrap>
					<bean:write name="element" property="string(codeitem_value)" filter="true" />
					&nbsp;
				</td>
				<td align="left" class="RecordRow" nowrap>
					<bean:write name="element" property="string(key_factors)" filter="true" />
					&nbsp;
				</td>
				<%
					RecordVo vo = (RecordVo)pageContext.getAttribute("element");
					String itemid = vo.getString("itemid");
				 %>
				<td align="center" class="RecordRow" nowrap>
					<a href="/general/deci/definition/statCutline/searchStatCutline.do?b_init=link&encryptParam=<%=PubFunc.encrypt("item_id="+itemid)%>"><img src="/images/edit.gif" border=0></a>

				</td>

			</tr>
		</hrms:extenditerate>

	</table>
  </td>
 </tr>
 <tr>
  <td>
     <table width="100%" align="center" class="RecordRowP">
		<tr>
			<td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
				<bean:write name="statCutlineForm" property="statCutlinelistform.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
				<bean:write name="statCutlineForm" property="statCutlinelistform.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
				<bean:write name="statCutlineForm" property="statCutlinelistform.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
			<td align="right" nowrap class="tdFontcolor">

				<p align="right">
					<hrms:paginationlink name="statCutlineForm" property="statCutlinelistform.pagination" nameId="statCutlinelistform" propertyId="roleListProperty">
					</hrms:paginationlink>
			</td>
		</tr>
	</table>
  </td>
 </tr>
</table>
	<table width="85%" align="center">
		<tr>
			<td align="center" height="35px;">
				<html:button styleClass="mybutton" property="b_init" onclick="binit();">
                                  <bean:message key="button.insert" />
	                        </html:button>
				<hrms:submit styleClass="mybutton" property="b_delete" onclick="return checkdelete()">
					<bean:message key="button.delete" />
				</hrms:submit>
				<INPUT type="button" name="b_return" onClick="javascript:history.back();" value="<bean:message key="button.return" />" class="mybutton" >
			</td>
		</tr>
	</table>
</html:form>

 