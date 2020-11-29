<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.gz.gz_accounting.AcountingForm,java.util.*,com.hjsj.hrms.utils.PubFunc,com.hrms.hjsj.sys.VersionControl,com.hrms.struts.constant.WebConstant,
com.hrms.struts.valueobject.UserView"%>

<script language="Javascript" src="/gz/salary.js"/></script>
<script language="javascript" src="/js/dict.js"></script> 
<%
	AcountingForm accountingForm=(AcountingForm)session.getAttribute("accountingForm"); 
	String subFlag=accountingForm.getSubFlag(); 
	String bedit=accountingForm.getBedit();
	String gz_module=accountingForm.getGz_module();
	String priv_mode =accountingForm.getPriv_mode();
	String isShowManagerFunction=accountingForm.getIsShowManagerFunction();
	String salaryIsSubed=accountingForm.getSalaryIsSubed();
	String isEditData=accountingForm.getIsEditDate();
	String manager=accountingForm.getManager();
	String returnFlag = accountingForm.getReturnFlag();
    String isVisibleitem=accountingForm.getIsVisibleItem();
    String royalty_valid=accountingForm.getRoyalty_valid(); //提成工资
    String isRedo=accountingForm.getIsRedo();
   
    String sp_actor_str=accountingForm.getSp_actor_str();
    String sp_actor_name=accountingForm.getSpActorName();
    String appealName="报批";
    if(sp_actor_name.length()>0)
    	appealName="报["+sp_actor_name+"]审批";
    
	//String 
	String gzb_str="menu.gz.table";
	String gzff_str="menu.gz.extend";
	String gzrp_str="menu.gz.report";
	String gzqr_str="button.submit";  //"menu.gz.ok";
	String gzcf="label.gz.gzRedo";
	if(gz_module.equals("1"))
	{
		gzb_str="menu.ins.table";
		gzff_str="menu.ins.extend";
		gzrp_str="menu.ins.report";
		gzcf="label.gz.insRedo";
	//	gzqr_str="menu.gz.ok2";
	}
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
	
	String _sql=PubFunc.decrypt(accountingForm.getSql());
	
%>

<html>
<body onload='resetleft()'  >
<html:form action="/gz/gz_accounting/gz_table">
<script language='javascript'>
	var appflag='${accountingForm.appflag}'
    var gztablename='${accountingForm.gz_tablename}';
    var salaryid='${accountingForm.salaryid}';
	var a_code='<%=(request.getParameter("a_code"))%>';
	var isNotSpFlag2Records='${accountingForm.isNotSpFlag2Records}';
	var verify_ctrl='${accountingForm.verify_ctrl}';
	var subNoShowUpdateFashion='${accountingForm.subNoShowUpdateFashion}';
	var isHistory='${accountingForm.isHistory}';
	var gz_module='${accountingForm.gz_module}';
	var isTotalControl='${accountingForm.isTotalControl}';
	var sp_actor_str='${accountingForm.sp_actor_str}';
	var isRedo='${accountingForm.isRedo}';
	
	function gzReport()
	{
		    var arguments=new Array();     
		    var strurl="/gz/gz_accounting/report.do?b_query=link`model=0`salaryid=${accountingForm.salaryid}`a_code=${accountingForm.a_code}`gz_module=${accountingForm.gz_module}`condid=${accountingForm.condid}";
		    var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
		    
		    var flag=window.showModalDialog(iframe_url,arguments,"dialogWidth=500px;dialogHeight=330px;resizable=yes;scroll=no;status=no;");  
			parent.mil_menu.location=parent.mil_menu.location+"?b_opt=1";
	}

	
	var prv_filter_id="${accountingForm.condid}";
	
	
	
	var prv_project_id="${accountingForm.itemid}";
	
	
	
	<logic:equal name="accountingForm" property="salaryIsSubed"  value="true">
	window.status=HAS_BEAN_SUBMIT+"(${accountingForm.ff_setname} ${accountingForm.ff_bosdate})";
	</logic:equal>
	<logic:equal name="accountingForm" property="salaryIsSubed"  value="false">
	window.status=DID_NOT_SUBMIT+"(${accountingForm.ff_setname} ${accountingForm.ff_bosdate})";
	</logic:equal>
	
	document.body.onbeforeunload=function(){ 
		window.status='';
	}
function selectMusterName(gz_module)
{
   var nFlag="4";
   if(gz_module=="1")
      nFlag="1";
   var strurl="/general/muster/hmuster/searchHroster.do?b_search=link`nFlag="+nFlag+"`a_inforkind=1`result=0`dbpre=Usr`closeWindow=2";
   var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
   var flag=window.showModalDialog(iframe_url,arguments,"dialogWidth="+window.screen.width+"px;dialogHeight="+window.screen.height+"px;resizable=yes;scroll=no;status=no;");  
}

function verifyFormula(salaryid,aisAppealData,auserid)
{
		 var hashvo=new ParameterSet();
	     hashvo.setValue("a_code","${accountingForm.a_code}");
	     hashvo.setValue("condid",accountingForm.condid.value);
	     hashvo.setValue("salaryid",salaryid);
	     hashvo.setValue("type","0");
	     hashvo.setValue("aisAppealData",aisAppealData);
	     hashvo.setValue("auserid",auserid);
	     var sql = accountingForm.filterWhl.value;
  		 hashvo.setValue("reportSQL",sql);
	     var request=new Request({asynchronous:false,onSuccess:check_ok2,functionId:'3020070016'},hashvo);	
}

function selectFormula(salaryid)
{
   var hashvo=new ParameterSet();
   hashvo.setValue("a_code","${accountingForm.a_code}");
   hashvo.setValue("condid",accountingForm.condid.value);
   hashvo.setValue("salaryid",salaryid);
   hashvo.setValue("type","0");
   var sql = accountingForm.filterWhl.value;
   hashvo.setValue("reportSQL",sql);
   var request=new Request({asynchronous:false,onSuccess:check_ok,functionId:'3020070016'},hashvo);	
   
   //var strurl="/gz/gz_accounting/sh_formula.do?b_query=link`salaryid="+salaryid+"`condid="+accountingForm.condid.value+"`a_code=${accountingForm.a_code}`type=0";
  // var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
   //var flag=window.showModalDialog(iframe_url,arguments,"dialogWidth=400px;dialogHeight=400px;resizable=yes;scroll=no;status=no;");  
   
}
function check_ok(outparameters)
{
  var msg=outparameters.getValue("msg");
  if(msg=='0')
  {
    alert("审核公式未定义!");
    return;
  }
  if(msg=='no')
  {
     alert("审核完毕！");
     return;
  }
  else{
     var filename=outparameters.getValue("fileName");
     var fieldName = getDecodeStr(filename);
     var win=open("/servlet/vfsservlet?fileid="+fieldName+"&fromjavafolder=true","excel");
  }
}
function dygs()
{
  var salaryid="${accountingForm.salaryid}";
  var strurl="/gz/templateset/spformula/sp_formula.do?b_query=link`returnType=1`opt=0`salaryid="+salaryid;
  var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
  var flag=window.showModalDialog(iframe_url,arguments,"dialogWidth=800px;dialogHeight=490px;resizable=yes;scroll=no;status=no;");  
}

var urlStr = "&returnFlag=${accountingForm.returnFlag}&theyear=${accountingForm.theyear}&themonth=${accountingForm.themonth}&operOrg=${accountingForm.operOrg}";

var queryhidden=0;
function visiblequery(){
   if(queryhidden==0)
   {
      var queryblank=document.getElementById("queryblank");
      if(queryblank)
          queryblank.style.display="block";
      var querydata=document.getElementById("querydata");
      if(querydata)
          querydata.style.display="block";
      queryhidden=1;
      var obj=document.getElementById("querydesc");
      obj.innerHTML="[&nbsp;<a href=\"javascript:visiblequery();\" >查询隐藏&nbsp;</a>]&nbsp;&nbsp;&nbsp;";
   }
   else
   {
       var queryblank=document.getElementById("queryblank");
       if(queryblank)
           queryblank.style.display="none";
      var querydata=document.getElementById("querydata");
      if(querydata)
          querydata.style.display="none";
      queryhidden=1;
      var obj=document.getElementById("querydesc");
      obj.innerHTML="[&nbsp;<a href=\"javascript:visiblequery();\" >查询显示&nbsp;</a>]&nbsp;&nbsp;&nbsp;";
       queryhidden="0";
   }
}
function queryR()
{
     document.getElementById("empfiltersql").value="";
	 accountingForm.action="/gz/gz_accounting/gz_table.do?b_query=link&query=1&salaryid="+salaryid;
	 accountingForm.submit(); 
}

</script>
	<div id='wait' style='position:absolute;top:120;left:300;display:none;'>
		<table border="1" width="100" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td id='wait_desc' class="td_style" height=24>
					<bean:message key="label.gz.submitData"/>......
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee class="marquee_style"  direction="right" width="300" scrollamount="5" scrolldelay="10"  >
						<table cellspacing="1" cellpadding="0">
							<tr height=8>
								<td bgcolor=#3399FF  width=8></td>
								<td></td>
								<td bgcolor=#3399FF  width=8></td>
								<td></td>
								<td bgcolor=#3399FF  width=8></td>
								<td></td>
								<td bgcolor=#3399FF  width=8></td>
								<td></td>
							</tr>
						</table>
					</marquee>
				</td>
			</tr>
		</table>
		<iframe src="javascript:false" style="position:absolute; visibility:inherit; top:0px; left:0px;width:315; height:87; 					    	
			   			 				z-index:-1;filter='progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)';"></iframe>	
	</div>
<%if("hl".equals(hcmflag)){ %>
	<table><tr><td>
<%}else{ %>
	<table style="margin-top:-5px"><tr><td>
<%} %>

<table><tr><td>
<hrms:menubar menu="menu1" id="menubar1">
  <hrms:menuitem name="gz0" label="<%=gzb_str%>"  function_id="3240203,3250203,3270203,3271203" >
    <logic:equal name="accountingForm" property="isShowManagerFunction"  value="1">
    <hrms:menuitem name="m1" label="menu.gz.new" icon="/images/prop_ps.gif" url="create_gz_table('${accountingForm.salaryid}')"  function_id="324020301,325020301,327020301,327120301" /> 
    </logic:equal>
    <hrms:menuitem name="m2" label="menu.gz.import" icon="/images/import.gif" url="importTable('${accountingForm.salaryid}')"  function_id="324020302,325020302,327020302,327120302" />   
    <hrms:menuitem name="m3" label="menu.gz.export" icon="/images/export.gif" url="exportTable('${accountingForm.salaryid}')"  function_id="324020303,325020303,327020303,327120303" /> 
    <hrms:menuitem name="m4" label="button.download.template" icon="/images/export.gif" url="downLoadTemp('${accountingForm.salaryid}')"  function_id="324020304,325020304,327020304,327120304"/> 
	<hrms:menuitem name="m5" label="import.tempData" icon="/images/import.gif" url="exportTempData('${accountingForm.salaryid}','fafang')"  function_id="324020305,325020305,327020305,327120305" /> 
  </hrms:menuitem>
  <hrms:menuitem name="gz1" label="menu.gz.edit" ><!-- 编辑 -->
  	 
   <% if(!royalty_valid.equals("1")){ %>
    <hrms:menuitem name="m1_chg" label="menu.gz.changinfo" icon="/images/edit.gif" url="chginfo_bd('${accountingForm.salaryid}','${accountingForm.gz_module}');" command=""   function_id="3240201,3250201,3270201,3271201" enabled="${accountingForm.isEditDate}" />  
    <%  } %>
   
    <%if(priv_mode.equals("1")){%>
    <hrms:menuitem name="m2" label="menu.gz.numericcompare" icon="/images/edit.gif" url="compare('${accountingForm.salaryid}','${accountingForm.gz_module}','${accountingForm.flow_flag}');" command=""   function_id="3240202,3250202,3270202,3271202" enabled="${accountingForm.isEditDate}" /> 
    <%} %>
    
    <% if(!royalty_valid.equals("1")){ %>
    <hrms:menuitem name="m4" label="label.gz.importMen"  function_id="3240212,3250212,3270212,3271212"     >
    	<hrms:menuitem name="m41" label="label.gz.handimport"  url="hand_importMen('${accountingForm.gz_tablename}','${accountingForm.nbase}','${accountingForm.salaryid}','${accountingForm.isSalaryManager}')"  />
    	<%if(isVisibleitem.equals("1")){ %>
    	<hrms:menuitem name="m43" label="label.gz.importUn_Um"   url="importChangeMen('${accountingForm.salaryid}','${accountingForm.isSalaryManager}');" />  
    	<%} %>
    </hrms:menuitem> 
     <% } %>
    <hrms:menuitem name="m7" label="label.gz.lookitemsum" icon="" url='itemsSum("${accountingForm.salaryid}");' command="" enabled="true" visible="true"   />
    <hrms:menuitem name="deletem3" label="menu.gz.delete" icon="/images/del.gif" url="" command="delselected" hint="general.inform.search.confirmed.del"  function_id="3240207,3250207,3270207,3271207" enabled="${accountingForm.isEditDate}"/>  
 <logic:notEqual name="accountingForm" property="isImportBonus"  value="0">
    <hrms:menuitem name="m5" label="menu.item.importbonus" icon="/images/import.gif" url="importBonus()"  function_id="3240214" />   
 </logic:notEqual>
 <logic:notEqual name="accountingForm" property="isImportPiece"  value="0">
    <%
	VersionControl vc = new VersionControl();
	if(vc.searchFunctionId("32421")){ 
	%>
    <hrms:menuitem name="mnPieceRate" label="引入计件薪资" icon="/images/import.gif" url="importPiece()" function_id="3242112"  />
  	<%}
  	%>    
 </logic:notEqual>
  </hrms:menuitem>  
  <hrms:menuitem name="gz2" label="menu.gz.batch" function_id="3240204,3250204,3270204,3271204" >
   <% if(!royalty_valid.equals("1")){ %>
      <hrms:menuitem name="mitem1" label="menu.gz.batch.import" icon="/images/goto_input.gif" url="batch_import('${accountingForm.salaryid}','${accountingForm.gz_module}')" command="" enabled="${accountingForm.isEditDate}" visible="true"  function_id="324020401,325020401,327020401,327120401"  />
   <% } %>  
      <hrms:menuitem name="mitem2" label="menu.gz.batch.update" icon="/images/edit.gif" url="batch_update('${accountingForm.salaryid}','${accountingForm.gz_module}')" command="" enabled="${accountingForm.isEditDate}" visible="true"  function_id="324020402,325020402,327020402,327120402"  />
      <hrms:menuitem name="mitem3" label="menu.gz.batch.compute" icon="/images/compute.gif" url="get_formula('${accountingForm.salaryid}')" command="" enabled="${accountingForm.isEditDate}" visible="true"   function_id="324020403,325020403,327020403,327120403" />

  </hrms:menuitem>  
  
  <hrms:menuitem name="gz3" label="menu.gz.view">
      <hrms:menuitem name="mitem1" label="menu.gz.itemfilter" icon="/images/groups.gif" url="to_project_filter('${accountingForm.salaryid}')" command="" enabled="true" visible="true"/>
      <hrms:menuitem name="mitem2" label="menu.gz.hide"   function_id="3240210,3250210,3270210,3271210"    icon="/images/write.gif" url="gzPayrollViewHide('${accountingForm.salaryid}','${accountingForm.flag}');" command="" enabled="true" visible="true"/>
      <hrms:menuitem name="mitem3" label="menu.gz.sortitem" icon=""  function_id="3240209,3250209,3270209,3271209"   url="gzPayrollSort('${accountingForm.salaryid}');" command="" enabled="true" visible="true"/>
      <hrms:menuitem name="mitem4" label="menu.gz.manfilter" icon=""   url="to_gz_person_filter('${accountingForm.salaryid}','${accountingForm.gz_tablename}')" command="" enabled="true" visible="true"/>
      <hrms:menuitem name="mitem5" label="infor.menu.msort" icon="/images/sort.gif" url="to_sort_emp1('${accountingForm.salaryid}');" command="" enabled="true" visible="true"/>
      <hrms:menuitem name="mitem6" label="menu.gz.sortman" function_id="3240216,3240316,3250216,3270216,3271216" icon="/images/sort.gif" url="syncgzemp('${accountingForm.salaryid}')" command="" enabled="true" visible="true"/>
  </hrms:menuitem>  
  <hrms:menuitem name="gz4" label="<%=gzff_str%>" function_id="3240205,3250205,3270205,3271205" >
  <% if(!gz_module.equals("1")){ %>
      <hrms:menuitem name="mitem1" label="menu.gz.moneylist" icon="/images/add_del.gif" url='cashlist_cashList("${accountingForm.salaryid}","${accountingForm.priv}");' command="" enabled="true" visible="true"   function_id="324020501,327020501"  />
      <hrms:menuitem name="mitem2" label="menu.gz.updisk" icon="/images/write.gif" url='bankdisk_bankDisk("${accountingForm.a_code}","${accountingForm.gz_tablename}","${accountingForm.salaryid}");' command="" enabled="true" visible="true" function_id="324020502,325020502,327020502,327120502"   />
   <% } %>  
      <hrms:menuitem name="mitem3" label="<%=gzrp_str%>" icon="" url="gzReport()" command="" enabled="true" visible="true"  function_id="324020503,325020503,327020503,327120503"  />
<logic:equal name="accountingForm" property="isShowManagerFunction"  value="1">

	  
<% if(bedit.equals("true")){ %>
      <hrms:menuitem name="mitem44" label="<%=gzqr_str%>"    icon="" url="open_submit_dialog('${accountingForm.salaryid}','${accountingForm.gz_module}');" command=""   function_id="324020504,325020504,327020504,327120504"   />
<% } %>
</logic:equal>

      <hrms:menuitem name="mitem5" label="menu.gz.sendmessage" icon="" url='searchTemplate("${accountingForm.salaryid}","${accountingForm.a_code}","1","${accountingForm.order_by}");' command="" enabled="true" visible="true"  function_id="324020505,325020505,327020505,327120505"  />
  <% if(!gz_module.equals("1")){ %>
      <hrms:menuitem name="mitem6" label="menu.gz.tax" icon="" url="show_tax_mx('${accountingForm.salaryid}');" command="" enabled="true" visible="true"  function_id="32404,32704"  />
  <% } %>
  </hrms:menuitem>  
  <hrms:menuitem name="gz5" label="menu.gz.options"  function_id="3240206,3250206,3270206,3271206"  >
      <logic:equal name="accountingForm" property="isShowManagerFunction"  value="1">
	      <hrms:menuitem name="m1" label="menu.gz.appdate" icon="/images/waiting.gif" url='setapp_date();' command="" enabled="true" visible="true"/>
	  </logic:equal>
	  
	  
	  <%  if(isShowManagerFunction.equals("1")&&isRedo.equals("0")){ %>
	      <hrms:menuitem name="m2" label="menu.gz.reappdate" icon="/images/waiting.gif" url="reset_gz_date('${accountingForm.salaryid}','${accountingForm.salaryIsSubed}');" command="" enabled="true" visible="true"  function_id="324020601,325020601,327020601,327120601"  />
	      <hrms:menuitem name="m3" label="<%=gzcf%>" icon="" url="reDoGz('${accountingForm.salaryid}','${accountingForm.salaryIsSubed}');" command="" enabled="true" visible="true"  function_id="324020602,325020602,327020602,327120602"  />
	  <% } %>
	 
      <hrms:menuitem name="m4" label="menu.gz.formula" icon="" url='condFormula("${accountingForm.salaryid}");' command="" enabled="true" visible="true"  function_id="324020603,325020603,327020603,327120603" />
      <hrms:menuitem name="m5" label="menu.gz.template" icon="" url="addEmailTemplate();" command="" enabled="true" visible="true"  function_id="324020604,325020604,327020604,327120604" />
      	<hrms:menuitem name="m6" label="infor.menu.definition.shformula" icon="" url="dygs();" command="" enabled="true" visible="true"  function_id="324020605,325020605,327020605,327120605" />
  </hrms:menuitem>  
</hrms:menubar>
</td></tr></table>
</td>
<td>&nbsp;                         
</td>
</tr>
<tr id="queryblank" style="display=none" height="25">

</tr>
<tr id="querydata" style="display=none" >
<td align="left" width="100%">
<table ellspacing="0" cellpadding="0" width="100%">
<tr>
<% int i=0; %>
<td align="left">
<logic:iterate id="element" name="accountingForm" property="queryFieldList" indexId="index">

<bean:write name="element" property="itemdesc"/>:
 <logic:equal name="element" property="itemtype" value="D">
 <html:text name="accountingForm" property='<%="queryFieldList["+i+"].value"%>'  size="20"  onclick='popUpCalendar(this,this, dateFormat,\"\",\"\",true,false)' styleClass="inputtext"/>                                            
  </logic:equal>
                          <!--字符型 -->                                                    
                          <logic:equal name="element" property="itemtype" value="A">
                              <logic:notEqual name="element" property="codesetid" value="0">
                              <html:hidden name="accountingForm" property='<%="queryFieldList["+i+"].value"%>'/>
                                 <html:text name="accountingForm" property='<%="queryFieldList["+i+"].viewvalue"%>'  size="20"  onchange="fieldcode(this,1)"  styleClass="inputtext"/>         
                                <img src="/images/code.gif" onclick='openCondCodeDialog("${element.codesetid}","<%="queryFieldList["+i+"].viewvalue"%>");'/>
                              </logic:notEqual>               
                              <logic:equal name="element" property="codesetid" value="0">
                              <html:text name="accountingForm" property='<%="queryFieldList["+i+"].value"%>' size="20"  styleClass="inputtext"/>                                 
                              </logic:equal>                                                         
                          </logic:equal> 
                          <!--数据值-->                            
                          <logic:equal name="element" property="itemtype" value="N">
                            <html:text name="accountingForm" property='<%="queryFieldList["+i+"].value"%>' size="20" styleClass="inputtext"/>                      
                                               
                          </logic:equal>    
                          <!--备注型--> 
                          <logic:equal name="element" property="itemtype" value="M">
                           
                            <html:text name="accountingForm" property='<%="queryFieldList["+i+"].value"%>' size="20"  styleClass="inputtext"/>                      
                                                
                          </logic:equal>                   

  
<%i++; %>
</logic:iterate>
 
<input type="button" name="query" value="查询" class="mybutton" onclick="queryR();"/>&nbsp;&nbsp;&nbsp;
</td>
</tr>


</table>
</td>
</tr>
</table>

<% 
if(isShowManagerFunction.equals("0")){
%>

<hrms:dataset name="accountingForm" property="fieldlist" scope="session" setname="${accountingForm.gz_tablename}" 
pagerows="${accountingForm.pagerows}" setalias="gz_table" readonly="false" rowlock="true"  rowlockfield="sp_flag2"    rowlockvalues=",01,07,"   
editable="true" select="true" sql="<%=_sql%>" buttons="bottom">
 <% if(isShowManagerFunction.equals("1")){ %>
   <hrms:commandbutton name="table" hint="workdiary.message.creat.pay.table" function_id="324020301,325020301,327020301,327120301" refresh="true" type="selected" setname="${accountingForm.gz_tablename}" onclick="create_gz_table('${accountingForm.salaryid}')">
     <bean:message key="menu.gz.new"/>
   </hrms:commandbutton>
<% } 
	if(isEditData.equals("true")){
	%>
    <hrms:commandbutton name="delselected" hint="general.inform.search.confirmed.del"  functionId="3020070002" function_id="3240207,3250207,3270207,3271207" refresh="true" type="selected" setname="${accountingForm.gz_tablename}" >
     <bean:message key="button.delete"/>
   </hrms:commandbutton>
    <hrms:commandbutton name="savedata"  functionId="3020070003" function_id="3240202,3250202,3270202,3271202" refresh="false" type="all-change" setname="${accountingForm.gz_tablename}" >
     <bean:message key="button.save"/>
   </hrms:commandbutton>    
   <hrms:commandbutton name="compute" functionId="" function_id="324020403,325020403,327020403,327120403" onclick="get_formula('${accountingForm.salaryid}')" refresh="true" type="selected" setname="${accountingForm.gz_tablename}" >
     <bean:message key="button.computer"/>
   </hrms:commandbutton>  
   
   <hrms:commandbutton name="personalcompute"  onclick="validateFormula('${accountingForm.salaryid}')"   function_id="3250211,3240211,3270211,3271211"  refresh="true" type="selected" setname="${accountingForm.gz_tablename}" >
     <bean:message key="menu.gz.personalcompute"/>
   </hrms:commandbutton>
		<%
 	}
  if(isShowManagerFunction.equals("1")){ %>
   <hrms:commandbutton name="appeal" hint="performance.workdiary.approval.ok" function_id="3240208,3250208,3270208,3271208" refresh="true" visible="${accountingForm.appflag}" type="selected" setname="${accountingForm.gz_tablename}" onclick="appealTotalControl('${accountingForm.userid}','${accountingForm.isAppealData}','${accountingForm.salaryid}')" >
    	<%=appealName%>
   </hrms:commandbutton>     
<% 		if(bedit.equals("true")){ %>
   <hrms:commandbutton name="submit" hint="workdiary.message.submit.ok" function_id="324020504,325020504,327020504,327120504" refresh="true"  type="selected"   setname="${accountingForm.gz_tablename}" onclick="open_submit_dialog('${accountingForm.salaryid}','${accountingForm.gz_module}');">
    	 <bean:message key="button.submit"/>
   </hrms:commandbutton>   
<% 		}
	}
	

%>		
   <hrms:commandbutton name="appeal2" hint="general.inform.search.confirmed.appeal" function_id="3240225,3250225,3270225,3271225" refresh="true"  type="selected" setname="${accountingForm.gz_tablename}" onclick="report('2')" >
    	报审
   </hrms:commandbutton>   

  
   <hrms:commandbutton name="sendmail"  function_id="324020505,325020505,327020505,327120505" onclick='searchTemplate("${accountingForm.salaryid}","${accountingForm.a_code}","1","${accountingForm.order_by}");' refresh="true" type="selected" setname="${accountingForm.gz_tablename}" >
     <bean:message key="menu.gz.sendmessage"/>
   </hrms:commandbutton>  
   
    <hrms:commandbutton name="sh_formula"  onclick="selectFormula('${accountingForm.salaryid}')"   function_id="3240309,3250309,3240213"  refresh="true" type="selected" setname="${accountingForm.gz_tablename}" >
   <bean:message key="button.audit"/>
   </hrms:commandbutton>
   
   
    <hrms:commandbutton name="showstate"  onclick="showstate()"   function_id="3250220,3240220"  refresh="true" type="selected" setname="${accountingForm.gz_tablename}" >
  				 <bean:message key="menu.gz.state"/>
   	</hrms:commandbutton>
   	<hrms:commandbutton name="showprocess"  onclick="showprocess('ff','${accountingForm.gz_tablename}')"   function_id="3250221,3240221"  refresh="true" type="selected" setname="${accountingForm.gz_tablename}" >
  			 <bean:message key="menu.gz.process"/>
  	</hrms:commandbutton>
  
   <hrms:commandbutton name="reimport"  onclick="reImport_ff('${accountingForm.salaryid}','${accountingForm.gz_module}')"   function_id="3240224,3250224"  refresh="true" type="selected" setname="${accountingForm.gz_tablename}" >
  			重新导入
  	</hrms:commandbutton>
  
   <%if(returnFlag.equals("0")){ %>
   <hrms:commandbutton name="goback1"  onclick="goback()" refresh="true" type="selected" setname="${accountingForm.gz_tablename}" >
     <bean:message key="button.return"/>
   </hrms:commandbutton>    
   <%}else if(returnFlag.equals("1")) {%>
   <hrms:commandbutton name="goback2"  onclick="go_back2('${accountingForm.theyear}','${accountingForm.themonth}','${accountingForm.operOrg}')" refresh="true" type="selected" setname="${accountingForm.gz_tablename}" >
     	<bean:message key="button.return"/>
   </hrms:commandbutton>  
    <%}%> 
</hrms:dataset>

<%
}
else
{
%>

<hrms:dataset name="accountingForm" property="fieldlist" scope="session" 
  setname="${accountingForm.gz_tablename}" setalias="gz_table" pagerows="${accountingForm.pagerows}"
 readonly="false" editable="true" select="true" 
 sql="<%=_sql%>"  rowlock="true"  rowlockfield="sp_flag"   
  rowlockvalues='${accountingForm.gzRowCanEditStatus}'     buttons="bottom">
 <% if(isShowManagerFunction.equals("1")){ %>
   <hrms:commandbutton name="table" hint="workdiary.message.creat.pay.table" function_id="324020301,325020301,327020301,327120301" refresh="true" type="selected" setname="${accountingForm.gz_tablename}" onclick="create_gz_table('${accountingForm.salaryid}')">
     <bean:message key="menu.gz.new"/>
   </hrms:commandbutton>
<% } 
	if(isEditData.equals("true")){
	%>
    <hrms:commandbutton name="delselected" hint="general.inform.search.confirmed.del"  functionId="3020070002" function_id="3240207,3250207,3270207,3271207" refresh="true" type="selected" setname="${accountingForm.gz_tablename}" >
     <bean:message key="button.delete"/>
   </hrms:commandbutton>
    <hrms:commandbutton name="savedata"  functionId="3020070003" function_id="3240202,3250202,3270202,3271202" refresh="false" type="all-change" setname="${accountingForm.gz_tablename}" >
     <bean:message key="button.save"/>
   </hrms:commandbutton>    
   <hrms:commandbutton name="compute" functionId="" function_id="324020403,325020403,327020403,327120403" onclick="get_formula('${accountingForm.salaryid}')" refresh="true" type="selected" setname="${accountingForm.gz_tablename}" >
     <bean:message key="button.computer"/>
   </hrms:commandbutton>  
   
   <hrms:commandbutton name="personalcompute"  onclick="validateFormula('${accountingForm.salaryid}')"   function_id="3250211,3240211,3270211,3271211"  refresh="true" type="selected" setname="${accountingForm.gz_tablename}" >
     <bean:message key="menu.gz.personalcompute"/>
   </hrms:commandbutton>
		<%
 	}
  if(isShowManagerFunction.equals("1")){ %>
   <hrms:commandbutton name="appeal" hint="performance.workdiary.approval.ok" function_id="3240208,3250208,3270208,3271208" refresh="true" visible="${accountingForm.appflag}" type="selected" setname="${accountingForm.gz_tablename}" onclick="appealTotalControl('${accountingForm.userid}','${accountingForm.isAppealData}','${accountingForm.salaryid}')" >
    	<%=appealName%>
   </hrms:commandbutton>     
<% 		if(bedit.equals("true")){ %>
   <hrms:commandbutton name="submit" hint="workdiary.message.submit.ok" function_id="324020504,325020504,327020504,327120504" refresh="true"  type="selected"   setname="${accountingForm.gz_tablename}" onclick="open_submit_dialog('${accountingForm.salaryid}','${accountingForm.gz_module}');">
    	 <bean:message key="button.submit"/>
   </hrms:commandbutton>   
<% 		} 
	}
	
	if(manager!=null&&manager.length()>0)
 	{
%>
   <hrms:commandbutton name="reject" hint="general.inform.search.confirmed.rejectSelectedRecords" function_id="" refresh="true" visible="true" type="selected" setname="${accountingForm.gz_tablename}" onclick="report('1')" >
    	驳回
   </hrms:commandbutton>   
<%
	}
%>
	
  
   <hrms:commandbutton name="sendmail"  function_id="324020505,325020505,327020505,327120505" onclick='searchTemplate("${accountingForm.salaryid}","${accountingForm.a_code}","1","${accountingForm.order_by}");' refresh="true" type="selected" setname="${accountingForm.gz_tablename}" >
     <bean:message key="menu.gz.sendmessage"/>
   </hrms:commandbutton>  
   
    <hrms:commandbutton name="sh_formula"  onclick="selectFormula('${accountingForm.salaryid}')"   function_id="3240309,3250309,3240213"  refresh="true" type="selected" setname="${accountingForm.gz_tablename}" >
   <bean:message key="button.audit"/>
   </hrms:commandbutton>
   
   <hrms:commandbutton name="showstate"  onclick="showstate()"   function_id="3250220,3240220"  refresh="true" type="selected" setname="${accountingForm.gz_tablename}" >
  				 <bean:message key="menu.gz.state"/>
   	</hrms:commandbutton>
   	<hrms:commandbutton name="showprocess"  onclick="showprocess('ff','${accountingForm.gz_tablename}')"   function_id="3250221,3240221"  refresh="true" type="selected" setname="${accountingForm.gz_tablename}" >
  			 <bean:message key="menu.gz.process"/>
  	</hrms:commandbutton>
 
   <hrms:commandbutton name="reimport"  onclick="reImport_ff('${accountingForm.salaryid}','${accountingForm.gz_module}')"   function_id="3240224,3250224"  refresh="true" type="selected" setname="${accountingForm.gz_tablename}" >
  			重新导入
  	</hrms:commandbutton>
  
   <%if(returnFlag.equals("0")){ %>
   <hrms:commandbutton name="goback1"  onclick="goback()" refresh="true" type="selected" setname="${accountingForm.gz_tablename}" >
     <bean:message key="button.return"/>
   </hrms:commandbutton>    
   <%}else if(returnFlag.equals("1")) {%>
   <hrms:commandbutton name="goback2"  onclick="go_back2('${accountingForm.theyear}','${accountingForm.themonth}','${accountingForm.operOrg}')" refresh="true" type="selected" setname="${accountingForm.gz_tablename}" >
     	<bean:message key="button.return"/>
   </hrms:commandbutton>  
    <%}%>   
     
</hrms:dataset>

<% } %>




<html:hidden name="accountingForm" property="sort_table_detail" />
<html:hidden name="accountingForm" property="empfiltersql" />
<html:hidden name="accountingForm" property="proright_str" /> 
<html:hidden name="accountingForm" property="filterWhl" />
<html:hidden name="accountingForm" property="flag"/>
<html:hidden name="accountingForm" property="order_by" /> 
<html:hidden name="accountingForm" property="sql" />  
<Input type='hidden' name='rejectCause' value='' />
<input type='hidden' name='selectGzRecords' value='' />
<input type='hidden' name='approveObject' value='' />
<input type='hidden' name='cond_id_str' value=''/>
<input type="hidden" name="model" value="0" id="gm"/>

<%if("hl".equals(hcmflag)){ %>
<table id="selectprename"  style="position:absolute;left:700px;top:31px;z-index:10;"><tr>
<%}else{ %>
<table id="selectprename"  style="position:absolute;left:700px;top:36px;z-index:10;"><tr>
<%} %>
<td id="querydesc" nowrap>
[&nbsp;<a href="javascript:visiblequery();" >查询显示&nbsp;</a>]&nbsp;&nbsp;&nbsp;
</td>
<td nowrap ><bean:message key="label.gz.itemfilter"/></td>
<td>
<html:select name="accountingForm" styleId="projectFilter" property="itemid" size="1" onchange="search_gz_data_byitem('${accountingForm.salaryid}',this,'0');">
   <html:optionsCollection property="itemlist" value="dataValue" label="dataName"/>
</html:select> 
</td><td  nowrap ><bean:message key="label.gz.condfilter"/> </td>
<td>
<html:select name="accountingForm" property="condid" size="1" onchange="search_gz_data_bycond('${accountingForm.salaryid}',this,'${accountingForm.gz_tablename}');">
   <html:optionsCollection property="condlist" value="dataValue" label="dataName"/>
</html:select>  
</td></tr></table>



</html:form>
<script language="javascript">

  function isEdit()
  {
  		var _dataset=${accountingForm.gz_tablename};
  		var record=_dataset.firstUnit; 
  		while (record) 
		{
					if (record.recordState == "insert" ||
						record.recordState == "modify" ||
						record.recordState == "delete") {
						return true;
					}
					record=record.nextUnit;
		}
		return false;
  }

  function goback()
  {
  		
  		if(isEdit())
  		{
  			alert("数据已被更改，请执行保存操作!");
  			return;
  		}
		accountingForm.target="il_body";
		accountingForm.action="/gz/gz_accounting/gz_set_list.do?b_query=link2";
		accountingForm.submit();
  }
	

   function ${accountingForm.gz_tablename}_afterChange(dataset,field)
   {
   	  var field_name=field.getName();
   	  var record;
   	  var a0100;
   	  
   	  if(field_name!='select')
   	  		dataset.getCurrent().recordState="modify";
   	  		
   	  if(field_name=="A00Z1")
   	  {
   	  	record=dataset.getCurrent(); 
   	  	var newvalue=record.getValue(field_name);
   	  	var oldvalue=record.getOldValue(field_name);
   	  }
   	  if(field_name=="A00Z0")
   	  {
   	  	record=dataset.getCurrent(); 
   	  	var newvalue=record.getValue(field_name);
   	  	var oldvalue=record.getOldValue(field_name);		 
   	  }   
   	  if(field_name=="A00Z1"||field_name=="A00Z0")
   	  {
	   	   var hashVo=new ParameterSet();
		   hashVo.setValue("field_name",field_name);
		    if(field_name=="A00Z0")
		   { 
   		      hashVo.setValue("newvalue",newvalue*1);
		      hashVo.setValue("oldvalue",oldvalue*1);
		  	  hashVo.setValue("A00Z1",record.getValue("A00Z1"));
		   }
		   else
		   {
		   	  hashVo.setValue("newvalue",newvalue);
		      hashVo.setValue("oldvalue",oldvalue);
		   	  hashVo.setValue("A00Z0",record.getValue("A00Z0")*1);
		   }
		   hashVo.setValue("NBASE",record.getValue("NBASE"));	    
		   hashVo.setValue("A0100",record.getValue("A0100"));
		   hashVo.setValue("salaryid","${accountingForm.salaryid}");
		   var request=new Request({method:'post',asynchronous:false,onSuccess:changeOk,functionId:'3020100022'},hashVo);			
	  }  
   }
   /*
   var menuitem=getMenuItem("mitem4");
   alert(menuitem.getLabel());
   menuitem.enabled=false;
   menuitem.label="aaaa";
   */
   function changeOk(outparameters){
   
   }
   
   function resetleft()
   {
   		var a_obj;
   		 <%if(returnFlag.equals("0")){ %>
   		 a_obj=document.getElementById('buttongoback1');
   		 <%}else if(returnFlag.equals("1")) {%>
   		 a_obj=document.getElementById('buttongoback2');
   		 <% } %>
   		if(a_obj)
   		{
	   		oRect=a_obj.getBoundingClientRect();   
	   		var a_value=oRect.left;
	   		if(a_value<430)
	   			a_value=430;
	        document.getElementById('selectprename').style.left=a_value+50;
	    }
   }
</script>
</body>
</html>