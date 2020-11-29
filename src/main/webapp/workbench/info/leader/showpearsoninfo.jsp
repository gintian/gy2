<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<SCRIPT LANGUAGE=javascript src="/js/common.js"></SCRIPT>
<style type="text/css"> 
#strTable{
           border: 1px solid;
           height: 266px;    
           width: 98%;           
           overflow-y:auto;
           overflow-x:auto;            
           margin: 1em 1;
           margin-left:5px;
           margin-right:5px;
           position:absolute;
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
	 var checkselect="0";
	 var checkselects=document.getElementsByName("checkselect");
	
	 for(var i=0;i<checkselects.length;i++){
		if(checkselects[i].checked){
			checkselect=checkselects[i].value;
		}
	}
    if(checkselect=="0"){
    		var hashvo=new ParameterSet();
		    hashvo.setValue("sql",getEncodeStr("${searchInformForm.sqlstr}")); 
		    var request=new Request({method:'post',asynchronous:false,onSuccess:queryresult,functionId:'3020110078'},hashvo);	
    }else{
		var selectvalue = "";
		var tablevos=document.getElementsByTagName("input");
		for(var i=0;i<tablevos.length;i++){
			if(tablevos[i].type=="checkbox"&&tablevos[i].value!='selectall'){
				if(tablevos[i].checked==true){
					selectvalue+=tablevos[i].value+","
				}
	      	 }
	   	}
	   	if(selectvalue!=null&&selectvalue.length>1){
	   		if(parent.parent.parent.Ext)
	   			parent.parent.parent.Ext.getCmp('simple_query').return_vo = selectvalue;
	   		else
	   			window.returnValue=selectvalue;
	   		//window.close();
	   		winclose();
	   	}
    }
}
function queryresult(outparamters){
	var objlist = outparamters.getValue("listvalue");
	if(parent.parent.parent.Ext)
	   	parent.parent.parent.Ext.getCmp('simple_query').return_vo = objlist;
	else
	   	window.returnValue=objlist;
	//window.close();
	winclose();	
}

function returnback(){
	window.parent.document.getElementById("divid").style.display='';
	window.parent.document.getElementById("iframeid").style.display='none';
	window.parent.document.getElementById("iframeid").src='';

}
//关闭弹窗  wangb 20190319
function winclose(){
	if(parent.parent.parent.Ext)
		parent.parent.parent.Ext.getCmp('simple_query').close();
	else
		window.close();
}
</script>
<html:form action="/general/inform/search/gmsearcher">
<table border="0" align="center" width="100%">
	<tr><td>
		<table border="0" width="100%" cellspacing="0" cellpadding="0">
			<tr>
				<td height="30" align="left">
				&nbsp;&nbsp;
				<input type="radio" name="checkselect" value="0" checked="checked"><bean:message key="hire.jp.pos.all"/>
			&nbsp;&nbsp;
				<input type="radio" name="checkselect" value="1"><bean:message key="jx.eval.handSel"/>
			&nbsp;&nbsp;
				<input type="button" value="<bean:message key="button.query.pre"/>" onclick="returnback();" class="mybutton">
			&nbsp;&nbsp;&nbsp;&nbsp;
				<input type="button" value="<bean:message key='button.ok'/>" onclick="setOk();" class="mybutton">
			&nbsp;&nbsp;&nbsp;&nbsp;
				<input type="button" value="<bean:message key='button.cancel'/>" onclick="winclose();" class="mybutton"></td> 
			</tr>
		</table>
	</td></tr>
	<tr>
		<td>
		<fieldset align="center" style="width:100%;height:328">
      	<legend>选择人员</legend>
      	<div id="strTable" class="ListTableF">
		<table border="0" class="ListTable1" width="100%" style="border-right:none">
		<tr class="fixedHeaderTr1">
		<logic:iterate id="element" name="searchInformForm"  property="titlelist" indexId="index">
			<logic:equal name="element" property="itemid" value="a0100">
    		<td align="center" class="TableRow" style="border-left:none" nowrap>
    		<input type="checkbox" name="selbox" onclick="selectAll(this);" title='<bean:message key="label.query.selectall"/>'>
    		</td> 
	      	</logic:equal>
	      	<logic:notEqual name="element" property="itemid" value="a0100">
			<td align="center" class="TableRow" style="border-right:none" nowrap>
                 <bean:write  name="element" property="itemdesc" filter="true"/>
	       </td> 
	       </logic:notEqual>
		</logic:iterate>
	</tr>
	<hrms:paginationdb id="element" name="searchInformForm" sql_str="searchInformForm.sqlstr" 
	table="" where_str="" columns="A0100,A0101,B0110,E0122,E01A1,dbpre,dbname"
	 order_by="order by dbid,b0110,e0122" 
	 page_id="pagination" pagerows="100">
	 <bean:define id="a0100" name='element' property='a0100'/>
	 <bean:define id="a0101" name='element' property='a0101'/>
	 <bean:define id="b0110" name='element' property='b0110'/>
	 <bean:define id="e0122" name='element' property='e0122'/>
	 <bean:define id="e01a1" name='element' property='e01a1'/>
	 <bean:define id="dbname" name='element' property='dbname'/>
	  <bean:define id="dbpre" name='element' property='dbpre'/>
     <tr> 
     	<logic:iterate id="fielditem"  name="searchInformForm"  property="titlelist" indexId="index">
    	 <logic:equal name="fielditem" property="itemid" value="a0100">
    		<td align="center" class="RecordRow" style="border-left:none" nowrap>
    			<input type="checkbox" name="<%=a0100+"_"+dbpre%>" value="<%=dbpre.toString()+a0100.toString()%>">                               
	      	</td> 
	      </logic:equal>
	      <logic:notEqual name="fielditem" property="itemid" value="a0100">
    		<td align="left" class="RecordRow" style="border-right:none" nowrap>
               <logic:notEqual name="fielditem" property="codesetid" value="0">
          	   		&nbsp;<hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
          	   		<bean:write name="codeitem" property="codename" />&nbsp;                    
               </logic:notEqual>
               <logic:equal name="fielditem" property="codesetid" value="0">
                   &nbsp;<bean:write name="element" property="${fielditem.itemid}" filter="false"/>&nbsp;                 
               </logic:equal>                              
	      </td> 
	      </logic:notEqual>
    	</logic:iterate>
	 </tr>
	 </hrms:paginationdb>
</table>
</div>
<table border="0" width="98%" align="center" style="position:absolute;left:10px; top:340px;">
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
</html:form>
<script>
	if(!getBrowseVersion() || getBrowseVersion() == 10 ){//非ie浏览器  样式修改   wangb 20190319
		var form = document.getElementsByName('searchInformForm')[0];
		form.style.width='96%';
		var strTable = document.getElementById('strTable');
		strTable.style.width = '94%';
		strTable.style.margin = '0';
		
		if(getBrowseVersion() == 10){//ie11 单独修改样式  wangb 20190322
			var pForm = parent.document.getElementsByName('searchInformForm')[0];
			pForm.style.overflow='hidden';
			var iframeid = parent.document.getElementById('iframeid');
			iframeid.style.width='99%';
		}
	}

</script>