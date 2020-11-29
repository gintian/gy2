<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.businessobject.train.trainexam.question.questiones.QuestionesBo"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hrms.struts.constant.SystemConfig"%>
<%@page import="java.util.List"%>

<%// 在标题栏显示当前用户和日期 2004-5-10 
			String userName = null;
			String css_url = "/css/css1.css";
			UserView userView = (UserView) session
					.getAttribute(WebConstant.userView);
			int status=userView.getStatus();
			String manager=userView.getManagePrivCodeValue();
			int fflag=1;
			String webserver=SystemConfig.getPropertyValue("webserver");
			if(webserver.equalsIgnoreCase("websphere"))
				fflag=2;
%>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<style type="text/css">
<!--
.fixedDiv2 
{ 
	overflow:auto; 
	height:400px;
	width:800px;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; 
}
-->
</style>
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

<script language="javascript" src="/js/constant.js"></script>
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
	var webserver=<%=fflag%>;
	
	function addquestions(type_id){
		paperQuestionTypeForm.action="/train/trainexam/paper/questiontype.do?b_query=link&type_id="+type_id;
		paperQuestionTypeForm.submit();
	}
	
	function delquestiontype(){
		var a=0;
		var sels="";
		var a_IDs=document.getElementsByName("selectid");		
		for(var i=0;i<document.forms[0].elements.length;i++)
		{			
		   if(document.forms[0].elements[i].type=='checkbox'&&document.paperQuestionTypeForm.elements[i].name!="selbox")
		   {		   			
				if(document.forms[0].elements[i].checked==true)
				{
					sels+=a_IDs[a].value+",";
				}
				a++;
		   }
	    }
	    if(!sels)
    		alert("请选择要删除的题型！");
    	else if(confirm("确认要删除吗？")){
			var hashvo=new ParameterSet();
	        hashvo.setValue("sels", sels);
	        hashvo.setValue("r5300","${paperQuestionTypeForm.r5300}");
	        var request=new Request({method:'post',onSuccess:delajaxok,functionId:'2020070003'},hashvo);
	        function delajaxok(outparamters){
				var flag=outparamters.getValue("flag"); 
				if("ok"==flag){
					paperQuestionTypeForm.action="/train/trainexam/paper/autoquestiontype.do?b_add=link&r5300=${paperQuestionTypeForm.r5300}";
					paperQuestionTypeForm.submit();
				}else
					alert("删除失败！");
			}
        }
	}
	
	function addquestionstype(){
		var hashvo=new ParameterSet();
        hashvo.setValue("type", document.getElementById("questiontype").value);
        hashvo.setValue("r5300","${paperQuestionTypeForm.r5300}");
        var request=new Request({method:'post',onSuccess:addtypeok,functionId:'2020070002'},hashvo);
		function addtypeok(outparamters){
			var flag=outparamters.getValue("flag"); 
			if("ok"==flag){
				paperQuestionTypeForm.action="/train/trainexam/paper/autoquestiontype.do?b_add=link&r5300=${paperQuestionTypeForm.r5300}";
				paperQuestionTypeForm.submit();
			}else if("no"==flag)
				alert("该题型已存在！");
			else
				alert("添加失败！");
		}
	}
	
	function editquestions(order,type_id){
		//var thecodeurl="/train/trainexam/paper/questiontype.do?b_edit=link&type_id="+type_id+"&order="+order;
	   // var return_vo= window.showModalDialog(thecodeurl, "", 
	   //           "dialogWidth:600px; dialogHeight:530px;resizable:no;center:yes;scroll:no;status:yes");
	   // if(!return_vo)return;
	    //paperQuestionTypeForm.action="/train/trainexam/paper/questiontype.do?b_add=link&r5300=${paperQuestionTypeForm.r5300}";
		//paperQuestionTypeForm.submit();
		paperQuestionTypeForm.action="/train/trainexam/paper/questiontype.do?b_edit=link&type_id="+type_id+"&order="+order;
		paperQuestionTypeForm.submit();
	}
	
	function sortItem(type,typeId){
		var hashvo=new ParameterSet();
		hashvo.setValue("flag","2");
		hashvo.setValue("type", type);
		hashvo.setValue("r5300", "${paperQuestionTypeForm.r5300}");
        hashvo.setValue("type_id", typeId);
        hashvo.setValue("strwhere", "${paperQuestionTypeForm.strwhere}");
        var request=new Request({method:'post',functionId:'2020070014'},hashvo);
        
        paperQuestionTypeForm.action="/train/trainexam/paper/autoquestiontype.do?b_add=link&r5300=${paperQuestionTypeForm.r5300}";
		paperQuestionTypeForm.submit();
	}
	
	function changeknow(type_id){
		document.getElementById(type_id+"_knowsvalue").innerHTML=document.getElementById(type_id+"_addKnowledgeviewvalue").value;
		saveknowledge(type_id,'know_ids',document.getElementById(type_id+"_addKnowledge").value);
	}
	
	function saveknowledge(type_id,column,value){
		var hashvo=new ParameterSet();
		hashvo.setValue("r5300","${paperQuestionTypeForm.r5300}");
		hashvo.setValue("type_id",type_id);
		hashvo.setValue("column", column);
		hashvo.setValue("value", value);
		var request=new Request({method:'post',functionId:'2020070026'},hashvo);
	}
	
	function verify(r5300){//校验
		var scores=0;
		var inps = document.getElementsByTagName("input");
		for(var i=0;i<inps.length;i++){
			if("score"==inps[i].name&&inps[i].value)
				scores+=parseFloat(inps[i].value);
		}
		//alert(scores);
		var hashvo=new ParameterSet();
		hashvo.setValue("flag","2");
		hashvo.setValue("score",scores);
	    hashvo.setValue("r5300",r5300);
	    var request=new Request({method:'post',onSuccess:addtypeok,functionId:'2020070009'},hashvo);
	}
	function addtypeok(outparamters){
		var flag=outparamters.getValue("flag"); 
		if("ok"==flag){
			tonext();
		}else if("no"==flag)
			if(confirm(getDecodeStr(outparamters.getValue("mess"))))
				tonext();
	}
	
	function tonext(){
		var max_nums="";
		var scores="";
		var knowledges="";
		var inps = document.getElementsByTagName("input");
		for(var i=0;i<inps.length;i++){
			if("max_num"==inps[i].name)
				max_nums+=inps[i].value+"`";
			if("score"==inps[i].name)
				scores+=inps[i].value+"`";
			if("knowledge"==inps[i].id)
				knowledges+=inps[i].value+"`";
		}
		paperQuestionTypeForm.action="/train/trainexam/paper/autoquestiontype.do?b_next=link&max_nums="+$URL.encode(max_nums)+"&scores="+$URL.encode(scores)+"&knowledges="+$URL.encode(knowledges);
		paperQuestionTypeForm.submit();
	}
</script>
<hrms:themes/>
<%int i=0;%>
<html:form action="/train/trainexam/paper/questiontype">
<table border="0" align="center" cellpadding="0" cellspacing="0">
   <tr style="height: 35px">
   	<td>题型&nbsp;
   	<hrms:optioncollection name="paperQuestionTypeForm" property="questiontypes" collection="pos_list"/>
	<html:select name="paperQuestionTypeForm" property="questiontype" styleId="questiontype" size="1">
	    <html:options collection="pos_list" property="dataValue" labelProperty="dataName"/>
	</html:select>
	&nbsp;<input type="button" class="mybutton" value="添加题型" onclick="addquestionstype();" />
	</td>
   </tr>
   <tr>
    <td>
     <div class="fixedDiv2"> 
     <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" id="tableid">
           <tr class="fixedHeaderTr">
             <td align="center" class="TableRow" nowrap width="20" style="border-left: none;border-top:none;">
              <input type="checkbox" name="selbox" onclick="batch_select(this,'pagination.select');" title='<bean:message key="label.query.selectall"/>'>
             </td>
	         <td align="center" class="TableRow" style="border-left: none;border-top:none;" nowrap >
	           &nbsp;题型&nbsp; 
	         </td>
	         <td align="center" class="TableRow" style="border-left: none;border-top:none;" nowrap width="120">
	            &nbsp;题数&nbsp;
	         </td>
	         <td align="center" class="TableRow" style="border-left: none;border-top:none;" nowrap width="120">
	            &nbsp;分数&nbsp;              
	         </td>
             <td align="center" class="TableRow" style="border-left: none;border-top:none;" colspan="2" nowrap  width="300">
                &nbsp;知识点&nbsp;
             </td>	
             <td align="center" class="TableRow" nowrap  width="45"  style="border-left: none;border-top:none;border-right: none;">
                &nbsp;排序
             </td>    	    	    		        	        	        
           </tr>

          <hrms:paginationdb id="element" name="paperQuestionTypeForm" sql_str="paperQuestionTypeForm.strsql" table="" where_str="paperQuestionTypeForm.strwhere" columns="paperQuestionTypeForm.columns" order_by="paperQuestionTypeForm.order_by" page_id="pagination" pagerows="15" indexes="indexes">
          <bean:define id="type_id" name="element" property="type_id"></bean:define>
          <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow" onMouseOver="javascript:tr_onclick(this,'')">
          <%}else{%>
          <tr class="trDeep" onMouseOver="javascript:tr_onclick(this,'E4F2FC')">
          <%}
          i++;          
          %>  
            <td align="center" class="RecordRow" nowrap  style="border-left: none;border-top:none;">
               <hrms:checkmultibox name="paperQuestionTypeForm" property="pagination.select" value="true" indexes="indexes"/>
               <input type=hidden name=selectid value='${type_id }' />
            </td>  
                         
              <td align="left" class="RecordRow" style="border-left: none;border-top:none;" nowrap> 
              	&nbsp;<bean:write  name="element" property="type_name" filter="true"/>&nbsp;
              </td>                      
              <td align="center" class="RecordRow" style="border-left: none;border-top:none;" nowrap>
              	<input type="text" name="max_num" size=12 class="text4" value='<bean:write name="element" property="max_num" format="000"/>' style="text-align:right;" onpropertychange='if(/[^\d*]/.test(this.value)) this.value=this.value.replace(/[^\d*]/,"")' onblur="saveknowledge('${type_id }','max_num',this.value);"/>
              	<!-- <html:text name="element" property="max_num" size="12" style="text-align: right;" onkeyup="isNumber(this);" onblur="saveknowledge('${type_id }','max_num',this.value);"/> -->   
              </td>  
              <td align="center" class="RecordRow" style="border-left: none;border-top:none;" nowrap>  
              <logic:equal value=".00" name="element" property="score" >
              <!--	<input type="text" id="${type_id}_score" value="0.00" size="12" style="text-align: right;" onkeyup="isNumber(${type_id});" onblur="saveknowledge('${type_id }','score',this.value);"/>  --><!-- 此处是小数单位的问题 所以干脆就注释了此文本不要两位小数 只取一位小数 -->
              <html:text  name="element" property="score" styleId="${type_id}_score" size="12" styleClass="text4" style="text-align: right;" onkeyup="isNumber(${type_id});" onblur="saveknowledge('${type_id }','score',this.value);"/>
              </logic:equal>
              <logic:notEqual value=".00" name="element" property="score" >
              	<html:text  name="element" property="score" styleId="${type_id}_score" size="12" styleClass="text4" style="text-align: right;" onkeyup="isNumber(${type_id});" onblur="saveknowledge('${type_id }','score',this.value);"/> 
              </logic:notEqual>
              </td> 
             <td class="RecordRow" nowrap  width="250"  style="border-left: none;border-top:none;border-right: none;"">
             	<bean:define id="know_ids" name="element" property="know_ids" />
                 &nbsp;<label id="${type_id }_knowsvalue"><%out.println(QuestionesBo.getKnowledgeviewvaluee(know_ids.toString())); %></label>
                 <input type="hidden" name="${type_id }_addKnowledgeviewvalue" id="${type_id }_addKnowledgeviewvalue" size="30" border="0" value="<%out.println(QuestionesBo.getKnowledgeviewvaluee(know_ids.toString())); %>"/>
                 <input type="hidden" name="${type_id }_addKnowledge" value="${know_ids }" id="knowledge" onpropertychange="changeknow('${type_id }');"/>
                 &nbsp;
             </td>
             <td align="center" class="RecordRow" nowrap  width="50" style="border-left: 0px;border-top: none;">
                 <img src="/images/code.gif" onclick="openTrainInputCodeDialog('68','${type_id }_addKnowledge');" style='cursor:pointer;' align='absmiddle' />
             </td>
             <td align="center" class="RecordRow" nowrap  width="50" style="border-right: 0px;border-left: none;border-top: none;">
             	<bean:define id="id" name="element" property="norder"/>
             	<bean:define id="start" name="paperQuestionTypeForm" property="start"/>
             	<bean:define id="end" name="paperQuestionTypeForm" property="end"/>
             	<%if(id.toString().equals(start)){ %>
             		&nbsp;&nbsp;&nbsp;
				<%} else{%>
				<a href="javaScript:sortItem('up','${type_id }');">
					<img src="/images/up01.gif" width="12" height="17" border=0></a>
				<%} 
             	if(id.toString().equals(end)){ 
				%>
					&nbsp;&nbsp;&nbsp;
				<%} else{%>
				<a href="javaScript:sortItem('down','${type_id }');">
					<img src="/images/down01.gif" width="12" height="17" border=0></a> 
				<%} %>
             </td>
          </tr>
        </hrms:paginationdb>
</table>
</div>
</td>
</tr>
<tr style="height: 35px"><td align="left" style="padding-top: 5px;">
	<hrms:priv func_id="" module_id="">
			<input type="button" name="b_next" value="下一步" class="mybutton" onclick="verify('${paperQuestionTypeForm.r5300 }');" />
   	</hrms:priv>
   	<hrms:priv func_id="" module_id="">
   			<input type="button" name="b_retrun" value="删除" class="mybutton" onclick="delquestiontype();" />
   	</hrms:priv>
   	<hrms:priv func_id="" module_id="">
   			<input type="button" name="b_retrun" value="返回" class="mybutton" onclick="returnexam();" />
	</hrms:priv>
       </td>
     </tr>     
</table>
</html:form>
<script>
function returnexam(){
	paperQuestionTypeForm.action = "/train/trainexam/paper.do?b_query=link";
	paperQuestionTypeForm.submit();
}
function isNumber(id){
	var theFloat = document.getElementById(id+"_score").value;
	//判断是否为浮点数
	var len=theFloat.length;
	if (len==0)
		return;
	var dotNum=0;
	for(var i=0;i<len;i++){
		var oneNum=theFloat.substring(i,i+1);
		if (oneNum==".")
			dotNum++;
		if ( ((oneNum<"0" || oneNum>"9") && oneNum!=".") || dotNum>1){
			document.getElementById(id+"_score").value="";
			return;
		}
		if (len>1 && theFloat.substring(0,1)=="0"){
			if (theFloat.substring(1,2)!="."){
				document.getElementById(id+"_score").value="";
				return;
			}
		}
	}
}
</script>