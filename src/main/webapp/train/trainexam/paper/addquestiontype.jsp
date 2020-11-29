<%@ page contentType="text/html; charset=UTF-8"%>
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
					paperQuestionTypeForm.action="/train/trainexam/paper/questiontype.do?b_add=link&r5300=${paperQuestionTypeForm.r5300}";
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
				paperQuestionTypeForm.action="/train/trainexam/paper/questiontype.do?b_add=link&r5300=${paperQuestionTypeForm.r5300}";
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
        
        paperQuestionTypeForm.action="/train/trainexam/paper/questiontype.do?b_add=link&r5300=${paperQuestionTypeForm.r5300}";
		paperQuestionTypeForm.submit();
	}
	
	function verify(r5300){//校验
		var hashvo=new ParameterSet();
		hashvo.setValue("flag","1");
	    hashvo.setValue("r5300",r5300);
	    var request=new Request({method:'post',onSuccess:addtypeok,functionId:'2020070009'},hashvo);
	}
	function addtypeok(outparamters){
		var flag=outparamters.getValue("flag"); 
		if("ok"==flag){
			paperspreview();
		}else if("no"==flag)
			if(confirm(getDecodeStr(outparamters.getValue("mess"))))
				paperspreview();
	}
	
	function paperspreview(){
		paperQuestionTypeForm.action="/train/trainexam/paper/preview/paperspreview.do?b_query=link&r5300=${paperQuestionTypeForm.r5300}&exam_type=2&flag=1&returnId=1";
		//paperQuestionTypeForm.action="/train/trainexam/paper/preview/paperspreview.do?b_single=link&r5300=${paperQuestionTypeForm.r5300}&current=1&exam_type=2&flag=1&returnId=1";//单题试卷
		paperQuestionTypeForm.submit();
	}
</script>
<hrms:themes></hrms:themes>
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
             <td align="center" class="TableRow" nowrap width="20" style="border-left: 0px;border-top: none;">
              <input type="checkbox" name="selbox" onclick="batch_select(this,'pagination.select');" title='<bean:message key="label.query.selectall"/>'>
             </td>
	         <td align="center" class="TableRow" style="border-left: 0px;border-top: none;" nowrap >
	           &nbsp;题型&nbsp; 
	         </td>
	         <td align="center" class="TableRow" style="border-left: 0px;border-top: none;" nowrap width="130">
	            &nbsp;题数&nbsp;
	         </td>
	         <td align="center" class="TableRow" style="border-left: 0px;border-top: none;" nowrap width="130">
	            &nbsp;分数&nbsp;              
	         </td>
             <td align="center" class="TableRow" style="border-left: 0px;border-top: none;" nowrap  width="100">
                &nbsp;操作&nbsp;
             </td>	
             <td align="center" class="TableRow" nowrap  width="45" style="border-right: 0px;border-left: 0px;border-top: none;"">
                &nbsp;排序
             </td>    	    	    		        	        	        
           </tr>

          <hrms:paginationdb id="element" name="paperQuestionTypeForm" sql_str="paperQuestionTypeForm.strsql" table="" where_str="paperQuestionTypeForm.strwhere" columns="paperQuestionTypeForm.columns" order_by="paperQuestionTypeForm.order_by" page_id="pagination" pagerows="15" indexes="indexes">
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
            <td align="center" class="RecordRow" nowrap style="border-left: 0px;border-top: none;">
               <hrms:checkmultibox name="paperQuestionTypeForm" property="pagination.select" value="true" indexes="indexes"/>
               <input type=hidden name=selectid value='<bean:write  name="element" property="type_id" filter="true"/>' />
            </td>  
                         
              <td align="left" class="RecordRow" style="border-left: 0px;border-top: none;" nowrap> 
              	&nbsp;<bean:write  name="element" property="type_name" filter="true"/>&nbsp;
              </td>                      
              <td align="right" class="RecordRow" style="border-left: 0px;border-top: none;" nowrap> 
              	&nbsp;<bean:write  name="element" property="max_num" filter="true"/>&nbsp;   
              </td>  
              <td align="right" class="RecordRow" style="border-left: 0px;border-top: none;" nowrap>  
              	<logic:equal value=".00" name="element" property="score" >
              		&nbsp;0.00&nbsp;
              	</logic:equal>
              	<logic:notEqual value=".00" name="element" property="score" >
              		&nbsp;<bean:write  name="element" property="score" filter="true" format="0.00"/>&nbsp;
              	</logic:notEqual>
              </td> 
             <td align="center" class="RecordRow" style="border-left: 0px;border-top: none;" nowrap  width="100">
                 <a href="###" onclick="editquestions('<%=i %>','<bean:write  name="element" property="type_id" filter="true"/>')"> 
          	   		<img src="/images/edit.gif" alt="编辑试题" border="0">
            	 </a>&nbsp; 
            	 <a href="###" onclick="addquestions('<bean:write  name="element" property="type_id" filter="true"/>')"> 
          	   		<img src="/images/add.gif" alt="增加试题" border="0">
            	 </a>
             </td>
             <td align="center" class="RecordRow" nowrap width="50" style="border-right: 0px;border-left: 0px;border-top: none;"">
             	<bean:define id="id" name="element" property="norder"/>
             	<bean:define id="start" name="paperQuestionTypeForm" property="start"/>
             	<bean:define id="end" name="paperQuestionTypeForm" property="end"/>
             	<%if(id.toString().equals(start)){ %>
             		&nbsp;&nbsp;&nbsp;
				<%} else{%>
				<a href="javaScript:sortItem('up','<bean:write  name="element" property="type_id" filter="true"/>');">
					<img src="/images/up01.gif" width="12" height="17" border=0></a>
				<%} 
             	if(id.toString().equals(end)){ 
				%>
					&nbsp;&nbsp;&nbsp;
				<%} else{%>
				<a href="javaScript:sortItem('down','<bean:write  name="element" property="type_id" filter="true"/>');">
					<img src="/images/down01.gif" width="12" height="17" border=0></a> 
				<%} %>
             </td>
          </tr>
        </hrms:paginationdb>
</table>
</div>
</td>
</tr>
<tr style="height: 35px"><td align="left">
	<hrms:priv func_id="" module_id="">
			<input type="button" name="b_retrun" value="预览试卷" class="mybutton" onclick="verify('${paperQuestionTypeForm.r5300 }');" />
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
</script>