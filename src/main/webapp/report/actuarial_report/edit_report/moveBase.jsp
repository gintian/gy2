<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.report.actuarial_report.edit_report.EditReport_actuaialForm" %>				
<%
	EditReport_actuaialForm editReport_actuaialForm=(EditReport_actuaialForm)session.getAttribute("editReport_actuaialForm");
	String selfUnitcode=editReport_actuaialForm.getSelfUnitcode();
//	String unitcode=editReport_actuaialForm.getUnitcode();
//	String flag=editReport_actuaialForm.getFlag();
//	String flagSub=editReport_actuaialForm.getFlagSub(); 
//	String opt=editReport_actuaialForm.getOpt();
//	String opt2=editReport_actuaialForm.getOpt2();
//	String idstatus=editReport_actuaialForm.getIdstatus();
//	String from_model=editReport_actuaialForm.getFrom_model();
//	String subquerysql=editReport_actuaialForm.getSubquerysql();
//	String rootUnit=editReport_actuaialForm.getRootUnit();
//	String id =editReport_actuaialForm.getId();
//	System.out.println("selfUnitcode:"+selfUnitcode+"unitcode:"+unitcode+"id:"+id+"flag:"+flag+"opt:"+opt+"opt2:"+opt2+"from_model:"+from_model+"subquerysql:"+subquerysql+"rootUnit:"+rootUnit+"idstatus:"+idstatus);
	
%>		
<html>
<head>
 
</head> 
  <script type="text/javascript" src="/js/validateDate.js"></script>
  <script language='javascript' >
  var info=dialogArguments;
  
 
   function setCancel()
   {
  	if(confirm("请确认执行人员移库操作!"))
  	{
	  	if(document.getElementsByName('goal_personState.value')[0].value.length==0||document.editReport_actuaialForm.u02base.value.length==0)
	  	{
	  		alert("所有输入项均为必填项");
	  		return;
	  	}
	  	var result=new Array();
	  	result[0]=document.getElementsByName('goal_personState.value')[0].value;
	  	result[1]= document.editReport_actuaialForm.u02base.value;
	    returnValue=result;
	    window.close();
	  
  	}
  }
  function openU02Base() 
{
   
    thecodeurl="/system/codeselect.jsp"; 
    var popwin= window.showModelessDialog(thecodeurl, theArr, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:yes");
}
  </script>
  <hrms:themes />
<body>
<html:form action="/report/actuarial_report/edit_report/searcheportU02List" > 




<br>
<table width='95%' border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap colspan="2">
		    人员移库
            </td>            	        	        	        
           </tr>
   	  </thead>
   	  
   	  <tr class="trDeep" >
	   	  <td align="left" class="RecordRow" nowrap>
	   	  &nbsp;人员状态：
	   	  </td>
	   	   <td align="left" class="RecordRow" nowrap>&nbsp;<input type="text" name="goal_personState.viewvalue" maxlength="0" size="28" value="" style="width:200px;" onchange="fieldcode(this,2)" />
                                  <img src="/images/code.gif" onclick="openInputCodeDialog('63','goal_personState.viewvalue');"  align="absmiddle"/>
         							 <input type="hidden" name="goal_personState.value" value="" class="text">           
         
	   	  </td>
   	  </tr>
   	  
   	  <tr class="trDeep" >
	   	  <td align="left" class="RecordRow" nowrap >
	   	 &nbsp;目标库:
	   	  </td>
	   	  <td align="left" class="RecordRow" nowrap>&nbsp;<select name="u02base" style="width:200px;vertical-align:middle;" >
										<option value=''>
										</option>
										<option value='1'>
											表2-1 离休人员
										</option>
										<option value='2'>
											表2-2 退休人员
										</option>
										<option value='3'>
											表2-3 内退人员
										</option>
										<option value='4'>
											表2-4 遗属
										</option>
									
								</select>
         
	   	  </td>
   	  </tr>
   	  
   	  
   	     
       
      
        
</table>

<table align='center' width='96%' ><tr><td align='center'>

<input type="button" name="button1"  value="确 定" class="mybutton" onclick='setCancel()'>  
<input type="button" name="button2"  value="关 闭" class="mybutton" onclick='window.close()'>  


</td></tr></table>



</html:form>

</body>
</html>