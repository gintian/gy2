<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<hrms:themes />
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="javascript">
function sort()
{
	var sortStr=selectTostr('sort_fields');
	var thevo=new Object();
	thevo.flag="true";
	thevo.sortStr=sortStr;
    parent.window.returnValuee=thevo;
    parent.window.close();;
}
function selectTostr(listbox)
{
  var vos,right_vo,i,str='';
  vos= document.getElementsByName(listbox);
  if(vos==null || vos[0].length==0)
  	return; 
  right_vo=vos[0];  
  for(i=0;i<right_vo.options.length;i++){
	str += right_vo.options[i].value+",";
  }
  return str;  	
}		   	  		 		
</script>
<base target="_self">
<html:form action="/performance/options/degreeHighSetList">
<center>
<table width="80%" height="300" border="0" align="center">
  <tr> 
    <td height="20" >
	</td>
  </tr>
  <tr> 
    <td>
    	<fieldset style="width:100%;height:300">
    	<legend><bean:message key="jx.param.modifysort"/></legend>
    	<table width="100%"  border="0" align="center">
    	<tr><td colspan="2">&nbsp;</td></tr>
    	<tr>
    		<td width="100%">
    			<html:select name="perDegreeForm" property="sort_fields" multiple="multiple"  style="height:240px;width:100%;font-size:10pt">
                         <html:optionsCollection property="sortlist" value="dataValue" label="dataName"/>   		      
 		     	</html:select>
    		</td>
    		<td>
    			<table width="100%"  border="0" align="center" height="240">
    				<tr height="100">
    					<td>&nbsp;</td>
    				</tr>
    				<tr>
    					<td>&nbsp;</td>
    				</tr>
    				<tr>
    					<td align="center">
							<html:button  styleClass="mybutton" property="b_up" onclick="upItem($('sort_fields'));">
            		     		<bean:message key="button.previous"/> 
	           				</html:button >
						</td>
    				</tr>
    				<tr>
    					<td align="center">
							<html:button  styleClass="mybutton" property="b_down" onclick="downItem($('sort_fields'));">
            		     		<bean:message key="button.next"/>    
	           				</html:button >	 
						</td>
    				</tr>
    				<tr>
    					<td align="center"><input type="button" id='ok' value="<bean:message key='button.ok'/>" onclick="sort();" Class="mybutton"></td>
    				</tr>
    				<tr>
    					<td align="center"><input type="button" value="<bean:message key='button.close'/>" onclick="window.close();" Class="mybutton"></td>
    				</tr>
    			</table>
    		</td>
    	</tr>
    	</table>
    	 </fieldset>
    </td>
  </tr>
</table>
</center>
</html:form>
<script>
  var vo= document.getElementsByName('sort_fields');
  var vo1=vo[0];  
  if(vo1.length==0)
  	document.getElementById('ok').disabled=true;
</script>