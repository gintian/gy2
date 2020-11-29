<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	
%>
<style>
.headerTr{ 
	position:relative; 
	top:expression(this.offsetParent.scrollTop); 
}

.va {vertical-align:middle;}
</style>
<!-- DIV id="overDiv" class="RecordRow" style="POSITION: absolute; Z-INDEX: 1;background-color:#FFFFCC;overflow:visible;background-image:../../images/mainbg.jpg"></DIV> -->
<script language="JavaScript" src="/js/wz_tooltip.js"></script>
<script language="JavaScript">
function toggles(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "block";
	}
} 
function hides(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "none";
	}
}
function addKanban(){
	kanBanForm.hsearch.value="";
	kanBanForm.itemid.value="";
	kanBanForm.action="/general/kanban/kanban.do?b_add=link&checkflag=add";
	kanBanForm.submit();
}
function editResPg(checkflag,p0500){
	kanBanForm.action="/general/kanban/kanban.do?b_add=link&checkflag="+checkflag+"&p0500="+p0500;
	kanBanForm.submit();
}
function delResPg(){
	if(!confirm("确定删除?")){
		return false;
	}
	var p0500arr="";
	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++){
		if(tablevos[i].type=="checkbox"){
			if(tablevos[i].checked==true){
				if(tablevos[i].value=="selbox")
					continue;
				if(tablevos[i].value=="on")
					continue;
				p0500arr+=tablevos[i].value+",";	
			}
      	 }
   	}	
   	if(p0500arr.length<1){
   		alert("请选择记录后再删除！");
   		return false;
   	}
	kanBanForm.action="/general/kanban/kanban.do?b_query=link&checkflag=delete&p0500arr="+p0500arr;
	kanBanForm.submit();
}
function sortItem(){
	kanBanForm.action="/general/kanban/kanban.do?b_query=link";
	kanBanForm.submit();
}
function changeCodeValue(){
  	var item=document.getElementById("itemid").value;
  	if(item=='no'){
  		hides("fromdateview");
		hides("codeidview");
		hides("textview");
		hides("fromnumview");
		toggles("allbutton");
  		return;
  	}
  	if(item==null||item==undefined||item.length<1){
  		hides("fromdateview");
		hides("codeidview");
		hides("textview");
		hides("fromnumview");
		toggles("allbutton");
  		return ;
  	}
  	if(item.toLowerCase()=='a0101_1'||item.toLowerCase()=='a0101_0'||item.toLowerCase()=='a0101'){
  		toggles("textview");
		hides("codeidview");
		hides("fromnumview");
		hides("fromdateview");
		hides("allbutton");
  		return ;
  	}
	var in_paramters="itemid="+item;
    var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,
    	onSuccess:showCodeFieldList,functionId:'3020050012'});
}
function showCodeFieldList(outparamters){
	var codelist=outparamters.getValue("codelist");
	var typeid=outparamters.getValue("typeid");
	if(typeid=='A'){
		if(codelist!=null&&codelist.length>1){
			toggles("codeidview");
			hides("textview");
			hides("fromnumview");
			hides("fromdateview");
			hides("allbutton");
			AjaxBind.bind(kanBanForm.codeid,codelist);
			document.getElementById("codeid").value="${kanBanForm.codeid}";
		}else{
			toggles("textview");
			hides("codeidview");
			hides("fromnumview");
			hides("fromdateview");
			hides("allbutton");
		}
	}else if(typeid=='N'){
		toggles("fromnumview");
		hides("codeidview");
		hides("textview");
		hides("fromdateview");
		hides("allbutton");
	}else if(typeid=='D'){
		toggles("fromdateview");
		hides("codeidview");
		hides("textview");
		hides("fromnumview");
		hides("allbutton");
	}else{
		hides("fromdateview");
		hides("codeidview");
		hides("textview");
		hides("fromnumview");
		toggles("allbutton");
	}
}
function  searchAll(){
	kanBanForm.hsearch.value="";
	kanBanForm.action="/general/kanban/kanban.do?b_query=link";
	kanBanForm.submit();
}
function viewRemark(p0500,resumeid){
	var hashvo=new ParameterSet();
	hashvo.setValue("p0500",p0500);
	hashvo.setValue("itemid",resumeid);
	var request=new Request({asynchronous:false,onSuccess:showRemark,functionId:'2020050032'},hashvo);
}
function showRemark(outparamters){
	var resume=outparamters.getValue("resume");
	Tip(getDecodeStr(resume),STICKY,true);
}
function selectQ(){
	var thecodeurl ="/train/traincourse/generalsearch.do?b_query=link&fieldsetid=p05"; 
	var dw=700,dh=350,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
	var return_vo= window.showModalDialog(thecodeurl, "", 
              	"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:no;status:no;");
    if(return_vo!=null) {
    	kanBanForm.hsearch.value=return_vo;
    	kanBanForm.action="/general/kanban/kanban.do?b_query=link";
		kanBanForm.submit();
    }
}
function printExcel(){
	var hashvo=new ParameterSet();
	hashvo.setValue("fieldsetid","P05"); 
	var request=new Request({method:'post',asynchronous:false,onSuccess:showFieldList,
		functionId:'2020050033'},hashvo);
}
function showFieldList(outparamters){
	var outName=outparamters.getValue("outName");
	window.location.target="_blank";
	outName=decode(outName)
	window.location.href="/servlet/vfsservlet?fromjavafolder=true&fileid="+outName;
}
function IsDigit(e){ 
	e=e?e:(window.event?window.event:null);
    var key = window.event?e.keyCode:e.which;
	key=parseInt(key);
	if(((key >= 48) && (key <= 57))||key==8){
		return true;
	}else{
		return false;
	}
} 
function checkInt(obj){
	var v=obj.value;
	var reg="1234567890";
	for(var i=0;i<v.length;i++){
		var char=v.charAt(i);
		if(reg.indexOf(char)==-1){
			alert("请输入数值!");
			obj.value="";
			obj.focus();
			break;
		}
	}
}
</script>
<html:form action="/general/kanban/kanban">
<%int i=0;%>
<html:hidden name="kanBanForm" property="hsearch"/>
<table border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" style="margin-top:-1;">
<tr><td align="center">
<table width="100%" border="0" cellspacing="0"  align="left" cellpadding="0">
	<tr>
		<td width="150" height="30" class="va">
		按
		<html:select name="kanBanForm" property="itemid" styleId="itemid" onchange="changeCodeValue();" style="width:120">
    		<html:optionsCollection property="itemlist" value="dataValue" label="dataName" />
 		</html:select> 
		</td>
		<td align="left" nowrap>
			<span id="textview" style="display:none;vertical-align:middle;">
				<html:text name="kanBanForm" property="searchtext" style="width:150px;" styleClass="text4 va"/>
				<input type='button' class="mybutton va" value='查询' onclick="searchAll();"/>
				<input type='button' class="mybutton va" value='高级' onclick="selectQ();"/>
			</span>
			<span id="fromnumview" style="display:none;vertical-align:middle;">
				从<html:text name="kanBanForm" onblur="checkInt(this);" onkeypress="return IsDigit(event);" property="fromnum" style="width:80px;" styleClass="text4 va"/>
				到<html:text name="kanBanForm" onblur="checkInt(this);" onkeypress="return IsDigit(event);" property="tonum" style="width:80px;" styleClass="text4 va"/>
				<input type='button' class="mybutton va" value='查询' onclick="searchAll();"/>
				<input type='button' class="mybutton va" value='高级' onclick="selectQ();"/>
			</span>
			<span id="fromdateview" style="display:none;vertical-align:middle;">
				从<input type="text" name="fromdate"  value="${kanBanForm.fromdate}"  extra="editor" style="width:80px;font-size:10pt;text-align:left" dropDown="dropDownDate">
				到<input type="text" name="todate" value="${kanBanForm.todate}" extra="editor" style="width:80px;font-size:10pt;text-align:left" dropDown="dropDownDate">
				<input type='button' class="mybutton va" value='查询' onclick="searchAll();"/>
				<input type='button' class="mybutton va" value='高级' onclick="selectQ();"/>
			</span>
			<span id="codeidview" style="display:none;vertical-align:middle;">
				<select name="codeid" id="codeid"  style="width:150;" class="va">
             	</select>
             	<input type='button' class="mybutton va" value='查询' onclick="searchAll();"/>
             	<input type='button' class="mybutton va" value='高级' onclick="selectQ();"/>
            </span>
            <span id="allbutton" style="vertical-align:middle;">
             	<input type='button' class="mybutton va" value='查询' onclick="searchAll();"/>
             	<input type='button' class="mybutton va" value='高级' onclick="selectQ();"/>
            </span>
		</td>
		<td align="right" height="30">&nbsp;&nbsp;
			排序指标<hrms:optioncollection name="kanBanForm" property="orderlist" collection="list1" />
			<html:select name="kanBanForm" property="orderid" onchange="sortItem();" style="width:100px;font-size:10pt;text-align:left">
				<html:options collection="list1" property="dataValue" labelProperty="dataName"/>
			</html:select>
			升降<hrms:optioncollection name="kanBanForm" property="desclist" collection="list2" />
			<html:select name="kanBanForm" property="descid" onchange="sortItem();" style="width:60px;font-size:10pt;text-align:left">
				<html:options collection="list2" property="dataValue" labelProperty="dataName"/>
			</html:select>
			<input type='button' class="mybutton"  value='组合排序' onclick="sortItems();"/>
			<input type="hidden" value="" id="sortitem" name="sortitem"/>
		</td>
	</tr>
</table>
</td></tr>
<tr><td>
<div class="fixedDiv2">
<table width="100%" border="0" cellspacing="0"  align="left" cellpadding="0">
	<tr class="headerTr">
		<hrms:priv func_id="111202"> 
		<td align="center" class="TableRow" style="border-left: none;border-right: none;border-top: none;" nowrap>
    		<input type="checkbox" name="selbox" onclick="batch_select_all(this);" title='<bean:message key="label.query.selectall"/>'>                        
	     </td>
	     </hrms:priv>
	     <td align="center"  class="TableRow" style="border-right: none;border-top: none;" nowrap>审核</td> 
	     <td align="center"  class="TableRow" style="border-right: none;border-top: none;" nowrap>回复</td> 
	     <td align="center"  class="TableRow" style="border-right: none;border-top: none;" nowrap>填写</td> 
	     <td align="center"  class="TableRow" style="border-right: none;border-top: none;" nowrap>修改</td> 
		<logic:iterate id="element" name="kanBanForm"  property="fieldlist" indexId="index">
			<logic:notEqual name="element" property="itemid" value="p0500">
			<logic:notEqual name="element" property="itemid" value="nbase">
			<logic:notEqual name="element" property="itemid" value="nbase_0">
			<logic:notEqual name="element" property="itemid" value="nbase_1">
			<logic:notEqual name="element" property="itemid" value="a0100">
			<logic:notEqual name="element" property="itemid" value="a0100_0">
			<logic:notEqual name="element" property="itemid" value="a0100_1">
			<td align="center" class="TableRow" style="border-right: none;border-top: none;" nowrap>
                 <bean:write name="element" property="itemdesc" filter="false"/>&nbsp;        
	       </td>
	       </logic:notEqual>
	       </logic:notEqual>
	       </logic:notEqual>
	       </logic:notEqual>
	       </logic:notEqual>
	       </logic:notEqual>
	       </logic:notEqual>
		</logic:iterate>
	</tr>
	<hrms:paginationdb id="element" name="kanBanForm" sql_str="kanBanForm.sqlstr" table="" 
	where_str="" columns="kanBanForm.cloums" 
	order_by="kanBanForm.orderby" page_id="pagination" pagerows="${kanBanForm.pagerows}">
	<bean:define id="nid" name='element' property='p0500'/>
    <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow">
          <%}
          else
          {%>
          <tr class="trDeep">
          <%
          }
          i++;          
          %>  
          <hrms:priv func_id="111202"> 
         <td align="center" class="RecordRow" style="border-left:none;border-right: none;border-top: none;" nowrap>
         <!-- 
         	<logic:equal name="element" property="a0100" value="<%=userView.getA0100()%>">
         	<logic:equal name="element" property="nbase" value="<%=userView.getDbname()%>">
    		</logic:equal>     
    		</logic:equal>&nbsp; 
    		 --> 
    		 <input type="checkbox" name="${nid}" value="${nid}">                     
		</td> 
		</hrms:priv>
		<td class="RecordRow" style="border-right: none;border-top: none;" nowrap>&nbsp;
			<logic:equal name="element" property="a0100_1" value="<%=userView.getA0100()%>">
         	<logic:equal name="element" property="nbase_1" value="<%=userView.getDbname()%>">
         	<logic:equal name="element" property="p0513" value="2">
         	<logic:notEqual name="element" property="p0502" value="">
    		<a href="javascript:editResPg('audit','${nid}')"><img src="/images/edit.gif" border=0></a>  
    		</logic:notEqual>   
    		</logic:equal>   
    		 </logic:equal>
    		</logic:equal>                             
	    </td>
	    <td class="RecordRow" style="border-right: none;border-top: none;" nowrap>&nbsp;
	   	 	<logic:equal name="element" property="p0513" value="2">
    		<a href="javascript:editResPg('reply','${nid}')"><img src="/images/edit.gif" border=0></a>
    		</logic:equal>                                
	    </td> 
	    <td class="RecordRow" style="border-right: none;border-top: none;" nowrap>&nbsp;
	    	<logic:equal name="element" property="a0100_0" value="<%=userView.getA0100()%>">
         	<logic:equal name="element" property="nbase_0" value="<%=userView.getDbname()%>">
         	<logic:equal name="element" property="p0513" value="2">
         	<logic:equal name="element" property="p0502" value="">
    		<a href="javascript:editResPg('fill','${nid}')"><img src="/images/edit.gif" border=0></a>  
    		</logic:equal>
    		</logic:equal>
    		</logic:equal>
    		</logic:equal>                                 
	    </td> 
	     <td class="RecordRow" style="border-right: none;border-top: none;" nowrap>&nbsp;
	     	<logic:equal name="element" property="a0100" value="<%=userView.getA0100()%>">
         	<logic:equal name="element" property="nbase" value="<%=userView.getDbname()%>">
         	<logic:equal name="element" property="p0513" value="2">
    		<a href="javascript:editResPg('update','${nid}')"><img src="/images/edit.gif" border=0></a>  
    		</logic:equal>
    		</logic:equal>
    		</logic:equal>                              
	    </td> 
    	<logic:iterate id="fielditem"  name="kanBanForm"  property="fieldlist" indexId="index">
    		<logic:notEqual name="fielditem" property="itemid" value="nbase">
			<logic:notEqual name="fielditem" property="itemid" value="nbase_0">
			<logic:notEqual name="fielditem" property="itemid" value="nbase_1">
			<logic:notEqual name="fielditem" property="itemid" value="a0100">
			<logic:notEqual name="fielditem" property="itemid" value="a0100_0">
			<logic:notEqual name="fielditem" property="itemid" value="a0100_1">
    		<logic:notEqual name="fielditem" property="itemid" value="p0500">
    		<logic:equal name="fielditem" property="codesetid" value="0">
    		<logic:equal name="fielditem" property="itemid" value="p0507">
    		<td class="RecordRow" style="border-right: none;border-top: none;" nowrap>
    			<bean:define id="p0507" name='element' property='p0507'/>
    			<logic:notEqual name="element" property="p0507" value="">
    			<logic:notEqual name="element" property="p0507" value="0">
    			<img width="${p0507}px" height="10" src='/images/board_bottom_1.gif' />${p0507}%	
    			</logic:notEqual>
    			</logic:notEqual>
    			<logic:equal name="element" property="p0507" value="">
    			&nbsp;	
    			</logic:equal> 
    			<logic:equal name="element" property="p0507" value="0">
    			&nbsp;	
    			</logic:equal>               
	      	</td> 
	      	</logic:equal>
	      	<logic:notEqual name="fielditem" property="itemid" value="p0507">
	      	<logic:notEqual name="fielditem" property="itemtype" value="M">
	      	<td  align="left" style="border-right: none;border-top: none;" class="RecordRow" nowrap>
	      		<bean:write name="element" property="${fielditem.itemid}" filter="false"/>&nbsp;             
	      	</td>
	      	</logic:notEqual>
	      	<logic:equal name="fielditem" property="itemtype" value="M">
	      	<logic:notEqual name="fielditem" property="itemid" value="p0509">
	      	<td  align="left" class="RecordRow" style="border-right: none;border-top: none;"  onmouseover="viewRemark('${nid}','${fielditem.itemid}');"   onmouseout="UnTip();" nowrap>
	      		<bean:write name="element" property="${fielditem.itemid}" filter="false"/>&nbsp;  
	      	</td>
	      	</logic:notEqual>  
	      	<logic:equal name="fielditem" property="itemid" value="p0509">
	      	<td  align="center" class="RecordRow" style="border-right: none;border-top: none;"  onmouseover="viewRemark('${nid}','${fielditem.itemid}');"   onmouseout="UnTip();" nowrap>
	      		内容          
	      	</td>
	      </logic:equal>          
	      </logic:equal>
	      </logic:notEqual>
	      </logic:equal>
	      <logic:notEqual name="fielditem" property="codesetid" value="0">
	      	<logic:notEqual name="fielditem" property="itemid" value="p0513">
    		<td align="left" class="RecordRow" style="border-right: none;border-top: none;" nowrap>
          	   <hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
          	   <bean:write name="codeitem" property="codename" />&nbsp;                                                 
	      	</td> 
	      	</logic:notEqual>
	      	<logic:equal name="fielditem" property="itemid" value="p0513">
    		<td align="center" class="RecordRow" style="border-right: none;border-top: none;" nowrap>
          	   <hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
          	   <bean:write name="codeitem" property="codename" />&nbsp;                                                 
	      	</td> 
	      	</logic:equal>
	      </logic:notEqual>
	      </logic:notEqual>
	      </logic:notEqual>
	      </logic:notEqual>
	      </logic:notEqual>
	      </logic:notEqual>
	      </logic:notEqual>
	      </logic:notEqual>
    	</logic:iterate>
    </tr>
    </hrms:paginationdb>      
</table>
</div>
</td>
</tr>
<tr>
	<td>
		<div class="fixedDiv3 common_border_color" style="border: 1px solid;border-top: none;">
		<table width="100%"  align="center">
				<tr>
				    <td valign="bottom" class="tdFontcolor">
				            <bean:message key="label.page.serial"/>
							<bean:write name="pagination" property="current" filter="true" />
							<bean:message key="label.page.sum"/>
							<bean:write name="pagination" property="count" filter="true" />
							<bean:message key="label.page.row"/>
							<bean:write name="pagination" property="pages" filter="true" />
							<bean:message key="label.page.page"/>
					</td>
			               <td  align="right" nowrap class="tdFontcolor">
				          <p align="right"><hrms:paginationdblink name="kanBanForm" property="pagination" nameId="kanBanForm" scope="page">
						</hrms:paginationdblink>
					</td>
				</tr>
		</table>
		</div>
	</td>
</tr>
<tr>
  <td height="35px;" align="center">
     <hrms:priv func_id="111201"> 
			<input type="button" value="新增" class="mybutton" onclick="addKanban();">
		</hrms:priv>
		<hrms:priv func_id="111202"> 
			<input type="button" value="删除" class="mybutton" onclick="delResPg();">
		</hrms:priv>
		<input type="button" value="输出EXCEL" class="mybutton" onclick="printExcel();">
			
  </td>
</tr>
</table>
</html:form>
<script language="javascript">
changeCodeValue();
</script>

<script>
if(parent.myNewBody!=null)
 {
    parent.myNewBody.cols="*,0"
  }
  function sortItems() {
  	document.getElementsByName("orderid")[0].options[0].selected=true;
	var thecodeurl ="/general/kanban/kanban.do?b_query_order=link";
	//thecodeurl+="&model=1&sortitem="+getEncodeStr(""); 
	var dw=510,dh=420,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no;");
              //alert(return_vo);
	if(return_vo!=null&&return_vo!="not"){
		document.getElementById("sortitem").value=return_vo;
    	kanBanForm.action="/general/kanban/kanban.do?b_query=link";
		kanBanForm.submit();
    }
}
</script>