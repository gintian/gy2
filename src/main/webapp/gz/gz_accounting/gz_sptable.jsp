<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@page import="com.hrms.hjsj.sys.VersionControl"%>
<%@ page import="com.hjsj.hrms.actionform.gz.gz_accounting.AcountingForm" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>

<script language="JavaScript" src="/gz/salary.js" type="text/javascript"></script>
<script language="javascript" src="/js/dict.js"></script> 
<%
    VersionControl ver = new VersionControl();
	AcountingForm acountingForm=(AcountingForm)session.getAttribute("accountingForm");
	String bedit=acountingForm.getBedit();
	String sp_ori=acountingForm.getSp_ori();
	String gz_module=acountingForm.getGz_module();
	String gzrp_str="menu.gz.report";
	String salaryid = acountingForm.getSalaryid();
	if(gz_module.equals("1"))
	{
	   gzrp_str="menu.ins.report";
	}
	 UserView userView = (UserView) request.getSession().getAttribute(
	WebConstant.userView);
	String username = userView.getUserName();
	 String returnflag="menu";
	 if(acountingForm.getReturnflag()!=null)
	 	returnflag=acountingForm.getReturnflag();
    String url="/system/home.do?b_query=link";
    String target="i_body";
    String tar=userView.getBosflag();
    if(tar.equals("hl4"))
  	  target="il_body";
  
   // if(request.getParameter("ver")!=null&&request.getParameter("ver").equals("5"))
    if(tar.equalsIgnoreCase("hl"))
    {
   		url="/templates/index/portal.do?b_query=link";
   		target="il_body";
    }else if(tar.equalsIgnoreCase("hcm")){
   		url="/templates/index/hcm_portal.do?b_query=link";
   		target="il_body";    
    }
    if(!sp_ori.equals("1"))
    {
		if(returnflag.equals("menu"))
		{
			url="/gz/gz_accounting/gz_sp_setlist.do?b_query=link&flow_flag=1&gz_module="+gz_module+"&returnvalue=menu";
	   		target="il_body";
		}
		if(returnflag.equals("collect"))
		{
			url="/gz/gz_sp_collect/gz_sp_collect.do?b_query=link&ori=0&zjjt=1&returnflag=menu&salaryid="+salaryid+"&records="+salaryid+"&gz_module="+gz_module;
	   		target="il_body";
		}
	     /* 2051 薪资审批，从导航图中进入薪资审批界面，点明细表进入后，点返回，回到的界面没有返回按钮导致回不到导航图界面了，不对 xiaoyun 2014-10-23 start */
	     if(returnflag.equals("dxt")) {
	    	 url="/gz/gz_accounting/gz_sp_setlist.do?b_query=link&flow_flag=1&gz_module="+gz_module;
		   	 target="il_body";
	     }
	     /* 2051 薪资审批，从导航图中进入薪资审批界面，点明细表进入后，点返回，回到的界面没有返回按钮导致回不到导航图界面了，不对 xiaoyun 2014-10-23 end */
	}
	 int versionFlag = 1;
	 //zxj 20160613 薪资审批不区分标准版专业版
	 //if (userView != null)
	//	versionFlag = userView.getVersion_flag(); // 1:专业版 0:标准版		
		
	String m_sql=acountingForm.getSql();
	String _sql=PubFunc.decrypt(m_sql);
	String sp_actor_str=acountingForm.getSp_actor_str();
    String sp_actor_name=acountingForm.getSpActorName();
    String relation_id=acountingForm.getRelation_id();
    String appealName="报批";
    if(sp_actor_name.length()>0)
    	appealName="报["+sp_actor_name+"]审批";
    boolean isApprove=true;
    boolean isAppeal=true;
    if(relation_id!=null&&relation_id.length()>0&&sp_actor_str.length()>0)
    	isApprove=false;
    if(relation_id!=null&&relation_id.length()>0&&sp_actor_str.length()==0)
    	isAppeal=false;
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}  
 %>
<html> 
<script language="javascript">
<!--
  var prv_project_id="${accountingForm.itemid}";
  var isSendMessage="${accountingForm.isSendMessage}";
  var salaryid='${accountingForm.salaryid}';
  var verify_ctrl='${accountingForm.verify_ctrl}';
  var isTotalControl='${accountingForm.isTotalControl}';
  var subNoShowUpdateFashion='${accountingForm.subNoShowUpdateFashion}';
  var gz_module='${accountingForm.gz_module}';
  var gzsptablename = '${accountingForm.gz_tablename}';
  var sp_actor_str='${accountingForm.sp_actor_str}'
  var a_code='<%=(request.getParameter("a_code"))%>';
	function back()
	{
		accountingForm.target="<%=target%>";
		accountingForm.action="<%=url%>";
		accountingForm.submit(); 
	}
	
	
	var prv_filter_id="${accountingForm.condid}";
	function bankdisk_changeCondList(salaryid)
	{
		 var hashVo=new ParameterSet();
		 hashVo.setValue("isclose","2");
		 hashVo.setValue("salaryid",salaryid);
		 var In_parameters="opt=1";
		 var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:change_condlist_ok,functionId:'3020100017'},hashVo);			
		    
	}
	
	
	function collectTable(gz_module)
	{
		 if(document.getElementById("buttoncollect_table"))
		 	document.getElementById("buttoncollect_table").disabled=true;
	 	 accountingForm.target="<%=target%>";
	 	 accountingForm.action="/gz/gz_accounting/gz_collect_orgtree.do?b_tree=link&salaryid=${accountingForm.salaryid}&gz_module="+gz_module;//指向和70一致的路径
	 	 accountingForm.submit();
	}
	
	
	function change_condlist_ok(outparameters)
	{
	   var filterList = outparameters.getValue("filterCondList");
	   AjaxBind.bind(accountingForm.condid,filterList);    
	   var obj=$("condid"); 
		  if(obj.options.length==2)
		  {
		    accountingForm.action="/gz/gz_accounting/gz_sptable.do?b_query=link&salaryid=${accountingForm.salaryid}";
		    accountingForm.submit();
		  }
		  else
		  {
		     for(var i=0;i<obj.options.length;i++)
		     {
		        if(obj.options[i].value==prv_filter_id)
		        {
		            obj.options[i].selected=true;
		            return;
		        }
		     }
		    accountingForm.action="/gz/gz_accounting/gz_sptable.do?b_query=link&salaryid=${accountingForm.salaryid}";
			accountingForm.submit();  
		  }     
	}
	

function verifyFormula(selectID,opt,userid)
{
		 var a00z1=accountingForm.bosdate.value;
		   var a00z0=accountingForm.count.value;
		   if(a00z1==''||a00z0=='')
		   {
		     alert(GZ_SELECT_BOSDATEANDCOUNT+"！");
		     return;
		   }
		   var hashvo=new ParameterSet();
		   hashvo.setValue("a_code","${accountingForm.a_code}");
		   hashvo.setValue("condid",accountingForm.condid.value);
		   hashvo.setValue("salaryid",salaryid);
		   hashvo.setValue("type","1");
		   hashvo.setValue("a00z0",a00z0);
		   hashvo.setValue("a00z1",a00z1);
	     var sql = accountingForm.reportSql.value;
   		 hashvo.setValue("reportSQL",sql);
	     hashvo.setValue("selectID",selectID);
	     hashvo.setValue("opt",opt);
	     hashvo.setValue("userid",userid);
	     var request=new Request({asynchronous:false,onSuccess:check_ok3,functionId:'3020070016'},hashvo);	
}


	
	
function selectFormula(salaryid)
{
   var a00z1=accountingForm.bosdate.value;
   var a00z0=accountingForm.count.value;
   if(a00z1==''||a00z0=='')
   {
     alert(GZ_SELECT_BOSDATEANDCOUNT+"！");
     return;
   }
   var hashvo=new ParameterSet();
   hashvo.setValue("a_code","${accountingForm.a_code}");
   hashvo.setValue("condid",accountingForm.condid.value);
   hashvo.setValue("salaryid",salaryid);
   hashvo.setValue("type","1");
   var sql = accountingForm.reportSql.value;
   hashvo.setValue("reportSQL",sql);
   hashvo.setValue("a00z0",a00z0);
   hashvo.setValue("a00z1",a00z1);
   var request=new Request({asynchronous:false,onSuccess:check_ok,functionId:'3020070016'},hashvo);	
  // var strurl="/gz/gz_accounting/sh_formula.do?b_query=link`salaryid="+salaryid+"`condid="+accountingForm.condid.value+"`a_code=${accountingForm.a_code}`type=1`a00z1="+a00z1+"`a00z0="+a00z0;
  // var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
  // var flag=window.showModalDialog(iframe_url,arguments,"dialogWidth=400px;dialogHeight=400px;resizable=yes;scroll=no;status=no;");  
   
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
function gzReport()
	{
	       var a00z1=accountingForm.bosdate.value;
           var a00z0=accountingForm.count.value;
           if(a00z1==''||a00z0=='')
           {
             alert(GZ_SELECT_BOSDATEANDCOUNT+"！");
             return;
           }
            var rsql= getEncodeStr(accountingForm.reportSql.value);
           
		    var arguments=new Array();     
		    var strurl="/gz/gz_accounting/report.do?b_query=link`model=1`salaryid=${accountingForm.salaryid}`a_code=${accountingForm.a_code}`gz_module=${accountingForm.gz_module}`condid=${accountingForm.condid}`count=${accountingForm.count}`bosdate=${accountingForm.bosdate}`s="+rsql;
		    var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
		    
		    var flag=window.showModalDialog(iframe_url,arguments,"dialogWidth=500px;dialogHeight=330px;resizable=yes;scroll=no;status=no;");  
			//parent.mil_menu.location=parent.mil_menu.location+"?b_opt=1";
	}
function bankdisk_bankDisk(a_code,tableName,salaryid)
{
  var a00z1=accountingForm.bosdate.value;
  var a00z0=accountingForm.count.value;
  if(a00z1==''||a00z0=='')
  {
       alert(GZ_SELECT_BOSDATEANDCOUNT+"！");
       return;
  }
 var condid=accountingForm.condid.value;
 var height=window.screen.height-85;
 var Actual_Version=browserinfo();
 if (Actual_Version!=null&&Actual_Version.length>0&&Actual_Version=='7.0') {
				  	
	   height=height-25;
 }
var strurl="/gz/gz_accountingt/bankdisk/initBankDisk.do?b_init=init`model=1`condid="+condid+"`opt=init`o=1`code="+a_code+"`tableName="+tableName+"`count=${accountingForm.count}`bosdate=${accountingForm.bosdate}`salaryid="+salaryid+"`s="+getEncodeStr(accountingForm.reportSql.value);
 var iframe_url="/gz/gz_accounting/bankdisk/iframe_bank_disk.jsp?src="+strurl; 
var flag=window.showModalDialog(iframe_url,null,"dialogWidth="+window.screen.width+"px;dialogHeight="+window.screen.height+"px;resizable=yes;scroll=no;status=no;");  

}
function dygs()
{
  var salaryid="${accountingForm.salaryid}";
  var strurl="/gz/templateset/spformula/sp_formula.do?b_query=link`returnType=1`opt=0`salaryid="+salaryid;
  var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
  var flag=window.showModalDialog(iframe_url,arguments,"dialogWidth=810px;dialogHeight=500px;resizable=yes;scroll=no;status=no;");  
}

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
	 accountingForm.action="/gz/gz_accounting/gz_sptable.do?b_query=link&query=1&salaryid="+salaryid;
	 accountingForm.submit();    
}


function resetleft(i)
{
   		var a_obj=document.getElementById('buttonback');
   		if(a_obj)
   		{
            oRect=a_obj.getBoundingClientRect();   
            var a_value=oRect.left;
            var minlength=300;
            if (i>1){
                minlength=420;
            }
            if(a_value<minlength)
                a_value=minlength; 
	        document.getElementById('selectprename').style.left=a_value+50;
	    }
}


-->

</script>
<% int i=0; %>
<body      onload='resetleft1()' >
<html:form action="/gz/gz_accounting/gz_sptable">

	<div id='wait' style='position:absolute;top:130;left:300;display:none;'>
		<table border="1" width="100" cellspacing="0" cellpadding="4"  class="table_style"  height="87" align="center">
			<tr>
				<td  class="td_style"  id='wait_desc'   height=24>
					<bean:message key="label.gz.submitData"/>......
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee  class="marquee_style"  direction="right" width="300" scrollamount="5" scrolldelay="10" >
						<table cellspacing="1" cellpadding="0">
							<tr height=8>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
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
<table width="100%" >
<%}else{ %>
<table width="100%"  style="margin-top:-3px;">
<%} %>	
		<tr>
			<td width="100%">

				<table>
					<tr>
						<td >
						<logic:equal name="accountingForm" property="gz_module" value="0">
							<hrms:menubar menu="menu1" id="menubar1">
								<hrms:menuitem name="gz1" label="menu.gz.edit" >
									<% if(isAppeal){ %>
								    <hrms:menuitem name="m0" label="个别报批"   url="appeal('${accountingForm.userid}','appeal','${accountingForm.salaryid}','${accountingForm.gz_module}')" command=""  function_id="3240301,3270301"  />
									<% } %>
									<% if(isApprove){ %>
									<hrms:menuitem name="m1" label="个别批准"  url="optSalary('confirm','${accountingForm.userid}')" command=""  function_id="3240302,3270302" />
									<% } %>
									
									<hrms:menuitem name="m2" label="button.reject"   url="optSalary('reject','${accountingForm.userid}')" command=""  function_id="3240303,3270303" />
									<% if(bedit.equals("true")){ %>
									<hrms:menuitem name="m3" label="button.submit"  icon="/images/sb.gif" url="sub('${accountingForm.salaryid}')" command=""  function_id="3240305,3270305"  />
									<% } %> 
									<hrms:menuitem name="mitem1" label="menu.gz.batch.import" icon="/images/goto_input.gif" url="batch_import_history('${accountingForm.salaryid}','${accountingForm.gz_module}')" command="" enabled="true" visible="true"  function_id="3240317,3270315,3271317" />
									<hrms:menuitem name="m4" label="menu.gz.delete"   icon="/images/del.gif"  url="" command="delselected" hint="general.inform.search.confirmed.del"  function_id="3240306,3270306"  />
									<hrms:menuitem name="m6" label="menu.gz.numericcompare" icon="/images/edit.gif"  url="javascript:checkHasCompareField('${accountingForm.salaryid}','${accountingForm.gz_module}','${accountingForm.flow_flag}');" command=""  function_id="3240307,3270307" />
									<hrms:menuitem name="m7" label="button.download.template" icon="/images/export.gif" url="downLoadTemp('${accountingForm.salaryid}')"  function_id="3240313,3270313"/> 
									<hrms:menuitem name="m8" label="import.tempData" icon="/images/import.gif" url="exportTempData('${accountingForm.salaryid}','sp')"  function_id="3240314,3270314" /> 
									<hrms:menuitem name="m9" label="menu.gz.export" icon="/images/import.gif" url="exportTable('${accountingForm.salaryid}','sp')"  function_id="3240318,3270317,3271320" />
									<hrms:menuitem name="m10" label="label.gz.lookitemsum" icon="" url="itemsSum('${accountingForm.salaryid}','sp')"  function_id="" />  
								</hrms:menuitem>
								<hrms:menuitem name="gz2" label="menu.gz.view">
									<hrms:menuitem name="mitem1" label="menu.gz.manfilter" icon="/images/add_del.gif" url="to_gzsp_person_filter('${accountingForm.salaryid}','${accountingForm.gz_tablename}')" command="" enabled="true" visible="true" />
									<!-- zgd 2015-1-21 add 薪资审批增加人员排序 start -->
									<hrms:menuitem name="mitem5" label="infor.menu.msort" icon="/images/sort.gif" url="to_sort_emp2('${accountingForm.salaryid}');" command="" enabled="true" visible="true"/>
									<!-- zgd 2015-1-21 add 薪资审批增加人员排序 end -->
									<hrms:menuitem name="mitem2" label="menu.gz.sortman" function_id="3240216,3240316,3270216,3271216,3270316,3271316" icon="/images/write.gif" url="syncgzspemp('${accountingForm.salaryid}','${accountingForm.bosdate}');" command="" enabled="true" visible="true" />
									<hrms:menuitem name="mitem3" label="menu.gz.hide"   function_id="3240325,3270210"    icon="/images/write.gif" url="gzSpHide('${accountingForm.salaryid}','sp');" command="" enabled="true" visible="true"/>
     
								</hrms:menuitem>
								<hrms:menuitem name="gz4" label="menu.gz.spaccounting" function_id="3240310,3270309">
									<hrms:menuitem name="mitem1" label="menu.gz.updisk" function_id="324031001,327030901" icon="/images/write.gif" url="bankdisk_bankDisk('${accountingForm.a_code}','${accountingForm.gz_tablename}','${accountingForm.salaryid}')" command="" enabled="true" visible="true" />
									<hrms:menuitem name="mitem2" label="<%=gzrp_str%>" function_id="324031002,327030902" icon="" url="gzReport();" command="" enabled="true" visible="true" />
									<hrms:menuitem name="gz4mitem2" label="审批提交" function_id="324031003,327030903"     icon="" url="open_submit_dialog2('${accountingForm.salaryid}','${accountingForm.gz_module}');"  command="" enabled="true" visible="true" />
								</hrms:menuitem>
								<hrms:menuitem name="gz3" label="menu.gz.options"  function_id="3240307,3270307,32403011,32703011" >
									<hrms:menuitem name="mitem1" label="menu.gz.chgitem" icon="/images/compute.gif" url="setSpchange();" command="" enabled="true" visible="true"  function_id="3240307,3270307" />
									<hrms:menuitem name="mitem2" label="infor.menu.definition.shformula" icon="" url="dygs();" command="" enabled="true" visible="true"  function_id="32403011,32703011" />
								</hrms:menuitem>
							</hrms:menubar>
						 </logic:equal>
						 
						 <logic:equal name="accountingForm" property="gz_module" value="1">
							<hrms:menubar menu="menu1" id="menubar1">
								<hrms:menuitem name="gz1" label="menu.gz.edit" >
								    <% if(isAppeal){ %>
								    <hrms:menuitem name="m0" label="个别报批"   url="appeal('${accountingForm.userid}','appeal','${accountingForm.salaryid}','${accountingForm.gz_module}')" command=""  function_id="3250301,3271301"  />
									<% } %>
									<% if(isApprove){ %>
									<hrms:menuitem name="m1" label="个别批准"  url="optSalary('confirm','${accountingForm.userid}')" command=""  function_id="3250302,3271302" />
									<% } %>
									<hrms:menuitem name="m2" label="button.reject"   url="optSalary('reject','${accountingForm.userid}')" command=""  function_id="3250303,3271303" />
									<% if(bedit.equals("true")){ %>
									<hrms:menuitem name="m3" label="button.submit"  icon="/images/sb.gif" url="sub('${accountingForm.salaryid}')" command=""  function_id="3250305,3271305"  />
									<% } %> 
									<hrms:menuitem name="m001" label="menu.gz.batch.import" icon="/images/goto_input.gif" url="batch_import_history('${accountingForm.salaryid}','${accountingForm.gz_module}')" command="" enabled="true" visible="true"  function_id="3250317,3270315,3271317" />
									
									<hrms:menuitem name="m4" label="menu.gz.delete"   icon="/images/del.gif"  url="" command="delselected" hint="general.inform.search.confirmed.del"  function_id="3250306,3271306"  />
									<hrms:menuitem name="m6" label="menu.gz.numericcompare" icon="/images/edit.gif"  url="javascript:checkHasCompareField('${accountingForm.salaryid}','${accountingForm.gz_module}','${accountingForm.flow_flag}');" command=""  function_id="3250307,3271307" />
									<hrms:menuitem name="m7" label="button.download.template" icon="/images/export.gif" url="downLoadTemp('${accountingForm.salaryid}')"  function_id="3250318,3271318"/> 
									<hrms:menuitem name="m8" label="import.tempData" icon="/images/import.gif" url="exportTempData('${accountingForm.salaryid}','sp')"  function_id="3250319,3271319" /> 
									<hrms:menuitem name="m9" label="menu.gz.export" icon="/images/import.gif" url="exportTable('${accountingForm.salaryid}','sp')"  function_id="3250315,3270317,3271320" />
									<hrms:menuitem name="m10" label="label.gz.lookitemsum" icon="" url="itemsSum('${accountingForm.salaryid}','sp')"  function_id="" />  
								</hrms:menuitem>
								<hrms:menuitem name="gz2" label="menu.gz.view">
									<hrms:menuitem name="mitem1" label="menu.gz.manfilter" icon="/images/add_del.gif" url="to_gzsp_person_filter('${accountingForm.salaryid}','${accountingForm.gz_tablename}')" command="" enabled="true" visible="true" />
									<hrms:menuitem name="mitem2" label="menu.gz.sortman" function_id="3250316,3250216,3270216,3271216,3270316,3271316" icon="/images/write.gif" url="syncgzspemp('${accountingForm.salaryid}','${accountingForm.bosdate}');" command="" enabled="true" visible="true" />
									<hrms:menuitem name="mitem3" label="menu.gz.hide"   function_id="3250325,3270210"    icon="/images/write.gif" url="gzSpHide('${accountingForm.salaryid}','sp');" command="" enabled="true" visible="true"/>
     
								</hrms:menuitem>
								<hrms:menuitem name="gz4" label="menu.gz.spaccounting" function_id="3250310,3271308">
									<hrms:menuitem name="mitem2" label="<%=gzrp_str%>" function_id="325031002,327130802" icon="" url="gzReport();" command="" enabled="true" visible="true" />
									<hrms:menuitem name="gz4mitem2" label="审批提交"     function_id="325031003,327130803" icon="" url="open_submit_dialog2('${accountingForm.salaryid}','${accountingForm.gz_module}');"  command="" enabled="true" visible="true" />
								</hrms:menuitem>
								<hrms:menuitem name="gz3" label="menu.gz.options"  function_id="3250307,3271307,32503011,32713011" >
									<hrms:menuitem name="mitem1" label="menu.gz.chgitem" icon="/images/compute.gif" url="setSpchange('1');" command="" enabled="true" visible="true"  function_id="3250307,3271307" />
									<hrms:menuitem name="mitem2" label="infor.menu.definition.shformula" icon="" url="dygs();" command="" enabled="true" visible="true"  function_id="32503011,32713011" />
								</hrms:menuitem>
							</hrms:menubar>
						 </logic:equal>	
						</td>
					</tr>
					</table>
			</td>
			</tr>
			</table>
<html:hidden name="accountingForm" property="sort_table_approval" />
<table width="100%">
<tr id="queryblank" style="display=none" height="25" >

</tr>
<tr id="querydata" style="display=none" >
<td align="left" width="100%">
<table ellspacing="0" cellpadding="0" width="100%">
<tr >
<td align="left" valign="middle">&nbsp;&nbsp;&nbsp;
<logic:iterate id="element" name="accountingForm" property="queryFieldList" indexId="index">

<bean:write name="element" property="itemdesc"/>&nbsp;
 <logic:equal name="element" property="itemtype" value="D">
 <html:text name="accountingForm" styleClass="text4" property='<%="queryFieldList["+i+"].value"%>'  size="20"  onclick='popUpCalendar(this,this, dateFormat,\"\",\"\",true,false)'/>                                            
  </logic:equal>
                          <!--字符型 -->                                                    
                          <logic:equal name="element" property="itemtype" value="A">
                              <logic:notEqual name="element" property="codesetid" value="0">
                              <html:hidden name="accountingForm" property='<%="queryFieldList["+i+"].value"%>'/>
                                 <html:text name="accountingForm" styleClass="text4" property='<%="queryFieldList["+i+"].viewvalue"%>'  size="20"  onchange="fieldcode(this,1)" style="vertical-align: middle;"/>         
                                <img src="/images/code.gif" onclick='openCondCodeDialog("${element.codesetid}","<%="queryFieldList["+i+"].viewvalue"%>");' style="vertical-align: middle;"/>
                              </logic:notEqual>               
                              <logic:equal name="element" property="codesetid" value="0">
                              <html:text name="accountingForm" styleClass="text4" property='<%="queryFieldList["+i+"].value"%>' size="20"  style="vertical-align: middle;"/>                                 
                              </logic:equal>                                                         
                          </logic:equal> 
                          <!--数据值-->                            
                          <logic:equal name="element" property="itemtype" value="N">
                            <html:text name="accountingForm" styleClass="text4" property='<%="queryFieldList["+i+"].value"%>' size="20" style="vertical-align: middle;"/>                      
                                               
                          </logic:equal>    
                          <!--备注型--> 
                          <logic:equal name="element" property="itemtype" value="M">
                           
                            <html:text name="accountingForm" styleClass="text4" property='<%="queryFieldList["+i+"].value"%>' size="20"  style="vertical-align: middle;"/>                      
                                                
                          </logic:equal>                   
                          &nbsp;&nbsp;&nbsp;&nbsp;
  
<%i++; %>
</logic:iterate>
 
&nbsp;&nbsp;<input type="button" name="query" value="查询" class="mybutton" onclick="queryR();" style="vertical-align: middle;" />&nbsp;&nbsp;&nbsp;
</td>
</tr>
</table>
</td>
</tr>
	</table>
<logic:equal name="accountingForm" property="gz_module" value="0">
		<hrms:dataset name="accountingForm" property="fieldlist" scope="session" setname="SalaryHistory" setalias="gz_sptable" pagerows="${accountingForm.pagerows}" readonly="false" editable="true" select="true" sql="<%=_sql %>"  rowlock="true"  rowlockfield="sp_flag"    rowlockvalues='${accountingForm.spRowCanEditStatus}'     buttons="bottom">
		
			<hrms:commandbutton name="delselected" hint="general.inform.search.confirmed.del"  functionId="3020111001" function_id="3240306,3270306" refresh="true" type="selected" setname="SalaryHistory">
				<bean:message key="button.delete" />
			</hrms:commandbutton>
			<hrms:commandbutton name="savedata"  functionId="3020111002" function_id="3240304,3270304" refresh="false" type="all-change" setname="SalaryHistory">
				<bean:message key="button.save" />
			</hrms:commandbutton>
	<% if(isAppeal){ %>
			<hrms:commandbutton name="appeal1" hint="general.inform.search.confirmed.appeal" function_id="3240301,3270301" refresh="true" type="selected" setname="${accountingForm.gz_tablename}" onclick="appeal('${accountingForm.userid}','appeal','${accountingForm.salaryid}','${accountingForm.gz_module}')">
				个别<%=appealName%>
			</hrms:commandbutton>
	<% } %>
	<% if(isApprove){ %>
			<hrms:commandbutton name="table" hint="general.inform.search.confirmed.agreeSelectedRecords" function_id="3240302,3270302" refresh="true" type="selected" setname="SalaryHistory" onclick="optSalary('confirm','${accountingForm.userid}')">
				个别<bean:message key="button.approve" />
			</hrms:commandbutton>
	<% } %>	
	<% if(isAppeal){ %>	
			<hrms:commandbutton name="appeal2" hint="general.inform.search.confirmed.appeal" function_id="3240311,3270311" refresh="true" type="selected" setname="${accountingForm.gz_tablename}" onclick="appeal('${accountingForm.userid}','appealAll','${accountingForm.salaryid}','${accountingForm.gz_module}')">
				<%=appealName%>
			</hrms:commandbutton>
	<% } %>
	<% if(isApprove){ %>		
			<hrms:commandbutton name="table2" hint="general.inform.search.confirmed.agreeSelectedRecords" function_id="3240312,3270312" refresh="true" type="selected" setname="SalaryHistory" onclick="optSalary('confirmAll','${accountingForm.userid}')">
				<bean:message key="button.approve" />
			</hrms:commandbutton>
	<% } %>		
			<hrms:commandbutton name="reject" hint="general.inform.search.confirmed.rejectSelectedRecords" function_id="3240303,3270303" refresh="true" type="selected" setname="SalaryHistory" onclick="optSalary('reject','${accountingForm.userid}')">
				个别<bean:message key="button.reject" />
			</hrms:commandbutton>
			
			<hrms:commandbutton name="rejectAll" hint="general.inform.search.confirmed.rejectSelectedRecords" function_id="3240303,3270303" refresh="true" type="selected" setname="SalaryHistory" onclick="optSalary('rejectAll','${accountingForm.userid}')">
				<bean:message key="button.reject" />
			</hrms:commandbutton>
			
			 <hrms:commandbutton name="compute"  refresh="true" type="selected" setname="SalaryHistory"  function_id="3240308,3270308"     onclick="get_sp_formula('${accountingForm.salaryid}')" >
				<bean:message key="button.computer"/>
			</hrms:commandbutton>
			
			
		 <hrms:commandbutton name="sh_formula"  onclick="selectFormula('${accountingForm.salaryid}')"   function_id="3240309,3240213"  refresh="true" type="selected" setname="${accountingForm.gz_tablename}" >
		   <bean:message key="button.audit"/>
		   </hrms:commandbutton>
		   
		   
		   <%if(ver.searchFunctionId("3240315")&&gz_module.equals("0")&&versionFlag==1){  %>
		
		   	 <hrms:commandbutton name="collect_table"  onclick="collectTable('0')"   function_id="3240315"  refresh="true" type="selected" setname="${accountingForm.gz_tablename}" >
		   			<bean:message key="label.gz.collecttable"/>
		     </hrms:commandbutton>
		 
		   <% } %>
		   <%if(ver.searchFunctionId("3250313")&&gz_module.equals("1")&&versionFlag==1){  %>
		   	 
		   	 <hrms:commandbutton name="collect_table"  onclick="collectTable('1')"   function_id="3250313"  refresh="true" type="selected" setname="${accountingForm.gz_tablename}" >
		   			<bean:message key="label.gz.collecttable"/>
		     </hrms:commandbutton>
		
		   <% } %>
		   
		   
		     <hrms:commandbutton name="showstate"  onclick="showstate()"   function_id="3240320"  refresh="true" type="selected" setname="${accountingForm.gz_tablename}" >
  				 <bean:message key="menu.gz.state"/>
   			</hrms:commandbutton>
   			 <hrms:commandbutton name="showprocess"  onclick="showprocess('sp','${accountingForm.gz_tablename}')"   function_id="3240321"  refresh="true" type="selected" setname="${accountingForm.gz_tablename}" >
  			 <bean:message key="menu.gz.process"/>
  			 </hrms:commandbutton>
  			 
  			  <hrms:commandbutton name="reimport"  onclick="reImport_sp('${accountingForm.salaryid}','${accountingForm.gz_module}')"   function_id="3240324"  refresh="true" type="selected" setname="${accountingForm.gz_tablename}" >
  					重新导入
  			</hrms:commandbutton>
  			 
  			 
  			 
		   <% //if(sp_ori.equals("1"))
		      { %>
				<hrms:commandbutton name="back"   refresh="true"   setname="SalaryHistory" onclick="back()">
				<bean:message key="button.return" />
				</hrms:commandbutton>
		    <% } %>
		   
		</hrms:dataset>
</logic:equal>
<logic:equal name="accountingForm" property="gz_module" value="1">
		<hrms:dataset name="accountingForm" property="fieldlist" scope="session" setname="SalaryHistory" setalias="gz_sptable" pagerows="${accountingForm.pagerows}" readonly="false" editable="true" select="true" sql="<%=_sql %>"  rowlock="true"  rowlockfield="sp_flag"    rowlockvalues='${accountingForm.spRowCanEditStatus}'     buttons="bottom">
		
			<hrms:commandbutton name="delselected" hint="general.inform.search.confirmed.del"  functionId="3020111001" function_id="3250306,3271306" refresh="true" type="selected" setname="SalaryHistory">
				<bean:message key="button.delete" />
			</hrms:commandbutton>
			<hrms:commandbutton name="savedata"  functionId="3020111002" function_id="3250304,3271304" refresh="false" type="all-change" setname="SalaryHistory">
				<bean:message key="button.save" />
			</hrms:commandbutton>
	<% if(isAppeal){ %>
			<hrms:commandbutton name="appeal" hint="general.inform.search.confirmed.appeal" function_id="3250301,3271301" refresh="true" type="selected" setname="${accountingForm.gz_tablename}" onclick="appeal('${accountingForm.userid}','appeal','${accountingForm.salaryid}','${accountingForm.gz_module}')">
				个别<%=appealName%>
			</hrms:commandbutton>
	<% } %>
	<% if(isApprove){ %>
			<hrms:commandbutton name="table" hint="general.inform.search.confirmed.agreeSelectedRecords" function_id="3250302,3271302" refresh="true" type="selected" setname="SalaryHistory" onclick="optSalary('confirm','${accountingForm.userid}')">
				个别<bean:message key="button.approve" />
			</hrms:commandbutton>
	<% } %>		
	<% if(isAppeal){ %>
			<hrms:commandbutton name="appeal2" hint="general.inform.search.confirmed.appeal" function_id="3250311,3271311" refresh="true" type="selected" setname="${accountingForm.gz_tablename}" onclick="appeal('${accountingForm.userid}','appealAll','${accountingForm.salaryid}','${accountingForm.gz_module}')">
				<%=appealName%>
			</hrms:commandbutton>
	<% } %>
	<% if(isApprove){ %>	
			<hrms:commandbutton name="table2" hint="general.inform.search.confirmed.agreeSelectedRecords" function_id="3250312,3271312" refresh="true" type="selected" setname="SalaryHistory" onclick="optSalary('confirmAll','${accountingForm.userid}')">
				<bean:message key="button.approve" />
			</hrms:commandbutton>
	<% } %>		
			<hrms:commandbutton name="reject" hint="general.inform.search.confirmed.rejectSelectedRecords" function_id="3250303,3271303" refresh="true" type="selected" setname="SalaryHistory" onclick="optSalary('reject','${accountingForm.userid}')">
				个别<bean:message key="button.reject" />
			</hrms:commandbutton>
			<hrms:commandbutton name="rejectAll" hint="general.inform.search.confirmed.rejectSelectedRecords" function_id="3250303,3271303" refresh="true" type="selected" setname="SalaryHistory" onclick="optSalary('rejectAll','${accountingForm.userid}')">
				<bean:message key="button.reject" />
			</hrms:commandbutton>
			 <hrms:commandbutton name="compute"  refresh="true" type="selected" setname="SalaryHistory"  function_id="3250308,3271309"     onclick="get_sp_formula('${accountingForm.salaryid}')" >
				<bean:message key="button.computer"/>
			</hrms:commandbutton>
			
		 <hrms:commandbutton name="sh_formula"  onclick="selectFormula('${accountingForm.salaryid}')"   function_id="3250309"  refresh="true" type="selected" setname="${accountingForm.gz_tablename}" >
		   <bean:message key="button.audit"/>
		   </hrms:commandbutton>
		   <hrms:commandbutton name="showstate"  onclick="showstate()"   function_id="3250320"  refresh="true" type="selected" setname="${accountingForm.gz_tablename}" >
  				 <bean:message key="menu.gz.state"/>
   			</hrms:commandbutton>
   			 <hrms:commandbutton name="showprocess"  onclick="showprocess('sp','${accountingForm.gz_tablename}')"   function_id="3250321"  refresh="true" type="selected" setname="${accountingForm.gz_tablename}" >
  			 <bean:message key="menu.gz.process"/>
  			 </hrms:commandbutton>
  			 
  			  <hrms:commandbutton name="reimport"  onclick="reImport_sp('${accountingForm.salaryid}','${accountingForm.gz_module}')"   function_id="3250324"  refresh="true" type="selected" setname="${accountingForm.gz_tablename}" >
  					重新导入
  			</hrms:commandbutton>
		   <% // if(sp_ori.equals("1"))		   		
		   	   { %>
				<hrms:commandbutton name="back"   refresh="true"   setname="SalaryHistory" onclick="back()">
				<bean:message key="button.return" />
				</hrms:commandbutton>
		    <% } %>
		   
		</hrms:dataset>
</logic:equal>

	
<%if("hl".equals(hcmflag)){ %>
<table id="selectprename"  style="position:absolute;left:810px;top:33px;z-index:10;"><tr>
<%}else{ %>
<table id="selectprename"  style="position:absolute;left:810px;top:40px;z-index:10;"><tr>
<%} %>  

<td id="querydesc" nowrap>
[&nbsp;<a href="javascript:visiblequery();" >查询显示&nbsp;</a>]&nbsp;&nbsp;&nbsp;
</td>
<td nowrap ><bean:message key="label.gz.appealUserFilter"/></td>
<td>
<html:select name="accountingForm" styleId="appUserFilter" property="appUser" size="1" onchange="search_gz_spdata('${accountingForm.salaryid}');">
   <html:optionsCollection property="appUserList" value="dataValue" label="dataName"/>
</html:select> &nbsp;&nbsp;
</td>


<td nowrap ><bean:message key="label.gz.itemfilter"/></td>
<td>
<html:select name="accountingForm" styleId="projectFilter" property="itemid" size="1" onchange="search_gz_data_byitem('${accountingForm.salaryid}',this,'1');">
   <html:optionsCollection property="itemlist" value="dataValue" label="dataName"/>
</html:select> 
</td>
<td nowrap ><bean:message key="label.gz.condfilter" /></td>
<td>
<html:select name="accountingForm" property="condid" size="1" onchange="search_gz_spdata_bycond('${accountingForm.salaryid}',this,'${accountingForm.gz_tablename}');">
					<html:optionsCollection property="condlist" value="dataValue" label="dataName" />
				</html:select>
</td><td  nowrap ><bean:message key="label.gz.appdate" /> </td>
<td>
<html:select name="accountingForm" property="bosdate" size="1" onchange="searchdata('${accountingForm.salaryid}');">
					<html:optionsCollection property="datelist" value="dataValue" label="dataName" />
				</html:select> 
</td>
<td  nowrap ><bean:message key="label.gz.count" /> </td>
<td>
<html:select name="accountingForm" property="count" size="1" onchange="searchdata('${accountingForm.salaryid}');">
					<html:optionsCollection property="countlist" value="dataValue" label="dataName" />
				</html:select>
</td>


<td  nowrap ><bean:message key="lable.zp_plan.status" /> </td>
<td>
<html:select name="accountingForm" property="sp_flag" size="1" onchange="search_gz_spdata('${accountingForm.salaryid}');">
					<html:optionsCollection property="spFlagList" value="dataValue" label="dataName" />
				</html:select>
</td>

</tr></table> 
	<input type='hidden' name='sql' value='<%=m_sql%>' />
	<html:hidden name="accountingForm" property="empfiltersql" />
	<html:hidden name="accountingForm" property="reportSql" />
	<html:hidden name="accountingForm" property="filterWhl" />
	<input type='hidden' name='selectGzRecords' value='' />
	<Input type='hidden' name='rejectCause' value='' />
	<Input type='hidden' name='sendMen'  value='' />
	<html:hidden name="accountingForm" property="proright_str" /> 
	<input type='hidden' name='approveObject' value='' />
	<input type='hidden' name='cond_id_str' value=''/>
	<input type="hidden" name="model" value="1" id="gm"/>
</html:form>
</body>
</html>

<script language="javascript">
function resetleft1()
{
   resetleft(<%=i%>); 
}

</script >