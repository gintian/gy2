<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<%SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");DecimalFormat df = new DecimalFormat("#.##");%>
<style type="text/css">
.divStyle1{
	overflow: auto;
	width:expression(document.body.clientWidth-50);
}
.myleft
{
	border-left: none;
}
.mytop
{
	border-top: none;
}
.myright
{
 	border-right:none; 
}
.fixedHeaderTr{
 	border:1px solid;
}
</style>
<script type="text/javascript">
var dh=0;
if(navigator.appVersion.indexOf('MSIE 6') != -1){
	dh=50;
}
function loadclass(){
		var hashvo=new ParameterSet(); 
		hashvo.setValue("classplan",document.getElementById("classplan").value);
		hashvo.setValue("flag","1"); 
    	var request=new Request({method:'post',onSuccess:showSelectOk,functionId:'2020020234'},hashvo);
}
function showSelectOk(outparamters){
	if(outparamters){
		var csp=document.getElementById("courseplan");
		csp.options.length = 0;
		var value1=outparamters.getValue("value");
		var text1=outparamters.getValue("text");
		if(value1!=null&&value1.length>0){
			var val1s=value1.split(",");
			var txs=text1.split(",");
			for(var i=0;i<val1s.length;i++){
				var varItem = new Option(txs[i],val1s[i]);
				csp.options.add(varItem);
				if(val1s[i]=="${trainAtteForm.courseplan}")
					csp.options[i].selected=true;
			}
		}
		
		planclassinfo();
	}
}

function planclassinfo(){
	    var obj=document.getElementById("courseplan").value;
	    if(obj==null||obj.length<1){
	    	document.getElementById("planclassinfo").innerHTML="&nbsp;";
	    }else{
			var hashvo=new ParameterSet();
			hashvo.setValue("flag","0"); 
			hashvo.setValue("r4101",obj);
	    	var request=new Request({method:'post',onSuccess:showClassInfo,functionId:'2020020234'},hashvo);
    	}
}

function showClassInfo(outparamters){
	if(outparamters){
		var classdate=outparamters.getValue("classInfo");
		var classnum1=outparamters.getValue("classnum1");
		var classnum2=outparamters.getValue("classnum2");
		document.getElementById("planclassinfo").innerHTML="课程期间："+classdate+"&nbsp;已排："+classnum1+"课时&nbsp;未排："+classnum2+"课时&nbsp;";
	}
}
function changeOk(){
    var obj=document.getElementById("courseplan").value;
	trainAtteForm.action="/train/attendance/trainAtteCourse.do?b_query=link&queryflag=1&courseplan="+obj;
	trainAtteForm.submit();
}
function toPlan(id){
	if(!viewRecord())
		return;
	var hh=325+dh;
	if(id!="0")
		hh=hh-50;
	var r4101=document.getElementById("courseplan").value;
	var thecodeurl="/train/attendance/trainAtteCourse.do?b_edit=link&r4101="+r4101+"&id="+id;
	var return_vo= window.showModalDialog(thecodeurl, "", 
              	"dialogWidth:400px; dialogHeight:"+hh+"px;resizable:no;center:yes;scroll:no;status:no");
    if(return_vo!=null&&return_vo=="success"){
    	changeOk();
    }
}
function selAll(){
	var chks=document.getElementsByTagName("input");
	for(var i=0;i<chks.length;i++){
		if(chks[i].type=="checkbox"&&chks[i].value=="0")
			chks[i].checked=document.getElementById("all").checked;
	}
}
function delPlan(){
	var ids="";
	var chks=document.getElementsByTagName("input");
	for(var i=0;i<chks.length;i++){
		if(chks[i].type=="checkbox"&&chks[i].value=="0"&&chks[i].checked==true)
			ids+=chks[i].name+",";
	}
	if(ids.length<1){
		alert("请选择要删除的排班信息！");
		return;
	}
	if(window.confirm("确认要删除吗？")){
		var hashvo=new ParameterSet();
		hashvo.setValue("ids",ids); 
	   	var request=new Request({method:'post',onSuccess:changeOk,functionId:'2020020237'},hashvo);
   	}
}
function clickCheckBox(obj,column,id){
	var state1=(obj.checked)?"1":"0";
	var hashvo=new ParameterSet();
	hashvo.setValue("flag","2"); 
	hashvo.setValue("state",state1); 
	hashvo.setValue("column",column);
	hashvo.setValue("id",id);
   	var request=new Request({method:'post',onSuccess:null,functionId:'2020020234'},hashvo);
}
function exportExcel(){
	if(!viewRecord())
		return;
	var hashvo=new ParameterSet();
   	var request=new Request({method:'post',onSuccess:showExportInfo,functionId:'2020020230'},hashvo);
}
function showExportInfo(outparamters){
	if(outparamters){
		var name=outparamters.getValue("filename");
		window.location.target="_blank";
		window.location.href="/servlet/vfsservlet?fromjavafolder=true&fileid="+name;
	}
}
function viewRecord(){
	var courseplan=document.getElementById("courseplan").value;
	if(courseplan==null||courseplan.length<1){
		alert("没有培训课程！");
		return false;
	}
	return true;
}
</script>
<hrms:themes />
<html:form action="/train/attendance/trainAtteCourse">
<table cellspacing="0" cellpadding="0" border="0" style="margin-top: 5px;border-collapse: collapse;">
	<tr >
		<td nowrap>
			&nbsp;培训班&nbsp;
			<hrms:optioncollection name="trainAtteForm" property="classplanlist" collection="list"/> 
     	              <html:select name="trainAtteForm" property="classplan" onchange="loadclass();changeOk()">
							<html:options collection="list" property="dataValue"
								labelProperty="dataName" />
						</html:select>
			培训课程&nbsp;<html:select name="trainAtteForm" property="courseplan" styleId="courseplan" onchange="planclassinfo();changeOk()"></html:select>&nbsp;
			<!-- <input type="button" name="query" value='查询' class="mybutton" onclick="changeOk();"/> -->
			<label id="planclassinfo"></label>
			<div style="height: 5px;width: 10px;overflow: hidden;border-width: 0px;"></div>
		</td>
	</tr>
	<tr>
		<td>
		<div class="fixedDiv2" style="border-top: none;">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
  	<thead>
  	  <tr  class="fixedHeaderTr common_border_color">
	  	 <td width="30" align="center" class="TableRow" style="border-left: 0px;border-right:none;"><input type="checkbox" name="all" onclick="selAll();"/></td>
	   	 <td class="TableRow" style="border-right: none;" align="center" nowrap>日期</td>
	   	 <td class="TableRow" style="border-right: none;" align="center" nowrap>上课时间</td>
	   	 <td class="TableRow" style="border-right: none;" align="center" nowrap>下课时间</td>
	   	 <td class="TableRow" style="border-right: none;" align="center" nowrap>课时</td>
	   	 <td class="TableRow" style="border-right: none;" align="center" nowrap>上课签到</td>
	   	 <td class="TableRow" style="border-right: none;" align="center" nowrap>下课签退</td>
	   	 <td width="30" class="TableRow" style="border-right: none;" style="border-right: 0px;" align="center" nowrap>操作</td>
  	  </tr>
  	</thead>
	<hrms:paginationdb id="element" pagerows="${trainAtteForm.pagerows}" name="trainAtteForm" sql_str="trainAtteForm.sql_str" table=""  where_str="trainAtteForm.cond_str" order_by="trainAtteForm.order_str" columns="trainAtteForm.columns" page_id="pagination"  indexes="indexes">
	  <bean:define id="idid" name="element" property="id"/>
	  <% String id = SafeCode.encode(PubFunc.encrypt(idid.toString())); %>
	  <bean:define id="nid" value="<%=id %>"/>
	  <tr>
		 <td align="center" class="RecordRow" style="border-left: 0px;border-right: none;border-top: none;">
		 <input type="checkbox" name='${nid }' value="0"/></td>
	   	 <td class="RecordRow" align="center" style="border-right: none;border-top: none;" nowrap>
	   	 	<bean:define id="d" name="element" property="train_date"/>
	   	 	&nbsp;<%=sdf.format(sdf.parseObject(String.valueOf(d))) %>&nbsp;
	   	 </td>
	   	 <td class="RecordRow" style="border-right: none;border-top: none;" nowrap>&nbsp;<bean:write name="element" property="begin_time"  filter="true"/>&nbsp;</td>
	   	 <td class="RecordRow" style="border-right: none;border-top: none;" nowrap>&nbsp;<bean:write name="element" property="end_time"  filter="true"/>&nbsp;</td>
	   	 <td class="RecordRow" style="border-right: none;border-top: none;" nowrap>
	   	 <bean:define name="element" property="class_len"  id="n"/>
	   	 &nbsp;<%=df.format(Double.parseDouble(n.toString())) %>&nbsp;
	   	 </td>
	   	 <td align="center" class="RecordRow" style="border-right: none;border-top: none;">
	   	 	<html:checkbox name="element" property="begin_card" value="1"  onclick="javascript:clickCheckBox(this,'begin_card','${nid }');"/>
	   	 </td>
	   	 <td align="center" class="RecordRow" style="border-right: none;border-top: none;">
	   	 	<html:checkbox name="element" property="end_card" value="1" onclick="javascript:clickCheckBox(this,'end_card','${nid }');" />
	   	 </td>
	   	 <td align="center" class="RecordRow" style="border-right: none;border-top: none;">
	   	 	<a href="javascript:toPlan('${nid }');"><img alt="编辑" src="/images/edit.gif" style="border: 0px;"></a>
	   	 </td>
	  </tr>
	</hrms:paginationdb>
</table>
</div>
		</td>
	</tr>
	<tr>
		<td class="RecordRow" style="border-top: none;">
		<div class="divStyle1">
			 <table  width="100%" align="center" class="RecordRowP" style="border: 0px;">
			<tr>
			    <td valign="bottom" class="tdFontcolor">
						<hrms:paginationtag name="trainAtteForm" pagerows="${trainAtteForm.pagerows}" property="pagination" scope="page" refresh="true"></hrms:paginationtag>
				</td>
		               <td align="right" nowrap class="tdFontcolor">
			          <hrms:paginationdblink name="trainAtteForm" property="pagination" nameId="trainAtteForm" scope="page">
					</hrms:paginationdblink>
				</td>
			</tr>
	        </table>
	      </div>
		</td>
	</tr>
	<tr>
		<td align="left" style="padding-top: 5px;">
		  <hrms:priv func_id="323320101"> 
			<input type="button" name="b_add" value='新增' onclick="toPlan('0');" class="mybutton"/>
		  </hrms:priv>
		  <hrms:priv func_id="323320102"> 
			<input type="button" name="b_del" value='删除' class="mybutton" onclick="delPlan();"/>
		  </hrms:priv>
		  <hrms:priv func_id="323320103"> 
			<input type="button" name="b_excel" value='导出Excel' class="mybutton" onclick="exportExcel();"/>
		  </hrms:priv>
		</td>
	</tr>
</table>

</html:form>
<script>loadclass();</script>