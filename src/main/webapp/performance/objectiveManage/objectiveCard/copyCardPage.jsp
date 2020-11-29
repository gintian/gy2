<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient"%>
<%@ page import="java.util.*,
				com.hjsj.hrms.actionform.performance.objectiveManage.CopyObjectCardForm,
				org.apache.commons.beanutils.LazyDynaBean,
				com.hrms.frame.dao.RecordVo,
				com.hrms.hjsj.utils.Sql_switcher,
				com.hrms.struts.constant.SystemConfig,
				com.hrms.hjsj.sys.Constant,
				com.hrms.struts.valueobject.UserView,
				com.hrms.struts.constant.WebConstant" %>
<%

	CopyObjectCardForm copyObjectCardForm=(CopyObjectCardForm)session.getAttribute("copyObjectCardForm");
	String returnflag=copyObjectCardForm.getReturnflag();
	if(returnflag==null)
		returnflag="";
	String objectSpFlag=copyObjectCardForm.getObjectSpFlag();
	String importPositionField=copyObjectCardForm.getImportPositionField();
	String body_id=copyObjectCardForm.getBody_id();
	String un_functionary=copyObjectCardForm.getUn_functionary();
	String currappuser=copyObjectCardForm.getCurrappuser();
	String entranceType=copyObjectCardForm.getEntranceType();
	String processing_state_all=copyObjectCardForm.getProcessing_state_all();
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String a0100=userView.getA0100();
	String object_id=copyObjectCardForm.getObject_id();
	String clientName=copyObjectCardForm.getClientName();
	ArrayList tlist = copyObjectCardForm.getTabList()==null?new ArrayList():copyObjectCardForm.getTabList();
	String appealObjectStr= copyObjectCardForm.getAppealObjectStr();
    int height=tlist.size()*10;
	
	int currentlevel=0;
	if(body_id.equalsIgnoreCase("1"))
			currentlevel=1;
	if(body_id.equalsIgnoreCase("0"))
			currentlevel=2;
	if(body_id.equalsIgnoreCase("-1"))
			currentlevel=3;
	if(body_id.equalsIgnoreCase("-2"))
			currentlevel=4;
	
	
	
	String opt=copyObjectCardForm.getOpt();
	Hashtable planParam=copyObjectCardForm.getPlanParam();
	String scoreflag=(String)planParam.get("scoreflag");  //=2混合，=1标度(默认值=混合)
	String EvalOutLimitStdScore=(String)planParam.get("EvalOutLimitStdScore");  //评分时得分不受标准分限制True, False, 默认为 False;都加
	String targetMakeSeries=(String)planParam.get("targetMakeSeries");
	int level=1;
    if(targetMakeSeries!=null&&!targetMakeSeries.equals(""))
    {
       level=Integer.parseInt(targetMakeSeries);
    }
	String taskAdjustNeedNew=(String)planParam.get("taskAdjustNeedNew");
	String EvalCanNewPoint=(String)planParam.get("EvalCanNewPoint");
	String allowSeeLowerGrade=(String)planParam.get("allowSeeLowerGrade");
	String isAdjustPoint=copyObjectCardForm.getIsAdjustPoint();
	String TargetCollectItem=(String)planParam.get("TargetCollectItem");
	String targetTraceEnabled=copyObjectCardForm.getTargetTraceEnabled();
	String targetCollectItem2=copyObjectCardForm.getTargetCollectItem();
	String model=copyObjectCardForm.getModel();
	String mainbodyScoreStatus=copyObjectCardForm.getMainbodyScoreStatus();
	String planStatus=copyObjectCardForm.getPlanStatus();
	RecordVo per_objectVo=copyObjectCardForm.getPer_objectVo();
	String returnURL=copyObjectCardForm.getReturnURL();
	ArrayList adjustBeforePointList=copyObjectCardForm.getAdjustBeforePointList();
	String allowLeadAdjustCard=copyObjectCardForm.getAllowLeadAdjustCard();
	String aurl = (String)request.getServerName();
	String trHeight="40";
	String divTop="47";
	String divHeight="0";
	String buttonClass="mybutton";
	String importPre="引入上期目标卡";
	String report="info.appleal.state1";
	String pz="info.appleal.state8";
	String bp="info.appleal.state7";
	String bh="button.rejeect2";
	int value=1;
	if(clientName.equals("zglt"))
	{
	     trHeight="62";
	     divTop="75";
	     divHeight="15";
	     buttonClass="mybuttonBig";
	     pz="info.appleal.state3";
	     bp="info.appleal.state1";
	    // bh="button.reject";
	     value=2;
	}
	if(clientName.equalsIgnoreCase("bjpt"))
	{
	   importPre="引入上期KPI表";
	   report="info.appleal.state7";
	} 
	if (userView.getBosflag().equalsIgnoreCase("hcm")) {
		divTop = "57";
		divHeight = "10";
	}
	int plan_Status=Integer.parseInt(planStatus);
	  
	 
	    String url_p=SystemConfig.getServerURL(request);
	    
	String creatCard_mail=copyObjectCardForm.getCreatCard_mail();
	String evaluateCard_mail=copyObjectCardForm.getEvaluateCard_mail();
	String isApprove=copyObjectCardForm.getIsApprove();
	String grade_template_id_str=copyObjectCardForm.getGrade_template_id_str();
	String isAllowAppealTrancePoint=copyObjectCardForm.getIsAllowAppealTrancePoint();  //是否允许报批跟踪指标
	String isAllowApproveTrancePoint=copyObjectCardForm.getIsAllowApproveTrancePoint();
	ArrayList mainbodylist=copyObjectCardForm.getMainbodylist();
	
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

<script type="text/javascript">

var IVersion=getBrowseVersion();
<!--
if(IVersion==8){
  document.writeln("<link href=\"/performance/objectiveManage/objectiveCard/objectiveCard_8.css\" rel=\"stylesheet\" type=\"text/css\">");
}else{
  document.writeln("<link href=\"/performance/objectiveManage/objectiveCard/objectiveCard.css\" rel=\"stylesheet\" type=\"text/css\">");
}
//-->
</script>
<!--
<link href="/performance/objectiveManage/objectiveCard/objectiveCard.css" rel="stylesheet" type="text/css">
-->
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/pergrade.js"></script>
<SCRIPT LANGUAGE=javascript src="/performance/objectiveManage/objectiveCard/objectiveCard.js"></SCRIPT>
<script language="JavaScript">
 var EvalOutLimitStdScore='<%=(EvalOutLimitStdScore.toLowerCase())%>'
 var old_itemid="";
 var old_p0400="";
 var old_obj;
 var status="${copyObjectCardForm.status}"    //　０:分值模版  1:权重模版
 var perPointNoGrade="${copyObjectCardForm.perPointNoGrade}"  //目标卡中引入的绩效指标是否设置了标度  0：  1：没有设置标度
 var noGradeItem="${copyObjectCardForm.noGradeItem}"
 var isEntireysub="${copyObjectCardForm.isEntireysub}"        //提交是否必填
 var aclientHeight=document.body.clientHeight
 var url_p="<%=url_p%>";
 var model="${copyObjectCardForm.model}";
 var body_id="${copyObjectCardForm.body_id}";
 var opt="${copyObjectCardForm.opt}";
 var plan_id="${copyObjectCardForm.planid}";
 var object_id="${copyObjectCardForm.object_id}";
 
 
 var un_functionary="${copyObjectCardForm.un_functionary}";
 var pendingCode="${copyObjectCardForm.pendingCode}";
 var creatCard_mail="<%=(creatCard_mail.toLowerCase())%>";
 var evaluateCard_mail="<%=(evaluateCard_mail.toLowerCase())%>";
 var currentlevel=<%=currentlevel%>;
 var targetMakeSeries=<%=targetMakeSeries%>;
 var scoreflag='<%=scoreflag%>';
 var grade_template_id_str='<%=grade_template_id_str%>';
 
function click(){ 
 
if(event.button==2){ 
	alert( '右键已屏蔽 !'); 
} 
} 
document.onmousedown=click 

</script>

<style>

</style>

<html>
<head>
<title>Insert title here</title>
</head>
<body >
<html:form action="/performance/objectiveManage/objectiveCard">

<table width="100%"><tr height="<%=trHeight%>"><td align="left">
${copyObjectCardForm.desc}</td>
</tr>
<tr><td>
<script language='javascript' >
		document.write("<div id=\"tbl-container\"  class=\"framestyle0\" style='position:absolute;top:<%=divTop%>;height:"+(aclientHeight-85-<%=divHeight%>)+";width:100%'  >");
</script>
${copyObjectCardForm.cardHtml} 
<%if(model.equals("3")&&(body_id.equals("0")||body_id.equals("1")||body_id.equals("-1")||body_id.equals("-2"))){ %>
<%if(!clientName.equals("zglt")){ %>
<table width="100%" align="left" border='0'>
<hrms:priv func_id="06070302">
<tr>
<td class="RecordRow_locked_sp">
审批过程&nbsp;<a href="javascript:displayIMG();"><img id="but" src="/images/button_vert1.gif" border="0" style="cursor:hand"/></a>
</td>
</tr>
<tr>
<td>
<table id="spd" style="display=block" width="70%" border="0" cellspacing="0"  align="left" cellpadding="0" class="ListTable">
<thead>
<tr>
  <td align="center" class="TableRow_locked_m"  nowrap>
  &nbsp;<bean:message key="label.zp_resource.status"/>&nbsp;
  </td>
  <td align="center" class="TableRow_locked_m"  colspan='3' nowrap>
  &nbsp;<bean:message key="label.performance.reportdate"/>&nbsp;
  </td>
</tr>
</thead>
 <%
   for(int k=0;k<mainbodylist.size();k++)
   {
     LazyDynaBean mbean=(LazyDynaBean)mainbodylist.get(k);
  %>
	     <tr class="trShallow">     	     
	      <td align="left" class="RecordRow_locked_m" nowrap>
	     &nbsp;<%=(String)mbean.get("sp_flag")%>&nbsp;
	     </td>
	      <td align="left" class="RecordRow_locked_m" colspan='3' nowrap>
	     &nbsp;<%=(String)mbean.get("report_date")%>&nbsp;
	     </td>
	     </tr>
	     <% for(int i=1;i<=level;i++)
	        {
	        String xx=String.valueOf(i);
	        ArrayList list = (ArrayList)mbean.get(xx);
	        String trclass="";
	        if(i%2==0)
	           trclass="trDeep";
	        else
	           trclass="trShallow";
	        for(int j=0;j<list.size();j++)
	        {
	           LazyDynaBean _bean=(LazyDynaBean)list.get(j);
	           String atrclass="";
	           if(j%2==0)
	             atrclass="trDeep";
	           else
	            atrclass="trShallow";
	     %>
	     <%if(j==0){ %>
	     <tr class="<%=trclass%>">
	        <td align="left" class="RecordRow_locked_m" rowspan="<%=(String)mbean.get(xx+"rowspan")%>" nowrap>
	         &nbsp;<%=(String)mbean.get(xx+"desc")%>&nbsp;
	        </td>
	        <td align="left" class="TableRow_locked_m" nowrap>
	       &nbsp;审批人&nbsp;
	        </td>
	         <td align="left" class="TableRow_locked_m" nowrap>
	       &nbsp;审批状态&nbsp;
	        </td>
	         <td align="left" class="TableRow_locked_m" nowrap>
	       &nbsp;审批时间&nbsp;
	        </td>
	        </tr>
	        <%} %>  
	        <tr class="<%=atrclass%>">
	        <td align="left" class="RecordRow_locked_m" nowrap>
	       &nbsp;<%=(String)_bean.get("a0101")%>&nbsp;
	        </td>
	         <td align="left" class="RecordRow_locked_m" nowrap>
	       &nbsp;<%=(String)_bean.get("sp_flag")%>&nbsp;
	        </td>
	         <td align="left" class="RecordRow_locked_m" nowrap>
	       &nbsp;<%=(String)_bean.get("sp_date")%>&nbsp;
	        </td>
	        </tr>
	        <%} %>
	     <% }
	     }
	     %>
</table>
</td>
</tr>
</hrms:priv>
</table>
<%} %>
<%} %>
<script language='javascript' >
		document.write("</div>");
</script>
</td>
</tr>
<tr><td>
<script language='javascript' >
		document.write("<div id=\"aa\"  style='position:absolute;top:"+(aclientHeight-30)+"'>");
</script>
</div>
</td>
</tr>
</table>
<script language='javascript' >
		document.write("<div id=\"bb\"  style='position:absolute;top:"+(aclientHeight-30)+";'  >");
</script>
<input type='button' value='关闭' onclick='javascript:window.close()' class="<%=buttonClass%>" />
     
</div>

<input type='hidden' value=''  name='importPoint_value' />

<div id="date_panel">
   			
</div>
<a href="<%=url_p%>" style="display:none" id="hostname">for vpn</a>

 <div id='menu_'   style="background:#ffffff;border:1px groove black;width:85;height:100 " >
	
</div>

<div id="date_panel0" style="background:#ffffff;border:1px groove black;width:130;height:<%=height%>">
  <select name="date_box" onblur="Element.hide('date_panel0');" id="selectBOX" size="<%=tlist.size()%>" onchange='addItem("${copyObjectCardForm.planid}","${copyObjectCardForm.object_id}");'>    
  <logic:iterate id="element" property="tabList" name="copyObjectCardForm" offset="0" indexId="index">
  <option value="<bean:write name="element" property="id"/>"/><bean:write name="element" property="name"/></option>
   
  </logic:iterate> 
  </select>
</div>
<div id="person_panel0" style="background:#ffffff;border:1px groove black;width:130;height:100px">

</div>
</html:form>
<script language='javascript'>
	document.getElementById('menu_').style.display="none";
	Element.hide('date_panel0');
	Element.hide('person_panel0');
	giveValue("1");
</script>

<script language="javascript" src="/general/sys/hjaxmanage.js"></script>
<script language="javascript">AxManager.writeCard();</script>


</body>
</html>