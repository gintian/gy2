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
<script language="JavaScript" src="/train/request/TrainData.js"></script>
<script type="text/javascript" src="../../js/hjsjUrlEncode.js"></script>
<%CourseTrainForm courseTrainForm = (CourseTrainForm)session.getAttribute("courseTrainForm");
ArrayList codelist = (ArrayList)courseTrainForm.getFormHM().get("codelist");
%>
<style>
.TableRowtop {
	background-position : center left;
	font-size: 12px;  
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 0pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	height:22px; 
	font-weight: bold;
	background-color:#f4f7f7;	
	valign:middle;
	
	position:relative; 
	top:expression(this.offsetParent.scrollTop);
	z-index: 15;
}
.TableRow1 {
	background: #FFFFFF;
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 0pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 0pt solid;
	font-size: 12px;
	font-weight:normal;
	height:22px;
}
  .fixedHeaderTr{
	position:relative; 
    left:expression(this.offsetParent.scrollLeft);
    top:expression(this.offsetParent.scrollTop);
    z-index: 20;
    margin-top: -10px;
    padding-top: 10px;
	}
  .left{
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 0pt solid;
	font-size: 12px;
	border-collapse:collapse;
	background-color: #ffffff; 
	height:22px;
	
    position:relative; 
    left:expression(this.offsetParent.scrollLeft);
    z-index: 10;
    }
    .top_left{
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 0pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	font-size: 12px;
	border-collapse:collapse;
	background-color:#f4f7f7;; 
	height:22px;
	
    position:relative; 
    left:expression(this.offsetParent.scrollLeft);
    top:expression(this.offsetParent.scrollTop);
    z-index: 20;
    }
</style>
<script language="JavaScript">
function delTrainStu(){
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
     	if(!confirm("<bean:message key='train.b_plan.request.del.traintable'/>")){
			return false;
		}
     	var hashvo=new ParameterSet();
     	hashvo.setValue("tablename","r37");
		hashvo.setValue("itemid","r3701");
		hashvo.setValue("keyid",nid);
		var request=new Request({method:'post',asynchronous:false,onSuccess:checkDelOk,functionId:'2020040005'},hashvo);	
     }else{
     	alert("<bean:message key='train.b_plan.request.selectdel.traintable'/>");
     	return false;
     }
}
function checkDelOk(outparamters){
	var flag = outparamters.getValue("flag");
	if(flag=='ok'){
		//alert("<bean:message key='train.b_plan.request.ok'/>");
		courseTrainForm.action="/train/request/trainStu.do?b_query=link&r3127=${param.r3127}&r3101=${param.r3101}&flag=${param.flag}";
		courseTrainForm.submit();
	}
}
</script>
<html:form action="/train/request/trainStu">
<table border="0" cellspacing="0"  align="left" cellpadding="0">
<tr style="background-color: #FFFFFF;"><td>
<div class="fixedHeaderTr" style="background-color: #FFFFFF;">
<table  width="400" border="0" cellspacing="0" cellpadding="0">
	<tr style="padding: 3px;">
		<td>
			<hrms:priv func_id="3233090">
				<logic:equal name="courseTrainForm"  property="r3127" value="04">
					<input type="button" value="<bean:message key='button.new.add'/>" class="mybutton" onclick="addResPg('${courseTrainForm.r3101}','${courseTrainForm.r3127}')">
				</logic:equal>
			</hrms:priv>
			<hrms:priv func_id="3233091">
				<logic:notEqual name="courseTrainForm"  property="r3127" value="06">
				<logic:notEqual name="courseTrainForm"  property="num" value="0">
					<input type="button" value="<bean:message key='button.setfield.delfield'/>" class="mybutton" onclick="delTrainStu();">
				</logic:notEqual>
				</logic:notEqual>
			</hrms:priv>  
	<!-- 		<hrms:menubar menu="menu1" id="menubar1">	
			  <logic:equal name="courseTrainForm"  property="r3127" value="04">				
				<hrms:menuitem name="mitem1" label="button.new.add" function_id="3233090" icon="" url="addResPg('${courseTrainForm.r3101}','${courseTrainForm.r3127}');"  />
			  </logic:equal>
			  <logic:notEqual name="courseTrainForm"  property="num" value="0">
				<hrms:menuitem name="mitem2" label="button.setfield.delfield" function_id="3233091" icon="" url="delTrainStu();"  />
			  </logic:notEqual>				
			</hrms:menubar>-->
		</td>
	</tr>
</table>
</div>
</td></tr>
<tr><td>
<table width="100%" border="0" cellspacing="0"  align="left" cellpadding="0" style="border-collapse: collapse;">
	<thead>
	<tr><!--  style="position:relative;top:expression(this.offsetParent.scrollTop-1);" -->
		<logic:iterate id="element" name="courseTrainForm"  property="setlist" indexId="index">
			<logic:equal name="element" property="itemid" value="r3701">
				<td align="center" width="30" class="TableRow" nowrap>
	                 <input type="checkbox" name="selbox" onclick="batch_select_all(this);" title='<bean:message key="label.query.selectall"/>'>
		        </td> 
			</logic:equal>
			<logic:notEqual name="element" property="itemid" value="r3701">
				<td align="center" width="200" class="TableRow" nowrap>
	                 <bean:write  name="element" property="itemdesc" filter="true"/>
		        </td> 
	        </logic:notEqual>
		</logic:iterate>
		<logic:notEqual name="courseTrainForm"  property="r3127" value="06">
		<td align="center" width="40" class="TableRow" nowrap><bean:message key='system.infor.oper'/></td> 
		</logic:notEqual>
	</tr>
	</thead>
	<hrms:paginationdb id="element" name="courseTrainForm" sql_str="courseTrainForm.sql" table="" 
	where_str="courseTrainForm.wherestr" columns="courseTrainForm.columns" 
	order_by="order by r3701" page_id="pagination" pagerows="${courseTrainForm.pagerows}">
	<bean:define id="nid" name='element' property='r3701'/>
	<%String r3701 = SafeCode.encode(PubFunc.encrypt(nid.toString())); %>
   	 <tr>
    	<logic:iterate id="fielditem"  name="courseTrainForm"  property="setlist" indexId="index">
    		<logic:equal name="fielditem" property="itemid" value="r3701">
    		<td align="center" class="RecordRow" nowrap>
    			
    			<input type="checkbox" name="<%=r3701 %>" value="<%=r3701 %>">                                 
	      	</td> 
	      </logic:equal>
	      <logic:notEqual name="fielditem" property="itemid" value="r3701">
    		<td align="left" class="RecordRow" nowrap>
    			<logic:equal name="fielditem" property="itemid" value="r3702">
                   	<bean:write name="element" property="${fielditem.itemid}" filter="false"/>
                 </logic:equal>
               <logic:notEqual name="fielditem" property="itemid" value="r3702">
	               <logic:notEqual name="fielditem" property="codesetid" value="0">
	          	   		<hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
	          	   		<bean:write name="codeitem" property="codename" />&nbsp;                    
	               </logic:notEqual>
	               <logic:equal name="fielditem" property="codesetid" value="0">
	                 <logic:equal name="fielditem" property="itemid" value="r3704">
	                   <a href="javascript:editMemoFild('<%=r3701 %>','r3704','${courseTrainForm.r3101}','${courseTrainForm.r3127}');">
	                   	<bean:write name="element" property="${fielditem.itemid}" filter="false"/>&nbsp;
	                   </a>  
	                 </logic:equal>
	                 <logic:notEqual name="fielditem" property="itemid" value="r3704">
	                   <bean:write name="element" property="${fielditem.itemid}" filter="false"/>&nbsp;  
	                 </logic:notEqual>               
	               </logic:equal> 
               </logic:notEqual>                             
	      </td> 
	      </logic:notEqual>
    	</logic:iterate>
    	<logic:notEqual name="courseTrainForm"  property="r3127" value="06">    	 
    	<td align="center" class="RecordRow" nowrap>
    		<a href="javascript:editResPg('<%=r3701 %>','${courseTrainForm.r3101}','${courseTrainForm.r3127}')"><img src="/images/edit.gif" border=0></a>
	    </td> 
	    </logic:notEqual>
    </tr>
    </hrms:paginationdb>      
</table>
</td></tr>
<tr><td>
<table  width="100%" border="0" class="RecordRowP" style="margin-top: 0px;">
	<tr>
		<td valign="bottom" class="tdFontcolor">
		<hrms:paginationtag name="courseTrainForm" pagerows="${courseTrainForm.pagerows}" property="pagination" scope="page" refresh="true"></hrms:paginationtag>
		</td>
	    <td  align="right" nowrap class="tdFontcolor">
		     <p align="right"><hrms:paginationdblink name="courseTrainForm" property="pagination" nameId="courseTrainForm" scope="page">
			</hrms:paginationdblink>
		</td>
	</tr>
</table>
</td></tr>
</table>
</html:form>
<%TrainClassBo.setcodesetid(codelist); %>
<script language='javascript' >			
	parent.parent.ril_body1.setSecondPage(4);			
</script>