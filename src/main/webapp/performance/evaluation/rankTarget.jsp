<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.performance.evaluation.EvaluationForm,
				 org.apache.commons.beanutils.LazyDynaBean,
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant" %>
<% 
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	EvaluationForm evaluationForm=(EvaluationForm)session.getAttribute("evaluationForm");	
	ArrayList customizeGradeList = evaluationForm.getCustomizeGradeList();
%>

<script type="text/javascript">
<% 
if(request.getParameter("opt")!=null&&request.getParameter("opt").equals("close")){
	out.print("goback();");
}
%>
function saveRankTarget()
{
	var str_1=document.getElementById("codeitem_1").value;
	var str_2=document.getElementById("codeitem_num1").value;	
	var sqlStr_1=document.getElementById("codeitem_2").value;	
	var sqlStr_2=document.getElementById("codeitem_num2").value;	
	
	if((str_1!=null && str_1.length>0) && (sqlStr_1!=null && sqlStr_1.length>0))
	{
		if(str_1==sqlStr_1)
		{
			alert("两组分组指标不能相同！");
			return;
		}	
	}
	var str=(str_1+";"+str_2);
	var sqlStr=(sqlStr_1+";"+sqlStr_2);	
	evaluationForm.grpMenu1.value=str;
	evaluationForm.grpMenu2.value=sqlStr;

	document.evaluationForm.action="/performance/evaluation/performanceEvaluation.do?b_saveRankTarget=link&rangkNum=1&opt=close";
	document.evaluationForm.submit();
}
function colorBlue()
{
		var str_1name=document.getElementById("codeitem_1").value;	  	
		var childrenTemp="${evaluationForm.childrenTemp}";
		var temps=childrenTemp.split(";");
		var daima;
		
		for(var i=0;i<temps.length;i++)
  	    {
  	    	var temp=temps[i].split(",");
  	        if(temp[0]==str_1name)
  	        {
  	        	daima=temp[1];
  	        	break;
  	        }
  	    }		
		if(daima==0 || str_1name=="body_id" || str_1name==null || str_1name.length<=0)	
		{	
			document.getElementById("codeitem_num1").disabled=true;
			document.getElementById("codeitem_num1").value=0;
		}
		else
			document.getElementById("codeitem_num1").disabled=false;	  		  	
	
}
function colorYellow()
{
		var sqlStr_1name=document.getElementById("codeitem_2").value;	  	
		var childrenTemp="${evaluationForm.childrenTemp}";
		var temps=childrenTemp.split(";");
		var daima;
		
		for(var i=0;i<temps.length;i++)
  	    {
  	    	var temp=temps[i].split(",");
  	        if(temp[0]==sqlStr_1name)
  	        {
  	        	daima=temp[1];
  	        	break;
  	        }
  	    }		
		if(daima==0 || sqlStr_1name=="body_id" || sqlStr_1name==null || sqlStr_1name.length<=0)	
		{	
			document.getElementById("codeitem_num2").disabled=true;
			document.getElementById("codeitem_num2").value=0;
		}
		else
			document.getElementById("codeitem_num2").disabled=false;	
		
}
function goback()
{
	if(window.showModalDialog){
		window.close();	
	}else {
		var win = parent.parent.Ext.getCmp('show_pmzb_win');
		if(win) {
			win.close();
		}
	}
}
</script>
<html>
<hrms:themes />
	<head>
		
	</head>
	<body>
		<html:form action="/performance/evaluation/performanceEvaluation">
		<html:hidden name="evaluationForm" property="grpMenu1" />
		<html:hidden name="evaluationForm" property="grpMenu2" />
		<Br>
			<div id="tbl-container"  style='height:70px;width:93%;align:center'>	
				<table border="0" cellspacing="0"  align="center" cellpadding="0" align="center" style="width:93%;">
	          		<tr align="center" > 
	            		<td id="rankTarget_name1">										
							<bean:message key='performance.workdiary.check.zbpmdyz' />:																						
							<html:select name="evaluationForm" property="grpMenu1Name" size="1" styleId="codeitem_1"
								onchange="colorBlue();" style="width:150px">
								<html:option value=""></html:option>
								<html:option value="B0110">所属单位</html:option>
								<html:option value="E0122">所属部门</html:option>
								<html:option value="body_id">对象类别</html:option>
								<%	for(int i=0;i<customizeGradeList.size();i++) 
									{
										LazyDynaBean abean=(LazyDynaBean)customizeGradeList.get(i);
       									String Itemdesc=(String)abean.get("Itemdesc");
       									String Itemdesc_value=(String)abean.get("Itemid");
								%>
								<html:option value="<%=Itemdesc_value %>"><%=Itemdesc %></html:option>
								<%}%>																
							</html:select>															
							&nbsp;&nbsp;&nbsp;										
						</td>
						<td id="rankTarget_1">										
							<bean:message key='performance.workdiary.check.cj' />:	
							<html:select name="evaluationForm" property="grpMenu1Num" size="1" styleId="codeitem_num1" style="width:50px">
								<html:option value="0">0</html:option>
								<html:option value="1">1</html:option>
								<html:option value="2">2</html:option>
								<html:option value="3">3</html:option>
								<html:option value="4">4</html:option>
								<html:option value="5">5</html:option>
								<html:option value="6">6</html:option>
								<html:option value="7">7</html:option>
								<html:option value="8">8</html:option>
								<html:option value="9">9</html:option>
								<html:option value="10">10</html:option>
							</html:select>												
						</td>
	          		</tr>	          			
					<tr>
						<td>
							&nbsp;
	          			</td>
					</tr>					
	        		<tr align="center" > 
	            		<td id="rankTarget_name2">										
							<bean:message key='performance.workdiary.check.zbpmdrz' />:	
							<html:select name="evaluationForm" property="grpMenu2Name" size="1" styleId="codeitem_2"
								onchange="colorYellow();" style="width:150px">
								<html:option value=""></html:option>
								<html:option value="B0110">所属单位</html:option>
								<html:option value="E0122">所属部门</html:option>
								<html:option value="body_id">对象类别</html:option>
								<%	for(int i=0;i<customizeGradeList.size();i++) 
									{
										LazyDynaBean abean=(LazyDynaBean)customizeGradeList.get(i);
       									String Itemdesc=(String)abean.get("Itemdesc");
       									String Itemdesc_value=(String)abean.get("Itemid");
								%>
								<html:option value="<%=Itemdesc_value %>"><%=Itemdesc %></html:option>
								<%}%>
							</html:select>	
							&nbsp;&nbsp;&nbsp;										
						</td>
						<td id="rankTarget_2">										
							<bean:message key='performance.workdiary.check.cj' />:	
							<html:select name="evaluationForm" property="grpMenu2Num" size="1" styleId="codeitem_num2" style="width:50px">
								<html:option value="0">0</html:option>
								<html:option value="1">1</html:option>
								<html:option value="2">2</html:option>
								<html:option value="3">3</html:option>
								<html:option value="4">4</html:option>
								<html:option value="5">5</html:option>
								<html:option value="6">6</html:option>
								<html:option value="7">7</html:option>
								<html:option value="8">8</html:option>
								<html:option value="9">9</html:option>
								<html:option value="10">10</html:option>
							</html:select>											
						</td>
	          		</tr>
				</table>	                         		           
		    </div>	
		    <table border="0" cellspacing="0"  align="center" cellpadding="0" style="padding-top: 10px;">
          		<tr align="center" > 
            		<td>
					    <input type="button" value="确定" id="b_ok" class="mybutton" onclick="saveRankTarget();" />
					    &nbsp;&nbsp;
					    <input type="button" value="取消" id="b_cansal" class="mybutton" onclick="goback();" />
					</td>
          		</tr>	  
        	</table>
        	<script>
        		
        		var str_1name=document.getElementById("codeitem_1").value;	  	
				var childrenTemp="${evaluationForm.childrenTemp}";
				var temps=childrenTemp.split(";");
				var daima;
				
				for(var i=0;i<temps.length;i++)
		  	    {
		  	    	var temp=temps[i].split(",");
		  	        if(temp[0]==str_1name)
		  	        {
		  	        	daima=temp[1];
		  	        	break;
		  	        }
		  	    }		
				if(daima==0 || str_1name=="body_id" || str_1name==null || str_1name.length<=0)
				{	
					document.getElementById("codeitem_num1").disabled=true;
					document.getElementById("codeitem_num1").value=0;
				}
				else
					document.getElementById("codeitem_num1").disabled=false;
        		
        		var sqlStr_1name=document.getElementById("codeitem_2").value;	  	
				var childrenTemp="${evaluationForm.childrenTemp}";
				var temps=childrenTemp.split(";");
				var daima;
				
				for(var i=0;i<temps.length;i++)
		  	    {
		  	    	var temp=temps[i].split(",");
		  	        if(temp[0]==sqlStr_1name)
		  	        {
		  	        	daima=temp[1];
		  	        	break;
		  	        }
		  	    }		
				if(daima==0 || sqlStr_1name=="body_id" || sqlStr_1name==null || sqlStr_1name.length<=0)
				{	
					document.getElementById("codeitem_num2").disabled=true;
					document.getElementById("codeitem_num2").value=0;
				}
				else
					document.getElementById("codeitem_num2").disabled=false;
        		
        	</script>
	   </html:form>								
   </body>
</html>

