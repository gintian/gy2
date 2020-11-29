<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script language="javascript" src="/ext/adapter/ext/ext-base.js"></script>
<script language="javascript" src="/ext/ext-all.js"></script>
<script language="javascript" src="/ext/rpc_command.js"></script>
<style>
.myfixedDiv
{  
	overflow:auto; 
	height:expression(document.body.clientHeight-130);
	width:expression(document.body.clientWidth-10); 
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; 
    border-collapse:collapse
}
</style>
<html:form action="/train/trainexam/exam/mytest/mytest" styleId="form1">
<bean:define id="testCount" name="myTestForm" property="testCount"></bean:define>
<% int s=0;
	int i=0;
	int count=0;
%>


<div class="myfixedDiv">
    <table width="100%" border="0" id="GV" cellspacing="0"  align="center" cellpadding="0" >
      
         <tr>
            <td align="center" class="TableRow" width="10%" nowrap style="border-left:none;border-top: none;">
				&nbsp;<bean:message key="label.serialnumber"/>&nbsp;
            </td>
            
	       <td align="center" class="TableRow" style="border-left:none;border-top: none;" width="30%" nowrap>
				&nbsp;<bean:message key="train.trainexam.question.questiones.fraction"/>&nbsp;
	       </td>
            
            <td align="center" class="TableRow" style="border-left:none;border-top: none;" width="30%" nowrap>
				&nbsp;<bean:message key="hire.interviewExamine.description"/>&nbsp;
            </td>
	        <td align="center" class="TableRow" style="border-left:none;border-top: none;border-right: none;" width="30%" nowrap>
				&nbsp;<bean:message key="kq.card.work_date"/>&nbsp;
	        </td>	        
         </tr>

      <%i=0; int num=1;%>
      <hrms:paginationdb id="element" name="myTestForm" sql_str="myTestForm.myTestSql" table="" where_str="myTestForm.myTestWhere" columns="myTestForm.myTestColumn" order_by="myTestForm.myTestOrder" page_id="pagination" pagerows="${myTestForm.pagerows}"  indexes="indexes">
        
          <% count ++;         
          if(i%2==0){ 
          %>
          <tr class="trShallow" onClick="javascript:tr_onclick(this,'')">
          <%
          }else{
          %>
          <tr class="trDeep" onClick="javascript:tr_onclick(this,'DDEAFE')">
          <%} 
                   
          %>     
         
          <td align="left" class="RecordRow" nowrap style="border-left:none;border-top: none;"> 
          	&nbsp;<%=num %>&nbsp;
          </td> 
          <td align="right" class="RecordRow" style="border-left:none;border-top: none;" nowrap> 
 			&nbsp;<bean:write name="element" property="score"/>&nbsp;
          </td>
          <td align="center" class="RecordRow" style="border-left:none;border-top: none;" nowrap> 
          <bean:define id="paperid" name="element" property="paper_id"></bean:define>
           <%String id = SafeCode.encode(PubFunc.encrypt(paperid.toString())); %>
          	&nbsp;<a href="javascript:;" onclick="detail('<%=id %>','<bean:write name="myTestForm" property="r5300"/>','<bean:write name="myTestForm" property="r5000"/>')"><img border="0" src="/images/lee.png" alt='<bean:message key="hire.interviewExamine.description"/>'/></a>&nbsp;
          </td>         
          <td align="center" class="RecordRow" style="border-left:none;border-top: none;border-right: none;" nowrap> 
          	&nbsp;<bean:write name="element" property="create_time"/>&nbsp;
          	
          </td>
           
            <%
            i++;  
            %>  
          </tr>
          <%
          s++;
          num ++;
          %>
        </hrms:paginationdb> 
         
         
                                 	    		        	        	        
    </table>
  </div>
  <div class="myfixedDiv" style="height:40px; border-top-width:0px;">		    
     <table  width="100%" >
       <tr>          
       <td width="60%" valign="bottom" align="left" height="30" nowrap>
           <hrms:paginationtag name="myTestForm"
								pagerows="${myTestForm.pagerows}" property="pagination"
								scope="page" refresh="true"></hrms:paginationtag>
	  </td>
	  <td  width="40%" valign="bottom" align="right" nowrap>
	     <hrms:paginationdblink name="myTestForm" property="pagination" nameId="myTestForm" scope="page">
             </hrms:paginationdblink>
	  </td>
	  <td></td>
	</tr>
     </table>
</div>
<div style="height:35px; text-align: center;padding-top: 5px;">
	<%if ("-1".equals(testCount.toString()) || count < Integer.parseInt(testCount.toString())){%>
	<logic:notEqual value="2" property="state" name="myTestForm">
		<input type="button" name="myselfexam"  class="mybutton" value="<bean:message key="train.trainexam.exam.mytest.selfexam"/>" onclick="selfexam('<bean:write name="myTestForm" property="r5300"/>','<bean:write name="myTestForm" property="r5000"/>')"/>
	</logic:notEqual>
	<%} %>
	&nbsp;&nbsp;<input type="button" name="myselfexam" value="<bean:message key="button.leave"/>" onclick="returnMyLesson()" class="mybutton"/>
</div>

</html:form>
<script type="text/javascript">
<!--
	//详情
	function detail(id,r5300,r5000) {	
		var url = "/train/trainexam/paper/preview/paperspreview.do?b_query=link&r5300="+r5300+"&exam_type=1&flag=4&returnId=5&paper_id="+id+"&r5000="+r5000;
		submitForm(url);
		
	}
	
	// 返回
	function returnMyLesson() {
		var url = "/train/resource/mylessons.do?b_query=link&opt=ing";
		submitForm(url);
	}
	
	// 提交表单
	function submitForm(url) {
		var form1 = document.getElementById("form1");
		form1.action = url;
		form1.submit();
	}
	
	// 个人考试
	function selfexam(r5300,r5000) {
		var map = new HashMap();
		map.put("r5300",r5300);
		map.put("r5000",r5000);
		Rpc({functionId:'2020030185',success:examSucc},map);		
		
	}
	
	function examSucc(response) {
		var value=response.responseText;
		var map=Ext.decode(value);
		if (map.biaozhi == "ok") {
			var url = "";
			if ("${myTestForm.modelType}" == "1") {
				url = "/train/trainexam/paper/preview/paperspreview.do?b_query=link&r5300="+map.r5300+"&exam_type=1&flag=5&returnId=2&r5000="+map.r5000+"&paper_id="+map.paper_id;
			} else {
				url = "/train/trainexam/paper/preview/paperspreview.do?b_single=link&r5300="+map.r5300+"&current=1&exam_type=1&flag=5&returnId=2&r5000="+map.r5000+"&paper_id="+map.paper_id;
			}
			submitForm(url);
		} else {
			alert(getDecodeStr(map.biaozhi));
		}
	}
//-->
</script>
