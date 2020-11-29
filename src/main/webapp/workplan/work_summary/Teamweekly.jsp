<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<%@ page import="java.util.*"%>
 <%--<hrms:themes /> 7.0css --%>
<link href="style/styleTeamWeek.css" rel="stylesheet" />
<link href="style/stars.css" rel="stylesheet" />




<style>
<!--
.hj-wzm-one-right a:active{width:80px;height:32px;background:#999999;};
-->
#tab a:visited {
	COLOR: #838383 !important; TEXT-DECORATION: none;font-size: 12px
}
</style>
<%
	String summaryType = request.getParameter("type");
	String year = request.getParameter("year") ;
	if(year == null) year="";
	String month = request.getParameter("month");
	if(month == null) month="";
	String cycle = request.getParameter("cycle");
	if(cycle == null) cycle="";
	String para = request.getParameter("querypara");
	if(para == null) para="";
	String commonpara = request.getParameter("commonpara");
	if(commonpara == null) commonpara="";
	String searchtype = request.getParameter("searchtype");
	String summaryWeek = request.getParameter("week")==null?"":request.getParameter("week");
%>


	<div class="hj-wzm-all">
    	<div class="hj-wzm-all-table">
    	  <div class="hj-wzm-one" style="border-bottom:1px #D5D5D5 solid;">
                <div class="hj-wzm-one-left" id='menubar'>
                    <a href="javascript:display('typelist','summarytype');" id="summarytype" style="text-align: right;width: 60px;margin-right: 30px;">
                    <span id='typetitle'></span>&nbsp;<img onclick="display('typelist','summarytype');" src="/workplan/image/jiantou.png" /></a>
                    <a href="javascript:display('yearlist','summaryyear');" id="summaryyear" style="text-align: right;width: 60px;display: none">
                    <span  id='yeartitle'></span>年&nbsp;<img onclick="display('typelist','summarytype');" src="/workplan/image/jiantou.png" />
                    </a>
                    <a href="javascript:display('monthlist','summarytype');" id="summarymonth" style="text-align: right;width: 80px;">
                    <span id="timetitle"></span>年<span id='monthtitle'></span>月&nbsp;<img onclick="display('typelist','summarytype');" src="/workplan/image/jiantou.png" /></a>
                     <input type="hidden" id="week" value="<%=summaryWeek %>" />
                     <input type="hidden" id="weeknum" value="" />
                     <input type="hidden" id="type" value="<%=summaryType %>" />
                     <input type="hidden" id="month" value="<%= month %>"/>
                     <input type="hidden" id="year" value="<%= year %>"/>
                     <input type="hidden" id="cycle" value="<%= cycle %>"/>
                     <input type="hidden" id="p0115" value=""/>
                     <input type="hidden" id="p011503" value="" />
                     <input type="hidden" id="score" value="" />
                     <input type="hidden" id="deptdesc" value="" />
                     <input type="hidden" id="yearListStr" value="" />                    
                    <input type="hidden" id="orgperson" value="" />
                    <input type="hidden" id="pagenum" value="1" />
                     <input type="hidden" id="weekstart" value="" />
                     <input type="hidden" id="weekend" value="" />
                     <input type="hidden" id="searchtype" value="<%=searchtype %>" />
                     <input type="hidden" id="commonsearchtext" value="<%=commonpara %>" />
                     <input type="hidden" id="summaryTypeJson" value="" />
                     <input type="hidden" id="belong_type" value="0" />
                      <%-- 每页多少条 --%>
                     <input type="hidden" id="hr_pagesize" value="11" />
                      <%-- 多少页 --%>
                     <input type="hidden" id="lastpage" value="" />
                     <%-- 本页状态 批准，未提交等 --%>
                     <input type="hidden" id="stateSign" value="" />
                </div>
              <div class="hj-wzm-xxk">
                  <div class="hj-wzm-shur" style="margin-top:5px;"><input type="text" style="height:22px;" class="hj-zm-xxk-style" id="searchtext" onblur="quickQueryTextBlur(this)"  onfocus="quickQueryTextFocus(this)" value="<%=para %>" onkeydown="javascript:if(event.keyCode==13) quicksearch();"/>
                      <a href="javascript:showsearch();" style="float:right;margin-top:2px;"><img src="/workplan/image/down.png" /></a>
                      <div class="hj-wzm-xxk-a" style="right:29px;top:6px"><a href="javascript:quicksearch();"><img src="/workplan/image/chaxun_no_trans.png" /></a></div>
                  </div>
              </div>
                <div class="hj-wzm-one-right" id="weeks" style="float:right;width:320px;">
                    <a href="###" onclick="checkweek('1')" >第一周</a>
                    <a href="###" onclick="checkweek('2')" >第二周</a>
                    <a href="###" onclick="checkweek('3')" ><%--    <img src="/workplan/image/123.png" /> --%> 第三周</a>
                    <a href="###" onclick="checkweek('4')" >第四周</a>
                    <a href="###" style = "display: none" id='fiveweek' onclick="checkweek('5')">第五周</a>
                </div>
            
                <div class="hj-wzm-one-right" id="quaters" style="display:none;float:right;width:320px;">
                    <a href="###" onclick="checkweek('1')" >第一季度</a>
                    <a href="###" onclick="checkweek('2')" >第二季度</a>
                    <a href="###" onclick="checkweek('3')" >第三季度</a>
                    <a href="###" onclick="checkweek('4')" >第四季度</a>
                </div>
                <div class="hj-wzm-one-right" id="halfyears" style="display:none;float:right;width:160px;">
                    <a href="###" onclick="checkweek('1')" >上半年</a>
                    <a href="###" onclick="checkweek('2')" >下半年</a>
                </div>
		

                <div class="hj-wzm-clock dropdownlist"  id="monthlist" style="display: none;">
                    <ul style="text-align:center;" >
                        <span style="color:#549FE3;">
                        <a s href="javascript:yearchange(-1);display('monthlist','summarytype');"><img style="margin-bottom: 3px;" src="/workplan/image/left2.gif" /></a>
                        <span id='myeartitle'></span>年 
                        <a href="javascript:yearchange(1);display('monthlist','summarytype');"><img style="margin-bottom: 5px;" src="/workplan/image/right2.gif" /></a>
                        </span>
                    </ul>
                    <ul id="months">
                        <li ><a href="###" onclick="hidemonth(2,'1',1)" >1月</a></li>
                        <li ><a href="###" onclick="hidemonth(2,'2',2)" >2月</a></li>
                        <li ><a href="###" onclick="hidemonth(2,'3',3)" >3月</a></li>
                        <li ><a href="###" onclick="hidemonth(2,'4',4)" >4月</a></li>
                        <li ><a href="###" onclick="hidemonth(2,'5',5)" >5月</a></li>
                        <li ><a href="###" onclick="hidemonth(2,'6',6)" >6月</a></li>
                        <li ><a href="###" onclick="hidemonth(2,'7',7)" >7月</a></li>
                        <li ><a href="###" onclick="hidemonth(2,'8',8)" >8月</a></li>
                        <li ><a href="###" onclick="hidemonth(2,'9',9)" >9月</a></li>
                        <li ><a href="###" onclick="hidemonth(2,'10',10)" >10月</a></li>
                        <li ><a href="###" onclick="hidemonth(2,'11',11)" >11月</a></li>
                        <li ><a href="###" onclick="hidemonth(2,'12',12)" >12月</a></li>
                    </ul>
                </div>
                <div class="hj-wzm-one-dinwei dropdownlist"  id="typelist" style="display: none;cursor: pointer;">
                    <ul>
                    </ul>
                </div>
                <div class="hj-wzm-one-dinwei dropdownlist"  id="yearlist" style="display: none;cursor: pointer;">
                    <ul>
                    </ul>
                </div>
            
            </div>
		<div class="hj-wzm-cxgn" style="display: none">
              	
              	<div class="hj-zm-cxgn">
                    <table width="100%" border="0">
					<tr>
						<input type="hidden" id="query_dept.value" name="query_dept.value" value="" class="text">
						<td width="100" align="right">
							部门
						</td>
						<td width="180" align="left">
							<input type="text" class="hj-zm-bumen hj-zm-bumen-line" readonly="readonly"
								name="query_dept.viewvalue" onchange="fieldcode(this,2);" />
                        </td>
                        <td align='left'>
                        	<img src="/images/code.gif"
                               onclick="openInputCodeDialogOrgInputPosForBatchUpdate('UM','query_dept.viewvalue','UN`',1,2);"
                                 align="absmiddle" />
                        </td>
					</tr>
				</table>
                </div>
                
                <div class="hj-zm-cxgn">
                    <table width="100%" border="0">
                      <tr>
                        <td width="100" align="right">姓名</td>
                        <td width="200" align="left"><input name="a0101" type="text" class="hj-zm-bumen hj-zm-bumen-line" /></td>
                      </tr>
                    </table>
                </div>
                
                <div class="hj-zm-cxgn">
                    <table width="100%" border="0">
                      <tr>
                        <td width="100" align="right">拼音简码</td>
                        <td width="200" align="left"><input type="text" name="pinyin" class="hj-zm-bumen hj-zm-bumen-line" /></td>
                      </tr>
                    </table>
                </div>
                
                <div class="hj-zm-cxgn">
                    <table width="100%" border="0">
                      <tr>
                        <td width="100" align="right">总结状态</td>
						<td width="200" align="left">
						<%--	<select id="plan_status" name="plan_status" size="1" class="hj-zm-bumen">
								<option value="">全部总结</option>
								<option value="score">已批准	</option>
								<option value="p011503">已提交</option>
								<option value="p011501">未提交</option>
							</select>  --%>
							<input type="hidden" name="plan_status" id="plan_status" value="" class="hj-zm-bumen"/>
							<input type="text" id="plantype" class="hj-zm-bumen-line" onclick="display('statuslist','plantype')" style="padding-left: 20px;width: 180px;height:18px;margin-left:10px;" readonly="readonly" value="全部总结"/>
						</td>
						<td align='left'>
							<img id="imga" src="/workplan/image/jiantou.png" onclick="display('statuslist','plantype')" style="cursor: pointer;"/>
						</td>
					</tr>
                    </table>
                </div>
                
                  
                <div class="hj-zm-cxgn">
                    <table width="100%" border="0">
                      <tr>
                        <td width="258" align="right">
                        <input type="button" value="查询" class="hj-zm-cx" style='background-color:#529FE5' onclick="commonsearch()"/>
                        <input type="button" value="重置" class="hj-zm-cx" style='background-color:#529FE5' onclick="resetHR()" /></td>
                        <td></td>
                      </tr>
                    </table>
                </div>
                      <div class="bh-clear"></div>
                         <div id="statuslist" class="hj-wzm-one-dinwei dropdownlist" style="display: none;z-index: 30;position: absolute;width:190px;">
                 	<ul>
                 	    <li><a href="###" style="float:none; width:180px;margin:0;padding-top:0;" onclick="statusvalue('','全部总结')" >全部总结</a></li>
                 	    <li><a href="###" style="float:none; width:180px;margin:0;padding-top:0;" onclick="statusvalue('score','已批准')" >已批准</a></li>
                 	    <li><a href="###" style="float:none; width:180px;margin:0;padding-top:0;" onclick="statusvalue('p011503','已提交')" >已提交</a></li>
                 	    <li><a href="###" style="float:none; width:180px;margin:0;padding-top:0;" onclick="statusvalue('p011501','未提交')" >未提交</a></li>
                    </ul>
                 </div>
                      
              </div>
        	</div>
        <div class="hj-wzm-all-left">
                  <%--<div class="hj-wzm-yue" style="height:20px;margin-top:2px;margin-bottom:5px;font-family: '微软雅黑';font-size: 18px;line-height: 20px;">
                  <span id="teamsummarydesc"></span>
        	   总结提交情况
                  </div>--%>
                	<div id="myTeam" class="hj-wzm-title">
                	
	          	<%--  <h2 id="teamsummarydesc"></h2> <span style="font-family: '微软雅黑';font-size: 18px;"></span>--%>
	            <p style="height:30px;padding-top:8px;">
	            	<b>提交情况：</b>
	            	&nbsp;&nbsp;应报：<a href="javascript:showTableByAjax('');" id="totalNum" >0</a>
	            	&nbsp;&nbsp;&nbsp;&nbsp;未报：<a href="javascript:showTableByAjax('p011501');" id="p011501Num" >0</a>
	            	&nbsp;&nbsp;&nbsp;&nbsp;已报：<a href="javascript:showTableByAjax('p011503');" id="p011503Num" >0</a>
	            	&nbsp;&nbsp;&nbsp;&nbsp;未批：<a href="javascript:showTableByAjax('ratified');" id="ratified" >0</a>
	            	&nbsp;&nbsp;&nbsp;&nbsp;已批：<a href="javascript:showTableByAjax('score');" id="scoreNum" >0</a>
	            	 </p>
	            	<div class="hj-zm-tixing"><a onclick="fontgrey(this);sendEmail('','more','','');" style="margin-left:45px;cursor:pointer;">提醒大家写总结</a>
	            	<a onclick="fontgrey(this);sendEmail('','more','approve','');" style="cursor:pointer;">提醒批准工作总结</a><a onclick="fontgrey(this);sendEmail('','more','contents','');" style="cursor:pointer;">提醒评价工作总结</a></div>
            </div>
            
              <div id="mySub_org"  class="hj-wzm-title">
	            <%-- <h2 id="suborgsummarydesc"> </h2> --%>
	            <p style="height:30px;padding-top:8px;">
	            	<b>提交情况：</b>
	            	&nbsp;&nbsp;应报：<a href="javascript:showTableByAjax('');"  id="subOrgTotalNum"></a>  
	            	&nbsp;&nbsp;&nbsp;&nbsp; 未报：<a href="javascript:showTableByAjax('p011501');"  id="subOrgP011501"></a> 
	            	&nbsp;&nbsp;&nbsp;&nbsp; 已报：<a href="javascript:showTableByAjax('p011503');" id="subOrgP011503"></a>  
	            	&nbsp;&nbsp;&nbsp;&nbsp;未批：<a href="javascript:showTableByAjax('ratified');" id="subOrgP011502"></a> 
	            	&nbsp;&nbsp;&nbsp;&nbsp;已批：<a href="javascript:showTableByAjax('score');" id="subOrgScore"></a> </p>
	            	<div class="hj-zm-tixing"><a style="margin-left:45px;cursor:pointer;" onclick="fontgrey(this);sendEmail('','more','','');">提醒大家写总结</a>
	            	<a onclick="fontgrey(this);sendEmail('','more','approve','');" style="cursor:pointer;">提醒批准工作总结</a><a onclick="fontgrey(this);sendEmail('','more','contents','');" style="cursor:pointer;">提醒评价工作总结</a></div>
            </div>
          
           
		  <div class="hj-wzm-tdzb-three">
           	<table width="100%" border="0" id="tab" style="border-top:1px #D5D5D5 solid; float:left;">

            </table>
          </div> 
            
               <div class="hj-wzm-gzzj-fanye">
               		<div class="hj-wzm-gzzj-fanye-left">
                    	<table width="280" border="0">
                          <tr >
                            <td width="58" id="page_now">第1页</td>
                            <td width="68" id="page_count">共1条</td>
                            <td width="68" id="page_numcount" >共1页</td>
                            <td width="24"></td>
                            <td width="24"></td>
                            <td width="26"></td>
                            <%-- <td width="24">每页</td>
                            <td width="24"><input type="text" class="hj-wzm-gzzj-table" /></td>
                            <td width="36"><a href="#">刷新</a></td> --%>
                          </tr>
                        </table>
                    </div>
                    <div class="hj-wzm-gzzj-fanye-right">
                    	<table width="240" border="0" align="right">
                          <tr>
                            <td width="24" name="pageup" >首页</td>
                            <td width="24" name="pageup" >上页</td>
                            <td width="24" name="pagedown"><a  href="javascript:changepagenum(1)">下页</a></td>
                            <td width="24" name="pagedown"><a  href="javascript:changepagenum(2)">末页</a></td>
                            <td width="12">第</td>
                            <td width="36"><input id="gopage" type="text" class="hj-wzm-gzzj-table" value="1" onKeyPress="if ((event.keyCode<48 || event.keyCode>57)) event.returnValue=false"  maxlength=4   onkeydown="javascript:if(event.keyCode==13) changepagenum(3);"/></td>
                            <td width="12">页</td>
                            <td width="36"><input type="button" onclick="changepagenum(3)" value="GO" class="hj-wzm-gzzj-go" /></td>
                          </tr>
                        </table>
                    </div>
               </div>
                  
        </div>
 
        </div>


<script type="text/javascript" src="js/hrworksummary.js"></script>
<script type="text/javascript" src="js/stars.js"></script>
<script type="text/javascript" src="js/sendemail.js"></script>
<script language="javascript">
//hidemonth参数 
var seldesc_a ='工作周报';
var selvalue_a =1;
var seltype_a =0;
function hidemonth(seltype, seldesc, selvalue){
    document.getElementById('week').value=""; //选月时 ，周重新赋值 
    var targetId = '';
    if (0 == seltype) { //周报类型
        targetId = "type";

        Ext.getDom("cycle").value = selvalue;
        
        //除周报外，其它都不需要显示月份选择
        Ext.get("summarymonth").setDisplayed(1 == selvalue);

        if (1 == selvalue)
            Ext.get("menubar").setWidth(320);
        else
            Ext.get("menubar").setWidth(280);

        Ext.get('weeks').setDisplayed(1==selvalue);
        Ext.get('quaters').setDisplayed(3==selvalue);
        Ext.get('halfyears').setDisplayed(5==selvalue);
        //Ext.get('months').setDisplayed(2==selvalue);
    }
    else if (1 == seltype) { //年
        targetId = "year";
        Ext.getDom("year").value = selvalue;
        Ext.get('timetitle').setHtml(seldesc);
		Ext.get('myeartitle').setHtml(seldesc);
    }
    else if (2 == seltype) {//月
        targetId = "month";
        Ext.getDom("month").value = selvalue;
		Ext.get("timetitle").setHtml(Ext.getDom('myeartitle').innerHTML);
		Ext.get("myeartitle").setHtml(Ext.getDom('myeartitle').innerHTML);
		Ext.get("yeartitle").setHtml(Ext.getDom('myeartitle').innerHTML);
    }
    else return;

    Ext.get(targetId+'list').setDisplayed(false);
    Ext.get(targetId+'title').setHtml(seldesc);

    if(0==seltype && 2== selvalue){
    	queryTeamPerson('month');
    }else if(2==seltype){
    	queryTeamPerson('week');
    }else{
	    queryTeamPerson('');
    }
    
    var months = Ext.query("#months li",false);
  	if(selvalue==1){//选中工作周报时显示全部月份haosl
       for(var j=0;j<months.length;j++){
            months[j].setDisplayed(true);
       }
   }
}

function tranNumberToChinese(aNum) {
	var numChn = ["一","二","三","四","五","六","七","八","九","十"];
	return numChn[aNum - 1];
}

function showSummaryDesc() {
	var cycle = Ext.get('cycle').getValue();
    var year = Ext.get('year').getValue();
    var month = Ext.get('month').getValue();
    var week = Ext.get('week').getValue();
    var weekstart = Ext.get('weekstart').getValue();
    var weekend = Ext.get('weekend').getValue();
    var cyclename = "";
    var desc = "";
    var summaryDetailDesc = year + "年";
    if (1 == cycle) {
        cyclename = "周";
        desc = month + "月周报"; 
        if (!Ext.isEmpty(weekstart)) {
	        var adate = dt = Ext.Date.parse(weekstart, "Y-m-d");
	        var startMonth = adate.getMonth()+1;
	        var startDay = adate.getDate();
	        
	        adate = Ext.Date.parse(weekend, "Y-m-d");;
	        var endMonth = adate.getMonth()+1;
	        var endDay = adate.getDate();
	        
	        summaryDetailDesc = summaryDetailDesc + month + "月第" + tranNumberToChinese(week) + "周" 
	                          + "（" + startMonth + "." + startDay + "~"+ endMonth + "." + endDay +"）";
        }
    } else {
        desc = year + "年";
        if (2 == cycle) {
        	cyclename = "月";
            desc = desc + "月";
            summaryDetailDesc = summaryDetailDesc + month + "月";
        }
        else if (3 == cycle) {
        	cyclename = "季";
            desc = desc + "季";
            summaryDetailDesc = summaryDetailDesc + "第" + tranNumberToChinese(week) + "季度";
        }
        else if (4 == cycle) {
        	cyclename = "年";
            desc = desc + "年";
        }
        else if (5 == cycle) {
        	cyclename = "半年";
            desc = desc + "半年";
            if (1 == week)
                summaryDetailDesc = summaryDetailDesc + "上半年";
            else 
                summaryDetailDesc = summaryDetailDesc + "下半年";
        }

        desc = desc + "报";
    }
    //Ext.getDom('teamsummarydesc').innerHTML = summaryDetailDesc;
}

// 选择周数
function checkweek(week)
{  
	document.getElementById('week').value=week;
	queryTeamPerson('');
}

function statusvalue(plan,value)
{
	document.getElementById('plantype').value=value;
	Ext.getDom('plan_status').value = plan;
}

function initYears(){

	if (!Ext.isEmpty(Ext.getDom("yearlist").innerHTML))
	    Ext.getDom("yearlist").innerHTML = "";

	var ul = document.createElement("ul"); 
	var yearStr = Ext.get('yearListStr').getValue();
	yearStr = yearStr.substr(1,yearStr.length-2);
	
	var yearList = yearStr.split(",");

	for(var i = 0 ;i < yearList.length; i++){
		var item = yearList[i];
		ul.innerHTML += "<li><a href='###' onclick=\"hidemonth(1,'"+item+"',"+item+")\">"+item+"年</a></li>";
	}
	Ext.getDom("yearlist").appendChild(ul);
}

Ext.onReady(function (){
	quickQueryTextBlur(Ext.getDom("searchtext"));
	 queryTeamPerson('');
	

	Ext.get(document).on("click", function(e) {
			    e = e || window.event;
	    var target = e.target || e.srcElement;
	    if (target.getAttribute("id") == "searchinput")
	        return false;
	    if (target.getAttribute("id") == "summarymonthtitle")
	        return false;
	    if (target.getAttribute("id") == "_e0122")
	        return false;

	    if (target.getAttribute("id") == "typelist")
            return false;
	    if (target.getAttribute("id") == "monthlist")
            return false;
        if (target.getAttribute("id") == "summarytype")
            return false;
        if (target.getAttribute("id") == "summarymonth") // 选择日期 月
            return false;
        if (target.getAttribute("id") == "yeartitle") // 选择日期 年
            return false;
        if (target.getAttribute("id") == "typetitle") // 选择日期
            return false;
        if (target.getAttribute("id") == "monthtitle") // 选择日期
            return false;
        if (target.getAttribute("id") == "timetitle") // 选择日期
            return false;
	    
	    if (target.getAttribute("id") == "plantype")
	        return false;
	    if (target.getAttribute("id") == "imga")
	        return false;
	    
	    var dropDownList = Ext.query("*[class$=dropdownlist]");
	    for (i=0;i<dropDownList.length;i++){
	        var aDropDown = Ext.get(dropDownList[i]);
	        
	        if (target != dropDownList[i])
	            aDropDown.setDisplayed(false);
	    }
	});
});

	
</script>