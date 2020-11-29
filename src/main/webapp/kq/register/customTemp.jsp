<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.kq.register.DailyRegisterForm" %>
<%@ page import="java.util.ArrayList" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<STYLE type=text/css>
.div2
{
 overflow:auto; 
 width: 430px;height: 300px;
 line-height:15px; 
}
</STYLE>
<script type="text/javascript">

function DownCustomTemp(){
	var j=0;
	var all_array = document.getElementsByName("index");
	var check_array = new Array;
	var indexId="";
	var indexName="";
	for(var i = 0;i<all_array.length;i++){
		if(all_array[i].checked){
			check_array[j]=(all_array[i].value);
			indexId = indexId + all_array[i].id + ".";
			indexName = indexName + all_array[i].value + ".";
			j++;
		}
	}
	if((check_array == null) || (check_array.length==0)){
		alert("至少选择一个指标项！");
	}else{
	
		var obj = new Object();
		obj.indexId=indexId;
		obj.indexName=indexName;
		window.returnValue=obj;
		window.close();
	}
  
}
function selectAll(){
	var checkBoxs = document.getElementsByName("index");
	for(var i =0;i<checkBoxs.length;i++){
		var x = checkBoxs[i];
		if(x.checked){
			continue;
		}
		x.checked = !x.checked
	}
}
function cancleAll(){
	var checkBoxs = document.getElementsByName("index");
	for(var i =0;i<checkBoxs.length;i++){
		var x = checkBoxs[i];
		if(x.checked){
		x.checked = !x.checked
		}
	}
}
</script>

<%
	int i=0;
	int j=1;
	DailyRegisterForm dailyRegisterForm = (DailyRegisterForm)session.getAttribute("dailyRegisterForm");
	ArrayList id = dailyRegisterForm.getListid();
%>

<html:form action="/kq/register/daily_register" >

<table border="0" cellspacing="0" width="300" align="center" cellpadding="0" width="90%" >
	<tr>
		<td width="100%">
		<div id="d" class="div2 complex_border_color" >
			<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
				<thead>
            		<tr >
            			<td height="20" class="TableRow" style="border-top: none;border-left: none;" nowrap colspan="3" align="center">
					 		请选择指标项&nbsp;&nbsp;
            			</td>            	        	        	        
           			</tr>
   	  			</thead>
				<thead>              
		        	<tr>     
		        		<td align="center" class="TableRow" style="border-left: none;" nowrap> 
		         			&nbsp;
		        		</td>
		        		<td align="center" class="TableRow" nowrap>
							序号&nbsp;
		        		</td>  
		        		<td align="center" class="TableRow" nowrap>
							指标名称&nbsp;
		       			</td>  
		        	</tr>                           
		   	    </thead>
				<logic:iterate id="element" name="dailyRegisterForm" property="showlist" indexId="index" >
					<tr>
						<td align="center" class="RecordRow" style="border-left: none;" nowrap>
							<input type="checkbox" name="index" id="<%=id.get(i)%>" value="<bean:write name="element"/>"/>
						</td>
						<td align="center" class="RecordRow" nowrap>
							<%=j %>
						</td>
						<td align="center" class="RecordRow" nowrap>
							<bean:write name="element"/><br>
						</td>
						<%i++; 
						  j++;	
						%>
					</tr>
				</logic:iterate>
			</table>
			</div>
   		</td>
	</tr>
</table>
<table border="0" cellspacing="0" width="300" align="center" cellpadding="0" width="90%" style="margin-top: 10px;">
	<tr>
		<td align="center">    
 			<input type="button" value="全选" class="mybutton" onclick="selectAll()"/>
			<input type="button" value="全撤" class="mybutton" onclick="cancleAll()"/>
			<input type="button" value="确定" class="mybutton" onclick="DownCustomTemp();"/>
			<input type="button" value="取消" class="mybutton" onclick="window.close();"/>
		</td>
	</tr>
</table>
</html:form>