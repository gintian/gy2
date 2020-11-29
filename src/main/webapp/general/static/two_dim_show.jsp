<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
  UserView userView=(UserView)session.getAttribute(WebConstant.userView);
  String bosflag="";
  if(userView != null)
  {
     bosflag=userView.getBosflag();
  
  }
%>
<script language="javascript"> 
   function photo()
   {
        staticFieldForm.action="/general/static/two_dim_show.do?b_photo=link&result=${staticFieldForm.result}";       
        staticFieldForm.submit();
   }
</script> 
<hrms:themes />
<%if("hcm".equalsIgnoreCase(bosflag)){ %>
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
<html:form action="/general/static/two_dim_show">
 <html:hidden name="staticFieldForm" property="result" styleClass="text"/>
<logic:equal name="staticFieldForm" property="infor_Flag" value="1">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
<tr>
  <td>
   <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	   <thead>
           <tr>
           <td align="center" class="TableRow" nowrap>
             人员库   
	    </td>
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="staticFieldForm" fieldname="B0110" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />
            </td>           
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="staticFieldForm" fieldname="E0122" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />
	    </td>
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="staticFieldForm" fieldname="E01A1" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />
	    </td>
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="staticFieldForm" fieldname="A0101" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />   
	    </td>          	    	    	    		        	        	        
           </tr>
   	  </thead>
          <hrms:paginationdb id="element" name="staticFieldForm" sql_str="staticFieldForm.strsql" table="" where_str="staticFieldForm.cond_str" columns="A0100,B0110,E0122,E01A1,A0101,UserName,db" order_by="staticFieldForm.order_by" pagerows="20" page_id="pagination">
	    <tr>
	    <bean:define id="db" name="element" property="db"></bean:define>
	    <td align="left" class="RecordRow" nowrap>  
	    	<%=com.hrms.frame.utility.AdminCode.getCodeName("@@",((String)pageContext.getAttribute("db")).substring(1)) %>
	    </td>
            <td align="left" class="RecordRow" nowrap>
          	<hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>  	      
          	<bean:write name="codeitem" property="codename" />
          	<hrms:codetoname codeid="UM" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>  	      
          	<bean:write name="codeitem" property="codename" />
          	
         	
	    </td>            
            <td align="left" class="RecordRow" nowrap>
          	<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" uplevel="${staticFieldForm.uplevel}" scope="page"/>     
          	<bean:write name="codeitem" property="codename" />            
	    </td>
            <td align="left" class="RecordRow" nowrap>
                <hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>  	      
          	<bean:write name="codeitem" property="codename" />    
	    </td>
            <td align="left" class="RecordRow" nowrap>
                 <a href="/workbench/browse/showselfinfo.do?b_search=link&userbase=<%=((String)pageContext.getAttribute("db")).substring(1) %>&a0100=<bean:write name="element" property="a0100" filter="true"/>&flag=notself&returnvalue=34&result=${staticFieldForm.result}" target="il_body"><bean:write name="element" property="a0101" filter="true"/></a>
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
		          <p align="right"><hrms:paginationdblink name="staticFieldForm" property="pagination" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table>
</td>
</tr>
</table>

<table  width="100%" align="center" border="0" cellspacing="0" cellpadding="0">
          <tr><td height="5px"></td></tr>
          <tr>
            <td align="left">
             <input type="button" name="bc_clear" value="<bean:message key="button.query.viewphoto"/>" class="mybutton" onclick="photo();">        	 
             <hrms:submit styleClass="mybutton" property="b_return">
            		<bean:message key="button.return"/>
	 	        </hrms:submit>  
	      
            </td>            
          </tr>          
</table>
</logic:equal>
<logic:equal name="staticFieldForm" property="infor_Flag" value="2">
<table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	   <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="staticFieldForm" fieldname="B0110" fielditem="fielditem"/>
	        <bean:write name="fielditem" property="dataValue" />
            </td>                	    	    	    		        	        	        
           </tr>
          
   	  </thead>
          <hrms:paginationdb id="element" name="staticFieldForm" sql_str="staticFieldForm.strsql" table="" where_str="staticFieldForm.cond_str" columns="B0110," order_by="staticFieldForm.order_by" pagerows="20" page_id="pagination">
	    <tr>
            <td align="left" class="RecordRow" nowrap>
          	 <a href="/general/static/show_company.do?br_infodata=link&a0100=<bean:write name="element" property="b0110" filter="true"/>&result=${staticFieldForm.result}" target="il_body">
          	 <hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>  	      
          	<bean:write name="codeitem" property="codename" />
          	<hrms:codetoname codeid="UM" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>  	      
          	<bean:write name="codeitem" property="codename" />
          	<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" scope="page"/>  	      
          	<bean:write name="codeitem" property="codename" />
          	</a>
	    </td>                         	    	    	    		        	        	        
          </tr>
        </hrms:paginationdb>
        
</table>
<table  width="100%" align="center">
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
		          <p align="right"><hrms:paginationdblink name="staticFieldForm" property="pagination"  scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table>
<table  width="100%" align="center" border="0" cellspacing="0" cellpadding="0">
          <tr><td height="5px"></td></tr>
          <tr>
            <td align="left">
       	 	  <logic:equal name="staticFieldForm" property="flag" value="1">
                    <hrms:submit styleClass="mybutton" property="br_return">
            		<bean:message key="button.return"/>
	 	    </hrms:submit>          
                 </logic:equal>
                 <logic:equal name="staticFieldForm" property="flag" value="2">
                    <hrms:submit styleClass="mybutton" property="br_returndouble">
            		<bean:message key="button.return"/>
	 	    </hrms:submit>  
                </logic:equal>        	   
            </td>            
          </tr>          
</table>
</logic:equal>
<logic:equal name="staticFieldForm" property="infor_Flag" value="3">
<table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	   <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="staticFieldForm" fieldname="E01A1" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />
            </td>                	    	    	    		        	        	        
           </tr>
   	  </thead>
   	
          <hrms:paginationdb id="element" name="staticFieldForm" sql_str="staticFieldForm.strsql" table="" where_str="staticFieldForm.cond_str" columns="e01a1," order_by="staticFieldForm.order_by" pagerows="20" page_id="pagination">
	    <tr>
            <td align="left" class="RecordRow" nowrap>
          	  <a href="/general/static/show_company.do?br_infodata=link&a0100=<bean:write name="element" property="e01a1" filter="true"/>&result=${staticFieldForm.result}" target="il_body">
          	 <hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>  	      
          	 <bean:write name="codeitem" property="codename" />
          	 <hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" scope="page"/>  	      
          	 <bean:write name="codeitem" property="codename" />
          	 <hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>  	      
          	 <bean:write name="codeitem" property="codename" />
          	</a>          	     
	    </td>                 	    	    	    		        	        	        
          </tr>
        </hrms:paginationdb>
        
</table>
<table  width="100%" align="center">
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
		          <p align="right"><hrms:paginationdblink name="staticFieldForm" property="pagination"  scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table>
<table  width="100%" align="center" border="0" cellspacing="0" cellpadding="0">
          <tr><td height="5px"></td></tr>
          <tr>
            <td align="left">
       	 	  <logic:equal name="staticFieldForm" property="flag" value="1">
                    <hrms:submit styleClass="mybutton" property="br_return">
            		<bean:message key="button.return"/>
	 	         </hrms:submit>          
                 </logic:equal>
                 <logic:equal name="staticFieldForm" property="flag" value="2">
                    <hrms:submit styleClass="mybutton" property="br_returndouble">
            		<bean:message key="button.return"/>
	 	          </hrms:submit>  
                </logic:equal>        	   
            </td>            
          </tr>          
</table>
</logic:equal>
</html:form>
<script>
setTimeout(function(){
	var buttons= document.getElementsByTagName('input');
	for( var i = 0 ; i < buttons.length ; i++){
		var type = buttons[i].getAttribute('type');
		if(type == 'submit' ){
			buttons[i].focus();
			buttons[i].blur();
		}
	}
},100);
</script>