<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
                 com.hjsj.hrms.actionform.performance.evaluation.CalcRuleForm,
                 org.apache.commons.beanutils.LazyDynaBean" %>
<%
	CalcRuleForm crf=(CalcRuleForm)session.getAttribute("calcRuleForm");	
	String UnLeadSingleAvg=crf.getUnLeadSingleAvg();
	ArrayList rangelist=crf.getRangelist();
	
 %>
<html>
  <head>
   

  </head>
  <style>
	div#treemenu {
	BORDER-BOTTOM:#94B6E6 1pt solid;
	BORDER-LEFT: #94B6E6 1pt solid;
	BORDER-RIGHT: #94B6E6 1pt solid;
	BORDER-TOP: #94B6E6 1pt solid;
	width: 500px;
	height: 300px;
	overflow: auto;
	}
</style>
  
  <script language='javascript' >
  	function numCheck(){
		if ( !(((window.event.keyCode >= 48) && (window.event.keyCode <= 57)) 
		|| (window.event.keyCode == 13) || (window.event.keyCode == 46) 
		|| (window.event.keyCode == 45)))
		{
			window.event.keyCode = 0 ;
		}
	}
  function checkNum(obj)
  {
  	if(ltrim(rtrim(obj.value))=='')
  	{
  		obj.value="";
  		return;
  	}
  	if(checkIsNum2(obj.value))
  	{
  		
  	}
  	else
  	{
  		alert(KHPLAN_ERRORINFO3);
  		obj.value="";
  		return;
  	}
  	
  	var temps=obj.name.split("\.");
  	 
  	if(obj.name.indexOf("maxscore")==-1)
  	{ 
  		var obj2=document.getElementsByName(temps[0]+".maxscore") 
  		if(checkIsNum2(obj2[0].value))
  		{
  			if(obj2[0].value*1<obj.value*1)
  			{
  				alert("下限分值不能大于上限分值!");
  				obj.value="";
  				return;
  			}
  		}
  	}
  	else
  	{
  		var obj2=document.getElementsByName(temps[0]+".minscore")
  		if(checkIsNum2(obj2[0].value))
  		{
  			if(obj2[0].value*1>obj.value*1)
  			{
  				alert("下限分值不能大于上限分值!");
  				obj.value="";
  				return;
  			}
  		}
  	}
  	
  	
  }
  
  
  function saveRange()
  {
  	document.calcRuleForm.action="/performance/evaluation/calculate.do?b_saverange=sub";
  	document.calcRuleForm.submit();
  }
  
  <%
  	if(request.getParameter("b_saverange")!=null&&request.getParameter("b_saverange").equals("sub")){
  		
  		%>
  			var ret=new Object();
  			ret.ok='ok';
  			ret.isvalidate='${calcRuleForm.isvalidate}';
  			if(window.showModalDialog){
                parent.window.returnValue=ret;
  			}else {
  		 		parent.window.opener.rulefanwei_ok(ret);
  			}
  		<%
  		out.println("parent.window.close();");
  	}
  %>
  
  </script>
  <script language="JavaScript" src="../../js/function.js"></script>
  <body>
   <html:form action="/performance/evaluation/calculate"> 
     <table width='100%' > 
     	<tr><td width='80%'>
     	 <div id='treemenu' >
     		<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0"   style="margin-top:-1"  class="ListTable">
		   	  <thead>
		   	  <tr style="position:relative;top:expression(this.offsetParent.scrollTop-1);">   
		   	 	  <td align="center" class="TableRow" style="text-align:center;border-left:0;" width='10%'  nowrap>序号</td>   
     			 <td align="center" class="TableRow"  width='40%'  nowrap>指标名称</td> 
     			 <td align="center" class="TableRow" width='25%'  nowrap>上限分值</td> 
     			 <td align="center" class="TableRow" width='25%' style="border-right:0;"  nowrap>下限分值</td>
     		    </tr></thead>
     		    <%  int i=0; 
     		    	
     		    	for(int j=0;j<rangelist.size();j++){
     		    	LazyDynaBean bean=(LazyDynaBean)rangelist.get(i);
     		    	String pointname=(String)bean.get("pointname");
     		    	String pointid=(String)bean.get("point_id");
     		    	String upvalue=(String)bean.get("maxscore");
     		    	String downvalue=(String)bean.get("minscore");
     		    	
     		    %>
     		   
     		    <%
     		    	if(i%2==0){
     		     %>
     		     <tr class="trShallow">
     		     <% } else { %>
     		      <tr class="trDeep">
     		     
     		     <% } i++; %>
     		     
     		    	 <td align="center" class="RecordRow" style="border-left:0;" nowrap><%=i %></td>
  		   		     <td align="left" class="RecordRow"  nowrap><%=pointname %></td>
     			     <td align="center" class="RecordRow" nowrap><input type="text" name="rangelist[<%=j%>].maxscore"  onblur='checkNum(this)' onKeypress="numCheck()" value="<%=upvalue %>"  size='15' /></td>
     				 <td align="center" class="RecordRow" style="border-right:0;" nowrap><input type="text" name="rangelist[<%=j %>].minscore"    onblur='checkNum(this)' onKeypress="numCheck()" value="<%=downvalue %>"  size='15'  /></td>
     				 </tr>
     				 <%} %>
     			
     		</table>
     	</div>
     	</td>
        <td width='20%' valign='top' >
          <table width='100%' > 
     			<tr><td>
      				 <input type='button' value='<bean:message key="lable.tz_template.enter"/>'   onclick='saveRange()'  class="mybutton"  >
      		   </td></tr>
      		   <tr><td height='10'>
      				&nbsp;
      		   </td></tr>
        	   <tr><td>		
					<input type='button' value='<bean:message key="lable.tz_template.cancel"/>' onclick='parent.window.close()'  class="mybutton"  >
			   </td></tr>
			</table>  
        </td></tr>
     </table>
    
    
   </html:form> 
  </body>
</html>
  <script>  
var aa=document.getElementsByTagName("input");
for(var i=0;i<aa.length;i++){
	if(aa[i].type=="text"){
		aa[i].className="inputtext";
	}
}
  </script>  