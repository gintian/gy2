<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
<%@ page import="com.hjsj.hrms.actionform.stat.StatForm" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.hrms.struts.taglib.CommonData" %>
<% 
    StatForm statForm = (StatForm) session.getAttribute("statForm"); 
    ArrayList valuelist=statForm.getList();
%>
<%
//String home=(String)request.getParameter("home");
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
String url="";
if(userView != null)
{
	url=userView.getBosflag();
}
%>
     <!-- 引入ext框架      wangb 20180207 -->
	 <script language="JavaScript" src="../../../module/utils/js/template.js"></script>
	 <script language="JavaScript" src="../../../components/extWidget/field/CodeTreeCombox.js"></script>
	 
       <SCRIPT LANGUAGE=javascript>
   	/*******************************
   	 *设置统计信息
   	 *******************************/
    	function statset()
    	{
    	   
    	   //newwindow=window.open(target_url,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,top=170,left=220,width=400,height=468'); 
            var theurl="/general/static/commonstatic/statset.do?b_search=link`target=il_body`isoneortwo=1`statid=${statForm.statid}`istwostat=1";
            var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
            var dw=500,dh=468,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
            /*
            var config = {id:'statset_exportExcel',width:dw,height:dh,type:'0'};
        	modalDialog.showModalDialogs(iframe_url,'',config,statset_callbackfunc);
        	
            if(getBrowseVersion() && getBrowseVersion()!=10){//ie兼容模式下
            	var return_vo= window.showModalDialog(iframe_url,0, 
                "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:500px; dialogHeight:468px;resizable:no;center:yes;scroll:no;status:no");
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
        	var dialog=[];dialog.dw=dw;dialog.dh=dh;dialog.iframe_url=iframe_url;
            openWin(dialog);
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
       function returnhome(tar)
       {
         if(tar=="hl4")
         {
            statForm.action="/system/home.do?b_query=link";
            statForm.target="il_body";
            statForm.submit();
         }else if(tar=="hl")
         {
            statForm.action="/templates/index/portal.do?b_query=link";
            statForm.target="il_body";
            statForm.submit();
         }else if(tar=="hcm")
         {
             statForm.action="/templates/index/hcm_portal.do?b_query=link";
             statForm.target="il_body";
             statForm.submit();
          }
         else
         {
            statForm.action="/system/home.do?b_query=link";
            statForm.target="i_body";
            statForm.submit();
         }
       }
       
       function testchart(e)
       {
      	  var name=e.name;
      	  if(name!="")
      	  {
      	     name=getEncodeStr(name);
      	     statForm.action="/general/static/commonstatic/statshow.do?b_data=data&statid=${statForm.statid}&showLegend="+$URL.encode(name)+"&showflag=1&flag=12";
      	     statForm.submit();
      	  }
       }
       function statsave()
       {
            var theurl="/general/static/commonstatic/statshow.do?b_save=link`statid=${statForm.statid}";
            var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
            if(getBrowseVersion()){
	            var return_vo= window.showModalDialog(iframe_url,0,
	             "dialogWidth:400px; dialogHeight:410px;resizable:no;center:yes;scroll:no;status:no");
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
        
        function chartPointClick(){
			alert("测试仪表盘点击穿透方法");
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
   </SCRIPT>
   <hrms:themes/>
<html:form action="/general/static/commonstatic/statshow">
<%String onlychart=statForm.getOnlychart();
if(onlychart!=null){
statForm.setOnlychart(null); 
%>
<%String subtitle=statForm.getSubtitle();
if(subtitle==null){
%>
	<logic:equal name="statForm" property="chart_type" value="1000">
					<hrms:chart name="statForm" isneedsum="${statForm.isneedsum }"
						xangle="${statForm.xangle }" title="${statForm.snamedisplay}"
						scope="session" legends="jfreemap" data="" width="-1"
						height="-1" chart_type="${statForm.chart_type}"
						pointClick="testchart" numDecimals="${statForm.decimalwidth}" pieoutin="true">
					</hrms:chart>
				</logic:equal>
				<logic:notEqual name="statForm" property="chart_type" value="1000">
					<logic:notEqual name="statForm" property="chart_type" value="42">
						<logic:notEqual name="statForm" property="chart_type" value="43">
							<hrms:chart name="statForm" isneedsum="${statForm.isneedsum }"
								xangle="${statForm.xangle }" title="${statForm.snamedisplay}"
								scope="session" legends="list" data="" width="-1" height="-1"
								chart_type="${statForm.chart_type}" pointClick="testchart"
								label_enabled="${statForm.label_enabled }"
								numDecimals="${statForm.decimalwidth}" pieoutin="true">
							</hrms:chart>
						</logic:notEqual>
					</logic:notEqual>
					<logic:equal name="statForm" property="chart_type" value="42">
						<hrms:chart name="statForm" title="${statForm.snamedisplay}"
							scope="session" legends="list" data="" width="-1"
							height="-1" chart_type="${statForm.chart_type}" 
							pointClick="chartPointClick"
							numDecimals="${statForm.decimalwidth}"
							minvalue="${statForm.minvalue}" maxvalue="${statForm.maxvalue}"
							valves="${statForm.valves}" cvalues="${statForm.cvalues}" pieoutin="true">
						</hrms:chart>
					</logic:equal>
					<logic:equal name="statForm" property="chart_type" value="43">
						<hrms:chart name="statForm" title="${statForm.snamedisplay}"
							scope="session" legends="list" data="" width="-1"
							height="-1" chart_type="${statForm.chart_type}"
							pointClick="chartPointClick"
							numDecimals="${statForm.decimalwidth}"
							minvalue="${statForm.minvalue}" maxvalue="${statForm.maxvalue}"
							valves="${statForm.valves}" cvalues="${statForm.cvalues}" pieoutin="true">
						</hrms:chart>
					</logic:equal>
				</logic:notEqual>	
				<script type="text/javascript">
					document.getElementById("table").height='100%';
				</script>
	<%}else{
		statForm.setSubtitle(null);  
		ArrayList alllist = statForm.getList();
		%>
				全体职工人数（<%=Math.round(Float.parseFloat(((CommonData)alllist.get(0)).getDataValue())) %>人），其中正式用工（<%=Math.round(Float.parseFloat(((CommonData)alllist.get(1)).getDataValue())) %>人）、临时用工（<%=Math.round(Float.parseFloat(((CommonData)alllist.get(2)).getDataValue()))%>人）、工业（不含总部、疫苗）（<%=Math.round(Float.parseFloat(((CommonData)alllist.get(19)).getDataValue()))%>人）、直接生产（<%=Math.round(Float.parseFloat(((CommonData)alllist.get(16)).getDataValue())) %>人）、营销人员（<%=Math.round(Float.parseFloat(((CommonData)alllist.get(17)).getDataValue())) %>人）、科研人员（<%=Math.round(Float.parseFloat(((CommonData)alllist.get(18)).getDataValue()))%>人）
			<script type="text/javascript">
					document.getElementById("table").height='100%';
				</script>
	<%} %>
<%
}else{
%>
<table  align="center" style="margin-top:10px;">
<tr align="left">  
  	<logic:notEqual name="statForm"  property="showsformula" value="1">
  	<logic:equal name="statForm" property="org_filter" value="1">
		    <div style="position:absolute;top:10px;left:20px" id="org">
		    </div>
	</logic:equal>  	
  	<logic:notEqual name="statForm" property="org_filter"  value="1">
	  	<logic:equal name="statForm" property="infokind" value="1">
		    <td valign="bottom"  nowrap>
		    <hrms:priv func_id="3221001,2602301,04010101">
		        <a href="####" onclick="javascript:statset();">设置统计范围</a>   
		        </hrms:priv>  
		     &nbsp;&nbsp;
		    </td>
	    </logic:equal>
    </logic:notEqual>
   <%--  <td valign="bottom"  nowrap>
      &nbsp;&nbsp;<a href="/general/static/commonstatic/statshowchart.do?chart_type=12&label_enabled=${statForm.label_enabled }">立体直方图</a>
    </td> --%>
    <td valign="bottom"  nowrap>
      &nbsp;&nbsp;<a href="/general/static/commonstatic/statshowchart.do?chart_type=11&label_enabled=${statForm.label_enabled }">平面直方图</a>
    </td>
   <%--  <td valign="bottom"  nowrap>
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
    <logic:notEmpty name="statForm"  property="archive_set">
    <hrms:priv func_id="231706,2602306,04010106,2311036">
    <hrms:priv func_id="3221002,2602307,04010107,231708,2311038">
       <td valign="bottom"  nowrap>
       &nbsp;&nbsp;
       <a href="####" onclick="javascript:statsave()">统计数据归档</a>  
       </td>
    </hrms:priv> 
    </hrms:priv>
    </logic:notEmpty>
    </logic:notEqual>
    <logic:equal name="statForm"  property="showsformula" value="1">
    <logic:equal name="statForm" property="org_filter" value="1">
		    <div style="position:absolute;position:absolute;top:10px;left:20px" id="org">

		    </div>
	</logic:equal> 
    <logic:notEqual name="statForm" property="org_filter"  value="1">
    	<logic:equal name="statForm" property="infokind" value="1">
		    <td valign="bottom"  nowrap>
		    <hrms:priv func_id="3221001,2602301,04010101">
		        <a href="####" onclick="javascript:statset();">设置统计范围</a>   
		        </hrms:priv>  
		     &nbsp;&nbsp;
		    </td>
    	</logic:equal>
    </logic:notEqual>
    <td valign="bottom" nowrap>
      &nbsp;&nbsp;图形<html:select property="chart_type" name="statForm" onchange="changetype(this.value)">
      	<%-- <html:option value="12">立体直方图</html:option> --%>
      	<html:option value="11">平面直方图</html:option>
      	<%-- <html:option value="5">立体圆饼图</html:option> --%>
      	<html:option value="55">雷达图</html:option>
      	<html:option value="20">平面圆饼图</html:option>
      	<html:option value="1000">平面折线图</html:option>
      </html:select>
    </td>
    <td valign="bottom" nowrap>
		&nbsp;&nbsp;统计方式<html:select property="sformula" name="statForm" onchange="changesformula(this.value);">
      	<html:optionsCollection property="sformulalist" value="dataValue" label="dataName" />
      </html:select>     
    </td>
    <logic:notEmpty name="statForm"  property="archive_set">
    <hrms:priv func_id="231706,2602306,04010106,2311036">
    <hrms:priv func_id="3221002,2602307,04010107">
       <td valign="bottom"  nowrap>
       &nbsp;&nbsp;
        <a href="####" onclick="javascript:statsave()">统计数据归档</a>  
       </td>
    </hrms:priv> 
    </hrms:priv>
    </logic:notEmpty>
    </logic:equal>
<%-- 现程序默认去掉显示统计值。默认显示
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
 <table  align="center" width="100%" height="500" border='0' style="margin-bottom:10px;">
          <tr>
            <td align="center" nowrap colspan="5" id="" width="100%" height="100%">
				<logic:equal name="statForm" property="chart_type" value="1000">
					<hrms:chart name="statForm" isneedsum="${statForm.isneedsum }"
						xangle="${statForm.xangle }" title="${statForm.snamedisplay}"
						scope="session" legends="jfreemap" data="" width="-1"
						height="-1" chart_type="${statForm.chart_type}"
						pointClick="testchart" numDecimals="${statForm.decimalwidth}" pieoutin="true">
					</hrms:chart>
				</logic:equal>
				<logic:notEqual name="statForm" property="chart_type" value="1000">
					<logic:notEqual name="statForm" property="chart_type" value="42">
						<logic:notEqual name="statForm" property="chart_type" value="43">
							<logic:notEqual name="statForm" property="chart_type" value="44">
							  <hrms:chart name="statForm" isneedsum="${statForm.isneedsum }"
								xangle="${statForm.xangle }" title="${statForm.snamedisplay}"
								scope="session" legends="list" data="" width="-1" height="-1"
								chart_type="${statForm.chart_type}" pointClick="testchart"
								label_enabled="${statForm.label_enabled }"
								numDecimals="${statForm.decimalwidth}" pieoutin="true"> 
							</hrms:chart>
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
							valves="${statForm.valves}" cvalues="${statForm.cvalues}" pieoutin="true">
						</hrms:chart>
					</logic:equal>
					<logic:equal name="statForm" property="chart_type" value="43">
						<hrms:chart name="statForm" title="${statForm.snamedisplay}"
							scope="session" legends="list" data="" width="-1"
							height="-1" chart_type="${statForm.chart_type}"
							pointClick="chartPointClick"
							numDecimals="${statForm.decimalwidth}"
							minvalue="${statForm.minvalue}" maxvalue="${statForm.maxvalue}"
							valves="${statForm.valves}" cvalues="${statForm.cvalues}" pieoutin="true">
						</hrms:chart>
					</logic:equal>
						<logic:equal name="statForm" property="chart_type" value="44">
						<hrms:chart name="statForm" title="${statForm.snamedisplay}"
							scope="session" legends="list" data="" width="-1"
							height="-1" chart_type="${statForm.chart_type}"
							pointClick="chartPointClick"
							numDecimals="${statForm.decimalwidth}"
							minvalue="${statForm.minvalue}" maxvalue="${statForm.maxvalue}"
							valves="${statForm.valves}" cvalues="${statForm.cvalues}" pieoutin="true">
						</hrms:chart>
					</logic:equal>
				</logic:notEqual>
			</td>
          </tr>      
            <tr>
    
  </tr>    
</table>

<div style="text-align:center;">
	           <logic:equal  name="statForm"  property="home"  value="1" >
          &nbsp;&nbsp; <input type="button" name="b_save" value="<bean:message key="button.return"/>" class="mybutton" onclick="returnhome('<%=url%>')">
      </logic:equal>
   <logic:equal  name="statForm"  property="home"  value="5" >
          &nbsp;&nbsp; <input type="button" name="b_save" value="<bean:message key="button.return"/>" class="mybutton" onclick="returnhome('<%=url%>')">
   </logic:equal>
   <%if(url!=null&&url.equals("hl")){ %>  
      <logic:equal  name="statForm"  property="home"  value="6" >
           <hrms:tipwizardbutton flag="emp" target="il_body" formname="statForm"/>
       </logic:equal>
     <%}%>
</div>
<%} %>
</html:form>
<script type="text/javascript">
function refresh(){
	if(document.getElementById("___CONTAINER___Nchart__0")){
		document.getElementById("___CONTAINER___Nchart__0").style.position="absolute";
		document.getElementById("___CONTAINER___Nchart__0").style.left="50%";
		document.getElementById("___CONTAINER___Nchart__0").style.margin="0 0 0 -335px";
		document.getElementById("___CONTAINER___Nchart__0").style.top="60px";
	}
}
if(navigator.appName.indexOf("Microsoft")== -1)
refresh();
<%--创建机构过滤--%>
createOrgSelector();
</script>
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
