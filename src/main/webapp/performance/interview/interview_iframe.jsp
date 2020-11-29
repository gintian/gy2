<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView,java.util.ArrayList"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hjsj.hrms.actionform.performance.kh_result.KhResultForm,com.hrms.hjsj.utils.Sql_switcher,com.hrms.hjsj.sys.Constant"%>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient"%>
<%
     
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String aurl = (String)request.getServerName();
    String port=request.getServerPort()+"";  
    String url_p="HTTP://"+aurl+":"+port;
    KhResultForm khResultForm=(KhResultForm)session.getAttribute("khResultForm");
    ArrayList tlist = khResultForm.getTabList();
    int height=tlist.size()*10;
    int height2=khResultForm.getRecordsList().size()*10;
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
   	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
	String userName = userView.getUserName();
 %>  
<style type="text/css">
    body td{
        overflow:visible !important;
    }
</style>

 <script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
 <link href="/performance/objectiveManage/objectiveCard/interview.css" rel="stylesheet" type="text/css">
<SCRIPT LANGUAGE=javascript src="/performance/objectiveManage/objectiveCard/objectiveCard.js"></SCRIPT>
<script type="text/javascript">
var aclientHeight = document.body.clientHeight;
if(document.documentElement && aclientHeight == 0) {
	aclientHeight = document.documentElement.clientHeight;
}
var alertMessage="${khResultForm.alertMessage}";
<!--
var opt='';
function openInterviewTemplet(tab_id,opt,objectid,planid)
{
  if(alertMessage!="0"&&trim(alertMessage).length>0)
  {
     alert(alertMessage);
     return;
  }
	 
	if(opt=="1")
	{
		var hashVo=new ParameterSet();
		hashVo.setValue("tab_id",tab_id);
	    hashVo.setValue("objectid",objectid);
	     hashVo.setValue("planid","${khResultForm.planid}");
	    hashVo.setValue("pre","Usr");
		var request=new Request({method:'post',asynchronous:false,onSuccess:subSuccess0,functionId:'9028000415'},hashVo);	
	
	}
	else
	{
		window.open("/general/template/edit_form.do?b_query=link&tabid="+tab_id+"&taskid=${khResultForm.task_id}&ins_id=${khResultForm.ins_id}&businessModel=2&sp_flag=2&returnflag="+tab_id,"_ad","top=0,left=5,height="+(window.screen.height-70)+",width="+(window.screen.width-20));
	}
}


function subSuccess0(outparamters)
{
		var tab_id="${khResultForm.templet_id}";
		var thecodeurl ="/general/template/edit_form.do?b_query=link`businessModel=2`returnflag=3`sp_flag=1`ins_id=0`tabid="+tab_id; 
		var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);     
	    var return_vo= window.showModalDialog(iframe_url, "", 
	              "dialogWidth:"+(window.screen.width-20)+"px; dialogHeight:"+(window.screen.height-70)+"px;resizable:no;center:yes;scroll:yes;status:no");    
		if(return_vo)
		{
			
		   var hashVo=new ParameterSet();
		   hashVo.setValue("ins_id",return_vo);
		   hashVo.setValue("plan_id",'${khResultForm.planid}');
		   hashVo.setValue("objectid","${khResultForm.object_id}");
		   var request=new Request({method:'post',asynchronous:false,onSuccess:subSuccess,functionId:'9028000414'},hashVo);	
		
		} 

}


function subSuccess()
{
	var src=document.location;
	document.location=src;

}


function openInterView(planid,objectid,body,oper)
{
var id=document.getElementById("idvalue").value;
   var str="/performance/interview/search_interview_list.do?b_edit=edit`id="+id+"`planid="+planid+"`objectid="+objectid+"`isClose=0`body="+body+"`oper="+oper;
   var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(str);
   if(isIE6()){
       //var values= window.showModalDialog(iframe_url,window, 
		//        "dialogWidth:655px; dialogHeight:700px;resizable:yes;center:yes;scroll:yes;status:no");	
       //兼容非IE浏览器 弹窗替换用 open弹窗  wangb 20171205
       var iTop = (window.screen.availHeight - 30 - 700) / 2; //获得窗口的垂直位置
       var iLeft = (window.screen.availWidth - 10 - 655) / 2; //获得窗口的水平位置
       window.open(iframe_url,'',"width=655px,height=700px,resizable=yes,scroll=yes,status=no,left="+iLeft+",top="+iTop);
   }else{
      // var values= window.showModalDialog(iframe_url,window, 
	//	        "dialogWidth:650px; dialogHeight:700px;resizable:yes;center:yes;scroll:yes;status:no");
      //兼容非IE浏览器 弹窗替换用 open弹窗  wangb 20171205
       var iTop = (window.screen.availHeight - 30 - 700) / 2;  //获得窗口的垂直位置
       var iLeft = (window.screen.availWidth - 10 - 650) / 2; //获得窗口的水平位置 
       window.open(iframe_url,'',"width=655px,height=700px,resizable=yes,scroll=yes,status=no,left="+iLeft+",top="+iTop);
   }
   openOper = oper;
  // if(values&&oper=='1')
  // {
  //    var obj = new Object();
  //    obj.id=values.id;
 //     document.getElementById("idvalue").value=obj.id;
 //  }	
}
//兼容非IE浏览器  弹窗调用父窗口方法
var openOper;
function openValue(values){
	 if(values&&openOper=='1')
	 {
	      var obj = new Object();
	      obj.id=values.id;
	      document.getElementById("idvalue").value=obj.id;
	 }
}

function initidValue(id)
{
 document.getElementById("idvalue").value=id;
  Element.hide('date_panel');
}
function returnList()
{
   performanceInterviewForm.action="/performance/interview/search_interview_list.do?b_init=init&opt=2";
   performanceInterviewForm.submit();
}
var onetabid="";
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
       Element.show('date_panel');   
       var expr_editor=$('date_box');
       expr_editor.focus();
       var tyle=document.all.date_panel.style;
       tyle.position="absolute";
       tyle.top=(event.clientY-<%=height%>-40)+"px";
       tyle.left=(event.clientX-40)+"px";
  }
  else
  {
    id=replaceAllStr(tabid,",","");
  }
 if(id=='')
    return;
  onetabid=id;
  window.setTimeout("printOneTable()",3000);
  
}
function printOneTable()
{
   var hashVo=new ParameterSet();
  hashVo.setValue("id",onetabid);
  hashVo.setValue("plan_id","${khResultForm.planid}");
  hashVo.setValue("a0100","${khResultForm.object_id}");
  var In_parameters="opt=1";
  var request=new Request({method:'post',asynchronous:false,onSuccess:showPrint,functionId:'9028000412'},hashVo);			
		  
}
function addItem(planid,objectid)
{
   Element.hide('date_panel');
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
function initCard()
{
      var rl = document.getElementById("hostname").href;     
      var aurl=rl;//tomcat路径
      var DBType="<%=dbtype%>";//1：mssql，2：oracle，3：DB2
      var UserName="<%=userName%>";    
      var obj = document.getElementById('CardPreview1');   
      var superUser="1";
      var menuPriv="";
      var tablePriv="";
      if(obj==null)
      {
         return false;
      }
      obj.SetSelfInfo("1");//自助用户
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
function changeBgColor(id)
{
   var e = document.getElementById(id);
   e.style.backgroundColor="#98C2E8";
  
}
function goBackBgColor(id)
{
   var e = document.getElementById(id);
   e.style.backgroundColor="white";
}
//-->
</script>
<hrms:themes />
<body onload="initCard();">
<html:form action="/performance/kh_result/kh_result_figures">
<table align="center" border="0" width="90%" cellpmoding="0" cellspacing="0" cellpadding="0">
<html:hidden name="khResultForm" property="object_id"/>
<html:hidden name="khResultForm" property="planid"/>
<html:hidden name="khResultForm" property="oper"/>
<input type="hidden" name="id" id="idvalue"/>
<a href="<%=url_p%>" style="display:none" id="hostname">for vpn</a>
<tr>
<td>
<%if("hl".equals(hcmflag)){ %>
<br>
<%} %>
<script language='javascript' >
    var theWidth = document.body.clientWidth-150;
		document.write("<div id=\"tbl-container\" class=\"framestyle0\"  style='position:absolute;height:"+(aclientHeight-65)+"px;width:"+theWidth+"px;'  >");
</script>
	 ${khResultForm.cardHtml}
<script language='javascript' >
		document.write("</div>");
</script>
</td>
</tr>
<tr>
<td>
<script language='javascript' >
<%if("hl".equals(hcmflag)){ %>
	document.write("<div id=\"aa\"  style='position:absolute;top:"+(aclientHeight-31)+"px;margin-left:-2px;'  >");
<%}else{ %>
		document.write("<div id=\"aa\"  style='position:absolute;top:"+(aclientHeight-51)+"px;margin-left:-2px;'  >");
<%}%>
</script>
 <table width="100%">
<tr>
<td align="left">

<logic:equal value="3" name="khResultForm" property="flag">
<% if(khResultForm.getRecordsList().size()>0){ %>
	<input type="button" name="interview" class="mybutton" value="面谈记录" onclick='showBox(this)'/>
<% } %>
</logic:equal>
<logic:equal value="2" name="khResultForm" property="flag">
<input type="button" name="interview" class="mybutton" value="面谈记录" onclick='openInterviewTemplet("${khResultForm.templet_id}","${khResultForm.opt}","${khResultForm.object_id}","${khResultForm.planid}");'/>
</logic:equal>
<logic:equal value="1" name="khResultForm" property="flag">
<input type="button" name="interview" class="mybutton" value="面谈记录" onclick='openInterView("${khResultForm.planid}","${khResultForm.object_id}","${khResultForm.body}","${khResultForm.oper}");'/>
</logic:equal>
<%-- 暂时仅ie下显示打印按钮--%>
<% if(request.getHeader("User-Agent").toLowerCase().indexOf("msie")>0){ %>
<logic:equal value="1" name="khResultForm" property="isCard">
<input type="button" class="mybutton" name="print" value="打 印" onclick='printP("${khResultForm.planid}","${khResultForm.object_id}","${khResultForm.tabIDs}");'/>
</logic:equal>
<% } %>

<input type="button" name="ret" class="mybutton" value="<bean:message key="button.return"/>" onclick='returnList();'/>
</td>
</tr>
</table>
</div>
</td>
</tr>
</table>
  <div id="date_panel" style="background:#ffffff;border:1px groove black;width:130;height:<%=height%>">
  <select name="date_box" onblur="Element.hide('date_panel');" id="selectBOX" size="<%=tlist.size()%>" onclick='addItem("${khResultForm.planid}","${khResultForm.object_id}");'>    
  <logic:iterate id="element" property="tabList" name="khResultForm" offset="0" indexId="index">
  <option value="<bean:write name="element" property="id"/>"/><bean:write name="element" property="name"/></option>
   
  </logic:iterate> 
  </select>
  </div>
<script type="text/javascript">
initidValue("<%=request.getParameter("id")%>");

function showBox(srcobj)
{
	  Element.show('a0101_pnl');   
      var pos=getAbsPosition(srcobj);
	  with($('a0101_pnl'))
	  {
	        style.position="absolute";
    		style.posLeft=pos[0]-1;
 		    style.posTop=pos[1]-1-<%=height2%>-40;
 		   
		    style.width=(srcobj.offsetWidth<150)?150:srcobj.offsetWidth+1;
      }  
      if(document.getElementById("a0101_box").options.length>0)
      {
      	document.getElementById("a0101_box").focus();
      }               
}



function setSelectValue()
{
       var objid,i;
       var obj=$('a0101_box');
   	   for(i=0;i<obj.options.length;i++)
       {
          if(obj.options[i].selected)
            objid=obj.options[i].value
       }       
       if(objid)
       {
       		window.open("/general/template/edit_form.do?b_query=link&tabid=${khResultForm.templet_id}&ins_id="+objid+"&businessModel=2&sp_flag=2&returnflag=${khResultForm.templet_id}","_ad","top=0,left=5,height="+(window.screen.height-70)+",width="+(window.screen.width-20));
	          	   
       }
       Element.hide('a0101_pnl');         
}
</script>


 <div id="a0101_pnl"   style="border-style:nono">
  	<select name="a0101_box"  onblur="hideBox()"   multiple="multiple"  size="<%=(khResultForm.getRecordsList().size()+2)%>" class="dropdown_frame"  ondblclick='setSelectValue();'>    
    	<logic:iterate id="element"  property="recordsList" name="khResultForm" >
    		<option value='<bean:write name="element" property="dataValue"/>'><bean:write name="element" property="dataName"/></option>
    	</logic:iterate>
    </select>
  </div>  



</html:form>
</body>
<html:form action="/performance/interview/search_interview_list">
<input type="hidden" name="dd" value="3"/>
</html:form>
<script language="javascript" src="/general/sys/hjaxmanage.js"></script>
<script language="javascript">AxManager.writeCard();</script>
<script language="javascript">
              

         function hideBox()
         {
         	 Element.hide('a0101_pnl');
         }
         
         
         Element.hide('a0101_pnl');

         //根据页面宽度设置 haosl 2019-6-20
        /* var tblContainer = document.getElementById('tbl-container');//调整div 的width 属性值
         var document
         if(!getBrowseVersion()){//兼容非IE浏览器  wangb  20171205
        	 tblContainer.style.width = '88%';
        	 var div = document.getElementById('aa');//最下面一个table 
        	 div.style.marginTop='2px';
         }*/

         //这里代码导致列头宽度设置无效，应该是宽度不够，bug 49309 haosl delete 2019-6-20
         //bug 36821 table 宽度小了 右边留着大量空白    wangb 20180419
		/* var tbltable = tblContainer.getElementsByTagName('table')[0];
         tbltable.style.tableLayout = '';
         tbltable.setAttribute('width','96%');*/
         
</script>  
