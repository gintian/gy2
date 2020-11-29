<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@ page import="com.hjsj.hrms.actionform.stat.StatForm" %>
<%@ page import="com.hjsj.hrms.utils.PubFunc" %>
<html>
<%
String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	StatForm statForm=(StatForm)session.getAttribute("statForm");
	String bosflag="";
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
	  	bosflag=userView.getBosflag();
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");          
	}
	String statid= statForm.getStatid();
	//liuy 2014-10-8 修改常用统计穿透id暴露的问题
	String encryptParam=PubFunc.encrypt("statid="+statid);
%>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=8;">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<title>人力资源信息管理系统</title>
	<!-- 引入ext框架      wangb 20180207 -->
	 <script language="JavaScript" src="../../../module/utils/js/template.js"></script>
	 <script language="JavaScript" src="../../../components/extWidget/field/CodeTreeCombox.js"></script>
	 <script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
       <SCRIPT LANGUAGE=javascript>
   	/*******************************
   	 *设置统计信息
   	 *******************************/
    	function statset()
    	{
    	   var theurl="/general/static/commonstatic/statset.do?b_search=link`target=il_body`isoneortwo=1`statid=${statForm.statid}`istwostat=1";
    	   //newwindow=window.open(target_url,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,top=170,left=220,width=400,height=468'); 
           var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
           var dw=500,dh=425,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
           /*
           var config = {id:'statset_showModalDialogs',width:dw,height:dh,type:'0'};
       	   modalDialog.showModalDialogs(iframe_url,'',config,statset_callbackfunc);
            if(getBrowseVersion() && getBrowseVersion()!=10){//ie 兼容性视图
            	var return_vo= window.showModalDialog(iframe_url,0, 
             	 "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:no;status:no");
            	if(return_vo)
              		if(return_vo.flag=="true")
            		{
            			var dw=300,dh=150;
						var x=(document.body.clientWidth-dw)/2;;
	    				var y=(document.body.clientHeight-dh)/2; 
						var waitInfo=eval("wait");
						waitInfo.style.top=y;
						waitInfo.style.left=x;
						waitInfo.style.display="block";
                		var href=return_vo.href;
                		window.location.href=href;
            		}
            }else{//非IE浏览器使用ext window显示  wangb 20180207 bug 34602
            	var dialog=[];dialog.dw=dw;dialog.dh=dh;dialog.iframe_url=iframe_url;
            	openWin(dialog);
            }
           */
           var dialog=[];dialog.dw=dw;dialog.dh=dh+40;dialog.iframe_url=iframe_url;
           openWin(dialog);
        }
    	function statset_callbackfunc(return_vo)
    	{
    		if(return_vo.flag=="true")
    		{
    			var dw=300,dh=150;
				var x=(document.body.clientWidth-dw)/2;;
				var y=(document.body.clientHeight-dh)/2; 
				var waitInfo=eval("wait");
				waitInfo.style.top=y;
				waitInfo.style.left=x;
				waitInfo.style.display="block";
        		var href=return_vo.href;
        		window.location.href=href;
    		}
    	}
        //ext window 弹窗 方法 
        function openWin(dialog){
    	    var height = "100%";
    	    if(getBrowseVersion()){//ie11 解决出现滚动条  wangz 2019-03-05
    	        height = "98%";
    	        dialog.dh = dialog.dh+10;
            }
		    Ext.create("Ext.window.Window",{
		    	id:'statistical',
		    	width:dialog.dw,
		    	height:dialog.dh,
		    	title:'<bean:message key="workbench.stat.statsettitle"/>',
		    	resizable:false,
		    	modal:true,
		    	autoScroll:true,
		    	renderTo:Ext.getBody(),
		    	html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height="+height+" width='100%' src='"+dialog.iframe_url+"'></iframe>",
		    	listeners:{
		    		'close':function(){
              			if(this.return_vo && this.return_vo.flag=="true")
            			{
            				var dw=300,dh=150;
							var x=(document.body.clientWidth-dw)/2;;
	    					var y=(document.body.clientHeight-dh)/2; 
							var waitInfo=eval("wait");
							waitInfo.style.top=y;
							waitInfo.style.left=x;
							waitInfo.style.display="block";
                			var href=this.return_vo.href;
                			window.location.href=href;
            			}
		    		}
		    	}
		    }).show();	
		    var dom = document.getElementById('statistical');
		  	if(dom){
		  		dom.style.zIndex='99999999';
		  	}
		}
   </SCRIPT> 
<script language="JavaScript">
function pf_ChangeFocus(e) 
			{
				  e=e?e:(window.event?window.event:null);//xuj update 2011-5-11 兼容firefox、chrome
			      var key = window.event?e.keyCode:e.which;
			      var t=e.target?e.target:e.srcElement;
			      if ( key==0xD && t.tagName!='TEXTAREA') /*0xD*/
			      {    
			   		   if(window.event)
			   		   	e.keyCode=9;
			   		   else
			   		   	e.which=9;
			      }
			   //按F5刷新问题,重复提交问题,右键菜单也设法去掉
			   if ( key==116)
			   {
			   		if(window.event){
			   		   	e.keyCode=0;
			   		   	e.returnValue=false;
			   		}else{
			   		   	e.which=0;
			   		   	e.preventDefault();
			   		}
			   }   
			   if ((e.ctrlKey)&&(key==82))//屏蔽 Ctrl+R  
			   {    
			        if(window.event){
			   		   	e.keyCode=0;
			   		   	e.returnValue=false;
			   		}else{
			   		   	e.which=0;
			   		   	e.preventDefault();
			   		}
			   } 
			}
function pf_return(form,element) 
{
	document.forms[form].elements[element].focus();
	return false;
}
function blackMaint(checkflag,target){
	if ('leaderdxt' == checkflag) {
        statForm.action="/general/tipwizard/tipwizard.do?br_leader=link";
        statForm.target=target;
        statForm.submit(); 
    }else if(checkflag=='hcm'){
		statForm.action="/templates/index/hcm_portal.do?b_query=link";
        statForm.target=target;
        statForm.submit(); 
	}else if(checkflag=='hl'){
		statForm.action="/templates/index/portal.do?b_query=link";
        statForm.target=target;
        statForm.submit(); 
    }else if(checkflag=='2'){
		statForm.action="/templates/index/portal.do?b_query=link";
		statForm.target=target;
		statForm.submit(); 
    } else {
		statForm.action="/system/home.do?b_query=link";
		statForm.target=target;
		statForm.submit(); 
	}
}

function testchart(e)
{
   var name=e.name;
   if(name!="")
   {
      	name=$URL.encode(getEncodeStr(name));
      	statForm.action="/general/static/commonstatic/statshow.do?b_data=data&encryptParam=<%=encryptParam%>&showLegend="+name+"&showflag=1&flag=12";
      	statForm.submit();
   }
}
function statsave()
       {
	
		     var theurl="/general/static/commonstatic/statshow.do?b_save=link`statid=${statForm.statid}";
             var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
			 if(getBrowseVersion()){
            	var return_vo= window.showModalDialog(iframe_url,0,"dialogWidth:400px; dialogHeight:408px;resizable:no;center:yes;scroll:no;status:no");
			 }else{
				 window.open(iframe_url,"_blank","left="+((screen.availWidth/2)-30)+",top=100,width=440,height=430,scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no"); 
			 }
            
       
       }
       
function changetype(v){
	window.location.href="/general/static/commonstatic/statshowchart.do?chart_type="+v+"&label_enabled=" + ${statForm.label_enabled };
}
function changesformula(sformula){
			var dw=300,dh=150;
			var x=(document.body.clientWidth-dw)/2;;
    		var y=(document.body.clientHeight-dh)/2; 
			var waitInfo=eval("wait");
			waitInfo.style.top=y;
			waitInfo.style.left=x;
			waitInfo.style.display="block";
			window.location.href="/general/static/commonstatic/statshow.do?b_chart=chart&statid=${statForm.statid }&sformula="+sformula;
		}

function changeLableEnable(){
    var label_enabled = document.getElementById("showLegndValue").checked;
    window.location.href="/general/static/commonstatic/statshowchart.do?chart_type=${statForm.chart_type}&label_enabled=" + label_enabled;
}
/*
 *创建机构筛选组件  wangb  20190819
 */
function createOrgSelector(){
	if('${statForm.org_filter}'=='1'){
		var value = '${statForm.filterId}'+'`'+'${statForm.filterName}';
		Ext.onReady(function(){
			Ext.widget("codecomboxfield",{
				border: false,
				onlySelectCodeset: false,
				codesetid: "UM",
				ctrltype: "1",
				editable: false,
				value:value,
				listeners: {
					afterrender: function () {
						// this.setValue("",true); //初始化赋值
					},
					select: function (a, b) {
						var dw=300,dh=150;
						var x=(document.body.clientWidth-dw)/2;;
	    				var y=(document.body.clientHeight-dh)/2; 
						var waitInfo=eval("wait");
						waitInfo.style.top=y;
						waitInfo.style.left=x;
						waitInfo.style.display="block";
						window.location.href = '/general/static/commonstatic/statshow.do?b_chart=chart&filter_type=1&filterId='+a.value;
					}
				},
				renderTo:document.getElementById("org")
			}).show();
		});
	}
}
</script>
   <link href="/css/css1.css" rel="stylesheet" type="text/css">
   <style type="">
   </style>
</head>
<hrms:themes />
<body onKeyDown="return pf_ChangeFocus(event);" style="height:100%;">
<br>

<html:form action="/general/static/commonstatic/statshow">
<logic:equal name="statForm" property="istwostat" value="2">
    <table width="80%" border="0" cellspacing="1"  align="center" cellpadding="1">
 <tr>  
     <td align="center"  nowrap>
         <bean:message key="workbench.stat.statidinfo"/>
         <%
           response.sendRedirect("/general/static/commonstatic/statshow.do?b_doubledata=data&statid="+statid+"");
          %>
     </td>        
  </tr>    
 </table> 
</logic:equal>
<logic:notEqual name="statForm" property="istwostat" value="2">
<table align="center" style="margin-top:10px;">
<tr align="left">  
 
 	<logic:equal name="statForm" property="org_filter" value="1">
		    <div style="position:absolute;top:10px;left:20px" id="org">
		    </div>
	</logic:equal>  	
  	<logic:notEqual name="statForm" property="org_filter"  value="1">
   		<logic:equal name="statForm" property="infokind" value="1">
    		<td valign="bottom"  nowrap>
    			<hrms:priv func_id="3221001,2602301,04010101">
       				<a href="####" onclick="javascript:statset();"><bean:message key="workbench.stat.statsettitle"/></a>   
     			</hrms:priv>      
    		</td>
   		</logic:equal>
   </logic:notEqual>
   <logic:notEqual name="statForm"  property="showsformula" value="1">
<%--     <td valign="bottom"  nowrap>
      &nbsp;&nbsp;<a href="/general/static/commonstatic/statshowchart.do?chart_type=12&label_enabled=${statForm.label_enabled }">立体直方图</a>
    </td> --%>
    <td valign="bottom"  nowrap>
      &nbsp;&nbsp;<a href="/general/static/commonstatic/statshowchart.do?chart_type=11&label_enabled=${statForm.label_enabled }">平面直方图</a>
    </td>
 <%--    <td valign="bottom"  nowrap>
      &nbsp;&nbsp;<a href="/general/static/commonstatic/statshowchart.do?chart_type=5&label_enabled=${statForm.label_enabled }">立体圆饼图</a>
    </td> --%>
       <td valign="bottom"  nowrap>
      &nbsp;&nbsp;<a href="/general/static/commonstatic/statshowchart.do?chart_type=55&label_enabled=${statForm.label_enabled }">雷达图</a>
    </td>
    <td valign="bottom"  nowrap>
      &nbsp;&nbsp;<a href="/general/static/commonstatic/statshowchart.do?chart_type=20&label_enabled=${statForm.label_enabled }">平面圆饼图</a>
    </td>  
    <td valign="bottom"  nowrap>
      &nbsp;&nbsp;<a href="/general/static/commonstatic/statshowchart.do?chart_type=1000&label_enabled=${statForm.label_enabled }">平面折线图</a>
    </td>
    </logic:notEqual>
    <logic:equal name="statForm"  property="showsformula" value="1">
	    <td valign="bottom" nowrap>
	      &nbsp;&nbsp;图形&nbsp;<html:select property="chart_type" name="statForm" onchange="changetype(this.value)">
	      	<html:option value="12">立体直方图</html:option>
	      	<html:option value="11">平面直方图</html:option>
<%-- 	      	<html:option value="5">立体圆饼图</html:option> --%>
	      	<html:option value="55">雷达图</html:option>
	      	<html:option value="20">平面圆饼图</html:option>
	      	<html:option value="1000">平面折线图</html:option>
	      </html:select>
	    </td>
    	<td valign="bottom" nowrap>
      &nbsp;&nbsp;统计方式&nbsp;<html:select property="sformula" name="statForm" onchange="changesformula(this.value);">
      	<html:optionsCollection property="sformulalist" value="dataValue" label="dataName" />
      </html:select>
      </td>
    </logic:equal>
    <logic:notEmpty name="statForm"  property="archive_set">
      <hrms:priv func_id="231706,2602306,04010106,2311036">
      <hrms:priv func_id="3221002,231708,2311038,2602307,04010107">
       <td valign="bottom"  nowrap>
       &nbsp;&nbsp;
       <a href="####" onclick="javascript:statsave()">统计数据归档</a>  
       </td>
      </hrms:priv> 
     </hrms:priv> 
    </logic:notEmpty>
<%--  
    <logic:equal name="statForm" property="chart_type" value="11">
    <logic:equal name="statForm"  property="label_enabled"  value="true" >
    	<td>
       		&nbsp;<input type="checkbox" id="showLegndValue" checked="checked" onclick="changeLableEnable();">显示统计值</input>
       </td>
    </logic:equal>
    <logic:notEqual name="statForm"  property="label_enabled"  value="true" >
    	<td>
       		&nbsp;<input type="checkbox" id="showLegndValue" onclick="changeLableEnable();">显示统计值</input>
       </td>
    </logic:notEqual>
	</logic:equal>

<logic:equal name="statForm" property="chart_type" value="12">
    <logic:equal name="statForm"  property="label_enabled"  value="true" >
    <td>
       &nbsp;<input type="checkbox" id="showLegndValue" checked="checked" onclick="changeLableEnable();">显示统计值</input>
    </td>
    </logic:equal>
    <logic:notEqual name="statForm"  property="label_enabled"  value="true" >
    <td>
       &nbsp;<input type="checkbox" id="showLegndValue" onclick="changeLableEnable();">显示统计值</input>
    </td>
    </logic:notEqual>
</logic:equal>
--%>
  </tr>
  </table>
<table   width="100%" height="500" align="center" border="0" cellpadding="0" cellspacing="0">
          <tr align="center">
            <td width="100%" height="500">
          <logic:equal  name="statForm"  property="chart_type"  value="5" >
             <hrms:chart name="statForm" isneedsum="${statForm.isneedsum }" xangle="${statForm.xangle }"  title="${statForm.snamedisplay}" scope="session" legends="list" data="" width="-1" height="-1" chart_type="${statForm.chart_type}" pointClick="testchart" numDecimals="${statForm.decimalwidth}" label_enabled="${statForm.label_enabled }" >
	 	  </hrms:chart>
          </logic:equal>
          <logic:equal  name="statForm"  property="chart_type"  value="1000" >
             <hrms:chart name="statForm" isneedsum="${statForm.isneedsum }" xangle="${statForm.xangle }"  title="${statForm.snamedisplay}" scope="session" legends="jfreemap" data="" width="-1" height="-1" chart_type="1000" pointClick="testchart" numDecimals="${statForm.decimalwidth}">
	 	  </hrms:chart>
          </logic:equal>
           <logic:notEqual name="statForm"  property="chart_type"  value="5" >   
            <logic:notEqual name="statForm"  property="chart_type"  value="1000" >
             <logic:notEqual name="statForm" property="chart_type" value="42">
			  <logic:notEqual name="statForm" property="chart_type" value="43">      
	      	<hrms:chart name="statForm" isneedsum="${statForm.isneedsum }" xangle="${statForm.xangle }" title="${statForm.snamedisplay}" scope="session" legends="list" data="" width="-1" height="-1" chart_type="${statForm.chart_type}" pointClick="testchart" label_enabled="${statForm.label_enabled }" numDecimals="${statForm.decimalwidth}">
	 	    </hrms:chart>
	 	    </logic:notEqual>
	 	  </logic:notEqual>
	 	   </logic:notEqual>
	 	  </logic:notEqual>
	 	  <logic:equal name="statForm" property="chart_type" value="42">
						<hrms:chart name="statForm" title="${statForm.snamedisplay}"
							scope="session" legends="list" data="" width="-1"
							height="-1" chart_type="${statForm.chart_type}" 
							pointClick="chartPointClick"
							numDecimals="${statForm.decimalwidth}"
							minvalue="${statForm.minvalue}" maxvalue="${statForm.maxvalue}"
							valves="${statForm.valves}" cvalues="${statForm.cvalues}">
						</hrms:chart>
					</logic:equal>
					<logic:equal name="statForm" property="chart_type" value="43">
						<hrms:chart name="statForm" title="${statForm.snamedisplay}"
							scope="session" legends="list" data="" width="-1"
							height="-1" chart_type="${statForm.chart_type}"
							pointClick="chartPointClick"
							numDecimals="${statForm.decimalwidth}"
							minvalue="${statForm.minvalue}" maxvalue="${statForm.maxvalue}"
							valves="${statForm.valves}" cvalues="${statForm.cvalues}">
						</hrms:chart>
					</logic:equal>
            </td>
          </tr>     
</table>
<div style="text-align:center;">

	           <%if(bosflag!=null&&(bosflag.equals("hl")||bosflag.equals("hcm"))){ 
	            request.setAttribute("bosflag",bosflag);
	           %>  
                 <logic:equal name="statForm" property="home" value="leaderdxt">
                    <html:button styleClass="mybutton" property="bc_btn1" onclick="blackMaint('leaderdxt','il_body');">
                                                         返回
                    </html:button>
                 </logic:equal>
                 <logic:equal name="statForm" property="home" value="5">    
                    <html:button styleClass="mybutton" property="bc_btn1" onclick="blackMaint('${bosflag }','il_body');">
					    返回
				    </html:button>
                 </logic:equal>
                 <logic:notEqual name="statForm" property="home" value="leaderdxt">
                 <logic:notEqual name="statForm" property="home" value="5">  
                   <logic:notEqual name="statForm" property="home" value="0">   
                     <hrms:tipwizardbutton flag="emp" target="il_body" formname="statForm"/>
                    </logic:notEqual> 
                 </logic:notEqual>
                 </logic:notEqual>
              <%}%>
             <logic:equal name="statForm" property="isshowstatcond" value="1">
            <logic:equal name="statForm" property="home" value="1">        
             <%if(bosflag!=null&&bosflag.equals("ul")){ %>  
               	   <html:button styleClass="mybutton" property="bc_btn1" onclick="blackMaint('1','i_body');">
					   返回
				    </html:button>
              <%}else{ %>
               	   <html:button styleClass="mybutton" property="bc_btn1" onclick="blackMaint('1','il_body');">
					    返回
				    </html:button>
              <%} %>
            </logic:equal>
            
   </logic:equal>
</div>
</logic:notEqual>

</html:form>
</body>
<script type="text/javascript">
function refresh(){
	if(document.getElementById("___CONTAINER___Nchart__0")){
		document.getElementById("___CONTAINER___Nchart__0").style.position="absolute";
		document.getElementById("___CONTAINER___Nchart__0").style.left="50%";
		document.getElementById("___CONTAINER___Nchart__0").style.margin="0 0 0 -335px";
		document.getElementById("___CONTAINER___Nchart__0").style.top="60px";
	}
}
<%--创建机构过滤--%>
createOrgSelector();
if(navigator.appName.indexOf("Microsoft")== -1)
	refresh();
</script>
</html>
<div id='wait' style='position:absolute;top:285;left:80;display:none;z-index:9999999999;'>
		<table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td class="td_style" height=24>
					正在提取数据,请稍候......
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10">
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