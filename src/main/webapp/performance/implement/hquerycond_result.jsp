<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
	  int i=0;

%>
<hrms:themes />
<html>
<head>
<title></title>
</head>

<script language="javascript">
function selectAll()
{
/*
	for(var i=0;i<document.conditionQueryForm.elements.length ; i ++)
	{
		if( document.conditionQueryForm.elements[i].type=='checkbox')
			document.conditionQueryForm.elements[i].checked =true;
	}
	*/
	conditionQueryForm.isSelectAll.value="1";
	conditionQueryForm.action="/selfservice/performance/hquery_interface.do";
	conditionQueryForm.submit();
}
 
function noSelect()
{
	/*
	for(var i=0;i<document.conditionQueryForm.elements.length ; i ++)
	{
		if( document.conditionQueryForm.elements[i].type=='checkbox')
			document.conditionQueryForm.elements[i].checked =false;
	}
	*/
	conditionQueryForm.isSelectAll.value="0";
	conditionQueryForm.action="/selfservice/performance/hquery_interface.do";
	conditionQueryForm.submit();
}

function addFild(theObj)
{	

	for(var i=0;i<document.conditionQueryForm.elements.length ; i ++)
	{
		if(document.conditionQueryForm.elements[i].type=='checkbox' && document.conditionQueryForm.elements[i].checked ==false)
		{
				conditionQueryForm.isSelectAll.value="0";
				break;
		}
	}
	
	theObj.disabled=true;
	conditionQueryForm.action='/selfservice/performance/hquery_interface.do?b_add=link';
	conditionQueryForm.submit();
}
function addFild2(theObj)
{
	for(var i=0;i<document.conditionQueryForm.elements.length ; i ++)
	{
		if(document.conditionQueryForm.elements[i].type=='checkbox' && document.conditionQueryForm.elements[i].checked ==false)
		{
				conditionQueryForm.isSelectAll.value="0";
				break;
		}
	}
	theObj.disabled=true;
	conditionQueryForm.action='/selfservice/performance/hquery_interface.do?b_add2=link';
	conditionQueryForm.submit();
}
</script>

<body>
<html:form action="/selfservice/performance/hquery_interface"  >
<br>
<br>
<table width="85%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
              <bean:message key="column.select"/>&nbsp;
             </td>
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="accountForm" fieldname="B0110" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;
            </td>           
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="accountForm" fieldname="E0122" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="accountForm" fieldname="E01A1" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="accountForm" fieldname="A0101" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;          	
	    </td>
            
	      	    	    		        	        	        
           </tr>
   	  </thead>
	
   	  <hrms:paginationdb id="element" name="conditionQueryForm" sql_str="conditionQueryForm.str_sql" table="" where_str="conditionQueryForm.str_whl" columns="A0100,B0110,E0122,E01A1,A0101"  order_by="order by A0000" page_id="pagination" pagerows="20" indexes="indexes">
	    
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
            <td align="center" class="RecordRow" nowrap>
               <hrms:checkmultibox name="conditionQueryForm" property="pagination.select" value="true" indexes="indexes"/>&nbsp;
            </td>
            <td align="left" class="RecordRow" nowrap>
          	<hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>  	      
          	<bean:write name="codeitem" property="codename" />&nbsp;
	    </td>            
            <td align="left" class="RecordRow" nowrap>
          	<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" scope="page"/>  	      
          	<bean:write name="codeitem" property="codename" />&nbsp;            
	    </td>
            <td align="left" class="RecordRow" nowrap>
                <hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>  	      
          	<bean:write name="codeitem" property="codename" />&nbsp;     
	    </td>
            <td align="left" class="RecordRow" nowrap>
                      <bean:write name="element" property="a0101" filter="false"/>&nbsp;
	    </td>  
	             	    	    	    		        	        	        
          </tr>
        </hrms:paginationdb>
        
        
</table>
<table  width="70%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="conditionQueryForm" property="pagination" nameId="conditionQueryForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table> 

<html:hidden name="conditionQueryForm" property="isSelectAll" />
<table  width="85%" align="center">
          <tr>
            <td align="left">
            	<html:button  styleClass="mybutton" property="b_addfield" onclick="selectAll();">
            		     <bean:message key="label.query.selectall"/> 
	        </html:button>
            	<html:button  styleClass="mybutton" property="b_addfield" onclick="noSelect();">
            		     <bean:message key="lable.performance.clear"/> 
	            </html:button>
            
            	<logic:equal name="conditionQueryForm" property="flag" value="1">
            		 <html:button  styleClass="mybutton" property="b_add" onclick="addFild(this);">
            		     <bean:message key="button.save"/> 
	            	 </html:button><!-- 
              	 	 <hrms:submit styleClass="mybutton" property="b_add" ><bean:message key="button.save"/></hrms:submit> -->
              	</logic:equal>
              	<logic:equal name="conditionQueryForm" property="flag" value="2">
              	  <html:button  styleClass="mybutton" property="b_add2" onclick="addFild2(this);">
            		     <bean:message key="button.save"/> 
	              </html:button><!-- 
              	  <hrms:submit styleClass="mybutton" property="b_add2" ><bean:message key="button.save"/></hrms:submit>-->
              	</logic:equal> 
            </td>
          </tr>          
</table>





</html:form>
<script language="javascript">
	for(var i=0;i<document.conditionQueryForm.elements.length ; i ++)
	{
		if( document.conditionQueryForm.elements[i].type=='checkbox')
		{
			if('${conditionQueryForm.isSelectAll}'=='1')
				document.conditionQueryForm.elements[i].checked =true;
			else
				document.conditionQueryForm.elements[i].checked =false;
		}
			
	}
</script>
</body>
</html>