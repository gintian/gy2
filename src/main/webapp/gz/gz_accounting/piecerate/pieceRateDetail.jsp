<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript" src="/js/dict.js"></script> 
<script type="text/javascript"
	src="/gz/gz_accounting/piecerate/pieceratedetail.js"></script>
<%@page import="com.hjsj.hrms.actionform.gz.gz_accounting.piecerate.PieceRateDetailForm"%>
<%
PieceRateDetailForm bform=(PieceRateDetailForm)session.getAttribute("pieceRateDetailForm");
String s0100=bform.getS0100();
String tableName=bform.getTableName();
String canEdit=bform.getCanEdit();
 %> 
<script language='javascript'>
	var queryhidden=0;
	function visiblequery(){
	   if(queryhidden==0) {
	      var queryblank=document.getElementById("tblname");
	      if(queryblank)   queryblank.style.display="block";
	      queryhidden=1;

      	var obj=document.getElementById("querydesc");
      	obj.innerHTML="[&nbsp;<a href=\"javascript:visiblequery();\" >查询隐藏&nbsp;</a>]&nbsp;&nbsp;&nbsp;";
	   }
	   else
	   {
	       var queryblank=document.getElementById("tblname");
	       if(queryblank)     queryblank.style.display="none";
	      queryhidden=0;
      	var obj=document.getElementById("querydesc");
      	obj.innerHTML="[&nbsp;<a href=\"javascript:visiblequery();\" >查询显示&nbsp;</a>]&nbsp;&nbsp;&nbsp;";
	   }
	}
	
	function queryR(){
		var txtname=document.getElementById("txtname");
		var value=txtname.value;
		
		pieceRateDetailForm.action="/gz/gz_accounting/piecerate/search_piecerate_detail.do?b_query=link"
		     +"&s0100="+"${pieceRateDetailForm.s0100}"
		     +"&personname="+getEncodeStr(value);
		pieceRateDetailForm.submit(); 


	}
</script>
<html:form action="/gz/gz_accounting/piecerate/search_piecerate_detail" >
<html:hidden property="s0100" name="pieceRateDetailForm"/>
<html:hidden property="busiid" name="pieceRateDetailForm"/>
<html:hidden property="tableName" name="pieceRateDetailForm"/>
<html:hidden property="infoStr" name="pieceRateDetailForm"/>
<html:hidden property="canEdit" name="pieceRateDetailForm"/>
<table width="100%" align="left" >
	<tr>
	  <td>
        <span>&nbsp;${pieceRateDetailForm.infoStr}</span> 
        <logic:equal  name="pieceRateDetailForm" property="tableName" value="S05"> 
         &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
          <span id="querydesc" > &nbsp;&nbsp;[&nbsp;
   		  <a href="javascript:visiblequery();" >查询显示&nbsp;</a>]&nbsp;&nbsp;&nbsp;</span> 
      </logic:equal>
	  </td>
	  
	  
	</tr>
	<tr>
	<td>
 <table style="align:left;display:none" id ="tblname">
 	<tr>
		<td>
 	 		&nbsp;姓名&nbsp;<input type="text" id="txtname" class="text4" value="" />&nbsp;&nbsp;&nbsp;
 		</td>
 		<td>
 			<input type="button" name="query" value="查询" class="mybutton" onclick="queryR();"/>&nbsp;&nbsp;&nbsp;
 		</td>
	</tr>
</table>
</td>
	</tr>
	<tr>
	<td valign="top">
	<logic:equal  name="pieceRateDetailForm" property="tableName" value="S04"> 
		<hrms:dataset name="pieceRateDetailForm" property="fieldlist" scope="session" setname="${pieceRateDetailForm.tableName}" 
		      	pagerows="${pieceRateDetailForm.pagerows}" setalias="detail" readonly="false"  editable="true" select="true" sql="${pieceRateDetailForm.sql}" 
		      	  buttons="bottom">
		   <%if(canEdit.equalsIgnoreCase("true")){ %>   	  
			  <hrms:commandbutton name="btn_insert" functionId="" function_id="324210901" type="selected" setname="${pieceRateDetailForm.tableName}" onclick="handSelectProduct()">
			    <bean:message key="button.insert"/>  
			  </hrms:commandbutton> 
			  <hrms:commandbutton name="btn_save" functionId="3020091035" function_id="324210902" refresh="false" type="all-change" setname="${pieceRateDetailForm.tableName}">
			    <bean:message key="button.save"/>
			  </hrms:commandbutton>
			  
			  <hrms:commandbutton name="btn_delete" hint=""  refresh="true" function_id="324210903" type="selected" setname="${pieceRateDetailForm.tableName}" onclick="deleteproducts('${pieceRateDetailForm.tableName}')">
			    <bean:message key="button.delete"/>
			  </hrms:commandbutton>   
	   <%} %>
		  <hrms:commandbutton name="btn_return" hint=""  refresh="true" function_id="" type="selected" setname="${pieceRateDetailForm.tableName}" onclick="goback();">
		    <bean:message key="button.return"/>
		  </hrms:commandbutton>   
		
		</hrms:dataset>	
     </logic:equal>
	
	<logic:equal  name="pieceRateDetailForm" property="tableName" value="S05"> 
		<hrms:dataset name="pieceRateDetailForm" property="fieldlist" scope="session" setname="${pieceRateDetailForm.tableName}" 
		      	pagerows="${pieceRateDetailForm.pagerows}" setalias="detail" readonly="false"  editable="true" select="true" sql="${pieceRateDetailForm.sql}" 
		      	  buttons="bottom">
		   <%if(canEdit.equalsIgnoreCase("true")){ %>   
			  <hrms:commandbutton name="btn_condsel" functionId="" function_id="324211001" type="selected" setname="${pieceRateDetailForm.tableName}"  onclick="conditionselect('${pieceRateDetailForm.dbname}')" >
			    <bean:message key="train.examstudent.selbycond"/>  
			  </hrms:commandbutton> 
			  <hrms:commandbutton name="btn_handsel" functionId="" function_id="324211002" refresh="false" type="all-change" setname="${pieceRateDetailForm.tableName}" onclick="handSelectPeople()">
			    <bean:message key="train.examstudent.selbyhand"/>
			  </hrms:commandbutton>
			  <hrms:commandbutton name="btn_save" functionId="3020091035" function_id="324211003" refresh="false" type="all-change" setname="${pieceRateDetailForm.tableName}">
			    <bean:message key="button.save"/>
			  </hrms:commandbutton>
			  <hrms:commandbutton name="btn_delete" hint=""  refresh="true" function_id="324211004" type="selected" setname="${pieceRateDetailForm.tableName}" onclick="deleteobjs('${pieceRateDetailForm.tableName}')">
			    <bean:message key="button.delete"/>
			  </hrms:commandbutton>   
			  
			  <hrms:commandbutton name="btn_calc" hint=""  refresh="true" function_id="324211005" type="selected" setname="${pieceRateDetailForm.tableName}" onclick="calc('${pieceRateDetailForm.tableName}')">
			    <bean:message key="infor.menu.compute"/>
			  </hrms:commandbutton>   	  
		   <%} %>   
		  <hrms:commandbutton name="btn_Setcalc" hint=""  refresh="true" function_id="324211006" type="selected" setname="${pieceRateDetailForm.tableName}" onclick="setformula();">
		    <bean:message key="gz.premium.countformula"/>
		  </hrms:commandbutton>   		
		  <hrms:commandbutton name="btn_return" hint=""  refresh="true" function_id="" type="selected" setname="${pieceRateDetailForm.tableName}" onclick="goback();">
		    <bean:message key="button.return"/>
		  </hrms:commandbutton>   

		  
		</hrms:dataset>	
	</logic:equal>		
	
	</td>
	</tr>
</table>

</html:form>