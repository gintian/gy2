<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html>
<head>
 
</head> 
  <script type="text/javascript" src="/js/validateDate.js"></script>
  <script language='javascript' >
  var info;
  if(parent.getArguments)
	  info=parent.getArguments();
  else
	  info=dialogArguments;
  var isActuarialData=${searchReportUnitForm.isActuarialData};
  var historyu01="${searchReportUnitForm.historyu01}";
   function setCancel()
   {
  	    if(document.searchReportUnitForm.end_date.value.length==0)
	  	{
	  		alert("请输入有效日期止时间!");
	  		return;
	  	}
	  	
	  	if(isActuarialData==1)
	  	{
	  		if(document.searchReportUnitForm.goal_unit.value.length==0)
		  	{
		  	if(confirm("未选择人员移动目标机构，撤销机构的报表数据将丢失！是否继续？")){
		  	}else
		  		return;
		  	}
		  if(document.searchReportUnitForm.transfer_date.checked){	
		  	if(historyu01.indexOf(","+document.searchReportUnitForm.goal_unit.value+",")!=-1){
		  	alert("请选择精算报表没有数据的单位!");
		  	return;
		  	}
		  }
	  	}
  	
  	
  	if(confirm("请确认执行撤销操作?"))
  	{
	  	var result=new Array();
	  	result[0]=document.searchReportUnitForm.end_date.value;
	  	if(isActuarialData==1)
	  	{
	  	result[1]=document.searchReportUnitForm.goal_unit.value;
	  	if(document.searchReportUnitForm.transfer_date.checked){
	  	result[2]='1';
	  	}else{
	  		result[2]='0';
	  	}
	  	}
	    returnValue=result;
	    parent.goalunit = result;
	    closewin();
	  
  	}
  }
   
   function closewin()
	{
	   var perWin = parent.Ext.getCmp("showBatId");
		if(!perWin)
	     	window.close();
		else
			perWin.close();
	}
  
  </script>
  <hrms:themes/>
<body>
<html:form action="/report/org_maintenance/reportunitlist" > 
<br>
<table width='95%' border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap colspan="2">
		    撤销机构 
            </td>            	        	        	        
           </tr>
   	  </thead>
   	  
   	  <tr class="trDeep" >
	   	  <td align="right" class="RecordRow" nowrap>
	   	  &nbsp;撤销机构
	   	  </td>
	   	  <td align="left" class="RecordRow" nowrap> 
	   	  <script language='javascript'>
	   	   	document.write('&nbsp;'+info[0]);
	   	   </script>
	   	  </td>
   	  </tr>
   	  
   	  
   	  <tr class="trShallow" >
	   	  <td align="right" class="RecordRow" nowrap>
	   	  &nbsp;有效日期止
	   	  </td>
	   	  <td align="left" class="RecordRow" nowrap>
	 
	   	  <script language='javascript'>
	   	  document.write('<input type="text" name="end_date" value="'+info[1]+'" maxlength="50" style="width:150px" extra="editor" dropDown="dropDownDate" onchange="if(!validate(this,\'有效日期止\')) {this.focus(); this.value=\''+info[1]+'\'; }"/>');
   	   	  </script>
	   	  </td>
   	  </tr>
   	  
   	  <logic:equal  name="searchReportUnitForm"  property="isActuarialData" value="1">
   	  <tr class="trDeep" >
	   	  <td align="right" class="RecordRow" nowrap >
	   	 &nbsp;人员移动目标机构
	   	  </td>
	   	  <td align="left" class="RecordRow" nowrap><input type="text" name="goal_unitDesc" maxlength="40" size="28" value=""  />
	         <img src="/images/code.gif" onclick="openReportorgCodeDialog('goal_unitDesc','all','');"  align="absmiddle"/>
			 <input type="hidden" name="goal_unit" value="" class="text">（请选择基层单位）     
	   	  </td>
   	  </tr>
   	 <tr class="trDeep" >
	   	  <td align="left" colspan="2" class="RecordRow" nowrap >
	   	 <input type="checkbox" name="transfer_date" >&nbsp;划转该机构所有周期数据
	   	  </td>
   	  </tr>
   	  </logic:equal>
        
</table>

<table align='center' width='96%' style="padding-top:10px"><tr><td align='center'>

<input type="button" name="button1"  value="执行撤销" class="mybutton" onclick='setCancel()'>  
<input type="button" name="button2"  value="返 回" class="mybutton" onclick='closewin()'>  


</td></tr></table>



</html:form>

</body>
</html>