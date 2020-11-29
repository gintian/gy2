<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="Javascript" src="/gz/salary.js"/></script>
<script language="javascript" src="/js/dict.js"></script> 
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.gz.gz_accounting.TaxTableForm,com.hjsj.hrms.utils.PubFunc,com.hrms.frame.codec.SafeCode"%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<%

UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	TaxTableForm form=(TaxTableForm)session.getAttribute("taxTableForm"); 
	String m_sql=SafeCode.encode(PubFunc.encrypt(form.getSql()));
	String returnvalue=form.getReturnvalue();
	if(returnvalue==null)
		returnvalue="";
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
	
 %>
<script language="javascript">

	var urlParams = '&returnFlag=${taxTableForm.returnFlag}&theyear=${taxTableForm.theyear}&themonth=${taxTableForm.themonth}&operOrg=${taxTableForm.operOrg}';
	function to_import_tax_mx_excel()
	{
		taxTableForm.action="/gz/gz_accounting/tax/import_tax_mx_excel.do?br_import=link";
	    taxTableForm.submit();		
	}

	function query()
	{
		// gby,2015-01-19,根据姓名进行模糊查询。添加标识：signQueryValue
		taxTableForm.action="/gz/gz_accounting/tax/search_tax_table.do?b_query=link&a_code=${taxTableForm.a_code}&init=date&signQueryValue=1";
	    taxTableForm.submit();	
	}
	function query1()
	{
		var  declaredate =  document.getElementById("declaredate").value;	
		if(declaredate=='all'){
			taxTableForm.action="/gz/gz_accounting/tax/search_tax_table.do?b_query=link&a_code=${taxTableForm.a_code}&init=date&initflag=1";
		}else{
			taxTableForm.action="/gz/gz_accounting/tax/search_tax_table.do?b_query=link&a_code=${taxTableForm.a_code}&init=date";
		}
	    taxTableForm.submit();	
	}
	function to_maintenan_tax()
	{	
		var theURL = "/gz/gz_accounting/tax/maintenan_tax_table.do?b_query=link";
		if(isIE6()){
				var return_vo =window.showModalDialog(theURL,"",
			"dialogWidth=580px;dialogHeight=440px;resizable=yes;scroll:yes;center:yes;status=no;");  
		}else{
				var return_vo =window.showModalDialog(theURL,"",
			"dialogWidth=550px;dialogHeight=440px;resizable=yes;scroll:yes;center:yes;status=no;");  
		}
		if(return_vo!=null){
			 taxTableForm.action="/gz/gz_accounting/tax/search_tax_table.do?b_query=link&a_code=${taxTableForm.a_code}&init=main"+urlParams;
   			 taxTableForm.submit();  
		}
	}
	
	function to_sort_taxmx()
	{
		theURL = "/gz/gz_accounting/tax/sort_tax_table.do?b_query=link"
		if(isIE6() ){
				var return_vo =window.showModalDialog(theURL,"",
			"dialogWidth=320px;dialogHeight=380px;resizable=no;scroll:yes;center:yes;status=no;");
		}else{
				var return_vo =window.showModalDialog(theURL,"",
			"dialogWidth=300px;dialogHeight=380px;resizable=no;scroll:yes;center:yes;status=no;");
		}
		if(return_vo!=null){
			 taxTableForm.action="/gz/gz_accounting/tax/search_tax_table.do?b_query=link&a_code=${taxTableForm.a_code}&init=sort"+urlParams;
   			 taxTableForm.submit();  
		}
	}
	
	function to_hide_taxmx()
	{
		var theURL="/gz/gz_accounting/tax/hide_tax_table.do?b_query=link";
		var return_vo =window.showModalDialog(theURL,"",
			"dialogWidth=350px;dialogHeight=360px;resizable=yes;scroll:no;center:yes;status=no;");  
		if(return_vo!=null){
			 taxTableForm.action="/gz/gz_accounting/tax/search_tax_table.do?b_query=link&a_code=${taxTableForm.a_code}&init=hide"+urlParams;
   			 taxTableForm.submit();  
		}
		
	}
	
	
	function outExcel(){
	var exporttype="0";
	if(confirm(EXPORT_PERSON_COND))
	{
	    exporttype="1";
	}
	else
	{
	    exporttype="0";
	}
	var  declaredate = document.getElementById("declaredate").value;
	var  a_code = '${taxTableForm.a_code}';	
	var  condtionsql = document.getElementById("condtionsql").value;
	var hashvo=new ParameterSet();
	hashvo.setValue("declaredate",declaredate);
	hashvo.setValue("a_code",a_code);
	hashvo.setValue("condtionsql",condtionsql);
	hashvo.setValue("exporttype",exporttype);
	hashvo.setValue("filterByMdule","${taxTableForm.filterByMdule}");
	hashvo.setValue("fromtable","${taxTableForm.tax_tablename}");
	var In_paramters="flag=1"; 	
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showfile,functionId:'3020091007'},hashvo);		
}
function to_template()
{
	taxTableForm.action="/gz/gz_accounting/tax/tax_export_template.do?b_query=link";
	taxTableForm.submit();
}


//个人扣缴报税
function outExcel_ks(){
	var exporttype="3";
	var  declaredate = document.getElementById("declaredate").value;
	var  a_code = '${taxTableForm.a_code}';	
	var  condtionsql = document.getElementById("condtionsql").value;
	var hashvo=new ParameterSet();
	hashvo.setValue("declaredate",declaredate);
	hashvo.setValue("a_code",a_code);
	hashvo.setValue("condtionsql",condtionsql);
	hashvo.setValue("exporttype",exporttype);
	hashvo.setValue("filterByMdule","${taxTableForm.filterByMdule}");
	hashvo.setValue("fromtable","${taxTableForm.tax_tablename}");
	var In_paramters="flag=1"; 	
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showfile,functionId:'3020091007'},hashvo);		
}
function sumOutExcel(){
	var  path =  document.getElementById("path").value;
	var hashvo=new ParameterSet();
	hashvo.setValue("path",path);
	var In_paramters="flag=1"; 	
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:checkResult,functionId:'3020091010'},hashvo);
	
}
function checkResult(outparamters)
{
	var checkResult=outparamters.getValue("checkResult");
	if(checkResult=="0")
	{
		default_sumOutExcel();
	}else
	{
		taxTableForm.action="/gz/gz_accounting/tax/tax_export_template.do?b_query=link&filterByMdule=${taxTableForm.filterByMdule}&tax_tablename=${taxTableForm.tax_tablename}";
		taxTableForm.submit();
	}
}
function default_sumOutExcel(){
	
	var  declaredate =  document.getElementById("declaredate").value;	
	var  a_code = '${taxTableForm.a_code}';	
	var  condtionsql = document.getElementById("condtionsql").value;
	var hashvo=new ParameterSet();
	hashvo.setValue("declaredate",declaredate);
	hashvo.setValue("a_code",a_code);
	hashvo.setValue("condtionsql",condtionsql);
	hashvo.setValue("filterByMdule","${taxTableForm.filterByMdule}");
	hashvo.setValue("fromtable","${taxTableForm.tax_tablename}");
	var In_paramters="flag=1"; 	
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showfile,functionId:'3020091008'},hashvo);		
}

	function showfile(outparamters){
		var outName=outparamters.getValue("outName");
		var fieldName = getDecodeStr(outName);
		var win=open("/servlet/vfsservlet?fileid="+fieldName+"&fromjavafolder=true","excel");
	}
	
function refresh(outparameters){
	//document.location.reload();
	taxTableForm.action=document.location;
	taxTableForm.submit();
}
function printInform(){
	var  declaredate =  document.getElementById("declaredate").value;
	var fromtable="${taxTableForm.fromTable}";	
	var url="/general/muster/hmuster/searchHroster.do?b_search=link";
	url+="`nFlag=15`sortid=11`a_code=${taxTableForm.a_code}`filterByMdule=${taxTableForm.filterByMdule}";
	url+="`salarydate="+declaredate;
	url+="`fromTable="+fromtable;
	url+="`salaryid=${taxTableForm.salaryid}`closeWindow=1`conSQL="+"${taxTableForm.condtionsql}";
   //	window.open(url);
   	var framesurl = "/general/query/common/iframe_query.jsp?src="+url;
    var return_vo =window.showModalDialog(framesurl,"",
			"dialogWidth="+window.screen.width+"px;dialogHeight="+window.screen.height+"px;resizable=yes;scroll:yes;center:yes;status=no;");
	
}
function bacthDelete()
{
  if(confirm("您确定要删除当前显示的全部记录吗？\r\n如果需要删除选中的记录，请使用个别删除！\r\n删除后，将不能再恢复，请谨慎操作！"))
  {
   var hashvo=new ParameterSet();
	hashvo.setValue("whereSql","<%=m_sql%>");
	hashvo.setValue("fromtable","${taxTableForm.tax_tablename}");
	var request=new Request({method:'post',asynchronous:false,onSuccess:refresh,functionId:'3020091015'},hashvo);
   }
}
</script>
<style type="text/css"> 

.selectPre{
	position:absolute;
    left:450px;
    top:40px;
    z-index: 10;
}
.selectPreback{
	position:absolute;
    left:500px;
    top:35px;
    z-index: 10;
}
</style>
<html:form action="/gz/gz_accounting/tax/search_tax_table">
<%if("hl".equals(hcmflag)){ %>
<table><tr><td>
<%}else{ %>
<table style="margin-top:-5px;"><tr><td>
<%} %>

<html:hidden name="taxTableForm" property="is_back" />
<html:hidden name="taxTableForm" property="path"/>
<html:hidden name="taxTableForm" property="filterByMdule"/>
<html:hidden name="taxTableForm" property="condtionsql"/>

<table><tr><td>
<hrms:menubar menu="menu1" id="menubar1">
  <hrms:menuitem name="gz0" label="menu.file">
    <hrms:menuitem name="m1" label="menu.tax.strut" icon="/images/edit.gif" url="to_maintenan_tax();"  function_id="3240404,3270404" /> 
    <hrms:menuitem name="m2" label="menu.tax.input_m" icon="/images/import.gif" url="to_import_tax_mx_excel()"  function_id="3240405,3270405"  />
    <hrms:menuitem name="m3" label="menu.tax.export_m" icon="/images/export.gif" url="outExcel()"  function_id="3240406,3270406"  />   
    <hrms:menuitem name="m3" label="导出个税扣缴申报表" icon="/images/export.gif" url="outExcel_ks()"  function_id="3240406,3270406"  />   
    <hrms:menuitem name="m4" label="menu.tax.export_s" icon="/images/export.gif" url="sumOutExcel()"  function_id="3240407,3270407"  />              
    <hrms:menuitem name="m1" label="gz_tax_sum_template" icon="/images/edit.gif" url="to_template()"  function_id="3240407,3270407" />
  	<hrms:menuitem name="m5" label="infor.menu.outhmuster" icon="/images/print.gif" url="printInform();" command="" enabled="true" visible="true"/>
  </hrms:menuitem>
  <hrms:menuitem name="gz1" label="menu.gz.edit" >
  	<hrms:menuitem name="m1" label="button.insert" icon="/images/prop_ps.gif"   function_id="3240401,3270401"  url="new_record('${taxTableForm.tax_tablename}','${taxTableForm.a_code}')" command=""  />
    <hrms:menuitem name="m2" label="批量删除" icon="/images/del.gif" url="bacthDelete();" command="" function_id="3240408,3270408" />  
  
  </hrms:menuitem>  
  <hrms:menuitem name="gz2" label="menu.tax.display">
      <hrms:menuitem name="mitem1" label="menu.gz.hide" icon="/images/add_del.gif" url="to_hide_taxmx()" command="" enabled="true" visible="true"/>
      <hrms:menuitem name="mitem2" label="menu.gz.sortitem" icon="/images/write.gif" url="to_sort_taxmx()" command="" enabled="true" visible="true"/>
      <hrms:menuitem name="mitem3" label="button.query" icon="/images/quick_query.gif" url="to_querycondition()" command="" />  
  </hrms:menuitem> 
</hrms:menubar>   
</td></tr></table>
</td>
<td>
 &nbsp;
</td>
</tr>
</table>
<logic:equal value="back" name="taxTableForm" property="is_back">
<hrms:dataset name="taxTableForm" pagerows="${taxTableForm.pagerows}" property="fieldlist" scope="session" setname="${taxTableForm.tax_tablename}"  setalias="tax_table" readonly="false" editable="true" select="true" sql="${taxTableForm.sql}"  buttons="bottom">
	<hrms:commandbutton name="newrecord"  functionId="" refresh="true"  function_id="3240401,3270401"  type="selected" setname="${taxTableForm.tax_tablename}" onclick="new_record('${taxTableForm.tax_tablename}','${taxTableForm.a_code}')">
     <bean:message key="button.insert"/>
   </hrms:commandbutton>
    <hrms:commandbutton name="savedata" functionId="3020091009"   function_id="3240402,3270402"   refresh="false" type="all-change" setname="${taxTableForm.tax_tablename}" >
     <bean:message key="button.save"/>
   </hrms:commandbutton> 
    <hrms:commandbutton name="delselected" hint="general.inform.search.confirmed.del"  functionId="3020091001"  function_id="3240403,3270403" refresh="true" type="selected" setname="${taxTableForm.tax_tablename}" >
    个别删除
   </hrms:commandbutton>
   <hrms:commandbutton name="export_m"  onclick="outExcel();"  function_id="3240406,3270406"  >
     <bean:message key="menu.tax.export_m"/>
   </hrms:commandbutton> 
   <hrms:commandbutton name="export_s"   onclick="sumOutExcel();" function_id="3240407,3270407" >
     <bean:message key="menu.tax.export_s"/>
   </hrms:commandbutton>   
	<hrms:commandbutton name="return_gz"   onclick="return_gz('${taxTableForm.salaryid}');"  >
     <bean:message key="button.return"/>
    </hrms:commandbutton>  
</hrms:dataset>
</logic:equal>	
<logic:notEqual value="back" name="taxTableForm" property="is_back">
<hrms:dataset name="taxTableForm" property="fieldlist" pagerows="${taxTableForm.pagerows}" scope="session" setname="${taxTableForm.tax_tablename}"  setalias="tax_table" readonly="false" editable="true" select="true" sql="${taxTableForm.sql}"  buttons="bottom">
	<hrms:commandbutton name="newrecord"  functionId="" refresh="true" function_id="3240401,3270401" type="selected" setname="${taxTableForm.tax_tablename}" onclick="new_record('${taxTableForm.tax_tablename}','${taxTableForm.a_code}')">
     <bean:message key="button.insert"/>
   </hrms:commandbutton>
   <hrms:commandbutton name="saveselected" functionId="3020091009" function_id="3240402,3270402" refresh="false" type="all-change" setname="${taxTableForm.tax_tablename}" >
     <bean:message key="button.save"/>
   </hrms:commandbutton> 
    <hrms:commandbutton name="delselected" hint="general.inform.search.confirmed.del" functionId="3020091001"  function_id="3240403,3270403" refresh="true" type="selected" setname="${taxTableForm.tax_tablename}" >
    个别删除
   </hrms:commandbutton>
   <hrms:commandbutton name="export_m"  onclick="outExcel();" function_id="3240406,3270406"  >
     <bean:message key="menu.tax.export_m"/>
   </hrms:commandbutton> 
   <hrms:commandbutton name="export_s"   onclick="sumOutExcel();" function_id="3240407,3270407" >
     <bean:message key="menu.tax.export_s"/>
   </hrms:commandbutton>   
   
   <% if(returnvalue.equals("dxt")){ %>
   <hrms:commandbutton name="returnHome" hint="" functionId="" refresh="true" type="selected" setname="${taxTableForm.tax_tablename}" onclick="hrbreturn('compensation','il_body','taxTableForm');">
     <bean:message key="button.return"/>
   </hrms:commandbutton>
  <% } %>
  
</hrms:dataset>
</logic:notEqual>
<logic:notEqual value="back" name="taxTableForm" property="is_back"> 
<%if(userView.getBosflag()!=null&&userView.getBosflag().equalsIgnoreCase("hl")){ %>
<table id="selectprename"  class="selectPreback" style="top:31px;">
<%}else{ %>
<table id="selectprename"  class="selectPre" style="top:35px;"> 
<%} %>
</logic:notEqual>
<logic:equal value="back" name="taxTableForm" property="is_back">
<table id="selectprename"  class="selectPreback"   >
</logic:equal>
<tr>
<td align="right" valign="middle">

<% if(userView.hasTheFunction("3240409")||userView.hasTheFunction("3270409")){ %>
	&nbsp;数据来源&nbsp; 
	   <html:select name="taxTableForm" property="fromTable" size="1" onchange="query1();" style="vertical-align:middle;">
		   <html:optionsCollection property="fromTableList" value="dataValue" label="dataName"/>
	   </html:select>  
<% }else{ %>
		<iput type='hidden' name='fromTable' value='gz_tax_mx' />
<% } %>

 &nbsp; &nbsp; &nbsp;
<bean:message key="gz.columns.declaredate"/>
        <html:select name="taxTableForm" property="declaredate" size="1" onchange="query();" style="vertical-align:middle;">
        <html:optionsCollection property="datelist" value="dataValue" label="dataName"/>
        </html:select>  
        
        <!-- gby,2015-01-20  -->
&nbsp; &nbsp; &nbsp;
		<html:text property="queryValue" styleClass="text4" name="taxTableForm" style="vertical-align: middle;"></html:text>
        &nbsp; <hrms:commandbutton name="" onclick="query()" function_id="" >查询</hrms:commandbutton> 
      
        </td>
        </tr>
        </table>

</html:form>

<script language="javascript">
 var arrs = document.getElementsByTagName("button"); 
 if(arrs.length!=0){
 	for(var i=0;i<arrs.length;i++){        
 		if (arrs[i].id.indexOf("button")==-1){  
 			var temp = document.getElementById("selectprename");                
 			temp.style.top=8;      
 		} 
 	}
 }else{
  		var temp = document.getElementById("selectprename");              
 		temp.style.top=5;     
 }

 
</script>