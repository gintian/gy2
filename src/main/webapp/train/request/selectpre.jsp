<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<SCRIPT LANGUAGE=javascript src="/js/common.js"></SCRIPT>
<SCRIPT LANGUAGE=javascript src="./selectper.js"></SCRIPT>
<style type="text/css"> 
#scroll_box {
    border: 1px solid #eee;
    height: 278px;    
    width: 240px;            
    overflow: auto;            
    margin: 0;
}
.text4{
	width: 150px;
}
</style>
<html:form action="/train/request/selectpre">
<input type="hidden" name="personid" id="personid">
<input type="hidden" name="idarr" id="idarr">
<input type="hidden" name="flag" id="flag" value="1">
<table width='590px' border='0' cellpadding="0" cellspacing="0" align="center">
	<tr>
		<td width='45%'>
		<fieldset style="width:100%;">
    		<legend ><bean:message key="wd.lawbase.standbypersonnel"/></legend>
    		<table width='100%'>
    			<tr>
    				<td colspan='2' style="width: 100%;padding-top: 5px;padding-left: 3px;" nowrap>
    					<bean:message key="columns.archive.name"/>&nbsp;
    		 			<Input type='text' name='a_name' id='a_name' size='17' class="text4" onpropertychange='searchSelect("${param.nbase}","${param.itemkey}","${param.preflag}");'/>
    		 			<input id="cancelQuery" type="button" value="<bean:message key='train.b_plan.trains.nosearch'/>" Class="mybutton" onclick='cancelSelect("${param.nbase}","${param.itemkey}","${param.preflag}");' style="display: none;" />
    				</td>
		     	</tr>
		     	<tr>
    				<td colspan='2' height="240">
    				<iframe src="/train/request/selectifram.jsp?nbase=${param.nbase}&preflag=${param.preflag}&itemkey=${param.itemkey}" name="selectiframe" width="100%" height="250" style="border: 1px solid;" class="common_border_color" ></iframe>
    				</td>
    			</tr>
		     </table>             
		</fieldset>
		</td>
		<td width='10%' align="center">
			<table align="center" border="0" width="100%">
				<tr><td>&nbsp;</td></tr>
				<tr>
					<td align="center">
						<html:button  styleClass="mybutton" property="b_addfield" onclick="addSelectPerson();">
            				<bean:message key="button.setfield.addfield"/> 
	        			</html:button>
	        			<html:button style="display:none"  styleClass="mybutton" property="b_addfield1" onclick="addSelectNamePer();">
            				<bean:message key="button.setfield.addfield"/> 
	        			</html:button>
	        		</td>
	        	</tr>
	        	<tr><td>&nbsp;</td></tr>
	        	<tr>
	        		<td align="center">
	        			<html:button  styleClass="mybutton" property="b_delfield" onclick="deleterow(1);">
            				<bean:message key="button.setfield.delfield"/>    
	        			</html:button>
	        			<html:button style="display:none" styleClass="mybutton" property="b_delfield1" onclick="deleterow(2);">
            				<bean:message key="button.setfield.delfield"/>    
	        			</html:button>		
	        		</td>
	        	</tr>
	        	<tr><td>&nbsp;</td></tr>
	        </table>
		</td>
		<td width='39%' valign="top">
			<fieldset align="center" style="width:100%;">
    		<legend ><bean:message key="wd.lawbase.alreadypickpersonnel"/></legend>
			<table  width='100%'>
				<tr>
					<td height="280" valign="top">
						<div id="scroll_box" style="width: 100%;height: 280" class="complex_border_color">
							<table border="0" id="person" width='100%'>
								<tr><td width='15'>&nbsp;</td><td>&nbsp;</td></tr>
							</table>
						</div>
             		</td>
             	</tr>
             </table>           
		</fieldset>
		</td>
	</tr>
	<tr>
		<td colspan='3' align=right>
			<table width='100%'>
				<tr>
					<td align="center">
						<html:button  styleClass="mybutton" property="enter" onclick="setOk();">
            				<bean:message key="button.ok"/>
	       			 	</html:button >	
	      				<html:button  styleClass="mybutton" property="cancel" onclick="window.close();">
            				<bean:message key="button.cancel"/>
	        			</html:button >
	        		</td>
	        	</tr>
	        </table>
		</td>
	</tr>
</table>
</html:form>
    		 			