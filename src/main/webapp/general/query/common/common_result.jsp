<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.general.query.CommonQueryForm,		
			     java.util.*,com.hjsj.hrms.utils.PubFunc"%>
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
       
       if(checkselect=="0"){
       		var hashvo=new ParameterSet();
		    hashvo.setValue("infor",infor);
		    hashvo.setValue("sql",getEncodeStr("${commonQueryForm.sql}")); 
		    hashvo.setValue("row_num",'${commonQueryForm.row_num}');
		    var request=new Request({method:'post',asynchronous:false,onSuccess:queryresult,functionId:'0202011016'},hashvo);	
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
		    if(isCompatibleIE()){//兼容模式
   				returnValue=objlist;
	   			window.close();
   			}else{//非IE浏览器返回数据  bug 34769 wangb 201080209
				parent.opener.openReturn(objlist);   			
				parent.window.close();	
   			}
	   }      
	}
	function queryresult(outparamters){
		var objlist = outparamters.getValue("listvalue");
		if(isCompatibleIE()){//兼容模式
			returnValue=objlist;
			window.close();
		}else{//非IE浏览器返回数据 bug 34769 wangb 201080209
			parent.opener.openReturn(objlist);   			
			parent.window.close();	
		}		
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
		if(isCompatibleIE()){//兼容模式
			//安全平台改造,将sql进行加密处理	
			returnValue="<%=PubFunc.encrypt(sql)%>";
			window.close();
		}else{//非IE浏览器返回数据  bug 34769 wangb 201080209
			parent.opener.openReturn("<%=PubFunc.encrypt(sql)%>");   			
			parent.window.close();	
		}
<%	  
	}

%>
</script>
<base id="mybase" target="_self">
<html:form action="/general/query/common/common_result">
<%
	int i = 0;
	%>
<table width="70%" border="0" cellspacing="0"  align="center" cellpadding="0" style="border-collapse:collapse">
<tr>
<td align="left"  nowrap >
<table  width="100%">
	<tr>
		<td>
          	<html:radio name="commonQueryForm" property="checkselect" value="0"/><bean:message key="hire.jp.pos.all"/>&nbsp;&nbsp;
          	<html:radio name="commonQueryForm" property="checkselect" value="1"/><bean:message key="jx.eval.handSel"/>
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
</td>
</tr>
<tr>
<td width="100%" nowrap>
<div class="fixedDiv2">
<table border="0" id=tab cellpadding="0" cellspacing="0" width="100%" >
	<tr class="fixedHeaderTr">
		<td align="center" width="30" class="TableRow" style="border-top: none;border-left: none;border-right:none;" nowrap>
			<input type="checkbox" name="selectall" value="selectall" onclick="selectAll(this);">
		</td> 
		<logic:iterate id="info" name="commonQueryForm" property="showlist" >
		<logic:notEqual name="info" property="name" value="a0000">
		<logic:notEqual name="info" property="name" value="a0100">
		<logic:notEqual name="info" property="name" value="dbase">
		<td align="center" class="TableRow" style="border-top:none;border-right:none;" nowrap>
			<bean:write name="info" property="label"/>
		</td> 
		</logic:notEqual>
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
	 	<td class="RecordRow" align="center" style="border-top: none;border-left: none;border-right:none;"  nowrap>
	 		<logic:equal name="commonQueryForm" property="type" value="1">
	 		  	<bean:define id="a0100" name='element' property='a0100'/>
	 		  	<bean:define id="dbase" name='element' property='dbase'/>
	 		  	 <input type="checkbox" name="<%=dbase+""+a0100%>" value="<%=dbase+""+a0100%>">
	 		</logic:equal>
	 		<logic:equal name="commonQueryForm" property="type" value="2">
	 		  	<bean:define id="b0110" name='element' property='b0110'/>
	 		  	 <input type="checkbox" name="<%=b0110%>" value="<%=b0110%>">
	 		</logic:equal>
	 		<logic:equal name="commonQueryForm" property="type" value="3">
	 		  	<bean:define id="e01a1" name='element' property='e01a1'/>
	 		  	 <input type="checkbox" name="<%=e01a1%>" value="<%=e01a1%>">
	 		</logic:equal>
	 		<logic:equal name="commonQueryForm" property="type" value="9"><!-- 基准岗位 -->
	 		  	<bean:define id="h0100" name='element' property='h0100'/>
	 		  	 <input type="checkbox" name="<%=h0100%>" value="<%=h0100%>">
	 		</logic:equal>
	 	</td> 
	 	<logic:iterate id="info" name="commonQueryForm" property="showlist" >
		<logic:notEqual name="info" property="name" value="a0000">
		<logic:notEqual name="info" property="name" value="a0100">
		<logic:notEqual name="info" property="name" value="dbase">
		<logic:notEqual  name="info" property="codesetid" value="0">  
			<td class="RecordRow" style="border-top:none;border-right:none;" nowrap>
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
			<td class="RecordRow" style="border-top:none;border-right:none;" nowrap>
				&nbsp;<bean:write name="element" property="${info.name}" filter="true"/>&nbsp;
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
<table width="100%"  align="center" class="fixedDiv3 common_border_color" style="border: 1px solid;border-top: none;">
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
    </td>
</tr>
</table>
</html:form>
