<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script type="text/javascript" src="../../../../js/hjsjUrlEncode.js"></script>
<%
//String home=(String)request.getParameter("home");
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
String url="";
if(userView != null)
{
	url=userView.getBosflag();
}

String showreturn = "1";
if(null != request.getParameter("showreturn"))
    showreturn = (String)request.getParameter("showreturn");
%>
<style>
a:hover{color:pink;}
<!--
.div_table{
    border-width: 1px;
    BORDER-BOTTOM: #aeac9f 1pt solid; 
    BORDER-LEFT: #aeac9f 1pt solid; 
    BORDER-RIGHT: #aeac9f 1pt solid; 
    BORDER-TOP: #aeac9f 1pt solid ; 

}
.tdFontcolor{
	text-decoration: none;
	Font-family:????;
	font-size:12px;
	height=20px;
	align="center"
}
-->
</style>
     <SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>
       <SCRIPT LANGUAGE=javascript>
       function returnhome()
       {
            historyStatForm.action="/workbench/browse/history/showinfo.do?b_search=link&action=showinfodata.do&target=nil_body&userbase=usr";
            historyStatForm.target="il_body";
            historyStatForm.submit();
       }
       
       function testchart(e)
       {
      	  var key,name, backdates;
      	  if("${historyStatForm.chart_type}" == '20'){//饼状图
      		  name = e.name;
      		  backdates = 'Series 1';
      	  }
   		  if("${historyStatForm.chart_type}" == '11')//折线图
   		  {
   			backdates = e.name;
   			name = e.seriesName;
   		  }
   		  if("${historyStatForm.chart_type}" == '29'){
   			//name = e.seriesName;
   			//backdates = e.name;
   			name = e.name;//传入的是统计项的名称不是 统计图名称  wangb 20180713 bug 38814
   			backdates = e.seriesName;
   		  }
      	  if(name!="")
      	  {
      	     /* name=getEncodeStr(name); */
      	     name = $URL.encode(name);
      	     historyStatForm.action="/general/static/commonstatic/history/statshow.do?b_data=data&statid=${historyStatForm.statid}&showLegend="+name+"&showflag=1&flag=12&type=1&tmpbackdates="+backdates;
      	     
      	     historyStatForm.submit();
      	  }
       }
       function changechar_type(val){
       		historyStatForm.action="/general/static/commonstatic/history/statshowchart.do?b_chart=link&showreturn=<%=showreturn%>&chart_type="+val;
       		historyStatForm.submit();
       }
       
       
       /*function screen(obj,flag)
{
  var targetvalue=document.getElementById(flag); 
  targetvalue.value=obj.value; 
}*/
function   addDict(obj,event,flag)
{ 
	var ff=document.getElementById('dict').style.display;
	if('block'==ff){
		hiddendict('');
		//document.getElementById('dict').style.display="none";
		return;
	}
   var evt = event ? event : (window.event ? window.event : null);
   var np=   evt.keyCode; 
   if(np==38||np==40){ 
   
   } 
   var aTag;
   	aTag = obj;   
   var un_vos=document.getElementsByName("allbackdates");
   var backdates=document.getElementsByName("backdates")[0].value;
   if(!un_vos)
		return false;
   var unStrs=un_vos[0].value;	
   var unArrs=unStrs.split(",");
   var   c=0;
   var   rs   =new   Array();
   for(var i=0;i<unArrs.length;i++)
   {
		 var un_str=unArrs[i];
		if(un_str)
		 {
		     if(backdates.indexOf(un_str)!=-1){
			     rs[c]="<tr id='tv' name='tv'><logic:equal  name="historyStatForm"  property="chart_type"  value="11" ><td style='width:10;cursor:pointer'><input name=backdatebox type=checkbox value='"+un_str+"' checked=checked /></td></logic:equal><td id='al"+c+"'  onclick=\"hiddendict('"+un_str+"')\"  style='height:10;cursor:pointer' onmouseover='alterBg("+c+",0)' onmouseout='alterBg("+c+",1)' nowrap class=tdFontcolor>"+un_str+"</td></tr>"; 
             }else{
                 rs[c]="<tr id='tv' name='tv'><logic:equal  name="historyStatForm"  property="chart_type"  value="11" ><td style='width:10;cursor:pointer'><input name=backdatebox type=checkbox value='"+un_str+"' /></td></logic:equal><td id='al"+c+"'  onclick=\"hiddendict('"+un_str+"')\"  style='height:10;cursor:pointer' onmouseover='alterBg("+c+",0)' onmouseout='alterBg("+c+",1)' nowrap class=tdFontcolor>"+un_str+"</td></tr>"; 
             }
             c++;
		 }
        
	}
    resultuser=rs.join(""); 
    document.getElementById("dict").innerHTML="<table width='100%' class='div_table'  cellpadding='2' border='0'  bgcolor='#FFFFFF'   cellspacing='2'>"+resultuser+"</table>";
    document.getElementById('dict').style.display = "block";
    document.getElementById('dict').style.position="absolute";
	document.getElementById('dict').style.left=aTag.offsetLeft-132;
    document.getElementById('dict').style.top=aTag.offsetTop+46;
} 
function onV(j,flag){
   var  o =   document.getElementById('al'+j).innerHTML; 
   document.getElementById(flag).value=o; 
   document.getElementById(flag+"select").value=o;
   document.getElementById('dict').style.display = "none";
} 
function   alterBg(j,i){
    var   o   =   document.getElementById('al'+j); 
    if(i==0) 
       o.style.backgroundColor   ="#FFFFcc"; 
    else   if(i==1) 
       o.style.backgroundColor   ="#FFFFFF"; 
}
function hiddendict(val){
	<logic:equal  name="historyStatForm"  property="chart_type"  value="11" >
		var backdatebox = document.getElementsByName("backdatebox");
		var backdates="";
		for(i=0;i<backdatebox.length;i++){
			if(backdatebox[i].checked){
				backdates+="&"+backdatebox[i].value;
			}
		}
		if(backdates.length==0)
			backdates=document.getElementsByName('backdate')[0].value;
		else 
			backdates=backdates.substring(1);	
		document.getElementsByName("backdates")[0].value=backdates;
		document.getElementById('dict').style.display = 'none';
		var oldbackdates=document.getElementsByName("categories")[0].value;
		if(oldbackdates!=backdates){
			historyStatForm.action="/general/static/commonstatic/history/statshowchart.do?b_chart=link&showreturn=<%=showreturn%>";
	       	historyStatForm.submit();
		}
	</logic:equal>
	<logic:notEqual  name="historyStatForm"  property="chart_type"  value="11" >
		document.getElementsByName("backdates")[0].value=val;
		document.getElementById('dict').style.display = 'none';
		var oldbackdates=document.getElementsByName("categories")[0].value;
		if(val!=''&&oldbackdates!=val){
			historyStatForm.action="/general/static/commonstatic/history/statshowchart.do?b_chart=link&showreturn=<%=showreturn%>";
	       	historyStatForm.submit();
		}
	</logic:notEqual>
	
}

function changedate(val){
	document.getElementsByName("backdates")[0].value=val;
	historyStatForm.action="/general/static/commonstatic/history/statshowchart.do?b_chart=link&showreturn=<%=showreturn%>";
	       	historyStatForm.submit();
}

function getbackdates(){
	var thecodeurl ="/general/static/commonstatic/history/statshow.do?b_getbackdates=link"; 
    var dh=350;
      if(navigator.appVersion.indexOf('MSIE 6') != -1){
			dh=370;
		}
		var dw=400,dl=(screen.width-dw)/2,dt=(screen.height-dh)/2;
	//19/3/22 xus 浏览器兼容 弹窗 
	var config = {id:'getbackdates_showModalDialogs',width:dw,height:dh};
	modalDialog.showModalDialogs(thecodeurl,'',config,getbackdates_callbackfunc);	
	/*
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:330px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
    */
    
}
//19/3/22 xus 浏览器兼容 弹窗 回调函数
function getbackdates_callbackfunc(return_vo){
	if(return_vo!=null&&return_vo.length>0){
    	document.getElementsByName("backdates")[0].value=return_vo;
    	historyStatForm.action="/general/static/commonstatic/history/statshowchart.do?b_chart=link&showreturn=<%=showreturn%>";
	    historyStatForm.submit();
    }
}
//19/3/22 xus 浏览器兼容 弹窗 关闭窗口方法
function closeExtWin(){
	if(Ext.getCmp('getbackdates_showModalDialogs'))
		Ext.getCmp('getbackdates_showModalDialogs').close();
}
function initdate(){

	
}
   </SCRIPT>
 
   <hrms:themes />
<br>
  <html:form action="/general/static/commonstatic/history/statshow">
<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0" class="statshowchariniTable">
	<tr>
		<td>
			<table align="center" border="0" cellpadding="0" cellspacing="0">
<tr align="left">  
 	
   <logic:equal name="historyStatForm" property="infokind" value="1">
    <td valign="bottom" nowrap>
    	<html:hidden name="historyStatForm" property="backdates"/>
      	历史时点
             <html:select property="backdates" name="historyStatForm" onchange="changedate(this.value);" onclick="">
             	<html:optionsCollection property="backdateslist" value="dataValue" label="dataName"/>
             </html:select>
      <logic:notEqual name="historyStatForm"  property="chart_type"  value="5" >   
         <logic:notEqual name="historyStatForm"  property="chart_type"  value="20" >
     	<img src='/images/code.gif' align="absmiddle" onclick="getbackdates();" style="cursor:pointer"/>
       </logic:notEqual>
        </logic:notEqual>
      <!-- 历史时点&nbsp;<input name=categories id='hidcategories' style="width: 135px; height:20px;" value='${historyStatForm.backdates }' readonly="readonly">
      <img alt="历史时点" style="cursor:pointer" src="/images/rarrow.gif" onclick="addDict(this,event,'hidcategories');">    -->
    	<html:hidden name="historyStatForm" property="allbackdates"/>
    	<html:hidden name="historyStatForm" property="backdate"/>
    </td>
    </logic:equal>
    <td nowrap>
    	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;图形&nbsp;<html:select property="chart_type" name="historyStatForm" onchange="changechar_type(this.value);">
    		<html:optionsCollection property="chart_types" value="dataValue" label="dataName" />
    	</html:select>
      
    </td>
   <% if (!showreturn.equalsIgnoreCase("0")) { %>
		<td align="center">
		&nbsp;&nbsp;&nbsp;&nbsp;
		<%-- <input type="button" name="b_save" value="<bean:message key="button.return"/>" class="mybutton" onclick="returnhome()"> --%>
		<a href="###" onclick="returnhome()"><bean:message key="button.return"/></a>
		</td>
	<% } %>
  </tr>
  </table>
  <br><br>
		
		</td>
	</tr>
	<tr>
		<td>
		
		 <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">

          <tr>
            <td align="center" nowrap colspan="5" id="">       
         <logic:equal  name="historyStatForm"  property="chart_type"  value="5" >
         	<hrms:chart name="historyStatForm" xangle="${historyStatForm.xangle }" title="${historyStatForm.snamedisplay}" scope="session" legends="list" data="" width="1200" height="350" chart_type="${historyStatForm.chart_type}" pointClick="testchart" pieoutin="true">
	 		</hrms:chart>
         </logic:equal>  
         <logic:equal  name="historyStatForm"  property="chart_type"  value="11" >
         	<hrms:chart name="historyStatForm" xangle="${historyStatForm.xangle }" title="${historyStatForm.snamedisplay}" scope="session" legends="jfreemap" data="" width="1200" height="350" chart_type="${historyStatForm.chart_type}" pointClick="testchart" pieoutin="true">
	 		</hrms:chart>
         </logic:equal>     
        <logic:notEqual name="historyStatForm"  property="chart_type"  value="5" >   
         <logic:notEqual name="historyStatForm"  property="chart_type"  value="11" >         
	 	   <hrms:chart name="historyStatForm" xangle="${historyStatForm.xangle }" title="${historyStatForm.snamedisplay}" scope="session" legends="list" data="" width="1200" height="530" chart_type="${historyStatForm.chart_type}" pointClick="testchart" pieoutin="true">
	 	    </hrms:chart>
	 	 </logic:notEqual>
        </logic:notEqual>
            </td>
          </tr>          
</table>
		</td>
	</tr>
</table>
<!-- 
<div style="position:relative; width:50px; margin-top:550px!important; margin-top:;left:50%;margin-left:-0px; ">
	&nbsp;&nbsp; <input type="button" name="b_save" value="<bean:message key="button.return"/>" class="mybutton" onclick="returnhome()">
</div> -->
<div id="dict" style="display:none;z-index:+999;position:absolute;height:100px;width:153px;overflow:auto;bgcolor='#FFFFFF';border:0pt;"></div>
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
</script>
