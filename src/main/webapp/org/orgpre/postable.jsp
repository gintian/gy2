<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
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
	height:expression(document.body.clientHeight-160);
	width:100%; 
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; 
}
.headerTr{ 
	position:relative; 
	top:expression(this.offsetParent.scrollTop-1); 
}
</style>
<hrms:themes></hrms:themes>
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
//-->
</script>
<html:form action="/org/orgpre/postable">
<%int i=0;%>
<bean:message key='kq.shift.relief.name'/>：${orgPreForm.dpname}
<div class="myfixedDiv common_border_color">
<table  border="0" cellspacing="0" width="100%" cellpadding="0" >
	<tr class="fixedHeaderTr">
	    <logic:iterate id="item" name="orgPreForm" property="fieldlist">
	         <td align="center" style="border-left:none;border-top:none;" class="TableRow" nowrap>${item.itemdesc }</td> 
	    </logic:iterate>
		
		<td align="center" class="TableRow" style="border-left:none;border-top:none;" nowrap><bean:message key='org.autostatic.mainp.several.plans'/></td>  
		<td align="center" class="TableRow" style="border-left:none;border-top:none;" nowrap><bean:message key='org.autostatic.mainp.several.real'/></td>  
		<logic:equal value="1" name="orgPreForm" property="ps_parttime">
			<td align="center" class="TableRow" style="border-left:none;border-top:none;" nowrap>兼职数</td> 
		</logic:equal>
		<td align="center"  style="border-left:none;border-top:none;border-right:none;" class="TableRow" nowrap><bean:message key='system.options.customreport.tableinfo.edite'/></td>  
	</tr>
	<hrms:paginationdb id="element" name="orgPreForm" sql_str="orgPreForm.sql" table="" where_str="orgPreForm.wherestr" 
	columns="orgPreForm.columns" order_by="orgPreForm.orderby" page_id="pagination" pagerows="${orgPreForm.pagerows}" >
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
    	<td align="left" class="RecordRow" style="border-left:none;border-left:none;border-top:none;" nowrap>
          <hrms:extendscodetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" uplevel="3" scope="page"/>  	      
          &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;                                                 
	     </td> 
	     <td align="left" class="RecordRow" style="border-left:none;border-top:none;" nowrap>
          	<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" uplevel="${orgPreForm.level }" scope="page"/>  	      
          	&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;                                                   
	     </td> 
	     <td align="left" class="RecordRow" style="border-left:none;border-top:none;" nowrap>
          	<hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>  	      
          	&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;                                                   
	     </td> 
	     <td align="right" class="RecordRow" style="border-left:none;border-top:none;" nowrap>
                &nbsp;<bean:write name="element" property="${orgPreForm.planitem}"/>&nbsp;                                                                    
	     </td> 
	     <td align="right" class="RecordRow" style="border-left:none;border-top:none;" nowrap>
                &nbsp;<a href="###" onclick="showstatnum('<bean:write name="element" property="e01a1"/>','${orgPreForm.realitem}')"><bean:write name="element" property="${orgPreForm.realitem}"/></a>&nbsp;                                                                    
	     </td> 
	     <logic:equal value="1" name="orgPreForm" property="ps_parttime">
			<td align="right" class="RecordRow" style="border-left:none;border-top:none;" nowrap>
                &nbsp;<a href="###" onclick="showparttimenum('<bean:write name="element" property="e01a1"/>','${orgPreForm.ps_workparttime}')"><bean:write name="element" property="${orgPreForm.ps_workparttime}"/></a>&nbsp;                                                                    
	     	</td>
		</logic:equal>
	      <td align="center" class="RecordRow" style="border-left:none;border-top:none;border-right:none;" nowrap>
                &nbsp;
                <a href="###" onclick="javascript:showOrgContext('<bean:write name="element" property="e01a1"/>');editorg();"><img src="/images/edit.gif" border="0"></a>
                &nbsp;                                                                    
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
<div align="left" style="width: 100%;margin-top: 5px;"><input type="button" value="<bean:message key='reportcheck.return'/>" onclick="goBack();" class="mybutton"></div>
</html:form>
<script type="text/javascript">
function goBack(){
	orgPreForm.action="/org/orgpre/orgpretable.do?b_query=link&a_code=${orgPreForm.a_code}&infor=${orgPreForm.infor}&unit_type=${orgPreForm.unit_type}&nextlevel=${orgPreForm.nextlevel}";
   	orgPreForm.submit();
}
function showstatnum(b0110,fielditemid){
	//alert(b0110+fielditemid);
	orgPreForm.action="/org/orgpre/showstatnum.do?b_showstatnum=link&b0110="+b0110+"&setid=${orgPreForm.setid}&a_code=${orgPreForm.a_code}&infor=${orgPreForm.infor}&unit_type=${orgPreForm.unit_type}&nextlevel=${orgPreForm.nextlevel}&fielditemid="+fielditemid+"&fromway=duty&parttime=0";
   	orgPreForm.submit();
}
function showparttimenum(b0110,fielditemid){
	//alert(b0110+fielditemid);
	orgPreForm.action="/org/orgpre/showstatnum.do?b_showstatnum=link&b0110="+b0110+"&setid=${orgPreForm.setid}&a_code=${orgPreForm.a_code}&infor=${orgPreForm.infor}&unit_type=${orgPreForm.unit_type}&nextlevel=${orgPreForm.nextlevel}&fielditemid="+fielditemid+"&fromway=duty&parttime=1";
   	orgPreForm.submit();
}
</script>