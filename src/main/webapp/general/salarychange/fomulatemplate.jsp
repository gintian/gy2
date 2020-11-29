<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script language="JavaScript" src="./formulatemp.js"></script>
<script type="text/javascript" src="/js/function.js"></script> 
<style type="text/css"> 
.gztable {
 	border-right:#7b9ebd 1px solid;
 	border-left:#7b9ebd 1px solid;
 	border-top:#7b9ebd 1px solid;
 	border-bottom:#7b9ebd 1px solid;
 	word-break: break-all; 
 	word-wrap:break-word;
}
#temptable {
           border: 1px solid #eee;
           height: 300px;    
           width: 280px;            
           overflow: auto;            
           margin: 1em 1;
}
</style>
<%

    UserView userView = (UserView) session.getAttribute(WebConstant.userView);
    String bosflag = "";
    if (userView != null) {
        bosflag = userView.getBosflag();
    }
%>
 <%
     if ("hcm".equals(bosflag)) {
 %>
<link href="/general/template/template.css" rel="stylesheet" type="text/css"/>
<%
    }
%>
<html:form action="/general/salarychange/fomulatemplate">
<html:hidden name="setFormulaForm" property="id"/>
<html:hidden name="setFormulaForm" property="tableid"/>
<%int n=1;%>
<table width="390" height="380" border="0" cellpadding="0" class="formulatemplatemargin">
  <tr> 
    <td width="350" height="380" align="center" valign="top"> 
      <table width="100%" height="100%" border="0" align="center" class="gztable common_border_color">
    		<tr>	
    			<td valign="top">
    				<div id="temptable common_border_color">
    					<table width="100%" border="0" align="center" class="ListTable1 common_border_color">
    						<tr>
								<td width="10%" class="TableRow" align="center">&nbsp;</td>
								<td class="TableRow" align="center"><bean:message key='workdiary.message.formula.group.name'/></td>
								<td width="20%" class="TableRow" align="center"><bean:message key='workdiary.message.effective.stats'/></td>
							</tr>
							<hrms:paginationdb id="element" name="setFormulaForm" 
							sql_str="select id,flag,chz" table="" 
							where_str="from gzAdj_formula where tabid=${setFormulaForm.tableid}" 
							columns="id,flag,chz" 
							order_by="order by nsort" 
							pagerows="200" page_id="pagination" 
							indexes="indexes">
								<bean:define id="nid" name='element' property='id'/>
								<bean:define id="chz" name='element' property='chz'/>
								<tr>	
									<td class="RecordRow" align="center" onclick='tr_bgcolor("${nid}");setId("${nid}");'>
										<%=n%>
									</td>
									<td class="RecordRow" align="center" onclick='tr_bgcolor("${nid}");setId("${nid}");'>
										<div id="view_${nid}" ondblclick='alertName("${nid}")'>${chz}</div> 
										<div id="hide_${nid}" style="display:none">
											<input type="text" name="value_${nid}" value="${chz}"  
											size="25" onblur='onLeave("${nid}",this)'>
										</div>
									</td>
									<td class="RecordRow" align="center" onclick='tr_bgcolor("${nid}");setId("${nid}");'>
										<logic:equal name='element' property='flag' value="1">
											<input type="checkbox" onclick='alertFlag("${nid}",this);' name="${nid}" value="1" checked>
										</logic:equal>
										<logic:notEqual name='element' property='flag' value="1">
											<input type="checkbox" onclick='alertFlag("${nid}",this);' name="${nid}" value="0">
										</logic:notEqual>
									</td>
								</tr>
								<%n++;%>
							</hrms:paginationdb>
    					</table>
    				</div>
    			</td>
  			</tr>
    	</table>
    </td>
    <td width="40" valign="top" align="right" style="margin-right:0px;"> 
      <table width="100%" border="0" align="right">
        <tr>
          <td  align="right" valign="bottom">
			<input name="add" type="button" class="mybutton" onclick="addFormula();" value="<bean:message key='button.new.add'/>" style="margin-right:0px;">
          </td>
        </tr>
        <tr>
          <td  align="right" valign="bottom">
			<input name="alert" type="button" class="mybutton" onclick="alertModeFormula();" value="<bean:message key='label.edit'/>" style="margin-right:0px;">
          </td>
        </tr>
        <tr>
          <td  align="right" valign="bottom">
			<input name="sort" type="button" class="mybutton" onclick='setSorting("${setFormulaForm.tableid}");' value="<bean:message key='button.itemadjust'/>" style="margin-right:0px;">
		  </td>
        </tr>
        <tr>
          <td  align="right" valign="bottom">
			<input name="delete" type="button" class="mybutton" onclick="delTemp();" value="<bean:message key='kq.shift.cycle.del'/>" style="margin-right:0px;">
          </td>
        </tr>
        <tr>
          <td  align="right" valign="bottom"> 
            <input name="close" type="button" class="mybutton" onclick="window.close();" value="<bean:message key='lable.welcomeboard.close'/>" style="margin-right:0px;">
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
</html:form>
<script language="javascript">
defaultSelect("${setFormulaForm.id}");
</script>
