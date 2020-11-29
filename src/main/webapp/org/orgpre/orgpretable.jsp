<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="com.hjsj.hrms.actionform.org.orgpre.OrgPreForm"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hrms.struts.constant.SystemConfig"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc" %>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String bosflag="";
	String a_code="";
	if(userView != null){	  
	  	bosflag=userView.getBosflag();
	  	bosflag=bosflag!=null?bosflag:"";               
	}
	String autostaticdialog=SystemConfig.getPropertyValue("autostaticdialog");
	autostaticdialog= autostaticdialog!=null?autostaticdialog:"";
	pageContext.setAttribute("autostaticdialog",autostaticdialog);
	
	OrgPreForm orgPreForm = (OrgPreForm)session.getAttribute("orgPreForm");
	String returnvalue=orgPreForm.getReturnvalue();
	//add by wangchaoqun on 2014-11-3 
    String sqlstr = PubFunc.encrypt(orgPreForm.getSqlstr());
 %>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/js/numberS.js"></script>
<script language="JavaScript" src="./orgpretable.js"></script>
<script language="javascript" src="/js/dict.js"></script>  
<script type="text/javascript" src="../../js/hjsjUrlEncode.js"></script>
<script type="text/javascript">
CheckBrowserCompatibility();
</script>
<hrms:themes></hrms:themes>
<html:form action="/org/orgpre/orgpretable">
<div id="ly" style="position:absolute;top:0px;FILTER:alpha(opacity=0);background-color:#FFF;z-index:2;left:0px;display:none;"></div> 
<div id='wait' style='position:absolute;top:285;left:80;display:none;'>
		<table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td class="td_style" height=24>
					<bean:message key='org.autostatic.mainp.calculation.wait'/>
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
<table cellpadding="0" cellspacing="0"><tr><td>
<table><tr><td>
<hrms:menubar menu="menubar1" id="menubar1">
	<hrms:menuitem name="file" label="menu.file.label">
    	<hrms:menuitem name="data_output" label="general.inform.muster.output.excel" icon="" url="outExcel();" command="" function_id="2306413"/>
    </hrms:menuitem> 
  	<hrms:menuitem name="gz1" label="infor.menu.edit" >
    	<hrms:menuitem name="m1" label="infor.menu.new" icon="/images/quick_query.gif" url="insert('${orgPreForm.setid}','add','${orgPreForm.a_code}','${orgPreForm.infor}','${orgPreForm.unit_type}','${orgPreForm.sp_flag}');" command="" function_id="2306407"/>
    	<hrms:menuitem name="m3" label="infor.menu.del" icon="/images/del.gif" url="" command="delselected" hint="general.inform.search.confirmed.del" function_id="2306408" />
    	<hrms:menuitem name="m4" label="infor.menu.query" icon="/images/view.gif" url="searchorg()" command="" function_id="2306415" />  
    </hrms:menuitem> 
  	<hrms:menuitem name="batch" label="label.gz.operation" >
    	<hrms:menuitem name="mitem1" label="infor.menu.compute" icon="/images/add_del.gif" url="batchCond('${orgPreForm.setid}','${orgPreForm.a_code}','6','${orgPreForm.unit_type}');" command="" enabled="true" visible="true" function_id="2306410"/>
		<hrms:menuitem name="mitem1" label="infor.menu.statisticalsummary" icon="/images/add_del.gif" url="checkDatejindu()" command="" function_id="2306406" enabled="true" visible="true"/>
	</hrms:menuitem>  
  	<hrms:menuitem name="show" label="conlumn.board.approveoperation">
  		<hrms:priv func_id="2306401">
      		<hrms:menuitem name="show1 " label="label.hiremanage.status2" icon="" url="" command="approval1" hint="performance.workdiary.approval.ok"/>
      	</hrms:priv>
   		<hrms:priv func_id="2306402">
      		<hrms:menuitem name="show2" label="edit_report.status.dh" icon="" url="" command="rejected" hint="org.orgpre.orgpretable.bohuiok"/>
      	</hrms:priv>
   		<hrms:priv func_id="2306403">
      		<hrms:menuitem name="show3" label="button.approve" icon="" url="" command="approval2" hint="org.orgpre.orgpretable.approvalok"/>
 		</hrms:priv>
 	</hrms:menuitem> 
 	<hrms:menuitem name="set" label="button.orgmapset">
      	<hrms:menuitem name="set1" label="org.orgpre.orgpretable.setcond" icon="" url="setCondPerson('${orgPreForm.a_code}','${orgPreForm.infor}','${orgPreForm.unit_type}');" command="" function_id="2306411"/>
      	<hrms:menuitem name="set2" label="infor.menu.sortitem" icon="" url="to_sort_field('${orgPreForm.setid}','${orgPreForm.a_code}','${orgPreForm.infor}','${orgPreForm.unit_type}');" command="" function_id="2306412"/>
 		<hrms:menuitem name="show_hide" label="org.autostatic.confset.datascan.viewhide" icon="" url="viewhide()" command="" enabled="true" visible="true" function_id="2306414"/>
 		<hrms:menuitem name="set_scan" label="org.autostatic.confset.datascan.setscan" icon="" url="setscan();" command="" function_id="2306404"/>
    	<hrms:menuitem name="set_project" label="org.autostatic.confset.datascan.setproject" icon="" url="setproject()" command="" function_id="2306405"/>
 	</hrms:menuitem> 
</hrms:menubar>
</td></tr></table>
</td>
<td>&nbsp;</td>
</tr></table>
<hrms:dataset name="orgPreForm" property="itemlist" scope="session" 
	setname="${orgPreForm.tablename}"  setalias="position_set" 
	readonly="false" editable="true" select="true" 
	sql="${orgPreForm.sqlstr}" pagerows="${orgPreForm.pagerows}" buttons="bottom">      
	<hrms:commandbutton name="table" hint="" functionId="" refresh="true" type="selected" onclick="insert('${orgPreForm.setid}','add','${orgPreForm.a_code}','${orgPreForm.infor}','${orgPreForm.unit_type}','${orgPreForm.sp_flag}');" setname="${orgPreForm.tablename}" function_id="2306407">
     <bean:message key="button.insert"/>
   </hrms:commandbutton>
    <hrms:commandbutton name="delselected" hint="general.inform.search.confirmed.del" functionId="0401000025" refresh="true" type="selected" setname="${orgPreForm.tablename}" function_id="2306408">
     <bean:message key="button.delete"/>
   </hrms:commandbutton>
    <hrms:commandbutton name="savedata" functionId="0401000029" refresh="true" type="all-change" setname="${orgPreForm.tablename}" function_id="2306409">
     <bean:message key="button.save"/>
   </hrms:commandbutton>   
   <hrms:commandbutton name="compute"  functionId="" refresh="true" onclick="batchCond('${orgPreForm.setid}','${orgPreForm.a_code}','6','${orgPreForm.unit_type}');" type="selected" setname="${orgPreForm.tablename}" function_id="2306410">
     <bean:message key="button.computer"/>
   </hrms:commandbutton> 
   	<hrms:commandbutton name="approval1" hint="performance.workdiary.approval.ok" functionId="0401000024" function_id="2306401" refresh="true" type="selected" setname="${orgPreForm.tablename}">
    	<bean:message key="info.appleal.state1"/>
   	</hrms:commandbutton>
   	<hrms:commandbutton name="rejected" hint="org.orgpre.orgpretable.bohuiok" functionId="0401000023" function_id="2306402" refresh="true" type="selected" setname="${orgPreForm.tablename}">
     	<bean:message key="info.appleal.state2"/>
   	</hrms:commandbutton>
   	<hrms:commandbutton name="approval2" hint="org.orgpre.orgpretable.approvalok"  functionId="0401000022"  function_id="2306403" refresh="true" type="selected" setname="${orgPreForm.tablename}">
     	<bean:message key="info.appleal.state3"/>
   	</hrms:commandbutton> 
   	<%if(returnvalue.equals("dxt")){ %>
   <hrms:commandbutton name="firstpage" function_id="" hint="" functionId="" visible="true" refresh="true" type="selected" setname="${orgPreForm.tablename}" onclick="returnFirst();" >
     <bean:message key="reportcheck.return"/>
   </hrms:commandbutton> 
   <%} %>
</hrms:dataset>

	<table border="0" width="330" style="position:absolute;left:300px;top:13px;">
	    <tr>
		    <td>
			<bean:message key="kq.card.status"/>
			<hrms:optioncollection name="orgPreForm" property="splist"  collection="list" /> 
			<html:select name="orgPreForm" property="flag" onchange="change('${orgPreForm.a_code}','${orgPreForm.infor}','${orgPreForm.unit_type}');" style="width:100px">
				<html:options collection="list" property="dataValue" labelProperty="dataName"/>
			</html:select>
		    </td>
	    </tr>
	</table>
	
<html:hidden name="orgPreForm" property="sp_flag"/>
<html:hidden name="orgPreForm" property="cloumstr"/>
<html:hidden name="orgPreForm" property="nextlevel"/>
<html:hidden name="orgPreForm" property="view_scan"/>

</html:form>
<script type="text/javascript">
window.status="";
var ctrl_type="${orgPreForm.ctrl_type}";
var tabid = "${orgPreForm.tablename}";
if(tabid!='A01'){
	function table${orgPreForm.tablename}_hispre_onRefresh(cell,value,record){
		if(record!=null){
			var b0110 = record.getString("b0110name");
			cell.innerHTML="<img src=\"/images/view.gif\" border=\"0\" onclick=\"depLink('"+b0110+"')\" style=\"cursor:hand;\">";
		}
	}
}
function table${orgPreForm.tablename}_pospre1_onRefresh(cell,value,record){
	if(record!=null){
		var b0110name = record.getString("b0110name");
		cell.innerHTML="<img src=\"/images/view.gif\" border=\"0\" onclick=\"posLink('"+b0110name+"')\" style=\"cursor:hand;\">";
	}
}
function table${orgPreForm.tablename}_pospre_onRefresh(cell,value,record){
	if(record!=null){
		var b0110name = record.getString("b0110name");
		cell.innerHTML="<img src=\"/images/view.gif\" border=\"0\" onclick=\"posLink('"+b0110name+"')\" style=\"cursor:hand;\">";
	}
}
function depLink(b0110){
	orgPreForm.action="/org/orgpre/deptable.do?b_query=link&b0110="+b0110+"&setid=${orgPreForm.setid}&a_code=${orgPreForm.a_code}&infor=${orgPreForm.infor}&unit_type=${orgPreForm.unit_type}&nextlevel=${orgPreForm.nextlevel}";
   	orgPreForm.submit();
}
function posLink(b0110){
	orgPreForm.action="/org/orgpre/postable.do?b_query=link&b0110="+b0110+"&setid=${orgPreForm.setid}&a_code=${orgPreForm.a_code}&infor=${orgPreForm.infor}&unit_type=${orgPreForm.unit_type}&nextlevel=${orgPreForm.nextlevel}&rb0110="+b0110;
   	orgPreForm.submit();
}
function returnFirst(){
   	self.parent.location= "/general/tipwizard/tipwizard.do?br_orginfo=link";
}

function setproject(){
   var thecodeurl ="/org/autostatic/mainp/project.do?b_query=link&flag=1&param=orgpre"; 
   //var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:760px; dialogHeight:550px;resizable:no;center:yes;scroll:yes;status:no");
}
function setscan(){
	//var view_scan = document.getElementById("view_scan").value;
    var thecodeurl ="/org/autostatic/confset/setscandata.do?b_query=link&flag=orgpre&view_scan="/*+view_scan*/; 
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:340px; dialogHeight:350px;resizable:no;center:yes;scroll:no;status:no");
    /*if(return_vo!=null){
    	document.getElementById("view_scan").value = return_vo;
    	
    	//checkDate();
    }*/
}

function checkDate(){
	var  yearnum = "${orgPreForm.yearnum}";
	var  months = "${orgPreForm.monthnum}";
	var  monthnum = "";
	var  subset =  "${orgPreForm.setid}";;
	if(months!=0){
		monthnum = "${orgPreForm.monthnum}";
		if(monthnum<1||monthnum>12){
			alert(INPUT_CORRECT_MONTH);
			return;
		}
	}else{
		monthnum='0';
	}
	if(isNaN(yearnum)==true){
		alert(INPUT_CORRECT_YEAR);
		return;
	}
	if(isNaN(monthnum)==true){
		alert(INPUT_CORRECT_MONTH);
		return;
	}
	
	var hashvo=new ParameterSet();
   	hashvo.setValue("yearnum",yearnum);
	hashvo.setValue("monthnum",monthnum);
	hashvo.setValue("fieldsetid","orgpre-"+subset);
	var In_paramters="flag=orgpre"; 
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:count,functionId:'1602010222'},hashvo);
}

function jindu(){
	//新加的，屏蔽整个页面不可操作
	document.all.ly.style.display="block";   
	document.all.ly.style.width=document.body.clientWidth;   
	document.all.ly.style.height=document.body.clientHeight; 
	
	var x=document.body.scrollLeft+event.clientX+150;
    var y=document.body.scrollTop+event.clientY+100; 
	var waitInfo=eval("wait");
	waitInfo.style.top=y;
	waitInfo.style.left=x;
	waitInfo.style.display="block";
}

function checkDatejindu(){
	var  yearnum = "${orgPreForm.yearnum}";
	var  months = "${orgPreForm.monthnum}";
	var  monthnum = "";
	var  subset =  "${orgPreForm.setid}";
	if(months!=0){
		monthnum = "${orgPreForm.monthnum}";
		if(monthnum<1||monthnum>12){
			alert(INPUT_CORRECT_MONTH);
			return;
		}
	}else{
		monthnum='0';
	}
	if(isNaN(yearnum)==true){
		alert(INPUT_CORRECT_YEAR);
		return;
	}
	if(isNaN(monthnum)==true){
		alert(INPUT_CORRECT_MONTH);
		return;
	}
	
	var hashvo=new ParameterSet();
   	hashvo.setValue("yearnum",yearnum);
	hashvo.setValue("monthnum",monthnum);
	hashvo.setValue("fieldsetid","orgpre-"+subset);
	var In_paramters="flag=orgpre"; 
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:count,functionId:'1602010222'},hashvo);
	
}
function count(outparamters){
	var checkdb = outparamters.getValue("checkdb");
	var included = "";
	var a_code='${orgPreForm.a_code}';//当前机构树选中节点
	var autostaticdialog='${autostaticdialog}';//设置编制管理中计算或统计汇总是否出现引入上期数据对话框 system.properties
	//alert(autostaticdialog);
	if(checkdb=='ok'&&autostaticdialog!='false'){
 		var fieldsetid = "${orgPreForm.setid}";
    	var thecodeurl="/org/autostatic/confset/included.do?b_included=link&flag=orgpre&fieldsetid="+fieldsetid; 
    	var popwin= window.showModalDialog(thecodeurl,"", 
        	"dialogWidth:430px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
        if(popwin!=null){
        	included = popwin;
        	jindu();
        	orgPreForm.action="/org/orgpre/orgpretable.do?b_query_count=link&count=0k&infor=2&unit_type=3&a_code="+a_code+"&nextlevel=0&included="+included;
    		orgPreForm.submit();
        }
   }else{
   		jindu();
    	orgPreForm.action="/org/orgpre/orgpretable.do?b_query_count=link&count=0k&infor=2&unit_type=3&a_code="+a_code+"&nextlevel=0&included="+included;
   	 	orgPreForm.submit();
    }
}

function table${orgPreForm.tablename}_b0110_onRefresh(cell,value,record){
	if(record!=null&&record!=""){	
		var values = record.getValue("b0110");
		var hashvo=new ParameterSet();
   		hashvo.setValue("b0110",values);
   		hashvo.setValue("cell",cell);
   		var In_paramters="flag=1"; 
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:rs_view,functionId:'1602010228'},hashvo);
		
	}
	function rs_view(outparamters){
		var rs_view=outparamters.getValue("rs_view");
		cell.innerHTML=rs_view;
	}
}

function outExcel(){
	var  yearnum = "${orgPreForm.yearnum}";
	var  monthnum = "${orgPreForm.monthnum}";
	var setid="${orgPreForm.setid}";
	var ctrl_type="${orgPreForm.ctrl_type}";
	var nextlevel="${orgPreForm.levelnext}";
	var sp_flag="${orgPreForm.sp_flag}";
	var realitem="${orgPreForm.realitem}";
	var a_code="${orgPreForm.a_code}";
	var dataresource="<%=sqlstr%>";
	
	var hashvo=new ParameterSet();
    hashvo.setValue("yearnum",yearnum);
	hashvo.setValue("monthnum",monthnum);
	hashvo.setValue("setid",setid);
	hashvo.setValue("ctrl_type",ctrl_type);
	hashvo.setValue("nextlevel",nextlevel);
	hashvo.setValue("sp_flag",sp_flag);
	hashvo.setValue("realitem",realitem);
	hashvo.setValue("a_code",a_code);
	hashvo.setValue("dataresource",dataresource);
	var In_paramters="flag=${orgPreForm.flag}"; 
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showfile,functionId:'1602010229'},hashvo);
		
}
function showfile(outparamters){
	var outName=outparamters.getValue("outName");
	//var name=outName.substring(0,outName.length-1)+".xls";
	window.location.target="_blank";
	window.location.href="/servlet/vfsservlet?fromjavafolder=true&fileid="+outName;
}

function viewhide(){
    var thecodeurl ="/org/orgpre/orgpretable.do?b_view_hide=link"; 
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:350px; dialogHeight:380px;resizable:no;center:yes;scroll:no;status:no");
    if(return_vo!=null){
    	var a_code="${orgPreForm.a_code}";
   		orgPreForm.action="/org/orgpre/orgpretable.do?b_query=link&infor=2&unit_type=3&a_code="+a_code;
    	orgPreForm.submit();
    }
}

<logic:iterate id="statNumItem" name="orgPreForm" property="statNumItemlist"> 
	function table${orgPreForm.tablename}_${statNumItem }_onRefresh(cell,value,record){
		var fielditemid="${statNumItem }";
		if(record!=null&&record!=""){	
			var values = record.getValue("b0110");
			//alert('<a href="###" onclick="showstatnum(\''+values+'\')" >'+value+'</a>');
			cell.innerHTML='<a href="###" onclick="showstatnum(\''+values+'\',\''+fielditemid+'\')" >'+value+'</a>';
		}
	}
</logic:iterate>

function showstatnum(b0110,fielditemid){
	//alert(b0110+fielditemid);
	orgPreForm.action="/org/orgpre/showstatnum.do?b_showstatnum=link&b0110="+b0110+"&setid=${orgPreForm.setid}&a_code=${orgPreForm.a_code}&infor=${orgPreForm.infor}&unit_type=${orgPreForm.unit_type}&nextlevel=${orgPreForm.nextlevel}&fielditemid="+fielditemid+"&fromway=org&parttime=0";
   	orgPreForm.submit();
}

function searchorg(){
	var url = "/org/orgpre/orgpretable.do?b_search=link`searchOrg=true";
	var iframesrc="/general/query/common/iframe_query.jsp?src="+$URL.encode(url);
	 var return_vo= window.showModalDialog(iframesrc, "查询", 
	 "dialogWidth:710px; dialogHeight:420px;resizable:no;center:yes;scroll:no;status:no");
	 if(return_vo == 'ok'){
	     //this.location.reload();
	     //add by wangchaoqun on 2014-10-27 将reload修改为href，以免前台出现重发请求提示框
	     this.location.href=this.location.href;
	 }
}
</script>

