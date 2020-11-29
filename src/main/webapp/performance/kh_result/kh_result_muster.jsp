<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.SystemConfig" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.hjsj.utils.Sql_switcher"%>
<%@ page import="com.hjsj.hrms.actionform.performance.kh_result.KhResultForm"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient"%>
<%
   String tt4CssName="ttNomal4";
   String tt3CssName="ttNomal3";
   String buttonClass="mybutton";
   boolean scoreStatus=false;
   if(SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("zglt"))
   {
      tt4CssName="tt4";
      tt3CssName="tt3";
      buttonClass="mybuttonBig";
      scoreStatus=true;
   }
   String url_p=SystemConfig.getServerURL(request);  
   UserView userView=(UserView)session.getAttribute(WebConstant.userView);
   String fields=userView.getFieldpriv().toString();
   String tables=userView.getTablepriv().toString(); 
   String userName=userView.getUserName();
   String userFullName=userView.getUserFullName();
   String superUser="0";
   if(userView.isSuper_admin())
	  superUser="1";
   //liuy 2015-1-30 7141：自助服务/绩效考评/考评反馈/本人考核结果：按照登记表方式查看，报错 start 
   KhResultForm khResultForm=(KhResultForm)session.getAttribute("khResultForm");
   String plan_id = PubFunc.decrypt(khResultForm.getPlanid());
   String object_id = PubFunc.decrypt(khResultForm.getObject_id());
   //liuy 2015-1-30 end
   EncryptLockClient lockclient=(EncryptLockClient)session.getServletContext().getAttribute("lock");
   String license=lockclient.getLicenseCount();
   int version=userView.getVersion();
   if(license.equals("0"))
        version=100+version;
   int usedday=lockclient.getUseddays();
   String dbtype=String.valueOf(Sql_switcher.searchDbServer());
   
   String browser = "MSIE";
	String agent = request.getHeader("user-agent").toLowerCase(); 
	if(agent.indexOf("firefox")!=-1)
		browser="Firefox";
	else if(agent.indexOf("chrome")!=-1)
		browser="Chrome";
	else if(agent.indexOf("safari")!=-1)
		browser="Safari";
 %>
<script language="javascript" src="/general/sys/hjaxmanage.js"></script>
<script type="text/javascript">
<!--
var oper =0;

function initCard()
{
      
}

 
function showPrintCard()
{
	 document.getElementById("printButton").disabled=true;
     if(!AxManager.setup("TJBP", "CardPreview1", 0, 0, showPrintCard3, AxManager.cardpkgName))
         return false;
	 showPrintCard3();
}

var timecount=0;
function showPrintCard3()
{
	 	 var obj = document.getElementById('CardPreview1');  
	     try{
	     	  
	     	  
	     	  var rl = document.getElementById("hostname").href;     
		      var aurl=rl;//tomcat路径
		      var DBType="<%=dbtype%>";//1：mssql，2：oracle，3：DB2
		      var UserName="<%=userName%>";   //登陆用户名暂时用su      
		      var superUser="<%=superUser%>";
		      var menuPriv="<%=fields%>";
		      var tablePriv="<%=tables%>";
		      obj.SetSuperUser(superUser);  // 1为超级用户,0非超级用户
		      obj.SetUserMenuPriv(menuPriv);  // 指标权限, 逗号分隔, 空表示全权
		      obj.SetUserTablePriv(tablePriv);  // 子集权限, 逗号分隔, 空表示全权         
		      obj.SetURL(aurl);
		      obj.SetDBType(DBType);
		      obj.SetUserName(UserName);
		      obj.SetUserFullName("<%=userFullName%>");
		      obj.SetHrpVersion("<%=version%>");
		      obj.SetTrialDays("<%=usedday%>","30");
	     	  
		      var tab_id="${khResultForm.tabid}"; 
			  obj.SetCardID("${khResultForm.tabid}");
			  obj.SetDataFlag("<CARDSTYLE>P</CARDSTYLE><TEMPLATEID>${khResultForm.tabid}</TEMPLATEID><PLANID><%=plan_id%></PLANID>");
			  obj.SetNBASE("${khResultForm.nbase}");
			  obj.ClearObjs();  
			  obj.AddObjId("<NBASE>${khResultForm.nbase}</NBASE><ID><%=object_id%></ID><NAME>${khResultForm.object_name}</NAME>");
			  try { obj.SetJSessionId(AxManager.getJSessionId()); } catch(err) {}
			  obj.ShowCardModal();
			  document.getElementById("printButton").disabled=false;
		}catch(e1){
			timecount=timecount+1;
			if(timecount<20){
				setTimeout("showPrintCard3()",2000);
			}else{ 
	 			 alert("插件下载失败，请查看网速是否太慢或者插件被禁用！");
			}
		}

}






	function excecutePDF()
{
        var hashvo=new ParameterSet();        
        hashvo.setValue("nid","<%=object_id%>");
        hashvo.setValue("tabid","${khResultForm.tabid}");
        if(${khResultForm.cardparam.queryflagtype}==1)
        {
           hashvo.setValue("cyear","${khResultForm.cardparam.cyear}");
        }else if(${khResultForm.cardparam.queryflagtype}==3)
        {
           hashvo.setValue("cyear","${khResultForm.cardparam.csyear}");
        }else if(${khResultForm.cardparam.queryflagtype}==4)
        {
           hashvo.setValue("cyear","${khResultForm.cardparam.csyear}");
        }        
        hashvo.setValue("userpriv","noinfo");
        hashvo.setValue("istype","1");        
        hashvo.setValue("cmonth","${khResultForm.cardparam.cmonth}");
        hashvo.setValue("season","${khResultForm.cardparam.season}");
        hashvo.setValue("ctimes","${khResultForm.cardparam.ctimes}");
        hashvo.setValue("cdatestart","${khResultForm.cardparam.cdatestart}");
	    hashvo.setValue("cdateend","${khResultForm.cardparam.cdateend}");
    	hashvo.setValue("infokind","5");
    	hashvo.setValue("plan_id","<%=plan_id%>");
     	hashvo.setValue("querytype","${khResultForm.cardparam.queryflagtype}");	
     	hashvo.setValue("userbase","${khResultForm.cardparam.userbase}");
        var In_paramters="exce=PDF";  
        var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showPDF,functionId:'90100130017'},hashvo);
	
}	
function showPDF(outparamters)
{
 
    var url=outparamters.getValue("url");
    var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+url,"pdf");	
}
function changeDraw(obj,isClose)
{
   var drawId = "";
   for(var i=0;i<obj.options.length;i++)
   {
       if(obj.options[i].selected)
        {
             drawId = obj.options[i].value;
             break;
        }
   }
   khResultForm.action="/performance/kh_result/kh_result_muster.do?b_init=link&isClose="+isClose+"&tabid="+drawId;
   khResultForm.submit();
}	
function confirmResult()
{
    var hashvo=new ParameterSet();   
    if(!confirm("确定对考核结果表进行确认？"))
    {
      return;
    }
    hashvo.setValue("plan_id","${khResultForm.planid}"); 
    hashvo.setValue("object_id","${khResultForm.object_id}"); 
    hashvo.setValue("tabid","${khResultForm.selectTabId}");
    var request=new Request({method:'post',asynchronous:false,onSuccess:confirm_ok,functionId:'90100130031'},hashvo);    
}
function confirm_ok(outparameter)
{
   alert("考核结果已确认!");
   var selectTabId=outparameter.getValue("selectTabId");
   khResultForm.action="/performance/kh_result/kh_result_muster.do?b_init=link&isClose=${khResultForm.isCloseButton}&tabid="+selectTabId;
   khResultForm.submit();
}
function ret(code,model,distinctionFlag,isClose)
{
  if(model=='3')
  {
    khResultForm.action="/performance/kh_result/org_kh_plan.do?b_init=init&distinctionFlag=0&model=3&isClose="+isClose;
    khResultForm.submit();
  }
  else
 {
   khResultForm.action="/performance/kh_result/kh_plan_list.do?b_init=init&opt=1&isClose="+isClose+"&a0100="+code+"&model="+model+"&distinctionFlag="+distinctionFlag;
   khResultForm.submit();
 }
   
}
function ltret(code,model,distinctionFlag,nbase)
{
   khResultForm.action="/performance/kh_result/kh_result_personlist.do?b_init=init&opt=1&a_code="+code+"&model="+model+"&distinctionFlag="+distinctionFlag+"&nbase="+nbase;
   khResultForm.submit();
   
}
//-->
</script>
<link href="/performance/objectiveManage/objectiveCard/objectiveCard.css" rel="stylesheet" type="text/css">
<html:form action="/performance/kh_result/kh_result_muster">
<a href="<%=url_p%>" style="display:none" id="hostname">for vpn</a>
<br>

<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
<tr>
<td align="left">
<logic:equal value="1" property="isLT" name="khResultForm">

<html:select name="khResultForm" property="selectTabId" size="1" onchange="changeDraw(this,'${khResultForm.isCloseButton}');">
			<html:optionsCollection property="tableList" value="dataValue" label="dataName"/>
		    </html:select>&nbsp;
		    
		    <logic:equal value="1" property="configButton" name="khResultForm">
		    <input type="button" class="<%=buttonClass%>" value="确认" onclick="confirmResult();"/>
		    &nbsp;
		    </logic:equal>
</logic:equal>

<input type='button' value='打印预演' id='printButton'  class='mybutton' onclick='showPrintCard();'> 
<input type="button" value="<bean:message key="button.cardpdf"/>" name="pdf" onclick="excecutePDF();" class="<%=buttonClass%>"/>
&nbsp;
<logic:equal value="0" name="khResultForm" property="isCloseButton">
<logic:equal value="1" property="isLT" name="khResultForm">
<%if(scoreStatus){ %>
<input type="button" class="<%=buttonClass%>" value="返回" onclick='ltret("${khResultForm.code}","${khResultForm.model}","${khResultForm.distinctionFlag}","${khResultForm.nbase}");'/>
<%}else{ %>
<input type="button" class="<%=buttonClass%>" value="返回" onclick='ret("<%=object_id%>","${khResultForm.model}","${khResultForm.distinctionFlag}","${khResultForm.isCloseButton}");'/>
<%} %>
</logic:equal>
</logic:equal>
<logic:equal value="1" name="khResultForm" property="isCloseButton">
<logic:equal value="0" name="khResultForm" property="model">
<input type="button" class="<%=buttonClass%>" value="<bean:message key="button.close"/>" onclick='window.close();'/>
</logic:equal>
<logic:notEqual value="0" name="khResultForm" property="model">
<%if(scoreStatus){ %>
<input type="button" class="<%=buttonClass%>" value="返回" onclick='ltret("${khResultForm.code}","${khResultForm.model}","${khResultForm.distinctionFlag}","${khResultForm.nbase}");'/>
<%}else{ %>
<input type="button" class="<%=buttonClass%>" value="返回" onclick='ret("${khResultForm.object_id}","${khResultForm.model}","${khResultForm.distinctionFlag}","${khResultForm.isCloseButton}");'/>
<%} %>
</logic:notEqual>

</logic:equal>
</td>
</tr>
<tr>
<td>
<div id="TJBP">
</div>
<html:hidden  property="planid" name="khResultForm"/>
<html:hidden  property="object_id" name="khResultForm"/>
<html:hidden  property="model" name="khResultForm"/>
<hrms:ykcard name="khResultForm" property="cardparam" userbase="usr" istype="3" nid="<%=object_id %>" tabid="${khResultForm.tabid}" cardtype="plan" disting_pt="javascript:screen.width" userpriv="noinfo" havepriv="1" queryflag="0" infokind="5" plan_id="<%=plan_id %>"  browser="<%=browser %>"/>

</td>
</tr>
</table>
</html:form>

 
