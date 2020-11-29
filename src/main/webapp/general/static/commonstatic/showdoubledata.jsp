<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript" src="/js/validate.js"></script>
<%@ page import="com.hjsj.hrms.actionform.stat.StatForm" %>
<%@ page import="com.hjsj.hrms.utils.PubFunc" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
<%
String home=(String)request.getParameter("home");
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
String url="";
if(userView != null)
{
	url=userView.getBosflag();
}
StatForm statForm = (StatForm)session.getAttribute("statForm");
String querycond = statForm.getQuerycond();
querycond = querycond == null? "":querycond;
querycond = PubFunc.encrypt(querycond);
%>
<script language="JavaScript" src="../../../module/utils/js/template.js"></script>
<script language="JavaScript" src="../../../components/extWidget/field/CodeTreeCombox.js"></script>
<script language="javascript">
    var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	var ViewProperties=new ParameterSet();
</script>

  <SCRIPT LANGUAGE=javascript>
   	/*******************************
   	 *设置统计信息
   	 *******************************/
    	function statset()
    	{
    	   var theurl="/general/static/commonstatic/statset.do?b_search=link`target=il_body`isoneortwo=2`statid=${statForm.statid}`istwostat=2";
    	   //newwindow=window.open(target_url,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,top=170,left=220,width=400,height=468'); 
           var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
           var dw=500,dh=420,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
           /**
                add by sunming 2015-06-30 ie6下显示滚动条
             */
             if (isIE6()) {
             	dh=440;
             	dw=520;
             }
             /*兼容非IE浏览器弹窗  wangb 20180703  bug 38604 */
            if(getBrowseVersion()){//IE浏览器 
            	var return_vo= window.showModalDialog(iframe_url,0, 
              	"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:no;status:no");
            	if(return_vo)
            		statset_callbackfunc(return_vo);
            }else{//非IE浏览器 弹窗替换用 open弹窗
            	var iTop = (window.screen.availHeight - 30 - dh) / 2;  //获得窗口的垂直位置
				var iLeft = (window.screen.availWidth - 10 - dw) / 2; //获得窗口的水平位置 
            	window.open(iframe_url,'',"width="+dw+"px,height="+dh+"px,resizable=no,scrollbars=no,status=no,left="+iLeft+",top="+iTop);
            }
       }
   /**弹窗返回数据方法 wangb 20180703  bug 38604 */
   function statset_callbackfunc(return_vo){
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
   function excecuteExcel()
   {
	var hashvo=new ParameterSet();	
	hashvo.setValue("userbases","${statForm.userbases}");
	hashvo.setValue("userbase","${statForm.userbase}");
	hashvo.setValue("statid","${statForm.statid}");
	hashvo.setValue("querycond","${statForm.querycond}");
	hashvo.setValue("infokind","${statForm.infokind}");
	var curr = new Array();
	var a = new Array();
	<%
		//StatForm statForm = (StatForm)session.getAttribute("statForm");
		String[] curr_id = (String[])statForm.getCurr_id();
		if(curr_id!=null)
		for(int i=0;i<curr_id.length;i++){
	%>
	
	a[<%=i%>] = "<%=curr_id[i]%>";
	<%}%>
	for(var i=0;i<a.length;i++){
		curr.push(a[i]);
	}
	hashvo.setValue("curr_id",curr);
	hashvo.setValue("sformula","${statForm.sformula}");
	hashvo.setValue("preresult","${statForm.preresult}");
	hashvo.setValue("history","${statForm.history}");
	hashvo.setValue("result","${statForm.result}");
	hashvo.setValue("vtotal","${statForm.vtotal}");
	hashvo.setValue("htotal","${statForm.htotal}");
	hashvo.setValue("filterId","${statForm.filterId}");
	hashvo.setValue("org_filter","${statForm.org_filter}");
	var request=new Request({method:'post',asynchronous:false,onSuccess:showExcel,functionId:'02040001003'},hashvo);
   }	
   function showExcel(outparamters)
   {
	var url=outparamters.getValue("excelfile");	
	url = getDecodeStr(url);
	var win=open("/servlet/vfsservlet?fileid=" + url +"&fromjavafolder=true","excel");
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
  function changesformula(sformula){
  			var dw=300,dh=150;
			var x=(document.body.clientWidth-dw)/2;;
    		var y=(document.body.clientHeight-dh)/2; 
			var waitInfo=eval("wait");
			waitInfo.style.top=y;
			waitInfo.style.left=x;
			waitInfo.style.display="block";
			window.location.href="/general/static/commonstatic/statshow.do?b_doubledata=data&statid=${statForm.statid }&sformula="+sformula;
		}
	function vhtotal(flag,total){
		if(total==0){
			statForm.action="/general/static/commonstatic/statshow.do?b_doubledata=data&statid=${statForm.statid }&"+flag+"total=1";
			statForm.submit();
		}else{
			statForm.action="/general/static/commonstatic/statshow.do?b_doubledata=data&statid=${statForm.statid }&"+flag+"total=0";
			statForm.submit();
		}
	}
	function switchtype(value){
		waitingf();
		statForm.action="/general/static/commonstatic/statshow.do?b_doubledata=data&statid=${statForm.statid }&chart_type="+value;
		statForm.submit();
	}
	
	function testchart(e)
	{
  	 	var v = e.dataIndex;
  	 	var h  = e.seriesIndex;
		var name=e.name;
   		if(name!="")
  	    {
  	    	waitingf();
			var peneURL = "/general/static/commonstatic/statshow.do?b_double=link&querycond=${statForm.querycond}&v="+v+"&h="+h+"&flag=2&home=${statForm.home}";
			window.location.href=peneURL;

        }
	}
   function waitingf(){
			var dw=300,dh=150;
			var x=(document.body.clientWidth-dw)/2;;
   		var y=(document.body.clientHeight-dh)/2; 
			var waitInfo=eval("wait");
			waitInfo.style.top=y;
			waitInfo.style.left=x;
			waitInfo.style.display="block";
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
							window.location.href = '/general/static/commonstatic/statshow.do?b_doubledata=data&filter_type=1&filterId='+a.value;
						}
					},
					renderTo:document.getElementById("org")
				}).show();
			});
		}
	}
	createOrgSelector();
</SCRIPT> 

<hrms:themes/>
<html:form action="/general/static/commonstatic/statshow">
<logic:notEqual  name="statForm" property="onlychart"  value="1">
<table width="100%">
<tr><td>
	<table align="left" width="90%" height="100%" >
		<tr>
			 <td valign="bottom" nowrap>
			 		<logic:equal name="statForm" property="org_filter" value="1">
		    			<div style="margin-left:10px;float:left;" id="org">
		    			</div>
					</logic:equal> 
			      	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			      	图形&nbsp;<html:select property="chart_type" name="statForm" onchange="switchtype(this.value)">
			      	<html:option value="299">柱状图</html:option>
			      	<html:option value="11">折线图</html:option>
			      	<html:option value="33">堆叠条形图</html:option><!--add by xiegh on 20180611 陈总提：增加该种图表 type=33  -->
			      </html:select>
			    </td>
		</tr>
		<tr>
			<td align="center" nowrap colspan="5">
				<logic:equal  name="statForm" property="chart_type"  value="299"><!-- 改 -->
					<hrms:chart name="statForm" title="" 
					scope="session" numDecimals="0" legends="list" data="" width="${statForm.chartWidth}" height="400" 
					chart_type="${statForm.chart_type}" pointClick="testchart"
					 isneedsum="false" >
					</hrms:chart>
				</logic:equal>
				<logic:equal  name="statForm" property="chart_type"  value="11">
					<hrms:chart name="statForm" title="" 
					scope="session" numDecimals="0" legends="jfreemap" data="" width="${statForm.chartWidth}" height="400"
					chart_type="${statForm.chart_type}"  pointClick="testchart" 
					 isneedsum="false" >
					</hrms:chart>
				</logic:equal>	
					<logic:equal  name="statForm" property="chart_type"  value="33">
						<hrms:chart name="statForm" title="" 
						scope="session" numDecimals="0" legends="jfreemap" data="" width="${statForm.chartWidth}" height="400"
						chart_type="${statForm.chart_type}"  pointClick="testchart" 
						 isneedsum="false" >
						</hrms:chart>
					</logic:equal>	
			</td>
		</tr>
	</table>
</td></tr>
<tr><td>
	<table width="100%" border="0" cellspacing="0"  align="left" cellpadding="0" class="ListTable">
	   	  <thead>
	           <tr>
	           <td align="center" class="TableRow" nowrap>
	               <!--<bean:write name="statForm" property="querycond"/> --> 
	           </td> 
	            <logic:iterate id="element" name="statForm" property="varraylist">
	              <td align="center" class="TableRow" nowrap>
	                 <bean:write name="element" property="legend"/>
	              </td>   
	             </logic:iterate>                      	    	    	    		        	        	        
	           </tr>
	   	  </thead>
	   	  <logic:empty name="statForm" property="sformula">
	   	  <logic:iterate id="element" name="statForm" property="harraylist" indexId="indexh">
	   	  <tr>
	           <td align="center" class="TableRow" nowrap>
	               <bean:write name="element" property="legend"/> 
	           </td> 
	           <!-- liuy 2014-12-12 5888:员工管理/统计分析，模块中，去调0链接 start --> 
	            <logic:iterate id="helement" name="statForm" property="varraylist" indexId="indexv">
	             <logic:equal  name="statForm" property="home" value="1">
	              <td align="center" class="RecordRow" nowrap>
	              	<bean:define id="cellvalue" value="${statForm.statdoublevalues[indexv][indexh]}"/>
	                <logic:equal name="cellvalue" value="0">
	                	0
	                </logic:equal>
	                <logic:notEqual name="cellvalue" value="0">
	          			<a href="/general/static/commonstatic/statshow.do?b_double=link&querycond=<%=querycond %>&e_querycond=1&v=${indexv}&h=${indexh}&flag=2&home=${statForm.home}">${statForm.statdoublevalues[indexv][indexh]}</a>
	                </logic:notEqual>
	              </td> 
	              </logic:equal>
	               <logic:equal  name="statForm" property="home" value="2">
	              <td align="center" class="RecordRow" nowrap>
	                <bean:define id="cellvalue" value="${statForm.statdoublevalues[indexv][indexh]}"/>
	                <logic:equal name="cellvalue" value="0">
	                	0
	                </logic:equal>
	                <logic:notEqual name="cellvalue" value="0">
	                	<a href="/general/static/commonstatic/statshow.do?b_double=link&querycond=<%=querycond %>&e_querycond=1&v=${indexv}&h=${indexh}&flag=2&home=${statForm.home}">${statForm.statdoublevalues[indexv][indexh]}</a>
	                </logic:notEqual>
	              </td> 
	              </logic:equal>
	               <logic:equal  name="statForm" property="home" value="0">
	              <td align="center" class="RecordRow" nowrap>
	                <bean:define id="cellvalue" value="${statForm.statdoublevalues[indexv][indexh]}"/>
	                <logic:equal name="cellvalue" value="0">
	                	0
	                </logic:equal>
	                <logic:notEqual name="cellvalue" value="0">
	                <!-- liuy 2015-7-6 10775：BI平台主页二维统计，点统计表中的数字穿透到人员列表界面，但是人员列表被内嵌在小小的框里了，不方便。begin -->
	                <%if("bi".equals(url)){%>
		                <a href="/general/static/commonstatic/statshow.do?b_double=link&querycond=<%=querycond %>&e_querycond=1&v=${indexv}&h=${indexh}&flag=13&home=6" target="i_body">${statForm.statdoublevalues[indexv][indexh]}</a>
				 	<%}else{%>
	      	     		<a href="/general/static/commonstatic/statshow.do?b_double=link&querycond=<%=querycond %>&e_querycond=1&v=${indexv}&h=${indexh}&flag=2&home=${statForm.home}">${statForm.statdoublevalues[indexv][indexh]}</a>			        
				  	<%}%>
				  	<!-- liuy end -->
	                </logic:notEqual>
	              </td> 
	              </logic:equal>
	              <logic:equal  name="statForm" property="home" value="6">
	              <td align="center" class="RecordRow" nowrap>
	              	<bean:define id="cellvalue" value="${statForm.statdoublevalues[indexv][indexh]}"/>
	                <logic:equal name="cellvalue" value="0">
	                	0
	                </logic:equal>
	                <logic:notEqual name="cellvalue" value="0">
	                	<%if("bi".equals(url)){%>
			                <a href="/general/static/commonstatic/statshow.do?b_double=link&querycond=<%=querycond %>&e_querycond=1&v=${indexv}&h=${indexh}&flag=13&home=6" target="i_body">${statForm.statdoublevalues[indexv][indexh]}</a>
					 	<%}else{%>
			                <a href="/general/static/commonstatic/statshow.do?b_double=link&querycond=<%=querycond %>&e_querycond=1&v=${indexv}&h=${indexh}&flag=2&home=${statForm.home}">${statForm.statdoublevalues[indexv][indexh]}</a>
					  	<%}%>
	                </logic:notEqual>
	              </td> 
	              </logic:equal>
	              <logic:equal  name="statForm" property="home" value="5">
	              <td align="center" class="RecordRow" nowrap>
	                <bean:define id="cellvalue" value="${statForm.statdoublevalues[indexv][indexh]}"/>
	                <logic:equal name="cellvalue" value="0">
	                	0
	                </logic:equal>
	                <logic:notEqual name="cellvalue" value="0">
		                <a href="/general/static/commonstatic/statshow.do?b_double=link&querycond=<%=querycond %>&e_querycond=1&v=${indexv}&h=${indexh}&flag=2&home=${statForm.home}">${statForm.statdoublevalues[indexv][indexh]}</a>
	                </logic:notEqual>
	              </td>
	              </logic:equal>
	               </logic:iterate>          	    	    	    		        	        	        
	              <!-- liuy 2014-12-12 end --> 
	           </tr> 
	           </logic:iterate>
	         </logic:empty>
	         <logic:notEmpty name="statForm" property="sformula">
	   	  <logic:iterate id="element" name="statForm" property="harraylist" indexId="indexh">
	   	  <tr>
	           <td align="center" class="TableRow" nowrap>
	               <bean:write name="element" property="legend"/> 
	           </td> 
	            <logic:iterate id="helement" name="statForm" property="varraylist" indexId="indexv">
	             <logic:equal  name="statForm" property="home" value="1">
	              <td align="center" class="RecordRow" nowrap>
	                <a href="/general/static/commonstatic/statshow.do?b_double=link&querycond=<%=querycond %>&e_querycond=1&v=${indexv}&h=${indexh}&flag=2&home=${statForm.home}"><hrms:formatDecimals value="${statForm.statdoublevaluess[indexv][indexh]}" length="${statForm.decimalwidth}"></hrms:formatDecimals></a>
	              </td> 
	              </logic:equal>
	               <logic:equal  name="statForm" property="home" value="2">
	              <td align="center" class="RecordRow" nowrap>
	                <a href="/general/static/commonstatic/statshow.do?b_double=link&querycond=$<%=querycond %>&e_querycond=1&v=${indexv}&h=${indexh}&flag=2&home=${statForm.home}"><hrms:formatDecimals value="${statForm.statdoublevaluess[indexv][indexh]}" length="${statForm.decimalwidth}"></hrms:formatDecimals></a>
	              </td> 
	              </logic:equal>
	               <logic:equal  name="statForm" property="home" value="0">
	              <td align="center" class="RecordRow" nowrap>
	                <a href="/general/static/commonstatic/statshow.do?b_double=link&querycond=<%=querycond %>&e_querycond=1&v=${indexv}&h=${indexh}&flag=2&home=${statForm.home}"><hrms:formatDecimals value="${statForm.statdoublevaluess[indexv][indexh]}" length="${statForm.decimalwidth}"></hrms:formatDecimals></a>
	              </td> 
	              </logic:equal>
	              <logic:equal  name="statForm" property="home" value="6">
	              <td align="center" class="RecordRow" nowrap>
	                <a href="/general/static/commonstatic/statshow.do?b_double=link&querycond=<%=querycond %>&e_querycond=1&v=${indexv}&h=${indexh}&flag=2&home=${statForm.home}"><hrms:formatDecimals value="${statForm.statdoublevaluess[indexv][indexh]}" length="${statForm.decimalwidth}"></hrms:formatDecimals></a>
	              </td> 
	              </logic:equal>
	              <logic:equal  name="statForm" property="home" value="5">
	              <td align="center" class="RecordRow" nowrap>
	                <a href="/general/static/commonstatic/statshow.do?b_double=link&querycond=<%=querycond %>&e_querycond=1&v=${indexv}&h=${indexh}&flag=2&home=${statForm.home}"><hrms:formatDecimals value="${statForm.statdoublevaluess[indexv][indexh]}" length="${statForm.decimalwidth}"></hrms:formatDecimals></a>
	              </td> 
	              </logic:equal>
	               </logic:iterate>          	    	    	    		        	        	        
	           </tr> 
	           </logic:iterate>
	         </logic:notEmpty>
	</table>
</td></tr>
<tr><td>
	<table width="70%" border="0" style="margin-top: 4px;" cellspacing="1" align="center" cellpadding="0">
		<TR>
			<TD nowrap align="left">
				<logic:notEqual name="statForm" property="org_filter"  value="1">
	               <logic:equal name="statForm" property="infokind" value="1">
	               <hrms:priv func_id="3221001,2602301,04010101">
	                   <a href="javascript:statset()"><bean:message key="workbench.stat.statsettitle"/></a>     
	                </hrms:priv>  
	              </logic:equal>
	            </logic:notEqual>	
	                
	            <logic:equal name="statForm"  property="showsformula" value="1">
	              &nbsp;&nbsp;统计方式<html:select property="sformula" name="statForm" onchange="changesformula(this.value);">
			      <html:optionsCollection property="sformulalist" value="dataValue" label="dataName" />
			      </html:select>
			    </logic:equal>
	              &nbsp;&nbsp;&nbsp;
	       	   <%-- <bean:write name="statForm" property="snamedisplay" />&nbsp;<logic:notEqual value="false" name="statForm" property="isneedsum">(<bean:message key="workbench.stat.stattotalvalue"/><hrms:formatDecimals value="${ statForm.totalvalue }" length="${statForm.decimalwidth}"></hrms:formatDecimals>)</logic:notEqual> --%>
	     		&nbsp;&nbsp;&nbsp;
	     		<logic:equal value="0" name="statForm" property="vtotal" >
	     			<input type="checkbox" onclick="vhtotal('v',${statForm.vtotal });" /><bean:message key="planar.across.total"/>
	     		</logic:equal>
	     		<logic:equal value="1" name="statForm" property="vtotal" >
	     			<input type="checkbox" checked="checked" onclick="vhtotal('v',${statForm.vtotal });" /><bean:message key="planar.across.total"/>
	     		</logic:equal>
	     		<logic:equal value="0" name="statForm" property="htotal" >
	     			<input type="checkbox" onclick="vhtotal('h',${statForm.htotal });" /><bean:message key="planar.portrait.total"/>
	     		</logic:equal>
	     		<logic:equal value="1" name="statForm" property="htotal" >
	     			<input type="checkbox" checked="checked" onclick="vhtotal('h',${statForm.htotal });" /><bean:message key="planar.portrait.total"/>
	     		</logic:equal>
			
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<input type="button" name="b_excel" value="<bean:message key="goabroad.collect.educe.excel"/>" class="mybutton" onclick="excecuteExcel();">
			    <logic:equal  name="statForm"  property="home"  value="1" >
	            <input type="button" name="b_save" value="<bean:message key="button.return"/>" class="mybutton" onclick="returnhome('<%=url%>')">
	           </logic:equal>
	           <logic:equal  name="statForm"  property="home"  value="5" >
	            <input type="button" name="b_save" value="<bean:message key="button.return"/>" class="mybutton" onclick="returnhome('<%=url%>')">
	           </logic:equal>
	           <%if(url!=null&&url.equals("hl")){ %>  
	               <logic:equal  name="statForm"  property="home"  value="6" >
	                 <hrms:tipwizardbutton flag="emp" target="il_body" formname="statForm"/>
	              </logic:equal>
	           <%}%>
			 </TD>
		</TR>
	</table>
</td></tr>
</table>
</logic:notEqual>
<logic:equal  name="statForm" property="onlychart"  value="1">
<logic:equal  name="statForm" property="chart_type"  value="29"><!-- 改 -->
			<hrms:chart name="statForm" title="" 
			scope="session" numDecimals="0" legends="list" data="" width="${statForm.chartWidth}" height="400" 
			chart_type="${statForm.chart_type}"
			 isneedsum="false" >
			</hrms:chart>
		</logic:equal>
		<logic:equal  name="statForm" property="chart_type"  value="11">
			<hrms:chart name="statForm" title="" 
			scope="session" numDecimals="0" legends="jfreemap" data="" width="${statForm.chartWidth}" height="400" 
			chart_type="${statForm.chart_type}" 
			 isneedsum="false" >
			</hrms:chart>
		</logic:equal>	
		<script type="text/javascript">
			document.getElementById("table").height='100%';
		</script>
</logic:equal>	
</html:form>
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