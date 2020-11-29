<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
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
<script language="JavaScript">
function toggles(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "block";
	}
} 
function searchImpv(){
	importantEvForm.action="/general/impev/importantev.do?b_search=link&a_code=${importantEvForm.a_code}";
	importantEvForm.submit();
}
function comment(p0600){
	importantEvForm.action="/general/impev/importantevcomment.do?b_comment=link&a_code=${importantEvForm.a_code}&p0600="+p0600;
	importantEvForm.submit();
	return false;
}
function del(){
	var checks = document.getElementsByName("selectedev"); 
	var selecteds = "";
	var temp="";
	for(var i=0;i<checks.length;i++){
		if(checks[i].checked){
			temp=checks[i].value.split(",");
			selecteds +=temp[0]+",";
		}
	}
	if(selecteds==""){
		alert("请选择要删除项！");
		return;
	}else{
		if(confirm("确认要删除吗？")){
			importantEvForm.action = "/general/impev/importantev.do?b_delete=link&a_code=${importantEvForm.a_code}&selecteds="+selecteds;
			importantEvForm.submit();
		}else{
			return;
		}
	}
}
function outContent(p0600,contentid){
	var hashvo=new ParameterSet();
	hashvo.setValue("contentid",contentid);	
	hashvo.setValue("p0600",getEncodeStr(p0600));
   	var request=new Request({method:'post',asynchronous:true,onSuccess:viewContent,functionId:'2020050048'},hashvo);
}
function viewContent(outparamters){
	var content=outparamters.getValue("content");
	Tip(getDecodeStr(content),STICKY,true);
}
function particular(p0600){
	importantEvForm.action = "/general/impev/importantev.do?b_particular=link&p0600="+p0600;
	importantEvForm.submit();
}
</script>
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
				从&nbsp;<input type="text" name="fromdate"  value="${importantEvForm.fromdate}"  extra="editor" class="inputtext" style="width:100px;font-size:10pt;text-align:left;border: 1pt solid #C4D8EE;" dropDown="dropDownDate">
				到&nbsp;<input type="text" name="todate" value="${importantEvForm.todate}" extra="editor" class="inputtext" style="width:100px;font-size:10pt;text-align:left;border: 1pt solid #C4D8EE;" dropDown="dropDownDate">
				&nbsp;<input type='button' class="mybutton" value='查询' onclick="searchImpv();"/>
			</span>
			</logic:equal>
			<logic:notEqual name="importantEvForm" property="checkflag" value="0"> 
			<span id="fromdateview">
				从&nbsp;<input type="text" name="fromdate"  value="${importantEvForm.fromdate}"  extra="editor" class="inputtext" style="width:100px;font-size:10pt;text-align:left;border: 1pt solid #C4D8EE;" dropDown="dropDownDate">
				到&nbsp;<input type="text" name="todate" value="${importantEvForm.todate}" extra="editor" class="inputtext" style="width:100px;font-size:10pt;text-align:left;border: 1pt solid #C4D8EE;" dropDown="dropDownDate">
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
	<tr id='headerTr'>
		<td align="center" class="TableRow_right common_background_color common_border_color" nowrap>
    		<input type="checkbox" name="selbox" onclick="batch_select_all(this);" title='<bean:message key="label.query.selectall"/>'>                        
	     </td>
		<logic:iterate id="element" name="importantEvForm"  property="fieldlist" indexId="index">
			<logic:notEqual name="element" property="itemid" value="p0600">
			<logic:notEqual name="element" property="itemid" value="a0100">
			<td align="center" class="TableRow" nowrap>
                 <bean:write name="element" property="itemdesc" filter="false"/>&nbsp;        
	       </td>
	       </logic:notEqual>
	       </logic:notEqual>
		</logic:iterate>
		 <td align="center"  class="TableRow_left common_background_color common_border_color" nowrap>评论</td> 
	</tr>
	<hrms:extenditerate id="element" name="importantEvForm" property="paginationForm.list" indexes="indexes"  
	pagination="paginationForm.pagination" pageCount="${importantEvForm.pagerows}" scope="session">
	<bean:define id="nid" name='element' property='p0600'/>
    <%if(i%2==0){%>
    	<tr class="trShallow">
    <%}else{%>
         <tr class="trDeep">
    <%}i++;%>  
         <td align="center" class="RecordRow_right common_border_color" nowrap>
    		 <input type="checkbox" name="selectedev" value="${nid}">                     
		</td> 
    	<logic:iterate id="fielditem"  name="importantEvForm"  property="fieldlist" indexId="index">
			<logic:notEqual name="element" property="itemid" value="p0600">
			<logic:notEqual name="element" property="itemid" value="a0100">
    		<logic:equal name="fielditem" property="codesetid" value="0"> 
	      	<logic:notEqual name="fielditem" property="itemtype" value="M">
	      	<td  align="center" class="RecordRow" nowrap>
	      		<bean:write name="element" property="${fielditem.itemid}" filter="false"/>&nbsp;             
	      	</td>
	      	</logic:notEqual>
	      	<logic:equal name="fielditem" property="itemtype" value="M">
	      	<!-- td height="10" align="center" class="RecordRow" onmouseout="UnTip();" onmouseover="outContent('${nid }','${fielditem.itemid}');" nowrap -->
	      	<td height="10" align="center" onmouseout="UnTip();" onmouseover='outContent("${nid}","${fielditem.itemid }");' class="RecordRow"  nowrap>
	      		<!-- <logic:notEmpty name="element" property="${fielditem.itemid}"><span style="cursor:hand;color:#00C" onclick="particular('${nid }');">详细</span></logic:notEmpty> -->
	      		<span style="cursor:hand;color:#00C;color: #000000;" onclick="particular('${nid }');">
	      			<bean:define id="ncontent" name="element" property="${fielditem.itemid}" type="java.lang.String"></bean:define>
	      			      			<% 
	      			
	      		//LazyDynaBean dynaBean=(LazyDynaBean)pageContext.getAttribute("element");
	      		//FieldItem item=(FieldItem)pageContext.getAttribute("fielditem");
	      		//String value=(String)dynaBean.get(item.getItemid());
	      		//value=PubFunc.reverseHtml(value);
	      		//out.println(value);
	      		 %>	
	      		 <%=ncontent %>
	      		</span>
	      	</td>
	      </logic:equal>          
	      </logic:equal>
	      <logic:notEqual name="fielditem" property="codesetid" value="0">
    		<td align="center" class="RecordRow" nowrap>
          	   <hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
          	   <bean:write name="codeitem" property="codename" />&nbsp;                                                 
	      	</td> 
	      </logic:notEqual>
	      </logic:notEqual>
	      </logic:notEqual>
    	</logic:iterate>
    	  <td class="RecordRow_left  common_border_color" align="center" nowrap>&nbsp;
    		<a href="" onclick="return comment('${nid }');"><img src="/images/edit.gif" border=0 ></a>                
	    </td>
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
<hrms:priv func_id="031301">
<table width="100%" border="0" cellspacing="0"  align="left" cellpadding="0">
	<tr>
		<td align="center" height="40">
		<input type="button" value="删除" class="mybutton" onclick="del();">
		</td>
	</tr>
</table>
</hrms:priv>
</td></tr>
</table>
<script>
	if(/msie/i.test(navigator.userAgent)){//该样式只在ie下生效
		document.getElementById("headerTr").className="headerTr";
	}
	
	</script>
</html:form>
