<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,				 
				 com.hrms.struts.taglib.CommonData,
				 com.hrms.hjsj.sys.DataDictionary,
				 com.hrms.hjsj.sys.FieldItem,
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant" %>
<hrms:themes />
<html>
<head>
<title></title>
</head>
<%   
	int i=0;
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
%>
<script language="javascript">
function returns()
{
	//performanceImpForm.action="/selfservice/performance/performancePointPrivImplement.do?br_return=return";
	performanceImpForm.action="/selfservice/performance/performanceImplement.do?b_query=link&a_code=${performanceImpForm.a_code}";
	performanceImpForm.submit();
}
function setPointPriv(mainbody_id,point_id,object_id,obj)
{	
	var hashvo=new ParameterSet();
	hashvo.setValue("object_id",object_id);
	hashvo.setValue("plan_id",'${performanceImpForm.dbpre}');
	hashvo.setValue("mainbody_id",mainbody_id);
	hashvo.setValue("pointid",point_id);
	if(obj.checked)
		hashvo.setValue("value","1");
	else 
	    hashvo.setValue("value","0");
	hashvo.setValue("opt","3");
	var request=new Request({method:'post',asynchronous:false,functionId:'9023000003'},hashvo);
}
</script>
<body>
<html:form action="/selfservice/performance/performancePointPrivAdd">
<%if("hl".equals(hcmflag)){%>
	<br>
<% } %>
<table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
  <tr>
    <td align="left" nowrap style="height:20px">       
        <bean:message key="lable.appraisemutual.examineobject"/>: <bean:write name="performanceImpForm" property="khobjname" filter="false"/>&nbsp;
    </td> 
   </tr>
	   <tr>	  
	   		<td>
<bean:message key="lable.performance.targetPurview"/>
			</td>
			</tr>
	 <logic:iterate id="element" name="performanceImpForm" property="perPointPrivList"  length="1"> 
 	    
             <tr class="trDeep" >
 		<logic:iterate id="item" name="element" indexId="index"> 

	            <td align="center" class="TableRow"  width="70" nowrap >
	              	
	              	<bean:write name="item" />&nbsp; 
	             </td>
                </logic:iterate>
                 	    	    		        	        	        
           </tr>
   	
	</logic:iterate>


	<logic:iterate id="element" name="performanceImpForm" property="perPointPrivList"  offset="1"> 
   	 
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
          
          int b=0;
                  
          %>  
           	<logic:iterate id="item" name="element" indexId="index">
           		
	     <td align="<%=(b<2?"left":"center")%>" class="RecordRow" nowrap>
            	 	&nbsp;<bean:write name="item" filter="false"/>&nbsp;
            	        
	     </td> 
	     <% b++; %>
	     	</logic:iterate>
	    		        	        	        
          </tr>
       </logic:iterate>
               <tr>
            <td align="left" style="height:35px">
            <!-- 
             <logic:equal name="performanceImpForm" property="status" value="3">
                 
 		 <hrms:submit styleClass="mybutton" property="add" ><bean:message key="button.save"/></hrms:submit>
             </logic:equal>
              <logic:equal name="performanceImpForm" property="status" value="5">
                 
 		 <hrms:submit styleClass="mybutton" property="add" ><bean:message key="button.save"/></hrms:submit>
             </logic:equal>
             --> 
             
             <input type="button" name="addbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="returns()">    
             
            
            </td>
          </tr>  
        
</table>

<br>
<input type="hidden" name="plan_id" value="<bean:write name="performanceImpForm" property="dbpre" />" />
<input type="hidden" name="object_id" value="<bean:write name="performanceImpForm" property="objectID" />" />

</html:form>


</body>
</html>