<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<hrms:themes></hrms:themes>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,java.util.*"%>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String a_code="UN";
	if(userView != null){ 
	  	a_code=userView.getManagePrivCode()+userView.getManagePrivCodeValue();
	  	if(a_code==null||a_code.length()<3){
	  		a_code="UN";
	  	}             
	}
	
%>
<style>
.myfixedDiv
{ 
	overflow:auto; 
	height:expression(document.body.clientHeight-100);
	width:100%; 
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid;
}
.headerTr{ 
	position:relative; 
	top:expression(this.offsetParent.scrollTop-1); 
}
</style>
<script type="text/javascript">
<!--
var code="";
var kind="";
var orgtype="";
var parentid="";
function showOrgContext(codeitemid)
{
   var hashvo=new ParameterSet();
   hashvo.setValue("codeitemid",codeitemid);	
   var request=new Request({asynchronous:false,onSuccess:getContext,functionId:'0401004003'},hashvo);
}
function getContext(outparamters)
{
    code=outparamters.getValue("codeitemid");
	kind=outparamters.getValue("kind");
	orgtype=outparamters.getValue("orgtype");
	parentid=outparamters.getValue("parentid");
}
function editorg()
{

	var b='${orgPreForm.b0110}';
	//b=b.substring(2,b.length);
   orgPreForm.action="/workbench/dutyinfo/editorginfodata.do?b_search=link&edit_flag=edit&code="+code+"&kind="+kind+"&orgtype="+orgtype+"&parentid="+parentid+"&returnvalue=orgpre&b0110="+b+"&setid=${orgPreForm.setid}&a_code=${orgPreForm.a_code}&infor=${orgPreForm.infor}&unit_type=${orgPreForm.unit_type}&nextlevel=${orgPreForm.nextlevel}&postable=${orgPreForm.setid}";
   orgPreForm.submit();
}
 function winhrefOT(url,target,a0100)
{	   if(a0100=="")
      		return false;    
	   orgPreForm.action=url+"&a0100="+a0100;
	   orgPreForm.target=target;
	   orgPreForm.submit();
}
//-->
</script>
<html:form action="/org/orgpre/showstatnum">
<%int i=0;%>
<div class="myfixedDiv">
<table  border="0" cellspacing="0" width="100%" cellpadding="0" class="ListTable1">
	<thead>
           <logic:iterate id="element"    name="orgPreForm"  property="fieldlist" indexId="index">            
                <td align="center" class="TableRow" style="border-left:none;border-top:none;" nowrap>
                   <bean:write  name="element" property="itemdesc" filter="true"/>
	      		</td>   
            </logic:iterate>          
            <td align="center" class="TableRow" style="border-top:none;border-right:none;" nowrap>
		     	<bean:message key="tab.synthesis.info"/>          	
		    </td>        
   	  </thead>
	<hrms:paginationdb id="element" name="orgPreForm" sql_str="orgPreForm.sql" table="" where_str="orgPreForm.wherestr" 
	columns="orgPreForm.columns" order_by="orgPreForm.orderby" page_id="pagination" pagerows="${orgPreForm.pagerows}">
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
    	<logic:iterate id="fielditem"  name="orgPreForm"  property="fieldlist" indexId="index">
          
              <td align="left" class="RecordRow" style="border-left:none;" nowrap>&nbsp;  
                 <logic:notEqual name="fielditem" property="codesetid" value="0">
                   <logic:equal name="fielditem" property="codesetid" value="UM">
          	         <hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem"  uplevel="5" scope="page"/>  	      
          	         <bean:write name="codeitem" property="codename" />&nbsp;  
          	       </logic:equal>
          	        <logic:notEqual name="fielditem" property="codesetid" value="UM">
          	         <hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
          	         <bean:write name="codeitem" property="codename" />&nbsp;  
          	       </logic:notEqual>                  
                 </logic:notEqual>
                 <logic:equal name="fielditem" property="codesetid" value="0">
                   <bean:write name="element" property="${fielditem.itemid}" filter="false"/>&nbsp;               
                 </logic:equal>                                
	      </td>    	                          
         </logic:iterate> 	
            <td align="center" class="RecordRow" style="border-right:none;">
             <a href="javascript:winhrefOT('/workbench/browse/showselfinfo.do?b_search=link&userbase=<bean:write name="element" property="dbpre" filter="false"/>&flag=notself&returnvalue=statnum','mmil_body','<bean:write name="element" property="a0100" filter="false"/>');"><img src="/images/view.gif" border="0"></a>&nbsp;      
	         </td>	    	    	    		        	        	        
    </tr>
    </hrms:paginationdb>      
</table>
</div>
<table  border="0" width="100%" class="RecordRowP">
	<tr>
		<td valign="bottom" class="tdFontcolor">
		<hrms:paginationtag name="orgPreForm" pagerows="${orgPreForm.pagerows}" property="pagination" scope="page" refresh="true"></hrms:paginationtag>
		</td>
	    <td  align="right" nowrap class="tdFontcolor">
		     <p align="right"><hrms:paginationdblink name="orgPreForm" property="pagination" nameId="orgPreForm" scope="page">
			</hrms:paginationdblink>
		</td>
	</tr>
</table>
<table  border="0" width="100%">
	<tr>
		<td colspan="2" align="center"><input type="button" value="<bean:message key='reportcheck.return'/>" onclick="goBack();" class="mybutton"></td>
	</tr>
</table>
</html:form>
<script type="text/javascript">
function goBack(){
	<logic:equal value="duty" name="orgPreForm" property="fromway">
		orgPreForm.action="/org/orgpre/postable.do?b_query=link&b0110=${orgPreForm.rb0110 }&setid=${orgPreForm.setid}&a_code=${orgPreForm.a_code}&infor=${orgPreForm.infor}&unit_type=${orgPreForm.unit_type}&nextlevel=${orgPreForm.nextlevel}";
  </logic:equal>
   <logic:equal value="org" name="orgPreForm" property="fromway">
   		orgPreForm.action="/org/orgpre/orgpretable.do?b_query=link&a_code=${orgPreForm.a_code}&infor=${orgPreForm.infor}&unit_type=${orgPreForm.unit_type}&nextlevel=${orgPreForm.nextlevel}";
    </logic:equal>
   	orgPreForm.submit();
}

</script>