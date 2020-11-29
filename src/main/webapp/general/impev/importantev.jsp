<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.general.impev.ImportantEvForm,
				 com.hjsj.hrms.businessobject.general.impev.DateUtil,
				 com.hrms.hjsj.sys.DataDictionary,
				 com.hrms.hjsj.sys.FieldItem,
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant,
				 org.apache.commons.beanutils.LazyDynaBean,
				 com.hjsj.hrms.utils.PubFunc" %>
<style>
.headerTr{ 
	position:relative; 
	top:expression(this.offsetParent.scrollTop); 
}
</style>
<script language="JavaScript" src="/js/wz_tooltip.js"></script>
<script type="text/javascript" src="/js/validateDate.js"></script>
<script type="text/javascript">
<!--
function toggles(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "block";
	}
} 
function searchImpv(){
	importantEvForm.action="/general/impev/importantev.do?b_query=link";
	importantEvForm.submit();
}
function add() {
	importantEvForm.action = "/general/impev/importantev.do?b_add=link";
	importantEvForm.submit();
}
function update(p0600){
	importantEvForm.action = "/general/impev/importantev.do?b_add=link&p0600="+p0600;
	importantEvForm.submit();
}
function del(){
	var checks = document.getElementsByName("selectedev"); 
	var selecteds = "";
	var flag="";
	var temp="";
	for(var i=0;i<checks.length;i++){
		if(checks[i].checked){
			temp=checks[i].value.split(",");
			selecteds +=temp[0]+",";
			flag +=temp[1]+",";
		}
	}
	if(selecteds==""){
		alert("请选择要删除项！");
		return;
	}else{
		if(flag.indexOf("1")!=-1){
			alert("您不能删除已提交项，请重新选择！");
			return;
		}
		if(confirm("确认要删除吗？")){
			importantEvForm.action = "/general/impev/importantev.do?b_del=link&selecteds="+selecteds;
			importantEvForm.submit();
		}else{
			return;
		}
	}
}
function view(p0600){
	importantEvForm.action = "/general/impev/importantevcomment.do?b_query=link&p0600="+p0600;
	importantEvForm.submit();
}
function chaosong(){
	var checks = document.getElementsByName("selectedev"); 
	var selecteds = "";
	var flag="";//未提交项主键
	var temp="";
	for(var i=0;i<checks.length;i++){
		if(checks[i].checked){
			temp=checks[i].value.split(",");
			selecteds +=temp[0]+",";
			if(temp[1]==2){//未提交项
				flag +=temp[0]+",";
			}
		}
	}
	if(selecteds==""){
		alert("请选择要抄送项！");
		return;
	}else{		
			//var target_url="/selfservice/lawbase/add_law_text_role.do?b_relating1=link&pri=0&chkflag=12&p0600="+selecteds;
			var target_url="/selfservice/lawbase/add_law_text_role.do?b_relating1=link`pri=0`chkflag=12`p0600="+selecteds;
       		var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
       		if(getBrowseVersion()){
       			var return_vo= window.showModalDialog(iframe_url, 'trainClass_win2', 
		      				"dialogWidth:600px; dialogHeight:430px;resizable:no;center:yes;scroll:yes;status:no;");	
				if(return_vo!=null){
					var recordstr = "";
					for(var i=0;i<return_vo.length;i++){
						if(return_vo[i]!=null&&return_vo[i].length>0){
							recordstr+=return_vo[i]+"`";
						}
					}
		    		var hashvo=new ParameterSet();
					hashvo.setValue("personstr",recordstr);
					hashvo.setValue("selecteds",selecteds);
					hashvo.setValue("flag",flag);
			 		var request=new Request({method:'post',onSuccess:showResult,functionId:'2020050044'},hashvo);
				}
       		}else{
       			//兼容非IE浏览器 弹窗替换用 open弹窗  wangb 201801023
				var iTop = (window.screen.availHeight - 30 - 430) / 2;  //获得窗口的垂直位置
				var iLeft = (window.screen.availWidth - 10 - 600) / 2; //获得窗口的水平位置 
				window.open(iframe_url,"","width=600px,height=430px,resizable=no,scrollbars=no,status=no,left="+iLeft+",top="+iTop);
       		}
	}
}
//open 弹窗页面调用方法 效果等同showModalDialog  wangb 20180123
function openReturn_vo(return_vo){
	if(return_vo!=null){
		var recordstr = "";
		for(var i=0;i<return_vo.length;i++){
			if(return_vo[i]!=null&&return_vo[i].length>0){
				recordstr+=return_vo[i]+"`";
			}
		}
		var checks = document.getElementsByName("selectedev"); 
		var selecteds = "";
		var flag="";//未提交项主键
		var temp="";
		for(var i=0;i<checks.length;i++){
			if(checks[i].checked){
				temp=checks[i].value.split(",");
				selecteds +=temp[0]+",";
				if(temp[1]==2){//未提交项
					flag +=temp[0]+",";
				}
			}
		}
		var hashvo=new ParameterSet();
		hashvo.setValue("personstr",recordstr);
		hashvo.setValue("selecteds",selecteds);
		hashvo.setValue("flag",flag);
		var request=new Request({method:'post',onSuccess:showResult,functionId:'2020050044'},hashvo);
	}
}   	
function showResult(outparamters){ 
   	 if(outparamters.getValue("result")=="success"){
   	 	alert("抄送已完成！");
   	 	importantEvForm.action="/general/impev/importantev.do?b_query=link";
		importantEvForm.submit();
   	 } else{
   	 	alert("抄送失败！！！您选中的重要报告已有抄送给"+outparamters.getValue("result").substring(0,outparamters.getValue("result").length-1)+"这些人了，很抱歉不能重复抄送！");
   	 }
}
function outContent(p0600,contentid){
	var hashvo=new ParameterSet();
	hashvo.setValue("contentid",contentid);	
	hashvo.setValue("p0600",p0600);
	hashvo.setValue("flag","antev");
   	var request=new Request({method:'post',asynchronous:true,onSuccess:viewContent,functionId:'2020050048'},hashvo);
}
function viewContent(outparamters){
	var content=outparamters.getValue("content");
	Tip(getDecodeStr(content),STICKY,true);
}
function particular(p0600){
	importantEvForm.action = "/general/impev/importantev.do?b_particular=link&flag=isDecode&p0600="+p0600;
	importantEvForm.submit();
}
//-->
</script>
<hrms:themes />
<html:form action="/general/impev/importantev">
<%int i=0;%>
<table width="100%" border="0" cellspacing="0"  align="left" cellpadding="0">
<tr><td >
<table width="100%" border="0" cellspacing="0"  align="left" cellpadding="0">
	<tr>
		<td width="150">
			<html:radio name="importantEvForm" property="checkflag" onclick="searchImpv();" value="0"/>全部
			<html:radio name="importantEvForm" property="checkflag" onclick="toggles('fromdateview');" value="1"/>时间范围
		</td>
		<td align="left" nowrap>
			<logic:equal name="importantEvForm" property="checkflag" value="0"> 
			<span id="fromdateview" style="display:none;">
				从&nbsp;<input type="text" name="fromdate" onchange="if(!validate(this,'')) {this.focus(); this.value='${importantEvForm.fromdate }'; }" value="${importantEvForm.fromdate }"  extra="editor" class="inputtext" style="width:100px;font-size:10pt;text-align:left;border: 1pt solid #C4D8EE;"  dropDown="dropDownDate">
				到&nbsp;<input type="text" name="todate" onchange="if(!validate(this,'')) {this.focus(); this.value='${importantEvForm.todate }'; }"  value="${importantEvForm.todate }" extra="editor" class="inputtext" style="width:100px;font-size:10pt;text-align:left;border: 1pt solid #C4D8EE;" dropDown="dropDownDate">
				&nbsp;<input type='button' class="mybutton" value='查询' onclick="searchImpv();"/>
			</span>
			</logic:equal>
			<logic:notEqual name="importantEvForm" property="checkflag" value="0"> 
			<span id="fromdateview">
				从&nbsp;<input type="text" name="fromdate"  onchange="if(!validate(this,'')) {this.focus(); this.value='${importantEvForm.fromdate }'; }"  value="${importantEvForm.fromdate }"  extra="editor" class="inputtext" style="width:100px;font-size:10pt;text-align:left;border: 1pt solid #C4D8EE;" dropDown="dropDownDate">
				到&nbsp;<input type="text" name="todate"   onchange="if(!validate(this,'')) {this.focus(); this.value='${importantEvForm.todate }'; }" value="${importantEvForm.todate }" extra="editor" class="inputtext" style="width:100px;font-size:10pt;text-align:left;border: 1pt solid #C4D8EE;" dropDown="dropDownDate">
				&nbsp;<input type='button' class="mybutton" value='查询' onclick="searchImpv();"/>
			</span>
			</logic:notEqual>
		</td>
	</tr>
	<tr><td colspan="2" height="5px"></td></tr>
</table>
</td></tr>
<tr><td>
<div class="fixedDiv2" style="border-top:0px;">
<table width="100%" border="0" cellspacing="0"  align="left" cellpadding="0" class="ListTable">
	<tr class="headerTr">
		<td align="center" class="TableRow_right common_background_color common_border_color" nowrap>
    		<input type="checkbox" name="selbox" onclick="batch_select_all(this);" title='<bean:message key="label.query.selectall"/>'>                        
	     </td>
	     
		<logic:iterate id="element" name="importantEvForm"  property="fieldlist" indexId="index">
			<logic:notEqual name="element" property="itemid" value="p0600">
			<logic:notEqual name="element" property="itemid" value="a0100">
			<logic:notEqual name="element" property="itemid" value="a0101">
			<logic:notEqual name="element" property="itemid" value="b0110">
			<logic:notEqual name="element" property="itemid" value="e0122">
			<logic:notEqual name="element" property="itemid" value="e01a1">
			<td align="center" class="TableRow" nowrap>
                 &nbsp;<bean:write name="element" property="itemdesc" filter="false"/>&nbsp;     
	       </td>
	       </logic:notEqual>
	       </logic:notEqual>
	       </logic:notEqual>
	       </logic:notEqual>
	       </logic:notEqual>
	       </logic:notEqual>
		</logic:iterate>
		 <td align="center" class="TableRow" nowrap>修改</td>
		 <td align="center" class="TableRow_left common_background_color common_border_color" nowrap>评论</td>
		 
	</tr>
	<hrms:extenditerate id="element" name="importantEvForm" property="paginationForm.list" indexes="indexes"  
	pagination="paginationForm.pagination" pageCount="${importantEvForm.pagerows}" scope="session">
	<bean:define id="nid" name='element' property='p0600'/>
	<bean:define id="p0609" name="element" property="p0609" />
    <%if(i%2==0){%>
    	<tr class="trShallow">
    <%}else{%>
         <tr class="trDeep">
    <%}i++;%>  
         
		<td align="center" class="RecordRow_right common_border_color"  width="4%" nowrap>
    		 <input type="checkbox" name="selectedev" value="${nid},${p0609 }">                     
		</td> 
    	<logic:iterate id="fielditem"  name="importantEvForm"  property="fieldlist" indexId="index">
			<logic:notEqual name="fielditem" property="itemid" value="p0600">
			<logic:notEqual name="fielditem" property="itemid" value="a0100">
			<logic:notEqual name="fielditem" property="itemid" value="a0101">
			<logic:notEqual name="fielditem" property="itemid" value="b0110">
			<logic:notEqual name="fielditem" property="itemid" value="e0122">
			<logic:notEqual name="fielditem" property="itemid" value="e01a1">
    		<logic:equal name="fielditem" property="codesetid" value="0"> 
	      	<logic:notEqual name="fielditem" property="itemtype" value="M">
	      	<td  align="left" class="RecordRow_left common_border_color" nowrap>
	      		&nbsp;<bean:write name="element" property="${fielditem.itemid}" filter="false"/>&nbsp;             
	      	</td>
	      	</logic:notEqual>
	      	<logic:equal name="fielditem" property="itemtype" value="M">
	      	<!-- td  height="10" align="center" class="RecordRow" onmouseout="UnTip();" onmouseover="outContent('${nid }','${fielditem.itemid}');" nowrap  -->
	      	<td height="10"  class="RecordRow_left common_border_color" style="padding-left:5px;padding-right:5px;" nowrap>
	      		<!-- <logic:notEmpty name="element" property="${fielditem.itemid}"><span style="cursor:hand;color:#00C" onclick="particular('${nid }');">详细</span></logic:notEmpty> -->
	      		<span onmouseout="UnTip();" onmouseover='outContent("${nid}","${fielditem.itemid }");' style="cursor:hand;color:#00C;color: #000000;" onclick="particular('${nid }');">
	      			<% 
	      			
	      		//LazyDynaBean dynaBean=(LazyDynaBean)pageContext.getAttribute("element");
	      		//FieldItem item=(FieldItem)pageContext.getAttribute("fielditem");
	      		//String value=(String)dynaBean.get(item.getItemid());
	      		//value=PubFunc.reverseHtml(value);
	      		//out.println(value);
		      		 %>	
		      		 	
		      			<bean:write name="element" property="${fielditem.itemid}" filter="false"/>
		      			
	      		
	      		</span>
	      	</td>
	      </logic:equal>          
	      </logic:equal>
	      <logic:notEqual name="fielditem" property="codesetid" value="0">
	    	<td align="left" class="RecordRow_left common_border_color" width="8%" nowrap>
          	   	<hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
          	   	&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;                                                 
	      	</td> 
	      </logic:notEqual>
	      </logic:notEqual>
	      </logic:notEqual>
	      </logic:notEqual>
	      </logic:notEqual>
	       </logic:notEqual>
	       </logic:notEqual>
    	</logic:iterate>
    	<logic:equal name="element" property="p0609" value="1"> 
    	<td align="center" class="RecordRow" width="5%" nowrap>
          	  &nbsp;                                                 
	    </td>
	    
	    <%
			LazyDynaBean bean = (LazyDynaBean)pageContext.getAttribute("element");
			String p0600 = (String)bean.get("p0600");
			p0600 = PubFunc.encryption(p0600);
		%>
	     
	    <td align="center" class="RecordRow_left common_border_color" width="5%" nowrap>&nbsp;
    		<img src="/images/view.gif" border=0 onclick="view('<%=p0600%>');">                 
	    </td>
	    </logic:equal>
	    <logic:notEqual name="element" property="p0609" value="1">
	    <td align="center" class="RecordRow" width="5%" nowrap>
          	 <img src="/images/edit.gif" border=0 onclick="update('${nid}');">                                               
	    </td> 
	    <td align="center" class="RecordRow_left common_border_color" width="5%" nowrap>&nbsp;
    		&nbsp;                 
	    </td>
	    </logic:notEqual>
    </tr>

	
  </hrms:extenditerate>   
</table>
</div>
<table width="100%"  align="center" class="RecordRowP fixedDiv3">
		<td valign="bottom" class="tdFontcolor">
			<hrms:paginationtag name="importantEvForm" pagerows="${importantEvForm.pagerows}" property="paginationForm.pagination" refresh="true"></hrms:paginationtag>
		</td>
	    <td align="right" nowrap class="tdFontcolor">
		     <hrms:paginationlink  name="importantEvForm" property="paginationForm.pagination" nameId="paginationForm" >
			</hrms:paginationlink>
		</td>
</table>
<div>
<table width="100%" border="0" cellspacing="0"  align="left" cellpadding="0">
	<tr>
		<td align="center" height="40">
		<hrms:priv func_id="010801">
		<input type="button" value="新增" class="mybutton" onclick="add();">

		</hrms:priv>
		<hrms:priv func_id="010802">
		<input type="button" value="删除" class="mybutton" onclick="del();">

		</hrms:priv>
		<hrms:priv func_id="010803">
		<input type="button" value="抄送" class="mybutton" onclick="chaosong();">
		</hrms:priv>
		</td>
	</tr>
</table>
</div>
</td></tr>
</table>
<script language="javascript">
	if(!getBrowseVersion()){//兼容非IE浏览器  wangb 20180123
		var form = document.getElementsByName('importantEvForm')[0];
		var firstTable = form.getElementsByTagName('table')[0];
		firstTable.style.width = '100%';
		var pageTable = form.getElementsByClassName('RecordRowP')[0];
		pageTable.setAttribute('align','left');
		pageTable.style.width = '99.7%';
    	
	}

	var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串  
    var isFF = userAgent.indexOf("Firefox") > -1; //判断是否Firefox浏览器  
    if(isFF){//wangb 20180123 add 单独处理火狐浏览器 table 边框不显示处理
		var headerTr = document.getElementsByClassName('headerTr')[0];
		headerTr.removeAttribute('class');
    }	

</script>
</html:form>
