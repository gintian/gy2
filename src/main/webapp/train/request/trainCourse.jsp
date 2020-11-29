<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="com.hjsj.hrms.actionform.train.request.CourseTrainForm"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.hjsj.hrms.businessobject.train.TrainClassBo"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%CourseTrainForm courseTrainForm = (CourseTrainForm)session.getAttribute("courseTrainForm");
ArrayList codelist = (ArrayList)courseTrainForm.getFormHM().get("codelist");
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+(80==(request.getServerPort())?"":(":"+request.getServerPort()))+path+"/";
%>
<script language="JavaScript" src="/train/request/TrainData.js"></script>
<script type="text/javascript" src="../../js/hjsjUrlEncode.js"></script>
<script language="JavaScript">
function delTrainCourse(){
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
     	if(!confirm("<bean:message key='train.b_plan.request.delcourse'/>")){
			return false;
		}
     	var hashvo=new ParameterSet();
     	hashvo.setValue("tablename","r41");
		hashvo.setValue("itemid","r4101");
		hashvo.setValue("keyid",nid);
		var request=new Request({method:'post',asynchronous:false,onSuccess:checkDelOk,functionId:'2020040005'},hashvo);	
     }else{
     	alert("<bean:message key='train.b_plan.request.select.delcourse'/>");
     	return false;
     }
}
function checkDelOk(outparamters){
	var flag = outparamters.getValue("flag");
	if(flag=='ok'){
		alert("<bean:message key='train.b_plan.request.ok'/>");
		courseTrainForm.action="/train/request/trainCourse.do?b_query=link&r3127=${param.r3127}&r3101=${param.r3101}&flag=${param.flag}";
		courseTrainForm.submit();
	}
}
</script>
<style>
 .ListTable td{
   padding:0px,5px,0px,5px;
 }
 .fixedHeaderTr{
    margin-top: -10;
    padding-top: 10px;
}
</style>
<html:form action="/train/request/trainCourse">
<%int i=0;%>
<table border="0" cellspacing="0"  align="left" cellpadding="0">
<tr><td>
<div class="fixedHeaderTr" style="background-color: #FFFFFF;">
<table  width="400" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td style="padding: 3px">
		<logic:notEqual name="courseTrainForm"  property="r3127" value="06">
			<hrms:priv func_id="3233070">
				<input type="button" value="<bean:message key='button.new.add'/>" class="mybutton" onclick="addCourse('${courseTrainForm.r3101}','${courseTrainForm.r3127}','${param.r3115}','${param.r3116}');">
			</hrms:priv>
			<hrms:priv func_id="3233071">
			<logic:notEqual name="courseTrainForm"  property="num" value="0">
				<input type="button" value="<bean:message key='lable.tz_template.delete'/>" class="mybutton" onclick="delTrainCourse();">
			</logic:notEqual>
			</hrms:priv>
			 
		<!--	<hrms:menubar menu="menu1" id="menubar1">	
			<logic:notEqual name="courseTrainForm"  property="r3127" value="06">				
				<hrms:menuitem name="mitem1" label="button.new.add" function_id="3233070" icon="" url="addCourse('${courseTrainForm.r3101}','${courseTrainForm.r3127}','${param.r3115}','${param.r3116}');"  />
			</logic:notEqual>
			<logic:notEqual name="courseTrainForm"  property="num" value="0">
				<hrms:menuitem name="mitem2" label="lable.tz_template.delete" function_id="3233071" icon="" url="delTrainCourse();"/>
			</logic:notEqual>				
			</hrms:menubar>-->
		</logic:notEqual>
		</td>
	</tr>
</table>
</div>
</td></tr>
<tr><td>
<table width="100%" border="0" cellspacing="0"  align="left" cellpadding="0" class="ListTable">
	<thead>
	<tr>
		<logic:iterate id="element" name="courseTrainForm"  property="r41list" indexId="index">
			<logic:equal name="element" property="itemid" value="r4101">
    		<td align="center" class="TableRow" nowrap>
    		<input type="checkbox" name="selbox" onclick="batch_select_all(this);" title='<bean:message key="label.query.selectall"/>'>
    		</td> 
	      	</logic:equal>
	      	<logic:notEqual name="element" property="itemid" value="r4101">
			<td align="center" class="TableRow" nowrap>
                 <bean:write  name="element" property="itemdesc" filter="true"/>
	       </td> 
	       </logic:notEqual>
		</logic:iterate>
		<logic:notEqual name="courseTrainForm"  property="r3127" value="06">
		   <td align="center" class="TableRow" nowrap><bean:message key='system.infor.oper'/></td> 
		</logic:notEqual>
		<logic:equal name="courseTrainForm"  property="r3127" value="04">
		<hrms:priv func_id="3233073">
		<logic:notEmpty name="courseTrainForm" property="pushitem">
			<td align="center" class="TableRow" nowrap><bean:message key='button.propelling'/></td> 
		</logic:notEmpty>
		</hrms:priv>
		</logic:equal>
	</tr>
	</thead>
	<hrms:paginationdb id="element" name="courseTrainForm" sql_str="courseTrainForm.sql" table="" 
	where_str="courseTrainForm.wherestr" columns="courseTrainForm.columns" 
	order_by="order by r4101" page_id="pagination" pagerows="${courseTrainForm.pagerows}">
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
    	<bean:define id="nid" name='element' property='r4101'/>
    	<% String r4101 = SafeCode.encode(PubFunc.encrypt(nid.toString())); %>
    	<logic:iterate id="fielditem"  name="courseTrainForm"  property="r41list" indexId="index">
    	 <logic:equal name="fielditem" property="itemid" value="r4101">
    		<td align="center" class="RecordRow" nowrap>
    			<input type="checkbox" name="<%=r4101 %>" value="<%=r4101 %>">                                 
	      	</td> 
	      </logic:equal>
	      
	      <logic:equal name="fielditem" property="itemid" value="r4105">
	      	<td align="left" class="RecordRow" nowrap>
            	<bean:write name="element" property="${fielditem.itemid}" filter="false"/>&nbsp;    
            </td>
          </logic:equal>
               		
          <logic:equal name="fielditem" property="itemid" value="r4106">
          	<td align="left" class="RecordRow" nowrap>
           		<bean:write name="element" property="${fielditem.itemid}" filter="false"/>&nbsp;    
           	</td>
          </logic:equal>
               		
          <logic:equal name="fielditem" property="itemid" value="r4114">
          	<td align="left" class="RecordRow" nowrap>
           		<bean:write name="element" property="${fielditem.itemid}" filter="false"/>&nbsp;    
           	</td>
          </logic:equal>
	      
	      <logic:notEqual name="fielditem" property="itemid" value="r4101">
	      <logic:notEqual name="fielditem" property="itemid" value="r4105">
          <logic:notEqual name="fielditem" property="itemid" value="r4106">
          <logic:notEqual name="fielditem" property="itemid" value="r4114">
    		<td align="left" class="RecordRow" nowrap>
               <logic:notEqual name="fielditem" property="codesetid" value="0">
          	   		<hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
          	   		<bean:write name="codeitem" property="codename" />&nbsp;                    
               </logic:notEqual>
               
               <logic:equal name="fielditem" property="codesetid" value="0">
               		<bean:write name="element" property="${fielditem.itemid}" filter="false"/>&nbsp;     
               </logic:equal>                              
	      </td> 
	      </logic:notEqual>
          </logic:notEqual>
          </logic:notEqual>   
	      </logic:notEqual>
    	</logic:iterate>
    	<logic:notEqual name="courseTrainForm"  property="r3127" value="06">	
    	<td align="center" class="RecordRow" nowrap>
    		<a href='javascript:editCourse("<%=r4101 %>","${courseTrainForm.r3101}","${courseTrainForm.r3127}","${param.r3115}","${param.r3116}")'><img src="/images/edit.gif" border=0></a>                                
	    </td> 
	    </logic:notEqual>
	    <logic:equal name="courseTrainForm"  property="r3127" value="04">
		    <hrms:priv func_id="3233073" module_id="39">
			    <logic:notEmpty name="courseTrainForm" property="pushitem">
				    <td align="center" class="RecordRow" nowrap>
			    		<hrms:traincoursepushtag projectid="<%=r4101 %>" item="${courseTrainForm.pushitem}">
							<a href='javascript:pushCourse("${courseTrainForm.r3101}","${r5000 }","<%=basePath %>");'><img src="/images/edit.gif" border=0 alt="推送"/></a> 
						</hrms:traincoursepushtag>
					</td> 
				</logic:notEmpty>
			</hrms:priv>
		</logic:equal>
    </tr>
    </hrms:paginationdb>      
</table>
</td></tr>
<tr><td>
<table  width="100%" border="0" class="RecordRowP">
	<tr>
		<td valign="bottom" class="tdFontcolor" nowrap>
		<hrms:paginationtag name="courseTrainForm" pagerows="${courseTrainForm.pagerows}" property="pagination" scope="page" refresh="true"></hrms:paginationtag>
		</td>
	    <td  align="right" nowrap class="tdFontcolor" nowrap>
		     <hrms:paginationdblink name="courseTrainForm" property="pagination" nameId="courseTrainForm" scope="page">
			</hrms:paginationdblink>
		</td>
	</tr>
</table>
</td></tr>
</table>
</html:form>
<%TrainClassBo.setcodesetid(codelist); %>
<script language='javascript' >			
	parent.parent.ril_body1.setSecondPage(2)			
</script>