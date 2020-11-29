<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript" src="/js/common.js"></script>
<hrms:themes />
<%@ page import="java.util.*,
				 com.hrms.struts.valueobject.UserView,				 
				 com.hrms.struts.constant.WebConstant,
				 com.hrms.struts.constant.SystemConfig,
                 com.hjsj.hrms.actionform.performance.evaluation.CalcRuleForm,
                 org.apache.commons.beanutils.LazyDynaBean" %>
                 
<%
	UserView userView = (UserView) session.getAttribute(WebConstant.userView);
	int versionFlag = 1;
	if (userView != null)
		versionFlag = userView.getVersion_flag(); // 1:专业版 0:标准版

	CalcRuleForm crf=(CalcRuleForm)session.getAttribute("calcRuleForm");	
	String throwHighCount=crf.getThrowHighCount();
	String planid=(String)request.getParameter("planid");
	String planstatus=(String)request.getParameter("planStatus");
	String throwLowCount=crf.getThrowLowCount();
	String keepDecimal=crf.getKeepDecimal();
	String estBodyText_value=crf.getEstBodyText_value();
	String throwBaseNum = crf.getThrowBaseNum();
	if(estBodyText_value.length()>0)
		estBodyText_value=","+estBodyText_value;
	String useKnow=crf.getUseKnow();	
	String knowText_value=crf.getKnowText_value();
	if(knowText_value.length()>0)
		knowText_value=","+knowText_value;
	String useWeight=crf.getUseWeight();
	String appUseWeight=crf.getAppUseWeight();
	String wholeEval = crf.getWholeEval();
	String method=crf.getMethod();
	String isvalidate=crf.getIsvalidate();
	String code = crf.getCode();
	String 	isShowValPrecision=crf.getIsShowValPrecision();
	
	String clientName="";
	if(SystemConfig.getPropertyValue("clientName")!=null)
  		clientName=SystemConfig.getPropertyValue("clientName").trim();
	String str="none";
	if(throwHighCount==null||throwHighCount.trim().length()==0)
		throwHighCount="0";
	if(throwLowCount==null||throwLowCount.trim().length()==0)
		throwLowCount="0";
	
	String _pointScoreFromKeyEvent=crf.getPointScoreFromKeyEvent();
	if(_pointScoreFromKeyEvent.equalsIgnoreCase("true"))
		_pointScoreFromKeyEvent=" checked ";
	String showRule="cal";
	if(crf.getShowRule()!=null)
		showRule=crf.getShowRule();
	
	if(Integer.parseInt(throwHighCount)>0||Integer.parseInt(throwLowCount)>0)
 	{
 		str="block";
    }
 
	
 %>
 <script language="JavaScript" src="evaluation.js"></script>
 	<script language="JavaScript" src="/ajax/control.js"></script>
 	<script language="JavaScript" src="/js/validate.js"></script>
	<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language='javascript' >
var showRule='<%=showRule%>';
<%
String callbackFunc = request.getParameter("callbackFunc");
%>

function goback(flag)
{
	var thevo=new Object();
	thevo.flag=flag;
	if(window.showModalDialog){
		parent.window.returnValue = thevo;
		parent.window.close();
	}else{
		<%
			if(callbackFunc != null && callbackFunc.length() > 0) {
		%>
			parent.parent.<%=callbackFunc%>(thevo);
		<%}%>
		var win = parent.parent.Ext.getCmp('defineRule_win');
   		if(win) {
    		win.close();
   		}
	}
}	

function checkGrade()
{
	if(document.getElementById('checkInvalidGrade'))
	{	
		var flag = document.getElementById('checkInvalidGrade').checked;
		if(flag==true)		
			document.getElementById('invalidGradeSelect').disabled=false;					
		else		
			document.getElementById('invalidGradeSelect').disabled=true;	
	}	  	
}
	function test(name)
	{
		if(name=="select")
		{			
			var obj=$('pageset');			
			obj.setSelectedTab("tab9");
		}
	}
	
</script>

<style>

.calculateStyle 
{
   	/*
   	BACKGROUND-COLOR:#F7FAFF;
   	*/
   	BORDER-BOTTOM: #C4D8EE 0pt solid; 
   	BORDER-LEFT: #C4D8EE 0pt solid; 
   	BORDER-RIGHT: #C4D8EE 0pt solid; 
   	BORDER-TOP: #C4D8EE 0pt solid; 
}
</style>

<html>
  <head>
  
  </head>
  <%
	String operate=null;
	if(request.getParameter("operate")!=null)
		   operate=request.getParameter("operate");
%>	
  <body  <%=(operate!=null?"onload=\"test('"+operate+"')\"":""  )%> >
	<html:form action="/performance/evaluation/calculate">  
   <table style="width:100%">
   <tr><td>
   
   <hrms:tabset name="pageset" width="480" height="290" type="false"> 
	<logic:equal name="calcRuleForm" property="isShowValPrecision" value="false">
	<hrms:tab name="tab3" label="jx.evaluation.mainProportion" visible="true">
	 <table width="100%" height='100%' align="center"> 
	 
		<tr> <td class="calculateStyle" valign="top" align="center"><Br>
			<table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">    
			<tr>
			<td>               	
				<fieldset align="center" style="width:95%;height:150">
	    		<Br>
			    	<table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
			        	<tr>
					    	<td width="70%" height="30" >
					        	<input type='radio' name='useWeight'  onclick='setValue(this)'    <% if(useWeight.equalsIgnoreCase("true")) out.print("checked"); %>  value='True'  ><bean:message key="jx.evaluation.makeProportion"/>
					                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type='button'   value='<bean:message key="jx.evaluation.proportionSet"/>' name='sp'  <% if(useWeight.equalsIgnoreCase("0")) out.print("disabled"); %>   onclick='setWeight()'  class="mybutton"  >
					        </td>
					        <td>
					            &nbsp;
					        </td>					                					
					    </tr>
					                			
					    <tr>
					    	<td width="70%" height="30" >
					        	<span id='theAppUseWeight' >&nbsp;&nbsp;&nbsp;&nbsp;<input type='checkbox'  <% if(useWeight.equalsIgnoreCase("false")) out.print("disabled"); %>  <% if(appUseWeight.equalsIgnoreCase("1")) out.print("checked"); %>   name='appUseWeight'  value='1'  ><bean:message key="jx.evaluation.hasProportion"/>
					            </span>
					        </td>
					        <td>
					            &nbsp;
					        </td>					                					
					    </tr>
					                			
					    <tr>		
					        <td width="70%" height="30" >
					        	<input type='radio' name='useWeight'  onclick='setValue(this)'   <% if(useWeight.equalsIgnoreCase("false")) out.print("checked"); %>  value='False'  ><bean:message key="jx.evaluation.notProportion"/>					                					
					        </td>
					        <td>
					            &nbsp;
					        </td>
					    </tr>				                			
			    	</table>			  
			   </fieldset>
			</td>
			</tr>
			</table>
		               
			</td></tr>
	 </table>
	</hrms:tab>
	
	<hrms:tab name="tab1" label="jx.evaluation.doffNumber" visible="true">
	 <table width="100%" height='100%' align="center"> 
	 
		<tr> <td class="calculateStyle" valign="top"><Br>
			<table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">    
			<tr>
			<td>               	
							<fieldset align="center" style="width:90%;">
	    						<table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
			                        <tr><td width="90%" height="25" >
					                	<table><tr>
					                	<td>&nbsp;<bean:message key="jx.evalution.bodycountbigger"/></td>
					                	<td valign="middle"> 
										<input type="text" id="throwBaseNum" name="throwBaseNum" onkeypress="event.returnValue=IsDigit(this);" onchange='validateData(this)'  value="<%=throwBaseNum%>" style="width:30" maxlength="2">                     
										</td>
										<td valign="middle" align="left">
											<table border="0" cellspacing="2" cellpadding="0">
												<tr><td><button type="button" id="m_up" class="m_arrow" onclick="mincrease1('throwBaseNum');">5</button></td></tr>
												<tr><td><button type="button" id="m_down" class="m_arrow" onclick="msubtract1('throwBaseNum');">6</button></td></tr>
											</table>		
										</td>
										<td><bean:message key="jx.evalution.hour"/></td>
										</tr></table>
				                	</td></tr>
			                      	<tr><td width="90%" height="25" >
					                	<table><tr>
					                	<td>&nbsp;<bean:message key="jx.evaluation.doff"/></td>
					                	<td valign="middle"> 
										<input type="text" id="throwHighCount" name="throwHighCount" onkeypress="event.returnValue=IsDigit(this);" onmousemove='validateData(this)'  value="<%=throwHighCount%>" style="width:100" maxlength="2">                     
										</td>
										<td valign="middle" align="left">
											<table border="0" cellspacing="2" cellpadding="0">
												<tr><td><button type="button" id="m_up" class="m_arrow" onclick="mincrease1('throwHighCount');">5</button></td></tr>
												<tr><td><button type="button" id="m_down" class="m_arrow" onclick="msubtract1('throwHighCount');">6</button></td></tr>
											</table>		
										</td>
										<td><bean:message key="jx.evaluation.heightScore"/></td>
										</tr></table>
				                	</td></tr>
			                		<tr><td width="90%" height="25" >
				                		<table><tr>
					                	<td>&nbsp;<bean:message key="jx.evaluation.doff"/></td>
					                	<td valign="middle"> 
											<input type="text" id="throwLowCount" name="throwLowCount" onkeypress="event.returnValue=IsDigit(this);" onmousemove='validateData(this)'  value="<%=throwLowCount%>" style="width:100" maxlength="2">                     
										</td>
										<td valign="middle" align="left">
											<table border="0" cellspacing="2" cellpadding="0">
												<tr><td><button type="button" id="m_up" class="m_arrow" onclick="mincrease1('throwLowCount');">5</button></td></tr>
												<tr><td><button type="button" id="m_down" class="m_arrow" onclick="msubtract1('throwLowCount');">6</button></td></tr>
											</table>		
										</td>
										<td><bean:message key="jx.evaluation.lowerScore"/></td>
										</tr></table>	
				                	</td></tr>	
				                	<tr><td width="90%" height="25" >
				                		<table><tr>
					                	<td>&nbsp;<bean:message key="jx.evaluation.resultRemain"/></td>
					                	<td valign="middle"> 
											<input type="text" id="keepDecimal" name="keepDecimal" onkeypress="event.returnValue=IsDigit(this);" onchange='validateData(this)'   value="<%=keepDecimal%>" style="width:52" maxlength="2">                     
										</td>
										<td valign="middle" align="left">
											<table border="0" cellspacing="2" cellpadding="0">
												<tr><td><button type="button" id="m_up" class="m_arrow" onclick="mincrease1('keepDecimal');">5</button></td></tr>
												<tr><td><button type="button" id="m_down" class="m_arrow" onclick="msubtract1('keepDecimal');">6</button></td></tr>
											</table>		
										</td>
										<td><bean:message key="jx.evaluation.dicimal"/></td>
										</tr></table>  		
				                	</td></tr>		
			                   </table>
			  
			                 </fieldset>
			                 <br> <br>
			                 <div id='dropValue'  style="display:<%=str%>" >
			                 <fieldset align="center" style="width:90%;">
			                 <legend ><bean:message key="jx.evaluation.doffRule"/></legend>
	    						<table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
			                      	<logic:iterate id="element"  name="calcRuleForm" property="bodySetList"  >
				                      		<tr><td>
				                      		<%
				                      			LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
				          						String body_id=(String)abean.get("body_id");
				                      		 %>
				                      		
				                      		&nbsp;<input type='checkbox' name='estBodyText' <% if(estBodyText_value.indexOf(","+body_id)!=-1){ out.print("checked"); } %>   value='<bean:write name="element" property="body_id" filter="true"/>'  /><bean:message key="kq.rule.from"/><bean:write name="element" property="name" filter="true"/><bean:message key="jx.evaluation.doffBester"/>
				                      		</td></tr>
			                 		</logic:iterate>
			                 	</table>
			                 </fieldset>
			                 </div>
			                 
			                 
			                 
			 </td>
			 </tr>
			 </table>
		               
			</td></tr>
	 </table>
	</hrms:tab>
	
	<logic:equal name="calcRuleForm" property="nodeKnowDegree"  value="true"  >
	
	<hrms:tab name="tab2" label="jx.evaluation.statScope" visible="true">
	 <table width="100%" height='100%' align="center"> 
	 
		<tr> <td class="calculateStyle" valign="top"><Br>
			<table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">    
			<tr>
			<td>               	
							<fieldset align="center" style="width:90%;height:150">
	    								<br>
			                      			<table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
			                      				<% int i=0; %>
			                      				<logic:iterate id="element"  name="calcRuleForm" property="knowList"  >
				                      				<tr>
					                					<td width="20%" height="25" align='left' >
					                					<% if(i==0){ %>
					                						&nbsp;&nbsp;<input type='checkbox' name='useKnow'  id='useKnow' onclick="validateBox(this)" <% if(useKnow.equalsIgnoreCase("false")){ out.print("checked"); } %>   value='1'><bean:message key="gz.gz_analyse.gz_setinfor.all"/>
					                					<% }else{%>					                					
					                					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					                					<%}
					                					   i++;
					                					   
					                					   LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
				          								   String know_id=(String)abean.get("know_id");
					                					   
					                					 %>
					                					
					                					</td>
					                					<td align='left' >
					                					&nbsp;&nbsp;<input type='checkbox' name='knowText' onclick="validateBox(this)"   <% if(knowText_value.indexOf(","+know_id)!=-1){ out.print("checked"); } %>     value='<bean:write name="element" property="know_id" filter="true"/>'>
					                					&nbsp;&nbsp;<bean:message key="kq.wizard.stas"/>&nbsp;<bean:write name="element" property="name" filter="true"/>&nbsp;<bean:message key="jx.evaluation.mainbodyGrade"/>
					                					</td>
					                				</tr>
				                				</logic:iterate>
			                      			</table>
			  
			                 </fieldset>
			 </td>
			 </tr>
			 </table>
		               
			</td></tr>
	 </table>
	</hrms:tab>
	
	</logic:equal>
</logic:equal>
<logic:equal name="calcRuleForm" property="isShowValPrecision" value="true">
   <hrms:tab name="tab4" label="jx.eval.calcu.valuePrecision" visible="true">
		<table><tr>
					                	<td><bean:message key="jx.evaluation.resultRemain"/></td>
					                	<td valign="middle"> 
											<input type="text" id="keepDecimal" name="keepDecimal"  onchange='validateData(this)'   value="<%=keepDecimal%>" style="width:52" maxlength="2">                     
										</td>
										<td valign="middle" align="left">
											<table border="0" cellspacing="2" cellpadding="0">
												<tr><td><button id="m_up" class="m_arrow" onclick="mincrease1('keepDecimal');">5</button></td></tr>
												<tr><td><button id="m_down" class="m_arrow" onclick="msubtract1('keepDecimal');">6</button></td></tr>
											</table>		
										</td>
										<td><bean:message key="jx.evaluation.dicimal"/></td>
										</tr></table>  
   </hrms:tab>
</logic:equal> 
<logic:equal name="calcRuleForm" property="showRange" value="true">

<% if(versionFlag==1){ %>
<logic:notEqual name="calcRuleForm" property="byModel" value="1">
	<hrms:tab name="tab9" label="指标分值范围" visible="true">
		 <table width="100%" height='100%' align="center"> 
		 
			<tr> <td class="calculateStyle" valign="top"><Br>
				<table width="100%"  border="0" cellpmoding="0" cellspacing="0"   class="ListTable"  cellpadding="0">    
				<tr>
				<td  colspan="4" style="line-height:0px;">               	
								<fieldset align="center" style="width:97%;height:200px;padding:0px;overflow-y:scroll;overflow-x:hidden">
		    						<table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
				                        <tr>
					                        <td width="10%" style="border-left:none;" class='TableRow'align="center" style="border-left:0;border-top:0;" height="25" >
							                	序号		
						                	</td>
						                	<td  class='TableRow' align="center" style="border-top:0;border-left:0;">
						                	指标名称
						                	</td>
						                	<td  class='TableRow' align="center" style="border-top:0;border-left:0;">
						                		上限分值
						                	</td>
						                	<td  class='TableRow' align="center" style="border-top:0;border-left:0;">
						                		下限分值
						                	</td>
					                	</tr>
					                	<% int k=0; %>	
					                	<logic:iterate id="element" name="calcRuleForm" property="rangelist">
					                	<%k++; %>
					                		<tr>
					                		<td align="center" style="border-left:none;" class='RecordRow'  style=";border-top:0;">
					                			<%=k %>
					                		</td>
					                		<td class='RecordRow' style="border-left:0;border-top:0;">
					                			<bean:write name='element' property='pointname' filter='true' />&nbsp;
					                		</td>
					                		<td align="right"  class='RecordRow'  style="border-left:0;border-top:0;">
					                			<bean:write name='element' property='maxscore' filter='true' />&nbsp;
					                		</td>
					                		<td align="right"  class='RecordRow' style="border-left:0;border-top:0;">
					                			<bean:write name='element' property='minscore' filter='true' />&nbsp;
					                		</td>
					                		</tr>
					                	</logic:iterate>
				                   </table>
				  
				                 </fieldset>
				                 <br> 			          
				 </td>
				 </tr>
				 <tr>
				 <td  colspan="2" align='left' nowrap style="padding-top:8px;">
				 
						<input type='checkbox'<%if(isvalidate!=null&&isvalidate.equalsIgnoreCase("true")){%>checked<%} %> id='wer' > 有效性
				 </td>
				 <td colspan="2" align='right' style="padding-top:8px;">
				 	<input type='button' class='MyButton' onclick='rulefanwei("<%=planid %>","<%=planstatus %>");' value='定义'>
				 </td>
				 </tr>
				 </table>
			               
				</td></tr>
		 </table>
	</hrms:tab>
</logic:notEqual>
<% } %>
</logic:equal>

   
</hrms:tabset>
   
   </td>
   <td valign='top' ><br>
   <html:hidden name="calcRuleForm" property="isvalidate"/>
  &nbsp; &nbsp;<input style="margin-top:10px;" type='button' id="b_ok"  value='<bean:message key="lable.tz_template.enter"/>' onclick='sub()'  class="mybutton"  >
   <br>
   <br>
  &nbsp;&nbsp; <input type='button' id="b_cancel" value='<bean:message key="lable.tz_template.cancel"/>' onclick='goback("false")'  class="mybutton"  >
    
   </td>
   </tr>
   <tr>
   	<td colspan='2'>
   	<logic:equal name="calcRuleForm" property="isShowScoreFromKey" value="1">
   	<input type='checkbox' id='pointScoreFromKeyEvent'  <%=_pointScoreFromKeyEvent%> value="True" >指标评分优先取自关键事件
   	<br>
   	</logic:equal>
   	   	
 <!--  	<input type='checkbox' id='calcuAgain'><bean:message key="performance.evalution.calcu.info"/>   zhaoxg  2016-1-15 屏蔽掉，以免误操作后不可还原--> 	   	   	
   	
   	<br>
   <% { 
   
   		String checkInvalidGrade=crf.getCheckInvalidGrade();
   		String checked="";
   		if(checkInvalidGrade!=null&&checkInvalidGrade.equalsIgnoreCase("True"))
   			checked="checked";
   %>
   	<input type='checkbox' id='checkInvalidGrade' <%=checked%> value="True" onclick='checkGrade()'>选<html:select name="calcRuleForm" property="invalidGrade" styleId="invalidGradeSelect" size="1"  style="width:300px">
  				 <html:optionsCollection property="gradeList" value="dataValue" label="dataName"/>
				</html:select> 
   	作无效票处理
   		
   	<% } %>
   	<br>
   	<%
   		String zeroByNull=crf.getZeroByNull();
		String checked="";
		if(zeroByNull!=null&&zeroByNull.equalsIgnoreCase("true"))
			checked="checked";
   	%>
   	<input type='checkbox' value="True" id='zeroByNull'   <%=checked%>><bean:message key="performance.evalution.calcu.zeroByNull"/>
   	</td>
   </tr>
   </table>
<div id='wait' style='position:absolute;top:160;left:250;display:none;'>
  <table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>
             <td id="wait_desc" class="td_style" height=24>正在计算，请稍候....</td>
           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center>
               <marquee class="marquee_style" direction="right" width="260" scrollamount="5" scrolldelay="10" >
                 <table cellspacing="1" cellpadding="0">
                   <tr height=8>
                     <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                    </tr>
                  </table>
               </marquee>
             </td>
          </tr>
        </table>
</div>
   
   
   </html:form>
  
  </body>
  <script language='javascript' >
  checkGrade();
  if('${calcRuleForm.planStatus}'=='7')
  {
  		document.getElementById('b_ok').disabled="disabled";
  		var weight = document.getElementsByName('useWeight');  	
  		for(var i=0;i<weight.length;i++)
  			weight[i].disabled="disabled";
  			
  		var inputObj = document.getElementsByTagName('input');  	
  		for(var i=0;i<inputObj.length;i++)
  			inputObj[i].disabled="disabled";	
  			
  	    inputObj = document.getElementsByTagName('button');  	
  		for(var i=0;i<inputObj.length;i++)
  			inputObj[i].disabled="disabled";		
  		document.getElementById('b_cancel').disabled=false;
  }
  if('${calcRuleForm.zeroflag}'=='0'){
	  document.getElementById('zeroByNull').disabled=true;
  }
  <%
  if(useWeight.equalsIgnoreCase("false")&& isShowValPrecision.equalsIgnoreCase("false"))
  {
  %>
  document.calcRuleForm.sp.disabled=true;
  <%}%>
  <% 
  //if(request.getParameter("b_calculate")!=null&&request.getParameter("b_calculate").equals("cal"))
  		//out.print("window.close();");
  if(wholeEval.equalsIgnoreCase("false"))
  {
  %>
  	if(document.getElementById('theAppUseWeight')!=null)
  		document.getElementById('theAppUseWeight').style.display='none';  	
  <%}%>
  function setValue(obj)
  {
  	if(obj.value=='True')
  	{
  		document.calcRuleForm.appUseWeight.disabled=false;
  		document.calcRuleForm.sp.disabled=false;
  	}
  	if(obj.value=='False')
  	{
  		document.calcRuleForm.appUseWeight.disabled=true;
  		document.calcRuleForm.sp.disabled=true;
  		document.calcRuleForm.appUseWeight.checked=false;
  	}
  }
  
function validateData(obj)
{
	var strP=/^\d+(\.\d+)?$/;
  	if(!strP.test(trim(obj.value)))
  	{
  		alert(INPUT_POSITIVE_VALUE);
  		obj.value="0";
  		return;
  	}
  	var isShowValPrecision='${calcRuleForm.isShowValPrecision}';
  	
  	if(isShowValPrecision!='true')
  	{
	  	if(obj.value!=0)
	  		if(dropValue.style.display=='none')
				dropValue.style.display='block'; 	
		if(document.calcRuleForm.throwHighCount.value==0 && document.calcRuleForm.throwLowCount.value==0)
			dropValue.style.display='none';
	}
}
  
  
  function mincrease1(obj_name)
  {
  		var obj=eval("document.calcRuleForm."+obj_name);
  		//xus 19/12/13 【55723】V7.6.2绩效管理：绩效评估，计算规则，手动输入最多只能输入两位数，按钮可以大于两位数，滚动条也去掉，见附件
  		if(obj.value<99)
			obj.value = obj.value*1+1;
		if(obj_name!='keepDecimal' && obj_name!='throwBaseNum')
			if(dropValue.style.display=='none')
				dropValue.style.display='block';
  }
  
  function msubtract1(obj_name){
  		var obj=eval("document.calcRuleForm."+obj_name);
   		if(obj.value>0)
			obj.value = obj.value*1-1;
	if(obj_name!='keepDecimal' && obj_name!='throwBaseNum')		
		if(document.calcRuleForm.throwHighCount.value==0&&document.calcRuleForm.throwLowCount.value==0)
			dropValue.style.display='none';
  }
  
  
  function setWeight()
  {
  		var zerovalue="false";
  		var obj = document.getElementById("zeroByNull");
  		if(obj!=null){
  			if(obj.checked)
  			{
  				zerovalue="true";
  			}
  		}
	    var strurl="/performance/evaluation/calculate.do?b_setProPortion=show`planid=${calcRuleForm.planid}`zeroByNull="+zerovalue;
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
	    
	    var config = {
    	    width:600,
    	    height:440,
    	    type:'2'
    	}

    	modalDialog.showModalDialogs(iframe_url,"setWeight_win",config,setWeight_ok);
		//var ss=window.showModalDialog(iframe_url,arguments,"dialogWidth=600px;dialogHeight=440px;resizable=yes;scroll=no;status=no;");

  }
  function setWeight_ok(ss) {
	  if(ss!=null){
		var zeroflag = ss;
		if(zeroflag!=null&&zeroflag=="0")
			document.getElementById("zeroByNull").disabled=true;
		else
			document.getElementById("zeroByNull").disabled=false;
	  }
  }
  
  
  function validateBox(obj)
  {
  	if(obj==null)
  		return;
  	if(obj.name=='useKnow')
  	{
  		if(obj.checked)//选中所有 后面的了解程度项目就都不选
  		{
  			var obj_arr=eval("document.calcRuleForm.knowText");
  			for(var i=0;i<obj_arr.length;i++)
  					obj_arr[i].checked=false;
  		
  		}
  	
  	}
  	else if(obj.name=='knowText')
  	{
  		if(obj.checked)
  		{
  			var obj_arr=eval("document.calcRuleForm.useKnow");
  			obj_arr.checked=false;
  		}
  		
  		var obj_arr=eval("document.calcRuleForm.knowText");
  		var num=0;
  		for(var i=0;i<obj_arr.length;i++)
  		{
  			if(obj_arr[i].checked==false)
  				num++;
  		}
  		if(num==obj_arr.length)//全不选 就选中所有
  		{
  			var obj_arr=eval("document.calcRuleForm.useKnow");
  			obj_arr.checked=true;
  		}else
  		{
  			var obj_arr=eval("document.calcRuleForm.useKnow");
  			obj_arr.checked=false;
  		}	
  	}
  }
  function jinduo(){
 	var x=document.body.scrollLeft+100;
    var y=document.body.scrollTop+70; 
	var waitInfo;
	waitInfo=eval("wait");	
	waitInfo.style.top=y;
	waitInfo.style.left=x;	
	waitInfo.style.display="block";
}
  function sub()
  {
  		if('${calcRuleForm.nodeKnowDegree}'=='true')//保存时候要判断必须选中一个了解程度
  		{ 
  			 for(var i=0;i<document.calcRuleForm.elements.length;i++)	
  				if(document.calcRuleForm.elements[i].type=='checkbox' && document.calcRuleForm.elements[i].name=='knowText' && document.calcRuleForm.elements[i].checked)			
  				{
  					continue;
  				}	
  		}
  
  
  
  		for(var i=0;i<document.calcRuleForm.elements.length;i++)
  		{
  			if(document.calcRuleForm.elements[i].type=='checkbox'&&document.calcRuleForm.elements[i].checked==false && document.calcRuleForm.elements[i].id!='calcuAgain' && document.calcRuleForm.elements[i].id!='zeroByNull' && document.calcRuleForm.elements[i].id!='checkInvalidGrade')
  			{
  				if(document.calcRuleForm.elements[i].id=='wer'||document.calcRuleForm.elements[i].id=='pointScoreFromKeyEvent'){
  				continue;
  				}
  				document.calcRuleForm.elements[i].value='-1';
  				document.calcRuleForm.elements[i].checked=true;
  			}
  		}  		
  		document.getElementById("b_ok").disabled=true;
  		if(showRule=='cal')
	  		jinduo();
  		if(document.getElementById('calcuAgain') && document.getElementById('calcuAgain').checked)
  		{
  			var hashvo=new ParameterSet();
			hashvo.setValue("plan_id",'${calcRuleForm.planid}');
		//	hashvo.setValue("isvalidate",tem);
			var request=new Request({method:'post',asynchronous:true,onSuccess:compute,functionId:'9024000012'},hashvo);
  		}
  		else
  		{
  			//document.calcRuleForm.action="/performance/evaluation/calculate.do?b_calculate=cal";
  			//document.calcRuleForm.submit();
  			compute2();
  		}
  		
  }
  function compute(outparamters)
  {		
  		//document.calcRuleForm.action="/performance/evaluation/calculate.do?b_calculate=cal";
  		//document.calcRuleForm.submit();
  		compute2();
  }
  ///计算评估结果
   function compute2()
   {   
   		var isShowValPrecision='${calcRuleForm.isShowValPrecision}';
   		
   		var validate=document.getElementById('wer');
		var tem="false";
		if(validate && validate.checked){
			var tem="true";
		}else{
		
		}		
   		var hashvo=new ParameterSet();
		hashvo.setValue("isShowValPrecision",isShowValPrecision);
		hashvo.setValue("planid",'${calcRuleForm.planid}');
  		if(isShowValPrecision=='false')
  		{
  			var weight = document.getElementsByName('useWeight'); 
   			var theWeight='0'; 	
  			if(weight!=null && weight[0].checked)
  				theWeight='1'; 
 
   			var theUseKnow='False'; 
   			var obj_arr=eval("document.calcRuleForm.useKnow");
   			  	 
  	   	 	var KnowText_value = '';
  	    	var m=0;
  			for(var i=0;i<document.calcRuleForm.elements.length;i++)
  			{
  				if(document.calcRuleForm.elements[i].type=='checkbox' && document.calcRuleForm.elements[i].name=='knowText')
  				{  				
  					if(document.calcRuleForm.elements[i].value!='-1')
  					{
  						KnowText_value+=','+document.calcRuleForm.elements[i].value;  			
  						m++;
  					}	
  				}
  			}  	 
 
  			if(m>0)//如果后面了解程度条目选中 所有不选就是True 其余都是False
  			{
  			 	if(obj_arr.checked && obj_arr.value=='-1')
   			  	 	theUseKnow='True'; 
  			}  		
  		
  			var appUseWeight1='0';
  			if(document.calcRuleForm.appUseWeight!=null && document.calcRuleForm.appUseWeight.value!='-1') 
  				 appUseWeight1='1'; 			 
  		
  			var EstBodyText_value="";	
  			if(parseFloat(document.getElementById('throwHighCount').value)>0||parseFloat(document.getElementById('throwLowCount').value)>0)
			{
				for(var i=0;i<document.calcRuleForm.elements.length;i++)
  				{
					if(document.calcRuleForm.elements[i].type=='checkbox' && document.calcRuleForm.elements[i].name=='estBodyText')
  					{
  						if(document.calcRuleForm.elements[i].value!='-1')
  							EstBodyText_value+=','+document.calcRuleForm.elements[i].value;  				
  					}	
  				}			
			}
			
			var _pointScoreFromKeyEvent="False";
			if(document.getElementById("pointScoreFromKeyEvent"))
			{
				if(document.getElementById("pointScoreFromKeyEvent").checked)
				{ 
					 
					_pointScoreFromKeyEvent="True";
				}
			}
			hashvo.setValue("pointScoreFromKeyEvent",_pointScoreFromKeyEvent);
			hashvo.setValue("useWeight",theWeight);
			hashvo.setValue("throwBaseNum",document.getElementById('throwBaseNum').value);
			hashvo.setValue("throwHighCount",document.getElementById('throwHighCount').value);
			hashvo.setValue("throwLowCount",document.getElementById('throwLowCount').value);
			hashvo.setValue("keepDecimal",document.getElementById('keepDecimal').value);
			hashvo.setValue("KnowText_value",KnowText_value);
			hashvo.setValue("useKnow",theUseKnow);
			hashvo.setValue("appUseWeight",appUseWeight1);
			hashvo.setValue("EstBodyText_value",EstBodyText_value);		
			hashvo.setValue("nodeKnowDegree",'${calcRuleForm.nodeKnowDegree}');
			hashvo.setValue("isvalidate",tem);
			if(document.getElementById('checkInvalidGrade')&&document.getElementById('checkInvalidGrade').checked)
			{
				hashvo.setValue("checkInvalidGrade","True");		
			    hashvo.setValue("invalidGrade",document.getElementsByName('invalidGrade').value);	
			}
			else
			{
				hashvo.setValue("checkInvalidGrade","False");		
			    hashvo.setValue("invalidGrade",'');
			}
			if(document.getElementById('zeroByNull')&&document.getElementById('zeroByNull').checked)
			{
				hashvo.setValue("zeroByNull","true");
			} else 
			{
				hashvo.setValue("zeroByNull","false");
			}
			hashvo.setValue("appUseWeight",appUseWeight1);
			hashvo.setValue("EstBodyText_value",EstBodyText_value);	
			
  		}else if(isShowValPrecision=='true')//对于计划参数中设置了 打分按加扣分处理 且 评估打分允许新增考核指标 这两个参数的计划计算时候只是设置一个参数
   		{
   		
   			hashvo.setValue("keepDecimal",document.getElementById('keepDecimal').value);
   		}
   		hashvo.setValue("showRule",'<%=showRule%>');
   		hashvo.setValue("method",'<%=method%>');
   		hashvo.setValue("code",'<%=code%>');
		var request=new Request({method:'post',asynchronous:true,onSuccess:compute3,functionId:'9024000006'},hashvo);
	
   }
   ///判断结果表中是否有分数相同但考核等级不同的考核对象
   function compute3(outparamters)
   {		
  		var flag=outparamters.getValue("flag");
  		var code=outparamters.getValue("code"); 		  		  		
  		var hashvo=new ParameterSet();			
		hashvo.setValue("planid",'${calcRuleForm.planid}');
		hashvo.setValue("flag",flag);
		hashvo.setValue("code",code);			
		var request=new Request({method:'post',onSuccess:resultYesOrNo,functionId:'9024000292'},hashvo);  		  		  		 		 		
   }
   
   function resultYesOrNo(outparamters)
   {
   		var flag=outparamters.getValue("flag");
		var yesOrn = outparamters.getValue("yScoreNGrade"); 	  	  	
		//if(yesOrn=="yes")	//  列出分数相同但考核等级不同的考核对象    在“多人考评”提交时控制，在绩效评估中无需进行控制，故此注释掉
		//	yScoreNgrade(); 
  		if(flag=='1')
  		{	
  			var waitInfo=eval("wait");	
	   		waitInfo.style.display="none";
	   		if(showRule=='cal')
	  			alert('计算完成！');
	   		goback("true");
  		}else
  		{
  			if(showRule=='cal')
	  			alert('计算失败！');
  			goback("false");
  		}
   }
	//  列出分数相同但考核等级不同的考核对象 
   function yScoreNgrade()
   {
 		var target_url="/performance/evaluation/performanceEvaluation.do?b_searchYSorNG=link";
 		var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
		var return_vo= window.showModalDialog(iframe_url, "", 
				"dialogWidth:450px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
   }
       	
  	validateBox(eval("document.calcRuleForm.useKnow"));  	
var aa=document.getElementsByTagName("input");
for(var i=0;i<aa.length;i++){
	if(aa[i].type=="text"){
		aa[i].className="inputtext";
	}
}
  </script>  

</html>
