<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.welcome.WelcomeForm,com.hjsj.hrms.actionform.askinv.EndViewForm,java.util.ArrayList,com.hjsj.hrms.utils.PubFunc"%>

<%@ page import="java.util.*"%>
<style>
table.ftable { background:#FFFFFF; border-collapse:collapse; font-size:12px;  /*color:#666633;*/}
table.ftable td { border:1px solid #C4D8EE; height:22px; padding:0 5px; overflow: hidden; text-overflow:ellipsis;word-break: normal;}
table.ftable th {border:1px solid #C4D8EE;height:22px;/*color:#336699*/;line-height:22px; padding:0 5px;  background-color:#f4f7f7;}

.RecordRowLast {
    border: inset 0px #C4D8EE;
    border-bottom-width: 1px;
    
    BORDER-LEFT: #C4D8EE 1pt solid; 
    BORDER-RIGHT: #C4D8EE 1pt solid; 
    BORDER-TOP: #C4D8EE 0pt solid;
    font-size: 12px;
    height:100px;
}
.RecordRowTxt {
    border: inset 1px #C4D8EE;
    BORDER-BOTTOM: #C4D8EE 1pt solid; 
    BORDER-LEFT: #C4D8EE 1pt solid; 
    BORDER-RIGHT: #C4D8EE 1pt solid; 
    BORDER-TOP: #C4D8EE 1pt solid;
    font-size: 12px;
    font-weight: bold;
    border-collapse:collapse; 
    height:22px;
}
</style>
<hrms:themes></hrms:themes>
<%
	EndViewForm endViewForm=(EndViewForm)session.getAttribute("endViewForm");
	int i=0;
	int count=0;
	count=endViewForm.getItemtxtlist().size();
	int k=0;
	int count2=0;
	count2=endViewForm.getItemwhilelst().size();
	ArrayList allList= endViewForm.getAllList();
	
	
%>

<script language="JavaScript" src="/anychart/js/AnyChart.js"></script>
<script language="JavaScript" src="/ajax/basic.js"></script>
<script language='javascript'>

	 function showfile(outparamters)
	{
		
		var outName=outparamters.getValue("outName");
	    var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName,"excel");
	}

	function excel()
	{
		var hashvo=new ParameterSet();
		hashvo.setValue("id",'<%=(PubFunc.decryption(request.getParameter("id")))%>');
	    var In_paramters="opt=2";  
	   	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showfile,functionId:'10300130015'},hashvo);
	}

</script>


<html:form action="/selfservice/infomanager/askinv/searchendview" >
	 
     <table  width="80%" align="center" border="0" cellspacing="0" cellpadding="0" class="ftable" >
     	<tr>
     		<td class="TableRow"  height='50' colspan="2">
			  <bean:message key="lable.topicname"/>:
   		      <bean:write name="endViewForm" property="name" filter="true"/>	     		
     		</td>
     	</tr>
   		<logic:iterate  id="element" name="endViewForm" property="itemwhilelst"  scope="session">
   		<tr>
   		
   		<td valign='top'>
   			<table border="0" cellspacing="0" cellpadding="0">
   			 <tr>
   			    <td style="border: 0" align="center">
   			<table cellspacing="0" cellpadding="0"  width='450' class="ListTable" style="margin-top:10px;  margin-bottom: 10px">
    		 <tr> 
    		   		<th  align='left' width='350' class='common_background_color' >
    		   		<bean:write name="element" property="itemName"/>
    		   		<logic:equal value="1" name="element" property="fillflag"><font color="red">*</font></logic:equal>
    		   		</th>
    		   		<th    align='center' width='50' nowrap  class='common_background_color'>&nbsp;
    		   		票数
    		   		</th>
    		   		<th    align='center'  width='50'  nowrap  class='common_background_color'>&nbsp;
    		   		比例
    		   		</th>
 			 </tr>
  			 <logic:iterate id="test" name="element" property="endviewlst" >
 			 <tr>
    				<td nowrap>
    					<table border="0" cellspacing="0" cellpadding="0">
    						<tr>
    							<td  align="left" style="border: 0;word-break: break-all;">
    							&nbsp;°&nbsp;<bean:write name="test" property="pointName" filter="true"/>
    							</td>	
    							<td style="border: 0">
    							<logic:equal name="test" property="conextFlag" value="1">
    								<a href="/selfservice/welcome/infodescribe.do?b_query=link&itemid=<bean:write name="test" property="itemid" filter="true"/>&pointid=<bean:write name="test" property="pointid" filter="true"/>&txtf=1&train=train&classid=<%=request.getParameter("id") %>">
    									<bean:message key="lable.welcome.invdescribe.describeinfo"/>
    								</a>
    							</logic:equal>
    							</td>
    						</tr>
    					</table>
   				    </td>
    				<td  nowrap>&nbsp;  				
    					<bean:write name="test" property="sumNum" filter="true"/>
   					    <br>
   				    </td>
   				    
   				    <td  nowrap align='center' >  				
    					
    					<bean:write name="test" property="precent" filter="true"/>
   					   
   				    </td>
   				    
  			</tr>
 
  			</logic:iterate>
  			</table>
  			</td>
  		    </tr>
  		  </table>
  			
  			
  			</td  >
  			 <td  valign='top' >
	  			<table>
	  			 <tr   >
	  			 <%
	  			      ArrayList alist = (ArrayList)allList.get(k);
	  			      endViewForm.setChartList(alist);
	  			  %>
	  			 	<td style="border: 0;margin-top:10px;" align="center"  id='<%="pnl_"+k %>'>
	   				  <hrms:chart name="endViewForm" title="" scope="session" isneedsum="false" legends="chartList" data="" width="480" height="430" chart_type="11" chartpnl='<%="pnl_"+k %>'>
	   				  </hrms:chart>
	   				</td>
	   			</tr> 
	   			</table>
   			</td>
   		
   		</tr>
   		<%
   			k++;
   		%>
  		</logic:iterate>
  		
	</table>
	
	<%
		if(count>0)
		{
	%>
		<table  width="80%" align="center" border="0" cellspacing="0" cellpadding="0" class="ListTable">
		  <tr><td class="TableRow" style="border-top:0;"><b>问答题</b></td></tr>
			<%
			   ArrayList itemtxtlist = (ArrayList)endViewForm.getItemtxtlist();
			   HashMap itemMap = (HashMap)endViewForm.getItemMap();
		       for(int x=0; x<itemtxtlist.size();x++){
		           WelcomeForm wf = (WelcomeForm)itemtxtlist.get(x);
		           String itemid = wf.getItemid();
		           ArrayList itemwhilelst = (ArrayList)itemMap.get(itemid);
		           if(itemwhilelst.size()>0) {
		     %>
		     <tr>
                 <td align="left" class="RecordRowTxt common_border_color">
                    <%=x+1 %>、<%=wf.getItemName() %>
                 </td>
             </tr>
             <tr>
             <td align="left" class="RecordRowLast common_border_color" valign='top'>
             <%
                for(int m=0;m<itemwhilelst.size();m++){
                    wf = (WelcomeForm)itemwhilelst.get(m);
                    String itemTxt = wf.getContext();
             %>
             &nbsp;&nbsp;&nbsp;&nbsp;<%=itemTxt %><br>
             <%
                if(m!=itemwhilelst.size()-1){
             %>
             <hr style="border-bottom:1px dotted #000; width:100% ;" align="center">
             <%} %>
             <%} %>
             </td>
             </tr>
             <%} %>
		     <%
		       }
			%>
		</table>
	<%
	}
	%>
 <table  width="70%" align="center" style="height:35px;">
          <tr>
            <td align="center">
            <Input type='button' value='生成Excel' class="mybutton"  onclick="excel()" /> 
         	        <logic:equal name="endViewForm" property="f" value="0">
	 			<hrms:submit styleClass="mybutton" property="br_return">
            				<bean:message key="button.return"/>
	 			</hrms:submit>  
	 		</logic:equal>
	 		<logic:equal name="endViewForm" property="f" value="1">
	 			<input type="button" class="mybutton" value="<bean:message key="button.close"/>" onclick="window.close();">
	 		</logic:equal>
	 		
	 		
        
            </td>
           
          </tr>          
</table>
</html:form>
