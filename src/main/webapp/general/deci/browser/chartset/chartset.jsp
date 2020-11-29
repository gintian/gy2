<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<% 
	String opt="0";   //0显示分值序列面板 -1：显示分值序列面板，显示图形大小设置  -2:显示图形大小设置  否则不显示分值序列面板，图形大小设置面板
	if(request.getParameter("opt")!=null&&!request.getParameter("opt").equalsIgnoreCase("undefined"))  
		opt=request.getParameter("opt");
	
	UserView userview=(UserView)session.getAttribute(WebConstant.userView);
	//if(userview.getVersion()>=50)//5.0以上版本只是显示分值序列
	//	opt="0";
	
	String chartTitle = request.getParameter("chartTitle");
	String controlNames = request.getParameter("controlNames");
	String chartSets = request.getParameter("chartSets");
	String length_width = request.getParameter("length_width");
 %>


<html>
	<head>
		<link href="../../../../css/css1.css" rel="stylesheet" type="text/css">
	</head>
	<script language=JavaScript> 
	function load(){
	// 获取参数传递的对象
	  	var obj = '';
		info = [];
		if(window.dialogArguments){
			obj = window.dialogArguments;	  
		  	info = obj.split("#");
		} else {
			info.push('<%=chartTitle%>');
			info.push('<%=controlNames%>');
			info.push('<%=chartSets%>');
			info.push('<%=length_width%>');
		}
	  	<%if(userview.getVersion()<50){%>
	  	if(info[0]!="no")
	  	{	
	  		if(document.chartSet.titleName!=null)
	    		document.chartSet.titleName.value=info[0];	 
	  	}
	  	<% } %>
	  	
	  	var b = document.chartSet.controlValues; 		  	
		
		<% if(opt.equals("0")||opt.equals("-1")){ %>
		//控制值，由于暂时没用到分值序列，先注释掉，以后用到在家
		var controlStr = info[1];
		
			arrays = controlStr.split(",");
			for(var i=0 ; i<arrays.length; i++){
				var oOption = document.createElement("OPTION");
				var temps=arrays[i].split("~");
				b.options.add(oOption);
				oOption.innerText =temps[1];
				oOption.value =temps[0];	
				if(temps[2]=="1")
					b.options[b.options.length-1].selected=true;
			}
		<% } %>
		
		//设置图形大小
		<% if(opt.equals("-1")||opt.equals("-2")){ %>
		if(info.length==4&&info[3].length>0)
		{
			var temps=info[3].split(",");
			document.chartSet.p_length.value=temps[1];
			document.chartSet.p_width.value=temps[0];
		}
		<% } %>
		
		<%if(userview.getVersion()<50){%>
		//参数信息
		if(info.length>2)
		{			
			var chartParameter = info[2];			
			if(chartParameter!=null&&chartParameter!='undefined'){
			ays = chartParameter.split("`");
			var n = ays[0];
			
            if(chartSet.titleAlign!=null && chartSet.titleAlign[n]!=null)
            {
	    		chartSet.titleAlign[n].checked=true;//对齐
			}
			if(ays.length>2){
			 for(var i =0; i < chartSet.xStratValue.options.length; i++){   
					  if(chartSet.xStratValue.options[i].value == ays[5]){   
						 chartSet.xStratValue.options[i].selected  = true;   
					  }   
			 }   
			 for(var i =0; i < chartSet.xEndValue.options.length; i++){   
					  if   (chartSet.xEndValue.options[i].value   ==   ays[6]){   
						 chartSet.xEndValue.options[i].selected   =   true;   
					  }   
			  } 
			  for(var i =0; i < chartSet.yStartValue.options.length; i++){   
					  if   (chartSet.yStartValue.options[i].value   ==   ays[2]){   
						 chartSet.yStartValue.options[i].selected   =   true;   
					  }   
			  } 
			  for(var i =0; i < chartSet.yEndValue.options.length; i++){   
					  if   (chartSet.yEndValue.options[i].value   ==   ays[3]){   
						 chartSet.yEndValue.options[i].selected   =   true;   
					  }   
			  } 
				var infoo = ays[1];
				aaa = infoo.split(",");		
				var isauto = aaa[0];
				var sv = aaa[1];
				var ev = aaa[2];
		
				if(isauto == "0"){
					chartSet.numberAxis[0].checked=true;
					showView();
				}else{
					chartSet.numberAxis[1].checked=true;
					showView();
		
					chartSet.numberStartValue.value= sv;
					chartSet.numberEndValue.value= ev;
		
				}	
				chartSet.addValue.value=ays[4];
			  }
			} 
		}
		<% } %>
	}
	
    function showView() {
	       var a = eval("d1");
	       var b = eval("d2");
	       var c = eval("d3");
	       if (chartSet.numberAxis[0].checked == true) {
	           a.style.display = 'none';
	           b.style.display = 'none';
	           c.style.display = 'none';
	       } else {
	           a.style.display = 'block';
	           b.style.display = 'block';
	           c.style.display = 'block';
	       }
	}
	function numCheck(){
		if ( !(((window.event.keyCode >= 48) && (window.event.keyCode <= 57)) 
		|| (window.event.keyCode == 13) || (window.event.keyCode == 46) 
		|| (window.event.keyCode == 45)))
		{
			window.event.keyCode = 0 ;
		}
	}
	function chartSetResult(){
		var info="";
		<%if(userview.getVersion()<50){%>
		if(document.chartSet.titleName.value == ""){//可以为空
			//alert("标题不能为空");
			//return;
		}
		else if(document.chartSet.titleName.value.indexOf("\"")!=-1)
		{
			alert("标题不能输入 \" 字符!");
			return;
		}
		info +=  document.chartSet.titleName.value;
		info +=  "`";
		
		var titleAlign="";
		if (chartSet.titleAlign!=null)
		{
			if (chartSet.titleAlign!=null && chartSet.titleAlign[0].checked == true) {
				titleAlign="0";
			}else if(chartSet.titleAlign!=null && chartSet.titleAlign[1].checked == true) {
				titleAlign="1";
			}else{
				titleAlign="2";
			}
		}
		
		info += titleAlign;
		info += "`";
		
		var na="";
		if (chartSet.numberAxis[0].checked == true) {
			//自动设置
			na += "0";
		}else{
			na += "1";
			na += ",";
			//指定区间
			var numStartValue = chartSet.numberStartValue.value;
			if(numStartValue == ""){
				alert(CHART_SET_INFO1);
				return;
			}
			na += numStartValue;
			na += ",";
			
			var numEndValue = chartSet.numberEndValue.value;
			if(numEndValue == ""){
				alert(CHART_SET_INFO2);
				return;
			}
			na += numEndValue;
			na += ",";
			
			if(numStartValue*1 > numEndValue*1){
				alert(CHART_SET_INFO3);
				return;
			}
			
		}
		info += na;
		info += "`"; 
		
		
		var yStartValue = chartSet.yStartValue.value;
		info += yStartValue;
		info += "`"; 
		
		var yEndValue = chartSet.yEndValue.value;
		info += yEndValue;
		info += "`"; 
		
		if(yStartValue*1 > yEndValue*1){
			alert(CHART_SET_INFO4);
			return;
		}
		
		var addValue ="";
		addValue = chartSet.addValue.value;//坐标增量可以为空
		if(addValue!='')
		{
			if(parseFloat(addValue)<0)
			{
				alert(CHART_SET_INFO8);
				return;
			}
		}
		info += addValue;
		info += "`"; 
				
		var xStratValue = chartSet.xStratValue.value;
		info += xStratValue;
		info += "`"; 
		
		var xEndValue = chartSet.xEndValue.value;
		info += xEndValue;
		info += "`"; 
		
		if(xStratValue*1 > xEndValue*1){
			alert(CHART_SET_INFO5);
			return;
		}
		<% }else{ %>			
		info += "`";		
		info += "0`";
		info += "0`";		
		info += "0`";
		info += "0`";
		info += "0`"; 
		info += "0`";
		info += "0`";
		<% } %>	
			
		<% if(opt.equals("0")||opt.equals("-1")){ %>
		var cvs = "";
		/////////////////
	    for(var i = 0; i < document.chartSet.controlValues.options.length; i++) {
			if (document.chartSet.controlValues.options[i].selected ) {
		        cvs += document.chartSet.controlValues.options[i].value +",";
			} 
		}
		if(cvs == ""){
			info += "null";
		}else{
			info += cvs;
		}
		<% }else{ %>
			info += "null";
		<% } %>			
		info += "`"; 
		
	<%if(userview.getVersion()<50){%>
	   <% if(opt.equals("-1")||opt.equals("-2")){ %>	    
	   if(document.chartSet.p_width.value.length>0&&!checkIsIntNum(document.chartSet.p_width.value))
	    {
				alert(CHART_SET_INFO6+"!");
				return;
		}
			
		if(document.chartSet.p_length.value.length>0&&!checkIsIntNum(document.chartSet.p_length.value))
		{
				alert(CHART_SET_INFO7+"!");
				return;
		}
		if(document.chartSet.p_length.value.length>0&&document.chartSet.p_width.value.length>0)
		{
			
			if(!checkIsIntNum(document.chartSet.p_width.value))
			{
				alert(CHART_SET_INFO6+"!");
				return;
			}
			
			if(!checkIsIntNum(document.chartSet.p_length.value))
			{
				alert(CHART_SET_INFO7+"!");
				return;
			}
			info+=document.chartSet.p_width.value+","+document.chartSet.p_length.value+"`";
		}
		<% }%>
	<% }else{%>
			info += "0,0`";
	<%}%>
		if(parent && parent.parent && parent.parent.Ext && parent.parent.jfreechartSet2_ok){
			parent.parent.jfreechartSet2_ok(info);
		}else {
			returnValue=info;
		}
		window.close();
	}
	
	
	function setSize()
	{
		document.chartSet.p_width.value="";
		document.chartSet.p_length.value="";
		chartSetResult();
	}
	function closewin(){
		if(parent && parent.parent && parent.parent.Ext && parent.parent.chartWinClose){
			parent.parent.chartWinClose();
		} else {
			window.close();
		}
	}
	
</script>
<hrms:themes />

	<body onload="load();setFieldSetWidth();">
		<br>
		<br>
		<FORM style='position:absolute;top:10px;' method="post" action="" name="chartSet">
			<hrms:tabset name="chartset" width="430" height="223" type="false">
			<%if(userview.getVersion()<50){%>
				<hrms:tab name="title" label='column.law_base.title' visible="true">
					<table>
						<tr>
							<td>
								<bean:message key="column.law_base.title"/>
							</td>
						</tr>
						<tr>
							<td>
								<TEXTAREA style="overflow-y:hidden" NAME="titleName" ROWS="4" COLS="49.8"></TEXTAREA>
							</td>
						</tr>
					</table>
					<br>
					<fieldset id='alginmode'>
						<legend>
							<bean:message key="chartset.alginmode"/>
						</legend>
						<TABLE>
							<TR>
								<td>
									<INPUT TYPE="radio" NAME="titleAlign" value="0">
									<bean:message key="chartset.alginleft"/>
								</td>
								<td>
									<INPUT TYPE="radio" NAME="titleAlign" value="1" checked>
									<bean:message key="chartset.alginmiddle"/>
								</td>
								<td>
									<INPUT TYPE="radio" NAME="titleAlign" value="2">
									<bean:message key="chartset.alginright"/>
								</td>
							</TR>
						</TABLE>
					</fieldset>
				</hrms:tab>
				<hrms:tab name="axis" label='chartset.axis' visible="true">
					<fieldset id='fengzhizuobiao'>
						<legend>
							<bean:message key="chartset.fengzhizuobiao"/>
						</legend>
						<TABLE>
							<tr>
								<td>
									<INPUT TYPE="radio" NAME="numberAxis" checked onClick="showView()">
									<bean:message key="chartset.autoset"/>
								</td>
							</tr>
							<TR>
								<TD>
									<INPUT TYPE="radio" NAME="numberAxis" onClick="showView()">
									<bean:message key="chartset.setspace"/>
								</TD>

								<TD>
									<div id="d1" style="display:none">
										<INPUT TYPE="text" NAME="numberStartValue" size="4" onKeypress="numCheck()">
									</div>
								</TD>
								<TD>
									<div id="d2" style="display:none">
										<bean:message key="label.query.to"/>
									</div>
								</TD>
								<TD>
									<div id="d3" style="display:none">
										<INPUT TYPE="text" NAME="numberEndValue" size="4" onKeypress="numCheck()">
									</div>
								</TD>

							</TR>
							<TR>
								<TD>
									<bean:message key="chartset.axisstart"/>
								</TD>
								<TD>
									<SELECT NAME="yStartValue">
										<%for (int i = 0; i <= 100; i++) {
											if (i == 10) {%>
											<option value="<%= i %>" selected>
												<%=i%>
											</option>
											<%} else {%>
											<option value="<%= i %>">
												<%=i%>
											</option>
											<%}
										
										}%>
									</SELECT>
									%
								</TD>
								<TD>
									<bean:message key="chartset.axisend"/>
								</TD>
								<TD>
									<SELECT NAME="yEndValue">
										<%for (int i = 0; i <= 100; i++) {
										if (i == 90) {%>
										<option value="<%= i %>" selected>
											<%=i%>
										</option>
										<%} else {%>
										<option value="<%= i %>">
											<%=i%>
										</option>
										<%}
										}%>
									</SELECT>
									%
								</TD>
							</TR>
							<TR>
								<TD>
									<bean:message key="chartset.axisadd"/>
								</TD>
								<TD>
									<INPUT TYPE="text" NAME="addValue" size="4" value="" onKeypress="numCheck()">
								</TD>
								<TD></TD>
								<TD></TD>
							</TR>

						</TABLE>

					</fieldset>
					<fieldset id='spaxis'>
						<legend>
							<bean:message key="chartset.spaxis"/>
						</legend>
						<table>
							<tr>
								<TD>
									<bean:message key="chartset.axisstart"/>
								</TD>
								<TD>
									<SELECT NAME="xStratValue">
										<%for (int i = 0; i <= 100; i++) {
											if (i == 10) {%>
											<option value="<%= i %>" selected>
												<%=i%>
											</option>
											<%} else {%>
											<option value="<%= i %>">
												<%=i%>
											</option>
											<%}
										
										}%>
									</SELECT>
									%
								</TD>
								<TD>
									<bean:message key="chartset.axisend"/>
								</TD>
								<TD>
									<SELECT NAME="xEndValue">
										<%for (int i = 0; i <= 100; i++) {
											if (i == 90) {%>
											<option value="<%= i %>" selected>
												<%=i%>
											</option>
											<%} else {%>
											<option value="<%= i %>">
												<%=i%>
											</option>
											<%}
										}%>
									</SELECT>
									%
								</TD>
							</tr>
						</table>
					</fieldset>
				</hrms:tab>
				
				<% }%>
				
				<% if(opt.equals("0")||opt.equals("-1")){%>		 
				<hrms:tab name="value" label='chartset.fzxl'  visible="true">
					<fieldset id='showScore'>
						<legend>
							<bean:message key="lable.performance.showScore"/>
						</legend>
						<table>
							<tr>
								<td>
									<SELECT NAME="controlValues" multiple style="height:120px;width:330;font-size:10pt">
									</SELECT>
								</td>
							</tr>
						</table>
					</fieldset>
				</hrms:tab>
				<% }%>
				<%if(userview.getVersion()<50){%>
				  <% if(opt.equals("-1")||opt.equals("-2")){ %>
			   <hrms:tab name="picSize" label='chartset.txcc'  visible="true">
			   		<Br>
					<fieldset id='setsize'>
						<legend>
							<bean:message key="chartset.setsize"/>
						</legend>
						<table>
							<tr>
								<td>
									<span>&nbsp;<bean:message key="report.parse.pagelength"/>:&nbsp; <Input type='text' name='p_length' value=''  >&nbsp;<bean:message key="report.parse.px"/></span>
									<span style="margin-top:5px;display:block;">&nbsp;<bean:message key="report.parse.pagewidth"/>:&nbsp; <Input type='text' name='p_width' value=''  >&nbsp;<bean:message key="report.parse.px"/></span>
									<Br><br>
									<input type="button" name="sss" value='<bean:message key="chartset.autoSize"/>' onclick="setSize()" class="mybutton">
								</td>
							</tr>
						</table>
					</fieldset>
				</hrms:tab>
				<% }%>
					<% }%>
			</hrms:tabset>
			<table width="50%" align="center">
				<tr>
					<td align="center">
						<input type="button" name="confirm" value='<bean:message key="button.ok"/>' onclick="chartSetResult()" class="mybutton">
						<input type="button" name="cancel" value='<bean:message key="button.cancel"/>' onclick="closewin();" class="mybutton">
					</td>
				</tr>
			</table>
		</form>
	</body>
	<script type="text/javascript">
	function setFieldSetWidth(){
		 if(/msie/i.test(navigator.userAgent)){
				var _tabsetpane_chartset =  document.getElementById("_tabsetpane_chartset");
				if(_tabsetpane_chartset){
					_tabsetpane_chartset.style.width='440px';
					_tabsetpane_chartset.style.height='210px';
				}
				var alginmode =  document.getElementById("alginmode");
				if(alginmode)
					alginmode.style.width='423px';
				
				var fengzhizuobiao =  document.getElementById("fengzhizuobiao");
				if(fengzhizuobiao)
					fengzhizuobiao.style.width='423px';
				
				var spaxis =  document.getElementById("spaxis");
				if(spaxis)
					spaxis.style.width='423px';
				
				var showScore =  document.getElementById("showScore");
				if(showScore)
					showScore.style.width='423px';
				
				var setsize =  document.getElementById("setsize");
				if(setsize)
					setsize.style.width='422px';
			}
	 }
	</script>
	
	</html>