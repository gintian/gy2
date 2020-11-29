<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="com.hjsj.hrms.businessobject.train.resource.TrainResourceBo"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/train/trainCosts/planTrain.js"></script>
<script language="JavaScript">
function delTrainCosts(){
	var tablevos=document.getElementsByTagName("input");
	var nid="";
	for(var i=0;i<tablevos.length;i++){
	     if(tablevos[i].type=="checkbox"&&tablevos[i].name!="selbox"){
	     	if(tablevos[i].checked){
	     		nid += tablevos[i].value+",";
	     	}
		 }
     }
     if(nid!=null&&nid.length>0){
		if(!confirm("<bean:message key='train.b_plan.request.del.traincosts'/>")){
			return false;
		}
     	var hashvo=new ParameterSet();
     	hashvo.setValue("tablename","r45");
		hashvo.setValue("itemid","r4501");
		hashvo.setValue("keyid",nid);
		var request=new Request({method:'post',asynchronous:false,onSuccess:checkDelOk,functionId:'2020040005'},hashvo);	
     }else{
     	alert("<bean:message key='train.b_plan.request.select.traincosts'/>");
     	return false;
     }
}
function checkDelOk(outparamters){
	var flag = outparamters.getValue("flag");
	if(flag=='ok'){
		//alert("<bean:message key='train.b_plan.request.ok'/>");
		trainCostsForm.action="/train/trainCosts/trainCosts.do?b_query=link&r2501=${param.r2501}&b0110=${param.b0110}&e0122=${param.b0110}&flag=${param.flag}";
		trainCostsForm.submit();
	}
}
function setColcul(){
	var thecodeurl="/general/inform/emp/batch/calculation.do?b_query=link&unit_type=5&setname=R45&a_code=${param.r2501}&infor=5&flag=ie";
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:430px; dialogHeight:400px;resizable:no;center:yes;scroll:no;status:no");
    if(return_vo!=null){
    	trainCostsForm.action="/train/trainCosts/trainCosts.do?b_query=link&r2501=${param.r2501}&b0110=${param.b0110}&e0122=${param.b0110}&flag=${param.flag}";
		trainCostsForm.submit();
    }
}
</script>
<html:form action="/train/trainCosts/trainCosts.do?b_query=link&r2501=${param.r2501}&b0110=${param.b0110}&e0122=${param.b0110}&flag=${param.flag}">
<%int i=0;%>
<table border="0" cellspacing="0"  align="left" cellpadding="0">
<tr><td>
<table  width="400" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td style="padding-bottom: 5px;"> 
			<hrms:priv func_id="3235030">
			<input type="button" value="<bean:message key='button.new.add'/>" class="mybutton" onclick='addRelaCost("${param.b0110}","${param.e0122}","${param.r2501}");'>
			</hrms:priv>
			<hrms:priv func_id="3235031">
			<input type="button" value="<bean:message key='button.delete'/>" class="mybutton" onclick="delTrainCosts();">
			</hrms:priv>
			<hrms:priv func_id="3235032">
			<input type="button" value="<bean:message key='button.computer'/>" class="mybutton" onclick="setColcul();">
			</hrms:priv>
			 <!--
			<hrms:menubar menu="menu1" id="menubar1">					
				<hrms:menuitem name="mitem1" label="button.new.add" function_id="3235030" icon="" url="addRelaCost('${param.b0110}','${param.e0122}','${param.r2501}');"  />
				<hrms:menuitem name="mitem2" label="button.delete" function_id="3235031" icon="" url="delTrainCosts();"/>
				<hrms:menuitem name="mitem3" label="button.computer" function_id="3235032" icon="" url="setColcul();"/>					
			</hrms:menubar>
			-->
		</td>
	</tr>
</table>
</td></tr>
<tr><td>
<table width="100%" border="0" cellspacing="0"  align="left" cellpadding="0" class="ListTable1">
	<thead>
	<tr>
		<logic:iterate id="element" name="trainCostsForm"  property="setlist" indexId="index">
			<logic:equal name="element" property="itemid" value="r4501">
			<td align="center" width="30" class="TableRow" nowrap>
                 <input type="checkbox" name="selbox" onclick="batch_select_all(this);" title='<bean:message key="label.query.selectall"/>'>
	       </td> 
			</logic:equal>
			<logic:notEqual name="element" property="itemid" value="r4501">
			<td align="center" class="TableRow" nowrap>
                 <bean:write  name="element" property="itemdesc" filter="true"/>
	       </td> 
	       </logic:notEqual>
		</logic:iterate>
		<td align="center" width="40" class="TableRow" nowrap><bean:message key='system.infor.oper'/></td> 
	</tr>
	</thead>
	<hrms:paginationdb id="element" name="trainCostsForm" sql_str="trainCostsForm.sql" table="" 
	where_str="trainCostsForm.wherestr" columns="trainCostsForm.columns" 
	order_by="order by r4501" page_id="pagination" pagerows="${trainCostsForm.pagerows}">
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
          <bean:define id="nid" name='element' property='r4501'/>
          <%String r4501 = SafeCode.encode(PubFunc.encrypt(nid.toString())); %>
    	<logic:iterate id="fielditem"  name="trainCostsForm"  property="setlist" indexId="index">
    		<logic:equal name="fielditem" property="itemid" value="r4501">
    		<td align="center" class="RecordRow" nowrap>
    			<input type="checkbox" name="<%=r4501 %>" value="<%=r4501 %>">                                 
	      	</td> 
	      </logic:equal>
	      <logic:notEqual name="fielditem" property="itemid" value="r4501">
    		<td align="left" class="RecordRow" nowrap>
               <logic:notEqual name="fielditem" property="codesetid" value="0">
          	   		<hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
          	   		<bean:write name="codeitem" property="codename" />&nbsp;                    
               </logic:notEqual>
               <logic:equal name="fielditem" property="codesetid" value="0">
               		<logic:equal name="fielditem" property="itemtype" value="M">
						<bean:define id="beizhu" name="element" property="${fielditem.itemid}"/>
						<%
						  String remark = TrainResourceBo.substr(beizhu.toString());
						%>
						<%=remark %>
					</logic:equal>
					<logic:notEqual name="fielditem" property="itemtype" value="M">
                   		<bean:write name="element" property="${fielditem.itemid}" filter="false"/>&nbsp;
                   </logic:notEqual>                 
               </logic:equal>                              
	      </td> 
	      </logic:notEqual>
    	</logic:iterate>
    	<td align="center" class="RecordRow" nowrap>
    		<a href='javascript:editRelaCost("<%=r4501 %>","${param.r2501}","${param.b0110}","${param.e0122}");'><img src="/images/edit.gif" border=0></a>                                
	    </td> 
    </tr>
    </hrms:paginationdb>      
</table>
</td></tr>
<tr><td>
<table  width="100%" border="0"  class="RecordRowP" cellpadding="0" cellspacing="0">
	<tr>
		<td valign="bottom" class="tdFontcolor">
		&nbsp;<hrms:paginationtag name="trainCostsForm" pagerows="${trainCostsForm.pagerows}" property="pagination" scope="page" refresh="true"></hrms:paginationtag>
		</td>
	    <td align="right" nowrap class="tdFontcolor">
		     <p align="right"><hrms:paginationdblink name="trainCostsForm" property="pagination" nameId="trainCostsForm" scope="page">
			</hrms:paginationdblink>&nbsp;</p>
		</td>
	</tr>
</table>
</td></tr>
</table>
</html:form>