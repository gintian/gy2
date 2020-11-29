<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@page import="com.hjsj.hrms.businessobject.train.trainexam.question.questiones.QuestionesBo"%>
<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.businessobject.train.TrainClassBo" %>
<script language="javascript" src="/ajax/basic.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
<script type="text/javascript">
<!--
	function showR4015(descId){
		if(descId!="-1"){
			document.getElementById("r4015desc").innerHTML=document.getElementById("r4015"+descId).value;
			document.getElementById("r4015Hidden").style.display='none';
			document.getElementById("r4015Show").style.display='';
		}else{
			document.getElementById("r4015Hidden").style.display='';
			document.getElementById("r4015Show").style.display='none';
		}
	}
	function showR4009(descId){
		if(descId!="-1"){
			document.getElementById("r4009desc").innerHTML=document.getElementById("r4009"+descId).value;
			document.getElementById("r4015Hidden").style.display='none';
			document.getElementById("r4009Show").style.display='';
		}else{
			document.getElementById("r4015Hidden").style.display='';
			document.getElementById("r4009Show").style.display='none';
		}
	}
   function search(flag){
	   var year=document.getElementById("year").value;
	   var classname=document.getElementById("classname").value;
	   classname=getEncodeStr(classname);
	   trainClassForm.action="/train/job/browseTrainClassList.do?b_myClass=link&year="+year+"&classname="+$URL.encode(classname);
	   trainClassForm.submit();
   }
   function openw(url){
		var ww = 800;
		var hh = 600;
		window.open(url, "_new","toolbar=no,location=no,directories=no,menubar=no,scrollbars=yes,resizable=no,status=no,top=0,left=0,width="+ww+",height="+hh);
	   
	}
	function eventDesc(id,readonly){
		var wherestr = " from r31 where r3101='"+id+"'";
		var code ="/train/job/browseTrainClassList.do?b_event=link`id=r3117`read="+readonly+"`flag=add`classid="+id;
		var iframe_url="/general/query/common/iframe_query.jsp?src="+code;
		openw(iframe_url);     
	}
//-->
</script>
<html:form action="/train/job/browseTrainClassList">
<div id="r4015Hidden">
<table>
<tr>
<td>
　<html:select name="trainClassForm" property="year" styleId="year" onchange="search('1');">
    <html:optionsCollection property="yearList" value="dataValue" label="dataName"/>          
  </html:select>
</td>
<td><bean:message key="train.job.year"/>&nbsp;</td>
<td>&nbsp;<bean:message key="train.job.trainClassName"/>&nbsp;</td><td><html:text name="trainClassForm" property="classname" styleClass="TEXT4" styleId="classname"/></td>
<td><input type="button" class="mybutton" value='<bean:message key="button.query"/>' onclick="search('2')"></td>
</tr>
</table>
<%int i=0; 
	UserView userViews=(UserView)session.getAttribute(WebConstant.userView); %>
	<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
	<thead>
	<tr>
		<logic:iterate id="element" name="trainClassForm"  property="list" indexId="index">
			<logic:equal name="element" property="itemid" value="r3130">
    		<td align="center" class="TableRow" nowrap>
    			 &nbsp;<bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;                      
	      	</td>
	      	<td align="center" class="TableRow" nowrap>
			<bean:message key="hire.interviewExamine.description"/>&nbsp;
		    </td> 
	      </logic:equal>
	      <logic:notEqual name="element" property="itemid" value="r3101">
	      <logic:notEqual name="element" property="itemid" value="r3130">
			<td align="center"class="TableRow" nowrap>
                 &nbsp;<bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
	       </td>
	       </logic:notEqual>
	       </logic:notEqual>
		</logic:iterate>
		
	</tr>
	</thead>
	<hrms:paginationdb id="element" name="trainClassForm" sql_str="trainClassForm.sql" table="" 
	where_str="" columns="trainClassForm.columns" 
	order_by="trainClassForm.orderBy" page_id="pagination" pagerows="99999999">
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
    	<logic:iterate id="fielditem"  name="trainClassForm"  property="list" indexId="index">
    	<bean:define id="nid" name='element' property='r3101'/>
    	<%String r3101 = SafeCode.encode(PubFunc.encrypt(nid.toString())); %>
	      <logic:notEqual name="fielditem" property="itemid" value="r3101">
		      <logic:equal name="fielditem" property="itemid" value="r3130">
				<td align="left"class="RecordRow" nowrap>
	               &nbsp;<bean:write name="element" property="${fielditem.itemid}" filter="false"/>&nbsp;   
		       </td>
		       <td align="center" class="RecordRow"  nowrap>
			    <a href="/train/job/browseTrainClassList.do?b_queryDesc=query&operator=2&r3101=<%=r3101 %>" >    
                 <img src="/images/view.gif" border=0>    
             	</a>
		    	</td>
		       </logic:equal>
		       <logic:notEqual name="fielditem" property="itemid" value="r3130">
		       <logic:notEqual name="fielditem" property="itemid" value="r4009">
		       <logic:notEqual name="fielditem" property="itemid" value="r4015">
		       <logic:notEqual name="fielditem" property="itemid" value="r3117">
		       <logic:notEqual name="fielditem" property="itemid" value="r3125">
		               <logic:notEqual name="fielditem" property="codesetid" value="0">
		               <td align="left" class="RecordRow" nowrap>
		          	   		<hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
		          	   		&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;   
		          	   </td>         
		               </logic:notEqual>
		               <logic:equal name="fielditem" property="codesetid" value="0">
		               <logic:equal name="fielditem" property="itemtype" value="N">
		               <td align="right" class="RecordRow" nowrap>
		                &nbsp;<bean:write name="element" property="${fielditem.itemid}" filter="false"/>&nbsp;
		               </logic:equal>
		               <logic:notEqual name="fielditem" property="itemtype" value="N">
		               
		               <logic:equal name="fielditem" property="itemtype" value="M">
		               <bean:define id="itemid" value="${fielditem.itemid}"></bean:define>
					<bean:define id="fieldsetid" value="${fielditem.fieldsetid}"></bean:define>
					<%
           			String value = QuestionesBo.checkDate(itemid.toString(),fieldsetid.toString(),nid.toString(),"",""); 
           			%>
		               	   <td align="left" class="RecordRow" style="width: 100px;"  nowrap title="<%=value %>">
		               		<div STYLE="width: 100px; overflow: hidden; text-overflow: ellipsis; white-space:nowrap;">
		               		&nbsp;<%=value %>&nbsp;
		               		</div>
		               </logic:equal>
		               <logic:notEqual name="fielditem" property="itemtype" value="M">
		               <td align="left" class="RecordRow" nowrap>
		                   &nbsp;<bean:write name="element" property="${fielditem.itemid}" filter="false"/>&nbsp;
		               </logic:notEqual> 
		               </logic:notEqual>   
		               </td>             
		               </logic:equal>
		           </logic:notEqual>
		           <logic:equal name="fielditem" property="itemid" value="r3125">
		            <td align="left" class="RecordRow" nowrap> 
		             	<bean:define id="r3125" name="element" property="${fielditem.itemid}" />
		                   &nbsp;<%=TrainClassBo.getPlanName(r3125.toString()) %>&nbsp;
		           </logic:equal>                            
			      </logic:notEqual>
			      <logic:equal name="fielditem" property="itemid" value="r3117">
			      	<td align="center" class="RecordRow" >
			      	<img src="/images/view.gif" onclick="eventDesc('<%=r3101 %>','0')" border="0"  style="cursor:hand;">
			      	</td>
			      </logic:equal>
			      </logic:notEqual>
			      </logic:notEqual>
			      <logic:equal name="fielditem" property="itemid" value="r4009">
		               <td align="center" class="RecordRow" >
           		<logic:notEqual value="" name="element" property="r4009">
           			<%
           			String value = QuestionesBo.checkDate("r4009","r40",userViews.getA0100(),"",nid.toString()); %>
           			<input type="hidden" name="r4009<%=i %>" id="r4009<%=i %>" value='<%=value %>' />
           			<a href="javascript:showR4009('<%=i %>');"><img src="/images/view.gif" border=0></a>
           		</logic:notEqual>
             		</td>	        	        
		          </logic:equal>
			      <logic:equal name="fielditem" property="itemid" value="r4015">
		               <td align="center" class="RecordRow" >
           		<logic:notEqual value="" name="element" property="r4015">
           		<%
           			String value = QuestionesBo.checkDate("r4015","r40",userViews.getA0100(),"",nid.toString()); %>
           			<input type="hidden" name="r4015<%=i %>" id="r4015<%=i %>" value='<%=value %>' />
           			<a href="javascript:showR4015('<%=i %>');"><img src="/images/view.gif" border=0></a>
           		</logic:notEqual>
             </td>	        	        
		          </logic:equal>
			    </logic:notEqual>
	      </logic:notEqual>
    	</logic:iterate>
    	
    </tr>
    </hrms:paginationdb> 
</table>
</div>
<table id="r4015Show" class="ListTable" style="display: none;width: 400px;height: 200px;" cellpadding="0" cellspacing="0" align="center">
	<tr><td><br/></td></tr>
	<tr>
		<td class="TableRow" align="center"><bean:message key="train.job.student.idea"/></td>
	</tr>
	<tr>
		<td class="RecordRow">
		<div id="r4015desc" style="width: 100%;height: 160px;padding: 10px;">
			
		</div>
		</td>
	</tr>
	<tr>
		<td align="center" style="padding: 8px;"><input type='button' value="<bean:message key='button.return'/>"  class="mybutton"   onclick="showR4015('-1');" ></td>
	</tr>
</table>
<table id="r4009Show" class="ListTable" style="display: none;width: 400px;height: 200px;" cellpadding="0" cellspacing="0" align="center">
	<tr><td><br/></td></tr>
	<tr>
		<td class="TableRow" align="center"><bean:message key="train.plan.review.ass.results"/></td>
	</tr>
	<tr>
		<td class="RecordRow">
		<div id="r4009desc" style="width: 100%;height: 160px;padding: 10px;">
			
		</div>
		</td>
	</tr>
	<tr>
		<td align="center" style="padding: 8px;"><input type='button' value="<bean:message key='button.return'/>"  class="mybutton"   onclick="showR4009('-1');" ></td>
	</tr>
</table>
</html:form>