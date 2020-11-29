<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.ht.inform.ContractForm"%>
<%@ page import="com.hrms.struts.taglib.CommonData,java.util.ArrayList"%>
<%@ page import="com.hrms.hjsj.sys.DataDictionary"%>
<%@ page import="com.hrms.hjsj.sys.FieldSet"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>

<script language="JavaScript" src="iteminfor.js"></script>
<script language="javascript" src="/js/constant.js"></script>
<script language="javascript" src="/js/dict.js"></script> 
<html:form action="/ht/inform/data_table">
<%
	int i=0;
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	ContractForm form = (ContractForm) session.getAttribute("contractForm");
	String defitem = form.getDefitem();
	String set= userView.analyseTablePriv(defitem);
	String count = form.getCount();
	if (!userView.isAdmin()) {
	if ("1".equalsIgnoreCase(count)) {
	
%>
<hrms:dataset name="contractForm" property="itemlist" scope="session" setname="${contractForm.itemtable}"  
setalias="data_table" readonly="false" rowlock="true" editable="true" select="true"  
sql="${contractForm.itemsql}" pagerows="100" buttons="movefirst,prevpage,nextpage,movelast">
</hrms:dataset>
<%} else { if (userView.hasTheFunction("3300205")) {%>
<hrms:dataset name="contractForm" property="itemlist" scope="session" setname="${contractForm.itemtable}"  
setalias="data_table" readonly="false" rowlock="true" editable="true" select="true"  
sql="${contractForm.itemsql}" pagerows="100" buttons="movefirst,prevpage,nextpage,movelast">
</hrms:dataset>

<%} else {%>
	<hrms:dataset name="contractForm" property="itemlist" scope="session" setname="${contractForm.itemtable}"  
setalias="data_table" readonly="false" rowlock="true" editable="false" select="true"  
sql="${contractForm.itemsql}" pagerows="100" buttons="movefirst,prevpage,nextpage,movelast">
</hrms:dataset>
<%}} } else {%>
<hrms:dataset name="contractForm" property="itemlist" scope="session" setname="${contractForm.itemtable}"  
setalias="data_table" readonly="false" rowlock="true" editable="true" select="true"  
sql="${contractForm.itemsql}" pagerows="100" buttons="movefirst,prevpage,nextpage,movelast">
</hrms:dataset>
<%} 
if("hcm".equalsIgnoreCase(userView.getBosflag())){
%>
<table style="position:absolute;left:140px;top:3px;">
<%}else {%>
<table style="position:absolute;left:140px;top:-5px;">
<%}%>
<tr><td>
<table><tr><td>
<hrms:menubar menu="menu1" id="menubar1">
<%if ("2".equalsIgnoreCase(set)) {%>
<logic:equal name="contractForm" property="checkflag" value="0"><!--append('${contractForm.itemtable}','${contractForm.a_code}');  -->
<hrms:menuitem name="insertAdd" function_id="3300205" label="button.insert" url="append2('${contractForm.a0100}','${contractForm.itemtable}','noflag','${contractForm.dbname}','${contractForm.ctflag}');"/>
</logic:equal>

<hrms:menuitem name="delmenu"  function_id="3300206" label="button.delete"   url="del();"/>  
<%} %>
<!-- command="delselected" hint="general.inform.search.confirmed.del" -->
<%if ("2".equalsIgnoreCase(set)) {%>
<logic:notEqual name="contractForm" property="checkflag" value="0">
<hrms:priv func_id="3300205">
<logic:iterate id="element"  name="contractForm"  property="ctflaglist" indexId="index">
<% 
	CommonData item=(CommonData)pageContext.getAttribute("element");
	String ctflagdesc=item.getDataName();
	String ctflagvalue=item.getDataValue();
	++i;
%>
<bean:define id="ctflagval" value="<%=ctflagvalue %>"/>
<hrms:menuitem name='<%="ctflag"+i%>' label='<%=ctflagdesc%>' checked="true" groupindex="1" url="append2('${contractForm.a0100}','${contractForm.itemtable}','${ctflagval}','${contractForm.dbname}','${contractForm.ctflag}');"/>
</logic:iterate>
</hrms:priv>
</logic:notEqual>
<%} %>
</hrms:menubar>
</td></tr></table>
</td>
<td>
</td>
</tr>
</table>
</html:form>
<script language="javascript">
function ${contractForm.itemtable}_afterChange(dataset,field,value){
	var field_name=field.getName();
	var record,pfield;
	record=dataset.getCurrent(); 
	if(field_name=='select')
		return;
		
	if(field_name=="e01a1"){
   	  	value=record.getValue("e01a1");
   	  	if(value!=null&&value.length>0){
   	  		value=getDeptParentId(value);
   	  		pfield=dataset.getField("e0122");
			if(typeof(pfield)!="undefined"){
				if(isExistField(dataset,'e0122')) 
					record.setValue("e0122",value);
			}
		}
	}
	if(field_name=="e0122"){
		value=record.getValue("e0122");
   	  	if(value!=null&&value.length>0){
   	  		value=getUnitParentId(value);
   	  		pfield=dataset.getField("e0122");
			if(typeof(pfield)!="undefined"){
				 if(isExistField(dataset,'b0110')){    	  		 	
					record.setValue("b0110",value);
				}
			}
		}
	} 
	var a0100 = record.getValue("a0100");
	var tablename = "${contractForm.itemtable}";
	var fieldvalue = record.getValue(field_name);
	if(field.getDataType()=='date'&&fieldvalue!=null&&fieldvalue!=""){
		var date=new Date(); 
		date.setTime(fieldvalue); 
		var month =  date.getMonth();
		var year =  date.getFullYear();
		if(month>11){
			month=1;
			year+=1;
		}else{
			month+=1;
		}
		fieldvalue = year+"-"+month+"-"+date.getDate();
	}
	var hashvo=new ParameterSet();
	hashvo.setValue("fieldvalue",fieldvalue);  
	hashvo.setValue("itemid",field_name);	
	hashvo.setValue("tablename",tablename);  
	hashvo.setValue("a0100",a0100);  
	hashvo.setValue("inforflag","1");  
	if(tablename.indexOf("A01")==-1&&tablename.indexOf("a01")==-1){
		var i9999 = record.getValue("i9999");
		hashvo.setValue("i9999",i9999);  
	}
	var request=new Request({method:'post',asynchronous:false,functionId:"1010090011"},hashvo);
	if(tablename.indexOf("A01")==-1&&tablename.indexOf("a01")==-1){
		var i9999 = record.getValue("i9999");
		if(i9999==null||i9999.length<1)
			record.setValue("i9999","1");
	}
}

	function table${contractForm.itemtable}_oper_onRefresh(cell,value,record){	
		if(record!=null){
			var i9999 = record.getValue("i9999");	
			if(i9999!=''){//有子集的时候才能编辑
				<hrms:priv func_id="3300207">	
					cell.innerHTML="<img src=\"/images/edit.gif\" border=\"0\" onclick=\"edit('${contractForm.a0100}','${contractForm.itemtable}','noflag','${contractForm.dbname}','${contractForm.ctflag}','"+i9999+"')\" style=\"cursor:hand;\">";
				</hrms:priv>
			}
		}
	}
	function del()
	{	
		if(!confirm(IS_DEL_NOT)){
			return false;
		}
		 var str='';
		 var tablename="${contractForm.itemtable}";		      
         var table=$('table'+tablename);                  
         var dataset=table.getDataset();
	 
		 var record=dataset.getFirstRecord();
		 while (record) 
		 {			
		     if (record.getValue("select"))
	    	 	str+=','+record.getValue("i9999");
	    	 record=record.getNextRecord();
	     }
	     if(str=='')
	     {
	     	alert(SELECT_DELETED);
	     	return;
	     }
	     	
	     contractForm.action="/ht/inform/data_table.do?b_del=link&delStr="+str.substring(1);
	     contractForm.submit();
	}
</script>

