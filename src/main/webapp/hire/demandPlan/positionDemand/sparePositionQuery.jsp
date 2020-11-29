<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean" %>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy-mm-dd'</SCRIPT>

<script language='javascript'>
	var oprateType="${positionDemandForm.operateType}";
	function imports()
	{
		
		var a_sparePositionIDs=eval("document.positionDemandForm.sparePositionIDs");
		var selectNum=0;	
		if(a_sparePositionIDs)
		{
			if(a_sparePositionIDs.length)
			{
				for(var i=0;i<a_sparePositionIDs.length;i++)
				{
					if(a_sparePositionIDs[i].checked==true)
					{
						selectNum++;
					}
				
				}
			}
			else
			{
				if(a_sparePositionIDs.checked==true)
				{
						selectNum++;
				}
			}
		}
		if(selectNum==0)
		{
			alert(SELECT_IMPORT_POSITION);
			return;
		}
		
		
		var model='<%=(request.getParameter("model"))%>';
		if(model=='1')
			positionDemandForm.action="/hire/demandPlan/positionDemand/positionDemandTree.do?b_importSparePosition=import&code=<%=(request.getParameter("codeid"))%>&model="+model;
		if(model=='2')
			positionDemandForm.action="/hire/demandPlan/positionDemand/positionDemandTree.do?b_importSparePosition2=import&code=<%=(request.getParameter("codeid"))%>&model="+model;
		positionDemandForm.target="il_body";
		positionDemandForm.submit();
	}
	
	function goback()
	{
		var model='<%=(request.getParameter("model"))%>';
		var code='<%=request.getParameter("codeid")%>';

		if(model=='1')
		{
				positionDemandForm.action="/hire/demandPlan/positionDemand/positionDemandTree.do?br_query1=query";
			
		}
		if(model=='2')
		{
				positionDemandForm.action="/hire/demandPlan/positionDemand/positionDemandTree.do?br_query2=query";
		}
		positionDemandForm.target="il_body";
		positionDemandForm.submit();
	
	}
	function allselect(obj)
	{
	   var arr=document.getElementsByName("sparePositionIDs");
	   if(arr)
	   {
	       for(var i=0;i<arr.length;i++)
	       {
	           if(obj.checked)
	               arr[i].checked=true;
	           else
	               arr[i].checked=false;
	       }
	   }
	}

</script>
<hrms:themes></hrms:themes>
<html:form action="/hire/demandPlan/positionDemand/positionDemandTree">
	<table width="75%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" style="margin-top:10px;"><!--这里就不引用layout.css了,因为还要导入包  -->
    <thead>
      <tr> 
        <logic:equal name="positionDemandForm" property="model" value="1">
        	<td width="10%" align="center" nowrap class="TableRow"><input type="checkbox" name="dd" onclick="allselect(this);"/></td>
        </logic:equal>
	        <td width="24%" align="center" nowrap class="TableRow"><bean:message key="column.sys.org"/></td>
	        <td width="20%" align="center" nowrap class="TableRow"><bean:message key="column.sys.dept"/></td>
	        <td width="20%" align="center" nowrap class="TableRow"><bean:message key="hmuster.label.post"/></td>
	        <td width="13%" align="center" nowrap class="TableRow">${positionDemandForm.actualNumberFieldName}</td>
	        <td width="13%" align="center" nowrap class="TableRow">${positionDemandForm.planNumberFieldName}</td>
      </tr>
    </thead>
    <% 
    	int i=0;
    	String className="trDeep";   	
    %>
    <logic:iterate id="element" name="positionDemandForm" property="sparePositionList" > 
    	<%
    		if(i%2==1)
    			className="trDeep";
    		else
    			className="trShallow";
    	%>
	    <tr class="<%=className%>">
	    	 <logic:equal name="positionDemandForm" property="model" value="1">
		    	 <td height="22" align="center" nowrap class="RecordRow">
		    	 	 <input type="checkbox" name="sparePositionIDs" value='<bean:write  name="element" property="codeitemid" filter="true"/>' />  
		        </td>
	         </logic:equal>
			      <td align="left" class="RecordRow" nowrap >&nbsp;<bean:write  name="element" property="UN" filter="true"/></td>
			      <td align="left" class="RecordRow" nowrap >&nbsp;<bean:write  name="element" property="UM" filter="true"/></td>
			      <td align="left" class="RecordRow" nowrap>&nbsp;<bean:write  name="element" property="@K" filter="true"/></td>
			      <td align="right" class="RecordRow" nowrap><bean:write  name="element" property="actualNumber" filter="true"/>&nbsp;</td>
			      <td align="right" class="RecordRow" nowrap><bean:write  name="element" property="planNumber" filter="true"/>&nbsp;</td>
		
	    </tr> 
	    <%
	    	i++;
	    %>
	</logic:iterate>
    
    </table>

		
	<table  width="70%" align="center">
	          <tr>
	            
	      <td height="30" align="center"> 
	        <div align="left"> 
	          <p align="center"> 
	          <logic:equal name="positionDemandForm" property="model" value="1">
	            <input type="button" name="b_add52" value="<bean:message key="lable.resource_paln.inductRequest"/>" class="mybutton" onClick="imports();">
	           </logic:equal>
	            <input type="button" name="b_add522" value="<bean:message key="button.return"/>" class="mybutton" onClick="goback();">
	          </p>
	          </div></td>
	          </tr>          
	</table>







</html:form>