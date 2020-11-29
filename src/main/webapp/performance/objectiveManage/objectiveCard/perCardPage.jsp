<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient"%>
<%@ page import="java.util.*,
				com.hjsj.hrms.actionform.performance.objectiveManage.ObjectCardForm,
				org.apache.commons.beanutils.LazyDynaBean,
				com.hrms.struts.valueobject.UserView,
				com.hrms.hjsj.utils.Sql_switcher,
				com.hrms.hjsj.sys.Constant,
				com.hrms.struts.constant.WebConstant,com.hrms.struts.constant.SystemConfig" %>
<%

	ObjectCardForm objectCardForm=(ObjectCardForm)session.getAttribute("objectCardForm");
	String objectSpFlag=objectCardForm.getObjectSpFlag();
	String body_id=objectCardForm.getBody_id();
	String currappuser=objectCardForm.getCurrappuser();
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String a0100=userView.getA0100();
	String object_id=objectCardForm.getObject_id();
	String opt=objectCardForm.getOpt();
	Hashtable planParam=objectCardForm.getPlanParam();
	String taskAdjustNeedNew=(String)planParam.get("taskAdjustNeedNew");
	String scoreflag=(String)planParam.get("scoreflag");  //=2混合，=1标度(默认值=混合)
	String EvalOutLimitStdScore=(String)planParam.get("EvalOutLimitStdScore");  //评分时得分不受标准分限制True, False, 默认为 False;都加
	String isAdjustPoint=objectCardForm.getIsAdjustPoint();
	String model=objectCardForm.getModel();
	String buttonClass="mybutton";
	 String tt3CssName="ttNomal3";
	if(SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("zglt"))
	{
	       buttonClass="mybuttonBig";
	       tt3CssName="tt3";
	}
	String url_p=SystemConfig.getServerURL(request);
	
	ArrayList tlist = objectCardForm.getTabList()==null?new ArrayList():objectCardForm.getTabList(); 
    int height=tlist.size()*10;
    
    
      String dbtype="1";
	  if(Sql_switcher.searchDbServer()== Constant.ORACEL)
	  {
	    dbtype="2";
	  }
	  else if(Sql_switcher.searchDbServer()== Constant.DB2)
	  {
	    dbtype="3";
	  }
	EncryptLockClient lockclient=(EncryptLockClient)session.getServletContext().getAttribute("lock");
   String license=lockclient.getLicenseCount();
   int version=userView.getVersion();
   if(license.equals("0"))
        version=100+version;
   int usedday=lockclient.getUseddays();
    
    
%>
<hrms:themes />
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/pergrade.js"></script>
<SCRIPT LANGUAGE=javascript src="/performance/objectiveManage/objectiveCard/objectiveCard.js"></SCRIPT>
<script language="JavaScript">
 var old_itemid="";
 var old_p0400="";
 var old_obj;
 var status="${objectCardForm.status}"    //　０:分值模版  1:权重模版
 var perPointNoGrade="${objectCardForm.perPointNoGrade}"  //目标卡中引入的绩效指标是否设置了标度  0：  1：没有设置标度
 var noGradeItem="${objectCardForm.noGradeItem}"
 var isEntireysub="${objectCardForm.isEntireysub}"        //提交是否必填
 var aclientHeight=document.body.clientHeight
 var scoreflag='<%=scoreflag%>';
 var EvalOutLimitStdScore='<%=(EvalOutLimitStdScore.toLowerCase())%>'
 
 function selectPlan()
 {
 		<%
 		String  param="";
 		if(request.getParameter("from")!=null&&request.getParameter("from").equals("jj"))
 				param="&from="+request.getParameter("from");
 		if(request.getParameter("from")!=null&&request.getParameter("from").equals("batchGrade"))
 				param="&from="+request.getParameter("from");
 		if(request.getParameter("operator")!=null&&request.getParameter("operator").equals("1"))
 				param+="&is360=1";	
 		else if(request.getParameter("is360")!=null&&request.getParameter("is360").equals("1"))
 				param+="&is360=1";	
 		
 		%>
 
 		document.objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_query2=query<%=param%>&opt=0&body_id=1&model=3&planids=<%=(request.getParameter("planids"))%>&object_id=<%=(request.getParameter("object_id"))%>";
 		document.objectCardForm.submit();
 }
  
 function goback()
 {
	
 }

//var IVersion=getBrowseVersion();
//if(IVersion==8){
  document.writeln("<link href=\"/performance/objectiveManage/objectiveCard/perCardPage.css\" rel=\"stylesheet\" type=\"text/css\">");
//}
//else{
//  document.writeln("<link href=\"/performance/objectiveManage/objectiveCard/objectiveCard.css\" rel=\"stylesheet\" type=\"text/css\">");
//}
  
</script>

<style>
    .RecordRow_self_nolocked2{
        border-left: 0pt;
        border-top:0pt;
        border-bottom: 1pt solid rgb(148, 182, 230);
        border-right: 1pt solid rgb(148, 182, 230);
    }

</style>

<html>
<head>
<title>Insert title here</title>
</head>
<body  onload="initCard();" >

<html:form action="/performance/objectiveManage/objectiveCard"><br>
 &nbsp;&nbsp;
<font class="<%=tt3CssName%>">目标计划：</font><hrms:optioncollection name="objectCardForm" property="planList" collection="list"   />
             <html:select name="objectCardForm" property="planid" size="1" onchange="selectPlan()"  >
             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
        </html:select>
        
 &nbsp;&nbsp;<font class="<%=tt3CssName%>">${objectCardForm.desc} </font>
<br>
<script language='javascript' >
		document.write("<div id=\"tbl-container\"  class=\"framestyle0\" style='position:absolute;height:"+(aclientHeight-100)+";width:99%'  >");
</script>
${objectCardForm.cardHtml} 
<script language='javascript' >
		document.write("</div>");
</script>


<script language='javascript' >
		document.write("<div id=\"aa\"  style='position:absolute;top:"+(aclientHeight-30)+"'  >");
</script>

	
	 <input type='button' value='总结回顾' onclick="review('-1','',520)" class="<%=buttonClass%>" />




<logic:equal value="1" name="objectCardForm" property="isCard">
<hrms:priv func_id="06070204">
<input type="button" class="<%=buttonClass%>" name="print" value="打 印" onclick='printP("${objectCardForm.planid}","${objectCardForm.object_id}","${objectCardForm.tabIDs}");'/>
</hrms:priv>
</logic:equal>




${objectCardForm.personalComment}
${objectCardForm.targetDeclare}
<% if(request.getParameter("from")==null||!request.getParameter("from").equals("jj")){

		if(request.getParameter("from")!=null&&request.getParameter("from").equals("batchGrade")){
 %>
	<input type='button' value='关闭' onclick='javascript:parent.hiddenWin()' class="<%=buttonClass%>" />
<%
		}
		else
		{
%>
	<input type='button' value='关闭' onclick='javascript:window.close()' class="<%=buttonClass%>" />
<%		
		}
 } %>
</div>

<script language='javascript' >
		document.write("<div id=\"bb\"  style='position:absolute;top:"+(aclientHeight-30)+";display:none'  >");
</script>
	${objectCardForm.personalComment}
	${objectCardForm.targetDeclare}
	<input type='button' value='<bean:message key="kq.search_feast.back"/>' onclick='go_back()' class="<%=buttonClass%>" />

</div>

<input type='hidden' value=''  name='importPoint_value' />

<div id="date_panel">
   			
</div>
<a href="<%=url_p%>" style="display:none" id="hostname">for vpn</a>

 <div id='menu_'   style="background:#ffffff;border:1px groove black;width:85;height:100 " >
	
</div>

<div id="date_panel0" style="background:#ffffff;border:1px groove black;width:130;height:<%=height%>">
  <select name="date_box" onblur="Element.hide('date_panel0');" id="selectBOX" size="<%=tlist.size()%>" onclick='addItem("${objectCardForm.planid}","${objectCardForm.object_id}");'>    
  <logic:iterate id="element" property="tabList" name="objectCardForm" offset="0" indexId="index">
  <option value="<bean:write name="element" property="id"/>"/><bean:write name="element" property="name"/></option>
   
  </logic:iterate> 
  </select>
</div>
</html:form>
<%if(userView.hasTheFunction("06070204")){ %>
<script language="javascript" src="/general/sys/hjaxmanage.js"></script>
<script language="javascript">AxManager.writeCard();</script>
<%} %>
<script language='javascript'>
	document.getElementById('menu_').style.display="none";

function initCard()
{
      var rl = document.getElementById("hostname").href;     
      var aurl=rl;//tomcat路径
      var DBType="<%=dbtype%>";//1：mssql，2：oracle，3：DB2
      var UserName="su";   //登陆用户名暂时用su     
      var obj = document.getElementById('CardPreview1');   
      var superUser="1";
      var menuPriv="";
      var tablePriv="";
      if(obj==null)
      {
         return false;
      }
      obj.SetSuperUser(superUser);  // 1为超级用户,0非超级用户
      obj.SetUserMenuPriv(menuPriv);  // 指标权限, 逗号分隔, 空表示全权
      obj.SetUserTablePriv(tablePriv);  // 子集权限, 逗号分隔, 空表示全权         
      obj.SetURL(aurl);
      obj.SetDBType(DBType);
      obj.SetUserName(UserName);
      obj.SetUserFullName("su");
      obj.SetHrpVersion("<%=version%>");
      obj.SetTrialDays("<%=usedday%>","30");
}	
	
function replaceAllStr(str, from, to ) {
	    var idx = str.indexOf( from );
	    while ( idx > -1 ) {
	        str = str.replace( from, to ); 
	        idx = str.indexOf( from );
	    }
	    return str;
}  

function addItem(planid,objectid)
{
   Element.hide('date_panel0');
   var hashVo=new ParameterSet();
   var obj = document.getElementById("selectBOX");
   var id="";
   for(var i=0;i<obj.options.length;i++)
   {
     if(obj.options[i].selected)
        id=obj.options[i].value;
   }
  hashVo.setValue("id",id);
  hashVo.setValue("plan_id",planid);
  hashVo.setValue("a0100",objectid);
//var In_parameters="opt=1";
var request=new Request({method:'post',asynchronous:false,onSuccess:showPrint,functionId:'9028000412'},hashVo);			
		  
}
	
function printP(planid,objectid,tabid)
{
  var arr=tabid.split(",");
  var num=0;
  var id="";
  for(var i=0;i<arr.length;i++)
  {
    if(arr[i]==null||trim(arr[i]).length==0)
       continue;
    else
       num++;
  }
  if(num>1)
  {
       Element.show('date_panel0');   
       var expr_editor=$('date_box');
       expr_editor.focus();
       var tyle=document.getElementById('date_panel0').style;
       tyle.position="absolute";
       tyle.top=(event.y-<%=height%>-30)+"px";
       tyle.left=(event.x)+"px";
   		
  }
  else
  {
    id=replaceAllStr(tabid,",","");
  }
 if(id=='')
    return;
  var hashVo=new ParameterSet();
  hashVo.setValue("id",id);
  hashVo.setValue("plan_id",planid);
  hashVo.setValue("a0100",objectid);
var In_parameters="opt=1";
var request=new Request({method:'post',asynchronous:false,onSuccess:showPrint,functionId:'9028000412'},hashVo);			
		  
}
	

function showPrint(outparamters)
{
   var dataflag=outparamters.getValue("d"); 
   var objid=outparamters.getValue("o"); 
   var tabid=outparamters.getValue("tabid"); 
   var obj = document.getElementById('CardPreview1');    
   if(obj==null)
   {
      alert("没有下载打印控件，请设置IE重新下载！");
      return false;
   }

   obj.SetCardID(tabid);
   obj.SetDataFlag(dataflag);
   obj.SetNBASE("USR");
   obj.ClearObjs();   
   obj.AddObjId(objid);
   try { obj.SetJSessionId(AxManager.getJSessionId()); } catch(err) {}
   obj.ShowCardModal();
   
}	
	
	
	
</script>
</body>
</html>