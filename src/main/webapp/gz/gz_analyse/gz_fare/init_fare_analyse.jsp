<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.gz.gz_analyse.GzAnalyseForm,java.util.ArrayList" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hjsj.hrms.utils.PubFunc,org.apache.commons.beanutils.LazyDynaBean,com.hrms.hjsj.sys.FieldItem"%>
<%
UserView userview=(UserView)session.getAttribute(WebConstant.userView);
    int width=600;
    if(userview.getVersion()>=50)
      width=-1;
    String bosflag = userview.getBosflag();
 GzAnalyseForm gzAnalyseForm = (GzAnalyseForm)session.getAttribute("gzAnalyseForm");    
 ArrayList alist = gzAnalyseForm.getAlist();
 int clospan=alist!=null?alist.size()+6:6;
 String chartkind=gzAnalyseForm.getChartkind();
 ArrayList tableHeaderList=gzAnalyseForm.getTableHeaderList();
 String m_code=gzAnalyseForm.getCode();
 if(m_code==null||m_code.trim().length()==0)
 	m_code=PubFunc.encrypt("null");
 else
 	m_code=PubFunc.encrypt(m_code);
 %>
<script language="JavaScript" src="/anychart/js/AnyChart.js"></script>
<style>
<!--
.TableRow2 {
	background-position : center left;
	font-size: 12px;  
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	height:30px;
	font-weight: bold;
	background-color:#f4f7f7;	
	/*
	color:#336699;
	*/
	valign:middle;
}
-->
</style>
<hrms:themes></hrms:themes>
<html:form action="/gz/gz_analyse/gz_fare/init_fare_analyse" method="post" style="height: 100%">
<html:hidden name="gzAnalyseForm" property="code"/>
<html:hidden name="gzAnalyseForm" property="charttype"/>

<table height="100%" width="90%" align="center" border="0" cellpadding="0" cellspacing="0" <%if("bi".equals(bosflag)){ %> style="margin-top: 10px;"<%} %>>
    <tr style="height: 20px">
        <td align="left">
            <div style="float: left">
                <logic:equal value="5" name="gzAnalyseForm" property="chartkind">
                    <strong>${gzAnalyseForm.totalValue}</strong>
                </logic:equal>
                <html:select name="gzAnalyseForm" property="planitemid" onchange="changesetid();"
                             style="margin-right: 0px">
                    <html:optionsCollection property="planitemlist" value="dataValue" label="dataName"/>
                </html:select>
            </div>
            <logic:equal value="5" name="gzAnalyseForm" property="chartkind">
                <div style="float: left;margin-left: 5px;">
                    <input type="button" class="mybutton" name="ff"
                           value="<bean:message key="general.inform.muster.output.excel"/>"
                           onclick="exportFile();"/>
                </div>
            </logic:equal>
            <div style="float: right;margin-left: 5px">
                <table align="center" style="width: 140px" border="0" cellpadding="0" cellspacing="0">
                    <tr>
                        <td align="right">
                            <html:text name="gzAnalyseForm" property="yearf"  onkeydown="checkItem()" style="width:100px;padding-top:0px;height: 20px;border-color:#a6a6a6 !important;" styleClass="inputtext"/><!-- modify by xiaoyun 2014-9-10 -->
                        </td>
                        <td align="left">
                            <table border="0" cellspacing="2" cellpadding="0">
                                <tr>
                                    <td>
                                        <button type="button" id="y_up" class="m_arrow" onclick="yincrease();">5
                                        </button>
                                    </td>
                                </tr>
                                <tr>
                                    <td>
                                        <button type="button" id="y_down" class="m_arrow" style="margin-bottom:8px;" onclick="ysubtract();">6
                                        </button>
                                    </td>
                                </tr>
                            </table>
                        </td>
                        <td width="10%" nowrap>
                            <logic:equal value="4" name="gzAnalyseForm" property="chartkind">
                                &nbsp;&nbsp;
                                <a href="javascript:changeChartff('2');">折线图</a>&nbsp;&nbsp;<a href="javascript:changeChartff('1');" >柱状图</a>
                            </logic:equal>
                        </td>
                    </tr>
                </table>
            </div>

        </td>
    </tr>
    <logic:equal value="1" name="gzAnalyseForm" property="chartkind">
        <tr>
            <td width="5%" height="5px"></td>
        </tr>
        <tr>
            <td colspan="3" width="90%" height="100%" id="pnl_0">
                <bean:define id="showbtlist" name="gzAnalyseForm" property="btlist"/>
                <hrms:chart name="gzAnalyseForm" title="年度工资总额使用情况图" scope="session" legends="btlist" data=""
                            width="<%=width%>" numDecimals="2" labelIsPercent="0" height="-1" chart_type="20"
                            chartpnl='pnl_0'>
                </hrms:chart>
            </td>
        </tr>
    </logic:equal>
    <logic:equal value="2" name="gzAnalyseForm" property="chartkind">
        <tr>
            <td height="5px"></td>
        </tr>
        <tr>
            <td colspan="3" height="100%" id="pnl_0">
                <bean:define id="showztlist" name="gzAnalyseForm" property="ztlist"/>
                <hrms:chart name="gzAnalyseForm" title="计划总额,实发总额,剩余额对比分析(${gzAnalyseForm.totalAmount})"
                            isneedsum="false" scope="session" legends="ztlist" data="" numDecimals="2"
                            labelIsPercent="0" width="<%=width%>" height="-1" chart_type="11" chartpnl='pnl_0'
                            tooltip_enabled="false">
                </hrms:chart>
            </td>
        </tr>
    </logic:equal>
    <logic:equal value="3" name="gzAnalyseForm" property="chartkind">
        <tr>
            <td colspan="3">
                <table width="100%" align="center" id="tab" border="0" cellpadding="0" cellspacing="0">
                    <tr>
                        <td align="center" colspan="5">
                            <strong>工资总额使用情况表</strong>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <table width="100%" align="center" id="tab" border="0" cellpadding="0" cellspacing="0"
                                   class="ListTable">
                                <thead>
                                <tr>
                                    <td class="TableRow" align="center">
                                        月份
                                    </td>
                                    <td class="TableRow" align="center">
                                        计划总额
                                    </td>
                                    <td class="TableRow" align="center">
                                        实发总额
                                    </td>
                                    <td class="TableRow" align="center">
                                        剩余总额
                                    </td>
                                    <td class="TableRow" align="center">
                                        超出%
                                    </td>
                                </tr>
                                </thead>
                                <% int j = 0; %>
                                <logic:iterate id="element" name="gzAnalyseForm" property="ltlist" offset="0"
                                               indexId="index">
                                    <%if (j % 2 == 0) { %>
                                    <tr class="trShallow">
                                                <%} else { %>
                                    <tr class="trDeep">
                                        <%}%>
                                        <td align="right" class="RecordRow">
                                            &nbsp;<bean:write name="element" property="month"/>
                                        </td>
                                        <td align="right" class="RecordRow">
                                            &nbsp;<bean:write name="element" property="planitem"/>
                                        </td>
                                        <td align="right" class="RecordRow">
                                            &nbsp;<bean:write name="element" property="realitem"/>
                                        </td>
                                        <td align="right" class="RecordRow">
                                            &nbsp;<bean:write name="element" property="balanceitem"/>
                                        </td>
                                        <td align="right" class="RecordRow">
                                            &nbsp;<bean:write name="element" property="adde"/>
                                        </td>
                                    </tr>
                                    <%j++; %>
                                </logic:iterate>
                            </table>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </logic:equal>
    <logic:equal value="4" name="gzAnalyseForm" property="chartkind">
        <tr>
            <td height="5px"></td>
        </tr>
        <tr>
            <td height="100%" colspan="3" id="chart1">
                <logic:equal value="2" name="gzAnalyseForm" property="charttype">
                    <hrms:chart name="gzAnalyseForm" title="月度工资总额使用情况图(${gzAnalyseForm.totalAmount})" isneedsum="false"
                                label_enabled="false" scope="session" legends="xtmap" data="" width="<%=width%>"
                                height="-1" chart_type="4" numDecimals="2" labelIsPercent="0"
                                chartParameter="chartParameter" chartpnl="chart1">
                    </hrms:chart>
                </logic:equal>
                <logic:equal value="1" name="gzAnalyseForm" property="charttype">
                    <hrms:chart name="gzAnalyseForm" title="月度工资总额使用情况图(${gzAnalyseForm.totalAmount})" isneedsum="false"
                                label_enabled="false" scope="session" legends="xtlist" data="" width="<%=width%>"
                                height="-1" chart_type="29" numDecimals="2" labelIsPercent="0"
                                chartParameter="chartParameter" chartpnl="chart1">
                    </hrms:chart>
                </logic:equal>
            </td>
        </tr>
    </logic:equal>
    <logic:equal value="5" name="gzAnalyseForm" property="chartkind">
    <tr>
        <td colspan=3 align="center" style="vertical-align:top">

            <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0" class="ListTable"
                   style="margin-top: 10px;">
                <tr>
                    <td align="center" class="TableRow" rowspan=2>月份</td>
                    <logic:iterate id="element" name="gzAnalyseForm" property="alist" indexId="index" offset="0">
                        <td align="center" colspan=2 class="TableRow"><bean:write name="element"
                                                                                  property="planitemdesc"/></td>
                    </logic:iterate>
                    <td rowspan="2" align="center" class="TableRow">月度发生额</td>
                    <td rowspan="2" align="center" class="TableRow">年累计发生额</td>
                    <td rowspan="2" align="center" class="TableRow">年度剩余额</td>
                    <td rowspan="2" align="center" class="TableRow">预算完成率</td>
                </tr>
                <tr>
                    <logic:iterate id="element2" name="gzAnalyseForm" property="alist" indexId="index" offset="0">
                        <td align="center" class="TableRow" height="35" nowrap>月度发生额</td>
                        <td align="center" class="TableRow" nowrap>使用占比</td>
                    </logic:iterate>
                </tr>
                <logic:iterate id="element3" name="gzAnalyseForm" property="dataList" indexId="index" offset="0">
                    <tr>
                        <td align='left' class="RecordRow" nowrap>&nbsp;<bean:write name="element3" property="month"/>&nbsp;</td>
                        <%
                            for (int i = 0; i < alist.size(); i++) {
                                LazyDynaBean bean = (LazyDynaBean) alist.get(i);
                                String planitem = (String) bean.get("planitem");
                                String realitem = (String) bean.get("realitem");
                        %>

                        <td align="right" class="RecordRow" nowrap><bean:write name="element3"
                                                                               property="<%=planitem.toLowerCase()%>"/></td>
                        <td align="right" class="RecordRow" nowrap><bean:write name="element3"
                                                                               property="<%=realitem.toLowerCase()%>"/></td>
                        <%
                            }
                        %>
                        <td align="right" class="RecordRow"><bean:write name="element3" property="ydfse"/></td>
                        <td align="right" class="RecordRow"><bean:write name="element3" property="ljfs"/></td>
                        <td align="right" class="RecordRow"><bean:write name="element3" property="ndsy"/></td>
                        <td align="right" class="RecordRow"><bean:write name="element3" property="yswcl"/></td>
                    </tr>
                </logic:iterate>
            </table>
        </td>
        </logic:equal>
        <input type="hidden" name="planItemDesc" value="${gzAnalyseForm.planItemDesc}"/>
                <%if(chartkind.equals("3")||chartkind.equals("5")){ %>
        <logic:notEqual value="-1" name='gzAnalyseForm' property="isHasAdjustSet">
    <tr>
        <td colspan="3" align="left"><br>
            总额调整记录
            <br>
        </td>
    </tr>
    <tr>
        <td colspan="3" align="center">
            <div style='overflow:auto;width:100%;height:150'>
                <table width="100%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
                    <thead>
                    <tr>
                        <logic:iterate id="header" name="gzAnalyseForm" property="tableHeaderList">
                            <td align="center" class='TableRow' nowrap>
                                <bean:write name="header" property="itemdesc"/>
                            </td>
                        </logic:iterate>
                    </tr>
                    </thead>
                    <logic:iterate id="element" name="gzAnalyseForm" property="adjustDataList" indexId="indexes">
                        <tr>
                            <%
                                for (int i = 0; i < tableHeaderList.size(); i++) {
                                    FieldItem item = (FieldItem) tableHeaderList.get(i);
                                    String ss = "left";
                                    if (item.getItemtype().equalsIgnoreCase("N"))
                                        ss = "right";
                            %>
                            <td align="<%=ss%>" class='RecordRow'>
                                &nbsp;<bean:write name="element" property="<%=item.getItemid().toLowerCase()%>"/>&nbsp;
                            </td>

                            <%} %>
                        </tr>
                    </logic:iterate>
                </table>
            </div>
        </td>
    </tr>
    </logic:notEqual>
    <%} %>


</table>
<script type="text/javascript">
<!--
function exportFile()
{
//3020130067
  var hashVo=new ParameterSet();
  hashVo.setValue("year","${gzAnalyseForm.yearf}");
  hashVo.setValue("code","${gzAnalyseForm.code}");
  hashVo.setValue("chartkind","${gzAnalyseForm.chartkind}");
  hashVo.setValue("planitem","${gzAnalyseForm.planitemid}");
  hashVo.setValue("planItemDesc",gzAnalyseForm.planItemDesc.value);
  hashVo.setValue("m_code","<%=m_code%>");
  var request=new Request({method:'post',asynchronous:false,onSuccess:export_tax_ok,functionId:'3020130067'},hashVo);	
}
function export_tax_ok(outparameters)
{
   var outName=outparameters.getValue("outName");
   outName=getDecodeStr(outName);
   var win=open("/servlet/vfsservlet?fileid="+outName+"&fromjavafolder=true","excel");
}
function changesetid()
{
   var kind="${gzAnalyseForm.chartkind}";
   var chartkind="${gzAnalyseForm.chartkind}";
   var planItemDesc="";
   if(chartkind=='3'||chartkind=='5')
   {
       var obj=document.getElementById("planitemid");
       if(obj)
       {
          for(var i=0;i<obj.options.length;i++)
          {
             if(obj.options[i].selected)
             {
                chartkind=obj.options[i].value.substring(0,1);
                planItemDesc=obj.options[i].text;
             }
          }
       }
   }
   gzAnalyseForm.planItemDesc.value=planItemDesc;
   gzAnalyseForm.action="/gz/gz_analyse/gz_fare/init_fare_analyse.do?b_query=query&chartkind="+chartkind+"&opt=second&option=two";
   gzAnalyseForm.submit();
}
function changeChartff(flag)
{
  
   gzAnalyseForm.action="/gz/gz_analyse/gz_fare/init_fare_analyse.do?b_query=query&opt=second&option=two&charttype="+flag;
   gzAnalyseForm.submit();
}
//var yearnum = document.getElementById("yearf").value;
var yearnum = document.getElementsByName("yearf")[0].value;//非ie浏览器 name属性不能做为id属性 
var yearset = parseInt(yearnum);
function yincrease(){
	yearset = yearset+1;
//	document.getElementById("yearf").value = yearset;
	document.getElementsByName("yearf")[0].value = yearset;
	gzAnalyseForm.action="/gz/gz_analyse/gz_fare/init_fare_analyse.do?b_query=query&opt=second&option=two";
    gzAnalyseForm.submit();
}
function ysubtract(){
	if(yearset<1991){
//		document.getElementById("yearf").value = 1990;
		document.getElementsByName("yearf")[0].value = 1990;
	}else{
		yearset = yearset-1;
//		document.getElementById("yearf").value = yearset;
		document.getElementsByName("yearf")[0].value = yearset;
	}
	gzAnalyseForm.action="/gz/gz_analyse/gz_fare/init_fare_analyse.do?b_query=query&opt=second&option=two";
    gzAnalyseForm.submit();
}
function checkItem()
{
  var code=window.event.keyCode;
  if(code==13)
  {
  if(document.getElementById("yearf").value.length==4)
  {
    gzAnalyseForm.action="/gz/gz_analyse/gz_fare/init_fare_analyse.do?b_query=query&opt=second&option=two";
    gzAnalyseForm.submit();
    }
  }
  else if(code==8||code==46)
  {
  }
  else if(code<48)
      window.event.returnValue=false;
  else if(code>57&&code<96)
       window.event.returnValue=false;
  else if(code>105)
   window.event.returnValue=false;
}
function bgc(obj)
{
	if(obj.style.backgroundColor!=''){
		obj.style.backgroundColor = '' ;
	}else{
		obj.style.backgroundColor = '#add6a6' ;
	}
}
//-->
</script>
</html:form>
<script>
var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
var isFF = userAgent.indexOf("Firefox") > -1; //判断是否Firefox浏览器
if(isFF){//火狐浏览器 样式兼容  wangb 201807102 bug 38752
	var tds = document.getElementsByClassName('m_arrow');
	for(var i =0 ; i < tds.length ; i++){
		var tid = tds[i].getAttribute('id');
		if(!(tid == 'y_up' || tid == 'y_down'))
			continue;
		if(tid == 'y_up')
			tds[i].innerHTML='▲';
			
		if(tid == 'y_down')
			tds[i].innerHTML='▼';
				
		tds[i].style.fontSize='7';
		tds[i].style.height ='10px';
	}
}

</script>