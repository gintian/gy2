<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="javascript">
function returnTO()
  {
     configParameterForm.action="/templates/attestation/police/wizard.do?br_postwizard=link";
     configParameterForm.target="il_body";
     configParameterForm.submit();
  }
</script> 
<style type="text/css"> 
.myfixedDiv
{ 
	overflow:auto; 
	height:expression(document.body.clientHeight-140);
	width:expression(document.body.clientWidth-10); 
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; 
}
.headerTr{ 
	position:relative; 
	top:expression(this.offsetParent.scrollTop); 
}
</style>
<script language="javascript" src="/js/dict.js"></script> 
<html:form action="/performance/totalrank/totalrank">
<%int i=0;%>
<html:hidden name="configParameterForm" property="fromScope"/>
<html:hidden name="configParameterForm" property="toScope"/>
<html:hidden name="configParameterForm" property="timeitemid"/>
<html:hidden name="configParameterForm" property="highsearch"/>
<html:hidden name="configParameterForm" property="sortitem"/>
<table width="100%" border="0" cellspacing="0" cellpadding="0" align="center">
	<tr>
		<td align="left" nowrap>
			<table border="0" width="100%" cellspacing="0"  cellpadding="0">
				<tr width="500">
					<td align="left" width="480" nowrap>
						考核得分:
						<html:select name="configParameterForm" property="setid" onchange="changeField();"  style="width:120">
    						<html:optionsCollection property="setlist" value="dataValue" label="dataName" />
 						</html:select> 
						人员库:
						<html:select name="configParameterForm" property="dbname" onchange="searchSort();"  style="width:100">
    						<html:optionsCollection property="dblist" value="dataValue" label="dataName" />
 						</html:select> 
						按:
						<html:select name="configParameterForm" property="itemid" onchange="changeCodeValue();" style="width:120">
    						<html:optionsCollection property="itemList" value="dataValue" label="dataName" />
 						</html:select> 
 					</td>
 					<td align="left" nowrap>
							<span id="textview" style="display:none;">
								<html:text name="configParameterForm" property="searchtext" style="width:150px;" />
								<input type='button' class="mybutton" value='查询' onclick="searchAll();"/>
								<input type='button' class="mybutton" value='高级' onclick="selectQ();"/>
								<logic:equal name="configParameterForm" property="model" value="2">
								<input type='button' class="mybutton" value='统计' onclick="statistics();"/>
								</logic:equal>
								<input type="button" value="打印" class="mybutton" onclick="outExcel();">
							</span>
							<span id="fromnumview" style="display:none;">
								从<html:text name="configParameterForm" onkeypress="event.returnValue=IsDigit(this);" property="fromnum" style="width:80px;" />
								到<html:text name="configParameterForm" onkeypress="event.returnValue=IsDigit(this);" property="tonum" style="width:80px;" />
								<input type='button' class="mybutton" value='查询' onclick="searchAll();"/>
								<input type='button' class="mybutton" value='高级' onclick="selectQ();"/>
								<logic:equal name="configParameterForm" property="model" value="2">
								<input type='button' class="mybutton" value='统计' onclick="statistics();"/>
								</logic:equal>
								<input type="button" value="打印" class="mybutton" onclick="outExcel();">
							</span>
							<span id="fromdateview" style="display:none;">
							从<input type="text" name="fromdate"  value="${configParameterForm.fromdate}"  extra="editor" style="width:80px;font-size:10pt;text-align:left" dropDown="dropDownDate">
							到<input type="text" name="todate" value="${configParameterForm.todate}" extra="editor" style="width:80px;font-size:10pt;text-align:left" dropDown="dropDownDate">
							<input type='button' class="mybutton" value='查询' onclick="searchAll();"/>
							<input type='button' class="mybutton" value='高级' onclick="selectQ();"/>
							<logic:equal name="configParameterForm" property="model" value="2">
								<input type='button' class="mybutton" value='统计' onclick="statistics();"/>
							</logic:equal>
							<input type="button" value="打印" class="mybutton" onclick="outExcel();">
							</span>
							<span id="codeidview">
							<select name="codeid"  style="width:150;">
             				</select>
             				<input type='button' class="mybutton" value='查询' onclick="searchAll();"/>
             				<input type='button' class="mybutton" value='高级' onclick="selectQ();"/>
             				<logic:equal name="configParameterForm" property="model" value="2">
								<input type='button' class="mybutton" value='统计' onclick="statistics();"/>
							</logic:equal>
							<input type="button" value="打印" class="mybutton" onclick="outExcel();">
             				</span>
             				<span id="allbutton" style="display:none;">
             					<input type='button' class="mybutton" value='查询' onclick="searchAll();"/>
             					<input type='button' class="mybutton" value='高级' onclick="selectQ();"/>
             					<logic:equal name="configParameterForm" property="model" value="2">
									<input type='button' class="mybutton" value='统计' onclick="statistics();"/>
								</logic:equal>
								<input type="button" value="打印" class="mybutton" onclick="outExcel();">
             				</span>
						</td>
					</tr>
				</table>
				</td>
			</tr>
			<tr>
				<td align="left" class="RecordRow" nowrap>
					<table border="0" width="100%" cellspacing="0"  cellpadding="0">
					<tr>
						<td align="right" nowrap>
						 按:
						<html:select name="configParameterForm" property="fieldid" onchange="searchSort();"  style="width:120">
    							<html:optionsCollection property="itemList" value="dataValue" label="dataName" />
 						</html:select> 
 						<html:select name="configParameterForm" property="sortid" onchange="searchSort();" style="width:50">
    							<html:optionsCollection property="sortList" value="dataValue" label="dataName" />
 						</html:select> 
                		<input type='button' class="mybutton"  value='组合排序' onclick="sortItems();"/>
                		</td>
					</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td  nowrap >
					<div class="myfixedDiv">
						<table width="100%" border="0" cellspacing="0" align="center"
							cellpadding="0" class="ListTableF">
						<tr class="headerTr">
							<logic:iterate id="info" name="configParameterForm" property="fieldList">
							<bean:define id="fids" name="info" property="name"/>
							<logic:notEqual value="A0100" name="fids" >
							<logic:notEqual value="i9999" name="fids" >
							<td align="center" class="TableRow" nowrap>
								<bean:write name="info" property="label"/>
							</td>
							</logic:notEqual>
							</logic:notEqual>
							
						</logic:iterate>
						</tr>
						<hrms:paginationdb id="element" name="configParameterForm" sql_str="configParameterForm.sqlstr" table="" 
						where_str="configParameterForm.wherestr" columns="configParameterForm.column" 
						order_by="configParameterForm.orderby" pagerows="${configParameterForm.pagerows}" page_id="pagination" indexes="indexes">
						<%if(i%2==0){%>
          				<tr class="trShallow">
          				<%}else{%>
          				<tr class="trDeep">
         				<% }i++;%>  
						<logic:iterate id="info" name="configParameterForm" property="fieldList">
							<bean:define id="fid" name="info" property="name"/>
							<logic:notEqual  name="info" property="name" value="A0100">
							<logic:notEqual  name="info" property="name" value="i9999">
							<td align="left" class="RecordRow" nowrap>&nbsp;
								<bean:define id="codesetid" name="info" property="codesetid"/>
								<logic:equal value="0" name="codesetid" >
									<logic:equal value="B0110" name="fids" >
										<hrms:codetoname codeid="UN" name="element" codevalue="${fid}" codeitem="codeitem" scope="page"/>  	      
          	             				<bean:write name="codeitem" property="codename" />&nbsp;
									</logic:equal>
									<logic:notEqual value="B0110" name="fids" >
									<bean:write name="element" property="${fid}" filter="false"/>
									</logic:notEqual>
								</logic:equal>
								<logic:notEqual value="0" name="codesetid" >
									<logic:equal value="B0110" name="fids" >
										<hrms:codetoname codeid="UN" name="element" codevalue="${fid}" codeitem="codeitem" scope="page"/>  	      
          	             				<bean:write name="codeitem" property="codename" />&nbsp;
									</logic:equal>
									<logic:equal value="E0122" name="fids" >
										<hrms:codetoname codeid="UM" name="element" codevalue="${fid}" codeitem="codeitem" scope="page"/>  	      
          	             				<bean:write name="codeitem" property="codename" />&nbsp;
									</logic:equal>
									<logic:equal value="E01A1" name="fids" >
										<hrms:codetoname codeid="@K" name="element" codevalue="${fid}" codeitem="codeitem" scope="page"/>  	      
          	             				<bean:write name="codeitem" property="codename" />&nbsp;
									</logic:equal>
									<logic:notEqual value="B0110" name="fids" >
									<logic:notEqual value="E0122" name="fids" >
									<logic:notEqual value="E01A1" name="fids" >
										<hrms:codetoname name="element" codeid="${codesetid}" codeitem="codeitem" codevalue="${fid}" scope="page"/>
										<bean:write name="codeitem" property="codename"/>&nbsp;
									</logic:notEqual>
									</logic:notEqual>
									</logic:notEqual>
								</logic:notEqual>
								</td>
							</logic:notEqual>
							</logic:notEqual>
						</logic:iterate>
						</tr>
						</hrms:paginationdb>
						</table>
					</div>
				</td>
		</tr>
		<tr>
			<td>
			<table  width="100%" border="0" class="RecordRowP">
				<tr>
					<td valign="bottom" class="tdFontcolor">
					<hrms:paginationtag name="configParameterForm" pagerows="${configParameterForm.pagerows}" property="pagination" scope="page" refresh="true"></hrms:paginationtag>
					</td>
	    			<td  align="right" nowrap class="tdFontcolor">
		     			<p align="right"><hrms:paginationdblink name="configParameterForm" property="pagination" nameId="configParameterForm" scope="page">
						</hrms:paginationdblink>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
			<td>
			<table  width="100%" border="0" class="RecordRowP">
				<tr>
					<td valign="bottom" class="tdFontcolor">
					 <logic:equal value="poloicewizard" name="configParameterForm" property="returnvalue">
                          <input type='button' name='b_save' value='返回' onclick='returnTO();' class='mybutton'>
                     </logic:equal>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
</html:form>
<script language="javascript">
function toggles(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "block";
	}
} 
function hides(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "none";
	}
}
function selectQ(){
	var dbname=document.getElementById("dbname").value;
	var thecodeurl="/general/inform/search/generalsearch.do?b_query=link&type=1&a_code=${configParameterForm.treeCode}&tablename="+dbname;
    var  return_vo= window.showModalDialog(thecodeurl, "", 
     "dialogWidth:700px; dialogHeight:430px;resizable:no;center:yes;scroll:no;status:no");
    if(return_vo!=null){
     	document.getElementById("highsearch").value=return_vo;
     	document.getElementById("itemid").value="no";
     	configParameterForm.action="/performance/totalrank/totalrank.do?b_look=link&a_code=${configParameterForm.treeCode}";
		configParameterForm.submit();
    }
}
function  searchSort(){
	document.getElementById("sortitem").value="";
	configParameterForm.action="/performance/totalrank/totalrank.do?b_look=link&a_code=${configParameterForm.treeCode}";
	configParameterForm.submit();
}
function  changeField(){
	configParameterForm.action="/performance/totalrank/totalrank.do?b_look=link&a_code=${configParameterForm.treeCode}";
	configParameterForm.submit();
}
function  searchAll(){
	var searchs = "no";
	document.getElementById("fromScope").value="";
    document.getElementById("toScope").value="";
    document.getElementById("timeitemid").value="";
	var item=document.getElementById("itemid").value;
	if(item=='no'){
		searchs = "all";
	}
	document.getElementById("highsearch").value="";
	configParameterForm.action="/performance/totalrank/totalrank.do?b_look=link&search="+searchs+"&a_code=${configParameterForm.treeCode}";
	configParameterForm.submit();
}
function changeCodeValue(){
  	var item=document.getElementById("itemid").value;
  	if(item=='no'){
  		hides("fromdateview");
		hides("codeidview");
		hides("textview");
		hides("fromnumview");
		toggles("allbutton");
  		return;
  	}
  	if(item==null||item==undefined||item.length<1){
  		hides("fromdateview");
		hides("codeidview");
		hides("textview");
		hides("fromnumview");
		toggles("allbutton");
  		return ;
  	}
	var in_paramters="itemid="+item;
    var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showCodeFieldList,functionId:'3020050012'});
}
function showCodeFieldList(outparamters){
	var codelist=outparamters.getValue("codelist");
	var typeid=outparamters.getValue("typeid");
	if(typeid=='A'){
		if(codelist!=null&&codelist.length>1){
			toggles("codeidview");
			hides("textview");
			hides("fromnumview");
			hides("fromdateview");
			hides("allbutton");
			AjaxBind.bind(configParameterForm.codeid,codelist);
			document.getElementById("codeid").value="${configParameterForm.codeid}";
		}else{
			toggles("textview");
			hides("codeidview");
			hides("fromnumview");
			hides("fromdateview");
			hides("allbutton");
		}
	}else if(typeid=='N'){
		toggles("fromnumview");
		hides("codeidview");
		hides("textview");
		hides("fromdateview");
		hides("allbutton");
	}else if(typeid=='D'){
		toggles("fromdateview");
		hides("codeidview");
		hides("textview");
		hides("fromnumview");
		hides("allbutton");
	}else{
		hides("fromdateview");
		hides("codeidview");
		hides("textview");
		hides("fromnumview");
		toggles("allbutton");
	}
}
function statistics() {
	var iframe_url = "/performance/totalrank/totalrank.do?b_stat=link";
	var return_vo= window.showModalDialog(iframe_url, "", 
              "dialogWidth:500px; dialogHeight:250px;resizable:no;center:yes;scroll:no;status:no");
    if(return_vo!=null){
    	var tiemArr = return_vo.split("::");
    	if(tiemArr.length==3){
    		document.getElementById("fromScope").value=tiemArr[1];
    		document.getElementById("toScope").value=tiemArr[2]
    		document.getElementById("timeitemid").value=tiemArr[0]
    		configParameterForm.action="/performance/totalrank/totalrank.do?b_look=link&a_code=${configParameterForm.treeCode}";
			configParameterForm.submit();
    	}
    }
}
function sortItems() {
	var thecodeurl ="/gz/sort/sorting.do?b_query=link&flag=zfw&setid=${configParameterForm.setid}";
	thecodeurl+="&model=${configParameterForm.model}&sortitem="+getEncodeStr("${configParameterForm.sortitem}"); 
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:510px; dialogHeight:420px;resizable:no;center:yes;scroll:yes;status:no");
	if(return_vo!=null&&return_vo!="not"){
		document.getElementById("sortitem").value=return_vo;
    	document.getElementById("fieldid").value="no";
    	configParameterForm.action="/performance/totalrank/totalrank.do?b_look=link&a_code=${configParameterForm.treeCode}";
		configParameterForm.submit();
    }
}
function outExcel(){
	var hashvo=new ParameterSet();
	hashvo.setValue("fieldsetid","${configParameterForm.setid}");
	hashvo.setValue("strsql","${configParameterForm.ecxelsql}");
	var request=new Request({method:'post',asynchronous:false,
		onSuccess:showExcel,functionId:'9026006017'},hashvo);
}
function showExcel(outparamters){
	var outName=outparamters.getValue("outName");
	var name=outName.substring(0,outName.length-1)+".xls";
	window.open("/servlet/vfsservlet?fromjavafolder=true&fileid="+name,"xls");
}
function IsDigit(obj) {
	if((event.keyCode >= 46) && (event.keyCode <= 57)){
		var values=obj.value;
		if((event.keyCode == 46) && (values.indexOf(".")!=-1))
			return false;
		if((event.keyCode == 46) && (values.length==0))
			return false;	
		
		if((values.lastIndexOf(".")<values.length)&&(values.indexOf(".")!=1)){
			return true;
		}else{
			return false;
		}
	}else{
		return false;
	}
}
changeCodeValue();

</script>