<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.general.query.QuickQueryForm"%>
<%
	UserView userView = (UserView) session
			.getAttribute(WebConstant.userView);
	String bosflag="";
	if(userView != null)
	{
		bosflag=userView.getBosflag();
	}
	QuickQueryForm quickQueryForm = (QuickQueryForm)session.getAttribute("quickQueryForm");
	String staff_sql = quickQueryForm.getSql();
	staff_sql = PubFunc.encrypt(staff_sql);
	
%>
<script language="javascript" src="/js/dict.js"></script> 
<SCRIPT LANGUAGE=javascript src="/js/common.js"></SCRIPT>
<script language="javascript">
    function allselect(flag,setname)
    {
           var tablename,table,dataset;
           tablename="table"+setname;
           table=$(tablename);
      	   if(table.length==0)
        	  return; 
           dataset=table.getDataset();  
	   	   var record=dataset.getFirstRecord();	
	   	   
	       while (record) 
	       {
	          if(flag=="1")
				  record.setValue("select","true");
			  else
				  record.setValue("select","false");			  
			  record=record.getNextRecord();	       
	       }
    }

    function selectAll(flag){
        var tablevos=document.getElementsByTagName("input");
        for(var i=0;i<tablevos.length;i++){
          	if(tablevos[i].type=="checkbox"){
          		if(flag=="1")
          			tablevos[i].checked=true;
          		else
          			tablevos[i].checked=false;
          	}
        }
    }
	function getSelectedData(infor,setname,sql){
	   var selectt=document.getElementsByName("selecttype");
	   var selectType="1";
	   for(var j=0;j<selectt.length;j++)
	   {
	       if(selectt[j].checked)
	       {
	          selectType=selectt[j].value;
	       }
	   }
	   if(selectType=='1')
	   {
	       var hashvo=new ParameterSet();
		   hashvo.setValue("infor",infor);
		   hashvo.setValue("sql","<%=staff_sql%>"); 
		   var request=new Request({method:'post',asynchronous:true,onSuccess:all_selectOK,functionId:'0202011018'},hashvo);
	   }
	   else
	   {
          var objlist=new Array();
          var tablevos=document.getElementsByTagName("input");
	      for(var i=0;i<tablevos.length;i++){
			 if(tablevos[i].type=="checkbox"){
				if(tablevos[i].checked==true&&tablevos[i].value!="selectall"){
					objlist.push(tablevos[i].value);	
				}
      	 	 }
   		  }
	     returnValue=objlist;
	     window.close();	
	   }	
	}
function selectAll(obj){
	if(obj.checked==true){
		checkAll();
	}else{
		clearAll();
	}
}
function all_selectOK(outparameters)
{
   var personlist=outparameters.getValue("list");
   var objlist=new Array();
   for(var i=0;i<personlist.length;i++)
   {
     objlist.push(personlist[i].dataName);
   }
  returnValue=objlist;
  window.close();	
}
</script>
<style>
.fixedDiv2{
	border-top:0;
	height:90%;
	margin-right: 0px !important;
}
.TableRow{
	border-top:0;
	border-right:0;
}
.RecordRow1{
	border-top:0;
	border-right:0;
}
.RecordRow_inside{
	border-top:0;
}
</style>
<%if("hcm".equalsIgnoreCase(bosflag)){ %>
<style>
.quickresultTable {
	height:expression(document.body.clientHeight-30);
	width:expression(document.body.clientWidth-20);
}
</style>
<%}else{ %>
<style>
.quickresultTable {
	margin-top:10px;
	height:expression(document.body.clientHeight-30);
	width:expression(document.body.clientWidth-10);
}
</style>
<%} %>

<base id="mybase" target="_self">
<html:form action="/general/query/quick/quick_result">
<%int i = 0;%>
<table width="70%" border="0" cellspacing="0"  align="center" cellpadding="0" class="quickresultTable">
<tr>
<td align="left" valign="middle" nowrap class="RecordRow" width="90%" style="border-top: none;border-left: none;border-right: none">
           <input type="radio" name="selecttype" value="1" checked/>全部
           <input type="radio" name="selecttype" value="0"/>手工选择
         	<hrms:submit styleClass="mybutton" property="br_pre">
            		<bean:message key="button.query.pre"/>
	 		</hrms:submit>	 	
         	<html:button styleClass="mybutton" property="b_ok" onclick="getSelectedData('${quickQueryForm.type}','${quickQueryForm.setname}');">
            		<bean:message key="button.ok"/>
	 		</html:button>
</td>
</tr>
<tr>
<td width="100%" nowrap>
<div class="fixedDiv2">
<table border="0" id=tab width="100%" cellspacing="0"  align="center" cellpadding="0">
	<tr class="fixedHeaderTr">
		<td align="center" width="30" class="TableRow_top" nowrap>
			<input type="checkbox" name="selectall" value="selectall" onclick="selectAll(this);">
		</td> 
		<logic:iterate id="info" name="quickQueryForm" property="showlist" >
		<logic:notEqual name="info" property="name" value="a0000">
		<logic:notEqual name="info" property="name" value="a0100">
		<logic:notEqual name="info" property="name" value="dbase">
		<td align="center" class="TableRow" nowrap>
			<bean:write name="info" property="label"/>
		</td> 
		</logic:notEqual>
		</logic:notEqual>
		</logic:notEqual>
		</logic:iterate>
	</tr>
	<hrms:paginationdb id="element" name="quickQueryForm" sql_str="${quickQueryForm.sql}" 
	table="" where_str="" columns="${quickQueryForm.cloums}" order_by="${quickQueryForm.orderby}" page_id="pagination" pagerows="${quickQueryForm.pagerows}">

    <%if (i % 2 == 0){%>
		<tr class="trShallow">
	<%} else{%>
		<tr class="trDeep">
	<%}i++;%>
	 	<td class="RecordRow_inside" align="center" nowrap>
	 		<logic:equal name="quickQueryForm" property="type" value="1">
	 		  <bean:define id="a0100" name='element' property='a0100'/>
	 		  <bean:define id="dbase" name='element' property='dbase'/>
	 		  	 <input type="checkbox" name="<%=dbase+""+a0100%>" value="<%=dbase+""+a0100%>">
	 		</logic:equal>
	 		<logic:equal name="quickQueryForm" property="type" value="2">
	 		  	<bean:define id="b0110" name='element' property='b0110'/>
	 		  	 <input type="checkbox" name="<%=b0110%>" value="<%=b0110%>">
	 		</logic:equal>
	 		<logic:equal name="quickQueryForm" property="type" value="3">
	 		  	<bean:define id="e01a1" name='element' property='e01a1'/>
	 		  	 <input type="checkbox" name="<%=e01a1%>" value="<%=e01a1%>">
	 		</logic:equal>
	 	</td> 
	 	<logic:iterate id="info" name="quickQueryForm" property="showlist" >
		<logic:notEqual name="info" property="name" value="a0000">
		<logic:notEqual name="info" property="name" value="a0100">
		<logic:notEqual name="info" property="name" value="dbase">
		<logic:notEqual  name="info" property="codesetid" value="0">  
			<td class="RecordRow1" nowrap>
				<hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.name}" codeitem="codeitem" scope="page"/>  	      
          	    <logic:notEqual name="codeitem" property="codename" value="">
          	    	<bean:write name="codeitem" property="codename"/>
          	    </logic:notEqual>
          	   <logic:equal name="codeitem" property="codename" value="">   
          	        <hrms:codetoname codeid="UN" name="element" codevalue="${info.name}" codeitem="codeitem" scope="page"/>  	      
          	    	<bean:write name="codeitem" property="codename"/>&nbsp;
          	    	<hrms:codetoname codeid="UM" name="element" codevalue="${info.name}" codeitem="codeitem" scope="page"/>  	      
          	    	<bean:write name="codeitem" property="codename"/>&nbsp;
          	    </logic:equal>
			</td>
		</logic:notEqual>
		<logic:equal  name="info" property="codesetid" value="0">
			<td class="RecordRow1" nowrap>
				<bean:write name="element" property="${info.name}" filter="true"/>&nbsp;
			</td>
		</logic:equal>
		</logic:notEqual>
		</logic:notEqual>
		</logic:notEqual>
		</logic:iterate>
	 </tr>
	 </hrms:paginationdb>
</table>
</div>
<table width="100%"  align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <hrms:paginationtag name="quickQueryForm"
								pagerows="${quickQueryForm.pagerows}" property="pagination"
								scope="page" refresh="true"></hrms:paginationtag>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="quickQueryForm" property="pagination" nameId="quickQueryForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table>
    </td>
</tr>
</table>
</html:form>
