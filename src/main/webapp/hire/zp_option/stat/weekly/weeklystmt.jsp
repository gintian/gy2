<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.ArrayList,com.hjsj.hrms.actionform.hire.zp_option.stat.weekly.WeeklyStmtForm" %>
<%@ page import="com.hrms.struts.valueobject.UserView,
				com.hrms.struts.constant.WebConstant" %>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>

<script language="javascript" src="/js/validate.js"></script>
<script language="JavaScript" src="/anychart/js/AnyChart.js"></script>
<%
  
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String unitid="";
	if(!userView.isSuper_admin())
		//unitid=userView.getUnit_id();
	{
		unitid=userView.getUnitIdByBusi("7");
		unitid = PubFunc.getTopOrgDept(unitid);
		if(unitid.trim().length()==3)
		{
			unitid="-1";
		}
		else if(unitid.indexOf("`")==-1&&unitid.trim().length()>2){
			unitid=unitid.substring(2);
		}
	}
		 WeeklyStmtForm weeklyStmtForm = (WeeklyStmtForm)session.getAttribute("weeklyStmtForm");
 		ArrayList list = weeklyStmtForm.getListView();
 		
	
%>
<script type="text/javascript" language="javascript">
var depids="${weeklyStmtForm.depid}";
function searchWeek(){
	weeklyStmtForm.action="/hire/zp_option/stat/weekly/weeklystmt.do?b_query=link";
	weeklyStmtForm.submit();
}
function jobchange(){
	depids=document.weeklyStmtForm.depid.value;
	if(depids.length==0)
		return;
	var hirePathobj = 	document.weeklyStmtForm.hirePath;
	var hirepath;
	for(var i=0;i<hirePathobj.options.length;i++)
   		{
   			if(hirePathobj.options[i].selected)
   				hirepath=hirePathobj.options[i].value;
   		}
	var hashvo=new ParameterSet();
	hashvo.setValue("operator","4");
	hashvo.setValue("hirepath",hirepath);
	hashvo.setValue("id",'${weeklyStmtForm.id}');
	var In_paramters="orgID="+depids; 
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'3000000174'},hashvo);
}
	
function returnInfo(outparamters)
{
	var poslist=outparamters.getValue("poslist");	
	var aa=document.getElementsByName("jobid");
	AjaxBind.bind(aa[0],poslist);
}
function openInputCodeDialog_self(codeid,mytarget) 
{
    var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
    
    if(mytarget==null)
      return;
    var oldInputs=document.getElementsByName(mytarget);
    
    oldobj=oldInputs[0];
    //根据代码显示的对象名称查找代码值名称	
    target_name=oldobj.name;
    hidden_name=target_name.replace("dep","depid");
    var hiddenInputs=document.getElementsByName(hidden_name);
    if(hiddenInputs!=null)
    {
    	hiddenobj=hiddenInputs[0];
    	codevalue="";
    }
    codevalue='<%=(unitid.length()>=2?unitid:"")%>'
   var theArr=new Array(codeid,codevalue,oldobj,hiddenobj,1,0);
   thecodeurl="/system/codeselectposinputpos.jsp?codesetid="+codeid+"&codeitemid=&isfirstnode=1";
    var popwin= window.showModalDialog(thecodeurl, theArr, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
	var aa=document.getElementsByName("dep");
	aa[0].fireEvent("onChange");
}	
   function setqd(obj)
   {
   		var value='';
   		for(var i=0;i<obj.options.length;i++)
   		{
   			if(obj.options[i].selected)
   				value=obj.options[i].value;
   		}
   		if(value=='01')
   		{
	 	   //  var obj2=eval("document.employPositionForm.posID");
   		     document.getElementById("pos_str2").style.display='block';
   		       document.getElementById("pos_str").style.display='none';
   		         document.getElementById("pos_str3").style.display='none';
 				  document.getElementById("pos_str4").style.display='none';
	 	}
	 	else{
	 		 document.getElementById("pos_str").style.display='block';
   		       document.getElementById("pos_str2").style.display='none';
	 		   document.getElementById("pos_str4").style.display='none';
	 		    document.getElementById("pos_str3").style.display='none';
	 		}
	 	
	var depids=document.weeklyStmtForm.depid.value;
	
	if(depids.length==0)
		return;
		
	var hashvo=new ParameterSet();
	hashvo.setValue("operator","4");
	hashvo.setValue("hirepath",value);
	hashvo.setValue("id",'${weeklyStmtForm.id}');
	var In_paramters="orgID="+depids;  
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'3000000174'},hashvo);
   }
   	function onValid(){
	   var aa=document.getElementsByName("jobid");
	   if(aa[0].value==''){
	   alert("不存在岗位名称或者应聘专业！");
	   document.returnValue=false;
	   }
	   
	}
	function setTPinput(){
	    var InputObject=document.getElementsByTagName("input");
	    for(var i=0;i<InputObject.length;i++){
	        var InputType=InputObject[i].getAttribute("type");
	        if(InputType!=null&&(InputType=="text"||InputType=="password")){
	            InputObject[i].className=" "+"TEXT4";
	        }
	    }
    }
</script>

<style type="text/css">
<!--
.list_tb{width:100%;_width:94%;border-collapse:collapse; clear:both}
.list_tb a:visited{color:#660066; text-decoration:underline}
.list_tb a:hover{color:#FE6700;text-decoration:none}
.list_tb th,.list_tb .th td{
	background-position : center left;
	font-size: 12px;  
	BORDER-BOTTOM: #C4D8EE 0pt solid; 
	BORDER-LEFT: #C4D8EE 0pt solid; 
	BORDER-RIGHT: #C4D8EE 0pt solid; 
	BORDER-TOP: #C4D8EE 0pt solid;
	height:22px;
	font-weight: bold;
	background-color:#f4f7f7;	
	/*
	color:#336699;
	*/
	valign:middle;
}
.list_tb .th2 td{background:#FEFFD7;height:25px;line-height:25px}
.list_tb td{border-bottom:1px solid #DEE0EB;height:30px;line-height:16px;padding:2px 4px 2px 5px}
.list_tb a{margin-right:5px}
.list_tb th.chk{padding:0 4px 0 4px;width:20px; vertical-align:middle}
.list_tb th.icon{width:35px}
.list_tb th.time{width:75px}
.list_tb td.chk{text-align:center;width:20px;}
.list_tb td.icon{text-align:center}
.list_tb th.num{text-align:right;padding-right:5px}
.list_tb td.num{text-align:right;padding-right:5px}
.list_tb td.total{font-weight:bolder}
.con{
	color:#000;
	font-family: "宋体", Verdana;
	font-weight: bold;
	font-size: 14px
}
.weekstmt{
	color:#000;
	font-family: "宋体", Verdana;
	font-weight: bold;
	font-size: 13px
}

-->
</style>
<hrms:themes></hrms:themes>
<body onload="setTPinput()">
<table>
<tr><td>
<html:form action="/hire/zp_option/stat/weekly/weeklystmt">
<table width="98%" border="0" cellspacing="0" cellpadding="0" align="center">
	<tr>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td class="con">&nbsp;&nbsp;<bean:message key="workdiary.message.job.stat.week"/></td>
	</tr>
	<tr height="30">
		<td>&nbsp;</td>
	</tr>
</table>
<table width="98%" border="0" cellspacing="0"  cellpadding="0" align="center">
	<tr>
		<td width="5%">&nbsp;</td>
		<td class="weekstmt" width="35%"><bean:message key="workdiary.message.set.stat.range"/></td>
		<td>&nbsp;</td>
	</tr>
	<table style="margin-left:60px; margin-top:5px;" >
	<tr>
	<td  nowrap valign="top" height="22px">&nbsp;</td>
	<td  nowrap valign="top" height="22px" width="15%">
		招聘渠道&nbsp;&nbsp;
	</td>
	<td  nowrap valign="top" align="left">
	<html:select name="weeklyStmtForm" property="hirePath" size="1"  onchange='setqd(this)'  >
                              <html:optionsCollection property="hirePathList" value="dataValue" label="dataName"/>
        			</html:select>
    </td>
	  </tr>
	  
  <tr>
  	<td  nowrap valign="top" height="22px">&nbsp;</td>
	<td  nowrap  height="22px">
		<bean:message key="workbench.pos.posname"/>&nbsp;&nbsp;<!-- 所属部门 -->
	</td>
	<td  nowrap  height="22px">
	<html:hidden name="weeklyStmtForm" property='depid' onchange="jobchange()"/> 
     <html:text name="weeklyStmtForm" property='dep' style="height:22px;"  onchange="fieldcode(this,2)"/> 
     	<span>
         <img  src="/images/code.gif"  style="position:relative;top:5px;" onclick='javascript:openInputCodeDialog_self("UM","dep");'/>&nbsp;
        </span>
	  	<!-- 
         <html:hidden name="weeklyStmtForm" property='depid' onchange="jobchange()"/>  
         &nbsp;<html:text name="weeklyStmtForm" property='dep' styleClass="textColorWrite" onchange="fieldcode(this,2)"/> 
         <img  src="/images/code.gif" onclick='javascript:hireZpOption("UN","dep","","1");'/>&nbsp;
          -->	
    </td>
  </tr>
  <tr>
  	<td  nowrap valign="top" height="22px">&nbsp;</td>
  	<logic:equal value="01" name="weeklyStmtForm" property="hirePath">
  	<td align="left"  height="22px" nowrap valign="top" id='pos_str3'  style='display:block;'  >
    	<bean:message key="hire.employActualize.interviewProfessional"/>&nbsp;&nbsp;<!-- 应聘专业 -->
    </td>
    <td align="left"  height="22px" nowrap valign="top" id='pos_str4' style='display:none;' >
    	  	<bean:message key="column.sys.pos"/>&nbsp;&nbsp;
    </td>
  	</logic:equal>
  	<logic:notEqual value="01" name="weeklyStmtForm" property="hirePath">
  	<td align="left"   height="22px" nowrap valign="top" id='pos_str3'  style='display:none;'  >
    	<bean:message key="hire.employActualize.interviewProfessional"/>&nbsp;&nbsp;
    </td>
  	<td align="left"   height="22px" nowrap valign="top" id='pos_str4' style='display:block;' >
    	  	<bean:message key="column.sys.pos"/>&nbsp;&nbsp;<!-- 岗位名称 -->
    </td>
  	</logic:notEqual>
  	<td align="left"  height="22px"  nowrap valign="top" id='pos_str' style='display:none;'   >
    	<bean:message key="column.sys.pos"/>&nbsp;&nbsp;
    </td>
  	<td align="left"   height="22px" nowrap valign="top" id='pos_str2' style='display:none;' >
    	<bean:message key="hire.employActualize.interviewProfessional"/>&nbsp;&nbsp;
    </td>
    <td  height="22px" >

    	<html:select name="weeklyStmtForm" property='jobid' size="1">
                              <html:optionsCollection property="jobidlist" value="dataValue" label="dataName"/>
        			</html:select> 
      <!-- 
<html:hidden name="weeklyStmtForm" property='jobid'/>  
         &nbsp;<html:text name="weeklyStmtForm" property='job' styleClass="textColorWrite" onchange="fieldcode(this,2)"/> 
         	<img  src="/images/code.gif" onclick='javascript:hireZpOption("@K","job",depids,"2");'/>&nbsp;
         	-->	
    </td>
  </tr>
  <tr>
  	<td  height="22px">&nbsp;</td>
    <td  height="22px">
        <bean:message key="label.by.time.domain"/>&nbsp;&nbsp;<!-- 按时间段 -->
        </td>
        <td  height="22px">
           <bean:message key="label.from"/>
   	  	 	<input type="text" name="start_date" value="${weeklyStmtForm.start_date}" extra="editor"  style="width:100px;font-size:10pt;text-align:left" id="start_date"  dropDown="dropDownDate">
   	  	 	<bean:message key="label.to"/>
   	  	 	<input type="text" name="end_date"  value="${weeklyStmtForm.end_date}" extra="editor" style="width:100px;font-size:10pt;text-align:left" id="end_date"  dropDown="dropDownDate">
       </td>
  </tr>
  <tr>
  	<td  height="22px">&nbsp;</td>
  	<td  height="22px">&nbsp;</td>
  	<td  height="22px">
  		<hrms:submit styleClass="mybutton"  property="b_query" onclick="document.weeklyStmtForm.target='_self';onValid();return (document.returnValue && ifqrbc());" >
                  <bean:message key="hire.zp_option.weekly.button"/>
	    </hrms:submit> 
  	</td>
  </tr>
  </table>
</table>
</html:form>
<hr>
<table width="100%" border="0" cellspacing="0" cellpadding="0" align="center" style="margin-top:5px">
<tr><td>&nbsp;</td></tr>
<tr>
<%for(int i=0;i<list.size();i++){ 
   String title="";
   if(i==0)
       title=weeklyStmtForm.getView_title();
   else
       title=weeklyStmtForm.getApp_title();
   ArrayList alist = (ArrayList)list.get(i);
   weeklyStmtForm.setListApp(alist);
%>
  <td width="50%" align="center" id='<%="pnl_"+i %>'>
   	<hrms:chart name="weeklyStmtForm" title="<%=title%>" scope="session" legends="listApp" data=""  width="500" height="580" chart_type="11" chartpnl='<%="pnl_"+i %>'>
</hrms:chart>
  </td>
  <%} %>
</tr>
</table>
<p>
<table align="center" class="list_tb">
	<tr>
		<th nowrap="nowrap" class="common_background_color" width="40%"><bean:message key="hire.zp_option.weekly.week"/></th>
		<th  class="num common_background_color" nowrap="nowrap" width="15%"><bean:message key="hire.zp_option.weekly.Viewed"/></th>
		<th  class="num common_background_color" nowrap="nowrap" width="15%"><bean:message key="hire.zp_option.weekly.apped"/></th>
		<th  class="num common_background_color" nowrap="nowrap" width="15%"><bean:message key="hire.zp_option.weekly.apps"/></th>
		<th  class="num common_background_color" nowrap="nowrap" width="15%">&nbsp;</th>
	</tr>
	<logic:iterate id="line" name="weeklyStmtForm" property="weeklystmt" type="java.util.Map"> 
	 <tr  class='trShallow'>
		<td class="common_border_color"><%=line.get("weeklyday")%></td>
		<td class="num common_border_color"><%=line.get("view")%></td>
		<td class="num common_border_color"><%=line.get("app")%></td>
		<%if(!line.get("apps").equals("0.000%")){%>
		<td class="num common_border_color"><%=line.get("apps")%></td>
		<td class="common_border_color"><a href='/hire/zp_option/stat/weekly/weeklyday.do?b_query=link&theweekday=<%=line.get("weeklyday")%>&id=${weeklyStmtForm.id}'>
			<bean:message key="hire.zp_option.weekly.day"/></a></td>
		<%}else{%>
		<td class="num common_border_color">&nbsp;</td>
		<td class="num common_border_color">&nbsp;</td>
		<%}%>
	 </tr>
	</logic:iterate>
	<tr  class='trShallow'>
		<td class="total common_border_color"><bean:message key="planar.stat.total"/></td>
		<td class="num total common_border_color">
				<bean:write name="weeklyStmtForm" property="view_sum" filter="true" />
		</td>
		<td class="num total common_border_color">
				<bean:write name="weeklyStmtForm" property="app_sum" filter="true" />
		</td>
		<bean:define id="judgment" name="weeklyStmtForm" property="apps_sum"/>        
		<logic:notEqual name="judgment" value="0.000%">
		<td class="num total common_border_color"> 
				<bean:write name="judgment" filter="true" />
		</td>
		</logic:notEqual>
		<logic:equal name="judgment" value="0.000%">
		<td class="num total common_border_color">&nbsp;</td>
		</logic:equal>
		<td class="num common_border_color">&nbsp;</td>
	 </tr>
</table>
<table width="80%" border="0" cellspacing="0" cellpadding="0" align="center">
<tr><td>&nbsp;</td></tr>
<tr>
  <td align="center">
    <html:button property="button1" styleClass="mybutton" onclick="javascript:window.close();"><bean:message key="button.close"/></html:button>
  </td>
</tr>
</table>
</td>
</tr>
</table>
</body>