<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="java.util.*,
				com.hjsj.hrms.actionform.general.deci.leader.LeaderForm,
				org.apache.commons.beanutils.LazyDynaBean" %>
<%
   UserView us = (UserView)session.getAttribute(WebConstant.userView);
   String bosflag = us.getBosflag();
   String frameMargin = "0px";
   if(bosflag.equals("hcm"))
	   frameMargin = "-10px";
%>
<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<script language="javascript" src="/js/validate.js"></script>
<script language="javascript">
function getcandi_leader(page,pagesize)
{
    var hashvo=new ParameterSet();
    hashvo.setValue("code","${leaderForm.code}");
    hashvo.setValue("kind","${leaderForm.kind}");
	hashvo.setValue("curpage",page);
	hashvo.setValue("pagesize",pagesize);
	var request=new Request({method:'post',asynchronous:false,onSuccess:setCandi_leader,functionId:'05603000050'},hashvo);
}
function setCandi_leader(outparamters)
{
   var candi_info=outparamters.getValue("candi_info");  
   if(candi_info!="noting")
   {
     var candi_tab=document.getElementById("candi_tab");
	 candi_tab.style.display="block";
     var candi_html=outparamters.getValue("candi_html");  
     candi_html=getDecodeStr(candi_html);
     var tr_str=document.getElementById('candi'); 
     tr_str.innerHTML=candi_html;     
   }else
   { 
      var candi_tab=document.getElementById("candi_tab");      
	  candi_tab.style.display="none";
   }
   
}
function getcandi_photo(page,pagesize)
{
    var hashvo=new ParameterSet();
    hashvo.setValue("code","${leaderForm.code}");
    hashvo.setValue("kind","${leaderForm.kind}");
	hashvo.setValue("curpage",page);
	hashvo.setValue("pagesize",pagesize);
	var request=new Request({method:'post',asynchronous:false,onSuccess:setCandi_photo,functionId:'05603000051'},hashvo);
}
function setCandi_photo(outparamters)
{
   var candi_html=outparamters.getValue("candi_html");  
   candi_html=getDecodeStr(candi_html);      
   var tr_str=document.getElementById('candi'); 
   tr_str.innerHTML=candi_html;
}

function getteam_photo(page,pagesize)
{
    var hashvo=new ParameterSet();
    hashvo.setValue("code","${leaderForm.code}");
    hashvo.setValue("kind","${leaderForm.kind}");
	hashvo.setValue("curpage",page);
	hashvo.setValue("pagesize",pagesize);
	var request=new Request({method:'post',asynchronous:false,onSuccess:setteam_photo,functionId:'05603000016'},hashvo);
}
function setteam_photo(outparamters)
{
   var candi_html=outparamters.getValue("candi_html"); 
   candi_html=getDecodeStr(candi_html);       
   var tr_str=document.getElementById('team'); 
   tr_str.innerHTML=candi_html;
}

function getteam_leader(page,pagesize)
{
    var hashvo=new ParameterSet();
    hashvo.setValue("code","${leaderForm.code}");
    hashvo.setValue("kind","${leaderForm.kind}");
	hashvo.setValue("curpage",page);
	hashvo.setValue("pagesize",pagesize);
	var request=new Request({method:'post',asynchronous:false,onSuccess:setteam_leader,functionId:'05603000014'},hashvo);
}
function setteam_leader(outparamters)
{
    var candi_info=outparamters.getValue("candi_info");  
    if(candi_info!="noting")
    {
      var candi_tab=document.getElementById("team_tab");
	  candi_tab.style.display="block";
      var candi_html=outparamters.getValue("candi_html"); 
      candi_html=getDecodeStr(candi_html);       
      var tr_str=document.getElementById('team'); 
      tr_str.innerHTML=candi_html;
    }else
    {
       var candi_tab=document.getElementById("team_tab");
	   candi_tab.style.display="none";
    }
}
function show_candi_stat()
{
  var t_url="/general/deci/leader/candi_stat.do?search=link`a_code=${leaderForm.a_code}";
  var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(t_url);
  t_url="/general/deci/leader/candi_stat.do?b_search=link&a_code=${leaderForm.a_code}&leader_type=candi";
  //var return_vo= window.showModalDialog(t_url,1, 
  //      "dialogWidth:686px; dialogHeight:624px;resizable:no;center:yes;scroll:yes;status:yes;scrollbars:yes");
  newwindow=window.open(t_url,'rr','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,top=70,left=170,width=736,height=614'); 
}
function show_team_stat()
{
  var t_url="/general/deci/leader/candi_stat.do?search=link`a_code=${leaderForm.a_code}";
  var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(t_url);
  t_url="/general/deci/leader/candi_stat.do?b_search=link&a_code=${leaderForm.a_code}&leader_type=team";
  //var return_vo= window.showModalDialog(t_url,1, 
  //      "dialogWidth:686px; dialogHeight:624px;resizable:no;center:yes;scroll:yes;status:yes;scrollbars:yes");
  newwindow=window.open(t_url,'rr','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,top=70,left=170,width=736,height=614'); 
}
function excecuteExcel()
{
    var x=document.body.scrollLeft+event.clientX+150;
	var y=document.body.scrollTop+event.clientY-100; 
	var waitInfo=eval("wait");
	waitInfo.style.top=y;
	waitInfo.style.left=x;	   
	waitInfo.style.display="block";	
	var hashvo=new ParameterSet();
	hashvo.setValue("code","${leaderForm.code}");
	hashvo.setValue("kind","${leaderForm.kind}");
	hashvo.setValue("a_code","${leaderForm.a_code}");
	function delay()   
	{   
		var request=new Request({method:'post',asynchronous:false,onSuccess:showExcel,functionId:'05603000015'},hashvo);
	} 
	window.setTimeout(delay,500); 
}	 
function showExcel(outparamters){
	var dis = outparamters.getValue("display");
	if(dis=='yes'){
		var waitInfo=eval("wait");	
		waitInfo.style.display="none";
		var url=outparamters.getValue("txtfile");
		var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+url);
	}
	else{
		var waitInfo=eval("wait");	
		waitInfo.style.display="none";
		alert(LEADRETEAM_LEADERFRAME_SET_MESSAGE);
	}
}
function MusterInitData()
   {
	   var waitInfo=eval("wait");	
	   waitInfo.style.display="none";
   }
var dragable=false;
var dy;
function down() {
 dragable=true;
 dy=event.clientY-td1.offsetHeight;
 event.srcElement.setCapture();
 }
function move() {
 if(dragable==true){
  td1.height=Math.min(Math.max(1,event.clientY-dy),document.body.clientHeight-parseInt(8)-15-21-1);
  }
 }
function up() {
 event.srcElement.releaseCapture();
 dragable=false;
 }
 var menuSwitch,o_ms,s,td_ms;
 function init()
 {
    o_ms = document.getElementById("menuSwitch");
    td_ms= document.getElementById("td1")
    s=false;
 }
 function changeTop()
 {
    var offset_Height=document.body.clientHeight;     
     s = !s;
     td_ms.height=s?offset_Height:"150"; 
     o_ms.innerHTML = s ?"<img border=0 src='/images/to_top.gif'>" : "<img border=0 src='/images/to_bottom.jpg'>";
 }
 function showunitcard()
{
	var t_url = "/general/deci/leader/ykcard.do?b_search=link";
	window.showModalDialog(t_url,1, 
        "dialogWidth:672px; dialogHeight:555px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
	
}
function winhref(url,a0100,target)
{
   if(a0100=="")
      return false;
   var o_obj=document.getElementById('a0100');   
   if(o_obj)
     o_obj.value=a0100;
    leaderForm.action=url;
    leaderForm.target=target;
    leaderForm.submit();
}
function document.oncontextmenu() 
{ 
      return　false; 
} 
function openwin(url)
	{
	   window.open(url,"_blank","left=0,top=0,width="+screen.availWidth+",height="+screen.availHeight+",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");
	}
	
	function openwin1(url)
{
   //window.open(url,'_blank','left=0,top=0,width='+screen.availWidth+',height='+screen.availHeight+',scrollbars=yes,toolbar=yes,menubar=yes,location=yes,resizable=no,status=yes');
   //browseForm.action=url;
   //browseForm.target="_blank";
   //browseForm.submit();
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(url);
       /*operuser中用户名*/
	   //newwindow=window.open(target_url,'app','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=180,left=350,width=530,height=500');  	
    var return_vo= window.showModalDialog(iframe_url,"app", 
       "dialogWidth:"+screen.availWidth+"; dialogHeight:"+screen.availHeight+";resizable:no;center:yes;scroll:yes;status:no");
}
function showsetinfo(setid,e)
{
	var obj = document.getElementById(setid);
	var obj1=document.getElementById(setid+"a");
		if(obj.style.display=='none'){
			if(obj.innerHTML.length==0){
				//var x=document.body.scrollLeft+event.clientX+150;
			    //var y=document.body.scrollTop+event.clientY-100; 
				//var waitInfo=eval("wait");
				//waitInfo.style.top=y;
				//waitInfo.style.left=x;	
				//waitInfo.style.display="block";
				var hashvo=new ParameterSet();
				hashvo.setValue("code","${leaderForm.code}");
				hashvo.setValue("kind","${leaderForm.kind}");
				hashvo.setValue("a_code","${leaderForm.a_code}");
				var h=0;
				if(setid=='setB')
					h=1;
				hashvo.setValue("h",h);
				function delay()   
				{   
					var request=new Request({method:'post',asynchronous:false,onSuccess:showLeaderAnalyse,functionId:'05603000023'},hashvo);
				} 
				window.setTimeout(delay,500); 
				function showLeaderAnalyse(outparamters){
					var txtfile=outparamters.getValue("txtfile"); 
	    			txtfile=getDecodeStr(txtfile); 
					obj.style.display='block';
					obj1.innerHTML='[隐藏]';
					obj.innerHTML=txtfile;
					//var waitInfo=eval("wait");	   
					//waitInfo.style.display="none";
				}
			}else{
				obj.style.display='block';
				obj1.innerHTML='[隐藏]';
			}
		}else{
			obj.style.display='none';
			obj1.innerHTML='[显示]';
		}
}
</script>
<body onload="doInit();">
<html:form action="/general/deci/leader/leaderframedata"> 
<input type="hidden" name="a0100" id="a0100">
<table width="100%" border="0" cellspacing="0" cellpadding="0" >
	<logic:notEqual name="leaderForm" property="columns" value="">
		<logic:equal value="1" name="leaderForm" property="param">
		    <tr> 
					<td align="left" id=td1 valign="top"   style="width: 100%;height: 315px;"> 
						<iframe src="/general/deci/leader/unitlist.do?b_search=link" frameborder="0" name="unit_list" width="100%" height="100%" scrolling="no" style="margin-top:<%=frameMargin %>;"></IFRAME>
					</td>
		 	</tr>
	 	</logic:equal>
	 	<logic:notEqual value="1" name="leaderForm" property="param">
		    <tr> 
					<td align="left" id=td1  style="width: 100%;height: 315px;" >
						<iframe src="/general/deci/leader/unitlist.do?b_search=link" frameborder="0" name="unit_list" width="100%" height="100%" scrolling="no" style="margin-top:<%=frameMargin %>;"></IFRAME>
					</td>
		 	</tr>
	 	</logic:notEqual>
	</logic:notEqual>
	<logic:equal value="1" name="leaderForm" property="param">
	<tr><td style="height:3px;">&nbsp;</td></tr>
   <tr > 
   <td align="left" valign="middle">
    <table width="100%" border="0" cellspacing="0" cellpadding="0" id="team_tab" style='display:block;'>
     <tr>
       <td style="padding-bottom: 5px;">
         <b><bean:message key="leaderteam.leaderframe.leaderteam"/></b>
         <a href="javascript:void(0);" onclick="getteam_leader(document.getElementById('curpage').value,document.getElementById('pagerows').value);"><bean:message key="leaderteam.leaderframe.memberlist"/></a> 
         <a href="javascript:void(0);" onclick="getteam_photo(document.getElementById('curpage').value,document.getElementById('pagerows').value);"><bean:message key="leaderteam.leaderframe.memberphoto"/></a>      
         <logic:notEmpty name="leaderForm" property="gcond">
         	<hrms:priv func_id="AK111,322041">
         		<a href="javascript:void(0);" onclick="show_team_stat();"><bean:message key="leaderteam.leaderframe.statisticanalyse"/></a>      
         	</hrms:priv>
         </logic:notEmpty>
         <logic:notEmpty name="leaderForm" property="display_field">
         	<hrms:priv func_id="AK112,322042">	
        	 	<a href="javascript:void(0);" onclick="excecuteExcel();"><bean:message key="leaderteam.leaderframe.builddatum"/></a>      
      		</hrms:priv>
      	</logic:notEmpty>
      </td>
	 </tr>
     <tr> 
     <td align="left" id="team">
    
     </td>
    </tr>
    <logic:notEmpty name="leaderForm" property="gcond">
    <tr><td style="height:3px;">&nbsp;</td></tr>
    <tr style="height: 20px;">
    	<td><b>领导班子成员分析</b> 
    		<a href="javascript:void(0);" onclick="showsetinfo('setA');" id='setAa'> 
            [显示]  
            </a>
        </td>
   	</tr>
   	<tr>
    	<td>
	   	<div id=setA style="display: none">
	   	
		</div>
		</td>
	</tr>
	</logic:notEmpty>										
   </table>
  </td>
  </tr>
  </logic:equal>
  <logic:equal value="2" name="leaderForm" property="param">
  <tr><td style="height:3px;">&nbsp;</td></tr>
  <tr> 
  <td align="left" valign="middle">
    <table width="100%" border="0" cellspacing="0" cellpadding="0" id="candi_tab" style='display:block;margin-left: 10px'>
      <tr>
        <td>
           <b><bean:message key="leaderteam.leaderframe.insupportcadre"/></b>
           <a href="javascript:void(0);" onclick="getcandi_leader(document.getElementById('curpage').value,document.getElementById('pagerows').value);"><bean:message key="leaderteam.leaderframe.memberlist"/></a> 
           <a href="javascript:void(0);" onclick="getcandi_photo(document.getElementById('curpage').value,document.getElementById('pagerows').value);"><bean:message key="leaderteam.leaderframe.memberphoto"/></a>      
          	 <logic:notEmpty name="leaderForm" property="gcond">
	          	 <hrms:priv func_id="AK131,322051">
	           		<a href="javascript:void(0);" onclick="show_candi_stat();"><bean:message key="leaderteam.leaderframe.statisticanalyse"/></a> 
	        	</hrms:priv>
        	</logic:notEmpty>
        </td>
      </tr>
      <tr>
         <td align="left" id="candi">
     
         </td>
      </tr>
      <logic:notEmpty name="leaderForm" property="gcond">
      <tr><td style="height:3px;">&nbsp;</td></tr>
      <tr style="height: 20px;">
    	<td><b>后备干部成员分析</b> 
    		<a href="javascript:void(0);" onclick="showsetinfo('setB');" id='setBa'> 
            [显示]  
            </a>
        </td>
   	</tr>
   	<tr>
    	<td>
	   	<div id=setB style="display: none">
		</div>
		</td>
	</tr>	
	</logic:notEmpty>	
    </table>
  </td>
  </tr>
  </logic:equal>    
 </table> 
</html:form>
</body>
<script language="javascript">
<logic:equal value="2" name="leaderForm" property="param">
getcandi_leader(1,20);
</logic:equal>
<logic:equal value="1" name="leaderForm" property="param">
getteam_leader(1,20);
</logic:equal>
</script>
<div id='wait' style='position:absolute;top:200;left:250;display:none;'>
  <table border="1" width="300" cellspacing="0" cellpadding="4" style="border-collapse: collapse;" bgcolor="#FFFFFF" height="87" align="center">
           <tr>
             <td bgcolor="#f4f7f7" style="font-size:12px;color:#000000" height=24><bean:message key="leaderteam.leaderframe.waitingmessage"/></td>
           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center>
               <marquee style="border:1px solid #000000" direction="right" width="300" scrollamount="5" scrolldelay="10" bgcolor="#FFFFFF">
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
</div> 
<script language="javascript">
 MusterInitData();
 init();
 function doInit(){
 	<logic:equal value="1" name="leaderForm" property="param">
 	function delayA()   
	{   
		var obj1=document.getElementById("setAa");
		if(obj1){
			obj1.click();
		}
	} 
	window.setTimeout(delayA,1500); 
	</logic:equal> 
	<logic:equal value="2" name="leaderForm" property="param">
	function delayB()   
	{   
		var obj1=document.getElementById("setBa");
		if(obj1){
			obj1.click();
		}
	} 
	window.setTimeout(delayB,4500); 
	</logic:equal> 
 }
</script>