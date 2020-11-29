<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.performance.workPlanTeam.WorkPlanTeamForm,
                 org.apache.commons.beanutils.LazyDynaBean,
                 com.hrms.struts.constant.SystemConfig,
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant,
				 com.hrms.hjsj.sys.EncryptLockClient,
                 java.util.*,
                 java.text.*,
                 com.hjsj.hrms.businessobject.performance.workplanteam.WorkPlanTeamBo,
                 com.hrms.frame.codec.SafeCode,
                 com.hjsj.hrms.utils.PubFunc
                 "%>
                 
<script language="javascript" src="/general/sys/hjaxmanage.js"></script>
<script language="javascript">AxManager.writeCard();</script>               
<SCRIPT LANGUAGE=javascript src="/performance/workplan/workplanteam/workplanteam.js"></SCRIPT>
<% 
   WorkPlanTeamForm myForm=(WorkPlanTeamForm)session.getAttribute("workPlanTeamForm");
   WorkPlanTeamBo bo = new WorkPlanTeamBo();
   String workType = myForm.getWorkType();
   String status = myForm.getStatus();
   HashMap spMap = myForm.getSpMap();
   LinkedHashMap weekMap = myForm.getWeekMap();
   int i=0;
   int j=0;
   if("2".equals(workType)){
     i=8;
     j=8;
   }else if("4".equals(workType)){
     i=9; 
     j=9;
   }else if("6".equals(workType)){
  	 i=9;
  	 j=9;
   }else if("10".equals(workType)){
  	 i=10;
  	 j=10;
   }else if("8".equals(workType)){
  	 i=10;
  	 j=10;
   }
   
   		String url_p = SystemConfig.getServerURL(request);
   		String userName = null;
   		String userFullName = null;
   		String superUser = "0";
   		String fields = "";
		String tables = "";
   		UserView userView = (UserView)session.getAttribute(WebConstant.userView);   		
   		if(userView != null)
   		{
   			userName = userView.getUserName();  
   			userFullName = userView.getUserFullName();		  		  		
			if(userView.isSuper_admin())
	  			superUser = "1";
	  		
	  		if(!userView.isSuper_admin())
      		{
           	//	if(userpriv!=null && userpriv.equals("selfinfo"))
	       	//	{
	        //   	fields = userView.getEmp_fieldpriv().toString();
	        //   	tables = userView.getEmp_tablepriv().toString();
	        //	}else
	       		{
	           		fields = userView.getFieldpriv().toString();
	           		tables = userView.getTablepriv().toString();
	       		}
	       		if(fields==null || fields.length()<=0)
	         		fields = ",";
	       		if(tables==null || tables.length()<=0)
	         		tables = ",";  
      		}
	  	}
	  	
	  	EncryptLockClient lockclient = (EncryptLockClient)session.getServletContext().getAttribute("lock");
   		String license = lockclient.getLicenseCount();
   		int version = userView.getVersion();
   		if(license.equals("0"))
        	version = 100+version;
   		int usedday = lockclient.getUseddays();
   
%>
<%
   if("30".equals(status)){
	   int dataSize = myForm.getNoFillList().size();	
		int currentPage = myForm.getSetlistform().getPagination().getCurrent();
		int pagecount = myForm.getSetlistform().getPagination().getPageCount();
		int pages = myForm.getSetlistform().getPagination().getPages();
		String pagerows = String.valueOf(myForm.getPagerows());
		int lastIndex = pagecount;//当前页的最后一条	

		if(pages>1&&currentPage<pages)
			lastIndex = pagecount;
		else if(pages>1&&currentPage==pages)		
			lastIndex=dataSize-pagecount*(currentPage-1);
		else if(pages==1)
			lastIndex = dataSize;
		boolean zp=true;
%>
<script type="text/javascript">

// 驳回未填写的工作纪实
function rebutWorkPlan(opt,nbase,a0100,log_type,p0100,state,p0115,year_num,quarter_num,month_num,week_num,day_num)
{
	var hashvo = new ParameterSet();
	hashvo.setValue("opt",opt);
	hashvo.setValue("nbase",nbase);
	hashvo.setValue("a0100",a0100);
	hashvo.setValue("log_type",log_type);
	hashvo.setValue("p0100",p0100);
	hashvo.setValue("state",state);
	hashvo.setValue("p0115",p0115);	
	hashvo.setValue("year_num",year_num);
	hashvo.setValue("quarter_num",quarter_num);
	hashvo.setValue("month_num",month_num);
	hashvo.setValue("week_num",week_num);
	hashvo.setValue("day_num",day_num);	
	hashvo.setValue("returnURL","/performance/workplan/workplanteam/workplan_team_list.do?b_query=link&workType=${workPlanTeamForm.workType}&state=${workPlanTeamForm.state}&year=${workPlanTeamForm.year}&month=${workPlanTeamForm.month}&status=${workPlanTeamForm.status}&name=${workPlanTeamForm.name}&season=${workPlanTeamForm.season}&week=${workPlanTeamForm.week}&day=${workPlanTeamForm.day}&a_code=${workPlanTeamForm.a_code}&log_type=${workPlanTeamForm.log_type}&pagerows=${workPlanTeamForm.pagerows}&flag=1");		
	var request = new Request({method:'post',asynchronous:false,onSuccess:showTeamList,functionId:'90100170060'},hashvo);
}
function showTeamList(outparamters)
{
	var returnURL = outparamters.getValue("returnURL");	
	workPlanTeamForm.action=returnURL;
	workPlanTeamForm.submit();
}


/*
function writePlan(opt,nbase,a0100,log_type,p0100,state,p0115,year_num,quarter_num,month_num,week_num,day_num)
{
	workPlanViewForm.action="/performance/workplan/workplanview/workplan_view_list.do?b_write=write&mdopt="+opt+"&mdnbase="+nbase+"&mda0100="+a0100+"&log_type="+log_type+"&mdp0100="+p0100+"&state="+state+"&p0115="+p0115+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num;
	workPlanViewForm.submit();
}
*/
</script>
<html:form action="/performance/workplan/workplanteam/workplan_team_list"> 
<html:hidden property="workType" name="workPlanTeamForm"/>
<html:hidden property="state" name="workPlanTeamForm"/>
<input type='hidden' value="${workPlanTeamForm.isSelectedAll}"  name='isSelectedAll'>
  <table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
  <!-- 查询条件 -->
     <tr>
       <td colspan="<%=j %>" align="right" style="height:35px">
       <bean:message key="log.teamwork.workplan.year"/><hrms:optioncollection name="workPlanTeamForm" property="yearList" collection="list" />
						 <html:select name="workPlanTeamForm" property="year" size="1" style="width:70px;" onchange="changeQuery(1);">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>
				        &nbsp;&nbsp;			        
  <% 
     if("6".equals(workType)||"10".equals(workType)||"8".equals(workType)){
    	 %>
    	  <bean:message key="gz.acount.month"/><hrms:optioncollection name="workPlanTeamForm" property="monthList" collection="list" />
						 <html:select name="workPlanTeamForm" property="month" size="1" style="width:70px;" onchange="changeQuery(1);">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>
                        &nbsp;&nbsp;
  <%    }
  %>
  <% 
     if("4".equals(workType)){
    	 %>
    	 <bean:message key="log.teamwork.workplan.season"/><hrms:optioncollection name="workPlanTeamForm" property="seasonList" collection="list" />
						 <html:select name="workPlanTeamForm" property="season" size="1" style="width:80px;" onchange="changeQuery(1);">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>
                        &nbsp;&nbsp;
  <%    }
  %>
  <% 
     if("10".equals(workType)){
    	 %>
    	  <bean:message key="log.teamwork.workplan.week"/><hrms:optioncollection name="workPlanTeamForm" property="weekList" collection="list" />
						 <html:select name="workPlanTeamForm" property="week" size="1" style="width:70px;" onchange="changeQuery(1);">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>
                        &nbsp;&nbsp;
  <%    }
  %>
  <% 
     if("8".equals(workType)){
    	 %>
    	  <bean:message key="log.teamwork.workplan.day"/><hrms:optioncollection name="workPlanTeamForm" property="dayList" collection="list" />
						 <html:select name="workPlanTeamForm" property="day" size="1" style="width:70px;" onchange="changeQuery(1);">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>
                        &nbsp;&nbsp;
  <%    }
  %>
      
       <bean:message key="log.teamwork.workplan.status"/><hrms:optioncollection name="workPlanTeamForm" property="statusList" collection="list" />
						 <html:select name="workPlanTeamForm" property="status" size="1" style="width:70px;" onchange="changeQuery(1);">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>
				        &nbsp;&nbsp;
       <bean:message key="log.teamwork.workplan.logtype"/><hrms:optioncollection name="workPlanTeamForm" property="logtypeList" collection="list" />
					   <html:select name="workPlanTeamForm" property="log_type" size="1" style="width:75px;" onchange="changeQuery(1);">
				            <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				       </html:select>
       &nbsp;&nbsp;
	  <bean:message key="log.teamwork.workplan.name"/><html:text name="workPlanTeamForm" property="name" size="10"/>	
	  &nbsp;
	       <input type="button" onclick="changeQuery(1);" class="mybutton" value="<bean:message key="button.query"/>">		        	        
       </td>  
    </tr>
    <!-- 列 -->
    <tr>
      <td align="center" class="TableRow" nowrap>
        <Input type='checkbox' name='allOperat' onclick="noFillOperateCheckBox(this,'setlistform.select');" />
	  </td> 
      <td align="center" class="TableRow">
       <bean:message key="edit_report.year"/>
      </td>
     <% 
      if("6".equals(workType)||"10".equals(workType)||"8".equals(workType)){
     %>
      <td align="center" class="TableRow">
       <bean:message key="log.teamwork.workplan.cmonth"/>
      </td>
     <%    }
     %> 
     <% 
     if("4".equals(workType)){
    	 %>
      <td align="center" class="TableRow">
       <bean:message key="kpi.originalData.targetQuarter"/>
      </td>
      <%    }
     %>
     <% 
     if("10".equals(workType)){
    	 %>
      <td align="center" class="TableRow">
      <bean:message key="kq.wizard.week"/>
      </td>
      <%    }
     %>
     <% 
     if("8".equals(workType)){
    	 %>
      <td align="center" class="TableRow">
       <bean:message key="columns.archive.day"/>
      </td>
     <%    }
     %>
      <td align="center" class="TableRow">
       <bean:message key="train.setparam.mediaserver.mediaserver.type"/>
      </td>
      <td align="center" class="TableRow">
       <bean:message key="columns.archive.name"/>
      </td>
      <td align="center" class="TableRow">
       <bean:message key="kq.card.filtrate.start"/>
      </td>
      <td align="center" class="TableRow">
       <bean:message key="kq.card.filtrate.end"/>
      </td>
      <td align="center" class="TableRow">
       <bean:message key="selfinfo.status"/>
      </td>
      <td align="center" class="TableRow">
       <bean:message key="lable.portal.panel.operation"/>
      </td>
   </tr>
    <!-- 数据 -->
	    	<hrms:extenditerate id="element" name="workPlanTeamForm" property="setlistform.list" indexes="indexes"  pagination="setlistform.pagination" pageCount="<%=pagerows %>" scope="session">   	
	 <tr>
   		    <td align="center" class="RecordRow" nowrap>
   			<hrms:checkmultibox name="workPlanTeamForm" property="setlistform.select" value="true" indexes="indexes"/>
   			
   		    </td>
   		    <td class="RecordRow" nowrap align='left' >
	   		&nbsp;<bean:write name="element" property="year" />&nbsp;
	   		</td>
	   		<% 
            if("6".equals(workType)||"10".equals(workType)||"8".equals(workType)){
            %>
	   		<td class="RecordRow" nowrap align='left' >
	   		&nbsp;<bean:write name="element" property="month" />&nbsp;
	   		</td>
	   		<%      }
            %>
            <% 
		     if("4".equals(workType)){
		    	 %>
		      <td class="RecordRow" nowrap align='left' >
	   		  &nbsp;<bean:message key="reportinnercheck.di"/><bean:write name="element" property="season" /><bean:message key="kpi.originalData.targetQuarter"/>&nbsp;
	   		  </td>
		      <%    }
		     %>
		     <% 
		     if("10".equals(workType)){
		    	 %>
		      <td class="RecordRow" nowrap align='left' >
	   		  &nbsp;<bean:message key="reportinnercheck.di"/><bean:write name="element" property="week" /><bean:message key="kq.wizard.week"/>&nbsp;
	   		  </td>
		      <%    }
		     %>
		     <% 
		     if("8".equals(workType)){
		    	 %>
		      <td class="RecordRow" nowrap align='left' >
	   		  &nbsp;<bean:message key="reportinnercheck.di"/><bean:write name="element" property="day" /><bean:message key="columns.archive.day"/>&nbsp;
	   		  </td>
		     <%    }
		     %>
	   		<td class="RecordRow" nowrap align='left' >
	   		&nbsp;
	   		<logic:equal value="1" name="element" property="log_type">
	   		 <bean:message key="performance.workdiary.workplan"/>
	   		</logic:equal>
	   		<logic:equal value="2" name="element" property="log_type">
	   		 <bean:message key="performance.workdiary.worksummary"/>
	   		</logic:equal>
	   		&nbsp;
	   		</td>
	   		<td class="RecordRow" nowrap align='left' >
	   		&nbsp;<bean:write name="element" property="name" />&nbsp;
	   		</td>
	   		<td class="RecordRow" nowrap align='right' >
	   		&nbsp;<bean:write name="element" property="begin_time" />&nbsp;
	   		</td>
	   		<td class="RecordRow" nowrap align='right' >
	   		&nbsp;<bean:write name="element" property="end_time" />&nbsp;
	   		</td>
	   		<td class="RecordRow" nowrap align='center' >
	   		&nbsp;<bean:write name="element" property="status" />&nbsp;
	   		</td>
	   		<td class="RecordRow" nowrap align='center' >
	   		&nbsp;
           
	   		<logic:equal value="1" name="element" property="sp_flag">   
	   		    <input type="button" 
	   		    onclick='rebutWorkPlan("<bean:write name="element" property="opt" />"
	   		    ,"<bean:write name="element" property="nbase" />"
	   		    ,"<bean:write name="element" property="a0100" />"
	   		    ,"<bean:write name="element" property="log_type" />"
         		,"<bean:write name="element" property="p0100"/>"
         		,"<bean:write name="element" property="state"/>"
         		,"<bean:write name="element" property="p0115"/>"
         		,"<bean:write name="element" property="ayear"/>"
         		,"<bean:write name="element" property="aquarter" />"
		    	,"<bean:write name="element" property="amonth"/>"
         		,"<bean:write name="element" property="aweek"/>"
         		,"<bean:write name="element" property="aday" />");'
	   		     class="mybutton" value="<bean:message key="button.rejeect2"/>">
	   		</logic:equal>
	   		<logic:equal value="0" name="element" property="sp_flag">
         	<input type="button" class="mybutton" value="<bean:message key="button.rejeect2"/>" disabled/>
            </logic:equal>
	   		&nbsp;
	   		</td>
	  </tr>
	  </hrms:extenditerate>
	  <tr>
	  <td colspan="<%=j %>">
	    <table  width="100%"  class='RecordRowP'  align="center" >
		<tr>
		    <td valign="bottom" class="tdFontcolor"><bean:message key="hmuster.label.d"/>
		    
		    <bean:write name="workPlanTeamForm" property="setlistform.pagination.current" filter="true" />
					<bean:message key="hmuster.label.paper"/>
					<bean:message key="hmuster.label.total"/>
			
		    <bean:write name="workPlanTeamForm" property="setlistform.pagination.count" filter="true" />
		    
					
					<bean:message key="label.item"/>
					<bean:message key="hmuster.label.total"/>
			
		    <bean:write name="workPlanTeamForm" property="setlistform.pagination.pages" filter="true" />	
					<bean:message key="hmuster.label.paper"/>
					&nbsp;&nbsp;
			 <bean:message key="log.teamwork.workplan.show"/><html:text property="pagerows" name="workPlanTeamForm" size="3"></html:text>
			        
					 
			 <bean:message key="label.every.row"/>
			 &nbsp;&nbsp;
			 <a href="javascript:changeQuery(1);"><bean:message key="label.page.refresh"/></a>
			</td>
	        <td  align="right" nowrap class="tdFontcolor">
	               
				<p align="right"><hrms:paginationlink name="workPlanTeamForm" property="setlistform.pagination"
				nameId="setlistform" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
       </table>
	  </td>
	  </tr>
   </table>
   
</html:form>
<html:form action="/performance/workplan/workplanview/workplan_view_list">
<input type="hidden" name="returnURL" value="/performance/workplan/workplanteam/workplan_team_list.do?b_query=link&workType=${workPlanTeamForm.workType}&state=${workPlanTeamForm.state}&year=${workPlanTeamForm.year}&month=${workPlanTeamForm.month}&status=${workPlanTeamForm.status}&name=${workPlanTeamForm.name}&season=${workPlanTeamForm.season}&week=${workPlanTeamForm.week}&day=${workPlanTeamForm.day}&a_code=${workPlanTeamForm.a_code}&log_type=${workPlanTeamForm.log_type}&pagerows=${workPlanTeamForm.pagerows}&flag=1"/>
<input type="hidden" name="target" value="mil_body"/>
</html:form>
<script type="text/javascript">
///打开页面自动执行
 if(document.workPlanTeamForm.isSelectedAll.value=="1")
{		
		document.workPlanTeamForm.allOperat.checked=true;
    	setCheckState(1,'setlistform.select');
}
</script>
<%	  
  } else{
%>
<script type="text/javascript">

function initCard()
{
	var rl = document.getElementById("hostname").href;     
    var aurl = rl;//tomcat路径
    var DBType = "${workPlanTeamForm.dbType}";//1：mssql，2：oracle，3：DB2
    var UserName="<%=userName%>";   //登陆用户名     
    var obj = document.getElementById('CardPreview1');
    if(obj == null)
    	return false;
       
    var superUser="<%=superUser%>";
 // var menuPriv="<%=fields%>";
 // var tablePriv="<%=tables%>";
 	var menuPriv="";
    var tablePriv="";
    obj.SetSuperUser(superUser);  // 1为超级用户,0非超级用户
    obj.SetUserMenuPriv(menuPriv);  // 指标权限, 逗号分隔, 空表示全权
    obj.SetUserTablePriv(tablePriv);  // 子集权限, 逗号分隔, 空表示全权         
    obj.SetURL(aurl);
    obj.SetDBType(DBType);
    obj.SetUserName(UserName);
    obj.SetUserFullName("<%=userFullName%>");
    obj.SetHrpVersion("<%=version%>");
    obj.SetTrialDays("<%=usedday%>","30");
}

function showPrintCard()
{   
	var str="";
	for(var i=0;i<document.workPlanTeamForm.elements.length;i++)
	{
		if(document.workPlanTeamForm.elements[i].type=="checkbox")
		{					
			var ff = workPlanTeamForm.elements[i].name.substring(0,17);						
			if(document.workPlanTeamForm.elements[i].checked==true && ff=='pagination.select')
			{
				str = document.workPlanTeamForm.elements[i+1].value.split("&");											
			}
		}
	}
	if(str.length<=0)
	{
		alert(PERFORMANCE_WORKPLAN_WORKPLANTEAM_INFO01);
		return;
	}

  	var tab_id = "${workPlanTeamForm.print_id}"; 
  	if(tab_id==null || tab_id.length<=0 || tab_id=="-1")
  	{
    	alert(PERFORMANCE_WORKPLAN_WORKPLANTEAM_INFO02);
        return false;
  	}  
  	var obj = document.getElementById('CardPreview1');  
  	if(obj==null)
  	{
      	alert(PERFORMANCE_WORKPLAN_WORKPLANTEAM_INFO03);
      	return false;
  	}
  	obj.SetCardID("${workPlanTeamForm.print_id}");
  	obj.SetDataFlag("<CARDSTYLE>A</CARDSTYLE>"); /* <CARDSTYLE>A人员,B单位,K职位,R培训,P绩效</CARDSTYLE><TEMPLATEID>考核模板号</TEMPLATEID><PLANID>考核计划号</PLANID>*/       
  	obj.ClearObjs();
  	
  	for(var i=0;i<document.workPlanTeamForm.elements.length;i++)
	{
		if(document.workPlanTeamForm.elements[i].type=="checkbox")
		{					
			var ff = workPlanTeamForm.elements[i].name.substring(0,17);						
			if(document.workPlanTeamForm.elements[i].checked==true && ff=='pagination.select')
			{
				var arr = document.workPlanTeamForm.elements[i+1].value.split("&");							
				obj.AddObjId("<NBASE>"+arr[0]+"</NBASE><ID>"+arr[1]+"</ID><NAME>"+arr[2]+"</NAME><BIZDATE>"+arr[3]+"</BIZDATE>");
			}
		}
	} 
  	try { obj.SetJSessionId(AxManager.getJSessionId()); } catch(err) {}
  	obj.ShowCardModal();
}

function writePlan(opt,nbase,a0100,log_type,p0100,state,p0115,year_num,quarter_num,month_num,week_num,day_num)
{
	workPlanViewForm.action="/performance/workplan/workplanview/workplan_view_list.do?b_write=write&mdopt="+opt+"&mdnbase="+nbase+"&mda0100="+a0100+"&log_type="+log_type+"&mdp0100="+p0100+"&state="+state+"&p0115="+p0115+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num;
	workPlanViewForm.submit();
}
function batApply()
{
    var str="";
	for(var i=0;i<document.workPlanTeamForm.elements.length;i++)
	{
		if(document.workPlanTeamForm.elements[i].type=="checkbox")
		{					
			var ff = workPlanTeamForm.elements[i].name.substring(0,17);						
			if(document.workPlanTeamForm.elements[i].checked==true && ff=='pagination.select')
			{
				str = document.workPlanTeamForm.elements[i+1].value.split("&");											
			}
		}
	}
	if(str.length<=0)
	{
		alert(PERFORMANCE_WORKPLAN_WORKPLANTEAM_INFO04);
		return;
	}
	var thecodeurl="/performance/workplan/workplanteam/choose_opt_flag.jsp";
    var return_vo= window.showModalDialog(thecodeurl, "_blank", 
              "dialogHeight:250px;dialogWidth:450px;center:yes;help:no;resizable:yes;status:no;scroll:no;");
    if(!return_vo)
	    return;
	if(return_vo.flag=="true")  
    { 
	    var strids = "";
	    for(var i=0;i<document.workPlanTeamForm.elements.length;i++)
		{
			if(document.workPlanTeamForm.elements[i].type=="checkbox")
			{					
				var ff = workPlanTeamForm.elements[i].name.substring(0,17);						
				if(document.workPlanTeamForm.elements[i].checked==true && ff=='pagination.select')
				{
					var arr = document.workPlanTeamForm.elements[i+1].value.split("&");							
					strids+=arr[4]+"@"+arr[5]+"@"+arr[1]+"@"+arr[0]+"/";
				}
			}
		}
		if(return_vo.spContent==''){
		   alert(PERFORMANCE_WORKPLAN_WORKPLANTEAM_INFO05);
		   return;
		}
		var hashvo=new ParameterSet();
		hashvo.setValue("strids",strids); 
		hashvo.setValue("spContent",return_vo.spContent);
		var request=new Request({asynchronous:false,onSuccess:successFunc,functionId:'90100170061'},hashvo);
	}   
}
function successFunc(outparameters)
{
   var msg = getDecodeStr(outparameters.getValue("msg"));
   if(msg=='error'){
      alert(PERFORMANCE_WORKPLAN_WORKPLANTEAM_INFO06);
      return;
   }
   if(!confirm(PERFORMANCE_WORKPLAN_WORKPLANTEAM_INFO07)){
      return;
   }	
   workPlanTeamForm.action="/performance/workplan/workplanteam/workplan_team_list.do?b_query=link&workType=${workPlanTeamForm.workType}&state=${workPlanTeamForm.state}&year=${workPlanTeamForm.year}&month=${workPlanTeamForm.month}&status=${workPlanTeamForm.status}&name=${workPlanTeamForm.name}&season=${workPlanTeamForm.season}&week=${workPlanTeamForm.week}&day=${workPlanTeamForm.day}&a_code=${workPlanTeamForm.a_code}&log_type=${workPlanTeamForm.log_type}&pagerows=${workPlanTeamForm.pagerows}&flag=1";
   workPlanTeamForm.target="mil_body";
   workPlanTeamForm.submit(); 
}
</script>
<html:form action="/performance/workplan/workplanteam/workplan_team_list"> 
<a href="<%=url_p%>" style="display:none" id="hostname">for vpn</a>
<html:hidden property="workType" name="workPlanTeamForm"/>
<html:hidden property="state" name="workPlanTeamForm"/>
<input type='hidden' value="${workPlanTeamForm.isSelectedAll}"  name='isSelectedAll'>
  <table width="92%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
  <!-- 查询条件 -->
     <tr>
       <td colspan="<%=i %>" margin-left="20px" align="left" style="height:35px"><div style="margin-left:10px">
       <bean:message key="log.teamwork.workplan.year"/><hrms:optioncollection name="workPlanTeamForm" property="yearList" collection="list" />
						 <html:select name="workPlanTeamForm" property="year" size="1" style="width:70px;" onchange="changeQuery(1);">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>
				        &nbsp;&nbsp;			        
  <% 
     if("6".equals(workType)||"10".equals(workType)||"8".equals(workType)){
    	 %>
    	  <bean:message key="gz.acount.month"/><hrms:optioncollection name="workPlanTeamForm" property="monthList" collection="list" />
						 <html:select name="workPlanTeamForm" property="month" size="1" style="width:70px;" onchange="changeQuery(1);">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>
                        &nbsp;&nbsp;
  <%    }
  %>
  <% 
     if("4".equals(workType)){
    	 %>
    	 <bean:message key="log.teamwork.workplan.season"/><hrms:optioncollection name="workPlanTeamForm" property="seasonList" collection="list" />
						 <html:select name="workPlanTeamForm" property="season" size="1" style="width:80px;" onchange="changeQuery(1);">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>
                        &nbsp;&nbsp;
  <%    }
  %>
  <% 
     if("10".equals(workType)){
    	 %>
    	  <bean:message key="log.teamwork.workplan.week"/><hrms:optioncollection name="workPlanTeamForm" property="weekList" collection="list" />
						 <html:select name="workPlanTeamForm" property="week" size="1" style="width:70px;" onchange="changeQuery(1);">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>
                        &nbsp;&nbsp;
  <%    }
  %>
  <% 
     if("8".equals(workType)){
    	 %>
    	  <bean:message key="log.teamwork.workplan.day"/><hrms:optioncollection name="workPlanTeamForm" property="dayList" collection="list" />
						 <html:select name="workPlanTeamForm" property="day" size="1" style="width:70px;" onchange="changeQuery(1);">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>
                        &nbsp;&nbsp;
  <%    }
  %>
      
       <bean:message key="log.teamwork.workplan.status"/><hrms:optioncollection name="workPlanTeamForm" property="statusList" collection="list" />
						 <html:select name="workPlanTeamForm" property="status" size="1" style="width:70px;" onchange="changeQuery(1);">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>
				        &nbsp;&nbsp;
       <bean:message key="log.teamwork.workplan.logtype"/><hrms:optioncollection name="workPlanTeamForm" property="logtypeList" collection="list" />
						 <html:select name="workPlanTeamForm" property="log_type" size="1" style="width:75px;" onchange="changeQuery(1);">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>
				        &nbsp;&nbsp;
		<%-- 2016/1/21 wangjl 将姓名查询改成 姓名，领导批示，工作内容的查询 --%>
	  <%--<bean:message key="log.teamwork.workplan.name"/>--%>
	  <logic:equal value="" name="workPlanTeamForm" property="name">
	  <html:text name="workPlanTeamForm" style="color:DDDDDD" value="请输入姓名、批示、内容" onfocus="notext(this);"  onblur="addtext(this)" property="name" size="25"/>	
	  &nbsp;
	  </logic:equal>
	  <logic:notEqual value="" name="workPlanTeamForm" property="name">
	  <logic:equal value="请输入姓名、批示、内容" name="workPlanTeamForm" property="name">
	  <html:text name="workPlanTeamForm" style="color:DDDDDD" onfocus="notext(this);"  onblur="addtext(this)" property="name" size="25"/>
	  </logic:equal>
	  <logic:notEqual value="请输入姓名、批示、内容" name="workPlanTeamForm" property="name">
	  <html:text name="workPlanTeamForm"  onfocus="notext(this);"  onblur="addtext(this)" property="name" size="25"/>	
	  &nbsp;
	  </logic:notEqual>
	  </logic:notEqual>
	       <input type="button" onclick="changeQuery(1);" class="mybutton" value="<bean:message key="button.query"/>">
	     <%if(!"all".equals(myForm.getLog_type()) && "03".equals(myForm.getStatus())){ %>		        
	  &nbsp;
	       <input type="button" onclick="showPrintCard();" class="mybutton" value="<bean:message key="button.print"/>">		        		        
       <%} %> 
       <%if("31".equals(myForm.getStatus())){ %>	
       <input type="button" onclick="batApply();" class="mybutton" value="<bean:message key="button.batapply"/>">
       <%} %> 
        </td>
        </div>
    </tr>
    
    <!-- 列 -->
    <tr>
      <td align="center" class="TableRow" nowrap >
        &nbsp;<Input type='checkbox' name='allOperat' onclick='operateCheckBox(this)' />&nbsp;
	  </td> 
      <td align="center" class="TableRow">
       &nbsp;<bean:message key="edit_report.year"/>&nbsp;
      </td>
     <% 
      if("6".equals(workType)||"10".equals(workType)||"8".equals(workType)){
     %>
      <td align="center" class="TableRow">
       &nbsp;<bean:message key="log.teamwork.workplan.cmonth"/>&nbsp;
      </td>
     <%    }
     %> 
     <% 
     if("4".equals(workType)){
    	 %>
      <td align="center" class="TableRow">
       &nbsp;<bean:message key="kpi.originalData.targetQuarter"/>&nbsp;
      </td>
      <%    }
     %>
     <% 
     if("10".equals(workType)){
    	 %>
      <td align="center" class="TableRow">
       &nbsp;<bean:message key="kq.wizard.week"/>&nbsp;
      </td>
      <%    }
     %>
     <% 
     if("8".equals(workType)){
    	 %>
      <td align="center" class="TableRow">
       &nbsp;<bean:message key="columns.archive.day"/>&nbsp;
      </td>
     <%    }
     %>
      <td align="center" class="TableRow">
       &nbsp;<bean:message key="train.setparam.mediaserver.mediaserver.type"/>&nbsp;
      </td>
      <td align="center" class="TableRow">
       &nbsp;<bean:message key="columns.archive.name"/>&nbsp;
      </td>
      <td align="center" class="TableRow">
       &nbsp;<bean:message key="kq.card.filtrate.start"/>&nbsp;
      </td>
      <td align="center" class="TableRow">
       &nbsp;<bean:message key="kq.card.filtrate.end"/>&nbsp;
      </td>
      <td align="center" class="TableRow">
       &nbsp;<bean:message key="selfinfo.status"/>&nbsp;
      </td>
      <td align="center" class="TableRow">
       &nbsp;<bean:message key="lable.portal.panel.operation"/>&nbsp;
      </td>
   </tr>
    <!-- 数据 -->
	    <hrms:paginationdb id="element" name="workPlanTeamForm" sql_str="${workPlanTeamForm.str_sql}" table="" where_str="${workPlanTeamForm.str_whl}"  order_by="${workPlanTeamForm.order_str}" columns="${workPlanTeamForm.columns}"  page_id="pagination" pagerows="${workPlanTeamForm.pagerows}" indexes="indexes">
	    <tr>
   		    <td align="center" class="RecordRow" nowrap>
   			<hrms:checkmultibox name="workPlanTeamForm" property="pagination.select" value="true" indexes="indexes"/>
   			<Input type='hidden' value='<bean:write name="element" property="nbase" filter="true"/>&<bean:write name="element" property="a0100" filter="true"/>&<bean:write name="element" property="a0101" filter="true"/>&<bean:write name="element" property="p0104" filter="true"/>&<bean:write name="element" property="p0100" filter="true"/>&<bean:write name="element" property="log_type" filter="true"/>' />
   		    </td>
   		    <td class="RecordRow" nowrap align='left' >
	   		&nbsp;<bean:write name="element" property="ayear" />&nbsp;
	   		</td>
	   		<% 
            if("6".equals(workType)||"8".equals(workType)){
            %>
	   		<td class="RecordRow" nowrap align='left' >
	   		&nbsp;<bean:write name="element" property="amonth" />&nbsp;
	   		</td>
	   		<%      }
            %>
            <% 
		     if("4".equals(workType)){
		    	 %>
		      <td class="RecordRow" nowrap align='left' >
	   		  &nbsp;<bean:message key="reportinnercheck.di"/><bean:write name="element" property="aquarter" /><bean:message key="kq.wizard.quarter"/>&nbsp;
	   		  </td>
		      <%    }
		     %>
		      
	   		  <%
	   	      LazyDynaBean weekbean=(LazyDynaBean)pageContext.getAttribute("element");
		      String monthIndex = "";
		      String weekIndex = "";
				Set keySet=weekMap.keySet();
				  java.util.Iterator t=keySet.iterator();
				  
					while(t.hasNext())
					{
						String strKey = (String)t.next();  //键值	    
						String strValue = (String)weekMap.get(strKey);   //value值  
						String strDate = strKey.replace("-",".");
						if(strDate.equals((String)weekbean.get("p0104"))){
							//2015/12/22 wangjl 【|】做分隔符，split需要加上两个斜杠【\\】
							String[] strArray = strValue.split("\\|");
							//2015/12/22 wangjl 数组越界异常
							monthIndex = strArray[0];
							weekIndex = strArray[1];
							break;
						}
					}
	   		%>	   
		     <% 
		     if("10".equals(workType)){
		    	 %>
		    <td class="RecordRow" nowrap align='left' >
	   		   &nbsp;<%=monthIndex%>&nbsp;
	   		</td>
		      <td class="RecordRow" nowrap align='left' >
	   		  &nbsp;<bean:message key="reportinnercheck.di"/> 		  
	   		  <%=weekIndex%>
	   		  <bean:message key="kq.wizard.week"/>&nbsp;
	   		  </td>
		      <%    }
		     %>
		     <% 
		     if("8".equals(workType)){
		    	 %>
		      <td class="RecordRow" nowrap align='left' >
	   		  &nbsp;<bean:message key="reportinnercheck.di"/><bean:write name="element" property="aday" /><bean:message key="columns.archive.day"/>&nbsp;
	   		  </td>
		     <%    }
		     %>
	   		<td class="RecordRow" nowrap align='left' >
	   		&nbsp;
	   		<logic:equal value="1" name="element" property="log_type">
	   		 <bean:message key="performance.workdiary.workplan"/>
	   		</logic:equal>
	   		<logic:equal value="2" name="element" property="log_type">
	   		<bean:message key="performance.workdiary.worksummary"/>
	   		</logic:equal>
	   		&nbsp;
	   		</td>
	   		<td class="RecordRow" nowrap align='left' >
	   		&nbsp;<bean:write name="element" property="a0101" />&nbsp;
	   		</td>
	   		<td class="RecordRow" nowrap align='right' >
	   		&nbsp;<bean:write name="element" property="p0104" />&nbsp;
	   		</td>
	   		<td class="RecordRow" nowrap align='right' >
	   		&nbsp;<bean:write name="element" property="p0106" />&nbsp;
	   		</td>
	   		<td class="RecordRow" nowrap align='center' > 
	   		<hrms:codetoname codeid="23" name="element"
										codevalue="p0115" codeitem="codeitem" scope="page" />&nbsp;
				<bean:write name="codeitem" property="codename" />&nbsp;
	   		</td>
	   		<%
	   		  LazyDynaBean abean2=(LazyDynaBean)pageContext.getAttribute("element");
		      String id=(String)abean2.get("p0100");
		      String flag = (String)spMap.get(id);
		      String mdopt0 = SafeCode.encode(PubFunc.convertTo64Base("0"));
		      String mdopt3 = SafeCode.encode(PubFunc.convertTo64Base("3"));
		      String mdnbase= SafeCode.encode(PubFunc.convertTo64Base((String)abean2.get("nbase")));
		      String mda0100= SafeCode.encode(PubFunc.convertTo64Base((String)abean2.get("a0100")));
		      String mdp0100= SafeCode.encode(PubFunc.convertTo64Base((String)abean2.get("p0100")));
		      if(flag!=null){
		    	  %>
		    	 <td class="RecordRow" nowrap align='center' >
	   		    &nbsp;<input type="button" 
	   		    onclick='writePlan("<%=mdopt3 %>","<%=mdnbase %>","<%=mda0100 %>","<bean:write name="element" property="log_type" />"
         		,"<%=mdp0100 %>","<bean:write name="element" property="state"/>","<bean:write name="element" property="p0115"/>","<bean:write name="element" property="ayear"/>"
         		,"<bean:write name="element" property="aquarter" />",
         		<% 
		         if(!"10".equals(workType)){
		    	 %>"<bean:write name="element" property="amonth"/>"
		    	 <%}else{ %>
		    	   "<%=monthIndex %>"
		    	 <%} %>
		    	
         		,<% 
		         if(!"10".equals(workType)){
		    	 %>"<bean:write name="element" property="aweek"/>"
		    	 <%}else{ %>
		    	   "<%=weekIndex %>"
		    	 <%} %>
         		,"<bean:write name="element" property="aday" />");'
	   		     class="mybutton" value="<bean:message key="button.apply"/>">&nbsp;
	   		    </td> 
		    <%	  
		      } else{
		    	  %>
		    	 <td class="RecordRow" nowrap align='center' >
	   		    &nbsp;<input type="button" 
	   		    onclick='writePlan("<%=mdopt0 %>","<%=mdnbase %>","<%=mda0100 %>","<bean:write name="element" property="log_type" />"
         		,"<%=mdp0100 %>","<bean:write name="element" property="state"/>","<bean:write name="element" property="p0115"/>","<bean:write name="element" property="ayear"/>"
         		,"<bean:write name="element" property="aquarter" />",
         		<% 
		         if(!"10".equals(workType)){
		    	 %>"<bean:write name="element" property="amonth"/>"
		    	 <%}else{ %>
		    	   "<%=monthIndex %>"
		    	 <%} %>
		    	
         		,<% 
		         if(!"10".equals(workType)){
		    	 %>"<bean:write name="element" property="aweek"/>"
		    	 <%}else{ %>
		    	   "<%=weekIndex %>"
		    	 <%} %>
         		,"<bean:write name="element" property="aday" />");'
	   		    class="mybutton" value="<bean:message key="label.view"/>">&nbsp;
	   		    </td>
		    <%	  
		      }
	   		%>
	  </tr>
	  </hrms:paginationdb>  
	  <tr>
	  <td colspan="<%=i %>">
	   <table  width="100%"  class='RecordRowP'  align="center" >
		<tr>
		    <td valign="bottom" class="tdFontcolor"><bean:message key="hmuster.label.d"/>
		    
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="hmuster.label.paper"/>
					<bean:message key="hmuster.label.total"/>
			
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.item"/>
					<bean:message key="hmuster.label.total"/>
					
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="hmuster.label.paper"/>
					&nbsp;&nbsp;
			 <bean:message key="log.teamwork.workplan.show"/><html:text property="pagerows" name="workPlanTeamForm" size="3"></html:text>
			       
		
			 <bean:message key="label.every.row"/>&nbsp;&nbsp;
			 <a href="javascript:changeQuery(1);"><bean:message key="label.page.refresh"/></a>
			</td>
	        <td  align="right" nowrap class="tdFontcolor">
				<p align="right">
				<hrms:paginationdblink name="workPlanTeamForm" property="pagination" nameId="workPlanTeamForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
       </table>
	  </td>
	  </tr>
   </table>
</html:form>

<html:form action="/performance/workplan/workplanview/workplan_view_list">
<input type="hidden" name="returnURL" value="/performance/workplan/workplanteam/workplan_team_list.do?b_query=link&workType=${workPlanTeamForm.workType}&state=${workPlanTeamForm.state}&year=${workPlanTeamForm.year}&month=${workPlanTeamForm.month}&status=${workPlanTeamForm.status}&name=${workPlanTeamForm.name}&season=${workPlanTeamForm.season}&week=${workPlanTeamForm.week}&day=${workPlanTeamForm.day}&a_code=${workPlanTeamForm.a_code}&log_type=${workPlanTeamForm.log_type}&pagerows=${workPlanTeamForm.pagerows}&flag=1"/>
<input type="hidden" name="target" value="mil_body"/>
</html:form>
<script type="text/javascript">
///打开页面自动执行
 if(document.workPlanTeamForm.isSelectedAll.value=="1")
{		
		document.workPlanTeamForm.allOperat.checked=true;
    	for(var i=0;i<document.workPlanTeamForm.elements.length;i++)
   		{
   			if(document.workPlanTeamForm.elements[i].type=='checkbox'&&document.workPlanTeamForm.elements[i].name.length>18&&document.workPlanTeamForm.elements[i].name.substring(0,18)=='pagination.select[')
   				document.workPlanTeamForm.elements[i].checked=true;
   		}
}
//2016/1/20 wangjl 
 function notext(dd){
	 dd.value="";
	 dd.style.color="black";
 }
 function addtext(ad){
	 if(ad.value==""){
		 ad.value="请输入姓名、批示、内容";
		 ad.style.color="#DDDDDD";
		 }
 }

initCard();
</script>
<%	  
  }
%>

