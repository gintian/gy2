<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
		<script type="text/javascript" src="/ext/adapter/ext/ext-base.js"></script>
		<script type="text/javascript" src="/ext/ext-all.js"></script>
		<script type="text/javascript" src="/ext/ext-lang-zh_CN.js"></script>
		<script type="text/javascript" src="/ext/rpc_command.js"></script>
		<script type='text/javascript' src='/ajax/basic.js'></script>
		<script type='text/javascript' src='/js/validate.js'></script>

<style type="text/css">
body {  
	background-color:#FFFFFF;
	
}
</style>
<%
	//String home=(String)request.getParameter("home");
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String url="";
	if(userView != null)
	{
		url=userView.getBosflag();
	}
	String orglabel=SystemConfig.getPropertyValue("orglabel");
	orglabel=new String(orglabel.getBytes("ISO8859_1"),"GB2312");
	pageContext.setAttribute("orglabel",orglabel);
	String hideOrg=request.getParameter("hideOrg");
	hideOrg=hideOrg==null?"":hideOrg;
	pageContext.setAttribute("hideOrg",hideOrg);
	String forward = "b_msgchart";
  	int height = 256;
  	if(request.getParameter("b_msgchart2")!=null){
  		height = 630;
  		forward = "b_msgchart2";  		
  	}
  	if(request.getParameter("b_gaugeboard")!=null){
  		height = 256;
  		forward = "b_gaugeboard";  		
  	}
%>
     <SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>
       <SCRIPT LANGUAGE=javascript>
   	/*******************************
   	 *设置统计信息
   	 *******************************/
    
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
      	     //name=getEncodeStr(name);
      	     //liuy 2015-1-20 6772：十四所：统计分析-信息集设置处未设置分类统计条件时，到总裁桌面点图列上的数字，点了没有反应，不对。 start
			 var lexprId = "";
			 if(document.getElementsByName('lexprId')[0]!=null)
			 	lexprId = document.getElementsByName('lexprId')[0].value;
			 //liuy 2015-1-20 end
      	     statForm.action="/general/static/commonstatic/statshow.do?b_data=data&userbases=${statForm.userbases}&statid=${statForm.statid}&showLegend="+name+"&showflag=1&flag=13&chart_type=${statForm.chart_type}&infokind=${statForm.infokind}&home=6&lexprId="+lexprId;
      	     <%if("bi".equals(url)){%>
      	     	statForm.target="i_body";
			 <%}else{%>
      	     	statForm.target="il_body";			        
			  <%}%>
      	     statForm.submit();
      	  }
       }
       function changeMsg()
       {  	
       		var sformula="";
       		var lexprId="";
       		//liuy 2015-2-4 7364：领导桌面：主页岗位分布，切换分类，报错 start
       		if(document.getElementsByName('sformula')&&document.getElementsByName('sformula')[0])     
				sformula = document.getElementsByName('sformula')[0].value;
			if(document.getElementsByName('lexprId')&&document.getElementsByName('lexprId')[0])
      	    	lexprId = document.getElementsByName('lexprId')[0].value; 
			if(document.getElementsByName('querycond')&&document.getElementsByName('querycond')[0])
	       		if("root"==(document.getElementsByName('querycond')[0]).value)
	       			(document.getElementsByName('orgName')[0]).value="";
	       	//liuy 2015-2-4 end   																			去掉 sformula参数 后台获取不到统计方式   wangb 20190624 bug 49376
          statForm.action="/general/static/commonstatic/statshowmsgchart.do?<%=forward%>=data&statid=${statForm.statid}&hideOrg=${hideOrg }&lexprId="+lexprId;         
      	  statForm.submit();
       } 
       
       function changesformula(sformula){
       		var lexprId = "";
       		if(document.getElementsByName('lexprId')&&document.getElementsByName('lexprId')[0])
				lexprId = document.getElementsByName('lexprId')[0].value; 
       		statForm.action="/general/static/commonstatic/statshowmsgchart.do?<%=forward%>=data&defaultNum=budgetSum&statid=${statForm.statid }&hideOrg=${hideOrg }&sformula="+sformula+"&lexprId="+lexprId;        
      	    statForm.submit();
			//window.location.href="/general/static/commonstatic/statshowmsgchart.do?b_msgchart=data&statid=${statForm.statid }&sformula="+sformula;
		}
		
		window.onload=function(){
			//var a= Integer.parse('${requestContext.agent.capabilities.width}');
		}
		
		function chartPointClick(e){
			<%	
			String pagename = request.getParameter("page");
			String pageid = request.getParameter("pageid");
			if("pcw".equalsIgnoreCase(pagename) || "rate".equalsIgnoreCase(pagename)) { %>
			var name=e.name;
			var map = new HashMap();
			map.put("pageid", '<%=pageid %>');
			
			Rpc( {
				functionId : '1010010103',
				success : linkpage
			}, map);
			
			function linkpage(response){
				var map = Ext.decode(response.responseText);
				var list = map.panelids;
		      	if(name!="") {
			      	if(list == null || list.length < 1)
				      	return;
			      	
		      	     name=getEncodeStr(name);
		      	     var panelids = list.split(",");
		      	     for(var i=0; i < panelids.length; i++){
						var id = panelids[i];
						if(id == null || id.length < 1)
							continue;
						
						var srcpath = parent.document.getElementById("iframe"+id).src;
						if(srcpath == null || srcpath.length < 1)
							continue;
						
						if(srcpath.indexOf("&lexprName") != -1)
							srcpath = srcpath.substring(0,srcpath.indexOf("&lexprName"));
						
						parent.document.getElementById("iframe"+id).src = srcpath + "&lexprName=" + name;
			      	 }
		      	     
		      	  }
			}
			<%} else{%>
				var name=e.name;
				var forw = "<%=forward%>";
      	  	if(name!="")
      	  		{
      	     	name=getEncodeStr(name);
      	     	if("b_msgchart"==forw)
      	     		statForm.target="${statForm.statid}";
      	     	else
      	     		statForm.target="i_body";
      	     	var lexprId = "";
			 	if(document.getElementsByName('lexprId')[0]!=null)
			 		lexprId = document.getElementsByName('lexprId')[0].value;
      	     	//statForm.action="/general/static/commonstatic/statshow.do?b_data=data&statid=${statForm.statid}&showLegend="+name+"&showflag=1&flag=13";
      	     	statForm.action="/general/static/commonstatic/statshow.do?b_data=data&userbases=${statForm.userbases}&statid=${statForm.statid}&showLegend="+name+"&showflag=1&flag=13&chart_type=${statForm.chart_type}&infokind=${statForm.infokind}&home=6&lexprId="+lexprId;
      	     	statForm.submit();
				}
			<%}%>
		}
		
//		function changeValue(value){
//			document.getElementById('cvalue').value = value;
//			alert(document.getElementById('cvalue').value);
//			changeMsg();
//		}	
		   
   </SCRIPT>
<hrms:themes/> 

<body bgcolor="#FFFFFF" >
<html:form action="/general/static/commonstatic/statshowmsgchart">
<html:hidden name="statForm" property="statid" value="${statForm.statid}"/>
<!-- html:hidden name="statForm" property="cvalues" styleId="cvalue" value="${statForm.cvalues}"/-->
<html:hidden name="statForm" property="label_enabled" value="${statForm.label_enabled }"/>
		<%	
		String onlychart = request.getParameter("onlychart");
			if(onlychart!=null){%>
		<logic:equal name="statForm" property="chart_type" value="1000">
					<hrms:chart name="statForm" isneedsum="${statForm.isneedsum }"
						xangle="${statForm.xangle }" title="${statForm.snamedisplay}"
						scope="request" legends="jfreemap" data="" width="-1"
						height="-1" chart_type="${statForm.chart_type}"
						pointClick="chartPointClick" numDecimals="${statForm.decimalwidth}" biDesk="true" pieoutin="true">
					</hrms:chart>
				</logic:equal>
				<logic:notEqual name="statForm" property="chart_type" value="1000">
					<logic:notEqual name="statForm" property="chart_type" value="42">
						<logic:notEqual name="statForm" property="chart_type" value="43">
							<logic:notEqual name="statForm" property="chart_type" value="20">
							<logic:notEqual name="statForm" property="chart_type" value="44">
							<hrms:chart name="statForm" isneedsum="${statForm.isneedsum }"
								xangle="${statForm.xangle }" title="${statForm.snamedisplay}"
								scope="request" legends="list" data="" width="-1" height="-1"
								chart_type="${statForm.chart_type}" pointClick="chartPointClick"
								label_enabled="${statForm.label_enabled }"
								numDecimals="${statForm.decimalwidth}" biDesk="true" pieoutin="true">
							</hrms:chart>
							</logic:notEqual>
							</logic:notEqual>
						</logic:notEqual>
					</logic:notEqual>
					<logic:equal name="statForm" property="chart_type" value="20">
						<hrms:chart name="statForm" title="${statForm.snamedisplay}" biDesk="true"
							scope="request" legends="list" data="" width="-1"
							height="-1" chart_type="${statForm.chart_type}" 
							pointClick="chartPointClick" xangle="${statForm.xangle }"
							numDecimals="${statForm.decimalwidth}"
							minvalue="${statForm.minvalue}" maxvalue="${statForm.maxvalue}"
							valves="${statForm.valves}" cvalues="${statForm.cvalues}" pieoutin="true">
						</hrms:chart>
					</logic:equal>
					<logic:equal name="statForm" property="chart_type" value="42">
					<div style="padding-left: 6%;padding-right: 6%;">
						<hrms:chart name="statForm" title="${statForm.snamedisplay}"
							scope="request" legends="list" data="" width="-1"
							height="-1" chart_type="${statForm.chart_type}" 
							pointClick="chartPointClick" xangle="${statForm.xangle }"
							numDecimals="${statForm.decimalwidth}"
							minvalue="${statForm.minvalue}" maxvalue="${statForm.maxvalue}"
							valves="${statForm.valves}" cvalues="${statForm.cvalues}" biDesk="true" pieoutin="true">
						</hrms:chart>
					</div>
					</logic:equal>
					<logic:equal name="statForm" property="chart_type" value="43">
						<hrms:chart name="statForm" title="${statForm.snamedisplay}"
							scope="request" legends="list" data="" width="-1"
							height="-1" chart_type="${statForm.chart_type}"
							pointClick="chartPointClick" xangle="${statForm.xangle }"
							numDecimals="${statForm.decimalwidth}"
							minvalue="${statForm.minvalue}" maxvalue="${statForm.maxvalue}"
							valves="${statForm.valves}" cvalues="${statForm.cvalues}" biDesk="true" pieoutin="true">
						</hrms:chart>
					</logic:equal>
					<logic:equal name="statForm" property="chart_type" value="44">
						<hrms:chart name="statForm" title="${statForm.snamedisplay}" biDesk="true"
							scope="request" legends="list" data="" width="-1"
							height="-1" chart_type="${statForm.chart_type}"
							pointClick="chartPointClick" xangle="${statForm.xangle }"
							numDecimals="${statForm.decimalwidth}"
							minvalue="${statForm.minvalue}" maxvalue="${statForm.maxvalue}"
							valves="${statForm.valves}" cvalues="${statForm.cvalues}" pieoutin="true">
						</hrms:chart>
					</logic:equal>
				</logic:notEqual>	
				<script type="text/javascript">
					var height = document.body.clientHeight;
					document.getElementById("table").height='100%';
					document.getElementById("tableaad").height=(height/10)*9;
				</script>
	<%}else{ %>


 <table id="tabTable"  align="center" width="100%" >
     <tr>  
       <td valign="top"  nowrap>
          <table>
             <tr> 
              	<td style="vertical-align: middle;">
               	<logic:notEqual value="true" name="statForm" property="isHideBiPanelOrg">
               	<logic:empty name="hideOrg">
                <span style="display: inline-block;">
                	<logic:empty name="orglabel">
                    	<bean:message key="label.select.org"/>&nbsp;<input style="vertical-align: middle;font-family:微软雅黑;font-size: 12px" type="text" name="orgName" value="${statForm.orgName}" readonly="readonly" onclick="openInputCodeDialogTextIncludeSetid('UM','orgName','querycond');"/>&nbsp;<img style="vertical-align: middle;" src="/images/code.gif" border="0"  onclick="openInputCodeDialogTextIncludeSetid('UM','orgName','querycond');"/>
           			</logic:empty>
           			<logic:notEmpty name="orglabel">
           				<bean:write name="orglabel"/><input type="text" style="vertical-align: middle;" name="orgName" value="${statForm.orgName}" size="20px" readonly="readonly" onclick="openInputCodeDialogTextIncludeSetid('UM','orgName','querycond');"/>&nbsp;<img style="vertical-align: top;" src="/images/code.gif" border="0"  onclick="openInputCodeDialogTextIncludeSetid('UM','orgName','querycond');"/>
           			</logic:notEmpty>
           			<!-- <select onchange="changeValue(this.value)">
						<option value="20">20</option>
						<option value="40">40</option>
						<option value="60">60</option>
						<option value="80">80</option>
					</select>
					 -->
          		</span>
         		<input type="hidden" name="querycond" value="${statForm.querycond}" onpropertychange="changeMsg();"/> 
          		</logic:empty>
               	</logic:notEqual>
                <logic:notEmpty name="statForm" property="condlist">
					<span style="display: inline-block;">
					<bean:message key="conlumn.mediainfo.info_title"/>
					<html:select name="statForm" property="lexprId" style="max-width: 140px; vertical-align: middle;"  size="1"  onchange="javascript:changeMsg();">
                           <html:optionsCollection property="condlist" value="dataValue" label="dataName"/>
					</html:select>&nbsp;
					</span>
                </logic:notEmpty> 
                <logic:equal value="1" name="statForm" property="showsformula">
               		<span style="display: inline-block;">
                		统计方式
                		<html:select property="sformula" name="statForm" style="max-width: 100px;font-size:13px; vertical-align: middle;"  onchange="changesformula(this.value);">
					    	<html:optionsCollection property="sformulalist" value="dataValue" label="dataName" />
						</html:select>&nbsp;
					</span>
                </logic:equal>
                <logic:notEqual name="statForm" property="chart_type" value="42">
                <logic:notEqual name="statForm" property="chart_type" value="43">
               	  <span style="display: inline-block;">
                  <bean:message key="general.inform.org.graph"/>&nbsp;
                  <select name="chart_type" size="0" style="max-width: 100px; vertical-align: middle;"  onchange="javascript:changeMsg();">
                     <logic:equal name="statForm" property="chart_type" value="12">
                       <option value="12" selected><bean:message key="static.figure.vertical_bar"/></option><!--原有的3D图 还不能注释了  因为这些type是配置在portal.xml中 如果找不到会默认显示第一个  这里兼容处理一下  -->
                     </logic:equal>                      
                     <logic:equal name="statForm" property="chart_type" value="11">
                       <option value="11" selected><bean:message key="static.figure.vertical_bar"/></option>
                     </logic:equal>
                     <logic:equal name="statForm" property="chart_type" value="5">
                       <option value="5" selected><bean:message key="static.figure.pie"/></option>
                     </logic:equal>
                     <logic:equal name="statForm" property="chart_type" value="20">
                       <option value="20" selected><bean:message key="static.figure.pie"/></option>
                     </logic:equal>
                      <logic:equal name="statForm" property="chart_type" value="1000">
                       <option value="1000" selected><bean:message key="static.figure.line"/></option>
                     </logic:equal>
                     <logic:equal name="statForm" property="chart_type" value="40">
                       <option value="40" selected><bean:message key="static.figure.bar_line"/></option>
                     </logic:equal>
              <%--        <logic:notEqual name="statForm" property="chart_type" value="12">
                       <option value="12"><bean:message key="static.figure.vertical_bar_3d"/></option>
                     </logic:notEqual> --%>
                     <logic:notEqual name="statForm" property="chart_type" value="11">
                       <option value="11" ><bean:message key="static.figure.vertical_bar"/></option>
                     </logic:notEqual>
                    <%--  <logic:notEqual name="statForm" property="chart_type" value="5">
                       <option value="5" ><bean:message key="static.figure.pie_3d"/></option>
                     </logic:notEqual> --%>
                     <logic:notEqual name="statForm" property="chart_type" value="20">
                     	<logic:notEqual name="statForm" property="chart_type" value="5"><%-- chart_type值为5时 同时显示2个平面饼图 wangb 20180726 bug 39048 --%>
	                       <option value="20" ><bean:message key="static.figure.pie"/></option>
                     	</logic:notEqual>
                     </logic:notEqual>                      
                     <logic:notEqual name="statForm" property="chart_type" value="1000">
                       <option value="1000" ><bean:message key="static.figure.line"/></option>
                     </logic:notEqual>
                     <logic:notEqual name="statForm" property="chart_type" value="40">
                       <option value="40"><bean:message key="static.figure.bar_line"/></option>
                     </logic:notEqual>                    
                  </select>
                  </span>  
                </logic:notEqual> 
                </logic:notEqual> 
                </td> 
             </tr>
          </table>
       </td>
     </tr>
			<tr id="tableaad">
				<td align="center" nowrap colspan="5" id="">
						<logic:equal name="statForm" property="chart_type" value="1000">
					<hrms:chart name="statForm" title="${statForm.snamedisplay}"
						scope="request" legends="jfreemap" data="" width="-1" height="-1" biDesk="true"
						chart_type="${statForm.chart_type}" pointClick="chartPointClick"
						numDecimals="${statForm.decimalwidth}" xangle="${statForm.xangle }"
						minvalue="${statForm.minvalue}" maxvalue="${statForm.maxvalue}"
						valves="${statForm.valves}" cvalues="${statForm.cvalues}" pieoutin="true">
					</hrms:chart>
				</logic:equal>
				<logic:notEqual name="statForm" property="chart_type" value="1000">
					<hrms:chart name="statForm" title="${statForm.snamedisplay}" biDesk="true"
						scope="request" legends="list" data="" width="-1" height="-1"
						chart_type="${statForm.chart_type}" pointClick="chartPointClick"
						numDecimals="${statForm.decimalwidth}" xangle="${statForm.xangle }"
						minvalue="${statForm.minvalue}" maxvalue="${statForm.maxvalue}"
						valves="${statForm.valves}" cvalues="${statForm.cvalues}" pieoutin="true">
					</hrms:chart>
					</logic:notEqual>
				</td>
			</tr>
		</table>
			<script type="text/javascript">	
				    var height = document.body.clientHeight;
					document.getElementById("tableaad").style.height=(height/10)*9;
					//bug 51367 wangbo 2019-08-07
					document.getElementById("tabTable").style.height=(height/10)*9;
					if(!getBrowseVersion() || getBrowseVersion()==10){
							window.onresize();
					}
					if(document.getElementById('__chart_generated_container____AnyChart___0')){
						document.getElementById('__chart_generated_container____AnyChart___0').style.height=(height/10)*9;
					}
				</script>
<%} %>
</html:form>
</body>
