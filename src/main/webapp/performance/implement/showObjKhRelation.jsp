<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*" %>
<script>
    function closeWin(){
        if (window.showModalDialog) {
            parent.window.close();
        }else{
           var wind = parent.parent.Ext.getCmp("batchSetObjKhRelationsWin");
           if (wind){
               wind.close();
           }
        }
    }
	function selObjKhRelation()
	{
		var khRelation = document.getElementById('khRelation').value;
		var thevo=new Object();
       	thevo.flag="true";
       	thevo.objKhRelation=khRelation;
       	if (window.showModalDialog){
            parent.window.returnValue=thevo;
        }else{
            parent.parent.batchSetObjKhRelations_ok(thevo);
        }
        closeWin();
	}
</script>		 
<html:form action="/performance/implement/performanceImplement">

	<table border="0" cellspacing="0" align="center" cellpadding="2">
			<tr>
						<td height='10' nowrap>
							&nbsp;
						</td>
			</tr>
			<tr>
						<td align="center" nowrap >
							<fieldset align="center" style="width:300;">
							<legend>
									<bean:message key='jx.implement.selObjKhRelation' />
							</legend>
							<table border="0" cellspacing="0" align="center" cellpadding="2">
								<tr>
									<td style="height:35px" nowrap>
							<select id="khRelation" size="1" style="width:250px">
								<option value="0"><bean:message key="khrelation.option1"/></option>
			  		  			<option value="1"><bean:message key="khrelation.option2"/></option>
							</select>
						</td>
					</tr>
				</table>
			</fieldset>
						</td>
			</tr>
			</table>			
			
			<table width="100%">
				<tr>
					<td align="center">
						
						<input type="button" class="mybutton"
							value="<bean:message key='button.ok' />" onClick="selObjKhRelation();" />
					
						<input type="button" class="mybutton"
							value="<bean:message key='button.cancel' />"
							onClick="closeWin();">
					</td>
				</tr>
			</table>
</html:form>