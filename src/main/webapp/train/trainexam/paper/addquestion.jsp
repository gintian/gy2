<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.businessobject.train.trainexam.question.questiones.QuestionesBo"%>
<%@page import="com.hrms.struts.taglib.CommonData,com.hrms.frame.utility.AdminDb,java.sql.Connection"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
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
			UserView userView = (UserView) session.getAttribute(WebConstant.userView);
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
	height:expression(document.body.clientHeight-133);
  	width:expression(document.body.clientWidth-20); 
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
<script language="JavaScript" src="/js/wz_tooltip.js"></script>

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
	
	function savequestion(){
		var a=0;
		var sels="";
		var allsels="";
		var a_IDs=document.getElementsByName("selectid");
		var inputs = document.getElementsByName("r5200");		
		for(var i=0;i<inputs.length;i++)
		{			
		   if(inputs[i].type=='checkbox' && inputs[i].name!="selbox")
		   {		   			
				if(inputs[i].checked==true)
				{
					sels+=a_IDs[a].value+",";
				}
				allsels+=a_IDs[a].value+",";
				a++;
		   }
	    }
	    if(allsels.length<1){
	    	alert("没有要添加的试题!");
	    	return;
	    }
		var hashvo=new ParameterSet();
		hashvo.setValue("allsels", allsels);
        hashvo.setValue("sels", sels);
        hashvo.setValue("r5300","${paperQuestionTypeForm.r5300}");
        hashvo.setValue("type_id","${paperQuestionTypeForm.type_id}");
        var request=new Request({method:'post',onSuccess:delajaxok,functionId:'2020070005'},hashvo);
	}
    function delajaxok(outparamters){
		var flag=outparamters.getValue("flag"); 
		if("ok"==flag){
			returnexam();
		}else
			alert("删除失败！");
	}
	
	function change(){
		paperQuestionTypeForm.action="/train/trainexam/paper/questiontype.do?b_query=link";
		paperQuestionTypeForm.submit();
	}
	
	function getContent(r5200){
		if(!r5200)
			return;
		var hashvo=new ParameterSet();
		hashvo.setValue("r5200", r5200);
        var request=new Request({method:'post',onSuccess:outContent,functionId:'2020070070'},hashvo);
	}
	function outContent(outparamters){
		var content=outparamters.getValue("content");
		config.FontSize='10pt';//hint提示信息中的字体大小
		Tip(getDecodeStr(content),STICKY,true);
	}
</script>
<hrms:themes></hrms:themes>
<%int i=0;%>
<html:form action="/train/trainexam/paper/questiontype" >
<table border="0" align="center" cellpadding="0" cellspacing="0">
   <tr style="height: 35px">
   	<td>
   	&nbsp;<bean:message key="train.trainexam.question.questiones.knowledge"/>&nbsp;
   	<html:text name="paperQuestionTypeForm" styleClass="text4" property="knowledgeviewvalue"/>
   	<html:hidden name="paperQuestionTypeForm" property="knowledge" onchange="change();"></html:hidden>
   	<img src="/images/code.gif" onclick="openTrainInputCodeDialog('68','knowledge');" style="cursor: pointer;" align="absmiddle"/>&nbsp;&nbsp;&nbsp;&nbsp;
   	<bean:message key="train.trainexam.question.questiones.difficulty"/>&nbsp;
    <span style="vertical-align: middle;">
   	<hrms:optioncollection name="paperQuestionTypeForm" property="questions" collection="pos_list"/>
	<html:select name="paperQuestionTypeForm" property="difficulty" styleId="difficulty" onchange="change();" size="1">
		<html:option value=""><bean:message key="edit_report.All"/></html:option>
	    <html:options collection="pos_list" property="dataValue" labelProperty="dataName"/>
	</html:select>
	</span>
	</td>
   </tr>
   <tr>
    <td>
     <div class="fixedDiv2"> 
     <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" id="tableid">
           <tr class="fixedHeaderTr">
             <td align="center" class="TableRow noleft" style="border-top: none;" nowrap width="30">
              <input type="checkbox" name="selbox" onclick="batch_select(this,'r5200');" title='<bean:message key="label.query.selectall"/>'>
             </td>
	         <td align="center" class="TableRow noleft" style="border-top: none;" nowrap  style="width:150px;">
	           &nbsp;<bean:message key="train.trainexam.question.questiones.knowledge"/>&nbsp;
	         </td>
	         <td align="center" class="TableRow noleft" style="border-top: none;" nowrap>
	            &nbsp;<bean:message key="train.trainexam.question.questiones.questionname"/>&nbsp;
	         </td>
	         <td align="center" class="TableRow noleft" style="border-top: none;" nowrap style="width:120px;">
	            &nbsp;<bean:message key="train.trainexam.question.questiones.difficulty"/>&nbsp;          
	         </td>
             <td align="center" class="TableRow noleft noright" style="border-top: none;" nowrap  style="width:80px;">
                &nbsp;<bean:message key="train.trainexam.question.questiones.fraction"/>&nbsp;  
             </td> 	    	    		        	        	        
           </tr>

          <hrms:paginationdb id="element" name="paperQuestionTypeForm" sql_str="paperQuestionTypeForm.strsql" table="" where_str="paperQuestionTypeForm.strwhere" columns="paperQuestionTypeForm.columns" order_by="paperQuestionTypeForm.order_by" page_id="pagination" pagerows="${paperQuestionTypeForm.pagerows}" indexes="indexes" >
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
          <bean:define  name="element" property="r5200" id="r5200"/>
            <td align="center" class="RecordRow noleft" style="border-top: none;" nowrap>
            	<bean:define id="id" name="element" property="id" />
            	<%String r5200id = SafeCode.encode(PubFunc.encrypt(id.toString()));
            	String tid = SafeCode.encode(PubFunc.encrypt(r5200.toString()));%>
            	
            	<html:checkbox name="element" property="r5200" value="<%=r5200id %>" />
               <input type=hidden name=selectid value='<%=tid %>' />
            </td>  
                         
              <td align="left" class="RecordRow noleft" style="border-top: none;" nowrap> 
              	&nbsp;<%=QuestionesBo.getKnowledgeIdByNames(r5200.toString()) %>&nbsp;
              </td>                      
              <td align="left" class="RecordRow noleft" style="border-top: none;" onmouseout="UnTip();" onmouseover="getContent('${r5200 }');" nowrap> 
              	<!-- &nbsp;<bean:write  name="element" property="r5204" filter="true"/>&nbsp;  -->
              	<bean:define id="r5204" name="element" property="r5204"></bean:define> 
              	&nbsp;<%=QuestionesBo.toHtml(r5204.toString()) %>
              </td>  
              <td align="left" class="RecordRow noleft" style="border-top: none;" nowrap>  
              	<logic:iterate id="it" name="paperQuestionTypeForm" property="questions">
					<bean:define id="r5203" name="element" property="r5203"></bean:define>
					<%if (((CommonData)it).getDataValue().equals(r5203.toString())) {%>
						&nbsp;<bean:write name="it" property="dataName"/>&nbsp;
					<%} %>
				</logic:iterate>
              </td> 
              <td align="right" class="RecordRow noleft noright" style="border-top: none;" nowrap>
                &nbsp;<bean:write  name="element" property="r5213" filter="true"/>&nbsp;
              </td>
          </tr>
        </hrms:paginationdb>
</table>
</div>
</td>
</tr>
<tr><td style="padding-right: 5px;">
<table width="100%"  align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            	<hrms:paginationtag name="paperQuestionTypeForm"
								pagerows="${paperQuestionTypeForm.pagerows}" property="pagination"
								scope="page" refresh="true"></hrms:paginationtag>
			</td>
	        <td  align="right" nowrap class="tdFontcolor">
		          <hrms:paginationdblink name="paperQuestionTypeForm" property="pagination" nameId="paperQuestionTypeForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table>
</td></tr>
<tr style="height: 35px"><td align="center">
	<hrms:priv func_id="" module_id="">
			<input type="button" name="b_retrun" value="确定" class="mybutton" onclick="savequestion();" />
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
	paperQuestionTypeForm.action = "/train/trainexam/paper/questiontype.do?b_add=link";
	paperQuestionTypeForm.submit();
}
</script>