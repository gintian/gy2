<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%int i=0;%> 
<hrms:themes />
<script language="javascript" src="/js/validate.js"></script>
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
<html:form action="/workbench/media/showinfodata">
<script language="javascript">
   function change()
   {
      multMediaForm.action="/workbench/media/showinfodata.do?b_search=link&code=${multMediaForm.code}&kind=${multMediaForm.kind}";
      multMediaForm.submit();
   }
   function winhref(a0100,target)
{
   if(a0100=="")
      return false;
    multMediaForm.action="/workbench/media/searchmediainfolist.do?b_search=link&userbase=${multMediaForm.userbase}&a0100="+a0100+"&flag=notself&setprv=2&returnvalue=1";
    multMediaForm.target=target;
    multMediaForm.submit();
   
      
}
 function to_multimedia_tree(a0100)
 {	
 	var dbname=document.getElementsByName('userbase')[0].value;
 	var thecodeurl ="/general/inform/emp/view/multimedia_tree.do?b_query=link&kind=6&dbname="+dbname+"&a0100="+a0100+"&multimediaflag="; 
 	thecodeurl = thecodeurl.replace(/&/g,"`");
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
 	window.open(iframe_url,'_blank',"width=620,height=460,top="+screen.height/3+"px,left="+screen.width/3+"px,toolbar=no,location=no,resizable=no");
 }
</script>
<style>
.TableRow_right{
	BORDER-TOP: 0pt solid;
}
</style>
 <table width="100%" border="0" cellspacing="0"   cellpadding="0">
 <tr>
   <td>
   <div class="RecordRow_lrt" id="topDiv">
   <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
     <tr>
    	<td align="left" valign="middle" style="padding-top: 4px;" nowrap>
     	     <bean:message key="label.query.dbpre"/>
    	         <hrms:importgeneraldata showColumn="dbname" valueColumn="pre" flag="false" paraValue="" 
                 sql="multMediaForm.dbcond" collection="list" scope="page"/>
              <html:select name="multMediaForm" property="userbase" size="1" onchange="javascript:change()">
                      <html:options collection="list" property="dataValue" labelProperty="dataName"/>
              </html:select>
	    </td>         
   </tr>
   </table>
   </div>
   </td>
 </tr>
 <tr>
 	 <td width="100%" nowrap>
    <div class="fixedDiv2" id="fixedDiv"> 
     <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">  
   	  <thead>
          <tr class="">
            <logic:iterate id="info"    name="multMediaForm"  property="browsefields">   
              <td align="center" class="TableRow_right" nowrap>
                   <bean:write  name="info" property="itemdesc" filter="true"/>
              </td>
             </logic:iterate> 	
              <td align="center" class="TableRow_top" style="width:40px" nowrap>     	 
              	<bean:message key="column.operation"/>
              </td>   	    		        	        	        
           </tr>
   	  </thead>
          <hrms:paginationdb id="element" name="multMediaForm" sql_str="multMediaForm.strsql" table="" where_str="multMediaForm.cond_str" columns="multMediaForm.columns" order_by="multMediaForm.order_by" pagerows="21" page_id="pagination" keys="">
          <%          
          if(i%2==0)
          {
          %>
          <tr class="trShallow" >
          <%}
          else
          {%>
          <tr class="trDeep" >
          <%
          }
          i++;          
          %>  
          <%
             	LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
             	String a0100=(String)abean.get("a0100"); 
                request.setAttribute("name",a0100);       	                           
          %>
            <logic:iterate id="info"    name="multMediaForm"  property="browsefields">  	         
                  <logic:notEqual  name="info" property="itemtype" value="N">               
                    <td align="left" class="RecordRow_right" nowrap>        
                  </logic:notEqual>
                  <logic:equal  name="info" property="itemtype" value="N">               
                    <td align="right" class="RecordRow_right" nowrap>        
                  </logic:equal>    
                  <logic:equal  name="info" property="codesetid" value="0">   
                 		<logic:equal value="a0101" name="info" property="itemid">
                 		<a href="###" onclick="to_multimedia_tree('${name }')">
                 				<bean:write  name="element" property="${info.itemid}" filter="true"/>
                 			</a>
                 		</logic:equal>
                 		<logic:notEqual value="a0101" name="info" property="itemid">
                     <bean:write  name="element" property="${info.itemid}" filter="true"/>
                     </logic:notEqual>
                  </logic:equal>
                 <logic:notEqual  name="info" property="codesetid" value="0">                   
                    <hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page"/>  	      
          	    	 <bean:write name="codeitem" property="codename" />   
          	     </logic:notEqual>  
              </td>
             </logic:iterate>     
            <td align="center" class="RecordRow_left" nowrap>
                <!-- a href="###" onclick="winhref('${name}','mil_body')"-->
                <a href="###" onclick="to_multimedia_tree('${name }')">
                <img src="/images/edit.gif" border=0></a>
	    </td>
	            	    	    	    		        	        	        
          </tr>
        </hrms:paginationdb>
        
</table>
</div>
<div id="pageDiv">
<table width="100%" align="center" class="RecordRowP">
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
		          <p align="right"><hrms:paginationdblink name="multMediaForm" property="pagination" nameId="multMediaForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table>
</div>
   </td>
 </tr>
</table> 
<script>
window.onresize = function(){
	setDivStyle();
}

function setDivStyle(){
	document.getElementById("fixedDiv").style.height = document.body.clientHeight-70;
    document.getElementById("fixedDiv").style.width = document.body.clientWidth-15; 
    if(getBrowseVersion()) {
	    document.getElementById("topDiv").style.width = document.body.clientWidth-15; 
	    document.getElementById("pageDiv").style.width = document.body.clientWidth-15; 
    } else {
    	document.getElementById("topDiv").style.width = document.body.clientWidth-41; 
	    document.getElementById("pageDiv").style.width = document.body.clientWidth-29; 
    }
}

setDivStyle();
</script>

</html:form>
