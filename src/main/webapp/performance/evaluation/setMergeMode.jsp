<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<script type="text/javascript">

function saveMergeMode()
{		
	var mergeModePrams="";
	if(document.getElementById("average").checked==true)	
		mergeModePrams=",average";	
	if(document.getElementById("b_sum").checked==true)	
		mergeModePrams+=",sum";	
	if(document.getElementById("b_max").checked==true)	
		mergeModePrams+=",max";	
	if(document.getElementById("b_min").checked==true)	
		mergeModePrams+=",min";
	
	evaluationForm.mergeModePrams.value=mergeModePrams;	
	evaluationForm.submit();
}

function goback()
{
	window.close();	
}
</script>
<html>
<hrms:themes />
	<head>
		
	</head>
	<body>
		<html:form action="/performance/evaluation/performanceEvaluation">
		<html:hidden name="evaluationForm" property="mergeModePrams" styleId="mergeModePrams"/>
			<br>
			&nbsp;&nbsp;<bean:message key='jx.evaluation.performanceMergeModeTrans'/>
			<br/>
			<br/>
			<fieldset align="center" style="width:93%;">	
				<table border="0" cellspacing="0"  align="left" cellpadding="0" >
	          		<tr height="30px"> 
	            		<td  align="center" width="120px">			
							<input type="checkbox" id="average" onclick="" checked/>&nbsp;平均值											
						</td>
						<td  align="center" width="79px">			
							<input type="checkbox" id="b_sum" onclick="" />&nbsp;求和												
						</td>
	          		</tr>	  
	        		<tr height="30px"> 
	            		<td  align="center" width="120px">													
							<input type="checkbox" id="b_max" onclick="" />&nbsp;最大值
						</td>
						<td  align="center" width="90px">			
							<input type="checkbox" id="b_min" onclick="" />&nbsp;最小值												
						</td>
	          		</tr>
				</table>	                         		           
		    </fieldset>	
		    <br/>
		    <br/>
		    <table border="0" cellspacing="0"  align="center" cellpadding="0" >
          		<tr align="center" > 
            		<td>
					    <input type="button" value="确定" id="b_ok" class="mybutton" onclick="saveMergeMode();goback();" />
					    &nbsp;&nbsp;
					    <input type="button" value="取消" id="b_cansal" class="mybutton" onclick="goback();" />
					</td>
          		</tr>	  
        	</table>
        	<script>
        		
        	</script>
	   </html:form>								
   </body>
</html>