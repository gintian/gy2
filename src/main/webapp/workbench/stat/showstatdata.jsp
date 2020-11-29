<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%int i=0;%>
<hrms:themes></hrms:themes>
<script language="javascript">
  function winhref(url,target,a0100)
  {
   if(a0100=="")
      return false;
   var o_obj=document.getElementById('a0100');   
   if(o_obj)
     o_obj.value=a0100;   
   statForm.action=url;
   statForm.target=target;
   statForm.submit();
  }  
  function winopen(url,a0100)
  {
      if(a0100=="")
        return false;
        var o_obj=document.getElementById('a0100');   
      if(o_obj)
        o_obj.value=a0100;   
       statForm.action=url;
       statForm.target="_blank";
       statForm.submit();
      //window.open(url,"_blank","left=0,top=0,width="+screen.availWidth+",height="+screen.availHeight+",scrollbars=yes,toolbar=yes,menubar=yes,location=yes,resizable=no,status=yes");
  }   
  function document.oncontextmenu() 
   { 
      return　false; 
   } 
    function viewPhoto()
   {
       statForm.action="/workbench/stat/statshow.do?b_view_photo=link";
       statForm.target="_self";
       statForm.submit();
   }  
   function returnH(flag)
   {
      if(flag=="1")
      {
         statForm.action="/general/static/commonstatic/statshow.do?b_return=link";
         statForm.target="_self";
         statForm.submit();
      }else if(flag=="2")
      {
         statForm.action="/general/static/commonstatic/statshow.do?b_returndouble=link";
         statForm.target="_self";
         statForm.submit();
      }             	
   }
</script>
<html:form action="/workbench/stat/statshow">
<input type="hidden" name="a0100" id="a0100">
<br>
<table border="0" width="80%" cellspacing="0"  align="center" cellpadding="0">
<tr> 
<td>
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	   <thead>
           <tr>
           <!-- 
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="statForm" fieldname="B0110" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;
            </td>           
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="statForm" fieldname="E0122" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="statForm" fieldname="E01A1" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="statForm" fieldname="A0101" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;   
	    </td>     
	     -->
            <logic:iterate id="element"    name="statForm"  property="fieldlist" indexId="index">
              <logic:notEqual name="element" property="priv_status" value="0">               
                <td align="center" class="TableRow" nowrap>
                   <bean:write  name="element" property="itemdesc" filter="true"/>
	      		</td>  
  	          </logic:notEqual>	 
            </logic:iterate>
      	    <logic:notEqual name="statForm" property="tabid" value="-1">
            <td align="center" class="TableRow" nowrap>
		     	<bean:message key="tab.base.info"/>          	
		    </td>	                
		    </logic:notEqual>            
            <td align="center" class="TableRow" nowrap>
		     	<bean:message key="tab.synthesis.info"/>          	
		    </td>            	      	          	    	    	    		        	        	        
           </tr>
   	  </thead>

          <hrms:paginationdb id="element" name="statForm" sql_str="statForm.strsql" table="" where_str="statForm.cond_str" columns="statForm.columns" order_by="statForm.order_by" pagerows="21" page_id="pagination">
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
	    <!-- 
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
                 <a href="/workbench/browse/showselfinfo.do?b_search=link&userbase=<bean:write name="statForm" property="userbase" filter="true"/>&a0100=<bean:write name="element" property="a0100" filter="true"/>&flag=notself&returnvalue=2" target="il_body"><bean:write name="element" property="a0101" filter="true"/></a>&nbsp;
	    </td>        
	     -->
            <logic:iterate id="fielditem"  name="statForm"  property="fieldlist" indexId="index">
              <logic:notEqual name="fielditem" property="priv_status" value="0">             
              <td align="left" class="RecordRow" nowrap>
                 <logic:notEqual name="fielditem" property="codesetid" value="0">
          	   <hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
          	   <bean:write name="codeitem" property="codename" />&nbsp;                    
                 </logic:notEqual>
                 <logic:equal name="fielditem" property="codesetid" value="0">
                   <bean:write name="element" property="${fielditem.itemid}" filter="false"/>&nbsp;                 
                 </logic:equal>                                
	      </td>   
	      </logic:notEqual>	 	                          
            </logic:iterate> 
      	    <logic:notEqual name="statForm" property="tabid" value="-1">
              <td align="center" class="RecordRow" nowrap>
               		<a href="###" onclick="winopen('/general/inform/synthesisbrowse/synthesiscard.do?b_search=link&userbase=${statForm.userbase}&inforkind=${statForm.infokind}&tabid=${statForm.tabid}&multi_cards=-1','<bean:write name="element" property="a0100" filter="true"/>');"><img src="../../images/table.gif" border="0"></a>
		      </td>	                
            </logic:notEqual>	            	
            <td align="center" class="RecordRow" nowrap>
                 <a href="###" target="" onclick="winhref('/workbench/browse/showselfinfo.do?b_search=link&userbase=<bean:write name="statForm" property="userbase" filter="true"/>&flag=notself&returnvalue=2','il_body','<bean:write name="element" property="a0100" filter="true"/>')"><img src="../../images/view.gif" border="0"></a>&nbsp;
		    </td>                          	    	    	    		        	        	        
          </tr>
        </hrms:paginationdb>
        
</table>
</td>
</tr>
<tr>
<td>
<table  width="100%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">第
					<bean:write name="pagination" property="current" filter="true" />
					页
					共
					<bean:write name="pagination" property="count" filter="true" />
					条
					共
					<bean:write name="pagination" property="pages" filter="true" />
					页
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="statForm" property="pagination" nameId="statForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table>
</td>
</tr>
</table>
<table  width="70%" align="center">
          <tr>
            <td align="left">         	
	 	    <input type="button" name="addbutton"  value="<bean:message key="button.query.viewphoto"/>" class="mybutton" onclick='viewPhoto();' >  	
	 	    <input type="button" name="addbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="returnH('${statForm.flag}');" >
	 	   	   
            </td>            
          </tr>          
</table>
</html:form>
