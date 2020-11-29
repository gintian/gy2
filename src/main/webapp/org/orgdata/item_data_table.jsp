<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.org.orgdata.OrgDataForm"%>
<script language="javascript" src="/js/constant.js"></script>
<script language="javascript" src="/js/dict.js"></script> 
<script language="JavaScript" src="orgdata.js"></script>
<%
OrgDataForm orgForm = (OrgDataForm)session.getAttribute("orgDataForm");
String isInsert = (String)orgForm.getIsInsert();
%>
<body onclick="showoff();">
<html:form action="/org/orgdata/orgdata">
<!--onclick="insert('${orgDataForm.itemtable}','${orgDataForm.itemid}','add');"  -->
<html:hidden name="orgDataForm" property="infor"/>
<logic:notEqual name="orgDataForm" property="infor" value="3">
<hrms:dataset name="orgDataForm" property="itemlist" scope="session" setname="${orgDataForm.itemtable}"  
setalias="data_table" readonly="false" editable="true" select="true" 
sql="${orgDataForm.itemsql}" pagerows="${orgDataForm.pagerows}" buttons="movefirst,prevpage,nextpage,movelast">
    <%if(orgForm.getPriItem().equals("2")){%>
    <hrms:commandbutton name="table" hint="" function_id="230650102" functionId="" refresh="true" type="selected" setname="${orgDataForm.itemtable}"  onclick="addSubSet('${orgDataForm.itemtable}','${orgDataForm.itemid}','${orgDataForm.infor}');">
     <bean:message key="button.insert"/>
   </hrms:commandbutton>
    <%if(isInsert.equals("1")){%>
   <hrms:commandbutton name="tableinsert" hint="" function_id="2306519" functionId="" refresh="true" type="selected" setname="${orgDataForm.itemtable}" onclick="insertSubSet('${orgDataForm.itemtable}','${orgDataForm.itemid}','${orgDataForm.infor}');">
    	<bean:message key="button.new.insert"/>
   	</hrms:commandbutton>
   	 <%}%>
   	 <hrms:commandbutton name="delselected" function_id="230650202" hint="general.inform.search.confirmed.del" functionId="0401000042" refresh="true" type="selected" setname="${orgDataForm.itemtable}">
     <bean:message key="button.delete"/>
   </hrms:commandbutton>  
   <%}%>     
</hrms:dataset>
</logic:notEqual>
<logic:equal name="orgDataForm" property="infor" value="3">
<hrms:dataset name="orgDataForm" property="itemlist" scope="session" setname="${orgDataForm.itemtable}"  
setalias="data_table" readonly="false" editable="true" select="true" 
sql="${orgDataForm.itemsql}" pagerows="${orgDataForm.pagerows}" buttons="movefirst,prevpage,nextpage,movelast">
    <%if(orgForm.getPriItem().equals("2")){%>
    <hrms:commandbutton name="table" hint="" function_id="250650102" functionId="" refresh="true" type="selected" setname="${orgDataForm.itemtable}"  onclick="addSubSet('${orgDataForm.itemtable}','${orgDataForm.itemid}','${orgDataForm.infor}');">
     <bean:message key="button.insert"/>
   </hrms:commandbutton>
       <%if(isInsert.equals("1")){%>
   <hrms:commandbutton name="tableinsert" hint="" function_id="2506519" functionId="" refresh="true" type="selected" setname="${orgDataForm.itemtable}" onclick="insertSubSet('${orgDataForm.itemtable}','${orgDataForm.itemid}','${orgDataForm.infor}');">
    	<bean:message key="button.new.insert"/>
   	</hrms:commandbutton>
   	   <%}%>  
   	 <hrms:commandbutton name="delselected" function_id="250650202" hint="general.inform.search.confirmed.del" functionId="0401000042" refresh="true" type="selected" setname="${orgDataForm.itemtable}">
     <bean:message key="button.delete"/>
   </hrms:commandbutton>  
   <%}%>     
</hrms:dataset>
</logic:equal>
<input type="button" name="testbutton" value="a" style="width:1px;height:1px;display:none"/>
</html:form>
</body>
<script language="javascript">
function ${orgDataForm.itemtable}_afterChange(dataset,field,value){
	var field_name=field.getName();
	var record,pfield;
	record=dataset.getCurrent(); 
	if(field_name=='select')
		return;
	var infor = "${orgDataForm.infor}";
	var mainset = "B01";
	var a0100 = "";
	if(infor=='2'){
		a0100 = record.getValue("b0110");
	}else if(infor=='3'){
		a0100 = record.getValue("e01a1");
		mainset = "K01";
	}
	var tablename = "${orgDataForm.itemtable}";
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
	hashvo.setValue("inforflag","${orgDataForm.infor}");  
	if(tablename.indexOf("B01")==-1&&tablename.indexOf("b01")==-1){
		var i9999 = record.getValue("i9999");
		hashvo.setValue("i9999",i9999);  
	}
	var request=new Request({method:'post',asynchronous:false,functionId:"1010090011"},hashvo);
	if(tablename.indexOf("B01")==-1&&tablename.indexOf("b01")==-1){
		var i9999 = record.getValue("i9999");
		if(i9999==null||i9999.length<1)
			record.setValue("i9999","1");
	}
}
var oldrecord;
function table${orgDataForm.itemtable}_onRowClick(table){
	var getablename = "${orgDataForm.itemtable}";
	var reserveitem = "${orgDataForm.resitemid}";
	var dataset=table.getDataset();	
   	var record=dataset.getCurrent();
   	if(!record)
	    return;
	/**必添项处理*/
	if(oldrecord&&record!=oldrecord){
		var arr = reserveitem.split("`");
		var itemvalues="";
		var checkflag="";
		for(var i=0;i<arr.length;i++){
			if(arr[i]!=null&&arr[i].length>0){
				var item_arr = arr[i].split(",.");
				if(item_arr!=null&&item_arr.length==2){
					itemvalues=oldrecord.getValue(item_arr[0]); 
					if(itemvalues==null||itemvalues.length<1){
						checkflag = item_arr[1]+"为必填项!";
						break;
					}
				}
			}
		}
		if(checkflag!=null&&checkflag.length>3){
			dataset.setCurrent(oldrecord); 
			alert(checkflag);
			return false;
		}  
	}
	if(oldrecord&&record==oldrecord){
		return false;
	}
	oldrecord = record;
}
function table${orgDataForm.itemtable}_oper_onRefresh(cell,value,record){	
	if(record!=null){
		var i9999 = record.getValue("i9999");	
		if(i9999!='')//有子集的时候才能编辑	
			cell.innerHTML="<img src=\"/images/edit.gif\" border=\"0\" onclick=\"editSubSet('${orgDataForm.itemtable}','${orgDataForm.itemid}','${orgDataForm.infor}')\" style=\"cursor:hand;\">";
	}
} 
function table${orgDataForm.itemtable}_downole_onRefresh(cell,value,record){	
	if(record!=null){
		var i9999 = record.getValue("i9999");	
		if(i9999!='')//有子集的时候才能编辑	
			cell.innerHTML="<img src=\"/images/view.gif\" border=\"0\" onclick=\"downLoadOle('${orgDataForm.itemtable}','${orgDataForm.itemid}','"+i9999+"','${orgDataForm.infor}')\" style=\"cursor:hand;\">";
	}
}
function table${orgDataForm.itemtable}_upole_onRefresh(cell,value,record){	
	if(record!=null){
		var i9999 = record.getValue("i9999");	
		if(i9999!='')//有子集的时候才能编辑	
			cell.innerHTML="<img src=\"/images/import.gif\" border=\"0\" onclick=\"uploadMedia('','${orgDataForm.itemid}','"+i9999+"','${orgDataForm.infor}')\" style=\"cursor:hand;\">";
	}
}
function table${orgDataForm.itemtable}_flag_onRefresh(cell,value,record){	
	if(record!=null){
		var i9999 = record.getValue("i9999");	
		var flag = record.getValue("flag");
		if(flag==null||flag.length<1)
			flag = "选择";
		var cellstr = "<div style=\"cursor:hand;color:#0033FF\" ";
		cellstr+=" onclick=\"selectMedia('','${orgDataForm.itemid}','";
		cellstr+=i9999+"','${orgDataForm.infor}','1',cell)\" ";
		cellstr+=">"+flag+"</div>";
		if(i9999!='')//有子集的时候才能编辑	
			cell.innerHTML=cellstr;
		
	}
}
document.body.onbeforeunload=function(){ 
	var target = document.getElementById("testbutton");
	target.style.display = "block";
	target.focus();
	target.style.display="none"; 
}
function showoff() {
	parent.mlay.style.display="none"; 
}
window.parent.document.getElementById("selectbutton").click();
</script>

