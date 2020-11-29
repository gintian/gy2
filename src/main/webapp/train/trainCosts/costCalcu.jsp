<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/train/trainCosts/planTrain.js"></script>
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
}
</style>
<script type="text/javascript">
function setFlag(itemid)
{
	var value = '0';
	if(item.checked==true)
		value='1';
	alert(itemid+'--'+value)
}
</script>
<html:form action="/train/trainCosts/costCalcu">
<table width="100%" height="310" border="0">
  <tr> 
    <td width="80%" height="310" align="center" valign="top"> 
      <table width="100%" height="100%" border="0" align="center" class="gztable"><!--class="gztable" -->
			<tr>	
    			<td valign="top">
    				<div id="temptable"><!-- id="temptable" -->
    					<table width="100%" border="1" align="center" class="ListTable">	
    						<tr class="fixedHeaderTr1">								
								<td colspan="2" class="TableRow"><bean:message key='train.job.selfomula'/></td>
							</tr>						
							<hrms:paginationdb id="element" name="trainCostsForm" 
							sql_str="select itemid,forname,flag" table="" 
							where_str="from HrpFormula where Unit_type='5' and SetId='R45'" 
							columns="itemid,forname,flag" 
							order_by="order by DB_TYPE" 
							pagerows="200" page_id="pagination" 
							indexes="indexes">
								<bean:define id="nid" name='element' property='itemid'/>
								<tr>	
									<td class="RecordRow" align="right" width="25">										
										<input type="checkbox" onclick='setFlag("${nid}");' id="${nid}" 
											<logic:equal name='element' property='flag' value="1">
												checked
											</logic:equal>>
									</td>
									<td class="RecordRow" align="left">
									&nbsp;<bean:write name="element" property="forname" filter="true" />
									</td>
								</tr>
							</hrms:paginationdb>
    					</table>
    				</div>
    			</td>
  			</tr>
    	</table>
    </td>
    <td width="20%" valign="bottom"> 
      <table width="100%" border="0">
        <tr> 
          <td height="23">&nbsp;</td>
        </tr>
        <tr>
          <td height="32" align="center" valign="bottom">
			<input  type="button" class="mybutton" onclick="costSort();" value="<bean:message key='kq.item.change'/>">
          </td>
        </tr>
        <tr>
          <td height="32" align="center" valign="bottom">
			<input name="add" type="button" class="mybutton" onclick="addFormula();" value="<bean:message key='infor.menu.definition.formula'/>">
          </td>
        </tr>
        <tr>
          <td height="33" align="center" valign="bottom">
			<input  type="button" class="mybutton" onclick="" value="&nbsp;&nbsp;<bean:message key='button.ok'/>&nbsp;&nbsp;">
          </td>
        </tr>
        <tr>
          <td height="34" align="center" valign="bottom">
			<input type="button" class="mybutton" onclick="window.close()" value="&nbsp;&nbsp;<bean:message key='button.cancel'/>&nbsp;&nbsp;">
		  </td>
        </tr>  
      </table>
    </td>
  </tr>
</table>
</html:form>

