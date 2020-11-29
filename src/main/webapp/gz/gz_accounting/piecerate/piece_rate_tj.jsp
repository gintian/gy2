<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript" src="/js/dict.js"></script> 
<script type="text/javascript" src="/js/wz_tooltip.js"></script>  
<%@page import="com.hjsj.hrms.actionform.gz.gz_accounting.piecerate.PieceRateTjForm" %>
<%
PieceRateTjForm prform=(PieceRateTjForm)session.getAttribute("pieceRateTjForm");
String defId =prform.getDefId();
 %>
<script type="text/javascript">
/**
 * 判断当前浏览器是否为ie6
 * 返回boolean 可直接用于判断 
 * @returns {Boolean}
 */
function isIE6() 
{ 
	if(navigator.appName == "Microsoft Internet Explorer") 
	{ 
		if(navigator.userAgent.indexOf("MSIE 6.0")>0) 
		{ 
			return true;
		}else{
			return false;
		}
	}else{
		return false;
	}
}
function init()
{ 
	var obj=document.getElementById("defId");
	if (obj==null) return;
	for(var i=0;i<obj.options.length;i++)
	{
		if('<%= defId%>' =='null'||'<%= defId%>'==''||'<%= defId%>'=='-1'){
			obj.options[0].selected=true;
			return;
		}else{
			if(obj.options[i].value=='<%= defId%>'){
				obj.options[i].selected=true;
				return;
			}
		}
	}
}
//报表定义
function reportDef(){
	var thecodeurl ="/gz/gz_accounting/piecerate/search_piecerate_tj_report.do?b_searchReport=link"; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
	if(isIE6() ){
		var defId= window.showModalDialog(thecodeurl, "", 
	            "dialogWidth:480px; dialogHeight:430px;resizable:no;center:yes;scroll:yes;status:no");
	}else{
		var defId= window.showModalDialog(thecodeurl, "", 
	            "dialogWidth:450px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
	}
   if(defId==null){
   	return;
   }
   	var  startDate = document.getElementById("start_date").value;
	var  endDate = document.getElementById("end_date").value;	
 	pieceRateTjForm.action="/gz/gz_accounting/piecerate/search_piecerate_tj_report.do?b_search=link&startDate="+startDate+"&endDate="+endDate+"&defId="+defId;
 	pieceRateTjForm.submit();

}
function changeTjReport(){
	var  startDate = document.getElementById("start_date").value;
	var  endDate = document.getElementById("end_date").value;
	var obj=document.getElementById("defId");
	var defId="";
	for(var i=0;i<obj.options.length;i++)
	{
		if(obj.options[i].selected==true){
			defId=obj.options[i].value;
			continue;
		}
	}
	if(defId==""){
		defId = '-1';
	}
   	pieceRateTjForm.action="/gz/gz_accounting/piecerate/search_piecerate_tj_report.do?b_search=link&startDate="+startDate+"&endDate="+endDate;
   	pieceRateTjForm.submit();

}

function changeTjReportId(){
	var obj=document.getElementById("defId");
	var defId="";
	for(var i=0;i<obj.options.length;i++)
	{
		if(obj.options[i].selected==true){
			defId=obj.options[i].value;
			continue;
		}
	}
	if(defId==""){
		defId = '-1';
	}else{
		if(defId=="new"){
			defId = addReport();
			if(defId==null){
				defId = '<%= defId%>';
				if(defId=='null'){
					if(obj.options.length>1){
						defId = obj.options[0].value;
					}
				}
			}
		}
	}
   	pieceRateTjForm.action="/gz/gz_accounting/piecerate/search_piecerate_tj_report.do?b_search=link&defId="+defId;
   	pieceRateTjForm.submit();

}


function setTjWhere(){
	/* 安全问题 sql-in-url 计件薪资-报表-统计条件 xiaoyun 2014-9-18 start  */
    //var thecodeurl ="/gz/gz_accounting/piecerate/piecerate_tj_def.do?b_setCondition=link&tjWhere="+ getEncodeStr(pieceRateTjForm.tjWhere.value);
    var thecodeurl ="/gz/gz_accounting/piecerate/piecerate_tj_def.do?b_setCondition=link&tjWhere="+pieceRateTjForm.tjWhere.value;
    /* 安全问题 sql-in-url 计件薪资-报表-统计条件 xiaoyun 2014-9-18 end  */
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:550px; dialogHeight:330px;resizable:no;center:yes;scroll:no;status:no");
	if(return_vo!=null)
	{	
		var itemid = return_vo;  
		pieceRateTjForm.tjWhere.value=itemid;
		//提交刷新
		/* 安全问题 sql-in-url 计件薪资-报表-统计条件 xiaoyun 2014-9-18 start  */
	   	pieceRateTjForm.action="/gz/gz_accounting/piecerate/search_piecerate_tj_report.do?b_search=link";
	   	/* 安全问题 sql-in-url 计件薪资-报表-统计条件 xiaoyun 2014-9-18 end  */
	   	pieceRateTjForm.submit();
    }
}

 function goback()
 {
  url="/gz/gz_accounting/piecerate/search_piecerate.do?b_query=back"
  pieceRateTjForm.action=url;
  pieceRateTjForm.submit();
 }  


function expReport(){
	var obj=document.getElementById("defId");
	var defId="";
	for(var i=0;i<obj.options.length;i++)
	{
		if(obj.options[i].selected==true){
			defId=obj.options[i].value;
			continue;
		}
	}
	if(defId==""){
		return;
	}
	var hashvo=new ParameterSet();
    hashvo.setValue("defId",defId);     
	hashvo.setValue("flag","expReport");
	/* 安全问题 sql-in-url 计件薪资-报表  xiaoyun 2014-9-18 start */
	// hashvo.setValue("sql",getEncodeStr(pieceRateTjForm.sql.value));
	/* 安全问题 sql-in-url 计件薪资-报表  xiaoyun 2014-9-18 end */
	var request=new Request({asynchronous:false,onSuccess:sucessExp,functionId:'3020091066'},hashvo);	

}
function sucessExp(outparameters){
	var filename=outparameters.getValue("fileName");
	if (filename=="undefined")  return;
	/* 安全问题 计件薪资 报表导出 xiaoyun 2014-9-13 start */
    // var name=filename.substring(0,filename.length-1)+".xls";
    /* 安全问题 计件薪资 报表导出 xiaoyun 2014-9-13 end */
    filename = getDecodeStr(filename);
    var win=open("/servlet/vfsservlet?fileid="+filename+"&fromjavafolder=true","excel");
}

function addReport(){
	 var theURL = "/gz/gz_accounting/piecerate/piecerate_tj_def.do?b_selectfld=link`model=add";
     var iframe_url="/gz/gz_accounting/bankdisk/iframe_bank_disk.jsp?src="+theURL;   
	var objlist =window.showModalDialog(iframe_url,null,"dialogWidth=600px;dialogHeight=400px;resizable:no;center:yes;scroll:yes;status:no");  
	if(objlist!=null){
		return objlist.defid;
	}
 	
}


</script>
<style type="text/css">

.myfixedDiv2 { 
	overflow:auto; 
	height:400px;
	*height:expression(document.body.clientHeight-150);
	width:100%;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; 
}

</style>
<html:form action="/gz/gz_accounting/piecerate/search_piecerate_tj_report">
 <html:hidden name="pieceRateTjForm" property="tjWhere" />
<table width="98%" align="left" >
	<tr>
				<td align="left" valign="bottom" nowrap style="height:35;">
					
					<bean:message key="gz.report.table" />：<!-- 报表 -->
						<logic:notEmpty name="pieceRateTjForm" property="reportList" >
							<hrms:optioncollection name="pieceRateTjForm" property="reportList" collection="list"/>
								<html:select name="pieceRateTjForm" property="defId" onchange="changeTjReportId();" style="width:140">
										<html:options collection="list" property="dataValue" labelProperty="dataName" />
										<option value="new" >&lt;新建 &gt;</option>
								</html:select>
						</logic:notEmpty>
						<logic:empty name="pieceRateTjForm" property="reportList" >
								<select  name="defId" onmousedown="changeTjReportId();" style="width:140" >
										<option value="new" >&lt;新建 &gt;</option>
								</select>
 						</logic:empty>
 						
 					<hrms:priv func_id="324211301">
					<!-- 报表定义按钮 -->
		    		<input type="button" class="mybutton" value="<bean:message key='gz.piecerate.tj.table.definition'/>" onclick="reportDef()">
		    		</hrms:priv>
		    		<hrms:priv func_id="324211302">
					<!-- 统计条件 -->
		    		<input type="button" class="mybutton" value="<bean:message key='makeupanalyse.stat'/>" onclick="setTjWhere();">
		    		</hrms:priv>
		    		<hrms:priv func_id="324211303">
					<!-- 导出 -->
		    		<input type="button" class="mybutton" value="<bean:message key='button.export'/>" onclick="expReport();">
		    		</hrms:priv>
		    		&nbsp;&nbsp;
					<bean:message key="jx.khplan.timeframe" />
					<span id="datepnl">							
					  <bean:message key="label.from" /> 				
					   <input type="text" name="start_date"  onchange="" 
							value="${pieceRateTjForm.startDate}" extra="editor"
							style="width:100px;font-size:10pt;text-align:left" id="editor1" 
							dropDown="dropDownDate" /> 					
						<bean:message key="label.to" />			
					<input type="text" name="end_date"  onchange=""
							value="${pieceRateTjForm.endDate}" extra="editor"
							style="width:100px;font-size:10pt;text-align:left" id="editor2" 
							dropDown="dropDownDate"/>  													
					</span>	
					<input type="button" class="mybutton" value="<bean:message key='button.query'/>" onclick="changeTjReport();">					
					<!-- 返回 -->
		    		<input type="button" class="mybutton" value="<bean:message key='button.return'/>" onclick="goback();">
				</td>
			</tr>        
   <tr>
    <td >
	<logic:notEmpty name="pieceRateTjForm" property="reportList" > 
		<hrms:dataset name="pieceRateTjForm" property="fieldlist" scope="session" setname="${pieceRateTjForm.tableName}" 
		      	pagerows="${pieceRateTjForm.pagerows}" setalias="detail" readonly="false"  editable="true" sql="${pieceRateTjForm.sql}" 
		      	  buttons="bottom">
		</hrms:dataset>	
	</logic:notEmpty>
	
	<logic:empty name="pieceRateTjForm" property="reportList" >
	    <div class="myfixedDiv2"  >
          <H3 ALIGN = left> 请定义报表！</H3> 
	    </div>
	</logic:empty>
	</td>	
   </tr>   
</table>
	</html:form>
<script language="javascript">
	init();
</script>
<style type="text/css">
TABLE.datatable { 
	margin-top:1px;
}

</style>