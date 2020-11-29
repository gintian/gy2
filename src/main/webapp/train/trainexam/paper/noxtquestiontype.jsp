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
.out{
	border-top:50px #f4f7f7 solid;/*上边框宽度等于表格第一行行高*/
	width:0px;/*让容器宽度为0*/
	height:0px;/*让容器高度为0*/
	border-left:200px #f4f7f7 solid;/*左边框宽度等于表格第一行第一格宽度*/
	position:relative;/*让里面的两个子容器绝对定位*/
}
b{font-style:normal;display:block;position:absolute;top:-40px;left:-70px;width:35px;}
em{font-style:normal;display:block;position:absolute;top:-20px;left:-170px;width:50x; }
.common_background_color{
	background-color:#f4f7f7;
}

.px_shijuan_style{
 border-left: 0px;
 height: 50px;
 width: 200px;
 background-image: url('/images/dipline.jpg');
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
	
	function checkscale(){
		var ids="";
		var values="";
		var check=0;
		var inputs = document.getElementsByName("kdscale");
		var tmpT = "";
		for(var i=0;i<inputs.length;i++){
			//if("text"==inputs[i].type&&"kdscale"==inputs[i].id){
				var _T = inputs[i].id.substr(0,1);//暂时试题类型只有1-6  1位
				if(tmpT=="")
					tmpT=_T;
				if(tmpT!=_T){
					if(check!=0&&check!=100){
						alert(inputs[i-1].alt+"的百分比和为："+check+"%!\r\n题型百分比和应为100%！\r\n请重新设置生成比例。");
						return;
					}
					tmpL=i;
					tmpT=_T;
					check=0;
				}
				if(inputs[i].value){
					ids+=inputs[i].id+"`";
					values+=inputs[i].value+"`";
					//if(tmpT==_T){
						check+=parseInt(inputs[i].value);
					//}
					
				}
				if(i==inputs.length-1){
					if(check!=0&&check!=100){
						alert(inputs[i].alt+"的百分比和为："+check+"%!\r\n题型百分比和应为100%！\r\n请重新设置生成比例。");
						return;
					}
				}
				
			//}
		}
		
		var hashvo=new ParameterSet();
        hashvo.setValue("ids", ids);
        hashvo.setValue("values", values);
        hashvo.setValue("r5300","${paperQuestionTypeForm.r5300}");
        return hashvo;
	}
	
	function checkquestion(){
		
		var hashvo = checkscale();
		if(hashvo!=null){
			document.getElementById("wait").style.display='';
        	var request=new Request({method:'post',asynchronous:true,onSuccess:generatequestion,functionId:'2020070027'},hashvo);
        }
	}
	function generatequestion(outparamters){
		var flag=outparamters.getValue("flag"); 
		document.getElementById("wait").style.display='none';
		if("ok"==flag){
			//paperQuestionTypeForm.action="/train/trainexam/paper/preview/paperspreview.do?b_search=link&r5300=${paperQuestionTypeForm.r5300}&exam_type=2&flag=1&returnId=1";
			//paperQuestionTypeForm.submit();
			var w=window.screen.width-20;
		var h=window.screen.height-80;
		var url="/train/trainexam/paper/preview/paperspreview.do?b_search=link&r5300=${paperQuestionTypeForm.r5300}&exam_type=2&flag=1&returnId=0";
		window.open (url, 'newwindow', 'top=0,left=0,toolbar=no,menubar=no,resizable=yes,location=no,status=no,scrollbars=yes,width='+w+',height='+h);
		}else if("error"==flag)
			alert("生成失败！");
		else
			alert(getDecodeStr(flag));
	}
	
	function save(){
		var hashvo = checkscale();
		if(hashvo!=null)
			var request=new Request({method:'post',asynchronous:false,onSuccess:generatesave,functionId:'2020070022'},hashvo);
	}
	
	function generatesave(outparamters){
		var flag=outparamters.getValue("flag"); 
		if("ok"==flag){
			alert("保存成功！");
		}else if("error"==flag)
			alert("生成失败！");
		else
			alert(getDecodeStr(flag));
	}
	
	function changeknow(type_id){
		document.getElementById(type_id+"_knowsvalue").innerHTML=document.getElementById(type_id+"_addKnowledgeviewvalue").value;
	}
	
	function toback(){
		paperQuestionTypeForm.action="/train/trainexam/paper/autoquestiontype.do?b_add=link";
		paperQuestionTypeForm.submit();
	}
	
	function cleartext(){
		var scale = document.getElementsByName("kdscale");
		for(var i=0;i<scale.length;i++){
			scale[i].value="";
		}
	}
</script>
<hrms:themes/>
<%int i=0;%>
<html:form action="/train/trainexam/paper/questiontype">
<table border="0" align="center" cellpadding="0" cellspacing="0">
   <tr>
    <td style="padding-top: 5px;">
     <div class="fixedDiv2"> 
     <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="" id="tableid" style="border-collapse: collapse;">
           <tr class="fixedHeaderTr">
             <td align="center" class="px_shijuan_style" style="padding: 0;" width="200px" id="td1" nowrap colspan="2">
             
             <!-- <div class="out">
             <b>难度</b><em>知识点</em>
             </div> --> 
             </td>
             <logic:iterate id="difficulty" name="paperQuestionTypeForm" property="difficultyList">
		         <td align="center" class="TableRow" style="border-right: none;border-top: none;" nowrap>
		           <bean:write name="difficulty" property="dataName"/>
		         </td>
	         </logic:iterate>
           </tr>
           <%String t=""; %>
           <logic:iterate id="knowledge" name="paperQuestionTypeForm" property="knowledgeList" indexId="y">
           	<tr>
           		<bean:define id="r5300" name="paperQuestionTypeForm" property="r5300"></bean:define>
           		<%String type_id=knowledge.toString().substring(0,knowledge.toString().indexOf("_"));
           		String kl=knowledge.toString().substring(knowledge.toString().indexOf("_")+1);
           		if(!t.equals(type_id)){
           			t=type_id; %>
	           		<td align="center" class="RecordRow common_background_color" style="padding:0; border-left: 0px;width: 60px;" rowspan="<%=QuestionesBo.knowLedgeSize(r5300.toString(),type_id) %>">
	           			<%=QuestionesBo.getTypeNmae(type_id) %>
	           		</td>
           		<%} %>
	           	<td align="left" class="RecordRow common_background_color" style="width: 140px;border-left: none;padding: 0;"> 
	              	&nbsp;<%=QuestionesBo.getKnowledgeviewvaluee(kl) %>&nbsp;
              	</td> 
              	<logic:iterate id="difficulty" name="paperQuestionTypeForm" property="difficultyList" indexId="x">
              		<bean:define id="diffvalue" name="difficulty" property="dataValue"></bean:define>
			         <td align="center" class="RecordRow" nowrap style="border-right: 0px;">
			           <input type="text" maxlength="3" class="text4" name="kdscale" id="${knowledge }_${diffvalue }" alt="<%=QuestionesBo.getTypeNmae(type_id) %>"
			           		value="<%=QuestionesBo.getValue(knowledge.toString()+"_"+diffvalue.toString()) %>"  
			           		style="width: 30px;text-align: right;padding-right: 2px;" 
			           		onpropertychange='if(/[^\d*]/.test(this.value)) this.value=this.value.replace(/[^\d*]/,"")'/><font color="#666666">%</font>
			         </td>
		        </logic:iterate>
           	</tr>
           </logic:iterate>
</table>
</div>
</td>
</tr>
<tr style="height: 35px"><td align="left">
	<hrms:priv func_id="" module_id="">
			<input type="button" name="b_next" value="上一步" class="mybutton" onclick="toback();" />
   	</hrms:priv>
   	<hrms:priv func_id="" module_id="">
   			<input type="button" name="b_goQuestion" value="保存" class="mybutton" onclick="save();" />
   	</hrms:priv>
   	 		<input type="button" name="b_clear" value="清空" class="mybutton" onclick="cleartext();" />
   	<hrms:priv func_id="" module_id="">
   			<input type="button" name="b_goQuestion" value="生成试卷" class="mybutton" onclick="checkquestion();" />
   	</hrms:priv>
       </td>
     </tr>     
</table>
</html:form>
<div id='wait' style='position:absolute;top:180;left:300;display: none'>
<table border="1" width="430" cellspacing="0" cellpadding="4" class="table_style" height="150" align="center">
           <tr>

             <td class="td_style common_background_color" height="40">正在生成试卷...</td>

           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center>
               <marquee class="marquee_style" direction="right" width="430" scrollamount="7" scrolldelay="10">
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
<script>
function returnexam(){
	paperQuestionTypeForm.action = "/train/trainexam/paper.do?b_query=link";
	paperQuestionTypeForm.submit();
}
</script>