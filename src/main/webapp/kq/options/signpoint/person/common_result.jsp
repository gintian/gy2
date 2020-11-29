<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.general.query.CommonQueryForm,		
			     java.util.*"%>
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
    
	function getSelectedData(infor,setname){
		   var tablename,table,dataset;
	        var checkselect="0";
			for(var i=0;i<document.commonQueryForm.checkselect.length;i++)
			{
				if(document.commonQueryForm.checkselect[i].checked)
				{
					
					 checkselect=document.commonQueryForm.checkselect[i].value;
				}
			}
   
			if(checkselect=="1"){
	       		var hashvo=new ParameterSet();
			    hashvo.setValue("infor",infor);
			    hashvo.setValue("sql",getEncodeStr("${commonQueryForm.sql}")); 
			    hashvo.setValue("row_num",'${commonQueryForm.row_num}');
			    var request=new Request({method:'post',asynchronous:false,onSuccess:queryresult,functionId:'151211001137'},hashvo);	
	       }else{
       		var objlist=new Array();
       		var tablevos=document.getElementsByTagName("input");
			for(var i=0;i<tablevos.length;i++){
				if(tablevos[i].type=="checkbox"){
					if(tablevos[i].checked==true && tablevos[i].name!="selectall"){
						objlist.push(tablevos[i].value);	
					}
      	 		}
   			}
	   		returnValue=objlist;
	   		window.close();		
	       }
	}
	function queryresult(outparamters){
		var objlist = outparamters.getValue("listvalue");
		returnValue=objlist;
	   	window.close();		
	}
	function selectAll(obj){
	if(obj.checked==true){
		checkAll();
	}else{
		clearAll();
	}
}


<%
	  CommonQueryForm commonQueryForm=(CommonQueryForm)session.getAttribute("commonQueryForm");
	  String isGetSql=commonQueryForm.getIsGetSql();
	  String sql=commonQueryForm.getSql();
	  if(isGetSql!=null&&isGetSql.equals("1"))
	  {
%>			
	    returnValue="<%=sql%>";
	   	window.close();		
<%	  
	  }

%>

</script>
<base id="mybase" target="_self">
<html:form action="/kq/options/sign_point/common_result">
<%
	int i = 0;
	%>
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" style="border-collapse:collapse;">
<tr>
<td align="left"  nowrap>
<div class="complex_border_color" style="*width:expression(document.body.clientWidth-10);border-bottom: none;">
<table  width="100%">
	<tr>
		<td>
          	<html:radio name="commonQueryForm" property="checkselect" value="1"/><bean:message key="hire.jp.pos.all"/>&nbsp;&nbsp;
          	<html:radio name="commonQueryForm" property="checkselect" value="0"/><bean:message key="jx.eval.handSel"/>
     		&nbsp;&nbsp;&nbsp;&nbsp;
     		<hrms:submit styleClass="mybutton" property="br_pre">
            		<bean:message key="button.query.pre"/>
	 		</hrms:submit> 	
        	<html:button styleClass="mybutton" property="b_ok" onclick="getSelectedData('${commonQueryForm.type}','${commonQueryForm.setname}');">
            		&nbsp;<bean:message key="button.ok"/>&nbsp;
	 		</html:button>
     	</td>
	</tr>          
</table>
</div>
</td>
</tr>
<tr>
<td width="100%" nowrap>
<div class="fixedDiv2">
<table border="0" id=tab cellspacing="0" cellpadding="0" style="border-collapse:collapse; " width="100%" >
	<tr >
		<td align="center" width="30" class="TableRow" style="border-left: none;border-top:none;" nowrap>
			<input type="checkbox" name="selectall" value="selectall" onclick="selectAll(this);">
		</td> 
		<logic:iterate id="info" name="commonQueryForm" property="showlist" >
		<logic:notEqual name="info" property="name" value="a0000">
		<logic:notEqual name="info" property="name" value="a0100">
		<td align="center" class="TableRow" style="border-top:none;border-right: none;" nowrap>
			<bean:write name="info" property="label"/>
		</td> 
		</logic:notEqual>
		</logic:notEqual>
		</logic:iterate>
	</tr>
	<hrms:paginationdb id="element" name="commonQueryForm" sql_str="commonQueryForm.sql" 
	table="" where_str="" columns="commonQueryForm.columns" order_by="commonQueryForm.orderby" page_id="pagination" pagerows="100">
    <%if (i % 2 == 0){%>
		<tr class="trShallow">
	<%} else{%>
		<tr class="trDeep">
	<%}i++;%>
	 	<td class="RecordRow" align="center" style="border-left: none;" nowrap>
	 		  	<bean:define id="a0100" name='element' property='a0100'/>
	 		  	<bean:define id="a0101" name='element' property='a0101'/>
	 		  	<bean:define id="b0110" name='element' property='b0110'/>
	 		  	<bean:define id="e0122" name='element' property='e0122'/>
	 		  	<bean:define id="e01a1" name='element' property='e01a1'/>
	 		  	<bean:define id="dbase" name='element' property='dbase'/>
	 		  	<bean:define id="a0000" name='element' property='a0000'/>
	 		  	 <input type="checkbox" name="<%=dbase+""+a0100%>" value="<%=a0100+"`"+a0101+"`"+b0110+"`"+e0122+"`"+e01a1+"`"+dbase+"`"+a0000%>">
	 	</td> 
	 	<logic:iterate id="info" name="commonQueryForm" property="showlist" >
		<logic:notEqual name="info" property="name" value="a0000">
		<logic:notEqual name="info" property="name" value="a0100">
		<logic:notEqual  name="info" property="codesetid" value="0">  
			<td class="RecordRow"  style="border-right: none;" nowrap>
				&nbsp;
				<logic:equal value="UN" name="info" property="codesetid">
					<hrms:codetoname codeid="UN" name="element" codevalue="${info.name}" codeitem="codeitem" scope="page"/>
                    <bean:write name="codeitem" property="codename"/>
                    <logic:empty name="codeitem" property="codename" >
						<hrms:codetoname codeid="UM" name="element" codevalue="${info.name}" codeitem="codeitem" scope="page" uplevel="${commonQueryForm.uplevel }"/>
                    	<bean:write name="codeitem" property="codename"/>
                    </logic:empty>
				</logic:equal>
				<logic:equal value="UM" name="info" property="codesetid">
					<hrms:codetoname codeid="UM" name="element" codevalue="${info.name}" codeitem="codeitem" scope="page" uplevel="${commonQueryForm.uplevel }"/>
                    <bean:write name="codeitem" property="codename"/>
				</logic:equal>
				<logic:equal name="commonQueryForm" property="type" value="1">
				<logic:equal value="@K" name="info" property="codesetid">
					<hrms:codetoname codeid="@K" name="element" codevalue="${info.name}" codeitem="codeitem" scope="page"/>
                    <bean:write name="codeitem" property="codename"/>
				</logic:equal>
				</logic:equal>
				<logic:notEqual name="commonQueryForm" property="type" value="1">
				<logic:equal value="@K" name="info" property="codesetid">
					<hrms:codetoname codeid="@K" name="element" codevalue="${info.name}" codeitem="codeitem" scope="page" uplevel="${commonQueryForm.uplevel }"/>
                    <bean:write name="codeitem" property="codename"/>
				</logic:equal>
				</logic:notEqual>
				<logic:notEqual value="UN" name="info" property="codesetid">
				<logic:notEqual value="@K" name="info" property="codesetid">
				<logic:notEqual value="UM" name="info" property="codesetid">
					<hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.name}" codeitem="codeitem" scope="page"/>
                    <bean:write name="codeitem" property="codename"/>
				</logic:notEqual>
				</logic:notEqual>
				</logic:notEqual>
			</td>
		</logic:notEqual>
		<logic:equal  name="info" property="codesetid" value="0">
			<td class="RecordRow" style="border-right: none;" nowrap>
				&nbsp;<bean:write name="element" property="${info.name}" filter="true"/>&nbsp;
			</td>
		</logic:equal>
		</logic:notEqual>
		</logic:notEqual>
		</logic:iterate>
	 </tr>
	 </hrms:paginationdb>
</table>
</div>
<div style="*width:expression(document.body.clientWidth-10);">
<table width="100%"  align="center" class="RecordRowP">
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
		          <p align="right"><hrms:paginationdblink name="commonQueryForm" property="pagination" nameId="commonQueryForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table>
</div>
    </td>
</tr>

</table>
</html:form>
