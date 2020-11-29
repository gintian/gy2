<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/common.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/js/wz_tooltip.js"></script>
<script language="JavaScript" src="/train/traincourse/traindata.js"></script>

<style>
.myfixedDiv
{ 
	overflow:auto; 
	height:expression(document.body.clientHeight-160);
	width:expression(document.body.clientWidth-10); 
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; 
}
</style>
<script language="JavaScript">
function introdRecord(){
	
	var tablevos=document.getElementsByTagName("input");
	var nid="";
	for(var i=0;i<tablevos.length;i++){
	     if(tablevos[i].type=="checkbox" && tablevos[i].name!="selbox"){
	     	if(tablevos[i].checked){
	     		nid += tablevos[i].value+",";
	     	}
		 }
     }
     if(nid!=null&&nid.length>0){
     	if(!confirm("<bean:message key='train.b_plan.addtrain'/>"))
			return false;
     	var hashvo=new ParameterSet();
		hashvo.setValue("r3101",nid);
		hashvo.setValue("r2501","${trainCourseForm.r2501}");
		hashvo.setValue("checkflag","introd");
		var request=new Request({method:'post',asynchronous:false,onSuccess:checkDelOk,functionId:'2020050011'},hashvo);	
     }else{
     	alert("<bean:message key='train.b_plan.selecttrain'/>");
     	return false;
     }
}
function checkDelOk(outparamters){
	var flag = outparamters.getValue("flag");
	if(flag=='ok'){
		var exper = outparamters.getValue("exper");
		if(exper!=null&&exper.length>10){
			alert(getDecodeStr(exper));
		}
		trainCourseForm.action="/train/b_plan/introdTrain.do?b_query=link&r2501=${trainCourseForm.r2501}&model=${trainCourseForm.model}&b0110=${trainCourseForm.b0110}&e0122=${trainCourseForm.e0122}";
		trainCourseForm.submit();
		window.close();
	}
}
function outContent(r3101,contentid){
	var hashvo=new ParameterSet();
	hashvo.setValue("contentid",contentid);	
	hashvo.setValue("wherestr",getEncodeStr("from r31 where r3101='"+r3101+"'"));
   	var request=new Request({method:'post',asynchronous:true,onSuccess:viewContent,functionId:'2020050048'},hashvo);
}
function viewContent(outparamters){
	var content=outparamters.getValue("content");
	Tip(getDecodeStr(content));
}

function openw(url){
	var ww = 800;//window.screen.width-5;
	var hh = 600;//window.screen.height - 40;
	window.open(url, "_new","toolbar=no,location=no,directories=no,menubar=no,scrollbars=yes,resizable=no,status=no,top=0,left=0,width="+ww+",height="+hh);
}
function eventDesc(id){
	var wherestr = " from r31 where r3101='"+id+"'";
	var thecodeurl ="/train/b_plan/introdTrain.do?b_event=link`msg=1`id=r3117`wherestr="+getEncodeStr(wherestr);
	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
    //var return_vo= window.showModalDialog(iframe_url,"", "dialogWidth:800px; dialogHeight:500px;resizable:yes;center:yes;scroll:yes;status:yes");
    //var return_vo= window.showModelessDialog(iframe_url,"", "dialogWidth:800px; dialogHeight:500px;resizable:yes;center:yes;scroll:yes;status:yes");
	openw(iframe_url);     
}
</script>
<html:form action="/train/b_plan/introdTrain">
<%int i=0;%>
<div class="myfixedDiv" style="border-top: none;">
<table width="100%" border="0" cellspacing="0"  align="left" cellpadding="0" class="ListTable1">
	<tr class="fixedHeaderTr">
		<logic:iterate id="element" name="trainCourseForm"  property="itemlist" indexId="index">
			<logic:equal name="element" property="itemid" value="r3101">
    			<td align="center" class="TableRow" style="border-left: none;" nowrap>
    				<input type="checkbox" name="selbox" onclick="batch_select_all(this);" title='<bean:message key="label.query.selectall"/>'>                        
	      		</td> 
	     	</logic:equal>
	     	<logic:notEqual name="element" property="itemid" value="r3101">
			<td align="center" class="TableRow" nowrap>
                 <bean:write  name="element" property="itemdesc" filter="true"/>
	       </td> 
	       </logic:notEqual>
		</logic:iterate>
	</tr>
	<hrms:paginationdb id="element" name="trainCourseForm" sql_str="trainCourseForm.sql" table="" 
	where_str="trainCourseForm.wherestr" columns="trainCourseForm.columns" 
	order_by="order by r3101" page_id="pagination" pagerows="10">
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
    	<logic:iterate id="fielditem"  name="trainCourseForm"  property="itemlist" indexId="index">
    		<bean:define id="nid" name='element' property='r3101'/>
    		<%String r3101 = SafeCode.encode(PubFunc.encrypt(nid.toString())); %>
    		<logic:equal name="fielditem" property="itemid" value="r3101">
    		<td align="center" class="RecordRow" style="border-left: none;" nowrap>
    			<input type="checkbox" name="<%=r3101 %>" value="<%=r3101 %>">                                 
	      	</td> 
	      </logic:equal>
	      <logic:notEqual name="fielditem" property="itemid" value="r3101">
    		<logic:notEqual name="fielditem" property="codesetid" value="0">
               		<td align="left" class="RecordRow" nowrap>
          	   		<hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
          	   		<bean:write name="codeitem" property="codename" />&nbsp;   
          	   		</td>                 
               </logic:notEqual>
               <logic:equal name="fielditem" property="codesetid" value="0">
					<logic:equal name="fielditem" property="itemid" value="r3117">
					<td align="center" class="RecordRow" nowrap>
               			&nbsp; <a href="###" onclick='eventDesc("<%=r3101 %>");'> 
	          	   		<img src="/images/view.gif" alt="浏览" border="0"></a>&nbsp;
	            	 </td>
               		</logic:equal>
               		<logic:notEqual name="fielditem" property="itemid" value="r3117">
	               	   <logic:equal name="fielditem" property="itemtype" value="M">
		               	   <td align="left" class="RecordRow" onmouseout="UnTip();" onmouseover="outContent('<%=r3101 %>','${fielditem.itemid}');" nowrap>
		                   	<bean:write name="element" property="${fielditem.itemid}" filter="false"/>&nbsp;     
		                   </td>
	                   </logic:equal>   
	                   <logic:notEqual name="fielditem" property="itemtype" value="M">
		                   <td align="left" class="RecordRow" nowrap>
		                  	 <bean:write name="element" property="${fielditem.itemid}" filter="false"/>&nbsp;  
		                   </td>   
	                   </logic:notEqual> 
                   </logic:notEqual>   
               </logic:equal>                              
	      </logic:notEqual>
    	</logic:iterate>
    </tr>
    </hrms:paginationdb>      
</table>
</div>
<table   width="100%" border="0" class="RecordRowP">
	<tr>
		<td valign="bottom" class="tdFontcolor"><bean:message key='reportanalyse.di'/>
			<bean:write name="pagination" property="current" filter="true" />
					<bean:message key='hmuster.label.paper'/>
					<bean:message key='hmuster.label.total'/>
			<bean:write name="pagination" property="count" filter="true" />
					<bean:message key='label.item'/>
					<bean:message key='hmuster.label.total'/>
			<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key='hmuster.label.paper'/>
		</td>
	    <td  align="right" nowrap class="tdFontcolor">
		     <p align="right"><hrms:paginationdblink name="trainCourseForm" property="pagination" nameId="trainCourseForm" scope="page">
			</hrms:paginationdblink>
		</td>
	</tr>
</table>
<table  width="100%" border="0" align="center">
	<tr>
		<td align="center">
			<input type="button" value="<bean:message key='kq.formula.true'/>" class="mybutton" onclick="introdRecord();">
		</td>
	</tr>
</table>		
</html:form>
