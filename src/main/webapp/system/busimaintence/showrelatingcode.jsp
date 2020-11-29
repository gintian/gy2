
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>

<link rel="stylesheet" href="/css/css1.css" type="text/css">
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/popcalendar3.js"></script>
<script type="text/javascript" language="javascript">
function addrela(){
	relatingcodeForm.action="/system/busimaintence/showrelatingcode.do?b_action=link&add=add";
	relatingcodeForm.submit();
}
function updaterela(){
	relatingcodeForm.action="/system/busimaintence/showrelatingcode.do?b_action=link&update=update";
	relatingcodeForm.submit();
}
function delrela(){
	var len=document.relatingcodeForm.elements.length;
       var uu;
       for (var i=0;i<len;i++)
       {
           if (document.relatingcodeForm.elements[i].type=="checkbox")
           {
              if(document.relatingcodeForm.elements[i].checked==true)
              {
                uu="dd";
                break;
               }
           }
       }
       if(uu!="dd")
       {
          alert(CHOISE_DELETE_NOT);
          return false;
       }
	relatingcodeForm.action="/system/busimaintence/showrelatingcode.do?b_del=link&del=del";
	relatingcodeForm.submit();
}
function blackrela(){
    var obj = new Object();
    obj.type="1";
    ReturnValue=obj;
    window.close();
}
function bak()
{
   relatingcodeForm.action="/system/busimaintence/showbusiname.do?br_return=return";
   relatingcodeForm.target="il_body";
   relatingcodeForm.submit();
}
</script>
<hrms:themes/>
<html:form action="/system/busimaintence/showrelatingcode">
<%
int i=1;
%>
<br/>
	<table width="80%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">

			<tr>
					
					<td align="center" class="TableRow" nowrap>
					<bean:message key="recidx.label"/>					
					</td>
					<td align="center" class="TableRow" nowrap>
					<bean:message key="column.select"/>
					</td>
					<td align="center" class="TableRow" nowrap>
					<bean:message key="label.sys.code"/>
					</td>
					<td align="center" class="TableRow" nowrap>
					<bean:message key="kjg.title.codetable"/>
					</td>
					<td align="center" class="TableRow" nowrap>
					<bean:message key="kjg.title.codeindex"/>
					</td>
					<td align="center" class="TableRow" nowrap>
					<bean:message key="kjg.title.codenameindex"/>
					</td>
					<td align="center" class="TableRow" nowrap>
					<bean:message key="kjg.title.topcodeindex"/>
					</td>
					<td align="center" class="TableRow" nowrap>
					<bean:message key="kjg.title.showform"/>
					</td>
					<td align="center" class="TableRow" nowrap>
					<bean:message key="label.kh.edit"/>
					</td>
					
					
			</TR>
		<hrms:paginationdb id="element" name="relatingcodeForm" sql_str="relatingcodeForm.sql" table="" where_str="relatingcodeForm.where" columns="relatingcodeForm.column" order_by="relatingcodeForm.orderby" pagerows="10" page_id="pagination" indexes="indexes">	
		<%
          		if(i%2==0)
          		{
          	%>
          <tr class="trShallow" onClick="javascript:tr_onclick(this,'')">
          	<%}
          		else
          	{%>
          <tr class="trDeep" onClick="javascript:tr_onclick(this,'')">
          	<%
          		}
          		i++;          
          	%> 
		<bean:define id="codesetid" name="element" property="codesetid"/>
		<td class="RecordRow" nowrap>
		</td>
		<td align="center" class="RecordRow" nowrap>
		<hrms:checkmultibox name="relatingcodeForm" property="pagination.select" value="true" indexes="indexes" />
		</td>
		<td class="RecordRow" nowrap>
		<bean:write name="element" property="codesetid"/>
		</td>
		<td class="RecordRow" align="center" nowrap>
		<bean:write name="element" property="codetable"/>
		</td>
		<td class="RecordRow" nowrap>
		<bean:write name="element" property="codevalue"/>
		</td>
		<td class="RecordRow" nowrap>
		<bean:write name="element" property="codedesc"/>
		</td>
		<td class="RecordRow" nowrap>
		<bean:write name="element" property="upcodevalue"/>
		</td>
		<td class="RecordRow" align="center" nowrap>
		<logic:equal value="1" name="element" property="status">
		<bean:message key="kjg.title.listtable"/>
		</logic:equal>
		<logic:equal value="0" name="element" property="status">
		<bean:message key="kjg.title.tree"/>
		</logic:equal>
		</td>
		<td class="RecordRow" align="center" nowrap>
		<a href='/system/busimaintence/showrelatingcode.do?b_action=link&update=update&codesetid=${codesetid}'><bean:message key="label.kh.edit"/></a>
		</td>
		</tr>
	
		</hrms:paginationdb>
		<tr><td colspan="9" class="RecordRow">
			<table width="100%" align="center" cellpadding="0" cellspacing="0">
			<tr>
				<td align="left" valign="middle" class="tdFontcolor" nowrap>
					<bean:message key="label.page.serial" />
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="label.page.sum" />
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.page.row" />
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page" />
				</td>
				<td align="right" nowrap class="tdFontcolor">
					<p align="right">
						<hrms:paginationdblink name="relatingcodeForm" property="pagination" nameId="browseRegisterForm" scope="page">
						</hrms:paginationdblink>
				</td>
			</tr>
			</table>
		
		</td></tr>
	</table>
	
	<table width="83%" align="center">
			<tr>
			<td align="center" class="tdFontcolor" nowrap>	
			<button name="add" class="mybutton" onclick="addrela();">
			<bean:message key="kq.shift.cycle.add" />
			</button>

			<button name="del" class="mybutton" onclick="delrela();">
			<bean:message key="kq.shift.cycle.del" />
			</button>
			
			<logic:equal value="1" name="relatingcodeForm" property="add_flag"> 
			<button name="black" class="mybutton" onclick="blackrela();">
			<bean:message key="button.close"/>
			</button>
			</logic:equal>
			<logic:notEqual value="1" name="relatingcodeForm" property="add_flag">
				<input type="button" name="ret" value="<bean:message key="button.leave"/>" onclick="bak();" class="mybutton"/>
			</logic:notEqual>
			</td>
			</tr>
		</table>	
</html:form>
