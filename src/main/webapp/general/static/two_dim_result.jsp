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
<%@ page import="java.util.*,com.hjsj.hrms.actionform.general.statics.StaticFieldForm" %>


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

<SCRIPT language=JavaScript>
   function excecuteExcel()
   {
	var hashvo=new ParameterSet();			
	hashvo.setValue("userbase","${staticFieldForm.userbase}");
	hashvo.setValue("userbases","${staticFieldForm.userbases}");
	hashvo.setValue("mess","${staticFieldForm.mess}");
	hashvo.setValue("selOne","${staticFieldForm.selOne}");
	hashvo.setValue("selTwo","${staticFieldForm.selTwo}");
	hashvo.setValue("infor_Flag","${staticFieldForm.infor_Flag}");
	hashvo.setValue("result","${staticFieldForm.result}");
	hashvo.setValue("infor_Flag","${staticFieldForm.infor_Flag}");
	hashvo.setValue("htotal","${staticFieldForm.htotal}");
	hashvo.setValue("vtotal","${staticFieldForm.vtotal}");
	var request=new Request({method:'post',asynchronous:false,onSuccess:showExcel,functionId:'05301010018'},hashvo);
   }	
   function showExcel(outparamters)
   {
	var url=outparamters.getValue("excelfile");	
	var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+url,"excel");
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
   
   function vhtotal(flag,total){
  	 	waitingf();
		if(total==0){
			staticFieldForm.action="/general/static/two_dim_static.do?b_next=data&"+flag+"total=1";
			staticFieldForm.submit();
		}else{
			staticFieldForm.action="/general/static/two_dim_static.do?b_next=data&"+flag+"total=0";
			staticFieldForm.submit();
		}
	}
	function switchtype(value){
		waitingf();
		window.location.href="/general/static/two_dim_static.do?b_next=data&chartType="+value;
	}
	function testchart(e)
	{

  	 	var name=e.name;
   		if(name!="")
  	    {
  	    	waitingf();
      		name=getEncodeStr(name);
			var peneURL = "/general/static/two_dim_result.do?b_result=link&querycond=${staticFieldForm.querycond}&vv="+e.dataIndex+"&hh="+e.seriesIndex+"&flag=2&result=${staticFieldForm.result}&chartType=${staticFieldForm.chartType}&chartclick=true";
			window.location.href=peneURL;

        }
	}
</SCRIPT>
<hrms:themes />
<html:form action="/general/static/two_dim_result"> 
<logic:equal  name="staticFieldForm" property="chartFlag"  value="no">
<table align="center" width="70%" height="100%"  >
</logic:equal>
<logic:equal  name="staticFieldForm" property="chartFlag"  value="yes">
<table align="left" width="70%" height="100%" >
</logic:equal>
<tr><td valign="bottom" nowrap>
	      &nbsp;&nbsp;图形&nbsp;<html:select property="chartType" name="staticFieldForm" onchange="switchtype(this.value)">
	      	<html:option value="299">柱状图</html:option>
	      	<html:option value="11">折线图</html:option>
	      	<html:option value="33">堆叠条形图</html:option><!--add by xiegh on 20180611 陈总提：增加该种图表 type=33  -->
	      </html:select>
	    </td></tr>
<tr>
		<td align="left" nowrap colspan="5">
		<logic:equal  name="staticFieldForm" property="chartType"  value="299"><!-- 改 -->
			<hrms:chart name="staticFieldForm" title="" 
			scope="session" numDecimals="0" legends="histogramlist" data="" width="${staticFieldForm.chartWidth}" height="${staticFieldForm.chartHeight}" 
			chart_type="${staticFieldForm.chartType}" pointClick="testchart"
			 isneedsum="false" >
			</hrms:chart>
		</logic:equal>
		<logic:equal  name="staticFieldForm" property="chartType"  value="11">
			<hrms:chart name="staticFieldForm" title="" 
			scope="session" numDecimals="0" legends="dataMap" data="" width="${staticFieldForm.chartWidth}" height="${staticFieldForm.chartHeight}" 
			chart_type="${staticFieldForm.chartType}"  pointClick="testchart" 
			 isneedsum="false" >
			</hrms:chart>
		</logic:equal>
		<logic:equal  name="staticFieldForm" property="chartType"  value="33">
			<hrms:chart name="staticFieldForm" title="" 
			scope="session" numDecimals="0" legends="dataMap" data="" width="${staticFieldForm.chartWidth}" height="${staticFieldForm.chartHeight}" 
			chart_type="${staticFieldForm.chartType}"  pointClick="testchart" 
			 isneedsum="false" >
			</hrms:chart>
		</logic:equal>		
		</td>
	</tr>
</table>
<logic:equal  name="staticFieldForm" property="chartFlag"  value="no">
<table width="70%" border="0" cellspacing="0"  align="center" cellpadding="0">
    <tr>
      <td align="center"  nowrap>
 
      </td>                	    	    	    		        	        	        
   </tr>      
</table>
<table width="70%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
           <td align="center" class="TableRow" nowrap>
               <%--<bean:write name="staticFieldForm" property="querycond"/>二维统计不显示查询机构代码 wangb 20180804 bug 39340  --%>
           </td> 
            <logic:iterate id="element" name="staticFieldForm" property="dlist">
              <td align="center" class="TableRow" nowrap>
                 <bean:write name="element" property="legend"/>
              </td>   
             </logic:iterate>                      	    	    	    		        	        	        
           </tr>
   	  </thead>
   	  <logic:iterate id="element" name="staticFieldForm" property="hlist" indexId="indexh">
   	  <tr>
           <td align="center" class="TableRow" nowrap>
               <bean:write name="element" property="legend"/> 
           </td> 
            <logic:iterate id="helement" name="staticFieldForm" property="dlist" indexId="indexv">
              <td align="center" class="RecordRow" nowrap>
              	<bean:define id="cellvalue" value="${staticFieldForm.doublevalues[indexv][indexh]}"/>
                <logic:equal name="cellvalue" value="0">
                	0
                </logic:equal>
                <logic:notEqual name="cellvalue" value="0">
              		<a href="/general/static/two_dim_result.do?b_result=link&querycond=${staticFieldForm.querycond}&vv=${indexv}&hh=${indexh}&flag=2&result=${staticFieldForm.result}">${staticFieldForm.doublevalues[indexv][indexh]}</a>
                </logic:notEqual>
              </td> 
               </logic:iterate>          	    	    	    		        	        	        
           </tr> 
           </logic:iterate>  
</table>

<table width="70%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
<tr>             
  	      <td align="left" nowrap height="35">
  	      
  	           <%--  	   <bean:write name="staticFieldForm" property="snamedisplay" />&nbsp;(<bean:message key="workbench.stat.stattotalvalue"/><bean:write name="staticFieldForm" property="tovalue" />)
      &nbsp;&nbsp;&nbsp; --%>
     		<logic:equal value="0" name="staticFieldForm" property="vtotal" >
     			<input type="checkbox" onclick="vhtotal('v',${staticFieldForm.vtotal });" /><bean:message key="planar.across.total"/>
     		</logic:equal>
     		<logic:equal value="1" name="staticFieldForm" property="vtotal" >
     			<input type="checkbox" checked="checked" onclick="vhtotal('v',${staticFieldForm.vtotal });" /><bean:message key="planar.across.total"/>
     		</logic:equal>
     		<logic:equal value="0" name="staticFieldForm" property="htotal" >
     			<input type="checkbox" onclick="vhtotal('h',${staticFieldForm.htotal });" /><bean:message key="planar.portrait.total"/>
     		</logic:equal>
     		<logic:equal value="1" name="staticFieldForm" property="htotal" >
     			<input type="checkbox" checked="checked" onclick="vhtotal('h',${staticFieldForm.htotal });" /><bean:message key="planar.portrait.total"/>
     		</logic:equal>
     		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
              <hrms:submit styleClass="mybutton" property="br_back">
                <bean:message key="static.back"/>
	             </hrms:submit>
	             <input type="button" name="b_excel" value="<bean:message key="goabroad.collect.educe.excel"/>" class="mybutton" onclick="excecuteExcel();">
            </td>
            </tr>
            </table>
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

