<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean" %>
<%@ page import="com.hjsj.hrms.utils.PubFunc" %>
<%
  UserView userView=(UserView)session.getAttribute(WebConstant.userView);
  String url="";
  if(userView != null)
  {
     url=userView.getBosflag();
  
  }
%>
<script language="javascript">
  function winhref(url,target,a0100)
  {
   if(a0100=="")
      return false;
   var o_obj=document.getElementById('a0100');   
   if(o_obj)
     o_obj.value=a0100;   
   staticFieldForm.action=url;
   staticFieldForm.target=target;
   staticFieldForm.submit();
  }  
  document.oncontextmenu = function() {return false;}
   function back()
   {
     
     
   }
   function winopen(url,a0100)
  {
      if(a0100=="")
        return false;
        var o_obj=document.getElementById('a0100');   
      if(o_obj)
        o_obj.value=a0100;   
      //window.open(url,"_blank","left=0,top=0,width="+screen.availWidth+",height="+screen.availHeight+",scrollbars=yes,toolbar=yes,menubar=yes,location=yes,resizable=no,status=yes");
       staticFieldForm.action=url;
       staticFieldForm.target="_blank";
       staticFieldForm.submit();
  }  
  function returnH(flag)
  {
      if(flag=="simple")
      {
         staticFieldForm.action="/general/static/simple_static.do?b_next=link&result=${staticFieldForm.result}&history=${staticFieldForm.history}";
         staticFieldForm.target="_self";
         staticFieldForm.submit();
      }else if(flag=="general")
      {
         staticFieldForm.action="/general/static/general_static.do?b_next=link&result=${staticFieldForm.result}&history=${staticFieldForm.history}";
         staticFieldForm.target="_self";
         staticFieldForm.submit();
      }
                   	
  }  
</script>
<%int i=0;%>
<hrms:themes />
<%if("hcm".equalsIgnoreCase(url)){ %>
<style>
.ListTable{
	width:expression(document.body.clientWidth-10);
	margin-left:0px;
	margin-top:4px;
}
</style>
<%}else{ %>
<style>
.ListTable{
	width:expression(document.body.clientWidth-10);
	margin-left:1px;
	margin-top:3px;
}
</style>
<%} %>
<html:form action="/general/static/static_data">
<input type="hidden" name="a0100" id="a0100">
<logic:equal name="staticFieldForm" property="infor_Flag" value="1">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">

<tr>
  <td>
   <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	   <thead>   
   	   		<td align="center" class="TableRow" nowrap>
             人员库   
	    </td>        
           <logic:iterate id="element"    name="staticFieldForm"  property="fieldlist" indexId="index">
              <logic:notEqual name="element" property="priv_status" value="0">               
                <td align="center" class="TableRow" nowrap>
                   <bean:write  name="element" property="itemdesc" filter="true"/>
	      		</td>  
  	          </logic:notEqual>	 
            </logic:iterate>
      	    <logic:notEqual name="staticFieldForm" property="tabid" value="-1">
            <td align="center" class="TableRow" nowrap>
		     	<bean:message key="tab.base.info"/>          	
		    </td>	                
		    </logic:notEqual>            
            <td align="center" class="TableRow" nowrap>
		     	<bean:message key="tab.synthesis.info"/>          	
		    </td>        
   	  </thead>
          <hrms:paginationdb id="element" name="staticFieldForm" sql_str="staticFieldForm.strsql" table="" where_str="staticFieldForm.cond_str" columns="staticFieldForm.columns" order_by="staticFieldForm.order_by" distinct="staticFieldForm.distinct" pagerows="21" page_id="pagination">
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
          <bean:define id="db" name="element" property="db"></bean:define>
          <td align="left" class="RecordRow" nowrap>  
	    	<%=com.hrms.frame.utility.AdminCode.getCodeName("@@",((String)pageContext.getAttribute("db")).substring(1)) %>
	    </td>
	    <logic:iterate id="fielditem"  name="staticFieldForm"  property="fieldlist" indexId="index">
              <logic:notEqual name="fielditem" property="priv_status" value="0">             
              <td align="left" class="RecordRow" nowrap>  
                 <logic:notEqual name="fielditem" property="codesetid" value="0">
          	        <logic:equal name="fielditem" property="codesetid" value="UM">
          	         <hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem"  uplevel="${staticFieldForm.uplevel}" scope="page"/>  	      
          	          <!-- 
          	            	//tianye update start
							//关联部门的指标支持指定单位（部门中查不出信息就去单位中查找）
          	            	-->
          	            	<logic:notEqual  name="codeitem" property="codename" value="">
          	           			<bean:write name="codeitem" property="codename" /> 
          	           		</logic:notEqual>
          	          		<logic:equal  name="codeitem" property="codename" value="">
          	          		 	<hrms:codetoname codeid="UN" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page" uplevel="${staticFieldForm.uplevel}"/>  
          	           			<bean:write name="codeitem" property="codename" /> 
          	           		</logic:equal>   
          	           		<!-- end --> 
          	       </logic:equal>
          	        <logic:notEqual name="fielditem" property="codesetid" value="UM">
          	         <hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
          	         <bean:write name="codeitem" property="codename" />  
          	       </logic:notEqual>              
                 </logic:notEqual>
                 <logic:equal name="fielditem" property="codesetid" value="0">
                   <bean:write name="element" property="${fielditem.itemid}" filter="false"/>               
                 </logic:equal>  
	      </td>   
	      </logic:notEqual>	 	                          
         </logic:iterate>
              <%
                  LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
                  String a0100_encrypt= PubFunc.encrypt((String)abean.get("a0100"));
              %>
         <logic:notEqual name="staticFieldForm" property="tabid" value="-1">
              <td align="center" class="RecordRow" nowrap>
               		<a href="javascript:winopen('/general/inform/synthesisbrowse/synthesiscard.do?b_search=link&userbase=<%=((String)pageContext.getAttribute("db")).substring(1) %>&inforkind=${staticFieldForm.infor_Flag}&tabid=${staticFieldForm.tabid}&multi_cards=-1','<%=a0100_encrypt%>');"><img src="/images/table.gif" border="0"></a>
		      </td>	                
         </logic:notEqual>	
            <td align="center" class="RecordRow">	         
              <a href="javascript:winhref('/workbench/browse/showselfinfo.do?b_search=link&userbase=<%=((String)pageContext.getAttribute("db")).substring(1) %>&flag=notself&returnvalue=simple5','il_body','<%=a0100_encrypt%>');"><img src="/images/view.gif" border="0"></a>
	        </td>	    	    	    		        	        	        
          </tr>
        </hrms:paginationdb>  
   </table>
 </td>             	    	    	    		        	        	        
 </tr>
 <tr>
  <td>
    <table  width="100%" class="RecordRowP" align="center">

		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="staticFieldForm" property="pagination" nameId="staticFieldForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
   </table>
  </td>
 </tr>
</table>

<table  width="100%" align="center" style="margin-top:3px;margin-left:3px;">
          <tr>
            <td align="center">
         	    <input type="button" name="addbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="returnH('${staticFieldForm.stat_type}');" >
            </td>            
          </tr>          
</table>
</logic:equal>
<logic:equal name="staticFieldForm" property="infor_Flag" value="2">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	   <thead>
           <tr>
             <logic:iterate id="element"    name="staticFieldForm"  property="fieldlist" indexId="index">                    
                <td align="center" class="TableRow" nowrap>
                   <bean:write  name="element" property="itemdesc" filter="true"/>
	      		</td>    	         
  	        </logic:iterate>                 	    	    	    		        	        	        
           </tr>
   	  </thead>
          <hrms:paginationdb id="element" name="staticFieldForm" sql_str="staticFieldForm.strsql" table="" where_str="staticFieldForm.cond_str" columns="staticFieldForm.columns" order_by="staticFieldForm.order_by" pagerows="21" page_id="pagination">
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
            <logic:iterate id="fielditem"  name="staticFieldForm"  property="fieldlist" indexId="index">
              <td align="left" class="RecordRow" nowrap> 
                 <logic:notEqual name="fielditem" property="codesetid" value="0">
                      <logic:equal name="fielditem" property="itemid" value="b0110">
                        <a href="/general/static/commonstatic/statshowinfodata.do?br_infodata=link&a0100=<bean:write name="element" property="${fielditem.itemid}" filter="true"/>" target="mil_body">
          	                  <hrms:codetoname codeid="UN" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
                               <bean:write name="codeitem" property="codename" />
                          	  <hrms:codetoname codeid="UM" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
                               <bean:write name="codeitem" property="codename" />
                          	</a>
                      </logic:equal>
                      <logic:notEqual name="fielditem" property="itemid" value="b0110">
	                      	<logic:equal name="fielditem" property="codesetid" value="UM">
	                       <hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
	          	            <!-- 
          	            	//tianye update start
							//关联部门的指标支持指定单位（部门中查不出信息就去单位中查找）
          	            	-->
          	            	<logic:notEqual  name="codeitem" property="codename" value="">
          	           			<bean:write name="codeitem" property="codename" /> 
          	           		</logic:notEqual>
          	          		<logic:equal  name="codeitem" property="codename" value="">
          	          		 	<hrms:codetoname codeid="UN" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page" uplevel="${staticFieldForm.uplevel}"/>  
          	           			<bean:write name="codeitem" property="codename" /> 
          	           		</logic:equal>   
          	           		<!-- end -->             
	                   		</logic:equal>
	                   	 	<logic:notEqual name="fielditem" property="codesetid" value="UM">
	                       <hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
	          	           	<bean:write name="codeitem" property="codename" />             
	                   		</logic:notEqual>
                    </logic:notEqual>     
                 </logic:notEqual>
                 <logic:equal name="fielditem" property="codesetid" value="0">
                   <bean:write name="element" property="${fielditem.itemid}" filter="false"/>               
                 </logic:equal>  
               </td>
            </logic:iterate>                   	    	    	    		        	        	        
          </tr>
        </hrms:paginationdb>

 <tr>
  <td>
    <table  width="100%" class="RecordRowP" align="center">

		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="staticFieldForm" property="pagination" nameId="staticFieldForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
   </table>
  </td>
 </tr>        
</table>

<table  width="100%" align="center" style="margin-top:3px;margin-left:3px;">
          <tr>
            <td align="center">
       	 	          	   
            </td>            
          </tr>          
</table>
</logic:equal>
<logic:equal name="staticFieldForm" property="infor_Flag" value="3">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
<tr>
  <td>
   <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	   <thead>
           <tr>
            <logic:iterate id="element"    name="staticFieldForm"  property="fieldlist" indexId="index">                    
                <td align="center" class="TableRow" nowrap>
                   <bean:write  name="element" property="itemdesc" filter="true"/>
	      		</td>    	         
  	        </logic:iterate>              	    	    	    		        	        	        
           </tr>
   	  </thead>
   	
          <hrms:paginationdb id="element" name="staticFieldForm" sql_str="staticFieldForm.strsql" table="" where_str="staticFieldForm.cond_str" columns="staticFieldForm.columns" order_by="staticFieldForm.order_by" pagerows="21" page_id="pagination">
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
            <logic:iterate id="fielditem"  name="staticFieldForm"  property="fieldlist" indexId="index">
              <td align="left" class="RecordRow" nowrap> 
                 <logic:notEqual name="fielditem" property="codesetid" value="0">
                      <logic:equal name="fielditem" property="codesetid" value="@K">
                      <a href="/general/static/commonstatic/statshowinfodata.do?br_infodata=link&a0100=<bean:write name="element" property="e01a1" filter="true"/>" target="mil_body">
                      <hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
          	           <bean:write name="codeitem" property="codename" />
          	          </a>
          	                 
                    </logic:equal>
          	         <logic:notEqual name="fielditem" property="codesetid" value="@K">
                       <hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
          	           <bean:write name="codeitem" property="codename" />             
                    </logic:notEqual>     
                 </logic:notEqual>
                 <logic:equal name="fielditem" property="codesetid" value="0">
                   <bean:write name="element" property="${fielditem.itemid}" filter="false"/>               
                 </logic:equal>  
          	   
          	 
	           </td>       
	        </logic:iterate>          	    	    	    		        	        	        
          </tr>
        </hrms:paginationdb>
   </table>
 </td>             	    	    	    		        	        	        
 </tr>        
 <tr>
  <td>
    <table  width="100%" class="RecordRowP" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="staticFieldForm" property="pagination" nameId="staticFieldForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
   </table>
  </td>
 </tr>          
</table>

<table  width="100%" align="center" style="margin-top:3px;margin-left:3px;">
          <tr>
            <td align="center">
       	 	     
            </td>            
          </tr>          
</table>
</logic:equal>
</html:form>
<script>
if(!getBrowseVersion()|| getBrowseVersion()==10){
	var form = document.getElementsByName('staticFieldForm')[0];
	form.style.width = '99%'; 
}
</script>