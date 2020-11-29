<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import=" java.util.*,com.hjsj.hrms.actionform.performance.data_collect.Data_collectForm"%>
<script language="Javascript" src="/gz/salary.js"/></script>
<script language="javascript" src="/js/dict.js"></script> 
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<%
	Data_collectForm dcform =(Data_collectForm) session.getAttribute("data_collectForm");
	ArrayList dbList = dcform.getDbList();
	String personScope = dcform. getPersonScope();
%>
<style type="text/css">
	#tff{
		position:absolute;
		top:10%;
		left:20%;
	}
	#tf{
		position:absolute;
		top:10%;
		left:10%;
	}
</style>
<script language="javascript">
	function simpleCondition()
  	{
  		var info,queryType,dbPre;
	    info="1";
	    dbPre="Usr";
        queryType="1";
        var express="";
        var strExpression = generalExpressionDialog(info,dbPre,queryType,express);
        if(strExpression)
        {	
        	var temps=strExpression.split("|");
        	document.getElementById("cexpr").value=temps[1];
        	var rr = document.getElementById("cexpr").value;
        	document.getElementById("personScope").value="1";
        	var personScope_s = document.getElementById("personScope_s");
        	personScope_s.checked=true;
        }
  	}
	 function complexCondition()
  	{
  		var personScope = document.getElementById("personScope").value;
  		var cexpr = document.getElementById("cexpr").value;
  		if(""==personScope||"1"==personScope){
  			cexpr="";
  		}
  		var strExpression=generalComplexConditionDialog(cexpr,"0",GZ_TEMPLATESET_LOOKCONDITION,"4");
  		 if(strExpression!=undefined)
        {
			document.getElementById("cexpr").value=strExpression;
			var personScope_c = document.getElementById("personScope_c");
			document.getElementById("personScope").value="2";
			personScope_c.checked=true;
        }
  	}
  	function clearCondition(){
  		 if(confirm(GZ_TEMPLATESET_INFO33+"！"))
  	   {
	  		var arr = new Array();
	  		var sdbid="";
	  		 arr= document.getElementsByName("dbValue");
	  		 for(var i=0;i<arr.length;i++){
	  		 	if(arr[i].checked){
	  		 		sdbid=sdbid+arr[i].value+",";
	  		 	}
	  		 }
	  		 var audit = document.getElementById("audit").value;
	  		 var fieldsetid= document.getElementById("fieldsetid").value;
	  		 var personScope=document.getElementById("personScope").value;
	  		 var cexpr = "";
	  		 var hashvo=new ParameterSet();		
	  		 hashvo.setValue("sdbid",sdbid);
			 hashvo.setValue("audit",audit);
			 hashvo.setValue("fieldsetid",fieldsetid);
			 hashvo.setValue("personScope",personScope);
			 hashvo.setValue("cexpr",cexpr);
			 var request=new Request({method:'post',asynchronous:true,onSuccess:clearpama_ok,functionId:'3020073052'},hashvo);
		}
  	}
  	function clearpama_ok(){
  		alert("条件已清空");
  		data_collectForm.action="/performance/data_collect/data_collect.do?b_setpama=link";
  		data_collectForm.submit();
  	}
  	function setpama_save(){
  		var arr = new Array();
  		var sdbid="";
  		 arr= document.getElementsByName("dbValue");
  		 for(var i=0;i<arr.length;i++){
  		 	if(arr[i].checked){
  		 		sdbid=sdbid+arr[i].value+",";
  		 	}
  		 }
  		 if(""==sdbid){
  		 	alert("请至少选择一个人员库");
  		 	return false;
  		 }
  		 var audit = document.getElementById("audit").value;
  		 var fieldsetid= document.getElementById("fieldsetid").value;
  		 var personScope=document.getElementById("personScope").value;
  		 if(""==personScope){
  		 	alert("请选择人员范围");
  		 }
  		 var cexpr = document.getElementById("cexpr").value;
  		 var hashvo=new ParameterSet();		
  		hashvo.setValue("sdbid",sdbid);
		hashvo.setValue("audit",audit);
		hashvo.setValue("fieldsetid",fieldsetid);
		hashvo.setValue("personScope",personScope);
		hashvo.setValue("cexpr",cexpr);
		var request=new Request({method:'post',asynchronous:true,onSuccess:setpama_ok,functionId:'3020073052'},hashvo);
  	}
  	function setpama_ok(){
  		data_collectForm.action="/performance/data_collect/data_collect.do?b_query=link";
  		data_collectForm.submit();
  	}
  	function setpama_cancel(){
  		data_collectForm.action="/performance/data_collect/data_collect.do?b_query=link";
  		data_collectForm.submit();
  	}
function yincrease(){
    var yearnum = document.getElementById("yearnum").value; 
    var yearset = parseInt(yearnum);
	yearset = yearset+1;
	document.getElementById("yearnum").value = yearset;
	data_collectForm.action="/performance/data_collect/data_collect.do?b_query=link&opt=2";
  	data_collectForm.submit();
}
function ysubtract(){
    var yearnum = document.getElementById("yearnum").value; 
    var yearset = parseInt(yearnum);
	if(yearset<1991){
		document.getElementById("yearnum").value = 1990;
	}else{
		yearset = yearset-1;
		document.getElementById("yearnum").value = yearset;
	}
	data_collectForm.action="/performance/data_collect/data_collect.do?b_query=link&opt=2";
  	data_collectForm.submit();
}
function add(){
	var infos=new Array();
	infos[0]="${data_collectForm.fieldsetid}";
	var thecodeurl="/performance/data_collect/data_collect.do?b_add=link"; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
	var retvo= window.showModalDialog(iframe_url, infos, 
		        "dialogWidth:520px; dialogHeight:230px;resizable:no;center:yes;scroll:yes;status:no");			
  	 if(retvo==null){
  	 	return ;
  	 }else{
  	 	data_collectForm.action="/performance/data_collect/data_collect.do?b_query=link";
  		data_collectForm.submit();
  	 }	
}
 function query()
 {
  	 	data_collectForm.action="/performance/data_collect/data_collect.do?b_query=link&opt=2";
  		data_collectForm.submit();
 }
 function outExcel(){
 	var hashvo=new ParameterSet();	
	hashvo.setValue("tablename",getEncodeStr(${data_collectForm.tablename}));  
	hashvo.setValue("sql","${data_collectForm._sql}");
	hashvo.setValue("fieldsetid","${data_collectForm.fieldsetid}");
	var request=new Request({method:'post',asynchronous:false,onSuccess:showfile1,functionId:'3020073065'},hashvo);
 }
function showfile1(outparamters)
{
	var fileName=outparamters.getValue("fileName");
	//xus 20/4/30 vfs改造
	var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+fileName,"excel");
}
function exportExcel()
{
var infos=new Array();
infos[0]="${data_collectForm.fieldsetid}";
var theURL="/performance/data_collect/data_collect.do?br_file=link`opt=0";
var iframe_url="/performance/data_collect/iframe_export.jsp?src="+$URL.encode(theURL);
var objlist =window.showModalDialog(iframe_url,infos,"dialogWidth=520px;dialogHeight=250px;resizable=yes;status=no;");  
if(objlist==null)
return;
var obj=new Object();
obj.fresh=objlist.fresh;
if(obj.fresh=="1")
{
		data_collectForm.action="/performance/data_collect/data_collect.do?b_query=link";
		data_collectForm.submit();
}
else
{    
}
}
function hand_importMen()
{
	var vos= document.getElementById("filtervalue");
	var yearnum = document.getElementById("yearnum").value; 
	var options=vos.options; 
	var yue = "";
	for(var i=0;i<options.length;i++){
		if(options[i].selected==true){
			yue = options[i].value;
		}
	} 
	if(yue=="0"){
		alert("请选择具体的年月");
		return;
	}
	var infos=new Array();
	infos[0]="${data_collectForm.dbname}";
	infos[1]=yearnum;
	infos[2]=yue;
	infos[3]="${data_collectForm.fieldsetid}";
	var strurl="/performance/data_collect/data_collect.do?br_handImportMen=query";
	var iframe_url="/gz/gz_accounting/bankdisk/iframe_bank_disk.jsp?src="+strurl; 
	var flag=window.showModalDialog(iframe_url,infos,"dialogWidth=600px;dialogHeight=480px;resizable=yes;scroll=no;status=no;");  
	if(flag=="1")
	{
		data_collectForm.action="/performance/data_collect/data_collect.do?b_query=link";
		data_collectForm.submit();
	}
}
function get_formula()
{
	var infos=new Array();
	infos[0]="${data_collectForm.dbname}";
	infos[1]="${data_collectForm.fieldsetid}";
	infos[2]="${data_collectForm.ym}";
	var thecodeurl="/performance/data_collect/data_collect.do?b_getformula=link"; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
	var retvo= window.showModalDialog(iframe_url, infos, 
		        "dialogWidth:460px; dialogHeight:550px;resizable:no;center:yes;scroll:yes;status:no");			
  	 if(retvo==null)
  	 	return ;		        
	if(retvo.success=="1")
	{
		data_collectForm.action="/performance/data_collect/data_collect.do?b_query=link";
		data_collectForm.submit();
	}
}
function dataappeal(){
		if(confirm("确定把所有起草和驳回的记录都报批吗？"))
		{
			data_collectForm.action="/performance/data_collect/data_collect.do?b_appeal=link";
			data_collectForm.submit();
		}else{
			return;
		}
}
function dataapprove(){
		if(confirm("确定批准所有已报批的记录吗？"))
		{
			data_collectForm.action="/performance/data_collect/data_collect.do?b_approve=link";
			data_collectForm.submit();
		}else{
			return;
		}
}
function inc_month(){
    var value = document.getElementById("filtervalue").value; 
    var value = parseInt(value);
	value = value+1;
	if(value>12)
	  value=12;
	if(value<10){
		value = value;
	}
	document.getElementById("filtervalue").value = value;
	data_collectForm.action="/performance/data_collect/data_collect.do?b_query=link&opt=2";
  	data_collectForm.submit();
}
function dec_month(){
    var value = document.getElementById("filtervalue").value; 
    var value = parseInt(value);
    	value = value-1;
		if(value<=0)
			value=1;
		if(value<10){
			value = value;
		}	
	document.getElementById("filtervalue").value = value;
	data_collectForm.action="/performance/data_collect/data_collect.do?b_query=link&opt=2";
  	data_collectForm.submit();
}
</script>
<body >
	<html:form action="/performance/data_collect/data_collect">
	<%if(dcform.getIsHave().equals("2")){%>
		<html:hidden name="data_collectForm" property="cexpr"/>
		<html:hidden name="data_collectForm" property="personScope"/>
		<html:hidden name="data_collectForm" property="fieldsetid"/>
		<logic:empty name="data_collectForm" property="auditList">
<br>
<br>
<br>
		<table>
			<tr>
				<td>&nbsp;&nbsp;&nbsp;</td>
				<td>注意</td>
				<td>：</td>
			</tr>
			<tr>
				<td>&nbsp;&nbsp;&nbsp;</td>
				<td></td>
				<td>1.所选子集必须年月变化子集。</td>
			</tr>
			<tr>
				<td>&nbsp;&nbsp;&nbsp;</td>
				<td></td>
				<td>2.子集中未关联代码类23的指标(审批指标)且该指标要构库。</td>
			</tr>
		</table>
		
		
		</logic:empty>
		<logic:notEmpty name="data_collectForm" property="auditList">
	<fieldset align="center" style="width:70%;" style="border:0px">
			<br/>
			<div id="nomal" align="center" style="width:90%;">
			<fieldset align="center" style="width:90%;">
			 <legend ><bean:message key="gz.acount.sp.state"/></legend>
			<table width="90%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
  				<tr>
  					<td width="40%"align="left"><bean:message key="gz.acount.sp.state"/>:
  						<hrms:optioncollection name="data_collectForm" property="auditList" collection="list" />
  						<html:select name="data_collectForm" property="audit" style="width:152">
							<html:options collection="list" property="dataValue" labelProperty="dataName" />
						</html:select>(关联代码类23)
					</td>
  				</tr>
		      </table>
			 <br/>
			 </fieldset>
			 <br/>
			 <br/>
			  <fieldset align="center" style="width:90%;">
			  		<legend ><bean:message key="gz.columns.nbase"/></legend>
			  		<div style="overflow:auto;height:150px;" >
			  			<table width="90%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
			  				<%
			  					for(int i=0;i<dbList.size();i++){
			  						HashMap  tMap = (HashMap)dbList.get(i);
			  				%>
			  					<tr>
			  						<td>
			  							<input type='checkbox' name='dbValue'
			  							 <%
			  								if("1".equals((String)tMap.get("isSelect"))){
			  								%>
												checked value='<%=tMap.get("dbid")%>' />			  								
			  							<%
			  								}
			  							else{
			  							%>
			  								value='<%=tMap.get("dbid")%>' />
			  							<%
			  							}
			  							%>
			  						</td>
			  						<td align="left"><%=tMap.get("dbname")%></td>
			  					</tr>
			  				<%
			  					}
			  				%>
		                	
					    </table>
					    </div>
			  </fieldset>
			  <br>&nbsp;<br>
		                 <fieldset align="center" style="width:90%;">
    							 <legend ><bean:message key="gz.columns.menScope"/></legend>
		                      			<table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		                      				<tr>
			                					<td width="20%" height="25" >
			                		            <input name="personScope" type="radio" id="personScope_s" value="2" 
			                					<%
			                						if("1".equals(personScope)){
			                					%>
			                						checked="checked"
			                					<%
			                						}
			                					%> 
			                					 />
			                					 <bean:message key="gz.templateset.simpleCondition"/>
			                		            <Input type='button' value='...'  class="mybutton"  onclick="simpleCondition();"/>&nbsp;&nbsp;&nbsp;&nbsp;
			                		            <Input type='button' value='<bean:message key="button.clearup"/>'  class="mybutton"  onclick="clearCondition();" />
			                					</td>
			                				</tr>
			                				<tr>
			                					<td width="20%" height="25" >
			                					<input name="personScope" type="radio" id="personScope_c" value="2" 
			                					<%
			                						if("2".equals(personScope)){
			                					%>
			                						checked="checked"
			                					<%
			                						}
			                					%> 
			                					 />
			                		            <bean:message key="gz.templateset.complexCondition"/>&nbsp;
			                		            <Input type='button' value='...'  class="mybutton"  onclick="complexCondition();"/>
			                					</td>
			                				</tr>
		                      			</table>
		                      			<br>
		                 </fieldset>
			  
			  
			  <br/> <br/>

			  <table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">  
				<tr>
					<td width="10%" align="center">
	     				<Input type='button' value='<bean:message key="lable.tz_template.enter"/>'  class="mybutton"  onclick="setpama_save()" /> 
	    	 				&nbsp;&nbsp;&nbsp;
	  	 				<Input type='button' value='<bean:message key="lable.tz_template.cancel"/>'  class="mybutton"  onclick="setpama_cancel()" /> 
	  	 			</td>
	  	 	  </tr>
  	 		</table>

  	 		</div>
		</fieldset>
	</logic:notEmpty>
	<%}else if(dcform.getIsHave().equals("1")){ %>
	<table>
      <tr>
      <td>
      <bean:message key="gz.acount.annual"/>
      	<html:text name="data_collectForm" property="yearnum" onkeypress="event.returnValue=IsDigit();" style="width:40"/>
      </td>
      <td valign="middle" align="left">
		<table border="0" cellspacing="2" cellpadding="0" >
			<tr><td><button id="y_up" class="m_arrow" onclick="yincrease();">5</button></td></tr>
			<tr><td><button id="y_down" class="m_arrow" onclick="ysubtract();">6</button></td></tr>
		</table>
	  </td>
	  <td align="left">
	  			<bean:message key="gz.acount.month"/>
	  			<html:select name="data_collectForm" property="filtervalue"  onchange="query();">
			 				<html:optionsCollection property="filterList" value="dataValue" label="dataName" />
						</html:select>
	  </td>
	          	
	   
	  <td align="left">&nbsp;	  
	  		<bean:message key="jx.khplan.spstatus"/>：	  	  
	  		<html:select name="data_collectForm" property="spType" onchange="query();">
			 	<html:optionsCollection property="spTypeList" value="dataValue" label="dataName" />
			</html:select>		
	  </td>
	  <td align="left">&nbsp;	  
	  		<bean:message key="label.dbase"/>：	  	  
	  		<html:select name="data_collectForm" property="dbname" onchange="query();">
			 	<html:optionsCollection property="dblist" value="dataValue" label="dataName" />
			</html:select>		
	  </td>
	  </tr>


	  </table>
	  <table>
	  	  <tr>
	  <td>
<hrms:dataset name="data_collectForm" property="fieldlist" scope="session" setname="${data_collectForm.tablename}" pagerows="${data_collectForm.pagerows}" setalias="data_collect" readonly="false"  rowlock="true"  rowlockfield="zt"    rowlockvalues=",01,07," editable="true" select="true" sql="${data_collectForm._sql}" buttons="bottom">      

	<hrms:commandbutton name="add" onclick="add();"  function_id="0608060101"    ><bean:message key="button.insert"/>
	</hrms:commandbutton> 
	<hrms:commandbutton name="deleterecord" hint="gz.acount.determined.del" functionId="3020073063"  function_id="0608060102"  refresh="true" type="selected" setname="${data_collectForm.tablename}">
		<bean:message key="button.delete"/>
	</hrms:commandbutton> 
	<hrms:commandbutton name="ru" hint="org.orgpre.orgpretable.baocunok"  functionId=""  function_id="0608060103"   refresh="true" type="all-change" setname="${data_collectForm.tablename}" onclick="hand_importMen();">
	    <bean:message key="label.gz.importMen"/>
	</hrms:commandbutton> 
	<hrms:commandbutton name="save" hint="org.orgpre.orgpretable.baocunok"  functionId="3020073064"  function_id="0608060104"   refresh="true" type="all-change" setname="${data_collectForm.tablename}" >
	    <bean:message key="button.save"/>
	</hrms:commandbutton> 
	<hrms:commandbutton name="computa" hint="org.orgpre.orgpretable.baocunok"  functionId=""  function_id="0608060105"   refresh="true" type="all-change" setname="${data_collectForm.tablename}" onclick="get_formula();">
	    <bean:message key="infor.menu.compute"/>
	</hrms:commandbutton> 
	<hrms:commandbutton name="appeal" hint="org.orgpre.orgpretable.baopiok" functionId=""  refresh="true"  function_id="0608060106"  type="selected" setname="${data_collectForm.tablename}" onclick="dataappeal();">
		<bean:message key="button.appeal"/>
	</hrms:commandbutton>
	<hrms:commandbutton name="release" hint="org.orgpre.orgpretable.approvalok" functionId=""  refresh="true"   function_id="0608060107"  type="selected" setname="${data_collectForm.tablename}" onclick="dataapprove();">
		<bean:message key="button.approve"/>
	</hrms:commandbutton> 
	<hrms:commandbutton name="suspended" hint="org.orgpre.orgpretable.bohuiok" functionId="3020073072"  refresh="true"  function_id="0608060108"  type="selected" setname="${data_collectForm.tablename}">
		<bean:message key="button.reject"/>
	</hrms:commandbutton>

	<hrms:commandbutton name="outputexc" onclick="outExcel();"  function_id="0608060109">
		<bean:message key="button.download.template"/>
	</hrms:commandbutton>  
	<hrms:commandbutton name="inputexc" onclick="exportExcel();"  function_id="0608060110">
		<bean:message key="import.tempData"/>
	</hrms:commandbutton> 	
</hrms:dataset>
</td>
</tr>
	  </table>
	<%}%>
	</html:form>
</body>