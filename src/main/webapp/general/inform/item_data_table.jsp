<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.general.inform.MInformForm"%>
<%
	MInformForm mInformForm = (MInformForm)session.getAttribute("mInformForm");
	String isInsert = (String)mInformForm.getIsInsert();
%>
<script language="JavaScript" src="inform.js"></script>
<script language="javascript" src="/js/constant.js"></script>
<script language="javascript" src="/js/dict.js"></script> 
<body onclick="showoff();">
<html:form action="/general/inform/get_data_table">
<hrms:dataset name="mInformForm" property="itemlist" scope="session" setname="${mInformForm.itemtable}"  
setalias="data_table" readonly="false" rowlock="true" editable="true" select="true" 
sql="${mInformForm.itemsql}" pagerows="20" buttons="movefirst,prevpage,nextpage,movelast">
	<hrms:commandbutton name="table" function_id="3233101" hint="" functionId="" visible="${mInformForm.viewbutton}" refresh="true" type="selected" setname="${mInformForm.itemtable}" onclick="addSubSet('${mInformForm.itemtable}','${mInformForm.a0100}','${mInformForm.dbname}');" >
     <bean:message key="button.insert"/> 
   </hrms:commandbutton>
   <%if(!mInformForm.getItemtable().equalsIgnoreCase("t_vorg_staff")){ %>
   		<%if(isInsert.equals("1")){%>
   <hrms:commandbutton name="tableinsert" function_id="3233102" hint="" functionId="" visible="${mInformForm.viewbutton}" refresh="true" type="selected" setname="${mInformForm.itemtable}" onclick="insertSubSet('${mInformForm.itemtable}','${mInformForm.a0100}','${mInformForm.dbname}');" >
    <bean:message key="button.new.insert"/>
   </hrms:commandbutton> 
   <%}} %>
    <hrms:commandbutton name="delselected" function_id="3233103" hint="general.inform.search.confirmed.del" visible="${mInformForm.viewbutton}" functionId="1010090003" refresh="true" type="selected" setname="${mInformForm.itemtable}" >
     <bean:message key="button.delete"/>
   </hrms:commandbutton>      
</hrms:dataset>
<input type="button" name="testbutton" value="a" style="width:1px;height:1px;display:none"/>
</html:form>
</body>
<script language="javascript">
function ${mInformForm.itemtable}_afterChange(dataset,field,value){
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
	var tablename = "${mInformForm.itemtable}";
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
var oldrecord;
function table${mInformForm.itemtable}_onRowClick(table){
	var getablename = "${mInformForm.itemtable}";
	var reserveitem = "${mInformForm.resitemid}";
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
function table${mInformForm.itemtable}_oper_onRefresh(cell,value,record){	
	if(record!=null){
		var i9999 = record.getValue("i9999");	
		if(i9999!='')//有子集的时候才能编辑	
			cell.innerHTML="<img src=\"/images/edit.gif\" border=\"0\" onclick=\"editSubSet('${mInformForm.itemtable}','${mInformForm.a0100}','${mInformForm.dbname}')\" style=\"cursor:hand;\">";
	}
}
function table${mInformForm.itemtable}_downole_onRefresh(cell,value,record){	
	if(record!=null){
		var i9999 = record.getValue("i9999");	
		if(i9999!='')
			cell.innerHTML="<img src=\"/images/view.gif\" border=\"0\" onclick=\"downLoadOle('${mInformForm.itemtable}','${mInformForm.a0100}','"+i9999+"','1')\" style=\"cursor:hand;\">";
	}
}
function table${mInformForm.itemtable}_upole_onRefresh(cell,value,record){	
	if(record!=null){
		var i9999 = record.getValue("i9999");	
		if(i9999!='')
			cell.innerHTML="<img src=\"/images/import.gif\" border=\"0\" onclick=\"uploadMedia('${mInformForm.dbname}','${mInformForm.a0100}','"+i9999+"','1')\" style=\"cursor:hand;\">";
	}
}
function table${mInformForm.itemtable}_flag_onRefresh(cell,value,record){	
	if(record!=null){
		var i9999 = record.getValue("i9999");	
		var flag = record.getValue("flag");
		if(flag==null||flag.length<1)
			flag = "选择";
		var cellstr = "<div style=\"cursor:hand;color:#0033FF\" ";
		cellstr+=" onclick=\"selectMedia('${mInformForm.dbname}','${mInformForm.a0100}','";
		cellstr+=i9999+"','1','1',cell)\" ";
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
parent.document.getElementById("selectbutton").click();
</script>

