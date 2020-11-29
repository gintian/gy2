<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
                 com.hjsj.hrms.actionform.performance.PerAnalyseForm,
                 com.hrms.struts.taglib.CommonData,
                 org.apache.commons.beanutils.LazyDynaBean" %>
<html>
  <head>
    <hrms:themes />
	<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
	<%
	
	PerAnalyseForm perAnalyseForm=(PerAnalyseForm)session.getAttribute("perAnalyseForm");
    ArrayList perDegreeList=perAnalyseForm.getPerDegreeList();
	ArrayList wholeEvalDataList=perAnalyseForm.getWholeEvalDataList();
    %>
  </head>
  <script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
  <script type="text/javascript" src="/js/constant.js"></script>
  <SCRIPT LANGUAGE=javascript src="/performance/perAnalyse/perAnalyse.js"></SCRIPT> 
  <script language='javascript' >

  	function voteStat(codeitemid)
  	{
  		document.perAnalyseForm.action="/performance/perAnalyse.do?b_voteStat=query0&codeitemid="+codeitemid;
  		document.perAnalyseForm.submit();
  	}
  	
  	function changePlan()
  	{
  		document.perAnalyseForm.action="/performance/perAnalyse.do?b_voteStat0=query";
		document.perAnalyseForm.target="detail";
		document.perAnalyseForm.submit();
  	
  	}
  	
  	function executeExcel2()
	{
		var planid = document.perAnalyseForm.planIds.value;
		if(planid=='')
			return;
		var hashvo=new ParameterSet();
		hashvo.setValue("model","9");
		var codeitemid="-1";
		<% if(request.getParameter("codeitemid")!=null){
				out.println("codeitemid='"+request.getParameter("codeitemid")+"'");
			}
		 %>
		hashvo.setValue("codeitemid",codeitemid);
		hashvo.setValue("planid",planid);
		var request=new Request({method:'post',asynchronous:false,onSuccess:showFile,functionId:'9026000014'},hashvo);
	}

  </script>  
  <body>

   <html:form action="performance/perAnalyse">  
    <font size='2'> <bean:message key="kh.field.plan"/>:</font>&nbsp;
	<html:select name="perAnalyseForm" property="planIds" size="1" onchange="changePlan()">
  	 <html:optionsCollection property="perPlanList" value="dataValue" label="dataName"/>
	</html:select>&nbsp;
	
	 &nbsp;&nbsp; <input type='button' value='<bean:message key="general.inform.muster.output.excel"/>' onclick="executeExcel2()" class="mybutton" />
	<logic:equal name="perAnalyseForm" property="fromModule" value="analyse">
		<hrms:tipwizardbutton flag="performance" target="il_body" formname="perAnalyseForm"/> 
	</logic:equal>
	<Br>
	
	 <table width="80%" border="0" cellspacing="0"  id='a_table' align="left" cellpadding="0" class="ListTable">
   			  <% if(perDegreeList.size()>0){ %>
   			  <tr >
   				 <td align="center" rowspan='3' width='7%'  class='TableRow' nowrap><bean:message key="conlumn.mediainfo.info_id"/> </td>
   				 <td align="center" rowspan='3' width='9%' class="TableRow" nowrap><bean:message key="jx.datacol.khobj"/> </td>    
   			   <td align="center"   height='25' width='64%' colspan='<%=(perDegreeList.size()*2)%>'  class="TableRow" nowrap><bean:message key="org.performance.zt"/> </td> 
   			  </tr>
   			  <tr >
   			  		<logic:iterate id="element" name="perAnalyseForm" property="perDegreeList"  >
	   			  		 <td align="center" height='25' colspan='2' class="TableRow" nowrap><bean:write name="element" property="itemname" filter="true"/> </td>
   			  		</logic:iterate>
   			  </tr>
   			   <tr >
   			  		<logic:iterate id="element"  name="perAnalyseForm" property="perDegreeList"  >
	   			  		 <td align="center" width='8%' height='25'  class="TableRow" nowrap><bean:message key="lable.welcome.invtextresult.ballot"/> </td>
	   			  		 <td align="center" width='8%' height='25'  class="TableRow" nowrap><bean:message key="train.evaluationStencil.percent"/> </td>
   			  		</logic:iterate>
   			  </tr>
   			  <% } else {  %>
   			  <tr>
   			 		<td align="center"  width='50'  class='TableRow' nowrap><bean:message key="conlumn.mediainfo.info_id"/> </td>
   				 	<td align="center"  width='80' class="TableRow" nowrap><bean:message key="jx.datacol.khobj"/> </td>  
   			  		<td align="center"   height='25' class="TableRow" nowrap><bean:message key="org.performance.zt"/> </td> 
   			  </tr>
   			  <% }%>
   			  
   			  <%
   			  	for(int i=0;i<wholeEvalDataList.size();i++){
   			  		LazyDynaBean abean=(LazyDynaBean)wholeEvalDataList.get(i);
   			  		
   			  		out.println("<tr >");
   			  		out.println("<td align='center' class='RecordRow' >"+(i+1)+"</td>");
   			  		out.println("<td align='left' class='RecordRow' >&nbsp;"+((String)abean.get("a0101"))+"</td>");
   			  		if(perDegreeList.size()>0) {
	   			  		for(int j=0;j<perDegreeList.size();j++)
	   			  		{
	   			  			LazyDynaBean a_bean =(LazyDynaBean)perDegreeList.get(j);
	   			  			String id=(String)a_bean.get("id");
	   			  		    out.println("<td align='right' class='RecordRow' >"+((String)abean.get(id))+"&nbsp;</td>");
	   			  		    out.println("<td align='right' class='RecordRow' >"+((String)abean.get(id+"%"))+"&nbsp;</td>");
	   			  		}
   			  		} 
   			  		else {
   			  			out.println("<td align='left' class='RecordRow' >&nbsp;</td>");
   			  		}
   			  		out.println("</tr>");
   			   }
   			   %>
   	 </table>
   </html:form>
   
   
  </body>
</html>
<script type="text/javascript">
	//if(!getBrowseVersion()) {//非IE，将iframe的il_body置为和ie一样的name  detail
    if(parent.parent.frames["il_body"])
        parent.parent.frames["il_body"].name = "detail";
	//}
	if('${perAnalyseForm.perVoteStatInfo}'!='')	
		alert('${perAnalyseForm.perVoteStatInfo}');
</script>
