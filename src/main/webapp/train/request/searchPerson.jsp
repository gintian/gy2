<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<SCRIPT LANGUAGE=javascript src="/js/common.js"></SCRIPT>
<style type="text/css"> 
#strTable{
           border: 1px solid #C4D8EE;
           height: 266px;    
           width: 98%;           
           overflow-y:auto;            
           margin: 1em 1;
           margin-left:5px;
           position:absolute;
}

.record {
    border-left: 0px;word-break:break-all;padding-left:5px;
}
</style>
<script language="JavaScript">
function selectAll(obj){
	if(obj.checked==true){
		checkAll();
	}else{
		clearAll();
	}
}
function setOk(){
	var selectvalue = "";
	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++){
		if(tablevos[i].type=="checkbox"&&tablevos[i].value!='selectall'){
			if(tablevos[i].checked==true){
				selectvalue+=tablevos[i].value+"`"
			}
      	 }
   	}
   	if(selectvalue!=null&&selectvalue.length>1){
   		window.returnValue=selectvalue;
   		window.close();
   	}
}
</script>
<html:form action="/general/inform/search/generalsearch">
<table border="0" align="center" width="100%">
	<tr><td>&nbsp;</td></tr>
	<tr>
		<td>
		<fieldset align="center" style="width:98%;height:328;">
      	<legend><bean:message key='train.b_plan.trains.selectperson'/></legend>
      	<div id="strTable" class="common_border_color">
		<table border="0" class="ListTable1" width="100%" style="border-right: 0px;">
			<tr class="fixedHeaderTr1">
				<td align="center" width="50" class="TableRow" style="border-left: 0px;" nowrap>
					<input type="checkbox" name="selectall" value="selectall" onclick="selectAll(this);">
				</td> 
				<td align="center" width="90" class="TableRow" style="border-right: 0px;" nowrap><bean:message key='label.query.dbpre'/></td>
				<td align="center" width="170" class="TableRow" nowrap><bean:message key='label.title.org'/></td> 
				<td align="center" width="170" class="TableRow" nowrap><bean:message key='label.title.dept'/></td> 
				<td align="center" width="90" class="TableRow" nowrap><bean:message key='label.title.name'/></td> 
			</tr>
			<hrms:paginationdb id="element" name="searchInformForm" sql_str="searchInformForm.sqlstr" 
			table="" where_str="" columns="A0100,A0101,B0110,E0122,B0110_desc,E0122_desc,dbpre,dbname"
			 order_by="order by dbid,b0110,e0122" 
			 page_id="pagination" pagerows="100">
			 <bean:define id="a0100" name='element' property='a0100'/>
			 <bean:define id="a0101" name='element' property='a0101'/>
			 <bean:define id="b0110" name='element' property='b0110'/>
			 <bean:define id="e0122" name='element' property='e0122'/>
			 <bean:define id="e0122_desc" name='element' property='e0122_desc'/>
			 <bean:define id="b0110_desc" name='element' property='b0110_desc'/>
			 <bean:define id="dbname" name='element' property='dbname'/>
			  <bean:define id="dbpre" name='element' property='dbpre'/>
		     <tr> 
			 	<td class="RecordRow" align="center"> 
			 	<%  a0100 = SafeCode.encode(PubFunc.encrypt(a0100.toString())); 
			 	    e0122 = SafeCode.encode(PubFunc.encrypt("UM"+ e0122));
			 		dbpre = SafeCode.encode(PubFunc.encrypt(dbpre.toString()));
			 	    b0110 = SafeCode.encode(PubFunc.encrypt("UN"+ b0110)); %>
			   <input type="checkbox" name="<%=a0100+"_"+dbpre%>" value="<%=b0110!=null&&b0110.toString().length()>0?e0122!=null&&e0122.toString().length()>0?
			       a0100+"::"+a0101
			   		+"::"+ e0122+"::"+e0122_desc+"::"+dbpre:a0100+"::"+a0101
			   		+"::"+ b0110+"::"+b0110_desc+"::"+dbpre:a0100+"::"+a0101
			   		+"::root::"+e0122_desc+"::"+dbpre%>">
			 	</td>
			 	<td class="RecordRow record"><bean:write name="element" property="dbname" filter="false"/></td>
			 	<td class="RecordRow record"><bean:write name="element" property="b0110_desc" filter="false"/></td> 
			 	<td class="RecordRow record"><bean:write name="element" property="e0122_desc" filter="false"/></td>
			 	<td class="RecordRow record">
			 		<div STYLE="width: 100px; overflow:hidden; text-overflow:ellipsis">
			 			<span>${a0101}</span>
			 		</div>
			 	</td>
			 </tr>
			 </hrms:paginationdb>
	   </table>
</div>
<%UserView userView=(UserView)session.getAttribute(WebConstant.userView);
if("hcm".equalsIgnoreCase(userView.getBosflag())){%>
<table border="0" width="98%" style="position:absolute;left:20;top:330">
<%}else{ %>
<table border="0" width="98%" style="position:absolute;left:20;top:313">
<%} %>
	<tr>
		<td valign="bottom" class="tdFontcolor" nowrap>
		  	<bean:message key="label.page.serial"/>
		  	<bean:write name="pagination" property="current" filter="true" />
			<bean:message key="label.page.sum"/>
			<bean:write name="pagination" property="count" filter="true" />
			<bean:message key="label.page.row"/>
			<bean:write name="pagination" property="pages" filter="true" />
			<bean:message key="label.page.page"/>
   		</td>
       	<td nowrap class="tdFontcolor">
            <p align="right"><hrms:paginationdblink name="searchInformForm" property="pagination" nameId="searchInformForm" scope="page">
    		</hrms:paginationdblink>
   		</td>
	</tr>
</table>
</fieldset>
		</td>
	</tr>
</table>

<center>
<input type="button" value="<bean:message key='kq.formula.true'/>" onclick="setOk();" class="mybutton"> 
<input type="button" value="<bean:message key='kq.register.kqduration.cancel'/>" onclick="window.close();" class="mybutton"> 
</center>
</html:form>
