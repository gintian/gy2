<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet"
	href="/gz/gz_budget/budget_rule/formula/budget_formula.css"
	type="text/css">
<%@ page import="java.util.*,
				com.hrms.struts.taglib.CommonData,
				com.hjsj.hrms.actionform.gz.gz_accounting.piecerate.PieceRateForm"%>
<script type="text/javascript" src="/gz/gz_accounting/piecerate/piecerate.js"></script>
<hrms:themes />
<html>
   <%
   	  PieceRateForm pieceRateForm = (PieceRateForm)session.getAttribute("pieceRateForm");
   	  ArrayList fieldList=pieceRateForm.getZblist();
    %> 
  <body>
 <html:form action="/gz/gz_accounting/piecerate/search_piecerate">
  
    <table width='490px;' style="margin-left:-3px;margin-top:-5px;"><tr><td>
     <fieldset align="center" style="width:100%;">
    							 <legend>计件薪资</legend>
    
    <table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">     
    	<tr>
    	<td align='left'  valign='top' > 
			&nbsp;&nbsp;&nbsp;<bean:message key="gz.templateset.cond" />:&nbsp;
		</td >
		<td >	
			<Input type='button' value='...'  class="mybutton"  onclick='simpleCondition()' />&nbsp;&nbsp; 
    	</td>
    	</tr>

    	<tr>
    		<td align='left'>    			
    	 		&nbsp;&nbsp;&nbsp;周期: 
    	 		<br> 
    	 	
    	 	</td>
    		<td align='left'>
    			<br><br> 
    			<table>
    				<tr height="100px">
    				<td>
					<html:select name="pieceRateForm" property="sp_status" size="1"	onchange="show(this)">
						<html:option value="1">
							月
						</html:option>
						<html:option value="2">
							季
						</html:option>
						<html:option value="3">
							半年
						</html:option>
						<html:option value="4">
							年
						</html:option>
							
					</html:select>
				</td>
				<td>
				<br><br> 
				<div id = "ss">
					<table width='100%' border='0' cellspacing='0'  align='center' cellpadding='0' class="ListTable">
						<tr class="">
							<td class=""><input type="radio" id="y1" name="zq" value="1" checked onclick="on(this)"/></td>
							<td class="">自然月份</td>
						</tr>
						<tr class="">
							<td class=""><input type="radio" id="y2" name="zq" value="2" onclick="on(this)"/></td>
							<td class="">
										<table>
											<tr>
											<td>
										<div class="m_frameborder">
											上月
											<input type="text" name="delayTime2" size="2" value="2" onkeypress="" onblur="testNum(this)"  class="m_input">																
										</div>
										</td>
										<td>																
											<table border="0" cellspacing="2" cellpadding="0">
												<tr><td><button id="delayTime2_up" class="m_arrow" onmouseup="mincrease('delayTime2','delayTime1',28);">5</button></td></tr>
												<tr><td><button id="delayTime2_down" class="m_arrow" onmouseup="msubtract('delayTime2','delayTime1',2);">6</button></td></tr>
										    </table>							
										</td>	
										<td>
											<div>
											日到本月
											<input type="text" name="delayTime1" size="2" value="1" onkeypress="" onblur="" disabled="disabled" class="m_input">
											日		
											</div>
										</td>
										</tr>
								
										</table>
							 </td>
						</tr>
					</table>
				</div>
				</td>
				</tr>
				</table>
				<br><br> 
			</td>
		</tr>
    	
    	<tr>
    		<td align='left'  valign='top' >
    			
    	 		&nbsp;&nbsp;&nbsp;引入指标: 
    	 	</td>
    		<td  align='left' valign='top' >
    		
    		<table class="ListTable" width='100%'>
    		<tr>
    		<td>
    		<div id="scroll_box1" align='left'>
    		<table id="table1" width='100%' border='0' cellspacing='0'  align='center' cellpadding='0' class="ListTable">
    				<tr >
						<td   width='10%' class="TableRow" align='center'><input type='checkbox' name='' value='' onclick="selall()"  /></td>
						<td   width='45%' class="TableRow" align='center'>计件指标</td>
						<td   width='45%' class="TableRow" align='center'>薪资指标</td>
					</tr>
			<% for(int i=0;i<fieldList.size();i++){
    				CommonData cd=(CommonData)fieldList.get(i);
    				out.print("<tr><td  width='10%' class='RecordRow' align='center'><input type='checkbox' name='quanxuan' /></td>" );
    				out.print("<td  width='45%' class='RecordRow' align='center'>"+cd.getDataValue()+"<input type='hidden' name='zhib3' value='"+cd.getDataValue().split(":")[0]+"'/></td><td  width='45%' class='RecordRow' align='center'>"+cd.getDataName()+"<input type='hidden' name='zhib4' value='"+cd.getDataName().split(":")[0]+"'/></td></tr>");
    		} %>
			</table>
			
    		<table  width='100%' border='0' cellspacing='0'  align='center' cellpadding='0' class="ListTable">

    				<tr ><td  width='10%' class='RecordRow' align='center' style="border-top:0px;"><input type='checkbox' name='' value='' /></td>
    				<td   width='45%' class='RecordRow' align='center' style="border-top:0px;">
    				<hrms:optioncollection name="pieceRateForm" property="setlist1" collection="list"/>
    				<html:select name="pieceRateForm" property="zhib1" onchange="check(this)" style="width:150">
							<html:options collection="list" property="dataValue" labelProperty="dataName" />
					</html:select>
					</td>
					<td   width='45%' class='RecordRow' align='center' style="border-top:0px;">
    				<hrms:optioncollection name="pieceRateForm" property="zhibiaolist" collection="list"/>
    				<html:select name="pieceRateForm" property="zhib2" onchange="check2(this)" style="width:150">
							<html:options collection="list" property="dataValue" labelProperty="dataName" />
					</html:select>
					</td>
					</tr>
    		</table>
    		</div>
    		</td>
    		</tr>
    		</table>
			
    	 </td></tr>
 
    
    </table>
    <table>
    	<tr>
    		<td width='20%'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
    		<td align="right" width='80%'><input type="button" value="增加" class="mybutton" onclick="addtable()"><input type="button" value="删除" class="mybutton" onclick="deltable()"></td>
    	</tr>
    </table>
    </fieldset>
    <input type='hidden' name='expression_str'  value='' />
    <input type='hidden' name='zhouqi1'  value='1' />
    <input type='hidden' name='zhouqi'  value='1' />
    <table width="490px;" align="center"><tr><td align="center">
     <Input type='button' value='<bean:message key="lable.tz_template.enter"/>'  class="mybutton"  onclick="ok()" /> 
  	 <Input type='button' value='<bean:message key="lable.tz_template.cancel"/>'  class="mybutton"  onclick='window.close()' /> 
  	 </td></tr></table>
	</td></tr></table>
</html:form>
  </body>
</html>
<script language='javascript' >
	var id = "${pieceRateForm.sp_status}";
	var day = "${pieceRateForm.yuezb}";
	var formula = '${pieceRateForm.formula}';
	if(id==""){
		id = "1";
	}
	if(day==""){
		day = "1";
	}
    show1(id);
    show2(day,getDecodeStr(formula));
</script>