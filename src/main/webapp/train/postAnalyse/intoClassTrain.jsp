<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/common.js"></script>
<script language="JavaScript" src="/train/traincourse/traindata.js"></script>
<script language="JavaScript">
function returned(){
	
	var tablevos=document.getElementsByTagName("input");
	var nid="";
	for(var i=0;i<tablevos.length;i++){
	     if(tablevos[i].type=="checkbox"){
	     	if(tablevos[i].checked){
	     		nid += tablevos[i].value+",";
	     	}
		 }
     }

	if(nid==null || nid.length<1){
		alert(SELECT_TRAIN_CLASS);
		return;
	}
     window.returnValue=nid;
     window.opener=null;
     window.open('','_self');
     window.close();
}

</script>
<hrms:themes/>
<html:form action="/train/postAnalyse/notaccordpostwork/searchClass">
<body>
<%int i=0;%>
<table width="690px" border="0" cellspacing="0"  align="center" cellpadding="0">
<tr><td>
<table width="100%" border="0" cellspacing="0" cellpadding="0" class="ListTable1">
	<thead>
	<tr>
		<logic:iterate id="element" name="trainStationForm"  property="browsefields" indexId="index">
			<logic:equal name="element" property="itemid" value="r3101">
    		<td width="30" align="center" class="TableRow" nowrap>
    			<input type="checkbox" name="selbox" onclick="batch_select_all(this);" title='<bean:message key="label.query.selectall"/>'>                              
	      	</td> 
	      </logic:equal>
	      <logic:notEqual name="element" property="itemid" value="r3101">
			<td align="center"class="TableRow" nowrap>
                 &nbsp;<bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
	       </td>
	       </logic:notEqual>
	       
		</logic:iterate>
	</tr>
	</thead>
	<hrms:paginationdb id="element" name="trainStationForm" sql_str="trainStationForm.sqlstr" table="" 
	where_str="trainStationForm.where" columns="trainStationForm.cloumn" 
	order_by="order by r3101" page_id="pagination" pagerows="${trainStationForm.pagerows}">
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
    	<logic:iterate id="fielditem"  name="trainStationForm"  property="browsefields" indexId="index">
    		<logic:equal name="fielditem" property="itemid" value="r3101">
    		<td align="center" class="RecordRow" nowrap>
    			<bean:define id="nid" name='element' property='r3101'/>
    			<%String r3101 = SafeCode.encode(PubFunc.encrypt(nid.toString())); %>
    			<input type="checkbox" name="<%=r3101 %>" value="<%=r3101 %>">                                 
	      	</td> 
	      </logic:equal>
	      <logic:notEqual name="fielditem" property="itemid" value="r3101">
		    		<td align="left" class="RecordRow" nowrap>
		               <logic:notEqual name="fielditem" property="codesetid" value="0">
		          	   		<hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
		          	   		&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;            
		               </logic:notEqual>
		               <logic:equal name="fielditem" property="codesetid" value="0">
		                   &nbsp;<bean:write name="element" property="${fielditem.itemid}" filter="false"/>&nbsp;                 
		               </logic:equal>                              
			      </td> 
	      </logic:notEqual>
    	</logic:iterate>
    </tr>
    </hrms:paginationdb>      
</table>
</td></tr>
<tr><td>
<table  width="100%" border="0" class="RecordRowP" cellpadding="0" cellspacing="0">
	<tr>
		<td width="300" valign="bottom" class="tdFontcolor">
		<hrms:paginationtag name="trainStationForm" pagerows="${trainStationForm.pagerows}" property="pagination" scope="page" refresh="true"></hrms:paginationtag>
		</td>
	    <td width="300" align="right" nowrap class="tdFontcolor">
		     <hrms:paginationdblink name="trainStationForm" property="pagination" nameId="trainStationForm" scope="page">
			</hrms:paginationdblink>
		</td>
		<td>&nbsp;</td>
	</tr>
</table>
<table  align="center"  >
          <tr>
            <td align="left">        
               
	 	    		<input type="button" value="<bean:message key='button.ok' />" onclick="returned();" class="mybutton">
                
            </td>
          </tr>          
</table>
</td></tr>
</table>	
</body>	
</html:form>