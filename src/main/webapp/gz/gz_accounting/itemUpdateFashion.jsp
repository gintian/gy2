
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/gz/salary.js"></script>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
 <%
	int i=0;
%>

  <style type="text/css">
	
#scroll_box {
	           border: 1px solid #ccc;
	           height: 280px;    
	           width: 100%;            
	           overflow: auto;            
	           margin: 1em 0;

	       }
	</style>
<hrms:themes />	
	
<html:form action="/gz/gz_accounting/submit_data"> 
<br>
<table align="center"  width="80%">

<tr>
<td>

  <fieldset align="center" style="width:100%;">
   <legend><bean:message key="label.gz.updateAdvancedSet"/></legend>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr><td>&nbsp;</td></tr>
	<tr>
	 <td width="85%">
		<table border="0" cellspacing="0" cellpadding="0" align="center">
		<tr>
 		   <td valign="top">
 		    	<div id="scroll_box" >	
				<table width="100%" border="0" cellspacing="0" cellpadding="0" class="ListTable1">
				   	  <thead>
			           <tr class="fixedHeaderTr1">
				            <td align="center" class='TableRow' width="210">
								<bean:message key="kh.field.field_n"/>&nbsp;
					    	</td>         
				            <td align="center" class='TableRow' >
								<bean:message key="label.gz.updateMode"/>&nbsp;
					    	</td>
			           </tr>
				   	  </thead>
				      
				      <logic:iterate id="element" name="batchForm" property="gzItemList"  indexId="index" >   
				          <%
				          if(i%2==0)
				          {
				          %>
				          <tr class="trShallow">
				          <%}
				          else
				          {%>
				          <tr class="trDeep">
				          <%
				          }
				          i++;          
				          %>  
				            <td align="left" class="RecordRow" nowrap>
				                <bean:write name="element" property="itemdesc" filter="true"/>&nbsp;
				                <html:hidden name="element" property="itemid"/>
					    	</td>            
				            <td align="center" class="RecordRow" nowrap>
				    		    <select name='type'>
				    		    	<option value='0' <logic:equal name="element" property="flag"  value="0">selected</logic:equal>   >累加更新</option>
				    		    	<option value='1' <logic:equal name="element" property="flag"  value="1">selected</logic:equal> >替换更新</option>
				    		    </select>					    	
				    		</td>
				          </tr>
				      </logic:iterate>  
				</table>
 		   </div>
    	   </td>
		</tr>
		</table>    
     </td>     
	</tr>

	</table>
	</fieldset>
</td>
</tr>
	<tr>
	<td>
		<table align="center" width='90%'>
    		<tr >
		  	  <td align='center' >
				 <input type='button' value='<bean:message key="reporttypelist.confirm"/>' Class="mybutton" onclick='enter()' >
				 &nbsp;&nbsp;&nbsp;&nbsp;
				 <input type='button' value='<bean:message key="lable.content_channel.cancel"/>' onclick='window.close()' Class="mybutton" >
				 
			   </td>
	    	</tr>
    	</table>
	</td>
	</tr>
</table>

<script language='javascript'>

function enter()
{
	 var objlist = new Object();
	 if($('itemid').length==undefined)
	 {
	 	var items=new Array();
	 	var typs=new Array();
	 	items[0]=$F('itemid');
	 	typs[0]=$F('type');
	 	
	 	objlist.items=items;
	    objlist.types=typs;
	 }
	 else
	 {
	     objlist.items=$F('itemid');
	     objlist.types=$F('type');
	 }
	 returnValue=objlist;
     window.close();

}

</script>

</html:form>


  