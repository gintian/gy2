<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.report.edit_report.EditReportForm" %>
<script language="JavaScript"src="../../../module/utils/js/template.js"></script>
<SCRIPT LANGUAGE=javascript src="/js/function.js"></SCRIPT> 
<script language="javascript">
	var username = "${editReportForm.username}";
	var obj1 = "${editReportForm.obj1}";
	function move_up()
	{
	 	 var index=document.editReportForm.formula.selectedIndex;
		 if(index==0||index==-1)
		 	return;
		 var v=document.editReportForm.formula.options[index].value
		 var t=document.editReportForm.formula.options[index].text		
		 var v1=v.substring(0,v.indexOf("§§"));
		 var v2=document.editReportForm.formula.options[index-1].value.substring(0,document.editReportForm.formula.options[index-1].value.indexOf("§§"));
		
		 document.editReportForm.formula.options[index].value=v1+document.editReportForm.formula.options[index-1].value.substring(v2.length);
		 document.editReportForm.formula.options[index].text=document.editReportForm.formula.options[index-1].text
		 document.editReportForm.formula.options[index-1].value=v2+v.substring(v1.length);
		 document.editReportForm.formula.options[index-1].text=t
		 
		 var hashvo=new ParameterSet();
		 hashvo.setValue("upExpid",v); 
		 hashvo.setValue("downExpid",document.editReportForm.formula.options[index-1].value);
		 var In_paramters="flag=1"; 	
		 var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:success,functionId:'03020000012'},hashvo);			
		 		
		 
		 document.editReportForm.formula.options[index-1].selected=true;
		 document.editReportForm.formula.options[index].selected=false;
	}
	
	function success(outparamters){
	
	}
	
	function move_down()
	{
		 var index=document.editReportForm.formula.selectedIndex;
		 if(index>=document.editReportForm.formula.options.length-1||index==-1)
		 	return;
		 
		 var v=document.editReportForm.formula.options[index].value
		 var t=document.editReportForm.formula.options[index].text
		 var v1=v.substring(0,v.indexOf("§§"));
		 var v2=document.editReportForm.formula.options[index+1].value.substring(0,document.editReportForm.formula.options[index+1].value.indexOf("§§"));
		
		 document.editReportForm.formula.options[index].value=v1+document.editReportForm.formula.options[index+1].value.substring(v2.length);
		 document.editReportForm.formula.options[index].text=document.editReportForm.formula.options[index+1].text
		 document.editReportForm.formula.options[index+1].value=v2+v.substring(v1.length);
		 document.editReportForm.formula.options[index+1].text=t
		 
		 var hashvo=new ParameterSet();
		 hashvo.setValue("upExpid",v); 
		 hashvo.setValue("downExpid",document.editReportForm.formula.options[index+1].value);
		 var In_paramters="flag=1"; 	
		 var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:success,functionId:'03020000012'},hashvo);			
		 	 
		 document.editReportForm.formula.options[index+1].selected=true;
		 document.editReportForm.formula.options[index].selected=false;
	}
	
	function selectAll()
	{
		for(var i=0;i<document.editReportForm.formula.options.length;i++)
		{
			 document.editReportForm.formula.options[i].selected=true;
		}
	}
	
	
	
	
	function calculates()
	{
		var count=0;
		var value="";
		for(var i=0;i<document.editReportForm.formula.options.length;i++)
		{ 
			 if(document.editReportForm.formula.options[i].selected)
			 {
			 	count++;
			 	value+="&&"+document.editReportForm.formula.options[i].value;
			 }
		}
		if(count==0)
		{
			alert(REPORT_INFO28+"！");
			return;
		}
		else
		{		//dml 2011-04-11
				var obj=document.getElementsByName("b_compute");
				obj[0].disabled=true;
				var obj=document.getElementsByName("b_up");
				obj[0].disabled=true;
				var obj=document.getElementsByName("b_down");
				obj[0].disabled=true;
				var obj=document.getElementsByName("b_add");
				if(obj!=null&&obj.length!=0){
					for(var i=0;i<obj.length;i++){
						obj[i].disabled=true;
					}
				}
				var obj=document.getElementsByName("b_next");
				obj[0].disabled=true;
				var obj=document.getElementsByName("b_next2");
				obj[0].disabled=true;
				obj1=eval("wait");
				obj1.style.display="block";		
				var hashvo=new ParameterSet();
				hashvo.setValue("tabid","${editReportForm.tabid}");
				hashvo.setValue("formula_str",value.substring(2)); 	
				hashvo.setValue("operateObject","${editReportForm.operateObject}");
				hashvo.setValue("unitcode","${editReportForm.unitcode}");	
				hashvo.setValue("username1","${editReportForm.username1}");	
				hashvo.setValue("obj1","${editReportForm.obj1}");	
				var In_paramters="flag=1"; 	
					
				var request=new Request({method:'post',asynchronous:true,parameters:In_paramters,onSuccess:returnInfo,functionId:'03030000001'},hashvo);
		}
	}
	
	function returns()
	{
		var operateObject="${editReportForm.operateObject}";
		if(operateObject=='1')
			editReportForm.action="/report/edit_report/reportSettree.do?b_query=link&code=${editReportForm.tabid}&username="+$URL.encode(username)+"&obj1="+obj1+"&operateObject=1&status=<%=(request.getParameter("status"))%>";
		else
			editReportForm.action="/report/report_collect/reportOrgCollecttree.do?b_query=link&a_code=${editReportForm.unitcode}&operateObject=2";	
		editReportForm.submit();
	}
	
	
	function returnInfo(outparamters)
	{
		var info=outparamters.getValue("info");
		var obj1 =outparamters.getValue("obj1");
		var obj=document.getElementsByName("b_compute");
		obj[0].disabled=false;
		obj=eval("wait");
		obj.style.display="none";	
		if(info=='null')
		{
			Ext.showAlert(COMPUTESUCCESS,function(){
				var operateObject="${editReportForm.operateObject}";
				if(operateObject=='1')
					editReportForm.action="/report/edit_report/reportSettree.do?b_query=link&code=${editReportForm.tabid}&username="+$URL.encode(username)+"&obj1="+obj1+"&operateObject=1&status=<%=(request.getParameter("status"))%>";
				else
					editReportForm.action="/report/report_collect/reportOrgCollecttree.do?b_query=link&code=${editReportForm.unitcode}&operateObject=2";
				editReportForm.submit();
			});
		}
		else
		{		//2011-04-11 dml
				var obj=document.getElementsByName("b_compute");
				obj[0].disabled=false;
				var obj=document.getElementsByName("b_up");
				obj[0].disabled=false;
				var obj=document.getElementsByName("b_down");
				obj[0].disabled=false;
				var obj=document.getElementsByName("b_add");
				if(obj!=null&&obj.length!=0){
					for(var i=0;i<obj.length;i++){
						obj[i].disabled=false;
					}
				}
				var obj=document.getElementsByName("b_next");
				obj[0].disabled=false;
				var obj=document.getElementsByName("b_next2");
				obj[0].disabled=false;
			if(info.indexOf('#')==-1)
			{
				Ext.showAlert(info);
			}
			else
			{
				var infos=info.split('#');
				for(var i=0;i<infos.length;i++)
					Ext.showAlert(infos[i]);
			}
			return;
		}
	}
	

	var status='<%=request.getParameter("status")%>';

	function addformula(){
		var tabid = "${editReportForm.tabid}";
		var formulaType ="${editReportForm.formulaType}";
		var flag="${editReportForm.operateObject}";
		var url="";
		if(formulaType =="a"){
			url="/report/edit_report/editinnerformula.do?b_add=add&tabid="+tabid+"&returnflag=a&status="+status;
			var config = {
					width:480,
					height:340,
					title:'增加表内计算公式',
					theurl:url,
					id:'addformulaWin'
				}
			openWin(config);
		
		}else if(formulaType == "b"){
			url="/report/edit_report/editspaceformula.do?b_add=add&tabid="+tabid+"&flag="+flag+"&returnflag=b&status="+status+"&username="+$URL.encode(username);
			var config = {
					width:480,
					height:300,
					title:'增加表间计算公式',
					theurl:url,
					id:'addformulaWin'
				}
			openWin(config);
  		}
	}
	
	function openWin(config){
	    Ext.create("Ext.window.Window",{
	    	id:config.id,
	    	width:config.width,
	    	height:config.height,
	    	title:config.title,
	    	resizable:false,
	    	autoScroll:false,
	    	modal:true,
	    	renderTo:Ext.getBody(),
	    	html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=yes height='100%' width='100%' src='"+config.theurl+"'></iframe>",
    		listeners :{
	    		'close':function(){
	    			editReportForm.action="/report/edit_report/editReport.do?b_initFormula=initFormula&flag=${editReportForm.formulaType}&status=${editFormulaForm.status}";
	    			editReportForm.submit();
	    		}
	    	}
 	    }).show();	
	}

	
	
	function updateformula(){
		var count=0;
		var value="";
		for(var i=0;i<document.editReportForm.formula.options.length;i++){ 
			 if(document.editReportForm.formula.options[i].selected){
			 	count++;
			 	value = document.editReportForm.formula.options[i].value;
			 	
			 }
		}
		if(count==0){
			alert(REPORT_INFO28+"！");
			return;
		}else if(count > 1){
			alert(REPORT_INFO29+"！");
			return;
		}
		value=$URL.encode(getEncodeStr(value));
		//alert(value);
		
		var formulaType ="${editReportForm.formulaType}";
		var flag="${editReportForm.operateObject}";
		if(formulaType =="a"){
			var url="/report/edit_report/editinnerformula.do?b_update=update&value="+value+"&returnflag=a&status="+$URL.encode(status);
			var config = {
					width:480,
					height:340,
					title:'修改表内计算公式',
					theurl:url,
					id:'addformulaWin'
				}
			openWin(config);
		}else if(formulaType == "b"){
			var url="/report/edit_report/editspaceformula.do?b_update=update&value="+value+"&flag="+flag+"&returnflag=b&status="+$URL.encode(status);
			var config = {
					width:480,
					height:300,
					title:'修改表间计算公式',
					theurl:url,
					id:'addformulaWin'
				}
			openWin(config);
  		}
  	}
  	
	function delformula(){
		var count=0;
		var value="";
		for(var i=0;i<document.editReportForm.formula.options.length;i++){ 
			 if(document.editReportForm.formula.options[i].selected){
			 	count++;
			 	value+="&&"+document.editReportForm.formula.options[i].value;
			 }
		}
		if(count==0){
			alert(REPORT_INFO28+"！");
			return;
		}else{	
			 if(confirm("<bean:message key="workbench.info.isdelete"/>?")){
				var hashvo=new ParameterSet();
				hashvo.setValue("value",value);			
				var In_paramters="flag=1"; 				
				var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:delresult,functionId:'03020000039'},hashvo);
			}
		}
			
	}
	
	function delresult(outparamters){
		var info=outparamters.getValue("info");
		var status='<%=request.getParameter("status")%>'
		if(info == "ok"){
			var formulaType ="${editReportForm.formulaType}";
			if(formulaType =="a"){
				 editReportForm.action="/report/edit_report/editReport.do?b_initFormula=initFormula&flag=a&status="+status;
				 editReportForm.submit();
			}else if(formulaType =="b"){
			  	 editReportForm.action="/report/edit_report/editReport.do?b_initFormula=initFormula&flag=b&status="+status;
				 editReportForm.submit();
		    }
		}
	}

</script>
<hrms:themes/>
<html:form action="/report/edit_report/editReport">	


<div id='wait' style='position:absolute;top:150px;left:300px;text-align:center;display:none;'>
		<table border="1" width="400" cellspacing="0" cellpadding="4"  class="table_style"  height="87" align="center">
			<tr>
				<td  class="td_style"  id='wait_desc'   height=24>
					正在计算，请稍候......
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee  class="marquee_style"  direction="right" width="300" scrollamount="5" scrolldelay="10"  >
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


<br><br>
<table width="60%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable1">
            <thead>
           <tr>
                <td align="left" class="TableRow" nowrap colspan="3"><bean:message key="column.select"/>
                 <logic:equal name="editReportForm" property="formulaType" value="a"><bean:message key="edit_report.reportCalculate"/></logic:equal>
                 <logic:equal name="editReportForm" property="formulaType" value="b"><bean:message key="edit_report.reportsCalculate"/></logic:equal>
                 <logic:equal name="editReportForm" property="formulaType" value="c"><bean:message key="edit_report.inner_spaceCalculate"/></logic:equal> <bean:message key="hmuster.label.expressions"/></td>            	        	        	        
           </tr>
   	  </thead>
   	   <tr>
            <td width="100%" align="center" class="RecordRow_left" style="border-right:#C4D8EE 1pt solid " nowrap >
             
             
              <table width="80%">
                  <tr> 
                    <td width="80%" align="center"> 
                      <table width="100%">
                        <tr> 
                          <td width="100%" align="left"></td>
                        </tr>
                        <tr> 
                          <td width="100%" align="left"> 
	                          <select name="formula" multiple="multiple" size="10"  style="height:230px;width:100%;font-size:9pt; margin: 0 0 0 0;">
	                              <logic:iterate id="element" name="editReportForm" property="formulaList"  > 
	                             		<% int i=0; %>
	                             		<logic:iterate id="item" name="element" indexId="index"> 
		                              		<% if(i==0){%>
		                              			<option value='<bean:write name="item" filter="false"/>' >
		                              		<% } else { %>
		                              			<bean:write name="item" filter="false"/>
			                             	<% } i++; %> 
			                             </logic:iterate>
	
	                              </logic:iterate>
	                            </select> 
                            </td>
                        </tr>
                      </table>
                    </td>
                    
                    <td width="12%" align="center">
                    <table>
                    <tr><td>
                     <input type="button" name="b_up" value="<bean:message key="button.previous"/>" onClick="move_up();" class="mybutton">
                    </td></tr>
                    <tr><td>
                     <input type="button" name="b_down" value="<bean:message key="button.next"/>" onClick="move_down();" style="margin-top: 27px;" class="mybutton"> 
                    </td></tr>
                    </table>
                    </td>
                  </tr>
                </table>             
             
            </td>
            </tr>
          <tr>
          <td align="center" class="RecordRow" nowrap  colspan="3" style="height:35px; padding-top: 4px; vertical-align: top;">
          	 <logic:notEqual name="editReportForm" property="formulaType" value="c" >
          	 <logic:equal name="editReportForm" property="operateObject" value="1" >
          	 
          	  <hrms:priv func_id="290214">  
          	  		<input type="button" name="b_add" value="<bean:message key="kq.emp.button.add"/>"  onclick="addformula()" class="mybutton" > 
          	  </hrms:priv>
          	   <hrms:priv func_id="290215"> 
          	  		<input type="button" name="b_add" value="<bean:message key="kq.report.update"/>" style="margin-left: -3px;"  onclick="updateformula()" class="mybutton"> 
          	  </hrms:priv>
          	 <hrms:priv func_id="290216"> 
          	  		<input type="button" name="b_add" value="<bean:message key="kq.emp.change.emp.leave"/>" style="margin-left: -3px;" onclick="delformula()" class="mybutton"> 
          		</hrms:priv>
          	  <input type="button" name="b_next" value="<bean:message key="label.query.selectall"/>" style="margin-left: -3px;" onclick="selectAll()" class="mybutton">
          	
          	</logic:equal>	
          		
          	<logic:equal name="editReportForm" property="operateObject" value="2" >
          	 
          	  <hrms:priv func_id="2903214">  
          	  		<input type="button" name="b_add" value="<bean:message key="kq.emp.button.add"/>" onclick="addformula()" class="mybutton" > 
          	  </hrms:priv>
          	   <hrms:priv func_id="2903215"> 
          	  		<input type="button" name="b_add" value="<bean:message key="kq.report.update"/>" style="margin-left: -3px;" onclick="updateformula()" class="mybutton"> 
          	  </hrms:priv>
          	   <hrms:priv func_id="2903216"> 
          	  		<input type="button" name="b_add" value="<bean:message key="kq.emp.change.emp.leave"/>" style="margin-left: -3px;" onclick="delformula()" class="mybutton"> 
          		</hrms:priv>
          	  <input type="button" name="b_next" value="<bean:message key="label.query.selectall"/>" style="margin-left: -3px;" onclick="selectAll()" class="mybutton">
          	  
          	</logic:equal>		
          	</logic:notEqual>
          	 <logic:equal name="editReportForm" property="formulaType" value="c" >
          	  <input type="button" name="b_next" value="<bean:message key="label.query.selectall"/>"  onclick="selectAll()" class="mybutton">
          	 </logic:equal>
          	<%EditReportForm editReportForm = (EditReportForm) session.getAttribute("editReportForm");
          	if(editReportForm.getOperateObject().equals("1")){
          		if((Integer.parseInt(editReportForm.getStatus())!=1)&&(Integer.parseInt(editReportForm.getStatus())!=3)) {%>
              <input type="button" name="b_compute" value="<bean:message key="button.computer"/>" style="margin-left: -3px;" onclick="calculates()"   class="mybutton"> 
             <%} }else{
              	if(!((String)editReportForm.getDmlflag()).equals("true")){ 
              %>
              <input type="button" name="b_compute" value="<bean:message key="button.computer"/>" style="margin-left: -3px;" onclick="calculates()"   class="mybutton"> 
              <%}else{%>
              
              <%} }%>
                <input type="button" name="b_next2" value="<bean:message key="kq.search_feast.back"/>" style="margin-left: -3px;" onclick="returns()" class="mybutton"> 
              </td>
          </tr>   
</table>
</html:form>